package com.iexceed.appzillonbanking.cob.loans.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationDocuments;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.BipApiExecutionLog;
import com.iexceed.appzillonbanking.cob.core.domain.ab.BipDetails;
import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationDocumentsRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.BipApiExecutionLogRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.BipDetailsRepository;
import com.iexceed.appzillonbanking.cob.core.services.InterfaceAdapter;
import com.iexceed.appzillonbanking.cob.core.utils.*;
import com.iexceed.appzillonbanking.cob.loans.payload.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BIPImageService {

    private static final Logger logger = LogManager.getLogger(BIPImageService.class);
    @Autowired
    private InterfaceAdapter interfaceAdapter;
    @Autowired
    private LoanService loanService;
    @Autowired
    private ApplicationDocumentsRepository appLoanDocsRepository;
    @Autowired
    private ApplicationMasterRepository applicationMasterRepo;
    @Autowired
    private AdapterUtil adapterUtil;
    @Autowired
    private BipApiExecutionLogRepository bipLogRepository;
    @Autowired
    private BipDetailsRepository bipDetailsRepository;

    @CircuitBreaker(name = "fallback", fallbackMethod = "bIPFallback")
    public Mono<Object> bussinessImgProcessingNew1(BIPRequestWrapper.BIPMasterRequest bipRequest,
                                                   Header header,
                                                   Properties prop) {

        logger.debug("Request from bussinessImgProcessing API: {}", bipRequest.toString());

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String applicationId = bipRequest.getRequestObj().getApplicationId();
            String appId = bipRequest.getRequestObj().getAppId();
            Integer versionNo = bipRequest.getRequestObj().getVersionNum();
            String userId = bipRequest.getRequestObj().getUserId();
            String customerType = bipRequest.getRequestObj().getCustType();
            String documentType = bipRequest.getRequestObj().getDocumentType();
            String business = documentType.split(" ")[0];

            logger.debug("BIP START | applicationId={} | customerType={} | business = {}",
                    applicationId, customerType, business);

            String applicationFolderPath =
                    prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/"
                            + bipRequest.getRequestObj().getAppId() + "/"
                            + Constants.LOAN + "/"
                            + bipRequest.getRequestObj().getApplicationId() + "/";

            logger.debug("applicationFolderPath resolved as: {}", applicationFolderPath);

            List<String> imageFiles = bipRequest.getRequestObj().getFiles();
            List<Map<String, String>> imageUrls = new ArrayList<>();
            if (!imageFiles.isEmpty()) {
                logger.debug("!imageFiles.isEmpty()");
                int index = 1;
                for (String imgName : imageFiles) {

                    File file = new File(applicationFolderPath + imgName);

                    if (!file.exists()) {
                        throw new RuntimeException("Image file not found: " + imgName);
                    }

                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    String base64 = java.util.Base64.getEncoder().encodeToString(fileContent);

                    String lowerName = imgName.toLowerCase();
                    String prefix = lowerName.endsWith(".png")
                            ? "data:image/png;base64,"
                            : "data:image/jpeg;base64,";

                    Map<String, String> imageMap = new HashMap<>();
                    imageMap.put("image" + index++, prefix + base64);
                    imageUrls.add(imageMap);
                }

                BIPRequestExt bipRequestExt = new BIPRequestExt();
                bipRequestExt.setAppId(bipRequest.getRequestObj().getAppId());
                bipRequestExt.setInterfaceName(
                        prop.getProperty(CobFlagsProperties.BIP_SUBMIT_IMG_INTF.getKey())
                );

                BIPInputRequest apiReq = new BIPInputRequest();
                apiReq.setRequestId(UUID.randomUUID().toString());
                apiReq.setBusinessType(business);
                apiReq.setImageUrls(imageUrls);
                apiReq.setOptions(new BIPInputRequest.Options());

                bipRequestExt.setRequestObj(apiReq);

                logger.debug("Request for external BOT Api : {}", bipRequestExt);
                Mono<Object> apiRespMono = interfaceAdapter.callExternalService(
                        header,
                        bipRequestExt,
                        bipRequestExt.getInterfaceName()
                );

                return apiRespMono.flatMap(val -> {
                    try {
                        logger.debug("BIP API RAW RESPONSE :: {}", val);
                        JSONObject apiResp = new JSONObject(new Gson().toJson(val));

                        Optional<List<ApplicationDocuments>> applicationDocumentsOptional = appLoanDocsRepository.applicationDocsRecords(bipRequest.getRequestObj().getCustType(), bipRequest.getRequestObj().getApplicationId(), bipRequest.getRequestObj().getDocumentType());
                        if (applicationDocumentsOptional.isPresent()) {
                            logger.debug("applicationDocumentsOptional.isPresent()");
                            List<ApplicationDocuments> applicationDocumentsList = applicationDocumentsOptional.get();
                            logger.debug("applicationDocumentsList.size()" + applicationDocumentsList.size());
                            for (ApplicationDocuments applicationDocuments : applicationDocumentsList) {
                                logger.debug("ApplicationDocument Record :" + applicationDocuments);
                                ApplicationDocumentsPayload applicationDocumentsPayload = null;
                                try {
                                    applicationDocumentsPayload = objectMapper.readValue(applicationDocuments.getPayloadColumn(), ApplicationDocumentsPayload.class);
                                    logger.debug("applicationDocumentsPayload" + applicationDocumentsPayload);
                                } catch (JsonProcessingException e) {
                                    logger.error("error while JsonProcessing " + e);
                                    throw new RuntimeException(e);
                                }
                                applicationDocumentsPayload.setBotTrigger("Y");
                                logger.debug("Updated the Bot Trigger property, Bot Trigger :" + applicationDocumentsPayload.getBotTrigger());
                                applicationDocuments.setPayloadColumn(new Gson().toJson(applicationDocumentsPayload));
                                appLoanDocsRepository.save(applicationDocuments);
                                logger.debug("Successfully updated the application documents table");
                            }
                        }else{
                            logger.debug("No Application Documents found");
                        }

                        if (!apiResp.optBoolean("success")) {
                            logger.debug("BIP API FAILED RESPONSE :: {}", apiResp);
                            Optional<ApplicationMaster> appMasterDb = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNum(
                                    appId, applicationId, versionNo);

                            if (appMasterDb.isPresent()) {
                                logger.debug("appMasterDb data found Id: {}", applicationId);
                                getApplicationDocuments(apiResp, applicationId, appId);
                                return Mono.just(getBotFailureApiJson(apiResp.toString(),
                                        Constants.BIP_IMG_PROCESS));
                            }else{
                                logger.debug("No data found for ID {} in appMasterDb", applicationId);
                                apiResp.put("applicationDocuments", new ArrayList<>());
                                return Mono.just(getBotFailureApiJson(apiResp.toString(),
                                        Constants.BIP_IMG_PROCESS));
                            }

                        }

                        JSONObject dataObj = apiResp.getJSONObject("data");
                        JsonNode responseData = objectMapper.readTree(dataObj.toString());
                        String businessType = apiResp.optString("business_type");//

                        if (businessType == null || businessType.trim().isEmpty()) {
                            logger.debug("Business type missing in API response");
                            return Mono.just(getBotFailureApiJson(
                                    errorMsgGeneration("Business type missing", "BUSINESS_TYPE_NOT_FOUND", applicationId, appId, versionNo).toString(),
                                    Constants.BIP_IMG_PROCESS));
                        }

                        logger.debug("BIP SUCCESS | businessType={}", businessType);
                        Mono<Object> bipDbResponse = dbPersistence(responseData, objectMapper, businessType, applicationId, appId, versionNo, userId, customerType);
                        return bipDbResponse.flatMap(obj -> {
                            JSONObject finalResponse = new JSONObject();
                            logger.debug("");
                            Response response = (Response) obj;
                            logger.debug("");

                            if (ResponseCodes.FAILURE.getKey().equals(response.getResponseHeader().getResponseCode())){
                                return Mono.just(response);
                            }

                            saveLogBip(bipRequest.getRequestObj().getApplicationId(), Constants.BIP_IMG_PROCESS, bipRequestExt.toString(),
                                    apiResp.toString(), ResponseCodes.SUCCESS.getValue(), null, "");
                            logger.debug("BIPLog table saved");

                            //
                            Optional<ApplicationMaster> appMasterDb = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNum(
                                    appId, applicationId, versionNo);

                            if (appMasterDb.isPresent()) {
                                logger.debug("appMasterDb data found Id: {}", applicationId);
                                getApplicationDocuments(apiResp, applicationId, appId);
                                String jsonString = apiResp.toString();
                                finalResponse = adapterUtil.setSuccessResp(jsonString);
                                logger.debug("finalResponse" + finalResponse);

                            } else {
                                logger.debug("!appMasterDb.isPresent()");
                            }

                            return Mono.just(finalResponse);
                        });
                    } catch (Exception e) {
                        logger.error("Exception inside BIP processing", e);
                        //
                        saveLogBip(bipRequest.getRequestObj().getApplicationId(), Constants.BIP_IMG_PROCESS, bipRequest.toString(), null,
                                ResponseCodes.FAILURE.getValue(), e.getMessage(), "");
                        logger.debug("BipLog Table successfully saved");
                        return Mono.just(getBotFailureApiJson(
                                errorMsgGeneration(e.getMessage(), "CUSTOM_ERROR", applicationId, appId, versionNo).toString(),
                                Constants.BIP_IMG_PROCESS));
                    }
                });
            } else {
                logger.debug("imageFiles.isEmpty()");
                if (!(business == null || business.trim().isEmpty())) {

                    logger.debug("!(business == null || business.trim().isEmpty()) | businessType = {}", business);
                    JsonNode responseData = updateResponseData(business.toLowerCase(), objectMapper);
                    if(responseData != null){
                        logger.debug("responseData != null");
                        Mono<Object> bipDbResponse = dbPersistence(responseData, objectMapper, business, applicationId, appId, versionNo, userId, customerType);
                        return bipDbResponse.flatMap(obj -> {
                            // JSONObject finalResponse = null;
                            Response response = (Response) obj;
                            logger.debug("Response for imageFiles empty scenario : {}", response);
                            if (ResponseCodes.FAILURE.getKey().equals(response.getResponseHeader().getResponseCode())){
                                logger.debug("ResponseCode for imageFiles empty scenario : {}", response.getResponseHeader().getResponseCode());
                                return Mono.just(response);
                            }

                            JSONObject resp = new JSONObject();
                            resp.put("success", "true");
                            resp.put("business_type", business);
                            resp.put("data", responseData);
                            JSONObject finalResponse = new JSONObject();
                            finalResponse.put(Constants.ERRORCODE, "0");
                            finalResponse.put(Constants.ERRORMESSAGE, "");
                            finalResponse.put(Constants.RESPONSEOBJ, resp.toString());
                            return Mono.just(finalResponse);
                        });
                    } else {
                        return Mono.just(getBotFailureApiJson(errorMsgGeneration("Invalid businessType without images", "INVALID_BUSINESS_TYPE", applicationId, appId, versionNo).toString(), Constants.BIP_IMG_PROCESS));
                    }
                }
                return Mono.just(getBotFailureApiJson(errorMsgGeneration("BusinessType is missing in the request", "BUSINESS_TYPE_NOT_FOUND", applicationId, appId, versionNo).toString(), Constants.BIP_IMG_PROCESS));
            }

        } catch (Exception e) {
            logger.error("Outer Exception in BIP API", e);
            //
            saveLogBip(bipRequest.getRequestObj().getApplicationId(), Constants.BIP_IMG_PROCESS, bipRequest.toString(), null,
                    ResponseCodes.FAILURE.getValue(), e.getMessage(), "");
            return Mono.just(getBotFailureApiJson(
                    errorMsgGeneration(e.getMessage(), "CUSTOM_ERROR", bipRequest.getRequestObj().getApplicationId(), bipRequest.getRequestObj().getAppId(), bipRequest.getRequestObj().getVersionNum()).toString(),
                    Constants.BIP_IMG_PROCESS));
        }
    }

    private Mono<Object> dbPersistence(JsonNode responseData, ObjectMapper mapper, String businessType, String applicationId, String appId, Integer versionNo, String userId, String customerType)  {
        try {
            Optional<BipDetails> optBipDetails =
                    bipDetailsRepository.findByApplicationId(applicationId);

            BREBusinessImageAssessment assessment;

            BipDetails existing = null;
            //  existing record
            if (optBipDetails.isPresent()) {

                logger.debug("Existing BIP record found for applicationId={}", applicationId);

                existing = optBipDetails.get();

                BREBusinessImageWrapper wrapper =
                        mapper.readValue(
                                existing.getPayload(),
                                BREBusinessImageWrapper.class
                        );

                assessment = wrapper.getBusinessImageAssessment();

                if (assessment == null) {
                    logger.debug("Assessment NULL in DB. Initializing fresh.");
                    assessment = initializeFullAssessment();
                } else {
                    ensureFullInitialization(assessment);
                }

            } else {
                logger.debug("No existing record. Creating new assessment.");
                assessment = initializeFullAssessment();
            }

            //
            logger.debug("Before Update | AppCategory={} | CoAppCategory={} | CommonCategory = {}",
                    assessment.getAppBusinessCategory(),
                    assessment.getCoAppBusinessCategory(),
                    assessment.getCommonBusinessCategory());

            //  target category
            Object targetCategory = null;
            logger.debug("Customer Type : {}", customerType);
            if (Constants.APPLICANT.equalsIgnoreCase(customerType)) {
                logger.debug("Updating Applicant business only");
                targetCategory = assessment.getAppBusinessCategory();
            } else if (Constants.CO_APPLICANT.equalsIgnoreCase(customerType)) {
                logger.debug("Updating CoApplicant business only");
                targetCategory = assessment.getCoAppBusinessCategory();
            } else if ("Both".equalsIgnoreCase(customerType)) {
                logger.debug("Updating Common business only");
                targetCategory = assessment.getCommonBusinessCategory();
            } else {
                return Mono.just(getBotFailureApiJson(errorMsgGeneration("Invalid Business Type", "INVALID_BUSINESS_TYPE", applicationId, appId, versionNo).toString(), Constants.BIP_IMG_PROCESS));
            }

            //  update only one business
            updateBusinessCategory2(
                    targetCategory,
                    businessType.trim(),
                    responseData,
                    mapper
            );

            logger.debug("After Update | AppCategory={} | CoAppCategory={} | CommonCategory = {}",
                    assessment.getAppBusinessCategory(),
                    assessment.getCoAppBusinessCategory(),
                    assessment.getCommonBusinessCategory());

            BREBusinessImageWrapper wrapper = new BREBusinessImageWrapper();
            wrapper.setBusinessImageAssessment(assessment);

            String finalPayload = mapper.writeValueAsString(wrapper);
            logger.debug("Final Payload Length :: {}", finalPayload.length());

            saveBipDetails1(applicationId, appId, versionNo, finalPayload, userId, existing);
            logger.debug("BIP SAVE COMPLETED | applicationId={}", applicationId);
            return Mono.just(loanService.getSuccessJson("BIP Details Table Successfully saved "));
        }catch(JsonProcessingException e){
            return Mono.just(getBotFailureApiJson(errorMsgGeneration("BIP Details Table Persistence failed" + e.getMessage(), "CUSTOM_ERROR", applicationId, appId, versionNo).toString(), Constants.BIP_IMG_PROCESS));
        }
    }

    private BREBusinessImageAssessment initializeFullAssessment() {

        BREBusinessImageAssessment assessment = new BREBusinessImageAssessment();

        // Applicant
        BREAppBusinessCategory app = new BREAppBusinessCategory();
        app.setKirana(new BREKirana());
        app.setDairy(new BREDairy());
        app.setTailoring(new BRETailoring());
        app.setAgriculture(new BREAgriculture());
        app.setRestaurantEatery(new BRERestaurantEatery());
        app.setBarber(new BREBarber());
        app.setAnimalHusbandry(new BREAnimalHusbandry());
        app.setClothShop(new BREClothShop());

        // Co-Applicant
        BRECoAppBusinessCategory co = new BRECoAppBusinessCategory();
        co.setKirana(new BREKirana());
        co.setDairy(new BREDairy());
        co.setTailoring(new BRETailoring());
        co.setAgriculture(new BREAgriculture());
        co.setRestaurantEatery(new BRERestaurantEatery());
        co.setBarber(new BREBarber());
        co.setAnimalHusbandry(new BREAnimalHusbandry());
        co.setClothShop(new BREClothShop());

        //Common  //both
        BRECommonBusinessCategory common = new BRECommonBusinessCategory();
        common.setKirana(new BREKirana());
        common.setDairy(new BREDairy());
        common.setTailoring(new BRETailoring());
        common.setAgriculture(new BREAgriculture());
        common.setRestaurantEatery(new BRERestaurantEatery());
        common.setBarber(new BREBarber());
        common.setAnimalHusbandry(new BREAnimalHusbandry());
        common.setClothShop(new BREClothShop());

        assessment.setAppBusinessCategory(app);
        assessment.setCoAppBusinessCategory(co);
        assessment.setCommonBusinessCategory(common);
        logger.debug("Initialized Assessment with Default values");
        return assessment;
    }


    private void ensureFullInitialization(BREBusinessImageAssessment assessment) {

        if (assessment.getAppBusinessCategory() == null) {
            assessment.setAppBusinessCategory(
                    initializeFullAssessment().getAppBusinessCategory());
        }

        if (assessment.getCoAppBusinessCategory() == null) {
            assessment.setCoAppBusinessCategory(
                    initializeFullAssessment().getCoAppBusinessCategory());
        }

        if (assessment.getCommonBusinessCategory() == null) {
            assessment.setCommonBusinessCategory(
                    initializeFullAssessment().getCommonBusinessCategory());
        }
    }

    private void updateBusinessCategory2(Object category, String businessType, JsonNode responseData,
                                         ObjectMapper mapper) {

        try {
            String normalizedType = businessType.replace(" ", "_").toUpperCase();
            logger.debug("Business Type in updating business category function : {} ", normalizedType);
            logger.debug("Category type : {}", category.getClass());
            logger.debug("Before updating category : {}", category);
            if (category instanceof BREAppBusinessCategory) {

                BREAppBusinessCategory app = (BREAppBusinessCategory) category;

                switch (normalizedType) {

                    case "KIRANA":
                        app.setKirana(mapper.treeToValue(responseData, BREKirana.class));
                        break;

                    case "DAIRY":
                        app.setDairy(mapper.treeToValue(responseData, BREDairy.class));
                        break;

                    case "TAILORING":
                        app.setTailoring(mapper.treeToValue(responseData, BRETailoring.class));
                        break;

                    case "AGRICULTURE":
                        app.setAgriculture(mapper.treeToValue(responseData, BREAgriculture.class));
                        break;

                    case "RESTAURANT_EATERY":
                        app.setRestaurantEatery(mapper.treeToValue(responseData, BRERestaurantEatery.class));
                        break;

                    case "BARBER":
                        app.setBarber(mapper.treeToValue(responseData, BREBarber.class));
                        break;

                    case "ANIMAL_HUSBANDRY":
                        app.setAnimalHusbandry(mapper.treeToValue(responseData, BREAnimalHusbandry.class));
                        break;

                    case "CLOTH_SHOP":
                        app.setClothShop(mapper.treeToValue(responseData, BREClothShop.class));
                        break;

                    default:
                        logger.debug("Unknown business type: {}", businessType);
                }

            } else if (category instanceof BRECoAppBusinessCategory) {

                BRECoAppBusinessCategory co = (BRECoAppBusinessCategory) category;

                switch (normalizedType) {

                    case "KIRANA":
                        co.setKirana(mapper.treeToValue(responseData, BREKirana.class));
                        break;

                    case "DAIRY":
                        co.setDairy(mapper.treeToValue(responseData, BREDairy.class));
                        break;

                    case "TAILORING":
                        co.setTailoring(mapper.treeToValue(responseData, BRETailoring.class));
                        break;

                    case "AGRICULTURE":
                        co.setAgriculture(mapper.treeToValue(responseData, BREAgriculture.class));
                        break;

                    case "RESTAURANT_EATERY":
                        co.setRestaurantEatery(mapper.treeToValue(responseData, BRERestaurantEatery.class));
                        break;

                    case "BARBER":
                        co.setBarber(mapper.treeToValue(responseData, BREBarber.class));
                        break;

                    case "ANIMAL_HUSBANDRY":
                        co.setAnimalHusbandry(mapper.treeToValue(responseData, BREAnimalHusbandry.class));
                        break;

                    case "CLOTH_SHOP":
                        co.setClothShop(mapper.treeToValue(responseData, BREClothShop.class));
                        break;

                    default:
                        logger.debug("Unknown business type: {}", businessType);
                }
            } else if (category instanceof BRECommonBusinessCategory) {

                BRECommonBusinessCategory common = (BRECommonBusinessCategory) category;

                switch (normalizedType) {

                    case "KIRANA":
                        common.setKirana(mapper.treeToValue(responseData, BREKirana.class));
                        break;

                    case "DAIRY":
                        common.setDairy(mapper.treeToValue(responseData, BREDairy.class));
                        break;

                    case "TAILORING":
                        common.setTailoring(mapper.treeToValue(responseData, BRETailoring.class));
                        break;

                    case "AGRICULTURE":
                        common.setAgriculture(mapper.treeToValue(responseData, BREAgriculture.class));
                        break;

                    case "RESTAURANT_EATERY":
                        common.setRestaurantEatery(mapper.treeToValue(responseData, BRERestaurantEatery.class));
                        break;

                    case "BARBER":
                        common.setBarber(mapper.treeToValue(responseData, BREBarber.class));
                        break;

                    case "ANIMAL_HUSBANDRY":
                        common.setAnimalHusbandry(mapper.treeToValue(responseData, BREAnimalHusbandry.class));
                        break;

                    case "CLOTH_SHOP":
                        common.setClothShop(mapper.treeToValue(responseData, BREClothShop.class));
                        break;

                    default:
                        logger.debug("Unknown business type: {}", businessType);
                }
            }
            logger.debug("After updating category : {}", category);
        } catch (Exception e) {
            logger.error("Business update failed", e);
            throw new RuntimeException("Business update failed", e);
        }
    }

    private JsonNode updateResponseData(String businessType, ObjectMapper mapper){
        BREBusinessImageAssessment breBusinessImageAssessment = new BREBusinessImageAssessment();
        BREAppBusinessCategory breAppBusinessCategory = breBusinessImageAssessment.getAppBusinessCategory();
        logger.debug("BREAppBusinessCategory : {}", breAppBusinessCategory);
        JsonNode response = null;
        logger.debug("businessType: " + businessType);
        try {
            switch (businessType) {
                case "kirana":
                    response = mapper.valueToTree(breAppBusinessCategory.getKirana());
                    break;
                case "dairy":
                    response = mapper.valueToTree(breAppBusinessCategory.getDairy());
                    break;
                case "tailoring":
                    response = mapper.valueToTree(breAppBusinessCategory.getTailoring());
                    break;
                case "barber":
                    response = mapper.valueToTree(breAppBusinessCategory.getBarber());
                    break;
                case "agriculture":
                    response = mapper.valueToTree(breAppBusinessCategory.getAgriculture());
                    break;
                case "animal_husbandry":
                    response = mapper.valueToTree(breAppBusinessCategory.getAnimalHusbandry());
                    break;
                case "restaurant_eatery":
                    response = mapper.valueToTree(breAppBusinessCategory.getRestaurantEatery());
                    break;
                case "cloth_shop":
                    response = mapper.valueToTree(breAppBusinessCategory.getClothShop());
                    break;
                default:
                    logger.debug("Invalid businesstype for assigning default value if images are not present");
                    break;
            }
        }catch(Exception e){
            logger.debug("Updating Responsedata with Default value is failed : {}", e.getMessage());
        }
        logger.debug("Updated ResponseData with Default value : {}", response);
        return response;
    }

    private Mono<Object> bIPFallback(BIPRequestWrapper.BIPMasterRequest apiRequest, Header header, Properties prop,
                                     Exception e) {
        logger.error("bIPFallback error : ", e);
        return FallbackUtils.genericFallbackMonoObject();
    }

    private void saveBipDetails1(String applicationID, String appId, Integer versionNo, String payload, String userId,
                                 BipDetails bipDetails) {

        logger.debug("saveBipDetails1 called for applicationId={}", applicationID);

        if (bipDetails == null) {
            logger.debug("saveBipDetails - bipDetails - null");
            bipDetails = new BipDetails();
            bipDetails.setCreateTs(LocalDateTime.now());
            bipDetails.setCreatedBy(userId);
        }

        bipDetails.setApplicationId(applicationID);
        bipDetails.setAppId(appId);
        bipDetails.setVersionNo(versionNo);
        bipDetails.setPayload(payload);
        bipDetails.setUpdatedBy(userId);
        bipDetails.setUpdateTs(LocalDateTime.now());
        logger.debug("BIPDetails value before saving in DB : {}", bipDetails);
        bipDetailsRepository.save(bipDetails);
    }

    private void saveLogBip(String applicationId, String stepName, String request, String response, String status,
                            String errorMsg, String currentStage) {
        logger.debug("saveLogBip called for applicationId={}", applicationId);

        BipApiExecutionLog log = new BipApiExecutionLog();
        log.setApplicationId(applicationId);
        log.setApiName(stepName);
        log.setRequestPayload(request);
        log.setResponsePayload(response);
        log.setApiStatus(status);
        log.setErrorMessage(errorMsg);
        log.setCreateTs(LocalDateTime.now());
        log.setCurrentStage(currentStage);
        logger.debug("BIPApiExecutionLog value before saving in DB : {}", log);
        bipLogRepository.save(log);
    }

    public Response getBotFailureApiJson(String error, String apiName) {
        logger.debug("Inside getBotFailureJson");
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
        JSONObject failureResponse = new JSONObject();
        failureResponse.put("apiName", apiName);
        failureResponse.put("status", ResponseCodes.FAILURE.getValue());
        failureResponse.put("responseObj", error);
        responseBody.setResponseObj(failureResponse.toString());
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        logger.debug("FailureResponse created" + response.toString());
        return response;
    }

    public JSONObject errorMsgGeneration(String msg, String code, String applicationId, String appId, int versionNo){
        JSONObject errorObj = new JSONObject();
        errorObj.put("success", "false");
        JSONObject error = new JSONObject();
        error.put("code", code);
        error.put("message", msg);
        errorObj.put("error", error);

        Optional<ApplicationMaster> appMasterDb = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNum(
                appId, applicationId, versionNo);
        if (appMasterDb.isPresent()) {
            logger.debug("appMasterDb data found Id: {}", applicationId);
            getApplicationDocuments(errorObj, applicationId, appId);
        }else{
            logger.debug("No data found for Id {} in appMasterDb", applicationId);
            errorObj.put("applicationDocuments", new ArrayList<>());
        }
        logger.debug("Generated Error Body :" + errorObj);
        return errorObj;
    }

    public void getApplicationDocuments(JSONObject apiResp, String applicationId, String appId){
        List<ApplicationDocuments> documentRecords = appLoanDocsRepository
                .findByApplicationIdAndAppId(applicationId, appId);

        logger.debug("documentRecordsOpt value: {}", documentRecords);
        //1
        Gson gson1 = new Gson();
        if (!documentRecords.isEmpty()) {
            logger.debug("documentRecords present: {}", documentRecords);
            apiResp.put("applicationDocuments", documentRecords);
        } else {
            logger.debug("No document records found for applicationId: {}", applicationId);
            apiResp.put("applicationDocuments", new ArrayList<>());
        }
    }
}
