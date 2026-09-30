package com.iexceed.appzillonbanking.cob.core.services;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.core.repository.ab.*;
import com.iexceed.appzillonbanking.cob.core.utils.*;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedCDHLead;
import com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest;
import com.iexceed.appzillonbanking.cob.payload.SendSmsAndEmailApiRequest;
import com.iexceed.appzillonbanking.cob.payload.SendSmsAndEmailRequestObject;
import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiIexceedCDHLeadRepo;
import com.iexceed.appzillonbanking.cob.service.SendSmsAndEmailService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.apz.UserRole;
import com.iexceed.appzillonbanking.cob.core.repository.apz.UserRoleRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

@Service
public class CommonParamService {

    private static final Logger logger = LogManager.getLogger(CommonParamService.class);

    @Autowired
    private TbAbmiCommonCodeRepository tbAbmiCommonCodeRepository;

    @Autowired
    private ProductGroupRepository productGroupRepository;

    @Autowired
    private ProductDetailsrepository productDetailsrepository;

    @Autowired
    private ApplicationMasterRepository appMasterRepo;

    @Autowired
    private CustomerDetailsRepository custDtlRepo;

    @Autowired
    private DepositDtlsRepo depoDtlRepo;

    @Autowired
    private LoanDtlsRepo loanDtlsRepo;

    @Autowired
    private ApplicationWorkflowRepository applnWfRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private NomineeDetailsRepository nomineeDetailsRepository;

    @Autowired
    private OccupationDetailsRepository occupationDetailsRepository;

    @Autowired
    private FaqDetailsRepository faqRepository;

    @Autowired
    private ApplicationDocumentsRepository applicationDocumentsRepository;

    @Autowired
    private DeviationRATrackerRepository deviationRATrackerRepository;

    @Autowired
    private ApplicationMasterRepository applicationMasterRepository;

    @Autowired
    private SendSmsAndEmailService sendSmsAndEmailService;

    @Autowired
    private CustomerDetailsRepository customerDetailsRepository;

    @Autowired
    private UnnatiIexceedCDHLeadRepo unnatiIexceedCDHLeadRepo;

    @Autowired
    private ApplicationWorkflowRepository applicationWorkflowRepository;

    @Autowired
    private CibilDetailsRepository cibilDetailsRepository;

    public Response fetchAllData(CommonParamRequest commonRequestParam) throws IOException {
        Gson gson = new Gson();
        CommonParamResponse commonParamResponseOBJ;
        Response commonParamResponse = new Response();
        List<CommonParamResponse> commonParamResponseList = null;

        String accessType = commonRequestParam.getRequestObj().getAccessType();
        logger.debug("Access Type :" + accessType);

        String code = commonRequestParam.getRequestObj().getCode();
        logger.debug("Code:" + code);

        if (accessType.isEmpty() && code.isEmpty()) {
            logger.debug("COB Fetching all the common codes from DB");
            Iterable<TbAbmiCommonCodeDomain> commonParam = tbAbmiCommonCodeRepository.findAll();
            commonParamResponseList = generateResponseWrapper(commonParam);
        } else if (!accessType.isEmpty() && !code.isEmpty()) {
            logger.debug("COB Fetching based on accessType and code from DB");
            Iterable<TbAbmiCommonCodeDomain> commonParam = tbAbmiCommonCodeRepository.findAllByCodeAndAccessType(code,
                    accessType);
            commonParamResponseList = generateResponseWrapper(commonParam);
        } else {
            if (!accessType.isEmpty()) {
                logger.debug("COB Fetching based on accessType only from DB.");
                Iterable<TbAbmiCommonCodeDomain> commonParam = tbAbmiCommonCodeRepository
                        .findAllByAccessType(accessType);
                commonParamResponseList = generateResponseWrapper(commonParam);
            } else if (!code.isEmpty()) {
                logger.debug("COB Fetching based on code only from DB.");
                Iterable<TbAbmiCommonCodeDomain> commonParam = tbAbmiCommonCodeRepository.findAllByCode(code);
                commonParamResponseList = generateResponseWrapper(commonParam);
            }
        }
        Properties prop = new Properties();
        try (FileReader fileReader = new FileReader(
                CommonUtils.getCommonProperties(SpringCloudProperties.PROP_FILE_PATH.getKey()))) {
            prop.load(fileReader);
        } catch (IOException ex) {
            logger.error("Exception in fetchAllData ", ex);
            throw ex;
        }
        if (commonParamResponseList != null) {
            LocalDate today = LocalDate.now();
            commonParamResponseOBJ = new CommonParamResponse();
            commonParamResponseOBJ.setCodeType("COMM");
            commonParamResponseOBJ.setParamName("allowPartiallyFilledApplication");
            commonParamResponseOBJ
                    .setParamValue(prop.getProperty(CobFlagsProperties.ALLOW_PARTIAL_APPLICATION.getKey()));
            commonParamResponseList.add(commonParamResponseOBJ);

            commonParamResponseOBJ = new CommonParamResponse();
            commonParamResponseOBJ.setCodeType("COMM");
            commonParamResponseOBJ.setParamName("demoMode");
            commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.DEMO_MODE.getKey()));
            commonParamResponseList.add(commonParamResponseOBJ);

