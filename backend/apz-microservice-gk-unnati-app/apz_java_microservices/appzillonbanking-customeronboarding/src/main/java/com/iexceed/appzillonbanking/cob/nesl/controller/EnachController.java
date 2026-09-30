package com.iexceed.appzillonbanking.cob.nesl.controller;

import java.io.IOException;
import java.util.Properties;

import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.nesl.payload.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import com.iexceed.appzillonbanking.cob.core.utils.AdapterUtil;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.loans.payload.MergeImageToPdfRequestWrapper;
import com.iexceed.appzillonbanking.cob.nesl.service.EnachService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/enach")
@Component
@Api(tags = "ENACH", value = "/enach")
public class EnachController {
	
	private static final Logger logger = LogManager.getLogger(EnachController.class);
	private String stringPlaceholder = "%s %s";
	
	@Autowired
	EnachService enachService;
	
	@Autowired
	private AdapterUtil adapterUtil;
	

	
	@ApiResponses({
	@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
	@ApiResponse(code = 408, message = "Service Timed Out"),
	@ApiResponse(code = 500, message = "Internal Server Error"),
	@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
@ApiOperation(value = "Verify Enach", notes = "API to Verify Enach")
@PostMapping(value = "/rppservice", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)	
public Mono<ResponseEntity<ResponseWrapper>> verifyEnach(@RequestBody EnachRequestWrapper enachRequestWrapper,
		@RequestHeader(defaultValue = "APZCBO") String appId,
		@RequestHeader(defaultValue = "RPPService") String interfaceId,
		@RequestHeader(defaultValue = "000000000002") String userId,
		@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
		@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
	Properties prop = null;
	logger.debug("Recieved request for verifyEnach: {}", enachRequestWrapper);
	try {
		prop = CommonUtils.readPropertyFile();
	} catch (IOException e) {
		logger.error("Error while reading property file in populateRejectedData ", e);
	}

	Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
	if (null != prop) {
		logger.debug(stringPlaceholder, "verifyEnach Header value :: ", header);
		Mono<Object> response = enachService.enachrpp(enachRequestWrapper.getApiRequest(), header, prop);
		logger.debug(stringPlaceholder, "End : verifyEnach response :: ", response);
		return adapterUtil.generateResponseWrapper(response, enachRequestWrapper.getApiRequest().getInterfaceName(),
				header);
	} else {
		Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
				ResponseCodes.FAILURE.getKey());
		ResponseWrapper responseWrapper = new ResponseWrapper();
		responseWrapper.setApiResponse(response);
		return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
	}

}
	
	
	//requsest need only paynimoRequestId = 'P27032-124656299.AJ1468'
	@ApiResponses({
	@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
	@ApiResponse(code = 408, message = "Service Timed Out"),
	@ApiResponse(code = 500, message = "Internal Server Error"),
	@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
@ApiOperation(value = "Pull Transaction Status", notes = "API to check Transaction Status of enach")
@PostMapping(value = "/transactionstatus", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)	
public Mono<ResponseEntity<ResponseWrapper>> transactionStatus(@RequestBody EnachRequestWrapper enachRequestWrapper,
		@RequestHeader(defaultValue = "APZCBO") String appId,
		@RequestHeader(defaultValue = "TransactionStatus") String interfaceId,
		@RequestHeader(defaultValue = "000000000002") String userId,
		@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
		@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
	Properties prop = null;
	logger.debug("Recieved request for transactionStatus: {}", enachRequestWrapper);
	try {
		prop = CommonUtils.readPropertyFile();
	} catch (IOException e) {
		logger.error("Error while reading property file in populateRejectedData ", e);
	}

	Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
	if (null != prop) {
		logger.debug(stringPlaceholder, "transactionStatus Header value :: ", header);
		Mono<Object> response = enachService.transactionStatus(enachRequestWrapper.getApiRequest(), header, prop);
		logger.debug(stringPlaceholder, "End : transactionStatus response :: ", response);
		return adapterUtil.generateResponseWrapper(response, enachRequestWrapper.getApiRequest().getInterfaceName(),
				header);
	} else {
		Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
				ResponseCodes.FAILURE.getKey());
		ResponseWrapper responseWrapper = new ResponseWrapper();
		responseWrapper.setApiResponse(response);
		return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
	}

}
	

}
