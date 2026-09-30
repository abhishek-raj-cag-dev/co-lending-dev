package com.iexceed.appzillonbanking.cob.deposit.rest;

import java.util.HashMap;
import java.util.Properties;

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

import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.cob.core.utils.AdapterUtil;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.deposit.payload.CreateDepositRequest;
import com.iexceed.appzillonbanking.cob.deposit.payload.CreateDepositRequestFields;
import com.iexceed.appzillonbanking.cob.deposit.payload.CreateDepositRequestWrapper;
import com.iexceed.appzillonbanking.cob.deposit.payload.DeleteNomineeRequest;
import com.iexceed.appzillonbanking.cob.deposit.payload.DeleteNomineeRequestWrapper;
import com.iexceed.appzillonbanking.cob.deposit.payload.FetchCustDtlRequestWrapper;
import com.iexceed.appzillonbanking.cob.deposit.payload.FetchDeleteUserRequest;
import com.iexceed.appzillonbanking.cob.deposit.payload.FetchDeleteUserRequestWrapper;
import com.iexceed.appzillonbanking.cob.deposit.payload.FetchRoiRequestWrapper;
import com.iexceed.appzillonbanking.cob.deposit.service.DepositService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/deposit")
@Component
@Api(tags = "DEPOSIT", value = "/deposit")
public class DepositAPI {

	private String stringPlaceholder= "%s %s";
    private static final Logger logger = LogManager.getLogger(DepositAPI.class);

    @Autowired
    private DepositService depositService;

