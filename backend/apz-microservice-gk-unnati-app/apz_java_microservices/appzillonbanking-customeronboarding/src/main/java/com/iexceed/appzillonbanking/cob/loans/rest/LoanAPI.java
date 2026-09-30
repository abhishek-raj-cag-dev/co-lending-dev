package com.iexceed.appzillonbanking.cob.loans.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.iexceed.appzillonbanking.cob.core.utils.*;
import com.iexceed.appzillonbanking.cob.loans.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.BCMPIMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.DBKITMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.InsurancePremiumMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.SanctionMaster;
import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.cob.core.repository.ab.BCMPIMasterRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.DBKITMasterRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.InsurancePremiumMasterRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.SanctionMasterRepository;
import com.iexceed.appzillonbanking.cob.loans.payload.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/loan")
@Component
@Api(tags = "LOAN", value = "/loan")
public class LoanAPI {

	private static final Logger logger = LogManager.getLogger(LoanAPI.class);
	private String stringPlaceholder = "%s %s";

	@Autowired
	private LoanService loanService;

	@Autowired
	private SourcingService sourcingService;

	@Autowired
	private BIPImageService bipImageService;

	@Autowired
	private IncomeAssessmentService incomeAssessmentService;

	@Autowired
	private AdapterUtil adapterUtil;

	@Autowired
    private SanctionMasterRepository sanctionMasterRepository;

	private final BCMPIService bcmpiService;
	private final BCMPIMasterRepository bcmpiMasterRepository;
	private final DBKITService dbkitService;
	private final DBKITMasterRepository dbkitMasterRepository;

	private final InsurancePremiumMasterRepository insurancePremiumMasterRepository;

