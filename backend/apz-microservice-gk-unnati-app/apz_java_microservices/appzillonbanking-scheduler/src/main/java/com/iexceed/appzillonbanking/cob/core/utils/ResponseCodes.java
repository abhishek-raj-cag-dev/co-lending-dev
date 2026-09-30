package com.iexceed.appzillonbanking.cob.core.utils;

public enum ResponseCodes {

	
	
	INVALID_APP_MASTER("IV100", "Invalid Application Master"),
	INVALID_NOMINEE("IV101", "Invalid Nominee"),
	INVALID_PAN("IV103", "Invalid PAN"),
	INVALID_DISCARD("IV104", "Invalid Discard"),
	PATH_NOT_CONFIGURED("IV105", "Path Not Configured"),
	OCR_RES_NOT_VALID("IV106", "OCR Response Not Valid"),
	BASE64_DATA_NOT_FOUND("IV107", "Base64 Not Found"),
	APP_PRESENT_INPROGRESS_STATUS("IV108", "Application is present in INPROGRESS status"),
	APP_PRESENT_APPROVED_STATUS("IV109", "Application is present in APPROVED status"),
	INVALID_STATUS("IV110", "Invalid Status"),
	INVALID_ROLE("IV111", "Invalid Role"),
	VAPT_ISSUE_PERMISSION(Constants.IV112, "No Permission"),
	VAPT_ISSUE_FIELDS(Constants.IV112, "VAPT failed for screen elements"),
	VAPT_ISSUE_STAGE(Constants.IV112, "VAPT failed for stage validation"),
	VAPT_ISSUE_FILE_FORMAT(Constants.IV112, "VAPT Issue for file format"),
	VAPT_ISSUE_STATUS(Constants.IV112, "Status is not setup in TB_ABOB_ROLE_ACCESS_MAP or VAPT issue"),
	INVALIDUPI("IV113", "Invalid UPI ID"),
	RELATED_APPLN_FAIL("IV114", "Update Related application failed"),
	APP_PRESENT_INPROGRESS_LAST_STAGE("IV115", "Application is present in INPROGRESS status but all stages completed"),
	CASA_CREATION_FAIL("IV116", "CASA Creation failed"),
	REJECT_RES_NOT_VALID("IV117", "Reject Response Not Valid"),
	PENDING_DEVIATION("IV118", "Pending Deviation"),
	PENDING_REASSESSMENT("IV119", "Pending Reassessment"),
	FAILURE("1", "Failure"),
	SUCCESS("0", "Success");
	
	private final String key;
	private final String value;
	
	
	ResponseCodes(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
