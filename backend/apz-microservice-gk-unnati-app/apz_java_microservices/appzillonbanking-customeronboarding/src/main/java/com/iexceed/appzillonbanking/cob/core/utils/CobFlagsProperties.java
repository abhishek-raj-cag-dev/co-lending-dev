package com.iexceed.appzillonbanking.cob.core.utils;

public enum CobFlagsProperties {

	APZ_USER_APPID("apzUserAppId", "appzillon User AppId"),
	APPID_SELF_ONBOARDING("appIDSelfOnBoarding", "SELF ONBOARDING APP ID"),
	EXT_SYSTEM_DEDUPE_REQUIRED("extSystemDedupeRequired", "Flag to enable/disable external system dedupe check"),
	CASA_DELETE_RULE("accountDeleteRule", "Delete Rule for CASA"),
	DEPOSIT_DELETE_RULE("depositDeleteRule", "Delete Rule for Deposit"),
	CARDS_DELETE_RULE("cardsDeleteRule", "Delete Rule for Cards"),
	LOANS_DELETE_RULE("loanDeleteRule", "Delete Rule for loans"),
	FILE_UPLOAD("fileUpload", "Default file upload location"),
	NEWGEN_FILE_UPLOAD("newgenFileUpload", "Newgen file upload location"),
	DEFAULT_CARD_LOCATION("defaultCardLocation", "Default location for Card default themes"),
	ALLOW_PARTIAL_APPLICATION("allowPartiallyFilledApplication", "Flag for allowing partially filled applications"),
	DEMO_MODE("demoMode", "Demo mode flag"),
	ACCOUNT_STP("accountSTP", "Flag to enable direct application creation at CBS for Accounts"),
	DEPOSIT_STP("depositSTP", "Flag to enable direct application creation at CBS for Deposits"),
	CARD_STP("cardSTP", "Flag to enable direct application creation at CBS for Cards"),
	LOAN_STP("loanSTP", "Flag to enable direct application creation at CBS for Loans"),
	DEFAULT_CASA_PRODUCT("defaultCasaProductCode", "Default CASA product for Deposits"),
	DEFAULT_CASA_PRODUCTLN("defaultCasaProductCodeLN", "Default CASA product for Loans"),
	DEFAULT_CASA_GRP("defaultCasaProductGrpCode", "Default CASA product group for Deposits"),
	DEFAULT_BRANCH_CARDS("defaultBranchForCards", "Default branch for Cards"),
	EXTRACTED_ENTITIES_KEY("extractedEntities", "OCR KEY"),
	NATIONAL_ID_KEY("nationalId", "National Id KEY"),
	ADDRESS_KEY("address", "Address Key for OCR"),
	NUM_OF_REC_IN_WIDGET("numOfRecordsInWidget", "Number of records in each widget"),
	NUM_OF_DAYS_RECORDS("numOfDaysRecords", "Number of days to query"),
	
    BRE_CHECK_RETRY_IRIS_ERRORS("breCheckRetryIrisErrors", "Retry IRIS errors from bre cb Check"),
    BRE_CHECK_TEMP_IRIS_ERRORS("breCheckTempIrisErrors", "Temporary IRIS errors from bre cb Check"),
    BRE_RETRY_ATTEMPT("breRetryAttempt", "Retry count for BRE due to CRIF Errors"),
	
	ACC_CREATION_INTF("createCasaIntfName", "Interface name for account creation at external system"),
	DEP_ACC_CREATION_INTF("createDepositIntfName", "Interface name for deposit account creation at external system"),
	CARD_ACC_CREATION_INTF("createCardIntfName", "Interface name for card account creation at external system"),
	LOAN_ACC_CREATION_INTF("createLoanIntfName", "Interface name for loan account creation at external system"),
	
	CASA_DEP_ACC_CREATION_INTF("createCasaDepositIntfName", "Interface name for CASA and deposit account creation at external system"),
	CASA_LOAN_ACC_CREATION_INTF("createCasaLoanIntfName", "Interface name for CASA and loan account creation at external system"),
	
	FETCH_IFSC_INTF("fetchIFSCIntf", "Interface name for fetch IFSC"),
	FETCH_IFSC_ID("fetchIFSCId", "ID for fetch IFSC"),
	FETCH_IFSC_GKV("fetchIFSCGkv", "GKV for fetch IFSC"),
	FETCH_IFSC_METHOD("fetchIFSCMethod", "Method name for fetch IFSC"),
	
