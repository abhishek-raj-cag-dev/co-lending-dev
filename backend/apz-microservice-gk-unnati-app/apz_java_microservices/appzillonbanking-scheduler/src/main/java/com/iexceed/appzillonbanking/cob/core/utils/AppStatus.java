package com.iexceed.appzillonbanking.cob.core.utils;

import lombok.Getter;

public enum AppStatus {

    UNATTENEDED("UNATTENDED","Unnatended Lead", "UNATTENDED"),
    INPROGRESS("INPROGRESS", "KM Sourcing – In Progress", "PENDING_KM_SOURCING"),
    PENDING("PENDING", "Pending with BM","PENDING_BM_RECOMMENDATION"),
    APPROVED("APPROVED", "Pending with RPC Maker", "PENDING_WITH_RPC_MAKER"),
    DELETED("DELETED", "Application Deleted","DELETED"),
    REJECTED("REJECTED", "Rejected", "REJECTED"),
    PUSHBACK("PUSHBACK", "Pushed back to Sourcing From NewGen", "PENDING_KM_SOURCING"),
    IPUSHBACK("IPUSHBACK", "Pushed back to Sourcing from Iexceed", "PENDING_KM_SOURCING"),
    ACTIVE_STATUS("A", "Active", "ACTIVE"),
    INACTIVESTATUS("I", "Inactive", "INACTIVE"),
    PENDINGFORRPCVERIFICATION("PENDINGFORRPCVERIFICATION", "Pending with RPC checker", "PENDING_WITH_RPC_CHECKER"),
    RPCVERIFIED("RPCVERIFIED", "Credit assessment", "PENDING_CREDIT_ASSESSMENT"),
    RPCPUSHBACK("RPCPUSHBACK", "RPC checker to maker", "PENDING_WITH_RPC_MAKER"),
    CACOMPLETED("CACOMPLETED", "Pending in sanction", "PENDING_SANCTION"),
    CAPUSHBACK("CAPUSHBACK", "Pushed back from CA to Sourcing", "PENDING_KM_SOURCING"),
    PENDINGREASSESSMENT("PENDINGREASSESSMENT", "Pending Reassessment","PENDING_REASSESSMENT"),
    PENDINGDEVIATION("PENDINGDEVIATION", "Pending deviation","PENDING_DEVIATION"),
    PENDINGPRESANCTION("PENDINGPRESANCTION", "Pending Pre-Sanction", "PENDING_PRESANCTION"),
    SANCTIONED("SANCTIONED", "DB Kit generation", "CLOSED"),
    RESANCTION("RESANCTION", "Pending Re-sanction", "CLOSED"),
    DBKITGENERATED("DBKITGENERATED", "Pending db kit verification", "CLOSED"),
    RPCBANKUPDATE("RPCBANKUPDATE", "RPC Bank Update", "CLOSED"),
    DBKITVERIFIED("DBKITVERIFIED", "DB Kit Verified", "CLOSED"),
    DISBURSED("DISBURSED", "Loan Disbursed", "CLOSED"),
    DBPUSHBACK("DBPUSHBACK", "Pushed back to DB kit generation", "CLOSED"),
    PENDINGSERVICECALL("PENDINGSERVICECALL", "Pending Service Call", "CLOSED"),
    LUC("LUC","PENDING LUC", "CLOSED"),
    PENDINGLUCVERIFICATION("PENDINGLUCVERIFICATION", "PENDING LUC VERIFICATION", "CLOSED"),
    LUCVERIFIED("LUCVERIFIED", "LUC VERIFIED", "CLOSED");
	
	@Getter
	private final String value;
    @Getter
    private final String stageDescription;
    @Getter
    private final String cdhStatus;
	
	AppStatus(String value, String stageDescription, String cdhStatus) {
		this.value = value;
        this.stageDescription = stageDescription;
        this.cdhStatus = cdhStatus;
	}
	
    public static String getStageDescriptionByValue(String value) {
        for (AppStatus status : AppStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status.getStageDescription();
	}
        }
        return null;
    }

    public static String getCdhStatusByValue(String value){
        for (AppStatus status : AppStatus.values()) {
            if(status.getValue().equalsIgnoreCase(value)){
                return status.getCdhStatus();
            }
        }
        return null;
	}
}


