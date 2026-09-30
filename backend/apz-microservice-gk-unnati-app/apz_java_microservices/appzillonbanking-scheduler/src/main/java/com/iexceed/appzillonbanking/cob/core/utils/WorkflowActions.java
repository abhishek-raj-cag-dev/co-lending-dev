package com.iexceed.appzillonbanking.cob.core.utils;

public enum WorkflowActions {
	
	INITIATED_BY("Initiated By"),
	ASSIGNED_TO("Assigned to"),
	REVIEWED_BY("Reviewed By"),
	VERIFIED_BY("Submitted By"),
	APPROVED_BY("Submitted By"),
	REJECTED_BY("Rejected By"),
	SUBMITTED_FOR_VERIFICATION_BY("Submitted"),
	PUSHBACK_BY("Sent Back By"),
	RPC_PUSHBACK_BY("RPC Pushback By"),
	CA_BY("Credit Assessed By"),
	DEVIATION_APPROVED_BY(Constants.APPROVED_BY),
	REASSESSMENT_APPROVED_BY(Constants.APPROVED_BY),
	PRESANCTION_APPROVED_BY(Constants.APPROVED_BY),
	SANCTIONED_BY("Sanctioned By"),
	RESANCTIONED_BY("Resanctioned By"),
	DISBURSED_BY("Disbursed By"),
	BANKUPDATESENDBACK("Bank Update Sendback By"),
	DBKITVERIFIEDBY("DB Kit Verified By");     
	
	private final String value;
	
	WorkflowActions(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
