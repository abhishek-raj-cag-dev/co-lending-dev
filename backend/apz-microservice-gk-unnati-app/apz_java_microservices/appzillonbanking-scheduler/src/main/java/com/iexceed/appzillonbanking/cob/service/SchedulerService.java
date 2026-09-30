package com.iexceed.appzillonbanking.cob.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CibilDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.SchedulerStageMovement;
import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.WorkFlowDetails;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.CibilDetailsRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.SchedulerStageMovementRepository;
import com.iexceed.appzillonbanking.cob.core.utils.AppStatus;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.loans.payload.BRECBRequest;
import com.iexceed.appzillonbanking.cob.loans.service.LoanService;
import com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserFields;
import com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest;

import reactor.core.publisher.Mono;

@Component
@Lazy(false)
public class SchedulerService {

    private static final Logger logger = LogManager.getLogger(SchedulerService.class);

    private final CibilDetailsRepository cibilDtlRepo;
    private final LoanService loanService;
    private final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    private ScheduledFuture<?> scheduledTask;
    private final ApplicationMasterRepository applicationMasterRepository;
    private final CommonService commonService;
    private final SchedulerStageMovementRepository schedulerStageMovementRepository;
    private final LockProvider lockProvider;

    public SchedulerService(CibilDetailsRepository cibilDtlRepo, LoanService loanService,
                            ApplicationMasterRepository applicationMasterRepository, CommonService commonService,
                            SchedulerStageMovementRepository schedulerStageMovementRepository, LockProvider lockProvider) {
        this.cibilDtlRepo = cibilDtlRepo;
        this.loanService = loanService;
        this.applicationMasterRepository = applicationMasterRepository;
        this.commonService = commonService;
        this.schedulerStageMovementRepository = schedulerStageMovementRepository;
        this.lockProvider = lockProvider;
    }

    private String currentCronExpression = null;

   @PostConstruct
    public void init() {
        taskScheduler.initialize();
        scheduleBRECheck("0 30 18 * * ?");
    }

