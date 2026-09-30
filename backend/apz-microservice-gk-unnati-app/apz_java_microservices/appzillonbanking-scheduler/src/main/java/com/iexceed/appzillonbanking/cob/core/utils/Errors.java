package com.iexceed.appzillonbanking.cob.core.utils;

public enum Errors {
	SEVICETIMEDOUTERROR("450", "Timed out trying to reach external service"),
	SEVICENOTREACHABLEERROR("404", "Unable to reach the service"), 
	SERVICEERROR("460", "Service Response Invalid"),
	NORECORD("NR100", "No Record/Data Found"),
	PROCESSINGREQUESTERROR("600", "Error Processing Request"), 
	DEMOSERVICEERROR("9","Failed to execute the demo service"),
	EXTERNALSERVICEERROR("8","Failed to execute the external service"),
	AUTHTOKENFAILURE("TOKEN_FAIL","Failure response from Token Generation service, Please try after sometime."),

	PROCESSING_REQ_ERROR("ABS_COM_600","");
	
	private final String errorCode;
	private final String errorMessage;

	Errors(String id, String msg) {
		this.errorCode = id;
		this.errorMessage = msg;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
}