	public LoanAPI(BCMPIService bcmpiService, BCMPIMasterRepository bcmpiMasterRepository,
			InsurancePremiumMasterRepository insurancePremiumMasterRepository, DBKITService dbkitService, DBKITMasterRepository dbkitMasterRepository) {
		this.bcmpiMasterRepository = bcmpiMasterRepository;
		this.bcmpiService = bcmpiService;
		this.insurancePremiumMasterRepository = insurancePremiumMasterRepository;
		this.dbkitService = dbkitService;
		this.dbkitMasterRepository = dbkitMasterRepository;
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking loan API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Apply for loan", notes = "API to Apply for loan")
	@PostMapping(value = "/applyloan", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> applyLoan(@RequestBody ApplyLoanRequestWrapper requestWrapper,
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
			boolean isSelfOnBoardingAppId = false;
			boolean isSelfOnBoardingHeaderAppId = false;
			logger.debug("Incoming request: " + requestWrapper);
			ApplyLoanRequest apiRequest = requestWrapper.getApiRequest();
			String headerAppId = apiRequest.getAppId();
			String appId = apiRequest.getRequestObj().getAppId();
			Properties prop = CommonUtils.readPropertyFile();
			if (appId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
				isSelfOnBoardingAppId = true;
			}
			if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
				isSelfOnBoardingHeaderAppId = true;
			}
			JSONArray array = loanService.fetchFunctionSeqArray(isSelfOnBoardingHeaderAppId);
			logger.debug("array for loans is " + array);
			// if (loanService.isValidStage(apiRequest, isSelfOnBoardingHeaderAppId, array))
			// { // VAPT
			logger.debug("After stage validation");
			// if (loanService.isVaptPassedForScreenElements(apiRequest, array)) { // VAPT
			logger.debug("After field validation");
			HashMap<String, String> hm = new HashMap<>();
			hm.put("reqAppId", reqAppId);
			hm.put("interfaceId", interfaceId);
			hm.put("userId", userId);
			hm.put("masterTxnRefNo", masterTxnRefNo);
			hm.put("deviceId", deviceId);
			Mono<Response> response1;
			if (Constants.YES.equalsIgnoreCase(apiRequest.getRequestObj().getIsNewSourcing())){
				response1 = sourcingService.nextSourcingStage(hm, apiRequest, prop);
			} else {
				response1 = loanService.applyLoan(hm, apiRequest, isSelfOnBoardingAppId,
					isSelfOnBoardingHeaderAppId, prop, array);
			}
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
			responseBody.setResponseObj("Exception in applyLoan method");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : applyLoan method response is:: " + responseWrapper.toString());
		ResponseEntity<ResponseWrapper> res1 = new ResponseEntity<>(responseWrapper, HttpStatus.OK);
		return Mono.just(res1);
	}
	
	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking not reachable") })
	@ApiOperation(value = "Fetch Customer Details", notes = "API to Fetch Customer Details")
	@PostMapping(value = "/fetchcustomerdetails", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> fetchCustomerDetails(
			@RequestBody FetchCustDtlRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZCOB") String appId,
			@RequestHeader(defaultValue = "fetchcustomerdetails") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("fetchcustomerdetails Header value :: " + header);
		Mono<Object> response;
		try {
			response = loanService.fetchCustomerDetails(requestWrapper.getApiRequest(), header);
		} catch (Exception e) {
			logger.error("Fetch Customer Details ERROR = ", e);
			response = FallbackUtils.genericFallbackMonoObject();
		}
		logger.debug("End : Fetch Customer Details response :: " + response.toString());
		return adapterUtil.generateResponseWrapper(response, requestWrapper.getApiRequest().getInterfaceName(), header);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Loan API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Check application is present", notes = "API to Check application is present")
	@PostMapping(value = "/checkapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> checkApplication(
			@RequestBody CheckApplicationRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZCOB") String appId,
			@RequestHeader(defaultValue = "checkapplication") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Mono<Response> response;
		try {
			Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
			response = loanService.checkApplication(requestWrapper.getApiRequest(), header);
		} catch (Exception e) {
			response = FallbackUtils.genericFallbackMono();
		}
		return response.flatMap(val -> {
			logger.warn("End : checkApplication method for credit cards response is:: " + val.toString());
			ResponseWrapper responseWrapper = ResponseWrapper.builder().apiResponse(val).build();
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		});
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Customer onboarding API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Fetch customer's data", notes = "API to fetch customer's data")
	@PostMapping(value = "/fetchapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchApplication(@RequestBody FetchAppRequestWrapper requestWrapper) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper responseWrapper = new ResponseWrapper();
		FetchAppRequest req = requestWrapper.getApiRequest();
		try {
			response = loanService.fetchApplication(req);
		} catch (Exception e) {
			logger.error("Exception in fetchApplication method for loans = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in fetchApplication method for loans");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : fetchApplication method response for loans is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Loan API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Loan API not reachable") })
	@ApiOperation(value = "Discard Loan Application", notes = "API to Discard Loan Application")
	@PostMapping(value = "/discardapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> discardApplication(@RequestBody ApplyLoanRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZCOB") String reqAppId,
			@RequestHeader(defaultValue = "discardapplication") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Mono<Response> response = null;
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper fetchUserDetailsResponseWrapper = new ResponseWrapper();
		ApplyLoanRequest req = requestWrapper.getApiRequest();
		Response res = new Response();
		try {
			boolean discarded = loanService.discardApplication(req);
			if (discarded) {
				String headerAppId = req.getAppId();
				String appId = req.getRequestObj().getAppId();
				req.getRequestObj().setApplicationId(null); // set this to null so that new application Id will be
															// created in createDeposit method.
				Properties prop = CommonUtils.readPropertyFile();
				boolean isSelfOnBoardingAppId = false;
				if (appId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
					isSelfOnBoardingAppId = true;
				}
				boolean isSelfOnBoardingHeaderAppId = false;
				if (headerAppId.equalsIgnoreCase(prop.getProperty(CobFlagsProperties.APPID_SELF_ONBOARDING.getKey()))) {
					isSelfOnBoardingHeaderAppId = true;
				}
				HashMap<String, String> hm = new HashMap<>();
				hm.put("reqAppId", reqAppId);
				hm.put("interfaceId", interfaceId);
				hm.put("userId", userId);
				hm.put("masterTxnRefNo", masterTxnRefNo);
				hm.put("deviceId", deviceId);
				response = loanService.applyLoan(hm, req, isSelfOnBoardingAppId, isSelfOnBoardingHeaderAppId, prop,
						null);
				return response.flatMap(val -> {
					ResponseWrapper responseWrapper1 = ResponseWrapper.builder().apiResponse(val).build();
					return Mono.just(new ResponseEntity<>(responseWrapper1, HttpStatus.OK));
				});
			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALID_DISCARD.getKey());
				responseBody.setResponseObj(ResponseCodes.INVALID_DISCARD.getValue());
				res.setResponseBody(responseBody);
				res.setResponseHeader(responseHeader);
			}
		} catch (Exception e) {
			logger.error("Exception in discardApplication method for loans = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in discardApplication method for loans");
			res.setResponseHeader(responseHeader);
			res.setResponseBody(responseBody);
		}
		fetchUserDetailsResponseWrapper.setApiResponse(res);
		logger.warn("End : discardApplication method response for loans is:: "
				+ fetchUserDetailsResponseWrapper.toString());
		ResponseEntity<ResponseWrapper> res1 = new ResponseEntity<>(fetchUserDetailsResponseWrapper, HttpStatus.OK);
		return Mono.just(res1);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Loan API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Loan API not reachable") })
	@ApiOperation(value = "Discard Loan Co Application", notes = "API to Discard Loan Co Application")
	@PostMapping(value = "/discardcoapplicant", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> discardCoApplicant(
			@RequestBody DiscardCoApplicantRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZCOB") String reqAppId,
			@RequestHeader(defaultValue = "discardcoapplicant") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper DiscardCoApplicantRequestWrapper = new ResponseWrapper();
		DiscardCoApplicantRequest req = requestWrapper.getApiRequest();
		Response res = new Response();
		try {
			boolean discarded = loanService.discardApplicant(req);
			if (discarded) {
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				responseBody.setResponseObj(ResponseCodes.SUCCESS.getValue());
				res.setResponseBody(responseBody);
				res.setResponseHeader(responseHeader);
			} else {
				responseHeader.setResponseCode(ResponseCodes.INVALID_DISCARD.getKey());
				responseBody.setResponseObj(ResponseCodes.INVALID_DISCARD.getValue());
				res.setResponseBody(responseBody);
				res.setResponseHeader(responseHeader);
			}
		} catch (Exception e) {
			logger.error("Exception in discardApplication method for loans = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in discardApplicant method for loans");
			res.setResponseHeader(responseHeader);
			res.setResponseBody(responseBody);
		}
		DiscardCoApplicantRequestWrapper.setApiResponse(res);
		logger.warn("End : downloadApplication method response is:: " + DiscardCoApplicantRequestWrapper);
		return new ResponseEntity<>(DiscardCoApplicantRequestWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Deposit API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Download Application", notes = "API to Download Application")
	@PostMapping(value = "/downloadapplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> downloadApplication(@RequestBody FetchAppRequestWrapper requestWrapper) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper responseWrapper = new ResponseWrapper();
		try {
			logger.warn("Inside downloadApplication" + requestWrapper.getApiRequest());
			response = loanService.downloadApplication(requestWrapper.getApiRequest());
		} catch (Exception e) {
			logger.error("Exception in downloadApplication method = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in downloadApplication method");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : downloadApplication method response is:: " + responseWrapper);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "KYC Validate", notes = "API to Valide the KYC")
	@PostMapping(value = "/validatekyc", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> validatekyc(
			@RequestBody ValidatKycRequestWrapper validateKycRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "validateKyc") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "validate kyc Header value :: ", header);
			Mono<Object> response = loanService.validateKyc(validateKycRequestWrapper.getApiRequest(), header, prop);
			logger.debug(stringPlaceholder, "End : Validate KYC response :: ", response);
			return adapterUtil.generateResponseWrapper(response,
					validateKycRequestWrapper.getApiRequest().getInterfaceName(), header);
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}

	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "KYC Dedupe", notes = "API to check the kyc dedupe")
	@PostMapping(value = "/kycDedupe", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> kycDedupe(@RequestBody KycDedupeRequestWrapper kycDedupeRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "kycDedupe") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "Dedupe kyc Header value :: ", header);
			Mono<Object> response = loanService.kycDedupe(kycDedupeRequestWrapper.getApiRequest(), header, prop);
			logger.debug(stringPlaceholder, "End : Dedupe KYC response :: ", response);
			return adapterUtil.generateResponseWrapper(response,
					kycDedupeRequestWrapper.getApiRequest().getInterfaceName(), header);
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}

	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "Fetch IFSC", notes = "API to check the fetch IFSC")
	@PostMapping(value = "/fetchIFSC", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> fetchIFSC(@RequestBody FetchIFSCRequestWrapper fetchIFSCRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "fetchIFSC") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "Fetch IFSC Header value :: ", header);
			Mono<Object> response = loanService.fetchIFSC(fetchIFSCRequestWrapper.getApiRequest(), header, prop);
			logger.debug(stringPlaceholder, "End : Fetch IFSC response :: ", response);
			return adapterUtil.generateResponseWrapper(response,
					fetchIFSCRequestWrapper.getApiRequest().getInterfaceName(), header);
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}

	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "KYC Dedupe", notes = "API to get existing loan details")
	@PostMapping(value = "/fetchExistingLoan", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> fetchExistingLoan(
			@RequestBody ExistingLoanRequestWrapper existingLoanRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "fetchExistingLoan") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "Existing loan fetch Header value :: ", header);
			Mono<Object> response = loanService.fetchExistingLoan(existingLoanRequestWrapper.getApiRequest(), header,
					prop);
			logger.debug(stringPlaceholder, "End : Existing loan fetch response :: ", response);
			return adapterUtil.generateResponseWrapper(response,
					existingLoanRequestWrapper.getApiRequest().getInterfaceName(), header);
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}

	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "Highmark Check response", notes = "API to check the highmark")
	@PostMapping(value = "/highmarkCheckCallback", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> highmarkCheckCallback(
			@RequestBody HighMarkCheckCBRequestWrapper highMarkCheckCBRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "HighmarkCallback") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug(stringPlaceholder, "Highmark callback Header value :: ", header);
		Mono<Object> response = loanService.highmarkCheckCallback(highMarkCheckCBRequestWrapper.getApiRequest(),
				header);
		logger.debug(stringPlaceholder, "End : Highmark callback response :: ", response);
		return adapterUtil.generateResponseWrapper(response,
				highMarkCheckCBRequestWrapper.getApiRequest().getInterfaceName(), header);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "Highmark Renewal Check response", notes = "API to check the highmark Renewal")
	@PostMapping(value = "/highmarkRenewalCheckCallback", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> highmarkRenewalCheckCallback(
			@RequestBody HighMarkCheckCBRequestWrapper highMarkCheckCBRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "HighmarkRenewalCallback") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug(stringPlaceholder, "Highmark Renewal callback Header value :: ", header);
		Mono<Object> response = loanService.highmarkCheckCallback(highMarkCheckCBRequestWrapper.getApiRequest(),
				header);
		logger.debug(stringPlaceholder, "End : Highmark Renewal callback response :: ", response);
		return adapterUtil.generateResponseWrapper(response,
				highMarkCheckCBRequestWrapper.getApiRequest().getInterfaceName(), header);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "BRE CB Check", notes = "API to check the BRE CB")
	@PostMapping(value = "/BRECBCheck", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> BRECBCheck(@RequestBody BRECBRequestWrapper breCBRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "BRECBCheck") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "breCB check Header value :: ", header);
			Mono<Object> response = null;
			try {
				Mono<Object> apiResponse = loanService.breCBCheck(
						breCBRequestWrapper.getApiRequest(), header, prop
				);

				Mono<Object> safeResponse = apiResponse
						.switchIfEmpty(Mono.just(
								adapterUtil.setError("Empty response from BRE api.", "1")
						))
						.onErrorResume(e -> {
							logger.error("Handled error at controller level", e);
							return Mono.just(
									adapterUtil.setError("Error occurred while executing the BRE api.", "1")
							);
						});

				return adapterUtil.generateResponseWrapper(
						safeResponse,
						breCBRequestWrapper.getApiRequest().getInterfaceName(),
						header
				);
			}catch (Exception e){
				logger.error("Exception in breCBCheck method for loans = ", e);
				Mono<Object> errorResponse = Mono.just(adapterUtil.setError("Error occurred while executing the BRE api.", "1"));
				return adapterUtil.generateResponseWrapper(errorResponse, breCBRequestWrapper.getApiRequest().getInterfaceName(),
						header);
			}
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}

	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "Signzy Pennyless Check", notes = "API to check the Signzy Pennyless Check")
	@PostMapping(value = "/SignzyPennylessCheck", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> SignzyPennylessCheck(
			@RequestBody SignzyPennylessRequestWrapper signzyPennylessRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "SignzyPennylessCheck") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "SignzyPennyless check Header value :: ", header);
			Mono<Object> response = loanService.SignzyPennylessCheck(signzyPennylessRequestWrapper.getApiRequest(),
					header, prop);
			logger.debug(stringPlaceholder, "End : SignzyPennyless check response :: ", response);
			return adapterUtil.generateResponseWrapper(response,
					signzyPennylessRequestWrapper.getApiRequest().getInterfaceName(), header);
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "BRE CB Report", notes = "API to get BRE CB report")
	@PostMapping(value = "/BRECBReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> BRECBReport(@RequestBody BRECBReportRequestWrapper breCBReportRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "BRECBReport") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "breCB Report Header value :: ", header);
			Mono<Object> response = loanService.breCBReport(breCBReportRequestWrapper.getApiRequest(), header, prop, true);

			logger.debug(stringPlaceholder, "End : breCB Report response :: ", response);
			return adapterUtil.generateResponseWrapper(response, breCBReportRequestWrapper.getApiRequest().getInterfaceName(),
					header);
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}

	}


	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "WIP dedupe check", notes = "API to check wip item dedupe")
	@PostMapping(value = "/wipDedupeCheck", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> wipDedupeCheck(
			@RequestBody WipDedupeCheckRequestWrapper wipDedupeCheckRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "wipDedupeCheck") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "WEB") String deviceId) {
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug(stringPlaceholder, "i-exceed wip dedupe Header value :: ", header);
		Mono<Object> response = loanService.wipDedupeCheck(wipDedupeCheckRequestWrapper.getApiRequest(), header);
		logger.debug(stringPlaceholder, "End : i-exceed wip dedupe check response :: ", response);
		return adapterUtil.generateResponseWrapper(response,
				wipDedupeCheckRequestWrapper.getApiRequest().getInterfaceName(), header);
	}

	// A
	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking Deposit API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Download Loan Application", notes = "API to Download Loan Application")
	@PostMapping(value = "/downloadLoanApplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> downloadLoanApplication(@RequestBody FetchAppRequestWrapper requestWrapper) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper responseWrapper = new ResponseWrapper();
		try {
			logger.warn("Inside downloadLoanApplication : " + requestWrapper.getApiRequest());
			response = loanService.downloadLoanApplication(requestWrapper.getApiRequest());
		} catch (Exception e) {
			logger.error("Exception in downloadLoanApplication method = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in downloadLoanApplication method");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : downloadLoanApplication method response is:: " + responseWrapper);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "income Assessment check", notes = "API to check Income Assessment")
	@PostMapping(value = "/incomeAssessmentCheck", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> incomeAssessmentCheck(
			@RequestBody IncomeCalulatorRequestWrapper incomeCalulatorRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "incomeAssessmentCheck") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "WEB") String deviceId) {
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug(stringPlaceholder, "check Income Assessment Header value :: ", header);
		ResponseWrapper incomeAssessmentCheckResponseWrapper = new ResponseWrapper();
		Response incomeAssessmentCheckResponse = new Response();
		incomeAssessmentCheckResponse = incomeAssessmentService
				.incomeAssessmentCheck(incomeCalulatorRequestWrapper.getApiRequest());
		logger.debug(stringPlaceholder, "End : check Income Assessment response :: ", incomeAssessmentCheckResponse);
		incomeAssessmentCheckResponseWrapper.setApiResponse(incomeAssessmentCheckResponse);
		logger.warn("End : fetchApplication method response is:: " + incomeAssessmentCheckResponseWrapper.toString());
		return new ResponseEntity<>(incomeAssessmentCheckResponseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "Merge Image to PDF and Download", notes = "API to Merge Image to PDF and Download")
	@PostMapping(value = "/mergeImageToPdfAndDownload", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> mergeImageToPdfAndDownload(
			@RequestBody MergeImageToPdfRequestWrapper mergeImageToPdfRequestWrapper) {
		logger.debug("Started mergeImageToPdf method");
		logger.debug("Recieved request for mergeImageToPdf: {}", mergeImageToPdfRequestWrapper);
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper responseWrapper = new ResponseWrapper();
		try {
			response = loanService.mergeImageToPdfAndDownload(mergeImageToPdfRequestWrapper);
		} catch (Exception e) {
			logger.error("Exception in mergeImageToPdf method = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in mergeImageToPdf method");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : downloadApplication method response is:: " + responseWrapper);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking loan API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "Upload loan", notes = "API to uploadLoan")
	@PostMapping(value = "/uploadLoan", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> uploadLoan(@RequestBody UploadLoanRequestWrapper requestWrapper) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper responseWrapper = new ResponseWrapper();
	
		try {
			logger.debug("Incoming request: " + requestWrapper);
			UploadLoanRequest apiRequest = requestWrapper.getApiRequest();
			response = loanService.uploadLoan(apiRequest);
		} catch (Exception e) {
			logger.error("Exception in uploadLoan method = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in uploadLoan method for loans");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : uploadLoan method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking loan API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "BCMPI Upload Data", notes = "API to upload data in BCMPI")
	@PostMapping(value = "/bcmpiUploadData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> bcmpiUploadData(@RequestBody UploadLoanRequestWrapper requestWrapper){
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper responseWrapper = new ResponseWrapper();

		try{
			logger.debug("Incoming request: " + requestWrapper);
			UploadLoanRequest apiRequest = requestWrapper.getApiRequest();
			response = bcmpiService.BcmpiUploadData(apiRequest);
		}catch (Exception e) {
			logger.error("Exception in uploadLoan method = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in bcmpiStageMovement method");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}

		responseWrapper.setApiResponse(response);
		logger.warn("End : bcmpiUploadData method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking loan API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "BCMPI Upload Data", notes = "API to upload data in BCMPI")
	@PostMapping(value = "/dbkitUploadData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> dbkitUploadData(@RequestBody UploadLoanRequestWrapper requestWrapper){
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper responseWrapper = new ResponseWrapper();

		try{
			logger.debug("Incoming request: " + requestWrapper);
			UploadLoanRequest apiRequest = requestWrapper.getApiRequest();
			response = dbkitService.DBKITUploadData(apiRequest);
		}catch (Exception e) {
			logger.error("Exception in dbkitUploadData method = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in dbkitUploadData method");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}

		responseWrapper.setApiResponse(response);
		logger.warn("End : bcmpiUploadData method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking loan API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "DBKIT Master Data", notes = "API to get DBKIT master data in BCMPI")
	@PostMapping(value = "/dbkitMasterData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> dbkitMasterData(@RequestBody DBKITMasterRequestWrapper requestWrapper) {
		logger.info("Received Request Body: {}", requestWrapper);
		Response response = new Response();
		Gson gson = new Gson();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper responseWrapper = new ResponseWrapper();
		List<DBKITMaster> dbkitMasterList = new ArrayList<>();
		String category = requestWrapper.getApiRequest().getRequestObj().getCategory();
		logger.debug("category: " + category);
		try {
			if ("ALL".equalsIgnoreCase(category)) {
				logger.debug("category is ALL");
				dbkitMasterList = dbkitMasterRepository.findAll();
			} else {
				logger.debug("category is {}", category);
				// Fetching data based on category
				dbkitMasterList = dbkitMasterRepository.findByCategory(category);
			}
			logger.debug("dbkitMasterList: " + dbkitMasterList);
			if (dbkitMasterList.isEmpty()) {
				logger.debug("no data found in dbkitMasterData");
				responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
				responseBody.setResponseObj("No data found in dbkitMasterData");
				response.setResponseHeader(responseHeader);
				response.setResponseBody(responseBody);
			} else {
				logger.debug("dbkitMasterData data found");
				String bcmpiMasterData = gson.toJson(dbkitMasterList);
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				responseBody.setResponseObj(bcmpiMasterData);
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
			}
		} catch (Exception e) {
			logger.error("Exception in dbkitMasterData method = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in dbkitMasterData method");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : dbkitMasterData method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking loan API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
	@ApiOperation(value = "BCMPI Master Data", notes = "API to get bcmpi master data in BCMPI")
	@PostMapping(value = "/bcmpiMasterData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> bcmpiMasterData() {
		// logger.info("Received Request Body: {}", request);
		Response response = new Response();
		Gson gson = new Gson();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper responseWrapper = new ResponseWrapper();
		List<BCMPIMaster> bcmpiMasterList = new ArrayList<>();
		try {
			// response = bcmpiService.bcmpiMasterData(request);
			bcmpiMasterList = bcmpiMasterRepository.findAll();
			if(bcmpiMasterList.isEmpty()){
				logger.debug("no data found in bcmpiMaster");
				responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
				responseBody.setResponseObj("No data found in bcmpiMaster");
				response.setResponseHeader(responseHeader);
				response.setResponseBody(responseBody);
				// return fetchUserDetailsResponse;
			}else{
				logger.debug("bcmpiMaster data found");
				String bcmpiMasterData = gson.toJson(bcmpiMasterList);
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				responseBody.setResponseObj(bcmpiMasterData);
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
				// return fetchUserDetailsResponse;
			}
		} catch (Exception e) {
			logger.error("Exception in bcmpiMasterData method = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in bcmpiMasterData method");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}
		responseWrapper.setApiResponse(response);
		logger.warn("End : uploadLoan method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	@ApiResponses({
		@ApiResponse(code = 200, message = "AppzillonBanking loan API reachable", response = ResponseWrapper.class),
		@ApiResponse(code = 408, message = "Service Timed Out"),
		@ApiResponse(code = 500, message = "Internal Server Error"),
		@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
@ApiOperation(value = "Insurance Premium Master Data", notes = "API to get Insurance Premium Master data")
@PostMapping(value = "/fetchInsurancePremiumMaster", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ResponseWrapper> fetchInsurancePremiumMaster() {
	logger.debug("OnEntry :: fetchInsurancePremiumMaster");
	Response response = new Response();
	Gson gson = new Gson();
	ResponseHeader responseHeader = new ResponseHeader();
	ResponseBody responseBody = new ResponseBody();
	ResponseWrapper responseWrapper = new ResponseWrapper();
	List<InsurancePremiumMaster> insurancePremiumList = new ArrayList<>();
	try {
		insurancePremiumList = insurancePremiumMasterRepository.findAll();
		if(insurancePremiumList.isEmpty()){
			logger.debug("No data found in InsurancePremiumMaster");
			responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
			responseBody.setResponseObj("No data found in InsurancePremiumMaster");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}else{
			logger.debug("InsurancePremiumMaster data found");
			String insurancePremiumData = gson.toJson(insurancePremiumList);
			responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
			responseBody.setResponseObj(insurancePremiumData);
			response.setResponseBody(responseBody);
			response.setResponseHeader(responseHeader);
		}
	} catch (Exception e) {
		logger.error("Exception in fetchInsurancePremiumMaster method = ", e);
		CommonUtils.generateHeaderForGenericError(responseHeader);
		responseBody.setResponseObj("Exception in fetchInsurancePremiumMaster method");
		response.setResponseHeader(responseHeader);
		response.setResponseBody(responseBody);
	}
	responseWrapper.setApiResponse(response);
	logger.warn("End : fetchInsurancePremiumMaster method response is:: " + responseWrapper.toString());
	return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
}
	
	
	
	@ApiResponses({
		@ApiResponse(code = 200, message = "AppzillonBanking loan API reachable", response = ResponseWrapper.class),
		@ApiResponse(code = 408, message = "Service Timed Out"),
		@ApiResponse(code = 500, message = "Internal Server Error"),
		@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable") })
@ApiOperation(value = "Business Image Processing", notes = "Business Image Processing API ")
@PostMapping(value = "/BIPImgProcess", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public Mono<ResponseEntity<ResponseWrapper>> bussinessImgProcessingApi(@RequestBody BIPRequestWrapper requestWrapper,
		@RequestHeader(defaultValue = "APZCBO") String appId,
		@RequestHeader(defaultValue = "BRECBCheck") String interfaceId,
		@RequestHeader(defaultValue = "000000000002") String userId,
		@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
		@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
	Properties prop = null;
	try {
		prop = CommonUtils.readPropertyFile();
	} catch (IOException e) {
		logger.error("Error while reading property file in populateRejectedData ", e);
	}

	Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
	if (null != prop) {
		logger.debug(stringPlaceholder, "bussinessImgProcessing check Header value :: ", header);
		Mono<Object> response = bipImageService.bussinessImgProcessingNew1(requestWrapper.getApiRequest(), header, prop);
		
		logger.debug(stringPlaceholder, "End : bussinessImgProcessing response :: ", response);
		return adapterUtil.generateResponseWrapper(response, requestWrapper.getApiRequest().getInterfaceName(),
				header);
	} else {
		Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
				ResponseCodes.FAILURE.getKey());
		ResponseWrapper responseWrapper = new ResponseWrapper();
		responseWrapper.setApiResponse(response);
		return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
	}
 }

	// =================CAG CODE===================

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "Adhar Redact OCR", notes = "ADHAR REDACT API (Base64)")
	@PostMapping(value = "/adharRedactOcr", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> AdharRedact(
			@RequestBody AdharRedactOcrRequestWrapper adharRedactOcrRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "BRECBCheck") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "Adhar Readact Header value :: ", header);
			Mono<Object> response = loanService.AdharRedact(adharRedactOcrRequestWrapper.getApiRequest(), header, prop);
			logger.debug(stringPlaceholder, "End : Adhar Redact response :: ", response);

			return adapterUtil.generateResponseWrapper(response,
					adharRedactOcrRequestWrapper.getApiRequest().getInterfaceName(), header);
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}
	}

	/**
	 * @author Ankit.G
	 */
	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "kyc passport", notes = "kyc passport API")
	@PostMapping(value = "/kycpassport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> KycPassport(
			@RequestBody KycPassportRequestWrapper kycPassportRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "BRECBCheck") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "kycpassport Header value :: ", header);
			Mono<Object> response = loanService.KycPassportService(kycPassportRequestWrapper.getApiRequest(), header,
					prop);
			logger.debug(stringPlaceholder, "End : kycpassport response :: ", response);

			return adapterUtil.generateResponseWrapper(response,
					kycPassportRequestWrapper.getApiRequest().getInterfaceName(), header);
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}
	}

	/**
	 * @author Ankit.G
	 */
	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "kyc DrivingLicense ", notes = "kyc DrivingLicense API")
	@PostMapping(value = "/kycDrivingLicense", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> kycDrivingLicense(
			@RequestBody kycDrivingLicenseRequestWrapper kycDrivingLicenseRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "BRECBCheck") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "kycDrivingLicense Header value :: ", header);
			Mono<Object> response = loanService
					.kycDrivingLicenseService(kycDrivingLicenseRequestWrapper.getApiRequest(), header, prop);
			logger.debug(stringPlaceholder, "End : kycDrivingLicense response :: ", response);

			return adapterUtil.generateResponseWrapper(response,
					kycDrivingLicenseRequestWrapper.getApiRequest().getInterfaceName(), header);
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}
	}

	/**
	 * @author Ankit.G
	 */
	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "DrivingLicense OCR", notes = "DrivingLicense OCR API (Base64)")
	@PostMapping(value = "/drivingLicenseOcr", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> DrivingLicenseOcr(
			@RequestBody DrivingLicenseOcrRequestWrapper drivingLicenseOcrRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "BRECBCheck") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "DrivingLicenseOcr Header value :: ", header);
			Mono<Object> response = loanService
					.DrivingLicenseOcrService(drivingLicenseOcrRequestWrapper.getApiRequest(), header, prop);
			logger.debug(stringPlaceholder, "End : DrivingLicenseOcr response :: ", response);

			return adapterUtil.generateResponseWrapper(response,
					drivingLicenseOcrRequestWrapper.getApiRequest().getInterfaceName(), header);
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}
	}

	/**
	 * @author Ankit.G
	 */
	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "PAN check OCR", notes = "kyc passport API (Base64)")
	@PostMapping(value = "/panCheckOcr", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> panCheck(@RequestBody PanCheckRequestWrapper panCheckRequestWrapper,
														  @RequestHeader(defaultValue = "APZCBO") String appId,
														  @RequestHeader(defaultValue = "BRECBCheck") String interfaceId,
														  @RequestHeader(defaultValue = "000000000002") String userId,
														  @RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
														  @RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "panCheck Header value :: ", header);
			Mono<Object> response = loanService.panCheckService(panCheckRequestWrapper.getApiRequest(), header, prop);
			logger.debug(stringPlaceholder, "End : panCheck response :: ", response);

			return adapterUtil.generateResponseWrapper(response,
					panCheckRequestWrapper.getApiRequest().getInterfaceName(), header);
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}
	}

	/**
	 * @author Ankit.G
	 */
	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "Signzy PennyCheck", notes = "API to check the Signzy Penny Check")
	@PostMapping(value = "/SignzyPennyCheck", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> SignzyPennycheck(
			@RequestBody SignzyPennylessRequestWrapper signzyPennyRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "BRECBCheck") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "SignzyPenny Check Header value :: ", header);
			Mono<Object> response = loanService.SignzyPennyCheckService(signzyPennyRequestWrapper.getApiRequest(),
					header, prop);
			logger.debug(stringPlaceholder, "End : SignzyPenny Check response :: ", response);

			return adapterUtil.generateResponseWrapper(response,
					signzyPennyRequestWrapper.getApiRequest().getInterfaceName(), header);
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}

	}

	/**
	 * @author Ankit.G
	 */
	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "Voter Front OCR", notes = "API to check the voter OCR")
	@PostMapping(value = "/voterFrontOcr", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> VoterFrontOcr(
			@RequestBody VoterFrontOcrRequestWrapper voterFrontOcrRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "BRECBCheck") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "voterFrontOcr Header value :: ", header);
			Mono<Object> response = loanService.VoterFrontOcrService(voterFrontOcrRequestWrapper.getApiRequest(),
					header, prop);
			logger.debug(stringPlaceholder, "End : voterFrontOcr response :: ", response);

			return adapterUtil.generateResponseWrapper(response,
					voterFrontOcrRequestWrapper.getApiRequest().getInterfaceName(), header);
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}
	}

	/**
	 * @author Ankit.G
	 */
	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable", response = ResponseWrapper.class),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding  not reachable") })
	@ApiOperation(value = "Voter Back OCR", notes = "API to check the voter Back OCR")
	@PostMapping(value = "/voterBackOcr", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> VoterBackOcr(
			@RequestBody VoterBackOcrRequestWrapper voterBackOcrRequestWrapper,
			@RequestHeader(defaultValue = "APZCBO") String appId,
			@RequestHeader(defaultValue = "BRECBCheck") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		Properties prop = null;
		try {
			prop = CommonUtils.readPropertyFile();
		} catch (IOException e) {
			logger.error("Error while reading property file in populateRejectedData ", e);
		}

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		if (null != prop) {
			logger.debug(stringPlaceholder, "VoterBackOcr Header value :: ", header);
			Mono<Object> response = loanService.VoterBackOcrService(voterBackOcrRequestWrapper.getApiRequest(), header,
					prop);
			logger.debug(stringPlaceholder, "End : VoterBackOcr response :: ", response);

			return adapterUtil.generateResponseWrapper(response,
					voterBackOcrRequestWrapper.getApiRequest().getInterfaceName(), header);
		} else {
			Response response = CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(),
					ResponseCodes.FAILURE.getKey());
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		}

	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable"),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")
	})
	@ApiOperation(value = "smsDocDownload", notes = "API to download Documents Via SMS")
	@GetMapping(value = "/smsDocDownload")
	public ResponseEntity<?> smsDocDownload(@RequestParam String applicationId,
											@RequestParam String requestType) {
		try {
			byte[] pdf = loanService.smsDocDownload(applicationId, requestType);
			if (pdf == null || pdf.length == 0) {
				throw new IllegalStateException("Generated PDF is empty");
			}
			String fileName = "Sanction_KFS_" + applicationId + ".pdf";
			if(requestType.equalsIgnoreCase("DISBURSEMENT")){
				fileName = "Loan_Agreement_" + applicationId + ".pdf";
			}
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDisposition(
					ContentDisposition.attachment()
							.filename(fileName)
							.build()
			);
			return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Failed to generate SMS document", ex);
			String htmlError =
					"<html>" +
							"<head><title>Document Download Failed</title></head>" +
							"<body style='font-family: Arial; text-align:center; padding-top:50px'>" +
							"<h2>Unable to generate document</h2>" +
							"<p>Please try again later or contact support.</p>" +
							"</body>" +
							"</html>";
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.TEXT_HTML)
					.body(htmlError);
		}
	}

