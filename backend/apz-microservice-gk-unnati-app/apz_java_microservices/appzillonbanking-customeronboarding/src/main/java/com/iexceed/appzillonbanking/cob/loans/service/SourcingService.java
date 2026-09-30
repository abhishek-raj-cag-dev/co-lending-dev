package com.iexceed.appzillonbanking.cob.loans.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.core.repository.ab.*;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.core.utils.AppStatus;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.loans.payload.ApplyLoanRequest;
import com.iexceed.appzillonbanking.cob.loans.payload.ApplyLoanRequestFields;
import com.iexceed.appzillonbanking.cob.loans.payload.BCMPIIncomeDetailsWrapper;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;
import com.iexceed.appzillonbanking.cob.service.COBService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SourcingService {
    private static final Logger logger = LogManager.getLogger(SourcingService.class);

    @Autowired
    BCMPIService bcmpiService;

    @Autowired
    LoanService loanService;

    @Autowired
    private CustomerDetailsRepository custDtlRepo;

    @Autowired
    private CommonParamService commonParamService;

    @Autowired
    private COBService cobService;

    @Autowired
    private ApplicationMasterRepository applicationMasterRepo;

    @Autowired
    private LoanDtlsRepo loanDtlsRepo;

    @Autowired
    private AddressDetailsRepository addressDtlRepo;

    @Autowired
    private SourcingStageVerificationRepository srcStageVerificationRepo;

    @Autowired
    private AMLQuestionnaireRepository amlQuestionnaireRepo;

    @Autowired
    private AMLQuestionnaireRepository amlQuestionnaireRepository;

    @Autowired
    private ApplicationMasterRepository appMasterRepo;

    @Autowired
    private ApplicationMasterRepository2 appMasterRepo2;

    @Autowired
    private BCMPIIncomeDetailsRepository bcmpiIncomeDetailsRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private boolean fetchOrCreateAppMaster(ApplyLoanRequestFields requestObj, CustomerIdentificationLoan customerIdentification, Properties prop){
        ApplicationMaster applicationMaster = requestObj.getApplicationMaster();
        Optional<ApplicationMaster> masterData = applicationMasterRepo.findByAppIdAndWorkitemNo(requestObj.getAppId(),
            applicationMaster.getWorkitemNo());
        if (masterData.isPresent()){
            String applicationID = masterData.get().getApplicationId();
            Optional<ApplicationMaster> appMasterForVersionCheck = applicationMasterRepo
                    .findTopByAppIdAndApplicationIdOrderByVersionNumDesc(requestObj.getAppId(), applicationID);
            if (appMasterForVersionCheck.isPresent()) {
                ApplicationMaster appMaster = appMasterForVersionCheck.get();
                customerIdentification.setApplicationId(applicationID);
                customerIdentification.setRelatedApplicationId(appMaster.getRelatedApplicationId());
                customerIdentification.setVersionNum(appMaster.getVersionNum());
            }
        } else {
            String applicationID = CommonUtils.generateRandomNumStr();
            customerIdentification.setVersionNum(Constants.INITIAL_VERSION_NO);
            customerIdentification.setApplicationId(applicationID);
            loanService.processLoanSourcing("createApp", requestObj , customerIdentification, false, prop);
        }
        return masterData.isPresent();
    }

    public Mono<Response> nextSourcingStage(Map<String, String> hm2, ApplyLoanRequest loanRequest, Properties prop) {

        logger.debug("Inside nextSourcingStage");
        //Using it to pass version application Id and other values between methods or services
        CustomerIdentificationLoan customerIdentification = new CustomerIdentificationLoan();
        ApplyLoanRequestFields requestFields = loanRequest.getRequestObj();

        boolean isMasterPresent = false;


        logger.debug("Product Switch in Request" + requestFields.getProductSwitch());

        boolean isProductSwitch = "Y".equalsIgnoreCase(requestFields.getProductSwitch());

        logger.debug("Product Switch" + isProductSwitch );

        String productCode = requestFields.getApplicationMaster().getProductCode();
        logger.debug("productCode" + productCode);

        String workitemNo = requestFields.getApplicationMaster().getWorkitemNo();
        logger.debug("workitemNo" + workitemNo);

        String customerId = requestFields.getApplicationMaster().getSearchCode2();

        //todo : to be removed if confirmation given
        List<String> dedupeExcludedStatusList = new ArrayList<>();
//        dedupeExcludedStatusList.add(AppStatus.REJECTED.getValue());
        dedupeExcludedStatusList.add(AppStatus.EXIT.getValue());
        dedupeExcludedStatusList.add(AppStatus.DISBURSED.getValue());
        dedupeExcludedStatusList.add(AppStatus.LUC.getValue());
        dedupeExcludedStatusList.add(AppStatus.PENDINGLUCVERIFICATION.getValue());
        dedupeExcludedStatusList.add(AppStatus.LUCVERIFIED.getValue());

        if(isProductSwitch){
            String applicationId = requestFields.getApplicationId();
            logger.debug("Application id" + applicationId);
            Optional<ApplicationMaster> applicationMasterOpt = appMasterRepo.findByApplicationId(applicationId);
            logger.info("ApplicationMaster present: {}", applicationMasterOpt.isPresent());


            if(applicationMasterOpt.isPresent()) {
                isMasterPresent = true;
                customerIdentification.setApplicationId(applicationId);
                customerIdentification.setRelatedApplicationId(applicationMasterOpt.get().getRelatedApplicationId());
                customerIdentification.setVersionNum(applicationMasterOpt.get().getVersionNum());
                logger.debug("Master data present" + isMasterPresent);

                ApplicationMaster applicationMaster = applicationMasterOpt.get();
                logger.debug("Product code before" + applicationMaster.getProductCode());

                applicationMaster.setProductCode(productCode);
                logger.debug("Product code after" + applicationMaster.getProductCode());

                appMasterRepo.save(applicationMaster);
                logger.debug("data saved");

            }
        }
        List<String> manualRejectSourceOfApp = new ArrayList<>();
        manualRejectSourceOfApp.add(Constants.MAITRI_SOURCE);
        List<ApplicationMaster> applicationMasterList = appMasterRepo2
                    .findbyCustomerIdProductCodeAndNotInRejectStatusAndNotInPidAndBREReject(customerId, productCode, dedupeExcludedStatusList, workitemNo, manualRejectSourceOfApp);
        if(!applicationMasterList.isEmpty()){
            ResponseHeader responseHeader = new ResponseHeader();
            ResponseBody responseBody = new ResponseBody();
            Response response = new Response(responseHeader, responseBody);
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            responseBody.setResponseObj("A WIP application already exists for the selected product. Please verify.");
            return Mono.just(response);
        }

        if(requestFields.getWorkflow()!=null && Constants.BM.equalsIgnoreCase(requestFields.getWorkflow().getCurrentRole())) {
            return bmSourcingProcess(requestFields, loanRequest.getRequestType());
        }

        //Creating applicationmaster if not exist, Fetching applicationId and versionNum
        if(!isMasterPresent) {
            isMasterPresent = fetchOrCreateAppMaster(requestFields, customerIdentification, prop);
        }

        logger.debug("IsMasterPresent: {} and applicationID {}", isMasterPresent, customerIdentification.getApplicationId());

        String applicationId = customerIdentification.getApplicationId();
        ApplicationMaster applicationMaster = requestFields.getApplicationMaster();
        String screenId = applicationMaster.getCurrentScreenId();
        String custType = applicationMaster.getCustDtlSlNum()== null || applicationMaster.getCustDtlSlNum() == 1 ? Constants.APPLICANT : Constants.COAPPLICANT;

        String stageCode = getStageCode(screenId, custType);

        switch (screenId) {
            case Constants.CB_CHECK:
                updateCBCheckDetails(requestFields, custType, customerIdentification, isMasterPresent, prop, stageCode);
                break;
            case Constants.AADHAAR_DETAILS:
                updateAadhaarScreenDetails(requestFields, custType, customerIdentification);
                break;
            case Constants.BUSINESS:
                updateBusinessDetails(loanRequest, custType, customerIdentification);
                break;
            case Constants.OTHERDETAILS:
                updateOtherDetails(requestFields, loanRequest.getUserId(), customerIdentification, prop);
                break;
            case Constants.REVIEWCONFIRM:
                break;
            default:
                logger.debug("INVALID Stage ID");
        }
        if(null != requestFields.getQueryResponse()){
            loanService.processLoanSourcing("updateQuery", requestFields , customerIdentification, true, prop);
        }
        updateStageVerification(stageCode, applicationId, requestFields.getIsStageVerified(), false);
        Gson gson = new Gson();

        CustomerDataFields customerDataFields = fetchResponse(applicationMaster, applicationId, loanRequest.getAppId() , customerIdentification.getVersionNum(), false);

        String responseStr = gson.toJson(customerDataFields);
        responseStr = responseStr.replace(Constants.PAYLOAD_COLUMN, Constants.PAYLOAD);

        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        Response response = new Response(responseHeader, responseBody);
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        responseBody.setResponseObj(responseStr);
        return Mono.just(response);
    }

    //Setting All required Data for Response
    private CustomerDataFields fetchResponse(ApplicationMaster applicationMasterData, String applicationId,
                                             String appId, int versionNum, boolean isBM){
        if(Boolean.FALSE.equals(isBM)){
            Optional<ApplicationMaster> masterData = applicationMasterRepo.findByAppIdAndWorkitemNo(appId,
                    applicationMasterData.getWorkitemNo());
            if(masterData.isPresent())
            applicationMasterData = masterData.get();
        }
        CustomerDataFields customerDataFields = cobService.getCustomerData(applicationMasterData, applicationId, appId , versionNum);
        Gson gson = new Gson();

        AMLQuestionnaireDetails amlQuestionnaireDetails = amlQuestionnaireRepo.findById(applicationId).orElse(null);
        if(amlQuestionnaireDetails!= null && amlQuestionnaireDetails.getPayloadColumn() != null){
            logger.debug("Fetched AML Questionnaire Details");
            amlQuestionnaireDetails.setPayload(gson.fromJson(amlQuestionnaireDetails.getPayloadColumn(), AmlQuestionnairePayload.class));
            customerDataFields.setAmlQuestion(amlQuestionnaireDetails);
        }

        Optional<BCMPIIncomeDetails> bcmpiIncomeDataOpt = bcmpiIncomeDetailsRepo.findById(applicationId);
        if (bcmpiIncomeDataOpt.isPresent()) {
            logger.debug("bcmpiIncomeData found");
            BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper = gson.fromJson(bcmpiIncomeDataOpt.get().getPayload(), BCMPIIncomeDetailsWrapper.class); // Object to be changed to a wrapper class
            BCMPIIncomeDetails bcmpiIncomeDetails = bcmpiIncomeDataOpt.get();
            bcmpiIncomeDetails.setBcmpiIncomeDetailsWrapper(bcmpiIncomeDetailsWrapper);
            customerDataFields.setBcmpiIncomeDetails(bcmpiIncomeDetails);
        }
        return customerDataFields;
    }

    private void updateCBCheckDetails(ApplyLoanRequestFields requestObj, String custType, CustomerIdentificationLoan customerIdentification,
                                      Boolean isMasterPresent, Properties prop, String stageCode){

        BigDecimal custId = updateCustomerDetails(requestObj.getCustomerDetailsList(), custType,1, customerIdentification, requestObj.getAppId(), requestObj.getApplicationMaster().getCustDtlSlNum());
        if (custType.equalsIgnoreCase(Constants.APPLICANT)) {
            updateLoanDetails(requestObj.getLoanDetails(), customerIdentification, requestObj.getAppId());
        }
        if(custId != null){
            mergeAddressList(requestObj.getAddressDetailsWrapperList(), custId, customerIdentification, requestObj.getAppId());
        } else {
            logger.debug("Issue with updating Customer Details");
        }
        loanService.processLoanSourcing(stageCode, requestObj, customerIdentification, isMasterPresent, prop);
    }

    private void updateAadhaarScreenDetails(ApplyLoanRequestFields requestObj, String custType,
                                            CustomerIdentificationLoan customerIdentification){
        String applicationID = customerIdentification.getApplicationId();
        BigDecimal custId = updateCustomerDetails(requestObj.getCustomerDetailsList(), custType,2, customerIdentification, requestObj.getAppId(), requestObj.getApplicationMaster().getCustDtlSlNum());

        if(custId != null){
            AddressDetails mergedAddressDetails = mergeAddressList(requestObj.getAddressDetailsWrapperList(), custId, customerIdentification, requestObj.getAppId());

            if(mergedAddressDetails != null) {
                updateSameAsAddress(applicationID, mergedAddressDetails, custType, custId, requestObj.getAppId());
            }
        }
    }

    private BigDecimal updateCustomerDetails(List<CustomerDetails> customerDetailsList, String custType, int stageId, CustomerIdentificationLoan customerIdentification
            , String appId, int custDtlSlNum){
        String applicationID = customerIdentification.getApplicationId();
        int version = customerIdentification.getVersionNum();

        Optional<CustomerDetails> existCustDetails = custDtlRepo
                .findByApplicationIdAndAppIdAndCustomerType(applicationID, appId, custType);
        logger.debug("Existing customerDetails : {}", existCustDetails);
        CustomerDetails extCustDtl = existCustDetails.orElseGet(() -> {
            CustomerDetails cd = new CustomerDetails();
            logger.debug("Creating new Customer Details Record for {}, applicationId={}", custType, applicationID );
            cd.setCustDtlId(CommonUtils.generateRandomNum());
            cd.setAppId(appId);
            cd.setApplicationId(applicationID);
            cd.setCustomerType(custType);
            cd.setVersionNum(version);
            return cd;
        });
        logger.debug("Existing Object: {}", extCustDtl);
        Gson gson = new Gson();
        CustomerDetailsPayload custPayload;
        try{
            custPayload = objectMapper.readValue(extCustDtl.getPayloadColumn(),
                    CustomerDetailsPayload.class);
        } catch (Exception e){
            logger.debug("Error parsing payload So creating new Payload Obj");
            custPayload = new CustomerDetailsPayload();
        }
        CustomerDetails custDet = customerDetailsList.stream()
                .filter(c -> custType.equalsIgnoreCase(c.getCustomerType()))
                .findFirst()
                .orElse(null);
        if(custDet != null){
            CustomerDetailsPayload updatedPayload = custDet.getPayload();

            if(updatedPayload != null){
                setRequiredCustPayload(custPayload, updatedPayload, custType, stageId);

                String customerName = custDet.getCustomerName() != null ? custDet.getCustomerName() : "";
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
                custPayload.setFirstName(firstName);
                custPayload.setLastName(lastName);

                extCustDtl.setPayloadColumn(gson.toJson(custPayload));
            }

            if (stageId == 1) {
                extCustDtl.setMobileNumber(custDet.getMobileNumber());
                extCustDtl.setCustomerName(custDet.getCustomerName());
                extCustDtl.setSeqNumber(custDtlSlNum);
                extCustDtl.setCustomerId(custDet.getCustomerId());
                extCustDtl.setMemberId(custDet.getMemberId());
                if (custType.equalsIgnoreCase(Constants.COAPPLICANT)) {
                    extCustDtl.setMemberId(custDet.getMemberId());
                }
            }
            extCustDtl = custDtlRepo.save(extCustDtl);
            customerIdentification.setCustDtlId(extCustDtl.getCustDtlId().toString());
            customerIdentification.setApplicationId(applicationID);
            customerIdentification.setVersionNum(version);
            return extCustDtl.getCustDtlId();
        } else {
            logger.debug("Matching Customer Type not found in Payload");
            return null;
        }
    }

    private void setRequiredCustPayload(CustomerDetailsPayload existingCustPayload, CustomerDetailsPayload reqCustPayload, String custType, int stageId) {
        if(stageId == 1){
            existingCustPayload.setFirstName(reqCustPayload.getFirstName());
            existingCustPayload.setLastName(reqCustPayload.getLastName());
            existingCustPayload.setAlternateVoterIdValStatus(reqCustPayload.getAlternateVoterIdValStatus());
            existingCustPayload.setSecMobileNo(reqCustPayload.getSecMobileNo());
            existingCustPayload.setPrimaryKycType(reqCustPayload.getPrimaryKycType());
            existingCustPayload.setPrimaryKycId(reqCustPayload.getPrimaryKycId());
            existingCustPayload.setAlternateVoterId(reqCustPayload.getAlternateVoterId());
            existingCustPayload.setNamePerKyc(reqCustPayload.getNamePerKyc());
            existingCustPayload.setPrimaryKycIdValStatus(reqCustPayload.getPrimaryKycIdValStatus());
            existingCustPayload.setCkyc(reqCustPayload.getCkyc());
            existingCustPayload.setDob(reqCustPayload.getDob());
            existingCustPayload.setDobProof(reqCustPayload.getDobProof());
            existingCustPayload.setGender(reqCustPayload.getGender());
            existingCustPayload.setPanNumber(reqCustPayload.getPanNumber());
            existingCustPayload.setPanNumberValue(reqCustPayload.getPanNumberValue());
            existingCustPayload.setPanNumberStatus(reqCustPayload.getPanNumberStatus());
            existingCustPayload.setNamePerPan(reqCustPayload.getNamePerPan());
            existingCustPayload.setIsMobVerified(reqCustPayload.getIsMobVerified());
            existingCustPayload.setAge(reqCustPayload.getAge());
            existingCustPayload.setMaritalStatus(reqCustPayload.getMaritalStatus());
            existingCustPayload.setCustId(reqCustPayload.getCustId());
            existingCustPayload.setTitle(reqCustPayload.getTitle());
            if (custType.equalsIgnoreCase(Constants.COAPPLICANT)) {
                existingCustPayload.setRelationShipWithApplicant(reqCustPayload.getRelationShipWithApplicant());
                existingCustPayload.setCustomerIndex(reqCustPayload.getCustomerIndex());
                existingCustPayload.setIsNewCustomer(reqCustPayload.getIsNewCustomer());
            }
        } else {
            existingCustPayload.setMaritalStatus(reqCustPayload.getMaritalStatus());
            existingCustPayload.setFathersName(reqCustPayload.getFathersName());
            existingCustPayload.setSpouseName(reqCustPayload.getSpouseName());
            existingCustPayload.setEducation(reqCustPayload.getEducation());
            existingCustPayload.setReligion(reqCustPayload.getReligion());
            existingCustPayload.setCaste(reqCustPayload.getCaste());
            //Aadhaar Details
            existingCustPayload.setSecondaryKycId(reqCustPayload.getSecondaryKycId());
            existingCustPayload.setSecondaryKycDob(reqCustPayload.getSecondaryKycDob());
            existingCustPayload.setSecondaryKycName(reqCustPayload.getSecondaryKycName());
            existingCustPayload.setSecondaryKycType(reqCustPayload.getSecondaryKycType());
            if (custType.equalsIgnoreCase(Constants.COAPPLICANT)) {
                existingCustPayload.setRelationshipProof(reqCustPayload.getRelationshipProof());
            }
        }
    }

    private void updateLoanDetails( LoanDetails loanDetails, CustomerIdentificationLoan customerIdentification, String appId) {
        logger.debug("Loan Details Received: {}", loanDetails);
        String applicationID = customerIdentification.getApplicationId();
        int version = customerIdentification.getVersionNum();
        LoanDetails loanObj = loanDtlsRepo.findTopByApplicationIdAndAppId(applicationID,
                appId).orElseGet(() -> {
            LoanDetails ld = new LoanDetails();
            ld.setLoanDtlId(CommonUtils.generateRandomNum());
            ld.setApplicationId(applicationID);
            ld.setAppId(appId);
            ld.setVersionNum(version);
            return ld;
        });
        loanObj.setLoanAmount(loanDetails.getLoanAmount());
        loanObj.setRoi(loanDetails.getRoi());
        loanObj.setTenure(loanDetails.getTenure());
        LoanDetailsPayload loanObjPayload;
        LoanDetailsPayload loanDetailsPayload = loanDetails.getPayload();
        try{
            loanObjPayload = objectMapper.readValue(loanObj.getPayloadColumn(),
                    LoanDetailsPayload.class);
        } catch (Exception e){
            loanObjPayload = new LoanDetailsPayload();
        }
        if(loanDetailsPayload != null){
            loanObjPayload.setLoanPurpose(loanDetailsPayload.getLoanPurpose());
            loanObjPayload.setSubCategory(loanDetailsPayload.getSubCategory());
            loanObjPayload.setModeOfSecurity(loanDetailsPayload.getModeOfSecurity());
            loanObjPayload.setModeOfDisbursement(loanDetailsPayload.getModeOfDisbursement());
            loanObjPayload.setFrequencyOfRepayment(loanDetailsPayload.getFrequencyOfRepayment());
            loanObjPayload.setLanguage(loanDetailsPayload.getLanguage());
        }
        Gson gson = new Gson();
        loanObj.setPayloadColumn(gson.toJson(loanObjPayload));
        loanObj = loanDtlsRepo.save(loanObj);
        customerIdentification.setLoanDtlId(loanObj.getLoanDtlId().toString());
    }

    private void updateBusinessDetails (ApplyLoanRequest loanRequest, String custType, CustomerIdentificationLoan customerIdentification) {
        ApplyLoanRequestFields requestObj = loanRequest.getRequestObj();
        logger.debug("Updating Values for Business Details Screen");
        List<AddressDetailsWrapper> addressDetailsWrapperList = requestObj.getAddressDetailsWrapperList();
        String addressType = Constants.OCCUPATION;

        //Updating Occupational Address for both Applicant and Co-Applicant
        for(AddressDetailsWrapper addressDetailsWrapper: addressDetailsWrapperList){
            List<AddressDetails> addressDetailsList = addressDetailsWrapper.getAddressDetailsList().stream()
                    .filter(a -> addressType.equalsIgnoreCase(a.getAddressType())).collect(Collectors.toList());
            for(AddressDetails addressDetails: addressDetailsList) {
                updateBusinessAddress(addressDetails, customerIdentification.getVersionNum(), requestObj.getAppId());
            }
        }
        bcmpiService.updateSourcingIncome(loanRequest, custType.substring(0,1));
    }

    //Fetch Existing AddressDetails and Merging with Address Details in the Request
    private AddressDetails mergeAddressList(List<AddressDetailsWrapper> addressDetailsWrapperList, BigDecimal customerId, CustomerIdentificationLoan customerIdentification, String appId){
        Gson gson = new Gson();
        String addressType = Constants.PERSONAL;
        String applicationId = customerIdentification.getApplicationId();
        int version = customerIdentification.getVersionNum();
        for(AddressDetailsWrapper addrsWrapper: addressDetailsWrapperList) {
            List<AddressDetails> addrsDtlsList = addrsWrapper.getAddressDetailsList();
            AddressDetails addrsDtls = addrsDtlsList.stream()
                    .filter(a -> addressType.equalsIgnoreCase(a.getAddressType()) && (a.getCustDtlId() == null || customerId.compareTo(a.getCustDtlId()) == 0))
                    .findFirst()
                    .orElse(null);
            if(addrsDtls != null && addrsDtls.getPayload() != null) {

                //Fetching Existing Address Details or creating a new Address Detail
                AddressDetails existingAddressObj = addressDtlRepo
                        .findByApplicationIdAndCustDtlIdAndAddressType(applicationId, customerId,  Constants.PERSONAL)
                        .orElseGet(() -> {
                            String payloadColumn = gson.toJson(new AddressDetailsPayload());
                            return new AddressDetails(CommonUtils.generateRandomNum(), applicationId, version, appId,
                                    customerId, payloadColumn, addressType);
                        });
                try {
                    List<Address> requestAddressList = addrsDtls.getPayload().getAddressList();
                    AddressDetailsPayload existingAdrsPayload = objectMapper.readValue(existingAddressObj.getPayloadColumn(),
                            AddressDetailsPayload.class);
                    if (existingAdrsPayload == null){
                        existingAdrsPayload = new AddressDetailsPayload();
                    }
                    List<Address> mergedAddressDetails = existingAdrsPayload.getAddressList() != null ? new ArrayList<>(Stream.concat(existingAdrsPayload.getAddressList().stream(), requestAddressList.stream())
                            .collect(Collectors.toMap(
                                    Address::getAddressType,
                                    a -> a,
                                    (existing, request) -> request
                            ))
                            .values()) : requestAddressList;
                    existingAdrsPayload.setAddressList(mergedAddressDetails);
                    existingAddressObj.setPayload(existingAdrsPayload);
                    existingAddressObj.setPayloadColumn(gson.toJson(existingAdrsPayload));
                    logger.debug("{} Address Details of CustID: {}", addressType, customerId);
                    List<String> addressList = new ArrayList<>();
                    addressList.add(existingAddressObj.getAddressDtlsId().toString());
                    customerIdentification.setAddressList(addressList);
                    addressDtlRepo.save(existingAddressObj);
                    return existingAddressObj;
                } catch (Exception e) {
                    logger.debug("Error parsing payload", e);
                }
            }
        }
        logger.debug("Couldn't Save Address Details for {}", customerId);
        return null;
    }

    //Creating Map for AddressSameAsValues
    private Map<String, Address> getAddressSameAsMap(AddressDetails adrsDtls, String custType) {
        List<Address> addressList = adrsDtls.getPayload().getAddressList();
        Map<String, Address> sameAsAddressMap = new HashMap<>();
        boolean isApplicant = Constants.APPLICANT.equalsIgnoreCase(custType);
        String voterIDType = isApplicant ? Constants.APPLICANT_VOTER : Constants.COAPPLICANT_VOTER;
        String aadhaarIDType = isApplicant ? Constants.APPLICANT_AADHAAR : Constants.COAPPLICANT_AADHAAR;
        for(Address adrs: addressList) {
            if(isApplicant && Constants.PRESENT.equalsIgnoreCase(adrs.getAddressType())) {
                sameAsAddressMap.put((Constants.YES).toLowerCase(), adrs);
                sameAsAddressMap.put(Constants.PRESENT.toLowerCase(), adrs);
            } else if(isApplicant && Constants.PERMANENT.equalsIgnoreCase(adrs.getAddressType())) {
                sameAsAddressMap.put(Constants.PERMANENT.toLowerCase(), adrs);
            } else if(Constants.VOTER.equalsIgnoreCase(adrs.getAddressType())) {
                sameAsAddressMap.put(voterIDType.toLowerCase(), adrs);
            } else if(Constants.SECONDARY_KYC.equalsIgnoreCase(adrs.getAddressType())) {
                sameAsAddressMap.put(aadhaarIDType.toLowerCase(), adrs);
            } else {
                logger.debug("Not a sameAsAddress Type : {}", adrs.getAddressType());
            }
        }
        logger.debug("Same As Address Map {}", sameAsAddressMap);
        return sameAsAddressMap;
    }

    private boolean isCoAppPersonalAddress(String custType, BigDecimal custId, AddressDetails adrs){
        return custType.equals(Constants.APPLICANT) && custId.compareTo(adrs.getCustDtlId()) != 0
                && Constants.PERSONAL.equalsIgnoreCase(adrs.getAddressType());
    }

    private void updateSameAsAddress(String applicationId, AddressDetails adrsLst, String custType, BigDecimal custId, String appId){
        Gson gson = new Gson();
        List<AddressDetails> addressDetailsList = addressDtlRepo.findByApplicationIdAndAppId(applicationId, appId);
        Map <String, Address> sameAsAddressMap = getAddressSameAsMap(adrsLst, custType);
        logger.debug("Existing Address List: {}", addressDetailsList);
        for(AddressDetails adrs : addressDetailsList){
            adrs.setPayload(gson.fromJson(adrs.getPayloadColumn(), AddressDetailsPayload.class));
            if(adrs == null || adrs.getPayload() == null)
                continue;
            boolean updateCoAppPermAddress = isCoAppPersonalAddress(custType, custId, adrs);
            List<Address> addressList = adrs.getPayload().getAddressList();
            for( Address address: addressList) {
                if(address.getAddressSameAs() != null) {
                    Address newAddress = sameAsAddressMap.get(address.getAddressSameAs().toLowerCase());
                    logger.debug("Same as Address: {} , Address from Map : {}", address.getAddressSameAs(), newAddress);
                    copyAddressDetails(newAddress, address);
                    logger.debug("Copied address value: {}", address);
                }
                if(address.getAddressType().equalsIgnoreCase(Constants.PERMANENT)) {
                    Address presAdd = sameAsAddressMap.get((Constants.PRESENT).toLowerCase());
                    if( presAdd != null && presAdd.getResidenceOwnership()!= null &&  presAdd.getResidenceOwnership().equalsIgnoreCase("Own")  && Constants.OTHER.equalsIgnoreCase(address.getAddressSameAs())) {
                        copyAddressDetails(presAdd, address);
                    }
                    updateCoAppPermAddress = false;
                }
            }

            //Adding Co-Applicant Address same as Applicant Address
            if (updateCoAppPermAddress){
                Address permAddress = new Address();
                permAddress.setAddressType(Constants.PERMANENT);
                permAddress.setAddressSameAs(Constants.PERMANENT);
                Address newAddress = sameAsAddressMap.get(Constants.PERMANENT.toLowerCase());
                copyAddressDetails(newAddress, permAddress);
                addressList.add(permAddress);
            }
            adrs.setPayloadColumn(gson.toJson(adrs.getPayload()));
        }
        logger.debug("Saving all the Modified Address Details {}", addressDetailsList);
        addressDtlRepo.saveAll(addressDetailsList);
    }

    private void copyAddressDetails(Address source, Address target) {
        if(source == null)
            return;
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
    }

    private void updateBusinessAddress(AddressDetails addressDetails, int version, String appId) {
        String applicationId = addressDetails.getApplicationId();
        BigDecimal custDtlId = addressDetails.getCustDtlId();
        Gson gson = new Gson();
        AddressDetails addressDtl = addressDtlRepo.findByApplicationIdAndCustDtlIdAndAddressType(applicationId, custDtlId, Constants.OCCUPATION)
                .orElseGet(() -> new AddressDetails(CommonUtils.generateRandomNum(), applicationId, version, appId,
                            custDtlId, "", Constants.OCCUPATION));
        logger.debug("Occupational Address Object {}",addressDtl);
        addressDtl.setPayloadColumn(gson.toJson(addressDetails.getPayload()));
        addressDtlRepo.save(addressDtl);
    }

    public void updateStageVerification(String stageCode, String applicationId, String isVerified, Boolean isBM){
        if(isVerified == null)
            return;
        SorucingStageVerification stageVerification = srcStageVerificationRepo.findById(applicationId).orElseGet(() -> {
            SorucingStageVerification srcStageVerification = new SorucingStageVerification();
            srcStageVerification.setApplicationId(applicationId);
            return srcStageVerification;
        });

        String strStages;
        if(Boolean.TRUE.equals(isBM)){
            strStages = stageVerification.getBmVerifiedStages();
        } else {
            strStages = stageVerification.getVerifiedStages();
        }

        if (strStages == null){
            strStages = stageCode.concat(isVerified);
        } else {
            if(strStages.contains(stageCode)) {
                List<String> verifiedStages = Arrays.asList(strStages.split(","));
                strStages = verifiedStages.stream().map(v ->  v.startsWith(stageCode) ? stageCode.concat(isVerified) : v).collect(Collectors.joining(","));
            } else {
                strStages = strStages+"," + stageCode + isVerified;
            }
        }
        if(Boolean.TRUE.equals(isBM)){
            stageVerification.setBmVerifiedStages(strStages);
        } else {
            stageVerification.setVerifiedStages(strStages);
        }
        srcStageVerificationRepo.save(stageVerification);
    }

    public void updateOtherDetails(ApplyLoanRequestFields requestObj, String userId, CustomerIdentificationLoan customerIdentification, Properties prop){
        updateAmlQuestionnaire(requestObj.getAmlQuestionnaireDetails(), userId, requestObj.getApplicationId());
        logger.debug("Application Id in Request Object {}", requestObj.getApplicationId());
        bcmpiService.updateSourcingInsurance(requestObj);
        loanService.processLoanSourcing("4_", requestObj, customerIdentification, true, prop);
    }

    public void updateAmlQuestionnaire(AMLQuestionnaireDetails amlQuestionnaireDetails, String userId, String applicationId){
        try {
            AMLQuestionnaireDetails extAmlQuestion = amlQuestionnaireRepository.findById(applicationId).orElseGet(() ->{
                AMLQuestionnaireDetails amlQuestion = new AMLQuestionnaireDetails();
                amlQuestion.setApplicationId(applicationId);
                amlQuestion.setCreatedAt(LocalDateTime.now());
                amlQuestion.setCreatedBy(userId);
                return amlQuestion;
            });
            AmlQuestionnairePayload amlPayload = amlQuestionnaireDetails.getPayload();
            Gson gson = new Gson();
            extAmlQuestion.setUpdatedAt(LocalDateTime.now());
            extAmlQuestion.setUpdatedBy(userId);
            extAmlQuestion.setPayloadColumn(gson.toJson(amlPayload));
            amlQuestionnaireRepository.save(extAmlQuestion);
        } catch (NullPointerException e){
            logger.debug("Aml Questionnare values are null");
        }
    }

    private Mono<Response> bmSourcingProcess(ApplyLoanRequestFields requestObj, String requestType){
        logger.debug("BM Sourcing Process for Request Type: {} and requestObj : {}", requestType, requestObj);
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        Response response = new Response(responseHeader, responseBody);

        ApplicationMaster applicationMaster = requestObj.getApplicationMaster();
        Optional<ApplicationMaster> masterData = applicationMasterRepo.findByAppIdAndWorkitemNo(requestObj.getAppId(),
                applicationMaster.getWorkitemNo());
        if(masterData.isPresent()){
            Gson gson = new Gson();
            String applicationId = masterData.get().getApplicationId();
            int version = masterData.get().getVersionNum();
            String custType = applicationMaster.getCustDtlSlNum() == null || applicationMaster.getCustDtlSlNum() == 1 ? Constants.APPLICANT : Constants.COAPPLICANT;
            String stageCode = getStageCode(applicationMaster.getCurrentScreenId(), custType);
            if(Constants.QUERY.equalsIgnoreCase(requestType)){
                updateQueries(requestObj, applicationId, stageCode);
            } else {
                updateStageVerification(stageCode, applicationId, requestObj.getIsStageVerified(), true);
            }

            CustomerDataFields customerDataFields = fetchResponse(masterData.get(), applicationId, requestObj.getAppId() , version, true);

            String responseStr = gson.toJson(customerDataFields);
            responseStr = responseStr.replace(Constants.PAYLOAD_COLUMN, Constants.PAYLOAD);
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
            responseBody.setResponseObj(responseStr);
        } else{
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            responseBody.setResponseObj(Constants.APP_MASTER_NOT_FOUND);
        }
        return Mono.just(response);
    }

    private String getStageCode(String screenId, String custType){
        if(custType == null){
            custType = Constants.APPLICANT;
        }
        String mainScreenId = screenId.split("_")[0];
        String[] screenArray = {Constants.CB_CHECK, Constants.AADHAAR_DETAILS, Constants.BUSINESS,
                Constants.OTHERDETAILS, Constants.REVIEWCONFIRM};
        int screenIndx = Arrays.asList(screenArray).indexOf(mainScreenId)+1;
        screenId = screenId.replace(mainScreenId, screenIndx+"");
        if(screenIndx == 0) return "";
        return screenId+"_"+custType.substring(0,1)+"_";
    }

    private void updateQueries(ApplyLoanRequestFields requestFields, String applicationId,String stageCode) {
        try {
            SorucingStageVerification stageVerification = srcStageVerificationRepo.findById(applicationId).orElseGet(() -> {
                SorucingStageVerification srcStageVerification = new SorucingStageVerification();
                srcStageVerification.setApplicationId(applicationId);
                return srcStageVerification;
            });
            String queryRequest = "";
            String existingQuery = stageVerification.getQueries();
            List<String> requestQueryList = requestFields.getQueries().stream()
                    .map(query -> stageCode+query+ "_" + LocalDateTime.now()).collect(Collectors.toList());
            if (StringUtils.isEmpty(existingQuery)) {
                queryRequest = String.join("|", requestQueryList);
            } else {
                logger.debug("Requested Query List {}",requestQueryList);
                List<String> existingQueryList = new ArrayList<>(Arrays.asList(existingQuery.split("\\|")));
                logger.debug("Existing Query List before remove {}",existingQueryList);
                existingQueryList.removeIf(query -> query.startsWith(stageCode));
                logger.debug("Existing Query List after remove {}",existingQueryList);
                existingQueryList = new ArrayList<>(existingQueryList);
                existingQueryList.addAll(requestQueryList);
                queryRequest = String.join("|", existingQueryList);
            }
            stageVerification.setQueries(queryRequest);
            srcStageVerificationRepo.save(stageVerification);
        } catch (Exception e) {
            logger.debug("Exception in updating query", e);
        }
    }

}
