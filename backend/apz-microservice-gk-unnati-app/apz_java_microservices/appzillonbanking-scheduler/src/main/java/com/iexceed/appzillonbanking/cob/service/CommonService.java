package com.iexceed.appzillonbanking.cob.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.cards.service.CreditCardService;
import com.iexceed.appzillonbanking.cob.constants.CommonConstants;
import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.core.repository.ab.*;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.core.services.InterfaceAdapter;
import com.iexceed.appzillonbanking.cob.core.utils.*;
import com.iexceed.appzillonbanking.cob.domain.ab.WhitelistedBranches;
import com.iexceed.appzillonbanking.cob.entity.ngoAddFolder.Folder;
import com.iexceed.appzillonbanking.cob.loans.payload.*;
import com.iexceed.appzillonbanking.cob.loans.service.LoanService;
import com.iexceed.appzillonbanking.cob.loans.service.LucService;
import com.iexceed.appzillonbanking.cob.nesl.repository.ab.EnachRepository;
import com.iexceed.appzillonbanking.cob.payload.*;
import com.iexceed.appzillonbanking.cob.repository.ab.WhitelistedBranchesRepository;
import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiIexceedCDHLeadRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@DependsOn("commandLineRunnerImpl")
public class CommonService {

	private static final Logger logger = LogManager.getLogger(CommonService.class);

	@Autowired
	private COBService cobService;

	@Autowired
	private CreditCardService creditCardService;

	@Autowired
	private LoanService loanService;

	@Autowired
	private CibilDetailsRepository cibilDetailsRepository;

	@Autowired
	private ApplicationMasterRepository applicationMasterRepository;

	@Autowired
	private CustomerDetailsRepository customerDetailsRepository;

	@Autowired
	private CommonParamService commonCoreService;

	@Autowired
	private InterfaceAdapter interfaceAdapter;

	@Autowired
	private PinCodeDetailsRepository pinCodeDetailsRepository;

	@Autowired
	private RpcStageVerificationRepository rpcStageVerificationRepo;

	@Autowired
	private BCMPIStageVerificationRepository bcmpiStageVerificationRepo;

	@Autowired
	private CADeviationMasterRepository caDeviationMasterRepo;

	@Autowired
	private ReassessmentMasterRepository reassessmentMasterRepo;

	@Autowired
	private OccupationDetailsRepository occupationDetailsRepo;

	@Autowired
	private AddressDetailsRepository addressDetailsRepo;

	@Autowired
	private LoanDtlsRepo loanDtlsRepo;

	@Autowired
	private CibilDetailsRepository CibilDetailsRepo;

	@Autowired
	private DeviationRATrackerRepository deviationRATrackerRepo;

	@Autowired
	private WorkflowDefinitionRepository workflowDefinitionRepository;

	@Autowired
	private ApiExecutionLogRepository logRepository;

	@Autowired
	private AdapterUtil adapterUtil;

	@Autowired
	private BCMPIIncomeDetailsRepository bcmpiIncomeDetailsRepo;

	@Autowired
	private DBKITStageVerificationRepository dbkitStageVerificationRepository;

	@Autowired
	private ApplicationWorkflowRepository applicationWorkflowRepository;

	@Autowired
	private WhitelistedBranchesRepository whitelistedBranchesRepo;

	@Autowired
	private EnachRepository enachRepository;

	@Autowired
	private UdhyamRepository udhyamRepository;

	@Autowired
	private ApplicationDocumentsRepository applicationDocumentsRepository;

	@Autowired
	private SendSmsAndEmailService sendSmsAndEmailService;

	@Autowired
	private BankDetailsRepository bankDetailsRepository;

	@Autowired
	private SourcingResponseTrackerRepository sourcingResponseTrackerRepository;

	@Autowired
	private WebClient webClient;

	@Autowired
	private DocumentsRepository documentsRepository;

	@Autowired
	private WhitelistedBranchesRepository whitelistedBranchesRepository;

	@Autowired
	private RenewalLeadDetailsRepository renewalLeadDetailsRepository;

	private final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
	private ScheduledFuture<?> scheduledTask;
	private String currentCronExpression = null;
	@Autowired
	private LucService lucService;

	@Autowired
	private LucRepository lucRepository;

	@Autowired
	private UnnatiIexceedCDHLeadRepo unnatiIexceedCDHLeadRepo;
	@Autowired
	private ApplicationMasterRepository2 applicationMasterRepository2;