	LOAN_VALIDATE_VOTER_ID_INTF("validateVoterIdIntf", "Interface name for validate voter id number"),
	LOAN_VALIDATE_PAN_INTF("validatePanIntf", "Interface name for validate pan number"),
	LOAN_VALIDATE_DL_INTF("validateDlIntf", "Interface name for validate driving license number"),
	LOAN_VALIDATE_PASSPORT_INTF("validatePassportIntf", "Interface name for validate passport number"),
	LOAN_DEDUPE_VOTER_ID_INTF("dedupeVoterIdIntf", "Interface name for find dedupe voter id number"),
	BRE_CB_CHECK_INTF("breCBCheckIntf", "Interface name for check BRE CB service"),
	BRE_CB_REPORT_INTF("breCBReportIntf", "Interface name for check BRE CB Report Generation"),
	OM_BRE_CB_REPORT_INTF("OpenMarketBreCBReportIntf", "Interface name for check Open market BRE CB Report Generation"),
	LOAN_CREATION_INTF("loanCreationIntf", "Interface name for Loan Creation"),
	LOAN_REJECTION_INTF("loanRejectionIntf", "Interface name for Loan Rejection"),
	LOAN_REPAYMENT_SCHEDULE_INTF("loanRepaymntScheduleIntf", "Interface name for Loan repayment schedule"),
	LOAN_DISBURSEMENT_INTF("loanDisbursementIntf", "Interface name for Loan Disbursement"),
	DISBURSEMENT_REPAYMENTSCHEDULE_INTF("disbursementRepaymentSchedule","Interface name for repayment schedule at Disbursement stage"),
	COAPPLICANT_UPDATION_INTF("coapplicantUpdationIntf", "Interface name for initiating Customer CoApplicant Link"),
	APPLICANT_ACTIVATION_INTF("applicantActivationIntf", "Interface name for activating applicant"),
    DEDUPE_TABLE_UPDATE_INTF("dedupeTableUpdateIntf", "Interface name for updating dedupe table"),
	SIGNZY_PENNYLESS_CHECK_INTF("signzyPennylessCheckIntf", "Interface name for check BRE CB service"),
	LOAN_DEDUPE_MOBILE_NO_INTF("dedupeMobileNoIntf", "Interface name for find dedupe mobile number"),
	LOAN_DEDUPE_VOTER_ID_GKV("dedupeVoterIdGkv", "GKV for find dedupe voter id number"),
	LOAN_DEDUPE_VOTER_ID_METHOD("dedupeVoterIdMethod", "Method name for find dedupe voter id number"),
	LOAN_DEDUPE_VOTER_ID_ID("dedupeVoterIdId", "ID for find dedupe voter id number"),
	LOAN_DEDUPE_BANK_INTF("dedupeBankIntf", "Interface name for find dedupe bank details"),
	LOAN_DEDUPE_BANK_ID_GKV("dedupeBankIdGkv", "GKV for find dedupe Bank account number"),
	LOAN_DEDUPE_BANK_ID_METHOD("dedupeBankIdMethod", "Method name for find dedupe Bank account number"),
	LOAN_DEDUPE_BANK_ID_ID("dedupeBankIdId", "ID for find dedupe Bank account number"),
	LOAN_DEDUPE_BANK_SEARCH_ID("dedupeBankSearchId", "ID for find dedupe Bank account number"),	
	EXISTING_LOAN_URL("existingLoanUrl", "Request URL for fetch existing loan"),
	EXISTING_LOAN_REQUST("existingLoanRequest", "Request for fetch existing loan"),
	EXISTING_LOAN_COMPANY("existingLoanCompany", "Company value for fetch existing loan"),
	EXISTING_LOAN_USERNAME("existingLoanUserName", "Username value for fetch existing loan"),
	EXISTING_LOAN_PASSWORD("existingLoanPassword", "Password value for fetch existing loan"),
	APZ_USER_ROLE("apzUserRoles", "appzillon User roles"),
	KM("KM", "Kendra Manager"),
	BM("BM", "Branch Manager"),
	RPC("RPC", "RPC Maker/Checker"),
	PRODUCT_TYPE("productType", "Highmark header product type"),
	LOS_VERSION("losVersion", "Highmark header LOS version"),
	USER_ID("userId", "Highmark header user Id"),
	INDV("indv", "Highmark header indv"),
	SCORE("score", "Highmark header score"),
	GROUP("group", "Highmark header group"),
	CNS_INDV("cnsIndv", "Highmark header CNS INDV"),
	CNS_SCORE("cnsScore", "Highmark header CNS SCORE"),
	IOI("ioi", "Highmark header IOI"),
	ENTITY_ID("entityId", "Highmark applicant entity ID"),
	LOAN_TYPE("loanType", "Highmark application Loan Type"),
	COAPP_CUSTOMER_TYPE("coAppCustomerType", "Highmark Co-Applicant Customer Type"),
	HIGHMARK_CHECK_URL("highmarkCheckUrl", "Request URL for highmark check"),
	HIGHMARK_REPORT_URL("highmarkReportUrl", "Request URL for highmark report"),
	HIGHMARK_CHECK_USERID("highmarkCheckUserId", "user id for highmark check"),
	HIGHMARK_CHECK_PASSWORD("highmarkCheckPassword", "password for highmark check"),
	NEWGEN_WORKITEM_CREATION_URL("workitemCreationUrl", "Request URL for workitem creation"),
	NEWGEN_SENDBACK_WORKITEMS_URL("sendbackWorkitemUrl","Request URL for sendback workitem"),
	NEWGEN_WIP_DEDUPE_URL("wipDedupeUrl","Request URL for wip dedupe check"),
	NEWGEN_SENDBACK_DATAFETCH_URL("sendbackDataFetchUrl","Request URL for sendback data fetch"),
	WORKITEM_MASTERS("workitemMasters","newgen master data"),
	CB_REPORT_READ_TIMEOUT("cbReportReadTimeout","cb Report Read Timeout"),
	CB_REPORT_CONNECT_TIMEOUT("cbReportConnectTimeout","cb Report Connect Timeout"),
	CB_REPORT_INTERNAL_URL("cbReportInternalURL","cb Report Internal URL"),
	CB_REPORT_EXTERNAL_URL("cbReportExternalURL","cb Report External URL"),
	CB_EXPIRY_DAYS("cbExpiryDays","cb Expiry Days"),
	CB_CHECK_SCHEDULER_FLAG("cbCheckSchedulerFlag","cb Check Scheduler Flag"),
	CB_CHECK_CRON_TIME("cbCheckCronTime","cb Check Cron Time"),
	TAT_START_TIME("tatStartTime","TAT Start Time"),
	TAT_END_TIME("tatEndTime","TAT End Time"),
	HIGHMARK_CONNECT_TIMEOUT("highmarkConnectTimeout", "Highmark Connect Timeout"),
	HIGHMARK_READ_TIMEOUT("highmarkReadTimeout", "Highmark Read Timeout"),
	WORKITEM_CREATION_CONNECT_TIMEOUT("workitemCreationConnectTimeout", "Workitem Creation Connect Timeout"),
	WORKITEM_CREATION_READ_TIMEOUT("workitemCreationReadTimeout", "Workitem Creation Read Timeout"),
	SENDBACK_WORKITEM_CONNECT_TIMEOUT("sendbackWorkitemConnectTimeout", "Sendback Workitem Connect Timeout"),
	SENDBACK_WORKITEM_READ_TIMEOUT("sendbackWorkitemReadTimeout", "Sendback Workitem Read Timeout"),
	NEWGEN_WIPDEDUPE_CONNECT_TIMEOUT("newgenWipDedupeConnectTimeout", "Newgen WipDedupe Connect Timeout"),
	NEWGEN_WIPDEDUPE_READ_TIMEOUT("newgenWipDedupeReadTimeout", "Newgen WipDedupe Read Timeout"),
	SENDBACK_DATAFETCH_CONNECT_TIMEOUT("sendbackDataFetchConnectTimeout", "Sendback Data Fetch Connect Timeout"),
	SENDBACK_DATAFETCH_READ_TIMEOUT("sendbackDataFetchReadTimeout", "Sendback Data Fetch Read Timeout"),
	SEND_SMS_CONNECT_TIMEOUT("sendSMSConnectTimeout", "Send SMS Connect Timeout"),
	SEND_SMS_READ_TIMEOUT("sendSMSReadTimeout", "Send SMS Read Timeout"),
	FETCH_APPLICATION_EXPIRY_DURATION("fetchApplicationExpiryDuration", "Fetch application expiry duration in minutes"),
	REJECTION_EXPIRY_DAYS("rejectionExpiryDays", "Number of days to renew rejected applications"),
	
