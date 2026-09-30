package com.iexceed.appzillonbanking.cob.core.rest;

import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/core")
@Api(tags = "CommonParameters", value = "/core")
@Component
public class CommonParamsAPI {

	@Autowired
	private CommonParamService commonParamService;
	
	private static final Logger logger = LogManager.getLogger(CommonParamsAPI.class);
 	
	@ApiResponses({
		@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
		@ApiResponse(code = 408, message = "Service Timed Out"),
		@ApiResponse(code = 500, message = "Internal Server Error"),
		@ApiResponse(code = 404, message = "AppzillonBanking not reachable") })
	@ApiOperation(value = "Obtain the Common Params", notes = "API to obtain the Common Params")
	@PostMapping(value = "/fetchCommonParams", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity <ResponseWrapper> fetchCommonParams(@RequestBody CommonParamRequestWrapper commonRequestParam) {		
		logger.debug("COB Start : Fetch Common Params with request :: " +commonRequestParam.toString());		
		ResponseWrapper commonParamResponseWrapper = new ResponseWrapper();
		Response commonParamResponse = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();		
		try { 
			commonParamResponse = commonParamService.fetchAllData(commonRequestParam.getApiRequest());
		} 		
		catch (Exception e) {
			logger.error("COB COMMON PARAMS ERROR = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("");

			commonParamResponse.setResponseHeader(responseHeader);
			commonParamResponse.setResponseBody(responseBody);
		}		
		commonParamResponseWrapper.setApiResponse(commonParamResponse);
		logger.debug("End : COB Fetch Common Params with response :: " +commonParamResponseWrapper.toString());
		return new ResponseEntity<>(commonParamResponseWrapper, HttpStatus.OK);
	}
	
	@ApiResponses({
		@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
		@ApiResponse(code = 408, message = "Service Timed Out"),
		@ApiResponse(code = 500, message = "Internal Server Error"),
		@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
@ApiOperation(value = "Fetch Products", notes = "API to fetch products")
@PostMapping(value = "/fetchproducts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ResponseWrapper> fetchProducts() {
		Response fetchproductsResponse = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper fetchproductsResponseWrapper = new ResponseWrapper();
		try {
			fetchproductsResponse = commonParamService.fetchProducts();
		}
				
		catch (Exception e) {
			logger.error("Exception in fetchProducts method = ",e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in fetchProducts method");
			fetchproductsResponse.setResponseHeader(responseHeader);
			fetchproductsResponse.setResponseBody(responseBody);
		}
		fetchproductsResponseWrapper.setApiResponse(fetchproductsResponse);
		logger.warn("End : fetchProducts method response is:: " +fetchproductsResponseWrapper.toString());
		return new ResponseEntity<>(fetchproductsResponseWrapper, HttpStatus.OK);
	}	
	
	@ApiResponses({
		@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
		@ApiResponse(code = 408, message = "Service Timed Out"),
		@ApiResponse(code = 500, message = "Internal Server Error"),
		@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
@ApiOperation(value = "Fetch Products", notes = "API to fetch products")
@PostMapping(value = "/fetchproductdetails", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ResponseWrapper> fetchProductDetails(@RequestBody FetchProductDetailsRequestWrapper fetchProductDetailsRequestWrapper) {
		Response fetchProductDetailsResponse = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper fetchProductDetailsResponseWrapper = new ResponseWrapper();
		try {
			fetchProductDetailsResponse = commonParamService.fetchProductDetails(fetchProductDetailsRequestWrapper.getFetchProductDetailsRequest());
		}
				
		catch (Exception e) {
			logger.error("Exception in fetchProductDetails method = ",e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in fetchProductDetails method");
			fetchProductDetailsResponse.setResponseHeader(responseHeader);
			fetchProductDetailsResponse.setResponseBody(responseBody);
		}
		fetchProductDetailsResponseWrapper.setApiResponse(fetchProductDetailsResponse);
		logger.warn("End : fetchProductDetails method response is:: " +fetchProductDetailsResponseWrapper.toString());
		return new ResponseEntity<>(fetchProductDetailsResponseWrapper, HttpStatus.OK);
	}
	
	@ApiResponses({
		@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
		@ApiResponse(code = 408, message = "Service Timed Out"),
		@ApiResponse(code = 500, message = "Internal Server Error"),
		@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
@ApiOperation(value = "Fetch FAQs", notes = "API to fetch FAQs")
@PostMapping(value = "/fetchfaq", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ResponseWrapper> fetchFaq(@RequestBody FetchFaqRequestWrapper requestWrapper) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper responseWrapper = new ResponseWrapper();
		try {
			response = commonParamService.fetchFaq(requestWrapper.getApiRequest());
		}
				
		catch (Exception e) {
			logger.error("Exception in fetchFaq method = ",e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in fetchFaq method");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : fetchFaq method response is:: " +responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
}