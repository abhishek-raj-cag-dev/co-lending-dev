package com.iexceed.appzillonbanking.cob.core.utils;

public enum WidgetQueueStatus {

	PARTIAL_BY_CUSTOMER("Partial by customer"),
	PARTIAL_BY_SELF("Partial by Self"),
	PARTIAL_BY_OTHERS("Partial by Others"),
	COMPLETED("Completed"),
	REJECTED("Rejected"),
	DELETED("Deleted"),
	PENDING_IN_QUEUE("Pending in Queue"),
	ASSIGNED("Assigned"),
	PENDING_FOR_VERIFICATION("Pending for verification"),
	PENDING_FOR_APPROVAL("Pending for approval"),
	PUSHBACK("Push Back"),
	PENDING_RPC_MAKER("Pending in RPC Maker"),
	PENDING_FOR_RPC_VERIFICATION("Pending for RPC verification"),
	PENDING_FOR_RPC_APPROVAL("Pending for RPC approval"),
	RPCVERIFIED("RPC Verified"),
	RPCPUSHBACK("RPC Push Back");
	
	private final String value;
	
	WidgetQueueStatus(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}


