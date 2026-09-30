package com.iexceed.appzillonbanking.cob.loans.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.core.repository.ab.*;
import com.iexceed.appzillonbanking.cob.core.services.InterfaceAdapter;
import com.iexceed.appzillonbanking.cob.core.utils.*;
import com.iexceed.appzillonbanking.cob.domain.ab.LovMaster;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiCoApplicantDetails;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedCDHLead;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedOccpInsr;
import com.iexceed.appzillonbanking.cob.loans.payload.*;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;
import com.iexceed.appzillonbanking.cob.payload.LoanCreationReqFields;
import com.iexceed.appzillonbanking.cob.payload.LoanFetchReqFields;
import com.iexceed.appzillonbanking.cob.repository.ab.LovMasterRepository;
import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiCoApplicantDetailsRepository;
import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiIexceedCDHLeadRepo;
import com.iexceed.appzillonbanking.cob.repository.cdh.UnnatiIexceedOccpInsrRepository;
import com.iexceed.appzillonbanking.cob.service.COBService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.iexceed.appzillonbanking.cob.core.utils.CommonUtils.extractErrorMessages;

@Service
public class T24AndCDHService {

    private static final Logger logger = LogManager.getLogger(T24AndCDHService.class);

    private final AdapterUtil adapterUtil;
    private final ApplicationMasterRepository applicationMasterRepo;
    private final LoanDtlsRepo loanDtlsRepo;
    private final InterfaceAdapter interfaceAdapter;
    private final CustomerDetailsRepository custDtlRepo;
    private final AddressDetailsRepository addressDtlRepo;
    private final BankDetailsRepository bankDtlRepo;
    private final CibilDetailsRepository cibilDtlRepo;
    private final BCMPIIncomeDetailsRepository bcmpiIncomeDetailsRepo;
    private final BCMPIOtherDetailsRepository bcmpiOtherDetailsRepo;
    private final LovMasterRepository lovMasterRepository;
    private final ApiExecutionLogRepository logRepository;
    private final UnnatiIexceedCDHLeadRepo unnatiIexceedCDHLeadRepo;
    private final COBService cobService;
    private final UnnatiCoApplicantDetailsRepository coAppDetailsRepository;
    private final UnnatiIexceedOccpInsrRepository iexceedOccpInsrRepository;

    public T24AndCDHService(
            AdapterUtil adapterUtil,
            ApplicationMasterRepository applicationMasterRepo,
            LoanDtlsRepo loanDtlsRepo,
            InterfaceAdapter interfaceAdapter,
            CustomerDetailsRepository custDtlRepo,
            AddressDetailsRepository addressDtlRepo,
            BankDetailsRepository bankDtlRepo,
            CibilDetailsRepository cibilDtlRepo,
            BCMPIIncomeDetailsRepository bcmpiIncomeDetailsRepo,
            BCMPIOtherDetailsRepository bcmpiOtherDetailsRepo,
            LovMasterRepository lovMasterRepository,
            ApiExecutionLogRepository logRepository,
            UnnatiIexceedCDHLeadRepo unnatiIexceedCDHLeadRepo, COBService cobService, UnnatiCoApplicantDetailsRepository coAppDetailsRepository, UnnatiIexceedOccpInsrRepository iexceedOccpInsrRepository
    ) {
        this.adapterUtil = adapterUtil;
        this.applicationMasterRepo = applicationMasterRepo;
        this.loanDtlsRepo = loanDtlsRepo;
        this.interfaceAdapter = interfaceAdapter;
        this.custDtlRepo = custDtlRepo;
        this.addressDtlRepo = addressDtlRepo;
        this.bankDtlRepo = bankDtlRepo;
        this.cibilDtlRepo = cibilDtlRepo;
        this.bcmpiIncomeDetailsRepo = bcmpiIncomeDetailsRepo;
        this.bcmpiOtherDetailsRepo = bcmpiOtherDetailsRepo;
        this.lovMasterRepository = lovMasterRepository;
        this.logRepository = logRepository;
        this.unnatiIexceedCDHLeadRepo = unnatiIexceedCDHLeadRepo;
        this.cobService = cobService;
        this.coAppDetailsRepository = coAppDetailsRepository;
        this.iexceedOccpInsrRepository = iexceedOccpInsrRepository;
    }

    private String requestLog;

    public String getRequestLog() {
        return requestLog;
    }

    public void setRequestLog(String requestLog) {
        this.requestLog = requestLog;
    }

    DateTimeFormatter localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat inFormat = new SimpleDateFormat("dd/MM/yyyy");

    private int loanDetailsLovId = 29;

    public Mono<Response> deleteProspectCustomerData(ApplicationMaster applicationMaster, String appId, Header header, Properties prop) {
        String applicationId = applicationMaster.getApplicationId();
        logger.debug("Deleting prospect customer data for applicationId: {}", applicationId);

        List<String> prospectCustomerIdList = new ArrayList<>();
        if (Constants.OPENMARKET_LOAN_PRODUCT_CODE.equalsIgnoreCase(applicationMaster.getProductCode())) {
            prospectCustomerIdList.add(applicationMaster.getSearchCode2());
        }
        prospectCustomerIdList.add(applicationId);

        return Flux.fromIterable(prospectCustomerIdList)
                .flatMap(prospectId -> deleteProspectCustomer(prospectId, appId, header, prop, applicationId, applicationMaster.getApplicationStatus())
                        .then(Mono.<String>empty())                             // success -> contributes nothing to the list
                        .onErrorResume(ex -> failureMessage(prospectId, ex)))    // failure -> contributes a message, or nothing if "already deleted"
                .collectList()
                .map(this::buildResponse);
    }

    private Mono<String> failureMessage(String prospectId, Throwable ex) {
        String[] errorDetails = extractErrorDetails(ex);
        String apiMessage = errorDetails[1];
        logger.error("Delete Prospect Customer API failed for prospectId {}: {}", prospectId, errorDetails[0]);

        if (apiMessage != null && apiMessage.contains(Constants.ALREADY_ABSENT_MARKER)) {
            logger.debug("Prospect customer {} already absent, treating as success: {}", prospectId, apiMessage);
            return Mono.empty();
        }
        return Mono.just((apiMessage != null ? apiMessage : "Failed to delete prospect customer") + " (customerId: " + prospectId + ")");
    }

    private Response buildResponse(List<String> failures) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();

        if (!failures.isEmpty()) {
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            responseHeader.setResponseMessage(String.join("; ", failures));
            responseBody.setResponseObj(new Gson().toJson(failures));
        } else {
            responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
            responseHeader.setResponseMessage("Prospect customer(s) deleted successfully");
        }
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    public Mono<Object> deleteProspectCustomer(String prospectCustomerId, String appId, Header header,
                                               Properties prop, String applicationId, String currentStage) {
        logger.debug("Deleting prospect customer with ID: {}", prospectCustomerId);
        Map<String, String> mapReq = new HashMap<>();
        mapReq.put("customerId", prospectCustomerId);
        logger.debug("JSON request for deleting prospect customer: {}", mapReq);

        LoanRequestExt deleteProspectCustomerReq = new LoanRequestExt();
        deleteProspectCustomerReq.setAppId(appId);
        deleteProspectCustomerReq.setInterfaceName(prop.getProperty(CobFlagsProperties.DELETE_PROSPECT_CUSTOMER_API_INTF.getKey()));
        deleteProspectCustomerReq.setRequestObj(mapReq);
        logger.debug("Delete Prospect Customer API Request: {}", deleteProspectCustomerReq.toString());

        return interfaceAdapter.callExternalService(header, deleteProspectCustomerReq, deleteProspectCustomerReq.getInterfaceName())
                .flatMap(val -> {
                    JSONObject apiResp = new JSONObject(new Gson().toJson(val));
                    apiResp.put(Constants.API_REQUEST, deleteProspectCustomerReq.toString());
                    logger.debug("Response from Delete Prospect Customer API: {}", apiResp);
                    return logAsyncForProspectCustDeletion(applicationId, deleteProspectCustomerReq.toString(), apiResp.toString(), ResponseCodes.SUCCESS.getValue(), null, currentStage)
                            .thenReturn((Object) apiResp);
                })
                .onErrorResume(ex -> {
                    String[] errorDetails = extractErrorDetails(ex);
                    return logAsyncForProspectCustDeletion(applicationId, deleteProspectCustomerReq.toString(), errorDetails[0], ResponseCodes.FAILURE.getValue(), errorDetails[1], currentStage)
                            .then(Mono.error(ex)); // re-throw so the caller's success/failure classification still runs
                });
    }

