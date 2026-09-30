package com.iexceed.appzillonbanking.cob.logs.service;

import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.logs.payload.LogData;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class LogExternalReqRes {

	@Autowired
	private LoggingServiceProxy proxy;

	public void logTransactionToDb(Header header, String request, String response, LocalDateTime startDateTime, String status, String requestType, JSONObject interfaceJsonContent) {
		LogData logData = new LogData();
		Date currDate = new Date();
		logData.setAppId(header.getAppId());
		logData.setDeviceId(header.getDeviceId());
		logData.setInterfaceId(header.getInterfaceId());
		logData.setMasterTxnRefNo(header.getMasterTxnRefNo());
		logData.setStTm(startDateTime);
		logData.setEndTm(LocalDateTime.now());
		logData.setStatus(status);
		logData.setEndpointType(requestType);
		logData.setRequest(request);
		logData.setResponse(response);
		logData.setTxnRefNo(header.getUserId()+currDate.getTime());
		logData.setUserId(header.getUserId());
		proxy.logTransactionDetails(logData, interfaceJsonContent);
	}
}
