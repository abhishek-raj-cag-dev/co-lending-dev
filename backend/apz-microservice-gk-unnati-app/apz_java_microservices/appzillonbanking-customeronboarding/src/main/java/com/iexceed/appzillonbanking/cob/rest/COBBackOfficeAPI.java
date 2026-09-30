package com.iexceed.appzillonbanking.cob.rest;

import java.io.IOException;
import java.util.Properties;

import com.iexceed.appzillonbanking.cob.payload.*;
import com.iexceed.appzillonbanking.cob.report.rpcReports.RpcReports;
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
import com.iexceed.appzillonbanking.cob.core.payload.PopulateapplnWFRequestWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.service.COBService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/cob")
@Component
@Api(tags = "COB", value = "/cob")
public class COBBackOfficeAPI {

	private static final Logger logger = LogManager.getLogger(COBBackOfficeAPI.class);

	@Autowired
	private COBService cobBackOffService;

	@Autowired
	private CommonParamService commonService;

    @Autowired
    private RpcReports rpcReportService;

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Create a new Role", notes = "API to Create a new Role")
	@PostMapping(value = "/createrole", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> createRole(@RequestBody CreateRoleRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = cobBackOffService.createRole(requestWrapper.getCreateRoleRequest());
		responseWrapper.setApiResponse(response);
		logger.warn("End : createRole method response is:: " , responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Fetch Roles", notes = "API to Fetch Roles")
	@PostMapping(value = "/fetchrole", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchRole(@RequestBody FetchRoleRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = null;
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in fetchRole ", e);
			response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
		}
		if (null != prop) {
			response = cobBackOffService.fetchRole(requestWrapper.getFetchRoleRequest(), prop);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : fetchRole method response is:: " , responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Error Logging", notes = "Logging Errors in DB")
	@PostMapping(value = "/fetchdashboard", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> logError(@RequestBody ErrorLoggerRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = null;
			response = cobBackOffService.logError(requestWrapper.getErrorLoggerRequest());
		responseWrapper.setApiResponse(response);
		logger.warn("End : fetchRole method response is:: " , responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	
	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Fetch RPC Role Data", notes = "API to Fetch RPC Roles Data")
	@PostMapping(value = "/fetchRPCRoleData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchRPCRoleData(@RequestBody FetchRoleRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = null;
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in fetchRole ", e);
			response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
		}
		if (null != prop) {
			response = cobBackOffService.fetchRPCData(requestWrapper.getFetchRoleRequest(), prop);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : fetchRole method response is:: ", responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	
	@ApiResponses({
		@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
		@ApiResponse(code = 408, message = "Service Timed Out"),
		@ApiResponse(code = 500, message = "Internal Server Error"),
		@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
@ApiOperation(value = "Fetch TAT Report", notes = "API to Fetch TAT Report")
@PostMapping(value = "/fetchTATReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ResponseWrapper> fetchTATReport(@RequestBody RpcReportRequestWrapper requestWrapper) {
	ResponseWrapper responseWrapper = new ResponseWrapper();
	Response response = null;
	Properties prop = null;
	try {
		prop = CommonUtils.readPropertyFile();
	} catch (IOException e) {
		logger.error("Error while reading property file in fetchTATReport ", e);
		response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
	}
	if (null != prop) {
		response = rpcReportService.generateReport(requestWrapper.getApiRequest(), prop);
	}
	responseWrapper.setApiResponse(response);
	logger.warn("End : fetchTATReport method response is:: " , responseWrapper.toString());
	return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
}

	@ApiResponses({
		@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
		@ApiResponse(code = 408, message = "Service Timed Out"),
		@ApiResponse(code = 500, message = "Internal Server Error"),
		@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
@ApiOperation(value = "Fetch State Master", notes = "API to Fetch State Master")
@PostMapping(value = "/fetchStateMaster", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ResponseWrapper> fetchStates() {
	ResponseWrapper responseWrapper = new ResponseWrapper();
	Response response = null;
	Properties prop = null;
	try {
		prop = CommonUtils.readPropertyFile();
	} catch (IOException e) {
		logger.error("Error while reading property file in fetchStateMaster ", e);
		response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
	}
	if (null != prop) {
		response = cobBackOffService.fetchStateMaster();
	}
	responseWrapper.setApiResponse(response);
	logger.warn("End : fetchStateMaster method response is:: " , responseWrapper.toString());
	return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
}
	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Delete Role", notes = "API to Fetch Roles")
	@PostMapping(value = "/deleterole", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> deleteRole(@RequestBody FetchRoleRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = cobBackOffService.deleteRole(requestWrapper.getFetchRoleRequest());
		responseWrapper.setApiResponse(response);
		logger.warn("End : deleteRole method response is:: " , responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Search Applications", notes = "API to Search Applications")
	@PostMapping(value = "/searchapplications", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> searchApplications(@RequestBody SearchAppRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = cobBackOffService.searchApplications(requestWrapper.getApiRequest());
		responseWrapper.setApiResponse(response);
		logger.warn("End : searchApplications method response is:: " , responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Assign Application to user", notes = "API to Assign Application to user")
	@PostMapping(value = "/assignapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> assignApplication(
			@RequestBody AssignApplicationRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = cobBackOffService.assignApplication(requestWrapper.getApiRequest());
		responseWrapper.setApiResponse(response);
		logger.warn("End : assignApplication method response is:: " , responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Populate application workflow", notes = "API to populate application workflow")
	@PostMapping(value = "/populateapplnworkFlow", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> populateapplnworkFlow(@RequestBody PopulateapplnWFRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = commonService.populateApplnWorkFlow(requestWrapper.getApiRequest());
		cobBackOffService.updateStatusInMaster(requestWrapper.getApiRequest()); // If INITIATOR assigns and submits change the master status to PENDING
		responseWrapper.setApiResponse(response);
		logger.warn("End : populateapplnworkFlow method response is:: " , responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "View All Records", notes = "API to View All Records of a perticular widget")
	@PostMapping(value = "/viewall", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> viewAllRecords(@RequestBody ViewAllRecordsRequestWrapper requestWrapper) {
		Response response = new Response();
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in viewAllRecords ", e);
			response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
		}
		if(null!=prop) {
			response = cobBackOffService.viewAllRecords(requestWrapper.getApiRequest(), prop);	
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : viewAllRecords method response is:: " , responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Status Report and count for each widget", notes = "API to View Status Report and count for each widget")
	@PostMapping(value = "/statusreport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> statusReport(@RequestBody StatusReportRequestWrapper requestWrapper) {
		Response response=null;
		ResponseWrapper responseWrapper=new ResponseWrapper();
		Properties prop=null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in statusReport ", e);
			response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
		}
		if(null!=prop) {
			response = cobBackOffService.statusReport(requestWrapper.getApiRequest(), prop);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : statusReport method response is:: ", responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Fetch reject history", notes = "API to Fetch reject history")
	@PostMapping(value = "/rejecthistory", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> rejectHistory(@RequestBody PopulateRejectedDataRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = cobBackOffService.rejectHistory(requestWrapper.getApiRequest());
		responseWrapper.setApiResponse(response);
		logger.warn("End : rejectHistory method response is:: ", responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Advance Search Applications", notes = "API to Advance Search Applications")
	@PostMapping(value = "/advanceSearch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> advanceSearchApplications(@RequestBody AdvanceSearchAppRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = cobBackOffService.advanceSearchApplications(requestWrapper.getApiRequest());
		responseWrapper.setApiResponse(response);
		logger.warn("End : Advance SearchApplications method response is:: ", responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
}
