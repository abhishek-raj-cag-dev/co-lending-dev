package com.iexceed.appzillonbanking.cob.rest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.core.utils.*;
import com.iexceed.appzillonbanking.cob.loans.service.T24AndCDHService;
import com.iexceed.appzillonbanking.cob.payload.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.domain.ab.RoleAccessMap;
import com.iexceed.appzillonbanking.cob.loans.payload.ApplyLoanRequest;
import com.iexceed.appzillonbanking.cob.loans.payload.ApplyLoanRequestWrapper;
import com.iexceed.appzillonbanking.cob.loans.payload.ApproveDeviationRaApplicationsReq;
import com.iexceed.appzillonbanking.cob.loans.payload.ApproveDeviationRaApplicationsReqWrapper;
import com.iexceed.appzillonbanking.cob.loans.service.LoanService;
import com.iexceed.appzillonbanking.cob.service.COBService;
import com.iexceed.appzillonbanking.cob.service.CommonService;
import com.iexceed.appzillonbanking.cob.service.SendSmsAndEmailService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/common")
@Component
@Api(tags = "COMMON", value = "/common")
public class CommonAPI {

	private static final Logger logger = LogManager.getLogger(CommonAPI.class);

	@Autowired
	private CommonService commonService;

	@Autowired
	private SendSmsAndEmailService smsAndEmailService;

	@Autowired
	private CommonParamService commonCoreService;

	@Autowired
	private COBService cobBackOffService;

	@Autowired
	private LoanService loanService;

	@Autowired
	private ApplicationMasterRepository applicationMasterRepository;