	RPP_VERSION("rppVersion", "rppVersion for enach api"),
	MERCHANT_NAME("merchantName", "merchantName rppVersion for enach api"),
	MERCHANT_ID("merchantId", "merchantId for enach api"),
	MERCHANT_EMAIL("merchantEmail", "merchantEmail"),
	MERCHANT_MOBILE("merchantMobile", "merchantMobile for enach api"),
	PRODUCT_IDENTIFIER("productIdentifier", "productIdentifier for enach api"),
	DISTRIBUTOR_IDENTIFIER("distributorIdentifier", "distributorIdentifier for enach api"),
	FREQUENCY("frequency", "frequency for enach api"), 
	AMOUNT_TYPE("amountType", "amountType for enach api"),
	MAX_AMOUNT("maxAmount", "maxAmount for enach api"),
	TOTAL_AMOUNT("totalAmount", "totalAmount for enach api"),
	NOTE_FROM_MERCHANT("noteFromMerchant", "noteFromMerchant for enach api"),
	ENACH_RPPSERVICE_INTF("enachRppServiceIntf", "Interface name for verify enachRpp service"),
	ENACH_TRANSACTION_STATUS_INTF("enachTransactionStatusIntf", "Interface name for check enach transactionStatus service"),
	
	LOANPURPOSE_IDENTIFIER("loanPurposeIdentifier", "Loan Purpose id for Loan creation API"),
	RELATIONSHIP_CODES("relationshipCodes", "relationship mapping codes for Loan creation API"),
	CUSTOMER_OCCUPATION("customerOccupation", "customer occupation from T24 master table for loan creation API"),
	COAPPL_PHONENUMBER_DEFAULT("coapplPhoneNumberDefault", "Default value for coapplicant phone number"),
	BUSINESS_CODES("nameOfBusinessCodes", "customer occupation from T24 master table for loan creation API"),
	PRAYAAS_CUSTCREATION("prayaasCustCreation", "customer creation API url"),
	PRAYAAS_CUSTUPDATION("prayaasCustUpdation", "customer updation API url"),
	NONBLOCKING_DISBURSEMENT_ERROR("nonBlockingDisbursementError", "Acceptable Disbursement API error which to be considered success"),
	RPC_REPORT_USER("rpcReportUser", "User Id for centrailized RPC report generation"),
	
