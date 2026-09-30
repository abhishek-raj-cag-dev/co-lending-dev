package com.iexceed.appzillonbanking.cob.logs.service;

import com.iexceed.appzillonbanking.cob.logs.payload.LogData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoggingServiceProxy {

	private static final Logger logger = LogManager.getLogger(LoggingServiceProxy.class);

	@Autowired
	private LoggingService loggingService;

	public void logTransactionDetails(LogData logData, JSONObject interfaceJsonContent) {
		try {
			loggingService.logTransactionDetails(logData, interfaceJsonContent);
		} catch (Exception e) {
			logger.error("logTransactionDetails ERROR = " , e);
		}
	}
}
