package com.iexceed.appzillonbanking.cob.service;

import java.util.Optional;
import java.util.Properties;

import com.iexceed.appzillonbanking.cob.core.utils.SmsStage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.iexceed.appzillonbanking.cob.constants.CommonConstants;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.domain.ab.SmsTemplate;
import com.iexceed.appzillonbanking.cob.domain.ab.SmsTemplatePK;
import com.iexceed.appzillonbanking.cob.payload.SendSmsAndEmailApiRequest;
import com.iexceed.appzillonbanking.cob.payload.SendSmsAndEmailRequestObject;
import com.iexceed.appzillonbanking.cob.payload.SmsAndEmailDtls;
import com.iexceed.appzillonbanking.cob.repository.ab.SmsTemplateRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class SendSmsAndEmailService {

	private static final Logger logger = LogManager.getLogger(SendSmsAndEmailService.class);

	@Autowired
	private SmsTemplateRepository smsTemplateRepository;

	@CircuitBreaker(name = "fallback", fallbackMethod = "sendSmsAndEmailServiceFallback")
	public Response sendSmsAndEmailService(SendSmsAndEmailApiRequest sendSmsandEmailApiRequest, Properties prop, SmsStage smsStage) {
		logger.warn("Start: send SMS and EMAIL method");
		Thread smsEmailThread = new Thread(() -> {
			logger.debug(" Going to initiate email and sms ");
			JSONObject lResponse = sendSmsAndEmail(sendSmsandEmailApiRequest, prop, smsStage);
            logger.debug(" Send SMS EMAIL response :: {}", lResponse);
		});
		smsEmailThread.start();

		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		responseBody.setResponseObj("");
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseCode(CommonConstants.SUCCESS);
		responseHeader.setResponseMessage("Send sms and email initiated");
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		logger.warn("End: send SMS and EMAIL method Response :" + response.toString());
		return response;

	}

	private JSONObject sendSmsAndEmail(SendSmsAndEmailApiRequest sendSmsandEmailApiRequest, Properties prop, SmsStage smsStage) {
		logger.debug(" Send SMS And EMAIL service :: Service Started ");
		JSONObject lResponse = null;
		try {
			//TODO:Get language based on branch Id
			String actionType = sendSmsandEmailApiRequest.getRequestObject().getActionType();
			SmsTemplatePK smsTemplatePK = new SmsTemplatePK();
			String templateActionType = smsStage.getTemplateActionType();
			smsTemplatePK.setActionType(templateActionType);
			smsTemplatePK.setLanguage(sendSmsandEmailApiRequest.getRequestObject().getLanguage());
			smsTemplatePK.setAppId(sendSmsandEmailApiRequest.getAppId());

			logger.debug(" Action type: {}, Language: {}, App Id: {}", smsTemplatePK.getActionType(), sendSmsandEmailApiRequest.getRequestObject().getLanguage(), smsTemplatePK.getAppId());
			// since email is not applicable so setting default details
			sendSmsandEmailApiRequest.getRequestObject().setAttachmentReq(false);
			sendSmsandEmailApiRequest.getRequestObject().setAttachmentType("");
			
			Optional<SmsTemplate> smsTemplate = smsTemplateRepository.findById(smsTemplatePK);
			Iterable<SmsTemplate> templates = smsTemplateRepository.findAll();
			templates.forEach(template -> logger.info("Template: {}", template));
			if (smsTemplate.isPresent()) {
				logger.debug("SMS and EMAIL template present ");
				SmsAndEmailDtls smsAndEmailDtls = new SmsAndEmailDtls();
				smsAndEmailDtls.setActionType(actionType);
				smsAndEmailDtls = checkCommunicationChannel(smsAndEmailDtls, smsTemplate.get().getChannel());
				logger.debug(
						"SMS and EMAIL details after communication channel update ::" + smsAndEmailDtls.toString());
				lResponse = new JSONObject();
				try {
					if (smsAndEmailDtls.isSmsReq()) {
						logger.debug("SMS required true");
						smsAndEmailDtls.setCustId(sendSmsandEmailApiRequest.getUserId());
						smsAndEmailDtls.setCustName(sendSmsandEmailApiRequest.getUserName());
						smsAndEmailDtls.setMobileNo(
								getMobileNumbertoSendSms(actionType, sendSmsandEmailApiRequest.getRequestObject()));
						switch (smsStage) {
							case SANCTION:
							smsAndEmailDtls.setSmsBody(replaceSanctionTempValues(smsTemplate.get().getSms(),
									sendSmsandEmailApiRequest.getRequestObject()));
								break;
							case DISBURSED:
							smsAndEmailDtls.setSmsBody(replaceDisbursementTempValues(smsTemplate.get().getSms(),
									sendSmsandEmailApiRequest.getRequestObject()));
								break;
							case SOURCING_SUBMISSION:
								smsAndEmailDtls.setSmsBody(replaceSourcingSubmissionTempValues(smsTemplate.get().getSms(),
										sendSmsandEmailApiRequest.getRequestObject()));
								break;
							case REJECTION:
								smsAndEmailDtls.setSmsBody(replaceRejectionTempValues(smsTemplate.get().getSms(),
										sendSmsandEmailApiRequest.getRequestObject()));
								break;
							default:
						smsAndEmailDtls.setSmsBody(replaceTemplateValues(smsTemplate.get().getSms(),
									sendSmsandEmailApiRequest.getRequestObject(),
									smsTemplate.get().getSmsDynamicParam()));
								break;
						}
						logger.debug(
								"SMS and EMAIL details after template changes for SMS::" + smsAndEmailDtls.toString());
						JSONObject smsResp = SendSmsService.sendSms(smsAndEmailDtls, prop,sendSmsandEmailApiRequest.getRequestObject().getLanguage());
						lResponse.put("smsResp", smsResp);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					JSONObject smsResp = new JSONObject();
					smsResp.put("status", "failure");
					smsResp.put("errMsg", e.getMessage());
					lResponse.put("smsResp", smsResp);
				}
				try {
					if (smsAndEmailDtls.isEmailReq()) {
						logger.debug("EMAIL required true");
						smsAndEmailDtls.setEmailId(
								getEmailIdtoSendEmail(actionType, sendSmsandEmailApiRequest.getRequestObject()));
						smsAndEmailDtls.setEmailTitle(smsTemplate.get().getEmailTitle());
						smsAndEmailDtls.setEmailBody(replaceTemplateValues(smsTemplate.get().getEmailBody(),
								sendSmsandEmailApiRequest.getRequestObject(),
								smsTemplate.get().getEmailDynamicParam()));

						if (sendSmsandEmailApiRequest.getRequestObject().isAttachmentReq()) {
							smsAndEmailDtls.setAttachmentContent(
									sendSmsandEmailApiRequest.getRequestObject().getAttachmentContent());
						}
						logger.debug("SMS and EMAIL details after template changes for EMAIL::"
								+ smsAndEmailDtls.toString());
						JSONObject emailResp = SendEmailService.sendMail(smsAndEmailDtls, prop);
						lResponse.put("emailResp", emailResp);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					JSONObject emailResp = new JSONObject();
					emailResp.put("status", "failure");
					emailResp.put("errMsg", e.getMessage());
					lResponse.put("emailResp", emailResp);
				}

			} else {
				logger.debug("SMS and EMAIL template not present ");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			lResponse = new JSONObject();
			lResponse.put("status", "failure");
			lResponse.put("errMsg", e.getMessage());
		}
		logger.debug("Response from Send OTP And EMAIL service :: " + lResponse);
		logger.debug(" Send SMS And EMAIL service :: Service end ");
		return lResponse;
	}

	private String getMobileNumbertoSendSms(String actionType, SendSmsAndEmailRequestObject requestObj) {
		return requestObj.getMobileNo();
	}

	private String getEmailIdtoSendEmail(String actionType, SendSmsAndEmailRequestObject requestObj) {
		return requestObj.getEmailId();
	}

	private SmsAndEmailDtls checkCommunicationChannel(SmsAndEmailDtls smsAndEmailDtls, String commChannel) {
		logger.debug("communication channel ::" + commChannel);
		if (commChannel.equals("BOTH")) {
			smsAndEmailDtls.setSmsReq(true);
			smsAndEmailDtls.setEmailReq(true);
		} else if (commChannel.equals("SMS")) {
			smsAndEmailDtls.setSmsReq(true);
			smsAndEmailDtls.setEmailReq(false);
		} else if (commChannel.equals("EMAIL")) {
			smsAndEmailDtls.setSmsReq(false);
			smsAndEmailDtls.setEmailReq(true);
		} else {
			smsAndEmailDtls.setSmsReq(false);
			smsAndEmailDtls.setEmailReq(false);
		}
		return smsAndEmailDtls;
	}

	private String replaceTemplateValues(String template, SendSmsAndEmailRequestObject requestObj, String dynamicParams)
			throws JsonProcessingException, JSONException {
		if (!dynamicParams.equals("")) {
			ObjectMapper mapperObj = new ObjectMapper();
			mapperObj.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			JSONObject reqJson = new JSONObject(mapperObj.writeValueAsString(requestObj));
			for (String dynamicParam : dynamicParams.split(",")) {
				template = template.replace(dynamicParam.split("~")[1].trim(),
						reqJson.getString(dynamicParam.split("~")[0].trim()));
			}
		}
		return template;
	}
	private String replaceSanctionTempValues(String template, SendSmsAndEmailRequestObject requestObj) {
		String[] reqFields = requestObj.getAttachmentContent().split("\\|~\\|");

		template = template.replace("#applicantName", requestObj.getCustName())
						   .replace("#loanId", reqFields[0])
						   .replace("#sanctionedAmount", reqFields[1])
						   .replace("#sanctionedDate", reqFields[2])
				.replace("#URL#", reqFields[3]);
		
		return template;
	}
	
	private String replaceDisbursementTempValues(String template, SendSmsAndEmailRequestObject requestObj) {
		String[] reqFields = requestObj.getAttachmentContent().split("\\|~\\|");

		template = template.replace("#applicantName", requestObj.getCustName())
						   .replace("#loanId", reqFields[0])
						   .replace("#sanctionedAmount", reqFields[1])
						   .replace("#sanctionedDate", reqFields[2])
						   .replace("#disbursementDate", reqFields[3])
				.replace("#URL#", reqFields[4]);
		
		return template;
	}

	private String replaceSourcingSubmissionTempValues(String template, SendSmsAndEmailRequestObject requestObj){
		String[] reqFields = requestObj.getAttachmentContent().split("\\|~\\|");
		template = template.replace("#CustomerName", reqFields[0])
				.replace("#LoanAmt", reqFields[1])
				.replace("#SubmissionDate", reqFields[2])
				.replace("#ApplicationId",reqFields[3]);
		return template;
	}

	private String replaceRejectionTempValues(String template, SendSmsAndEmailRequestObject requestObj){
		String[] reqFields = requestObj.getAttachmentContent().split("\\|~\\|");
		template = template.replace("#CustomerName", reqFields[0])
				.replace("#LoanAmt", reqFields[1]);
		return template;
	}

	// ALL FALLBACK METHODS
	private Response sendSmsAndEmailServiceFallback(SendSmsAndEmailApiRequest sendSmsandEmailApiRequest, Properties prop, SmsStage smsStage, Exception e) {
	    logger.error("sendSmsAndEmailServiceFallback triggered due to exception: ", e);
		return CommonUtils.formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
	}



}
