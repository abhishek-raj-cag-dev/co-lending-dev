package com.iexceed.appzillonbanking.cob.deposit.service;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetailsHistory;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMasterHistory;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cob.core.domain.ab.DepositDtls;
import com.iexceed.appzillonbanking.cob.core.domain.ab.DepositDtlsHis;
import com.iexceed.appzillonbanking.cob.core.domain.ab.NomineeDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.NomineeDetailsHistory;
import com.iexceed.appzillonbanking.cob.core.domain.ab.WorkflowDefinition;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CheckApplicationRes;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerIdentificationCasa;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerIdentificationDep;
import com.iexceed.appzillonbanking.cob.core.payload.FundAccountRequestFields;
import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.payload.NomineeDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.PopulateapplnWFRequest;
import com.iexceed.appzillonbanking.cob.core.payload.PopulateapplnWFRequestFields;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.WorkFlowDetails;
import com.iexceed.appzillonbanking.cob.core.repository.ab.AddressDetailsHisRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.AddressDetailsRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationMasterHisRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationWorkflowRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.DepositDtlsHisRepo;
import com.iexceed.appzillonbanking.cob.core.repository.ab.DepositDtlsRepo;
import com.iexceed.appzillonbanking.cob.core.repository.ab.NomineeDetailsHisRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.NomineeDetailsRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.WorkflowDefinitionRepository;
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
import com.iexceed.appzillonbanking.cob.core.utils.Products;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.deposit.payload.CreateDepositRequest;
import com.iexceed.appzillonbanking.cob.deposit.payload.CreateDepositRequestFields;
import com.iexceed.appzillonbanking.cob.deposit.payload.DeleteNomineeRequest;
import com.iexceed.appzillonbanking.cob.deposit.payload.DeleteNomineeRequestFields;
import com.iexceed.appzillonbanking.cob.deposit.payload.FetchCustDtlRequest;
import com.iexceed.appzillonbanking.cob.deposit.payload.FetchCustDtlRequestFields;
import com.iexceed.appzillonbanking.cob.deposit.payload.FetchDeleteUserRequest;
import com.iexceed.appzillonbanking.cob.deposit.payload.FetchRoiRequest;
import com.iexceed.appzillonbanking.cob.deposit.payload.FundAccountRequest;
import com.iexceed.appzillonbanking.cob.deposit.report.DepositReport;
import com.iexceed.appzillonbanking.cob.payload.CreateModifyUserRequest;
import com.iexceed.appzillonbanking.cob.payload.CreateModifyUserRequestWrapper;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;
import com.iexceed.appzillonbanking.cob.rest.CustomerOnBoardingAPI;
import com.iexceed.appzillonbanking.cob.service.COBService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import reactor.core.publisher.Mono;

@Service
public class DepositService {

    private static final Logger logger = LogManager.getLogger(DepositService.class);

    @Autowired
    private AdapterUtil adapterUtil;

    @Autowired
    private ApplicationMasterRepository applicationMasterRepo;

    @Autowired
    private ApplicationMasterHisRepository applicationMasterHisRepo;

    @Autowired
    private NomineeDetailsRepository nomineeDetailsRepo;

    @Autowired
    private NomineeDetailsHisRepository nomineeDetailsHisRepo;

    @Autowired
    private AddressDetailsRepository addressDetailsRepo;

    @Autowired
    private AddressDetailsHisRepository addressDetailsHisRepo;

    @Autowired
    private DepositDtlsRepo depositDtlsRepo;

    @Autowired
    private DepositDtlsHisRepo depositDtlsHisRepo;

    @Autowired
    private InterfaceAdapter interfaceAdapter;

    @Autowired
    private ApplicationWorkflowRepository applnWfRepository;

    @Autowired
    private WorkflowDefinitionRepository wfDefnrepo;

    @Autowired
    private CustomerOnBoardingAPI casaApi;

    @Autowired
    private CommonParamService commonService;

    @Autowired
    private DepositReport report;

    @Autowired
    private WorkflowDefinitionRepository wfDefnRepositoryDp;
    
    @Autowired
    private COBService cobService;

    private String versionHm="versionHm";
    private String headerHm="headerHm";
    private String applicationIDHm="applicationIDHm";
    private String propHm="propHm";
    
