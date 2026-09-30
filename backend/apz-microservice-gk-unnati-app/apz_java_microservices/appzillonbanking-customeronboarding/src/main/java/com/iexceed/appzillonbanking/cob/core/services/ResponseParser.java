package com.iexceed.appzillonbanking.cob.core.services;

import com.iexceed.appzillonbanking.cob.core.payload.CustomerIdentificationCards;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResponseParser {
	
	private ResponseParser() {}
	
	private static final Logger logger = LogManager.getLogger(ResponseParser.class);
	
	private static final String SERVICE_STATUS_CODE="serviceStatusCode";
	
	public static boolean isExtCallSuccess(Response extResponse, String src) {
		switch(src) {
		case "checkApplication":
		case "fundDepositAccount":
		case "getEligibleCards":
		case "fundAccount":
			logger.debug("inside isExtCallSuccess respnse obj is "+extResponse.getResponseBody().getResponseObj());
			if(extResponse.getResponseBody()!=null) {
				JSONObject resJson=new JSONObject(extResponse.getResponseBody().getResponseObj());
				if(resJson.get(SERVICE_STATUS_CODE)!=null && "0".equalsIgnoreCase((String) resJson.get(SERVICE_STATUS_CODE))) {
					return true;
				}
			}
			return false;
		default:
			return false;
		}
	}

	public static boolean isNewCustomer(Response extResponse) {
		logger.debug("Inside isNewCustomer ");
		if(extResponse.getResponseBody()!=null) {
			JSONObject resJson=new JSONObject(extResponse.getResponseBody().getResponseObj());
			JSONArray customerList=resJson.getJSONArray("applicationList");
			if(customerList.length()>0) {
				logger.debug("Inside isNewCustomer "+customerList);
				return false;
			}
			return true;
		}		
		return false;
	}
	
	public static String getFundAccRefNum(Response extResponse) {
		if(extResponse.getResponseBody()!=null) {
			JSONObject resJson=new JSONObject(extResponse.getResponseBody().getResponseObj());
			String fundAccRefNum=resJson.getString("txnRefNo");
			return fundAccRefNum;
		}
		return null;
	}
	
	public static JSONArray getApplicationList(Response extResponse) {
		if(extResponse.getResponseBody()!=null) {
			JSONObject resJson=new JSONObject(extResponse.getResponseBody().getResponseObj());
			return resJson.getJSONArray("applicationList");
		}	
		return null;
	}
	
	public static boolean isExistingCustomer(Response extResponse) {
		if(extResponse.getResponseBody()!=null) {
			JSONObject resJson=new JSONObject(extResponse.getResponseBody().getResponseObj());
			if(resJson.get(SERVICE_STATUS_CODE)!=null && "0".equalsIgnoreCase((String) resJson.get(SERVICE_STATUS_CODE))) {
				return true;
			}
		}
		return false;
	}
	
	public static String fetchCustomerName(Response extResponse) {
		JSONObject resJson=new JSONObject(extResponse.getResponseBody().getResponseObj());
		JSONObject custJson=resJson.getJSONObject("customerDetails");
		return (String)custJson.get("customerFName")+" "+(String)custJson.get("customerLName");
	}
	
	public static void getResponseData(Response extResponse, CustomerIdentificationCards customerIdentification) {
		List<String> eligibleCardsList=new ArrayList<>();
		if(extResponse.getResponseBody()!=null) {
			JSONObject resJson=new JSONObject(extResponse.getResponseBody().getResponseObj());
			JSONArray eligibleCards=resJson.getJSONArray("eligibleCards");
			for(Object jsonElement:eligibleCards) {
				JSONObject jsonObj=(JSONObject)jsonElement;
				eligibleCardsList.add(jsonObj.getString("productCode"));
			}
			customerIdentification.setEligibleCardsList(eligibleCardsList);
			customerIdentification.setCreditLimit(resJson.getString("creditLimit"));
			customerIdentification.setWithdrawalLimit(resJson.getString("withdrawalLimit"));
			customerIdentification.setCurrency(resJson.getString("currency"));
		}
	}
	
	
}