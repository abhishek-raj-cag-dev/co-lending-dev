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
        String customerType = requestObj.getCustomerType();
        UdhyamIdClass udhyamId = new UdhyamIdClass();
        udhyamId.setApplicationId(applicationId);
        udhyamId.setCustomerType(customerType);
        String appId = requestObj.getAppId();
        String udhyamRegId = requestObj.getUdhyamRegId();
        String udhyamStatus = requestObj.getUdhyamStatus();
        String remarks = requestObj.getRemarks();

        logger.debug("applicationId: {}, customerType: {}, appId: {}, udhyamRegId: {}, udhyamStatus: {}, remarks: {}",
                applicationId, customerType, appId, udhyamRegId, udhyamStatus, remarks);

        Optional<Udhyam> udhyamRecordOpt = udhyamRepository.findById(udhyamId);
        if (udhyamRecordOpt.isPresent()) {
            logger.debug("Udhyam record found for applicationId: {}, customerType: {}", applicationId, customerType);
            Udhyam udhyamRecord = udhyamRecordOpt.get();
            udhyamRecord.setRemarks(remarks);
            udhyamRecord.setUdhyamStatus(udhyamStatus);
            udhyamRecord.setUpdatedTs(LocalDateTime.now());
            udhyamRepository.save(udhyamRecord);
            logger.debug("Udhyam record updated successfully");
        } else {
            logger.debug("Udhyam record not found for applicationId: {}, customerType: {}. Creating new record.",
                    applicationId, customerType);
            Udhyam udhyamRecord = new Udhyam();
            udhyamRecord.setApplicationId(applicationId);
            udhyamRecord.setAppId(appId);
            udhyamRecord.setCustomerType(customerType);
            udhyamRecord.setUdhyamRegId(udhyamRegId);
            udhyamRecord.setUdhyamStatus(udhyamStatus);
            udhyamRecord.setRemarks(remarks);
            udhyamRecord.setCreatedBy(userId);
            udhyamRecord.setCreateTs(LocalDateTime.now());
            udhyamRepository.save(udhyamRecord);
            logger.debug("New Udhyam record created successfully");
        }
//        Optional<ApplicationMaster> appMaster = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, Constants.INITIAL_VERSION_NO);
//        if(appMaster.isPresent()){
//            if(AppStatus.DBPUSHBACK.getValue().equalsIgnoreCase(appMaster.get().getApplicationStatus())){
//                Response docDeleteResp = cobService.handleDeleteAllDocuments(prop, appId, applicationId);
//                    logger.debug("Successfully deleted documents for application Id : {}",applicationId );
//            }
//        }
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
