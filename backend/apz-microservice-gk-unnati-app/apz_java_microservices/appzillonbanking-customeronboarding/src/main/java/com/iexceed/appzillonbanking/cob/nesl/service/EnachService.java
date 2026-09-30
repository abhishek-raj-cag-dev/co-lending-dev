package com.iexceed.appzillonbanking.cob.nesl.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.repository.ab.*;
import com.iexceed.appzillonbanking.cob.core.utils.*;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedCDHLead;
import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiIexceedCDHLeadRepo;
import com.iexceed.appzillonbanking.cob.nesl.payload.*;
import com.iexceed.appzillonbanking.cob.service.COBService;
import com.iexceed.appzillonbanking.cob.service.CommonService;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.services.InterfaceAdapter;
import com.iexceed.appzillonbanking.cob.nesl.domain.ab.Enach;
import com.iexceed.appzillonbanking.cob.nesl.repository.ab.EnachRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import reactor.core.publisher.Mono;

@Service
public class EnachService {

	private static final Logger logger = LogManager.getLogger(EnachService.class);

	@Autowired
	private AdapterUtil adapterUtil;

	@Autowired
    UnnatiIexceedCDHLeadRepo UnnatiIexceedCDHLeadRepo;

    @Autowired
	private InterfaceAdapter interfaceAdapter;

	@Autowired
	private CustomerDetailsRepository custDtlRepo;

	@Autowired
	private EnachRepository enachRepo;
	
	@Autowired
	private LoanDtlsRepo loanDtlsRepo;
	
	@Autowired
	private ApplicationMasterRepository applicationMasterRepository;

    @Autowired
    private ApplicationDocumentsRepository applicationDocumentsRepository;

	@Autowired
	private BankDetailsRepository bankDtlRepo;
	
    @Autowired
    private ApiExecutionLogRepository logRepository;
	
    private final COBService cobService;

    public EnachService(COBService cobService){
        this.cobService = cobService;
    }

