package com.iexceed.appzillonbanking.cob.loans.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.core.repository.ab.*;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.core.services.InterfaceAdapter;
import com.iexceed.appzillonbanking.cob.core.services.ResponseParser;
import com.iexceed.appzillonbanking.cob.core.utils.*;
import com.iexceed.appzillonbanking.cob.domain.ab.LovMaster;
import com.iexceed.appzillonbanking.cob.domain.ab.RoleAccessMap;
import com.iexceed.appzillonbanking.cob.domain.ab.WhitelistedBranches;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiCoApplicantDetails;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedCDHLead;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedOccpInsr;
import com.iexceed.appzillonbanking.cob.dto.UnnCbResponseDto;
import com.iexceed.appzillonbanking.cob.exception.ReplicationFailedException;
import com.iexceed.appzillonbanking.cob.loans.payload.*;
import com.iexceed.appzillonbanking.cob.loans.payload.BIPRequestWrapper.BIPMasterRequest;
import com.iexceed.appzillonbanking.cob.loans.payload.CheckApplicationRequest;
import com.iexceed.appzillonbanking.cob.loans.payload.UploadLoanRequestFields.DBKITResponse;
import com.iexceed.appzillonbanking.cob.loans.report.LoanReport;
import com.iexceed.appzillonbanking.cob.loans.repository.user.TbUserRepository;
import com.iexceed.appzillonbanking.cob.nesl.domain.ab.Enach;
import com.iexceed.appzillonbanking.cob.nesl.repository.ab.EnachRepository;
import com.iexceed.appzillonbanking.cob.payload.*;
import com.iexceed.appzillonbanking.cob.report.LoanApplication;
import com.iexceed.appzillonbanking.cob.repository.ab.LovMasterRepository;
import com.iexceed.appzillonbanking.cob.repository.ab.WhitelistedBranchesRepository;
import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiCoApplicantDetailsRepository;
import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiIexceedCDHLeadRepo;
import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiIexceedOccpInsrRepository;
import com.iexceed.appzillonbanking.cob.rest.CustomerOnBoardingAPI;
import com.iexceed.appzillonbanking.cob.service.COBService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LoanService {

    private static final Logger logger = LogManager.getLogger(LoanService.class);

    @Autowired
    private AdapterUtil adapterUtil;

    @Autowired
    private ApplicationMasterRepository applicationMasterRepo;

    @Autowired
    private ApplicationMasterHisRepository applicationMasterHisRepo;

    @Autowired
    private ApplicationWorkflowRepository applnWfRepository;

    @Autowired
	private ApplicationDocumentsRepository applicationDocumentsRepository;

	@Autowired
    private WorkflowDefinitionRepository wfDefnLoanRepo;

    @Autowired
    private LoanDtlsRepo loanDtlsRepo;

    @Autowired
    private LoanDtlsHisRepo loanDtlsHisRepo;

    @Autowired
    private ApplicationDocumentsRepository appLoanDocsRepository;

    @Autowired
    private ApplicationDocumentsHisRepository appLoanDocsHisRepository;

    @Autowired
    private InterfaceAdapter interfaceAdapter;

    @Autowired
    private CustomerDetailsRepository custDtlRepo;

    @Autowired
    private CustomerDetailsHisRepository custDtlHisRepo;

    @Autowired
    private AddressDetailsRepository addressDtlRepo;

    @Autowired
    private ExistingLoanDetailsRepository existingLoanDtlRepo;

    @Autowired
    private AddressDetailsHisRepository addressDtlHisRepo;

    @Autowired
    private OccupationDetailsRepository occupationDtlRepo;

    @Autowired
    private InsuranceDetailsRepository insuranceDtlRepo;

    @Autowired
    private InsuranceDetailsHisRepository insuranceDtlHisRepo;

    @Autowired
    private BankDetailsRepository bankDtlRepo;

    @Autowired
    private CibilDetailsRepository cibilDtlRepo;

    @Autowired
    private CibilDetailsHisRepository cibilDtlHisRepo;

    @Autowired
    private OccupationDetailsHisRepository occupationDtlHisRepo;

    @Autowired
	private OCRDetailsRepository ocrDetailsRepo;

	@Autowired
    private CommonParamService commonParamService;

    @Autowired
    private LoanReport report;

    @Autowired
	@Lazy
    private CustomerOnBoardingAPI casaApi;

    @Autowired
    private WorkflowDefinitionRepository wfDefnRepoLn;

    @Autowired
    private COBService cobService;

    @Autowired
    private ExistingGLLoanDetailsRepository existingGLLoanDetailsRepo;

    @Autowired
    private RpcStageVerificationRepository rpcStgVerificationRepo;

    @Autowired
    private BCMPIIncomeDetailsRepository bcmpiIncomeDetailsRepo;

    @Autowired
    private BCMPIStageVerificationRepository bcmpiStageVerificationRepository;

    @Autowired
    private BCMPILoanObligationsRepository bcmpiLoanObligationsRepo;

    @Autowired
    private BCMPIOtherDetailsRepository bcmpiOtherDetailsRepo;

    @Autowired
    private LovMasterRepository lovMasterRepository;

    @Autowired
    private UdhyamRepository udhyamRepository;

    @Autowired
    private SourcingResponseTrackerRepository sourcingResponseTrackerRepo;

    @Autowired
    private EnachRepository enachRepository;

    @Autowired
    private WhitelistedBranchesRepository whitelistedBranchesRepository;

    @Autowired
    private WebClient webClient;

    @Autowired
    DBKITStageVerificationRepository dbkitStageVerificationRepository;

    @Autowired
    private ApiExecutionLogRepository logRepository;

    @Autowired
    private UnnatiIexceedCDHLeadRepo unnatiIexceedCDHLeadRepo;

    @Autowired
    TbUserRepository tbUserRepository;

    @Autowired
    BipDetailsRepository bipDetailsRepository;

    @Autowired
    private T24AndCDHService t24AndCDHService;

	@Autowired
	UnnatiCoApplicantDetailsRepository coAppDetailsRepository;

	@Autowired
	UnnatiIexceedOccpInsrRepository iexceedOccpInsrRepository;

    private String versionHm = "versionHm";
    private String headerHm = "headerHm";
    private String applicationIDHm = "applicationIDHm";
    private String propHm = "propHm";
	/**
	 * @author Ankit.CAG
	 */
	public static final String SUCCESS_MSG = "SUCCESS";
	public static final String FAIL_MSG = "FAIL";
	public static final String EXCEPTION_MSG = "Something went wrong, Please try again!!";
	public static final String EXCEPTION_OCCURED = "Exception occurred";

    @Autowired
    private DocumentsRepository documentsRepository;

	@Autowired
	private AMLQuestionnaireRepository amlQuestionnaireRepo;

    @Autowired
    private ApplicationMasterRepository2 applicationMasterRepository2;

    public static final ObjectMapper mapper = new ObjectMapper();

    public Mono<Response> applyLoan(HashMap<String, String> hm2, ApplyLoanRequest applyLoanRequest,
                                    boolean isSelfOnBoardingAppId, boolean isSelfOnBoardingHeaderAppId, Properties prop, JSONArray array) {
        Header header = CommonUtils.obtainHeader(hm2.get("reqAppId"), hm2.get("interfaceId"), hm2.get("userId"),
                hm2.get("masterTxnRefNo"), hm2.get("deviceId"));
        ApplyLoanRequestFields requestObj = applyLoanRequest.getRequestObj();
        ApplicationMaster applicationMaster = requestObj.getApplicationMaster();
        ResponseBody responseBody = new ResponseBody();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        CustomerIdentificationLoan customerIdentification = new CustomerIdentificationLoan();
        String applicationID;
        // Previously it was set to 0. Change done on 25/02/2024
        int version = Constants.INITIAL_VERSION_NO;
        boolean isThisLastStage = commonParamService
                .isThisLastStage(applicationMaster.getCurrentScreenId().split("~")[0], array);
        boolean isAccountCreationisNextStage = false;
        // BigDecimal custDtlId = commonParamService.getCustDtlId(applicationMaster);
        BigDecimal custDtlId;
        Optional<ApplicationMaster> masterData = applicationMasterRepo.findByAppIdAndWorkitemNo(requestObj.getAppId(),
                applicationMaster.getWorkitemNo());

        if (!masterData.isPresent()) {
            applicationID = CommonUtils.generateRandomNumStr();
            // custDtlId =
            // commonParamService.generateCustDtlId(applicationID,requestObj.getCustomerDetailsList().get(0).getCustomerType());
            version = Constants.INITIAL_VERSION_NO; // initial creation of loan application version number should be 1.
            populateAppMasterAndApplnwf(requestObj, applicationID, version, customerIdentification,
                    isSelfOnBoardingHeaderAppId, prop);
            /*
             * commonParamService.populateCustomerDtlsIfNotPresent(requestObj.
             * getApplicationMaster(), applicationID, custDtlId, version,
             * requestObj.getAppId());
             */
        } else { // this ID should be created once only.
            applicationID = masterData.get().getApplicationId();
            // custDtlId =
            // commonParamService.generateCustDtlId(applicationID,requestObj.getCustomerDetailsList().get(0).getCustomerType());
            Optional<ApplicationMaster> appMasterForVersionCheck = applicationMasterRepo
                    .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(requestObj.getAppId(), applicationID);
            if (appMasterForVersionCheck.isPresent()) {
                ApplicationMaster appMaster = appMasterForVersionCheck.get();
                customerIdentification.setRelatedApplicationId(appMaster.getRelatedApplicationId());
                if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMaster.getApplicationStatus())
                        || AppStatus.APPROVED.getValue().equalsIgnoreCase(appMaster.getApplicationStatus())
						|| AppStatus.PUSHBACK.getValue().equalsIgnoreCase(appMaster.getApplicationStatus())
|| AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(appMaster.getApplicationStatus())
                        || AppStatus.PENDING.getValue().equalsIgnoreCase(appMaster.getApplicationStatus())) {
                    // Taking version number always from db as part of VAPT too.
                    // If application is in INPROGRESS status,subsequent tables should have same
                    // version number.
                    version = appMaster.getVersionNum();
                }
                if (Objects.equals(appMaster.getApplicantsCount(), applicationMaster.getCustDtlSlNum())) {
                    isAccountCreationisNextStage = commonParamService
                            .isAccountCreationisNextStage(applicationMaster.getCurrentScreenId().split("~")[0], array);
                }
            }
        }
        String[] currentScreenIdArray = requestObj.getApplicationMaster().getCurrentScreenId().split("~");
        commonParamService.updateCurrentStageInMaster(requestObj.getApplicationMaster(), currentScreenIdArray, version,
                requestObj.getAppId(), requestObj.getApplicationId());
        String custType = (requestObj.getApplicationMaster().getCustDtlSlNum() <= 1) ? Constants.APPLICANT
                : Constants.COAPPLICANT;
        custDtlId = commonParamService.generateCustDtlId(applicationID, custType);
        switch (currentScreenIdArray[0]) {
            case Constants.CUST_VERIFICATION:
                if (requestObj.getApplicationMaster().getCustomerId() == null
                        && "N".equalsIgnoreCase(requestObj.getIsExistingCustomer())) {

                    CreateModifyUserRequestWrapper createUserRequestWrapper = new CreateModifyUserRequestWrapper();
                    CreateModifyUserRequest createUserRequest = new CreateModifyUserRequest();
                    CustomerDataFields createUserRequestFields = new CustomerDataFields();
                    ApplicationMaster appMasterCasa = new ApplicationMaster();
                    BeanUtils.copyProperties(applicationMaster, appMasterCasa);
                    appMasterCasa.setCustDtlSlNum(1); // By default creating single holder casa account for Loans.
                    appMasterCasa.setApplicantsCount(requestObj.getApplicationMaster().getApplicantsCount());
                    appMasterCasa.setProductCode(prop.getProperty(CobFlagsProperties.DEFAULT_CASA_PRODUCTLN.getKey()));
                    appMasterCasa.setProductGroupCode(prop.getProperty(CobFlagsProperties.DEFAULT_CASA_GRP.getKey()));
                    appMasterCasa.setRelatedApplicationId(applicationID);
                    appMasterCasa.setApplicationId(null); // set this to null so that new application Id will be created in
                    // createApplication method.
                    appMasterCasa.setCustDtlId(null); // set this to null so that new custDtlId will be created in
                    // createApplication method.
                    createUserRequestFields.setIsExistingCustomer(requestObj.getIsExistingCustomer());
                    createUserRequestFields.setApplicationMaster(appMasterCasa);
                    createUserRequestFields.setWorkflow(null); // Workflow is not required for this casa sub application.
                    createUserRequestFields.setAppId(requestObj.getAppId());
                    if (null != requestObj.getBankingFacilityList()) {
                        createUserRequestFields.setBankingFacilityList(requestObj.getBankingFacilityList());
                    }
                    createUserRequest.setAppId(applyLoanRequest.getAppId());
                    createUserRequest.setInterfaceName(applyLoanRequest.getInterfaceName());
                    createUserRequest.setUserId(applyLoanRequest.getUserId());
                    createUserRequest.setRequestObj(createUserRequestFields);
                    createUserRequestWrapper.setCreateModifyUserRequest(createUserRequest);
                    Mono<ResponseEntity<ResponseWrapper>> responseMono = casaApi.createApplication(createUserRequestWrapper,
                            hm2.get("reqAppId"), hm2.get("interfaceId"), hm2.get("userId"), hm2.get("masterTxnRefNo"),
                            hm2.get("deviceId"));
                    final int versionFinal = version;
                    return responseMono.flatMap(res -> {
                        if (ResponseCodes.SUCCESS.getKey()
                                .equalsIgnoreCase(res.getBody().getApiResponse().getResponseHeader().getResponseCode())) {
                            String casaResStr = res.getBody().getApiResponse().getResponseBody().getResponseObj();
                            JSONObject casaResjson = new JSONObject(casaResStr);
                            CustomerIdentificationCasa casaCustIdentification = new CustomerIdentificationCasa();
                            casaCustIdentification.setApplicationId(casaResjson.getString("applicationId"));
                            casaCustIdentification.setCustDtlId(casaResjson.getString("custDtlId"));
                            casaCustIdentification.setVersionNum(casaResjson.getInt("versionNum"));
                            if (casaResjson.has("bankFacilityList")) {
                                JSONArray bankFacilityArr = casaResjson.getJSONArray("bankFacilityList");
                                List<String> bankFacilityList = new ArrayList<>();
                                for (Object obj : bankFacilityArr) {
                                    bankFacilityList.add((String) obj);
                                }
                                casaCustIdentification.setBankFacilityList(bankFacilityList);
                            }
                            customerIdentification.setCasaCustomerIdentification(casaCustIdentification);
                        } else {
                            logger.debug("CASA creation failed for loan with error code"
                                    + res.getBody().getApiResponse().getResponseHeader().getResponseCode());
                            logger.debug("CASA creation failed for loan with error message"
                                    + res.getBody().getApiResponse().getResponseBody().getResponseObj());
                            responseHeader.setResponseCode(ResponseCodes.CASA_CREATION_FAIL.getKey());
                        }

                        updateCustomerDtlInMaster(requestObj, versionFinal, applicationID, customerIdentification);
                        updateCustIdAndBranchInMaster(requestObj, versionFinal);
                        populateOrUpdateLoanDtls(requestObj, versionFinal, applicationID, customerIdentification,
                                Constants.LOAN_DETAILS); // populateOrUpdateLoanDtls method call is required here. Because
                        // it is possible to have first screen as loan details and
                        // second screen as customer verification.
                        Gson gson = new Gson();
                        String responseStr = gson.toJson(customerIdentification);
                        responseBody.setResponseObj(responseStr);
                        response.setResponseBody(responseBody);
                        response.setResponseHeader(responseHeader);
                        return Mono.just(response);
                    });
                }
                break;
            case Constants.CUSTOMER_DETAILS:

                populateCustomerDtls(requestObj, customerIdentification, applicationID, custDtlId, version);
                populateAddressDtls(requestObj, customerIdentification, applicationID, custDtlId, version,
                        Constants.CUSTOMER_DETAILS, custType);
				updateApplicantsCount(applicationID, version, requestObj);
                /*
                 * commonParamService.updatePanInMaster(requestObj.getApplicationMaster(),
                 * version, requestObj.getAppId(), requestObj.getApplicationId());
                 */
                populateExistingLoanDtls(requestObj, customerIdentification, applicationID, custDtlId, version,
                        Constants.CUSTOMER_DETAILS);
                // replicate data from leads table for renewal
                if (!masterData.isPresent()
					&& applicationMaster.getProductCode().equalsIgnoreCase(Constants.RENEWAL_PRODUCT_CODE)) {
                    logger.debug("inside replicate application");
                    replicateApplicationDetails(requestObj, customerIdentification, applicationID, custDtlId, version);
                }
                if (Constants.COAPPLICANT.equalsIgnoreCase(custType)) {
                    updateCoApplicantId(requestObj, applicationID, requestObj.getAppId());
                }
                if (null != requestObj.getQueryResponse()) {
                    updateQueryResponse(requestObj, applicationID);
                }

                break;
            case Constants.OCCUPATION_DETAILS:
                List<String> disabledFields = new ArrayList<>();
                for (CustomerDetails cd : requestObj.getCustomerDetailsList()) {
                    if (cd != null && cd.getPayload() != null && cd.getPayload().getIsDisabled() != null) {
                        disabledFields.add(cd.getPayload().getIsDisabled().toUpperCase());
                    }
                }
                if (disabledFields.contains("Y") || disabledFields.contains("YES")) {
                    populateCustomerDtlsForDisabled(requestObj, applicationID);
                } else {
                    logger.debug("No disabled fields found with value Y or YES");
                }
                populateApplicationDocs(requestObj, customerIdentification, applicationID, version);
                populateOccupationdtls(requestObj, customerIdentification, applicationID, custDtlId, version, custType);
                populateAddressDtls(requestObj, customerIdentification, applicationID, custDtlId, version,
                        Constants.OCCUPATION_DETAILS, custType);
                populateCoAppOccupationAddressdtls(requestObj, applicationID, custDtlId, version);
                populateInsuranceDtls(requestObj, customerIdentification, applicationID, custDtlId, version, custType);
                if (requestObj.getApplicationMaster().getCustDtlSlNum() <= 1)
                    populateBankDtls(requestObj, customerIdentification, applicationID, custDtlId, version);

                if (null != requestObj.getQueryResponse()) {
                    updateQueryResponse(requestObj, applicationID);
                }
                break;
            case Constants.LOAN_DETAILS:
                if (requestObj.getApplicationMaster().getCustDtlSlNum() <= 1)
                    populateOrUpdateLoanDtls(requestObj, version, applicationID, customerIdentification,
                            Constants.LOAN_DETAILS); // This is required if first screen is customer verification and second
                // screen is loan details
                // populateCibilDtls(requestObj, customerIdentification, applicationID,
                // custDtlId, version); //A// have to comment
                populateApplicationDocs(requestObj, customerIdentification, applicationID, version);

                if (null != requestObj.getQueryResponse()) {
                    updateQueryResponse(requestObj, applicationID);
                }
                break;
            case Constants.EMI_DETAILS:
                populateOrUpdateLoanDtls(requestObj, version, applicationID, customerIdentification, Constants.EMI_DETAILS);
                break;
            case Constants.LOAN_CR_DETAILS:
                populateOrUpdateLoanDtls(requestObj, version, applicationID, customerIdentification,
                        Constants.LOAN_CR_DETAILS);
                break;
            case Constants.UPLOAD_DOCUMENTS:
//			populateApplicationDocs(requestObj, customerIdentification, applicationID, version);
                if (null != requestObj.getQueryResponse()) {
                    updateQueryResponse(requestObj, applicationID);
                }
                break;
            case Constants.TERMS_AND_CONDITIONS:
                updateDeclarationFlagInMaster(requestObj, version, applicationID, customerIdentification);
                break;
            case Constants.CONFIRMATION:
                // No action to do specifically for CONFIRMATION. Appropriate actions are taken
                // based on return value of isAccountCreationisNextStage() and
                // isThisLastStage().
                customerIdentification.setApplicationId(applicationID);
                customerIdentification.setVersionNum(version);
                break;
            default:
                logger.error("INVALID current screen ID");
                // call all the above methods at once if you need to insert all data at once at
                // the last screen (CONFIRMATION)
                break;
        }
        logger.error("customerIdentification 1 : " + customerIdentification + "  --  " + isThisLastStage);

        if (isThisLastStage) {
            Set<String> cbDetailsMissing = cbReportExists(applicationID, applyLoanRequest.getAppId(), version, prop);
            if (!cbDetailsMissing.isEmpty()) {
                String formattedMissing = String.join(", ", cbDetailsMissing);
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                responseHeader.setResponseMessage(ResponseCodes.FAILURE.getValue());
                responseBody.setResponseObj("CB Report Missing for : " + formattedMissing);
                response.setResponseBody(responseBody);
                response.setResponseHeader(responseHeader);
                return Mono.just(response);
            }
            int applicationDocCount = appLoanDocsRepository.fetchApplicationDocsCountByAppIdAndApplicationId(
					requestObj.getAppId(), applicationID,
					new ArrayList<>(Arrays.asList(Constants.APPLICANT, Constants.COAPPLICANT)));
            if(applicationDocCount < 5){ // Assuming 5 documents are mandatory, this can be changed as per requirement
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
				responseBody.setResponseObj(
						"All required documents have not been uploaded. Please upload all documents to proceed.");
                response.setResponseBody(responseBody);
                response.setResponseHeader(responseHeader);
                return Mono.just(response);
            }
            String workflowStatus = requestObj.getWorkflow().getNextWorkflowStatus();
            if (null == workflowStatus) {
                logger.error("Workflow status is null for applicationID: {}", applicationID);
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                responseHeader.setResponseMessage(ResponseCodes.FAILURE.getValue());
                responseBody.setResponseObj("Workflow status is null. Please try again.");
                response.setResponseBody(responseBody);
                response.setResponseHeader(responseHeader);
                return Mono.just(response);
            }
			boolean isDedupeUpdatedEnabled = (prop
					.getProperty(CobFlagsProperties.IS_SOURCING_DEDUPEUPDATE_ENABLED.getKey())).equalsIgnoreCase("Y")
							? true
							: false;
            if (isDedupeUpdatedEnabled) {
//                Mono<Object> t24CDHUpdateApplicantResp = this.t24AndCDHService
//                        .dedupeUpdateT24(applicationMaster.getSearchCode2(),
//                                applicationID,
//                                applicationMaster.getAppId(), applicationMaster.getProductCode(), header, prop, 1);
//                return t24CDHUpdateApplicantResp.flatMap(resp -> {
//                    logger.debug("Received response from T24 update for applicationID: {}. Response: {}", applicationID, resp);
//                    JSONObject jsonResp = (JSONObject) resp;
//                    String errorCode = jsonResp.optString(Constants.ERRORCODE);
//                    String errorMessage = jsonResp.optString(Constants.ERRORMESSAGE);
//                    if (!"0".equals(errorCode)) {
//                        Response errorResponse = new Response();
//                        ResponseHeader headerResp = new ResponseHeader();
//                        ResponseBody bodyResp = new ResponseBody();
//                        headerResp.setResponseCode(errorCode);
//                        bodyResp.setResponseObj(errorMessage);
//                        errorResponse.setResponseHeader(headerResp);
//                        errorResponse.setResponseBody(bodyResp);
//                        logger.debug("Error response from T24 update for applicationID: {}. ErrorCode: {}, Response: {}", applicationID, errorCode, jsonResp.toString());
//                        return Mono.just(errorResponse);
//                    }

				return this.t24AndCDHService.dedupeUpdateCDH(applicationMaster.getSearchCode2(), applicationID,
						applicationMaster.getAppId(), applicationMaster.getProductCode(), header, prop,
						Constants.APPLICANT).flatMap(resp -> {
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
								logger.debug(
										"Error response from CDH update for applicationID: {}. ErrorCode: {}, Response: {}",
                                applicationID, firstErrorCode, firstJson.toString());
                        return Mono.just(errorResponse);
                    }
                    String coApplicantId = Optional.ofNullable(loanDtlsRepo.findByApplicationId(applicationID))
									.map(LoanDetails::getCoapplicantId).orElse("");// send blank for prospect Id
							return this.t24AndCDHService
									.dedupeUpdateCDH(coApplicantId, applicationID, requestObj.getAppId(),
											applicationMaster.getProductCode(), header, prop, Constants.COAPPLICANT)
                            .flatMap(secondResp -> {
										logger.debug(
												"Received response from CDH update for applicationID: {}. Response: {}",
												applicationID, secondResp);
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
											logger.debug(
													"Error response from CDH update for applicationID: {}. ErrorCode: {}, Response: {}",
													applicationID, secondErrorCode, secondJson.toString());
                                    return Mono.just(errorResponse);
                                } else {
											logger.debug(
													"Successfully updated dedupe info in T24 and CDH for applicationID: {}",
													applicationID);
											updateConfirmFlagInMaster(requestObj, Constants.INITIAL_VERSION_NO,
													applicationID, customerIdentification, prop, isSelfOnBoardingAppId,
													isSelfOnBoardingHeaderAppId);
                                    Gson gson = new Gson();
                                    String responseStr = gson.toJson(customerIdentification);
                                    responseBody.setResponseObj(responseStr);
                                    response.setResponseBody(responseBody);
                                    response.setResponseHeader(responseHeader);
                                    return Mono.just(response);
                                }
                            });
                });
//                });
            }
			updateConfirmFlagInMaster(requestObj, version, applicationID, customerIdentification, prop,
					isSelfOnBoardingAppId, isSelfOnBoardingHeaderAppId);
        }
        if (isAccountCreationisNextStage) {
            HashMap<String, Object> hm = new HashMap<>(); // HM is used to keep number of arguments less than 8 as per
            // sonarqube
            hm.put(versionHm, version);
            hm.put(propHm, prop);
            hm.put(headerHm, header);
            hm.put(applicationIDHm, applicationID);
            return accountCreationStageOperations(hm, applicationMaster, requestObj, isSelfOnBoardingAppId,
                    applyLoanRequest, customerIdentification, isSelfOnBoardingHeaderAppId);
        }

        Gson gson = new Gson();
        String responseStr = gson.toJson(customerIdentification);
        responseBody.setResponseObj(responseStr);
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return Mono.just(response);
    }

	private void updateApplicantsCount(String applicationID, Integer version, ApplyLoanRequestFields requestObj){
		UpdateApplicantsCountRequest updateApplicantsCountRequest = new UpdateApplicantsCountRequest();
		UpdateApplicantsCountRequestFields updateApplicantsCountRequestFields = new UpdateApplicantsCountRequestFields();
		updateApplicantsCountRequestFields.setAppId(requestObj.getAppId());
		updateApplicantsCountRequestFields.setApplicationId(applicationID);
		updateApplicantsCountRequestFields.setVersionNum(version);
		updateApplicantsCountRequestFields.setApplicantsCount(requestObj.getApplicationMaster().getApplicantsCount());
		updateApplicantsCountRequest.setRequestObj(updateApplicantsCountRequestFields);
		cobService.updateApplicantsCount(updateApplicantsCountRequest);
	}

	public void processLoanSourcing(String stageCode, ApplyLoanRequestFields requestFields,
			CustomerIdentificationLoan customerIdentification, boolean isMasterPresent, Properties prop) {

		logger.debug("Inside processLoanSourcing Method");
		String applicationId = customerIdentification.getApplicationId();
		String custType = stageCode.contains("_C_") ? Constants.COAPPLICANT : Constants.APPLICANT;
		BigDecimal custDtlId = commonParamService.generateCustDtlId(applicationId, custType);
		int version = Constants.INITIAL_VERSION_NO;
		String appId = requestFields.getAppId();
		boolean isSelfOnBoardingAppId = appId
				.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()));
		ApplicationMaster applicationMaster = requestFields.getApplicationMaster();

		//Calling Remaining update methods which are on existing flow
		switch(stageCode){
			case "createApp":
				logger.debug("Creation of application");
				applicationMaster.setAssignedTo("Y");
				requestFields.setApplicationMaster(applicationMaster);
				populateAppMasterAndApplnwf(requestFields, applicationId, version, customerIdentification, false, prop);
				applicationMaster.setApplicationId(applicationId);
				break;
			case "updateQuery":
				updateQueryResponse(requestFields, applicationId);
				break;
			case "1_A_":
				if (!isMasterPresent
					&& applicationMaster.getProductCode().equalsIgnoreCase(Constants.RENEWAL_PRODUCT_CODE)) {
					logger.debug("inside replicate application");
					replicateApplicationDetails(requestFields, customerIdentification, applicationId, custDtlId, version);
				}
				populateExistingLoanDtls(requestFields, customerIdentification, applicationId, custDtlId, version,
						Constants.CUSTOMER_DETAILS);
				updateApplicantsCount(applicationId, version,requestFields);
				break;
			case "1_C_":
				updateApplicantsCount(applicationId, version,requestFields);
				updateCoApplicantId(requestFields, applicationId, appId);
				break;
			case "4_":
				populateBankDtls(requestFields, customerIdentification, applicationId, custDtlId, version);
				break;
			default:
				logger.debug("All required fields are updated");
				break;
		}
	}

    private void updateQueryResponse(ApplyLoanRequestFields requestObj, String applicationID) {
        logger.debug("Entering updateQueryResponse for applicationID: {}", applicationID);
        JsonNode queryResponse = requestObj.getQueryResponse();
        ObjectMapper mapper = new ObjectMapper();
        String queryResponseString = "";
        try {
            queryResponseString = mapper.writeValueAsString(queryResponse);
            logger.debug("Converted queryResponse to string: {}", queryResponseString);
        } catch (JsonProcessingException e) {
            logger.error("Error converting query response to string: ", e);
        }

		Optional<SourcingResponseTracker> sourcingResponseTrackerOpt = sourcingResponseTrackerRepo
				.findById(applicationID);
        if (sourcingResponseTrackerOpt.isPresent()) {
            SourcingResponseTracker sourcingResponseTracker = sourcingResponseTrackerOpt.get();
            logger.debug("Found existing SourcingResponseTracker for applicationID: {}", applicationID);
            sourcingResponseTracker.setResponse(queryResponseString);
            sourcingResponseTrackerRepo.save(sourcingResponseTracker);
            logger.debug("Updated SourcingResponseTracker with new response for applicationID: {}", applicationID);
        } else {
            SourcingResponseTracker sourcingResponseTracker = new SourcingResponseTracker();
            sourcingResponseTracker.setApplicationId(applicationID);
            sourcingResponseTracker.setResponse(queryResponseString);
            sourcingResponseTracker.setCreatedAt(LocalDateTime.now());
            sourcingResponseTracker.setStage(""); // empty for now
            sourcingResponseTrackerRepo.save(sourcingResponseTracker);
            logger.debug("Created new SourcingResponseTracker for applicationID: {}", applicationID);
        }
        logger.debug("Exiting updateQueryResponse for applicationID: {}", applicationID);
    }

    public Set<String> cbReportExists(String applicationId, String appId, int versionNum, Properties prop) {
        Set<String> cbReportMissing = new HashSet<>();
        Gson gson = new Gson();
		String fileLocation = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId
				+ Constants.LOANPATH + applicationId + "/";

		logger.debug("Checking CB reports for applicationId: {}, appId: {}, versionNum: {}", applicationId, appId,
				versionNum);
        logger.debug("file location for CB reports: {}", fileLocation);

		Optional<List<CibilDetails>> cibilDetailsOpt = cibilDtlRepo
				.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        if (cibilDetailsOpt.isPresent()) {
            List<CibilDetails> cibilDetailsList = cibilDetailsOpt.get();
            logger.debug("Found {} CIBIL details for the application.", cibilDetailsList.size());

            for (CibilDetails cibilDetail : cibilDetailsList) {
				CibilDetailsPayload cibilDetailsPayload = gson.fromJson(cibilDetail.getPayloadColumn(),
						CibilDetailsPayload.class);
                String cbLoanId = cibilDetailsPayload.getCbLoanId();
                String applicantType = ""; // Initialize with a default value
				if (!cibilDetailsPayload.getBureauName().isEmpty()
						&& cibilDetailsPayload.getBureauName().equalsIgnoreCase("BRE")) {
                    if (cbLoanId.startsWith("A")) {
                        applicantType = Constants.APPLICANT;
                    } else if (cbLoanId.startsWith("C")) {
						applicantType = Constants.CO_APPLICANT;
                    }

                    File file = new File(fileLocation + cbLoanId + Constants.PDF_EXTENSION);
                    if (!file.exists() && !applicantType.isEmpty()) { // Ensure applicantType is valid
                        logger.warn("CB report missing for applicantType: {}, cbLoanId: {}", applicantType, cbLoanId);
                        cbReportMissing.add(applicantType);
                    } else {
                        logger.debug("CB report exists for applicantType: {}, cbLoanId: {}", applicantType, cbLoanId);
                    }
                }
            }
        } else {
			logger.warn("No CIBIL details found for applicationId: {}, appId: {}, versionNum: {}", applicationId, appId,
					versionNum);
            cbReportMissing.add(Constants.APPLICANT);
			cbReportMissing.add(Constants.CO_APPLICANT);
        }

        logger.debug("CB report missing types: {}", cbReportMissing);
        return cbReportMissing;
    }

    private void updateCoApplicantId(ApplyLoanRequestFields requestObj, String applicationID, String appId) {
        ObjectMapper objectMapper = new ObjectMapper();
        Gson gson = new Gson();
        int customerIndex = 1;
        try {
            List<CustomerDetails> customerDetailsList = requestObj.getCustomerDetailsList();
            for (CustomerDetails customerDtl : customerDetailsList) {
                customerIndex = customerDtl.getPayload().getCustomerIndex();
            }
            Optional<CustomerDetails> customerDetails = custDtlRepo
                    .findByApplicationIdAndAppIdAndCustomerType(applicationID, appId, Constants.APPLICANT);
            if (customerDetails.isPresent()) {
                CustomerDetails customerDetail = customerDetails.get();
                CustomerDetailsPayload customerDetailPayload = objectMapper.readValue(customerDetail.getPayloadColumn(),
                        CustomerDetailsPayload.class);
                customerDetailPayload.setCustomerIndex(customerIndex);

                customerDetail.setPayloadColumn(gson.toJson(customerDetailPayload));
                logger.warn("customerDetail.toString() to be updated: " + customerDetail.toString());
                custDtlRepo.save(customerDetail);
            }
        } catch (Exception e) {
            logger.error("Error : ", e.getMessage());
        }

    }

	public void replicateApplicationDetails(ApplyLoanRequestFields requestObj,
                                             CustomerIdentificationLoan customerIdentification, String applicationID, BigDecimal custDtlId,
                                             int version) {
		logger.warn("replicateApplicationDetails : " + requestObj);

		logger.warn("replicateApplicationDetails Existing Flow" + requestObj);

		List<UnnatiIexceedOccpInsr> occInsDetails = iexceedOccpInsrRepository
				.findByCustomerId(requestObj.getApplicationMaster().getSearchCode2());
        logger.debug("app id :occInsDetails " + occInsDetails.toString());

		String cdhProductCode = ProductCode.getCdhCodeByUnnatiCode(requestObj.getApplicationMaster().getProductCode());
		if(ProductCode.OPEN_MARKET.getUnnatiCode().equalsIgnoreCase(requestObj.getApplicationMaster().getProductCode())) {
			cdhProductCode = ProductCode.OPEN_MARKET.getUnnatiCode();
		}

		logger.debug("Caling CDH");
		Optional<UnnatiIexceedCDHLead> optLeadData = unnatiIexceedCDHLeadRepo
				.findByCustomerIdAndProductAndReferenceId(requestObj.getApplicationMaster().getSearchCode2(), cdhProductCode, requestObj.getApplicationMaster().getWorkitemNo());
		logger.debug("app id :renewalLeadData  " + optLeadData.isPresent());

		logger.debug("inside replicate application occInsDetails.isEmpty() --" + occInsDetails.isEmpty());
		logger.debug("inside replicate application leadData.isEmpty() --" + optLeadData.isPresent());

		List<UnnatiCoApplicantDetails> coApplicantDetails = coAppDetailsRepository
				.findByCustomerId(requestObj.getApplicationMaster().getSearchCode2());

		if (!occInsDetails.isEmpty() && optLeadData.isPresent() && !coApplicantDetails.isEmpty()) {
			UnnatiIexceedOccpInsr occInsDetail = occInsDetails.get(0);
            logger.debug("app id :occInsDetail  " + occInsDetail.toString());

			UnnatiIexceedCDHLead leadDetail = optLeadData.get();
			logger.debug("app id :renewalLeadDetail  " + leadDetail.toString());

			UnnatiCoApplicantDetails coAppDetail = coApplicantDetails.get(0);
			logger.debug("app id :coAppDetail  " + coAppDetail.toString());

			BigDecimal coAppCustDtlId = populateLeadCustomerDtls(coAppDetail, requestObj, applicationID, version);
            logger.debug("app id :coAppCustDtlId  " + coAppCustDtlId.toString());

			populateLeadAddressDtls(leadDetail, coAppDetail, occInsDetail, requestObj.getAppId(), applicationID,
					custDtlId, coAppCustDtlId, version);

			populateLeadOccupationdtls(occInsDetail, requestObj, applicationID, custDtlId, coAppCustDtlId, version);

			populateLeadInsuranceDtls(occInsDetail, requestObj, applicationID, custDtlId, coAppCustDtlId, version);
		} else {

        }
    }

    public Mono<Response> applyRejectLoan(ApplyLoanRequest applyLoanRequest, boolean isSelfOnBoardingHeaderAppId,
                                          Properties prop) {
        ApplyLoanRequestFields requestObj = applyLoanRequest.getRequestObj();
        ApplicationMaster applicationMaster = requestObj.getApplicationMaster();
        ResponseBody responseBody = new ResponseBody();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        CustomerIdentificationLoan customerIdentification = new CustomerIdentificationLoan();
        String applicationID = CommonUtils.generateRandomNumStr();
        Optional<ApplicationMaster> masterData = applicationMasterRepo.findByAppIdAndWorkitemNo(requestObj.getAppId(),
                applicationMaster.getWorkitemNo());
        if (masterData.isPresent()) {
            applicationID = masterData.get().getApplicationId();
        }
        int version = Constants.INITIAL_VERSION_NO;
        String custType = (requestObj.getApplicationMaster().getCustDtlSlNum() <= 1) ? Constants.APPLICANT
                : Constants.COAPPLICANT;
        BigDecimal custDtlId = commonParamService.generateCustDtlId(applicationID, custType);
        populateAppMasterAndApplnwf(requestObj, applicationID, version, customerIdentification,
                isSelfOnBoardingHeaderAppId, prop);
        commonParamService.populateCustomerDtlsIfNotPresent(requestObj.getApplicationMaster(), applicationID, custDtlId,
                version, requestObj.getAppId());

        String[] currentScreenIdArray = requestObj.getApplicationMaster().getCurrentScreenId().split("~");
        commonParamService.updateCurrentStageInMaster(requestObj.getApplicationMaster(), currentScreenIdArray, version,
                requestObj.getAppId(), requestObj.getApplicationId());

        populateCustomerDtls(requestObj, customerIdentification, applicationID, custDtlId, version);
        populateAddressDtls(requestObj, customerIdentification, applicationID, custDtlId, version,
                Constants.CUSTOMER_DETAILS, custType);

		updateApplicantsCount(applicationID, version, requestObj);

        populateExistingLoanDtls(requestObj, customerIdentification, applicationID, custDtlId, version,
                Constants.CUSTOMER_DETAILS);

        Mono<Response> rejectRespBlock = callRejectApplication(applyLoanRequest.getAppId(),
                applyLoanRequest.getUserId(), prop, applicationID, custDtlId, applicationMaster.getRemarks());
        Response rejectResp = rejectRespBlock.block();
        if (null != rejectResp) {
            if (rejectResp.getResponseHeader().getResponseCode().equals("0")) {
                Gson gson = new Gson();
                String responseStr = gson.toJson(customerIdentification);
                responseBody.setResponseObj(responseStr);
                response.setResponseBody(responseBody);
                response.setResponseHeader(responseHeader);
            } else {
                response.setResponseBody(rejectResp.getResponseBody());
                response.setResponseHeader(rejectResp.getResponseHeader());
            }
        } else {
            response = CommonUtils.formFailResponse(ResponseCodes.REJECT_RES_NOT_VALID.getValue(),
                    ResponseCodes.REJECT_RES_NOT_VALID.getKey());
        }
        return Mono.just(response);
    }

    private Mono<Response> callRejectApplication(String appId, String userId, Properties prop, String applicationId,
                                                 BigDecimal custDtlId, String remarks) {
        Mono<Response> response = Mono.empty();
        String roleId = commonParamService.fetchRoleId(appId, userId);
        RoleAccessMap objDb = cobService.fetchRoleAccessMapObj(appId, roleId);
        if (Constants.ACCESS_PERMISSION_VIEWONLY.equalsIgnoreCase(objDb.getAccessPermission())) {
            response = CommonUtils.formFailResponseMono(ResponseCodes.VAPT_ISSUE_PERMISSION.getValue(),
                    ResponseCodes.VAPT_ISSUE_PERMISSION.getKey());
        } else if (Constants.ACCESS_PERMISSION_APPROVER.equalsIgnoreCase(objDb.getAccessPermission())
                || Constants.ACCESS_PERMISSION_BOTH.equalsIgnoreCase(objDb.getAccessPermission())
                || Constants.ACCESS_PERMISSION_VERIFIER.equalsIgnoreCase(objDb.getAccessPermission())
                || Constants.ACCESS_PERMISSION_INITIATOR.equalsIgnoreCase(objDb.getAccessPermission())) {

            String rejectionFlowId = "REJECTAPPLICATION";
            String stageId = "INPUTINPROGRESS";
            String action = "REJECT";

            Optional<WorkflowDefinition> workflowDef = wfDefnRepoLn.findByAppIdAndWorkFlowIdAndFromStageId(appId,
                    rejectionFlowId, stageId);
            if (workflowDef.isPresent()) {
                WorkFlowDetails wf = new WorkFlowDetails();
                wf.setCurrentRole(roleId);
                wf.setAction(action);
                wf.setWorkflowId(rejectionFlowId);
                wf.setCurrentStage(stageId);
                wf.setNextStageId(workflowDef.get().getNextStageId());
                wf.setNextWorkflowStatus(workflowDef.get().getNextWorkflowStatus());
                FetchDeleteUserFields reqFields = new FetchDeleteUserFields();
                reqFields.setAppId(appId);
                reqFields.setStatus(AppStatus.REJECTED.getValue());
                reqFields.setUserId(userId);
                reqFields.setApplicationId(applicationId);
                reqFields.setVersionNum(Constants.INITIAL_VERSION_NO);
                reqFields.setCustDtlId(custDtlId);
                reqFields.setRemarks(remarks);
                reqFields.setWorkFlow(wf);
                FetchDeleteUserRequest req = new FetchDeleteUserRequest();
                req.setAppId(appId);
                req.setRequestObj(reqFields);
                logger.debug("Approve reject application request: {} ", req);
                response = rejectApplication(req, prop);
            }
        }
        return response;
    }

    private Mono<Response> rejectApplication(FetchDeleteUserRequest fetchDeleteUserRequest, Properties prop) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        response.setResponseHeader(responseHeader);
        FetchDeleteUserFields customerDataFields = fetchDeleteUserRequest.getRequestObj();
        String status = customerDataFields.getStatus();
        List<String> applnStatus = new ArrayList<>();
        applnStatus.add(AppStatus.PENDING.getValue());
        applnStatus.add(AppStatus.INPROGRESS.getValue());
        logger.debug("app id : {} ", customerDataFields.getAppId());
        logger.debug("application id : {} ", customerDataFields.getApplicationId());
        logger.debug("version no : {} ", customerDataFields.getVersionNum());
        logger.debug("application status : {} ", applnStatus);
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepo
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(customerDataFields.getAppId(),
                        customerDataFields.getApplicationId(), customerDataFields.getVersionNum(), applnStatus);
        logger.debug("Getting optional master object");
        if (masterObjDb.isPresent()) {
            logger.debug("Master data value present.");
            Gson gson = new Gson();
            CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
            ApplicationMaster masterObj = masterObjDb.get();
            masterObj.setRemarks(customerDataFields.getRemarks());
            cobService.updateStatus(masterObj, status);
            if (!CommonUtils.isNullOrEmpty(masterObj.getRelatedApplicationId())) {
                Optional<ApplicationMaster> appMasterRelated = applicationMasterRepo
                        .findByAppIdAndApplicationIdAndVersionNum(customerDataFields.getAppId(),
                                masterObj.getRelatedApplicationId(), customerDataFields.getVersionNum());
                if (appMasterRelated.isPresent()) {
                    ApplicationMaster appMasterObjRelated = appMasterRelated.get();
                    cobService.updateStatus(appMasterObjRelated, status);
                }
            }
            PopulateapplnWFRequest req = new PopulateapplnWFRequest();
            PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
            reqFields.setAppId(masterObj.getAppId());
            reqFields.setApplicationId(masterObj.getApplicationId());
            reqFields.setCreatedBy(customerDataFields.getUserId());
            reqFields.setVersionNum(masterObj.getVersionNum());
            reqFields.setApplicationStatus(masterObj.getApplicationStatus());
            WorkFlowDetails wf = customerDataFields.getWorkFlow();
            wf.setRemarks(customerDataFields.getRemarks());
            reqFields.setWorkflow(wf);
            req.setRequestObj(reqFields);
            commonParamService.populateApplnWorkFlow(req);
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
            customerIdentification.setVersionNum(customerDataFields.getVersionNum());
            responseBody.setResponseObj(gson.toJson(customerIdentification));
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
            logger.debug("application status update completed");
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
        }
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return Mono.just(response);
    }

    private Mono<Response> accountCreationStageOperations(HashMap<String, Object> hm,
                                                          ApplicationMaster applicationMaster, ApplyLoanRequestFields requestObj, boolean isSelfOnBoardingAppId,
                                                          ApplyLoanRequest applyLoanRequest, CustomerIdentificationLoan customerIdentification,
                                                          boolean isSelfOnBoardingHeaderAppId) {
        int version = (int) hm.get(versionHm);
        Properties prop = (Properties) hm.get(propHm);
        Header header = (Header) hm.get(headerHm);
        String applicationID = (String) hm.get(applicationIDHm);
        String[] strAr1 = new String[]{Constants.ACCOUNT_CREATION, "Y"};
        commonParamService.updateCurrentStageInMaster(applicationMaster, strAr1, version, requestObj.getAppId(),
                requestObj.getApplicationId());
        HashMap<String, Object> hm1 = new HashMap<>(); // HM is used to keep number of arguments less than 8 as per
        // sonarqube
        hm1.put(propHm, prop);
        hm1.put(versionHm, version);
        return createAccountInCbs(hm1, isSelfOnBoardingAppId, applyLoanRequest, customerIdentification, header,
                isSelfOnBoardingHeaderAppId, applicationID);
    }

    private Mono<Response> createAccountInCbs(HashMap<String, Object> hm1, boolean isSelfOnBoardingAppId,
                                              ApplyLoanRequest applyLoanRequest, CustomerIdentificationLoan customerIdentification, Header header,
                                              boolean isSelfOnBoardingHeaderAppId, String applicationID) {
        Properties prop = (Properties) hm1.get(propHm);
        int version = (int) hm1.get(versionHm);
        ApplyLoanRequestFields requestObj = applyLoanRequest.getRequestObj();
        ApplicationMaster masterRequest = requestObj.getApplicationMaster();
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepo
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(),
                        requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
            if (isSelfOnBoardingAppId) { // self onboarding
                if ("N".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.LOAN_STP.getKey()))) {
                    commonParamService.createAccountInCbsForNonStp(isSelfOnBoardingHeaderAppId, masterObj);
                } else if ("Y".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.LOAN_STP.getKey()))) {
                    String accNum;
                    if (CommonUtils.isNullOrEmpty(masterRequest.getAccNumber())) {
                        accNum = CommonUtils.generateRandomNumStr();
                    } else {
                        accNum = masterRequest.getAccNumber();
                    }
                    masterObj.setAccNumber(accNum);
                    customerIdentification.setAccNumber(accNum);
                    masterObj.setApplicationStatus(AppStatus.APPROVED.getValue());
                    applicationMasterRepo.save(masterObj);

                    // Hook to call external service for loan account creation.

                    CreateLoanRequest extReq = formExtReq(requestObj.getAppId(), requestObj.getApplicationId(), version,
                            accNum, masterObj.getCustomerId());
                    String interfaceName = prop.getProperty(CobFlagsProperties.LOAN_ACC_CREATION_INTF.getKey());
                    Mono<Object> extRes = interfaceAdapter.callExternalService(header, extReq, interfaceName);

                    return extRes.flatMap(val -> {
                        customerIdentification.setApplicationId(applicationID);
                        customerIdentification.setVersionNum(version);
                        ResponseBody responseBody = new ResponseBody();
                        Response response = new Response();
                        ResponseHeader responseHeader = new ResponseHeader();
                        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                        Gson gson = new Gson();
                        String responseStr = gson.toJson(customerIdentification);
                        responseBody.setResponseObj(responseStr);
                        response.setResponseBody(responseBody);
                        response.setResponseHeader(responseHeader);
                        return Mono.just(response);
                    });
                }
            } else { // assisted on boarding
                masterObj.setApplicationStatus(AppStatus.PENDING.getValue());
                applicationMasterRepo.save(masterObj);
            }
        }
        ResponseBody responseBody = new ResponseBody();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        Gson gson = new Gson();
        String responseStr = gson.toJson(customerIdentification);
        responseBody.setResponseObj(responseStr);
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return Mono.just(response);
    }

    public CreateLoanRequest formExtReq(String appId, String applicationId, int version, String accNum,
                                        BigDecimal customerId) {
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepo
                .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, version);
        CreateLoanRequest request = null;
        if (applicationMasterOpt.isPresent()) {
            request = new CreateLoanRequest();
            CreateLoanRequestFields requestObj = new CreateLoanRequestFields();
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            ApplicationMaster masterObj = new ApplicationMaster();
            BeanUtils.copyProperties(applicationMasterData, masterObj);
            masterObj.setAccNumber(accNum);
            masterObj.setCustomerId(customerId);
            masterObj.setCreateTs(null); // to avoid jackson parsing error. Need to send data based on external service
            // request during implementation.
            masterObj.setApplicationDate(null); // to avoid jackson parsing error. Need to send data based on external
            // service request during implementation.
            requestObj.setApplicationMaster(masterObj);

            LoanDetails loanDtl = loanDtlsRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, version);
            requestObj.setLoanDetails(loanDtl);

            request.setRequestObj(requestObj);
        }
        return request;
    }

    private void populateOccupationdtls(ApplyLoanRequestFields requestObj,
                                        CustomerIdentificationLoan customerIdentification, String applicationID, BigDecimal custDtlId, int version,
                                        String custType) {
        Gson gson = new Gson();
        List<String> occupationList = new ArrayList<>();
        List<OccupationDetailsWrapper> occupationDetailsWrapperList = requestObj.getOccupationDetailsWrapperList();
        for (OccupationDetailsWrapper occupationDetailsWrapper : occupationDetailsWrapperList) {
            OccupationDetails occupationDetails = occupationDetailsWrapper.getOccupationDetails();

            Optional<OccupationDetails> occupationDetailsDb = this.occupationDtlRepo
                    .findOccupationDetailsByCustomerType(custType, applicationID);

            if (occupationDetailsDb.isPresent()) {
                occupationDetails.setOccptDtlId(occupationDetailsDb.get().getOccptDtlId());
                occupationList.add(occupationDetailsDb.get().getOccptDtlId().toString());
            } else {
                BigDecimal occptnDtlId = CommonUtils.generateRandomNum();
                occupationDetails.setOccptDtlId(occptnDtlId);
                occupationList.add(occptnDtlId.toString());// to String is required to avoid rounding issue of Big
                // Decimal at front end.
            }
            occupationDetails.setAppId(requestObj.getAppId());
            occupationDetails.setApplicationId(applicationID);
            occupationDetails.setCustDtlId(custDtlId);
            occupationDetails.setVersionNum(version);
            String payload = gson.toJson(occupationDetails.getPayload());
            occupationDetails.setPayloadColumn(payload);
            occupationDtlRepo.save(occupationDetails);
            logger.warn("Data inserted into TB_ABOB_OCCUPATION_DETAILS for loans");
        }
        customerIdentification.setOccupationList(occupationList);
        customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue of
        // Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_OCCUPATION_DETAILS for loans");
    }

	private void populateLeadOccupationdtls(UnnatiIexceedOccpInsr occInsDetail, ApplyLoanRequestFields requestObj,
			String applicationID, BigDecimal custDtlId, BigDecimal coAppCustDtlId, int version) {
		logger.warn("populateRenewalOccupationdtls requestObj: " + requestObj);

        Gson gson = new Gson();

		logger.warn("populateRenewalOccupationdtls Existing Flow");
        OccupationDetails occupationDetail = new OccupationDetails();
        BigDecimal appOoccptnDtlId = CommonUtils.generateRandomNum();
        occupationDetail.setOccptDtlId(appOoccptnDtlId);
        occupationDetail.setAppId(requestObj.getAppId());
        occupationDetail.setApplicationId(applicationID);
        occupationDetail.setCustDtlId(custDtlId);
        occupationDetail.setVersionNum(version);
        OccupationDetailsPayload appPayload = new OccupationDetailsPayload();
        appPayload.setOccupationType(getDefaultValueIfObjNull(occInsDetail.getOccupationType()));
        appPayload.setDesignation(getDefaultValueIfObjNull(occInsDetail.getDesignation()));
        appPayload.setAnnualIncome(
                occInsDetail.getAnnualIncome() == null ? null : new BigDecimal(occInsDetail.getAnnualIncome()));
        appPayload.setOrganisationName(getDefaultValueIfObjNull(occInsDetail.getOrganisationName()));
        appPayload.setOfficePhone("");
        appPayload.setOfficeEmail("");
        appPayload.setEmployeeId("");
		appPayload.setEmployeeSince(getDefaultValueIfObjNull(occInsDetail.getEmpSince()));
        appPayload.setExperience(getDefaultValueIfObjNull(occInsDetail.getExperience()));
        appPayload.setEmployer(getDefaultValueIfObjNull(occInsDetail.getEmployer()));
        appPayload.setRetirementAge(getDefaultValueIfObjNull(occInsDetail.getRetirementAge()));
        appPayload.setLastEmployer(getDefaultValueIfObjNull(occInsDetail.getLastEmployer()));
		appPayload.setPreviousJobYears(getDefaultValueIfObjNull(occInsDetail.getPrevJobYears()));
        appPayload.setTypeOfEmployer(getDefaultValueIfObjNull(occInsDetail.getTypeOfEmployer()));
		appPayload.setNatureOfOccupation(getDefaultValueIfObjNull(occInsDetail.getNatureOfOccpn()));
        appPayload.setAddressProof("");
		appPayload.setBusinessAddressProof(getDefaultValueIfObjNull(occInsDetail.getBussAddProof()));
        appPayload.setEmploymentProof(getDefaultValueIfObjNull(occInsDetail.getEmploymentProof()));
        appPayload.setEmployeeActivity(getDefaultValueIfObjNull(occInsDetail.getEmployeeActivity()));
		appPayload.setBusinessPremiseOwnerShip(getDefaultValueIfObjNull(occInsDetail.getBussPremOwnship()));
        appPayload.setFreqOfIncome(getDefaultValueIfObjNull(occInsDetail.getFreqOfIncome()));
		appPayload.setOtherSourceIncome(getDefaultValueIfObjNull(occInsDetail.getOtherSrcIncome()));
		appPayload.setOtherSourceAnnualIncome(getDefaultValueIfObjNull(occInsDetail.getOtherSrcAnnInc()));
        appPayload.setStreetVendor(getDefaultValueIfObjNull(occInsDetail.getStreetVendor()));
        appPayload.setModeOfIncome("");
		appPayload.setTypeofbusiness(getDefaultValueIfObjNull(occInsDetail.getTypeOfBuss()));
		appPayload.setBusinessEmpStartDate(CommonUtils
				.formatCustomDate(getDefaultValueIfObjNull(occInsDetail.getBussEmpStartDate()), "dd/MM/yyyy"));
		appPayload.setBusinessEmpVintageYear(getDefaultValueIfObjNull(occInsDetail.getBussEmpVintage()));
        appPayload.setOccupationTag(getDefaultValueIfObjNull(occInsDetail.getOccupationTag()));

        String PayloadStr = gson.toJson(appPayload);
        occupationDetail.setPayloadColumn(PayloadStr);
        occupationDtlRepo.save(occupationDetail);
        logger.warn("Data inserted into TB_ABOB_OCCUPATION_DETAILS for applicant loans");

        OccupationDetails coAppOccupationDetail = new OccupationDetails();
        BigDecimal coappOoccptnDtlId = CommonUtils.generateRandomNum();
        coAppOccupationDetail.setOccptDtlId(coappOoccptnDtlId);
        coAppOccupationDetail.setAppId(requestObj.getAppId());
        coAppOccupationDetail.setApplicationId(applicationID);
        coAppOccupationDetail.setCustDtlId(coAppCustDtlId);
        coAppOccupationDetail.setVersionNum(version);

        OccupationDetailsPayload coappPayload = new OccupationDetailsPayload();

		coappPayload.setOccupationType(getDefaultValueIfObjNull(occInsDetail.getCoOccupationType()));
        coappPayload.setDesignation(getDefaultValueIfObjNull(occInsDetail.getCoDesignation()));
        coappPayload.setAnnualIncome(
				occInsDetail.getCoAnnualIncome() == null ? null : new BigDecimal(occInsDetail.getCoAnnualIncome()));
		coappPayload.setOrganisationName(getDefaultValueIfObjNull(occInsDetail.getCoOrganisationName()));
        coappPayload.setOfficePhone("");
        coappPayload.setOfficeEmail("");
        coappPayload.setEmployeeId("");
		coappPayload.setEmployeeSince(getDefaultValueIfObjNull(occInsDetail.getCoEmpSince()));
        coappPayload.setExperience(getDefaultValueIfObjNull(occInsDetail.getCoExperience()));
        coappPayload.setEmployer(getDefaultValueIfObjNull(occInsDetail.getCoEmployer()));
		coappPayload.setRetirementAge(getDefaultValueIfObjNull(occInsDetail.getCoRetirementAge()));
		coappPayload.setLastEmployer(getDefaultValueIfObjNull(occInsDetail.getCoLastEmployer()));
		coappPayload.setPreviousJobYears(getDefaultValueIfObjNull(occInsDetail.getCoPrevJobYears()));
		coappPayload.setTypeOfEmployer(getDefaultValueIfObjNull(occInsDetail.getCoTypeOfEmployer()));
		coappPayload.setNatureOfOccupation(getDefaultValueIfObjNull(occInsDetail.getCoNatureOfOccpn()));
        coappPayload.setAddressProof("");
		coappPayload.setBusinessAddressProof(getDefaultValueIfObjNull(occInsDetail.getCoBussAddProof()));
		coappPayload.setEmploymentProof(getDefaultValueIfObjNull(occInsDetail.getCoEmploymentProof()));
		coappPayload.setEmployeeActivity(getDefaultValueIfObjNull(occInsDetail.getCoEmployeeActivity()));
		coappPayload.setBusinessPremiseOwnerShip(getDefaultValueIfObjNull(occInsDetail.getCoBussPremOwnship()));
		coappPayload.setFreqOfIncome(getDefaultValueIfObjNull(occInsDetail.getCoFreqOfIncome()));
		coappPayload.setOtherSourceIncome(getDefaultValueIfObjNull(occInsDetail.getCoOtherSrcIncome()));
		coappPayload.setOtherSourceAnnualIncome(getDefaultValueIfObjNull(occInsDetail.getCoOtherSrcAnnInc()));
		coappPayload.setStreetVendor(getDefaultValueIfObjNull(occInsDetail.getCoStreetVendor()));
        coappPayload.setModeOfIncome("");
		coappPayload.setTypeofbusiness(getDefaultValueIfObjNull(occInsDetail.getCoTypeOfBuss()));
        coappPayload.setBusinessEmpStartDate(CommonUtils
				.formatCustomDate(getDefaultValueIfObjNull(occInsDetail.getCoBussEmpStartDate()), "dd/MM/yyyy"));
		coappPayload.setBusinessEmpVintageYear(getDefaultValueIfObjNull(occInsDetail.getCoBussEmpVintage()));
		coappPayload.setOccupationTag(getDefaultValueIfObjNull(occInsDetail.getCoOccupationTag()));

        String coAppPayloadStr = gson.toJson(coappPayload);
        coAppOccupationDetail.setPayloadColumn(coAppPayloadStr);
        occupationDtlRepo.save(coAppOccupationDetail);
        logger.warn("Data inserted into TB_ABOB_OCCUPATION_DETAILS for coapplicant loans");

    }

    private void populateCoAppOccupationAddressdtls(ApplyLoanRequestFields requestObj, String applicationID,
                                                    BigDecimal custDtlId, int version) {
        Gson gson = new Gson();
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> occupationList = new ArrayList<>();
        List<OccupationDetailsWrapper> occupationDetailsWrapperList = requestObj.getOccupationDetailsWrapperList();
        for (OccupationDetailsWrapper occupationDetailsWrapper : occupationDetailsWrapperList) {
            OccupationDetails occupationDetails = occupationDetailsWrapper.getOccupationDetails();
            if (occupationDetails.getOccptDtlId() == null) {// This is to handle the case if user changed the data after
                // its being inserted by using the back navigation within
                // the session.
                BigDecimal occptnDtlId = CommonUtils.generateRandomNum();
                occupationDetails.setOccptDtlId(occptnDtlId);
                occupationList.add(occptnDtlId.toString());// to String is required to avoid rounding issue of Big
                // Decimal at front end.
            } else {
                occupationList.add(occupationDetails.getOccptDtlId().toString());// to String is required to avoid
                // rounding issue of Big Decimal at
                // front end.
            }
            occupationDetails.setAppId(requestObj.getAppId());
            occupationDetails.setApplicationId(applicationID);
            occupationDetails.setCustDtlId(custDtlId);
            occupationDetails.setVersionNum(version);
            String payload = gson.toJson(occupationDetails.getPayload());
            occupationDetails.setPayloadColumn(payload);
            List<CustomerDetails> customerList = requestObj.getCustomerDetailsList();
            logger.warn("customerList : " + customerList);
            boolean isApplicant = false;
            for (CustomerDetails customer : customerList) {
                logger.warn("customer 1: " + (custDtlId.compareTo(customer.getCustDtlId()) == 0));
                logger.warn("customer 2: " + ("Applicant".equalsIgnoreCase(customer.getCustomerType())));
                if ((custDtlId.compareTo(customer.getCustDtlId()) == 0)
                        && ("Applicant".equalsIgnoreCase(customer.getCustomerType())))
                    isApplicant = true;
                logger.warn("customer : " + customer + "-" + isApplicant);
                logger.warn("customer : " + custDtlId + " - " + customer.getCustDtlId() + " - "
                        + customer.getCustomerType());
            }
            logger.debug("isApplicant : " + isApplicant);
            try {
                if (isApplicant) {
                    List<OccupationDetails> coAppOccupationDetailsList = occupationDtlRepo
                            .findByApplicationIdAndAppIdAndCustDtlIdNot(applicationID, requestObj.getAppId(),
                                    custDtlId);
                    logger.debug("coAppOccupationDetailsList : " + coAppOccupationDetailsList);
                    for (OccupationDetails coAppOccupationDetail : coAppOccupationDetailsList) {
                        logger.debug("coAppOccupationDetail : " + coAppOccupationDetail.toString());
                        OccupationDetailsPayload coAppOccupationPayload = objectMapper
                                .readValue(coAppOccupationDetail.getPayloadColumn(), OccupationDetailsPayload.class);
                        logger.debug("coAppOccupationPayload.getOccupationTag() : "
                                + coAppOccupationPayload.getOccupationTag());
                        if ((coAppOccupationPayload.getOccupationTag() != null)
                                && ("yes".equalsIgnoreCase(coAppOccupationPayload.getOccupationTag()))) {
                            OccupationDetailsPayload applicantOccupationPayload = objectMapper
                                    .readValue(occupationDetails.getPayloadColumn(), OccupationDetailsPayload.class);

                            applicantOccupationPayload.setOccupationTag(coAppOccupationPayload.getOccupationTag());
                            coAppOccupationDetail.setPayloadColumn(gson.toJson(applicantOccupationPayload));
                            occupationDtlRepo.save(coAppOccupationDetail);
                            logger.warn("Data inserted into TB_ABOB_OCCUPATION_DETAILS for coApplicant loans");
                            AddressDetails applicantAddressDetails = new AddressDetails();
                            AddressDetails coApplicantAddressDetails = new AddressDetails();
                            List<AddressDetails> appAddressDetailsList = addressDtlRepo
                                    .findByApplicationIdAndAppId(applicationID, requestObj.getAppId());
                            for (AddressDetails addressDetail : appAddressDetailsList) {
                                AddressDetailsPayload addrPayload = objectMapper
                                        .readValue(addressDetail.getPayloadColumn(), AddressDetailsPayload.class);

                                for (Address address : addrPayload.getAddressList()) {
                                    if ("Office".equalsIgnoreCase(address.getAddressType())) {
                                        if (custDtlId.compareTo(addressDetail.getCustDtlId()) == 0) {
                                            applicantAddressDetails = addressDetail;
                                        } else {
                                            coApplicantAddressDetails = addressDetail;
                                        }
                                    }
                                }
                            }
                            AddressDetailsPayload applicantPayload = objectMapper
                                    .readValue(applicantAddressDetails.getPayloadColumn(), AddressDetailsPayload.class);
                            logger.debug("applicantPayload : " + applicantPayload.toString());
                            logger.debug("applicantAddressDetails : " + applicantAddressDetails.toString());
                            logger.debug("coApplicantAddressDetails : " + coApplicantAddressDetails.toString());
                            coApplicantAddressDetails.setPayloadColumn(gson.toJson(applicantPayload));
                            logger.debug("coApplicantAddressDetails after : " + coApplicantAddressDetails.toString());
                            addressDtlRepo.save(coApplicantAddressDetails);
                        }

                    }
                }
            } catch (Exception e) {
                logger.error("Error : ", e.getMessage());
            }
        }
        logger.warn("Data inserted into TB_ABOB_OCCUPATION_DETAILS for loans");
    }

    private void populateInsuranceDtls(ApplyLoanRequestFields requestObj,
                                       CustomerIdentificationLoan customerIdentification, String applicationID, BigDecimal custDtlId, int version,
                                       String custType) {
        Gson gson = new Gson();
        List<String> insuranceList = new ArrayList<>();
        List<InsuranceDetailsWrapper> insuranceDetailsWrapperList = requestObj.getInsuranceDetailsWrapperList();
        for (InsuranceDetailsWrapper insuranceDetailsWrapper : insuranceDetailsWrapperList) {
            InsuranceDetails insuranceDetails = insuranceDetailsWrapper.getInsuranceDetails();

            Optional<InsuranceDetails> insuranceDetailsDb = insuranceDtlRepo
                    .findInsuranceDetailsByCustomerType(custType, applicationID);

            if (insuranceDetailsDb.isPresent()) {
                insuranceDetails.setInsuranceDtlId(insuranceDetailsDb.get().getInsuranceDtlId());
                insuranceList.add(insuranceDetailsDb.get().getInsuranceDtlId().toString());
            } else {
                BigDecimal insuranceDtlId = CommonUtils.generateRandomNum();
                insuranceDetails.setInsuranceDtlId(insuranceDtlId);
                insuranceList.add(insuranceDtlId.toString());// to String is required to avoid rounding issue of Big
                // Decimal at front end.
            }
            insuranceDetails.setAppId(requestObj.getAppId());
            insuranceDetails.setApplicationId(applicationID);
            insuranceDetails.setCustDtlId(custDtlId);
            insuranceDetails.setVersionNum(version);
            String payload = gson.toJson(insuranceDetails.getPayload());
            insuranceDetails.setPayloadColumn(payload);
            insuranceDtlRepo.save(insuranceDetails);
            logger.warn("Data inserted into TB_CGOB_INSURANCE_DTLS for loans");
            if ((requestObj.getApplicationMaster().getCustDtlSlNum() <= 1)
                    && ("No".equalsIgnoreCase(insuranceDetails.getPayload().getCoApplicantInsurance()))) {
                Optional<InsuranceDetails> insuDtlObj = insuranceDtlRepo
                        .findByApplicationIdAndAppIdAndCustDtlIdNot(applicationID, requestObj.getAppId(), custDtlId);
                logger.warn("insuDtlObj : " + insuDtlObj.toString());
                if (insuDtlObj.isPresent()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    InsuranceDetails insuDtl = insuDtlObj.get();
                    InsuranceDetailsPayload insuDtlsPayload;
                    try {
                        insuDtlsPayload = objectMapper.readValue(insuDtl.getPayloadColumn(),
                                InsuranceDetailsPayload.class);
                        insuDtl.setPayload(insuDtlsPayload);
                        insuDtl.getPayload().setAge("");
                        insuDtl.getPayload().setCoApplicantInsurance("");
                        insuDtl.getPayload().setInsuranceReqd("");
                        insuDtl.getPayload().setInsuredName("");
                        insuDtl.getPayload().setNomineeDob("");
                        insuDtl.getPayload().setNomineeName("");
                        insuDtl.getPayload().setNomineeRelation("");
                        String coAppPayload = gson.toJson(insuDtl.getPayload());
                        insuDtl.setPayloadColumn(coAppPayload);
                        logger.warn("insuDtl : " + insuDtl.toString());
                        insuranceDtlRepo.save(insuDtl);
                        logger.warn("Data updated into TB_CGOB_INSURANCE_DTLS for loans");
                    } catch (Exception e) {
                        logger.error("Error while updating insurance details : {}", e);
                    }

                }
            }
        }
        customerIdentification.setInsuranceList(insuranceList);
        customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue of
        // Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_CGOB_INSURANCE_DTLS for loans");
    }

	private void populateLeadInsuranceDtls(UnnatiIexceedOccpInsr occInsDetail, ApplyLoanRequestFields requestObj,
			String applicationID, BigDecimal custDtlId, BigDecimal coAppCustDtlId, int version) {
		logger.warn("populateRenewalInsuranceDtls requestObj: " + requestObj);

        Gson gson = new Gson();
		logger.warn("populateRenewalInsuranceDtls Existing Flow");
        InsuranceDetails appInsuranceDetail = new InsuranceDetails();
        BigDecimal appInsuranceDtlId = CommonUtils.generateRandomNum();
        appInsuranceDetail.setInsuranceDtlId(appInsuranceDtlId);
        appInsuranceDetail.setAppId(requestObj.getAppId());
        appInsuranceDetail.setApplicationId(applicationID);
        appInsuranceDetail.setCustDtlId(custDtlId);
        appInsuranceDetail.setVersionNum(version);
        InsuranceDetailsPayload appPayload = new InsuranceDetailsPayload();
        String appPayloadInsuranceReqd = getDefaultValueIfObjNull(occInsDetail.getInsuranceReqd());
        if ("yes".equalsIgnoreCase(appPayloadInsuranceReqd)) {
            appPayloadInsuranceReqd = "Y";
        } else if ("no".equalsIgnoreCase(appPayloadInsuranceReqd)) {
            appPayloadInsuranceReqd = "N";
        }
        appPayload.setInsuranceReqd(appPayloadInsuranceReqd);
        appPayload.setInsuredName(getDefaultValueIfObjNull(occInsDetail.getInsuredName()));
        appPayload.setNomineeName(getDefaultValueIfObjNull(occInsDetail.getNomineeName()));
        appPayload.setNomineeRelation(getDefaultValueIfObjNull(occInsDetail.getNomineeRelation()));
        appPayload.setNomineeDob(
                CommonUtils.formatCustomDate(getDefaultValueIfObjNull(occInsDetail.getNomineeDob()), "dd/MM/yyyy"));
        appPayload.setAge(getDefaultValueIfObjNull(occInsDetail.getAge()));
        appPayload.setCoApplicantInsurance(getDefaultValueIfObjNull(occInsDetail.getCoApplicantInsurance()));
        String appPayloadStr = gson.toJson(appPayload);
        appInsuranceDetail.setPayloadColumn(appPayloadStr);
        insuranceDtlRepo.save(appInsuranceDetail);
        logger.warn("Data inserted into TB_CGOB_INSURANCE_DTLS for applicant loans");

        InsuranceDetails coappInsuranceDetail = new InsuranceDetails();
        BigDecimal coappInsuranceDtlId = CommonUtils.generateRandomNum();
        coappInsuranceDetail.setInsuranceDtlId(coappInsuranceDtlId);
        coappInsuranceDetail.setAppId(requestObj.getAppId());
        coappInsuranceDetail.setApplicationId(applicationID);
        coappInsuranceDetail.setCustDtlId(coAppCustDtlId);
        coappInsuranceDetail.setVersionNum(version);
        InsuranceDetailsPayload coappPayload = new InsuranceDetailsPayload();
		String coappPayloadInsuranceReqd = getDefaultValueIfObjNull(occInsDetail.getCoInsuranceReqd());
        if ("yes".equalsIgnoreCase(coappPayloadInsuranceReqd)) {
            coappPayloadInsuranceReqd = "Y";
        } else if ("no".equalsIgnoreCase(coappPayloadInsuranceReqd)) {
            coappPayloadInsuranceReqd = "N";
        }
        coappPayload.setInsuranceReqd(coappPayloadInsuranceReqd);
		coappPayload.setInsuredName(getDefaultValueIfObjNull(occInsDetail.getCoInsuredName()));
		coappPayload.setNomineeName(getDefaultValueIfObjNull(occInsDetail.getCoNomineeName()));
		coappPayload.setNomineeRelation(getDefaultValueIfObjNull(occInsDetail.getCoNomineeRelation()));
        coappPayload.setNomineeDob(
				CommonUtils.formatCustomDate(getDefaultValueIfObjNull(occInsDetail.getCoNomineeDob()), "dd/MM/yyyy"));
        coappPayload.setAge(getDefaultValueIfObjNull(occInsDetail.getCoAge()));
		coappPayload.setCoApplicantInsurance(getDefaultValueIfObjNull(occInsDetail.getCoCoApplicantInsurance()));
        String coappPayloadStr = gson.toJson(coappPayload);
        coappInsuranceDetail.setPayloadColumn(coappPayloadStr);
        insuranceDtlRepo.save(coappInsuranceDetail);
        logger.warn("Data inserted into TB_CGOB_INSURANCE_DTLS for coapplicant loans");

    }

    private void populateBankDtls(ApplyLoanRequestFields requestObj, CustomerIdentificationLoan customerIdentification,
                                  String applicationID, BigDecimal custDtlId, int version) {
        Gson gson = new Gson();
        List<String> bankList = new ArrayList<>();
        List<BankDetailsWrapper> bankDetailsWrapperList = requestObj.getBankDetailsWrapperList();
        for (BankDetailsWrapper bankDetailsWrapper : bankDetailsWrapperList) {
            BankDetails bankDetails = bankDetailsWrapper.getBankDetails();

//			Optional<BankDetails> bankDetailsDb = bankDtlRepo.findByApplicationId(applicationID);
            Optional<BankDetails> bankDetailsDb = bankDtlRepo.findByApplicationIdAndCustDtlId(applicationID, custDtlId);

            if (bankDetailsDb.isPresent()) {
                bankDetails.setBankDtlId(bankDetailsDb.get().getBankDtlId());
                bankList.add(bankDetailsDb.get().getBankDtlId().toString());
            } else {
                BigDecimal bankDtlId = CommonUtils.generateRandomNum();
                bankDetails.setBankDtlId(bankDtlId);
                bankList.add(bankDtlId.toString());// to String is required to avoid rounding issue of Big
                // Decimal at front end.
            }
            bankDetails.setAppId(requestObj.getAppId());
            bankDetails.setApplicationId(applicationID);
            bankDetails.setCustDtlId(custDtlId);
            bankDetails.setVersionNum(version);
            String payload = gson.toJson(bankDetails.getPayload());
            bankDetails.setPayloadColumn(payload);
            bankDtlRepo.save(bankDetails);
        }
        customerIdentification.setBankList(bankList);
        customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue of
        // Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_CGOB_BANK_DTLS for loans");
    }

    private void populateAddressDtls(ApplyLoanRequestFields requestObj,
                                     CustomerIdentificationLoan customerIdentification, String applicationID, BigDecimal custDtlId, int version,
                                     String relatedScreen, String custType) {
        Gson gson = new Gson();
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> addressList = new ArrayList<>();
        List<AddressDetailsWrapper> addressDetailsWrapperList = requestObj.getAddressDetailsWrapperList();
        for (AddressDetailsWrapper addressDetailsWrapper : addressDetailsWrapperList) {
            List<AddressDetails> addressDetailsList = addressDetailsWrapper.getAddressDetailsList();
            for (AddressDetails addressDetails : addressDetailsList) {
                logger.debug("addressDetails.getAddressType() : " + addressDetails.getAddressType().toString());
                Optional<AddressDetails> addressDetailsDb = addressDtlRepo
                        .findAddressByCustomerTypeAndAddressTypeAndApplicationId(custType,
                                addressDetails.getAddressType(), applicationID);

                if (addressDetailsDb.isPresent()) {
                    logger.debug("inside present : " + addressDetailsDb.get().getAddressDtlsId());
                    addressDetails.setAddressDtlsId(addressDetailsDb.get().getAddressDtlsId());
                    addressList.add(addressDetailsDb.get().getAddressDtlsId().toString());
                } else {
                    BigDecimal addressDtlId = CommonUtils.generateRandomNum();
                    addressDetails.setAddressDtlsId(addressDtlId);
                    addressList.add(addressDtlId.toString());

                    if (Constants.CUSTOMER_DETAILS.equalsIgnoreCase(relatedScreen)) {
                        addressDetails.setUniqueId(custDtlId);
                    } else if (Constants.OCCUPATION_DETAILS.equalsIgnoreCase(relatedScreen)) {
                        List<String> occupationList = customerIdentification.getOccupationList();
                        for (String occptDtlId : occupationList) {
                            addressDetails.setUniqueId(new BigDecimal(occptDtlId));
                        }
                    }
                }
                addressDetails.setAppId(requestObj.getAppId());
                addressDetails.setApplicationId(applicationID);
                addressDetails.setCustDtlId(custDtlId);
                addressDetails.setVersionNum(version);
                String payload = gson.toJson(addressDetails.getPayload());
                addressDetails.setPayloadColumn(payload);
                addressDtlRepo.save(addressDetails);
                try {
                    boolean Updated = false;
                    for (Address addrList : addressDetails.getPayload().getAddressList()) {
                        logger.warn("addrList : " + addrList.toString());
                        logger.warn("addrList.getAddressType() : " + addrList.getAddressType());
                        if ("Office".equalsIgnoreCase(addrList.getAddressType())) {
                            List<AddressDetails> custAddressDetailsList = addressDtlRepo
                                    .findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(applicationID,
                                            requestObj.getAppId(), version, custDtlId);
                            logger.warn("custAddressDetailsList.toString() : " + custAddressDetailsList.toString());
                            for (AddressDetails addressDetail : custAddressDetailsList) {
                                logger.warn("addressDetail.toString() : " + addressDetail.toString());
                                AddressDetailsPayload addrPayload = objectMapper
                                        .readValue(addressDetail.getPayloadColumn(), AddressDetailsPayload.class);
                                List<Address> updatedAddrList = new ArrayList<>();
                                for (Address address : addrPayload.getAddressList()) {
                                    logger.warn("address.toString() : " + address.toString());
                                    logger.warn("updatedAddrList.toString() : " + updatedAddrList.toString());
                                    if ("business".equalsIgnoreCase(address.getAddressSameAs())
                                            && "Communication".equalsIgnoreCase(address.getAddressType())) {
                                        Address UpdAddr = new Address();
                                        UpdAddr = addrList;
                                        logger.warn("UpdAddr.toString() 1 : " + UpdAddr.toString());
                                        UpdAddr.setAddressSameAs(address.getAddressSameAs());
                                        UpdAddr.setAddressType(address.getAddressType());
                                        logger.warn("UpdAddr.toString() 2 : " + UpdAddr.toString());
                                        updatedAddrList.add(UpdAddr);
                                        Updated = true;
                                    } else {
                                        updatedAddrList.add(address);
                                    }
                                }
                                if (Updated) {
                                    Updated = false;
                                    AddressDetailsPayload updtPayload = objectMapper
                                            .readValue(addressDetail.getPayloadColumn(), AddressDetailsPayload.class);
                                    logger.warn("inside Updated : " + Updated);
                                    updtPayload.setAddressList(updatedAddrList);
                                    addressDetail.setPayloadColumn(gson.toJson(updtPayload));
                                    logger.warn("addressDetail.toString() to be updated: " + addressDetail.toString());
                                    addressDtlRepo.save(addressDetail);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error while populating address details: ", e);
                }

            }
            customerIdentification.setAddressList(addressList);
            customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue
            // of Big Decimal at front end.
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
        }
        logger.warn("Data inserted into TB_ABOB_ADDRESS_DETAILS for loans");
    }

	private void populateLeadAddressDtls(UnnatiIexceedCDHLead leadDetails, UnnatiCoApplicantDetails coAppDetails,
			UnnatiIexceedOccpInsr occInsDetail, String appId, String applicationID, BigDecimal custDtlId,
			BigDecimal coAppCustDtlId, int version) {
        Gson gson = new Gson();
		logger.debug(
				"Entered populateRenewalAddressDtls method with parameters: LeadDetail={}, CoappDetails = {}, occInsDetail={}, requestObj={}, applicationID={}, custDtlId={}, coAppCustDtlId={}, version={}",
				leadDetails, coAppDetails, occInsDetail, appId, applicationID, custDtlId, coAppCustDtlId, version);
        AddressDetails applicantAddressDetail = new AddressDetails();
        BigDecimal applicantAddressDtlId = CommonUtils.generateRandomNum();
        applicantAddressDetail.setAddressDtlsId(applicantAddressDtlId);
		applicantAddressDetail.setAppId(appId);
        applicantAddressDetail.setApplicationId(applicationID);
        applicantAddressDetail.setCustDtlId(custDtlId);
        applicantAddressDetail.setVersionNum(version);
        applicantAddressDetail.setAddressType(Constants.PERSONAL);
        List<Address> applicantAddressList = new ArrayList<Address>();
        Address presentAddress = new Address();
        Address permanentAddress = new Address();
        Address communicationAddress = new Address();

        presentAddress.setAddressType(Constants.PRESENT);
		presentAddress.setAddressLine1(getDefaultValueIfObjNull(leadDetails.getPresentLine1()));
		presentAddress.setAddressLine2(getDefaultValueIfObjNull(leadDetails.getPresentLine2()));
		presentAddress.setAddressLine3(getDefaultValueIfObjNull(leadDetails.getPresentLine3()));
		presentAddress.setDistrict(getDefaultValueIfObjNull(leadDetails.getPresentDistrict()));
		presentAddress.setCity(getDefaultValueIfObjNull(leadDetails.getPresentDistrict()));
		presentAddress.setState(getDefaultValueIfObjNull(leadDetails.getPresentState()));
		presentAddress.setCountry(getDefaultValueIfObjNull(leadDetails.getPresentCountry()));
		presentAddress.setPinCode(getDefaultValueIfObjNull(leadDetails.getPresentPincode()));
		presentAddress.setLandMark(getDefaultValueIfObjNull(leadDetails.getPrestAddreLandmark()));
		presentAddress.setArea(getDefaultValueIfObjNull(leadDetails.getPresentDistrict()));
		presentAddress.setCurrentAddressProof(getDefaultValueIfObjNull(leadDetails.getPrestAddreCurrentAddressProof()));
		presentAddress.setHouseType(getDefaultValueIfObjNull(leadDetails.getPrestAddreHouseType()));
		presentAddress.setLocateCoOrdinates(getDefaultValueIfObjNull(leadDetails.getPresentLocationCoordinates()));
        presentAddress.setLocateCoOrdinatesFor("");
		presentAddress.setResidenceOwnership(getDefaultValueIfObjNull(leadDetails.getPrestAddreResidentOwnership()));
        presentAddress
				.setResidenceAddressSince(getDefaultValueIfObjNull(leadDetails.getPrestAddreResidenceAddressSince()));
		presentAddress.setResidenceCitySince(getDefaultValueIfObjNull(leadDetails.getPrestAddreResidenceCitySince()));

        permanentAddress.setAddressType(Constants.PERMANENT);
        permanentAddress.setAddressSameAs("");
		permanentAddress.setAddressLine1(getDefaultValueIfObjNull(leadDetails.getPermanentLine1()));
		permanentAddress.setAddressLine2(getDefaultValueIfObjNull(leadDetails.getPermanentLine2()));
		permanentAddress.setAddressLine3(getDefaultValueIfObjNull(leadDetails.getPermanentLine3()));
		permanentAddress.setDistrict(getDefaultValueIfObjNull(leadDetails.getPermanentDistrict()));
		permanentAddress.setCity(getDefaultValueIfObjNull(leadDetails.getPermanentDistrict()));
		permanentAddress.setState(getDefaultValueIfObjNull(leadDetails.getPermanentState()));
		permanentAddress.setCountry(getDefaultValueIfObjNull(leadDetails.getPermanentCountry()));
		permanentAddress.setPinCode(getDefaultValueIfObjNull(leadDetails.getPermanentPincode()));
		permanentAddress.setLandMark(getDefaultValueIfObjNull(leadDetails.getPermtAddreLandmark()));
		permanentAddress.setArea(getDefaultValueIfObjNull(leadDetails.getPermanentDistrict()));
        permanentAddress
				.setCurrentAddressProof(getDefaultValueIfObjNull(leadDetails.getPermtAddreCurrentAddressProof()));
		permanentAddress.setHouseType(getDefaultValueIfObjNull(leadDetails.getPermtAddreHouseType()));
		permanentAddress.setLocateCoOrdinates(getDefaultValueIfObjNull(leadDetails.getPermanentLocationCoordinates()));
        permanentAddress.setLocateCoOrdinatesFor("");
		permanentAddress.setResidenceOwnership(getDefaultValueIfObjNull(leadDetails.getPermtAddreResidentOwnership()));
        permanentAddress
				.setResidenceAddressSince(getDefaultValueIfObjNull(leadDetails.getPermtAddreResidenceAddressSince()));
		permanentAddress.setResidenceCitySince(getDefaultValueIfObjNull(leadDetails.getPermtAddreResidenceCitySince()));

		if (null != occInsDetail) {

        communicationAddress.setAddressType(Constants.COMMUNICATION);
			communicationAddress.setAddressSameAs(getDefaultValueIfObjNull(occInsDetail.getComAddressSameAs()));
			communicationAddress.setAddressLine1(getDefaultValueIfObjNull(occInsDetail.getComAddLine1()));
			communicationAddress.setAddressLine2(getDefaultValueIfObjNull(occInsDetail.getComAddLine2()));
			communicationAddress.setAddressLine3(getDefaultValueIfObjNull(occInsDetail.getComAddLine3()));
			communicationAddress.setDistrict(getDefaultValueIfObjNull(occInsDetail.getComDistrict()));
			communicationAddress.setCity(getDefaultValueIfObjNull(occInsDetail.getComCity()));
			communicationAddress.setState(getDefaultValueIfObjNull(occInsDetail.getComState()));
			communicationAddress.setCountry(getDefaultValueIfObjNull(occInsDetail.getComCountry()));
			communicationAddress.setPinCode(getDefaultValueIfObjNull(occInsDetail.getComPincode()));
			communicationAddress.setLandMark(getDefaultValueIfObjNull(occInsDetail.getComLandmark()));
			communicationAddress.setArea(getDefaultValueIfObjNull(occInsDetail.getComArea()));
        communicationAddress.setCurrentAddressProof("");
        communicationAddress.setHouseType("");
        communicationAddress.setLocateCoOrdinates("");
        communicationAddress.setLocateCoOrdinatesFor("");
        communicationAddress.setResidenceOwnership(getDefaultValueIfObjNull(occInsDetail.getResidenceOwnership()));
        communicationAddress
					.setResidenceAddressSince(getDefaultValueIfObjNull(occInsDetail.getResidenceAddSince()));
        communicationAddress.setResidenceCitySince(getDefaultValueIfObjNull(occInsDetail.getResidenceCitySince()));

			applicantAddressList.add(communicationAddress);
		}

        applicantAddressList.add(presentAddress);
        applicantAddressList.add(permanentAddress);
        AddressDetailsPayload payloadObj = new AddressDetailsPayload();
        payloadObj.setAddressList(applicantAddressList);

        String applicantAddrPayload = gson.toJson(payloadObj);
        applicantAddressDetail.setPayloadColumn(applicantAddrPayload);
		if(!leadDetails.getProduct().equalsIgnoreCase(ProductCode.UNNATI_RENEW.getCdhCode())) {
			addressDtlRepo.save(applicantAddressDetail);
		}
        logger.warn("Data inserted into TB_ABOB_ADDRESS_DETAILS for applicant renewal loans: {}",
                applicantAddressDetail.toString());

		if (null != coAppDetails) {

        AddressDetails coApplicantAddressDetail = new AddressDetails();
        BigDecimal coAppAddressDtlId = CommonUtils.generateRandomNum();
        coApplicantAddressDetail.setAddressDtlsId(coAppAddressDtlId);
			coApplicantAddressDetail.setAppId(appId);
        coApplicantAddressDetail.setApplicationId(applicationID);
        coApplicantAddressDetail.setCustDtlId(coAppCustDtlId);
        coApplicantAddressDetail.setVersionNum(version);
        coApplicantAddressDetail.setAddressType(Constants.PERSONAL);
        List<Address> coApplicantAddressList = new ArrayList<Address>();
        Address coPresentAddress = new Address();
        Address coPermanentAddress = new Address();
        Address coCommunicationAddress = new Address();

        coPresentAddress.setAddressType(Constants.PRESENT);
			coPresentAddress.setAddressLine1(getDefaultValueIfObjNull(coAppDetails.getCoPresentLine1()));
			coPresentAddress.setAddressLine2(getDefaultValueIfObjNull(coAppDetails.getCoPresentLine2()));
			coPresentAddress.setAddressLine3(getDefaultValueIfObjNull(coAppDetails.getCoPresentLine3()));
			coPresentAddress.setDistrict(getDefaultValueIfObjNull(coAppDetails.getCoPresentDistrict()));
			coPresentAddress.setCity(getDefaultValueIfObjNull(coAppDetails.getCoPresentCityTownVillage()));
			coPresentAddress.setState(getDefaultValueIfObjNull(coAppDetails.getCoPresentState()));
			coPresentAddress.setCountry(getDefaultValueIfObjNull(coAppDetails.getCoPresentCountry()));
			coPresentAddress.setPinCode(getDefaultValueIfObjNull(coAppDetails.getCoPresentPincode()));
			coPresentAddress.setLandMark(getDefaultValueIfObjNull(coAppDetails.getCoPrestAddresLandmark()));
			coPresentAddress.setArea(getDefaultValueIfObjNull(coAppDetails.getCoPresentArea()));
        coPresentAddress.setCurrentAddressProof(
					getDefaultValueIfObjNull(coAppDetails.getCoPrestAddresCurrentaddressproof()));
			coPresentAddress.setHouseType(getDefaultValueIfObjNull(coAppDetails.getCoPrestAddresHousetype()));
        coPresentAddress
					.setLocateCoOrdinates(getDefaultValueIfObjNull(coAppDetails.getCoPresentLocationCoOrdinates()));
			coPresentAddress.setLocateCoOrdinatesFor(coAppDetails.getCoPresentLocationCoOrdinates());
        coPresentAddress
					.setResidenceOwnership(getDefaultValueIfObjNull(coAppDetails.getCoPrestAddresResidentownership()));
        coPresentAddress.setResidenceAddressSince(
					getDefaultValueIfObjNull(coAppDetails.getCoPrestAddresResidenceaddresssince()));
			coPresentAddress
					.setResidenceCitySince(getDefaultValueIfObjNull(coAppDetails.getCoPermtAddresResidencecitysince()));

        coPermanentAddress.setAddressType(Constants.PERMANENT);
			coPermanentAddress.setAddressSameAs(coAppDetails.getCoPermtAddrsameas());
			coPermanentAddress.setAddressLine1(getDefaultValueIfObjNull(coAppDetails.getCoPermanentLine1()));
			coPermanentAddress.setAddressLine2(getDefaultValueIfObjNull(coAppDetails.getCoPermanentLine2()));
			coPermanentAddress.setAddressLine3(getDefaultValueIfObjNull(coAppDetails.getCoPermanentLine3()));
			coPermanentAddress.setDistrict(getDefaultValueIfObjNull(coAppDetails.getCoPermanentDistrict()));
			coPermanentAddress.setCity(getDefaultValueIfObjNull(coAppDetails.getCoPermanentCityTownVillage()));
			coPermanentAddress.setState(getDefaultValueIfObjNull(coAppDetails.getCoPermanentState()));
			coPermanentAddress.setCountry(getDefaultValueIfObjNull(coAppDetails.getCoPermanentCountry()));
			coPermanentAddress.setPinCode(getDefaultValueIfObjNull(coAppDetails.getCoPermanentPincode()));
			coPermanentAddress.setLandMark(getDefaultValueIfObjNull(coAppDetails.getCoPermtAddresLandmark()));
			coPermanentAddress.setArea(getDefaultValueIfObjNull(coAppDetails.getCoPermanentArea()));
        coPermanentAddress.setCurrentAddressProof(
					getDefaultValueIfObjNull(coAppDetails.getCoPermtAddresCurrentaddressproof()));
			coPermanentAddress.setHouseType(getDefaultValueIfObjNull(coAppDetails.getCoPermtAddresHousetype()));
        coPermanentAddress
					.setLocateCoOrdinates(getDefaultValueIfObjNull(coAppDetails.getCoPermanentLocationCoOrdinates()));
        coPermanentAddress.setLocateCoOrdinatesFor("");
        coPermanentAddress
					.setResidenceOwnership(getDefaultValueIfObjNull(coAppDetails.getCoPermtAddresResidentownership()));
        coPermanentAddress.setResidenceAddressSince(
					getDefaultValueIfObjNull(coAppDetails.getCoPermtAddresResidenceaddresssince()));
			coPermanentAddress
					.setResidenceCitySince(getDefaultValueIfObjNull(coAppDetails.getCoPermtAddresResidencecitysince()));

			if (null != occInsDetail) {
				/**
				 * Co-App communicationAddress
				 */
        coCommunicationAddress.setAddressType(Constants.COMMUNICATION);
				coCommunicationAddress.setAddressSameAs(getDefaultValueIfObjNull(occInsDetail.getCoComAddressSameAs()));
				coCommunicationAddress.setAddressLine1(getDefaultValueIfObjNull(occInsDetail.getCoComAddLine1()));
				coCommunicationAddress.setAddressLine2(getDefaultValueIfObjNull(occInsDetail.getCoComAddLine2()));
				coCommunicationAddress.setAddressLine3(getDefaultValueIfObjNull(occInsDetail.getCoComAddLine3()));
				coCommunicationAddress.setDistrict(getDefaultValueIfObjNull(occInsDetail.getCoComDistrict()));
				coCommunicationAddress.setCity(getDefaultValueIfObjNull(occInsDetail.getCoComCity()));
				coCommunicationAddress.setState(getDefaultValueIfObjNull(occInsDetail.getCoComState()));
				coCommunicationAddress.setCountry(getDefaultValueIfObjNull(occInsDetail.getCoComCountry()));
				coCommunicationAddress.setPinCode(getDefaultValueIfObjNull(occInsDetail.getCoComPincode()));
				coCommunicationAddress.setLandMark(getDefaultValueIfObjNull(occInsDetail.getCoComLandmark()));
				coCommunicationAddress.setArea(getDefaultValueIfObjNull(occInsDetail.getCoComArea()));
        coCommunicationAddress.setCurrentAddressProof("");
        coCommunicationAddress.setHouseType("");
        coCommunicationAddress.setLocateCoOrdinates("");
        coCommunicationAddress.setLocateCoOrdinatesFor("");
        coCommunicationAddress
						.setResidenceOwnership(getDefaultValueIfObjNull(occInsDetail.getCoResidenceOwnership()));
				coCommunicationAddress
						.setResidenceAddressSince(getDefaultValueIfObjNull(occInsDetail.getCoResidenceAddSince()));
				coCommunicationAddress
						.setResidenceCitySince(getDefaultValueIfObjNull(occInsDetail.getCoResidenceCitySince()));
				coApplicantAddressList.add(coCommunicationAddress);
			}

        coApplicantAddressList.add(coPresentAddress);
        coApplicantAddressList.add(coPermanentAddress);
			// Removed coapplicant communication since details not present in CDH
        AddressDetailsPayload coAppPayloadObj = new AddressDetailsPayload();
        coAppPayloadObj.setAddressList(coApplicantAddressList);

        String coApplicantAddrPayload = gson.toJson(coAppPayloadObj);
        coApplicantAddressDetail.setPayloadColumn(coApplicantAddrPayload);
        addressDtlRepo.save(coApplicantAddressDetail);
        logger.warn("Data inserted into TB_ABOB_ADDRESS_DETAILS for co-applicant renewal loans: {}",
                coApplicantAddressDetail.toString());

		}

        /*----------------------------- populating occupation address details -----------------------------*/
		if (null != occInsDetail) {

        AddressDetails applicantOccuAddressDetail = new AddressDetails();
        BigDecimal applicantOccuAddressDtlId = CommonUtils.generateRandomNum();
        applicantOccuAddressDetail.setAddressDtlsId(applicantOccuAddressDtlId);
			applicantOccuAddressDetail.setAppId(appId);
        applicantOccuAddressDetail.setApplicationId(applicationID);
        applicantOccuAddressDetail.setCustDtlId(custDtlId);
        applicantOccuAddressDetail.setVersionNum(version);
        applicantOccuAddressDetail.setAddressType(Constants.OCCUPATION);
        List<Address> applicantOccuAddressList = new ArrayList<Address>();
        // Address occuPresentAddress = new Address();
        Address occuOfficeAddress = new Address();
        // Address communicationAddress = new Address();

        occuOfficeAddress.setAddressType(Constants.OFFICE);
        occuOfficeAddress.setAddressSameAs(getDefaultValueIfObjNull(occInsDetail.getOffAddressSameAs()));
			occuOfficeAddress.setAddressLine1(getDefaultValueIfObjNull(occInsDetail.getOffAddLine1()));
			occuOfficeAddress.setAddressLine2(getDefaultValueIfObjNull(occInsDetail.getOffAddLine2()));
			occuOfficeAddress.setAddressLine3(getDefaultValueIfObjNull(occInsDetail.getOffAddLine3()));
        occuOfficeAddress.setDistrict(getDefaultValueIfObjNull(occInsDetail.getOffDistrict()));
        occuOfficeAddress.setCity(getDefaultValueIfObjNull(occInsDetail.getOffCity()));
        occuOfficeAddress.setState(getDefaultValueIfObjNull(occInsDetail.getOffState()));
        occuOfficeAddress.setCountry(getDefaultValueIfObjNull(occInsDetail.getOffCountry()));
			occuOfficeAddress.setPinCode(getDefaultValueIfObjNull(occInsDetail.getOffPincode()));
			occuOfficeAddress.setLandMark(getDefaultValueIfObjNull(occInsDetail.getOffLandmark()));
        occuOfficeAddress.setArea(getDefaultValueIfObjNull(occInsDetail.getOffArea()));

        applicantOccuAddressList.add(occuOfficeAddress);
        AddressDetailsPayload appOccuPayloadObj = new AddressDetailsPayload();
        appOccuPayloadObj.setAddressList(applicantOccuAddressList);

        String applicantOccuAddrPayload = gson.toJson(appOccuPayloadObj);
        applicantOccuAddressDetail.setPayloadColumn(applicantOccuAddrPayload);
        addressDtlRepo.save(applicantOccuAddressDetail);
        logger.warn("Data inserted into TB_ABOB_ADDRESS_DETAILS for applicant Occupation renewal loans: {}",
                applicantOccuAddressDetail.toString());

			if (null != coAppDetails) {

				AddressDetails CoApplicantOccuAddressDetail = new AddressDetails();
				BigDecimal CoApplicantOccuAddressDtlId = CommonUtils.generateRandomNum();
				CoApplicantOccuAddressDetail.setAddressDtlsId(CoApplicantOccuAddressDtlId);
				CoApplicantOccuAddressDetail.setAppId(appId);
				CoApplicantOccuAddressDetail.setApplicationId(applicationID);
				CoApplicantOccuAddressDetail.setCustDtlId(coAppCustDtlId);
				CoApplicantOccuAddressDetail.setVersionNum(version);
				CoApplicantOccuAddressDetail.setAddressType(Constants.OCCUPATION);
				List<Address> CoApplicantOccuAddressList = new ArrayList<Address>();
				// Address occuPresentAddress = new Address();
				Address CoOccuOfficeAddress = new Address();

				CoOccuOfficeAddress.setAddressType(Constants.OFFICE);
				CoOccuOfficeAddress.setAddressSameAs(getDefaultValueIfObjNull(occInsDetail.getCoOffAddressSameAs()));
				CoOccuOfficeAddress.setAddressLine1(getDefaultValueIfObjNull(occInsDetail.getCoOffAddLine1()));
				CoOccuOfficeAddress.setAddressLine2(getDefaultValueIfObjNull(occInsDetail.getCoOffAddLine2()));
				CoOccuOfficeAddress.setAddressLine3(getDefaultValueIfObjNull(occInsDetail.getCoOffAddLine3()));
				CoOccuOfficeAddress.setDistrict(getDefaultValueIfObjNull(occInsDetail.getCoOffDistrict()));
				CoOccuOfficeAddress.setCity(getDefaultValueIfObjNull(occInsDetail.getCoOffCity()));
				CoOccuOfficeAddress.setState(getDefaultValueIfObjNull(occInsDetail.getCoOffState()));
				CoOccuOfficeAddress.setCountry(getDefaultValueIfObjNull(occInsDetail.getCoOffCountry()));
				CoOccuOfficeAddress.setPinCode(getDefaultValueIfObjNull(occInsDetail.getCoOffPincode()));
				CoOccuOfficeAddress.setLandMark(getDefaultValueIfObjNull(occInsDetail.getCoOffLandmark()));
				CoOccuOfficeAddress.setArea(getDefaultValueIfObjNull(occInsDetail.getCoOffArea()));

				CoApplicantOccuAddressList.add(CoOccuOfficeAddress);
				AddressDetailsPayload CoAppOccuPayloadObj = new AddressDetailsPayload();
				CoAppOccuPayloadObj.setAddressList(CoApplicantOccuAddressList);

				String CoApplicantOccuAddrPayload = gson.toJson(CoAppOccuPayloadObj);
				CoApplicantOccuAddressDetail.setPayloadColumn(CoApplicantOccuAddrPayload);
//				addressDtlRepo.save(CoApplicantOccuAddressDetail);
				logger.warn("Data inserted into TB_ABOB_ADDRESS_DETAILS for Co-Applicant Occupation renewal loans: {}",
						CoApplicantOccuAddressDetail.toString());
			}
		}
    }

    private void populateExistingLoanDtls(ApplyLoanRequestFields requestObj,
                                          CustomerIdentificationLoan customerIdentification, String applicationID, BigDecimal custDtlId, int version,
                                          String relatedScreen) {
        Gson gson = new Gson();
        List<String> existingLoanList = new ArrayList<>();
        List<String> loanList = new ArrayList<>();
        List<ExistingLoanDetailsWrapper> existingLoanDetailsWrapperList = requestObj
                .getExistingLoanDetailsWrapperList();
        for (ExistingLoanDetailsWrapper existingLoanDetailsWrapper : existingLoanDetailsWrapperList) {
            List<ExistingLoanDetails> existingLoanDetailsList = existingLoanDetailsWrapper.getExistingLoanDetailsList();
            for (ExistingLoanDetails existingLoanDetails : existingLoanDetailsList) {
                if (existingLoanDetails.getLoanDtlsId() == null && existingLoanDetails.getExistingLoanId() == null) {// This
                    // data
                    // after its being inserted by using the back
                    // navigation
                    // within the session.
                    BigDecimal loanDtlId = CommonUtils.generateRandomNum();
                    existingLoanDetails.setLoanDtlsId(loanDtlId);
                    loanList.add(loanDtlId.toString());// to String is required to avoid rounding issue of Big
                    // Decimal at front end.

                    BigDecimal existingLoanId = CommonUtils.generateRandomNum();
                    existingLoanDetails.setExistingLoanId(existingLoanId);
                    existingLoanList.add(existingLoanId.toString());

                } else {
                    loanList.add(existingLoanDetails.getLoanDtlsId().toString());
                    existingLoanList.add(existingLoanDetails.getExistingLoanId().toString());
                }
                existingLoanDetails.setAppId(requestObj.getAppId());
                existingLoanDetails.setApplicationId(applicationID);
                existingLoanDetails.setCustDtlId(custDtlId);
                existingLoanDetails.setVersionNum(version);
                String payload = gson.toJson(existingLoanDetails.getPayload());
                existingLoanDetails.setPayloadColumn(payload);
                existingLoanDtlRepo.save(existingLoanDetails);
            }
            customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue
            // of Big Decimal at front end.
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setExistisingLoanDtlId(existingLoanList);
            customerIdentification.setLoanDtlIds(loanList);
            customerIdentification.setVersionNum(version);
        }
        logger.warn("Data inserted into TB_ABOB_ADDRESS_DETAILS for loans");
    }

    private void populateCustomerDtlsForDisabled(ApplyLoanRequestFields requestObj, String applicationID) {
        Gson gson = new Gson();
        String payload;

        List<CustomerDetails> customerDetailsList = requestObj.getCustomerDetailsList();
        List<CustomerDetails> customerDtl = custDtlRepo.findByApplicationId(applicationID);
        if (!customerDtl.isEmpty()) {
            for (CustomerDetails cust : customerDetailsList) {
                for (CustomerDetails custfind : customerDtl) {
                    if (cust.getCustDtlId().equals(custfind.getCustDtlId())) {
                        payload = gson.toJson(cust.getPayload());
                        custfind.setPayloadColumn(payload);
                        custDtlRepo.save(custfind);
                    }
                }
            }
        }
    }

    private void populateCustomerDtls(ApplyLoanRequestFields requestObj,
                                      CustomerIdentificationLoan customerIdentification, String applicationID, BigDecimal custDtlId,
                                      int version) {
        Gson gson = new Gson();
        ObjectMapper objectMapper = new ObjectMapper();
        boolean cbRetrigger = false;
        String payload;
        List<CustomerDetails> customerDetailsList = requestObj.getCustomerDetailsList();
        for (CustomerDetails customerDetails : customerDetailsList) {
            customerDetails.setApplicationId(applicationID);
            customerDetails.setAppId(requestObj.getAppId());
            customerDetails.setVersionNum(version);
            String customerName = customerDetails.getCustomerName();
            if (customerName == null) {
                customerName = "";
            }

            customerName = customerName.trim().replaceAll("\\s+", " ");

            int maxLength = Constants.MAX_FIRST_NAME_LENGTH;
            int splitIndex;

            if (customerName.length() <= maxLength) {
                splitIndex = customerName.length();
            } else {
                int lastSpaceIndex = customerName.lastIndexOf(' ', maxLength);

                if (lastSpaceIndex == -1) {
                    splitIndex = maxLength;
                } else {
                    splitIndex = lastSpaceIndex;
                }
            }

            String firstName = customerName.substring(0, splitIndex).trim();
            String lastName = customerName.substring(splitIndex).trim();

            customerDetails.getPayload().setFirstName(firstName);
            customerDetails.getPayload().setLastName(lastName);
            payload = gson.toJson(customerDetails.getPayload());
            logger.debug(
                    "customerDetails.getPayload().isRpcEditFlag() : " + customerDetails.getPayload().isRpcEditFlag());
            if (customerDetails.getPayload().isRpcEditFlag()) {
                cbRetrigger = true;
            }
            if (StringUtils.isBlank(customerDetails.getPayload().getPanNumber())) {
				Optional<List<ApplicationDocuments>> panDocOpt = appLoanDocsRepository
						.findByApplicationIdAndCustTypeAndDocType(customerDetails.getCustomerType(), applicationID,
								Constants.PAN_NUMBER_DOC_TYPE);
                if (panDocOpt.isPresent() && !panDocOpt.get().isEmpty()) {
                    appLoanDocsRepository.deleteAll(panDocOpt.get());
                }
            }
            customerDetails.setPayloadColumn(payload);
            customerDetails.setCustDtlId(custDtlId);
            customerDetails.setSeqNumber(requestObj.getApplicationMaster().getCustDtlSlNum());
            custDtlRepo.save(customerDetails);
        }
        try {
            if (cbRetrigger) {
                List<CibilDetails> cibilDtlList = cibilDtlRepo.findByApplicationIdAndAppId(applicationID,
                        Constants.APPID);
                for (CibilDetails cibilDtl : cibilDtlList) {
                    CibilDetailsPayload cibilDetailsPayload = objectMapper.readValue(cibilDtl.getPayloadColumn(),
                            CibilDetailsPayload.class);
                    cibilDetailsPayload.setCbRetrigger(cbRetrigger);
                    cibilDtl.setPayloadColumn(gson.toJson(cibilDetailsPayload));
                    cibilDtlRepo.save(cibilDtl);
                }
            }
        } catch (Exception e) {
            logger.error("Exception while updating CIBIL details", e);
        }
        customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue of
        // Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_CUSTOMER_DETAILS for loans");
    }

	private BigDecimal populateLeadCustomerDtls(UnnatiCoApplicantDetails coAppDetails,
			ApplyLoanRequestFields requestObj, String applicationID, int version) {
        logger.debug("inside populateRenewalCustomerDtls  ");
        Gson gson = new Gson();
        String payload;
        CustomerDetails customerDetail = new CustomerDetails();
        BigDecimal coAppCustDtlId = CommonUtils.generateRandomNum();
        customerDetail.setCustDtlId(coAppCustDtlId);
        customerDetail.setAppId(requestObj.getAppId());
        customerDetail.setApplicationId(applicationID);
        customerDetail.setVersionNum(version);
        customerDetail.setCustomerType(Constants.COAPPLICANT);
		customerDetail.setCustomerName(getDefaultValueIfObjNull(coAppDetails.getCoFullName()));
		customerDetail.setMobileNumber(getDefaultValueIfObjNull(coAppDetails.getCoMobileNo()));
        customerDetail.setSeqNumber(2);
        customerDetail.setKycStatus("Pending");
        customerDetail.setAmlStatus("Pending");
        logger.debug("inside populateRenewalCustomerDtls customerDetail 1 " + customerDetail.toString());
        CustomerDetailsPayload payloadObj = new CustomerDetailsPayload();

		payloadObj.setTitle(getDefaultValueIfObjNull(coAppDetails.getCoTitle()));
		payloadObj.setDob(getDefaultValueIfObjNull(coAppDetails.getCoDateOfBirth()));
		payloadObj.setAge(getDefaultValueIfObjNull(coAppDetails.getCoAge()));
		payloadObj.setGender(getDefaultValueIfObjNull(coAppDetails.getCoGender()));
		payloadObj.setMaritalStatus(getDefaultValueIfObjNull(coAppDetails.getCoMaritalStatus()));
        // payloadObj.setPan(getDefaultValueIfObjNull(renewalLeadDetail.getCopan()));
		payloadObj.setSpouseName(getDefaultValueIfObjNull(coAppDetails.getCoSpouseName()));
		payloadObj.setFathersName(getDefaultValueIfObjNull(coAppDetails.getCoFatherName()));
        // payloadObj.setAadhaarNumber(getDefaultValueIfObjNull(renewalLeadDetail.getCoA));
		payloadObj.setOccupation(getDefaultValueIfObjNull(coAppDetails.getCoOccupation()));
        payloadObj.setCustId("");
		payloadObj.setFirstName(getDefaultValueIfObjNull(coAppDetails.getCoFirstname()));
		payloadObj.setMiddleName(getDefaultValueIfObjNull(coAppDetails.getCoMiddlename()));
		payloadObj.setLastName(getDefaultValueIfObjNull(coAppDetails.getCoLastname()));
		payloadObj
				.setRelationShipWithApplicant(getDefaultValueIfObjNull(coAppDetails.getCoRelationshipwithapplicant()));
		payloadObj.setNamePerKyc(getDefaultValueIfObjNull(coAppDetails.getCoNameperkyc()));
		payloadObj.setReligion(getDefaultValueIfObjNull(coAppDetails.getCoReligion()));
		payloadObj.setCaste(getDefaultValueIfObjNull(coAppDetails.getCoCaste()));
        payloadObj.setPrimaryKycType(getDefaultValueIfObjNull(Constants.VOTER_ID));
		payloadObj.setPrimaryKycId(getDefaultValueIfObjNull(coAppDetails.getCoVoterIdNo()));
        payloadObj.setPrimaryKycIdValStatus("");
        payloadObj.setSecondaryKycType("");
        payloadObj.setSecondaryKycId(getDefaultValueIfObjNull(""));
        payloadObj.setSecondaryKycIdValStatus("");
        payloadObj.setGkCustomerType("Co-Applicant");
		payloadObj.setEducation(getDefaultValueIfObjNull(coAppDetails.getCoEducation()));
        payloadObj.setAlternateVoterId("");
        payloadObj.setAlternateVoterIdValStatus("");
        payloadObj.setCustomerIndex(2);

        payload = gson.toJson(payloadObj);
        logger.debug("inside populateRenewalCustomerDtls payload " + payload.toString());
        customerDetail.setPayloadColumn(payload);
        logger.debug("inside populateRenewalCustomerDtls customerDetail 2 " + customerDetail.toString());
        custDtlRepo.save(customerDetail);
        // customerIdentification.setApplicationId(applicationID);
        // customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_CUSTOMER_DETAILS for Renewal Applicant");

        return coAppCustDtlId;
    }

    private void updateConfirmFlagInMaster(ApplyLoanRequestFields requestObj, int version, String applicationID,
                                           CustomerIdentificationLoan customerIdentification, Properties prop, boolean isSelfOnBoardingAppId,
                                           boolean isSelfOnBoardingHeaderAppId) {
        ApplicationMaster masterRequest = requestObj.getApplicationMaster();
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepo
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(),
                        requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            WorkFlowDetails wfObj = requestObj.getWorkflow();
            PopulateapplnWFRequest apiRequest = new PopulateapplnWFRequest();
            PopulateapplnWFRequestFields requestObjWf = new PopulateapplnWFRequestFields();
            requestObjWf.setAppId(requestObj.getAppId());
            requestObjWf.setApplicationId(applicationID);
            requestObjWf.setVersionNum(version);
            requestObjWf.setWorkflow(wfObj);
            apiRequest.setRequestObj(requestObjWf);
            if (isSelfOnBoardingAppId) { // self onboarding
                updateConfirmFlagInMasterForSob(apiRequest, requestObjWf, isSelfOnBoardingHeaderAppId, prop,
                        masterRequest);
            } else { // assisted on boarding
                requestObjWf.setApplicationStatus(AppStatus.PENDING.getValue());
                requestObjWf.setCreatedBy(masterRequest.getCreatedBy());
                String roleId = commonParamService.fetchRoleId(requestObj.getAppId(), masterRequest.getCreatedBy());
                if (wfObj != null) {
                    if (wfObj.getCurrentRole().equalsIgnoreCase(roleId)) { // VAPT
                        commonParamService.populateApplnWorkFlow(apiRequest);
                    } else {
                        logger.error("VAPT issue in updateAppMaster. Current role id from request is tampered.");
                    }
                }
            }
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
        }
    }

    private void updateConfirmFlagInMasterForSob(PopulateapplnWFRequest apiRequest,
                                                 PopulateapplnWFRequestFields requestObjWf, boolean isSelfOnBoardingHeaderAppId, Properties prop,
                                                 ApplicationMaster masterRequest) {
        if ("N".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.LOAN_STP.getKey()))) {
            if (!isSelfOnBoardingHeaderAppId) { // INITIATOR submits it after review.
                requestObjWf.setApplicationStatus(AppStatus.PENDING.getValue());
                requestObjWf.setCreatedBy(masterRequest.getCreatedBy());
            } else {
                requestObjWf.setCreatedBy("Customer");
                requestObjWf.setApplicationStatus(AppStatus.INPROGRESS.getValue());
            }
            commonParamService.populateApplnWorkFlow(apiRequest);
        }

    }

    private void updateDeclarationFlagInMaster(ApplyLoanRequestFields requestObj, int version, String applicationID,
                                               CustomerIdentificationLoan customerIdentification) {
        ApplicationMaster masterRequest = requestObj.getApplicationMaster();
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepo
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(),
                        requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
//			masterObj.setDeclarationFlag(masterRequest.getDeclarationFlag());
            applicationMasterRepo.save(masterObj);
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
        }
    }

    private void populateApplicationDocs(ApplyLoanRequestFields requestObj,
                                         CustomerIdentificationLoan customerIdentification, String applicationID, int version) {
        logger.debug("Inside populateApplicationDocs");
        Gson gson = new Gson();
        List<String> documentList = new ArrayList<>();
        List<ApplicationDocumentsWrapper> applicationDocumentsWrapperList = requestObj
                .getApplicationDocumentsWrapperList();

        List<CustomerDetails> customerDetailsWrapperList = requestObj.getCustomerDetailsList();

        // Changed date - 24/02/2025
        boolean isCustDtl = false;

        try {
            List<CustomerDetails> custDetails = custDtlRepo.findByApplicationIdAndAppId(applicationID,
                    requestObj.getAppId());
            logger.debug("custDetails size" + custDetails.size());
            logger.debug("custDetails" + custDetails);
            if (custDetails.size() > 0) {
                for (CustomerDetails customerDetails : customerDetailsWrapperList) {
                    logger.debug("Size of customerDetailsWrapperList" + customerDetailsWrapperList.size());
                    for (CustomerDetails customerDtls : custDetails) {
                        logger.debug("Size of custDetails" + custDetails.size());
                        logger.debug("custDtlId from request" + customerDetails.getCustDtlId());
                        if (customerDetails.getCustDtlId().equals(customerDtls.getCustDtlId())) {
                            isCustDtl = true;
                            logger.debug("Success case of custdtlID");
                            break;
                        }
                    }
                    if (isCustDtl)
                        break;
                }
            } else {
                isCustDtl = true;
                logger.debug("Cusrtdtl value " + isCustDtl);

            }
        } catch (Exception e) {
            logger.error("Error fetching customer details: " + e.getMessage());
        }
        logger.debug("Cusrtdtl value 1 " + isCustDtl);
        if (isCustDtl) {
            logger.debug("Customer details matched.");
            for (ApplicationDocumentsWrapper applicationDocumentsWrapper : applicationDocumentsWrapperList) {
                List<ApplicationDocuments> applicationDocumentsList = applicationDocumentsWrapper
                        .getApplicationDocumentsList();
                for (ApplicationDocuments applicationDocuments : applicationDocumentsList) {
                    if (applicationDocuments.getAppDocId() == null) {// This is to handle the case if user changed the
                        // data
                        // after its being inserted by using the back
                        // navigation within the session.
                        BigDecimal appDocId = CommonUtils.generateRandomNum();
                        applicationDocuments.setAppDocId(appDocId);
                        documentList.add(appDocId.toString());// to String is required to avoid rounding issue of Big
                        // Decimal at front end.
                        logger.debug("Generated new AppDocId: " + appDocId);
                    }
                    applicationDocuments.setApplicationId(applicationID);
                    applicationDocuments.setVersionNum(version);
                    applicationDocuments.setAppId(requestObj.getAppId());
                    String payload = gson.toJson(applicationDocuments.getPayload());
                    applicationDocuments.setPayloadColumn(payload);
                    applicationDocuments.setStatus(AppStatus.ACTIVE_STATUS.getValue());
                    logger.debug("Saving application document with AppDocId:" + applicationDocuments.getAppDocId());
                    appLoanDocsRepository.save(applicationDocuments);
                }
            }
            customerIdentification.setDocumentList(documentList);
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
            logger.warn("Data inserted into TB_ABOB_APPLN_DOCUMENTS for CASA");
        }
        logger.debug("Customer Details not matched");
    }

    private void populateOrUpdateLoanDtls(ApplyLoanRequestFields requestObj, int version, String applicationID,
                                          CustomerIdentificationLoan customerIdentification, String src) {
        Gson gson = new Gson();
        String payload;
        LoanDetails loanDtlObj = new LoanDetails();
        LoanDetails loanDtlObjReq = requestObj.getLoanDetails();
        if (loanDtlObjReq != null) {

            Optional<LoanDetails> loanDetailsDb = loanDtlsRepo.findTopByApplicationIdAndAppId(applicationID,
                    Constants.APPID);
            if (loanDetailsDb.isPresent()) {
                loanDtlObj.setLoanDtlId(loanDetailsDb.get().getLoanDtlId());
                loanDtlObj.setCoapplicantId(loanDetailsDb.get().getCoapplicantId());
                loanDtlObj.setT24LoanId(loanDetailsDb.get().getT24LoanId());
                loanDtlObj.setLoanStatus(loanDetailsDb.get().getLoanStatus());
                loanDtlObj.setCoapplicantUpdateId(loanDetailsDb.get().getCoapplicantUpdateId());
                loanDtlObj.setLoanRepaymentSchedule(loanDetailsDb.get().getLoanRepaymentSchedule());
            } else {
                loanDtlObj.setLoanDtlId(CommonUtils.generateRandomNum());
            }
            loanDtlObj.setAppId(requestObj.getAppId());
            loanDtlObj.setApplicationId(applicationID);
            loanDtlObj.setVersionNum(version);
            if (loanDtlObj != null) {
                if (Constants.LOAN_DETAILS.equalsIgnoreCase(src)) {
                    loanDtlObj.setLoanAmount(loanDtlObjReq.getLoanAmount());
                    loanDtlObj.setTenureInMonths(loanDtlObjReq.getTenureInMonths());
                    loanDtlObj.setTenure(loanDtlObjReq.getTenure());
                    loanDtlObj.setRoi(loanDtlObjReq.getRoi());
                    loanDtlObj.setInterest(loanDtlObjReq.getInterest());
                    loanDtlObj.setLoanClosureDate(null);
                    loanDtlObj.setTotPayableAmount(loanDtlObjReq.getTotPayableAmount());
                    payload = gson.toJson(loanDtlObjReq.getPayload());
                    loanDtlObj.setPayloadColumn(payload);
                } else if (Constants.EMI_DETAILS.equalsIgnoreCase(src)) {
                    loanDtlObj.setAutoEmiAccount(loanDtlObjReq.getAutoEmiAccount());
                    loanDtlObj.setAutoEmiAccountType(loanDtlObjReq.getAutoEmiAccountType());
                    loanDtlObj.setEmiDate(loanDtlObjReq.getEmiDate());
                    loanDtlObj.setMonthlyEmi(loanDtlObjReq.getMonthlyEmi());
                } else if (Constants.LOAN_CR_DETAILS.equalsIgnoreCase(src)) {
                    loanDtlObj.setLoanCrAccount(loanDtlObjReq.getLoanCrAccount());
                    loanDtlObj.setLoanCrAccountType(loanDtlObjReq.getLoanCrAccountType());
                }
                customerIdentification.setLoanDtlId(loanDtlObj.getLoanDtlId().toString());
                customerIdentification.setVersionNum(version);
                customerIdentification.setApplicationId(applicationID);
                loanDtlsRepo.save(loanDtlObj);
            }
        }
    }

    private void updateCustomerDtlInMaster(ApplyLoanRequestFields requestObj, int version, String applicationID,
                                           CustomerIdentificationLoan customerIdentification) {
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepo
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(),
                        requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterRequest = requestObj.getApplicationMaster();
            ApplicationMaster masterObj = masterObjDb.get();
            if (!(CommonUtils.isNullOrEmpty(masterRequest.getCreatedBy()))) {
                masterObj.setCreatedBy(masterRequest.getCreatedBy());
            }
            if (!(CommonUtils.isNullOrEmpty(masterRequest.getMobileNumber()))) {
                masterObj.setMobileNumber(masterRequest.getMobileNumber());
            }
            if (!(CommonUtils.isNullOrEmpty(masterRequest.getEmailId()))) {
                masterObj.setEmailId(masterRequest.getEmailId());
            }
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
            applicationMasterRepo.save(masterObj);
        }
    }

    private void updateCustIdAndBranchInMaster(ApplyLoanRequestFields requestObj, int version) {
        ApplicationMaster masterRequest = requestObj.getApplicationMaster();
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepo
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(),
                        requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
            masterObj.setCustomerId(masterRequest.getCustomerId());
            masterObj.setSearchCode1(masterRequest.getSearchCode1());
            applicationMasterRepo.save(masterObj);
        }
    }

    private void populateAppMasterAndApplnwf(ApplyLoanRequestFields requestObj, String applicationID, int version,
                                             CustomerIdentificationLoan customerIdentification, boolean isSelfOnBoardingHeaderAppId, Properties prop) {
        ApplicationMaster appMasterReq = requestObj.getApplicationMaster();
        ApplicationMaster appMaster = new ApplicationMaster();

		logger.debug("populateAppMasterAndApplnwf Existing Flow");
        appMaster.setAppId(requestObj.getAppId());
        appMaster.setApplicationDate(LocalDate.now());
        appMaster.setApplicationId(applicationID);
        appMaster.setApplicationStatus(AppStatus.INPROGRESS.getValue());
        appMaster.setCreatedBy(appMasterReq.getCreatedBy());
        appMaster.setUpdatedBy(appMasterReq.getCreatedBy());
        appMaster.setApplicantsCount(appMasterReq.getApplicantsCount());
        appMaster.setEmailId(appMasterReq.getEmailId());
        appMaster.setMobileNumber(appMasterReq.getMobileNumber());
        appMaster.setProductCode(appMasterReq.getProductCode());
        appMaster.setProductGroupCode(appMasterReq.getProductGroupCode());
        appMaster.setVersionNum(version);
        appMaster.setCurrentScreenId(appMasterReq.getCurrentScreenId().split("~")[0]);
        appMaster.setCustomerId(appMasterReq.getCustomerId());
        appMaster.setSearchCode1(appMasterReq.getSearchCode1());
        appMaster.setSearchCode2(appMasterReq.getSearchCode2());
		String sourceOfApplication = appMasterReq.getSourceOfApplication();
		if(("UNNATI").equalsIgnoreCase(sourceOfApplication)){
			sourceOfApplication = "IEXCEED";
		}
		appMaster.setSourceOfApplication(sourceOfApplication);
        if (!(CommonUtils.isNullOrEmpty(appMasterReq.getMobileNumber()))) {
            appMaster.setMobileVerStatus("Y");
        }
        if (!(CommonUtils.isNullOrEmpty(appMasterReq.getEmailId()))) {
            appMaster.setEmailVerStatus("Y");
        }
		if (!(CommonUtils.isNullOrEmpty(appMasterReq.getAssignedTo()))) {
			logger.debug("Assigned TO value: {}", appMasterReq.getAssignedTo());
			appMaster.setAssignedTo("Y");

		}
        appMaster.setKendraId(appMasterReq.getKendraId());
        appMaster.setKendraName(appMasterReq.getKendraName());
        appMaster.setBranchId(appMasterReq.getBranchId());
        appMaster.setBranchName(appMasterReq.getBranchName());
        appMaster.setMemberId(appMasterReq.getMemberId());
        appMaster.setPrimaryKycType(appMasterReq.getPrimaryKycType());
        appMaster.setPrimaryKycId(appMasterReq.getPrimaryKycId());
        appMaster.setSecondaryKycType(appMasterReq.getSecondaryKycType());
        appMaster.setSecondaryKycId(appMasterReq.getSecondaryKycId());
        appMaster.setWorkitemNo(appMasterReq.getWorkitemNo());
        appMaster.setCurrentStageNo(appMasterReq.getCurrentStageNo());
        appMaster.setAlternateVoterId(appMasterReq.getAlternateVoterId());

		boolean isAnyBranchWhitelisted = whitelistedBranchesRepository
				.isAnyBranchWhitelisted(Arrays.asList(appMasterReq.getBranchId()));
        if (isAnyBranchWhitelisted) {
            appMaster.setDeclarationFlag(Constants.IEXCEED_FLAG);
        }

		if (Constants.RENEWAL_PRODUCT_CODE.equals(appMasterReq.getProductCode())) {
                appMaster.setRelatedApplicationId(Constants.NEWGEN_RENEWAL_LOAN);
            }

        logger.debug("appMaster : " + appMaster.toString());
        customerIdentification.setRelatedApplicationId(appMaster.getRelatedApplicationId());
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        applicationMasterRepo.save(appMaster);
        if (!isSelfOnBoardingHeaderAppId
                || ("N".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.LOAN_STP.getKey())))) {
            WorkFlowDetails wfObj = requestObj.getWorkflow();
            PopulateapplnWFRequest apiRequest = new PopulateapplnWFRequest();
            PopulateapplnWFRequestFields requestObjWf = new PopulateapplnWFRequestFields();
            requestObjWf.setAppId(requestObj.getAppId());
            requestObjWf.setApplicationId(applicationID);
            requestObjWf.setApplicationStatus(AppStatus.INPROGRESS.getValue());
            if (!isSelfOnBoardingHeaderAppId) {
                requestObjWf.setCreatedBy(appMasterReq.getCreatedBy());
            } else {
                requestObjWf.setCreatedBy(Constants.CUSTOMER);
            }
            requestObjWf.setVersionNum(version);
            requestObjWf.setWorkflow(wfObj);
            apiRequest.setRequestObj(requestObjWf);
            commonParamService.populateApplnWorkFlow(apiRequest);
            logger.warn("Data inserted into TB_ABOB_APPLN_WORKFLOW");
            Optional<ApplicationWorkflow> workflow = applnWfRepository
                    .findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(requestObj.getAppId(),
                            applicationID, version);
            if (workflow.isPresent()) {
                ApplicationWorkflow applnWf = workflow.get();
                List<WorkflowDefinition> wfDefnList = wfDefnLoanRepo.findByFromStageId(applnWf.getNextWorkFlowStage());
                customerIdentification.setApplnWfDefinitionList(wfDefnList);
            }
        }

    }

    public Mono<Object> fetchCustomerDetails(FetchCustDtlRequest request, Header header) {
        return interfaceAdapter.callExternalService(header, request, request.getInterfaceName());
    }

    public Mono<Response> checkApplication(CheckApplicationRequest request, Header header) throws IOException {
        Gson gson = new Gson();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        Properties prop = CommonUtils.readPropertyFile();
        if ("Y".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.EXT_SYSTEM_DEDUPE_REQUIRED.getKey()))) {
            // dedupe check hook.
            Mono<Object> extResponse = interfaceAdapter.callExternalService(header, request,
                    request.getInterfaceName());
            return extResponse.flatMap(val -> {
                Response response = new Response();
                ResponseWrapper res = adapterUtil.getResponseMapper(val, request.getInterfaceName(), header);
                if (ResponseParser.isExtCallSuccess(res.getApiResponse(), "checkApplication")) {
                    if (ResponseParser.isNewCustomer(res.getApiResponse())) {
                        response = checkApplication(request, responseHeader, prop, responseBody);
                    } else {
                        responseHeader.setResponseCode(ResponseCodes.APP_PRESENT_APPROVED_STATUS.getKey()); // IV109
                        JSONArray customerList = ResponseParser.getApplicationList(res.getApiResponse());
                        responseBody.setResponseObj(gson.toJson(customerList));
                        response.setResponseHeader(responseHeader);
                        response.setResponseBody(responseBody);
                    }
                } else {
                    // custom code to handle failure of external API.
                    responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                    response.setResponseHeader(responseHeader);
                }
                return Mono.just(response);
            });
        } else if ("N".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.EXT_SYSTEM_DEDUPE_REQUIRED.getKey()))) {
            Response response = checkApplication(request, responseHeader, prop, responseBody);
            return Mono.just(response);
        } else {
            return Mono.empty();
        }
    }

    public Response checkApplication(CheckApplicationRequest request, ResponseHeader responseHeader, Properties prop,
                                     ResponseBody responseBody) {
        Gson gson = new Gson();
        Response response = new Response();
        CheckApplicationRes resElements = new CheckApplicationRes();
        List<String> inprogress = new ArrayList<>();
        String mobileNum = null;
        String emailId = null;
        String nationalId = null;
        String pan = null;
        String productGroupCode = null;
        String customerId = null;
        String res = "";
        CheckAppRequestFields requestFields = request.getRequestObj();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        List<String> statusList = new ArrayList<>();
        statusList.add(AppStatus.INPROGRESS.getValue());
        statusList.add(AppStatus.APPROVED.getValue());
        if (!(CommonUtils.isNullOrEmpty(requestFields.getMobileNumber()))) {
            mobileNum = requestFields.getMobileNumber();
        }
        if (!(CommonUtils.isNullOrEmpty(requestFields.getNationalId()))) {
            nationalId = requestFields.getNationalId();
        }
        if (!(CommonUtils.isNullOrEmpty(requestFields.getEmailId()))) {
            emailId = requestFields.getEmailId();
        }
        if (!(CommonUtils.isNullOrEmpty(requestFields.getPan()))) {
            pan = requestFields.getPan();
        }
        if (!(CommonUtils.isNullOrEmpty(requestFields.getProductGroupCode()))) {
            productGroupCode = requestFields.getProductGroupCode();
        }
        if (!(CommonUtils.isNullOrEmpty(requestFields.getCustomerId()))) {
            customerId = requestFields.getCustomerId();
        }
        List<ApplicationMaster> appMasterObj = applicationMasterRepo.findData(requestFields.getAppId(), mobileNum,
                nationalId, pan, emailId, productGroupCode, statusList,
                customerId == null ? null : new BigDecimal(customerId));
        boolean iv108 = false;
        boolean iv115 = false;
        boolean iv109 = false;
        for (ApplicationMaster appMasterObjDb : appMasterObj) {
            String headerAppId = request.getAppId();
            JSONArray array;
            if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
                array = commonParamService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE,
                        CodeTypes.LOAN_ETB.getKey(), Constants.FUNCTIONSEQUENCE);
            } else {
                array = commonParamService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE,
                        CodeTypes.LOAN_BO_ETB.getKey(), Constants.FUNCTIONSEQUENCE);
            }
            String lastElementArr = ((String) array.get(array.length() - 1)).split("~")[0];
            String currentSrnId = appMasterObjDb.getCurrentScreenId();
            if (appMasterObjDb != null
                    && AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObjDb.getApplicationStatus())) {
                iv109 = true;
            } else if (appMasterObjDb != null
                    && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObjDb.getApplicationStatus())
                    && lastElementArr.equalsIgnoreCase(currentSrnId)) {
                res = appMasterObjDb.getApplicationId() + "~" + appMasterObjDb.getAppId() + "~"
                        + appMasterObjDb.getVersionNum() + "~" + appMasterObjDb.getApplicationStatus() + "~"
                        + appMasterObjDb.getRelatedApplicationId() + "~" + appMasterObjDb.getProductGroupCode() + "~"
                        + appMasterObjDb.getProductCode();
                inprogress.add(res);
                iv115 = true; // All stages are done but still in inprogress status so dont allow to proceed.
                // IV115
            } else {
                String allowPartialApplication = prop
                        .getProperty(CobFlagsProperties.ALLOW_PARTIAL_APPLICATION.getKey());
                if ("Y".equalsIgnoreCase(allowPartialApplication) && appMasterObjDb != null) {
                    res = appMasterObjDb.getApplicationId() + "~" + appMasterObjDb.getAppId() + "~"
                            + appMasterObjDb.getVersionNum() + "~" + appMasterObjDb.getApplicationStatus() + "~"
                            + appMasterObjDb.getRelatedApplicationId() + "~" + appMasterObjDb.getProductGroupCode()
                            + "~" + appMasterObjDb.getProductCode();
                    inprogress.add(res);
                    if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObjDb.getApplicationStatus())) {
                        iv108 = true;
                    }
                } else if ("N".equalsIgnoreCase(allowPartialApplication)) {
                    String deleteRule = prop.getProperty(CobFlagsProperties.LOANS_DELETE_RULE.getKey());
                    if (Constants.HARD_DELETE.equalsIgnoreCase(deleteRule) && appMasterObjDb != null) {
                        deleteApplication(appMasterObjDb.getApplicationId(), appMasterObjDb.getAppId());
                    } else if (Constants.MOVE_TO_HISTORY_TABLES.equalsIgnoreCase(deleteRule)) {
                        populateHistoryTables(appMasterObjDb.getApplicationId(), appMasterObjDb.getAppId());
                    } else if (Constants.UPDATE_STATUS.equalsIgnoreCase(deleteRule)) {
                        appMasterObjDb.setApplicationStatus(AppStatus.DELETED.getValue());
                        applicationMasterRepo.save(appMasterObjDb);
                    } else {
                        responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                    }
                }
            }
        }
        resElements.setInProgress(inprogress);
        responseBody.setResponseObj(gson.toJson(resElements));
        if (iv108 && !iv109 && !iv115) {
            responseHeader.setResponseCode(ResponseCodes.APP_PRESENT_INPROGRESS_STATUS.getKey()); // IV108
        } else if (!iv108 && iv109 && !iv115) {
            responseHeader.setResponseCode(ResponseCodes.APP_PRESENT_APPROVED_STATUS.getKey()); // IV109
        } else if (!iv108 && !iv109 && iv115) {
            responseHeader.setResponseCode(ResponseCodes.APP_PRESENT_INPROGRESS_LAST_STAGE.getKey()); // All stages are
            // done but
            // still in
            // inprogress
            // status so
            // dont allow to
            // proceed.
            // IV115
        } else if (iv108 && iv109 && !iv115) {
            responseHeader.setResponseCode(ResponseCodes.APP_PRESENT_INPROGRESS_STATUS.getKey()); // IV108
        } else if (!iv108 && iv109 && iv115) {
            responseHeader.setResponseCode(ResponseCodes.APP_PRESENT_INPROGRESS_LAST_STAGE.getKey()); // All stages are
            // done but
            // still in
            // inprogress
            // status so
            // dont allow to
            // proceed.
            // IV115
        }
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    private void populateHistoryTables(String applicationId, String appId) {
        List<ApplicationMaster> appMasterOpt = applicationMasterRepo.findByAppIdAndApplicationId(appId, applicationId);
        if (null != appMasterOpt && appMasterOpt.size() > 0) {
            for (ApplicationMaster appMaster : appMasterOpt) {
                ApplicationMasterHistory appMasterHistory = new ApplicationMasterHistory();
                BeanUtils.copyProperties(appMaster, appMasterHistory);
                applicationMasterHisRepo.save(appMasterHistory);
                applicationMasterRepo.deleteByApplicationIdAndAppId(applicationId, appId);
            }

            LoanHisDetails loanHisDtls;
            List<LoanDetails> loanList = loanDtlsRepo.findByApplicationIdAndAppId(applicationId, appId);
            for (LoanDetails loan : loanList) {
                loanHisDtls = new LoanHisDetails();
                BeanUtils.copyProperties(loan, loanHisDtls);
                loanDtlsHisRepo.save(loanHisDtls);
            }
            loanDtlsRepo.deleteByApplicationIdAndAppId(applicationId, appId);

            ApplicationDocumentsHistory documentHistory;
            List<ApplicationDocuments> documentList = appLoanDocsRepository.findByApplicationIdAndAppId(applicationId,
                    appId);
            for (ApplicationDocuments documentObj : documentList) {
                documentHistory = new ApplicationDocumentsHistory();
                BeanUtils.copyProperties(documentObj, documentHistory);
                appLoanDocsHisRepository.save(documentHistory);
            }
            appLoanDocsRepository.deleteByApplicationIdAndAppId(applicationId, appId);

            CustomerDetailsHistory custDtlHistory;
            List<CustomerDetails> custDtlList = custDtlRepo.findByApplicationIdAndAppId(applicationId, appId);
            for (CustomerDetails custdtlObj : custDtlList) {
                custDtlHistory = new CustomerDetailsHistory();
                BeanUtils.copyProperties(custdtlObj, custDtlHistory);
                custDtlHisRepo.save(custDtlHistory);
            }
            custDtlRepo.deleteByApplicationIdAndAppId(applicationId, appId);

            AddressDetailsHistory addresshistory;
            List<AddressDetails> addressList = addressDtlRepo.findByApplicationIdAndAppId(applicationId, appId);
            for (AddressDetails addressObj : addressList) {
                addresshistory = new AddressDetailsHistory();
                BeanUtils.copyProperties(addressObj, addresshistory);
                addressDtlHisRepo.save(addresshistory);
            }
            addressDtlRepo.deleteByApplicationIdAndAppId(applicationId, appId);

            OccupationDetailsHistory occupationHistory;
            List<OccupationDetails> occupationList = occupationDtlRepo.findByApplicationIdAndAppId(applicationId,
                    appId);
            for (OccupationDetails ocupationObj : occupationList) {
                occupationHistory = new OccupationDetailsHistory();
                BeanUtils.copyProperties(ocupationObj, occupationHistory);
                occupationDtlHisRepo.save(occupationHistory);
            }
            occupationDtlRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        }
    }

    private boolean populateHistoryTablesAndDiscardCoApplicant(DiscardCoApplicantRequestFields requestFields) {
        boolean flag = false;
        ObjectMapper objectMapper = new ObjectMapper();
        Gson gson = new Gson();
        try {
            String appId = requestFields.getAppId();
            String applicationId = requestFields.getApplicationId();
            BigDecimal custDtlId = requestFields.getCustDtlId();
            int versionNo = requestFields.getVersionNum();
            List<CustomerDetails> custDetails = custDtlRepo
                    .findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(applicationId, appId, versionNo, custDtlId);
            if (null != custDetails) {
                CustomerDetailsHistory custDtlHistory;
                for (CustomerDetails custdtlObj : custDetails) {
                    custDtlHistory = new CustomerDetailsHistory();
                    BeanUtils.copyProperties(custdtlObj, custDtlHistory);
                    CustomerDetailsPayload customerDetailsPayload = objectMapper
                            .readValue(custDtlHistory.getPayloadColumn(), CustomerDetailsPayload.class);
                    customerDetailsPayload.setRemarks(requestFields.getRemarks());
                    customerDetailsPayload.setReason(requestFields.getReason());
                    custDtlHistory.setPayloadColumn(gson.toJson(customerDetailsPayload));
                    custDtlHisRepo.save(custDtlHistory);
                }
                custDtlRepo.deleteByApplicationIdAndAppIdAndCustDtlId(applicationId, appId, custDtlId);

                AddressDetailsHistory addresshistory;
                List<AddressDetails> addressList = addressDtlRepo.findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(
                        applicationId, appId, versionNo, custDtlId);
                for (AddressDetails addressObj : addressList) {
                    addresshistory = new AddressDetailsHistory();
                    BeanUtils.copyProperties(addressObj, addresshistory);
                    addressDtlHisRepo.save(addresshistory);
                }
                addressDtlRepo.deleteByApplicationIdAndAppIdAndCustDtlId(applicationId, appId, custDtlId);

                OccupationDetailsHistory occupationHistory;
                List<OccupationDetails> occupationList = occupationDtlRepo
                        .findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(applicationId, appId, versionNo,
                                custDtlId);
                for (OccupationDetails ocupationObj : occupationList) {
                    occupationHistory = new OccupationDetailsHistory();
                    BeanUtils.copyProperties(ocupationObj, occupationHistory);
                    occupationDtlHisRepo.save(occupationHistory);
                }
                occupationDtlRepo.deleteByApplicationIdAndAppIdAndCustDtlId(applicationId, appId, custDtlId);

                InsuranceDetailsHistory insuranceHistory;
                Optional<InsuranceDetails> insuranceDetails = insuranceDtlRepo
                        .findByApplicationIdAndAppIdAndCustDtlId(applicationId, appId, custDtlId);
                if (insuranceDetails.isPresent()) {
                    InsuranceDetails insuranceObj = insuranceDetails.get();

                    insuranceHistory = new InsuranceDetailsHistory();
                    BeanUtils.copyProperties(insuranceObj, insuranceHistory);
                    insuranceDtlHisRepo.save(insuranceHistory);
                    insuranceDtlRepo.deleteByApplicationIdAndAppIdAndCustDtlId(applicationId, appId, custDtlId);
                }

                CibilDetailsHistory cibilHisDetails;
                Optional<CibilDetails> cibilDetails = cibilDtlRepo
                        .findByApplicationIdAndAppIdAndCustDtlId(applicationId, appId, custDtlId);
                if (cibilDetails.isPresent()) {
                    CibilDetails cibilObj = cibilDetails.get();

                    cibilHisDetails = new CibilDetailsHistory();
                    BeanUtils.copyProperties(cibilObj, cibilHisDetails);
                    cibilDtlHisRepo.save(cibilHisDetails);
                    cibilDtlRepo.deleteByApplicationIdAndAppIdAndCustDtlId(applicationId, appId, custDtlId);
                }

                ApplicationDocumentsHistory documentHistory;
                Optional<List<ApplicationDocuments>> documentList = appLoanDocsRepository
                        .findByApplicationIdAndCustDtlId(applicationId, custDtlId);

                if (documentList.isPresent()) {

                    List<ApplicationDocuments> docList = documentList.get();
                    for (ApplicationDocuments documentObj : docList) {
                        documentHistory = new ApplicationDocumentsHistory();
                        BeanUtils.copyProperties(documentObj, documentHistory);
                        appLoanDocsHisRepository.save(documentHistory);
                    }
                    appLoanDocsRepository.deleteByApplicationIdAndCustDtlId(applicationId, custDtlId);
                }

                List<ApplicationMaster> appDetails = applicationMasterRepo.findByAppIdAndApplicationId(appId,
                        applicationId);
                ApplicationMaster appMasterObj = appDetails.get(0);
                appMasterObj.setCurrentStageNo(4);
                appMasterObj.setCurrentScreenId(Constants.LOAN_DETAILS);
                applicationMasterRepo.save(appMasterObj);
                flag = true;
            }
        } catch (Exception e) {
            logger.error("Exception in discard co applicant " + e.getMessage());
        }
        return flag;
    }

    private void deleteApplication(String applicationId, String appId) {
        applicationMasterRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        loanDtlsRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        appLoanDocsRepository.deleteByApplicationIdAndAppId(applicationId, appId);
        custDtlRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        addressDtlRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        occupationDtlRepo.deleteByApplicationIdAndAppId(applicationId, appId);
    }

    public Response fetchApplication(FetchAppRequest request) {
        String applicationId = request.getRequestObj().getApplicationId();
        String appId = request.getRequestObj().getAppId();
        int versionNum = request.getRequestObj().getVersionNum();
        Gson gson = new Gson();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        response.setResponseHeader(responseHeader);
        ResponseBody responseBody = new ResponseBody();
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepo
                .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, applicationId);
        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            ApplyLoanRequestFields loanFields = getCustomerData(applicationMasterData, applicationId, appId,
                    versionNum);
            String customerdata = gson.toJson(loanFields);
            customerdata = customerdata.replace(Constants.PAYLOAD_COLUMN, Constants.PAYLOAD);
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
            responseBody.setResponseObj(customerdata);
            response.setResponseBody(responseBody);
            return response;
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            responseBody.setResponseObj(Constants.APP_MASTER_NOT_FOUND);
            response.setResponseBody(responseBody);
            return response;
        }
    }

    public boolean discardApplication(ApplyLoanRequest req) throws IOException {
        boolean flag = false;
        ApplyLoanRequestFields requestFields = req.getRequestObj();
        ApplicationMaster masterObj = requestFields.getApplicationMaster();
        String deleteRule;
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepo
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusAndCustomerId(requestFields.getAppId(),
                        requestFields.getApplicationId(), requestFields.getVersionNum(),
                        AppStatus.INPROGRESS.getValue(), masterObj.getCustomerId());
        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster masterObjDb = applicationMasterOpt.get();
            Properties prop = CommonUtils.readPropertyFile();
            deleteRule = prop.getProperty(CobFlagsProperties.LOANS_DELETE_RULE.getKey());
            if (Constants.HARD_DELETE.equalsIgnoreCase(deleteRule)) {
                deleteApplication(requestFields.getApplicationId(), requestFields.getAppId());
            } else if (Constants.MOVE_TO_HISTORY_TABLES.equalsIgnoreCase(deleteRule)) {
                populateHistoryTables(requestFields.getApplicationId(), requestFields.getAppId());
            } else if (Constants.UPDATE_STATUS.equalsIgnoreCase(deleteRule)) {
                masterObjDb.setApplicationStatus(AppStatus.DELETED.getValue());
                applicationMasterRepo.save(masterObjDb);
            } else {
                flag = false;
            }
            if (!CommonUtils.isNullOrEmpty(masterObjDb.getRelatedApplicationId())) { // discard the corresponding casa
                CustomerDataFields requestObj = new CustomerDataFields();
                CreateModifyUserRequest apiRequest = new CreateModifyUserRequest();
                requestObj.setApplicationId(masterObjDb.getRelatedApplicationId());
                requestObj.setAppId(requestFields.getAppId());
                requestObj.setVersionNum(requestFields.getVersionNum());
                apiRequest.setRequestObj(requestObj);
                flag = cobService.discardApplication(apiRequest);
            }
            flag = true;
        }
        return flag;
    }

    public boolean discardApplicant(DiscardCoApplicantRequest req) throws IOException {
        DiscardCoApplicantRequestFields requestFields = req.getRequestObj();
        return populateHistoryTablesAndDiscardCoApplicant(requestFields);
    }

    public Response downloadApplication(FetchAppRequest fetchAppReq) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        String applicationId = fetchAppReq.getRequestObj().getApplicationId();
        String appId = fetchAppReq.getRequestObj().getAppId();
        int versionNum = fetchAppReq.getRequestObj().getVersionNum();
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepo
                .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, applicationId);
        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            ApplyLoanRequestFields customerLoanDataFields = getCustomerData(applicationMasterData, applicationId, appId,
                    versionNum);
            try {
                response = report.genratePdfService(customerLoanDataFields);
            } catch (FileNotFoundException e) {
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                responseBody.setResponseObj(e.getMessage());
                response.setResponseHeader(responseHeader);
                response.setResponseBody(responseBody);
            }
        } else {
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            responseBody.setResponseObj(Constants.APP_MASTER_NOT_FOUND);
            response.setResponseHeader(responseHeader);
            response.setResponseBody(responseBody);
        }
        return response;
    }

    private ApplyLoanRequestFields getCustomerData(ApplicationMaster applicationMasterData, String applicationId,
                                                   String appId, int versionNum) {
        ApplyLoanRequestFields loanFields = new ApplyLoanRequestFields();
        loanFields.setAppId(applicationMasterData.getAppId());
        loanFields.setApplicationId(applicationMasterData.getApplicationId());
        loanFields.setApplicationMaster(applicationMasterData);
        loanFields.setVersionNum(applicationMasterData.getVersionNum());

        List<CustomerDetails> customerDetailsList = custDtlRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId,
                appId, versionNum);
        loanFields.setCustomerDetailsList(customerDetailsList);

        AddressDetailsWrapper addressDetailsWrapper = new AddressDetailsWrapper();
        List<AddressDetailsWrapper> addressDetailsWrapperList = new ArrayList<>();
        List<AddressDetails> addressDetailsList = addressDtlRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId,
                appId, versionNum);
        addressDetailsWrapper.setAddressDetailsList(addressDetailsList);
        addressDetailsWrapperList.add(addressDetailsWrapper);
        loanFields.setAddressDetailsWrapperList(addressDetailsWrapperList);

        List<OccupationDetailsWrapper> occupationDetailsWrapperList = new ArrayList<>();
        OccupationDetailsWrapper occupationDetailsWrapper;
        List<OccupationDetails> occupationDetailsList = occupationDtlRepo
                .findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        for (OccupationDetails occupationDetails : occupationDetailsList) {
            occupationDetailsWrapper = new OccupationDetailsWrapper();
            occupationDetailsWrapper.setOccupationDetails(occupationDetails);
            occupationDetailsWrapperList.add(occupationDetailsWrapper);
        }
        loanFields.setOccupationDetailsWrapperList(occupationDetailsWrapperList);

        // insuranceDetails
        List<InsuranceDetailsWrapper> insuranceDetailsWrapper = new ArrayList<>();
        Optional<List<InsuranceDetails>> insuranceDetails = insuranceDtlRepo
                .findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        if (insuranceDetails.isPresent() && !insuranceDetails.get().isEmpty()) {
            insuranceDetails.get().forEach(insurance -> {
                InsuranceDetailsWrapper wrapperDetails = InsuranceDetailsWrapper.builder().insuranceDetails(insurance)
                        .build();
                insuranceDetailsWrapper.add(wrapperDetails);
            });
            loanFields.setInsuranceDetailsWrapperList(insuranceDetailsWrapper);
        } else {
            loanFields.setInsuranceDetailsWrapperList(null);
        }
        // branchDetails
        List<BankDetailsWrapper> bankDetails = new ArrayList<>();
        Optional<List<BankDetails>> bankDetailsList = bankDtlRepo
                .findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        if (bankDetailsList.isPresent() && !bankDetailsList.get().isEmpty()) {
            bankDetailsList.get().forEach(bankDetail -> {
                BankDetailsWrapper detailsBankWrapper = BankDetailsWrapper.builder().bankDetails(bankDetail).build();
                bankDetails.add(detailsBankWrapper);
            });
            loanFields.setBankDetailsWrapperList(bankDetails);
        } else {
            loanFields.setBankDetailsWrapperList(null);
        }

        // CibilDetails
        List<CibilDetailsWrapper> cibilDetailsWrapper = new ArrayList<>();
        Optional<List<CibilDetails>> cibilDetails = cibilDtlRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId,
                appId, versionNum);
        if (cibilDetails.isPresent() && !cibilDetails.get().isEmpty()) {
            cibilDetails.get().forEach(cibilDetail -> {
                CibilDetailsWrapper detailsWrapper = CibilDetailsWrapper.builder().cibilDetails(cibilDetail).build();
                cibilDetailsWrapper.add(detailsWrapper);
            });
            loanFields.setCibilDetailsWrapperList(cibilDetailsWrapper);
        } else {
            loanFields.setBankDetailsWrapperList(null);
        }

        List<ExistingLoanDetailsWrapper> existingLoanDetailsWrapper = new ArrayList<>();
        Optional<List<ExistingLoanDetails>> existingLoandDetails = existingLoanDtlRepo
                .findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        if (existingLoandDetails.isPresent() && !existingLoandDetails.get().isEmpty()) {
            ExistingLoanDetailsWrapper existingWrapper = ExistingLoanDetailsWrapper.builder()
                    .existingLoanDetailsList(existingLoandDetails.get()).build();
            existingLoanDetailsWrapper.add(existingWrapper);
            loanFields.setExistingLoanDetailsWrapperList(existingLoanDetailsWrapper);
        } else {
            loanFields.setExistingLoanDetailsWrapperList(existingLoanDetailsWrapper);
        }

        LoanDetails loanDetails = loanDtlsRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId,
                versionNum);
        loanFields.setLoanDetails(loanDetails);

        ApplicationDocumentsWrapper applicationDocumentsWrapper = new ApplicationDocumentsWrapper();
        List<ApplicationDocumentsWrapper> applicationDocumentsWrapperList = new ArrayList<>();
        List<ApplicationDocuments> applicationDocumentsList = appLoanDocsRepository
                .findByApplicationIdAndAppIdAndVersionNumAndStatus(applicationId, appId, versionNum,
                        AppStatus.ACTIVE_STATUS.getValue());
        applicationDocumentsWrapper.setApplicationDocumentsList(applicationDocumentsList);
        applicationDocumentsWrapperList.add(applicationDocumentsWrapper);
        loanFields.setApplicationDocumentsWrapperList(applicationDocumentsWrapperList);

        Optional<ApplicationWorkflow> workflow = applnWfRepository
                .findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(appId, applicationId, versionNum);

        if (workflow.isPresent()) {
            ApplicationWorkflow applnWf = workflow.get();
            List<WorkflowDefinition> wfDefnLis = wfDefnRepoLn.findByFromStageId(applnWf.getNextWorkFlowStage());
            loanFields.setApplnWfDefinitionList(wfDefnLis);
        }

        loanFields.setApplicationTimelineDtl(
                commonParamService.getApplicationTimelineDtl(applicationMasterData.getApplicationId()));

        return loanFields;
    }

    public void updateRelatedApplnIdDetails(ApplyLoanRequest apiRequest, String appId) {
        ApplyLoanRequestFields requestObj = apiRequest.getRequestObj();
        Optional<ApplicationMaster> appMasterObj = applicationMasterRepo
                .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, requestObj.getApplicationId());
        if (appMasterObj.isPresent()) {
            ApplicationMaster appMasterObjDb = appMasterObj.get();
            String relatedApplnId = appMasterObjDb.getRelatedApplicationId();
            if (!CommonUtils.isNullOrEmpty(relatedApplnId)) {
                Optional<ApplicationMaster> appMasterObjRelated = applicationMasterRepo
                        .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, relatedApplnId);
                if (appMasterObj.isPresent()) {
                    ApplicationMaster appMasterObjDbRelated = appMasterObjRelated.get();
                    String[] arr = requestObj.getApplicationMaster().getCurrentScreenId().split("~");
                    String currenctSrcId = arr[0];
                    if ("Y".equalsIgnoreCase(arr[1])) {
                        appMasterObjDbRelated.setCurrentScreenId(currenctSrcId);
                        applicationMasterRepo.save(appMasterObjDbRelated);
                    }
                }
            }
        }
    }

    public void duplicateLoanTablesETB(String appId, String applicationId, int newVersionNum, int oldVersionNum) {
        Optional<ApplicationMaster> appMasterForVersionCheck = applicationMasterRepo
                .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, applicationId);
        if (appMasterForVersionCheck.isPresent()) {
            BigDecimal newCustDtlId;
            ApplicationMaster appMaster = appMasterForVersionCheck.get();
            commonParamService.duplicateMasterData(appMaster, newVersionNum);
            duplicateLoanData(applicationId, appId, oldVersionNum, newVersionNum);
            List<CustomerDetails> custList = custDtlRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId,
                    oldVersionNum);
            List<OccupationDetails> occupationDetailsList = occupationDtlRepo
                    .findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, oldVersionNum);
            for (CustomerDetails custObj : custList) {
                newCustDtlId = CommonUtils.generateRandomNum();
                commonParamService.duplicateCustomerData(custObj, newVersionNum, newCustDtlId);
                for (OccupationDetails occupationObj : occupationDetailsList) {
                    commonParamService.duplicateOccupationData(occupationObj, newVersionNum, newCustDtlId);
                }
            }
        }
    }

    private void duplicateLoanData(String applicationId, String appId, int oldVersionNum, int newVersionNum) {
        LoanDetails loanDetails = loanDtlsRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId,
                oldVersionNum);
        if (null != loanDetails) {
            LoanDetails loanDetailsNew = new LoanDetails();
            BeanUtils.copyProperties(loanDetails, loanDetailsNew);
            loanDetailsNew.setVersionNum(newVersionNum);
            loanDetailsNew.setLoanDtlId(CommonUtils.generateRandomNum());
            loanDtlsRepo.save(loanDetailsNew);
        }
    }

    public void duplicateLoanTablesNTB(String appId, String applicationId, int newVersionNum, int oldVersionNum) {
        Optional<ApplicationMaster> appMasterForVersionCheck = applicationMasterRepo
                .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, applicationId);
        if (appMasterForVersionCheck.isPresent()) {
            ApplicationMaster appMaster = appMasterForVersionCheck.get();
            commonParamService.duplicateMasterData(appMaster, newVersionNum);
            duplicateLoanData(applicationId, appId, oldVersionNum, newVersionNum);
        }
    }

    public JSONArray fetchFunctionSeqArray(boolean isSelfOnBoardingHeaderAppId) {
        JSONArray array = null;
        if (isSelfOnBoardingHeaderAppId) {
            array = commonParamService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE,
                    CodeTypes.LOAN_ETB.getKey(), Constants.FUNCTIONSEQUENCE);
        } else {
            array = commonParamService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE,
                    CodeTypes.LOAN_BO_ETB.getKey(), Constants.FUNCTIONSEQUENCE);
        }
        return array;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "validateKycFallback")
    public Mono<Object> validateKyc(ValidateKycRequest validateKycRequest, Header header, Properties prop) {
        String reqRefNo = CommonUtils.generateRandomNumStr();
        ValidateKycRequestExt validateKycRequestExt = new ValidateKycRequestExt();
        validateKycRequestExt.setAppId(validateKycRequest.getAppId());
        if (validateKycRequest.getRequestObj().getKycType().equals(Constants.VOTER)) {
            validateKycRequestExt
                    .setInterfaceName(prop.getProperty(CobFlagsProperties.LOAN_VALIDATE_VOTER_ID_INTF.getKey()));
            ValidateVoterIdRequestFields voterIdRequestFields = new ValidateVoterIdRequestFields();
            voterIdRequestFields.setReqRefNo(reqRefNo);
            voterIdRequestFields.setKycId(validateKycRequest.getRequestObj().getKycId());
            validateKycRequestExt.setRequestObj(voterIdRequestFields);
        } else if (validateKycRequest.getRequestObj().getKycType().equals(Constants.PAN)) {
            validateKycRequestExt
                    .setInterfaceName(prop.getProperty(CobFlagsProperties.LOAN_VALIDATE_PAN_INTF.getKey()));
            ValidatePanRequestFields panRequestFields = new ValidatePanRequestFields();
            panRequestFields.setReqRefNo(reqRefNo);
            panRequestFields.setKycId(validateKycRequest.getRequestObj().getKycId());
            validateKycRequestExt.setRequestObj(panRequestFields);
        } else if (validateKycRequest.getRequestObj().getKycType().equals(Constants.DRIVING_LICENSE)) {
            validateKycRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.LOAN_VALIDATE_DL_INTF.getKey()));
            ValidateDrivingLicenseRequestFields drivingLicenseRequestFields = new ValidateDrivingLicenseRequestFields();
            drivingLicenseRequestFields.setReqRefNo(reqRefNo);
            drivingLicenseRequestFields.setKycId(validateKycRequest.getRequestObj().getKycId());
            drivingLicenseRequestFields.setDob(validateKycRequest.getRequestObj().getDob());
            validateKycRequestExt.setRequestObj(drivingLicenseRequestFields);
        } else if (validateKycRequest.getRequestObj().getKycType().equals(Constants.PASSPORT)) {
            validateKycRequestExt
                    .setInterfaceName(prop.getProperty(CobFlagsProperties.LOAN_VALIDATE_PASSPORT_INTF.getKey()));
            ValidatePassportRequestFields passportRequestFields = new ValidatePassportRequestFields();
            passportRequestFields.setReqRefNo(reqRefNo);
            passportRequestFields.setKycId(validateKycRequest.getRequestObj().getKycId());
            passportRequestFields.setDob(validateKycRequest.getRequestObj().getDob());
            validateKycRequestExt.setRequestObj(passportRequestFields);
        }
        //saveLog
        return interfaceAdapter.callExternalService(header, validateKycRequestExt,
                validateKycRequestExt.getInterfaceName());
    }

	public String aadhaarDedupe(String aadhaarNumber, String aadhaarName, String aadhaarDob, BigDecimal customerId,
			String applicationId) {
		List<String> dedupeExcludedStatusList = new ArrayList<>();
		dedupeExcludedStatusList.add(AppStatus.REJECTED.getValue());
		dedupeExcludedStatusList.add(AppStatus.EXIT.getValue());
		dedupeExcludedStatusList.add(AppStatus.DISBURSED.getValue());
		dedupeExcludedStatusList.add(AppStatus.LUC.getValue());
		dedupeExcludedStatusList.add(AppStatus.PENDINGLUCVERIFICATION.getValue());
		dedupeExcludedStatusList.add(AppStatus.LUCVERIFIED.getValue());

		aadhaarNumber = aadhaarNumber.replaceAll("X", "");
		logger.debug("Aadhaar number: {}, Aadhaar Name: {}, Aadhaar DOB: {}, Customer ID: {}", aadhaarNumber,
				aadhaarName, aadhaarDob, customerId);
		List<CustomerDetails> custDtl = custDtlRepo.findByAadhaarNumberAndApplicationId(aadhaarNumber, aadhaarName,
				aadhaarDob, customerId, dedupeExcludedStatusList);
		if( custDtl != null && custDtl.size() > 0){
			//Checking if same application has the aadhaar details
			Optional<CustomerDetails> customerDetails = custDtl.stream()
					.filter(cust -> cust.getApplicationId().equalsIgnoreCase(applicationId)).findFirst();
			if(customerDetails.isPresent()){
				return "This Aadhaar number is already linked to applicant/co-applicant in the current application.";
			} else {
				String dedupeApplicationId = custDtl.get(0).getApplicationId();
				String custId = custDtl.get(0).getCustId();
				return "The Aadhaar number is already mapped to another customer.\n" + "  Application ID: "
						+ dedupeApplicationId + "  Customer ID: " + "/" + custId + ".";
			}
		} else {
			return "Success";
		}
	}

    @CircuitBreaker(name = "fallback", fallbackMethod = "kycDedupeFallback")
    public Mono<Object> kycDedupe(KycDedupeRequest kycDedupeRequest, Header header, Properties prop) {
        logger.debug("onEntry :: kycDedupe API Requsest: {} ", kycDedupeRequest.toString());
        String productCode;
        String productType = kycDedupeRequest.getRequestObj().getProductType();
        String customerIdLeadTable = kycDedupeRequest.getRequestObj().getCustomerId();
        String applicationIdRequest = kycDedupeRequest.getRequestObj().getApplicationId();
        String appId = kycDedupeRequest.getAppId();
        String kycType = kycDedupeRequest.getRequestObj().getKycType();
        String kycId = kycDedupeRequest.getRequestObj().getKycId();
		String kycName = kycDedupeRequest.getRequestObj().getCustomerName();
		String kycDob = kycDedupeRequest.getRequestObj().getDob();

		List<String> dedupeExcludedStatusList = new ArrayList<>();
		dedupeExcludedStatusList.add(AppStatus.REJECTED.getValue());
		dedupeExcludedStatusList.add(AppStatus.EXIT.getValue());
		dedupeExcludedStatusList.add(AppStatus.DISBURSED.getValue());
		dedupeExcludedStatusList.add(AppStatus.LUC.getValue());
		dedupeExcludedStatusList.add(AppStatus.PENDINGLUCVERIFICATION.getValue());
		dedupeExcludedStatusList.add(AppStatus.LUCVERIFIED.getValue());

        if (Constants.DASHBOARD_STATS_RENEWAL.equalsIgnoreCase(productType.trim())) {
			productCode = Constants.RENEWAL_PRODUCT_CODE;
        } else if (Constants.DASHBOARD_STATS_OPENMARKET.equalsIgnoreCase(productType.trim())) {
            productCode = Constants.OPENMARKET_LOAN_PRODUCT_CODE;
        } else {
            productCode = Constants.UNNATI_PRODUCT_CODE;
        }
        JSONObject apiResponse = null;
        KycDedupeRequestExt kycDedupeRequestExt = new KycDedupeRequestExt();
        kycDedupeRequestExt.setAppId(appId);
        int count = -1;
        Optional<CustomerDetails> custDtl = Optional.empty();
        int customerType = kycDedupeRequest.getRequestObj().getCustomerType();
        if (kycType.equalsIgnoreCase(Constants.VOTER)) {
            if (customerType == 1) {
                if (StringUtils.isNotEmpty(applicationIdRequest)) {
                    List<String> applicationIds = new ArrayList<>();
                    applicationIds.add(applicationIdRequest);
                    custDtl = custDtlRepo.findByPrimaryKycIdAndAlternateVoterIdAndApplicationIdNotIn(applicationIds,
							kycId, kycId, customerIdLeadTable, dedupeExcludedStatusList);
                } else {
					custDtl = custDtlRepo.findByPrimaryKycIdAndAlternateVoterId(kycId, kycId, customerIdLeadTable, dedupeExcludedStatusList);
                }
            } else if (customerType == 2) {
                List<String> applicationIds = new ArrayList<>();
                applicationIds.add(applicationIdRequest);
				custDtl = custDtlRepo.findByPrimaryKycIdAndAlternateVoterIdAndApplicationIdNotIn(applicationIds, kycId,
						kycId, customerIdLeadTable, dedupeExcludedStatusList);
            }
            if (!custDtl.isPresent()) {
                kycDedupeRequestExt
                        .setInterfaceName(prop.getProperty(CobFlagsProperties.LOAN_DEDUPE_VOTER_ID_INTF.getKey()));
                DedupeVoterIdRequestFields voterIdRequestFields = new DedupeVoterIdRequestFields();
                voterIdRequestFields.setGkv(prop.getProperty(CobFlagsProperties.LOAN_DEDUPE_VOTER_ID_GKV.getKey()));
                voterIdRequestFields
                        .setMethod(prop.getProperty(CobFlagsProperties.LOAN_DEDUPE_VOTER_ID_METHOD.getKey()));
                voterIdRequestFields.setId(prop.getProperty(CobFlagsProperties.LOAN_DEDUPE_VOTER_ID_METHOD.getKey()));
                DedupeVoterIdLegalDocument legalDocument = new DedupeVoterIdLegalDocument();
                legalDocument.setId(kycId);
                voterIdRequestFields.setLegalDocument(legalDocument);
                kycDedupeRequestExt.setRequestObj(voterIdRequestFields);
                Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, kycDedupeRequestExt,
                        kycDedupeRequestExt.getInterfaceName());
                return apiRespMono.flatMap(val -> {
                    JSONObject resp = null;
                    logger.debug("response from the API: {} ", val);
                    JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                    logger.debug("JSON response from the API: {} ", apiResp);
                    if (null != apiResp && apiResp.has("response")
                            && apiResp.getString("response").equalsIgnoreCase("Success")) {
                        JSONArray result = apiResp.getJSONArray("result");
                        if (customerType == 1) {
                            if (result.length() > 0 && result.getJSONObject(0).has("customer_id")) {
                                for (Object jsonObj : result) {
                                    JSONObject json = (JSONObject) jsonObj;
									if (json.getString("customer_id").equals(customerIdLeadTable)) {
                                        resp = adapterUtil.setSuccessResp(apiResp.toString());

                                        String customerId = json.getString("customer_id");
                                    } else {
                                        logger.debug("External Dedupe match found");
                                        resp = adapterUtil
                                                .setError("Entered Voter Id is already mapped with other customer - "
                                                        + result.getJSONObject(0).get("customer_id"), "1");
                                        break;
                                    }
                                }
                            } else {
                                resp = adapterUtil.setSuccessResp(apiResp.toString());
                            }
                        } else if (customerType == 2) {
                            String customerId = "";
                            ApplicationMaster applicationMaster = null;
							List<ApplicationMaster> applicationMasterList = applicationMasterRepo
									.findByAppIdAndApplicationId(appId, applicationIdRequest);
                            if (!applicationMasterList.isEmpty()) {
                                applicationMaster = applicationMasterList.get(0);
                            }
                            if (result.length() > 0 && result.getJSONObject(0).has("customer_id")) {
								String coCustId = coAppDetailsRepository.getCoAppCustId(customerIdLeadTable);
                                for (Object jsonObj : result) {
                                    JSONObject json = (JSONObject) jsonObj;
                                    String respCustomerId = json.getString("customer_id");
                                    String respRelation = json.getString("Relation");
                                    if (respCustomerId.equals(coCustId)
                                            || respCustomerId.equals(applicationIdRequest)) {
                                        resp = adapterUtil.setSuccessResp(apiResp.toString());
                                    } else if (respCustomerId.equalsIgnoreCase(applicationMaster.getSearchCode2())
                                            && Constants.MEMBER.equalsIgnoreCase(respRelation)) {
                                        logger.debug("External Dedupe match found but customer id is applicant id");
                                        return Mono.just(adapterUtil
                                                .setError("Entered Voter Id is already mapped with applicant Id - "
                                                        + result.getJSONObject(0).get("customer_id"), "1"));
                                    } else {
										return isDedupeCustomerIdValid(respCustomerId, appId, header, prop,
												applicationMaster.getSearchCode2()).flatMap(isValid -> {
                                            if (Boolean.TRUE.equals(isValid)) {
														logger.debug(
																"External Dedupe match found but customer id is valid for co-applicant");
                                                if (Constants.MEMBER.equalsIgnoreCase(respRelation)) {
															loanDtlsRepo.updateCoapplicantId(applicationIdRequest,
																	respCustomerId);
                                                }
														logger.debug(
																"Co-applicant id updated in loan details table for application id: {} with co-applicant id: {}",
																applicationIdRequest, respCustomerId);
														return Mono
																.just(adapterUtil.setSuccessResp(apiResp.toString()));
                                            } else {
														logger.debug(
																"External Dedupe match found but customer id is not valid for co-applicant");
														return Mono.just(adapterUtil.setError(
																"Entered Voter Id is already mapped with other customer - "
																		+ result.getJSONObject(0).get("customer_id"),
																"1"));
                                            }
                                        });
                                    }
                                }
                            } else {
                                resp = adapterUtil.setSuccessResp(apiResp.toString());
                            }
                        }
                    } else {
                        logger.error("error response from dedupe API. {}", apiResp);
                        resp = adapterUtil.setError("error response from dedupe API.", "2");
                        saveLog(applicationIdRequest, "VoterId Dedupe", kycDedupeRequestExt.toString(),
                                apiResp.toString(), ResponseCodes.FAILURE.getValue(), apiResp.toString(), null);
                    }
                    return Mono.just(resp);
                });
            } else {
                logger.debug("Internal Dedupe match found");
                String applicationId = custDtl.get().getApplicationId();
                String custId = custDtl.get().getCustId();
                return Mono
						.just(adapterUtil.setError("Entered Voter Id is already mapped with other application/customer "
								+ applicationId + "/" + custId + ".", "1"));
            }
        } else if (kycType.equalsIgnoreCase(Constants.MOBILE_NO)) {
            if (customerType == 1) {
				count = custDtlRepo.countByMobileNoAndSecMobileNo(kycId, kycId, customerIdLeadTable);
            } else if (customerType == 2) {
                List<String> applicationIds = new ArrayList<>();
                applicationIds.add(applicationIdRequest);
				count = custDtlRepo.countByMobileNoAndSecMobileNoAndApplicationIdNotIn(applicationIds, kycId, kycId,
                        customerIdLeadTable);
            }
            if (count == 0) {
                kycDedupeRequestExt
                        .setInterfaceName(prop.getProperty(CobFlagsProperties.LOAN_DEDUPE_MOBILE_NO_INTF.getKey()));
                DedupeMobileNoRequestFields mobileNoRequestFields = new DedupeMobileNoRequestFields();
                mobileNoRequestFields.setGkv(prop.getProperty(CobFlagsProperties.LOAN_DEDUPE_VOTER_ID_GKV.getKey()));
                mobileNoRequestFields.setId(prop.getProperty(CobFlagsProperties.LOAN_DEDUPE_VOTER_ID_METHOD.getKey()));
                mobileNoRequestFields.setPhonenumber(kycId);
                kycDedupeRequestExt.setRequestObj(mobileNoRequestFields);
                Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, kycDedupeRequestExt,
                        kycDedupeRequestExt.getInterfaceName());
                return apiRespMono.flatMap(val -> {
                    JSONObject resp = null;
                    JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                    if (null != apiResp && apiResp.has("response")
                            && apiResp.getString("response").equalsIgnoreCase("Success")) {
                        JSONArray result = apiResp.getJSONArray("result");
                        if (customerType == 1) {
                            if (result.length() > 0 && result.getJSONObject(0).has("customer_id")) {
                                for (Object jsonObj : result) {
                                    JSONObject json = (JSONObject) jsonObj;
									if (json.getString("customer_id").equals(customerIdLeadTable)) {
                                        resp = adapterUtil.setSuccessResp(apiResp.toString());
                                    } else {
                                        logger.debug("External Dedupe match found");
										resp = adapterUtil.setError(
												"Entered Mobile number is already mapped with other customer - "
														+ result.getJSONObject(0).get("customer_id"),
												"1");
                                        break;
                                    }
                                }
                            } else {
                                resp = adapterUtil.setSuccessResp(apiResp.toString());
                            }
                        } else if (customerType == 2) {
                            // Changed - 27-02-2024
                            String customerId = "";
                            String customerTypeStr = (customerType == 1) ? Constants.APPLICANT : Constants.COAPPLICANT;
                            ApplicationMaster applicationMaster = null;
							List<ApplicationMaster> applicationMasterList = applicationMasterRepo
									.findByAppIdAndApplicationId(appId, applicationIdRequest);
                            if (!applicationMasterList.isEmpty()) {
                                applicationMaster = applicationMasterList.get(0);
                            }
                            if (result.length() > 0 && result.getJSONObject(0).has("customer_id")) {
								String coCustId = coAppDetailsRepository.getCoAppCustId(customerIdLeadTable);
                                for (Object jsonObj : result) {
                                    JSONObject json = (JSONObject) jsonObj;
									if (StringUtils.isEmpty(coCustId) || json.getString("customer_id").equals(coCustId)
                                            || json.getString("customer_id").equals(applicationIdRequest)) {
                                        resp = adapterUtil.setSuccessResp(apiResp.toString());
                                    } else {
                                        logger.debug("External Dedupe match found");
                                        resp = adapterUtil.setError(
                                                "Entered Mobile number is already mapped with other customer - "
                                                        + result.getJSONObject(0).get("customer_id"),
                                                "1");
                                        break;
                                    }
                                }
                            } else {
                                resp = adapterUtil.setSuccessResp(apiResp.toString());
                            }
                        }
                    } else {
                        logger.error("error response from dedupe API. {}", apiResp);
                        resp = adapterUtil.setError("error response from dedupe API.", "2");
                        saveLog(applicationIdRequest, "MobileNo Dedupe ", kycDedupeRequestExt.toString(),
                                apiResp.toString(), ResponseCodes.FAILURE.getValue(), apiResp.toString(), null);
                    }
                    return Mono.just(resp);
                });
            } else {
                logger.debug("Internal Dedupe match found");
                return Mono
                        .just(adapterUtil.setError("Entered mobile no is already mapped with other application.", "1"));
            }

        } else if (kycType.equalsIgnoreCase(Constants.BANK_DETAILS)) {
            logger.debug("onEntry :: kycDedupe :: BANK_DETAILS");
            Optional<List<BankDetails>> bankDtlOpt = Optional.empty();
            BigDecimal custDtlId = null;

            logger.debug("Account No :" + kycId);
            if (customerType == 1) {
                String custType = Constants.APPLICANT;
                Optional<CustomerDetails> customerDetails = custDtlRepo
                        .findByApplicationIdAndAppIdAndCustomerType(applicationIdRequest, Constants.APPID, custType);

                if (customerDetails.isPresent()) {
                    custDtlId = customerDetails.get().getCustDtlId();
                    logger.debug("custDtlId : Applicant :: " + custDtlId);
                }

				bankDtlOpt = bankDtlRepo.findByCustIdAndAccountNoAndApplicationIdNotIn(applicationIdRequest, kycId,
						custDtlId, customerIdLeadTable);

            } else if (customerType == 2) {
                String custType = Constants.COAPPLICANT;
                Optional<CustomerDetails> customerDetails = custDtlRepo
                        .findByApplicationIdAndAppIdAndCustomerType(applicationIdRequest, Constants.APPID, custType);

                if (customerDetails.isPresent()) {
                    custDtlId = customerDetails.get().getCustDtlId();
                    logger.debug("custDtlId : Co-Applicant :: " + custDtlId);
                }

				bankDtlOpt = bankDtlRepo.findByCustIdAndAccountNoAndApplicationIdNotIn(applicationIdRequest, kycId,
						custDtlId, customerIdLeadTable);
            }

            List<BankDetails> bankDtlList = bankDtlOpt.orElse(Collections.emptyList());
            if (bankDtlList.isEmpty()) {
                logger.debug("bankDtl record not found!");
                kycDedupeRequestExt
                        .setInterfaceName(prop.getProperty(CobFlagsProperties.LOAN_DEDUPE_BANK_INTF.getKey()));
                DedupeBankRequestFields bankRequestFields = new DedupeBankRequestFields();
                bankRequestFields.setGkv(prop.getProperty(CobFlagsProperties.LOAN_DEDUPE_BANK_ID_GKV.getKey()));
                bankRequestFields.setMethod(prop.getProperty(CobFlagsProperties.LOAN_DEDUPE_BANK_ID_METHOD.getKey()));
                bankRequestFields.setId(prop.getProperty(CobFlagsProperties.LOAN_DEDUPE_BANK_ID_ID.getKey()));
                bankRequestFields.setTypesrch(prop.getProperty(CobFlagsProperties.LOAN_DEDUPE_BANK_SEARCH_ID.getKey()));

                DedupeVoterIdLegalDocument legalDocumentParam = new DedupeVoterIdLegalDocument();
                legalDocumentParam.setId(kycId);
                bankRequestFields.setParam(legalDocumentParam);
                kycDedupeRequestExt.setRequestObj(bankRequestFields);

                logger.debug("F: {} ", kycDedupeRequestExt.toString());

                Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, kycDedupeRequestExt,
                        kycDedupeRequestExt.getInterfaceName());
                return apiRespMono.flatMap(val -> {

                    logger.debug("bankDedupeResponse 2 from the API: {} ", val);
                    JSONObject resp = null;
                    JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                    logger.debug("JSON response 3 from the API: {} ", apiResp);

                    if (null != apiResp && apiResp.has("response")
                            && apiResp.getString("response").equalsIgnoreCase("Success")) {
                        JSONArray result = apiResp.getJSONArray("result");
                        if (customerType == 1) {
                            if (result.length() > 0 && result.getJSONObject(0).has("customer_id")) {
                                for (Object jsonObj : result) {
                                    JSONObject json = (JSONObject) jsonObj;
									if (json.getString("customer_id").equals(customerIdLeadTable)) {
                                        resp = adapterUtil.setSuccessResp(apiResp.toString());
                                    } else {
                                        logger.debug("External Dedupe match found");
										resp = adapterUtil.setError(
												"Entered bank account number is already mapped with other customer - "
														+ result.getJSONObject(0).get("customer_id"),
												"1");
                                        break;
                                    }
                                }
                            } else {
                                resp = adapterUtil.setSuccessResp(apiResp.toString());
                            }
                        } else if (customerType == 2) {
                            if (result.length() > 0 && result.getJSONObject(0).has("customer_id")) {
								String coCustId = coAppDetailsRepository.getCoAppCustId(customerIdLeadTable);
                                for (Object jsonObj : result) {
                                    JSONObject json = (JSONObject) jsonObj;
                                    if (StringUtils.isEmpty(coCustId)
                                            || json.getString("customer_id").equals(coCustId)
                                            || json.getString("customer_id").equals(applicationIdRequest)) {
                                        resp = adapterUtil.setSuccessResp(apiResp.toString());
                                    } else {
                                        logger.debug("External Dedupe match found");
                                        resp = adapterUtil.setError(
                                                "Entered bank account number is already mapped with other customer - "
                                                        + result.getJSONObject(0).get("customer_id"),
                                                "1");
                                        break;
                                    }
                                }
                            } else {
                                resp = adapterUtil.setSuccessResp(apiResp.toString());
                            }
                        }
					} else if (null != apiResp && apiResp.has("response")
							&& apiResp.getString("response").equalsIgnoreCase(Constants.NO_RECORDS_FOUND)) {
                        resp = adapterUtil.setSuccessResp(apiResp.toString());

                    } else {
                        logger.error("Error response from dedupe API. {}", apiResp);
						saveLog(applicationIdRequest, "Bank Dedupe", kycDedupeRequest.toString(), apiResp.toString(),
								ResponseCodes.FAILURE.getValue(), apiResp.toString(), "");
                        resp = adapterUtil.setError("Error response from dedupe API.", "2");
                    }
                    return Mono.just(resp);
                });
            } else {
                logger.debug("Internal Dedupe Match Found!");
                if (bankDtlList.size() > 1) {
                    logger.debug("Multiple Internal Dedupe Matches Found!");
//				        return Mono.just(adapterUtil.setError(
//				                "Multiple bank account matches found for given details.", "1"));
                }

                String mappedCustId = "";
                BankDetails matched = bankDtlList.get(0);
                logger.debug("Internal Dedupe match found for applicationId: {}", matched.getApplicationId());

                String applicationId = matched.getApplicationId();

                Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepo
                        .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(Constants.APPID, applicationId);
                if (applicationMasterOpt.isPresent()) {
                    ApplicationMaster applicationMasterData = applicationMasterOpt.get();
                    mappedCustId = applicationMasterData.getSearchCode2();
                }

				return Mono.just(adapterUtil
						.setError("Entered bank account Number is already mapped with other application/customer "
								+ applicationId + "/" + mappedCustId + ".", "1"));

            }

		} else if(kycType.equalsIgnoreCase(Constants.AADHAAR)){
			BigDecimal customerId = new BigDecimal(customerIdLeadTable);
			String aadhaarDedupeResponse = aadhaarDedupe(kycId, kycName, kycDob, customerId, applicationIdRequest);
			if(aadhaarDedupeResponse.equalsIgnoreCase("Success")){
				return Mono.just(adapterUtil.setSuccessResp(aadhaarDedupeResponse));
			} else{
				return Mono.just(adapterUtil.setError(aadhaarDedupeResponse, "1"));
			}
        } else {
            logger.error("Invalid option");
            return Mono.just(adapterUtil.setError("Invalid option.", "1"));
        }
    }

	private Mono<Boolean> isDedupeCustomerIdValid(String customerId, String appId, Header header, Properties prop,
			String applicantId) {
        logger.debug("Validating dedupe customer id: {} for appId: {}", customerId, appId);

        Mono<Object> customerLoanCheckApiResp = t24AndCDHService.customerLoanCheckApi(customerId, appId, header, prop);

        return customerLoanCheckApiResp.flatMap(resp -> {
            logger.debug("Response from customer Loan Check API: {}", resp);
            try {
                JsonNode root = mapper.readTree(resp.toString());

                JsonNode errorNode = root.path("error");
                if (!errorNode.isMissingNode() && !errorNode.isEmpty()) {
                    String code = errorNode.path("code").asText();
                    String type = errorNode.path("type").asText();
                    String message = errorNode.path("message").asText();
					logger.error("Loan API Error -> Code: {}, Type: {}, Message: {}", code, type, message);

					return Mono
							.error(new RuntimeException("Customer Loan check API failed: " + code + " - " + message));
                }

                JsonNode body = root.path("body");
                if (!body.isArray()) {
					return Mono.error(new RuntimeException("Customer Loan check API failed: Service request failed"));
                }
                if (body.isEmpty()) {
                    return Mono.just(true);
                }
                boolean matchFound = false;
                for (JsonNode loanNode : body) {
                    String loanId = loanNode.path("loanId").asText(null);
                    String applicantCustomerIdRes = loanNode.path("customerId").asText(null);
                    String productCode = loanNode.path("productCode").asText(null);

					logger.debug("Checking loan -> LoanId: {}, CustomerId: {}, ProductCode: {}", loanId,
							applicantCustomerIdRes, productCode);
                    if (applicantCustomerIdRes != null && applicantCustomerIdRes.equalsIgnoreCase(applicantId)) {
                        logger.debug("Found matching customerIds so valid");
                        matchFound = true;
                        break;
                    }
                }
                return Mono.just(matchFound);
            } catch (Exception e) {
                logger.error("Error while validating dedupe customer id: {}", e.getMessage());
				return Mono.error(new RuntimeException("Customer Loan check API failed: Service request failed"));
            }
        });
	}

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchIFSCFallback")
    public Mono<Object> fetchIFSC(FetchIFSCRequest fetchIFSCRequest, Header header, Properties prop) {
        try {
            FetchIFSCRequestExt fetchIFSCRequestExt = new FetchIFSCRequestExt();
            fetchIFSCRequestExt.setAppId(fetchIFSCRequest.getAppId());
            fetchIFSCRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.FETCH_IFSC_INTF.getKey()));
            IFSCFetchRequestFields fetchIFSCRequestFields = new IFSCFetchRequestFields();
            fetchIFSCRequestFields.setGkv(prop.getProperty(CobFlagsProperties.FETCH_IFSC_GKV.getKey()));
            fetchIFSCRequestFields.setMethod(prop.getProperty(CobFlagsProperties.FETCH_IFSC_METHOD.getKey()));
            fetchIFSCRequestFields.setId(prop.getProperty(CobFlagsProperties.FETCH_IFSC_ID.getKey()));
            fetchIFSCRequestFields.setIfsc(fetchIFSCRequest.getRequestObj().getIfsc());
            fetchIFSCRequestExt.setRequestObj(fetchIFSCRequestFields);
            logger.debug("request from the API: {} ", fetchIFSCRequestExt.toString());
            Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, fetchIFSCRequestExt,
                    fetchIFSCRequestExt.getInterfaceName());
            return apiRespMono.flatMap(val -> {
                JSONObject resp = null;
                logger.debug("response from the API: {} ", val);
                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("JSON response from the API: {} ", apiResp);
                if (null != apiResp && apiResp.has("response")
                        && apiResp.getString("response").equalsIgnoreCase("Success")) {
                    JSONArray result = apiResp.getJSONArray("result");
                    resp = adapterUtil.setSuccessResp(apiResp.toString());
                } else {
                    logger.error("error response from  fetch ifsc API. {}", apiResp);
                    resp = adapterUtil.setError("error response from fetch ifsc API.", "2");
                }
                return Mono.just(resp);
            });
        } catch (Exception e) {
            logger.error("Exception occurred: " + e.getMessage());
            return Mono.just(adapterUtil.setError("Exception response from fetch ifsc API", "1"));
        }
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchExistingLoanFallback")
    public Mono<Object> fetchExistingLoan(ExistingLoanRequest existingLoanRequest, Header header, Properties prop) {
        JSONObject soapApiResponse;
        String soapOutput = null;
        Gson gson = new Gson();
        try {
            List<ExistingGLLoanDetails> existingGLLoanDetails = existingGLLoanDetailsRepo
                    .findByCustomerId(existingLoanRequest.getRequestObj().getMemberId());
            logger.debug("existingGLLoanDetails : " + existingGLLoanDetails.toString());
            logger.debug("Table retrieval is successful");
            soapOutput = gson.toJson(existingGLLoanDetails);
            soapApiResponse = adapterUtil.setSuccessResp(soapOutput);

        } catch (Exception e) {
            logger.error("Error occurred while executing the soap api, error = " + e.getMessage());
            soapApiResponse = adapterUtil.setError("Table retrieval failed", "4");
        }
        logger.debug("logging the request and response in db");

        logger.error("End : callService");

        return Mono.just(soapApiResponse);
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "breCBCheckFallback")
	public Mono<Object> breCBCheck(BRECBRequest brecbRequest, Header header, Properties prop)
			throws JsonProcessingException {
        final String loanId;
        final String applicantType;
        final String userId;
        logger.debug("request from the BRECBCheck API: {} ", brecbRequest.toString());
        Gson gson = new Gson();
        BRECBCheckRequestExt CBCheckRequestExt = new BRECBCheckRequestExt();
        CBCheckRequestExt.setAppId(brecbRequest.getAppId());
        CBCheckRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.BRE_CB_CHECK_INTF.getKey()));

        BRECBRequestFields breRequest = brecbRequest.getRequestObj();
        BRECBInputRequest2 input = breRequest.getBreCBValuesRequestvalues1().getBreCBInputRequestinput1()
                .getBreCBValuesRequestvalues2().getBreCBInputRequestinput2();

        BREApplicant breApplicant = input.getApplicant();
        String appnId = breApplicant.getAppId();
        String branchId = breApplicant.getBranch();

        Optional<ApplicationMaster> appMasterOpt = applicationMasterRepository2
                .findApplicationProductCode(brecbRequest.getAppId(), appnId);
        if (!appMasterOpt.isPresent()) {
            logger.debug("AppMaster not present for the application_id : {}", appnId);
            return Mono.just(adapterUtil.setError("Application Master not present for the Application Id: " + appnId, "1"));
        }
        ApplicationMaster appMaster = appMasterOpt.get();
        String productCode = appMaster.getProductCode();
        logger.debug("Product code for the application is: {}", productCode);

		String familyWelfare = ProductCode.FAMILY_WELFARE.getCode(ProductCode.ProductType.UNNATI);
		String restart = ProductCode.UNNATI_RESTART.getCode(ProductCode.ProductType.UNNATI);
		String supplementary = ProductCode.UNNATI_SUPPLEMENTARY.getCode(ProductCode.ProductType.UNNATI);
		String emergency = ProductCode.UNNATI_EMERGENCY.getCode(ProductCode.ProductType.UNNATI);

		if (familyWelfare.equals(productCode)) {
			logger.debug("Mapping product_code from {} to {}", productCode,
					ProductCode.FAMILY_WELFARE.getCode(ProductCode.ProductType.CDH));

			input.getApplicant().setProduct_code(ProductCode.FAMILY_WELFARE.getCode(ProductCode.ProductType.CDH));

			logger.debug("Mapped product_code: {}", input.getApplicant().getProduct_code());

		} else if (restart.equals(productCode)) {
			logger.debug("Mapping product_code from {} to {}", productCode,
					ProductCode.UNNATI_RESTART.getCode(ProductCode.ProductType.CDH));

			input.getApplicant().setProduct_code(ProductCode.UNNATI_RESTART.getCode(ProductCode.ProductType.CDH));

			logger.debug("Mapped product_code: {}", input.getApplicant().getProduct_code());

		} else if (supplementary.equals(productCode)) {
			logger.debug("Mapping product_code from {} to {}", productCode,
					ProductCode.UNNATI_SUPPLEMENTARY.getCode(ProductCode.ProductType.CDH));

			input.getApplicant().setProduct_code(ProductCode.UNNATI_SUPPLEMENTARY.getCode(ProductCode.ProductType.CDH));

			logger.debug("Mapped product_code: {}", input.getApplicant().getProduct_code());

		} else if (emergency.equals(productCode)) {
			logger.debug("Mapping product_code from {} to {}", productCode,
					ProductCode.UNNATI_EMERGENCY.getCode(ProductCode.ProductType.CDH));

			input.getApplicant().setProduct_code(ProductCode.UNNATI_EMERGENCY.getCode(ProductCode.ProductType.CDH));

			logger.debug("Mapped product_code: {}", input.getApplicant().getProduct_code());
		}else if(ProductCode.VISHESH.getUnnatiCode().equalsIgnoreCase(productCode)) {
			logger.debug("Mapping product_code from {} to {}", productCode,
					ProductCode.VISHESH.getCode(ProductCode.ProductType.CDH));

			input.getApplicant().setProduct_code(Constants.UNNATI_LITE_PRODUCT_CODE);

			logger.debug("Mapped product_code: {}", input.getApplicant().getProduct_code());

		}

		boolean isRestart = ProductCode.UNNATI_RESTART.getCode(ProductCode.ProductType.UNNATI).equals(productCode)
				|| ProductCode.UNNATI_RESTART.getCode(ProductCode.ProductType.CDH).equals(productCode);
		logger.debug("Implemented additional fields for UNNATI_RESTART and keeping them empty for others", productCode);
		// --------END--------
		if (StringUtils.isNotBlank(productCode) && productCode.equalsIgnoreCase(Constants.RENEWAL_PRODUCT_CODE)) {
            logger.debug("Renewal loan product code found, setting Unnati Renewal Flag to Y");
            breApplicant.setUnnatiRenewalFlag("Y");
        } else {
            breApplicant.setUnnatiRenewalFlag("N");
        }
        String loanAmount = breApplicant.getLoanAmount();

		boolean invalidLoanAmount = StringUtils.isBlank(loanAmount) || "NaN".equalsIgnoreCase(loanAmount)
                || !NumberUtils.isCreatable(loanAmount);
        if (invalidLoanAmount) {
            LoanDetails loanDetails = loanDtlsRepo.findByApplicationId(breApplicant.getAppId());
            if (loanDetails != null) {
                BigDecimal appliedLoanAmount = loanDetails.getLoanAmount();
                BigDecimal bmRecommendedLoanAmount = loanDetails.getBmRecommendedLoanAmount();
                BigDecimal sanctionedLoanAmount = loanDetails.getSanctionedLoanAmount();
                BigDecimal minAmount = Stream.of(appliedLoanAmount, bmRecommendedLoanAmount, sanctionedLoanAmount)
						.filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                // assign computed value to loanAmount
                loanAmount = minAmount.toPlainString();
            } else {
                loanAmount = "0";
            }
        }
        breApplicant.setLoanAmount(loanAmount);

        if (0 == input.getCoappFlag()) {
            loanId = "A01" + breApplicant.getAppId();
            applicantType = Constants.APPLICANT;
			if (breApplicant.getDocumentDetails() == null || breApplicant.getDocumentDetails().isEmpty()
                    || breApplicant.getDocumentDetails().get(0) == null) {
                throw new IllegalStateException("Document details missing");
            }
            userId = breApplicant.getDocumentDetails().get(0).getDocId();
        } else {
            loanId = "C01" + breApplicant.getAppId();
            applicantType = Constants.COAPPLICANT;
			if (input.getHouseholdMember() == null || input.getHouseholdMember().isEmpty()
                    || input.getHouseholdMember().get(0) == null) {
                throw new IllegalStateException("Document details missing");
            }
            userId = input.getHouseholdMember().get(0).getDocumentDetails().get(0).getDocId();
        }
        breApplicant.setLoanId(loanId);

		Optional<WhitelistedBranches> whitelistedBranchOpt = whitelistedBranchesRepository.findByBranchCode(branchId);
		Optional<BipDetails> bipDetailsOpt = bipDetailsRepository.findByApplicationId(breApplicant.getAppId());
        BREBusinessImageAssessment breBusinessImageAssessment = null;
		if (bipDetailsOpt.isPresent() && whitelistedBranchOpt.isPresent()
                && "Y".equalsIgnoreCase(whitelistedBranchOpt.get().getBotApiEnabled())) {
            logger.debug("BIPDetails is present in DB");
            BipDetails details = bipDetailsOpt.get();
            String payload = details.getPayload();
            logger.debug("BIPDetails Payload : {}", payload);
			BREBusinessImageWrapper wrapper = new ObjectMapper().readValue(payload, BREBusinessImageWrapper.class);
            breBusinessImageAssessment = wrapper.getBusinessImageAssessment();
        } else {
            logger.debug("Using default BusinessImageAssessment (null or empty)");
        }
        input.setBusinessImageAssessment(breBusinessImageAssessment);
        logger.debug("Input after setting BusinessImageAssessment: {}", input);

        String interfaceName = CBCheckRequestExt.getInterfaceName();

		boolean isOpenMarket = productCode.equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE);

		Optional<UnnatiIexceedCDHLead> leadOpt = unnatiIexceedCDHLeadRepo.findByCustomerIdAndProductAndReferenceId(
				input.getApplicant().getCustId(), isOpenMarket? productCode :ProductCode.getCdhCodeByUnnatiCode(productCode), appMaster.getWorkitemNo());
		if (isOpenMarket) {
            interfaceName = prop.getProperty(CobFlagsProperties.OPEN_MARKET_BRE_INTF.getKey());
            Integer weeklyTerm = input.getApplicant().getTerm() * 52;
            input.getApplicant().setAppliedTermWeeks(weeklyTerm);
			logger.debug("CDH Called");
			if (leadOpt.isPresent()) {
				input.getApplicant()
						.setKendraActivationDate(String.valueOf(leadOpt.get().getKendraActDate()));
            } else {
                logger.debug("Lead details not present for the customerId : {}", input.getApplicant().getCustId());
				return Mono
						.just(adapterUtil.setError("Lead details not present for this customer. Please verify", "1"));
            }
        }
		if (leadOpt.isPresent()) {
			UnnatiIexceedCDHLead lead = leadOpt.get();
			if (isRestart) {
				BigDecimal crtApprovedAmount = lead.getCrtApprovedAmount();
				input.getApplicant().setCrt_approved_amount(
						crtApprovedAmount != null ? crtApprovedAmount.toPlainString() : "");
				input.getApplicant().setCrt_flag(lead.getCrtFlag());
				input.getApplicant().setCrt_identifier(lead.getCrtIdentifier());
			}
		} else {
			logger.debug("Lead details not present for the customerId : {}", input.getApplicant().getCustId());
			return Mono
					.just(adapterUtil.setError("Lead details not present for this customer. Please verify", "1"));
		}

        CBCheckRequestExt.setRequestObj(brecbRequest.getRequestObj());
		Map<String, Object> combinedRequest = new HashMap<>();
		input.setBusinessImageAssessment(null);
		combinedRequest.put("brecbRequest", brecbRequest);
		combinedRequest.put("header", header);
		String breCheckReq = gson.toJson(combinedRequest);
		logger.debug("Combined request JSON: {}", breCheckReq);

        logger.debug("CBCheckRequestExt from the API: {} ", CBCheckRequestExt.toString());
		Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, CBCheckRequestExt, interfaceName);

        // A
        String finalLoanAmount = loanAmount;
        return apiRespMono.flatMap(val -> {
            logger.debug("response 2 from the API: {} ", val);
            JSONObject resp = null;
            JSONObject apiResp = new JSONObject(new Gson().toJson(val));

            logger.debug("JSON response 3 from the API: {} ", apiResp);

            CibilDetails cblDetails = new CibilDetails();
            CibilDetailsPayload cblPayLoad = new CibilDetailsPayload();
            // response persistence
            try {
                // Fetch custDtlId from CustomerDetails table - by - applicationId and
                // customerType
                Optional<CustomerDetails> customerDetails = custDtlRepo
                        .findByApplicationIdAndCustomerType(input.getApplicant().getAppId(), applicantType);
                if (customerDetails.isPresent()) {
                    cblDetails.setCustDtlId(customerDetails.get().getCustDtlId());
                } else {
                    logger.debug("No customer details found for the given applicationId and customerType.");
					return Mono.just(adapterUtil
							.setError("No customer details found for the given applicationId and customerType", "1"));
                }
                int previousRetryAttempt = 0;
                // check for existing record
                Optional<CibilDetails> cblDetailsExtg = cibilDtlRepo.findByApplicationIdAndAppIdAndCustDtlId(
						input.getApplicant().getAppId(), brecbRequest.getAppId(), customerDetails.get().getCustDtlId());

                if (cblDetailsExtg.isPresent()) {
                    /*
                     * If present delete the original record from CibilDetails and move it to
                     * CibilDetailsHistory table and then insert as new record in CibilDetails
                     */

                    CibilDetails cibilDetails = cblDetailsExtg.get();
					previousRetryAttempt = gson.fromJson(cibilDetails.getPayloadColumn(), CibilDetailsPayload.class)
							.getRetryAttempts();

                    // Create a new instance of CibilDetailsHistory
                    CibilDetailsHistory cblHistory = new CibilDetailsHistory();

                    // Map fields from CibilDetails to CibilDetailsHistory
                    cblHistory.setCbDtlId(cibilDetails.getCbDtlId());
                    cblHistory.setApplicationId(cibilDetails.getApplicationId());
                    cblHistory.setAppId(cibilDetails.getAppId());
                    cblHistory.setVersionNum(cibilDetails.getVersionNum());
                    cblHistory.setCustDtlId(cibilDetails.getCustDtlId());
                    cblHistory.setRequestId(cibilDetails.getRequestId());
                    cblHistory.setResponseId(cibilDetails.getResponseId());
                    cblHistory.setCbDate(cibilDetails.getCbDate());
                    cblHistory.setCbStatus(cibilDetails.getCbStatus());
                    cblHistory.setAdditionalInfo(cibilDetails.getAdditionalInfo());
                    cblHistory.setPayloadColumn(cibilDetails.getPayloadColumn());
                    cblHistory.setRequest(cibilDetails.getRequest());

                    // Save the history record
                    cibilDtlHisRepo.save(cblHistory);

                    String applicationNum = input.getApplicant().getAppId();
                    String fileName = loanId + ".pdf";
                    String fileLocation = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/"
                            + applicationNum + Constants.LOANPATH + applicationNum + "/";
                    String filePath = fileLocation + fileName;
                    File file = new File(filePath);

                    if (file.exists()) {
                        try {
                            Path path = Paths.get(filePath);
                            Files.delete(path);
                            logger.info("file deleted successfully in path: {}", filePath);
                        } catch (NoSuchFileException e) {
                            logger.error("No such file in the path: " + e.getMessage());
                        } catch (IOException e) {
                            logger.error("Error while deleting the file : " + e.getMessage());
                        }
                    }
                    logger.info("Moved record from CibilDetails to CibilDetailsHistory successfully.");
                } else {
                    logger.warn("No record found in CibilDetails for the given criteria.");
                    // insert as new record in CibilDetails
                }

                // insert as new record in CibilDetails
                BigDecimal cbDtlId = CommonUtils.generateRandomNum();
                cblDetails.setCbDtlId(cbDtlId);

                cblDetails.setAppId(brecbRequest.getAppId()); // APZCOB
                cblDetails.setVersionNum(1);
                cblDetails.setApplicationId(input.getApplicant().getAppId()); // 77777777

//					cblDetails.setCbDate(
//							CommonUtils.convertStringToLocalDate(apiResp.getString("request_Date"), "yyyy-MM-dd"));
                cblDetails.setCbDate(LocalDate.now());

                // Set status
                if (apiResp.getString("Final_Decision").toLowerCase().indexOf("approved".toLowerCase()) != -1) {
                    cblDetails.setCbStatus("PASS"); // Pass case
                } else { // Reject case
                    cblDetails.setCbStatus("FAIL");
                    cblPayLoad.setRejectionReason(apiResp.optString("Rejection_reason"));
                }
                cblPayLoad.setFlowResponse(apiResp.optString("flow_response"));

				cblPayLoad.setVisheshEligibility(apiResp.optString("vishesh_eligibility"));
				cblPayLoad.setVisheshIndebtEligibleAmount(apiResp.optString("vishesh_indebt_eligible_amount"));
				cblPayLoad.setUnnatiTentativeEligibility(apiResp.optString("unnati_tentative_eligibility"));
                cblPayLoad.setUnnatiTentativeEligibleAmount(apiResp.optString("unnati_tentative_eligible_amount"));
				cblPayLoad.setUnnatiLiteTentativeEligibility(apiResp.optString("unnati_lite_tentative_eligibility"));
				cblPayLoad.setUnnatiLiteTentativeEligibleAmount(apiResp.optString("unnati_lite_tentative_eligible_amount"));

                cblPayLoad.setIrisMessage(apiResp.getString("IRIS_message"));
				String normalizedIrisMsg = cblPayLoad.getIrisMessage().replaceAll("[–—]", "-") // replace
																								// en-dash/em-dash with
																								// hyphen
                        .toLowerCase();
                String irisTempErrorMsgs = prop.getProperty(CobFlagsProperties.BRE_CHECK_TEMP_IRIS_ERRORS.getKey());
                String[] irisTempErrorMsgsIterable = irisTempErrorMsgs.toLowerCase().split(",");
                cblPayLoad.setRetryAttempts(0);
				int maxRetries = Integer.parseInt(prop.getProperty(CobFlagsProperties.BRE_RETRY_ATTEMPT.getKey()));
                for (String irisError : irisTempErrorMsgsIterable) {
                    if (normalizedIrisMsg.equalsIgnoreCase(irisError)) {

                        int retryCount;

                        if (previousRetryAttempt == 0) {
                            // case 1: first time, initialize from config
                            retryCount = maxRetries;
                        } else {
                            // case 2: subsequent calls, decrement
                            retryCount = Math.max(0, previousRetryAttempt - 1);
                        }

                        cblPayLoad.setRetryAttempts(retryCount);
                        cblPayLoad.setEligibleAmt(finalLoanAmount);
						cblPayLoad.setRejectionReason(cblPayLoad.getRejectionReason() + " | BRE Error message: "
								+ cblPayLoad.getIrisMessage());
                        break; // exit loop once matched
                    }
                }
                String irisRetryErrorMsgs = prop.getProperty(CobFlagsProperties.BRE_CHECK_RETRY_IRIS_ERRORS.getKey());
                String[] irisRetryErrorMsgsIterable = irisRetryErrorMsgs.toLowerCase().split(",");
                for (String irisErrorPattern : irisRetryErrorMsgsIterable) {
                    if (normalizedIrisMsg.contains(irisErrorPattern)) {
                        cblPayLoad.setRetryAttempts(maxRetries);
                        cblPayLoad.setEligibleAmt(finalLoanAmount);
						cblPayLoad.setRejectionReason(cblPayLoad.getRejectionReason() + " | BRE Error Message: "
								+ cblPayLoad.getIrisMessage());
                        break;
                    }
                }

                cblPayLoad.setFinalDecision(apiResp.getString("Final_Decision"));

                cblPayLoad.setCbLoanId(loanId);
                cblPayLoad.setAppliedLoanCode(apiResp.getString("applied_loan_code"));

//					String foir = String.valueOf((apiResp.getString("FOIR").split(":")[1])).split("\\|")[0].trim().split(",")[0].trim();
//                String foir = extractValue(apiResp.getString("FOIR"));
//                cblPayLoad.setFoir(foir);
				cblPayLoad.setFoir(Optional.ofNullable(apiResp.getBigDecimal("Final_FOIR_obligation"))
						.orElse(BigDecimal.ZERO).toPlainString());
//					String foirPercentage = String.valueOf(apiResp.getString("FOIR").split(",")[1].split("%")[0].trim());
                String foirPercentage = apiResp.getBigDecimal("Final_FOIR").toPlainString();
                cblPayLoad.setFoirPercentage(foirPercentage);
                cblPayLoad.setApprovedLoanEMI(String.valueOf(apiResp.optInt("Installment_amt")));
                cblPayLoad.setFinalTenure(apiResp.getString("Final_Tenure"));
                String finalTenure = cblPayLoad.getFinalTenure();
                try {
                    int tenure = Integer.parseInt(finalTenure);
                    loanDtlsRepo.updateTenure(input.getApplicant().getAppId(), tenure);
                } catch (Exception e) {
                    // ignore invalid or null values
                    logger.debug("{} : tenue is not a number", finalTenure);
                }
                cblPayLoad.setEligibleAmt(String.valueOf(apiResp.opt("Approved_Loan_Amount")));
                if (cblPayLoad.getRetryAttempts() > 0) {
                    cblPayLoad.setEligibleAmt(finalLoanAmount);
                }
                cblPayLoad.setEligibleEMI(apiResp.getString("Eligible_EMI"));

                if (0 == input.getCoappFlag()) { // Applicant
                    cblPayLoad.setOverdueAmt(apiResp.getString("applicant_overdue_amount"));
                    cblPayLoad.setWriteOffAmt(apiResp.getString("applicant_Write_Off_Amount"));
                    cblPayLoad.setWriteoffSuitFiledFlag(apiResp.getString("applicant_Writeoff_Suit_filed_Flag"));
//						cblPayLoad.setTotIndebtness(apiResp.getString("applicant_Indebtedness"));
                    cblPayLoad.setIndividualIndebtness(apiResp.getString("applicant_Indebtedness"));

//						String score = String.valueOf((apiResp.getString("score").split(":")[1])).split("\\|")[0].trim();
                    String score = extractValue(apiResp.getString("score"));// "Score : 650 | Pass"
                    cblPayLoad.setCbScore(score);

                } else { // Co-Applicant
                    cblPayLoad.setOverdueAmt(apiResp.getString("co_applicant_Overdue_Amount"));
                    cblPayLoad.setWriteOffAmt(apiResp.getString("co_applicant_Write_Off_Amount"));
                    cblPayLoad.setWriteoffSuitFiledFlag(apiResp.getString("co_applicant_Writeoff_Suit_filed_Flag"));
//						cblPayLoad.setTotIndebtness(apiResp.getString("co_applicant_Indebtedness"));
                    cblPayLoad.setIndividualIndebtness(apiResp.getString("co_applicant_Indebtedness"));

//						String coApptScore = String.valueOf((apiResp.getString("Score").split(":")[1])).split("\\|")[0].trim();
                    String coApptScore = extractValue(apiResp.getString("Score"));
                    cblPayLoad.setCbScore(coApptScore);
                }

                cblPayLoad.setTotIndebtness(apiResp.getString("Indebtedness"));

                cblPayLoad.setOtsFlag(apiResp.getString("OTS_flag"));
                cblPayLoad.setCaglDpdFlag(apiResp.getString("CAGL_DPD_Flag"));
                cblPayLoad.setCaglUnnatiFlag(apiResp.getString("CAGL_Unnati_Flag"));
                cblPayLoad.setEir(apiResp.getString("EIR"));
                cblPayLoad.setRoi(apiResp.getString("ROI"));
//					cblPayLoad.setProcessingFees(apiResp.getString("Processing_fees"));
                // Updated - Jun 03
                double processingFeesRaw = apiResp.optDouble("Processing_fees_incl_GST", 0.0);
				BigDecimal processingFees = BigDecimal.valueOf(processingFeesRaw).setScale(0, RoundingMode.HALF_UP);
                cblPayLoad.setProcessingFees(processingFees.toPlainString());

				BigDecimal processingFeesWithoutGST = BigDecimal.valueOf(apiResp.optDouble("Processing_fees", 0.0));
                cblPayLoad.setProcessingFeesWithoutGST(processingFeesWithoutGST.toPlainString());

				BigDecimal gstAmount = BigDecimal.valueOf(apiResp.optDouble("GST_pf", 0.0));
                cblPayLoad.setGstOnProcessingFees(gstAmount.toPlainString());

                cblPayLoad.setInsuranceChargeMember(String.valueOf(apiResp.optInt("Insurance_charge_Member")));
                cblPayLoad.setInsuranceChargeSpouse(String.valueOf(apiResp.optInt("Insurance_charge_Spouse")));
                cblPayLoad.setInsuranceChargeJoint(String.valueOf(apiResp.optInt("Insurance_charge_Joint")));

                cblPayLoad.setStampDutyCharge(String.valueOf(apiResp.optInt("stamp_duty")));
                cblPayLoad.setRepaymentFrequency(String.valueOf(apiResp.opt("Repayment_frequency")));

                cblPayLoad.setMemberId(input.getApplicant().getCustId());
                cblPayLoad.setKycId(userId);
                cblPayLoad.setBureauName("BRE");
                // Updated - Jun 03
                cblPayLoad.setAppIndebtednessLimit(String.valueOf(apiResp.opt("app_indebtedness_limit")));
                cblPayLoad.setAppMaxLoanLimit(String.valueOf(apiResp.opt("app_max_loan_limit")));
                cblPayLoad.setCoappIndebtednessLimit(String.valueOf(apiResp.opt("coapp_indebtedness_limit")));
                cblPayLoad.setCoappMaxLoanLimit(String.valueOf(apiResp.opt("coapp_max_loan_limit")));
                cblPayLoad.setApplicantIndebtedness(extractValue(apiResp.getString("applicant_Indebtedness")));
                cblPayLoad.setCoApplicantIndebtedness(extractValue(apiResp.getString("co_applicant_Indebtedness")));

                JSONObject addInfo = new JSONObject();
                if (StringUtils.isNotEmpty(input.getCaglOs())) {
                    addInfo.put("caglOs", input.getCaglOs());
                }
                if (StringUtils.isNotEmpty(input.getCurrentStage())) {
                    String stage_subStage = input.getCurrentStage();
                    String[] stageSubStageArr = stage_subStage.split("\\|");
                    String stage = stageSubStageArr[0].trim();
                    String subStage = stageSubStageArr[1].trim();
                    addInfo.put("stage", stage);
                    addInfo.put("subStage", subStage);
                    if (AppStatus.RPCVERIFIED.getValue().equalsIgnoreCase(stage)
                            && Constants.REVIEW_SUBMIT.equalsIgnoreCase(subStage)) {
                        LoanDetails loanDetails = loanDtlsRepo.findByApplicationId(breApplicant.getAppId());
                        LoanDetailsPayload payload = gson.fromJson(loanDetails.getPayloadColumn(), LoanDetailsPayload.class);
                        if (null == payload.getInitialBrePayload() && Constants.PASS_STRING.equalsIgnoreCase(cblDetails.getCbStatus())) {
                            payload.setInitialBrePayload(cblPayLoad);
                            loanDetails.setPayloadColumn(gson.toJson(payload));
                            loanDtlsRepo.save(loanDetails);
                        }
                    }
                }
                cblDetails.setAdditionalInfo(gson.toJson(addInfo));

                // delete the original record from CibilDetails
                cibilDtlRepo.deleteByApplicationIdAndAppIdAndCustDtlId(input.getApplicant().getAppId(),
                        brecbRequest.getAppId(), customerDetails.get().getCustDtlId());

                String payload = new Gson().toJson(cblPayLoad);
                cblDetails.setPayloadColumn(payload);
                cblDetails.setRequest(breCheckReq);
                logger.debug("Cibil Details record: {}", cblDetails);
				Optional<CibilDetails> existing = cibilDtlRepo
						.findByApplicationIdAndCustDtlId(cblDetails.getApplicationId(), cblDetails.getCustDtlId());
                existing.ifPresent(cibilDtlRepo::delete);
                cibilDtlRepo.save(cblDetails);
                logger.debug("Data inserted" + cblDetails.toString());

            } catch (Exception ex) {
                logger.error("Error occurred while executing the BRE details, error = {}", ex.getMessage(), ex);
				saveLog(input.getApplicant().getAppId(), "breCBCheck", CBCheckRequestExt.toString(), val.toString(),
						ResponseCodes.FAILURE.getValue(), ex.getMessage(), "");
                return Mono.error(ex);
            }

            // call report method
            BRECBReportRequest brecbReportRequest = new BRECBReportRequest();
            brecbReportRequest.setLoanId(loanId);
            brecbReportRequest.setAppId(brecbRequest.getAppId()); // APZCOB
            brecbReportRequest.setProductCode(productCode);

            BRECBReportRequestFields reqRptObj = new BRECBReportRequestFields();
            reqRptObj.setCustomerId(input.getApplicant().getCustId());
            reqRptObj.setUserId(userId);
            brecbReportRequest.setRequestObj(reqRptObj);

            Mono<Object> apiReportRespMono = breCBReport(brecbReportRequest, header, prop, false);
            logger.debug("apiReportResp" + apiRespMono);

            return apiReportRespMono.flatMap(val1 -> {
                logger.debug("response 3 from the report API: {} ", val1);
                JSONObject apiReportResp = new JSONObject(new Gson().toJson(val1)).getJSONObject("map");

                // Create a combined response
                String jsonString = new Gson().toJson(cblDetails);
                JSONObject combinedResp = adapterUtil.setSuccessResp(jsonString);
                logger.debug("combinedResp1" + combinedResp);
                combinedResp.put("responseObj", new JSONObject(combinedResp.getString("responseObj")));
                logger.debug("breCbSuccessResonse :" + combinedResp);
                logger.debug("apiReportResp.toString() : " + apiReportResp.toString());
//		            Log.debug("breCbSuccessResonse type :"+ combinedResp.getClass());
                if (apiReportResp.has("base64")) {
                    combinedResp.put("base64", apiReportResp.getString("base64"));
                }
                if (apiReportResp.has("filePath")) {
                    combinedResp.put("filePath", apiReportResp.getString("filePath"));
                }
                logger.error("breFinalResonse :" + combinedResp);
                logger.debug("reponse type :" + combinedResp.getClass());
                return Mono.<Object>just(combinedResp);   // Mono<Object>
            }).onErrorResume(e -> {
                logger.error("Error occurred while executing the BRE Report api, error = {}", e.getMessage(), e);
				saveLog(appnId, "breCBReport", brecbReportRequest.toString(), e.getMessage(),
						ResponseCodes.FAILURE.getValue(), e.getMessage(), "");
                return Mono.just(adapterUtil.setError("Error occurred while executing the BRE Report api.", "1"));
            });
        }).cache().onErrorMap(e -> {
			brecbRequest.getRequestObj().getBreCBValuesRequestvalues1().getBreCBInputRequestinput1()
					.getBreCBValuesRequestvalues2().getBreCBInputRequestinput2().setBusinessImageAssessment(null);
            logger.error("Error occurred while executing the BRE api, error = {}", e.getMessage(), e);
			saveLog(appnId, "breCBCheck", brecbRequest.toString(), e.getMessage(), ResponseCodes.FAILURE.getValue(),
					e.getMessage(), "");
            throw new RuntimeException("Error occurred while executing the BRE api.", e);
        });
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

    @CircuitBreaker(name = "fallback", fallbackMethod = "highmarkCheckCBFallback")
    public Mono<Object> highmarkCheckCallback(HighMarkCheckCBRequest highMarkCheckCBRequest, Header header) {
        try {

            final String loanId;
            final String applicantType;

            logger.debug("request from the highmarkCheckCallback API: {} ", highMarkCheckCBRequest.toString());
            loanId = highMarkCheckCBRequest.getRequestObj().getLoanId();

            if (loanId.startsWith("A01")) {
                applicantType = Constants.APPLICANT;
            } else {
                applicantType = Constants.COAPPLICANT;
            }

            Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, highMarkCheckCBRequest,
                    highMarkCheckCBRequest.getInterfaceName());
            logger.debug("response 1 from the API: {} ", apiRespMono);

            return apiRespMono.flatMap(val -> {
                logger.debug("response 2 from the API: {} ", val);

                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("JSON response 3 from the API: {} ", apiResp);

                CibilDetails cblDetails = new CibilDetails();
                CibilDetailsPayload cblPayLoad = new CibilDetailsPayload();

                try {
                    // Fetch custDtlId from CustomerDetails table - by - applicationId and
                    // customerType
                    Optional<CustomerDetails> customerDetails = custDtlRepo.findByApplicationIdAndCustomerType(
                            highMarkCheckCBRequest.getApplicationId(), applicantType);
                    if (customerDetails.isPresent()) {
                        cblDetails.setCustDtlId(customerDetails.get().getCustDtlId());
                    } else {
                        logger.debug("No customer details found for the given applicationId and customerType.");
                        return Mono.just(adapterUtil.setError(
                                "No customer details found for the given applicationId and customerType", "1"));
                    }

                    // check for existing record
                    Optional<CibilDetails> cblDetailsExtg = cibilDtlRepo.findByApplicationIdAndAppIdAndCustDtlId(
                            highMarkCheckCBRequest.getApplicationId(), highMarkCheckCBRequest.getAppId(),
                            customerDetails.get().getCustDtlId());

                    if (cblDetailsExtg.isPresent()) {
                        /*
                         * If present delete the original record from CibilDetails and move it to
                         * CibilDetailsHistory table and then insert as new record in CibilDetails
                         */

                        CibilDetails cibilDetails = cblDetailsExtg.get();

                        // Create a new instance of CibilDetailsHistory
                        CibilDetailsHistory cblHistory = new CibilDetailsHistory();

                        // Map fields from CibilDetails to CibilDetailsHistory
                        cblHistory.setCbDtlId(cibilDetails.getCbDtlId());
                        cblHistory.setApplicationId(cibilDetails.getApplicationId());
                        cblHistory.setAppId(cibilDetails.getAppId());
                        cblHistory.setVersionNum(cibilDetails.getVersionNum());
                        cblHistory.setCustDtlId(cibilDetails.getCustDtlId());
                        cblHistory.setRequestId(cibilDetails.getRequestId());
                        cblHistory.setResponseId(cibilDetails.getResponseId());
                        cblHistory.setCbDate(cibilDetails.getCbDate());
                        cblHistory.setCbStatus(cibilDetails.getCbStatus());

                        cblHistory.setPayloadColumn(cibilDetails.getPayloadColumn());

                        // Save the history record
                        cibilDtlHisRepo.save(cblHistory);

                        // delete the original record from CibilDetails
                        cibilDtlRepo.deleteByApplicationIdAndAppIdAndCustDtlId(
                                highMarkCheckCBRequest.getApplicationId(), highMarkCheckCBRequest.getAppId(),
                                customerDetails.get().getCustDtlId());
                        logger.info("Moved record from CibilDetails to CibilDetailsHistory successfully.");
                    } else {
                        logger.warn("No record found in CibilDetails for the given criteria.");
                        // insert as new record in CibilDetails
                    }

                    // response persistence
                    BigDecimal cbDtlId = CommonUtils.generateRandomNum();
                    cblDetails.setCbDtlId(cbDtlId);

                    cblDetails.setAppId(highMarkCheckCBRequest.getAppId()); // APZCOB
                    cblDetails.setVersionNum(1);
                    cblDetails.setApplicationId(highMarkCheckCBRequest.getApplicationId()); // 77777777

                    cblDetails.setCbDate(LocalDate.now());
                    // Set status
                    if (apiResp.getString("FINAL_DECISION").equalsIgnoreCase("PASS")) {
                        cblDetails.setCbStatus("PASS");
                    } else {
                        cblDetails.setCbStatus("FAIL");
                    }

                    cblPayLoad.setBureauName("Highmark");
                    cblPayLoad.setHitNohit(apiResp.getString("Inquiry_decision"));
                    cblPayLoad.setTotIndebtness(apiResp.getString("Unsecured_Indebtedness"));
                    String score = extractValue(apiResp.getString("CB_SCORE"));
                    cblPayLoad.setCbScore(score);
                    cblPayLoad.setNoParInLastMonths(apiResp.getString("Industry_DPD"));
                    cblPayLoad.setMaxDpdInLastMonths(apiResp.getString("CAGL_DPD"));
                    cblPayLoad.setWrittenOff(apiResp.getString("Total_WrittenOffAmount"));
                    cblPayLoad.setWriteOffAmt(apiResp.getString("Total_WrittenOffAmount"));
                    cblPayLoad.setOverlapWithMmfl(apiResp.getString("OVERLAP_WITH_MMFL"));
                    cblPayLoad.setOverdueAmt(apiResp.getString("Total_overdue"));

                    String eligibleAmount = apiResp.has("ELIGIBLE_AMOUNT") ? apiResp.get("ELIGIBLE_AMOUNT").toString()
                            : "0";
                    cblPayLoad.setEligibleAmt(eligibleAmount);

                    cblPayLoad.setCgpDpd(apiResp.getString("CAGL_DPD"));
                    cblPayLoad.setFinalDecision(apiResp.getString("FINAL_DECISION"));
                    cblPayLoad.setFoir("" + apiResp.get("FOIR"));

                    cblPayLoad.setConsentType("OTP");
                    cblPayLoad.setCbLoanId(loanId);

                    Properties prop = CommonUtils.readPropertyFile();

                    cblPayLoad.setCbReport(prop.getProperty(CobFlagsProperties.HIGHMARK_REPORT_URL.getKey()) + "?losId="
                            + apiResp.get("request_id").toString() + "&customerId="
                            + highMarkCheckCBRequest.getMemberId() + "&loanId=" + loanId);

                    String payload = new Gson().toJson(cblPayLoad);
                    logger.debug("cibilPayload : " + payload);
                    cblDetails.setPayloadColumn(payload);

                    cibilDtlRepo.save(cblDetails);
                    logger.debug("Data inserted" + cblDetails.toString());
                } catch (Exception ex) {
                    logger.error("Error occurred while executing the highmarkCheckCallback details, error = "
                            + ex.getMessage());
                    return Mono.just(
                            adapterUtil.setError("Error occurred while executing the highmarkCheckCallback api.", "1"));
                }

                String jsonString = new Gson().toJson(cblDetails);
                JSONObject finalResp = adapterUtil.setSuccessResp(jsonString);

                return Mono.just(finalResp);
            });

        } catch (Exception e) {
            logger.error("Error occurred while executing the highMarkCallback api, error = " + e.getMessage());
            return Mono
                    .just(adapterUtil.setError("Error occurred while executing the highmarkCheckCallback api.", "1"));
        }

    }

    private String getDefaultValueIfObjNull(Object obj) {
		return obj == null ? "" : String.valueOf(obj).trim().replaceAll("[^a-zA-Z0-9\\s,.]", "");
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "wipDedupeCheckFallback")
    public Mono<Object> wipDedupeCheck(WipDedupeCheckRequest wipDedupeCheckRequest, Header header) {
        LocalDateTime startDateTime = LocalDateTime.now();
        JSONObject resp = new JSONObject();
        ObjectMapper mapperObj = new ObjectMapper();
        mapperObj.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String reqStr = "";
        try {
            reqStr = mapperObj.writeValueAsString(wipDedupeCheckRequest);

            logger.debug("Request received for the API {}", reqStr);
            String customerId = wipDedupeCheckRequest.getRequestObj().getCustomerId();
            String primaryKycId = wipDedupeCheckRequest.getRequestObj().getPrimaryKycId();
            logger.debug("Customer ID received: {}", customerId);
            logger.debug("Primary KYC ID received: {}", primaryKycId);
            long count = applicationMasterRepo.countByMemberIdAndPrimaryKycId(customerId, primaryKycId);
            logger.debug("Count from database query: {}", count);
            resp.put(Constants.CUSTOMERID1, customerId);
            resp.put("primaryKycId", primaryKycId);
            if (count > 0) {
                resp.put("result", 1);
            } else {
                resp.put("result", 0);
            }

        } catch (JsonProcessingException e) {

            resp = adapterUtil.setError("Failed to parse the request object.", "1");
        }
        logger.debug("logging the request and response in db");

        logger.error("End : callService");
        return Mono.just(resp);
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "SignzyPennylessCheckFallback")
    public Mono<Object> SignzyPennylessCheck(SignzyPennylessRequest signzyPennylessRequest, Header header,
                                             Properties prop) {
        try {
            logger.debug("request from the API: {} ", signzyPennylessRequest.toString());

            //set pennyStatus and pennyResp as empty
            savePennyDetails(null, signzyPennylessRequest.getApplicationId(), signzyPennylessRequest.getAppId(), "");

            SignzyPennylessCheckRequestExt signzyPennylessCheckRequestExt = new SignzyPennylessCheckRequestExt();
            signzyPennylessCheckRequestExt.setAppId(signzyPennylessRequest.getAppId());
            signzyPennylessCheckRequestExt
                    .setInterfaceName(prop.getProperty(CobFlagsProperties.SIGNZY_PENNYLESS_CHECK_INTF.getKey()));
            signzyPennylessCheckRequestExt.setRequestObj(signzyPennylessRequest.getRequestObj());
            logger.debug("signzyPennylessCheckRequestExt from the API: {} ", signzyPennylessCheckRequestExt.toString());
            Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, signzyPennylessCheckRequestExt,
                    signzyPennylessCheckRequestExt.getInterfaceName());
            logger.debug("response 1 from the API: {} ", apiRespMono);

            return apiRespMono.flatMap(val -> {
                String pennyStatus = "Fail";
                logger.debug("response 2 from the API: {} ", val);
                JSONObject resp = null;
                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("JSON response 3 from the API: {} ", apiResp);

                if (!apiResp.isEmpty()) {
                    if (apiResp.has("result") && !apiResp.getJSONObject("result").isEmpty()) {
                        if (apiResp.getJSONObject("result").has(Constants.REASON)) {
                            String reason = apiResp.getJSONObject("result").getString(Constants.REASON);
                            logger.debug("response :" + reason);
                            if (ResponseCodes.SUCCESS.getValue().equalsIgnoreCase(reason)) {
                                resp = adapterUtil.setSuccessResp(apiResp.toString());
                                pennyStatus = "Pass";
                            } else {
                                logger.error("Error occurred while executing the Signzy Pennyless Check api, error = "
                                        + reason + ",Response = " + apiResp.toString());
								savePennyDetails(apiResp, signzyPennylessRequest.getApplicationId(),
										signzyPennylessRequest.getAppId(), pennyStatus);
                                //
								saveLog(signzyPennylessRequest.getApplicationId(), "SignzyPennylessCheck",
										signzyPennylessRequest.toString(), apiResp.toString(),
										ResponseCodes.FAILURE.getValue(), apiResp.toString(), "");
                                return Mono.just(adapterUtil.setError("" + reason, "0"));
                            }
                        }
                    } else {
                        if (apiResp.has(Constants.ERROR1) && !apiResp.getJSONObject(Constants.ERROR1).isEmpty()) {
                            String errorMsg = apiResp.getJSONObject(Constants.ERROR1).getString(Constants.MESSAGE);
                            String errorReason = apiResp.getJSONObject(Constants.ERROR1).getString(Constants.REASON);
                            logger.error("Error occurred while executing the Signzy Pennyless Check api, error = "
                                    + errorMsg + ",Reason = " + errorReason);
							savePennyDetails(apiResp, signzyPennylessRequest.getApplicationId(),
									signzyPennylessRequest.getAppId(), pennyStatus);
                            return Mono.just(adapterUtil.setError(errorMsg, "0"));
                        } else {
                            logger.error(
                                    "Error occurred while executing the Signzy Pennyless Check api, error = result is empty");
							savePennyDetails(apiResp, signzyPennylessRequest.getApplicationId(),
									signzyPennylessRequest.getAppId(), pennyStatus);
                            //
							saveLog(signzyPennylessRequest.getApplicationId(), "SignzyPennylessCheck",
									signzyPennylessRequest.toString(), apiResp.toString(),
									ResponseCodes.FAILURE.getValue(), apiResp.toString(), "");
                            return Mono.just(adapterUtil.setError(
                                    "Error occurred while executing the Signzy Pennyless Check api, error = result is empty",
                                    "1"));
                        }

                    }
                } else {
                    logger.error(
                            "Error occurred while executing the Signzy Pennyless Check api, error = empty response");
					savePennyDetails(apiResp, signzyPennylessRequest.getApplicationId(),
							signzyPennylessRequest.getAppId(), pennyStatus);
					saveLog(signzyPennylessRequest.getApplicationId(), "SignzyPennylessCheck",
							signzyPennylessRequest.toString(), apiResp.toString(), ResponseCodes.FAILURE.getValue(),
							apiResp.toString(), "");
                    return Mono.just(adapterUtil.setError(
                            "Error occurred while executing the Signzy Pennyless Check api, error = empty response",
                            "1"));
                }

                /*
                 * ResponseBody responseBody = new ResponseBody(); Response response = new
                 * Response(); ResponseHeader responseHeader = new ResponseHeader();
                 * responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey()); Gson gson =
                 * new Gson(); String responseStr = gson.toJson(val);
                 * responseBody.setResponseObj(responseStr);
                 * response.setResponseBody(responseBody);
                 * response.setResponseHeader(responseHeader);
                 */
				savePennyDetails(apiResp, signzyPennylessRequest.getApplicationId(), signzyPennylessRequest.getAppId(),
						pennyStatus);
                return Mono.just(resp);
            });
        } catch (Exception e) {
            logger.error("Error occurred while executing the Signzy Pennyless Check api, error = " + e.getMessage());
			saveLog(signzyPennylessRequest.getApplicationId(), "SignzyPennylessCheck",
					signzyPennylessRequest.toString(), e.getMessage(), ResponseCodes.FAILURE.getValue(), e.getMessage(),
					"");
            return Mono
                    .just(adapterUtil.setError("Error occurred while executing the Signzy Pennyless Check api.", "1"));
        }
    }

    private Mono<Object> savePennyDetails(JSONObject apiResp, String applicationId, String appId, String pennyStatus) {
        logger.debug("apiResp : {}", apiResp);
        logger.debug("applicationId : {}", applicationId);
        logger.debug("pennyStatus : {}", pennyStatus);
        try {
            //
//		Optional<BankDetails> bankDetailsDb = bankDtlRepo.findByApplicationId(applicationId);
			Optional<BankDetails> bankDetailsDb = bankDtlRepo.findBankDetailsByCustomerType(Constants.APPLICANT,
					applicationId);
            ObjectMapper objectMapper = new ObjectMapper();
            Gson gson = new Gson();
            BankDetails existingBankRecord = null;
            if (bankDetailsDb.isPresent()) {
                logger.debug("bankDetails data found: ");
                existingBankRecord = bankDetailsDb.get();

                BankDetailsPayload bankDetailsPayload = objectMapper.readValue(existingBankRecord.getPayloadColumn(),
                        BankDetailsPayload.class);
                if (pennyStatus.equalsIgnoreCase("Pass")) {
                    bankDetailsPayload.setPennyResp(apiResp.toString());
                } else {
                    bankDetailsPayload.setPennyResp("");
                }

                bankDetailsPayload.setPennyCheckStatus(pennyStatus);
                existingBankRecord.setPayloadColumn(gson.toJson(bankDetailsPayload));
                logger.debug("bankDetailsPayload " + existingBankRecord.getPayloadColumn());

                bankDtlRepo.save(existingBankRecord);

                logger.warn("Completed updating Bank Details - TB_CGOB_BANK_DTLS for loans");
                return Mono.just(adapterUtil.setSuccessResp(existingBankRecord.toString()));
            } else {
                logger.debug("record not found for bankDetails " + "applicationId :" + applicationId);
                return Mono.just(adapterUtil.setError("application not found.", "1"));
            }
        } catch (Exception ex) {
            logger.error("Error occurred while executing savePennyDetails, error = " + ex.getMessage());
            return Mono.just(adapterUtil.setError("Error occurred while savePennyDetails api.", "1"));

        }

    }

    private Mono<Object> validateKycFallback(ValidateKycRequest validateKycRequest, Header header, Properties prop,
                                             Exception e) {
        logger.error("validateKycFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> kycDedupeFallback(KycDedupeRequest kycDedupeRequest, Header header, Properties prop,
                                           Exception e) {
        logger.error("kycDedupeFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> fetchIFSCFallback(FetchIFSCRequest fetchIFSCRequest, Header header, Properties prop,
                                           Exception e) {
        logger.error("fetchIFSCFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> fetchExistingLoanFallback(ExistingLoanRequest existingLoanRequest, Header header,
                                                   Properties prop, Exception e) {
        logger.error("fetchExistingLoanFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> highmarkCheckFallback(HighMarkCheckRequest highmarkCheckRequest, Header header,
                                               Properties prop, Exception e) {
        logger.error("highmarkCheckFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

	private Mono<Object> breCBCheckFallback(BRECBRequest breCBCheckRequest, Header header, Properties prop,
			Exception e) {
        logger.error("breCBCheckFallback error", e);

		String currentStatus = breCBCheckRequest.getRequestObj().getBreCBValuesRequestvalues1()
				.getBreCBInputRequestinput1().getBreCBValuesRequestvalues2().getBreCBInputRequestinput2()
                .getCurrentStage();

		if (currentStatus != null
				&& (currentStatus.toUpperCase().contains(AppStatus.CACOMPLETED.getValue().toUpperCase())
						|| currentStatus.toUpperCase().contains(AppStatus.RESANCTION.getValue().toUpperCase())
						|| currentStatus.toUpperCase().contains(AppStatus.RPCVERIFIED.getValue().toUpperCase()))) {
            return Mono.error(e);
        }
        return FallbackUtils.genericFallbackMonoObject();
    }

	private Mono<Object> bIPFallback(BIPMasterRequest apiRequest, Header header, Properties prop, Exception e) {
        logger.error("bIPFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

	private Mono<Object> loanCreationFallback(String applicationId, Header header, Properties prop, Exception e) {
        logger.error("loanCreationFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> coApplicantCreationFallback(String applicationId, Header header, Properties prop,
                                                     Exception e) {
        logger.error("coApplicantCreationFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> breCBReportFallback(BRECBReportRequest breCBReportRequest, Header header, Properties prop,
                                             boolean isExist, Exception e) {
        logger.error("breCBReportFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> SignzyPennylessCheckFallback(SignzyPennylessRequest signzyPennylessRequest, Header header,
                                                      Properties prop, Exception e) {
        logger.error("SignzyPennylessCheckFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> highmarkCheckCBFallback(HighMarkCheckCBRequest highMarkCheckCBRequest, Header header,
                                                 Exception e) {
        logger.error("highmarkCheckCBFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> workitemCreationFallback(WorkitemCreationRequest workitemCreationRequest, Header header,
                                                  Properties prop, Exception e) {
        logger.error("workitemCreationFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> sendBackWorkitemsFallback(SendbackWorkitemRequest sendbackWorkitemRequest, Header header,
                                                   Properties prop, Exception e) {
        logger.error("sendBackWorkitemsFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> newgenWipDedupeFallback(WipDedupeRequest wipDedupeRequest, Header header, Properties prop,
                                                 Exception e) {
        logger.error("newgenWipDedupeFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> sendBackDataFetchFallback(SendbackDataFetchRequest sendbackDataFetchRequest, Header header,
                                                   Properties prop, Exception e) {
        logger.error("sendBackDataFetchFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> wipDedupeCheckFallback(WipDedupeCheckRequest wipDedupeCheckRequest, Header header,
                                                Exception e) {
        logger.error("wipDedupeCheckFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Response rpcUploadLoanFallback(UploadLoanRequest uploadLoanRequest, Exception e) {
        logger.error("rpcUploadLoanFallback error : ", e);
        return CommonUtils.setError(e.getMessage(), "");

    }

    // A
    @CircuitBreaker(name = "fallback", fallbackMethod = "breCBReportFallback")
    public Mono<Object> breCBReport(BRECBReportRequest brecbReportRequest, Header header, Properties prop,
                                    boolean isExist) {

        try {
            String customerId = brecbReportRequest.getRequestObj().getCustomerId();
            String userId = brecbReportRequest.getRequestObj().getUserId();

            if(StringUtils.isBlank(customerId) || StringUtils.isBlank(userId)){
                logger.error("Invalid input: customerId and userId are required.");
                JSONObject errorResp = new JSONObject();
                errorResp.put("status", Constants.ERROR1);
                errorResp.put(Constants.MESSAGE, "Invalid input: customerId and userId are required.");
                return Mono.just(errorResp);
            }

            String productCode = brecbReportRequest.getProductCode();

            String interfaceName = prop.getProperty(CobFlagsProperties.BRE_CB_REPORT_INTF.getKey());
			if (StringUtils.isNotBlank(productCode)
					&& Constants.OPENMARKET_LOAN_PRODUCT_CODE.equalsIgnoreCase(productCode)) {
                interfaceName = prop.getProperty(CobFlagsProperties.OM_BRE_CB_REPORT_INTF.getKey());
            }

            logger.debug("request from the breCBReport API: {} ", brecbReportRequest.toString());
            BRECBCheckRequestExt CBCheckRequestExt = new BRECBCheckRequestExt();
            CBCheckRequestExt.setAppId(brecbReportRequest.getAppId());
            CBCheckRequestExt.setInterfaceName(interfaceName);
            CBCheckRequestExt.setRequestObj(brecbReportRequest.getRequestObj());

            String applicationNum = brecbReportRequest.getLoanId().substring(3); // 2313213131311
            String fileName = brecbReportRequest.getLoanId() + ".pdf"; // A012313213131311.pdf
            String uploadLocation = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/"
                    + brecbReportRequest.getAppId() + Constants.LOANPATH + applicationNum + "/";
            logger.debug("request from the breCBReport API: {} ", brecbReportRequest.toString());
            JSONObject resp = null;
            String filePath1 = uploadLocation + fileName;
            File file = new File(filePath1);

            // If the file exists, read it and convert to Base64
            if (isExist && file.exists()) {
                try {
                    logger.debug("file exist ");
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    String base64String = Base64.getEncoder().encodeToString(fileContent);

                    resp = adapterUtil.setSuccessResp("success");
                    resp.put("status", "success");
                    resp.put("filePath", filePath1);
                    resp.put("base64", base64String);

                    return Mono.just(resp);

                } catch (IOException e) {
                    JSONObject errorResp = new JSONObject();
                    errorResp.put("status", Constants.ERROR1);
                    errorResp.put(Constants.MESSAGE, "File reading failed.");
                    return Mono.just(errorResp);

                }
            } else {
                // If file does not exist
                logger.debug("file not exist ");
                return interfaceAdapter
                        .callExternalService(header, CBCheckRequestExt, CBCheckRequestExt.getInterfaceName())
                        .flatMap(val -> {
                            logger.debug("val: {}", val.getClass().getName());
                            logger.debug("val: {}", val);
                            if (val instanceof byte[]) {
                                if (CommonUtils.isPdf((byte[]) val)) {
                                    logger.debug("val: inside");
                                    // InputStream pdfStream = (InputStream) val;

                                    // Write the PDF to file
                                    Path writtenFilePath = writeBytePdfToFile((byte[]) val, uploadLocation, fileName);

                                    // Log the file path where the PDF was written
                                    logger.debug("File written to: {}", writtenFilePath);

                                    // Convert the file to Base64
                                    String base64String = convertFileToBase64(writtenFilePath);
                                    logger.debug("base64String generated");
                                    // Create a success response
                                    JSONObject resp1 = adapterUtil.setSuccessResp("success");
                                    resp1.put("status", "success");
                                    resp1.put("filePath", writtenFilePath.toString());
                                    resp1.put("base64", base64String);

                                    return Mono.just(resp1);
                                } else if (CommonUtils.isJson((byte[]) val)) {
                                    JSONObject errorResp = new JSONObject();
                                    try {
                                        JSONObject response = CommonUtils.convertByteStreamToJson((byte[]) val);
                                        errorResp.put(Constants.MESSAGE, response.getString(Constants.MESSAGE));
                                    } catch (IOException e) {
                                        logger.error("Error converting byte stream to JSON: {}", e);
                                    }
                                    // If the response is not an InputStream, return an error response

                                    errorResp.put("status", Constants.ERROR1);

                                    return Mono.just(errorResp);
                                } else {
                                    // If the response is not an InputStream, return an error response
                                    JSONObject errorResp = new JSONObject();
                                    errorResp.put("status", Constants.ERROR1);
                                    errorResp.put(Constants.MESSAGE, "Invalid response format.");
                                    saveLog(applicationNum, "breCBReport", CBCheckRequestExt.toString(),
											errorResp.toString(), ResponseCodes.FAILURE.getValue(),
											errorResp.toString(), "");
                                    return Mono.just(errorResp);
                                }
                            } else {
                                // If the response is not an InputStream, return an error response
                                JSONObject errorResp = new JSONObject();
                                errorResp.put("status", Constants.ERROR1);
                                errorResp.put(Constants.MESSAGE, "Invalid response format.");
                                saveLog(applicationNum, "breCBReport", CBCheckRequestExt.toString(),
										errorResp.toString(), ResponseCodes.FAILURE.getValue(), errorResp.toString(),
										"");
                                return Mono.just(errorResp);
                            }
                        });
            }
        } catch (Exception e) {
            logger.error("Error occurred while executing the BRE CB Report api, error = " + e.getMessage());
            saveLog(brecbReportRequest.getLoanId().substring(3), "breCBReport", brecbReportRequest.toString(),
                    e.getMessage(), ResponseCodes.FAILURE.getValue(), e.getMessage(), "");
            return Mono.just(adapterUtil.setError("Error occurred while executing the BRE CB Report api.", "1"));
        }

    }

    // Method to write InputStream to a file
    private Path writeBytePdfToFile(byte[] fileContent, String filePath, String fileName) {
//      Path filePath = Paths.get("some/directory", fileName); // Define the folder where the PDF will be saved
        Path path = Paths.get(filePath, fileName);
        logger.debug("path: " + path.toString());
        try {
            // Ensure the directories exist
            Files.createDirectories(path.getParent());

            try (FileOutputStream fos = new FileOutputStream(path.toString())) {
                fos.write(fileContent); // Write the byte array to the file
                logger.debug("PDF file successfully written to " + filePath);
            } catch (IOException e) {
                logger.error("Error writing PDF file: " + e.getMessage());
            }
        } catch (IOException e) {
            logger.error("Error writing file: " + e.getMessage(), e);
            throw new RuntimeException("Error writing file: " + e.getMessage(), e);
        }

        return path;
    }

    /**
     * Converts a file's content to a Base64 string.
     *
     * @param filePath Path to the file
     * @return Base64 encoded string
     */

    // Method to convert a file to Base64 string
    private String convertFileToBase64(Path filePath) {
        try {
            byte[] fileContent = Files.readAllBytes(filePath); // Read the file content
            return Base64.getEncoder().encodeToString(fileContent);
//            return new sun.misc.BASE64Encoder().encode(fileContent);

        } catch (IOException e) {
            logger.error("Error converting file to Base64: " + e.getMessage(), e);
            throw new RuntimeException("Error converting file to Base64: " + e.getMessage(), e);
        }
    }

    private String extractValue(String input) {
        // Normalize delimiters: Remove unnecessary spaces around ":" and ","
        String normalizedInput = CommonUtils.normalizeInput(input);

        // Find the first key-value pair ("Score : 650")
        int colonIndex = normalizedInput.indexOf(":");
        if (colonIndex != -1) {
            // Get the value after the colon
            int startIndex = colonIndex + 1;
            int endIndex = normalizedInput.indexOf(",", startIndex); // Find the next comma
            if (endIndex == -1) { // If no comma, find the next space or take till the end
                endIndex = normalizedInput.indexOf(" ", startIndex);
                if (endIndex == -1) {
                    endIndex = normalizedInput.length(); // No space found; take till the end
                }
            }
            return normalizedInput.substring(startIndex, endIndex).trim(); // Extract and trim
        }
        return null; // No key-value pair found
    }

    //A
    public Response downloadLoanApplication(FetchAppRequest fetchAppReq) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        String applicationId = fetchAppReq.getRequestObj().getApplicationId();
        String appId = fetchAppReq.getRequestObj().getAppId();
        int versionNum = fetchAppReq.getRequestObj().getVersionNum();

        CustomerDataFields custmrDataFields = null;
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepo
                .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, versionNum);

        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            custmrDataFields = cobService.getCustomerData(applicationMasterData, applicationId, appId, versionNum);
            logger.debug("custmrDataFields.toString() : " + custmrDataFields.toString());

            List<ApplicationWorkflow> workflow;
            workflow = applnWfRepository.findByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumAsc(appId,
                    applicationId, versionNum);
            logger.debug("Workflow details {} ", workflow);

            String bmId = "-";
            String kmId = "-";
            String usernameKM = "-";
            String usernameBM = "-";
            String kmSubmDateStr = "";
            String bmSubmDateStr = "";
			List<String> docTypes = Arrays.asList("CUSTOMER_PHOTOGRAPH");

			List<ApplicationDocuments> applnDocumentList = applicationDocumentsRepository.fetchDocumentsByApplicationIdAndDocumentTypeList(applicationMasterData.getApplicationId(),docTypes);
            if (!workflow.isEmpty()) {
                custmrDataFields.setApplicationWorkflowList(workflow);

                LocalDateTime kmSubmDate = null;
                for (ApplicationWorkflow applnWorkflow : custmrDataFields.getApplicationWorkflowList()) {
                    if (Constants.INITIATOR.equalsIgnoreCase(applnWorkflow.getCurrentRole()) && (WorkflowStatus.APPROVED
                            .getValue().equalsIgnoreCase(applnWorkflow.getApplicationStatus())
                            || WorkflowStatus.PENDING_FOR_APPROVAL.getValue()
                            .equalsIgnoreCase(applnWorkflow.getApplicationStatus()))) {
                        kmId = applnWorkflow.getCreatedBy();
                        kmSubmDate = applnWorkflow.getCreateTs();

                        try {
                            kmSubmDateStr = CommonUtils.formatDateTimeToDateStr(kmSubmDate);
                            logger.debug("Formatted kmSubmDateStr: " + kmSubmDateStr);
                        } catch (Exception e) {
                            logger.error("error while formatted date : " + e);
                        }
                    }
                }

				if (Constants.UNNATI_PRODUCT_CODE.equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.OPENMARKET_LOAN_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())) {
                    logger.info("Unnati application");

                    String previousWorkflowStatus = null;
                    for (ApplicationWorkflow appnWorkflow : custmrDataFields.getApplicationWorkflowList()) {
                        String currentStatus = appnWorkflow.getApplicationStatus();
                        if (Constants.APPROVED.equalsIgnoreCase(appnWorkflow.getApplicationStatus())) {
                            if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue()
                                    .equalsIgnoreCase(previousWorkflowStatus)) {
                                bmId = appnWorkflow.getCreatedBy();
                                LocalDateTime bmSubmDate = appnWorkflow.getCreateTs();

                                try {
                                    bmSubmDate = kmSubmDate;
                                    bmSubmDateStr = CommonUtils.formatDateTimeToDateStr(bmSubmDate);
                                    logger.debug("Formatted bmSubmDateStr: {}", bmSubmDateStr);
                                } catch (Exception e) {
                                    logger.error("Error while formatting date: ", e);
                                }
                            }
                        }
                        previousWorkflowStatus = currentStatus;
                    }

				} else if (Constants.RENEWAL_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())) {
                    logger.info("Renewal Unnati application");
                    List<ApplicationWorkflow> list = custmrDataFields.getApplicationWorkflowList();

                    for (int i = list.size() - 1; i >= 0; i--) {

                        ApplicationWorkflow appnWorkflow = list.get(i);
                        String currentStatus = appnWorkflow.getApplicationStatus();

                        if (Constants.RPCVERIFIED.equalsIgnoreCase(currentStatus)) {

                            // Check next workflow (forward direction)
                            if (i + 1 < list.size()) {
                                ApplicationWorkflow nextWorkflow = list.get(i + 1);
                                String nextRole = nextWorkflow.getCurrentRole();
                                String nextStatus = nextWorkflow.getApplicationStatus();
                                logger.info("nextStatus - after RPCVERIFIED -" + nextStatus);
                                if (Constants.APPROVER.equalsIgnoreCase(nextRole)) {
                                    // Take Approver's createdBy
                                    bmId = nextWorkflow.getCreatedBy();
                                    LocalDateTime bmSubmDate = nextWorkflow.getCreateTs();

                                    try {
                                        bmSubmDate = kmSubmDate;
                                        bmSubmDateStr = CommonUtils.formatDateTimeToDateStr(bmSubmDate);
                                        logger.debug("Formatted bmSubmDateStr: {}", bmSubmDateStr);
                                    } catch (Exception e) {
                                        logger.error("Error while formatting date: ", e);
                                    }
                                }
                            }
                            break;
                        }
                    }

                } else {
                    logger.info("Not an Unnati/Renewal application");
                }

                Optional<String> usernameOptKM = tbUserRepository.findUserNameByUserId(kmId);
                usernameKM = usernameOptKM.orElse("");
                logger.info("usernameKM : " + usernameKM);

                Optional<String> usernameOptBM = tbUserRepository.findUserNameByUserId(bmId);
                usernameBM = usernameOptBM.orElse("");
                logger.info("usernameBM : " + usernameBM);

            }

            Gson gsonObj = new Gson();
            LoanDetailsPayload payload = gsonObj.fromJson(custmrDataFields.getLoanDetails().getPayloadColumn(),
                    LoanDetailsPayload.class);

            String inputlanguage = payload.getLanguage();
            String language = Constants.DEFAULTLANGUAGE;
            logger.debug("finputlanguage Language:" + inputlanguage);
            if (inputlanguage.equalsIgnoreCase("Kannada")) {
                language = "Kannada";
            }
            logger.debug("final Language:" + language);

            String fileName = "jsonKeysFor" + language + "LoanApplication.json"; // jsonKeysForKannadaLoanApplication.json

            logger.debug("fileName :" + fileName);
            JSONObject keysForContent = new JSONObject();
            JSONObject fileContent = new JSONObject();
            try {
                try {
                    fileContent = new JSONObject(adapterUtil.readJSONContentFromServer("LOANAPPLICATION/" + fileName));
                    logger.debug("fileContent 1: " + fileContent.toString());
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
                keysForContent = fileContent.getJSONObject("keysForContent");
                logger.debug("fileContent 2: " + keysForContent.toString());

            } catch (JSONException e) {
                logger.error(e.getMessage());
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                responseBody.setResponseObj(e.getMessage());
                response.setResponseHeader(responseHeader);
                response.setResponseBody(responseBody);
            }
            logger.debug("Fetching json files 2: " + keysForContent.toString());

            try {
				response = new LoanApplication().generateLoanApplicationPdf(applicationMasterData, applnDocumentList, custmrDataFields,
						keysForContent, language, kmId, bmId, kmSubmDateStr, bmSubmDateStr, usernameKM, usernameBM, "", "", "", "", "");
            } catch (Exception e) {
                logger.error(e.getMessage());
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                responseBody.setResponseObj(e.getMessage());
                response.setResponseHeader(responseHeader);
                response.setResponseBody(responseBody);
            }
        } else {
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            responseBody.setResponseObj(Constants.APP_MASTER_NOT_FOUND);
            response.setResponseHeader(responseHeader);
            response.setResponseBody(responseBody);
        }
        return response;
    }

    public JSONObject readSpecificJsonFile(String fileName) {
        JSONObject content = new JSONObject();
        try {
            // Use PathMatchingResourcePatternResolver to get resources from the data folder
            logger.debug("Fetching fileName: " + fileName);
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:*.json"); // Read all JSON files
            // in data folder
            logger.debug("Fetching resources: " + resources.toString());
            // Find the resource that matches the specific file name
            Optional<Resource> targetResource = Arrays.stream(resources)
					.filter(resource -> fileName.equals(resource.getFilename())).findFirst();
            logger.debug("Fetching targetResource: " + targetResource.toString());
            logger.debug("Fetching targetResource.getURI(): " + targetResource.get().getURI());
            if (targetResource.isPresent()) {
                // Read and parse the content of the specific file
                content = new JSONObject(new String(Files.readAllBytes(Paths.get(targetResource.get().getURI()))));
            } else {
                logger.debug("File not found: " + fileName);
            }
        } catch (IOException e) {
        }
        return content;
    }

    public Response mergeImageToPdfAndDownload(MergeImageToPdfRequestWrapper mergeImageToPdfRequestWrapper) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        try {
            String applicationId = mergeImageToPdfRequestWrapper.getApplicationId();
            String applicantType = mergeImageToPdfRequestWrapper.getApplicantType();
            String documentType = mergeImageToPdfRequestWrapper.getDocumentType();
            ObjectMapper objectMapper = new ObjectMapper();
            String PdfResponse = null;
            logger.debug("Inside mergeImageToPdfAndDownload method");
            Properties prop = null;

            prop = CommonUtils.readPropertyFile();
            String filePath = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + Constants.APPID
                    + Constants.LOANPATH + applicationId + "/";
            logger.debug("File path :: {}", filePath);
            Optional<List<ApplicationDocuments>> documentDetails = appLoanDocsRepository
                    .findByApplicationIdAndCustType(applicantType, applicationId);
            List<ApplicationDocumentsPayload> payloads = new ArrayList<>();
            if (documentDetails.isPresent()) {
                for (ApplicationDocuments document : documentDetails.get()) {
                    ApplicationDocumentsPayload documentPayload = objectMapper.readValue(document.getPayloadColumn(),
                            ApplicationDocumentsPayload.class);
					if (documentType.equalsIgnoreCase(documentPayload.getDocumentName())) {
                        payloads.add(documentPayload);
					} else if (documentType.equalsIgnoreCase(documentPayload.getDocumentType())){
						payloads.add(documentPayload);
					}
                }
                if (payloads.size() != 0) {
                    PdfResponse = CommonUtils.mergePDFFiles(StringUtils.EMPTY, payloads, filePath);
                    JSONObject resp = new JSONObject();
                    resp.put("status", "success");
                    resp.put("base64", PdfResponse);
                    responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                    responseBody.setResponseObj(resp.toString());
                } else {
                    responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                    responseBody.setResponseObj(ResponseCodes.BASE64_DATA_NOT_FOUND.getKey());
                }
            } else {
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                responseBody.setResponseObj(ResponseCodes.BASE64_DATA_NOT_FOUND.getKey());
            }
        } catch (Exception e) {
            logger.error("Error occurred while merging the PDF files: " + e.getMessage());
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            responseBody.setResponseObj(e.getMessage());
        }
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "rpcUploadLoanFallback")
    public Response uploadLoan(UploadLoanRequest uploadLoanRequest) {
        logger.debug("OnEntry :: uploadLoan");
        Gson gson = new Gson();
        Response fetchUserDetailsResponse = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        fetchUserDetailsResponse.setResponseHeader(responseHeader);
        ResponseBody responseBody = new ResponseBody();
        CustomerDataFields customerDataFields;
        try {
            Properties prop = null;
            try {
                prop = CommonUtils.readPropertyFile();
            } catch (IOException e) {
                logger.error("Error while reading property file in populateRejectedData ", e);
                fetchUserDetailsResponse = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
                        ResponseCodes.FAILURE.getKey());
            }

            UploadLoanRequestFields requestObj = uploadLoanRequest.getRequestObj();

            String applicationId = requestObj.getApplicationId();
            String appId = requestObj.getAppId();
            int versionNum = requestObj.getVersionNum();
            logger.debug("applicationID : " + applicationId.toString());
            logger.debug("appId : " + appId + ", versionNum : " + versionNum);
            logger.debug("customerType : " + requestObj.getCustomerType());

            ApplicationMaster applicationMasterData;
            Optional<ApplicationMaster> appMasterDb = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNum(
                    requestObj.getAppId(), requestObj.getApplicationId(), requestObj.getVersionNum());
            if (appMasterDb.isPresent()) {
                logger.debug("appMasterDb data found");
                applicationMasterData = appMasterDb.get();
                logger.debug("appMasterDb data found: " + applicationMasterData.toString());
            } else {
                logger.debug("appMasterDb data not found");
                responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
                responseBody.setResponseObj(Constants.APP_MASTER_NOT_FOUND);
                fetchUserDetailsResponse.setResponseBody(responseBody);
                return fetchUserDetailsResponse;
            }

            String stageId = requestObj.getStageId();
            logger.debug("stageId : " + stageId);
            logger.debug("customerType: " + requestObj.getCustomerType());
            String custCode = requestObj.getCustomerType().equalsIgnoreCase("Applicant") ? "A" : "C";
            logger.debug("custCode : " + custCode);
            logger.debug("requestType : " + requestObj.getRequestType());

            ApplicationMaster applicationMasterReq = null;
            switch (stageId) {
                case "1": // primary Kyc Details
                    logger.debug("Stage 1 : primary Kyc Details");
                    if (requestObj.getRequestType().equalsIgnoreCase("Edit")) {

                        applicationMasterReq = requestObj.getApplicationMaster();
                        logger.debug("applicationMasterReq: " + applicationMasterReq);
                        applicationMasterData.setPrimaryKycId(applicationMasterReq.getPrimaryKycId());
                        applicationMasterData.setAlternateVoterId(applicationMasterReq.getAlternateVoterId());

                        updateCustomerDtls(requestObj);
                        updateEditedFields(1, requestObj, custCode);
                    } else if (requestObj.getRequestType().equalsIgnoreCase("query")) {
                        updateQueries(1, requestObj, custCode);
                    } else {
                        updateStageVerification(1, requestObj, custCode);
                    }

                    break;

                case "2": // Secondary Kyc Details
                    if (requestObj.getRequestType().equalsIgnoreCase("Edit")) {
                        updateCustomerDtls(requestObj);
                        updateAddressDtls(2, requestObj, Constants.SECONDARY_KYC); // personal // present //secondary
                        updateEditedFields(2, requestObj, custCode);

                        applicationMasterReq = requestObj.getApplicationMaster();
                        applicationMasterData.setSecondaryKycType(applicationMasterReq.getSecondaryKycType());
                        applicationMasterData.setSecondaryKycId(applicationMasterReq.getSecondaryKycId());

                    } else if (requestObj.getRequestType().equalsIgnoreCase("query")) {
                        updateQueries(2, requestObj, custCode);
                    } else {
                        updateStageVerification(2, requestObj, custCode);
                    }
                    break;

                case "3": // Relationship Proof
                    if (requestObj.getRequestType().equalsIgnoreCase("Edit")) {
                        updateCustomerDtls(requestObj);
                        updateEditedFields(3, requestObj, custCode);
                    } else if (requestObj.getRequestType().equalsIgnoreCase("query")) {
                        updateQueries(3, requestObj, custCode);
                    } else {
                        updateStageVerification(3, requestObj, custCode);
                    }
                    break;

                case "4": // Residence Address Proof
                    if (requestObj.getRequestType().equalsIgnoreCase("Edit")) {
						updateCustomerDtls(requestObj);
                        updateAddressDtls(4, requestObj, "present"); // personal //present
                        updateEditedFields(4, requestObj, custCode);
                    } else if (requestObj.getRequestType().equalsIgnoreCase("query")) {
                        updateQueries(4, requestObj, custCode);
                    } else {
                        updateStageVerification(4, requestObj, custCode);
                    }
                    break;

                case "5": // Residence Photo
                    if (requestObj.getRequestType().equalsIgnoreCase("Edit")) {
                        updateAddressDtls(5, requestObj, "present"); // personal //present
                        updateEditedFields(5, requestObj, custCode);
                    } else if (requestObj.getRequestType().equalsIgnoreCase("query")) {
                        updateQueries(5, requestObj, custCode);
                    } else {
                        updateStageVerification(5, requestObj, custCode);
                    }
                    break;

                case "6": // Bussiness/Employment Proof
                    if (requestObj.getRequestType().equalsIgnoreCase("Edit")) {
                        updateOccupationdtls(requestObj);
						if(requestObj.getAddressDetailsWrapperList() != null) {
							updateAddressDtls(5, requestObj, "present");
						}
                        updateLoanDtls(requestObj);

                        updateEditedFields(6, requestObj, custCode);
                    } else if (requestObj.getRequestType().equalsIgnoreCase("query")) {
                        updateQueries(6, requestObj, custCode);
                    } else {
                        updateStageVerification(6, requestObj, custCode);
                    }
                    break;

                case "7": // Bussiness Address Proof
					custCode = requestObj.getCustomerType();
                    if (requestObj.getRequestType().equalsIgnoreCase("Edit")) {
                        updateAddressDtls(7, requestObj, "office");
                        updateOccupationdtls(requestObj);// occupation //office
						updateIncomeDetails(uploadLoanRequest);
                        updateEditedFields(7, requestObj, custCode);
                    } else if (requestObj.getRequestType().equalsIgnoreCase("query")) {
                        updateQueries(7, requestObj, custCode);
                    } else {
                        updateStageVerification(7, requestObj, custCode);
                    }
                    break;

                case "8": // Bank Account Proof
                    if (requestObj.getRequestType().equalsIgnoreCase("Edit")) {
                        updateBankDtls(requestObj);
                        updateEditedFields(8, requestObj, custCode);
                    } else if (requestObj.getRequestType().equalsIgnoreCase("query")) {
                        updateQueries(8, requestObj, custCode);
                    } else {
                        updateStageVerification(8, requestObj, custCode);
                    }
                    break;

                case "9": // Other Information
					if (requestObj.getRequestType().equalsIgnoreCase("Edit")) {
						insuranceDetailSave(requestObj);
						updateEditedFields(9, requestObj, custCode);
					} else if (requestObj.getRequestType().equalsIgnoreCase("query")) {
                        updateQueries(9, requestObj, custCode);
                    } else if (requestObj.getRequestType().equalsIgnoreCase("stageVerification")) {
                        updateStageVerification(9, requestObj, custCode);
                    }
                    break;

                case Constants.UPLOAD_DOCS:
                    uploadApplicationDocs(requestObj, applicationId, versionNum);
                    uploadDocument(requestObj.getUploadDocumentRequestFields(), prop);

                    break;

                case Constants.PENNY_CHECK_DOCS:
                    deleteDocument(applicationId, appId);
                    updateApplicationDocs(requestObj, applicationId, versionNum);
                    uploadDocument(requestObj.getUploadDocumentRequestFields(), prop);

                    break;

                case Constants.COAPPLICANT_BANK_DETAILS:
                    updateCoApplicantBankDtls(requestObj, applicationId, appId, versionNum);
                    break;

                case Constants.APPLICANT_BANK_DETAILS:
                    updateCoApplicantBankDtls(requestObj, applicationId, appId, versionNum);
                    break;
                default:
                    logger.error("Invalid StageId");
                    break;
            }

            logger.debug("Stage : " + stageId);
            if (!stageId.equalsIgnoreCase(Constants.COAPPLICANT_BANK_DETAILS)) {
                logger.debug("Stage1 : " + stageId);
                applicationMasterData.setUpdateTs(LocalDateTime.now());
                applicationMasterData.setUpdatedBy(uploadLoanRequest.getUserId());
                applicationMasterRepo.save(applicationMasterData);
                logger.debug("applicationMaster saved");
            }

            customerDataFields = cobService.getCustomerData(applicationMasterData, applicationId, appId, versionNum);
            logger.debug("customerDataFields  : {}", customerDataFields);
            Optional<BCMPIStageVerification> bcmpiStageData = bcmpiStageVerificationRepository.findById(applicationId);
            if (bcmpiStageData.isPresent()) {
                logger.debug("bcmpiStageData found");
				customerDataFields.setBcmpiStatDetails(CommonUtils.parseBCMPIStageVerificationData(
						bcmpiStageData.get().getEditedFields(), bcmpiStageData.get().getQueries()));
                customerDataFields.setBcmpiVerifiedStage(null != bcmpiStageData.get().getVerifiedStages()
						? Arrays.asList(bcmpiStageData.get().getVerifiedStages().split("\\|"))
						: new ArrayList<>());
            }
            Optional<BCMPIIncomeDetails> bcmpiIncomeDataOpt = bcmpiIncomeDetailsRepo.findById(applicationId);
            if (bcmpiIncomeDataOpt.isPresent()) {
                logger.debug("bcmpiIncomeData found");
                BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper = gson.fromJson(bcmpiIncomeDataOpt.get().getPayload(), BCMPIIncomeDetailsWrapper.class); // Object to be changed to a wrapper class
                BCMPIIncomeDetails bcmpiIncomeDetails = bcmpiIncomeDataOpt.get();
                bcmpiIncomeDetails.setBcmpiIncomeDetailsWrapper(bcmpiIncomeDetailsWrapper);
                customerDataFields.setBcmpiIncomeDetails(bcmpiIncomeDetails);
            }
            Optional<BCMPILoanObligations> bcmpiLoanObligationsOpt = bcmpiLoanObligationsRepo.findById(applicationId);
            if (bcmpiLoanObligationsOpt.isPresent()) {
                logger.debug("bcmpiLoanObligations found");
				LoanObligationsWrapper loanObligationsWrapper = gson
						.fromJson(bcmpiLoanObligationsOpt.get().getPayload(), LoanObligationsWrapper.class);
                BCMPILoanObligations bcmpiLoanObligations = bcmpiLoanObligationsOpt.get();
                bcmpiLoanObligations.setLoanObligationsWrapper(loanObligationsWrapper);
                customerDataFields.setBcmpiLoanObligations(bcmpiLoanObligations);
            }
            Optional<BCMPIOtherDetails> bcmpiOtherDetailsOpt = bcmpiOtherDetailsRepo.findById(applicationId);
            if (bcmpiOtherDetailsOpt.isPresent()) {
                logger.debug("bcmpiIncomeData found");
				BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = gson
						.fromJson(bcmpiOtherDetailsOpt.get().getPayload(), BCMPIOtherDetailsWrapper.class);
                BCMPIOtherDetails bcmpiOtherDetails = bcmpiOtherDetailsOpt.get();
                bcmpiOtherDetails.setBcmpiOtherDetailsWrapper(bcmpiOtherDetailsWrapper);
                customerDataFields.setBcmpiOtherDetails(bcmpiOtherDetails);
            }
            logger.debug("customerDataFields  :" + customerDataFields.toString());
            Optional<RpcStageVerification> rpcStageData = rpcStgVerificationRepo.findById(applicationId);
            if (rpcStageData.isPresent()) {
                logger.debug("rpcStageData found");
                customerDataFields.setRpcStatDetails(CommonUtils.parseRPCStageVerificationData(
                        rpcStageData.get().getEditedFields(), rpcStageData.get().getQueries()));
                customerDataFields.setVerifiedStage(null != rpcStageData.get().getVerifiedStages()
                        ? Arrays.asList(rpcStageData.get().getVerifiedStages().split("\\|"))
                        : new ArrayList<>());
            }
            Optional<List<Udhyam>> udhyamRecordsOpt = udhyamRepository.findByApplicationId(applicationId);
            if (udhyamRecordsOpt.isPresent()) {
                List<Udhyam> udhyamRecords = udhyamRecordsOpt.get();
                customerDataFields.setUdhyamDetails(udhyamRecords);
			}

			AMLQuestionnaireDetails amlQuestionnaireDetails = amlQuestionnaireRepo.findById(applicationId).orElse(null);
			if(amlQuestionnaireDetails!= null && amlQuestionnaireDetails.getPayloadColumn() != null){
				logger.debug("Fetched AML Questionnaire Details");
				amlQuestionnaireDetails.setPayload(
						gson.fromJson(amlQuestionnaireDetails.getPayloadColumn(), AmlQuestionnairePayload.class));
				customerDataFields.setAmlQuestion(amlQuestionnaireDetails);
            }

            Optional<DBKITStageVerification> dbKitStageData = dbkitStageVerificationRepository.findById(applicationId);
            if (dbKitStageData.isPresent()) {
                logger.debug("dbKitStageData found");
                try {
                    customerDataFields.setDbKitStatDetails(
                            CommonUtils.parseBCMPIStageVerificationData("", dbKitStageData.get().getQueries()));
                } catch (Exception e) {
                    logger.error("error while parsing queries in dbkit: {}", e.getMessage(), e);
                }
                customerDataFields.setDbKitVerifiedStage(null != dbKitStageData.get().getVerifiedStages()
						? Arrays.asList(dbKitStageData.get().getVerifiedStages().split("\\|"))
						: new ArrayList<>());
				customerDataFields.setDbKitResponse(
						gson.fromJson(dbKitStageData.get().getResponse(), new TypeToken<List<DBKITResponse>>() {
                }.getType()));
				customerDataFields.setApprovedDocs(
						gson.fromJson(dbKitStageData.get().getApprovedDocs(), new TypeToken<List<String>>() {
                }.getType()));
                customerDataFields.setDbVerificationQueries(
                        gson.fromJson(dbKitStageData.get().getQueryDocs(), new TypeToken<List<String>>() {
                        }.getType()));
            }

            String customerdata = gson.toJson(customerDataFields);
            customerdata = customerdata.replace(Constants.PAYLOAD_COLUMN, Constants.PAYLOAD);
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
            responseBody.setResponseObj(customerdata);
            fetchUserDetailsResponse.setResponseBody(responseBody);
            return fetchUserDetailsResponse;

        } catch (Exception e) {
            logger.error("Exception in uploadLoan : " + e.getMessage(), e);
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            responseBody.setResponseObj(e.getMessage());
            fetchUserDetailsResponse.setResponseBody(responseBody);
            return fetchUserDetailsResponse;
        }

    }

    private void updateCustomerDtls(UploadLoanRequestFields requestObj) {
        try {
            Gson gson = new Gson();
            String payload;
            logger.debug("Onentry :: updateCustomerDtls");
            // Get the list of customer details from the request object
            List<CustomerDetails> customerDetailsList = requestObj.getCustomerDetailsList();
            logger.debug("customerDetailsList size :" + customerDetailsList.size());
            for (CustomerDetails customerDetails : customerDetailsList) {
                logger.debug("inside customerDetailsList : " + customerDetails.toString());
                // Check if a record exists for the given ApplicationId and CustomerType
                Optional<CustomerDetails> existingDetails = custDtlRepo.findByApplicationIdAndCustomerType(
                        requestObj.getApplicationId(), requestObj.getCustomerType());
                logger.debug("existingDetails of customerDetails" + existingDetails.toString());
                if (existingDetails.isPresent()) {
                    logger.debug("data found for customerDetails " + "applicationId :" + requestObj.getApplicationId()
                            + "and " + requestObj.getCustomerType());
                    // Fetch the existing record
                    CustomerDetails existingCustomerDetails = existingDetails.get();
                    logger.debug("existingCustomerDetails : " + existingCustomerDetails.toString());
                    // Update the fields with new values

                    existingCustomerDetails.setVersionNum(Constants.INITIAL_VERSION_NO);
					existingCustomerDetails.setCustomerName(customerDetails.getPayload().getFirstName() + " "
							+ customerDetails.getPayload().getLastName());
                    existingCustomerDetails.setMobileNumber(customerDetails.getMobileNumber());
                    existingCustomerDetails.setKycStatus(customerDetails.getKycStatus());
                    existingCustomerDetails.setAmlStatus(customerDetails.getAmlStatus());

                    payload = gson.toJson(customerDetails.getPayload());
                    logger.debug("existingCustomerDetails : payload : " + payload);
                    existingCustomerDetails.setPayloadColumn(payload);

                    // Save the updated record
                    custDtlRepo.save(existingCustomerDetails);
					logger.debug("Checking for InsuranceDetails with applicationId: {} and excluding custDtlId: {}",
							requestObj.getApplicationId(), existingCustomerDetails.getCustDtlId());
					Optional<InsuranceDetails> insuranceDetailsOpt = insuranceDtlRepo
							.findByApplicationIdAndCustDtlIdNot(requestObj.getApplicationId(),
									existingCustomerDetails.getCustDtlId());
                    if (insuranceDetailsOpt.isPresent()) {
                        InsuranceDetails insuranceDetails = insuranceDetailsOpt.get();
                        logger.debug("InsuranceDetails found: {}", insuranceDetails);
						InsuranceDetailsPayload insuranceDetailsPayload = gson
								.fromJson(insuranceDetails.getPayloadColumn(), InsuranceDetailsPayload.class);
						logger.debug("InsuranceDetailsPayload nomineeRelation: {}, customer relationship: {}",
								insuranceDetailsPayload.getNomineeRelation(),
								customerDetails.getPayload().getRelationShipWithApplicant());
                        if (StringUtils.isNoneEmpty(customerDetails.getPayload().getRelationShipWithApplicant())) {
							if (insuranceDetailsPayload.getNomineeRelation()
									.equalsIgnoreCase(customerDetails.getPayload().getRelationShipWithApplicant())) {
                                logger.debug("Nominee relation matches, updating nominee name and dob.");
								insuranceDetailsPayload.setNomineeName(customerDetails.getPayload().getFirstName() + " "
										+ customerDetails.getPayload().getLastName());
                                insuranceDetailsPayload.setNomineeDob(customerDetails.getPayload().getDob());
                                insuranceDetailsPayload.setAge(customerDetails.getPayload().getAge());
                                String insurancePayloadColumn = gson.toJson(insuranceDetailsPayload);
                                insuranceDetails.setPayloadColumn(insurancePayloadColumn);
                                logger.debug("Saving updated InsuranceDetails: {}", insuranceDetails);
                                insuranceDtlRepo.save(insuranceDetails);
                            }
                        } else {
							Optional<CustomerDetails> coAppCustDetailsopt = custDtlRepo
									.findByApplicationIdAndAppIdAndCustomerType(requestObj.getApplicationId(),
                                    requestObj.getAppId(), Constants.COAPPLICANT);
							logger.debug(
									"Checking for Co-Applicant CustomerDetails with applicationId: {} and appId: {}",
									requestObj.getApplicationId(), requestObj.getAppId());
                            if (coAppCustDetailsopt.isPresent()) {
                                logger.debug("Co-Applicant CustomerDetails found: {}", coAppCustDetailsopt.get());
                                CustomerDetails coAppDetails = coAppCustDetailsopt.get();
								CustomerDetailsPayload coAppDetailsPayload = gson
										.fromJson(coAppDetails.getPayloadColumn(), CustomerDetailsPayload.class);
								logger.debug(
										"Co-Applicant CustomerDetailsPayload nomineeRelation: {}, customer relationship: {}",
										insuranceDetailsPayload.getNomineeRelation(),
										coAppDetailsPayload.getRelationShipWithApplicant());
								if (coAppDetailsPayload.getRelationShipWithApplicant().equalsIgnoreCase("Father")
										&& insuranceDetailsPayload.getNomineeRelation().equalsIgnoreCase("Daughter")
										|| coAppDetailsPayload.getRelationShipWithApplicant().equalsIgnoreCase("Mother")
												&& insuranceDetailsPayload.getNomineeRelation()
														.equalsIgnoreCase("Daughter")
										|| coAppDetailsPayload.getRelationShipWithApplicant().equalsIgnoreCase("Son")
												&& insuranceDetailsPayload.getNomineeRelation()
														.equalsIgnoreCase("Mother")
										|| coAppDetailsPayload.getRelationShipWithApplicant()
												.equalsIgnoreCase("Daughter-in-law")
												&& insuranceDetailsPayload.getNomineeRelation()
														.equalsIgnoreCase("Mother-In-law")
										|| coAppDetailsPayload.getRelationShipWithApplicant().equalsIgnoreCase("Spouse")
												&& insuranceDetailsPayload.getNomineeRelation()
														.equalsIgnoreCase("Spouse")) {
									logger.debug(
											"Nominee relation: {}, corresponds with Co-Applicant: {}, updating nominee name and dob.",
											insuranceDetailsPayload.getNomineeRelation(),
											coAppDetailsPayload.getRelationShipWithApplicant());
									insuranceDetailsPayload.setNomineeName(customerDetails.getPayload().getFirstName()
											+ " " + customerDetails.getPayload().getLastName());
                                    insuranceDetailsPayload.setNomineeDob(customerDetails.getPayload().getDob());
                                    insuranceDetailsPayload.setAge(customerDetails.getPayload().getAge());
                                    String insurancePayloadColumn = gson.toJson(insuranceDetailsPayload);
                                    insuranceDetails.setPayloadColumn(insurancePayloadColumn);
                                    logger.debug("Saving updated InsuranceDetails: {}", insuranceDetails);
                                    insuranceDtlRepo.save(insuranceDetails);
                                }
                            }
                        }
                    }
                    logger.warn("Completed updating customer details - TB_ABOB_CUSTOMER_DETAILS for loans");
                } else {
                    logger.warn("No matching record found for ApplicationId: " + requestObj.getApplicationId()
                            + ", CustomerType: " + requestObj.getCustomerType() + ". Update skipped.");
                }
            }
        } catch (Exception e) {
            logger.error("Exception in updateCustomerDtls: " + e.getMessage(), e);
        }
    }

    private void updateOccupationdtls(UploadLoanRequestFields requestObj) {
        logger.debug("Onentry :: updateOccupationdtls");
        Gson gson = new Gson();
        try {
            List<OccupationDetailsWrapper> occupationDetailsWrapperList = requestObj.getOccupationDetailsWrapperList();

            for (OccupationDetailsWrapper occupationDetailsWrapper : occupationDetailsWrapperList) {
                logger.debug("inside occupationDetailsWrapperList : " + occupationDetailsWrapper.toString());
                Optional<OccupationDetails> occupationDetailsDb = occupationDtlRepo
                        .findOccupationDetailsByCustomerTypeForRpc(requestObj.getCustomerType(),
                                requestObj.getApplicationId());

                if (occupationDetailsDb.isPresent()) {
					logger.debug("data found for occupation Details " + "applicationId :"
							+ requestObj.getApplicationId() + "and " + requestObj.getCustomerType());
                    OccupationDetails existingRecord = occupationDetailsDb.get(); // Get existing record

                    existingRecord.setVersionNum(Constants.INITIAL_VERSION_NO);
                    String payload = gson.toJson(occupationDetailsWrapper.getOccupationDetails().getPayload());
                    existingRecord.setPayloadColumn(payload);

                    occupationDtlRepo.save(existingRecord); // Save the updated record
                    logger.warn("Completed updating occupation Details - TB_ABOB_OCCUPATION_DETAILS for loans");

                    Optional<OccupationDetails> occupationDetailsDbCoApp = occupationDtlRepo
                            .findOccupationDetailsByCustomerTypeForRpc(Constants.COAPPLICANT,
                                    requestObj.getApplicationId());

                    if (occupationDetailsDbCoApp.isPresent()) {
						logger.debug("data found for customerDetails " + "applicationId :"
								+ requestObj.getApplicationId() + "and " + Constants.COAPPLICANT);
                        OccupationDetails existingRecordCoApp = occupationDetailsDbCoApp.get(); // Get existing record

						OccupationDetailsPayload occpnPayloadDbCoApp = gson
								.fromJson(existingRecordCoApp.getPayloadColumn(), OccupationDetailsPayload.class);
						if (occpnPayloadDbCoApp.getOccupationTag() != null
								&& occpnPayloadDbCoApp.getOccupationTag().equalsIgnoreCase("YES")) {
                            existingRecordCoApp.setVersionNum(Constants.INITIAL_VERSION_NO);
							String payloadCoApp = gson
									.toJson(occupationDetailsWrapper.getOccupationDetails().getPayload());

                            OccupationDetailsPayload occpnPayloadDbCoApp1 = gson.fromJson(payloadCoApp,
                                    OccupationDetailsPayload.class);

                            occpnPayloadDbCoApp1.setOccupationTag("YES");
                            String payloadCoApp1 = gson.toJson(occpnPayloadDbCoApp1);

                            existingRecordCoApp.setPayloadColumn(payloadCoApp1);

                            occupationDtlRepo.save(existingRecordCoApp); // Save the updated record
							logger.warn(
									"Completed updating occupation Details for Co-Applicant - TB_ABOB_OCCUPATION_DETAILS for loans");
                        }
                    }
                } else {
                    logger.debug("record not found for Occupation Details " + "applicationId :"
                            + requestObj.getApplicationId() + "and " + requestObj.getCustomerType());
                }
            }
        } catch (Exception e) {
            logger.error("Exception in updateOccupationdtls: " + e.getMessage(), e);
        }
    }

	private void updateIncomeDetails(UploadLoanRequest request) {
		try {
			String userId = request.getUserId();
			UploadLoanRequestFields requestObj = request.getRequestObj();
			Gson gson = new Gson();
			BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapperDetails = request.getRequestObj()
					.getBcmpiIncomeDetailsWrapper();
			if(bcmpiIncomeDetailsWrapperDetails == null)
				return;
			String payloadStringified = gson.toJson(bcmpiIncomeDetailsWrapperDetails);
			Optional<BCMPIIncomeDetails> existingIncomeDetailsOpt = bcmpiIncomeDetailsRepo
					.findById(requestObj.getApplicationId());
			if (existingIncomeDetailsOpt.isPresent()) {
				logger.debug("Existing BCMPIIncomeDetails record found for ApplicationId: {}",
						requestObj.getApplicationId());
				BCMPIIncomeDetails existingIncomeDetails = existingIncomeDetailsOpt.get();
				existingIncomeDetails.setPayload(payloadStringified);
				existingIncomeDetails.setUpdateTs(LocalDateTime.now());
				existingIncomeDetails.setUpdatedBy(userId);
				bcmpiIncomeDetailsRepo.save(existingIncomeDetails);
				logger.debug("Updated existing BCMPIIncomeDetails record: {}", existingIncomeDetails);
			} else {
				logger.debug("record not found for Income Details " + "applicationId :" + requestObj.getApplicationId()
						+ "and " + requestObj.getCustomerType());
			}
		}catch (Exception e ){
			logger.error("Exception in updateIncomedtls: " + e.getMessage(), e);
		}
	}

    private void updateBankDtls(UploadLoanRequestFields requestObj) {
        logger.debug("Onentry :: updateBankDtls");
        Gson gson = new Gson();
        try {
            List<BankDetailsWrapper> bankDetailsWrapperList = requestObj.getBankDetailsWrapperList();
            for (BankDetailsWrapper bankDetailsWrapper : bankDetailsWrapperList) {
                logger.debug("inside bankDetailsWrapperList :: getBankDetails: "
                        + bankDetailsWrapper.getBankDetails().toString());
//				Optional<BankDetails> bankDetailsDb = bankDtlRepo.findByApplicationId(requestObj.getApplicationId());
				Optional<BankDetails> bankDetailsDb = bankDtlRepo
						.findBankDetailsByCustomerType(requestObj.getCustomerType(), requestObj.getApplicationId());
                if (bankDetailsDb.isPresent()) {
                    logger.debug("updateBankDtls data found for the applicationId : " + requestObj.getApplicationId());
                    BankDetails existingRecord = bankDetailsDb.get(); // Fetch the existing record
                    existingRecord.setVersionNum(Constants.INITIAL_VERSION_NO);
                    logger.debug("existingRecord : " + existingRecord.toString());

					boolean isAccountNoPresent = requestObj.getEditedFields().stream().anyMatch(s -> s.equalsIgnoreCase("accountNumber"))
							|| requestObj.getEditedFields().stream().anyMatch(s -> s.equalsIgnoreCase("accountName"))
							|| requestObj.getEditedFields().stream().anyMatch(s -> s.equalsIgnoreCase("ifsc"));

                    BankDetailsPayload bankPayloadDb = gson.fromJson(existingRecord.getPayloadColumn(),
                            BankDetailsPayload.class);

                    BankDetailsPayload bankDetailsPayloadReq = bankDetailsWrapper.getBankDetails().getPayload();
                    if (isAccountNoPresent) {
                        logger.debug("accountNumber found in EditedFields() ");
                        deleteDocument(requestObj.getApplicationId(), requestObj.getAppId());
                        bankPayloadDb.setPennyCheckStatus("");
                        bankPayloadDb.setPennyResp("");
                        bankPayloadDb.setAccountNumber(bankDetailsPayloadReq.getAccountNumber());
                    }

                    bankPayloadDb.setAccountName(bankDetailsPayloadReq.getAccountName());
                    bankPayloadDb.setAccountType(bankDetailsPayloadReq.getAccountType());
                    bankPayloadDb.setBankName(bankDetailsPayloadReq.getBankName());
                    bankPayloadDb.setIfsc(bankDetailsPayloadReq.getIfsc());
                    bankPayloadDb.setBranchName(bankDetailsPayloadReq.getBranchName());
                    bankPayloadDb.setEditBankDetails(bankDetailsPayloadReq.getEditBankDetails());

                    bankPayloadDb.setRpcEditCheck(bankDetailsPayloadReq.isRpcEditCheck());
                    bankPayloadDb.setReEnterAccountNumber(bankDetailsPayloadReq.getReEnterAccountNumber());
                    bankPayloadDb.setRpcaccntVerified(bankDetailsPayloadReq.getRpcaccntVerified());
					bankPayloadDb.setBankdocumentType(bankDetailsPayloadReq.getBankdocumentType());
					/* updated changes 25-05-2026 */
					bankPayloadDb.setPrimaryENachStatus(bankDetailsPayloadReq.getPrimaryENachStatus());
					bankPayloadDb.setPrimaryPennyCheckStatus(bankDetailsPayloadReq.getPrimaryPennyCheckStatus());
					bankPayloadDb.setSecondaryENachStatus(bankDetailsPayloadReq.getSecondaryENachStatus());
					bankPayloadDb.setIsAccountVerified(bankDetailsPayloadReq.getIsAccountVerified());

                    String payloadStr = gson.toJson(bankPayloadDb);
                    existingRecord.setPayloadColumn(payloadStr);

                    bankDtlRepo.save(existingRecord); // Save the updated record
                    logger.warn("Completed updating Bank Details - TB_CGOB_BANK_DTLS for loans");
                } else {
                    logger.debug("record not found for bankDetails " + "applicationId :" + requestObj.getApplicationId()
                            + "and " + requestObj.getCustomerType());
                }
            }
        } catch (Exception e) {
            logger.error("Exception in updateBankDtls: " + e.getMessage(), e);
        }
    }

    public void updateLoanDtls(UploadLoanRequestFields requestObj) {
        logger.debug("Onentry :: updateOccupationdtls");
        Gson gson = new Gson();
        String payload;
        try {
            LoanDetails loanDtlObjReq = requestObj.getLoanDetails();
            logger.debug("loanDtlObjReq: " + loanDtlObjReq.toString());
            if (loanDtlObjReq != null) {
                Optional<LoanDetails> loanDetailsDb = loanDtlsRepo
                        .findTopByApplicationIdAndAppId(requestObj.getApplicationId(), Constants.APPID);

                if (loanDetailsDb.isPresent()) {
                    logger.debug("data found: ");
                    LoanDetails existingLoanDtl = loanDetailsDb.get(); // Fetch existing record
                    logger.debug("existingLoanDtl : " + existingLoanDtl.toString());
                    // Update
                    existingLoanDtl.setVersionNum(Constants.INITIAL_VERSION_NO);
                    existingLoanDtl.setLoanAmount(loanDtlObjReq.getLoanAmount());
                    existingLoanDtl.setTenureInMonths(loanDtlObjReq.getTenureInMonths());
                    existingLoanDtl.setTenure(loanDtlObjReq.getTenure());
                    existingLoanDtl.setRoi(loanDtlObjReq.getRoi());
                    existingLoanDtl.setInterest(loanDtlObjReq.getInterest());
                    existingLoanDtl.setTotPayableAmount(loanDtlObjReq.getTotPayableAmount());

                    existingLoanDtl.setAutoEmiAccount(loanDtlObjReq.getAutoEmiAccount());
                    existingLoanDtl.setAutoEmiAccountType(loanDtlObjReq.getAutoEmiAccountType());
                    existingLoanDtl.setMonthlyEmi(loanDtlObjReq.getMonthlyEmi());
                    existingLoanDtl.setLoanClosureDate(loanDtlObjReq.getLoanClosureDate());
                    existingLoanDtl.setEmiDate(loanDtlObjReq.getEmiDate());
                    existingLoanDtl.setLoanClosureDate(loanDtlObjReq.getLoanClosureDate());
                    existingLoanDtl.setLoanCrAccount(loanDtlObjReq.getLoanCrAccount());
                    existingLoanDtl.setLoanCrAccountType(loanDtlObjReq.getLoanCrAccountType());

                    payload = gson.toJson(loanDtlObjReq.getPayload());
                    existingLoanDtl.setPayloadColumn(payload);

                    loanDtlsRepo.save(existingLoanDtl); // Save the updated record
                    logger.warn("Completed updating Loan Details");
                } else {
                    logger.debug("record not found for Loan Details " + "applicationId :"
                            + requestObj.getApplicationId() + "and " + requestObj.getCustomerType());
                }
            }
        } catch (Exception e) {
            logger.error("Exception in updateLoanDtls: " + e.getMessage(), e);
        }
    }

    private void updateAddressDtls(Integer stageId, UploadLoanRequestFields requestObj, String addrType) {
        logger.debug("OnEntry :: updateAddressDtls : stageId :" + stageId + ", addrType :" + addrType);

        Gson gsonObj = new Gson();
        try {
            List<AddressDetailsWrapper> addressDetailsWrapperList = requestObj.getAddressDetailsWrapperList();
            String businessAddressProof = "";
            for (AddressDetailsWrapper addressDetailsWrapper : addressDetailsWrapperList) {
                List<AddressDetails> addressDetailsList = addressDetailsWrapper.getAddressDetailsList();
                logger.debug("addressDetailsList : " + addressDetailsList.toString());

                for (AddressDetails addressDetails : addressDetailsList) {
                    logger.debug("addressDetails : " + addressDetails.toString());
                    // Fetch existing AddressDetails from DB based on criteria
                    Optional<AddressDetails> existingAddressOpt = addressDtlRepo
                            .findAddressByCustomerTypeAndAddressTypeAndApplicationIdForRpc(requestObj.getCustomerType(),
                                    addressDetails.getAddressType(), requestObj.getApplicationId());
                    logger.debug("existingAddressOpt : " + existingAddressOpt.toString());
                    if (existingAddressOpt.isPresent()) {
                        logger.debug("existingAddressOpt data found : ");
                        AddressDetails existingAddress = existingAddressOpt.get();
                        logger.debug("existingAddress : " + existingAddress.toString());

                        AddressDetailsPayload addressPayload = addressDetails.getPayload();
                        logger.debug("addressPayloadReq : " + addressPayload.toString());
                        List<Address> addrPayLoadLstEdt = addressPayload.getAddressList();
                        Address addressEdt = addrPayLoadLstEdt.get(0);
                        logger.debug("addressEdtReq : " + addressEdt.toString());

                        List<Address> addrPayLoadLstDb = null;
                        AddressDetailsPayload addressPayloadDb = gsonObj.fromJson(existingAddress.getPayloadColumn(),
                                AddressDetailsPayload.class);
                        addrPayLoadLstDb = addressPayloadDb.getAddressList();
                        logger.debug("addrPayLoadLstDb : " + addrPayLoadLstDb.toString());

                        boolean addressFound = false;
                        for (int i = 0; i < addrPayLoadLstDb.size(); i++) {
                            Address addr = addrPayLoadLstDb.get(i);
							Boolean businessIndexCheck = addressEdt.getBusinessIndex() == null ? addr.getBusinessIndex() == null : addressEdt.getBusinessIndex().equalsIgnoreCase(addr.getBusinessIndex());

							if (addr.getAddressType().equalsIgnoreCase(addrType) && businessIndexCheck) {
                                // Update the existing address
                                addrPayLoadLstDb.set(i, addressEdt);
                                addressFound = true;
                                logger.info("Address of type '" + addrType + "' found, updating - address.");
                                break;
                            }
                        }

                        // If address not found, insert the new address
                        if (!addressFound && addrType.equalsIgnoreCase(Constants.SECONDARY_KYC)) {
                            addrPayLoadLstDb.add(addressEdt); // Add new address to the list
                            logger.info(
                                    "Address of type '" + addrType + "' not found, so added a new record - address.");
                        }

                        // Update sameAs address (PERMANENT or COMMUNICATION)
						if (addressEdt.getAddressType().equalsIgnoreCase(Constants.PRESENT)
								&& requestObj.getCustomerType().equalsIgnoreCase(Constants.APPLICANT)
								&& addressDetails.getAddressType().equalsIgnoreCase(Constants.PERSONAL)) { // (addrType.equalsIgnoreCase(Constants.PRESENT){
                            businessAddressProof = addressEdt.getCurrentAddressProof();
							Optional<OccupationDetails> applicantOccupationDetailsOpt = occupationDtlRepo
									.findOccupationDetailsByCustomerTypeForRpc(Constants.APPLICANT,
											requestObj.getApplicationId());
                            if (applicantOccupationDetailsOpt.isPresent()) {
                                OccupationDetails applicantOccupationDetails = applicantOccupationDetailsOpt.get();
								OccupationDetailsPayload occupationDetailsPayload = gsonObj.fromJson(
										applicantOccupationDetails.getPayloadColumn(), OccupationDetailsPayload.class);
                                occupationDetailsPayload.setBusinessAddressProof(businessAddressProof);
                                String occupationPayloadStr = gsonObj.toJson(occupationDetailsPayload);
                                applicantOccupationDetails.setPayloadColumn(occupationPayloadStr);
                                occupationDtlRepo.save(applicantOccupationDetails);
								logger.info("Updated business address proof in occupation details for applicant: "
										+ applicantOccupationDetails.toString());
                            }
                            for (Address addr : addrPayLoadLstDb) {
								if ((addr.getAddressType().equalsIgnoreCase(Constants.PERMANENT)
										&& addr.getAddressSameAs().equalsIgnoreCase("Y"))
										|| (addr.getAddressType().equalsIgnoreCase(Constants.COMMUNICATION)
												&& addr.getAddressSameAs().equalsIgnoreCase(Constants.PRESENT))) {

                                    copyAddressDetails(addressEdt, addr);

                                    logger.info("Copied address - '" + addr.getAddressType() + "'.");
                                    //break;//?
                                }
                            }

                        }

                        // Convert payload to JSON string and set it
                        addressPayloadDb.setAddressList(addrPayLoadLstDb);
                        String updatedPayload = gsonObj.toJson(addressPayloadDb);
                        existingAddress.setPayloadColumn(updatedPayload);
                        addressDtlRepo.save(existingAddress);
						logger.info(
								"Updated occupation address details of Co-Applicant: " + existingAddress.toString());

                        // Update OCCUPATION address if sameAs PRESENT
						if (addressEdt.getAddressType().equalsIgnoreCase(Constants.PRESENT)
								&& requestObj.getCustomerType().equalsIgnoreCase(Constants.APPLICANT)) { // (addrType.equalsIgnoreCase(Constants.PRESENT){
                            Optional<AddressDetails> existingAddressOccpnObj = addressDtlRepo
									.findAddressByCustomerTypeAndAddressTypeAndApplicationIdForRpc(Constants.APPLICANT,
											Constants.OCCUPATION, requestObj.getApplicationId());

                            logger.debug("existingAddressOccpnObj : " + existingAddressOccpnObj.toString());
                            if (existingAddressOccpnObj.isPresent()) {
                                logger.debug("existingAddressOccpn data found : ");
                                AddressDetails existingAddrOccpn = existingAddressOccpnObj.get();

                                List<Address> addrPayLoadLstDbOccpn = null;
								AddressDetailsPayload occpnAddrPayLoadLstDb = gsonObj
										.fromJson(existingAddrOccpn.getPayloadColumn(), AddressDetailsPayload.class);

                                addrPayLoadLstDbOccpn = occpnAddrPayLoadLstDb.getAddressList();
                                logger.debug("addrPayLoadLstDbOccpn : " + addrPayLoadLstDbOccpn.toString());

                                for (Address addr : addrPayLoadLstDbOccpn) {
                                    String addressSameAs = Optional.ofNullable(addr.getAddressSameAs()).orElse("");
									if (addr.getAddressType().equalsIgnoreCase(Constants.OFFICE)
											&& addressSameAs.equalsIgnoreCase(Constants.PRESENT)) {

                                        copyAddressDetails(addressEdt, addr);

                                        break;

                                    }
                                }

                                // Convert updated personal address payload to JSON and save
                                occpnAddrPayLoadLstDb.setAddressList(addrPayLoadLstDbOccpn);
                                String updtOccpnAddrPayLoadLstDb = gsonObj.toJson(occpnAddrPayLoadLstDb);
                                existingAddrOccpn.setPayloadColumn(updtOccpnAddrPayLoadLstDb);
                                addressDtlRepo.save(existingAddrOccpn);
								logger.info("Updated occupation address details of applicant: "
										+ existingAddrOccpn.toString());
                            } else {
                                logger.debug("existingAddress data not found : ");
                            }
                        }

//				   // Update PERSONAL with OFFICE if sameAs COMMUNICATION or PERMANENT
                        if (Constants.OFFICE.equalsIgnoreCase(addressEdt.getAddressType())) {

                            logger.debug("addressEdt.getAddressType() :" + addressEdt.getAddressType());
                            if (addressEdt.getAddressSameAs().equalsIgnoreCase(Constants.COMMUNICATION)
                                    || addressEdt.getAddressSameAs().equalsIgnoreCase(Constants.PERMANENT)) {
                                logger.debug("addressEdt.getAddressSameAs()" + addressEdt.getAddressSameAs());
                                Optional<AddressDetails> existingAddressPersonalObj = addressDtlRepo
                                        .findAddressByCustomerTypeAndAddressTypeAndApplicationIdForRpc(
                                                requestObj.getCustomerType(), Constants.PERSONAL,
                                                requestObj.getApplicationId());

                                logger.debug("existingAddressPersonal : " + existingAddressPersonalObj.toString());
                                if (existingAddressPersonalObj.isPresent()) {
                                    logger.debug("existingAddressPersonal data found : ");
                                    AddressDetails existingAdrPersonal = existingAddressPersonalObj.get();

                                    List<Address> addrPayLoadLstDbPr = null;
                                    AddressDetailsPayload personalAddressPayload = gsonObj.fromJson(
                                            existingAdrPersonal.getPayloadColumn(), AddressDetailsPayload.class);
                                    addrPayLoadLstDbPr = personalAddressPayload.getAddressList();
                                    logger.debug("addrPayLoadLstDbPersonal : " + addrPayLoadLstDbPr.toString());

                                    for (Address addr : addrPayLoadLstDbPr) {
                                        if (addr.getAddressType().equalsIgnoreCase(addressEdt.getAddressSameAs())) {

                                            copyAddressDetails(addressEdt, addr);

                                            logger.info("Copied 'Office' address - '" + addr.getAddressType() + "'.");
                                            break;
                                        }
                                    }

                                    // Convert updated personal address payload to JSON and save
                                    personalAddressPayload.setAddressList(addrPayLoadLstDbPr);
                                    String updatedPersonalPayload = gsonObj.toJson(personalAddressPayload);
                                    existingAdrPersonal.setPayloadColumn(updatedPersonalPayload);
                                    addressDtlRepo.save(existingAdrPersonal);
                                    logger.info("Updated personal address details: " + existingAdrPersonal.toString());
                                }
                            }
                        }

                        //updating Co-applicant Address if request Address is Present
						if (addressEdt.getAddressType().equalsIgnoreCase(Constants.PRESENT)) { // if
																								// (addrType.equalsIgnoreCase(Constants.PRESENT))
																								// {

							// updating co-applicant address //applicant and co-applicant present address
							// should be same.
                            Optional<AddressDetails> existingAddressCoOpt = addressDtlRepo
									.findAddressByCustomerTypeAndAddressTypeAndApplicationIdForRpc(
											Constants.COAPPLICANT, Constants.PERSONAL, requestObj.getApplicationId()); // addressDetails.getAddressType()
                            logger.debug("existingAddressCoAplicant : " + existingAddressCoOpt.toString());

                            List<Address> addrPayLoadLstDbCo = null;
                            AddressDetailsPayload addressPayloadDbCo = null;
                            if (existingAddressCoOpt.isPresent()) {
                                logger.debug("existingAddressCo data found : ");
                                AddressDetails existingAddressCo = existingAddressCoOpt.get();
                                logger.debug("existingAddressCo : " + existingAddress.toString());

                                addressPayloadDbCo = gsonObj.fromJson(existingAddressCo.getPayloadColumn(),
                                        AddressDetailsPayload.class);
                                addrPayLoadLstDbCo = addressPayloadDbCo.getAddressList();
                                logger.debug("addrPayLoadLstDbCo : " + addrPayLoadLstDbCo.toString());

                                for (int i = 0; i < addrPayLoadLstDbCo.size(); i++) {
                                    Address addr = addrPayLoadLstDbCo.get(i);

                                    if (addr.getAddressType().equalsIgnoreCase(addrType)) {
                                        // Update the existing address
                                        addrPayLoadLstDbCo.set(i, addressEdt);
                                        logger.info("Address of type '" + addrType + "' found, updating - address.");
                                        break; // Exit after updating the address
                                    }
                                }

                                //updating same as Address
                                for (Address addr : addrPayLoadLstDbCo) {
									if ((addr.getAddressType().equalsIgnoreCase(Constants.PERMANENT)
											&& addr.getAddressSameAs().equalsIgnoreCase("Y"))
											|| (addr.getAddressType().equalsIgnoreCase(Constants.COMMUNICATION)
													&& addr.getAddressSameAs().equalsIgnoreCase(Constants.PRESENT))) {

                                        copyAddressDetails(addressEdt, addr);
                                        logger.info("Copied address - '" + addr.getAddressType() + "'.");
                                        //break;//?
                                    }
                                }

                                // Convert payload to JSON string and set it
                                addressPayloadDbCo.setAddressList(addrPayLoadLstDbCo);
                                String updatedPayloadCo = gsonObj.toJson(addressPayloadDbCo);
                                existingAddressCo.setPayloadColumn(updatedPayloadCo);
                                addressDtlRepo.save(existingAddressCo);

                                logger.warn("Address updated for Co-Applicant - personal");
                            } else {
                                logger.debug("existingAddressCo data not found : ");
                            }
                            //
                            //if(addressEdt.getAddressSameAs().equalsIgnoreCase(Constants.OCCUPATION)){
                            Optional<AddressDetails> existingAddressOccpnObjCo = addressDtlRepo
                                    .findAddressByCustomerTypeAndAddressTypeAndApplicationIdForRpc(
											Constants.COAPPLICANT, Constants.OCCUPATION, requestObj.getApplicationId());

                            logger.debug("existingAddressOccpnObjCo : " + existingAddressOccpnObjCo.toString());
                            if (existingAddressOccpnObjCo.isPresent()) {
                                logger.debug("existingAddressOccpn data found : ");
                                AddressDetails existingAddrOccpnCo = existingAddressOccpnObjCo.get();

                                List<Address> addrPayLoadLstDbOccpnCo = null;
								AddressDetailsPayload occpnAddrPayLoadLstDbCo = gsonObj
										.fromJson(existingAddrOccpnCo.getPayloadColumn(), AddressDetailsPayload.class);

                                addrPayLoadLstDbOccpnCo = occpnAddrPayLoadLstDbCo.getAddressList();
                                logger.debug("addrPayLoadLstDbOccpn : " + addrPayLoadLstDbOccpnCo.toString());

                                for (Address addr : addrPayLoadLstDbOccpnCo) {
									if (addr.getAddressType().equalsIgnoreCase(Constants.OFFICE)
											&& addr.getAddressSameAs().equalsIgnoreCase(Constants.PRESENT)) {

                                        copyAddressDetails(addressEdt, addr);

                                        break;
                                    }
                                }

                                // Convert updated personal address payload to JSON and save
                                occpnAddrPayLoadLstDbCo.setAddressList(addrPayLoadLstDbOccpnCo);
                                String updtOccpnAddrPayLoadLstDbCo = gsonObj.toJson(occpnAddrPayLoadLstDbCo);
                                existingAddrOccpnCo.setPayloadColumn(updtOccpnAddrPayLoadLstDbCo);
                                addressDtlRepo.save(existingAddrOccpnCo);
								logger.info("Updated occupation address details of Co-Applicant: "
										+ existingAddrOccpnCo.toString());
                            }

                        }

                    } else {
                        logger.warn("No existing Address Details found for Application ID: "
                                + requestObj.getApplicationId() + ", Address Type: " + addressDetails.getAddressType());
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Exception in updateAddressDtls: " + e.getMessage(), e);
        }

    }

    private void copyAddressDetails(Address source, Address target) {
        target.setAddressLine1(source.getAddressLine1());
        target.setAddressLine2(source.getAddressLine2());
        target.setAddressLine3(source.getAddressLine3());
        target.setDistrict(source.getDistrict());
        target.setCity(source.getCity());
        target.setState(source.getState());
        target.setCountry(source.getCountry());
        target.setPinCode(source.getPinCode());
        target.setLandMark(source.getLandMark());
        target.setArea(source.getArea());

        target.setCurrentAddressProof(source.getCurrentAddressProof());
        target.setHouseType(source.getHouseType());
        target.setLocateCoOrdinatesFor(source.getLocateCoOrdinatesFor());
        target.setLocateCoOrdinates(source.getLocateCoOrdinates());

        target.setResidenceOwnership(source.getResidenceOwnership());
        target.setResidenceAddressSince(source.getResidenceAddressSince());
        target.setResidenceCitySince(source.getResidenceCitySince());

    }

    private void updateEditedFields(int stageId, UploadLoanRequestFields requestObj, String custCode) {
        logger.debug("Entry updateEditedFields method");
        // Construct the new stage edit strings
        List<String> newEntries = requestObj.getEditedFields().stream()
                .map(field -> stageId + "_" + custCode + "_" + field).collect(Collectors.toList());
        logger.debug("newEntries" + newEntries);

        // Construct the new stage verification strings
        List<String> entriesTocompareStageVr = requestObj.getEditedFields().stream()
                .map(field -> stageId + "_" + custCode).collect(Collectors.toList());
        logger.debug("entriesTocompareStageVr" + entriesTocompareStageVr);

        // Fetch existing record from DB
        Optional<RpcStageVerification> rpcStageVerificationDb = rpcStgVerificationRepo
                .findById(requestObj.getApplicationId());
        logger.debug("rpcStageVerificationDb findById :" + requestObj.getApplicationId());
        logger.debug("rpcStageVerificationDb :" + rpcStageVerificationDb.toString());

        if (rpcStageVerificationDb.isPresent()) {
            logger.debug("rpcStageVerificationDb record found");
            RpcStageVerification stageVerificationDbObj = rpcStageVerificationDb.get();

            String existingEdits = stageVerificationDbObj.getEditedFields();
            logger.debug("existingEdits: " + existingEdits);
            if (StringUtils.isNotEmpty(existingEdits)) {
                // Convert existing DB string into a Set for quick lookup
                Set<String> existingSet = new HashSet<>(Arrays.asList(existingEdits.split("\\|")));
                logger.debug("existingSet: " + existingSet.toString());

                // Append only new values that are not already present
                for (String entry : newEntries) {
                    if (!existingSet.contains(entry)) {
                        logger.debug("entry" + entry);
                        existingSet.add(entry); // Append new entry
                        logger.debug("existingSet: " + existingSet.toString());
                    }
                }

                logger.debug("existingSet final: " + existingSet.toString());

                // Update
                stageVerificationDbObj.setEditedFields(String.join("|", existingSet));
            } else {
                stageVerificationDbObj.setEditedFields(String.join("|", newEntries));
            }

//		        // Remove entries from stage same combination  StageId_ApplicantType
            if (stageVerificationDbObj.getVerifiedStages() != null) {
                String existingStagesVr = stageVerificationDbObj.getVerifiedStages();

                Set<String> existingStageVrSet = new HashSet<>(Arrays.asList(existingStagesVr.split("\\|")));
                // Identify entries that match the stageId_custCode pattern and remove them
                existingStageVrSet.removeIf(entry -> entriesTocompareStageVr.stream()
                        .anyMatch(compareEntry -> entry.startsWith(compareEntry + "_")));
                logger.debug("existingStageVrSet : " + existingStageVrSet.toString());

                if (existingStageVrSet.isEmpty()) {
                    stageVerificationDbObj.setVerifiedStages(null);
                } else {
                    stageVerificationDbObj.setVerifiedStages(String.join("|", existingStageVrSet));
                }
            }
            rpcStgVerificationRepo.save(stageVerificationDbObj);

        } else {
            logger.debug("rpcStageVerificationDb record not found. Inserting new record.");
            // If no record exists, create a new one
            String newStageString = String.join("|", newEntries);
            RpcStageVerification newEntry = new RpcStageVerification();
            newEntry.setApplicationId(requestObj.getApplicationId());
            newEntry.setEditedFields(newStageString);
            rpcStgVerificationRepo.save(newEntry);
        }

        logger.debug("End updateEditedFields method");
    }

    private void updateQueries(int stageId, UploadLoanRequestFields requestObj, String custCode) {
        logger.debug("Entry updateQueries method");

        try {
            // Construct the new stage Queries strings
            List<String> newEntries = requestObj.getQueries().stream()
                    .map(field -> stageId + "_" + custCode + "_" + field).collect(Collectors.toList());

            // Construct the new stage verification strings
            List<String> entriesTocompareStageVr = new ArrayList<>();
            if (requestObj.getQueries().isEmpty()) {
                entriesTocompareStageVr.add(stageId + "_" + custCode);
            } else {
				entriesTocompareStageVr.addAll(requestObj.getQueries().stream().map(field -> stageId + "_" + custCode)
						.collect(Collectors.toList()));
            }
            logger.debug("entriesTocompareStageVr" + entriesTocompareStageVr);

            // Fetch existing record from DB
            Optional<RpcStageVerification> rpcStageVerificationDb = rpcStgVerificationRepo
                    .findById(requestObj.getApplicationId());

            logger.debug("rpcStageVerificationDb findById" + requestObj.getApplicationId());
            logger.debug("rpcStageVerificationDb :" + rpcStageVerificationDb.toString());

            if (rpcStageVerificationDb.isPresent()) {
                logger.debug("rpcStageVerificationDb record found");
                RpcStageVerification stageVerificationDbObj = rpcStageVerificationDb.get();
                logger.debug("stageVerificationDbObj");
                String existingQueries = stageVerificationDbObj.getQueries();
                logger.debug("existingQueries: " + existingQueries);

                if (StringUtils.isNotEmpty(existingQueries)) {
                    Set<String> existingSet;
                    logger.debug("existingQueries: " + existingQueries);
                    // Convert existing DB string into a Set
                    existingSet = new HashSet<>(Arrays.asList(existingQueries.split("\\|")));
                    Set<String> filteredSet = existingSet.stream().filter(e -> e.startsWith(stageId + "_" + custCode))
                            .collect(Collectors.toSet());
                    logger.debug("Filtered Stream " + filteredSet);

                    existingSet.removeIf(elem -> filteredSet.contains(elem));
                    logger.debug("Set after operation :" + existingSet);

                    // add only new values that are not already present
                    for (String entry : newEntries) {
                        if (!existingSet.contains(entry)) {
                            existingSet.add(entry);
                            logger.debug("Existing set : " + entry);
                        }
                    }

                    logger.debug("existingSet final: " + existingSet.toString());
                    stageVerificationDbObj.setQueries(String.join("|", existingSet));
                } else {
                    stageVerificationDbObj.setQueries(String.join("|", newEntries));
                }

//			        // Remove entries with same stage and applicant type combination (1_A) -  that already exist
                if (stageVerificationDbObj.getVerifiedStages() != null) {
                    String existingStagesVr = stageVerificationDbObj.getVerifiedStages();

                    Set<String> existingStageVrSet = new HashSet<>(Arrays.asList(existingStagesVr.split("\\|")));
                    logger.debug("existingStageVrSet : " + existingStageVrSet.toString());

                    // Identify entries that match the stageId_custCode pattern and remove them
                    existingStageVrSet.removeIf(entry -> entriesTocompareStageVr.stream()
                            .anyMatch(compareEntry -> entry.startsWith(compareEntry + "_")));
                    logger.debug("existingStageVrSet : " + existingStageVrSet.toString());

                    if (existingStageVrSet.isEmpty()) {
                        stageVerificationDbObj.setVerifiedStages(null);
                    } else {
                        stageVerificationDbObj.setVerifiedStages(String.join("|", existingStageVrSet));
                    }
                }
                rpcStgVerificationRepo.save(stageVerificationDbObj);
            } else {
                logger.debug("rpcStageVerificationDb record not found. Inserting new record.");

                // If no record exists, create a new one
                String newStageString = String.join("|", newEntries);
                RpcStageVerification newEntry = new RpcStageVerification();
                newEntry.setApplicationId(requestObj.getApplicationId());
                newEntry.setQueries(newStageString);
                rpcStgVerificationRepo.save(newEntry);
            }

            logger.debug("End updateQueries method");
        } catch (Exception e) {
            logger.debug("Exception in updatde" + e);
        }
    }

    private void updateStageVerification(int stageId, UploadLoanRequestFields requestObj, String custCode) {
        logger.debug("Entry updateStageVerification method");
        // Construct the new stage verification strings

        String newEntry = stageId + "_" + custCode + "_" + LocalDateTime.now();

        Set<String> entriesTocompareStageVr = new HashSet<>();
        entriesTocompareStageVr.add(stageId + "_" + custCode);

        List<String> newEntries = Arrays.asList(newEntry);
        // Fetch existing record from DB
        Optional<RpcStageVerification> stageVerificationDb = rpcStgVerificationRepo
                .findById(requestObj.getApplicationId());
        logger.debug("Size of stage" + stageVerificationDb);

        if (stageVerificationDb.isPresent()) {
            logger.debug("rpcStageVerificationDb record found");
            RpcStageVerification stageVerificationDbObj = stageVerificationDb.get();
            String existingStages = stageVerificationDbObj.getVerifiedStages();
            logger.debug("existingStages: " + existingStages);
            // Convert existing DB string into a Set
            if (StringUtils.isNotEmpty(existingStages)) {
                Set<String> existingSet = null;
                // Convert existing DB string into a Set
                existingSet = new HashSet<>(Arrays.asList(existingStages.split("\\|")));

                existingSet.removeIf(entry -> entriesTocompareStageVr.stream()
                        .anyMatch(compareEntry -> entry.startsWith(compareEntry + "_")));

                logger.debug("existingStageVrSet : " + existingSet.toString());
                //
                existingSet.add(newEntry);

                if (existingSet.isEmpty()) {
                    stageVerificationDbObj.setVerifiedStages(null);
                } else {
                    stageVerificationDbObj.setVerifiedStages(String.join("|", existingSet));
                    logger.debug("existingStageVrSetFinal : " + existingSet.toString());
                }
            } else {
                stageVerificationDbObj.setVerifiedStages(newEntry);
            }

            rpcStgVerificationRepo.save(stageVerificationDbObj);
        } else {
            logger.debug("rpcStageVerificationDb record not found. Inserting new record.");
            // If no record exists, create a new one
            String newStageString = String.join("|", newEntries);
            logger.debug("Newstage:" + newStageString);
            RpcStageVerification newRpcStageVn = new RpcStageVerification();
            newRpcStageVn.setApplicationId(requestObj.getApplicationId());
            newRpcStageVn.setVerifiedStages(newStageString);
            rpcStgVerificationRepo.save(newRpcStageVn);
        }

        logger.debug("End updateStageVerification method");

    }

    private void updateApplicationDocs(UploadLoanRequestFields requestObj, String applicationID, int version) {
        logger.debug("Onentry :: updateApplicationDocs");
        String documentName = Constants.PENNY_DOCUMENT_NAME;
        String documentType = Constants.PENNY_DOCUMENT_TYPE;

        try {
            Gson gson = new Gson();
            List<ApplicationDocumentsWrapper> applicationDocumentsWrapperList = requestObj
                    .getApplicationDocumentsWrapperList();
            logger.debug("applicationDocumentsWrapperList size :" + applicationDocumentsWrapperList.size());
            for (ApplicationDocumentsWrapper applicationDocumentsWrapper : applicationDocumentsWrapperList) {
                List<ApplicationDocuments> applicationDocumentsList = applicationDocumentsWrapper
                        .getApplicationDocumentsList();
                for (ApplicationDocuments applicationDocuments : applicationDocumentsList) {
                    if (applicationDocuments.getAppDocId() == null) {// This is to handle the case if user changed the
                        // data
                        // after its being inserted by using the back
                        // navigation within the session.
                        BigDecimal appDocId = CommonUtils.generateRandomNum();
                        applicationDocuments.setAppDocId(appDocId);
                    }
                    applicationDocuments.setApplicationId(applicationID);
                    applicationDocuments.setVersionNum(version);
                    applicationDocuments.setAppId(requestObj.getAppId());
//					ObjectMapper objectMapper= new ObjectMapper();

                    logger.debug("Doc Type :" + applicationDocuments.getPayload().getDocumentType());

                    ApplicationDocumentsPayload payload = applicationDocuments.getPayload();

                    logger.debug("Payload " + payload);
                    JSONObject js = new JSONObject();
//					js.put("docLevel", payload.getDocLevel());
                    js.put("documentType", documentType);
                    js.put("documentName", documentName);
                    js.put("docSide", payload.getDocSide());
                    js.put("docStatus", payload.getDocStatus());
                    js.put("documentFileName", payload.getDocumentFileName());
                    js.put("documentFormat", payload.getDocumentFormat());
                    js.put("documentLoc", payload.getDocumentLoc());
                    js.put("expiryDate", payload.getExpiryDate());
                    js.put("documentDesc", payload.getDocumentDesc());
                    js.put("issueDate", payload.getIssueDate());
                    js.put("screenId", payload.getScreenId());

                    applicationDocuments.setPayloadColumn(js.toString());
                    applicationDocuments.setStatus(AppStatus.ACTIVE_STATUS.getValue());

                    appLoanDocsRepository.save(applicationDocuments);
                }
            }
            logger.warn("Data inserted into TB_ABOB_APPLN_DOCUMENTS for uploadLoan");

        } catch (Exception e) {
            logger.error("Exception in Dtls: " + e.getMessage(), e);
        }

    }

    public Response deleteDocument(String applicationId, String appId) {
        logger.debug("Inside Delete Document Function");
        String res = "";
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        String filePath = "";
        String fileName = "";
        String documentName = Constants.PENNY_DOCUMENT_NAME;
        String documentType = Constants.PENNY_DOCUMENT_TYPE;

        try {
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
            Properties prop = null;
            try {
                prop = CommonUtils.readPropertyFile();
            } catch (IOException e) {
                logger.error("Error while reading property file in deleteDocument ", e);
                return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
            }
			List<ApplicationDocuments> applDocs = appLoanDocsRepository.findByApplicationIdAndAppId(applicationId,
					appId);
            for (int i = 0; i < applDocs.size(); i++) {
                String payloadColumn = applDocs.get(i).getPayloadColumn();
                logger.debug("payload " + payloadColumn);
                JSONObject request = new JSONObject(payloadColumn);
                if (request.getString("documentType").equalsIgnoreCase(documentType)
                        && request.getString("documentName").equalsIgnoreCase(documentName)) {

                    appLoanDocsRepository.deleteById(applDocs.get(i).getAppDocId());
                    filePath = request.getString("documentLoc");
                    fileName = request.getString("documentFileName");
                    logger.debug("FilePth " + filePath);
                    responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());

                    String uploadLocation = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey());
                    if (null != uploadLocation && !"".equalsIgnoreCase(uploadLocation)) {
                        Path path = Paths.get(uploadLocation + "/" + filePath + "/" + fileName);
                        logger.debug("Path to be deleted " + path.toString());
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            logger.error("Error while deleting file in deleteDocument ", e);
                            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
                                    ResponseCodes.FAILURE.getKey());
                        }
                        res = "Success";
                    } else {
                        responseHeader.setResponseCode(ResponseCodes.PATH_NOT_CONFIGURED.getKey());
                    }

                    break;

                } else {
                    responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                    res = "Failed to delete";
                }
            }

            responseBody.setResponseObj(res);
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
        } catch (JSONException e) {
            logger.error("Error while deleting file in deleteDocument Function ", e);
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }
        return response;
    }

    public Response uploadDocument(UploadDocumentRequestFields requestFields, Properties prop) {
        Gson gson = new Gson();
        CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        BigDecimal docId = null;
        logger.debug("uploadDocument called with requestFields: {}", requestFields);
        if (requestFields.getDocumentId() == null) {
            docId = CommonUtils.generateRandomNum();
            logger.debug("Generated new docId: {}", docId);
        } else {
            docId = requestFields.getDocumentId();
            logger.debug("Using provided docId: {}", docId);
        }

        Optional<ApplicationMaster> masterObjDb = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNum(
                requestFields.getAppId(), requestFields.getApplicationId(), requestFields.getVersionNum());
        if (masterObjDb.isPresent()) {
            logger.debug("Application master found");
            byte[] docByte;
            if (!(CommonUtils.isNullOrEmpty(requestFields.getBase64Value()))) {
                logger.error("Base64 data found for file: {}", requestFields.getFileName());
                String uploadLocation = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey());
                logger.debug("Upload Location " + uploadLocation);
                if (null != uploadLocation && !"".equalsIgnoreCase(uploadLocation)) {
                    docByte = Base64.getDecoder().decode(requestFields.getBase64Value());
                    logger.debug("Decoded Base64, docByte length: {}", docByte.length);
                    if (!(CommonUtils.isNullOrEmpty(requestFields.getFilePath()))) {
                        logger.debug("File path for file: {}", requestFields.getFileName());
                        String[] splitFileName = requestFields.getFileName().split("\\.");
                        logger.debug("File name is valid: {}", splitFileName);
                        String fileFormat = splitFileName[splitFileName.length - 1];
                        logger.debug("file format: {}", fileFormat);
                        try {
                            if (isFileFormatValid(docByte, fileFormat)) {
                                String fileLoc = uploadLocation + "/" + requestFields.getFilePath();
                                logger.debug("File Location " + fileLoc);
                                File file = new File(fileLoc);
                                if (!file.exists()) {
                                    boolean dirCreated = file.mkdirs();
                                    logger.debug("Directory {} created: {}", fileLoc, dirCreated);
                                }
                                File outputFile = new File(fileLoc + "/" + requestFields.getFileName());
                                if (Constants.DOCFORMATPDF.equalsIgnoreCase(fileFormat)) {
                                    try (FileOutputStream fos = new FileOutputStream(
                                            fileLoc + "/" + requestFields.getFileName())) {
                                        fos.write(docByte);
                                        logger.debug("Saving PDF file to: {}", outputFile.getAbsolutePath());
                                    }
                                } else {// other formats like jpeg, jpg, png
                                    try (ByteArrayInputStream bis = new ByteArrayInputStream(docByte)) {
                                        logger.debug("Saving image file to: {}", outputFile.getAbsolutePath());
                                        BufferedImage image = ImageIO.read(bis);
                                        File outputfile = new File(fileLoc + "/" + requestFields.getFileName());
                                        ImageIO.write(image, fileFormat, outputfile);
                                    }
                                }
                                logger.debug("File saved successfully: {}", outputFile.getAbsolutePath());
                                responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                                customerIdentification.setAppDocId(docId);
//								checkAppCreateAppElements.setCreateAppRes(customerIdentification);
//								responseBody.setResponseObj(gson.toJson(checkAppCreateAppElements));
                            } else {
                                logger.error("Invalid file format : {} ", fileFormat);
                                responseHeader.setResponseCode(ResponseCodes.VAPT_ISSUE_FILE_FORMAT.getKey());
                                responseBody.setResponseObj(ResponseCodes.VAPT_ISSUE_FILE_FORMAT.getValue());

                            }
                        } catch (Exception e) {
                            logger.error("Exception in upload document IOException ", e);
                            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                        }
                    }
                } else {
                    responseHeader.setResponseCode(ResponseCodes.PATH_NOT_CONFIGURED.getKey());
                    responseBody.setResponseObj("Document not uploaded.");

                }
            } else {
                responseHeader.setResponseCode(ResponseCodes.BASE64_DATA_NOT_FOUND.getKey());
                responseBody.setResponseObj(ResponseCodes.BASE64_DATA_NOT_FOUND.getValue());

            }
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            responseBody.setResponseObj(Constants.APP_MASTER_NOT_FOUND);
        }
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    private boolean isFileFormatValid(byte[] docByte, String fileFormat) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(docByte);
        Tika tika = new Tika();
        String fileMimeType = tika.detect(inputStream);
        return (Constants.DOCFORMATPDF.equalsIgnoreCase(fileFormat) && "application/pdf".equalsIgnoreCase(fileMimeType))
                || (Constants.DOCFORMATPNG.equalsIgnoreCase(fileFormat) && "image/png".equalsIgnoreCase(fileMimeType))
                || ((Constants.DOCFORMATJPEG.equalsIgnoreCase(fileFormat)
                || Constants.DOCFORMATJPG.equalsIgnoreCase(fileFormat))
                && "image/jpeg".equalsIgnoreCase(fileMimeType));
    }

    private void uploadApplicationDocs(UploadLoanRequestFields requestObj, String applicationID, int version) {
        logger.debug("Onentry :: updateApplicationDocs");
        try {
            Gson gson = new Gson();
            List<ApplicationDocumentsWrapper> applicationDocumentsWrapperList = requestObj
                    .getApplicationDocumentsWrapperList();
            logger.debug("applicationDocumentsWrapperList size :" + applicationDocumentsWrapperList.size());
            for (ApplicationDocumentsWrapper applicationDocumentsWrapper : applicationDocumentsWrapperList) {
                List<ApplicationDocuments> applicationDocumentsList = applicationDocumentsWrapper
                        .getApplicationDocumentsList();
                for (ApplicationDocuments applicationDocuments : applicationDocumentsList) {
					List<ApplicationDocuments> existingAppDocs = appLoanDocsRepository
							.findByApplicationIdAndCustDtlIdAndDocLevelAndDocumentType(applicationID,
									applicationDocuments.getCustDtlId(),
                            applicationDocuments.getPayload().getDocLevel(),
									applicationDocuments.getPayload().getDocumentType());

                    if (!existingAppDocs.isEmpty()) {
                        for (ApplicationDocuments appDocs : existingAppDocs) {
                            appLoanDocsRepository.delete(appDocs);
                        }
                    }

                    if (applicationDocuments.getAppDocId() == null) {// This is to handle the case if user changed the
                        // data
                        // after its being inserted by using the back
                        // navigation within the session.
                        BigDecimal appDocId = CommonUtils.generateRandomNum();
                        applicationDocuments.setAppDocId(appDocId);
                        applicationDocuments.setDocumentId(appDocId);
                    }
                    applicationDocuments.setApplicationId(applicationID);
                    applicationDocuments.setVersionNum(version);
                    applicationDocuments.setAppId(requestObj.getAppId());

                    String payload = gson.toJson(applicationDocuments.getPayload());
                    applicationDocuments.setPayloadColumn(payload);

                    applicationDocuments.setStatus(AppStatus.ACTIVE_STATUS.getValue());
                    logger.debug("Saving application document with AppDocId:" + applicationDocuments.getAppDocId());
                    logger.debug("Payload " + payload);
                    appLoanDocsRepository.save(applicationDocuments);
                }
            }
            logger.warn("Data inserted into TB_ABOB_APPLN_DOCUMENTS for uploadLoan");

        } catch (Exception e) {
            logger.error("Exception in Dtls: " + e.getMessage(), e);
        }
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

    public Response getFailureApiJson(String error, String apiName) {
        logger.debug("Inside getFailureJsonn");
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
		responseBody.setResponseObj("{\"apiName\":\"" + apiName + "\",\"errorMessage\":\"" + error + "\", \"status\":\""
				+ ResponseCodes.FAILURE.getValue() + "\"}");
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        logger.debug("FailureJson created" + response.toString());
        return response;
    }

	private void updateCoApplicantBankDtls(UploadLoanRequestFields requestObj, String applicationID, String appId,
			int version) {
        Gson gson = new Gson();

        List<BankDetailsWrapper> bankDetailsWrapperList = requestObj.getBankDetailsWrapperList();
        for (BankDetailsWrapper bankDetailsWrapper : bankDetailsWrapperList) {
            BankDetails bankDetails = bankDetailsWrapper.getBankDetails();

			Optional<BankDetails> bankDetailsDb = bankDtlRepo
					.findBankDetailsByCustomerType(requestObj.getCustomerType(), requestObj.getApplicationId());
            if (bankDetailsDb.isPresent()) {
                logger.debug("records found for the given applicationId and customerType.");
                bankDetails.setBankDtlId(bankDetailsDb.get().getBankDtlId());
                bankDetails.setCustDtlId(bankDetailsDb.get().getCustDtlId());
            } else {
                logger.debug("creating new");
                BigDecimal bankDtlId = CommonUtils.generateRandomNum();
                bankDetails.setBankDtlId(bankDtlId);

				Optional<CustomerDetails> customerDetails = custDtlRepo
						.findByApplicationIdAndCustomerType(applicationID, requestObj.getCustomerType());
                if (customerDetails.isPresent()) {
                    bankDetails.setCustDtlId(customerDetails.get().getCustDtlId());
                } else {
                    logger.debug("No customer details found for the given applicationId and customerType.");
                }
            }
            bankDetails.setAppId(requestObj.getAppId());
            bankDetails.setApplicationId(applicationID);
            bankDetails.setVersionNum(version);
            String payload = gson.toJson(bankDetails.getPayload());
            bankDetails.setPayloadColumn(payload);
            bankDtlRepo.save(bankDetails);
            logger.debug("Co applicant bank details saved.");
            String eNachStatus = bankDetails != null
					? bankDetails.getPayload() != null ? bankDetails.getPayload().getENachStatus() : null
                    : null;
            if (eNachStatus != null && !eNachStatus.isEmpty()) {
				Optional<Enach> enachOpt = enachRepository.findByApplicationIdAndCustomerType(applicationID,
						requestObj.getCustomerType());
                if (enachOpt.isPresent()) {
                    Enach enachDetails = enachOpt.get();
                    enachRepository.delete(enachDetails);
                }
            }
        }
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "bIPFallback")
    public Mono<Object> bussinessImgProcessingApi(BIPMasterRequest bipRequest, Header header, Properties prop) {
        try {

            //file path
			String applicationFolderPath = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/"
					+ bipRequest.getRequestObj().getAppId() + "/" + Constants.LOAN + "/"
                    + bipRequest.getRequestObj().getApplicationId() + "/";
            logger.debug("applicationFolderPath resolved as: {}", applicationFolderPath);

            logger.debug("request from the bussinessImgProcessing API: {} ", bipRequest.toString());

            List<String> imageFiles = bipRequest.getRequestObj().getFiles();
            List<Map<String, String>> imageUrls = new ArrayList<>();

            int index = 1;
            for (String imgName : imageFiles) {
                String filePath = applicationFolderPath + imgName;
                File file = new File(filePath);

                if (file.exists()) {
                    try {
                        byte[] fileContent = Files.readAllBytes(file.toPath());
                        String base64 = Base64.getEncoder().encodeToString(fileContent);

//		                // detect file type
//		                String mimeType = Files.probeContentType(file.toPath());
//		                if (mimeType == null) {
//		                    mimeType = "image/jpeg";
//		                }
//
//		                String dataUrl = "data:" + mimeType + ";base64," + base64;
//		                Map<String, String> imageMap = new HashMap<>();
//		                imageMap.put("image" + index, dataUrl);

						String prefix = imgName.endsWith(".png") ? "data:image/png;base64," : "data:image/jpeg;base64,";

                        Map<String, String> imageMap = new HashMap<>();
                        imageMap.put("image" + index, prefix + base64);

                        imageUrls.add(imageMap);
                        index++;

                    } catch (Exception e) {
                        logger.error("Error while processing image: " + imgName, e);
                    }
                }
            }

            BIPRequestExt bipRequestExt = new BIPRequestExt();
            bipRequestExt.setAppId(bipRequest.getRequestObj().getAppId());
			bipRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.BIP_SUBMIT_IMG_INTF.getKey()));

            BIPInputRequest apiReq = new BIPInputRequest();

            String requestId = UUID.randomUUID().toString().replace("-", "");

            apiReq.setRequestId(requestId);
            apiReq.setBusinessType(bipRequest.getRequestObj().getDocumentType()); //
            apiReq.setImageUrls(imageUrls);
            apiReq.setOptions(new BIPInputRequest.Options());

            bipRequestExt.setRequestObj(apiReq);

            logger.debug("final requsest :: bussinessImgProcessing  API: {} ", bipRequestExt.toString());
            Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, bipRequestExt,
                    bipRequestExt.getInterfaceName());

            return apiRespMono.flatMap(val -> {
                logger.debug("response 2 from the API: {} ", val);
                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("JSON response 3 from the API: {} ", apiResp);

//             logger.debug("response 2 from the API: " + val);
//             String apiResp = new Gson().toJson(val);
//             logger.debug("JSON response 3 from the API: " + apiResp);

                if (apiResp.get("success").equals(true)) {

					saveLog(bipRequest.getRequestObj().getApplicationId(), Constants.BIP_IMG_PROCESS,
							bipRequestExt.toString(), apiResp.toString(), ResponseCodes.SUCCESS.getValue(), null, "");

                    return Mono.just(apiResp);
                } else {
					saveLog(bipRequest.getRequestObj().getApplicationId(), Constants.BIP_IMG_PROCESS,
							bipRequestExt.toString(), apiResp.toString(), ResponseCodes.FAILURE.getValue(), null, "");
                    return Mono.just(getFailureApiJson(apiResp.toString(), Constants.BIP_IMG_PROCESS));
                }
            });

        } catch (Exception e) {
			saveLog(bipRequest.getRequestObj().getApplicationId(), Constants.BIP_IMG_PROCESS, bipRequest.toString(),
					null, ResponseCodes.FAILURE.getValue(), e.getMessage(), "");
            return Mono.just(getFailureApiJson(e.toString(), Constants.BIP_IMG_PROCESS));
        }

    }

	/**
	 * @author Ankit.CAG
	 */
	public Mono<Object> AdharRedact(AdharRedactOcrRequest apiRequest, Header header, Properties prop) {
		logger.debug("AdharRedact Ocr service for Application ID: {} ", apiRequest.getApplicationId());

		AdharUploadRequest requestObj = apiRequest.getRequestObj();

		if(requestObj.getFetchAadhaar()!=null && requestObj.getFetchAadhaar()){
			return fetchAdharData(apiRequest.getCustDtlId(), apiRequest.getDocType());
		}

		AdharRedactOcrRequestFields adharRedactOcrRequestFields = requestObj.getAdharRedactOcrRequestFields();
		adharRedactOcrRequestFields.setClientRefId(CommonUtils.generateRandomNumStr());

		// Printing Hole Object in JSON for REF:
		Gson gson = new Gson();
		Map<String, Object> combinedRequest = new HashMap<>();
		combinedRequest.put("brecbRequest", apiRequest);
		combinedRequest.put("header", header);
		String adharRedactReq = gson.toJson(combinedRequest);

		AdharRedactOcrRequestExt adharRedactOcrRequestExt = new AdharRedactOcrRequestExt();
		adharRedactOcrRequestExt.setAppId(apiRequest.getAppId());
		adharRedactOcrRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.ADHAR_REDACT_OCR_INTF.getKey()));
		adharRedactOcrRequestExt.setRequestObj(adharRedactOcrRequestFields);
		logger.debug("adharRedactOcrRequestExt from the API: {} ", adharRedactOcrRequestExt.toString());
		Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, adharRedactOcrRequestExt,
				adharRedactOcrRequestExt.getInterfaceName());
		logger.debug("AdharRedact Ocr APi Response: {} ", apiRespMono);
		//return apiRespMono;
		return apiRespMono.flatMap(response -> {

			// 🔹 Prepare request payload (same pattern as PAN)
			Map<String, Object> requestPayload = new HashMap<>();

			AdharRedactOcrRequestFields copyReqFields = new AdharRedactOcrRequestFields(adharRedactOcrRequestFields);

			AdharRedactOcrRequestExt copyRequest = new AdharRedactOcrRequestExt();
			copyRequest.setAppId(adharRedactOcrRequestExt.getAppId());
			copyRequest.setInterfaceName(adharRedactOcrRequestExt.getInterfaceName());
			copyRequest.setRequestObj(copyReqFields);

			requestPayload.put("header", header);
			requestPayload.put("body", copyRequest);

			String requestPayloadStr = gson.toJson(requestPayload);

			// 🔹 Create Entity
			OCRDetails entity = new OCRDetails();
			entity.setApplicationId(apiRequest.getApplicationId());
			entity.setCustDtlId(apiRequest.getCustDtlId());
			entity.setDocSide(apiRequest.getDocSide());
			entity.setDocType(apiRequest.getDocType());
			entity.setCreatedBy(apiRequest.getCreatedBy());
			entity.setCreatedAt(LocalDateTime.now());
			entity.setRequestPayload(requestPayloadStr);

			//Getting the base64 value of Masked Image

			JsonObject adharRes = gson.toJsonTree(response).getAsJsonObject();
			String status = Optional.ofNullable(adharRes.get("status")).filter(e -> !e.isJsonNull())
					.map(JsonElement::getAsString).orElse(null);
			ResponseHeader responseHeader = new ResponseHeader();
			ResponseBody responseBody = new ResponseBody();
			if(("failure").equalsIgnoreCase(status)){

				response = new Response(responseHeader, responseBody);
				responseHeader.setResponseCode(ResponseCodes.AADHAAR_VALIDATION_FAIL.getKey());
				responseBody.setResponseObj("No Aadhaar Detected");
				return Mono.fromCallable(() -> ocrDetailsRepo.save(entity)).thenReturn(response);
			}

			JsonObject details = Optional.ofNullable(adharRes.getAsJsonArray("result")).filter(arr -> arr.size() > 0)
					.map(arr -> arr.get(0).getAsJsonObject()).map(obj -> obj.getAsJsonObject("details")).orElse(null);

			String maskedBase64 = null;

			if (details != null) {
				JsonObject maskedObj = details.getAsJsonObject("maskedImageURL");
				JsonObject address = details.getAsJsonObject("address");
				if(address != null) {
					String line1 = address.get("line1").getAsString();
					if(line1 == null)
						line1 = "";
					String line2 = address.get("line2").getAsString();
					if(line2 == null)
						line2 = "";
					if (line1.length() > 30 || line2.length() > 30) {
						logger.debug("Before split Line1: {} , Line2: {}", line1, line2);
						List<String> addrsLines = CommonUtils.getStringWithLimit(line1 + " " + line2, 30);
						address.addProperty("line1", addrsLines.get(0));
						address.addProperty("line2", addrsLines.get(1));
						if(addrsLines.size() > 2) {
							address.addProperty("line3", addrsLines.get(2));
						}
						details.add("address", address);
						logger.debug("After split Line1: {} , Line2: {}", addrsLines.get(0), addrsLines.get(1));
					}
				}

				if (maskedObj != null && maskedObj.has("value") && !maskedObj.get("value").isJsonNull()) {
					maskedBase64 = maskedObj.get("value").getAsString();
				}

				details.remove("maskedImageURL");
			}

			// 🔹 Set Response
			String responsePayload = gson.toJson(adharRes);

			String type = Optional.ofNullable(adharRes.getAsJsonArray("result")).filter(arr -> arr.size() > 0)
					.map(arr -> arr.get(0).getAsJsonObject()).map(obj -> obj.get("type").getAsString()).orElse(null);

			if(!StringUtils.isEmpty(maskedBase64) && type.contains(apiRequest.getDocSide().toLowerCase())) {
				Boolean isDedupePass = true;
				if(apiRequest.getDocSide().toLowerCase().equalsIgnoreCase("front")) {
					String name = details.getAsJsonObject("name").get("value").getAsString();
					String aadhaarId  = details.getAsJsonObject("aadhaar").get("value").getAsString();
					String aadhaarDob  = details.getAsJsonObject("dob").get("value").getAsString();
					String aadhaarDedupeResponse = aadhaarDedupe(aadhaarId, name, aadhaarDob, apiRequest.getCustDtlId(),
							apiRequest.getApplicationId());
					if (!aadhaarDedupeResponse.equalsIgnoreCase("Success")) {
						isDedupePass = false;
						response = new Response(responseHeader, responseBody);
						responseHeader.setResponseCode(ResponseCodes.AADHAAR_VALIDATION_FAIL.getKey());
						responseBody.setResponseObj(aadhaarDedupeResponse);
						logger.debug("Aadhaar Dedupe Failed");
					}
				}
				if(isDedupePass) {
					UploadLoanRequestFields uploadLoanRequestFields = requestObj.getUploadLoanRequestFields();
					UploadDocumentRequestFields uploadDocumentRequestFields = uploadLoanRequestFields
							.getUploadDocumentRequestFields();
					uploadDocumentRequestFields.setBase64Value(maskedBase64);
					UploadLoanRequest uploadLoanRequest = new UploadLoanRequest();
					uploadLoanRequest.setRequestObj(uploadLoanRequestFields);
					uploadLoanRequest.setUserId(apiRequest.getCreatedBy());
					response = uploadLoan(uploadLoanRequest);
				}
			} else if(type!= null && !type.contains(apiRequest.getDocSide().toLowerCase())) {
				response = new Response(responseHeader, responseBody);
				responseHeader.setResponseCode(ResponseCodes.AADHAAR_VALIDATION_FAIL.getKey());
				responseBody.setResponseObj("Please upload the  of the "+apiRequest.getDocSide()+" document");
				logger.debug("Document Side Mismatch");
			} else {
				response = new Response(responseHeader, responseBody);
				responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
				responseBody.setResponseObj("Aadhaar Redact Couldn't get Masked Image");
				logger.debug("Masked Image not found");
			}

			entity.setResponsePayload(responsePayload);

			// 🔹 Save DB (non-blocking safe way)
			return Mono.fromCallable(() -> ocrDetailsRepo.save(entity)).thenReturn(response);
		});
	}

	private Mono<Object> fetchAdharData(BigDecimal custDtlId, String docType){
		Optional<OCRDetails> frontAdharData = ocrDetailsRepo
				.findFirstByCustDtlIdAndDocTypeAndDocSideOrderByCreatedAtDesc(custDtlId, docType, "Front");
		Optional<OCRDetails> backAdharData = ocrDetailsRepo
				.findFirstByCustDtlIdAndDocTypeAndDocSideOrderByCreatedAtDesc(custDtlId, docType, "Back");
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		Gson gson = new Gson();

		if((!frontAdharData.isPresent() || frontAdharData.get().getDetails() == null)
			&& (!backAdharData.isPresent() || backAdharData.get().getDetails() == null)){
			responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
			responseBody.setResponseObj("Cannot Find Aadhaar Redact Records");
		} else{
			Map<String,String> aadhaarResponse = new HashMap<>();
			if(frontAdharData.isPresent()){
				String frontAdhar = frontAdharData.get().getDetails();
				logger.debug("Front Aadhaar Redact Response : {}", frontAdhar);
				if(frontAdhar != null) {
					aadhaarResponse.put("front", frontAdhar);
				}
			}
			if(backAdharData.isPresent()){
				String backAdhar = backAdharData.get().getDetails();
				logger.debug("Back Aadhaar Redact Response : {}", backAdhar);
				if(backAdhar != null) {
					aadhaarResponse.put("back", backAdhar);
				}
			}
			responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
			responseBody.setResponseObj(gson.toJson(aadhaarResponse));
		}

		return Mono.just(response);
	}

	/**
	 * @author Ankit.CAG
	 */
	public Mono<Object> KycPassportService(KycPassportRequest apiRequest, Header header, Properties prop) {
		logger.debug("KycPassport service: {} ", apiRequest.toString());

		// Printing Hole Object in JSON for REF:
		Gson gson = new Gson();
		Map<String, Object> combinedRequest = new HashMap<>();
		combinedRequest.put("KycPassportRequest", apiRequest);
		combinedRequest.put("header", header);
		String KycPassportReq = gson.toJson(combinedRequest);
		logger.debug("Combined request JSON: {}", KycPassportReq);

		KycPassportRequestExt kycPassportRequestExt = new KycPassportRequestExt();
		kycPassportRequestExt.setAppId(apiRequest.getAppId());
		kycPassportRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.KYC_PASSPORT_INTF.getKey()));
		kycPassportRequestExt.setRequestObj(apiRequest.getRequestObj());
		logger.debug("KycPassportRequestExt from the API: {} ", kycPassportRequestExt.toString());
		Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, kycPassportRequestExt,
				kycPassportRequestExt.getInterfaceName());
		logger.debug("KycPassport APi Response: {} ", apiRespMono);
		return apiRespMono;
	}

	/**
	 * @author Ankit.CAG
	 */
	public Mono<Object> kycDrivingLicenseService(kycDrivingLicenseRequest apiRequest, Header header, Properties prop) {
		logger.debug("kycDrivingLicense service: {} ", apiRequest.toString());

		// Printing Hole Object in JSON for REF:
		Gson gson = new Gson();
		Map<String, Object> combinedRequest = new HashMap<>();
		combinedRequest.put("kycDrivingLicense Request", apiRequest);
		combinedRequest.put("header", header);
		String kycDrivingLicenseReq = gson.toJson(combinedRequest);
		logger.debug("Combined request JSON: {}", kycDrivingLicenseReq);

		kycDrivingLicenseRequestExt kycDrivingLicenseRequestExt = new kycDrivingLicenseRequestExt();
		kycDrivingLicenseRequestExt.setAppId(apiRequest.getAppId());
		kycDrivingLicenseRequestExt
				.setInterfaceName(prop.getProperty(CobFlagsProperties.KYC_DRIVING_LICENSE_INTF.getKey()));
		kycDrivingLicenseRequestExt.setRequestObj(apiRequest.getRequestObj());
		logger.debug("kycDrivingLicenseRequestExt from the API: {} ", kycDrivingLicenseRequestExt.toString());
		Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, kycDrivingLicenseRequestExt,
				kycDrivingLicenseRequestExt.getInterfaceName());
		logger.debug("kycDrivingLicense APi Response: {} ", apiRespMono);
		return apiRespMono;
	}

	/**
	 * @author Ankit.CAG
	 */
	public Mono<Object> panCheckService(PanCheckRequest apiRequest, Header header, Properties prop) {
		logger.debug("panCheck service: {} ", apiRequest.toString());

		// Printing Hole Object in JSON for REF:
		Gson gson = new Gson();
		Map<String, Object> combinedRequest = new HashMap<>();
		combinedRequest.put("panCheck Request", apiRequest);
		combinedRequest.put("header", header);
		String panCheckReq = gson.toJson(combinedRequest);
		logger.debug("Combined request JSON: {}", panCheckReq);

		PanCheckRequestExt panCheckRequestExt = new PanCheckRequestExt();
		panCheckRequestExt.setAppId(apiRequest.getAppId());
		panCheckRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.PAN_CHECK_OCR_INTF.getKey()));
		panCheckRequestExt.setRequestObj(apiRequest.getRequestObj());

		//Creating Map for Request Payload without imageUrl to store in db
		Map<String, Object> requestPayload = new HashMap<>();
		PanCheckRequestFields copyReqFields = new PanCheckRequestFields(apiRequest.getRequestObj());
		PanCheckRequestExt copyRequest = new PanCheckRequestExt();
		copyRequest.setAppId(panCheckRequestExt.getAppId());
		copyRequest.setInterfaceName(panCheckRequestExt.getInterfaceName());
		copyRequest.setRequestObj(copyReqFields);
		requestPayload.put("header", header);
		requestPayload.put("body", copyRequest);
		String requestPayloadStr = gson.toJson(requestPayload);

		//Storing all the OCR request and response in OCR_DETAILS table
		OCRDetails ocrDetails = new OCRDetails();
		ocrDetails.setDocType(apiRequest.getRequestObj().getDoctype());
		ocrDetails.setApplicationId(apiRequest.getApplicationId());
		ocrDetails.setDocSide("Front");
		ocrDetails.setCreatedBy(apiRequest.getCreatedBy());
		ocrDetails.setCreatedAt(LocalDateTime.now());
		ocrDetails.setCustDtlId(apiRequest.getCustDtlId());
		ocrDetails.setRequestPayload(requestPayloadStr);

		logger.debug("panCheckRequestExt from the API: {} ", panCheckRequestExt.toString());
		Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, panCheckRequestExt,
				panCheckRequestExt.getInterfaceName());
		logger.debug("panCheck APi Response: {} ", apiRespMono);
		return apiRespMono.flatMap(response -> {
			String responsePayload = gson.toJson(response);
			logger.debug("Pan OCR Response check {}", responsePayload);
			ocrDetails.setResponsePayload(responsePayload);
			return Mono.fromCallable(() -> ocrDetailsRepo.save(ocrDetails)).thenReturn(response);
		});
	}

	/**
	 * @author Ankit.CAG
	 */
	public Mono<Object> DrivingLicenseOcrService(DrivingLicenseOcrRequest apiRequest, Header header, Properties prop) {
		logger.debug("DrivingLicenseOcrRequest service: {} ", apiRequest.toString());

		// Printing Hole Object in JSON for REF:
		Gson gson = new Gson();
		Map<String, Object> combinedRequest = new HashMap<>();
		combinedRequest.put("DrivingLicenseOcrRequest Request", apiRequest);
		combinedRequest.put("header", header);
		String DrivingLicenseOcrReq = gson.toJson(combinedRequest);
		logger.debug("Combined request JSON: {}", DrivingLicenseOcrReq);

		DrivingLicenseOcrRequestExt drivingLicenseOcrRequestExt = new DrivingLicenseOcrRequestExt();
		drivingLicenseOcrRequestExt.setAppId(apiRequest.getAppId());
		drivingLicenseOcrRequestExt
				.setInterfaceName(prop.getProperty(CobFlagsProperties.DRIVING_LICENSE_OCR_INTF.getKey()));
		drivingLicenseOcrRequestExt.setRequestObj(apiRequest.getRequestObj());
		logger.debug("drivingLicenseOcrRequestExt from the API: {} ", drivingLicenseOcrRequestExt.toString());
		Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, drivingLicenseOcrRequestExt,
				drivingLicenseOcrRequestExt.getInterfaceName());
		logger.debug("drivingLicenseOcr APi Response: {} ", apiRespMono);
		return apiRespMono;
	}

	/**
	 * @author Ankit.CAG
	 */
	public Mono<Object> SignzyPennyCheckService(SignzyPennylessRequest apiRequest, Header header, Properties prop) {
		logger.debug("SignzyPennyCheckService service: {} ", apiRequest.toString());

		// Printing Hole Object in JSON for REF:
		Gson gson = new Gson();
		Map<String, Object> combinedRequest = new HashMap<>();
		combinedRequest.put("SignzyPennyCheckService Request", apiRequest);
		combinedRequest.put("header", header);
		String SignzyPennyCheckReq = gson.toJson(combinedRequest);
		logger.debug("Combined request JSON: {}", SignzyPennyCheckReq);

		SignzyPennylessCheckRequestExt signzyPennylessCheckRequestExt = new SignzyPennylessCheckRequestExt();
		signzyPennylessCheckRequestExt.setAppId(apiRequest.getAppId());
		signzyPennylessCheckRequestExt
				.setInterfaceName(prop.getProperty(CobFlagsProperties.SIGNZY_PENNYLESS_CHECK_INTF.getKey()));
		signzyPennylessCheckRequestExt.setRequestObj(apiRequest.getRequestObj());
		logger.debug("signzyPennylessCheckRequestExt from the API: {} ", signzyPennylessCheckRequestExt.toString());
		Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, signzyPennylessCheckRequestExt,
				signzyPennylessCheckRequestExt.getInterfaceName());
		logger.debug("SignzyPennyCheckService APi Response: {} ", apiRespMono);
		return apiRespMono;
	}

	/**
	 * @author Ankit.CAG
	 */
	public Mono<Object> VoterFrontOcrService(VoterFrontOcrRequest apiRequest, Header header, Properties prop) {
		// Printing Hole Object in JSON for REF:
		Gson gson = new Gson();
		Map<String, Object> combinedRequest = new HashMap<>();
		combinedRequest.put("header", header);
		String VoterFrontOcrReq = gson.toJson(combinedRequest);

		VoterFrontOcrRequestExt voterFrontOcrRequestExt = new VoterFrontOcrRequestExt();
		voterFrontOcrRequestExt.setAppId(apiRequest.getAppId());
		voterFrontOcrRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.VOTER_FRONT_OCR_INTF.getKey()));
		voterFrontOcrRequestExt.setRequestObj(apiRequest.getRequestObj());

		//Creating Map for Request Payload without imageUrl to store in db
		Map<String, Object> requestPayload = new HashMap<>();
		VoterFrontOcrRequestFields copyReqFields = new VoterFrontOcrRequestFields(apiRequest.getRequestObj());
		VoterFrontOcrRequestExt copyRequest = new VoterFrontOcrRequestExt();
		copyRequest.setAppId(voterFrontOcrRequestExt.getAppId());
		copyRequest.setInterfaceName(voterFrontOcrRequestExt.getInterfaceName());
		copyRequest.setRequestObj(copyReqFields);
		requestPayload.put("header", header);
		requestPayload.put("body", copyRequest);
		String requestPayloadStr = gson.toJson(requestPayload);

		//Storing all the OCR request and response in OCR_DETAILS table
		OCRDetails ocrDetails = new OCRDetails();
		ocrDetails.setDocType(apiRequest.getRequestObj().getDoctype());
		ocrDetails.setApplicationId(apiRequest.getApplicationId());
		ocrDetails.setDocSide("Front");
		ocrDetails.setCreatedBy(apiRequest.getCreatedBy());
		ocrDetails.setCreatedAt(LocalDateTime.now());
		ocrDetails.setCustDtlId(apiRequest.getCustDtlId());
		ocrDetails.setRequestPayload(requestPayloadStr);

		Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, voterFrontOcrRequestExt,
				voterFrontOcrRequestExt.getInterfaceName());
		logger.debug("VoterFrontOcrService APi Response: {} ", apiRespMono);
		return apiRespMono.flatMap(response -> {
			String responsePayload = gson.toJson(response);
			logger.debug("Front Voter OCR Response check {}", responsePayload);
			ocrDetails.setResponsePayload(responsePayload);
			return Mono.fromCallable(() -> ocrDetailsRepo.save(ocrDetails)).thenReturn(response);
		});
	}

	/**
	 * @author Ankit.CAG
	 */
	public Mono<Object> VoterBackOcrService(VoterBackOcrRequest apiRequest, Header header, Properties prop) {

		// Printing Hole Object in JSON for REF:
		Gson gson = new Gson();
		Map<String, Object> combinedRequest = new HashMap<>();
		combinedRequest.put("header", header);
		String VoterBackOcrReq = gson.toJson(combinedRequest);

		VoterBackOcrRequestExt voterBackOcrRequestExt = new VoterBackOcrRequestExt();
		voterBackOcrRequestExt.setAppId(apiRequest.getAppId());
		voterBackOcrRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.VOTER_BACK_OCR_INTF.getKey()));
		voterBackOcrRequestExt.setRequestObj(apiRequest.getRequestObj());

		//Creating Map for Request Payload without imageUrl to store in db
		Map<String, Object> requestPayload = new HashMap<>();
		VoterBackOcrRequestFields copyReqFields = new VoterBackOcrRequestFields(apiRequest.getRequestObj());
		VoterBackOcrRequestExt copyRequest = new VoterBackOcrRequestExt();
		copyRequest.setAppId(voterBackOcrRequestExt.getAppId());
		copyRequest.setInterfaceName(voterBackOcrRequestExt.getInterfaceName());
		copyRequest.setRequestObj(copyReqFields);
		requestPayload.put("header", header);
		requestPayload.put("body", copyRequest);
		String requestPayloadStr = gson.toJson(requestPayload);

		//Storing all the OCR request and response in OCR_DETAILS table
		OCRDetails ocrDetails = new OCRDetails();
		ocrDetails.setDocType(apiRequest.getRequestObj().getDoctype());
		ocrDetails.setApplicationId(apiRequest.getApplicationId());
		ocrDetails.setDocSide("Back");
		ocrDetails.setCreatedBy(apiRequest.getCreatedBy());
		ocrDetails.setCreatedAt(LocalDateTime.now());
		ocrDetails.setCustDtlId(apiRequest.getCustDtlId());
		ocrDetails.setRequestPayload(requestPayloadStr);

		logger.debug("voterBackOcrRequestExt from the API: {} ", voterBackOcrRequestExt.toString());
		Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, voterBackOcrRequestExt,
				voterBackOcrRequestExt.getInterfaceName());
		logger.debug("VoterBackOcrService APi Response: {} ", apiRespMono);
		return apiRespMono.flatMap(response -> {
			JsonObject voterRes = gson.toJsonTree(response).getAsJsonObject();

			JsonObject details = Optional.ofNullable(voterRes.getAsJsonArray("result")).filter(arr -> arr.size() > 0)
					.map(arr -> arr.get(0).getAsJsonObject()).map(obj -> obj.getAsJsonObject("details")).orElse(null);
			if (details != null) {
				JsonObject address = details.getAsJsonObject("address");
				if (address != null) {
					String line1 = address.get("line1").getAsString();
					if (line1 == null)
						line1 = "";
					String line2 = address.get("line2").getAsString();
					if (line2 == null)
						line2 = "";
					if (line1.length() > 30 || line2.length() > 30) {
						logger.debug("Before split Line1: {} , Line2: {}", line1, line2);
						List<String> addrsLines = CommonUtils.getStringWithLimit(line1 + " " + line2, 30);
						address.addProperty("line1", addrsLines.get(0));
						address.addProperty("line2", addrsLines.get(1));
						if (addrsLines.size() > 2) {
							address.addProperty("line3", addrsLines.get(2));
						}
						details.add("address", address);
						logger.debug("After split Line1: {} , Line2: {}", addrsLines.get(0), addrsLines.get(1));
					}
					voterRes.add("details", details);
					logger.debug("Voter Back Manipulated Response {}", voterRes);
				}
			}
			String responsePayload = gson.toJson(response);
			logger.debug("Back Voter OCR Response check {}", responsePayload);
			ocrDetails.setResponsePayload(responsePayload);
			return Mono.fromCallable(() -> ocrDetailsRepo.save(ocrDetails)).thenReturn(voterRes);
		});
	}

    public byte[] smsDocDownload(String applicationId, String requestType) throws IOException {
        byte[] smsDocByteCode = new byte[0];
        Properties prop = CommonUtils.readPropertyFile();
        logger.debug("Entry smsDocDownload with applicationId : {} and requestType {}", applicationId, requestType);
        switch (requestType) {
            case Constants.SANCTION:
                try {
                    DownloadReportRequest request = new DownloadReportRequest();
                    DownloadReportRequestFields fields = new DownloadReportRequestFields();
                    fields.setApplicationId(applicationId);
                    fields.setAppId(Constants.APPID);
                    request.setRequestObj(fields);
                    // -------- SANCTION LETTER --------
                    fields.setReportType("SanctionLetter");
                    LoanDetails loanDetails = loanDtlsRepo.findByApplicationId(applicationId);

				LoanDetailsPayload payload = new Gson().fromJson(loanDetails.getPayloadColumn(),
						LoanDetailsPayload.class);
                    String language = payload.getLanguage();
                    String[] vernacularIterable = Constants.NEW_VERNCLR_LANGUAGES.toUpperCase().split(",");
                    if (Arrays.asList(vernacularIterable).contains(language.toUpperCase())) {
					String filePath = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + Constants.APPID
							+ "/" + Constants.LOAN + "/" + applicationId + "/";
                        String sanctionLetterFileName = applicationId + "_SanctionLetter" + ".pdf";
                        String sanctionLetterPath = filePath + sanctionLetterFileName;

                        String kfsSheetFileName = applicationId + "_KfsSheetReport" + ".pdf";
                        String kfsSheetPath = filePath + kfsSheetFileName;
                        try {
                            Path sanctionPath = Paths.get(sanctionLetterPath);
                            Path kfsPath = Paths.get(kfsSheetPath);

                            if (Files.exists(sanctionPath) && Files.exists(kfsPath)) {
                                byte[] sanctionBytes = Files.readAllBytes(sanctionPath);
                                byte[] kfsBytes = Files.readAllBytes(kfsPath);

                                return CommonUtils.mergePdf(sanctionBytes, kfsBytes);
                            } else {
							logger.warn(
									"One or both files missing. Falling back to report generation. sanctionExists={}, kfsExists={}",
                                        Files.exists(sanctionPath), Files.exists(kfsPath));
                            }
                        } catch (IOException ex) {
                            logger.error("Error reading local files. Falling back to report generation", ex);
                        }
                    }
                    Response sanctionLetterReport = cobService.downloadReport(request, true);
                    String sanctionJson = sanctionLetterReport.getResponseBody().getResponseObj();
                    JsonObject sanctionObj = JsonParser.parseString(sanctionJson).getAsJsonObject();
                    String sanctionContent = sanctionObj.get("base64").getAsString();
				String sanctionType = sanctionObj.has("fileType") ? sanctionObj.get("fileType").getAsString() : "";
                    byte[] sanctionBytes = resolveReportBytes(sanctionContent, sanctionType);
                    // -------- KFS SHEET --------
                    fields.setReportType("KfsSheetReport");
                    Response kfsSheetReport = cobService.downloadReport(request, true);
                    String kfsJson = kfsSheetReport.getResponseBody().getResponseObj();
                    JsonObject kfsObj = JsonParser.parseString(kfsJson).getAsJsonObject();
                    String kfsContent = kfsObj.get("base64").getAsString();
				String kfsType = kfsObj.has("fileType") ? kfsObj.get("fileType").getAsString() : "";
                    byte[] kfsBytes = resolveReportBytes(kfsContent, kfsType);
                    // -------- MERGE PDFs --------
                    smsDocByteCode = CommonUtils.mergePdf(sanctionBytes, kfsBytes);
                } catch (Exception e) {
				logger.error("Exception in smsDocdownload while generating sanction reports with exception: {}",
						e.getMessage(), e);
                    throw new RuntimeException(e);
                }
                break;
            case Constants.DISBURSEMENT:
                logger.debug("Generating disbursement report for applicationId: {}", applicationId);
                try {
				Optional<Documents> loanAgreementOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(
						applicationId, Constants.LOAN_AGREEMENT, Constants.MANUAL);
                    if (loanAgreementOpt.isPresent()) {
                        Documents loanAgreement = loanAgreementOpt.get();
					String filePath = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + Constants.APPID
							+ "/" + Constants.LOAN + "/" + applicationId + "/" + Constants.MANUAL + "/";
                        String docName = loanAgreement.getDocName();
                        String docPath = filePath + docName;
                        logger.debug("Reading loan agreement from file path: {}", docPath);
                        smsDocByteCode = Files.readAllBytes(Paths.get(docPath));
                    } else {
                        logger.warn("No manual loan agreement found for applicationId: {}", applicationId);
                        throw new RuntimeException("No manual loan agreement found for applicationId: " + applicationId);
                    }
                } catch (Exception e) {
				logger.error("Exception in smsDocdownload while generating disbursement report with exception: {}",
						e.getMessage(), e);
                    throw new RuntimeException(e);
                }
                break;
            default:
                logger.error("Unknown RequestType: {}", requestType);
        }
        return smsDocByteCode;
    }

    private byte[] resolveReportBytes(String content, String type) {

        if ("html".equalsIgnoreCase(type)) {
            return CommonUtils.htmlToPdf(content);
        }
		return java.util.Base64.getDecoder().decode(content);
    }

	private void insuranceDetailSave(UploadLoanRequestFields requestObj){
		List<InsuranceDetailsWrapper> InsuranceDetailsWrapperList =  requestObj.getInsuranceDetailsWrapperList();
		logger.debug("InsuranceDetailsWrapperList size : {}", InsuranceDetailsWrapperList.size());
		logger.debug("InsuranceDetailsWrapperList Full : {}", InsuranceDetailsWrapperList);
		logger.debug("InsuranceDetailsWrapperList Request Obj : {}", requestObj);

		if(InsuranceDetailsWrapperList != null){
			for(InsuranceDetailsWrapper wrapper : InsuranceDetailsWrapperList){
				logger.debug("InsuranceDetailsWrapper : {}", wrapper);
				InsuranceDetails insuranceDetails = wrapper.getInsuranceDetails();
				Gson gson = new Gson();
				insuranceDetails.setPayloadColumn(gson.toJson(insuranceDetails.getPayload()));
				logger.debug("InsuranceDetails : {}", insuranceDetails);
				if(insuranceDetails != null){
					insuranceDtlRepo.save(insuranceDetails);
					logger.debug("Saved InsuranceDetails with ID : {}", insuranceDetails.getInsuranceDtlId());
				}
			}
		}
    }

	private String getLanguageByState(String lovDtls, String state) {
		if (lovDtls == null || state == null || state.trim().isEmpty()) {
			return "English";
		}
		try {
			JsonNode list = new ObjectMapper().readTree(lovDtls).path("StateAndLanguage");
			for (JsonNode node : list) {
				for (String part : node.path("key").asText().split("~")) {
					if (part.trim().equalsIgnoreCase(state.trim())) {
						return node.path("value").asText();
					}
				}
			}
		} catch (Exception e) {
			logger.error("Failed to resolve language for state: {}", state, e);
		}
		return "English";
	}

	/**
	 * @author Ankit.CAG CDH to Unnati Side Data Sink Helper Service function
	 */
	@Transactional
	public HashMap<String, String> populateCdhToUnnati(CallReplicateFields requestObj) {
		logger.debug("populateCdhToUnnati Called " + requestObj);
		HashMap<String, String> responseObj = new HashMap<>();
		/**
		 * @author Ankit.CAG Note:(commonParamService.populateApplnWorkFlow
		 *         (apiRequest):IGNOR) Update CdhToUnnati only @Additional Product
		 *         (Maitri-Unnnati)
		 */
		Map<String, String> allowedProducts = new HashMap<>();
		Set<ProductCode> allowed = EnumSet.of(ProductCode.FAMILY_WELFARE, ProductCode.UNNATI_SUPPLEMENTARY,
				ProductCode.UNNATI_RESTART, ProductCode.UNNATI_EMERGENCY);

		for (ProductCode pc : allowed) {
			allowedProducts.put(pc.getCode(ProductCode.ProductType.UNNATI), pc.getCode(ProductCode.ProductType.CDH));
		}
		logger.debug("populateCdhToUnnati allowedProducts " + allowedProducts);
		try {

			UnnatiIexceedCDHLead unnatiIexceedCDHLead = null;
			String applicationId = "";

			if (allowedProducts.keySet().contains(requestObj.getProduct())) {
				logger.debug("populateCdhToUnnati  allowedProducts" + requestObj.getProduct() + ":"
						+ allowedProducts.get(requestObj.getProduct()));

				List<UnnatiIexceedCDHLead> unnatiIexceedCDHLeads = unnatiIexceedCDHLeadRepo
						.findByCustomerIdAndProductAndUnnatiStatusIn(requestObj.getCustomerId(),
								allowedProducts.get(requestObj.getProduct()), Arrays.asList("Initialize", "Initiate"));

				List<UnnatiCoApplicantDetails> cdhCoApplicantDetails = coAppDetailsRepository
						.findByCustomerId(requestObj.getCustomerId() + "");
				if(cdhCoApplicantDetails.isEmpty()){
					logger.error("Coapplicant record missing in CDH");
					throw new ReplicationFailedException(
							"Coapplicant record missing in CDH for applicant : " + requestObj.getCustomerId());
				}

				if (!unnatiIexceedCDHLeads.isEmpty()) {
					logger.debug(
							"populateCdhToUnnati unnatiIexceedCDHLeads Exist size:" + unnatiIexceedCDHLeads.size());

					/*
					 * @Duplicate Entry Exception Handled
					 */
					if (unnatiIexceedCDHLeads.size() > 1) {
						throw new ReplicationFailedException(
								"CDH Present Duplicate Entry CustomerId: " + requestObj.getCustomerId()
										+ "& Product Code" + allowedProducts.get(requestObj.getProduct()));
					}
					Gson gson = new Gson();
					unnatiIexceedCDHLead = unnatiIexceedCDHLeads.get(0);

					logger.debug("unnatiIexceedCDHLeads :" + unnatiIexceedCDHLead);
					UnnCbResponseDto unnCbResponseDto = new UnnCbResponseDto();

					try {
						/* @uthour Abhishek.Raj.CAGL */
						logger.debug("Extract unnCbResponse ");
						String unnCbResponse = unnatiIexceedCDHLead.getUnnCbResponse();
						logger.debug("unnCbResponse " + unnCbResponse);
						if (unnCbResponse != null && !unnCbResponse.trim().isEmpty())
							unnCbResponseDto = gson.fromJson(unnCbResponse, UnnCbResponseDto.class);
						logger.debug("unnCbResponseDto " + unnCbResponseDto);
					} catch (Exception e) {

						logger.debug("Error while Extract unnCbResponse " + e.getMessage());
					}

					/**
					 * @generate unique ApplicationId
					 */
					applicationId = CommonUtils.generateRandomNumStr();

					List<ApplicationMaster> appMasterLead = applicationMasterRepo
							.findBySearchCode2IgnoreCaseAndProductCodeAndWorkitemNo(requestObj.getCustomerId(),
									requestObj.getProduct(), unnatiIexceedCDHLead.getReferenceId());

					ApplicationMaster appMaster = null;
					if (appMasterLead.isEmpty()) {
						logger.warn("ApplicationMaster Not Present");
						appMaster = new ApplicationMaster();
						appMaster.setAppId(requestObj.getAppId());
						appMaster.setApplicationId(applicationId);
						appMaster.setVersionNum(requestObj.getVersionNum());
					} else {
						appMaster = appMasterLead.get(0);
						/* Re-Assign Application ID if already Exist */
						applicationId = appMaster.getApplicationId();
						logger.warn("ApplicationMaster Present:" + appMaster);
					}
					appMaster.setApplicationDate(LocalDate.now());
					appMaster.setApplicationStatus(AppStatus.CACOMPLETED.getValue());
					appMaster.setCreatedBy(requestObj.getUserId());
					appMaster.setUpdatedBy(requestObj.getUserId());
					appMaster.setApplicantsCount(null);
					appMaster.setEmailId(unnatiIexceedCDHLead.getBranchEMailId());
					appMaster.setMobileNumber(unnatiIexceedCDHLead.getMobileNumber());
					// converting @Maitri-Unnati product code
					appMaster.setProductCode(requestObj.getProduct());
					appMaster.setProductGroupCode(Constants.LOAN);
					appMaster.setCurrentScreenId("");
					appMaster.setCustomerId(new BigDecimal(requestObj.getCustomerId()));
					appMaster.setSearchCode1(unnatiIexceedCDHLead.getKendraId());
					appMaster.setSearchCode2(requestObj.getCustomerId());
					appMaster.setSourceOfApplication(unnatiIexceedCDHLead.getSourceOfTheApplication());
					if (!(CommonUtils.isNullOrEmpty(unnatiIexceedCDHLead.getMobileNumber()))) {
						appMaster.setMobileVerStatus("Y");
					}
					if (!(CommonUtils.isNullOrEmpty(unnatiIexceedCDHLead.getBranchEMailId()))) {
						appMaster.setEmailVerStatus("Y");
					}
					appMaster.setKendraId(unnatiIexceedCDHLead.getKendraId());
					appMaster.setKendraName(unnatiIexceedCDHLead.getKendraName());
					// Need to check (Branch Mapping)
					appMaster.setBranchId(unnatiIexceedCDHLead.getGlBranchId());
					appMaster.setBranchName(unnatiIexceedCDHLead.getGlBranchName());
					appMaster.setMemberId(requestObj.getCustomerId());
					appMaster.setPrimaryKycType(Constants.VOTER);
					appMaster.setPrimaryKycId(unnatiIexceedCDHLead.getPrimaryKyc());
					appMaster.setSecondaryKycType("");
					appMaster.setSecondaryKycId("");
					appMaster.setCurrentStageNo(null);
					appMaster.setAlternateVoterId("");
					appMaster.setWorkitemNo(unnatiIexceedCDHLead.getReferenceId());
					boolean isAnyBranchWhitelisted = whitelistedBranchesRepository
							.isAnyBranchWhitelisted(Arrays.asList(unnatiIexceedCDHLead.getGlBranchId()));
					if (isAnyBranchWhitelisted) {
						appMaster.setDeclarationFlag(Constants.IEXCEED_FLAG);
					}
					logger.debug("Updated appMaster : " + appMaster);

					try {
						ApplicationMaster save = applicationMasterRepo.saveAndFlush(appMaster);
						logger.debug("ApplicationMaster Saved::{}", save);
					} catch (Exception e) {

						logger.error("Failed to save UnnatiLeadQuestionnaire: ", e);
//						
						throw new ReplicationFailedException("ApplicationMaster",
								buildFailureDetail("ApplicationMaster", e), e);
					}
					// ---------------@ApplicationWorkflow--------

					Optional<ApplicationWorkflow> workflowObjOpt = applnWfRepository
							.findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(requestObj.getAppId(),
									applicationId, Constants.INITIAL_VERSION_NO);
					if (!workflowObjOpt.isPresent()) {
						logger.debug("applnWfRepository Not present");
						ApplicationWorkflow applicationWorkflow = new ApplicationWorkflow();
						applicationWorkflow.setAppId(requestObj.getAppId());
						applicationWorkflow.setApplicationId(applicationId);
						applicationWorkflow.setVersionNum(1);
						applicationWorkflow.setWorkflowSeqNum(1);
						applicationWorkflow.setCreatedBy(requestObj.getUserId());
						applicationWorkflow.setCreateTs(LocalDateTime.now());
						applicationWorkflow.setApplicationStatus(Constants.CACOMPLETED);
						applicationWorkflow.setRemarks("Moved form Maitri to Unnati");
						applicationWorkflow.setCurrentRole("Approver");
						applicationWorkflow.setNextWorkFlowStage("SANCTION");
						applnWfRepository.saveAndFlush(applicationWorkflow);
						logger.debug("applnWfRepository saved " + applicationWorkflow);
					} else {
						logger.debug("ApplnWfRepository Not Present");
						throw new ReplicationFailedException(
								"ApplicationWorkflow Already Exist" + requestObj.getCustomerId());
					}

					// ----------@LoanDetails--------

					logger.debug("Updating LoanDetails for requestObj: " + requestObj);

					LoanDetails loanData = loanDtlsRepo.findByApplicationId(applicationId);

					if (loanData == null) {
						BigDecimal loanDtlId = new BigDecimal(CommonUtils.generateRandomNumStr());
						loanData = new LoanDetails();
						loanData.setLoanDtlId(loanDtlId);
						logger.debug("LoanDetails Not present loanDtlId: " + loanDtlId);
					} else
						logger.debug("LoanDetails Present:" + loanData);

					loanData.setApplicationId(applicationId);
					/* Storing UNNATI type Product code */
					loanData.setProductCode(requestObj.getProduct());
					loanData.setAppId(requestObj.getAppId());
					loanData.setVersionNum(requestObj.getVersionNum());
					loanData.setLoanAmount(unnCbResponseDto.getApproved_Loan_Amount());
					loanData.setTenureInMonths(null);
					loanData.setTenure(Integer.valueOf(unnCbResponseDto.getFinal_Tenure()));
					loanData.setRoi(Float.valueOf(unnCbResponseDto.getROI()));
					loanData.setInterest(Float.valueOf(unnCbResponseDto.getROI()));
					loanData.setLoanClosureDate(null);
					loanData.setTotPayableAmount(null);
					loanData.setAutoEmiAccount(null);
					loanData.setAutoEmiAccountType(null);
					loanData.setEmiDate(null);
					loanData.setLoanCrAccount(unnCbResponseDto.getApproved_Loan_Amount() + "");
					loanData.setLoanCrAccountType(null);
					loanData.setMonthlyEmi(BigDecimal.valueOf(unnCbResponseDto.getApproved_Loan_EMI()));
					loanData.setSanctionedLoanAmount(unnCbResponseDto.getApproved_Loan_Amount());
					loanData.setBmRecommendedLoanAmount(unnCbResponseDto.getApproved_Loan_Amount());
					loanData.setOldBmRecommendedLoanAmount(unnCbResponseDto.getApproved_Loan_Amount());
					loanData.setOldBmRecommendedLoanAmount(unnCbResponseDto.getApproved_Loan_Amount());
					loanData.setOldSanctionRecommendedLoanAmount(unnCbResponseDto.getApproved_Loan_Amount());
					loanData.setLoanClosureDate(null);
					loanData.setCoapplicantId(cdhCoApplicantDetails.get(0).getCoCustomerId());
					
					if(appMaster.getProductCode().equalsIgnoreCase(ProductCode.UNNATI_RESTART.getUnnatiCode())) {
						loanData.setLoanAmount(unnatiIexceedCDHLead.getCrtApprovedAmount());
						loanData.setLoanCrAccount(unnatiIexceedCDHLead.getCrtApprovedAmount() + "");
						loanData.setSanctionedLoanAmount(unnatiIexceedCDHLead.getCrtApprovedAmount());
						loanData.setBmRecommendedLoanAmount(unnatiIexceedCDHLead.getCrtApprovedAmount());
						loanData.setOldBmRecommendedLoanAmount(unnatiIexceedCDHLead.getCrtApprovedAmount());
						loanData.setOldBmRecommendedLoanAmount(unnatiIexceedCDHLead.getCrtApprovedAmount());
						loanData.setOldSanctionRecommendedLoanAmount(unnatiIexceedCDHLead.getCrtApprovedAmount());
					}

					LovMaster languageLov = lovMasterRepository.findByLovIdAndLovName(60, "LOANDETAILS");
					String language = getLanguageByState(languageLov != null ? languageLov.getLovDtls() : null,
							unnatiIexceedCDHLead.getGlBranchState());


					LoanDetailsPayload loanDetailsPayload = LoanDetailsPayload.builder().loanPurpose(unnatiIexceedCDHLead.getLoanPurpose())
							.modeOfSecurity("").subCategory(unnatiIexceedCDHLead.getLoanSubPurpose()).modeOfDisbursement("NEFT").language(language)
							.frequencyOfRepayment(unnCbResponseDto.getRepayment_frequency()).initialBrePayload(null)
							.build();
					String loanDetailsPayloadStr = gson.toJson(loanDetailsPayload);
					logger.debug("loanDetailsPayloadStr" + loanDetailsPayloadStr);
					loanData.setPayloadColumn(loanDetailsPayloadStr);
					try {
						LoanDetails save = loanDtlsRepo.saveAndFlush(loanData);
						logger.debug("LoanDetails Saved::{}", save);
					} catch (Exception e) {

						logger.error("Failed to save LoanDetails: ", e);
						throw new ReplicationFailedException("LoanDetails", buildFailureDetail("ApplicationMaster", e),
								e);
					}

					logger.debug("populateLoanDetails loanDtlsRepo Save");

					// ---------------@CustomerDetails Table APPLICANT & COAPPLICANT Both-----------

					logger.debug("Checking CustomerDetails");

					Optional<CustomerDetails> CustomerDetailsData = custDtlRepo
							.findByApplicationIdAndCustomerType(applicationId, Constants.APPLICANT);

					CustomerDetails customerDetail = null;
					BigDecimal AppCustDtlId = new BigDecimal(0);

					if (!CustomerDetailsData.isPresent()) {
						AppCustDtlId = CommonUtils.generateRandomNum();
						logger.debug("CustomerDetailsData not present: AppCustDtlId=" + AppCustDtlId);
						customerDetail = new CustomerDetails();
						customerDetail.setCustDtlId(AppCustDtlId);
					} else {
						customerDetail = CustomerDetailsData.get();
						AppCustDtlId = customerDetail.getCustDtlId();
						logger.debug("CustomerDetailsData  present: AppCustDtlId=" + AppCustDtlId);
					}

					customerDetail.setAppId(requestObj.getAppId());
					customerDetail.setApplicationId(applicationId);
					customerDetail.setVersionNum(requestObj.getVersionNum());
					customerDetail.setCustomerType(Constants.APPLICANT);
					customerDetail.setCustomerName(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getFullName()));
					customerDetail.setMobileNumber(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getMobileNumber()));
					customerDetail.setSeqNumber(2);
					customerDetail.setKycStatus("Pending");
					customerDetail.setAmlStatus("Pending");
					try {
						customerDetail.setCustomerId(new BigDecimal(requestObj.getCustomerId()));
					} catch (Exception e) {
						customerDetail.setCustomerId(new BigDecimal(0));
					}
					logger.debug("inside populateRenewalCustomerDtls customerDetail 1 " + customerDetail.toString());

					CustomerDetailsPayload payloadObj = new CustomerDetailsPayload();

					payloadObj.setTitle(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getTitle()));
					payloadObj.setDob(formatDate(unnatiIexceedCDHLead.getDateOfBirth(), "yyyy-MM-dd"));
					payloadObj.setAge(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getAge()));
					payloadObj.setGender(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getGender()));
					payloadObj.setMaritalStatus(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getMaritalStatus()));
					// payloadObj.setPan(getDefaultValueIfObjNull(renewalLeadDetail.getCopan()));
					payloadObj.setSpouseName(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getSpouseName()));
					payloadObj.setFathersName(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getFatherName()));
					// payloadObj.setAadhaarNumber(getDefaultValueIfObjNull(renewalLeadDetail.getCoA));
					payloadObj.setOccupation(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getOccupation()));
					payloadObj.setCustId(requestObj.getCustomerId());
					payloadObj.setFirstName(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getFirstName()));
					payloadObj.setMiddleName(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getMiddleName()));
					payloadObj.setLastName(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getLastName()));
					payloadObj.setRelationShipWithApplicant(
							getDefaultValueIfObjNull(""));
					payloadObj.setNamePerKyc(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getNameAsPerBankAccount()));// need
																														// clarify
					payloadObj.setReligion(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getAppReligion()));
					payloadObj.setCaste(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getAppCaste()));
					payloadObj.setPrimaryKycType(getDefaultValueIfObjNull(Constants.VOTER_ID));
					payloadObj.setPrimaryKycId(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getPrimaryKyc()));
					payloadObj.setPrimaryKycIdValStatus(Constants.VERIFIED_STS);
					payloadObj.setSecondaryKycType("");
					payloadObj.setSecondaryKycId(getDefaultValueIfObjNull(""));
					payloadObj.setSecondaryKycIdValStatus("");
					payloadObj.setGkCustomerType("Applicant");
					payloadObj.setEducation(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getAppEducation()));
					payloadObj.setAlternateVoterId("");
					payloadObj.setAlternateVoterIdValStatus("");
					payloadObj.setCustomerIndex(2);// need clarify

					String payload = gson.toJson(payloadObj);
					logger.debug("inside populateRenewalCustomerDtls  APP payload " + payload.toString());
					customerDetail.setPayloadColumn(payload);
					logger.debug("inside populateRenewalCustomerDtls APP customerDetail  " + customerDetail.toString());
					try {
						CustomerDetails save = custDtlRepo.saveAndFlush(customerDetail);
						logger.debug("customerDetail Saved::{}", save);
					} catch (Exception e) {

						logger.error("Failed to save customerDetail: ", e);
						throw new ReplicationFailedException("customerDetail", buildFailureDetail("customerDetail", e),
								e);
					}

					// ====================@COAPPLICANT=======================

					logger.debug("Checking co-CustomerDetails");

					BigDecimal coAppCustDtlId = new BigDecimal(0);

					UnnatiCoApplicantDetails unnatiCoApplicantDetails = new UnnatiCoApplicantDetails();

					if (cdhCoApplicantDetails != null && !cdhCoApplicantDetails.isEmpty()) {
						unnatiCoApplicantDetails = cdhCoApplicantDetails.get(0);
						logger.debug("UnnatiCoApplicantDetails:=" + unnatiCoApplicantDetails);

						Optional<CustomerDetails> CoCustomerDetailsData = custDtlRepo
								.findByApplicationIdAndCustomerType(applicationId, Constants.COAPPLICANT);

						CustomerDetails coCustomerDetail = null;
						if (!CoCustomerDetailsData.isPresent()) {
							coAppCustDtlId = CommonUtils.generateRandomNum();
							logger.debug("CoCustomerDetailsData not present: coAppCustDtlId=" + coAppCustDtlId);
							coCustomerDetail = new CustomerDetails();
							coCustomerDetail.setCustDtlId(coAppCustDtlId);
						} else {
							coCustomerDetail = CoCustomerDetailsData.get();
							coAppCustDtlId = coCustomerDetail.getCustDtlId();
							logger.debug("CoCustomerDetailsData  present: coAppCustDtlId=" + coAppCustDtlId);
						}

						coCustomerDetail.setAppId(requestObj.getAppId());
						coCustomerDetail.setApplicationId(applicationId);
						coCustomerDetail.setVersionNum(requestObj.getVersionNum());
						coCustomerDetail.setCustomerType(Constants.COAPPLICANT);
						coCustomerDetail
								.setCustomerName(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoFullName()));
						coCustomerDetail
								.setMobileNumber(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoMobileNo()));
						coCustomerDetail.setSeqNumber(2);
						coCustomerDetail.setKycStatus("Pending");
						coCustomerDetail.setAmlStatus("Pending");
						try {
							coCustomerDetail.setCustomerId(new BigDecimal(requestObj.getCustomerId()));
						} catch (Exception e) {
							coCustomerDetail.setCustomerId(new BigDecimal(0));
						}

						logger.debug(
								"inside populateRenewalCustomerDtls cocustomerDetail 1 " + coCustomerDetail.toString());
						CustomerDetailsPayload copayloadObj = new CustomerDetailsPayload();

						copayloadObj.setTitle(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoTitle()));
						copayloadObj.setDob(formatDate(unnatiCoApplicantDetails.getCoDateOfBirth(), "yyyy-MM-dd"));
						copayloadObj.setAge(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoAge()));
						copayloadObj.setGender(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoGender()));
						copayloadObj.setMaritalStatus(
								getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoMaritalStatus()));
						// copayloadObj.setPan(getDefaultValueIfObjNull(renewalLeadDetail.getCopan()));
						copayloadObj
								.setSpouseName(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoSpouseName()));
						copayloadObj
								.setFathersName(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoFatherName()));
						// copayloadObj.setAadhaarNumber(getDefaultValueIfObjNull(renewalLeadDetail.getCoA));
						copayloadObj
								.setOccupation(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoOccupation()));
						copayloadObj.setCustId("");
						copayloadObj.setFirstName(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoFirstname()));
						copayloadObj
								.setMiddleName(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoMiddlename()));
						copayloadObj.setLastName(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoLastname()));
						logger.debug("relationship with applicant: " + unnatiCoApplicantDetails.getCoRelationshipwithapplicant());
						copayloadObj.setRelationShipWithApplicant(
								getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoRelationshipwithapplicant()));
						logger.debug("copayloadObj.setRelationShipWithApplicant : {}", copayloadObj.getRelationShipWithApplicant());
						copayloadObj
								.setNamePerKyc(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoNameperkyc()));// need
																														// clarify
						copayloadObj.setReligion(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoReligion()));
						copayloadObj.setCaste(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoCaste()));
						copayloadObj.setPrimaryKycType(getDefaultValueIfObjNull(Constants.VOTER_ID));
						copayloadObj.setPrimaryKycId(
								getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoVoterIdNo() + ""));
						copayloadObj.setPrimaryKycIdValStatus(Constants.VERIFIED_STS);
						copayloadObj.setSecondaryKycType("");
						copayloadObj.setSecondaryKycId(getDefaultValueIfObjNull(""));
						copayloadObj.setSecondaryKycIdValStatus(Constants.VERIFIED_STS);
						copayloadObj.setGkCustomerType("Applicant");
						copayloadObj.setEducation(getDefaultValueIfObjNull(unnatiCoApplicantDetails.getCoEducation()));
						copayloadObj.setAlternateVoterId("");
						copayloadObj.setAlternateVoterIdValStatus("");
						copayloadObj.setCustomerIndex(2);// need clarify

						String copayload = gson.toJson(copayloadObj);
						logger.debug("inside populateRenewalCustomerDtls  APP copayload " + copayload.toString());
						coCustomerDetail.setPayloadColumn(copayload);
						logger.debug("inside populateRenewalCustomerDtls APP coCustomerDetail  "
								+ coCustomerDetail.toString());
						try {
							CustomerDetails save = custDtlRepo.saveAndFlush(coCustomerDetail);
							logger.debug("coCustomerDetail Saved::{}", save);
						} catch (Exception e) {

							logger.error("Failed to save coCustomerDetail: ", e);
							throw new ReplicationFailedException("coCustomerDetail",
									buildFailureDetail("coCustomerDetail", e), e);
						}
					} else
						logger.error("coCustomerDetail not save : cdhCoApplicantDetails not avaliable");

					// ------@OccupationDetails------

					logger.debug("Updateing RenewalOccupationdtls requestObj: " + requestObj);
					List<UnnatiIexceedOccpInsr> UnnatiIexceedOccpInsr = iexceedOccpInsrRepository
							.findByCustomerId(requestObj.getCustomerId());

					UnnatiIexceedOccpInsr cdhOccpInsrData = null;

					if (!UnnatiIexceedOccpInsr.isEmpty()) {

						List<OccupationDetails> OccupationDetails = occupationDtlRepo
								.findByApplicationId(requestObj.getApplicationId());
						OccupationDetails occupationDetail = null;
						OccupationDetails coAppOccupationDetail = null;
						if (OccupationDetails.isEmpty()) {
							occupationDetail = new OccupationDetails();
							occupationDetail.setOccptDtlId(CommonUtils.generateRandomNum());
							coAppOccupationDetail = new OccupationDetails();
							coAppOccupationDetail.setOccptDtlId(CommonUtils.generateRandomNum());
							logger.debug("OccupationDetails not Present Gen(OccptDtlId)-> occupationDetail: "
									+ occupationDetail.getOccptDtlId() + " coAppOccupationDetail: "
									+ coAppOccupationDetail.getOccptDtlId());
						} else {
							occupationDetail = OccupationDetails.get(0);
							logger.debug("occupationDetail Present:" + occupationDetail);
							if (OccupationDetails.size() > 1) {
								coAppOccupationDetail = OccupationDetails.get(1);
								logger.debug("coAppOccupationDetail Present:" + occupationDetail);
							} else {
								coAppOccupationDetail = new OccupationDetails();
								BigDecimal coAppoccptnDtlId = CommonUtils.generateRandomNum();
								logger.debug("coAppOccupationDetail New created co-OccptDtlId:" + coAppoccptnDtlId);
								coAppOccupationDetail.setOccptDtlId(coAppoccptnDtlId);
							}

						}
						logger.debug("Updateing UnnatiIexceedOccpInsr" + UnnatiIexceedOccpInsr);
						cdhOccpInsrData = UnnatiIexceedOccpInsr.get(0);

						occupationDetail.setAppId(requestObj.getAppId());
						occupationDetail.setApplicationId(applicationId);
						occupationDetail.setCustDtlId(AppCustDtlId);// Need Clarification
						occupationDetail.setVersionNum(requestObj.getVersionNum());

						OccupationDetailsPayload appPayload = new OccupationDetailsPayload();
						appPayload.setOccupationType(getDefaultValueIfObjNull(cdhOccpInsrData.getOccupationType()));
						appPayload.setDesignation(getDefaultValueIfObjNull(cdhOccpInsrData.getDesignation()));
						appPayload.setAnnualIncome(cdhOccpInsrData.getAnnualIncome() == null ? null
								: new BigDecimal(cdhOccpInsrData.getAnnualIncome()));
						appPayload.setOrganisationName(getDefaultValueIfObjNull(cdhOccpInsrData.getOrganisationName()));
						appPayload.setOfficePhone("");
						appPayload.setOfficeEmail("");
						appPayload.setEmployeeId("");
						appPayload.setEmployeeSince(getDefaultValueIfObjNull(cdhOccpInsrData.getEmpSince()));
						appPayload.setExperience(getDefaultValueIfObjNull(cdhOccpInsrData.getExperience()));
						appPayload.setEmployer(getDefaultValueIfObjNull(cdhOccpInsrData.getEmployer()));
						appPayload.setRetirementAge(getDefaultValueIfObjNull(cdhOccpInsrData.getRetirementAge()));
						appPayload.setLastEmployer(getDefaultValueIfObjNull(cdhOccpInsrData.getLastEmployer()));
						appPayload.setPreviousJobYears(getDefaultValueIfObjNull(cdhOccpInsrData.getPrevJobYears()));
						appPayload.setTypeOfEmployer(getDefaultValueIfObjNull(cdhOccpInsrData.getTypeOfEmployer()));
						appPayload.setNatureOfOccupation(getDefaultValueIfObjNull(cdhOccpInsrData.getNatureOfOccpn()));
						appPayload.setAddressProof("");
						appPayload.setBusinessAddressProof(getDefaultValueIfObjNull(cdhOccpInsrData.getBussAddProof()));
						appPayload.setEmploymentProof(getDefaultValueIfObjNull(cdhOccpInsrData.getEmploymentProof()));
						appPayload.setEmployeeActivity(getDefaultValueIfObjNull(cdhOccpInsrData.getEmployeeActivity()));
						appPayload.setBusinessPremiseOwnerShip(
								getDefaultValueIfObjNull(cdhOccpInsrData.getBussPremOwnship()));
						appPayload.setFreqOfIncome(getDefaultValueIfObjNull(cdhOccpInsrData.getFreqOfIncome()));
						appPayload.setOtherSourceIncome(getDefaultValueIfObjNull(cdhOccpInsrData.getOtherSrcIncome()));
						appPayload.setOtherSourceAnnualIncome(
								getDefaultValueIfObjNull(cdhOccpInsrData.getOtherSrcAnnInc()));
						appPayload.setStreetVendor(getDefaultValueIfObjNull(cdhOccpInsrData.getStreetVendor()));
						appPayload.setModeOfIncome("");
						appPayload.setTypeofbusiness(getDefaultValueIfObjNull(cdhOccpInsrData.getTypeOfBuss()));
						appPayload.setBusinessEmpStartDate(CommonUtils.formatCustomDate(
								getDefaultValueIfObjNull(cdhOccpInsrData.getBussEmpStartDate()), "dd/MM/yyyy"));
						appPayload.setBusinessEmpVintageYear(
								getDefaultValueIfObjNull(cdhOccpInsrData.getBussEmpVintage()));
						appPayload.setOccupationTag(getDefaultValueIfObjNull(cdhOccpInsrData.getOccupationTag()));
						String PayloadStr = gson.toJson(appPayload);
						occupationDetail.setPayloadColumn(PayloadStr);

						logger.debug("occupationDetail " + occupationDetail);

						try {
							OccupationDetails save = occupationDtlRepo.saveAndFlush(occupationDetail);
							logger.debug("App-OccupationDetails Saved::{}", save);
						} catch (Exception e) {

							logger.error("Failed to save App-OccupationDetails: ", e);
//							TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//							responseObj.put("message", buildFailureDetail("App-OccupationDetails", e));
//							responseObj.put("status", FAIL_MSG);
//							return responseObj;
							throw new ReplicationFailedException("App-OccupationDetails",
									buildFailureDetail("App-OccupationDetails", e), e);
						}

						coAppOccupationDetail.setAppId(requestObj.getAppId());
						coAppOccupationDetail.setApplicationId(applicationId);
						coAppOccupationDetail.setCustDtlId(coAppCustDtlId);
						coAppOccupationDetail.setVersionNum(requestObj.getVersionNum());

						OccupationDetailsPayload coappPayload = new OccupationDetailsPayload();

						coappPayload.setOccupationType(getDefaultValueIfObjNull(cdhOccpInsrData.getCoOccupationType()));
						coappPayload.setDesignation(getDefaultValueIfObjNull(cdhOccpInsrData.getCoDesignation()));
						coappPayload.setAnnualIncome(cdhOccpInsrData.getCoAnnualIncome() == null ? null
								: new BigDecimal(cdhOccpInsrData.getCoAnnualIncome()));
						coappPayload
								.setOrganisationName(getDefaultValueIfObjNull(cdhOccpInsrData.getCoOrganisationName()));
						coappPayload.setOfficePhone("");
						coappPayload.setOfficeEmail("");
						coappPayload.setEmployeeId("");
						coappPayload.setEmployeeSince(getDefaultValueIfObjNull(cdhOccpInsrData.getCoEmpSince()));
						coappPayload.setExperience(getDefaultValueIfObjNull(cdhOccpInsrData.getCoExperience()));
						coappPayload.setEmployer(getDefaultValueIfObjNull(cdhOccpInsrData.getCoEmployer()));
						coappPayload.setRetirementAge(getDefaultValueIfObjNull(cdhOccpInsrData.getCoRetirementAge()));
						coappPayload.setLastEmployer(getDefaultValueIfObjNull(cdhOccpInsrData.getCoLastEmployer()));
						coappPayload.setPreviousJobYears(getDefaultValueIfObjNull(cdhOccpInsrData.getCoPrevJobYears()));
						coappPayload.setTypeOfEmployer(getDefaultValueIfObjNull(cdhOccpInsrData.getCoTypeOfEmployer()));
						coappPayload
								.setNatureOfOccupation(getDefaultValueIfObjNull(cdhOccpInsrData.getCoNatureOfOccpn()));
						coappPayload.setAddressProof("");
						coappPayload
								.setBusinessAddressProof(getDefaultValueIfObjNull(cdhOccpInsrData.getCoBussAddProof()));
						coappPayload
								.setEmploymentProof(getDefaultValueIfObjNull(cdhOccpInsrData.getCoEmploymentProof()));
						coappPayload
								.setEmployeeActivity(getDefaultValueIfObjNull(cdhOccpInsrData.getCoEmployeeActivity()));
						coappPayload.setBusinessPremiseOwnerShip(
								getDefaultValueIfObjNull(cdhOccpInsrData.getCoBussPremOwnship()));
						coappPayload.setFreqOfIncome(getDefaultValueIfObjNull(cdhOccpInsrData.getCoFreqOfIncome()));
						coappPayload
								.setOtherSourceIncome(getDefaultValueIfObjNull(cdhOccpInsrData.getCoOtherSrcIncome()));
						coappPayload.setOtherSourceAnnualIncome(
								getDefaultValueIfObjNull(cdhOccpInsrData.getCoOtherSrcAnnInc()));
						coappPayload.setStreetVendor(getDefaultValueIfObjNull(cdhOccpInsrData.getCoStreetVendor()));
						coappPayload.setModeOfIncome("");
						coappPayload.setTypeofbusiness(getDefaultValueIfObjNull(cdhOccpInsrData.getCoTypeOfBuss()));
						coappPayload.setBusinessEmpStartDate(CommonUtils.formatCustomDate(
								getDefaultValueIfObjNull(cdhOccpInsrData.getCoBussEmpStartDate()), "dd/MM/yyyy"));
						coappPayload.setBusinessEmpVintageYear(
								getDefaultValueIfObjNull(cdhOccpInsrData.getCoBussEmpVintage()));
						coappPayload.setOccupationTag(getDefaultValueIfObjNull(cdhOccpInsrData.getCoOccupationTag()));
						String coAppPayloadStr = gson.toJson(coappPayload);
						coAppOccupationDetail.setPayloadColumn(coAppPayloadStr);

						logger.debug("coappPayload TB_ABOB_OCCUPATION_DETAILS " + coAppOccupationDetail);

						try {
							OccupationDetails save = occupationDtlRepo.saveAndFlush(coAppOccupationDetail);
							logger.debug("Co-OccupationDetails Saved::{}", save);
						} catch (Exception e) {

							logger.error("Failed to save CO-OccupationDetails: ", e);

							throw new ReplicationFailedException("Co-OccupationDetails",
									buildFailureDetail("App-OccupationDetails", e), e);
						}

					} else
						logger.warn("UnnatiIexceedOccpInsr CDH Not Found");

					// -------------@InsuranceDetails---------

					logger.warn("Updating InsuranceDtls:Additional_LOAN_PRODUCT_CODE: " + requestObj.getProduct());

						logger.warn("UnnatiIexceedOccpInsr", UnnatiIexceedOccpInsr);

						/* GET Existing Records */
						List<InsuranceDetails> insuranceDetails = insuranceDtlRepo
								.findByApplicationId(requestObj.getApplicationId());

						InsuranceDetails appInsuranceDetail = null;
						InsuranceDetails coappInsuranceDetail = null;

						if (!insuranceDetails.isEmpty()) {
							for (InsuranceDetails data : insuranceDetails) {
								if (Constants.APPLICANT.equals(data.getInsuredCustType())) {
									appInsuranceDetail = data;
									logger.warn("insuranceDetails APPLICANT present");
								} else if (Constants.COAPPLICANT.equals(data.getInsuredCustType())) {
									coappInsuranceDetail = data;
									logger.warn("insuranceDetails COAPPLICANT present");
								}
							}
						}
						/* Create Applicant object if not found */
						if (appInsuranceDetail == null) {
							appInsuranceDetail = new InsuranceDetails();
							appInsuranceDetail.setInsuranceDtlId(CommonUtils.generateRandomNum());
							appInsuranceDetail.setInsuredCustType(Constants.APPLICANT);
						}
						/* Create Co-Applicant object if not found */
						if (coappInsuranceDetail == null) {
							coappInsuranceDetail = new InsuranceDetails();
							coappInsuranceDetail.setInsuranceDtlId(CommonUtils.generateRandomNum());
							coappInsuranceDetail.setInsuredCustType(Constants.COAPPLICANT);
						}

						logger.warn("insuranceDetails present appInsuranceDetail:", appInsuranceDetail);
						logger.warn("insuranceDetails present coappInsuranceDetail:", coappInsuranceDetail);

						logger.debug("Updateing InsuranceDetails" + insuranceDetails);
//						UnnatiIexceedOccpInsr cdhOccpInsrData1 = UnnatiIexceedOccpInsr.get(0);
//						logger.debug("Get  cdhOccpInsrData" + cdhOccpInsrData1);
					String appPayloadInsuranceReqd = getDefaultValueIfObjNull(unnatiIexceedCDHLead.getInsuranceReqd());
					if (appPayloadInsuranceReqd == null) {
						appPayloadInsuranceReqd = "";
					}

					String coappPayloadInsuranceReqd = getDefaultValueIfObjNull(
							unnatiIexceedCDHLead.getCoInsuranceReqd());
					if (coappPayloadInsuranceReqd == null) {
						coappPayloadInsuranceReqd = "";
					}

					String insuranceOption = "Noinsurance";
					if(appPayloadInsuranceReqd.equalsIgnoreCase("Y") && coappPayloadInsuranceReqd.equalsIgnoreCase("Y")) {
						insuranceOption = "Both";
					}else if(appPayloadInsuranceReqd.equalsIgnoreCase("Y")
							&& ( StringUtils.isBlank(coappPayloadInsuranceReqd)|| coappPayloadInsuranceReqd.equalsIgnoreCase("N"))) {
						insuranceOption = "Applicant";
					}

					appInsuranceDetail.setAppId(requestObj.getAppId());
						appInsuranceDetail.setApplicationId(applicationId);
						appInsuranceDetail.setCustDtlId(AppCustDtlId);
						appInsuranceDetail.setVersionNum(requestObj.getVersionNum());
						appInsuranceDetail.setInsuredCustType(Constants.APPLICANT);
						InsuranceDetailsPayload appPayload = new InsuranceDetailsPayload();

						appPayload.setInsuranceReqd(appPayloadInsuranceReqd);
						appPayload.setInsuredName("");
						appPayload.setNomineeName(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getNomineeName()));
						appPayload.setNomineeRelation(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getNomineeRelation()));
						appPayload.setNomineeDob("");
						appPayload.setInsuranceOption(insuranceOption);
						appPayload.setAge("");
						appPayload.setCoApplicantInsurance("");
						String appPayloadStr = gson.toJson(appPayload);
						appInsuranceDetail.setPayloadColumn(appPayloadStr);

						logger.warn("TB_CGOB_INSURANCE_DTLS: " + appInsuranceDetail);

						try {
							InsuranceDetails save = insuranceDtlRepo.saveAndFlush(appInsuranceDetail);
							logger.debug("App-InsuranceDetails Saved::{}", save);
						} catch (Exception e) {

							logger.error("Failed to save App-InsuranceDetails: ", e);
							throw new ReplicationFailedException("App-InsuranceDetails",
									buildFailureDetail("App-InsuranceDetails", e), e);
						}

						coappInsuranceDetail.setAppId(requestObj.getAppId());
						coappInsuranceDetail.setApplicationId(applicationId);
						coappInsuranceDetail.setCustDtlId(coAppCustDtlId);
						coappInsuranceDetail.setVersionNum(requestObj.getVersionNum());
						coappInsuranceDetail.setInsuredCustType(Constants.COAPPLICANT);
						InsuranceDetailsPayload coappPayload = new InsuranceDetailsPayload();

						coappPayload.setInsuranceReqd(coappPayloadInsuranceReqd);
						coappPayload.setInsuredName("");
						coappPayload.setNomineeName(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getNomineeName()));
						coappPayload
								.setNomineeRelation(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getNomineeRelation()));
						coappPayload.setNomineeDob("");
						coappPayload.setInsuranceOption(insuranceOption);
						coappPayload.setAge(getDefaultValueIfObjNull(""));
						coappPayload.setCoApplicantInsurance(
								getDefaultValueIfObjNull(""));
						String coappPayloadStr = gson.toJson(coappPayload);
						coappInsuranceDetail.setPayloadColumn(coappPayloadStr);
						logger.warn("Co-TB_CGOB_INSURANCE_DTLS " + coappInsuranceDetail);
						try {
							InsuranceDetails save = insuranceDtlRepo.saveAndFlush(coappInsuranceDetail);
							logger.debug("CO-InsuranceDetails Saved::{}", save);
						} catch (Exception e) {

							logger.error("Failed to save CO-InsuranceDetails: " + e);
							throw new ReplicationFailedException("CO-InsuranceDetails",
									buildFailureDetail("CO-InsuranceDetails", e), e);
						}
					

					// ---------@address Details------------
					try {
						if (!UnnatiIexceedOccpInsr.isEmpty()) {
							cdhOccpInsrData = UnnatiIexceedOccpInsr.get(0);
						}
						logger.debug("populateLeadAddressDtls called");
						populateLeadAddressDtls(unnatiIexceedCDHLead, unnatiCoApplicantDetails, cdhOccpInsrData,
								requestObj.getAppId(), applicationId, AppCustDtlId, coAppCustDtlId, 1);
					} catch (Exception e) {
						logger.error("Failed to save LeadAddressDtls: " + e);
						throw new ReplicationFailedException("LeadAddressDtls",
								buildFailureDetail("LeadAddressDtls", e), e);
					}
					// ----------@CibilDetails-----------
					logger.debug("CibilDetails Data Sink on process");

					CibilDetails cblDetails = new CibilDetails();
					CibilDetailsPayload cblPayLoad = new CibilDetailsPayload();

					BigDecimal cbDtlId = CommonUtils.generateRandomNum();
					cblDetails.setCbDtlId(cbDtlId);
					cblDetails.setAppId(requestObj.getAppId());
					cblDetails.setVersionNum(1);
					cblDetails.setApplicationId(applicationId);
					cblDetails.setCbDate(LocalDate.now());
					cblDetails.setCustDtlId(AppCustDtlId);

					if ((unnCbResponseDto.getFinal_Decision() != null
							? unnCbResponseDto.getFinal_Decision().toLowerCase()
							: "").indexOf("approved".toLowerCase()) != -1) {
						cblDetails.setCbStatus("PASS"); // Pass case
					} else { // Reject case
						cblDetails.setCbStatus("FAIL");
						cblPayLoad.setRejectionReason(unnCbResponseDto.getRejection_reason());
					}
					cblPayLoad.setFlowResponse(unnCbResponseDto.getFlow_response());
					cblPayLoad.setVisheshEligibility(unnCbResponseDto.getVishesh_eligibility());
					cblPayLoad
							.setVisheshIndebtEligibleAmount(unnCbResponseDto.getVishesh_indebt_eligible_amount() + "");
					cblPayLoad.setIrisMessage(unnCbResponseDto.getIRIS_message() + "");
					cblPayLoad.setFinalDecision(unnCbResponseDto.getFinal_Decision());
					cblPayLoad.setCbLoanId("");
					cblPayLoad.setAppliedLoanCode(unnCbResponseDto.getApplied_loan_code());

//					cblPayLoad.setFoir(Optional.ofNullable(new BigDecimal(unnCbResponseDto.getFinal_FOIR_obligation()))
//							.orElse(BigDecimal.ZERO).toPlainString());
					BigDecimal foirValue;
					try {
						foirValue = (unnatiIexceedCDHLead.getFoir() != null) ? new BigDecimal(unnatiIexceedCDHLead.getFoir()) : BigDecimal.ZERO;
					} catch (NumberFormatException e) {
						logger.debug("foirValue Error"+ e.getMessage());
						foirValue = BigDecimal.ZERO;
					}
					cblPayLoad.setFoir(foirValue.toPlainString());


					cblPayLoad.setFoirPercentage(unnCbResponseDto.getFinal_FOIR() + "");
					cblPayLoad.setApprovedLoanEMI(unnCbResponseDto.getInstallment_amt() + "");
					cblPayLoad.setFinalTenure(unnCbResponseDto.getFinal_Tenure());

					cblPayLoad.setEligibleAmt(String.valueOf(unnCbResponseDto.getApproved_Loan_Amount()));
					if(appMaster.getProductCode().equalsIgnoreCase(ProductCode.UNNATI_RESTART.getUnnatiCode())) {
						cblPayLoad.setEligibleAmt(String.valueOf(unnatiIexceedCDHLead.getCrtApprovedAmount()));
					}
					cblPayLoad.setEligibleEMI(unnCbResponseDto.getEligible_EMI());
					cblPayLoad.setOverdueAmt(unnCbResponseDto.getApplicant_overdue_amount());
					cblPayLoad.setWriteOffAmt(unnCbResponseDto.getApplicant_Write_Off_Amount());
					cblPayLoad.setWriteoffSuitFiledFlag(unnCbResponseDto.getApplicant_Writeoff_Suit_filed_Flag());

					cblPayLoad.setIndividualIndebtness(unnCbResponseDto.getApplicant_Indebtedness());

					String score = extractValue(unnCbResponseDto.getScore());
					cblPayLoad.setCbScore(score);

					cblPayLoad.setTotIndebtness(unnCbResponseDto.getIndebtedness());

					cblPayLoad.setOtsFlag(unnCbResponseDto.getOTS_flag());
					cblPayLoad.setCaglDpdFlag(unnCbResponseDto.getCAGL_DPD_Flag());
					cblPayLoad.setCaglUnnatiFlag(unnCbResponseDto.getCAGL_Unnati_Flag());
					cblPayLoad.setEir(unnatiIexceedCDHLead.getEir());
					cblPayLoad.setRoi(unnCbResponseDto.getROI());

					cblPayLoad.setBureauName("BRE");

					cblPayLoad.setProcessingFeesWithoutGST(unnCbResponseDto.getProcessing_fees() + "");

					cblPayLoad.setGstOnProcessingFees(unnCbResponseDto.getGST_pf() + "");

					cblPayLoad.setInsuranceChargeMember(unnCbResponseDto.getInsurance_charge_Member() + "");
					cblPayLoad.setInsuranceChargeSpouse(unnCbResponseDto.getInsurance_charge_Spouse() + "");
					cblPayLoad.setInsuranceChargeJoint(unnCbResponseDto.getInsurance_charge_Joint() + "");
					cblPayLoad.setStampDutyCharge(unnCbResponseDto.getStamp_duty() + "");
					cblPayLoad.setRepaymentFrequency(unnCbResponseDto.getRepayment_frequency());

					cblPayLoad.setAppIndebtednessLimit(unnCbResponseDto.getApp_indebtedness_limit() + "");
					cblPayLoad.setAppMaxLoanLimit(unnCbResponseDto.getApp_max_loan_limit() + "");
					cblPayLoad.setCoappIndebtednessLimit(unnCbResponseDto.getCoapp_indebtedness_limit() + "");
					cblPayLoad.setCoappMaxLoanLimit(unnCbResponseDto.getCoapp_max_loan_limit() + "");

					cblPayLoad.setApplicantIndebtedness(unnCbResponseDto.getApplicant_Indebtedness());
					cblPayLoad.setCoApplicantIndebtedness(unnCbResponseDto.getCo_applicant_Indebtedness());

					cblPayLoad.setFinalTenure(unnCbResponseDto.getFinal_Tenure());

					String cblPayloadData = new Gson().toJson(cblPayLoad);
					cblDetails.setPayloadColumn(cblPayloadData);

					logger.debug("Data inserted cbil" + cblDetails.toString());

					try {
						CibilDetails saveAndFlush = cibilDtlRepo.saveAndFlush(cblDetails);
						logger.debug("Cbil Saved::{}", saveAndFlush);
					} catch (Exception e) {
						logger.error("Cbil Saved Error", e);
						throw new ReplicationFailedException("CibilDetails", buildFailureDetail("CibilDetails", e), e);
					}

					// ----------@CO-CibilDetails-----------

					logger.debug("coCblDetails Data Sink on process");

					if (coAppCustDtlId.signum() != 0) {

						CibilDetails coCblDetails = new CibilDetails();
						CibilDetailsPayload CoCblPayLoad = new CibilDetailsPayload();

						BigDecimal coCbDtlId = CommonUtils.generateRandomNum();
						coCblDetails.setCbDtlId(coCbDtlId);
						coCblDetails.setAppId(requestObj.getAppId());
						coCblDetails.setVersionNum(1);
						coCblDetails.setApplicationId(applicationId);
						coCblDetails.setCbDate(LocalDate.now());
						coCblDetails.setCustDtlId(coAppCustDtlId);

						if ((unnCbResponseDto.getFinal_Decision() != null
								? unnCbResponseDto.getFinal_Decision().toLowerCase()
								: "").indexOf("approved".toLowerCase()) != -1) {
							coCblDetails.setCbStatus("PASS"); // Pass case
						} else { // Reject case
							coCblDetails.setCbStatus("FAIL");
							CoCblPayLoad.setRejectionReason(unnCbResponseDto.getRejection_reason());
						}
						CoCblPayLoad.setFlowResponse(unnCbResponseDto.getFlow_response());
						CoCblPayLoad.setVisheshEligibility(unnCbResponseDto.getVishesh_eligibility());
						CoCblPayLoad.setVisheshIndebtEligibleAmount(
								unnCbResponseDto.getVishesh_indebt_eligible_amount() + "");
						CoCblPayLoad.setIrisMessage(unnCbResponseDto.getIRIS_message() + "");
						CoCblPayLoad.setFinalDecision(unnCbResponseDto.getFinal_Decision());
						CoCblPayLoad.setCbLoanId("");
						CoCblPayLoad.setAppliedLoanCode(unnCbResponseDto.getApplied_loan_code());

//						CoCblPayLoad.setFoir(
//								Optional.ofNullable(new BigDecimal(unnCbResponseDto.getFinal_FOIR_obligation()))
//										.orElse(BigDecimal.ZERO).toPlainString());
						CoCblPayLoad.setFoir(foirValue.toPlainString());

						CoCblPayLoad.setFoirPercentage(unnCbResponseDto.getFinal_FOIR() + "");
						CoCblPayLoad.setApprovedLoanEMI(unnCbResponseDto.getInstallment_amt() + "");
						CoCblPayLoad.setFinalTenure(unnCbResponseDto.getFinal_Tenure());

						CoCblPayLoad.setEligibleAmt(String.valueOf(unnCbResponseDto.getApproved_Loan_Amount()));
						if(appMaster.getProductCode().equalsIgnoreCase(ProductCode.UNNATI_RESTART.getUnnatiCode())) {
							CoCblPayLoad.setEligibleAmt(String.valueOf(unnatiIexceedCDHLead.getCrtApprovedAmount()));
						}
						CoCblPayLoad.setEligibleEMI(unnCbResponseDto.getEligible_EMI());
						CoCblPayLoad.setOverdueAmt(unnCbResponseDto.getCo_applicant_Overdue_Amount());
						CoCblPayLoad.setWriteOffAmt(unnCbResponseDto.getCo_applicant_Write_Off_Amount());
						CoCblPayLoad
								.setWriteoffSuitFiledFlag(unnCbResponseDto.getCo_applicant_Writeoff_Suit_filed_Flag());

						CoCblPayLoad.setIndividualIndebtness(unnCbResponseDto.getCo_applicant_Indebtedness());

						String coscore = extractValue(unnCbResponseDto.getScore());
						CoCblPayLoad.setCbScore(coscore);

						CoCblPayLoad.setTotIndebtness(unnCbResponseDto.getIndebtedness());

						CoCblPayLoad.setOtsFlag(unnCbResponseDto.getOTS_flag());
						CoCblPayLoad.setCaglDpdFlag(unnCbResponseDto.getCAGL_DPD_Flag());
						CoCblPayLoad.setCaglUnnatiFlag(unnCbResponseDto.getCAGL_Unnati_Flag());
						CoCblPayLoad.setEir(unnatiIexceedCDHLead.getEir());
						CoCblPayLoad.setRoi(unnCbResponseDto.getROI());

						CoCblPayLoad.setBureauName("BRE");

						CoCblPayLoad.setProcessingFeesWithoutGST(unnCbResponseDto.getProcessing_fees() + "");

						CoCblPayLoad.setGstOnProcessingFees(unnCbResponseDto.getGST_pf() + "");

						CoCblPayLoad.setInsuranceChargeMember(unnCbResponseDto.getInsurance_charge_Member() + "");
						CoCblPayLoad.setInsuranceChargeSpouse(unnCbResponseDto.getInsurance_charge_Spouse() + "");
						CoCblPayLoad.setInsuranceChargeJoint(unnCbResponseDto.getInsurance_charge_Joint() + "");
						CoCblPayLoad.setStampDutyCharge(unnCbResponseDto.getStamp_duty() + "");
						CoCblPayLoad.setRepaymentFrequency(unnCbResponseDto.getRepayment_frequency());

						CoCblPayLoad.setAppIndebtednessLimit(unnCbResponseDto.getCoapp_indebtedness_limit() + "");
						CoCblPayLoad.setAppMaxLoanLimit(unnCbResponseDto.getApp_max_loan_limit() + "");
						CoCblPayLoad.setCoappIndebtednessLimit(unnCbResponseDto.getCoapp_indebtedness_limit() + "");
						CoCblPayLoad.setCoappMaxLoanLimit(unnCbResponseDto.getCoapp_max_loan_limit() + "");

						CoCblPayLoad.setApplicantIndebtedness(unnCbResponseDto.getApplicant_Indebtedness());
						CoCblPayLoad.setCoApplicantIndebtedness(unnCbResponseDto.getCo_applicant_Indebtedness());

						CoCblPayLoad.setFinalTenure(unnCbResponseDto.getFinal_Tenure());

						String coCblPayloadData = new Gson().toJson(CoCblPayLoad);
						coCblDetails.setPayloadColumn(coCblPayloadData);

						logger.debug("Data inserted coCblDetails" + coCblDetails.toString());

						try {
							CibilDetails saveAndFlush = cibilDtlRepo.saveAndFlush(coCblDetails);
							logger.debug("coCblDetails Saved::{}", saveAndFlush);
						} catch (Exception e) {
							logger.error("coCblDetails Saved Error", e);
							throw new ReplicationFailedException("coCblDetails", buildFailureDetail("coCblDetails", e),
									e);
						}

					} else
						logger.debug("coCblDetails Not present");

					// ------END------

					// ----------@BankDetails-----------
					logger.debug("BankDetails Data Sink on process");

					BankDetails appBankDetails = new BankDetails();
					BankDetailsPayload appBankPayLoad = new BankDetailsPayload();

					BigDecimal bankDtlId = CommonUtils.generateRandomNum();
					appBankDetails.setBankDtlId(bankDtlId);
					appBankDetails.setAppId(requestObj.getAppId());
					appBankDetails.setVersionNum(1);
					appBankDetails.setApplicationId(applicationId);
					appBankDetails.setCustDtlId(AppCustDtlId);

					appBankPayLoad.setAccountType(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getTypeOfAccount()));
					appBankPayLoad
							.setAccountName(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getNameAsPerBankAccount()));
					appBankPayLoad.setIfsc(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getIfscCode()));
					appBankPayLoad.setBankName(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getBankName()));
					appBankPayLoad.setBranchName(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getBranchName()));
					appBankPayLoad.setAccountNumber(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getAccountNumber()));
					appBankPayLoad
							.setReEnterAccountNumber(getDefaultValueIfObjNull(unnatiIexceedCDHLead.getAccountNumber()));

					String bankPayloadData = new Gson().toJson(appBankPayLoad);
					appBankDetails.setPayloadColumn(bankPayloadData);
					logger.debug("Data inserted bank" + appBankDetails.toString());

					try {
						BankDetails saveAndFlush = bankDtlRepo.saveAndFlush(appBankDetails);
						logger.debug("BankDetails Saved::{}", saveAndFlush);
					} catch (Exception e) {
						logger.error("Bank Details Saved Error", e);
						throw new ReplicationFailedException("BankDetails", buildFailureDetail("BankDetails", e), e);
					}

					// --------END-------
				} else {
					logger.warn("unnatiIexceedCDHLeads Lead not found");
					responseObj.put("message", "CDH Lead not found");
					responseObj.put("Status", FAIL_MSG);
					return responseObj;
				}
			} else {
				logger.warn("Requested Product Code not @Additional_Product" + requestObj.getProduct());
				responseObj.put("message",
						"Invalid product code. Expected @Additional_Product:" + requestObj.getProduct());
				responseObj.put("Status", FAIL_MSG);
				return responseObj;
			}
			/**
			 * @Updating CDH Status
			 */
			if (unnatiIexceedCDHLead != null) {
				// ----END----
				unnatiIexceedCDHLead.setUnnatiStatus(AppStatus.getCdhStatusByValue("CACOMPLETED"));
				/* Updating unique @Unnati ApplicationId */
				unnatiIexceedCDHLead.setUnnatiApplicationId(applicationId);
				unnatiIexceedCDHLeadRepo.save(unnatiIexceedCDHLead);
			}
			logger.warn("CDH to Unnati Data Sink succeeded." + requestObj.getProduct());
			responseObj.put("message", "CDH to Unnati Data Sink succeeded.");
			responseObj.put("Status", SUCCESS_MSG);
			responseObj.put("ApplicationId", applicationId);
			return responseObj;
		} catch (Exception e) {

			logger.error("Unexpected G-Exception at :populateCdhToUnnati ", e);
			throw new ReplicationFailedException("ReplicationFailed", e.getMessage());
		}
	}

	public String formatDate(LocalDate date, String pattern) {
		if (date == null) {
			return getDefaultValueIfObjNull(null);
		}
		return DateTimeFormatter.ofPattern(pattern).format(date);
	}

	/*
	 * @Exception Handler
	 */
	private String buildFailureDetail(String tableName, Exception ex) {
		String rootMsg = getRootCauseMessage(ex);
		String errorType = classifyDbError(ex, rootMsg);

		return String.format("Transaction rolled back. Failed table: [%s] | Error type: [%s] | Detail: %s", tableName,
				errorType, rootMsg);
	}

	private String classifyDbError(Exception ex, String msg) {
		String lower = msg.toLowerCase();
		if (ex instanceof org.springframework.dao.DataIntegrityViolationException) {
			if (lower.contains("too long") || lower.contains("data truncation"))
				return "DATA_TOO_LONG";
			if (lower.contains("duplicate") || lower.contains("unique"))
				return "DUPLICATE_VALUE";
			if (lower.contains("cannot be null") || lower.contains("not null"))
				return "NULL_NOT_ALLOWED";
			return "DATA_INTEGRITY_ERROR";
		}
		if (lower.contains("type") || lower.contains("cast") || lower.contains("number format"))
			return "TYPE_MISMATCH";
		if (lower.contains("date") || lower.contains("timestamp") || lower.contains("format"))
			return "DATE_FORMAT";
		return "DB_ERROR";
	}

	private String getRootCauseMessage(Throwable ex) {
		Throwable cause = ex;
		while (cause.getCause() != null)
			cause = cause.getCause();
		String msg = cause.getMessage();
		return (msg != null && msg.length() > 250) ? msg.substring(0, 250) : (msg != null ? msg : ex.getMessage());
	}

	/**
	 * @Fetch Application Master Data
	 */
	public ResponseEntity<ResponseWrapper> getApplicationMaster(CallReplicateRequest req, String applicationId) {

		logger.debug("service getApplicationMaster req" + req);
		logger.debug("service getApplicationMaster applicationId" + applicationId);
		ResponseWrapper fetchUserDetailsResponseWrapper = new ResponseWrapper();
		Response populateCdhToUnnatiResponse = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();

		CallReplicateFields reqFieldsq = req.getRequestObj();
		Optional<ApplicationMaster> appMasterByApplicationId = applicationMasterRepo.findByApplicationId(applicationId);

		try {

			if (appMasterByApplicationId.isPresent()) {

				logger.debug("appMasterByApplicationId present");

				ApplicationMaster applicationMaster = appMasterByApplicationId.get();
				FetchDeleteUserFields fetchUserFields = new FetchDeleteUserFields();

				fetchUserFields.setAppId(applicationMaster.getAppId());
				fetchUserFields.setApplicationId(applicationId);
				fetchUserFields.setVersionNum(applicationMaster.getVersionNum());
				fetchUserFields.setStatus(applicationMaster.getApplicationStatus() + "");
				fetchUserFields.setUserId(req.getRequestObj().getUserId());
				fetchUserFields.setCustDtlId(applicationMaster.getCustDtlId() != null ? applicationMaster.getCustDtlId()
						: new BigDecimal(0));
				fetchUserFields.setRemarks(applicationMaster.getRemarks() + "");
				fetchUserFields.setWorkFlow(new WorkFlowDetails());
				fetchUserFields.setLUCRequest(new LucUploadLoanRequest());
				fetchUserFields.setIsPreview("N");
				fetchUserFields.setCdhFlag("N");
				fetchUserFields.setCustomerId(applicationMaster.getCustomerId() + "");
				fetchUserFields.setProductId(applicationMaster.getProductCode());

				FetchDeleteUserRequest fetchDeleteUserRequest = FetchDeleteUserRequest.builder()
						.appId(applicationMaster.getAppId()).interfaceName(req.getInterfaceName())
						.requestObj(fetchUserFields).build();

				logger.debug("fetchapplication-> fetchDeleteUserRequest:" + fetchDeleteUserRequest);

				ResponseEntity<ResponseWrapper> serviceFetchApplication = cobService
						.serviceFetchApplication(fetchDeleteUserRequest);

				logger.debug("serviceFetchApplication Response" + serviceFetchApplication);
				return serviceFetchApplication;
			} else {
				logger.debug("Deaplink completed (Application Master not Found)");
				respHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				respHeader.setHttpStatus(HttpStatus.OK);
				responseBody.setResponseObj("Deaplink completed (Application Master not Found)");
				populateCdhToUnnatiResponse.setResponseHeader(respHeader);
				populateCdhToUnnatiResponse.setResponseBody(responseBody);
				fetchUserDetailsResponseWrapper.setApiResponse(populateCdhToUnnatiResponse);
				logger.debug("Deaplink completed (Application Master not Found Application:)"
						+ reqFieldsq.getApplicationId());
				return new ResponseEntity<>(fetchUserDetailsResponseWrapper, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.debug("ApplicationId={}", applicationId);
			logger.debug("getApplicationMaster Exception" + e.getMessage());
			logger.debug("getApplicationMaster Exception Error", e);
			throw new ReplicationFailedException(
					"callReplicateApplicationDetails Data sink Completed Error in FetchApplication call");
		}
	}

}