	LOAN_FETCH_COMPANY("loanFetchCompany", "Company Name for Loan Fetch API"),
	LOAN_FETCH_OPERAND("loanFetchOperand", "Operand for Loan Fetch API criteria"),
	LOAN_FETCH_PASSWORD("loanFetchPassword", "Password for Loan Fetch API authentication"),
	LOAN_FETCH_USERNAME("loanFetchUsername", "Username for Loan Fetch API authentication"),
	LOAN_FETCH_COLUMN_NAME("loanFetchColumnName", "Column Name used for filtering in Loan Fetch API"),
	LOAN_FETCH_INTF("loanFetchIntf", "Interface name for Loan Fetch details"),
    CO_CUST_FETCH_INTF("coCustFetchIntf", "Interface name for Co Customer Fetch details"),
	LOAN_FETCH_INPUT_ERROR("loanFetchInputError", "Associated error for which loan fetch api to be called further"),
    CO_CUST_FETCH_INPUT_ERROR("coCustFetchInputError", "Associated error for which Co Customer api to be called further"),

	DMS_UPLOAD_PATH("dmsFileUploadPath", "Server path to upload DMS files from directory"),
	DMS_CABINET_NAME("cabinetName", "Cabinet name for DMS document push"),
	DMS_FOLDER_INDEX("folderIndex", "folderIndex for DMS document push"),
	DMS_USERDB_ID("userDbId", "UserdbId for DMS document push"),
	DMS_VOLUME_ID("volumeId", "VolumeId for DMS document push"),
	DMS_UPLOADED_FILE_EXT("uploadedFileExt", "Created by app name for DMS document push"),
	DMS_USERNAME("userName", "Username for DMS document push"),
	DMS_USERPASSWORD("userPassword", "Userpassword for DMS document push"),
	DMS_USER_EXIST("isUserExist", "Is user exist for DMS"),
	DMS_CONNECT_OPTION("option","Option for connecting cabinet"),
	DMS_DISCONNECT_OPTION("disconnectOption","Option for disconnecting cabinet"),
	DMS_LOCALE("locale", "Language for DMS push"),
	DMS_CONNECT_CABINET_INTF("connectCabinetIntf", "Interface name for Omnidocs api to connect to cabinet"),
	BRE_CHECK_THREAD_TIMEOUT("breCheckThreadTimeout", "Executor thread timeout for BRE check"),
	DELETE_PROSPECT_CUSTOMER_API_INTF("deleteProspectCustomerApiIntf", "Interface name for delete prospect customer API"),