    private Mono<Void> logAsyncForProspectCustDeletion(String applicationId, String request, String response, String status, String errorMsg, String currentStage) {
        return Mono.fromRunnable(() ->
                        saveLog(applicationId, Constants.STEP_PROSPECT_CUSTOMER_DELETION, request, response, status, errorMsg, currentStage))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> {
                    logger.error("Failed to write API execution log for applicationId {} step {}", applicationId, Constants.STEP_PROSPECT_CUSTOMER_DELETION, ex);
                    return Mono.empty();
                })
                .then();
    }

    private String[] extractErrorDetails(Throwable ex) {
        if (ex instanceof WebClientResponseException) {
            WebClientResponseException wcre = (WebClientResponseException) ex;
            String rawBody = wcre.getResponseBodyAsString();
            String message = null;
            try {
                message = new JSONObject(rawBody).optString("message", null);
            } catch (Exception parseEx) {
                logger.error("Could not parse error body: {}", rawBody, parseEx);
            }
            return new String[] { rawBody, message };
        }
        return new String[] { ex.getMessage(), ex.getMessage() };
    }

    public Mono<Object> externalDedupeAPIV2(String appId, String kycId, String typesrch, Header header, Properties prop){
        logger.debug("External Dedupe API V2 started for kycId: {}, typesrch: {}", kycId, typesrch);
        String method = Constants.DEDUPE_CHECK;
        String id = "5";//As per API docs
        String gkv = "4.1"; //As per API docs

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("id", id);
        requestBody.put("gkv", gkv);
        requestBody.put("method", method);
        requestBody.put("typesrch", typesrch);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("id", kycId);
        requestBody.put("param", paramMap);

        logger.debug("External Dedupe API V2 request body: {}", requestBody);
        LoanRequestExt externalDedupeCheckReq = new LoanRequestExt();
        externalDedupeCheckReq.setAppId(appId);
        externalDedupeCheckReq.setInterfaceName(prop.getProperty(CobFlagsProperties.EXTERNAL_DEDUPE_CHECK_V2.getKey()));
        externalDedupeCheckReq.setRequestObj(requestBody);
        logger.debug("ExternalDedupeAPI Req: {} ", externalDedupeCheckReq.toString());

        Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, externalDedupeCheckReq,
                externalDedupeCheckReq.getInterfaceName());
        logger.debug("response 1 from the API:" + apiRespMono);

        return apiRespMono.flatMap(val -> {
            logger.debug("response 2 from the API: " + val);
            JSONObject apiResp = new JSONObject(new Gson().toJson(val));
            apiResp.put(Constants.API_REQUEST, externalDedupeCheckReq.toString());
            logger.debug("JSON response 3 from the API: " + apiResp);
            return Mono.just(apiResp);
        });
    }


    public Mono<Object> customerLoanCheckApi(String customerId, String appId, Header header, Properties prop){
        logger.debug("Validating existing loans for customerId: {}", customerId);

        Map<String, String> mapReq = new HashMap<>();
        mapReq.put("customerId", customerId);
        logger.debug("JSON request for coapplicant creation api :" + mapReq);
        logger.debug("Final request object: {}", mapReq);

        LoanRequestExt customerLoanCheckApiReq = new LoanRequestExt();
        customerLoanCheckApiReq.setAppId(appId);
        customerLoanCheckApiReq.setInterfaceName(prop.getProperty(CobFlagsProperties.CUSTOMER_LOAN_CHECK_API_INTF.getKey()));
        customerLoanCheckApiReq.setRequestObj(mapReq);
        logger.debug("Coapplicant creation API: {} ", customerLoanCheckApiReq.toString());

        Mono<Object> apiRespMono = interfaceAdapter.callExternalService(header, customerLoanCheckApiReq,
                customerLoanCheckApiReq.getInterfaceName());
        logger.debug("response 1 from the API:" + apiRespMono);

        return apiRespMono.flatMap(val -> {
            logger.debug("response 2 from the API: " + val);
            JSONObject apiResp = new JSONObject(new Gson().toJson(val));
            apiResp.put(Constants.API_REQUEST, customerLoanCheckApiReq.toString());
            logger.debug("JSON response 3 from the API: " + apiResp);
            return Mono.just(apiResp);
        });
    }

    public Mono<Object> dedupeUpdateT24(String customerId,
                                        String applicationId,
                                        String appId,
                                        String productCode,
                                        Header header, Properties prop, int customerType) {
        logger.debug("Inside dedupeUpdateT24 for applicationId: {}, customerId: {}", applicationId, customerId);
        List<ApplicationMaster> appMasterList = applicationMasterRepo.findByAppIdAndApplicationId(
                Constants.APPID, applicationId);
        String customerTypeStr = (customerType == 1) ? Constants.APPLICANT : Constants.COAPPLICANT;
        if (!appMasterList.isEmpty()) {
            logger.debug("Application Master record found for applicationId: {}. Application Status: {}", applicationId, appMasterList.get(0).getApplicationStatus());
            String applicationStatus = appMasterList.get(0).getApplicationStatus();
            ApplicationMaster appMaster = appMasterList.get(0);

            if(Constants.OPENMARKET_LOAN_PRODUCT_CODE.equalsIgnoreCase(productCode)) {
                String applicantId = customerId;
                if(StringUtils.isNotBlank(appMaster.getApplicantT24Id())){
                    applicantId = appMaster.getApplicantT24Id();
                }
                return dedupeUpdateCDH(applicantId, applicationId, appId, productCode,  header, prop, customerTypeStr);
            }//Updating CDH direclty for open market cases since we don't have T24 id to update applicant at this stage
            Mono<Object> t24UpdateResp = coapplicantCreation(
                    applicationId, appId, customerId, header, prop, true, "", false
            );

            return t24UpdateResp.flatMap(t24Resp -> {
                try {
                    logger.debug("Applicant Update response: {}", t24Resp);
                    String jsonBody = (new Gson()).toJson(t24Resp);
                    JSONObject apiRes = CommonUtils.convertResponseToJson(jsonBody);
                    Object requestObj = apiRes.get(Constants.API_REQUEST);
                    if (apiRes.has(Constants.HEADER)) {
                        JSONObject headerJs = apiRes.getJSONObject(Constants.HEADER);
                        String status = headerJs.optString(Constants.STATUS);
                        // If Record not changed considering it as Success
                        boolean isBusinessRecordNotChanged = false;
                        JSONArray errorDetails = null;
                        if ("failed".equalsIgnoreCase(status) && apiRes.has(Constants.ERROR1)) {
                            JSONObject errorObj = apiRes.getJSONObject(Constants.ERROR1);
                            if (errorObj.has(Constants.ERROR_DETAILS)) {
                                errorDetails = errorObj.getJSONArray(Constants.ERROR_DETAILS);
                                for (int i = 0; i < errorDetails.length(); i++) {
                                    JSONObject err = errorDetails.getJSONObject(i);
                                    if (Constants.UNCHANGED_RECORD_CODE.equalsIgnoreCase(err.optString("code"))) {
                                        isBusinessRecordNotChanged = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (ResponseCodes.SUCCESS.getValue().equalsIgnoreCase(status) || isBusinessRecordNotChanged) {
                            logger.debug("Applicant Update started");
                            saveLog(applicationId, customerTypeStr + " " + "Sourcing T24 Updation", requestObj.toString(), t24Resp.toString(),
                                    ResponseCodes.SUCCESS.getValue(), null, applicationStatus);
                            return dedupeUpdateCDH(customerId, applicationId, appId, productCode,  header, prop, customerTypeStr);
                        }
                        if (apiRes.has(Constants.ERROR1) && !apiRes.getJSONObject(Constants.ERROR1).isEmpty()) {
                            JSONArray coCustCreationErr = apiRes.getJSONObject(Constants.ERROR1).getJSONArray(Constants.ERROR_DETAILS);
                            List<String> coCustCreationErrors = extractErrorMessages(apiRes.getJSONObject("error"));
                            saveLog(applicationId, applicationStatus,requestObj.toString(),
                                    t24Resp.toString(), ResponseCodes.FAILURE.getValue(), coCustCreationErrors.toString(), applicationStatus);
                            logger.debug("CoCustCreationErr :" + coCustCreationErr.toString());
                            List<String> coCustCreationErrList = new ArrayList<>();
                            for (Object field : coCustCreationErr) {
                                JSONObject fieldError = new JSONObject(field.toString());
                                logger.debug("coCustCreation Field Error " + fieldError);
                                String message = fieldError.optString(Constants.MESSAGE);
                                String expected = prop.getProperty(
                                        CobFlagsProperties.CO_CUST_FETCH_INPUT_ERROR.getKey()
                                );
                                // Build a tolerant regex from the configured message
                                String baseRegex = expected
                                        .toLowerCase()
                                        .replaceAll("voter-id", "voter[- ]?id")   // voterId / voter-id / VOTER-ID
                                        .replaceAll("\\s+", "\\\\s*")             // flexible spacing
                                        .replaceAll(":", "\\\\s*:?\\\\s*");       // optional colon & spaces
                                // Full regex with optional brackets and numeric customer ID
                                String finalRegex = "(?i)\\[?\\s*.*?" + baseRegex + "(\\d+)\\s*\\]?";

                                Pattern pattern = Pattern.compile(finalRegex);
                                Matcher matcher = pattern.matcher(message);

                                if (matcher.find()) {
                                    String existingCoCustId = matcher.group(1);
                                    logger.debug("Extracted Co-Customer ID: " + existingCoCustId);

                                    return dedupeUpdateCDH(customerId, applicationId, appId, productCode,  header, prop, customerTypeStr);

                                } else {
                                    logger.debug("No matching Co-Customer ID found in the message. : {}", message);
                                    coCustCreationErrList.add(fieldError.optString(Constants.FIELD_NAME, Constants.ERROR2) + " - " + fieldError.optString(Constants.MESSAGE, "Unknown Error").trim());

                                    saveLog(applicationId, customerTypeStr + " " + "Sourcing T24 Updation", requestObj.toString(), coCustCreationErrList.toString() + "No customer id in the error message", ResponseCodes.FAILURE.getValue(), coCustCreationErrList.toString(), applicationStatus);
                                    return Mono.just(adapterUtil.setError("Error while updating dedupe details in T24 : " + coCustCreationErrList.toString(), "1"));
                                }
                            }
                            List<String> errors = extractErrorMessages(apiRes.getJSONObject(Constants.ERROR1));
                            saveLog(applicationId, customerTypeStr + " " + "Sourcing T24 Updation", requestObj.toString(),
                                    t24Resp.toString(), ResponseCodes.FAILURE.getValue(), errors.toString(), applicationStatus);
                            return Mono.just(adapterUtil.setError(" Error while updating dedupe details in T24 : " + errors.toString(), "1"));
                        }
                    }
                    logger.error("Applicant Update failed for applicationId: {}, customerId: {}. Response: {}",
                            applicationId, customerId, t24Resp);
                    saveLog(applicationId, customerTypeStr + " " + "Sourcing T24 Updation", requestObj.toString(), t24Resp.toString(),
                            ResponseCodes.FAILURE.getValue(), t24Resp.toString(), applicationStatus);
                    return Mono.just(adapterUtil.setError("Error while updating dedupe details in T24. Please try again.", "1"));
                } catch (Exception e) {
                    logger.error(
                            "Error while updating dedupe details in T24 for applicationId: {}, customerId: {}. Error: {}",
                            applicationId, customerId, e.getMessage(),
                            e
                    );
                    saveLog(applicationId, customerTypeStr + " " + "Sourcing T24 Updation", t24Resp.toString(),
                            t24Resp.toString(), ResponseCodes.FAILURE.getValue(), t24Resp.toString(), applicationStatus);
                    return Mono.just(adapterUtil.setError("Error while updating dedupe details in T24. Please try again.", "1"));
                }
            });
        } else {
            logger.debug("No Application Master record found for applicationId: {}. Proceeding with dedupe update in T24/CDH", applicationId);

            logger.error("Error while updating dedupe details in CDH and T24 for applicationId: {}, customerId: {}. Error: {}",
                    applicationId, customerId, "No Application Master record found for applicationId: " + applicationId);
            return Mono.just(adapterUtil.setError("Error while updating dedupe details in CDH and T24. Please try again.", "1"));
        }
    }

    public Mono<Object> dedupeUpdateCDH(String customerId,
                                        String applicationId,
                                        String appId,
                                        String productCode,
                                        Header header, Properties prop, String customerType) {
        logger.debug("Inside dedupeUpdateCDH for applicationId: {}, customerId: {}", applicationId, customerId);
        List<ApplicationMaster> appMasterOpt = applicationMasterRepo.findByAppIdAndApplicationId(Constants.APPID, applicationId);
        if(appMasterOpt.isEmpty()) {
            logger.error("No Application Master record found for applicationId: {}. Cannot proceed with CDH dedupe update.", applicationId);
            saveLog(applicationId, customerType + " " + "Sourcing Dedupe Updation", "" , "No Application Master record found for applicationId: " + applicationId,
                    ResponseCodes.FAILURE.getValue(), "No Application Master record found for applicationId: " + applicationId, "N/A");
            return Mono.just(adapterUtil.setError("Error while updating dedupe details in CDH. Please try again. Application Master not found", "1"));
        }
        ApplicationMaster appMaster = appMasterOpt.get(0);
        String applicationStatus = appMaster.getApplicationStatus();
        boolean isCoapp = Constants.COAPPLICANT.equalsIgnoreCase(customerType);
        Mono<Object> dedupeUpdateResp = dedupeTableUpdate(appMaster, header, prop, customerId, isCoapp);
        return dedupeUpdateResp.flatMap(dedupeResp -> {
            logger.debug("Dedupe table update response: {}", dedupeResp);
            try {
                String jsonResp = (new Gson()).toJson(dedupeResp);
                JSONObject dedupeRes = CommonUtils.convertResponseToJson(jsonResp);
                Object requestObj = dedupeRes.get(Constants.API_REQUEST);
                if (dedupeRes.has("response") && dedupeRes.has("msg")) {
                    String response = dedupeRes.getString("response");
                    String msg = dedupeRes.getString("msg");

                    if (Constants.SUCCESS.equalsIgnoreCase(response)) {
                        if ("Details Updated".equalsIgnoreCase(msg)) {
                            saveLog(applicationId, customerType + " " + "Sourcing Dedupe Updation", requestObj.toString(), dedupeResp.toString(),
                                    ResponseCodes.SUCCESS.getValue(), null, applicationStatus);
                            logger.debug("Applicant Update started");
                            return Mono.just(adapterUtil.setSuccessResp(dedupeResp.toString()));
                        }
                    } else {
                        Object resultObj = dedupeRes.opt("result");
                        String existingCustId = null;

                        try {
                            if (resultObj instanceof JSONObject) {
                                JSONObject resultJson = (JSONObject) resultObj;
                                existingCustId = resultJson.optString("customer_id", null);
                            } else if (resultObj instanceof JSONArray) {
                                JSONArray resultArr = (JSONArray) resultObj;
                                if (resultArr.length() > 0) {
                                    JSONObject first = resultArr.getJSONObject(0);
                                    existingCustId = first.optString("customer_id", null);
                                }
                            }
                        } catch (Exception ex) {
                            logger.warn("Unexpected 'result' format in dedupe response", ex);
                        }

                        String errorMsg = "Dedupe Table Update failed: " + msg +
                                (existingCustId != null ? ", Existing Customer ID: " + existingCustId : "");
                        saveLog(applicationId, customerType + " " + "Sourcing Dedupe Updation",  requestObj.toString(), dedupeRes.toString(),
                                ResponseCodes.FAILURE.getValue(), errorMsg, applicationStatus);
                        return Mono.just(adapterUtil.setError(errorMsg, "1"));
                    }
                }
                logger.error("Invalid response format from dedupe table update: {}", dedupeRes);
                saveLog(applicationId, customerType + " " + "Sourcing Dedupe Updation",  requestObj.toString(), dedupeRes.toString(),
                        ResponseCodes.FAILURE.getValue(), "Invalid response format from dedupe table update", applicationStatus);
                return Mono.just(adapterUtil.setError("Error while updating dedupe details . Please try again.", "1"));
            } catch (Exception e) {
                logger.error("Error while updating dedupe details in T24 for applicationId: {}, customerId: {}. Error: {}",
                        applicationId, customerId, e.getMessage());
                saveLog(applicationId, customerType + " " + "Sourcing Dedupe Updation", getRequestLog(), dedupeResp.toString(),
                        ResponseCodes.FAILURE.getValue(), dedupeResp.toString(), applicationStatus);
                return Mono.just(adapterUtil.setError("Error while updating dedupe details. Please try again.", "1"));
            }
        }).map(obj -> (Object) obj).onErrorResume(e -> {
            logger.error("Error during dedupe reverse feed", e);
            String frontendMsg = "Error during dedupe reverse feed";
            if (e.getMessage() != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(e.getMessage());
                    logger.debug("rootNode: {}", rootNode.toString());
                    JsonNode msgNode = rootNode
                            .path("errorMessage")
                            .path("msg");
                    logger.debug("msgNode: {}", msgNode.toString());
                    if (!msgNode.isMissingNode() && !msgNode.asText().trim().isEmpty()) {

                        frontendMsg = msgNode.asText();
                        String errorCustomerId = null;
                        Pattern pattern = Pattern.compile(
                                "(?i)(?:customer\\s*id[^0-9]*([0-9]{4,})|([0-9]{4,})[^a-zA-Z]*customer\\s*id)"
                        );
                        Matcher matcher = pattern.matcher(frontendMsg);

                        if (matcher.find()) {
                            logger.debug("Matcher found in error message: {}", frontendMsg);
                            errorCustomerId =
                                    matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
                            logger.debug("Extracted Customer ID from error message: {}", errorCustomerId);
                            if (errorCustomerId.equalsIgnoreCase(customerId)) {
                                logger.debug("dedupe Update skipped for customerId: {}, since same ID recieved in response {}", customerId, frontendMsg);
                                saveLog(applicationId, customerType + " " + "Sourcing Dedupe Updation", getRequestLog(), e.toString(),
                                        ResponseCodes.SUCCESS.getValue(), null, applicationStatus);
                                return Mono.just(adapterUtil.setSuccessResp(e.toString()));
                            }
                            if (ProductCode.UNNATI_RENEW.getUnnatiCode().equalsIgnoreCase(appMaster.getProductCode())) {
                                if(isCoapp){
                                    String leadCoApplicantId = null;
                                    List<UnnatiCoApplicantDetails> CoApplicantData = coAppDetailsRepository.findByCustomerId(appMaster.getSearchCode2());
                                    if (!CoApplicantData.isEmpty()) {
                                        leadCoApplicantId = CoApplicantData.get(0).getCoCustomerId();
                                        if(StringUtils.isNotBlank(leadCoApplicantId) && errorCustomerId.equalsIgnoreCase(leadCoApplicantId)){
                                            logger.debug("dedupe Update skipped for co-applicant customerId: {}, since same ID recieved in response {}", customerId, frontendMsg);
                                            saveLog(applicationId, customerType + " " + "Sourcing Dedupe Updation", getRequestLog(), e.toString(),
                                                    ResponseCodes.SUCCESS.getValue(), null, applicationStatus);
                                            return Mono.just(adapterUtil.setSuccessResp(e.toString()));
                                        }
                                    }
                                }
                            }
                            if (errorCustomerId.equalsIgnoreCase(customerId)) {
                                if (frontendMsg.toUpperCase().contains("PHONEDEDUPE")) {
                                    logger.debug("dedupe Update skipped for customerId: {}, since applicant/coapplicant id recieved in the response {}", customerId, frontendMsg);
                                    saveLog(applicationId, customerType + " " + "Sourcing Dedupe Updation", getRequestLog(), e.toString(),
                                            ResponseCodes.SUCCESS.getValue(), null, applicationStatus);
                                    return Mono.just(adapterUtil.setSuccessResp(e.toString()));
                                }
                            } else {
                                saveLog(applicationId, customerType + " " + "Sourcing Dedupe Updation", getRequestLog(), e.getMessage(), ResponseCodes.FAILURE.getValue(), frontendMsg, applicationStatus);
                                return Mono.just(adapterUtil.setError(frontendMsg, "1"));
                            }
                        } else {
                            logger.debug("No Customer ID found in error message: {}", frontendMsg);
                        }
                    }
                } catch (Exception ex) {
                    logger.warn("Failed to parse dedupe reverse feed error message", ex);
                }
            }
            saveLog(applicationId, customerType + " " + "Sourcing Dedupe Updation", getRequestLog(), e.getMessage(), ResponseCodes.FAILURE.getValue(), frontendMsg, applicationStatus);
            return Mono.just(adapterUtil.setError(frontendMsg, "1"));
        });
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
        String applicantInsurancePremium = "";
        String coApplicantInsurancePremium = "";
        String processingFees = "";
        String processingFeesTax = "";
        String mappedProduct = "";
        Gson gsonObj = new Gson();
        try {

            CustomerDataFields custFields = null;
            ApplicationMaster applicationMasterData = null;
            List<ApplicationMaster> appMasterDb = applicationMasterRepo
                    .findByAppIdAndApplicationId(Constants.APPID, applicationId);

            if (null != appMasterDb && !appMasterDb.isEmpty()) {
                for (ApplicationMaster appMaster : appMasterDb) {
                    applicationMasterData = appMaster;
                }
                String product = applicationMasterData.getProductCode();
                logger.debug("product :" + product);


                mappedProduct = ProductCode.getCdhCodeByUnnatiCode(product);
                if(ProductCode.OPEN_MARKET.getUnnatiCode().equalsIgnoreCase(product)){
                    mappedProduct = ProductCode.OPEN_MARKET.getUnnatiCode();
            }
                Optional<UnnatiIexceedCDHLead> cdhLeadOpt = unnatiIexceedCDHLeadRepo
                        .findByCustomerIdAndProductAndReferenceId(applicationMasterData.getSearchCode2(), mappedProduct, applicationMasterData.getWorkitemNo());
                if (!cdhLeadOpt.isPresent()) {
                    logger.error("CDH Lead not found for customerId: {}, product: {}, referenceId: {}",
                            applicationMasterData.getSearchCode2(), mappedProduct, applicationMasterData.getWorkitemNo());
                    saveLog(applicationId, "Loan Creation", "", "CDH Lead not found for customerId: " + applicationMasterData.getSearchCode2()
                                    + ", product: " + mappedProduct + ", referenceId: " + applicationMasterData.getWorkitemNo(),
                            ResponseCodes.FAILURE.getValue(), "CDH Lead not found", applicationMasterData.getApplicationStatus());
                    return Mono.just(adapterUtil.setError("CDH Lead not found for customerId: " + applicationMasterData.getSearchCode2() + ", product: " + mappedProduct + ", referenceId: " + applicationMasterData.getWorkitemNo(), "1"));
                }

                UnnatiIexceedCDHLead cdhLead = cdhLeadOpt.get();

                if ("1003".equalsIgnoreCase(product)) {
                    mappedProduct = "GL.UNNATI.LITE";
                }
                if (product.equalsIgnoreCase(Constants.RENEWAL_PRODUCT_CODE)) {
                    mappedProduct = Constants.UNNATI_LOAN_PRODUCT;
                }

                logger.debug("Final mapped product code: {}", mappedProduct);


                custFields = cobService.getCustomerData(applicationMasterData, applicationId, Constants.APPID, 1);


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
                if (null != custFields.getInsuranceDetailsWrapperList()) {
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
                }

                CibilDetailsWrapper resolvedWrapper = CommonUtils.resolveCibilWrapper(
                        custFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId);
                if (resolvedWrapper != null) {
                    cibilPayload = gsonObj.fromJson(
                            resolvedWrapper.getCibilDetails().getPayloadColumn(), CibilDetailsPayload.class);
                logger.debug("CreditDetailsPayload Payload : " + cibilPayload);
                    LocalDate cbDate = resolvedWrapper.getCibilDetails().getCbDate();
                    cbDateStr = cbDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                    coAppFoir = cibilPayload.getFoir();
                    applicantInsurancePremium = cibilPayload.getInsuranceChargeMember();
                    coApplicantInsurancePremium = cibilPayload.getInsuranceChargeSpouse();
                    processingFees = cibilPayload.getProcessingFeesWithoutGST();
                    processingFeesTax = cibilPayload.getGstOnProcessingFees();
                }

            LoanCreationReqFields loanRequest = new LoanCreationReqFields();
                logger.debug("Mapped product code: " + mappedProduct);
                loanRequest.setProduct(mappedProduct);

                logger.debug("After mapped: " + loanRequest.getProduct());

            String borrowerInsurance = insuranceRequiredApplicant ? "YES" : "NO";

            String jointInsurance = insuranceRequiredApplicant
                    && insuranceRequiredCoapplicant ? "YES" : "NO";

            logger.debug("Computed borrowerInsurance: '{}'", borrowerInsurance);
            logger.debug("Computed jointInsurance: '{}'", jointInsurance);

            LoanDetails loanDetails = custFields.getLoanDetails();
                BigDecimal weeks = BigDecimal.valueOf(loanDetails.getTenure())
                        .multiply(BigDecimal.valueOf(52))
                        .divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);

                String yearInWeeks = weeks.toPlainString() + Constants.TERM_WEEK;

            LoanDetailsPayload loanPayload = gson.fromJson(loanDetails.getPayloadColumn(), LoanDetailsPayload.class);

            Optional<LovMaster> subPurposeId = lovMasterRepository.findById(loanDetailsLovId);
            logger.debug("Subpurpose Id :" + subPurposeId.get().getLovDtls().toString());
                String purposeId = findCategoryId(subPurposeId.get().getLovDtls().toString(),
                        loanPayload.getSubCategory().replace("_", "."), prop);

            // As per latest discussion - 19/05/2025
            Map<String, Integer> coCus = new HashMap<>();
                if (null != coApplicantId) {
            coCus.put("ltUnniCoCus", Integer.parseInt(coApplicantId));
                }
            List<Map<String, Integer>> unnatiCoCustomerList = new ArrayList<>();
            unnatiCoCustomerList.add(coCus);
            String applicantId = applicationMasterData.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE) ?
                    applicationMasterData.getApplicantT24Id() : applicationMasterData.getMemberId();
            loanRequest.setApplicantId(applicantId);
            loanRequest.setUnnatiCoCustomer(unnatiCoCustomerList);

                //if unnati, unnati renewal fetch UNNATI_LOAN_PRODUCT

                if (applicationMasterData.getProductCode() != null && (applicationMasterData.getProductCode() == "1009" ||
                        applicationMasterData.getProductCode() == "1002")) {
                    logger.debug("For unnati, unnati renewal: " + applicationMasterData.getProductCode());
            loanRequest.setProduct(Constants.UNNATI_LOAN_PRODUCT);
                    logger.debug("Loan request product set to: {}", loanRequest.getProduct());
                }

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
                String productCode = applicationMasterData.getProductCode();
                ProductCode product1 = null;
                for (ProductCode pc : ProductCode.values()) {
                    if (pc.getUnnatiCode().equalsIgnoreCase(productCode)) {
                        product1 = pc;
                        break;
                    }
                }

                if (product1 != null) {
                    switch (product1) {
                        case UNNATI:
                        case OPEN_MARKET:
                        case UNNATI_RESTART:
                            loanRequest.setFoir(cdhLead.getFoir());
                        case VISHESH:
                            loanRequest.setPreCloseType(prop.getProperty(CobFlagsProperties.UNNATI_PRE_CLOSE_TYPE.getKey()));
                            break;

                        case UNNATI_RENEW:
                        case UNNATI_EMERGENCY:
                        case FAMILY_WELFARE:
                        case UNNATI_SUPPLEMENTARY:
                loanRequest.setPreCloseType(prop.getProperty(CobFlagsProperties.RENEWAL_PRE_CLOSE_TYPE.getKey()));
                            break;

                        default:
                            break;
            }
                }

            loanRequest.setPayoffAccount(""); // NA
            loanRequest.setCompanyIdTemp(applicationMasterData.getBranchId());
            String eir = cibilPayload.getEir();
            if (eir != null) {
                annualPercentageRate = eir.replace("%", "").trim();
            }

            loanRequest.setAnnualPercentageRate(annualPercentageRate);
                loanRequest.setFamilyIncome(cdhLead.getAnnualIncome());

                Set<ProductCode> additionalProducts = EnumSet.of(ProductCode.FAMILY_WELFARE, ProductCode.UNNATI_SUPPLEMENTARY,
                        ProductCode.UNNATI_RESTART, ProductCode.UNNATI_EMERGENCY);
                ApplicationMaster finalApplicationMasterData = applicationMasterData;
                boolean isAdditionalProduct = additionalProducts.stream()
                        .anyMatch(productCode1 -> productCode1.getUnnatiCode().equalsIgnoreCase(finalApplicationMasterData.getProductCode()));
                if (!isAdditionalProduct) {
            Optional<BCMPIIncomeDetails> incomeDetails = bcmpiIncomeDetailsRepo.findById(applicationId);
            if (incomeDetails.isPresent()) {
                BCMPIIncomeDetailsWrapper incomeDetailsWrapper = gson.fromJson(incomeDetails.get().getPayload(), BCMPIIncomeDetailsWrapper.class);
                BigDecimal fieldAssessedIncome = incomeDetailsWrapper.getFieldAssessedIncome();
                BigDecimal selfDeclaredIncome = incomeDetailsWrapper.getTotalDeclaredIncome();
                BigDecimal familyIncome = fieldAssessedIncome.min(selfDeclaredIncome);
                BigDecimal familyIncomeMultiply = familyIncome.multiply(BigDecimal.valueOf(12));

                familyIncomeStr = String.valueOf(familyIncomeMultiply);
            } else {
                logger.debug("Income details not present for application_id : {}", applicationId);
                return Mono.just(adapterUtil.setError("Income details not present for application Id : " + applicationId, "1"));
            }
            loanRequest.setFamilyIncome(familyIncomeStr);
                }

            loanRequest.setMemberIns(applicantInsurancePremium);
            loanRequest.setCoBorrowerIns(coApplicantInsurancePremium);
            loanRequest.setProcsFees(processingFees);
            loanRequest.setProcsFeeTax(processingFeesTax);

            Optional<BCMPIOtherDetails> caOtherDetails = bcmpiOtherDetailsRepo.findById(applicationId);
            if (caOtherDetails.isPresent()) {
                BCMPIOtherDetailsWrapper otherDetailsWrapper = gson.fromJson(caOtherDetails.get().getPayload(), BCMPIOtherDetailsWrapper.class);
                earningMembers = otherDetailsWrapper.getNoOfOtherEarningMembers();

            }

            String earningMembersNumber = earningMembers.replaceAll("[^0-9]", "");
            loanRequest.setEarningMembers(earningMembersNumber);
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
                apiReportResp.put(Constants.API_REQUEST, loanCreationRequestExt.toString());
                return Mono.just(apiReportResp);
            });
            }else{
                logger.error("No Application Master record found for applicationId: {}. Cannot proceed with Loan Creation.", applicationId);
                saveLog(applicationId, "Loan Creation", "", "No Application Master record found for applicationId: " + applicationId,
                        ResponseCodes.FAILURE.getValue(), "No Application Master record found for applicationId: " + applicationId, "N/A");
                return Mono.just(adapterUtil.setError("Error while executing the Loan Creation api. Application Master not found for applicationId: " + applicationId, "1"));
            }

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
                apiResp.put(Constants.API_REQUEST, loanRequestExt.toString());
                logger.debug("JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });
        } catch (Exception e) {
            logger.error("Error occurred while executing the Loan Rejection api", e);
            return Mono.just(getFailureApiJson("Error occurred while executing the Loan Rejection api", Constants.LOAN_REJECTION));
        }
    }

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
                apiResp.put(Constants.API_REQUEST, loanRequestExt.toString());
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
                apiResp.put(Constants.API_REQUEST, loanRequestExt.toString());
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

            CustomerDataFields custFields = null;

            ApplicationMaster applicationMasterData = null;
            List<ApplicationMaster> appMasterDb = applicationMasterRepo.findByAppIdAndApplicationId(Constants.APPID, applicationId);

            if (null != appMasterDb && !appMasterDb.isEmpty()) {
                for (ApplicationMaster appMaster : appMasterDb) {
                    applicationMasterData = appMaster;
                }
                String product = applicationMasterData.getProductCode();
                logger.debug("product :" + product);
                custFields = cobService.getCustomerData(applicationMasterData, applicationId, Constants.APPID, Constants.INITIAL_VERSION_NO);
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

            cibilPayload = CommonUtils.resolveCibilPayload(custFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId, gsonObj);

            LoanDetails loanDetails = custFields.getLoanDetails();

            BigDecimal weeks = BigDecimal.valueOf(loanDetails.getTenure())
                    .multiply(BigDecimal.valueOf(52))
                    .divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);

            String yearInWeeks = weeks.toPlainString();

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

            loanReq.put(Constants.CUSTOMERID1, applicationMasterData.getMemberId());
            loanReq.put("productID", ProductCode.getCdhCodeByUnnatiCode(applicationMasterData.getProductCode()));
            if (applicationMasterData.getProductCode().equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)) {
                loanReq.put(Constants.CUSTOMERID1, applicationMasterData.getApplicantT24Id());
            } else if (applicationMasterData.getProductCode().equalsIgnoreCase(Constants.RENEWAL_PRODUCT_CODE)) {
                loanReq.put("productID", Constants.UNNATI_LOAN_PRODUCT);
            }else if(applicationMasterData.getProductCode().equalsIgnoreCase(ProductCode.VISHESH.getUnnatiCode())){
                loanReq.put("productID", Constants.UNNATI_LITE_PRODUCT_CODE);
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
                apiResp.put(Constants.API_REQUEST, loanRequestExt.toString());
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

            CustomerDataFields custFields = null;
            ApplicationMaster applicationMasterData = null;
            List<ApplicationMaster> appMasterDb = applicationMasterRepo
                    .findByAppIdAndApplicationId(Constants.APPID, applicationId);

            if (null != appMasterDb && !appMasterDb.isEmpty()) {
                for (ApplicationMaster appMaster : appMasterDb) {
                    applicationMasterData = appMaster;
                }
                String product = applicationMasterData.getProductCode();
                logger.debug("product :" + product);
                custFields = cobService.getCustomerData(applicationMasterData, applicationId, Constants.APPID, 1);
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
                apiResp.put(Constants.API_REQUEST, loanRequestExt.toString());
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
                apiResp.put(Constants.API_REQUEST, loanRequestExt.toString());
                logger.debug("JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });

        } catch (Exception e) {
            logger.error("Error occurred while executing the Loan Repyament Disbursement api", e);
            return Mono.just(getFailureApiJson("Error occurred while executing the Loan Repyament Disbursement api",
                    Constants.LOAN_DISBURSEMENT));
        }
    }

    public Mono<Object> dedupeTableUpdate(ApplicationMaster master,
                                          Header header, Properties prop,
                                          String targetCustomerId, boolean isCoapp) {
        logger.debug("Dedupe Table Update Started");
        Gson gson = new Gson();
        String updateApi = isCoapp ? Constants.COAPPLICANT_DEDUPE_UPDATE : Constants.APPLICANT_DEDUPE_UPDATE;
        try {
            String customerType = isCoapp ? Constants.COAPPLICANT : Constants.APPLICANT;
            String applicationId = master.getApplicationId();

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

            if(isCoapp && request.getRecordtype().equalsIgnoreCase("PROSPECT")){
                String coApplicantId = loanDtlsRepo.fetchCoapplicantIdByApplicationId(applicationId);
                if(!StringUtils.isBlank(coApplicantId)){
                    request.setCustomerId(coApplicantId);
                    request.setRecordtype("ACTIVE");
                    logger.debug("Using coapplicant id instead of prospect id since coApplicant ID exists");
                }else{
                    logger.debug("Using prospect id since coApplicant ID does not exist");
                }
            }

            request.setCustqualify("CREDIT.IL");

            Optional<CustomerDetails> customerOpt = custDtlRepo
                    .findByApplicationIdAndCustomerType(master.getApplicationId(), customerType);
            if (!customerOpt.isPresent()) {
                logger.debug("Customer details not found for type: {}", customerType);
                return Mono.just(getFailureApiJson("Customer details not found.", updateApi));
            }
            CustomerDetails customerDetails = customerOpt.get();
            CustomerDetailsPayload custPayload = gson.fromJson(customerDetails.getPayloadColumn(), CustomerDetailsPayload.class);
            logger.debug("CustomerDetailsPayload: {}", custPayload);

            String unnatiProductCode = master.getProductCode();
            String maitriProductCode = ProductCode.getCdhCodeByUnnatiCode(unnatiProductCode);
            if(ProductCode.OPEN_MARKET.getUnnatiCode().equalsIgnoreCase(unnatiProductCode)) {
                maitriProductCode = unnatiProductCode;
                }
            if (maitriProductCode == null) {
                logger.debug("Unknown product code for dedupe update: {}", master.getProductCode());
                return Mono.just(getFailureApiJson("Unknown product code.", updateApi));
                }
            logger.debug("Caling CDH");
            Optional<UnnatiIexceedCDHLead> optIexceedCDHLeadOpt = unnatiIexceedCDHLeadRepo
                    .findByCustomerIdAndProductAndReferenceId(master.getSearchCode2(), maitriProductCode, master.getWorkitemNo());

            if (!optIexceedCDHLeadOpt.isPresent()) {
                logger.debug("Iexceed CDH Lead details not found for memberId: {} and product_code : {}", master.getSearchCode2(), maitriProductCode);
                    return Mono.just(getFailureApiJson("Iexceed CDH Lead details not found.", updateApi));
                }
            UnnatiIexceedCDHLead iexceedCDHLead = optIexceedCDHLeadOpt.get();
                request.setKendraId(iexceedCDHLead.getKendraId());
                request.setGroupId(iexceedCDHLead.getGroupId());
                request.setBranchId(iexceedCDHLead.getGlBranchId());

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
                if (custPayload.getMaritalStatus().equalsIgnoreCase(Constants.MARRIED)
                        && custPayload.getRelationShipWithApplicant().equalsIgnoreCase(Constants.SPOUSE)) {
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
//                    request.setSpkycid(apptKycNo.toUpperCase());
//                    request.setSpkycname("VOTER-ID");
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
                if (coApplicantCustPayload.getMaritalStatus().equalsIgnoreCase(Constants.MARRIED)
                        && coApplicantCustPayload.getRelationShipWithApplicant().equalsIgnoreCase(Constants.SPOUSE)) {
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
//                    request.setSpkycid(coappKycNo.toUpperCase());
//                    request.setSpkycname("VOTER-ID");
                }
                Optional<BankDetails> bankDetailsOpt = bankDtlRepo
                        .findByApplicationIdAndCustDtlId(master.getApplicationId(), customerDetails.getCustDtlId());
                if (bankDetailsOpt.isPresent()) {
                    BankDetails bankDetails = bankDetailsOpt.get();
                    BankDetailsPayload bankPayload = gson.fromJson(bankDetails.getPayloadColumn(), BankDetailsPayload.class);
                    logger.debug("BankDetailsPayload: {}", bankPayload);
                    if(StringUtils.isNotBlank(bankPayload.getAccountNumber())){
                    request.setBankAccNo(bankPayload.getAccountNumber());
                    request.setBankname(bankPayload.getBankName());
                    request.setBankBranchName(bankPayload.getBranchName());
                    request.setIfscCode(bankPayload.getIfsc());
                    request.setAccHolderName(bankPayload.getAccountName());
                }
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
                apiResp.put(Constants.API_REQUEST, loanCreationRequestExt.toString());
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
            String maitriProductCode = ProductCode.getCdhCodeByUnnatiCode(master.getProductCode());
            if(ProductCode.OPEN_MARKET.getUnnatiCode().equalsIgnoreCase(master.getProductCode())) {
                maitriProductCode = master.getProductCode();
            }
            Optional<UnnatiIexceedCDHLead> cdhLeadOpt = unnatiIexceedCDHLeadRepo
                    .findByCustomerIdAndProductAndReferenceId(master.getSearchCode2(),maitriProductCode, master.getWorkitemNo());
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
                cbDetailsOpt = cibilDtlRepo.findCibilDetailsByCustomerTypeAndApplicationId(Constants.APPLICANT, applicationId);
                if (!cbDetailsOpt.isPresent()) {
                    logger.error("CIBIL details not found for applicationId: {}", applicationId);
                return Mono.error(new RuntimeException("BRE details not found"));
            }
            }

            if (StringUtils.isBlank(cbDetailsOpt.get().getRequest())) {
                logger.warn("Co-Applicant BRE request is null/blank, falling back to Applicant CibilDetails");
                cbDetailsOpt = cibilDtlRepo.findCibilDetailsByCustomerTypeAndApplicationId(
                        Constants.APPLICANT, applicationId);
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
                apiResp.put(Constants.API_REQUEST, loanCreationRequestExt.toString());
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

    public Mono<Object> coapplicantCreation(
            String applicationId,
            String appId,
            String memId,
            Header header,
            Properties prop,
            boolean updateCall,
            String coapplCreationId,
            boolean coapplUpdate) {

        logger.debug("Create & Updated Co Applicant Started");
        Gson gsonObj = new Gson();
        try {
            CustomerDetailsPayload coappPayload = new CustomerDetailsPayload();
            OccupationDetails coApplicant = new OccupationDetails();
            OccupationDetailsPayload occAppPayload = new OccupationDetailsPayload();
            OccupationDetailsPayload occCoappPayload = new OccupationDetailsPayload();
            String applicantCustId = "";
            String coApplicantCustId = "";
            String appCustName = "";
            String appPhnNum = "";
            String appGender = "";
            String appVoterId = "";
            String coAppName = "";
            String coAppPhnNum = "";
            String coAppGender = "";
            String coAppVoterId = "";
            String appStoredDOB = "";
            String coAppStoredDOB = "";
            String appMaritalStatus = "";
            String coAppMaritalStatus = "";
            String appFirstName = "";
            String coAppFirstName = "";
            String appLastName = "";
            String coAppLastName = "";
            CustomerDetailsPayload appPayload = null;
            CustomerDataFields custFields = null;
            ApplicationMaster applicationMasterData = null;

            boolean useCoApplicant = !updateCall || coapplUpdate;
            boolean useApplicant = updateCall && !coapplUpdate;

            List<ApplicationMaster> appMasterDb = applicationMasterRepo
                    .findByAppIdAndApplicationId(Constants.APPID, applicationId);

            if (null != appMasterDb && !appMasterDb.isEmpty()) {
                for (ApplicationMaster appMaster : appMasterDb) {
                    applicationMasterData = appMaster;
                }
                String product = applicationMasterData.getProductCode();
                logger.debug("product :" + product);
                custFields = cobService.getCustomerData(applicationMasterData, applicationId, Constants.APPID, 1);
            }
            for (CustomerDetails custDtl : custFields.getCustomerDetailsList()) {

                logger.debug("customer Type : " + custDtl.getCustomerType());
                if (custDtl.getCustomerType().equalsIgnoreCase("Applicant")) {
                    applicantCustId = String.valueOf(custDtl.getCustDtlId());
                    logger.debug("applicantCustId : " + applicantCustId);
                    appPayload = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    logger.debug("custApplicantPayload :" + appPayload);
                    appCustName = custDtl.getCustomerName();
                    appFirstName = appPayload.getFirstName();
                    appPhnNum = custDtl.getMobileNumber();
                    appGender = appPayload.getGender();
                    appVoterId = "";
                    appStoredDOB = appPayload.getDob().replace("-", "");
                    appLastName = StringUtils.isNotBlank(appPayload.getLastName()) ? appPayload.getLastName() : "";
                    appMaritalStatus = appPayload.getMaritalStatus();
                    boolean isAlternateVerified =
                            Constants.VERIFIED_STS.equalsIgnoreCase(appPayload.getAlternateVoterIdValStatus());
                    boolean hasAlternateId =
                            StringUtils.isNotBlank(appPayload.getAlternateVoterId());

                    boolean isPrimaryVerified =
                            Constants.VERIFIED_STS.equalsIgnoreCase(appPayload.getPrimaryKycIdValStatus());
                    boolean hasPrimaryId =
                            StringUtils.isNotBlank(appPayload.getPrimaryKycId());

                    if (isAlternateVerified && hasAlternateId) {

                        // Highest priority
                        appVoterId = appPayload.getAlternateVoterId();

                    } else if (isPrimaryVerified && hasPrimaryId) {

                        // Fallback
                        appVoterId = appPayload.getPrimaryKycId();

                    } else {
                        logger.debug(
                                "No valid verified Voter ID found for applicant. " +
                                        "Alternate verified={}, Alternate blank={}, " +
                                        "Primary verified={}, Primary blank={}",
                                isAlternateVerified, !hasAlternateId,
                                isPrimaryVerified, !hasPrimaryId
                        );

                        return Mono.error(new RuntimeException(
                                "No valid verified Voter ID found for applicant."
                        ));
                    }

                } else if (custDtl.getCustomerType().equalsIgnoreCase(Constants.COAPPLICANT)) {
                    coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
                    logger.debug("coApplicantCustId : " + coApplicantCustId);
                    coappPayload = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    coAppName = custDtl.getCustomerName();
                    coAppFirstName = coappPayload.getFirstName();
                    coAppPhnNum = custDtl.getMobileNumber();
                    coAppGender = coappPayload.getGender();
                    coAppVoterId = "";
                    coAppStoredDOB = coappPayload.getDob().replace("-", "");
                    coAppLastName = StringUtils.isNotBlank(coappPayload.getLastName()) ? coappPayload.getLastName() : "";
                    coAppMaritalStatus = coappPayload.getMaritalStatus();
                    boolean isAlternateVerified =
                            Constants.VERIFIED_STS.equalsIgnoreCase(coappPayload.getAlternateVoterIdValStatus());
                    boolean hasAlternateId =
                            StringUtils.isNotBlank(coappPayload.getAlternateVoterId());

                    boolean isPrimaryVerified =
                            Constants.VERIFIED_STS.equalsIgnoreCase(coappPayload.getPrimaryKycIdValStatus());
                    boolean hasPrimaryId =
                            StringUtils.isNotBlank(coappPayload.getPrimaryKycId());
                    if (isAlternateVerified && hasAlternateId) {

                        // Highest priority
                        coAppVoterId = coappPayload.getAlternateVoterId();

                    } else if (isPrimaryVerified && hasPrimaryId) {

                        // Fallback
                        coAppVoterId = coappPayload.getPrimaryKycId();

                    } else {
                        logger.debug(
                                "No valid verified Voter ID for co-applicant. " +
                                        "Alternate verified={}, Alternate blank={}, " +
                                        "Primary verified={}, Primary blank={}",
                                isAlternateVerified, !hasAlternateId,
                                isPrimaryVerified, !hasPrimaryId
                        );

                        return Mono.error(new RuntimeException(
                                "No valid verified Voter ID found for co-applicant."
                        ));
                    }
                    logger.debug("custCo-ApplicantPayload :" + coappPayload);
                }
            }
//            String occup = normalize(appPayload.getOccupation());
//            for (OccupationDetailsWrapper applicantwrpr : custFields.getOccupationDetailsWrapperList()) {
//                logger.debug("applicantCustId " + applicantCustId);
//                if (String.valueOf(applicantwrpr.getOccupationDetails().getCustDtlId()).equals(applicantCustId)) {
//                    occAppPayload = gsonObj.fromJson(applicantwrpr.getOccupationDetails().getPayloadColumn(),
//                            OccupationDetailsPayload.class);
//                    logger.debug("occupationApplicantPayload : " + occAppPayload.toString());
//                } else if (String.valueOf(applicantwrpr.getOccupationDetails().getCustDtlId())
//                        .equals(coApplicantCustId)) {
//                    coApplicant = applicantwrpr.getOccupationDetails();
//                    occCoappPayload = gsonObj.fromJson(applicantwrpr.getOccupationDetails().getPayloadColumn(),
//                            OccupationDetailsPayload.class);
//                    logger.debug("occupationCo-appliocantPayload : " + occCoappPayload.toString());
//                }
//            }
            String relation = prop.getProperty(CobFlagsProperties.RELATIONSHIP_CODES.getKey());

            String coappRelation = "";
            JSONObject relationshipCodes = new JSONObject(relation);

            String inputRelation = normalize(StringUtils.isEmpty(coappPayload.getRelationShipWithApplicant()) ? "" :
                    coappPayload.getRelationShipWithApplicant());

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

            // First Name
            CustomerFirstNameRequestField firstNameObj = new CustomerFirstNameRequestField();
            firstNameObj.setFirstName(useCoApplicant ?
                    (StringUtils.isNotBlank(coAppFirstName) ? coAppFirstName : coAppName) : (StringUtils.isNotBlank(appFirstName) ? appFirstName : appCustName));
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
            req.setDateOfBirth(useCoApplicant ? Long.parseLong(coAppStoredDOB) : Long.parseLong(appStoredDOB));
            req.setGender(useCoApplicant ? coAppGender.toUpperCase() : appGender.toUpperCase());
            req.setMaritalstatus(useCoApplicant ? coAppMaritalStatus.toUpperCase() : appMaritalStatus.toUpperCase());
            req.setVoterId(useCoApplicant ? coAppVoterId : appVoterId);

            // Basic info
//            String title = useCoApplicant ? coappPayload.getTitle() : appPayload.getTitle();
//            req.setTitle(title.endsWith(".") ? title : title + ".");
            req.setFamilyName("");

            // RationCard Pan DL Passport
            String secKycType = useCoApplicant ? coappPayload.getSecondaryKycType() : appPayload.getSecondaryKycType();
            String secKycId = useCoApplicant ? coappPayload.getSecondaryKycId() : appPayload.getSecondaryKycId();
            String kyc = secKycType != null ? secKycType.toLowerCase() : "";
            req.setRationCardNumber("");
            req.setPanIdNumber(kyc.contains("pan") ? secKycId : "");
            req.setDlNumber(kyc.contains("driving") || kyc.contains("license") ? secKycId : "");
            req.setPassport(kyc.contains("passport") ? secKycId : "");
            req.setOtherGovernmentId("");

            //BSN
            String bsn = prop.getProperty(CobFlagsProperties.BUSINESS_CODES.getKey());
            JSONObject js = new JSONObject(bsn);

            String loanPurpose = "";
            LoanDetails loanDet = loanDtlsRepo.findByApplicationId(applicationId);
            if(loanDet != null) {
                LoanDetailsPayload loanPayload = gsonObj.fromJson(loanDet.getPayloadColumn(), LoanDetailsPayload.class);
                loanPurpose = loanPayload.getLoanPurpose();
            }

            req.setNameOfBSN("");

            req.setEmployeeNumber("");  // According to API spec


            // Phone Number
            if (appPhnNum == null || appPhnNum.trim().isEmpty()) {
                appPhnNum = prop.getProperty(CobFlagsProperties.COAPPL_PHONENUMBER_DEFAULT.getKey());
                logger.debug("Default phone num" + appPhnNum);
            }
            if (coAppPhnNum == null || coAppPhnNum.trim().isEmpty()) {
                coAppPhnNum = prop.getProperty(CobFlagsProperties.COAPPL_PHONENUMBER_DEFAULT.getKey());
                logger.debug("Default phone num" + coAppPhnNum);
            }

            if(coAppPhnNum.equalsIgnoreCase(appPhnNum)){
                coAppPhnNum = "";
            }
            PhoneNumberRequestField phoneObj = new PhoneNumberRequestField();
            phoneObj.setPhoneNumber(useCoApplicant ? coAppPhnNum : appPhnNum);
            List<PhoneNumberRequestField> phoneList = new ArrayList<>();
            phoneList.add(phoneObj);
            req.setPhoneNumber(phoneList);

            //Occuaption
            String occupation = prop.getProperty(CobFlagsProperties.CUSTOMER_OCCUPATION.getKey());
            JSONObject arr = new JSONObject(occupation);
            String custOccupation = "";
            List<UnnatiIexceedOccpInsr> unnatiIexceedOccpInsrOpt = iexceedOccpInsrRepository.findByCustomerId(applicationMasterData.getSearchCode2());
            String applicantOccupation = "";
            String coapplicantOccupation = "";
            if (!unnatiIexceedOccpInsrOpt.isEmpty()) {
                UnnatiIexceedOccpInsr unnatiIexceedOccpInsr = unnatiIexceedOccpInsrOpt.get(0);
                applicantOccupation = unnatiIexceedOccpInsr.getOccupationType();
                coapplicantOccupation = unnatiIexceedOccpInsr.getCoOccupationType();
            }

            String occupationType = useCoApplicant ? coappPayload.getOccupation() : appPayload.getOccupation();
            logger.debug("Occupation Type {}", occupationType);
            if(StringUtils.isEmpty(occupationType)){
                BCMPIIncomeDetails bcmpiIncomeDetails = bcmpiIncomeDetailsRepo.findById(applicationId).orElse(null);

                logger.debug("Bcmpi IncomDetails {}", bcmpiIncomeDetails);
                if(bcmpiIncomeDetails != null){
                    BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper = gsonObj.fromJson(bcmpiIncomeDetails.getPayload(), BCMPIIncomeDetailsWrapper.class);
                    logger.debug("Wrapper Details {}", bcmpiIncomeDetailsWrapper);
                    long businessCount = 0;
                    if(bcmpiIncomeDetailsWrapper != null) {
                        BCMPIIncomeDetailsWrapper.Business business = bcmpiIncomeDetailsWrapper.getBusiness();
                        String type = useCoApplicant ? "Co-Applicant" : "Applicant";

                        boolean hasMoreThanOneBusiness =
                                Stream.of(business.getDairy().stream()
                                                        .filter(d -> d.getDairyType().equalsIgnoreCase("Both")
                                                                || d.getDairyType().equalsIgnoreCase(type)),
                                                business.getOther().stream()
                                                        .filter(o -> o.getOtherType().equalsIgnoreCase("Both")
                                                                || o.getOtherType().equalsIgnoreCase(type)),
                                                business.getKirana().stream()
                                                        .filter(k -> k.getKiranaType().equalsIgnoreCase("Both")
                                                                || k.getKiranaType().equalsIgnoreCase(type)),
                                                business.getTailoring().stream()
                                                        .filter(t -> t.getTailoringType().equalsIgnoreCase("Both")
                                                                || t.getTailoringType().equalsIgnoreCase(type))
                                        )
                                        .flatMap(Function.identity())
                                        .limit(2)
                                        .count() > 1;
                        if(hasMoreThanOneBusiness) {
                            occupationType = "Self employed";
                        } else {
                            BCMPIIncomeDetailsWrapper.Salary salary = bcmpiIncomeDetailsWrapper.getSalary();
                            BCMPIIncomeDetailsWrapper.SalaryDetails salaryDetails = useCoApplicant ? salary.getCoApplicant() : salary.getApplicant();
                            if(salaryDetails != null){
                                occupationType = "Salaried";
                            } else {
                                occupationType = "Home Maker";
                            }
                        }
                    }
                } else {
                    if (useCoApplicant) {
                        occupationType = StringUtils.isBlank(coapplicantOccupation) ? coappPayload.getOccupation() : coapplicantOccupation;
                    } else {
                        occupationType = StringUtils.isBlank(applicantOccupation) ? appPayload.getOccupation() : applicantOccupation;
                    }
                }
            }
            String normalizedInput = normalize(occupationType);

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
            JSONArray addrArray = getAllAddresses(custFields, useCoApplicant ? coApplicantCustId : applicantCustId);
            List<CustomerAddressRequestField> addrList = new Gson().fromJson(addrArray.toString(), new TypeToken<List<CustomerAddressRequestField>>() {
            }.getType());
            req.setCustomerAddress(addrList);

            //Co Applicant
            CoApplicantDetailRequestField coappObj = new CoApplicantDetailRequestField();
            coappObj.setCoApplicantCustomerID(useApplicant ? coapplCreationId : "");//As per Api doc
            coappObj.setCoApplicantRelation(useApplicant && !StringUtils.isBlank(coapplCreationId) ? coappRelation : "");
            List<CoApplicantDetailRequestField> coAppList = new ArrayList<>();
            coAppList.add(coappObj);
            req.setCoApplicantDetails(coAppList);

            // Beneficiary details
            req.setBeneficiaryBankAccountNum(useApplicant ? bankPayload.getAccountNumber() : "");
            req.setBeneficirayIfscCode(useApplicant ? bankPayload.getIfsc() : "");
            req.setBeneficiaryBankName(useApplicant ? bankPayload.getBankName() : "");
            req.setBeneficiaryBranchkName(useApplicant ? bankPayload.getBranchName() : "");

            String applicantT24Id = applicationMasterData
                    .getProductCode()
                    .equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE) ? applicationMasterData.getApplicantT24Id() : memId;
            req.setCustomerIdTemp(useCoApplicant ? coapplCreationId : applicantT24Id);
            req.setCompanyIdTemp(applicationMasterData.getBranchId());
            req.setIsUpdateCall(updateCall ? "Y" : "N");

            req.setLastName(useCoApplicant ? coAppLastName : appLastName);

            Set<String> sanctionStatus = new HashSet<>();//For status where this method is called but we want to send the prospect ID to ESB
            sanctionStatus.add(AppStatus.CACOMPLETED.getValue());
            sanctionStatus.add(AppStatus.RESANCTION.getValue());
            sanctionStatus.add(AppStatus.SANCTIONED.getValue());

            String status = applicationMasterData.getApplicationStatus();

            boolean isSanction = status != null &&
                    sanctionStatus.contains(status.trim().toUpperCase());

            req.setProspectCustomerId(
                    (useCoApplicant && isSanction) ? applicationId : ""
            ); //As we are sending prospect ID only for co-Applicant

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
                apiResp.put(Constants.API_REQUEST, loanCreationRequestExt.toString());
                logger.debug("JSON response 3 from the API: " + apiResp);
                return Mono.just(apiResp);
            });

        } catch (Exception e) {
            logger.error("Error occurred while executing the Cust Creation api, error = {}" , e);
            return Mono.just(adapterUtil.setError("Error occurred while executing the Cust Creation api.", "1"));
        }
    }

    public String findCategoryId(String subPurposeObj, String searchValue, Properties prop) {

        logger.debug("findCategoryId() - ENTRY | searchValue=[{}]", searchValue);

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

                    // strip everything that isn't a letter or digit
                    String normalizedValue = normalizeString(value)
                            .toUpperCase().replaceAll("[^A-Z0-9]", "");
                    reverseLookupMap.put(normalizedValue, category);
                }
            }
            logger.debug("findCategoryId() - reverseLookupMap built | size={}", reverseLookupMap.size());

            String purposeId = prop.getProperty(CobFlagsProperties.LOANPURPOSE_IDENTIFIER.getKey());
            Map<String, String> categoryIdMap = new HashMap<>();
            JSONObject json = new JSONObject(purposeId);
            for (String key : json.keySet()) {
                categoryIdMap.put(key, json.getString(key));
            }
            logger.debug("findCategoryId() - categoryIdMap {}", categoryIdMap);

            // same treatment on the search side
            String normalizedSearchValue = normalizeString(searchValue)
                    .toUpperCase().replaceAll("[^A-Z0-9]", "");
            logger.debug("findCategoryId() - searchValue raw=[{}] normalized=[{}]",
                    searchValue, normalizedSearchValue);

            String category = reverseLookupMap.get(normalizedSearchValue);
            if (category != null) {
                String categoryId = categoryIdMap.getOrDefault(category, "01");
                logger.debug("findCategoryId() - MATCH | category=[{}] categoryId=[{}]", category, categoryId);
                return categoryId;
            } else {
                logger.warn("findCategoryId() - NO MATCH | normalized=[{}] | keys={}",
                        normalizedSearchValue, reverseLookupMap.keySet());
                return "Value Not Found";
            }
        } catch (Exception e) {
            logger.error("Exception in findCategory ID ", e);
        }
        return "Value not Found";
    }

    public JSONArray getAllAddresses(CustomerDataFields req, String custId) {

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
                            if (StringUtils.isNotBlank(addrObj.getString("residenceState"))
                                    && StringUtils.isNotBlank(addrObj.getString("address1"))
                                    && StringUtils.isNotBlank(addrObj.getString("pinCode"))) {
                                finalArr.put(addrObj);
                            }
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
                            if (StringUtils.isNotBlank(addrObj.getString("residenceState"))
                                    && StringUtils.isNotBlank(addrObj.getString("address1"))
                                    && StringUtils.isNotBlank(addrObj.getString("pinCode"))) {
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
            }
        } catch (Exception e) {
            logger.error("Error in Address details list {}", e);
        }
        return finalArr;
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

    private static String normalize(String str) {
        return str.toLowerCase().replaceAll("[^a-z0-9]", "");
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

    private static String normalizeString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("_", "")
                .replace(".", "")
                .toUpperCase()
                .trim();
    }
}