	@CircuitBreaker(name = "fallback", fallbackMethod = "approveRejectApplicationFallback")
	public Mono<Response> approveRejectApplication(
			com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
			boolean isSelfOnBoardingHeaderAppId, Properties prop) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		response.setResponseHeader(responseHeader);
		com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest
				.getRequestObj();
		String status = customerDataFields.getStatus();
		if ((!(CommonUtils.isNullOrEmpty(status)))
				&& (AppStatus.APPROVED.getValue().equalsIgnoreCase(status)
						|| AppStatus.REJECTED.getValue().equalsIgnoreCase(status))
				|| AppStatus.PUSHBACK.getValue().equalsIgnoreCase(status)
				|| AppStatus.RPCVERIFIED.getValue().equalsIgnoreCase(status)
				|| AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status)) {
			List<String> applnStatus = new ArrayList<>();
			applnStatus.add(AppStatus.PENDING.getValue());
			applnStatus.add(AppStatus.INPROGRESS.getValue());
			applnStatus.add(AppStatus.PUSHBACK.getValue());
			applnStatus.add(AppStatus.CAPUSHBACK.getValue());
			applnStatus.add(AppStatus.IPUSHBACK.getValue());
			logger.debug("app id : {} ", customerDataFields.getAppId());
			logger.debug("application id : {} ", customerDataFields.getApplicationId());
			logger.debug("version no : {} ", customerDataFields.getVersionNum());
			logger.debug("application status : {} ", applnStatus);
			Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
					.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
							customerDataFields.getApplicationId(), customerDataFields.getVersionNum(), applnStatus);
			logger.debug("Getting optional master object");
			if (masterObjDb.isPresent()) {
				ApplicationMaster masterObj = masterObjDb.get();
				logger.debug("Master data value present.");
				String accNum = null;
				BigDecimal customerId = null;
				Gson gson = new Gson();
				CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
				if (AppStatus.APPROVED.getValue().equalsIgnoreCase(status)) {
					accNum = CommonUtils.generateRandomNumStr();
					customerId = CommonUtils.generateRandomNum();
					masterObj.setAccNumber(accNum);
					masterObj.setCustomerId(customerId);
					customerIdentification.setAccNumber(accNum);
					customerIdentification.setCustomerId(customerId.toString());

					if (!(Constants.ETB.equalsIgnoreCase(masterObj.getApplicationType())
							&& Products.DEPOSIT.getKey().equalsIgnoreCase(masterObj.getProductGroupCode()))) {
						logger.debug("Updating customer ID in TB_ABOB_CUSTOMER_DETAILS");
						Optional<CustomerDetails> custDtl = customerDetailsRepository
								.findById(customerDataFields.getCustDtlId());
						if (custDtl.isPresent()) {
							CustomerDetails custDtlObj = custDtl.get();
							custDtlObj.setCustomerId(customerId);
							customerDetailsRepository.save(custDtlObj);
						}
					}
				}
				masterObj.setRemarks(null);
				masterObj.setAssignedTo(null);
				cobService.updateStatus(masterObj, status);
				PopulateapplnWFRequest req = new PopulateapplnWFRequest();
				PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
				reqFields.setAppId(masterObj.getAppId());
				reqFields.setApplicationId(masterObj.getApplicationId());
				reqFields.setCreatedBy(customerDataFields.getUserId());
				reqFields.setVersionNum(masterObj.getVersionNum());
				reqFields.setApplicationStatus(masterObj.getApplicationStatus());
				WorkFlowDetails wf = customerDataFields.getWorkFlow();
				logger.debug("customerDataFields.getWorkFlow() :" + customerDataFields.getWorkFlow().toString());
				wf.setRemarks(customerDataFields.getRemarks());
				reqFields.setWorkflow(wf);
				req.setRequestObj(reqFields);
				logger.debug("req :" + req.toString());
				commonCoreService.populateApplnWorkFlow(req);
				Optional<SourcingResponseTracker> sourcingResponseTrackerOptional = sourcingResponseTrackerRepository
						.findById(masterObj.getApplicationId());
				sourcingResponseTrackerOptional.ifPresent(
						sourcingResponseTracker -> sourcingResponseTrackerRepository.delete(sourcingResponseTracker));
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				customerIdentification.setVersionNum(customerDataFields.getVersionNum());
				responseBody.setResponseObj(gson.toJson(customerIdentification));
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
				logger.debug("application status update completed");
				boolean isIexceedFlag = "I".equalsIgnoreCase(masterObj.getDeclarationFlag());
				if (AppStatus.APPROVED.getValue().equalsIgnoreCase(status) && !isIexceedFlag) {
					Optional<WhitelistedBranches> optionalBranch = whitelistedBranchesRepo
							.findByBranchCode(masterObj.getBranchId());
					if (optionalBranch.isPresent()) {
						WhitelistedBranches branch = optionalBranch.get();
						String productCode = masterObj.getProductCode();
						boolean enableFlag = false;
						switch (productCode) {
							case Constants.UNNATI_PRODUCT_CODE:
								enableFlag = "Y".equalsIgnoreCase(branch.getUnnatiEnabled());
								break;
							case Constants.RENEWAL_LOAN_PRODUCT_CODE:
								enableFlag = "Y".equalsIgnoreCase(branch.getRenewalEnabled());
								break;
							case Constants.OPENMARKET_LOAN_PRODUCT_CODE:
								enableFlag = true;
								break;
							default:
								logger.debug("Invalid product code : {}, hence skipping", productCode);
								break;
						}
						if (enableFlag) {
							masterObj.setDeclarationFlag("I");
							applicationMasterRepository.save(masterObj);
						}
					}
				}
			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
			}
		} else {
			responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
		}
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return Mono.just(response);
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "stageMovementApplicationFallback")
	public Mono<Response> stageMovementApplication(FetchDeleteUserRequest fetchDeleteUserRequest,
			Properties prop, String roleId) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		response.setResponseHeader(responseHeader);
		FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest.getRequestObj();
		String status = customerDataFields.getStatus();
		if ((CobFlagsProperties.RPC.getKey().equalsIgnoreCase(roleId)) && (!(CommonUtils.isNullOrEmpty(status)))
				&& (AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status)
						|| AppStatus.REJECTED.getValue().equalsIgnoreCase(status)
						|| AppStatus.PENDINGFORRPCVERIFICATION.getValue().equalsIgnoreCase(status)
						|| AppStatus.RPCVERIFIED.getValue().equalsIgnoreCase(status)
						|| AppStatus.RPCPUSHBACK.getValue().equalsIgnoreCase(status))) {
			List<String> applnStatus = new ArrayList<>();
			applnStatus.add(AppStatus.APPROVED.getValue());
			applnStatus.add(AppStatus.PENDINGFORRPCVERIFICATION.getValue());
			applnStatus.add(AppStatus.RPCPUSHBACK.getValue());
			logger.debug("app id : {} ", customerDataFields.getAppId());
			logger.debug("application id : {} ", customerDataFields.getApplicationId());
			logger.debug("version no : {} ", customerDataFields.getVersionNum());
			logger.debug("application status : {} ", applnStatus);
			Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
					.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
							customerDataFields.getApplicationId(), customerDataFields.getVersionNum(), applnStatus);
			logger.debug("Getting optional master object");
			if (masterObjDb.isPresent()) {
				Optional<RpcStageVerification> verificationData = rpcStageVerificationRepo
						.findById(customerDataFields.getApplicationId());
				logger.debug("Master data value present.");
				Gson gson = new Gson();
				CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
				ApplicationMaster masterObj = masterObjDb.get();

				masterObj.setRemarks(null);
				masterObj.setUpdatedBy(customerDataFields.getUserId());
				masterObj.setAssignedTo(null);
				cobService.updateStatus(masterObj, status);

				logger.debug("customerDataFields :" + customerDataFields.toString());
				PopulateapplnWFRequest req = new PopulateapplnWFRequest();
				PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
				reqFields.setAppId(masterObj.getAppId());
				reqFields.setApplicationId(masterObj.getApplicationId());
				reqFields.setCreatedBy(customerDataFields.getUserId());
				reqFields.setVersionNum(masterObj.getVersionNum());
				reqFields.setApplicationStatus(masterObj.getApplicationStatus());
				WorkFlowDetails wf = customerDataFields.getWorkFlow();
				logger.debug("customerDataFields.getWorkFlow() :" + customerDataFields.getWorkFlow().toString());
				wf.setRemarks(customerDataFields.getRemarks());
				if (verificationData.isPresent()) {
					RpcStageVerification data = verificationData.get();
					if (AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status)
							|| AppStatus.RPCPUSHBACK.getValue().equalsIgnoreCase(status)) {
						wf.setRemarks(data.getQueries());
					}
					data.setQueries(null);
					data.setVerifiedStages(null);
					rpcStageVerificationRepo.save(data);
				}
				reqFields.setWorkflow(wf);
				req.setRequestObj(reqFields);
				logger.debug("req :" + req.toString());
				commonCoreService.populateApplnWorkFlow(req);
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				customerIdentification.setVersionNum(customerDataFields.getVersionNum());
				responseBody.setResponseObj(gson.toJson(customerIdentification));
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
				logger.debug("application status update completed");
			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
			}
		} else {
			responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
		}
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return Mono.just(response);
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "creditAssessmentApplicationMovementFallback")
	public Mono<Response> creditAssessmentApplicationMovement(FetchDeleteUserRequest fetchDeleteUserRequest,
			Properties prop, String roleId) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		response.setResponseHeader(responseHeader);
		FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest.getRequestObj();
		String status = customerDataFields.getStatus();
		CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
		String nextStage = AppStatus.PENDINGPRESANCTION.getValue();
		if (!AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status) &&
				!AppStatus.REJECTED.getValue().equalsIgnoreCase(status)
				&& !AppStatus.CAPUSHBACK.getValue().equalsIgnoreCase(status)) {
			boolean isValidSanctionedAmount = loanDtlsRepo
					.eliglibleAmtMoreThanBmRecommendedAmt(fetchDeleteUserRequest.getRequestObj().getApplicationId());
			if (!isValidSanctionedAmount) {
				responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
				response.setResponseHeader(responseHeader);
				responseBody.setResponseObj("BM Recommended amount cannot be greater than eligible amount.");
				response.setResponseBody(responseBody);
				return Mono.just(response);
			}
		}
		if ((AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status)
				|| AppStatus.REJECTED.getValue().equalsIgnoreCase(status)
				|| AppStatus.CAPUSHBACK.getValue().equalsIgnoreCase(status)
				|| AppStatus.CACOMPLETED.getValue().equalsIgnoreCase(status)
				|| AppStatus.PENDINGDEVIATION.getValue().equalsIgnoreCase(status)
				|| AppStatus.PENDINGREASSESSMENT.getValue().equalsIgnoreCase(status)
				|| AppStatus.PENDINGPRESANCTION.getValue().equalsIgnoreCase(status))) {
			List<String> applnStatus = new ArrayList<>();
			applnStatus.add(AppStatus.RPCVERIFIED.getValue());
			logger.debug("app id : {} ", customerDataFields.getAppId());
			logger.debug("application id : {} ", customerDataFields.getApplicationId());
			logger.debug("version no : {} ", customerDataFields.getVersionNum());
			logger.debug("application status : {} ", applnStatus);
			Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
					.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
							customerDataFields.getApplicationId(), customerDataFields.getVersionNum(), applnStatus);
			logger.debug("Getting optional master object");
			String userId = customerDataFields.getUserId();
			if (masterObjDb.isPresent()) {
				ApplicationMaster masterObj = masterObjDb.get();
				if (AppStatus.PENDINGPRESANCTION.getValue().equalsIgnoreCase(status)) {
					LoanDetails loanDetails = loanDtlsRepo.findByApplicationId(customerDataFields.getApplicationId());
					Optional<CibilDetails> cibilOpt = cibilDetailsRepository
							.findCibilDetailsByCustomerTypeAndApplicationId(
									Constants.COAPPLICANT,
									customerDataFields.getApplicationId());
					if (!cibilOpt.isPresent()) {
						return Mono.just(getFailureApiJson("Cibil details not present",
								"Credit Assessment Application Movement"));
					}
					CibilDetailsPayload payload = new Gson().fromJson(cibilOpt.get().getPayloadColumn(),
							CibilDetailsPayload.class);
					String eligibleAmtStr = payload.getEligibleAmt();
					if (eligibleAmtStr == null || StringUtils.isEmpty(eligibleAmtStr)) {
						return Mono.just(getFailureApiJson("BRE eligible amount missing",
								"Credit Assessment Application Movement"));
					}
					BigDecimal eligibleAmount;
					try {
						eligibleAmount = new BigDecimal(eligibleAmtStr);
					} catch (NumberFormatException e) {
						return Mono.just(getFailureApiJson("Invalid BRE eligible amount",
								"Credit Assessment Application Movement"));
					}
					loanDetails.setBmRecommendedLoanAmount(eligibleAmount);
					loanDtlsRepo.save(loanDetails);

					List<String> insertionFlags = ValidateDeviation(masterObj, userId, prop);
					String action = "";
					if (insertionFlags.contains(Constants.ERROR)) {
						responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
						response.setResponseBody(responseBody);
						response.setResponseHeader(responseHeader);
						return Mono.just(response);
					} else if (insertionFlags.contains(Constants.CA_DEVIATION)) {
						action = Constants.SUBMITDEVIATION;
						WorkFlowDetails workFlowDetails = changeWorkFlowDetails(Constants.CREDITASSESSMENT,
								Constants.CREDITASSESSMENT, action);
						customerDataFields.setWorkFlow(workFlowDetails);
						status = workFlowDetails.getNextWorkflowStatus();
						nextStage = Constants.CA_DEVIATION;
					} else if (insertionFlags.contains(Constants.REASSESSMENT)) {
						action = Constants.SUBMITREASSESSMENT;
						WorkFlowDetails workFlowDetails = changeWorkFlowDetails(Constants.CREDITASSESSMENT,
								Constants.CREDITASSESSMENT, action);
						customerDataFields.setWorkFlow(workFlowDetails);
						status = workFlowDetails.getNextWorkflowStatus();
						nextStage = Constants.REASSESSMENT;
					}
				}
				customerIdentification.setNextStage(nextStage);
				logger.debug("Master data value present.");
				Gson gson = new Gson();

				masterObj.setRemarks(null);
				masterObj.setUpdatedBy(customerDataFields.getUserId());
				masterObj.setAssignedTo(null);
				cobService.updateStatus(masterObj, status);

				logger.debug("customerDataFields :" + customerDataFields.toString());
				PopulateapplnWFRequest req = new PopulateapplnWFRequest();
				PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
				reqFields.setAppId(masterObj.getAppId());
				reqFields.setApplicationId(masterObj.getApplicationId());
				reqFields.setCreatedBy(customerDataFields.getUserId());
				reqFields.setVersionNum(masterObj.getVersionNum());
				reqFields.setApplicationStatus(masterObj.getApplicationStatus());
				WorkFlowDetails wf = customerDataFields.getWorkFlow();
				logger.debug("customerDataFields.getWorkFlow() :" + customerDataFields.getWorkFlow().toString());
				if ((AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status)
						|| AppStatus.CAPUSHBACK.getValue().equalsIgnoreCase(status))
						&& StringUtils.isEmpty(customerDataFields.getRemarks())) {
					Optional<BCMPIStageVerification> verificationData = bcmpiStageVerificationRepo
							.findById(customerDataFields.getApplicationId());
					if (verificationData.isPresent()) {
						BCMPIStageVerification data = verificationData.get();
						wf.setRemarks(data.getQueries());
						data.setQueries(null);
						data.setVerifiedStages(null);
						bcmpiStageVerificationRepo.save(data);
					}
				}
				if (StringUtils.isNotEmpty(customerDataFields.getRemarks())) {
					wf.setRemarks(customerDataFields.getRemarks());
				} else {
					wf.setRemarks(null);
				}
				try {
					bcmpiStageVerificationRepo.deleteById(customerDataFields.getApplicationId());
				} catch (Exception e) {
					logger.error(
							"Error while deleting the record from BCMPIStageVerification table for application Id: {}, with error: {}",
							customerDataFields.getApplicationId(), e);
				}
				logger.debug("deleted the record from BCMPIStageVerification table for application Id: {}",
						customerDataFields.getApplicationId());
				reqFields.setWorkflow(wf);
				req.setRequestObj(reqFields);
				logger.debug("req :" + req.toString());
				commonCoreService.populateApplnWorkFlow(req);
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				customerIdentification.setVersionNum(customerDataFields.getVersionNum());
				responseBody.setResponseObj(gson.toJson(customerIdentification));
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
				logger.debug("application status update completed");
			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
			}
		} else {
			responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
		}
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return Mono.just(response);
	}

	private WorkFlowDetails changeWorkFlowDetails(String workFlowId, String fromStageId, String action) {
		WorkflowDefinition workFlowDef = null;
		workFlowDef = workflowDefinitionRepository.findByWorkFlowIdAndFromStageIdAndAction(workFlowId, fromStageId,
				action);
		WorkFlowDetails workFlowDetails = new WorkFlowDetails();
		workFlowDetails.setWorkflowId(workFlowDef.getWorkFlowId());
		workFlowDetails.setCurrentStage(workFlowDef.getFromStageId());
		workFlowDetails.setAction(workFlowDef.getAction());
		workFlowDetails.setSeqNo(workFlowDef.getStageSeqNum());
		workFlowDetails.setNextStageId(workFlowDef.getNextStageId());
		workFlowDetails.setCurrentRole(workFlowDef.getCurrentRole());
		workFlowDetails.setNextWorkflowStatus(workFlowDef.getNextWorkflowStatus());
		return workFlowDetails;
	}

	private List<String> ValidateDeviation(ApplicationMaster masterObj, String userId, Properties prop) {
		List<String> insertionFlags = new ArrayList<>();
		Gson gson = new Gson();
		try {
			List<CADeviationMaster> deviationMaster = caDeviationMasterRepo
					.findByProductAndActive(masterObj.getProductCode(), Constants.YES);
			logger.debug("deviationMaster " + deviationMaster.toString());
			List<CADeviationMaster> recordedDeviations = new ArrayList<>();
			String state = "";
			if (masterObj.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)) {
				state = unnatiIexceedCDHLeadRepo.findStateByCustomerId(masterObj.getSearchCode2());
			} else {
				state = reassessmentMasterRepo.findStateByWorkitemNo(masterObj.getWorkitemNo(),
						Constants.UNNATI_PRODUCT_CODE);
			}
			List<ReassessmentMaster> reassessmentMaster = reassessmentMasterRepo.findByProductAndActiveStatusAndState(
					masterObj.getProductCode(), Constants.YES, state.toUpperCase());
			logger.debug("reassessmentMaster " + reassessmentMaster.toString());
			List<ReassessmentMaster> recordedReassessments = new ArrayList<>();
			List<LoanDetails> loanDtls = null;
			List<CustomerDetails> custDetails;
			List<OccupationDetails> occupationDetails;
			List<AddressDetails> addressDetails;
			List<CibilDetails> cbDtls;
			BigDecimal applicantCustDtlId = null;
			BigDecimal coApplicantCustDtlId = null;
			AddressDetailsPayload appOccupAddr = null;
			AddressDetailsPayload appPersonalAddr = null;
			AddressDetailsPayload coappOccupAddr = null;
			AddressDetailsPayload coappPersonalAddr = null;
			BCMPIIncomeDetailsWrapper incomeDetails;

			int updatedRows = deviationRATrackerRepo.deleteByApplicationId(masterObj.getApplicationId());
			logger.debug("updatedRows " + updatedRows);
			List<ApplicationDocuments> appDocs = applicationDocumentsRepository.findByApplicationIdAndAppId(
					masterObj.getApplicationId(), masterObj.getAppId());
			if (appDocs != null && !appDocs.isEmpty()) {
				logger.debug("Found {} application documents for applicationId={}, appId={}", appDocs.size(),
						masterObj.getApplicationId(), masterObj.getAppId());
				Path basePath = Paths.get(prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()),
						masterObj.getAppId(), Constants.LOAN, masterObj.getApplicationId());
				for (ApplicationDocuments appDoc : appDocs) {
					try {
						ApplicationDocumentsPayload appDocPayload = new Gson().fromJson(appDoc.getPayloadColumn(),
								ApplicationDocumentsPayload.class);
						if (Constants.RE_ASSESSMENT_DOC.equalsIgnoreCase(appDocPayload.getDocumentType())) {
							Path fileDest = basePath.resolve(appDocPayload.getDocumentFileName());

							if (Files.deleteIfExists(fileDest)) {
								logger.info("Deleted file: {}", fileDest);
							} else {
								logger.warn("File not found for deletion: {}", fileDest);
							}

							applicationDocumentsRepository.delete(appDoc);
							logger.info("Deleted ApplicationDocuments record with id: {}", appDoc.getAppDocId());

						}
					} catch (Exception e) {
						logger.error(
								"Exception while processing ApplicationDocuments payload for id: {}. Exception: {}",
								appDoc.getAppDocId(), e.getMessage(), e);
					}
				}
			}
			custDetails = customerDetailsRepository.findByApplicationIdAndAppId(masterObj.getApplicationId(),
					masterObj.getAppId());
			CustomerDetailsPayload applicantCustDetailPayload = null;
			CustomerDetailsPayload coApplicantCustDetailPayload = null;

			for (CustomerDetails custDetail : custDetails) {
				if (custDetail.getCustomerType().equals(Constants.APPLICANT)) {
					applicantCustDtlId = custDetail.getCustDtlId();
					applicantCustDetailPayload = gson.fromJson(custDetail.getPayloadColumn(),
							CustomerDetailsPayload.class);
				} else {
					coApplicantCustDtlId = custDetail.getCustDtlId();
					coApplicantCustDetailPayload = gson.fromJson(custDetail.getPayloadColumn(),
							CustomerDetailsPayload.class);
				}
			}
			occupationDetails = occupationDetailsRepo.findByApplicationIdAndAppId(masterObj.getApplicationId(),
					masterObj.getAppId());
			addressDetails = addressDetailsRepo.findByApplicationIdAndAppId(masterObj.getApplicationId(),
					masterObj.getAppId());
			for (AddressDetails addressDetail : addressDetails) {
				if (applicantCustDtlId.compareTo(addressDetail.getCustDtlId()) == 0
						&& Constants.OCCUPATION.equalsIgnoreCase(addressDetail.getAddressType())) {
					appOccupAddr = gson.fromJson(addressDetail.getPayloadColumn(),
							AddressDetailsPayload.class);
				} else if (applicantCustDtlId.compareTo(addressDetail.getCustDtlId()) == 0
						&& !Constants.OCCUPATION.equalsIgnoreCase(addressDetail.getAddressType())) {
					appPersonalAddr = gson.fromJson(addressDetail.getPayloadColumn(),
							AddressDetailsPayload.class);
				} else if (coApplicantCustDtlId.compareTo(addressDetail.getCustDtlId()) == 0
						&& Constants.OCCUPATION.equalsIgnoreCase(addressDetail.getAddressType())) {
					coappOccupAddr = gson.fromJson(addressDetail.getPayloadColumn(),
							AddressDetailsPayload.class);
				} else if (coApplicantCustDtlId.compareTo(addressDetail.getCustDtlId()) == 0
						&& !Constants.OCCUPATION.equalsIgnoreCase(addressDetail.getAddressType())) {
					coappPersonalAddr = gson.fromJson(addressDetail.getPayloadColumn(),
							AddressDetailsPayload.class);
				}
			}

			BigDecimal businessIncome = BigDecimal.ZERO;
			BigDecimal wageIncome = BigDecimal.ZERO;
			BigDecimal agriculturalIncome = BigDecimal.ZERO;

			Optional<BCMPIIncomeDetails> bcmpiIncomeDataOpt = bcmpiIncomeDetailsRepo
					.findById(masterObj.getApplicationId());
			if (bcmpiIncomeDataOpt.isPresent()) {
				incomeDetails = new Gson().fromJson(bcmpiIncomeDataOpt.get().getPayload(),
						BCMPIIncomeDetailsWrapper.class);
				businessIncome = BCMPIIncomeDetailsWrapper
						.calculateBusinessIncome(incomeDetails.getBusiness(), Constants.COAPPLICANT_STRING)
						.add(BCMPIIncomeDetailsWrapper.calculateBusinessIncome(incomeDetails.getBusiness(),
								Constants.APPLICANT));
				wageIncome = BCMPIIncomeDetailsWrapper
						.calculateWageIncome(incomeDetails.getWage(), Constants.COAPPLICANT_STRING)
						.add(BCMPIIncomeDetailsWrapper.calculateWageIncome(incomeDetails.getWage(),
								Constants.APPLICANT));
				agriculturalIncome = BCMPIIncomeDetailsWrapper
						.calculateAgricultureIncome(incomeDetails.getAgriculture(), Constants.COAPPLICANT_STRING)
						.add(BCMPIIncomeDetailsWrapper.calculateAgricultureIncome(incomeDetails.getAgriculture(),
								Constants.APPLICANT));
			}

			loanDtls = loanDtlsRepo.findByApplicationIdAndAppId(masterObj.getApplicationId(), masterObj.getAppId());
			cbDtls = CibilDetailsRepo.findByApplicationIdAndAppId(masterObj.getApplicationId(), masterObj.getAppId());

			Set<String> ALLOWED_RESIDENCE_TYPES_RENTED = new HashSet<>(Arrays.asList("Rented", "Leased"));
			Set<String> ALLOWED_RESIDENCE_OWNERSHIP_OWNED = new HashSet<>(Arrays.asList("Self Owned", "Ancestral"));

			for (CADeviationMaster deviation : deviationMaster) {
				switch (deviation.getDeviationId().split("_")[2]) {
					case "D1":
						// Applicant with salaried/housewife/self-employed profile and co-applicant's
						// owns a business
						logger.debug("applicantCustDetailPayload.getOccupation() "
								+ applicantCustDetailPayload.getOccupation());
						logger.debug("coApplicantCustDetailPayload.getOccupation() "
								+ coApplicantCustDetailPayload.getOccupation());
						if (("Wage earner".equalsIgnoreCase(applicantCustDetailPayload.getOccupation())
								|| "Salaried".equalsIgnoreCase(applicantCustDetailPayload.getOccupation())
								|| "Homemaker".equalsIgnoreCase(applicantCustDetailPayload.getOccupation()))
								&& "Self Employed".equalsIgnoreCase(coApplicantCustDetailPayload.getOccupation())) {
							recordedDeviations.add(deviation);

						}
						break;
					case "D2":
						// Co-Applicant with salaried/housewife/self-employed profile and applicant's
						// owns a business
						logger.debug("applicantCustDetailPayload.getOccupation() "
								+ applicantCustDetailPayload.getOccupation());
						logger.debug("coApplicantCustDetailPayload.getOccupation() "
								+ coApplicantCustDetailPayload.getOccupation());
						if (("Wage earner".equalsIgnoreCase(coApplicantCustDetailPayload.getOccupation())
								|| "Salaried".equalsIgnoreCase(coApplicantCustDetailPayload.getOccupation())
								|| "Homemaker".equalsIgnoreCase(coApplicantCustDetailPayload.getOccupation()))
								&& "Self Employed".equalsIgnoreCase(applicantCustDetailPayload.getOccupation())) {
							recordedDeviations.add(deviation);
						}
						break;
					case "D3":
						// Applicant/co-applicant with street vendor profiles
						for (OccupationDetails occupDetail : occupationDetails) {
							OccupationDetailsPayload occupPayload = gson.fromJson(occupDetail.getPayloadColumn(),
									OccupationDetailsPayload.class);
							if (StringUtils.isNotEmpty(occupPayload.getStreetVendor())
									&& "YES".equalsIgnoreCase(occupPayload.getStreetVendor())) {
								recordedDeviations.add(deviation);
								break;
							}
						}
						break;
					case "D4":
						// Applicant (Existing GL customer) with NO-HIT in Bureau
						// TBD
						break;
					case "D5":
						// Applicant/Co-Applicant business stability norms not met i.e
						// Applicant/Co-Applicant any business tenure less than 3 years
						for (OccupationDetails occupDetail : occupationDetails) {
							OccupationDetailsPayload occupPayload = gson.fromJson(occupDetail.getPayloadColumn(),
									OccupationDetailsPayload.class);
							if (StringUtils.isNotEmpty(occupPayload.getBusinessEmpVintageYear())
									&& Integer.parseInt(occupPayload.getBusinessEmpVintageYear()) < 3) {
								recordedDeviations.add(deviation);
								break;
							}
						}
						break;
					case "D6":
						// Applicant/Co-Applicant staying at Rented Residence and Residence address same
						// as Business address
						for (Address address : appPersonalAddr.getAddressList()) {
							if (Constants.PRESENT.equalsIgnoreCase(address.getAddressType())
									&& ALLOWED_RESIDENCE_TYPES_RENTED.contains(address.getResidenceOwnership())
									&& Constants.PRESENT
											.equalsIgnoreCase(
													appOccupAddr.getAddressList().get(0).getAddressSameAs())) {
								recordedDeviations.add(deviation);
								break;
							}
						}

						for (Address address : coappPersonalAddr.getAddressList()) {
							if (Constants.PRESENT.equalsIgnoreCase(address.getAddressType())
									&& ALLOWED_RESIDENCE_TYPES_RENTED.contains(address.getResidenceOwnership())
									&& Constants.PRESENT
											.equalsIgnoreCase(
													coappOccupAddr.getAddressList().get(0).getAddressSameAs())) {
								recordedDeviations.add(deviation);
								break;
							}
						}
						break;
					case "D7":
						// Residence stability criteria not met - less than 3 years for rented residence
						// and less than 2 years for own residence
						List<String> rentedStability = Arrays.asList(Constants.RENTEDADDRESSSTABILITY.split(","));
						List<String> selfOwnedStability = Arrays.asList(Constants.OWNEDADDRESSSTABILITY.split(","));
						for (Address address : appPersonalAddr.getAddressList()) {
							if (Constants.PRESENT.equalsIgnoreCase(address.getAddressType())
									&& (ALLOWED_RESIDENCE_TYPES_RENTED.contains(address.getResidenceOwnership())
											&& rentedStability.contains(address.getResidenceAddressSince()))
									|| (ALLOWED_RESIDENCE_OWNERSHIP_OWNED.contains(address.getResidenceOwnership())
											&& selfOwnedStability.contains(address.getResidenceAddressSince()))) {
								recordedDeviations.add(deviation);
								break;
							}
						}

						for (Address address : coappPersonalAddr.getAddressList()) {
							if (Constants.PRESENT.equalsIgnoreCase(address.getAddressType())
									&& (ALLOWED_RESIDENCE_TYPES_RENTED.contains(address.getResidenceOwnership())
											&& rentedStability.contains(address.getResidenceAddressSince()))
									|| (ALLOWED_RESIDENCE_OWNERSHIP_OWNED.contains(address.getResidenceOwnership())
											&& selfOwnedStability.contains(address.getResidenceAddressSince()))) {
								recordedDeviations.add(deviation);
								break;
							}
						}
						break;
					case "D8":
						// Applicant existing loans start date should be greater than 6 months. Top up
						// loans need not be considered.
						// TBD
						break;
					case "D9":
						// Extended RF loans to active CA Grameen customers (waiting borrowers of GL)
						// with >12 and <24 months gap
						// TBD
						break;
				}

			}
			for (ReassessmentMaster assessment : reassessmentMaster) {
				logger.debug("assessment.toString() " + assessment.toString());
				BigDecimal minval = (null != assessment.getMinValue()) ? new BigDecimal(assessment.getMinValue())
						: new BigDecimal("0.00");
				BigDecimal maxval = (null != assessment.getMaxValue()) ? new BigDecimal(assessment.getMaxValue())
						: new BigDecimal("0.00");
				logger.debug("minval -- maxval " + minval + " -- " + maxval);

				switch (assessment.getCriteria()) {
					case "Loan Amount":
						logger.debug("inside loanDtls.get(0).getBmRecommendedLoanAmount() "
								+ loanDtls.get(0).getBmRecommendedLoanAmount());
						if (loanDtls.get(0).getBmRecommendedLoanAmount().compareTo(minval) > 0) {
							recordedReassessments.add(assessment);
						}
						break;

					case "Income":
						logger.debug("inside occupationDtls " + occupationDetails.toString());
						for (OccupationDetails occupDetail : occupationDetails) {
							logger.debug("inside occupDetail " + occupDetail.toString());
							OccupationDetailsPayload occupPayload = gson.fromJson(occupDetail.getPayloadColumn(),
									OccupationDetailsPayload.class);
							logger.debug("inside occupationDtls " + occupPayload.toString());
							boolean isIncomeWithinRange = null != assessment.getMaxValue()
									&& occupPayload.getAnnualIncome().compareTo(minval) > 0
									&& occupPayload.getAnnualIncome().compareTo(maxval) < 0;
							boolean isIncomeInvalid = null == assessment.getMaxValue()
									&& occupPayload.getAnnualIncome().compareTo(minval) > 0;
							if (isIncomeWithinRange || isIncomeInvalid) {
								recordedReassessments.add(assessment);
								break;
							}
						}
						break;
					case "FOIR":
						logger.debug("inside FOIR " + cbDtls.toString());
						for (CibilDetails cbDtl : cbDtls) {
							logger.debug("inside cbDtl " + cbDtl.toString());
							CibilDetailsPayload cbDtlPayload = gson.fromJson(cbDtl.getPayloadColumn(),
									CibilDetailsPayload.class);
							logger.debug("inside cbDtlPayload " + cbDtlPayload.toString());
							if (null != cbDtlPayload.getFoirPercentage()
									&& new BigDecimal(cbDtlPayload.getFoirPercentage()).compareTo(minval) > 0) {
								recordedReassessments.add(assessment);
								break;
							}
						}

						break;

					case "Score":
						logger.debug("inside Score " + cbDtls.toString());
						for (CibilDetails cbDtl : cbDtls) {
							logger.debug("inside cbDtl " + cbDtl.toString());
							CibilDetailsPayload cbDtlPayload = gson.fromJson(cbDtl.getPayloadColumn(),
									CibilDetailsPayload.class);
							logger.debug("inside cbDtlPayload " + cbDtlPayload.toString());
							if (new BigDecimal(cbDtlPayload.getCbScore()).compareTo(minval) > 0
									&& new BigDecimal(cbDtlPayload.getCbScore()).compareTo(maxval) < 0) {
								recordedReassessments.add(assessment);
								break;
							}
						}
						break;

					case "Other Income":
						logger.debug("inside Other Income ");
						if (wageIncome.compareTo(businessIncome) > 0
								|| agriculturalIncome.compareTo(businessIncome) > 0) {
							recordedReassessments.add(assessment);
						}
						break;

					case "Rented Premises":
						logger.debug("inside rented premises ");
						for (Address AppAddress : appPersonalAddr.getAddressList()) {
							for (Address coAppAddress : coappPersonalAddr.getAddressList()) {
								if (Constants.PRESENT.equalsIgnoreCase(AppAddress.getAddressType())
										&& ALLOWED_RESIDENCE_TYPES_RENTED.contains(AppAddress.getResidenceOwnership())
										&& Constants.PRESENT.equalsIgnoreCase(coAppAddress.getAddressType())
										&& ALLOWED_RESIDENCE_TYPES_RENTED
												.contains(coAppAddress.getResidenceOwnership())) {
									recordedReassessments.add(assessment);
								}
							}
						}
						break;

					case "Indebtedness - Overall":
						logger.debug("inside Indebtedness " + cbDtls.toString());
						for (CibilDetails cbDtl : cbDtls) {
							logger.debug("inside cbDtl " + cbDtl.toString());
							CibilDetailsPayload cbDtlPayload = gson.fromJson(cbDtl.getPayloadColumn(),
									CibilDetailsPayload.class);
							logger.debug("inside cbDtlPayload " + cbDtlPayload.toString());
							BigDecimal totIndebtness = new BigDecimal(
									cbDtlPayload.getTotIndebtness().split(":")[1].split("\\|")[0].trim());
							logger.debug("inside totIndebtness " + totIndebtness.toString());
							if (totIndebtness.compareTo(minval) > 0) {
								recordedReassessments.add(assessment);
								break;
							}
						}
						break;
					case "CCM":
						logger.debug("inside CCM ");
						recordedReassessments.add(assessment);
						break;

				}
			}
			logger.debug("recordedDeviations " + recordedDeviations.toString());
			logger.debug("recordedReassessments " + recordedReassessments.toString());

			if (!recordedDeviations.isEmpty()) {
				for (CADeviationMaster deviationRecord : recordedDeviations) {
					DeviationRATracker deviationRATracker = new DeviationRATracker();
					deviationRATracker.setApplicationId(masterObj.getApplicationId());
					deviationRATracker.setAppId(masterObj.getAppId());
					deviationRATracker.setRecordId(deviationRecord.getDeviationId());
					deviationRATracker.setRecordMsg(deviationRecord.getDeviationDescription());
					deviationRATracker.setRecordType(Constants.CA_DEVIATION);
					deviationRATracker.setCaBy(userId);
					if (deviationRecord.getAutoApprove().equalsIgnoreCase("Y")
							&& deviationRecord.getBcm().equalsIgnoreCase("N")
							&& deviationRecord.getAcm().equalsIgnoreCase("N")) {
						deviationRATracker.setAuthority(Constants.SYSTEM);
						deviationRATracker.setApprovedBy(Constants.SYSTEM);
						deviationRATracker.setApprovedStatus(Constants.APPROVED);
					} else if (deviationRecord.getBcm().equalsIgnoreCase("Y")
							&& deviationRecord.getAutoApprove().equalsIgnoreCase("N")
							&& deviationRecord.getAcm().equalsIgnoreCase("N")) {
						deviationRATracker.setAuthority(Constants.BCM);
						deviationRATracker.setApprovedStatus(Constants.PENDING);

						insertionFlags.add(Constants.CA_DEVIATION);
					} else if (deviationRecord.getAcm().equalsIgnoreCase("Y")
							&& deviationRecord.getAutoApprove().equalsIgnoreCase("N")
							&& deviationRecord.getBcm().equalsIgnoreCase("N")) {
						deviationRATracker.setAuthority(Constants.ACM);
						deviationRATracker.setApprovedStatus(Constants.PENDING);

						insertionFlags.add(Constants.CA_DEVIATION);
					}
					deviationRATracker.setCreateTs(LocalDateTime.now());
					deviationRATracker.setProduct(deviationRecord.getProduct());
					logger.debug("recordedDeviations " + recordedDeviations.toString());
					deviationRATrackerRepo.save(deviationRATracker);
				}
				// insertionFlags.add(Constants.CA_DEVIATION);
			}
			if (!recordedReassessments.isEmpty()) {
				for (ReassessmentMaster reassessmentRecord : recordedReassessments) {
					DeviationRATracker deviationRATracker = new DeviationRATracker();
					deviationRATracker.setApplicationId(masterObj.getApplicationId());
					deviationRATracker.setAppId(masterObj.getAppId());
					deviationRATracker.setRecordId(reassessmentRecord.getReassessmentId());
					deviationRATracker.setRecordMsg(reassessmentRecord.getReassessmentDescription());
					deviationRATracker.setRecordType(Constants.REASSESSMENT);
					deviationRATracker.setCaBy(userId);
					if (reassessmentRecord.getBcm().equalsIgnoreCase("Y")
							&& reassessmentRecord.getAcm().equalsIgnoreCase("N")
							&& reassessmentRecord.getAm().equalsIgnoreCase("N")) {
						deviationRATracker.setAuthority(Constants.BCM);
						deviationRATracker.setApprovedStatus(Constants.PENDING);
					} else if (reassessmentRecord.getAcm().equalsIgnoreCase("Y")
							&& reassessmentRecord.getBcm().equalsIgnoreCase("N")
							&& reassessmentRecord.getAm().equalsIgnoreCase("N")) {
						deviationRATracker.setAuthority(Constants.ACM);
						deviationRATracker.setApprovedStatus(Constants.PENDING);
					} else if (reassessmentRecord.getAm().equalsIgnoreCase("Y")
							&& reassessmentRecord.getBcm().equalsIgnoreCase("N")
							&& reassessmentRecord.getAcm().equalsIgnoreCase("N")) {
						deviationRATracker.setAuthority(Constants.AM);
						deviationRATracker.setApprovedStatus(Constants.PENDING);
					}
					deviationRATracker.setCreateTs(LocalDateTime.now());
					deviationRATracker.setProduct(reassessmentRecord.getProduct());
					logger.debug("deviationRATracker " + deviationRATracker.toString());
					deviationRATrackerRepo.save(deviationRATracker);
				}
				insertionFlags.add(Constants.REASSESSMENT);
			}
			logger.debug("insertionFlags " + insertionFlags.toString());
		} catch (Exception e) {

			insertionFlags.add(Constants.ERROR);

			logger.debug("Error in ValidateDeviation : {}", e.getMessage(), e);
		}
		return insertionFlags;
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "creditDeviationApplicationMovementFallback")
	public Mono<Response> creditDeviationApplicationMovement(FetchDeleteUserRequest fetchDeleteUserRequest,
			Properties prop, String roleId) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		response.setResponseHeader(responseHeader);
		FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest.getRequestObj();
		String status = customerDataFields.getStatus();
		CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
		String nextStage = AppStatus.PENDINGPRESANCTION.getValue();
		if ((AppStatus.PENDINGREASSESSMENT.getValue().equalsIgnoreCase(status)
				|| AppStatus.REJECTED.getValue().equalsIgnoreCase(status)
				|| AppStatus.RPCVERIFIED.getValue().equalsIgnoreCase(status)
				|| AppStatus.CACOMPLETED.getValue().equalsIgnoreCase(status)
				|| AppStatus.PENDINGPRESANCTION.getValue().equalsIgnoreCase(status))) {
			List<String> applnStatus = new ArrayList<>();
			applnStatus.add(AppStatus.PENDINGDEVIATION.getValue());
			logger.debug("app id : {} ", customerDataFields.getAppId());
			logger.debug("application id : {} ", customerDataFields.getApplicationId());
			logger.debug("version no : {} ", customerDataFields.getVersionNum());
			logger.debug("application status : {} ", applnStatus);
			Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
					.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
							customerDataFields.getApplicationId(), customerDataFields.getVersionNum(), applnStatus);
			logger.debug("Getting optional master object");
			if (masterObjDb.isPresent()) {
				logger.debug("Master data value present.");
				Gson gson = new Gson();
				ApplicationMaster masterObj = masterObjDb.get();
				if (AppStatus.PENDINGPRESANCTION.getValue().equalsIgnoreCase(status)) {
					List<DeviationRATracker> deviationData = deviationRATrackerRepo
							.findByApplicationIdAndApprovedStatus(customerDataFields.getApplicationId(),
									Constants.PENDING);
					if (!deviationData.isEmpty()) {
						for (DeviationRATracker deviationRecord : deviationData) {
							if (deviationRecord.getRecordType().equalsIgnoreCase(Constants.CA_DEVIATION)) {
								responseHeader.setResponseCode(ResponseCodes.PENDING_DEVIATION.getKey());
								response.setResponseHeader(responseHeader);
								responseBody.setResponseObj("Some deviations are still pending.");
								response.setResponseBody(responseBody);
								return Mono.just(response);
							} else if (deviationRecord.getRecordType().equalsIgnoreCase(Constants.REASSESSMENT)) {
								String action = Constants.SUBMITREASSESSMENT;
								WorkFlowDetails workFlowDetails = changeWorkFlowDetails(Constants.PENDINGDEVIATION,
										Constants.PENDINGDEVIATION, action);
								customerDataFields.setWorkFlow(workFlowDetails);
								status = workFlowDetails.getNextWorkflowStatus();
								nextStage = Constants.REASSESSMENT;
							}
						}
					}
				}
				customerIdentification.setNextStage(nextStage);
				masterObj.setRemarks(null);
				masterObj.setUpdatedBy(customerDataFields.getUserId());
				masterObj.setAssignedTo(null);
				cobService.updateStatus(masterObj, status);

				logger.debug("customerDataFields :" + customerDataFields.toString());
				PopulateapplnWFRequest req = new PopulateapplnWFRequest();
				PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
				reqFields.setAppId(masterObj.getAppId());
				reqFields.setApplicationId(masterObj.getApplicationId());
				reqFields.setCreatedBy(customerDataFields.getUserId());
				reqFields.setVersionNum(masterObj.getVersionNum());
				reqFields.setApplicationStatus(masterObj.getApplicationStatus());
				WorkFlowDetails wf = customerDataFields.getWorkFlow();
				logger.debug("customerDataFields.getWorkFlow() :" + customerDataFields.getWorkFlow().toString());
				wf.setRemarks(customerDataFields.getRemarks());
				reqFields.setWorkflow(wf);
				req.setRequestObj(reqFields);
				logger.debug("req :" + req.toString());
				commonCoreService.populateApplnWorkFlow(req);
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				customerIdentification.setVersionNum(customerDataFields.getVersionNum());
				responseBody.setResponseObj(gson.toJson(customerIdentification));
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
				logger.debug("application status update completed");
			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
			}
		} else {
			responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
		}
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return Mono.just(response);
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "creditReassessmentApplicationMovementFallback")
	public Mono<Response> creditReassessmentApplicationMovement(FetchDeleteUserRequest fetchDeleteUserRequest,
			Properties prop, String roleId) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		response.setResponseHeader(responseHeader);
		FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest.getRequestObj();
		String status = customerDataFields.getStatus();
		CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
		String nextStage = AppStatus.PENDINGPRESANCTION.getValue();
		if ((AppStatus.REJECTED.getValue().equalsIgnoreCase(status)
				|| AppStatus.RPCVERIFIED.getValue().equalsIgnoreCase(status)
				|| AppStatus.CACOMPLETED.getValue().equalsIgnoreCase(status)
				|| AppStatus.PENDINGPRESANCTION.getValue().equalsIgnoreCase(status))) {
			List<String> applnStatus = new ArrayList<>();
			applnStatus.add(AppStatus.PENDINGREASSESSMENT.getValue());
			logger.debug("app id : {} ", customerDataFields.getAppId());
			logger.debug("application id : {} ", customerDataFields.getApplicationId());
			logger.debug("version no : {} ", customerDataFields.getVersionNum());
			logger.debug("application status : {} ", applnStatus);
			Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
					.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
							customerDataFields.getApplicationId(), customerDataFields.getVersionNum(), applnStatus);
			logger.debug("Getting optional master object");
			if (masterObjDb.isPresent()) {
				logger.debug("Master data value present.");
				Gson gson = new Gson();
				ApplicationMaster masterObj = masterObjDb.get();
				if (AppStatus.PENDINGPRESANCTION.getValue().equalsIgnoreCase(status)) {
					List<DeviationRATracker> deviationData = deviationRATrackerRepo
							.findByApplicationIdAndApprovedStatus(customerDataFields.getApplicationId(),
									Constants.PENDING);
					if (!deviationData.isEmpty()) {
						for (DeviationRATracker deviationRecord : deviationData) {
							if (deviationRecord.getRecordType().equalsIgnoreCase(Constants.REASSESSMENT)) {
								responseHeader.setResponseCode(ResponseCodes.PENDING_REASSESSMENT.getKey());
								response.setResponseHeader(responseHeader);
								responseBody.setResponseObj("Some reassessments are still pending.");
								response.setResponseBody(responseBody);
								return Mono.just(response);
							}
						}
					}
				}
				customerIdentification.setNextStage(nextStage);
				masterObj.setRemarks(null);
				masterObj.setUpdatedBy(customerDataFields.getUserId());
				masterObj.setAssignedTo(null);
				cobService.updateStatus(masterObj, status);

				logger.debug("customerDataFields :" + customerDataFields.toString());
				PopulateapplnWFRequest req = new PopulateapplnWFRequest();
				PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
				reqFields.setAppId(masterObj.getAppId());
				reqFields.setApplicationId(masterObj.getApplicationId());
				reqFields.setCreatedBy(customerDataFields.getUserId());
				reqFields.setVersionNum(masterObj.getVersionNum());
				reqFields.setApplicationStatus(masterObj.getApplicationStatus());
				WorkFlowDetails wf = customerDataFields.getWorkFlow();
				logger.debug("customerDataFields.getWorkFlow() :" + customerDataFields.getWorkFlow().toString());
				wf.setRemarks(customerDataFields.getRemarks());
				reqFields.setWorkflow(wf);
				req.setRequestObj(reqFields);
				logger.debug("req :" + req.toString());
				commonCoreService.populateApplnWorkFlow(req);
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				customerIdentification.setVersionNum(customerDataFields.getVersionNum());
				responseBody.setResponseObj(gson.toJson(customerIdentification));
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
				logger.debug("application status update completed");
			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
			}
		} else {
			responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
		}
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return Mono.just(response);
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "stageMovementApplicationFallback")
	public Mono<Response> preSanctionApplicationMovement(FetchDeleteUserRequest fetchDeleteUserRequest,
			Properties prop, String roleId) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		response.setResponseHeader(responseHeader);
		FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest.getRequestObj();
		String status = customerDataFields.getStatus();
		if ((!(CommonUtils.isNullOrEmpty(status)))
				&& (AppStatus.RPCVERIFIED.getValue().equalsIgnoreCase(status)
						|| AppStatus.REJECTED.getValue().equalsIgnoreCase(status)
						|| AppStatus.CACOMPLETED.getValue().equalsIgnoreCase(status))) {
			List<String> applnStatus = new ArrayList<>();
			String nextStage = Constants.CACOMPLETED;
			applnStatus.add(AppStatus.PENDINGPRESANCTION.getValue());
			logger.debug("app id : {} ", customerDataFields.getAppId());
			logger.debug("application id : {} ", customerDataFields.getApplicationId());
			logger.debug("version no : {} ", customerDataFields.getVersionNum());
			logger.debug("application status : {} ", applnStatus);
			Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
					.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
							customerDataFields.getApplicationId(), customerDataFields.getVersionNum(), applnStatus);
			logger.debug("Getting optional master object");
			if (masterObjDb.isPresent()) {
				logger.debug("Master data value present.");
				Gson gson = new Gson();
				CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
				ApplicationMaster masterObj = masterObjDb.get();

				if (AppStatus.CACOMPLETED.getValue().equalsIgnoreCase(status)) {
					List<ApplicationWorkflow> applicationWorkflows = applicationWorkflowRepository
							.findByApplicationIdAndApplicationStatusInOrderByWorkflowSeqNum(
									customerDataFields.getApplicationId(),
									Arrays.asList(AppStatus.SANCTIONED.getValue()));
					if (!applicationWorkflows.isEmpty()) {
						String action = Constants.RESANCTION;
						WorkFlowDetails workFlowDetails = changeWorkFlowDetails(Constants.PENDINGPRESANCTION,
								Constants.PENDINGPRESANCTION, action);
						customerDataFields.setWorkFlow(workFlowDetails);
						status = workFlowDetails.getNextWorkflowStatus();
						nextStage = Constants.RESANCTION;
					}
				}
				customerIdentification.setNextStage(nextStage);
				masterObj.setRemarks(null);
				masterObj.setUpdatedBy(customerDataFields.getUserId());
				masterObj.setAssignedTo(null);
				cobService.updateStatus(masterObj, status);

				logger.debug("customerDataFields :" + customerDataFields.toString());
				PopulateapplnWFRequest req = new PopulateapplnWFRequest();
				PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
				reqFields.setAppId(masterObj.getAppId());
				reqFields.setApplicationId(masterObj.getApplicationId());
				reqFields.setCreatedBy(customerDataFields.getUserId());
				reqFields.setVersionNum(masterObj.getVersionNum());
				reqFields.setApplicationStatus(masterObj.getApplicationStatus());
				WorkFlowDetails wf = customerDataFields.getWorkFlow();
				logger.debug("customerDataFields.getWorkFlow() :" + customerDataFields.getWorkFlow().toString());
				wf.setRemarks(customerDataFields.getRemarks());
				reqFields.setWorkflow(wf);
				req.setRequestObj(reqFields);
				logger.debug("req :" + req.toString());
				commonCoreService.populateApplnWorkFlow(req);
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				customerIdentification.setVersionNum(customerDataFields.getVersionNum());
				responseBody.setResponseObj(gson.toJson(customerIdentification));
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
				logger.debug("application status update completed");
			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
			}
		} else {
			responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
		}
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return Mono.just(response);
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "sanctionApplicationMovementFallback")
	public Mono<Object> sanctionApplicationMovement(FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
			Properties prop) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		try {
			response.setResponseHeader(responseHeader);
			FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest.getRequestObj();
			String status = customerDataFields.getStatus();
			String action = customerDataFields.getWorkFlow().getAction();
			String remarks = customerDataFields.getRemarks();
			if ((AppStatus.REJECTED.getValue().equalsIgnoreCase(status)
					|| AppStatus.RPCVERIFIED.getValue().equalsIgnoreCase(status)
					|| AppStatus.SANCTIONED.getValue().equalsIgnoreCase(status))) {
				List<String> applnStatus = new ArrayList<>();
				applnStatus.add(AppStatus.CACOMPLETED.getValue());
				logger.debug("app id : {} ", customerDataFields.getAppId());
				logger.debug("application id : {} ", customerDataFields.getApplicationId());
				logger.debug("version no : {} ", customerDataFields.getVersionNum());
				logger.debug("application status : {} ", applnStatus);
				Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
						.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
								customerDataFields.getApplicationId(), customerDataFields.getVersionNum(), applnStatus);
				String applicationId = customerDataFields.getApplicationId();
				logger.debug("Getting optional master object");
				if (masterObjDb.isPresent()) {
					logger.debug("Master data value present.");
					Gson gson = new Gson();
					CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
					ApplicationMaster masterObj = masterObjDb.get();
					// Coapplicant Creation Followed by loan Creation call
					if (Constants.SUBMIT.equalsIgnoreCase(action)) {
						Optional<LoanDetails> coapplicantIdOpt = loanDtlsRepo
								.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(
										customerDataFields.getAppId(), applicationId);
						if (coapplicantIdOpt.isPresent()) {
							Optional<CibilDetails> cibilDetailsOptional = cibilDetailsRepository
									.findCibilDetailsByCustomerTypeAndApplicationId(Constants.COAPPLICANT,
											applicationId);
							if (cibilDetailsOptional.isPresent()) {
								logger.debug("cibilDetailsOptional.get() : {}", cibilDetailsOptional.get());
								CibilDetailsPayload cibilDetailsPayload = gson.fromJson(
										cibilDetailsOptional.get().getPayloadColumn(), CibilDetailsPayload.class);
								logger.debug("cibilDetailsPayload : {}", cibilDetailsPayload);
								if (null != cibilDetailsPayload.getEligibleAmt()) {
									LoanDetails loanDetails = coapplicantIdOpt.get();
									BigDecimal bmRecommendedAmount = loanDetails.getBmRecommendedLoanAmount();
									BigDecimal eligibleAmt = new BigDecimal(cibilDetailsPayload.getEligibleAmt());
									if (eligibleAmt.compareTo(bmRecommendedAmount) > 0) {
										logger.info(
												"BRE Eligible amount {} is greater than BM recommended amount {} for applicationId {}",
												eligibleAmt, bmRecommendedAmount,
												customerDataFields.getApplicationId());
										return Mono.just(getFailureApiJson(
												"Sanction amount is greater than BM recommended amount. Please modify the sanction amount",
												"Sanction Application Movement"));
									}
									loanDetails.setSanctionedLoanAmount(
											new BigDecimal(cibilDetailsPayload.getEligibleAmt()));
									loanDtlsRepo.save(loanDetails);
									String[] remarksIterable = remarks.split("\\|");
									String frontendLoanAmount = remarksIterable[0];
									String updatedRemarks = remarks.replace(frontendLoanAmount,
											String.valueOf(loanDetails.getSanctionedLoanAmount()));
									customerDataFields.setRemarks(updatedRemarks);
								}
							}
							String coapplicantId = coapplicantIdOpt.get().getCoapplicantId();
							// Coapplicant creation & applicant update
							if (masterObj.getProductCode().equalsIgnoreCase(Constants.UNNATI_PRODUCT_CODE)) {
								return performSanctionUnnatiProductT24Calls(applicationId, masterObj, header, prop,
										customerDataFields, coapplicantId)
										.switchIfEmpty(
												Mono.defer(() -> {
													// Loan Creation
													if (!isStepSuccessful(applicationId, Constants.LOAN_CREATION,
															Constants.SANCTION)) {
														return processLoanCreation(masterObj, applicationId, header,
																prop, customerDataFields,
																Constants.SANCTION);
													}
													// Loan Repayment schedule
													if (!isStepSuccessful(applicationId,
															Constants.LOAN_REPAYMENT_SCHEDULE, Constants.SANCTION)) {
														return initiateLoanRepaySchedule(masterObj, applicationId,
																header, prop,
																Constants.SANCTION, customerDataFields, "");
													}
													return Mono.empty();
												}));
							} else if (masterObj.getProductCode()
									.equalsIgnoreCase(Constants.RENEWAL_LOAN_PRODUCT_CODE)) {
								return performSanctionRenewalProductT24Calls(applicationId, masterObj, header, prop,
										customerDataFields, coapplicantId)
										.switchIfEmpty(
												Mono.defer(() -> {
													// Loan Creation
													if (!isStepSuccessful(applicationId, Constants.LOAN_CREATION,
															Constants.SANCTION)) {
														return processLoanCreation(masterObj, applicationId, header,
																prop, customerDataFields,
																Constants.SANCTION);
													}
													// Loan Repayment schedule
													if (!isStepSuccessful(applicationId,
															Constants.LOAN_REPAYMENT_SCHEDULE, Constants.SANCTION)) {
														return initiateLoanRepaySchedule(masterObj, applicationId,
																header, prop,
																Constants.SANCTION, customerDataFields, "");
													}
													return Mono.empty();
												}));
							} else if (masterObj.getProductCode()
									.equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)) {
								return performOpenMarketProductT24Calls(applicationId, masterObj, header, prop,
										customerDataFields, coapplicantId, Constants.SANCTION)
										.switchIfEmpty(
												Mono.defer(() -> {
													// Loan Creation
													if (!isStepSuccessful(applicationId, Constants.LOAN_CREATION,
															Constants.SANCTION)) {
														return processLoanCreation(masterObj, applicationId, header,
																prop, customerDataFields,
																Constants.SANCTION);
													}
													// Loan Repayment schedule
													if (!isStepSuccessful(applicationId,
															Constants.LOAN_REPAYMENT_SCHEDULE, Constants.SANCTION)) {
														return initiateLoanRepaySchedule(masterObj, applicationId,
																header, prop,
																Constants.SANCTION, customerDataFields, "");
													}
													return Mono.empty();
												}));
							} else {
								return Mono
										.just(getFailureApiJson("Invalid product code :" + masterObj.getProductCode(),
												Constants.COAPPLICANT_UPDATION));
							}
						}
					}

					masterObj.setRemarks(customerDataFields.getRemarks());
					masterObj.setUpdatedBy(customerDataFields.getUserId());
					masterObj.setAssignedTo(null);
					cobService.updateStatus(masterObj, status);

					logger.debug("customerDataFields :" + customerDataFields.toString());
					PopulateapplnWFRequest req = new PopulateapplnWFRequest();
					PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
					reqFields.setAppId(masterObj.getAppId());
					reqFields.setApplicationId(masterObj.getApplicationId());
					reqFields.setCreatedBy(customerDataFields.getUserId());
					reqFields.setVersionNum(masterObj.getVersionNum());
					reqFields.setApplicationStatus(masterObj.getApplicationStatus());
					WorkFlowDetails wf = customerDataFields.getWorkFlow();
					logger.debug("customerDataFields.getWorkFlow() :" + customerDataFields.getWorkFlow().toString());
					wf.setRemarks(customerDataFields.getRemarks());
					reqFields.setWorkflow(wf);
					req.setRequestObj(reqFields);
					logger.debug("req :" + req.toString());
					commonCoreService.populateApplnWorkFlow(req);
					responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
					customerIdentification.setVersionNum(customerDataFields.getVersionNum());
					responseBody.setResponseObj(gson.toJson(customerIdentification));
					response.setResponseBody(responseBody);
					response.setResponseHeader(responseHeader);
					logger.debug("application status update completed");
				} else {
					responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
				}
			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
			}
			response.setResponseBody(responseBody);
			response.setResponseHeader(responseHeader);
		} catch (Exception e) {
			logger.error("Exception in loan creation API - sanctionApplicationMovement :: ", e);
		}
		return Mono.just(response);
	}

	private Mono<Object> performSanctionUnnatiProductT24Calls(
			String applicationId,
			ApplicationMaster masterObj,
			Header header,
			Properties prop,
			FetchDeleteUserFields customerDataFields,
			String coapplicantId) {

		if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_CREATION, Constants.SANCTION)
				|| !isStepSuccessful(applicationId, Constants.CO_CUSTOMER_FETCH, Constants.SANCTION)) {
			return initiateCoApplicantCreation(masterObj, header, prop, customerDataFields,
					Constants.SANCTION);
		}
		// coapplicant update
		if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_UPDATION, Constants.SANCTION)) {
			logger.info("co-applicant update for coapplicantId={} ", coapplicantId);
			return initiateCoapplicantUpdation(masterObj, header, prop, customerDataFields, coapplicantId,
					Constants.SANCTION, true);
		}
		if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_DEDUPE_UPDATE, Constants.SANCTION)) {
			return initiateDedupeTableUpdate(masterObj, header, prop, customerDataFields, coapplicantId,
					Constants.SANCTION, true);
		}
		if (!isStepSuccessful(applicationId, Constants.APPLICANT_UPDATION, Constants.SANCTION)) {
			return initiateApplicantUpdation(masterObj, header, prop, customerDataFields, masterObj.getMemberId(),
					Constants.SANCTION, coapplicantId);
		}
		if (!isStepSuccessful(applicationId, Constants.APPLICANT_DEDUPE_UPDATE, Constants.SANCTION)) {
			return initiateDedupeTableUpdate(masterObj, header, prop, customerDataFields, coapplicantId,
					Constants.SANCTION, false);
		}
		return Mono.empty();
	}

	private Mono<Object> performSanctionRenewalProductT24Calls(
			String applicationId,
			ApplicationMaster masterObj,
			Header header,
			Properties prop,
			FetchDeleteUserFields customerDataFields,
			String coapplicantId) {
		Optional<CustomerDetails> coappDetailsOpt = customerDetailsRepository
				.findByApplicationIdAndAppIdAndCustomerType(masterObj.getApplicationId(),
						masterObj.getAppId(),
						Constants.COAPPLICANT);
		if (!coappDetailsOpt.isPresent()) {
			logger.warn("No co-applicant details found for appId={} and applicationId={}",
					customerDataFields.getAppId(), applicationId);
			return Mono.just(getFailureApiJson("Missing coapplicant details",
					Constants.COAPPLICANT_UPDATION));
		}
		CustomerDetailsPayload payload = new Gson().fromJson(
				coappDetailsOpt.get().getPayloadColumn(),
				CustomerDetailsPayload.class);
		String isNewCoapplicant = payload != null ? payload.getIsNewCustomer() : null;

		boolean createNewCoapplicant = isNewCoapplicant != null
				&& isNewCoapplicant.toUpperCase().contains("CREATE");
		if (createNewCoapplicant) {
			if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_CREATION, Constants.SANCTION)
					|| !isStepSuccessful(applicationId, Constants.CO_CUSTOMER_FETCH, Constants.SANCTION)) {
				return initiateCoApplicantCreation(masterObj, header, prop, customerDataFields,
						Constants.SANCTION);
			}
			if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_UPDATION, Constants.SANCTION)) {
				logger.info("co-applicant update for coapplicantId={} ", coapplicantId);
				return initiateCoapplicantUpdation(masterObj, header, prop, customerDataFields, coapplicantId,
						Constants.SANCTION, true);
			}
			if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_DEDUPE_UPDATE, Constants.SANCTION)) {
				return initiateDedupeTableUpdate(masterObj, header, prop, customerDataFields, coapplicantId,
						Constants.SANCTION, true);
			}
			if (!isStepSuccessful(applicationId, Constants.APPLICANT_UPDATION, Constants.SANCTION)) {
				return initiateApplicantUpdation(masterObj, header, prop,
						customerDataFields, masterObj.getMemberId(),
						Constants.SANCTION, coapplicantId);
			}
			if (!isStepSuccessful(applicationId, Constants.APPLICANT_DEDUPE_UPDATE, Constants.SANCTION)) {
				return initiateDedupeTableUpdate(masterObj, header, prop, customerDataFields, coapplicantId,
						Constants.SANCTION, false);
			}
		} else {
			Optional<RenewalLeadDetails> renewalLeadDetails = renewalLeadDetailsRepository
					.findByCustomerId(masterObj.getSearchCode2());
			if (renewalLeadDetails.isPresent()) {
				String renewalCoapplicantId = renewalLeadDetails.get().getCoCustomerId();
				loanDtlsRepo.updateCoapplicantId(applicationId, renewalCoapplicantId);

				logger.info("co-applicant update for coapplicantId={} ", renewalCoapplicantId);
				if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_UPDATION, Constants.SANCTION)) {
					return initiateCoapplicantUpdation(masterObj, header, prop, customerDataFields,
							renewalCoapplicantId,
							Constants.SANCTION, true);
				}
				if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_DEDUPE_UPDATE, Constants.SANCTION)) {
					return initiateDedupeTableUpdate(masterObj, header, prop, customerDataFields, renewalCoapplicantId,
							Constants.SANCTION, true);
				}
				if (!isStepSuccessful(applicationId, Constants.APPLICANT_UPDATION, Constants.SANCTION)) {
					return initiateApplicantUpdation(masterObj, header, prop, customerDataFields,
							masterObj.getMemberId(),
							Constants.SANCTION, renewalCoapplicantId);
				}
				if (!isStepSuccessful(applicationId, Constants.APPLICANT_DEDUPE_UPDATE, Constants.SANCTION)) {
					return initiateDedupeTableUpdate(masterObj, header, prop, customerDataFields, renewalCoapplicantId,
							Constants.SANCTION, false);
				}
			} else {
				logger.warn("No co-applicant details found for appId={} and applicationId={}",
						customerDataFields.getAppId(), applicationId);
				return Mono.just(getFailureApiJson("Missing coapplicant ID", Constants.COAPPLICANT_UPDATION));
			}
		}
		return Mono.empty();
	}

	public Mono<Object> performOpenMarketProductT24Calls(
			String applicationId,
			ApplicationMaster masterObj,
			Header header,
			Properties prop,
			FetchDeleteUserFields customerDataFields,
			String coapplicantId, String currentStage) {
		logger.debug("Inside performOpenMarketProductT24Calls with applicationId={}, currentStage={}", applicationId,
				currentStage);

		if (Constants.SANCTION.equalsIgnoreCase(currentStage)) {
			if (!isStepSuccessful(applicationId, Constants.APPLICANT_ACTIVATION, currentStage)) {
				logger.debug("Applicant Activation started for applicationId: {}", applicationId);
				return initiateApplicantActivation(masterObj, header, prop, currentStage, customerDataFields);
			}

			if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_CREATION, currentStage)
					|| !isStepSuccessful(applicationId, Constants.CO_CUSTOMER_FETCH, currentStage)) {
				return initiateCoApplicantCreation(masterObj, header, prop, customerDataFields,
						currentStage);
			}
		}

		String applicantT24Id = masterObj.getApplicantT24Id();
		// coapplicant update
		logger.info("co-applicant update for coapplicantId={} ", coapplicantId);
		return initiateCoapplicantUpdation(masterObj, header, prop, customerDataFields, coapplicantId,
				currentStage, true);
		// if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_DEDUPE_UPDATE,
		// currentStage)) {
		// return initiateDedupeTableUpdate(masterObj, header, prop, customerDataFields,
		// coapplicantId,
		// currentStage, true);
		// }
		// if (!isStepSuccessful(applicationId, Constants.APPLICANT_UPDATION,
		// currentStage)) {
		// return initiateApplicantUpdation(masterObj, header, prop,
		// customerDataFields,applicantT24Id,
		// currentStage, coapplicantId);
		// }
		// if (!isStepSuccessful(applicationId, Constants.APPLICANT_DEDUPE_UPDATE,
		// currentStage)) {
		// return initiateDedupeTableUpdate(masterObj, header, prop, customerDataFields,
		// coapplicantId,
		// currentStage, false);
		// }
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "sanctionApplicationMovementFallback")
	public Mono<Object> ReSanctionApplicationMovement(FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
			Properties prop) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		try {
			response.setResponseHeader(responseHeader);
			FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest.getRequestObj();
			String status = customerDataFields.getStatus();
			String action = customerDataFields.getWorkFlow().getAction();
			String remarks = customerDataFields.getRemarks();
			if ((AppStatus.REJECTED.getValue().equalsIgnoreCase(status)
					|| AppStatus.RPCVERIFIED.getValue().equalsIgnoreCase(status)
					|| AppStatus.SANCTIONED.getValue().equalsIgnoreCase(status))) {
				List<String> applnStatus = new ArrayList<>();
				applnStatus.add(AppStatus.RESANCTION.getValue());
				logger.debug("app id : {} ", customerDataFields.getAppId());
				logger.debug("application id : {} ", customerDataFields.getApplicationId());
				logger.debug("version no : {} ", customerDataFields.getVersionNum());
				logger.debug("application status : {} ", applnStatus);
				Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
						.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
								customerDataFields.getApplicationId(), customerDataFields.getVersionNum(), applnStatus);
				String applicationId = customerDataFields.getApplicationId();
				logger.debug("Getting optional master object");
				if (masterObjDb.isPresent()) {
					logger.debug("Master data value present.");
					Gson gson = new Gson();
					CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
					ApplicationMaster masterObj = masterObjDb.get();
					if (Constants.SUBMIT.equalsIgnoreCase(action)) {
						Optional<LoanDetails> coapplicantIdOpt = loanDtlsRepo
								.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(
										customerDataFields.getAppId(), applicationId);

						if (coapplicantIdOpt.isPresent()) {
							Optional<CibilDetails> cibilDetailsOptional = cibilDetailsRepository
									.findCibilDetailsByCustomerTypeAndApplicationId(Constants.COAPPLICANT,
											applicationId);
							if (cibilDetailsOptional.isPresent()) {
								CibilDetailsPayload cibilDetailsPayload = gson.fromJson(
										cibilDetailsOptional.get().getPayloadColumn(), CibilDetailsPayload.class);
								if (null != cibilDetailsPayload.getEligibleAmt()) {
									LoanDetails loanDetails = coapplicantIdOpt.get();
									BigDecimal bmRecommendedAmount = loanDetails.getBmRecommendedLoanAmount();
									BigDecimal eligibleAmt = new BigDecimal(cibilDetailsPayload.getEligibleAmt());
									if (eligibleAmt.compareTo(bmRecommendedAmount) > 0) {
										logger.info(
												"BRE Eligible amount {} is greater than BM recommended amount {} for applicationId {}",
												eligibleAmt, bmRecommendedAmount,
												customerDataFields.getApplicationId());
										return Mono.just(getFailureApiJson(
												"Sanction amount is greater than BM recommended amount. Please modify the sanction amount",
												"Resanction Application Movement"));
									}

									loanDetails.setSanctionedLoanAmount(eligibleAmt);
									loanDtlsRepo.save(loanDetails);
									String[] remarksIterable = remarks.split("\\|");
									String frontendLoanAmount = remarksIterable[0];
									String updatedRemarks = remarks.replace(frontendLoanAmount,
											String.valueOf(loanDetails.getSanctionedLoanAmount()));
									customerDataFields.setRemarks(updatedRemarks);
								}
							}

						}
						if (masterObj.getProductCode().equalsIgnoreCase(Constants.UNNATI_PRODUCT_CODE)) {
							if (coapplicantIdOpt.isPresent()) {
								String coapplicantId = coapplicantIdOpt.get().getCoapplicantId();
								logger.info("co-applicant update for coapplicantId={} ", coapplicantId);
								return initiateCoapplicantUpdation(masterObj, header, prop, customerDataFields,
										coapplicantId, Constants.RESANCTION, true);

							} else {
								logger.warn("No co-applicant details found for appId={} and applicationId={}",
										customerDataFields.getAppId(), applicationId);
								return Mono.just(
										getFailureApiJson("Missing coapplicant ID", Constants.COAPPLICANT_UPDATION));
							}
						} else if (masterObj.getProductCode().equalsIgnoreCase(Constants.RENEWAL_LOAN_PRODUCT_CODE)) {
							Optional<CustomerDetails> coappDetailsOpt = customerDetailsRepository
									.findByApplicationIdAndAppIdAndCustomerType(masterObj.getApplicationId(),
											masterObj.getAppId(), Constants.COAPPLICANT);
							if (!coappDetailsOpt.isPresent()) {
								logger.warn("No co-applicant details found for appId={} and applicationId={}",
										customerDataFields.getAppId(), applicationId);
								return Mono.just(getFailureApiJson("Missing coapplicant details",
										Constants.COAPPLICANT_UPDATION));
							}
							CustomerDetailsPayload payload = new Gson().fromJson(
									coappDetailsOpt.get().getPayloadColumn(),
									CustomerDetailsPayload.class);
							String isNewCoapplicant = payload != null ? payload.getIsNewCustomer() : null;
							boolean createNewCoapplicant = isNewCoapplicant != null
									&& isNewCoapplicant.toUpperCase().contains("CREATE");

							if (createNewCoapplicant) {
								if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_CREATION,
										Constants.RESANCTION)
										|| !isStepSuccessful(applicationId, Constants.CO_CUSTOMER_FETCH,
												Constants.RESANCTION)) {
									return initiateCoApplicantCreation(masterObj, header, prop, customerDataFields,
											Constants.RESANCTION);
								}
							}

							if (coapplicantIdOpt.isPresent()) {
								String coapplicantId = coapplicantIdOpt.get().getCoapplicantId();
								logger.info("co-applicant update for coapplicantId={} ", coapplicantId);
								return initiateCoapplicantUpdation(masterObj, header, prop, customerDataFields,
										coapplicantId, Constants.RESANCTION, true);
							} else {
								logger.warn("No co-applicant details found for appId={} and applicationId={}",
										customerDataFields.getAppId(), applicationId);
								return Mono.just(
										getFailureApiJson("Missing coapplicant ID", Constants.COAPPLICANT_UPDATION));
							}
						} else if (masterObj.getProductCode()
								.equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)) {
							logger.debug("Inside Re-sanction flow for Open Market Product");
							if (coapplicantIdOpt.isPresent()) {
								String coapplicantId = coapplicantIdOpt.get().getCoapplicantId();
								logger.info(
										"Calling performOpenMarketProductT24Calls - applicationId={}, appId={}, productCode={}, coapplicantId={}, stage={}, header={}",
										applicationId, masterObj.getAppId(), masterObj.getProductCode(), coapplicantId,
										Constants.RESANCTION, String.valueOf(header));
								return performOpenMarketProductT24Calls(applicationId, masterObj, header, prop,
										customerDataFields, coapplicantId, Constants.RESANCTION);

							} else {
								logger.warn("No co-applicant details found for appId={} and applicationId={}",
										customerDataFields.getAppId(), applicationId);
								return Mono.just(
										getFailureApiJson("Missing coapplicant ID", Constants.COAPPLICANT_UPDATION));
							}
						} else {
							return Mono.just(getFailureApiJson("Invalid product code :" + masterObj.getProductCode(),
									Constants.COAPPLICANT_UPDATION));
						}
					}

					masterObj.setRemarks(customerDataFields.getRemarks());
					masterObj.setUpdatedBy(customerDataFields.getUserId());
					masterObj.setAssignedTo(null);
					cobService.updateStatus(masterObj, status);

					Optional<DBKITStageVerification> dbkitStageVerificationOpt = dbkitStageVerificationRepository
							.findById(applicationId);
					if (dbkitStageVerificationOpt.isPresent()) {
						dbkitStageVerificationRepository.delete(dbkitStageVerificationOpt.get());
					}

					logger.debug("customerDataFields :" + customerDataFields.toString());
					PopulateapplnWFRequest req = new PopulateapplnWFRequest();
					PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
					reqFields.setAppId(masterObj.getAppId());
					reqFields.setApplicationId(masterObj.getApplicationId());
					reqFields.setCreatedBy(customerDataFields.getUserId());
					reqFields.setVersionNum(masterObj.getVersionNum());
					reqFields.setApplicationStatus(masterObj.getApplicationStatus());
					WorkFlowDetails wf = customerDataFields.getWorkFlow();
					logger.debug("customerDataFields.getWorkFlow() :" + customerDataFields.getWorkFlow().toString());
					wf.setRemarks(customerDataFields.getRemarks());
					reqFields.setWorkflow(wf);
					req.setRequestObj(reqFields);
					logger.debug("req :" + req.toString());
					commonCoreService.populateApplnWorkFlow(req);
					responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
					customerIdentification.setVersionNum(customerDataFields.getVersionNum());
					responseBody.setResponseObj(gson.toJson(customerIdentification));
					response.setResponseBody(responseBody);
					response.setResponseHeader(responseHeader);
					logger.debug("application status update completed");
				} else {
					responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
				}
			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
			}
			response.setResponseBody(responseBody);
			response.setResponseHeader(responseHeader);
		} catch (Exception e) {
			logger.error("Exception in loan creation API - sanctionApplicationMovement :: ", e);
		}
		return Mono.just(response);
	}

	private Mono<Object> processLoanCreation(ApplicationMaster master, String applicationId,
			Header header, Properties prop, FetchDeleteUserFields customerDataFields, String currentStage) {
		if (master.getProductCode().equalsIgnoreCase(Constants.UNNATI_PRODUCT_CODE)) {
			return loanDtlsRepo.findTopByApplicationIdAndAppId(applicationId, customerDataFields.getAppId())
					.map(loan -> initiateLoanCreation(master, loan.getCoapplicantId(), applicationId, header, prop,
							customerDataFields, currentStage))
					.orElse(Mono.just(getFailureJson("Co-applicant Id not found")));
		} else if (master.getProductCode().equalsIgnoreCase(Constants.RENEWAL_LOAN_PRODUCT_CODE)) {
			return loanDtlsRepo.findTopByApplicationIdAndAppId(applicationId, customerDataFields.getAppId())
					.map(loan -> initiateLoanCreation(master, loan.getCoapplicantId(), applicationId, header, prop,
							customerDataFields, currentStage))
					.orElse(Mono.just(getFailureJson("Co-applicant Id not found")));
		} else if (master.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)) {
			return loanDtlsRepo.findTopByApplicationIdAndAppId(applicationId, customerDataFields.getAppId())
					.map(loan -> initiateLoanCreation(master, loan.getCoapplicantId(), applicationId, header, prop,
							customerDataFields, currentStage))
					.orElse(Mono.just(getFailureJson("Co-applicant Id not found")));

		} else {
			return Mono.just(
					getFailureApiJson("Invalid product code :" + master.getProductCode(), Constants.LOAN_CREATION));
		}
	}

	private List<String> extractErrorMessages(JSONObject errorObject) {
		JSONArray errorDetails = errorObject.optJSONArray(Constants.ERROR_DETAILS);
		Map<String, String> fieldReplacementMap = new HashMap<>();
		fieldReplacementMap.put("customerAddress[0]", "office ");
		fieldReplacementMap.put("customerAddress[1]", "present ");
		fieldReplacementMap.put("customerAddress[2]", "permanent ");
		fieldReplacementMap.put("customerAddress[3]", "communication ");
		fieldReplacementMap.put("coApplicantDetails[0]", "coapplicant ");
		fieldReplacementMap.put("phoneNumber[0]", "");
		fieldReplacementMap.put("customerShortName[0]", "");
		fieldReplacementMap.put("customerFirstName[0]", "");

		List<String> messages = new ArrayList<>();

		if (errorDetails != null) {
			for (Object err : errorDetails) {
				JSONObject errJson = new JSONObject(err.toString());
				String field = errJson.optString(Constants.FIELD_NAME, Constants.ERROR2);
				String message = errJson.optString(Constants.MESSAGE).toLowerCase();
				boolean replaced = false;

				for (Map.Entry<String, String> entry : fieldReplacementMap.entrySet()) {
					if (field.contains(entry.getKey())) {
						messages.add(field.replace(entry.getKey(), entry.getValue()) + " - " + message.trim());
						replaced = true;
						break;
					}
				}

				if (!replaced) {
					messages.add(field + " - " + message.trim());
				}
			}
		}

		return messages;
	}

	private Mono<Object> initiateCoApplicantCreation(ApplicationMaster master, Header header, Properties prop,
			FetchDeleteUserFields customerDataFields, String currentStage) {
		String applicationId = master.getApplicationId();
		String applicantId = master.getProductCode().equalsIgnoreCase(Constants.OPEN_MARKET_LOAN_PRODUCT)
				? master.getApplicantT24Id()
				: master.getMemberId();
		Mono<Object> coapplicantCreation = loanService.coapplicantCreation(applicationId, master.getAppId(),
				applicantId, header, prop, false, "", false);
		return coapplicantCreation.flatMap(response -> {
			try {
				logger.debug("Coapplicant creation response: {}", response);
				String json = (new Gson()).toJson(response);
				JSONObject apiResp = convertResponseToJson(json);
				if (apiResp.has(Constants.HEADER)) {
					JSONObject headerJson = apiResp.getJSONObject(Constants.HEADER);
					if (ResponseCodes.SUCCESS.getValue().equalsIgnoreCase(headerJson.optString(Constants.STATUS))) {
						String coApplicantId = headerJson.optString("id");
						logger.debug("coApplicantId :" + coApplicantId);
						saveLog(applicationId, Constants.COAPPLICANT_CREATION, loanService.getRequestLog(),
								response.toString(), ResponseCodes.SUCCESS.getValue(), null, currentStage);
						if (!coApplicantId.equalsIgnoreCase(applicantId)) {
							loanDtlsRepo.updateCoapplicantId(applicationId, coApplicantId);
						}
						return initiateCoapplicantUpdation(master, header, prop, customerDataFields,
								coApplicantId, currentStage, true);
					}
					if (apiResp.has(Constants.ERROR1) && !apiResp.getJSONObject(Constants.ERROR1).isEmpty()) {
						JSONArray coCustCreationErr = apiResp.getJSONObject(Constants.ERROR1)
								.getJSONArray(Constants.ERROR_DETAILS);
						List<String> coCustCreationErrors = extractErrorMessages(apiResp.getJSONObject("error"));
						saveLog(applicationId, Constants.COAPPLICANT_CREATION, loanService.getRequestLog(),
								response.toString(), ResponseCodes.FAILURE.getValue(), coCustCreationErrors.toString(),
								currentStage);
						logger.debug("CoCustCreationErr :" + coCustCreationErr.toString());
						List<String> coCustCreationErrList = new ArrayList<>();
						for (Object field : coCustCreationErr) {
							JSONObject fieldError = new JSONObject(field.toString());
							logger.debug("coCustCreation Field Error " + fieldError);
							String message = fieldError.optString(Constants.MESSAGE);
							String expected = prop.getProperty(
									CobFlagsProperties.CO_CUST_FETCH_INPUT_ERROR.getKey());
							// Build a tolerant regex from the configured message
							String baseRegex = expected
									.toLowerCase()
									.replaceAll("voter-id", "voter[- ]?id") // voterId / voter-id / VOTER-ID
									.replaceAll("\\s+", "\\\\s*") // flexible spacing
									.replaceAll(":", "\\\\s*:?\\\\s*"); // optional colon & spaces
							// Full regex with optional brackets and numeric customer ID
							String finalRegex = "(?i)\\[?\\s*.*?" + baseRegex + "(\\d+)\\s*\\]?";

							Pattern pattern = Pattern.compile(finalRegex);
							Matcher matcher = pattern.matcher(message);

							if (matcher.find()) {
								String existingCoCustId = matcher.group(1);
								logger.debug("Extracted Co-Customer ID: " + existingCoCustId);

								return initiateCoapplicantUpdation(master, header, prop, customerDataFields,
										existingCoCustId, currentStage, true);

							} else {
								logger.debug("No matching Co-Customer ID found in the message. : {}", message);
								coCustCreationErrList.add(fieldError.optString(Constants.FIELD_NAME, Constants.ERROR2)
										+ " - " + fieldError.optString(Constants.MESSAGE, "Unknown Error").trim());

								saveLog(applicationId, Constants.COAPPLICANT_CREATION, this.loanService.getRequestLog(),
										coCustCreationErrList.toString() + "No customer id in the error message",
										ResponseCodes.FAILURE.getValue(), coCustCreationErrList.toString(),
										currentStage);
								Response failureJson = getFailureApiJson(coCustCreationErrList.toString(),
										Constants.CO_CUSTOMER_FETCH);
								return Mono.just(failureJson);
							}
						}
						List<String> errors = extractErrorMessages(apiResp.getJSONObject(Constants.ERROR1));
						saveLog(applicationId, Constants.COAPPLICANT_CREATION, loanService.getRequestLog(),
								response.toString(), ResponseCodes.FAILURE.getValue(), errors.toString(), currentStage);
						return Mono.just(getFailureApiJson(errors.toString(), Constants.COAPPLICANT_CREATION));
					}
				}
				saveLog(applicationId, Constants.COAPPLICANT_CREATION, this.loanService.getRequestLog(),
						response.toString(), ResponseCodes.FAILURE.getValue(), Constants.SERVICE_DOWN, currentStage);
				return Mono.just(getFailureApiJson(Constants.SERVICE_DOWN, Constants.COAPPLICANT_CREATION));

			} catch (Exception e) {
				logger.error("Error parsing coapplicant response", e);
				saveLog(applicationId, Constants.COAPPLICANT_CREATION, loanService.getRequestLog(), response.toString(),
						ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
				return Mono.just(getFailureApiJson(e.toString(), Constants.COAPPLICANT_CREATION));
			}
		}).onErrorResume(e -> {
			logger.error("Error during Coapplicant Creation: ", e);
			saveLog(applicationId, Constants.COAPPLICANT_CREATION, loanService.getRequestLog(), e.getMessage(),
					ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
			if (e.getMessage().toLowerCase().contains("voter")) {
				return Mono.just(getFailureApiJson(e.getMessage(), Constants.COAPPLICANT_CREATION));
			} else {
				return Mono
						.just(getFailureApiJson("Error during Coapplicant Creation", Constants.COAPPLICANT_CREATION));
			}
		});
	}

	private Mono<Object> initiateDedupeTableUpdate(ApplicationMaster master, Header header, Properties prop,
			FetchDeleteUserFields customerDataFields, String coApplicantId, String currentStage, boolean coapplUpdate) {
		String applicationId = master.getApplicationId();
		String updateApi = coapplUpdate ? Constants.COAPPLICANT_DEDUPE_UPDATE : Constants.APPLICANT_DEDUPE_UPDATE;
		String applicantId = master.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)
				? master.getApplicantT24Id()
				: master.getMemberId();
		String updateCustomerId = coapplUpdate ? coApplicantId : applicantId;
		Mono<Object> dedupeTableUpdate = loanService.dedupeTableUpdate(master, header, prop, updateCustomerId,
				coapplUpdate);
		return dedupeTableUpdate.flatMap(res -> {
			logger.debug("Dedupe Table update response: {}", res);
			try {
				String jsonBody = (new Gson()).toJson(res);
				JSONObject apiRes = convertResponseToJson(jsonBody);
				if (apiRes.has("response") && apiRes.has("msg")) {
					String resp = apiRes.getString("response");
					String msg = apiRes.getString("msg");

					if (Constants.SUCCESS.equalsIgnoreCase(resp)) {
						if ("Details Updated".equalsIgnoreCase(msg)) {
							saveLog(applicationId, updateApi, loanService.getRequestLog(), res.toString(),
									ResponseCodes.SUCCESS.getValue(), null, currentStage);
							logger.debug("Applicant Update started");
							if (coapplUpdate) {
								Mono<Object> applicantUpdateMono = initiateApplicantUpdation(master, header, prop,
										customerDataFields, applicantId, currentStage, coApplicantId);
								return applicantUpdateMono;
							} else {
								return initiateLoanCreation(master, coApplicantId, applicationId, header, prop,
										customerDataFields, currentStage);
							}
						} else {
							Object resultObj = apiRes.opt("result");
							String existingCustId = null;

							try {
								if (resultObj instanceof JSONObject) {
									JSONObject resultJson = (JSONObject) resultObj;
									existingCustId = resultJson.optString("customer_id", null);
								} else if (resultObj instanceof JSONArray) {
									JSONArray resultArr = (JSONArray) resultObj;
									if (resultArr.length() > 0) {
										JSONObject first = resultArr.getJSONObject(0);
										existingCustId = first.optString("customer_id", null);
									}
								}
							} catch (Exception ex) {
								logger.warn("Unexpected 'result' format in dedupe response", ex);
							}

							String errorMsg = "Dedupe Table Update failed: " + msg +
									(existingCustId != null ? ", Existing Customer ID: " + existingCustId : "");
							saveLog(applicationId, updateApi, loanService.getRequestLog(), res.toString(),
									ResponseCodes.FAILURE.getValue(), errorMsg, currentStage);
							return Mono.just(getFailureApiJson(errorMsg, updateApi));
						}
					}
				}
				saveLog(applicationId, updateApi, this.loanService.getRequestLog(),
						res.toString(), ResponseCodes.FAILURE.getValue(), Constants.SERVICE_DOWN, currentStage);
				return Mono.just(getFailureApiJson(Constants.SERVICE_DOWN, updateApi));
			} catch (Exception e) {
				logger.error("Error parsing dedupeTableUpdate API response", e);
				saveLog(applicationId, updateApi, loanService.getRequestLog(), res.toString(),
						ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
				return Mono.just(getFailureApiJson(e.toString(), updateApi));
			}
		}).onErrorResume(e -> {
			logger.error("Error during dedupe reverse feed", e);
			String frontendMsg = "Error during dedupe reverse feed";
			if (e.getMessage() != null) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					JsonNode rootNode = objectMapper.readTree(e.getMessage());
					logger.debug("rootNode: {}", rootNode.toString());
					JsonNode msgNode = rootNode
							.path("errorMessage")
							.path("msg");
					logger.debug("msgNode: {}", msgNode.toString());
					if (!msgNode.isMissingNode() && !msgNode.asText().trim().isEmpty()) {

						frontendMsg = msgNode.asText();
						String errorCustomerId = null;
						Pattern pattern = Pattern.compile(
								"(?i)(?:customer\\s*id[^a-zA-Z0-9]*([a-zA-Z0-9]{4,})|([a-zA-Z0-9]{4,})[^a-zA-Z]*customer\\s*id)");
						Matcher matcher = pattern.matcher(frontendMsg);

						if (matcher.find()) {
							logger.debug("Matcher found in error message: {}", frontendMsg);
							errorCustomerId = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
							logger.debug("Extracted Customer ID from error message: {}", errorCustomerId);
							if (errorCustomerId.equalsIgnoreCase(updateCustomerId)
									|| errorCustomerId.equalsIgnoreCase(master.getSearchCode2())) {
								logger.debug(
										"dedupe Update skipped for customerId: {}, since same ID recieved in response {}",
										updateCustomerId, frontendMsg);
								saveLog(applicationId, updateApi, loanService.getRequestLog(), e.toString(),
										ResponseCodes.SUCCESS.getValue(), null, currentStage);
								if (coapplUpdate) {
									Mono<Object> applicantUpdateMono = initiateApplicantUpdation(master, header, prop,
											customerDataFields, applicantId, currentStage, coApplicantId);
									return applicantUpdateMono;
								} else {
									return initiateLoanCreation(master, coApplicantId, applicationId, header, prop,
											customerDataFields, currentStage);
								}
							}
							if (errorCustomerId.equalsIgnoreCase(coApplicantId)
									|| errorCustomerId.equalsIgnoreCase(applicantId)) {
								if (frontendMsg.toUpperCase().contains("PHONEDEDUPE")) {
									logger.debug(
											"dedupe Update skipped for customerId: {}, since applicant/coapplicant id recieved in the response {}",
											updateCustomerId, frontendMsg);
									saveLog(applicationId, updateApi, loanService.getRequestLog(), e.toString(),
											ResponseCodes.SUCCESS.getValue(), null, currentStage);
									if (coapplUpdate) {
										Mono<Object> applicantUpdateMono = initiateApplicantUpdation(master, header,
												prop,
												customerDataFields, applicantId, currentStage, coApplicantId);
										return applicantUpdateMono;
									} else {
										return initiateLoanCreation(master, coApplicantId, applicationId, header, prop,
												customerDataFields, currentStage);
									}
								}
							} else {
								saveLog(applicationId, updateApi, loanService.getRequestLog(), e.getMessage(),
										ResponseCodes.FAILURE.getValue(), frontendMsg, currentStage);
								return Mono.just(getFailureApiJson(frontendMsg, updateApi));
							}
						} else {
							logger.debug("No Customer ID found in error message: {}", frontendMsg);
						}
					}
				} catch (Exception ex) {
					logger.warn("Failed to parse dedupe reverse feed error message", ex);
				}
			}
			saveLog(applicationId, updateApi, loanService.getRequestLog(), e.getMessage(),
					ResponseCodes.FAILURE.getValue(), frontendMsg, currentStage);
			return Mono.just(getFailureApiJson(frontendMsg, updateApi));
		});
	}

	private Mono<Object> initiateApplicantActivation(
			ApplicationMaster master,
			Header header,
			Properties prop,
			String currentStage,
			FetchDeleteUserFields customerDataFields) {
		String applicationId = master.getApplicationId();
		Mono<Object> applicantActivationRes = loanService.applicantActivation(master, header, prop);
		return applicantActivationRes.flatMap(res -> {
			try {
				logger.debug("applicant activation response: {}", res);
				String jsonBody = (new Gson()).toJson(res);
				JSONObject apiRes = convertResponseToJson(jsonBody);
				if (apiRes.has(Constants.HEADER)) {
					JSONObject headerJs = apiRes.getJSONObject(Constants.HEADER);
					String status = headerJs.optString(Constants.STATUS);
					if (ResponseCodes.SUCCESS.getValue().equalsIgnoreCase(status)) {
						String t24id = headerJs.optString("id");
						logger.debug("Applicant activated with T24 ID: {}", t24id);
						applicationMasterRepository2.updateApplicantT24IdInMaster(applicationId, t24id);
						master.setApplicantT24Id(t24id);
						saveLog(applicationId, Constants.APPLICANT_ACTIVATION, loanService.getRequestLog(),
								res.toString(),
								ResponseCodes.SUCCESS.getValue(), null, currentStage);
						return initiateCoApplicantCreation(master, header, prop, customerDataFields, currentStage);
					} else {
						if (apiRes.has(Constants.ERROR1) && !apiRes.getJSONObject(Constants.ERROR1).isEmpty()) {
							List<String> errors = extractErrorMessages(apiRes.getJSONObject(Constants.ERROR1));
							saveLog(applicationId, Constants.APPLICANT_ACTIVATION, loanService.getRequestLog(),
									res.toString(), ResponseCodes.FAILURE.getValue(), errors.toString(), currentStage);
							return Mono.just(getFailureApiJson(errors.toString(), Constants.APPLICANT_ACTIVATION));
						}
					}
				}
				saveLog(applicationId, Constants.APPLICANT_ACTIVATION, this.loanService.getRequestLog(),
						res.toString(), ResponseCodes.FAILURE.getValue(), Constants.SERVICE_DOWN, currentStage);
				return Mono.just(getFailureApiJson(Constants.SERVICE_DOWN, Constants.APPLICANT_ACTIVATION));
			} catch (Exception e) {
				logger.error("Error parsing coapplicant response", e);
				saveLog(applicationId, Constants.APPLICANT_ACTIVATION, loanService.getRequestLog(), res.toString(),
						ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
				return Mono.just(getFailureApiJson(e.toString(), Constants.APPLICANT_ACTIVATION));
			}
		}).onErrorResume(e -> {
			logger.error("Error during Customer update: ", e);
			saveLog(applicationId, Constants.APPLICANT_ACTIVATION, loanService.getRequestLog(), e.getMessage(),
					ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
			return Mono.just(getFailureApiJson("Error during Customer update", Constants.APPLICANT_ACTIVATION));
		});
	}

	private Mono<Object> initiateCoapplicantUpdation(ApplicationMaster master, Header header, Properties prop,
			FetchDeleteUserFields customerDataFields, String coApplicantId, String currentStage, boolean coapplUpdate) {
		String applicationId = master.getApplicationId();
		String updateApi = Constants.COAPPLICANT_UPDATION;
		String applicantId = master.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)
				? master.getApplicantT24Id()
				: master.getMemberId();
		Mono<Object> coapplicantUpdation = loanService.coapplicantCreation(applicationId, master.getAppId(),
				applicantId, header, prop, true, coApplicantId, coapplUpdate);
		return coapplicantUpdation.flatMap(res -> {
			try {
				logger.debug("Coapplicant creation response: {}", res);
				String jsonBody = (new Gson()).toJson(res);
				JSONObject apiRes = convertResponseToJson(jsonBody);
				if (apiRes.has(Constants.HEADER)) {
					JSONObject headerJs = apiRes.getJSONObject(Constants.HEADER);
					String status = headerJs.optString(Constants.STATUS);

					// If Record not changed considering it as Success
					boolean isBusinessRecordNotChanged = false;
					if ("failed".equalsIgnoreCase(status) && apiRes.has(Constants.ERROR1)) {
						JSONObject errorObj = apiRes.getJSONObject(Constants.ERROR1);
						if (errorObj.has(Constants.ERROR_DETAILS)) {
							JSONArray errorDetails = errorObj.getJSONArray(Constants.ERROR_DETAILS);
							for (int i = 0; i < errorDetails.length(); i++) {
								JSONObject err = errorDetails.getJSONObject(i);
								if (Constants.UNCHANGED_RECORD_CODE.equalsIgnoreCase(err.optString("code"))) {
									isBusinessRecordNotChanged = true;
									break;
								}
							}
						}
					}

					if (ResponseCodes.SUCCESS.getValue().equalsIgnoreCase(status)
							|| isBusinessRecordNotChanged) {
						String coApplicantupdateId = headerJs.optString("id");
						logger.debug("coApplicantId: {}", coApplicantupdateId);

						loanDtlsRepo.updateCoapplicantId(applicationId, coApplicantupdateId);

						saveLog(applicationId, updateApi, loanService.getRequestLog(), res.toString(),
								ResponseCodes.SUCCESS.getValue(), null, currentStage);
						logger.debug("dedupe Update started");
						return initiateDedupeTableUpdate(master, header, prop,
								customerDataFields, coApplicantId, currentStage, coapplUpdate);

					}

					// Actual failure
					if (apiRes.has(Constants.ERROR1) && !apiRes.getJSONObject(Constants.ERROR1).isEmpty()) {
						List<String> errors = extractErrorMessages(apiRes.getJSONObject(Constants.ERROR1));
						saveLog(applicationId, updateApi, loanService.getRequestLog(),
								res.toString(), ResponseCodes.FAILURE.getValue(), errors.toString(), currentStage);
						return Mono.just(getFailureApiJson(errors.toString(), updateApi));
					}
				}
				saveLog(applicationId, Constants.COAPPLICANT_UPDATION, this.loanService.getRequestLog(),
						res.toString(), ResponseCodes.FAILURE.getValue(), Constants.SERVICE_DOWN, currentStage);
				return Mono.just(getFailureApiJson(Constants.SERVICE_DOWN, Constants.COAPPLICANT_UPDATION));

			} catch (Exception e) {
				logger.error("Error parsing coapplicant response", e);
				saveLog(applicationId, updateApi, loanService.getRequestLog(), res.toString(),
						ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
				return Mono.just(getFailureApiJson(e.toString(), updateApi));
			}
		}).onErrorResume(e -> {
			logger.error("Error during Customer update: ", e);
			saveLog(applicationId, updateApi, loanService.getRequestLog(), e.getMessage(),
					ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
			return Mono.just(getFailureApiJson("Error during Customer update", updateApi));
		});
	}

	private Mono<Object> initiateApplicantUpdation(ApplicationMaster master, Header header, Properties prop,
			FetchDeleteUserFields customerDataFields, String memberId, String currentStage, String coApplicantId) {
		String applicationId = master.getApplicationId();
		String updateApi = Constants.APPLICANT_UPDATION;
		String applicantIdForUpdation = master.getProductCode()
				.equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)
						? master.getApplicantT24Id()
						: master.getMemberId();
		Mono<Object> coapplicantUpdation = loanService.coapplicantCreation(applicationId, master.getAppId(),
				applicantIdForUpdation, header, prop, true, coApplicantId, false);

		return coapplicantUpdation.flatMap(res -> {
			try {
				logger.debug("Applicant Update response: {}", res);
				String jsonBody = (new Gson()).toJson(res);
				JSONObject apiRes = convertResponseToJson(jsonBody);
				if (apiRes.has(Constants.HEADER)) {
					JSONObject headerJs = apiRes.getJSONObject(Constants.HEADER);
					String status = headerJs.optString(Constants.STATUS);

					// If Record not changed considering it as Success
					boolean isBusinessRecordNotChanged = false;
					if ("failed".equalsIgnoreCase(status) && apiRes.has(Constants.ERROR1)) {
						JSONObject errorObj = apiRes.getJSONObject(Constants.ERROR1);
						if (errorObj.has(Constants.ERROR_DETAILS)) {
							JSONArray errorDetails = errorObj.getJSONArray(Constants.ERROR_DETAILS);
							for (int i = 0; i < errorDetails.length(); i++) {
								JSONObject err = errorDetails.getJSONObject(i);
								if (Constants.UNCHANGED_RECORD_CODE.equalsIgnoreCase(err.optString("code"))) {
									isBusinessRecordNotChanged = true;
									break;
								}
							}
						}
					}

					if (ResponseCodes.SUCCESS.getValue().equalsIgnoreCase(status) || isBusinessRecordNotChanged) {
						logger.debug("Applicant Update started");
						saveLog(applicationId, updateApi, loanService.getRequestLog(), res.toString(),
								ResponseCodes.SUCCESS.getValue(), null, currentStage);
						logger.debug("dedupe Update started");
						return initiateDedupeTableUpdate(master, header, prop,
								customerDataFields, coApplicantId, currentStage, false);
					}

					// Actual failure
					if (apiRes.has(Constants.ERROR1) && !apiRes.getJSONObject(Constants.ERROR1).isEmpty()) {
						List<String> errors = extractErrorMessages(apiRes.getJSONObject(Constants.ERROR1));
						saveLog(applicationId, updateApi, loanService.getRequestLog(),
								res.toString(), ResponseCodes.FAILURE.getValue(), errors.toString(), currentStage);
						return Mono.just(getFailureApiJson(errors.toString(), updateApi));
					}
				}
				saveLog(applicationId, Constants.APPLICANT_UPDATION, this.loanService.getRequestLog(),
						res.toString(), ResponseCodes.FAILURE.getValue(), Constants.SERVICE_DOWN, currentStage);
				return Mono.just(getFailureApiJson(Constants.SERVICE_DOWN, Constants.APPLICANT_UPDATION));

			} catch (Exception e) {
				logger.error("Error parsing Applicant response", e);
				saveLog(applicationId, updateApi, loanService.getRequestLog(), res.toString(),
						ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
				return Mono.just(getFailureApiJson(e.toString(), updateApi));
			}
		}).onErrorResume(e -> {
			logger.error("Error during Customer update: ", e);
			saveLog(applicationId, updateApi, loanService.getRequestLog(), e.getMessage(),
					ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
			return Mono.just(getFailureApiJson("Error during Customer update", updateApi));
		});
	}

	private Mono<Object> initiateLoanCreation(ApplicationMaster masterObj, String coApplicantId,
			String applicationId, Header header, Properties prop, FetchDeleteUserFields customerDataFields,
			String currentStage) {

		Optional<LoanDetails> loanOpt = loanDtlsRepo.findTopByApplicationIdAndAppId(applicationId,
				masterObj.getAppId());
		if (loanOpt.isPresent()) {
			String loanStatus = Optional.ofNullable(loanOpt.get().getLoanStatus()).orElse("");
			if (Constants.ACTIVE.equals(loanStatus) && currentStage.equalsIgnoreCase(Constants.RESANCTION)) {
				logger.debug("Loan Rejection API started");
				return initiateLoanRejection(masterObj, applicationId, header, prop, customerDataFields, coApplicantId,
						currentStage);
			}
		}
		String memberIdForLoanCreation = masterObj.getProductCode().equalsIgnoreCase(
				Constants.OPENMARKET_LOAN_PRODUCT_CODE) ? masterObj.getApplicantT24Id() : masterObj.getMemberId();
		logger.debug("Executing loan creation API");
		Mono<Object> loanCreation = this.loanService.loanCreation(masterObj.getApplicationId(),
				masterObj.getAppId(), memberIdForLoanCreation, header, prop, coApplicantId);

		return loanCreation.flatMap(loanResponse -> {
			logger.debug("Loan Creation API response {}", loanResponse);
			try {

				String json = (new Gson()).toJson(loanResponse);
				JSONObject apiResp = convertResponseToJson(json);
				JSONObject loanHeader = apiResp.getJSONObject(Constants.HEADER);
				if (loanHeader.has(Constants.STATUS)
						&& loanHeader.getString(Constants.STATUS).equalsIgnoreCase(ResponseCodes.SUCCESS.getValue())) {
					String loanId = loanHeader.getString("id");
					this.loanDtlsRepo.updateT24LoanId(masterObj.getApplicationId(), loanId);
					this.loanDtlsRepo.updateT24LoanStatus(masterObj.getApplicationId(), Constants.ACTIVE);
					JSONObject loanBody = apiResp.getJSONObject("body");
					saveLog(applicationId, Constants.LOAN_CREATION, this.loanService.getRequestLog(),
							loanResponse.toString(), ResponseCodes.SUCCESS.getValue(), null, currentStage);

					return initiateLoanRepaySchedule(masterObj, applicationId, header, prop, currentStage,
							customerDataFields, loanId);
				}
				if (apiResp.has(Constants.ERROR1) && !apiResp.getJSONObject(Constants.ERROR1).isEmpty()) {
					JSONArray loanErrors = apiResp.getJSONObject(Constants.ERROR1)
							.getJSONArray(Constants.ERROR_DETAILS);
					saveLog(applicationId, Constants.LOAN_CREATION, loanService.getRequestLog(),
							loanResponse.toString(), ResponseCodes.FAILURE.getValue(), loanErrors.toString(),
							currentStage);
					logger.debug("LoanErrors :" + loanErrors);
					List<String> loanErrorList = new ArrayList<>();
					for (Object field : loanErrors) {
						JSONObject fieldError = new JSONObject(field.toString());
						logger.debug("Field Error " + fieldError);
						if (fieldError.optString(Constants.MESSAGE).equalsIgnoreCase(
								prop.getProperty(CobFlagsProperties.LOAN_FETCH_INPUT_ERROR.getKey()))) {
							return loanService.loanFetch(applicationId, prop, masterObj, header)
									.flatMap(res -> {
										logger.debug("Loan Fetch API response: {}", res);
										try {
											JSONObject apiRes = convertResponseToJson(new Gson().toJson(res));
											JSONObject customerResponse = apiRes
													.optJSONObject("CustomerLoanDetailsResponse");
											if (customerResponse == null) {
												saveLog(applicationId, Constants.LOAN_FETCH,
														this.loanService.getRequestLog(), loanResponse.toString(),
														ResponseCodes.FAILURE.getValue(), "Customer Response is null",
														currentStage);
												return Mono.just(getFailureApiJson(
														"Loan Fetch Failed due to incorrect T24 response",
														Constants.LOAN_CREATION));
											}
											JSONObject statusHeader = customerResponse.optJSONObject("Status");
											if (statusHeader != null && statusHeader.optString("successIndicator")
													.equalsIgnoreCase(ResponseCodes.SUCCESS.getValue())) {
												JSONArray detailArray;
												try {
													detailArray = Optional.ofNullable(customerResponse)
															.map(obj -> obj.optJSONObject("CAGCUSTLNDETSType"))
															.map(obj -> obj.optJSONObject("gCAGCUSTLNDETSDetailType"))
															.map(obj -> obj.optJSONArray("mCAGCUSTLNDETSDetailType"))
															.orElseThrow(() -> new JSONException(
																	"Loan Fetch Failed: Missing loan details array"));
												} catch (JSONException ex) {
													logger.error("Loan fetch structure invalid: {}", ex.getMessage());
													saveLog(applicationId, Constants.LOAN_FETCH,
															this.loanService.getRequestLog(), loanResponse.toString(),
															ResponseCodes.FAILURE.getValue(), ex.toString(),
															currentStage);
													return Mono.just(getFailureApiJson(
															"Loan Fetch Failed due to incorrect T24 response",
															Constants.LOAN_CREATION));
												}
												String matchedLoanId = "";
												String productId = masterObj.getProductCode()
														.equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)
																? Constants.OPEN_MARKET_LOAN_PRODUCT
																: Constants.UNNATI_LOAN_PRODUCT;
												for (int i = 0; i < detailArray.length(); i++) {
													JSONObject detail = detailArray.getJSONObject(i);
													String[] loanIds = detail.optString("LoanID", "")
															.split(Constants.REGEX_DELIMITER);
													String[] loanAmounts = detail.optString("LoanAmount", "")
															.split(Constants.REGEX_DELIMITER);
													String[] statuses = detail.optString("Status", "")
															.split(Constants.REGEX_DELIMITER);
													String[] productIds = detail.optString("LoanProductID", "")
															.split(Constants.REGEX_DELIMITER);
													Optional<LoanDetails> loanRecord = loanDtlsRepo
															.findTopByApplicationIdAndAppId(
																	masterObj.getApplicationId(), masterObj.getAppId());
													BigDecimal sanctionedLoanAmount = loanRecord
															.map(LoanDetails::getSanctionedLoanAmount)
															.orElse(new BigDecimal(0));
													for (int j = 0; j < productIds.length; j++) {
														if (productIds[j].equalsIgnoreCase(productId)) {
															if (j < loanAmounts.length && j < statuses.length
																	&& j < loanIds.length) {
																if (loanAmounts[j].equalsIgnoreCase(
																		sanctionedLoanAmount.toString())
																		&& statuses[j]
																				.equalsIgnoreCase(Constants.APPROVED)) {
																	matchedLoanId = loanIds[j];
																	this.loanDtlsRepo.updateT24LoanId(
																			masterObj.getApplicationId(), loanIds[j]);
																	this.loanDtlsRepo.updateT24LoanStatus(
																			masterObj.getApplicationId(),
																			Constants.ACTIVE);
																	break;
																}
															}
														}
													}
													logger.debug("Loan ID: {}", Arrays.toString(loanIds));
													logger.debug("Loan Sanctioned AMount: {}", sanctionedLoanAmount);
													logger.debug("Loan Amounts in loan fetch: {}",
															Arrays.toString(loanAmounts));
												}
												if (!matchedLoanId.isEmpty()) {
													saveLog(applicationId, Constants.LOAN_FETCH,
															this.loanService.getRequestLog(), res.toString(),
															ResponseCodes.SUCCESS.getValue(), "", currentStage);
													return initiateLoanRepaySchedule(masterObj, applicationId, header,
															prop, currentStage, customerDataFields, matchedLoanId);
												} else {
													logger.warn("No matched Loan ID found for product {}", productId);
													saveLog(applicationId, Constants.LOAN_FETCH,
															this.loanService.getRequestLog(), res.toString(),
															ResponseCodes.FAILURE.getValue(),
															"No matched Loan ID found for product : " + productId,
															currentStage);
													return Mono.just(getFailureApiJson(
															"Another active loan is in progress for this customer in T24",
															Constants.LOAN_CREATION));
												}
											} else {
												logger.error("Loan fetch API response did not indicate success.");
												saveLog(applicationId, Constants.LOAN_FETCH,
														this.loanService.getRequestLog(), res.toString(),
														ResponseCodes.FAILURE.getValue(),
														"Loan fetch API response did not indicate success.",
														currentStage);
												return Mono.just(getFailureApiJson(
														"Loan Fetch Failed. Invalid Loan Fetch Response",
														Constants.LOAN_CREATION));
											}
										} catch (Exception e) {
											logger.error("Unexpected error during loan Fetch response processing", e);
											saveLog(applicationId, Constants.LOAN_FETCH,
													this.loanService.getRequestLog(), res.toString(),
													ResponseCodes.FAILURE.getValue(),
													"Unexpected error during loan Fetch response processing",
													currentStage);
											return Mono.just(getFailureApiJson("Exception during Loan Creation",
													Constants.LOAN_CREATION));
										}
									}).onErrorResume(e -> {
										logger.error("Error during Loan Fetch API: ", e);
										saveLog(applicationId, Constants.LOAN_FETCH, this.loanService.getRequestLog(),
												e.getMessage(), ResponseCodes.FAILURE.getValue(),
												"Error during Loan Fetch API: " + e.getMessage(), currentStage);
										return Mono.just(getFailureApiJson("Error during Loan Creation",
												Constants.LOAN_CREATION));
									});
						} else {
							loanErrorList.add(fieldError.optString(Constants.FIELD_NAME, Constants.ERROR2) + " - "
									+ fieldError.optString(Constants.MESSAGE, "Unknown Error").trim());
							logger.debug("Error List in Loan creation API {}", loanErrorList);
							saveLog(applicationId, Constants.LOAN_CREATION, this.loanService.getRequestLog(),
									loanResponse.toString(), ResponseCodes.FAILURE.getValue(), loanErrorList.toString(),
									currentStage);
							Response failureJson = getFailureApiJson(loanErrorList.toString(), Constants.LOAN_FETCH);
							return Mono.just(failureJson);
						}
					}
				}

				logger.error("Loan creation API failed: Empty or missing error details.");
				return Mono.just(getFailureApiJson("Empty or missing error details", Constants.LOAN_CREATION));
			} catch (Exception e) {
				logger.error("Unexpected error during loan response processing", e);
				return loanCreation;
			}
		}).onErrorResume(e -> {
			logger.error("Error during Loan Creation: ", e);
			saveLog(applicationId, Constants.LOAN_CREATION, this.loanService.getRequestLog(), null,
					ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
			return Mono.just(getFailureApiJson("Error during Loan Creation", Constants.LOAN_CREATION));
		});
	}

	public Response sendSanctionSms(String applicationId, String appId, Properties prop, boolean isDisbursed) {
		String phnNum = "";
		String custName = "";
		Response resp = null;

		Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
				.findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, Constants.INITIAL_VERSION_NO);
		logger.debug("Getting optional master object");
		if (masterObjDb.isPresent()) {
			logger.debug("Master data value present.");
			ApplicationMaster masterObj = masterObjDb.get();

			// To find lagnuage based on branch
			CustomerDataFields custmrDataFields = cobService.getCustomerData(masterObj,
					masterObj.getApplicationId(), masterObj.getAppId(), Constants.INITIAL_VERSION_NO);
			logger.debug("custmrDataFields.toString() : " + custmrDataFields.toString());
			Gson gsonObj = new Gson();
			LoanDetailsPayload payload = gsonObj.fromJson(custmrDataFields.getLoanDetails().getPayloadColumn(),
					LoanDetailsPayload.class);

			String language = payload.getLanguage();
			String loanId = "";
			Optional<LoanDetails> loanOpt = loanDtlsRepo
					.findTopByApplicationIdAndAppId(masterObj.getApplicationId(), masterObj.getAppId());
			if (loanOpt.isPresent()) {
				loanId = Optional.ofNullable(loanOpt.get().getT24LoanId()).orElse("");
			}

			for (CustomerDetails custDtl : custmrDataFields.getCustomerDetailsList()) {
				logger.debug("customer Type : " + custDtl.getCustomerType());
				if (custDtl.getCustomerType().equalsIgnoreCase(Constants.APPLICANT)) {
					phnNum = custDtl.getMobileNumber();
					logger.debug("phnNum : " + phnNum);
					custName = custDtl.getCustomerName();
					logger.debug("custName :" + custName);
				}
			}
			List<String> statusList = new ArrayList<>();
			statusList.add(WorkflowStatus.SANCTIONED.getValue());
			List<ApplicationWorkflow> wfList = applicationWorkflowRepository
					.findByApplicationIdAndApplicationStatusInOrderByCreateTsDesc(custmrDataFields.getApplicationId(),
							statusList);

			String sactionedDateStr = "Sanctioned Date";
			String disbursementDateStr = "Disbursement Date";
			if (!wfList.isEmpty()) {
				LocalDateTime sactionedDate = wfList.get(0).getCreateTs();
				logger.debug("sactionedDate raw" + sactionedDate);
				try {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
					sactionedDateStr = sactionedDate.format(formatter);
					logger.debug("sactionedDateStr" + sactionedDateStr);
					disbursementDateStr = LocalDate.now().format(formatter);
				} catch (Exception e) {
					logger.error("Error while date formating.");
				}
			}
			BigDecimal sanctionedAmount = custmrDataFields.getLoanDetails().getSanctionedLoanAmount();
			String amountStr = (sanctionedAmount != null) ? sanctionedAmount.toString() : "Sanctioned Amount";

			SendSmsAndEmailApiRequest sendSmsandEmailApiRequest = new SendSmsAndEmailApiRequest();
			SendSmsAndEmailRequestObject sendSmsAndEmailRequestObject = new SendSmsAndEmailRequestObject();
			sendSmsandEmailApiRequest.setAppId(masterObj.getAppId());
			sendSmsandEmailApiRequest.setInterfaceName(Constants.SMS_INTF);
			sendSmsandEmailApiRequest.setUserId(masterObj.getCreatedBy());
			sendSmsandEmailApiRequest.setUserName("");
			sendSmsAndEmailRequestObject.setActionType(Constants.OTP);
			sendSmsAndEmailRequestObject.setLanguage(language);
			sendSmsAndEmailRequestObject.setMobileNo(phnNum);
			sendSmsAndEmailRequestObject.setCustName(custName);
			sendSmsandEmailApiRequest.setRequestObject(sendSmsAndEmailRequestObject);

			String documentDownloadURL = prop.getProperty(
					CobFlagsProperties.SMS_DOC_DOWNLOAD_URL.getKey(),
					Constants.SMS_DOC_DOWNLOAD_URL);

			String requestType = isDisbursed ? Constants.DISBURSEMENT : Constants.SANCTION;

			documentDownloadURL = documentDownloadURL
					.replace("#APPLICATIONID#", applicationId)
					.replace("#REQUESTTYPE#", requestType);

			logger.debug("Document Download URL: {}", documentDownloadURL);
			if (isDisbursed) {
				sendSmsAndEmailRequestObject
						.setAttachmentContent(loanId + "|~|" + amountStr + "|~|" + sactionedDateStr + "|~|"
								+ LocalDate.now().toString() + "|~|" + documentDownloadURL);
				resp = sendSmsAndEmailService.sendSmsAndEmailService(sendSmsandEmailApiRequest, prop,
						SmsStage.DISBURSED);
			} else {
				sendSmsAndEmailRequestObject
						.setAttachmentContent(
								loanId + "|~|" + amountStr + "|~|" + disbursementDateStr + "|~|" + documentDownloadURL);
				resp = sendSmsAndEmailService.sendSmsAndEmailService(sendSmsandEmailApiRequest, prop,
						SmsStage.SANCTION);
			}
		} else {
			logger.error("Invalid Application Master");
		}
		return resp;
	}

	private Mono<Object> initiateLoanRejection(ApplicationMaster masterObj, String applicationId, Header header,
			Properties prop, FetchDeleteUserFields customerDataFields, String coApplicantId, String currentStage) {
		logger.debug("Executing loan Rejection API");
		Mono<Object> loanRejection = this.loanService.loanRejection(masterObj.getApplicationId(), prop,
				masterObj.getAppId(), header);
		return loanRejection.flatMap(loanResponse -> {
			logger.debug("Loan Rejection API response {}", loanResponse);
			try {
				String json = (new Gson()).toJson(loanResponse);
				JSONObject apiResp = convertResponseToJson(json);
				JSONObject loanHeader = apiResp.getJSONObject(Constants.HEADER);
				if (loanHeader.has(Constants.STATUS)
						&& loanHeader.getString(Constants.STATUS).equalsIgnoreCase(ResponseCodes.SUCCESS.getValue())) {
					// Saving log
					saveLog(applicationId, Constants.LOAN_REJECTION, this.loanService.getRequestLog(),
							loanResponse.toString(), ResponseCodes.SUCCESS.getValue(), null, currentStage);
					loanDtlsRepo.updateT24LoanStatus(applicationId, Constants.INACTIVE);
					return initiateLoanCreation(masterObj, coApplicantId, applicationId, header, prop,
							customerDataFields, currentStage);
				} else if (apiResp.has(Constants.ERROR1) && !apiResp.getJSONObject(Constants.ERROR1).isEmpty()) {
					JSONArray loanErrors = apiResp.getJSONObject(Constants.ERROR1)
							.getJSONArray(Constants.ERROR_DETAILS);
					logger.debug("LoanErrors " + loanErrors);
					List<String> loanErrorList = new ArrayList<>();
					for (Object field : loanErrors) {
						JSONObject fieldError = new JSONObject(field.toString());
						logger.debug("Field Error " + fieldError);
						loanErrorList.add(
								fieldError.optString(Constants.FIELD_NAME, Constants.ERROR2) + " - "
										+ fieldError.getString(Constants.MESSAGE).trim());
					}
					logger.debug("Error List in Loan Rejection API {}", loanErrorList);
					saveLog(applicationId, Constants.LOAN_REJECTION, this.loanService.getRequestLog(),
							loanResponse.toString(), ResponseCodes.FAILURE.getValue(), loanErrorList.toString(),
							currentStage);
					Response failureJson = getFailureApiJson(loanErrorList.toString(), Constants.LOAN_REJECTION);
					return Mono.just(failureJson);
				} else {
					logger.error("Loan Rejection API failed: Empty or missing error details.");
					return Mono.just(getFailureApiJson("Empty or missing error details", Constants.LOAN_REJECTION));
				}
			} catch (Exception e) {
				logger.error("Unexpected error during loan response processing", e);
				return loanRejection;
			}
		}).onErrorResume(e -> {
			logger.error("Error during Loan Rejection: ", e);
			saveLog(applicationId, Constants.LOAN_REJECTION, this.loanService.getRequestLog(), null,
					ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
			return Mono.just(getFailureApiJson("Error during Loan Rejection", Constants.LOAN_REJECTION));
		});
	}

	private Mono<Object> initiateLoanRepaySchedule(ApplicationMaster masterObj, String applicationId, Header header,
			Properties prop, String currentStage, FetchDeleteUserFields customerDataFields, String loanId) {
		logger.debug("Executing loanRepaySchedule API");

		Mono<Object> loanRepaySchd = this.loanService.loanRepaySchedule(masterObj.getApplicationId(), prop,
				masterObj.getAppId(), header);
		return loanRepaySchd.flatMap(loanResponse -> {
			logger.debug("loanRepaySchedule API response {}", loanResponse);
			try {
				String t24LoanId = loanId;
				if (t24LoanId.isEmpty()) {
					Optional<LoanDetails> loanOpt = loanDtlsRepo
							.findTopByApplicationIdAndAppId(masterObj.getApplicationId(), masterObj.getAppId());
					if (loanOpt.isPresent()) {
						t24LoanId = Optional.ofNullable(loanOpt.get().getT24LoanId()).orElse("");
					}
				}
				String json = (new Gson()).toJson(loanResponse);
				JSONObject apiResp = convertResponseToJson(json);
				if (apiResp.has("body") && apiResp.opt("body") instanceof JSONArray) {
					saveLog(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE, this.loanService.getRequestLog(),
							loanResponse.toString(), ResponseCodes.SUCCESS.getValue(), null, currentStage);
					masterObj.setRemarks(customerDataFields.getRemarks());
					masterObj.setUpdatedBy(customerDataFields.getUserId());
					masterObj.setAssignedTo(null);
					this.cobService.updateStatus(masterObj, customerDataFields.getStatus());
					logger.debug("customerDataFields :" + customerDataFields.toString());
					PopulateapplnWFRequest req = new PopulateapplnWFRequest();
					PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
					reqFields.setAppId(masterObj.getAppId());
					reqFields.setApplicationId(masterObj.getApplicationId());
					reqFields.setCreatedBy(customerDataFields.getUserId());
					reqFields.setVersionNum(masterObj.getVersionNum().intValue());
					reqFields.setApplicationStatus(masterObj.getApplicationStatus());
					WorkFlowDetails wf = customerDataFields.getWorkFlow();
					logger.debug("customerDataFields.getWorkFlow() :" + customerDataFields.getWorkFlow().toString());
					wf.setRemarks(customerDataFields.getRemarks());
					reqFields.setWorkflow(wf);
					req.setRequestObj(reqFields);
					logger.debug("req :" + req.toString());
					this.commonCoreService.populateApplnWorkFlow(req);
					return Mono.just(getSuccessJson(Constants.LOAN_CREATION_SUCCESS + t24LoanId));
				} else if (apiResp.has(Constants.ERROR1)) {
					saveLog(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE, this.loanService.getRequestLog(),
							loanResponse.toString(), ResponseCodes.FAILURE.getValue(), null, currentStage);
					return Mono.just(getFailureApiJson(Constants.LOAN_CREATION_SUCCESS + t24LoanId
							+ ", but we're experiencing a temporary issue with fetching the repayment details. Please try again after sometime",
							Constants.LOAN_CREATION));
				} else {
					logger.error("loanRepaySchedule API failed: Empty or missing error details.");
					return Mono.just(getFailureApiJson(Constants.LOAN_CREATION_SUCCESS + t24LoanId
							+ ", but we're experiencing a temporary issue with fetching the repayment details. Please try again after sometime",
							Constants.LOAN_CREATION));
				}
			} catch (Exception e) {
				saveLog(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE, this.loanService.getRequestLog(),
						loanResponse.toString(), ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
				logger.error("Unexpected error during loan response processing", e);
				return Mono.just(getFailureApiJson(
						"Loan Created Successfully, but we're experiencing a temporary issue with fetching the repayment details. Please try again after sometime",
						Constants.LOAN_CREATION));
			}
		});
	}

	@PostConstruct
    public void init() {
        taskScheduler.initialize();
		String cron = "0 30 23 * * ?";   // fallback if properties unavailable at startup
		try {
			String fromProps = CommonUtils.readPropertyFile()
					.getProperty(CobFlagsProperties.DMS_CRON_TIME.getKey());
			if (org.apache.commons.lang3.StringUtils.isNotBlank(fromProps)) {
				cron = fromProps;
			}
		} catch (IOException e) {
			logger.warn("Could not read dmsCronTime at startup, using default {}", cron, e);
		}
		schedulerDMSCheck(cron);

		//Author : Chandas Patel CI
		// ImageIO buffers reads/writes via temp files in java.io.tmpdir by default.
		// A full /tmp caused "Can't create cache file" failures in DMS PDF merge (Jul 2026 incident).
		// In-memory caching avoids the temp files entirely. JVM-global setting. Do not remove.
		ImageIO.setUseCache(false);
    }
	@PreDestroy
	public void shutdownScheduler() {
		if (scheduledTask != null) {
			scheduledTask.cancel(false);
		}
		taskScheduler.shutdown();
	}

	 @Scheduled(cron = "0 */4 * * * ?")
	public void checkPropertyChange() {
		try {
			Properties prop = CommonUtils.readPropertyFile();
			String newCronExpression = prop.getProperty(CobFlagsProperties.DMS_CRON_TIME.getKey());
			if (newCronExpression != null) {
				schedulerDMSCheck(newCronExpression);

			}
		} catch (IOException e) {
			logger.error("Error while reading property file in checkPropertyChange", e);
		}
	}

	private void schedulerDMSCheck(String newCronExpression) {
		if (newCronExpression == null || newCronExpression.isEmpty()) {
			logger.warn("Invalid cron expression received, skipping scheduling.");
			return;
		}

		if (newCronExpression.equals(currentCronExpression)) {
			logger.info("Cron expression remains unchanged: {}. Skipping reschedule.", newCronExpression);
			return;
		}

		logger.info("Updating DMS Scheduler. Old Cron: {}, New Cron: {}", currentCronExpression, newCronExpression);

		if (scheduledTask != null) {
			scheduledTask.cancel(false);
			logger.info("Previous DMS Scheduler task cancelled.");
		}

		currentCronExpression = newCronExpression;

		CronTrigger cronTrigger = new CronTrigger(newCronExpression);
		scheduledTask = taskScheduler.schedule(this::dmsSchedulerCheck, cronTrigger);

		logger.info("DMS Scheduler updated with new cron expression: {}", newCronExpression);
	}

	private void dmsSchedulerCheck() {
		logger.info("DMS Scheduler check started");
		try {
			Properties prop = CommonUtils.readPropertyFile();
			String dmsSchedulerFlag = prop.getProperty(CobFlagsProperties.DMS_SCHEDULER_FLAG.getKey());

			if ("Y".equalsIgnoreCase(dmsSchedulerFlag)) {
				Header headers = new Header();
				dmsSchedulerProcess(headers, prop);
			} else {
				logger.info("DMS Scheduler check is disabled");
			}
		} catch (NumberFormatException e) {
			logger.error("Invalid expiry limit value, defaulting to a safe limit", e);
			return;
		} catch (IOException e) {
			logger.error("Error while reading property file in DMS SchedulerCheck", e);
		}
	}

	public void dmsSchedulerProcess(Header header, Properties prop) {
		try {
			logger.debug("DMS Scheduler started");

			List<ApiExecutionLog> recordsToBeProcessed = logRepository.findAllByApiStatusAndApiName(Constants.SUCCESS,
					Constants.LOAN_DISBURSEMENT);
			String dmsEndTimeStr = prop.getProperty(CobFlagsProperties.DMS_END_TIME.getKey());
			LocalDateTime start = LocalDateTime.now();
			LocalDateTime endDateTime;

			try {
				LocalTime endTime = LocalTime.parse(dmsEndTimeStr);
				endDateTime = endTime.isBefore(start.toLocalTime())
						? LocalDateTime.of(start.toLocalDate().plusDays(1), endTime)
						: LocalDateTime.of(start.toLocalDate(), endTime);
			} catch (DateTimeParseException | NullPointerException e) {
				endDateTime = start.plusHours(6);
				logger.warn("Invalid or missing dmsEndTime '{}', defaulting cutoff to {}", dmsEndTimeStr, endDateTime);
			}

			if (!recordsToBeProcessed.isEmpty()) {
				AtomicBoolean stopProcessing = new AtomicBoolean(false);
				LocalDateTime finalEndDateTime = endDateTime;
				Flux.fromIterable(recordsToBeProcessed)
						.takeWhile(record -> !stopProcessing.get()) // stop processing if flag is set
						.concatMap(record -> {
							String applicationId = record.getApplicationId();

							return Mono
									.fromCallable(
											() -> applicationMasterRepository.findByAppIdAndApplicationIdAndVersionNum(
													Constants.APPID, applicationId, Constants.INITIAL_VERSION_NO))
									.subscribeOn(Schedulers.boundedElastic())
									.flatMap(optionalMaster -> optionalMaster
											.map(master -> executeDMSActivity(master, header, prop)
													.then(Mono.fromRunnable(() -> {
														logRepository.updateStatusAfterDMSPush("Success-DMS",
																applicationId);
														logger.debug("Completed DMS push for appId={}", applicationId);
														if (LocalDateTime.now().isAfter(finalEndDateTime)) {
															logger.info(
																	"End time {} reached, stopping further processing",
																	finalEndDateTime.toString());
															stopProcessing.set(true);
														}
													}).subscribeOn(Schedulers.boundedElastic())))
											.orElseGet(() -> {
												logger.warn("No ApplicationMaster found for applicationId={}",
														applicationId);
												return Mono.empty();
											}))
									.doOnError(
											e -> logger.error("Error processing applicationId={} -> {}", applicationId,
													e.getClass().getSimpleName() + ": " + e.getMessage(), e))
									.onErrorResume(e -> Mono.empty()); // skip update, move to next record
						}, 1)
						.doOnComplete(() -> logger.info("DMSScheduler Scheduler completed at {}",
								LocalDateTime.now().toString()))
						.blockLast();
			}

		} catch (Exception e) {
			logger.error("Error occurred in DMSSchedular::DMSScheduler ", e);
		}
	}

	public Mono<Object> executeDMSActivity(ApplicationMaster masterObj, Header header, Properties prop) {
		logger.debug("Executing DMS API");
		Mono<Object> connectCabinet = this.loanService.toConnectCabinet(masterObj.getApplicationId(), prop,
				masterObj.getAppId(), header);
		AtomicReference<String> userDBIdRef = new AtomicReference<>("");
		return connectCabinet.flatMap(response -> {
			logger.debug("Connect Cabinet API response {}", response);
			try {
				String json = new Gson().toJson(response);
				JSONObject apiResp = convertResponseToJson(json);
				JSONObject connectCabinetOutput = Optional.ofNullable(apiResp.optJSONObject("NGOExecuteAPIResponseBDO"))
						.map(obj -> obj.optJSONObject("outputData"))
						.map(obj -> obj.optJSONObject("NGOConnectCabinet_Output"))
						.orElse(null);
				JSONObject folderOutput = Optional.ofNullable(apiResp.optJSONObject("NGOExecuteAPIResponseBDO"))
						.map(obj -> obj.optJSONObject("outputData"))
						.map(obj -> obj.optJSONObject("NGOConnectCabinet_Output"))
						.map(obj -> obj.optJSONObject("Folders"))
						.map(obj -> obj.optJSONObject("Folder"))
						.orElse(null);
				String userDBId = (connectCabinetOutput != null) ? connectCabinetOutput.optString("UserDBId", "") : "";
				userDBIdRef.set(userDBId);
				if (userDBId.isEmpty()) {
					return Mono.error(new RuntimeException(getFailureJson("Session Id is empty").toString()));
				}
				if (folderOutput != null) {
					Folder folder = new Folder();
					folder.setCreationDateTime(folderOutput.optString("CreationDateTime", ""));
					folder.setImageVolumeIndex(folderOutput.optString("ImageVolumeIndex", ""));
					folder.setFolderType(folderOutput.optString("FolderType", ""));
					folder.setLocation(folderOutput.optString("Location", ""));
					folder.setComment(folderOutput.optString("Comment", ""));
					folder.setOwner(folderOutput.optString("Owner", ""));
					folder.setAccessType(folderOutput.optString("AccessType", ""));
					return this.loanService.toAddFolder(masterObj, prop,
							header, userDBId, folder).flatMap(folderResp -> {

								logger.debug("Add Folder API response {}", folderResp);
								String folderJson = new Gson().toJson(folderResp);
								JSONObject folderApiResp = null;
								try {
									folderApiResp = convertResponseToJson(folderJson);
								} catch (JsonProcessingException e) {
									throw new RuntimeException(e);
								}
								String folderIndex = Optional
										.ofNullable(folderApiResp.optJSONObject("NGOExecuteAPIResponseBDO"))
										.map(obj -> obj.optJSONObject("outputData"))
										.map(obj -> obj.optJSONObject("NGOAddFolder_Output"))
										.map(obj -> obj.optJSONObject("Folder"))
										.map(obj -> obj.optString("FolderIndex", ""))
										.orElse("");
								if (folderIndex.isEmpty()) {
									return Mono.error(
											new RuntimeException(getFailureJson("Invalid folderIndex Id").toString()));
								}
								return this.loanService.toAddDocument(masterObj, prop, folderIndex, userDBId)
										.map(addDocResp -> {
											logger.debug("Add Document API response {}", addDocResp);
											return (Object) getSuccessJson("DMS Process Completed Successfully");
										});
							});
				}
			} catch (Exception e) {
				logger.error("Unexpected error during DMS processing", e);
				return Mono.error(new RuntimeException(
						getFailureJson("Exception during DMS processing: " + e.getMessage()).toString()));
			}
			return Mono.error(new RuntimeException(getFailureJson("Exception during DMS processing").toString()));
		}).doFinally(sig -> {
			String userDBId = userDBIdRef.get();
			if (!userDBId.isEmpty()) {
				this.loanService.toDisconnectCabinet(
						masterObj.getApplicationId(), prop, masterObj.getAppId(), header, userDBId)
						.doOnNext(r -> logger.debug("Disconnect Cabinet API response {}", r))
						.doOnError(e -> logger.error("Error during Disconnect Cabinet API: ", e))
						.onErrorResume(e -> Mono.empty())
						.subscribe();
			}
		});
	}

	private Mono<Object> initiateDisbursementRepaySchedule(ApplicationMaster masterObj, String applicationId,
			Header header, Properties prop, String currentStage, FetchDeleteUserFields customerDataFields,
			PopulateapplnWFRequestFields reqFields, PopulateapplnWFRequest req) {
		logger.debug("Executing loanRepaySchedule API");
		Mono<Object> loanRepaySchd = this.loanService.disbRepaySchedule(masterObj.getApplicationId(), prop,
				masterObj.getAppId(), header);
		return loanRepaySchd.flatMap(loanResponse -> {
			logger.debug("loanRepaySchedule API response {}", loanResponse);
			try {
				String json = (new Gson()).toJson(loanResponse);
				JSONObject apiResp = convertResponseToJson(json);
				if (apiResp.has("body")) {
					// Saving log
					saveLog(applicationId, Constants.DISBURSEMENT_REPAY_SCHEDULE, this.loanService.getRequestLog(),
							loanResponse.toString(), ResponseCodes.SUCCESS.getValue(), null, currentStage);
					masterObj.setRemarks(customerDataFields.getRemarks());
					masterObj.setUpdatedBy(customerDataFields.getUserId());
					masterObj.setAssignedTo(null);

					cobService.updateStatus(masterObj, customerDataFields.getStatus());

					logger.debug("Updated application status to: {}", customerDataFields.getStatus());

					reqFields.setAppId(masterObj.getAppId());
					reqFields.setApplicationId(masterObj.getApplicationId());
					reqFields.setCreatedBy(customerDataFields.getUserId());
					reqFields.setVersionNum(masterObj.getVersionNum());
					reqFields.setApplicationStatus(masterObj.getApplicationStatus());

					WorkFlowDetails wf = customerDataFields.getWorkFlow();
					wf.setRemarks(customerDataFields.getRemarks());
					reqFields.setWorkflow(wf);

					req.setRequestObj(reqFields);
					logger.debug("PopulateapplnWFRequest: {}", req.toString());

					commonCoreService.populateApplnWorkFlow(req);
					try {
						sendSanctionSms(applicationId, masterObj.getAppId(), prop, true);
					} catch (Exception e) {
						logger.error("Error sending disbursement SMS: ", e);
					}
					return Mono.just(getSuccessJson("Loan Disbursed Successfully"));
				} else if (apiResp.has(Constants.ERROR1)) {
					saveLog(applicationId, Constants.DISBURSEMENT_REPAY_SCHEDULE, this.loanService.getRequestLog(),
							loanResponse.toString(), ResponseCodes.FAILURE.getValue(), loanResponse.toString(),
							currentStage);

					// return executeDMSActivity(masterObj, header, prop);
					return Mono.just(getFailureApiJson(Constants.LOAN_DISBURSE_SUCCESS
							+ ", but we're experiencing a temporary issue with fetching the repayment details. Please try again after sometime",
							Constants.LOAN_DISBURSEMENT));
				}
				logger.error("loanRepaySchedule API failed: Empty or missing error details.");
				return Mono.just(getFailureApiJson(Constants.LOAN_DISBURSE_SUCCESS
						+ ", but we're experiencing a temporary issue with fetching the repayment details. Please try again after sometime",
						Constants.LOAN_DISBURSEMENT));
			} catch (Exception e) {
				saveLog(applicationId, Constants.DISBURSEMENT_REPAY_SCHEDULE, this.loanService.getRequestLog(),
						loanResponse.toString(), ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
				logger.error("Unexpected error during loan response processing", e);
				return Mono.just(getFailureApiJson(Constants.LOAN_DISBURSE_SUCCESS
						+ ", but we're experiencing a temporary issue with fetching the repayment details. Please try again after sometime",
						Constants.LOAN_DISBURSEMENT));
			}
		});
	}

	/**
	 * Convert response to JSON body
	 */
	private JSONObject convertResponseToJson(String json) throws JsonProcessingException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> raw = (Map<String, Object>) mapper.readValue(json, Map.class);
		Object cleaned = cleanUp(raw);
		String cleanJson = mapper.writeValueAsString(cleaned);
		JSONObject apiResp = new JSONObject(cleanJson);
		logger.debug("convertResponseToJson full response 2 --> {}", apiResp);
		return apiResp;
	}

	private Mono<Object> initiateLoanDisbursement(ApplicationMaster masterObj, Header header,
			Properties prop, FetchDeleteUserFields customerDataFields, PopulateapplnWFRequest req,
			PopulateapplnWFRequestFields reqFields, String currentStage) {

		logger.debug("Executing loan disbursement API");

		Mono<Object> loanDisb = loanService.loanDisbursement(masterObj.getApplicationId(), prop,
				masterObj.getAppId(), header);

		return loanDisb.flatMap(loanResponse -> {
			try {
				logger.debug("Loan Disbursement API response: {}", loanResponse);
				String json = (loanResponse instanceof String) ? (String) loanResponse
						: new Gson().toJson(loanResponse);
				JSONObject apiResp = convertResponseToJson(json);

				if (apiResp.has(Constants.HEADER)) {
					JSONObject headerObj = apiResp.getJSONObject(Constants.HEADER);
					String status = headerObj.optString(Constants.STATUS);

					boolean isBusinessAcceptableError = false;

					if (apiResp.has(Constants.ERROR1)) {
						JSONObject errorObj = apiResp.getJSONObject(Constants.ERROR1);

						if (errorObj.has(Constants.ERROR_DETAILS)) {
							JSONArray errorDetails = errorObj.getJSONArray(Constants.ERROR_DETAILS);

							for (int i = 0; i < errorDetails.length(); i++) {
								JSONObject err = errorDetails.getJSONObject(i);

								String code = err.optString("code");
								String message = err.optString(Constants.MESSAGE);
								String allowedDisbError = prop
										.getProperty(CobFlagsProperties.NONBLOCKING_DISBURSEMENT_ERROR.getKey());
								logger.debug("nonBlockingDisbursementError :" + allowedDisbError);
								String[] errorDetail = allowedDisbError.split("/", 2);
								if (errorDetail[0].equalsIgnoreCase(code)
										&& message.contains(errorDetail[1])) {
									isBusinessAcceptableError = true;
									break;
								}
							}
						}
					}

					if (ResponseCodes.SUCCESS.getValue().equalsIgnoreCase(status) || isBusinessAcceptableError) {

						saveLog(masterObj.getApplicationId(), Constants.LOAN_DISBURSEMENT,
								this.loanService.getRequestLog(),
								loanResponse.toString(), ResponseCodes.SUCCESS.getValue(), null, currentStage);

						// Commented for testing purpose - 12/08
						// return executeDMSActivity(masterObj, header, prop);
						return initiateDisbursementRepaySchedule(masterObj, masterObj.getApplicationId(), header, prop,
								currentStage, customerDataFields, reqFields, req);
					}

					if (apiResp.has(Constants.ERROR1) && !apiResp.getJSONObject(Constants.ERROR1).isEmpty()) {
						List<String> loanErrorList = extractErrorMessages(apiResp.getJSONObject(Constants.ERROR1));
						// If empty resp recieved returning service down error
						if (loanResponse instanceof JSONArray && ((JSONArray) loanResponse).isEmpty()) {
							logger.error("Empty response received from Disb api.");
							saveLog(masterObj.getApplicationId(), Constants.LOAN_DISBURSEMENT,
									this.loanService.getRequestLog(),
									loanResponse.toString(), ResponseCodes.FAILURE.getValue(), Constants.SERVICE_DOWN,
									currentStage);
							return Mono.just(getFailureApiJson(Constants.SERVICE_DOWN, Constants.LOAN_DISBURSEMENT));
						}

						saveLog(masterObj.getApplicationId(), Constants.LOAN_DISBURSEMENT,
								this.loanService.getRequestLog(),
								loanResponse.toString(), ResponseCodes.FAILURE.getValue(), loanErrorList.toString(),
								currentStage);
						return Mono.just(getFailureApiJson(loanErrorList.toString(), Constants.LOAN_DISBURSEMENT));
					}
				}
				saveLog(masterObj.getApplicationId(), Constants.LOAN_DISBURSEMENT, this.loanService.getRequestLog(),
						loanResponse.toString(), ResponseCodes.FAILURE.getValue(), Constants.SERVICE_DOWN,
						currentStage);
				logger.error("Loan disbursement API failed: Unhandled or empty response.");
				return Mono.just(getFailureApiJson(Constants.SERVICE_DOWN, Constants.LOAN_DISBURSEMENT));
			} catch (Exception e) {
				logger.error("Unexpected error during loan disbursement processing", e);

				saveLog(masterObj.getApplicationId(), Constants.LOAN_DISBURSEMENT, this.loanService.getRequestLog(),
						null,
						ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);

				return Mono.just(getFailureApiJson(e.toString(), Constants.LOAN_DISBURSEMENT));
			}
		});
	}

	public Object cleanUp(Object input) {
		if (input instanceof Map) {
			Map<String, Object> original = (Map<String, Object>) input;
			Map<String, Object> cleaned = new LinkedHashMap<>();
			for (Map.Entry<String, Object> entry : original.entrySet()) {
				String key = entry.getKey();
				Object value = cleanUp(entry.getValue());

				if ("map".equals(key) && value instanceof Map) {
					cleaned.putAll((Map) value);
				} else if ("myArrayList".equals(key) && value instanceof List) {
					return value;
				} else {
					cleaned.put(key, value);
				}
			}
			return cleaned;
		} else if (input instanceof List) {
			List<Object> originalList = (List<Object>) input;
			List<Object> cleanedList = new ArrayList<>();
			for (Object item : originalList) {
				cleanedList.add(cleanUp(item));
			}
			return cleanedList;
		} else {
			return input;
		}
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "dbkitApplicationMovementFallback")
	public Mono<Response> dbkitApplicationMovement(FetchDeleteUserRequest fetchDeleteUserRequest,
			Properties prop, String roleId) {
		logger.info("Starting dbkitApplicationMovement for applicationId: {}",
				fetchDeleteUserRequest.getRequestObj().getApplicationId());
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		response.setResponseHeader(responseHeader);
		FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest.getRequestObj();
		String status = customerDataFields.getStatus();
		String action = customerDataFields.getWorkFlow().getAction();
		CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
		logger.debug("Received status: {}", status);
		if (AppStatus.REJECTED.getValue().equalsIgnoreCase(status)
				|| AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status)) {
			logger.debug("Loan Rejection API");
			loanService.loanRejection(fetchDeleteUserRequest.getRequestObj().getApplicationId(), prop,
					fetchDeleteUserRequest.getRequestObj().getAppId(), null);

		}
		if ((AppStatus.REJECTED.getValue().equalsIgnoreCase(status)
				|| AppStatus.RPCVERIFIED.getValue().equalsIgnoreCase(status)
				|| AppStatus.DBKITGENERATED.getValue().equalsIgnoreCase(status)
				|| AppStatus.RPCBANKUPDATE.getValue().equalsIgnoreCase(status)
				|| AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status)
				|| AppStatus.CACOMPLETED.getValue().equalsIgnoreCase(status)
				|| AppStatus.RESANCTION.getValue().equalsIgnoreCase(status))) {
			List<String> applnStatus = new ArrayList<>();
			applnStatus.add(AppStatus.SANCTIONED.getValue());
			applnStatus.add(AppStatus.DBPUSHBACK.getValue());
			logger.debug("Application statuses to check: {}", applnStatus);

			Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
					.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
							customerDataFields.getApplicationId(), customerDataFields.getVersionNum(), applnStatus);
			if (masterObjDb.isPresent()) {
				logger.info("ApplicationMaster found for applicationId: {}", customerDataFields.getApplicationId());
				Gson gson = new Gson();
				ApplicationMaster masterObj = masterObjDb.get();
				masterObj.setRemarks(null);
				masterObj.setUpdatedBy(customerDataFields.getUserId());
				masterObj.setAssignedTo(null);
				cobService.updateStatus(masterObj, status);
				logger.debug("Updated application status to: {}", status);
				if (action.equalsIgnoreCase(Constants.REJECT) || action.equalsIgnoreCase(Constants.DBSANCTIONPUSHBACK)
						|| action.equalsIgnoreCase(Constants.DBCAPUSHBACK)
						|| action.equalsIgnoreCase(Constants.RESANCTION)) {
					cobService.handleDeleteAllDocuments(prop, fetchDeleteUserRequest.getRequestObj().getAppId(),
							fetchDeleteUserRequest.getRequestObj().getApplicationId());
					int deletedEnachRecords = enachRepository
							.deleteByApplicationId(fetchDeleteUserRequest.getRequestObj().getApplicationId());
					if (deletedEnachRecords == 0) {
						logger.debug("No ENACH records found for given Application ID : {}",
								fetchDeleteUserRequest.getRequestObj().getApplicationId());
					}
					int deletedUdhyamRecords = udhyamRepository
							.deleteByApplicationId(fetchDeleteUserRequest.getRequestObj().getApplicationId());
					if (deletedUdhyamRecords == 0) {
						logger.debug("No Udhyam records found for given Application ID : {}",
								fetchDeleteUserRequest.getRequestObj().getApplicationId());
					}

				}
				logger.debug("customerDataFields: {}", customerDataFields.toString());
				PopulateapplnWFRequest req = new PopulateapplnWFRequest();
				PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
				reqFields.setAppId(masterObj.getAppId());
				reqFields.setApplicationId(masterObj.getApplicationId());
				reqFields.setCreatedBy(customerDataFields.getUserId());
				reqFields.setVersionNum(masterObj.getVersionNum());
				reqFields.setApplicationStatus(masterObj.getApplicationStatus());
				WorkFlowDetails wf = customerDataFields.getWorkFlow();
				logger.debug("WorkFlowDetails: {}", wf.toString());
				wf.setRemarks(customerDataFields.getRemarks());
				reqFields.setWorkflow(wf);
				if ((AppStatus.REJECTED.getValue().equalsIgnoreCase(status)
						|| AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status)
						|| AppStatus.RESANCTION.getValue().equalsIgnoreCase(status)
						|| AppStatus.RPCVERIFIED.getValue().equalsIgnoreCase(status))) {
					Optional<DBKITStageVerification> verificationData = dbkitStageVerificationRepository
							.findById(customerDataFields.getApplicationId());
					if (verificationData.isPresent()) {
						DBKITStageVerification data = verificationData.get();
						if (StringUtils.isEmpty(customerDataFields.getRemarks())) {
							wf.setRemarks(data.getQueries());
						}
						data.setVerifiedStages(null);
						data.setReuploadedDocs(null);
						dbkitStageVerificationRepository.save(data);
					}
					List<ApplicationDocuments> documents = applicationDocumentsRepository
							.findByApplicationIdAndAppId(masterObj.getApplicationId(), masterObj.getAppId());
					String[] enachUdhyamDocTypes = Constants.ENACH_UDHYAM_DOC_TYPE.split(",");
					Set<String> docTypeSet = new HashSet<>(Arrays.asList(enachUdhyamDocTypes));
					if (!documents.isEmpty()) {
						List<ApplicationDocuments> modifiedDocs = new ArrayList<>();

						for (ApplicationDocuments appDoc : documents) {
							ApplicationDocumentsPayload appDocPayload = gson.fromJson(
									appDoc.getPayloadColumn(), ApplicationDocumentsPayload.class);
							logger.debug("Processing document: {}",
									appDocPayload != null ? appDocPayload.getDocumentType() : "null");
							// Delete matching documents first
							boolean isDeleteAction = action.equalsIgnoreCase(Constants.REJECT)
									|| action.equalsIgnoreCase(Constants.DBSANCTIONPUSHBACK)
									|| action.equalsIgnoreCase(Constants.DBCAPUSHBACK)
									|| action.equalsIgnoreCase(Constants.RESANCTION);
							if (isDeleteAction && appDocPayload != null
									&& docTypeSet.contains(appDocPayload.getDocumentType())) {
								logger.debug("Deleting document: {}", appDocPayload.getDocumentType());
								applicationDocumentsRepository.delete(appDoc);
								continue;
							}
							// Modify only non-deleted documents
							if (appDocPayload != null && appDocPayload.getIsReupload() != null) {
								appDocPayload.setIsReupload("N");
								appDoc.setPayloadColumn(gson.toJson(appDocPayload));
								modifiedDocs.add(appDoc);
							}
						}
						if (!modifiedDocs.isEmpty()) {
							applicationDocumentsRepository.saveAll(modifiedDocs);
						}
					}
				}
				Optional<DBKITStageVerification> verificationData = dbkitStageVerificationRepository
						.findById(customerDataFields.getApplicationId());
				if (verificationData.isPresent()) {
					DBKITStageVerification data = verificationData.get();
					data.setVerifiedStages(null);
					dbkitStageVerificationRepository.save(data);
				}
				req.setRequestObj(reqFields);
				logger.debug("PopulateapplnWFRequest: {}", req.toString());
				commonCoreService.populateApplnWorkFlow(req);
				Optional<BankDetails> bankDetailsOpt = bankDetailsRepository
						.findBankDetailsByCustomerType(Constants.COAPPLICANT, customerDataFields.getApplicationId());
				if (bankDetailsOpt.isPresent()) {
					BankDetails bankDetails = bankDetailsOpt.get();
					BankDetailsPayload payload = new Gson().fromJson(bankDetails.getPayloadColumn(),
							BankDetailsPayload.class);
					payload.setRpcEditCheck(false);
					String payloadString = new Gson().toJson(payload);
					bankDetails.setPayloadColumn(payloadString);
					bankDetailsRepository.save(bankDetails);
				}
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				customerIdentification.setVersionNum(customerDataFields.getVersionNum());
				responseBody.setResponseObj(gson.toJson(customerIdentification));
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
				logger.info("Application status update completed successfully for applicationId: {}",
						customerDataFields.getApplicationId());
			} else {
				logger.warn("ApplicationMaster not found for applicationId: {}", customerDataFields.getApplicationId());
				responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
			}
		} else {
			logger.warn("Invalid status received: {}", status);
			responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
		}
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		logger.info("Exiting dbkitApplicationMovement for applicationId: {}",
				fetchDeleteUserRequest.getRequestObj().getApplicationId());
		return Mono.just(response);
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "dbkitVerificationApplicationMovementFallback")
	public Mono<Response> dbkitVerificationApplicationMovement(FetchDeleteUserRequest fetchDeleteUserRequest,
			Properties prop, String roleId) {
		logger.info("Starting dbkitVerificationApplicationMovement for applicationId: {}",
				fetchDeleteUserRequest.getRequestObj().getApplicationId());
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		response.setResponseHeader(responseHeader);
		FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest.getRequestObj();
		String status = customerDataFields.getStatus();
		String action = customerDataFields.getWorkFlow().getAction();
		CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
		logger.debug("Received status: {}", status);

		if ((AppStatus.REJECTED.getValue().equalsIgnoreCase(status)
				|| AppStatus.SANCTIONED.getValue().equalsIgnoreCase(status)
				|| AppStatus.DBKITVERIFIED.getValue().equalsIgnoreCase(status)
				|| AppStatus.DBPUSHBACK.getValue().equalsIgnoreCase(status)
				|| AppStatus.RESANCTION.getValue().equalsIgnoreCase(status))) {
			List<String> applnStatus = new ArrayList<>();
			applnStatus.add(AppStatus.DBKITGENERATED.getValue());
			logger.debug("Application statuses to check: {}", applnStatus);

			Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
					.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
							customerDataFields.getApplicationId(), customerDataFields.getVersionNum(), applnStatus);
			if (masterObjDb.isPresent()) {
				logger.info("ApplicationMaster found for applicationId: {}", customerDataFields.getApplicationId());
				Gson gson = new Gson();
				ApplicationMaster masterObj = masterObjDb.get();
				masterObj.setRemarks(null);
				masterObj.setUpdatedBy(customerDataFields.getUserId());
				masterObj.setAssignedTo(null);
				cobService.updateStatus(masterObj, status);
				logger.debug("Updated application status to: {}", status);
				if (action.equalsIgnoreCase(Constants.REJECT) || action.equalsIgnoreCase(Constants.RESANCTION)) {
					cobService.handleDeleteAllDocuments(prop, fetchDeleteUserRequest.getRequestObj().getAppId(),
							fetchDeleteUserRequest.getRequestObj().getApplicationId());
					int deletedEnachRecords = enachRepository
							.deleteByApplicationId(fetchDeleteUserRequest.getRequestObj().getApplicationId());
					if (deletedEnachRecords == 0) {
						logger.debug("No ENACH records found for given Application ID : {}",
								fetchDeleteUserRequest.getRequestObj().getApplicationId());
					}
					int deletedUdhyamRecords = udhyamRepository
							.deleteByApplicationId(fetchDeleteUserRequest.getRequestObj().getApplicationId());
					if (deletedUdhyamRecords == 0) {
						logger.debug("No Udhyam records found for given Application ID : {}",
								fetchDeleteUserRequest.getRequestObj().getApplicationId());
					}
					Optional<DBKITStageVerification> dbkitStageVerification = dbkitStageVerificationRepository
							.findById(customerDataFields.getApplicationId());
					if (dbkitStageVerification.isPresent()) {
						DBKITStageVerification data = dbkitStageVerification.get();
						data.setVerifiedStages(null);
						data.setReuploadedDocs(null);
						dbkitStageVerificationRepository.save(data);

					}

				}
				logger.debug("customerDataFields: {}", customerDataFields.toString());
				PopulateapplnWFRequest req = new PopulateapplnWFRequest();
				PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
				reqFields.setAppId(masterObj.getAppId());
				reqFields.setApplicationId(masterObj.getApplicationId());
				reqFields.setCreatedBy(customerDataFields.getUserId());
				reqFields.setVersionNum(masterObj.getVersionNum());
				reqFields.setApplicationStatus(masterObj.getApplicationStatus());
				WorkFlowDetails wf = customerDataFields.getWorkFlow();
				logger.debug("WorkFlowDetails: {}", wf.toString());
				cobService.updateStatus(masterObj, wf.getNextWorkflowStatus());
				logger.debug("Updated application status to: {}", status);
				wf.setRemarks(customerDataFields.getRemarks());
				if (AppStatus.DBPUSHBACK.getValue().equalsIgnoreCase(status)
						|| AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status)) {
					Optional<DBKITStageVerification> dbkitStageVerification = dbkitStageVerificationRepository
							.findById(customerDataFields.getApplicationId());
					if (dbkitStageVerification.isPresent()) {
						DBKITStageVerification data = dbkitStageVerification.get();
						wf.setRemarks(data.getQueries());
						data.setVerifiedStages(null);
						dbkitStageVerificationRepository.save(data);

					}
					Optional<List<Documents>> documentsOpt = documentsRepository
							.findByApplicationId(masterObj.getApplicationId());
					if (documentsOpt.isPresent() && !documentsOpt.get().isEmpty()) {
						for (Documents doc : documentsOpt.get()) {
							doc.setQueryResponse("N");
							documentsRepository.save(doc);
						}
					}
					List<ApplicationDocuments> documents = applicationDocumentsRepository
							.findByApplicationIdAndAppId(masterObj.getApplicationId(), masterObj.getAppId());

					if (!documents.isEmpty()) {
						List<ApplicationDocuments> modifiedDocs = new ArrayList<>();

						for (ApplicationDocuments appDoc : documents) {
							ApplicationDocumentsPayload appDocPayload = gson.fromJson(
									appDoc.getPayloadColumn(), ApplicationDocumentsPayload.class);

							if (appDocPayload != null && appDocPayload.getIsReupload() != null) {
								appDocPayload.setIsReupload("N");
								appDoc.setPayloadColumn(gson.toJson(appDocPayload));
								modifiedDocs.add(appDoc); // Only add modified ones
							}
						}

						if (!modifiedDocs.isEmpty()) {
							applicationDocumentsRepository.saveAll(modifiedDocs); // Save only modified entries
						}
					}

				}
				reqFields.setWorkflow(wf);
				req.setRequestObj(reqFields);
				logger.debug("PopulateapplnWFRequest: {}", req.toString());
				commonCoreService.populateApplnWorkFlow(req);
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				customerIdentification.setVersionNum(customerDataFields.getVersionNum());
				responseBody.setResponseObj(gson.toJson(customerIdentification));
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
				logger.info("Application status update completed successfully for applicationId: {}",
						customerDataFields.getApplicationId());
			} else {
				logger.warn("ApplicationMaster not found for applicationId: {}", customerDataFields.getApplicationId());
				responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
			}
		} else {
			logger.warn("Invalid status received: {}", status);
			responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
		}
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		logger.info("Exiting dbkitVerificationApplicationMovement for applicationId: {}",
				fetchDeleteUserRequest.getRequestObj().getApplicationId());
		return Mono.just(response);
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "disbursementApplicationMovementFallback")
	public Mono<Object> disbursementApplicationMovement(FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
			Properties prop) {

		logger.info("Starting disbursmentApplicationMovement");
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		response.setResponseHeader(responseHeader);
		FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest.getRequestObj();
		String status = customerDataFields.getStatus();
		String action = customerDataFields.getWorkFlow().getAction();
		String applicationId = customerDataFields.getApplicationId();
		CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
		logger.debug("Received status: {} : action :{}", status, action);

		if ((AppStatus.REJECTED.getValue().equalsIgnoreCase(status)
				|| AppStatus.DISBURSED.getValue().equalsIgnoreCase(status)
				|| AppStatus.RPCVERIFIED.getValue().equalsIgnoreCase(status)
				|| AppStatus.RESANCTION.getValue().equalsIgnoreCase(status))) {
			List<String> applnStatus = new ArrayList<>();
			applnStatus.add(AppStatus.DBKITVERIFIED.getValue());
			logger.debug("Application statuses to check: {}", applnStatus);

			Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
					.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
							applicationId, customerDataFields.getVersionNum(), applnStatus);
			if (masterObjDb.isPresent()) {
				logger.info("ApplicationMaster found for applicationId: {}", applicationId);
				Gson gson = new Gson();
				ApplicationMaster masterObj = masterObjDb.get();

				PopulateapplnWFRequest req = new PopulateapplnWFRequest();
				PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
				if (action.equalsIgnoreCase(Constants.SUBMIT)) {
					// Loan Disbursement
					LoanDetails loanDetails = loanDtlsRepo.findByApplicationId(applicationId);
					if (loanDetails == null || loanDetails.getSanctionedLoanAmount() == null) {
						return Mono.just(getFailureApiJson("Sanctioned Loan Amount not found",
								"Disbursement Application Movement"));
					}

					BigDecimal sanctionedAmount = loanDetails.getSanctionedLoanAmount();
					BigDecimal bmRecommendedAmt = loanDetails.getBmRecommendedLoanAmount();
					Optional<CibilDetails> cibilOpt = cibilDetailsRepository
							.findCibilDetailsByCustomerTypeAndApplicationId(
									Constants.COAPPLICANT,
									applicationId);
					if (!cibilOpt.isPresent()) {
						return Mono.just(
								getFailureApiJson("Cibil details not present", "Disbursement Application Movement"));
					}
					CibilDetailsPayload payload = gson.fromJson(cibilOpt.get().getPayloadColumn(),
							CibilDetailsPayload.class);
					String eligibleAmtStr = payload.getEligibleAmt();
					if (eligibleAmtStr == null || StringUtils.isEmpty(eligibleAmtStr)) {
						return Mono.just(
								getFailureApiJson("BRE eligible amount missing", "Disbursement Application Movement"));
					}
					BigDecimal eligibleAmount;
					try {
						eligibleAmount = new BigDecimal(eligibleAmtStr);
					} catch (NumberFormatException e) {
						return Mono.just(
								getFailureApiJson("Invalid BRE eligible amount", "Disbursement Application Movement"));
					}
					if (eligibleAmount.compareTo(sanctionedAmount) < 0) {
						logger.info(
								"Eligible amount {} is less than sanctioned amount {} for applicationId {}",
								eligibleAmount, sanctionedAmount, applicationId);
						return Mono.just(getFailureApiJson(
								"Sanctioned amount is greater than BRE eligible amount, please verify",
								"Disbursement Application Movement"));
					}
					if (bmRecommendedAmt.compareTo(eligibleAmount) < 0) {
						logger.info(
								"Bm recommended amount {} is less than BRE eligible amount {} for applicationId {}",
								bmRecommendedAmt, eligibleAmount, applicationId);
						return Mono.just(getFailureApiJson(
								"Bm recommended amount is less than BRE eligible amount, please verify",
								"Disbursement Application Movement"));
					}
					if (bmRecommendedAmt.compareTo(sanctionedAmount) < 0) {
						logger.info(
								"BM recommended amount {} is less than sanctioned amount {} for applicationId {}",
								bmRecommendedAmt, sanctionedAmount, applicationId);
						return Mono.just(
								getFailureApiJson("Bm recommended amount is less than sanctioned amount, please verify",
										"Disbursement Application Movement"));
					}

					Mono<Object> executeLoanDisbursement = initiateLoanDisbursement(masterObj, header, prop,
							customerDataFields, req, reqFields, Constants.DISBURSEMENT_PENDING);
					return executeLoanDisbursement;
				} else {
					masterObj.setRemarks(customerDataFields.getRemarks());
					masterObj.setUpdatedBy(customerDataFields.getUserId());
					masterObj.setAssignedTo(null);
					cobService.updateStatus(masterObj, status);
					logger.debug("Updated application status to: {}", status);

					if (action.equalsIgnoreCase(Constants.REJECT) || action.equalsIgnoreCase(Constants.RESANCTION)) {
						cobService.handleDeleteAllDocuments(prop, fetchDeleteUserRequest.getRequestObj().getAppId(),
								applicationId);
						int deletedEnachRecords = enachRepository.deleteByApplicationId(applicationId);
						if (deletedEnachRecords == 0) {
							logger.debug("No ENACH records found for given Application ID : {}", applicationId);
						}
						int deletedUdhyamRecords = udhyamRepository.deleteByApplicationId(applicationId);
						if (deletedUdhyamRecords == 0) {
							logger.debug("No Udhyam records found for given Application ID : {}", applicationId);
						}
					}

					logger.debug("customerDataFields: {}", customerDataFields.toString());
					reqFields.setAppId(masterObj.getAppId());
					reqFields.setApplicationId(applicationId);
					reqFields.setCreatedBy(customerDataFields.getUserId());
					reqFields.setVersionNum(masterObj.getVersionNum());
					reqFields.setApplicationStatus(masterObj.getApplicationStatus());
					WorkFlowDetails wf = customerDataFields.getWorkFlow();
					logger.debug("WorkFlowDetails: {}", wf.toString());
					wf.setRemarks(customerDataFields.getRemarks());
					reqFields.setWorkflow(wf);
					req.setRequestObj(reqFields);
					logger.debug("PopulateapplnWFRequest: {}", req.toString());
					commonCoreService.populateApplnWorkFlow(req);
					responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
					customerIdentification.setVersionNum(customerDataFields.getVersionNum());
					responseBody.setResponseObj(gson.toJson(customerIdentification));
					response.setResponseBody(responseBody);
					response.setResponseHeader(responseHeader);
					logger.info("Application status update completed successfully for applicationId: {}",
							applicationId);
				}
			} else {
				logger.warn("ApplicationMaster not found for applicationId: {}", applicationId);
				responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
			}
		} else {
			logger.warn("Invalid status received: {}", status);
			responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
		}
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		logger.info("Exiting disbursementApplicationMovement for applicationId: {}",
				applicationId);
		return Mono.just(response);
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "stageMovementApplicationFallback")
	public Mono<Response> disbursedApplicationMovement(FetchDeleteUserRequest request, Properties prop, String roleId) {

		FetchDeleteUserFields fields = request.getRequestObj();
		String status = fields.getStatus();

		Response response = new Response();
		ResponseHeader header = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		try {

			List<String> validStatuses = Arrays.asList(AppStatus.DISBURSED.getValue(), AppStatus.LUC.getValue(),
					AppStatus.PENDINGLUCVERIFICATION.getValue(), AppStatus.LUCVERIFIED.getValue());

			if (CommonUtils.isNullOrEmpty(status) || !validStatuses.contains(status)) {
				return Mono.just(getFailureApiJson("Invalid Status", "Disbursed Application Movement"));
			}

			String action = fields.getWorkFlow().getAction();
			String currentStage = fields.getWorkFlow().getCurrentStage();
			String nextStage = null;
			switch (currentStage) {
				case "DISBURSED":
					if ("SUBMIT".equals(action))
						nextStage = "LUC";
					break;

				case "LUC":
					if ("SUBMIT".equals(action))
						nextStage = "PENDINGLUCVERIFICATION";
					break;

				case "PENDINGLUCVERIFICATION":
					if ("SUBMIT".equals(action))
						nextStage = "EXIT";
					if ("PUSHBACK".equals(action))
						nextStage = "LUC";
					break;

				case "SERVICECALL":
					if ("SUBMIT".equals(action))
						nextStage = "LUC";
					break;
				default:
					return Mono.just(getFailureApiJson("Invalid stage/action", "Disbursed Application Movement"));
			}

			if (nextStage == null) {
				return Mono.just(getFailureApiJson("Invalid stage/action", "Disbursed Application Movement"));
			}
			Optional<ApplicationMaster> masterOpt = applicationMasterRepository
					.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(fields.getAppId(),
							fields.getApplicationId(), fields.getVersionNum(), validStatuses);
			if (!masterOpt.isPresent()) {
				return Mono.just(getFailureApiJson("Invalid AppMaster", "Disbursed Application Movement"));
			}
			ApplicationMaster master = masterOpt.get();

			if (fields.getLUCRequest() != null) {
				if (master.getApplicationStatus().equalsIgnoreCase(AppStatus.LUC.getValue())) {
					response = lucService.LucUploadData(fields.getLUCRequest(), true);
					if (response.getResponseHeader().getResponseCode().equals("1")) {
						return Mono.just(response);
					}
				}
				if (currentStage.equalsIgnoreCase("PENDINGLUCVERIFICATION") && action.equalsIgnoreCase("PUSHBACK")) {
					response = lucService.LucUploadData(fields.getLUCRequest(), false);
					if (response.getResponseHeader().getResponseCode().equals("1")) {
						return Mono.just(response);
					}
				}
			}

			boolean allowUpdate = currentStage.equalsIgnoreCase("DISBURSED")
					|| currentStage.equalsIgnoreCase("PENDINGLUCVERIFICATION")
					|| (currentStage.equalsIgnoreCase("LUC") && fields.getLUCRequest().getRequestObj().getPayload()
							.getTotalUnutilisedAmount().compareTo(BigDecimal.ZERO) == 0);

			if (allowUpdate) {

				master.setRemarks(fields.getRemarks());
				master.setUpdatedBy(fields.getUserId());
				master.setAssignedTo(null);

				WorkFlowDetails wf = fields.getWorkFlow();
				wf.setRemarks(fields.getRemarks());

				if (currentStage.equalsIgnoreCase("PENDINGLUCVERIFICATION") && action.equals("PUSHBACK")) {
					master.setRemarks(null);
					wf.setRemarks(null);
				}
				cobService.updateStatus(master, status);

				PopulateapplnWFRequest req = new PopulateapplnWFRequest();
				PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();

				reqFields.setAppId(master.getAppId());
				reqFields.setApplicationId(master.getApplicationId());
				reqFields.setCreatedBy(fields.getUserId());
				reqFields.setVersionNum(master.getVersionNum());
				reqFields.setApplicationStatus(master.getApplicationStatus());
				reqFields.setWorkflow(wf);

				req.setRequestObj(reqFields);
				logger.debug("req :" + req.toString());
				commonCoreService.populateApplnWorkFlow(req);
			}

			header.setResponseCode(ResponseCodes.SUCCESS.getKey());
			response.setResponseHeader(header);
		} catch (Exception e) {
			header.setResponseCode(ResponseCodes.FAILURE.getKey());
			responseBody.setResponseObj(e.getMessage());
			response.setResponseBody(responseBody);
			response.setResponseHeader(header);
		}
		return Mono.just(response);
	}

	public JSONObject getFileContent(String fileName, String directory) {

		logger.debug("fileName :" + fileName);
		JSONObject keysForContent = new JSONObject();
		JSONObject fileContent = new JSONObject();
		try {
			fileContent = new JSONObject(adapterUtil.readJSONContentFromServer(directory + "/" + fileName));
			logger.debug("fileContent 1: " + fileContent.toString());

			keysForContent = fileContent.getJSONObject("keysForContent");
			logger.debug("fileContent 2: " + keysForContent.toString());

		} catch (IOException | JSONException e) {
			getFailureJson(e.getMessage());
		}
		logger.debug("Fetching json files 2: " + keysForContent.toString());

		return keysForContent;
	}

	public Response getFailureApiJson(String error, String apiName) {
		logger.debug("Inside getFailureJsonn");
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
		responseBody.setResponseObj(
				"{\"apiName\":\"" + apiName + "\",\"errorMessage\":\"" + error + "\", \"status\":\""
						+ ResponseCodes.FAILURE.getValue() + "\"}");
		response.setResponseHeader(responseHeader);
		response.setResponseBody(responseBody);
		logger.debug("FailureJson created" + response.toString());
		return response;
	}

	public Response getFailureJson(String error) {
		logger.debug("Inside getFailureJson");
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();

		responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
		responseBody.setResponseObj(
				"{\"errorMessage\":\"" + error + "\", \"status\":\"" + ResponseCodes.FAILURE.getValue() + "\"}");
		response.setResponseHeader(responseHeader);
		response.setResponseBody(responseBody);
		logger.debug("FailureJson created");
		return response;
	}

	public Response getSuccessJson(String baseString) {
		logger.debug("Inside getSuccessJson");
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();

		responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
		logger.debug("responseCode added to responseHeader");
		responseBody.setResponseObj(
				"{\"message\":\"" + baseString + "\", \"status\":\"" + ResponseCodes.SUCCESS.getValue() + "\"}");
		logger.debug("string added to resonseBody as responseObj");
		response.setResponseHeader(responseHeader);
		logger.debug("responseHeader added");
		response.setResponseBody(responseBody);

		logger.debug("SuccessJson created");
		return response;
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "approveRenewalApplicationFallback")
	public Mono<Response> approveRenewalApplication(
			ApplyLoanRequest req2, Header header,
			boolean isSelfOnBoardingHeaderAppId, Properties prop) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		response.setResponseHeader(responseHeader);
		ApplyLoanRequestFields customerDataFields = req2
				.getRequestObj();
		String status = AppStatus.APPROVED.getValue();
		if ((!(CommonUtils.isNullOrEmpty(status)))
				&& (AppStatus.APPROVED.getValue().equalsIgnoreCase(status)
						|| AppStatus.REJECTED.getValue().equalsIgnoreCase(status))
				|| AppStatus.PUSHBACK.getValue().equalsIgnoreCase(status)
				|| AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status)) {
			List<String> applnStatus = new ArrayList<>();
			applnStatus.add(AppStatus.PENDING.getValue());
			applnStatus.add(AppStatus.INPROGRESS.getValue());
			applnStatus.add(AppStatus.PUSHBACK.getValue());
			applnStatus.add(AppStatus.IPUSHBACK.getValue());
			/*
			 * Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
			 * .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(
			 * customerDataFields.getAppId(), customerDataFields.getApplicationId(),
			 * customerDataFields.getVersionNum(), AppStatus.PENDING.getValue());
			 */
			logger.debug("app id : {} ", customerDataFields.getAppId());
			logger.debug("application id : {} ", customerDataFields.getApplicationId());
			logger.debug("version no : {} ", customerDataFields.getVersionNum());
			logger.debug("application status : {} ", applnStatus);
			Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
					.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
							customerDataFields.getApplicationId(), customerDataFields.getVersionNum(),
							applnStatus);
			logger.debug("Getting optional master object");
			if (masterObjDb.isPresent()) {
				logger.debug("Master data value present.");
				String accNum = null;
				BigDecimal customerId = null;
				Gson gson = new Gson();
				CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
				ApplicationMaster masterObj = masterObjDb.get();
				if (AppStatus.APPROVED.getValue().equalsIgnoreCase(status)) {
					accNum = CommonUtils.generateRandomNumStr();
					customerId = CommonUtils.generateRandomNum();
					masterObj.setAccNumber(accNum);
					masterObj.setCustomerId(customerId);
					customerIdentification.setAccNumber(accNum);
					customerIdentification.setCustomerId(customerId.toString());
					customerIdentification.setApplicationId(customerDataFields.getApplicationId());

				}
				masterObj.setAssignedTo(null);
				masterObj.setCurrentStageNo(11);
				masterObj.setBranchName(customerDataFields.getApplicationMaster().getBranchName());
				masterObj.setCurrentScreenId(Constants.ACCOUNT_CREATION);
				cobService.updateStatus(masterObj, status);
				PopulateapplnWFRequest req = new PopulateapplnWFRequest();
				PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
				reqFields.setAppId(masterObj.getAppId());
				reqFields.setApplicationId(masterObj.getApplicationId());
				reqFields.setCreatedBy(req2.getUserId());
				reqFields.setVersionNum(masterObj.getVersionNum());
				reqFields.setApplicationStatus(masterObj.getApplicationStatus());
				WorkFlowDetails wf = customerDataFields.getWorkflow();
				wf.setRemarks(null);
				reqFields.setWorkflow(wf);
				req.setRequestObj(reqFields);
				commonCoreService.populateApplnWorkFlow(req);
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				customerIdentification.setVersionNum(customerDataFields.getVersionNum());
				responseBody.setResponseObj(gson.toJson(customerIdentification));
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
				logger.debug("application status update completed");

			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
			}
		} else {
			responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
		}
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return Mono.just(response);
	}

	/*
	 * This method re-initiate rejected application
	 */
	@CircuitBreaker(name = "fallback", fallbackMethod = "initiateRejectedApplicationFallback")
	public Mono<Response> initiateRejectedApplication(
			com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
			boolean isSelfOnBoardingHeaderAppId, Properties prop) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		response.setResponseHeader(responseHeader);
		com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest
				.getRequestObj();
		String status = customerDataFields.getStatus();
		if ((!(CommonUtils.isNullOrEmpty(status))) && (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(status))) {
			List<String> applnStatus = new ArrayList<>();
			applnStatus.add(AppStatus.REJECTED.getValue());
			logger.debug("app id : {} " + customerDataFields.getAppId());
			logger.debug("application id : {} " + customerDataFields.getApplicationId());
			logger.debug("version no : {} " + customerDataFields.getVersionNum());
			logger.debug("application status : {} " + applnStatus);
			WorkFlowDetails wf = customerDataFields.getWorkFlow();
			Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
					.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
							customerDataFields.getApplicationId(), customerDataFields.getVersionNum(), applnStatus);
			logger.debug("Getting optional master object");
			if (masterObjDb.isPresent()) {
				logger.debug("Master data value present.");
				Gson gson = new Gson();
				CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
				ApplicationMaster masterObj = masterObjDb.get();
				logger.debug("Master data value present." + masterObj.toString());
				logger.debug("Status : {}", status);
				if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(status)) {
					List<BankDetails> bankDetailsList = bankDetailsRepository.findByApplicationIdAndAppId(
							customerDataFields.getApplicationId(), customerDataFields.getAppId());
					if (!bankDetailsList.isEmpty()) {
						logger.debug("Bank details found: {}", bankDetailsList);
						for (BankDetails bankDetails : bankDetailsList) {
							BankDetailsPayload bankDetailsPayload = gson.fromJson(bankDetails.getPayloadColumn(),
									BankDetailsPayload.class);
							if (null != bankDetailsPayload.getAccntVerified()) {
								bankDetailsPayload.setAccntVerified("");
							}
							if (null != bankDetailsPayload.getRpcaccntVerified()) {
								bankDetailsPayload.setRpcaccntVerified("");
							}
							bankDetails.setPayloadColumn(gson.toJson(bankDetailsPayload));
							bankDetailsRepository.save(bankDetails);
						}
						logger.debug("updated bank details for applicationId : {}",
								customerDataFields.getApplicationId());
					}
					List<CibilDetails> cibilDetailsList = cibilDetailsRepository.findByApplicationIdAndAppId(
							customerDataFields.getApplicationId(), customerDataFields.getAppId());
					if (!cibilDetailsList.isEmpty()) {
						logger.debug("cibilDetailsList found: {}", bankDetailsList);
						for (CibilDetails cibilDetails : cibilDetailsList) {
							CibilDetailsPayload cibilDetailsPayload = gson.fromJson(cibilDetails.getPayloadColumn(),
									CibilDetailsPayload.class);
							cibilDetailsPayload.setRetryAttempts(Integer.parseInt(
									prop.getProperty(CobFlagsProperties.BRE_RETRY_ATTEMPT.getKey())));
							cibilDetails.setPayloadColumn(gson.toJson(cibilDetailsPayload));
							cibilDetailsRepository.save(cibilDetails);
						}
						logger.debug("updated cibilDetailsList for applicationId : {}",
								customerDataFields.getApplicationId());
					}
					if (Products.LOAN.getKey().equalsIgnoreCase(masterObj.getProductGroupCode())) {
						masterObj.setCurrentScreenId(wf.getAction());
						masterObj.setRemarks(customerDataFields.getRemarks());
						String branchId = masterObj.getBranchId();
						boolean isAnyBranchWhitelisted = whitelistedBranchesRepository
								.isAnyBranchWhitelisted(Arrays.asList(branchId));
						if (isAnyBranchWhitelisted) {
							masterObj.setDeclarationFlag(Constants.IEXCEED_FLAG);
						}
						cobService.updateStatus(masterObj, status);
					}
				}
				if (!CommonUtils.isNullOrEmpty(masterObj.getRelatedApplicationId())) {
					Optional<ApplicationMaster> appMasterRelated = applicationMasterRepository
							.findByAppIdAndApplicationIdAndVersionNum(customerDataFields.getAppId(),
									masterObj.getRelatedApplicationId(), customerDataFields.getVersionNum());
					if (appMasterRelated.isPresent()) {
						ApplicationMaster appMasterObjRelated = appMasterRelated.get();
						appMasterObjRelated.setCurrentScreenId(wf.getAction());
						appMasterObjRelated.setRemarks(customerDataFields.getRemarks());
						String branchId = appMasterObjRelated.getBranchId();
						boolean isAnyBranchWhitelisted = whitelistedBranchesRepository
								.isAnyBranchWhitelisted(Arrays.asList(branchId));
						if (isAnyBranchWhitelisted) {
							appMasterObjRelated.setDeclarationFlag(Constants.IEXCEED_FLAG);
						}
						cobService.updateStatus(appMasterObjRelated, status);

					}
				}
				PopulateapplnWFRequest req = new PopulateapplnWFRequest();
				PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
				logger.debug("Master data value present 2." + masterObj.toString());
				reqFields.setAppId(masterObj.getAppId());
				reqFields.setApplicationId(masterObj.getApplicationId());
				reqFields.setCreatedBy(customerDataFields.getUserId());
				reqFields.setVersionNum(masterObj.getVersionNum());
				reqFields.setApplicationStatus(masterObj.getApplicationStatus());
				wf.setRemarks(customerDataFields.getRemarks());
				reqFields.setWorkflow(wf);
				req.setRequestObj(reqFields);
				commonCoreService.populateApplnWorkFlow(req);
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				customerIdentification.setVersionNum(customerDataFields.getVersionNum());
				responseBody.setResponseObj(gson.toJson(customerIdentification));
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
				logger.debug("application status update completed");
			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
			}
		} else {
			responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
		}
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return Mono.just(response);
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "fetchDetailsBasedOnPinCodeFallback")
	public Response fetchDetailsBasedOnPinCode(PinCodeApiRequest pinCodeApiRequest) {
		logger.warn("Start: Fetch PinCode details from DB");
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		JSONObject pinCodeObject = new JSONObject();
		PinCodeRequestObject pinCodeRequestObject = pinCodeApiRequest.getRequestObject();
		logger.warn("PinCode : " + pinCodeRequestObject.getPinCode());
		String[] actualPinCode = pinCodeRequestObject.getPinCode().trim().split("\\.");
		Integer pincodeInt = Integer.valueOf(actualPinCode[0]);
		Optional<PinCodeDetails> pinCodeDetails = pinCodeDetailsRepository
				.findByPinCode(pincodeInt);

		if (pinCodeDetails.isPresent()) {
			logger.warn("PinCode Details present in DB");
			PinCodeDetails dataPinCodeDetails = pinCodeDetails.get();
			PinCodeDetailsResponse pinCodeResponse = new PinCodeDetailsResponse();
			pinCodeResponse.setPincode(String.valueOf(dataPinCodeDetails.getPinCode()));
			pinCodeResponse.setState(dataPinCodeDetails.getState());
			pinCodeResponse.setDistrict(dataPinCodeDetails.getDistrict());
			pinCodeResponse.setCity(dataPinCodeDetails.getCity());
			pinCodeResponse.setArea(dataPinCodeDetails.getArea());
			pinCodeResponse.setCountry(dataPinCodeDetails.getCountry());
			Gson gson = new Gson();
			String jsonResponsePin = gson.toJson(pinCodeResponse);
			pinCodeObject.put("pinCodeDetails", jsonResponsePin);
			responseBody.setResponseObj(pinCodeObject.toString());
			responseHeader.setResponseCode(CommonConstants.SUCCESS);
			responseHeader.setResponseMessage("");
			response.setResponseBody(responseBody);
			response.setResponseHeader(responseHeader);
		} else {
			logger.warn("No Data Found based on PinCode in DB");
			responseBody.setResponseObj("");
			responseHeader.setResponseCode(CommonConstants.FAILURE);
			responseHeader.setResponseMessage("No Record Found");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}
		logger.warn("End: Final PinCode Response :" + response.toString());
		return response;

	}

	public Response approveDeviationRaApplications(ApproveDeviationRaApplicationsReq requestObj) {
		logger.debug("Enter into approveDeviationRaApplications method");
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ApproveDeviationRaApplicationsReqFields reqFields = requestObj.getRequestObj();
		Gson gson = new Gson();
		try {
			logger.info("Processing request for applicationId: {}, recordType: {}, role: {}",
					reqFields.getApplicationId(), reqFields.getRecordType(), reqFields.getRole());
			if (Constants.CA_DEVIATION.equalsIgnoreCase(reqFields.getRecordType())) {
				logger.debug("Processing CA_DEVIATION record type");
				Optional<DeviationRATracker> deviationRATrackerRecordOpt = deviationRATrackerRepo
						.findByApplicationIdAndRecordId(reqFields.getApplicationId(), reqFields.getRecordId());
				if (deviationRATrackerRecordOpt.isPresent()) {
					DeviationRATracker deviationRATrackerRecord = deviationRATrackerRecordOpt.get();
					logger.debug("Found deviation record: {}", deviationRATrackerRecord);
					if (deviationRATrackerRecord.getAuthority().equalsIgnoreCase(reqFields.getRole())) {
						logger.info("Updating deviation record for applicationId: {}", reqFields.getApplicationId());
						deviationRATrackerRecord.setApprovedStatus(Constants.APPROVED);
						deviationRATrackerRecord.setRemarks(reqFields.getRemarks());
						deviationRATrackerRecord.setApprovedBy(requestObj.getUserId());
						deviationRATrackerRecord.setApprovedTs(LocalDateTime.now());
						deviationRATrackerRepo.save(deviationRATrackerRecord);
					} else {
						logger.error("Invalid role: {}", reqFields.getRole());
						responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
						responseBody.setResponseObj("Invalid role");
						response.setResponseBody(responseBody);
						response.setResponseHeader(responseHeader);
						return response;
					}
				} else {
					logger.warn("No deviation record found for applicationId: {}", reqFields.getApplicationId());
				}
			} else if (Constants.REASSESSMENT.equalsIgnoreCase(reqFields.getRecordType())) {
				logger.debug("Processing REASSESSMENT record type");
				List<DeviationRATracker> deviationRATrackerList = deviationRATrackerRepo
						.findByApplicationIdAndRecordTypeAndAuthority(reqFields.getApplicationId(),
								reqFields.getRecordType(), reqFields.getRole());
				if (!deviationRATrackerList.isEmpty()) {
					logger.info("Found {} reassessment records for applicationId: {}", deviationRATrackerList.size(),
							reqFields.getApplicationId());
					for (DeviationRATracker deviationRATrackerRecord : deviationRATrackerList) {
						logger.debug("Updating reassessment record: {}", deviationRATrackerRecord);
						if (deviationRATrackerRecord.getAuthority().equalsIgnoreCase(reqFields.getRole())) {
							deviationRATrackerRecord.setApprovedStatus(Constants.APPROVED);
							deviationRATrackerRecord.setRemarks(reqFields.getRemarks());
							deviationRATrackerRecord.setApprovedBy(requestObj.getUserId());
							deviationRATrackerRecord.setApprovedTs(LocalDateTime.now());
							deviationRATrackerRepo.save(deviationRATrackerRecord);
							logger.debug("successfully updated reassessment record: {}",
									deviationRATrackerRecord.getRecordId());
						} else {
							logger.debug("Skipping record with recordID : {} as authority does not match",
									deviationRATrackerRecord.getRecordId());
						}
					}
				} else {
					logger.warn("No reassessment records found for applicationId: {}", reqFields.getApplicationId());
				}
			} else {
				logger.error("Invalid record type: {}", reqFields.getRecordType());
				responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
				responseBody.setResponseObj("Invalid record type");
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
				return response;
			}
			List<DeviationRATracker> deviationRATrackerList = deviationRATrackerRepo
					.findByApplicationIdOrderByCreateTsAsc(reqFields.getApplicationId());
			if (deviationRATrackerList != null && !deviationRATrackerList.isEmpty()) {
				logger.info("Formatting timestamps for {} deviation records", deviationRATrackerList.size());
				deviationRATrackerList.forEach(deviationRATracker -> {
					deviationRATracker.setApprovedTimeStamp(
							deviationRATracker.getApprovedTs().format(Constants.ADMINFORMATTER));
					deviationRATracker.setCreatedTimeStamp(
							deviationRATracker.getCreateTs().format(Constants.ADMINFORMATTER));
				});
			} else {
				logger.warn("No deviation records found for applicationId: {}", reqFields.getApplicationId());
			}
			String deviationRATrackerListJson = gson.toJson(deviationRATrackerList);
			responseBody.setResponseObj(deviationRATrackerListJson);
			responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
			response.setResponseBody(responseBody);
			response.setResponseHeader(responseHeader);
			logger.info("Successfully processed approveDeviationRaApplications request for applicationId: {}",
					reqFields.getApplicationId());
		} catch (Exception e) {
			logger.error("Error while updating deviation RA tracker record for applicationId: {}",
					reqFields.getApplicationId(), e);
			responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
			responseBody.setResponseObj("Error while updating deviation RA tracker record");
			response.setResponseBody(responseBody);
			response.setResponseHeader(responseHeader);
			return response;
		}
		return response;
	}

	private boolean isStepSuccessful(String applicationId, String apiName, String currentStage) {
		List<ApiExecutionLog> logs = logRepository.findAllByApplicationIdAndApiNameAndCurrentStage(applicationId,
				apiName, currentStage);

		for (ApiExecutionLog log : logs) {
			logger.debug("Checking status for apiName={} | DB Value={} || currStage={}", apiName, log.getApiStatus(),
					log.getCurrentStage());
			if (ResponseCodes.SUCCESS.getValue().equals(log.getApiStatus())
					&& currentStage.equalsIgnoreCase(log.getCurrentStage())) {
				return true;
			}
		}

		return false;
	}

	private void saveLog(String applicationId, String stepName, String request, String response,
			String status, String errorMsg, String currentStage) {
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
	// ALL FALLBACK METHODS

	private Mono<Response> dbkitApplicationMovementFallback(
			FetchDeleteUserRequest fetchDeleteUserRequest, Properties prop, String roleId, Exception e) {
		logger.error("dbkitApplicationMovementFallback error : ", e);
		return FallbackUtils.genericFallbackMono();
	}

	private Mono<Response> dbkitVerificationApplicationMovementFallback(
			FetchDeleteUserRequest fetchDeleteUserRequest, Properties prop, String roleId, Exception e) {
		logger.error("dbkitVerificationApplicationMovementFallback error : ", e);
		return FallbackUtils.genericFallbackMono();
	}

	private Mono<Response> disbursementApplicationMovementFallback(
			com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
			Properties prop, Exception e) {
		logger.error("disbursementApplicationMovementFallback error : ", e);
		return FallbackUtils.genericFallbackMono();
	}

	private Mono<Response> approveRejectApplicationFallback(
			com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
			boolean isSelfOnBoardingHeaderAppId, Properties prop, Exception e) {
		logger.error("approveRejectApplicationFallback error : ", e);
		return FallbackUtils.genericFallbackMono();
	}

	private Mono<Response> stageMovementApplicationFallback(
			com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest fetchDeleteUserRequest, Properties prop,
			String roleId, Exception e) {
		logger.error("stageMovementApplicationFallback error : ", e);
		return FallbackUtils.genericFallbackMono();
	}

	private Mono<Response> creditAssessmentApplicationMovementFallback(
			com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest fetchDeleteUserRequest, Properties prop,
			String roleId, Exception e) {
		logger.error("creditAssessmentApplicationMovementFallback error : ", e);
		return FallbackUtils.genericFallbackMono();
	}

	private Mono<Response> creditDeviationApplicationMovementFallback(
			com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest fetchDeleteUserRequest, Properties prop,
			String roleId, Exception e) {
		logger.error("creditDeviationApplicationMovementFallback error : ", e);
		return FallbackUtils.genericFallbackMono();
	}

	private Mono<Response> creditReassessmentApplicationMovementFallback(
			com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest fetchDeleteUserRequest, Properties prop,
			String roleId, Exception e) {
		logger.error("creditReassessmentApplicationMovementFallback error : ", e);
		return FallbackUtils.genericFallbackMono();
	}

	// Loan Creation & CoAPplicant Creation
	private Mono<Response> sanctionApplicationMovementFallback(
			com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
			Properties prop,
			Exception e) {
		logger.error("sanctionApplicationMovementFallback error : ", e);
		return FallbackUtils.genericFallbackMono();
	}

	private Mono<Response> approveRenewalApplicationFallback(
			ApplyLoanRequest fetchDeleteUserRequest, Header header,
			boolean isSelfOnBoardingHeaderAppId, Properties prop, Exception e) {
		logger.error("approveRejectApplicationFallback error : ", e);
		return FallbackUtils.genericFallbackMono();
	}

	private Mono<Response> initiateRejectedApplicationFallback(
			com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
			boolean isSelfOnBoardingHeaderAppId, Properties prop, Exception e) {
		logger.error("initiateRejectedApplicationFallback error : ", e);
		return FallbackUtils.genericFallbackMono();
	}

	private Response populateRejectedDataInAllTablesFallback(PopulateRejectedDataRequest apiRequest,
			boolean isSelfOnBoardingappId, Properties prop, Exception e) {
		logger.error("populateRejectedDataInAllTablesFallback error : ", e);
		return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
	}

	private Response fetchLitCodeFallback(String scrName, String language, Exception e) {
		logger.error("fetchLitCodeFallback error : ", e);
		return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
	}

	private Response fetchDetailsBasedOnPinCodeFallback(PinCodeApiRequest requestWrapper, Exception e) {
		logger.error("fetchDetailsBasedOnPinCodeFallback error : ", e);
		return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
	}

	/**
	 * Recursively compare keys from English JSON to target JSON.
	 */
	private static void compareKeys(JsonNode englishNode, JsonNode targetNode, String path, Set<String> missingKeys) {
		Iterator<String> fieldNames = englishNode.fieldNames();
		while (fieldNames.hasNext()) {
			String field = fieldNames.next();
			String currentPath = path.isEmpty() ? field : path + "." + field;

			if (!targetNode.has(field)) {
				missingKeys.add(currentPath);
			} else {
				JsonNode englishChild = englishNode.get(field);
				JsonNode targetChild = targetNode.get(field);

				// If it's an object, go deeper
				if (englishChild.isObject() && targetChild.isObject()) {
					compareKeys(englishChild, targetChild, currentPath, missingKeys);
				}
			}
		}
	}

}