	UNNATI_PRE_CLOSE_TYPE("unnatiPreCloseType", "Unnati Pre Close Type for Loans"),
    RENEWAL_PRE_CLOSE_TYPE("renewalPreCloseType", "Renewal Pre Close Type for Loans"),

	SENDERID_UNIQUE("senderIdUnique", "Unique sender id for all SMS templates"),


	REPAYMENT_MODE("repaymentMode", "repaymentMode for welcome letter"),
	REPORT_LANGUAGES("reportLanguages", "report Languages"),
    LUC_WAIT_DAYS("lucWaitDays", "LUC wait Days for loan application"),
	DUPLICATE_REQUEST_THRESHOLD("duplicateRequestThreshold", "Threshold for duplicate request check in minutes"),

	BIP_SUBMIT_IMG_INTF("bipSubmitImageIntf", "Interface name for BIP Submit Image bot service"),
	/**
	 * author Ankit.CAG
	 */
	KYC_PASSPORT_INTF("KycPassportIntf", "Interface name for Kyc Passport service"),
	KYC_DRIVING_LICENSE_INTF("KycDrivingLicenseIntf", "Interface name for Kyc-DL service"),
	DRIVING_LICENSE_OCR_INTF("DrivingLicenseOcrIntf", "Interface name for DL OCR service"),
	ADHAR_REDACT_OCR_INTF("AdharRedactOcrIntf", "Interface name for AdharRedact OCR service"),
	VOTER_FRONT_OCR_INTF("VoterFrontOcrIntf", "Interface name for Voter-Front OCR service"),
	VOTER_BACK_OCR_INTF("VoterBackOcrIntf", "Interface name for Voter-Back OCR service"),
	PAN_CHECK_OCR_INTF("PanCheckOcrIntf", "Interface name for Pan check OCR service"),
	SMS_DOC_DOWNLOAD_URL("smsDocDownloadUrl", "URL for downloading documents in SMS"),

	OPEN_MARKET_BRE_INTF("openMarketBREIntf", "Interface name for open market BRE API"),
	IS_SOURCING_DEDUPEUPDATE_ENABLED("isSourcingDedupeUpdateEnabled", "Flag to enable/disable updating dedupe table from sourcing module"),
	CUSTOMER_LOAN_CHECK_API_INTF("customerLoanCheckApiIntf", "Interface name for customer loan check API"),
	EXTERNAL_DEDUPE_CHECK_V2("externalDedupeCheckV2", "Interface name for external dedupe check API V2"),

/**
	 * author Ankit.CAG
	 */
	SUBMIT_ESTAMP_ESIGN_DOC_INTF("SubmitEstampESignIntf", "Interface name for submit Estamp and ESign document"),
	FETCH_ESIGN_LINK_INTF("FetchEsignLinkIntf", "Interface name for get ESign link"),
	RETRIGGER_ESIGN_INTF("RetriggerEsignIntf", "Interface name for Retrigger ESign"),
	//------END------
	
	/**
	 * author Abhishek.Raj.CAG
	 */
	STAMP_DUTY_CALC_INTF("StampDutyCalcIntf", "Interface name for Stamp Duty Calculator Service"),
	TRANS_DOC_INTF("TransDocIntf", "Interface name for Transaction DOC Service"),
	LOAN_DOC_STATUS_INTF("LoanDocStatusIntf", "Interface name for Loan DOC status Service"),
	DDE_SIGNED_DOC_SBMSN_INTF("DDESignedDOCIntf", "Interface name for DDE signed doc submission"),
	DOC_PFX_STAMP_INTF("DocPfxStampIntf", "Interface for Document Pfx CAGL Stamp"),

	NESL_STAGE_STORE_TRACKER_INTF("NeslStageStoreTrackerIntf", "Interface for Nesl Stage tracker inserting"),
	NESL_STAGE_FETCH_TRACKER_INTF("NeslStageFetchTrackerIntf", "Interface for Nesl Stage tracker fetching");
	// ------END------

	private final String key;
	private final String value;
	
	CobFlagsProperties(String key, String value) {
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
