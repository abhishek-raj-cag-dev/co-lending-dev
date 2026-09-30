package com.iexceed.appzillonbanking.cob.service;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.payload.SmsAndEmailDtls;
import com.iexceed.appzillonbanking.cob.payload.SmsRequestFields;

public class SendSmsService {

	private static final Logger logger = LogManager.getLogger(SendSmsService.class);

	private SendSmsService() {

	}

	public static JSONObject sendSms(SmsAndEmailDtls smsAndEmailDtls, Properties prop,String language) {
		logger.debug(" Send SMS service :: Service Started ");
		JSONObject lResponse = new JSONObject();

		SmsRequestFields smsRequestFields = new SmsRequestFields();
		smsRequestFields.setTransId(CommonUtils.generateRandomNumStr());
		smsRequestFields.setMobileNo(smsAndEmailDtls.getMobileNo());
		smsRequestFields.setMsg(smsAndEmailDtls.getSmsBody());
        String senderId="";
//        if(null!=language && (language.equalsIgnoreCase(Constants.ODIYA) || language.equalsIgnoreCase(Constants.ODIA))){
//            senderId=prop.getProperty(Constants.SANCTION_SENDERID_ODIYA);
//        }else{
//           senderId = prop.getProperty(Constants.SANCTION_SENDERID);
//        }
		senderId = prop.getProperty(CobFlagsProperties.SENDERID_UNIQUE.getKey());
		smsRequestFields.setSenderId(senderId);
		smsRequestFields.setCustomerId(smsAndEmailDtls.getCustId());
		smsRequestFields.setCustomerName(smsAndEmailDtls.getCustName());
		smsRequestFields.setActionTypes(smsAndEmailDtls.getActionType());

		ObjectMapper mapperObj = new ObjectMapper();

		mapperObj.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		String endPointUrl = prop.getProperty(Constants.SANCTION_SMS_URL);
        logger.warn(" final sms end point URL: {}", endPointUrl);
		logger.debug(" Send SMS service :: Service Started ");

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("key", prop.getProperty(Constants.SMS_KEY));
		headers.add("Authorization", prop.getProperty(Constants.SANCTION_SMS_URL));
		headers.add("Apikey", prop.getProperty(Constants.SANCTION_SMS_APIKEY));
		//headers.add("Cache-Control", "no-cache");

		try {
			SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
			factory.setConnectTimeout(
					Integer.parseInt(prop.getProperty(CobFlagsProperties.SEND_SMS_CONNECT_TIMEOUT.getKey())));
			factory.setReadTimeout(
					Integer.parseInt(prop.getProperty(CobFlagsProperties.SEND_SMS_READ_TIMEOUT.getKey())));
			RestTemplate restTmplt = new RestTemplate(factory);
			// Normalize message (critical for Malayalam/Odia)
			if(null!=language && (language.equalsIgnoreCase(Constants.ODIYA)
					|| language.equalsIgnoreCase(Constants.ODIA)
					|| language.equalsIgnoreCase(Constants.MALAYALAM))) {
				logger.debug("Original message: {}", smsRequestFields.getMsg());
			smsRequestFields.setMsg(
					java.text.Normalizer.normalize(
							smsRequestFields.getMsg(),
							java.text.Normalizer.Form.NFC
					)
			);
				logger.debug("Normalized message: {}", smsRequestFields.getMsg());
			}
			String requestBody = mapperObj.writeValueAsString(smsRequestFields);
			logger.debug("Request Json: {}", requestBody);
			HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
			ResponseEntity<String> externalRes = restTmplt.exchange(
					endPointUrl,
					HttpMethod.POST,
					requestEntity,
					String.class
			);
			lResponse.put("response", externalRes.getBody());
		} catch (HttpStatusCodeException | JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			JSONObject smsResp = new JSONObject();
			smsResp.put("status", "failure");
			smsResp.put("errMsg", e.getMessage());
			lResponse.put("response", smsResp);
		}
		logger.debug(" Send SMS service :: Service end ");
		logger.debug("Response from send SMS service :: " + lResponse);

		return lResponse;
	}

}