    @CircuitBreaker(name = "fallback", fallbackMethod = "createDepositFallback")
    public Mono<Response> createDeposit(HashMap<String, String> hm, CreateDepositRequest request, boolean isSelfOnBoardingAppId, 
    		boolean isSelfOnBoardingHeaderAppId, Properties prop, JSONArray array) {
    	Header header = CommonUtils.obtainHeader(hm.get("reqAppId"), hm.get("interfaceId"), hm.get("userId"), hm.get("masterTxnRefNo"), hm.get("deviceId"));
        ResponseBody responseBody = new ResponseBody();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        CustomerIdentificationDep customerIdentification = new CustomerIdentificationDep();
        String applicationID;
        int version = 0;
        CreateDepositRequestFields requestObj = request.getRequestObj();
        ApplicationMaster applicationMaster = requestObj.getApplicationMaster();
        boolean isThisLastStage=commonService.isThisLastStage(applicationMaster.getCurrentScreenId().split("~")[0], array);
        boolean isAccountCreationisNextStage=commonService.isAccountCreationisNextStage(applicationMaster.getCurrentScreenId().split("~")[0], array);
        if (CommonUtils.isNullOrEmpty(requestObj.getApplicationId())) {
            applicationID = CommonUtils.generateRandomNumStr();
            version = Constants.INITIAL_VERSION_NO; //initial creation of deposit application version number should be 1.
            populateAppMasterAndApplnwf(requestObj, applicationID, version, customerIdentification, isSelfOnBoardingHeaderAppId, prop);
        } else {  //this ID should be created once only.
            applicationID = requestObj.getApplicationId();
            Optional<ApplicationMaster> appMasterForVersionCheck = applicationMasterRepo.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(requestObj.getAppId(), applicationID);
            if (appMasterForVersionCheck.isPresent()) {
                ApplicationMaster appMaster = appMasterForVersionCheck.get();
                if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMaster.getApplicationStatus()) || 
                    	AppStatus.APPROVED.getValue().equalsIgnoreCase(appMaster.getApplicationStatus())) {
                    //Taking version number always from db as part of VAPT too.
                    //If application is in INPROGRESS status,subsequent tables should have same version number.
                    version = appMaster.getVersionNum();
                }
            }
        }
        final int versionFinal=version;
        String[] currentScreenIdArray = applicationMaster.getCurrentScreenId().split("~");
        commonService.updateCurrentStageInMaster(applicationMaster, currentScreenIdArray, version, requestObj.getAppId(), requestObj.getApplicationId());
        switch (currentScreenIdArray[0]) {
            case Constants.CUST_VERIFICATION:
                if (applicationMaster.getCustomerId() == null && "N".equalsIgnoreCase(requestObj.getIsExistingCustomer())) {
                    CreateModifyUserRequestWrapper createUserRequestWrapper = new CreateModifyUserRequestWrapper();
                    CreateModifyUserRequest createUserRequest = new CreateModifyUserRequest();
                    CustomerDataFields createUserRequestFields = new CustomerDataFields();
                    ApplicationMaster applicationMaster1 = request.getRequestObj().getApplicationMaster();
                    ApplicationMaster appMasterCasa = new ApplicationMaster();
                    BeanUtils.copyProperties(applicationMaster1, appMasterCasa);
                    appMasterCasa.setCustDtlSlNum(1);   // By default creating single holder casa account for deposit.
                    appMasterCasa.setApplicantsCount(1);   // By default creating single holder casa account for deposit.
                    appMasterCasa.setProductCode(prop.getProperty(CobFlagsProperties.DEFAULT_CASA_PRODUCT.getKey()));
                    appMasterCasa.setProductGroupCode(prop.getProperty(CobFlagsProperties.DEFAULT_CASA_GRP.getKey()));
                    appMasterCasa.setRelatedApplicationId(applicationID);
                    appMasterCasa.setApplicationId(null); //set this to null so that new application Id will be created in createApplication method.
                    appMasterCasa.setCustDtlId(null); //set this to null so that new custDtlId will be created in createApplication method.
                    createUserRequestFields.setIsExistingCustomer(requestObj.getIsExistingCustomer());
                    createUserRequestFields.setApplicationMaster(appMasterCasa);
                    createUserRequestFields.setWorkflow(null); // Workflow is not required for this casa sub application.
                    createUserRequestFields.setAppId(requestObj.getAppId());
                    if(null!=requestObj.getBankingFacilityList()) {
                    	createUserRequestFields.setBankingFacilityList(requestObj.getBankingFacilityList());
                    }
                    createUserRequest.setAppId(request.getAppId());
                    createUserRequest.setInterfaceName(request.getInterfaceName());
                    createUserRequest.setUserId(request.getUserId());
                    createUserRequest.setRequestObj(createUserRequestFields);
                    createUserRequestWrapper.setCreateModifyUserRequest(createUserRequest);
                    Mono<ResponseEntity<ResponseWrapper>> responseMono = casaApi.createApplication(createUserRequestWrapper, hm.get("reqAppId"), hm.get("interfaceId"), hm.get("userId"), hm.get("masterTxnRefNo"), hm.get("deviceId"));
                    return responseMono.flatMap(res->{
                    	 logger.debug("Response from casa to deposit is " + res.toString());
                    	 if (ResponseCodes.SUCCESS.getKey().equalsIgnoreCase(res.getBody().getApiResponse().getResponseHeader().getResponseCode())) {
                             String casaResStr = res.getBody().getApiResponse().getResponseBody().getResponseObj();
                             JSONObject casaResjson = new JSONObject(casaResStr);
                             CustomerIdentificationCasa casaCustIdentification = new CustomerIdentificationCasa();
                             casaCustIdentification.setApplicationId(casaResjson.getString("applicationId"));
                             casaCustIdentification.setCustDtlId(casaResjson.getString("custDtlId"));
                             casaCustIdentification.setVersionNum(casaResjson.getInt("versionNum"));
                             JSONArray bankFacilityArr = casaResjson.getJSONArray("bankFacilityList");
                             List<String> bankFacilityList=new ArrayList<>();
                             for(Object obj:bankFacilityArr) {
                             	bankFacilityList.add((String) obj);               	
                             }
                             casaCustIdentification.setBankFacilityList(bankFacilityList);
                             customerIdentification.setCasaCustomerIdentification(casaCustIdentification);
                         } else {
                             responseHeader.setResponseCode(ResponseCodes.CASA_CREATION_FAIL.getKey());
                         } 
                    	 updateCustomerDtlInMaster(requestObj, versionFinal, applicationID, customerIdentification);
                         updateCustIdAndBranchInMaster(requestObj, versionFinal);
                         populateOrUpdateDepositDtls(requestObj, versionFinal, applicationID, customerIdentification, Constants.DEPOSIT_DETAILS); // populateOrUpdateDepositDtls method call is required here. Because it is possible to have first screen as deposit details and second screen as customer verification.
                         Gson gson = new Gson();
                         String responseStr = gson.toJson(customerIdentification);
                         responseBody.setResponseObj(responseStr);
                         response.setResponseBody(responseBody);
                         response.setResponseHeader(responseHeader);
                         return Mono.just(response);
                    });
                }
                break;
            case Constants.DEPOSIT_DETAILS:
                populateOrUpdateDepositDtls(requestObj, version, applicationID, customerIdentification, Constants.DEPOSIT_DETAILS); // This is required if first screen is customer verification and second screen is deposit details
                break;
            case Constants.NOMINEE_DETAILS:
                populateNomineeDtls(requestObj, version, applicationID, customerIdentification);
                populateAddressDtls(requestObj, version, applicationID, customerIdentification);
                break;
            case Constants.AUTO_PAY_MI:
                populateOrUpdateDepositDtls(requestObj, version, applicationID, customerIdentification, Constants.AUTO_PAY_MI);
                break;
            case Constants.INITIAL_FUND:
                populateOrUpdateDepositDtls(requestObj, version, applicationID, customerIdentification, Constants.INITIAL_FUND);
                break;
            case Constants.TERMS_AND_CONDITIONS:
                updateDeclarationFlagInMaster(requestObj, version, applicationID, customerIdentification);
                break;
            case Constants.CONFIRMATION:
            	// No action to do specifically for CONFIRMATION. Appropriate actions are taken based on return value of isAccountCreationisNextStage() and isThisLastStage().
            	customerIdentification.setApplicationId(applicationID);
                customerIdentification.setVersionNum(version);
            	break;
            case Constants.FUND_ACCOUNT:
            	FundAccountRequest faReq=formFundAccRequest(requestObj);
            	Mono<Object> monoResponse = interfaceAdapter.callExternalService(header, faReq, request.getInterfaceName());
            	final int versionFinal1=version;
            	return monoResponse.flatMap(val -> {
            		ResponseWrapper res = adapterUtil.getResponseMapper(val, request.getInterfaceName(), header);
                     if (ResponseParser.isExtCallSuccess(res.getApiResponse(), "fundDepositAccount")) {
                    	 String fundAccRefNum=ResponseParser.getFundAccRefNum(res.getApiResponse());
                 		 customerIdentification.setFundAccRefNum(fundAccRefNum);
                 		 customerIdentification.setApplicationId(applicationID);
                         customerIdentification.setVersionNum(versionFinal1);
                     }
                     Gson gson = new Gson();
                     String responseStr = gson.toJson(customerIdentification);
                     responseBody.setResponseObj(responseStr);
                     response.setResponseBody(responseBody);
                     response.setResponseHeader(responseHeader);
                     if(isThisLastStage) {
                     	updateConfirmFlagInMaster(request, versionFinal1, applicationID, customerIdentification, prop, isSelfOnBoardingAppId, isSelfOnBoardingHeaderAppId);
                     }
                     if(isAccountCreationisNextStage) {
                    	HashMap<String, Object> hm1=new HashMap<>(); // HM is used to keep number of arguments less than 8 as per sonarqube
                     	hm1.put(versionHm, versionFinal1);
                     	hm1.put(propHm, prop);
                     	hm1.put(headerHm, header);
                     	hm1.put(applicationIDHm, applicationID);
                     	return accountCreationStageOperations(hm1, applicationMaster, requestObj, isSelfOnBoardingAppId, request, customerIdentification, 
                     			isSelfOnBoardingHeaderAppId);
                     }
                     return Mono.just(response);
                 });
            default:
                logger.error("INVALID current screen ID");
                //call all the above methods at once if you need to insert all data at once at the last screen (CONFIRMATION)
                break;
        }
        
        if(isThisLastStage) {
        	updateConfirmFlagInMaster(request, version, applicationID, customerIdentification, prop, isSelfOnBoardingAppId, isSelfOnBoardingHeaderAppId);
        }
        if(isAccountCreationisNextStage) {
        	HashMap<String, Object> hm2=new HashMap<>(); // HM is used to keep number of arguments less than 8 as per sonarqube
        	hm2.put(versionHm, version);
        	hm2.put(propHm, prop);
        	hm2.put(headerHm, header);
        	hm2.put(applicationIDHm, applicationID);
        	return accountCreationStageOperations(hm2, applicationMaster, requestObj, isSelfOnBoardingAppId, request, customerIdentification, isSelfOnBoardingHeaderAppId);
        }
        Gson gson = new Gson();
        String responseStr = gson.toJson(customerIdentification);
        responseBody.setResponseObj(responseStr);
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return Mono.just(response);
    }
    
    private Mono<Response> accountCreationStageOperations(HashMap<String, Object> hm, ApplicationMaster applicationMaster, CreateDepositRequestFields requestObj, boolean isSelfOnBoardingAppId, 
    		CreateDepositRequest request, CustomerIdentificationDep customerIdentification, boolean isSelfOnBoardingHeaderAppId) {
    	int version=(int) hm.get(versionHm);
    	Properties prop = (Properties) hm.get(propHm);
    	Header header=(Header) hm.get(headerHm);
    	String applicationID=(String) hm.get(applicationIDHm);  
    	String[] strAr1=new String[] {Constants.ACCOUNT_CREATION, "Y"}; 
    	commonService.updateCurrentStageInMaster(applicationMaster, strAr1, version, requestObj.getAppId(), requestObj.getApplicationId());
    	HashMap<String, Object> hm1=new HashMap<>(); // HM is used to keep number of arguments less than 8 as per sonarqube
    	hm1.put(propHm, prop);
    	hm1.put(versionHm, version);
    	return createAccountInCbs(hm1, isSelfOnBoardingAppId, request, customerIdentification, header, isSelfOnBoardingHeaderAppId, applicationID);
	}

	private Mono<Response> createAccountInCbs(HashMap<String, Object> hm1, boolean isSelfOnBoardingAppId, CreateDepositRequest request, CustomerIdentificationDep customerIdentification, Header header,
			boolean isSelfOnBoardingHeaderAppId, String applicationID) {
		Properties prop = (Properties) hm1.get(propHm);
    	int version=(int) hm1.get(versionHm);
		CreateDepositRequestFields requestObj = request.getRequestObj();
		ApplicationMaster masterRequest = requestObj.getApplicationMaster();
		Optional<ApplicationMaster> masterObjDb = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(), requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
		if (masterObjDb.isPresent()) {
			ApplicationMaster masterObj = masterObjDb.get();
			 if (isSelfOnBoardingAppId) { // self onboarding
				 if ("N".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.DEPOSIT_STP.getKey()))) {
					 commonService.createAccountInCbsForNonStp(isSelfOnBoardingHeaderAppId, masterObj);
				 } else if ("Y".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.DEPOSIT_STP.getKey()))) {
					 String accNum;
	                    if (CommonUtils.isNullOrEmpty(masterRequest.getAccNumber())) {//This is to handle the case if user changed the data after its being inserted by using the back navigation within the session.
	                        accNum = CommonUtils.generateRandomNumStr();
	                    } else {
	                        accNum = masterRequest.getAccNumber();
	                    }
	                    masterObj.setAccNumber(accNum);
	                    customerIdentification.setAccNumber(accNum);
	                    masterObj.setApplicationStatus(AppStatus.APPROVED.getValue());
	                    applicationMasterRepo.save(masterObj);
	                 // Hook to call external service for deposit account creation.
	                    CreateDepositRequest extReq=formExtReq(requestObj.getAppId(), requestObj.getApplicationId(), version, accNum, masterObj.getCustomerId());
	                    String interfaceName=prop.getProperty(CobFlagsProperties.DEP_ACC_CREATION_INTF.getKey());
	                    Mono<Object> extRes  = interfaceAdapter.callExternalService(header, extReq, interfaceName);
	                    return extRes.flatMap(val->{
	                    	ResponseWrapper res = adapterUtil.getResponseMapper(val, interfaceName, header);
	                    	customerIdentification.setVersionNum(version);
	                    	customerIdentification.setApplicationId(applicationID);
				            Response response = new Response();
				            ResponseBody responseBody = new ResponseBody();
				            ResponseHeader responseHeader = new ResponseHeader();
				    		responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				    		Gson gson = new Gson();
				            String responseStr = gson.toJson(customerIdentification);
				            responseBody.setResponseObj(responseStr);
				            response.setResponseHeader(responseHeader);
				            response.setResponseBody(responseBody);
				            return Mono.just(response);
	                    });
				 }
			 } else { //assisted on boarding
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

	private FundAccountRequest formFundAccRequest(CreateDepositRequestFields requestObj) {
    	FundAccountRequest faReq=new FundAccountRequest(); 
    	FundAccountRequestFields faReqFields=new FundAccountRequestFields();
    	FundAccountRequestFields faReqObj=requestObj.getFundDepositAccount();
    	BeanUtils.copyProperties(faReqObj, faReqFields);
    	faReq.setRequestObj(faReqFields);   
    	return faReq;
	}
    
    private void updateCustIdAndBranchInMaster(CreateDepositRequestFields requestObj, int version) {
        ApplicationMaster masterRequest = requestObj.getApplicationMaster();
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(), requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
            masterObj.setCustomerId(masterRequest.getCustomerId());
            masterObj.setSearchCode1(masterRequest.getSearchCode1());
            applicationMasterRepo.save(masterObj);
        }
    }

    private void updateConfirmFlagInMaster(CreateDepositRequest request, int version, String applicationID,
                                           CustomerIdentificationDep customerIdentification, Properties prop, boolean isSelfOnBoardingAppId, boolean isSelfOnBoardingHeaderAppId) {
    	CreateDepositRequestFields requestObj = request.getRequestObj();
        ApplicationMaster masterRequest = requestObj.getApplicationMaster();
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(), requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
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
            	updateConfirmFlagInMasterForSob(prop, masterRequest, requestObjWf, isSelfOnBoardingHeaderAppId, apiRequest);
            } else { //assisted on boarding
            	requestObjWf.setApplicationStatus(AppStatus.PENDING.getValue());
            	requestObjWf.setCreatedBy(masterRequest.getCreatedBy());
                String roleId = commonService.fetchRoleId(requestObj.getAppId(), masterRequest.getCreatedBy());
                if (wfObj.getCurrentRole().equalsIgnoreCase(roleId)) { //VAPT
                	commonService.populateApplnWorkFlow(apiRequest);
                } else {
                    logger.error("VAPT issue in updateAppMaster. Current role id from request is tampered.");
                }
            }
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
        }
    }

    private void updateConfirmFlagInMasterForSob(Properties prop, ApplicationMaster masterRequest,
			PopulateapplnWFRequestFields requestObjWf, boolean isSelfOnBoardingHeaderAppId, PopulateapplnWFRequest apiRequest) {
    	if ("N".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.DEPOSIT_STP.getKey()))) {
    		if (!isSelfOnBoardingHeaderAppId) {  //INITIATOR submits it after review.
    			requestObjWf.setApplicationStatus(AppStatus.PENDING.getValue());
    			requestObjWf.setCreatedBy(masterRequest.getCreatedBy());
    		}
    		else {
    			requestObjWf.setCreatedBy("Customer");
    			requestObjWf.setApplicationStatus(AppStatus.INPROGRESS.getValue());
            }
    		commonService.populateApplnWorkFlow(apiRequest);
    	}
	}

	public CreateDepositRequest formExtReq(String appId, String applicationId, int version, String accNum, BigDecimal customerId) {
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, version);
        CreateDepositRequest request = null;
        if (applicationMasterOpt.isPresent()) {
            request = new CreateDepositRequest();
            CreateDepositRequestFields requestObj = new CreateDepositRequestFields();
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            ApplicationMaster masterObj = new ApplicationMaster();
            BeanUtils.copyProperties(applicationMasterData, masterObj);
            masterObj.setAccNumber(accNum);
            masterObj.setCustomerId(customerId);
            masterObj.setCreateTs(null); //to avoid jackson parsing error. Need to send data based on external service request during implementation.
            masterObj.setApplicationDate(null); //to avoid jackson parsing error. Need to send data based on external service request during implementation.
            requestObj.setApplicationMaster(masterObj);

            DepositDtls depositDtl = depositDtlsRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, version);
            requestObj.setDepositDetails(depositDtl);

            request.setRequestObj(requestObj);
        }
        return request;
    }

    private void updateDeclarationFlagInMaster(CreateDepositRequestFields requestObj, int version, String applicationID,
                                               CustomerIdentificationDep customerIdentification) {
        ApplicationMaster masterRequest = requestObj.getApplicationMaster();
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(), requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
//            masterObj.setDeclarationFlag(masterRequest.getDeclarationFlag());
            applicationMasterRepo.save(masterObj);
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
        }
    }

    private void populateAddressDtls(CreateDepositRequestFields requestObj, int version, String applicationID, CustomerIdentificationDep customerIdentification) {
        Gson gson = new Gson();
        AddressDetails addressDetails;
        List<String> nomineeList;
        String nomineeDtlId;
        List<String> addressList = new ArrayList<>();
        List<AddressDetailsWrapper> addressDetailsWrapperList = requestObj.getAddressDetailsWrapperList();
        for (AddressDetailsWrapper addressDetailsWrapper : addressDetailsWrapperList) {
            List<AddressDetails> addressListReq = addressDetailsWrapper.getAddressDetailsList();
            for (int i = 0; i < addressListReq.size(); i++) {
                addressDetails = addressListReq.get(i);
                if (addressDetails.getAddressDtlsId() == null) {//This is to handle the case if user changed the data after its being inserted by using the back navigation within the session.
                    BigDecimal addressDtlId = CommonUtils.generateRandomNum();
                    addressDetails.setAddressDtlsId(addressDtlId);
                }
                addressList.add(addressDetails.getAddressDtlsId().toString());//toString is required to avoid rounding issue of BigDecimal at front end.)
                nomineeList = customerIdentification.getNomineeList();
                nomineeDtlId = nomineeList.get(i);
                addressDetails.setUniqueId(new BigDecimal(nomineeDtlId));
                addressDetails.setAppId(requestObj.getAppId());
                addressDetails.setApplicationId(applicationID);
                String payload = gson.toJson(addressDetails.getPayload());
                addressDetails.setPayloadColumn(payload);
                addressDetails.setVersionNum(version);
                addressDetailsRepo.save(addressDetails);
            }
        }
        customerIdentification.setAddressList(addressList);
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
    }

    private void populateNomineeDtls(CreateDepositRequestFields requestObj, int version, String applicationID, CustomerIdentificationDep customerIdentification) {
        String payload;
        List<String> nomineeList = new ArrayList<>();
        Gson gson = new Gson();
        List<NomineeDetailsWrapper> nomineeDetailsWrapperList = requestObj.getNomineeDetailsWrapperList();
        for (NomineeDetailsWrapper nomineeDetailsWrapper : nomineeDetailsWrapperList) {
            List<NomineeDetails> nomineeListReq = nomineeDetailsWrapper.getNomineeDetailsList();
            for (NomineeDetails nomineeDetails : nomineeListReq) {
                if (nomineeDetails.getNomineeDtlsId() == null) {//This is to handle the case if user changed the data after its being inserted by using the back navigation within the session.
                    BigDecimal nomineeDtlId = CommonUtils.generateRandomNum();
                    nomineeDetails.setNomineeDtlsId(nomineeDtlId);
                }
                nomineeList.add(nomineeDetails.getNomineeDtlsId().toString());//toString is required to avoid rounding issue of BigDecimal at front end.
                nomineeDetails.setAppId(requestObj.getAppId());
                nomineeDetails.setApplicationId(applicationID);
                payload = gson.toJson(nomineeDetails.getPayload());
                nomineeDetails.setPayloadColumn(payload);
                nomineeDetails.setStatus(AppStatus.ACTIVE_STATUS.getValue());
                nomineeDetails.setVersionNum(version);
                nomineeDetailsRepo.save(nomineeDetails);
            }
        }
        customerIdentification.setNomineeList(nomineeList);
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
    }


    private void populateOrUpdateDepositDtls(CreateDepositRequestFields requestObj, int version, String applicationID, CustomerIdentificationDep customerIdentification, String src) {
        BigDecimal depositDtlId;
        DepositDtls depositDtlObj = null;
        DepositDtls depositDtlReq = requestObj.getDepositDetails();
        if(null != depositDtlReq) {
           if (depositDtlReq.getDepositDtlId() == null) {
                depositDtlObj = new DepositDtls();
                depositDtlId = CommonUtils.generateRandomNum();
                depositDtlObj.setAppId(requestObj.getAppId());
                depositDtlObj.setApplicationId(applicationID);
                depositDtlObj.setVersionNum(version);
                depositDtlObj.setDepositDtlId(depositDtlId);
            } else {
                Optional<DepositDtls> depositDtlDb = depositDtlsRepo.findById(depositDtlReq.getDepositDtlId());
                if (depositDtlDb.isPresent()) {
                    depositDtlObj = depositDtlDb.get();
                } 
            }
        }
        if (depositDtlObj != null) {
            if (Constants.DEPOSIT_DETAILS.equalsIgnoreCase(src)) {
                depositDtlObj.setDepositAmount(depositDtlReq.getDepositAmount());
                depositDtlObj.setTenureInDays(depositDtlReq.getTenureInDays());
                depositDtlObj.setTenureInMonths(depositDtlReq.getTenureInMonths());
                depositDtlObj.setTenureInYears(depositDtlReq.getTenureInYears());
                depositDtlObj.setMaturityInstn(depositDtlReq.getMaturityInstn());
                depositDtlObj.setRoi(depositDtlReq.getRoi());
                depositDtlObj.setInterest(depositDtlReq.getInterest());
                depositDtlObj.setMaturityDate(depositDtlReq.getMaturityDate());
                depositDtlObj.setMaturityAmount(depositDtlReq.getMaturityAmount());
                depositDtlObj.setProductType(depositDtlReq.getProductType());
            } else if (Constants.AUTO_PAY_MI.equalsIgnoreCase(src)) {
                depositDtlObj.setAutopayEnabled(depositDtlReq.getAutopayEnabled());
                if (!CommonUtils.isNullOrEmpty(depositDtlReq.getAutopayEnabled()) && "Y".equalsIgnoreCase(depositDtlReq.getAutopayEnabled())) {
                    depositDtlObj.setAutopaySrcAccount(depositDtlReq.getAutopaySrcAccount());
                    depositDtlObj.setAutopaySrcAccountType(depositDtlReq.getAutopaySrcAccountType());
                    depositDtlObj.setAutopayDate(depositDtlReq.getAutopayDate());
                }
                depositDtlObj.setPayoutAccount(depositDtlReq.getPayoutAccount());
                depositDtlObj.setPayoutAccountType(depositDtlReq.getPayoutAccountType());
            } else if (Constants.INITIAL_FUND.equalsIgnoreCase(src)) {
                depositDtlObj.setInitialFundAccount(depositDtlReq.getInitialFundAccount());
                depositDtlObj.setInitialFundAccountType(depositDtlReq.getInitialFundAccountType());
            }
            customerIdentification.setDepositDtlId(depositDtlObj.getDepositDtlId());
            customerIdentification.setVersionNum(version);
            customerIdentification.setApplicationId(applicationID);
            depositDtlsRepo.save(depositDtlObj);
        }
    }

    private void updateCustomerDtlInMaster(CreateDepositRequestFields requestObj, int version, String applicationID, CustomerIdentificationDep customerIdentification) {
        Optional<ApplicationMaster> masterObjDb = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(), requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
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

    private void populateAppMasterAndApplnwf(CreateDepositRequestFields requestObj, String applicationID, int version, CustomerIdentificationDep customerIdentification, boolean isSelfOnBoardingHeaderAppId, Properties prop) {
        ApplicationMaster appMasterReq = requestObj.getApplicationMaster();
        ApplicationMaster appMaster = new ApplicationMaster();
        appMaster.setAppId(requestObj.getAppId());
        appMaster.setApplicationDate(LocalDate.now());
        appMaster.setApplicationId(applicationID);
        appMaster.setApplicationStatus(AppStatus.INPROGRESS.getValue());
        appMaster.setCreatedBy(appMasterReq.getCreatedBy());
        appMaster.setApplicantsCount(appMasterReq.getApplicantsCount());
        appMaster.setEmailId(appMasterReq.getEmailId());
        appMaster.setMobileNumber(appMasterReq.getMobileNumber());
        appMaster.setProductCode(appMasterReq.getProductCode());
        appMaster.setProductGroupCode(appMasterReq.getProductGroupCode());
        appMaster.setVersionNum(version);
        appMaster.setCurrentScreenId(appMasterReq.getCurrentScreenId().split("~")[0]);
        appMaster.setCustomerId(appMasterReq.getCustomerId());
        appMaster.setSearchCode1(appMasterReq.getSearchCode1());
        if (!(CommonUtils.isNullOrEmpty(appMasterReq.getMobileNumber()))) {
            appMaster.setMobileVerStatus("Y");
        }
        if (!(CommonUtils.isNullOrEmpty(appMasterReq.getEmailId()))) {
            appMaster.setEmailVerStatus("Y");
        }
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        applicationMasterRepo.save(appMaster);
        if (!isSelfOnBoardingHeaderAppId || ("N".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.DEPOSIT_STP.getKey())))) {
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
            commonService.populateApplnWorkFlow(apiRequest);
            logger.warn("Data inserted into TB_ABOB_APPLN_WORKFLOW");
            Optional<ApplicationWorkflow> workflow = applnWfRepository.findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(requestObj.getAppId(), applicationID, version);
            if (workflow.isPresent()) {
                ApplicationWorkflow applnWf = workflow.get();
                List<WorkflowDefinition> wfDefnList = wfDefnrepo.findByFromStageId(applnWf.getNextWorkFlowStage());
                customerIdentification.setApplnWfDefinitionList(wfDefnList);
            }
        }
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "isValidStageFallback")
    public boolean isValidStage(CreateDepositRequest createDepositRequest, boolean isSelfOnBoardingHeaderAppId, JSONArray array) {
    	boolean flag=false;
    	String currentScrIdFromDb = null;
        String prevElement = "";
        CreateDepositRequestFields requestObj = createDepositRequest.getRequestObj();
        ApplicationMaster appMasterReq = requestObj.getApplicationMaster();
        String[] currentScreenIdArray = appMasterReq.getCurrentScreenId().split("~");
        if ("N".equalsIgnoreCase(currentScreenIdArray[1])) { //back navigation flow
        	flag= true;
        } else if (array != null) {
        	String firstElementArr = ((String) array.get(0)).split("~")[0];
            String secondElementArr = ((String) array.get(1)).split("~")[0];
            String thirdElementArr = ((String) array.get(2)).split("~")[0];
            String fourthElementArr = ((String) array.get(3)).split("~")[0];
            String currenScrnIdReq = currentScreenIdArray[0];
            Optional<ApplicationMaster> masterObjDb = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(), requestObj.getApplicationId(), requestObj.getVersionNum(), AppStatus.INPROGRESS.getValue());
            if (masterObjDb.isPresent()) {
                ApplicationMaster masterObj = masterObjDb.get();
                currentScrIdFromDb = masterObj.getCurrentScreenId();
            }
            
            List<Object> list = array.toList();
            for (int i = 0; i < list.size(); i++) {
                String element = (String) list.get(i);
                list.remove(i);
                list.add(i, element.split("~")[0]);
            }
            int index = list.indexOf(currenScrnIdReq);
            if (index > 0) {
                prevElement = (String) list.get(index - 1);
            }
            
            if (CommonUtils.isNullOrEmpty(requestObj.getApplicationId()) || CommonUtils.isNullOrEmpty(currentScrIdFromDb)) {   //First hit to the service. Currentscrenid will be null when hit for the first time for reject-modify flow.
                if ((Constants.SELECT_PRODUCT.equalsIgnoreCase(firstElementArr) && Constants.VALIDATE_PINCODE.equalsIgnoreCase(secondElementArr)) || 
                	(Constants.VALIDATE_PINCODE.equalsIgnoreCase(firstElementArr) && Constants.SELECT_PRODUCT.equalsIgnoreCase(secondElementArr))) { // Service is not hit for SELECTPRODUCT
                    if ((Constants.DEPOSIT_DETAILS.equalsIgnoreCase(thirdElementArr) && fourthElementArr.equalsIgnoreCase(currenScrnIdReq)) || thirdElementArr.equalsIgnoreCase(currenScrnIdReq)) {
                        flag= true;
                    }
                } else {
                	flag=flagBasedOnScrnId(firstElementArr, secondElementArr, thirdElementArr, currenScrnIdReq);
                }
                if ((!(isSelfOnBoardingHeaderAppId)) && requestObj.getVersionNum() > 1 && Constants.CUST_VERIFICATION.equalsIgnoreCase(prevElement)) {  // required for version>1 case. During reject modify flow.
                	flag=  true;
                }
            } else {
                for (int i = 0; i < array.length(); i++) {
                    if (((String) array.get(i)).split("~")[0].equalsIgnoreCase(currentScrIdFromDb)) {
                        String nextArrayElement = ((String) array.get(i + 1));
                        if (nextArrayElement.split("~")[0].equalsIgnoreCase(Constants.SELECT_PRODUCT) || nextArrayElement.split("~")[0].equalsIgnoreCase(Constants.VALIDATE_PINCODE) || 
                        	nextArrayElement.split("~")[0].equalsIgnoreCase(Constants.ACCOUNT_CREATION) ) {
                        	String secondNextArrayElement = ((String) array.get(i + 2));
                        	if(secondNextArrayElement.split("~")[0].equalsIgnoreCase(Constants.SELECT_PRODUCT) || secondNextArrayElement.split("~")[0].equalsIgnoreCase(Constants.VALIDATE_PINCODE) || 
                            		secondNextArrayElement.split("~")[0].equalsIgnoreCase(Constants.ACCOUNT_CREATION)) {
                        		String thirdNextArrayElement = ((String) array.get(i + 3));
                        		if(thirdNextArrayElement.split("~")[0].equalsIgnoreCase(Constants.SELECT_PRODUCT) || thirdNextArrayElement.split("~")[0].equalsIgnoreCase(Constants.VALIDATE_PINCODE) || 
                            			thirdNextArrayElement.split("~")[0].equalsIgnoreCase(Constants.ACCOUNT_CREATION)) {
                        			String fourthNextArrayElement = ((String) array.get(i + 4));
                        			if (fourthNextArrayElement.split("~")[0].equalsIgnoreCase(currenScrnIdReq)) {
                        				flag=  true;
                                    }
                            	}
                            	if (thirdNextArrayElement.split("~")[0].equalsIgnoreCase(currenScrnIdReq)) {
                            		flag=  true;
                                }
                        	}
                            if (secondNextArrayElement.split("~")[0].equalsIgnoreCase(currenScrnIdReq)) {
                            	flag=  true;
                            }
                        } else {
                            if (nextArrayElement.split("~")[0].equalsIgnoreCase(currenScrnIdReq)) {
                            	flag=  true;
                            }
                        }
                    }
                }
            }
        }
        return flag;
    }

    private boolean flagBasedOnScrnId(String firstElementArr, String secondElementArr, String thirdElementArr, String currenScrnIdReq) {
    	if (Constants.DEPOSIT_DETAILS.equalsIgnoreCase(firstElementArr)) {
            if (Constants.SELECT_PRODUCT.equalsIgnoreCase(secondElementArr)) {
                if (thirdElementArr.equalsIgnoreCase(currenScrnIdReq)) {
                	return true;
                }
            } else {
                if (secondElementArr.equalsIgnoreCase(currenScrnIdReq)) {
                	return true;
                }
            }
        } else if (firstElementArr.equalsIgnoreCase(currenScrnIdReq)) {
        	return true;
        }
    	return false;
	}

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchCustomerDetailsFallback")
    public Mono<Object> fetchCustomerDetails(FetchCustDtlRequest request, Header header) {
        return interfaceAdapter.callExternalService(header, request, request.getInterfaceName());
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchNomineeFallback")
    public Mono<Object> fetchNominee(FetchCustDtlRequest request, Header header) {
        return interfaceAdapter.callExternalService(header, request, request.getInterfaceName());
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "deleteNomineeFallback")
    public Response deleteNominee(DeleteNomineeRequest request) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        response.setResponseHeader(responseHeader);
        DeleteNomineeRequestFields reqObj = request.getRequestObj();
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(reqObj.getAppId(), reqObj.getApplicationId(), reqObj.getVersionNum(), AppStatus.INPROGRESS.getValue());
        if (applicationMasterOpt.isPresent()) {
            if (reqObj.getNomineeId() != null) {
                Optional<NomineeDetails> nomineeDtl = nomineeDetailsRepo.findById(reqObj.getNomineeId());
                if (nomineeDtl.isPresent()) {
                    NomineeDetails nomineeObj = nomineeDtl.get();
                    nomineeObj.setStatus(AppStatus.INACTIVESTATUS.getValue());
                    nomineeDetailsRepo.save(nomineeObj);
                    responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
                    responseBody.setResponseObj("Nominee deleted.");
                }
            } else {
                responseHeader.setResponseCode(ResponseCodes.INVALID_NOMINEE.getKey());
                responseBody.setResponseObj(ResponseCodes.INVALID_NOMINEE.getValue());
            }
        } else {
            responseHeader.setResponseCode(ResponseCodes.INVALID_APP_MASTER.getKey());
            responseBody.setResponseObj(ResponseCodes.INVALID_APP_MASTER.getValue());
        }
        response.setResponseBody(responseBody);
        return response;

    }
    
    @CircuitBreaker(name = "fallback", fallbackMethod = "checkApplicationFallback")
    public Mono<Response> checkApplication(FetchCustDtlRequest request, Header header, Properties prop) {
        Gson gson = new Gson();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        if ("Y".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.EXT_SYSTEM_DEDUPE_REQUIRED.getKey()))) {
            //dedupe check hook.
            Mono<Object> extResponse = interfaceAdapter.callExternalService(header, request, request.getInterfaceName());
            return extResponse.flatMap(val->{
            	Response response = new Response();
            	ResponseWrapper res = adapterUtil.getResponseMapper(val, request.getInterfaceName(), header);
            	if (res.getApiResponse() != null && ResponseParser.isExtCallSuccess(res.getApiResponse(), "checkApplication")) {
                    if (ResponseParser.isNewCustomer(res.getApiResponse())) {
                    	response=checkApplication(responseHeader, request, prop, responseBody);
                    } else {
                        responseHeader.setResponseCode(ResponseCodes.APP_PRESENT_APPROVED_STATUS.getKey()); //IV109
                        JSONArray customerList = ResponseParser.getApplicationList(res.getApiResponse());
                        responseBody.setResponseObj(gson.toJson(customerList));
                        response.setResponseHeader(responseHeader);
                        response.setResponseBody(responseBody);
                    }
                } else {
                    //custom code to handle failure of external API.
                    responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                    response.setResponseHeader(responseHeader);
                }
            	return Mono.just(response);
            });
        } else if ("N".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.EXT_SYSTEM_DEDUPE_REQUIRED.getKey()))) {
        	Response response=checkApplication(responseHeader, request, prop, responseBody);
        	return Mono.just(response);
        } else {
        	return Mono.empty();
        }
    }
    
    public Response checkApplication(ResponseHeader responseHeader, FetchCustDtlRequest request, Properties prop, ResponseBody responseBody) {
    	Gson gson = new Gson();
    	Response response = new Response();
    	CheckApplicationRes resElements = new CheckApplicationRes();
        List<String> inprogress = new ArrayList<>();
        FetchCustDtlRequestFields requestFields = request.getRequestObj();
        String customerId = getCustomerId(requestFields);
        String productGroupCode = getProductGrpCode(requestFields);
        String res = "";
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        List<String> statusList = new ArrayList<>();
        statusList.add(AppStatus.INPROGRESS.getValue());
        statusList.add(AppStatus.APPROVED.getValue());
        List<ApplicationMaster> appMasterObj = applicationMasterRepo.findData(requestFields.getAppId(), new BigDecimal(customerId), productGroupCode, statusList);
        boolean iv108 = false;
        boolean iv115 = false;
        boolean iv109 = false;
        for (ApplicationMaster appMasterObjDb : appMasterObj) {
            String headerAppId = request.getAppId();
            JSONArray array=fetchArrayBasedOnHeaderAppid(headerAppId, prop);
            String lastElementArr = ((String) array.get(array.length() - 1)).split("~")[0];
            String currentSrnId = appMasterObjDb.getCurrentScreenId();
            if (appMasterObjDb != null && AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObjDb.getApplicationStatus())) {
                iv109 = true;
            } else if (appMasterObjDb != null && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObjDb.getApplicationStatus()) && lastElementArr.equalsIgnoreCase(currentSrnId)) {
                res = appMasterObjDb.getApplicationId() + "~" + appMasterObjDb.getAppId() + "~" + appMasterObjDb.getVersionNum() + "~" + appMasterObjDb.getApplicationStatus() + "~" + appMasterObjDb.getRelatedApplicationId() + "~" + appMasterObjDb.getProductGroupCode() + "~" + appMasterObjDb.getProductCode();
                inprogress.add(res);// All stages are done but still in inprogress status so dont allow to proceed.
                iv115 = true;
            } else {
            	iv108 = checkAllowPartialApp(prop, appMasterObjDb, responseHeader, inprogress);
            }
        }
        resElements.setInProgress(inprogress);
        responseBody.setResponseObj(gson.toJson(resElements));
        setResHeaderBasedOnFlag(iv108, iv109, iv115, responseHeader);
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    private boolean checkAllowPartialApp(Properties prop, ApplicationMaster appMasterObjDb, ResponseHeader responseHeader, List<String> inprogress) {
    	String allowPartialApplication = prop.getProperty(CobFlagsProperties.ALLOW_PARTIAL_APPLICATION.getKey());
        if ("Y".equalsIgnoreCase(allowPartialApplication) && appMasterObjDb != null) {
            inprogress.add(appMasterObjDb.getApplicationId() + "~" + appMasterObjDb.getAppId() + "~" + appMasterObjDb.getVersionNum() + "~" + appMasterObjDb.getApplicationStatus() + "~" + appMasterObjDb.getRelatedApplicationId() + "~" + appMasterObjDb.getProductGroupCode() + "~" + appMasterObjDb.getProductCode());
            if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObjDb.getApplicationStatus())) {
               return true;
            }
        } else if ("N".equalsIgnoreCase(allowPartialApplication)) {
        	dontAllowPartialApp(appMasterObjDb, responseHeader, prop);
        	return false;
        }
        return false;
	}

	private String getProductGrpCode(FetchCustDtlRequestFields requestFields) {
    	if (!(CommonUtils.isNullOrEmpty(requestFields.getProductGroupCode()))) {
            return requestFields.getProductGroupCode();
        }
		return null;
	}

	private String getCustomerId(FetchCustDtlRequestFields requestFields) {
    	if (!(CommonUtils.isNullOrEmpty(requestFields.getCustomerId()))) {
            return requestFields.getCustomerId();
        }
    	return null;
	}
	
	private void setResHeaderBasedOnFlag(boolean iv108, boolean iv109, boolean iv115, ResponseHeader responseHeader) {
    	if (iv108 && !iv109 && !iv115) {
            responseHeader.setResponseCode(ResponseCodes.APP_PRESENT_INPROGRESS_STATUS.getKey()); //IV108
        } else if (!iv108 && iv109 && !iv115) {
            responseHeader.setResponseCode(ResponseCodes.APP_PRESENT_APPROVED_STATUS.getKey()); //IV109
        } else if (!iv108 && !iv109 && iv115) {
            responseHeader.setResponseCode(ResponseCodes.APP_PRESENT_INPROGRESS_LAST_STAGE.getKey()); // All stages are done but still in inprogress status so dont allow to proceed. IV115
        } else if (iv108 && iv109 && !iv115) {
            responseHeader.setResponseCode(ResponseCodes.APP_PRESENT_INPROGRESS_STATUS.getKey()); //IV108
        } else if (!iv108 && iv109 && iv115) {
            responseHeader.setResponseCode(ResponseCodes.APP_PRESENT_INPROGRESS_LAST_STAGE.getKey()); // All stages are done but still in inprogress status so dont allow to proceed. IV115
        }
	}

	private void dontAllowPartialApp(ApplicationMaster appMasterObjDb, ResponseHeader responseHeader, Properties prop) {
    	String deleteRule = prop.getProperty(CobFlagsProperties.DEPOSIT_DELETE_RULE.getKey());
    	if(appMasterObjDb != null) {
    		if (Constants.HARD_DELETE.equalsIgnoreCase(deleteRule)) {
                deleteApplication(appMasterObjDb.getApplicationId(), appMasterObjDb.getAppId());
            } else if (Constants.MOVE_TO_HISTORY_TABLES.equalsIgnoreCase(deleteRule)) {
                populateHistoryTables(appMasterObjDb.getApplicationId(), appMasterObjDb.getAppId());
            } else if (Constants.UPDATE_STATUS.equalsIgnoreCase(deleteRule)) {
                appMasterObjDb.setApplicationStatus(AppStatus.DELETED.getValue());
                applicationMasterRepo.save(appMasterObjDb);
            } else {
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey()); //IV108
            }
    	}
    }

	private JSONArray fetchArrayBasedOnHeaderAppid(String headerAppId, Properties prop) {
    	if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
            return commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.DEPOSIT_ETB.getKey(), Constants.FUNCTIONSEQUENCE);
        } else {
            return  commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.DEPOSIT_BO_ETB.getKey(), Constants.FUNCTIONSEQUENCE);
        }
	}

	public void deleteApplication(String applicationId, String appId) {
        applicationMasterRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        nomineeDetailsRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        addressDetailsRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        depositDtlsRepo.deleteByApplicationIdAndAppId(applicationId, appId);
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchApplicationFallback")
    public Response fetchApplication(FetchDeleteUserRequest fetchUserDetailsRequest) {
        String applicationId = fetchUserDetailsRequest.getRequestObj().getApplicationId();
        String appId = fetchUserDetailsRequest.getRequestObj().getAppId();
        int versionNum = fetchUserDetailsRequest.getRequestObj().getVersionNum();
        Gson gson = new Gson();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        response.setResponseHeader(responseHeader);
        ResponseBody responseBody = new ResponseBody();
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepo.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, applicationId);
        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            CreateDepositRequestFields customerDataFields = getCustomerData(applicationMasterData, applicationId, appId, versionNum);
            String customerdata = gson.toJson(customerDataFields);
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

    private CreateDepositRequestFields getCustomerData(ApplicationMaster applicationMasterData, String applicationId, String appId, int versionNum) {
        CreateDepositRequestFields customerDataFields = new CreateDepositRequestFields();
        customerDataFields.setAppId(applicationMasterData.getAppId());
        customerDataFields.setApplicationId(applicationMasterData.getApplicationId());
        customerDataFields.setApplicationMaster(applicationMasterData);
        customerDataFields.setVersionNum(applicationMasterData.getVersionNum());

        AddressDetailsWrapper addressDetailsWrapper = new AddressDetailsWrapper();
        List<AddressDetailsWrapper> addressDetailsWrapperList = new ArrayList<>();
        List<AddressDetails> addressDetailsList = addressDetailsRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        addressDetailsWrapper.setAddressDetailsList(addressDetailsList);
        addressDetailsWrapperList.add(addressDetailsWrapper);
        customerDataFields.setAddressDetailsWrapperList(addressDetailsWrapperList);

        NomineeDetailsWrapper nomineeDetailsWrapper = new NomineeDetailsWrapper();
        List<NomineeDetailsWrapper> nomineeDetailsWrapperList = new ArrayList<>();
        List<NomineeDetails> nomineeDetailsList = nomineeDetailsRepo.findByApplicationIdAndAppIdAndVersionNumAndStatus(applicationId, appId, versionNum, AppStatus.ACTIVE_STATUS.getValue());
        nomineeDetailsWrapper.setNomineeDetailsList(nomineeDetailsList);
        nomineeDetailsWrapperList.add(nomineeDetailsWrapper);
        customerDataFields.setNomineeDetailsWrapperList(nomineeDetailsWrapperList);

        DepositDtls depositDetails = depositDtlsRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        customerDataFields.setDepositDetails(depositDetails);

        Optional<ApplicationWorkflow> workflow = applnWfRepository.findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(appId, applicationId, versionNum);
        if (workflow.isPresent()) {
            ApplicationWorkflow applnWf = workflow.get();
            List<WorkflowDefinition> wfDefnLis = wfDefnRepositoryDp.findByFromStageId(applnWf.getNextWorkFlowStage());
            customerDataFields.setApplnWfDefinitionList(wfDefnLis);
        }

        customerDataFields.setApplicationTimelineDtl(commonService.getApplicationTimelineDtl(applicationMasterData.getApplicationId()));

        return customerDataFields;
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "discardApplicationFallback")
    public boolean discardApplication(CreateDepositRequest req, Properties prop) {
        CreateDepositRequestFields requestFields = req.getRequestObj();
        ApplicationMaster masterObj = requestFields.getApplicationMaster();
        String deleteRule;
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusAndCustomerId(requestFields.getAppId(), requestFields.getApplicationId(), requestFields.getVersionNum(), AppStatus.INPROGRESS.getValue(), masterObj.getCustomerId());
        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster masterObjDb = applicationMasterOpt.get();
            deleteRule = prop.getProperty(CobFlagsProperties.DEPOSIT_DELETE_RULE.getKey());
            if (Constants.HARD_DELETE.equalsIgnoreCase(deleteRule)) {
                deleteApplication(requestFields.getApplicationId(), requestFields.getAppId());
            } else if (Constants.MOVE_TO_HISTORY_TABLES.equalsIgnoreCase(deleteRule)) {
                populateHistoryTables(requestFields.getApplicationId(), requestFields.getAppId());
            } else if (Constants.UPDATE_STATUS.equalsIgnoreCase(deleteRule)) {
                masterObjDb.setApplicationStatus(AppStatus.DELETED.getValue());
                applicationMasterRepo.save(masterObjDb);
            }
            if(!CommonUtils.isNullOrEmpty(masterObjDb.getRelatedApplicationId())) {  //discard the corresponding casa
            	CustomerDataFields requestObj=new CustomerDataFields();
            	CreateModifyUserRequest apiRequest=new CreateModifyUserRequest();
            	requestObj.setApplicationId(masterObjDb.getRelatedApplicationId());
            	requestObj.setAppId(requestFields.getAppId());
            	requestObj.setVersionNum(requestFields.getVersionNum());
            	apiRequest.setRequestObj(requestObj);            	
            	return cobService.discardApplication(apiRequest);           
            }
            return true;
        } else {
            return false;
        }
    }

    private void populateHistoryTables(String applicationId, String appId) {
        List<ApplicationMaster> appMasterOpt = applicationMasterRepo.findByAppIdAndApplicationId(appId, applicationId);
        if (null!=appMasterOpt && appMasterOpt.size()>0){
        	for(ApplicationMaster appMaster:appMasterOpt) {
        		ApplicationMasterHistory appMasterHistory = new ApplicationMasterHistory();
                BeanUtils.copyProperties(appMaster, appMasterHistory);
                applicationMasterHisRepo.save(appMasterHistory);
                applicationMasterRepo.deleteByApplicationIdAndAppId(applicationId, appId);	
        	}
            
            NomineeDetailsHistory nomineehistory;
            List<NomineeDetails> nomineeList = nomineeDetailsRepo.findByApplicationIdAndAppId(applicationId, appId);
            for (NomineeDetails nomineeObj : nomineeList) {
                nomineehistory = new NomineeDetailsHistory();
                BeanUtils.copyProperties(nomineeObj, nomineehistory);
                nomineeDetailsHisRepo.save(nomineehistory);
            }
            nomineeDetailsRepo.deleteByApplicationIdAndAppId(applicationId, appId);

            AddressDetailsHistory addressHistory;
            List<AddressDetails> addressList = addressDetailsRepo.findByApplicationIdAndAppId(applicationId, appId);
            for (AddressDetails addressObj : addressList) {
                addressHistory = new AddressDetailsHistory();
                BeanUtils.copyProperties(addressObj, addressHistory);
                addressDetailsHisRepo.save(addressHistory);
            }
            addressDetailsRepo.deleteByApplicationIdAndAppId(applicationId, appId);

            DepositDtlsHis depositHistory;
            List<DepositDtls> depositList = depositDtlsRepo.findByApplicationIdAndAppId(applicationId, appId);
            for (DepositDtls depositObj : depositList) {
                depositHistory = new DepositDtlsHis();
                BeanUtils.copyProperties(depositObj, depositHistory);
                depositDtlsHisRepo.save(depositHistory);
            }
            depositDtlsRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        }
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchRoiFallback")
    public Mono<Object> fetchRoi(FetchRoiRequest fetchRoiRequest, Header header) {
        return interfaceAdapter.callExternalService(header, fetchRoiRequest, fetchRoiRequest.getInterfaceName());
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "updateRelatedApplnIdDetailsFallback")
    public void updateRelatedApplnIdDetails(CreateDepositRequest apiRequest, String appId) {
        CreateDepositRequestFields requestObj = apiRequest.getRequestObj();
        Optional<ApplicationMaster> appMasterObj = applicationMasterRepo.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, requestObj.getApplicationId());
        if (appMasterObj.isPresent()) {
            ApplicationMaster appMasterObjDb = appMasterObj.get();
            String relatedApplnId = appMasterObjDb.getRelatedApplicationId();
            if (!CommonUtils.isNullOrEmpty(relatedApplnId)) {
                Optional<ApplicationMaster> appMasterObjRelated = applicationMasterRepo.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, relatedApplnId);
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

    @CircuitBreaker(name = "fallback", fallbackMethod = "downloadApplicationFallback")
    public Response downloadApplication(FetchDeleteUserRequest fetchAppReq) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        String applicationId = fetchAppReq.getRequestObj().getApplicationId();
        String appId = fetchAppReq.getRequestObj().getAppId();
        int versionNum = fetchAppReq.getRequestObj().getVersionNum();
        Optional<ApplicationMaster> applicationMasterOpt = applicationMasterRepo.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, applicationId);
        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            CreateDepositRequestFields customerDataFields = getCustomerData(applicationMasterData, applicationId, appId, versionNum);
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

    public String getCasaCurrentScrId(boolean isSelfOnBoardingHeaderAppId) {
        JSONArray array;
        if (isSelfOnBoardingHeaderAppId) {
            array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.CASA.getKey(), Constants.FUNCTIONSEQUENCE);
        } else {
            array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.CASA_BO.getKey(), Constants.FUNCTIONSEQUENCE);
        }
        return ((String) array.get(0)).split("~")[0];
    }
    
    public void duplicateDepositTablesETB(String appId, String applicationId, int newVersionNum, int oldVersionNum) {
		Optional<ApplicationMaster> appMasterForVersionCheck = applicationMasterRepo.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, applicationId);
		if (appMasterForVersionCheck.isPresent()) {
			ApplicationMaster appMaster = appMasterForVersionCheck.get();
	       	commonService.duplicateMasterData(appMaster, newVersionNum);
	       	duplicateDepositData(applicationId, appId, oldVersionNum, newVersionNum);
	      
	       	List<NomineeDetails> nomineeDetailsList = nomineeDetailsRepo.findByApplicationIdAndAppIdAndVersionNumAndStatus(applicationId, appId, oldVersionNum, AppStatus.ACTIVE_STATUS.getValue());
	       	for(NomineeDetails nomineeObj:nomineeDetailsList) {
	       		commonService.duplicateNomineeData(nomineeObj, newVersionNum, null);  //Cust Dtl ID to be null for deposit ETB as there is no customer detail screen. Change this logic if customer detail screen is introduced in deposit ETB.
	       	}
		}
	}
    
    private void duplicateDepositData(String applicationId, String appId, int oldVersionNum, int newVersionNum) {
		DepositDtls depositDtl = depositDtlsRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, oldVersionNum);
		if(null != depositDtl) {
			DepositDtls depositDtlNew=new DepositDtls(); 
			BeanUtils.copyProperties(depositDtl, depositDtlNew);
			depositDtlNew.setVersionNum(newVersionNum);
			depositDtlNew.setDepositDtlId(CommonUtils.generateRandomNum());
			depositDtlsRepo.save(depositDtlNew);
		}	
	}
    
    public void duplicateDepositTablesNTB(String appId, String applicationId, int newVersionNum, int oldVersionNum) {
      	 Optional<ApplicationMaster> appMasterForVersionCheck = applicationMasterRepo.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, applicationId);
           if (appMasterForVersionCheck.isPresent()) {
          	 ApplicationMaster appMaster = appMasterForVersionCheck.get();
          	 commonService.duplicateMasterData(appMaster, newVersionNum);
          	 duplicateDepositData(applicationId, appId, oldVersionNum, newVersionNum);
          	}
   	}

    @CircuitBreaker(name = "fallback", fallbackMethod = "fetchFunctionSeqArrayFallback")
    public JSONArray fetchFunctionSeqArray(CreateDepositRequest apiRequest, boolean isSelfOnBoardingHeaderAppId) {
		JSONArray array=null;
		if (isSelfOnBoardingHeaderAppId) {
            array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.DEPOSIT_ETB.getKey(), Constants.FUNCTIONSEQUENCE);
        } else {
            array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.DEPOSIT_BO_ETB.getKey(), Constants.FUNCTIONSEQUENCE);
        }
		return array;
	}
    
    // ALL FALLBACK METHODS
    
    private JSONArray fetchFunctionSeqArrayFallback(CreateDepositRequest apiRequest, boolean isSelfOnBoardingHeaderAppId, Exception e) {
		logger.error("fetchFunctionSeqArrayFallback error : " , e);
		return null;
	}
    
    private boolean isValidStageFallback(CreateDepositRequest createDepositRequest, boolean isSelfOnBoardingHeaderAppId, JSONArray array, Exception e) {
		logger.error("isValidStageFallback error : " , e);
		return false;
	}
    
    private boolean isVaptPassedForScreenElementsFallback(CreateDepositRequest request, boolean isSelfOnBoardingHeaderAppId, JSONArray array, Exception e) {
		logger.error("isVaptPassedForScreenElementsFallback error : " , e);
		return false;
	}
    
    private Mono<Response> createDepositFallback(HashMap<String, String> hm, CreateDepositRequest request, boolean isSelfOnBoardingAppId, 
    		boolean isSelfOnBoardingHeaderAppId, Properties prop, JSONArray array, Exception e) {
		logger.error("createDepositFallback error : " , e);
		return FallbackUtils.genericFallbackMono();
	}
    
    private void updateRelatedApplnIdDetailsFallback(CreateDepositRequest apiRequest, String appId, Exception e) {
		logger.error("updateRelatedApplnIdDetailsFallback error : " , e);
	}
    
    private Mono<Object> fetchCustomerDetailsFallback(FetchCustDtlRequest request, Header header, Exception e) {
		logger.error("fetchCustomerDetailsFallback error : " , e);
		return FallbackUtils.genericFallbackMonoObject();
	}
    
    private Mono<Object> fetchNomineeFallback(FetchCustDtlRequest request, Header header, Exception e) {
		logger.error("fetchNomineeFallback error : " , e);
		return FallbackUtils.genericFallbackMonoObject();
	}
    
    private Response deleteNomineeFallback(DeleteNomineeRequest request, Exception e) {
		logger.error("deleteNomineeFallback error : " , e);
		return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
	}
    
    private Mono<Response> checkApplicationFallback(FetchCustDtlRequest request, Header header, Properties prop, Exception e) {
		logger.error("checkApplicationFallback error : " , e);
		return FallbackUtils.genericFallbackMono();
	}
    
    private Response fetchApplicationFallback(FetchDeleteUserRequest fetchUserDetailsRequest, Exception e) {
		logger.error("fetchApplicationFallback error : " , e);
		return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
	}
    
    private Response downloadApplicationFallback(FetchDeleteUserRequest fetchAppReq, Exception e) {
		logger.error("downloadApplicationFallback error : " , e);
		return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
	}
    
    private boolean discardApplicationFallback(CreateDepositRequest req, Properties prop, Exception e) {
		logger.error("discardApplicationFallback error : " , e);
		return false;
	}
    
    private Mono<Object> fetchRoiFallback(FetchRoiRequest fetchRoiRequest, Header header, Exception e) {
		logger.error("fetchRoiFallback error : " , e);
		return FallbackUtils.genericFallbackMonoObject();
	}
}
