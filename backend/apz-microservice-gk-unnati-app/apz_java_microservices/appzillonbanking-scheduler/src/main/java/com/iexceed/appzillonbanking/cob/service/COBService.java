package com.iexceed.appzillonbanking.cob.service;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import com.google.gson.*;
import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
import com.iexceed.appzillonbanking.cob.core.repository.ab.*;
import com.iexceed.appzillonbanking.cob.domain.ab.*;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedCDHLead;
import com.iexceed.appzillonbanking.cob.loans.domain.user.KendraDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.LoanRequestExt;
import com.iexceed.appzillonbanking.cob.loans.repository.user.TbUserRepository;
import com.iexceed.appzillonbanking.cob.repository.ab.*;
import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiIexceedCDHLeadRepo;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.ApplicationDocumentsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.ApplicationDocumentsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CheckApplicationRes;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CommonParamResponse;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerIdentificationCasa;
import com.iexceed.appzillonbanking.cob.core.payload.ExistingLoanDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.FundAccountRequestFields;
import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.payload.InsuranceDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.LoanDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.NomineeDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.OccupationDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.PopulateapplnWFRequest;
import com.iexceed.appzillonbanking.cob.core.payload.PopulateapplnWFRequestFields;
import com.iexceed.appzillonbanking.cob.core.payload.RepaymentSchedule;
import com.iexceed.appzillonbanking.cob.core.payload.RepaymentScheduleDisbursed;
import com.iexceed.appzillonbanking.cob.core.payload.Request;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.TATReportPayload;
import com.iexceed.appzillonbanking.cob.core.payload.WorkFlowDetails;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.core.services.InterfaceAdapter;
import com.iexceed.appzillonbanking.cob.core.services.ResponseParser;
import com.iexceed.appzillonbanking.cob.core.utils.AdapterUtil;
import com.iexceed.appzillonbanking.cob.core.utils.AppStatus;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.CodeTypes;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.FallbackUtils;
import com.iexceed.appzillonbanking.cob.core.utils.JsonKeyFolders;
import com.iexceed.appzillonbanking.cob.core.utils.Products;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.core.utils.SpringCloudProperties;
import com.iexceed.appzillonbanking.cob.core.utils.WidgetQueueStatus;
import com.iexceed.appzillonbanking.cob.core.utils.WorkflowStatus;
import com.iexceed.appzillonbanking.cob.domain.apz.User;
import com.iexceed.appzillonbanking.cob.domain.apz.UserId;
import com.iexceed.appzillonbanking.cob.loans.domain.user.BranchAreaMappingDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.BCMPIIncomeDetailsWrapper;
import com.iexceed.appzillonbanking.cob.loans.payload.BCMPIOtherDetailsWrapper;
import com.iexceed.appzillonbanking.cob.loans.payload.LoanObligationsWrapper;
import com.iexceed.appzillonbanking.cob.loans.payload.LucPayloadPurposesRequest;
import com.iexceed.appzillonbanking.cob.loans.payload.LucPayloadRequest;
import com.iexceed.appzillonbanking.cob.loans.payload.UploadLoanRequestFields.DBKITResponse;
import com.iexceed.appzillonbanking.cob.loans.repository.user.KendraDetailsRepository;
import com.iexceed.appzillonbanking.cob.loans.repository.user.StateDetailsRepository;
import com.iexceed.appzillonbanking.cob.loans.repository.user.TATBranchDetailsRepository;
import com.iexceed.appzillonbanking.cob.nesl.domain.ab.Enach;
import com.iexceed.appzillonbanking.cob.nesl.repository.ab.EnachRepository;
import com.iexceed.appzillonbanking.cob.payload.AdvanceSearchAppRequest;
import com.iexceed.appzillonbanking.cob.payload.AdvanceSearchAppRequestFields;
import com.iexceed.appzillonbanking.cob.payload.AssignApplicationRequest;
import com.iexceed.appzillonbanking.cob.payload.AssignApplicationRequestFields;
import com.iexceed.appzillonbanking.cob.payload.BankingFacilitiesPayload;
import com.iexceed.appzillonbanking.cob.payload.CRSDetailsPayload;
import com.iexceed.appzillonbanking.cob.payload.CheckAppCreateAppElements;
import com.iexceed.appzillonbanking.cob.payload.CheckApplicationRequest;
import com.iexceed.appzillonbanking.cob.payload.CheckApplicationRequestFields;
import com.iexceed.appzillonbanking.cob.payload.CreateModifyUserRequest;
import com.iexceed.appzillonbanking.cob.payload.CreateRoleRequest;
import com.iexceed.appzillonbanking.cob.payload.CreateRoleRequestFields;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;
import com.iexceed.appzillonbanking.cob.payload.DeleteDocumentRequest;
import com.iexceed.appzillonbanking.cob.payload.DeleteDocumentRequestFields;
import com.iexceed.appzillonbanking.cob.payload.DeleteNomineeRequest;
import com.iexceed.appzillonbanking.cob.payload.DeleteNomineeRequestFields;
import com.iexceed.appzillonbanking.cob.payload.DownloadReportRequest;
import com.iexceed.appzillonbanking.cob.payload.DownloadReportRequestFields;
import com.iexceed.appzillonbanking.cob.payload.ExtractOcrDataRequest;
import com.iexceed.appzillonbanking.cob.payload.ExtractOcrDataRequesttFields;
import com.iexceed.appzillonbanking.cob.payload.FatcaDetailsPayload;
import com.iexceed.appzillonbanking.cob.payload.FetchBanksRequest;
import com.iexceed.appzillonbanking.cob.payload.FetchBranchesRequest;
import com.iexceed.appzillonbanking.cob.payload.FetchBranchesRequestFields;
import com.iexceed.appzillonbanking.cob.payload.FetchCitiesRequest;
import com.iexceed.appzillonbanking.cob.payload.FetchCitiesRequestFields;
import com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest;
import com.iexceed.appzillonbanking.cob.payload.FetchLitByLanguageRequest;
import com.iexceed.appzillonbanking.cob.payload.FetchNomineeRequest;
import com.iexceed.appzillonbanking.cob.payload.FetchNomineeRequestFields;
import com.iexceed.appzillonbanking.cob.payload.FetchRoleRequest;
import com.iexceed.appzillonbanking.cob.payload.FetchRoleRequestFields;
import com.iexceed.appzillonbanking.cob.payload.FetchTATReportRequest;
import com.iexceed.appzillonbanking.cob.payload.FetchTATReportRequestFields;
import com.iexceed.appzillonbanking.cob.payload.FundAccountRequest;
import com.iexceed.appzillonbanking.cob.payload.LITDomain;
import com.iexceed.appzillonbanking.cob.payload.PopulateRejectedDataRequest;
import com.iexceed.appzillonbanking.cob.payload.PopulateRejectedDataRequestFields;
import com.iexceed.appzillonbanking.cob.payload.SearchAppRequest;
import com.iexceed.appzillonbanking.cob.payload.SearchAppRequestFields;
import com.iexceed.appzillonbanking.cob.payload.StatusReportRequest;
import com.iexceed.appzillonbanking.cob.payload.StatusReportRequestFields;
import com.iexceed.appzillonbanking.cob.payload.TaxDetails;
import com.iexceed.appzillonbanking.cob.payload.UpdateApplicantsCountRequest;
import com.iexceed.appzillonbanking.cob.payload.UpdateApplicantsCountRequestFields;
import com.iexceed.appzillonbanking.cob.payload.UpdateLitFileRequest;
import com.iexceed.appzillonbanking.cob.payload.UpdateLitFileRequestFields;
import com.iexceed.appzillonbanking.cob.payload.UpdateLovRequest;
import com.iexceed.appzillonbanking.cob.payload.UpdateLovRequestFields;
import com.iexceed.appzillonbanking.cob.payload.UploadDocumentRequest;
import com.iexceed.appzillonbanking.cob.payload.UploadDocumentRequestFields;
import com.iexceed.appzillonbanking.cob.payload.ViewAllRecordsRequest;
import com.iexceed.appzillonbanking.cob.payload.ViewAllRecordsRequestFields;
import com.iexceed.appzillonbanking.cob.report.CamReport;
import com.iexceed.appzillonbanking.cob.report.ConsentLetter;
import com.iexceed.appzillonbanking.cob.report.CreditAssessment;
import com.iexceed.appzillonbanking.cob.report.DemandPromisoryNote;
import com.iexceed.appzillonbanking.cob.report.InsuranceConsent;
import com.iexceed.appzillonbanking.cob.report.KfsReport;
import com.iexceed.appzillonbanking.cob.report.LoanAgreement;
import com.iexceed.appzillonbanking.cob.report.LoanApplication;
import com.iexceed.appzillonbanking.cob.report.MSMEReport;
import com.iexceed.appzillonbanking.cob.report.RepaymentScheduleTemplate;
import com.iexceed.appzillonbanking.cob.report.Report;
import com.iexceed.appzillonbanking.cob.report.SanctionLetter;
import com.iexceed.appzillonbanking.cob.report.ScheduleATemplate;
import com.iexceed.appzillonbanking.cob.report.TATReport;
import com.iexceed.appzillonbanking.cob.report.WelcomeLetter;
import com.iexceed.appzillonbanking.cob.repository.apz.UserRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

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
    private LeadDetailsRepository leadDetailsRepository;

    @Autowired
    private RenewalLeadDetailsRepository renewalLeadDetailsRepository;

    @Autowired
    private Report report;

    @Autowired
    private LoanDtlsRepo loanDtlsRepo;

    @Autowired
    private DepositDtlsRepo depositDtlsRepo;

    @Autowired
    private KendraDetailsRepository kendraDetailsRepository;

    @Autowired
    private LeadDashboardRepository leadDashboardRepository;

    @Autowired
    private StateDetailsRepository stateDetailsRepository;

    @Autowired
    private TATBranchDetailsRepository tATBranchDetailsRepository;

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
                      TbUserRepository tbUserRepository,LucRepository lucRepository,
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
        this.lucRepository=lucRepository;
        this.whitelistedBranchesRepository = whitelistedBranchesRepository;
        this.existingGLLoanDetailsRepository = existingGLLoanDetailsRepository;
    }

    private String nomineeDelMsg = "Nominee deleted.";

    @Autowired
    private EnachRepository enachRepo;

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
        Optional<List<InsuranceDetails>> insuranceDetails = insuranceRepository
                .findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        if (insuranceDetails.isPresent() && !insuranceDetails.get().isEmpty()) {
            insuranceDetails.get().forEach(insurance -> {
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

        LoanDetails loanDetail = loanDtlsRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId,
                versionNum);
        customerDataFields.setLoanDetails(loanDetail);

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

        RenewalLeadDetails renewalLeadDetails = null;
        LeadDetails leadDetails = null;
        UnnatiIexceedCDHLead cdhLeadDetails = null;
        if (applicationMasterData.getProductCode().equalsIgnoreCase(Constants.RENEWAL_LOAN_PRODUCT_CODE)) {
            Optional<RenewalLeadDetails> renewalLeadDetailsOpt = renewalLeadDetailsRepository.findByCustomerId(applicationMasterData.getSearchCode2());
            if (renewalLeadDetailsOpt.isPresent()) {
                renewalLeadDetails = renewalLeadDetailsOpt.get();
            }
        } else if(applicationMasterData.getProductCode().equalsIgnoreCase(Constants.UNNATI_PRODUCT_CODE)){
            Optional<LeadDetails> leadDetailsOpt = leadDetailsRepository.findByCustomerId(applicationMasterData.getSearchCode2());
            if (leadDetailsOpt.isPresent()) {
                leadDetails = leadDetailsOpt.get();
            }
        }else if(appMasterData.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)) {
            Optional<UnnatiIexceedCDHLead> openMarketLeadOpt = unnatiIexceedCDHLeadRepo.findByCustomerId(applicationMasterData.getSearchCode2());
            if (openMarketLeadOpt.isPresent()) {
                cdhLeadDetails = openMarketLeadOpt.get();
        }
        }


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
        customerDataFields.setRenewalLeadDetails(renewalLeadDetails);
        customerDataFields.setLeadDetails(leadDetails);
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
        } else if (
                AppStatus.CACOMPLETED.getValue().equalsIgnoreCase(appStatus) ||
                        AppStatus.RESANCTION.getValue().equalsIgnoreCase(appStatus)
        ) {
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

    private String fetchBranchCode(String appId, String userId) {
        Optional<User> userDb = userRepository.findById(new UserId(appId, userId));
        if (userDb.isPresent()) {
            User user = userDb.get();
            return user.getAddInfo2();
        }
        return null;
    }

    private List<String> fetchKendrasByUserIsAndRoleId(String userId, String roleAccess, String branchCode,
                                                       String rolesStr, String fetchType) {
        List<String> kendraIds = null;
        String roleId = getUserRole(roleAccess, rolesStr);
        if (null != roleId) {
            if (roleId.equals(CobFlagsProperties.KM.getKey())) {
                if(Constants.DASHBOARD_STATS_RENEWAL.equalsIgnoreCase(fetchType)){
                    List<KendraDetails> kendraDetailsList = kendraDetailsRepository.findKendraDetailsByHandledBy(userId);
                    List<String> renewalBranches = whitelistedBranchesRepository.findAllRenewalEnabledBranches();
                    kendraIds = kendraDetailsList.stream()
                            .filter(kendra -> renewalBranches.contains(kendra.getBranchId()))
                            .map(KendraDetails::getT24Id)
                            .collect(Collectors.toList());
                } else {
                kendraIds = kendraDetailsRepository.findKendraIdByHandledBy(userId);
                }
            } else if (Constants.APPROVER.equalsIgnoreCase(roleId) || CobFlagsProperties.BM.getKey().equalsIgnoreCase(roleId) || Constants.BCM.equalsIgnoreCase(roleId)) {
                kendraIds = kendraDetailsRepository.findKendraIdByBranchId(branchCode);
            } else if (Constants.AM.equalsIgnoreCase(roleId) || Constants.ACM.equalsIgnoreCase(roleId)) {
                List<String> branches = fetchBranchesByUserIdAndRoleId(userId, roleAccess, branchCode, rolesStr);
                kendraIds = kendraDetailsRepository.findKendraIdByBranchIdIn(branches);
            }
        }
        return kendraIds;
    }

    private List<String> fetchBranchesByUserIdAndRoleId(String userId, String roleAccess, String branchCode,
                                                        String rolesStr) {
        List<String> branches = new ArrayList<>();
        String roleId = getUserRole(roleAccess, rolesStr);
        if (null != roleId) {
            if (roleId.equals(CobFlagsProperties.RPC.getKey()) || roleId.equalsIgnoreCase(Constants.RM) || roleId.equalsIgnoreCase(Constants.DM)) {
                logger.debug("branchCode id's : {}", branchCode);
                String[] regionIdList = branchCode.split(",");
                List<Integer> regionId = new ArrayList<>();
                for (String region : regionIdList) {
                    regionId.add(Integer.parseInt(region.trim()));
                }
                logger.debug("region id's : {}", regionId);
                List<BranchAreaMappingDetails> branchCodes = tATBranchDetailsRepository
                        .findBranchIdDetailsByRPCId(regionId);
                logger.debug("branchCodes id's : {}", branchCodes.toString());
                for (BranchAreaMappingDetails branchCodelist : branchCodes) {
                    branches.add(branchCodelist.getBranchId());
                }
                logger.debug("branchCodes : {}", branches);
                // kendraIds = kendraDetailsRepository.findKendraIdByBranchIdIn(branches);
                // logger.debug("kendra id's : {}", kendraIds);
            } else if (roleId.equalsIgnoreCase(Constants.AM) || roleId.equalsIgnoreCase(Constants.ACM)) {
                logger.debug("branchCode id's : {}", branchCode);
                String[] areaIdList = branchCode.split(",");
                List<Integer> areaIds = new ArrayList<>();
                for (String areaId : areaIdList) {
                    areaIds.add(Integer.parseInt(areaId.trim()));
                }
                logger.debug("area ids: {}", areaIds);
                List<BranchAreaMappingDetails> branchCodes = tATBranchDetailsRepository.findBranchIdByAreaId(areaIds);
                for (BranchAreaMappingDetails branchCodelist : branchCodes) {
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
        if (Products.CASA.getKey().equalsIgnoreCase(appMasterObj.getProductGroupCode())) {
            stpFlag = prop.getProperty(CobFlagsProperties.ACCOUNT_STP.getKey());
        } else if (Products.DEPOSIT.getKey().equalsIgnoreCase(appMasterObj.getProductGroupCode())) {
            stpFlag = prop.getProperty(CobFlagsProperties.DEPOSIT_STP.getKey());
        } else if (Products.CARDS.getKey().equalsIgnoreCase(appMasterObj.getProductGroupCode())) {
            stpFlag = prop.getProperty(CobFlagsProperties.CARD_STP.getKey());
        } else if (Products.LOAN.getKey().equalsIgnoreCase(appMasterObj.getProductGroupCode())) {
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
        if (Products.CASA.getKey().equalsIgnoreCase(applicationMaster.getMainProductGroupCode())) { // creating CASA
            // account as part
            // of CASA account
            // when customer is
            // new
            array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.CASA_BO.getKey(),
                    Constants.FUNCTIONSEQUENCE);
        } else if (Products.DEPOSIT.getKey().equalsIgnoreCase(applicationMaster.getMainProductGroupCode())) { // creating
            // CASA
            // account
            // as
            // part
            // of
            // deposit
            // account
            // when
            // customer
            // is
            // new
            array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE,
                    CodeTypes.DEPOSIT_BO_NTB.getKey(), Constants.FUNCTIONSEQUENCE);
        } else if (Products.LOAN.getKey().equalsIgnoreCase(applicationMaster.getMainProductGroupCode())) { // creating
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
        if (Products.CASA.getKey().equalsIgnoreCase(applicationMaster.getMainProductGroupCode())) { // creating CASA
            // account as part
            // of CASA account
            // when customer is
            // new
            array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.CASA.getKey(),
                    Constants.FUNCTIONSEQUENCE);
        } else if (Products.DEPOSIT.getKey().equalsIgnoreCase(applicationMaster.getMainProductGroupCode())) { // creating
            // CASA
            // account
            // as
            // part
            // of
            // deposit
            // account
            // when
            // customer
            // is
            // new
            array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE,
                    CodeTypes.DEPOSIT_NTB.getKey(), Constants.FUNCTIONSEQUENCE);
        } else if (Products.LOAN.getKey().equalsIgnoreCase(applicationMaster.getMainProductGroupCode())) { // creating
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

    public Response handleDeleteAllDocuments(Properties prop, String appId, String applicationId){
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        String filePath = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId + "/" + Constants.LOAN + "/"
                + applicationId + "/";
        Optional<List<Documents>> documentsOpt = documentsRepository.findByApplicationId(applicationId);
        if(documentsOpt.isPresent()){
            List<Documents> documents = documentsOpt.get();
            if(!documents.isEmpty()){
                for(Documents document : documents){
                    String fileName = document.getDocName();
                    String uploadType = document.getUploadType();
                    String fileDest = filePath + uploadType + "/" + fileName;
                    try{
                        Path fileToDelete = Paths.get(fileDest);
                        boolean deleted = Files.deleteIfExists(fileToDelete);
                        if (!deleted) {
                            logger.warn("File not found to delete: {}", fileDest);
                        } else {
                            logger.info("File deleted successfully: {}", fileDest);
                        }

                        documentsRepository.delete(document);
                        Optional<DBKITStageVerification> dbkitStageVerificationOpt = dbkitStageVerificationRepository.findById(applicationId);
                        if(dbkitStageVerificationOpt.isPresent()){
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
        }else {
            logger.debug("document not found for applicationId: {}", applicationId);
            return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
        }


        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;

    }

    private void addGeneratedDocument(List<JsonObject> fileList, JsonObject documentJson) {
        if (documentJson.size() != 0) {
            fileList.add(documentJson);
        }
    }

    // -- ALL FALLBACK METHODS
    public Response dbKitDocGenerationAndDownloadFallback(UploadDocumentRequestFields requestObj, Exception e) throws IOException {
        logger.error("dbKitDocGenerationAndDownload error : request-{}, error -{}", requestObj,e);
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

    private Response downloadReportFallback(DownloadReportRequest downloadDocumentRequest, Exception e) {
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
}
