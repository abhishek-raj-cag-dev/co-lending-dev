package com.iexceed.appzillonbanking.cob.core.enums;

public enum Errors {

	PERSISTENCEERROR("510", "Persistence Error"),
	PROCESSINGREQUESTERROR("600", "Error Processing Request"),
	NORECORD("NR100", "No Record/Data Found"),
	DATAINSERTIONSUCCESS("ABS_COM_001", "Data insertion successful"),
	DATAUPDATIONSUCCESS("ABS_COM_003", "Data updation successful"),
	DATAUPDATIONFAILURE("ABS_COM_004", "Unable to update the record(s)"),
	EXTERNALSERVICEERROR("8","Failed to execute the external service"),
	DEMOSERVICEERROR("9","Failed to execute the demo service"),
	DATADELETIONSUCCESS("ABS_COM_005", "Data deletion successful"),
	CALCULATIONERROR("260", "Calculation Error"),
	AUTHTOKENFAILURE("TOKEN_FAIL","Failure response from Token Generation service, Please try after sometime."),
	MAKERCHECKERERROR("SAME_MAKER_CHECKER","Same Maker and Checker Authorization not allowed");

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
