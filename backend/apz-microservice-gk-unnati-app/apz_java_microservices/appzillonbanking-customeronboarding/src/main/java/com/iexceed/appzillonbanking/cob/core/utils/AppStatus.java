package com.iexceed.appzillonbanking.cob.core.utils;

import lombok.Getter;

public enum AppStatus {

    UNATTENEDED("UNATTENDED","Unnatended Lead", "UNATTENDED"),
    INPROGRESS("INPROGRESS", "KM Sourcing – In Progress", "PENDING_KM_SOURCING"),
    PENDING("PENDING", "Pending with BM","PENDING_BM_RECOMMENDATION"),
    BMPUSHBACK("BMPUSHBACK", "Pushed back to KM from BM Recommendation","PENDING_KM_SOURCING"),
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
    SANCTIONED("SANCTIONED", "DB Kit generation", "DB_KIT_GENERATION"),
    RESANCTION("RESANCTION", "Pending Re-sanction", "PENDING_RE_SANCTION"),
    DBKITGENERATED("DBKITGENERATED", "Pending db kit verification", "PENDING_DB_KIT_VERIFICATION"),
    RPCBANKUPDATE("RPCBANKUPDATE", "RPC Bank Update", "RPC_BANK_UPDATE"),
    DBKITVERIFIED("DBKITVERIFIED", "DB Kit Verified", "DB_KIT_VERIFIED"),
    DISBURSED("DISBURSED", "Loan Disbursed", "LOAN_DISBURSED"),
    DBPUSHBACK("DBPUSHBACK", "Pushed back to DB kit generation", "PUSHBACK_DBKIT_GENERATION"),
    PENDINGSERVICECALL("PENDINGSERVICECALL", "Pending Service Call", "PENDING_SERVICE_CALL"),
    LUC("LUC","PENDING LUC", "PENDING_LUC"),
    PENDINGLUCVERIFICATION("PENDINGLUCVERIFICATION", "PENDING LUC VERIFICATION", "PENDING_LUC_VERIFICATION"),
    LUCVERIFIED("LUCVERIFIED", "LUC VERIFIED", "LUC_VERIFIED"),
    EXIT("EXIT", "EXIT", "EXIT");
	
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