	@CircuitBreaker(name = "fallback", fallbackMethod = "enachrppFallback")
	public Mono<Object> enachrpp(EnachRequest enachRequest, Header header, Properties prop) {
		try {
			String custName = "";
			String custMobile = "";
			String custEmailId = "";
			String custId = "";
			String loanAcountNumber = "";
			String accountNo = enachRequest.getRequestObj().getAccountNumber();
			String additionalField2 = enachRequest.getRequestObj().getAdditionalField2() + "-N"; 


            Gson gson = new Gson();
			Optional<CustomerDetails> existingDetails = custDtlRepo.findByApplicationIdAndCustomerType(
					enachRequest.getRequestObj().getApplicationId(), enachRequest.getRequestObj().getApplicantType());

			List<ApplicationMaster> appMasterDb = applicationMasterRepository.findByAppIdAndApplicationId(enachRequest.getAppId(),
					enachRequest.getRequestObj().getApplicationId());
			custId = appMasterDb.get(0).getMemberId();
            String workItemNo = (appMasterDb.get(0) != null )?appMasterDb.get(0).getWorkitemNo():"";
            String productCode = (appMasterDb.get(0) != null )?appMasterDb.get(0).getProductCode():"";
            String SearchCode2 = (appMasterDb.get(0) != null )?appMasterDb.get(0).getSearchCode2():"";
//			String customerId = appMasterDb.get(0).getSearchCode2();
            if(AppStatus.DBPUSHBACK.getValue().equalsIgnoreCase(appMasterDb.get(0).getApplicationStatus())) {
                Response docDeleteResp = cobService.handleDeleteAllDocuments(prop, enachRequest.getAppId(),
                        enachRequest.getRequestObj().getApplicationId());
                logger.debug("Successfully deleted documents for application Id : {}", enachRequest.getRequestObj().getApplicationId());
                Optional<List<ApplicationDocuments>> appDocOpt = applicationDocumentsRepository.findByApplicationIdAndCustTypeAndDocType(enachRequest.getRequestObj().getApplicantType(),
                        enachRequest.getRequestObj().getApplicationId(), Constants.ENACH_DOC_TYPE
                );
                if (appDocOpt.isPresent() && !appDocOpt.get().isEmpty()) {
                    for (ApplicationDocuments appDoc : appDocOpt.get()) {
                        applicationDocumentsRepository.delete(appDoc);
                    }
                }
            }

			if (existingDetails.isPresent()) {
				logger.debug("data found for customerDetails " + "applicationId :"
						+ enachRequest.getRequestObj().getApplicationId() + "and "
						+ enachRequest.getRequestObj().getApplicantType());
				// Fetch the existing record
				CustomerDetails existingCustomerDetails = existingDetails.get();
				logger.debug("existingCustomerDetails : " + existingCustomerDetails.toString());

				custName = existingCustomerDetails.getCustomerName();
			//	custMobile = existingCustomerDetails.getMobileNumber() != null? existingCustomerDetails.getMobileNumber() : "";

                CustomerDetailsPayload payload = gson.fromJson(existingCustomerDetails.getPayloadColumn(),
						CustomerDetailsPayload.class);
				logger.debug("custApplicantPayload :" + payload);

				custEmailId = payload.getEmailId(); 
                if (StringUtils.isEmpty(custEmailId)) { // From Lead Table
                    // From CDH Table
                	logger.debug("Caling CDH");
                    String maitriProductCode = ProductCode.getCdhCodeByUnnatiCode(productCode);
                    if(ProductCode.OPEN_MARKET.getUnnatiCode().equalsIgnoreCase(productCode)) {
                        maitriProductCode = productCode;
                    }
                    Optional<UnnatiIexceedCDHLead> optUnnatiIexceedCDHLeadList = UnnatiIexceedCDHLeadRepo
                    		.findByCustomerIdAndProductAndReferenceId(SearchCode2, maitriProductCode,workItemNo);
                    UnnatiIexceedCDHLead unnatiIexceedCDHLead = !optUnnatiIexceedCDHLeadList.isPresent() ? null : optUnnatiIexceedCDHLeadList.get();
                    custEmailId = (unnatiIexceedCDHLead != null) ? unnatiIexceedCDHLead.getBranchEMailId() : "";
                }

                if(StringUtils.isEmpty(custEmailId) || !CommonUtils.validateEmailId(custEmailId)){
                    custEmailId = enachRequest.getRequestObj().getEmailId();
                }

				custId = payload.getCustId(); // memeberId
			} else {
				logger.warn("No matching customer record found for ApplicationId: "
						+ enachRequest.getRequestObj().getApplicationId() + ", CustomerType: "
						+ enachRequest.getRequestObj().getApplicantType());
			}
			
			LoanDetails loanDetail = loanDtlsRepo.findByApplicationIdAndAppIdAndVersionNum(enachRequest.getRequestObj().getApplicationId(), enachRequest.getAppId(),
					Constants.INITIAL_VERSION_NO);
            Optional<BankDetails> bankDetailsDb = bankDtlRepo.findBankDetailsByCustomerType(enachRequest.getRequestObj().getApplicantType(), enachRequest.getRequestObj().getApplicationId());
            if (bankDetailsDb.isPresent()) {
                BankDetails bankDetails = bankDetailsDb.get();
                BankDetailsPayload bankDetailsPayload = gson.fromJson(bankDetails.getPayloadColumn(), BankDetailsPayload.class);
                String nameAsPerBankAccount = bankDetailsPayload.getAccountName();
			String sactionAmt = "";
			if(loanDetail != null) {
				logger.warn("loanDetail record found: ");
				loanAcountNumber = loanDetail.getT24LoanId() == null ? "" : loanDetail.getT24LoanId();
				BigDecimal sactionAmtDb = loanDetail.getSanctionedLoanAmount();
				sactionAmt = (sactionAmtDb == null) ? "" : sactionAmtDb.toPlainString();
			}
			
			String rppVersion = prop.getProperty(CobFlagsProperties.RPP_VERSION.getKey());
			String merchantId = prop.getProperty(CobFlagsProperties.MERCHANT_ID.getKey());
			String merchantName = prop.getProperty(CobFlagsProperties.MERCHANT_NAME.getKey());
			String merchantEmail = prop.getProperty(CobFlagsProperties.MERCHANT_EMAIL.getKey());
			String merchantMobile = prop.getProperty(CobFlagsProperties.MERCHANT_MOBILE.getKey());
			String productIdentifier = prop.getProperty(CobFlagsProperties.PRODUCT_IDENTIFIER.getKey());
			String DistributorIdentifier = prop.getProperty(CobFlagsProperties.DISTRIBUTOR_IDENTIFIER.getKey());
			String frequency = prop.getProperty(CobFlagsProperties.FREQUENCY.getKey());
			String amountType = prop.getProperty(CobFlagsProperties.AMOUNT_TYPE.getKey());
			String totalAmount = prop.getProperty(CobFlagsProperties.TOTAL_AMOUNT.getKey());
			String noteFromMerchant = prop.getProperty(CobFlagsProperties.NOTE_FROM_MERCHANT.getKey());
			

			// create request
			StringBuilder rppRequest = new StringBuilder();

                rppRequest.append(rppVersion).append("|").append(merchantId).append("|").append(nameAsPerBankAccount).append("|") //Customer name
					//.append(custEmailId).append("|") // "emmadishetty.abhiram@cagrameen.in" //Customer email/mobile no
					.append(custEmailId).append("|") // Customer email/mobile no		
					.append(totalAmount).append("|") // Total amount (In Rs.)
					.append(noteFromMerchant).append("|") // Note from merchant //E-NACH
					.append(loanAcountNumber).append("|") // Invoice No
					.append(merchantName).append("|")//Merchant Name
					.append(DistributorIdentifier).append("|") // Distributor Identifier //WORLDLINE
					.append("").append("|") // Agent
                        .append(nameAsPerBankAccount).append("|") // Customer Identifier - Customer ID from the Application //customer Name
					.append(productIdentifier).append("|") // Product Identifier - //Unnati
					.append("").append("|") // Cart Details (Scheme)
					.append("").append("|") // ParticipantNotiContact1
					.append("").append("|") // Additional Field 1
					.append(additionalField2).append("|") // 9480-N //Additional Field 2
					.append("").append("|") // Additional Field 3
					.append("").append("|") // Additional Field 4
					.append("").append("|") // Additional Field 5
					.append("").append("|") // Image Name
					.append("").append("|") // Image
					.append(merchantEmail).append("|") // Merchant Email/Mobile
					.append(CommonUtils.getCurDatePlusone()).append("|") // Start Date
					.append(CommonUtils.getDatePlus30Years()).append("|") // End Date
					.append(sactionAmt).append("|") // MaxAmount // maximum EMI amount that should be debited from the customer's account.
					.append(frequency).append("|") // Frequency
					.append(amountType).append("|") // AmountType
					.append("").append("|") // BillDate
					.append(loanAcountNumber).append("|") // ConsumerCD //C12345
					.append(accountNo); // Account No


//			RPP Version |Merchant ID|Customer name|Customer email/mobile no |Total amount (In Rs .)|Note from
//			merchant|Invoice No |Merchant Name|Distributor Identifier|Agent|Customer Identifier|Product Identifier|Cart Details(Scheme)|Participant Contact1|Additional Field 1|Additional Field 2|Additional Field 3|Additional Field 4 |Additional Field 5|Image Name|Image|Merchant Email/Mobile|Start Date|End Date|MaxAmount|Frequency |AmountType|BillDate|ConsumerCD|Account No|Paynimo Request Id|URL to pay|ErrorCode|ErrorDesc

			String finalRppRequest = rppRequest.toString();
			logger.warn("finalRppRequest : " + finalRppRequest);
			String interfaceName = prop.getProperty(CobFlagsProperties.ENACH_RPPSERVICE_INTF.getKey()); // create

			Map<String, Object> obj = new HashMap<>();
			obj.put("RPPRequest", finalRppRequest);

			EnachRequestExt requestExt = new EnachRequestExt();
			requestExt.setAppId(enachRequest.getAppId());
			requestExt.setInterfaceName(interfaceName);
			requestExt.setRequestObj(obj);

			obj.put("header", header);
			String verifyEnachReq = gson.toJson(obj);
			logger.debug("verifyEnachReq: {} ", verifyEnachReq);

			logger.debug("verifyenchReqFinal from the API: {} ", finalRppRequest.toString());
			Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, requestExt, interfaceName);
			logger.debug("verify enach response 1 from the API: {} ", apiRespMono);

			return apiRespMono.flatMap(val -> {
				JSONObject resp = null;
				logger.debug("response 2 from the API: {} ", val);
				JSONObject apiResp = new JSONObject(new Gson().toJson(val));
				logger.debug("json Response 3 from the API: {} ", apiResp);

				if (!apiResp.isEmpty() && null != apiResp && apiResp.has("RPPServiceCallResult")) {
					String rppSrviceResp = apiResp.getString("RPPServiceCallResult");

					String[] respArray = rppSrviceResp.split("\\|");
					String errorDesc = respArray[respArray.length - 1];
					String errorCode = respArray[respArray.length - 2];
					String payReqUrl = respArray[respArray.length - 3];
					String enachReqId = respArray[respArray.length - 4];
					logger.error("ErrorDiscription. {}", errorDesc);
					if (StringUtils.isNotEmpty(errorDesc)
							&& errorDesc.equalsIgnoreCase(ResponseCodes.SUCCESS.getValue())) {
						logger.error("success response from RPPService. {}", apiResp);
						Optional<Enach> enachDetailsDb = enachRepo.findByApplicationIdAndCustomerType(
								enachRequest.getRequestObj().getApplicationId(),
								enachRequest.getRequestObj().getApplicantType());

						Enach enachDetails = null;
						if (enachDetailsDb.isPresent()) {
							logger.debug("enachDetailsDb record found : updating : ");
							enachDetails = enachDetailsDb.get();
							enachDetails.setRetryAttempts(enachDetails.getRetryAttempts().add(BigDecimal.ONE));
						} else {
							logger.debug("enachDetailsDb record found : inserting : ");
							enachDetails = new Enach();
							enachDetails.setApplicationId(enachRequest.getRequestObj().getApplicationId());
							enachDetails.setAppId(enachRequest.getAppId());
							enachDetails.setCustomerType(enachRequest.getRequestObj().getApplicantType());
							enachDetails.setRetryAttempts(BigDecimal.ZERO);
						}

						enachDetails.setRequestString(rppRequest.toString());
						//enachDetailsDbObj.setRequestString(verifyEnachReq);
						enachDetails.setEnachReqId(enachReqId);
						enachDetails.setUrlString(payReqUrl);
						enachDetails.setReqErrorCode(errorCode);
						enachDetails.setReqErrorDesc(errorDesc);
						enachDetails.setCreatedBy(enachRequest.getUserId());
						enachDetails.setResCode(null);
						enachDetails.setResDesc(null);
						enachDetails.setPgTranId(null);
						enachDetails.setUpdatedTs(LocalDateTime.now());
						enachDetails.setEnachType(enachRequest.getRequestObj().getEnachType());
						
						enachDetails = enachRepo.save(enachDetails);
						logger.debug("Data updated :" + enachDetails.toString());

							bankDetailsPayload.setENachStatus("");
							bankDetails.setPayloadColumn(gson.toJson(bankDetailsPayload));
							logger.warn("customerDetail.toString() to be updated: " + bankDetails.toString());
							bankDtlRepo.save(bankDetails);
						String jsonString = new Gson().toJson(enachDetails);
						resp = adapterUtil.setSuccessResp(jsonString);
					} else {
						saveLog(enachRequest.getRequestObj().getApplicationId(), Constants.ENACH_VERIFY, finalRppRequest.toString(),
								apiResp.toString(), ResponseCodes.FAILURE.getValue(), errorDesc, "");
						logger.error("Error response from RPPService. {}", apiResp);
						resp = adapterUtil.setError(errorDesc, "3");
					}

				} else {
					saveLog(enachRequest.getRequestObj().getApplicationId(), Constants.ENACH_VERIFY, finalRppRequest.toString(),
							apiResp.toString(), ResponseCodes.FAILURE.getValue(), apiResp.toString(), null);
					logger.error("error response from verify Enach RPPService API. {}", apiResp);
					resp = adapterUtil.setError("Error response from RPPService  API.", "2");
				}

				return Mono.just(resp);
			});
            } else {
                logger.error("Bank details not found for ApplicationId: "
                        + enachRequest.getRequestObj().getApplicationId() + ", CustomerType: "
                        + enachRequest.getRequestObj().getApplicantType());
                saveLog(enachRequest.getRequestObj().getApplicationId(), Constants.ENACH_VERIFY, "",
                        "Bank details not found", ResponseCodes.FAILURE.getValue(), "Bank details not found", null);
                return Mono.just(adapterUtil.setError("Bank details not found.", "4"));
            }
		} catch (Exception e) {
			saveLog(enachRequest.getRequestObj().getApplicationId(), Constants.ENACH_VERIFY, enachRequest.toString(),
					e.getMessage(), ResponseCodes.FAILURE.getValue(), e.getMessage(), "");
			logger.error("error in RPPService API. {}", e);
			return Mono.just(adapterUtil.setError("Error occurred while executing the RPPService api.", "1"));
		}

	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "transactionStatusFallback")
	public Mono<Object> transactionStatus(EnachRequest enachRequest, Header header, Properties prop) {
		try {
			logger.debug("request from the transactionStatus API: {} ", enachRequest.toString());

			String rppVersion = prop.getProperty(CobFlagsProperties.RPP_VERSION.getKey());
			String merchantId = prop.getProperty(CobFlagsProperties.MERCHANT_ID.getKey());
			String merchantName = prop.getProperty(CobFlagsProperties.MERCHANT_NAME.getKey());

			// String paynimoRequestId = "P27032-124656299.AJ1468";
			String paynimoRequestId = enachRequest.getRequestObj().getPaynimoRequestId();

			StringBuilder trnStatusReqExt = new StringBuilder();

			trnStatusReqExt.append(rppVersion).append("|").append(merchantId).append("|").append(merchantName)
					.append("|").append(paynimoRequestId); // Request Id from "RPP with ENACH" API

			String finaltrnStatusReq = trnStatusReqExt.toString();
			String interfaceName = prop.getProperty(CobFlagsProperties.ENACH_TRANSACTION_STATUS_INTF.getKey());
			logger.debug("finaltrnStatusReq from the API: {} ", finaltrnStatusReq);

			Gson gson = new Gson();
			Map<String, Object> ob = new HashMap<>();
			ob.put("RPPRequest", finaltrnStatusReq);

			EnachRequestExt requestExt = new EnachRequestExt();
			requestExt.setAppId(enachRequest.getAppId());
			requestExt.setInterfaceName(interfaceName);
			requestExt.setRequestObj(ob);

			ob.put("header", header);
			String stsEnachReq = gson.toJson(ob);

			Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, requestExt, interfaceName);
			logger.debug("trnStatus response 1 from the API: {} ", apiRespMono);
			// persist request and response

			return apiRespMono.flatMap(val -> {
				JSONObject resp = null;
				logger.debug("trnStatus response 2 from the API: {} ", val);
				JSONObject apiResp = new JSONObject(new Gson().toJson(val));

				if (!apiResp.isEmpty() && null != apiResp && apiResp.has("PullTransactionStatusResult")) {
					logger.debug("Inside PullTransactionStatusResult : ");
					String rppSrviceResp = apiResp.getString("PullTransactionStatusResult");
					String[] respArray = rppSrviceResp.split("\\|");

					String pgTranId = respArray[respArray.length - 1];
					String statusDesc = respArray[respArray.length - 2];
					String status = respArray[respArray.length - 3];

					Optional<Enach> enachDetailsDb = enachRepo.findByApplicationIdAndCustomerTypeAndEnachReqId(
							enachRequest.getRequestObj().getApplicationId(),
							enachRequest.getRequestObj().getApplicantType(), paynimoRequestId);

					Enach enachDetails = null;
					if (enachDetailsDb.isPresent()) {
						logger.debug("enachDetailsDb record found for : ");
						enachDetails = enachDetailsDb.get();
						logger.debug("enachDetailsDb record found for 1 : " + enachDetails.toString());
						enachDetails.setResCode(status);
						enachDetails.setResDesc(statusDesc);
						enachDetails.setPgTranId(pgTranId);
						enachDetails.setUpdatedTs(LocalDateTime.now());
						logger.debug("enachDetailsDb record found for 2 : " + enachDetails.toString());
						enachDetails = enachRepo.save(enachDetails);
						logger.debug("Data updated" + enachDetails.toString());

						String jsonString = new Gson().toJson(enachDetails);
						resp = adapterUtil.setSuccessResp(jsonString);

						// resp = adapterUtil.setSuccessResp(enachDetails.toString());
					} else {
						resp = adapterUtil.setError("Application not found .", "2");
						logger.error("error response from  transactionStatus API. {}", apiResp);
						saveLog(enachRequest.getRequestObj().getApplicationId(), Constants.ENACH_STS, finaltrnStatusReq.toString().toString(),
								apiResp.toString() + " " + resp.toString(), ResponseCodes.FAILURE.getValue(), apiResp.toString(), null);
					}

				} else {
					resp = adapterUtil.setError("error response from transactionStatus API.", "2");
					logger.error("error response from  transactionStatus API. {}", apiResp);
					saveLog(enachRequest.getRequestObj().getApplicationId(), Constants.ENACH_STS, finaltrnStatusReq.toString(),
							apiResp.toString() + " " + resp.toString(), ResponseCodes.FAILURE.getValue(), apiResp.toString(), null);
				}

				return Mono.just(resp);
			});
			// persist request and response

		} catch (Exception e) {
			logger.error("error in Enach pull transactionStatus API. {}", e);
			saveLog(enachRequest.getRequestObj().getApplicationId(), Constants.ENACH_STS, enachRequest.toString(),
					e.getMessage(), ResponseCodes.FAILURE.getValue(), e.getMessage(), "");
			return Mono
					.just(adapterUtil.setError("Error occurred while executing the pull transaction Status api.", "1"));
		}
	}

	private Mono<Object> enachrppFallback(EnachRequest request, Header header, Properties prop, Exception e) {
		logger.error("enachrppFallback error : ", e);
		return FallbackUtils.genericFallbackMonoObject();
	}

	private Mono<Object> transactionStatusFallback(EnachRequest request, Header header, Properties prop, Exception e) {
		logger.error("transactionStatusFallback error : ", e);
		return FallbackUtils.genericFallbackMonoObject();
	}

	private void saveLog(String applicationId, String stepName, String request, String response, String status,
			String errorMsg, String currentStage) {
		ApiExecutionLog log = new ApiExecutionLog();
		log.setApplicationId(applicationId);
		log.setApiName(stepName);
		log.setRequestPayload(request);
		log.setResponsePayload(response);
		log.setApiStatus(status);
		log.setErrorMessage(errorMsg);
		log.setCreateTs(LocalDateTime.now());
		log.setCurrentStage(currentStage);
		logRepository.save(log);
	}

}
