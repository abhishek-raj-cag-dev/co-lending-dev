package com.iexceed.appzillonbanking.cob.core.utils;

public enum JsonKeyFolders {
    DEMAND_PROMISSORY_NOTE("DEMANDPROMISSORYNOTE"),
    CONSENT_LETTER("CONSENTLETTER"),
    MSME("MSME"),
    INSURANCE_CONSENT("INSURANCECONSENT"),
    SCHEDULE_A("SCHEDULEA"),
    LOAN_AGREEMENT("LOANAGREEMENT"),
    LOAN_APPLICATION("LOANAPPLICATION"),
    SANCTION_LETTER("SANCTIONLETTER"),
    KFS("KFS"),
    KFS_SUMMARY("KFSSUMMARY"),
    WELCOME_LETTER("WELCOMELETTER"),
    SCHEDULE_B("SCHEDULEB"),
    FORM_97("FORM97"),
	REPAYMENT_SCHEDULE("REPAYMENTSCHEDULE");
	
    private final String value;
    
    JsonKeyFolders(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