    @Autowired
    private AdapterUtil adapterUtil;
    
    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Deposit Opening API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Deposit Opening not reachable")})
    @ApiOperation(value = "Create Deposit Account", notes = "API to Create Deposit Account")
    @PostMapping(value = "/createdeposit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> createDeposit(@RequestBody CreateDepositRequestWrapper requestWrapper,
                                                         @RequestHeader(defaultValue = "APZCOB") String reqAppId,
                                                         @RequestHeader(defaultValue = "createdeposit") String interfaceId,
                                                         @RequestHeader(defaultValue = "000000000002") String userId,
                                                         @RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
                                                         @RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
        Response response=null;
        ResponseWrapper responseWrapper = new ResponseWrapper();
        ResponseEntity<ResponseWrapper> res1= new ResponseEntity<>(responseWrapper, HttpStatus.OK);
        return Mono.just(res1);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking not reachable")})
    @ApiOperation(value = "Fetch Customer Details", notes = "API to Fetch Customer Details")
    @PostMapping(value = "/fetchcustomerdetails", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> fetchCustomerDetails(@RequestBody FetchCustDtlRequestWrapper requestWrapper,
                                                                      @RequestHeader(defaultValue = "APZCOB") String appId,
                                                                      @RequestHeader(defaultValue = "fetchcustomerdetails") String interfaceId,
                                                                      @RequestHeader(defaultValue = "000000000002") String userId,
                                                                      @RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
                                                                      @RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
        Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
        logger.debug(stringPlaceholder, "fetchcustomerdetails Header value :: ", header);
        Mono<Object> response = depositService.fetchCustomerDetails(requestWrapper.getApiRequest(), header);
        logger.debug(stringPlaceholder, "End : Fetch Customer Details response :: ", response.toString());
        return adapterUtil.generateResponseWrapper(response, requestWrapper.getApiRequest().getInterfaceName(), header);
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking not reachable")})
    @ApiOperation(value = "Fetch Customer Nominee Details", notes = "API to Fetch Customer Nominee Details")
    @PostMapping(value = "/fetchnominee", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> fetchNominee(@RequestBody FetchCustDtlRequestWrapper requestWrapper,
                                                        @RequestHeader(defaultValue = "APZCOB") String appId,
                                                        @RequestHeader(defaultValue = "fetchcustomerdetails") String interfaceId,
                                                        @RequestHeader(defaultValue = "000000000002") String userId,
                                                        @RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
                                                        @RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
        Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
        logger.debug(stringPlaceholder, "fetchnominee Header value :: ", header);
       	Mono<Object> response = depositService.fetchNominee(requestWrapper.getApiRequest(), header);
        logger.debug(stringPlaceholder, "End : Fetch Customer Nominee Details response :: ", response.toString());
        return adapterUtil.generateResponseWrapper(response, requestWrapper.getApiRequest().getInterfaceName(), header);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking not reachable")})
    @ApiOperation(value = "Delete Nominee Details", notes = "API to Delete Nominee Details")
    @PostMapping(value = "/deletenominee", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> deleteNominee(@RequestBody DeleteNomineeRequestWrapper requestWrapper) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        DeleteNomineeRequest request = requestWrapper.getApiRequest();
        Response response = depositService.deleteNominee(request);
        responseWrapper.setApiResponse(response);
        logger.debug(stringPlaceholder, "End : Delete Nominee details response :: ", responseWrapper.toString());
        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Deposit API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Check application is present", notes = "API to Check application is present")
    @PostMapping(value = "/checkapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> checkApplication(@RequestBody FetchCustDtlRequestWrapper requestWrapper,
                                                            @RequestHeader(defaultValue = "APZCOB") String appId,
                                                            @RequestHeader(defaultValue = "checkapplication") String interfaceId,
                                                            @RequestHeader(defaultValue = "000000000002") String userId,
                                                            @RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
                                                            @RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
        Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
        Properties prop = null;
        Mono<Response> response=Mono.empty();
        try {
        	prop = CommonUtils.readPropertyFile();
        } catch (Exception e) {
        	logger.error("Error while reading property file in checkApplication for deposits ",e);
			response = CommonUtils.formFailResponseMono(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey()); 
        }
        if(null!=prop) {
        	 response= depositService.checkApplication(requestWrapper.getApiRequest(), header, prop);
        }
        return response.flatMap(val -> {
        	logger.warn(stringPlaceholder, "End : checkApplication method for credit cards response is:: ", val.toString());
            ResponseWrapper responseWrapper = ResponseWrapper.builder().apiResponse(val).build();
            return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
        });
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Deposit API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Fetch customer's data", notes = "API to fetch customer's data")
    @PostMapping(value = "/fetchapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> fetchApplication(@RequestBody FetchDeleteUserRequestWrapper fetchUserRequestWrapper) {
        ResponseWrapper fetchUserDetailsResponseWrapper = new ResponseWrapper();
        FetchDeleteUserRequest req = fetchUserRequestWrapper.getFetchDeleteUserRequest();
        Response response  = depositService.fetchApplication(req);
        fetchUserDetailsResponseWrapper.setApiResponse(response);
        logger.warn(stringPlaceholder, "End : fetchApplication method response is:: ", fetchUserDetailsResponseWrapper.toString());
        return new ResponseEntity<>(fetchUserDetailsResponseWrapper, HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Deposit API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")})
    @ApiOperation(value = "Download Application", notes = "API to Download Application")
    @PostMapping(value = "/downloadapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> downloadApplication(@RequestBody FetchDeleteUserRequestWrapper requestWrapper) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        logger.warn(stringPlaceholder, "Inside downloadApplication", requestWrapper.getFetchDeleteUserRequest());
        Response response = depositService.downloadApplication(requestWrapper.getFetchDeleteUserRequest());
        responseWrapper.setApiResponse(response);
        logger.warn(stringPlaceholder, "End : downloadApplication method response is:: ", responseWrapper.toString());
        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking Deposit API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking Deposit API not reachable")})
    @ApiOperation(value = "Discard Deposit Application", notes = "API to Discard Deposit Application")
    @PostMapping(value = "/discardapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> discardApplication(@RequestBody CreateDepositRequestWrapper discardAppRequestWrapper,
                                                              @RequestHeader(defaultValue = "APZCOB") String reqAppId,
                                                              @RequestHeader(defaultValue = "fetchroi") String interfaceId,
                                                              @RequestHeader(defaultValue = "000000000002") String userId,
                                                              @RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
                                                              @RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper fetchUserDetailsResponseWrapper = new ResponseWrapper();
		CreateDepositRequest req = discardAppRequestWrapper.getApiRequest();
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (Exception e) {
			logger.error("Error while reading property file in discardApplication ", e);
			response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
		}
		if (null != prop) {
			boolean discarded = depositService.discardApplication(req, prop);
			if (discarded) {
				String headerAppId = req.getAppId();
				CreateDepositRequestFields requestObj = req.getRequestObj();
				String appId = requestObj.getAppId();
				req.getRequestObj().setApplicationId(null); // set this to null so that new application Id will be created in createDeposit method.
				boolean isSelfOnBoardingAppId = false;
				if (appId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
					isSelfOnBoardingAppId = true;
				}
				boolean isSelfOnBoardingHeaderAppId = false;
				if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
					isSelfOnBoardingHeaderAppId = true;
				}
				HashMap<String, String> hm=new HashMap<>();
            	hm.put("reqAppId",reqAppId);
            	hm.put("interfaceId",interfaceId);
            	hm.put("userId",userId);
            	hm.put("masterTxnRefNo",masterTxnRefNo);
            	hm.put("deviceId",deviceId);
				Mono<Response> response1 = depositService.createDeposit(hm, req, isSelfOnBoardingAppId, isSelfOnBoardingHeaderAppId, prop, null);
				return response1.flatMap(val -> {
					ResponseWrapper responseWrapper1 = ResponseWrapper.builder().apiResponse(val).build();
					return Mono.just(new ResponseEntity<>(responseWrapper1, HttpStatus.OK));
				});
			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALID_DISCARD.getKey());
				responseBody.setResponseObj(ResponseCodes.INVALID_DISCARD.getValue());
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
			}
		}
		fetchUserDetailsResponseWrapper.setApiResponse(response);
		logger.warn(stringPlaceholder, "End : discardApplication method response is:: ",fetchUserDetailsResponseWrapper.toString());
		ResponseEntity<ResponseWrapper> res1 = new ResponseEntity<>(fetchUserDetailsResponseWrapper, HttpStatus.OK);
		return Mono.just(res1);
	}

    @ApiResponses({
            @ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
            @ApiResponse(code = 408, message = "Service Timed Out"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "AppzillonBanking not reachable")})
    @ApiOperation(value = "Fetch Deposit interest rates", notes = "API to Fetch Deposit interest rates")
    @PostMapping(value = "/fetchroi", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> fetchRoi(@RequestBody FetchRoiRequestWrapper fetchRoiRequestWrapper,
                                                    @RequestHeader(defaultValue = "APZCOB") String appId,
                                                    @RequestHeader(defaultValue = "fetchroi") String interfaceId,
                                                    @RequestHeader(defaultValue = "000000000002") String userId,
                                                    @RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
                                                    @RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
        Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
        logger.debug(stringPlaceholder, "fetchroi Header value :: ", header);
        Mono<Object> response = depositService.fetchRoi(fetchRoiRequestWrapper.getApiRequest(), header);
        logger.debug(stringPlaceholder, "End : Fetch ROI response :: ", response);
        return adapterUtil.generateResponseWrapper(response, fetchRoiRequestWrapper.getApiRequest().getInterfaceName(), header);
    }
}
