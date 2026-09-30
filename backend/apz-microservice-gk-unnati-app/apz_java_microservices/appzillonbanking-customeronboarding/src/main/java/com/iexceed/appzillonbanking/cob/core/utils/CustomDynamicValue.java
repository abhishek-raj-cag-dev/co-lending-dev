package com.iexceed.appzillonbanking.cob.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class CustomDynamicValue {

	private static final Logger logger = LogManager.getLogger(CustomDynamicValue.class);

	public String generateValue(String defaultValue) {
		logger.debug("Start : generateValue with defaultValue = " + defaultValue);
		String randomValue = null;
		//Write your project specific logic here
		return randomValue;
	}

}