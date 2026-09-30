package com.iexceed.appzillonbanking.cob.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.core.repository.ab.*;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.core.services.InterfaceAdapter;
import com.iexceed.appzillonbanking.cob.core.services.ResponseParser;
import com.iexceed.appzillonbanking.cob.core.utils.*;
import com.iexceed.appzillonbanking.cob.domain.ab.*;
import com.iexceed.appzillonbanking.cob.domain.apz.User;
import com.iexceed.appzillonbanking.cob.domain.apz.UserId;
import com.iexceed.appzillonbanking.cob.domain.cdh.GkKendraData;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiCoApplicantDetails;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedCDHLead;
import com.iexceed.appzillonbanking.cob.loans.domain.user.OfficeData;
import com.iexceed.appzillonbanking.cob.loans.payload.*;
import com.iexceed.appzillonbanking.cob.loans.payload.UploadLoanRequestFields.DBKITResponse;
import com.iexceed.appzillonbanking.cob.loans.repository.user.*;
import com.iexceed.appzillonbanking.cob.nesl.domain.ab.Enach;
import com.iexceed.appzillonbanking.cob.nesl.repository.ab.EnachRepository;
import com.iexceed.appzillonbanking.cob.payload.*;
import com.iexceed.appzillonbanking.cob.payload.CheckApplicationRequest;
import com.iexceed.appzillonbanking.cob.report.*;
import com.iexceed.appzillonbanking.cob.repository.ab.*;
import com.iexceed.appzillonbanking.cob.repository.apz.UserRepository;
import com.iexceed.appzillonbanking.cob.repository.cdh.KendraDataRepository;
import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiCoApplicantDetailsRepository;
import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiIexceedCDHLeadRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.tika.Tika;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class COBService {

    private static final Logger logger = LogManager.getLogger(COBService.class);

    @Autowired
    private AdapterUtil adapterUtil;

    @Autowired
    private ApplicationMasterRepository applicationMasterRepository;

    @Autowired
    private CustomerDetailsRepository customerDetailsRepository;

    @Autowired
    private AddressDetailsRepository addressDetailsRepository;

    @Autowired
    private OccupationDetailsRepository occupationDetailsRepository;

    @Autowired
    private NomineeDetailsRepository nomineeDetailsRepository;

    @Autowired
    private ApplicationDocumentsRepository applicationDocumentsRepository;

    @Autowired
    private CountriesRepository countriesRepository;

    @Autowired
    private StatesRepository statesRepository;

    @Autowired
    private CitiesRepository citiesRepository;

    @Autowired
    private LovMasterRepository lovMasterRepository;

    @Autowired
    private BankingFacilitiesRepository bankingFacilitiesRepository;

    @Autowired
    private ApplicationMasterHisRepository applicationMasterHisRepository;

    @Autowired
    private OccupationDetailsHisRepository occupationDetailsHisRepository;

    @Autowired
    private NomineeDetailsHisRepository nomineeDetailsHisRepository;

    @Autowired
    private CustomerDetailsHisRepository customerDetailsHisRepository;

    @Autowired
    private BankingFacilitiesHisRepository bankingFacilitiesHisRepository;

    @Autowired
    private ApplicationDocumentsHisRepository applicationDocumentsHisRepository;

    @Autowired
    private AddressDetailsHisRepository addressDetailsHisRepository;

    @Autowired
    private InterfaceAdapter interfaceAdapter;

    @Autowired
    private BranchesRepository branchesRepository;

    @Autowired
    private RoleAccessMapRepository roleAccessMapRepository;

    @Autowired
    private ApplicationWorkflowRepository applnWfRepository;

    @Autowired
    private WorkflowDefinitionRepository wfDefnRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FatcaDetailsRepository fatcaDtlsrepository;

    @Autowired
    private CRSDetailsRepository crsDtlsrepository;

    @Autowired
    private CRSDetailsHisRepository crsDtlsHisrepository;

    @Autowired
    private FatcaDetailsHisRepository fatcaDtlsHisrepository;

    @Autowired
    private CommonParamService commonService;

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @Autowired
    private InsuranceDetailsRepository insuranceRepository;

    @Autowired
    private CibilDetailsRepository cibilDetailsRepository;

    @Autowired
    private ExistingLoanDetailsRepository existingLoanRepository;

    @Autowired
    private SourcingStageVerificationRepository srcStageVerificationRepo;

    @Autowired
    private AMLQuestionnaireRepository amlQuestionnaireRepo;

    @Autowired
    private Report report;

    @Autowired
    private LoanDtlsRepo loanDtlsRepo;

    @Autowired
    private DepositDtlsRepo depositDtlsRepo;

    @Autowired
    private KendraDetailsRepository kendraDetailsRepository;

    @Autowired
    private KendraDataRepository gkKendraDataRepo;

    @Autowired
    private LeadDashboardRepository leadDashboardRepository;

    @Autowired
    private StateDetailsRepository stateDetailsRepository;

    @Autowired
    private TATBranchDetailsRepository tATBranchDetailsRepository;

    @Autowired
    private OfficeDataRepo officeDataRepo;

    @Autowired
    private RpcStageVerificationRepository rpcStageVerificationRepository;

    @Autowired
    private DeviationRATrackerRepository deviationRATrackerRepository;

    @Autowired
    private SanctionMasterRepository sanctionMasterRepositoy;

    @Autowired
    private ProductDetailsrepository productDetailsrepository;

    @Autowired
    private SourcingResponseTrackerRepository sourcingResponseTrackerRepo;

    @Autowired
    private UnnatiIexceedCDHLeadRepo unnatiIexceedCDHLeadRepo;

    @Autowired
    private ErrorTraceRepository errorTraceRepository;

    @Autowired
    private UnnatiCoApplicantDetailsRepository coApplicantDetailsRepository;

    private final BCMPIStageVerificationRepository bcmpiStageVerificationRepository;

    private final BCMPIIncomeDetailsRepository bcmpiIncomeDetailsRepo;
    private final BCMPILoanObligationsRepository bcmpiLoanObligationsRepo;
    private final BCMPIOtherDetailsRepository bcmpiOtherDetailsRepo;
    private final ApplicationMasterRepository2 applicationMasterRepository2;
    private final DocumentsRepository documentsRepository;
    private final ApiExecutionLogRepository apiExecutionLogRepository;
    private final UdhyamRepository udhyamRepository;
    private final DBKITStageVerificationRepository dbkitStageVerificationRepository;
    private final CibilDetailsHisRepository cibilDetailsHisRepository;
    private final TbUserRepository tbUserRepository;
    private final LucRepository lucRepository;
    private final WhitelistedBranchesRepository whitelistedBranchesRepository;
    private final ExistingGLLoanDetailsRepository existingGLLoanDetailsRepository;

    public COBService(BCMPIStageVerificationRepository bcmpiStageVerificationRepository,
                      BCMPIIncomeDetailsRepository bcmpiIncomeDetailsRepo,
                      BCMPILoanObligationsRepository bcmpiLoanObligationsRepo,
                      BCMPIOtherDetailsRepository bcmpiOtherDetailsRepo,
                      ApplicationMasterRepository2 applicationMasterRepository2,
                      DocumentsRepository documentsRepository,
                      ApiExecutionLogRepository apiExecutionLogRepository,
                      UdhyamRepository udhyamRepository,
                      DBKITStageVerificationRepository dbkitStageVerificationRepository,
                      CibilDetailsHisRepository cibilDetailsHisRepository,
                      TbUserRepository tbUserRepository, LucRepository lucRepository,
                      WhitelistedBranchesRepository whitelistedBranchesRepository,
                      ExistingGLLoanDetailsRepository existingGLLoanDetailsRepository) {
        this.bcmpiStageVerificationRepository = bcmpiStageVerificationRepository;
        this.bcmpiIncomeDetailsRepo = bcmpiIncomeDetailsRepo;
        this.bcmpiLoanObligationsRepo = bcmpiLoanObligationsRepo;
        this.bcmpiOtherDetailsRepo = bcmpiOtherDetailsRepo;
        this.applicationMasterRepository2 = applicationMasterRepository2;
        this.documentsRepository = documentsRepository;
        this.apiExecutionLogRepository = apiExecutionLogRepository;
        this.udhyamRepository = udhyamRepository;
        this.dbkitStageVerificationRepository = dbkitStageVerificationRepository;
        this.cibilDetailsHisRepository = cibilDetailsHisRepository;
        this.tbUserRepository = tbUserRepository;
        this.lucRepository = lucRepository;
        this.whitelistedBranchesRepository = whitelistedBranchesRepository;
        this.existingGLLoanDetailsRepository = existingGLLoanDetailsRepository;
    }

    private String nomineeDelMsg = "Nominee deleted.";

    @Autowired
    private EnachRepository enachRepo;

    @CircuitBreaker(name = "fallback", fallbackMethod = "createApplicationFallback")
    public Mono<Response> createApplication(CreateModifyUserRequest createUserRequest, boolean isSelfOnBoardingAppId,
                                            Properties prop, boolean isSelfOnBoardingHeaderAppId, Header header, JSONArray array) {
        ResponseBody responseBody = new ResponseBody();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        CustomerDataFields requestObj = createUserRequest.getRequestObj();
        ApplicationMaster applicationMaster = requestObj.getApplicationMaster();
        CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
        String applicationID;
        int version = 0;
        boolean isThisLastStage = commonService.isThisLastStage(applicationMaster.getCurrentScreenId().split("~")[0],
                array);
        boolean isAccountCreationisNextStage = false;
        ApplicationMaster appMaster = null;
        BigDecimal custDtlId = commonService.getCustDtlId(applicationMaster);
        if (CommonUtils.isNullOrEmpty(requestObj.getApplicationId())) {
            applicationID = CommonUtils.generateRandomNumStr();
            version = Constants.INITIAL_VERSION_NO; // initial creation of application version number should be 1.
            populateAppMasterAndApplnwf(requestObj, customerIdentification, applicationID, custDtlId, version,
                    isSelfOnBoardingHeaderAppId, prop);// method to insert into TB_ABOB_APPLICATION_MASTER.
            commonService.populateCustomerDtlsIfNotPresent(requestObj.getApplicationMaster(), applicationID, custDtlId,
                    version, requestObj.getAppId());
        } else { // this ID should be created once only.
            applicationID = requestObj.getApplicationId();
            Optional<ApplicationMaster> appMasterForVersionCheck = applicationMasterRepository
                    .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(requestObj.getAppId(), applicationID);
            if (appMasterForVersionCheck.isPresent()) {
                appMaster = appMasterForVersionCheck.get();
                if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMaster.getApplicationStatus())
                        || AppStatus.APPROVED.getValue().equalsIgnoreCase(appMaster.getApplicationStatus())) {
                    // Taking version number always from db as part of VAPT too.
                    version = appMaster.getVersionNum(); // if application is in inprogress status,subsequent tables
                    // should have same version number.
                    commonService.populateCustomerDtlsIfNotPresent(requestObj.getApplicationMaster(), applicationID,
                            custDtlId, version, requestObj.getAppId());
                }
                if (Objects.equals(appMaster.getApplicantsCount(), applicationMaster.getCustDtlSlNum())) {
                    isAccountCreationisNextStage = commonService
                            .isAccountCreationisNextStage(applicationMaster.getCurrentScreenId().split("~")[0], array);
                }
            }
        }
        String[] currentScreenIdArray = applicationMaster.getCurrentScreenId().split("~");
        commonService.updateCurrentStageInMaster(requestObj.getApplicationMaster(), currentScreenIdArray, version,
                requestObj.getAppId(), requestObj.getApplicationId());
        switch (currentScreenIdArray[0]) {
            case Constants.CUST_VERIFICATION:
                updateCustomerDtlInMaster(requestObj, version, custDtlId, applicationID, customerIdentification);
                updateBranchCodeInMaster(requestObj, version, applicationID); // PIN CODE screen changes
                populateBankingfacilities(requestObj, customerIdentification, applicationID, custDtlId, version); // PIN
                // CODE
                // screen
                // changes
                break;
            case Constants.KYC_VERIFICATION:
                populateApplicationDocs(requestObj, customerIdentification, applicationID, custDtlId, version);
                commonService.updateNationIdInMaster(requestObj.getApplicationMaster(), version, requestObj.getAppId(),
                        requestObj.getApplicationId());
                break;
            case Constants.CUSTOMER_DETAILS:
                populateCustomerDtls(requestObj, customerIdentification, applicationID, custDtlId, version);
                populateAddressDtls(requestObj, customerIdentification, applicationID, custDtlId, version,
                        Constants.CUSTOMER_DETAILS);
                commonService.updatePanInMaster(requestObj.getApplicationMaster(), version, requestObj.getAppId(),
                        requestObj.getApplicationId());
                break;
            case Constants.OCCUPATION_DETAILS:
                populateOccupationdtls(requestObj, customerIdentification, applicationID, custDtlId, version);
                populateAddressDtls(requestObj, customerIdentification, applicationID, custDtlId, version,
                        Constants.OCCUPATION_DETAILS);
                break;
            case Constants.NOMINEE_DETAILS:
                populateNomineeDtls(requestObj, customerIdentification, applicationID, custDtlId, version);
                populateAddressDtls(requestObj, customerIdentification, applicationID, custDtlId, version,
                        Constants.NOMINEE_DETAILS);
                break;
            case Constants.UPLOAD_DOCUMENTS:
                populateApplicationDocs(requestObj, customerIdentification, applicationID, custDtlId, version);
                break;
            case Constants.SERVICES:
                populateBankingfacilities(requestObj, customerIdentification, applicationID, custDtlId, version);
                updateBranchCodeInMaster(requestObj, version, applicationID);
                break;
            case Constants.KYC_MODE:
                updateKycFlagInMaster(requestObj, customerIdentification, applicationID, custDtlId, version);
                break;
            case Constants.TERMS_AND_CONDITIONS:
                updateDeclarationFlagInMaster(requestObj, version, applicationID, custDtlId, customerIdentification);
                break;
            case Constants.CONFIRMATION:
                // No action to do specifically for CONFIRMATION. Appropriate actions are taken
                // based on return value of isAccountCreationisNextStage() and
                // isThisLastStage().
                customerIdentification.setCustDtlId(custDtlId.toString()); // to String is required to avoid rounding issue
                // of Big Decimal at front end.
                customerIdentification.setApplicationId(applicationID);
                customerIdentification.setVersionNum(version);
                break;
            case Constants.FATCA:
                populateFatcaDetails(requestObj, customerIdentification, applicationID, custDtlId, version);
                break;
            case Constants.CRS:
                populateCRSDetails(requestObj, customerIdentification, applicationID, custDtlId, version);
                break;
            case Constants.FUND_ACCOUNT:
                FundAccountRequest faReq = formFundAccRequest(requestObj);
                Mono<Object> extResponse = interfaceAdapter.callExternalService(header, faReq,
                        createUserRequest.getInterfaceName());
                final int versionFinal = version;
                final ApplicationMaster appMasterFinal = appMaster;
                final boolean isAccountCreationisNextStageFinal = isAccountCreationisNextStage;
                return extResponse.flatMap(val -> {
                    ResponseWrapper res = adapterUtil.getResponseMapper(val, createUserRequest.getInterfaceName(), header);
                    if (ResponseParser.isExtCallSuccess(res.getApiResponse(), "fundAccount")) {
                        String fundAccRefNum = ResponseParser.getFundAccRefNum(res.getApiResponse());
                        customerIdentification.setFundAccRefNum(fundAccRefNum);
                        customerIdentification.setCustDtlId(custDtlId.toString());
                        customerIdentification.setApplicationId(applicationID);
                        customerIdentification.setVersionNum(versionFinal);
                    }
                    Gson gson = new Gson();
                    String responseStr = gson.toJson(customerIdentification);
                    responseBody.setResponseObj(responseStr);
                    response.setResponseBody(responseBody);
                    responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                    response.setResponseHeader(responseHeader);
                    if (isThisLastStage) {
                        lastStageOperations(createUserRequest, customerIdentification, applicationID, custDtlId,
                                isSelfOnBoardingAppId, versionFinal, prop, isSelfOnBoardingHeaderAppId, requestObj,
                                appMasterFinal, applicationMaster);
                    }
                    if (isAccountCreationisNextStageFinal) {
                        return accountCreationStageOperations(applicationMaster, versionFinal, requestObj,
                                isSelfOnBoardingAppId, prop, createUserRequest, customerIdentification, header,
                                isSelfOnBoardingHeaderAppId, applicationID, array);
                    }
                    return Mono.just(response);
                });
            default:
                break;
        }

        if (isThisLastStage) {
            lastStageOperations(createUserRequest, customerIdentification, applicationID, custDtlId,
                    isSelfOnBoardingAppId, version, prop, isSelfOnBoardingHeaderAppId, requestObj, appMaster,
                    applicationMaster);
        }
        if (isAccountCreationisNextStage) {
            return accountCreationStageOperations(applicationMaster, version, requestObj, isSelfOnBoardingAppId, prop,
                    createUserRequest, customerIdentification, header, isSelfOnBoardingHeaderAppId, applicationID,
                    array);
        }
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        Gson gson = new Gson();
        String responseStr = gson.toJson(customerIdentification);
        responseBody.setResponseObj(responseStr);
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return Mono.just(response);
    }

    private Mono<Response> accountCreationStageOperations(ApplicationMaster applicationMaster, int version,
                                                          CustomerDataFields requestObj, boolean isSelfOnBoardingAppId, Properties prop,
                                                          CreateModifyUserRequest createUserRequest, CustomerIdentificationCasa customerIdentification, Header header,
                                                          boolean isSelfOnBoardingHeaderAppId, String applicationID, JSONArray array) {
        String[] strAr1 = new String[]{Constants.ACCOUNT_CREATION, "Y"};
        if (null != requestObj.getApplicationMaster().getApplicantsCount()
                && 0 != requestObj.getApplicationMaster().getCustDtlSlNum() && requestObj.getApplicationMaster()
                .getApplicantsCount().equals(requestObj.getApplicationMaster().getCustDtlSlNum())) {
            commonService.updateCurrentStageInMaster(applicationMaster, strAr1, version, requestObj.getAppId(),
                    requestObj.getApplicationId());
        }
        return createAccountInCbs(isSelfOnBoardingAppId, prop, createUserRequest, version, customerIdentification,
                header, isSelfOnBoardingHeaderAppId, applicationID, array);
    }

    private void lastStageOperations(CreateModifyUserRequest createUserRequest,
                                     CustomerIdentificationCasa customerIdentification, String applicationID, BigDecimal custDtlId,
                                     boolean isSelfOnBoardingAppId, int version, Properties prop, boolean isSelfOnBoardingHeaderAppId,
                                     CustomerDataFields requestObj, ApplicationMaster appMaster, ApplicationMaster applicationMaster) {
        updateConfirmFlagInMaster(createUserRequest, customerIdentification, applicationID, custDtlId,
                isSelfOnBoardingAppId, version, prop, isSelfOnBoardingHeaderAppId);
        if (appMaster != null && !CommonUtils.isNullOrEmpty(appMaster.getRelatedApplicationId())) {
            commonService.updateDtlsForRelatedAppln(createUserRequest.getAppId(), appMaster.getRelatedApplicationId(),
                    requestObj.getAppId(), requestObj.getApplicationId(), version, prop,
                    applicationMaster.getMainProductGroupCode());
        }
    }

    private Mono<Response> createAccountInCbs(boolean isSelfOnBoardingAppId, Properties prop,
                                              CreateModifyUserRequest request, int version, CustomerIdentificationCasa customerIdentification,
                                              Header header, boolean isSelfOnBoardingHeaderAppId, String applicationID, JSONArray array) {
        CustomerDataFields customerDataFields = request.getRequestObj();
        ApplicationMaster masterRequest = customerDataFields.getApplicationMaster();
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(customerDataFields.getAppId(),
                        customerDataFields.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
            if (Objects.equals(masterObj.getApplicantsCount(), masterRequest.getCustDtlSlNum())) {
                if (isSelfOnBoardingAppId) {
                    if ("N".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.ACCOUNT_STP.getKey()))) {
                        if (!isSelfOnBoardingHeaderAppId) { // INITIATOR submits it after review.
                            masterObj.setApplicationStatus(AppStatus.PENDING.getValue());
                            applicationMasterRepository.save(masterObj);
                        }
                    } else if ("Y".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.ACCOUNT_STP.getKey()))) {
                        // STEP 1: Create acc num and cust id and store in internal tables.
                        String accNum;
                        BigDecimal customerId;
                        if (CommonUtils.isNullOrEmpty(masterRequest.getAccNumber())) {// This is to handle the case if
                            // user changed the data after
                            // its being inserted by using
                            // the back navigation within
                            // the session.
                            accNum = CommonUtils.generateRandomNumStr();
                        } else {
                            accNum = masterRequest.getAccNumber();
                        }
                        if (masterRequest.getCustomerId() == null) {// This is to handle the case if user changed the
                            // data after its being inserted by using the back
                            // navigation within the session.
                            customerId = CommonUtils.generateRandomNum();
                        } else {
                            customerId = masterRequest.getCustomerId();
                        }
                        masterObj.setAccNumber(accNum);
                        masterObj.setCustomerId(customerId);
                        masterObj.setApplicationStatus(AppStatus.APPROVED.getValue());
                        applicationMasterRepository.save(masterObj);
                        // update customer ID present in TB_ABOB_CUSTOMER_DETAILS table.
                        Optional<CustomerDetails> custDtl = customerDetailsRepository
                                .findById(masterRequest.getCustDtlId());
                        if (custDtl.isPresent()) {
                            CustomerDetails custDtlObj = custDtl.get();
                            custDtlObj.setCustomerId(customerId);
                            customerDetailsRepository.save(custDtlObj);
                        }
                        customerIdentification.setCustomerId(customerId.toString());// to String is required to avoid
                        // rounding issue of Big Decimal at
                        // front end.
                        customerIdentification.setCasaAccNumber(accNum);
                        // STEP 2: Hook to call external service for account creation
                        CreateModifyUserRequest extReq = formExtReq(customerDataFields.getAppId(),
                                customerDataFields.getApplicationId(), version, accNum, customerId,
                                customerDataFields.getFundAccount());
                        String interfaceNameCasa = prop.getProperty(CobFlagsProperties.ACC_CREATION_INTF.getKey());
                        Mono<Object> extRes = interfaceAdapter.callExternalService(header, extReq, interfaceNameCasa);
                        return extRes.flatMap(val -> {
                            ResponseWrapper res = adapterUtil.getResponseMapper(val, interfaceNameCasa, header);
                            if (null != masterObj.getRelatedApplicationId()) {
                                Optional<ApplicationMaster> appMasterObjRelated = applicationMasterRepository
                                        .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(
                                                customerDataFields.getAppId(), masterObj.getRelatedApplicationId());
                                if (appMasterObjRelated.isPresent()) {
                                    ApplicationMaster appMasterObjRelatedDb = appMasterObjRelated.get();
                                    if (Products.LOAN.getKey()
                                            .equalsIgnoreCase(appMasterObjRelatedDb.getProductGroupCode())) {
                                        if ("N".equalsIgnoreCase(
                                                prop.getProperty(CobFlagsProperties.LOAN_STP.getKey()))) {
                                            appMasterObjRelatedDb.setApplicationStatus(AppStatus.PENDING.getValue());
                                            appMasterObjRelatedDb
                                                    .setCurrentScreenId(array.getString(array.length() - 1));
                                            applicationMasterRepository.save(appMasterObjRelatedDb);
                                        } else if ("Y".equalsIgnoreCase(
                                                prop.getProperty(CobFlagsProperties.LOAN_STP.getKey()))) {
                                            // STEP 1: Create acc num and cust id and store in internal tables.
                                            String accNumLoan = CommonUtils.generateRandomNumStr();
                                            appMasterObjRelatedDb.setCustomerId(masterObj.getCustomerId());
                                            appMasterObjRelatedDb.setAccNumber(accNumLoan);
                                            appMasterObjRelatedDb.setApplicationStatus(AppStatus.APPROVED.getValue());
                                            appMasterObjRelatedDb
                                                    .setCurrentScreenId(array.getString(array.length() - 1));
                                            applicationMasterRepository.save(appMasterObjRelatedDb);
                                            customerIdentification.setLoanAccNumber(accNumLoan);

                                            // STEP 2: Hook to call external service for loan account creation
                                            String interfaceNameLoan = prop
                                                    .getProperty(CobFlagsProperties.LOAN_ACC_CREATION_INTF.getKey());
                                            Mono<Object> extResDep = interfaceAdapter.callExternalService(header,
                                                    extReq, interfaceNameLoan);
                                            extResDep.flatMap(valLoan -> {
                                                return null;
                                            });
                                        }
                                    }
                                }
                            }
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
                    applicationMasterRepository.save(masterObj);
                }
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

    private FundAccountRequest formFundAccRequest(CustomerDataFields requestObj) {
        FundAccountRequest faReq = new FundAccountRequest();
        FundAccountRequestFields faReqFields = new FundAccountRequestFields();
        FundAccountRequestFields faReqObj = requestObj.getFundAccount();
        BeanUtils.copyProperties(faReqObj, faReqFields);
        faReq.setRequestObj(faReqFields);
        return faReq;
    }

    private void updateDeclarationFlagInMaster(CustomerDataFields customerDataFields, int version, String applicationID,
                                               BigDecimal custDtlId, CustomerIdentificationCasa customerIdentification) {
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(customerDataFields.getAppId(),
                        customerDataFields.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
//            masterObj.setDeclarationFlag(masterRequest.getDeclarationFlag());
            applicationMasterRepository.save(masterObj);
            customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue
            // of Big Decimal at front end.
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
        }
    }

    private void updateCustomerDtlInMaster(CustomerDataFields customerDataFields, int version, BigDecimal custDtlId,
                                           String applicationID, CustomerIdentificationCasa customerIdentification) {
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(customerDataFields.getAppId(),
                        customerDataFields.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterRequest = customerDataFields.getApplicationMaster();
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
            if (!(CommonUtils.isNullOrEmpty(masterRequest.getNationalId()))) {
                masterObj.setNationalId(masterRequest.getNationalId());
            }
            if (!(CommonUtils.isNullOrEmpty(masterRequest.getPan()))) {
                masterObj.setPan(masterRequest.getPan());
            }
            applicationMasterRepository.save(masterObj);
            customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue
            // of Big Decimal at front end.
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
        }
    }

    private void updateBranchCodeInMaster(CustomerDataFields customerDataFields, int version, String applicationID) {
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(customerDataFields.getAppId(),
                        applicationID, version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
            List<BankingFacilities> bankFacilityList = customerDataFields.getBankingFacilityList();
            if (null != bankFacilityList) {
                for (BankingFacilities bankfacility : bankFacilityList) {
                    masterObj.setSearchCode1(bankfacility.getPayload().getBranchCode());
                    applicationMasterRepository.save(masterObj);
                }
            }
        }
    }

    private void updateKycFlagInMaster(CustomerDataFields customerDataFields,
                                       CustomerIdentificationCasa customerIdentification, String applicationID, BigDecimal custDtlId,
                                       int version) {
        ApplicationMaster masterRequest = customerDataFields.getApplicationMaster();
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(customerDataFields.getAppId(),
                        customerDataFields.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
            masterObj.setKycType(masterRequest.getKycType());
            applicationMasterRepository.save(masterObj);
            customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue
            // of Big Decimal at front end.
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
        }
    }

    private void populateCRSDetails(CustomerDataFields customerDataFields,
                                    CustomerIdentificationCasa customerIdentification, String applicationID, BigDecimal custDtlId,
                                    int version) {
        List<String> crsDetailsIdList = new ArrayList<>();
        Gson gson = new Gson();
        String payload = "";
        List<CRSDetails> crsDetailsList = customerDataFields.getCrsDetailsList();
        for (CRSDetails crsObj : crsDetailsList) {
            if (crsObj.getCrsDtlId() == null) { // This is to handle the case if user changed the data after its being
                // inserted by using the back navigation within the session.
                BigDecimal crsDtlsId = CommonUtils.generateRandomNum();
                crsObj.setCrsDtlId(crsDtlsId);
                crsDetailsIdList.add(crsDtlsId.toString());
            }
            crsObj.setApplicationId(applicationID);
            crsObj.setAppId(customerDataFields.getAppId());
            crsObj.setCustDtlId(custDtlId);
            crsObj.setVersionNum(version);
            List<TaxDetails> taxList = crsObj.getPayload().getTaxDetailsList();
            for (TaxDetails taxDtl : taxList) {
                taxDtl.setTaxDtlId(CommonUtils.generateRandomNumStr());
            }
            crsObj.getPayload().setTaxDetailsList(taxList);
            payload = gson.toJson(crsObj.getPayload());
            crsObj.setPayloadColumn(payload);
            crsDtlsrepository.save(crsObj);
        }
        customerIdentification.setCrsDetailsList(crsDetailsIdList);
        customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue of
        // Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_CRS_DETAILS");

    }

    private void populateFatcaDetails(CustomerDataFields customerDataFields,
                                      CustomerIdentificationCasa customerIdentification, String applicationID, BigDecimal custDtlId,
                                      int version) {
        List<String> fatcaDetailsIdList = new ArrayList<>();
        Gson gson = new Gson();
        String payload = "";
        List<FatcaDetails> fatcaDetailsList = customerDataFields.getFatcaDetailsList();
        for (FatcaDetails fatcaObj : fatcaDetailsList) {
            if (fatcaObj.getFatcaDtlsId() == null) { // This is to handle the case if user changed the data after its
                // being inserted by using the back navigation within the
                // session.
                BigDecimal fatcaDtlsId = CommonUtils.generateRandomNum();
                fatcaObj.setFatcaDtlsId(fatcaDtlsId);
                fatcaDetailsIdList.add(fatcaDtlsId.toString());
            }
            fatcaObj.setApplicationId(applicationID);
            fatcaObj.setAppId(customerDataFields.getAppId());
            fatcaObj.setCustDtlId(custDtlId);
            fatcaObj.setVersionNum(version);
            payload = gson.toJson(fatcaObj.getPayload());
            fatcaObj.setPayloadColumn(payload);
            fatcaDtlsrepository.save(fatcaObj);
        }
        customerIdentification.setFatcaDetailsList(fatcaDetailsIdList);
        customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue of
        // Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_FATCA_DETAILS");
    }

    private void populateAppMasterAndApplnwf(CustomerDataFields customerDataFields,
                                             CustomerIdentificationCasa customerIdentification, String applicationID, BigDecimal custDtlId, int version,
                                             boolean isSelfOnBoardingHeaderAppId, Properties prop) {
        ApplicationMaster applicationMaster = customerDataFields.getApplicationMaster();
        ApplicationMaster applicationMasterToSave = new ApplicationMaster();
        BeanUtils.copyProperties(applicationMaster, applicationMasterToSave);
        applicationMasterToSave.setAppId(customerDataFields.getAppId());
        applicationMasterToSave.setApplicationId(applicationID);
        applicationMasterToSave.setVersionNum(version);
        applicationMasterToSave.setApplicationDate(LocalDate.now());
        applicationMasterToSave.setApplicationStatus(AppStatus.INPROGRESS.getValue());
        applicationMasterToSave.setCurrentScreenId(applicationMaster.getCurrentScreenId().split("~")[0]);
        if (!(CommonUtils.isNullOrEmpty(applicationMaster.getMobileNumber()))) {
            applicationMasterToSave.setMobileVerStatus("Y");
        }
        if (!(CommonUtils.isNullOrEmpty(applicationMaster.getEmailId()))) {
            applicationMasterToSave.setEmailVerStatus("Y");
        }
//        if ("Y".equalsIgnoreCase(customerDataFields.getIsExistingCustomer())) {
//            applicationMasterToSave.setApplicationType(Constants.ETB);
//        } else if ("N".equalsIgnoreCase(customerDataFields.getIsExistingCustomer())) {
//            applicationMasterToSave.setApplicationType(Constants.NTB);
//        }
        applicationMasterRepository.save(applicationMasterToSave);
        customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue of
        // Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_APPLICATION_MASTER");
        WorkFlowDetails wfObj = customerDataFields.getWorkflow();
        // If related application id is present then casa is the sub application so dont
        // insert to workflow.
        if ((CommonUtils.isNullOrEmpty(applicationMaster.getRelatedApplicationId())) && (wfObj != null)
                && (!isSelfOnBoardingHeaderAppId
                || ("N".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.ACCOUNT_STP.getKey()))))) {
            PopulateapplnWFRequest apiRequest = new PopulateapplnWFRequest();
            PopulateapplnWFRequestFields requestObj = new PopulateapplnWFRequestFields();
            requestObj.setAppId(customerDataFields.getAppId());
            requestObj.setApplicationId(applicationID);
            requestObj.setApplicationStatus(AppStatus.INPROGRESS.getValue());
            if (!isSelfOnBoardingHeaderAppId) {
                requestObj.setCreatedBy(applicationMaster.getCreatedBy());
            } else {
                requestObj.setCreatedBy(Constants.CUSTOMER);
            }
            requestObj.setVersionNum(version);
            requestObj.setWorkflow(wfObj);
            apiRequest.setRequestObj(requestObj);
            commonService.populateApplnWorkFlow(apiRequest);
            logger.warn("Data inserted into TB_ABOB_APPLN_WORKFLOW");
            Optional<ApplicationWorkflow> workflow = applnWfRepository
                    .findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(customerDataFields.getAppId(),
                            applicationID, version);
            if (workflow.isPresent()) {
                ApplicationWorkflow applnWf = workflow.get();
                List<WorkflowDefinition> wfDefnList = wfDefnRepository
                        .findByFromStageId(applnWf.getNextWorkFlowStage());
                customerIdentification.setApplnWfDefinitionList(wfDefnList);
            }
        }
    }

    private void populateCustomerDtls(CustomerDataFields customerDataFields,
                                      CustomerIdentificationCasa customerIdentification, String applicationID, BigDecimal custDtlId,
                                      int version) {
        Gson gson = new Gson();
        String payload;
        List<CustomerDetails> customerDetailsList = customerDataFields.getCustomerDetailsList();
        for (CustomerDetails customerDetails : customerDetailsList) {
            customerDetails.setApplicationId(applicationID);
            customerDetails.setAppId(customerDataFields.getAppId());
            customerDetails.setVersionNum(version);
            payload = gson.toJson(customerDetails.getPayload());
            customerDetails.setPayloadColumn(payload);
            customerDetails.setCustDtlId(custDtlId);
            customerDetails.setSeqNumber(customerDataFields.getApplicationMaster().getCustDtlSlNum());
            customerDetailsRepository.save(customerDetails);
        }
        customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue of
        // Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_CUSTOMER_DETAILS for CASA");
    }

    private void populateAddressDtls(CustomerDataFields customerDataFields,
                                     CustomerIdentificationCasa customerIdentification, String applicationID, BigDecimal custDtlId, int version,
                                     String relatedScreen) {
        Gson gson = new Gson();
        List<String> addressList = new ArrayList<>();
        List<AddressDetailsWrapper> addressDetailsWrapperList = customerDataFields.getAddressDetailsWrapperList();
        for (AddressDetailsWrapper addressDetailsWrapper : addressDetailsWrapperList) {
            List<AddressDetails> addressDetailsList = addressDetailsWrapper.getAddressDetailsList();
            for (AddressDetails addressDetails : addressDetailsList) {
                if (addressDetails.getAddressDtlsId() == null) {// This is to handle the case if user changed the data
                    // after its being inserted by using the back navigation
                    // within the session.
                    BigDecimal addressDtlId = CommonUtils.generateRandomNum();
                    addressDetails.setAddressDtlsId(addressDtlId);
                    addressList.add(addressDtlId.toString());// to String is required to avoid rounding issue of Big
                    // Decimal at front end.
                    if (Constants.CUSTOMER_DETAILS.equalsIgnoreCase(relatedScreen)) {
                        addressDetails.setUniqueId(custDtlId);
                    } else if (Constants.OCCUPATION_DETAILS.equalsIgnoreCase(relatedScreen)) {
                        List<String> occupationList = customerIdentification.getOccupationList();
                        for (String occptDtlId : occupationList) {
                            addressDetails.setUniqueId(new BigDecimal(occptDtlId));
                        }
                    } else if (Constants.NOMINEE_DETAILS.equalsIgnoreCase(relatedScreen)) {
                        List<String> nomineeList = customerIdentification.getNomineeList();
                        for (String nomineeDtlId : nomineeList) {
                            addressDetails.setUniqueId(new BigDecimal(nomineeDtlId));
                        }
                    }
                }
                addressDetails.setAppId(customerDataFields.getAppId());
                addressDetails.setApplicationId(applicationID);
                addressDetails.setCustDtlId(custDtlId);
                addressDetails.setVersionNum(version);
                String payload = gson.toJson(addressDetails.getPayload());
                addressDetails.setPayloadColumn(payload);
                addressDetailsRepository.save(addressDetails);
            }
            customerIdentification.setAddressList(addressList);
            customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue
            // of Big Decimal at front end.
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
        }
        logger.warn("Data inserted into TB_ABOB_ADDRESS_DETAILS for CASA");
    }

    private void populateOccupationdtls(CustomerDataFields customerDataFields,
                                        CustomerIdentificationCasa customerIdentification, String applicationID, BigDecimal custDtlId,
                                        int version) {
        Gson gson = new Gson();
        List<String> occupationList = new ArrayList<>();
        List<OccupationDetailsWrapper> occupationDetailsWrapperList = customerDataFields
                .getOccupationDetailsWrapperList();
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
            occupationDetails.setAppId(customerDataFields.getAppId());
            occupationDetails.setApplicationId(applicationID);
            occupationDetails.setCustDtlId(custDtlId);
            occupationDetails.setVersionNum(version);
            String payload = gson.toJson(occupationDetails.getPayload());
            occupationDetails.setPayloadColumn(payload);
            occupationDetailsRepository.save(occupationDetails);
        }
        customerIdentification.setOccupationList(occupationList);
        customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue of
        // Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_OCCUPATION_DETAILS for CASA");
    }

    private void populateNomineeDtls(CustomerDataFields customerDataFields,
                                     CustomerIdentificationCasa customerIdentification, String applicationID, BigDecimal custDtlId,
                                     int version) {
        Gson gson = new Gson();
        String payload;
        List<String> nomineeList = new ArrayList<>();
        List<NomineeDetailsWrapper> nomineeDetailsWrapperList = customerDataFields.getNomineeDetailsWrapperList();
        for (NomineeDetailsWrapper nomineeDetailsWrapper : nomineeDetailsWrapperList) {
            List<NomineeDetails> nomineeDetailsList = nomineeDetailsWrapper.getNomineeDetailsList();
            for (NomineeDetails nomineeDetails : nomineeDetailsList) {
                if (nomineeDetails.getNomineeDtlsId() == null) {// This is to handle the case if user changed the data
                    // after its being inserted by using the back navigation
                    // within the session.
                    BigDecimal nomineeDtlId = CommonUtils.generateRandomNum();
                    nomineeDetails.setNomineeDtlsId(nomineeDtlId);
                    nomineeList.add(nomineeDtlId.toString());// to String is required to avoid rounding issue of Big
                    // Decimal at front end.
                } else {
                    nomineeList.add(nomineeDetails.getNomineeDtlsId().toString());// to String is required to avoid
                    // rounding issue of Big Decimal at
                    // front end.
                }
                nomineeDetails.setApplicationId(applicationID);
                nomineeDetails.setCustDtlId(custDtlId);
                nomineeDetails.setVersionNum(version);
                nomineeDetails.setAppId(customerDataFields.getAppId());
                payload = gson.toJson(nomineeDetails.getPayload());
                nomineeDetails.setPayloadColumn(payload);
                nomineeDetails.setStatus(AppStatus.ACTIVE_STATUS.getValue());
                nomineeDetailsRepository.save(nomineeDetails);
            }
        }
        customerIdentification.setNomineeList(nomineeList);
        customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue of
        // Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_NOMINEE_DETAILS");
    }

    private void populateApplicationDocs(CustomerDataFields customerDataFields,
                                         CustomerIdentificationCasa customerIdentification, String applicationID, BigDecimal custDtlId,
                                         int version) {
        Gson gson = new Gson();
        List<String> documentList = new ArrayList<>();
        List<ApplicationDocumentsWrapper> applicationDocumentsWrapperList = customerDataFields
                .getApplicationDocumentsWrapperList();
        for (ApplicationDocumentsWrapper applicationDocumentsWrapper : applicationDocumentsWrapperList) {
            List<ApplicationDocuments> applicationDocumentsList = applicationDocumentsWrapper
                    .getApplicationDocumentsList();
            for (ApplicationDocuments applicationDocuments : applicationDocumentsList) {
                if (applicationDocuments.getAppDocId() == null) {// This is to handle the case if user changed the data
                    // after its being inserted by using the back
                    // navigation within the session.
                    BigDecimal appDocId = CommonUtils.generateRandomNum();
                    applicationDocuments.setAppDocId(appDocId);
                    documentList.add(appDocId.toString());// to String is required to avoid rounding issue of Big
                    // Decimal at front end.
                }
                applicationDocuments.setApplicationId(applicationID);
                applicationDocuments.setCustDtlId(custDtlId);
                applicationDocuments.setVersionNum(version);
                applicationDocuments.setAppId(customerDataFields.getAppId());
                String payload = gson.toJson(applicationDocuments.getPayload());
                applicationDocuments.setPayloadColumn(payload);
                applicationDocuments.setStatus(AppStatus.ACTIVE_STATUS.getValue());
                applicationDocumentsRepository.save(applicationDocuments);
            }
        }
        customerIdentification.setDocumentList(documentList);
        customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue of
        // Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_APPLN_DOCUMENTS for CASA");
    }

    private void populateBankingfacilities(CustomerDataFields customerDataFields,
                                           CustomerIdentificationCasa customerIdentification, String applicationID, BigDecimal custDtlId,
                                           int version) {
        Gson gson = new Gson();
        String payload = "";
        List<String> bankFacilityIdList = new ArrayList<>();
        List<BankingFacilities> bankFacilityList = customerDataFields.getBankingFacilityList();
        if (null != bankFacilityList) {
            for (BankingFacilities bankfacility : bankFacilityList) {
                if (bankfacility.getBankFacilityId() == null) {// This is to handle the case if user changed the data
                    // after its being inserted by using the back navigation
                    // within the session.
                    BigDecimal bankFacilityId = CommonUtils.generateRandomNum();
                    bankfacility.setBankFacilityId(bankFacilityId);
                    bankFacilityIdList.add(bankFacilityId.toString());
                }
                bankfacility.setApplicationId(applicationID);
                bankfacility.setAppId(customerDataFields.getAppId());
                bankfacility.setCustDtlId(custDtlId);
                bankfacility.setVersionNum(version);
                payload = gson.toJson(bankfacility.getPayload());
                bankfacility.setPayloadColumn(payload);
                bankingFacilitiesRepository.save(bankfacility);
            }
            customerIdentification.setBankFacilityList(bankFacilityIdList);
        }
        customerIdentification.setCustDtlId(custDtlId.toString());// to String is required to avoid rounding issue of
        // Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_BANKING_FACILITIES");
    }

    private void updateConfirmFlagInMaster(CreateModifyUserRequest request,
                                           CustomerIdentificationCasa customerIdentification, String applicationID, BigDecimal custDtlId,
                                           boolean isSelfOnBoardingAppId, int version, Properties prop, boolean isSelfOnBoardingHeaderAppId) {
        CustomerDataFields customerDataFields = request.getRequestObj();
        ApplicationMaster masterRequest = customerDataFields.getApplicationMaster();
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(customerDataFields.getAppId(),
                        customerDataFields.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
            if (masterObj.getApplicantsCount() == masterRequest.getCustDtlSlNum()) {
                WorkFlowDetails wfObj = customerDataFields.getWorkflow();
                PopulateapplnWFRequest apiRequest = new PopulateapplnWFRequest();
                PopulateapplnWFRequestFields requestObj = new PopulateapplnWFRequestFields();
                requestObj.setAppId(customerDataFields.getAppId());
                requestObj.setVersionNum(version);
                requestObj.setWorkflow(wfObj);
                apiRequest.setRequestObj(requestObj);
                if (isSelfOnBoardingAppId) { // self onboarding
                    if ("N".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.ACCOUNT_STP.getKey()))) {
                        if (!isSelfOnBoardingHeaderAppId) { // INITIATOR submits it after review.
                            requestObj.setApplicationStatus(AppStatus.PENDING.getValue());
                            requestObj.setCreatedBy(masterRequest.getCreatedBy());
                        } else {
                            requestObj.setCreatedBy(Constants.CUSTOMER);
                            requestObj.setApplicationStatus(AppStatus.INPROGRESS.getValue());
                        }
                        // If related application id is present then casa is the sub application so dont
                        // insert casa application id to workflow.
                        if ((CommonUtils.isNullOrEmpty(masterObj.getRelatedApplicationId()))) {
                            apiRequest.getRequestObj().setApplicationId(applicationID);
                        } else {
                            apiRequest.getRequestObj().setApplicationId(masterObj.getRelatedApplicationId());
                        }
                        commonService.populateApplnWorkFlow(apiRequest);
                    }
                } else { // assisted on boarding
                    requestObj.setApplicationStatus(AppStatus.PENDING.getValue());
                    requestObj.setCreatedBy(masterRequest.getCreatedBy());
                    String roleId = commonService.fetchRoleId(customerDataFields.getAppId(),
                            masterRequest.getCreatedBy());
                    if (wfObj.getCurrentRole().equalsIgnoreCase(roleId)) { // VAPT
                        // If related application id is present then casa is the sub application so dont
                        // insert casa application id to workflow.
                        if ((CommonUtils.isNullOrEmpty(masterRequest.getRelatedApplicationId()))) {
                            apiRequest.getRequestObj().setApplicationId(applicationID);
                        } else {
                            apiRequest.getRequestObj().setApplicationId(masterRequest.getRelatedApplicationId());
                        }
                        commonService.populateApplnWorkFlow(apiRequest);
                    } else {
                        logger.error("VAPT issue in updateAppMaster. Current role id from request is tampered.");
                    }
                }
            }
            customerIdentification.setCustDtlId(custDtlId.toString()); // to String is required to avoid rounding issue
            // of Big Decimal at front end.
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
        }
    }

    public CreateModifyUserRequest formExtReq(String appId, String applicationId, int versionNum, String accNum,
                                              BigDecimal customerId, FundAccountRequestFields fundAccountRequestFields) {
        CreateModifyUserRequest request = new CreateModifyUserRequest();
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, versionNum);
        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            ApplicationMaster applicationMasterDataDB = new ApplicationMaster();
            BeanUtils.copyProperties(applicationMasterData, applicationMasterDataDB);
            applicationMasterDataDB.setCreateTs(null); // to avoid jackson parsing error. Need to send data based on
            // external service request during implementation.
            applicationMasterDataDB.setApplicationDate(null); // to avoid jackson parsing error. Need to send data based
            // external service request during implementation.
            CustomerDataFields requestObj = getCustomerData(applicationMasterDataDB, applicationId, appId, versionNum);
            logger.error("requestObj 1 :" + requestObj.toString());
            requestObj.setFundAccount(fundAccountRequestFields);
            request.setRequestObj(requestObj);
            return request;
        }
        return null;
    }

    public static String extractUserId(String value) {
        if (value == null) {
            return null;
        }
        String v = value.trim();
        int open = v.lastIndexOf('(');
        if (open >= 0 && v.endsWith(")")) {
            return v.substring(open + 1, v.length() - 1).trim();
        }
        return v;
    }

    public static boolean isSameUser(String a, String b) {
        String ua = extractUserId(a);
        String ub = extractUserId(b);
        return ua != null && ua.equalsIgnoreCase(ub);
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchApplicationFallback")
    public Response fetchApplication(FetchDeleteUserRequest fetchUserDetailsRequest, String src,
                                     boolean isSelfOnBoardingAppId, Properties prop) {
        Optional<ApplicationMaster> applicationMasterOpt = Optional.empty();
        logger.debug("Fetch application request request : {}, src: {}, isSelfOnBoardingAppId : {}", fetchUserDetailsRequest, src, isSelfOnBoardingAppId);
        String applicationId = fetchUserDetailsRequest.getRequestObj().getApplicationId();
        String appId = fetchUserDetailsRequest.getRequestObj().getAppId();
		int versionNum = Constants.INITIAL_VERSION_NO;
        CustomerDataFields customerDataFields;
        Gson gson = new Gson();
        Response fetchUserDetailsResponse = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        fetchUserDetailsResponse.setResponseHeader(responseHeader);
        ResponseBody responseBody = new ResponseBody();
        String roleId = "";
        if (isSelfOnBoardingAppId) {
            if ("fetchapplication".equalsIgnoreCase(src)) {
                applicationMasterOpt = applicationMasterRepository.findByAppIdAndApplicationIdAndVersionNum(appId,
                        applicationId, versionNum);
            } else if ("downloadApplication".equalsIgnoreCase(src)) {
                applicationMasterOpt = applicationMasterRepository
                        .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(appId, applicationId, versionNum,
                                AppStatus.APPROVED.getValue());
            }
        } else {
            roleId = commonService.fetchRoleId(fetchUserDetailsRequest.getAppId(),
                    fetchUserDetailsRequest.getRequestObj().getUserId()); // Taking app id outside requestObj bec app id
            applicationMasterOpt = applicationMasterRepository
                    .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId,
                            versionNum);
            // }
        }
        logger.debug("roleID : {}", roleId);
        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            String applicationBranchId = applicationMasterData.getBranchId();
            String productCode = applicationMasterData.getProductCode();
            logger.debug("applicationBranchId : {}", applicationBranchId);
            logger.debug("fetchUserDetailsRequest.getRequestObj().getIsPreview() : {}",fetchUserDetailsRequest.getRequestObj().getIsPreview());
            boolean isPreview = "Y".equalsIgnoreCase(fetchUserDetailsRequest.getRequestObj().getIsPreview());
            Optional<WhitelistedBranches> whitelistedBranch = whitelistedBranchesRepository.findByBranchCode(applicationBranchId);
            if (!isPreview) {
                if (!whitelistedBranch.isPresent()) {
                    responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                    responseBody.setResponseObj("The branch {branchid} is not whitelisted for COB applications.".replace("{branchid}", applicationBranchId));
                    logger.debug("ResponseObj : {}", responseBody.getResponseObj());
                    fetchUserDetailsResponse.setResponseHeader(responseHeader);
                    fetchUserDetailsResponse.setResponseBody(responseBody);
                    return fetchUserDetailsResponse;
                }
                switch (productCode) {
                    case Constants.UNNATI_PRODUCT_CODE:
                        if (!"Y".equalsIgnoreCase(whitelistedBranch.get().getUnnatiEnabled())) {
                            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                            responseBody.setResponseObj("Applications for Unnati product are not allowed from branch {branchid}.".replace("{branchid}", applicationBranchId));
                            logger.debug("ResponseObj : {}", responseBody.getResponseObj());
                            fetchUserDetailsResponse.setResponseHeader(responseHeader);
                            fetchUserDetailsResponse.setResponseBody(responseBody);
                            return fetchUserDetailsResponse;
                        }
                        break;
                    case Constants.RENEWAL_LOAN_PRODUCT_CODE:
                        if (!"Y".equalsIgnoreCase(whitelistedBranch.get().getRenewalEnabled())) {
                            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                            responseBody.setResponseObj("Applications for Renewal Loan product are not allowed from branch {branchid}.".replace("{branchid}", applicationBranchId));
                            logger.debug("ResponseObj : {}", responseBody.getResponseObj());
                            fetchUserDetailsResponse.setResponseHeader(responseHeader);
                            fetchUserDetailsResponse.setResponseBody(responseBody);
                            return fetchUserDetailsResponse;
                        }
                        break;
                    case Constants.OPENMARKET_LOAN_PRODUCT_CODE:
                        logger.debug("Open market whitelisting check is done in DigiAgil, so skipping the check here.");
                        break;
                    default:

                        if (ProductCode.isValidUnnatiCode(productCode)) {
                            logger.debug("No whitelisting check configured for product code {}, skipping.", productCode);
                            break;
                        }

                        logger.error("Invalid Product code {} for applicationId: {}", productCode, applicationId);
                        responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                        responseBody.setResponseObj("Invalid Product : " + productCode + " code for applicationId: " + applicationId);
                        logger.debug("ResponseObj : {}", responseBody.getResponseObj());
                        fetchUserDetailsResponse.setResponseHeader(responseHeader);
                        fetchUserDetailsResponse.setResponseBody(responseBody);
                        return fetchUserDetailsResponse;
                }

                String currentUser = fetchUserDetailsRequest.getRequestObj().getUserId();
                String lockedBy = applicationMasterData.getUpdatedBy();
                if (applicationMasterData.getLockTs() != null
                        && lockedBy != null
                        && !isSameUser(currentUser, lockedBy)
                        && CommonUtils.isApplicationLocked(fetchUserDetailsRequest, applicationMasterData)) {
                    responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                    responseBody.setResponseObj(
                            "Application is currently being used by user (" + lockedBy + ")"
                    );
                    fetchUserDetailsResponse.setResponseBody(responseBody);
                    return fetchUserDetailsResponse;
                } else {
                    logger.debug("Locking application since either no lock is present or locked by same user or lock has expired");
                    int i = applicationMasterRepository.updateTimestampOnLogin(applicationId, currentUser);
                    logger.debug(" application locked --> {} ", applicationId);
                }


                if (AppStatus.LUC.getValue().equalsIgnoreCase(applicationMasterData.getApplicationStatus())) {
                    List<String> disbursedStatus = new ArrayList<>();
                    disbursedStatus.add(AppStatus.DISBURSED.getValue());
                    List<ApplicationWorkflow> disbursedWorkflow = applnWfRepository.findByApplicationIdAndApplicationStatusInOrderByWorkflowSeqNumDesc(applicationId, disbursedStatus);
                    if (disbursedWorkflow.isEmpty()) {
                        logger.error("No Disbursed Status found in the workflow");
                        responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                        responseBody.setResponseObj("No Disbursed Status found in the workflow, Please Verify");
                        logger.debug("ResponseObj : {}", responseBody.getResponseObj());
                        fetchUserDetailsResponse.setResponseHeader(responseHeader);
                        fetchUserDetailsResponse.setResponseBody(responseBody);
                        return fetchUserDetailsResponse;
                    }
                    LocalDate disbursedDate = disbursedWorkflow.get(0).getCreateTs().toLocalDate();
                    int disbursementWaitDays = Integer.parseInt(prop.getProperty(CobFlagsProperties.LUC_WAIT_DAYS.getKey()));
                    LocalDate waitPeriodEndDate = disbursedDate.plusDays(disbursementWaitDays);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    String formattedWaitPeriodEndDate = waitPeriodEndDate.format(formatter);
                    if (LocalDate.now().isBefore(waitPeriodEndDate)) {
                        logger.error("Disbursement wait period has not been completed. Please check after : {}", formattedWaitPeriodEndDate);

                        responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                        responseBody.setResponseObj("Disbursement wait period has not been completed. Please check after : " + formattedWaitPeriodEndDate);

                        logger.debug("ResponseObj : {}", responseBody.getResponseObj());
                        fetchUserDetailsResponse.setResponseHeader(responseHeader);
                        fetchUserDetailsResponse.setResponseBody(responseBody);

                        return fetchUserDetailsResponse;
                    }
                }
            }

            customerDataFields = getCustomerData(applicationMasterData, applicationId, appId, versionNum);
            logger.debug("customerDataFields" + " " + customerDataFields);
            logger.debug("Before getCustomerData applicationId={}, appId={}",
                    applicationId, appId);

            if (CobFlagsProperties.RPC.getKey().equalsIgnoreCase(roleId)) {
                Optional<RpcStageVerification> rpcStageData = rpcStageVerificationRepository.findById(applicationId);
                logger.debug("RPCStageData " +
                        " " + rpcStageData);
                if (rpcStageData.isPresent()) {
                    RpcStageVerification rpcData = rpcStageData.get();
                    rpcData.setVerifiedStages(null);
                    rpcStageVerificationRepository.save(rpcData);
                    logger.debug("Rpc Data  " + rpcData);

                    customerDataFields.setRpcStatDetails(
                            CommonUtils.parseRPCStageVerificationData(rpcData.getEditedFields(), rpcData.getQueries()));
                    customerDataFields.setVerifiedStage(
                            null != rpcData.getVerifiedStages() ? Arrays.asList(rpcData.getVerifiedStages().split("\\|"))
                                    : new ArrayList<>());
                }
            }
            logger.debug("fetchApplication: Credit Assessment");
            Optional<BCMPIStageVerification> bcmpiStageData = bcmpiStageVerificationRepository.findById(applicationId);
            if (bcmpiStageData.isPresent()) {
                logger.debug("bcmpiStageData found");
                customerDataFields.setBcmpiStatDetails(
                        CommonUtils.parseBCMPIStageVerificationData(bcmpiStageData.get().getEditedFields(), bcmpiStageData.get().getQueries()));
                customerDataFields.setBcmpiVerifiedStage(null != bcmpiStageData.get().getVerifiedStages()
                        ? Arrays.asList(bcmpiStageData.get().getVerifiedStages().split("\\|")) : new ArrayList<>());
            }
            Optional<LUCEntity> LucData = lucRepository.findById(applicationId);
            if (LucData.isPresent()) {
                logger.debug("lucData found");
                LucPayloadRequest LUCDetailsWrapper = gson.fromJson(LucData.get().getPayload(), LucPayloadRequest.class); // Object to be changed to a wrapper class
                LUCEntity lucD = LucData.get();
                lucD.setLucDetailsWrapper(LUCDetailsWrapper);
                customerDataFields.setLucDetails(lucD);


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
                LoanObligationsWrapper loanObligationsWrapper = gson.fromJson(bcmpiLoanObligationsOpt.get().getPayload(), LoanObligationsWrapper.class);
                BCMPILoanObligations bcmpiLoanObligations = bcmpiLoanObligationsOpt.get();
                bcmpiLoanObligations.setLoanObligationsWrapper(loanObligationsWrapper);
                customerDataFields.setBcmpiLoanObligations(bcmpiLoanObligations);
            }
            Optional<BCMPIOtherDetails> bcmpiOtherDetailsOpt = bcmpiOtherDetailsRepo.findById(applicationId);
            if (bcmpiOtherDetailsOpt.isPresent()) {
                logger.debug("bcmpiIncomeData found");
                BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = gson.fromJson(bcmpiOtherDetailsOpt.get().getPayload(), BCMPIOtherDetailsWrapper.class);
                BCMPIOtherDetails bcmpiOtherDetails = bcmpiOtherDetailsOpt.get();
                bcmpiOtherDetails.setBcmpiOtherDetailsWrapper(bcmpiOtherDetailsWrapper);
                customerDataFields.setBcmpiOtherDetails(bcmpiOtherDetails);
            }

            Optional<List<Enach>> enachDetails = enachRepo.findByApplicationIdAndAppId(applicationId, appId);
            if (enachDetails.isPresent()) {
                logger.debug("enachData found");
                customerDataFields.setEnachDetails(enachDetails.get());
            }

            Optional<List<Udhyam>> udhyamRecordsOpt = udhyamRepository.findByApplicationId(applicationId);
            if (udhyamRecordsOpt.isPresent()) {
                List<Udhyam> udhyamRecords = udhyamRecordsOpt.get();
                customerDataFields.setUdhyamDetails(udhyamRecords);
            }

            whitelistedBranch.ifPresent(branch -> {
                String value = "Y".equalsIgnoreCase(branch.getBotApiEnabled()) ? "Y" : "N";

                CustomerDataFields.BotEnabled botEnabled = new CustomerDataFields.BotEnabled();
                botEnabled.setBotEnabled(value);

                customerDataFields.setBipData(Collections.singletonList(botEnabled));
            });

            Optional<List<Documents>> documentRecordsOpt = documentsRepository.findByApplicationId(applicationId);
            logger.debug("documentRecordsOpt value:" + documentRecordsOpt);
            if (documentRecordsOpt.isPresent()) {
                logger.error("documentRecordsOpt present : " + (documentRecordsOpt));
                List<Documents> documentRecords = documentRecordsOpt.get();
                logger.error("documentRecords present : " + (null == documentRecords));
                logger.error("documentRecords present : " + documentRecords.toString());
                if (!documentRecords.isEmpty()) {
                    customerDataFields.setDocumentRecordDetails(documentRecords);
                }
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
                        ? Arrays.asList(dbKitStageData.get().getVerifiedStages().split("\\|")) : new ArrayList<>());
                customerDataFields.setDbKitResponse(gson.fromJson(dbKitStageData.get().getResponse(), new TypeToken<List<DBKITResponse>>() {
                }.getType()));
                customerDataFields.setApprovedDocs(gson.fromJson(dbKitStageData.get().getApprovedDocs(), new TypeToken<List<String>>() {
                }.getType()));
                customerDataFields.setDbVerificationQueries(
                        gson.fromJson(dbKitStageData.get().getQueryDocs(), new TypeToken<List<String>>() {
                        }.getType()));
                customerDataFields.setReuploadedDocs(
                        gson.fromJson(dbKitStageData.get().getReuploadedDocs(), new TypeToken<List<String>>() {
                        }.getType())
                );
            }


            AMLQuestionnaireDetails amlQuestionnaireDetails = amlQuestionnaireRepo.findById(applicationId).orElse(null);
            if(amlQuestionnaireDetails!= null && amlQuestionnaireDetails.getPayloadColumn() != null){
                logger.debug("Fetched AML Questionnaire Details");
                amlQuestionnaireDetails.setPayload(gson.fromJson(amlQuestionnaireDetails.getPayloadColumn(), AmlQuestionnairePayload.class));
                customerDataFields.setAmlQuestion(amlQuestionnaireDetails);
            }

            if (AppStatus.DBKITGENERATED.getValue().equalsIgnoreCase(applicationMasterData.getApplicationStatus())) {
                Optional<DBKITStageVerification> dbkitStageVerificationOpt = dbkitStageVerificationRepository.findById(applicationId);
                if (dbkitStageVerificationOpt.isPresent()) {
                    dbkitStageVerificationOpt.get().setVerifiedStages(null);
                    dbkitStageVerificationRepository.save(dbkitStageVerificationOpt.get());
                }
            }
            String customerdata = gson.toJson(customerDataFields);
            customerdata = customerdata.replace(Constants.PAYLOAD_COLUMN, Constants.PAYLOAD);
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
            responseBody.setResponseObj(customerdata);
            fetchUserDetailsResponse.setResponseBody(responseBody);
            return fetchUserDetailsResponse;
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            responseBody.setResponseObj(Constants.APP_MASTER_NOT_FOUND);
            fetchUserDetailsResponse.setResponseBody(responseBody);
            return fetchUserDetailsResponse;
        }
    }

    public LoanDetails prepareLoanDetails(ApplicationMaster master) {
        if (master == null || StringUtils.isBlank(master.getApplicationStatus())) {
            return null;
        }
        String applicationId = master.getApplicationId();
        LoanDetails loanDetails = loanDtlsRepo.findByApplicationId(applicationId);
        if (loanDetails == null || StringUtils.isBlank(loanDetails.getPayloadColumn())) {
            return loanDetails;
        }
        Gson gson = new Gson();
        LoanDetailsPayload payload =
                gson.fromJson(loanDetails.getPayloadColumn(), LoanDetailsPayload.class);
        if (payload == null) {
            return loanDetails;
        }
        if (StringUtils.isNotBlank(payload.getRemittanceAmount())) {
            return loanDetails;
        }
        String applicationMasterStatus = master.getApplicationStatus().toUpperCase();
        List<String> disbursedStatuses = Arrays.asList(
                AppStatus.DISBURSED.getValue(),
                AppStatus.LUC.getValue(),
                AppStatus.PENDINGLUCVERIFICATION.getValue(),
                AppStatus.LUCVERIFIED.getValue()
        );
        if (!disbursedStatuses.contains(applicationMasterStatus)) {
            return loanDetails;
        }
        Optional<ApiExecutionLog> apiExecutionDisburseRecordOpt =
                apiExecutionLogRepository
                        .findTopByApplicationIdAndApiNameOrderByCreateTsDesc(applicationId, "Loan Disbursement");
        if (!apiExecutionDisburseRecordOpt.isPresent()) {
            return loanDetails;
        }
        ApiExecutionLog apiExecutionDisburseRecord = apiExecutionDisburseRecordOpt.get();
        if (!(Constants.SUCCESS.equalsIgnoreCase(apiExecutionDisburseRecord.getApiStatus())
                || Constants.SUCCESS_DMS.equalsIgnoreCase(apiExecutionDisburseRecord.getApiStatus()))) {
            return loanDetails;
        }
        String disbApiRespJsonString = apiExecutionDisburseRecord.getResponsePayload();
        if (StringUtils.isBlank(disbApiRespJsonString)) {
            return loanDetails;
        }
        String lnRemActAm;
        try {
            JSONObject root = new JSONObject(disbApiRespJsonString);
            JSONObject body = root.optJSONObject("body");
            if (body == null) {
                return loanDetails;
            }
            lnRemActAm = body.optString("lnRemitAmt", null);
            if (StringUtils.isBlank(lnRemActAm)) {
                return loanDetails;
            }
        } catch (Exception e) {
            logger.error("Error parsing disbursement API response", e);
            return loanDetails;
        }
        try {
            payload.setRemittanceAmount(lnRemActAm);
            loanDetails.setPayloadColumn(gson.toJson(payload));
            loanDtlsRepo.save(loanDetails);
        } catch (Exception e) {
            logger.error("Error updating loan details payload", e);
        }
        return loanDetails;
    }

    public CustomerDataFields getCustomerData(ApplicationMaster applicationMasterData, String applicationId,
                                              String appId, int versionNum) {
        Properties prop = null;
        try {
            prop = CommonUtils.readPropertyFile();
        } catch (IOException e) {
            logger.error("Error while reading property file in deleteNominee ", e);
        }
        CustomerDataFields customerDataFields = new CustomerDataFields();
        customerDataFields.setAppId(applicationMasterData.getAppId());
        customerDataFields.setApplicationId(applicationMasterData.getApplicationId());
        customerDataFields.setApplicationMaster(applicationMasterData);

        List<CustomerDetails> customerDetailsList =
                customerDetailsRepository.findByApplicationIdAndAppIdAndVersionNum(
                        applicationId, appId, versionNum);

        Gson gson = new Gson();
        List<CustomerDetails> updatedList = new ArrayList<>();

        for (CustomerDetails customerDetails : customerDetailsList) {
            String payloadColumn = customerDetails.getPayloadColumn();
            if (StringUtils.isBlank(payloadColumn)) {
                continue;
            }
            CustomerDetailsPayload payload =
                    gson.fromJson(payloadColumn, CustomerDetailsPayload.class);
            if (payload == null) {
                continue;
            }
            if (StringUtils.isNotBlank(payload.getFirstName())) {
                continue;
            }
            String customerName = customerDetails.getCustomerName();
            if (StringUtils.isBlank(customerName)) {
                continue;
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
            payload.setFirstName(firstName);
            payload.setLastName(lastName);
            customerDetails.setPayloadColumn(gson.toJson(payload));
            updatedList.add(customerDetails);
        }
        if (!updatedList.isEmpty()) {
            customerDetailsRepository.saveAll(updatedList);
        }
        customerDataFields.setCustomerDetailsList(customerDetailsList);

        AddressDetailsWrapper addressDetailsWrapper = new AddressDetailsWrapper();
        List<AddressDetailsWrapper> addressDetailsWrapperList = new ArrayList<>();
        List<AddressDetails> addressDetailsList = addressDetailsRepository
                .findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        addressDetailsWrapper.setAddressDetailsList(addressDetailsList);
        addressDetailsWrapperList.add(addressDetailsWrapper);
        customerDataFields.setAddressDetailsWrapperList(addressDetailsWrapperList);

        List<OccupationDetailsWrapper> occupationDetailsWrapperList = new ArrayList<>();
        OccupationDetailsWrapper occupationDetailsWrapper;
        List<OccupationDetails> occupationDetailsList = occupationDetailsRepository
                .findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        for (OccupationDetails occupationDetails : occupationDetailsList) {
            occupationDetailsWrapper = new OccupationDetailsWrapper();
            occupationDetailsWrapper.setOccupationDetails(occupationDetails);
            occupationDetailsWrapperList.add(occupationDetailsWrapper);
        }
        customerDataFields.setOccupationDetailsWrapperList(occupationDetailsWrapperList);

        // insuranceDetails
        List<InsuranceDetailsWrapper> insuranceDetailsWrapper = new ArrayList<>();
        List<InsuranceDetails> insuranceDetails = insuranceRepository.findByApplicationIdAndAppId(applicationId, appId);
        if (!insuranceDetails.isEmpty()) {
            insuranceDetails.forEach(insurance -> {
                InsuranceDetailsWrapper wrapperDetails = InsuranceDetailsWrapper.builder().insuranceDetails(insurance)
                        .build();
                insuranceDetailsWrapper.add(wrapperDetails);
            });
            customerDataFields.setInsuranceDetailsWrapperList(insuranceDetailsWrapper);
        } else {
            customerDataFields.setInsuranceDetailsWrapperList(null);
        }
        // branchDetails
        List<BankDetailsWrapper> bankDetails = new ArrayList<>();
        Optional<List<BankDetails>> bankDetailsList = bankDetailsRepository
                .findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        if (bankDetailsList.isPresent() && !bankDetailsList.get().isEmpty()) {
            bankDetailsList.get().forEach(bankDetail -> {
                BankDetailsWrapper detailsBankWrapper = BankDetailsWrapper.builder().bankDetails(bankDetail).build();
                bankDetails.add(detailsBankWrapper);
            });
            customerDataFields.setBankDetailsWrapperList(bankDetails);
        } else {
            customerDataFields.setBankDetailsWrapperList(null);
        }

        // CibilDetails
        populateCibilDetails(applicationId, appId, versionNum, customerDataFields);

        LocalDate applicantCBDate = null;
        LocalDate coApplicantCBDate = null;
        if (null != customerDataFields.getCibilDetailsWrapperList()) {
            for (CustomerDetails customerDetail : customerDataFields.getCustomerDetailsList()) {
                for (CibilDetailsWrapper cibilDetail : customerDataFields.getCibilDetailsWrapperList()) {
                    if (cibilDetail.getCibilDetails().getCustDtlId().compareTo(customerDetail.getCustDtlId()) == 0
                            && ("Applicant".equalsIgnoreCase(customerDetail.getCustomerType()))) {
                        applicantCBDate = cibilDetail.getCibilDetails().getCbDate();
                    }
                    if (cibilDetail.getCibilDetails().getCustDtlId().compareTo(customerDetail.getCustDtlId()) == 0
                            && (!"Applicant".equalsIgnoreCase(customerDetail.getCustomerType()))) {
                        coApplicantCBDate = cibilDetail.getCibilDetails().getCbDate();
                    }
                }
            }
        }
        ApplicationMaster appMasterData = customerDataFields.getApplicationMaster();
        if ((applicantCBDate != null) && (CommonUtils.getDateDiff(applicantCBDate, LocalDate.now()) > Integer
                .parseInt(prop.getProperty(CobFlagsProperties.CB_EXPIRY_DAYS.getKey())))) {
            appMasterData.setApplicantCBExpiry(true);
        } else {
            appMasterData.setApplicantCBExpiry(false);
        }
        if ((coApplicantCBDate != null) && (CommonUtils.getDateDiff(coApplicantCBDate, LocalDate.now()) > Integer
                .parseInt(prop.getProperty(CobFlagsProperties.CB_EXPIRY_DAYS.getKey())))) {
            appMasterData.setCoApplicantCBExpiry(true);
        } else {
            appMasterData.setCoApplicantCBExpiry(false);
        }
        customerDataFields.setApplicationMaster(appMasterData);

        List<ExistingLoanDetailsWrapper> existingLoanDetailsWrapper = new ArrayList<>();
        Optional<List<ExistingLoanDetails>> existingLoandDetails = existingLoanRepository
                .findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        if (existingLoandDetails.isPresent() && !existingLoandDetails.get().isEmpty()) {
            ExistingLoanDetailsWrapper existingWrapper = ExistingLoanDetailsWrapper.builder()
                    .existingLoanDetailsList(existingLoandDetails.get()).build();
            existingLoanDetailsWrapper.add(existingWrapper);
            customerDataFields.setExistingLoanDetailsWrapperList(existingLoanDetailsWrapper);
        } else {
            customerDataFields.setExistingLoanDetailsWrapperList(existingLoanDetailsWrapper);
        }

//        LoanDetails loanDetail = loanDtlsRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId,
//                versionNum);
        customerDataFields.setLoanDetails(prepareLoanDetails(applicationMasterData));

        NomineeDetailsWrapper nomineeDetailsWrapper = new NomineeDetailsWrapper();
        List<NomineeDetailsWrapper> nomineeDetailsWrapperList = new ArrayList<>();
        List<NomineeDetails> nomineeDetailsList = nomineeDetailsRepository
                .findByApplicationIdAndAppIdAndVersionNumAndStatus(applicationId, appId, versionNum,
                        AppStatus.ACTIVE_STATUS.getValue());
        nomineeDetailsWrapper.setNomineeDetailsList(nomineeDetailsList);
        nomineeDetailsWrapperList.add(nomineeDetailsWrapper);
        customerDataFields.setNomineeDetailsWrapperList(nomineeDetailsWrapperList);

        ApplicationDocumentsWrapper applicationDocumentsWrapper = new ApplicationDocumentsWrapper();
        List<ApplicationDocumentsWrapper> applicationDocumentsWrapperList = new ArrayList<>();
        List<ApplicationDocuments> applicationDocumentsList = applicationDocumentsRepository
                .findByApplicationIdAndAppIdAndVersionNumAndStatus(applicationId, appId, versionNum,
                        AppStatus.ACTIVE_STATUS.getValue());
        applicationDocumentsWrapper.setApplicationDocumentsList(applicationDocumentsList);
        applicationDocumentsWrapperList.add(applicationDocumentsWrapper);
        customerDataFields.setApplicationDocumentsWrapperList(applicationDocumentsWrapperList);

        List<BankingFacilities> bankingFacilityList = bankingFacilitiesRepository
                .findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        customerDataFields.setBankingFacilityList(bankingFacilityList);

        List<FatcaDetails> fatcaDetailsList = fatcaDtlsrepository
                .findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        customerDataFields.setFatcaDetailsList(fatcaDetailsList);

        List<CRSDetails> crsDetailsList = crsDtlsrepository.findByApplicationIdAndAppIdAndVersionNum(applicationId,
                appId, versionNum);
        customerDataFields.setCrsDetailsList(crsDetailsList);

        Optional<ApplicationWorkflow> workflow;
        /*
         * if
         * (!CommonUtils.isNullOrEmpty(applicationMasterData.getRelatedApplicationId()))
         * { workflow = applnWfRepository.
         * findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(appId,
         * applicationMasterData.getRelatedApplicationId(), versionNum); } else {
         */
        workflow = applnWfRepository.findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(appId,
                applicationId, versionNum);
        /* } */
        if (workflow.isPresent()) {
            ApplicationWorkflow applnWf = workflow.get();
            List<WorkflowDefinition> wfDefnLis = wfDefnRepository.findByFromStageId(applnWf.getNextWorkFlowStage());
            customerDataFields.setApplnWfDefinitionList(wfDefnLis);
        }
        UnnatiIexceedCDHLead cdhLeadDetails = null;
        logger.debug("Caling CDH" + applicationMasterData.getSearchCode2() + "/"
                + ProductCode.getCdhCodeByUnnatiCode(applicationMasterData.getProductCode()));
        String cdhProductCode = ProductCode.getCdhCodeByUnnatiCode(applicationMasterData.getProductCode());
        if(ProductCode.OPEN_MARKET.getUnnatiCode().equalsIgnoreCase(applicationMasterData.getProductCode())){
            cdhProductCode = ProductCode.OPEN_MARKET.getUnnatiCode();
            }

        Optional<UnnatiIexceedCDHLead> cdhLeadOpt = unnatiIexceedCDHLeadRepo.findByCustomerIdAndProductAndReferenceId(
                applicationMasterData.getSearchCode2(),
                cdhProductCode, applicationMasterData.getWorkitemNo());
        if (cdhLeadOpt.isPresent()) {
            cdhLeadDetails = cdhLeadOpt.get();
            if(("IEXCEED").equalsIgnoreCase(cdhLeadDetails.getSourceOfTheApplication()))
                cdhLeadDetails.setSourceOfTheApplication("UNNATI");
            }

        List<UnnatiCoApplicantDetails> CoApplicantData = null;
        try {
            CoApplicantData = coApplicantDetailsRepository.findByCustomerId(applicationMasterData.getSearchCode2());
        } catch (Exception e) {
            logger.debug("CoApplicantData fetch Error:", e.getMessage());
            }
        logger.debug("CoApplicantData Data:" + CoApplicantData);
        customerDataFields.setCdhCoAppLeadDetails(
                (CoApplicantData != null && !CoApplicantData.isEmpty()) ? CoApplicantData.get(0) : null);


        List<DeviationRATracker> deviationRATrackerList = deviationRATrackerRepository.findByApplicationIdOrderByCreateTsAsc(applicationId);
        if (deviationRATrackerList != null && !deviationRATrackerList.isEmpty()) {
            deviationRATrackerList.forEach(deviationRATracker -> {
                deviationRATracker.setApprovedTimeStamp(
                        deviationRATracker.getApprovedTs().format(Constants.ADMINFORMATTER));
                deviationRATracker.setCreatedTimeStamp(
                        deviationRATracker.getCreateTs().format(Constants.ADMINFORMATTER));
            });
            customerDataFields.setDeviationRATrackerList(deviationRATrackerList);
        }
        customerDataFields.setCdhLeadDetails(cdhLeadDetails);

        customerDataFields.setApplicationTimelineDtl(
                commonService.getApplicationTimelineDtl(applicationMasterData.getApplicationId()));

        Optional<List<Enach>> enachDetails = enachRepo.findByApplicationIdAndAppId(applicationId, appId);
        if (enachDetails.isPresent() && !enachDetails.get().isEmpty()) {
            customerDataFields.setEnachDetails(enachDetails.get());
        } else {
            customerDataFields.setEnachDetails(null);
        }
        Optional<SourcingResponseTracker> sourcingResponseTrackerOpt = sourcingResponseTrackerRepo.findById(applicationId);
        if (sourcingResponseTrackerOpt.isPresent()) {
            SourcingResponseTracker sourcingResponseTracker = sourcingResponseTrackerOpt.get();
            customerDataFields.setSourcingQueryResponse(sourcingResponseTracker);
        }

        String appStatus = applicationMasterData.getApplicationStatus();
        String stage = AppStatus.RPCVERIFIED.getValue();
        String subStage = null;
        if (AppStatus.RPCVERIFIED.getValue().equalsIgnoreCase(appStatus)) {
            subStage = Constants.REVIEW_SUBMIT;
        } else if (AppStatus.CACOMPLETED.getValue().equalsIgnoreCase(appStatus)
                || AppStatus.RESANCTION.getValue().equalsIgnoreCase(appStatus)) {
            subStage = Constants.IN_PRINCIPLE_DECISION;
        }
        if (subStage != null) {
            Page<CibilDetailsHistory> cbHistoryPage = cibilDetailsHisRepository.findByApplicationIdAndStageAndSubStage(
                    applicationId, stage, subStage, Constants.COAPPLICANT, PageRequest.of(0, 1)
            );
            if (cbHistoryPage.hasContent()) {
                customerDataFields.setCibilDetailsHistory(cbHistoryPage.getContent().get(0));
            }
        }
        SorucingStageVerification srcStageVerification = srcStageVerificationRepo.findById(applicationId).orElse(null);
        if(srcStageVerification!= null){
            logger.debug("Fetched Sourcing Verified Stages and application status : {}", applicationMasterData.getApplicationStatus());

            customerDataFields.setSrcStatDetails(
                    CommonUtils.parseSrcStageVerificationData(srcStageVerification.getEditedFields(), srcStageVerification.getQueries()));

            String currentStage = AppStatus.PENDING.getValue().equalsIgnoreCase(applicationMasterData.getApplicationStatus())
                    ? srcStageVerification.getBmVerifiedStages() : srcStageVerification.getVerifiedStages();
            List<String> verifiedStages = currentStage!= null ? Arrays.asList(currentStage.split(",")) : new ArrayList<>();
            customerDataFields.setSrcVerifiedStages(verifiedStages);
        }
        return customerDataFields;
    }

    @Transactional
    public void populateCibilDetails(String applicationId, String appId, Integer versionNum,
                                     CustomerDataFields customerDataFields) {
        List<CibilDetailsWrapper> cibilDetailsWrapper = new ArrayList<>();
        Optional<List<CibilDetails>> optionalList =
                cibilDetailsRepository.findByApplicationIdAndAppIdAndVersionNum(
                        applicationId, appId, versionNum);
        if (!optionalList.isPresent() || optionalList.get().isEmpty()) {
            return;
        }
        List<CibilDetails> cibilDetails = optionalList.get();
        // Group by custDtlId
        Map<BigDecimal, List<CibilDetails>> grouped =
                cibilDetails.stream()
                        .filter(c -> c.getCustDtlId() != null)
                        .collect(Collectors.groupingBy(CibilDetails::getCustDtlId));

        List<CibilDetails> recordsToDelete = new ArrayList<>();
        List<CibilDetails> finalList = new ArrayList<>();
        for (List<CibilDetails> list : grouped.values()) {
            if (list.size() == 1) {
                finalList.add(list.get(0));
                continue;
            }
            CibilDetails latest = list.stream()
                    .max(Comparator.comparing(CibilDetails::getCbDtlId))
                    .orElse(null);
            if (latest == null) {
                continue;
            }
            finalList.add(latest);
            list.stream()
                    .filter(c -> !c.getCbDtlId().equals(latest.getCbDtlId()))
                    .forEach(recordsToDelete::add);
        }
        if (!recordsToDelete.isEmpty()) {
            try {
                cibilDetailsRepository.deleteAllInBatch(recordsToDelete);
            } catch (Exception ex) {
                logger.error("Failed to delete duplicate CIBIL records: {}", recordsToDelete.stream()
                        .map(CibilDetails::getCbDtlId)
                        .collect(Collectors.toList()), ex);
            }
        }
        // Build wrapper
        finalList.forEach(cibilDetail -> {
            CibilDetailsWrapper wrapper = CibilDetailsWrapper.builder()
                    .cibilDetails(cibilDetail)
                    .build();
            cibilDetailsWrapper.add(wrapper);
        });
        customerDataFields.setCibilDetailsWrapperList(cibilDetailsWrapper);
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "downloadApplicationFallback")
    public Response downloadApplication(FetchDeleteUserRequest fetchDeleteUserRequest) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        String applicationId = fetchDeleteUserRequest.getRequestObj().getApplicationId();
        String appId = fetchDeleteUserRequest.getRequestObj().getAppId();
        int versionNum = fetchDeleteUserRequest.getRequestObj().getVersionNum();
        List<String> statusList = new ArrayList<>();
        statusList.add(AppStatus.INPROGRESS.getValue());
        statusList.add(AppStatus.PENDING.getValue());
        statusList.add(AppStatus.APPROVED.getValue());
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(appId, applicationId, versionNum,
                        statusList);
        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            CustomerDataFields customerDataFields = getCustomerData(applicationMasterData, applicationId, appId,
                    versionNum);
            try {
                response = report.genratePdfService(customerDataFields);
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

    public void deleteApplication(String applicationId, String appId) {
        applicationMasterRepository.deleteByApplicationIdAndAppId(applicationId, appId);
        nomineeDetailsRepository.deleteByApplicationIdAndAppId(applicationId, appId);
        addressDetailsRepository.deleteByApplicationIdAndAppId(applicationId, appId);
        occupationDetailsRepository.deleteByApplicationIdAndAppId(applicationId, appId);
        applicationDocumentsRepository.deleteByApplicationIdAndAppId(applicationId, appId);
        customerDetailsRepository.deleteByApplicationIdAndAppId(applicationId, appId);
        bankingFacilitiesRepository.deleteByApplicationIdAndAppId(applicationId, appId);
        crsDtlsrepository.deleteByApplicationIdAndAppId(applicationId, appId);
        fatcaDtlsrepository.deleteByApplicationIdAndAppId(applicationId, appId);
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchCountriesFallback")
    public Response fetchCountries(Request request) {
        Gson gson = new Gson();
        Response fetchCountriesResponse = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        fetchCountriesResponse.setResponseHeader(responseHeader);
        Iterable<Countries> countriesList = countriesRepository.findByAppId(request.getAppId());
        String response = gson.toJson(countriesList);
        responseBody.setResponseObj(response);
        fetchCountriesResponse.setResponseBody(responseBody);
        return fetchCountriesResponse;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchStatesFallback")
    public Response fetchStates(Request request) {
        Gson gson = new Gson();
        Response fetchStatesResponse = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        fetchStatesResponse.setResponseHeader(responseHeader);
        Iterable<States> statesList = statesRepository.findByAppId(request.getAppId());
        String response = gson.toJson(statesList);
        responseBody.setResponseObj(response);
        fetchStatesResponse.setResponseBody(responseBody);
        return fetchStatesResponse;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchCitiesFallback")
    public Response fetchCities(FetchCitiesRequest fetchCitiesRequest) {
        FetchCitiesRequestFields requestFields = fetchCitiesRequest.getRequestObj();
        Gson gson = new Gson();
        Response fetchCitiesResponse = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        fetchCitiesResponse.setResponseHeader(responseHeader);
        Iterable<Cities> cityList = citiesRepository.findByStateCodeAndAppId(requestFields.getStateCode(),
                fetchCitiesRequest.getAppId());
        String response = gson.toJson(cityList);
        responseBody.setResponseObj(response);
        fetchCitiesResponse.setResponseBody(responseBody);
        return fetchCitiesResponse;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "deleteNomineeFallback")
    public Response deleteNominee(DeleteNomineeRequest deleteNomineeRequest) {
        String deleteRule;
        Response deleteNomineeResponse = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        deleteNomineeResponse.setResponseHeader(responseHeader);
        DeleteNomineeRequestFields requestFields = deleteNomineeRequest.getRequestObj();
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestFields.getAppId(),
                        requestFields.getApplicationId(), requestFields.getVersionNum(),
                        AppStatus.INPROGRESS.getValue());
        if (applicationMasterOpt.isPresent()) {
            if (requestFields.getNomineeDtlsId() != null) {
                Optional<NomineeDetails> nomineeDtl = nomineeDetailsRepository
                        .findById(requestFields.getNomineeDtlsId());
                if (nomineeDtl.isPresent()) {
                    NomineeDetails nomineeObj = nomineeDtl.get();
                    Properties prop = null;
                    try {
                        prop = CommonUtils.readPropertyFile();
                    } catch (IOException e) {
                        logger.error("Error while reading property file in deleteNominee ", e);
                        deleteNomineeResponse = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
                                ResponseCodes.FAILURE.getKey());
                    }
                    if (null != prop) {
                        deleteRule = prop.getProperty(CobFlagsProperties.CASA_DELETE_RULE.getKey());
                        if (Constants.HARD_DELETE.equalsIgnoreCase(deleteRule)) {
                            nomineeDetailsRepository.deleteById(requestFields.getNomineeDtlsId());
                            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                            responseBody.setResponseObj(nomineeDelMsg);
                        } else if (Constants.MOVE_TO_HISTORY_TABLES.equalsIgnoreCase(deleteRule)) {
                            NomineeDetailsHistory nomineehistory = new NomineeDetailsHistory();
                            BeanUtils.copyProperties(nomineeObj, nomineehistory);
                            nomineeDetailsHisRepository.save(nomineehistory);
                            nomineeDetailsRepository.deleteById(requestFields.getNomineeDtlsId());
                            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                            responseBody.setResponseObj(nomineeDelMsg);
                        } else if (Constants.UPDATE_STATUS.equalsIgnoreCase(deleteRule)) {
                            nomineeObj.setStatus(AppStatus.INACTIVESTATUS.getValue());
                            nomineeDetailsRepository.save(nomineeObj);
                            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                            responseBody.setResponseObj(nomineeDelMsg);
                        } else {
                            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                            responseBody.setResponseObj("Delete Rule for accounts not set");
                        }
                    }
                }
            } else {
                responseHeader.setResponseCode(ResponseCodes.INVALID_NOMINEE.getKey());
                responseBody.setResponseObj(ResponseCodes.INVALID_NOMINEE.getValue());
            }
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            responseBody.setResponseObj(ResponseCodes.INVALID_APP_MASTER.getValue());
        }
        deleteNomineeResponse.setResponseBody(responseBody);
        return deleteNomineeResponse;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "discardApplicationFallback")
    public boolean discardApplication(CreateModifyUserRequest createModifyUserRequest) {
        String deleteRule;
        CustomerDataFields requestFields = createModifyUserRequest.getRequestObj();
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestFields.getAppId(),
                        requestFields.getApplicationId(), requestFields.getVersionNum(),
                        AppStatus.INPROGRESS.getValue());
        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster masterObjDb = applicationMasterOpt.get();
            Properties prop = null;
            try {
                prop = CommonUtils.readPropertyFile();
            } catch (IOException e) {
                logger.error("Error while reading property file in discardApplication ", e);
                return false;
            }
            if (null != prop) {
                deleteRule = prop.getProperty(CobFlagsProperties.CASA_DELETE_RULE.getKey());
                if (Constants.HARD_DELETE.equalsIgnoreCase(deleteRule)) {
                    deleteApplication(requestFields.getApplicationId(), requestFields.getAppId());
                } else if (Constants.MOVE_TO_HISTORY_TABLES.equalsIgnoreCase(deleteRule)) {
                    populateHistoryTables(requestFields.getApplicationId(), requestFields.getAppId());
                } else if (Constants.UPDATE_STATUS.equalsIgnoreCase(deleteRule)) {
                    masterObjDb.setApplicationStatus(AppStatus.DELETED.getValue());
                    applicationMasterRepository.save(masterObjDb);
                } else {
                    return false;
                }
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "deleteDocumentFallback")
    public Response deleteDocument(DeleteDocumentRequest deleteDocumentRequest) {
        String deleteRule;
        String res = "";
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        DeleteDocumentRequestFields requestFields = deleteDocumentRequest.getRequestObj();
        String bulkDelete = requestFields.getBulkDelete() == null ? "" : requestFields.getBulkDelete();
        Gson gson = new Gson();
        List<String> statusList = new ArrayList<>();
        List<ApplicationDocuments> docs = new ArrayList<>();
        logger.debug("Delete Document Request Body : " + requestFields.toString());
        for (AppStatus status : AppStatus.values()) {
            statusList.add(status.getValue());
        }
        Properties prop = null;
        try {
            prop = CommonUtils.readPropertyFile();
        } catch (IOException e) {
            logger.error("Error while reading property file in deleteDocument ", e);
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }
        String uploadLocation = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey());

        logger.debug("Attempting to delete document(s) for applicationId: {}, appId: {}, versionNum: {}, documentType: {}, bulkDelete: {}",
                requestFields.getApplicationId(), requestFields.getAppId(), requestFields.getVersionNum(), requestFields.getDocumentType(), bulkDelete);

        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(requestFields.getAppId(),
                        requestFields.getApplicationId(), requestFields.getVersionNum(), statusList);
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        if (applicationMasterOpt.isPresent()) {
            if (bulkDelete.equalsIgnoreCase(Constants.YES)) {
                String filePath = "/" + requestFields.getAppId() + "/" + Constants.LOAN + "/" + requestFields.getApplicationId() + "/";
                if (!StringUtils.isEmpty(requestFields.getIncomeType())) {
                    List<ApplicationDocuments> applicationDocumentsOpt = applicationDocumentsRepository
                            .findByApplicationIdAndAppId(requestFields.getApplicationId()
                                    , requestFields.getAppId());
                    if (!applicationDocumentsOpt.isEmpty()) {
                        for (ApplicationDocuments appDocs : applicationDocumentsOpt) {
                            ApplicationDocumentsPayload documentsPayload = gson.fromJson(appDocs.getPayloadColumn(), ApplicationDocumentsPayload.class);
                            logger.debug("documents payload: {}", documentsPayload);
                            if (null != documentsPayload.getIncomeType()
                                    && documentsPayload.getIncomeType().equalsIgnoreCase(requestFields.getIncomeType())) {
                                Path docPath = Paths.get(uploadLocation + filePath + documentsPayload.getDocumentFileName());
                                try {
                                    Boolean isFileDeleted = Files.deleteIfExists(docPath);
                                    if (isFileDeleted) {
                                        applicationDocumentsRepository.delete(appDocs);
                                    }
                                } catch (Exception e) {
                                    logger.error("Error while deleting file in deleteDocument for file: {} ", docPath, e);
                                    return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
                                            ResponseCodes.FAILURE.getKey());
                                }
                                res = Constants.SUCCESS;
                            }
                        }
                    }
                } else {
                    String customerType = requestFields.getCustomerType().equalsIgnoreCase(Constants.BOTH) ?
                            Constants.APPLICANT : requestFields.getCustomerType();
                    Optional<List<ApplicationDocuments>> applicationDocumentListOpt = applicationDocumentsRepository
                            .findByApplicationIdAndCustType(customerType, requestFields.getApplicationId());
                    if (applicationDocumentListOpt.isPresent()) {
                        List<ApplicationDocuments> applicationDocumentList = applicationDocumentListOpt.get();
                        logger.debug("Found {} documents for bulk delete.", applicationDocumentList.size());
                        if (!applicationDocumentList.isEmpty()) {
                            for (ApplicationDocuments applicationDocuments : applicationDocumentList) {
                                String payloadColumn = applicationDocuments.getPayloadColumn();
                                ApplicationDocumentsPayload docPayload = gson.fromJson(payloadColumn, ApplicationDocumentsPayload.class);
                                if (docPayload.getDocumentType().equalsIgnoreCase(requestFields.getDocumentType())) {
                                    if (!StringUtils.isEmpty(docPayload.getDocNo())
                                            && docPayload.getDocNo().equalsIgnoreCase(requestFields.getDocNo())) {
                                        Path docPath = Paths.get(uploadLocation + filePath + docPayload.getDocumentFileName());
                                        logger.info("Deleting file: {}", docPath);
                                        try {
                                            boolean isfileDeleted = Files.deleteIfExists(docPath);
                                            if (isfileDeleted) {
                                                applicationDocumentsRepository.delete(applicationDocuments);
                                            }

                                            logger.info("File deleted successfully: {}", docPath);
                                        } catch (IOException e) {
                                            logger.error("Error while deleting file in deleteDocument for file: {} ", docPath, e);
                                            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
                                                    ResponseCodes.FAILURE.getKey());
                                        }
                                    }
                                    res = Constants.SUCCESS;
                                }
                            }
                        }
                    } else {
                        logger.warn("No documents found for bulk delete for applicationId: {}, appId: {}", requestFields.getApplicationId(), requestFields.getAppId());
                    }
                }
            } else {
                logger.debug("Deleting a single file");
                if (null != uploadLocation && !"".equalsIgnoreCase(uploadLocation)) {
                    logger.debug("upload location is present");
                    Path path = Paths.get(uploadLocation + requestFields.getFilePath() + requestFields.getFileName());
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        logger.error("Error while deleting file in deleteDocument ", e);
                        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
                                ResponseCodes.FAILURE.getKey());
                    }
                    res = Constants.SUCCESS;
                } else {
                    responseHeader.setResponseCode(ResponseCodes.PATH_NOT_CONFIGURED.getKey());
                }
                if (requestFields.getAppDocId() != null) {
                    Optional<ApplicationDocuments> docDtl = applicationDocumentsRepository
                            .findById(requestFields.getAppDocId());
                    if (docDtl.isPresent()) {
                        ApplicationDocuments docObj = docDtl.get();
                        deleteRule = prop.getProperty(CobFlagsProperties.CASA_DELETE_RULE.getKey());
                        logger.debug("Delete Rule :" + deleteRule);
                        if (Constants.HARD_DELETE.equalsIgnoreCase(deleteRule)) {
                            applicationDocumentsRepository.deleteById(requestFields.getAppDocId());
                        } else if (Constants.MOVE_TO_HISTORY_TABLES.equalsIgnoreCase(deleteRule)) {
                            ApplicationDocumentsHistory documentHistory = new ApplicationDocumentsHistory();
                            BeanUtils.copyProperties(docObj, documentHistory);
                            applicationDocumentsHisRepository.save(documentHistory);
                            applicationDocumentsRepository.deleteById(requestFields.getAppDocId());
                        } else if (Constants.UPDATE_STATUS.equalsIgnoreCase(deleteRule)) {
                            docObj.setStatus(AppStatus.INACTIVESTATUS.getValue());
                            applicationDocumentsRepository.save(docObj);
                        } else {
                            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                            res = "Delete rule not set";
                        }
                    } else {
                        logger.debug("No Documents found found for delete operation");
                    }
                }
            }
            if( Constants.PRIMARY_KYC.equalsIgnoreCase(requestFields.getDocumentType())){
                Optional<CustomerDetails> custDet = customerDetailsRepository.findByApplicationIdAndAppIdAndCustomerType(requestFields.getApplicationId()
                        , requestFields.getAppId(), requestFields.getCustomerType());
                logger.debug("Customer Type : {}", requestFields.getCustomerType());
                if(custDet.isPresent()) {
                    logger.debug("Customer Details of {} found",requestFields.getCustomerType());
                    CustomerDetails customerDet = custDet.get();
                    CustomerDetailsPayload custDetPayload = gson.fromJson(customerDet.getPayloadColumn(), CustomerDetailsPayload.class) ;
                    if(custDetPayload!= null) {
                        if ((!Constants.APPLICANT.equalsIgnoreCase(requestFields.getCustomerType()))
                                && Constants.VERIFIED_STS.equalsIgnoreCase(custDetPayload.getPrimaryKycIdValStatus())) {
                            custDetPayload.setPrimaryKycId("");
                        }
                        custDetPayload.setPrimaryKycIdValStatus("Pending");
                        custDetPayload.setAlternateVoterIdValStatus("Pending");
                        custDetPayload.setAlternateVoterId("");
                        customerDet.setPayloadColumn(gson.toJson(custDetPayload));
                        customerDetailsRepository.save(customerDet);
                    }
                }
            }
            if (!(requestFields.getBotTrigger() == null)) {
                logger.debug("Entered Bot trigger modifying block");
                Optional<List<ApplicationDocuments>> applicationDocumentsOptional = applicationDocumentsRepository.applicationDocsRecords(requestFields.getCustomerType(), requestFields.getApplicationId(), requestFields.getDocumentType());
                if (applicationDocumentsOptional.isPresent()) {
                    logger.debug("applicationDocumentsOptional.isPresent()");
                    List<ApplicationDocuments> applicationDocumentsList = applicationDocumentsOptional.get();
                    logger.debug("applicationDocumentsList.size()" + applicationDocumentsList.size());
                    for (ApplicationDocuments applicationDocuments : applicationDocumentsList) {
                        logger.debug("ApplicationDocument Record :" + applicationDocuments);
                        ApplicationDocumentsPayload applicationDocumentsPayload = null;
                        try {
                            applicationDocumentsPayload = new ObjectMapper().readValue(applicationDocuments.getPayloadColumn(), ApplicationDocumentsPayload.class);
                            logger.debug("applicationDocumentsPayload" + applicationDocumentsPayload);
                        } catch (JsonProcessingException e) {
                            logger.error("error while JsonProcessing " + e);
                            throw new RuntimeException(e);
                        }
                        applicationDocumentsPayload.setBotTrigger("N");
                        logger.debug("Updated the Bot Trigger property, Bot Trigger :" + applicationDocumentsPayload.getBotTrigger());
                        applicationDocuments.setPayloadColumn(new Gson().toJson(applicationDocumentsPayload));
                        applicationDocumentsRepository.save(applicationDocuments);
                        logger.debug("Successfully updated the application documents table");
                    }
                } else {
                    logger.debug("No record found in ApplicationDocuments table");
                }
            }
            logger.debug("Bot Trigger node is modified");

            Optional<ApplicationMaster> appMasterDb = applicationMasterRepository.findByAppIdAndApplicationIdAndVersionNum(
                    requestFields.getAppId(), requestFields.getApplicationId(), requestFields.getVersionNum());

            if (appMasterDb.isPresent()) {
                logger.debug("appMasterDb data found Id: {}", requestFields.getApplicationId());
                List<ApplicationDocuments> documentRecords = applicationDocumentsRepository
                        .findByApplicationIdAndAppId(requestFields.getApplicationId(), requestFields.getAppId());
                logger.debug("documentRecordsOpt value: {}", documentRecords);

                //1
                Gson gson1 = new Gson();
                if (!documentRecords.isEmpty()) {
                    logger.debug("documentRecords present: {}", documentRecords);
                    //2
                    if (!documentRecords.isEmpty()) {
                        docs = documentRecords;
                        for(ApplicationDocuments doc : docs) {
                            if(doc.getPayloadColumn() != null) {
                                doc.setPayload(gson.fromJson(doc.getPayloadColumn(), ApplicationDocumentsPayload.class));
                            }
                        }
                        logger.debug("Application Documents :" + docs);
                    } else {
                        logger.debug("No document records found for applicationId: {}", requestFields.getApplicationId());
                    }
                }
            } else {
                logger.debug("!appMasterDb.isPresent()");
            }
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            res = "Invalid application ID.";

        }
        responseBody.setResponseObj(res);
        responseBody.setApplicationDocuments(docs);
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "downloadReportFallback")
    public Response downloadReport(DownloadReportRequest downloadReportRequest, boolean isSmsReport) {

        Header header = new Header();
        header.setAppId("APZCOB");
        header.setDeviceId("abcd1234efgh5678");
        header.setInterfaceId("RepaymentSchedule");
        header.setMasterTxnRefNo("12345678");
        header.setUserId("000000000002");

        Properties prop = null;
        try {
            prop = CommonUtils.readPropertyFile();
            logger.debug("Property file read successfully");
        } catch (IOException e) {
            logger.error("Error while reading property file in approveRejectApplication ", e);
        }

        //
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        Gson gson = new Gson();
        JSONObject contentKeys;

        DownloadReportRequestFields requestFields = downloadReportRequest.getRequestObj();
        String reportType = requestFields.getReportType();
        String applicationId = requestFields.getApplicationId();
        String appId = requestFields.getAppId();
        int versionNum = Constants.INITIAL_VERSION_NO;

        logger.debug("reportType:" + reportType);
        logger.debug("applicationId:" + applicationId);

        // sactionedDateStr
        List<String> statusList = new ArrayList<>();
        statusList.add(WorkflowStatus.SANCTIONED.getValue());
        List<ApplicationWorkflow> wfList = applnWfRepository
                .findByApplicationIdAndApplicationStatusInOrderByCreateTsDesc(applicationId,
                        statusList);

        String sactionedDateStr = "";
        if (!wfList.isEmpty()) {
            LocalDateTime sactionedDate = wfList.get(0).getCreateTs();
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                sactionedDateStr = sactionedDate.format(formatter);
            } catch (Exception e) {
                logger.error("error while conversion of Date :" + e);
            }
        }

        CustomerDataFields custmrDataFields = null;
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, versionNum);

        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            String product = applicationMasterData.getProductCode();
            logger.debug("product :" + product);
            custmrDataFields = getCustomerData(applicationMasterData, applicationId, appId, versionNum);

            Optional<BCMPIStageVerification> bcmpiStageData = bcmpiStageVerificationRepository.findById(applicationId);
            if (bcmpiStageData.isPresent()) {
                logger.debug("bcmpiStageData found");
                custmrDataFields.setBcmpiStatDetails(
                        CommonUtils.parseBCMPIStageVerificationData(bcmpiStageData.get().getEditedFields(), bcmpiStageData.get().getQueries()));
                custmrDataFields.setBcmpiVerifiedStage(null != bcmpiStageData.get().getVerifiedStages()
                        ? Arrays.asList(bcmpiStageData.get().getVerifiedStages().split("\\|")) : new ArrayList<>());
            }
            Optional<BCMPIIncomeDetails> bcmpiIncomeDataOpt = bcmpiIncomeDetailsRepo.findById(applicationId);
            if (bcmpiIncomeDataOpt.isPresent()) {
                logger.debug("bcmpiIncomeData found");
                BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper = gson.fromJson(bcmpiIncomeDataOpt.get().getPayload(), BCMPIIncomeDetailsWrapper.class); // Object to be changed to a wrapper class
                BCMPIIncomeDetails bcmpiIncomeDetails = bcmpiIncomeDataOpt.get();
                bcmpiIncomeDetails.setBcmpiIncomeDetailsWrapper(bcmpiIncomeDetailsWrapper);
                custmrDataFields.setBcmpiIncomeDetails(bcmpiIncomeDetails);
            }
            Optional<BCMPILoanObligations> bcmpiLoanObligationsOpt = bcmpiLoanObligationsRepo.findById(applicationId);
            if (bcmpiLoanObligationsOpt.isPresent()) {
                logger.debug("bcmpiLoanObligations found");
                LoanObligationsWrapper loanObligationsWrapper = gson.fromJson(bcmpiLoanObligationsOpt.get().getPayload(), LoanObligationsWrapper.class);
                BCMPILoanObligations bcmpiLoanObligations = bcmpiLoanObligationsOpt.get();
                bcmpiLoanObligations.setLoanObligationsWrapper(loanObligationsWrapper);
                custmrDataFields.setBcmpiLoanObligations(bcmpiLoanObligations);
            }
            Optional<BCMPIOtherDetails> bcmpiOtherDetailsOpt = bcmpiOtherDetailsRepo.findById(applicationId);
            if (bcmpiOtherDetailsOpt.isPresent()) {
                logger.debug("bcmpiIncomeData found");
                BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = gson.fromJson(bcmpiOtherDetailsOpt.get().getPayload(), BCMPIOtherDetailsWrapper.class);
                BCMPIOtherDetails bcmpiOtherDetails = bcmpiOtherDetailsOpt.get();
                bcmpiOtherDetails.setBcmpiOtherDetailsWrapper(bcmpiOtherDetailsWrapper);
                custmrDataFields.setBcmpiOtherDetails(bcmpiOtherDetails);
            }
            List<DeviationRATracker> deviationRecords = deviationRATrackerRepository
                    .findByApplicationId(custmrDataFields.getApplicationId());
            logger.debug("Size of deviationRecords" + deviationRecords.isEmpty());
            Optional<SanctionMaster> sanctionAuthority = sanctionMasterRepositoy.findByProductAndValueBetween(product,
                    custmrDataFields.getLoanDetails().getLoanAmount());
            Optional<ProductDetails> productName = productDetailsrepository.findById(product);
            String productDetail = "";
            if (productName.isPresent()) {
                logger.debug("Loan product Name:" + productName.get().getProductName());
                productDetail = productName.get().getProductName();
            }
            LoanDetailsPayload payload = gson.fromJson(custmrDataFields.getLoanDetails().getPayloadColumn(),
                    LoanDetailsPayload.class);

            String reportLanguages = prop.getProperty(CobFlagsProperties.REPORT_LANGUAGES.getKey());
            String[] reportLanguageArr = reportLanguages.split(",");
            String inputLanguage = payload.getLanguage();
            logger.debug("inputLanguage" + inputLanguage);
            boolean isValidLanguage = Arrays.stream(reportLanguageArr)
                    .anyMatch(lang -> lang.equalsIgnoreCase(inputLanguage));
            String language = isValidLanguage ? inputLanguage : Constants.DEFAULTLANGUAGE;
            logger.debug("final Language:" + language);


            logger.debug("Loan Amount for sanction Approval :" + custmrDataFields.getLoanDetails().getLoanAmount());
            logger.debug("custmrDataFields.toString() : " + custmrDataFields.toString());
            logger.debug("Size of sanctionAuthority" + sanctionAuthority.isPresent());

            List<ApplicationWorkflow> workflow;
            workflow = applnWfRepository.findByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumAsc(appId,
                    applicationId, versionNum);
            logger.debug("Workflow details {} ", workflow);

            if (!workflow.isEmpty()) {
                custmrDataFields.setApplicationWorkflowList(workflow);
            }

            try {

                if (reportType.equalsIgnoreCase("CreditAssessmentReport")) {
                    contentKeys = getFileContent(Constants.REPORT_JSONKEYS + reportType + Constants.JSON_EXT, Constants.LOANAPPLICATION);
                    response = new CreditAssessment().generatePdf(requestFields, contentKeys);
                } else if (reportType.equalsIgnoreCase("CamSheetReport")) {
                    contentKeys = getFileContent(Constants.REPORT_JSONKEYS + reportType + Constants.JSON_EXT,
                            Constants.LOANAPPLICATION);

                    String gkId = custmrDataFields.getApplicationMaster().getUpdatedBy();
                    Optional<String> gkUsernameOpt = tbUserRepository.findUserNameByUserId(gkId);
                    String gkUserName = gkUsernameOpt.orElse("-");
                    logger.info("usernameKM : " + gkUserName);

                    response = new CamReport().generatePdf(custmrDataFields, contentKeys, deviationRecords,
                            sanctionAuthority, productDetail, gkId, gkUserName);
                } else if (reportType.equalsIgnoreCase("LoanApplicationReport")) {
                    response = downloadLoanApplication(downloadReportRequest);
                } else if (reportType.equalsIgnoreCase("KfsSheetReport")) {
                    logger.debug("reportType-KfsSheetReport");
                    String sanctionDateStr = "";
                    List<RepaymentSchedule> repaymentList = new ArrayList<>();
                    try {
                        Optional<ApiExecutionLog> apiExecutionRecordOpt = apiExecutionLogRepository.findTopByApplicationIdAndApiNameOrderByCreateTsDesc(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE);
                        if (apiExecutionRecordOpt.isPresent()) {
                            ApiExecutionLog apiExecutionRecord = apiExecutionRecordOpt.get();
                            if (apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS) || apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS_DMS)) {

                                ///need to change
                                LocalDateTime sanctionDate = apiExecutionRecord.getCreateTs();
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                                sanctionDateStr = sanctionDate.format(formatter);

                                String apiRespJsonString = apiExecutionRecord.getResponsePayload();
                                logger.debug("apiRespJsonString " + apiRespJsonString);
                                Gson gson1 = new Gson();

                                // Parse the string into a JsonObject
                                JsonObject root = gson1.fromJson(apiRespJsonString, JsonObject.class);

                                // Extract the "body" array
                                JsonArray bodyArray = root.getAsJsonArray("body");

                                // Convert to list of RepaymentSchedule
                                Type listType = new TypeToken<List<RepaymentSchedule>>() {
                                }.getType();
                                repaymentList = gson1.fromJson(bodyArray, listType);
                                logger.debug("repaymentList size." + repaymentList.size());
                                logger.debug(" repaymentList size str." + repaymentList.toString());
                            } else {
                                logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR);
                                throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR);
                            }
                        } else {
                            logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                            throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                        }
                    } catch (Exception e) {
                        logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR2 + e);
                        throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR2, e);
                    }

                    contentKeys = getFileContent(Constants.REPORT_JSONKEYS + language + reportType + Constants.JSON_EXT, Constants.KFS);
                    response = new KfsReport().generatePdf(contentKeys, custmrDataFields, productDetail, repaymentList,sanctionDateStr,language, isSmsReport);
                } else if (reportType.equalsIgnoreCase("SanctionLetter")) {
                    logger.debug("reportType-SanctionLetter");

                    List<RepaymentSchedule> repaymentList = new ArrayList<>();
                    try {
                        Optional<ApiExecutionLog> apiExecutionRecordOpt = apiExecutionLogRepository.findTopByApplicationIdAndApiNameOrderByCreateTsDesc(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE);
                        if (apiExecutionRecordOpt.isPresent()) {
                            ApiExecutionLog apiExecutionRecord = apiExecutionRecordOpt.get();
                            if (apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS) || apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS_DMS)) {
                                String apiRespJsonString = apiExecutionRecord.getResponsePayload();
                                logger.debug("apiRespJsonString " + apiRespJsonString);
                                Gson gson1 = new Gson();

                                // Parse the string into a JsonObject
                                JsonObject root = gson1.fromJson(apiRespJsonString, JsonObject.class);

                                // Extract the "body" array
                                JsonArray bodyArray = root.getAsJsonArray("body");

                                // Convert to list of RepaymentSchedule
                                Type listType = new TypeToken<List<RepaymentSchedule>>() {
                                }.getType();
                                repaymentList = gson1.fromJson(bodyArray, listType);
                                logger.debug("repaymentList size." + repaymentList.size());
                            } else {
                                logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR);
                                throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR);
                            }
                        } else {
                            logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                            throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                        }
                    } catch (Exception e) {
                        logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR2 + e);
                        throw new IOException("Error fetching ApiExecutionLog - SanctionLetter data", e);
                    }


                    // @author Abhishek.Raj.CAGL
                    if (Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
                            .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())){
                        //jsonKeysForHindiADD.json
                        contentKeys = getFileContent(Constants.REPORT_JSONKEYS + language  + "SanctionLetterSupAdd" + Constants.JSON_EXT,
                                Constants.SANCTIONLETTER);
                    }
                    else if (Constants.FAMILY_WELFARE_PRODUCT_CODE
                            .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode()) ||
                            Constants.UNNATI_EMERGENCY_PRODUCT_CODE
                                    .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())){
                        //jsonKeysForHindiADD.json
                        contentKeys = getFileContent(Constants.REPORT_JSONKEYS + language  + "SanctionLetterFWLAdd" + Constants.JSON_EXT,
                                Constants.SANCTIONLETTER);
                    }else{
                        //jsonKeysForHindiReport.json
                    contentKeys = getFileContent(Constants.REPORT_JSONKEYS + language + reportType + Constants.JSON_EXT,
                            Constants.SANCTIONLETTER);
                    }


                    String bmId = "-";
                    if (Constants.UNNATI_PRODUCT_CODE
                            .equals(custmrDataFields.getApplicationMaster().getProductCode())
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
                                    }
                                }
                                break;
                            }
                        }

                    } else {
                        logger.info("Not an Unnati/Renewal application");
                    }

                    Optional<String> usernameOptBM = tbUserRepository.findUserNameByUserId(bmId);
                    String usernameBM = usernameOptBM.orElse("-");
                    logger.info("BM name" + usernameBM);

                    // @author Abhishek.Raj.CAGL

                    String bmIdFromCDH = "-";


                    List<ApplicationTimelineDtl> timelineList = custmrDataFields.getApplicationTimelineDtl();

                    if (timelineList != null && !timelineList.isEmpty()) {
                        for (ApplicationTimelineDtl dtl : timelineList) {
                            if (dtl != null && Constants.SANCTION.equalsIgnoreCase(dtl.getStage())) {
                                bmIdFromCDH = dtl.getUserId() == null ? "-" : dtl.getUserId();
                            }
                        }
                        logger.debug("bmIdFromCDH: {}", bmIdFromCDH);
                    } else {
                        logger.debug("ApplicationTimelineDtl is empty for applicationId: {}", applicationId);
                    }



                    String actualUserId;

                    if (bmIdFromCDH != null && bmIdFromCDH.contains("(")) {
//                        actualUserId = bmIdFromCDH.substring(0, bmIdFromCDH.indexOf("("));
                        actualUserId = bmIdFromCDH.substring(
                                bmIdFromCDH.indexOf("(") + 1,
                                bmIdFromCDH.indexOf(")")
                        );
                    } else {
                        actualUserId = bmIdFromCDH;
                    }
                    Optional<String> usernameOptBMForAddProdct = tbUserRepository.findUserNameByUserId(actualUserId);
                    String usernameBMForAddProdct = usernameOptBMForAddProdct.orElse("-");
                    logger.info("usernameBMForAddProdct : " + usernameBMForAddProdct);
					if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
                            .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())
                            || Constants.FAMILY_WELFARE_PRODUCT_CODE
                            .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode()) ||
                            Constants.UNNATI_EMERGENCY_PRODUCT_CODE
                                    .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())
					){
                        response = new SanctionLetter().generatePdfAdd(contentKeys, custmrDataFields, sactionedDateStr, repaymentList, bmIdFromCDH, usernameBMForAddProdct, language, productDetail, isSmsReport);
					}else{
                        response = new SanctionLetter().generatePdf(contentKeys, custmrDataFields, sactionedDateStr, repaymentList, bmId, usernameBM, language, productDetail, isSmsReport, bmIdFromCDH, usernameBMForAddProdct);
					}
//                    response = new SanctionLetter().generatePdf(contentKeys, custmrDataFields, sactionedDateStr, repaymentList, bmId, usernameBM, language, productDetail, isSmsReport);
                    // @author Abhishek.Raj.CAGL end
                } else if (reportType.equalsIgnoreCase("WelcomeLetter")) {
                    logger.debug("reportType-WelcomeLetter");
                    contentKeys = getFileContent(Constants.REPORT_JSONKEYS + language + reportType + Constants.JSON_EXT, Constants.WELCOMELETTER);

                    List<RepaymentScheduleDisbursed> repaymentList = new ArrayList<>();
                    String disburseDateStr = "";
                    String lonAmountDisbursed = "0";

                    try {
                        Optional<ApiExecutionLog> apiExecutionRecordOpt = apiExecutionLogRepository
                                .findTopByApplicationIdAndApiNameOrderByCreateTsDesc(applicationId, Constants.DISBURSEMENT_REPAY_SCHEDULE);

                        if (apiExecutionRecordOpt.isPresent()) {
                            ApiExecutionLog apiExecutionRecord = apiExecutionRecordOpt.get();
                            if (apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS) || apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS_DMS)) {
                                String apiRespJsonString = apiExecutionRecord.getResponsePayload();
                                logger.debug("apiRespJsonString " + apiRespJsonString);

                                LocalDateTime createDate = apiExecutionRecord.getCreateTs();
                                try {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                                    disburseDateStr = createDate.format(formatter);
                                } catch (Exception e) {
                                    logger.error("error while conversion of Date :" + e);
                                }

                                Gson gson1 = new Gson();

                                // Parse the string into a JsonObject
                                JsonObject root = gson1.fromJson(apiRespJsonString, JsonObject.class);

                                // Extract the "body" array
                                JsonArray bodyArray = root.getAsJsonArray("body");

                                // Convert to list of RepaymentSchedule
                                Type listType = new TypeToken<List<RepaymentScheduleDisbursed>>() {
                                }.getType();
                                repaymentList = gson1.fromJson(bodyArray, listType);
                                logger.debug("repaymentList size." + repaymentList.size());

                            } else {
                                logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR);
                                throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR);
                            }
                        } else {
                            logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                            throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                        }
                    } catch (Exception e) {
                        logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR2 + e);
                        throw new IOException("Error fetching ApiExecutionLog - SanctionLetter data", e);
                    }

                    try {
                        Optional<ApiExecutionLog> apiExecutionDisburseRecordOpt = apiExecutionLogRepository
                                .findTopByApplicationIdAndApiNameOrderByCreateTsDesc(applicationId, "Loan Disbursement");
                        if (apiExecutionDisburseRecordOpt.isPresent()) {
                            ApiExecutionLog apiExecutionDisburseRecord = apiExecutionDisburseRecordOpt.get();
                            if (apiExecutionDisburseRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS) || apiExecutionDisburseRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS_DMS)) {
                                String disbApiRespJsonString = apiExecutionDisburseRecord.getResponsePayload();
                                logger.debug("disbApiRespJsonString " + disbApiRespJsonString);

//    						LocalDateTime createDate = apiExecutionDisburseRecord.getCreateTs();
//    						try {
//    		                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
//    		                    disburseDateStr = createDate.format(formatter);
//    		                } catch (Exception e) {
//    		                    logger.error("error while conversion of Date :" + e);
//    		                }

                                Gson gson1 = new Gson();
                                JsonObject root = gson1.fromJson(disbApiRespJsonString, JsonObject.class);
                                JsonObject bodyObj = root.getAsJsonObject("body");
                                lonAmountDisbursed = bodyObj.get("amount").getAsString();

                                logger.debug("Amount: " + lonAmountDisbursed);
                            } else {
                                logger.error("Success - Loan Disbursement record not found.");
                                throw new IOException("Loan Disbursement API - Success record not found.");
                            }

                        } else {
                            logger.error("Loan Disbursement record not found.");
                            throw new IOException("Loan Disbursement API - Record not found.");
                        }
                    } catch (Exception e) {
                        logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR2 + e);
                        throw new IOException("Error fetching ApiExecutionLog - WelcomeLetter data", e);
                    }
                    response = new WelcomeLetter().generatePdf(contentKeys, custmrDataFields, sactionedDateStr, repaymentList, disburseDateStr, lonAmountDisbursed);
                } else if (reportType.equalsIgnoreCase("RepaymentSchedule")) {
                    logger.debug("reportType-RepaymentSchedule");
                    contentKeys = getFileContent(Constants.REPORT_JSONKEYS + language + reportType + Constants.JSON_EXT, Constants.REPAYMENTSCHEDULE);

                    String disburseDateStr = "";
                    List<RepaymentScheduleDisbursed> repaymentList = new ArrayList<>();
                    try {
                        Optional<ApiExecutionLog> apiExecutionRecordOpt = apiExecutionLogRepository
                                .findTopByApplicationIdAndApiNameOrderByCreateTsDesc(applicationId, Constants.DISBURSEMENT_REPAY_SCHEDULE);
                        if (apiExecutionRecordOpt.isPresent()) {
                            ApiExecutionLog apiExecutionRecord = apiExecutionRecordOpt.get();
                            if (apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS) || apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS_DMS)) {
                                String apiRespJsonString = apiExecutionRecord.getResponsePayload();
                                logger.debug("apiRespJsonString " + apiRespJsonString);

                                LocalDateTime createDate = apiExecutionRecord.getCreateTs();
                                try {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                                    disburseDateStr = createDate.format(formatter);
                                } catch (Exception e) {
                                    logger.error("error while conversion of Date :" + e);
                                }

                                Gson gson1 = new Gson();

                                // Parse the string into a JsonObject
                                JsonObject root = gson1.fromJson(apiRespJsonString, JsonObject.class);

                                // Extract the "body" array
                                JsonArray bodyArray = root.getAsJsonArray("body");

                                // Convert to list of RepaymentSchedule
                                Type listType = new TypeToken<List<RepaymentScheduleDisbursed>>() {
                                }.getType();
                                repaymentList = gson1.fromJson(bodyArray, listType);
                                logger.debug("repaymentList size." + repaymentList.size());
                                logger.debug("repaymentList size." + repaymentList.toString());
                            } else {
                                logger.error(Constants.DISBURSE_REPAY_SCHEDULE_ERR2);
                                throw new IOException(Constants.DISBURSE_REPAY_SCHEDULE_ERR1);
                            }
                        } else {
                            logger.error(Constants.DISBURSE_REPAY_SCHEDULE_ERR2);
                            throw new IOException(Constants.DISBURSE_REPAY_SCHEDULE_ERR2);
                        }
                    } catch (Exception e) {
                        logger.error("error while fetcthing ApiExecutionLog - RepaymentSchedule" + e);
                        response = getFailureJson(e.getMessage());
                    }

                    response = new RepaymentScheduleTemplate().generatePdf(contentKeys, custmrDataFields, repaymentList, sactionedDateStr, productDetail, disburseDateStr);
                } else {
                    logger.warn("Unsupported report type: " + reportType);
                    response = getFailureJson("Unsupported report type: " + reportType);
                }

            } catch (DRException | IOException e) {
                logger.error("Error generating PDF report: ", e);
                response = getFailureJson(e.getMessage());
            }
        } else {
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            responseBody.setResponseObj(Constants.APP_MASTER_NOT_FOUND);
            response.setResponseHeader(responseHeader);
            response.setResponseBody(responseBody);
        }
        return response;
    }

    public Optional<String> getBase64IfFileExists(String appId, String applicationId, String reportName) throws IOException {
        Properties prop = null;
        try {
            prop = CommonUtils.readPropertyFile();
        } catch (IOException e) {
            logger.error("Error while reading property file in deleteDocument {}", e);
        }
//        String filePathDest = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId
//                + "/" + reportName.replace("Report", "") + "/" + applicationId + "/";

        String filePathDest = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + "APZCBO" + "/" + Constants.LOAN + "/"
                + applicationId + "/";

        logger.debug("filePathDest :: {}", filePathDest);

        String filePath = filePathDest + applicationId + "_" + reportName + ".pdf";
        // Ensure directory exists
        File file = new File(filePath);
        if (file.exists()) {
            String base64 = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(filePath)));
            return Optional.of(base64);
        } else {
            logger.warn("File not found at path: {}", filePath);
            return Optional.empty();
        }

    }

    public Response downloadLoanApplication(DownloadReportRequest downloadReportRequest) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        String applicationId = downloadReportRequest.getRequestObj().getApplicationId();
        String appId = downloadReportRequest.getRequestObj().getAppId();
        int versionNum = Constants.INITIAL_VERSION_NO;

        CustomerDataFields custmrDataFields = null;
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, versionNum);
        logger.debug("applicationMasterOpt.isPresent()" + applicationMasterOpt.isPresent());
        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            custmrDataFields = getCustomerData(applicationMasterData, applicationId, appId, versionNum);
            logger.debug("custmrDataFields.toString() : " + custmrDataFields.toString());

            Gson gson = new Gson();
            Optional<BCMPIIncomeDetails> bcmpiIncomeDataOpt = bcmpiIncomeDetailsRepo.findById(applicationId);
            if (bcmpiIncomeDataOpt.isPresent()) {
                logger.debug("bcmpiIncomeData found");
                BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper = gson.fromJson(
                        bcmpiIncomeDataOpt.get().getPayload(), BCMPIIncomeDetailsWrapper.class);
                BCMPIIncomeDetails bcmpiIncomeDetails = bcmpiIncomeDataOpt.get();
                bcmpiIncomeDetails.setBcmpiIncomeDetailsWrapper(bcmpiIncomeDetailsWrapper);
                custmrDataFields.setBcmpiIncomeDetails(bcmpiIncomeDetails);
            }

            List<String> docTypes = Arrays.asList("CUSTOMER_PHOTOGRAPH");

            List<ApplicationDocuments> applnDocumentList = applicationDocumentsRepository.fetchDocumentsByApplicationIdAndDocumentTypeList(applicationMasterData.getApplicationId(),docTypes);
            List<ApplicationWorkflow> workflow;
            workflow = applnWfRepository.findByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumAsc(appId,
                    applicationId, versionNum);
            logger.debug("Workflow details {} ", workflow);

            String bmId = "-";
            String kmId = "-";

            String kmSubmDateStr = "";
            String bmSubmDateStr = "";

            if (!workflow.isEmpty()) {
                custmrDataFields.setApplicationWorkflowList(workflow);

                LocalDateTime kmSubmDate = null;
                for (ApplicationWorkflow applnWorkflow : custmrDataFields.getApplicationWorkflowList()) {
                    if (Constants.INITIATOR.equalsIgnoreCase(applnWorkflow.getCurrentRole()) && (WorkflowStatus.APPROVED.getValue().equalsIgnoreCase(
                            applnWorkflow.getApplicationStatus()
                    ) || WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(applnWorkflow.getApplicationStatus()))) {
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

                if (Constants.UNNATI_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.OPENMARKET_LOAN_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())) {
                    logger.info("Unnati application");

                    String previousWorkflowStatus = null;
                    for (ApplicationWorkflow appnWorkflow : custmrDataFields.getApplicationWorkflowList()) {
                        String currentStatus = appnWorkflow.getApplicationStatus();
                        if (Constants.APPROVED.equalsIgnoreCase(appnWorkflow.getApplicationStatus())) {
                            if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(previousWorkflowStatus)) {
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
            }

            String leadInitiationDateStr = "";
            String kmIdFromCDH = "-";


            String bmIdFromCDH = "-";

            Optional<String> usernameOptKM = tbUserRepository.findUserNameByUserId(kmId);
            String usernameKM = usernameOptKM.orElse("");
            logger.info("usernameKM : " + usernameKM);

            Optional<String> usernameOptBM = tbUserRepository.findUserNameByUserId(bmId);
            String usernameBM = usernameOptBM.orElse("");
            logger.info("usernameBM : " + usernameBM);




            Optional<String> usernameOptKMFromCDH = tbUserRepository.findUserNameByUserId(kmIdFromCDH);
            String usernameKMFromCDH = usernameOptKMFromCDH.orElse("-");
            logger.info("usernameOptKMFromCDH : " + usernameKM);

            List<ApplicationTimelineDtl> timelineList = custmrDataFields.getApplicationTimelineDtl();

            if (timelineList != null && !timelineList.isEmpty()) {
                for (ApplicationTimelineDtl dtl : timelineList) {
                    if (dtl != null && Constants.SANCTION.equalsIgnoreCase(dtl.getStage())) {
                        bmIdFromCDH = dtl.getUserId() == null ? "-" : dtl.getUserId();
                    }
                }
                logger.debug("bmIdFromCDH: {}", bmIdFromCDH);
            } else {
                logger.debug("ApplicationTimelineDtl is empty for applicationId: {}", applicationId);
            }

            String actualUserId;

            if (bmIdFromCDH != null && bmIdFromCDH.contains("(")) {
//                actualUserId = bmIdFromCDH.substring(0, bmIdFromCDH.indexOf("("));
                actualUserId = bmIdFromCDH.substring(
                    bmIdFromCDH.indexOf("(") + 1,
                    bmIdFromCDH.indexOf(")")
            );
            } else {
                actualUserId = bmIdFromCDH;
            }
            Optional<String> usernameOptBMForAddProdct = tbUserRepository.findUserNameByUserId(actualUserId);
            String usernameBMForAddProdct = usernameOptBMForAddProdct.orElse("-");
            logger.info("usernameBMForAddProdct : " + usernameBMForAddProdct);

            Gson gsonObj = new Gson();
            LoanDetailsPayload payload = gsonObj.fromJson(custmrDataFields.getLoanDetails().getPayloadColumn(),
                    LoanDetailsPayload.class);
//            String language = payload.getLanguage();
//            logger.debug("Fetching json files: " + language); // Kannada

            Properties prop = null;
            try {
                prop = CommonUtils.readPropertyFile();
                logger.debug("Property file read successfully");
            } catch (IOException e) {
                logger.error("Error while reading property file in approveRejectApplication ", e);
            }

            String reportLanguages = prop.getProperty(CobFlagsProperties.REPORT_LANGUAGES.getKey());
            String[] reportLanguageArr = reportLanguages.split(",");
            String inputLanguage = payload.getLanguage();
            logger.debug("inputLanguage" + inputLanguage);
            boolean isValidLanguage = Arrays.stream(reportLanguageArr)
                    .anyMatch(lang -> lang.equalsIgnoreCase(inputLanguage));
            String language = isValidLanguage ? inputLanguage : Constants.DEFAULTLANGUAGE;
            logger.debug("final Language:" + language);

            String fileName = ""; // jsonKeysForKannadaLoanApplication.json

            // @author Abhishek.Raj.CAGL
            if (Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
                    .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())
            ){

//                        jsonKeysForHindiLoanApplicationAdd.json
                fileName = Constants.REPORT_JSONKEYS + language + "LoanApplicationSupAdd.json";

            }
            else if (Constants.FAMILY_WELFARE_PRODUCT_CODE
                    .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode()) ||
                    Constants.UNNATI_EMERGENCY_PRODUCT_CODE
                            .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode()))
            {
//                        jsonKeysForHindiLoanApplicationAdd.json
                fileName = Constants.REPORT_JSONKEYS + language + "LoanApplicationFWLAdd.json";
            }
            else{
//                        jsonKeysForKannadaLoanApplication.json
                fileName = Constants.REPORT_JSONKEYS + language + "LoanApplication.json";

            }

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
                keysForContent = fileContent.getJSONObject(Constants.KEYS_FOR_CONTENT);
                logger.debug("fileContent 2: " + keysForContent.toString());

            } catch (JSONException e) {
                logger.error(e.getMessage());
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                responseBody.setResponseObj(e.getMessage());
                response.setResponseHeader(responseHeader);
                response.setResponseBody(responseBody);
            }
            logger.debug("Fetching json files 2: " + keysForContent.toString());




//            List<ApplicationTimelineDtl> timelineList = custmrDataFields.getApplicationTimelineDtl();

            if (timelineList != null && !timelineList.isEmpty()) {
                for (ApplicationTimelineDtl dtl : timelineList) {
                    if (dtl != null && Constants.SANCTION.equalsIgnoreCase(dtl.getStage())) {
                        bmIdFromCDH = dtl.getUserId() == null ? "-" : dtl.getUserId();
                    }
                }
                logger.debug("bmIdFromCDH: {}", bmIdFromCDH);
            } else {
                logger.debug("ApplicationTimelineDtl is empty for applicationId: {}", applicationId);
            }
            UnnatiIexceedCDHLead cdhLeadDetails = custmrDataFields.getCdhLeadDetails();
            if (cdhLeadDetails != null && cdhLeadDetails.getKmId() != null){
                kmIdFromCDH = cdhLeadDetails.getKmId();
                logger.debug("KM ID from CDH: {}", kmIdFromCDH);
            }else {
                logger.debug("cdhLeadDetails or getKmId is null for applicationId: {}", applicationId);
            }

            if (cdhLeadDetails != null && cdhLeadDetails.getLeadInitiationDate() != null) {
                try {
                    leadInitiationDateStr = CommonUtils.formatDateTimeToDateStr(cdhLeadDetails.getLeadInitiationDate());
                    logger.debug("Formatted leadInitiationDateStr: {}", leadInitiationDateStr);
                } catch (Exception e) {
                    logger.error("Error while formatting lead initiation date: ", e);
                }
            } else {
                logger.debug("cdhLeadDetails or leadInitiationDate is null for applicationId: {}", applicationId);
            }
            try {
                // @author Abhishek.Raj.CAGL
                if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
                        .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.FAMILY_WELFARE_PRODUCT_CODE
                        .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode()) ||
                        Constants.UNNATI_EMERGENCY_PRODUCT_CODE
                                .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())
                ){
                    response = new LoanApplication().generateLoanApplicationPdfAdd(applicationMasterData, applnDocumentList, custmrDataFields, keysForContent, language, kmIdFromCDH, bmIdFromCDH, kmSubmDateStr, bmSubmDateStr, usernameKMFromCDH, usernameBMForAddProdct);
                }else{
                    response = new LoanApplication().generateLoanApplicationPdf(applicationMasterData, applnDocumentList, custmrDataFields, keysForContent, language, kmId, bmId, kmSubmDateStr, bmSubmDateStr, usernameKM, usernameBM, leadInitiationDateStr, kmIdFromCDH, bmIdFromCDH, usernameKMFromCDH, usernameBMForAddProdct);
                }
                // @author Abhishek.Raj.CAGL end
//                response = new LoanApplication().generateLoanApplicationPdf(applicationMasterData, custmrDataFields, keysForContent, language, kmId, bmId, kmSubmDateStr, bmSubmDateStr, usernameKM, usernameBM);
            } catch (Exception e) {
                logger.error(e.getMessage());
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                responseBody.setResponseObj(e.getMessage());
                response.setResponseHeader(responseHeader);
                response.setResponseBody(responseBody);
            }
        } else {
            logger.debug("Failed download Loan Applicaiton ");
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            responseBody.setResponseObj(Constants.APP_MASTER_NOT_FOUND);
            response.setResponseHeader(responseHeader);
            response.setResponseBody(responseBody);
        }
        return response;
    }

    private JSONObject sanitizeJson(JSONObject input) {
        JSONObject cleaned = new JSONObject();

        input.keySet().forEach(key -> {
            Object value = input.opt(key);

            if (value instanceof String) {
                String original = (String) value;
                String cleanedValue = sanitizeAndSafe(original);

                if (!original.equals(cleanedValue)) {
                    logger.warn("Sanitized key [{}]", key);
                }
                cleaned.put(key, cleanedValue);
            }
        });

        return cleaned;
    }

    private String sanitizeAndSafe(String input) {
        if (input == null) return "";

        return input
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replace("\u00A0", " ")
                .replaceAll("[\\u200B-\\u200D\\uFEFF]", "")
                .replaceAll("[\\p{Cntrl}&&[^\n]]", "")
                .trim();
    }


    public JSONObject getFileContent(String fileName, String directory) {

        logger.debug("fileName :" + fileName);
        JSONObject keysForContent = new JSONObject();
        JSONObject fileContent = new JSONObject();
        try {
            fileContent = new JSONObject(adapterUtil.readJSONContentFromServer(directory + "/" + fileName));
            logger.debug("fileContent 1: " + fileContent.toString());

            keysForContent = fileContent.getJSONObject(Constants.KEYS_FOR_CONTENT);
            logger.debug("fileContent 2: " + keysForContent.toString());

        } catch (IOException | JSONException e) {
            getFailureJson(e.getMessage());
        }
        logger.debug("Fetching json files 2: " + keysForContent.toString());

        return keysForContent;
    }

    public Response getFailureJson(String error) {
        logger.debug("Inside getFailureJson");
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
        responseBody.setResponseObj(error);
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
        responseBody.setResponseObj("{\"base64\":\"" + baseString + "\", \"status\":\"" + ResponseCodes.SUCCESS.getValue() + "\"}");
        logger.debug("string added to resonseBody as responseObj");
        response.setResponseHeader(responseHeader);
        logger.debug("responseHeader added");
        response.setResponseBody(responseBody);

        logger.debug("SuccessJson created");
        return response;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchLovMasterFallback")
    public Response fetchLovMaster(Request request) {
        Gson gson = new Gson();
        Response fetchLovMasterResponse = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        fetchLovMasterResponse.setResponseHeader(responseHeader);
        Iterable<LovMaster> lovDtls = lovMasterRepository.findByAppId(request.getAppId());
        String response = gson.toJson(lovDtls);
        responseBody.setResponseObj(response);
        fetchLovMasterResponse.setResponseBody(responseBody);
        return fetchLovMasterResponse;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchNomineeFallback")
    public Response fetchNominee(FetchNomineeRequest fetchNomineeRequest) {
        Gson gson = new Gson();
        String response = "";
        Response fetchNomineeResponse = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        fetchNomineeResponse.setResponseHeader(responseHeader);
        FetchNomineeRequestFields requestFields = fetchNomineeRequest.getRequestObj();
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestFields.getAppId(),
                        requestFields.getApplicationId(), requestFields.getVersionNum(),
                        AppStatus.INPROGRESS.getValue());
        if (applicationMasterOpt.isPresent()) {
            List<NomineeDetails> nomineeList;
            if (requestFields.getCustDtlId() != null) {
                nomineeList = nomineeDetailsRepository.findByApplicationIdAndAppIdAndVersionNumAndStatusAndCustDtlId(
                        requestFields.getApplicationId(), requestFields.getAppId(), requestFields.getVersionNum(),
                        AppStatus.ACTIVE_STATUS.getValue(), requestFields.getCustDtlId());
            } else {
                nomineeList = nomineeDetailsRepository.fetchNominees(requestFields.getApplicationId(),
                        requestFields.getAppId(), AppStatus.ACTIVE_STATUS.getValue());
            }
            response = gson.toJson(nomineeList);
            response = response.replace(Constants.PAYLOAD_COLUMN, Constants.PAYLOAD);
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        } else {
            response = "Invalid details.";
            responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
        }
        responseBody.setResponseObj(response);
        fetchNomineeResponse.setResponseBody(responseBody);
        return fetchNomineeResponse;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "checkApplicationFallback")
    public Mono<Response> checkApplication(CheckApplicationRequest request, Header header) {
        Gson gson = new Gson();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        Properties prop = null;
        try {
            prop = CommonUtils.readPropertyFile();
        } catch (IOException e) {
            logger.error("Error while reading property file in checkApplication ", e);
            return FallbackUtils.genericFallbackMono();
        }
        if ("Y".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.EXT_SYSTEM_DEDUPE_REQUIRED.getKey()))) {
            // dedupe check hook.
            Mono<Object> extResponse = interfaceAdapter.callExternalService(header, request, "VerifyApplication");
            Properties propFinal = prop;
            return extResponse.flatMap(val -> {
                Response response = new Response();
                ResponseWrapper res = adapterUtil.getResponseMapper(val, "VerifyApplication", header);
                if (ResponseParser.isExtCallSuccess(res.getApiResponse(), "checkApplication")) {
                    if (ResponseParser.isNewCustomer(res.getApiResponse())) {
                        response = checkApplication(responseHeader, request, propFinal, responseBody);
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
            Response response = checkApplication(responseHeader, request, prop, responseBody);
            return Mono.just(response);
        } else {
            return Mono.empty();
        }
    }

    public Response checkApplication(ResponseHeader responseHeader, CheckApplicationRequest request, Properties prop,
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
        String branchCode = null;
        String res = "";
        String lastElementArr = "";
        CheckApplicationRequestFields requestFields = request.getRequestObj();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        List<String> statusList = new ArrayList<>();
        statusList.add(AppStatus.INPROGRESS.getValue());
        statusList.add(AppStatus.APPROVED.getValue());
        if (!(CommonUtils.isNullOrEmpty(requestFields.getMobileNumber()))) {
            mobileNum = requestFields.getMobileNumber();
        }
        if (!(CommonUtils.isNullOrEmpty(requestFields.getEmailId()))) {
            emailId = requestFields.getEmailId();
        }
        if (!(CommonUtils.isNullOrEmpty(requestFields.getNationalId()))) {
            nationalId = requestFields.getNationalId();
        }
        if (!(CommonUtils.isNullOrEmpty(requestFields.getPan()))) {
            pan = requestFields.getPan();
        }
        if (!(CommonUtils.isNullOrEmpty(requestFields.getProductGroupCode()))) {
            productGroupCode = requestFields.getProductGroupCode();
        }
        if (!(CommonUtils.isNullOrEmpty(requestFields.getBranchCode()))) {
            branchCode = requestFields.getBranchCode();
        }
        List<ApplicationMaster> appMasterObj = applicationMasterRepository.findDataCasa(requestFields.getAppId(),
                mobileNum, nationalId, pan, emailId, productGroupCode, statusList, branchCode);

        // List<ApplicationMaster> appMasterObj = new ArrayList<>();

        boolean iv108 = false;
        boolean iv115 = false;
        boolean iv109 = false;
        for (ApplicationMaster appMasterObjDb : appMasterObj) {
            String headerAppId = request.getAppId();
            JSONArray array = null;
            if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) { // self
                // onboarding
                if (Products.LOAN.getKey().equalsIgnoreCase(requestFields.getMainProductGroupCode())) {
                    if (Products.LOAN.getKey().equalsIgnoreCase(productGroupCode)) {
                        array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE,
                                CodeTypes.LOAN_ETB.getKey(), Constants.FUNCTIONSEQUENCE);
                    }
                }
            } else {
                if (Products.LOAN.getKey().equalsIgnoreCase(requestFields.getMainProductGroupCode())) {
                    if (Products.LOAN.getKey().equalsIgnoreCase(productGroupCode)) {
                        array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE,
                                CodeTypes.LOAN_BO_ETB.getKey(), Constants.FUNCTIONSEQUENCE);
                    }
                }
            }
            if (null != array) {
                lastElementArr = ((String) array.get(array.length() - 1)).split("~")[0];
            }
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
                    String deleteRule = prop.getProperty(CobFlagsProperties.CASA_DELETE_RULE.getKey());
                    if (Constants.HARD_DELETE.equalsIgnoreCase(deleteRule) && appMasterObjDb != null) {
                        deleteApplication(appMasterObjDb.getApplicationId(), appMasterObjDb.getAppId());
                    } else if (Constants.MOVE_TO_HISTORY_TABLES.equalsIgnoreCase(deleteRule)
                            && appMasterObjDb != null) {
                        populateHistoryTables(appMasterObjDb.getApplicationId(), appMasterObjDb.getAppId());
                    } else if (Constants.UPDATE_STATUS.equalsIgnoreCase(deleteRule)) {
                        appMasterObjDb.setApplicationStatus(AppStatus.DELETED.getValue());
                        applicationMasterRepository.save(appMasterObjDb);
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
        List<ApplicationMaster> appMasterDb = applicationMasterRepository.findByAppIdAndApplicationId(appId,
                applicationId);
        if (null != appMasterDb && !appMasterDb.isEmpty()) {
            for (ApplicationMaster appMaster : appMasterDb) {
                ApplicationMasterHistory appMasterHistory = new ApplicationMasterHistory();
                BeanUtils.copyProperties(appMaster, appMasterHistory);
                applicationMasterHisRepository.save(appMasterHistory);
                applicationMasterRepository.deleteByApplicationIdAndAppId(applicationId, appId);
            }
        }

        AddressDetailsHistory addresshistory;
        List<AddressDetails> addressList = addressDetailsRepository.findByApplicationIdAndAppId(applicationId, appId);
        for (AddressDetails addressObj : addressList) {
            addresshistory = new AddressDetailsHistory();
            BeanUtils.copyProperties(addressObj, addresshistory);
            addressDetailsHisRepository.save(addresshistory);
        }
        addressDetailsRepository.deleteByApplicationIdAndAppId(applicationId, appId);

        OccupationDetailsHistory occupationHistory;
        List<OccupationDetails> occupationList = occupationDetailsRepository.findByApplicationIdAndAppId(applicationId,
                appId);
        for (OccupationDetails ocupationObj : occupationList) {
            occupationHistory = new OccupationDetailsHistory();
            BeanUtils.copyProperties(ocupationObj, occupationHistory);
            occupationDetailsHisRepository.save(occupationHistory);
        }
        occupationDetailsRepository.deleteByApplicationIdAndAppId(applicationId, appId);

        ApplicationDocumentsHistory documentHistory;
        List<ApplicationDocuments> documentList = applicationDocumentsRepository
                .findByApplicationIdAndAppId(applicationId, appId);
        for (ApplicationDocuments documentObj : documentList) {
            documentHistory = new ApplicationDocumentsHistory();
            BeanUtils.copyProperties(documentObj, documentHistory);
            applicationDocumentsHisRepository.save(documentHistory);
        }
        applicationDocumentsRepository.deleteByApplicationIdAndAppId(applicationId, appId);

        BankingFacilitiesHistory bankFacHistory;
        List<BankingFacilities> bankfacList = bankingFacilitiesRepository.findByApplicationIdAndAppId(applicationId,
                appId);
        for (BankingFacilities bankFacObj : bankfacList) {
            bankFacHistory = new BankingFacilitiesHistory();
            BeanUtils.copyProperties(bankFacObj, bankFacHistory);
            bankingFacilitiesHisRepository.save(bankFacHistory);
        }
        bankingFacilitiesRepository.deleteByApplicationIdAndAppId(applicationId, appId);

        NomineeDetailsHistory nomineehistory;
        List<NomineeDetails> nomineeList = nomineeDetailsRepository.findByApplicationIdAndAppId(applicationId, appId);
        for (NomineeDetails nomineeObj : nomineeList) {
            nomineehistory = new NomineeDetailsHistory();
            BeanUtils.copyProperties(nomineeObj, nomineehistory);
            nomineeDetailsHisRepository.save(nomineehistory);
        }
        nomineeDetailsRepository.deleteByApplicationIdAndAppId(applicationId, appId);

        CRSDetailsHistory crsDtlHistory;
        List<CRSDetails> crsList = crsDtlsrepository.findByApplicationIdAndAppId(applicationId, appId);
        for (CRSDetails crsObj : crsList) {
            crsDtlHistory = new CRSDetailsHistory();
            BeanUtils.copyProperties(crsObj, crsDtlHistory);
            crsDtlsHisrepository.save(crsDtlHistory);
        }
        crsDtlsrepository.deleteByApplicationIdAndAppId(applicationId, appId);

        FatcaDetailsHistory fatcaDtlHistory;
        List<FatcaDetails> fatcaList = fatcaDtlsrepository.findByApplicationIdAndAppId(applicationId, appId);
        for (FatcaDetails fatcaObj : fatcaList) {
            fatcaDtlHistory = new FatcaDetailsHistory();
            BeanUtils.copyProperties(fatcaObj, fatcaDtlHistory);
            fatcaDtlsHisrepository.save(fatcaDtlHistory);
        }
        fatcaDtlsrepository.deleteByApplicationIdAndAppId(applicationId, appId);

        CustomerDetailsHistory custDtlHistory;
        List<CustomerDetails> custDtlList = customerDetailsRepository.findByApplicationIdAndAppId(applicationId, appId);
        for (CustomerDetails custdtlObj : custDtlList) {
            custDtlHistory = new CustomerDetailsHistory();
            BeanUtils.copyProperties(custdtlObj, custDtlHistory);
            customerDetailsHisRepository.save(custDtlHistory);
        }
        customerDetailsRepository.deleteByApplicationIdAndAppId(applicationId, appId);
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "extractOcrDataFallback")
    public Mono<Response> extractOcrData(ExtractOcrDataRequest request, Header header) {
        Response response = new Response();
        ExtractOcrDataRequesttFields requestFields = request.getRequestObj();
        if ((requestFields != null) && (!(CommonUtils.isNullOrEmpty(requestFields.getDataBase64())))) {
            Mono<Object> monoResponse = interfaceAdapter.callExternalService(header, request,
                    request.getInterfaceName());
            return monoResponse.flatMap(val -> {
                ResponseWrapper wrapper = adapterUtil.getResponseMapper(val, request.getInterfaceName(), header);
                if (wrapper.getApiResponse() != null && wrapper.getApiResponse().getResponseBody() != null
                        && wrapper.getApiResponse().getResponseHeader() != null) {
                    if (ResponseCodes.SUCCESS.getKey()
                            .equalsIgnoreCase(wrapper.getApiResponse().getResponseHeader().getResponseCode())) {
                        String responseObj = wrapper.getApiResponse().getResponseBody().getResponseObj();
                        JSONObject responseJson = new JSONObject(responseObj);
                        Properties prop = null;
                        try {
                            prop = CommonUtils.readPropertyFile();
                        } catch (IOException e) {
                            response.getResponseHeader().setResponseCode(ResponseCodes.FAILURE.getKey());
                        }
                        JSONObject extractedJson = (JSONObject) responseJson
                                .get(prop.getProperty(CobFlagsProperties.EXTRACTED_ENTITIES_KEY.getKey()));
                        if (Constants.DOCUMENT_FRONT.equalsIgnoreCase(request.getRequestObj().getDocSide())) {
                            JSONArray nationalIdList = (JSONArray) extractedJson
                                    .get(prop.getProperty(CobFlagsProperties.NATIONAL_ID_KEY.getKey()));
                            if (nationalIdList.length() > 0) {
                                response.getResponseHeader().setResponseCode(ResponseCodes.SUCCESS.getKey());
                            } else {
                                response.getResponseHeader().setResponseCode(ResponseCodes.OCR_RES_NOT_VALID.getKey());
                                response.getResponseBody().setResponseObj(ResponseCodes.OCR_RES_NOT_VALID.getValue());
                            }
                        } else if (Constants.DOCUMENT_BACK.equalsIgnoreCase(request.getRequestObj().getDocSide())) {
                            JSONArray adressList = (JSONArray) extractedJson
                                    .get(prop.getProperty(CobFlagsProperties.ADDRESS_KEY.getKey()));
                            if (adressList.length() > 0) {
                                response.getResponseHeader().setResponseCode(ResponseCodes.SUCCESS.getKey());
                            } else {
                                response.getResponseHeader().setResponseCode(ResponseCodes.OCR_RES_NOT_VALID.getKey());
                                response.getResponseBody().setResponseObj(ResponseCodes.OCR_RES_NOT_VALID.getValue());
                            }
                        }
                    } else {
                        response.getResponseHeader().setResponseCode(ResponseCodes.OCR_RES_NOT_VALID.getKey());
                        response.getResponseBody().setResponseObj(ResponseCodes.OCR_RES_NOT_VALID.getValue());
                    }
                }
                return Mono.just(response);
            });
        } else {
            response.getResponseHeader().setResponseCode(ResponseCodes.BASE64_DATA_NOT_FOUND.getKey());
            response.getResponseBody().setResponseObj(ResponseCodes.BASE64_DATA_NOT_FOUND.getValue());
        }
        return Mono.just(response);
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "uploadDocumentFallback")
    public Mono<Response> uploadDocument(UploadDocumentRequest request, String nationalId, Header header,
                                         boolean isSelfOnBoardingHeaderAppId, Properties prop) {
        UploadDocumentRequestFields requestFields = request.getRequestObj();
        boolean isFirstStage = false;
        CheckAppCreateAppElements checkAppCreateAppElements = new CheckAppCreateAppElements();
        if ((CommonUtils.isNullOrEmpty(requestFields.getApplicationId()))
                && (CommonUtils.isNullOrEmpty(requestFields.getFilePath())) && (requestFields.getVersionNum() == 0)) {
            isFirstStage = true;
        }
        if ((CommonUtils.isNullOrEmpty(nationalId))) {
            Response res = uploadDocument(requestFields, prop, checkAppCreateAppElements, isFirstStage);
            return Mono.just(res);
        } else {
            // This code is to handle when application ID and file path are not provided in
            // request. This happens when KYC verification or upload document is the first
            // screen of the application. Because OCR/upload docuemtn is called even before
            // application ID is created.
            // Application ID will be null when OCR service/upload document service is
            // called even before application ID got created. This happens when KYC
            // Documents screen/upload documents screen is the first screen of the
            // application.
            // File path will be same as /appid/application ID/. File path will be null or
            // empty when OCR service/upload document service is called even before
            // application ID got created. This happens when KYC Documents screen/upload
            // documents screen is the first screen of the application.
            // version number will not be sent in this scenario.
            CheckApplicationRequestFields checkAppReqFields = new CheckApplicationRequestFields();
            CheckApplicationRequest checkAppReq = new CheckApplicationRequest();
            checkAppReqFields.setAppId(requestFields.getAppId());
            checkAppReqFields.setNationalId(nationalId);
            checkAppReq.setRequestObj(checkAppReqFields);
            // check if an application is present for the given national ID in master table.
            // calling this service when kyc verification is the first screen is restricted
            // for back image first. It is allowed for front image first. During
            // implementation this has to be handled based on the document side which has
            // the national id.
            Mono<Response> checkAppResMono = checkApplication(checkAppReq, header);
            return checkAppResMono.flatMap(checkAppRes -> {
                Gson gson = new Gson();
                Response response = new Response();
                ResponseHeader responseHeader = new ResponseHeader();
                ResponseBody responseBody = new ResponseBody();
                boolean checkAppFlag = false;
                CustomerIdentificationCasa customerIdentification = null;
                String applicationID = "";
                String resStr = "";
                if (checkAppRes != null && checkAppRes.getResponseBody() != null
                        && checkAppRes.getResponseBody().getResponseObj().length() > 0) {// length>0 --NON STP and
                    // application exists for
                    // the given national ID.
                    checkAppFlag = true;
                    resStr = checkAppRes.getResponseBody().getResponseObj();
                } else {
                    checkAppFlag = false;
                    customerIdentification = new CustomerIdentificationCasa();
                    CustomerDataFields customerDataFields = new CustomerDataFields();
                    applicationID = CommonUtils.generateRandomNumStr();
                    BigDecimal custDtlId = CommonUtils.generateRandomNum();
                    ApplicationMaster applicationMaster = new ApplicationMaster();
                    applicationMaster.setApplicationId(applicationID);
                    applicationMaster.setVersionNum(Constants.INITIAL_VERSION_NO);
                    applicationMaster.setApplicationDate(LocalDate.now());
                    applicationMaster.setApplicationStatus(AppStatus.INPROGRESS.getValue());
                    applicationMaster.setCurrentScreenId("");
                    customerDataFields.setApplicationMaster(applicationMaster);
                    customerDataFields.setAppId(requestFields.getAppId());
                    populateAppMasterAndApplnwf(customerDataFields, customerIdentification, applicationID, custDtlId,
                            Constants.INITIAL_VERSION_NO, isSelfOnBoardingHeaderAppId, prop);// method to insert into
                    // TB_ABOB_APPLICATION_MASTER.
                    commonService.populateCustomerDtlsIfNotPresent(customerDataFields.getApplicationMaster(),
                            applicationID, custDtlId, Constants.INITIAL_VERSION_NO, customerDataFields.getAppId());
                    requestFields.setApplicationId(applicationID);
                    requestFields.setFilePath("/" + requestFields.getAppId() + "/" + applicationID + "/");
                    requestFields.setVersionNum(Constants.INITIAL_VERSION_NO);
                }

                if (checkAppFlag) {
                    checkAppCreateAppElements.setCheckAppRes(resStr);
                    responseHeader.setResponseCode(checkAppRes.getResponseHeader().getResponseCode());
                    responseBody.setResponseObj(gson.toJson(checkAppCreateAppElements));
                } else {
                    boolean isFirstStage1 = false;
                    if ((CommonUtils.isNullOrEmpty(requestFields.getApplicationId()))
                            && (CommonUtils.isNullOrEmpty(requestFields.getFilePath()))
                            && (requestFields.getVersionNum() == 0)) {
                        isFirstStage1 = true;
                    }
                    Response res = uploadDocument(requestFields, prop, checkAppCreateAppElements, isFirstStage1);
                    return Mono.just(res);
                }
                response.setResponseHeader(responseHeader);
                response.setResponseBody(responseBody);
                logger.debug("%s %s", "inside uploadDocument responseBody returned is ", responseBody.getResponseObj());
                return Mono.just(response);
            });
        }
    }

    public Response uploadDocument(UploadDocumentRequestFields requestFields, Properties prop,
                                   CheckAppCreateAppElements checkAppCreateAppElements, boolean isFirstStage) {
        Gson gson = new Gson();
        CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
        String applicationID = "";
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        BigDecimal docId = null;
        if (requestFields.getDocumentId() == null) {
            docId = CommonUtils.generateRandomNum();
        } else {
            docId = requestFields.getDocumentId();
        }
        List<String> applnStatus = new ArrayList<>();
        applnStatus.add(AppStatus.PENDING.getValue());
        applnStatus.add(AppStatus.INPROGRESS.getValue());
        applnStatus.add(AppStatus.PUSHBACK.getValue());
        applnStatus.add(AppStatus.PENDINGREASSESSMENT.getValue()); // A // need remove
        applnStatus.add(AppStatus.IPUSHBACK.getValue());
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(requestFields.getAppId(),
                        requestFields.getApplicationId(), requestFields.getVersionNum(), applnStatus);
        if (masterObjDb.isPresent()) {
            byte[] docByte;
            if (!(CommonUtils.isNullOrEmpty(requestFields.getBase64Value()))) {
                String uploadLocation = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey());
                if (null != uploadLocation && !"".equalsIgnoreCase(uploadLocation)) {
                    docByte = Base64.getDecoder().decode(requestFields.getBase64Value());
                    if (!(CommonUtils.isNullOrEmpty(requestFields.getFilePath()))) {
                        String[] splitFileName = requestFields.getFileName().split("\\.");
                        String fileFormat = splitFileName[splitFileName.length - 1];
                        try {
                            JSONArray array = getArray(requestFields);
                            if (isFileFormatValid(docByte, fileFormat) && isFileFormatAllowed(array, fileFormat)) { // VAPT
                                String fileLoc = uploadLocation + requestFields.getFilePath();
                                File file = new File(fileLoc);
                                if (!file.exists()) {
                                    file.mkdirs();
                                }
                                if (Constants.DOCFORMATPDF.equalsIgnoreCase(fileFormat)) {
                                    try (FileOutputStream fos = new FileOutputStream(
                                            fileLoc + requestFields.getFileName())) {
                                        fos.write(docByte);
                                    }
                                } else {// other formats like jpeg, jpg, png
                                    try (ByteArrayInputStream bis = new ByteArrayInputStream(docByte)) {
                                        BufferedImage image = ImageIO.read(bis);
                                        File outputfile = new File(fileLoc + requestFields.getFileName());
                                        ImageIO.write(image, fileFormat, outputfile);
                                    }
                                }
                                responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                                customerIdentification.setAppDocId(docId);
                                checkAppCreateAppElements.setCreateAppRes(customerIdentification);
                                responseBody.setResponseObj(gson.toJson(checkAppCreateAppElements));
                            } else {
                                responseHeader.setResponseCode(ResponseCodes.VAPT_ISSUE_FILE_FORMAT.getKey());
                                responseBody.setResponseObj(ResponseCodes.VAPT_ISSUE_FILE_FORMAT.getValue());
                                if (isFirstStage) {
                                    applicationMasterRepository.deleteByApplicationIdAndAppId(applicationID,
                                            requestFields.getAppId());
                                    customerDetailsRepository.deleteByApplicationIdAndAppId(applicationID,
                                            requestFields.getAppId());
                                }
                            }
                        } catch (IOException e) {
                            logger.error("Exception in upload document IOException ", e);
                            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                        }
                    }
                } else {
                    responseHeader.setResponseCode(ResponseCodes.PATH_NOT_CONFIGURED.getKey());
                    responseBody.setResponseObj("Document not uploaded.");
                    if (isFirstStage) {
                        applicationMasterRepository.deleteByApplicationIdAndAppId(applicationID,
                                requestFields.getAppId());
                        customerDetailsRepository.deleteByApplicationIdAndAppId(applicationID,
                                requestFields.getAppId());
                    }
                }
            } else {
                responseHeader.setResponseCode(ResponseCodes.BASE64_DATA_NOT_FOUND.getKey());
                responseBody.setResponseObj(ResponseCodes.BASE64_DATA_NOT_FOUND.getValue());
                if (isFirstStage) {
                    applicationMasterRepository.deleteByApplicationIdAndAppId(applicationID, requestFields.getAppId());
                    customerDetailsRepository.deleteByApplicationIdAndAppId(applicationID, requestFields.getAppId());
                }
            }
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            responseBody.setResponseObj(Constants.APP_MASTER_NOT_FOUND);
        }
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    private JSONArray getArray(UploadDocumentRequestFields requestFields) {
        JSONArray array = null;
        if ("KYC".equalsIgnoreCase(requestFields.getSrcScreen())) {
            array = commonService.getJsonArrayForCmCode(Constants.KYC_VERIFICATION, Constants.TYPES);
        } else if ("UPLOADDOCS".equalsIgnoreCase(requestFields.getSrcScreen())) {
            if (Products.LOAN.getKey().equalsIgnoreCase(requestFields.getProductGroupCode())) {
                if ("N".equalsIgnoreCase(requestFields.getIsExistingCustomer())) {
                    array = commonService.getJsonArrayForCmCodeAndKey(Constants.UPLOAD_DOCS, CodeTypes.CASA.getKey(),
                            Constants.UPLOAD_DOCS, Constants.TYPES);
                } else if ("Y".equalsIgnoreCase(requestFields.getIsExistingCustomer())) {
                    array = commonService.getJsonArrayForCmCodeAndKey(Constants.UPLOAD_DOCS,
                            CodeTypes.LOAN_ETB.getKey(), Constants.UPLOAD_DOCS, Constants.TYPES);
                }
            }
        }
        return array;
    }

    // VAPT check
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

    // VAPT check
    private boolean isFileFormatAllowed(JSONArray array, String fileFormat) {
        String fileType;
        if (array != null) {
            for (Object arrayObj : array) {
                fileType = (String) arrayObj;
                String[] split = fileType.split("~");
                if ("Y".equalsIgnoreCase(split[1]) && fileFormat.equalsIgnoreCase(split[0])) {
                    return true;
                }
            }
        }
        return false;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchBranchesFallback")
    public Response fetchBranches(FetchBranchesRequest request) {
        FetchBranchesRequestFields requestFields = request.getRequestObj();
        Gson gson = new Gson();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        response.setResponseHeader(responseHeader);
        Iterable<Branches> branchList;
        if (!CommonUtils.isNullOrEmpty(requestFields.getPinCode())) {
            branchList = branchesRepository.findByPinCode(requestFields.getPinCode());
        } else {
            branchList = branchesRepository.findByStateCodeAndCityCode(requestFields.getStateCode(),
                    requestFields.getCityCode());
        }
        String responseStr = gson.toJson(branchList);
        responseBody.setResponseObj(responseStr);
        response.setResponseBody(responseBody);
        return response;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "createApplicationInDemoModeFallback")
    public Mono<Response> createApplicationInDemoMode(CreateModifyUserRequest request) {
        Response createuserResponse = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        ResponseBody responseBody = new ResponseBody();
        Gson gson = new Gson();
        CustomerIdentificationCasa customerIdentification = new CustomerIdentificationCasa();
        CustomerDataFields customerDataFields = request.getRequestObj();
        ApplicationMaster masterRequest = customerDataFields.getApplicationMaster();
        String accNum;
        BigDecimal customerId;
        String applicationID;
        BigDecimal custDtlId;
        if (CommonUtils.isNullOrEmpty(customerDataFields.getApplicationId())) {
            applicationID = CommonUtils.generateRandomNumStr();
        } else {
            applicationID = customerDataFields.getApplicationId();
        }
        if (masterRequest.getCustDtlId() == null) {
            custDtlId = CommonUtils.generateRandomNum();
        } else {
            custDtlId = masterRequest.getCustDtlId();
        }
        if (CommonUtils.isNullOrEmpty(masterRequest.getAccNumber())) {
            accNum = CommonUtils.generateRandomNumStr();
        } else {
            accNum = masterRequest.getAccNumber();
        }
        if (masterRequest.getCustomerId() == null) {
            customerId = CommonUtils.generateRandomNum();
        } else {
            customerId = masterRequest.getCustomerId();
        }
        customerIdentification.setCustDtlId(custDtlId.toString());
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setCustomerId(customerId.toString());
        customerIdentification.setCasaAccNumber(accNum);
        customerIdentification.setVersionNum(Constants.INITIAL_VERSION_NO);
        String response = gson.toJson(customerIdentification);
        responseBody.setResponseObj(response);
        createuserResponse.setResponseBody(responseBody);
        createuserResponse.setResponseHeader(responseHeader);
        return Mono.just(createuserResponse);
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchPropertyFromOcrResponseFallback")
    public String fetchPropertyFromOcrResponse(Response response, String nationalIdKey, ExtractOcrDataRequest request,
                                               Properties prop) {
        String nationalId = "";
        String nationalIdFormatted = "";
        String responseObj = response.getResponseBody().getResponseObj();
        JSONObject responseJson = new JSONObject(responseObj);
        JSONObject extractedJson = (JSONObject) responseJson
                .get(prop.getProperty(CobFlagsProperties.EXTRACTED_ENTITIES_KEY.getKey()));
        if (Constants.DOCUMENT_FRONT.equalsIgnoreCase(request.getRequestObj().getDocSide())) {
            JSONArray nationalIdList = (JSONArray) extractedJson.get(prop.getProperty(nationalIdKey));
            if (nationalIdList.length() > 0) {
                nationalId = (String) nationalIdList.get(0);
                nationalIdFormatted = nationalId.replace(" ", "").replace("-", ""); // First replace is for Aadhaar and
                // second replace is for emirates.
                // Please use accordingly.
            }
        }
        return nationalIdFormatted;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "updateLitFileFallback")
    public Response updateLitFile(UpdateLitFileRequest updateLitFileRequest) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        UpdateLitFileRequestFields reqFields = updateLitFileRequest.getRequestObj();
        List<LITDomain> litList = reqFields.getLitList();
        String file = CommonUtils.getCommonProperties(SpringCloudProperties.LIT_FILE_PATH.getKey())
                + reqFields.getLanguageCode() + "."
                + CommonUtils.getCommonProperties(SpringCloudProperties.LIT_FILE_FORMAT.getKey());
        File fileObj = new File(file);
        if (!fileObj.exists()) {
            fileObj.mkdirs();
        }
        try (FileWriter myWriter = new FileWriter(file)) {
            for (LITDomain listObj : litList) {
                myWriter.write(listObj.toString());
            }
        } catch (IOException e) {
            logger.error("Error in updateLitFile ", e);
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        responseBody.setResponseObj("");
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchLitByLanguageFallback")
    public Response fetchLitByLanguage(FetchLitByLanguageRequest request) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        ResponseBody responseBody = new ResponseBody();
        Gson gson = new Gson();
        List<CommonParamResponse> commonParamResponseList = new ArrayList<>();
        String languageCode = request.getRequestObj().getLanguageCode();
        CommonParamResponse commonParamResponseObj;
        String file = CommonUtils.getCommonProperties(SpringCloudProperties.LIT_FILE_PATH.getKey()) + languageCode + "."
                + CommonUtils.getCommonProperties(SpringCloudProperties.LIT_FILE_FORMAT.getKey());
        try (FileReader fileReader = new FileReader(file); BufferedReader stream = new BufferedReader(fileReader)) {
            String line;
            List<String> list = new ArrayList<>();
            commonParamResponseObj = new CommonParamResponse();
            commonParamResponseObj.setParamName("LITCODES");
            // line = stream.readLine(); // Read the header and ignore
            while ((line = stream.readLine()) != null) {
                list.add(line);
            }
            commonParamResponseObj.setParamValue(gson.toJson(list));
            commonParamResponseList.add(commonParamResponseObj);
        } catch (Exception e) {
            logger.error("Exception in fetchLitByLanguage ", e);
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }
        responseBody.setResponseObj(gson.toJson(commonParamResponseList));
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "createRoleFallback")
    public Response createRole(CreateRoleRequest apiRequest) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        CreateRoleRequestFields requestObj = apiRequest.getRequestObj();
        RoleAccessMap roleObj = new RoleAccessMap();
        roleObj.setAccessPermission(requestObj.getAccessPermission());
        roleObj.setAppId(requestObj.getAppId());
        roleObj.setRoleId(requestObj.getRoleId());
        roleObj.setAllowedFeature(requestObj.getFeatureStatusMap());
        roleAccessMapRepository.save(roleObj);
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        responseBody.setResponseObj("");
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "deleteRoleFallback")
    public Response deleteRole(FetchRoleRequest apiRequest) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        FetchRoleRequestFields requestObj = apiRequest.getRequestObj();
        RoleAccessMapId id = new RoleAccessMapId(apiRequest.getAppId(), requestObj.getRoleId());
        Optional<RoleAccessMap> obj = roleAccessMapRepository.findById(id);
        if (obj.isPresent()) {
            roleAccessMapRepository.deleteById(id);
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_ROLE.getKey());
        }
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchRoleFallback")
    public Response fetchRole(FetchRoleRequest apiRequest, Properties prop) {
        Gson gson = new Gson();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        FetchRoleRequestFields requestObj = apiRequest.getRequestObj();
        logger.debug("Request obj: {}", requestObj);

        String roleId = requestObj.getRoleId();
        String branchCode = requestObj.getBranchCode();
        String fetchType = requestObj.getFetchType() != null ? requestObj.getFetchType() : "";
        String filterType = requestObj.getFilterType() != null ? requestObj.getFilterType() : "";
        String useRolesString = prop.getProperty(CobFlagsProperties.APZ_USER_ROLE.getKey());
        String userRole = getUserRole(roleId, useRolesString);

        List<String> kendraIds = null;
        List<String> branches = null;
        JSONObject resjson = new JSONObject();

        String productId = ProductCode.getCdhCodeByFetchType(fetchType);
        List<String> additonalProducts = new ArrayList<>();
        additonalProducts.add(ProductCode.FAMILY_WELFARE.getCdhCode());
        additonalProducts.add(ProductCode.UNNATI_SUPPLEMENTARY.getCdhCode());
        additonalProducts.add(ProductCode.UNNATI_RESTART.getCdhCode());
        additonalProducts.add(ProductCode.UNNATI_EMERGENCY.getCdhCode());

        String userId = requestObj.getUserId();
        int i = applicationMasterRepository.updateLockOut(userId, userId + "_INPROGRESS");
        logger.debug(" records unlocked -->" + i);

        int numOfRecords = Integer.parseInt(prop.getProperty(CobFlagsProperties.NUM_OF_REC_IN_WIDGET.getKey()));

        int currentPage = 0;
        Integer pageNo = requestObj.getPageNo();
        if (pageNo != null && pageNo > 0) {
            currentPage = pageNo - 1;
        }
        Pageable page = PageRequest.of(currentPage, numOfRecords);

        boolean customLoginFlow = !CommonUtils.isNullOrEmpty(requestObj.getUserId());

        if (customLoginFlow) {
            if (CobFlagsProperties.RPC.getKey().equalsIgnoreCase(roleId) || Constants.RM.equalsIgnoreCase(roleId)
                    || Constants.DM.equalsIgnoreCase(roleId)) {
                branches = fetchBranchesByUserIdAndRoleId(requestObj.getUserId(), roleId, branchCode, useRolesString);
            } else {
                kendraIds = fetchKendrasByUserIsAndRoleId(requestObj.getUserId(), roleId, branchCode, useRolesString, fetchType);
            }
        }

        RoleAccessMap objDb = fetchRoleAccessMapObj(apiRequest.getAppId(), roleId);

        if (customLoginFlow && objDb != null) {
            List<String> statusList = fetchAllowedStatusListForRole(objDb, Constants.FEATURE_DASHBOARD_WIDGETS);
            long numOfDays = Long.parseLong(prop.getProperty(CobFlagsProperties.NUM_OF_DAYS_RECORDS.getKey()));
            LocalDate toDay = LocalDate.now();
            LocalDate fromDay = toDay.minusDays(numOfDays);
            Pageable page_old = PageRequest.of(0, numOfRecords);

            if (CobFlagsProperties.RPC.getKey().equalsIgnoreCase(roleId)) {
                logger.error("RPC dashboard is done with another endpoint: /fetchRPCRoleData");
            } else if (Constants.APPROVER.equalsIgnoreCase(roleId)
                    || CobFlagsProperties.BM.getKey().equalsIgnoreCase(roleId)) {
                resjson.put("roleAccessMap", objDb);

                if (filterType.isEmpty()) {
                    filterType = Constants.BM_APPROVAL + "|" + Constants.PENDING;
                }
                branches = branchCode != null && !branchCode.isEmpty()
                        ? Arrays.stream(branchCode.split(",")).map(String::trim).filter(s -> !s.isEmpty()) // optional:
                        // removes
                        // empty
                        // codes
                          .collect(Collectors.toList())
                        : new ArrayList<>();
                logger.debug("BM fetchDashboard branches:" + branches);
                ApplicationMaster bmDashboardCounts = fetchDashboardCounts(branches, fetchType, Constants.BM, prop);
                Page<ApplicationMaster> bmDashboardApplications = fetchDashboardApplications(branches, fetchType,
                        filterType, userRole, Constants.BM, page, prop);

                resjson.put(Constants.DASHBOARD_COUNTS, bmDashboardCounts != null ? bmDashboardCounts : JSONObject.NULL);
                resjson.put(Constants.DASHBOARD_APPLICATIONS,
                        bmDashboardApplications != null ? bmDashboardApplications : JSONObject.NULL);
                resjson.put("allowedStatus", fetchAllowedStatusListForRole(objDb, Constants.FEATURE_SEARCH));
                resjson.put("kendraIds", kendraIds);
                resjson.put(Constants.BRANCHES, branches);

            } else if (Constants.BCM.equalsIgnoreCase(roleId)) {
                branches = branchCode != null && !branchCode.isEmpty()
                        ? Arrays.stream(branchCode.split(",")).map(String::trim).filter(s -> !s.isEmpty()) // optional:
                        // removes
                        // empty
                        // codes
                          .collect(Collectors.toList())
                        : new ArrayList<>();

                if (filterType.isEmpty()) {
                    filterType = Constants.DEVIATION_RA + "|" + Constants.CA_DEVIATION;
                }
                logger.debug("BCM fetchDashboard branches:" + branches);
                ApplicationMaster bcmDashboardCounts = fetchDashboardCounts(branches, fetchType, Constants.BCM, prop);
                Page<ApplicationMaster> bcmDashboardApplications = fetchDashboardApplications(branches, fetchType,
                        filterType, userRole, Constants.BCM, page, prop);
                resjson.put(Constants.DASHBOARD_COUNTS,
                        bcmDashboardCounts != null ? bcmDashboardCounts : JSONObject.NULL);
                resjson.put(Constants.DASHBOARD_APPLICATIONS,
                        bcmDashboardApplications != null ? bcmDashboardApplications : JSONObject.NULL);
                resjson.put("allowedStatus", fetchAllowedStatusListForRole(objDb, Constants.FEATURE_SEARCH));
                resjson.put("kendraIds", kendraIds);
                resjson.put(Constants.BRANCHES, branches);
                resjson.put("roleAccessMap", objDb);

            } else if (Constants.CCM.equalsIgnoreCase(roleId)) {
                List<String> branchCodesList = new ArrayList<>(); // CCM role is not associated with any branch, so sending empty list
                if (filterType.isEmpty()) {
                    filterType = Constants.DEVIATION_RA + "|" + Constants.REASSESSMENT;
                }
                ApplicationMaster ccmDashboardCounts = fetchCCMDashboardCounts(fetchType, Constants.CCM, prop);
                Page<ApplicationMaster> ccmDashboardApplications = fetchDashboardApplications(branchCodesList,
                        fetchType, filterType, userRole, Constants.CCM, page, prop);
                resjson.put(Constants.DASHBOARD_COUNTS, ccmDashboardCounts != null ? ccmDashboardCounts : JSONObject.NULL);
                resjson.put(Constants.DASHBOARD_APPLICATIONS,
                        ccmDashboardApplications != null ? ccmDashboardApplications : JSONObject.NULL);
                resjson.put("allowedStatus", fetchAllowedStatusListForRole(objDb, Constants.FEATURE_SEARCH));
                resjson.put("kendraIds", kendraIds);
                resjson.put(Constants.BRANCHES, new ArrayList<>());//sending empty list for branches as CCM role is not associated with any branch
                resjson.put("roleAccessMap", objDb);

            } else if (Constants.AM.equalsIgnoreCase(roleId)) {
                branches = fetchBranchesByUserIdAndRoleId(requestObj.getUserId(), roleId, branchCode, useRolesString);
                if (filterType.isEmpty()) {
                    filterType = Constants.DEVIATION_RA + "|" + Constants.CA_DEVIATION;
                }
                ApplicationMaster amDashboardCounts = fetchDashboardCounts(branches, fetchType, Constants.AM, prop);
                Page<ApplicationMaster> amDashboardApplications = fetchDashboardApplications(branches, fetchType,
                        filterType, userRole, Constants.AM, page, prop);
                resjson.put(Constants.DASHBOARD_COUNTS, amDashboardCounts != null ? amDashboardCounts : JSONObject.NULL);
                resjson.put(Constants.DASHBOARD_APPLICATIONS,
                        amDashboardApplications != null ? amDashboardApplications : JSONObject.NULL);
                resjson.put("allowedStatus", fetchAllowedStatusListForRole(objDb, Constants.FEATURE_SEARCH));
                resjson.put("kendraIds", kendraIds);
                resjson.put(Constants.BRANCHES, branches);
                resjson.put("roleAccessMap", objDb);

            } else if (Constants.ACM.equalsIgnoreCase(roleId)) {
                branches = fetchBranchesByUserIdAndRoleId(requestObj.getUserId(), roleId, branchCode, useRolesString);
                if (filterType.isEmpty()) {
                    filterType = Constants.DEVIATION_RA + "|" + Constants.REASSESSMENT;
                }
                ApplicationMaster acmDashboardCounts = fetchDashboardCounts(branches, fetchType, Constants.ACM, prop);
                Page<ApplicationMaster> acmDashboardApplications = fetchDashboardApplications(branches, fetchType,
                        filterType, userRole, Constants.ACM, page, prop);
                resjson.put(Constants.DASHBOARD_COUNTS, acmDashboardCounts != null ? acmDashboardCounts : JSONObject.NULL);
                resjson.put(Constants.DASHBOARD_APPLICATIONS,
                        acmDashboardApplications != null ? acmDashboardApplications : JSONObject.NULL);
                resjson.put("allowedStatus", fetchAllowedStatusListForRole(objDb, Constants.FEATURE_SEARCH));
                resjson.put("kendraIds", kendraIds);
                resjson.put(Constants.BRANCHES, branches);
                resjson.put("roleAccessMap", objDb);

            } else if (Constants.RM.equalsIgnoreCase(roleId)) {
                branches = fetchBranchesByUserIdAndRoleId(requestObj.getUserId(), roleId, branchCode, useRolesString);
                if (filterType.isEmpty()) {
					filterType = Constants.SANCTION + "|" + Constants.PENDING;
                }
                ApplicationMaster rmDashboardCounts = fetchDashboardCounts(branches, fetchType, Constants.RM, prop);
                Page<ApplicationMaster> rmDashboardApplications = fetchDashboardApplications(branches, fetchType,
                        filterType, userRole, Constants.RM, page, prop);
                resjson.put(Constants.DASHBOARD_COUNTS, rmDashboardCounts != null ? rmDashboardCounts : JSONObject.NULL);
                resjson.put(Constants.DASHBOARD_APPLICATIONS,
                        rmDashboardApplications != null ? rmDashboardApplications : JSONObject.NULL);
                resjson.put("allowedStatus", fetchAllowedStatusListForRole(objDb, Constants.FEATURE_SEARCH));
                resjson.put("kendraIds", kendraIds);
                resjson.put(Constants.BRANCHES, branches);
                resjson.put("roleAccessMap", objDb);

            } else if (Constants.DM.equalsIgnoreCase(roleId)) {
                branches = fetchBranchesByUserIdAndRoleId(requestObj.getUserId(), roleId, branchCode, useRolesString);
                if (filterType.isEmpty()) {
					filterType = Constants.SANCTION + "|" + Constants.PENDING;
                }
                ApplicationMaster dmDashboardCounts = fetchDashboardCounts(branches, fetchType, Constants.DM, prop);
                Page<ApplicationMaster> dmDashboardApplications = fetchDashboardApplications(branches, fetchType,
                        filterType, userRole, Constants.DM, page, prop);
                resjson.put(Constants.DASHBOARD_COUNTS, dmDashboardCounts != null ? dmDashboardCounts : JSONObject.NULL);
                resjson.put(Constants.DASHBOARD_APPLICATIONS,
                        dmDashboardApplications != null ? dmDashboardApplications : JSONObject.NULL);
                resjson.put("allowedStatus", fetchAllowedStatusListForRole(objDb, Constants.FEATURE_SEARCH));
                resjson.put("kendraIds", kendraIds);
                resjson.put(Constants.BRANCHES, branches);
                resjson.put("roleAccessMap", objDb);

            } else {
                statusList.add(AppStatus.CAPUSHBACK.getValue());
                if (filterType.isEmpty()) {
                    filterType = Constants.SOURCING_STRING + "|" + Constants.PENDING;
                }
                ApplicationMaster KmDashboardCounts = fetchKMDashboardCounts(kendraIds, fetchType, prop);
                Page<ApplicationMaster> KmDashboardApplications = fetchKMDashboardApplications(kendraIds, fetchType,
                        filterType, userRole, Constants.KM, page, prop);
                resjson.put(Constants.DASHBOARD_COUNTS, KmDashboardCounts != null ? KmDashboardCounts : JSONObject.NULL);
                resjson.put(Constants.DASHBOARD_APPLICATIONS,
                        KmDashboardApplications != null ? KmDashboardApplications : JSONObject.NULL);
                resjson.put("roleAccessMap", objDb);
                resjson.put("allowedStatus", fetchAllowedStatusListForRole(objDb, Constants.FEATURE_SEARCH));
                resjson.put("kendraIds", kendraIds);
                resjson.put(Constants.BRANCHES, branches);
            }

            if (Constants.APPROVER.equalsIgnoreCase(roleId) || Constants.BM.equalsIgnoreCase(roleId)) {
                resjson.put("leadDtls", new ArrayList<>());
                resjson.put("fliteredLeadDtls", new ArrayList<>());
            } else if (Constants.RM.equalsIgnoreCase(roleId) || Constants.DM.equalsIgnoreCase(roleId)
                    || Constants.AM.equalsIgnoreCase(roleId) || Constants.ACM.equalsIgnoreCase(roleId)
                    || Constants.BCM.equalsIgnoreCase(roleId)|| Constants.CCM.equalsIgnoreCase(roleId)) {
                // No lead details for RM and DM
            } else {
                List<String> productCode = new ArrayList<>();
                productCode.add(ProductCode.getCdhCodeByFetchType(fetchType));
                if(fetchType.equalsIgnoreCase(ProductCode.OPEN_MARKET.getFetchType())){
                    productCode.add(ProductCode.OPEN_MARKET.getUnnatiCode());
                }
                if(!additonalProducts.contains(productId)) {
                    List<UnnatiIexceedCDHLead> allLeads = fetchCDHOverallLeads(kendraIds, productCode);
                    resjson.put("leadDtls", allLeads);
                    resjson.put("fliteredLeadDtls", fetchUnactionedFreshLeads(kendraIds, allLeads));
                }else{
                    resjson.put("leadDtls", new ArrayList<>());
                    resjson.put("fliteredLeadDtls", new ArrayList<>());
                }
            }

            JSONArray leadDtlsJson = resjson.optJSONArray("leadDtls");
            JSONArray fliteredLeadDtlsJson = resjson.optJSONArray("fliteredLeadDtls");

            int updatedLeadCount = (leadDtlsJson != null) ? leadDtlsJson.length() : 0;
            int updatedFilteredCount = (fliteredLeadDtlsJson != null) ? fliteredLeadDtlsJson.length() : 0;

// Now safely override just those two fields in DashboardCounts
            Object dashboardObj = resjson.opt(Constants.DASHBOARD_COUNTS);
            if (dashboardObj instanceof ApplicationMaster) {
                ApplicationMaster original = (ApplicationMaster) dashboardObj;

                AtomicLong AddProductCount = new AtomicLong(0);
                if (additonalProducts.contains(productId)) {
                    logger.debug("FetchType :={}", fetchType);
                    try {
                        logger.debug("FetchType :={} Maitri productCodes :={} Branches :={}", fetchType, productId,
                                branches);
                        long count = unnatiIexceedCDHLeadRepo.countByGlBranchIdInAndProductAndUnnatiStatusIn(branches,
                                productId, Arrays.asList("Initialize", "Initiate"));
                        /* Removed Branch ID */
//						long countAdditionalProduct = unnatiIexceedCDHLeadRepo
//								.countByProductAndUnnatiStatusIn(productId, Arrays.asList("Initialize","Initiate"));
                        logger.debug("count :={}", count);
//						logger.debug("countAdditionalProduct :={}", countAdditionalProduct);
                        AddProductCount.getAndAdd(count);
                    } catch (Exception e) {
                        logger.debug("Error while Fetch Additional Product Count" + e);
                    }
                }

                logger.debug("PG SanctionCount :={} CDH SanctionCount :={}", original.getSanctionCount(),
                        AddProductCount.get());

                ApplicationMaster updatedCounts = new ApplicationMaster((long) updatedLeadCount,
                        (long) updatedFilteredCount, original.getSourcingCount(), original.getBmApprovalCount(),
                        original.getRpcVerificationCount(), original.getCreditAssessmentCount(),
                        original.getDeviationRACount(), original.getSanctionCount() + AddProductCount.get(),
                        original.getDisbursementInprogressCount(), original.getDisbursedCount(),
                        original.getPostDisbursementCount(), original.getRejectedCount());

                resjson.put(Constants.DASHBOARD_COUNTS, updatedCounts != null ? updatedCounts : JSONObject.NULL);
            }

            responseBody.setResponseObj(gson.toJson(resjson));
        } else {
            responseBody.setResponseObj(gson.toJson(objDb));
        }
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    public Response logError(ErrorLoggerRequest apiRequest) {
        Gson gson = new Gson();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        try {
            ErrorTrace errTrace = apiRequest.getErrorTrace();
            errTrace.setCreatedTs(LocalDateTime.now());
            if(!StringUtils.isBlank(errTrace.getTraceType())) {
                if(errTrace.getUniqueData() != null){
                    Optional<ErrorTrace> errorTraceOptional;
                    if(Boolean.TRUE.equals(errTrace.getCheckApplicationID())) {
                        errorTraceOptional = errorTraceRepository.findTopByUniqueDataAndApplicationId(errTrace.getUniqueData(), errTrace.getApplicationId());
                    } else {
                        errorTraceOptional = errorTraceRepository.findTopByUniqueDataOrderByCreatedTsDesc(errTrace.getUniqueData());
                    }
                   if(errorTraceOptional.isPresent()){
                       errTrace.setTraceId(errorTraceOptional.get().getTraceId());
                   }
                }
            	errTrace.setPayloadColumn(gson.toJson(errTrace.getPayload()));
            	errorTraceRepository.save(errTrace);
            }
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        } catch (Exception e) {
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            logger.debug("Exception while saving Error Trace ", e);
        }
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    private List<UnnatiIexceedCDHLead> fetchOpenMarketUnactionedLeads(List<String> kendraIds,
            List<UnnatiIexceedCDHLead> cdhLeads) {
        if (kendraIds == null || kendraIds.isEmpty()) {
            return Collections.emptyList();
        }
        if (cdhLeads == null || cdhLeads.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> memberIds = applicationMasterRepository2.fetchMemberIds(kendraIds);
        Set<String> memberIdSet = new HashSet<>();
        for (String memberId : memberIds) {
            if (memberId != null) {
                memberIdSet.add(memberId);
            }
        }
        List<UnnatiIexceedCDHLead> unactionedLeads = new ArrayList<>();
        for (UnnatiIexceedCDHLead lead : cdhLeads) {
            String customerId = lead.getCustomerId();
            if (customerId != null && !memberIdSet.contains(customerId)) {
                unactionedLeads.add(lead);
            }
        }
        return unactionedLeads;
    }

    /**
     * @param kendraIds
     * @param productCode fetchCDHOverallLeads->Changed to fetch Leads Base on
     *                    product-Type
     */
    private List<UnnatiIexceedCDHLead> fetchCDHOverallLeads(List<String> kendraIds, List<String> productCode) {
        List<UnnatiIexceedCDHLead> cdhLeads = new ArrayList<>();
        if (kendraIds == null || kendraIds.isEmpty()) {
            return Collections.emptyList();
        }
        // productCode.add(Constants.OPENMARKET_LOAN_PRODUCT_CODE);
        List<UnnatiIexceedCDHLead> allLeads = unnatiIexceedCDHLeadRepo.findByKendraId(kendraIds, productCode);
        allLeads.forEach(lead -> {
            /**
             * @author Ankit.CAG Modify CDH response Product Code Base on @Unnati-Product
             *         Code
             */
            lead.setProduct(ProductCode.getUnnatiCodeByCdhCode(lead.getProduct()));
        });
        // ------END-----
        if (!allLeads.isEmpty()) {
            cdhLeads.addAll(allLeads
                    .stream()
                    .map(lead -> {
                        if(("IEXCEED").equalsIgnoreCase(lead.getSourceOfTheApplication()))
                            lead.setSourceOfTheApplication("UNNATI");
                        return lead;
                    }).collect(Collectors.toList()));
        }
        return cdhLeads;
    }

    private ApplicationMaster fetchDashboardCounts(List<String> branches, String fetchType, String userRoleKey,
                                                   Properties prop) {
        logger.debug("Entering fetchDashboardCounts with branches: {} and fetchType: {}", branches, fetchType);

        // Determine product codes based on fetchType
        List<String> productCodeList = new ArrayList<>();
        if (Constants.DASHBOARD_STATS_RENEWAL.equalsIgnoreCase(fetchType.trim())) {
            productCodeList.add(Constants.RENEWAL_PRODUCT_CODE);
            logger.debug("fetchType is RENEWAL. Using RENEWAL_LOAN_PRODUCT_CODE.");
            branches = whitelistedBranchesRepository.findRenewalWhitelistedBranches(branches);
            if (branches == null || branches.isEmpty()) {
                return new ApplicationMaster(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
            }
        }

        productCodeList.add(ProductCode.getUnnatiCodeByFetchType(fetchType));

        // Define status lists
        List<String> sourcingStatus = Arrays.asList(AppStatus.INPROGRESS.getValue(),AppStatus.BMPUSHBACK.getValue(), AppStatus.PUSHBACK.getValue(), AppStatus.CAPUSHBACK.getValue(), AppStatus.IPUSHBACK.getValue());
        List<String> bmApprovalStatus = Arrays.asList(AppStatus.PENDING.getValue());
        List<String> rpcVerificationStatus = Arrays.asList(AppStatus.APPROVED.getValue(), AppStatus.PENDINGFORRPCVERIFICATION.getValue(), AppStatus.DBKITGENERATED.getValue(), AppStatus.RPCPUSHBACK.getValue());
        List<String> caStatus = Arrays.asList(AppStatus.RPCVERIFIED.getValue());
        List<String> deviationRAStatus = Arrays.asList(AppStatus.PENDINGDEVIATION.getValue(), AppStatus.PENDINGREASSESSMENT.getValue());
        List<String> sanctionStatus = Arrays.asList(AppStatus.CACOMPLETED.getValue(), AppStatus.RESANCTION.getValue(), AppStatus.PENDINGPRESANCTION.getValue());
        List<String> disbursementInprogressStatus = Arrays.asList(AppStatus.SANCTIONED.getValue(), AppStatus.DBPUSHBACK.getValue(), AppStatus.DBKITVERIFIED.getValue());
        List<String> disbursedStatus = Arrays.asList(AppStatus.DISBURSED.getValue());
        List<String> rejectedStatus = Arrays.asList(AppStatus.REJECTED.getValue());
        List<String> recordType = Arrays.asList(Constants.CA_DEVIATION, Constants.REASSESSMENT);
        List<String> postDisbursementStatus = Arrays.asList(AppStatus.LUC.getValue(), AppStatus.LUCVERIFIED.getValue(), AppStatus.PENDINGLUCVERIFICATION.getValue());

        // Fetch dashboard counts
        try {
            logger.debug("Calling applicationMasterRepository2.fetchDashboardCount");
            return applicationMasterRepository2.fetchDashboardCount(
                    branches, sourcingStatus, bmApprovalStatus, rpcVerificationStatus,
                    caStatus, deviationRAStatus, disbursementInprogressStatus,
                    disbursedStatus, rejectedStatus, postDisbursementStatus,
                    userRoleKey, recordType, productCodeList, Constants.IEXCEED_FLAG
            );
        } catch (Exception e) {
            logger.error("Error while fetching DashboardCounts: {}", e.getMessage(), e);
            return null;
        }
    }

    private ApplicationMaster fetchCCMDashboardCounts(String fetchType, String userRoleKey,Properties prop) {
        logger.debug("Entering fetchCCMDashboardCounts fetchType: {}", fetchType);

        // Determine product codes based on fetchType
        List<String> productCodeList = new ArrayList<>();
        if (Constants.DASHBOARD_STATS_RENEWAL.equalsIgnoreCase(fetchType.trim())) {
            productCodeList.add(Constants.RENEWAL_LOAN_PRODUCT_CODE);
            logger.debug("fetchType is RENEWAL. Using RENEWAL_LOAN_PRODUCT_CODE.");
        } else if (Constants.DASHBOARD_STATS_OPENMARKET.equalsIgnoreCase(fetchType.trim())) {
            productCodeList.add(Constants.OPENMARKET_LOAN_PRODUCT_CODE);
        } else {
            productCodeList.add(Constants.UNNATI_PRODUCT_CODE);
        }

        // Define status lists for CCM role
        List<String> deviationRAStatus = Arrays.asList(AppStatus.PENDINGDEVIATION.getValue(), AppStatus.PENDINGREASSESSMENT.getValue());
        List<String> recordType = Arrays.asList(Constants.CA_DEVIATION, Constants.REASSESSMENT);

        // Fetch ccm dashboard counts
        try {
            logger.debug("Calling applicationMasterRepository2.fetchKmDashboardCount");
            return applicationMasterRepository2.fetchCCMDashboardCount(
                    deviationRAStatus, productCodeList, recordType
            );
        } catch (Exception e) {
            logger.error("Error while fetching DashboardCounts: {}", e.getMessage(), e);
            return null;
        }
    }

    private ApplicationMaster fetchKMDashboardCounts(List<String> kendraIds, String fetchType, Properties prop) {
        logger.debug("Entering fetchDashboardCounts with kendraIds: {} and fetchType: {}", kendraIds, fetchType);

        // Determine product codes based on fetchType
        List<String> productCodeList = new ArrayList<>();
        productCodeList.add(ProductCode.getUnnatiCodeByFetchType(fetchType));

        int rejectionExpiry = Integer.parseInt(prop.getProperty(CobFlagsProperties.REJECTION_EXPIRY_DAYS.getKey()));
        LocalDateTime rejectionExpiryDateTime = LocalDateTime.now().minusDays(rejectionExpiry);
        logger.debug("Rejection expiry date time calculated as: {}", rejectionExpiryDateTime);

        // Define status lists
        List<String> sourcingStatus = Arrays.asList(AppStatus.INPROGRESS.getValue(),AppStatus.BMPUSHBACK.getValue(), AppStatus.PUSHBACK.getValue(), AppStatus.CAPUSHBACK.getValue(), AppStatus.IPUSHBACK.getValue());
        List<String> bmApprovalStatus = Arrays.asList(AppStatus.PENDING.getValue());
        List<String> rpcVerificationStatus = Arrays.asList(AppStatus.APPROVED.getValue(), AppStatus.PENDINGFORRPCVERIFICATION.getValue(), AppStatus.DBKITGENERATED.getValue(), AppStatus.RPCPUSHBACK.getValue());
        List<String> caStatus = Arrays.asList(AppStatus.RPCVERIFIED.getValue());
        List<String> deviationRAStatus = Arrays.asList(AppStatus.PENDINGDEVIATION.getValue(), AppStatus.PENDINGREASSESSMENT.getValue());
        List<String> sanctionStatus = Arrays.asList(AppStatus.CACOMPLETED.getValue(), AppStatus.RESANCTION.getValue(), AppStatus.PENDINGPRESANCTION.getValue());
        List<String> disbursementInprogressStatus = Arrays.asList(AppStatus.SANCTIONED.getValue(), AppStatus.DBPUSHBACK.getValue(), AppStatus.DBKITVERIFIED.getValue());
        List<String> disbursedStatus = Arrays.asList(AppStatus.DISBURSED.getValue());
        List<String> rejectedStatus = Arrays.asList(AppStatus.REJECTED.getValue());
        List<String> postDisbursementStatus = Arrays.asList(AppStatus.LUC.getValue(), AppStatus.LUCVERIFIED.getValue(), AppStatus.PENDINGLUCVERIFICATION.getValue());

        // Fetch km dashboard counts
        try {
            logger.debug("Calling applicationMasterRepository2.fetchKmDashboardCount");
            return applicationMasterRepository2.fetchKMDashboardCount(
                    kendraIds, sourcingStatus, bmApprovalStatus, rpcVerificationStatus,
                    caStatus, deviationRAStatus, sanctionStatus, disbursementInprogressStatus,
                    disbursedStatus, rejectedStatus, postDisbursementStatus, productCodeList, rejectionExpiryDateTime
            );
        } catch (Exception e) {
            logger.error("Error while fetching DashboardCounts: {}", e.getMessage(), e);
            return null;
        }
    }

    private Page<ApplicationMaster> fetchKMDashboardApplications(
            List<String> kendraIds, String fetchType, String filterType,
            String userRole, String userRoleKey, Pageable page, Properties prop) {
        logger.debug("Fetching dashboard applications for kendraIds: {}, fetchType: {}, filterType: {}", kendraIds, fetchType, filterType);

        Page<ApplicationMaster> appMasterList = null;
        String[] filterTypeArr = filterType.trim().split("\\|");
        String mainFilter = filterTypeArr[0].toUpperCase();
        String subFilter = filterTypeArr.length > 1 ? filterTypeArr[1].toUpperCase() : "";
        Set<String> userRoles = new HashSet<>(Arrays.asList(userRole, userRoleKey));
        List<String> userRoleList = new ArrayList<>(userRoles);

        List<String> productCodes = new ArrayList<>();
        productCodes.add(ProductCode.getUnnatiCodeByFetchType(fetchType));

        logger.debug("Main filter: {}, Sub filter: {}", mainFilter, subFilter);

        switch (mainFilter) {
            case Constants.SOURCING_STRING: {
                List<String> sourcingStatus = new ArrayList<>();
                logger.info("Processing SOURCING filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    sourcingStatus.add(AppStatus.INPROGRESS.getValue());
                    logger.debug("SOURCING: Added INPROGRESS status");
                } else if (Constants.SUBMITTED.equalsIgnoreCase(subFilter)) {
                    sourcingStatus.add(AppStatus.PENDING.getValue());
                    logger.debug("SOURCING: Added PENDING status");
                } else if (Constants.PUSHBACK_STRING.equalsIgnoreCase(subFilter)) {
                    sourcingStatus.add(AppStatus.PUSHBACK.getValue());
                    sourcingStatus.add(AppStatus.IPUSHBACK.getValue());
                    sourcingStatus.add(AppStatus.BMPUSHBACK.getValue());
                    logger.debug("SOURCING: Added PUSHBACK,IPUSHBACK and BMPUSHBACK status");
                } else if (Constants.REJECTED.equalsIgnoreCase(subFilter)) {
                    sourcingStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("SOURCING: Added REJECTED status");
                } else if (AppStatus.CAPUSHBACK.getValue().equalsIgnoreCase(subFilter)) {
                    sourcingStatus.add(AppStatus.CAPUSHBACK.getValue());
                    logger.debug("SOURCING: Added CAPUSHBACK status");
                }
                if (!sourcingStatus.isEmpty()) {
                    logger.info("Fetching new dashboard applications for SOURCING with statuses: {}", sourcingStatus);
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, sourcingStatus, productCodes);
                }
                break;
            }
            case Constants.BM_APPROVAL: {
                List<String> bmApprovalStatus = new ArrayList<>();
                List<String> previousStatusBM = Collections.singletonList(AppStatus.PENDING.getValue());
                logger.info("Processing BM_APPROVAL filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    bmApprovalStatus.add(AppStatus.PENDING.getValue());
                    logger.debug("BM_APPROVAL: Added PENDING status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, bmApprovalStatus, productCodes);
                } else if (Constants.APPROVED.equalsIgnoreCase(subFilter)) {
                    bmApprovalStatus.add(AppStatus.APPROVED.getValue());
                    logger.debug("BM_APPROVAL: Added APPROVED status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, bmApprovalStatus, productCodes);
                } else if (Constants.REJECTED.equalsIgnoreCase(subFilter)) {
                    bmApprovalStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("BM_APPROVAL: Added REJECTED status");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, bmApprovalStatus, previousStatusBM, productCodes);
                } else if (Constants.PUSHBACK_STRING.equalsIgnoreCase(subFilter)) {
                    bmApprovalStatus.add(AppStatus.IPUSHBACK.getValue());
                    logger.debug("BM_APPROVAL: Added IPUSHBACK status");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, bmApprovalStatus, previousStatusBM, productCodes);
                }
                break;
            }
            case "RPC_VERIFICATION": {
                List<String> rpcVerificationStatus = new ArrayList<>();
                List<String> previousStatusRPC = Arrays.asList(AppStatus.APPROVED.getValue(), AppStatus.PENDINGFORRPCVERIFICATION.getValue());
                logger.info("Processing RPC_VERIFICATION filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    rpcVerificationStatus.add(AppStatus.APPROVED.getValue());
                    rpcVerificationStatus.add(AppStatus.PENDINGFORRPCVERIFICATION.getValue());
                    rpcVerificationStatus.add(AppStatus.RPCPUSHBACK.getValue());
                    rpcVerificationStatus.add(AppStatus.DBKITGENERATED.getValue());
                    logger.debug("RPC_VERIFICATION: Added APPROVED and PENDINGFORRPCVERIFICATION status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, rpcVerificationStatus, productCodes);
                } else if (Constants.APPROVED.equalsIgnoreCase(subFilter)) {
                    rpcVerificationStatus.add(AppStatus.RPCVERIFIED.getValue());
                    logger.debug("RPC_VERIFICATION: Added RPCVERIFIED status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, rpcVerificationStatus, productCodes);
                } else if (Constants.RPC_MAKER.equalsIgnoreCase(subFilter)) {
                    rpcVerificationStatus.add(AppStatus.APPROVED.getValue());
                    logger.debug("RPC_VERIFICATION: Added RPC_MAKER status");
                    appMasterList = applicationMasterRepository2.fetchRPCKMDashBoardApplications(
                            rpcVerificationStatus, kendraIds, page,
                            Constants.RPC_MAKER, Constants.ALL,
                            Arrays.asList(AppStatus.IPUSHBACK.getValue(), AppStatus.RPCPUSHBACK.getValue()),
                            AppStatus.IPUSHBACK.getValue(), AppStatus.RPCPUSHBACK.getValue(),
                            Arrays.asList(3, 4), "", productCodes);
                } else if (Constants.RPC_CHECKER.equalsIgnoreCase(subFilter)) {
                    rpcVerificationStatus.add(AppStatus.PENDINGFORRPCVERIFICATION.getValue());
                    logger.debug("RPC_VERIFICATION: Added RPC_CHECKER status");
                    appMasterList = applicationMasterRepository2.fetchRPCKMDashBoardApplications(
                            rpcVerificationStatus, kendraIds, page,
                            Constants.RPC_CHECKER, Constants.ALL,
                            Arrays.asList(AppStatus.IPUSHBACK.getValue(), AppStatus.RPCPUSHBACK.getValue()),
                            AppStatus.IPUSHBACK.getValue(), AppStatus.RPCPUSHBACK.getValue(),
                            Arrays.asList(4, 5), "", productCodes);
                } else if (Constants.PUSHBACK_STRING.equalsIgnoreCase(subFilter)) {
                    rpcVerificationStatus.add(AppStatus.IPUSHBACK.getValue());
                    logger.debug("RPC_VERIFICATION: Added IPUSHBACK status");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, rpcVerificationStatus, previousStatusRPC, productCodes);
                } else if (Constants.REJECTED.equalsIgnoreCase(subFilter)) {
                    rpcVerificationStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("RPC_VERIFICATION: Added REJECTED status");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, rpcVerificationStatus, previousStatusRPC, productCodes);
                } else if (Constants.DBKITVERIFICATION.equalsIgnoreCase(subFilter)) {
                    rpcVerificationStatus.add(AppStatus.DBKITGENERATED.getValue());
                    List<String> rpcDBverificationReworkStatus = Collections.singletonList(AppStatus.DBPUSHBACK.getValue());
                    logger.debug("RPC_VERIFICATION: Added DBKITGENERATED status");
                    appMasterList = applicationMasterRepository2.fetchRPCDBVerificationForKMDashboardApplications(
                            rpcVerificationStatus, kendraIds, page, Constants.ALL, rpcDBverificationReworkStatus, productCodes);
                }
                break;
            }
            case "CREDIT_ASSESSMENT": {
                List<String> caStatus = new ArrayList<>();
                List<String> previousStatusCA = Collections.singletonList(AppStatus.RPCVERIFIED.getValue());
                logger.info("Processing CREDIT_ASSESSMENT filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    caStatus.add(AppStatus.RPCVERIFIED.getValue());
                    logger.debug("CREDIT_ASSESSMENT: Added RPCVERIFIED status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, caStatus, productCodes);
                } else if (Constants.APPROVED.equalsIgnoreCase(subFilter)) {
                    caStatus.add(AppStatus.CACOMPLETED.getValue());
                    caStatus.add(AppStatus.PENDINGDEVIATION.getValue());
                    caStatus.add(AppStatus.PENDINGREASSESSMENT.getValue());
                    logger.debug("CREDIT_ASSESSMENT: Added CACOMPLETED, PENDINGDEVIATION, PENDINGREASSESSMENT status");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, caStatus, previousStatusCA, productCodes);
                } else if (Constants.REJECTED.equalsIgnoreCase(subFilter)) {
                    caStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("CREDIT_ASSESSMENT: Added REJECTED status");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, caStatus, previousStatusCA, productCodes);
                } else if (Constants.PUSHBACK_STRING.equalsIgnoreCase(subFilter)) {
                    caStatus.add(AppStatus.IPUSHBACK.getValue());
                    logger.debug("CREDIT_ASSESSMENT: Added IPUSHBACK status");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, caStatus, previousStatusCA, productCodes);
                } else if (AppStatus.CAPUSHBACK.getValue().equalsIgnoreCase(subFilter)) {
                    caStatus.add(AppStatus.CAPUSHBACK.getValue());
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, caStatus, previousStatusCA, productCodes
                    );
                }
                break;
            }
            case Constants.DEVIATION_RA: {
                List<String> deviationRAStatus = new ArrayList<>();
                List<String> previousDeviationRAStatus = Arrays.asList(AppStatus.PENDINGDEVIATION.getValue(), AppStatus.PENDINGREASSESSMENT.getValue());
                List<String> recordTypes = new ArrayList<>();
                logger.info("Processing DEVIATION_RA filter with subFilter: {}", subFilter);
                logger.info("parameters : kendra: {}, status: {}, approvalStatus:{}, ", kendraIds, deviationRAStatus, userRoleList, page, productCodes);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    deviationRAStatus.add(AppStatus.PENDINGDEVIATION.getValue());
                    deviationRAStatus.add(AppStatus.PENDINGREASSESSMENT.getValue());
                    recordTypes.add(Constants.CA_DEVIATION);
                    recordTypes.add(Constants.REASSESSMENT);
                    logger.debug("DEVIATION_RA: Added PENDINGDEVIATION, PENDINGREASSESSMENT status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(
                            page, kendraIds, deviationRAStatus, productCodes);
                } else if (Constants.CA_DEVIATION.equalsIgnoreCase(subFilter)) {
                    deviationRAStatus.add(AppStatus.PENDINGDEVIATION.getValue());
                    recordTypes.add(Constants.CA_DEVIATION);
                    logger.debug("DEVIATION_RA: Added CA_DEVIATION status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(
                            page, kendraIds, deviationRAStatus, productCodes);
                } else if (Constants.REASSESSMENT.equalsIgnoreCase(subFilter)) {
                    deviationRAStatus.add(AppStatus.PENDINGREASSESSMENT.getValue());
                    recordTypes.add(Constants.REASSESSMENT);
                    logger.debug("DEVIATION_RA: Added REASSESSMENT status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(
                            page, kendraIds, deviationRAStatus, productCodes);
                } else if (Constants.APPROVED.equalsIgnoreCase(subFilter)) {
                    deviationRAStatus.add(AppStatus.CACOMPLETED.getValue());
                    deviationRAStatus.add(AppStatus.RESANCTION.getValue());
                    logger.debug("DEVIATION_RA: Added CACOMPLETED, RESANCTION status");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, deviationRAStatus, previousDeviationRAStatus, productCodes);
                } else if (Constants.PUSHBACK_STRING.equalsIgnoreCase(subFilter)) {
                    deviationRAStatus.add(AppStatus.IPUSHBACK.getValue());
                    logger.debug("DEVIATION_RA: Added IPUSHBACK status");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, deviationRAStatus, previousDeviationRAStatus, productCodes);
                } else if (Constants.REJECTED.equalsIgnoreCase(subFilter)) {
                    deviationRAStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("DEVIATION_RA: Added REJECTED status");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, deviationRAStatus, previousDeviationRAStatus, productCodes);
                }
                break;
            }
            case "SANCTION": {
                List<String> sanctionStatus = new ArrayList<>();
                List<String> previousStatusSanction = Arrays.asList(AppStatus.CACOMPLETED.getValue(), AppStatus.RESANCTION.getValue());
                logger.info("Processing SANCTION filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    sanctionStatus.add(AppStatus.CACOMPLETED.getValue());
                    logger.debug("SANCTION: Added CACOMPLETED status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds,
                            sanctionStatus, productCodes);
                } else if (Constants.ADD_PRODUCT_PENDING.equalsIgnoreCase(subFilter)) {
                    /**
                     * @author Ankit.CAG
                     * @Fetching Additional Product's Leads From CDH & Initialize field
                     *           (unnatiProductCode) for @Unnati Product Codes. After fetching CDH
                     *           leads, the product field is converted to the corresponding Unnati
                     *           product code format.(FOR RESPONSE)
                     */
                    String unnatiProductCode = "";
                    logger.debug("KM login subFilter ADD_PRODUCT_PENDING");

                    logger.debug("subFilter ADD_PRODUCT_PENDING");

                    unnatiProductCode = ProductCode.getUnnatiCodeByFetchType(fetchType);
                    productCodes.add(ProductCode.getCdhCodeByFetchType(fetchType));
                    logger.debug("unnati ProductCode :={} Maitri productCodes :={} kendraIds :={}", unnatiProductCode,
                            productCodes, kendraIds);
                    final String finalUnnatiProductCode = unnatiProductCode;
                    try {
                        List<AdditionalLoanLeadProjection> fetchAdditionalLoanLeads = unnatiIexceedCDHLeadRepo
                                .fetchAdditionalLoanLeadsbykendraIds(kendraIds, productCodes);
                        logger.debug("subFilter fetchAdditionalLoanLeads" + fetchAdditionalLoanLeads);
                        List<ApplicationMaster> appMasters = new ArrayList<>();

                        fetchAdditionalLoanLeads.stream().forEach(e -> {
                            appMasters.add(ApplicationMaster.builder().applicationId(e.getUnnatiApplicationId() + "")
                                    .customerId(e.getCustomerId() == null ? null : new BigDecimal(e.getCustomerId()))
                                    .customerName(e.getCustomerName()).branchName(e.getBranchName()).rpcTAT(e.getRpcTAT())
                                    .loanAmount(e.getLoanAmount()).applicationStatus(e.getApplicationStatus())
                                    /*
                                     * Field used to distinguish whether the ApplicationMaster object data comes
                                     * from @CDH or @PostgreSQL.
                                     */
                                    .cdhFLag("Y").productCode(finalUnnatiProductCode).kendraId(e.getKendraId()).build());
                        });
                        logger.debug("List<ApplicationMaster> appMasters =" + appMasters);
                        appMasterList = new PageImpl<>(appMasters, page, appMasters.size());
                    } catch (Exception e) {
                        logger.error("SANCTION Stage Error " + e);
                    }
                    // --------END--------
                } else if (Constants.APPROVED.equalsIgnoreCase(subFilter)) {
                    sanctionStatus.add(AppStatus.SANCTIONED.getValue());
                    logger.debug("SANCTION: Added SANCTIONED status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, sanctionStatus, productCodes);
                } else if (Constants.PUSHBACK_STRING.equalsIgnoreCase(subFilter)) {
                    sanctionStatus.add(AppStatus.IPUSHBACK.getValue());
                    logger.debug("SANCTION: Added IPUSHBACK status");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, sanctionStatus, previousStatusSanction, productCodes);
                } else if (Constants.REJECTED.equalsIgnoreCase(subFilter)) {
                    sanctionStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("SANCTION: Added REJECTED status");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, sanctionStatus, previousStatusSanction, productCodes);
                } else if (Constants.RESANCTION.equalsIgnoreCase(subFilter)) {
                    sanctionStatus.add(AppStatus.RESANCTION.getValue());
                    logger.debug("SANCTION: Added RESANCTION status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, sanctionStatus, productCodes);
                } else if (Constants.PRESANCTION.equalsIgnoreCase(subFilter)) {
                    sanctionStatus.add(AppStatus.PENDINGPRESANCTION.getValue());
                    logger.debug("PRESANCTION: Added PENDINGPRESANCTION status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(
                            page, kendraIds, sanctionStatus, productCodes
                    );
                }
                break;
            }
            case "DISBURSEMENT_INPROGRESS": {
                List<String> disbursementInprogressStatus = new ArrayList<>();
                List<String> previousStatusDisbursementIP = Collections.singletonList(AppStatus.SANCTIONED.getValue());
                logger.info("Processing DISBURSEMENT_INPROGRESS filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.SANCTIONED.getValue());
                    logger.debug("DISBURSEMENT_INPROGRESS: Added SANCTIONED status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, disbursementInprogressStatus, productCodes);
                } else if (Constants.APPROVED.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.DISBURSED.getValue());
                    logger.debug("DISBURSEMENT_INPROGRESS: Added DISBURSED status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, disbursementInprogressStatus, productCodes);
                } else if (Constants.PUSHBACK_STRING.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.IPUSHBACK.getValue());
                    logger.debug("DISBURSEMENT_INPROGRESS: Added IPUSHBACK status");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, disbursementInprogressStatus, previousStatusDisbursementIP, productCodes);
                } else if (Constants.REJECTED.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("DISBURSEMENT_INPROGRESS: Added REJECTED status");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, disbursementInprogressStatus, previousStatusDisbursementIP, productCodes);
                } else if (Constants.DBCAPUSHBACK.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.RPCVERIFIED.getValue());
                    logger.debug("DISBURSEMENT_INPROGRESS: Added RPCVERIFIED status for DBCAPUSHBACK");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, disbursementInprogressStatus, previousStatusDisbursementIP, productCodes);
                } else if (Constants.BANKUPDATEPUSHBACK.equalsIgnoreCase(subFilter)) {
                    logger.warn("DISBURSEMENT_INPROGRESS: BANKUPDATEPUSHBACK case not implemented");
                } else if (Constants.DBSANCTIONPUSHBACK.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.RESANCTION.getValue());
                    logger.debug("DISBURSEMENT_INPROGRESS: Added RESANCTION status for DBSANCTIONPUSHBACK");
                    appMasterList = applicationMasterRepository2.fetchKMDashboardRejectionPushbackApplications(
                            page, kendraIds, disbursementInprogressStatus, previousStatusDisbursementIP, productCodes);
                } else if (Constants.DBKITVERIFICATION.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.DBKITGENERATED.getValue());
                    logger.debug("DISBURSEMENT_INPROGRESS: Added DBKITGENERATED status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, disbursementInprogressStatus, productCodes);
                } else if (Constants.DBPUSHBACK.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.DBPUSHBACK.getValue());
                    logger.debug("DBPUSHBACK : added DBPUSHBACK status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, disbursementInprogressStatus, productCodes);
                } else if (Constants.DB_KIT_VERIFIED_STATUS.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.DBKITVERIFIED.getValue());
                    logger.debug("DB_KIT_VERIFIED_STATUS : added DBKITVERIFIED status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, disbursementInprogressStatus, productCodes);
                }
                break;
            }
            case "DISBURSED": {
                List<String> disbursedStatus = new ArrayList<>();
                logger.info("Processing DISBURSED filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    disbursedStatus.add(AppStatus.DISBURSED.getValue());
                    logger.debug("DISBURSED: Added DISBURSED status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, disbursedStatus, productCodes);
                } else if (Constants.APPROVED.equalsIgnoreCase(subFilter)) {
                    disbursedStatus.add(AppStatus.PENDINGSERVICECALL.getValue());
                    logger.debug("DISBURSED: Added PENDINGSERVICECALL status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, disbursedStatus, productCodes);
                }
                break;
            }

            case "POST_DISBURSEMENT": {
                List<String> postDisbursedStatus = new ArrayList<>();
                logger.info("Processing POST_DISBURSEMENT filter with subFilter: {}", subFilter);
                if (Constants.LUC.equalsIgnoreCase(subFilter)) {
                    postDisbursedStatus.add(AppStatus.LUC.getValue());
                    logger.debug("POST_DISBURSEMENT: Added LUC status");
                    appMasterList = applicationMasterRepository2.lucKMDashboardApplications(page, kendraIds, postDisbursedStatus, productCodes);

                } else if (Constants.PENDINGLUCVERIFICATION.equalsIgnoreCase(subFilter)) {
                    postDisbursedStatus.add(AppStatus.PENDINGLUCVERIFICATION.getValue());
                    logger.debug("POST_DISBURSEMENT: Added PENDINGLUCVERIFICATION status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, postDisbursedStatus, productCodes);
                } else if (Constants.LUCVERIFIED.equalsIgnoreCase(subFilter)) {
                    postDisbursedStatus.add(AppStatus.LUCVERIFIED.getValue());
                    logger.debug("POST_DISBURSEMENT: Added EXIT status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardApplications(page, kendraIds, postDisbursedStatus, productCodes);
                }
                break;
            }
            case "REJECTED":
                int rejectionExpiry = Integer.parseInt(prop.getProperty(CobFlagsProperties.REJECTION_EXPIRY_DAYS.getKey()));
                LocalDateTime rejectionExpiryDateTime = LocalDateTime.now().minusDays(rejectionExpiry);
                logger.debug("Rejection expiry date time calculated as: {}", rejectionExpiryDateTime);
                logger.info("Processing REJECTED filter with subFilter");
                List<String> rejectedStatus = new ArrayList<>();
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    rejectedStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("REJECTED: Added REJECTED status");
                    appMasterList = applicationMasterRepository2.fetchNewKMDashboardRejectedApplications(
                            page, kendraIds, rejectedStatus, productCodes, rejectionExpiryDateTime
                    );
                }
                break;
            default:
                logger.error("Invalid filter type for dashboardApplications: {}", filterType);
                break;
        }

        logger.info("Returning appMasterList: {}", appMasterList != null ? appMasterList.getTotalElements() : 0);
        return appMasterList;

    }

    private Page<ApplicationMaster> fetchDashboardApplications(
            List<String> branches, String fetchType, String filterType,
            String userRole, String userRoleKey, Pageable page, Properties prop) {

        logger.debug("Fetching dashboard applications for branches: {}, fetchType: {}, filterType: {}", branches, fetchType, filterType);

        Page<ApplicationMaster> appMasterList = null;
        String[] filterTypeArr = filterType.trim().split("\\|");
        String mainFilter = filterTypeArr[0].toUpperCase();
        String subFilter = filterTypeArr.length > 1 ? filterTypeArr[1].toUpperCase() : "";
        Set<String> userRoles = new HashSet<>(Arrays.asList(userRole, userRoleKey));
        List<String> userRoleList = new ArrayList<>(userRoles);


        List<String> productCodes = new ArrayList<>();
        if (Constants.DASHBOARD_STATS_RENEWAL.equalsIgnoreCase(fetchType.trim())) {
            productCodes.add(Constants.RENEWAL_PRODUCT_CODE);
            logger.info("fetchType is DASHBOARD_STATS_RENEWAL. Using RENEWAL_LOAN_PRODUCT_CODE.");
            branches = whitelistedBranchesRepository.findRenewalWhitelistedBranches(branches);
            if (branches == null || branches.isEmpty()) {
                return Page.empty(page);
            }
        }
        productCodes.add(ProductCode.getUnnatiCodeByFetchType(fetchType));

        logger.debug("Main filter: {}, Sub filter: {}", mainFilter, subFilter);

        switch (mainFilter) {
            case Constants.SOURCING_STRING: {
                List<String> sourcingStatus = new ArrayList<>();
                logger.info("Processing SOURCING filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    sourcingStatus.add(AppStatus.INPROGRESS.getValue());
                    logger.debug("SOURCING: Added INPROGRESS status");
                } else if (Constants.SUBMITTED.equalsIgnoreCase(subFilter)) {
                    sourcingStatus.add(AppStatus.PENDING.getValue());
                    logger.debug("SOURCING: Added PENDING status");
                } else if (Constants.PUSHBACK_STRING.equalsIgnoreCase(subFilter)) {
                    sourcingStatus.add(AppStatus.PUSHBACK.getValue());
                    sourcingStatus.add(AppStatus.IPUSHBACK.getValue());
                    logger.debug("SOURCING: Added PUSHBACK,IPUSHBACK status");
                } else if (Constants.REJECTED.equalsIgnoreCase(subFilter)) {
                    sourcingStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("SOURCING: Added REJECTED status");
                } else if (AppStatus.CAPUSHBACK.getValue().equalsIgnoreCase(subFilter)) {
                    sourcingStatus.add(AppStatus.CAPUSHBACK.getValue());
                    logger.debug("SOURCING: Added CAPUSHBACK status");
                }
                if (!sourcingStatus.isEmpty()) {
                    logger.info("Fetching new dashboard applications for SOURCING with statuses: {}", sourcingStatus);
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(page, branches, sourcingStatus, productCodes);
                }
                break;
            }
            case Constants.BM_APPROVAL: {
                List<String> bmApprovalStatus = new ArrayList<>();
                List<String> previousStatusBM = Collections.singletonList(AppStatus.PENDING.getValue());
                logger.info("Processing BM_APPROVAL filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    bmApprovalStatus.add(AppStatus.PENDING.getValue());
                    logger.debug("BM_APPROVAL: Added PENDING status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(page, branches, bmApprovalStatus, productCodes);
                } else if (Constants.APPROVED.equalsIgnoreCase(subFilter)) {
                    bmApprovalStatus.add(AppStatus.APPROVED.getValue());
                    logger.debug("BM_APPROVAL: Added APPROVED status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(page, branches, bmApprovalStatus, productCodes);
                } else if (Constants.REJECTED.equalsIgnoreCase(subFilter)) {
                    bmApprovalStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("BM_APPROVAL: Added REJECTED status");
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, bmApprovalStatus, previousStatusBM, productCodes);
                } else if (Constants.PUSHBACK_STRING.equalsIgnoreCase(subFilter)) {
                    bmApprovalStatus.add(AppStatus.IPUSHBACK.getValue());
                    logger.debug("BM_APPROVAL: Added IPUSHBACK status");
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, bmApprovalStatus, previousStatusBM, productCodes);
                }
                break;
            }
            case "RPC_VERIFICATION": {
                List<String> rpcVerificationStatus = new ArrayList<>();
                List<String> previousStatusRPC = Arrays.asList(AppStatus.APPROVED.getValue(), AppStatus.PENDINGFORRPCVERIFICATION.getValue());
                logger.info("Processing RPC_VERIFICATION filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    rpcVerificationStatus.add(AppStatus.APPROVED.getValue());
                    rpcVerificationStatus.add(AppStatus.PENDINGFORRPCVERIFICATION.getValue());
                    rpcVerificationStatus.add(AppStatus.RPCPUSHBACK.getValue());
                    rpcVerificationStatus.add(AppStatus.DBKITGENERATED.getValue());
                    logger.debug("RPC_VERIFICATION: Added APPROVED and PENDINGFORRPCVERIFICATION status");
                    appMasterList = applicationMasterRepository2.fetchRPCNewDashboardApplications(page, branches, rpcVerificationStatus, productCodes, Constants.IEXCEED_FLAG);
                } else if (Constants.APPROVED.equalsIgnoreCase(subFilter)) {
                    rpcVerificationStatus.add(AppStatus.RPCVERIFIED.getValue());
                    logger.debug("RPC_VERIFICATION: Added RPCVERIFIED status");
                    appMasterList = applicationMasterRepository2.fetchRPCNewDashboardApplications(page, branches, rpcVerificationStatus, productCodes, Constants.IEXCEED_FLAG);
                } else if (Constants.RPC_MAKER.equalsIgnoreCase(subFilter)) {
                    rpcVerificationStatus.add(AppStatus.APPROVED.getValue());
                    logger.debug("RPC_VERIFICATION: Added RPC_MAKER status");
                    appMasterList = applicationMasterRepository.fetchDashBoardApplications(
                            rpcVerificationStatus, branches, page,
                            Constants.RPC_MAKER, Constants.ALL,
                            Arrays.asList(AppStatus.IPUSHBACK.getValue(), AppStatus.RPCPUSHBACK.getValue()),
                            AppStatus.IPUSHBACK.getValue(), AppStatus.RPCPUSHBACK.getValue(), "", Constants.IEXCEED_FLAG);
                } else if (Constants.RPC_CHECKER.equalsIgnoreCase(subFilter)) {
                    rpcVerificationStatus.add(AppStatus.PENDINGFORRPCVERIFICATION.getValue());
                    logger.debug("RPC_VERIFICATION: Added RPC_CHECKER status");
                    appMasterList = applicationMasterRepository.fetchDashBoardApplications(
                            rpcVerificationStatus, branches, page,
                            Constants.RPC_CHECKER, Constants.ALL,
                            Arrays.asList(AppStatus.IPUSHBACK.getValue(), AppStatus.RPCPUSHBACK.getValue()),
                            AppStatus.IPUSHBACK.getValue(), AppStatus.RPCPUSHBACK.getValue(), "", Constants.IEXCEED_FLAG);
                } else if (Constants.PUSHBACK_STRING.equalsIgnoreCase(subFilter)) {
                    rpcVerificationStatus.add(AppStatus.IPUSHBACK.getValue());
                    logger.debug("RPC_VERIFICATION: Added IPUSHBACK status");
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, rpcVerificationStatus, previousStatusRPC, productCodes);
                } else if (Constants.REJECTED.equalsIgnoreCase(subFilter)) {
                    rpcVerificationStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("RPC_VERIFICATION: Added REJECTED status");
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, rpcVerificationStatus, previousStatusRPC, productCodes);
                } else if (Constants.DBKITVERIFICATION.equalsIgnoreCase(subFilter)) {
                    rpcVerificationStatus.add(AppStatus.DBKITGENERATED.getValue());
                    List<String> rpcDBverificationReworkStatus = Collections.singletonList(AppStatus.DBPUSHBACK.getValue());
                    logger.debug("RPC_VERIFICATION: Added DBKITGENERATED status");
                    appMasterList = applicationMasterRepository2.fetchRPCDBVerificationDashboardApplications(
                            rpcVerificationStatus, branches, page, Constants.ALL, rpcDBverificationReworkStatus);
                }
                break;
            }
            case "CREDIT_ASSESSMENT": {
                List<String> caStatus = new ArrayList<>();
                List<String> previousStatusCA = Collections.singletonList(AppStatus.RPCVERIFIED.getValue());
                logger.info("Processing CREDIT_ASSESSMENT filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    caStatus.add(AppStatus.RPCVERIFIED.getValue());
                    List<String> previousStage = Arrays.asList(AppStatus.PENDINGDEVIATION.getValue(),
                            AppStatus.PENDINGREASSESSMENT.getValue(), AppStatus.CACOMPLETED.getValue(),
                            AppStatus.RESANCTION.getValue(), AppStatus.SANCTIONED.getValue(), AppStatus.PENDINGPRESANCTION.getValue(), AppStatus.DBPUSHBACK.getValue());
                    logger.debug("CREDIT_ASSESSMENT: Added RPCVERIFIED status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardFreshApplications(
                            page, branches, caStatus, previousStage, productCodes);
                } else if (Constants.APPROVED.equalsIgnoreCase(subFilter)) {
                    caStatus.add(AppStatus.CACOMPLETED.getValue());
                    caStatus.add(AppStatus.PENDINGDEVIATION.getValue());
                    caStatus.add(AppStatus.PENDINGREASSESSMENT.getValue());
                    caStatus.add(AppStatus.PENDINGPRESANCTION.getValue());
                    logger.debug("CREDIT_ASSESSMENT: Added CACOMPLETED, PENDINGDEVIATION, PENDINGREASSESSMENT status");
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, caStatus, previousStatusCA, productCodes);
                } else if (Constants.REJECTED.equalsIgnoreCase(subFilter)) {
                    caStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("CREDIT_ASSESSMENT: Added REJECTED status");
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, caStatus, previousStatusCA, productCodes);
                } else if (Constants.PUSHBACK_STRING.equalsIgnoreCase(subFilter)) {
                    caStatus.add(AppStatus.RPCVERIFIED.getValue());
                    List<String> previousStage = Arrays.asList(AppStatus.PENDINGDEVIATION.getValue(),
                            AppStatus.PENDINGREASSESSMENT.getValue(), AppStatus.CACOMPLETED.getValue(),
                            AppStatus.RESANCTION.getValue(), AppStatus.SANCTIONED.getValue(), AppStatus.PENDINGPRESANCTION.getValue(), AppStatus.DBPUSHBACK.getValue());
                    logger.debug("CREDIT_ASSESSMENT: Added RPCVERIFIED status");
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, caStatus, previousStage, productCodes);
                }
                break;
            }
            case Constants.DEVIATION_RA: {
                List<String> deviationRAStatus = new ArrayList<>();
                List<String> previousDeviationRAStatus = Arrays.asList(AppStatus.PENDINGDEVIATION.getValue(), AppStatus.PENDINGREASSESSMENT.getValue());
                List<String> recordTypes = new ArrayList<>();
                logger.info("Processing DEVIATION_RA filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    deviationRAStatus.add(AppStatus.PENDINGDEVIATION.getValue());
                    recordTypes.add(Constants.CA_DEVIATION);
                    logger.debug("DEVIATION_RA: Added PENDINGDEVIATION, PENDINGREASSESSMENT status");
                    logger.debug("fetchDeviationRADashboardApplication parameters: {}, {}, {}, {}, {}, {}", branches, deviationRAStatus, recordTypes, userRoleList, page, productCodes);
                    appMasterList = applicationMasterRepository2.fetchDeviationRADashboardApplication(
                            branches, deviationRAStatus, userRoleList, recordTypes, page, productCodes);
                } else if (Constants.CA_DEVIATION.equalsIgnoreCase(subFilter)) {
                    deviationRAStatus.add(AppStatus.PENDINGDEVIATION.getValue());
                    recordTypes.add(Constants.CA_DEVIATION);
                    logger.debug("DEVIATION_RA: Added CA_DEVIATION status");
                    logger.debug("fetchDeviationRADashboardApplication parameters: {}, {}, {}, {}, {}, {}", branches, deviationRAStatus, recordTypes, userRoleList, page, productCodes);
                    appMasterList = applicationMasterRepository2.fetchDeviationRADashboardApplication(
                            branches, deviationRAStatus, userRoleList, recordTypes, page, productCodes);
                } else if (Constants.REASSESSMENT.equalsIgnoreCase(subFilter)) {
                    deviationRAStatus.add(AppStatus.PENDINGREASSESSMENT.getValue());
                    recordTypes.add(Constants.REASSESSMENT);
                    logger.debug("DEVIATION_RA: Added REASSESSMENT status");

                    if(Constants.CCM.equalsIgnoreCase(userRoleKey)){
                        logger.debug("fetchReassessmentDashboardApplicationForCCM parameters: {}, {}, {}, {}, {}",  deviationRAStatus, recordTypes, userRoleList, page, productCodes);
                        appMasterList = applicationMasterRepository2.fetchReassessmentDashboardApplicationForCCM(
                                deviationRAStatus, recordTypes, page, productCodes);
                    }else {
                    logger.debug("fetchDeviationRADashboardApplication parameters: {}, {}, {}, {}, {}, {}", branches, deviationRAStatus, recordTypes, userRoleList, page, productCodes);
                        appMasterList = applicationMasterRepository2.fetchDeviationRADashboardApplication(
                            branches, deviationRAStatus, userRoleList, recordTypes, page, productCodes);
                    }
                } else if (Constants.APPROVED.equalsIgnoreCase(subFilter)) {
                    deviationRAStatus.add(AppStatus.CACOMPLETED.getValue());
                    deviationRAStatus.add(AppStatus.RESANCTION.getValue());
                    logger.debug("DEVIATION_RA: Added CACOMPLETED, RESANCTION status");
                    logger.debug("fetchDashboardRejectionPushbackApplications params: {},{}, {}, {}, {}", page, branches, deviationRAStatus, previousDeviationRAStatus, productCodes);
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, deviationRAStatus, previousDeviationRAStatus, productCodes);
                } else if (Constants.PUSHBACK_STRING.equalsIgnoreCase(subFilter)) {
                    deviationRAStatus.add(AppStatus.IPUSHBACK.getValue());
                    logger.debug("DEVIATION_RA: Added IPUSHBACK status");
                    logger.debug("fetchDashboardRejectionPushbackApplications params: {},{}, {}, {}, {}", page, branches, deviationRAStatus, previousDeviationRAStatus, productCodes);
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, deviationRAStatus, previousDeviationRAStatus, productCodes);
                } else if (Constants.REJECTED.equalsIgnoreCase(subFilter)) {
                    deviationRAStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("DEVIATION_RA: Added REJECTED status");
                    logger.debug("fetchDashboardRejectionPushbackApplications params: {},{}, {}, {}, {}", page, branches, deviationRAStatus, previousDeviationRAStatus, productCodes);
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, deviationRAStatus, previousDeviationRAStatus, productCodes);
                }
                break;
            }
            case "SANCTION": {
                List<String> sanctionStatus = new ArrayList<>();
                List<String> previousStatusSanction = Arrays.asList(AppStatus.CACOMPLETED.getValue(), AppStatus.RESANCTION.getValue());
                logger.info("Processing SANCTION filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    sanctionStatus.add(AppStatus.CACOMPLETED.getValue());
                    logger.debug("SANCTION: Added CACOMPLETED status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardSanctionApplications(page, branches,
                            sanctionStatus, userRoleKey, productCodes);
                } else if (Constants.ADD_PRODUCT_PENDING.equalsIgnoreCase(subFilter)) {

                    if(ProductCode.isAdditionalFetchType(fetchType)) {
                    /**
                     * @author Ankit.CAG
                     * @Fetching Additional Product's Leads From CDH & Initialize field
                     *           (unnatiProductCode) for @Unnati Product Codes. After fetching CDH
                     *           leads, the product field is converted to the corresponding Unnati
                     *           product code format.(FOR RESPONSE)
                     */
                    String unnatiProductCode = "";
                    unnatiProductCode = ProductCode.getUnnatiCodeByFetchType(fetchType);
                    productCodes.add(ProductCode.getCdhCodeByFetchType(fetchType));

                    logger.debug("unnati ProductCode :={} Maitri productCodes :={} Branches :={}", unnatiProductCode,
                            productCodes, branches);
                    final String finalUnnatiProductCode = unnatiProductCode;
                    try {
                        List<AdditionalLoanLeadProjection> fetchAdditionalLoanLeads = unnatiIexceedCDHLeadRepo
                                .fetchAdditionalLoanLeads(branches, productCodes);
                        logger.debug("subFilter fetchAdditionalLoanLeads" + fetchAdditionalLoanLeads);
                        List<ApplicationMaster> appMasters = new ArrayList<>();

                        fetchAdditionalLoanLeads.stream().forEach(e -> {
                            appMasters.add(ApplicationMaster.builder().applicationId(e.getUnnatiApplicationId() + "")
                                    .customerId(e.getCustomerId() == null ? null : new BigDecimal(e.getCustomerId()))
                                    .memberId(e.getCustomerId() == null ? " " : e.getCustomerId() + "")
                                    .customerName(e.getCustomerName() + " ")
                                    .branchName(e.getBranchName() == null ? "" : e.getBranchName())
                                    .rpcTAT(e.getRpcTAT() + "")
                                    .loanAmount(setLoanAmtIfRestartLoan(e.getLoanAmount(), finalUnnatiProductCode, e.getCrtApprovedAmt()))
                                    .applicationStatus(e.getApplicationStatus() + " ").currentStage("SANCTION")
                                    .cdhUnnatiStatus(e.getCdhUnnatiStatus() + "").kendraId(e.getKendraId() + " ")
                                    .kendraName(e.getKendraName() + " ")
                                    /*
                                     * Field used to distinguish whether the ApplicationMaster object data comes
                                     * from @CDH or @PostgreSQL.
                                     */
                                    .cdhFLag("Y").productCode(finalUnnatiProductCode + "").build());
                        });
                        logger.debug("List<ApplicationMaster> appMasters =" + appMasters);
                        appMasterList = new PageImpl<>(appMasters, page, appMasters.size());
                    } catch (Exception e) {
                        logger.error("SANCTION Stage Error " + e);
                    }
                    }else{
                        appMasterList = new PageImpl<>(Collections.emptyList(), page, 0);
                    }
                    // --------END--------
                } else if (Constants.APPROVED.equalsIgnoreCase(subFilter)) {
                    sanctionStatus.add(AppStatus.SANCTIONED.getValue());
                    logger.debug("SANCTION: Added SANCTIONED status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(page, branches, sanctionStatus, productCodes);
                } else if (Constants.PUSHBACK_STRING.equalsIgnoreCase(subFilter)) {
                    sanctionStatus.add(AppStatus.IPUSHBACK.getValue());
                    logger.debug("SANCTION: Added IPUSHBACK status");
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, sanctionStatus, previousStatusSanction, productCodes);
                } else if (Constants.REJECTED.equalsIgnoreCase(subFilter)) {
                    sanctionStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("SANCTION: Added REJECTED status");
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, sanctionStatus, previousStatusSanction, productCodes);
                } else if (Constants.RESANCTION.equalsIgnoreCase(subFilter)) {
                    sanctionStatus.add(AppStatus.RESANCTION.getValue());
                    logger.debug("SANCTION: Added RESANCTION status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardResanctionApplications(page, branches, sanctionStatus, userRoleKey, productCodes);
                } else if (Constants.PRESANCTION.equalsIgnoreCase(subFilter)) {
                    sanctionStatus.add(AppStatus.PENDINGPRESANCTION.getValue());
                    logger.debug("PRESANCTION: Added PENDINGPRESANCTION status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(
                            page, branches, sanctionStatus, productCodes
                    );
                }
                break;
            }
            case "DISBURSEMENT_INPROGRESS": {
                List<String> disbursementInprogressStatus = new ArrayList<>();
                List<String> previousStatusDisbursementIP = Collections.singletonList(AppStatus.SANCTIONED.getValue());
                logger.info("Processing DISBURSEMENT_INPROGRESS filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.SANCTIONED.getValue());
                    logger.debug("DISBURSEMENT_INPROGRESS: Added SANCTIONED status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(page, branches, disbursementInprogressStatus, productCodes);
                } else if (Constants.APPROVED.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.DISBURSED.getValue());
                    logger.debug("DISBURSEMENT_INPROGRESS: Added DISBURSED status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(page, branches, disbursementInprogressStatus, productCodes);
                } else if (Constants.PUSHBACK_STRING.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.IPUSHBACK.getValue());
                    logger.debug("DISBURSEMENT_INPROGRESS: Added IPUSHBACK status");
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, disbursementInprogressStatus, previousStatusDisbursementIP, productCodes);
                } else if (Constants.REJECTED.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("DISBURSEMENT_INPROGRESS: Added REJECTED status");
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, disbursementInprogressStatus, previousStatusDisbursementIP, productCodes);
                } else if (Constants.DBCAPUSHBACK.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.RPCVERIFIED.getValue());
                    logger.debug("DISBURSEMENT_INPROGRESS: Added RPCVERIFIED status for DBCAPUSHBACK");
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, disbursementInprogressStatus, previousStatusDisbursementIP, productCodes);
                } else if (Constants.BANKUPDATEPUSHBACK.equalsIgnoreCase(subFilter)) {
                    logger.warn("DISBURSEMENT_INPROGRESS: BANKUPDATEPUSHBACK case not implemented");
                } else if (Constants.DBSANCTIONPUSHBACK.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.RESANCTION.getValue());
                    logger.debug("DISBURSEMENT_INPROGRESS: Added RESANCTION status for DBSANCTIONPUSHBACK");
                    appMasterList = applicationMasterRepository2.fetchDashboardRejectionPushbackApplications(
                            page, branches, disbursementInprogressStatus, previousStatusDisbursementIP, productCodes);
                } else if (Constants.DBKITVERIFICATION.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.DBKITGENERATED.getValue());
                    logger.debug("DISBURSEMENT_INPROGRESS: Added DBKITGENERATED status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(page, branches, disbursementInprogressStatus, productCodes);
                } else if (Constants.DBPUSHBACK.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.DBPUSHBACK.getValue());
                    logger.debug("DBPUSHBACK : added DBPUSHBACK status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(page, branches, disbursementInprogressStatus, productCodes);
                } else if (Constants.DB_KIT_VERIFIED_STATUS.equalsIgnoreCase(subFilter)) {
                    disbursementInprogressStatus.add(AppStatus.DBKITVERIFIED.getValue());
                    logger.debug("DB_KIT_VERIFIED_STATUS : added DBKITVERIFIED status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(page, branches, disbursementInprogressStatus, productCodes);
                }
                break;
            }
            case "DISBURSED": {
                List<String> disbursedStatus = new ArrayList<>();
                logger.info("Processing DISBURSED filter with subFilter: {}", subFilter);
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    disbursedStatus.add(AppStatus.DISBURSED.getValue());
                    logger.debug("DISBURSED: Added DISBURSED status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(page, branches, disbursedStatus, productCodes);
                } else if (Constants.APPROVED.equalsIgnoreCase(subFilter)) {
                    disbursedStatus.add(AppStatus.PENDINGSERVICECALL.getValue());
                    logger.debug("DISBURSED: Added PENDINGSERVICECALL status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(page, branches, disbursedStatus, productCodes);
                }
                break;
            }
            case "POST_DISBURSEMENT":
                List<String> postDisbursedStatus = new ArrayList<>();
                logger.info("Processing POST_DISBURSEMENT filter with subFilter: {}", subFilter);
                if (Constants.LUC.equalsIgnoreCase(subFilter)) {
                    postDisbursedStatus.add(AppStatus.LUC.getValue());
                    logger.debug("POST_DISBURSEMENT: Added LUC status");
                    appMasterList = applicationMasterRepository2.fetchLUCDashboardApplications(page, branches, postDisbursedStatus, productCodes);
                } else if (Constants.PENDINGLUCVERIFICATION.equalsIgnoreCase(subFilter)) {
                    postDisbursedStatus.add(AppStatus.PENDINGLUCVERIFICATION.getValue());
                    logger.debug("POST_DISBURSEMENT: Added PENDINGLUCVERIFICATION status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(page, branches, postDisbursedStatus, productCodes);
                } else if (Constants.LUCVERIFIED.equalsIgnoreCase(subFilter)) {
                    postDisbursedStatus.add(AppStatus.LUCVERIFIED.getValue());
                    logger.debug("POST_DISBURSEMENT: Added EXIT status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(page, branches, postDisbursedStatus, productCodes);
                }
                break;
            case "REJECTED":
                logger.warn("REJECTED case not implemented");
                List<String> rejectedStatus = new ArrayList<>();
                if (Constants.PENDING.equalsIgnoreCase(subFilter)) {
                    rejectedStatus.add(AppStatus.REJECTED.getValue());
                    logger.debug("REJECTED: Added REJECTED status");
                    appMasterList = applicationMasterRepository2.fetchNewDashboardApplications(
                            page, branches, rejectedStatus, productCodes
                    );
                }
                break;
            default:
                logger.error("Invalid filter type for dashboardApplications: {}", filterType);
                break;
        }

        logger.info("Returning appMasterList: {}", appMasterList != null ? appMasterList.getTotalElements() : 0);
        return appMasterList;
    }

    private BigDecimal setLoanAmtIfRestartLoan(BigDecimal loanAmount, String productCode, BigDecimal crtApprovedAmt) {
        if (null == loanAmount) {
            return BigDecimal.ZERO;
        } else if (null != productCode && productCode.equalsIgnoreCase(ProductCode.UNNATI_RESTART.getUnnatiCode())) {
            return null != crtApprovedAmt ? crtApprovedAmt : BigDecimal.ZERO;
        } else {
            return loanAmount;
        }
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchRPCDataFallback")
    public Response fetchRPCData(FetchRoleRequest apiRequest, Properties prop) {
        Gson gson = new Gson();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        FetchRoleRequestFields requestObj = apiRequest.getRequestObj();
        logger.debug("Request obj: {} ", requestObj);
        String roleId = "";
        String branchCode = "";
        String searchVal = requestObj.getSearchVal();
        logger.debug("searchVal : {}", searchVal);
        List<String> kendraIds = null;
        List<String> branches = null;
        List<String> reworkStatus = new ArrayList<>();
        JSONObject resjson = new JSONObject();
        roleId = requestObj.getRoleId();
        branchCode = requestObj.getBranchCode();

        String userId = requestObj.getUserId();
        int i = applicationMasterRepository.updateLockOut(userId, userId + "_INPROGRESS");
        logger.debug(" records unlocked -->" + i);

        logger.info("Fetching branches for userId: {}, roleId: {}, branchCode: {}", userId, roleId, branchCode);
        branches = fetchBranchesByUserIdAndRoleId(requestObj.getUserId(), requestObj.getRoleId(), branchCode,
                prop.getProperty(CobFlagsProperties.APZ_USER_ROLE.getKey()));
        RoleAccessMap objDb = fetchRoleAccessMapObj(apiRequest.getAppId(), roleId);
        if (objDb != null) { // custom login flow
            int numOfRecords = Integer.parseInt(prop.getProperty(CobFlagsProperties.NUM_OF_REC_IN_WIDGET.getKey()));
            int currentPage = requestObj.getPageNo() - 1; // Index value
            Pageable page = PageRequest.of(currentPage, numOfRecords);
            String filterType = requestObj.getFilterType().trim();
            List<String> completedStatus = new ArrayList<>();
            List<JSONObject> caseAgeing = new ArrayList<>();
            completedStatus.clear();
            caseAgeing.clear();
            logger.info("Processing fetchRPCData for role: {}, filterType: {}", roleId, filterType);
            completedStatus.add(AppStatus.IPUSHBACK.getValue());
            completedStatus.add(AppStatus.APPROVED.getValue());
            completedStatus.add(AppStatus.INPROGRESS.getValue());
            completedStatus.add(AppStatus.PENDING.getValue());
            completedStatus.add(AppStatus.REJECTED.getValue());
            completedStatus.add(AppStatus.RPCPUSHBACK.getValue());

            List<String> altFilterTypes = new ArrayList<>();
            altFilterTypes.add(AppStatus.PENDINGFORRPCVERIFICATION.getValue());
            altFilterTypes.add(AppStatus.IPUSHBACK.getValue());
            altFilterTypes.add(AppStatus.PUSHBACK.getValue());
            altFilterTypes.add(AppStatus.RPCVERIFIED.getValue());
            altFilterTypes.add(AppStatus.RPCPUSHBACK.getValue());
            altFilterTypes.add("ALLSENTBACK");
            altFilterTypes.add(AppStatus.DBPUSHBACK.getValue());
            altFilterTypes.add(AppStatus.DBKITVERIFIED.getValue());

            if (Constants.RPC_MAKER.equalsIgnoreCase(apiRequest.getRequestObj().getFetchRole())) {
                reworkStatus.add(AppStatus.IPUSHBACK.getValue());
                ApplicationMaster aggregatedCountsRPCMaker = null;
                Page<ApplicationMaster> applicationsRPCMaker = null;
                List<String> RPCMakerStatusList = new ArrayList<>();
                RPCMakerStatusList.add(AppStatus.APPROVED.getValue());
                RPCMakerStatusList.add(AppStatus.RPCPUSHBACK.getValue());
                RPCMakerStatusList.add(AppStatus.IPUSHBACK.getValue());
                RPCMakerStatusList.add(AppStatus.PENDINGFORRPCVERIFICATION.getValue());

                List<Integer> possibleMakerFreshCaseSeqNums = new ArrayList<>();
                Arrays.stream(Constants.MAKER_FRESH_CASE_SEQ_NUM.split(","))
                        .map(Integer::parseInt)
                        .forEach(possibleMakerFreshCaseSeqNums::add);

                try {
                    logger.info("Fetching dashboard data with counts for RPC Maker");
                    aggregatedCountsRPCMaker = applicationMasterRepository.fetchDashBoardDataWithCounts(
                            RPCMakerStatusList, branches, reworkStatus,
                            AppStatus.RPCPUSHBACK.getValue(), AppStatus.IPUSHBACK.getValue(),
                            AppStatus.PENDINGFORRPCVERIFICATION.getValue(), Constants.RPC_MAKER, userId, Constants.IEXCEED_FLAG);

                    if (searchVal != null && searchVal.trim().length() > 0) {
                        logger.debug("Inside search application: {}", searchVal);
                        if (altFilterTypes.contains(filterType.toUpperCase())) {
                            logger.debug("Inside fetchDashBoardALTApplications with search");
                            applicationsRPCMaker = applicationMasterRepository.fetchDashBoardALTApplicationsWithSearch(
                                    RPCMakerStatusList, branches, page, Constants.RPC_MAKER,
                                    filterType.toUpperCase(), AppStatus.IPUSHBACK.getValue(), reworkStatus,
                                    AppStatus.RPCPUSHBACK.getValue(), searchVal, Constants.IEXCEED_FLAG);
                        } else {
                            logger.debug("Inside fetchDashBoardApplications with search");
                            RPCMakerStatusList.remove(AppStatus.IPUSHBACK.getValue());
                            RPCMakerStatusList.remove(AppStatus.PENDINGFORRPCVERIFICATION.getValue());
                            applicationsRPCMaker = applicationMasterRepository.fetchDashBoardApplicationsWithSearch(
                                    RPCMakerStatusList, branches, page, Constants.RPC_MAKER,
                                    filterType, reworkStatus, AppStatus.IPUSHBACK.getValue(),
                                    AppStatus.RPCPUSHBACK.getValue(), searchVal, userId, Constants.IEXCEED_FLAG);
                        }
                    } else {
                        if (altFilterTypes.contains(filterType.toUpperCase())) {
                            logger.debug("Inside fetchDashBoardALTApplications without search");
                            applicationsRPCMaker = applicationMasterRepository.fetchDashBoardALTApplications(
                                    RPCMakerStatusList, branches, page, Constants.RPC_MAKER,
                                    filterType.toUpperCase(), AppStatus.IPUSHBACK.getValue(), reworkStatus,
                                    AppStatus.RPCPUSHBACK.getValue(), Constants.IEXCEED_FLAG);
                        } else {
                            logger.debug("Inside fetchDashBoardApplications without search");
                            RPCMakerStatusList.remove(AppStatus.IPUSHBACK.getValue());
                            RPCMakerStatusList.remove(AppStatus.PENDINGFORRPCVERIFICATION.getValue());
                            applicationsRPCMaker = applicationMasterRepository.fetchDashBoardApplications(
                                    RPCMakerStatusList, branches, page, Constants.RPC_MAKER,
                                    filterType, reworkStatus, AppStatus.IPUSHBACK.getValue(),
                                    AppStatus.RPCPUSHBACK.getValue(), userId, Constants.IEXCEED_FLAG);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error fetching RPC Maker data", e);
                }

                if (aggregatedCountsRPCMaker != null) {
                    logger.debug("RPCPushbackCases : {}", aggregatedCountsRPCMaker.getPushbackCases());

                    caseAgeing.add(new JSONObject().put(Constants.DAY_0, aggregatedCountsRPCMaker.getDayZero())
                            .put(Constants.ORDER, 1));
                    caseAgeing.add(new JSONObject().put(Constants.DAY_1, aggregatedCountsRPCMaker.getDayOne())
                            .put(Constants.ORDER, 2));
                    caseAgeing.add(new JSONObject().put(Constants.DAY_2, aggregatedCountsRPCMaker.getDayTwo())
                            .put(Constants.ORDER, 3));
                    caseAgeing.add(new JSONObject().put(Constants.DAY_3, aggregatedCountsRPCMaker.getDayThree())
                            .put(Constants.ORDER, 4));
                    caseAgeing.add(new JSONObject().put(Constants.DAY_4, aggregatedCountsRPCMaker.getDayFour())
                            .put(Constants.ORDER, 5));
                    caseAgeing.add(new JSONObject().put(Constants.DAY_5, aggregatedCountsRPCMaker.getDayFive())
                            .put(Constants.ORDER, 6));
                    caseAgeing.add(new JSONObject()
                            .put(Constants.DAY_5PLUS, aggregatedCountsRPCMaker.getMoreThanFiveDays()).put(Constants.ORDER, 7));

                    resjson.put(Constants.TOTAL_APPLICATIONS, aggregatedCountsRPCMaker.getTotalApplications());
                    resjson.put(Constants.FRESHCASES, aggregatedCountsRPCMaker.getFreshCases());
                    resjson.put(Constants.REWORKCASES, aggregatedCountsRPCMaker.getReworkCases());
                    resjson.put(Constants.RPCCHECKERTOMAKER, aggregatedCountsRPCMaker.getRpcCheckerToMaker());
                    resjson.put(Constants.SOURCINGPUSHBACK, aggregatedCountsRPCMaker.getPushbackCases());
                    resjson.put("PendingRpcVerification", aggregatedCountsRPCMaker.getCompletedCases());
                    resjson.put(Constants.RPCCASEAGEING, caseAgeing);
                }

                if (applicationsRPCMaker != null) {
                    logger.debug("Pending RPC Maker applications: {}", applicationsRPCMaker);
                    resjson.put(Constants.PENDINGRPC, applicationsRPCMaker.getContent());
                    resjson.put(Constants.PENDINGRPCPAGINATION, applicationsRPCMaker.getPageable());
                    resjson.put(Constants.TOTALELEMENTSPAGE, applicationsRPCMaker.getTotalElements());
                    resjson.put(Constants.TOTALPAGES, applicationsRPCMaker.getTotalPages());
                }
            } else if (Constants.RPC_CHECKER.equalsIgnoreCase(apiRequest.getRequestObj().getFetchRole())) {
                reworkStatus.add(AppStatus.IPUSHBACK.getValue());
                reworkStatus.add(AppStatus.RPCPUSHBACK.getValue());
                List<String> RPCCheckerStatusList = new ArrayList<>();
                RPCCheckerStatusList.add(AppStatus.RPCPUSHBACK.getValue());
                RPCCheckerStatusList.add(AppStatus.IPUSHBACK.getValue());
                RPCCheckerStatusList.add(AppStatus.PENDINGFORRPCVERIFICATION.getValue());
                RPCCheckerStatusList.add(AppStatus.RPCVERIFIED.getValue());

                ApplicationMaster aggregatedCountsRPCChecker = null;
                Page<ApplicationMaster> applicationsRPCChecker = null;

                List<Integer> possibleCheckerFreshCaseSeqNums = new ArrayList<>();
                Arrays.stream(Constants.CHECKER_FRESH_CASE_SEQ_NUM.split(","))
                        .map(Integer::parseInt)
                        .forEach(possibleCheckerFreshCaseSeqNums::add);

                try {
                    logger.info("Fetching dashboard data with counts for RPC Checker");
                    aggregatedCountsRPCChecker = applicationMasterRepository.fetchDashBoardDataWithCounts(
                            RPCCheckerStatusList, branches, reworkStatus,
                            AppStatus.RPCPUSHBACK.getValue(), AppStatus.IPUSHBACK.getValue(),
                            AppStatus.RPCVERIFIED.getValue(), Constants.RPC_CHECKER, userId, Constants.IEXCEED_FLAG);

                    if (searchVal != null && searchVal.trim().length() > 0) {
                        logger.debug("Inside search application: {}", searchVal);
                        if (altFilterTypes.contains(filterType.toUpperCase())) {
                            logger.debug("Inside fetchDashBoardALTApplications");
                            applicationsRPCChecker = applicationMasterRepository
                                    .fetchDashBoardALTApplicationsWithSearch(
                                            RPCCheckerStatusList, branches, page, Constants.RPC_CHECKER,
                                            filterType.toUpperCase(), AppStatus.IPUSHBACK.getValue(), reworkStatus,
                                            AppStatus.RPCPUSHBACK.getValue(),
                                            searchVal, Constants.IEXCEED_FLAG);
                        } else {
                            logger.debug("Inside fetchDashBoardApplications");
                            RPCCheckerStatusList.remove(AppStatus.IPUSHBACK.getValue());
                            RPCCheckerStatusList.remove(AppStatus.RPCVERIFIED.getValue());
                            applicationsRPCChecker = applicationMasterRepository.fetchDashBoardApplicationsWithSearch(
                                    RPCCheckerStatusList, branches, page, Constants.RPC_CHECKER,
                                    filterType, reworkStatus, AppStatus.IPUSHBACK.getValue(),
                                    AppStatus.RPCPUSHBACK.getValue(), searchVal, userId, Constants.IEXCEED_FLAG);
                        }
                    } else {
                        if (altFilterTypes.contains(filterType.toUpperCase())) {
                            logger.debug("Inside fetchDashBoardALTApplications");
                            applicationsRPCChecker = applicationMasterRepository.fetchDashBoardALTApplications(
                                    RPCCheckerStatusList, branches, page, Constants.RPC_CHECKER,
                                    filterType.toUpperCase(), AppStatus.IPUSHBACK.getValue(), reworkStatus,
                                    AppStatus.RPCPUSHBACK.getValue(), Constants.IEXCEED_FLAG);
                        } else {
                            logger.debug("Inside fetchDashBoardApplications");
                            RPCCheckerStatusList.remove(AppStatus.IPUSHBACK.getValue());
                            RPCCheckerStatusList.remove(AppStatus.RPCVERIFIED.getValue());
                            applicationsRPCChecker = applicationMasterRepository.fetchDashBoardApplications(
                                    RPCCheckerStatusList, branches, page, Constants.RPC_CHECKER,
                                    filterType, reworkStatus, AppStatus.IPUSHBACK.getValue(),
                                    AppStatus.RPCPUSHBACK.getValue(), userId, Constants.IEXCEED_FLAG);
                        }
                    }

                } catch (Exception e) {
                    logger.error("Error fetching RPC Checker data", e);
                }

                if (aggregatedCountsRPCChecker != null) {
                    logger.debug("RPCPushbackCases : {}", aggregatedCountsRPCChecker.getPushbackCases());
                    completedStatus.add(AppStatus.RPCVERIFIED.getValue());

                    caseAgeing.add(new JSONObject().put(Constants.DAY_0, aggregatedCountsRPCChecker.getDayZero())
                            .put(Constants.ORDER, 1));
                    caseAgeing.add(new JSONObject().put(Constants.DAY_1, aggregatedCountsRPCChecker.getDayOne())
                            .put(Constants.ORDER, 2));
                    caseAgeing.add(new JSONObject().put(Constants.DAY_2, aggregatedCountsRPCChecker.getDayTwo())
                            .put(Constants.ORDER, 3));
                    caseAgeing.add(
                            new JSONObject().put(Constants.DAY_3, aggregatedCountsRPCChecker.getDayThree())
                                    .put(Constants.ORDER, 4));
                    caseAgeing.add(new JSONObject().put(Constants.DAY_4, aggregatedCountsRPCChecker.getDayFour())
                            .put(Constants.ORDER, 5));
                    caseAgeing.add(new JSONObject().put(Constants.DAY_5, aggregatedCountsRPCChecker.getDayFive())
                            .put(Constants.ORDER, 6));
                    caseAgeing.add(
                            new JSONObject().put(Constants.DAY_5PLUS, aggregatedCountsRPCChecker.getMoreThanFiveDays())
                                    .put(Constants.ORDER, 7));

                    resjson.put(Constants.TOTAL_APPLICATIONS, aggregatedCountsRPCChecker.getTotalApplications());
                    resjson.put(Constants.FRESHCASES, aggregatedCountsRPCChecker.getFreshCases());
                    resjson.put(Constants.REWORKCASES, aggregatedCountsRPCChecker.getReworkCases());
                    resjson.put(Constants.RPCCHECKERTOMAKER, aggregatedCountsRPCChecker.getRpcCheckerToMaker());
                    resjson.put("RpcVerified", aggregatedCountsRPCChecker.getCompletedCases());
                    resjson.put(Constants.SOURCINGPUSHBACK, aggregatedCountsRPCChecker.getPushbackCases());
                    resjson.put(Constants.RPCCASEAGEING, caseAgeing);
                }

                if (applicationsRPCChecker != null) {
                    logger.debug("Pending RPC Checker applications: {}", applicationsRPCChecker);
                    resjson.put(Constants.PENDINGRPC, applicationsRPCChecker.getContent());
                    resjson.put(Constants.PENDINGRPCPAGINATION, applicationsRPCChecker.getPageable());
                    resjson.put(Constants.TOTALELEMENTSPAGE, applicationsRPCChecker.getTotalElements());
                    resjson.put(Constants.TOTALPAGES, applicationsRPCChecker.getTotalPages());
                }
            } else if (Constants.RPC_DB_VERIFICATION.equalsIgnoreCase(apiRequest.getRequestObj().getFetchRole())) {
                List<String> RPCDBVerificationStatusList = new ArrayList<>();
                RPCDBVerificationStatusList.add(AppStatus.DBKITGENERATED.getValue());
                List<String> rpcDbVerificationAllowedStatus = new ArrayList<>();
                rpcDbVerificationAllowedStatus.add(AppStatus.DBKITGENERATED.getValue());
                rpcDbVerificationAllowedStatus.add(AppStatus.DBKITVERIFIED.getValue());
                rpcDbVerificationAllowedStatus.add(AppStatus.DBPUSHBACK.getValue());
                List<String> rpcDBVerificationReworkStatus = Arrays.asList(AppStatus.DBPUSHBACK.getValue());

                ApplicationMaster aggregatedCountsRPCDBVerification = null;
                Page<ApplicationMaster> applicationsRPCDBVerification = null;
                try {
                    logger.info("Fetching dashboard data with counts for RPC DB Verification");
                    aggregatedCountsRPCDBVerification = applicationMasterRepository2
                            .fetchRPCDBVerificationDashboardCounts(RPCDBVerificationStatusList, branches, rpcDBVerificationReworkStatus,
                                    Arrays.asList(AppStatus.DBKITVERIFIED.getValue()), rpcDBVerificationReworkStatus, rpcDbVerificationAllowedStatus);

                    if (searchVal != null && searchVal.trim().length() > 0) {
                        if (altFilterTypes.contains(filterType.toUpperCase())) {
                            applicationsRPCDBVerification = applicationMasterRepository2
                                    .fetchRPCDBVerificationDashboardALTApplicationsSearch(
                                            branches, page, filterType.toUpperCase(), searchVal);
                        } else {
                            applicationsRPCDBVerification = applicationMasterRepository2
                                    .fetchRPCDBVerificationDashboardApplicationsSearch(
                                            RPCDBVerificationStatusList, branches, page, filterType.toUpperCase(),
                                            rpcDBVerificationReworkStatus, searchVal);
                        }
                    } else {
                        if (altFilterTypes.contains(filterType.toUpperCase())) {
                            applicationsRPCDBVerification = applicationMasterRepository2
                                    .fetchRPCDBVerificationDashboardALTApplications(
                                            branches, page, filterType.toUpperCase());
                        } else {
                            applicationsRPCDBVerification = applicationMasterRepository2
                                    .fetchRPCDBVerificationDashboardApplications(
                                            RPCDBVerificationStatusList, branches, page, filterType.toUpperCase(),
                                            rpcDBVerificationReworkStatus);
                        }
                    }

                } catch (Exception e) {
                    logger.error("Error fetching RPC DB Verification data", e);
                }

                if (aggregatedCountsRPCDBVerification != null) {
                    logger.debug("DBPUSHBACK cases : {}", aggregatedCountsRPCDBVerification.getPushbackCases());
                    completedStatus.add(AppStatus.RPCVERIFIED.getValue());

                    caseAgeing.add(new JSONObject().put(Constants.DAY_0, aggregatedCountsRPCDBVerification.getDayZero())
                            .put(Constants.ORDER, 1));
                    caseAgeing.add(new JSONObject().put(Constants.DAY_1, aggregatedCountsRPCDBVerification.getDayOne())
                            .put(Constants.ORDER, 2));
                    caseAgeing.add(new JSONObject().put(Constants.DAY_2, aggregatedCountsRPCDBVerification.getDayTwo())
                            .put(Constants.ORDER, 3));
                    caseAgeing
                            .add(new JSONObject().put(Constants.DAY_3, aggregatedCountsRPCDBVerification.getDayThree())
                                    .put(Constants.ORDER, 4));
                    caseAgeing.add(new JSONObject().put(Constants.DAY_4, aggregatedCountsRPCDBVerification.getDayFour())
                            .put(Constants.ORDER, 5));
                    caseAgeing.add(new JSONObject().put(Constants.DAY_5, aggregatedCountsRPCDBVerification.getDayFive())
                            .put(Constants.ORDER, 6));
                    caseAgeing.add(
                            new JSONObject()
                                    .put(Constants.DAY_5PLUS, aggregatedCountsRPCDBVerification.getMoreThanFiveDays())
                                    .put(Constants.ORDER, 7));

                    resjson.put(Constants.TOTAL_APPLICATIONS, aggregatedCountsRPCDBVerification.getTotalApplications());
                    resjson.put(Constants.FRESHCASES, aggregatedCountsRPCDBVerification.getFreshCases());
                    resjson.put(Constants.REWORKCASES, aggregatedCountsRPCDBVerification.getReworkCases());
                    resjson.put(Constants.RPCCHECKERTOMAKER, aggregatedCountsRPCDBVerification.getRpcCheckerToMaker());
                    resjson.put("RpcVerified", aggregatedCountsRPCDBVerification.getCompletedCases());
                    resjson.put(Constants.SOURCINGPUSHBACK, aggregatedCountsRPCDBVerification.getPushbackCases());
                    resjson.put(Constants.RPCCASEAGEING, caseAgeing);
                }

                if (applicationsRPCDBVerification != null) {
                    logger.debug("Pending RPC DB verification applications: {}", applicationsRPCDBVerification);
                    resjson.put(Constants.PENDINGRPC, applicationsRPCDBVerification.getContent());
                    resjson.put(Constants.PENDINGRPCPAGINATION, applicationsRPCDBVerification.getPageable());
                    resjson.put(Constants.TOTALELEMENTSPAGE, applicationsRPCDBVerification.getTotalElements());
                    resjson.put(Constants.TOTALPAGES, applicationsRPCDBVerification.getTotalPages());
                }
            }

            logger.debug("Request obj: {} {}", requestObj.getRoleId(), requestObj.getUserId());

            resjson.put("roleAccessMap", objDb);
            resjson.put("allowedStatus", fetchAllowedStatusListForRole(objDb, Constants.FEATURE_SEARCH));
            resjson.put("kendraIds", kendraIds);
            responseBody.setResponseObj(gson.toJson(resjson));
        } else {
            logger.warn("RoleAccessMap not found for appId: {}, roleId: {}", apiRequest.getAppId(), roleId);
            responseBody.setResponseObj(gson.toJson(objDb));
        }
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        logger.info("Returning response for fetchRPCData for roleId: {}", roleId);
        return response;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchStateMasterFallback")
    public Response fetchStateMaster() {
        Gson gson = new Gson();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        responseBody.setResponseObj(gson.toJson(stateDetailsRepository.findAll()));
        logger.debug(responseBody.toString());
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    private void getWorkflowStats(TATReportPayload TATObj) {
    }

    private String fetchBranchCode(String appId, String userId) {
        Optional<User> userDb = userRepository.findById(new UserId(appId, userId));
        if (userDb.isPresent()) {
            User user = userDb.get();
            return user.getAddInfo2();
        }
        return null;
    }

    public SanctionMaster getLoanRangeForRole(String productCode, String role) {
        logger.debug("Fetching loan range for productCode: {} and role: {}", productCode, role);
        List<SanctionMaster> ranges = sanctionMasterRepositoy.findByProduct(productCode);

        if (ranges == null || ranges.isEmpty()) {
            logger.warn("No loan ranges found for productCode: {}", productCode);
            return null;
        }

        logger.debug("Filtering loan ranges for role: {}", role);
        return ranges.stream()
                .filter(r -> {
                    switch (role.toUpperCase()) {
                        case "BM":
                            return "Y".equalsIgnoreCase(r.getBm());
                        case "AM":
                            return "Y".equalsIgnoreCase(r.getAm());
                        case "RM":
                            return "Y".equalsIgnoreCase(r.getRm());
                        case "DM":
                            return "Y".equalsIgnoreCase(r.getDm());
                        default:
                            logger.warn("Invalid role: {}", role);
                            return false;
                    }
                })
                .findFirst()
                .orElseGet(() -> {
                    logger.warn("No matching loan range found for role: {}", role);
                    return null;
                });
    }

    private void handleSanctionPending(ApplicationMaster appMaster, String role, List<ApplicationMaster> sanctionList) {
        String applicationId = appMaster.getApplicationId();
        String productCode = appMaster.getProductCode();

        logger.debug("Handling sanction pending for applicationId: {} and role: {}", applicationId, role);

        LoanDetails loanDetails = loanDtlsRepo.findByApplicationId(applicationId);
        if (loanDetails == null || loanDetails.getBmRecommendedLoanAmount() == null) {
            logger.warn("Loan details or BM recommended loan amount is null for applicationId: {}", applicationId);
            return;
        }

        logger.debug("Fetching loan range for productCode: {} and role: {}", productCode, role);
        SanctionMaster range = getLoanRangeForRole(productCode, role);
        if (range != null &&
                loanDetails.getBmRecommendedLoanAmount().compareTo(range.getMinValue()) >= 0 &&
                loanDetails.getBmRecommendedLoanAmount().compareTo(range.getMaxValue()) <= 0) {

            logger.debug("applicationId: {} and bmRecommendedLoanAmount: {} falls in range: {} - {}",
                    applicationId, loanDetails.getBmRecommendedLoanAmount(), range.getMinValue(), range.getMaxValue());
            logger.debug("Adding application to SANCTION_PENDING for {} role: {}", role, applicationId);
            sanctionList.add(appMaster);
        } else {
            logger.warn("applicationId: {} and bmRecommendedLoanAmount: {} does not fall in range for role: {}",
                    applicationId, loanDetails.getBmRecommendedLoanAmount(), role);
        }
    }

    private void handleResanctionPending(ApplicationMaster app, String role, List<ApplicationMaster> resanctionList) {
        String applicationId = app.getApplicationId();
        String productCode = app.getProductCode();
        BigDecimal custDtlId = null;
        Gson gson = new Gson();

        logger.debug("Entering handleResanctionPending for applicationId: {}, role: {}", applicationId, role);

        List<CustomerDetails> custDtlList = customerDetailsRepository.findByApplicationId(applicationId);
        if (custDtlList == null || custDtlList.isEmpty()) {
            logger.warn("Customer details not found for applicationId: {}", applicationId);
            return;
        }
        boolean coApplicantFound = false;
        for (CustomerDetails custDtlObj : custDtlList) {
            logger.debug("Customer details found for applicationId: {}, customerType: {}", applicationId, custDtlObj.getCustomerType());
            if (custDtlObj.getCustomerType() != null && custDtlObj.getCustomerType().equalsIgnoreCase(Constants.COAPPLICANT)) {
                custDtlId = custDtlObj.getCustDtlId();
                logger.debug("Co-applicant found. custDtlId: {}", custDtlId);
                coApplicantFound = true;
                break;
            }
        }
        if (!coApplicantFound) {
            logger.debug("Customer is not a co-applicant for applicationId: {}", applicationId);
        }

        CibilDetails cibilDetails;
        if (null != custDtlId) {
            cibilDetails = cibilDetailsRepository.findByApplicationIdAndCustDtlIdAndCbStatus(applicationId, custDtlId, Constants.PASS_STRING);
            if (cibilDetails == null) {
                logger.warn("CIBIL details not found for applicationId: {}, custDtlId: {}", applicationId, custDtlId);
                return;
            }
            logger.debug("CIBIL details found for applicationId: {}, custDtlId: {}", applicationId, custDtlId);

            CibilDetailsPayload cibilPayload = gson.fromJson(cibilDetails.getPayloadColumn(), CibilDetailsPayload.class);
            String eligibleLoanAmount = cibilPayload.getEligibleAmt();
            logger.debug("Eligible loan amount from CIBIL payload: {}", eligibleLoanAmount);

            SanctionMaster range = getLoanRangeForRole(productCode, role);
            if (range != null &&
                    new BigDecimal(eligibleLoanAmount).compareTo(range.getMinValue()) >= 0 &&
                    new BigDecimal(eligibleLoanAmount).compareTo(range.getMaxValue()) <= 0) {
                logger.debug("applicationId: {} and eligibleLoanAmount: {} falls in range: {} - {}",
                        applicationId, eligibleLoanAmount, range.getMinValue(), range.getMaxValue());
                logger.debug("Adding application to RESANCTION for {} role: {}", role, applicationId);
                resanctionList.add(app);
            } else {
                logger.warn("applicationId: {} and eligibleLoanAmount: {} does not fall in range for role: {}",
                        applicationId, eligibleLoanAmount, role);
            }
        } else {
            logger.debug("custDtlId is null for applicationId: {}, skipping RESANCTION handling.", applicationId);
        }
        logger.debug("Exiting handleResanctionPending for applicationId: {}, role: {}", applicationId, role);
    }

    private List<UnnatiIexceedCDHLead> fetchUnactionedFreshLeads(List<String> kendraIds,
                                                                 List<UnnatiIexceedCDHLead> allLeads) {
        List<String> workItems = new ArrayList<>();
        List<ApplicationMaster> appMasterList = applicationMasterRepository.fetchDashBoardData(kendraIds);
        for (ApplicationMaster appMaster : appMasterList) {
            logger.debug("Application Master : {}", appMaster);
            if (null != appMaster.getWorkitemNo()) {
                logger.debug("WorkItem No : {}", appMaster.getWorkitemNo());
                workItems.add(appMaster.getWorkitemNo());
            }
        }

        logger.debug("Kendra id's : {}", kendraIds);
        logger.debug("work Items : {}", workItems);

        List<UnnatiIexceedCDHLead> UnactionedLeads = allLeads.stream()
                .filter(lead -> !workItems.contains(lead.getReferenceId())) // Exclude
                // names
                // in
                // namesToRemove
                .map(lead -> {
                    if(("IEXCEED").equalsIgnoreCase(lead.getSourceOfTheApplication()))
                        lead.setSourceOfTheApplication("UNNATI");
                    return lead;
                })
                .collect(Collectors.toList());
        logger.debug("UnactionedLeads : {}", UnactionedLeads.toString());
        return UnactionedLeads;
    }

    private List<UnnatiIexceedCDHLead> fetchUnactionedFreshRenewalLeads(List<String> kendraIds,
                                                                        List<UnnatiIexceedCDHLead> allLeads) {
        List<String> memberIds = new ArrayList<>();
        List<ApplicationMaster> appMasterList = applicationMasterRepository.fetchRenewalDashBoardData(kendraIds);
        for (ApplicationMaster appMaster : appMasterList) {
            logger.debug("Application Master : {}", appMaster);
            if (null != appMaster.getMemberId()) {
                logger.debug("Member Id : {}", appMaster.getMemberId());
                memberIds.add(appMaster.getMemberId());
            }
        }
        logger.debug("Kendra id's : {}", kendraIds);
        logger.debug("Member id's : {}", memberIds);
        List<UnnatiIexceedCDHLead> UnactionedLeads = allLeads.stream()
                .filter(lead -> !memberIds.contains(lead.getCustomerId())) // Exclude names in namesToRemove
                .collect(Collectors.toList());
        logger.debug("UnactionedLeads : {}", UnactionedLeads.toString());
        return UnactionedLeads;
    }

    private List<String> fetchKendrasByUserIsAndRoleId(String userId, String roleAccess, String branchCode,
                                                       String rolesStr, String fetchType) {
        List<String> kendraIds = null;
        String roleId = getUserRole(roleAccess, rolesStr);
        if (null != roleId) {
            if (roleId.equals(CobFlagsProperties.KM.getKey())) {
                if (Constants.DASHBOARD_STATS_RENEWAL.equalsIgnoreCase(fetchType)) {
                    List<GkKendraData> kendraDetailsList = gkKendraDataRepo.findKendraList(userId);
                    // List<KendraDetails> kendraDetailsList =
                    // asdkendraDetailsRepository.findKendraDetailsByHandledBy(userId);
                    List<String> renewalBranches = whitelistedBranchesRepository.findAllRenewalEnabledBranches();
                    kendraIds = kendraDetailsList.stream()
                            .filter(kendra -> renewalBranches.contains(kendra.getBranchId()))
                            .map(GkKendraData::getKendraId).map(String::valueOf).collect(Collectors.toList());
                } else {
                    kendraIds = gkKendraDataRepo.findKendraIdByKmId(userId);
                }
            } else if (Constants.APPROVER.equalsIgnoreCase(roleId)
                    || CobFlagsProperties.BM.getKey().equalsIgnoreCase(roleId)
                    || Constants.BCM.equalsIgnoreCase(roleId)) {
                kendraIds = gkKendraDataRepo.findKendraIdByBranchId(branchCode);
            } else if (Constants.AM.equalsIgnoreCase(roleId) || Constants.ACM.equalsIgnoreCase(roleId)) {
                List<String> branches = fetchBranchesByUserIdAndRoleId(userId, roleAccess, branchCode, rolesStr);
                kendraIds = gkKendraDataRepo.findKendraIdByBranchIdIn(branches);
            }
        }
        return kendraIds;
    }

    private List<String> fetchBranchesByUserIdAndRoleId(String userId, String roleAccess, String branchCode,
                                                        String rolesStr) {
        List<String> branches = new ArrayList<>();
        String roleId = getUserRole(roleAccess, rolesStr);
        if (null != roleId) {
            if (roleId.equals(CobFlagsProperties.RPC.getKey()) || roleId.equalsIgnoreCase(Constants.RM)
                    || roleId.equalsIgnoreCase(Constants.DM)) {
                logger.debug("branchCode id's : {}", branchCode);
                String[] regionIdList = branchCode.split(",");
                List<String> regionId = new ArrayList<>();
                for (String region : regionIdList) {
                    regionId.add(region.trim());
                }
                logger.debug("region id's : {}", regionId);
                List<OfficeData> branchCodes = officeDataRepo.findBranchIdDetailsByRPCId(regionId);
                logger.debug("branchCodes id's : {}", branchCodes.toString());
                for (OfficeData branchCodelist : branchCodes) {
                    branches.add(branchCodelist.getBranchId());
                }
                logger.debug("branchCodes : {}", branches);
                // kendraIds = kendraDetailsRepository.findKendraIdByBranchIdIn(branches);
                // logger.debug("kendra id's : {}", kendraIds);
            } else if (roleId.equalsIgnoreCase(Constants.AM) || roleId.equalsIgnoreCase(Constants.ACM)) {
                logger.debug("branchCode id's : {}", branchCode);
                String[] areaIdList = branchCode.split(",");
                List<String> areaIds = new ArrayList<>();
                for (String areaId : areaIdList) {
                    areaIds.add(areaId.trim());
                }
                logger.debug("area ids: {}", areaIds);
                List<OfficeData> branchCodes = officeDataRepo.findBranchIdByAreaId(areaIds);
                for (OfficeData branchCodelist : branchCodes) {
                    branches.add(branchCodelist.getBranchId());
                }
                logger.debug("branchCodes : {}", branches);
            }
        }
        return branches;
    }

    public static String getUserRole(String roleAccess, String rolesStr) {
        if (null != rolesStr) {
            HashMap<String, String> rolesMap = (HashMap<String, String>) Arrays.asList(rolesStr.split(",")).stream()
                    .map(s -> s.split(":")).collect(Collectors.toMap(e -> e[1].trim(), e -> e[0].trim()));
            return rolesMap.get(roleAccess);
        } else {
            return null;
        }
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchRoleAccessMapObjFallback")
    public RoleAccessMap fetchRoleAccessMapObj(String appId, String roleId) {

        logger.debug("appId - roleId : " + appId + " - " + roleId);
        RoleAccessMap objDb = null;
        RoleAccessMapId id = new RoleAccessMapId(appId, roleId);
        Optional<RoleAccessMap> obj = roleAccessMapRepository.findById(id);
        if (obj.isPresent()) {
            objDb = obj.get();
        }
        return objDb;
    }

    public List<String> fetchAllowedStatusListForRole(RoleAccessMap roleAccessMapObj, String requiredFeature) {
        String allowedFeatures = roleAccessMapObj.getAllowedFeature();
        JSONObject json = new JSONObject(allowedFeatures);
        JSONArray jsonArray = json.getJSONArray(requiredFeature);
        ArrayList<String> dbFeaturesList = new ArrayList<>();
        for (Object arrayElement : jsonArray) {
            dbFeaturesList.add((String) arrayElement);
        }
        return dbFeaturesList;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "searchApplicationsFallback")
    public Response searchApplications(SearchAppRequest apiRequest) {
        Gson gson = new Gson();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        SearchAppRequestFields reqFields = apiRequest.getRequestObj();
        String roleId = commonService.fetchRoleId(apiRequest.getAppId(), reqFields.getUserId());
        RoleAccessMap objDb = fetchRoleAccessMapObj(apiRequest.getAppId(), roleId);
        List<String> dbFeaturesList = fetchAllowedStatusListForRole(objDb, Constants.FEATURE_SEARCH);
        if (null != dbFeaturesList && !dbFeaturesList.isEmpty()) {
            String branchCode = fetchBranchCode(apiRequest.getAppId(), reqFields.getUserId());
            String mobileNum = "%" + reqFields.getMobileOrApplnId() + "%";
            List<ApplicationMaster> appMasterList = applicationMasterRepository.searchApplications(dbFeaturesList,
                    mobileNum, reqFields.getMobileOrApplnId(), branchCode);
            responseBody.setResponseObj(gson.toJson(appMasterList));
        } else {
            responseBody.setResponseObj("");
        }
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "isVaptPassedForScreenElementsFallback")
    public boolean isVaptPassedForScreenElements(CreateModifyUserRequest request, boolean isSelfOnBoardingHeaderAppId,
                                                 JSONArray array) {
        boolean flag = false;
        return flag;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "isValidStageFallback")
    public boolean isValidStage(CreateModifyUserRequest request, boolean isSelfOnBoardingHeaderAppId, JSONArray array) {
        int mainOrJointHolder = 0;
        String currentScrIdFromDb = "";
        CustomerDataFields requestObj = request.getRequestObj();
        ApplicationMaster appMasterReq = requestObj.getApplicationMaster();
        if (appMasterReq.getCustDtlSlNum() == 1) {
            mainOrJointHolder = 2;
        } else if (appMasterReq.getCustDtlSlNum() > 1) {
            mainOrJointHolder = 3;
        }
        String[] currentScreenIdArray = appMasterReq.getCurrentScreenId().split("~");
        if ("N".equalsIgnoreCase(currentScreenIdArray[1])) { // back navigation flow
            return true;
        } else if (array != null) {
            String currenScrnIdReq = currentScreenIdArray[0];
            Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
                    .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(),
                            requestObj.getApplicationId(), requestObj.getVersionNum(), AppStatus.INPROGRESS.getValue());
            if (masterObjDb.isPresent()) {
                ApplicationMaster masterObj = masterObjDb.get();
                currentScrIdFromDb = masterObj.getCurrentScreenId();
            }
            if (CommonUtils.isNullOrEmpty(requestObj.getApplicationId()) // First hit to the service.
                    || CommonUtils.isNullOrEmpty(currentScrIdFromDb) // currentScrIdFromDb will be null when hit for the
                    // first time for reject-modify flow.
                    || (appMasterReq.getCustDtlSlNum() > 1 && isJointHolderFlowFirstStage(appMasterReq))) {
                for (Object arrayElement : array) {
                    if ("Y".equalsIgnoreCase(((String) arrayElement).split("~")[mainOrJointHolder])
                            && currenScrnIdReq.equalsIgnoreCase(((String) arrayElement).split("~")[0])) {
                        return true;
                    }
                }
                return false;
            } else {
                List<Object> list = array.toList();
                for (int i = 0; i < array.length(); i++) {
                    if (currentScrIdFromDb.equalsIgnoreCase(((String) array.get(i)).split("~")[0])) {
                        List<Object> subList = list.subList(i + 1, array.length());
                        for (Object arrayElement : subList) {
                            if ("Y".equalsIgnoreCase(((String) arrayElement).split("~")[mainOrJointHolder])
                                    && currenScrnIdReq.equalsIgnoreCase(((String) arrayElement).split("~")[0])) {
                                return true;
                            }
                        }
                        return false;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public boolean isJointHolderFlowFirstStage(ApplicationMaster appMasterReq) {
        return null == appMasterReq.getCustDtlId();
    }

    /**
     * @author Ankit.CAG
     * @Fetching Leads by CustomerID & ProductID
     */
    public Response getCdhLeads(String productId, String customerId, UnnatiIexceedCDHLead unnatiIexceedCDHLead) {
        logger.debug("getCdhLeads for productId: {} and customerId: {}", productId, customerId);
        Response fetchCdhLeadsDetailsResponse = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        Gson gson = new Gson();
        CustomerDataFields customerDataFields = new CustomerDataFields();
        String leadsData = null;

        productId = ProductCode.getCdhCodeByUnnatiCode(productId);
        logger.debug(" fetching CDH productId:", productId);

        List<UnnatiCoApplicantDetails> CoApplicantData = null;
        try {
            CoApplicantData = coApplicantDetailsRepository.findByCustomerId(customerId);
        } catch (Exception e) {
            logger.debug("CoApplicantData fetch Error:", e.getMessage());
        }


        logger.debug("UnnatiIexceedCDHLead:", unnatiIexceedCDHLead);
        if (ProductCode.VISHESH.getCode((ProductCode.ProductType.UNNATI)).equalsIgnoreCase(productId)) {
            unnatiIexceedCDHLead.setProduct(ProductCode.VISHESH.getCode((ProductCode.ProductType.UNNATI)));
        }
        customerDataFields.setCdhLeadDetails(unnatiIexceedCDHLead);
        customerDataFields.setCdhCoAppLeadDetails(
                (CoApplicantData != null && !CoApplicantData.isEmpty()) ? CoApplicantData.get(0) : null);
        leadsData = gson.toJson(customerDataFields);
        leadsData = leadsData.replace(Constants.PAYLOAD_COLUMN, Constants.PAYLOAD);
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        responseBody.setResponseObj(leadsData);
        fetchCdhLeadsDetailsResponse.setResponseBody(responseBody);
        fetchCdhLeadsDetailsResponse.setResponseHeader(responseHeader);
        return fetchCdhLeadsDetailsResponse;
    }

    public Response fetchAppByCustDtlIdAndApplnID(FetchDeleteUserRequest fetchUserDetailsRequest) {
        logger.debug("fetchAppByCustDtlIdAndApplnID fetchUserDetailsRequest:" + fetchUserDetailsRequest);
        String applicationId = fetchUserDetailsRequest.getRequestObj().getApplicationId();
        String appId = fetchUserDetailsRequest.getRequestObj().getAppId();
        int versionNum = fetchUserDetailsRequest.getRequestObj().getVersionNum();
        BigDecimal custDtlId = fetchUserDetailsRequest.getRequestObj().getCustDtlId();
        CustomerDataFields customerDataFields = new CustomerDataFields();
        Gson gson = new Gson();
        Response fetchUserDetailsResponse = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        fetchUserDetailsResponse.setResponseHeader(responseHeader);
        ResponseBody responseBody = new ResponseBody();
        List<String> statusList = new ArrayList<>();
        for (AppStatus status : AppStatus.values()) {
            statusList.add(status.getValue());
        }
        Properties prop = null;
        try {
            prop = CommonUtils.readPropertyFile();
        } catch (IOException e) {
            logger.error("Error while reading property file in deleteNominee ", e);
        }
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(appId, applicationId, versionNum,
                        statusList);
        if (applicationMasterOpt.isPresent()) {
            logger.debug("applicationMasterOpt present:");
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();

            customerDataFields.setAppId(applicationMasterData.getAppId());
            customerDataFields.setApplicationId(applicationMasterData.getApplicationId());
            customerDataFields.setApplicationMaster(applicationMasterData);

            List<CustomerDetails> customerDetailsList = customerDetailsRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(applicationId, appId, versionNum, custDtlId);
            customerDataFields.setCustomerDetailsList(customerDetailsList);

            AddressDetailsWrapper addressDetailsWrapper = new AddressDetailsWrapper();
            List<AddressDetailsWrapper> addressDetailsWrapperList = new ArrayList<>();
            List<AddressDetails> addressDetailsList = addressDetailsRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(applicationId, appId, versionNum, custDtlId);
            addressDetailsWrapper.setAddressDetailsList(addressDetailsList);
            addressDetailsWrapperList.add(addressDetailsWrapper);
            customerDataFields.setAddressDetailsWrapperList(addressDetailsWrapperList);

            List<OccupationDetailsWrapper> occupationDetailsWrapperList = new ArrayList<>();
            OccupationDetailsWrapper occupationDetailsWrapper;
            List<OccupationDetails> occupationDetailsList = occupationDetailsRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(applicationId, appId, versionNum, custDtlId);
            for (OccupationDetails occupationDetails : occupationDetailsList) {
                occupationDetailsWrapper = new OccupationDetailsWrapper();
                occupationDetailsWrapper.setOccupationDetails(occupationDetails);
                occupationDetailsWrapperList.add(occupationDetailsWrapper);
            }
            customerDataFields.setOccupationDetailsWrapperList(occupationDetailsWrapperList);

            // insuranceDetails
            List<InsuranceDetailsWrapper> insuranceDetailsWrapper = new ArrayList<>();
            Optional<List<InsuranceDetails>> insuranceDetails = insuranceRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(applicationId, appId, versionNum, custDtlId);
            if (insuranceDetails.isPresent() && !insuranceDetails.get().isEmpty()) {
                insuranceDetails.get().forEach(insurance -> {
                    InsuranceDetailsWrapper wrapperDetails = InsuranceDetailsWrapper.builder()
                            .insuranceDetails(insurance).build();
                    insuranceDetailsWrapper.add(wrapperDetails);
                });
                customerDataFields.setInsuranceDetailsWrapperList(insuranceDetailsWrapper);
            } else {
                customerDataFields.setInsuranceDetailsWrapperList(null);
            }
            // branchDetails
            List<BankDetailsWrapper> bankDetails = new ArrayList<>();
            Optional<List<BankDetails>> bankDetailsList = bankDetailsRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(applicationId, appId, versionNum, custDtlId);
            if (bankDetailsList.isPresent() && !bankDetailsList.get().isEmpty()) {
                bankDetailsList.get().forEach(bankDetail -> {
                    BankDetailsWrapper detailsBankWrapper = BankDetailsWrapper.builder().bankDetails(bankDetail)
                            .build();
                    bankDetails.add(detailsBankWrapper);
                });
                customerDataFields.setBankDetailsWrapperList(bankDetails);
            } else {
                customerDataFields.setBankDetailsWrapperList(null);
            }

            // CibilDetails
            List<CibilDetailsWrapper> cibilDetailsWrapper = new ArrayList<>();
            Optional<List<CibilDetails>> cibilDetails = cibilDetailsRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(applicationId, appId, versionNum, custDtlId);
            if (cibilDetails.isPresent() && !cibilDetails.get().isEmpty()) {
                cibilDetails.get().forEach(cibilDetail -> {
                    CibilDetailsWrapper detailsWrapper = CibilDetailsWrapper.builder().cibilDetails(cibilDetail)
                            .build();
                    cibilDetailsWrapper.add(detailsWrapper);
                });
                customerDataFields.setCibilDetailsWrapperList(cibilDetailsWrapper);
            } else {
                customerDataFields.setCibilDetailsWrapperList(null);
            }
            LocalDate applicantCBDate = null;
            LocalDate coApplicantCBDate = null;
            if (null != customerDataFields.getCibilDetailsWrapperList()) {
                for (CustomerDetails customerDetail : customerDataFields.getCustomerDetailsList()) {
                    for (CibilDetailsWrapper cibilDetail : customerDataFields.getCibilDetailsWrapperList()) {
                        if (cibilDetail.getCibilDetails().getCustDtlId().compareTo(customerDetail.getCustDtlId()) == 0
                                && ("Applicant".equalsIgnoreCase(customerDetail.getCustomerType()))) {
                            applicantCBDate = cibilDetail.getCibilDetails().getCbDate();
                        }
                        if (cibilDetail.getCibilDetails().getCustDtlId().compareTo(customerDetail.getCustDtlId()) == 0
                                && (!"Applicant".equalsIgnoreCase(customerDetail.getCustomerType()))) {
                            coApplicantCBDate = cibilDetail.getCibilDetails().getCbDate();
                        }
                    }
                }
            }
            logger.error("Error CB Expiry : " + CobFlagsProperties.CB_EXPIRY_DAYS.getKey() + " - "
                    + prop.getProperty(CobFlagsProperties.CB_EXPIRY_DAYS.getKey()));
            ApplicationMaster appMasterData = customerDataFields.getApplicationMaster();
            if ((applicantCBDate != null) && (CommonUtils.getDateDiff(applicantCBDate, LocalDate.now()) > Integer
                    .parseInt(prop.getProperty(CobFlagsProperties.CB_EXPIRY_DAYS.getKey())))) {
                appMasterData.setApplicantCBExpiry(true);
            } else {
                appMasterData.setApplicantCBExpiry(false);
            }
            if ((coApplicantCBDate != null) && (CommonUtils.getDateDiff(coApplicantCBDate, LocalDate.now()) > Integer
                    .parseInt(prop.getProperty(CobFlagsProperties.CB_EXPIRY_DAYS.getKey())))) {
                appMasterData.setCoApplicantCBExpiry(true);
            } else {
                appMasterData.setCoApplicantCBExpiry(false);
            }
            customerDataFields.setApplicationMaster(appMasterData);
            // existingLoanDetails
            ExistingLoanDetailsWrapper existingWrapper = null;
            List<ExistingLoanDetailsWrapper> existingLoanDetailsWrapper = new ArrayList<>();
            Optional<List<ExistingLoanDetails>> existingLoandDetails = existingLoanRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(applicationId, appId, versionNum, custDtlId);
            if (existingLoandDetails.isPresent() && !existingLoandDetails.get().isEmpty()) {
                ExistingLoanDetailsWrapper.builder().existingLoanDetailsList(existingLoandDetails.get()).build();
                existingLoanDetailsWrapper.add(existingWrapper);
                customerDataFields.setExistingLoanDetailsWrapperList(existingLoanDetailsWrapper);
                ;

            } else {
                customerDataFields.setExistingLoanDetailsWrapperList(existingLoanDetailsWrapper);
                ;
            }
            LoanDetails loanDetails = loanDtlsRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId,
                    versionNum);
            customerDataFields.setLoanDetails(loanDetails);

            NomineeDetailsWrapper nomineeDetailsWrapper = new NomineeDetailsWrapper();
            List<NomineeDetailsWrapper> nomineeDetailsWrapperList = new ArrayList<>();
            List<NomineeDetails> nomineeDetailsList = nomineeDetailsRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndStatusAndCustDtlId(applicationId, appId, versionNum,
                            AppStatus.ACTIVE_STATUS.getValue(), custDtlId);
            nomineeDetailsWrapper.setNomineeDetailsList(nomineeDetailsList);
            nomineeDetailsWrapperList.add(nomineeDetailsWrapper);
            customerDataFields.setNomineeDetailsWrapperList(nomineeDetailsWrapperList);

            ApplicationDocumentsWrapper applicationDocumentsWrapper = new ApplicationDocumentsWrapper();
            List<ApplicationDocumentsWrapper> applicationDocumentsWrapperList = new ArrayList<>();
            List<ApplicationDocuments> applicationDocumentsList = applicationDocumentsRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndStatusAndCustDtlId(applicationId, appId, versionNum,
                            AppStatus.ACTIVE_STATUS.getValue(), custDtlId);
            applicationDocumentsWrapper.setApplicationDocumentsList(applicationDocumentsList);
            applicationDocumentsWrapperList.add(applicationDocumentsWrapper);
            customerDataFields.setApplicationDocumentsWrapperList(applicationDocumentsWrapperList);

            List<BankingFacilities> bankingFacilityList = bankingFacilitiesRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(applicationId, appId, versionNum, custDtlId);
            customerDataFields.setBankingFacilityList(bankingFacilityList);

            List<FatcaDetails> fatcaDetailsList = fatcaDtlsrepository
                    .findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
            customerDataFields.setFatcaDetailsList(fatcaDetailsList);

            List<CRSDetails> crsDetailsList = crsDtlsrepository.findByApplicationIdAndAppIdAndVersionNum(applicationId,
                    appId, versionNum);
            customerDataFields.setCrsDetailsList(crsDetailsList);

            customerDataFields.setApplicationTimelineDtl(
                    commonService.getApplicationTimelineDtl(applicationMasterData.getApplicationId()));

			Optional<SourcingResponseTracker> sourcingResponseTrackerOpt = sourcingResponseTrackerRepo
					.findById(applicationId);
            if (sourcingResponseTrackerOpt.isPresent()) {
                SourcingResponseTracker sourcingResponseTracker = sourcingResponseTrackerOpt.get();
                customerDataFields.setSourcingQueryResponse(sourcingResponseTracker);
			} else
				logger.debug("SourcingResponseTracker not present:");
            Optional<ApplicationWorkflow> workflow;
            workflow = applnWfRepository.findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(appId,
                    applicationId, versionNum);
            logger.debug("Workflow details " + workflow.get());

            if (workflow.isPresent()) {
                ApplicationWorkflow applnWf = workflow.get();
                List<WorkflowDefinition> wfDefnLis = wfDefnRepository.findByFromStageId(applnWf.getNextWorkFlowStage());
                customerDataFields.setApplnWfDefinitionList(wfDefnLis);
			} else
				logger.debug("ApplicationWorkflow not present:");

            String customerdata = gson.toJson(customerDataFields);
            customerdata = customerdata.replace(Constants.PAYLOAD_COLUMN, Constants.PAYLOAD);
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
            responseBody.setResponseObj(customerdata);
            fetchUserDetailsResponse.setResponseBody(responseBody);
            return fetchUserDetailsResponse;
        } else {
            logger.debug("applicationMasterOpt not  present:");
            responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            responseBody.setResponseObj(Constants.APP_MASTER_NOT_FOUND);
            fetchUserDetailsResponse.setResponseBody(responseBody);
            return fetchUserDetailsResponse;
        }
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "assignApplicationFallback")
    public Response assignApplication(AssignApplicationRequest apiRequest) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        AssignApplicationRequestFields requestFields = apiRequest.getRequestObj();
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestFields.getAppId(),
                        requestFields.getApplicationId(), requestFields.getVersionNum(),
                        AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
            masterObj.setAssignedTo(requestFields.getUserId());
            applicationMasterRepository.save(masterObj);
            if (!CommonUtils.isNullOrEmpty(masterObj.getRelatedApplicationId())) {
                masterObjDb = applicationMasterRepository.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(
                        requestFields.getAppId(), masterObj.getRelatedApplicationId(), requestFields.getVersionNum(),
                        AppStatus.INPROGRESS.getValue());
                ApplicationMaster masterObjRelated = masterObjDb.get();
                masterObjRelated.setAssignedTo(requestFields.getUserId());
                applicationMasterRepository.save(masterObjRelated);
            }
            PopulateapplnWFRequest req = new PopulateapplnWFRequest();
            PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
            reqFields.setAppId(masterObj.getAppId());
            reqFields.setApplicationId(masterObj.getApplicationId());
            reqFields.setCreatedBy(requestFields.getUserId());
            reqFields.setVersionNum(masterObj.getVersionNum());
            reqFields.setApplicationStatus(masterObj.getApplicationStatus());
            WorkFlowDetails wf = requestFields.getWorkFlow();
            reqFields.setWorkflow(wf);
            req.setRequestObj(reqFields);
            commonService.populateApplnWorkFlow(req);
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
            responseBody.setResponseObj(Constants.SUCCESS);
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            responseBody.setResponseObj("Invalid App master");
        }
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    private List<List<ApplicationMaster>> formQueueStatus(List<ApplicationMaster> appMasterList, String loggedInUserId,
                                                          String accessPermission, Properties prop) {
        List<ApplicationMaster> appMasterListInProgress = new ArrayList<>();
        List<ApplicationMaster> appMasterListPending = new ArrayList<>();
        List<ApplicationMaster> appMasterListRejected = new ArrayList<>();
        List<ApplicationMaster> appMasterListDeleted = new ArrayList<>();
        List<ApplicationMaster> appMasterListCompleted = new ArrayList<>();
        List<ApplicationMaster> appMasterListPushBack = new ArrayList<>();
        List<ApplicationMaster> appMasterListBCMPI = new ArrayList<>();
        List<List<ApplicationMaster>> finalList = new ArrayList<>();
        for (ApplicationMaster appMasterObj : appMasterList) {
            // Generic logic; either merge or customize based on requirement
            if (accessPermission.equalsIgnoreCase(Constants.ACCESS_PERMISSION_INITIATOR)) {
                formQueueStatusInitiator(appMasterObj, appMasterListInProgress, appMasterListCompleted,
                        appMasterListRejected, appMasterListDeleted, appMasterListPending, appMasterListPushBack,
                        loggedInUserId);
            } else if (accessPermission.equalsIgnoreCase(Constants.ACCESS_PERMISSION_APPROVER)) {
                formQueueStatusApprover(appMasterObj, appMasterListInProgress, appMasterListCompleted,
                        appMasterListRejected, appMasterListDeleted, appMasterListPending, appMasterListPushBack,
                        appMasterListBCMPI, loggedInUserId, prop);
            } else if (accessPermission.equalsIgnoreCase(Constants.ACCESS_PERMISSION_BOTH)) {
                if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                        && CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
                        && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_CUSTOMER.getValue());
                    appMasterListInProgress.add(appMasterObj);
                } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                        && !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
                        && appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
                        && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_SELF.getValue());
                    appMasterListInProgress.add(appMasterObj);
                } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                        && !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
                        && !appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
                        && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_OTHERS.getValue());
                    appMasterListInProgress.add(appMasterObj);
                } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                        && !AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                    formQueueStatusForInProgress(appMasterObj, appMasterListInProgress);
                } else if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    formQueueStatusForPending(appMasterObj, appMasterListPending, Constants.ACCESS_PERMISSION_BOTH);
                } else if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.COMPLETED.getValue());
                    appMasterListCompleted.add(appMasterObj);
                } else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.REJECTED.getValue());
                    appMasterListRejected.add(appMasterObj);
                } else if (AppStatus.DELETED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.DELETED.getValue());
                    appMasterListDeleted.add(appMasterObj);
                } else if (AppStatus.PUSHBACK.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus()) || AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.PUSHBACK.getValue());
                    appMasterListPushBack.add(appMasterObj);
                }
            } else if (accessPermission.equalsIgnoreCase(Constants.ACCESS_PERMISSION_VIEWONLY)) {
                if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                        && CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
                        && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_CUSTOMER.getValue());
                    appMasterListInProgress.add(appMasterObj);
                } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                        && !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
                        && !appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
                        && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_OTHERS.getValue());
                    appMasterListInProgress.add(appMasterObj);
                } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                        && !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
                        && appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
                        && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_SELF.getValue());
                    appMasterListInProgress.add(appMasterObj);
                } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                        && !AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                    formQueueStatusForInProgress(appMasterObj, appMasterListInProgress);
                } else if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    formQueueStatusForPending(appMasterObj, appMasterListPending, Constants.ACCESS_PERMISSION_VIEWONLY);
                } else if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.COMPLETED.getValue());
                    appMasterListCompleted.add(appMasterObj);
                } else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.REJECTED.getValue());
                    appMasterListRejected.add(appMasterObj);
                } else if (AppStatus.DELETED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.DELETED.getValue());
                    appMasterListDeleted.add(appMasterObj);
                }
            } else if (accessPermission.equalsIgnoreCase(Constants.ACCESS_PERMISSION_VERIFIER)) {
                if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                        && CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
                        && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_CUSTOMER.getValue());
                    appMasterListInProgress.add(appMasterObj);
                } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                        && !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
                        && !appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
                        && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_OTHERS.getValue());
                    appMasterListInProgress.add(appMasterObj);
                } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                        && !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
                        && appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
                        && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_SELF.getValue());
                    appMasterListInProgress.add(appMasterObj);
                } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                        && !AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                    formQueueStatusForInProgress(appMasterObj, appMasterListInProgress);
                } else if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    formQueueStatusForPending(appMasterObj, appMasterListPending, Constants.ACCESS_PERMISSION_VERIFIER);
                } else if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.COMPLETED.getValue());
                    appMasterListCompleted.add(appMasterObj);
                } else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    if (loggedInUserId.equalsIgnoreCase(appMasterObj.getWfCreatedBy())) { // verifier should not see
                        // rejected applications
                        // rejected by others
                        appMasterObj.setQueueStatus(WidgetQueueStatus.REJECTED.getValue());
                        appMasterListRejected.add(appMasterObj);
                    }
                } else if (AppStatus.DELETED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    appMasterObj.setQueueStatus(WidgetQueueStatus.DELETED.getValue());
                    appMasterListDeleted.add(appMasterObj);
                }
            }
        }
        finalList.add(appMasterListInProgress);
        finalList.add(appMasterListPending);
        finalList.add(appMasterListRejected);
        finalList.add(appMasterListDeleted);
        finalList.add(appMasterListCompleted);
        finalList.add(appMasterListPushBack);
        finalList.add(appMasterListBCMPI);

        return finalList;
    }

    private void formQueueStatusApprover(ApplicationMaster appMasterObj,
                                         List<ApplicationMaster> appMasterListInProgress, List<ApplicationMaster> appMasterListCompleted,
                                         List<ApplicationMaster> appMasterListRejected, List<ApplicationMaster> appMasterListDeleted,
                                         List<ApplicationMaster> appMasterListPending, List<ApplicationMaster> appMasterListPushBack,
                                         List<ApplicationMaster> appMasterListBCMPI, String loggedInUserId, Properties prop) {
        if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                && CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
                && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
            appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_CUSTOMER.getValue());
            appMasterListInProgress.add(appMasterObj);
        } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                && !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
                && !appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
                && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
            appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_OTHERS.getValue());
            appMasterListInProgress.add(appMasterObj);
        } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                && !AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
            formQueueStatusForInProgress(appMasterObj, appMasterListInProgress);
        } else if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
            formQueueStatusForPending(appMasterObj, appMasterListPending, Constants.ACCESS_PERMISSION_APPROVER);
        } else if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
            formQueueStatusApproverApproved(loggedInUserId, appMasterObj, appMasterListCompleted, prop);
        } else if (AppStatus.RPCVERIFIED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
            appMasterListBCMPI.add(appMasterObj);
            appMasterObj.setQueueStatus(WidgetQueueStatus.RPCVERIFIED.getValue());
        } else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
            if (loggedInUserId.equalsIgnoreCase(appMasterObj.getWfCreatedBy())) { // approver should not see rejected
                // applications rejected by others
                appMasterObj.setQueueStatus(WidgetQueueStatus.REJECTED.getValue());
                appMasterListRejected.add(appMasterObj);
            }
        } else if (AppStatus.DELETED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
            appMasterObj.setQueueStatus(WidgetQueueStatus.DELETED.getValue());
            appMasterListDeleted.add(appMasterObj);
        } else if (AppStatus.PUSHBACK.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus()) || AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
            appMasterObj.setQueueStatus(WidgetQueueStatus.PUSHBACK.getValue());
            appMasterListPushBack.add(appMasterObj);
        }
    }

    private void formQueueStatusApproverApproved(String loggedInUserId, ApplicationMaster appMasterObj,
                                                 List<ApplicationMaster> appMasterListCompleted, Properties prop) {
        if (loggedInUserId.equalsIgnoreCase(appMasterObj.getWfCreatedBy())) { // approver should not see completed
            // applications approved by others
            appMasterObj.setQueueStatus(WidgetQueueStatus.COMPLETED.getValue());
            appMasterListCompleted.add(appMasterObj);
        }
        String stpFlag = "";
        if (Products.LOAN.getKey().equalsIgnoreCase(appMasterObj.getProductGroupCode())) {
            stpFlag = prop.getProperty(CobFlagsProperties.LOAN_STP.getKey());
        }
        if (CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy()) && "Y".equalsIgnoreCase(stpFlag)) { // self
            // onboarding
            // applications
            // with STP flag
            // Y should be
            // shown.
            appMasterObj.setQueueStatus(WidgetQueueStatus.COMPLETED.getValue());
            appMasterListCompleted.add(appMasterObj);
        }
    }

    private void formQueueStatusInitiator(ApplicationMaster appMasterObj,
                                          List<ApplicationMaster> appMasterListInProgress, List<ApplicationMaster> appMasterListCompleted,
                                          List<ApplicationMaster> appMasterListRejected, List<ApplicationMaster> appMasterListDeleted,
                                          List<ApplicationMaster> appMasterListPending, List<ApplicationMaster> appMasterListPushBack,
                                          String loggedInUserId) {
        // int rejectionExpiry = Constants.REJECTION_EXPIRY_DAYS;
        Properties prop = null;
        try {
            prop = CommonUtils.readPropertyFile();
        } catch (IOException e) {
            logger.error("Error while reading property file in fetchRole ", e);
        }
        int rejectionExpiry = Integer.parseInt(prop.getProperty(CobFlagsProperties.REJECTION_EXPIRY_DAYS.getKey()));

        if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                && CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
                && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
            appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_CUSTOMER.getValue());
            appMasterListInProgress.add(appMasterObj);
        } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                && !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
                && appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
                && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
            appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_SELF.getValue());
            appMasterListInProgress.add(appMasterObj);
        } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                && !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
                && !appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
                && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
            appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_OTHERS.getValue());
            appMasterListInProgress.add(appMasterObj);
        } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
                && !AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
            formQueueStatusForInProgress(appMasterObj, appMasterListInProgress);
        } else if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
            formQueueStatusForPending(appMasterObj, appMasterListPending, Constants.ACCESS_PERMISSION_INITIATOR);
        } else if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
            appMasterObj.setQueueStatus(WidgetQueueStatus.COMPLETED.getValue());
            appMasterListCompleted.add(appMasterObj);
        } else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
            if (ChronoUnit.DAYS.between(appMasterObj.getWfCreateTs(), LocalDateTime.now()) > rejectionExpiry) {
                appMasterObj.setQueueStatus(WidgetQueueStatus.REJECTED.getValue());
                appMasterListRejected.add(appMasterObj);
            }
        } else if (AppStatus.DELETED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
            appMasterObj.setQueueStatus(WidgetQueueStatus.DELETED.getValue());
            appMasterListDeleted.add(appMasterObj);
        } else if (AppStatus.PUSHBACK.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus()) || AppStatus.IPUSHBACK.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
            appMasterObj.setQueueStatus(WidgetQueueStatus.PUSHBACK.getValue());
            appMasterListPushBack.add(appMasterObj);
        } else if (AppStatus.CAPUSHBACK.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
            appMasterObj.setQueueStatus(WidgetQueueStatus.PUSHBACK.getValue());
            appMasterListPushBack.add(appMasterObj);
        }
    }

    private void formQueueStatusForPending(ApplicationMaster appMasterObj, List<ApplicationMaster> appMasterListPending,
                                           String accessPermission) {
        if (Constants.ACCESS_PERMISSION_VERIFIER.equalsIgnoreCase(accessPermission)) { // verifier should not see
            // pending for approval
            if (WorkflowStatus.PENDING_FOR_VERIFICATION.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                appMasterObj.setQueueStatus(WidgetQueueStatus.PENDING_FOR_VERIFICATION.getValue());
                appMasterListPending.add(appMasterObj);
            }
        } else if (Constants.ACCESS_PERMISSION_APPROVER.equalsIgnoreCase(accessPermission)) { // approver should not see
            // pending for
            // verification
            if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                appMasterObj.setQueueStatus(WidgetQueueStatus.PENDING_FOR_APPROVAL.getValue());
                appMasterListPending.add(appMasterObj);
            }
        } else { // generic logic for initiator, view only, both accessPermission. Change based
            // on requirement.
            if (WorkflowStatus.PENDING_FOR_VERIFICATION.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                appMasterObj.setQueueStatus(WidgetQueueStatus.PENDING_FOR_VERIFICATION.getValue());
                appMasterListPending.add(appMasterObj);
            } else if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
                appMasterObj.setQueueStatus(WidgetQueueStatus.PENDING_FOR_APPROVAL.getValue());
                appMasterListPending.add(appMasterObj);
            }
        }
    }

    private void formQueueStatusForInProgress(ApplicationMaster appMasterObj,
                                              List<ApplicationMaster> appMasterListInProgress) {
        if (WorkflowStatus.PENDING_IN_QUEUE.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
            appMasterObj.setQueueStatus(WidgetQueueStatus.PENDING_IN_QUEUE.getValue());
            appMasterListInProgress.add(appMasterObj);
        } else if (WorkflowStatus.QUEUED_ASSIGNED.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
            appMasterObj.setQueueStatus(WidgetQueueStatus.ASSIGNED.getValue());
            appMasterListInProgress.add(appMasterObj);
        } else if (WorkflowStatus.PENDING_FOR_VERIFICATION.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
            appMasterObj.setQueueStatus(WidgetQueueStatus.PENDING_FOR_VERIFICATION.getValue());
            appMasterListInProgress.add(appMasterObj);
        }
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "viewAllRecordsFallback")
    public Response viewAllRecords(ViewAllRecordsRequest apiRequest, Properties prop) {
        List<String> kendraIds = null;
        List<String> branches = null;
        String responseStr = "";
        Gson gson = new Gson();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        ViewAllRecordsRequestFields requestObj = apiRequest.getRequestObj();
        // String roleId = commonService.fetchRoleId(apiRequest.getAppId(),
        // requestObj.getUserId());
        String roleId = requestObj.getRoleId();
        logger.debug("RPC ViewAll roleId : " + roleId.toString());
        String branchCode = requestObj.getBranchCode();
        String fetchType = requestObj.getFetchType() != null ? requestObj.getFetchType() : "";
        RoleAccessMap objDb = fetchRoleAccessMapObj(apiRequest.getAppId(), roleId);

        List<String> dbFeaturesList = fetchAllowedStatusListForRole(objDb, Constants.FEATURE_DASHBOARD_WIDGETS);

        if (dbFeaturesList != null && dbFeaturesList.contains(requestObj.getStatus())) { // VAPT
            long numOfDays = Long.parseLong(prop.getProperty(CobFlagsProperties.NUM_OF_DAYS_RECORDS.getKey()));
            LocalDate toDay = LocalDate.now();
            LocalDate fromDay = toDay.minusDays(numOfDays);
            // String branchCode = fetchBranchCode(apiRequest.getAppId(),
            // requestObj.getUserId());
            if (CobFlagsProperties.RPC.getKey().equalsIgnoreCase(roleId)) {
                branches = fetchBranchesByUserIdAndRoleId(requestObj.getUserId(), requestObj.getRoleId(), branchCode,
                        prop.getProperty(CobFlagsProperties.APZ_USER_ROLE.getKey()));
            } else {
                kendraIds = fetchKendrasByUserIsAndRoleId(requestObj.getUserId(), requestObj.getRoleId(), branchCode,
                        prop.getProperty(CobFlagsProperties.APZ_USER_ROLE.getKey()), fetchType);
            }
            List<ApplicationMaster> list = new ArrayList<ApplicationMaster>();
            if (CobFlagsProperties.RPC.getKey().equalsIgnoreCase(roleId)) {
                List<String> status = fetchAllowedStatusListForRole(objDb, requestObj.getStatus());
                logger.debug("RPC ViewAll status : " + status.toString());
                if (Constants.RENEWAL.equalsIgnoreCase(requestObj.getFetchType())) {
                    list = applicationMasterRepository.fetchRenewalDashBoardDataBranchIdsIn(status, branches, fromDay,
                            toDay);
                } else {
                    list = applicationMasterRepository.fetchNewDashBoardDataBranchIdsIn(status, branches, fromDay,
                            toDay);
                }
                responseStr = gson.toJson(list);
            } else {
                if (requestObj.getStatus().equalsIgnoreCase(AppStatus.PUSHBACK.getValue()) || requestObj.getStatus().equalsIgnoreCase(AppStatus.IPUSHBACK.getValue())) {
                    if (Constants.RENEWAL.equalsIgnoreCase(requestObj.getFetchType())) {
                        list = applicationMasterRepository.fetchRenewalDashBoardData1(requestObj.getStatus(), kendraIds,
                                fromDay, toDay);
                    } else {
                        list = applicationMasterRepository.fetchNewDashBoardData1(requestObj.getStatus(), kendraIds, fromDay,
                                toDay);//created new queries for sending capushback cases with pushback cases in sourcing stage
                    }
                } else {
                    if (Constants.RENEWAL.equalsIgnoreCase(requestObj.getFetchType())) {
                        list = applicationMasterRepository.fetchRenewalDashBoardData(requestObj.getStatus(), kendraIds,
                                fromDay, toDay);
                    } else {
                        list = applicationMasterRepository.fetchNewDashBoardData(requestObj.getStatus(), kendraIds, fromDay,
                                toDay);
                    }
                }

                List<List<ApplicationMaster>> listOflist = formQueueStatus(list, requestObj.getUserId(),
                        objDb.getAccessPermission(), prop);
                List<ApplicationMaster> finalList = new ArrayList<>();
                for (List<ApplicationMaster> listOfMaster : listOflist) {
                    finalList.addAll(listOfMaster);
                }
                responseStr = gson.toJson(finalList);
            }
            responseBody.setResponseObj(responseStr);
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        } else {
            responseBody.setResponseObj(ResponseCodes.VAPT_ISSUE_STATUS.getValue());
            responseHeader.setResponseCode(ResponseCodes.VAPT_ISSUE_STATUS.getKey());
        }
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "statusReportFallback")
    public Response statusReport(StatusReportRequest apiRequest, Properties prop) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    private List<ApplicationMaster> formWidgetCounts(List<ApplicationMaster> widgetData, String loggedInUserId,
                                                     String accessPermission) {
        long inProgressCount = 0, completedCount = 0, rejectedCount = 0, pendingCount = 0;
        logger.warn("inside formWidgetCounts accessPermission " + accessPermission);
        for (ApplicationMaster appMasterObj : widgetData) {
            if (accessPermission.equalsIgnoreCase(Constants.ACCESS_PERMISSION_INITIATOR)) { // Generic logic; either
                // merge or customize based
                // on requirement
                if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    completedCount++;
                } else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    rejectedCount++;
                } else if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    pendingCount++;
                } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    inProgressCount++;
                }
            } else if (accessPermission.equalsIgnoreCase(Constants.ACCESS_PERMISSION_APPROVER)) {
                if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    if (loggedInUserId.equalsIgnoreCase(appMasterObj.getWfCreatedBy())) { // approver should not see
                        // completed applications
                        // approved by others
                        completedCount++;
                    }
                    if (CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())) { // self onboarding applications
                        // created in CBS directly should be
                        // visible in count
                        completedCount++;
                    }
                } else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    if (loggedInUserId.equalsIgnoreCase(appMasterObj.getWfCreatedBy())) { // approver should not see
                        // rejected applications
                        // rejected by others
                        rejectedCount++;
                    }
                } else if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) { // approver
                        // should
                        // not
                        // see
                        // pending
                        // for
                        // verification
                        pendingCount++;
                    }
                } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    inProgressCount++;
                }
            } else if (accessPermission.equalsIgnoreCase(Constants.ACCESS_PERMISSION_BOTH)) { // Generic logic; either
                // merge or customize
                // based on requirement
                if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    completedCount++;
                } else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    rejectedCount++;
                } else if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    pendingCount++;
                } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    inProgressCount++;
                }
            } else if (accessPermission.equalsIgnoreCase(Constants.ACCESS_PERMISSION_VIEWONLY)) { // Generic logic;
                // either merge or
                // customize based
                // on requirement
                if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    completedCount++;
                } else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    rejectedCount++;
                } else if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    pendingCount++;
                } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    inProgressCount++;
                }
            } else if (accessPermission.equalsIgnoreCase(Constants.ACCESS_PERMISSION_VERIFIER)) {
                if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    completedCount++;
                } else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    if (loggedInUserId.equalsIgnoreCase(appMasterObj.getWfCreatedBy())) { // verifier should not see
                        // rejected applications
                        // rejected by others
                        rejectedCount++;
                    }
                }
                if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    if (WorkflowStatus.PENDING_FOR_VERIFICATION.getValue()
                            .equalsIgnoreCase(appMasterObj.getWfStatus())) { // verifier should not see pending for
                        // approval
                        pendingCount++;
                    }
                } else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
                    inProgressCount++;
                }
            }
        }
        ApplicationMaster appMasterApproved = new ApplicationMaster(AppStatus.APPROVED.getValue(), completedCount);
        ApplicationMaster appMasterRejected = new ApplicationMaster(AppStatus.REJECTED.getValue(), rejectedCount);
        ApplicationMaster appMasterPending = new ApplicationMaster(AppStatus.PENDING.getValue(), pendingCount);
        ApplicationMaster appMasterInprogress = new ApplicationMaster(AppStatus.INPROGRESS.getValue(), inProgressCount);
        List<ApplicationMaster> widgetCounts = new ArrayList<>();
        widgetCounts.add(appMasterApproved);
        widgetCounts.add(appMasterRejected);
        widgetCounts.add(appMasterPending);
        widgetCounts.add(appMasterInprogress);
        return widgetCounts;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "rejectHistoryFallback")
    public Response rejectHistory(PopulateRejectedDataRequest apiRequest) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        Gson gson = new Gson();
        PopulateRejectedDataRequestFields requestObj = apiRequest.getRequestObj();
        List<ApplicationWorkflow> list = applnWfRepository
                .findByAppIdAndApplicationIdAndApplicationStatusOrderByCreateTsAsc(requestObj.getAppId(),
                        requestObj.getApplicationId(), AppStatus.REJECTED.getValue());
        String responseStr = gson.toJson(list);
        responseBody.setResponseObj(responseStr);
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "updateStatusInMasterFallback")
    public void updateStatusInMaster(PopulateapplnWFRequest apiRequest) {
        PopulateapplnWFRequestFields requestObj = apiRequest.getRequestObj();
        Optional<ApplicationMaster> appMaster = applicationMasterRepository.findByAppIdAndApplicationIdAndVersionNum(
                requestObj.getAppId(), requestObj.getApplicationId(), requestObj.getVersionNum());
        if (appMaster.isPresent()) {
            ApplicationMaster appMasterObj = appMaster.get();
            updateStatus(AppStatus.INPROGRESS.getValue(), appMasterObj, AppStatus.PENDING.getValue());
            if (!CommonUtils.isNullOrEmpty(appMasterObj.getRelatedApplicationId())) {
                Optional<ApplicationMaster> appMasterRelated = applicationMasterRepository
                        .findByAppIdAndApplicationIdAndVersionNum(requestObj.getAppId(),
                                appMasterObj.getRelatedApplicationId(), requestObj.getVersionNum());
                if (appMasterRelated.isPresent()) {
                    ApplicationMaster appMasterObjRelated = appMasterRelated.get();
                    updateStatus(AppStatus.INPROGRESS.getValue(), appMasterObjRelated, AppStatus.PENDING.getValue());
                }
            }
        }
    }

    public void updateStatus(String fromStatus, ApplicationMaster appMasterObj, String toStatus) {
        if (fromStatus.equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
            appMasterObj.setApplicationStatus(toStatus);
            applicationMasterRepository.save(appMasterObj);
        }
    }

    public void updateStatus(ApplicationMaster appMasterObj, String toStatus) {
        logger.debug("Application master data : {}", appMasterObj);
        logger.debug("to Status : {}", toStatus);
        appMasterObj.setApplicationStatus(toStatus);
        appMasterObj.setReverseFeed(0);
        applicationMasterRepository.save(appMasterObj);
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchBanksFallback")
    public Mono<Object> fetchBanks(FetchBanksRequest apiRequest, Header header) {
        return interfaceAdapter.callExternalService(header, apiRequest, apiRequest.getInterfaceName());
    }

    public boolean updateRelatedApplnId(String casaApplnId, String relatedApplnId, String appId, String currenctSrcId,
                                        boolean updateRequired, boolean isSelfOnBoardingHeaderAppId, String casaStatus) {
        Optional<ApplicationMaster> appMasterObj = applicationMasterRepository
                .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, relatedApplnId);
        if (appMasterObj.isPresent()) {
            ApplicationMaster appMasterObjDb = appMasterObj.get();
            appMasterObjDb.setRelatedApplicationId(casaApplnId);
            if (updateRequired) {
                appMasterObjDb.setCurrentScreenId(currenctSrcId);
            }
            if (!isSelfOnBoardingHeaderAppId) { // CASA and DEP/LN (NTB) status should be in sync for backoffice created
                // applications.
                appMasterObjDb.setApplicationStatus(casaStatus);
            }
            applicationMasterRepository.save(appMasterObjDb);
            return true;
        }
        return false;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "updateRelatedApplnIdDetailsFallback")
    public Mono<Response> updateRelatedApplnIdDetails(CreateModifyUserRequest request, Mono<Response> response,
                                                      String appId, boolean isSelfOnBoardingHeaderAppId) {
        return response.flatMap(val -> {
            logger.debug("inside updateRelatedApplnIdDetails val=" + val);
            JSONObject responseJson = new JSONObject(val.getResponseBody().getResponseObj());
            if (responseJson.has(Constants.APPLICATION_ID)) {
                String casaApplnId = (String) responseJson.get(Constants.APPLICATION_ID);
                Optional<ApplicationMaster> appMasterObj = applicationMasterRepository
                        .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, casaApplnId);
                if (appMasterObj.isPresent()) {
                    ApplicationMaster appMasterObjDb = appMasterObj.get();
                    if (!CommonUtils.isNullOrEmpty(appMasterObjDb.getRelatedApplicationId())) {
                        String relatedApplnId = appMasterObjDb.getRelatedApplicationId();
                        CustomerDataFields requestObj = request.getRequestObj();
                        String[] arr = requestObj.getApplicationMaster().getCurrentScreenId().split("~");
                        String currenctSrcId = arr[0];
                        boolean updateRequired = false;
                        if ("Y".equalsIgnoreCase(arr[1])) {
                            updateRequired = true;
                        }
                        if (!updateRelatedApplnId(casaApplnId, relatedApplnId, appId, currenctSrcId, updateRequired,
                                isSelfOnBoardingHeaderAppId, appMasterObjDb.getApplicationStatus())) {
                            return CommonUtils.formFailResponseMono(ResponseCodes.RELATED_APPLN_FAIL.getValue(),
                                    ResponseCodes.RELATED_APPLN_FAIL.getKey());
                        }
                    }
                }
            }
            return Mono.just(val);
        });
    }

    public boolean vaptForFieldsBankingFac(List<BankingFacilities> bankFacilityList, JSONArray stageArray) {
        String fieldName;
        boolean isValid = true;
        for (Object screenElement : stageArray) {
            fieldName = ((String) screenElement).split("~")[0];
            for (BankingFacilities bankfacility : bankFacilityList) {
                BankingFacilitiesPayload bankingfacPayload = bankfacility.getPayload();
                if ("BranchAddress".equalsIgnoreCase(fieldName)) {
                    isValid = commonService.isValidFieldvalue(screenElement, bankingfacPayload.getBranchAddress());
                    if (!isValid) {
                        return false;
                    }
                    isValid = commonService.isValidFieldvalue(screenElement, bankingfacPayload.getBranchName());
                } else if ("MobileBanking".equalsIgnoreCase(fieldName)) {
                    isValid = commonService.isValidFieldvalue(screenElement, bankingfacPayload.getMbRequired());
                } else if ("InternetBanking".equalsIgnoreCase(fieldName)) {
                    isValid = commonService.isValidFieldvalue(screenElement, bankingfacPayload.getIbRequired());
                } else if ("DebitCard".equalsIgnoreCase(fieldName)) {
                    isValid = commonService.isValidFieldvalue(screenElement, bankingfacPayload.getDebitCardRequired());
                } else if ("SMSAlerts".equalsIgnoreCase(fieldName)) {
                    isValid = commonService.isValidFieldvalue(screenElement, bankingfacPayload.getSmsAlertsRequired());
                } else if ("E-Statement".equalsIgnoreCase(fieldName)) {
                    isValid = commonService.isValidFieldvalue(screenElement, bankingfacPayload.getEStmtRequired());
                } else if ("ChequeBook".equalsIgnoreCase(fieldName)) {
                    isValid = commonService.isValidFieldvalue(screenElement, bankingfacPayload.getChequeBookRequired());
                } else if ("Passbook".equalsIgnoreCase(fieldName)) {
                    isValid = commonService.isValidFieldvalue(screenElement, bankingfacPayload.getPassBookRequired());
                }
                if (!isValid) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean vaptForFieldsCrs(List<CRSDetails> crsList, JSONArray stageArray) {
        String fieldName;
        boolean isValid = true;
        CRSDetailsPayload payload;
        for (Object screenElement : stageArray) {
            fieldName = ((String) screenElement).split("~")[0];
            for (CRSDetails crsObj : crsList) {
                payload = crsObj.getPayload();
                if ("CustCountryRes".equalsIgnoreCase(fieldName)) {
                    isValid = commonService.isValidFieldvalue(screenElement, payload.getOtherCountryTaxResidant());
                }
                if (!(CommonUtils.isNullOrEmpty(payload.getOtherCountryTaxResidant()))
                        && "Y".equalsIgnoreCase(payload.getOtherCountryTaxResidant())) {
                    List<TaxDetails> taxDtlsList = payload.getTaxDetailsList();
                    for (TaxDetails taxDtlObj : taxDtlsList) {
                        if ("CountryofTaxResidence".equalsIgnoreCase(fieldName)) {
                            isValid = commonService.isValidFieldvalue(screenElement, taxDtlObj.getCountry());
                            if (!isValid) {
                                return false;
                            }
                            isValid = commonService.isValidFieldvalue(screenElement, taxDtlObj.getCountryCode());
                        } else if ("TINavilability".equalsIgnoreCase(fieldName)) {
                            isValid = commonService.isValidFieldvalue(screenElement, taxDtlObj.getCustomerHasTin());
                        }
                        if (!(CommonUtils.isNullOrEmpty(taxDtlObj.getCustomerHasTin()))
                                && "Y".equalsIgnoreCase(taxDtlObj.getCustomerHasTin())) {
                            if ("TINtype".equalsIgnoreCase(fieldName)) {
                                isValid = commonService.isValidFieldvalue(screenElement, taxDtlObj.getTinType());
                            } else if ("TIN".equalsIgnoreCase(fieldName)) {
                                isValid = commonService.isValidFieldvalue(screenElement, taxDtlObj.getTin());
                            }
                        } else if (!(CommonUtils.isNullOrEmpty(taxDtlObj.getCustomerHasTin()))
                                && "N".equalsIgnoreCase(taxDtlObj.getCustomerHasTin())) {
                            if ("ReasonforNotHavingTIN".equalsIgnoreCase(fieldName)) {
                                isValid = commonService.isValidFieldvalue(screenElement, taxDtlObj.getReason());
                            } else if ("Remarks".equalsIgnoreCase(fieldName)) {
                                isValid = commonService.isValidFieldvalue(screenElement, taxDtlObj.getRemarks());
                            }
                        }
                    }
                }
                if (!isValid) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean vaptForFieldsFatca(List<FatcaDetails> fatcaList, JSONArray stageArray) {
        String fieldName;
        boolean isValid = true;
        for (Object screenElement : stageArray) {
            fieldName = ((String) screenElement).split("~")[0];
            for (FatcaDetails fatcaObj : fatcaList) {
                FatcaDetailsPayload payload = fatcaObj.getPayload();
                if ("isUSCitizen".equalsIgnoreCase(fieldName)) {
                    isValid = commonService.isValidFieldvalue((String) screenElement, payload.getUsCitizenFlag());
                }
                if (!(CommonUtils.isNullOrEmpty(payload.getUsCitizenFlag()))
                        && "Y".equalsIgnoreCase(payload.getUsCitizenFlag())) {
                    if ("docType".equalsIgnoreCase(fieldName)) {
                        isValid = commonService.isValidFieldvalue((String) screenElement, payload.getDocumentIdName());
                    } else if ("docTypeNum".equalsIgnoreCase(fieldName)) {
                        isValid = commonService.isValidFieldvalue((String) screenElement, payload.getDocumentIdValue());
                    }
                }
                if (!isValid) {
                    return false;
                }
            }
        }
        return true;
    }

    public void duplicateCasaTables(ApplicationMaster appMaster, int newVersionNum, String applicationId, String appId,
                                    int oldVersionNum) {
        BigDecimal oldCustDtlId;
        BigDecimal newCustDtlId;
        commonService.duplicateMasterData(appMaster, newVersionNum);
        List<CustomerDetails> custList = customerDetailsRepository
                .findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, oldVersionNum);

        for (CustomerDetails custObj : custList) {
            oldCustDtlId = custObj.getCustDtlId();
            newCustDtlId = CommonUtils.generateRandomNum();
            CustomerDetails custNewObj = commonService.duplicateCustomerData(custObj, newVersionNum, newCustDtlId);

            // populate corresponding address data
            Optional<AddressDetails> personalAddressObj = addressDetailsRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndUniqueId(applicationId, appId, oldVersionNum,
                            oldCustDtlId);
            if (personalAddressObj.isPresent()) {
                AddressDetails addressObj = personalAddressObj.get();
                duplicateAddressData(addressObj, newVersionNum, newCustDtlId, custNewObj.getCustDtlId());
            }

            List<NomineeDetails> nomineeDetailsList = nomineeDetailsRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndStatusAndCustDtlId(applicationId, appId, oldVersionNum,
                            AppStatus.ACTIVE_STATUS.getValue(), oldCustDtlId);
            if (nomineeDetailsList != null) {
                for (NomineeDetails nomineeObj : nomineeDetailsList) {
                    NomineeDetails nomineeNewObj = commonService.duplicateNomineeData(nomineeObj, newVersionNum,
                            newCustDtlId);
                    // populate corresponding address data
                    Optional<AddressDetails> nomineeAddressObj = addressDetailsRepository
                            .findByApplicationIdAndAppIdAndVersionNumAndUniqueId(applicationId, appId, oldVersionNum,
                                    nomineeObj.getNomineeDtlsId());
                    if (nomineeAddressObj.isPresent()) {
                        AddressDetails addressObj = nomineeAddressObj.get();
                        duplicateAddressData(addressObj, newVersionNum, newCustDtlId, nomineeNewObj.getNomineeDtlsId());
                    }
                }
            }

            List<OccupationDetails> occupationDetailsList = occupationDetailsRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(applicationId, appId, oldVersionNum,
                            oldCustDtlId);
            for (OccupationDetails occupationObj : occupationDetailsList) {
                OccupationDetails occupationNewObj = commonService.duplicateOccupationData(occupationObj, newVersionNum,
                        newCustDtlId);

                // populate corresponding address data
                Optional<AddressDetails> occupationAddressObj = addressDetailsRepository
                        .findByApplicationIdAndAppIdAndVersionNumAndUniqueId(applicationId, appId, oldVersionNum,
                                occupationObj.getOccptDtlId());
                if (occupationAddressObj.isPresent()) {
                    AddressDetails addressObj = occupationAddressObj.get();
                    duplicateAddressData(addressObj, newVersionNum, newCustDtlId, occupationNewObj.getOccptDtlId());
                }
            }

            List<ApplicationDocuments> applicationDocumentsList = applicationDocumentsRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndStatusAndCustDtlId(applicationId, appId, oldVersionNum,
                            AppStatus.ACTIVE_STATUS.getValue(), oldCustDtlId);
            ApplicationDocuments docNewObj = null;
            for (ApplicationDocuments docObj : applicationDocumentsList) {
                commonService.duplicateDocsData(docNewObj, docObj, newVersionNum, newCustDtlId);
            }

            List<BankingFacilities> bankingFacilityList = bankingFacilitiesRepository
                    .findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(applicationId, appId, oldVersionNum,
                            oldCustDtlId);
            for (BankingFacilities bankFacObj : bankingFacilityList) {
                duplicateBankFacData(bankFacObj, newVersionNum, newCustDtlId);
            }

            List<FatcaDetails> fatcaList = fatcaDtlsrepository.findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(
                    applicationId, appId, oldVersionNum, oldCustDtlId);
            for (FatcaDetails fatcaObj : fatcaList) {
                duplicateFatcaData(fatcaObj, newVersionNum, newCustDtlId);
            }

            List<CRSDetails> crsList = crsDtlsrepository.findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(
                    applicationId, appId, oldVersionNum, oldCustDtlId);
            for (CRSDetails crsObj : crsList) {
                duplicateCrsData(crsObj, newVersionNum, newCustDtlId);
            }
        }
    }

    private void duplicateCrsData(CRSDetails crsObj, int newVersionNum, BigDecimal newCustDtlId) {
        CRSDetails crs = new CRSDetails();
        BeanUtils.copyProperties(crsObj, crs);
        crsObj.setCrsDtlId(CommonUtils.generateRandomNum());
        crsObj.setVersionNum(newVersionNum);
        crsObj.setCustDtlId(newCustDtlId);
        crsDtlsrepository.save(crsObj);
    }

    private void duplicateFatcaData(FatcaDetails fatcaObj, int newVersionNum, BigDecimal newCustDtlId) {
        FatcaDetails fatca = new FatcaDetails();
        BeanUtils.copyProperties(fatca, fatcaObj);
        fatca.setFatcaDtlsId(CommonUtils.generateRandomNum());
        fatca.setVersionNum(newVersionNum);
        fatca.setCustDtlId(newCustDtlId);
        fatcaDtlsrepository.save(fatca);
    }

    private void duplicateAddressData(AddressDetails addressObj, int newVersionNum, BigDecimal newCustDtlId,
                                      BigDecimal uniqueId) {
        AddressDetails addressNewObj = new AddressDetails();
        BeanUtils.copyProperties(addressObj, addressNewObj);
        addressNewObj.setUniqueId(uniqueId);
        addressNewObj.setAddressDtlsId(CommonUtils.generateRandomNum());
        addressNewObj.setVersionNum(newVersionNum);
        addressNewObj.setCustDtlId(newCustDtlId);
        addressDetailsRepository.save(addressNewObj);
    }

    private void duplicateBankFacData(BankingFacilities bankFacObj, int newVersionNum, BigDecimal newCustDtlId) {
        BankingFacilities bankFacNewObj = new BankingFacilities();
        BeanUtils.copyProperties(bankFacObj, bankFacNewObj);
        bankFacNewObj.setBankFacilityId(CommonUtils.generateRandomNum());
        bankFacNewObj.setVersionNum(newVersionNum);
        bankFacNewObj.setCustDtlId(newCustDtlId);
        bankingFacilitiesRepository.save(bankFacNewObj);
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "advanceSearchApplicationsFallback")
    public Response advanceSearchApplications(AdvanceSearchAppRequest apiRequest) {
        logger.debug("Request for Advance Search :: " + apiRequest.getRequestObj());
        Gson gson = new Gson();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        AdvanceSearchAppRequestFields reqFields = apiRequest.getRequestObj();
        String roleId = commonService.fetchRoleId(apiRequest.getAppId(), reqFields.getUserId());
        RoleAccessMap objDb = fetchRoleAccessMapObj(apiRequest.getAppId(), roleId);
        List<String> dbFeaturesList = fetchAllowedStatusListForRole(objDb, Constants.FEATURE_SEARCH);
        if (null != dbFeaturesList && !dbFeaturesList.isEmpty()) {
            String branchCode = fetchBranchCode(apiRequest.getAppId(), reqFields.getUserId());
            String mobileNum = reqFields.getMobileNo();
            List<String> applicationStatus = reqFields.getApplicationStatus();
            if (null != applicationStatus && applicationStatus.size() > 0) {
                applicationStatus.retainAll(dbFeaturesList);
            }
            String product = reqFields.getProduct();
            List<String> subProduct = reqFields.getSubProduct();
            LocalDate startDate = LocalDate.parse(reqFields.getStartDate());
            LocalDate endDate = LocalDate.parse(reqFields.getEndDate());
            List<ApplicationMaster> appMasterList = applicationMasterRepository.advanceSearchApplications(mobileNum,
                    subProduct, product, applicationStatus, startDate, endDate, branchCode);
            responseBody.setResponseObj(gson.toJson(appMasterList));
        } else {
            responseBody.setResponseObj("");
        }
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    public JSONArray fetchFunctionSeqArray(CreateModifyUserRequest request, boolean isSelfOnBoardingHeaderAppId) {
        JSONArray array = null;
        CustomerDataFields reqObj = request.getRequestObj();
        ApplicationMaster applicationMaster = reqObj.getApplicationMaster();
        if (isSelfOnBoardingHeaderAppId) {
            if ("N".equalsIgnoreCase(reqObj.getIsExistingCustomer())) {
                array = fetchFunctionSeqArrayNTB(applicationMaster);
            } else if ("Y".equalsIgnoreCase(reqObj.getIsExistingCustomer())) {
                // We don't have existing customer flow for CASA.
            }
        } else {
            if ("N".equalsIgnoreCase(reqObj.getIsExistingCustomer())) {
                array = fetchFunctionSeqArrayETB(applicationMaster);

            } else if ("Y".equalsIgnoreCase(reqObj.getIsExistingCustomer())) {
                // We don't have existing customer flow for CASA.
            }
        }
        return array;
    }

    private JSONArray fetchFunctionSeqArrayETB(ApplicationMaster applicationMaster) {
        JSONArray array = null;
        if (Products.LOAN.getKey().equalsIgnoreCase(applicationMaster.getMainProductGroupCode())) { // creating
            // CASA
            // account
            // as part
            // of loan
            // account
            // when
            // customer
            // is new
            array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE,
                    CodeTypes.LOAN_BO_NTB.getKey(), Constants.FUNCTIONSEQUENCE);
        }
        return array;
    }

    private JSONArray fetchFunctionSeqArrayNTB(ApplicationMaster applicationMaster) {
        JSONArray array = null;
        if (Products.LOAN.getKey().equalsIgnoreCase(applicationMaster.getMainProductGroupCode())) { // creating
            // CASA
            // account
            // as part
            // of loan
            // account
            // when
            // customer
            // is new
            array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.LOAN_NTB.getKey(),
                    Constants.FUNCTIONSEQUENCE);
        }
        return array;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "updateLovFallback")
    public Response updateLov(UpdateLovRequest apiRequest) {
        UpdateLovRequestFields requestObj = apiRequest.getRequestObj();
        lovMasterRepository.saveAll(requestObj.getLovMasterList());
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        ResponseBody responseBody = new ResponseBody();
        responseBody.setResponseObj("");
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "updateApplicantsCountFallback")
    public Response updateApplicantsCount(UpdateApplicantsCountRequest apiRequest) {
        UpdateApplicantsCountRequestFields requestObj = apiRequest.getRequestObj();
        logger.debug("Inside updateApplicantsCount requestObj is" + requestObj.getApplicationId());
        Optional<ApplicationMaster> appMaster = applicationMasterRepository.findByAppIdAndApplicationIdAndVersionNum(
                requestObj.getAppId(), requestObj.getApplicationId(), requestObj.getVersionNum());
        if (appMaster.isPresent()) {
            ApplicationMaster appMasterObj = appMaster.get();
            appMasterObj.setApplicantsCount(requestObj.getApplicantsCount());
            applicationMasterRepository.save(appMasterObj);
            if (CommonUtils.isNullOrEmpty(appMasterObj.getRelatedApplicationId())) {
                Optional<ApplicationMaster> appMasterRelated = applicationMasterRepository
                        .findByAppIdAndApplicationIdAndVersionNum(requestObj.getAppId(),
                                appMasterObj.getRelatedApplicationId(), requestObj.getVersionNum());
                if (appMasterRelated.isPresent()) {
                    ApplicationMaster appMasterObjRelated = appMasterRelated.get();
                    appMasterObjRelated.setApplicantsCount(requestObj.getApplicantsCount());
                    applicationMasterRepository.save(appMasterObjRelated);
                }
            }
        }
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        ResponseBody responseBody = new ResponseBody();
        responseBody.setResponseObj("");
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    // @CircuitBreaker(name = "fallback", fallbackMethod =
    // "collectionServiceCheckFallback")
    public Response collectionServiceCheck() {
        logger.debug("Inside collectionServiceCheck method");
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        ResponseBody responseBody = new ResponseBody();
        responseBody.setResponseObj("");
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        logger.debug("Inside collectionServiceCheck method resp" + response);
        return response;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "generateReportFallback")
    public Response generateReport(CustomerDataFields customerDataFields) {

        return null;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "dbKitDocGenerationAndDownloadFallback")
    public Response dbKitDocGenerationAndDownload(UploadDocumentRequestFields requestObj)
            throws DRException, IOException, JRException {
        logger.debug("Entering dbKitDocGenerationAndDownload with requestObj: {}", requestObj);
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        String applicationId = requestObj.getApplicationId();
        String documentGenerationType = requestObj.getDocumentGenerationType();
        String appId = requestObj.getAppId();
        String documentType = requestObj.getDocumentType();

        Gson gson = new Gson();
        List<JsonObject> fileList = new ArrayList<>();
        String userId = requestObj.getUserId();
        String productCode = requestObj.getProductGroupCode();
        Properties prop = null;
        String docSize = requestObj.getDocSize();
        if (docSize == null) {
            docSize = "0";
        }
        Boolean mergeDocument = requestObj.getMergeDocument().equalsIgnoreCase("Y");
        try {
            prop = CommonUtils.readPropertyFile();
        } catch (IOException e) {
            logger.error("Error while reading property file in dbKitDocGenerationAndDownload ", e);
        }
        if (prop == null) {
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }

        String reportLanguages = prop.getProperty(CobFlagsProperties.REPORT_LANGUAGES.getKey());
        String[] reportLanguageArr = reportLanguages.split(",");
        String inputLanguage = requestObj.getDocumentLanguage();
        logger.debug("inputLanguage: " + inputLanguage);
        boolean isValidLanguage = Arrays.stream(reportLanguageArr)
                .anyMatch(lang -> lang.equalsIgnoreCase(inputLanguage));
        String language = isValidLanguage ? inputLanguage : Constants.DEFAULTLANGUAGE;
        logger.debug("final Language:" + language);

        switch (documentGenerationType.toUpperCase()) {
            case Constants.MANUAL:
                return handleManualDocumentGeneration(requestObj, prop, applicationId, appId, documentType, userId, productCode, docSize);

            case Constants.GENERATED:
                return handleAutoDocumentGeneration(prop, appId, applicationId, language, gson, fileList, userId, productCode, mergeDocument);

            case Constants.ESIGN:
                logger.info("E-sign document generation service for applicationId: {}", applicationId);
                // Call the e-sign service to generate the document
                break;

            case Constants.DELETE:
                logger.info("Delete document for applicationId: {}", applicationId);
                return handleDeleteManualDocuments(prop, appId, applicationId, documentType);

            case Constants.FETCH:
                logger.info("fetch documents for applicationId: {}", applicationId);
                return handleFetchUploadedDocuments(prop, appId, applicationId, gson, mergeDocument, language, userId, productCode);

            case Constants.WELCOMEKIT:
                logger.info("WELCOMEKIT documents for applicationId: {}", applicationId);
                return handleAutoDocumentGenerationWelcomeKit(prop, appId, applicationId, language, gson, fileList, userId, productCode, mergeDocument);

            case Constants.REGENERATE:
                logger.info("Regenerate documents for applicationId: {}", applicationId);
                return handleRegenerateDocuments(prop, appId, applicationId, language, gson, userId, productCode, fileList, mergeDocument);

            //save handleSingleDocument
            case Constants.SINGLE_DOC:
                return handleSingleDocument(requestObj, prop, applicationId, appId, documentType, userId, productCode, docSize, mergeDocument);

            case Constants.FETCH_MANUAL:
                return handleMergeAndFetchDocuments(prop, appId, applicationId, gson);

            default:
                logger.warn("Invalid document generation type: {}", documentGenerationType);
                return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }

        logger.debug("Exiting dbKitDocGenerationAndDownload with response: {}", response);
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    private Response handleRegenerateDocuments(Properties prop, String appId, String applicationId, String language, Gson gson, String userId, String productCode,
                                               List<JsonObject> fileList, boolean mergeDocument) throws IOException, DRException, JRException {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();

        String[] documentOrder = Constants.DOCUMENT_TYPES.split(",");
        Map<String, Integer> docTypeOrderMap = new HashMap<>();
        for (int i = 0; i < documentOrder.length; i++) {
            docTypeOrderMap.put(documentOrder[i], i + 1); // Order starts from 1
        }
        String filePathGen = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId + "/" + Constants.LOAN + "/"
                + applicationId + "/" + Constants.GENERATED + "/";
        File directoryGen = new File(filePathGen);
        if (!directoryGen.exists() && !directoryGen.mkdirs()) {
            logger.error("Failed to create directory: {}", filePathGen);
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }
        Optional<List<Documents>> documentRecordListOpt = documentsRepository.findByApplicationId(applicationId);
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        ByteArrayOutputStream mergedOutputStream = new ByteArrayOutputStream();
        pdfMerger.setDestinationStream(mergedOutputStream);

        int versionNum = Constants.INITIAL_VERSION_NO;
        //
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, versionNum);

        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            String product = applicationMasterData.getProductCode();
            logger.debug("product :" + product);
            CustomerDataFields custmrDataFields = getCustomerData(applicationMasterData, applicationId, appId,
                    versionNum);

            Optional<BCMPIStageVerification> bcmpiStageData = bcmpiStageVerificationRepository.findById(applicationId);
            if (bcmpiStageData.isPresent()) {
                logger.debug("bcmpiStageData found");
                custmrDataFields.setBcmpiStatDetails(CommonUtils.parseBCMPIStageVerificationData(
                        bcmpiStageData.get().getEditedFields(), bcmpiStageData.get().getQueries()));
                custmrDataFields.setBcmpiVerifiedStage(
                        null != bcmpiStageData.get().getVerifiedStages()
                                ? Arrays.asList(bcmpiStageData.get().getVerifiedStages().split("\\|"))
                                : new ArrayList<>());
            }

            Optional<BCMPIIncomeDetails> bcmpiIncomeDataOpt = bcmpiIncomeDetailsRepo.findById(applicationId);
            if (bcmpiIncomeDataOpt.isPresent()) {
                logger.debug("bcmpiIncomeData found");
                BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper = gson.fromJson(
                        bcmpiIncomeDataOpt.get().getPayload(), BCMPIIncomeDetailsWrapper.class);
                BCMPIIncomeDetails bcmpiIncomeDetails = bcmpiIncomeDataOpt.get();
                bcmpiIncomeDetails.setBcmpiIncomeDetailsWrapper(bcmpiIncomeDetailsWrapper);
                custmrDataFields.setBcmpiIncomeDetails(bcmpiIncomeDetails);
            }

            Optional<BCMPILoanObligations> bcmpiLoanObligationsOpt = bcmpiLoanObligationsRepo.findById(applicationId);
            if (bcmpiLoanObligationsOpt.isPresent()) {
                logger.debug("bcmpiLoanObligations found");
                LoanObligationsWrapper loanObligationsWrapper = gson.fromJson(
                        bcmpiLoanObligationsOpt.get().getPayload(), LoanObligationsWrapper.class);
                BCMPILoanObligations bcmpiLoanObligations = bcmpiLoanObligationsOpt.get();
                bcmpiLoanObligations.setLoanObligationsWrapper(loanObligationsWrapper);
                custmrDataFields.setBcmpiLoanObligations(bcmpiLoanObligations);
            }

            Optional<BCMPIOtherDetails> bcmpiOtherDetailsOpt = bcmpiOtherDetailsRepo.findById(applicationId);
            if (bcmpiOtherDetailsOpt.isPresent()) {
                logger.debug("bcmpiOtherDetails found");
                BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = gson.fromJson(
                        bcmpiOtherDetailsOpt.get().getPayload(), BCMPIOtherDetailsWrapper.class);
                BCMPIOtherDetails bcmpiOtherDetails = bcmpiOtherDetailsOpt.get();
                bcmpiOtherDetails.setBcmpiOtherDetailsWrapper(bcmpiOtherDetailsWrapper);
                custmrDataFields.setBcmpiOtherDetails(bcmpiOtherDetails);
            }

            List<DeviationRATracker> deviationRecords = deviationRATrackerRepository
                    .findByApplicationId(custmrDataFields.getApplicationId());
            logger.debug("Size of deviationRecords: " + deviationRecords.isEmpty());

            Optional<SanctionMaster> sanctionAuthority = sanctionMasterRepositoy.findByProductAndValueBetween(product,
                    custmrDataFields.getLoanDetails().getLoanAmount());
            logger.debug("Loan Amount for sanction Approval: " + custmrDataFields.getLoanDetails().getLoanAmount());
            logger.debug("custmrDataFields.toString(): " + custmrDataFields.toString());
            logger.debug("Size of sanctionAuthority: " + sanctionAuthority.isPresent());
            boolean regenerate = true;
            boolean generateForm97 = false;
            Gson gsonObj = new Gson();
            if (custmrDataFields.getCustomerDetailsList() != null) {
                for (CustomerDetails customer : custmrDataFields.getCustomerDetailsList()) {

                    if ("Applicant".equalsIgnoreCase(customer.getCustomerType())) {
                        CustomerDetailsPayload applicantPayload = gsonObj.fromJson(customer.getPayloadColumn(), CustomerDetailsPayload.class);
                        String pan = applicantPayload != null ? applicantPayload.getPanNumber() : null;
                        logger.debug("Applicant PAN : {}", pan);
                        if (pan == null || pan.trim().isEmpty()) {
                            generateForm97 = true;
                            break;
                        }
                    } else if ("Co-App".equalsIgnoreCase(customer.getCustomerType())) {
                        CustomerDetailsPayload coApplicantPayload = gsonObj.fromJson(customer.getPayloadColumn(), CustomerDetailsPayload.class);
                        String pan = coApplicantPayload != null ? coApplicantPayload.getPanNumber() : null;
                        logger.debug("Co-Applicant PAN : {}", pan);
                        if (pan == null || pan.trim().isEmpty()) {
                            generateForm97 = true;
                            break;
                        }
                    }
                }
            }
            // @author Abhishek.Raj.CAG
            if (Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
                    .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())
                    || Constants.FAMILY_WELFARE_PRODUCT_CODE
                    .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())
            ) {
                logger.debug("Entered handleRegenerateDocument if case");
                addGeneratedDocument(fileList, generateLoanApplication(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, regenerate));
                addGeneratedDocument(fileList, generateSanctionLetter(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, regenerate));
                addGeneratedDocument(fileList, generateLoanAgreement(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList, generateDemandPromissoryNote(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList, generateInsuranceConsent(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList, generateKfs(appId, applicationId, versionNum,
                        filePathGen, language, userId, productCode, regenerate));
                addGeneratedDocument(fileList, generateKfsSummary(appId, applicationId, versionNum,
                        filePathGen, language, userId, productCode, regenerate));
                logger.debug("Entered handleRegenerateDocument if case --ended");
                // KFS, KFS summary
            } else if (Constants.UNNATI_EMERGENCY_PRODUCT_CODE
                    .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())) {
                logger.debug("Entered handleRegenerateDocument esle if case");
                addGeneratedDocument(fileList, generateLoanApplication(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, regenerate));
                addGeneratedDocument(fileList, generateSanctionLetter(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, regenerate));
                addGeneratedDocument(fileList, generateLoanAgreement(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList, generateDemandPromissoryNote(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList, generateKfs(appId, applicationId, versionNum,
                        filePathGen, language, userId, productCode, regenerate));
                addGeneratedDocument(fileList, generateKfsSummary(appId, applicationId, versionNum,
                        filePathGen, language, userId, productCode, regenerate));
                logger.debug("Entered handleRegenerateDocument esle if case --ended");
                // KFS, KFS summary
            }
            else if(Constants.UNNATI_RESTART_PRODUCT_CODE
                    .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode()) || Constants.Vishesh_LOAN_PRODUCT_CODE
                    .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())) {
                // existing flow
                logger.debug("Entered handleRegenerateDocument esle case");
            addGeneratedDocument(fileList,
                    generateLoanApplication(appId, applicationId, versionNum, filePathGen, language, userId, productCode, regenerate));
            addGeneratedDocument(fileList,
                    generateSanctionLetter(appId, applicationId, versionNum, filePathGen, language, userId, productCode, regenerate));
            addGeneratedDocument(fileList,
                    generateLoanAgreement(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
            addGeneratedDocument(fileList,
                    generateScheduleA(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
            if (generateForm97) {
                logger.debug("Form97 generation required due to missing PAN.");
                addGeneratedDocument(fileList,
                        generateForm97(appId, applicationId, versionNum, filePathGen,
                                language, userId, productCode, custmrDataFields, regenerate));
            }
                addGeneratedDocument(fileList,
                        generateScheduleB(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList,
                        generateInsuranceConsent(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList, generateMSME(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList,
                        generateConsentLetter(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList,
                        generateDemandPromissoryNote(appId, applicationId, versionNum, filePathGen, language, userId,
                                productCode, custmrDataFields, regenerate));

                logger.debug("Entered handleRegenerateDocument esle case --ended");
            }
            else {
                // existing flow
                logger.debug("Entered handleRegenerateDocument esle case");
                addGeneratedDocument(fileList,
                        generateLoanApplication(appId, applicationId, versionNum, filePathGen, language, userId, productCode, regenerate));
                addGeneratedDocument(fileList,
                        generateSanctionLetter(appId, applicationId, versionNum, filePathGen, language, userId, productCode, regenerate));
                addGeneratedDocument(fileList,
                        generateLoanAgreement(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList,
                        generateScheduleA(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
                if (generateForm97) {
                    logger.debug("Form97 generation required due to missing PAN.");
                    addGeneratedDocument(fileList,
                            generateForm97(appId, applicationId, versionNum, filePathGen,
                                    language, userId, productCode, custmrDataFields, regenerate));
                }
                addGeneratedDocument(fileList,
                        generateScheduleB(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
            addGeneratedDocument(fileList,
                    generateInsuranceConsent(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
            addGeneratedDocument(fileList, generateMSME(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
            addGeneratedDocument(fileList,
                    generateConsentLetter(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
            addGeneratedDocument(fileList,
                    generateDemandPromissoryNote(appId, applicationId, versionNum, filePathGen, language, userId,
                            productCode, custmrDataFields, regenerate));
                logger.debug("Entered handleRegenerateDocument esle case --ended");
            }
            //----END----


            responseBody.setResponseObj(gson.toJson(fileList));

            // language condition
            String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
            logger.debug("inputLanguage" + language);
            boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr).anyMatch(lang -> lang.equalsIgnoreCase(language));
            if (mergeDocument) {
                logger.debug("mergeDocument :" + mergeDocument);
                if (isValidLanguage) {
                    logger.debug("Constants.NEW_VERNCLR_LANGUAGES :" + isValidLanguage);
                    StringBuilder mergedHtml = new StringBuilder();
                    mergedHtml.append("<html><body>");

                    for (JsonObject file : fileList) {
                        //  Expect raw HTML only
                        String htmlContent = file.get(Constants.BASE64).getAsString();

                        // Clean duplicate <html>/<body> tags just to be safe
                        htmlContent = htmlContent.replaceAll("(?i)<\\/?html>|<\\/?body>", "");

                        mergedHtml.append(htmlContent);
                        mergedHtml.append("<hr style='page-break-after: always;'>");
                    }

                    mergedHtml.append("</body></html>");

                    JsonObject mergedHtmlJson = new JsonObject();
                    mergedHtmlJson.addProperty(Constants.MERGEDBASE64, mergedHtml.toString());
                    mergedHtmlJson.addProperty("fileType", "html");
                    responseBody.setResponseObj(gson.toJson(mergedHtmlJson));

                } else {
                    logger.debug("!Constants.NEW_VERNCLR_LANGUAGES");
                    for (JsonObject file : fileList) {
                        String base64 = file.get(Constants.BASE64).getAsString();
                        byte[] pdfBytes = Base64.getDecoder().decode(base64);
                        try (InputStream pdfStream = new ByteArrayInputStream(pdfBytes)) {
                            pdfMerger.addSource(pdfStream);
                        }
                    }
                    pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
                    String mergedBase64String = Base64.getEncoder().encodeToString(mergedOutputStream.toByteArray());
                    JsonObject mergedBase64 = new JsonObject();
                    mergedBase64.addProperty(Constants.MERGEDBASE64, mergedBase64String);

                    responseBody.setResponseObj(gson.toJson(mergedBase64));
                }

            }

            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
        } else {
            logger.error("Application not found for applicationId: {}", applicationId);
        }
        return response;
    }

    private Response handleMergeAndFetchDocuments(Properties prop, String appId, String applicationId, Gson gson)
            throws IOException {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();

        String[] documentOrder = Constants.MANUAL_DOCUMENT_TYPES.split(",");
        Map<String, Integer> docTypeOrderMap = new HashMap<>();
        for (int i = 0; i < documentOrder.length; i++) {
            docTypeOrderMap.put(documentOrder[i], i + 1); // Order starts from 1
        }

        String applicationFolderPath = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId + "/"
                + Constants.LOAN + "/"
                + applicationId + "/";
        Optional<List<Documents>> documentRecordListOpt = documentsRepository.findByApplicationId(applicationId);
        List<JsonObject> fileList = new ArrayList<>();
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        ByteArrayOutputStream mergedOutputStream = new ByteArrayOutputStream();
        pdfMerger.setDestinationStream(mergedOutputStream);

        if (documentRecordListOpt.isPresent()) {
            List<Documents> documentRecordList = documentRecordListOpt.get();
            for (Documents documentRecord : documentRecordList) {
                String fileName = documentRecord.getDocName();
                String fileFoldername = documentRecord.getUploadType();
                String documentType = documentRecord.getDocType();

                if (!docTypeOrderMap.containsKey(documentType)) {
                    logger.warn("Unrecognized documentType '{}' for applicationId '{}'", documentType, applicationId);
                    continue; // Skip if type not in expected list
                }

                String filePath = applicationFolderPath + fileFoldername + "/" + fileName;
                byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                String base64 = Base64.getEncoder().encodeToString(fileBytes);

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(Constants.ORDER, docTypeOrderMap.get(documentType));
                jsonObject.addProperty(Constants.BASE64, base64);
                fileList.add(jsonObject);
            }

            // Sort by order
            fileList.sort(Comparator.comparingInt(o -> o.get(Constants.ORDER).getAsInt()));
            for (JsonObject file : fileList) {
                String base64 = file.get(Constants.BASE64).getAsString();
                byte[] pdfBytes = Base64.getDecoder().decode(base64);
                try (InputStream pdfStream = new ByteArrayInputStream(pdfBytes)) {
                    pdfMerger.addSource(pdfStream);
                }
            }

            pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        } else {
            logger.error("Document records not found for applicationId: {}", applicationId);
        }

        if (fileList.isEmpty()) {
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }
        String mergedBase64String = Base64.getEncoder().encodeToString(mergedOutputStream.toByteArray());
        JsonObject mergedBase64 = new JsonObject();
        mergedBase64.addProperty(Constants.MERGEDBASE64, mergedBase64String);
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        responseBody.setResponseObj(gson.toJson(mergedBase64));
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    public Response handleFetchUploadedDocuments(Properties prop, String appId, String applicationId, Gson gson, Boolean mergeDocument, String language, String userId, String productCode)
            throws IOException, DRException, JRException {
        logger.debug("Entering handleFetchUploadedDocuments with appId: {}, applicationId: {}, mergeDocument: {}, language: {}, userId: {}, productCode: {}", appId, applicationId, mergeDocument, language, userId, productCode);
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        ByteArrayOutputStream mergedOutputStream = new ByteArrayOutputStream();
        pdfMerger.setDestinationStream(mergedOutputStream);
        Set<String> excludedTypes = new HashSet<>(
                Arrays.asList(
                        Constants.AML.toLowerCase(),
                        Constants.NATIONALIDPAN.toLowerCase(),
                        Constants.OTHER_DBDOCS.toLowerCase(),
                        Constants.KFS.toLowerCase()
                )
        );

        String[] documentOrder = Constants.MANUAL_DOCUMENT_TYPES.split(",");
        Map<String, Integer> docTypeOrderMap = new HashMap<>();
        for (int i = 0; i < documentOrder.length; i++) {
            docTypeOrderMap.put(documentOrder[i], i + 1); // Order starts from 1
        }
        List<JsonObject> fileList = new ArrayList<>();

        String applicationFolderPath = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId + "/" + Constants.LOAN + "/"
                + applicationId + "/";
        logger.debug("applicationFolderPath resolved as: {}", applicationFolderPath);


        if (mergeDocument) {
            logger.info("Fetching only GENERATED documents for applicationId: {}", applicationId);
            logger.info("Merging documents for applicationId: {}", applicationId);
            Optional<List<Documents>> documentRecordListOpt = documentsRepository.findByApplicationId(applicationId);
            if (documentRecordListOpt.isPresent() && !documentRecordListOpt.get().isEmpty()) {
                List<Documents> documentRecordList = documentRecordListOpt.get();
                logger.debug("Found {} documents to merge for applicationId: {}", documentRecordList.size(), applicationId);
                for (Documents documentRecord : documentRecordList) {
                    String fileName = documentRecord.getDocName();
                    String fileFoldername = documentRecord.getUploadType();
                    String documentType = documentRecord.getDocType();
                    if (!excludedTypes.contains(documentType.toLowerCase())) {
                        logger.debug("Processing document: fileName={}, fileFoldername={}, documentType={}", fileName, Constants.GENERATED, documentType);

                        if (!docTypeOrderMap.containsKey(documentType)) {
                            logger.warn("Unrecognized documentType '{}' for applicationId '{}'", documentType, applicationId);
                            continue; // Skip if type not in expected list
                        }

                        String filePath = applicationFolderPath + Constants.GENERATED + "/" + applicationId + "_" + documentType + Constants.PDF_EXTENSION;
                        logger.debug("Reading file from path: {}", filePath);
                        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                        String base64 = Base64.getEncoder().encodeToString(fileBytes);

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty(Constants.ORDER, docTypeOrderMap.get(documentType));
                        jsonObject.addProperty(Constants.BASE64, base64);
                        fileList.add(jsonObject);
                    }
                }

                // Sort by order
                fileList.sort(Comparator.comparingInt(o -> o.get(Constants.ORDER).getAsInt()));
                logger.debug("Sorted fileList by order for merging.");
            } else {
                logger.error("Document records not found for applicationId: {}", applicationId);
                if (!language.isEmpty() && !userId.isEmpty() && !productCode.isEmpty()) {
                    logger.info("Falling back to handleAutoDocumentGeneration for applicationId: {}", applicationId);
                    return handleAutoDocumentGeneration(prop, appId, applicationId, language, gson, fileList, userId, productCode, mergeDocument);
                }
            }

            if (fileList.isEmpty()) {
                logger.error("No files found to merge for applicationId: {}", applicationId);
                return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
            }
            for (JsonObject file : fileList) {
                String base64 = file.get(Constants.BASE64).getAsString();
                byte[] pdfBytes = Base64.getDecoder().decode(base64);
                try (InputStream pdfStream = new ByteArrayInputStream(pdfBytes)) {
                    pdfMerger.addSource(pdfStream);
                    logger.debug("Added PDF source for merging for applicationId: {}", applicationId);
                }
            }
            pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
            logger.info("Merged documents for applicationId: {}", applicationId);
            String mergedBase64String = Base64.getEncoder().encodeToString(mergedOutputStream.toByteArray());
            JsonObject mergedBase64 = new JsonObject();
            mergedBase64.addProperty(Constants.MERGEDBASE64, mergedBase64String);

            responseBody.setResponseObj(gson.toJson(mergedBase64));
            logger.debug("Set mergedBase64 in response for applicationId: {}", applicationId);

        } else {
            logger.info("Fetching only MANUAL documents for applicationId: {}", applicationId);
            Optional<List<Documents>> documentRecordListOpt = documentsRepository.findByApplicationIdAndUploadType(applicationId, Constants.MANUAL);
            if (documentRecordListOpt.isPresent() && !documentRecordListOpt.get().isEmpty()) {
                List<Documents> documentRecordList = documentRecordListOpt.get();
                logger.debug("Found {} MANUAL documents for applicationId: {}", documentRecordList.size(), applicationId);
                for (Documents documentRecord : documentRecordList) {
                    String fileName = documentRecord.getDocName();
                    String fileFoldername = documentRecord.getUploadType();
                    String documentType = documentRecord.getDocType();

                    logger.debug("Processing MANUAL document: fileName={}, fileFoldername={}, documentType={}", fileName, fileFoldername, documentType);

                    if (!docTypeOrderMap.containsKey(documentType)) {
                        logger.warn("Unrecognized documentType '{}' for applicationId '{}'", documentType, applicationId);
                        continue; // Skip if type not in expected list
                    }

                    String filePath = applicationFolderPath + fileFoldername + "/" + fileName;
                    logger.debug("Reading MANUAL file from path: {}", filePath);
                    byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                    String base64 = Base64.getEncoder().encodeToString(fileBytes);

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty(Constants.ORDER, docTypeOrderMap.get(documentType));
                    jsonObject.addProperty(Constants.BASE64, base64);
                    jsonObject.addProperty("size", documentRecord.getDocSize());
                    jsonObject.addProperty(Constants.APPLICATION_ID, applicationId);
                    jsonObject.addProperty("docName", fileName);
                    jsonObject.addProperty("appId", appId == null ? "" : appId);
                    jsonObject.addProperty("docType", documentType);
                    jsonObject.addProperty("docStatus", documentRecord.getDocStatus());
                    jsonObject.addProperty("uploadType", documentRecord.getUploadType());
                    jsonObject.addProperty("language", documentRecord.getLanguage());
                    jsonObject.addProperty("productType", documentRecord.getProductType());
                    jsonObject.addProperty("isReupload", documentRecord.getQueryResponse());
                    fileList.add(jsonObject);
                }

                // Sort by order
                fileList.sort(Comparator.comparingInt(o -> o.get(Constants.ORDER).getAsInt()));
                logger.debug("Sorted MANUAL fileList by order for applicationId: {}", applicationId);
            } else {
                logger.error("Document records not found for applicationId: {}", applicationId);
            }
            responseBody.setResponseObj(gson.toJson(fileList));
            logger.debug("Set MANUAL fileList in response for applicationId: {}", applicationId);
        }

        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        logger.debug("Exiting handleFetchUploadedDocuments for applicationId: {}", applicationId);
        return response;
    }

    public Response handleDeleteAllDocuments(Properties prop, String appId, String applicationId) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        String filePath = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId + "/" + Constants.LOAN + "/"
                + applicationId + "/";
        Optional<List<Documents>> documentsOpt = documentsRepository.findByApplicationId(applicationId);
        if (documentsOpt.isPresent()) {
            List<Documents> documents = documentsOpt.get();
            if (!documents.isEmpty()) {
                for (Documents document : documents) {
                    String fileName = document.getDocName();
                    String uploadType = document.getUploadType();
                    String fileDest = filePath + uploadType + "/" + fileName;
                    try {
                        Path fileToDelete = Paths.get(fileDest);
                        boolean deleted = Files.deleteIfExists(fileToDelete);
                        if (!deleted) {
                            logger.warn("File not found to delete: {}", fileDest);
                        } else {
                            logger.info("File deleted successfully: {}", fileDest);
                        }

                        documentsRepository.delete(document);
                        Optional<DBKITStageVerification> dbkitStageVerificationOpt = dbkitStageVerificationRepository.findById(applicationId);
                        if (dbkitStageVerificationOpt.isPresent()) {
                            DBKITStageVerification dbkitStageVerification = dbkitStageVerificationOpt.get();
                            dbkitStageVerification.setQueryDocs(null);
                            dbkitStageVerification.setApprovedDocs(null);
                            dbkitStageVerificationRepository.save(dbkitStageVerification);
                        }

                        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                        responseBody.setResponseObj("Document deleted successfully");
                    } catch (Exception e) {
                        logger.error("Failed to delete file: {}", fileDest, e);
                        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
                    }
                }
            }
        } else {
            logger.debug("document not found for applicationId: {}", applicationId);
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }


        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;

    }

    private Response handleDeleteManualDocuments(Properties prop, String appId, String applicationId,
                                                 String documentType) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        String filePath = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId + "/" + Constants.LOAN + "/"
                + applicationId + "/" + Constants.MANUAL + "/";
        Optional<Documents> documentRecordOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(applicationId,
                documentType, Constants.MANUAL);
        if (documentRecordOpt.isPresent()) {
            Documents documentRecord = documentRecordOpt.get();
            String fileName = documentRecord.getDocName();
            String fileDest = filePath + fileName;
            try {
                Path fileToDelete = Paths.get(fileDest);
                boolean deleted = Files.deleteIfExists(fileToDelete);
                if (!deleted) {
                    logger.warn("File not found to delete: {}", fileDest);
                } else {
                    logger.info("File deleted successfully: {}", fileDest);
                }

                documentsRepository.delete(documentRecord);

                responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                responseBody.setResponseObj("Document deleted successfully");
            } catch (IOException e) {
                logger.error("Failed to delete file: {}", fileDest, e);
                return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
            }
        } else {
            logger.debug("document not found for applicationId: {}", applicationId);
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }

        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    @Transactional
    private Response handleManualDocumentGeneration(UploadDocumentRequestFields requestObj, Properties prop,
                                                    String applicationId, String appId, String documentType, String userId, String productCode, String docSize) throws IOException {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();

        String filePath = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId + "/" + Constants.LOAN + "/"
                + applicationId + "/" + Constants.MANUAL + "/";
        File directory = new File(filePath);
        if (!directory.exists() && !directory.mkdirs()) {
            logger.error("Failed to create directory: {}", filePath);
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }

        String fileName = Constants.MANUAL + "_" + documentType + ".pdf";
        String filePathDest = filePath + fileName;
        Path destinationPath = Paths.get(filePathDest);

        if (!CommonUtils.isNullOrEmpty(requestObj.getBase64Value())) {
            byte[] docByte = Base64.getDecoder().decode(requestObj.getBase64Value());
            Files.write(destinationPath, docByte, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("Document successfully written to: {}", filePathDest);
            responseBody.setResponseObj("Document successfully written to: " + filePathDest);
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
            long deletedCount = documentsRepository.deleteByApplicationIdAndDocTypeAndUploadType(applicationId, documentType, Constants.GENERATED);

            if (deletedCount == 0) {
                logger.debug("No records found to delete for applicationId: {}, documentType: {}, uploadType: {}",
                        applicationId, documentType, Constants.GENERATED);
            } else {
                logger.debug("Deleted {} records for applicationId: {}, documentType: {}, uploadType: {}", deletedCount, applicationId, documentType, Constants.GENERATED);
            }
            if(documentType.equalsIgnoreCase(Constants.PAN_VALUE)){
                long deletedform97 = documentsRepository.deleteByApplicationIdAndDocTypeAndUploadType(applicationId, Constants.FORM_97, Constants.GENERATED);
            }

            Optional<Documents> documentRecordOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(applicationId,
                    documentType, Constants.MANUAL);
            if (documentRecordOpt.isPresent()) {
                Documents documentRecord = documentRecordOpt.get();
                documentRecord.setApplicationId(applicationId);
                documentRecord.setDocName(fileName);
                documentRecord.setAppId(appId);
                documentRecord.setDocType(documentType);
                documentRecord.setCreateTs(Timestamp.valueOf(LocalDateTime.now()));
                documentRecord.setDocStatus(Constants.PENDING);
                documentRecord.setQueryResponse(requestObj.getIsReupload());
                documentRecord.setUploadType(Constants.MANUAL);
                documentRecord.setCreatedBy(userId);
                documentRecord.setProductType(productCode);
                documentRecord.setDocSize(docSize);
                documentsRepository.save(documentRecord);
            } else {
                Documents documentRecord = new Documents();
                documentRecord.setApplicationId(applicationId);
                documentRecord.setDocName(fileName);
                documentRecord.setAppId(appId);
                documentRecord.setDocType(documentType);
                documentRecord.setDocStatus(Constants.PENDING);
                documentRecord.setUploadType(Constants.MANUAL);
                documentRecord.setQueryResponse(requestObj.getIsReupload());
                documentRecord.setCreatedBy(userId);
                documentRecord.setProductType(productCode);
                documentRecord.setDocSize(docSize);
                documentsRepository.save(documentRecord);
            }

        } else {
            logger.error("Base64 value is null or empty for applicationId: {}", applicationId);
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }

        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    private Response handleSingleDocument(UploadDocumentRequestFields requestObj, Properties prop,
                                          String applicationId, String appId, String documentType, String userId, String productCode, String docSize, Boolean mergeDocument) throws IOException {
        logger.error("OnEntry :: handleSingleDocument ");
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();

        String fileName = "";
        boolean isDelManualDocs = false;

        String filePath = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey())
                + "/" + appId + "/" + Constants.LOAN + "/" + applicationId + "/";

        if (mergeDocument && documentType.equalsIgnoreCase(Constants.DBKIT)) {
            filePath = filePath + Constants.GENERATED + "/";
            isDelManualDocs = true;
        } else if (mergeDocument && documentType.equalsIgnoreCase(Constants.WELCOMEKIT)) {
            filePath = filePath + Constants.WELCOMEKIT + "/";
        } else {
//            filePath = filePath; //individual docs
        }

        if (isDelManualDocs) {
            String folderPathToManual = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId + "/"
                    + Constants.LOAN + "/" + applicationId + "/" + Constants.MANUAL + "/";
            String folderPathToESIGN = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId + "/"
                    + Constants.LOAN + "/" + applicationId + "/" + Constants.ESIGN + "/";
            CommonUtils.deleteFilesInFolder(folderPathToManual);
            CommonUtils.deleteFilesInFolder(folderPathToESIGN);

            //Manually deleting other document from table since we are deleting it from the folder.
            List<String> docsToDelete = Arrays.asList(Constants.OTHER_DBDOCS, Constants.NATIONALIDPAN, Constants.AML,
                    Constants.KFS);
            List<Documents> docsToDeleteList = documentsRepository.findByApplicationIdAndDocTypeIn(applicationId,
                    docsToDelete);
            if (!docsToDeleteList.isEmpty()) {
                for (Documents docs : docsToDeleteList) {
                    documentsRepository.delete(docs);
                }
            }
        }

        fileName = applicationId + "_" + documentType + ".pdf";

        File directory = new File(filePath);
        if (!directory.exists() && !directory.mkdirs()) {
            logger.error("Failed to create directory: {}", filePath);
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }

        String filePathDest = filePath + fileName;
        Path destinationPath = Paths.get(filePathDest);

        if (!CommonUtils.isNullOrEmpty(requestObj.getBase64Value())) {
            byte[] docByte = Base64.getDecoder().decode(requestObj.getBase64Value());
            Files.write(destinationPath, docByte, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("Document successfully written to: {}", filePathDest);
            responseBody.setResponseObj("Document successfully written to: " + filePathDest);
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());

        } else {
            logger.error("Base64 value is null or empty for applicationId: {}", applicationId);
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }

        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }


    //  handleAutoDocumentGeneration html/pdf Merge
    private Response handleAutoDocumentGeneration(Properties prop, String appId, String applicationId, String language,
                                                  Gson gson, List<JsonObject> fileList, String userId, String productCode, Boolean mergeDocument)
            throws IOException, DRException, JRException {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        PDFMergerUtility pdfMerger = new PDFMergerUtility();

        ByteArrayOutputStream mergedOutputStream = new ByteArrayOutputStream();
        pdfMerger.setDestinationStream(mergedOutputStream);

        String filePathGen = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId + "/"
                + Constants.LOAN + "/" + applicationId + "/" + Constants.GENERATED + "/";
        File directoryGen = new File(filePathGen);
        if (!directoryGen.exists() && !directoryGen.mkdirs()) {
            logger.error("Failed to create directory: {}", filePathGen);
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }

        // language condition to
        String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
        logger.debug("inputLanguage" + language);
        boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr).anyMatch(lang -> lang.equalsIgnoreCase(language));
        if (!isValidLanguage) {
            String folderPathToManual = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId + "/"
                    + Constants.LOAN + "/" + applicationId + "/" + Constants.MANUAL + "/";
            String folderPathToESIGN = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId + "/"
                    + Constants.LOAN + "/" + applicationId + "/" + Constants.ESIGN + "/";
            CommonUtils.deleteFilesInFolder(folderPathToManual);
            CommonUtils.deleteFilesInFolder(folderPathToESIGN);

            //Manually deleting other document from table since we are deleting it from the folder.
            List<String> docsToDelete = Arrays.asList(Constants.OTHER_DBDOCS, Constants.NATIONALIDPAN, Constants.AML,
                    Constants.KFS);
            List<Documents> docsToDeleteList = documentsRepository.findByApplicationIdAndDocTypeIn(applicationId,
                    docsToDelete);
            if (!docsToDeleteList.isEmpty()) {
                for (Documents docs : docsToDeleteList) {
                    documentsRepository.delete(docs);
                }
            }
        }

        Optional<List<Documents>> documentRecordListOpt = documentsRepository.findByApplicationIdAndUploadType(applicationId, Constants.MANUAL);
        if (documentRecordListOpt.isPresent() && !documentRecordListOpt.get().isEmpty()) {
            List<Documents> documentsList = documentRecordListOpt.get();
            documentsRepository.deleteAll(documentsList);
        }

        int versionNum = Constants.INITIAL_VERSION_NO;

        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, versionNum);

        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            String product = applicationMasterData.getProductCode();
            logger.debug("product :" + product);
            CustomerDataFields custmrDataFields = getCustomerData(applicationMasterData, applicationId, appId,
                    versionNum);

            Optional<BCMPIStageVerification> bcmpiStageData = bcmpiStageVerificationRepository.findById(applicationId);
            if (bcmpiStageData.isPresent()) {
                logger.debug("bcmpiStageData found");
                custmrDataFields.setBcmpiStatDetails(CommonUtils.parseBCMPIStageVerificationData(
                        bcmpiStageData.get().getEditedFields(), bcmpiStageData.get().getQueries()));
                custmrDataFields.setBcmpiVerifiedStage(null != bcmpiStageData.get().getVerifiedStages()
                        ? Arrays.asList(bcmpiStageData.get().getVerifiedStages().split("\\|"))
                        : new ArrayList<>());
            }

            Optional<BCMPIIncomeDetails> bcmpiIncomeDataOpt = bcmpiIncomeDetailsRepo.findById(applicationId);
            if (bcmpiIncomeDataOpt.isPresent()) {
                logger.debug("bcmpiIncomeData found");
                BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper = gson
                        .fromJson(bcmpiIncomeDataOpt.get().getPayload(), BCMPIIncomeDetailsWrapper.class);
                BCMPIIncomeDetails bcmpiIncomeDetails = bcmpiIncomeDataOpt.get();
                bcmpiIncomeDetails.setBcmpiIncomeDetailsWrapper(bcmpiIncomeDetailsWrapper);
                custmrDataFields.setBcmpiIncomeDetails(bcmpiIncomeDetails);
            }

            Optional<BCMPILoanObligations> bcmpiLoanObligationsOpt = bcmpiLoanObligationsRepo.findById(applicationId);
            if (bcmpiLoanObligationsOpt.isPresent()) {
                logger.debug("bcmpiLoanObligations found");
                LoanObligationsWrapper loanObligationsWrapper = gson
                        .fromJson(bcmpiLoanObligationsOpt.get().getPayload(), LoanObligationsWrapper.class);
                BCMPILoanObligations bcmpiLoanObligations = bcmpiLoanObligationsOpt.get();
                bcmpiLoanObligations.setLoanObligationsWrapper(loanObligationsWrapper);
                custmrDataFields.setBcmpiLoanObligations(bcmpiLoanObligations);
            }

            Optional<BCMPIOtherDetails> bcmpiOtherDetailsOpt = bcmpiOtherDetailsRepo.findById(applicationId);
            if (bcmpiOtherDetailsOpt.isPresent()) {
                logger.debug("bcmpiOtherDetails found");
                BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = gson
                        .fromJson(bcmpiOtherDetailsOpt.get().getPayload(), BCMPIOtherDetailsWrapper.class);
                BCMPIOtherDetails bcmpiOtherDetails = bcmpiOtherDetailsOpt.get();
                bcmpiOtherDetails.setBcmpiOtherDetailsWrapper(bcmpiOtherDetailsWrapper);
                custmrDataFields.setBcmpiOtherDetails(bcmpiOtherDetails);
            }

            List<DeviationRATracker> deviationRecords = deviationRATrackerRepository
                    .findByApplicationId(custmrDataFields.getApplicationId());
            logger.debug("Size of deviationRecords: " + deviationRecords.isEmpty());

            Optional<SanctionMaster> sanctionAuthority = sanctionMasterRepositoy.findByProductAndValueBetween(product,
                    custmrDataFields.getLoanDetails().getLoanAmount());
            logger.debug("Loan Amount for sanction Approval: " + custmrDataFields.getLoanDetails().getLoanAmount());
            logger.debug("custmrDataFields.toString(): " + custmrDataFields.toString());
            logger.debug("Size of sanctionAuthority: " + sanctionAuthority.isPresent());
            boolean regenerate = false;
            boolean generateForm97 = false;
            Gson gsonObj = new Gson();
            if (custmrDataFields.getCustomerDetailsList() != null) {
                for (CustomerDetails customer : custmrDataFields.getCustomerDetailsList()) {

                    if ("Applicant".equalsIgnoreCase(customer.getCustomerType())) {
                        CustomerDetailsPayload applicantPayload = gsonObj.fromJson(customer.getPayloadColumn(), CustomerDetailsPayload.class);
                        String pan = applicantPayload != null ? applicantPayload.getPanNumber() : null;
                        logger.debug("Applicant PAN : {}", pan);
                        if (pan == null || pan.trim().isEmpty()) {
                            generateForm97 = true;
                            break;
                        }
                    } else if ("Co-App".equalsIgnoreCase(customer.getCustomerType())) {
                        CustomerDetailsPayload coApplicantPayload = gsonObj.fromJson(customer.getPayloadColumn(), CustomerDetailsPayload.class);
                        String pan = coApplicantPayload != null ? coApplicantPayload.getPanNumber() : null;
                        logger.debug("Co-Applicant PAN : {}", pan);
                        if (pan == null || pan.trim().isEmpty()) {
                            generateForm97 = true;
                            break;
                        }
                    }
                }
            }

            // @author Abhishek.Raj.CAG
            if (Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
                    .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())
                    || Constants.FAMILY_WELFARE_PRODUCT_CODE
                    .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())
            ) {
                logger.debug("Entered handleAutoDocumentGeneration if case");
                addGeneratedDocument(fileList, generateLoanApplication(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, regenerate));
                addGeneratedDocument(fileList, generateSanctionLetter(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, regenerate));
                addGeneratedDocument(fileList, generateLoanAgreement(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList, generateDemandPromissoryNote(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList, generateInsuranceConsent(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList, generateKfs(appId, applicationId, versionNum,
                        filePathGen, language, userId, productCode, regenerate));
                addGeneratedDocument(fileList, generateKfsSummary(appId, applicationId, versionNum,
                        filePathGen, language, userId, productCode, regenerate));
                if (generateForm97) {
                    logger.debug("Form97 auto generation required due to missing PAN.");
                    addGeneratedDocument(fileList,
                            generateForm97(appId, applicationId, versionNum, filePathGen,
                                    language, userId, productCode, custmrDataFields, regenerate));
                }
                logger.debug("Entered handleAutoDocumentGeneration if case --end");
                // KFS, KFS summary
            } else if (Constants.UNNATI_EMERGENCY_PRODUCT_CODE
                    .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())) {
                logger.debug("Entered handleAutoDocumentGeneration else if case");
                addGeneratedDocument(fileList, generateLoanApplication(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, regenerate));
                addGeneratedDocument(fileList, generateSanctionLetter(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, regenerate));
                addGeneratedDocument(fileList, generateLoanAgreement(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList, generateDemandPromissoryNote(appId, applicationId, versionNum, filePathGen,
                        language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList, generateKfs(appId, applicationId, versionNum,
                        filePathGen, language, userId, productCode, regenerate));
                addGeneratedDocument(fileList, generateKfsSummary(appId, applicationId, versionNum,
                        filePathGen, language, userId, productCode, regenerate));
                if (generateForm97) {
                    logger.debug("Form97 auto generation required due to missing PAN.");
                    addGeneratedDocument(fileList,
                            generateForm97(appId, applicationId, versionNum, filePathGen,
                                    language, userId, productCode, custmrDataFields, regenerate));
                }
                logger.debug("Entered handleAutoDocumentGeneration elseif case --end");
                // KFS, KFS summary
            }
            else if(Constants.UNNATI_RESTART_PRODUCT_CODE
                    .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode()) || Constants.Vishesh_LOAN_PRODUCT_CODE
                    .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())) {
                // existing flow
                logger.debug("Entered handleRegenerateDocument esle case");
                addGeneratedDocument(fileList,
                        generateLoanApplication(appId, applicationId, versionNum, filePathGen, language, userId, productCode, regenerate));
                addGeneratedDocument(fileList,
                        generateSanctionLetter(appId, applicationId, versionNum, filePathGen, language, userId, productCode, regenerate));
                addGeneratedDocument(fileList,
                        generateLoanAgreement(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList,
                        generateScheduleA(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
                if (generateForm97) {
                    logger.debug("Form97 generation required due to missing PAN.");
                    addGeneratedDocument(fileList,
                            generateForm97(appId, applicationId, versionNum, filePathGen,
                                    language, userId, productCode, custmrDataFields, regenerate));
                }
                addGeneratedDocument(fileList,
                        generateScheduleB(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList,
                        generateInsuranceConsent(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList, generateMSME(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList,
                        generateConsentLetter(appId, applicationId, versionNum, filePathGen, language, userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList,
                        generateDemandPromissoryNote(appId, applicationId, versionNum, filePathGen, language, userId,
                                productCode, custmrDataFields, regenerate));

                logger.debug("Entered handleRegenerateDocument esle case --ended");
            }
            else {
                // existing flow
                logger.debug("Entered handleAutoDocumentGeneration else case");
            addGeneratedDocument(fileList, generateLoanApplication(appId, applicationId, versionNum, filePathGen,
                    language, userId, productCode, regenerate));
            addGeneratedDocument(fileList, generateSanctionLetter(appId, applicationId, versionNum, filePathGen,
                    language, userId, productCode, regenerate));
            // addGeneratedDocument(fileList, generateKfs(appId, applicationId, versionNum,
            // filePathGen, language, userId, productCode, prop));
            addGeneratedDocument(fileList, generateLoanAgreement(appId, applicationId, versionNum, filePathGen,
                    language, userId, productCode, custmrDataFields, regenerate));
            addGeneratedDocument(fileList, generateScheduleA(appId, applicationId, versionNum, filePathGen, language,
                    userId, productCode, custmrDataFields, regenerate));
                addGeneratedDocument(fileList, generateScheduleB(appId, applicationId, versionNum, filePathGen, language,
                        userId, productCode, custmrDataFields, regenerate));
            if (generateForm97) {
                logger.debug("Form97 auto generation required due to missing PAN.");
                addGeneratedDocument(fileList,
                        generateForm97(appId, applicationId, versionNum, filePathGen,
                                language, userId, productCode, custmrDataFields, regenerate));
            }
            addGeneratedDocument(fileList, generateInsuranceConsent(appId, applicationId, versionNum, filePathGen,
                    language, userId, productCode, custmrDataFields, regenerate));
            addGeneratedDocument(fileList, generateMSME(appId, applicationId, versionNum, filePathGen, language, userId,
                    productCode, custmrDataFields, regenerate));
            addGeneratedDocument(fileList, generateConsentLetter(appId, applicationId, versionNum, filePathGen,
                    language, userId, productCode, custmrDataFields, regenerate));
            addGeneratedDocument(fileList, generateDemandPromissoryNote(appId, applicationId, versionNum, filePathGen,
                    language, userId, productCode, custmrDataFields, regenerate));
                logger.debug("Entered handleAutoDocumentGeneration else case --end");
            }
            //----END----

            responseBody.setResponseObj(gson.toJson(fileList));

            if (mergeDocument) {
                logger.debug("mergeDocument :" + mergeDocument);
                if (isValidLanguage) {
                    logger.debug("Constants.NEW_VERNCLR_LANGUAGES :" + isValidLanguage);

                    StringBuilder mergedHtml = new StringBuilder();
                    mergedHtml.append("<html><body>");

                    for (JsonObject file : fileList) {
                        //  Expect raw HTML only
                        String htmlContent = file.get(Constants.BASE64).getAsString();

                        // Clean duplicate <html>/<body> tags just to be safe
                        htmlContent = htmlContent.replaceAll("(?i)<\\/?html>|<\\/?body>", "");

                        mergedHtml.append(htmlContent);
                        mergedHtml.append("<hr style='page-break-after: always;'>");
                    }

                    mergedHtml.append("</body></html>");

                    JsonObject mergedHtmlJson = new JsonObject();
                    mergedHtmlJson.addProperty(Constants.MERGEDBASE64, mergedHtml.toString());
                    mergedHtmlJson.addProperty("fileType", "html");
                    responseBody.setResponseObj(gson.toJson(mergedHtmlJson));


                } else {
                    logger.debug("!Constants.NEW_VERNCLR_LANGUAGES");
                    for (JsonObject file : fileList) {
                        JsonElement base64Element = file.get(Constants.BASE64);
                        logger.debug("File object: {}", file);
                        logger.debug("Base64 element: {}", base64Element);
                        String base64 = base64Element.getAsString();
                        byte[] pdfBytes = Base64.getDecoder().decode(base64);
                        try (InputStream pdfStream = new ByteArrayInputStream(pdfBytes)) {
                            pdfMerger.addSource(pdfStream);
                        }
                    }
                    pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
                    String mergedBase64String = Base64.getEncoder().encodeToString(mergedOutputStream.toByteArray());
                    JsonObject mergedBase64 = new JsonObject();
                    mergedBase64.addProperty(Constants.MERGEDBASE64, mergedBase64String);

                    responseBody.setResponseObj(gson.toJson(mergedBase64));
                }

            }

            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
        } else {
            logger.error("Application not found for applicationId: {}", applicationId);
        }
        return response;
    }


    //Handle html/pdf - merged file - welcomeKit
    private Response handleAutoDocumentGenerationWelcomeKit(Properties prop, String appId, String applicationId,
                                                            String language1,
                                                            Gson gson, List<JsonObject> fileList, String userId, String productCode, Boolean mergeDocument)
            throws IOException, DRException, JRException {
        logger.error("Requsested language :" + language1);
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        ByteArrayOutputStream mergedOutputStream = new ByteArrayOutputStream();
        pdfMerger.setDestinationStream(mergedOutputStream);

        String filePathGen = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId + "/"
                + Constants.LOAN + "/" + applicationId + "/" + Constants.WELCOMEKIT + "/";
        File directoryGen = new File(filePathGen);
        if (!directoryGen.exists() && !directoryGen.mkdirs()) {
            logger.error("Failed to create directory: {}", filePathGen);
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }


        int versionNum = Constants.INITIAL_VERSION_NO;
//
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, versionNum);

        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            String product = applicationMasterData.getProductCode();
            logger.debug("product :" + product);
            CustomerDataFields custmrDataFields = getCustomerData(applicationMasterData, applicationId, appId,
                    versionNum);

            List<String> statusList = new ArrayList<>();
            statusList.add(WorkflowStatus.SANCTIONED.getValue());
            List<ApplicationWorkflow> wfList = applnWfRepository
                    .findByApplicationIdAndApplicationStatusInOrderByCreateTsDesc(custmrDataFields.getApplicationId(), statusList);

            String sactionedDateStr = "";
            if (!wfList.isEmpty()) {
                LocalDateTime sactionedDate = wfList.get(0).getCreateTs();
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                    sactionedDateStr = sactionedDate.format(formatter);
                } catch (Exception e) {
                    logger.error("Error while date formating.");
                }
            }

            Optional<ProductDetails> productDetails = productDetailsrepository.findById(product);
            String productName = "";
            if (productDetails.isPresent()) {
                logger.debug("productName data found.");
                logger.debug("Loan product Name:" + productDetails.get().getProductName());
                productName = productDetails.get().getProductName();
            }
            LoanDetailsPayload payload = gson.fromJson(custmrDataFields.getLoanDetails().getPayloadColumn(),
                    LoanDetailsPayload.class);

            String reportLanguages = prop.getProperty(CobFlagsProperties.REPORT_LANGUAGES.getKey());
            String[] reportLanguageArr = reportLanguages.split(",");
            String inputLanguage = payload.getLanguage();
            String sanctionedLoanAmtStr = String.valueOf(custmrDataFields.getLoanDetails().getSanctionedLoanAmount());
            logger.debug("inputLanguage" + inputLanguage);
            boolean isValidLanguage = Arrays.stream(reportLanguageArr)
                    .anyMatch(lang -> lang.equalsIgnoreCase(inputLanguage));
            String language = isValidLanguage ? inputLanguage : Constants.DEFAULTLANGUAGE;
            logger.debug("final Language welcomeKit :" + language);

            boolean isRestart = ProductCode.UNNATI_RESTART.getUnnatiCode().equalsIgnoreCase(productCode);
            boolean isAdditionalProduct = ProductCode.isAdditionalProduct(productCode);

            boolean shouldGenerateDocument = isRestart || !isAdditionalProduct;

            if (shouldGenerateDocument) {
            addGeneratedDocument(fileList, generateWelcomeLetter(appId, applicationId, versionNum, filePathGen,
                    language, userId, productCode, custmrDataFields, sactionedDateStr, productName, sanctionedLoanAmtStr));
            }
            addGeneratedDocument(fileList, generateRepaymentSchedule(appId, applicationId, versionNum, filePathGen,
                    language, userId, productCode, custmrDataFields, sactionedDateStr, productName));

            responseBody.setResponseObj(gson.toJson(fileList));
            //add language condition
            String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
            logger.debug("inputLanguage :" + language);
            boolean isNewLangLanguage = Arrays.stream(newVrnclrLanguageArr)
                    .anyMatch(lang -> lang.equalsIgnoreCase(language));

            if (mergeDocument) {
                logger.debug("mergeDocument :" + mergeDocument);
                if (isNewLangLanguage) {
                    logger.debug("Constants.NEW_VERNCLR_LANGUAGES");
                    StringBuilder mergedHtml = new StringBuilder();
                    mergedHtml.append("<html><body>");

                    for (JsonObject file : fileList) {
                        //  Expect raw HTML only
                        String htmlContent = file.get(Constants.BASE64).getAsString();

                        // Clean duplicate <html>/<body> tags just to be safe
                        htmlContent = htmlContent.replaceAll("(?i)<\\/?html>|<\\/?body>", "");

                        mergedHtml.append(htmlContent);
                        mergedHtml.append("<hr style='page-break-after: always;'>");
                    }

                    mergedHtml.append("</body></html>");

                    JsonObject mergedHtmlJson = new JsonObject();
                    mergedHtmlJson.addProperty(Constants.MERGEDBASE64, mergedHtml.toString());
                    mergedHtmlJson.addProperty("fileType", "html");
                    responseBody.setResponseObj(gson.toJson(mergedHtmlJson));


                } else {
                    logger.debug("!Constants.NEW_VERNCLR_LANGUAGES");
                    for (JsonObject file : fileList) {
                        String base64 = file.get(Constants.BASE64).getAsString();
                        byte[] pdfBytes = Base64.getDecoder().decode(base64);
                        try (InputStream pdfStream = new ByteArrayInputStream(pdfBytes)) {
                            pdfMerger.addSource(pdfStream);
                        }
                    }
                    pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
                    String mergedBase64String = Base64.getEncoder().encodeToString(mergedOutputStream.toByteArray());
                    JsonObject mergedBase64 = new JsonObject();
                    mergedBase64.addProperty(Constants.MERGEDBASE64, mergedBase64String);

                    responseBody.setResponseObj(gson.toJson(mergedBase64));
                }
            }

            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);


        } else {
            logger.error("Application not found for applicationId: {}", applicationId);
        }
        return response;
    }

    private JsonObject generateWelcomeLetter(String appId, String applicationId, int versionNum, String filePathGen,
                                             String language, String userId, String productCode, CustomerDataFields custmrDataFields,
                                             String sactionedDateStr, String productName, String disbursedLoanAmtStr)
            throws DRException, IOException, JRException {
        String jsonKeyFileName;
        JsonObject wlelcomeLetterJson = new JsonObject();
        String fileName = applicationId + "_" + Constants.WELCOMELETTER + ".pdf";
        String welcomeLetterPath = filePathGen + fileName;

        logger.warn("File not found at path: {}", welcomeLetterPath);
        jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "WelcomeLetter.json";
        JSONObject contentKeys = getFileContent(jsonKeyFileName, JsonKeyFolders.WELCOME_LETTER.getValue());

        List<RepaymentScheduleDisbursed> repaymentList = new ArrayList<>();
        String disburseDateStr = "";

        try {
            Optional<ApiExecutionLog> apiExecutionRecordOpt = apiExecutionLogRepository
                    .findTopByApplicationIdAndApiNameOrderByCreateTsDesc(applicationId, Constants.DISBURSEMENT_REPAY_SCHEDULE);

            if (apiExecutionRecordOpt.isPresent()) {
                ApiExecutionLog apiExecutionRecord = apiExecutionRecordOpt.get();
                if (apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS) || apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS_DMS)) {
                    String apiRespJsonString = apiExecutionRecord.getResponsePayload();
                    logger.debug("apiRespJsonString " + apiRespJsonString);

                    LocalDateTime createDate = apiExecutionRecord.getCreateTs();
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                        disburseDateStr = createDate.format(formatter);
                    } catch (Exception e) {
                        logger.error("error while conversion of Date :" + e);
                    }

                    Gson gson1 = new Gson();

                    // Parse the string into a JsonObject
                    JsonObject root = gson1.fromJson(apiRespJsonString, JsonObject.class);

                    // Extract the "body" array
                    JsonArray bodyArray = root.getAsJsonArray("body");

                    // Convert to list of RepaymentSchedule
                    Type listType = new TypeToken<List<RepaymentScheduleDisbursed>>() {
                    }.getType();
                    repaymentList = gson1.fromJson(bodyArray, listType);
                    logger.debug("repaymentList size." + repaymentList.size());
                    logger.debug("repaymentList size." + repaymentList.toString());
                } else {
                    logger.error("DisbursementRepaySchedule API- Successrecord not found.");
                    throw new IOException(Constants.DISBURSE_REPAY_SCHEDULE_ERR1);
                }
            } else {
                logger.error("repayment record not found.");
                throw new IOException(Constants.DISBURSE_REPAY_SCHEDULE_ERR2);
            }


        } catch (Exception e) {
            logger.error("Error while fetcthing ApiExecutionLog " + e);
            throw new IOException("Error fetching ApiExecutionLog - Welcomeletter data", e);
        }

        String wlelcomeLetterBase64 = new WelcomeLetter().generateWelcomeLetterPdfForDBKit(custmrDataFields, contentKeys,
                welcomeLetterPath, sactionedDateStr, repaymentList, disburseDateStr, disbursedLoanAmtStr, language);
//        wlelcomeLetterBase64 = Base64.getEncoder().encodeToString(wlelcomeLetterByte);
//    	}

        File document = new File(welcomeLetterPath);
        String docSize = String.valueOf(document.length() / 1024.0);

        wlelcomeLetterJson.addProperty(Constants.ORDER, 1);
        wlelcomeLetterJson.addProperty(Constants.BASE64, wlelcomeLetterBase64);
        wlelcomeLetterJson.addProperty("size", docSize);
        wlelcomeLetterJson.addProperty("docType", Constants.WELCOMELETTER);

        return wlelcomeLetterJson;
    }

    private String requestLog;

    public String getRequestLog() {
        return requestLog;
    }

    public void setRequestLog(String requestLog) {
        this.requestLog = requestLog;
    }

    private JsonObject generateRepaymentSchedule(String appId, String applicationId, int versionNum, String filePathGen,
                                                 String language, String userId, String productCode, CustomerDataFields custmrDataFields, String sactionedDateStr, String productName) throws DRException, IOException {

        String jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "RepaymentSchedule.json";
        JSONObject contentKeys = getFileContent(jsonKeyFileName, JsonKeyFolders.REPAYMENT_SCHEDULE.getValue());
        JsonObject repaymentScheduleJson = new JsonObject();
        String fileName = applicationId + "_" + Constants.REPAYMENTSCHEDULE + ".pdf";
        String repaymentSchedulePath = filePathGen + fileName;
        logger.warn("File path: {}", repaymentSchedulePath);
        List<RepaymentScheduleDisbursed> repaymentList = new ArrayList<>();
        String disburseDateStr = "";
        Properties prop = CommonUtils.readPropertyFile();
        Header header = CommonUtils.obtainHeader(applicationId, "", userId, "", "");
        try {
            Optional<ApiExecutionLog> apiExecutionRecordOpt = apiExecutionLogRepository
                    .findTopByApplicationIdAndApiNameOrderByCreateTsDesc(applicationId, Constants.DISBURSEMENT_REPAY_SCHEDULE);

            if (apiExecutionRecordOpt.isPresent()) {
                ApiExecutionLog apiExecutionRecord = apiExecutionRecordOpt.get();
                logger.debug("Loan Repyament at Disbursemnt Schedule started");
                try {

                    String loanId = "";
                    Optional<LoanDetails> loanOpt = loanDtlsRepo.findTopByApplicationIdAndAppId(applicationId, appId);

                    if (loanOpt.isPresent()) {
                        loanId = loanOpt.get().getT24LoanId();
                    }
                    logger.debug("Loan id from db:" + loanId);

                    Map<String, Object> loanRequest = new HashMap<>();
                    loanRequest.put("loanIdTemp", loanId);

                    Map<String, Object> mapReq = new HashMap<>();
                    mapReq.put("body", loanRequest);
                    logger.debug("Final request of loan Repyament disbursement  " + mapReq);

                    LoanRequestExt loanRequestExt = new LoanRequestExt();
                    loanRequestExt.setAppId(appId);
                    loanRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.DISBURSEMENT_REPAYMENTSCHEDULE_INTF.getKey()));
                    loanRequestExt.setRequestObj(mapReq);
                    logger.debug("Loan Repyament disbursement from the API: {} ", loanRequestExt.toString());

                    setRequestLog(loanRequestExt.toString());

                    Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, loanRequestExt,
                            loanRequestExt.getInterfaceName());

                    logger.debug("response 1 from the API1: {} ", apiRespMono);

                    apiRespMono.flatMap(val -> {
                        logger.debug("response 2 from the API: " + val);
                        String apiResp = new Gson().toJson(val);
                        logger.debug("JSON response 3 from the API: " + apiResp);
                        apiExecutionRecord.setResponsePayload(apiResp);
                        return Mono.just(apiResp);
                    });

                } catch (Exception e) {
                    logger.error("Error occurred while executing the Loan Repyament Disbursement api", e);
                }
                if (apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS) || apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS_DMS)) {
                    String apiRespJsonString = apiExecutionRecord.getResponsePayload();
                    logger.debug("apiRespJsonString " + apiRespJsonString);

                    LocalDateTime createDate = apiExecutionRecord.getCreateTs();
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                        disburseDateStr = createDate.format(formatter);
                    } catch (Exception e) {
                        logger.error("error while conversion of Date :" + e);
                    }

                    Gson gson1 = new Gson();

                    // Parse the string into a JsonObject
                    JsonObject root = gson1.fromJson(apiRespJsonString, JsonObject.class);

                    // Extract the "body" array
                    JsonArray bodyArray = root.getAsJsonArray("body");

                    // Convert to list of RepaymentSchedule
                    Type listType = new TypeToken<List<RepaymentScheduleDisbursed>>() {
                    }.getType();
                    repaymentList = gson1.fromJson(bodyArray, listType);
                    logger.debug("repaymentList size." + repaymentList.size());
                    logger.debug("repaymentList size." + repaymentList.toString());
                } else {
                    logger.error("DisbursementRepaySchedule API - Success Record not found.");
                    throw new IOException(Constants.DISBURSE_REPAY_SCHEDULE_ERR1);
                }
            } else {
                logger.error("repayment record not found.");
                throw new IOException(Constants.DISBURSE_REPAY_SCHEDULE_ERR2);
            }
        } catch (Exception e) {
            logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR2 + e);
            throw new IOException("Error fetching ApiExecutionLog - Repayment schedule data", e);
        }

        String repaymentScheduleBase64 = new RepaymentScheduleTemplate().generateRepaymentScuduleForWelcomeKit(contentKeys, custmrDataFields, repaymentSchedulePath,
                productName, repaymentList, disburseDateStr, language);

        repaymentScheduleJson.addProperty(Constants.ORDER, 2);
        repaymentScheduleJson.addProperty(Constants.BASE64, repaymentScheduleBase64);
        repaymentScheduleJson.addProperty("docType", Constants.REPAYMENTSCHEDULE);

        File document = new File(repaymentSchedulePath);
        String docSize = String.valueOf(document.length() / 1024.0);
        repaymentScheduleJson.addProperty("size", docSize);

        return repaymentScheduleJson;
    }

    private void addGeneratedDocument(List<JsonObject> fileList, JsonObject documentJson) {
        if (documentJson.size() != 0) {
            fileList.add(documentJson);
        }
    }

    private JsonObject generateDemandPromissoryNote(String appId, String applicationId, int versionNum,
                                                    String filePathGen,
                                                    String language, String userId, String productCode, CustomerDataFields custmrDataFields,
                                                    boolean regenerate) throws DRException, IOException {
        String jsonKeyFileName;
        JsonObject demandPromissoryNoteJson = new JsonObject();
        String fileName = applicationId + "_" + Constants.DEMAND_PROMISSORY_NOTE + ".pdf";
        String demandPromissoryNotePath = filePathGen + fileName;
        jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "DemandPromissoryNote.json";
        JSONObject contentKeys = getFileContent(jsonKeyFileName, JsonKeyFolders.DEMAND_PROMISSORY_NOTE.getValue());

        String demandPromissoryNoteBase64 ;

        // @author Abhishek.Raj.CAG
        String leadInitiationDateStr = "-";
        UnnatiIexceedCDHLead cdhLeadDetails = custmrDataFields.getCdhLeadDetails();

        if (cdhLeadDetails != null && cdhLeadDetails.getLeadInitiationDate() != null) {
            try {
                leadInitiationDateStr = CommonUtils.formatDateTimeToDateStr(cdhLeadDetails.getLeadInitiationDate());
                logger.debug("Formatted leadInitiationDateStr: {}", leadInitiationDateStr);
            } catch (Exception e) {
                logger.error("Error while formatting lead initiation date: ", e);
            }
        } else {
            logger.debug("cdhLeadDetails or leadInitiationDate is null for applicationId: {}", applicationId);
        }


        if (
                Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(productCode)
                        || Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(productCode)
        ) {
            demandPromissoryNoteBase64 = new DemandPromisoryNoteAdditional().generatePdfForDbkit(contentKeys,
                    demandPromissoryNotePath, custmrDataFields, language, leadInitiationDateStr);
        } else {
            demandPromissoryNoteBase64 = new DemandPromisoryNote().generatePdfForDbkit(contentKeys,
                    demandPromissoryNotePath, custmrDataFields, language, leadInitiationDateStr);
        }
        //----END----

        File document = new File(demandPromissoryNotePath);
        String docSize = String.valueOf(document.length() / 1024.0);

        demandPromissoryNoteJson.addProperty(Constants.ORDER, 9);
        demandPromissoryNoteJson.addProperty(Constants.BASE64, demandPromissoryNoteBase64);
        demandPromissoryNoteJson.addProperty("size", docSize);
        demandPromissoryNoteJson.addProperty("docType", Constants.DEMAND_PROMISSORY_NOTE);

        // language condition
        String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
        logger.debug("inputLanguage" + language);
        boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                .anyMatch(lang -> lang.equalsIgnoreCase(language));
        if (!isValidLanguage) {
            if (!regenerate) {
                long deletedCount = documentsRepository.deleteByApplicationIdAndDocTypeAndUploadType(applicationId, Constants.DEMAND_PROMISSORY_NOTE, Constants.MANUAL);

                if (deletedCount == 0) {
                    logger.debug("No records found to delete for applicationId: {}, documentType: {}, uploadType: {}",
                            applicationId, Constants.DEMAND_PROMISSORY_NOTE, Constants.MANUAL);
                } else {
                    logger.debug("Deleted {} records for applicationId: {}, documentType: {}, uploadType: {}", deletedCount, applicationId, Constants.DEMAND_PROMISSORY_NOTE, Constants.MANUAL);
                }
                Optional<Documents> documentRecordOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(applicationId,
                        Constants.DEMAND_PROMISSORY_NOTE, Constants.GENERATED);
                if (documentRecordOpt.isPresent()) {
                    Documents documentRecord = documentRecordOpt.get();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.DEMAND_PROMISSORY_NOTE);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setCreateTs(Timestamp.valueOf(LocalDateTime.now()));
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                } else {
                    Documents documentRecord = new Documents();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.DEMAND_PROMISSORY_NOTE);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                }
            }
        }
        return demandPromissoryNoteJson;
    }

    private JsonObject generateConsentLetter(String appId, String applicationId, int versionNum,
                                             String filePathGen,
                                             String language, String userId, String productCode, CustomerDataFields custmrDataFields, boolean regenerate) throws DRException, IOException {
        String jsonKeyFileName;
        JsonObject consentLetterJson = new JsonObject();
        String fileName = applicationId + "_" + Constants.CONSENT_LETTER_NEW + ".pdf";
        String consentLetterPath = filePathGen + fileName;
        jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "ConsentLetter.json";
        JSONObject contentKeys = getFileContent(jsonKeyFileName, JsonKeyFolders.CONSENT_LETTER.getValue());

        List<Enach> enachDetails = null;
        Optional<List<Enach>> enachDetailsDb = enachRepo.findByApplicationIdAndAppId(custmrDataFields.getApplicationId(), custmrDataFields.getAppId());
        if (enachDetailsDb.isPresent() && !enachDetailsDb.get().isEmpty()) {
            logger.debug("enachDetailsDb record found ");
            enachDetails = enachDetailsDb.get();
        } else {
            logger.debug("enachDetailsDb records notfound :");
        }

        String consentLetterBase64 = new ConsentLetter().generatePdfForDbkit(contentKeys, consentLetterPath, custmrDataFields, enachDetails, language, productCode);

        consentLetterJson.addProperty(Constants.ORDER, 8);
        consentLetterJson.addProperty(Constants.BASE64, consentLetterBase64);
        consentLetterJson.addProperty("docType", Constants.CONSENT_LETTER_NEW);

        File document = new File(consentLetterPath);
        String docSize = String.valueOf(document.length() / 1024.0);
        consentLetterJson.addProperty("size", docSize);


        // language condition
        String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
        logger.debug("inputLanguage" + language);
        boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                .anyMatch(lang -> lang.equalsIgnoreCase(language));
        if (!isValidLanguage) {
            if (!regenerate) {
                long deletedCount = documentsRepository.deleteByApplicationIdAndDocTypeAndUploadType(applicationId, Constants.CONSENT_LETTER_NEW, Constants.MANUAL);

                if (deletedCount == 0) {
                    logger.debug("No records found to delete for applicationId: {}, documentType: {}, uploadType: {}",
                            applicationId, Constants.CONSENT_LETTER_NEW, Constants.MANUAL);
                } else {
                    logger.debug("Deleted {} records for applicationId: {}, documentType: {}, uploadType: {}", deletedCount, applicationId, Constants.CONSENT_LETTER_NEW, Constants.MANUAL);
                }
                Optional<Documents> documentRecordOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(applicationId,
                        Constants.CONSENT_LETTER_NEW, Constants.GENERATED);
                if (documentRecordOpt.isPresent()) {
                    Documents documentRecord = documentRecordOpt.get();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.CONSENT_LETTER_NEW);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setCreateTs(Timestamp.valueOf(LocalDateTime.now()));
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                } else {
                    Documents documentRecord = new Documents();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.CONSENT_LETTER_NEW);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                }
            }
        }
        return consentLetterJson;
    }

    private JsonObject generateMSME(String appId, String applicationId, int versionNum,
                                    String filePathGen,
                                    String language, String userId, String productCode, CustomerDataFields custmrDataFields, boolean regenerate) throws DRException, IOException {
        String jsonKeyFileName;
        JsonObject MSMEJson = new JsonObject();
        String fileName = applicationId + "_" + Constants.MSME + ".pdf";
        String MSMEPath = filePathGen + fileName;
        jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "MSME.json";
        JSONObject contentKeys = getFileContent(jsonKeyFileName, JsonKeyFolders.MSME.getValue());

        List<String> statusList = new ArrayList<>();
        statusList.add(WorkflowStatus.SANCTIONED.getValue());
        List<ApplicationWorkflow> wfList = applnWfRepository
                .findByApplicationIdAndApplicationStatusInOrderByCreateTsDesc(custmrDataFields.getApplicationId(), statusList);

        String sactionedDateStr = "";
        if (!wfList.isEmpty()) {
            LocalDateTime sactionedDate = wfList.get(0).getCreateTs();
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                sactionedDateStr = sactionedDate.format(formatter);
            } catch (Exception e) {
            }
        }

        String MSMEBase64 = new MSMEReport().generatePdfForDbkit(contentKeys, MSMEPath, custmrDataFields, sactionedDateStr, language);

        MSMEJson.addProperty(Constants.ORDER, 7);
        MSMEJson.addProperty(Constants.BASE64, MSMEBase64);
        MSMEJson.addProperty("docType", Constants.MSME);

        File document = new File(MSMEPath);
        String docSize = String.valueOf(document.length() / 1024.0);
        MSMEJson.addProperty("size", docSize);


        // language condition
        String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
        logger.debug("inputLanguage" + language);
        boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                .anyMatch(lang -> lang.equalsIgnoreCase(language));
        if (!isValidLanguage) {
            if (!regenerate) {
                long deletedCount = documentsRepository.deleteByApplicationIdAndDocTypeAndUploadType(applicationId, Constants.MSME, Constants.MANUAL);

                if (deletedCount == 0) {
                    logger.debug("No records found to delete for applicationId: {}, documentType: {}, uploadType: {}",
                            applicationId, Constants.MSME, Constants.MANUAL);
                } else {
                    logger.debug("Deleted {} records for applicationId: {}, documentType: {}, uploadType: {}", deletedCount, applicationId, Constants.MSME, Constants.MANUAL);
                }
                Optional<Documents> documentRecordOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(applicationId,
                        Constants.MSME, Constants.GENERATED);
                if (documentRecordOpt.isPresent()) {
                    Documents documentRecord = documentRecordOpt.get();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.MSME);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setCreateTs(Timestamp.valueOf(LocalDateTime.now()));
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                } else {
                    Documents documentRecord = new Documents();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.MSME);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                }
            }
        }
        return MSMEJson;
    }

    private JsonObject generateInsuranceConsent(String appId, String applicationId, int versionNum,
                                                String filePathGen,
                                                String language, String userId, String productCode, CustomerDataFields custmrDataFields, boolean regenerate) throws DRException, IOException, JRException {
        String jsonKeyFileName;
        JsonObject insuranceConsentJson = new JsonObject();
        String fileName = applicationId + "_" + Constants.INSURANCE_CONSENT + ".pdf";
        String insuranceConsentPath = filePathGen + fileName;
        jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "InsuranceConsent.json";
        JSONObject contentKeys = getFileContent(jsonKeyFileName, JsonKeyFolders.INSURANCE_CONSENT.getValue());

        String bmId = "";
        List<ApplicationWorkflow> workflow;
        workflow = applnWfRepository.findByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumAsc(appId,
                applicationId, versionNum);
        logger.debug("Workflow details {} ", workflow);

        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, versionNum);

//        if (applicationMasterOpt.isPresent()) {
//            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
//        }

        ApplicationMaster applicationMasterData = null;
        if (applicationMasterOpt.isPresent()) {
            applicationMasterData = applicationMasterOpt.get();
        }

        String bmName = "";

        if (!workflow.isEmpty()) {
            custmrDataFields.setApplicationWorkflowList(workflow);

            if (Constants.UNNATI_PRODUCT_CODE
                    .equals(custmrDataFields.getApplicationMaster().getProductCode())
                    || Constants.OPENMARKET_LOAN_PRODUCT_CODE
                    .equals(custmrDataFields.getApplicationMaster().getProductCode())) {
                logger.info("Unnati application");

                String previousWorkflowStatus = null;
                for (ApplicationWorkflow appnWorkflow : custmrDataFields.getApplicationWorkflowList()) {
                    String currentStatus = appnWorkflow.getApplicationStatus();
                    if (Constants.APPROVED.equalsIgnoreCase(appnWorkflow.getApplicationStatus())) {
                        if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(previousWorkflowStatus)) {
                            bmId = appnWorkflow.getCreatedBy();
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
                            }
                        }
                        break;
                    }
                }
            } else {
                logger.info("Not an Unnati/Renewal application");
            }
        }

        Optional<String> usernameOptBM = tbUserRepository.findUserNameByUserId(bmId);
        String userNameBM = usernameOptBM.orElse("-");

        String insuranceConsentBase64 = new InsuranceConsent().generatePdfForDbKit(contentKeys, insuranceConsentPath, custmrDataFields, language, userNameBM, applicationMasterData);
        insuranceConsentJson.addProperty("order", 6);
        insuranceConsentJson.addProperty("base64", insuranceConsentBase64);
        insuranceConsentJson.addProperty("docType", Constants.INSURANCE_CONSENT);

        File document = new File(insuranceConsentPath);
        String docSize = String.valueOf(document.length() / 1024.0);
        insuranceConsentJson.addProperty("size", docSize);

        // language condition
        String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
        logger.debug("inputLanguage" + language);
        boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                .anyMatch(lang -> lang.equalsIgnoreCase(language));
        if (!isValidLanguage) {
            if (!regenerate) {
                long deletedCount = documentsRepository.deleteByApplicationIdAndDocTypeAndUploadType(applicationId, Constants.INSURANCE_CONSENT, Constants.MANUAL);

                if (deletedCount == 0) {
                    logger.debug("No records found to delete for applicationId: {}, documentType: {}, uploadType: {}",
                            applicationId, Constants.INSURANCE_CONSENT, Constants.MANUAL);
                } else {
                    logger.debug("Deleted {} records for applicationId: {}, documentType: {}, uploadType: {}", deletedCount, applicationId, Constants.INSURANCE_CONSENT, Constants.MANUAL);
                }
                Optional<Documents> documentRecordOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(applicationId,
                        Constants.INSURANCE_CONSENT, Constants.GENERATED);
                if (documentRecordOpt.isPresent()) {
                    Documents documentRecord = documentRecordOpt.get();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.INSURANCE_CONSENT);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setCreateTs(Timestamp.valueOf(LocalDateTime.now()));
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                } else {
                    Documents documentRecord = new Documents();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.INSURANCE_CONSENT);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                }
            }
        }
        return insuranceConsentJson;
    }

    private JsonObject generateScheduleA(String appId, String applicationId, int versionNum, String filePathGen,
                                         String language, String userId, String productCode, CustomerDataFields custmrDataFields, boolean regenerate) throws DRException, IOException {

        String jsonKeyFileName;
        JsonObject scheduleAJson = new JsonObject();
        String fileName = applicationId + "_" + Constants.SCHEDULE_A_KFS + ".pdf";
        String scheduleAPath = filePathGen + fileName;
        jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "ScheduleA.json";
        JSONObject contentKeys = getFileContent(jsonKeyFileName, JsonKeyFolders.SCHEDULE_A.getValue());

        String product = "";
        ApplicationMaster applicationMasterData = custmrDataFields.getApplicationMaster();
        product = applicationMasterData.getProductCode();
//		}

        Optional<ProductDetails> productDetails = productDetailsrepository.findById(product);
        String productName = "";
        if (productDetails.isPresent()) {
            logger.debug("productName data found.");
            logger.debug("Loan product Name:" + productDetails.get().getProductName());
            productName = productDetails.get().getProductName();
        }

        String applicantId = applicationMasterData.getSearchCode2();
        List<ExistingGLLoanDetails> existingGLLoanDetailsList = existingGLLoanDetailsRepository.findByCustomerId(applicantId);
        if (existingGLLoanDetailsList != null && !existingGLLoanDetailsList.isEmpty()) {
            custmrDataFields.setExistingGLLoanDetails(existingGLLoanDetailsList);
        } else {
            logger.debug("No Existing GL loan details found for applicantId: {}", applicantId);
            custmrDataFields.setExistingGLLoanDetails(Collections.emptyList());
        }
        //
        List<RepaymentSchedule> repaymentList = new ArrayList<>();
        try {
            Optional<ApiExecutionLog> apiExecutionRecordOpt = apiExecutionLogRepository.findTopByApplicationIdAndApiNameOrderByCreateTsDesc(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE);
            if (apiExecutionRecordOpt.isPresent()) {
                ApiExecutionLog apiExecutionRecord = apiExecutionRecordOpt.get();
                if (apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS) || apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS_DMS)) {
                    String apiRespJsonString = apiExecutionRecord.getResponsePayload();
                    logger.debug("apiRespJsonString " + apiRespJsonString);
                    Gson gson1 = new Gson();

                    // Parse the string into a JsonObject
                    JsonObject root = gson1.fromJson(apiRespJsonString, JsonObject.class);

                    // Extract the "body" array
                    JsonArray bodyArray = root.getAsJsonArray("body");

                    // Convert to list of RepaymentSchedule
                    Type listType = new TypeToken<List<RepaymentSchedule>>() {
                    }.getType();
                    repaymentList = gson1.fromJson(bodyArray, listType);
                    logger.debug("repaymentList size." + repaymentList.size());
                    logger.debug("repaymentList size." + repaymentList.toString());
                } else {
                    logger.error(Constants.DISBURSE_REPAY_SCHEDULE_ERR1);
                    throw new IOException(Constants.DISBURSE_REPAY_SCHEDULE_ERR1);
                }
            } else {
                logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                throw new IOException(Constants.DISBURSE_REPAY_SCHEDULE_ERR2);
            }
        } catch (Exception e) {
            logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR2 + e);
            throw new IOException("Error fetching ApiExecutionLog -generateScheduleA data", e);
        }

        String scheduleABase64 = new ScheduleATemplate().generatePdfForDbKit(contentKeys, scheduleAPath, custmrDataFields, productName, repaymentList, language);

        scheduleAJson.addProperty(Constants.ORDER, 5);
        scheduleAJson.addProperty(Constants.BASE64, scheduleABase64);
        scheduleAJson.addProperty("docType", Constants.SCHEDULE_A_KFS);

        File document = new File(scheduleAPath);
        String docSize = String.valueOf(document.length() / 1024.0);
        scheduleAJson.addProperty("size", docSize);

        // language condition
        String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
        logger.debug("inputLanguage" + language);
        boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                .anyMatch(lang -> lang.equalsIgnoreCase(language));
        if (!isValidLanguage) {
            if (!regenerate) {
                long deletedCount = documentsRepository.deleteByApplicationIdAndDocTypeAndUploadType(applicationId, Constants.SCHEDULE_A_KFS, Constants.MANUAL);

                if (deletedCount == 0) {
                    logger.debug("No records found to delete for applicationId: {}, documentType: {}, uploadType: {}",
                            applicationId, Constants.SCHEDULE_A_KFS, Constants.MANUAL);
                } else {
                    logger.debug("Deleted {} records for applicationId: {}, documentType: {}, uploadType: {}", deletedCount, applicationId, Constants.SCHEDULE_A_KFS, Constants.MANUAL);
                }
                Optional<Documents> documentRecordOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(applicationId,
                        Constants.SCHEDULE_A_KFS, Constants.GENERATED);
                if (documentRecordOpt.isPresent()) {
                    Documents documentRecord = documentRecordOpt.get();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.SCHEDULE_A_KFS);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setCreateTs(Timestamp.valueOf(LocalDateTime.now()));
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                } else {
                    Documents documentRecord = new Documents();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.SCHEDULE_A_KFS);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                }
            }
        }
        return scheduleAJson;
    }

    private JsonObject generateScheduleB(String appId, String applicationId, int versionNum, String filePathGen,
                                         String language, String userId, String productCode, CustomerDataFields custmrDataFields, boolean regenerate) throws DRException, IOException {
        logger.debug("scheduleB generateScheduleB method called with applicationId: {}, language: {}, regenerate: {}", applicationId, language, regenerate);
        String jsonKeyFileName;
        JsonObject scheduleBJson = new JsonObject();
        String fileName = applicationId + "_" + Constants.SCHEDULE_B + ".pdf";
        String scheduleBPath = filePathGen + fileName;
        jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "ScheduleB.json";
        JSONObject contentKeys = getFileContent(jsonKeyFileName, JsonKeyFolders.SCHEDULE_B.getValue());

        String product = "";
        ApplicationMaster applicationMasterData = custmrDataFields.getApplicationMaster();
        product = applicationMasterData.getProductCode();
//		}
        logger.debug("scheduleB cobservice "+jsonKeyFileName + " contentKeys: " + contentKeys.toString());
        Optional<ProductDetails> productDetails = productDetailsrepository.findById(product);
        String productName = "";
        if (productDetails.isPresent()) {
            logger.debug("productName data found.");
            logger.debug("Loan product Name:" + productDetails.get().getProductName());
            productName = productDetails.get().getProductName();
        }

        String applicantId = applicationMasterData.getSearchCode2();
        logger.debug("applicantId for scheduleB: " + applicantId);
        List<ExistingGLLoanDetails> existingGLLoanDetailsList = existingGLLoanDetailsRepository.findByCustomerId(applicantId);
        if (existingGLLoanDetailsList != null && !existingGLLoanDetailsList.isEmpty()) {
            custmrDataFields.setExistingGLLoanDetails(existingGLLoanDetailsList);
        } else {
            logger.debug("No Existing GL loan details found for applicantId: {}", applicantId);
            custmrDataFields.setExistingGLLoanDetails(Collections.emptyList());
        }
        //


        logger.debug("scheduleB size: " + contentKeys);
        String scheduleBBase64 = new ScheduleBTemplate().generatePdfForDbKit(contentKeys,custmrDataFields, productName, language, scheduleBPath);

        scheduleBJson.addProperty(Constants.ORDER, 5);
        scheduleBJson.addProperty(Constants.BASE64, scheduleBBase64);
        scheduleBJson.addProperty("docType", Constants.SCHEDULE_B);

        File document = new File(scheduleBPath);
        String docSize = String.valueOf(document.length() / 1024.0);
        scheduleBJson.addProperty("size", docSize);

        // language condition
        String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
        logger.debug("inputLanguage" + language);
        boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                .anyMatch(lang -> lang.equalsIgnoreCase(language));
        if (!isValidLanguage) {
            if (!regenerate) {
                long deletedCount = documentsRepository.deleteByApplicationIdAndDocTypeAndUploadType(applicationId, Constants.SCHEDULE_B, Constants.MANUAL);

                if (deletedCount == 0) {
                    logger.debug("No records found to delete for applicationId: {}, documentType: {}, uploadType: {}",
                            applicationId, Constants.SCHEDULE_B, Constants.MANUAL);
                } else {
                    logger.debug("Deleted {} records for applicationId: {}, documentType: {}, uploadType: {}", deletedCount, applicationId, Constants.SCHEDULE_B, Constants.MANUAL);
                }
                Optional<Documents> documentRecordOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(applicationId,
                        Constants.SCHEDULE_B, Constants.GENERATED);
                if (documentRecordOpt.isPresent()) {
                    Documents documentRecord = documentRecordOpt.get();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.SCHEDULE_B);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setCreateTs(Timestamp.valueOf(LocalDateTime.now()));
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                } else {
                    Documents documentRecord = new Documents();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.SCHEDULE_B);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                }
            }
        }
        logger.debug("scheduleBJson: {}", scheduleBJson);
        return scheduleBJson;
    }

    private JsonObject generateForm97(String appId, String applicationId, int versionNum, String filePathGen,
                                      String language, String userId, String productCode, CustomerDataFields custmrDataFields, boolean regenerate) throws DRException, IOException {
        logger.debug("form97 method called with applicationId: {}, language: {}, regenerate: {}", applicationId, language, regenerate);
        String jsonKeyFileName;
        JsonObject form97Json = new JsonObject();
        String fileName = applicationId + "_" + Constants.FORM_97 + ".pdf";
        String form97Path = filePathGen + fileName;
        jsonKeyFileName = Constants.REPORT_JSONKEYS + "English" + "Form97.json";
        JSONObject contentKeys = getFileContent(jsonKeyFileName, JsonKeyFolders.FORM_97.getValue());

        String product = "";
        ApplicationMaster applicationMasterData = custmrDataFields.getApplicationMaster();
        product = applicationMasterData.getProductCode();
//		}
        logger.debug("form97 cobservice "+jsonKeyFileName + " contentKeys: " + contentKeys.toString());
        Optional<ProductDetails> productDetails = productDetailsrepository.findById(product);
        String productName = "";
        if (productDetails.isPresent()) {
            logger.debug("productName data found.");
            logger.debug("Loan product Name:" + productDetails.get().getProductName());
            productName = productDetails.get().getProductName();
        }

        // sactionedDateStr
        List<String> statusList = new ArrayList<>();
        statusList.add(WorkflowStatus.SANCTIONED.getValue());
        List<ApplicationWorkflow> wfList = applnWfRepository
                .findByApplicationIdAndApplicationStatusInOrderByCreateTsDesc(applicationId,
                        statusList);

        String sactionedDateStr = "";
        if (!wfList.isEmpty()) {
            LocalDateTime sactionedDate = wfList.get(0).getCreateTs();
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                sactionedDateStr = sactionedDate.format(formatter);
            } catch (Exception e) {
                logger.error("error while conversion of Date :" + e);
            }
        }

        String applicantId = applicationMasterData.getSearchCode2();
        logger.debug("applicantId for form97: " + applicantId);
        List<ExistingGLLoanDetails> existingGLLoanDetailsList = existingGLLoanDetailsRepository.findByCustomerId(applicantId);
        if (existingGLLoanDetailsList != null && !existingGLLoanDetailsList.isEmpty()) {
            custmrDataFields.setExistingGLLoanDetails(existingGLLoanDetailsList);
        } else {
            logger.debug("No Existing GL loan details found for applicantId: {}", applicantId);
            custmrDataFields.setExistingGLLoanDetails(Collections.emptyList());
        }
        //
        List<RepaymentSchedule> repaymentList = new ArrayList<>();
        try {
            Optional<ApiExecutionLog> apiExecutionRecordOpt = apiExecutionLogRepository.findTopByApplicationIdAndApiNameOrderByCreateTsDesc(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE);
            if (apiExecutionRecordOpt.isPresent()) {
                ApiExecutionLog apiExecutionRecord = apiExecutionRecordOpt.get();
                if (apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS) || apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS_DMS)) {
                    String apiRespJsonString = apiExecutionRecord.getResponsePayload();
                    logger.debug("apiRespJsonString " + apiRespJsonString);
                    Gson gson1 = new Gson();

                    // Parse the string into a JsonObject
                    JsonObject root = gson1.fromJson(apiRespJsonString, JsonObject.class);

                    // Extract the "body" array
                    JsonArray bodyArray = root.getAsJsonArray("body");

                    // Convert to list of RepaymentSchedule
                    Type listType = new TypeToken<List<RepaymentSchedule>>() {
                    }.getType();
                    repaymentList = gson1.fromJson(bodyArray, listType);
                    logger.debug("repaymentList size." + repaymentList.size());
                    logger.debug("repaymentList size." + repaymentList.toString());
                } else {
                    logger.error(Constants.DISBURSE_REPAY_SCHEDULE_ERR1);
                    throw new IOException(Constants.DISBURSE_REPAY_SCHEDULE_ERR1);
                }
            } else {
                logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                throw new IOException(Constants.DISBURSE_REPAY_SCHEDULE_ERR2);
            }
        } catch (Exception e) {
            logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR2 + e);
            throw new IOException("Error fetching ApiExecutionLog -generateScheduleB data", e);
        }

        logger.debug("form97 size: " + contentKeys);
        String form97Base64 = new Form97().generatePdfForDbkit(contentKeys, custmrDataFields, language, form97Path, sactionedDateStr);

        form97Json.addProperty(Constants.ORDER, 5);
        form97Json.addProperty(Constants.BASE64, form97Base64);
        form97Json.addProperty("docType", Constants.FORM_97);

        File document = new File(form97Path);
        String docSize = String.valueOf(document.length() / 1024.0);
        form97Json.addProperty("size", docSize);

        // language condition
        String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
        logger.debug("inputLanguage" + language);
        boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                .anyMatch(lang -> lang.equalsIgnoreCase(language));
        if (!isValidLanguage) {
            if (!regenerate) {
                long deletedCount = documentsRepository.deleteByApplicationIdAndDocTypeAndUploadType(applicationId, Constants.FORM_97, Constants.MANUAL);

                if (deletedCount == 0) {
                    logger.debug("No records found to delete for applicationId: {}, documentType: {}, uploadType: {}",
                            applicationId, Constants.FORM_97, Constants.MANUAL);
                } else {
                    logger.debug("Deleted {} records for applicationId: {}, documentType: {}, uploadType: {}", deletedCount, applicationId, Constants.FORM_97, Constants.MANUAL);
                }
                Optional<Documents> documentRecordOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(applicationId,
                        Constants.FORM_97, Constants.GENERATED);
                if (documentRecordOpt.isPresent()) {
                    Documents documentRecord = documentRecordOpt.get();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.FORM_97);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setCreateTs(Timestamp.valueOf(LocalDateTime.now()));
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                } else {
                    Documents documentRecord = new Documents();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.FORM_97);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                }
            }
        }
        logger.debug("form97Json: {}", form97Json);
        return form97Json;
    }

    private JsonObject generateLoanAgreement(String appId, String applicationId, int versionNum, String filePathGen,
                                             String language, String userId, String productCode, CustomerDataFields custmrDataFields, boolean regenerate) throws DRException, IOException {
        String jsonKeyFileName;
        JsonObject loanAgreementJson = new JsonObject();
        String fileName = applicationId + "_" + Constants.LOAN_AGREEMENT + ".pdf";
        String loanAgreementPath = filePathGen + fileName;

        // @author Abhishek.Raj.CAG
        if (
                Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(productCode)
        ) {
            logger.debug("generateLoanAgreement- for Unnati sup add", productCode);
            jsonKeyFileName = Constants.REPORT_JSONKEYS
                    + language
                    + "LoanAgreementSupAdd.json";
        }
        else if (
                Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(productCode) || Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(productCode)
        ) {
            logger.debug("generateLoanAgreement- for UNNATI_EMERGENCY_PRODUCT_CODE", productCode);
            jsonKeyFileName = Constants.REPORT_JSONKEYS
                    + language
                    + "LoanAgreementFWLAdd.json";
        }
        else {
            logger.debug("generateLoanAgreement- for UNNATI_Main_ flow", productCode);

        jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "LoanAgreement.json";
        }
        //----END----
        JSONObject contentKeys = getFileContent(jsonKeyFileName, JsonKeyFolders.LOAN_AGREEMENT.getValue());

        List<ApplicationWorkflow> workflow;
        workflow = applnWfRepository.findByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumAsc(appId,
                applicationId, versionNum);
        logger.debug("Workflow details {} ", workflow);


        //            @author Abhishek.Raj CAGL
        String leadInitiationDateStr = "";
        String kmIdFromCDH = "-";


        String bmIdFromCDH = "-";

        List<ApplicationTimelineDtl> timelineList = custmrDataFields.getApplicationTimelineDtl();

        if (timelineList != null && !timelineList.isEmpty()) {
            for (ApplicationTimelineDtl dtl : timelineList) {
                if (dtl != null && Constants.SANCTION.equalsIgnoreCase(dtl.getStage())) {
                    bmIdFromCDH = dtl.getUserId() == null ? "-" : dtl.getUserId();
                }
            }
            logger.debug("bmIdFromCDH: {}", bmIdFromCDH);
        } else {
            logger.debug("ApplicationTimelineDtl is empty for applicationId: {}", applicationId);
        }

        UnnatiIexceedCDHLead cdhLeadDetails = custmrDataFields.getCdhLeadDetails();
        if (cdhLeadDetails != null && cdhLeadDetails.getKmId() != null){
            kmIdFromCDH = cdhLeadDetails.getKmId();
            logger.debug("KM ID from CDH: {}", kmIdFromCDH);
        }else {
            logger.debug("cdhLeadDetails or getKmId is null for applicationId: {}", applicationId);
        }

        if (cdhLeadDetails != null && cdhLeadDetails.getLeadInitiationDate() != null) {
            try {
                leadInitiationDateStr = CommonUtils.formatDateTimeToDateStr(cdhLeadDetails.getLeadInitiationDate());
                logger.debug("Formatted leadInitiationDateStr: {}", leadInitiationDateStr);
            } catch (Exception e) {
                logger.error("Error while formatting lead initiation date: ", e);
            }
        } else {
            logger.debug("cdhLeadDetails or leadInitiationDate is null for applicationId: {}", applicationId);
        }



        String actualUserId;

        if (bmIdFromCDH != null && bmIdFromCDH.contains("(")) {
            actualUserId = bmIdFromCDH.substring(
                bmIdFromCDH.indexOf("(") + 1,
                bmIdFromCDH.indexOf(")")
        );
        } else {
            actualUserId = bmIdFromCDH;
        }
        Optional<String> usernameOptBMForAddProdct = tbUserRepository.findUserNameByUserId(actualUserId);
        String usernameBMForAddProdct = usernameOptBMForAddProdct.orElse("-");
        logger.info("usernameBMForAddProdct : " + usernameBMForAddProdct);

        Optional<String> usernameOptKMFromCDH = tbUserRepository.findUserNameByUserId(kmIdFromCDH);
        String usernameKMFromCDH = usernameOptKMFromCDH.orElse("-");
        logger.info("usernameOptKMFromCDH : " + usernameKMFromCDH);

//            ----end

        if (!workflow.isEmpty()) {
            custmrDataFields.setApplicationWorkflowList(workflow);
        }
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, versionNum);

        ApplicationMaster applicationMasterData = null;
        if (applicationMasterOpt.isPresent()) {
            applicationMasterData = applicationMasterOpt.get();
        }
        String product = applicationMasterData.getProductCode();
        Optional<ProductDetails> productDetails = productDetailsrepository.findById(product);

        String productName = "";
        if (productDetails.isPresent()) {
            logger.debug("productName data found.");
            logger.debug("Loan product Name:" + productDetails.get().getProductName());
            productName = productDetails.get().getProductName();
        }

        // sactionedDateStr
        List<String> statusList = new ArrayList<>();
        statusList.add(WorkflowStatus.SANCTIONED.getValue());
        List<ApplicationWorkflow> wfList = applnWfRepository
                .findByApplicationIdAndApplicationStatusInOrderByCreateTsDesc(applicationId,
                        statusList);

        String sactionedDateStr = "";
        if (!wfList.isEmpty()) {
            LocalDateTime sactionedDate = wfList.get(0).getCreateTs();
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                sactionedDateStr = sactionedDate.format(formatter);
            } catch (Exception e) {
                logger.error("error while conversion of Date :" + e);
            }
        }
        List<ExistingGLLoanDetails> existingGLLoanDetailsList = existingGLLoanDetailsRepository.findByCustomerId(applicationMasterData.getSearchCode2());
        if (existingGLLoanDetailsList != null && !existingGLLoanDetailsList.isEmpty()) {
            custmrDataFields.setExistingGLLoanDetails(existingGLLoanDetailsList);
        } else {
            logger.debug("No Existing GL loan details found for applicantId: {}", applicationMasterData.getSearchCode2());
            custmrDataFields.setExistingGLLoanDetails(Collections.emptyList());
        }

// @author Abhishek.Raj
        List<RepaymentSchedule> repaymentList = new ArrayList<>();

        try {
            Optional<ApiExecutionLog> apiExecutionRecordOpt = apiExecutionLogRepository.findTopByApplicationIdAndApiNameOrderByCreateTsDesc(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE);
            if (apiExecutionRecordOpt.isPresent()) {
                ApiExecutionLog apiExecutionRecord = apiExecutionRecordOpt.get();
                if (apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS) || apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS_DMS)) {
                    String apiRespJsonString = apiExecutionRecord.getResponsePayload();
                    logger.debug("apiRespJsonString " + apiRespJsonString);
                    Gson gson1 = new Gson();

                    // Parse the string into a JsonObject
                    JsonObject root = gson1.fromJson(apiRespJsonString, JsonObject.class);

                    // Extract the "body" array
                    JsonArray bodyArray = root.getAsJsonArray("body");

                    // Convert to list of RepaymentSchedule
                    Type listType = new TypeToken<List<RepaymentSchedule>>() {
                    }.getType();
                    repaymentList = gson1.fromJson(bodyArray, listType);
                    logger.debug("repaymentList size." + repaymentList.size());
                } else {
                    logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR);
                    throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR);
                }
            } else {
                logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR1);
            }
        } catch (Exception e) {
            logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR2 + e);
            throw new IOException("Error fetching ApiExecutionLog - generateSanctionLetter data", e);
        }

        // @author Abhishek.Raj

        String loanAgreementBase64;
        if (Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(productCode)
                || Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(productCode)
                || Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(productCode)
                ) {
            loanAgreementBase64 = new LoanAgreement().generatePdfForDbkitAdd(contentKeys, loanAgreementPath, custmrDataFields, language, productName, sactionedDateStr,  kmIdFromCDH, usernameKMFromCDH, bmIdFromCDH, usernameBMForAddProdct, repaymentList);
        } else {
            loanAgreementBase64 = new LoanAgreement().generatePdfForDbkit(contentKeys, loanAgreementPath, custmrDataFields, language);
        }

        loanAgreementJson.addProperty(Constants.ORDER, 4);
        loanAgreementJson.addProperty(Constants.BASE64, loanAgreementBase64);
        loanAgreementJson.addProperty("docType", Constants.LOAN_AGREEMENT);

        File document = new File(loanAgreementPath);
        String docSize = String.valueOf(document.length() / 1024.0);
        loanAgreementJson.addProperty("size", docSize);

        // language condition
        String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
        logger.debug("inputLanguage" + language);
        boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                .anyMatch(lang -> lang.equalsIgnoreCase(language));
        if (!isValidLanguage) {
            if (!regenerate) {
                long deletedCount = documentsRepository.deleteByApplicationIdAndDocTypeAndUploadType(applicationId, Constants.LOAN_AGREEMENT, Constants.MANUAL);

                if (deletedCount == 0) {
                    logger.debug("No records found to delete for applicationId: {}, documentType: {}, uploadType: {}",
                            applicationId, Constants.LOAN_AGREEMENT, Constants.MANUAL);
                } else {
                    logger.debug("Deleted {} records for applicationId: {}, documentType: {}, uploadType: {}", deletedCount, applicationId, Constants.LOAN_AGREEMENT, Constants.MANUAL);
                }
                Optional<Documents> documentRecordOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(applicationId,
                        Constants.LOAN_AGREEMENT, Constants.GENERATED);
                if (documentRecordOpt.isPresent()) {
                    Documents documentRecord = documentRecordOpt.get();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.LOAN_AGREEMENT);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setCreateTs(Timestamp.valueOf(LocalDateTime.now()));
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                } else {
                    Documents documentRecord = new Documents();
                    documentRecord.setApplicationId(applicationId);
                    documentRecord.setDocName(fileName);
                    documentRecord.setAppId(appId);
                    documentRecord.setDocType(Constants.LOAN_AGREEMENT);
                    documentRecord.setDocStatus(Constants.PENDING);
                    documentRecord.setUploadType(Constants.GENERATED);
                    documentRecord.setCreatedBy(userId);
                    documentRecord.setProductType(productCode);
                    documentRecord.setLanguage(language);
                    documentRecord.setDocSize(docSize);
                    documentsRepository.save(documentRecord);
                }
            }
        }
        return loanAgreementJson;
    }

    // @author Abhishek.Raj.CAG
    private JsonObject generateLoanApplication(String appId, String applicationId, int versionNum, String filePathGen,
                                               String language, String userId, String productCode, boolean regenerate) throws DRException, IOException, JRException {
        logger.debug(
                "Entering generateLoanApplication with appId: {}, applicationId: {}, versionNum: {}, filePathGen: {}, language: {}",
                appId, applicationId, versionNum, filePathGen, language);
        String jsonKeyFileName;
        JSONObject keysForContent = new JSONObject();
        JSONObject fileContent = new JSONObject();
        CustomerDataFields custmrDataFields = null;
        JsonObject loanApplicationJson = new JsonObject();
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, versionNum);


        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            logger.debug("ApplicationMaster data found for applicationId: {}", applicationId);
            custmrDataFields = getCustomerData(applicationMasterData, applicationId, appId, versionNum);
            logger.debug("CustomerDataFields retrieved: {}", custmrDataFields);

            Gson gson = new Gson();
            Optional<BCMPIIncomeDetails> bcmpiIncomeDataOpt = bcmpiIncomeDetailsRepo.findById(applicationId);
            if (bcmpiIncomeDataOpt.isPresent()) {
                logger.debug("bcmpiIncomeData found");
                BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper = gson.fromJson(
                        bcmpiIncomeDataOpt.get().getPayload(), BCMPIIncomeDetailsWrapper.class);
                BCMPIIncomeDetails bcmpiIncomeDetails = bcmpiIncomeDataOpt.get();
                bcmpiIncomeDetails.setBcmpiIncomeDetailsWrapper(bcmpiIncomeDetailsWrapper);
                custmrDataFields.setBcmpiIncomeDetails(bcmpiIncomeDetails);
            }
            List<String> docTypes = Arrays.asList("CUSTOMER_PHOTOGRAPH");

            List<ApplicationDocuments> applnDocumentList = applicationDocumentsRepository.fetchDocumentsByApplicationIdAndDocumentTypeList(applicationMasterData.getApplicationId(),docTypes);
            List<ApplicationWorkflow> workflow;
            workflow = applnWfRepository.findByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumAsc(appId,
                    applicationId, versionNum);
            logger.debug("Workflow details {} ", workflow);


            String bmId = "-";
            String kmId = "-";

            String kmSubmDateStr = "";
            String bmSubmDateStr = "";
//            @author Abhishek.Raj CAGL
            String leadInitiationDateStr = "";
            String kmIdFromCDH = "-";


            String bmIdFromCDH = "-";

            List<ApplicationTimelineDtl> timelineList = custmrDataFields.getApplicationTimelineDtl();

            if (timelineList != null && !timelineList.isEmpty()) {
                for (ApplicationTimelineDtl dtl : timelineList) {
                    if (dtl != null && Constants.SANCTION.equalsIgnoreCase(dtl.getStage())) {
                        bmIdFromCDH = dtl.getUserId() == null ? "-" : dtl.getUserId();
                    }
                }
                logger.debug("bmIdFromCDH: {}", bmIdFromCDH);
            } else {
                logger.debug("ApplicationTimelineDtl is empty for applicationId: {}", applicationId);
            }

            UnnatiIexceedCDHLead cdhLeadDetails = custmrDataFields.getCdhLeadDetails();
            if (cdhLeadDetails != null && cdhLeadDetails.getKmId() != null){
                kmIdFromCDH = cdhLeadDetails.getKmId();
                logger.debug("KM ID from CDH: {}", kmIdFromCDH);
            }else {
                logger.debug("cdhLeadDetails or getKmId is null for applicationId: {}", applicationId);
            }

            if (cdhLeadDetails != null && cdhLeadDetails.getLeadInitiationDate() != null) {
                try {
                    leadInitiationDateStr = CommonUtils.formatDateTimeToDateStr(cdhLeadDetails.getLeadInitiationDate());
                    logger.debug("Formatted leadInitiationDateStr: {}", leadInitiationDateStr);
                } catch (Exception e) {
                    logger.error("Error while formatting lead initiation date: ", e);
                }
            } else {
                logger.debug("cdhLeadDetails or leadInitiationDate is null for applicationId: {}", applicationId);
            }

//            ----end
            if (!workflow.isEmpty()) {
                custmrDataFields.setApplicationWorkflowList(workflow);


                LocalDateTime kmSubmDate = null;
                for (ApplicationWorkflow applnWorkflow : custmrDataFields.getApplicationWorkflowList()) {
                    if (Constants.INITIATOR.equalsIgnoreCase(applnWorkflow.getCurrentRole()) && (WorkflowStatus.APPROVED.getValue().equalsIgnoreCase(
                            applnWorkflow.getApplicationStatus()
                    ) || WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(applnWorkflow.getApplicationStatus()))) {
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

                if (Constants.UNNATI_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.OPENMARKET_LOAN_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        // @author Abhishek.Raj.CAG
                        || Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.UNNATI_RESTART_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.UNNATI_EMERGENCY_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.FAMILY_WELFARE_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                    //----END----
                ) {
                    logger.info("Unnati application");

                    String previousWorkflowStatus = null;
                    for (ApplicationWorkflow appnWorkflow : custmrDataFields.getApplicationWorkflowList()) {
                        String currentStatus = appnWorkflow.getApplicationStatus();
                        if (Constants.APPROVED.equalsIgnoreCase(appnWorkflow.getApplicationStatus())) {
                            if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(previousWorkflowStatus)) {
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
            }
            // @author Abhishek.Raj.CAGL
            List<ExistingGLLoanDetails> existingGLLoanDetailsList = existingGLLoanDetailsRepository.findByCustomerId(applicationMasterData.getSearchCode2());
            if (existingGLLoanDetailsList != null && !existingGLLoanDetailsList.isEmpty()) {
                custmrDataFields.setExistingGLLoanDetails(existingGLLoanDetailsList);
            } else {
                logger.debug("No Existing GL loan details found for applicantId: {}", applicationMasterData.getSearchCode2());
                custmrDataFields.setExistingGLLoanDetails(Collections.emptyList());
            }
            // @author Abhishek.Raj.CAGL
            //2.
            Optional<String> usernameOptKM = tbUserRepository.findUserNameByUserId(kmId);
            String usernameKM = usernameOptKM.orElse("-");
            logger.info("usernameKM : " + usernameKM);

            Optional<String> usernameOptKMFromCDH = tbUserRepository.findUserNameByUserId(kmIdFromCDH);
            String usernameKMFromCDH = usernameOptKMFromCDH.orElse("-");
            logger.info("usernameOptKMFromCDH : " + usernameKM);

            Optional<String> usernameOptBM = tbUserRepository.findUserNameByUserId(bmId);
            String usernameBM = usernameOptBM.orElse("-");
            logger.info("usernameBM : " + usernameBM);




            String actualUserId;

            if (bmIdFromCDH != null && bmIdFromCDH.contains("(")) {
                actualUserId = bmIdFromCDH.substring(
                    bmIdFromCDH.indexOf("(") + 1,
                    bmIdFromCDH.indexOf(")")
            );
            } else {
                actualUserId = bmIdFromCDH;
            }

            Optional<String> usernameOptBMForAddProdct = tbUserRepository.findUserNameByUserId(actualUserId);
            String usernameBMForAddProdct = usernameOptBMForAddProdct.orElse("-");
            logger.info("usernameBMForAddProdct : " + usernameBMForAddProdct);


            String fileName = applicationId + "_" + Constants.LOAN_APPLICATION + ".pdf";
            String loanApplicationFilePath = filePathGen + fileName;
            logger.debug("Loan application file path: {}", loanApplicationFilePath);

            // @author Abhishek.Raj.CAG
            if (
                    Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
            ) {
                jsonKeyFileName = Constants.REPORT_JSONKEYS
                        + language
                        + "LoanApplicationSupAdd.json";
            }
            else if (Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(applicationMasterData.getProductCode()) || Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
            ) {
                jsonKeyFileName = Constants.REPORT_JSONKEYS
                        + language
                        + "LoanApplicationFWLAdd.json";
            }
            else {
            jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "LoanApplication.json";
            }
            //----END----

            logger.debug("Reading JSON content from server for file: {}", jsonKeyFileName);
            fileContent = new JSONObject(
                    adapterUtil.readJSONContentFromServer(JsonKeyFolders.LOAN_APPLICATION.getValue() + "/" + jsonKeyFileName));
            logger.debug("File content retrieved: {}", fileContent);
            keysForContent = fileContent.getJSONObject(Constants.KEYS_FOR_CONTENT);
            logger.debug("Keys for content extracted: {}", keysForContent);
            String loanApplicationBase64;
            // @author Abhishek.Raj.CAG
            if (
                    Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
                            || Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
                            || Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
            ) {
                loanApplicationBase64 = new LoanApplication().generateLoanApplicationPdfForDBKitAdd(applicationMasterData,applnDocumentList,
                        custmrDataFields, keysForContent, loanApplicationFilePath, language, kmIdFromCDH, bmIdFromCDH, kmSubmDateStr, bmSubmDateStr, usernameKMFromCDH, usernameBMForAddProdct, leadInitiationDateStr);
            } else {
                loanApplicationBase64 = new LoanApplication().generateLoanApplicationPdfForDBKit(applicationMasterData,applnDocumentList,
                        custmrDataFields, keysForContent, loanApplicationFilePath, language, kmId, bmId, kmSubmDateStr, bmSubmDateStr, usernameKM, usernameBM, leadInitiationDateStr,kmIdFromCDH, bmIdFromCDH,usernameKMFromCDH, usernameBMForAddProdct);
            }
            //----END----
//            String loanApplicationBase64 = Base64.getEncoder().encodeToString(loanApplicationByte);
            loanApplicationJson.addProperty(Constants.ORDER, 1);
            loanApplicationJson.addProperty(Constants.BASE64, loanApplicationBase64);
            loanApplicationJson.addProperty("docType", Constants.LOAN_APPLICATION);
            logger.debug("Generated loan application PDF successfully for applicationId: {}", applicationId);


            File document = new File(loanApplicationFilePath);
            String docSize = String.valueOf(document.length() / 1024.0);
            loanApplicationJson.addProperty("size", docSize);

            // language condition
            String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
            logger.debug("inputLanguage" + language);
            boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                    .anyMatch(lang -> lang.equalsIgnoreCase(language));
            if (!isValidLanguage) {
                if (!regenerate) {
                    long deletedCount = documentsRepository.deleteByApplicationIdAndDocTypeAndUploadType(applicationId, Constants.LOAN_APPLICATION, Constants.MANUAL);

                    if (deletedCount == 0) {
                        logger.debug("No records found to delete for applicationId: {}, documentType: {}, uploadType: {}",
                                applicationId, Constants.LOAN_APPLICATION, Constants.MANUAL);
                    } else {
                        logger.debug("Deleted {} records for applicationId: {}, documentType: {}, uploadType: {}", deletedCount, applicationId, Constants.LOAN_APPLICATION, Constants.MANUAL);
                    }

                    Optional<Documents> documentRecordOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(applicationId,
                            Constants.LOAN_APPLICATION, Constants.GENERATED);
                    if (documentRecordOpt.isPresent()) {
                        Documents documentRecord = documentRecordOpt.get();
                        documentRecord.setApplicationId(applicationId);
                        documentRecord.setDocName(fileName);
                        documentRecord.setAppId(appId);
                        documentRecord.setDocType(Constants.LOAN_APPLICATION);
                        documentRecord.setDocStatus(Constants.PENDING);
                        documentRecord.setUploadType(Constants.GENERATED);
                        documentRecord.setCreatedBy(userId);
                        documentRecord.setCreateTs(Timestamp.valueOf(LocalDateTime.now()));
                        documentRecord.setProductType(productCode);
                        documentRecord.setLanguage(language);
                        documentRecord.setDocSize(docSize);
                        documentsRepository.save(documentRecord);
                    } else {
                        Documents documentRecord = new Documents();
                        documentRecord.setApplicationId(applicationId);
                        documentRecord.setDocName(fileName);
                        documentRecord.setAppId(appId);
                        documentRecord.setDocType(Constants.LOAN_APPLICATION);
                        documentRecord.setDocStatus(Constants.PENDING);
                        documentRecord.setUploadType(Constants.GENERATED);
                        documentRecord.setCreatedBy(userId);
                        documentRecord.setProductType(productCode);
                        documentRecord.setLanguage(language);
                        documentRecord.setDocSize(docSize);
                        documentsRepository.save(documentRecord);
                    }
                }
            }
        } else {
            logger.error("Application not found for applicationId: {}", applicationId);
        }
        logger.debug("Exiting generateLoanApplication for applicationId: {}", applicationId);
        return loanApplicationJson;
    }

    private JsonObject generateSanctionLetter(String appId, String applicationId, int versionNum, String filePathGen,
                                              String language, String userId, String productCode, boolean regenerate) throws DRException, IOException, JRException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonKeyFileName;
        JSONObject fileContent = new JSONObject();
        CustomerDataFields custmrDataFields = null;
        JsonObject sanctionLetterJson = new JsonObject();
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, versionNum);

        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            String product = applicationMasterData.getProductCode();
            logger.debug("product :" + product);
            custmrDataFields = getCustomerData(applicationMasterData, applicationId, appId, versionNum);


            // @author Abhishek.Raj.CAG
            if (
                    Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
            ) {
                jsonKeyFileName = Constants.REPORT_JSONKEYS
                        + language
                        + "SanctionLetterSupAdd.json";
            }
            else if (Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
                            || Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
            ) {
                jsonKeyFileName = Constants.REPORT_JSONKEYS
                        + language
                        + "SanctionLetterFWLAdd.json";
            } else {
            jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "SanctionLetter.json";
            }
            //----END----

            fileContent = sanitizeJson(getFileContent(jsonKeyFileName, JsonKeyFolders.SANCTION_LETTER.getValue()));
            String fileName = applicationId + "_" + Constants.SANCTION_LETTER + ".pdf";
            String filePath = filePathGen + fileName;

            List<RepaymentSchedule> repaymentList = new ArrayList<>();

            try {
                Optional<ApiExecutionLog> apiExecutionRecordOpt = apiExecutionLogRepository.findTopByApplicationIdAndApiNameOrderByCreateTsDesc(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE);
                if (apiExecutionRecordOpt.isPresent()) {
                    ApiExecutionLog apiExecutionRecord = apiExecutionRecordOpt.get();
                    if (apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS) || apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS_DMS)) {
                        String apiRespJsonString = apiExecutionRecord.getResponsePayload();
                        logger.debug("apiRespJsonString " + apiRespJsonString);
                        Gson gson1 = new Gson();

                        // Parse the string into a JsonObject
                        JsonObject root = gson1.fromJson(apiRespJsonString, JsonObject.class);

                        // Extract the "body" array
                        JsonArray bodyArray = root.getAsJsonArray("body");

                        // Convert to list of RepaymentSchedule
                        Type listType = new TypeToken<List<RepaymentSchedule>>() {
                        }.getType();
                        repaymentList = gson1.fromJson(bodyArray, listType);
                        logger.debug("repaymentList size." + repaymentList.size());
                    } else {
                        logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR);
                        throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR);
                    }
                } else {
                    logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                    throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                }
            } catch (Exception e) {
                logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR2 + e);
                throw new IOException("Error fetching ApiExecutionLog - generateSanctionLetter data", e);
            }


            // sactionedDateStr
            List<String> statusList = new ArrayList<>();
            statusList.add(WorkflowStatus.SANCTIONED.getValue());
            List<ApplicationWorkflow> wfList = applnWfRepository
                    .findByApplicationIdAndApplicationStatusInOrderByCreateTsDesc(applicationId,
                            statusList);

            String sactionedDateStr = "";
            if (!wfList.isEmpty()) {
                LocalDateTime sactionedDate = wfList.get(0).getCreateTs();
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                    sactionedDateStr = sactionedDate.format(formatter);
                } catch (Exception e) {
                    logger.error("error while conversion of Date :" + e);
                }
            }

            List<ApplicationWorkflow> workflow;
            workflow = applnWfRepository.findByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumAsc(appId,
                    applicationId, versionNum);
            logger.debug("Workflow details {} ", workflow);

            String bmId = "-";
            String bmName = "";

            //            @author Abhishek.Raj CAGL
            String leadInitiationDateStr = "";
            String kmIdFromCDH = "-";

            String bmIdFromCDH = "-";

            List<ApplicationTimelineDtl> timelineList = custmrDataFields.getApplicationTimelineDtl();

            if (timelineList != null && !timelineList.isEmpty()) {
                for (ApplicationTimelineDtl dtl : timelineList) {
                    if (dtl != null && Constants.SANCTION.equalsIgnoreCase(dtl.getStage())) {
                        bmIdFromCDH = dtl.getUserId() == null ? "-" : dtl.getUserId();
                    }
                }
                logger.debug("bmIdFromCDH: {}", bmIdFromCDH);
            } else {
                logger.debug("ApplicationTimelineDtl is empty for applicationId: {}", applicationId);
            }

            UnnatiIexceedCDHLead cdhLeadDetails = custmrDataFields.getCdhLeadDetails();
            if (cdhLeadDetails != null && cdhLeadDetails.getKmId() != null){
                kmIdFromCDH = cdhLeadDetails.getKmId();
                logger.debug("KM ID from CDH: {}", kmIdFromCDH);
            }else {
                logger.debug("cdhLeadDetails or getKmId is null for applicationId: {}", applicationId);
            }

            if (cdhLeadDetails != null && cdhLeadDetails.getLeadInitiationDate() != null) {
                try {
                    leadInitiationDateStr = CommonUtils.formatDateTimeToDateStr(cdhLeadDetails.getLeadInitiationDate());
                    logger.debug("Formatted leadInitiationDateStr: {}", leadInitiationDateStr);
                } catch (Exception e) {
                    logger.error("Error while formatting lead initiation date: ", e);
                }
            } else {
                logger.debug("cdhLeadDetails or leadInitiationDate is null for applicationId: {}", applicationId);
            }

//            ----end


            if (!workflow.isEmpty()) {
                custmrDataFields.setApplicationWorkflowList(workflow);

                if (Constants.UNNATI_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.OPENMARKET_LOAN_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        // @author Abhishek.Raj.CAG
                        || Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.UNNATI_EMERGENCY_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.FAMILY_WELFARE_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.UNNATI_RESTART_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                    //----END----
                ) {
                    logger.info("Unnati application");

                    String previousWorkflowStatus = null;
                    for (ApplicationWorkflow appnWorkflow : custmrDataFields.getApplicationWorkflowList()) {
                        String currentStatus = appnWorkflow.getApplicationStatus();
                        if (Constants.APPROVED.equalsIgnoreCase(appnWorkflow.getApplicationStatus())) {
                            if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(previousWorkflowStatus)) {
                                bmId = appnWorkflow.getCreatedBy();
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
                                }
                            }
                            break;
                        }
                    }

                } else {
                    logger.info("Not an Unnati application");
                }
            }

            Optional<ProductDetails> productDetails = productDetailsrepository.findById(product);
            String productName = "";
            if (productDetails.isPresent()) {
                logger.debug("productName data found.");
                logger.debug("Loan product Name:" + productDetails.get().getProductName());
                productName = productDetails.get().getProductName();
            }

            Optional<String> usernameOptBM = tbUserRepository.findUserNameByUserId(bmId);
            String usernameBM = usernameOptBM.orElse("-");
            logger.info("BM name" + usernameBM);



            String actualUserId;

            if (bmIdFromCDH != null && bmIdFromCDH.contains("(")) {
                actualUserId = bmIdFromCDH.substring(
                    bmIdFromCDH.indexOf("(") + 1,
                    bmIdFromCDH.indexOf(")")
            );
            } else {
                actualUserId = bmIdFromCDH;
            }
            Optional<String> usernameOptBMForAddProdct = tbUserRepository.findUserNameByUserId(actualUserId);
            String usernameBMForAddProdct = usernameOptBMForAddProdct.orElse("-");
            logger.info("usernameBMForAddProdct : " + usernameBMForAddProdct);

            Optional<String> usernameOptKMFromCDH = tbUserRepository.findUserNameByUserId(kmIdFromCDH);
            String usernameKMFromCDH = usernameOptKMFromCDH.orElse("-");
            logger.info("usernameOptKMFromCDH : " + usernameKMFromCDH);

//            byte[] sanctionLetterByte = new SanctionLetter().generatePdfForDbKit(fileContent, custmrDataFields, amount,
//                    installment, term, filePath, sactionedDateStr);
            String sanctonLetterBase64;
            if (
                // @author Abhishek.Raj.CAG
                    Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
                            .equals(custmrDataFields.getApplicationMaster().getProductCode())
                            || Constants.UNNATI_EMERGENCY_PRODUCT_CODE
                            .equals(custmrDataFields.getApplicationMaster().getProductCode())
                            || Constants.FAMILY_WELFARE_PRODUCT_CODE
                            .equals(custmrDataFields.getApplicationMaster().getProductCode())
                //----END----
            ) {
                sanctonLetterBase64 = new SanctionLetter().generatePdfForDbKitAdd(fileContent, custmrDataFields, filePath, sactionedDateStr, repaymentList, language, bmIdFromCDH, usernameBMForAddProdct, productName);
            } else {
                sanctonLetterBase64 = new SanctionLetter().generatePdfForDbKit(fileContent, custmrDataFields, filePath, sactionedDateStr, repaymentList, language, bmId, usernameBM, productName, bmIdFromCDH, usernameBMForAddProdct, leadInitiationDateStr);
            }


//            String sanctonLetterBase64 = Base64.getEncoder().encodeToString(sanctionLetterByte);

            File document = new File(filePath);
            String docSize = String.valueOf(document.length() / 1024.0);

            sanctionLetterJson.addProperty("size", docSize);
            sanctionLetterJson.addProperty(Constants.ORDER, 2);
            sanctionLetterJson.addProperty(Constants.BASE64, sanctonLetterBase64);
            sanctionLetterJson.addProperty("docType", Constants.SANCTION_LETTER);

            // language condition
            String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
            logger.debug("inputLanguage" + language);
            boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                    .anyMatch(lang -> lang.equalsIgnoreCase(language));
            if (!isValidLanguage) {
                if (!regenerate) {
                    long deletedCount = documentsRepository.deleteByApplicationIdAndDocTypeAndUploadType(applicationId, Constants.SANCTION_LETTER, Constants.MANUAL);

                    if (deletedCount == 0) {
                        logger.debug("No records found to delete for applicationId: {}, documentType: {}, uploadType: {}",
                                applicationId, Constants.SANCTION_LETTER, Constants.MANUAL);
                    } else {
                        logger.debug("Deleted {} records for applicationId: {}, documentType: {}, uploadType: {}", deletedCount, applicationId, Constants.SANCTION_LETTER, Constants.MANUAL);
                    }
                    Optional<Documents> documentRecordOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(applicationId,
                            Constants.SANCTION_LETTER, Constants.GENERATED);
                    if (documentRecordOpt.isPresent()) {
                        Documents documentRecord = documentRecordOpt.get();
                        documentRecord.setApplicationId(applicationId);
                        documentRecord.setDocName(fileName);
                        documentRecord.setAppId(appId);
                        documentRecord.setDocType(Constants.SANCTION_LETTER);
                        documentRecord.setDocStatus(Constants.PENDING);
                        documentRecord.setUploadType(Constants.GENERATED);
                        documentRecord.setCreatedBy(userId);
                        documentRecord.setCreateTs(Timestamp.valueOf(LocalDateTime.now()));
                        documentRecord.setProductType(productCode);
                        documentRecord.setLanguage(language);
                        documentRecord.setDocSize(docSize);
                        documentsRepository.save(documentRecord);
                    } else {
                        Documents documentRecord = new Documents();
                        documentRecord.setApplicationId(applicationId);
                        documentRecord.setDocName(fileName);
                        documentRecord.setAppId(appId);
                        documentRecord.setDocType(Constants.SANCTION_LETTER);
                        documentRecord.setDocStatus(Constants.PENDING);
                        documentRecord.setUploadType(Constants.GENERATED);
                        documentRecord.setCreatedBy(userId);
                        documentRecord.setProductType(productCode);
                        documentRecord.setLanguage(language);
                        documentRecord.setDocSize(docSize);
                        documentsRepository.save(documentRecord);
                    }
                }
            }
        }
        return sanctionLetterJson;
    }

    // @author Abhishek.Raj.CAG
    private JsonObject generateKfs(String appId, String applicationId, int versionNum, String filePathGen,
                                   String language, String userId, String productCode, boolean regenerate) throws DRException, IOException, JRException {
        logger.debug(
                "Entering generateKfs with appId: {}, applicationId: {}, versionNum: {}, filePathGen: {}, language: {}",
                appId, applicationId, versionNum, filePathGen, language);
        String jsonKeyFileName;
        JSONObject keysForContent = new JSONObject();
        JSONObject fileContent = new JSONObject();
        CustomerDataFields custmrDataFields = null;
        JsonObject kfsJson = new JsonObject();
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, versionNum);




        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            logger.debug("ApplicationMaster data found for applicationId: {}", applicationId);
            custmrDataFields = getCustomerData(applicationMasterData, applicationId, appId, versionNum);
            logger.debug("CustomerDataFields retrieved: {}", custmrDataFields);

            List<ApplicationWorkflow> workflow;
            workflow = applnWfRepository.findByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumAsc(appId,
                    applicationId, versionNum);
            logger.debug("Workflow details {} ", workflow);


            String bmId = "-";
            String kmId = "-";

            String kmSubmDateStr = "";
            String bmSubmDateStr = "";

            //            @author Abhishek.Raj CAGL
            String leadInitiationDateStr = "";
            String kmIdFromCDH = "-";


            String bmIdFromCDH = "-";

            List<ApplicationTimelineDtl> timelineList = custmrDataFields.getApplicationTimelineDtl();

            if (timelineList != null && !timelineList.isEmpty()) {
                for (ApplicationTimelineDtl dtl : timelineList) {
                    if (dtl != null && Constants.SANCTION.equalsIgnoreCase(dtl.getStage())) {
                        bmIdFromCDH = dtl.getUserId() == null ? "-" : dtl.getUserId();
                    }
                }
                logger.debug("bmIdFromCDH: {}", bmIdFromCDH);
            } else {
                logger.debug("ApplicationTimelineDtl is empty for applicationId: {}", applicationId);
            }

            UnnatiIexceedCDHLead cdhLeadDetails = custmrDataFields.getCdhLeadDetails();
            if (cdhLeadDetails != null && cdhLeadDetails.getKmId() != null){
                kmIdFromCDH = cdhLeadDetails.getKmId();
                logger.debug("KM ID from CDH: {}", kmIdFromCDH);
            }else {
                logger.debug("cdhLeadDetails or getKmId is null for applicationId: {}", applicationId);
            }

            if (cdhLeadDetails != null && cdhLeadDetails.getLeadInitiationDate() != null) {
                try {
                    leadInitiationDateStr = CommonUtils.formatDateTimeToDateStr(cdhLeadDetails.getLeadInitiationDate());
                    logger.debug("Formatted leadInitiationDateStr: {}", leadInitiationDateStr);
                } catch (Exception e) {
                    logger.error("Error while formatting lead initiation date: ", e);
                }
            } else {
                logger.debug("cdhLeadDetails or leadInitiationDate is null for applicationId: {}", applicationId);
            }


            String product = applicationMasterData.getProductCode();
            Optional<ProductDetails> productDetails = productDetailsrepository.findById(product);

            String productName = "";
            if (productDetails.isPresent()) {
                logger.debug("productName data found.");
                logger.debug("Loan product Name:" + productDetails.get().getProductName());
                productName = productDetails.get().getProductName();
            }

            if (!workflow.isEmpty()) {
                custmrDataFields.setApplicationWorkflowList(workflow);


                LocalDateTime kmSubmDate = null;
                for (ApplicationWorkflow applnWorkflow : custmrDataFields.getApplicationWorkflowList()) {
                    if (Constants.INITIATOR.equalsIgnoreCase(applnWorkflow.getCurrentRole()) && (WorkflowStatus.APPROVED.getValue().equalsIgnoreCase(
                            applnWorkflow.getApplicationStatus()
                    ) || WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(applnWorkflow.getApplicationStatus()))) {
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

                if (Constants.UNNATI_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.OPENMARKET_LOAN_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        // @author Abhishek.Raj.CAG
                        || Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.UNNATI_EMERGENCY_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.FAMILY_WELFARE_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                    //----END----
                ) {
                    logger.info("Unnati application");
                    String previousWorkflowStatus = null;
                    for (ApplicationWorkflow appnWorkflow : custmrDataFields.getApplicationWorkflowList()) {
                        String currentStatus = appnWorkflow.getApplicationStatus();
                        if (Constants.APPROVED.equalsIgnoreCase(appnWorkflow.getApplicationStatus())) {
                            if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(previousWorkflowStatus)) {
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
            }

            //2.
            Optional<String> usernameOptKM = tbUserRepository.findUserNameByUserId(kmId);
            String usernameKM = usernameOptKM.orElse("-");
            logger.info("usernameKM : " + usernameKM);

            Optional<String> usernameOptBM = tbUserRepository.findUserNameByUserId(bmId);
            String usernameBM = usernameOptBM.orElse("-");
            logger.info("usernameBM : " + usernameBM);


            String fileName = applicationId + "_" + Constants.KFS + ".pdf";
            String kfsFilePath = filePathGen + fileName;
            logger.debug("KFS file path: {}", kfsFilePath);

            jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "KfsSheetReport.json";

            // @author Abhishek.Raj.CAG
//			if (
//					Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
//							|| Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
//							|| Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
//			) {
//				jsonKeyFileName = Constants.REPORT_JSONKEYS
//						+ language
//						+ "LoanApplicationAdd.json";
//			} else{
//				jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "LoanApplication.json";
//			}
            //----END----

            List<RepaymentSchedule> repaymentList = new ArrayList<>();
            try {
                Optional<ApiExecutionLog> apiExecutionRecordOpt = apiExecutionLogRepository.findTopByApplicationIdAndApiNameOrderByCreateTsDesc(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE);
                if (apiExecutionRecordOpt.isPresent()) {
                    ApiExecutionLog apiExecutionRecord = apiExecutionRecordOpt.get();
                    if (apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS) || apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS_DMS)) {
                        String apiRespJsonString = apiExecutionRecord.getResponsePayload();
                        logger.debug("apiRespJsonString " + apiRespJsonString);
                        Gson gson1 = new Gson();

                        // Parse the string into a JsonObject
                        JsonObject root = gson1.fromJson(apiRespJsonString, JsonObject.class);

                        // Extract the "body" array
                        JsonArray bodyArray = root.getAsJsonArray("body");

                        // Convert to list of RepaymentSchedule
                        Type listType = new TypeToken<List<RepaymentSchedule>>() {
                        }.getType();
                        repaymentList = gson1.fromJson(bodyArray, listType);
                        logger.debug("repaymentList size." + repaymentList.size());
                    } else {
                        logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR);
                        throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR);
                    }
                } else {
                    logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                    throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                }
            } catch (Exception e) {
                logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR2 + e);
                throw new IOException("Error fetching ApiExecutionLog - generateSanctionLetter data", e);
            }

            logger.debug("Reading JSON content from server for file: {}", jsonKeyFileName);
            fileContent = new JSONObject(
                    adapterUtil.readJSONContentFromServer(JsonKeyFolders.KFS.getValue() + "/" + jsonKeyFileName));
            logger.debug("File content retrieved: {}", fileContent);
            keysForContent = fileContent.getJSONObject(Constants.KEYS_FOR_CONTENT);
            logger.debug("Keys for content extracted: {}", keysForContent);

            String sanctionDate2 = "";
            if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
                    .equals(custmrDataFields.getApplicationMaster().getProductCode())
                    || Constants.UNNATI_EMERGENCY_PRODUCT_CODE
                    .equals(custmrDataFields.getApplicationMaster().getProductCode())
                    || Constants.FAMILY_WELFARE_PRODUCT_CODE
                    .equals(custmrDataFields.getApplicationMaster().getProductCode())){
                sanctionDate2 = leadInitiationDateStr;
            }

            byte[] kfsByteArray = new KfsReport().generatePdfForDbKit(keysForContent, custmrDataFields, kfsFilePath, productCode, productName, repaymentList, sanctionDate2);
            String kfsBase64 = Base64.getEncoder().encodeToString(kfsByteArray);
            //			kfsBase64 = new KfsReport().generatePdfForDbKit(applicationMasterData,
//					custmrDataFields, keysForContent, kfsFilePath,language, kmId, bmId, kmSubmDateStr, bmSubmDateStr, usernameKM, usernameBM);
            // @author Abhishek.Raj.CAG
//			if (
//					Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
//							|| Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
//							|| Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
//			) {
//				loanApplicationBase64 = new LoanApplication().generateLoanApplicationPdfForDBKitAdd(applicationMasterData,
//						custmrDataFields, keysForContent, kfsFilePath,language, kmId, bmId, kmSubmDateStr, bmSubmDateStr, usernameKM, usernameBM);
//			} else{
//				loanApplicationBase64 = new LoanApplication().generateLoanApplicationPdfForDBKit(applicationMasterData,
//						custmrDataFields, keysForContent, kfsFilePath,language, kmId, bmId, kmSubmDateStr, bmSubmDateStr, usernameKM, usernameBM);
//			}
            //----END----
            //            String loanApplicationBase64 = Base64.getEncoder().encodeToString(loanApplicationByte);
            kfsJson.addProperty(Constants.ORDER, 1);
            kfsJson.addProperty(Constants.BASE64, kfsBase64);
            kfsJson.addProperty("docType", Constants.KFS);
            logger.debug("Generated KFS PDF successfully for applicationId: {}", applicationId);


            File document = new File(kfsFilePath);
            String docSize = String.valueOf(document.length() / 1024.0);
            kfsJson.addProperty("size", docSize);

            // language condition
            String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
            logger.debug("inputLanguage" + language);
            boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                    .anyMatch(lang -> lang.equalsIgnoreCase(language));
            if (!isValidLanguage) {
                if (!regenerate) {
                    long deletedCount = documentsRepository.deleteByApplicationIdAndDocTypeAndUploadType(applicationId, Constants.KFS, Constants.MANUAL);

                    if (deletedCount == 0) {
                        logger.debug("No records found to delete for applicationId: {}, documentType: {}, uploadType: {}",
                                applicationId, Constants.KFS, Constants.MANUAL);
                    } else {
                        logger.debug("Deleted {} records for applicationId: {}, documentType: {}, uploadType: {}", deletedCount, applicationId, Constants.KFS, Constants.MANUAL);
                    }

                    Optional<Documents> documentRecordOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(applicationId,
                            Constants.KFS, Constants.GENERATED);
                    if (documentRecordOpt.isPresent()) {
                        Documents documentRecord = documentRecordOpt.get();
                        documentRecord.setApplicationId(applicationId);
                        documentRecord.setDocName(fileName);
                        documentRecord.setAppId(appId);
                        documentRecord.setDocType(Constants.KFS);
                        documentRecord.setDocStatus(Constants.PENDING);
                        documentRecord.setUploadType(Constants.GENERATED);
                        documentRecord.setCreatedBy(userId);
                        documentRecord.setCreateTs(Timestamp.valueOf(LocalDateTime.now()));
                        documentRecord.setProductType(productCode);
                        documentRecord.setLanguage(language);
                        documentRecord.setDocSize(docSize);
                        documentsRepository.save(documentRecord);
                    } else {
                        Documents documentRecord = new Documents();
                        documentRecord.setApplicationId(applicationId);
                        documentRecord.setDocName(fileName);
                        documentRecord.setAppId(appId);
                        documentRecord.setDocType(Constants.KFS);
                        documentRecord.setDocStatus(Constants.PENDING);
                        documentRecord.setUploadType(Constants.GENERATED);
                        documentRecord.setCreatedBy(userId);
                        documentRecord.setProductType(productCode);
                        documentRecord.setLanguage(language);
                        documentRecord.setDocSize(docSize);
                        documentsRepository.save(documentRecord);
                    }
                }
            }
        } else {
            logger.error("Application not found for applicationId: {}", applicationId);
        }
        logger.debug("Exiting generateKFS for applicationId: {}", applicationId);
        return kfsJson;
    }

    private JsonObject generateKfsSummary(String appId, String applicationId, int versionNum, String filePathGen,
                                          String language, String userId, String productCode, boolean regenerate) throws DRException, IOException, JRException {
        logger.debug(
                "Entering generateKfsSummary with appId: {}, applicationId: {}, versionNum: {}, filePathGen: {}, language: {}",
                appId, applicationId, versionNum, filePathGen, language);
        String jsonKeyFileName;
        JSONObject keysForContent = new JSONObject();
        JSONObject fileContent = new JSONObject();
        CustomerDataFields custmrDataFields = null;
        JsonObject kfsJsonSummary = new JsonObject();
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, versionNum);

        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            logger.debug("ApplicationMaster data found for applicationId: {}", applicationId);
            custmrDataFields = getCustomerData(applicationMasterData, applicationId, appId, versionNum);
            logger.debug("CustomerDataFields retrieved: {}", custmrDataFields);

            List<ApplicationWorkflow> workflow;
            workflow = applnWfRepository.findByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumAsc(appId,
                    applicationId, versionNum);
            logger.debug("Workflow details {} ", workflow);


            String bmId = "-";
            String kmId = "-";

            String kmSubmDateStr = "";
            String bmSubmDateStr = "";

            String product = applicationMasterData.getProductCode();
            Optional<ProductDetails> productDetails = productDetailsrepository.findById(product);

            String productName = "";
            if (productDetails.isPresent()) {
                logger.debug("productName data found.");
                logger.debug("Loan product Name:" + productDetails.get().getProductName());
                productName = productDetails.get().getProductName();
            }

            if (!workflow.isEmpty()) {
                custmrDataFields.setApplicationWorkflowList(workflow);


                LocalDateTime kmSubmDate = null;
                for (ApplicationWorkflow applnWorkflow : custmrDataFields.getApplicationWorkflowList()) {
                    if (Constants.INITIATOR.equalsIgnoreCase(applnWorkflow.getCurrentRole()) && (WorkflowStatus.APPROVED.getValue().equalsIgnoreCase(
                            applnWorkflow.getApplicationStatus()
                    ) || WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(applnWorkflow.getApplicationStatus()))) {
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

                if (Constants.UNNATI_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.OPENMARKET_LOAN_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        // @author Abhishek.Raj.CAG
                        || Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.UNNATI_RESTART_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.UNNATI_EMERGENCY_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                        || Constants.FAMILY_WELFARE_PRODUCT_CODE
                        .equals(custmrDataFields.getApplicationMaster().getProductCode())
                    //----END----
                ) {
                    logger.info("Unnati application");
                    String previousWorkflowStatus = null;
                    for (ApplicationWorkflow appnWorkflow : custmrDataFields.getApplicationWorkflowList()) {
                        String currentStatus = appnWorkflow.getApplicationStatus();
                        if (Constants.APPROVED.equalsIgnoreCase(appnWorkflow.getApplicationStatus())) {
                            if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(previousWorkflowStatus)) {
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
            }

            //2.
            Optional<String> usernameOptKM = tbUserRepository.findUserNameByUserId(kmId);
            String usernameKM = usernameOptKM.orElse("-");
            logger.info("usernameKM : " + usernameKM);

            Optional<String> usernameOptBM = tbUserRepository.findUserNameByUserId(bmId);
            String usernameBM = usernameOptBM.orElse("-");
            logger.info("usernameBM : " + usernameBM);


            String fileName = applicationId + "_" + Constants.KFS_SUMMARY + ".pdf";
            String kfsSummaryFilePath = filePathGen + fileName;
            logger.debug("KFS Summary file path: {}", kfsSummaryFilePath);

            jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "KfsSummaryReport.json";

            // @author Abhishek.Raj.CAG
//			if (
//					Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
//							|| Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
//							|| Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
//			) {
//				jsonKeyFileName = Constants.REPORT_JSONKEYS
//						+ language
//						+ "LoanApplicationAdd.json";
//			} else{
//				jsonKeyFileName = Constants.REPORT_JSONKEYS + language + "LoanApplication.json";
//			}
            //----END----

            List<RepaymentSchedule> repaymentList = new ArrayList<>();
            try {
                Optional<ApiExecutionLog> apiExecutionRecordOpt = apiExecutionLogRepository.findTopByApplicationIdAndApiNameOrderByCreateTsDesc(applicationId, Constants.LOAN_REPAYMENT_SCHEDULE);
                if (apiExecutionRecordOpt.isPresent()) {
                    ApiExecutionLog apiExecutionRecord = apiExecutionRecordOpt.get();
                    if (apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS) || apiExecutionRecord.getApiStatus().equalsIgnoreCase(Constants.SUCCESS_DMS)) {
                        String apiRespJsonString = apiExecutionRecord.getResponsePayload();
                        logger.debug("apiRespJsonString " + apiRespJsonString);
                        Gson gson1 = new Gson();

                        // Parse the string into a JsonObject
                        JsonObject root = gson1.fromJson(apiRespJsonString, JsonObject.class);

                        // Extract the "body" array
                        JsonArray bodyArray = root.getAsJsonArray("body");

                        // Convert to list of RepaymentSchedule
                        Type listType = new TypeToken<List<RepaymentSchedule>>() {
                        }.getType();
                        repaymentList = gson1.fromJson(bodyArray, listType);
                        logger.debug("repaymentList size." + repaymentList.size());
                    } else {
                        logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR);
                        throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR);
                    }
                } else {
                    logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                    throw new IOException(Constants.LOAN_REPAY_SCHEDULE_ERR1);
                }
            } catch (Exception e) {
                logger.error(Constants.LOAN_REPAY_SCHEDULE_ERR2 + e);
                throw new IOException("Error fetching ApiExecutionLog - generateKfsSummary data", e);
            }

            logger.debug("Reading JSON content from server for file: {}", jsonKeyFileName);
            fileContent = new JSONObject(
                    adapterUtil.readJSONContentFromServer(JsonKeyFolders.KFS_SUMMARY.getValue() + "/" + jsonKeyFileName));
            logger.debug("File content retrieved: {}", fileContent);
            keysForContent = fileContent.getJSONObject(Constants.KEYS_FOR_CONTENT);
            logger.debug("Keys for content extracted: {}", keysForContent);





            byte[] kfsSummaryByteArray = new KfsReport().generatePdfForDbKitKfsSummary(applicationMasterData, keysForContent, custmrDataFields, kfsSummaryFilePath, productCode, productName, repaymentList, "");
            String kfsSummaryBase64 = Base64.getEncoder().encodeToString(kfsSummaryByteArray);

            kfsJsonSummary.addProperty(Constants.ORDER, 1);
            kfsJsonSummary.addProperty(Constants.BASE64, kfsSummaryBase64);
            kfsJsonSummary.addProperty("docType", Constants.KFS_SUMMARY);
            logger.debug("Generated KFS Summary PDF successfully for applicationId: {}", applicationId);


            File document = new File(kfsSummaryFilePath);
            String docSize = String.valueOf(document.length() / 1024.0);
            kfsJsonSummary.addProperty("size", docSize);

            // language condition
            String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
            logger.debug("inputLanguage" + language);
            boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                    .anyMatch(lang -> lang.equalsIgnoreCase(language));
            if (!isValidLanguage) {
                if (!regenerate) {
                    long deletedCount = documentsRepository.deleteByApplicationIdAndDocTypeAndUploadType(applicationId, Constants.KFS_SUMMARY, Constants.MANUAL);

                    if (deletedCount == 0) {
                        logger.debug("No records found to delete for applicationId: {}, documentType: {}, uploadType: {}",
                                applicationId, Constants.KFS_SUMMARY, Constants.MANUAL);
                    } else {
                        logger.debug("Deleted {} records for applicationId: {}, documentType: {}, uploadType: {}", deletedCount, applicationId, Constants.KFS_SUMMARY, Constants.MANUAL);
                    }

                    Optional<Documents> documentRecordOpt = documentsRepository.findByApplicationIdAndDocTypeAndUploadType(applicationId,
                            Constants.KFS_SUMMARY, Constants.GENERATED);
                    if (documentRecordOpt.isPresent()) {
                        Documents documentRecord = documentRecordOpt.get();
                        documentRecord.setApplicationId(applicationId);
                        documentRecord.setDocName(fileName);
                        documentRecord.setAppId(appId);
                        documentRecord.setDocType(Constants.KFS_SUMMARY);
                        documentRecord.setDocStatus(Constants.PENDING);
                        documentRecord.setUploadType(Constants.GENERATED);
                        documentRecord.setCreatedBy(userId);
                        documentRecord.setCreateTs(Timestamp.valueOf(LocalDateTime.now()));
                        documentRecord.setProductType(productCode);
                        documentRecord.setLanguage(language);
                        documentRecord.setDocSize(docSize);
                        documentsRepository.save(documentRecord);
                    } else {
                        Documents documentRecord = new Documents();
                        documentRecord.setApplicationId(applicationId);
                        documentRecord.setDocName(fileName);
                        documentRecord.setAppId(appId);
                        documentRecord.setDocType(Constants.KFS_SUMMARY);
                        documentRecord.setDocStatus(Constants.PENDING);
                        documentRecord.setUploadType(Constants.GENERATED);
                        documentRecord.setCreatedBy(userId);
                        documentRecord.setProductType(productCode);
                        documentRecord.setLanguage(language);
                        documentRecord.setDocSize(docSize);
                        documentsRepository.save(documentRecord);
                    }
                }
            }
        } else {
            logger.error("Application not found for applicationId: {}", applicationId);
        }
        logger.debug("Exiting generateKFS for applicationId: {}", applicationId);
        return kfsJsonSummary;
    }
    //----END----

    // -- ALL FALLBACK METHODS
    public Response dbKitDocGenerationAndDownloadFallback(UploadDocumentRequestFields requestObj, Exception e) throws IOException {
        logger.error("dbKitDocGenerationAndDownload error : request-{}, error -{}", requestObj, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Mono<Response> createApplicationInDemoModeFallback(CreateModifyUserRequest request, Exception e) {
        logger.error("createApplicationInDemoModeFallback error : ", request, e);
        return FallbackUtils.genericFallbackMono();
    }

    private boolean isValidStageFallback(CreateModifyUserRequest request, boolean isSelfOnBoardingHeaderAppId,
                                         JSONArray array, Exception e) {
        logger.error("isValidStageFallback error : ", request, isSelfOnBoardingHeaderAppId, array, e);
        return false;
    }

    private boolean isVaptPassedForScreenElementsFallback(CreateModifyUserRequest request,
                                                          boolean isSelfOnBoardingHeaderAppId, JSONArray array, Exception e) {
        logger.error("isVaptPassedForScreenElementsFallback error : ", request, isSelfOnBoardingHeaderAppId, array, e);
        return false;
    }

    private Mono<Response> createApplicationFallback(CreateModifyUserRequest createUserRequest,
                                                     boolean isSelfOnBoardingAppId, Properties prop, boolean isSelfOnBoardingHeaderAppId, Header header,
                                                     JSONArray array, Exception e) {
        logger.error("createApplicationFallback error : ", createUserRequest, isSelfOnBoardingAppId, prop,
                isSelfOnBoardingHeaderAppId, header, array, e);
        return FallbackUtils.genericFallbackMono();
    }

    private Mono<Response> updateRelatedApplnIdDetailsFallback(CreateModifyUserRequest request, Mono<Response> response,
                                                               String appId, boolean isSelfOnBoardingHeaderAppId, Exception e) {
        logger.error("updateRelatedApplnIdDetailsFallback error : ", request, response, appId, e);
        return FallbackUtils.genericFallbackMono();
    }

    private RoleAccessMap fetchRoleAccessMapObjFallback(String appId, String roleId, Exception e) {
        logger.error("fetchRoleAccessMapObjFallback error : ", appId, roleId, e);
        return null;
    }

    private Response fetchApplicationFallback(FetchDeleteUserRequest fetchUserDetailsRequest, String src,
                                              boolean isSelfOnBoardingAppId, Properties prop, Exception e) {
        logger.error("fetchApplicationFallback error : ", fetchUserDetailsRequest, src, isSelfOnBoardingAppId, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response fetchCountriesFallback(Request request, Exception e) {
        logger.error("fetchCountriesFallback error : ", request, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response fetchStatesFallback(Request request, Exception e) {
        logger.error("fetchStatesFallback error : ", request, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response fetchCitiesFallback(FetchCitiesRequest fetchCitiesRequest, Exception e) {
        logger.error("fetchCitiesFallback error : ", fetchCitiesRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response deleteNomineeFallback(DeleteNomineeRequest deleteNomineeRequest, Exception e) {
        logger.error("deleteNomineeFallback error : ", deleteNomineeRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response fetchLovMasterFallback(Request request, Exception e) {
        logger.error("fetchLovMasterFallback error : ", request, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response fetchNomineeFallback(FetchNomineeRequest fetchNomineeRequest, Exception e) {
        logger.error("fetchNomineeFallback error : ", fetchNomineeRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Mono<Response> checkApplicationFallback(CheckApplicationRequest request, Header header, Exception e) {
        logger.error("checkApplicationFallback error : ", request, header, e);
        return FallbackUtils.genericFallbackMono();
    }

    private Response deleteDocumentFallback(DeleteDocumentRequest deleteDocumentRequest, Exception e) {
        logger.error("deleteDocumentFallback error : ", deleteDocumentRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response downloadReportFallback(DownloadReportRequest downloadDocumentRequest,boolean isSmsReport, Exception e) {
        logger.error("downloadDocument error : ", downloadDocumentRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

//	public Mono<Response> downloadReportFallback(DownloadReportRequest request, Throwable t) {
//	    logger.error("Fallback triggered: {}", t.getMessage(), t);
//	    return Mono.just(new Response("Fallback triggered due to service failure", null));
//	}

    private boolean discardApplicationFallback(CreateModifyUserRequest createModifyUserRequest, Exception e) {
        logger.error("discardApplicationFallback error : ", createModifyUserRequest, e);
        return false;
    }

    private String fetchPropertyFromOcrResponseFallback(Response response, String nationalIdKey,
                                                        ExtractOcrDataRequest request, Properties prop, Exception e) {
        logger.error("fetchPropertyFromOcrResponseFallback error : ", response, nationalIdKey, request, prop, e);
        return null;
    }

    private Mono<Response> extractOcrDataFallback(ExtractOcrDataRequest request, Header header, Exception e) {
        logger.error("extractOcrDataFallback error : ", request, header, e);
        return FallbackUtils.genericFallbackMono();
    }

    private Mono<Response> uploadDocumentFallback(UploadDocumentRequest request, String nationalId, Header header,
                                                  boolean isSelfOnBoardingHeaderAppId, Properties prop, Exception e) {
        logger.error("uploadDocumentFallback error : ", request, nationalId, header, isSelfOnBoardingHeaderAppId, prop,
                e);
        return FallbackUtils.genericFallbackMono();
    }

    private Response fetchBranchesFallback(FetchBranchesRequest request, Exception e) {
        logger.error("fetchBranchesFallback error : ", request, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response fetchLitByLanguageFallback(FetchLitByLanguageRequest request, Exception e) {
        logger.error("fetchLitByLanguageFallback error : ", request, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response downloadApplicationFallback(FetchDeleteUserRequest fetchDeleteUserRequest, Exception e) {
        logger.error("downloadApplicationFallback error : ", fetchDeleteUserRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response updateLitFileFallback(UpdateLitFileRequest updateLitFileRequest, Exception e) {
        logger.error("updateLitFileFallback error : ", updateLitFileRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Mono<Object> fetchBanksFallback(FetchBanksRequest apiRequest, Header header, Exception e) {
        logger.error("fetchBanksFallback error : ", apiRequest, header, e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Response updateLovFallback(UpdateLovRequest apiRequest, Exception e) {
        logger.error("updateLovFallback error : ", apiRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response updateApplicantsCountFallback(UpdateApplicantsCountRequest apiRequest, Exception e) {
        logger.error("updateApplicantsCountFallback error : ", apiRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response createRoleFallback(CreateRoleRequest apiRequest, Exception e) {
        logger.error("createRoleFallback error : ", apiRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response fetchRoleFallback(FetchRoleRequest apiRequest, Properties prop, IOException e) {
        logger.error("fetchRoleFallback IOException error : ", apiRequest, prop, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response fetchRPCDataFallback(FetchRoleRequest apiRequest, Properties prop, IOException e) {
        logger.error("fetchRPCDataFallback Exception error : ", apiRequest, prop, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response fetchRoleFallback(FetchRoleRequest apiRequest, Properties prop, Exception e) {
        logger.error("fetchRoleFallback error : ", apiRequest, prop, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response fetchDashboardFallback(FetchRoleRequest apiRequest, Properties prop, IOException e) {
        logger.error("fetchDashboardFallback IOException error : ", apiRequest, prop, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response fetchTATReportFallback(FetchTATReportRequest apiRequest, Properties prop, IOException e) {
        logger.error("fetchTATReportFallback IOException error : ", apiRequest, prop, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response fetchStateMasterFallback(IOException e) {
        logger.error("fetchStateMasterFallback IOException error : ", e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response deleteRoleFallback(FetchRoleRequest apiRequest, Exception e) {
        logger.error("deleteRoleFallback error : ", apiRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response searchApplicationsFallback(SearchAppRequest apiRequest, Exception e) {
        logger.error("searchApplicationsFallback error : ", apiRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response assignApplicationFallback(AssignApplicationRequest apiRequest, Exception e) {
        logger.error("searchApplicationsFallback error : ", apiRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private void updateStatusInMasterFallback(PopulateapplnWFRequest apiRequest, Exception e) {
        logger.error("updateStatusInMasterFallback error : ", apiRequest, e);
    }

    private Response viewAllRecordsFallback(ViewAllRecordsRequest apiRequest, Properties prop, Exception e) {
        logger.error("viewAllRecordsFallback error : ", apiRequest, prop, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response statusReportFallback(StatusReportRequest apiRequest, Properties prop, Exception e) {
        logger.error("statusReportFallback error : ", apiRequest, prop, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response rejectHistoryFallback(PopulateRejectedDataRequest apiRequest, Exception e) {
        logger.error("rejectHistoryFallback error : ", apiRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response advanceSearchApplicationsFallback(AdvanceSearchAppRequest apiRequest, Exception e) {
        logger.error("advanceSearchApplicationsFallback error : ", apiRequest, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    private Response generateReportFallback(CustomerDataFields customerDataFields, Exception e) {
        logger.error("generateReportFallback error : ", customerDataFields, e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

    /**
     * @Ankit.CAG Transfered Controller Logic
     */
    public ResponseEntity<ResponseWrapper> serviceFetchApplication(FetchDeleteUserRequest req) {
        Response fetchUserDetailsResponse = new Response();
        ResponseWrapper fetchUserDetailsResponseWrapper = new ResponseWrapper();
        FetchDeleteUserFields reqFields = req.getRequestObj();
        String applicationId = reqFields.getApplicationId();
        String customerId = reqFields.getCustomerId();
        String productId = reqFields.getProductId();


        try {
            logger.debug("serviceFetchApplication: {}" , reqFields);

            if ("Y".equalsIgnoreCase(reqFields.getCdhFlag())) {
                //if CdhFlag is Y, then the application has comethrough deeplinking
                String cdhProductCode = ProductCode.getCdhCodeByUnnatiCode(productId);
                if(productId.equalsIgnoreCase(ProductCode.OPEN_MARKET.getUnnatiCode())){
                    cdhProductCode = productId;
                }
                List<String> unnatiStatusList = new ArrayList<>();
                unnatiStatusList.add(AppStatus.EXIT.getCdhStatus());
                unnatiStatusList.add(AppStatus.REJECTED.getCdhStatus());
                unnatiStatusList.add(AppStatus.DISBURSED.getValue());
                unnatiStatusList.add(AppStatus.LUC.getValue());
                unnatiStatusList.add(AppStatus.PENDINGLUCVERIFICATION.getValue());
                unnatiStatusList.add(AppStatus.LUCVERIFIED.getValue());

                List<String> deeplinkAllowedSources = new ArrayList<>();
                deeplinkAllowedSources.add(Constants.MAITRI_SOURCE);

                logger.debug("Fetching UnnatiIexceedCDHLead for customerId: {} and cdhProductCode: {}", customerId, cdhProductCode);
                Optional<UnnatiIexceedCDHLead> optLeadsbyCustIdAndPrId = unnatiIexceedCDHLeadRepo
                        .findByCustomerIdAndProductAndUnnatiStatusNotInAndSourceOfTheApplicationIn(customerId, cdhProductCode, unnatiStatusList, deeplinkAllowedSources);

                if (!optLeadsbyCustIdAndPrId.isPresent()) {
                    Response fetchCdhLeadsDetailsResponse = new Response();
                    ResponseHeader responseHeader = new ResponseHeader();
                    ResponseBody responseBody = new ResponseBody();

                    logger.debug("UnnatiIexceedCDHLead Not Found");
                    responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                    responseBody.setResponseObj(Constants.NO_RECORDS_FOUND);
                    fetchCdhLeadsDetailsResponse.setResponseHeader(responseHeader);
                    fetchCdhLeadsDetailsResponse.setResponseBody(responseBody);

                    fetchUserDetailsResponseWrapper.setApiResponse(fetchCdhLeadsDetailsResponse);
                    return new ResponseEntity<>(fetchUserDetailsResponseWrapper, HttpStatus.BAD_REQUEST);
                }

                UnnatiIexceedCDHLead unnatiIexceedCDHLead = optLeadsbyCustIdAndPrId.get();
                logger.debug("CDH Flag: {}", reqFields.getCdhFlag());
                logger.debug("Customer ID: {}", reqFields.getCustomerId());
                logger.debug("Product ID: {}", reqFields.getProductId());

                List<ApplicationMaster> applicationMasterOpt = applicationMasterRepository
                        .findBySearchCode2IgnoreCaseAndProductCodeAndWorkitemNo(reqFields.getCustomerId(),
                                reqFields.getProductId(), unnatiIexceedCDHLead.getReferenceId());
                logger.debug("ApplicationMaster result size: {}", applicationMasterOpt.size());
                logger.debug("ApplicationMaster data: {}", applicationMasterOpt);
                if (applicationMasterOpt.isEmpty()) {
                    logger.debug("ApplicationMaster data NOT FOUND");
                    logger.debug("Calling getCdhLeads with customerId: {} and productId: {}",
                            reqFields.getCustomerId(),
                            reqFields.getProductId());
                    try {
                        Response cdhLeadsResponse = getCdhLeads(reqFields.getProductId(),
                                reqFields.getCustomerId(), unnatiIexceedCDHLead);
                        // If Application Master Lead is present, continue with the existing flow
                        logger.debug("getCdhLeads response: {}", cdhLeadsResponse);
                        if (!cdhLeadsResponse.getResponseBody().getResponseObj().equals(Constants.PRESENT)) {
                            logger.debug("ApplicationMaster customer Data PRESENT:");
                            fetchUserDetailsResponseWrapper.setApiResponse(cdhLeadsResponse);
                            return new ResponseEntity<>(fetchUserDetailsResponseWrapper, HttpStatus.OK);
                        }
                    } catch (Exception e) {
                        logger.debug("Exception:", e.getStackTrace().toString());
                        return new ResponseEntity<>(fetchUserDetailsResponseWrapper, HttpStatus.BAD_REQUEST);
                    }
                } else {
                   // applicationId = applicationMasterOpt.get(1).getApplicationId();
                    applicationId = applicationMasterOpt.get(0).getApplicationId();
                }
            }
            logger.debug("fetchapplication Existing Flow");
            // ----------END-----------
            boolean isSelfOnBoardingAppId = false;
            Properties prop = null;
            try {
                prop = CommonUtils.readPropertyFile();
            } catch (IOException e) {
                logger.error("Error while reading property file in fetchApplication ", e);
                fetchUserDetailsResponse = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
                        ResponseCodes.FAILURE.getKey());
            }
            if (null != prop) {
                if (reqFields.getAppId()
                        .equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
                    isSelfOnBoardingAppId = true;
                }
                req.getRequestObj().setApplicationId(applicationId);
                logger.debug("fetchapplication calling" + req + " isSelfOnBoardingAppId:" + isSelfOnBoardingAppId);
                fetchUserDetailsResponse = fetchApplication(req, "fetchapplication", isSelfOnBoardingAppId, prop);
            } else
                logger.debug("prop nul");

            fetchUserDetailsResponseWrapper.setApiResponse(fetchUserDetailsResponse);
            logger.warn("End : fetchApplication method response is:: " + fetchUserDetailsResponseWrapper.toString());
            return new ResponseEntity<>(fetchUserDetailsResponseWrapper, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in serviceFetchApplication: ", e);
            return new ResponseEntity<>(fetchUserDetailsResponseWrapper, HttpStatus.BAD_REQUEST);
        }
    }


}
