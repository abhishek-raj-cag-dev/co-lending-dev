package com.iexceed.appzillonbanking.cob.loans.service;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.text.html.HTML;

import com.iexceed.appzillonbanking.cob.entity.DmsRequestExt;
import com.iexceed.appzillonbanking.cob.entity.ngoAddDoc.NGOAddDocumentBDO;
import com.iexceed.appzillonbanking.cob.entity.ngoAddFolder.Folder;
import com.iexceed.appzillonbanking.cob.entity.ngoAddFolder.InputData;
import com.iexceed.appzillonbanking.cob.entity.ngoAddFolder.NGOAddFolderInput;
import com.iexceed.appzillonbanking.cob.entity.ngoAddFolder.NGOExecuteAPIBDORequest;
import com.iexceed.appzillonbanking.cob.entity.ngoConnectCabinet.NGOConnectCabinetInput;
import com.iexceed.appzillonbanking.cob.entity.ngoConnectCabinet.NGOExecuteAPIBDO;
import com.iexceed.appzillonbanking.cob.entity.ngoDisconnectCabinet.NGODisconnectCabinetInput;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
import com.iexceed.appzillonbanking.cob.core.repository.ab.*;
import com.iexceed.appzillonbanking.cob.core.utils.*;
import com.iexceed.appzillonbanking.cob.domain.ab.WhitelistedBranches;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedCDHLead;
import com.iexceed.appzillonbanking.cob.loans.payload.*;
import com.iexceed.appzillonbanking.cob.loans.payload.BIPRequestWrapper.BIPMasterRequest;

import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiIexceedCDHLeadRepo;
import com.iexceed.appzillonbanking.cob.loans.payload.CheckApplicationRequest;
import com.iexceed.appzillonbanking.cob.payload.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iexceed.appzillonbanking.cob.core.payload.Address;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.ApplicationDocumentsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.ApplicationDocumentsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CheckApplicationRes;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerIdentificationCasa;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerIdentificationLoan;
import com.iexceed.appzillonbanking.cob.core.payload.ExistingLoanDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.payload.InsuranceDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.InsuranceDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.LoanDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.OccupationDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.OccupationDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.PopulateapplnWFRequest;
import com.iexceed.appzillonbanking.cob.core.payload.PopulateapplnWFRequestFields;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.WorkFlowDetails;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.core.services.InterfaceAdapter;
import com.iexceed.appzillonbanking.cob.core.services.ResponseParser;
import com.iexceed.appzillonbanking.cob.core.services.SoapInterfaceParser;
import com.iexceed.appzillonbanking.cob.domain.ab.LovMaster;
import com.iexceed.appzillonbanking.cob.domain.ab.RoleAccessMap;
//import com.iexceed.appzillonbanking.cob.loans.payload.DmsRequestExt;
import com.iexceed.appzillonbanking.cob.loans.payload.UploadLoanRequestFields.DBKITResponse;
import com.iexceed.appzillonbanking.cob.loans.report.LoanReport;
import com.iexceed.appzillonbanking.cob.loans.repository.user.TbUserRepository;
import com.iexceed.appzillonbanking.cob.nesl.domain.ab.Enach;
import com.iexceed.appzillonbanking.cob.nesl.repository.ab.EnachRepository;
import com.iexceed.appzillonbanking.cob.report.LoanApplication;
import com.iexceed.appzillonbanking.cob.repository.ab.LovMasterRepository;
import com.iexceed.appzillonbanking.cob.repository.ab.WhitelistedBranchesRepository;
import com.iexceed.appzillonbanking.cob.service.COBService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

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
    private LeadDetailsRepository leadDtlsRepo;

    @Autowired
    private RenewalLeadDetailsRepository renewalLeadDtlsRepo;

    @Autowired
    private CommonParamService commonParamService;

    @Autowired
    private LoanReport report;

    @Autowired
    private WorkflowDefinitionRepository wfDefnRepoLn;

    @Autowired
    private COBService cobService;

    @Autowired
    private RenewalLeadOccpInsDetailsRepository renewalLeadOccpInsDetailsRepo;

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

    private String versionHm = "versionHm";
    private String headerHm = "headerHm";
    private String applicationIDHm = "applicationIDHm";
    private String propHm = "propHm";
    private int loanDetailsLovId = 29;

    private String requestLog;
    @Autowired
    private DocumentsRepository documentsRepository;
    @Autowired
    private ApplicationMasterRepository2 applicationMasterRepository2;

    public String getRequestLog() {
        return requestLog;
    }

    public void setRequestLog(String requestLog) {
        this.requestLog = requestLog;
    }

    DateTimeFormatter localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat inFormat = new SimpleDateFormat("dd/MM/yyyy");

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

    public Mono<Object> breCBCheck(BRECBRequest brecbRequest, Header header, Properties prop) {
        final String loanId;
        final String applicantType;
        final String userId;
        logger.debug("request from the BRECBCheck API: {} ", brecbRequest.toString());
        Gson gson = new Gson();
        Map<String, Object> combinedRequest = new HashMap<>();
        combinedRequest.put("brecbRequest", brecbRequest);
        combinedRequest.put("header", header);
        BRECBCheckRequestExt CBCheckRequestExt = new BRECBCheckRequestExt();
        CBCheckRequestExt.setAppId(brecbRequest.getAppId());
        CBCheckRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.BRE_CB_CHECK_INTF.getKey()));

        BRECBRequestFields breRequest = brecbRequest.getRequestObj();
        BRECBInputRequest2 input = breRequest.getBreCBValuesRequestvalues1().getBreCBInputRequestinput1()
                .getBreCBValuesRequestvalues2().getBreCBInputRequestinput2();

        BREApplicant breApplicant = input.getApplicant();
        String appnId = breApplicant.getAppId();
        String branchId = breApplicant.getBranch();

        String productCode = applicationMasterRepository2
                .findApplicationProductCode(brecbRequest.getAppId(), appnId);
        logger.debug("Product code for the application is: {}", productCode);
        if (StringUtils.isNotBlank(productCode) && productCode.equalsIgnoreCase(Constants.RENEWAL_LOAN_PRODUCT_CODE)) {
            logger.debug("Renewal loan product code found, setting Unnati Renewal Flag to Y");
            breApplicant.setUnnatiRenewalFlag("Y");
        } else {
            breApplicant.setUnnatiRenewalFlag("N");
        }
        String loanAmount = breApplicant.getLoanAmount();

        boolean invalidLoanAmount = StringUtils.isBlank(loanAmount)
                || "NaN".equalsIgnoreCase(loanAmount)
                || !NumberUtils.isCreatable(loanAmount);
        if (invalidLoanAmount) {
            LoanDetails loanDetails = loanDtlsRepo.findByApplicationId(breApplicant.getAppId());
            if (loanDetails != null) {
                BigDecimal appliedLoanAmount = loanDetails.getLoanAmount();
                BigDecimal bmRecommendedLoanAmount = loanDetails.getBmRecommendedLoanAmount();
                BigDecimal sanctionedLoanAmount = loanDetails.getSanctionedLoanAmount();
                BigDecimal minAmount = Stream.of(appliedLoanAmount, bmRecommendedLoanAmount, sanctionedLoanAmount)
                        .filter(Objects::nonNull)
                        .min(Comparator.naturalOrder())
                        .orElse(BigDecimal.ZERO);
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
            if (breApplicant.getDocumentDetails() == null
                    || breApplicant.getDocumentDetails().isEmpty()
                    || breApplicant.getDocumentDetails().get(0) == null) {
                throw new IllegalStateException("Document details missing");
            }
            userId = breApplicant.getDocumentDetails().get(0).getDocId();
        } else {
            loanId = "C01" + breApplicant.getAppId();
            applicantType = Constants.COAPPLICANT;
            if (input.getHouseholdMember() == null
                    || input.getHouseholdMember().isEmpty()
                    || input.getHouseholdMember().get(0) == null) {
                throw new IllegalStateException("Document details missing");
            }
            userId = input.getHouseholdMember().get(0).getDocumentDetails().get(0).getDocId();
        }
        breApplicant.setLoanId(loanId);
        String breCheckReq = gson.toJson(combinedRequest);
        logger.debug("Combined request JSON: {}", breCheckReq);

        Optional<WhitelistedBranches> whitelistedBranchOpt =
                whitelistedBranchesRepository.findByBranchCode(branchId);
        Optional<BipDetails> bipDetailsOpt =
                bipDetailsRepository.findByApplicationId(breApplicant.getAppId());
        BREBusinessImageAssessment breBusinessImageAssessment = null;
        if (bipDetailsOpt.isPresent()
                && whitelistedBranchOpt.isPresent()
                && "Y".equalsIgnoreCase(whitelistedBranchOpt.get().getBotApiEnabled())) {
            logger.debug("BIPDetails is present in DB");
            BipDetails details = bipDetailsOpt.get();
            String payload = details.getPayload();
            logger.debug("BIPDetails Payload : {}", payload);
            BREBusinessImageWrapper wrapper = new Gson()
                    .fromJson(payload, BREBusinessImageWrapper.class);
            breBusinessImageAssessment = wrapper.getBusinessImageAssessment();
        } else {
            logger.debug("Using default BusinessImageAssessment (null or empty)");
        }
        input.setBusinessImageAssessment(breBusinessImageAssessment);
        logger.debug("Input after setting BusinessImageAssessment: {}", input);

        String interfaceName = CBCheckRequestExt.getInterfaceName();

        if (productCode.equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)) {
            interfaceName = prop.getProperty(CobFlagsProperties.OPEN_MARKET_BRE_INTF.getKey());
            Integer weeklyTerm = input.getApplicant().getTerm() * 52;
            input.getApplicant().setAppliedTermWeeks(weeklyTerm);

            Optional<UnnatiIexceedCDHLead> openMarketLeadOpt = unnatiIexceedCDHLeadRepo.findByCustomerId(input.getApplicant().getCustId());
            if (openMarketLeadOpt.isPresent()) {
                input.getApplicant().setKendraActivationDate(String.valueOf(openMarketLeadOpt.get().getKendraActDate()));
            } else {
                logger.debug("Lead details not present for the customerId : {}", input.getApplicant().getCustId());
                return Mono.just(adapterUtil.setError("Lead details not present for this customer. Please verify", "1"));
            }
        }

        CBCheckRequestExt.setRequestObj(brecbRequest.getRequestObj());
        logger.debug("CBCheckRequestExt from the API: {} ", CBCheckRequestExt.toString());
        Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, CBCheckRequestExt,
                interfaceName);

        // A
        String finalLoanAmount = loanAmount;
        return apiRespMono.flatMap(val -> {
            logger.debug("response 2 from the API: {} ", val);
            JSONObject resp = null;
            JSONObject apiResp = new JSONObject(new Gson().toJson(val));
            logger.debug("JSON response 3 from the API: {} ", apiResp);

            CibilDetails cblDetails = new CibilDetails();
            CibilDetailsPayload cblPayLoad = new CibilDetailsPayload();
            LocalDate oldCbDate = null;

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
                    return Mono.just(adapterUtil.setError(
                            "No customer details found for the given applicationId and customerType", "1"));
                }
                int previousRetryAttempt = 0;
                // check for existing record
                Optional<CibilDetails> cblDetailsExtg = cibilDtlRepo.findByApplicationIdAndAppIdAndCustDtlId(
                        input.getApplicant().getAppId(), brecbRequest.getAppId(),
                        customerDetails.get().getCustDtlId());

                if (cblDetailsExtg.isPresent()) {
                    /*
                     * If present delete the original record from CibilDetails and move it to
                     * CibilDetailsHistory table and then insert as new record in CibilDetails
                     */

                    CibilDetails cibilDetails = cblDetailsExtg.get();
                    previousRetryAttempt = gson.fromJson(cibilDetails.getPayloadColumn(), CibilDetailsPayload.class).getRetryAttempts();

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
                    oldCbDate = cblHistory.getCbDate();
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
                }
                cblDetails.setAdditionalInfo(gson.toJson(addInfo));

                // Set status
                if (apiResp.getString("Final_Decision").toLowerCase().indexOf("approved".toLowerCase()) != -1) {
                    cblDetails.setCbStatus("PASS"); // Pass case
                } else { // Reject case
                    cblDetails.setCbStatus("FAIL");
                    cblPayLoad.setRejectionReason(apiResp.optString("Rejection_reason"));
                }
                cblPayLoad.setFlowResponse(apiResp.optString("flow_response"));

                cblPayLoad.setIrisMessage(apiResp.getString("IRIS_message"));
                String normalizedIrisMsg = cblPayLoad.getIrisMessage()
                        .replaceAll("[–—]", "-") // replace en-dash/em-dash with hyphen
                        .toLowerCase();
                String irisTempErrorMsgs = prop.getProperty(CobFlagsProperties.BRE_CHECK_TEMP_IRIS_ERRORS.getKey());
                String[] irisTempErrorMsgsIterable = irisTempErrorMsgs.toLowerCase().split(",");
                cblPayLoad.setRetryAttempts(0);
                int maxRetries = Integer.parseInt(
                        prop.getProperty(CobFlagsProperties.BRE_RETRY_ATTEMPT.getKey())
                );
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
                        cblPayLoad.setRejectionReason(cblPayLoad.getRejectionReason() + " | BRE Error message: " + cblPayLoad.getIrisMessage());
                        break; // exit loop once matched
                    }
                }
                String irisRetryErrorMsgs = prop.getProperty(CobFlagsProperties.BRE_CHECK_RETRY_IRIS_ERRORS.getKey());
                String[] irisRetryErrorMsgsIterable = irisRetryErrorMsgs.toLowerCase().split(",");
                for (String irisErrorPattern : irisRetryErrorMsgsIterable) {
                    if (normalizedIrisMsg.contains(irisErrorPattern)) {
                        cblPayLoad.setRetryAttempts(maxRetries);
                        cblPayLoad.setEligibleAmt(finalLoanAmount);
                        cblPayLoad.setRejectionReason(cblPayLoad.getRejectionReason() + " | BRE Error Message: " + cblPayLoad.getIrisMessage());
                        break;
                    }
                }

                if (cblPayLoad.getRetryAttempts() > 0 && cblDetails.getCbStatus().equalsIgnoreCase("FAIL")) {
                    cblDetails.setCbDate(oldCbDate);
                }

                cblPayLoad.setFinalDecision(apiResp.getString("Final_Decision"));


                cblPayLoad.setCbLoanId(loanId);
                cblPayLoad.setAppliedLoanCode(apiResp.getString("applied_loan_code"));

//					String foir = String.valueOf((apiResp.getString("FOIR").split(":")[1])).split("\\|")[0].trim().split(",")[0].trim();			
                String foir = extractValue(apiResp.getString("FOIR"));
                cblPayLoad.setFoir(foir);