    @PreDestroy
    public void shutdownScheduler() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }
        taskScheduler.shutdown();
    }

    @Scheduled(cron = "0 */4 * * * ?")
    public void checkPropertyChange() {
        try {
            Properties prop = CommonUtils.readPropertyFile();
            String newCronExpression = prop.getProperty(CobFlagsProperties.CB_CHECK_CRON_TIME.getKey());
            if (newCronExpression != null) {
                scheduleBRECheck(newCronExpression);
            }
        } catch (IOException e) {
            logger.error("Error while reading property file in checkPropertyChange", e);
        }
    }

    public void scheduleBRECheck(String newCronExpression) {
        if (newCronExpression == null || newCronExpression.isEmpty()) {
            logger.warn("Invalid cron expression received, skipping scheduling.");
            return;
        }

        if (newCronExpression.equals(currentCronExpression)) {
            logger.info("Cron expression remains unchanged: {}. Skipping reschedule.", newCronExpression);
            return;
        }

        logger.info("Updating BRE Scheduler. Old Cron: {}, New Cron: {}", currentCronExpression, newCronExpression);

        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            logger.info("Previous BRE Scheduler task cancelled.");
        }

        currentCronExpression = newCronExpression;

        CronTrigger cronTrigger = new CronTrigger(newCronExpression, TimeZone.getTimeZone("Asia/Kolkata"));
        scheduledTask = taskScheduler.schedule(this::breSchedulerCheck, cronTrigger);

        logger.info("BRE Scheduler updated with new cron expression: {}", newCronExpression);
    }

    public void breSchedulerCheck() {
        Optional<SimpleLock> lock = lockProvider.lock(
                new LockConfiguration(Instant.now(), "breSchedulerCheck", Duration.ofMinutes(10), Duration.ofMinutes(5))
        );

        if (!lock.isPresent()) {
            logger.info("Another node is running BRE Scheduler, skipping execution.");
            return;
        }
        logger.info("BRE Scheduler check started");
        try {
            Properties prop = CommonUtils.readPropertyFile();
            String breSchedulerFlag = prop.getProperty(CobFlagsProperties.CB_CHECK_SCHEDULER_FLAG.getKey());

            if ("Y".equalsIgnoreCase(breSchedulerFlag)) {
                int expiryLimit;
                    expiryLimit = Integer.parseInt(prop.getProperty(CobFlagsProperties.CB_EXPIRY_DAYS.getKey()));

                LocalDate expiryDate = LocalDate.now().minusDays(expiryLimit);

                List<CibilDetails> expiredCBApplications = cibilDtlRepo.findCibilDetailsByExpiryLimit(expiryDate);
                if (expiredCBApplications == null || expiredCBApplications.isEmpty()) {
                    logger.info("No expired CB applications found.");
                    return;
                }

                Gson gson = new Gson();
                for (CibilDetails cibilDetails : expiredCBApplications) {
                    processCBApplication(gson, cibilDetails, prop);
                }
            } else {
                logger.info("BRE Scheduler check is disabled");
            }
		} catch (NumberFormatException e) {
			logger.error("Invalid expiry limit value, defaulting to a safe limit", e);
			return;
        } catch (IOException e) {
            logger.error("Error while reading property file in breSchedulerCheck", e);
        }finally {
            lock.get().unlock();
        }
    }

    private void processCBApplication(Gson gson, CibilDetails cibilDetails, Properties prop) {

        String applicationId = cibilDetails.getApplicationId();
        String breCBCheckReq = cibilDetails.getRequest();

        logger.info("Starting CB processing for applicationId={}", applicationId);

        if (breCBCheckReq == null) {
            logger.error("Request is NULL for applicationId={}", applicationId);
            return;
        }

        try {
            JsonObject jsonObject = gson.fromJson(breCBCheckReq, JsonObject.class);

            if (jsonObject == null) {
                logger.error("Parsed JSON is NULL for applicationId={}", applicationId);
                return;
            }

            if (!jsonObject.has("brecbRequest") || !jsonObject.has("header")) {
                logger.error("Invalid JSON structure for applicationId={}, json={}", applicationId, jsonObject);
                return;
            }

            JsonObject brecbRequestObj = jsonObject.getAsJsonObject("brecbRequest");
            JsonObject headerObj = jsonObject.getAsJsonObject("header");

            if (brecbRequestObj == null || headerObj == null) {
                logger.error("brecbRequest/header is NULL for applicationId={}", applicationId);
                return;
            }

            BRECBRequest brecbRequest = gson.fromJson(brecbRequestObj, BRECBRequest.class);
            Header header = gson.fromJson(headerObj, Header.class);

            if (brecbRequest == null || header == null) {
                logger.error("Deserialization failed for applicationId={}", applicationId);
                return;
            }

            logger.info("Invoking BRE CB Check for applicationId={}", applicationId);

            loanService.breCBCheck(brecbRequest, header, prop)
                    .doOnSubscribe(sub ->
                            logger.debug("Subscribed to BRE CB Check for applicationId={}", applicationId))
                    .doOnNext(response -> {
                        logger.info("BRE CB Check SUCCESS for applicationId={}", applicationId);
                        logger.debug("BRE Response for applicationId={} => {}", applicationId, response);

                        try {
                            processSchedulerStageMovement(response);
                            logger.info("Stage movement triggered for applicationId={}", applicationId);
                        } catch (Exception ex) {
                            logger.error("Stage movement FAILED for applicationId={}", applicationId, ex);
                        }
                    }).switchIfEmpty(Mono.fromRunnable(() ->
                            logger.error("BRE CB Check returned EMPTY for applicationId={}", applicationId)
                    )).doOnError(error ->
                            logger.error("BRE CB Check ERROR for applicationId={}", applicationId, error)
                    ).doOnTerminate(() ->
                            logger.debug("BRE pipeline TERMINATED for applicationId={}", applicationId)
                    ).block();

            logger.info("Completed CB processing for applicationId={}", applicationId);

        } catch (JsonSyntaxException e) {
            logger.error("JSON parsing error for applicationId={}: {}", applicationId, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error in processCBApplication for applicationId={}", applicationId, e);
        }
    }

    private void processSchedulerStageMovement(Object response) {
        logger.info("Starting processSchedulerStageMovement with response: {}", response);
        Properties prop = loadProperties();

        try {
            logger.debug("Response class: {}", response == null ? "null" : response.getClass().getName());
            JsonObject root = parseResponseToJsonObject(response);

            if (root == null) {
                logger.warn("Parsed root is null - check response format or parsing errors");
                return;
            }

			if (!root.has(Constants.RESPONSEOBJ)) {
                logger.warn("Parsed root does not contain 'responseObj'. Root: {}", root);
                return;
            }

            JsonObject responseObj;
			if (root.get(Constants.RESPONSEOBJ).isJsonPrimitive()) {
                // responseObj is a stringified JSON - parse it
				responseObj = JsonParser.parseString(root.get(Constants.RESPONSEOBJ).getAsString()).getAsJsonObject();
			} else if (root.get(Constants.RESPONSEOBJ).isJsonObject()) {
                // responseObj is already a JSON object
				responseObj = root.getAsJsonObject(Constants.RESPONSEOBJ);
            } else {
				logger.warn("Unexpected 'responseObj' type: {}", root.get(Constants.RESPONSEOBJ).toString());
                return;
            }
            if (responseObj.has("map") && responseObj.get("map").isJsonObject()) {
                responseObj = responseObj.getAsJsonObject("map");
            } else {
                logger.warn("Expected 'map' inside responseObj but it's missing or not a JsonObject");
                return;
            }

            logger.debug("Parsed responseObj: {}", responseObj);

            String cbStatus = getAsString(responseObj, "cbStatus");
            String applicationId = getAsString(responseObj, "applicationId");
            BigDecimal customerDtlId = new BigDecimal(getAsString(responseObj, "custDtlId", "0"));
            int versionNum = getAsInt(responseObj, "versionNum", 0);
            String appId = getAsString(responseObj, "appId");
            logger.debug("Extracted values - cbStatus: {}, applicationId: {}, customerDtlId: {}, versionNum: {}, appId: {}",
                    cbStatus, applicationId, customerDtlId, versionNum, appId);

            Optional<ApplicationMaster> appMasterOpt = applicationMasterRepository
                    .findByAppIdAndApplicationIdAndVersionNum(appId, applicationId, versionNum);

            if (appMasterOpt.isPresent()) {
                ApplicationMaster appMaster = appMasterOpt.get();
                logger.debug("ApplicationMaster found: {}", appMaster);

                if ("PASS".equalsIgnoreCase(cbStatus)) {
                    logger.info("CB Status is PASS for applicationId: {}", applicationId);
                    handlePassStatus(appMaster, appId, applicationId, versionNum, customerDtlId, prop);
                } else {
                    logger.info("CB Status is NOT PASS for applicationId: {}", applicationId);
                    String brePayload = getAsString(responseObj, "payloadColumn");
                    logger.debug("Extracted values - cbStatus: {}, applicationId: {}, customerDtlId: {}, versionNum: {}, appId: {}, brePayload: {}",
                            cbStatus, applicationId, customerDtlId, versionNum, appId, brePayload);
                    Gson gson = new Gson();
                    CibilDetailsPayload cibilDetailsPayload = gson.fromJson(brePayload, CibilDetailsPayload.class);
                    logger.debug("Deserialized CibilDetailsPayload: {}", cibilDetailsPayload);
                    int retriesLeft = Optional.of(cibilDetailsPayload.getRetryAttempts()).orElse(0);

                    if (retriesLeft > 0) {
                        logger.info("Retry attempts left for applicationId: {}: {}", applicationId, retriesLeft);
                    } else {
                        logger.info("No retry attempts left for applicationId: {}. Proceeding with NON-PASS handling.", applicationId);
                        handleNonPassStatus(appMaster, appId, applicationId, versionNum, prop, cbStatus);
                    }
                }
            } else {
                logger.warn("ApplicationMaster not found for applicationId: {}, appId: {}, versionNum: {}",
                        applicationId, appId, versionNum);
            }

        } catch (Exception e) {
            logger.error("Exception occurred in processSchedulerStageMovement", e);
        }
    }

    private JsonObject parseResponseToJsonObject(Object response) {
        try {
            JsonObject rawJson;
            if (response instanceof String) {
                logger.debug("Parsing response as String");
                rawJson = JsonParser.parseString((String) response).getAsJsonObject();
            } else {
                logger.debug("Parsing response via Gson toJsonTree");
                rawJson = new Gson().toJsonTree(response).getAsJsonObject();
            }

            // Special handling for org.json.JSONObject via Gson
            if (rawJson.has("map")) {
                logger.debug("Unwrapping 'map' from parsed JSON (org.json.JSONObject case)");
                return rawJson.getAsJsonObject("map");
            }

            return rawJson;
        } catch (Exception e) {
            logger.error("Error while parsing response to JsonObject. Raw response: {}", response, e);
            return null;
        }
    }

    private String getAsString(JsonObject obj, String key) {
        return getAsString(obj, key, null);
    }

    private String getAsString(JsonObject obj, String key, String defaultVal) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : defaultVal;
    }

    private int getAsInt(JsonObject obj, String key, int defaultVal) {
        return obj.has(key) && obj.get(key).isJsonPrimitive() ? obj.get(key).getAsInt() : defaultVal;
    }

    private Properties loadProperties() {
        logger.info("Loading properties in loadProperties method");
        try {
            return CommonUtils.readPropertyFile();
        } catch (Exception e) {
            logger.error("Error while loading properties", e);
            return null;
        }
    }

    private void handlePassStatus(ApplicationMaster appMaster, String appId, String applicationId, int versionNum,
                                  BigDecimal customerDtlId, Properties prop) {
        logger.info("Handling PASS status for applicationId: {}", applicationId);
        String applicationStatus = appMaster.getApplicationStatus();
        logger.info("Application status: {}", applicationStatus);

        List<CibilDetails> cibilDetailsList = cibilDtlRepo.findByApplicationIdAndAppId(applicationId, appId);
        if (cibilDetailsList == null || cibilDetailsList.isEmpty()) {
            logger.warn("No CIBIL details found for applicationId: {}, appId: {}", applicationId, appId);
            return;
        }
        String cbStatusResp = null;
        boolean allPass = true;
        for (CibilDetails cibilDetails : cibilDetailsList) {
            logger.info("Processing CIBIL details: {}", cibilDetails);
            String breStatus = cibilDetails.getCbStatus();
            if (!"PASS".equalsIgnoreCase(breStatus)) {
                allPass = false;
            }
        }
        cbStatusResp = allPass ? "PASS" : "FAIL";
        try {
            if (cbStatusResp.equalsIgnoreCase("PASS")) {
				FetchDeleteUserRequest fetchDeleteUserRequest = prepareFetchDeleteUserRequest(appId, applicationId,
						versionNum);
                WorkFlowDetails workFlowDetails = prepareWorkFlowDetails();

                switch (applicationStatus) {
                    case Constants.CACOMPLETED:
                    case Constants.PENDINGDEVIATION:
                    case Constants.PENDINGREASSESSMENT:
                    case Constants.PENDINGPRESANCTION:
                        logger.info(" Processing stage movement for application status: {}", applicationStatus);
                        workFlowDetails.setAction(Constants.REASSESS);
                        processStageMovement(fetchDeleteUserRequest, workFlowDetails, applicationStatus,
                                AppStatus.RPCVERIFIED.getValue(), Constants.CREDITASSESSMENT, prop,
                                cbStatusResp);
                        break;
                    case Constants.SANCTIONED:
                    case Constants.DBKITGENERATED:
                    case Constants.DBKITVERIFIED:
                    case Constants.DBPUSHBACK:
                        logger.info("Processing stage movement for application status : {}", applicationStatus);
                        workFlowDetails.setAction(Constants.RESANCTION);
                        processStageMovement(fetchDeleteUserRequest, workFlowDetails, applicationStatus,
                                AppStatus.RESANCTION.getValue(), Constants.RESANCTION, prop, cbStatusResp);
                        break;
                    default:
                        logger.warn("Unhandled application status: {}", applicationStatus);
                        break;
                }
            } else {
				handleNonPassStatus(appMaster, appId, applicationId, versionNum, prop, cbStatusResp);
            }

        } catch (Exception e) {
			logger.error("Error while handling pass status for Application ID {}: {}", applicationId, e.getMessage(),
					e);
        }

    }

	private void handleNonPassStatus(ApplicationMaster appMaster, String appId, String applicationId, int versionNum,
			Properties prop, String cbStatus) {
        logger.info("Handling NON-PASS status for applicationId: {}", applicationId);
        String applicationStatus = appMaster.getApplicationStatus();
        logger.info("Application status :: {}", applicationStatus);
		FetchDeleteUserRequest fetchDeleteUserRequest = prepareFetchDeleteUserRequest(appId, applicationId, versionNum);
        WorkFlowDetails workFlowDetails = prepareWorkFlowDetails();

        switch (applicationStatus) {
            case Constants.CACOMPLETED:
            case Constants.PENDINGDEVIATION:
            case Constants.PENDINGREASSESSMENT:
            case Constants.SANCTIONED:
            case Constants.DBKITGENERATED:
            case Constants.DBKITVERIFIED:
            case Constants.PENDINGPRESANCTION:
            case Constants.DBPUSHBACK:
            case Constants.APPROVED:
            case Constants.PENDING_FOR_RPCVERIFICATION :
            case Constants.RPCPUSHBACK:
                logger.info("Processing stage movement for application status: {}", applicationStatus);
                workFlowDetails.setAction(Constants.REJECT);
                processStageMovement(fetchDeleteUserRequest, workFlowDetails, applicationStatus,
                        AppStatus.REJECTED.getValue(), Constants.INPUT, prop, cbStatus);
                break;
            default:
                logger.warn("Unhandled application status:: {}", applicationStatus);
        }
    }

    private FetchDeleteUserRequest prepareFetchDeleteUserRequest(String appId, String applicationId, int versionNum) {
        logger.info("Preparing FetchDeleteUserRequest for applicationId: {}", applicationId);
        FetchDeleteUserRequest fetchDeleteUserRequest = new FetchDeleteUserRequest();
        FetchDeleteUserFields customerDataFields = new FetchDeleteUserFields();
        customerDataFields.setAppId(appId);
        customerDataFields.setApplicationId(applicationId);
        customerDataFields.setVersionNum(versionNum);
        fetchDeleteUserRequest.setRequestObj(customerDataFields);
        return fetchDeleteUserRequest;
    }

    private WorkFlowDetails prepareWorkFlowDetails() {
        logger.info("Preparing WorkFlowDetails");
        WorkFlowDetails workFlowDetails = new WorkFlowDetails();
        workFlowDetails.setWorkflowId(Constants.CBSCHEDULER);
        workFlowDetails.setCurrentRole(Constants.SYSTEM);
        return workFlowDetails;
    }

    private void processStageMovement(FetchDeleteUserRequest fetchDeleteUserRequest, WorkFlowDetails workFlowDetails,
			String fromStatus, String toStatus, String nextStageId, Properties prop,
                                      String cbStatus) {
        logger.info("Processing stage movement from {} to {} for applicationId: {}", fromStatus, toStatus,
                fetchDeleteUserRequest.getRequestObj().getApplicationId());

        Mono<Response> stageMovementResponse = null;
        Mono<Object> stageMovementResp = null;
        workFlowDetails.setNextStageId(nextStageId);
        workFlowDetails.setNextWorkflowStatus(toStatus);
        workFlowDetails.setCurrentStage(Constants.CBSCHEDULER);
        workFlowDetails.setRemarks(String.format("Scheduler BRE Triggered, BRE decision : %s", cbStatus));
        fetchDeleteUserRequest.getRequestObj().setRemarks(String.format("Scheduler BRE Triggered, BRE decision : %s", cbStatus));
        fetchDeleteUserRequest.getRequestObj().setWorkFlow(workFlowDetails);
        fetchDeleteUserRequest.getRequestObj().setStatus(toStatus);
        fetchDeleteUserRequest.getRequestObj().setUserId(Constants.SYSTEM);

        Header header = new Header();

        switch (fromStatus) {
            case Constants.CACOMPLETED:
                logger.info("Processing stage movement for status: CACOMPLETED");
                stageMovementResp = commonService.sanctionApplicationMovement(fetchDeleteUserRequest, header, prop);
                stageMovementResponse = stageMovementResp.map(obj -> (Response) obj);
                break;
            case Constants.PENDINGDEVIATION:
                logger.info("Processing stage movement for status: PENDINGDEVIATION");
                stageMovementResponse = commonService.creditDeviationApplicationMovement(fetchDeleteUserRequest, prop,
                        fetchDeleteUserRequest.getRequestObj().getApplicationId());
                break;
            case Constants.PENDINGREASSESSMENT:
                logger.info("Processing stage movement for status: PENDINGREASSESSMENT");
                stageMovementResponse = commonService.creditReassessmentApplicationMovement(fetchDeleteUserRequest, prop,
                        fetchDeleteUserRequest.getRequestObj().getApplicationId());
                break;
            case Constants.SANCTIONED:
            case Constants.DBPUSHBACK:
                logger.info("Processing stage movement for status: SANCTIONED");
                stageMovementResponse = commonService.dbkitApplicationMovement(fetchDeleteUserRequest, prop, fetchDeleteUserRequest.getRequestObj().getApplicationId());
                break;
            case Constants.DBKITGENERATED:
                logger.info("Processing stage movement for status: DBKITGENERATED");
                stageMovementResponse = commonService.dbkitVerificationApplicationMovement(fetchDeleteUserRequest, prop,
                        fetchDeleteUserRequest.getRequestObj().getApplicationId());
                break;
            case Constants.DBKITVERIFIED:
                logger.info("Processing stage movement for status: DBKITVERIFIED");
                // Haeader has to be passed. Right declared as null
                stageMovementResp = commonService.disbursementApplicationMovement(fetchDeleteUserRequest, null, prop);
                stageMovementResponse = stageMovementResp.map(obj -> (Response) obj);
                break;
            case Constants.PENDINGPRESANCTION:
                logger.info("Processing stage movement for status: PENDINGPRESANCTION");
                // Haeader has to be passed. Right declared as null
                stageMovementResponse = commonService.preSanctionApplicationMovement(fetchDeleteUserRequest, prop, Constants.CBSCHEDULER);
                break;
            case Constants.APPROVED:
            case Constants.PENDING_FOR_RPCVERIFICATION :
            case Constants.RPCPUSHBACK:
                logger.info("Processing stage movement for status: APPROVED/PENDING_FOR_RPCVERIFICATION/RPCPUSHBACK");
                stageMovementResponse = commonService.stageMovementApplication(fetchDeleteUserRequest, prop, CobFlagsProperties.RPC.getKey());
                break;
            default:
                logger.warn("Unhandled application status :: {}", fromStatus);
        }
        if (stageMovementResponse != null) {
            stageMovementResponse.subscribe(res -> {
                logger.info("Stage movement response: {}", res);
                if (ResponseCodes.SUCCESS.getKey().equalsIgnoreCase(res.getResponseHeader().getResponseCode())) {
					saveSchedulerStageMovement(fromStatus, toStatus, fetchDeleteUserRequest.getRequestObj().getApplicationId(), cbStatus);
                    logger.info("Stage movement successful from {} to {} for applicationId: {}", fromStatus, toStatus,
                            fetchDeleteUserRequest.getRequestObj().getApplicationId());
                }
            }, error -> logger.error("Error during stage movement", error));
        } else {
			logger.warn("Stage movement response is null for applicationId: {}", fetchDeleteUserRequest.getRequestObj().getApplicationId());
        }
    }

    private void saveSchedulerStageMovement(String fromStatus, String toStatus, String applicationId, String cbStatus) {
        logger.info("Saving scheduler stage movement for applicationId: {}", applicationId);

        Optional<SchedulerStageMovement> schedulerStageMovementOpt = schedulerStageMovementRepository
                .findById(applicationId);
        SchedulerStageMovement schedulerStageMovement = schedulerStageMovementOpt
                .orElseGet(SchedulerStageMovement::new);

        schedulerStageMovement.setApplicationId(applicationId);
        schedulerStageMovement.setFromStatus(fromStatus);
        schedulerStageMovement.setToStatus(toStatus);
        schedulerStageMovement.setCreateTs(LocalDateTime.now());
        schedulerStageMovement.setCbStatus(cbStatus);

        schedulerStageMovementRepository.save(schedulerStageMovement);
        logger.info("Scheduler stage movement saved successfully for applicationId: {}", applicationId);
    }
}
