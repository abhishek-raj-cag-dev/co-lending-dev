package com.iexceed.appzillonbanking.cob.core.utils;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class Constants {

    public static final Pattern BI_WEEKLY_PATTERN = Pattern.compile("\\bBI[-\\s]?WEEKLY\\b", Pattern.CASE_INSENSITIVE);
	public static final String INPUT = "INPUT";
	public static final String REJECT = "REJECT";
	public static final String REASSESS = "REASSESS";
	public static final String NATIONALIDPAN = "PAN";
	public static final String TYPES = "TYPES";
	public static final String NATIONALIDUPI = "UPI";
	public static final String DOCFORMATPDF = "pdf";
	public static final String DOCFORMATPNG = "png";
	public static final String DOCFORMATJPEG = "jpeg";
	public static final String DOCFORMATJPG = "jpg";
	public static final String FEATURE_DASHBOARD_WIDGETS = "DashboardStatus";
	public static final String FEATURE_RPC_MAKER = "PENDINGWITHRPCMAKER";
	public static final String FEATURE_RPC_CHECKER = "PENDINGFORRPCCHECKER";
	public static final String FEATURE_SEARCH = "SearchApplication";
	public static final String NOMINEE_MAJOR_KEY = "NomineeMajor";
	public static final String PAYLOAD_COLUMN = "payloadColumn";
	public static final String PAYLOAD = "payload";
	public static final String COMM = "COMM";
	public static final String UPLOAD_DOCS = "UPLOADDOCS";
	public static final String OCR_REQUIRED = "OCR_Required";
	public static final String REGEX_PAN = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
	public static final String REGEX_UPI = "^[\\w\\.\\-]{3,}@[a-zA-Z]{3,}";
	public static final String REGEX_EMAIL = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
	public static final String REGEX_PH_WITH_COUNTRY = "^\\+(?:[0-9] ?){6,14}[0-9]$";
	public static final String REGEX_PH_WITHOUT_COUNTRY = "\\d+";
	public static final String CARD_IMAGES = "CardImages";
	public static final String DEFAULT_CARD = "defaultCard";
	public static final String APP_MASTER_NOT_FOUND = "Application Master data not found. Please check the application ID";
	public static final String APP_LOCKED_BY_RPC = "This Application is WIP by another user $userId. Please select a new application.";
	public static final String APP_RPC_MAKER_NOT_CHECKER = "RPC Maker/Checker cannot be same user. Please select a new application.";
	public static final String SOAP_ERROR_MSG = "We are experiencing an internal error and are unable to complete your request, Please try again in some time.";
	public static final String APP_CB_MISSING = "Applicant CB does not exist";
	public static final int INITIAL_VERSION_NO = 1;
	public static final String IV112 = "IV112";
	public static final String ADDRESSLINE1 = "AddressLine1";
	public static final String MOBILENO = "MobileNo";
	public static final String EMAIL = "Email";
	public static final String ETB = "ETB";
	public static final String NTB = "NTB";
	public static final String CUSTOMER = "Customer";

	// ALL STAGES
	public static final String SELECT_PRODUCT = "SELECTPRODUCT";
	public static final String CUST_VERIFICATION = "CUSTVERIFICATION";
	public static final String KYC_VERIFICATION = "KYCVERIFICATION";
	public static final String CUSTOMER_DETAILS = "CUSTOMERDETAILS";
	public static final String OCCUPATION_DETAILS = "OCCUPATIONDETAILS";
	public static final String NOMINEE_DETAILS = "NOMINEEDETAILS";
	public static final String NOMINEE_DETAILS_DP = "NOMINEEDETAILSDP";
	public static final String UPLOAD_DOCUMENTS = "UPLOADDOCUMENTS";
	public static final String SERVICES = "SERVICES";
	public static final String KYC_MODE = "KYCMODE";
	public static final String TERMS_AND_CONDITIONS = "TERMSANDCONDITIONS";
	public static final String CONFIRMATION = "CONFIRMATION";
	public static final String FATCA = "FATCA";
	public static final String CRS = "CRS";
	public static final String DEPOSIT_DETAILS = "DEPOSITDETAILS";
	public static final String AUTO_PAY_MI = "AUTOPAYMI";
	public static final String INITIAL_FUND = "INITIALFUND";
	public static final String CARD_SERVICE = "CARDSERVICE";
	public static final String ELIGIBLE_CARDS = "ELIGIBLECARDSLIST";
	public static final String LOAN_DETAILS = "LOANDETAILS";
	public static final String EMI_DETAILS = "EMIDETAILS";
	public static final String LOAN_CR_DETAILS = "CREDITDETAILS";
	public static final String VALIDATE_PINCODE = "VALIDATEPINCODE";
	public static final String FUND_ACCOUNT = "FUNDACCOUNT";
	public static final String ACCOUNT_CREATION = "ACCOUNTCREATION";
    public static final String INITIATOR = "Initiator";

	// Roles
	public static final String ACCESS_PERMISSION_INITIATOR = "I";
	public static final String ACCESS_PERMISSION_APPROVER = "A";
	public static final String ACCESS_PERMISSION_BOTH = "B";
	public static final String ACCESS_PERMISSION_VIEWONLY = "VO";
	public static final String ACCESS_PERMISSION_VERIFIER = "V";
	public static final String ACCESS_PERMISSION_RPC = "RPC";

	// Date Formatter
	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm:ss a");
	public static final DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	public static final DateTimeFormatter ADMINFORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");

	// Delete rules
	public static final String HARD_DELETE = "hardDelete";
	public static final String MOVE_TO_HISTORY_TABLES = "moveTohistoryTables";
	public static final String UPDATE_STATUS = "updateStatus";

	// Documents
	public static final String DOCUMENT_FRONT = "docSideFront";
	public static final String DOCUMENT_BACK = "docSideBack";

	public static final String FUNCTIONSEQUENCE = "FUNCTIONSEQUENCE";

	// KYC Type
	public static final String VOTER = "voter";
	public static final String PAN = "pan";
	public static final String DRIVING_LICENSE = "drivingLicense";
	public static final String PASSPORT = "passport";
	public static final String MOBILE_NO = "mobileNo";
	public static final String BANK_DETAILS = "bankAcc";

	// KYC Type value
	public static final String PAN_VALUE = "PAN";
	public static final String DRIVING_LICENSE_VALUE = "Driving Licence";
	public static final String PASSPORT_VALUE = "Passport number";

	// SMTP Constant
	public static final String FROM_EMAIL = "fromEmail";
	public static final String EMAIL_HOST = "emailHost";
	public static final String EMAIL_PORT = "emailPort";
	public static final String EMAIL_TIMEOUT = "emailTimeout";

	// SMS constant
	public static final String SENDER_ID = "senderId";
	public static final String SMS_URL = "smsUrl";
	public static final String SMS_KEY = "smsKey";

	public static final String SANCTION_SMS_URL = "sanctionSmsUrl";
	public static final String SANCTION_SMS_AUTH = "smsAuthorization";
	public static final String SANCTION_SMS_APIKEY = "smsApikey";
	public static final String SANCTION_SENDERID = "sanctionSenderId";
	
	// Replicate KYC Document
	// public static final String CUR_ADDR_PROOF = "As per KYC document";

	// Renewal
	public static final String RENEWAL = "RENEWAL";
	public static final String RENEWAL_LOAN_PRODUCT_CODE = "1002";
	public static final String NEW_LOAN_PRODUCT_CODE = "1009";
	public static final String NEWGEN_RENEWAL_LOAN = "NEWGEN RENEWAL LOAN";

	public static final String GRAMEEN_UNNATI_LOAN = "1";
	public static final String GRAMEEN_RENEWAL_LOAN = "2";

	public static final String APPLICANT = "Applicant";
	public static final String COAPPLICANT = "Co-App";
	public static final String CO_APPLICANT = "Co-Applicant";
	public static final String COAPPLICANT_STRING = "Co-Applicant";
	public static final String VOTER_ID = "Voter ID";
	public static final String PDF_EXTENSION = ".pdf";
	public static final String JPG_EXTENSION = ".jpg";
	public static final String JPEG_EXTENSION = ".jpeg";
	public static final String PNG_EXTENSION = ".png";
	public static final String BOTH = "Both";
	public static final String JOINT = "Joint";

	public static final String PRESENT = "Present";
	public static final String PERMANENT = "Permanent";
	public static final String COMMUNICATION = "Communication";
	public static final String OFFICE = "Office";

	public static final String OCCUPATION = "Occupation";
	public static final String PERSONAL = "Personal";

	public static final String DASHBOARD_STATS_SOURCE = "Iexceed";
	public static final String DASHBOARD_STATS_UNNATI = "Unnati";
	public static final String DASHBOARD_STATS_RENEWAL = "Renewal";
	public static final String DASHBOARD_STATS_OPENMARKET = "OpenMarket";
	public static final String DASHBOARD_STATUS = "Sanction,Sanction Approval 1,Sanction Approval 2,Post Sanction,Disbursement Kit,RPC DB Verification,BM DB Approval,Disbursed,Welcome kit,Service call,Exit,Archival disbursement,LUC,LUC verification,CKYC Archival,Repayment fetch";
	public static final String ALL_DASHBOARD_STATUS = "PENDING,PENDINGFORRPCVERIFICATION,RPCPUSHBACK,APPROVED,RPCVERIFIED,PENDINGDEVIATION,PENDINGREASSESSMENT,CACOMPLETED,SANCTIONED,RESANCTION,DBKITGENERATED,RPCBANKUPDATE,DBKITVERIFIED,DISBURSED,DBPUSHBACK";
	public static final String PENDING_STATUS = "PENDING";
	public static final String PENDING_WITH_RPC_STATUS = "PENDINGFORRPCVERIFICATION,RPCPUSHBACK,APPROVED";
	public static final String CREDIT_ASSESSMENT_PENDING_STATUS = "RPCVERIFIED";
	public static final String SANCTION_PENDING_STATUS = "Sanction,Sanction Approval 1,Sanction Approval 2,Post Sanction";
	public static final String DISBURSEMENT_PENDING_STATUS = "Disbursement Kit,RPC DB Verification,BM DB Approval";
	public static final String DISBURSED_STATUS = "Disbursed,Welcome kit,Service call,Exit,Archival disbursement,LUC,LUC verification,CKYC Archival,Repayment fetch";
	public static final String UNATTENDED_LEADS = "UNATTENDED_LEADS";
	public static final String BM_APPROVAL_PENDING = "BM_RECOMMENDATION_PENDING";
	public static final String SOURCING_STAGE = "SOURCING_STAGE";
	public static final String CB_PASS_IN_KM_STAGE = "CB_PASS_IN_KM_STAGE";
	public static final String PENDING_WITH_RPC = "PENDING_WITH_RPC";
	public static final String CREDIT_ASSESSMENT_PENDING = "CREDIT_ASSESSMENT_PENDING";
	public static final String SANCTION_PENDING = "SANCTION_APPROVAL_PENDING";
	public static final String DISBURSEMENT_PENDING = "DISBURSEMENT_PENDING";
	public static final String DISBURSED = "DISBURSED";
	public static final String CACOMPLETED = "CACOMPLETED";
	public static final String DB_KIT_STATUS = "SANCTIONED";
	public static final String DBKIT = "DBKIT";
	public static final String WELCOMEKIT = "WELCOMEKIT";
	public static final String DBKITGENERATION = "DBKITGENERATION";
	public static final String DISBURSEMENT = "DISBURSEMENT";
	public static final String CBSCHEDULER = "CBSCHEDULER";
	public static final String DB_KIT_GENERATION_PENDING = "DB_KIT_GENERATION_PENDING";
	public static final String DB_KIT_VERIFICATION_STATUS = "DBKITGENERATED";
	public static final String DB_KIT_VERIFIED_STATUS = "DBKITVERIFIED";
	public static final String DB_KIT_VERIFICATION_PENDING = "DB_KIT_VERIFICATION_PENDING";
	public static final String SERVICECALL = "SERVICECALL";
	public static final String LUC = "LUC";
	public static final String PENDINGLUCVERIFICATION = "PENDINGLUCVERIFICATION";

	public static final String SOURCING_STAGES = "3,4,5";
	public static final String CB_PASS_IN_KM_STAGES = "6,7,8,9,10,11";

	public static final String TAT_UNNATI_PRODUCT = "Unnati";
	public static final String TAT_RENEWAL_PRODUCT = "Renewal";
	public static final String APPID = "APZCBO";

	public static final String DAY_0 = "0 DAY";
	public static final String DAY_1 = "1 DAY";
	public static final String DAY_2 = "2 DAY";
	public static final String DAY_3 = "3 DAY";
	public static final String DAY_4 = "4 DAY";
	public static final String DAY_5 = "5 DAY";
	public static final String DAY_5PLUS = "ABOVE 5 DAYS";

	public static final String KM = "KM";
	public static final String BM = "BM";
	public static final String BCM = "BCM";
	public static final String ACM = "ACM";
	public static final String AM = "AM";
	public static final String RM = "RM";
	public static final String DM = "DM";
	public static final String RPCMAKER = "RPC Maker";
	public static final String RPCCHECKER = "RPC Checker";
	public static final String RPC_MAKER = "RPC_MAKER";
	public static final String RPC_CHECKER = "RPC_CHECKER";
	public static final String RPC_DB_VERIFICATION = "RPC_DB_VERIFICATION";
	public static final String MAKER_FRESH_CASE_SEQ_NUM = "2,3";
	public static final String CHECKER_FRESH_CASE_SEQ_NUM = "3,4";
	public static final String PENNY_CHECK_DOCS = "pennyCheckDocs";
	public static final String REGEX_QUERIES = "^(?:\\d+_[A-Z]_oth\\b|\\d+_[A-Z]_[A-Z]\\d+)";
	public static final String REGEX_QUERIES_BCMPI = "^(?:\\d+_oth\\b|\\d+_[A-Z]\\d+)";
	public static final String REGEX_DELIMITER = "~";

	public static final String REGEX_QUERIES_BCMPI2 = "^(?:\\d+_[A-Z]\\d+_oth\\b|\\d+_[A-Z]\\d+_[A-Z]\\d+)";

	public static final String PENNY_DOCUMENT_NAME = "Pennyless exception";
	public static final String PENNY_DOCUMENT_TYPE = "PennyExceptionDoc";

	public static final int REJECTION_EXPIRY_DAYS = 30;

	public static final String SUBMITTED = "SUBMITTED";

	public static final String CREDITASSESSMENT = "CREDITASSESSMENT";
	public static final String VERIFYAPPLICATION = "VERIFYAPPLICATION";
	public static final String PENDINGDEVIATION = "PENDINGDEVIATION";
	public static final String PENDINGREASSESSMENT = "PENDINGREASSESSMENT";
	public static final String SANCTION = "SANCTION";
	public static final String RESANCTION = "RESANCTION";
	public static final String CA_DEVIATION = "CA_DEVIATION";
	public static final String REASSESSMENT = "REASSESSMENT";
	public static final String LUCVERIFIED = "LUCVERIFIED";
	public static final String SYSTEM = "SYSTEM";
	public static final String APPROVED = "APPROVED";
	public static final String PENDING_FOR_RPCVERIFICATION = "PENDINGFORRPCVERIFICATION";
	public static final String RPCPUSHBACK = "RPCPUSHBACK";
	public static final String PENDING ="PENDING";
	public static final String SUBMIT = "SUBMIT";
	public static final String SUBMITDEVIATION = "SUBMITDEVIATION";
	public static final String SUBMITREASSESSMENT = "SUBMITREASSESSMENT";
	public static final String YES = "Y";
	public static final String NO = "N";
    public static final String NO_INSURANCE_OPTION = "NOINSURANCE";
    public static final String APPLICANT_INSURANCE_OPTION = "APPLICANT";
    public static final String JOINT_INSURANCE_OPTION = "JOINT";
    public static final String BOTH_INSURANCE_OPTION = "BOTH";
	public static final String UNNATI_PRODUCT_CODE = "1009";
	public static final String RENEWAL_PRODUCT_CODE = "1002";
	public static final String OPENMARKET_LOAN_PRODUCT_CODE = "1001";
	public static final String ERROR = "ERROR";
	public static final String ERROR1 = "error";
	public static final String MESSAGE = "message";
	public static final String PENDING_FOR_APPROVAL = "PENDINGFORAPPROVAL";
	public static final String APPROVER = "APPROVER";
	public static final String DBKITVERIFICATION = "DBKITVERIFICATION";

	public static final String RENTEDADDRESSSTABILITY = "Less than 1 Year,1 Year,2 Years";
	public static final String OWNEDADDRESSSTABILITY = "Less than 1 Year,1 Year";

	public static final String ALL = "ALL";
	public static final String PUSHBACK_STRING = "PUSHBACK";
	public static final String REJECTED = "REJECTED";

	public static final String SOURCING_STRING = "SOURCING";
	public static final String RPCVERIFIED = "RPCVERIFIED";
	public static final String OTHER_INCOME_OF_FAMILY_MEMBER = "Other Income of family Member";

	// Loan API - Sanction stage
	public static final String LOAN_PRODUCT = "GL.GRM.UNNATI.LN";
public static final String UNNATI_LOAN_PRODUCT = "GL.GRM.UNNATI.LN";
	public static final String OPEN_MARKET_LOAN_PRODUCT = "GL.GRM.UNN.LN.OM";
	public static final String CURRENCY_INR = "INR";
	public static final String PRE_CLOSE_TYPE = "3- All Loan Preclosure";
	public static final String TERM_WEEK = "W";
	public static final String COAPPLICANT_CREATION = "Co-Applicant Creation";
	public static final String COAPPLICANT_UPDATION = "Co-Applicant Updation";
	public static final String APPLICANT_ACTIVATION = "Applicant Activation";
	public static final String COAPPLICANT_DEDUPE_UPDATE = "Co-Applicant Dedupe Update";
	public static final String APPLICANT_CREATION = "Applicant Creation";
	public static final String APPLICANT_DEDUPE_UPDATE = "Applicant Dedupe Update";
	public static final String APPLICANT_UPDATION = "Applicant Updation";
	public static final String LOAN_CREATION = "Loan Creation";
	public static final String LOAN_REJECTION = "Loan Rejection";
	public static final String LOAN_FETCH = "Loan Fetch";
    public static final String CO_CUSTOMER_FETCH = "Co Customer Fetch";
	public static final String LOAN_DISBURSEMENT = "Loan Disbursement";
	public static final String DISBURSEMENT_REPAY_SCHEDULE = "DisbursementRepaySchedule";
	public static final String REPORT_JSONKEYS = "jsonKeysFor";
	public static final String JSON_EXT = ".json";
	public static final String UNCHANGED_RECORD_CODE = "TGVCP-004";

	public static final String PAN_NUMBER_DOC_TYPE = "PAN_NUMBER_PHOTO";
	public static final String SANCTIONLETTER = "SANCTIONLETTER";
	public static final String LOANAPPLICATION = "LOANAPPLICATION";
	public static final String WELCOMELETTER = "WELCOMELETTER";
	public static final String REPAYMENTSCHEDULE = "REPAYMENTSCHEDULE";
	public static final String DEFAULTLANGUAGE = "English";
	public static final String MANUAL = "MANUAL";
	public static final String GENERATED = "GENERATED";
	public static final String FETCH_MANUAL = "FETCH_MANUAL";
	public static final String ESIGN = "ESIGN";
	public static final String DELETE = "DELETE";
	public static final String FETCH = "FETCH";
	public static final String UPLOAD = "UPLOAD";
	public static final String MERGE = "MERGE";
	public static final String DOCUMENT_TYPES = "LOAN_APPLICATION,SANCTION_LETTER,KFS,LOAN_AGREEMENT,SCHEDULE_A_KFS,INSURANCE_CONSENT,MSME,CONSENT_LETTER_NEW,DEMAND_PROMISSORY_NOTE,OTHER_DBDOCS"; //Change the order of these comma seperated values to change the order of the document generate in dbKit.
	public static final String MANUAL_DOCUMENT_TYPES = "LOAN_APPLICATION,SANCTION_LETTER,AML,LOAN_AGREEMENT,SCHEDULE_A_KFS,INSURANCE_CONSENT,MSME,CONSENT_LETTER_NEW,DEMAND_PROMISSORY_NOTE,PAN,OTHER_DBDOCS";
	public static final String LOAN_APPLICATION = "LOAN_APPLICATION";
	public static final String SANCTION_LETTER = "SANCTION_LETTER";
	public static final String KFS = "KFS";
	public static final String LOAN_AGREEMENT = "LOAN_AGREEMENT";
	public static final String SCHEDULE_A_KFS = "SCHEDULE_A_KFS";
	public static final String INSURANCE_CONSENT = "INSURANCE_CONSENT";
	public static final String MSME = "MSME";
	public static final String CONSENT_LETTER_NEW = "CONSENT_LETTER_NEW";
	public static final String DEMAND_PROMISSORY_NOTE = "DEMAND_PROMISSORY_NOTE";
	public static final String OTHER_DBDOCS = "OTHER_DBDOCS";
	public static final String AML = "AML";
	public static final String ACTIVE = "ACTIVE";
	public static final String INACTIVE = "INACTIVE";
	public static final String COAPPLICANT_BANK_DETAILS = "Co-Applicant Bank Details";
	public static final String APPLICANT_BANK_DETAILS = "Applicant Bank Details";
	public static final String PASS_STRING = "PASS";
    public static final String ZERO = "0";
	public static final String REWORK = "REWORK";
    public static final String IEXCEED_FLAG = "I";
	public static final String PRESANCTION = "PRESANCTION";
	public static final String PENDINGPRESANCTION = "PENDINGPRESANCTION";
    public static final String REGENERATE = "REGENERATE";
    public static final String OTHER = "Other";
    public static final String MILK_RATES_PER_STATE = "MILK_RATES_PER_STATE";
	public static final String DEDUPE_TABLE_UPDATION = "DEDUPE_TABLE_UPDATION";
	public static final String ODIYA = "Odiya";
    public static final String ODIA = "Odia";
	public static final String SANCTION_SENDERID_ODIYA = "sanctionSenderIdOdiya";
    public static final String DISABILITY_DETAILS = "DISABILITY_DETAILS";
	public static final String MARRIED = "Married";
	public static final String SPOUSE = "Spouse";
    public static String RE_ASSESSMENT_DOC = "ReAssesmentDoc";
	public static final String REVIEW_SUBMIT = "ReviewSubmit";
	public static final String IN_PRINCIPLE_DECISION = "InPrincipleDecision";

	public static final BigDecimal BM_MIN_LOAN_AMOUNT = BigDecimal.valueOf(50000);
	public static final BigDecimal BM_MAX_LOAN_AMOUNT = BigDecimal.valueOf(75000);

	public static final BigDecimal AM_MIN_LOAN_AMOUNT = BigDecimal.valueOf(75001);
	public static final BigDecimal AM_MAX_LOAN_AMOUNT = BigDecimal.valueOf(125000);

	public static final BigDecimal RM_DM_MIN_LOAN_AMOUNT = BigDecimal.valueOf(125001);
	public static final BigDecimal RM_DM_MAX_LOAN_AMOUNT = BigDecimal.valueOf(250000);

	public static final String LOAN = "LOAN";
	public static final String LOAN_REPAYMENT_SCHEDULE = "LoanRepaymentSchedule";
	public static final String SMS_INTF = "OtpSmsService";
	public static final String OTP = "otp";
	public static final String SOURCING_SUBMISSION = "SOURCING_SUBMISSION";
	public static final String REJECTION = "REJECTION";

	public static final String PLEASE_SELECT = "Please select";
	public static final String DBCAPUSHBACK = "DBCAPUSHBACK";
	public static final String BANKUPDATEPUSHBACK = "BANKUPDATEPUSHBACK";
	public static final String DBSANCTIONPUSHBACK = "DBSANCTIONPUSHBACK";
	public static final String DBPUSHBACK = "DBPUSHBACK";
	public static final String SELF_EMPLOYED = "selfemployed";

	public static final int DIMINISHING_FACTOR_MULTIPLE = -5;
	public static final String PANINPUT = "PANInput";
	public static final String NIDINPUT = "NIDInput";
	public static final String CUSTOMERID = "CustomerID";
	public static final String CUSTOMERID1 = "customerId";
	public static final String NOMINEE = "Nominee";
	public static final String GUARDIAN = "Guardian";
	public static final String HTTPREQHEADERPARAMS = "httpreqheaderparams";
	public static final String JSON_CONTENT_TYPE = "application/json";
	public static final String OCTET_STREAM_CONTENT_TYPE = "application/octet-stream";
	public static final String REQ_RESP_MISSING = "Request/Response parameters are missing.";
	public static final String TIMEOUT = "timeout";
	public static final String SOAP_SERVICE_ERROR_MSG = "Error occurred while executing the soap service, error = ";
	public static final String ALIAS = "alias";
	public static final String ERRORCODE = "errorCode";
	public static final String ERRORMESSAGE = "errorMessage";
	public static final String RESPONSEOBJ = "responseObj";
	public static final String REQUESTOBJ = "requestObj";
	public static final String CONFIG_ERROR_MSG = "Please configure the value of default value properly";
	public static final String DATEFORMAT1 = "yyyy-MM-dd";
	public static final String DATEFORMAT2 = "dd/MM/yyyy";
	public static final String EDITEDFIELDS = "editedFields";
	public static final String QUERY = "query";
	public static final String TIMESTAMP = "timeStamp";
	public static final String SERVICE_DOWN = "Service is temporarily unavailable. Please try again later.";

	public static final String APPROVED_BY = "Approved By";
	public static final String VERIFY = "VERIFY";
	public static final String LOANPATH = "/LOAN/";
	public static final String DBKITVERIFIED = "DBKITVERIFIED";
	public static final String SANCTIONED = "SANCTIONED";
	public static final String DBKITGENERATED = "DBKITGENERATED";
	//Reports
	public static final String EPI_NOS = "EPIsNos";
	public static final String INSTALMENT_NO = "InstalmentNo";
	public static final String OUTSTANDING_PRINCIPAL = "OutstandingPrincipal";
	public static final String PRINCIPAL = "Principal";
	public static final String INTEREST = "Interest";
	public static final String INSTALMENT = "Instalment";
	
	public static final String INSURANCE_DETAILS = "insuranceDetails";
	public static final String DETAILS = "details";
	public static final String APPLICANT_KEY = "applicant";
	public static final String COAPPLICANT_KEY = "coApplicant";

	public static final String PRESENT_ADDRESS_APPLICANT = "presentAddressApplicant";
	public static final String PRESENT_ADDRESS_COAPPLICANT = "presentAddressCoApplicant";
	public static final String BLANK_SPACE = "_____________";
	public static final String SIGNATURE = "signature";
	public static final String DATE_FORMAT_STD = "yyyy-MM-dd";
	public static final String DATE_FORMAT= "dd/MM/yyyy";
	public static final String DECLARATION_SIGNATURE = "declarationSignature";
	public static final String TOTAL = "Total";
	public static final String TOTAL_OS = "TotalOs";
	public static final String INTEREST_OS = "InterestOs";
	public static final String PRINCIPAL_OS = "PrincipalOs";
	public static final String KM_SIGNATURE = "KMSignature";
	public static final String SANCTIONED_AMOUNT = "<sanctionedAmount>";
	public static final String APPLICANT_NAME_AND_SIGN = "applicantNameAndSign";
	public static final String COAPPLICANT_NAME_AND_SIGN = "coapplicantNameAndSign";

	public static final String PRESNT_CITY_IN_YEARS = "presntCityYears";
	public static final String FIELD_NAME = "fieldName";
	public static final String FIELD_VALUE = "fieldValue";
	public static final String PRESENT_ADDRESS_IN_YEARS = "presentAddressYears";

	public static final String APZINTERFACE_EXT = ".apzinterface";
	public static final String ENDPOINT_URL = "endpointurl";

	public static final String ENACH_UDHYAM_DOC_TYPE = "DBBankUpdate,Udyam Reg Success,Udyam Reg Failed";
    public static final String ENACH_DOC_TYPE = "DBBankUpdate";
	public static final String LOAN_ID_NOT_GENERATED = "Loan Id is not generated";
	public static final String ADDRESS_TYPE = "addressType";
	public static final String SECONDARY_KYC = "SecondaryKyc";
	public static final String REASON = "reason";
	public static final String BUSINESS_LOCATION = "businessLocation";
	public static final String BUSINESS_TENURE = "businessTenure";
	public static final String NET_MONTHLY_DECLARED_INCOME = "netMonthlyDeclaredIncome";
	public static final String APPLICANT_BUS_PRE_AS_RES = "applicantBusinessPremiseSameAsResidence";
	public static final String BUSINESS_TYPE = "typeOfBusiness";
	public static final String ORGANISATION_NAME = "nameOfOrganisation";
	public static final String NET_BUSINESS_INCOME = "netBusinessIncome";
	public static final String IMAGE = "image";
	public static final String NGO_ADDDOCUMENT_BDO = "NGOAddDocumentBDO";

    public static final String SUCCESS = "Success";
	public static final String SUCCESS_DMS = "Success-DMS";
    public static final String LOAN_REPAY_SCHEDULE_ERR = "LoanRepaymentSchedule API - Success record not found.";
    public static final String LOAN_REPAY_SCHEDULE_ERR1 = "LoanRepaymentSchedule API - Record not found.";
    public static final String LOAN_REPAY_SCHEDULE_ERR2 = "Error fetching ApiExecutionLog - KfsSheetReport data";
    public static final String DISBURSE_REPAY_SCHEDULE_ERR1 = "DisbursementRepaySchedule API - Success record not found.";
    public static final String DISBURSE_REPAY_SCHEDULE_ERR2 = "DisbursementRepaySchedule API - Record not found.";
    public static final String KEYS_FOR_CONTENT = "keysForContent";
    public static final String BM_APPROVAL = "BM_APPROVAL";
    public static final String DASHBOARD_COUNTS = "DashboardCounts";
    public static final String DASHBOARD_APPLICATIONS = "DashboardApplications";
    public static final String BRANCHES = "branches";
    public static final String DEVIATION_RA = "DEVIATION_RA";
    public static final String ORDER = "order";
    public static final String TOTAL_APPLICATIONS = "totalApplications";
    public static final String FRESHCASES = "freshCases";
    public static final String REWORKCASES ="reworkCases";
    public static final String RPCCHECKERTOMAKER ="rpcCheckerToMaker";
    public static final String SOURCINGPUSHBACK="SourcingPushback";
    public static final String RPCCASEAGEING="RPCCaseAgeing";
    public static final String PENDINGRPC="pendingRPC";
    public static final String PENDINGRPCPAGINATION = "pendingRpcPagination";
    public static final String TOTALELEMENTSPAGE="totalElementsPage";
    public static final String TOTALPAGES="totalPages";
    public static final String APPLICATION_ID="applicationId";
    public static final String BASE64="base64";
    public static final String MERGEDBASE64="mergedBase64";
    public static final String ERROR_DETAILS="errorDetails";
    public static final String ERROR2="Error";
    public static final String HEADER="header";
    public static final String STATUS="status";
    public static final String LOAN_CREATION_SUCCESS="Loan Created Successfully: ";
    public static final String LOAN_DISBURSE_SUCCESS="Loan Disbursed Successfully";
    public static final String MANUAL_PREFIX="MAN";

    public static final String PRODUCT = "PRODUCT";
    public static final String STATE = "STATE";
    public static final String ZONE = "ZONE";
    public static final String REGION = "REGION";
    public static final String AREA = "AREA";
    public static final String BRANCH = "BRANCH";
    public static final String KENDRA = "KENDRA";
    public static final String MEETING_DAY = "MEETING_DAY";

    public static final String VERIFIED_STS = "Verified";
    public static final String ENACH_SUCCESS_STS = "Initiated";

	public static final String NEW_VERNCLR_LANGUAGES = "Malayalam,Odia";
	public static final String MALAYALAM = "Malayalam";
	public static final String MAL_TTF = "NotoSansMalayalam";
	public static final String ODIA_TTF = "NotoSansOria";
	public static final String SINGLE_DOC = "SINGLE_DOC";
    public static final String NO_RECORDS_FOUND = "No records available";
	public static final String DBKIT_FILES = "DBKIT_FILES";
	public static final String WELCOME_KIT_FILES = "WELCOME_DBKIT_FILES";
	public static final String GENERATED_FILES = "GENERATED_FILES";
    public static final String ENACH_VERIFY = "Verify Enach";
	public static final String ENACH_STS = "Enach Status";

	public static final String RENEWAL_NEW_CUSTOMER_STRING = "Create a new Co-Applicant";

	public static final String HOMEMAKER = "HomeMaker";
	public static final String BIP_IMG_PROCESS = "BIP Img Process";

	public static final int MAX_FIRST_NAME_LENGTH = 35;

	public static final String SMS_DOC_DOWNLOAD_URL = "https://caglunnatie2euat.grameenkoota.in/appzillonbankingcob/loan/smsDocDownload?applicationId=#APPLICATIONID#&requestType=#REQUESTTYPE#";
	public static final String HINDU = "HINDU";
	public static final String MUSLIM = "MUSLIM";
	public static final String CHRISTIAN = "CHRISTIAN";
	public static final String OTHER_RELIGION = "OTHERS";

	public static final String UNNATI_PROCESSING_FEE_PERCENTAGE = "1.27";
	public static final String RENEWAL_PROCESSING_FEE_PERCENTAGE = "1.27";
	public static final String OPENMARKET_PROCESSING_FEE_PERCENTAGE = "2";

	public static final String UNNATI_AGE_LIMIT = "22-60";
	public static final String RENEWAL_AGE_LIMIT = "22-60";
	public static final String OPENMARKET_AGE_LIMIT = "24-60";

}
