package com.iexceed.appzillonbanking.cob.cards.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.cards.domain.ab.CardDetails;
import com.iexceed.appzillonbanking.cob.cards.domain.ab.CardDetailsHistory;
import com.iexceed.appzillonbanking.cob.cards.payload.ApplyCreditCardRequest;
import com.iexceed.appzillonbanking.cob.cards.payload.ApplyCreditCardRequestFields;
import com.iexceed.appzillonbanking.cob.cards.payload.CheckAppCCRequestFields;
import com.iexceed.appzillonbanking.cob.cards.payload.CheckApplicationRequest;
import com.iexceed.appzillonbanking.cob.cards.payload.DeleteFileRequest;
import com.iexceed.appzillonbanking.cob.cards.payload.FetchAppReq;
import com.iexceed.appzillonbanking.cob.cards.payload.FetchCustDtlReq;
import com.iexceed.appzillonbanking.cob.cards.payload.FetchEligibleCardsReq;
import com.iexceed.appzillonbanking.cob.cards.payload.FetchEligibleCardsReqFields;
import com.iexceed.appzillonbanking.cob.cards.payload.UpdateCommonCodeRequest;
import com.iexceed.appzillonbanking.cob.cards.report.CcReport;
import com.iexceed.appzillonbanking.cob.cards.repository.ab.CardDetailsHisRepository;
import com.iexceed.appzillonbanking.cob.cards.repository.ab.CardDetailsRepository;
import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetailsHistory;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationDocuments;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationDocumentsHistory;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMasterHistory;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetailsHistory;
import com.iexceed.appzillonbanking.cob.core.domain.ab.OccupationDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.OccupationDetailsHistory;
import com.iexceed.appzillonbanking.cob.core.domain.ab.TbAbmiCommonCodeDomain;
import com.iexceed.appzillonbanking.cob.core.domain.ab.WorkflowDefinition;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.ApplicationDocumentsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CheckApplicationRes;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerIdentificationCards;
import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.payload.OccupationDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.PopulateapplnWFRequest;
import com.iexceed.appzillonbanking.cob.core.payload.PopulateapplnWFRequestFields;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.WorkFlowDetails;
import com.iexceed.appzillonbanking.cob.core.repository.ab.AddressDetailsHisRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.AddressDetailsRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationDocumentsHisRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationDocumentsRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationMasterHisRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationWorkflowRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.CustomerDetailsHisRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.CustomerDetailsRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.OccupationDetailsHisRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.OccupationDetailsRepository;
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
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;

import reactor.core.publisher.Mono;

@Service
public class CreditCardService {

    private static final Logger logger = LogManager.getLogger(CreditCardService.class);

    @Autowired
    private ApplicationMasterRepository appMasterRepo;

    @Autowired
    private ApplicationDocumentsRepository appDocRepo;

    @Autowired
    private CustomerDetailsRepository custDtlRepo;

    @Autowired
    private AddressDetailsRepository addressDtlRepo;

    @Autowired
    private OccupationDetailsRepository occupationDtlRepo;

    @Autowired
    private CardDetailsRepository cardDtlRepo;

    @Autowired
    private ApplicationWorkflowRepository applnWfRepository;

    @Autowired
    private InterfaceAdapter interfaceAdapter;

    @Autowired
    private CommonParamService commonService;

    @Autowired
    private ApplicationMasterHisRepository appMasterHisRepo;

    @Autowired
    private ApplicationDocumentsHisRepository appDocHisRepo;

    @Autowired
    private CustomerDetailsHisRepository custDtlHisRepo;

    @Autowired
    private AddressDetailsHisRepository addressDtlHisRepo;

    @Autowired
    private OccupationDetailsHisRepository occupationDtlHisRepo;

    @Autowired
    private CardDetailsHisRepository cardDtlHisRepo;

    @Autowired
    private CcReport report;
    
    @Autowired
    private WorkflowDefinitionRepository wfDefnRepoCc;

    @Autowired
    private AdapterUtil adapterUtil;
    
    private String versionHm="versionHm";
    private String headerHm="headerHm";
    private String applicationIDHm="applicationIDHm";
    private String propHm="propHm";
    
    

