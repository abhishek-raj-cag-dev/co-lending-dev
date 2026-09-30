package com.iexceed.appzillonbanking.cob.loans.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.iexceed.appzillonbanking.cob.core.repository.ab.*;
import com.iexceed.appzillonbanking.cob.core.utils.AppStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.BCMPIIncomeDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.BCMPILoanObligations;
import com.iexceed.appzillonbanking.cob.core.domain.ab.BCMPIOtherDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.DBKITStageVerification;
import com.iexceed.appzillonbanking.cob.core.domain.ab.Documents;
import com.iexceed.appzillonbanking.cob.core.domain.ab.Udhyam;
import com.iexceed.appzillonbanking.cob.core.domain.ab.UdhyamIdClass;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.loans.payload.BCMPIIncomeDetailsWrapper;
import com.iexceed.appzillonbanking.cob.loans.payload.BCMPIOtherDetailsWrapper;
import com.iexceed.appzillonbanking.cob.loans.payload.LoanObligationsWrapper;
import com.iexceed.appzillonbanking.cob.loans.payload.UploadLoanRequest;
import com.iexceed.appzillonbanking.cob.loans.payload.UploadLoanRequestFields;
import com.iexceed.appzillonbanking.cob.loans.payload.UploadLoanRequestFields.DBKITResponse;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;
import com.iexceed.appzillonbanking.cob.service.COBService;

@Service
public class DBKITService {

    private static final Logger logger = LogManager.getLogger(DBKITService.class);

    private final ApplicationMasterRepository applicationMasterRepo;
    private final DBKITStageVerificationRepository dbkitStageVerificationRepo;
    private final COBService cobService;
    private final BCMPIIncomeDetailsRepository bcmpiIncomeDetailsRepo;
    private final BCMPILoanObligationsRepository bcmpiLoanObligationsRepo;
    private final BCMPIOtherDetailsRepository bcmpiOtherDetailsRepo;
    private final UdhyamRepository udhyamRepository;
    private final DocumentsRepository documentsRepository;
    private final ApplicationMasterRepository2 applicationMasterRepository2;

    public DBKITService(ApplicationMasterRepository applicationMasterRepo,
                        DBKITStageVerificationRepository dbkitStageVerificationRepo, COBService cobService,
                        BCMPIIncomeDetailsRepository bcmpiIncomeDetailsRepo,
                        BCMPILoanObligationsRepository bcmpiLoanObligationsRepo,
                        BCMPIOtherDetailsRepository bcmpiOtherDetailsRepo, UdhyamRepository udhyamRepository
            , DocumentsRepository documentsRepository, ApplicationMasterRepository2 applicationMasterRepository2) {
        this.documentsRepository = documentsRepository;
        this.applicationMasterRepo = applicationMasterRepo;
        this.dbkitStageVerificationRepo = dbkitStageVerificationRepo;
        this.cobService = cobService;
        this.bcmpiIncomeDetailsRepo = bcmpiIncomeDetailsRepo;
        this.bcmpiLoanObligationsRepo = bcmpiLoanObligationsRepo;
        this.bcmpiOtherDetailsRepo = bcmpiOtherDetailsRepo;
        this.udhyamRepository = udhyamRepository;
        this.applicationMasterRepository2 = applicationMasterRepository2;
    }

