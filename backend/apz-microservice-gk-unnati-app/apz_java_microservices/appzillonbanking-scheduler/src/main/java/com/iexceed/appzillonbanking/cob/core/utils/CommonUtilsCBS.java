package com.iexceed.appzillonbanking.cob.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;

@Component
public class CommonUtilsCBS {

	private static final Logger logger = LogManager.getLogger(CommonUtilsCBS.class);
	//private Random random = new Random();
	SecureRandom random = new SecureRandom();

	public int randomNumGenerate(int max) throws NoSuchAlgorithmException {
		random = SecureRandom.getInstanceStrong();
		return random.nextInt(max);
	}

	public int randomNumGenerate(int min, int max) {

		int ranValue = random.nextInt();
		return min + (ranValue * ((max - min) + 1));
	}
	
	public int randomNumGeneratesrgs(int min, int max) {

		int ranValue = random.nextInt();
		return min + (ranValue * ((max - min) + 1));
	}

	public String generateReqId(String customerId, int customerIdTrim) {
		if (customerId.length() >= customerIdTrim) {
			customerId = customerId.substring(customerId.length() - customerIdTrim);
		}
		return Long.toString(Instant.now().toEpochMilli()).substring(0, 12) + customerId;

	}

	public String generateReqId() {
		return Long.toString(Instant.now().toEpochMilli()).substring(0, 10);
	}

	public String generateReferenceNumber(int lengthOfCode) {
		String values = "0123456789";
		char[] refCode = new char[lengthOfCode];
		for (int i = 0; i < lengthOfCode; i++) {
			refCode[i] = values.charAt(random.nextInt(values.length()));
		}
		logger.debug("Random String generated for Auth Ref Code is -> " + Arrays.toString(refCode));
		return String.valueOf(refCode);
	}

	public Object generateRequest(String request, Class<?> wrapperClass) {
		ObjectMapper mapper = new ObjectMapper();
		Object requestObject = null;
		try {
			requestObject = mapper.readValue(request, wrapperClass);
		} catch (Exception e) {
			requestObject = null;
			logger.error("Error generating request " + e.getMessage());
		}
		return requestObject;
	}

	public String generateRequestString(Object requestObject) {
		ObjectMapper mapper = new ObjectMapper();
		String requestString = null;
		try {
			requestString = mapper.writeValueAsString(requestObject);
		} catch (Exception e) {
			requestString = null;
			logger.error("Cannot process persisted request payload " + e.getMessage());
		}
		return requestString;
	}

	public int generateRandomId(int minValue, int maxValue) {
		
		return random.nextInt(maxValue-minValue)+minValue;	
	}
	
	public String getTxnRefNum(String pUserId) 
	{
		logger.debug("Start : getTxnRefNum with pUserId = "+pUserId);
		String refNumber = null;
		
		if (pUserId == null || pUserId.isEmpty())
			refNumber = String.valueOf(new SecureRandom().nextInt(Integer.parseInt("999999999")));
		else
			refNumber = pUserId.substring(pUserId.length() - 4) + String.valueOf(new SecureRandom().nextInt(Integer.parseInt("99999")));
		
		logger.debug("End : getTxnRefNum with resp = "+refNumber);
		return refNumber;
	}
	
}