	@Autowired
	private T24AndCDHService t24AndCDHService;

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Approve or Reject Application", notes = "API to Approve or Reject Application")
	@PostMapping(value = "/approverejectapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> approveRejectApplication(
			@RequestBody FetchDeleteUserRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String applicationId,
			@RequestHeader(defaultValue = "extractocrdata") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		logger.info("Start: approveRejectApplication method");
		Header header = CommonUtils.obtainHeader(applicationId, interfaceId, userId, masterTxnRefNo, deviceId);
		Mono<Response> response = Mono.empty();
		Mono<Object> objResponse = Mono.empty();
		Mono<Object> objResponse2 = Mono.empty();
		Properties prop = null;
        FetchDeleteUserRequest req = requestWrapper.getFetchDeleteUserRequest();
        WorkFlowDetails workFlowDetailsReq = req.getRequestObj().getWorkFlow();
        String applicationIdFromRequest = req.getRequestObj().getApplicationId();
        boolean markedInProgress = false;
		try {
			prop = CommonUtils.readPropertyFile();
			logger.debug("Property file read successfully");
		} catch (IOException e) {
			logger.error("Error while reading property file in approveRejectApplication ", e);
			response = FallbackUtils.genericFallbackMono();
		}
		if (null != prop) {
            boolean isSelfOnBoardingHeaderAppId;
			logger.debug("FetchDeleteUserRequest obtained from wrapper: {}", req);
			String headerAppId = req.getAppId();
			logger.debug("Header App ID: {}", headerAppId);
			if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
				isSelfOnBoardingHeaderAppId = true;
				logger.debug("Self Onboarding Header App ID detected");
            } else {
                isSelfOnBoardingHeaderAppId = false;
			}
            List<String> postDisbursementWorkflows = Arrays.asList(
                    Constants.DISBURSED.toUpperCase(),
                    Constants.LUC.toUpperCase(),
                    Constants.PENDINGLUCVERIFICATION.toUpperCase()
            );
			String rawUserId = req.getRequestObj().getUserId();
			String role = null;
			userId = rawUserId;
			FetchDeleteUserFields requestObj = req.getRequestObj();

			if (rawUserId != null && rawUserId.contains("(") && rawUserId.endsWith(")")) {
				int idx = rawUserId.indexOf('(');
				role = rawUserId.substring(0, idx);
				userId = rawUserId.substring(idx + 1, rawUserId.length() - 1);
			}
			logger.debug("Parsed Role: {}", role);
			logger.debug("Parsed UserId: {}", userId);
			String roleId = (role != null)
					? role
                    : commonCoreService.fetchRoleId(headerAppId, userId);

			logger.debug("Final Role ID: {}", roleId);
			String workflowStatus = requestObj.getWorkFlow().getNextWorkflowStatus();
			if (null == workflowStatus) {
				logger.error("Workflow status is null for applicationID: {}", requestObj.getApplicationId());
				response = CommonUtils.formFailResponseMono("Workflow status is null. Please try again.",
						ResponseCodes.FAILURE.getKey());
				return response.flatMap(val -> {
					ResponseWrapper responseWrapper1 = ResponseWrapper.builder().apiResponse(val).build();
					logger.debug("ResponseWrapper built successfully" + responseWrapper1);
					return Mono.just(new ResponseEntity<>(responseWrapper1, HttpStatus.OK));
				});
			}

            long thresholdMinutes = Long.parseLong(
                    prop.getProperty(CobFlagsProperties.DUPLICATE_REQUEST_THRESHOLD.getKey(), "2"));

            CommonParamService.LockResultWrapper lockResult = commonCoreService.tryMarkInProgress(
                    req.getRequestObj().getApplicationId(),
                    userId,
                    userId + "_INPROGRESS",
                    thresholdMinutes
            );

            switch (lockResult.getResult()) {
                case DUPLICATE:
                    // block duplicate
                    logger.warn("Duplicate request detected for applicationID: {}. User: {} has already initiated an action within the last {} minutes.", requestObj.getApplicationId(), userId, thresholdMinutes);
                    response = CommonUtils.formFailResponseMono("Duplicate request detected. Please wait for " + thresholdMinutes + "minutes and try again.",
							ResponseCodes.FAILURE.getKey());
					return response.flatMap(val -> {
						ResponseWrapper responseWrapper1 = ResponseWrapper.builder().apiResponse(val).build();
						logger.debug("ResponseWrapper built successfully" + responseWrapper1);
						return Mono.just(new ResponseEntity<>(responseWrapper1, HttpStatus.OK));
					});
                case LOCKED_BY_OTHER:
                    logger.warn("Application is locked by another user: {}. Cannot proceed with approve/reject.", lockResult.getLockedByUser());
                    response = CommonUtils.formFailResponseMono("Application is currently being used by user (" + lockResult.getLockedByUser() + ")",
                            ResponseCodes.FAILURE.getKey());
                    return response.flatMap(val -> {
                        ResponseWrapper responseWrapper1 = ResponseWrapper.builder().apiResponse(val).build();
                        logger.debug("ResponseWrapper built successfully" + responseWrapper1);
                        return Mono.just(new ResponseEntity<>(responseWrapper1, HttpStatus.OK));
                    });
                case INVALID:
                    logger.error("Application master not found for applicationId={}", applicationIdFromRequest);
                    response = CommonUtils.formFailResponseMono(
                            "Application not found. Please try again.",
                            ResponseCodes.FAILURE.getKey());
                    return response.flatMap(val -> {
                        ResponseWrapper responseWrapper1 = ResponseWrapper.builder().apiResponse(val).build();
                        return Mono.just(new ResponseEntity<>(responseWrapper1, HttpStatus.OK));
                    });
                case PROCEEDED:
                    markedInProgress = true;
                    break;
				}

            boolean isValidWorkflow = commonCoreService.isValidWorkflow(req.getRequestObj().getApplicationId(), workFlowDetailsReq, headerAppId);
            if (!isValidWorkflow) {
                if (markedInProgress) {
                    applicationMasterRepository.clearSubmitInProgress(
                            applicationIdFromRequest, userId + "_INPROGRESS", userId);
			}
                logger.error("Invalid workflow transition for applicationID: {}. Current workflow Id: {}, Next workflow: {}",
                        requestObj.getApplicationId(), workFlowDetailsReq.getWorkflowId(), workFlowDetailsReq.getNextWorkflowStatus());
                response = CommonUtils.formFailResponseMono("Invalid workflow transition. Please refresh and try again.",
                        ResponseCodes.FAILURE.getKey());
                return response.flatMap(val -> {
                    ResponseWrapper responseWrapper1 = ResponseWrapper.builder().apiResponse(val).build();
                    logger.debug("ResponseWrapper built successfully" + responseWrapper1);
                    return Mono.just(new ResponseEntity<>(responseWrapper1, HttpStatus.OK));
                });
            }
            String action = requestObj.getWorkFlow().getAction();
            Properties finalProp = prop;
            response = buildDeleteGateOnReject(action, req, header, prop)
                    .switchIfEmpty(Mono.defer(() ->
                            routeWorkflow(req, header, finalProp, headerAppId, roleId, isSelfOnBoardingHeaderAppId,
                                    workFlowDetailsReq, postDisbursementWorkflows)));

        }
        logger.info("End: approveRejectApplication method");
        boolean finalMarkedInProgress = markedInProgress;
        String finalUserId = userId;
        return response.flatMap(val -> {
            ResponseWrapper responseWrapper1 = ResponseWrapper.builder().apiResponse(val).build();
            logger.debug("ResponseWrapper built successfully" + responseWrapper1);
            return Mono.just(new ResponseEntity<>(responseWrapper1, HttpStatus.OK));
        }).doFinally(signal -> {
            if (finalMarkedInProgress) {
                applicationMasterRepository.clearSubmitInProgress(
                        applicationIdFromRequest,
                        finalUserId + "_INPROGRESS",
                        finalUserId);
                logger.info("Cleared in-progress marker for applicationId={}, userId={}",
                        applicationIdFromRequest, finalUserId);
            }
        });
    }

    private Mono<Response> buildDeleteGateOnReject(String action, FetchDeleteUserRequest req, Header header, Properties prop) {
        if (!Constants.REJECT.equalsIgnoreCase(action)) {
            return Mono.empty();
        }
		/*if(commonService.isAdditonalProductApplication(req.getRequestObj().getApplicationId())) {
			logger.debug("Action is REJECT. Calling helper method to checkAndRejectActiveLoans");
			return commonService.checkAndRejectActiveLoans(req, header, prop)
					.flatMap(resp -> {
						if (resp instanceof Response) {
							return Mono.just((Response) resp);
						}
						logger.error("Unexpected response type from checkAndRejectActiveLoans : {}",
								resp.getClass().getName());
						return Mono.just(getFailureApiJson("Unexpected response during Loan Rejection",
								Constants.LOAN_REJECTION));
					});
		}
        logger.debug("Action is REJECT. Calling helper method to check if prospect deletion is required.");
        return commonService.deleteProspectCustomerOnRejection(req, header, prop)
                .flatMap(val -> ResponseCodes.SUCCESS.getKey().equalsIgnoreCase(val.getResponseHeader().getResponseCode())
                        ? Mono.<Response>empty()
						: Mono.just(val));*/

		logger.debug("Action is REJECT. Calling helper method to checkAndRejectActiveLoans");

		return commonService.checkAndRejectActiveLoans(req, header, prop)
				.flatMap(resp -> {
					if (resp instanceof Response) {
						Response response = (Response) resp;
						if (ResponseCodes.SUCCESS.getKey()
								.equalsIgnoreCase(
										response.getResponseHeader().getResponseCode())) {
							logger.debug(
									"checkAndRejectActiveLoans completed successfully. "
											+ "Calling helper method to check if prospect deletion is required.");
							return commonService.deleteProspectCustomerOnRejection(req, header, prop)
									.flatMap(val ->
											ResponseCodes.SUCCESS.getKey()
													.equalsIgnoreCase(
															val.getResponseHeader().getResponseCode())
													? Mono.<Response>empty()
                        : Mono.just(val));
						}
						// Failure response from checkAndRejectActiveLoans
						logger.debug(
								"checkAndRejectActiveLoans failed. Response code: {}",
								response.getResponseHeader().getResponseCode());
						return Mono.just(response);
					}

					logger.error(
							"Unexpected response type from checkAndRejectActiveLoans : {}",
							resp.getClass().getName());

					return Mono.just(getFailureApiJson(
							"Unexpected response during Loan Rejection",
							Constants.LOAN_REJECTION));
				});

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

    private Mono<Response> routeWorkflow(FetchDeleteUserRequest req, Header header, Properties prop, String headerAppId,
                                         String roleId, boolean isSelfOnBoardingHeaderAppId,
                                         WorkFlowDetails workFlowDetailsReq, List<String> postDisbursementWorkflows) {
        RoleAccessMap objDb = cobBackOffService.fetchRoleAccessMapObj(headerAppId, roleId);
			logger.debug("Fetched RoleAccessMap: {}", objDb);
        logger.error("Workflow ID: {}", workFlowDetailsReq.getWorkflowId());
        String workflowId = workFlowDetailsReq.getWorkflowId();

        if (Constants.CREDITASSESSMENT.equalsIgnoreCase(workflowId)) {
				logger.error("Inside CREDITASSESSMENT Workflow");
            return commonService.creditAssessmentApplicationMovement(req, prop, roleId);
        } else if (Constants.DBKITGENERATION.equalsIgnoreCase(workflowId)) {
				logger.error("Inside DBKITGENERATION Workflow");
            return commonService.dbkitApplicationMovement(req, prop, roleId);
        } else if (Constants.DISBURSEMENT.equalsIgnoreCase(workflowId)) {
				logger.error("Inside DISBURSEMENT Workflow");
            return commonService.disbursementApplicationMovement(req, header, prop, roleId).map(obj -> (Response) obj);
        } else if (Constants.PENDINGDEVIATION.equalsIgnoreCase(workflowId)) {
				logger.error("Inside PENDINGDEVIATION Workflow");
            return commonService.creditDeviationApplicationMovement(req, prop, roleId);
        } else if (Constants.PENDINGREASSESSMENT.equalsIgnoreCase(workflowId)) {
				logger.error("Inside PENDINGREASSESSMENT Workflow");
            return commonService.creditReassessmentApplicationMovement(req, prop, roleId);
        } else if (Constants.PENDINGPRESANCTION.equalsIgnoreCase(workflowId)) {
				logger.error("Inside PENDINGPRESANCTION Workflow");
            return commonService.preSanctionApplicationMovement(req, prop, roleId);
        } else if (Constants.SANCTION.equalsIgnoreCase(workflowId)) {
				logger.error("Inside SANCTION Workflow");
            return commonService.sanctionApplicationMovement(req, header, prop, roleId).map(obj -> (Response) obj);
        } else if (Constants.RESANCTION.equalsIgnoreCase(workflowId)) {
				logger.debug("Inside RESANCTION Workflow");
            return commonService.ReSanctionApplicationMovement(req, header, prop, roleId).map(obj -> (Response) obj);
        } else if (postDisbursementWorkflows.contains(workflowId)) {
                logger.error("Inside DISBURSED Workflow");
            return commonService.disbursedApplicationMovement(req, prop, roleId);
            }else if (Constants.ACCESS_PERMISSION_VIEWONLY.equalsIgnoreCase(objDb.getAccessPermission())) {
				logger.debug("Inside VIEWONLY permission");
            return CommonUtils.formFailResponseMono(ResponseCodes.VAPT_ISSUE_PERMISSION.getValue(),
						ResponseCodes.VAPT_ISSUE_PERMISSION.getKey());
			} else if (Constants.ACCESS_PERMISSION_APPROVER.equalsIgnoreCase(objDb.getAccessPermission())
					|| Constants.ACCESS_PERMISSION_BOTH.equalsIgnoreCase(objDb.getAccessPermission())
					|| Constants.ACCESS_PERMISSION_VERIFIER.equalsIgnoreCase(objDb.getAccessPermission())
					|| Constants.ACCESS_PERMISSION_INITIATOR.equalsIgnoreCase(objDb.getAccessPermission())) {
				logger.debug("Inside APPROVER permission");
				logger.error("Inside APPROVER Workflow");
            return commonService.approveRejectApplication(req, header, isSelfOnBoardingHeaderAppId, prop, roleId);
			} else if (Constants.ACCESS_PERMISSION_RPC.equalsIgnoreCase(objDb.getAccessPermission())) {
            if (Constants.DBKITVERIFICATION.equalsIgnoreCase(workflowId)) {
					logger.error("Inside DBKITVERIFICATION Workflow");
                return commonService.dbkitVerificationApplicationMovement(req, prop, roleId);
            } else if (Constants.VERIFYAPPLICATION.equalsIgnoreCase(workflowId)) {
					logger.error("Inside VERIFYAPPLICATION Workflow");
                return commonService.stageMovementApplication(req, prop, roleId);
				} else {
					logger.error("Inside SERVICE CALL Workflow - TBD");
				}
			}
        return Mono.empty(); // no branch matched — same fallthrough as the original method
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "common api", notes = "API to perform common functionalities")
	@PostMapping(value = "/commonapi", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public Response commonAPI(@RequestBody CommonAPIRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String applicationId,
			@RequestHeader(defaultValue = "commonapi") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Header header = CommonUtils.obtainHeader(applicationId, interfaceId, userId, masterTxnRefNo, deviceId);
		Mono<Response> response = Mono.empty();
		Response fetchUserDetailsResponse = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in approveRejectApplication ", e);
			response = FallbackUtils.genericFallbackMono();
		}
		if (null != prop) {
			logger.debug("requestWrapper.toString() " + requestWrapper.toString());
			CommonAPIRequest req = requestWrapper.getCommonAPIRequest();
			String flag = req.getInterfaceName();
			logger.debug(" Flag -->" + flag);
			String userid = req.getRequestObj().getUserId();
			logger.debug(" User ID -->" + userid);
			if (flag.equalsIgnoreCase("lockUser")) {
				String applicationid = req.getRequestObj().getApplicationId();
				logger.debug(" Application ID -->" + applicationid);
				int i = applicationMasterRepository.updateTimestampOnLogin(applicationid, userid);
				logger.debug(" records updated -->" + i);
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				responseHeader.setResponseMessage("Success");
				responseBody.setResponseObj("userLocked");
				fetchUserDetailsResponse.setResponseBody(responseBody);
				fetchUserDetailsResponse.setResponseHeader(responseHeader);
				return fetchUserDetailsResponse;
			} else if (flag.equalsIgnoreCase("unlockUser")) {
                int i = applicationMasterRepository.updateLockOut(userid,userid + "_INPROGRESS");
				logger.debug(" records updated -->" + i);
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				responseHeader.setResponseMessage("Success");
				responseBody.setResponseObj("userUnlocked");
				fetchUserDetailsResponse.setResponseBody(responseBody);
				fetchUserDetailsResponse.setResponseHeader(responseHeader);
				return fetchUserDetailsResponse;
			}
		}
		responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
		responseHeader.setResponseMessage("Failure");
		responseBody.setResponseObj("Failed to perform common functionalities");
		fetchUserDetailsResponse.setResponseBody(responseBody);
		fetchUserDetailsResponse.setResponseHeader(responseHeader);
		return fetchUserDetailsResponse;
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Approve Renewal Application", notes = "API to Approve Renewal Application")
	@PostMapping(value = "/approverenewalapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> approveRenewalApplication(
			@RequestBody ApplyLoanRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String applicationId,
			@RequestHeader(defaultValue = "approverenewalapplication") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Header header = CommonUtils.obtainHeader(applicationId, interfaceId, userId, masterTxnRefNo, deviceId);
		Mono<Response> response = Mono.empty();
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in approveRejectApplication ", e);
			response = FallbackUtils.genericFallbackMono();
		}
		if (null != prop) {
			boolean isSelfOnBoardingHeaderAppId = false;
			ApplyLoanRequest req = requestWrapper.getApiRequest();
            WorkFlowDetails workFlowDetailsReq = req.getRequestObj().getWorkflow();
			String headerAppId = req.getAppId();
			if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
				isSelfOnBoardingHeaderAppId = true;
			}


            boolean isValidWorkflow = commonCoreService.isValidWorkflow(req.getRequestObj().getApplicationId(), workFlowDetailsReq, headerAppId);
            if (!isValidWorkflow) {
                logger.error("Invalid workflow transition for applicationID: {}. Current workflow Id: {}, Next workflow: {}",
                        req.getRequestObj().getApplicationId(), workFlowDetailsReq.getWorkflowId(), workFlowDetailsReq.getNextWorkflowStatus());
                response = CommonUtils.formFailResponseMono("Invalid workflow transition. Please refresh and try again.",
                        ResponseCodes.FAILURE.getKey());
                return response.flatMap(val -> {
                    ResponseWrapper responseWrapper1 = ResponseWrapper.builder().apiResponse(val).build();
                    logger.debug("ResponseWrapper built successfully" + responseWrapper1);
                    return Mono.just(new ResponseEntity<>(responseWrapper1, HttpStatus.OK));
                });
            }

            String roleId = commonCoreService.fetchRoleId(headerAppId, req.getUserId());
            RoleAccessMap objDb = cobBackOffService.fetchRoleAccessMapObj(headerAppId, roleId);
			if (Constants.ACCESS_PERMISSION_VIEWONLY.equalsIgnoreCase(objDb.getAccessPermission())
			/*
			 * || Constants.ACCESS_PERMISSION_INITIATOR.equalsIgnoreCase(objDb.
			 * getAccessPermission())
			 */) {
				response = CommonUtils.formFailResponseMono(ResponseCodes.VAPT_ISSUE_PERMISSION.getValue(),
						ResponseCodes.VAPT_ISSUE_PERMISSION.getKey());
			} else if (Constants.ACCESS_PERMISSION_APPROVER.equalsIgnoreCase(objDb.getAccessPermission())
					|| Constants.ACCESS_PERMISSION_BOTH.equalsIgnoreCase(objDb.getAccessPermission())
					|| Constants.ACCESS_PERMISSION_VERIFIER.equalsIgnoreCase(objDb.getAccessPermission())
					|| Constants.ACCESS_PERMISSION_INITIATOR.equalsIgnoreCase(objDb.getAccessPermission())) {
				response = commonService.approveRenewalApplication(req, header, isSelfOnBoardingHeaderAppId, prop);
				logger.debug("Approve Renewal Application Response: " + response.toString());
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
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Initiate Rejected Application", notes = "API to Initiate Rejected Application")
	@PostMapping(value = "/initiaterejectedapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> initiateRejectApplication(
			@RequestBody FetchDeleteUserRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String applicationId,
			@RequestHeader(defaultValue = "extractocrdata") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Header header = CommonUtils.obtainHeader(applicationId, interfaceId, userId, masterTxnRefNo, deviceId);
		Mono<Response> response = Mono.empty();
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in approveRejectApplication ", e);
			response = FallbackUtils.genericFallbackMono();
		}
		if (null != prop) {
			boolean isSelfOnBoardingHeaderAppId = false;
			FetchDeleteUserRequest req = requestWrapper.getFetchDeleteUserRequest();
			String headerAppId = req.getAppId();
			if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
				isSelfOnBoardingHeaderAppId = true;
			}
            String roleId = commonCoreService.fetchRoleId(headerAppId, req.getRequestObj().getUserId());
            RoleAccessMap objDb = cobBackOffService.fetchRoleAccessMapObj(headerAppId, roleId);
			if (Constants.ACCESS_PERMISSION_VIEWONLY.equalsIgnoreCase(objDb.getAccessPermission())) {
				response = CommonUtils.formFailResponseMono(ResponseCodes.VAPT_ISSUE_PERMISSION.getValue(),
						ResponseCodes.VAPT_ISSUE_PERMISSION.getKey());
			} else if (Constants.ACCESS_PERMISSION_APPROVER.equalsIgnoreCase(objDb.getAccessPermission())
					|| Constants.ACCESS_PERMISSION_BOTH.equalsIgnoreCase(objDb.getAccessPermission())
					|| Constants.ACCESS_PERMISSION_VERIFIER.equalsIgnoreCase(objDb.getAccessPermission())
					|| Constants.ACCESS_PERMISSION_INITIATOR.equalsIgnoreCase(objDb.getAccessPermission())) {
				response = commonService.initiateRejectedApplication(req, header, isSelfOnBoardingHeaderAppId, prop);
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
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Insert rejected data and call fetch application", notes = "API to Insert rejected data and call fetch application")
	@PostMapping(value = "/modifyrejectapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> populateRejectedData(
			@RequestBody PopulateRejectedDataRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String applicationId,
			@RequestHeader(defaultValue = "extractocrdata") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Response response = null;
		Properties prop = null;
		ResponseWrapper responseWrapper = new ResponseWrapper();
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (Exception e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
			response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
		}
		if (null != prop) {
			boolean isSelfOnBoardingAppId = false;
			PopulateRejectedDataRequest request = requestWrapper.getApiRequest();
			String appId = request.getRequestObj().getAppId();
			if (appId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
				isSelfOnBoardingAppId = true;
			}
			response = commonService.populateRejectedDataInAllTables(request, isSelfOnBoardingAppId, prop);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("%s %s", "End : populateRejectedData method response is:: ", responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Fetch the PinCode details", notes = "API to Fetch state/country/area details based on API")
	@PostMapping(value = "/pinCode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchPinCodeDetails(@RequestBody PinCodeRequestWrapper requestWrapper) {
		logger.warn("start: Fetch data based pinCodeDetails request: " + requestWrapper.toString());
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response pinCodeResponse = commonService.fetchDetailsBasedOnPinCode(requestWrapper.getApiRequest());
		responseWrapper.setApiResponse(pinCodeResponse);
		logger.warn("End : Fetch data based pinCodeDetails response is :: ", responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Send email and SMS", notes = "API to send email and sms to customer")
	@PostMapping(value = "/sendSmsAndEmail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> sendSmsandEmail(
			@RequestBody SendSmsEmailRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in approveRejectApplication ", e);
		}
		if (null != prop) {
			logger.warn("start: send email and sms request: " + requestWrapper.toString());
			logger.debug("Action Type in SMS :" + requestWrapper.getApiRequest().getRequestObject().getActionType());
			if(requestWrapper.getApiRequest().getRequestObject().getActionType().equalsIgnoreCase(Constants.SANCTION)) {
				logger.debug("Sanction SMS started");
				Response sendSanctionSmsResp = commonService.sendSanctionSms(requestWrapper.getApiRequest().getApplicationId(),
						requestWrapper.getApiRequest().getAppId(), prop, false);
				responseWrapper.setApiResponse(sendSanctionSmsResp);
				logger.warn("End : send email and sms sanction response is :: ", responseWrapper.toString());
			} else if(requestWrapper.getApiRequest().getRequestObject().getActionType().equalsIgnoreCase(Constants.DISBURSED)) {
				logger.debug("Disbursed SMS started");
				Response sendDisbursementSmsResp = commonService.sendSanctionSms(requestWrapper.getApiRequest().getApplicationId(),
						requestWrapper.getApiRequest().getAppId(), prop, true);
				responseWrapper.setApiResponse(sendDisbursementSmsResp);
				logger.warn("End : send email and sms disbursement response is :: ", responseWrapper.toString());
			}else {
			Response smsAndEmailResponse = smsAndEmailService.sendSmsAndEmailService(requestWrapper.getApiRequest(),
					prop, SmsStage.OTP);
			responseWrapper.setApiResponse(smsAndEmailResponse);
			logger.warn("End : send email and sms CB response is :: ", responseWrapper.toString());
			}
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			responseWrapper.setApiResponse(response);
		}
		return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Apply for loan and reject immediately", notes = "API to Apply for loan and reject the application")
	@PostMapping(value = "/applyrejectapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> applyRejectApplication(
			@RequestBody ApplyLoanRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZCOB") String reqAppId,
			@RequestHeader(defaultValue = "applyloan") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper responseWrapper = new ResponseWrapper();
		try {
			boolean isSelfOnBoardingHeaderAppId = false;
			logger.debug("Incoming request for apply reject loan: {}", requestWrapper);
			ApplyLoanRequest apiRequest = requestWrapper.getApiRequest();
			String headerAppId = apiRequest.getAppId();
			String appId = apiRequest.getRequestObj().getAppId();
			Properties prop = CommonUtils.readPropertyFile();
			if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
				isSelfOnBoardingHeaderAppId = true;
			}
			// if (loanService.isValidStage(apiRequest, isSelfOnBoardingHeaderAppId, array))
			// { // VAPT
			logger.debug("After stage validation");
			// if (loanService.isVaptPassedForScreenElements(apiRequest, array)) { // VAPT
			logger.debug("After field validation");
			Mono<Response> response1 = loanService.applyRejectLoan(apiRequest, isSelfOnBoardingHeaderAppId, prop);
			return response1.flatMap(val -> {
				// loanService.updateRelatedApplnIdDetails(apiRequest, appId);
				ResponseWrapper responseWrapper1 = ResponseWrapper.builder().apiResponse(val).build();
				return Mono.just(new ResponseEntity<>(responseWrapper1, HttpStatus.OK));
			});

			/*
			 * } else { logger.debug("VAPT failed for screen elements for loans"); response
			 * = CommonUtils.formFailResponse(ResponseCodes.VAPT_ISSUE_FIELDS.getValue(),
			 * ResponseCodes.VAPT_ISSUE_FIELDS.getKey()); }
			 */

			/*
			 * } else { logger.debug("VAPT failed for stage validation for loans"); response
			 * = CommonUtils.formFailResponse(ResponseCodes.VAPT_ISSUE_STAGE.getValue(),
			 * ResponseCodes.VAPT_ISSUE_STAGE.getKey()); }
			 */

		} catch (Exception e) {
			logger.error("Exception in applyLoan method = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in apply reject Loan method");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : apply reject Loan method response is:: {}", responseWrapper.toString());
		ResponseEntity<ResponseWrapper> res1 = new ResponseEntity<>(responseWrapper, HttpStatus.OK);
		return Mono.just(res1);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "approve for deviation or reassesment applications", notes = "API to approve for deviation or reassesment applications")
	@PostMapping(value = "/approveDeviationRaApplications", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> approveDeviationRaApplications(
			@RequestBody ApproveDeviationRaApplicationsReqWrapper approveDeviationRaApplicationsReqWrapper) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper responseWrapper = new ResponseWrapper();
		ApproveDeviationRaApplicationsReq apiRequest = approveDeviationRaApplicationsReqWrapper.getApiRequest();
		try {
			logger.debug("Incoming api request for approveDeviationRaApplications: {}", apiRequest);
			response = commonService.approveDeviationRaApplications(apiRequest);
		} catch (Exception e) {
			logger.error("Exception in applyLoan method = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj(Constants.SOAP_ERROR_MSG);
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : approveDeviationRaApplications method response is:: {}", responseWrapper);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
}