//					String foirPercentage = String.valueOf(apiResp.getString("FOIR").split(",")[1].split("%")[0].trim());

                String foirPercentage = String.valueOf(apiResp.getBigDecimal("Final_FOIR"));
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
                BigDecimal processingFees = BigDecimal.valueOf(processingFeesRaw)
                        .setScale(0, RoundingMode.HALF_UP);
                cblPayLoad.setProcessingFees(processingFees.toPlainString());
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

                // delete the original record from CibilDetails
                cibilDtlRepo.deleteByApplicationIdAndAppIdAndCustDtlId(input.getApplicant().getAppId(),
                        brecbRequest.getAppId(), customerDetails.get().getCustDtlId());

                String payload = new Gson().toJson(cblPayLoad);
                cblDetails.setPayloadColumn(payload);
                cblDetails.setRequest(breCheckReq);
                logger.debug("Cibil Details record: {}", cblDetails);
                Optional<CibilDetails> existing =
                        Optional.ofNullable(cibilDtlRepo.findByApplicationIdAndCustDtlId(cblDetails.getApplicationId(), cblDetails.getCustDtlId()));
                existing.ifPresent(cibilDtlRepo::delete);
                cibilDtlRepo.save(cblDetails);
                logger.debug("Data inserted" + cblDetails.toString());

            } catch (Exception ex) {
                logger.error("Error occurred while executing the BRE details, error = {}", ex.getMessage(), ex);
                saveLog(input.getApplicant().getAppId(), "breCBCheck", CBCheckRequestExt.toString(),
                        ex.getMessage(), ResponseCodes.FAILURE.getValue(), ex.getMessage(), "");
                return Mono.just(adapterUtil.setError("Error occurred while executing the BRE api.", "1"));
            }

            // call report method
            BRECBReportRequest brecbReportRequest = new BRECBReportRequest();
            brecbReportRequest.setLoanId(loanId);
            brecbReportRequest.setAppId(brecbRequest.getAppId()); // APZCOB

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
                saveLog(appnId, "breCBReport", brecbReportRequest.toString(),
                        e.getMessage(), ResponseCodes.FAILURE.getValue(), e.getMessage(), "");
                return Mono.just(adapterUtil.setError("Error occurred while executing the BRE Report api.", "1"));
            });
        }).cache().onErrorMap(e -> {
            brecbRequest.getRequestObj().getBreCBValuesRequestvalues1().getBreCBInputRequestinput1().getBreCBValuesRequestvalues2().getBreCBInputRequestinput2().setBusinessImageAssessment(null);
            logger.error("Error occurred while executing the BRE api, error = {}", e.getMessage(), e);
            saveLog(appnId, "breCBCheck", brecbRequest.toString(),
                    e.getMessage(), ResponseCodes.FAILURE.getValue(), e.getMessage(), "");
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

    public String findCategoryId(String subPurposeObj, String searchValue, Properties prop) {

        try {
            JSONObject js = new JSONObject(subPurposeObj);

            Map<String, String> reverseLookupMap = new HashMap<>();
            JSONObject loanSubPurpose = js.getJSONObject("Loan Sub Purpose");
            Iterator<String> keys = loanSubPurpose.keys();

            while (keys.hasNext()) {
                String category = keys.next();
                JSONArray items = loanSubPurpose.getJSONArray(category);

                for (int i = 0; i < items.length(); i++) {
                    String value = items.getString(i);

                    String normalizedValue = normalizeString(value);
                    reverseLookupMap.put(normalizedValue, category);
                }
            }

            String purposeId = prop.getProperty(CobFlagsProperties.LOANPURPOSE_IDENTIFIER.getKey());
            Map<String, String> categoryIdMap = new HashMap<>();
            JSONObject json = new JSONObject(purposeId);
            for (String key : json.keySet()) {
                categoryIdMap.put(key, json.getString(key));
            }

            logger.debug("Category Id " + categoryIdMap);
            String normalizedSearchValue = normalizeString(searchValue);

            String category = reverseLookupMap.get(normalizedSearchValue);
            if (category != null) {
                return categoryIdMap.getOrDefault(category, "01");
            } else {
                return "Value Not Found";
            }
        } catch (Exception e) {
            logger.error("Exception in findCategory ID ", e);
        }
        return "Value not Found";
    }

    private static String normalizeString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("_", "")
                .replace(".", "")
                .toUpperCase()
                .trim();
    }

    public Mono<Object> loanCreation(String applicationId, String appId, String memberId, Header header,
                                     Properties prop, String coApplicantId) {

        logger.debug("Loan Creation API Started for ID :" + coApplicantId);

        CustomerDetailsPayload appPayload = null;
        CustomerDetailsPayload coappPayload = null;
        CibilDetailsPayload cibilPayload = null;
        String applicantCustId = "";
        String coApplicantCustId = "";
        Gson gson = new Gson();
        String nomineeName = "";
        String nomineeRelation = "";
        String cbDateStr = "";
        String coAppFoir = "";
        String familyIncomeStr = "";
        String earningMembers = "";
        String annualPercentageRate = "";
        Boolean insuranceRequiredApplicant = false;
        Boolean insuranceRequiredCoapplicant = false;
        Gson gsonObj = new Gson();
        try {

            ApplyLoanRequestFields custFields = null;
            ApplicationMaster applicationMasterData = null;
            List<ApplicationMaster> appMasterDb = applicationMasterRepo
                    .findByAppIdAndApplicationId(Constants.APPID, applicationId);

            if (null != appMasterDb && !appMasterDb.isEmpty()) {
                for (ApplicationMaster appMaster : appMasterDb) {
                    applicationMasterData = appMaster;
                }
                String product = applicationMasterData.getProductCode();
                logger.debug("product :" + product);
                custFields = getCustomerData(applicationMasterData, applicationId, Constants.APPID, 1);
            }

            for (CustomerDetails custDtl : custFields.getCustomerDetailsList()) {
                logger.debug("customer Type : " + custDtl.getCustomerType());
                if (custDtl.getCustomerType().equalsIgnoreCase("Applicant")) {
                    applicantCustId = String.valueOf(custDtl.getCustDtlId());
                    logger.debug("applicantCustId : " + applicantCustId);
                    appPayload = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    logger.debug("custApplicantPayload :" + appPayload);
                } else if (custDtl.getCustomerType().equalsIgnoreCase(Constants.COAPPLICANT)) {
                    coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
                    logger.debug("coApplicantCustId : " + applicantCustId);
                    coappPayload = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    logger.debug("custCo-ApplicantPayload :" + coappPayload);
                }
            }
            for (InsuranceDetailsWrapper wrapper : custFields.getInsuranceDetailsWrapperList()) {
                String custId = String.valueOf(wrapper.getInsuranceDetails().getCustDtlId());
                logger.debug("InsuranceDetailsWrapper CustId : {}", custId);

                InsuranceDetailsPayload payload = gsonObj.fromJson(
                        wrapper.getInsuranceDetails().getPayloadColumn(),
                        InsuranceDetailsPayload.class
                );
                logger.debug("insurancePayload Payload : {}", payload);

                String insuranceOption = payload != null ? payload.getInsuranceOption() : null;
                String insuranceReqd = payload != null ? payload.getInsuranceReqd() : null;

                List<String> insuranceEnableOptions = Arrays.asList(
                        Constants.BOTH_INSURANCE_OPTION,
                        Constants.JOINT_INSURANCE_OPTION
                );

                if (insuranceOption != null && !insuranceOption.trim().equalsIgnoreCase(Constants.NO_INSURANCE_OPTION)) {
                    String normalizedOption = insuranceOption.trim().toUpperCase();

                    boolean isInsuranceYes = Constants.YES.equalsIgnoreCase(insuranceReqd);
                    boolean isEnabledOption = insuranceEnableOptions.contains(normalizedOption);

                    if (custId.equals(applicantCustId)) {
                        logger.debug("Matched applicantCustId with InsuranceDetailsWrapper CustId");
                        if ((isEnabledOption || normalizedOption.equalsIgnoreCase(Constants.APPLICANT_INSURANCE_OPTION))
                                && isInsuranceYes) {
                            insuranceRequiredApplicant = true;
                        }
                        logger.debug("applicant insuranceReqd: '{}'", insuranceRequiredApplicant);

                    } else if (custId.equals(coApplicantCustId)) {
                        logger.debug("Matched coApplicantCustId with InsuranceDetailsWrapper CustId");
                        if (isEnabledOption && isInsuranceYes) {
                            insuranceRequiredCoapplicant = true;
                        }
                        logger.debug("coapplicant insuranceReqd: '{}'", insuranceRequiredCoapplicant);

                    } else {
                        logger.debug("New Nominee added");
                        nomineeName = payload.getNomineeName();
                        nomineeRelation = payload.getNomineeRelation();
                        logger.debug("Nominee Name: '{}', Relation: '{}'", nomineeName, nomineeRelation);
                    }
                } else {
                    logger.debug("No Insurance opted");
                }
            }

            for (CibilDetailsWrapper cibilDetailsWrapper : custFields.getCibilDetailsWrapperList()) {
                String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
                logger.debug("CreditDetailsPayload Payload : " + cibilPayload);
                if (custId.equals(coApplicantCustId)) {
                    cibilPayload = gsonObj.fromJson(
                            cibilDetailsWrapper.getCibilDetails().getPayloadColumn(), CibilDetailsPayload.class);
                    LocalDate cbDate = cibilDetailsWrapper.getCibilDetails().getCbDate();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    cbDateStr = cbDate.format(formatter);
                    coAppFoir = cibilPayload.getFoir();
                }
            }
//			String roi = String.valueOf(custFields.getLoanDetails().getRoi());
            LoanCreationReqFields loanRequest = new LoanCreationReqFields();


            String borrowerInsurance = insuranceRequiredApplicant ? "YES" : "NO";
            String jointInsurance = insuranceRequiredApplicant
                    && insuranceRequiredCoapplicant ? "YES" : "NO";

            logger.debug("Computed borrowerInsurance: '{}'", borrowerInsurance);
            logger.debug("Computed jointInsurance: '{}'", jointInsurance);

            LoanDetails loanDetails = custFields.getLoanDetails();
            Integer year = loanDetails.getTenure() / 12;
            String yearInWeeks = (year >= 1 && year <= 3) ? (year * 52) + Constants.TERM_WEEK : "";

            LoanDetailsPayload loanPayload = gson.fromJson(loanDetails.getPayloadColumn(), LoanDetailsPayload.class);

            Optional<LovMaster> subPurposeId = lovMasterRepository.findById(loanDetailsLovId);
            logger.debug("Subpurpose Id :" + subPurposeId.get().getLovDtls().toString());
            String purposeId = findCategoryId(subPurposeId.get().getLovDtls().toString(), loanPayload.getSubCategory().replace("_", "."), prop);

            // As per latest discussion - 19/05/2025
            Map<String, Integer> coCus = new HashMap<>();
            coCus.put("ltUnniCoCus", Integer.parseInt(coApplicantId));
            List<Map<String, Integer>> unnatiCoCustomerList = new ArrayList<>();
            unnatiCoCustomerList.add(coCus);
            String applicantId = applicationMasterData.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE) ?
                    applicationMasterData.getApplicantT24Id() : applicationMasterData.getMemberId();
            loanRequest.setApplicantId(applicantId);
            loanRequest.setUnnatiCoCustomer(unnatiCoCustomerList);
            loanRequest.setProduct(Constants.UNNATI_LOAN_PRODUCT);
            if (applicationMasterData.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)) {
                loanRequest.setProduct(Constants.OPEN_MARKET_LOAN_PRODUCT);
            }

            loanRequest.setCurrency(Constants.CURRENCY_INR); // Default value
            loanRequest.setTerm(yearInWeeks);
            loanRequest.setAmount(loanDetails.getSanctionedLoanAmount());
            loanRequest.setIntRate(cibilPayload.getRoi());
            String frequency = loanPayload.getFrequencyOfRepayment();
            String normalized = frequency.toUpperCase().trim();
// collapse multiple spaces or dashes into one dash
            normalized = normalized.replaceAll("[\\s\\-]+", "-");
// if it looks like any variant of BI-WEEKLY
            Matcher m = Constants.BI_WEEKLY_PATTERN.matcher(normalized);
            if (m.find()) {
                normalized = "BI-WEEKLY";
            }
            loanRequest.setFrequency(normalized);
            loanRequest.setDisburseMode(loanPayload.getModeOfDisbursement());
            loanRequest.setPurpose(purposeId);
            loanRequest.setSubPurpose(loanPayload.getSubCategory().replaceAll("[^A-Za-z0-9]+", "."));
            loanRequest.setNomnieeName(nomineeName);
            loanRequest.setNomineeRelation(nomineeRelation);
            loanRequest.setNomineePhone(""); // As per API Doc
            loanRequest.setCbResponseDate(cbDateStr);
            loanRequest.setCbRemarks("");
            loanRequest.setBorrowerInsurance(borrowerInsurance);
            loanRequest.setJointOwnerOrCoborrowerInsurance(jointInsurance);
            loanRequest.setSystemApplicationId(custFields.getApplicationId());
            loanRequest.setFoir(coAppFoir);
            if (applicationMasterData.getProductCode().equalsIgnoreCase(Constants.UNNATI_PRODUCT_CODE)) {
                loanRequest.setPreCloseType(prop.getProperty(CobFlagsProperties.UNNATI_PRE_CLOSE_TYPE.getKey())); // default for Unnati loan
            } else if (applicationMasterData.getProductCode().equalsIgnoreCase(Constants.RENEWAL_LOAN_PRODUCT_CODE)) {
                loanRequest.setPreCloseType(prop.getProperty(CobFlagsProperties.RENEWAL_PRE_CLOSE_TYPE.getKey()));
            } else if (applicationMasterData.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)) {
                loanRequest.setPreCloseType(prop.getProperty(CobFlagsProperties.UNNATI_PRE_CLOSE_TYPE.getKey()));
            } else {
                return Mono.just(adapterUtil.setError("Error occurred while executing the Loan Creation api. Unknown product code :" + applicationMasterData.getProductCode(), "1"));
            }
            loanRequest.setPayoffAccount(""); // NA
            loanRequest.setCompanyIdTemp(applicationMasterData.getBranchId());
            String eir = cibilPayload.getEir();
            if (eir != null) {
                annualPercentageRate = eir.replace("%", "").trim();
            }

            loanRequest.setAnnualPercentageRate(annualPercentageRate);


            Optional<BCMPIIncomeDetails> incomeDetails = bcmpiIncomeDetailsRepo.findById(applicationId);
            if (incomeDetails.isPresent()) {
                BCMPIIncomeDetailsWrapper incomeDetailsWrapper = gson.fromJson(incomeDetails.get().getPayload(), BCMPIIncomeDetailsWrapper.class);
                BigDecimal fieldAssessedIncome = incomeDetailsWrapper.getFieldAssessedIncome();
                BigDecimal selfDeclaredIncome = incomeDetailsWrapper.getTotalDeclaredIncome();
                BigDecimal familyIncome = fieldAssessedIncome.min(selfDeclaredIncome);
                familyIncomeStr = String.valueOf(familyIncome);
            } else {
                logger.debug("Income details not present for application_id : {}", applicationId);
                return Mono.just(adapterUtil.setError("Income details not present for application Id : " + applicationId, "1"));
            }
            loanRequest.setFamilyIncome(familyIncomeStr);

            Optional<BCMPIOtherDetails> caOtherDetails = bcmpiOtherDetailsRepo.findById(applicationId);
            if (caOtherDetails.isPresent()) {
                BCMPIOtherDetailsWrapper otherDetailsWrapper = gson.fromJson(caOtherDetails.get().getPayload(), BCMPIOtherDetailsWrapper.class);
                earningMembers = otherDetailsWrapper.getNoOfOtherEarningMembers();

            }
            loanRequest.setEarningMembers(earningMembers);
            Map<String, LoanCreationReqFields> mapReq = new HashMap<>();
            mapReq.put("body", loanRequest);
            logger.debug("JSON request for loan creation api :" + mapReq);

            LoanRequestExt loanCreationRequestExt = new LoanRequestExt();
            loanCreationRequestExt.setAppId(appId);
            loanCreationRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.LOAN_CREATION_INTF.getKey()));
            loanCreationRequestExt.setRequestObj(mapReq);
            logger.debug("Loan Creation from the API: {} ", loanCreationRequestExt.toString());

            setRequestLog(loanCreationRequestExt.toString());

            Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, loanCreationRequestExt,
                    loanCreationRequestExt.getInterfaceName());

            logger.debug("response 1 from the API1: {} ", apiRespMono);

			/*adapterUtil.generateResponseWrapper(apiRespMono,
					"LoanCreation", header);*/

            return apiRespMono.flatMap(val -> {
                logger.debug("response 3 from the report API: {} ", val);
                JSONObject apiReportResp = new JSONObject(new Gson().toJson(val));
                return Mono.just(apiReportResp);
            });

        } catch (Exception e) {
            logger.error("Error occurred while executing the Loan Creation api", e);
            return Mono.just(adapterUtil.setError("Error occurred while executing the Loan Creation api.", "1"));
        }
    }


    public Mono<Object> loanRejection(String applicationId, Properties prop, String appId, Header header) {
        logger.debug("Loan Rejection API started");
        try {
            ApplicationMaster applicationMasterData = null;
            List<ApplicationMaster> appMasterDb = applicationMasterRepo
                    .findByAppIdAndApplicationId(Constants.APPID, applicationId);

            if (null != appMasterDb && !appMasterDb.isEmpty()) {
                for (ApplicationMaster appMaster : appMasterDb) {
                    applicationMasterData = appMaster;
                }
            }

            String loanId = "";
            String branchId = "";
            Optional<LoanDetails> loanOpt =
                    loanDtlsRepo.findTopByApplicationIdAndAppId(applicationId, appId);

            if (loanOpt.isPresent()) {
                loanId = loanOpt.get().getT24LoanId();
                if (loanId == null) {
                    logger.debug("Loan ID is null inside the object.");
                    Response failureJson = getFailureJson(Constants.LOAN_ID_NOT_GENERATED);
                    return Mono.just(failureJson);
                }
            } else {
                Response failureJson = getFailureJson(Constants.LOAN_ID_NOT_GENERATED);
                return Mono.just(failureJson);
            }
            logger.debug("Loan id from db:" + loanId);

            if (null != applicationMasterData && null != applicationMasterData.getBranchId()) {
                branchId = applicationMasterData.getBranchId();
            } else {
                logger.debug("Unable to fetch application branch id.");
                Response failureJson = getFailureJson("Unable to fetch application branch id");
                return Mono.just(failureJson);
            }
            Map<String, Object> loanRequest = new HashMap<>();
            loanRequest.put("remarks", "rejected");
            loanRequest.put("companyIdTemp", branchId);
            loanRequest.put("loanIdTemp", loanId);

            Map<String, Object> mapReq = new HashMap<>();
            mapReq.put("body", loanRequest);
            logger.debug("Final request of loan rejection  " + mapReq);

            LoanRequestExt loanRequestExt = new LoanRequestExt();
            loanRequestExt.setAppId(appId);
            loanRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.LOAN_REJECTION_INTF.getKey()));
            loanRequestExt.setRequestObj(mapReq);
            logger.debug("Loan Rejection from the API: {} ", loanRequestExt.toString());

            setRequestLog(loanRequestExt.toString());

            Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, loanRequestExt, loanRequestExt.getInterfaceName());

            logger.debug("response 1 from the API1: {} ", apiRespMono);

            return apiRespMono.flatMap(val -> {
                logger.debug("response 2 from the API: " + val);
                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });
        } catch (Exception e) {
            logger.error("Error occurred while executing the Loan Rejection api", e);
            return Mono.just(getFailureApiJson("Error occurred while executing the Loan Rejection api", Constants.LOAN_REJECTION));
        }
    }

    /**
     * Function to get loan Details if error - Loan is already in processing stage
     *
     * @param applicationId
     * @param prop
     * @param header
     * @return
     */
    public Mono<Object> loanFetch(String applicationId, Properties prop, ApplicationMaster masterObj, Header header) {
        logger.debug("Loan Fetch API started");

        try {

            LoanFetchReqFields loanRequest = new LoanFetchReqFields();

            loanRequest.setCompany(prop.getProperty(CobFlagsProperties.LOAN_FETCH_COMPANY.getKey()));
            loanRequest.setOperand(prop.getProperty(CobFlagsProperties.LOAN_FETCH_OPERAND.getKey()));
            loanRequest.setPassword(prop.getProperty(CobFlagsProperties.LOAN_FETCH_PASSWORD.getKey()));
            loanRequest.setUserName(prop.getProperty(CobFlagsProperties.LOAN_FETCH_USERNAME.getKey()));
            loanRequest.setColumnName(prop.getProperty(CobFlagsProperties.LOAN_FETCH_COLUMN_NAME.getKey()));
            if (masterObj.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)) {
                loanRequest.setCriteriaValue(masterObj.getApplicantT24Id());
            } else {
                loanRequest.setCriteriaValue(masterObj.getMemberId());
            }

            LoanRequestExt loanRequestExt = new LoanRequestExt();
            loanRequestExt.setAppId(masterObj.getAppId());
            loanRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.LOAN_FETCH_INTF.getKey()));
            loanRequestExt.setRequestObj(loanRequest);
            logger.debug("Loan Fetch from the API: {} ", loanRequestExt.toString());

            setRequestLog(loanRequestExt.toString());

            Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, loanRequestExt, loanRequestExt.getInterfaceName());

            logger.debug("response 1 from the API1: {} ", apiRespMono);

            return apiRespMono.flatMap(val -> {
                logger.debug("response 2 from the API: " + val);
                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });
        } catch (Exception e) {
            logger.error("Error occurred while executing the Loan Fetch api", e);
            return Mono.just(getFailureApiJson("Error occurred while executing the Loan Fetch api", Constants.LOAN_FETCH));
        }
    }

    public Mono<Object> CoCustomerFetch(String applicationId, Properties prop, ApplicationMaster masterObj, Header header, String coApplicantId) {
        logger.debug("CoCustomerFetch API started");

        try {

            LoanFetchReqFields loanRequest = new LoanFetchReqFields();

            loanRequest.setCompany(prop.getProperty(CobFlagsProperties.LOAN_FETCH_COMPANY.getKey()));
            loanRequest.setPassword(prop.getProperty(CobFlagsProperties.LOAN_FETCH_PASSWORD.getKey()));
            loanRequest.setUserName(prop.getProperty(CobFlagsProperties.LOAN_FETCH_USERNAME.getKey()));
            loanRequest.setTransactionId(coApplicantId);

            LoanRequestExt loanRequestExt = new LoanRequestExt();
            loanRequestExt.setAppId(masterObj.getAppId());
            loanRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.CO_CUST_FETCH_INTF.getKey()));
            loanRequestExt.setRequestObj(loanRequest);
            logger.debug("CoCustomerFetch from the API: {} ", loanRequestExt.toString());

            setRequestLog(loanRequestExt.toString());

            Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, loanRequestExt, loanRequestExt.getInterfaceName());

            logger.debug("response 1 from the API1: {} ", apiRespMono);

            return apiRespMono.flatMap(val -> {
                logger.debug("CoCustomerFetch : response 2 from the API: " + val);
                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("CoCustomerFetch : JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });
        } catch (Exception e) {
            logger.error("Error occurred while executing the CoCustomerFetch api", e);
            return Mono.just(getFailureApiJson("Error occurred while executing the CoCustomerFetch api", Constants.CO_CUSTOMER_FETCH));
        }
    }

    public Mono<Object> loanRepaySchedule(String applicationId, Properties prop, String appId, Header header) {
        logger.debug("Loan RepaySchedule API started");
        CustomerDetailsPayload appPayload = null;
        CustomerDetailsPayload coappPayload = null;
        CibilDetailsPayload cibilPayload = null;
        String applicantCustId = "";
        String coApplicantCustId = "";
        Gson gsonObj = new Gson();
        try {

            ApplyLoanRequestFields custFields = null;

            ApplicationMaster applicationMasterData = null;
            List<ApplicationMaster> appMasterDb = applicationMasterRepo.findByAppIdAndApplicationId(Constants.APPID, applicationId);

            if (null != appMasterDb && !appMasterDb.isEmpty()) {
                for (ApplicationMaster appMaster : appMasterDb) {
                    applicationMasterData = appMaster;
                }
                String product = applicationMasterData.getProductCode();
                logger.debug("product :" + product);
                custFields = getCustomerData(applicationMasterData, applicationId, Constants.APPID, Constants.INITIAL_VERSION_NO);
            }


            for (CustomerDetails custDtl : custFields.getCustomerDetailsList()) {
                logger.debug("customer Type : " + custDtl.getCustomerType());
                if (custDtl.getCustomerType().equalsIgnoreCase("Applicant")) {
                    applicantCustId = String.valueOf(custDtl.getCustDtlId());
                    logger.debug("applicantCustId : " + applicantCustId);
                    appPayload = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    logger.debug("custApplicantPayload :" + appPayload);
                } else if (custDtl.getCustomerType().equalsIgnoreCase(Constants.COAPPLICANT)) {
                    coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
                    coappPayload = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    logger.debug("custCo-ApplicantPayload :" + coappPayload);
                }
            }
            for (CibilDetailsWrapper cibilDetailsWrapper : custFields.getCibilDetailsWrapperList()) {
                String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
                logger.debug("CreditDetailsPayload Payload : " + cibilPayload);
                if (custId.equals(coApplicantCustId)) {
                    cibilPayload = gsonObj.fromJson(
                            cibilDetailsWrapper.getCibilDetails().getPayloadColumn(), CibilDetailsPayload.class);
                }
            }

            LoanDetails loanDetails = custFields.getLoanDetails();

            Integer year = loanDetails.getTenure() / 12;
            String yearInWeeks = (year >= 1 && year <= 3) ? String.valueOf((year * 52)) : "";

            Map<String, Object> loanReq = new HashMap<>();
            String normalized = cibilPayload.getRepaymentFrequency().toUpperCase().replaceAll("\\s", "-");
            // collapse multiple spaces or dashes into one dash
            normalized = normalized.replaceAll("[\\s\\-]+", "-");
            // if it looks like any variant of BI-WEEKLY
            Matcher m = Constants.BI_WEEKLY_PATTERN.matcher(normalized);
            if (m.find()) {
                normalized = "BI-WEEKLY";
            }
            loanReq.put("loanFrequency", normalized);
            loanReq.put("interestRate", cibilPayload.getRoi());
            loanReq.put("loanAmount", loanDetails.getSanctionedLoanAmount());
            loanReq.put("tenure", yearInWeeks);
            if (applicationMasterData.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)) {
                loanReq.put(Constants.CUSTOMERID1, applicationMasterData.getApplicantT24Id());
                loanReq.put("productID", Constants.OPEN_MARKET_LOAN_PRODUCT);
            } else {
                loanReq.put(Constants.CUSTOMERID1, applicationMasterData.getMemberId());
                loanReq.put("productID", Constants.UNNATI_LOAN_PRODUCT);
            }


            Map<String, Object> mapReq = new HashMap<>();
            mapReq.put("body", loanReq);
            logger.debug("Final request of loan RepaySchedule  " + mapReq);

            LoanRequestExt loanRequestExt = new LoanRequestExt();
            loanRequestExt.setAppId(appId);
            loanRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.LOAN_REPAYMENT_SCHEDULE_INTF.getKey()));
            loanRequestExt.setRequestObj(mapReq);
            logger.debug("Loan RepaySchedule from the API: {} ", loanRequestExt.toString());

            setRequestLog(loanRequestExt.toString());

            Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, loanRequestExt, loanRequestExt.getInterfaceName());

            logger.debug("response 1 from the API1: {} ", apiRespMono);

            return apiRespMono.flatMap(val -> {
                logger.debug("response 2 from the API: " + val);
                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });
        } catch (Exception e) {
            logger.error("Error occurred while executing the Loan RepaySchedule api", e);
            return Mono.just(getFailureApiJson("Error occurred while executing the Loan RepaySchedule api", Constants.LOAN_REJECTION));
        }
    }

    public Mono<Object> loanDisbursement(String applicationId, Properties prop, String appId, Header header) {

        try {

            CustomerDetailsPayload appPayload = null;
            CustomerDetailsPayload coappPayload = null;
            CibilDetailsPayload cibilPayload = null;
            String applicantCustId = "";
            String coApplicantCustId = "";
            Gson gsonObj = new Gson();

            ApplyLoanRequestFields custFields = null;
            ApplicationMaster applicationMasterData = null;
            List<ApplicationMaster> appMasterDb = applicationMasterRepo
                    .findByAppIdAndApplicationId(Constants.APPID, applicationId);

            if (null != appMasterDb && !appMasterDb.isEmpty()) {
                for (ApplicationMaster appMaster : appMasterDb) {
                    applicationMasterData = appMaster;
                }
                String product = applicationMasterData.getProductCode();
                logger.debug("product :" + product);
                custFields = getCustomerData(applicationMasterData, applicationId, Constants.APPID, 1);
            }
            for (CustomerDetails custDtl : custFields.getCustomerDetailsList()) {
                logger.debug("customer Type : " + custDtl.getCustomerType());
                if (custDtl.getCustomerType().equalsIgnoreCase("Applicant")) {
                    applicantCustId = String.valueOf(custDtl.getCustDtlId());
                    logger.debug("applicantCustId : " + applicantCustId);
                    appPayload = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    logger.debug("custApplicantPayload :" + appPayload);
                } else if (custDtl.getCustomerType().equalsIgnoreCase(Constants.COAPPLICANT)) {
                    coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
                    coappPayload = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    logger.debug("custCo-ApplicantPayload :" + coappPayload);
                }
            }
            String loanId = "";
            Optional<LoanDetails> loanOpt =
                    loanDtlsRepo.findTopByApplicationIdAndAppId(applicationId, appId);

            if (loanOpt.isPresent()) {
                loanId = loanOpt.get().getT24LoanId();
                if (loanId == null) {
                    logger.debug("Loan ID is null inside the object.");
                    Response failureJson = getFailureJson(Constants.LOAN_ID_NOT_GENERATED);
                    return Mono.just(failureJson);
                }
            } else {
                Response failureJson = getFailureJson(Constants.LOAN_ID_NOT_GENERATED);
                return Mono.just(failureJson);
            }
            logger.debug("Loan id from db:" + loanId);

            Map<String, Object> loanRequest = new HashMap<>();
            loanRequest.put("stampDutyCharge", Integer.parseInt(Constants.ZERO));
            loanRequest.put("companyIdTemp", applicationMasterData.getBranchId());
            loanRequest.put("loanIdTemp", loanId);

            Map<String, Object> mapReq = new HashMap<>();
            mapReq.put("body", loanRequest);
            logger.debug("Final request of loan disbursement  " + mapReq);

            LoanRequestExt loanRequestExt = new LoanRequestExt();
            loanRequestExt.setAppId(appId);
            loanRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.LOAN_DISBURSEMENT_INTF.getKey()));
            loanRequestExt.setRequestObj(mapReq);
            logger.debug("Loan disbursement from the API: {} ", loanRequestExt.toString());

            setRequestLog(loanRequestExt.toString());

            Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, loanRequestExt, loanRequestExt.getInterfaceName());

            logger.debug("response 1 from the API1: {} ", apiRespMono);

            return apiRespMono.flatMap(val -> {
                logger.debug("response 2 from the API: " + val);
                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });

        } catch (Exception e) {
            logger.error("Error occurred while executing the Loan Disbursement api", e);
            return Mono.just(getFailureApiJson("Error occurred while executing the Loan Disbursement api", Constants.LOAN_DISBURSEMENT));
        }
    }


    public Mono<Object> disbRepaySchedule(String applicationId, Properties prop, String appId, Header header) {
        logger.debug("Loan Repyament at Disbursemnt Schedule started");
        try {

            String loanId = "";
            Optional<LoanDetails> loanOpt = loanDtlsRepo.findTopByApplicationIdAndAppId(applicationId, appId);

            if (loanOpt.isPresent()) {
                loanId = loanOpt.get().getT24LoanId();
                if (loanId == null) {
                    logger.debug("Loan ID is null inside the object.");
                    Response failureJson = getFailureJson(Constants.LOAN_ID_NOT_GENERATED);
                    return Mono.just(failureJson);
                }
            } else {
                Response failureJson = getFailureJson(Constants.LOAN_ID_NOT_GENERATED);
                return Mono.just(failureJson);
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

            return apiRespMono.flatMap(val -> {
                logger.debug("response 2 from the API: " + val);
                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });

        } catch (Exception e) {
            logger.error("Error occurred while executing the Loan Repyament Disbursement api", e);
            return Mono.just(getFailureApiJson("Error occurred while executing the Loan Repyament Disbursement api",
                    Constants.LOAN_DISBURSEMENT));
        }
    }

    public Mono<Object> dedupeTableUpdate(ApplicationMaster master, Header header, Properties prop, String targetCustomerId, boolean isCoapp) {
        logger.debug("Dedupe Table Update Started");
        Gson gson = new Gson();
        String updateApi = isCoapp ? Constants.COAPPLICANT_DEDUPE_UPDATE : Constants.APPLICANT_DEDUPE_UPDATE;
        try {
            String customerType = isCoapp ? Constants.COAPPLICANT : Constants.APPLICANT;

            DedupeTableUpdateRequest request = new DedupeTableUpdateRequest();
            request.setId("3");
            request.setGkv("1.2");
            request.setMethod("api.custUpdate");
            if (!StringUtils.isBlank(targetCustomerId)) {
                request.setCustomerId(targetCustomerId);
                request.setRecordtype("ACTIVE");
            } else {
                String prospectId = master.getApplicationId();
                request.setCustomerId(prospectId);
                request.setRecordtype("PROSPECT");
            }
            request.setCustqualify("CREDIT.IL");

            Optional<CustomerDetails> customerOpt = custDtlRepo.findByApplicationIdAndCustomerType(master.getApplicationId(), customerType);
            if (!customerOpt.isPresent()) {
                logger.debug("Customer details not found for type: {}", customerType);
                return Mono.just(getFailureApiJson("Customer details not found.", updateApi));
            }
            CustomerDetails customerDetails = customerOpt.get();
            CustomerDetailsPayload custPayload = gson.fromJson(customerDetails.getPayloadColumn(), CustomerDetailsPayload.class);
            logger.debug("CustomerDetailsPayload: {}", custPayload);

            if (master.getProductCode().equalsIgnoreCase(Constants.UNNATI_PRODUCT_CODE)) {
                Optional<LeadDetails> leadDetailsOpt = leadDtlsRepo.findByCustomerId(master.getSearchCode2());
                if (!leadDetailsOpt.isPresent()) {
                    logger.debug("Lead details not found for memberId: {}", master.getSearchCode2());
                    return Mono.just(getFailureApiJson("Lead details not found.", updateApi));
                }
                LeadDetails leadDetails = leadDetailsOpt.get();
                request.setKendraId(leadDetails.getKendraId());
                request.setGroupId(leadDetails.getGroupId());
                request.setBranchId(leadDetails.getGlBranchId());
            } else if (master.getProductCode().equalsIgnoreCase(Constants.RENEWAL_LOAN_PRODUCT_CODE)) {
                Optional<RenewalLeadDetails> renewalLeadDetailsOpt = renewalLeadDtlsRepo.findByCustomerId(master.getSearchCode2());
                if (!renewalLeadDetailsOpt.isPresent()) {
                    logger.debug("Renewal Lead details not found for memberId: {}", master.getSearchCode2());
                    return Mono.just(getFailureApiJson("Renewal Lead details not found.", updateApi));
                }
                RenewalLeadDetails renewalLeadDetails = renewalLeadDetailsOpt.get();
                request.setKendraId(renewalLeadDetails.getKendraId());
                request.setGroupId(renewalLeadDetails.getGroupId());
                request.setBranchId(renewalLeadDetails.getGlBranchId());
            } else if (master.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)) {
                Optional<UnnatiIexceedCDHLead> iexceedCDHLeadOpt = unnatiIexceedCDHLeadRepo.findByCustomerId(master.getSearchCode2());
                if (!iexceedCDHLeadOpt.isPresent()) {
                    logger.debug("Iexceed CDH Lead details not found for memberId: {}", master.getSearchCode2());
                    return Mono.just(getFailureApiJson("Iexceed CDH Lead details not found.", updateApi));
                }
                UnnatiIexceedCDHLead iexceedCDHLead = iexceedCDHLeadOpt.get();
                request.setKendraId(iexceedCDHLead.getKendraId());
                request.setGroupId(iexceedCDHLead.getGroupId());
                request.setBranchId(iexceedCDHLead.getGlBranchId());
            } else {
                logger.debug("Unknown product code for dedupe update: {}", master.getProductCode());
                return Mono.just(getFailureApiJson("Unknown product code.", updateApi));
            }
            request.setName(StringUtils.isBlank(custPayload.getFirstName()) ? customerDetails.getCustomerName() : custPayload.getFirstName());
            if (customerType.equalsIgnoreCase(Constants.COAPPLICANT)) {
                Optional<CustomerDetails> applicantCustOpt = custDtlRepo.findByApplicationIdAndCustomerType(master.getApplicationId(), Constants.APPLICANT);
                if (!applicantCustOpt.isPresent()) {
                    logger.debug("Customer details not found for type:" + Constants.APPLICANT);
                    return Mono.just(getFailureApiJson("Applicant customer details not found.", updateApi));
                }
                CustomerDetails applicantCustomerDetails = applicantCustOpt.get();
                CustomerDetailsPayload applicantCustPayload = gson.fromJson(applicantCustomerDetails.getPayloadColumn(), CustomerDetailsPayload.class);
                logger.debug("Applicant CustomerDetailsPayload: {}", applicantCustPayload);
                String customerMobile = customerDetails.getMobileNumber();
                String applicantMobile = applicantCustomerDetails.getMobileNumber();
                if (customerMobile != null
                        && (applicantMobile == null
                        || !customerMobile.equalsIgnoreCase(applicantMobile))) {
                    request.setPhoneNum1(customerMobile);
                }
                if (custPayload.getMaritalStatus().equalsIgnoreCase(Constants.MARRIED) && custPayload.getRelationShipWithApplicant().equalsIgnoreCase(Constants.SPOUSE)) {
                    boolean isPrimaryVerified = Constants.VERIFIED_STS.equalsIgnoreCase(applicantCustPayload.getPrimaryKycIdValStatus());
                    boolean isAlternateVerified = Constants.VERIFIED_STS.equalsIgnoreCase(applicantCustPayload.getAlternateVoterIdValStatus());
                    String apptKycNo = "";
                    logger.debug("isPrimaryVerified: {}, isAlternateVerified: {}", isPrimaryVerified, isAlternateVerified);
                    boolean hasAlternateId =
                            StringUtils.isNotBlank(applicantCustPayload.getAlternateVoterId());
                    boolean hasPrimaryId =
                            StringUtils.isNotBlank(applicantCustPayload.getPrimaryKycId());
                    if (isAlternateVerified && hasAlternateId) {
                        // Highest priority
                        apptKycNo = applicantCustPayload.getAlternateVoterId();
                    } else if (isPrimaryVerified && hasPrimaryId) {
                        // Fallback
                        apptKycNo = applicantCustPayload.getPrimaryKycId();
                    } else {
                        logger.debug(
                                "No valid verified Voter ID found for customerType {}. " +
                                        "Alternate verified={}, Alternate blank={}, " +
                                        "Primary verified={}, Primary blank={}", Constants.APPLICANT,
                                isAlternateVerified, !hasAlternateId,
                                isPrimaryVerified, !hasPrimaryId
                        );

                        return Mono.error(new RuntimeException(
                                "No valid verified Voter ID found for the " + Constants.APPLICANT + ". Cannot proceed with Dedupe Table Update."
                        ));
                    }
                    request.setSpkycid(apptKycNo.toUpperCase());
                    request.setSpkycname("VOTER-ID");
                }
            } else if (customerType.equalsIgnoreCase(Constants.APPLICANT)) {
                Optional<CustomerDetails> coApplicantCustOpt = custDtlRepo.findByApplicationIdAndCustomerType(master.getApplicationId(), Constants.COAPPLICANT);
                if (!coApplicantCustOpt.isPresent()) {
                    logger.debug("Customer details not found for type:" + Constants.COAPPLICANT);
                    return Mono.just(getFailureApiJson("Co-Applicant customer details not found.", updateApi));
                }
                request.setPhoneNum1(customerDetails.getMobileNumber());
                CustomerDetails coApplicantCustomerDetails = coApplicantCustOpt.get();
                CustomerDetailsPayload coApplicantCustPayload = gson.fromJson(coApplicantCustomerDetails.getPayloadColumn(), CustomerDetailsPayload.class);
                logger.debug("Co-Applicant CustomerDetailsPayload: {}", coApplicantCustPayload);
                if (coApplicantCustPayload.getMaritalStatus().equalsIgnoreCase(Constants.MARRIED) && coApplicantCustPayload.getRelationShipWithApplicant().equalsIgnoreCase(Constants.SPOUSE)) {
                    boolean isPrimaryVerified = Constants.VERIFIED_STS.equalsIgnoreCase(coApplicantCustPayload.getPrimaryKycIdValStatus());
                    boolean isAlternateVerified = Constants.VERIFIED_STS.equalsIgnoreCase(coApplicantCustPayload.getAlternateVoterIdValStatus());
                    String coappKycNo = "";
                    logger.debug("isPrimaryVerified: {}, isAlternateVerified: {}", isPrimaryVerified, isAlternateVerified);
                    boolean hasAlternateId =
                            StringUtils.isNotBlank(coApplicantCustPayload.getAlternateVoterId());
                    boolean hasPrimaryId =
                            StringUtils.isNotBlank(coApplicantCustPayload.getPrimaryKycId());
                    if (isAlternateVerified && hasAlternateId) {
                        // Highest priority
                        coappKycNo = coApplicantCustPayload.getAlternateVoterId();
                    } else if (isPrimaryVerified && hasPrimaryId) {
                        // Fallback
                        coappKycNo = coApplicantCustPayload.getPrimaryKycId();
                    } else {
                        logger.debug(
                                "No valid verified Voter ID found for customerType {}. " +
                                        "Alternate verified={}, Alternate blank={}, " +
                                        "Primary verified={}, Primary blank={}", Constants.COAPPLICANT,
                                isAlternateVerified, !hasAlternateId,
                                isPrimaryVerified, !hasPrimaryId
                        );

                        return Mono.error(new RuntimeException(
                                "No valid verified Voter ID found for the " + Constants.COAPPLICANT + ". Cannot proceed with Dedupe Table Update."
                        ));
                    }
                    request.setSpkycid(coappKycNo.toUpperCase());
                    request.setSpkycname("VOTER-ID");
                }
                Optional<BankDetails> bankDetailsOpt = bankDtlRepo
                        .findByApplicationIdAndCustDtlId(master.getApplicationId(), customerDetails.getCustDtlId());
                if (bankDetailsOpt.isPresent()) {
                    BankDetails bankDetails = bankDetailsOpt.get();
                    BankDetailsPayload bankPayload = gson.fromJson(bankDetails.getPayloadColumn(), BankDetailsPayload.class);
                    logger.debug("BankDetailsPayload: {}", bankPayload);
                    request.setBankAccNo(bankPayload.getAccountNumber());
                    request.setBankname(bankPayload.getBankName());
                    request.setBankBranchName(bankPayload.getBranchName());
                    request.setIfscCode(bankPayload.getIfsc());
                    request.setAccHolderName(bankPayload.getAccountName());
                }
            }

            request.setCb_status(Constants.PASS_STRING);//Pass since this method will called only when the credit check has been pased.
            List<DedupeTableUpdateRequest.LegalDocument> legalDocumentList = new ArrayList<>();
            DedupeTableUpdateRequest.LegalDocument voterId = new DedupeTableUpdateRequest.LegalDocument();
            voterId.setName("VOTER-ID");
            boolean isPrimaryVerified = Constants.VERIFIED_STS.equalsIgnoreCase(custPayload.getPrimaryKycIdValStatus());
            boolean isAlternateVerified = Constants.VERIFIED_STS.equalsIgnoreCase(custPayload.getAlternateVoterIdValStatus());
            String apptKycNo = "";
            logger.debug("isPrimaryVerified: {}, isAlternateVerified: {}", isPrimaryVerified, isAlternateVerified);
            boolean hasAlternateId =
                    StringUtils.isNotBlank(custPayload.getAlternateVoterId());
            boolean hasPrimaryId =
                    StringUtils.isNotBlank(custPayload.getPrimaryKycId());
            if (isAlternateVerified && hasAlternateId) {
                // Highest priority
                apptKycNo = custPayload.getAlternateVoterId();
            } else if (isPrimaryVerified && hasPrimaryId) {
                // Fallback
                apptKycNo = custPayload.getPrimaryKycId();
            } else {
                logger.debug(
                        "No valid verified Voter ID found for customerType {}. " +
                                "Alternate verified={}, Alternate blank={}, " +
                                "Primary verified={}, Primary blank={}", customerType,
                        isAlternateVerified, !hasAlternateId,
                        isPrimaryVerified, !hasPrimaryId
                );

                return Mono.error(new RuntimeException(
                        "No valid verified Voter ID found for the " + customerType + ". Cannot proceed with Dedupe Table Update."
                ));
            }
            voterId.setId(apptKycNo);
            legalDocumentList.add(voterId);
            if ("Driving Licence".equalsIgnoreCase(custPayload.getSecondaryKycType())
                    && StringUtils.isNotEmpty(custPayload.getSecondaryKycId()) && Constants.VERIFIED_STS.equalsIgnoreCase(custPayload.getSecondaryKycIdValStatus())) {
                DedupeTableUpdateRequest.LegalDocument drivingLicence = new DedupeTableUpdateRequest.LegalDocument();
                drivingLicence.setName("DRIVING-ID");
                drivingLicence.setId(custPayload.getSecondaryKycId());
                legalDocumentList.add(drivingLicence);
            }
            request.setLegalDocument(legalDocumentList);
            logger.debug("JSON request for Dedupe Table Update api :" + request);
            logger.debug("Final request object: {}", gson.toJson(request));
            LoanRequestExt loanCreationRequestExt = new LoanRequestExt();
            loanCreationRequestExt.setAppId(master.getAppId());
            loanCreationRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.DEDUPE_TABLE_UPDATE_INTF.getKey()));
            loanCreationRequestExt.setRequestObj(request);
            logger.debug("Dedupe Table Update API: {} ", loanCreationRequestExt.toString());

            setRequestLog(loanCreationRequestExt.toString());

            Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, loanCreationRequestExt,
                    loanCreationRequestExt.getInterfaceName());
            logger.debug("response 1 from the API:" + apiRespMono);

            return apiRespMono.flatMap(val -> {
                logger.debug("response 2 from the API: " + val);
                JSONObject apiResp = new JSONObject(gson.toJson(val));
                logger.debug("JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });

        } catch (Exception e) {
            logger.error("Error occurred while executing the Dedupe Table Update api", e);
            return Mono.just(getFailureApiJson("Error occurred while executing the Dedupe Table Update api", updateApi));
        }
    }

    public Mono<Object> applicantActivation(ApplicationMaster master, Header header, Properties prop) {
        logger.debug("Applicant Activation Started");
        String applicationId = master.getApplicationId();
        String appId = master.getAppId();
        Gson gson = new Gson();
        try {
            Optional<UnnatiIexceedCDHLead> cdhLeadOpt = unnatiIexceedCDHLeadRepo
                    .findByCustomerId(master.getSearchCode2());
            if (!cdhLeadOpt.isPresent()) {
                logger.error("CDH Lead details not found for memberId: {}", master.getSearchCode2());
                return Mono.just(getFailureApiJson("CDH Lead details not found.", Constants.APPLICANT_ACTIVATION));
            }

            UnnatiIexceedCDHLead cdhLead = cdhLeadOpt.get();

            Optional<CustomerDetails> applicantDetailsOpt = custDtlRepo
                    .findByApplicationIdAndAppIdAndCustomerType(applicationId, appId, Constants.APPLICANT);
            if (!applicantDetailsOpt.isPresent()) {
                logger.error("Applicant details not found for applicationId: {}", applicationId);
                return Mono.just(getFailureApiJson("Applicant Details not found", Constants.APPLICANT_CREATION));
            }

            LoanDetails loanDetails = loanDtlsRepo.findByApplicationId(applicationId);
            LoanDetailsPayload loanDetailsPayload = gson.fromJson(loanDetails.getPayloadColumn(), LoanDetailsPayload.class);

            CustomerDetails applicantDetails = applicantDetailsOpt.get();
            CustomerDetailsPayload applicantDetailsPayload = gson.fromJson(applicantDetails.getPayloadColumn(), CustomerDetailsPayload.class);
            boolean isPrimaryVerified = Constants.VERIFIED_STS.equalsIgnoreCase(applicantDetailsPayload.getPrimaryKycIdValStatus());
            boolean isAlternateVerified = Constants.VERIFIED_STS.equalsIgnoreCase(applicantDetailsPayload.getAlternateVoterIdValStatus());
            String apptKycNo = "";
            logger.debug("isPrimaryVerified: {}, isAlternateVerified: {}", isPrimaryVerified, isAlternateVerified);
            boolean hasAlternateId =
                    StringUtils.isNotBlank(applicantDetailsPayload.getAlternateVoterId());
            boolean hasPrimaryId =
                    StringUtils.isNotBlank(applicantDetailsPayload.getPrimaryKycId());
            if (isAlternateVerified && hasAlternateId) {
                // Highest priority
                apptKycNo = applicantDetailsPayload.getAlternateVoterId();
            } else if (isPrimaryVerified && hasPrimaryId) {
                // Fallback
                apptKycNo = applicantDetailsPayload.getPrimaryKycId();
            } else {
                logger.debug(
                        "No valid verified Voter ID found for customerType {}. " +
                                "Alternate verified={}, Alternate blank={}, " +
                                "Primary verified={}, Primary blank={}", Constants.APPLICANT,
                        isAlternateVerified, !hasAlternateId,
                        isPrimaryVerified, !hasPrimaryId
                );

                return Mono.error(new RuntimeException(
                        "No valid verified Voter ID found for the " + Constants.APPLICANT + ". Cannot proceed with Dedupe Table Update."
                ));
            }

            List<AddressDetails> applicantAddressDetailList = addressDtlRepo
                    .findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(applicationId, appId,
                            Constants.INITIAL_VERSION_NO, applicantDetails.getCustDtlId());
            AddressDetails applicantPersonalAddressDetails = null;
            AddressDetails applicantOccupationAddressDetails = null;

            for (AddressDetails addressDetails : applicantAddressDetailList) {
                if (Constants.PERSONAL.equalsIgnoreCase(addressDetails.getAddressType())) {
                    applicantPersonalAddressDetails = addressDetails;
                } else if (Constants.OCCUPATION.equalsIgnoreCase(addressDetails.getAddressType())) {
                    applicantOccupationAddressDetails = addressDetails;
                } else {
                    logger.error("Invalid address type");
                }
            }
            if (null == applicantPersonalAddressDetails || null == applicantOccupationAddressDetails) {
                logger.error("Address details missing for the applicant with application Id : {}", applicationId);
                return Mono.error(new RuntimeException("Address details missing for the customer"));
            }

            AddressDetailsPayload personalAddressPayload = gson
                    .fromJson(applicantPersonalAddressDetails.getPayloadColumn(), AddressDetailsPayload.class);
            AddressDetailsPayload occupationAddressPayload = gson
                    .fromJson(applicantPersonalAddressDetails.getPayloadColumn(), AddressDetailsPayload.class);
            String presentAddressline = "";
            String communicationAddressLine = "";
            String presentState = "";
            String presentCity = "";
            String presentPincode = "";
            String communicationState = "";
            String communicationCity = "";
            String communicationPincode = "";

            for (Address address : personalAddressPayload.getAddressList()) {
                if (address.getAddressType().equalsIgnoreCase("Present")) {
                    presentAddressline = address.getAddressLine1();
                    presentState = address.getState();
                    presentCity = address.getCity();
                    presentPincode = address.getPinCode();
                } else if (address.getAddressType().equalsIgnoreCase("Communication")) {
                    communicationAddressLine = address.getAddressLine1();
                    communicationState = address.getState();
                    communicationCity = address.getCity();
                    communicationPincode = address.getPinCode();
                }
            }

            Optional<CibilDetails> cbDetailsOpt = cibilDtlRepo
                    .findCibilDetailsByCustomerTypeAndApplicationId(Constants.COAPPLICANT, applicationId);
            if (!cbDetailsOpt.isPresent()) {
                return Mono.error(new RuntimeException("BRE details not found"));
            }
            CibilDetails cbDetails = cbDetailsOpt.get();

            Optional<BankDetails> bankDetailsOpt = bankDtlRepo
                    .findByApplicationIdAndCustDtlId(applicationId, applicantDetails.getCustDtlId());
            if (!bankDetailsOpt.isPresent()) {
                return Mono.error(new RuntimeException("Bank details not found"));
            }
            BankDetails bankDetails = bankDetailsOpt.get();
            BankDetailsPayload bankDetailsPayload = gson.fromJson(bankDetails.getPayloadColumn(), BankDetailsPayload.class);

            CustomerActivationRequest customerCreationRequest = new CustomerActivationRequest();
            CustomerActivationRequest.Body body = new CustomerActivationRequest.Body();
            CustomerActivationRequest.LegalDocument legalDocument = new CustomerActivationRequest.LegalDocument();
            legalDocument.setId(apptKycNo);
            legalDocument.setLegalHolderName(applicantDetailsPayload.getNamePerKyc());
            legalDocument.setName("VOTER");
            body.setLegalDocument(Collections.singletonList(legalDocument));

            CustomerActivationRequest.PresentAddress presentAddress = new CustomerActivationRequest.PresentAddress();
            presentAddress.setPermanentAddressLine(presentAddressline);
            body.setPresentAddress(Collections.singletonList(presentAddress));

            CustomerActivationRequest.CommunicationAddress communicationAddress = new CustomerActivationRequest.CommunicationAddress();
            communicationAddress.setAddress(communicationAddressLine);
            body.setCommunicationAddress(Collections.singletonList(communicationAddress));

            body.setMember(applicantDetailsPayload.getFirstName());
            body.setCustomerIdTemp(String.valueOf(cdhLead.getT24CustId()));

            body.setCustomerQualify("CREDIT");
            body.setCustomerType("I");
            body.setGender(mapGender(applicantDetailsPayload.getGender()));
            body.setGroupId(cdhLead.getGroupId());
            body.setCustomerlanguage("01");
            body.setResidence("IN");
            body.setResident("Y");
            body.setSector("1001");
            String formattedApplicationDate = master.getApplicationDate().format(localDateFormat);

            String formattedCBDate = cbDetails.getCbDate().format(localDateFormat);
            body.setCbDate(formattedCBDate.replace("-", ""));
            body.setKendraId(master.getKendraId());
            body.setCustomerName(applicantDetailsPayload.getFirstName());
            body.setLastName(applicantDetailsPayload.getLastName());
            String religion = applicantDetailsPayload.getReligion().toUpperCase();
            body.setReligion(religion.toUpperCase());
            body.setDistrictId(presentCity);
            body.setNationality("IN");
            body.setPhone(applicantDetails.getMobileNumber());
            body.setDob(applicantDetailsPayload.getDob().replace("-", ""));
            body.setPinCode(presentPincode);
            body.setName(applicantDetailsPayload.getFirstName());
            body.setVillage(presentAddressline);
            body.setOpeningDate(formattedApplicationDate.replace("-", ""));
            body.setMaritalStatus(applicantDetailsPayload.getMaritalStatus().toUpperCase());

            body.setBankACNo(bankDetailsPayload.getAccountNumber());
            body.setAccountHolderName(bankDetailsPayload.getAccountName());
            body.setAccountNumber(bankDetailsPayload.getAccountNumber());
            body.setBankifsccode(bankDetailsPayload.getIfsc());
            body.setBranchId(master.getBranchId());
            body.setCompanyIdTemp(master.getBranchId());
            body.setState(presentState);

            body.setFatherName(applicantDetailsPayload.getFathersName());
            String caste = applicantDetailsPayload.getCaste();
            if (applicantDetailsPayload.getReligion().equalsIgnoreCase("HINDU")) {
                if (StringUtils.isBlank(caste) || "OTHER".equalsIgnoreCase(caste)) {
                    caste = "OTHER";
                } else {
                    caste = caste.trim().toUpperCase();
                    caste = caste.length() >= 3 ? caste.substring(0, 3) : caste;
                }
                body.setCaste(caste);
            }
            body.setPinCode1(communicationPincode);
            body.setState1(communicationState);
            body.setDistrictId1(communicationCity);
            body.setVillage1(communicationAddressLine);
            body.setProspectCustomerId(master.getSearchCode2()); //As searchCode2 is storing the prospect customer ID from Digi Agil

            if (applicantDetailsPayload.getMaritalStatus().equalsIgnoreCase(Constants.MARRIED)) {
                Optional<CustomerDetails> coApplicantDetailsOpt = custDtlRepo
                        .findByApplicationIdAndAppIdAndCustomerType(applicationId, appId, Constants.COAPPLICANT);
                if (!coApplicantDetailsOpt.isPresent()) {
                    logger.error("Spouse details not found for applicationId: {}", applicationId);
                    return Mono.error(new RuntimeException("Spouse details not found for married applicant"));
                }
                CustomerDetails coApplicantDetails = coApplicantDetailsOpt.get();
                CustomerDetailsPayload coApplicantDetailsPayload = gson.fromJson(coApplicantDetails.getPayloadColumn(), CustomerDetailsPayload.class);
                CustomerActivationRequest.SpouseKyc spouseKyc = new CustomerActivationRequest.SpouseKyc();
                spouseKyc.setName("VOTER-ID");
                boolean isCoappPrimaryVerified = Constants.VERIFIED_STS.equalsIgnoreCase(coApplicantDetailsPayload.getPrimaryKycIdValStatus());
                boolean isCoappAlternateVerified = Constants.VERIFIED_STS.equalsIgnoreCase(coApplicantDetailsPayload.getAlternateVoterIdValStatus());
                String coappKycNo = "";
                logger.debug("isCoappPrimaryVerified: {}, isCoappAlternateVerified: {}", isCoappPrimaryVerified, isCoappAlternateVerified);
                boolean coappHasAlternateId =
                        StringUtils.isNotBlank(coApplicantDetailsPayload.getAlternateVoterId());
                boolean coappHasPrimaryId =
                        StringUtils.isNotBlank(coApplicantDetailsPayload.getPrimaryKycId());
                if (isCoappAlternateVerified && coappHasAlternateId) {
                    // Highest priority
                    coappKycNo = coApplicantDetailsPayload.getAlternateVoterId();
                } else if (isCoappPrimaryVerified && coappHasPrimaryId) {
                    // Fallback
                    coappKycNo = coApplicantDetailsPayload.getPrimaryKycId();
                } else {
                    logger.debug(
                            "No valid verified Voter ID found for spouse. " +
                                    "Alternate verified={}, Alternate blank={}, " +
                                    "Primary verified={}, Primary blank={}",
                            isCoappAlternateVerified, !coappHasAlternateId,
                            isCoappPrimaryVerified, !coappHasPrimaryId
                    );
                    return Mono.error(new RuntimeException(
                            "No valid verified Voter ID found for the spouse. Cannot proceed with Applicant Activation."
                    ));
                }
                spouseKyc.setId(coappKycNo);
                body.setSpouseKyc(Collections.singletonList(spouseKyc));
                body.setSpName(coApplicantDetailsPayload.getFirstName());
                body.setSpouseDob(coApplicantDetailsPayload.getDob().replace("-", ""));
            }

            Map<String, CustomerActivationRequest.Body> mapReq = new HashMap<>();
            mapReq.put("body", body);
            logger.debug("JSON request for coapplicant creation api :" + mapReq);
            logger.debug("Final request object: {}", new Gson().toJson(body));

            LoanRequestExt loanCreationRequestExt = new LoanRequestExt();
            loanCreationRequestExt.setAppId(appId);
            loanCreationRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.APPLICANT_ACTIVATION_INTF.getKey()));
            loanCreationRequestExt.setRequestObj(mapReq);
            logger.debug("applicant activation API: {} ", loanCreationRequestExt.toString());

            setRequestLog(loanCreationRequestExt.toString());

            Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, loanCreationRequestExt,
                    loanCreationRequestExt.getInterfaceName());
            logger.debug("response 1 from the API:" + apiRespMono);

            return apiRespMono.flatMap(val -> {
                logger.debug("response 2 from the API: " + val);
                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });
        } catch (Exception e) {
            logger.error("Error during customerCreation wiht error message : {}", e.getMessage(), e);
            return Mono.just(getFailureApiJson("Error occured while executing customer creation API", Constants.APPLICANT_CREATION));
        }
    }

    private String mapMaritalStatus(String maritalStatus) {
        if (null == maritalStatus || StringUtils.isBlank(maritalStatus)) {
            return "UNMARRIED";
        }
        if (maritalStatus.equalsIgnoreCase("M")) {
            return "MARRIED";
        }
        if (maritalStatus.equalsIgnoreCase("U")) {
            return "UNMARRIED";
        }
        return maritalStatus.toUpperCase();
    }

    private String mapGender(String gender) {
        if (gender == null || StringUtils.isBlank(gender)) {
            return "O";
        }
        char first = Character.toUpperCase(gender.trim().charAt(0));
        return (first == 'M' || first == 'F' || first == 'O')
                ? String.valueOf(first)
                : "O";
    }

    public Mono<Object> coapplicantCreation(String applicationId, String appId, String memId, Header header, Properties prop, boolean updateCall, String coapplCreationId, boolean coapplUpdate) {

        logger.debug("Create & Updated Co Applicant Started");
        Gson gsonObj = new Gson();
        try {
			/*try {
				prop = CommonUtils.readPropertyFile();
							} catch (IOException e) {
				logger.error("Error while reading property file in populateRejectedData ", e);
			}*/
//			CustomerDetailsPayload appPayload = null;
            CustomerDetailsPayload coappPayload = new CustomerDetailsPayload();
            OccupationDetails coApplicant = new OccupationDetails();
            OccupationDetailsPayload occAppPayload = new OccupationDetailsPayload();
            OccupationDetailsPayload occCoappPayload = new OccupationDetailsPayload();
            String applicantCustId = "";
            String coApplicantCustId = "";
            String coappltGender = "";
            String phnNum = "";
            String custName = "";
            String voterId = "";
            String maritalStatus = "";
            String custId = "";
            CustomerDetailsPayload appPayload = null;
            ApplyLoanRequestFields custFields = null;
            ApplicationMaster applicationMasterData = null;
            List<ApplicationMaster> appMasterDb = applicationMasterRepo
                    .findByAppIdAndApplicationId(Constants.APPID, applicationId);

            if (null != appMasterDb && !appMasterDb.isEmpty()) {
                for (ApplicationMaster appMaster : appMasterDb) {
                    applicationMasterData = appMaster;
                }
                String product = applicationMasterData.getProductCode();
                logger.debug("product :" + product);
                custFields = getCustomerData(applicationMasterData, applicationId, Constants.APPID, 1);
            }
            for (CustomerDetails custDtl : custFields.getCustomerDetailsList()) {

                logger.debug("customer Type : " + custDtl.getCustomerType());
                if (custDtl.getCustomerType().equalsIgnoreCase("Applicant")) {
                    applicantCustId = String.valueOf(custDtl.getCustDtlId());
                    custId = applicantCustId;
                    logger.debug("applicantCustId : " + applicantCustId);
                    appPayload = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    logger.debug("custApplicantPayload :" + appPayload);
                } else if (custDtl.getCustomerType().equalsIgnoreCase(Constants.COAPPLICANT)) {
                    coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
                    logger.debug("coApplicantCustId : " + coApplicantCustId);
                    coappPayload = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    coappltGender = coappPayload.getGender();
                    voterId = coappPayload.getPrimaryKycId();
                    maritalStatus = coappPayload.getMaritalStatus();
                    phnNum = custDtl.getMobileNumber();
                    custName = custDtl.getCustomerName();
                    logger.debug("phnNum" + phnNum);
                    logger.debug("custName" + custName);
                    logger.debug("custCo-ApplicantPayload :" + coappPayload);
                }
            }
            String occup = normalize(appPayload.getOccupation());
            if (occup.equalsIgnoreCase(Constants.SELF_EMPLOYED)) {
                custId = applicantCustId;
                logger.debug("Custoemrt ID for address fetch :" + custId);
            } else {
                custId = coApplicantCustId;
            }
            for (OccupationDetailsWrapper applicantwrpr : custFields.getOccupationDetailsWrapperList()) {
                logger.debug("applicantCustId " + applicantCustId);
                if (String.valueOf(applicantwrpr.getOccupationDetails().getCustDtlId()).equals(applicantCustId)) {
                    occAppPayload = gsonObj.fromJson(applicantwrpr.getOccupationDetails().getPayloadColumn(),
                            OccupationDetailsPayload.class);
                    logger.debug("occupationApplicantPayload : " + occAppPayload.toString());
                } else if (String.valueOf(applicantwrpr.getOccupationDetails().getCustDtlId())
                        .equals(coApplicantCustId)) {
                    coApplicant = applicantwrpr.getOccupationDetails();
                    occCoappPayload = gsonObj.fromJson(applicantwrpr.getOccupationDetails().getPayloadColumn(),
                            OccupationDetailsPayload.class);
                    logger.debug("occupationCo-appliocantPayload : " + occCoappPayload.toString());
                }
            }
            String relation = prop.getProperty(CobFlagsProperties.RELATIONSHIP_CODES.getKey());

            String coappRelation = "";
            JSONObject relationshipCodes = new JSONObject(relation);

            String inputRelation = normalize(StringUtils.isEmpty(coappPayload.getRelationShipWithApplicant()) ? "" : coappPayload.getRelationShipWithApplicant());

            for (String key : relationshipCodes.keySet()) {
                if (normalize(key).equalsIgnoreCase(inputRelation)) {
                    coappRelation = relationshipCodes.getString(key);
                    break;
                }
            }

            logger.debug("coappRelation code" + coappRelation);
            List<CustomerDetails> customerDetailsList = custFields.getCustomerDetailsList();

// Always resolve the applicant's custDtlId
            String applicantCustDtlId = customerDetailsList.stream()
                    .filter(c -> c.getCustomerType().equals(Constants.APPLICANT))
                    .findFirst()
                    .map(c -> c.getCustDtlId().toString())
                    .orElseThrow(() -> new IllegalStateException("Applicant not found"));

// Find matching bank details for the applicant
            BankDetailsPayload bankPayload = custFields.getBankDetailsWrapperList().stream()
                    .filter(b -> b.getBankDetails().getCustDtlId().toString().equals(applicantCustDtlId))
                    .findFirst()
                    .map(b -> gsonObj.fromJson(b.getBankDetails().getPayloadColumn(), BankDetailsPayload.class))
                    .orElseThrow(() -> new IllegalStateException("No bank details found for applicant"));

            CoapplicantCreationRequestFields req = new CoapplicantCreationRequestFields();

            // Specifically for coapplicant creation and not coapplicant updation
            if (!updateCall) {
                String storedDOB = "";
                storedDOB = coappPayload.getDob().replace("-", "");

                // First Name
                CustomerFirstNameRequestField firstNameObj = new CustomerFirstNameRequestField();
                firstNameObj.setFirstName(custName);
                List<CustomerFirstNameRequestField> firstNameList = new ArrayList<>();
                firstNameList.add(firstNameObj);
                req.setCustomerFirstName(firstNameList);

                // Short Name
                CustomerShortNameRequestField shortNameObj = new CustomerShortNameRequestField();
                shortNameObj.setShortName(".."); // According to API spec
                List<CustomerShortNameRequestField> shortNameList = new ArrayList<>();
                shortNameList.add(shortNameObj);
                req.setCustomerShortName(shortNameList);

                //other details
                req.setDateOfBirth(Long.parseLong(storedDOB));
                req.setGender(coappltGender.toUpperCase());
                req.setMaritalstatus(maritalStatus.toUpperCase());
                req.setVoterId(voterId);

                // Basic info
                String title = coappPayload.getTitle();
                req.setTitle(title.endsWith(".") ? title : title + ".");
                req.setFamilyName("");

                // RationCard Pan DL Passport
                String secKycType = coappPayload.getSecondaryKycType();
                String secKycId = coappPayload.getSecondaryKycId();
                String kyc = secKycType != null ? secKycType.toLowerCase() : "";
                req.setRationCardNumber("");
                req.setPanIdNumber(kyc.contains("pan") ? secKycId : "");
                req.setDlNumber(kyc.contains("driving") || kyc.contains("license") ? secKycId : "");
                req.setPassport(kyc.contains("passport") ? secKycId : "");
                req.setOtherGovernmentId("");

                //BSN
                String bsn = prop.getProperty(CobFlagsProperties.BUSINESS_CODES.getKey());
                JSONObject js = new JSONObject(bsn);
                String nameOfbsn = "";

                if (js.has(occCoappPayload.getNatureOfOccupation())) {
                    nameOfbsn = js.getString(occCoappPayload.getNatureOfOccupation());
                    System.out.println("Matched value: " + nameOfbsn);
                    logger.debug("Matched Value :" + nameOfbsn);
                } else {
                    logger.debug("No match found");
                }

                req.setNameOfBSN(nameOfbsn);

                req.setEmployeeNumber("");  // According to API spec

            }
            // Phone Number
            if (phnNum == null || phnNum.trim().isEmpty()) {
                phnNum = prop.getProperty(CobFlagsProperties.COAPPL_PHONENUMBER_DEFAULT.getKey());
                logger.debug("Default phone num" + phnNum);
            }
            PhoneNumberRequestField phoneObj = new PhoneNumberRequestField();
            phoneObj.setPhoneNumber(phnNum);
            List<PhoneNumberRequestField> phoneList = new ArrayList<>();
            phoneList.add(phoneObj);
            req.setPhoneNumber(phoneList);

            //Occuaption
            String occupation = prop.getProperty(CobFlagsProperties.CUSTOMER_OCCUPATION.getKey());
            JSONObject arr = new JSONObject(occupation);
            String custOccupation = "";

            String normalizedInput = normalize(coappPayload.getOccupation());

            Iterator<String> keys = arr.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (normalize(key).equalsIgnoreCase(normalizedInput)) {
                    custOccupation = arr.getString(key);
                    break;
                }
            }
            logger.debug("custOccupation : " + custOccupation);

            req.setCustomerOccupation(custOccupation);

            // Addresses
            JSONArray addrArray = getAllAddresses(custFields, custId);
            List<CustomerAddressRequestField> addrList = new Gson().fromJson(addrArray.toString(), new TypeToken<List<CustomerAddressRequestField>>() {
            }.getType());
            req.setCustomerAddress(addrList);

            //Co Applicant
            CoApplicantDetailRequestField coappObj = new CoApplicantDetailRequestField();
            coappObj.setCoApplicantCustomerID(updateCall && coapplUpdate ? coapplCreationId : "");//As per Api doc
            coappObj.setCoApplicantRelation(updateCall && coapplUpdate ? coappRelation : "");
            List<CoApplicantDetailRequestField> coAppList = new ArrayList<>();
            coAppList.add(coappObj);
            req.setCoApplicantDetails(coAppList);

            // Beneficiary details
            req.setBeneficiaryBankAccountNum(updateCall ? bankPayload.getAccountNumber() : "");
            req.setBeneficirayIfscCode(updateCall ? bankPayload.getIfsc() : "");
            req.setBeneficiaryBankName(updateCall ? bankPayload.getBankName() : "");
            req.setBeneficiaryBranchkName(updateCall ? bankPayload.getBranchName() : "");

            req.setCustomerIdTemp(memId);
            req.setCompanyIdTemp(applicationMasterData.getBranchId());
            Map<String, CoapplicantCreationRequestFields> mapReq = new HashMap<>();
            mapReq.put("body", req);
            logger.debug("JSON request for coapplicant creation api :" + mapReq);
            logger.debug("Final request object: {}", new Gson().toJson(req));

            LoanRequestExt loanCreationRequestExt = new LoanRequestExt();
            loanCreationRequestExt.setAppId(appId);
            loanCreationRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.COAPPLICANT_UPDATION_INTF.getKey()));
            loanCreationRequestExt.setRequestObj(mapReq);
            logger.debug("Coapplicant creation API: {} ", loanCreationRequestExt.toString());

            setRequestLog(loanCreationRequestExt.toString());

            Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, loanCreationRequestExt,
                    loanCreationRequestExt.getInterfaceName());
            logger.debug("response 1 from the API:" + apiRespMono);

            return apiRespMono.flatMap(val -> {
                logger.debug("response 2 from the API: " + val);
                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });

        } catch (Exception e) {
            logger.error("Error occurred while executing the Cust Orchestration api, error = " + e);
            return Mono.just(adapterUtil.setError("Error occurred while executing the Cust Orchestration api.", "1"));
        }
    }

    private static String normalize(String str) {
        return str.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    public JSONArray getAllAddresses(ApplyLoanRequestFields req, String custId) {

        Gson gson = new Gson();
        JSONArray finalArr = new JSONArray();

        try {
            for (AddressDetails addr : req.getAddressDetailsWrapperList().get(0).getAddressDetailsList()) {
                if (String.valueOf(addr.getCustDtlId()).equalsIgnoreCase(custId)) {

                    if (addr.getAddressType().equalsIgnoreCase("Occupation")) {
                        AddressDetailsPayload payload = gson.fromJson(addr.getPayloadColumn(), AddressDetailsPayload.class);

                        for (Address address : payload.getAddressList()) {
                            JSONObject addrObj = new JSONObject();
                            String country = address.getCountry().equalsIgnoreCase("India") ? "IN" : " ";
                            addrObj.put("address1", address.getAddressLine1());
                            addrObj.put(Constants.ADDRESS_TYPE, address.getAddressType().toUpperCase());
                            addrObj.put("pinCode", address.getPinCode());
                            addrObj.put("residenceArea", address.getArea());
                            addrObj.put("residenceCity", address.getCity());
                            addrObj.put("residenceCountry", country);
                            addrObj.put("residenceState", address.getState());
                            addrObj.put("address2", address.getAddressLine2());
                            addrObj.put("address3", address.getAddressLine3());
                            addrObj.put("landmark", address.getLandMark() == null ? "" : address.getLandMark());

                            finalArr.put(addrObj);
                        }

                    } else if (addr.getAddressType().equalsIgnoreCase("Personal")) {
                        AddressDetailsPayload payload = gson.fromJson(addr.getPayloadColumn(), AddressDetailsPayload.class);

                        for (Address address : payload.getAddressList()) {
                            if (Constants.SECONDARY_KYC.equalsIgnoreCase(address.getAddressType())) {
                                continue;
                            }
                            String country = address.getCountry().equalsIgnoreCase("India") ? "IN" : " ";
                            JSONObject addrObj = new JSONObject();
                            String landMark = address.getLandMark() == null ? "" : StringUtils.defaultString(address.getLandMark());
                            addrObj.put("address1", StringUtils.defaultString(address.getAddressLine1()));
                            addrObj.put(Constants.ADDRESS_TYPE, StringUtils.defaultString(address.getAddressType().toUpperCase()));
                            addrObj.put("pinCode", StringUtils.defaultString(address.getPinCode()));
                            addrObj.put("residenceArea", StringUtils.defaultString(address.getArea()));
                            addrObj.put("residenceCity", StringUtils.defaultString(address.getCity()));
                            addrObj.put("residenceCountry", country);
                            addrObj.put("residenceState", StringUtils.defaultString(address.getState()));
                            addrObj.put("address2", StringUtils.defaultString(address.getAddressLine2()));
                            addrObj.put("address3", StringUtils.defaultString(address.getAddressLine3()));
                            addrObj.put("landMark", landMark);
                            String type = address.getAddressType();
                            if ("Present".equalsIgnoreCase(type)) {
                                finalArr.put(addrObj);
                            } else if ("Permanent".equalsIgnoreCase(type)) {
                                finalArr.put(addrObj);
                            } else if ("Communication".equalsIgnoreCase(type)) {
                                finalArr.put(addrObj);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in Address details list {}", e);
        }
        return finalArr;
    }

    public Mono<Object> toConnectCabinet(String applicationId, Properties prop, String appId, Header header) {

        Gson gson = new Gson();
        try {

            com.iexceed.appzillonbanking.cob.entity.ngoConnectCabinet.NGOExecuteAPIBDORequest docRequest = new com.iexceed.appzillonbanking.cob.entity.ngoConnectCabinet.NGOExecuteAPIBDORequest();

            NGOExecuteAPIBDO executeReq = new NGOExecuteAPIBDO();
            com.iexceed.appzillonbanking.cob.entity.ngoConnectCabinet.InputData inputData = new com.iexceed.appzillonbanking.cob.entity.ngoConnectCabinet.InputData();

            NGOConnectCabinetInput cabinetInput = new NGOConnectCabinetInput();
            cabinetInput.setOption(prop.getProperty(CobFlagsProperties.DMS_CONNECT_OPTION.getKey()));
            cabinetInput.setUserExist(prop.getProperty(CobFlagsProperties.DMS_USER_EXIST.getKey()));
            cabinetInput.setCabinetName(prop.getProperty(CobFlagsProperties.DMS_CABINET_NAME.getKey()));
            cabinetInput.setUserName(prop.getProperty(CobFlagsProperties.DMS_USERNAME.getKey()));
            cabinetInput.setUserPassword(prop.getProperty(CobFlagsProperties.DMS_USERPASSWORD.getKey()));
            cabinetInput.setLocale(prop.getProperty(CobFlagsProperties.DMS_LOCALE.getKey()));

            inputData.setNgoConnectCabinetInput(cabinetInput);
            executeReq.setInput(inputData);
            executeReq.setBase64Encoded(Constants.NO);
            executeReq.setLocale(prop.getProperty(CobFlagsProperties.DMS_LOCALE.getKey()));

            docRequest.setNgoExecuteAPIBDO(executeReq);

            logger.debug("Final request of Connect cabinet API :" + docRequest);

            DmsRequestExt dmsRequest = new DmsRequestExt();
            dmsRequest.setAppId(appId);
            dmsRequest.setInterfaceName(prop.getProperty(CobFlagsProperties.DMS_CONNECT_CABINET_INTF.getKey()));
            dmsRequest.setRequestObj(docRequest);
            logger.debug("Connect cabinet from the API: {} ", dmsRequest.toString());

            Mono<Object> apiRespMono = interfaceAdapter.executeInterfaceAdapter(dmsRequest,
                    dmsRequest.getInterfaceName(), header);

            logger.debug("response 1 from the API1: {} ", apiRespMono);

            return apiRespMono.flatMap(val -> {
                logger.debug("response 2 from the API: " + val);
                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });

        } catch (Exception e) {
            logger.error("Error occurred while connecting to cabinet for DMS processing", e);
            return Mono.error(new RuntimeException("Error occurred while connecting to cabinet for DMS processing"));
        }
    }

    public Mono<Object> toDisconnectCabinet(String applicationId, Properties prop, String appId, Header header, String userDBId) {
        Gson gson = new Gson();
        try {
            com.iexceed.appzillonbanking.cob.entity.ngoConnectCabinet.NGOExecuteAPIBDORequest docRequest = new com.iexceed.appzillonbanking.cob.entity.ngoConnectCabinet.NGOExecuteAPIBDORequest();

            NGOExecuteAPIBDO executeReq = new NGOExecuteAPIBDO();
            com.iexceed.appzillonbanking.cob.entity.ngoConnectCabinet.InputData inputData = new com.iexceed.appzillonbanking.cob.entity.ngoConnectCabinet.InputData();
            NGODisconnectCabinetInput cabinetInput = new NGODisconnectCabinetInput();
            cabinetInput.setOption(prop.getProperty(CobFlagsProperties.DMS_DISCONNECT_OPTION.getKey()));
            cabinetInput.setCabinetName(prop.getProperty(CobFlagsProperties.DMS_CABINET_NAME.getKey()));
            cabinetInput.setUserDBId(userDBId);

            inputData.setNgoDisconnectCabinetInput(cabinetInput);
            executeReq.setInput(inputData);

            executeReq.setBase64Encoded(Constants.NO);
            executeReq.setLocale(prop.getProperty(CobFlagsProperties.DMS_LOCALE.getKey()));

            docRequest.setNgoExecuteAPIBDO(executeReq);
            logger.debug("Final request of Connect cabinet API :" + docRequest);

            DmsRequestExt dmsRequest = new DmsRequestExt();
            dmsRequest.setAppId(appId);
            dmsRequest.setInterfaceName(prop.getProperty(CobFlagsProperties.DMS_CONNECT_CABINET_INTF.getKey()));
            dmsRequest.setRequestObj(docRequest);
            logger.debug("DisConnect cabinet from the API: {} ", dmsRequest.toString());

            Mono<Object> apiRespMono = interfaceAdapter.executeInterfaceAdapter(dmsRequest,
                    dmsRequest.getInterfaceName(), header);

            logger.debug("response 1 from the API1: {} ", apiRespMono);

            return apiRespMono.flatMap(val -> {
                logger.debug("response 2 from the API: " + val);
                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });

        } catch (Exception e) {
            logger.error("Error occurred while disconnecting cabinet for DMS processing", e);
            return Mono.error(new RuntimeException("Error occurred while disconnecting cabinet for DMS processing"));
        }
    }

    public Mono<Object> toAddFolder(ApplicationMaster masterObj, Properties prop, Header header, String userDBId,
                                    Folder folderInput) {

        Gson gson = new Gson();
        try {
            Folder folder = new Folder();
            String t24CustomerId = masterObj.getSearchCode2();
            if(Constants.OPENMARKET_LOAN_PRODUCT_CODE.equalsIgnoreCase(masterObj.getProductCode())) {
                t24CustomerId = masterObj.getApplicantT24Id();
            }
            folder.setParentFolderIndex(prop.getProperty(CobFlagsProperties.DMS_PARENT_FOLDER_INDEX.getKey()));
            folder.setFolderName(t24CustomerId + "_" + masterObj.getApplicationId());
            folder.setCreationDateTime(resolveValue(folderInput.getCreationDateTime(), prop, CobFlagsProperties.DMS_CREATION_DATETIME.getKey()));
            folder.setAccessType(resolveValue(folderInput.getAccessType(), prop, CobFlagsProperties.DMS_ACCESS_TYPE.getKey()));
            folder.setImageVolumeIndex(resolveValue(folderInput.getImageVolumeIndex(), prop, CobFlagsProperties.DMS_IMAGE_VOLUME_INDEX.getKey()));
            folder.setFolderType(resolveValue(folderInput.getFolderType(), prop, CobFlagsProperties.DMS_FOLDER_TYPE.getKey()));
            folder.setLocation(resolveValue(folderInput.getLocation(), prop, CobFlagsProperties.DMS_LOCATION.getKey()));
            folder.setComment(resolveValue(folderInput.getComment(), prop, CobFlagsProperties.DMS_COMMENT.getKey()));
            folder.setOwner(resolveValue(folderInput.getOwner(), prop, CobFlagsProperties.DMS_OWNER.getKey()));

            NGOAddFolderInput addFolderInput = new NGOAddFolderInput();
            addFolderInput.setOption(prop.getProperty(CobFlagsProperties.DMS_ADD_FOLDER_OPTION.getKey()));
            addFolderInput.setCabinetName(prop.getProperty(CobFlagsProperties.DMS_ADD_FOLDER_CABINET_NAME.getKey()));
            addFolderInput.setUserDBId(userDBId);
            addFolderInput.setFolderInput(folder);

            InputData inputData = new InputData();
            inputData.setNgoAddFolderInput(addFolderInput);

            com.iexceed.appzillonbanking.cob.entity.ngoAddFolder.NGOExecuteAPIBDO executeAPIBDO = new com.iexceed.appzillonbanking.cob.entity.ngoAddFolder.NGOExecuteAPIBDO();
            executeAPIBDO.setInputData(inputData);

            NGOExecuteAPIBDORequest request = new NGOExecuteAPIBDORequest();
            request.setNgoExecuteAPIBDO(executeAPIBDO);

            logger.debug("Final request of Add folder API :" + request);

            DmsRequestExt dmsRequest = new DmsRequestExt();
            dmsRequest.setAppId(masterObj.getAppId());
            dmsRequest.setInterfaceName(prop.getProperty(CobFlagsProperties.DMS_ADD_FOLDER_INTF.getKey()));
            dmsRequest.setRequestObj(request);
            logger.debug("Add Folder Request from the API: {} ", dmsRequest.toString());

            Mono<Object> apiRespMono = interfaceAdapter.executeInterfaceAdapter(dmsRequest,
                    dmsRequest.getInterfaceName(), header);

            logger.debug("response 1 from the API1: {} ", apiRespMono);

            return apiRespMono.flatMap(val -> {
                logger.debug("response 2 from the API: " + val);
                JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                logger.debug("JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });

        } catch (Exception e) {
            logger.error("Error occurred while Adding folder for DMS processing", e);
            return Mono.error(new RuntimeException("Error occurred while Adding folder for DMS processing: " + e.getMessage(), e));
        }
    }

    public Mono<Object> toAddDocument(ApplicationMaster masterObj, Properties prop,
                                      String folderIndex, String userDBId) {

        NGOAddDocumentBDO documentBDO = new NGOAddDocumentBDO();
        Gson gson = new Gson();
        List<File> tempFiles = new ArrayList<>();
        try {
            logger.debug("DMS Upload for Application ID :" + masterObj.getApplicationId());

            String applicationId = masterObj.getApplicationId();

            String interfaceFileName = prop.getProperty(CobFlagsProperties.DMS_ADD_DOCUMENT_INTF.getKey()) + Constants.APZINTERFACE_EXT;
            logger.debug("interfaceFileName =" + interfaceFileName);

            String interfaceFileContent = adapterUtil.readInterfaceContentFromServer(interfaceFileName);
            logger.debug("interfaceFileContent = " + interfaceFileContent);

            JSONObject interfaceJsonContent = new JSONObject(interfaceFileContent);

            String endPointUrl = interfaceJsonContent.get(Constants.ENDPOINT_URL).toString();
            logger.debug("endPointUrl = " + endPointUrl);

            URL url = new URL(endPointUrl);

            String base = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort(); // Till port number
            String path = url.getPath();

            WebClient webClient = WebClient.builder()
                    .baseUrl(base)
                    .build();
            String basePath = prop.getProperty(CobFlagsProperties.DMS_UPLOAD_PATH.getKey());
            File[] folders = {
                    new File(basePath + applicationId + "/" + Constants.MANUAL),
                    new File(basePath + applicationId + "/" + Constants.GENERATED),
                    new File(basePath + applicationId + "/" + Constants.WELCOMEKIT)
            };

            List<File> generatedFiles = new ArrayList<>();
            Map<File, String> uploadNames = new HashMap<>();

            for (File folder : folders) {
                if (folder.exists() && folder.isDirectory()) {
                    File[] files = folder.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            String originalName = file.getName();
                            String uploadName = originalName; // default

                            if (folder.getName().equals(Constants.MANUAL)) {
                                String[] parts = originalName.split("_", 2);
                                String documentType = (parts.length > 1) ? parts[1] : originalName;
                                uploadName = Constants.MANUAL + "_" + documentType;
                            } else if (folder.getName().equals(Constants.GENERATED)) {
                                String[] parts = originalName.split("_", 2);
                                String documentType = (parts.length > 1) ? parts[1] : originalName;
                                uploadName = Constants.GENERATED + "_" + documentType;
                            } else if (folder.getName().equals(Constants.WELCOMEKIT)) {
                                String[] parts = originalName.split("_", 2);
                                String documentType = (parts.length > 1) ? parts[1] : originalName;
                                uploadName = documentType;
                            }

                            generatedFiles.add(file);
                            uploadNames.put(file, uploadName);
                        }
                    }
                } else {
                    logger.warn("Skipping folder (not found or not a directory): {}", folder.getAbsolutePath());
                }
            }


            CreateModifyUserRequest extReq = new CreateModifyUserRequest();
            CustomerDataFields custmrDataFields = null;
            Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepo
                    .findByAppIdAndApplicationIdAndVersionNum(masterObj.getAppId(), applicationId, Constants.INITIAL_VERSION_NO);

            if (applicationMasterOpt.isPresent()) {
                ApplicationMaster applicationMasterData = applicationMasterOpt.get();
                String product = applicationMasterData.getProductCode();
                logger.debug("product :" + product);

                custmrDataFields = cobService.getCustomerData(applicationMasterData, applicationId, masterObj.getAppId(), Constants.INITIAL_VERSION_NO);
            }
            List<CibilDetailsWrapper> wrapperList = custmrDataFields.getCibilDetailsWrapperList();

            if (wrapperList != null && !wrapperList.isEmpty()) {
                for (CibilDetailsWrapper wrapper : wrapperList) {
                    if (wrapper != null) {
                        CibilDetails cibilDetails = wrapper.getCibilDetails();
                        CibilDetailsPayload cibilPayload = gson.fromJson(cibilDetails.getPayloadColumn(), CibilDetailsPayload.class);
                        if (cibilPayload != null) {
                            String cbLoanId = cibilPayload.getCbLoanId();
                            if (cbLoanId != null) {
                                File file = new File(basePath + applicationId + "/" + cbLoanId + Constants.PDF_EXTENSION);
                                if (file.exists() && file.isFile()) {
                                    generatedFiles.add(file);
                                    logger.debug("Added file from cbLoanId: {}", file.getAbsolutePath());
                                } else {
                                    logger.warn("CB report PDF not found for applicationId={}, cbLoanId={}, path={}",
                                            applicationId, cbLoanId, file.getAbsolutePath());
                                }
                            }
                        }
                    }
                }
            }

            extReq.setAppId(masterObj.getAppId());
            extReq.setInterfaceName("Document Details for DMS");
            extReq.setUserId(applicationId);
            extReq.setRequestObj(custmrDataFields);
            logger.debug("Document Details for DMS request :" + extReq);
            JSONObject masterData = new JSONObject(prop.getProperty(CobFlagsProperties.WORKITEM_MASTERS.getKey()));
            logger.debug("Master data fetch completed :: {} ", masterData);
            Map<BigDecimal, String> applicantTypeMap = new HashMap<>();

            List<CustomerDetails> customerList = custmrDataFields.getCustomerDetailsList();

            for (CustomerDetails customer : customerList) {
                BigDecimal custId = customer.getCustDtlId();
                String customerType = getDefaultValueIfObjNull(customer.getCustomerType());
                String masterValue = getMasterValue("customerType", customerType, masterData);

                logger.debug("Customer ID: {}, Type: {}", custId, masterValue);

                applicantTypeMap.put(custId, masterValue);
            }
            List<WorkitemCreationRequestDocDtls> documentDetailsForNewgen = getDocumentDetailsForNewgen(extReq, prop, applicantTypeMap);


            Map<String, List<File>> groupedByExt = prepareFilesFromDocDetails(documentDetailsForNewgen, generatedFiles, tempFiles);
            for (Entry<String, List<File>> entry : groupedByExt.entrySet()) {
                String ext = entry.getKey();
                List<File> files = entry.getValue();
                logger.info("Extension: {}", ext);
                for (File file : files) {
                    logger.info("  File: {}", file.getAbsolutePath());
                }
            }
            List<Mono<Object>> uploadMonos = new ArrayList<>();

            for (Entry<String, List<File>> entry : groupedByExt.entrySet()) {
                String ext = entry.getKey();
                List<File> files = entry.getValue();

                for (File file : files) {
                    logger.debug("Uploading file: {} ({} bytes)", file.getName(), file.length());
                    documentBDO.setCabinetName(prop.getProperty(CobFlagsProperties.DMS_CABINET_NAME.getKey()));
                    documentBDO.setFolderIndex(folderIndex);
                    String uploadName = uploadNames.getOrDefault(file, file.getName());
                    documentBDO.setDocumentName(uploadName);
                    documentBDO.setUserDBId(userDBId);
                    documentBDO.setVolumeId(prop.getProperty(CobFlagsProperties.DMS_VOLUME_ID.getKey()));
                    documentBDO.setCreatedByAppName(ext);
                    documentBDO.setUserName(StringUtils.isBlank(userDBId) ? prop.getProperty(CobFlagsProperties.DMS_USERNAME.getKey()) : "");
                    documentBDO.setUserPassword(StringUtils.isBlank(userDBId) ? prop.getProperty(CobFlagsProperties.DMS_USERPASSWORD.getKey()).trim() : "");
                    documentBDO.setComment(prop.getProperty(CobFlagsProperties.DMS_COMMENT.getKey()));

                    Gson gsonObj = new Gson();
                    String jsonPart = gson.toJson(Collections.singletonMap(Constants.NGO_ADDDOCUMENT_BDO, documentBDO));
                    logger.debug("Add document Request :" + jsonPart);
                    logger.debug("Uploading file: {} as {}", file.getName(), uploadName);
                    FileSystemResource resource = new FileSystemResource(file);
                    MultipartBodyBuilder builder = new MultipartBodyBuilder();

                    builder.part(Constants.NGO_ADDDOCUMENT_BDO, jsonPart)
                            .header("Content-Type", Constants.JSON_CONTENT_TYPE);
                    builder.part("file", resource)
                            .header("Content-Type", Constants.OCTET_STREAM_CONTENT_TYPE)
                            .filename(uploadName);

                    Mono<Object> uploadMono = webClient.post()
                            .uri(path)  //"/OmniDocsRestWS/rest/services/addDocumentJSON"
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .body(BodyInserters.fromMultipartData(builder.build()))
                            .retrieve()
                            .bodyToMono(Object.class)
                            .doOnNext(response -> logger.debug("Upload response for '{}': {}", ext, response))
                            .doOnError(err -> logger.error("Error during DMS upload", err))
                            .onErrorResume(err -> {
                                String errorMessage = "Error uploading " + ext + ": " + err.getMessage();
                                return Mono.error(new RuntimeException(getFailureApiJson(errorMessage, "DMS Add Doc").toString(), err));
                            });
                    uploadMonos.add(uploadMono);
                }
            }
            return Flux.concat(uploadMonos)
                    .collectList()
                    .flatMap(results -> {
                        if (results.isEmpty()) {
                            return Mono.error(new RuntimeException(
                                    "No documents found to upload for applicationId=" + applicationId));
                        }
                        logger.debug("All uploads completed. Total: {}", results.size());
                        return Mono.just((Object) getSuccessJson("All Documents Uploaded"));
                    }).doFinally(sig -> tempFiles.forEach(f -> {
                        if (!f.delete()) {
                            logger.warn("Could not delete temp file {}", f.getAbsolutePath());
                        }
                    }));
        } catch (Exception e) {
            tempFiles.forEach(f -> {
                if (!f.delete()) {
                    logger.warn("Could not delete temp file {}", f.getAbsolutePath());
                }
            });
            logger.error("Error occurred during bulk file upload", e);
            return Mono.error(new RuntimeException(getFailureApiJson("Exception occurred during bulk file upload: ", "DMS Add Doc").toString()));
        }
    }

    public Map<String, List<File>> prepareFilesFromDocDetails(List<WorkitemCreationRequestDocDtls> docDetails,
                                                              List<File> generatedFiles,
                                                              List<File> tempFilesOut) throws IOException {
        Map<String, List<File>> groupedByExt = new HashMap<>();

        try {
            for (WorkitemCreationRequestDocDtls doc : docDetails) {
                String ext = doc.getDocExtn(); // "pdf", "jpg", etc.
                String name = doc.getDocName(); // "C01_Voter ID"
                byte[] fileContent = java.util.Base64.getDecoder().decode(doc.getDocContent());

                // Create temp file
                File file = File.createTempFile(name.replaceAll("\\s+", "_"), "." + ext);
                tempFilesOut.add(file);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(fileContent);
                }

                groupedByExt.computeIfAbsent(ext, k -> new ArrayList<>()).add(file);
            }
        }catch (IOException | IllegalArgumentException e) {
            tempFilesOut.forEach(File::delete);
            tempFilesOut.clear();
            throw e instanceof IOException ? (IOException) e
                    : new IOException("Failed preparing document files: " + e.getMessage(), e);
        }

        if (generatedFiles != null && !generatedFiles.isEmpty()) {
            groupedByExt.computeIfAbsent(Constants.DOCFORMATPDF, k -> new ArrayList<>())
                    .addAll(generatedFiles);
        }
        return groupedByExt;
    }

    private List<WorkitemCreationRequestDocDtls> getDocumentDetailsForNewgen(CreateModifyUserRequest extReq,
                                                                             Properties prop, Map<BigDecimal, String> applicantTypeMap) throws IOException {
        List<WorkitemCreationRequestDocDtls> docDtls = new ArrayList<>();
        try {
            List<String> applicantDoctypes = new ArrayList<>();
            List<String> coApplicantDoctypes = new ArrayList<>();
            JSONObject applicantDataset = new JSONObject();
            JSONObject coApplicantDataset = new JSONObject();
            List<ApplicationDocumentsPayload> appDataSet = new ArrayList<>();
            List<ApplicationDocumentsPayload> coAppDataSet = new ArrayList<>();
            String doctype = "";
            Gson gson = new Gson();
            logger.debug("applicantDoctypes 1 : ");
            for (ApplicationDocumentsWrapper applicationDocumentsWrapper : extReq.getRequestObj()
                    .getApplicationDocumentsWrapperList()) {
                logger.debug("applicantDoctypes 2 : ");
                for (ApplicationDocuments applicationDocuments : applicationDocumentsWrapper
                        .getApplicationDocumentsList()) {
                    logger.debug("applicantDoctypes 2 : " + applicationDocuments.toString());
                    logger.debug("applicantDoctypes 2 : " + applicationDocuments.getPayload());
                    logger.debug("applicantDoctypes 2 : " + applicationDocuments.getPayloadColumn());
                    ApplicationDocumentsPayload documentsPayload = gson
                            .fromJson(applicationDocuments.getPayloadColumn(), ApplicationDocumentsPayload.class);
                    doctype = documentsPayload.getDocumentName();
                    logger.debug("applicantDoctypes 3 : " + doctype);
                    if (applicantTypeMap.get(applicationDocuments.getCustDtlId()).equalsIgnoreCase("1")) {
                        appDataSet.add(documentsPayload);
                    } else {
                        coAppDataSet.add(documentsPayload);
                    }
                }
            }

            logger.debug("applicantDoctypes : " + applicantDoctypes);
            logger.debug("coApplicantDoctypes : " + coApplicantDoctypes);
            logger.debug("applicantDataset : " + applicantDataset);
            logger.debug("coApplicantDataset : " + coApplicantDataset);
            logger.debug("appDataSet : " + appDataSet);
            logger.debug("coAppDataSet : " + coAppDataSet);

            String filePath = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/"
                    + extReq.getRequestObj().getAppId() + Constants.LOANPATH + extReq.getRequestObj().getApplicationId() + "/";
            logger.debug("File path :: {}", filePath);

//			Map<String, List<ApplicationDocumentsPayload>> appDataSetMap = appDataSet.stream()
//					.collect(Collectors.groupingBy(ApplicationDocumentsPayload::getDocumentName));
//			Map<String, List<ApplicationDocumentsPayload>> coAppDataSetMap = coAppDataSet.stream()
//					.collect(Collectors.groupingBy(ApplicationDocumentsPayload::getDocumentName));
            Map<String, List<ApplicationDocumentsPayload>> appDataSetMap = appDataSet.stream()
                    .collect(Collectors.groupingBy(doc -> {
                        if (doc.getDocumentName() == null) {
                            logger.debug("Using documentType for App: {}", doc.getDocumentType());
                            return doc.getDocumentType();
                        }
                        return doc.getDocumentName();
                    }));

            Map<String, List<ApplicationDocumentsPayload>> coAppDataSetMap = coAppDataSet.stream()
                    .collect(Collectors.groupingBy(doc -> {
                        if (doc.getDocumentName() == null) {
                            logger.debug("Using documentType for Coapp: {}", doc.getDocumentType());
                            return doc.getDocumentType();
                        }
                        return doc.getDocumentName();
                    }));

            for (Entry<String, List<ApplicationDocumentsPayload>> e : appDataSetMap.entrySet()) {
                String key = e.getKey();
                List<ApplicationDocumentsPayload> payloads = e.getValue();
                WorkitemCreationRequestDocDtls docDtl = new WorkitemCreationRequestDocDtls();
                if (payloads.get(0).getDocumentName() == null) {
                    docDtl.setDocName("A01_" + payloads.get(0).getDocumentType());
                } else {
                    docDtl.setDocName("A01_" + payloads.get(0).getDocumentName());
                }
                docDtl.setDocExtn("pdf");
                docDtl.setDocSize("");
                String PDFString = CommonUtils.mergePDFFiles(docDtl.getDocName(), payloads, filePath);
                docDtl.setDocContent(PDFString);
                docDtls.add(docDtl);
            }
            for (Entry<String, List<ApplicationDocumentsPayload>> e : coAppDataSetMap.entrySet()) {
                String key = e.getKey();
                List<ApplicationDocumentsPayload> payloads = e.getValue();
                WorkitemCreationRequestDocDtls docDtl = new WorkitemCreationRequestDocDtls();
                if (payloads.get(0).getDocumentName() == null) {
                    docDtl.setDocName("C01_" + payloads.get(0).getDocumentType());
                } else {
                    docDtl.setDocName("C01_" + payloads.get(0).getDocumentName());
                }
                docDtl.setDocExtn("pdf");
                docDtl.setDocSize("");
                String PDFString = CommonUtils.mergePDFFiles(docDtl.getDocName(), payloads, filePath);
                docDtl.setDocContent(PDFString);
                docDtls.add(docDtl);
            }
        } catch (JSONException e) {
            logger.error("New case exception : " + e.getMessage());
        }
        return docDtls;
    }

    private String resolveValue(String input, Properties prop, String propKey) {
        return (input == null || input.trim().isEmpty()) ? prop.getProperty(propKey) : input;
    }

    private String getDefaultValueIfObjNull(Object obj) {
        String value = "";
        if (null != obj) {
            value = String.valueOf(obj).trim();
        }
        return value;
    }

    private String getMasterValue(String masterName, String key, JSONObject masterData) {
        logger.debug("getMasterValue :: {} {}", masterName, key);
        String value = "";
        JSONArray jsonArray = masterData.getJSONArray(masterName);
        logger.debug("jsonArray :: {}", jsonArray);
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            logger.debug("jsonObject :: {}", jsonObject);
            if (key.equalsIgnoreCase(jsonObject.getString("Description"))) {
                value = jsonObject.optString("Code");
                logger.debug("value :: {}", value);
                break;
            }
        }
        logger.debug("final value :: {}", value);
        return value;
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

    private Mono<Object> breCBCheckFallback(
            BRECBRequest breCBCheckRequest,
            Header header,
            Properties prop,
            Exception e
    ) {
        logger.error("breCBCheckFallback error", e);

        String currentStatus = breCBCheckRequest.getRequestObj()
                .getBreCBValuesRequestvalues1()
                .getBreCBInputRequestinput1()
                .getBreCBValuesRequestvalues2()
                .getBreCBInputRequestinput2()
                .getCurrentStage();

        if (currentStatus != null &&
                (currentStatus.toUpperCase().contains(AppStatus.CACOMPLETED.getValue().toUpperCase()) ||
                        currentStatus.toUpperCase().contains(AppStatus.RESANCTION.getValue().toUpperCase()) || currentStatus.toUpperCase().contains(AppStatus.RPCVERIFIED.getValue().toUpperCase())
                )
        ) {
            return Mono.error(e);
        }
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> bIPFallback(BIPMasterRequest apiRequest, Header header, Properties prop,
                                     Exception e) {
        logger.error("bIPFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private Mono<Object> loanCreationFallback(String applicationId, Header header, Properties prop,
                                              Exception e) {
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

    public Mono<Object> breCBReport(BRECBReportRequest brecbReportRequest, Header header, Properties prop,
                                    boolean isExist) {

        try {


            logger.debug("request from the breCBReport API: {} ", brecbReportRequest.toString());
            BRECBCheckRequestExt CBCheckRequestExt = new BRECBCheckRequestExt();
            CBCheckRequestExt.setAppId(brecbReportRequest.getAppId());
            CBCheckRequestExt.setInterfaceName(prop.getProperty(CobFlagsProperties.BRE_CB_REPORT_INTF.getKey()));
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
                    String base64String = java.util.Base64.getEncoder().encodeToString(fileContent);

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
                                            errorResp.toString(), ResponseCodes.FAILURE.getValue(), errorResp.toString(), "");
                                    return Mono.just(errorResp);
                                }
                            } else {
                                // If the response is not an InputStream, return an error response
                                JSONObject errorResp = new JSONObject();
                                errorResp.put("status", Constants.ERROR1);
                                errorResp.put(Constants.MESSAGE, "Invalid response format.");
                                saveLog(applicationNum, "breCBReport", CBCheckRequestExt.toString(),
                                        errorResp.toString(), ResponseCodes.FAILURE.getValue(), errorResp.toString(), "");
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

    /**
     * Writes InputStream data to a file.
     *
     * @param inputStream InputStream to write
     * @param fileName    File name to save
     * @return Path to the saved file
     */
    private static Path writeInputStreamToFile(InputStream inputStream, String fileName) {
        Path filePath = Paths.get(fileName);
        try (BufferedInputStream bis = new BufferedInputStream(inputStream);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // Read the input stream into a byte array
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            // Write the byte array to the file
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, baos.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            logger.debug("File written to: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error writing file: " + e.getMessage(), e);
        }
        return filePath;
    }

    // Method to write InputStream to a file
    private Path writePdfToFile(InputStream inputStream, String uploadLocation, String fileName) {
//      Path filePath = Paths.get("some/directory", fileName); // Define the folder where the PDF will be saved
        Path filePath = Paths.get(uploadLocation, fileName);
        try {
            // Ensure the directories exist
            Files.createDirectories(filePath.getParent());

            // Write the InputStream content to the file
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            // Close the InputStream
            inputStream.close();
        } catch (IOException e) {
            logger.error("Error writing file: " + e.getMessage(), e);
            throw new RuntimeException("Error writing file: " + e.getMessage(), e);
        }

        return filePath;
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
            return java.util.Base64.getEncoder().encodeToString(fileContent);
//            return new sun.misc.BASE64Encoder().encode(fileContent);

        } catch (IOException e) {
            logger.error("Error converting file to Base64: " + e.getMessage(), e);
            throw new RuntimeException("Error converting file to Base64: " + e.getMessage(), e);
        }
    }

    private String extractValue(String input) {
        // Normalize delimiters: Remove unnecessary spaces around ":" and ","
        String normalizedInput = input.replaceAll("\\s*:\\s*", ":").replaceAll("\\s*,\\s*", ",").trim();

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

    public void updateLoanDtls(UploadLoanRequestFields requestObj) {
        logger.debug("Onentry :: updateOccupationdtls");
        Gson gson = new Gson();
        String payload;
        try {
            LoanDetails loanDtlObjReq = requestObj.getLoanDetails();
            logger.debug("loanDtlObjReq: {}", loanDtlObjReq);
            if (loanDtlObjReq != null) {
                Optional<LoanDetails> loanDetailsDb = loanDtlsRepo
                        .findTopByApplicationIdAndAppId(requestObj.getApplicationId(), Constants.APPID);

                if (loanDetailsDb.isPresent()) {
                    logger.debug("data found: ");
                    LoanDetails existingLoanDtl = loanDetailsDb.get(); // Fetch existing record
                    logger.debug("existingLoanDtl : {}", existingLoanDtl);
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
}