    public Response DBKITUploadData(UploadLoanRequest dbkitStageVerificationRequest) {
        logger.debug("OnEntry :: uploadLoan");
        Gson gson = new Gson();
        Response fetchUserDetailsResponse = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        fetchUserDetailsResponse.setResponseHeader(responseHeader);
        ResponseBody responseBody = new ResponseBody();
        CustomerDataFields customerDataFields = null;
        String userId = dbkitStageVerificationRequest.getUserId();

        try {

            Properties prop = null;
            try {
                prop = CommonUtils.readPropertyFile();
            } catch (IOException e) {
                logger.error("Error while reading property file in populateRejectedData ", e);
                fetchUserDetailsResponse = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
                        ResponseCodes.FAILURE.getKey());
            }

            UploadLoanRequestFields requestObj = dbkitStageVerificationRequest.getRequestObj();
            String applicationId = requestObj.getApplicationId();
            String appId = requestObj.getAppId();
            int versionNum = requestObj.getVersionNum();
            logger.debug("applicationID : {}", applicationId);
            logger.debug("appId : {}, versionNum : {}", appId, versionNum);
            logger.debug("customerType : {}", requestObj.getCustomerType());

            ApplicationMaster applicationMasterData;
            Optional<ApplicationMaster> appMasterDb = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNum(
                    requestObj.getAppId(), requestObj.getApplicationId(), requestObj.getVersionNum());
            if (appMasterDb.isPresent()) {
                logger.debug("appMasterDb data found");
                applicationMasterData = appMasterDb.get();
                logger.debug("appMasterDb data found: {}", applicationMasterData);
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
            logger.debug("stageId : {}", stageId);
            logger.debug("customerType: {}", requestObj.getCustomerType());
            String custCode = requestObj.getCustomerType().equalsIgnoreCase("Applicant") ? "A" : "C";
            logger.debug("custCode : {}", custCode);
            logger.debug("requestType : {}", requestObj.getRequestType());
            String requestType = requestObj.getRequestType().trim();

            switch (stageId) {
                case "1":// Loan Details
                    logger.debug("stageId 1");
                    if (Constants.QUERY.equalsIgnoreCase(requestType)) {
                        updateQueries(1, requestObj, true);
                    } else if (Constants.VERIFY.equalsIgnoreCase(requestType)) {
                        updateDBKITStageVerification(1, requestObj, custCode);
                    }
                    break;
                case "2":// e-NACH registration
                    logger.debug("stageId 2");
                    if (Constants.QUERY.equalsIgnoreCase(requestType)) {
                        updateQueries(2, requestObj, true);
                    } else if (Constants.VERIFY.equalsIgnoreCase(requestType)) {
                        updateDBKITStageVerification(2, requestObj, custCode);
                    }
                    break;
                case "3":// Udyam registration
                    logger.debug("stageId 3");
                    if ("EDIT".equalsIgnoreCase(requestType)) {
                        updateUdhyam(3, requestObj, userId, prop);
                    } else if (Constants.VERIFY.equalsIgnoreCase(requestType)) {
                        updateUdhyam(3, requestObj, userId, prop);
                        updateDBKITStageVerification(3, requestObj, custCode);
                    }else if ("VERIFY_UDHYAM".equalsIgnoreCase(requestType)) {
                        updateUdhyam(3, requestObj, userId, prop);
                        updateDBKITStageVerification(3, requestObj, custCode);
                    }else if(Constants.QUERY.equalsIgnoreCase(requestType)){
                        updateQueries(3, requestObj, true);
                    }
                    break;
                case "4":// Document execution (Digital)
                    logger.debug("stageId 4");
                    if (Constants.QUERY.equalsIgnoreCase(requestType)) {
                        updateQueries(4, requestObj, true);
                    } else if (Constants.VERIFY.equalsIgnoreCase(requestType)) {
                        updateDBKITStageVerification(4, requestObj, custCode);
                    }
                    break;
                case "5":// Document execution (Manual)
                    logger.debug("stageId 5");
                    if (Constants.QUERY.equalsIgnoreCase(requestType)) {
                        updateQueries(5, requestObj, true);
                    } else if (Constants.VERIFY.equalsIgnoreCase(requestType)) {
                        updateDBKITStageVerification(5, requestObj, custCode);
                    }
                    break;
                case "6":// Document execution (Prayaas)
                    logger.debug("stageId 6");
                    if (Constants.QUERY.equalsIgnoreCase(requestType)) {
                        updateQueries(6, requestObj, true);
                    } else if (Constants.VERIFY.equalsIgnoreCase(requestType)) {
                        updateDBKITStageVerification(6, requestObj, custCode);
                    }
                    break;
                case "7":// Additional documentation (Digital)
                    logger.debug("stageId 7");
                    if (Constants.QUERY.equalsIgnoreCase(requestType)) {
                        updateQueries(7, requestObj, true);
                    } else if (Constants.VERIFY.equalsIgnoreCase(requestType)) {
                        updateDBKITStageVerification(7, requestObj, custCode);
                    }
                    break;
                case "8":// Additional documentation (Manual)
                    logger.debug("stageId 8");
                    if (Constants.QUERY.equalsIgnoreCase(requestType)) {
                        updateQueries(8, requestObj, true);
                    } else if (Constants.VERIFY.equalsIgnoreCase(requestType)) {
                        updateDBKITStageVerification(8, requestObj, custCode);
                    }
                    break;
                case "9":// RPC verification status
                    logger.debug("stageId 9");
                    if (Constants.QUERY.equalsIgnoreCase(requestType)) {
                        updateQueries(9, requestObj, true);
                    } else if (Constants.VERIFY.equalsIgnoreCase(requestType)) {
                        updateDBKITStageVerification(9, requestObj, custCode);
                    }
                    break;
                case "10":// Disbursement
                    logger.debug("stageId 10");
                    if (Constants.QUERY.equalsIgnoreCase(requestType)) {
                        updateQueries(10, requestObj, true);
                    } else if (Constants.VERIFY.equalsIgnoreCase(requestType)) {
                        updateDBKITStageVerification(10, requestObj, custCode);
                    }
                    break;
                case "11":// Welcome kit
                    logger.debug("stageId 11");
                    if (Constants.QUERY.equalsIgnoreCase(requestType)) {
                        updateQueries(11, requestObj, true);
                    } else if (Constants.VERIFY.equalsIgnoreCase(requestType)) {
                        updateDBKITStageVerification(11, requestObj, custCode);
                    }
                    break;
                case "12":// Disbursement status
                    logger.debug("stageId 12");
                    if (Constants.QUERY.equalsIgnoreCase(requestType)) {
                        updateQueries(12, requestObj, true);
                    } else if (Constants.VERIFY.equalsIgnoreCase(requestType)) {
                        updateDBKITStageVerification(12, requestObj, custCode);
                    }
                    break;
                default:
                    logger.debug("invalid stageId");
                    logger.error("Exception in uploadLoan : Invalid Stage Id");
                    responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                    responseBody.setResponseObj("Invalid Stage Id");
                    fetchUserDetailsResponse.setResponseHeader(responseHeader);
                    fetchUserDetailsResponse.setResponseBody(responseBody);
                    return fetchUserDetailsResponse;
            }
            applicationMasterData.setUpdateTs(LocalDateTime.now());
            applicationMasterData.setUpdatedBy(dbkitStageVerificationRequest.getUserId());
            applicationMasterRepo.save(applicationMasterData);
            logger.debug("applicationMaster saved");

            customerDataFields = cobService.getCustomerData(applicationMasterData, applicationId, appId, versionNum);
            logger.debug("customerDataFields  : {}", customerDataFields);

            Optional<DBKITStageVerification> dbKitStageData = dbkitStageVerificationRepo.findById(applicationId);
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
                customerDataFields.setReuploadedDocs(
                        gson.fromJson(dbKitStageData.get().getReuploadedDocs(), new TypeToken<List<String>>() {
                        }.getType()));
            }

            Optional<BCMPIIncomeDetails> bcmpiIncomeDataOpt = bcmpiIncomeDetailsRepo.findById(applicationId);
            if (bcmpiIncomeDataOpt.isPresent()) {
                logger.debug("bcmpiIncomeData found");
                BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper = gson
                        .fromJson(bcmpiIncomeDataOpt.get().getPayload(), BCMPIIncomeDetailsWrapper.class);
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

            Optional<List<Udhyam>> udhyamRecordsOpt = udhyamRepository.findByApplicationId(applicationId);
            if (udhyamRecordsOpt.isPresent()) {
                List<Udhyam> udhyamRecords = udhyamRecordsOpt.get();
                customerDataFields.setUdhyamDetails(udhyamRecords);
            }
            if (stageId.equalsIgnoreCase("3") || stageId.equalsIgnoreCase("4")) {
                Optional<List<Documents>> documentRecordsOpt = documentsRepository.findByApplicationId(applicationId);
                if (documentRecordsOpt.isPresent()) {
                    logger.error("documentRecordsOpt present : " + (documentRecordsOpt));
                    List<Documents> documentRecords = documentRecordsOpt.get();
                    logger.error("documentRecords present : " + (null == documentRecords));
                    logger.error("documentRecords present : " + documentRecords.toString());
                    if (!documentRecords.isEmpty()) {
                        customerDataFields.setDocumentRecordDetails(documentRecords);
                        try {
                            Response documentGenerationResponse = cobService.handleFetchUploadedDocuments(prop, appId, applicationId, gson,
                                    false, "", "", "");
                            logger.debug("reponse from document Generation: {}", documentGenerationResponse);
                            String documentGenerationResponseObject = documentGenerationResponse.getResponseBody().getResponseObj();
                            Type listType = new TypeToken<List<JsonObject>>() {
                            }.getType();
                            List<JsonObject> fileList = gson.fromJson(documentGenerationResponseObject, listType);
                            customerDataFields.setDocumentList(fileList);
                        } catch (Exception e) {
                            logger.error(
                                    "Error while fetching created documents for applicationId : {} , with error message : {}",
                                    applicationId, e.getMessage(), e);
                        }
                    }
                }
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
            responseBody.setResponseObj(e.toString());
            fetchUserDetailsResponse.setResponseHeader(responseHeader);
            fetchUserDetailsResponse.setResponseBody(responseBody);
            return fetchUserDetailsResponse;
        }
    }

    private void updateDBKITStageVerification(int stageId, UploadLoanRequestFields requestObj, String custCode) {
        logger.debug("Entry updateDBKITStageVerification method");
        Gson gson = new Gson();
        logger.debug("updateDBKITStageVerification request : {}", requestObj);
        // Construct the new stage verification strings

        String newEntry = stageId + "_" + custCode + "_" + LocalDateTime.now();

        Set<String> entriesTocompareStageVr = new HashSet<>();
        entriesTocompareStageVr.add(stageId + "_" + custCode);

        List<String> newEntries = Arrays.asList(newEntry);
        // Fetch existing record from DB
        Optional<DBKITStageVerification> stageVerificationDb = dbkitStageVerificationRepo
                .findById(requestObj.getApplicationId());
        logger.debug("Size of stage : {}", stageVerificationDb);

        if (stageVerificationDb.isPresent()) {
            logger.debug("DBKITStageVerificationDb record found");
            DBKITStageVerification stageVerificationDbObj = stageVerificationDb.get();
            String existingStages = stageVerificationDbObj.getVerifiedStages();
            logger.debug("existingStages: {}", existingStages);
            // Convert existing DB string into a Set
            if (StringUtils.isNotEmpty(existingStages)) {
                Set<String> existingSet = null;
                // Convert existing DB string into a Set
                existingSet = new HashSet<>(Arrays.asList(existingStages.split("\\|")));

                existingSet.removeIf(entry -> entriesTocompareStageVr.stream()
                        .anyMatch(compareEntry -> entry.startsWith(compareEntry + "_")));

                logger.debug("existingStageVrSet : {}", existingSet);
                //
                existingSet.add(newEntry);

                if (existingSet.isEmpty()) {
                    stageVerificationDbObj.setVerifiedStages(null);
                } else {
                    stageVerificationDbObj.setVerifiedStages(String.join("|", existingSet));
                    logger.debug("existingStageVrSetFinal : {}", existingSet);
                }
            } else {
                stageVerificationDbObj.setVerifiedStages(newEntry);
            }

            String existingQueries = stageVerificationDbObj.getQueries();
            logger.debug("existingQueries: {}", existingQueries);
            if (StringUtils.isNotEmpty(existingQueries)) {
                Set<String> existingSet = new HashSet<>(Arrays.asList(existingQueries.split("\\|")));
                String applicationStatus = applicationMasterRepository2.findApplicationStatus(requestObj.getAppId(), requestObj.getApplicationId());
                if(applicationStatus.equalsIgnoreCase(AppStatus.DBKITGENERATED.getValue())) {
                    existingSet.removeIf(entry -> entry.startsWith(stageId + "_"));
                }
                logger.debug("existingSet after operation : {}", existingSet);
                stageVerificationDbObj.setQueries(existingSet.isEmpty() ? null : String.join("|", existingSet));
            }

            if(stageId == 4 || stageId == 5) {
                if (null != requestObj.getDbKitResponse() && !requestObj.getDbKitResponse().isEmpty()) {
                    stageVerificationDbObj.setResponse(gson.toJson(requestObj.getDbKitResponse()));
                }

                if (null != requestObj.getApprovedDocs()) {
                    stageVerificationDbObj.setApprovedDocs(gson.toJson(requestObj.getApprovedDocs()));
                }

                if (null != requestObj.getQueries()) {
                    stageVerificationDbObj.setQueryDocs(gson.toJson(requestObj.getQueries()));
                }

                if (null != requestObj.getReuploadedDocs()) {
                    stageVerificationDbObj.setReuploadedDocs(gson.toJson(requestObj.getReuploadedDocs()));
                }
            }

            dbkitStageVerificationRepo.save(stageVerificationDbObj);
        } else {
            logger.debug("DBKITStageVerificationDb record not found. Inserting new record.");
            // If no record exists, create a new one
            String newStageString = String.join("|", newEntries);
            logger.debug("Newstage: {}", newStageString);
            DBKITStageVerification newRpcStageVn = new DBKITStageVerification();
            newRpcStageVn.setApplicationId(requestObj.getApplicationId());
            newRpcStageVn.setVerifiedStages(newStageString);

            if (null != requestObj.getDbKitResponse() && !requestObj.getDbKitResponse().isEmpty()) {
                newRpcStageVn.setResponse(gson.toJson(requestObj.getDbKitResponse()));
            }

            if (null != requestObj.getApprovedDocs()) {
                newRpcStageVn.setApprovedDocs(gson.toJson(requestObj.getApprovedDocs()));
            }

            if (null != requestObj.getQueries() ) {
              newRpcStageVn.setQueryDocs(gson.toJson(requestObj.getQueries()));
            }
            dbkitStageVerificationRepo.save(newRpcStageVn);
        }

        logger.debug("End updateDBKITStageVerification method");

    }

    private void updateUdhyam(int stageId, UploadLoanRequestFields requestObj, String userId, Properties prop) {
        logger.debug("Entry updateUdhyam method");
        logger.debug("updateUdhyam request : {}", requestObj);

        String applicationId = requestObj.getApplicationId();
        String appId = requestObj.getAppId();

        if (requestObj.getUdhyamDetails() != null && !requestObj.getUdhyamDetails().isEmpty()) {
            for (Udhyam item : requestObj.getUdhyamDetails()) {
                if (item == null || item.getCustomerType() == null) {
                    continue;
                }
                String custType = item.getCustomerType();
                UdhyamIdClass udhyamId = new UdhyamIdClass();
                udhyamId.setApplicationId(applicationId);
                udhyamId.setCustomerType(custType);

                Optional<Udhyam> udhyamRecordOpt = udhyamRepository.findById(udhyamId);
                if (udhyamRecordOpt.isPresent()) {
                    logger.debug("Udhyam record found for applicationId: {}, customerType: {}", applicationId, custType);
                    Udhyam udhyamRecord = udhyamRecordOpt.get();
                    if (item.getRemarks() != null) {
                        udhyamRecord.setRemarks(item.getRemarks());
                    }
                    if (item.getUdhyamStatus() != null && !item.getUdhyamStatus().isEmpty()) {
                        udhyamRecord.setUdhyamStatus(item.getUdhyamStatus());
                    }
                    if (item.getUdhyamRegId() != null && !item.getUdhyamRegId().isEmpty()) {
                        udhyamRecord.setUdhyamRegId(item.getUdhyamRegId());
                    }
                    udhyamRecord.setUpdatedTs(LocalDateTime.now());
                    udhyamRepository.save(udhyamRecord);
                    logger.debug("Udhyam record updated successfully for customerType: {}", custType);
                } else {
                    logger.debug("Udhyam record not found for applicationId: {}, customerType: {}. Creating new record.",
                            applicationId, custType);
                    Udhyam udhyamRecord = new Udhyam();
                    udhyamRecord.setApplicationId(applicationId);
                    udhyamRecord.setAppId(appId != null ? appId : item.getAppId());
                    udhyamRecord.setCustomerType(custType);
                    udhyamRecord.setUdhyamRegId(item.getUdhyamRegId());
                    udhyamRecord.setUdhyamStatus(item.getUdhyamStatus() != null ? item.getUdhyamStatus() : "Failed");
                    udhyamRecord.setRemarks(item.getRemarks());
                    udhyamRecord.setCreatedBy(userId);
                    udhyamRecord.setCreateTs(LocalDateTime.now());
                    udhyamRepository.save(udhyamRecord);
                    logger.debug("New Udhyam record created successfully for customerType: {}", custType);
                }
            }
        } else if (requestObj.getCustomerType() != null) {
        String customerType = requestObj.getCustomerType();
        UdhyamIdClass udhyamId = new UdhyamIdClass();
        udhyamId.setApplicationId(applicationId);
        udhyamId.setCustomerType(customerType);
        String udhyamRegId = requestObj.getUdhyamRegId();
        String udhyamStatus = requestObj.getUdhyamStatus();
        String remarks = requestObj.getRemarks();

        logger.debug("applicationId: {}, customerType: {}, appId: {}, udhyamRegId: {}, udhyamStatus: {}, remarks: {}",
                applicationId, customerType, appId, udhyamRegId, udhyamStatus, remarks);

        Optional<Udhyam> udhyamRecordOpt = udhyamRepository.findById(udhyamId);
        if (udhyamRecordOpt.isPresent()) {
            logger.debug("Udhyam record found for applicationId: {}, customerType: {}", applicationId, customerType);
            Udhyam udhyamRecord = udhyamRecordOpt.get();
                if (remarks != null) {
            udhyamRecord.setRemarks(remarks);
                }
                if (udhyamStatus != null && !udhyamStatus.isEmpty()) {
            udhyamRecord.setUdhyamStatus(udhyamStatus);
                }
                if (udhyamRegId != null && !udhyamRegId.isEmpty()) {
                    udhyamRecord.setUdhyamRegId(udhyamRegId);
                }
            udhyamRecord.setUpdatedTs(LocalDateTime.now());
            udhyamRepository.save(udhyamRecord);
            logger.debug("Udhyam record updated successfully");
            } else if (remarks != null || udhyamStatus != null || udhyamRegId != null) {
            logger.debug("Udhyam record not found for applicationId: {}, customerType: {}. Creating new record.",
                    applicationId, customerType);
            Udhyam udhyamRecord = new Udhyam();
            udhyamRecord.setApplicationId(applicationId);
            udhyamRecord.setAppId(appId);
            udhyamRecord.setCustomerType(customerType);
            udhyamRecord.setUdhyamRegId(udhyamRegId);
                udhyamRecord.setUdhyamStatus(udhyamStatus != null ? udhyamStatus : "Failed");
            udhyamRecord.setRemarks(remarks);
            udhyamRecord.setCreatedBy(userId);
            udhyamRecord.setCreateTs(LocalDateTime.now());
            udhyamRepository.save(udhyamRecord);
            logger.debug("New Udhyam record created successfully");
        }
        }
        logger.debug("End updateUdhyam method");
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
            Optional<DBKITStageVerification> dbKitStageVerificationDb = dbkitStageVerificationRepo
                    .findById(requestObj.getApplicationId());

            logger.debug("dbKitStageVerificationDb findById : {}", requestObj.getApplicationId());
            logger.debug("dbKitStageVerificationDb : {}", dbKitStageVerificationDb);

            if (dbKitStageVerificationDb.isPresent()) {
                logger.debug("dbKitStageVerificationDb record found");
                DBKITStageVerification stageVerificationDbObj = dbKitStageVerificationDb.get();
                logger.debug("dbKitStageVerificationDbObj");
                String existingQueries = stageVerificationDbObj.getQueries();
                logger.debug("existingQueries: {}", existingQueries);

                if (newEntries.isEmpty()) {
                    // If new entries are empty, remove existing queries for the stage
                    if (StringUtils.isNotEmpty(existingQueries)) {
                        Set<String> existingSet = new HashSet<>(Arrays.asList(existingQueries.split("\\|")));
                        Set<String> filteredSet = existingSet.stream().filter(e -> e.startsWith(String.valueOf(stageId))).collect(Collectors.toSet());
                        existingSet.removeIf(filteredSet::contains);
                        stageVerificationDbObj.setQueries(existingSet.isEmpty() ? null : String.join("|", existingSet));
                    }
                } else {
                    // If new entries are not empty, update the queries
                    if (StringUtils.isNotEmpty(existingQueries)) {
                        Set<String> existingSet = new HashSet<>(Arrays.asList(existingQueries.split("\\|")));
                        Set<String> filteredSet = existingSet.stream()
                                .filter(e -> e.startsWith(String.valueOf(stageId)))
                                .collect(Collectors.toSet());
                        logger.debug("Filtered Stream : {}", filteredSet);

                        existingSet.removeIf(elem -> filteredSet.contains(elem));
                        logger.debug("Set after operation : {}", existingSet);

                        // Add only new values that are not already present
                        for (String entry : newEntries) {
                            if (!existingSet.contains(entry)) {
                                existingSet.add(entry);
                                logger.debug("Existing set : {}", entry);
                            }
                        }

                        logger.debug("existingSet final: {}", existingSet);
                        stageVerificationDbObj.setQueries(String.join("|", existingSet));
                    } else {
                        stageVerificationDbObj.setQueries(String.join("|", newEntries));
                    }
                }

                if (null != stageVerificationDbObj.getVerifiedStages()) {
                    String existingStagesVr = stageVerificationDbObj.getVerifiedStages();

                    Set<String> existingStageVrSet = new HashSet<>(Arrays.asList(existingStagesVr.split("\\|")));
                    logger.debug("existingStageVrSet : {}", existingStageVrSet);

                    if (!isQuery) {
                        stageVerificationDbObj.setVerifiedStages(
                                existingStageVrSet.isEmpty() ? null : String.join("|", existingStageVrSet));
                    } else {
                        existingStageVrSet.removeIf(entry -> entry.startsWith(stageId + "_"));
                        logger.debug("existingStageVrSet after removing stage specific entries: {}",
                                existingStageVrSet);
                        stageVerificationDbObj.setVerifiedStages(
                                existingStageVrSet.isEmpty() ? null : String.join("|", existingStageVrSet));
                    }

                }
                dbkitStageVerificationRepo.save(stageVerificationDbObj);
            } else {
                logger.debug("DBKITStageVerification record not found. Inserting new record.");

                // If no record exists and new entries are empty, do nothing
                if (!newEntries.isEmpty()) {
                    String newStageString = String.join("|", newEntries);
                    DBKITStageVerification newEntry = new DBKITStageVerification();
                    newEntry.setApplicationId(requestObj.getApplicationId());
                    newEntry.setQueries(newStageString);
                    dbkitStageVerificationRepo.save(newEntry);
                }
            }

            logger.debug("End updateQueries method");
        } catch (Exception e) {
            logger.error("Exception in update: ", e);
        }
    }

}
