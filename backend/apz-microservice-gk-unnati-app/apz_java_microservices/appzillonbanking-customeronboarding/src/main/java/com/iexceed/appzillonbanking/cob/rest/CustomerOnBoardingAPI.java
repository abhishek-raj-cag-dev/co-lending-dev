package com.iexceed.appzillonbanking.cob.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.iexceed.appzillonbanking.cob.loans.service.LoanService;
import com.iexceed.appzillonbanking.cob.payload.*;
import com.iexceed.appzillonbanking.cob.service.DashboardService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.payload.RequestWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.core.utils.AdapterUtil;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.FallbackUtils;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.domain.ab.RoleAccessMap;
import com.iexceed.appzillonbanking.cob.service.COBService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/cob")
@Component
@Api(tags = "COB", value = "/cob")
public class CustomerOnBoardingAPI {

    private static final Logger logger = LogManager.getLogger(CustomerOnBoardingAPI.class);
    private static final String DEMOMODE = "demoMode";
    public static final String APPIDASSISTEDONBOARDING = "appIDAssistedOnBoarding";


    @Autowired
    private COBService cobService;

    @Autowired
    private CommonParamService commonService;

    @Autowired
    private AdapterUtil adapterUtil;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    LoanService loanService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Insert new customer's data", notes = "API to insert new customer's data")
    @PostMapping(value = "/createapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> createApplication(@RequestBody CreateModifyUserRequestWrapper createUserRequestWrapper,
                                                             @RequestHeader(defaultValue = "APZRMB") String reqAppId,
                                                             @RequestHeader(defaultValue = "extractocrdata") String interfaceId,
                                                             @RequestHeader(defaultValue = "000000000002") String userId,
                                                             @RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
                                                             @RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Header header = CommonUtils.obtainHeader(reqAppId, interfaceId, userId, masterTxnRefNo, deviceId);
		Mono<Response> response = Mono.empty();
		CreateModifyUserRequest request = createUserRequestWrapper.getCreateModifyUserRequest();
		boolean isSelfOnBoardingAppId = false;
		boolean isSelfOnBoardingHeaderAppId = false;
		String appId = request.getRequestObj().getAppId();
		String headerAppId = request.getAppId();
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in createApplication ",e);
			response = CommonUtils.formFailResponseMono(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey()); 
		}
		if(null!=prop) {
			String isDemoMode = prop.getProperty(DEMOMODE); // Remove the logic of demo mode during project implementation.
			if (request.getRequestObj() != null && "Y".equalsIgnoreCase(isDemoMode)) {
				response = cobService.createApplicationInDemoMode(request);
			} else {
				if (appId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
					isSelfOnBoardingAppId = true;
				}
				if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
					isSelfOnBoardingHeaderAppId = true;
				}
				JSONArray array = cobService.fetchFunctionSeqArray(request, isSelfOnBoardingHeaderAppId);
				logger.error("Request in Customeronboarding is "+request.toString());
				logger.error("Array in Customeronboarding is "+array);
				if (cobService.isValidStage(request, isSelfOnBoardingHeaderAppId, array)) { // VAPT
					if(cobService.isVaptPassedForScreenElements(request, isSelfOnBoardingHeaderAppId, array)) { //VAPT
						if (appId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
						Mono<Response> response1 = cobService.createApplication(request, isSelfOnBoardingAppId, prop, isSelfOnBoardingHeaderAppId, header, array);
							response = cobService.updateRelatedApplnIdDetails(request, response1, appId, isSelfOnBoardingHeaderAppId);
						} else if (appId.equalsIgnoreCase(prop.getProperty(APPIDASSISTEDONBOARDING))) {
							String roleId = commonService.fetchRoleId(appId, request.getUserId());
							RoleAccessMap objDb = cobService.fetchRoleAccessMapObj(appId, roleId);
							if (Constants.ACCESS_PERMISSION_VIEWONLY.equalsIgnoreCase(objDb.getAccessPermission())) {
								response = CommonUtils.formFailResponseMono(ResponseCodes.VAPT_ISSUE_PERMISSION.getValue(), ResponseCodes.VAPT_ISSUE_PERMISSION.getKey());
							} else if (Constants.ACCESS_PERMISSION_INITIATOR.equalsIgnoreCase(objDb.getAccessPermission())
									|| Constants.ACCESS_PERMISSION_APPROVER.equalsIgnoreCase(objDb.getAccessPermission())
									|| Constants.ACCESS_PERMISSION_BOTH.equalsIgnoreCase(objDb.getAccessPermission())
									|| Constants.ACCESS_PERMISSION_VERIFIER.equalsIgnoreCase(objDb.getAccessPermission())) {
								Mono<Response> response1 = cobService.createApplication(request, isSelfOnBoardingAppId, prop, isSelfOnBoardingHeaderAppId, header, array);
								response = cobService.updateRelatedApplnIdDetails(request, response1, appId, isSelfOnBoardingHeaderAppId);
							}
						}
					} else { 
						logger.debug("VAPT failed for screen elements");
						response=CommonUtils.formFailResponseMono(ResponseCodes.VAPT_ISSUE_FIELDS.getValue(), ResponseCodes.VAPT_ISSUE_FIELDS.getKey()); 
					}
				} else {
					logger.debug("VAPT failed for stage validation");
					response = CommonUtils.formFailResponseMono(ResponseCodes.VAPT_ISSUE_STAGE.getValue(), ResponseCodes.VAPT_ISSUE_STAGE.getKey());
				}
			}
		}
		return response.flatMap(val -> {
			ResponseWrapper responseWrapper1 = ResponseWrapper.builder().apiResponse(val).build();
			return Mono.just(new ResponseEntity<>(responseWrapper1, HttpStatus.OK));
		});
	}

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Fetch customer's data", notes = "API to fetch customer's data")
    @PostMapping(value = "/fetchapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchApplication(
			@RequestBody FetchDeleteUserRequestWrapper fetchUserRequestWrapper) {
		logger.debug("fetchapplication-> FetchDeleteUserRequestWrapper: {}", fetchUserRequestWrapper);
		
		ResponseEntity<ResponseWrapper> serviceFetchApplication = cobService
				.serviceFetchApplication(fetchUserRequestWrapper.getFetchDeleteUserRequest());
		logger.debug("serviceFetchApplication Response", serviceFetchApplication);
		return serviceFetchApplication;
	}

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Fetch Countries", notes = "API to fetch Countries")
    @PostMapping(value = "/fetchcountries", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> fetchCountries(@RequestBody RequestWrapper requestWrapper) {
    	ResponseWrapper fetchCountriesResponseWrapper = new ResponseWrapper();
        Response fetchCountriesResponse = cobService.fetchCountries(requestWrapper.getApiRequest());
        fetchCountriesResponseWrapper.setApiResponse(fetchCountriesResponse);
        logger.warn("End : fetchCountries method response is:: " + fetchCountriesResponseWrapper.toString());
        return new ResponseEntity<>(fetchCountriesResponseWrapper, HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Fetch States", notes = "API to fetch States")
    @PostMapping(value = "/fetchstates", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> fetchStates(@RequestBody RequestWrapper requestWrapper) {
        ResponseWrapper fetchStatesResponseWrapper = new ResponseWrapper();
        Response fetchStatesResponse = cobService.fetchStates(requestWrapper.getApiRequest());
        fetchStatesResponseWrapper.setApiResponse(fetchStatesResponse);
        logger.warn("End : fetchStates method response is:: " + fetchStatesResponseWrapper.toString());
        return new ResponseEntity<>(fetchStatesResponseWrapper, HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Fetch Cities", notes = "API to fetch Cities")
    @PostMapping(value = "/fetchcities", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> fetchCities(@RequestBody FetchCitiesRequestWrapper fetchCitiesRequestWrapper) {
        ResponseWrapper fetchCitiesResponseWrapper = new ResponseWrapper();
        Response fetchCitiesResponse = cobService.fetchCities(fetchCitiesRequestWrapper.getFetchCitiesRequest());
        fetchCitiesResponseWrapper.setApiResponse(fetchCitiesResponse);
        logger.warn("End : fetchCities method response is:: " + fetchCitiesResponseWrapper.toString());
        return new ResponseEntity<>(fetchCitiesResponseWrapper, HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Delete Nominee", notes = "API to Delete Nominee")
    @PostMapping(value = "/deletenominee", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> deleteNominee(@RequestBody DeleteNomineeRequestWrapper deleteNomineeRequestWrapper) {
        ResponseWrapper deleteNomineeResponseWrapper = new ResponseWrapper();
        Response deleteNomineeResponse = cobService.deleteNominee(deleteNomineeRequestWrapper.getDeleteNomineeRequest());
        deleteNomineeResponseWrapper.setApiResponse(deleteNomineeResponse);
        logger.warn("End : deleteNominee method response is:: " + deleteNomineeResponseWrapper.toString());
        return new ResponseEntity<>(deleteNomineeResponseWrapper, HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Fetch List Of Values Master table data", notes = "API to Fetch List Of Values Master table data")
    @PostMapping(value = "/fetchlov", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> fetchLovMaster(@RequestBody RequestWrapper requestWrapper) {
        ResponseWrapper fetchLovMasterResponseWrapper = new ResponseWrapper();
        Response fetchLovMasterResponse = cobService.fetchLovMaster(requestWrapper.getApiRequest());
        fetchLovMasterResponseWrapper.setApiResponse(fetchLovMasterResponse);
        logger.warn("End : fetchLovMaster method response is:: " + fetchLovMasterResponseWrapper.toString());
        return new ResponseEntity<>(fetchLovMasterResponseWrapper, HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Fetch Nominee", notes = "API to Fetch Nominee")
    @PostMapping(value = "/fetchnominee", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> fetchNominee(@RequestBody FetchNomineeRequestWrapper fetchNomineeRequestWrapper) {
        ResponseWrapper fetchNomineeResponseWrapper = new ResponseWrapper();
        Response fetchNomineeResponse = cobService.fetchNominee(fetchNomineeRequestWrapper.getFetchNomineeRequest());
        fetchNomineeResponseWrapper.setApiResponse(fetchNomineeResponse);
        logger.warn("End : fetchNominee method response is:: " + fetchNomineeResponseWrapper.toString());
        return new ResponseEntity<>(fetchNomineeResponseWrapper, HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Check application is present", notes = "API to Check application is present")
    @PostMapping(value = "/checkapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> checkApplication(@RequestBody CheckApplicationRequestWrapper requestWrapper,
	                                                            @RequestHeader(defaultValue = "APZRMB") String applicationId,
	                                                            @RequestHeader(defaultValue = "extractocrdata") String interfaceId,
	                                                            @RequestHeader(defaultValue = "000000000002") String userId,
	                                                            @RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
	                                                            @RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Header header = CommonUtils.obtainHeader(applicationId, interfaceId, userId, masterTxnRefNo, deviceId);
		Mono<Response> responseMono;
		CheckApplicationRequest request = requestWrapper.getCheckApplicationRequest();
		CheckApplicationRequestFields requestObj = request.getRequestObj();
		responseMono = cobService.checkApplication(request, header);
		return responseMono.flatMap(response -> {
			if (response != null && response.getResponseHeader() != null && ResponseCodes.SUCCESS.getKey().equalsIgnoreCase(response.getResponseHeader().getResponseCode())) {
				if ("Y".equalsIgnoreCase(requestObj.getProductChanged())) { // USE CASE: User changed the product during the journey so we need to discard the previously selected product application
					// call discard and create application
					CreateModifyUserRequestWrapper discardAndCreateRequestWrapper = new CreateModifyUserRequestWrapper();
					CreateModifyUserRequest discardAndCreateRequest = new CreateModifyUserRequest();
					discardAndCreateRequest.setAppId(request.getAppId());
					discardAndCreateRequest.setInterfaceName(null);
					discardAndCreateRequest.setRequestObj(requestObj.getCustomerDataFields());
					discardAndCreateRequest.setUserId(null);
					discardAndCreateRequestWrapper.setCreateModifyUserRequest(discardAndCreateRequest);
					discardAndCreateApplication(discardAndCreateRequestWrapper, applicationId, interfaceId, userId, masterTxnRefNo, deviceId);
				} else if ("N".equalsIgnoreCase(requestObj.getProductChanged())) {
					// call create application
					CreateModifyUserRequestWrapper createApplicationRequestWrapper = new CreateModifyUserRequestWrapper();
					CreateModifyUserRequest createApplicationRequest = new CreateModifyUserRequest();
					createApplicationRequest.setAppId(request.getAppId());
					createApplicationRequest.setInterfaceName(null);
					createApplicationRequest.setRequestObj(requestObj.getCustomerDataFields());
					createApplicationRequest.setUserId(userId);
					createApplicationRequestWrapper.setCreateModifyUserRequest(createApplicationRequest);
					createApplication(createApplicationRequestWrapper, applicationId, interfaceId, userId, masterTxnRefNo, deviceId);
				}
			}
			return Mono.just(new ResponseEntity<>(ResponseWrapper.builder().apiResponse(response).build(), HttpStatus.OK));
		});
	}

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Delete Document", notes = "API to Delete Document")
    @PostMapping(value = "/deletedocument", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> deleteDocument(@RequestBody DeleteDocumentRequestWrapper deleteDocumentRequestWrapper) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        Response response = cobService.deleteDocument(deleteDocumentRequestWrapper.getDeleteDocumentRequest());
        responseWrapper.setApiResponse(response);
        logger.warn("End : deleteDocument method response is:: " + responseWrapper.toString());
        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Download Report", notes = "API to Generate & Download Document")
	@PostMapping(value = "/generateAndDownloadReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> generateAndDownloadReports(@RequestBody DownloadReportRequestWrapper downloadDocumentRequestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.warn("Inside downloadApplication" + downloadDocumentRequestWrapper.getDownloadReportRequest());
		Response response = cobService.downloadReport(downloadDocumentRequestWrapper.getDownloadReportRequest(), false);
		responseWrapper.setApiResponse(response);
		logger.warn("End : Generate & Delete method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}


	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Download Report", notes = "API to Generate & Download Document")
	@PostMapping(value = "/dbKitDocGenerationAndDownload", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> dbKitDocGenerationAndDownload(
			@RequestBody UploadDocumentRequestWrapper uploadDocumentRequestWrapper) throws IOException {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.warn(
				"Inside dbKitDocGenerationAndDownload : {}" , uploadDocumentRequestWrapper.getApiRequest().getRequestObj());
		Response response = new Response();
		try {
			response = cobService
					.dbKitDocGenerationAndDownload(uploadDocumentRequestWrapper.getApiRequest().getRequestObj());
		} catch (Exception e) {
			logger.error("Error while executing dbKitDocGenerationAndDownload with exception: {}", e.getMessage(), e);
			response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : dbKitDocGenerationAndDownload method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Discard old appliction and create new application.", notes = "API to Discard old appliction and create new application.")
    @PostMapping(value = "/discardandcreateapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> discardAndCreateApplication(@RequestBody CreateModifyUserRequestWrapper discardAndCreateUserRequestWrapper,
                                                                       @RequestHeader(defaultValue = "APZRMB") String applicationId,
                                                                       @RequestHeader(defaultValue = "extractocrdata") String interfaceId,
                                                                       @RequestHeader(defaultValue = "000000000002") String userId,
                                                                       @RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
                                                                       @RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
        Header header = CommonUtils.obtainHeader(applicationId, interfaceId, userId, masterTxnRefNo, deviceId);
        Mono<Response> response = Mono.empty();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
            CreateModifyUserRequest request = discardAndCreateUserRequestWrapper.getCreateModifyUserRequest();
            boolean discarded = cobService.discardApplication(request);
            if (discarded) {
                boolean isSelfOnBoardingAppId = false;
                boolean isSelfOnBoardingHeaderAppId = false;
                Properties prop = null;
				try {
					prop = CommonUtils.readPropertyFile();
				} catch (IOException e) {
					logger.error("Error while reading property file in discardApplication ", e);
					response = FallbackUtils.genericFallbackMono();
				}
				if(null!=prop) {
					String headerAppId = request.getAppId();
	                String appId = request.getRequestObj().getAppId();
	                if (appId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
	                    isSelfOnBoardingAppId = true;
	                }
	                if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
	                    isSelfOnBoardingHeaderAppId = true;
	                }
	                request.getRequestObj().setApplicationId(null); //set this to null so that new application Id will be created in createApplication method.
	                request.getRequestObj().getApplicationMaster().setCustDtlId(null); //set this to null so that new custDtlId will be created in createApplication method.
	                response = cobService.createApplication(request, isSelfOnBoardingAppId, prop, isSelfOnBoardingHeaderAppId, header, null);
	                cobService.updateRelatedApplnIdDetails(request, response, appId, isSelfOnBoardingHeaderAppId);	
				}
            } else {
                responseHeader.setResponseCode(ResponseCodes.INVALID_DISCARD.getKey());
                responseBody.setResponseObj(ResponseCodes.INVALID_DISCARD.getValue());
                Response res=new Response();
                res.setResponseBody(responseBody);
                res.setResponseHeader(responseHeader);
                response=Mono.just(res);
            }
        return response.flatMap(val -> {
			ResponseWrapper responseWrapper = ResponseWrapper.builder().apiResponse(val).build();
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		});
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Verify National ID", notes = "API to Verify National ID")
    @PostMapping(value = "/verifynationalid", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> verifyNationalId(@RequestBody VerifyNationalIdRequestWrapper verifyNationalIdRequestWrapper) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        VerifyNationalIdRequest request = verifyNationalIdRequestWrapper.getVerifyNationalIdRequest();
        VerifyNationalIdRequestFields reqFields = request.getRequestObj();
        Response response = CommonUtils.verifyNationalId(reqFields.getNationalIdName(), reqFields.getNationalIdValue());
        responseWrapper.setApiResponse(response);
        logger.warn("End : verifyNationalId method response is:: " + responseWrapper.toString());
        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "OCR Extract", notes = "API to extract data using OCR")
    @PostMapping(value = "/ocranduploaddocument", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> extractOcrAndUploadDocument(@RequestBody ExtractOcrDataRequestWrapper extractOcrDataRequestWrapper,
                                                                       @RequestHeader(defaultValue = "APZRMB") String appId,
                                                                       @RequestHeader(defaultValue = "extractocrdata") String interfaceId,
                                                                       @RequestHeader(defaultValue = "000000000002") String userId,
                                                                       @RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
                                                                       @RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Mono<Response> response1 = Mono.empty();
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in extractOcrAndUploadDocument ", e);
			response1 = FallbackUtils.genericFallbackMono();
		}
		if (prop != null) {
			boolean isSelfOnBoardingHeaderAppId = false;
			ExtractOcrDataRequest request = extractOcrDataRequestWrapper.getExtractOcrDataRequest();
			String headerAppId = request.getAppId();
			if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
				isSelfOnBoardingHeaderAppId = true;
			}
			Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
			Mono<Response> responseMono = cobService.extractOcrData(request, header);
			String isDemoMode = prop.getProperty(DEMOMODE);
			final boolean isSelfOnBoardingHeaderAppIdFinal = isSelfOnBoardingHeaderAppId;
			final Properties propFinal = prop;
			return responseMono.flatMap(response -> {
				if ("N".equalsIgnoreCase(isDemoMode) && response != null && response.getResponseHeader() != null) {
					if (ResponseCodes.SUCCESS.getKey().equalsIgnoreCase(response.getResponseHeader().getResponseCode())) {
						String nationalId = cobService.fetchPropertyFromOcrResponse(response, CobFlagsProperties.NATIONAL_ID_KEY.getKey(), request, propFinal);
						// nationalID will be blank for back image. During implementation these
						// scenarios should be considered for specific document.
						ExtractOcrDataRequesttFields ocrReqFields = request.getRequestObj();
						UploadDocumentRequest uploadDocReq = new UploadDocumentRequest();
						UploadDocumentRequestFields uploadDocReqFields = new UploadDocumentRequestFields();
						uploadDocReqFields.setVersionNum(ocrReqFields.getVersionNum());
						uploadDocReqFields.setApplicationId(ocrReqFields.getApplicationId());
						uploadDocReqFields.setFilePath(ocrReqFields.getFilePath());
						uploadDocReqFields.setFileName(ocrReqFields.getFileName());
						uploadDocReqFields.setAppId(ocrReqFields.getAppId());
						uploadDocReqFields.setBase64Value(ocrReqFields.getDataBase64());
						uploadDocReqFields.setSrcScreen("KYC");
						uploadDocReq.setRequestObj(uploadDocReqFields);
						Mono<Response> uploadDocRes = null;
						uploadDocRes = cobService.uploadDocument(uploadDocReq, nationalId, header, isSelfOnBoardingHeaderAppIdFinal, propFinal);
						uploadDocRes.flatMap(val -> {
							if (!ResponseCodes.SUCCESS.getKey().equalsIgnoreCase(val.getResponseHeader().getResponseCode())) {
								response.getResponseHeader().setResponseCode(val.getResponseHeader().getResponseCode());
								response.getResponseBody().setResponseObj(val.getResponseBody().getResponseObj());
							} else {
								if (val.getResponseBody().getResponseObj() != null) {
									// for success send back application id and cust dtl id. They will be created in
									// upload document if any document gets uploaded in the first screen.
									List<Object> list = new ArrayList<>();
									list.add(response.getResponseBody().getResponseObj());
									list.add(val.getResponseBody().getResponseObj());
									Gson gson = new Gson();
									response.getResponseBody().setResponseObj(gson.toJson(list));
								}
							}
							return null;
						});
					}
				} else {
					Gson gson = new Gson();
					List<Object> list = new ArrayList<>();
					if (response != null && response.getResponseBody() != null) {
						list.add(response.getResponseBody().getResponseObj());
						response.getResponseBody().setResponseObj(gson.toJson(list));
					}
				}
				return Mono.just(new ResponseEntity<>(ResponseWrapper.builder().apiResponse(response).build(), HttpStatus.OK));
			});
		}
		return response1.flatMap(val -> {
			return Mono.just(new ResponseEntity<>(ResponseWrapper.builder().apiResponse(val).build(), HttpStatus.OK));
		});
	}


    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Upload document", notes = "API to Upload document")
    @PostMapping(value = "/uploaddocument", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> uploadDocument(@RequestBody UploadDocumentRequestWrapper uploadDocumentRequestWrapper,
                                                          @RequestHeader(defaultValue = "APZRMB") String appId,
                                                          @RequestHeader(defaultValue = "extractocrdata") String interfaceId,
                                                          @RequestHeader(defaultValue = "000000000002") String userId,
                                                          @RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
                                                          @RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
        Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
        Mono<Response> responseMono = Mono.empty();
        UploadDocumentRequest request = uploadDocumentRequestWrapper.getApiRequest();
        Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in uploaddocument ", e);
			responseMono = FallbackUtils.genericFallbackMono();
		}
		if(null!=prop) {
			 boolean isSelfOnBoardingHeaderAppId = false;
	            String headerAppId = request.getAppId();
	            if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
	                isSelfOnBoardingHeaderAppId = true;
	            }
	            responseMono = cobService.uploadDocument(request, null, header, isSelfOnBoardingHeaderAppId, prop);
		}
		return responseMono.flatMap(val -> {
			ResponseWrapper responseWrapper = ResponseWrapper.builder().apiResponse(val).build();
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		});
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Fetch Branch Details", notes = "API to Upload document")
    @PostMapping(value = "/fetchbranches", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> fetchBranches(@RequestBody FetchBranchesRequestWrapper requestWrapper) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        FetchBranchesRequest request = requestWrapper.getApiRequest();
        Response response = cobService.fetchBranches(request);
        responseWrapper.setApiResponse(response);
        logger.warn("End : fetchBranches method response is:: " + responseWrapper.toString());
        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Fetch LIT Codes", notes = "API to fetch LIT Codes based on language from property file")
    @PostMapping(value = "/fetchlitbylanguage", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchLitByLanguage(@RequestBody FetchLitByLanguageRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		FetchLitByLanguageRequest request = requestWrapper.getApiRequest();
		Response response = cobService.fetchLitByLanguage(request);
		responseWrapper.setApiResponse(response);
		logger.warn("End : fetchLitByLanguage method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}


    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Download Application", notes = "API to Download Application")
    @PostMapping(value = "/downloadapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> downloadApplication(@RequestBody FetchDeleteUserRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = cobService.downloadApplication(requestWrapper.getFetchDeleteUserRequest());
		responseWrapper.setApiResponse(response);
		logger.warn("End : downloadApplication method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Update LIT file", notes = "API to Update LIT file")
    @PostMapping(value = "/updatelitfile", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> updateLitFile(@RequestBody UpdateLitFileRequestWrapper requestWrapper) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        Response response = cobService.updateLitFile(requestWrapper.getUpdateLitFileRequest());
        responseWrapper.setApiResponse(response);
        logger.warn("End : updateLitFile method response is:: " + responseWrapper.toString());
        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Fetch Banks", notes = "API to Fetch Banks")
    @PostMapping(value = "/fetchbanks", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> fetchBanks(@RequestBody FetchBanksRequestWrapper requestWrapper,
                                                      @RequestHeader(defaultValue = "APZRMB") String appId,
                                                      @RequestHeader(defaultValue = "extractocrdata") String interfaceId,
                                                      @RequestHeader(defaultValue = "000000000002") String userId,
                                                      @RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
                                                      @RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
        Mono<Object> response;
        Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
        response = cobService.fetchBanks(requestWrapper.getApiRequest(), header);
        logger.warn("End : fetchBanks method response is:: " + response.toString());
        return adapterUtil.generateResponseWrapper(response, requestWrapper.getApiRequest().getInterfaceName(), header);
    }
    
    @ApiResponses({
        @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
        @ApiResponse(code = 408, message = "Service Timed Out"),
        @ApiResponse(code = 500, message = "Internal Server Error"),
        @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
		@ApiOperation(value = "Update List Of Values Master table data", notes = "API to update List Of Values Master table data")
		@PostMapping(value = "/updatelov", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> updateLov(@RequestBody UpdateLovRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = cobService.updateLov(requestWrapper.getApiRequest());
		responseWrapper.setApiResponse(response);
		logger.warn("End : updateLov method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
    
    @ApiResponses({
        @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
        @ApiResponse(code = 408, message = "Service Timed Out"),
        @ApiResponse(code = 500, message = "Internal Server Error"),
        @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
		@ApiOperation(value = "Update Number of Applicants in master", notes = "API to Update Number of Applicants in master")
		@PostMapping(value = "/updateapplicantscount", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> updateApplicantsCount(@RequestBody UpdateApplicantsCountRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = cobService.updateApplicantsCount(requestWrapper.getApiRequest());
		responseWrapper.setApiResponse(response);
		logger.warn("End : updateApplicantsCount method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

    @ApiResponses({
        @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
        @ApiResponse(code = 408, message = "Service Timed Out"),
        @ApiResponse(code = 500, message = "Internal Server Error"),
        @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
		@ApiOperation(value = "Just to check the application up or not", notes = "Just to check the application up or not")
		@PostMapping(value = "/collectionServiceCheck", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> collectionServiceCheck(@RequestBody UpdateApplicantsCountRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = cobService.collectionServiceCheck();
		responseWrapper.setApiResponse(response);
		logger.warn("End : collectionServiceCheck method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}


    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Master search API", notes = "Master search API")
    @PostMapping(value = "/masterSearchApi", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> masterSearchApi(@RequestBody MasterSearchRequestWrapper requestWrapper) {
        logger.debug("Entered into masterSearchApi method with request: {}", requestWrapper);
        ResponseWrapper responseWrapper = new ResponseWrapper();
        Response response = dashboardService.dashboardMasterSearch(requestWrapper.getApiRequest());
        responseWrapper.setApiResponse(response);
        logger.warn("End : masterSearchApi method response is:: {}" , responseWrapper);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	

	/**
	 * @author Ankit.CAG Update CdhToUnnati only @Additional Product
	 *         (Maitri-Unnnati) CDH to Unnati Side Data Sink function
	 */
	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Update replicate & calling fetchApplication", notes = "API to call replicateApplicationDetails & FetchApplication")
	@PostMapping(value = "/callreplicateupdate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> populateCdhToUnnatiApplicationDetails(
			@RequestBody CallReplicateRequestWrapper callReplicateRequestWrapper) {
		logger.debug("CallReplicateRequestWrapper: {}"+ callReplicateRequestWrapper);

		CallReplicateRequest req = callReplicateRequestWrapper.getCallReplicateRequest();
		CallReplicateFields reqFields = req.getRequestObj();
		HashMap<String, String> callReplicateAppResponse = null;

		/* Response Structure */
		Gson gson = new Gson();
		ResponseWrapper fetchUserDetailsResponseWrapper = new ResponseWrapper();
		Response populateCdhToUnnatiResponse = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();

		/* Calling Replicate Application function */

		logger.debug("callReplicateApplicationDetails: "+ reqFields);

		callReplicateAppResponse = loanService.populateCdhToUnnati(reqFields);

		if (callReplicateAppResponse.get("Status").equalsIgnoreCase("FAIL")) {
			logger.debug("replicateApplicationDetails Failed" + callReplicateAppResponse);
			String jsonString = gson.toJson(callReplicateAppResponse);
			respHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
			respHeader.setHttpStatus(HttpStatus.BAD_REQUEST);
			responseBody.setResponseObj(jsonString);
			populateCdhToUnnatiResponse.setResponseHeader(respHeader);
			populateCdhToUnnatiResponse.setResponseBody(responseBody);
			fetchUserDetailsResponseWrapper.setApiResponse(populateCdhToUnnatiResponse);
			return new ResponseEntity<>(fetchUserDetailsResponseWrapper, HttpStatus.BAD_REQUEST);
		}

		logger.debug("ReplicateApplicationDetails Success response" + callReplicateAppResponse);
		/*
		 * @CallApplication Master
		 */
		return loanService.getApplicationMaster(req,callReplicateAppResponse.get("ApplicationId"));

	}
}