            commonParamResponseOBJ = new CommonParamResponse();
            commonParamResponseOBJ.setCodeType("COMM");
            commonParamResponseOBJ.setParamName("accountSTP");
            commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.ACCOUNT_STP.getKey()));
            commonParamResponseList.add(commonParamResponseOBJ);

            commonParamResponseOBJ = new CommonParamResponse();
            commonParamResponseOBJ.setCodeType("COMM");
            commonParamResponseOBJ.setParamName("depositSTP");
            commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.DEPOSIT_STP.getKey()));
            commonParamResponseList.add(commonParamResponseOBJ);

            commonParamResponseOBJ = new CommonParamResponse();
            commonParamResponseOBJ.setCodeType("COMM");
            commonParamResponseOBJ.setParamName("cardSTP");
            commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.CARD_STP.getKey()));
            commonParamResponseList.add(commonParamResponseOBJ);

            commonParamResponseOBJ = new CommonParamResponse();
            commonParamResponseOBJ.setCodeType("COMM");
            commonParamResponseOBJ.setParamName("loanSTP");
            commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.LOAN_STP.getKey()));
            commonParamResponseList.add(commonParamResponseOBJ);

            commonParamResponseOBJ = new CommonParamResponse();
            commonParamResponseOBJ.setCodeType("COMM");
            commonParamResponseOBJ.setParamName("defaultCasaProductGrpCode");
            commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.DEFAULT_CASA_GRP.getKey()));
            commonParamResponseList.add(commonParamResponseOBJ);

            commonParamResponseOBJ = new CommonParamResponse();
            commonParamResponseOBJ.setCodeType("COMM");
            commonParamResponseOBJ.setParamName("defaultCasaProductCode");
            String defaultCasaProductCode = prop.getProperty(CobFlagsProperties.DEFAULT_CASA_PRODUCT.getKey());
            commonParamResponseOBJ.setParamValue(defaultCasaProductCode);
            commonParamResponseList.add(commonParamResponseOBJ);

            commonParamResponseOBJ = new CommonParamResponse();
            commonParamResponseOBJ.setCodeType("COMM");
            commonParamResponseOBJ.setParamName("depCasaJtAcc");
            commonParamResponseOBJ.setParamValue(getJtAccDtls(defaultCasaProductCode, today));
            commonParamResponseList.add(commonParamResponseOBJ);

            commonParamResponseOBJ = new CommonParamResponse();
            commonParamResponseOBJ.setCodeType("COMM");
            commonParamResponseOBJ.setParamName("defaultCasaProductCodeLN");
            String defaultCasaProductCodeLN = prop.getProperty(CobFlagsProperties.DEFAULT_CASA_PRODUCTLN.getKey());
            commonParamResponseOBJ.setParamValue(defaultCasaProductCodeLN);
            commonParamResponseList.add(commonParamResponseOBJ);

            commonParamResponseOBJ = new CommonParamResponse();
            commonParamResponseOBJ.setCodeType("COMM");
            commonParamResponseOBJ.setParamName("loanCasaJtAcc");
            commonParamResponseOBJ.setParamValue(getJtAccDtls(defaultCasaProductCodeLN, today));
            commonParamResponseList.add(commonParamResponseOBJ);

            commonParamResponseOBJ = new CommonParamResponse();
            commonParamResponseOBJ.setCodeType("COMM");
            commonParamResponseOBJ.setParamName("defaultCardlocation");
            commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.DEFAULT_CARD_LOCATION.getKey()));
            commonParamResponseList.add(commonParamResponseOBJ);

        }
        ResponseHeader commonParamRespHeader = new ResponseHeader();
        ResponseBody commonParamRespBody = new ResponseBody();

        if (commonParamResponseList == null || commonParamResponseList.isEmpty()) {
            logger.debug("COB Setting failure response for list common params");
            CommonUtils.generateHeaderForNoResult(commonParamRespHeader);
            commonParamRespBody.setResponseObj("");
        } else {
            logger.debug("COB Setting success response for list common params");
            CommonUtils.generateHeaderForSuccess(commonParamRespHeader);
            String commonParamsResponseJson = gson.toJson(commonParamResponseList);
            commonParamRespBody.setResponseObj(commonParamsResponseJson);
        }

        commonParamResponse.setResponseBody(commonParamRespBody);
        commonParamResponse.setResponseHeader(commonParamRespHeader);

        logger.debug("COB Common Param Response:" + commonParamResponse.toString());
        return commonParamResponse;
    }

    public String getJtAccDtls(String defaultCasaProductCodeLN, LocalDate today) {
        Optional<ProductDetails> prodObj = productDetailsrepository.findProductDetailsBasedOnProductCode(
                defaultCasaProductCodeLN, AppStatus.ACTIVE_STATUS.getValue(), today);
        String jointAccAllowed = "N";
        String numOfJointHolders = "";
        if (prodObj.isPresent()) {
            ProductDetails prdDtl = prodObj.get();
            JSONObject json = new JSONObject(prdDtl.getProductFeatures());
            if (json.has("isJointAccountRequired~Y")) {
                jointAccAllowed = json.getString("isJointAccountRequired~Y");
            } else if (json.has("isJointAccountRequired~N")) {
                jointAccAllowed = json.getString("isJointAccountRequired~N");
            }
            if (json.has("NoOfJointHolder~Y")) {
                numOfJointHolders = json.getString("NoOfJointHolder~Y");
            } else if (json.has("NoOfJointHolder~N")) {
                numOfJointHolders = json.getString("NoOfJointHolder~N");
            }
        }
        return jointAccAllowed + "~" + numOfJointHolders;
    }

    private List<CommonParamResponse> generateResponseWrapper(Iterable<TbAbmiCommonCodeDomain> commonParam) {
        logger.debug("Inside generate Response format function.");
        List<CommonParamResponse> commonParamResponse = new ArrayList<>();
        for (TbAbmiCommonCodeDomain tbCodeDomain : commonParam) {
            CommonParamResponse commonParamResponseOBJ = new CommonParamResponse();
            commonParamResponseOBJ.setParamName(tbCodeDomain.getCode());
            commonParamResponseOBJ.setParamValue(tbCodeDomain.getCodeDesc());
            commonParamResponseOBJ.setAccessType(tbCodeDomain.getAccessType());
            commonParamResponseOBJ.setLanguage(tbCodeDomain.getLanguage());
            commonParamResponseOBJ.setCodeType(tbCodeDomain.getCodeType());
            commonParamResponse.add(commonParamResponseOBJ);
        }
        return commonParamResponse;
    }

    public Response fetchProducts() {
        Gson gson = new Gson();
        Response fetchProductsResponse = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        fetchProductsResponse.setResponseHeader(responseHeader);
        List<ProductGroup> productList = productGroupRepository
                .findByProductGroupStatusOrderBySlNumAsc(AppStatus.ACTIVE_STATUS.getValue());
        String response = gson.toJson(productList);
        responseBody.setResponseObj(response);
        fetchProductsResponse.setResponseBody(responseBody);
        return fetchProductsResponse;
    }

    public Response fetchProductDetails(FetchProductDetailsRequest fetchProductDetailsRequest) {
        Gson gson = new Gson();
        Response fetchProductDetailsResponse = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        fetchProductDetailsResponse.setResponseHeader(responseHeader);
        List<ProductDetails> productDetailsList = null;
        LocalDate today = LocalDate.now();
        if (!(CommonUtils.isNullOrEmpty(fetchProductDetailsRequest.getRequestObj().getProductGroupCode()))) {
            String prodGrpCode = fetchProductDetailsRequest.getRequestObj().getProductGroupCode();
            productDetailsList = productDetailsrepository.findProductDetails(prodGrpCode,
                    AppStatus.ACTIVE_STATUS.getValue(), today);
        } else { // Required for admin application
            productDetailsList = productDetailsrepository.findProductDetails(AppStatus.ACTIVE_STATUS.getValue(), today);
        }
        String response = gson.toJson(productDetailsList);
        responseBody.setResponseObj(response);
        fetchProductDetailsResponse.setResponseBody(responseBody);
        return fetchProductDetailsResponse;
    }

    public JSONArray getJsonArrayForCmCodeAndKey(String cmCode, String codeType, String key) {
        Optional<TbAbmiCommonCodeDomain> commCodeObj = tbAbmiCommonCodeRepository
                .findById(new TbAbmiCommonCodeId(codeType, cmCode));
        if (commCodeObj.isPresent()) {
            TbAbmiCommonCodeDomain commCodeDb = commCodeObj.get();
            JSONObject jsonObj = new JSONObject(commCodeDb.getCodeDesc());
            return jsonObj.getJSONArray(key);
        }
        return null;
    }

    public String getElementForCmCode(String cmCode, String codeType, String key) {
        Optional<TbAbmiCommonCodeDomain> commCodeObj = tbAbmiCommonCodeRepository
                .findById(new TbAbmiCommonCodeId(codeType, cmCode));
        if (commCodeObj.isPresent()) {
            TbAbmiCommonCodeDomain commCodeDb = commCodeObj.get();
            JSONObject jsonObj = new JSONObject(commCodeDb.getCodeDesc());
            return jsonObj.getString(key);
        }
        return null;
    }

    public JSONArray getJsonArrayForCmCode(String cmCode, String key) {
        Optional<TbAbmiCommonCodeDomain> commCodeObj = tbAbmiCommonCodeRepository
                .findById(new TbAbmiCommonCodeId(Constants.COMM, cmCode));
        if (commCodeObj.isPresent()) {
            TbAbmiCommonCodeDomain commCodeDb = commCodeObj.get();
            JSONObject jsonObj = new JSONObject(commCodeDb.getCodeDesc());
            JSONObject jsonObjCmCode = jsonObj.getJSONObject(cmCode);
            return jsonObjCmCode.getJSONArray(key);
        }
        return null;
    }

    public JSONArray getJsonArrayForCmCodeAndKey(String cmCode, String codeType, String key, String types) {
        Optional<TbAbmiCommonCodeDomain> commCodeObj = tbAbmiCommonCodeRepository
                .findById(new TbAbmiCommonCodeId(codeType, cmCode));
        if (commCodeObj.isPresent()) {
            TbAbmiCommonCodeDomain commCodeDb = commCodeObj.get();
            JSONObject jsonObj = new JSONObject(commCodeDb.getCodeDesc());
            JSONObject jsonObj2 = jsonObj.getJSONObject(Constants.UPLOAD_DOCS);
            return jsonObj2.getJSONArray(types);
        }
        return null;
    }

    public JSONObject getJsonObjectForCmCode(String cmCode, String key) {
        Optional<TbAbmiCommonCodeDomain> commCodeObj = tbAbmiCommonCodeRepository
                .findById(new TbAbmiCommonCodeId(Constants.COMM, cmCode));
        if (commCodeObj.isPresent()) {
            TbAbmiCommonCodeDomain commCodeDb = commCodeObj.get();
            JSONObject jsonObj = new JSONObject(commCodeDb.getCodeDesc());
            return jsonObj.getJSONObject(key);
        }
        return null;
    }

    // Due to maven's cyclic dependency this method is written in core instead of
    // deposit module. Remove this method and its call if deposit is not in scope
    // for implementation.
    public void updateDtlsForRelatedAppln(String headerAppId, String relatedApplicationId, String appId,
                                          String applicationId, int version, Properties prop, String mainProductGroupCode) {
        JSONArray array = new JSONArray();
        if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
            if (Products.DEPOSIT.getKey().equalsIgnoreCase(mainProductGroupCode)) {
                array = getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.DEPOSIT_NTB.getKey(),
                        Constants.FUNCTIONSEQUENCE);
            } else if (Products.LOAN.getKey().equalsIgnoreCase(mainProductGroupCode)) {
                array = getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.LOAN_NTB.getKey(),
                        Constants.FUNCTIONSEQUENCE);
            }
        } else {
            if (Products.DEPOSIT.getKey().equalsIgnoreCase(mainProductGroupCode)) {
                array = getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.DEPOSIT_BO_NTB.getKey(),
                        Constants.FUNCTIONSEQUENCE);
            } else if (Products.LOAN.getKey().equalsIgnoreCase(mainProductGroupCode)) {
                array = getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.LOAN_BO_NTB.getKey(),
                        Constants.FUNCTIONSEQUENCE);
            }
        }
        String lastElementArr = ((String) array.get(array.length() - 1)).split("~")[0];
        Optional<ApplicationMaster> relatedmasterObjDb = appMasterRepo
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(appId, relatedApplicationId, version,
                        AppStatus.INPROGRESS.getValue());
        List<String> statusList = new ArrayList<>();
        statusList.add(AppStatus.INPROGRESS.getValue()); // Required for self onboarding
        statusList.add(AppStatus.PENDING.getValue()); // Required for back office
        statusList.add(AppStatus.APPROVED.getValue()); // Required for self onboarding and back office.
        Optional<ApplicationMaster> masterObjDb = appMasterRepo
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(appId, applicationId, version,
                        statusList);
        if (relatedmasterObjDb.isPresent() && masterObjDb.isPresent()) {
            ApplicationMaster relatedMasterObj = relatedmasterObjDb.get();
            ApplicationMaster masterObj = masterObjDb.get();
            relatedMasterObj.setCurrentScreenId(lastElementArr);
            relatedMasterObj.setNationalId(masterObj.getNationalId());
            relatedMasterObj.setPan(masterObj.getPan());
            relatedMasterObj.setSearchCode1(masterObj.getSearchCode1());
            relatedMasterObj.setEmailId(masterObj.getEmailId());
            //relatedMasterObj.setDeclarationFlag(masterObj.getDeclarationFlag());
            relatedMasterObj.setMobileVerStatus(masterObj.getMobileVerStatus());
            relatedMasterObj.setEmailVerStatus(masterObj.getEmailVerStatus());
            relatedMasterObj.setKycType(masterObj.getKycType());
            relatedMasterObj.setApplicationStatus(masterObj.getApplicationStatus());
            appMasterRepo.save(relatedMasterObj);
        }
    }

    public DepositDtls getDepoDtlsForCasa(String appId, String applicationId) {
        Optional<DepositDtls> depositDtlObj = depoDtlRepo.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId,
                applicationId);
        if (depositDtlObj.isPresent()) {
            return depositDtlObj.get();
        }
        return null;
    }

    public LoanDetails getLoanDetailsForRenewal(String appId, String applicationId) {
        Optional<LoanDetails> loanDetails = loanDtlsRepo.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId,
                applicationId);
        if (loanDetails.isPresent()) {
            return loanDetails.get();
        }
        return null;
    }

    public TbAbmiCommonCodeDomain fetchCommonCode(String code) {
        Optional<TbAbmiCommonCodeDomain> commCodeObj = tbAbmiCommonCodeRepository
                .findById(new TbAbmiCommonCodeId(Constants.COMM, code));
        if (commCodeObj.isPresent()) {
            return commCodeObj.get();
        }
        return null;
    }

    public void saveCommonCode(TbAbmiCommonCodeDomain commonCodeObj) {
        tbAbmiCommonCodeRepository.save(commonCodeObj);
    }

    public Response populateApplnWorkFlow(PopulateapplnWFRequest request) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        PopulateapplnWFRequestFields reqFields = request.getRequestObj();
        Set<String> userList = new HashSet<>();
        String userListString = "";
        String roleString = "";
        int wfSeqNum = Constants.INITIAL_VERSION_NO;
        String previousWorkflowStatus = "";
        Properties prop = null;
        try {
            prop = CommonUtils.readPropertyFile();
        } catch (Exception e) {
            logger.error("Error while reading property file with exception: {}", e.getMessage(), e);
        }
        Optional<ApplicationWorkflow> wfObj = applnWfRepository
                .findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(reqFields.getAppId(),
                        reqFields.getApplicationId(), reqFields.getVersionNum());
        if (wfObj.isPresent()) {
            ApplicationWorkflow dbObj = wfObj.get();
            wfSeqNum = dbObj.getWorkflowSeqNum() + 1;
            previousWorkflowStatus = dbObj.getApplicationStatus();
        }
        WorkFlowDetails workFlow = reqFields.getWorkflow();

        ApplicationWorkflow workFlowObj = new ApplicationWorkflow();
        workFlowObj.setAppId(reqFields.getAppId());
        workFlowObj.setApplicationId(reqFields.getApplicationId());
        String status = workFlow.getNextWorkflowStatus();
        workFlowObj.setApplicationStatus(status);

        if (null != workFlowObj.getApplicationStatus()) {
            if (StringUtils.isNotBlank(previousWorkflowStatus)) {
                if (AppStatus.REJECTED.getValue().equalsIgnoreCase(status)) {
                    logger.debug("Application Rejected, Sending Loan Rejection SMS");
                    sendLoanAcknowledgmentAndRejectionSMS(reqFields.getApplicationId(), reqFields.getAppId(), prop, true);
                } else {
                    if (previousWorkflowStatus.equalsIgnoreCase(AppStatus.INPROGRESS.getValue())) {
                        sendLoanAcknowledgmentAndRejectionSMS(reqFields.getApplicationId(), reqFields.getAppId(), prop, false);
                    }
                }
            }
        }

        if (Constants.PENDINGREASSESSMENT.equalsIgnoreCase(status)) {
            List<DeviationRATracker> deviationRATrackerList = deviationRATrackerRepository
                    .findByApplicationIdAndRecordType(reqFields.getApplicationId(), Constants.CA_DEVIATION);
            Set<DeviationRATracker> deviationRATrackerSet = new HashSet<>(deviationRATrackerList);
            if (!deviationRATrackerList.isEmpty()) {
                for (DeviationRATracker deviationRATracker : deviationRATrackerSet) {
                    if (deviationRATracker.getApprovedStatus().equalsIgnoreCase(Constants.APPROVED)) {
                        String approvedBy = deviationRATracker.getApprovedBy();
                        String authority = deviationRATracker.getAuthority();
                        String userIdAndRole = authority.trim() + "(" + approvedBy.trim() + ")";
                        userList.add(userIdAndRole);
                    }
                }
                userListString = String.join(",", userList);
                workFlowObj.setCreatedBy(userListString);
                workFlowObj.setCurrentRole(roleString);
            } else {
                workFlowObj.setCreatedBy(reqFields.getCreatedBy());
                workFlowObj.setCurrentRole(workFlow.getCurrentRole());
            }
        } else if (Constants.PENDINGPRESANCTION.equalsIgnoreCase(status)) {
            List<DeviationRATracker> deviationRATrackerList = deviationRATrackerRepository.findByApplicationIdAndRecordType(
                    reqFields.getApplicationId(), Constants.REASSESSMENT);
            Set<DeviationRATracker> deviationRATrackerSet = new HashSet<>(deviationRATrackerList);
            if (!deviationRATrackerList.isEmpty()) {
                for (DeviationRATracker deviationRATracker : deviationRATrackerSet) {
                    if (deviationRATracker.getApprovedStatus().equalsIgnoreCase(Constants.APPROVED)) {
                        String approvedBy = deviationRATracker.getApprovedBy();
                        String authority = deviationRATracker.getAuthority();
                        String userIdAndRole = authority.trim() + "(" + approvedBy.trim() + ")";
                        userList.add(userIdAndRole);
                    }
                }
                userListString = String.join(",", userList);
                workFlowObj.setCreatedBy(userListString);
                workFlowObj.setCurrentRole(roleString);
            } else {
                workFlowObj.setCreatedBy(reqFields.getCreatedBy());
                workFlowObj.setCurrentRole(workFlow.getCurrentRole());
            }
        } else {
            workFlowObj.setCreatedBy(reqFields.getCreatedBy());
            workFlowObj.setCurrentRole(workFlow.getCurrentRole());
        }
        workFlowObj.setCreateTs(LocalDateTime.now());
        if (workFlow != null) {
            workFlowObj.setNextWorkFlowStage(workFlow.getNextStageId());
            workFlowObj.setCurrentRole(workFlow.getCurrentRole());
            workFlowObj.setRemarks(workFlow.getRemarks());
        }

        workFlowObj.setVersionNum(reqFields.getVersionNum());
        workFlowObj.setWorkflowSeqNum(wfSeqNum);
        logger.debug("workFlowObj :" + workFlowObj.toString());
        applnWfRepository.save(workFlowObj);
        Optional<ApplicationMaster> appMaster = applicationMasterRepository.findByAppIdAndApplicationIdAndVersionNum(
                reqFields.getAppId(), reqFields.getApplicationId(), reqFields.getVersionNum());
        if (appMaster.isPresent()) {
            ApplicationMaster applicationMaster = appMaster.get();
            String productCode = applicationMaster.getProductCode();
            if (Constants.OPENMARKET_LOAN_PRODUCT_CODE.equalsIgnoreCase(productCode)) {
                logger.debug("going to update CDH table");
                updateCDHStatus(workFlow, applicationMaster);
            }
            if (null != workFlowObj.getApplicationStatus()) {
                if (!applicationMaster.getApplicationStatus().equalsIgnoreCase(workFlowObj.getApplicationStatus())) {
                    if (workFlowObj.getApplicationStatus().equalsIgnoreCase(WorkflowStatus.PENDING_FOR_APPROVAL.getValue())) {
                        applicationMaster.setApplicationStatus(AppStatus.PENDING.getValue());
                    } else {
                        applicationMaster.setApplicationStatus(workFlowObj.getApplicationStatus());
                    }
                    applicationMaster.setUpdatedBy(workFlowObj.getCreatedBy());
                    applicationMaster.setUpdateTs(LocalDateTime.now());
                    applicationMaster.setRemarks(workFlowObj.getRemarks());
                    applicationMasterRepository.save(applicationMaster);
                }
            }
        }
        responseBody.setResponseObj("");
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }

    public void updateCDHStatus(WorkFlowDetails workFlow, ApplicationMaster masterObj) {
        logger.debug("Inside updateCDHStatus with workflow details: {} and application master details: {}", workFlow, masterObj);
        String status = masterObj.getApplicationStatus();
        String workflowStatus = workFlow != null ? workFlow.getNextWorkflowStatus() : "";
        if (StringUtils.isNotBlank(workflowStatus)
                && workflowStatus.equalsIgnoreCase(WorkflowStatus.PENDING_FOR_APPROVAL.getValue())) {
            status = AppStatus.PENDING.getValue(); //becuase in from KM to BM the workflow is being inserted first
            // before the appMaster and the old status is being taken from the appMaster which is still in INPROGRESS status.
            // Hence to avoid this we are taking the workflow status
            // and mapping it to pending for approval and then to pending in appMaster.
        }
        String customerId = masterObj.getSearchCode2();
        String applicationId = masterObj.getApplicationId();
        logger.debug("updateCDHStatus called with applicationId={}, customerId={}, status={}",
                applicationId, customerId, status);
        try {
            boolean isCaseSanctionedBefore = applicationWorkflowRepository
                    .existsByApplicationIdAndApplicationStatus(applicationId, AppStatus.SANCTIONED.getValue());
            logger.debug("isCaseSanctionedBefore for applicationId {} : {}", applicationId, isCaseSanctionedBefore);
            boolean isCurrentSanctioned = AppStatus.SANCTIONED.getValue().equalsIgnoreCase(status);
            if (!isCaseSanctionedBefore || isCurrentSanctioned) {
                String cdhStatus = AppStatus.getCdhStatusByValue(status);
                logger.debug("Mapped CDH status for applicationId {} : {}", applicationId, cdhStatus);
                if (StringUtils.isBlank(customerId)) {
                    logger.warn("CustomerId is blank for applicationId {}, skipping CDH update", applicationId);
                    return;
                }
                Optional<UnnatiIexceedCDHLead> cdhLeadOpt = unnatiIexceedCDHLeadRepo.findByCustomerId(customerId);
                if (cdhLeadOpt.isPresent()) {
                    UnnatiIexceedCDHLead cdhLead = cdhLeadOpt.get();
                    cdhLead.setUnnatiStatus(cdhStatus);
                    if (AppStatus.REJECTED.getValue().equalsIgnoreCase(status)) {
                        List<CibilDetails> cibilDetailsList = cibilDetailsRepository.findByApplicationIdAndAppId(
                                applicationId, masterObj.getAppId());
                        boolean isBreReject = false;
                        if (!cibilDetailsList.isEmpty()) {
                            isBreReject = cibilDetailsList.stream()
                                    .anyMatch(c -> "FAIL".equalsIgnoreCase(c.getCbStatus()));
                        }
                        if (isBreReject) {
                            cdhLead.setRejectionReason("BRE check reject");
                            logger.debug("Set rejectionReason='BRE check reject' for customerId {}", customerId);
                        } else {
                            cdhLead.setRejectionReason("Manual Reject");
                            logger.debug("Set rejectionReason='Manual Reject' for customerId {}", customerId);
                        }
                    }
                    logger.debug("Saving updated CDH lead for customerId {} : {}", customerId, cdhLead);
                    unnatiIexceedCDHLeadRepo.save(cdhLead);
                    logger.info("CDH unnati status updated successfully to {} for customerId {}", cdhStatus, customerId);
                } else {
                    logger.warn("No CDH lead found for customerId {} - applicationId {}", customerId, applicationId);
                }
            } else {
                logger.debug("Case is already sanctioned before for applicationId {}, skipping CDH update", applicationId);
            }
        } catch (Exception e) {
            logger.error("Error while updating CDH status for applicationId={}, customerId={}", applicationId, customerId, e);
        }
    }

    public void sendLoanAcknowledgmentAndRejectionSMS(String applicationId, String appId, Properties prop, boolean isRejected) {
        String phoneNumber = "";
        String CustomerName = "";
        logger.debug("Starting sendLoanAcknowledgmentAndRejectionSMS service for applicationId : {}", applicationId);
        try {
            Optional<ApplicationMaster> applicationMasterOptional = applicationMasterRepository
                    .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, Constants.INITIAL_VERSION_NO);
            logger.debug("Getting optional master object");
            if (applicationMasterOptional.isPresent()) {
                logger.debug("Master data value present.");
                ApplicationMaster masterObj = applicationMasterOptional.get();
                LoanDetails loanDetails = loanDtlsRepo.findByApplicationId(applicationId);
                Gson gsonObj = new Gson();
                if (null == loanDetails) {
                    logger.debug("Loan details not present");
                    return;
                }
                LoanDetailsPayload loanDetailsPayload = gsonObj.fromJson(loanDetails.getPayloadColumn(),
                        LoanDetailsPayload.class);
                String language = loanDetailsPayload.getLanguage();
                BigDecimal loanAmount = loanDetails.getLoanAmount();

                Optional<CustomerDetails> customerDetailsOpt = customerDetailsRepository
                        .findByApplicationIdAndAppIdAndCustomerType(applicationId, appId, Constants.APPLICANT);
                if (!customerDetailsOpt.isPresent()) {
                    logger.debug("Customer details not present");
                    return;
                }
                phoneNumber = customerDetailsOpt.get().getMobileNumber();
                if (org.apache.commons.lang3.StringUtils.isBlank(phoneNumber)) {
                    logger.debug("Phone number not present");
                    return;
                }
                CustomerDetailsPayload custDtlsPayload = gsonObj.fromJson(customerDetailsOpt.get().getPayloadColumn(), CustomerDetailsPayload.class);
                String customerName = custDtlsPayload.getFirstName();
                String loanAmountString = (loanAmount != null) ? loanAmount.toString() : "";
                if (org.apache.commons.lang.StringUtils.isEmpty(loanAmountString)) {
                    logger.debug("Loan amount not present");
                    return;
                }
                SendSmsAndEmailApiRequest sendSmsandEmailApiRequest = new SendSmsAndEmailApiRequest();
                SendSmsAndEmailRequestObject sendSmsAndEmailRequestObject = new SendSmsAndEmailRequestObject();

                LocalDate actionDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String actionDateString = actionDate.format(formatter);

                String actionType = (isRejected) ? Constants.REJECTION : Constants.SOURCING_SUBMISSION;
                logger.debug("Action Type : {}", actionType);
                sendSmsandEmailApiRequest.setAppId(masterObj.getAppId());
                sendSmsandEmailApiRequest.setInterfaceName(Constants.SMS_INTF);
                sendSmsandEmailApiRequest.setUserId(masterObj.getCreatedBy());
                sendSmsandEmailApiRequest.setUserName("");
                sendSmsAndEmailRequestObject.setActionType(actionType);
                sendSmsAndEmailRequestObject.setLanguage(language);
                sendSmsAndEmailRequestObject.setMobileNo(phoneNumber);
                sendSmsAndEmailRequestObject.setCustName(customerName);
                sendSmsandEmailApiRequest.setRequestObject(sendSmsAndEmailRequestObject);
                if (isRejected) {
                    sendSmsAndEmailRequestObject
                            .setAttachmentContent(customerName + "|~|" + loanAmountString);
                    sendSmsAndEmailService.sendSmsAndEmailService(sendSmsandEmailApiRequest, prop, SmsStage.REJECTION);
                } else {
                    sendSmsAndEmailRequestObject
                            .setAttachmentContent(customerName + "|~|" + loanAmountString + "|~|" + actionDateString + "|~|" + applicationId);
                    sendSmsAndEmailService.sendSmsAndEmailService(sendSmsandEmailApiRequest, prop, SmsStage.SOURCING_SUBMISSION);
                }
            }
        } catch (Exception e) {
            logger.error("Error while sending SMS: {}", e.getMessage(), e);
        }
    }

    public void updateNationIdInMaster(ApplicationMaster masterRequest, int version, String appId,
                                       String applicationId) {
        Optional<ApplicationMaster> masterObjDb = appMasterRepo
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(appId, applicationId, version,
                        AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
            if (!(CommonUtils.isNullOrEmpty(masterRequest.getNationalId()))) {
                masterObj.setNationalId(masterRequest.getNationalId());
                appMasterRepo.save(masterObj);
            }
        }
    }

    public void updatePanInMaster(ApplicationMaster masterRequest, int version, String appId, String applicationId) {
        Optional<ApplicationMaster> masterObjDb = appMasterRepo
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(appId, applicationId, version,
                        AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
            if (!(CommonUtils.isNullOrEmpty(masterRequest.getPan()))) {
                masterObj.setPan(masterRequest.getPan());
                appMasterRepo.save(masterObj);
            }
        }
    }

    public void updateCurrentStageInMaster(ApplicationMaster masterRequest, String[] currentScreenIdArray, int version,
                                           String appId, String applicationId) {
        Optional<ApplicationMaster> masterObjDb = appMasterRepo
                .findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(appId, applicationId, version,
                        AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
            if (currentScreenIdArray != null && currentScreenIdArray.length > 1
                    && "Y".equalsIgnoreCase(currentScreenIdArray[1])) {//// It will be "N" when called service by using
            //// back navigation.
                masterObj.setCurrentScreenId(currentScreenIdArray[0]);
                masterObj.setCurrentStageNo(masterRequest.getCurrentStageNo());
                masterObj.setCreatedBy(masterRequest.getCreatedBy());
                appMasterRepo.save(masterObj);
            }
        }
    }

    /*
     * The purpose of this method is to populate cust dtl id, application id, app id
     * and version number in TB_ABOB_CUSTOMER_DETAILS table so that fetch
     * application service can always guarantee the custDtlId in response. Otherwise
     * fetch application service may not have custDtlId in response if
     * populateCustomerDtls method is not yet called.
     */
    public void populateCustomerDtlsIfNotPresent(ApplicationMaster appMasterObj, String applicationID,
                                                 BigDecimal custDtlId, int version, String appId) {
        Optional<CustomerDetails> custDtlObjDb = custDtlRepo.findById(custDtlId);
        if (!custDtlObjDb.isPresent()) {
            CustomerDetails custDtlObj = new CustomerDetails();
            custDtlObj.setAppId(appId);
            custDtlObj.setApplicationId(applicationID);
            custDtlObj.setVersionNum(version);
            custDtlObj.setCustDtlId(custDtlId);
            custDtlObj.setSeqNumber(appMasterObj.getCustDtlSlNum());
            custDtlRepo.save(custDtlObj);
        }
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchRoleIdFallback")
    public String fetchRoleId(String appId, String userId) {
        String roleId = "";
        Optional<UserRole> objDb = userRoleRepository.findByAppIdAndUserId(appId, userId);
        if (objDb.isPresent()) {
            UserRole obj = objDb.get();
            roleId = obj.getRoleId();
        }
        return roleId;
    }

    private boolean isNomineeMinor(String nomineeDob, String productGroupCode, String isExistingCustomer) {
        int majorMinimumAge;
        if (!(CommonUtils.isNullOrEmpty(nomineeDob))) {
            LocalDate dob = LocalDate.parse(nomineeDob); // yyyy-mm-dd
            Period period = Period.between(dob, LocalDate.now());
            if (Products.CASA.getKey().equalsIgnoreCase(productGroupCode)) {
                majorMinimumAge = Integer.parseInt(getElementForCmCode(Constants.NOMINEE_DETAILS,
                        CodeTypes.CASA.getKey(), Constants.NOMINEE_MAJOR_KEY));
                return (period.getYears() < majorMinimumAge);
            } else if (Products.DEPOSIT.getKey().equalsIgnoreCase(productGroupCode)) {
                if ("N".equalsIgnoreCase(isExistingCustomer)) {
                    majorMinimumAge = Integer.parseInt(getElementForCmCode(Constants.NOMINEE_DETAILS,
                            CodeTypes.CASA.getKey(), Constants.NOMINEE_MAJOR_KEY));
                    return (period.getYears() < majorMinimumAge);
                } else if ("Y".equalsIgnoreCase(isExistingCustomer)) {
                    majorMinimumAge = Integer.parseInt(getElementForCmCode(Constants.NOMINEE_DETAILS,
                            CodeTypes.DEPOSIT_ETB.getKey(), Constants.NOMINEE_MAJOR_KEY));
                    return (period.getYears() < majorMinimumAge);
                }
            }
        } else { // Else block will be executed when nominee dob is unknown. This can happen if
            // nominee dob is optional field. Based on requirement change the return value
            // of this else block.
            return false;
        }
        return false; // Based on requirement change the return value of this.
    }

    public boolean callIsValidFieldvalue(String param1, String fieldName, String screenElement, String value) {
        if (param1.equalsIgnoreCase(fieldName)) {
            return isValidFieldvalue(screenElement, value);
        } else {
            return false;
        }
    }

    public boolean isValidFieldvalue(Object screenElement, String fieldValue) {
        String[] fieldArray = ((String) screenElement).split("~");
        String fieldMandatoryOrNot = "";
        String fieldShowOrNot = fieldArray[1];
        if (fieldArray.length > 2) { // for banking facilities it will be length 1
            fieldMandatoryOrNot = fieldArray[2];
        }
        logger.debug("inside isValidFieldvalue,fieldArray " + fieldArray[0] + " fieldShowOrNot " + fieldShowOrNot
                + " fieldMandatoryOrNot " + fieldMandatoryOrNot + " fieldValue " + fieldValue);
        if ("N".equalsIgnoreCase(fieldShowOrNot)) {
            if (CommonUtils.isNullOrEmpty(fieldValue)) {
                return true;
            }
        } else if ("Y".equalsIgnoreCase(fieldShowOrNot)) {
            if ("M".equalsIgnoreCase(fieldMandatoryOrNot)) {
                if (!(CommonUtils.isNullOrEmpty(fieldValue))) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean vaptForFieldsCustVerificationCasa(ApplicationMaster appMasterReq, JSONArray stageArray) {
        boolean isValid = true;
        String fieldName;
        for (Object screenElement : stageArray) {
            fieldName = ((String) screenElement).split("~")[0];
            if (Constants.MOBILENO.equalsIgnoreCase(fieldName)) {
                if (!(CommonUtils.validateMobile(appMasterReq.getMobileNumber()))) {
                    return false;
                }
                isValid = isValidFieldvalue((String) screenElement, appMasterReq.getMobileNumber());
            } else if (Constants.EMAIL.equalsIgnoreCase(fieldName)) {
                if (!(CommonUtils.validateEmailId(appMasterReq.getEmailId()))) {
                    return false;
                }
                isValid = isValidFieldvalue((String) screenElement, appMasterReq.getEmailId());
            } else if (Constants.PANINPUT.equalsIgnoreCase(fieldName)) {
                if (!CommonUtils.isNullOrEmpty(appMasterReq.getPan())) {
                    Response isValidPan = CommonUtils.verifyNationalId(Constants.NATIONALIDPAN, appMasterReq.getPan());
                    if (ResponseCodes.INVALID_PAN.getKey()
                            .equalsIgnoreCase(isValidPan.getResponseHeader().getResponseCode())) {
                        return false;
                    }
                }
                isValid = isValidFieldvalue((String) screenElement, appMasterReq.getPan());
            } else if (Constants.NIDINPUT.equalsIgnoreCase(fieldName)) {
                isValid = isValidFieldvalue((String) screenElement, appMasterReq.getNationalId());
            }
            if (!isValid) {
                return false;
            }
        }
        return true;
    }

    public boolean vaptForFieldsCustVerificationDep(ApplicationMaster appMasterReq, JSONArray stageArray) {
        String fieldName;
        boolean isValid = true;
        for (Object screenElement : stageArray) {
            fieldName = ((String) screenElement).split("~")[0];
            if (Constants.CUSTOMERID.equalsIgnoreCase(fieldName)) {
                if (appMasterReq.getCustomerId() != null) {
                    isValid = isValidFieldvalue((String) screenElement, appMasterReq.getCustomerId().toString());
                }
            } else if ("MobileNo".equalsIgnoreCase(fieldName)) {
                if (!(CommonUtils.validateMobile(appMasterReq.getMobileNumber()))) {
                    return false;
                }
                isValid = isValidFieldvalue((String) screenElement, appMasterReq.getMobileNumber());
            } else if ("Email".equalsIgnoreCase(fieldName)) {
                if (!(CommonUtils.validateEmailId(appMasterReq.getEmailId()))) {
                    return false;
                }
                isValid = isValidFieldvalue((String) screenElement, appMasterReq.getEmailId());
            }
            if (!isValid) {
                return false;
            }
        }
        /*
         * if("Name".equalsIgnoreCase(fieldName)) { if(!isSelfOnBoardingHeaderAppId) {
         * //COB will have null for name in customer verification
         * if(!(isValidFieldvalue(screenElement, appMasterReq.getCreatedBy()))){ return
         * false; } } }
         */

        return true;
    }

    public boolean vaptForFieldsCustVerificationCards(ApplicationMaster appMasterReq, JSONArray stageArray) {
        String fieldName;
        boolean isValid = true;
        for (Object screenElement : stageArray) {
            fieldName = ((String) screenElement).split("~")[0];
            if (Constants.CUSTOMERID.equalsIgnoreCase(fieldName)) {
                if (appMasterReq.getCustomerId() != null) {
                    isValid = isValidFieldvalue((String) screenElement, appMasterReq.getCustomerId().toString());
                }
            } else if (Constants.MOBILENO.equalsIgnoreCase(fieldName)) {
                if (!CommonUtils.validateMobile(appMasterReq.getMobileNumber())) {
                    return false;
                }
                isValid = isValidFieldvalue((String) screenElement, appMasterReq.getMobileNumber());
            } else if (Constants.EMAIL.equalsIgnoreCase(fieldName)) {
                if (!CommonUtils.validateEmailId(appMasterReq.getEmailId())) {
                    return false;
                }
                isValid = isValidFieldvalue((String) screenElement, appMasterReq.getEmailId());
            } else if (Constants.PANINPUT.equalsIgnoreCase(fieldName)) {
                if (!CommonUtils.isNullOrEmpty(appMasterReq.getPan())) {
                    Response isValidPan = CommonUtils.verifyNationalId(Constants.NATIONALIDPAN, appMasterReq.getPan());
                    if (ResponseCodes.INVALID_PAN.getKey()
                            .equalsIgnoreCase(isValidPan.getResponseHeader().getResponseCode())) {
                        return false;
                    }
                }
                isValid = isValidFieldvalue((String) screenElement, appMasterReq.getPan());
            } else if (Constants.NIDINPUT.equalsIgnoreCase(fieldName)) {
                isValid = isValidFieldvalue((String) screenElement, appMasterReq.getNationalId());
            }
            if (!isValid) {
                return false;
            }
        }
        return true;
    }

    public boolean vaptForFieldsCustVerificationLoan(ApplicationMaster appMasterReq, JSONArray stageArray) {
        String fieldName;
        boolean isValid = true;
        for (Object screenElement : stageArray) {
            fieldName = ((String) screenElement).split("~")[0];
            if ("MobileNo".equalsIgnoreCase(fieldName)) {
                if (!CommonUtils.validateMobile(appMasterReq.getMobileNumber())) {
                    return false;
                }
                isValid = isValidFieldvalue((String) screenElement, appMasterReq.getMobileNumber());
            } else if (Constants.CUSTOMERID.equalsIgnoreCase(fieldName)) {
                if (appMasterReq.getCustomerId() != null) {
                    isValid = isValidFieldvalue((String) screenElement, appMasterReq.getCustomerId().toString());
                }
            } else if ("Email".equalsIgnoreCase(fieldName)) {
                if (!CommonUtils.validateEmailId(appMasterReq.getEmailId())) {
                    return false;
                }
                isValid = isValidFieldvalue((String) screenElement, appMasterReq.getEmailId());
            } else if (Constants.PANINPUT.equalsIgnoreCase(fieldName)) {
                isValid = isValidFieldvalue((String) screenElement, appMasterReq.getPan());
            } else if (Constants.NIDINPUT.equalsIgnoreCase(fieldName)) {
                isValid = isValidFieldvalue((String) screenElement, appMasterReq.getNationalId());
            }
            if (!isValid) {
                return false;
            }
        }
        return true;
    }

    public boolean vaptForFieldsCustDtls(List<CustomerDetails> customerDetailsList,
                                         List<AddressDetailsWrapper> addressDetailsWrapperList, JSONArray stageArray) {
        boolean isValid = true;
        String fieldName;
        for (Object screenElement : stageArray) {
            fieldName = ((String) screenElement).split("~")[0];
            for (CustomerDetails customerDetails : customerDetailsList) {
                CustomerDetailsPayload payload = customerDetails.getPayload();
                if ("Title".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, payload.getTitle());
                } else if ("Name".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, customerDetails.getCustomerName());
                } else if ("DOB".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, payload.getDob());
                } else if ("Gender".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, payload.getGender());
                } else if ("MaritalStatus".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, payload.getMaritalStatus());
                } else if ("SpouseName".equalsIgnoreCase(fieldName)) {
                    if ("M".equalsIgnoreCase(payload.getMaritalStatus())) {
                        isValid = isValidFieldvalue((String) screenElement, payload.getSpouseName());
                    }
                } else if (Constants.MOBILENO.equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, customerDetails.getMobileNumber());
                    if (!CommonUtils.validateMobile(customerDetails.getMobileNumber())) {
                        return false;
                    }
                } else if ("AltMobileNo".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, payload.getAltMobileNumber());
                    if (!CommonUtils.validateMobile(payload.getAltMobileNumber())) {
                        return false;
                    }
                } else if (Constants.EMAIL.equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, payload.getEmailId());
                    if (!CommonUtils.validateEmailId(payload.getEmailId())) {
                        return false;
                    }
                } else if ("PanNo".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, payload.getPan());
                    if (!CommonUtils.isNullOrEmpty(payload.getPan())) {
                        Response isValidPan = CommonUtils.verifyNationalId(Constants.NATIONALIDPAN, payload.getPan());
                        if (ResponseCodes.INVALID_PAN.getKey()
                                .equalsIgnoreCase(isValidPan.getResponseHeader().getResponseCode())) {
                            return false;
                        }
                    }
                }
                if (!isValid) {
                    return false;
                }
            }
            JSONObject jsonObj = getJsonObjectForCmCode(Constants.KYC_VERIFICATION, Constants.KYC_VERIFICATION);
            String ocrEnabled = jsonObj.getString(Constants.OCR_REQUIRED);
            for (AddressDetailsWrapper addressDetailsWrapper : addressDetailsWrapperList) {
                List<AddressDetails> addressDetailsList = addressDetailsWrapper.getAddressDetailsList();
                Address communicationAddress = null;
                Address permanentAddress = null;
                for (AddressDetails addressDetails : addressDetailsList) {
                    AddressDetailsPayload payload = addressDetails.getPayload();
                    List<Address> addressListComm = payload.getAddressList();
                    for (Address address : addressListComm) {
                        if ("Communication".equalsIgnoreCase(address.getAddressType())) {
                            communicationAddress = address;
                            if (("Y".equalsIgnoreCase(ocrEnabled) && "N".equalsIgnoreCase(payload.getCommAddEqPerAdd()))
                                    || ("N".equalsIgnoreCase(ocrEnabled))) {
                                if ("Pincode".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement,
                                            communicationAddress.getPinCode());
                                } else if ("State".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement,
                                            communicationAddress.getState());
                                } else if ("City".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement, communicationAddress.getCity());
                                } else if ("DoorNo".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement,
                                            communicationAddress.getDoorNum());
                                } else if (Constants.ADDRESSLINE1.equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement,
                                            communicationAddress.getAddressLine1());
                                } else if ("AddressLine2".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement,
                                            communicationAddress.getAddressLine2());
                                } else if ("LandMark".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement,
                                            communicationAddress.getLandMark());
                                }
                            }
                            if ("Y".equalsIgnoreCase(ocrEnabled)
                                    && "Y".equalsIgnoreCase(payload.getCommAddEqPerAdd())) {
                                if (Constants.ADDRESSLINE1.equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement,
                                            communicationAddress.getAddressLine1());
                                }
                            }
                        } else if ("Permanent".equalsIgnoreCase(address.getAddressType())) {
                            permanentAddress = address;
                            if ("Y".equalsIgnoreCase(ocrEnabled)) {
                                if ("pAddressLine1".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement,
                                            permanentAddress.getAddressLine1());
                                }
                            }
                            if ("N".equalsIgnoreCase(ocrEnabled)) {
                                if ("pPincode".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement, permanentAddress.getPinCode());
                                } else if ("pState".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement, permanentAddress.getState());
                                } else if ("pCity".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement, permanentAddress.getCity());
                                } else if ("pDoorNo".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement, permanentAddress.getDoorNum());
                                } else if ("pAddressLine1".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement,
                                            permanentAddress.getAddressLine1());
                                } else if ("pAddressLine2".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement,
                                            permanentAddress.getAddressLine2());
                                } else if ("pLandMark".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement, permanentAddress.getLandMark());
                                }
                            }
                        }
                        if (!isValid) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean vaptForFieldsOccupationDtls(List<OccupationDetailsWrapper> occupationDetailsWrapperList,
                                               List<AddressDetailsWrapper> addressDetailsWrapperList, JSONArray stageArray) {
        String fieldName;
        boolean isValid = true;
        for (Object screenElement : stageArray) {
            fieldName = ((String) screenElement).split("~")[0];
            for (OccupationDetailsWrapper occupationDetailsWrapper : occupationDetailsWrapperList) {
                OccupationDetails occupationDetails = occupationDetailsWrapper.getOccupationDetails();
                OccupationDetailsPayload payload = occupationDetails.getPayload();
                if ("OccupationType".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, payload.getOccupationType());
                } else if ("YrsOfExp".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, payload.getExperience());
                } else if ("CurrEmployerName".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, payload.getEmployer());
                } else if ("EmployeeID".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, payload.getEmployeeId());
                } else if ("Designation".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, payload.getDesignation());
                } else if ("CurrEmploymentYrs".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement, payload.getEmployeeSince());
                } else if ("AnnualIncome".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement,
                            payload.getAnnualIncome() == null ? null : payload.getAnnualIncome().toString());
                } else if ("GrossIncome".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement,
                            payload.getGrossIncome() == null ? null : payload.getGrossIncome().toString());
                } else if ("NetTakeHome".equalsIgnoreCase(fieldName)) {
                    isValid = isValidFieldvalue((String) screenElement,
                            payload.getNetTakeHome() == null ? null : payload.getNetTakeHome().toString());
                } else if ("LandLineNum".equalsIgnoreCase(fieldName)) {
                    if (!CommonUtils.validateMobile(payload.getOfficePhone())) {
                        return false;
                    }
                    isValid = isValidFieldvalue((String) screenElement, payload.getOfficePhone());
                } else if ("CompanyEmail".equalsIgnoreCase(fieldName)) {
                    if (!CommonUtils.validateEmailId(payload.getOfficeEmail())) {
                        return false;
                    }
                    isValid = isValidFieldvalue((String) screenElement, payload.getOfficeEmail());
                }
                if (!isValid) {
                    return false;
                }
            }
            Address officeAddress = new Address();
            for (AddressDetailsWrapper addressDetailsWrapper : addressDetailsWrapperList) {
                List<AddressDetails> addressDetailsList = addressDetailsWrapper.getAddressDetailsList();
                for (AddressDetails addressDetails : addressDetailsList) {
                    AddressDetailsPayload addressPayload = addressDetails.getPayload();
                    List<Address> addressListOffice = addressPayload.getAddressList();
                    for (Address address : addressListOffice) {
                        if ("Occupation".equalsIgnoreCase(address.getAddressType())) {
                            officeAddress = address;
                        }
                    }
                    if ("Pincode".equalsIgnoreCase(fieldName)) {
                        isValid = isValidFieldvalue((String) screenElement, officeAddress.getPinCode());
                    } else if ("State".equalsIgnoreCase(fieldName)) {
                        isValid = isValidFieldvalue((String) screenElement, officeAddress.getState());
                    } else if ("City".equalsIgnoreCase(fieldName)) {
                        isValid = isValidFieldvalue((String) screenElement, officeAddress.getCity());
                    } else if (Constants.ADDRESSLINE1.equalsIgnoreCase(fieldName)) {
                        isValid = isValidFieldvalue((String) screenElement, officeAddress.getAddressLine1());
                    } else if ("AddressLine2".equalsIgnoreCase(fieldName)) {
                        isValid = isValidFieldvalue((String) screenElement, officeAddress.getAddressLine2());
                    } else if ("LandMark".equalsIgnoreCase(fieldName)) {
                        isValid = isValidFieldvalue((String) screenElement, officeAddress.getLandMark());
                    }
                    if (!isValid) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean vaptForFieldsNominee(List<NomineeDetailsWrapper> nomineeDetailsWrapperList,
                                        List<AddressDetailsWrapper> addressDetailsWrapperList, JSONArray stageArray, String productGroupCode,
                                        String isExistingCustomer) {
        String fieldName;
        boolean isNomineeMinor = false; // Based on requirement change the default value of this flag.
        boolean isValid = true;
        for (Object screenElement : stageArray) {
            fieldName = ((String) screenElement).split("~")[0];
            for (NomineeDetailsWrapper nomineeDetailsWrapper : nomineeDetailsWrapperList) {
                List<NomineeDetails> nomineeDetailsList = nomineeDetailsWrapper.getNomineeDetailsList();
                for (NomineeDetails nomineeDetails : nomineeDetailsList) {
                    NomineeDetailsPayload nomineePayload = nomineeDetails.getPayload();
                    if ("NomineeName".equalsIgnoreCase(fieldName)) {
                        isValid = isValidFieldvalue(screenElement, nomineePayload.getNomineeName());
                    } else if ("NomineeRelationShip".equalsIgnoreCase(fieldName)) {
                        isValid = isValidFieldvalue(screenElement, nomineePayload.getNomineeRelationship());
                    } else if ("NomineeDOB".equalsIgnoreCase(fieldName)) {
                        isValid = isValidFieldvalue(screenElement, nomineePayload.getNomineeDob());
                    } else if ("NomineeEmail".equalsIgnoreCase(fieldName)) {
                        if (!CommonUtils.validateEmailId(nomineePayload.getNomineeEmail())) {
                            return false;
                        }
                        isValid = isValidFieldvalue(screenElement, nomineePayload.getNomineeEmail());
                    } else if ("NomineePhone".equalsIgnoreCase(fieldName)) {
                        if (!CommonUtils.validateMobile(nomineePayload.getNomineeMobile())) {
                            return false;
                        }
                        isValid = isValidFieldvalue(screenElement, nomineePayload.getNomineeMobile());
                    }
                    isNomineeMinor = isNomineeMinor(nomineePayload.getNomineeDob(), productGroupCode,
                            isExistingCustomer);
                    if (isNomineeMinor) {
                        if ("GuardianName".equalsIgnoreCase(fieldName)) {
                            isValid = isValidFieldvalue(screenElement, nomineePayload.getGuardianName());
                        } else if ("GuardianRelationShip".equalsIgnoreCase(fieldName)) {
                            isValid = isValidFieldvalue(screenElement, nomineePayload.getGuardianRelationship());
                        } else if ("GuardianDOB".equalsIgnoreCase(fieldName)) {
                            isValid = isValidFieldvalue(screenElement, nomineePayload.getGuardianDob());
                        } else if ("GuardianPhone".equalsIgnoreCase(fieldName)) {
                            if (!CommonUtils.validateMobile(nomineePayload.getGuardianMobile())) {
                                return false;
                            }
                            isValid = isValidFieldvalue(screenElement, nomineePayload.getGuardianMobile());
                        } else if ("GuardianEmail".equalsIgnoreCase(fieldName)) {
                            if (!CommonUtils.validateEmailId(nomineePayload.getGuardianEmail())) {
                                return false;
                            }
                            isValid = isValidFieldvalue(screenElement, nomineePayload.getGuardianEmail());
                        }
                    }
                    if (!isValid) {
                        return false;
                    }
                }
            }
            Address nomineeAddress = null;
            Address guardianAddress = null;
            for (AddressDetailsWrapper addressDetailsWrapper : addressDetailsWrapperList) {
                List<AddressDetails> addressDetailsList = addressDetailsWrapper.getAddressDetailsList();
                for (AddressDetails addressDetails : addressDetailsList) {
                    AddressDetailsPayload payload = addressDetails.getPayload();
                    List<Address> addressList = payload.getAddressList();
                    for (Address address : addressList) {
                        if (Constants.NOMINEE.equalsIgnoreCase(address.getAddressType())) {
                            nomineeAddress = address;
                            if ("N".equalsIgnoreCase(payload.getNomineeAddEqPerAdd())) {
                                if ("NomineeState".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue(screenElement, nomineeAddress.getState());
                                } else if ("NomineeCity".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue(screenElement, nomineeAddress.getCity());
                                } else if ("NomineePinCode".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue(screenElement, nomineeAddress.getPinCode());
                                } else if ("NomineeAddressLine1".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue(screenElement, nomineeAddress.getAddressLine1());
                                } else if ("NomineeAddressLine2".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue(screenElement, nomineeAddress.getAddressLine2());
                                } else if ("NomineeLandMark".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue(screenElement, nomineeAddress.getLandMark());
                                } else if ("NomineeDoorNumber".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue(screenElement, nomineeAddress.getDoorNum());
                                }
                            }
                        } else if (Constants.GUARDIAN.equalsIgnoreCase(address.getAddressType())) {
                            if (isNomineeMinor) {
                                guardianAddress = address;
                                if ("N".equalsIgnoreCase(payload.getGuardianAddEqPerAdd())) {
                                    if ("GuardianState".equalsIgnoreCase(fieldName)) {
                                        isValid = isValidFieldvalue(screenElement, guardianAddress.getState());
                                    } else if ("GuardianCity".equalsIgnoreCase(fieldName)) {
                                        isValid = isValidFieldvalue(screenElement, guardianAddress.getCity());
                                    } else if ("GuardianPinCode".equalsIgnoreCase(fieldName)) {
                                        isValid = isValidFieldvalue(screenElement, guardianAddress.getPinCode());
                                    } else if ("GuardianAddressLine1".equalsIgnoreCase(fieldName)) {
                                        isValid = isValidFieldvalue(screenElement, guardianAddress.getAddressLine1());
                                    } else if ("GuardianAddressLine2".equalsIgnoreCase(fieldName)) {
                                        isValid = isValidFieldvalue(screenElement, guardianAddress.getAddressLine2());
                                    } else if ("GuardianLandMark".equalsIgnoreCase(fieldName)) {
                                        isValid = isValidFieldvalue(screenElement, guardianAddress.getLandMark());
                                    } else if ("GuardianDoorNumber".equalsIgnoreCase(fieldName)) {
                                        isValid = isValidFieldvalue(screenElement, guardianAddress.getDoorNum());
                                    }
                                }
                            }
                        }
                        if (!isValid) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean vaptForFieldsNomineeDep(List<NomineeDetailsWrapper> nomineeDetailsWrapperList,
                                           List<AddressDetailsWrapper> addressDetailsWrapperList, JSONArray stageArray, String productGroupCode,
                                           String isExistingCustomer) {
        boolean isNomineeMinor = false; // Based on requirement change the default value of this flag.
        String fieldName;
        boolean isValid = true;
        for (Object screenElement : stageArray) {
            fieldName = ((String) screenElement).split("~")[0];
            for (NomineeDetailsWrapper nomineeDetailsWrapper : nomineeDetailsWrapperList) {
                List<NomineeDetails> nomineeDetailsList = nomineeDetailsWrapper.getNomineeDetailsList();
                for (NomineeDetails nomineeDetails : nomineeDetailsList) {
                    NomineeDetailsPayload nomineePayload = nomineeDetails.getPayload();
                    if ("NomineeName".equalsIgnoreCase(fieldName)) {
                        isValid = isValidFieldvalue((String) screenElement, nomineePayload.getNomineeName());
                    } else if ("NomineeRelationShip".equalsIgnoreCase(fieldName)) {
                        isValid = isValidFieldvalue((String) screenElement, nomineePayload.getNomineeRelationship());
                    } else if ("NomineeDOB".equalsIgnoreCase(fieldName)) {
                        isValid = isValidFieldvalue((String) screenElement, nomineePayload.getNomineeDob());
                    } else if ("NomineeEmail".equalsIgnoreCase(fieldName)) {
                        if (!CommonUtils.validateEmailId(nomineePayload.getNomineeEmail())) {
                            return false;
                        }
                        isValid = isValidFieldvalue((String) screenElement, nomineePayload.getNomineeEmail());
                    } else if ("NomineePhone".equalsIgnoreCase(fieldName)) {
                        if (!CommonUtils.validateMobile(nomineePayload.getNomineeMobile())) {
                            return false;
                        }
                        isValid = isValidFieldvalue((String) screenElement, nomineePayload.getNomineeMobile());
                    }
                    isNomineeMinor = isNomineeMinor(nomineePayload.getNomineeDob(), productGroupCode,
                            isExistingCustomer);
                    if (isNomineeMinor) {
                        if ("GuardianName".equalsIgnoreCase(fieldName)) {
                            isValid = isValidFieldvalue((String) screenElement, nomineePayload.getGuardianName());
                        } else if ("GuardianRelationShip".equalsIgnoreCase(fieldName)) {
                            isValid = isValidFieldvalue((String) screenElement,
                                    nomineePayload.getGuardianRelationship());
                        } else if ("GuardianDOB".equalsIgnoreCase(fieldName)) {
                            isValid = isValidFieldvalue((String) screenElement, nomineePayload.getGuardianDob());
                        } else if ("GuardianPhone".equalsIgnoreCase(fieldName)) {
                            if (!CommonUtils.validateMobile(nomineePayload.getGuardianMobile())) {
                                return false;
                            }
                            isValid = isValidFieldvalue((String) screenElement, nomineePayload.getGuardianMobile());
                        } else if ("GuardianEmail".equalsIgnoreCase(fieldName)) {
                            if (!CommonUtils.validateEmailId(nomineePayload.getGuardianEmail())) {
                                return false;
                            }
                            isValid = isValidFieldvalue((String) screenElement, nomineePayload.getGuardianEmail());
                        }
                    }
                    if (!isValid) {
                        return false;
                    }
                }
            }
            Address nomineeAddress = null;
            Address guardianAddress = null;
            for (AddressDetailsWrapper addressDetailsWrapper : addressDetailsWrapperList) {
                List<AddressDetails> addressDetailsList = addressDetailsWrapper.getAddressDetailsList();
                for (AddressDetails addressDetails : addressDetailsList) {
                    AddressDetailsPayload payload = addressDetails.getPayload();
                    List<Address> addressList = payload.getAddressList();
                    for (Address address : addressList) {
                        if (Constants.NOMINEE.equalsIgnoreCase(address.getAddressType())) {
                            nomineeAddress = address;
                            if ("NomineeState".equalsIgnoreCase(fieldName)) {
                                isValid = isValidFieldvalue((String) screenElement, nomineeAddress.getState());
                            } else if ("NomineeCity".equalsIgnoreCase(fieldName)) {
                                isValid = isValidFieldvalue((String) screenElement, nomineeAddress.getCity());
                            } else if ("NomineePinCode".equalsIgnoreCase(fieldName)) {
                                isValid = isValidFieldvalue((String) screenElement, nomineeAddress.getPinCode());
                            } else if ("NomineeAddressLine1".equalsIgnoreCase(fieldName)) {
                                isValid = isValidFieldvalue((String) screenElement, nomineeAddress.getAddressLine1());
                            } else if ("NomineeAddressLine2".equalsIgnoreCase(fieldName)) {
                                isValid = isValidFieldvalue((String) screenElement, nomineeAddress.getAddressLine2());
                            } else if ("NomineeLandMark".equalsIgnoreCase(fieldName)) {
                                isValid = isValidFieldvalue((String) screenElement, nomineeAddress.getLandMark());
                            } else if ("NomineeDoorNumber".equalsIgnoreCase(fieldName)) {
                                isValid = isValidFieldvalue((String) screenElement, nomineeAddress.getDoorNum());
                            }
                        } else if (Constants.GUARDIAN.equalsIgnoreCase(address.getAddressType())) {
                            if (isNomineeMinor) {
                                guardianAddress = address;
                                if ("GuardianState".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement, guardianAddress.getState());
                                } else if ("GuardianCity".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement, guardianAddress.getCity());
                                } else if ("GuardianPinCode".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement, guardianAddress.getPinCode());
                                } else if ("GuardianAddressLine1".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement,
                                            guardianAddress.getAddressLine1());
                                } else if ("GuardianAddressLine2".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement,
                                            guardianAddress.getAddressLine2());
                                } else if ("GuardianLandMark".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement, guardianAddress.getLandMark());
                                } else if ("GuardianDoorNumber".equalsIgnoreCase(fieldName)) {
                                    isValid = isValidFieldvalue((String) screenElement, guardianAddress.getDoorNum());
                                }
                            }
                        }
                        if (!isValid) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public List<ApplicationTimelineDtl> getApplicationTimelineDtl(String applicationId) {
        ApplicationTimelineDtl timeLineDtl;
        List<ApplicationTimelineDtl> timeLineDtlList = new ArrayList<>();
        List<String> statusList = Arrays.stream(WorkflowStatus.values())
                .map(WorkflowStatus::getValue)
                .collect(Collectors.toList());


        List<ApplicationWorkflow> wfList = applnWfRepository
                .findByApplicationIdAndApplicationStatusInOrderByWorkflowSeqNum(applicationId, statusList);
        boolean rework = false;
        ApplicationWorkflow prevWorkflow = null;
        String stage = "";
        // String prevAction = "";
        for (ApplicationWorkflow workflow : wfList) {
            timeLineDtl = new ApplicationTimelineDtl();
            if (null != workflow.getCreateTs()) {
                timeLineDtl.setTimeStamp(workflow.getCreateTs().format(Constants.FORMATTER));
            }
            if (null != prevWorkflow) {
                stage = prevWorkflow.getNextWorkFlowStage();
            }
            timeLineDtl.setUserId(workflow.getCreatedBy());
            if (WorkflowStatus.INPROGRESS.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.INITIATED_BY.getValue());
                stage = Constants.KM;
            } else if (WorkflowStatus.REJECTED.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.REJECTED_BY.getValue());
                if (Constants.KM.equalsIgnoreCase(stage)) {
                    stage = Constants.BM;
                }
            } else if (WorkflowStatus.PUSHBACK.getValue().equalsIgnoreCase(workflow.getApplicationStatus())
                    || WorkflowStatus.IPUSHBACK.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.PUSHBACK_BY.getValue());
                rework = true;
            } else if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.VERIFIED_BY.getValue());
                stage = Constants.KM;
            } else if (WorkflowStatus.APPROVED.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.APPROVED_BY.getValue());
                stage = Constants.BM;
                if (rework &&
                        (WorkflowStatus.PUSHBACK.getValue().equalsIgnoreCase(prevWorkflow.getApplicationStatus())
                                || WorkflowStatus.IPUSHBACK.getValue().equalsIgnoreCase(prevWorkflow.getApplicationStatus()))) {
                    stage = Constants.KM;
                }
                List<ApplicationMaster> applicationMasterList = applicationMasterRepository.findByAppIdAndApplicationId(Constants.APPID, applicationId);
                if (!applicationMasterList.isEmpty() && applicationMasterList.get(0).getProductCode().equalsIgnoreCase(Constants.RENEWAL_LOAN_PRODUCT_CODE)) {
                    stage = Constants.KM;
                }

            } else if (WorkflowStatus.PENDINGFORRPCVERIFICATION.getValue()
                    .equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.SUBMITTED_FOR_VERIFICATION_BY.getValue());
            } else if (WorkflowStatus.RPCPUSHBACK.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.RPC_PUSHBACK_BY.getValue());
            } else if (WorkflowStatus.RPCVERIFIED.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                if (Constants.PENDINGDEVIATION.equalsIgnoreCase(prevWorkflow.getApplicationStatus())) {
                    timeLineDtl.setActionTaken(WorkflowActions.PUSHBACK_BY.getValue());
                } else if (Constants.PENDINGREASSESSMENT.equalsIgnoreCase(prevWorkflow.getApplicationStatus())) {
                    timeLineDtl.setActionTaken(WorkflowActions.PUSHBACK_BY.getValue());
                } else if (Constants.CACOMPLETED.equalsIgnoreCase(prevWorkflow.getApplicationStatus())) {
                    timeLineDtl.setActionTaken(WorkflowActions.PUSHBACK_BY.getValue());
                } else if (Constants.DB_KIT_STATUS.equalsIgnoreCase(prevWorkflow.getApplicationStatus())) {
                    timeLineDtl.setActionTaken(WorkflowActions.PUSHBACK_BY.getValue());
                } else if (Constants.DBPUSHBACK.equalsIgnoreCase(prevWorkflow.getApplicationStatus())) {
                    timeLineDtl.setActionTaken(WorkflowActions.PUSHBACK_BY.getValue());
                } else if (Constants.RESANCTION.equalsIgnoreCase(prevWorkflow.getApplicationStatus())) {
                    timeLineDtl.setActionTaken(WorkflowActions.PUSHBACK_BY.getValue());
                } else if (Constants.PENDINGPRESANCTION.equalsIgnoreCase(prevWorkflow.getApplicationStatus())) {
                    timeLineDtl.setActionTaken(WorkflowActions.PUSHBACK_BY.getValue());
                } else {
                    timeLineDtl.setActionTaken(WorkflowActions.VERIFIED_BY.getValue());
                }
            } else if (WorkflowStatus.PENDINGDEVIATION.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.CA_BY.getValue());
            } else if (WorkflowStatus.PENDINGREASSESSMENT.getValue()
                    .equalsIgnoreCase(workflow.getApplicationStatus())) {
                if (Constants.PENDINGDEVIATION.equalsIgnoreCase(prevWorkflow.getApplicationStatus())) {
                    timeLineDtl.setActionTaken(WorkflowActions.DEVIATION_APPROVED_BY.getValue());
                } else {
                    timeLineDtl.setActionTaken(WorkflowActions.CA_BY.getValue());
                }
            } else if (WorkflowStatus.CACOMPLETED.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                if (Constants.PENDINGDEVIATION.equalsIgnoreCase(prevWorkflow.getApplicationStatus())) {
                    timeLineDtl.setActionTaken(WorkflowActions.DEVIATION_APPROVED_BY.getValue());
                } else if (Constants.PENDINGREASSESSMENT.equalsIgnoreCase(prevWorkflow.getApplicationStatus())) {
                    timeLineDtl.setActionTaken(WorkflowActions.REASSESSMENT_APPROVED_BY.getValue());
                } else if (Constants.PENDINGPRESANCTION.equalsIgnoreCase(prevWorkflow.getApplicationStatus())) {
                    timeLineDtl.setActionTaken(WorkflowActions.PRESANCTION_APPROVED_BY.getValue());
                } else {
                    timeLineDtl.setActionTaken(WorkflowActions.CA_BY.getValue());
                }
            } else if (WorkflowStatus.CAPUSHBACK.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.PUSHBACK_BY.getValue());
            } else if (WorkflowStatus.SANCTIONED.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                if (WorkflowStatus.CACOMPLETED.getValue().equalsIgnoreCase(prevWorkflow.getApplicationStatus())) {
                    timeLineDtl.setActionTaken(WorkflowActions.SANCTIONED_BY.getValue());
                } else if (WorkflowStatus.RESANCTION.getValue().equalsIgnoreCase(prevWorkflow.getApplicationStatus())) {
                    timeLineDtl.setActionTaken(WorkflowActions.RESANCTIONED_BY.getValue());
                }
            } else if (WorkflowStatus.RESANCTION.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.PUSHBACK_BY.getValue());
            } else if (WorkflowStatus.DBKITGENERATED.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.SUBMITTED_FOR_VERIFICATION_BY.getValue());
            } else if (WorkflowStatus.DBKITVERIFIED.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.DBKITVERIFIEDBY.getValue());
            } else if (WorkflowStatus.DBPUSHBACK.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.PUSHBACK_BY.getValue());
            } else if (WorkflowStatus.DISBURSED.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.DISBURSED_BY.getValue());
            } else if (WorkflowStatus.PENDINGSERVICECALL.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.APPROVED_BY.getValue());
            } else if (WorkflowStatus.RPCBANKUPDATE.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.BANKUPDATESENDBACK.getValue());
            } else if (WorkflowStatus.PENDINGPRESANCTION.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.APPROVED_BY.getValue());
            } else if (WorkflowStatus.LUC.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                if (Constants.PENDINGLUCVERIFICATION.equalsIgnoreCase(prevWorkflow.getApplicationStatus())) {
                    timeLineDtl.setActionTaken(WorkflowActions.PUSHBACK_BY.getValue());
                } else {
                    timeLineDtl.setActionTaken(WorkflowActions.APPROVED_BY.getValue());
                }
            } else if (WorkflowStatus.PENDINGLUCVERIFICATION.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.APPROVED_BY.getValue());
            } else if (WorkflowStatus.LUCVERIFIED.getValue().equalsIgnoreCase(workflow.getApplicationStatus())) {
                timeLineDtl.setActionTaken(WorkflowActions.APPROVED_BY.getValue());
            }

            timeLineDtl.setStage((StringUtils.isNotEmpty(stage)) ? getDisplayStageName(stage) : "");
            prevWorkflow = workflow;
            // fetching remarks from workflow
            if (StringUtils.isNotEmpty(workflow.getRemarks())) {
                if (CommonUtils.verifyQuery(workflow.getRemarks())) {
                    logger.debug("queries found : " + workflow.getRemarks());
                    timeLineDtl
                            .setRpcStatRemaks(CommonUtils.parseRPCStageVerificationData(null, workflow.getRemarks()));
                    logger.debug("queries found - timeLineDtl.getRpcStatRemaks() : " + timeLineDtl.getRpcStatRemaks());
                } else {
                    logger.debug("Queries not found -workflow.getRemarks(): " + workflow.getRemarks());
                    timeLineDtl.setRemarks(workflow.getRemarks());
                }

            }
            logger.debug("timeLineDtl.getRpcStatRemaks() : " + timeLineDtl.getRpcStatRemaks());
            logger.debug("workflow.getRemarks(): " + workflow.getRemarks());
            timeLineDtlList.add(timeLineDtl);
        }
        return timeLineDtlList;
    }

    private String getDisplayStageName(String stage) {

        String stageName = "";
        switch (stage) {
            case Constants.KM:
                stageName = "KM Sourcing";
                break;
            case Constants.BM:
                stageName = "BM Recommendation";
                break;
            case "PENDINGWITHRPCMAKER":
                stageName = Constants.RPCMAKER;
                break;
            case "PENDINGFORRPCCHECKER":
                stageName = Constants.RPCCHECKER;
                break;
            case Constants.PENDINGDEVIATION:
                stageName = "Deviation";
                break;
            case Constants.PENDINGREASSESSMENT:
                stageName = "Reassessment";
                break;
            case Constants.CREDITASSESSMENT:
                stageName = "Credit Assessment";
                break;
            case Constants.SANCTION:
                stageName = "Sanction";
                break;
            case Constants.RESANCTION:
                stageName = "Resanction";
                break;
            case Constants.DBKIT:
                stageName = "Disbursement Kit";
                break;
            case Constants.DBKITVERIFICATION:
                stageName = "Disbursement Kit Verification";
                break;
            case Constants.DISBURSED:
            case Constants.DISBURSEMENT:
                stageName = "Disbursement";
                break;
            case Constants.CBSCHEDULER:
                stageName = "CB Scheduler";
                break;
            case Constants.SERVICECALL:
                stageName = "Service Call";
                break;
            case Constants.LUC:
                stageName = Constants.LUC;
                break;
            case Constants.PENDINGLUCVERIFICATION:
                stageName = "LUC Verification";
                break;
            case Constants.PENDINGPRESANCTION:
                stageName = "Pre Sanction";
                break;
            default:
                break;
        }
        return stageName;
    }

    public void duplicateMasterData(ApplicationMaster appMaster, int newVersionNum) {
        ApplicationMaster appMasterNewObj = new ApplicationMaster();
        BeanUtils.copyProperties(appMaster, appMasterNewObj);
        appMasterNewObj.setVersionNum(newVersionNum);
        appMasterNewObj.setApplicationStatus(AppStatus.INPROGRESS.getValue());
        appMasterNewObj.setCurrentScreenId(null);
        appMasterNewObj.setRemarks(null);
//		appMasterNewObj.setDeclarationFlag(null);
        appMasterRepo.save(appMasterNewObj);
    }

    public NomineeDetails duplicateNomineeData(NomineeDetails nomineeObj, int newVersionNum, BigDecimal newCustDtlId) {
        NomineeDetails nomineeNewObj = new NomineeDetails();
        BeanUtils.copyProperties(nomineeObj, nomineeNewObj);
        nomineeNewObj.setNomineeDtlsId(CommonUtils.generateRandomNum());
        nomineeNewObj.setVersionNum(newVersionNum);
        nomineeNewObj.setCustDtlId(newCustDtlId);
        nomineeDetailsRepository.save(nomineeNewObj);
        return nomineeNewObj;
    }

    public OccupationDetails duplicateOccupationData(OccupationDetails occupationObj, int newVersionNum,
                                                     BigDecimal newCustDtlId) {
        OccupationDetails occupationNewObj = new OccupationDetails();
        BeanUtils.copyProperties(occupationObj, occupationNewObj);
        occupationNewObj.setOccptDtlId(CommonUtils.generateRandomNum());
        occupationNewObj.setVersionNum(newVersionNum);
        occupationNewObj.setCustDtlId(newCustDtlId);
        occupationDetailsRepository.save(occupationNewObj);
        return occupationNewObj;
    }

    public CustomerDetails duplicateCustomerData(CustomerDetails custObj, int newVersionNum, BigDecimal newCustDtlId) {
        CustomerDetails custNewObj = new CustomerDetails();
        BeanUtils.copyProperties(custObj, custNewObj);
        custNewObj.setCustDtlId(newCustDtlId);
        custNewObj.setVersionNum(newVersionNum);
        custDtlRepo.save(custNewObj);
        return custNewObj;
    }

    public Response fetchFaq(FetchFaqRequest apiRequest) {
        Gson gson = new Gson();
        FetchFaqRequestFields requestObj = apiRequest.getRequestObj();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        response.setResponseHeader(responseHeader);
        List<FaqDetails> faqList = faqRepository.findByAppIdAndProductAndStageOrderBySeqNumAsc(apiRequest.getAppId(),
                requestObj.getProduct(), requestObj.getStage());
        String responseStr = gson.toJson(faqList);
        responseBody.setResponseObj(responseStr);
        response.setResponseBody(responseBody);
        return response;
    }

    public void duplicateWf(String applicationId, String appId, int oldVersionNum, int newVersionNum) {
        Optional<ApplicationWorkflow> workflow = applnWfRepository
                .findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(appId, applicationId,
                        oldVersionNum);
        if (workflow.isPresent()) {
            ApplicationWorkflow workFlowObj = workflow.get();
            ApplicationWorkflow workFlowNewObj = new ApplicationWorkflow();
            duplicateWfData(workFlowNewObj, workFlowObj, newVersionNum);
        }
    }

    private void duplicateWfData(ApplicationWorkflow workFlowNewObj, ApplicationWorkflow workFlowObj,
                                 int newVersionNum) {
        BeanUtils.copyProperties(workFlowObj, workFlowNewObj);
        workFlowNewObj.setVersionNum(newVersionNum);
        workFlowNewObj.setWorkflowSeqNum(Constants.INITIAL_VERSION_NO);
        applnWfRepository.save(workFlowNewObj);
    }

    public void duplicateDocsData(ApplicationDocuments docNewObj, ApplicationDocuments docObj, int newVersionNum,
                                  BigDecimal newCustDtlId) {
        ApplicationDocuments docNewObj1 = new ApplicationDocuments();
        BeanUtils.copyProperties(docObj, docNewObj1);
        docNewObj1.setAppDocId(CommonUtils.generateRandomNum());
        docNewObj1.setVersionNum(newVersionNum);
        docNewObj1.setCustDtlId(newCustDtlId);
        applicationDocumentsRepository.save(docNewObj1);
    }

    public boolean isThisLastStage(String currentScreenId, JSONArray array) {
        if (null != array) {
            List<Object> list = array.toList();
            String element;
            for (int i = 0; i < list.size(); i++) {
                element = (String) list.get(i);
                list.remove(i);
                list.add(i, element.split("~")[0]);
            }
            if (list.indexOf(Constants.FUND_ACCOUNT) > list.indexOf(currentScreenId)) {
                list.remove(Constants.ACCOUNT_CREATION); // Current screen id will be never be sent as ACCOUNTCREATION
                // in request. ACCOUNTCREATION is backend stage.
                list.remove(Constants.FUND_ACCOUNT); // Customer can close the application in FUNDACCOUNT stage if its
                // the last stage.
            } else {
                list.remove(Constants.ACCOUNT_CREATION); // Current screen id will be never be sent as ACCOUNTCREATION
                // in request. ACCOUNTCREATION is backend stage.
            }
            if (list.indexOf(currentScreenId) == (list.size() - 1)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAccountCreationisNextStage(String currentScreenId, JSONArray array) {
        if (null != array) {
            List<Object> list = array.toList();
            String element;
            for (int i = 0; i < list.size(); i++) {
                element = (String) list.get(i);
                list.remove(i);
                list.add(i, element.split("~")[0]);
            }
            int currentScreenIdIndex = list.indexOf(currentScreenId);
            if (currentScreenIdIndex < (list.size() - 1)) {
                String nextScreenID = (String) list.get(currentScreenIdIndex + 1);
                String[] nextScreenIdArray = nextScreenID.split("~");
                if (Constants.ACCOUNT_CREATION.equalsIgnoreCase(nextScreenIdArray[0])) {
                    return true;
                }
            }
        }
        return false;
    }

    public void createAccountInCbsForNonStp(boolean isSelfOnBoardingHeaderAppId, ApplicationMaster masterObj) {
        if (!isSelfOnBoardingHeaderAppId) { // INITIATOR submits it after review.
            masterObj.setApplicationStatus(AppStatus.PENDING.getValue());
            appMasterRepo.save(masterObj);
        }
    }

    public Response getReportResponse(Map<String, Object> param, String reportPath) {
        Response response;
        for (int i = 1; i <= 2; i++) {
            param.put("p" + i, "Page " + i + " of 2");
        }
        List<JasperPrint> jasper = new ArrayList<>();
        try {
            JasperPrint jasperPrint1 = JasperFillManager.fillReport(
                    ResourceUtils.getFile(reportPath + "Page_1.jasper").getAbsolutePath(), param,
                    new JREmptyDataSource());
            jasper.add(jasperPrint1);

            JasperPrint jasperPrint2 = JasperFillManager.fillReport(
                    ResourceUtils.getFile(reportPath + "Page_2.jasper").getAbsolutePath(), param,
                    new JREmptyDataSource());
            jasper.add(jasperPrint2);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(SimpleExporterInput.getInstance(jasper));
        ByteArrayOutputStream pdfReportStream = new ByteArrayOutputStream();
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfReportStream));
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        configuration.setCreatingBatchModeBookmarks(true);
        exporter.setConfiguration(configuration);
        try {
            exporter.exportReport();
            logger.debug("Exporter ends");
            String base64String = Base64.getEncoder().encodeToString(pdfReportStream.toByteArray());
            response = getSuccessJson(base64String);
            logger.info("PDF Report Generated");
        } catch (JRException e) {
            response = getFailureJson(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        logger.debug("generatePdfService Function end");
        return response;
    }

    public Response getSuccessJson(String baseString) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();

        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        responseBody.setResponseObj(baseString);
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    public Response getFailureJson(String error) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();

        responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
        responseBody.setResponseObj(error);
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    public void putAddressDtlsBasedOnType(Map<String, Object> param, Address address) {
        String addressValue = address.getAddressLine1() + " " + address.getAddressLine2() + " " + address.getCity()
                + " " + address.getPinCode() + " " + address.getState();
        if (address.getAddressType().equalsIgnoreCase("Permanent")) {
            putIntoMap(param, "permAddProofType0", address.getAddressType(), "permAddress0", addressValue,
                    "permLandmark0", address.getLandMark());
        }
        if (address.getAddressType().equalsIgnoreCase("Communication")) {
            putIntoMap(param, "presAddProofType0", address.getAddressType(), "presentAddress0", addressValue,
                    "persentLandmark0", address.getLandMark());
        }
        if (address.getAddressType().equalsIgnoreCase("Occupation")) {
            putIntoMap(param, "officeAddProofType0", address.getAddressType(), "officeAddress0", addressValue,
                    "officeLandmark0", address.getLandMark());
        }
        if (address.getAddressType().equalsIgnoreCase(Constants.NOMINEE)) {
            putIntoMap(param, "nomineeAddProofType0", address.getAddressType(), "nomineeAddress0", addressValue,
                    "nomineeLandmark0", address.getLandMark());
        }
        if (address.getAddressType().equalsIgnoreCase(Constants.GUARDIAN)) {
            putIntoMap(param, "guardianAddProofType0", address.getAddressType(), "guardianAddress0", addressValue,
                    "guardianLandmark0", address.getLandMark());
        }
    }

    private void putIntoMap(Map<String, Object> param, String addressTypeKey, String addressTypeValue,
                            String addressKey, String addressValue, String landMarKey, String landMarkValue) {
        putIntoMap(param, addressTypeKey, addressTypeValue);
        putIntoMap(param, addressKey, addressValue);
        putIntoMap(param, landMarKey, landMarkValue);
    }

    private void putIntoMap(Map<String, Object> param, String key, String value) {
        param.put(key, value);
    }

    public void putLogoAndMaster(Map<String, Object> param, ApplicationMaster applicationMaster, int applicantsCount,
                                 String imagePath) throws FileNotFoundException {
        String logo = ResourceUtils.getFile(imagePath + "logo.png").getAbsolutePath();
        String checkbox = ResourceUtils.getFile(imagePath + "checkbox.png").getAbsolutePath();
        String checkboxUnselected = ResourceUtils.getFile(imagePath + "checkboxUnselected.png").getAbsolutePath();
        String radioSelected = ResourceUtils.getFile(imagePath + "radioSelected.png").getAbsolutePath();
        String radioUnselected = ResourceUtils.getFile(imagePath + "radioUnselected.png").getAbsolutePath();
        param.put("logo", logo);
        param.put("check", checkbox);
        param.put("checkNot", checkboxUnselected);
        param.put("radioSelect", radioSelected);
        param.put("radioNotSelect", radioUnselected);
        if (applicantsCount > 1) {
            param.put("applicationId1", applicationMaster.getApplicationId());
            param.put("applicationId", applicationMaster.getApplicationId());
            param.put("check1", checkbox);
        } else {
            param.put("check1", checkbox);
        }
        param.put("applicationId", applicationMaster.getApplicationId());
        param.put("applicantType0", applicationMaster.getApplicationType());
        param.put("applicationDate",
                applicationMaster.getApplicationDate() != null ? applicationMaster.getApplicationDate().toString()
                        : " ");
        String applicationDate = applicationMaster.getApplicationDate().toString();
        String[] splitApplicationDate = applicationDate.split("-");
        String month = new DateFormatSymbols().getMonths()[Integer.parseInt(splitApplicationDate[1]) - 1].substring(0,
                3);
        param.put("date1", "(" + splitApplicationDate[2] + " " + month + " " + splitApplicationDate[0] + " )");
        param.put("fatcaDate", splitApplicationDate[2] + " " + month + " " + splitApplicationDate[1]);
    }

    public void putCustomerDtls(Map<String, Object> param, List<CustomerDetails> custDetails, int applicantsCount) {
        if (!custDetails.isEmpty()) {
            for (int i = 0; i < custDetails.size(); i++) {
                CustomerDetails customerDetails = custDetails.get(i);
                param.put("customerId" + i,
                        customerDetails.getCustomerId() != null ? customerDetails.getCustomerId().toString() : "");
                param.put("customerType" + i, customerDetails.getCustomerType());
                if (applicantsCount == 2) {
                    param.put("applicantName" + i, customerDetails.getCustomerName());
                } else {
                    param.put("applicantName0", customerDetails.getCustomerName());
                }
                param.put("mobileNo" + i, customerDetails.getMobileNumber());
                Gson gsonObj = new Gson();
                putCustomerDtlsPayload(param,
                        gsonObj.fromJson(customerDetails.getPayloadColumn(), CustomerDetailsPayload.class), i);
            }
        }
    }

    private void putCustomerDtlsPayload(Map<String, Object> param, CustomerDetailsPayload custPayload, int i) {
        if (custPayload != null) {
            param.put("gender" + i, custPayload.getGender());
            param.put("f/sName" + i, custPayload.getSpouseName());
            param.put("panNo" + i, custPayload.getPan().toUpperCase());
            param.put("dateOfBirth" + i, custPayload.getDob());
            param.put("email" + i, custPayload.getEmailId());
            param.put("altMobileNo" + i, custPayload.getAltMobileNumber());
            if (custPayload.getMaritalStatus().equalsIgnoreCase("s")) {
                param.put("maritalStatus" + i, "Single");
            } else {
                param.put("maritalStatus" + i, "Married");
            }
        }
    }

    public void putProfessionDtls(Map<String, Object> param,
                                  List<OccupationDetailsWrapper> occupationDetailsWrapperList) {
        if (!occupationDetailsWrapperList.isEmpty()) {
            for (int i = 0; i < occupationDetailsWrapperList.size(); i++) {
                OccupationDetails occupationDetails = occupationDetailsWrapperList.get(i).getOccupationDetails();
                Gson gsonObj = new Gson();
                putProfessionDtlsPayload(param,
                        gsonObj.fromJson(occupationDetails.getPayloadColumn(), OccupationDetailsPayload.class), i);
            }
        }
    }

    private void putProfessionDtlsPayload(Map<String, Object> param, OccupationDetailsPayload occupationPayload,
                                          int i) {
        if (occupationPayload != null) {
            param.put("companyType" + i,
                    occupationPayload.getOccupationType().equals("") ? "-" : occupationPayload.getOccupationType());
            param.put("nameOfCompany" + i, occupationPayload.getEmployer());
            String exp = occupationPayload.getExperience();
            if (exp.length() > 0) {
                param.put("expCurrEmp" + i, exp);
            } else {
                param.put("expCurrEmp" + i, "0 Years " + "0 Months");
            }
            param.put("officePhone" + i, occupationPayload.getOfficePhone());
            param.put("officeEmail" + i, occupationPayload.getOfficeEmail());
            param.put("designationEdu" + i, occupationPayload.getDesignation());
            param.put("annualIncome" + i, occupationPayload.getAnnualIncome() != null
                    ? CommonUtils.formatAmount(Double.parseDouble(occupationPayload.getAnnualIncome().toString()))
                    : "");
            param.put("employeeId" + i, occupationPayload.getEmployeeId());
            param.put("employeeSince" + i, occupationPayload.getEmployeeSince());
            param.put("grossIncome" + i, occupationPayload.getGrossIncome() != null
                    ? CommonUtils.formatAmount(Double.parseDouble(occupationPayload.getGrossIncome().toString()))
                    : "");
            param.put("netTakeHome" + i, occupationPayload.getNetTakeHome() != null
                    ? CommonUtils.formatAmount(Double.parseDouble(occupationPayload.getNetTakeHome().toString()))
                    : "");
        }
    }

    public void putAddressDtls(Map<String, Object> param, List<AddressDetailsWrapper> addressDetailsWrapperList) {
        if (!addressDetailsWrapperList.isEmpty()) {
            for (AddressDetailsWrapper addressDetailsWrapper : addressDetailsWrapperList) {
                List<AddressDetails> addressList = addressDetailsWrapper.getAddressDetailsList();
                for (AddressDetails addressDetails : addressList) {
                    Gson gsonObj = new Gson();
                    AddressDetailsPayload addressPayload = gsonObj.fromJson(addressDetails.getPayloadColumn(),
                            AddressDetailsPayload.class);
                    if (addressPayload != null) {
                        List<Address> addresses = addressPayload.getAddressList();
                        for (Address address : addresses) {
                            putAddressDtlsBasedOnType(param, address);
                        }
                    }
                }
            }
        }
    }

    public void putDepositDtls(Map<String, Object> param, DepositDtls depositDetails) {
        if (depositDetails != null) {
            param.put("depositAmount",
                    depositDetails.getDepositAmount() != null
                            ? CommonUtils.formatAmount(Double.parseDouble(depositDetails.getDepositAmount().toString()))
                            : "");
            param.put("tenureInMonths",
                    depositDetails.getTenureInMonths() != null ? String.valueOf(depositDetails.getTenureInMonths())
                            : "");
            param.put("tenureInDays",
                    depositDetails.getTenureInDays() != null ? String.valueOf(depositDetails.getTenureInDays()) : "");
            param.put("tenureInYears",
                    depositDetails.getTenureInYears() != null ? String.valueOf(depositDetails.getTenureInYears()) : "");
            param.put("roi",
                    String.valueOf(depositDetails.getRoi()) != null ? String.valueOf(depositDetails.getRoi()) : "");
            param.put("interest",
                    String.valueOf(depositDetails.getInterest()) != null
                            ? CommonUtils.formatAmount(Double.parseDouble(String.valueOf(depositDetails.getInterest())))
                            : "");
            param.put("maturityDate",
                    depositDetails.getMaturityDate() != null ? depositDetails.getMaturityDate().toString() : "");
            param.put("maturityAmount", depositDetails.getMaturityAmount() != null
                    ? CommonUtils.formatAmount(Double.parseDouble(depositDetails.getMaturityAmount().toString()))
                    : "");
            param.put("autopayEnabled", depositDetails.getAutopayEnabled());
            param.put("autopaySrcAccount", depositDetails.getAutopaySrcAccount());
            param.put("autopaySrcAccountType", depositDetails.getAutopaySrcAccountType());
            param.put("autopayDate", depositDetails.getAutopayDate());
            param.put("maturityInstn", depositDetails.getMaturityInstn());
            param.put("payoutAccount", depositDetails.getPayoutAccount());
            param.put("payoutAccountType", depositDetails.getPayoutAccountType());
            param.put("initialFundAccount", depositDetails.getInitialFundAccount());
            param.put("initialFundAccountType", depositDetails.getInitialFundAccountType());
        }
    }

    public void putNomineeDtls(Map<String, Object> param, List<NomineeDetailsWrapper> nomineeDetailsWrapperList) {
        if (!nomineeDetailsWrapperList.isEmpty()) {
            for (NomineeDetailsWrapper nomineeDetailsWrapper : nomineeDetailsWrapperList) {
                List<NomineeDetails> nomineeDetailsList = nomineeDetailsWrapper.getNomineeDetailsList();
                if (!nomineeDetailsList.isEmpty()) {
                    AtomicInteger i = new AtomicInteger();
                    nomineeDetailsList.forEach(nomineeDetails -> {
                        Gson gsonObj = new Gson();
                        NomineeDetailsPayload nomineePayload = gsonObj.fromJson(nomineeDetails.getPayloadColumn(),
                                NomineeDetailsPayload.class);
                        if (nomineePayload != null) {
                            param.put("nomineeName" + i, nomineePayload.getNomineeName());
                            param.put("nomineeDOB" + i, nomineePayload.getNomineeDob());
                            param.put("nomineeRelationship" + i, nomineePayload.getNomineeRelationship());
                            param.put("nomineeMobile" + i, nomineePayload.getNomineeMobile());
                            param.put("nomineeEmail" + i, nomineePayload.getNomineeEmail());
                            param.put("guardianName" + i, nomineePayload.getGuardianName());
                            param.put("guardianDOB" + i, nomineePayload.getGuardianDob());
                            param.put("guardianRelationship" + i, nomineePayload.getGuardianRelationship());
                            param.put("guardianMobile" + i, nomineePayload.getGuardianMobile());
                            param.put("guardianEmail" + i, nomineePayload.getGuardianEmail());
                            i.getAndIncrement();
                        }
                    });
                }
            }
        }
    }

    public void putLoanDtls(Map<String, Object> param, LoanDetails loanDetails) {
        if (loanDetails != null) {
            param.put("loanAmount",
                    loanDetails.getLoanAmount() != null
                            ? CommonUtils.formatAmount(Double.parseDouble(loanDetails.getLoanAmount().toString()))
                            : "");
            param.put("tenureInMonths",
                    loanDetails.getTenureInMonths() != null ? String.valueOf(loanDetails.getTenureInMonths()) : "");
            param.put("tenure", loanDetails.getTenure() != null ? String.valueOf(loanDetails.getTenure()) : "");
            param.put("roi", String.valueOf(loanDetails.getRoi()) != null ? String.valueOf(loanDetails.getRoi()) : "");
            param.put("interest",
                    String.valueOf(loanDetails.getInterest()) != null
                            ? CommonUtils.formatAmount(Double.parseDouble(String.valueOf(loanDetails.getInterest())))
                            : "");
            param.put("loanClosureDate",
                    loanDetails.getLoanClosureDate() != null ? loanDetails.getLoanClosureDate().toString() : "");
            param.put("totPayableAmount",
                    loanDetails.getTotPayableAmount() != null
                            ? CommonUtils.formatAmount(Double.parseDouble(loanDetails.getTotPayableAmount().toString()))
                            : "");
            param.put("autoEmiAccount", loanDetails.getAutoEmiAccount());
            param.put("autoEmiAccountType", loanDetails.getAutoEmiAccountType());
            param.put("emiDate", loanDetails.getEmiDate() != null ? loanDetails.getEmiDate() : "");
            param.put("loanCrAccount", loanDetails.getLoanCrAccount());
            param.put("loanCrAccountType", loanDetails.getLoanCrAccountType());
            param.put("monthlyEmi",
                    loanDetails.getMonthlyEmi() != null
                            ? CommonUtils.formatAmount(Double.parseDouble(loanDetails.getMonthlyEmi().toString()))
                            : "");
        }
    }

    public BigDecimal getCustDtlId(ApplicationMaster applicationMaster) {
        if (applicationMaster.getCustDtlId() == null) {
            return CommonUtils.generateRandomNum();
        } else { // this ID should be created once only.
            return applicationMaster.getCustDtlId();
        }
    }

    public BigDecimal generateCustDtlId(String applicationID, String customerType) {
        logger.debug("applicationID : " + applicationID.toString());
        logger.debug("customerType : " + customerType.toString());
        Optional<CustomerDetails> customerDetails = custDtlRepo
                .findByApplicationIdAndAppIdAndCustomerType(applicationID, Constants.APPID, customerType);
        logger.debug("customerDetails : " + customerDetails.toString());
        if (customerDetails.isPresent()) {
            return customerDetails.get().getCustDtlId();
        } else {
            return CommonUtils.generateRandomNum();
        }
    }

    // -- ALL FALLBACK METHODS

    private String fetchRoleIdFallback(String appId, String userId, Exception e) {
        logger.error("fetchRoleIdFallback error : ", e);
        return "";
    }

    private Response populateApplnWorkFlowFallback(PopulateapplnWFRequest request, Exception e) {
        logger.error("populateApplnWorkFlowFallback error : ", e);
        return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
    }

}
