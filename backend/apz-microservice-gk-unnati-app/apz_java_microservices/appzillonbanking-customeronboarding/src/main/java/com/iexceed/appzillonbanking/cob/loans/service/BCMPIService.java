package com.iexceed.appzillonbanking.cob.loans.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.core.repository.ab.*;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.loans.payload.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;
import com.iexceed.appzillonbanking.cob.service.COBService;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class BCMPIService {

    private static final Logger logger = LogManager.getLogger(BCMPIService.class);

	private final ApplicationMasterRepository applicationMasterRepo;
	private final LoanService loanService;
	private final BCMPIStageVerificationRepository bcmpiStageVerificationRepository;
	private final COBService cobService;
	private final BCMPIMasterRepository bcmpiMasterRepository;
	private final AddressDetailsRepository addressDetailsRepository;
	private final BCMPIIncomeDetailsRepository bcmpiIncomeDetailsRepo;
	private final BCMPILoanObligationsRepository bcmpiLoanObligationsRepo;
	private final BCMPIOtherDetailsRepository bcmpiOtherDetailsRepo;
	private final InsuranceDetailsRepository insuranceDtlRepo;
	private final LoanDtlsRepo loanDtlsRepo;
	private final CibilDetailsRepository cibilDtlRepo;
	private final OccupationDetailsRepository occupationDetailsRepository;
	private final UdhyamRepository udhyamRepository;
    private final CustomerDetailsRepository custDtlRepo;
	private final ApiExecutionLogRepository logRepository;

	public BCMPIService(ApplicationMasterRepository applicationMasterRepo, LoanService loanService,
			BCMPIStageVerificationRepository bcmpiStageVerificationRepository,
			COBService cobService, BCMPIMasterRepository bcmpiMasterRepository,
			AddressDetailsRepository addressDetailsRepository, BCMPIIncomeDetailsRepository bcmpiIncomeDetailsRepo,
			BCMPILoanObligationsRepository bcmpiLoanObligationsRepo, BCMPIOtherDetailsRepository bcmpiOtherDetailsRepo,
			InsuranceDetailsRepository insuranceDtlRepo, LoanDtlsRepo loanDtlsRepo,
			CibilDetailsRepository cibilDtlRepo, OccupationDetailsRepository occupationDetailsRepository,
						UdhyamRepository udhyamRepository,CustomerDetailsRepository custDtlRepo, ApiExecutionLogRepository logRepository) {
		this.cobService = cobService;
		this.applicationMasterRepo = applicationMasterRepo;
		this.loanService = loanService;
		this.bcmpiStageVerificationRepository = bcmpiStageVerificationRepository;
		this.bcmpiMasterRepository = bcmpiMasterRepository;
		this.addressDetailsRepository = addressDetailsRepository;
		this.bcmpiIncomeDetailsRepo = bcmpiIncomeDetailsRepo;
		this.bcmpiLoanObligationsRepo = bcmpiLoanObligationsRepo;
		this.bcmpiOtherDetailsRepo = bcmpiOtherDetailsRepo;
		this.insuranceDtlRepo = insuranceDtlRepo;
		this.loanDtlsRepo = loanDtlsRepo;
		this.cibilDtlRepo = cibilDtlRepo;
		this.occupationDetailsRepository = occupationDetailsRepository;
		this.udhyamRepository = udhyamRepository;
        this.custDtlRepo = custDtlRepo;
		this.logRepository = logRepository;
	}

    public Response BcmpiUploadData(UploadLoanRequest bcmpiStageMovementRequest){
        logger.debug("OnEntry :: uploadLoan");
		Gson gson = new Gson();
		Response fetchUserDetailsResponse = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		fetchUserDetailsResponse.setResponseHeader(responseHeader);
		ResponseBody responseBody = new ResponseBody();
		CustomerDataFields customerDataFields = null;

        try{
            Properties prop = null;
			try {
				prop = CommonUtils.readPropertyFile();
			} catch (IOException e) {
				logger.error("Error while reading property file in populateRejectedData ", e);
				fetchUserDetailsResponse = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
						ResponseCodes.FAILURE.getKey());
			}

            UploadLoanRequestFields requestObj = bcmpiStageMovementRequest.getRequestObj();
            String applicationId = requestObj.getApplicationId();
			String appId = requestObj.getAppId();
			int versionNum = requestObj.getVersionNum();
			logger.debug("applicationID : {}" , applicationId);
			logger.debug("appId : {}, versionNum : {}", appId , versionNum);
			logger.debug("customerType : {}" , requestObj.getCustomerType());
			String userId = bcmpiStageMovementRequest.getUserId();

            ApplicationMaster applicationMasterData;
			Optional<ApplicationMaster> appMasterDb = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNum(
					requestObj.getAppId(), requestObj.getApplicationId(), requestObj.getVersionNum());
			if (appMasterDb.isPresent()) {
				logger.debug("appMasterDb data found");
				applicationMasterData = appMasterDb.get();
				logger.debug("appMasterDb data found: {}" , applicationMasterData);
				String lockedBy = applicationMasterData.getUpdatedBy();
				if(userId.equalsIgnoreCase(lockedBy)) {
					logger.debug("Application is locked by the same user: {}", userId);
				}else{
					logger.warn("Application is locked by another user: {}. Cannot proceed with approve/reject.", lockedBy);
					fetchUserDetailsResponse = CommonUtils.formFailResponse("Application is currently being used by user (" + lockedBy + ")",
							ResponseCodes.FAILURE.getKey());
					return fetchUserDetailsResponse;
				}
			} else {
				logger.debug("appMasterDb data not found");
				responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
				responseBody.setResponseObj(Constants.APP_MASTER_NOT_FOUND);
				fetchUserDetailsResponse.setResponseBody(responseBody);
				return fetchUserDetailsResponse;
			}

            String stageId = requestObj.getStageId().toUpperCase();
			logger.debug("stageId : {}" , stageId);
			logger.debug("customerType: {}" , requestObj.getCustomerType());
			String custCode = requestObj.getCustomerType().equalsIgnoreCase("Applicant") ? "A" : "C";
			logger.debug("custCode : {}" , custCode);
			logger.debug("requestType : {}" , requestObj.getRequestType());
			String requestType = requestObj.getRequestType().trim();

			ApplicationMaster applicationMasterReq = null;
            switch(stageId){
                case "1": //BCMPI Loan Details
                    logger.debug("Stage 1: BCMPI Loan Details");
                    applicationMasterReq = requestObj.getApplicationMaster();
                    logger.debug("applicationMasterReq: {}" , applicationMasterReq);
					if("EDIT".equalsIgnoreCase(requestType)){
						loanService.updateLoanDtls(requestObj);
						logger.debug("Stage 1 completed : Loan Details updated");
					}else if(Constants.VERIFY.equalsIgnoreCase(requestType)){
						updateBCMPIStageVerification(1, requestObj, custCode);
						logger.debug("Stage 1 completed : loan detail stage verified");
					}else{
						logger.debug("invalid request type");
					}
                    break;
                case "2":// BCMPI Photo, KYC & RelationShip
                    logger.debug("Stage 2");
					if(Constants.VERIFY.equalsIgnoreCase(requestType)){
						updateBCMPIStageVerification(2, requestObj, custCode);
						updateQueries(2,requestObj, false);
						logger.debug("Stage 2 completed : Photo, KYC & RelationShip verified");
					}else if("QUERY".equalsIgnoreCase(requestType)){
						updateQueries(2,requestObj, true);
						logger.debug("Stage 2 completed : Photo, KYC & RelationShip updated");
					}else{
						logger.debug("invalid request type");
					}

                    break;
                case "3":// BCMPI Address Details
                    logger.debug("Stage 3");
					if("EDIT".equalsIgnoreCase(requestType)){
						updateBCMPIAddressDetails(3, requestObj, custCode);// personal //present
						updateQueries(3, requestObj, false);
						logger.debug("Stage 3 completed : Address Details updated");
					}else if(Constants.VERIFY.equalsIgnoreCase(requestType)){
						updateBCMPIStageVerification(3, requestObj, custCode);
						updateQueries(3, requestObj, false);
						logger.debug("Stage 3 completed :  Address Details stage verified");
					}else if("QUERY".equalsIgnoreCase(requestType)){
						updateQueries(3, requestObj, true);
						logger.debug("Stage 3 completed : Address Details query updated");
					}else{
						logger.debug("invalid request type");
					}
                    break;
                case "4"://BCMPI Insurance stage
                    logger.debug("Stage 4");
					updateInsuranceDetails(4, requestObj);
					logger.debug("Insurance Details updated");
					updateBCMPIStageVerification(4, requestObj, custCode);
					logger.debug("Insurance Details stage verified");
					logger.debug("Stage 4 completed");
                    break;
                case "5"://BCMPI Income Details Stage
                    logger.debug("Stage 5");
					updateBCMPIIncomeDetails(5, bcmpiStageMovementRequest, custCode);
					logger.debug("Income Details updated");
					updateBCMPIStageVerification(5, requestObj, custCode);
					logger.debug("Income Details stage verified");
					logger.debug("Address Updated");
					updateBCMPIAddressDetails(5, requestObj, custCode);
					logger.debug("Stage 5 completed");
                    break;
                case "6"://BCMPI Informal Loan Obligations
                    logger.debug("Stage 6");
					updateBCMPILoanObligations(6, bcmpiStageMovementRequest, custCode);
					logger.debug("Informal Loan Obligations updated");
					updateBCMPIStageVerification(6, requestObj, custCode);
					logger.debug("Informal Loan Obligations stage verified");
					logger.debug("Stage 6 completed");
                    break;
                case "7"://BCMPI Other Details
                    logger.debug("Stage 7");
					if("EDIT".equalsIgnoreCase(requestType)){
					updateBCMPIOtherDetails(7, bcmpiStageMovementRequest, custCode);
					logger.debug("Other Details updated");
					}else if(Constants.VERIFY.equalsIgnoreCase(requestType)){
					updateBCMPIOtherDetails(7, bcmpiStageMovementRequest, custCode);
					updateBCMPIStageVerification(7, requestObj, custCode);
					logger.debug("Other Details stage verified");
                    } else if (Constants.DISABILITY_DETAILS.equalsIgnoreCase(requestType)) {
                        List<String> disabledFields = new ArrayList<>();
                        for (CustomerDetails cd : requestObj.getCustomerDetailsList()) {
                            if (cd != null && cd.getPayload() != null && cd.getPayload().getIsDisabled() != null) {
                                disabledFields.add(cd.getPayload().getIsDisabled().toUpperCase());
                            }
                        }
                        if(disabledFields.contains("Y")||disabledFields.contains("YES")){
                            populateCustomerDtlsForDisabled(requestObj,applicationId);
						}else if(disabledFields.contains("N")||disabledFields.contains("NO")){
							populateCustomerDtlsForDisabled(requestObj,applicationId);
                        }else {
                            logger.debug("No disabled fields found with value Y or YES");
                        }
                        logger.debug("Other Details stage verified");
					}else{
						logger.debug("invalid request type");
					}
					logger.debug("Stage 7 completed");
                    break;
                case "8"://BCMPI review and confirm
                    logger.debug("Stage 8");
					updateBCMPIStageVerification(8, requestObj, custCode);
					logger.debug(" review and confirm stage verified");
					logger.debug("Stage 8 completed");
                    break;
                case "9"://BCMPI in-principle decision
                    logger.debug("Stage 9");
					if("EDIT".equalsIgnoreCase(requestType)){
						updateInsuranceDetails(4, requestObj);
						boolean updateDetected = false;
						try {
						updateDetected = retryBRECall(stageId, requestObj, prop);
						} catch (Exception e) {
							logger.error("Exception in SANCTION stage BRE call: {}", e.getMessage(), e);
							responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
							responseBody.setResponseObj("BRE check service failed");
							fetchUserDetailsResponse.setResponseHeader(responseHeader);
							fetchUserDetailsResponse.setResponseBody(responseBody);
							return fetchUserDetailsResponse;
						}
						if (!updateDetected) {
							logger.error("fetching old data since BRE data has not changed");
							responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
							responseBody.setResponseObj("BRE check service failed");
							fetchUserDetailsResponse.setResponseHeader(responseHeader);
							fetchUserDetailsResponse.setResponseBody(responseBody);
							return fetchUserDetailsResponse;
						}
						logger.debug("Loan Amount updated");
						logger.debug("BRE Check done");
					}else if(Constants.VERIFY.equalsIgnoreCase(requestType)){
						updateLoanAmount(stageId, requestObj);
						updateBCMPIStageVerification(9, requestObj, custCode);
						logger.debug("in-principle decision stage verified");
					}else{
						logger.debug("invalid request type");
					}
					logger.debug("Stage 9 completed");
                    break;
                case "10"://BCMPI E-KYC
                    logger.debug("Stage 10");
					
                    break;
				case "SANCTION":
					logger.debug("Sanction Stage");
					boolean updateDetected = false;
					try {
					updateDetected = updateLoanAmountAndBRECheck(stageId, requestObj, prop);
					} catch (Exception e) {
						logger.error("Exception in SANCTION stage BRE call: {}", e.getMessage(), e);
						responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
						responseBody.setResponseObj("BRE check service failed");
						fetchUserDetailsResponse.setResponseHeader(responseHeader);
						fetchUserDetailsResponse.setResponseBody(responseBody);
						return fetchUserDetailsResponse;
					}
					if (!updateDetected) {
						logger.error("fetching old data since BRE data has not changed");
						responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
						responseBody.setResponseObj("BRE check service failed");
						fetchUserDetailsResponse.setResponseHeader(responseHeader);
						fetchUserDetailsResponse.setResponseBody(responseBody);
						return fetchUserDetailsResponse;
					}
					logger.debug("Sanction loan amount update and BRE Check done");
					break;
                case "BRE":
                    if ("EDIT".equalsIgnoreCase(requestType)) {
                        boolean updateDetectedBool = false;
						try {
                        updateDetectedBool = retryBRECall(stageId, requestObj, prop);
						} catch (Exception e) {
							logger.error("Exception in SANCTION stage BRE call: {}", e.getMessage(), e);
							responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
							responseBody.setResponseObj("BRE check service failed : " + e.getMessage());
							fetchUserDetailsResponse.setResponseHeader(responseHeader);
							fetchUserDetailsResponse.setResponseBody(responseBody);
							return fetchUserDetailsResponse;
						}
                        if (!updateDetectedBool) {
                            logger.error("fetching old data since BRE data has not changed");
                            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                            responseBody.setResponseObj("BRE check service failed");
                            fetchUserDetailsResponse.setResponseHeader(responseHeader);
                            fetchUserDetailsResponse.setResponseBody(responseBody);
                            return fetchUserDetailsResponse;
                        }
                    }
                    logger.debug("BRE Check done");
                    break;
                default:
                    logger.debug("Invalid Stage Id");
                    break;
            }
			BigDecimal customerDetailsId = requestObj.getCustomerDtlsId();
            applicationMasterData.setUpdateTs(LocalDateTime.now());
			applicationMasterData.setUpdatedBy(bcmpiStageMovementRequest.getUserId());
			applicationMasterRepo.save(applicationMasterData);
			logger.debug("applicationMaster saved");

            customerDataFields = cobService.getCustomerData(applicationMasterData, applicationId, appId, versionNum);
            logger.debug("customerDataFields  : {}" , customerDataFields);
            Optional<BCMPIStageVerification> bcmpiStageData = bcmpiStageVerificationRepository.findById(applicationId);
            if(bcmpiStageData.isPresent()){
                logger.debug("bcmpiStageData found");
				customerDataFields.setBcmpiStatDetails(
					CommonUtils.parseBCMPIStageVerificationData(bcmpiStageData.get().getEditedFields(), bcmpiStageData.get().getQueries()));
                customerDataFields.setBcmpiVerifiedStage(null != bcmpiStageData.get().getVerifiedStages()
                ? Arrays.asList(bcmpiStageData.get().getVerifiedStages().split("\\|")) : new ArrayList<>());
            }
			Optional<BCMPIIncomeDetails> bcmpiIncomeDataOpt = bcmpiIncomeDetailsRepo.findById(applicationId);
			if(bcmpiIncomeDataOpt.isPresent()){
				logger.debug("bcmpiIncomeData found");
				BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper = gson.fromJson(bcmpiIncomeDataOpt.get().getPayload(), BCMPIIncomeDetailsWrapper.class); // Object to be changed to a wrapper class
				BCMPIIncomeDetails bcmpiIncomeDetails = bcmpiIncomeDataOpt.get();
				bcmpiIncomeDetails.setBcmpiIncomeDetailsWrapper(bcmpiIncomeDetailsWrapper);
				customerDataFields.setBcmpiIncomeDetails(bcmpiIncomeDetails);
			}
			Optional<BCMPILoanObligations> bcmpiLoanObligationsOpt = bcmpiLoanObligationsRepo.findById(applicationId);
			if(bcmpiLoanObligationsOpt.isPresent()){
				logger.debug("bcmpiLoanObligations found");
				LoanObligationsWrapper loanObligationsWrapper = gson.fromJson(bcmpiLoanObligationsOpt.get().getPayload(), LoanObligationsWrapper.class);
				BCMPILoanObligations bcmpiLoanObligations = bcmpiLoanObligationsOpt.get();
				bcmpiLoanObligations.setLoanObligationsWrapper(loanObligationsWrapper);
				customerDataFields.setBcmpiLoanObligations(bcmpiLoanObligations);
			}
			Optional<BCMPIOtherDetails> bcmpiOtherDetailsOpt = bcmpiOtherDetailsRepo.findById(applicationId);
			if(bcmpiOtherDetailsOpt.isPresent()){
				logger.debug("bcmpiIncomeData found");
				BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = gson.fromJson(bcmpiOtherDetailsOpt.get().getPayload(), BCMPIOtherDetailsWrapper.class);
				BCMPIOtherDetails bcmpiOtherDetails = bcmpiOtherDetailsOpt.get();
				bcmpiOtherDetails.setBcmpiOtherDetailsWrapper(bcmpiOtherDetailsWrapper);
				customerDataFields.setBcmpiOtherDetails(bcmpiOtherDetails);
			}
			Optional<List<Udhyam>> udhyamRecordsOpt = udhyamRepository.findByApplicationId(applicationId);
            if(udhyamRecordsOpt.isPresent()){
                List<Udhyam> udhyamRecords = udhyamRecordsOpt.get();
                customerDataFields.setUdhyamDetails(udhyamRecords);
            }

            String customerdata = gson.toJson(customerDataFields);
			customerdata = customerdata.replace(Constants.PAYLOAD_COLUMN, Constants.PAYLOAD);
			responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
			responseBody.setResponseObj(customerdata);
			fetchUserDetailsResponse.setResponseHeader(responseHeader);
			fetchUserDetailsResponse.setResponseBody(responseBody);
			return fetchUserDetailsResponse;

		} catch (Exception e) {
			logger.error("Exception in uploadLoan : {}", e.getMessage(), e);
			responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
			responseBody.setResponseObj(e.getMessage());
			fetchUserDetailsResponse.setResponseHeader(responseHeader);
			fetchUserDetailsResponse.setResponseBody(responseBody);
			return fetchUserDetailsResponse;
		}
    }

    private void populateCustomerDtlsForDisabled(UploadLoanRequestFields requestObj, String applicationID) {
        Gson gson = new Gson();
        String payload;

        List<CustomerDetails> customerDetailsList = requestObj.getCustomerDetailsList();
        List<CustomerDetails> customerDtl=custDtlRepo.findByApplicationId(applicationID);
        if(!customerDtl.isEmpty()) {
            for(CustomerDetails cust: customerDetailsList) {
                for(CustomerDetails custfind : customerDtl) {
                    if(cust.getCustDtlId().equals(custfind.getCustDtlId())) {
                        payload=gson.toJson(cust.getPayload());
                        custfind.setPayloadColumn(payload);
                        custDtlRepo.save(custfind);
                    }
                }
            }
        }}


	public void updateSourcingInsurance(ApplyLoanRequestFields applyLoanRequestFields){
		UploadLoanRequestFields uploadLoanRequestFields = new UploadLoanRequestFields();
		uploadLoanRequestFields.setAppId(applyLoanRequestFields.getAppId());
		uploadLoanRequestFields.setApplicationId(applyLoanRequestFields.getApplicationId());
		uploadLoanRequestFields.setInsuranceDetailsWrapperList(applyLoanRequestFields.getInsuranceDetailsWrapperList());
		uploadLoanRequestFields.setCustomerType(applyLoanRequestFields.getInsuranceType());
		uploadLoanRequestFields.setVersionNum(applyLoanRequestFields.getVersionNum());
		updateInsuranceDetails(4, uploadLoanRequestFields);
	}

	public void updateSourcingIncome(ApplyLoanRequest applyLoanRequest, String custCode){
		//Creating new uploadLoanRequest
		UploadLoanRequest uploadLoanRequest = new UploadLoanRequest();
		uploadLoanRequest.setUserId(applyLoanRequest.getUserId());
		uploadLoanRequest.setAppId(applyLoanRequest.getAppId());

		//Seting required uploadLoanRequestFields to update BCMPI Income Details
		ApplyLoanRequestFields applyLoanRequestFields = applyLoanRequest.getRequestObj();
		UploadLoanRequestFields uploadLoanRequestFields = new UploadLoanRequestFields();
		uploadLoanRequestFields.setAppId(applyLoanRequestFields.getAppId());
		uploadLoanRequestFields.setApplicationId(applyLoanRequestFields.getApplicationId());
		uploadLoanRequestFields.setVersionNum(applyLoanRequestFields.getVersionNum());
		uploadLoanRequestFields.setBcmpiIncomeDetailsWrapper(applyLoanRequestFields.getBcmpiIncomeDetailsWrapper());
		uploadLoanRequest.setRequestObj(uploadLoanRequestFields);

		updateBCMPIIncomeDetails(5, uploadLoanRequest, custCode);
	}

    private void updateInsuranceDetails(int stageId, UploadLoanRequestFields requestObj) {
    	logger.debug("OnEntry : updateInsuranceDetails for bcmpi , StageID:{}, requestObj:{}", stageId, requestObj);
		Gson gson = new Gson();

		BigDecimal applicantCustDtlId = BigDecimal.ZERO;
		Optional<CustomerDetails> applicantCustDetailsOpt = custDtlRepo
				.findByApplicationIdAndAppIdAndCustomerType(requestObj.getApplicationId(), Constants.APPID, Constants.APPLICANT);
		if(applicantCustDetailsOpt.isPresent()){
			applicantCustDtlId = applicantCustDetailsOpt.get().getCustDtlId();
		}
		try {
			List<InsuranceDetailsWrapper> insuranceDetailsWrapperList = requestObj.getInsuranceDetailsWrapperList();
			if (insuranceDetailsWrapperList == null || insuranceDetailsWrapperList.isEmpty()) {
				logger.warn("No insurance details to process for stageId:{}", stageId);
				return;
			}
			// Old cases handling changes Rakshith CAGL
			for (InsuranceDetailsWrapper wrapper : insuranceDetailsWrapperList) {
				InsuranceDetails det = wrapper.getInsuranceDetails();
				if (det == null) continue;

				if(det.getCustDtlId() == null
						|| (det.getInsuranceType().equalsIgnoreCase("Cust-Nominee")
						&& det.getCustDtlId().equals(applicantCustDtlId))) {
					det.setCustDtlId(CommonUtils.generateRandomNum());
				}
				InsuranceDetailsPayload payload = det.getPayload();

				// insuredCustType from payload.insuredName
				if (det.getInsuredCustType() == null || det.getInsuredCustType().trim().isEmpty()) {
					String insuredName = (payload != null && payload.getInsuredName() != null)
							? payload.getInsuredName().trim().toLowerCase() : "";
					switch (insuredName) {
						case "applicant":
							det.setInsuredCustType("Applicant");
							break;
						case "co-app":
							det.setInsuredCustType("Co-App");
							break;
						default:
							logger.warn("Cannot derive insuredCustType from insuredName:{}", insuredName);
							break;
					}
					logger.debug("Derived insuredCustType:{} from insuredName:{}", det.getInsuredCustType(), insuredName);
				}

				// insuranceType from payload.insuranceOption
				if (det.getInsuranceType() == null || det.getInsuranceType().trim().isEmpty()) {
					String insuranceOption = (payload != null && payload.getInsuranceOption() != null)
							? payload.getInsuranceOption().trim() : "";
					switch (insuranceOption) {
						case "Joint":
						case "Both":
							det.setInsuranceType("bothInsurance");
							break;
						case "Applicant":
							det.setInsuranceType("applicant");
							break;
						case "No insurance":
						case "Noinsurance":
							det.setInsuranceType("noInsurance");
							break;
						default:
							logger.warn("Cannot derive insuranceType from insuranceOption:{}, defaulting to applicant", insuranceOption);
							det.setInsuranceType("applicant");
							break;
					}
					logger.debug("Derived insuranceType:{} from insuranceOption:{}", det.getInsuranceType(), insuranceOption);
				}
			}

			List<String> passedCustType = insuranceDetailsWrapperList.stream()
					.map(e -> e.getInsuranceDetails().getInsuredCustType())
					.filter(t -> t != null)
					.distinct()
					.collect(Collectors.toList());
			logger.debug("passedCustType" + passedCustType);

			InsuranceDetails insuranceDetail = insuranceDetailsWrapperList.get(0).getInsuranceDetails();
			String insuranceType = insuranceDetail.getInsuranceType();
			logger.warn("insuranceType:{}", insuranceType);

			// Null guard on switch
			if (insuranceType == null) {
				logger.error("insuranceType still null after normalization, aborting");
				return;
			}

			switch (insuranceType) {
				case "bothInsurance":
					logger.debug("OnEntry JOINT");
					if (insuranceDetail.getPayload().getNomineeAdded().equalsIgnoreCase("Y")) {
						logger.debug("OnEntry JOINT Y");
						insuranceDetailsWrapperList.stream().forEach(insDet -> {
							InsuranceDetails insDetObj = insDet.getInsuranceDetails();
							logger.debug(" InsuranceDetails: " + insDetObj);
//							Optional<InsuranceDetails> insuranceDtlObjDb = insuranceDtlRepo
//									.findByApplicationIdAndInsuredCustType(insDetObj.getApplicationId(),
//											insDetObj.getInsuredCustType());
							Optional<InsuranceDetails> insuranceDtlObjDb = findInsuranceRecord(insDetObj, requestObj.getApplicationId());
					if (insuranceDtlObjDb.isPresent()) {
								InsuranceDetails existinginsDtl = insuranceDtlObjDb.get();
								logger.debug("InsuranceDetails present with: " + insDetObj.getInsuredCustType());
								logger.debug("Existing InsuranceDetails: " + existinginsDtl);
								InsuranceDetailsPayload existingPayload = gson.fromJson(existinginsDtl.getPayloadColumn(),
										InsuranceDetailsPayload.class);
								logger.debug("Existing Deteils:{} ", existingPayload);
								existinginsDtl.setPayloadColumn(gson.toJson(insDetObj.getPayload()));
								existinginsDtl.setCustDtlId(insDetObj.getCustDtlId());
								existinginsDtl.setInsuredCustType(insDetObj.getInsuredCustType());
								existinginsDtl.setVersionNum(insDetObj.getVersionNum());
								existinginsDtl.setInsuranceType(insDetObj.getInsuranceType());
								existinginsDtl.setAppId(insDetObj.getAppId());
								InsuranceDetails save = insuranceDtlRepo.save(existinginsDtl);
								logger.debug("InsuranceDetails updated:" + save);
							} else {
								logger.debug("InsuranceDetails not present with: " + insDetObj.getInsuredCustType());
								// Not Exist insert
								InsuranceDetails save = insuranceDtlRepo.save(InsuranceDetails.builder()
										.applicationId(insDetObj.getApplicationId()).insuranceDtlId(CommonUtils.generateRandomNum())
										.appId(insDetObj.getAppId()).custDtlId(insDetObj.getCustDtlId())
										.insuranceType(insDetObj.getInsuranceType())
										.insuredCustType(insDetObj.getInsuredCustType())
										.versionNum(insDetObj.getVersionNum()).payloadColumn(gson.toJson(insDetObj.getPayload())).build());
								logger.debug("InsuranceDetails save: " + save);
                        }
						});
						logger.debug("Exit JOINT Y");
					} else if (("N").equalsIgnoreCase(insuranceDetail.getPayload().getNomineeAdded())) {
						logger.debug("OnEntry JOINT N");
						insuranceDetailsWrapperList.stream().forEach(insDet -> {
							InsuranceDetails insDetObj = insDet.getInsuranceDetails();
							logger.debug(" InsuranceDetails: " + insDetObj);
//							Optional<InsuranceDetails> insuranceDtlObjDb = insuranceDtlRepo
//									.findByApplicationIdAndInsuredCustType(insDetObj.getApplicationId(),
//											insDetObj.getInsuredCustType());
							Optional<InsuranceDetails> insuranceDtlObjDb = findInsuranceRecord(insDetObj, requestObj.getApplicationId());
							if (insuranceDtlObjDb.isPresent()) {
								logger.debug("InsuranceDetails present with: " + insDetObj.getInsuredCustType());
								if (!("Cust-Nominee").equalsIgnoreCase(insDetObj.getInsuredCustType())) {
									InsuranceDetails existinginsDtl = insuranceDtlObjDb.get();
									logger.debug("Existing InsuranceDetails: " + existinginsDtl);
									InsuranceDetailsPayload existingPayload = gson
											.fromJson(existinginsDtl.getPayloadColumn(), InsuranceDetailsPayload.class);
									logger.debug("Existing Deteils:{} ", existingPayload);
									existinginsDtl.setPayloadColumn(gson.toJson(insDetObj.getPayload()));
									existinginsDtl.setCustDtlId(insDetObj.getCustDtlId());
									existinginsDtl.setInsuredCustType(insDetObj.getInsuredCustType());
									existinginsDtl.setVersionNum(insDetObj.getVersionNum());
									existinginsDtl.setInsuranceType(insDetObj.getInsuranceType());
									existinginsDtl.setAppId(insDetObj.getAppId());
									InsuranceDetails save = insuranceDtlRepo.save(existinginsDtl);
									logger.debug("InsuranceDetails updated:" + save);
								}
					}else {
								logger.debug("InsuranceDetails not present with: " + insDetObj.getInsuredCustType());
								if (!("Cust-Nominee").equalsIgnoreCase(insDetObj.getInsuredCustType())) {
									InsuranceDetails save = insuranceDtlRepo.save(InsuranceDetails.builder()
											.applicationId(insDetObj.getApplicationId())
											.insuranceDtlId(CommonUtils.generateRandomNum()).appId(insDetObj.getAppId())
											.custDtlId(insDetObj.getCustDtlId()).insuranceType(insDetObj.getInsuranceType())
											.insuredCustType(insDetObj.getInsuredCustType())
											.versionNum(insDetObj.getVersionNum())
											.payloadColumn(gson.toJson(insDetObj.getPayload())).build());
									logger.debug("InsuranceDetails save: " + save);
					}
							}
							// For Applicant "N" deleting @Cust-Nominee
							logger.debug("Cust-Nominee InsuranceDetails Checking:");
							Optional<InsuranceDetails> custNomineeobj = insuranceDtlRepo
									.findByApplicationIdAndInsuredCustType(insDet.getInsuranceDetails().getApplicationId(),
											"Cust-Nominee");
							// Deleting Cust-Nominee if NomineeAdded() "N"
							if (custNomineeobj.isPresent()) {
								logger.debug("Cust-Nominee InsuranceDetails present:");
								insuranceDtlRepo.delete(custNomineeobj.get());
							}
						});
						logger.debug("Exit JOINT N");
					}
					logger.debug("Exit JOINT ");
					break;
				case "applicant":
					logger.debug("OnEntry Applicant ");
					if (("Y").equalsIgnoreCase(insuranceDetail.getPayload().getNomineeAdded())) {
						logger.debug("OnEntry Applicant Y ");
						insuranceDetailsWrapperList.stream().forEach(insDet -> {
							InsuranceDetails insDetObj = insDet.getInsuranceDetails();
							logger.debug(" InsuranceDetails: " + insDetObj);
							if (Arrays.asList("Applicant", "Cust-Nominee").contains(insDetObj.getInsuredCustType())) {
//								Optional<InsuranceDetails> insuranceDtlObjDb = insuranceDtlRepo
//										.findByApplicationIdAndInsuredCustType(insDetObj.getApplicationId(),
//												insDetObj.getInsuredCustType());
								Optional<InsuranceDetails> insuranceDtlObjDb = findInsuranceRecord(insDetObj, requestObj.getApplicationId());
								if (insuranceDtlObjDb.isPresent()) {
									logger.debug("InsuranceDetails present with: " + insDetObj.getInsuredCustType());
									InsuranceDetails existinginsDtl = insuranceDtlObjDb.get();
									logger.debug("Existing InsuranceDetails: " + existinginsDtl);
									InsuranceDetailsPayload existingPayload = gson
											.fromJson(existinginsDtl.getPayloadColumn(), InsuranceDetailsPayload.class);
									logger.debug("Existing Deteils:{} ", existingPayload);
									existinginsDtl.setPayloadColumn(gson.toJson(insDetObj.getPayload()));
									existinginsDtl.setCustDtlId(insDetObj.getCustDtlId());
									existinginsDtl.setInsuredCustType(insDetObj.getInsuredCustType());
									existinginsDtl.setVersionNum(insDetObj.getVersionNum());
									existinginsDtl.setInsuranceType(insDetObj.getInsuranceType());
									existinginsDtl.setAppId(insDetObj.getAppId());
									InsuranceDetails save = insuranceDtlRepo.save(existinginsDtl);
									logger.debug("InsuranceDetails updated:" + save);
				}else{
									logger.debug("InsuranceDetails not present with: " + insDetObj.getInsuredCustType());
									// Not Exist insert
									InsuranceDetails save = insuranceDtlRepo.save(InsuranceDetails.builder()
											.applicationId(insDetObj.getApplicationId())
											.insuranceDtlId(CommonUtils.generateRandomNum()).appId(insDetObj.getAppId())
											.custDtlId(insDetObj.getCustDtlId()).insuranceType(insDetObj.getInsuranceType())
											.insuredCustType(insDetObj.getInsuredCustType())
											.versionNum(insDetObj.getVersionNum()).payloadColumn(gson.toJson(insDetObj.getPayload())).build());
									logger.debug("InsuranceDetails save: " + save);
				}
							}
						});
						logger.debug("Exit Applicant Y");
					} else if (("N").equalsIgnoreCase(insuranceDetail.getPayload().getNomineeAdded())) {
						logger.debug("OnEntry Applicant N ");
						insuranceDetailsWrapperList.stream().forEach(insDet -> {
							InsuranceDetails insDetObj = insDet.getInsuranceDetails();
							logger.debug(" InsuranceDetails: " + insDetObj);
							if (Arrays.asList("Applicant").contains(insDetObj.getInsuredCustType())) {
//								Optional<InsuranceDetails> insuranceDtlObjDb = insuranceDtlRepo
//										.findByApplicationIdAndInsuredCustType(
//												insDet.getInsuranceDetails().getApplicationId(),
//												insDet.getInsuranceDetails().getInsuredCustType());
								Optional<InsuranceDetails> insuranceDtlObjDb = findInsuranceRecord(insDet.getInsuranceDetails(), requestObj.getApplicationId());
								if (insuranceDtlObjDb.isPresent()) {
									logger.debug("InsuranceDetails present with: " + insDetObj.getInsuredCustType());
									InsuranceDetails existinginsDtl = insuranceDtlObjDb.get();
									logger.debug("Existing InsuranceDetails: " + existinginsDtl);
									InsuranceDetailsPayload existingPayload = gson
											.fromJson(existinginsDtl.getPayloadColumn(), InsuranceDetailsPayload.class);
									logger.debug("Existing Deteils:{} ", existingPayload);
									existinginsDtl.setPayloadColumn(gson.toJson(insDetObj.getPayload()));
									existinginsDtl.setCustDtlId(insDetObj.getCustDtlId());
									existinginsDtl.setInsuredCustType(insDetObj.getInsuredCustType());
									existinginsDtl.setVersionNum(insDetObj.getVersionNum());
									existinginsDtl.setInsuranceType(insDetObj.getInsuranceType());
									existinginsDtl.setAppId(insDetObj.getAppId());
									InsuranceDetails save = insuranceDtlRepo.save(existinginsDtl);
									logger.debug("InsuranceDetails updated:" + save);
								} else {
									logger.debug("InsuranceDetails not present with: " + insDetObj.getInsuredCustType());
									// Not Exist insert
									InsuranceDetails save = insuranceDtlRepo.save(InsuranceDetails.builder()
											.applicationId(insDetObj.getApplicationId())
											.insuranceDtlId(CommonUtils.generateRandomNum()).appId(insDetObj.getAppId())
											.custDtlId(insDetObj.getCustDtlId()).insuranceType(insDetObj.getInsuranceType())
											.insuredCustType(insDetObj.getInsuredCustType())
											.versionNum(insDetObj.getVersionNum()).payloadColumn(gson.toJson(insDetObj.getPayload())).build());
									logger.debug("InsuranceDetails save: " + save);
								}
							}
						});
						logger.debug("Checking Cust-Nominee");
						Optional<InsuranceDetails> custNomineeObj = insuranceDtlRepo
								.findByApplicationIdAndInsuredCustType(insuranceDetail.getApplicationId(), "Cust-Nominee");
						if (custNomineeObj.isPresent()) {
							logger.debug(" Cust-Nominee present");
							// delete Existing Record
							insuranceDtlRepo.delete(custNomineeObj.get());
							logger.debug(" Delete present");
						}
						logger.debug("Exit Applicant N");
					}
					// For Y or N @Co-App Not Required
					logger.debug("Checking Co-App");
					Optional<InsuranceDetails> coAppObj = insuranceDtlRepo
							.findByApplicationIdAndInsuredCustType(insuranceDetail.getApplicationId(), "Co-App");
					if (!coAppObj.isPresent()) {
						BigDecimal coAppCustDtlId = insuranceDetailsWrapperList.stream()
								.map(w -> w.getInsuranceDetails())
								.filter(d -> "Co-App".equals(d.getInsuredCustType()))
								.map(d -> d.getCustDtlId())
								.findFirst()
								.orElse(null);
						if (coAppCustDtlId != null) {
							coAppObj = insuranceDtlRepo.findByApplicationIdAndCustDtlId(
									insuranceDetail.getApplicationId(), coAppCustDtlId);
							logger.debug("Co-App fallback lookup by custDtlId:{} found:{}", coAppCustDtlId, coAppObj.isPresent());
						}
					}
					if (coAppObj.isPresent()) {
						logger.debug(" Co-App present");
						// delete Existing Record
						insuranceDtlRepo.delete(coAppObj.get());
						logger.debug(" Co-App deleted rec: " + coAppObj.get());
					}
					logger.debug("Exit Applicant");
					break;
				default:
					logger.debug("OnEntry default");
					insuranceDetailsWrapperList.stream().forEach(insDet -> {
						InsuranceDetails insDetObj = insDet.getInsuranceDetails();
						logger.debug(" InsuranceDetails: " + insDetObj);
//						Optional<InsuranceDetails> insuranceDtlObjDb = insuranceDtlRepo
//								.findByApplicationIdAndInsuredCustType(insDet.getInsuranceDetails().getApplicationId(),
//										insDet.getInsuranceDetails().getInsuredCustType());
						Optional<InsuranceDetails> insuranceDtlObjDb = findInsuranceRecord(insDet.getInsuranceDetails(), requestObj.getApplicationId());
						if (insuranceDtlObjDb.isPresent()) {
							logger.debug("InsuranceDetails present with: " + insDetObj.getInsuredCustType());
							InsuranceDetails existinginsDtl = insuranceDtlObjDb.get();
							logger.debug("Existing InsuranceDetails: " + existinginsDtl);
							InsuranceDetailsPayload existingPayload = gson.fromJson(existinginsDtl.getPayloadColumn(),
									InsuranceDetailsPayload.class);
							logger.debug("Existing Deteils:{} ", existingPayload);
							existinginsDtl.setPayloadColumn(gson.toJson(insDetObj.getPayload()));
							existinginsDtl.setCustDtlId(insDetObj.getCustDtlId());
							existinginsDtl.setInsuredCustType(insDetObj.getInsuredCustType());
							existinginsDtl.setVersionNum(insDetObj.getVersionNum());
							existinginsDtl.setInsuranceType(insDetObj.getInsuranceType());
							existinginsDtl.setAppId(insDetObj.getAppId());
							InsuranceDetails save = insuranceDtlRepo.save(existinginsDtl);
							logger.debug("InsuranceDetails updated:" + save);
						} else {
							logger.debug("InsuranceDetails not present with: " + insDetObj.getInsuredCustType());
							// Not Exist insert
							InsuranceDetails save = insuranceDtlRepo
									.save(InsuranceDetails.builder().appId(insDetObj.getAppId())
											.insuranceDtlId(CommonUtils.generateRandomNum()).applicationId(insDetObj.getApplicationId())
											.custDtlId(insDetObj.getCustDtlId()).insuranceType(insDetObj.getInsuranceType())
											.insuredCustType(insDetObj.getInsuredCustType())
											.versionNum(insDetObj.getVersionNum()).payloadColumn(gson.toJson(insDetObj.getPayload())).build());
							logger.debug("InsuranceDetails save: " + save);
						}
					});
					/**
					 * @Delete all other's Object
					 */
					logger.debug("Deleteing  Except:" + passedCustType.toString());
					List<String> InsCustTypeList = Arrays.asList("Cust-Nominee", "Co-App", "Applicant");
					InsCustTypeList.stream().forEach(custType -> {
						if (!passedCustType.contains(custType)) {
							logger.debug("Checking  custType:" + custType);
							Optional<InsuranceDetails> custTypeobj = insuranceDtlRepo
									.findByApplicationIdAndInsuredCustType(insuranceDetail.getApplicationId(), custType);
							if (custTypeobj.isPresent()) {
								logger.debug("Checking  present:" + custType);
								// delete Existing Record
								insuranceDtlRepo.delete(custTypeobj.get());
								logger.debug("Deleted custType rec:" + custTypeobj);
							}
						}
					});

					logger.debug("Exit default");
					break;
			}
			}catch (Exception e) {
			logger.error("Exception in updateInsuranceDetails : {}", e.getMessage(), e);
			}
	}

	private Optional<InsuranceDetails> findInsuranceRecord(InsuranceDetails req, String applicationId) {
		// Try by primary key first — this covers old records where insuredCustType is null in DB
		if (req.getInsuranceDtlId() != null) {
			Optional<InsuranceDetails> byId = insuranceDtlRepo.findById(
					req.getInsuranceDtlId() instanceof BigDecimal
							? (BigDecimal) req.getInsuranceDtlId()
							: BigDecimal.valueOf(((Number) req.getInsuranceDtlId()).longValue())
			);
			if (byId.isPresent()) {
				logger.debug("Found insurance record by insuranceDtlId:{}", req.getInsuranceDtlId());
				return byId;
			}
		}
		// Fall back to custType lookup — for new records that don't have an ID yet
		logger.debug("insuranceDtlId not found, falling back to findByApplicationIdAndInsuredCustType for custType:{}", req.getInsuredCustType());
		return insuranceDtlRepo.findByApplicationIdAndInsuredCustType(applicationId, req.getInsuredCustType());
		}