    public Mono<Response> applyCreditCard(ApplyCreditCardRequest request, Properties prop, boolean isSelfOnBoardingAppId, boolean isSelfOnBoardingHeaderAppId, JSONArray array, Header header) {
        ApplyCreditCardRequestFields requestObj = request.getRequestObj();
        ApplicationMaster applicationMaster = requestObj.getApplicationMaster();
        ResponseBody responseBody = new ResponseBody();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        CustomerIdentificationCards customerIdentification = new CustomerIdentificationCards();
        int version = 0;
        String applicationID;
        boolean isAccountCreationisNextStage=commonService.isAccountCreationisNextStage(applicationMaster.getCurrentScreenId().split("~")[0], array);
        boolean isThisLastStage=commonService.isThisLastStage(applicationMaster.getCurrentScreenId().split("~")[0], array);
        BigDecimal custDtlId=commonService.getCustDtlId(applicationMaster);
        if (CommonUtils.isNullOrEmpty(requestObj.getApplicationId())) {
            applicationID = CommonUtils.generateRandomNumStr();
            version = Constants.INITIAL_VERSION_NO; //initial creation of credit card application version number should be 1.
            populateAppMasterAndApplnwf(requestObj, applicationID, version, customerIdentification, prop, custDtlId, isSelfOnBoardingHeaderAppId);
            commonService.populateCustomerDtlsIfNotPresent(requestObj.getApplicationMaster(), applicationID, custDtlId, version, requestObj.getAppId());
        } else {  //this ID should be created once only.
            applicationID = requestObj.getApplicationId();
            Optional<ApplicationMaster> appMasterForVersionCheck = appMasterRepo.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(requestObj.getAppId(), applicationID);
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
        String[] currentScreenIdArray = applicationMaster.getCurrentScreenId().split("~");
        commonService.updateCurrentStageInMaster(requestObj.getApplicationMaster(), currentScreenIdArray, version, requestObj.getAppId(), requestObj.getApplicationId());
        switch (currentScreenIdArray[0]) {
            case Constants.CUST_VERIFICATION:
                updateCustomerDtlInMaster(requestObj, version, applicationID, customerIdentification);
                break;
            case Constants.KYC_VERIFICATION:
                populateApplicationDocs(requestObj, customerIdentification, applicationID, custDtlId, version);
                commonService.updateNationIdInMaster(requestObj.getApplicationMaster(), version, requestObj.getAppId(), requestObj.getApplicationId());
                //External service call hook to get the eligible cards.
                FetchEligibleCardsReq requestEl = formExtReqForElCards(request);
                Mono<Object> monoResponse = interfaceAdapter.callExternalService(header, requestEl, requestEl.getInterfaceName());
                final int versionFinal=version;
                return monoResponse.flatMap(val -> {
                	ResponseWrapper res = adapterUtil.getResponseMapper(val, requestEl.getInterfaceName(), header);
                    if (ResponseParser.isExtCallSuccess(res.getApiResponse(), "getEligibleCards")) {
                        ResponseParser.getResponseData(res.getApiResponse(), customerIdentification);
                    } else {
                        //custom code to handle failure of external API.
                        responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                    }
                    Gson gson = new Gson();
                    String responseStr = gson.toJson(customerIdentification);
                    responseBody.setResponseObj(responseStr);
                    response.setResponseBody(responseBody);
                    response.setResponseHeader(responseHeader);
                    if(isThisLastStage) {
                    	 updateConfirmFlagInMaster(request, versionFinal, applicationID, customerIdentification, prop, isSelfOnBoardingAppId, isSelfOnBoardingHeaderAppId);
                    }
                    if(isAccountCreationisNextStage) {
                    	HashMap<String, Object> hm=new HashMap<>(); // HM is used to keep number of arguments less than 8 as per sonarqube
                    	hm.put(versionHm, versionFinal);
                    	hm.put(propHm, prop);
                    	hm.put(headerHm, header);
                    	hm.put(applicationIDHm, applicationID);
                    	return accountCreationStageOperations(hm, applicationMaster, requestObj, isSelfOnBoardingAppId, request, customerIdentification, isSelfOnBoardingHeaderAppId);
                    }
                    return Mono.just(response);
                });
            case Constants.ELIGIBLE_CARDS:
                updateProductCodeInMaster(requestObj, version, applicationID, custDtlId, customerIdentification);
                populateOrUpdateCardDtls(requestObj, customerIdentification, applicationID, custDtlId, version, Constants.ELIGIBLE_CARDS);
                break;
            case Constants.CUSTOMER_DETAILS:
                populateCustomerDtls(requestObj, customerIdentification, applicationID, custDtlId, version);
                populateAddressDtls(requestObj, customerIdentification, applicationID, custDtlId, version, Constants.CUSTOMER_DETAILS);
                commonService.updatePanInMaster(requestObj.getApplicationMaster(), version, requestObj.getAppId(), requestObj.getApplicationId());
                break;
            case Constants.OCCUPATION_DETAILS:
                populateOccupationdtls(requestObj, customerIdentification, applicationID, custDtlId, version);
                populateAddressDtls(requestObj, customerIdentification, applicationID, custDtlId, version, Constants.OCCUPATION_DETAILS);
                break;
            case Constants.UPLOAD_DOCUMENTS:
                populateApplicationDocs(requestObj, customerIdentification, applicationID, custDtlId, version);
                break;
            case Constants.CARD_SERVICE:
                populateOrUpdateCardDtls(requestObj, customerIdentification, applicationID, custDtlId, version, Constants.CARD_SERVICE);
                break;
            case Constants.TERMS_AND_CONDITIONS:
                updateDeclarationFlagInMaster(requestObj, version, applicationID, custDtlId, customerIdentification);
                break;
            case Constants.CONFIRMATION:
            	// No action to do specifically for CONFIRMATION. Appropriate actions are taken based on return value of isAccountCreationisNextStage() and isThisLastStage().
            	customerIdentification.setCustDtlId(custDtlId.toString()); //to String is required to avoid rounding issue of Big Decimal at front end.
                customerIdentification.setApplicationId(applicationID);
                customerIdentification.setVersionNum(version);
                break;
            default:
                logger.error("INVALID current screen ID");
                //call all the above methods at once if you need to insert all data at once at the last screen (CONFIRMATION)
                break;
        }
        if(isThisLastStage) {
       	 updateConfirmFlagInMaster(request, version, applicationID, customerIdentification, prop, isSelfOnBoardingAppId, isSelfOnBoardingHeaderAppId);
        }
        if(isAccountCreationisNextStage) {
        	HashMap<String, Object> hm=new HashMap<>(); // HM is used to keep number of arguments less than 8 as per sonarqube
        	hm.put(versionHm, version);
        	hm.put(propHm, prop);
        	hm.put(headerHm, header);
        	hm.put(applicationIDHm, applicationID);
        	return accountCreationStageOperations(hm, applicationMaster, requestObj, isSelfOnBoardingAppId, request, customerIdentification, isSelfOnBoardingHeaderAppId);
        }
        Gson gson = new Gson();
        String responseStr = gson.toJson(customerIdentification);
        responseBody.setResponseObj(responseStr);
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return Mono.just(response);
    }

    private Mono<Response> accountCreationStageOperations(HashMap<String, Object> hm, ApplicationMaster applicationMaster, ApplyCreditCardRequestFields requestObj, boolean isSelfOnBoardingAppId, 
			ApplyCreditCardRequest request, CustomerIdentificationCards customerIdentification, boolean isSelfOnBoardingHeaderAppId) {
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

	private FetchEligibleCardsReq formExtReqForElCards(ApplyCreditCardRequest request) {
        FetchEligibleCardsReq requestEl = new FetchEligibleCardsReq();
        requestEl.setInterfaceName(request.getInterfaceName());
        FetchEligibleCardsReqFields requestObjEl = new FetchEligibleCardsReqFields();
        requestObjEl.setKycID(null);  // send value as per external API requirement.
        requestEl.setRequestObj(requestObjEl);
        return requestEl;
    }

    private void updateProductCodeInMaster(ApplyCreditCardRequestFields requestObj, int version, String applicationID, BigDecimal custDtlId, CustomerIdentificationCards customerIdentification) {
        ApplicationMaster masterRequest = requestObj.getApplicationMaster();
        Optional<ApplicationMaster> masterObjDb = appMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(), requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
            if (!(CommonUtils.isNullOrEmpty(masterRequest.getProductCode()))) {
                masterObj.setProductCode(masterRequest.getProductCode());
                appMasterRepo.save(masterObj);
            }
            customerIdentification.setCustDtlId(custDtlId.toString());//to String is required to avoid rounding issue of Big Decimal at front end.
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
        }
    }
    
    private Mono<Response> createAccountInCbs(HashMap<String, Object> hm1, boolean isSelfOnBoardingAppId, ApplyCreditCardRequest request, CustomerIdentificationCards customerIdentification, Header header,
			boolean isSelfOnBoardingHeaderAppId, String applicationID) {
		    	Properties prop = (Properties) hm1.get(propHm);
		    	int version=(int) hm1.get(versionHm);
				ApplyCreditCardRequestFields requestObj = request.getRequestObj();
				ApplicationMaster masterRequest = requestObj.getApplicationMaster();
				Optional<ApplicationMaster> masterObjDb = appMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(), requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
				if (masterObjDb.isPresent()) {
					ApplicationMaster masterObj = masterObjDb.get();
					if (isSelfOnBoardingAppId) { // self onboarding
						if ("N".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.CARD_STP.getKey()))) {
							if (!isSelfOnBoardingHeaderAppId) { // INITIATOR submits it after review.
								masterObj.setApplicationStatus(AppStatus.PENDING.getValue());
								appMasterRepo.save(masterObj);
							}
						} else if ("Y".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.CARD_STP.getKey()))) {
							String accNum;
							BigDecimal customerId;
							if (CommonUtils.isNullOrEmpty(masterRequest.getAccNumber())) {// This is to handle the case if user changed the data after its being inserted by using the back navigation within the session.
								accNum = CommonUtils.generateRandomNumStr();
							} else {
								accNum = masterRequest.getAccNumber();
							}
							if (masterRequest.getCustomerId() == null) {//This is to handle the case if user changed the data after its being inserted by using the back navigation within the session.
		                        customerId = CommonUtils.generateRandomNum();
		                    } else {
		                        customerId = masterRequest.getCustomerId();
		                    }
							masterObj.setAccNumber(accNum);
							masterObj.setCustomerId(customerId);
							masterObj.setApplicationStatus(AppStatus.APPROVED.getValue());
							appMasterRepo.save(masterObj);
							//update customer ID present in TB_ABOB_CUSTOMER_DETAILS table.
		                    Optional<CustomerDetails> custDtl = custDtlRepo.findById(masterRequest.getCustDtlId());
		                    if (custDtl.isPresent()) {
		                        CustomerDetails custDtlObj = custDtl.get();
		                        custDtlObj.setCustomerId(customerId);
		                        custDtlRepo.save(custDtlObj);
		                    }
		                    customerIdentification.setCustomerId(customerId.toString());//to String is required to avoid rounding issue of Big Decimal at front end.
		                    customerIdentification.setAccNumber(accNum);

							// Hook to call external service for credit card creation.
							ApplyCreditCardRequest extReq = formExtReq(requestObj.getAppId(), requestObj.getApplicationId(), version);
							String interfaceName=prop.getProperty(CobFlagsProperties.CARD_ACC_CREATION_INTF.getKey());
							Mono<Object> monoResponse = interfaceAdapter.callExternalService(header, extReq, interfaceName);
							return monoResponse.flatMap(val->{
								ResponseWrapper res = adapterUtil.getResponseMapper(val, interfaceName, header);
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
						appMasterRepo.save(masterObj);
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

	private void updateConfirmFlagInMaster(ApplyCreditCardRequest request, int version, String applicationID, CustomerIdentificationCards customerIdentification, Properties prop,
			boolean isSelfOnBoardingAppId, boolean isSelfOnBoardingHeaderAppId) {
		ApplyCreditCardRequestFields requestObj = request.getRequestObj();
		ApplicationMaster masterRequest = requestObj.getApplicationMaster();
		Optional<ApplicationMaster> masterObjDb = appMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(), requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
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
				if ("N".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.CARD_STP.getKey()))) {
					if (!isSelfOnBoardingHeaderAppId) { // INITIATOR submits it after review.
						requestObjWf.setApplicationStatus(AppStatus.PENDING.getValue());
						requestObjWf.setCreatedBy(masterRequest.getCreatedBy());
					} else {
						requestObjWf.setCreatedBy("Customer");
						requestObjWf.setApplicationStatus(AppStatus.INPROGRESS.getValue());
					}
					commonService.populateApplnWorkFlow(apiRequest);
				} 
			} else { // assisted on boarding
				requestObjWf.setApplicationStatus(AppStatus.PENDING.getValue());
				requestObjWf.setCreatedBy(masterRequest.getCreatedBy());
				String roleId = commonService.fetchRoleId(requestObj.getAppId(), masterRequest.getCreatedBy());
				if (wfObj.getCurrentRole().equalsIgnoreCase(roleId)) { // VAPT
					commonService.populateApplnWorkFlow(apiRequest);
				}
			}
			customerIdentification.setApplicationId(applicationID);
			customerIdentification.setVersionNum(version);
		}
	}
    
    public ApplyCreditCardRequest formExtReq(String appId, String applicationId, int version) {
    	ApplyCreditCardRequest request = new ApplyCreditCardRequest();
    	Optional<ApplicationMaster> applicationMasterOpt = appMasterRepo.findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, version);
    	if (applicationMasterOpt.isPresent()) {
			ApplicationMaster applicationMasterData = applicationMasterOpt.get();
			ApplicationMaster appMasterDb=new ApplicationMaster();
			BeanUtils.copyProperties(applicationMasterData, appMasterDb);
			appMasterDb.setCreateTs(null); // to avoid jackson parsing error. Need to send data based on external service request during implementation.
			appMasterDb.setApplicationDate(null); // to avoid jackson parsing error. Need to send data based external service request during implementation.
			ApplyCreditCardRequestFields requestObj = getCustomerData(appMasterDb, applicationId, appId, version);
			request.setRequestObj(requestObj); 
			return request;
		}
    	return null;
	}

    private void updateDeclarationFlagInMaster(ApplyCreditCardRequestFields requestObj, int version, String applicationID, BigDecimal custDtlId, CustomerIdentificationCards customerIdentification) {
        ApplicationMaster masterRequest = requestObj.getApplicationMaster();
        Optional<ApplicationMaster> masterObjDb = appMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(), requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
        if (masterObjDb.isPresent()) {
            ApplicationMaster masterObj = masterObjDb.get();
//            masterObj.setDeclarationFlag(masterRequest.getDeclarationFlag());
            appMasterRepo.save(masterObj);
            customerIdentification.setCustDtlId(custDtlId.toString());//to String is required to avoid rounding issue of Big Decimal at front end.
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
        }
    }

    private void populateOrUpdateCardDtls(ApplyCreditCardRequestFields requestObj, CustomerIdentificationCards customerIdentification, String applicationID, BigDecimal custDtlId, int version, String src) {
        CardDetails cardDtlObj = null;
        CardDetails cardDtlReq = requestObj.getCardDetails();
        if (cardDtlReq != null) {
            if (cardDtlReq.getCcDtlId() == null) { //This is to handle the case if user changed the data after its being inserted by using the back navigation within the session.
                BigDecimal ccDtlId = CommonUtils.generateRandomNum();
                cardDtlObj = new CardDetails();
                cardDtlObj.setAppId(requestObj.getAppId());
                cardDtlObj.setApplicationId(applicationID);
                cardDtlObj.setVersionNum(version);
                cardDtlObj.setCcDtlId(ccDtlId);
            } else {
                Optional<CardDetails> cardDtlObjDb = cardDtlRepo.findById(cardDtlReq.getCcDtlId());
                cardDtlObj = cardDtlObjDb.get();
            }
        }
        if (cardDtlObj != null) {
            if (Constants.ELIGIBLE_CARDS.equalsIgnoreCase(src)) {
                cardDtlObj.setCardName(cardDtlReq.getCardName());
                cardDtlObj.setCreditLimit(cardDtlReq.getCreditLimit());
                cardDtlObj.setWithdrawalLimit(cardDtlReq.getWithdrawalLimit());
                cardDtlObj.setCurrency(cardDtlReq.getCurrency());
            } else if (Constants.CARD_SERVICE.equalsIgnoreCase(src)) {
                cardDtlObj.setCardName(cardDtlReq.getCardName());
                cardDtlObj.setNameOnCard(cardDtlReq.getNameOnCard());
                cardDtlObj.setEmailStmtReq(cardDtlReq.getEmailStmtReq());
                cardDtlObj.setPhysicalStmtReq(cardDtlReq.getPhysicalStmtReq());
                cardDtlObj.setTheme(cardDtlReq.getTheme());
                cardDtlObj.setCustomTheme(cardDtlReq.getCustomTheme());
                cardDtlObj.setCustomImagePath(cardDtlReq.getCustomImagePath());
            }
            cardDtlRepo.save(cardDtlObj);
            customerIdentification.setCardDtlId(cardDtlObj.getCcDtlId());
            customerIdentification.setCustDtlId(custDtlId.toString());//to String is required to avoid rounding issue of Big Decimal at front end.
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
        }
    }

    private void populateOccupationdtls(ApplyCreditCardRequestFields requestObj,
                                        CustomerIdentificationCards customerIdentification, String applicationID, BigDecimal custDtlId, int version) {
        Gson gson = new Gson();
        List<String> occupationList = new ArrayList<>();
        List<OccupationDetailsWrapper> occupationDetailsWrapperList = requestObj.getOccupationDetailsWrapperList();
        for (OccupationDetailsWrapper occupationDetailsWrapper : occupationDetailsWrapperList) {
            OccupationDetails occupationDetails = occupationDetailsWrapper.getOccupationDetails();
            if (occupationDetails.getOccptDtlId() == null) {//This is to handle the case if user changed the data after its being inserted by using the back navigation within the session.
                BigDecimal occptnDtlId = CommonUtils.generateRandomNum();
                occupationDetails.setOccptDtlId(occptnDtlId);
                occupationList.add(occptnDtlId.toString());//to String is required to avoid rounding issue of Big Decimal at front end.
            } else {
                occupationList.add(occupationDetails.getOccptDtlId().toString());//to String is required to avoid rounding issue of Big Decimal at front end.
            }
            occupationDetails.setAppId(requestObj.getAppId());
            occupationDetails.setApplicationId(applicationID);
            occupationDetails.setCustDtlId(custDtlId);
            occupationDetails.setVersionNum(version);
            String payload = gson.toJson(occupationDetails.getPayload());
            occupationDetails.setPayloadColumn(payload);
            occupationDtlRepo.save(occupationDetails);
        }
        customerIdentification.setOccupationList(occupationList);
        customerIdentification.setCustDtlId(custDtlId.toString());//to String is required to avoid rounding issue of Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_OCCUPATION_DETAILS for credit card");
    }

    private void populateAddressDtls(ApplyCreditCardRequestFields requestObj,
                                     CustomerIdentificationCards customerIdentification, String applicationID, BigDecimal custDtlId, int version,
                                     String relatedScreen) {
        Gson gson = new Gson();
        List<String> addressList = new ArrayList<>();
        List<AddressDetailsWrapper> addressDetailsWrapperList = requestObj.getAddressDetailsWrapperList();
        for (AddressDetailsWrapper addressDetailsWrapper : addressDetailsWrapperList) {
            List<AddressDetails> addressDetailsList = addressDetailsWrapper.getAddressDetailsList();
            for (AddressDetails addressDetails : addressDetailsList) {
                if (addressDetails.getAddressDtlsId() == null) {//This is to handle the case if user changed the data after its being inserted by using the back navigation within the session.
                    BigDecimal addressDtlId = CommonUtils.generateRandomNum();
                    addressDetails.setAddressDtlsId(addressDtlId);
                    addressList.add(addressDtlId.toString());//to String is required to avoid rounding issue of Big Decimal at front end.
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
            }
            customerIdentification.setAddressList(addressList);
            customerIdentification.setCustDtlId(custDtlId.toString());//to String is required to avoid rounding issue of Big Decimal at front end.
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
        }
        logger.warn("Data inserted into TB_ABOB_ADDRESS_DETAILS for Credit card");
    }

    private void populateCustomerDtls(ApplyCreditCardRequestFields requestObj,
                                      CustomerIdentificationCards customerIdentification, String applicationID, BigDecimal custDtlId, int version) {
        Gson gson = new Gson();
        String payload;
        List<CustomerDetails> customerDetailsList = requestObj.getCustomerDetailsList();
        for (CustomerDetails customerDetails : customerDetailsList) {
            customerDetails.setApplicationId(applicationID);
            customerDetails.setAppId(requestObj.getAppId());
            customerDetails.setVersionNum(version);
            payload = gson.toJson(customerDetails.getPayload());
            customerDetails.setPayloadColumn(payload);
            customerDetails.setCustDtlId(custDtlId);
            customerDetails.setSeqNumber(requestObj.getApplicationMaster().getCustDtlSlNum());
            custDtlRepo.save(customerDetails);
        }
        customerIdentification.setCustDtlId(custDtlId.toString());//to String is required to avoid rounding issue of Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_CUSTOMER_DETAILS for credit card");

    }

    private void updateCustomerDtlInMaster(ApplyCreditCardRequestFields requestObj, int version, String applicationID, CustomerIdentificationCards customerIdentification) {
        Optional<ApplicationMaster> masterObjDb = appMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(), requestObj.getApplicationId(), version, AppStatus.INPROGRESS.getValue());
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
            appMasterRepo.save(masterObj);
        }
    }
    
    private void populateAppMasterAndApplnwf(ApplyCreditCardRequestFields requestObj, String applicationID, int version,
                                   CustomerIdentificationCards customerIdentification, Properties prop, BigDecimal custDtlId, boolean isSelfOnBoardingHeaderAppId) {
        ApplicationMaster appMasterReq = requestObj.getApplicationMaster();
        ApplicationMaster appMaster = new ApplicationMaster();
        appMaster.setAppId(requestObj.getAppId());
        appMaster.setApplicationDate(LocalDate.now());
        appMaster.setApplicationId(applicationID);
        appMaster.setApplicationStatus(AppStatus.INPROGRESS.getValue());
        if("Y".equalsIgnoreCase(requestObj.getIsExistingCustomer())) {
			appMaster.setApplicationType(Constants.ETB);
		}else if("N".equalsIgnoreCase(requestObj.getIsExistingCustomer())) {
			appMaster.setApplicationType(Constants.NTB);
		}
        appMaster.setCreatedBy(appMasterReq.getCreatedBy());
        appMaster.setEmailId(appMasterReq.getEmailId());
        appMaster.setMobileNumber(appMasterReq.getMobileNumber());
        appMaster.setProductCode(appMasterReq.getProductCode()); //set this to null because user might not be eligible for this product group code. we will update this column when user selects one of the eligible cards in next screens.
        appMaster.setProductGroupCode(appMasterReq.getProductGroupCode());
        appMaster.setVersionNum(version);
        appMaster.setCurrentScreenId(appMasterReq.getCurrentScreenId().split("~")[0]);
        appMaster.setCustomerId(appMasterReq.getCustomerId());
        appMaster.setSearchCode1(prop.getProperty(CobFlagsProperties.DEFAULT_BRANCH_CARDS.getKey()));
        appMaster.setApplicantsCount(appMasterReq.getApplicantsCount());
        if (!(CommonUtils.isNullOrEmpty(appMasterReq.getMobileNumber()))) {
            appMaster.setMobileVerStatus("Y");
        }
        if (!(CommonUtils.isNullOrEmpty(appMasterReq.getEmailId()))) {
            appMaster.setEmailVerStatus("Y");
        }
        appMasterRepo.save(appMaster);
        customerIdentification.setCustDtlId(custDtlId.toString());//to String is required to avoid rounding issue of Big Decimal at front end.
		customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_APPLICATION_MASTER");
        if (!isSelfOnBoardingHeaderAppId || ("N".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.CARD_STP.getKey())))) {
            WorkFlowDetails wfObj = requestObj.getWorkflow();
            PopulateapplnWFRequest apiRequest = new PopulateapplnWFRequest();
            PopulateapplnWFRequestFields requestObj1 = new PopulateapplnWFRequestFields();
            requestObj1.setAppId(requestObj.getAppId());
            requestObj1.setApplicationId(applicationID);
            requestObj1.setApplicationStatus(AppStatus.INPROGRESS.getValue());
            if (!isSelfOnBoardingHeaderAppId) {
            	requestObj1.setCreatedBy(appMasterReq.getCreatedBy());	
            }
            else {
            	requestObj1.setCreatedBy(Constants.CUSTOMER);
            }
            requestObj1.setVersionNum(version);
            requestObj1.setWorkflow(wfObj);
            apiRequest.setRequestObj(requestObj1);
            commonService.populateApplnWorkFlow(apiRequest);
            logger.warn("Data inserted into TB_ABOB_APPLN_WORKFLOW");
            Optional<ApplicationWorkflow> workflow = applnWfRepository.findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(requestObj.getAppId(), applicationID, version);
            if (workflow.isPresent()) {
                ApplicationWorkflow applnWf = workflow.get();
                List<WorkflowDefinition> wfDefnList = wfDefnRepoCc.findByFromStageId(applnWf.getNextWorkFlowStage());
                customerIdentification.setApplnWfDefinitionList(wfDefnList);
            }
        }
    }

    private void populateApplicationDocs(ApplyCreditCardRequestFields customerDataFields, CustomerIdentificationCards customerIdentification, String applicationID, BigDecimal custDtlId, int version) {
        Gson gson = new Gson();
        List<String> documentList = new ArrayList<>();
        List<ApplicationDocumentsWrapper> applicationDocumentsWrapperList = customerDataFields.getApplicationDocumentsWrapperList();
        for (ApplicationDocumentsWrapper applicationDocumentsWrapper : applicationDocumentsWrapperList) {
            List<ApplicationDocuments> applicationDocumentsList = applicationDocumentsWrapper.getApplicationDocumentsList();
            for (ApplicationDocuments applicationDocuments : applicationDocumentsList) {
                if (applicationDocuments.getAppDocId() == null) {//This is to handle the case if user changed the data after its being inserted by using the back navigation within the session.
                    BigDecimal appDocId = CommonUtils.generateRandomNum();
                    applicationDocuments.setAppDocId(appDocId);
                    documentList.add(appDocId.toString());//to String is required to avoid rounding issue of Big Decimal at front end.
                }
                applicationDocuments.setApplicationId(applicationID);
                applicationDocuments.setCustDtlId(custDtlId);
                applicationDocuments.setVersionNum(version);
                applicationDocuments.setAppId(customerDataFields.getAppId());
                String payload = gson.toJson(applicationDocuments.getPayload());
                applicationDocuments.setPayloadColumn(payload);
                applicationDocuments.setStatus(AppStatus.ACTIVE_STATUS.getValue());
                appDocRepo.save(applicationDocuments);
            }
        }
        customerIdentification.setDocumentList(documentList);
        customerIdentification.setCustDtlId(custDtlId.toString());//to String is required to avoid rounding issue of Big Decimal at front end.
        customerIdentification.setApplicationId(applicationID);
        customerIdentification.setVersionNum(version);
        logger.warn("Data inserted into TB_ABOB_APPLN_DOCUMENTS for credit cards");
    }

    public Mono<Response> checkApplication(CheckApplicationRequest request, Header header) throws IOException {
        Gson gson = new Gson();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        Properties prop = CommonUtils.readPropertyFile();
        if ("Y".equalsIgnoreCase(prop.getProperty(CobFlagsProperties.EXT_SYSTEM_DEDUPE_REQUIRED.getKey()))) {
            //dedupe check hook.
            Mono<Object> monoResponse = interfaceAdapter.callExternalService(header, request, request.getInterfaceName());
            return monoResponse.flatMap(val -> {
            	Response response = new Response();
            	ResponseWrapper res = adapterUtil.getResponseMapper(val, request.getInterfaceName(), header);
            	if (ResponseParser.isExtCallSuccess(res.getApiResponse(), "checkApplication")) {
                    if (ResponseParser.isNewCustomer(res.getApiResponse())) {
                    	response=checkApplication(request, responseHeader, prop, responseBody);
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
        	Response response=checkApplication(request, responseHeader, prop, responseBody);
        	return Mono.just(response);
        } else {
        	return Mono.empty();
        }
    }

    private void populateHistoryTables(String applicationId, String appId) {
        List<ApplicationMaster> appMasterDb = appMasterRepo.findByAppIdAndApplicationId(appId, applicationId);
        if (null!=appMasterDb && appMasterDb.size()>0){
        	for(ApplicationMaster appMaster:appMasterDb) {
        		ApplicationMasterHistory appMasterHistory = new ApplicationMasterHistory();
                BeanUtils.copyProperties(appMaster, appMasterHistory);
                appMasterHisRepo.save(appMasterHistory);
                appMasterRepo.deleteByApplicationIdAndAppId(applicationId, appId);	
        	}
        }

        ApplicationDocumentsHistory documentHistory;
        List<ApplicationDocuments> documentList = appDocRepo.findByApplicationIdAndAppId(applicationId, appId);
        for (ApplicationDocuments documentObj : documentList) {
            documentHistory = new ApplicationDocumentsHistory();
            BeanUtils.copyProperties(documentObj, documentHistory);
            appDocHisRepo.save(documentHistory);
        }
        appDocRepo.deleteByApplicationIdAndAppId(applicationId, appId);

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
        List<OccupationDetails> occupationList = occupationDtlRepo.findByApplicationIdAndAppId(applicationId, appId);
        for (OccupationDetails ocupationObj : occupationList) {
            occupationHistory = new OccupationDetailsHistory();
            BeanUtils.copyProperties(ocupationObj, occupationHistory);
            occupationDtlHisRepo.save(occupationHistory);
        }
        occupationDtlRepo.deleteByApplicationIdAndAppId(applicationId, appId);

        CardDetailsHistory cardsHistory;
        List<CardDetails> cardsList = cardDtlRepo.findByApplicationIdAndAppId(applicationId, appId);
        for (CardDetails card : cardsList) {
            cardsHistory = new CardDetailsHistory();
            BeanUtils.copyProperties(card, cardsHistory);
            cardDtlHisRepo.save(cardsHistory);
        }
        cardDtlRepo.deleteByApplicationIdAndAppId(applicationId, appId);
    }

    private void deleteApplication(String applicationId, String appId) {
        appMasterRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        appDocRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        custDtlRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        addressDtlRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        occupationDtlRepo.deleteByApplicationIdAndAppId(applicationId, appId);
        cardDtlRepo.deleteByApplicationIdAndAppId(applicationId, appId);
    }

    public Response fetchApplication(FetchAppReq request) {
        String applicationId = request.getRequestObj().getApplicationId();
        String appId = request.getRequestObj().getAppId();
        int versionNum = request.getRequestObj().getVersionNum();
        Gson gson = new Gson();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        response.setResponseHeader(responseHeader);
        ResponseBody responseBody = new ResponseBody();
        Optional<ApplicationMaster> applicationMasterOpt = appMasterRepo.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, applicationId);
        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            ApplyCreditCardRequestFields customerDataFields = getCustomerData(applicationMasterData, applicationId, appId, versionNum);
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

    
    public Response checkApplication(CheckApplicationRequest request, ResponseHeader responseHeader, Properties prop, ResponseBody responseBody) {
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
        String lastElementArr="";
        CheckAppCCRequestFields requestFields = request.getRequestObj();
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
        List<ApplicationMaster> appMasterObj = appMasterRepo.findData(requestFields.getAppId(), mobileNum, nationalId, pan, emailId, productGroupCode, statusList, customerId == null ? null : new BigDecimal(customerId));
        boolean iv108 = false;
        boolean iv115 = false;
        boolean iv109 = false;
        for (ApplicationMaster appMasterObjDb : appMasterObj) {
            String headerAppId = request.getAppId();
            JSONArray array = null;
            if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
                if ("Y".equalsIgnoreCase(requestFields.getIsExistingCustomer())) {
                    array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.CARD_ETB.getKey(), Constants.FUNCTIONSEQUENCE);
                } else if ("N".equalsIgnoreCase(requestFields.getIsExistingCustomer())) {
                    array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.CARD_NTB.getKey(), Constants.FUNCTIONSEQUENCE);
                }
            } else {
                if ("Y".equalsIgnoreCase(requestFields.getIsExistingCustomer())) {
                    array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.CARD_BO_ETB.getKey(), Constants.FUNCTIONSEQUENCE);
                } else if ("N".equalsIgnoreCase(requestFields.getIsExistingCustomer())) {
                    array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.CARD_BO_NTB.getKey(), Constants.FUNCTIONSEQUENCE);
                }
            }
            if(null!=array) {
            	lastElementArr  = ((String) array.get(array.length() - 1)).split("~")[0];	
            }
            String currentSrnId = appMasterObjDb.getCurrentScreenId();
            if (appMasterObjDb != null && AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObjDb.getApplicationStatus())) {
                iv109 = true;
            } else if (appMasterObjDb != null && AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObjDb.getApplicationStatus()) && lastElementArr.equalsIgnoreCase(currentSrnId)) {
                res = appMasterObjDb.getApplicationId() + "~" + appMasterObjDb.getAppId() + "~" + appMasterObjDb.getVersionNum() + "~" + appMasterObjDb.getApplicationStatus() + "~" + appMasterObjDb.getRelatedApplicationId() + "~" + appMasterObjDb.getProductGroupCode() + "~" + appMasterObjDb.getProductCode();
                inprogress.add(res);
                iv115 = true; // All stages are done but still in inprogress status so dont allow to proceed. IV115
            } else {
                String allowPartialApplication = prop.getProperty(CobFlagsProperties.ALLOW_PARTIAL_APPLICATION.getKey());
                if ("Y".equalsIgnoreCase(allowPartialApplication) && appMasterObjDb != null) {
                    res = appMasterObjDb.getApplicationId() + "~" + appMasterObjDb.getAppId() + "~" + appMasterObjDb.getVersionNum() + "~" + appMasterObjDb.getApplicationStatus() + "~" + appMasterObjDb.getRelatedApplicationId() + "~" + appMasterObjDb.getProductGroupCode() + "~" + appMasterObjDb.getProductCode();
                    inprogress.add(res);
                    if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObjDb.getApplicationStatus())) {
                        iv108 = true;
                    }
                } else if ("N".equalsIgnoreCase(allowPartialApplication)) {
                	String deleteRule=prop.getProperty(CobFlagsProperties.CARDS_DELETE_RULE.getKey());
                    if (Constants.HARD_DELETE.equalsIgnoreCase(deleteRule) && appMasterObjDb != null) {
                        deleteApplication(appMasterObjDb.getApplicationId(), appMasterObjDb.getAppId());
                    } else if (Constants.MOVE_TO_HISTORY_TABLES.equalsIgnoreCase(deleteRule)) {
                        populateHistoryTables(appMasterObjDb.getApplicationId(), appMasterObjDb.getAppId());
                    }
                    else if (Constants.UPDATE_STATUS.equalsIgnoreCase(deleteRule)) {
                    	appMasterObjDb.setApplicationStatus(AppStatus.DELETED.getValue());
                        appMasterRepo.save(appMasterObjDb);
                    } else {
                    	responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey()); 
                    }
                }
            }
        }
        resElements.setInProgress(inprogress);
        responseBody.setResponseObj(gson.toJson(resElements));
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
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }
    
    
    
    public Mono<Object> fetchCustomerDetails(FetchCustDtlReq request, Header header) {
        return interfaceAdapter.callExternalService(header, request, request.getInterfaceName());
    }

    public Mono<Object> fetchEligibleCards(FetchEligibleCardsReq request, Header header) {
        return interfaceAdapter.callExternalService(header, request, request.getInterfaceName());
    }

    public boolean discardApplication(ApplyCreditCardRequest req) throws IOException {
        ApplyCreditCardRequestFields requestFields = req.getRequestObj();
        String deleteRule;
        Optional<ApplicationMaster> applicationMasterOpt = appMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestFields.getAppId(), requestFields.getApplicationId(), requestFields.getVersionNum(), AppStatus.INPROGRESS.getValue());
        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster masterObjDb = applicationMasterOpt.get();
            Properties prop = CommonUtils.readPropertyFile();
            deleteRule=prop.getProperty(CobFlagsProperties.CARDS_DELETE_RULE.getKey());
            if (Constants.HARD_DELETE.equalsIgnoreCase(deleteRule)) {
                deleteApplication(requestFields.getApplicationId(), requestFields.getAppId());
            } else if (Constants.MOVE_TO_HISTORY_TABLES.equalsIgnoreCase(deleteRule)) {
            	populateHistoryTables(requestFields.getApplicationId(), requestFields.getAppId());
            } else if (Constants.UPDATE_STATUS.equalsIgnoreCase(deleteRule)) {
            	masterObjDb.setApplicationStatus(AppStatus.DELETED.getValue());
                appMasterRepo.save(masterObjDb);
            } else {
            	return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public Response updateCommonCode(UpdateCommonCodeRequest req) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        response.setResponseHeader(responseHeader);
        ResponseBody responseBody = new ResponseBody();
        responseBody.setResponseObj("");
        response.setResponseBody(responseBody);
        TbAbmiCommonCodeDomain commonCodeObj = commonService.fetchCommonCode(Constants.CARD_SERVICE);
        JSONObject jsonObj = new JSONObject(commonCodeObj.getCodeDesc());
        JSONArray cardImgArr = jsonObj.getJSONArray(Constants.CARD_IMAGES);
        List<String> cardImages = req.getRequestObj().getCardImages();
        for (int i = 0; i < cardImgArr.length(); i++) {
            cardImgArr.put(i, cardImages.get(i));
        }
        jsonObj.put(Constants.DEFAULT_CARD, req.getRequestObj().getDefaultImage());
        jsonObj.put(Constants.CARD_IMAGES, cardImgArr);
        commonCodeObj.setCodeDesc(jsonObj.toString());
        commonService.saveCommonCode(commonCodeObj);
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        return response;
    }


    public boolean isValidStage(ApplyCreditCardRequest request, boolean isSelfOnBoardingHeaderAppId, JSONArray array) {
    	String currentScrIdFromDb = null;
        String prevElement = "";
        ApplyCreditCardRequestFields requestObj = request.getRequestObj();
        ApplicationMaster applicationMaster = requestObj.getApplicationMaster();
        String[] currentScreenIdArray = applicationMaster.getCurrentScreenId().split("~");
        boolean flag=false;
        if ("N".equalsIgnoreCase(currentScreenIdArray[1])) { //back navigation flow
            flag= true;
        }
        else if (array != null) {
            String firstElementArr = ((String) array.get(0)).split("~")[0];
            String secondElementArr = ((String) array.get(1)).split("~")[0];
            String thirdElementArr = ((String) array.get(2)).split("~")[0];
            String currenScrnIdReq = currentScreenIdArray[0];
            Optional<ApplicationMaster> masterObjDb = appMasterRepo.findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(requestObj.getAppId(), requestObj.getApplicationId(), requestObj.getVersionNum(), AppStatus.INPROGRESS.getValue());
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
            	 if (Constants.SELECT_PRODUCT.equalsIgnoreCase(firstElementArr) || Constants.VALIDATE_PINCODE.equalsIgnoreCase(firstElementArr)) {
            		 if((secondElementArr.equalsIgnoreCase(Constants.SELECT_PRODUCT) || secondElementArr.equalsIgnoreCase(Constants.VALIDATE_PINCODE)) && thirdElementArr.equalsIgnoreCase(currenScrnIdReq)) {
                        flag= true;
                     }
                    if (secondElementArr.equalsIgnoreCase(currenScrnIdReq)) {
                        flag= true;
                    }
                }
                if ((!(isSelfOnBoardingHeaderAppId)) && requestObj.getVersionNum() > 1 && Constants.CUST_VERIFICATION.equalsIgnoreCase(prevElement)) {  // required for version>1 case. During reject modify flow.
                    flag= true;
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
                                          flag= true;
                                      }
                              	}
                              	if (thirdNextArrayElement.split("~")[0].equalsIgnoreCase(currenScrnIdReq)) {
                              		flag= true;
                                  }
                          	}
                              if (secondNextArrayElement.split("~")[0].equalsIgnoreCase(currenScrnIdReq)) {
                            	  flag= true;
                              }
                          } else {
                              if (nextArrayElement.split("~")[0].equalsIgnoreCase(currenScrnIdReq)) {
                            	  flag= true;
                              }
                          }
                      }
                  }
                
            }
        }
        return flag;
    }


    public Response deleteFile(DeleteFileRequest req) throws IOException {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        responseBody.setResponseObj("");
        response.setResponseBody(responseBody);
        Properties prop = CommonUtils.readPropertyFile();
        String file = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + prop.getProperty(CobFlagsProperties.DEFAULT_CARD_LOCATION.getKey()) + "/" + req.getRequestObj().getFileName();
        Path path = Paths.get(file);
        if (Files.deleteIfExists(path)) {
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        } else {
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
        }
        response.setResponseHeader(responseHeader);
        return response;
    }


    public boolean isVaptPassedForScreenElements(ApplyCreditCardRequest apiRequest, JSONArray array) {
    	boolean flag=false;
		if (array == null) {
			flag= false;
		} else {
			JSONArray stageArray = null;
			ApplicationMaster appMasterReq = apiRequest.getRequestObj().getApplicationMaster();
			String[] currentStage = appMasterReq.getCurrentScreenId().split("~");
			for (Object element : array) {
				String stage = ((String) element).split("~")[0];
				if (stage.equalsIgnoreCase(Constants.SELECT_PRODUCT)
						|| stage.equalsIgnoreCase(Constants.TERMS_AND_CONDITIONS)
						|| stage.equalsIgnoreCase(Constants.CONFIRMATION)
						|| stage.equalsIgnoreCase(Constants.KYC_VERIFICATION)
						|| stage.equalsIgnoreCase(Constants.ELIGIBLE_CARDS)
						|| stage.equalsIgnoreCase(Constants.UPLOAD_DOCUMENTS)
						|| stage.equalsIgnoreCase(Constants.VALIDATE_PINCODE) 
						|| stage.equalsIgnoreCase(Constants.ACCOUNT_CREATION)) {
					continue;
				}
				if (currentStage[0].equalsIgnoreCase(Constants.SELECT_PRODUCT)
						|| currentStage[0].equalsIgnoreCase(Constants.TERMS_AND_CONDITIONS)
						|| currentStage[0].equalsIgnoreCase(Constants.CONFIRMATION)
						|| currentStage[0].equalsIgnoreCase(Constants.KYC_VERIFICATION)
						|| currentStage[0].equalsIgnoreCase(Constants.ELIGIBLE_CARDS)
						|| currentStage[0].equalsIgnoreCase(Constants.UPLOAD_DOCUMENTS) 
						|| currentStage[0].equalsIgnoreCase(Constants.VALIDATE_PINCODE)
						|| currentStage[0].equalsIgnoreCase(Constants.ACCOUNT_CREATION)) {
					flag= true;
				}
				if (stage.equalsIgnoreCase(Constants.CUST_VERIFICATION)) {
					if ("Y".equalsIgnoreCase(apiRequest.getRequestObj().getIsExistingCustomer())) {
						stageArray = commonService.getJsonArrayForCmCodeAndKey(stage, CodeTypes.CARD_ETB.getKey(), stage);
					} else if ("N".equalsIgnoreCase(apiRequest.getRequestObj().getIsExistingCustomer())) {
						stageArray = commonService.getJsonArrayForCmCodeAndKey(stage, CodeTypes.CARD_NTB.getKey(), stage);
					} else {
						flag= false;
					}
				} else {
					stageArray = commonService.getJsonArrayForCmCodeAndKey(stage, Constants.COMM, stage);
				}
				if (stage.equalsIgnoreCase(Constants.CUST_VERIFICATION)
						&& currentStage[0].equalsIgnoreCase(Constants.CUST_VERIFICATION)) {
					flag=vaptForFieldsCustVerification(apiRequest, stageArray, appMasterReq);
				} else if (stage.equalsIgnoreCase(Constants.CUSTOMER_DETAILS)
						&& currentStage[0].equalsIgnoreCase(Constants.CUSTOMER_DETAILS)) {
					List<CustomerDetails> customerDetailsList = apiRequest.getRequestObj().getCustomerDetailsList();
					List<AddressDetailsWrapper> addressDetailsWrapperList = apiRequest.getRequestObj().getAddressDetailsWrapperList();
					flag= commonService.vaptForFieldsCustDtls(customerDetailsList, addressDetailsWrapperList, stageArray);
				} else if (stage.equalsIgnoreCase(Constants.OCCUPATION_DETAILS)
						&& currentStage[0].equalsIgnoreCase(Constants.OCCUPATION_DETAILS)) {
					List<OccupationDetailsWrapper> occupationDetailsWrapperList = apiRequest.getRequestObj().getOccupationDetailsWrapperList();
					List<AddressDetailsWrapper> addressDetailsWrapperList = apiRequest.getRequestObj().getAddressDetailsWrapperList();
					flag= commonService.vaptForFieldsOccupationDtls(occupationDetailsWrapperList, addressDetailsWrapperList, stageArray);
				} else if (stage.equalsIgnoreCase(Constants.CARD_SERVICE) && currentStage[0].equalsIgnoreCase(Constants.CARD_SERVICE)) {
					CardDetails cardDtls = apiRequest.getRequestObj().getCardDetails();
					flag= vaptForFieldsCards(cardDtls, stageArray);
				}
			}
		}
		return flag;
	}

    private boolean vaptForFieldsCustVerification(ApplyCreditCardRequest apiRequest, JSONArray stageArray, ApplicationMaster appMasterReq) {
    	if ("Y".equalsIgnoreCase(apiRequest.getRequestObj().getIsExistingCustomer())) { // CUSTVERIFICATIONCDEC
			return commonService.vaptForFieldsCustVerificationCards(appMasterReq, stageArray);
		} else if ("N".equalsIgnoreCase(apiRequest.getRequestObj().getIsExistingCustomer())) { // CUSTVERIFICATIONCD
			return commonService.vaptForFieldsCustVerificationCasa(appMasterReq, stageArray);
		} else {
			return false;
		}
	}

	private boolean vaptForFieldsCards(CardDetails cardDtls, JSONArray stageArray ) {
    	String fieldName;
    	boolean isValid=true;
    	for (Object screenElement : stageArray) {
    		fieldName = ((String) screenElement).split("~")[0];
    		if ("NameOnCard".equalsIgnoreCase(fieldName)) {
    			isValid = commonService.isValidFieldvalue((String) screenElement, cardDtls.getNameOnCard());
			} else if ("CardStatement".equalsIgnoreCase(fieldName)) {
				boolean emailStValid = commonService.isValidFieldvalue((String) screenElement, cardDtls.getEmailStmtReq());
				boolean physicalStValid = commonService.isValidFieldvalue((String) screenElement, cardDtls.getPhysicalStmtReq());
				isValid=emailStValid || physicalStValid;
			}
    		if (!isValid) {
     			return false;
     		}
    	}
    	return true;
	}

	public Response downloadApplication(FetchAppReq fetchAppReq) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        String applicationId = fetchAppReq.getRequestObj().getApplicationId();
        String appId = fetchAppReq.getRequestObj().getAppId();
        int versionNum = fetchAppReq.getRequestObj().getVersionNum();
        Optional<ApplicationMaster> applicationMasterOpt = appMasterRepo.findTopByAppIdAndApplicationIdOrderByVersionNumDesc(appId, applicationId);
        if (applicationMasterOpt.isPresent()) {
            ApplicationMaster applicationMasterData = applicationMasterOpt.get();
            ApplyCreditCardRequestFields customerDataFields = getCustomerData(applicationMasterData, applicationId, appId, versionNum);
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

    private ApplyCreditCardRequestFields getCustomerData(ApplicationMaster applicationMasterData, String applicationId, String appId, int versionNum) {
        ApplyCreditCardRequestFields customerDataFields = new ApplyCreditCardRequestFields();
        customerDataFields.setAppId(applicationMasterData.getAppId());
        customerDataFields.setApplicationId(applicationMasterData.getApplicationId());
        customerDataFields.setApplicationMaster(applicationMasterData);
        customerDataFields.setVersionNum(applicationMasterData.getVersionNum());

        AddressDetailsWrapper addressDetailsWrapper = new AddressDetailsWrapper();
        List<AddressDetailsWrapper> addressDetailsWrapperList = new ArrayList<>();
        List<AddressDetails> addressDetailsList = addressDtlRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        addressDetailsWrapper.setAddressDetailsList(addressDetailsList);
        addressDetailsWrapperList.add(addressDetailsWrapper);
        customerDataFields.setAddressDetailsWrapperList(addressDetailsWrapperList);

        CardDetails cardDetails = cardDtlRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        customerDataFields.setCardDetails(cardDetails);

        List<CustomerDetails> customerDetailsList = custDtlRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        customerDataFields.setCustomerDetailsList(customerDetailsList);

        List<OccupationDetailsWrapper> occupationDetailsWrapperList = new ArrayList<>();
        OccupationDetailsWrapper occupationDetailsWrapper;
        List<OccupationDetails> occupationDetailsList = occupationDtlRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, versionNum);
        for (OccupationDetails occupationDetails : occupationDetailsList) {
            occupationDetailsWrapper = new OccupationDetailsWrapper();
            occupationDetailsWrapper.setOccupationDetails(occupationDetails);
            occupationDetailsWrapperList.add(occupationDetailsWrapper);
        }
        customerDataFields.setOccupationDetailsWrapperList(occupationDetailsWrapperList);

        ApplicationDocumentsWrapper applicationDocumentsWrapper = new ApplicationDocumentsWrapper();
        List<ApplicationDocumentsWrapper> applicationDocumentsWrapperList = new ArrayList<>();
        List<ApplicationDocuments> applicationDocumentsList = appDocRepo.findByApplicationIdAndAppIdAndVersionNumAndStatus(applicationId, appId, versionNum, AppStatus.ACTIVE_STATUS.getValue());
        applicationDocumentsWrapper.setApplicationDocumentsList(applicationDocumentsList);
        applicationDocumentsWrapperList.add(applicationDocumentsWrapper);
        customerDataFields.setApplicationDocumentsWrapperList(applicationDocumentsWrapperList);
       
        Optional<ApplicationWorkflow> workflow= applnWfRepository.findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(appId, applicationId, versionNum);
        
        if (workflow.isPresent()) {
        	ApplicationWorkflow applnWf = workflow.get();
            List<WorkflowDefinition> wfDefnLis = wfDefnRepoCc.findByFromStageId(applnWf.getNextWorkFlowStage());
            customerDataFields.setApplnWfDefinitionList(wfDefnLis);
        }
        
        customerDataFields.setApplicationTimelineDtl(commonService.getApplicationTimelineDtl(applicationMasterData.getApplicationId()));
        
        return customerDataFields;
    }
    
    public void duplicateCardsTables(ApplicationMaster appMaster, int newVersionNum, String applicationId, String appId, int oldVersionNum) {
		BigDecimal newCustDtlId;
		ApplicationDocuments docNewObj=null;
		commonService.duplicateMasterData(appMaster, newVersionNum);
		List<CustomerDetails> custList = custDtlRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, oldVersionNum);
		List<OccupationDetails> occupationDetailsList = occupationDtlRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, oldVersionNum);
		BigDecimal oldCustDtlId;
		for (CustomerDetails custObj : custList) {
			oldCustDtlId = custObj.getCustDtlId();
			newCustDtlId = CommonUtils.generateRandomNum();
			commonService.duplicateCustomerData(custObj, newVersionNum, newCustDtlId);
			for(OccupationDetails occupationObj:occupationDetailsList) {
	       		commonService.duplicateOccupationData(occupationObj, newVersionNum, newCustDtlId);
	       	}
			duplicateCardData(applicationId, appId, oldVersionNum, newVersionNum);
			List<ApplicationDocuments> applicationDocumentsList = appDocRepo.findByApplicationIdAndAppIdAndVersionNumAndStatusAndCustDtlId(applicationId, appId, oldVersionNum, AppStatus.ACTIVE_STATUS.getValue(), oldCustDtlId);
	        for (ApplicationDocuments docObj : applicationDocumentsList) {
	        	commonService.duplicateDocsData(docNewObj, docObj, newVersionNum, newCustDtlId);
	        }
		}
	}


	private void duplicateCardData(String applicationId, String appId, int oldVersionNum, int newVersionNum) {
		CardDetails cardDetails = cardDtlRepo.findByApplicationIdAndAppIdAndVersionNum(applicationId, appId, oldVersionNum);
		if(null!=cardDetails) {
			CardDetails cardDetailsNew=new CardDetails();
			BeanUtils.copyProperties(cardDetails, cardDetailsNew);
			cardDetailsNew.setVersionNum(newVersionNum);
			cardDetailsNew.setCcDtlId(CommonUtils.generateRandomNum());
			cardDtlRepo.save(cardDetailsNew);
		}
	}

	public JSONArray fetchFunctionSeqArray(ApplyCreditCardRequest apiRequest, boolean isSelfOnBoardingHeaderAppId) {
		JSONArray array=null;
		ApplyCreditCardRequestFields requestObj = apiRequest.getRequestObj();
		if (isSelfOnBoardingHeaderAppId) {
            if ("N".equalsIgnoreCase(requestObj.getIsExistingCustomer())) {
                array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.CARD_NTB.getKey(), Constants.FUNCTIONSEQUENCE);
            } else if ("Y".equalsIgnoreCase(requestObj.getIsExistingCustomer())) {
                array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.CARD_ETB.getKey(), Constants.FUNCTIONSEQUENCE);
            }
        } else {
            if ("N".equalsIgnoreCase(requestObj.getIsExistingCustomer())) {
                array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.CARD_BO_NTB.getKey(), Constants.FUNCTIONSEQUENCE);
            } else if ("Y".equalsIgnoreCase(requestObj.getIsExistingCustomer())) {
                array = commonService.getJsonArrayForCmCodeAndKey(Constants.FUNCTIONSEQUENCE, CodeTypes.CARD_BO_ETB.getKey(), Constants.FUNCTIONSEQUENCE);
            }
        }
		return array;
	}
}
