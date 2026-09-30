package com.iexceed.appzillonbanking.cob.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iexceed.appzillonbanking.cob.cards.payload.FetchAppReq;
import com.iexceed.appzillonbanking.cob.cards.payload.FetchAppReqFields;
import com.iexceed.appzillonbanking.cob.cards.service.CreditCardService;
import com.iexceed.appzillonbanking.cob.constants.CommonConstants;
import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.core.repository.ab.*;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.core.services.InterfaceAdapter;
import com.iexceed.appzillonbanking.cob.core.utils.*;
import com.iexceed.appzillonbanking.cob.deposit.service.DepositService;
import com.iexceed.appzillonbanking.cob.domain.ab.WhitelistedBranches;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiCoApplicantDetails;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedCDHLead;
import com.iexceed.appzillonbanking.cob.loans.payload.*;
import com.iexceed.appzillonbanking.cob.loans.service.LoanService;
import com.iexceed.appzillonbanking.cob.loans.service.LucService;
import com.iexceed.appzillonbanking.cob.loans.service.T24AndCDHService;
import com.iexceed.appzillonbanking.cob.nesl.repository.ab.EnachRepository;
import com.iexceed.appzillonbanking.cob.payload.*;
import com.iexceed.appzillonbanking.cob.repository.ab.WhitelistedBranchesRepository;
import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiCoApplicantDetailsRepository;
import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiIexceedCDHLeadRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class CommonService {

    private static final Logger logger = LogManager.getLogger(CommonService.class);

    @Autowired
    private COBService cobService;

    @Autowired
    private DepositService depositService;

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
    private LucService lucService;

    @Autowired
    private LucRepository lucRepository;

    @Autowired
    private SourcingStageVerificationRepository srcStageVerificationRepo;

    @Autowired
    private InsuranceDetailsRepository insuranceDetailsRepo;


    @Autowired
    private InsuranceDetailsHisRepository insuranceDetailsHisRepo;

    @Autowired
    private UnnatiIexceedCDHLeadRepo unnatiIexceedCDHLeadRepo;

    @Autowired
    private UnnatiCoApplicantDetailsRepository unnatiCoApplicantDetailsRepository;

    @Autowired
    private ApplicationMasterRepository2 applicationMasterRepository2;

    @Autowired
    private T24AndCDHService t24AndCDHService;

    @Autowired
    UnnatiCoApplicantDetailsRepository coAppDetailsRepository;

    public boolean isAdditonalProductApplication(String applicationId){
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepository.findByApplicationId(applicationId);
        if(masterObjDb.isPresent()){
            ApplicationMaster masterObj = masterObjDb.get();
            Set<ProductCode> additionalProducts = EnumSet.of(ProductCode.FAMILY_WELFARE, ProductCode.UNNATI_SUPPLEMENTARY,
                    ProductCode.UNNATI_RESTART, ProductCode.UNNATI_EMERGENCY);
            return additionalProducts.stream()
                    .anyMatch(productCode1 -> productCode1.getUnnatiCode().equalsIgnoreCase(masterObj.getProductCode()));
        }else{
            logger.error("ApplicationMaster not found for applicationId: {}", applicationId);
            return false;
        }
    }

    public Mono<Response> deleteProspectCustomerOnRejection(FetchDeleteUserRequest fetchDeleteUserRequest, Header header, Properties prop) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        response.setResponseHeader(responseHeader);
        FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest
                .getRequestObj();
        String applicationId = customerDataFields.getApplicationId();
        boolean isCaseSanctionedBefore = applicationWorkflowRepository
                .existsByApplicationIdAndApplicationStatus(applicationId, AppStatus.SANCTIONED.getValue());
        if (isCaseSanctionedBefore) {
            responseBody.setResponseObj("Application has been sanctioned before, no need to delete prospect customer.");
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
            return Mono.just(response);
        }
        String status = customerDataFields.getStatus();
        if (AppStatus.REJECTED.getValue().equalsIgnoreCase(status)) {
            Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
                    .findByAppIdAndApplicationIdAndVersionNum(customerDataFields.getAppId(),
                            customerDataFields.getApplicationId(), customerDataFields.getVersionNum());
            if (masterObjDb.isPresent()) {
                ApplicationMaster masterObj = masterObjDb.get();
                return t24AndCDHService.deleteProspectCustomerData(masterObj, Constants.APPID, header, prop)
                        .map(deleteResponse -> {
                            Response outerResponse = new Response();
                            ResponseHeader outerHeader = new ResponseHeader();
                            ResponseBody outerBody = new ResponseBody();

                            boolean deleteSucceeded = ResponseCodes.SUCCESS.getKey()
                                    .equalsIgnoreCase(deleteResponse.getResponseHeader().getResponseCode());

                            if (deleteSucceeded) {
                                logger.debug("Prospect customer data deleted successfully for applicationId: {}",
                                        customerDataFields.getApplicationId());
                                outerHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                                outerHeader.setResponseMessage(deleteResponse.getResponseHeader().getResponseMessage());
                            } else {
                                logger.error("Failed to delete prospect customer data for applicationId: {}. Reason: {}",
                                        customerDataFields.getApplicationId(), deleteResponse.getResponseHeader().getResponseMessage());
                                outerHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                                outerHeader.setResponseMessage(deleteResponse.getResponseHeader().getResponseMessage());
                                outerBody.setResponseObj(deleteResponse.getResponseBody().getResponseObj());
                            }
                            outerResponse.setResponseHeader(outerHeader);
                            outerResponse.setResponseBody(outerBody);
                            return outerResponse;
                        });
            } else {
                responseBody.setResponseObj(ResponseCodes.INVALID_APP_MASTER.getValue());
                responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            }
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
        }
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return Mono.just(response);
    }


    private static boolean isValidStatus(String status, Set<AppStatus> allowedStatuses) {
        if (CommonUtils.isNullOrEmpty(status)) {
            return false;
        }
        for (AppStatus appStatus : allowedStatuses) {
            if (appStatus.getValue().equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    public Mono<Object> checkAndRejectActiveLoans(FetchDeleteUserRequest fetchDeleteUserRequest, Header header, Properties prop){

        String currentStatus = fetchDeleteUserRequest.getRequestObj().getStatus();
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNum(fetchDeleteUserRequest.getRequestObj().getAppId(),
                        fetchDeleteUserRequest.getRequestObj().getApplicationId(), fetchDeleteUserRequest.getRequestObj().getVersionNum());
        if(!masterObjDb.isPresent()){
            Response response = new Response();
            ResponseHeader responseHeader = new ResponseHeader();
            ResponseBody responseBody = new ResponseBody();
            response.setResponseHeader(responseHeader);
            response.setResponseBody(responseBody);
            responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            return Mono.just(response);
        }
        ApplicationMaster masterObj = masterObjDb.get();
        String applicationId = masterObj.getApplicationId();
        String t24LoanId = loanDtlsRepo.fetchT24LoanIdByApplicationId(applicationId);
        if(StringUtils.isBlank(t24LoanId)){
            logger.error("Loan Id not found for application id : {}, hence skipping loan rejection", applicationId);
            return Mono.empty();
        }
        Mono<Object> loanRejection = this.t24AndCDHService.loanRejection(masterObj.getApplicationId(), prop,
                masterObj.getAppId(), header);
        return loanRejection.flatMap(loanResponse -> {
            logger.debug("Loan Rejection API response {}", loanResponse);
            try {
                String json = (new Gson()).toJson(loanResponse);
                JSONObject apiResp = convertResponseToJson(json);
                Object requestObj = apiResp.get(Constants.API_REQUEST);
                JSONObject loanHeader = apiResp.getJSONObject(Constants.HEADER);
                if (loanHeader.has(Constants.STATUS)
                        && loanHeader.getString(Constants.STATUS).equalsIgnoreCase(ResponseCodes.SUCCESS.getValue())) {
                    // Saving log
                    saveLog(applicationId, Constants.LOAN_REJECTION, requestObj.toString(),
                            loanResponse.toString(), ResponseCodes.SUCCESS.getValue(), null, currentStatus);
                    loanDtlsRepo.updateT24LoanStatus(applicationId, Constants.INACTIVE);
                    logger.debug("Loan Rejection API successful for application id : {}, " +
                            "hence updating loan status to INACTIVE", applicationId);
                    return Mono.empty();
                } else if (apiResp.has(Constants.ERROR1) && !apiResp.getJSONObject(Constants.ERROR1).isEmpty()) {
                    JSONArray loanErrors = apiResp.getJSONObject(Constants.ERROR1).getJSONArray(Constants.ERROR_DETAILS);
                    logger.debug("LoanErrors " + loanErrors);
                    List<String> loanErrorList = new ArrayList<>();
                    for (Object field : loanErrors) {
                        JSONObject fieldError = new JSONObject(field.toString());
                        logger.debug("Field Error " + fieldError);
                        loanErrorList.add(
                                fieldError.optString(Constants.FIELD_NAME, Constants.ERROR2) + " - " + fieldError.getString(Constants.MESSAGE).trim());
                    }
                    logger.debug("Error List in Loan Rejection API {}", loanErrorList);
                    saveLog(applicationId, Constants.LOAN_REJECTION, requestObj.toString(),
                            loanResponse.toString(), ResponseCodes.FAILURE.getValue(), loanErrorList.toString(), currentStatus);
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
            saveLog(applicationId, Constants.LOAN_REJECTION, this.t24AndCDHService.getRequestLog(), null,
                    ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStatus);
            return Mono.just(getFailureApiJson("Error during Loan Rejection", Constants.LOAN_REJECTION));
        });
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "approveRejectApplicationFallback")
    public Mono<Response> approveRejectApplication(
            FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
            boolean isSelfOnBoardingHeaderAppId, Properties prop, String roleId) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        response.setResponseHeader(responseHeader);
        FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest
                .getRequestObj();
        String status = customerDataFields.getStatus();
        if (!Constants.BM.equalsIgnoreCase(roleId) &&
                !Constants.APPROVER.equalsIgnoreCase(roleId)
                && !Constants.INITIATOR.equalsIgnoreCase(roleId) && !Constants.KM.equalsIgnoreCase(roleId)) {
            responseHeader.setResponseCode(ResponseCodes.INVALID_ROLE.getKey());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
            return Mono.just(response);
        }
        int applicationDocCount = applicationDocumentsRepository.fetchApplicationDocsCountByAppIdAndApplicationId(
                customerDataFields.getAppId(),
                customerDataFields.getApplicationId(),
                new ArrayList<>(Arrays.asList(Constants.APPLICANT, Constants.COAPPLICANT))
        );
        if(!AppStatus.REJECTED.getValue().equalsIgnoreCase(status)) {
            if (applicationDocCount < 5) { // Assuming 5 documents are mandatory, this can be changed as per requirement
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                responseBody.setResponseObj("All required documents have not been uploaded. Please upload all documents to proceed.");
                response.setResponseBody(responseBody);
                response.setResponseHeader(responseHeader);
                return Mono.just(response);
            }
        }
        Set<AppStatus> validStatuses = EnumSet.of(
                AppStatus.APPROVED,
                AppStatus.REJECTED,
                AppStatus.PUSHBACK,
                AppStatus.RPCVERIFIED,
                AppStatus.IPUSHBACK,
                AppStatus.PENDING, AppStatus.BMPUSHBACK
        );
        if (isValidStatus(status, validStatuses)) {
            List<String> applnStatus = new ArrayList<>();
            applnStatus.add(AppStatus.PENDING.getValue());
            applnStatus.add(AppStatus.INPROGRESS.getValue());
            applnStatus.add(AppStatus.PUSHBACK.getValue());
            applnStatus.add(AppStatus.CAPUSHBACK.getValue());
            applnStatus.add(AppStatus.IPUSHBACK.getValue());
            applnStatus.add(AppStatus.BMPUSHBACK.getValue());

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
                AtomicReference<String> accNum = new AtomicReference<>();
                AtomicReference<BigDecimal> customerId = new AtomicReference<>();
                Gson gson = new Gson();
                CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
                if (AppStatus.APPROVED.getValue().equalsIgnoreCase(status)
                        && Constants.INITIATOR.equalsIgnoreCase(roleId)) {
                    boolean isDedupeUpdatedEnabled = (prop.getProperty(CobFlagsProperties.IS_SOURCING_DEDUPEUPDATE_ENABLED.getKey()))
                            .equalsIgnoreCase("Y") ? true : false;
                    if (isDedupeUpdatedEnabled) {
//                        Mono<Object> t24CDHUpdateApplicantResp = this.t24AndCDHService
//                                .dedupeUpdateT24(masterObj.getSearchCode2(),
//                                        masterObj.getApplicationId(),
//                                        masterObj.getAppId(), masterObj.getProductCode(), header, prop, 1);
//                        return t24CDHUpdateApplicantResp.flatMap(resp -> {
//                            logger.debug("Received response from T24 update for applicationID: {}. Response: {}",
//                                    masterObj.getApplicationId(), resp);
//                            JSONObject jsonResp = (JSONObject) resp;
//                            String errorCode = jsonResp.optString(Constants.ERRORCODE);
//                            String errorMessage = jsonResp.optString(Constants.ERRORMESSAGE);
//                            if (!"0".equals(errorCode)) {
//                                Response errorResponse = new Response();
//                                ResponseHeader headerResp = new ResponseHeader();
//                                ResponseBody bodyResp = new ResponseBody();
//                                headerResp.setResponseCode(errorCode);
//                                bodyResp.setResponseObj(errorMessage);
//                                errorResponse.setResponseHeader(headerResp);
//                                errorResponse.setResponseBody(bodyResp);
//                                logger.debug("Error response from T24 update for applicationID: {}. ErrorCode: {}, Response: {}", masterObj.getApplicationId(), errorCode, jsonResp.toString());
//                                return Mono.just(errorResponse);
//                            }

                        return this.t24AndCDHService.dedupeUpdateCDH(masterObj.getSearchCode2(),
                                masterObj.getApplicationId(), masterObj.getAppId(),
                                masterObj.getProductCode(),
                                header, prop, Constants.APPLICANT).flatMap( resp -> {
                            JSONObject firstJson = (JSONObject) resp;
                            String firstErrorCode = firstJson.optString(Constants.ERRORCODE);
                            String firstErrorMessage = firstJson.optString(Constants.ERRORMESSAGE);
                            if (!"0".equals(firstErrorCode)) {
                                Response errorResponse = new Response();
                                ResponseHeader headerResp = new ResponseHeader();
                                ResponseBody bodyResp = new ResponseBody();
                                headerResp.setResponseCode(firstErrorCode);
                                bodyResp.setResponseObj(firstErrorMessage);
                                errorResponse.setResponseHeader(headerResp);
                                errorResponse.setResponseBody(bodyResp);
                                logger.debug("Error response from CDH update for applicationID: {}. ErrorCode: {}, Response: {}",
                                        masterObj.getApplicationId(), firstErrorCode, firstJson.toString());
                                return Mono.just(errorResponse);
                            }
                            String coApplicantId = Optional.ofNullable(loanDtlsRepo.findByApplicationId(masterObj.getApplicationId()))
                                    .map(LoanDetails::getCoapplicantId)
                                    .orElse("");//send blank for prospect Id
                            return this.t24AndCDHService.dedupeUpdateCDH(coApplicantId,
                                            masterObj.getApplicationId(), masterObj.getAppId(),
                                            masterObj.getProductCode(),
                                            header, prop, Constants.COAPPLICANT)
                                    .flatMap(secondResp -> {
                                        logger.debug("Received response from CDH update for applicationID: {}. Response: {}", masterObj.getApplicationId(), secondResp);
                                        JSONObject secondJson = (JSONObject) secondResp;
                                        String secondErrorCode = secondJson.optString(Constants.ERRORCODE);
                                        String secondErrorMessage = secondJson.optString(Constants.ERRORMESSAGE);
                                        if (!"0".equals(secondErrorCode)) {
                                            Response errorResponse = new Response();
                                            ResponseHeader headerResp = new ResponseHeader();
                                            ResponseBody bodyResp = new ResponseBody();
                                            headerResp.setResponseCode(secondErrorCode);
                                            bodyResp.setResponseObj(secondErrorMessage);
                                            errorResponse.setResponseHeader(headerResp);
                                            errorResponse.setResponseBody(bodyResp);
                                            logger.debug("Error response from CDH update for applicationID: {}. ErrorCode: {}, Response: {}",
                                                    masterObj.getApplicationId(), secondErrorCode, secondJson.toString());
                                            return Mono.just(errorResponse);
                                        } else {
                                            if (AppStatus.APPROVED.getValue().equalsIgnoreCase(status)) {
                                                accNum.set(CommonUtils.generateRandomNumStr());
                                                customerId.set(CommonUtils.generateRandomNum());
                                                masterObj.setAccNumber(accNum.get());
                                                masterObj.setCustomerId(customerId.get());
                                                customerIdentification.setAccNumber(accNum.get());
                                                customerIdentification.setCustomerId(customerId.toString());
                                            }
                                            masterObj.setRemarks(null);
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
                                            Optional<SourcingResponseTracker> sourcingResponseTrackerOptional = sourcingResponseTrackerRepository.findById(masterObj.getApplicationId());
                                            sourcingResponseTrackerOptional.ifPresent(sourcingResponseTracker -> sourcingResponseTrackerRepository.delete(sourcingResponseTracker));
                                            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                                            customerIdentification.setVersionNum(customerDataFields.getVersionNum());
                                            responseBody.setResponseObj(gson.toJson(customerIdentification));
                                            response.setResponseBody(responseBody);
                                            response.setResponseHeader(responseHeader);
                                            logger.debug("application status update completed");
                                            boolean isIexceedFlag = "I".equalsIgnoreCase(masterObj.getDeclarationFlag());
                                            if (AppStatus.APPROVED.getValue().equalsIgnoreCase(status) && !isIexceedFlag) {
                                                Optional<WhitelistedBranches> optionalBranch =
                                                        whitelistedBranchesRepo.findByBranchCode(masterObj.getBranchId());
                                                if (optionalBranch.isPresent()) {
                                                    WhitelistedBranches branch = optionalBranch.get();
                                                    String productCode = masterObj.getProductCode();
                                                    boolean enableFlag = false;
                                                    switch (productCode) {
                                                        case Constants.UNNATI_PRODUCT_CODE:
                                                            enableFlag = "Y".equalsIgnoreCase(branch.getUnnatiEnabled());
                                                            break;
                                                        case Constants.RENEWAL_PRODUCT_CODE:
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
                                            response.setResponseBody(responseBody);
                                            response.setResponseHeader(responseHeader);
                                            return Mono.just(response);
                                        }
                                    });
                        });
//                        });
                    }
                }
                if (AppStatus.APPROVED.getValue().equalsIgnoreCase(status)) {
                    accNum.set(CommonUtils.generateRandomNumStr());
                    customerId.set(CommonUtils.generateRandomNum());
                    masterObj.setAccNumber(accNum.get());
                    masterObj.setCustomerId(customerId.get());
                    customerIdentification.setAccNumber(accNum.get());
                    customerIdentification.setCustomerId(customerId.toString());
                }
                masterObj.setRemarks(null);
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
                if ((AppStatus.BMPUSHBACK.getValue().equalsIgnoreCase(status)
                        && StringUtils.isEmpty(customerDataFields.getRemarks()))){
                    Optional<SorucingStageVerification> verificationData = srcStageVerificationRepo
                            .findById(customerDataFields.getApplicationId());
                    if (verificationData.isPresent()) {
                        SorucingStageVerification data = verificationData.get();
                        wf.setRemarks(data.getQueries());
//                        data.setVerifiedStages(null);
                        data.setQueries(null);
                        data.setBmVerifiedStages(null);
                        srcStageVerificationRepo.save(data);
                    }
                }
                logger.debug("req :" + req.toString());
                commonCoreService.populateApplnWorkFlow(req);
                Optional<SourcingResponseTracker> sourcingResponseTrackerOptional = sourcingResponseTrackerRepository.findById(masterObj.getApplicationId());
                sourcingResponseTrackerOptional.ifPresent(sourcingResponseTracker -> sourcingResponseTrackerRepository.delete(sourcingResponseTracker));
                responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                customerIdentification.setVersionNum(customerDataFields.getVersionNum());
                responseBody.setResponseObj(gson.toJson(customerIdentification));
                response.setResponseBody(responseBody);
                response.setResponseHeader(responseHeader);
                logger.debug("application status update completed");
                boolean isIexceedFlag = "I".equalsIgnoreCase(masterObj.getDeclarationFlag());
                if (AppStatus.APPROVED.getValue().equalsIgnoreCase(status) && !isIexceedFlag) {
                    Optional<WhitelistedBranches> optionalBranch =
                            whitelistedBranchesRepo.findByBranchCode(masterObj.getBranchId());
                    if (optionalBranch.isPresent()) {
                        WhitelistedBranches branch = optionalBranch.get();
                        String productCode = masterObj.getProductCode();
                        boolean enableFlag = false;
                        switch (productCode) {
                            case Constants.UNNATI_PRODUCT_CODE:
                                enableFlag = "Y".equalsIgnoreCase(branch.getUnnatiEnabled());
                                break;
                            case Constants.RENEWAL_PRODUCT_CODE:
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
                responseBody.setResponseObj(Constants.APPLICATION_UPDATED_ERROR);
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
        String action = customerDataFields.getWorkFlow().getAction();
        if (!CobFlagsProperties.RPC.getKey().equalsIgnoreCase(roleId)) {
            responseHeader.setResponseCode(ResponseCodes.INVALID_ROLE.getKey());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
            return Mono.just(response);
        }
        int applicationDocCount = applicationDocumentsRepository.fetchApplicationDocsCountByAppIdAndApplicationId(
                customerDataFields.getAppId(),
                customerDataFields.getApplicationId(),
                new ArrayList<>(Arrays.asList(Constants.APPLICANT, Constants.COAPPLICANT))
        );
        if(!AppStatus.REJECTED.getValue().equalsIgnoreCase(status)) {
            if (applicationDocCount < 5) { // Assuming 5 documents are mandatory, this can be changed as per requirement
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                responseBody.setResponseObj("All required documents have not been uploaded. Please upload all documents to proceed.");
                response.setResponseBody(responseBody);
                response.setResponseHeader(responseHeader);
                return Mono.just(response);
            }
        }
        Set<AppStatus> validStatuses = EnumSet.of(
                AppStatus.IPUSHBACK,
                AppStatus.REJECTED,
                AppStatus.PENDINGFORRPCVERIFICATION,
                AppStatus.RPCVERIFIED,
                AppStatus.RPCPUSHBACK
        );

        if (isValidStatus(status, validStatuses)) {
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
                if (!verificationData.isPresent() && (Constants.SUBMIT.equalsIgnoreCase(action)
                        || "APPROVE".equalsIgnoreCase(action))) {
                    responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                    responseBody.setResponseObj(ResponseCodes.MISSING_MANDATORY_FIELD.getValue());
                    response.setResponseBody(responseBody);
                    response.setResponseHeader(responseHeader);
                    return Mono.just(response);
                }
                logger.debug("Master data value present.");
                Gson gson = new Gson();
                CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
                ApplicationMaster masterObj = masterObjDb.get();
                if (Constants.SUBMIT.equalsIgnoreCase(action) || "APPROVE".equalsIgnoreCase(action)) {
                    boolean isDedupeUpdatedEnabled = (prop.getProperty(CobFlagsProperties.IS_SOURCING_DEDUPEUPDATE_ENABLED.getKey()))
                            .equalsIgnoreCase("Y") ? true : false;
                    if (isDedupeUpdatedEnabled) {
                        Header header = new Header();
//                        Mono<Object> t24CDHUpdateApplicantResp = this.t24AndCDHService
//                                .dedupeUpdateT24(masterObj.getSearchCode2(),
//                                        masterObj.getApplicationId(),
//                                        masterObj.getAppId(), masterObj.getProductCode(), header, prop, 1);
//                        return t24CDHUpdateApplicantResp.flatMap(resp -> {
//                            JSONObject jsonResp = (JSONObject) resp;
//                            String errorCode = jsonResp.optString(Constants.ERRORCODE);
//                            String errorMessage = jsonResp.optString(Constants.ERRORMESSAGE);
//                            if (!"0".equals(errorCode)) {
//                                return Mono.just(buildErrorResponse(errorCode, errorMessage));
//                            }
                        return this.t24AndCDHService.dedupeUpdateCDH(masterObj.getSearchCode2(),
                                masterObj.getApplicationId(), masterObj.getAppId(),
                                masterObj.getProductCode(),
                                header, prop, Constants.APPLICANT).flatMap( resp -> {
                            JSONObject firstJson = (JSONObject) resp;
                            String firstErrorCode = firstJson.optString(Constants.ERRORCODE);
                            String firstErrorMessage = firstJson.optString(Constants.ERRORMESSAGE);
                            if (!"0".equals(firstErrorCode)) {
                                Response errorResponse = new Response();
                                ResponseHeader headerResp = new ResponseHeader();
                                ResponseBody bodyResp = new ResponseBody();
                                headerResp.setResponseCode(firstErrorCode);
                                bodyResp.setResponseObj(firstErrorMessage);
                                errorResponse.setResponseHeader(headerResp);
                                errorResponse.setResponseBody(bodyResp);
                                logger.debug("Error response from CDH update for applicationID: {}. ErrorCode: {}, Response: {}",
                                        masterObj.getApplicationId(), firstErrorCode, firstJson.toString());
                                return Mono.just(errorResponse);
                            }

                            String coApplicantId = Optional.ofNullable(loanDtlsRepo.findByApplicationId(masterObj.getApplicationId()))
                                    .map(LoanDetails::getCoapplicantId)
                                    .orElse("");//send blank for prospect Id
                            return this.t24AndCDHService.dedupeUpdateCDH(
                                            coApplicantId,//send blank for prospectId
                                            masterObj.getApplicationId(),
                                            masterObj.getAppId(),
                                            masterObj.getProductCode(),
                                            header,
                                            prop,
                                            Constants.COAPPLICANT
                                    )
                                    .map(secondResp -> {
                                        JSONObject secondJson = (JSONObject) secondResp;
                                        String secondErrorCode = secondJson.optString(Constants.ERRORCODE);
                                        String secondErrorMessage = secondJson.optString(Constants.ERRORMESSAGE);
                                        if (!"0".equals(secondErrorCode)) {
                                            return buildErrorResponse(secondErrorCode, secondErrorMessage);
                                        }
                                        masterObj.setRemarks(null);
                                        masterObj.setUpdatedBy(customerDataFields.getUserId());
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
                    if(AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status)
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
                                        return buildSuccessResponse(secondJson.toString());
                                    });
                        });
//                        });
                    }
                }

                masterObj.setRemarks(null);
                masterObj.setUpdatedBy(customerDataFields.getUserId());
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

                logger.debug("RPC Pushback");
                if ( (AppStatus.RPCPUSHBACK.getValue().equalsIgnoreCase(status) || AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status))){
                    logger.debug("RPC Pushback SourcingStage Null");
                    Optional<SorucingStageVerification> verificationDataRPC = srcStageVerificationRepo
                            .findById(customerDataFields.getApplicationId());
                    if (verificationDataRPC.isPresent()) {
                        logger.debug("RPC Pushback SourcingStage Save");
                        SorucingStageVerification data = verificationDataRPC.get();
//                        data.setVerifiedStages(null);
                        data.setQueries(null);
                        data.setBmVerifiedStages(null);
                        srcStageVerificationRepo.save(data);
                    }
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
                responseBody.setResponseObj(Constants.APPLICATION_UPDATED_ERROR);
                responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            }
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
        }
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return Mono.just(response);
    }

    private Response buildErrorResponse(String errorCode, String message) {
        Response response = new Response();
        ResponseHeader header = new ResponseHeader();
        ResponseBody body = new ResponseBody();

        header.setResponseCode(errorCode);
        body.setResponseObj(message);

        response.setResponseHeader(header);
        response.setResponseBody(body);
        return response;
    }

    private Response buildSuccessResponse(String message) {
        Response response = new Response();
        ResponseHeader header = new ResponseHeader();
        ResponseBody body = new ResponseBody();

        header.setResponseCode(ResponseCodes.SUCCESS.getKey());
        body.setResponseObj(message);

        response.setResponseHeader(header);
        response.setResponseBody(body);
        return response;
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
        String action = customerDataFields.getWorkFlow().getAction();
        if (StringUtils.isBlank(action)
                || StringUtils.isBlank(status)
                || StringUtils.isBlank(roleId)) {
            responseHeader.setResponseCode(ResponseCodes.MISSING_MANDATORY_FIELD.getKey());
            responseBody.setResponseObj(ResponseCodes.MISSING_MANDATORY_FIELD.getValue());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
            return Mono.just(response);

        }
        if (!Constants.APPROVER.equalsIgnoreCase(roleId)
                && !Constants.BM.equalsIgnoreCase(roleId)
                && !Constants.BCM.equalsIgnoreCase(roleId)) {
            responseHeader.setResponseCode(ResponseCodes.INVALID_ROLE.getKey());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
            return Mono.just(response);
        }
        CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
        if (!AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status) &&
                !AppStatus.REJECTED.getValue().equalsIgnoreCase(status) &&
                !AppStatus.CAPUSHBACK.getValue().equalsIgnoreCase(status)) {
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
        Set<AppStatus> validStatuses = EnumSet.of(
                AppStatus.IPUSHBACK,
                AppStatus.REJECTED,
                AppStatus.CAPUSHBACK,
                AppStatus.PENDINGDEVIATION,
                AppStatus.PENDINGREASSESSMENT,
                AppStatus.PENDINGPRESANCTION
        );
        if (isValidStatus(status, validStatuses)) {
            String nextStage = AppStatus.PENDINGPRESANCTION.getValue();
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
            Gson gson = new Gson();
            if (masterObjDb.isPresent()) {
                ApplicationMaster masterObj = masterObjDb.get();
                if (Constants.SUBMIT.equalsIgnoreCase(action)
                        || Constants.RESANCTION.equalsIgnoreCase(action)
                        || Constants.SUBMITDEVIATION.equalsIgnoreCase(action)
                        || Constants.SUBMITREASSESSMENT.equalsIgnoreCase(action)) {
                    Optional<Response> loanAmountUpdateError = updateLoanDetailsWithBreEligibleAmount(
                            gson,
                            customerDataFields.getApplicationId(),
                                    Constants.COAPPLICANT,
                            true,
                            "Credit Assessment");
                    if (loanAmountUpdateError.isPresent()) {
                        logger.debug("Loan amount update response: {}", loanAmountUpdateError.get());
                        return Mono.just(loanAmountUpdateError.get());
                    }
                    List<String> insertionFlags = ValidateDeviation(masterObj, userId, prop);
                    if (insertionFlags.contains(Constants.ERROR)) {
                        responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                        response.setResponseBody(responseBody);
                        response.setResponseHeader(responseHeader);
                        return Mono.just(response);
                    } else if (insertionFlags.contains(Constants.CA_DEVIATION)) {
                        action = Constants.SUBMITDEVIATION;
                        WorkFlowDetails workFlowDetails = changeWorkFlowDetails(Constants.CREDITASSESSMENT, Constants.CREDITASSESSMENT, action);
                        customerDataFields.setWorkFlow(workFlowDetails);
                        status = workFlowDetails.getNextWorkflowStatus();
                        nextStage = Constants.CA_DEVIATION;
                    } else if (insertionFlags.contains(Constants.REASSESSMENT)) {
                        action = Constants.SUBMITREASSESSMENT;
                        WorkFlowDetails workFlowDetails = changeWorkFlowDetails(Constants.CREDITASSESSMENT, Constants.CREDITASSESSMENT, action);
                        customerDataFields.setWorkFlow(workFlowDetails);
                        status = workFlowDetails.getNextWorkflowStatus();
                        nextStage = Constants.REASSESSMENT;
                    }
                }
                customerIdentification.setNextStage(nextStage);
                logger.debug("Master data value present.");

                masterObj.setRemarks(null);
                masterObj.setUpdatedBy(customerDataFields.getUserId());
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
                        && StringUtils.isBlank(customerDataFields.getRemarks())) {
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
                if(AppStatus.CAPUSHBACK.getValue().equalsIgnoreCase(status) || AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status)){
                    Optional<SorucingStageVerification> srcVerificationData = srcStageVerificationRepo
                            .findById(customerDataFields.getApplicationId());
                    if (srcVerificationData.isPresent()) {
                        logger.debug("Sourcing Verification Data clearing for Application ID: {}", customerDataFields.getApplicationId());
                        SorucingStageVerification data = srcVerificationData.get();
//                        data.setVerifiedStages(null);
                        data.setQueries(null);
                        data.setBmVerifiedStages(null);
                        srcStageVerificationRepo.save(data);
                    }
                }
                if (StringUtils.isNotBlank(customerDataFields.getRemarks())) {
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
                logger.debug("deleted the record from BCMPIStageVerification table for application Id: {}", customerDataFields.getApplicationId());
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
                responseBody.setResponseObj(Constants.APPLICATION_UPDATED_ERROR);
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
        workFlowDef = workflowDefinitionRepository.findByWorkFlowIdAndFromStageIdAndAction(workFlowId, fromStageId, action);
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
            String cdhProductCode = ProductCode.getCdhCodeByUnnatiCode(masterObj.getProductCode());
            if(masterObj.getProductCode().equalsIgnoreCase(ProductCode.OPEN_MARKET.getUnnatiCode())){
                cdhProductCode = masterObj.getProductCode();
            }
            state = unnatiIexceedCDHLeadRepo.findStateByCustomerIdAndReferenceId(masterObj.getSearchCode2(), cdhProductCode, masterObj.getWorkitemNo());

            state = state.replaceAll("\\s+", "");
            List<ReassessmentMaster> reassessmentMaster = reassessmentMasterRepo
                    .findByProductAndActiveStatusAndState(masterObj.getProductCode(), Constants.YES, state.toUpperCase());
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
                        logger.error("Exception while processing ApplicationDocuments payload for id: {}. Exception: {}",
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

            Optional<BCMPIIncomeDetails> bcmpiIncomeDataOpt = bcmpiIncomeDetailsRepo.findById(masterObj.getApplicationId());
            if (bcmpiIncomeDataOpt.isPresent()) {
                incomeDetails = new Gson().fromJson(bcmpiIncomeDataOpt.get().getPayload(),
                        BCMPIIncomeDetailsWrapper.class);
                businessIncome = BCMPIIncomeDetailsWrapper.calculateBusinessIncome(incomeDetails.getBusiness(), Constants.CO_APPLICANT)
                        .add(BCMPIIncomeDetailsWrapper.calculateBusinessIncome(incomeDetails.getBusiness(), Constants.APPLICANT));
                wageIncome = BCMPIIncomeDetailsWrapper.calculateWageIncome(incomeDetails.getWage(), Constants.CO_APPLICANT)
                        .add(BCMPIIncomeDetailsWrapper.calculateWageIncome(incomeDetails.getWage(), Constants.APPLICANT));
                agriculturalIncome = BCMPIIncomeDetailsWrapper.calculateAgricultureIncome(incomeDetails.getAgriculture(), Constants.CO_APPLICANT)
                        .add(BCMPIIncomeDetailsWrapper.calculateAgricultureIncome(incomeDetails.getAgriculture(), Constants.APPLICANT));
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
                        logger.debug("D1 : Applicant with salaried/housewife/self-employed profile and co-applicant's owns a business");
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
                        logger.debug("D2 : Co-Applicant with salaried/housewife/self-employed profile and applicant's owns a business");
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
//                        for (OccupationDetails occupDetail : occupationDetails) {
//						OccupationDetailsPayload occupPayload = gson.fromJson(occupDetail.getPayloadColumn(),
//                                    OccupationDetailsPayload.class);
//                            if (StringUtils.isNotEmpty(occupPayload.getStreetVendor()) && "YES".equalsIgnoreCase(occupPayload.getStreetVendor())) {
//                                recordedDeviations.add(deviation);
//                                break;
//                            }
//                        }
//                        break;
                        logger.debug("D3 : Applicant/co-applicant with street vendor profiles");
                        Optional<BCMPIIncomeDetails> bcmpiIncomeDetailsOpt = bcmpiIncomeDetailsRepo
                                .findByApplicationIdAndAppId(masterObj.getApplicationId(), masterObj.getAppId());

                        if (bcmpiIncomeDetailsOpt.isPresent()) {
                            BCMPIIncomeDetails bcmpiIncomeDetails = bcmpiIncomeDetailsOpt.get();
                            if (StringUtils.isNotEmpty(bcmpiIncomeDetails.getPayload())) {
                                try {
                                    BCMPIIncomeDetailsWrapper bcmpiWrapper = gson.fromJson(
                                            bcmpiIncomeDetails.getPayload(),
                                            BCMPIIncomeDetailsWrapper.class
                                    );
                                    if (bcmpiWrapper != null
                                            && bcmpiWrapper.getBusiness() != null
                                            && bcmpiWrapper.getBusiness().getKirana() != null) {
                                        for (BCMPIIncomeDetailsWrapper.Kirana kiranaEntry : bcmpiWrapper.getBusiness().getKirana()) {
                                            if (StringUtils.isNotEmpty(kiranaEntry.getStreetVendor())
                                                    && "YES".equalsIgnoreCase(kiranaEntry.getStreetVendor())) {
                                recordedDeviations.add(deviation);
                                break;
                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.error("Error parsing BCMPIIncomeDetails payload for D3 deviation check, applicationId:{} - {}",
                                            masterObj.getApplicationId(), e.getMessage(), e);
                                }
                            }
                        } else {
                            logger.debug("No BCMPIIncomeDetails found for D3 check, applicationId:{}", masterObj.getApplicationId());
                        }
                        break;
                    case "D4":
                        // Applicant (Existing GL customer) with NO-HIT in Bureau
                        logger.debug("D4 : Applicant (Existing GL customer) with NO-HIT in Bureau");
                        // TBD
                        break;
                    case "D5":
                        // Applicant/Co-Applicant business stability norms not met i.e
                        // Applicant/Co-Applicant any business tenure less than 3 years
                        logger.debug("D5 : Applicant/Co-Applicant business stability norms not met i.e Applicant/Co-Applicant any business tenure less than 3 years");
                        for (OccupationDetails occupDetail : occupationDetails) {
                            OccupationDetailsPayload occupPayload = gson.fromJson(occupDetail.getPayloadColumn(),
                                    OccupationDetailsPayload.class);
                            if (StringUtils.isNotBlank(occupPayload.getBusinessEmpVintageYear()) && Integer.parseInt(occupPayload.getBusinessEmpVintageYear()) < 3) {
                                recordedDeviations.add(deviation);
                                break;
                            }
                        }
                        break;
                    case "D6":
                        // Applicant/Co-Applicant staying at Rented Residence and Residence address same
                        // as Business address
                        logger.debug("D6 : Applicant/Co-Applicant staying at Rented Residence and Residence address same as Business address");
                        if (appPersonalAddr != null && appOccupAddr != null) {
                        for (Address address : appPersonalAddr.getAddressList()) {
                            if (Constants.PRESENT.equalsIgnoreCase(address.getAddressType())
                                    && ALLOWED_RESIDENCE_TYPES_RENTED.contains(address.getResidenceOwnership()) && Constants.PRESENT
                                    .equalsIgnoreCase(appOccupAddr.getAddressList().get(0).getAddressSameAs())) {
                                recordedDeviations.add(deviation);
                                break;
                            }
                        }
                        }

                        if (coappPersonalAddr != null && coappOccupAddr != null) {
                        for (Address address : coappPersonalAddr.getAddressList()) {
                            if (Constants.PRESENT.equalsIgnoreCase(address.getAddressType())
                                    && ALLOWED_RESIDENCE_TYPES_RENTED.contains(address.getResidenceOwnership()) && Constants.PRESENT
                                    .equalsIgnoreCase(coappOccupAddr.getAddressList().get(0).getAddressSameAs())) {
                                recordedDeviations.add(deviation);
                                break;
                            }
                        }
                        }
                        break;
                    case "D7":
                        // Residence stability criteria not met - less than 3 years for rented residence
                        // and less than 2 years for own residence
                        logger.debug("D7 : Residence stability criteria not met - less than 3 years for rented residence and less than 2 years for own residence");
                        List<String> rentedStability = Arrays.asList(Constants.RENTEDADDRESSSTABILITY.split(","));
                        List<String> selfOwnedStability = Arrays.asList(Constants.OWNEDADDRESSSTABILITY.split(","));
                        if (appPersonalAddr != null) {
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
                        }

                        if (coappPersonalAddr != null) {
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
                        }
                        break;
                    case "D8":
                        // Applicant existing loans start date should be greater than 6 months. Top up
                        // loans need not be considered.
                        logger.debug("D8 : Applicant existing loans start date should be greater than 6 months. Top up loans need not be considered.");
                        // TBD
                        break;
                    case "D9":
                        // Extended RF loans to active CA Grameen customers (waiting borrowers of GL)
                        // with >12 and <24 months gap
                        logger.debug("D9 : Extended RF loans to active CA Grameen customers (waiting borrowers of GL) with >12 and <24 months gap");
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
                        logger.debug("inside loanDtls.get(0).getBmRecommendedLoanAmount() " + loanDtls.get(0).getBmRecommendedLoanAmount());
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
                            if (null != cbDtlPayload.getFoirPercentage() && new BigDecimal(cbDtlPayload.getFoirPercentage()).compareTo(minval) > 0) {
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
                        if (wageIncome.compareTo(businessIncome) > 0 || agriculturalIncome.compareTo(businessIncome) > 0) {
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
                                        && ALLOWED_RESIDENCE_TYPES_RENTED.contains(coAppAddress.getResidenceOwnership())) {
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
                    if (reassessmentRecord.getBcm().equalsIgnoreCase("Y")) {
                        deviationRATracker.setAuthority(Constants.BCM);
                        deviationRATracker.setApprovedStatus(Constants.PENDING);
                    } else if (reassessmentRecord.getAcm().equalsIgnoreCase("Y")) {
                        deviationRATracker.setAuthority(Constants.ACM);
                        deviationRATracker.setApprovedStatus(Constants.PENDING);
                    } else if (reassessmentRecord.getAm().equalsIgnoreCase("Y")) {
                        deviationRATracker.setAuthority(Constants.AM);
                        deviationRATracker.setApprovedStatus(Constants.PENDING);
                    } else if (reassessmentRecord.getCcm().equalsIgnoreCase("Y")) {
                        deviationRATracker.setAuthority(Constants.CCM);
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

            logger.error("Error in ValidateDeviation : {}",e);
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
        String action = customerDataFields.getWorkFlow().getAction();
        if (!Constants.BCM.equalsIgnoreCase(roleId)
                && !Constants.ACM.equalsIgnoreCase(roleId)
                && !Constants.CCM.equalsIgnoreCase(roleId)) {
            responseHeader.setResponseCode(ResponseCodes.INVALID_ROLE.getKey());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
            return Mono.just(response);
        }
        CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
        Set<AppStatus> validStatuses = EnumSet.of(
                AppStatus.PENDINGREASSESSMENT,
                AppStatus.REJECTED,
                AppStatus.RPCVERIFIED,
                AppStatus.CACOMPLETED,
                AppStatus.PENDINGPRESANCTION
        );
        if (isValidStatus(status, validStatuses)) {
            List<String> applnStatus = new ArrayList<>();
        String nextStage = AppStatus.PENDINGPRESANCTION.getValue();
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
                if (AppStatus.PENDINGPRESANCTION.getValue().equalsIgnoreCase(status) || (Constants.SUBMIT.equalsIgnoreCase(action)
                        || Constants.RESANCTION.equalsIgnoreCase(action)
                        || Constants.SUBMITDEVIATION.equalsIgnoreCase(action)
                        || Constants.SUBMITREASSESSMENT.equalsIgnoreCase(action))) {
                    Optional<Response> loanAmountUpdateError = updateLoanDetailsWithBreEligibleAmount(
                            gson,
                            customerDataFields.getApplicationId(),
                            Constants.COAPPLICANT,
                            true,
                            "Deviation");
                    if (loanAmountUpdateError.isPresent()) {
                        logger.debug("Loan amount update response: {}", loanAmountUpdateError.get());
                        return Mono.just(loanAmountUpdateError.get());
                    }

                    List<DeviationRATracker> deviationData = deviationRATrackerRepo
                            .findByApplicationIdAndApprovedStatus(customerDataFields.getApplicationId(), Constants.PENDING);
                    if (!deviationData.isEmpty()) {
                        for (DeviationRATracker deviationRecord : deviationData) {
                            if (deviationRecord.getRecordType().equalsIgnoreCase(Constants.CA_DEVIATION)) {
                                responseHeader.setResponseCode(ResponseCodes.PENDING_DEVIATION.getKey());
                                response.setResponseHeader(responseHeader);
                                responseBody.setResponseObj("Some deviations are still pending.");
                                response.setResponseBody(responseBody);
                                return Mono.just(response);
                            } else if (deviationRecord.getRecordType().equalsIgnoreCase(Constants.REASSESSMENT)) {
                                action = Constants.SUBMITREASSESSMENT;
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
                responseBody.setResponseObj(Constants.APPLICATION_UPDATED_ERROR);
                responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            }
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
        }
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return Mono.just(response);
    }

    private Optional<Response> updateLoanDetailsWithBreEligibleAmount(
            Gson gson,
            String applicationId,
            String customerType,
            boolean isBeforeSanction,
            String currentStage) {
        try {
            LoanDetails loanDetails = loanDtlsRepo.findByApplicationId(applicationId);
            if (loanDetails == null) {
                return Optional.of(getFailureApiJson("Loan details not present", currentStage + " Application Movement"));
            }
            Optional<CibilDetails> cibilOpt =
                    cibilDetailsRepository.findCibilDetailsByCustomerTypeAndApplicationId(customerType, applicationId);
            if (!cibilOpt.isPresent()) {
                return Optional.of(getFailureApiJson("Cibil details not present", currentStage + " Application Movement"));
            }
            CibilDetailsPayload brePayload = gson.fromJson(cibilOpt.get().getPayloadColumn(), CibilDetailsPayload.class);
            String eligibleAmtStr = brePayload.getEligibleAmt();
            if (StringUtils.isBlank(eligibleAmtStr)) {
                return Optional.of(getFailureApiJson("BRE eligible amount missing", currentStage + " Application Movement"));
            }
            BigDecimal eligibleAmount;
            try {
                eligibleAmount = new BigDecimal(eligibleAmtStr);
            } catch (NumberFormatException e) {
                return Optional.of(getFailureApiJson("Invalid BRE eligible amount", currentStage + " Application Movement"));
            }
            if (isBeforeSanction) {
                if(currentStage.equalsIgnoreCase("pre-sanction")) {
                  loanDetails.setPresactionLoanAmount(eligibleAmount);
                } else {
                    loanDetails.setBmRecommendedLoanAmount(eligibleAmount);
                    LoanDetailsPayload loanDetailsPayload = gson.fromJson(loanDetails.getPayloadColumn(), LoanDetailsPayload.class);
                    loanDetailsPayload.setBmRecommendationBrePayload(brePayload);
                    loanDetails.setPayloadColumn(gson.toJson(loanDetailsPayload));
                }
            } else {
                loanDetails.setSanctionedLoanAmount(eligibleAmount);
            }
            loanDtlsRepo.save(loanDetails);
            logger.debug("loanDetails updated with BRE eligible amount: {}", loanDetails.toString());
            return Optional.empty(); // no failure
        } catch (Exception e) {
            logger.error("Error updating loan details with BRE eligible amount: {}", e.getMessage(), e);
            return Optional.of(getFailureApiJson("Error updating loan details with BRE eligible amount",
                    currentStage + " Application Movement"));
        }
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
        String action = customerDataFields.getWorkFlow().getAction();
        if (!Constants.BCM.equalsIgnoreCase(roleId)
                && !Constants.ACM.equalsIgnoreCase(roleId)
                && !Constants.CCM.equalsIgnoreCase(roleId)) {
            responseHeader.setResponseCode(ResponseCodes.INVALID_ROLE.getKey());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
            return Mono.just(response);
        }
        CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
        Set<AppStatus> validStatuses = EnumSet.of(
                AppStatus.REJECTED,
                AppStatus.RPCVERIFIED,
                AppStatus.CACOMPLETED,
                AppStatus.PENDINGPRESANCTION
        );
        if (isValidStatus(status, validStatuses)) {
        String nextStage = AppStatus.PENDINGPRESANCTION.getValue();
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
                if (AppStatus.PENDINGPRESANCTION.getValue().equalsIgnoreCase(status)
                        || (Constants.SUBMIT.equalsIgnoreCase(action)
                        || Constants.RESANCTION.equalsIgnoreCase(action)
                        || Constants.SUBMITDEVIATION.equalsIgnoreCase(action)
                        || Constants.SUBMITREASSESSMENT.equalsIgnoreCase(action))) {
                    Optional<Response> loanAmountUpdateError = updateLoanDetailsWithBreEligibleAmount(
                            gson,
                            customerDataFields.getApplicationId(),
                            Constants.COAPPLICANT,
                            true,
                            "Reassessment");
                    if (loanAmountUpdateError.isPresent()) {
                        logger.debug("Loan amount update response: {}", loanAmountUpdateError.get());
                        return Mono.just(loanAmountUpdateError.get());
                    }
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
                responseBody.setResponseObj(Constants.APPLICATION_UPDATED_ERROR);
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
        String action = customerDataFields.getWorkFlow().getAction();
        if (!Constants.AM.equalsIgnoreCase(roleId)
                && !Constants.ACM.equalsIgnoreCase(roleId)) {
            responseHeader.setResponseCode(ResponseCodes.INVALID_ROLE.getKey());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
            return Mono.just(response);
        }
        Set<AppStatus> validStatuses = EnumSet.of(
                AppStatus.RPCVERIFIED,
                AppStatus.REJECTED,
                AppStatus.CACOMPLETED
        );

        if (isValidStatus(status, validStatuses)) {
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

                if (Constants.SUBMIT.equalsIgnoreCase(action) || Constants.RESANCTION.equalsIgnoreCase(action)) {
                    Optional<Response> loanAmountUpdateError = updateLoanDetailsWithBreEligibleAmount(
                            gson,
                            customerDataFields.getApplicationId(),
                            Constants.COAPPLICANT,
                            true,
                            "Pre-Sanction");
                    if (loanAmountUpdateError.isPresent()) {
                        logger.debug("Loan amount update response: {}", loanAmountUpdateError.get());
                        return Mono.just(loanAmountUpdateError.get());
                    }
                }

                if (AppStatus.CACOMPLETED.getValue().equalsIgnoreCase(status)) {
                    List<ApplicationWorkflow> applicationWorkflows = applicationWorkflowRepository
                            .findByApplicationIdAndApplicationStatusInOrderByWorkflowSeqNum(
                                    customerDataFields.getApplicationId(),
                                    Arrays.asList(AppStatus.SANCTIONED.getValue()));
                    if (!applicationWorkflows.isEmpty()) {
                        action = Constants.RESANCTION;
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
                responseBody.setResponseObj(Constants.APPLICATION_UPDATED_ERROR);
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
                                                    Properties prop, String roleId) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        try {
            response.setResponseHeader(responseHeader);
            FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest.getRequestObj();
            String status = customerDataFields.getStatus();
            String action = customerDataFields.getWorkFlow().getAction();
            String remarks = customerDataFields.getRemarks();
            if (!Constants.APPROVER.equalsIgnoreCase(roleId)
                    && !Constants.BM.equalsIgnoreCase(roleId)
                    && !Constants.AM.equalsIgnoreCase(roleId)
                    && !Constants.RM.equalsIgnoreCase(roleId)
                    && !Constants.DM.equalsIgnoreCase(roleId)) {
                responseHeader.setResponseCode(ResponseCodes.INVALID_ROLE.getKey());
                response.setResponseBody(responseBody);
                response.setResponseHeader(responseHeader);
                return Mono.just(response);
            }
            Set<AppStatus> validStatuses = EnumSet.of(
                    AppStatus.REJECTED,
                    AppStatus.RPCVERIFIED,
                    AppStatus.SANCTIONED
            );
            if (isValidStatus(status, validStatuses)) {
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
                                    .findCibilDetailsByCustomerTypeAndApplicationId(Constants.COAPPLICANT, applicationId);
                            if (!cibilDetailsOptional.isPresent()) {
                                cibilDetailsOptional = cibilDetailsRepository.findCibilDetailsByCustomerTypeAndApplicationId(Constants.APPLICANT, applicationId);
                            }
                            if (cibilDetailsOptional.isPresent()) {
                                if(StringUtils.isBlank(cibilDetailsOptional.get().getRequest())){
                                    cibilDetailsOptional = cibilDetailsRepository.findCibilDetailsByCustomerTypeAndApplicationId(Constants.APPLICANT, applicationId);
                                }
                                logger.debug("cibilDetailsOptional.get() : {}", cibilDetailsOptional.get());
                                CibilDetailsPayload cibilDetailsPayload = gson.fromJson(cibilDetailsOptional.get().getPayloadColumn(), CibilDetailsPayload.class);
                                logger.debug("cibilDetailsPayload : {}", cibilDetailsPayload);
                                if (null != cibilDetailsPayload.getEligibleAmt()) {
                                    LoanDetails loanDetails = coapplicantIdOpt.get();
                                    BigDecimal bmRecommendedAmount = loanDetails.getBmRecommendedLoanAmount();
                                    BigDecimal eligibleAmt = new BigDecimal(cibilDetailsPayload.getEligibleAmt());
                                    if (eligibleAmt.compareTo(bmRecommendedAmount) > 0) {
                                        logger.info(
                                                "BRE Eligible amount {} is greater than BM recommended amount {} for applicationId {}",
                                                eligibleAmt, bmRecommendedAmount, customerDataFields.getApplicationId()
                                        );
                                        return Mono.just(getFailureApiJson("Sanction amount is greater than BM recommended amount. Please modify the sanction amount", "Sanction Application Movement"));
                                    }
                                    loanDetails.setSanctionedLoanAmount(new BigDecimal(cibilDetailsPayload.getEligibleAmt()));
                                    loanDtlsRepo.save(loanDetails);
                                    String[] remarksIterable = remarks.split("\\|");
                                    String frontendLoanAmount = remarksIterable[0];
                                    String updatedRemarks = remarks.replace(frontendLoanAmount, String.valueOf(loanDetails.getSanctionedLoanAmount()));
                                    customerDataFields.setRemarks(updatedRemarks);
                                }
                            }
                            String coapplicantId = coapplicantIdOpt.get().getCoapplicantId();
                            // Coapplicant creation & applicant update
                            if (masterObj.getProductCode().equalsIgnoreCase(Constants.UNNATI_PRODUCT_CODE)
                                    || masterObj.getProductCode().equalsIgnoreCase(ProductCode.VISHESH.getUnnatiCode())) {
                                return performSanctionUnnatiProductT24Calls(applicationId, masterObj, header, prop, customerDataFields, coapplicantId)
                                        .switchIfEmpty(
                                                Mono.defer(() -> {
                                                    //Loan Creation
                                                    if (!isStepSuccessful(applicationId, Constants.LOAN_CREATION, Constants.SANCTION)) {
                                                        return processLoanCreation(masterObj, applicationId, header, prop, customerDataFields,
                                                                Constants.SANCTION);
                                                    }
                                                    //Loan Repayment schedule
                                                    if (!isStepSuccessful(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE, Constants.SANCTION)) {
                                                        return initiateLoanRepaySchedule(masterObj, applicationId, header, prop,
                                                                Constants.SANCTION, customerDataFields, "");
                                                    }
                                                    return Mono.empty();
                                                }));
                            } else if (masterObj.getProductCode().equalsIgnoreCase(Constants.RENEWAL_PRODUCT_CODE)) {
                                return performSanctionRenewalProductT24Calls(applicationId, masterObj, header, prop, customerDataFields, coapplicantId)
                                        .switchIfEmpty(
                                                Mono.defer(() -> {
                                                    //Loan Creation
                                                    if (!isStepSuccessful(applicationId, Constants.LOAN_CREATION, Constants.SANCTION)) {
                                                        return processLoanCreation(masterObj, applicationId, header, prop, customerDataFields,
                                                                Constants.SANCTION);
                                                    }
                                                    //Loan Repayment schedule
                                                    if (!isStepSuccessful(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE, Constants.SANCTION)) {
                                                        return initiateLoanRepaySchedule(masterObj, applicationId, header, prop,
                                                                Constants.SANCTION, customerDataFields, "");
                                                    }
                                                    return Mono.empty();
                                                }));
                            } else if (masterObj.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)) {
                                return performOpenMarketProductT24Calls(applicationId, masterObj, header, prop, customerDataFields, coapplicantId, Constants.SANCTION)
                                        .switchIfEmpty(
                                                Mono.defer(() -> {
                                                    //Loan Creation
                                                    if (!isStepSuccessful(applicationId, Constants.LOAN_CREATION, Constants.SANCTION)) {
                                                        return processLoanCreation(masterObj, applicationId, header, prop, customerDataFields,
                                                                Constants.SANCTION);
                                                    }
                                                    //Loan Repayment schedule
                                                    if (!isStepSuccessful(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE, Constants.SANCTION)) {
                                                        return initiateLoanRepaySchedule(masterObj, applicationId, header, prop,
                                                                Constants.SANCTION, customerDataFields, "");
                                                    }
                                                    return Mono.empty();
                                                }));
                            } else if (masterObj.getProductCode().equalsIgnoreCase(ProductCode.FAMILY_WELFARE.getUnnatiCode())
                                    || masterObj.getProductCode().equalsIgnoreCase(ProductCode.UNNATI_EMERGENCY.getUnnatiCode())) {
                                //Commented out code to update applicant details and co-applicant details in t24 and CDH as requested by client
//                                if (!isStepSuccessful(applicationId, Constants.APPLICANT_UPDATION, Constants.SANCTION)) {
//                                    return initiateApplicantUpdation(masterObj, header, prop, customerDataFields, masterObj.getMemberId(),
//                                            Constants.SANCTION, coapplicantId);
//                                }
//                                if (!isStepSuccessful(applicationId, Constants.APPLICANT_DEDUPE_UPDATE, Constants.SANCTION)) {
//                                    return initiateDedupeTableUpdate(masterObj, header, prop, customerDataFields, coapplicantId,
//                                            Constants.SANCTION, false);
//                                }
                                if (!isStepSuccessful(applicationId, Constants.LOAN_CREATION, Constants.SANCTION)) {
                                    return processLoanCreation(masterObj, applicationId, header, prop, customerDataFields,
                                            Constants.SANCTION);
                                }
                                //Loan Repayment schedule
                                if (!isStepSuccessful(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE, Constants.SANCTION)) {
                                    return initiateLoanRepaySchedule(masterObj, applicationId, header, prop,
                                            Constants.SANCTION, customerDataFields, "");
                                }
                            } else if (masterObj.getProductCode().equalsIgnoreCase(ProductCode.UNNATI_RESTART.getUnnatiCode())
                                    || masterObj.getProductCode().equalsIgnoreCase(ProductCode.UNNATI_SUPPLEMENTARY.getUnnatiCode())) {
                                if (null == coapplicantId) {
                                    String coAppCustId = unnatiCoApplicantDetailsRepository.getCoAppCustId(masterObj.getSearchCode2());
                                    if (org.apache.commons.lang3.StringUtils.isNotBlank(coAppCustId)) {
                                        coapplicantId = coAppCustId;
                                    } else {
                                        return Mono.just(getFailureApiJson("Co-applicant Id not found for the application", Constants.COAPPLICANT_UPDATION));
                                    }
                                }
//                                return performSanctionUnnatiProductT24Calls(applicationId, masterObj, header, prop, customerDataFields, coapplicantId)
//                                        .switchIfEmpty(
//                                                Mono.defer(() -> {
                                //Loan Creation
                                if (!isStepSuccessful(applicationId, Constants.LOAN_CREATION, Constants.SANCTION)) {
                                    return processLoanCreation(masterObj, applicationId, header, prop, customerDataFields,
                                            Constants.SANCTION);
                                }
                                //Loan Repayment schedule
                                if (!isStepSuccessful(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE, Constants.SANCTION)) {
                                    return initiateLoanRepaySchedule(masterObj, applicationId, header, prop,
                                            Constants.SANCTION, customerDataFields, "");
                                }
//                                                    return Mono.empty();
//                                                }));
                            } else {
                                return Mono.just(getFailureApiJson("Invalid product code :" + masterObj.getProductCode(), Constants.COAPPLICANT_UPDATION));
                            }
                        }
                    }

                    masterObj.setRemarks(customerDataFields.getRemarks());
                    masterObj.setUpdatedBy(customerDataFields.getUserId());
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
                    responseBody.setResponseObj(Constants.APPLICATION_UPDATED_ERROR);
                    responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
                }
            } else {
                responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
            }
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
        } catch (Exception e) {
            logger.error("Exception in loan creation API - SanctionApplicationMovement :: {}", e.getMessage(), e);
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

        if (!masterObj.getProductCode().equalsIgnoreCase(ProductCode.UNNATI_SUPPLEMENTARY.getUnnatiCode())
                && !masterObj.getProductCode().equalsIgnoreCase(ProductCode.UNNATI_RESTART.getUnnatiCode())) {
        if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_CREATION, Constants.SANCTION)
                || !isStepSuccessful(applicationId, Constants.CO_CUSTOMER_FETCH, Constants.SANCTION)) {
            return initiateCoApplicantCreation(masterObj, header, prop, customerDataFields,
                    Constants.SANCTION);
        }
        }
        // coapplicant update
        if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_UPDATION, Constants.SANCTION)) {
            logger.info("co-applicant update for coapplicantId={} ", coapplicantId);
            return initiateCoapplicantUpdation(masterObj, header, prop, customerDataFields, coapplicantId,
                    Constants.SANCTION, true);
        }
        if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_DEDUPE_UPDATE, Constants.SANCTION)) {
            return Mono.delay(Duration.ofSeconds(Constants.FIVE_SECONDS))
                    .then(initiateDedupeTableUpdate(
                            masterObj, header, prop, customerDataFields, coapplicantId,
                            Constants.SANCTION, true));
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
        CustomerDetailsPayload payload =
                new Gson().fromJson(
                        coappDetailsOpt.get().getPayloadColumn(),
                        CustomerDetailsPayload.class
                );

        String isNewCoapplicant =
                payload != null ? payload.getIsNewCustomer() : null;

        boolean createNewCoapplicant =
                isNewCoapplicant != null
                        && isNewCoapplicant.toUpperCase().contains("CREATE");
        if (createNewCoapplicant) {
            logger.debug("Creating new co-applicant as per the flag in payload for applicationId={}", applicationId);
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
                return Mono.delay(Duration.ofSeconds(Constants.FIVE_SECONDS))
                        .then(initiateDedupeTableUpdate(
                                masterObj, header, prop, customerDataFields, coapplicantId,
                                Constants.SANCTION, true));
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
            logger.debug("Going with existing co-applicant");
            List<UnnatiCoApplicantDetails> renewalCoappLeadDetails = coAppDetailsRepository
                    .findByCustomerId(masterObj.getSearchCode2());
            if (!renewalCoappLeadDetails.isEmpty()) {
                String renewalCoapplicantId = renewalCoappLeadDetails.get(0).getCoCustomerId();
                loanDtlsRepo.updateCoapplicantId(applicationId, renewalCoapplicantId);

                logger.info("co-applicant update for coapplicantId={} ", renewalCoapplicantId);
                if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_UPDATION, Constants.SANCTION)) {
                    return initiateCoapplicantUpdation(masterObj, header, prop, customerDataFields, renewalCoapplicantId,
                            Constants.SANCTION, true);
                }
                if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_DEDUPE_UPDATE, Constants.SANCTION)) {
                    return Mono.delay(Duration.ofSeconds(Constants.FIVE_SECONDS))
                            .then(initiateDedupeTableUpdate(
                                    masterObj, header, prop, customerDataFields, renewalCoapplicantId,
                                    Constants.SANCTION, true));
                }
                if (!isStepSuccessful(applicationId, Constants.APPLICANT_UPDATION, Constants.SANCTION)) {
                    return initiateApplicantUpdation(masterObj, header, prop, customerDataFields, masterObj.getMemberId(),
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
        logger.debug("Inside performOpenMarketProductT24Calls with applicationId={}, currentStage={}", applicationId, currentStage);

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
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "sanctionApplicationMovementFallback")
    public Mono<Object> ReSanctionApplicationMovement(FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
                                                      Properties prop, String roleId) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        try {
            response.setResponseHeader(responseHeader);
            FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest.getRequestObj();
            String status = customerDataFields.getStatus();
            String action = customerDataFields.getWorkFlow().getAction();
            String remarks = customerDataFields.getRemarks();
            if (!Constants.APPROVER.equalsIgnoreCase(roleId)
                    && !Constants.BM.equalsIgnoreCase(roleId)
                    && !Constants.AM.equalsIgnoreCase(roleId)
                    && !Constants.RM.equalsIgnoreCase(roleId)
                    && !Constants.DM.equalsIgnoreCase(roleId)) {
                responseHeader.setResponseCode(ResponseCodes.INVALID_ROLE.getKey());
                response.setResponseBody(responseBody);
                response.setResponseHeader(responseHeader);
                return Mono.just(response);
            }
            Set<AppStatus> validStatuses = EnumSet.of(
                    AppStatus.REJECTED,
                    AppStatus.RPCVERIFIED,
                    AppStatus.SANCTIONED
            );

            if (isValidStatus(status, validStatuses)) {
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
                        Optional<LoanDetails> coapplicantIdOpt = loanDtlsRepo.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(
                                customerDataFields.getAppId(), applicationId);
                        String coapplicantId = null;
                        if (coapplicantIdOpt.isPresent()) {
                            coapplicantId = coapplicantIdOpt.get().getCoapplicantId();
                            Optional<CibilDetails> cibilDetailsOptional = cibilDetailsRepository.findCibilDetailsByCustomerTypeAndApplicationId(Constants.COAPPLICANT, applicationId);
                            if (!cibilDetailsOptional.isPresent()) {
                                cibilDetailsOptional = cibilDetailsRepository.findCibilDetailsByCustomerTypeAndApplicationId(Constants.APPLICANT, applicationId);
                            }
                            if (StringUtils.isBlank(cibilDetailsOptional.get().getRequest())) {

                                logger.warn("Co-Applicant BRE request is null/blank, falling back to Applicant CibilDetails");

                                cibilDetailsOptional = cibilDetailsRepository.findCibilDetailsByCustomerTypeAndApplicationId(
                                        Constants.APPLICANT,
                                        customerDataFields.getApplicationId()
                                );
                            }

                            if (cibilDetailsOptional.isPresent()) {
                                CibilDetailsPayload cibilDetailsPayload = gson.fromJson(cibilDetailsOptional.get().getPayloadColumn(), CibilDetailsPayload.class);
                                if (null != cibilDetailsPayload.getEligibleAmt()) {
                                    LoanDetails loanDetails = coapplicantIdOpt.get();
                                    BigDecimal bmRecommendedAmount = loanDetails.getBmRecommendedLoanAmount();
                                    BigDecimal eligibleAmt = new BigDecimal(cibilDetailsPayload.getEligibleAmt());
                                    if (eligibleAmt.compareTo(bmRecommendedAmount) > 0) {
                                        logger.info(
                                                "BRE Eligible amount {} is greater than BM recommended amount {} for applicationId {}",
                                                eligibleAmt, bmRecommendedAmount, customerDataFields.getApplicationId()
                                        );
                                        return Mono.just(getFailureApiJson("Sanction amount is greater than BM recommended amount. Please modify the sanction amount", "Resanction Application Movement"));
                                    }

                                    loanDetails.setSanctionedLoanAmount(eligibleAmt);
                                    loanDtlsRepo.save(loanDetails);
                                    String[] remarksIterable = remarks.split("\\|");
                                    String frontendLoanAmount = remarksIterable[0];
                                    String updatedRemarks = remarks.replace(frontendLoanAmount, String.valueOf(loanDetails.getSanctionedLoanAmount()));
                                    customerDataFields.setRemarks(updatedRemarks);
                                }
                            }

                        }
                        if (masterObj.getProductCode().equalsIgnoreCase(Constants.UNNATI_PRODUCT_CODE)
                                || masterObj.getProductCode().equalsIgnoreCase(ProductCode.VISHESH.getUnnatiCode())) {
                            if (coapplicantIdOpt.isPresent()) {

                                logger.info("co-applicant update for coapplicantId={} ", coapplicantId);
                                return initiateCoapplicantUpdation(masterObj, header, prop, customerDataFields, coapplicantId, Constants.RESANCTION, true);

                            } else {
                                logger.warn("No co-applicant details found for appId={} and applicationId={}",
                                        customerDataFields.getAppId(), applicationId);
                                return Mono.just(getFailureApiJson("Missing coapplicant ID", Constants.COAPPLICANT_UPDATION));
                            }
                        } else if (masterObj.getProductCode().equalsIgnoreCase(Constants.RENEWAL_PRODUCT_CODE)) {
                            Optional<CustomerDetails> coappDetailsOpt = customerDetailsRepository.findByApplicationIdAndAppIdAndCustomerType(masterObj.getApplicationId(), masterObj.getAppId(), Constants.COAPPLICANT);
                            if (!coappDetailsOpt.isPresent()) {
                                logger.warn("No co-applicant details found for appId={} and applicationId={}",
                                        customerDataFields.getAppId(), applicationId);
                                return Mono.just(getFailureApiJson("Missing coapplicant details", Constants.COAPPLICANT_UPDATION));
                            }
                            CustomerDetailsPayload payload =
                                    new Gson().fromJson(
                                            coappDetailsOpt.get().getPayloadColumn(),
                                            CustomerDetailsPayload.class
                                    );

                            String isNewCoapplicant =
                                    payload != null ? payload.getIsNewCustomer() : null;

                            boolean createNewCoapplicant =
                                    isNewCoapplicant != null
                                            && isNewCoapplicant.toUpperCase().contains("CREATE");

                            if (createNewCoapplicant) {
                                if (!isStepSuccessful(applicationId, Constants.COAPPLICANT_CREATION, Constants.RESANCTION)
                                        || !isStepSuccessful(applicationId, Constants.CO_CUSTOMER_FETCH, Constants.RESANCTION)) {
                                    return initiateCoApplicantCreation(masterObj, header, prop, customerDataFields,
                                            Constants.RESANCTION);
                                }
                            }

                            if (coapplicantIdOpt.isPresent()) {
                                logger.info("co-applicant update for coapplicantId={} ", coapplicantId);
                                return initiateCoapplicantUpdation(masterObj, header, prop, customerDataFields, coapplicantId, Constants.RESANCTION, true);
                            } else {
                                logger.warn("No co-applicant details found for appId={} and applicationId={}",
                                        customerDataFields.getAppId(), applicationId);
                                return Mono.just(getFailureApiJson("Missing coapplicant ID", Constants.COAPPLICANT_UPDATION));
                            }
                        } else if (masterObj.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)) {
                            logger.debug("Inside Re-sanction flow for Open Market Product");
                            if (coapplicantIdOpt.isPresent()) {
                                logger.info("Calling performOpenMarketProductT24Calls - applicationId={}, appId={}, productCode={}, coapplicantId={}, stage={}, header={}",
                                        applicationId, masterObj.getAppId(), masterObj.getProductCode(), coapplicantId, Constants.RESANCTION, String.valueOf(header));
                                return performOpenMarketProductT24Calls(applicationId, masterObj, header, prop, customerDataFields, coapplicantId, Constants.RESANCTION);

                            } else {
                                logger.warn("No co-applicant details found for appId={} and applicationId={}",
                                        customerDataFields.getAppId(), applicationId);
                                return Mono.just(getFailureApiJson("Missing coapplicant ID", Constants.COAPPLICANT_UPDATION));
                            }
                        } else if (masterObj.getProductCode().equalsIgnoreCase(ProductCode.FAMILY_WELFARE.getUnnatiCode())
                                || masterObj.getProductCode().equalsIgnoreCase(ProductCode.UNNATI_EMERGENCY.getUnnatiCode())) {
//                            return initiateApplicantUpdation(masterObj, header, prop, customerDataFields, masterObj.getMemberId(),
//                                    Constants.RESANCTION, coapplicantId);
                            return initiateLoanCreation(masterObj, coapplicantId, applicationId, header, prop,
                                    customerDataFields, Constants.RESANCTION);
                        } else if (masterObj.getProductCode().equalsIgnoreCase(ProductCode.UNNATI_RESTART.getUnnatiCode())
                                || masterObj.getProductCode().equalsIgnoreCase(ProductCode.UNNATI_SUPPLEMENTARY.getUnnatiCode())) {
                            if (null == coapplicantId) {
                                String coAppCustId = unnatiCoApplicantDetailsRepository.getCoAppCustId(masterObj.getSearchCode2());
                                if (org.apache.commons.lang3.StringUtils.isNotBlank(coAppCustId)) {
                                    coapplicantId = coAppCustId;
                                } else {
                                    return Mono.just(getFailureApiJson("Co-applicant Id not found for the application", Constants.COAPPLICANT_UPDATION));
                                }
                            }
//                            return initiateCoApplicantCreation(masterObj, header, prop, customerDataFields,
//                                    Constants.RESANCTION);
                            return initiateLoanCreation(masterObj, coapplicantId, applicationId, header, prop,
                                    customerDataFields, Constants.RESANCTION);
                        } else {
                            return Mono.just(getFailureApiJson("Invalid product code :" + masterObj.getProductCode(), Constants.COAPPLICANT_UPDATION));
                        }
                    }

                    masterObj.setRemarks(customerDataFields.getRemarks());
                    masterObj.setUpdatedBy(customerDataFields.getUserId());
                    cobService.updateStatus(masterObj, status);

                    Optional<DBKITStageVerification> dbkitStageVerificationOpt = dbkitStageVerificationRepository.findById(applicationId);
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
                    responseBody.setResponseObj(Constants.APPLICATION_UPDATED_ERROR);
                    responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
                }
            } else {
                responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
            }
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
        } catch (Exception e) {
            logger.error("Exception in loan creation API - ResanctionApplicationMovement :: {}", e.getMessage(), e);
        }
        return Mono.just(response);
    }

    private Mono<Object> processLoanCreation(ApplicationMaster master, String applicationId,
                                             Header header, Properties prop, FetchDeleteUserFields customerDataFields, String currentStage) {
            return loanDtlsRepo.findTopByApplicationIdAndAppId(applicationId, customerDataFields.getAppId())
                    .map(loan -> initiateLoanCreation(master, loan.getCoapplicantId(), applicationId, header, prop, customerDataFields, currentStage))
                .orElse(Mono.just(getFailureJson("No loan details found for applicationId")));
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
                ? master.getApplicantT24Id() : master.getMemberId();
        Mono<Object> coapplicantCreation = t24AndCDHService.coapplicantCreation(applicationId, master.getAppId(),
                applicantId, header, prop, false, "", false);
        return coapplicantCreation.flatMap(response -> {
            try {
                logger.debug("Coapplicant creation response: {}", response);
                String json = (new Gson()).toJson(response);
                JSONObject apiResp = convertResponseToJson(json);
                Object requestObj = apiResp.get(Constants.API_REQUEST);
                if (apiResp.has(Constants.HEADER)) {
                    JSONObject headerJson = apiResp.getJSONObject(Constants.HEADER);
                    if (ResponseCodes.SUCCESS.getValue().equalsIgnoreCase(headerJson.optString(Constants.STATUS))) {
                        String coApplicantId = headerJson.optString("id");
                        logger.debug("coApplicantId :" + coApplicantId);
                        saveLog(applicationId, Constants.COAPPLICANT_CREATION, requestObj.toString(),
                                response.toString(), ResponseCodes.SUCCESS.getValue(), null, currentStage);
                        if (!coApplicantId.equalsIgnoreCase(applicantId)) {
                            loanDtlsRepo.updateCoapplicantId(applicationId, coApplicantId);
                        }
                        return initiateCoapplicantUpdation(master, header, prop, customerDataFields,
                                coApplicantId, currentStage, true);
                    }
                    if (apiResp.has(Constants.ERROR1) && !apiResp.getJSONObject(Constants.ERROR1).isEmpty()) {
                        JSONArray coCustCreationErr = apiResp.getJSONObject(Constants.ERROR1).getJSONArray(Constants.ERROR_DETAILS);
                        List<String> coCustCreationErrors = extractErrorMessages(apiResp.getJSONObject("error"));
                        saveLog(applicationId, Constants.COAPPLICANT_CREATION, requestObj.toString(),
                                response.toString(), ResponseCodes.FAILURE.getValue(), coCustCreationErrors.toString(), currentStage);
                        logger.debug("CoCustCreationErr :" + coCustCreationErr.toString());
                        List<String> coCustCreationErrList = new ArrayList<>();
                        for (Object field : coCustCreationErr) {
                            JSONObject fieldError = new JSONObject(field.toString());
                            logger.debug("coCustCreation Field Error " + fieldError);
                            String message = fieldError.optString(Constants.MESSAGE);
                            String expected = prop.getProperty(
                                    CobFlagsProperties.CO_CUST_FETCH_INPUT_ERROR.getKey()
                            );
                            // Build a tolerant regex from the configured message
                            String baseRegex = expected
                                    .toLowerCase()
                                    .replaceAll("voter-id", "voter[- ]?id")   // voterId / voter-id / VOTER-ID
                                    .replaceAll("\\s+", "\\\\s*")             // flexible spacing
                                    .replaceAll(":", "\\\\s*:?\\\\s*");       // optional colon & spaces
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
                                coCustCreationErrList.add(fieldError.optString(Constants.FIELD_NAME, Constants.ERROR2) + " - " + fieldError.optString(Constants.MESSAGE, "Unknown Error").trim());

                                saveLog(applicationId, Constants.COAPPLICANT_CREATION, requestObj.toString(), coCustCreationErrList.toString() + "No customer id in the error message", ResponseCodes.FAILURE.getValue(), coCustCreationErrList.toString(), currentStage);
                                Response failureJson = getFailureApiJson(coCustCreationErrList.toString(), Constants.CO_CUSTOMER_FETCH);
                                return Mono.just(failureJson);
                            }
                        }
                        List<String> errors = extractErrorMessages(apiResp.getJSONObject(Constants.ERROR1));
                        saveLog(applicationId, Constants.COAPPLICANT_CREATION, requestObj.toString(),
                                response.toString(), ResponseCodes.FAILURE.getValue(), errors.toString(), currentStage);
                        return Mono.just(getFailureApiJson(errors.toString(), Constants.COAPPLICANT_CREATION));
                    }
                }
                saveLog(applicationId, Constants.COAPPLICANT_CREATION, requestObj.toString(),
                        response.toString(), ResponseCodes.FAILURE.getValue(), Constants.SERVICE_DOWN, currentStage);
                return Mono.just(getFailureApiJson(Constants.SERVICE_DOWN, Constants.COAPPLICANT_CREATION));

            } catch (Exception e) {
                logger.error("Error parsing coapplicant response", e);
                saveLog(applicationId, Constants.COAPPLICANT_CREATION, t24AndCDHService.getRequestLog(), response.toString(),
                        ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
                return Mono.just(getFailureApiJson(e.toString(), Constants.COAPPLICANT_CREATION));
            }
        }).onErrorResume(e -> {
            logger.error("Error during Coapplicant Creation: ", e);
            saveLog(applicationId, Constants.COAPPLICANT_CREATION, t24AndCDHService.getRequestLog(), e.getMessage(),
                    ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
            if (e.getMessage().toLowerCase().contains("voter")) {
                return Mono.just(getFailureApiJson(e.getMessage(), Constants.COAPPLICANT_CREATION));
            } else {
                return Mono.just(getFailureApiJson("Error during Coapplicant Creation", Constants.COAPPLICANT_CREATION));
            }
        });
    }

    private Mono<Object> initiateDedupeTableUpdate(ApplicationMaster master, Header header, Properties prop,
                                                   FetchDeleteUserFields customerDataFields, String coApplicantId, String currentStage, boolean coapplUpdate) {
        String applicationId = master.getApplicationId();
        String updateApi = coapplUpdate ? Constants.COAPPLICANT_DEDUPE_UPDATE : Constants.APPLICANT_DEDUPE_UPDATE;
        String applicantId = master.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE) ? master.getApplicantT24Id() : master.getMemberId();
        String updateCustomerId = coapplUpdate ? coApplicantId : applicantId;
        Mono<Object> dedupeTableUpdate = t24AndCDHService.dedupeTableUpdate(master, header, prop, updateCustomerId, coapplUpdate);
        return dedupeTableUpdate.flatMap(res -> {
            logger.debug("Dedupe Table update response: {}", res);
            try {
                String jsonBody = (new Gson()).toJson(res);
                JSONObject apiRes = convertResponseToJson(jsonBody);
                Object requestObj = apiRes.get(Constants.API_REQUEST);
                if (apiRes.has("response") && apiRes.has("msg")) {
                    String resp = apiRes.getString("response");
                    String msg = apiRes.getString("msg");

                    if (Constants.SUCCESS.equalsIgnoreCase(resp)) {
                        if ("Details Updated".equalsIgnoreCase(msg)) {
                            saveLog(applicationId, updateApi, requestObj.toString(), res.toString(),
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
                            saveLog(applicationId, updateApi, requestObj.toString(), res.toString(),
                                    ResponseCodes.FAILURE.getValue(), errorMsg, currentStage);
                            return Mono.just(getFailureApiJson(errorMsg, updateApi));
                        }
                    }
                }
                saveLog(applicationId, updateApi, requestObj.toString(),
                        res.toString(), ResponseCodes.FAILURE.getValue(), Constants.SERVICE_DOWN, currentStage);
                return Mono.just(getFailureApiJson(Constants.SERVICE_DOWN, updateApi));
            } catch (Exception e) {
                logger.error("Error parsing dedupeTableUpdate API response", e);
                saveLog(applicationId, updateApi, t24AndCDHService.getRequestLog(), res.toString(),
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
                                "(?i)(?:customer\\s*id[^a-zA-Z0-9]*([a-zA-Z0-9]{4,})|([a-zA-Z0-9]{4,})[^a-zA-Z]*customer\\s*id)"
                        );
                        Matcher matcher = pattern.matcher(frontendMsg);

                        if (matcher.find()) {
                            logger.debug("Matcher found in error message: {}", frontendMsg);
                            errorCustomerId =
                                    matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
                            logger.debug("Extracted Customer ID from error message: {}", errorCustomerId);
                            if (errorCustomerId.equalsIgnoreCase(updateCustomerId)
                                    || errorCustomerId.equalsIgnoreCase(master.getSearchCode2())) {
                                logger.debug("dedupe Update skipped for customerId: {}, since same ID recieved in response {}", updateCustomerId, frontendMsg);
                                saveLog(applicationId, updateApi, t24AndCDHService.getRequestLog(), e.toString(),
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
                            if (errorCustomerId.equalsIgnoreCase(coApplicantId) || errorCustomerId.equalsIgnoreCase(applicantId)) {
                                if (frontendMsg.toUpperCase().contains("PHONEDEDUPE")) {
                                    logger.debug("dedupe Update skipped for customerId: {}, since applicant/coapplicant id recieved in the response {}", updateCustomerId, frontendMsg);
                                    saveLog(applicationId, updateApi, t24AndCDHService.getRequestLog(), e.toString(),
                                            ResponseCodes.SUCCESS.getValue(), null, currentStage);
                                    if (coapplUpdate) {
                                        Mono<Object> applicantUpdateMono = initiateApplicantUpdation(master, header, prop,
                                                customerDataFields, applicantId, currentStage, coApplicantId);
                                        return applicantUpdateMono;
                                    } else {
                                        return initiateLoanCreation(master, coApplicantId, applicationId, header, prop,
                                                customerDataFields, currentStage);
                                    }
                                } else {
                                    saveLog(applicationId, updateApi, t24AndCDHService.getRequestLog(), e.getMessage(), ResponseCodes.FAILURE.getValue(), frontendMsg, currentStage);
                                    return Mono.just(getFailureApiJson(frontendMsg, updateApi));
                                }
                            } else {
                                saveLog(applicationId, updateApi, t24AndCDHService.getRequestLog(), e.getMessage(), ResponseCodes.FAILURE.getValue(), frontendMsg, currentStage);
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
            saveLog(applicationId, updateApi, t24AndCDHService.getRequestLog(), e.getMessage(), ResponseCodes.FAILURE.getValue(), frontendMsg, currentStage);
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
        Mono<Object> applicantActivationRes = t24AndCDHService.applicantActivation(master, header, prop);
        return applicantActivationRes.flatMap(res -> {
            try {
                logger.debug("applicant activation response: {}", res);
                String jsonBody = (new Gson()).toJson(res);
                JSONObject apiRes = convertResponseToJson(jsonBody);
                Object requestObj = apiRes.get(Constants.API_REQUEST);
                if (apiRes.has(Constants.HEADER)) {
                    JSONObject headerJs = apiRes.getJSONObject(Constants.HEADER);
                    String status = headerJs.optString(Constants.STATUS);
                    if (ResponseCodes.SUCCESS.getValue().equalsIgnoreCase(status)) {
                        String t24id = headerJs.optString("id");
                        logger.debug("Applicant activated with T24 ID: {}", t24id);
                        applicationMasterRepository2.updateApplicantT24IdInMaster(applicationId, t24id);
                        master.setApplicantT24Id(t24id);
                        saveLog(applicationId, Constants.APPLICANT_ACTIVATION, requestObj.toString(), res.toString(),
                                ResponseCodes.SUCCESS.getValue(), null, currentStage);
                        return initiateCoApplicantCreation(master, header, prop, customerDataFields, currentStage);
                    } else {
                        if (apiRes.has(Constants.ERROR1) && !apiRes.getJSONObject(Constants.ERROR1).isEmpty()) {
                            List<String> errors = extractErrorMessages(apiRes.getJSONObject(Constants.ERROR1));
                            saveLog(applicationId, Constants.APPLICANT_ACTIVATION, requestObj.toString(),
                                    res.toString(), ResponseCodes.FAILURE.getValue(), errors.toString(), currentStage);
                            return Mono.just(getFailureApiJson(errors.toString(), Constants.APPLICANT_ACTIVATION));
                        }
                    }
                }
                saveLog(applicationId, Constants.APPLICANT_ACTIVATION, requestObj.toString(),
                        res.toString(), ResponseCodes.FAILURE.getValue(), Constants.SERVICE_DOWN, currentStage);
                return Mono.just(getFailureApiJson(Constants.SERVICE_DOWN, Constants.APPLICANT_ACTIVATION));
            } catch (Exception e) {
                logger.error("Error parsing coapplicant response", e);
                saveLog(applicationId, Constants.APPLICANT_ACTIVATION, t24AndCDHService.getRequestLog(), res.toString(),
                        ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
                return Mono.just(getFailureApiJson(e.toString(), Constants.APPLICANT_ACTIVATION));
            }
        }).onErrorResume(e -> {
            logger.error("Error during Customer update: ", e);
            saveLog(applicationId, Constants.APPLICANT_ACTIVATION, t24AndCDHService.getRequestLog(), e.getMessage(),
                    ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
            return Mono.just(getFailureApiJson("Error during Customer update", Constants.APPLICANT_ACTIVATION));
        });
    }


    private Mono<Object> initiateCoapplicantUpdation(ApplicationMaster master, Header header, Properties prop,
                                                     FetchDeleteUserFields customerDataFields, String coApplicantId, String currentStage, boolean coapplUpdate) {
        String applicationId = master.getApplicationId();
        String updateApi = Constants.COAPPLICANT_UPDATION;
        String applicantId = master.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)
                ? master.getApplicantT24Id() : master.getMemberId();
        Mono<Object> coapplicantUpdation = t24AndCDHService.coapplicantCreation(applicationId, master.getAppId(),
                applicantId, header, prop, true, coApplicantId, coapplUpdate);
        return coapplicantUpdation.flatMap(res -> {
            try {
                logger.debug("Coapplicant creation response: {}", res);
                String jsonBody = (new Gson()).toJson(res);
                JSONObject apiRes = convertResponseToJson(jsonBody);
                Object requestObj = apiRes.get(Constants.API_REQUEST);
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

                        saveLog(applicationId, updateApi, requestObj.toString(), res.toString(),
                                ResponseCodes.SUCCESS.getValue(), null, currentStage);
                        logger.debug("dedupe Update started");
                        return Mono.delay(Duration.ofSeconds(Constants.FIVE_SECONDS))
                                .then(initiateDedupeTableUpdate(
                                        master, header, prop, customerDataFields, coApplicantId,
                                        currentStage, coapplUpdate));

                    }

                    // Actual failure
                    if (apiRes.has(Constants.ERROR1) && !apiRes.getJSONObject(Constants.ERROR1).isEmpty()) {
                        List<String> errors = extractErrorMessages(apiRes.getJSONObject(Constants.ERROR1));
                        saveLog(applicationId, updateApi, requestObj.toString(),
                                res.toString(), ResponseCodes.FAILURE.getValue(), errors.toString(), currentStage);
                        return Mono.just(getFailureApiJson(errors.toString(), updateApi));
                    }
                }
                saveLog(applicationId, Constants.COAPPLICANT_UPDATION, this.t24AndCDHService.getRequestLog(),
                        res.toString(), ResponseCodes.FAILURE.getValue(), Constants.SERVICE_DOWN, currentStage);
                return Mono.just(getFailureApiJson(Constants.SERVICE_DOWN, Constants.COAPPLICANT_UPDATION));

            } catch (Exception e) {
                logger.error("Error parsing coapplicant response", e);
                saveLog(applicationId, updateApi, t24AndCDHService.getRequestLog(), res.toString(),
                        ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
                return Mono.just(getFailureApiJson(e.toString(), updateApi));
            }
        }).onErrorResume(e -> {
            logger.error("Error during Customer update: ", e);
            saveLog(applicationId, updateApi, t24AndCDHService.getRequestLog(), e.getMessage(),
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
                ? master.getApplicantT24Id() : master.getMemberId();
        Mono<Object> coapplicantUpdation = t24AndCDHService.coapplicantCreation(applicationId, master.getAppId(),
                applicantIdForUpdation, header, prop, true, coApplicantId, false);

        return coapplicantUpdation.flatMap(res -> {
            try {
                logger.debug("Applicant Update response: {}", res);
                String jsonBody = (new Gson()).toJson(res);
                JSONObject apiRes = convertResponseToJson(jsonBody);
                Object requestObj = apiRes.get(Constants.API_REQUEST);
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
                        saveLog(applicationId, updateApi, requestObj.toString(), res.toString(),
                                ResponseCodes.SUCCESS.getValue(), null, currentStage);
                        logger.debug("dedupe Update started");
                        return initiateDedupeTableUpdate(master, header, prop,
                                customerDataFields, coApplicantId, currentStage, false);

                    }

                    // Actual failure
                    if (apiRes.has(Constants.ERROR1) && !apiRes.getJSONObject(Constants.ERROR1).isEmpty()) {
                        List<String> errors = extractErrorMessages(apiRes.getJSONObject(Constants.ERROR1));
                        saveLog(applicationId, updateApi, requestObj.toString(),
                                res.toString(), ResponseCodes.FAILURE.getValue(), errors.toString(), currentStage);
                        return Mono.just(getFailureApiJson(errors.toString(), updateApi));
                    }
                }
                saveLog(applicationId, Constants.APPLICANT_UPDATION, this.t24AndCDHService.getRequestLog(),
                        res.toString(), ResponseCodes.FAILURE.getValue(), Constants.SERVICE_DOWN, currentStage);
                return Mono.just(getFailureApiJson(Constants.SERVICE_DOWN, Constants.APPLICANT_UPDATION));

            } catch (Exception e) {
                logger.error("Error parsing Applicant response", e);
                saveLog(applicationId, updateApi, t24AndCDHService.getRequestLog(), res.toString(),
                        ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
                return Mono.just(getFailureApiJson(e.toString(), updateApi));
            }
        }).onErrorResume(e -> {
            logger.error("Error during Customer update: ", e);
            saveLog(applicationId, updateApi, t24AndCDHService.getRequestLog(), e.getMessage(),
                    ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
            return Mono.just(getFailureApiJson("Error during Customer update", updateApi));
        });
    }

    private Mono<Object> initiateLoanCreation(ApplicationMaster masterObj, String coApplicantId,
                                              String applicationId, Header header, Properties prop, FetchDeleteUserFields customerDataFields, String currentStage) {

        Optional<LoanDetails> loanOpt = loanDtlsRepo.findTopByApplicationIdAndAppId(applicationId, masterObj.getAppId());
        if (loanOpt.isPresent()) {
            String loanStatus = Optional.ofNullable(loanOpt.get().getLoanStatus()).orElse("");
            if (Constants.ACTIVE.equals(loanStatus) && currentStage.equalsIgnoreCase(Constants.RESANCTION)) {
                logger.debug("Loan Rejection API started");
                return initiateLoanRejection(masterObj, applicationId, header, prop, customerDataFields, coApplicantId, currentStage);
            }
        }
        String memberIdForLoanCreation = masterObj.getProductCode().equalsIgnoreCase(ProductCode.OPEN_MARKET.getUnnatiCode()) ?
                masterObj.getApplicantT24Id() : masterObj.getMemberId();
        logger.debug("Executing loan creation API");

        Mono<Object> loanCreation = this.t24AndCDHService.loanCreation(masterObj.getApplicationId(),
                masterObj.getAppId(), memberIdForLoanCreation, header, prop, coApplicantId);

        return loanCreation.flatMap(loanResponse -> {
            logger.debug("Loan Creation API response {}", loanResponse);
            try {

                String json = (new Gson()).toJson(loanResponse);
                JSONObject apiResp = convertResponseToJson(json);
                JSONObject loanHeader = apiResp.getJSONObject(Constants.HEADER);
                Object requestObj = apiResp.get(Constants.API_REQUEST);
                if (loanHeader.has(Constants.STATUS)
                        && loanHeader.getString(Constants.STATUS).equalsIgnoreCase(ResponseCodes.SUCCESS.getValue())) {
                    String loanId = loanHeader.getString("id");
                    this.loanDtlsRepo.updateT24LoanId(masterObj.getApplicationId(), loanId);
                    this.loanDtlsRepo.updateT24LoanStatus(masterObj.getApplicationId(), Constants.ACTIVE);
                    JSONObject loanBody = apiResp.getJSONObject("body");
                    saveLog(applicationId, Constants.LOAN_CREATION, requestObj.toString(),
                            loanResponse.toString(), ResponseCodes.SUCCESS.getValue(), null, currentStage);

                    return initiateLoanRepaySchedule(masterObj, applicationId, header, prop, currentStage, customerDataFields, loanId);
                }
                if (apiResp.has(Constants.ERROR1) && !apiResp.getJSONObject(Constants.ERROR1).isEmpty()) {
                    JSONArray loanErrors = apiResp.getJSONObject(Constants.ERROR1).getJSONArray(Constants.ERROR_DETAILS);
                    saveLog(applicationId, Constants.LOAN_CREATION, requestObj.toString(),
                            loanResponse.toString(), ResponseCodes.FAILURE.getValue(), loanErrors.toString(), currentStage);
                    logger.debug("LoanErrors :" + loanErrors);
                    List<String> loanErrorList = new ArrayList<>();
                    for (Object field : loanErrors) {
                        JSONObject fieldError = new JSONObject(field.toString());
                        logger.debug("Field Error " + fieldError);
                        if (fieldError.optString(Constants.MESSAGE).equalsIgnoreCase(prop.getProperty(CobFlagsProperties.LOAN_FETCH_INPUT_ERROR.getKey()))) {
                            return t24AndCDHService.loanFetch(applicationId, prop, masterObj, header)
                                    .flatMap(res -> {
                                        logger.debug("Loan Fetch API response: {}", res);
                                        try {
                                            JSONObject apiRes = convertResponseToJson(new Gson().toJson(res));
                                            Object loanFetchRequestObj = apiRes.get(Constants.API_REQUEST);
                                            JSONObject customerResponse = apiRes.optJSONObject("CustomerLoanDetailsResponse");
                                            if (customerResponse == null) {
                                                saveLog(applicationId, Constants.LOAN_FETCH, loanFetchRequestObj.toString(), loanResponse.toString(), ResponseCodes.FAILURE.getValue(), "Customer Response is null", currentStage);
                                                return Mono.just(getFailureApiJson("Loan Fetch Failed due to incorrect T24 response", Constants.LOAN_CREATION));
                                            }
                                            JSONObject statusHeader = customerResponse.optJSONObject("Status");
                                            if (statusHeader != null && statusHeader.optString("successIndicator").equalsIgnoreCase(ResponseCodes.SUCCESS.getValue())) {
                                                JSONArray detailArray;
                                                try {
                                                    detailArray = Optional.ofNullable(customerResponse)
                                                            .map(obj -> obj.optJSONObject("CAGCUSTLNDETSType"))
                                                            .map(obj -> obj.optJSONObject("gCAGCUSTLNDETSDetailType"))
                                                            .map(obj -> obj.optJSONArray("mCAGCUSTLNDETSDetailType"))
                                                            .orElseThrow(() -> new JSONException("Loan Fetch Failed: Missing loan details array"));
                                                } catch (JSONException ex) {
                                                    logger.error("Loan fetch structure invalid: {}", ex.getMessage());
                                                    saveLog(applicationId, Constants.LOAN_FETCH, loanFetchRequestObj.toString(), loanResponse.toString(), ResponseCodes.FAILURE.getValue(), ex.toString(), currentStage);
                                                    return Mono.just(getFailureApiJson("Loan Fetch Failed due to incorrect T24 response", Constants.LOAN_CREATION));
                                                }
                                                String matchedLoanId = "";
                                                String productId = ProductCode.getCdhCodeByUnnatiCode(masterObj.getProductCode());
                                                if (masterObj.getProductCode().equalsIgnoreCase(ProductCode.UNNATI_RENEW.getUnnatiCode())){
                                                    productId = ProductCode.UNNATI.getCdhCode(); //Since product ID is same for Unnati and Unnati Renewal in T24
                                                }
                                                for (int i = 0; i < detailArray.length(); i++) {
                                                    JSONObject detail = detailArray.getJSONObject(i);
                                                    String[] loanIds = detail.optString("LoanID", "").split(Constants.REGEX_DELIMITER);
                                                    String[] loanAmounts = detail.optString("LoanAmount", "").split(Constants.REGEX_DELIMITER);
                                                    String[] statuses = detail.optString("Status", "").split(Constants.REGEX_DELIMITER);
                                                    String[] productIds = detail.optString("LoanProductID", "").split(Constants.REGEX_DELIMITER);
                                                    Optional<LoanDetails> loanRecord = loanDtlsRepo.findTopByApplicationIdAndAppId(masterObj.getApplicationId(), masterObj.getAppId());
                                                    BigDecimal sanctionedLoanAmount = loanRecord.map(LoanDetails::getSanctionedLoanAmount).orElse(new BigDecimal(0));
                                                    for (int j = 0; j < productIds.length; j++) {
                                                        if (productIds[j].equalsIgnoreCase(productId)) {
                                                            if (j < loanAmounts.length && j < statuses.length && j < loanIds.length) {
                                                                if (loanAmounts[j].equalsIgnoreCase(sanctionedLoanAmount.toString())
                                                                        && statuses[j].equalsIgnoreCase(Constants.APPROVED)) {
                                                                    matchedLoanId = loanIds[j];
                                                                    this.loanDtlsRepo.updateT24LoanId(masterObj.getApplicationId(), loanIds[j]);
                                                                    this.loanDtlsRepo.updateT24LoanStatus(masterObj.getApplicationId(), Constants.ACTIVE);
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    logger.debug("Loan ID: {}", Arrays.toString(loanIds));
                                                    logger.debug("Loan Sanctioned AMount: {}", sanctionedLoanAmount);
                                                    logger.debug("Loan Amounts in loan fetch: {}", Arrays.toString(loanAmounts));
                                                }
                                                if (!matchedLoanId.isEmpty()) {
                                                    saveLog(applicationId, Constants.LOAN_FETCH, loanFetchRequestObj.toString(), res.toString(), ResponseCodes.SUCCESS.getValue(), "", currentStage);
                                                    return initiateLoanRepaySchedule(masterObj, applicationId, header, prop, currentStage, customerDataFields, matchedLoanId);
                                                } else {
                                                    logger.warn("No matched Loan ID found for product {}", productId);
                                                    saveLog(applicationId, Constants.LOAN_FETCH,loanFetchRequestObj.toString(), res.toString(), ResponseCodes.FAILURE.getValue(), "No matched Loan ID found for product : " + productId, currentStage);
                                                    return Mono.just(getFailureApiJson("Another active loan is in progress for this customer in T24", Constants.LOAN_CREATION));
                                                }
                                            } else {
                                                logger.error("Loan fetch API response did not indicate success.");
                                                saveLog(applicationId, Constants.LOAN_FETCH, loanFetchRequestObj.toString(), res.toString(), ResponseCodes.FAILURE.getValue(), "Loan fetch API response did not indicate success.", currentStage);
                                                return Mono.just(getFailureApiJson("Loan Fetch Failed. Invalid Loan Fetch Response", Constants.LOAN_CREATION));
                                            }
                                        } catch (Exception e) {
                                            logger.error("Unexpected error during loan Fetch response processing", e);
                                            saveLog(applicationId, Constants.LOAN_FETCH, this.t24AndCDHService.getRequestLog(), res.toString(), ResponseCodes.FAILURE.getValue(), "Unexpected error during loan Fetch response processing", currentStage);
                                            return Mono.just(getFailureApiJson("Exception during Loan Creation", Constants.LOAN_CREATION));
                                        }
                                    }).onErrorResume(e -> {
                                        logger.error("Error during Loan Fetch API: ", e);
                                        saveLog(applicationId, Constants.LOAN_FETCH, this.t24AndCDHService.getRequestLog(), e.getMessage(), ResponseCodes.FAILURE.getValue(), "Error during Loan Fetch API: " + e.getMessage(), currentStage);
                                        return Mono.just(getFailureApiJson("Error during Loan Creation", Constants.LOAN_CREATION));
                                    });
                        } else {
                            loanErrorList.add(fieldError.optString(Constants.FIELD_NAME, Constants.ERROR2) + " - " + fieldError.optString(Constants.MESSAGE, "Unknown Error").trim());
                            logger.debug("Error List in Loan creation API {}", loanErrorList);
                            saveLog(applicationId, Constants.LOAN_CREATION, requestObj.toString(), loanResponse.toString(), ResponseCodes.FAILURE.getValue(), loanErrorList.toString(), currentStage);
                            Response failureJson = getFailureApiJson(loanErrorList.toString(), Constants.LOAN_CREATION);
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
            saveLog(applicationId, Constants.LOAN_CREATION, this.t24AndCDHService.getRequestLog(), null,
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
                    .findByApplicationIdAndApplicationStatusInOrderByCreateTsDesc(custmrDataFields.getApplicationId(), statusList);

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
                    Constants.SMS_DOC_DOWNLOAD_URL
            );

            String requestType = isDisbursed ? Constants.DISBURSEMENT : Constants.SANCTION;

            documentDownloadURL = documentDownloadURL
                    .replace("#APPLICATIONID#", applicationId)
                    .replace("#REQUESTTYPE#", requestType);

            logger.debug("Document Download URL: {}", documentDownloadURL);
            if (isDisbursed) {
                sendSmsAndEmailRequestObject
                        .setAttachmentContent(loanId + "|~|" + amountStr + "|~|" + sactionedDateStr + "|~|" + LocalDate.now().toString() + "|~|" + documentDownloadURL);
                resp = sendSmsAndEmailService.sendSmsAndEmailService(sendSmsandEmailApiRequest, prop, SmsStage.DISBURSED);
            } else {
                sendSmsAndEmailRequestObject
                        .setAttachmentContent(loanId + "|~|" + amountStr + "|~|" + disbursementDateStr + "|~|" + documentDownloadURL);
                resp = sendSmsAndEmailService.sendSmsAndEmailService(sendSmsandEmailApiRequest, prop, SmsStage.SANCTION);
            }
        } else {
            logger.error("Invalid Application Master");
        }
        return resp;
    }

    private Mono<Object> initiateLoanRejection(ApplicationMaster masterObj, String applicationId, Header header, Properties prop, FetchDeleteUserFields customerDataFields, String coApplicantId, String currentStage) {
        logger.debug("Executing loan Rejection API");
        Mono<Object> loanRejection = this.t24AndCDHService.loanRejection(masterObj.getApplicationId(), prop,
                masterObj.getAppId(), header);
        return loanRejection.flatMap(loanResponse -> {
            logger.debug("Loan Rejection API response {}", loanResponse);
            try {
                String json = (new Gson()).toJson(loanResponse);
                JSONObject apiResp = convertResponseToJson(json);
                Object requestObj = apiResp.get(Constants.API_REQUEST);
                JSONObject loanHeader = apiResp.getJSONObject(Constants.HEADER);
                if (loanHeader.has(Constants.STATUS)
                        && loanHeader.getString(Constants.STATUS).equalsIgnoreCase(ResponseCodes.SUCCESS.getValue())) {
                    // Saving log
                    saveLog(applicationId, Constants.LOAN_REJECTION, requestObj.toString(),
                            loanResponse.toString(), ResponseCodes.SUCCESS.getValue(), null, currentStage);
                    loanDtlsRepo.updateT24LoanStatus(applicationId, Constants.INACTIVE);
                    return initiateLoanCreation(masterObj, coApplicantId, applicationId, header, prop,
                            customerDataFields, currentStage);
                } else if (apiResp.has(Constants.ERROR1) && !apiResp.getJSONObject(Constants.ERROR1).isEmpty()) {
                    JSONArray loanErrors = apiResp.getJSONObject(Constants.ERROR1).getJSONArray(Constants.ERROR_DETAILS);
                    logger.debug("LoanErrors " + loanErrors);
                    List<String> loanErrorList = new ArrayList<>();
                    for (Object field : loanErrors) {
                        JSONObject fieldError = new JSONObject(field.toString());
                        logger.debug("Field Error " + fieldError);
                        loanErrorList.add(
                                fieldError.optString(Constants.FIELD_NAME, Constants.ERROR2) + " - " + fieldError.getString(Constants.MESSAGE).trim());
                    }
                    logger.debug("Error List in Loan Rejection API {}", loanErrorList);
                    saveLog(applicationId, Constants.LOAN_REJECTION, requestObj.toString(),
                            loanResponse.toString(), ResponseCodes.FAILURE.getValue(), loanErrorList.toString(), currentStage);
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
            saveLog(applicationId, Constants.LOAN_REJECTION, this.t24AndCDHService.getRequestLog(), null,
                    ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
            return Mono.just(getFailureApiJson("Error during Loan Rejection", Constants.LOAN_REJECTION));
        });
    }

    private Mono<Object> initiateLoanRepaySchedule(ApplicationMaster masterObj, String applicationId, Header header, Properties prop, String currentStage, FetchDeleteUserFields customerDataFields, String loanId) {
        logger.debug("Executing loanRepaySchedule API");

        Mono<Object> loanRepaySchd = this.t24AndCDHService.loanRepaySchedule(masterObj.getApplicationId(), prop,
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
                Object requestObj = apiResp.get(Constants.API_REQUEST);
                if (apiResp.has("body") && apiResp.opt("body") instanceof JSONArray) {
                    saveLog(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE, requestObj.toString(),
                            loanResponse.toString(), ResponseCodes.SUCCESS.getValue(), null, currentStage);
                    masterObj.setRemarks(customerDataFields.getRemarks());
                    masterObj.setUpdatedBy(customerDataFields.getUserId());
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
                    saveLog(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE, requestObj.toString(),
                            loanResponse.toString(), ResponseCodes.FAILURE.getValue(), null, currentStage);
                    return Mono.just(getFailureApiJson(Constants.LOAN_CREATION_SUCCESS + t24LoanId + ", but we're experiencing a temporary issue with fetching the repayment details. Please try again after sometime", Constants.LOAN_CREATION));
                } else {
                    logger.error("loanRepaySchedule API failed: Empty or missing error details.");
                    return Mono.just(getFailureApiJson(Constants.LOAN_CREATION_SUCCESS + t24LoanId + ", but we're experiencing a temporary issue with fetching the repayment details. Please try again after sometime", Constants.LOAN_CREATION));
                }
            } catch (Exception e) {
                saveLog(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE, this.t24AndCDHService.getRequestLog(),
                        loanResponse.toString(), ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
                logger.error("Unexpected error during loan response processing", e);
                return Mono.just(getFailureApiJson("Loan Created Successfully, but we're experiencing a temporary issue with fetching the repayment details. Please try again after sometime", Constants.LOAN_CREATION));
            }
        });
    }

    private Mono<Object> initiateDisbursementRepaySchedule(ApplicationMaster masterObj, String applicationId, Header header, Properties prop, String currentStage, FetchDeleteUserFields customerDataFields, PopulateapplnWFRequestFields reqFields, PopulateapplnWFRequest req) {
        logger.debug("Executing loanRepaySchedule API");
        Mono<Object> loanRepaySchd = this.t24AndCDHService.disbRepaySchedule(masterObj.getApplicationId(), prop,
                masterObj.getAppId(), header);
        return loanRepaySchd.flatMap(loanResponse -> {
            logger.debug("loanRepaySchedule API response {}", loanResponse);
            try {
                String json = (new Gson()).toJson(loanResponse);
                JSONObject apiResp = convertResponseToJson(json);
                Object requestObj = apiResp.get(Constants.API_REQUEST);
                if (apiResp.has("body")) {
                    // Saving log
                    saveLog(applicationId, Constants.DISBURSEMENT_REPAY_SCHEDULE, requestObj.toString(),
                            loanResponse.toString(), ResponseCodes.SUCCESS.getValue(), null, currentStage);
                    masterObj.setRemarks(customerDataFields.getRemarks());
                    masterObj.setUpdatedBy(customerDataFields.getUserId());

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
                    saveLog(applicationId, Constants.DISBURSEMENT_REPAY_SCHEDULE, requestObj.toString(),
                            loanResponse.toString(), ResponseCodes.FAILURE.getValue(), loanResponse.toString(), currentStage);

                    //return executeDMSActivity(masterObj, header, prop);
                    return Mono.just(getFailureApiJson(Constants.LOAN_DISBURSE_SUCCESS + ", but we're experiencing a temporary issue with fetching the repayment details. Please try again after sometime", Constants.LOAN_DISBURSEMENT));
                }
                logger.error("loanRepaySchedule API failed: Empty or missing error details.");
                return Mono.just(getFailureApiJson(Constants.LOAN_DISBURSE_SUCCESS + ", but we're experiencing a temporary issue with fetching the repayment details. Please try again after sometime", Constants.LOAN_DISBURSEMENT));
            } catch (Exception e) {
                saveLog(applicationId, Constants.DISBURSEMENT_REPAY_SCHEDULE, this.t24AndCDHService.getRequestLog(),
                        loanResponse.toString(), ResponseCodes.FAILURE.getValue(), e.getMessage(), currentStage);
                logger.error("Unexpected error during loan response processing", e);
                return Mono.just(getFailureApiJson(Constants.LOAN_DISBURSE_SUCCESS + ", but we're experiencing a temporary issue with fetching the repayment details. Please try again after sometime", Constants.LOAN_DISBURSEMENT));
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

        Mono<Object> loanDisb = t24AndCDHService.loanDisbursement(masterObj.getApplicationId(), prop,
                masterObj.getAppId(), header);

        return loanDisb.flatMap(loanResponse -> {
            try {
                logger.debug("Loan Disbursement API response: {}", loanResponse);
                String json = (loanResponse instanceof String) ? (String) loanResponse
                        : new Gson().toJson(loanResponse);
                JSONObject apiResp = convertResponseToJson(json);
                Object requestObj = apiResp.get(Constants.API_REQUEST);
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
                                String allowedDisbError = prop.getProperty(CobFlagsProperties.NONBLOCKING_DISBURSEMENT_ERROR.getKey());
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
                        if(ResponseCodes.SUCCESS.getValue().equalsIgnoreCase(status)){
                            JSONObject body = apiResp.getJSONObject("body");
                            String lnRemActAm = body.getString("lnRemitAmt");
                            LoanDetails loanDetails = loanDtlsRepo.findByApplicationId(masterObj.getApplicationId());
                            LoanDetailsPayload payload = new LoanDetailsPayload();
                            payload.setRemittanceAmount(lnRemActAm.toString());
                            loanDetails.setPayload(payload);
                            loanDtlsRepo.save(loanDetails);
                        }

                        saveLog(masterObj.getApplicationId(), Constants.LOAN_DISBURSEMENT, requestObj.toString(),
                                loanResponse.toString(), ResponseCodes.SUCCESS.getValue(), null, currentStage);

                        //Commented for testing purpose - 12/08
//							return executeDMSActivity(masterObj, header, prop);
                        return initiateDisbursementRepaySchedule(masterObj, masterObj.getApplicationId(), header, prop, currentStage, customerDataFields, reqFields, req);
                    }

                    if (apiResp.has(Constants.ERROR1) && !apiResp.getJSONObject(Constants.ERROR1).isEmpty()) {
                        List<String> loanErrorList = extractErrorMessages(apiResp.getJSONObject(Constants.ERROR1));
                        // If empty resp recieved returning service down error
                        if (loanResponse instanceof JSONArray && ((JSONArray) loanResponse).isEmpty()) {
                            logger.error("Empty response received from Disb api.");
                            saveLog(masterObj.getApplicationId(), Constants.LOAN_DISBURSEMENT, requestObj.toString(),
                                    loanResponse.toString(), ResponseCodes.FAILURE.getValue(), Constants.SERVICE_DOWN, currentStage);
                            return Mono.just(getFailureApiJson(Constants.SERVICE_DOWN, Constants.LOAN_DISBURSEMENT));
                        }

                        saveLog(masterObj.getApplicationId(), Constants.LOAN_DISBURSEMENT, requestObj.toString(),
                                loanResponse.toString(), ResponseCodes.FAILURE.getValue(), loanErrorList.toString(),
                                currentStage);
                        return Mono.just(getFailureApiJson(loanErrorList.toString(), Constants.LOAN_DISBURSEMENT));
                    }
                }
                saveLog(masterObj.getApplicationId(), Constants.LOAN_DISBURSEMENT, requestObj.toString(),
                        loanResponse.toString(), ResponseCodes.FAILURE.getValue(), Constants.SERVICE_DOWN, currentStage);
                logger.error("Loan disbursement API failed: Unhandled or empty response.");
                return Mono.just(getFailureApiJson(Constants.SERVICE_DOWN, Constants.LOAN_DISBURSEMENT));
            } catch (Exception e) {
                logger.error("Unexpected error during loan disbursement processing", e);

                saveLog(masterObj.getApplicationId(), Constants.LOAN_DISBURSEMENT, this.t24AndCDHService.getRequestLog(), null,
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
        String applicationId = customerDataFields.getApplicationId();
        String remarks = customerDataFields.getRemarks();
        if (!Constants.APPROVER.equalsIgnoreCase(roleId)
                && !Constants.BM.equalsIgnoreCase(roleId)) {
            responseHeader.setResponseCode(ResponseCodes.INVALID_ROLE.getKey());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
            return Mono.just(response);
        }
        CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
        Set<AppStatus> validStatuses = new HashSet<>(Arrays.asList(
                AppStatus.REJECTED, AppStatus.RPCVERIFIED, AppStatus.DBKITGENERATED,
                AppStatus.RPCBANKUPDATE, AppStatus.IPUSHBACK, AppStatus.CACOMPLETED, AppStatus.RESANCTION
        ));

        if (isValidStatus(status, validStatuses)) {
            List<String> applnStatus = new ArrayList<>();
            applnStatus.add(AppStatus.SANCTIONED.getValue());
            applnStatus.add(AppStatus.DBPUSHBACK.getValue());
            logger.debug("Application statuses to check: {}", applnStatus);

            Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
                    .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
                            customerDataFields.getApplicationId(), customerDataFields.getVersionNum(), applnStatus);
            if (masterObjDb.isPresent()) {
                ApplicationMaster masterObj = masterObjDb.get();
        logger.debug("Received status: {}", status);
        if (AppStatus.REJECTED.getValue().equalsIgnoreCase(status)
                || AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status)) {
            logger.debug("Loan Rejection API");
            t24AndCDHService.loanRejection(fetchDeleteUserRequest.getRequestObj().getApplicationId(), prop,
                    fetchDeleteUserRequest.getRequestObj().getAppId(), null);
        }
        if(Constants.SUBMIT.equalsIgnoreCase(action) || Constants.APPROVED.equalsIgnoreCase(action)){
            boolean isDocGenDone = documentsRepository.existsByApplicationId(applicationId);
            List<String> missingSteps = new ArrayList<>();

                    Set<ProductCode> additionalProducts = EnumSet.of(ProductCode.FAMILY_WELFARE, ProductCode.UNNATI_SUPPLEMENTARY,
                            ProductCode.UNNATI_RESTART, ProductCode.UNNATI_EMERGENCY);
                    boolean isAdditionalProduct = additionalProducts.stream()
                            .anyMatch(productCode -> productCode.getUnnatiCode().equalsIgnoreCase(masterObj.getProductCode()));
                    if (!isAdditionalProduct) {
                        boolean isEnachDone = enachRepository.existsByApplicationId(applicationId);
            if (!isEnachDone) {
                missingSteps.add("eNACH");
            }
                    }
            if (!isDocGenDone) {
                missingSteps.add("Document Generation");
            }
            if (!missingSteps.isEmpty()) {
                String message = String.format(
                        "Cannot proceed with submission. The following steps are incomplete: %s.",
                        String.join(", ", missingSteps)
                );
                logger.debug("Validation failed for applicationId: {}. Missing steps: {}", applicationId, missingSteps);
                        responseBody.setResponseObj(message);
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                responseHeader.setResponseMessage(message);
                response.setResponseHeader(responseHeader);
                response.setResponseBody(responseBody);
                return Mono.just(response);
            }
        }

                logger.info("ApplicationMaster found for applicationId: {}", customerDataFields.getApplicationId());
                Gson gson = new Gson();
                masterObj.setRemarks(null);
                masterObj.setUpdatedBy(customerDataFields.getUserId());
                cobService.updateStatus(masterObj, status);
                logger.debug("Updated application status to: {}", status);
                if (action.equalsIgnoreCase(Constants.REJECT) || action.equalsIgnoreCase(Constants.DBSANCTIONPUSHBACK)
                        || action.equalsIgnoreCase(Constants.DBCAPUSHBACK) || action.equalsIgnoreCase(Constants.RESANCTION)) {
                    cobService.handleDeleteAllDocuments(prop, fetchDeleteUserRequest.getRequestObj().getAppId(), fetchDeleteUserRequest.getRequestObj().getApplicationId());
                    int deletedEnachRecords = enachRepository.deleteByApplicationId(fetchDeleteUserRequest.getRequestObj().getApplicationId());
                    if (deletedEnachRecords == 0) {
                        logger.debug("No ENACH records found for given Application ID : {}", fetchDeleteUserRequest.getRequestObj().getApplicationId());
                    }
                    int deletedUdhyamRecords = udhyamRepository.deleteByApplicationId(fetchDeleteUserRequest.getRequestObj().getApplicationId());
                    if (deletedUdhyamRecords == 0) {
                        logger.debug("No Udhyam records found for given Application ID : {}", fetchDeleteUserRequest.getRequestObj().getApplicationId());
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
                        if (StringUtils.isBlank(customerDataFields.getRemarks())) {
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
                                    appDoc.getPayloadColumn(), ApplicationDocumentsPayload.class
                            );
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
                Optional<BankDetails> bankDetailsOpt = bankDetailsRepository.findBankDetailsByCustomerType(Constants.COAPPLICANT, customerDataFields.getApplicationId());
                if (bankDetailsOpt.isPresent()) {
                    BankDetails bankDetails = bankDetailsOpt.get();
                    BankDetailsPayload payload = new Gson().fromJson(bankDetails.getPayloadColumn(), BankDetailsPayload.class);
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
                responseBody.setResponseObj(Constants.APPLICATION_UPDATED_ERROR);
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
        if (!CobFlagsProperties.RPC.getKey().equalsIgnoreCase(roleId)) {
            responseHeader.setResponseCode(ResponseCodes.INVALID_ROLE.getKey());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
            return Mono.just(response);
        }
        CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
        logger.debug("Received status: {}", status);

        Set<AppStatus> validStatuses = new HashSet<>(Arrays.asList(
                AppStatus.REJECTED, AppStatus.SANCTIONED, AppStatus.DBKITVERIFIED,
                AppStatus.DBPUSHBACK, AppStatus.RESANCTION
        ));

        if (isValidStatus(status, validStatuses)) {
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
                cobService.updateStatus(masterObj, status);
                logger.debug("Updated application status to: {}", status);
                if (action.equalsIgnoreCase(Constants.REJECT) || action.equalsIgnoreCase(Constants.RESANCTION)) {
                    cobService.handleDeleteAllDocuments(prop, fetchDeleteUserRequest.getRequestObj().getAppId(), fetchDeleteUserRequest.getRequestObj().getApplicationId());
                    int deletedEnachRecords = enachRepository.deleteByApplicationId(fetchDeleteUserRequest.getRequestObj().getApplicationId());
                    if (deletedEnachRecords == 0) {
                        logger.debug("No ENACH records found for given Application ID : {}", fetchDeleteUserRequest.getRequestObj().getApplicationId());
                    }
                    int deletedUdhyamRecords = udhyamRepository.deleteByApplicationId(fetchDeleteUserRequest.getRequestObj().getApplicationId());
                    if (deletedUdhyamRecords == 0) {
                        logger.debug("No Udhyam records found for given Application ID : {}", fetchDeleteUserRequest.getRequestObj().getApplicationId());
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
                if (AppStatus.DBPUSHBACK.getValue().equalsIgnoreCase(status) || AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(status)) {
                    Optional<DBKITStageVerification> dbkitStageVerification = dbkitStageVerificationRepository
                            .findById(customerDataFields.getApplicationId());
                    if (dbkitStageVerification.isPresent()) {
                        DBKITStageVerification data = dbkitStageVerification.get();
                        wf.setRemarks(data.getQueries());
                        data.setVerifiedStages(null);
                        dbkitStageVerificationRepository.save(data);

                    }
                    Optional<List<Documents>> documentsOpt = documentsRepository.findByApplicationId(masterObj.getApplicationId());
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
                                    appDoc.getPayloadColumn(), ApplicationDocumentsPayload.class
                            );

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
                responseBody.setResponseObj(Constants.APPLICATION_UPDATED_ERROR);
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

    private void prepareCkycDocumentsForDisbursedCases(ApplicationMaster appMaster, Properties prop) {
        logger.debug("Preparing CKYC documents for applicationId: {}", appMaster.getApplicationId());
        if(doCkycDocumentExist(appMaster, prop)){
            logger.debug("CKYC documents already exist for applicationId: {}. Skipping preparation.", appMaster.getApplicationId());
            return;
        }
        String applicationId = appMaster.getApplicationId();
        List<String> ckycDocTypes = Arrays.asList(
                Constants.PRIMARY_KYC,
                Constants.CUSTOMER_PHOTOGRAPH
        );
        Gson gson = new Gson();
        Path basePath = Paths.get(prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()));
        logger.debug("Base path for documents: {}", basePath.toString());
        List<JsonObject> ckycDocumentDetails = new ArrayList<>();
        collectDocuments(applicationId, Constants.APPLICANT, ckycDocTypes, gson, basePath, ckycDocumentDetails);
        collectDocuments(applicationId, Constants.COAPPLICANT, ckycDocTypes, gson, basePath, ckycDocumentDetails);
        if (ckycDocumentDetails.isEmpty()) {
            logger.error("No CKYC documents found for applicationId: {}. Skipping preparation.", applicationId);
            return;
        }

        JsonObject customerIds = getApplicantAndCoAppId(appMaster);
        if(customerIds == null || !customerIds.has("applicantCustomerId") || !customerIds.has("coApplicantCustomerId")){
            logger.error("Unable to retrieve applicant or co-applicant customer IDs for applicationId: {}. Skipping CKYC document preparation.", applicationId);
            return;
        }
        String applicantCustomerId = customerIds.get("applicantCustomerId").getAsString();
        String coApplicantCustomerId = customerIds.get("coApplicantCustomerId").getAsString();

        Map<String, Integer> docSequenceMap = new HashMap<>();
        ckycDocumentDetails.sort(
                Comparator
                        .comparing((JsonObject o) -> o.get("customerType").getAsString())
                        .thenComparing(o -> o.get("documentType").getAsString())
                        .thenComparingInt(o -> o.get("documentLevel").getAsInt())
        );
        for (JsonObject doc : ckycDocumentDetails) {
            try {
                Path documentPath = Paths.get(doc.get("documentPath").getAsString());
                String documentName = doc.get("documentName").getAsString();
                String documentType = doc.get("documentType").getAsString();
                String customerType = doc.get("customerType").getAsString();

                String customerId = customerType.equalsIgnoreCase(Constants.APPLICANT)
                        ? applicantCustomerId
                        : coApplicantCustomerId;

                Path originalFilePath = documentPath.resolve(documentName);

                if (!Files.exists(originalFilePath)) {
                    logger.debug("Original document file not found: {}. Skipping this document.", originalFilePath.toString());
                    continue;
                }
                String key = customerId + "_" + documentType;
                int sequence = docSequenceMap.getOrDefault(key, 0) + 1;
                docSequenceMap.put(key, sequence);

                String newFileName = generateCkycFileName(documentType, sequence, documentName, customerId);

                Path ckycFolderPath = documentPath.resolve(Constants.CKYC_DOCUMENTS);
                Files.createDirectories(ckycFolderPath);

                Path targetFilePath = ckycFolderPath.resolve(newFileName);
                Files.copy(originalFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);

            } catch (Exception e) {
                logger.error("Error while preparing CKYC document: {}", e.getMessage(), e);
            }
        }
    }

    private void collectDocuments(String applicationId,
                                  String customerType,
                                  List<String> docTypes,
                                  Gson gson,
                                  Path basePath,
                                  List<JsonObject> targetList) {
        List<ApplicationDocuments> documents =
                applicationDocumentsRepository
                        .fetchDocumentsByApplicationIdAndDocumentTypeListAndCustomerType(
                                applicationId, docTypes, customerType
                        );
        if (documents == null || documents.isEmpty()) {
            logger.error("No documents found for applicationId: {}, customerType: {}, docTypes: {}",
                    applicationId, customerType, docTypes);
            return;
        }
        for (ApplicationDocuments doc : documents) {
            try {
                ApplicationDocumentsPayload payload =
                        gson.fromJson(doc.getPayloadColumn(), ApplicationDocumentsPayload.class);
                JsonObject json = new JsonObject();
                json.addProperty("customerType", customerType);
                json.addProperty("documentType", payload.getDocumentType());
                json.addProperty("documentPath",
                        basePath.resolve(payload.getDocumentLoc()).toString());
                json.addProperty("documentLevel", payload.getDocLevel());
                json.addProperty("documentName", payload.getDocumentFileName());
                json.add("applicationDocumentRecord", gson.toJsonTree(doc));
                targetList.add(json);
                logger.debug("Collected document for CKYC preparation: applicationId={}, customerType={}, documentType={}",
                        applicationId, customerType, payload.getDocumentType());
            } catch (Exception e) {
                logger.error("Error while collecting document for CKYC preparation: applicationId={}, customerType={}, error={}",
                        applicationId, customerType, e.getMessage(), e);
            }
        }
    }

    private String extractExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        int lastSeparatorIndex = Math.max(
                fileName.lastIndexOf('/'),
                fileName.lastIndexOf('\\')
        );
        if (lastDotIndex > 0 && lastDotIndex > lastSeparatorIndex && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }

    private String generateCkycFileName(
            String documentType,
            int sequence,
            String originalName,
            String customerId
    ) {
        if (originalName == null || customerId == null || documentType == null) {
            return originalName;
        }

        String extension = extractExtension(originalName);
        String baseName;

        if (Constants.PRIMARY_KYC.equalsIgnoreCase(documentType)) {
            if (sequence == 1) {
                baseName = customerId + Constants.CKYC_VOTER_ID_1;
            } else if (sequence == 2) {
                baseName = customerId + Constants.CKYC_VOTER_ID_2;
            } else {
                baseName = customerId + "_Voter ID_OTHER_" + sequence;
            }
        } else if (Constants.CUSTOMER_PHOTOGRAPH.equalsIgnoreCase(documentType)) {
            if (sequence == 1) {
                baseName = customerId + Constants.CKYC_PHOTOGRAPH;
            } else {
                baseName = customerId + Constants.CKYC_PHOTOGRAPH+"_OTHER_" + sequence;
            }
        } else {
            baseName = customerId + "_DOC_" + sequence;
        }
        return baseName + extension;
    }

    private JsonObject getApplicantAndCoAppId(ApplicationMaster appMaster) {
        JsonObject json = new JsonObject();
        String applicantCustomerId = appMaster.getSearchCode2();
        if(Constants.OPENMARKET_LOAN_PRODUCT_CODE.equalsIgnoreCase(appMaster.getProductCode())){
            applicantCustomerId = appMaster.getApplicantT24Id();
        }
        json.addProperty("applicantCustomerId", applicantCustomerId);
        String coApplicantCustomerId = loanDtlsRepo.fetchCoapplicantIdByApplicationId(appMaster.getApplicationId());
        if(null == coApplicantCustomerId){
            logger.debug("No co-applicant found for applicationId: {}. CKYC documents will not be prepared.", appMaster.getApplicationId());
            return null;
        }
        json.addProperty("coApplicantCustomerId", coApplicantCustomerId);
        return json;
    }

    private boolean doCkycDocumentExist(ApplicationMaster appMaster, Properties prop){
        String applicationId = appMaster.getApplicationId();
        String productCode = appMaster.getProductCode();
        JsonObject customerIds = getApplicantAndCoAppId(appMaster);
        if(customerIds == null){
            logger.debug("Customer IDs not found for applicationId: {}. Cannot check CKYC documents.", applicationId);
            return false;
        }
        String applicantCustomerId = customerIds.get("applicantCustomerId").getAsString();
        String coApplicantCustomerId = customerIds.get("coApplicantCustomerId").getAsString();
        Path basePath = Paths.get(prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()));
        Path ckycFolderPath = basePath.resolve(Constants.APPID).resolve(Constants.LOAN)
                .resolve(applicationId).resolve(Constants.CKYC_DOCUMENTS);
        if (!Files.exists(ckycFolderPath)) {
            return false;
        }
        List<String> expectedFiles = new ArrayList<>();
        // Applicant
        expectedFiles.add(applicantCustomerId + Constants.CKYC_VOTER_ID_1);
        expectedFiles.add(applicantCustomerId + Constants.CKYC_VOTER_ID_2);
        expectedFiles.add(applicantCustomerId + Constants.CKYC_PHOTOGRAPH);
        // Co-applicant
        expectedFiles.add(coApplicantCustomerId + Constants.CKYC_VOTER_ID_1);
        expectedFiles.add(coApplicantCustomerId + Constants.CKYC_VOTER_ID_2);
        expectedFiles.add(coApplicantCustomerId + Constants.CKYC_PHOTOGRAPH);
        try (Stream<Path> stream = Files.list(ckycFolderPath)) {

            Set<String> existingFileNames = stream
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toSet());

            for (String expected : expectedFiles) {
                boolean exists = existingFileNames.stream()
                        .anyMatch(name -> name.startsWith(expected));
                if (!exists) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            logger.error("Error reading CKYC directory: {}", ckycFolderPath, e);
            return false;
        }
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "disbursementApplicationMovementFallback")
    public Mono<Object> disbursementApplicationMovement(FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
                                                        Properties prop, String roleId) {

        logger.info("Starting disbursmentApplicationMovement");
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        response.setResponseHeader(responseHeader);
        if (!Constants.APPROVER.equalsIgnoreCase(roleId)
                && !Constants.BM.equalsIgnoreCase(roleId)) {
            responseHeader.setResponseCode(ResponseCodes.INVALID_ROLE.getKey());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
            return Mono.just(response);
        }
        FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest.getRequestObj();
        String status = customerDataFields.getStatus();
        String action = customerDataFields.getWorkFlow().getAction();
        String applicationId = customerDataFields.getApplicationId();
        CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
        logger.debug("Received status: {} : action :{}", status, action);

        Set<AppStatus> validStatuses = new HashSet<>(Arrays.asList(
                AppStatus.REJECTED, AppStatus.DISBURSED, AppStatus.RPCVERIFIED, AppStatus.RESANCTION
        ));
        if (isValidStatus(status, validStatuses)) {
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
                    //Loan Disbursement
                    LoanDetails loanDetails = loanDtlsRepo.findByApplicationId(applicationId);
                    if (loanDetails == null || loanDetails.getSanctionedLoanAmount() == null) {
                        return Mono.just(getFailureApiJson("Sanctioned Loan Amount not found", "Disbursement Application Movement"));
                    }

                    BigDecimal sanctionedAmount = loanDetails.getSanctionedLoanAmount();
                    BigDecimal bmRecommendedAmt = loanDetails.getBmRecommendedLoanAmount();
                    Optional<CibilDetails> cibilOpt =
                            cibilDetailsRepository.findCibilDetailsByCustomerTypeAndApplicationId(
                                    Constants.COAPPLICANT,
                                    applicationId
                            );
                    if (!cibilOpt.isPresent()) {
                        cibilOpt = cibilDetailsRepository.findCibilDetailsByCustomerTypeAndApplicationId(
                                Constants.APPLICANT,
                                applicationId
                        );
                        if (!cibilOpt.isPresent()) {
                            logger.error("Cibil details not found for applicationId: {}", applicationId);
                        return Mono.just(getFailureApiJson("Cibil details not present", "Disbursement Application Movement"));
                    }
                    }
                    if (StringUtils.isBlank(cibilOpt.get().getRequest())) {

                        logger.warn("Co-Applicant BRE request is null/blank, falling back to Applicant CibilDetails");

                        cibilOpt = cibilDetailsRepository.findCibilDetailsByCustomerTypeAndApplicationId(Constants.APPLICANT, customerDataFields.getApplicationId()
                        );
                    }

                    CibilDetailsPayload payload =
                            gson.fromJson(cibilOpt.get().getPayloadColumn(), CibilDetailsPayload.class);
                    String eligibleAmtStr = payload.getEligibleAmt();
                    if (StringUtils.isBlank(eligibleAmtStr)) {
                        return Mono.just(getFailureApiJson("BRE eligible amount missing", "Disbursement Application Movement"));
                    }
                    BigDecimal eligibleAmount;
                    try {
                        eligibleAmount = new BigDecimal(eligibleAmtStr);
                    } catch (NumberFormatException e) {
                        return Mono.just(getFailureApiJson("Invalid BRE eligible amount", "Disbursement Application Movement"));
                    }
                    if (eligibleAmount.compareTo(sanctionedAmount) < 0) {
                        logger.info(
                                "Eligible amount {} is less than sanctioned amount {} for applicationId {}",
                                eligibleAmount, sanctionedAmount, applicationId
                        );
                        return Mono.just(getFailureApiJson("Sanctioned amount is greater than BRE eligible amount, please verify", "Disbursement Application Movement"));
                    }
                    if (bmRecommendedAmt.compareTo(eligibleAmount) < 0) {
                        logger.info(
                                "Bm recommended amount {} is less than BRE eligible amount {} for applicationId {}",
                                bmRecommendedAmt, eligibleAmount, applicationId
                        );
                        return Mono.just(getFailureApiJson("Bm recommended amount is less than BRE eligible amount, please verify", "Disbursement Application Movement"));
                    }
                    if (bmRecommendedAmt.compareTo(sanctionedAmount) < 0) {
                        logger.info(
                                "BM recommended amount {} is less than sanctioned amount {} for applicationId {}",
                                bmRecommendedAmt, sanctionedAmount, applicationId
                        );
                        return Mono.just(getFailureApiJson("Bm recommended amount is less than sanctioned amount, please verify", "Disbursement Application Movement"));
                    }

                    try{
                        prepareCkycDocumentsForDisbursedCases(masterObj, prop);
                    }catch (Exception e){
                        logger.error("Error while preparing CKYC documents for disbursement: {}", e.getMessage(), e);
                    }

                    Mono<Object> executeLoanDisbursement = initiateLoanDisbursement(masterObj, header, prop, customerDataFields, req, reqFields, Constants.DISBURSEMENT_PENDING);
                    return executeLoanDisbursement;
                } else {
                    masterObj.setRemarks(customerDataFields.getRemarks());
                    masterObj.setUpdatedBy(customerDataFields.getUserId());
                    cobService.updateStatus(masterObj, status);
                    logger.debug("Updated application status to: {}", status);

                    if (action.equalsIgnoreCase(Constants.REJECT) || action.equalsIgnoreCase(Constants.RESANCTION)) {
                        cobService.handleDeleteAllDocuments(prop, fetchDeleteUserRequest.getRequestObj().getAppId(), applicationId);
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
                responseBody.setResponseObj(Constants.APPLICATION_UPDATED_ERROR);
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
            if (!Constants.APPROVER.equalsIgnoreCase(roleId)
                    && !Constants.BM.equalsIgnoreCase(roleId) && !Constants.BCM.equalsIgnoreCase(roleId)) {
                header.setResponseCode(ResponseCodes.INVALID_ROLE.getKey());
                response.setResponseBody(responseBody);
                response.setResponseHeader(header);
                return Mono.just(response);
            }
            Set<AppStatus> validStatuses = EnumSet.of(AppStatus.DISBURSED, AppStatus.LUC,
                    AppStatus.PENDINGLUCVERIFICATION, AppStatus.LUCVERIFIED);

            if (!isValidStatus(status, validStatuses)) {
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

            List<String> validStatusList = Arrays.asList(AppStatus.DISBURSED.getValue(), AppStatus.LUC.getValue(),
                    AppStatus.PENDINGLUCVERIFICATION.getValue(), AppStatus.LUCVERIFIED.getValue());

            Optional<ApplicationMaster> masterOpt = applicationMasterRepository
                    .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(fields.getAppId(),
                            fields.getApplicationId(), fields.getVersionNum(), validStatusList);
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

                try{
                    prepareCkycDocumentsForDisbursedCases(master, prop);
                }catch (Exception e){
                    logger.error("Error while preparing CKYC documents for disbursement: {}", e.getMessage(), e);
                }

                master.setRemarks(fields.getRemarks());
                master.setUpdatedBy(fields.getUserId());

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
                "{\"apiName\":\"" + apiName + "\",\"errorMessage\":\"" + error + "\", \"status\":\"" + ResponseCodes.FAILURE.getValue() + "\"}");
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
        responseBody.setResponseObj("{\"errorMessage\":\"" + error + "\", \"status\":\"" + ResponseCodes.FAILURE.getValue() + "\"}");
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
        int applicationDocCount = applicationDocumentsRepository.fetchApplicationDocsCountByAppIdAndApplicationId(
                customerDataFields.getAppId(),
                customerDataFields.getApplicationId(),
                new ArrayList<>(Arrays.asList(Constants.APPLICANT, Constants.COAPPLICANT))
        );
        if(!AppStatus.REJECTED.getValue().equalsIgnoreCase(status)) {
            if (applicationDocCount < 5) { // Assuming 5 documents are mandatory, this can be changed as per requirement
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                responseBody.setResponseObj("All required documents have not been uploaded. Please upload all documents to proceed.");
                response.setResponseBody(responseBody);
                response.setResponseHeader(responseHeader);
                return Mono.just(response);
            }
        }
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

                    if (AppStatus.APPROVED.getValue().equalsIgnoreCase(status)) {
                        boolean isDedupeUpdatedEnabled = (prop.getProperty(CobFlagsProperties.IS_SOURCING_DEDUPEUPDATE_ENABLED.getKey()))
                                .equalsIgnoreCase("Y") ? true : false;
                        if (isDedupeUpdatedEnabled) {
//                            Mono<Object> t24CDHUpdateApplicantResp = this.t24AndCDHService
//                                    .dedupeUpdateT24(masterObj.getSearchCode2(),
//                                            masterObj.getApplicationId(),
//                                            masterObj.getAppId(), masterObj.getProductCode(), header, prop, 1);
//                            return t24CDHUpdateApplicantResp.flatMap(resp -> {
//                                logger.debug("T24 update response: {}", resp.toString());
//                                JSONObject jsonResp = (JSONObject) resp;
//                                String errorCode = jsonResp.optString(Constants.ERRORCODE);
//                                String errorMessage = jsonResp.optString(Constants.ERRORMESSAGE);
//                                if (!"0".equals(errorCode)) {
//                                    logger.debug("T24 update failed with error code: {} with resp : {}", errorCode, buildErrorResponse(errorCode, jsonResp.toString()));
//                                    return Mono.just(buildErrorResponse(errorCode, errorMessage));
//                                }

                            return this.t24AndCDHService.dedupeUpdateCDH(masterObj.getSearchCode2(),
                                    masterObj.getApplicationId(), masterObj.getAppId(),
                                    masterObj.getProductCode(),
                                    header, prop, Constants.APPLICANT).flatMap( resp -> {
                                JSONObject firstJson = (JSONObject) resp;
                                String firstErrorCode = firstJson.optString(Constants.ERRORCODE);
                                String firstErrorMessage = firstJson.optString(Constants.ERRORMESSAGE);
                                if (!"0".equals(firstErrorCode)) {
                                    Response errorResponse = new Response();
                                    ResponseHeader headerResp = new ResponseHeader();
                                    ResponseBody bodyResp = new ResponseBody();
                                    headerResp.setResponseCode(firstErrorCode);
                                    bodyResp.setResponseObj(firstErrorMessage);
                                    errorResponse.setResponseHeader(headerResp);
                                    errorResponse.setResponseBody(bodyResp);
                                    logger.debug("Error response from CDH update for applicationID: {}. ErrorCode: {}, Response: {}",
                                            masterObj.getApplicationId(), firstErrorCode, firstJson.toString());
                                    return Mono.just(errorResponse);
                                }
                                String coApplicantId = Optional.ofNullable(loanDtlsRepo.findByApplicationId(masterObj.getApplicationId()))
                                        .map(LoanDetails::getCoapplicantId)
                                        .orElse("");//send blank for prospect Id
                                return this.t24AndCDHService.dedupeUpdateCDH(
                                                coApplicantId,
                                                masterObj.getApplicationId(),
                                                masterObj.getAppId(),
                                                masterObj.getProductCode(),
                                                header,
                                                prop,
                                                Constants.COAPPLICANT
                                        )
                                        .map(secondResp -> {
                                            logger.debug("CDH update response: {}", secondResp.toString());
                                            JSONObject secondJson = (JSONObject) secondResp;
                                            String secondErrorCode = secondJson.optString(Constants.ERRORCODE);
                                            String secondErrorMessage = secondJson.optString(Constants.ERRORMESSAGE);
                                            if (!"0".equals(secondErrorCode)) {
                                                logger.debug("CDH update failed with error code: {} with resp : {}", secondErrorCode, buildErrorResponse(secondErrorCode, secondJson.toString()));
                                                return buildErrorResponse(secondErrorCode, secondErrorMessage);
                                            }
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
                                            return response;
                                        });
                            });
//                            });
                        }
                    }
                }
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
                responseBody.setResponseObj(Constants.APPLICATION_UPDATED_ERROR);
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
            FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
            boolean isSelfOnBoardingHeaderAppId, Properties prop) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        response.setResponseHeader(responseHeader);
        FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest
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

                List<String> allowedProducts = new ArrayList<>();
                Set<ProductCode> allowed = EnumSet.of(ProductCode.FAMILY_WELFARE, ProductCode.UNNATI_SUPPLEMENTARY,
                        ProductCode.UNNATI_RESTART, ProductCode.UNNATI_EMERGENCY, ProductCode.OPEN_MARKET);

                for (ProductCode pc : allowed) {
                    allowedProducts.add(pc.getCode(ProductCode.ProductType.UNNATI));
                }

                if (allowedProducts.contains(masterObj.getProductCode())) {
                    responseBody.setResponseObj("Rejected Open Market Loan product cases cannot be re-initiated.");
                    responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                    response.setResponseBody(responseBody);
                    response.setResponseHeader(responseHeader);
                    return Mono.just(response);
                }
                logger.debug("Master data value present." + masterObj.toString());
                logger.debug("Status : {}", status);
                if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(status)) {
                    List<BankDetails> bankDetailsList = bankDetailsRepository.findByApplicationIdAndAppId(customerDataFields.getApplicationId(), customerDataFields.getAppId());
                    if (!bankDetailsList.isEmpty()) {
                        logger.debug("Bank details found: {}", bankDetailsList);
                        for (BankDetails bankDetails : bankDetailsList) {
                            BankDetailsPayload bankDetailsPayload = gson.fromJson(bankDetails.getPayloadColumn(), BankDetailsPayload.class);
                            if (null != bankDetailsPayload.getAccntVerified()) {
                                bankDetailsPayload.setAccntVerified("");
                            }
                            bankDetailsPayload.setENachStatus(null);
                            bankDetailsPayload.setPennyCheckStatus(null);
                            bankDetailsPayload.setPennyResp(null);
                            bankDetailsPayload.setPrimaryENachStatus(null);
                            bankDetailsPayload.setPrimaryPennyCheckStatus(null);
                            bankDetailsPayload.setSecondaryENachStatus(null);
                            if (null != bankDetailsPayload.getRpcaccntVerified()) {
                                bankDetailsPayload.setRpcaccntVerified("");
                            }
                            bankDetails.setPayloadColumn(gson.toJson(bankDetailsPayload));
                            bankDetailsRepository.save(bankDetails);
                        }
                        logger.debug("updated bank details for applicationId : {}", customerDataFields.getApplicationId());
                    }
                    List<CibilDetails> cibilDetailsList = cibilDetailsRepository.findByApplicationIdAndAppId(customerDataFields.getApplicationId(), customerDataFields.getAppId());
                    if (!cibilDetailsList.isEmpty()) {
                        logger.debug("cibilDetailsList found: {}", bankDetailsList);
                        for (CibilDetails cibilDetails : cibilDetailsList) {
                            CibilDetailsPayload cibilDetailsPayload = gson.fromJson(cibilDetails.getPayloadColumn(), CibilDetailsPayload.class);
                            cibilDetailsPayload.setRetryAttempts(Integer.parseInt(
                                    prop.getProperty(CobFlagsProperties.BRE_RETRY_ATTEMPT.getKey())));
                            cibilDetailsPayload.setIsReinitiated("Y");
                            cibilDetails.setPayloadColumn(gson.toJson(cibilDetailsPayload));
                            cibilDetailsRepository.save(cibilDetails);
                        }
                        logger.debug("updated cibilDetailsList for applicationId : {}", customerDataFields.getApplicationId());
                    }
                    List<CustomerDetails> customerDetailsList = customerDetailsRepository.findByApplicationIdAndAppId(customerDataFields.getApplicationId(), customerDataFields.getAppId());
                    if(!customerDetailsList.isEmpty()) {
                        for(CustomerDetails customerDetails: customerDetailsList){
                            CustomerDetailsPayload customerDetailsPayload = gson.fromJson(customerDetails.getPayloadColumn(), CustomerDetailsPayload.class);
                            customerDetailsPayload.setIsMobVerified("N");
                            if(!Constants.YES.equalsIgnoreCase(masterObj.getAssignedTo())){
                                customerDetailsPayload.setOldReinitiate(true);
                            }
                            customerDetails.setPayloadColumn(gson.toJson(customerDetailsPayload));
                            customerDetailsRepository.save(customerDetails);
                        }
                    }
                    Optional<SorucingStageVerification> srcStatDetails = srcStageVerificationRepo.findById(customerDataFields.getApplicationId());
                    if(srcStatDetails.isPresent()) {
                        SorucingStageVerification srcStat = srcStatDetails.get();
                        srcStat.setBmVerifiedStages(null);
                        srcStat.setVerifiedStages(null);
                        srcStat.setQueries(null);
                        srcStageVerificationRepo.save(srcStat);
                    }
                    if (Products.LOAN.getKey().equalsIgnoreCase(masterObj.getProductGroupCode())) {
                        masterObj.setCurrentScreenId(wf.getAction());
                        masterObj.setRemarks(customerDataFields.getRemarks());
                        String branchId = masterObj.getBranchId();
                        boolean isAnyBranchWhitelisted = whitelistedBranchesRepository.isAnyBranchWhitelisted(Arrays.asList(branchId));
                        if (isAnyBranchWhitelisted) {
                            masterObj.setDeclarationFlag(Constants.IEXCEED_FLAG);
                        }
                        if(!Constants.YES.equalsIgnoreCase(masterObj.getAssignedTo())) {
                            handleOldSourcingReinitiate(customerDataFields.getAppId(),
                                    masterObj.getApplicationId());
                        }
                        masterObj.setAssignedTo("Y");
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
                        boolean isAnyBranchWhitelisted = whitelistedBranchesRepository.isAnyBranchWhitelisted(Arrays.asList(branchId));
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
                responseBody.setResponseObj(Constants.APPLICATION_UPDATED_ERROR);
                responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            }
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_STATUS.getKey());
        }
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return Mono.just(response);
    }

    public void handleOldSourcingReinitiate(String appId, String applicationId) {
        Gson gson = new Gson();
        logger.debug("Inside handle old Km Sourcing ");
        DeleteDocumentRequestFields delBusinessDocReqFields = new DeleteDocumentRequestFields();
        DeleteDocumentRequest delBusinessDocReq = new DeleteDocumentRequest();
        delBusinessDocReqFields.setApplicationId(applicationId);
        delBusinessDocReqFields.setAppId(appId);
        delBusinessDocReqFields.setVersionNum(Constants.INITIAL_VERSION_NO);
        delBusinessDocReqFields.setBulkDelete("N");
        delBusinessDocReqFields.setFilePath("/" + "APZCBO" + "/" + Constants.LOAN + "/" + applicationId + "/");
        delBusinessDocReq.setRequestObj(delBusinessDocReqFields);

        List<String> docTypes = Arrays.asList("BUSINESS_ADDRESS_PROOF", "BUSINESS_PREMISE_PHOTO",
                "EMPLOYMENT_PROOF", "Bank Passbook");
        List<ApplicationDocuments> applnDocumentList = applicationDocumentsRepository.fetchDocumentsByApplicationIdAndDocumentTypeList(applicationId,docTypes);
        if(!applnDocumentList.isEmpty()){
            logger.debug("Documents to be deleted Length: {}", applnDocumentList.size());
            Properties prop = null;
            try {
                prop = CommonUtils.readPropertyFile();
            } catch (IOException e) {
                logger.error("Error while reading property file in deleteDocument ", e);
                return;
            }
            String uploadLocation = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey())
                    +"/" + appId + "/" + Constants.LOAN + "/" + applicationId + "/";
            BigDecimal applicantID = commonCoreService.generateCustDtlId(applicationId, "Applicant");
            applnDocumentList.stream().forEach(appln -> {
//               delBusinessDocReqFields.setDocumentType(appln.getDocumentType());
//               delBusinessDocReqFields.setAppDocId(appln.getAppDocId());
//               delBusinessDocReqFields.setFileName(appln.getDocumentFileName());
//               cobService.deleteDocument(delBusinessDocReq);

                String applicantType = appln.getCustDtlId().equals(applicantID) ? "_A" : "_C";
                ApplicationDocumentsPayload appPayload = gson.fromJson(appln.getPayloadColumn(), ApplicationDocumentsPayload.class);
                Path oldPath = Paths.get(uploadLocation + appPayload.getDocumentFileName());
                String format = appPayload.getDocumentFileName().split("\\.")[1];
                String newFileName =  appPayload.getDocumentType() + applicantType + (appPayload.getDocLevel() != null ? ("_"+appPayload.getDocLevel()) : "")+"."+format;
                Path newPath = Paths.get(uploadLocation + newFileName);
                try {
                    Files.copy(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                    Files.deleteIfExists(oldPath);
                } catch (IOException e) {
                    logger.debug("IO Exception during rename ", e);
                }
                appPayload.setDocumentDesc("WIP_"+appPayload.getDocumentType()+applicantType);
                appPayload.setDocumentType("ADDITIONAL_DOCS");
                appPayload.setIsAdiDoc("Y");
                appPayload.setDocumentFileName(newFileName);
                appln.setPayloadColumn(gson.toJson(appPayload));
                applicationDocumentsRepository.save(appln);
           });
       }

        InsuranceDetailsHistory insuranceHistory;
        List<InsuranceDetails> insuranceDetails = insuranceDetailsRepo
                .findByApplicationIdAndAppId(applicationId, appId);
        if (!insuranceDetails.isEmpty()) {
            for(InsuranceDetails insuranceObj : insuranceDetails) {
                insuranceHistory = new InsuranceDetailsHistory();
                BeanUtils.copyProperties(insuranceObj, insuranceHistory);
                insuranceDetailsHisRepo.save(insuranceHistory);
            }
            insuranceDetailsRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        }
        List<CustomerDetails> customerDetailsList = customerDetailsRepository.findByApplicationIdAndAppId(applicationId, appId);
        if(!customerDetailsList.isEmpty()) {
            for(CustomerDetails customerDetails: customerDetailsList){
                CustomerDetailsPayload customerDetailsPayload = gson.fromJson(customerDetails.getPayloadColumn(), CustomerDetailsPayload.class);
                customerDetailsPayload.setPrimaryKycIdValStatus("Pending");
                customerDetailsPayload.setPanNumberStatus("Pending");
                if(Constants.VERIFIED_STS.equalsIgnoreCase(customerDetailsPayload.getAlternateVoterIdValStatus())) {
                    customerDetailsPayload.setAlternateVoterIdValStatus("Pending");
                }
                customerDetails.setPayloadColumn(gson.toJson(customerDetailsPayload));
                customerDetailsRepository.save(customerDetails);
            }
       }
    }

    /*
     * This method re-inserts rejected data in all tables with updated version
     * number, new cust dtl id and new table specific IDs. User will modify this
     * newly added data while editing a rejected application.
     */
    @CircuitBreaker(name = "fallback", fallbackMethod = "populateRejectedDataInAllTablesFallback")
    public Response populateRejectedDataInAllTables(PopulateRejectedDataRequest apiRequest,
                                                    boolean isSelfOnBoardingappId, Properties prop) {
        String fetchApplicationId = null;
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        PopulateRejectedDataRequestFields reqFields = apiRequest.getRequestObj();
        String applicationId = reqFields.getApplicationId();
        Optional<ApplicationMaster> appMasterForVersionCheck = applicationMasterRepository
                .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(reqFields.getAppId(), applicationId);
        if (appMasterForVersionCheck.isPresent()) {
            ApplicationMaster appMaster = appMasterForVersionCheck.get();
            if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMaster.getApplicationStatus())
                    || AppStatus.PENDING.getValue().equalsIgnoreCase(appMaster.getApplicationStatus())
                    || AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMaster.getApplicationStatus())) {
                int oldVersionNum = appMaster.getVersionNum();
                int newVersionNum = oldVersionNum + 1;
                appMaster.setRemarks(reqFields.getRemarks()); // Required for verifier flow when verifier edits an
                // application by giving a comment.
                applicationMasterRepository.save(appMaster);
                if (Products.LOAN.getKey().equalsIgnoreCase(appMaster.getProductGroupCode())) {
                    if (Constants.ETB.equalsIgnoreCase(appMaster.getApplicationType())) {
                        loanService.duplicateLoanTablesETB(reqFields.getAppId(), applicationId, newVersionNum,
                                oldVersionNum);
                        fetchApplicationId = applicationId;
                    } else if (Constants.NTB.equalsIgnoreCase(appMaster.getApplicationType())) {
                        Optional<ApplicationMaster> appMasterRelatedDb = applicationMasterRepository
                                .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(reqFields.getAppId(),
                                        appMaster.getRelatedApplicationId());
                        if (appMasterRelatedDb.isPresent()) {
                            ApplicationMaster appMasterRelated = appMasterRelatedDb.get();
                            cobService.duplicateCasaTables(appMasterRelated, newVersionNum,
                                    appMaster.getRelatedApplicationId(), reqFields.getAppId(), oldVersionNum);
                            fetchApplicationId = appMaster.getRelatedApplicationId();
                        }
                        loanService.duplicateLoanTablesNTB(reqFields.getAppId(), applicationId, newVersionNum,
                                oldVersionNum);
                    }
                }
                commonCoreService.duplicateWf(applicationId, reqFields.getAppId(), oldVersionNum, newVersionNum);

                if (Products.LOAN.getKey().equalsIgnoreCase(appMaster.getProductGroupCode())
                        && Constants.NTB.equalsIgnoreCase(appMaster.getApplicationType())) {
                    FetchDeleteUserRequest fetchAppReq = new FetchDeleteUserRequest();
                    FetchDeleteUserFields fetchAppReqFields = new FetchDeleteUserFields();
                    fetchAppReq.setAppId(apiRequest.getAppId());// Taking app id outside requestObj bec app id inside
                    // requestObj can be of COB or CBO but entry in roles
                    // table will be for CBO only.
                    fetchAppReqFields.setAppId(reqFields.getAppId());
                    fetchAppReqFields.setApplicationId(fetchApplicationId);
                    fetchAppReqFields.setUserId(reqFields.getUserId());
                    fetchAppReqFields.setVersionNum(newVersionNum);
                    fetchAppReq.setRequestObj(fetchAppReqFields);
                    response = cobService.fetchApplication(fetchAppReq, "fetchapplication", isSelfOnBoardingappId, prop);
                } else if ((Products.LOAN.getKey().equalsIgnoreCase(appMaster.getProductGroupCode()))
                        && Constants.ETB.equalsIgnoreCase(appMaster.getApplicationType())) {
                    FetchAppRequest fetchAppReq = new FetchAppRequest();
                    FetchAppRequestFields fetchAppReqFields = new FetchAppRequestFields();
                    fetchAppReqFields.setAppId(reqFields.getAppId());
                    fetchAppReqFields.setApplicationId(fetchApplicationId);
                    fetchAppReqFields.setVersionNum(newVersionNum);
                    fetchAppReq.setRequestObj(fetchAppReqFields);
                    response = loanService.fetchApplication(fetchAppReq);
                }
            } else {
                responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
                responseBody.setResponseObj(Constants.APPLICATION_UPDATED_ERROR);
                response.setResponseBody(responseBody);
                response.setResponseHeader(responseHeader);
            }
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            responseBody.setResponseObj(ResponseCodes.INVALID_APP_MASTER.getValue());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
        }
        return response;
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
        List<ApiExecutionLog> logs = logRepository.findAllByApplicationIdAndApiNameAndCurrentStage(applicationId, apiName, currentStage);

        for (ApiExecutionLog log : logs) {
            logger.debug("Checking status for apiName={} | DB Value={} || currStage={}", apiName, log.getApiStatus(), log.getCurrentStage());
            if (ResponseCodes.SUCCESS.getValue().equals(log.getApiStatus()) && currentStage.equalsIgnoreCase(log.getCurrentStage())) {
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
            FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
            Properties prop, String roleId, Exception e) {
        logger.error("disbursementApplicationMovementFallback error : ", e);
        return FallbackUtils.genericFallbackMono();
    }

    private Mono<Response> approveRejectApplicationFallback(
            FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
            boolean isSelfOnBoardingHeaderAppId, Properties prop, String roleId, Exception e) {
        logger.error("approveRejectApplicationFallback error : ", e);
        return FallbackUtils.genericFallbackMono();
    }

    private Mono<Response> stageMovementApplicationFallback(
            FetchDeleteUserRequest fetchDeleteUserRequest, Properties prop, String roleId, Exception e) {
        logger.error("stageMovementApplicationFallback error : ", e);
        return FallbackUtils.genericFallbackMono();
    }

    private Mono<Response> creditAssessmentApplicationMovementFallback(
            FetchDeleteUserRequest fetchDeleteUserRequest, Properties prop, String roleId, Exception e) {
        logger.error("creditAssessmentApplicationMovementFallback error : ", e);
        return FallbackUtils.genericFallbackMono();
    }

    private Mono<Response> creditDeviationApplicationMovementFallback(
            FetchDeleteUserRequest fetchDeleteUserRequest, Properties prop, String roleId, Exception e) {
        logger.error("creditDeviationApplicationMovementFallback error : ", e);
        return FallbackUtils.genericFallbackMono();
    }

    private Mono<Response> creditReassessmentApplicationMovementFallback(
            FetchDeleteUserRequest fetchDeleteUserRequest, Properties prop, String roleId, Exception e) {
        logger.error("creditReassessmentApplicationMovementFallback error : ", e);
        return FallbackUtils.genericFallbackMono();
    }

    // Loan Creation & CoAPplicant Creation
    private Mono<Response> sanctionApplicationMovementFallback(
            FetchDeleteUserRequest fetchDeleteUserRequest, Header header, Properties prop, String roleId,
            Exception e) {
        logger.error("sanctionApplicationMovementFallback error : ", e);
        return FallbackUtils.genericFallbackMono();
    }

    private Mono<Response> approveRenewalApplicationFallback(
            ApplyLoanRequest req2, Header header,
            boolean isSelfOnBoardingHeaderAppId, Properties prop, Exception e) {
        logger.error("approveRejectApplicationFallback error : ", e);
        return FallbackUtils.genericFallbackMono();
    }

    private Mono<Response> initiateRejectedApplicationFallback(
            FetchDeleteUserRequest fetchDeleteUserRequest, Header header,
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