@ApiResponses({
			@ApiResponse(code = 200, message = "AppzillonBanking API reachable"),
			@ApiResponse(code = 408, message = "Service Timed Out"),
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "AppzillonBanking Customer onboarding not reachable")
	})
	@ApiOperation(value = "SanctionMaster", notes = "API to fetch sanctionMaster data")
	@PostMapping(value = "/SanctionMaster", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> sanctionMaster(
			@RequestBody SanctionMasterRequestWrapper requestWrapper) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("SanctionMaster request: " + requestWrapper);
		try {
			String productCode = requestWrapper.getApiRequest().getRequestObj().getProductCode();
			List<SanctionMaster> sanctionMasterList = sanctionMasterRepository.findByProduct(productCode);
			if (sanctionMasterList.isEmpty()) {
				logger.debug("No data found in sanctionMaster");
				responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
				responseBody.setResponseObj("No data found in sanctionMaster");
				response.setResponseHeader(responseHeader);
				response.setResponseBody(responseBody);
			} else {
				logger.debug("sanctionMaster data found");
				Gson gson = new Gson();
				String sanctionMasterData = gson.toJson(sanctionMasterList);
				responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
				responseBody.setResponseObj(sanctionMasterData);
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
			}
		} catch (Exception e) {
			logger.error("Exception in sanctionMaster method = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("Exception in sanctionMaster method");
			response.setResponseHeader(responseHeader);
			response.setResponseBody(responseBody);
		}
		responseWrapper.setApiResponse(response);
		logger.debug("End : sanctionMaster method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
}