private void updateBCMPIStageVerification(int stageId, UploadLoanRequestFields requestObj, String custCode) {
		logger.debug("Entry updateStageVerification method");
		// Construct the new stage verification strings

		String newEntry = stageId + "_" + custCode + "_" + LocalDateTime.now();

		Set<String> entriesTocompareStageVr = new HashSet<>();
		entriesTocompareStageVr.add(stageId + "_" + custCode);

		List<String> newEntries = Arrays.asList(newEntry);
		// Fetch existing record from DB
		Optional<BCMPIStageVerification> stageVerificationDb = bcmpiStageVerificationRepository
				.findById(requestObj.getApplicationId());
		logger.debug("Size of stage : {}" , stageVerificationDb);

		if (stageVerificationDb.isPresent()) {
			logger.debug("rpcStageVerificationDb record found");
			BCMPIStageVerification stageVerificationDbObj = stageVerificationDb.get();
			String existingStages = stageVerificationDbObj.getVerifiedStages();
			logger.debug("existingStages: {}" , existingStages);
			// Convert existing DB string into a Set
			if (StringUtils.isNotEmpty(existingStages)) {
				Set<String> existingSet = null;
				// Convert existing DB string into a Set
				existingSet = new HashSet<>(Arrays.asList(existingStages.split("\\|")));

				existingSet.removeIf(entry -> entriesTocompareStageVr.stream()
						.anyMatch(compareEntry -> entry.startsWith(compareEntry + "_")));

				logger.debug("existingStageVrSet : {}" , existingSet);
				//
				existingSet.add(newEntry);

				if (existingSet.isEmpty()) {
					stageVerificationDbObj.setVerifiedStages(null);
				} else {
					stageVerificationDbObj.setVerifiedStages(String.join("|", existingSet));
					logger.debug("existingStageVrSetFinal : {}" , existingSet);
				}
			} else {
				stageVerificationDbObj.setVerifiedStages(newEntry);
			}

			bcmpiStageVerificationRepository.save(stageVerificationDbObj);
		} else {
			logger.debug("rpcStageVerificationDb record not found. Inserting new record.");
			// If no record exists, create a new one
			String newStageString = String.join("|", newEntries);
			logger.debug("Newstage: {}" , newStageString);
			BCMPIStageVerification newRpcStageVn = new BCMPIStageVerification();
			newRpcStageVn.setApplicationId(requestObj.getApplicationId());
			newRpcStageVn.setVerifiedStages(newStageString);
			bcmpiStageVerificationRepository.save(newRpcStageVn);
		}

		logger.debug("End updateStageVerification method");

	}

	private void updateQueries(int stageId, UploadLoanRequestFields requestObj, boolean isQuery) {
		logger.debug("Entry updateQueries method");
		logger.debug("updateQueries request : {}", requestObj);

		try {
			List<String> newEntries = Optional.ofNullable(requestObj.getQueries())
					.filter(queries -> !queries.isEmpty())
					.orElse(new ArrayList<>())
					.stream()
					.map(field -> stageId + "_" + field + "_" + LocalDateTime.now())
					.collect(Collectors.toList());

			// Fetch existing record from DB
			Optional<BCMPIStageVerification> bcmpiStageVerificationDb = bcmpiStageVerificationRepository
					.findById(requestObj.getApplicationId());

			logger.debug("rpcStageVerificationDb findById : {}" , requestObj.getApplicationId());
			logger.debug("rpcStageVerificationDb : {}" , bcmpiStageVerificationDb);

			if (bcmpiStageVerificationDb.isPresent()) {
				logger.debug("rpcStageVerificationDb record found");
				BCMPIStageVerification stageVerificationDbObj = bcmpiStageVerificationDb.get();
				logger.debug("stageVerificationDbObj");
				String existingQueries = stageVerificationDbObj.getQueries();
				logger.debug("existingQueries: {}" , existingQueries);

				if (newEntries.isEmpty()) {
					// If new entries are empty, remove existing queries for the stage
					if (StringUtils.isNotEmpty(existingQueries)) {
						Set<String> existingSet = new HashSet<>(Arrays.asList(existingQueries.split("\\|")));
						existingSet.removeIf(query -> query.startsWith(stageId + "_"));
						logger.debug("Set after removing stage-specific queries: {}", existingSet);

						stageVerificationDbObj.setQueries(existingSet.isEmpty() ? null : String.join("|", existingSet));
					}
				} else {
					// If new entries are not empty, update the queries
					if (StringUtils.isNotEmpty(existingQueries)) {
						Set<String> existingSet = new HashSet<>(Arrays.asList(existingQueries.split("\\|")));
						Set<String> filteredSet = existingSet.stream()
								.filter(e -> e.startsWith(String.valueOf(stageId)))
								.collect(Collectors.toSet());
						logger.debug("Filtered Stream : {}" , filteredSet);

						existingSet.removeIf(elem -> filteredSet.contains(elem));
						logger.debug("Set after operation : {}" , existingSet);

						// Add only new values that are not already present
						for (String entry : newEntries) {
							if (!existingSet.contains(entry)) {
								existingSet.add(entry);
								logger.debug("Existing set : {}" , entry);
							}
						}

						logger.debug("existingSet final: {}" , existingSet);
						stageVerificationDbObj.setQueries(String.join("|", existingSet));
					} else {
						stageVerificationDbObj.setQueries(String.join("|", newEntries));
					}
				}

				if (null != stageVerificationDbObj.getVerifiedStages()) {
					String existingStagesVr = stageVerificationDbObj.getVerifiedStages();

					Set<String> existingStageVrSet = new HashSet<>(Arrays.asList(existingStagesVr.split("\\|")));
					logger.debug("existingStageVrSet : {}" , existingStageVrSet);

					if (!isQuery) {
						stageVerificationDbObj.setVerifiedStages(existingStageVrSet.isEmpty() ? null : String.join("|", existingStageVrSet));
					} else {
						existingStageVrSet.removeIf(entry -> entry.startsWith(stageId + "_"));
						logger.debug("existingStageVrSet after removing stage specific entries: {}", existingStageVrSet);
						stageVerificationDbObj.setVerifiedStages(existingStageVrSet.isEmpty() ? null : String.join("|", existingStageVrSet));
					}
					
				}
				bcmpiStageVerificationRepository.save(stageVerificationDbObj);
			} else {
				logger.debug("bcmpiStageVerificationDb record not found. Inserting new record.");

				// If no record exists and new entries are empty, do nothing
				if (!newEntries.isEmpty()) {
					String newStageString = String.join("|", newEntries);
					BCMPIStageVerification newEntry = new BCMPIStageVerification();
					newEntry.setApplicationId(requestObj.getApplicationId());
					newEntry.setQueries(newStageString);
					bcmpiStageVerificationRepository.save(newEntry);
				}
			}

			logger.debug("End updateQueries method");
		} catch (Exception e) {
			logger.error("Exception in update: ", e);
		}
	}

	public Response bcmpiMasterData(Map<String, String> requestMap) {
		Gson gson = new Gson();
		logger.debug("Entry bcmpiMasterData method");
		Response fetchUserDetailsResponse = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		String stageId = requestMap.get("stageId");
		List<BCMPIMaster> bcmpiMasterList;
		if("ALL".equalsIgnoreCase(stageId) || StringUtils.isEmpty(stageId)){
			bcmpiMasterList = bcmpiMasterRepository.findAll();
		}else{
			bcmpiMasterList = bcmpiMasterRepository.findByStageName(stageId);
		}		
		if(bcmpiMasterList.isEmpty()){
			logger.debug("no data found in bcmpiMaster");
			responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
			responseBody.setResponseObj("No data found in bcmpiMaster");
			fetchUserDetailsResponse.setResponseHeader(responseHeader);
			fetchUserDetailsResponse.setResponseBody(responseBody);
			return fetchUserDetailsResponse;
		}else{
			logger.debug("bcmpiMaster data found");
			String bcmpiMasterData = gson.toJson(bcmpiMasterList);
			responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
			responseBody.setResponseObj(bcmpiMasterData);
			fetchUserDetailsResponse.setResponseBody(responseBody);
			fetchUserDetailsResponse.setResponseHeader(responseHeader);
			return fetchUserDetailsResponse;
		}

		
	} 

	private void updateBCMPIAddressDetails(int stageId, UploadLoanRequestFields requestObj, String custCode){
		logger.debug("updateBCMPIAddressDetails method, StageID:{}, CustCode:{}", stageId, custCode);
		List<AddressDetailsWrapper> addressDetailsWrapperList = requestObj.getAddressDetailsWrapperList();
		Gson gsonObj = new Gson();
		for (AddressDetailsWrapper addressDetailsWrapper : addressDetailsWrapperList){
			List<AddressDetails> addressDetailsList = addressDetailsWrapper.getAddressDetailsList();
			logger.debug("addressDetailsList:{}", addressDetailsList);
			for(AddressDetails addressDetails : addressDetailsList){
				logger.debug("addressDetails:{}", addressDetails);
				Optional<AddressDetails> existingAddressDetailsOpt = addressDetailsRepository.findById(addressDetails.getAddressDtlsId());
				if(existingAddressDetailsOpt.isPresent()){
					logger.debug("existingAddressDetails data found: {}", existingAddressDetailsOpt.get());
					AddressDetails existingAddressDetails = existingAddressDetailsOpt.get();
					logger.debug("Incoming payload: {}", addressDetails.getPayload());
					existingAddressDetails.setPayloadColumn(gsonObj.toJson(addressDetails.getPayload()));
					logger.debug("Updated address details: {}", existingAddressDetails);
					addressDetailsRepository.save(existingAddressDetails);
					logger.debug("existingAddressDetails updated");
				}else{
					logger.error("invalid address details");
				}
			}
		
		}
	}

	private void updateBCMPIIncomeDetails(int stageId, UploadLoanRequest request, String custCode) {
		logger.debug("Entry updateBCMPIIncomeDetails method, StageID:{}, CustCode:{}", stageId, custCode);
		BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapperDetails = request.getRequestObj().getBcmpiIncomeDetailsWrapper();
		String applicationId = request.getRequestObj().getApplicationId();
		String userId = request.getUserId();
		Gson gson = new Gson();
		logger.debug("request : {}", request);

		logger.debug("Calculating Applicant Total Income");
		bcmpiIncomeDetailsWrapperDetails.setApplicantTotalIncome(BCMPIIncomeDetailsWrapper.calculateTotalIncome(bcmpiIncomeDetailsWrapperDetails, Constants.APPLICANT)
				.add(BCMPIIncomeDetailsWrapper.calculateTotalIncome(bcmpiIncomeDetailsWrapperDetails, "BOTH")));
		logger.debug("Applicant Total Income: {}", bcmpiIncomeDetailsWrapperDetails.getApplicantTotalIncome());

		bcmpiIncomeDetailsWrapperDetails.setApplicantTotalDeclaredIncome(BCMPIIncomeDetailsWrapper.calculateApplicantTotalDeclaredIncome(bcmpiIncomeDetailsWrapperDetails));

		bcmpiIncomeDetailsWrapperDetails.setCoApplicantTotalDeclaredIncome(BCMPIIncomeDetailsWrapper.calculateCoApplicantTotalDeclaredIncome(bcmpiIncomeDetailsWrapperDetails));

		logger.debug("Calculating Co-Applicant Total Income");
		bcmpiIncomeDetailsWrapperDetails.setCoApplicantTotalIncome(BCMPIIncomeDetailsWrapper.calculateTotalIncome(bcmpiIncomeDetailsWrapperDetails, Constants.CO_APPLICANT));
		logger.debug("Co-Applicant Total Income: {}", bcmpiIncomeDetailsWrapperDetails.getCoApplicantTotalIncome());

		logger.debug("Calculating Other Family Income");
		bcmpiIncomeDetailsWrapperDetails.setOtherFamilyIncome(calculateOtherFamilyIncome(request));
		logger.debug("Other Family Income: {}", bcmpiIncomeDetailsWrapperDetails.getOtherFamilyIncome());

		logger.debug("Calculating Field Assessed Income");
		bcmpiIncomeDetailsWrapperDetails.setFieldAssessedIncome(BCMPIIncomeDetailsWrapper.calculateFieldAssessedIncome(bcmpiIncomeDetailsWrapperDetails));
		logger.debug("Field Assessed Income: {}", bcmpiIncomeDetailsWrapperDetails.getFieldAssessedIncome());

		logger.debug("Calculating other income");
		bcmpiIncomeDetailsWrapperDetails.setOtherIncome(BCMPIIncomeDetailsWrapper.calculateOtherIncome(bcmpiIncomeDetailsWrapperDetails));
		logger.debug("Other Income: {}", bcmpiIncomeDetailsWrapperDetails.getOtherIncome());

		bcmpiIncomeDetailsWrapperDetails.setFieldAssessedIncomeDate(LocalDateTime.now());
		logger.debug("Field Assessed Income Date set to: {}", bcmpiIncomeDetailsWrapperDetails.getFieldAssessedIncomeDate());
        bcmpiIncomeDetailsWrapperDetails.setTotalBusinessSelfDeclaredIncome(BCMPIIncomeDetailsWrapper.calculateTotalBusinessDeclaredIncome(bcmpiIncomeDetailsWrapperDetails));
        bcmpiIncomeDetailsWrapperDetails.setTotalAgricultureSelfDeclaredIncome(BCMPIIncomeDetailsWrapper.calculateTotalAgricultureDeclaredIncome(bcmpiIncomeDetailsWrapperDetails));
        bcmpiIncomeDetailsWrapperDetails.setTotalSalarySelfDeclaredIncome(BCMPIIncomeDetailsWrapper.calculateTotalSalaryDeclaredIncome(bcmpiIncomeDetailsWrapperDetails));
        bcmpiIncomeDetailsWrapperDetails.setTotalWageSelfDeclaredIncome(BCMPIIncomeDetailsWrapper.calculateTotalWageDeclaredIncome(bcmpiIncomeDetailsWrapperDetails));
        bcmpiIncomeDetailsWrapperDetails.setTotalPensionSelfDeclaredIncome(BCMPIIncomeDetailsWrapper.calculateTotalPensionDeclaredIncome(bcmpiIncomeDetailsWrapperDetails));
        bcmpiIncomeDetailsWrapperDetails.setTotalRentalSelfDeclaredIncome(BCMPIIncomeDetailsWrapper.calculateTotalRentalDeclaredIncome(bcmpiIncomeDetailsWrapperDetails));
        bcmpiIncomeDetailsWrapperDetails.setTotalDeclaredIncome(BCMPIIncomeDetailsWrapper.calculateTotalDeclaredIncome(bcmpiIncomeDetailsWrapperDetails));

		String payloadStringified = gson.toJson(bcmpiIncomeDetailsWrapperDetails);
		logger.debug("Serialized BCMPIIncomeDetails payload: {}", payloadStringified);

		Optional<BCMPIIncomeDetails> existingIncomeDetailsOpt = bcmpiIncomeDetailsRepo.findById(applicationId);
		if (existingIncomeDetailsOpt.isPresent()) {
			logger.debug("Existing BCMPIIncomeDetails record found for ApplicationId: {}", applicationId);
			BCMPIIncomeDetails existingIncomeDetails = existingIncomeDetailsOpt.get();
			existingIncomeDetails.setPayload(payloadStringified);
			existingIncomeDetails.setUpdateTs(LocalDateTime.now());
			existingIncomeDetails.setUpdatedBy(userId);
			bcmpiIncomeDetailsRepo.save(existingIncomeDetails);
			logger.debug("Updated existing BCMPIIncomeDetails record: {}", existingIncomeDetails);
		} else {
			logger.debug("No existing BCMPIIncomeDetails record found for ApplicationId: {}, creating a new record", applicationId);
			BCMPIIncomeDetails newIncomeDetails = new BCMPIIncomeDetails();
			newIncomeDetails.setApplicationId(applicationId);
			newIncomeDetails.setAppId(request.getAppId());
			newIncomeDetails.setPayload(payloadStringified);
			newIncomeDetails.setVersionNo(request.getRequestObj().getVersionNum());
			newIncomeDetails.setCreateTs(LocalDateTime.now());
			newIncomeDetails.setCreatedBy(userId);
			bcmpiIncomeDetailsRepo.save(newIncomeDetails);
			logger.debug("Created new BCMPIIncomeDetails record: {}", newIncomeDetails);
		}
		logger.debug("Exit updateBCMPIIncomeDetails method");
	}

	private BigDecimal calculateOtherFamilyIncome(UploadLoanRequest request) {
		logger.debug("Entry calculateOtherFamilyIncome method");
		BigDecimal otherFamilyIncome = BigDecimal.ZERO;
		List<OccupationDetails> occupationDetailsList = occupationDetailsRepository
				.findByApplicationIdAndAppIdAndVersionNum(request.getRequestObj().getApplicationId(),
						request.getRequestObj().getAppId(), request.getRequestObj().getVersionNum());

		if (occupationDetailsList == null || occupationDetailsList.isEmpty()) {
			logger.warn("No OccupationDetails found for ApplicationId: {}, AppId: {}, VersionNum: {}",
					request.getRequestObj().getApplicationId(), request.getRequestObj().getAppId(), request.getRequestObj().getVersionNum());
			return otherFamilyIncome;
		}

		logger.debug("Fetched OccupationDetails for ApplicationId: {}, AppId: {}, VersionNum: {}",
				request.getRequestObj().getApplicationId(), request.getRequestObj().getAppId(), request.getRequestObj().getVersionNum());

		for (OccupationDetails occupationDetail : occupationDetailsList) {
			logger.debug("Processing OccupationDetail: {}", occupationDetail);
			if (occupationDetail.getPayload() != null && occupationDetail.getPayload().getOtherSourceIncome() != null
					&& occupationDetail.getPayload().getOtherSourceIncome().equalsIgnoreCase(Constants.OTHER_INCOME_OF_FAMILY_MEMBER)) {
				String otherSourceAnnualIncome = occupationDetail.getPayload().getOtherSourceAnnualIncome();
				if (otherSourceAnnualIncome != null) {
					try {
						otherFamilyIncome = otherFamilyIncome.add(new BigDecimal(otherSourceAnnualIncome));
						logger.debug("Updated Other Family Income: {}", otherFamilyIncome);
					} catch (NumberFormatException e) {
						logger.error("Invalid number format for OtherSourceAnnualIncome: {}", otherSourceAnnualIncome, e);
					}
				} else {
					logger.warn("OtherSourceAnnualIncome is null for OccupationDetail: {}", occupationDetail);
				}
			}
		}
		logger.debug("Total Other Family Income: {}", otherFamilyIncome);
		logger.debug("Exit calculateOtherFamilyIncome method");
		return otherFamilyIncome;
	}

	private void updateBCMPILoanObligations(int stageId, UploadLoanRequest request, String custCode){
		logger.debug("updateBCMPILoanObligations method, StageID:{}, CustCode:{}", stageId, custCode);
		LoanObligationsWrapper loanObligationsWrapper = request.getRequestObj().getLoanObligationsWrapper();

		if (null != loanObligationsWrapper) {
			loanObligationsWrapper.computeTotalLoanObligations();
			logger.debug("Computed Total Loan Obligations: {}", loanObligationsWrapper.getTotalLoanObligations());
		} else {
			logger.warn("LoanObligationsWrapper is null for CustCode: {}", custCode);
		}
		String applicationId = request.getRequestObj().getApplicationId();
		String userId = request.getUserId();
		Gson gson = new Gson();
		String payloadStringified = gson.toJson(loanObligationsWrapper);
		logger.debug("bcmpiLoanObligations payload:{}", loanObligationsWrapper);
		logger.debug("bcmpiLoanObligations payload:{} Stringified", payloadStringified);
		Optional<BCMPILoanObligations> existingLoanObligationsOpt = bcmpiLoanObligationsRepo.findById(applicationId);
		if(existingLoanObligationsOpt.isPresent()){
			logger.debug("existingLoanObligations data found: {}", existingLoanObligationsOpt.get());
			BCMPILoanObligations existingLoanObligations = existingLoanObligationsOpt.get();
			existingLoanObligations.setPayload(payloadStringified);
			existingLoanObligations.setUpdateTs(LocalDateTime.now());
			existingLoanObligations.setUpdatedBy(userId);
			bcmpiLoanObligationsRepo.save(existingLoanObligations);
			logger.debug("existingLoanObligations updated");
		}else{
			logger.debug("existingLoanObligations data not found, Inseting new row");
			BCMPILoanObligations newLoanObligations = new BCMPILoanObligations();
			newLoanObligations.setApplicationId(applicationId);
			newLoanObligations.setAppId(request.getAppId());
			newLoanObligations.setPayload(payloadStringified);
			newLoanObligations.setVersionNo(request.getRequestObj().getVersionNum());
			newLoanObligations.setCreateTs(LocalDateTime.now());
			newLoanObligations.setCreatedBy(userId);
			bcmpiLoanObligationsRepo.save(newLoanObligations);
			logger.debug("newLoanObligations created : {}", newLoanObligations);
		}
	}

	private void updateBCMPIOtherDetails(int stageId, UploadLoanRequest request, String custCode){
		logger.debug("updateBCMPIOtherDetails method, StageID:{}, CustCode:{}", stageId, custCode);
		BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = request.getRequestObj().getBcmpiOtherDetailsWrapper();
		String applicationId = request.getRequestObj().getApplicationId();
		String userId = request.getUserId();
		Gson gson = new Gson();
		String payloadStringified = gson.toJson(bcmpiOtherDetailsWrapper);
		logger.debug("BCMPIOtherDetails payload:{}", bcmpiOtherDetailsWrapper);
		logger.debug("BCMPIOtherDetails payloadStringified:{}", payloadStringified);
		Optional<BCMPIOtherDetails> existingOtherDetailsOpt = bcmpiOtherDetailsRepo.findById(applicationId);
		if(existingOtherDetailsOpt.isPresent()){
			logger.debug("existingOtherDetails data found: {}", existingOtherDetailsOpt.get());
			BCMPIOtherDetails existingIncomeDetails = existingOtherDetailsOpt.get();
			existingIncomeDetails.setPayload(payloadStringified);
			existingIncomeDetails.setUpdateTs(LocalDateTime.now());
			existingIncomeDetails.setUpdatedBy(userId);
			bcmpiOtherDetailsRepo.save(existingIncomeDetails);
			logger.debug("existingOtherDetails updated");
		}else{
			logger.debug("existingOtherDetails data not found, Inseting new row");
			BCMPIOtherDetails newIncomeDetails = new BCMPIOtherDetails();
			newIncomeDetails.setApplicationId(applicationId);
			newIncomeDetails.setAppId(request.getAppId());
			newIncomeDetails.setPayload(payloadStringified);
			newIncomeDetails.setVersionNo(request.getRequestObj().getVersionNum());
			newIncomeDetails.setCreateTs(LocalDateTime.now());
			newIncomeDetails.setCreatedBy(userId);
			bcmpiOtherDetailsRepo.save(newIncomeDetails);
			logger.debug("newOtherDetails created : {}", newIncomeDetails);
		}
	}

	private boolean updateLoanAmountAndBRECheck(String stageId, UploadLoanRequestFields requestObj, Properties prop) {
		boolean isBRECheckDone = false;
		logger.debug("Entry updateLoanAmountAndBRECheck method, StageID: {}, RequestObj: {}", stageId, requestObj);
		String applicationId = requestObj.getApplicationId();
		String appId = requestObj.getAppId();
		int versionNum = requestObj.getVersionNum();
		Gson gson = new Gson();

		BigDecimal updatedLoanAmt = requestObj.getUpdatedLoanAmount();
		BigDecimal customerDetailsId = requestObj.getCustomerDtlsId();
		logger.debug("Updated Loan Amount: {}, Customer Details ID: {}", updatedLoanAmt, customerDetailsId);

		LoanDetails loanDetails = loanDtlsRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
		logger.debug("Fetched LoanDetails: {}", loanDetails);

		if (Constants.SANCTION.equalsIgnoreCase(stageId)) {
			logger.debug("Processing SANCTION stage");
			if(loanDetails.getSanctionedLoanAmount() == null){
				loanDetails.setSanctionedLoanAmount(updatedLoanAmt);
			}else{
				loanDetails.setOldSanctionRecommendedLoanAmount(loanDetails.getSanctionedLoanAmount());
				loanDetails.setSanctionedLoanAmount(updatedLoanAmt);
			}
		} else {
			logger.debug("Processing non-SANCTION stage");
			if(loanDetails.getBmRecommendedLoanAmount() == null){
				loanDetails.setBmRecommendedLoanAmount(updatedLoanAmt);
			}else{
				loanDetails.setOldBmRecommendedLoanAmount(loanDetails.getBmRecommendedLoanAmount());
				loanDetails.setBmRecommendedLoanAmount(updatedLoanAmt);
			}

		}

		loanDtlsRepo.save(loanDetails);
		logger.debug("Loan Amount updated in loanDetails table: {}", loanDetails);
			Optional<CibilDetails> cibilDetailsOpt = cibilDtlRepo.findByApplicationIdAndAppIdAndCustDtlId(applicationId, appId, customerDetailsId);
			if (cibilDetailsOpt.isPresent()) {
				logger.debug("CibilDetails found for ApplicationId: {}, AppId: {}, CustomerDetailsId: {}", applicationId, appId, customerDetailsId);
				CibilDetails cibilDetails = cibilDetailsOpt.get();
				String breCBCheckReq = cibilDetails.getRequest();
                String breCbPayload = cibilDetails.getPayloadColumn();
				logger.debug("BRE CB Check Request: {}", breCBCheckReq);

				try {
					JsonObject breReqObj = gson.fromJson(breCBCheckReq, JsonObject.class);
					if (breReqObj.has("brecbRequest") && breReqObj.has("header")) {
						logger.debug("BRE CB Check Request contains required fields");
						BRECBRequest brecbRequest = gson.fromJson(breReqObj.get("brecbRequest"), BRECBRequest.class);
						brecbRequest.getRequestObj()
								.getBreCBValuesRequestvalues1()
								.getBreCBInputRequestinput1()
								.getBreCBValuesRequestvalues2()
								.getBreCBInputRequestinput2()
								.getApplicant()
								.setLoanAmount(updatedLoanAmt.toString());
					brecbRequest.getRequestObj()
							.getBreCBValuesRequestvalues1()
							.getBreCBInputRequestinput1()
							.getBreCBValuesRequestvalues2()
							.getBreCBInputRequestinput2().setCaglOs(requestObj.getCaglOs());
					brecbRequest.getRequestObj()
							.getBreCBValuesRequestvalues1()
							.getBreCBInputRequestinput1()
							.getBreCBValuesRequestvalues2()
							.getBreCBInputRequestinput2().setCurrentStage(requestObj.getCurrentStage());
                    String selfDeclaredIncome = brecbRequest.getRequestObj().getBreCBValuesRequestvalues1()
                            .getBreCBInputRequestinput1().getBreCBValuesRequestvalues2()
                            .getBreCBInputRequestinput2().getApplicant().getSelf_Declared_Income_of_customer();
                    String fieldAssessedIncome = brecbRequest.getRequestObj().getBreCBValuesRequestvalues1()
                            .getBreCBInputRequestinput1().getBreCBValuesRequestvalues2()
                            .getBreCBInputRequestinput2().getApplicant().getField_Assessed_Income_of_customer();
                    if(null == selfDeclaredIncome){
                        brecbRequest.getRequestObj().getBreCBValuesRequestvalues1()
                                .getBreCBInputRequestinput1().getBreCBValuesRequestvalues2()
                                .getBreCBInputRequestinput2().getApplicant()
                                .setSelf_Declared_Income_of_customer(fieldAssessedIncome);
                    }
						Header header = gson.fromJson(breReqObj.get("header"), Header.class);

						logger.debug("Initiating BRE CB Check with updated request");

						try {
							Object breResponse = performBlockingBreCBCheck(brecbRequest, header, prop);

							logger.debug("BRE CB Check finished with response: {}", breResponse);

                        } catch (Exception e) {
							logger.error("Exception while waiting for BRE CB Check: {}", e.getMessage(), e);
							throw new RuntimeException("BRE CB Check failed", e);
						}
					} else {
						logger.warn("BRE CB Check Request does not contain required fields");
						return isBRECheckDone;
					}
				} catch (Exception e) {
					logger.error("Error while parsing BRECBRequest", e);
				throw new RuntimeException("BRE CB Check failed", e);
				}
			} else {
				logger.warn("CibilDetails not found for ApplicationId: {}, AppId: {}, CustomerDetailsId: {}", applicationId, appId, customerDetailsId);
				return  isBRECheckDone;
			}
		logger.debug("Exit updateLoanAmountAndBRECheck method");
		isBRECheckDone = true;
		return isBRECheckDone;

	}

    private boolean retryBRECall(String stageId, UploadLoanRequestFields requestObj, Properties prop) {
        boolean isBRECheckDone = false;
        logger.debug("Entry updateLoanAmountAndBRECheck method, StageID: {}, RequestObj: {}", stageId, requestObj);
        String applicationId = requestObj.getApplicationId();
        String appId = requestObj.getAppId();
        int versionNum = requestObj.getVersionNum();
        Gson gson = new Gson();

        BigDecimal updatedLoanAmt = requestObj.getUpdatedLoanAmount();
        BigDecimal customerDetailsId = requestObj.getCustomerDtlsId();
        logger.debug("Updated Loan Amount: {}, Customer Details ID: {}", updatedLoanAmt, customerDetailsId);
        Optional<CibilDetails> cibilDetailsOpt = cibilDtlRepo.findByApplicationIdAndAppIdAndCustDtlId(applicationId, appId, customerDetailsId);
        if (cibilDetailsOpt.isPresent()) {
            logger.debug("CibilDetails found for ApplicationId: {}, AppId: {}, CustomerDetailsId: {}", applicationId, appId, customerDetailsId);
            CibilDetails cibilDetails = cibilDetailsOpt.get();
            String breCBCheckReq = cibilDetails.getRequest();
            String breCbPayload = cibilDetails.getPayloadColumn();
            logger.debug("BRE CB Check Request: {}", breCBCheckReq);

            try {
                JsonObject breReqObj = gson.fromJson(breCBCheckReq, JsonObject.class);
                if (breReqObj.has("brecbRequest") && breReqObj.has("header")) {
                    logger.debug("BRE CB Check Request contains required fields");
                    BRECBRequest brecbRequest = gson.fromJson(breReqObj.get("brecbRequest"), BRECBRequest.class);
                    brecbRequest.getRequestObj()
                            .getBreCBValuesRequestvalues1()
                            .getBreCBInputRequestinput1()
                            .getBreCBValuesRequestvalues2()
                            .getBreCBInputRequestinput2()
                            .getApplicant()
                            .setLoanAmount(updatedLoanAmt.toString());
                    brecbRequest.getRequestObj()
                            .getBreCBValuesRequestvalues1()
                            .getBreCBInputRequestinput1()
                            .getBreCBValuesRequestvalues2()
                            .getBreCBInputRequestinput2().setCaglOs(requestObj.getCaglOs());
                    brecbRequest.getRequestObj()
                            .getBreCBValuesRequestvalues1()
                            .getBreCBInputRequestinput1()
                            .getBreCBValuesRequestvalues2()
                            .getBreCBInputRequestinput2().setCurrentStage(requestObj.getCurrentStage());
                    String selfDeclaredIncome = brecbRequest.getRequestObj().getBreCBValuesRequestvalues1()
                            .getBreCBInputRequestinput1().getBreCBValuesRequestvalues2()
                            .getBreCBInputRequestinput2().getApplicant().getSelf_Declared_Income_of_customer();
                    String fieldAssessedIncome = brecbRequest.getRequestObj().getBreCBValuesRequestvalues1()
                            .getBreCBInputRequestinput1().getBreCBValuesRequestvalues2()
                            .getBreCBInputRequestinput2().getApplicant().getField_Assessed_Income_of_customer();
                    if(null == selfDeclaredIncome){
                        brecbRequest.getRequestObj().getBreCBValuesRequestvalues1()
                                .getBreCBInputRequestinput1().getBreCBValuesRequestvalues2()
                                .getBreCBInputRequestinput2().getApplicant()
                                .setSelf_Declared_Income_of_customer(fieldAssessedIncome);
                    }
                    Header header = gson.fromJson(breReqObj.get("header"), Header.class);

                    logger.debug("Initiating BRE CB Check with updated request");

                    try {
                        Object breResponse = performBlockingBreCBCheck(brecbRequest, header, prop);

                        logger.debug("BRE CB Check finished with response: {}", breResponse);

						if ("9".equals(stageId) || "BRE".equals(stageId)) {
							logger.debug("Stage 9 retryBRECall - Updating insurance details from BRE response saved in Co-App CibilDetails");
							Optional<CibilDetails> updatedCibilOpt = cibilDtlRepo.findCibilDetailsByCustomerTypeAndApplicationId("Co-App", applicationId);
							if (!updatedCibilOpt.isPresent()) {
								logger.warn("Co-App CibilDetails not found, falling back to Applicant CibilDetails");
								updatedCibilOpt = cibilDtlRepo.findByApplicationIdAndAppIdAndCustDtlId(applicationId, appId, customerDetailsId);
							}
							if (StringUtils.isBlank(updatedCibilOpt.get().getRequest())) {

								logger.warn("Co-App BRE request is null/empty, falling back to Applicant CibilDetails");

								updatedCibilOpt = cibilDtlRepo.findByApplicationIdAndAppIdAndCustDtlId(
										applicationId, appId, customerDetailsId);
							}

							if (updatedCibilOpt.isPresent()) {
								CibilDetails updatedCibil = updatedCibilOpt.get();
								CibilDetailsPayload cblPayLoad = gson.fromJson(updatedCibil.getPayloadColumn(), CibilDetailsPayload.class);
								if (cblPayLoad != null) {
									String eligibleAmt = cblPayLoad.getEligibleAmt();
									String chargeMember = cblPayLoad.getInsuranceChargeMember();
									String chargeSpouse = cblPayLoad.getInsuranceChargeSpouse();
									String chargeJoint = cblPayLoad.getInsuranceChargeJoint();
									
									logger.debug("Stage 9: Found CibilDetails with payload. eligibleAmt: {}, chargeMember: {}, chargeSpouse: {}, chargeJoint: {}", 
											eligibleAmt, chargeMember, chargeSpouse, chargeJoint);
									
									BigDecimal eligibleAmtBD = BigDecimal.ZERO;
									if (eligibleAmt != null && !eligibleAmt.trim().isEmpty() && !"null".equalsIgnoreCase(eligibleAmt.trim())) {
										eligibleAmtBD = new BigDecimal(eligibleAmt.replaceAll("[^\\d.]", ""));
									}
//									if (eligibleAmtBD.compareTo(new BigDecimal("200000")) > 0) {
//										eligibleAmtBD = new BigDecimal("200000");
//									}
									String formattedLoanAmt = CommonUtils.amountFormat(eligibleAmtBD.toString());
									if (formattedLoanAmt.endsWith(".00")) {
										formattedLoanAmt = formattedLoanAmt.substring(0, formattedLoanAmt.length() - 3);
									}
									
									Optional<List<InsuranceDetails>> dbInsuranceListOpt = insuranceDtlRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
									if (dbInsuranceListOpt.isPresent()) {
										List<InsuranceDetails> dbInsuranceList = dbInsuranceListOpt.get();
										for (InsuranceDetails dbIns : dbInsuranceList) {
											try {
												InsuranceDetailsPayload dbPayload = gson.fromJson(dbIns.getPayloadColumn(), InsuranceDetailsPayload.class);
												if (dbPayload != null) {
													dbPayload.setLoanAmount(formattedLoanAmt);
													
													String custType = dbIns.getInsuredCustType();
													String insuranceType = dbIns.getInsuranceType();
													BigDecimal mPrem = BigDecimal.ZERO;
													BigDecimal sPrem = BigDecimal.ZERO;
													if (chargeMember != null && !chargeMember.trim().isEmpty()) {
														mPrem = new BigDecimal(chargeMember.replaceAll("[^\\d.]", ""));
													}
													if (chargeSpouse != null && !chargeSpouse.trim().isEmpty()) {
														sPrem = new BigDecimal(chargeSpouse.replaceAll("[^\\d.]", ""));
													}
													BigDecimal finalPremium = BigDecimal.ZERO;
													if ("bothInsurance".equalsIgnoreCase(insuranceType)) {
														finalPremium = ("Applicant".equalsIgnoreCase(custType)
																|| "Co-App".equalsIgnoreCase(custType)
																|| "Cust-Nominee".equalsIgnoreCase(custType))
																? mPrem.add(sPrem)
																: mPrem;
													} else {
														switch (custType) {
															case "Applicant":
														finalPremium = mPrem;
																break;
															case "Co-App":
														finalPremium = sPrem;
																break;
															case "Cust-Nominee":
														finalPremium = mPrem.add(sPrem);
																break;
															default:
														finalPremium = mPrem;
													}
													}
													String formattedPrem = CommonUtils.amountFormat(finalPremium.toString());
													if (formattedPrem.endsWith(".00")) {
														formattedPrem = formattedPrem.substring(0, formattedPrem.length() - 3);
													}
													dbPayload.setInsurancePremium(formattedPrem);
													
													dbIns.setPayloadColumn(gson.toJson(dbPayload));
													insuranceDtlRepo.save(dbIns);
													logger.debug("Successfully updated and saved InsuranceDetails DB payload in retryBRECall from BRE values for application: {} and custType: {}, premium: {}", applicationId, custType, formattedPrem);
												}
											} catch (Exception e) {
												logger.error("Error updating DB insurance details inside retryBRECall inner block: {}", e.getMessage(), e);
											}
										}
									}
								}
							}
						}

                    } catch (Exception e) {
                        logger.error("Exception while waiting for BRE CB Check: {}", e.getMessage(), e);
						throw new RuntimeException(e.getMessage(), e);
                    }
                } else {
                    logger.warn("BRE CB Check Request does not contain required fields");
                    return isBRECheckDone;
                }
            } catch (Exception e) {
                logger.error("Error while parsing BRECBRequest", e);
				throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            logger.warn("CibilDetails not found for ApplicationId: {}, AppId: {}, CustomerDetailsId: {}", applicationId, appId, customerDetailsId);
            return isBRECheckDone;
        }
        logger.debug("Exit updateLoanAmountAndBRECheck method");
        isBRECheckDone = true;
        return isBRECheckDone;
	}


	private Object performBlockingBreCBCheck(BRECBRequest brecbRequest, Header header, Properties prop) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		int timeout = Integer.parseInt(prop.getProperty(CobFlagsProperties.BRE_CHECK_THREAD_TIMEOUT.getKey(), String.valueOf(30)));
		Future<Object> future = executor.submit(() -> {
			return loanService.breCBCheck(brecbRequest, header, prop)
					.timeout(Duration.ofSeconds(timeout))
					.block(); // block safely in isolated thread
		});

		try {
			return future.get((long)timeout + 4, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			logger.error("BRE CB Check timed out", e);
			throw new RuntimeException("BRE CB Check timed out", e);
		}  catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		    logger.error("BRE CB Check interrupted", e);
		    throw new RuntimeException("BRE CB Check interrupted", e);
		} catch (Exception e) {
			Throwable cause = e;

			while (cause.getCause() != null && cause.getCause() != cause) {
				cause = cause.getCause();
			}

			if (cause instanceof TimeoutException) {
				logger.error("BRE CB Check timed out (reactive)", cause);
				throw new RuntimeException("BRE CB Check timed out", cause);
			}

			logger.error("BRE CB Check failed", e);
			throw new RuntimeException("BRE CB Check failed", e);
		} finally {
			executor.shutdown();
		}
	}



    private void updateLoanAmount(String stageId, UploadLoanRequestFields requestObj){
		String applicationId = requestObj.getApplicationId();
		String appId = requestObj.getAppId();
		int versionNum = requestObj.getVersionNum();

		BigDecimal updatedLoanAmt = requestObj.getUpdatedLoanAmount();
		BigDecimal customerDetailsId = requestObj.getCustomerDtlsId();
		logger.debug("Updated Loan Amount: {}, Customer Details ID: {}", updatedLoanAmt, customerDetailsId);

		LoanDetails loanDetails = loanDtlsRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
		logger.debug("Fetched LoanDetails: {}", loanDetails);

		if (Constants.SANCTION.equalsIgnoreCase(stageId)) {
			logger.debug("Processing SANCTION stage");
			if(loanDetails.getSanctionedLoanAmount() == null){
				loanDetails.setSanctionedLoanAmount(updatedLoanAmt);
			}else{
				loanDetails.setOldSanctionRecommendedLoanAmount(loanDetails.getSanctionedLoanAmount());
				loanDetails.setSanctionedLoanAmount(updatedLoanAmt);
			}
		} else {
			logger.debug("Processing non-SANCTION stage");
			if(loanDetails.getBmRecommendedLoanAmount() == null){
				loanDetails.setBmRecommendedLoanAmount(updatedLoanAmt);
			}else{
				loanDetails.setOldBmRecommendedLoanAmount(loanDetails.getBmRecommendedLoanAmount());
				loanDetails.setBmRecommendedLoanAmount(updatedLoanAmt);
			}
			
		}

		loanDtlsRepo.save(loanDetails);
		logger.debug("Loan Amount updated in loanDetails table: {}", loanDetails);
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
