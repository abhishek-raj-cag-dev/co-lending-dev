package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_APPLICATION_MASTER")
@IdClass(ApplicationMasterId.class)
@Getter
@Setter
@AllArgsConstructor
public class ApplicationMaster {

	@Id
	private String appId;

	@Id
	private String applicationId;

	@Id
	private Integer versionNum;

	@JsonProperty("applicationDate")
	@Column(name = "APPLICATION_DATE")
	private LocalDate applicationDate;

	@CreationTimestamp
	@JsonProperty("createTs")
	@Column(name = "CREATE_TS")
	private LocalDateTime createTs;
	
	@UpdateTimestamp
	@JsonProperty("updateTs")
	@Column(name = "UPDATE_TS")
	private LocalDateTime updateTs;

	@JsonProperty("lockTs")
	@Column(name = "LOCK_TS")
	private LocalDateTime lockTs;

	@JsonProperty("updatedBy")
	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@JsonProperty("createdBy")
	@Column(name = "CREATED_BY")
	private String createdBy;

	@JsonProperty("applicationType")
	@Column(name = "APPLICATION_TYPE")
	private String applicationType;

	@JsonProperty("kycType")
	@Column(name = "KYC_TYPE")
	private String kycType;

	@JsonProperty("applicationStatus")
	@Column(name = "APPLICATION_STATUS")
	private String applicationStatus;

	@JsonProperty("customerId")
	@Column(name = "CUSTOMER_ID")
	private BigDecimal customerId;

	@JsonProperty("mobileNumber")
	@Column(name = "MOBILE_NUMBER")
	private String mobileNumber;

	@JsonProperty("nationalId")
	@Column(name = "NATIONAL_ID")
	private String nationalId;

	@JsonProperty("pan")
	@Column(name = "PAN")
	private String pan;

	@JsonProperty("productGroupCode")
	@Column(name = "PRODUCT_GROUP_CODE")
	private String productGroupCode;

	@JsonProperty("productCode")
	@Column(name = "PRODUCT_CODE")
	private String productCode;

	@JsonProperty("searchCode1")
	@Column(name = "SEARCH_CODE1")
	private String searchCode1;

	@JsonProperty("searchCode2")
	@Column(name = "SEARCH_CODE2")
	private String searchCode2;

	@JsonProperty("assignedTo")
	@Column(name = "ASSIGNED_TO")
	private String assignedTo;

	@JsonProperty("emailId")
	@Column(name = "EMAILID")
	private String emailId;

	@JsonProperty("currentStage")
	@Column(name = "CURRENT_STAGE")
	private String currentStage;

	@JsonProperty("declarationFlag")
	@Column(name = "DECLARATION_FLAG")
	private String declarationFlag;

	@JsonProperty("accNumber")
	@Column(name = "ACCOUNT_NUMBER")
	private String accNumber;

	@JsonProperty("mobileVerStatus")
	@Column(name = "MOBILE_VER_STATUS")
	private String mobileVerStatus;

	@Column(name = "EMAIL_VER_STATUS")
	private String emailVerStatus;

	@JsonProperty("currentScreenId")
	@Column(name = "CURRENT_SCREEN_ID")
	private String currentScreenId;

	@JsonProperty("remarks")
	@Column(name = "REMARKS")
	private String remarks;

	@JsonProperty("applicantsCount")
	@Column(name = "NUM_OF_APPLICANTS")
	private Integer applicantsCount;

	@JsonProperty("relatedApplicationId")
	@Column(name = "RELATED_APPLICATION_ID")
	private String relatedApplicationId;

	@JsonProperty("kendraId")
	@Column(name = "KENDRA_ID")
	private String kendraId;

	@JsonProperty("kendraName")
	@Column(name = "KENDRA_NAME")
	private String kendraName;

	@JsonProperty("branchId")
	@Column(name = "BRANCH_ID")
	private String branchId;

	@JsonProperty("branchName")
	@Column(name = "BRANCH_NAME")
	private String branchName;

	@JsonProperty("memberId")
	@Column(name = "MEMBER_ID")
	private String memberId;

	@JsonProperty("primaryKycType")
	@Column(name = "PRIMARY_KYC_TYPE")
	private String primaryKycType;

	@JsonProperty("primaryKycId")
	@Column(name = "PRIMARY_KYC_ID")
	private String primaryKycId;

	@JsonProperty("secondaryKycType")
	@Column(name = "SECONDARY_KYC_TYPE")
	private String secondaryKycType;

	@JsonProperty("secondaryKycId")
	@Column(name = "SECONDARY_KYC_ID")
	private String secondaryKycId;

	@JsonProperty("workitemNo")
	@Column(name = "WORKITEM_NO")
	private String workitemNo;

	@JsonProperty("currentStageNo")
	@Column(name = "CURRENT_STAGE_NO")
	private Integer currentStageNo;

	@JsonProperty("alternateVoterId")
	@Column(name = "ALTERNATE_VOTER_ID")
	private String alternateVoterId;

	@JsonProperty("applicantT24Id")
	@Column(name = "APPLICANTT24ID")
	private String applicantT24Id;;

	// Generic transient fields
	@Transient
	@JsonProperty("mainProductGroupCode")
	private String mainProductGroupCode;

	// transient fields for casa start
	@Transient
	@JsonProperty("custDtlId")
	private BigDecimal custDtlId;

	@Transient
	@JsonProperty("coAppCustDtlId")
	private BigDecimal coAppCustDtlId;

	@Transient
	@JsonProperty("coAppCustomerName")
	private String coAppCustomerName;

	@Transient
	@JsonProperty("custDtlSlNum")
	private Integer custDtlSlNum; // Stored in SEQ_NO of TB_ABOB_CUSTOMER_DETAILS.

	@Transient
	@JsonProperty("queueStatus")
	private String queueStatus;

	@Transient
	@JsonProperty("applicationWorkflow")
	private ApplicationWorkflow applicationWorkflow;

	@Transient
	@JsonProperty("wfStatus")
	private String wfStatus;

	@Transient
	@JsonProperty("wfCreatedBy")
	private String wfCreatedBy;

	@Transient
	@JsonProperty("wfCreateTs")
	private LocalDateTime wfCreateTs;

	@Transient
	@JsonProperty("statusCount")
	private long statusCount;

	@Transient
	@JsonProperty("productCount")
	private long productCount;

	// transient fields for casa end

	// transient fields for deposit start
	@Transient
	@JsonProperty("isRd")
	private String isRd;
	// transient fields for deposit end

	@Transient
	@JsonProperty("customerDetails")
	private CustomerDetails customerDetails;
	
	@Transient
	@JsonProperty("cibilDetails")
	private CibilDetails cibilDetails;

	@Transient
	@JsonProperty("customerName")
	private String customerName;

	@Transient
	@JsonProperty("cbStatus")
	private String cbStatus;
	
	@Transient
	@JsonProperty("applicantCBExpiry")
	private boolean applicantCBExpiry;

	@Transient
	@JsonProperty("coApplicantCBExpiry")
	private boolean coApplicantCBExpiry;
	
	@Transient
	@JsonProperty("caglOs")
	private String caglOs;

	@Transient
	@JsonProperty("kendraMeetingDay")
	private String kendraMeetingDay;
	
	@Transient
	@JsonProperty("kendraVintageYrs")
	private String kendraVintageYrs;

	@Transient
	@JsonProperty("payloadColumn")
	private String payloadColumn;

	@Transient
	@JsonProperty("loanAmount")
	private BigDecimal loanAmount;

	@Transient
	@JsonProperty("applicantCBDtlid")
	private BigDecimal applicantCBDtlid;

	@Transient
	@JsonProperty("coApplicantCBDtlid")
	private BigDecimal coApplicantCBDtlid;

	@Transient
	@JsonProperty("isRework")
	private String isRework;

	@Transient
	@JsonProperty("TAT")
	private String TAT;

	@Transient
	@JsonProperty("rpcTAT")
	private String rpcTAT;

	@Transient
	@JsonProperty("totalApplications")
	private Long totalApplications;

	@Transient
	@JsonProperty("freshCases")
	private Long freshCases;

	@Transient
	@JsonProperty("reworkCases")
	private Long reworkCases;

	@Transient
	@JsonProperty("rpcCheckerToMaker")
	private Long rpcCheckerToMaker;

	@Transient
	@JsonProperty("dayZero")
	private Long dayZero;

	@Transient
	@JsonProperty("dayOne")
	private Long dayOne;

	@Transient
	@JsonProperty("dayTwo")
	private Long dayTwo;

	@Transient
	@JsonProperty("dayThree")
	private Long dayThree;

	@Transient
	@JsonProperty("dayFour")
	private Long dayFour;

	@Transient
	@JsonProperty("dayFive")
	private Long dayFive;

	@Transient
	@JsonProperty("moreThanFiveDays")
	private Long moreThanFiveDays;

	@Transient
	@JsonProperty("pushbackCases")
	private Long pushbackCases;

	@Transient
	@JsonProperty("completedCases")
	private Long completedCases;

	@Transient
	@JsonProperty("applications")
	private Page<ApplicationMaster> applications;

	@Transient
	@JsonProperty("sourcingCount")
	private Long sourcingCount;

	@Transient
	@JsonProperty("caCount")
	private Long caCount;

	@Transient
	@JsonProperty("sanctionCount")
	private Long sanctionCount;

	@Transient
	@JsonProperty("reSanctionCount")
	private Long reSanctionCount;

	@Transient
	@JsonProperty("dbKitCount")
	private Long dbKitCount;

	@Transient
	@JsonProperty("disbursementCount")
	private Long disbursementCount;

	@Transient
	@JsonProperty("lucCount")
	private Long lucCount;

	@Transient
	@JsonProperty("lucVerificationCount")
	private Long lucVerificationCount;

	@Transient
	@JsonProperty("reLucCount")
	private Long reLucCount;

	@Transient
	@JsonProperty("overAllLeadsCount")
	private Long overAllLeadsCount;

	@Transient
	@JsonProperty("unactionedLeadsCount")
	private Long unactionedLeadsCount;

	@Transient
	@JsonProperty("bmApprovalCount")
	private Long bmApprovalCount;

	@Transient
	@JsonProperty("rpcVerificationCount")
	private Long rpcVerificationCount;

	@Transient
	@JsonProperty("creditAssessmentCount")
	private Long creditAssessmentCount;

	@Transient
	@JsonProperty("deviationRACount")
	private Long deviationRACount;

	@Transient
	@JsonProperty("disbursementInprogressCount")
	private Long disbursementInprogressCount;

	@Transient
	@JsonProperty("disbursedCount")
	private Long disbursedCount;

	@Transient
	@JsonProperty("postDisbursementCount")
	private Long postDisbursementCount;

	@Transient
	@JsonProperty("rejectedCount")
	private Long rejectedCount;

	public ApplicationMaster(Long overAllLeadsCount, Long unactionedLeadsCount,
			Long sourcingCount,
			Long bmApprovalCount,
			Long rpcVerificationCount,
			Long creditAssessmentCount,
			Long deviationRACount,
			Long sanctionCount,
			Long disbursementInprogressCount,
			Long disbursedCount,
			Long postDisbursementCount,
			Long rejectedCount) {
		this.overAllLeadsCount = overAllLeadsCount;
		this.unactionedLeadsCount = unactionedLeadsCount;
		this.sourcingCount = sourcingCount;
		this.bmApprovalCount = bmApprovalCount;
		this.rpcVerificationCount = rpcVerificationCount;
		this.creditAssessmentCount = creditAssessmentCount;
		this.deviationRACount = deviationRACount;
		this.sanctionCount = sanctionCount;
		this.disbursementInprogressCount = disbursementInprogressCount;
		this.disbursedCount = disbursedCount;
		this.postDisbursementCount = postDisbursementCount;
		this.rejectedCount = rejectedCount;
	}


	public ApplicationMaster(Long sourcingCount, Long caCount, Long sanctionCount, Long reSanctionCount, Long dbKitCount,
			Long disbursementCount, Long lucCount, Long lucVerificationCount, Long reLucCount) {
		this.sourcingCount = sourcingCount;
		this.caCount = caCount;
		this.sanctionCount = sanctionCount;
		this.reSanctionCount = reSanctionCount;
		this.dbKitCount = dbKitCount;
		this.disbursementCount = disbursementCount;
		this.lucCount = lucCount;
		this.lucVerificationCount = lucVerificationCount;
		this.reLucCount = reLucCount;
	}


	public ApplicationMaster(Long totalApplications, Long freshCases, Long reworkCases, Long rpcCheckerToMaker,
			Long pushbackCases, Long completedCases, Long dayZero, Long dayOne, Long dayTwo, Long dayThree,
			Long dayFour, Long dayFive, Long moreThanFiveDays) {
		this.totalApplications = totalApplications;
		this.freshCases = freshCases;
		this.reworkCases = reworkCases;
		this.rpcCheckerToMaker = rpcCheckerToMaker;
		this.pushbackCases = pushbackCases;
		this.completedCases = completedCases;
		this.dayZero = dayZero;
		this.dayOne = dayOne;
		this.dayTwo = dayTwo;
		this.dayThree = dayThree;
		this.dayFour = dayFour;
		this.dayFive = dayFive;
		this.moreThanFiveDays = moreThanFiveDays;
	}

	// For Paginated Applications
	public ApplicationMaster(String applicationId, String memberId, String customerName, String branchName,
			String rpcTAT, BigDecimal loanAmount, String applicationStatus, String isRework) {
		this.applicationId = applicationId;
		this.memberId = memberId;
		this.customerName = customerName;
		this.branchName = branchName;
		this.rpcTAT = rpcTAT;
		this.loanAmount = loanAmount;
		this.applicationStatus = applicationStatus;
		this.isRework = isRework;
	}

	public ApplicationMaster(String applicationId, String memberId, String customerName, String branchName,
			String rpcTAT, BigDecimal loanAmount, String applicationStatus, String isRework, String productCode) {
		this.applicationId = applicationId;
		this.memberId = memberId;
		this.customerName = customerName;
		this.branchName = branchName;
		this.rpcTAT = rpcTAT;
		this.loanAmount = loanAmount;
		this.applicationStatus = applicationStatus;
		this.isRework = isRework;
		this.productCode = productCode;
	}

	public ApplicationMaster(String applicationId, String memberId, String customerName, String branchName,
							 String rpcTAT, BigDecimal loanAmount, String applicationStatus, String isRework, String productCode, String kendraId) {
		this.applicationId = applicationId;
		this.memberId = memberId;
		this.customerName = customerName;
		this.branchName = branchName;
		this.rpcTAT = rpcTAT;
		this.loanAmount = loanAmount;
		this.applicationStatus = applicationStatus;
		this.isRework = isRework;
		this.productCode = productCode;
		this.kendraId = kendraId;
	}

	public ApplicationMaster(String applicationId, String memberId, String customerName, String branchName,
							 String rpcTAT, int loanAmount, String applicationStatus, String isRework, String productCode, String kendraId) {
		this(applicationId, memberId, customerName, branchName,
				rpcTAT, BigDecimal.valueOf(loanAmount), applicationStatus, isRework, productCode, kendraId);
	}



	public ApplicationMaster(String appId, String applicationId, int versionNum, LocalDate applicationDate,
			LocalDateTime createTs, String createdBy, String applicationType, String kycType, String applicationStatus,
			BigDecimal customerId, String mobileNumber, String nationalId, String pan, String productGroupCode,
			String productCode, String searchCode1, String searchCode2, String assignedTo, String emailId,
			String currentStage, String declarationFlag, String accNumber, String mobileVerStatus,
			String emailVerStatus, String currentScreenId, String remarks, String relatedApplicationId) {
		this.appId = appId;
		this.applicationId = applicationId;
		this.versionNum = versionNum;
		this.applicationDate = applicationDate;
		this.createTs = createTs;
		this.createdBy = createdBy;
		this.applicationType = applicationType;
		this.kycType = kycType;
		this.applicationStatus = applicationStatus;
		this.customerId = customerId;
		this.mobileNumber = mobileNumber;
		this.nationalId = nationalId;
		this.pan = pan;
		this.productGroupCode = productGroupCode;
		this.productCode = productCode;
		this.searchCode1 = searchCode1;
		this.searchCode2 = searchCode2;
		this.assignedTo = assignedTo;
		this.emailId = emailId;
		this.currentStage = currentStage;
		this.declarationFlag = declarationFlag;
		this.accNumber = accNumber;
		this.mobileVerStatus = mobileVerStatus;
		this.emailVerStatus = emailVerStatus;
		this.currentScreenId = currentScreenId;
		this.remarks = remarks;
		this.relatedApplicationId = relatedApplicationId;
	}

	public ApplicationMaster(String applicationId, int versionNum, String applicationStatus, LocalDateTime createTs,
			String createdBy, String mobileNumber, LocalDate applicationDate, String kycType, String pan,
			String nationalId, String productCode, BigDecimal customerId, String emailId, String searchCode1,
			String accNumber, String productGroupCode, String appId, String wfStatus, String wfCreatedBy,
			LocalDateTime wfCreateTs, Integer applicantsCount, String applicationType, String workitemNo,
			String memberId) { // called from repository.
		this.applicationId = applicationId;
		this.versionNum = versionNum;
		this.applicationStatus = applicationStatus;
		this.createTs = createTs;
		this.createdBy = createdBy;
		this.mobileNumber = mobileNumber;
		this.applicationDate = applicationDate;
		this.kycType = kycType;
		this.pan = pan;
		this.nationalId = nationalId;
		this.productCode = productCode;
		this.customerId = customerId;
		this.emailId = emailId;
		this.searchCode1 = searchCode1;
		this.accNumber = accNumber;
		this.productGroupCode = productGroupCode;
		this.appId = appId;
		this.wfStatus = wfStatus;
		this.wfCreatedBy = wfCreatedBy;
		this.wfCreateTs = wfCreateTs;
		this.applicantsCount = applicantsCount;
		this.applicationType = applicationType;
		this.workitemNo = workitemNo;
		this.memberId = memberId;
	}
	
	public ApplicationMaster(String applicationId, String createdBy, LocalDate applicationDate, String kendraId,
			String kendraName, String workitemNo, String memberId, String customerName, Integer currentStageNo,
			String caglOs, String kendraMeetingDay, String kendraVintageYrs, String applicationStatus) {
		this.applicationId = applicationId;
		this.createdBy = createdBy;
		this.applicationDate = applicationDate;
		this.kendraId = kendraId;
		this.kendraName = kendraName;
		this.workitemNo = workitemNo;
		this.memberId = memberId;
		this.currentStageNo = currentStageNo;
		this.customerName = customerName;
		this.caglOs = caglOs;
		this.kendraMeetingDay = kendraMeetingDay;
		this.kendraVintageYrs = kendraVintageYrs;
		this.applicationStatus = applicationStatus;
	}
	
	public ApplicationMaster(String applicationId, String createdBy, LocalDate applicationDate, String kendraId,
			String kendraName, String workitemNo, String memberId, String customerName, Integer currentStageNo,
			String caglOs, String kendraMeetingDay, String kendraVintageYrs, String applicationStatus, String productCode) {
		this.applicationId = applicationId;
		this.createdBy = createdBy;
		this.applicationDate = applicationDate;
		this.kendraId = kendraId;
		this.kendraName = kendraName;
		this.workitemNo = workitemNo;
		this.memberId = memberId;
		this.currentStageNo = currentStageNo;
		this.customerName = customerName;
		this.caglOs = caglOs;
		this.kendraMeetingDay = kendraMeetingDay;
		this.kendraVintageYrs = kendraVintageYrs;
		this.applicationStatus = applicationStatus;
		this.productCode = productCode;
	}

	public ApplicationMaster(String applicationId, int versionNum, String applicationStatus, LocalDateTime createTs,
			String createdBy, String mobileNumber, LocalDate applicationDate, String kycType, String pan,
			String nationalId, String productCode, BigDecimal customerId, String emailId, String searchCode1,
			String accNumber, String productGroupCode, String appId, String wfStatus, String wfCreatedBy,
			LocalDateTime wfCreateTs, Integer applicantsCount, String applicationType, String workitemNo,
			String memberId, String customerName, String updatedBy, String isRework, String branchName,
			BigDecimal loanAmount) { // called from repository.
		this.applicationId = applicationId;
		this.versionNum = versionNum;
		this.applicationStatus = applicationStatus;
		this.createTs = createTs;
		this.createdBy = createdBy;
		this.mobileNumber = mobileNumber;
		this.applicationDate = applicationDate;
		this.kycType = kycType;
		this.pan = pan;
		this.nationalId = nationalId;
		this.productCode = productCode;
		this.customerId = customerId;
		this.emailId = emailId;
		this.searchCode1 = searchCode1;
		this.accNumber = accNumber;
		this.productGroupCode = productGroupCode;
		this.appId = appId;
		this.wfStatus = wfStatus;
		this.wfCreatedBy = wfCreatedBy;
		this.wfCreateTs = wfCreateTs;
		this.applicantsCount = applicantsCount;
		this.applicationType = applicationType;
		this.workitemNo = workitemNo;
		this.memberId = memberId;
		this.customerName = customerName;
		this.updatedBy = updatedBy;
		this.isRework = isRework;
		this.branchName = branchName;
		this.loanAmount = loanAmount;
	}

	public ApplicationMaster(String applicationId, int versionNum, String applicationStatus, LocalDateTime createTs,
			String createdBy, String mobileNumber, LocalDate applicationDate, String kycType, String pan,
			String nationalId, String productCode, BigDecimal customerId, String emailId, String searchCode1,
			String accNumber, String productGroupCode, String appId, String wfStatus, String wfCreatedBy,
			LocalDateTime wfCreateTs, Integer applicantsCount, String applicationType, String workitemNo,
			String memberId, String customerName, String updatedBy, String isRework, String branchName) { // called from
																											// repository.
		this.applicationId = applicationId;
		this.versionNum = versionNum;
		this.applicationStatus = applicationStatus;
		this.createTs = createTs;
		this.createdBy = createdBy;
		this.mobileNumber = mobileNumber;
		this.applicationDate = applicationDate;
		this.kycType = kycType;
		this.pan = pan;
		this.nationalId = nationalId;
		this.productCode = productCode;
		this.customerId = customerId;
		this.emailId = emailId;
		this.searchCode1 = searchCode1;
		this.accNumber = accNumber;
		this.productGroupCode = productGroupCode;
		this.appId = appId;
		this.wfStatus = wfStatus;
		this.wfCreatedBy = wfCreatedBy;
		this.wfCreateTs = wfCreateTs;
		this.applicantsCount = applicantsCount;
		this.applicationType = applicationType;
		this.workitemNo = workitemNo;
		this.memberId = memberId;
		this.customerName = customerName;
		this.updatedBy = updatedBy;
		this.isRework = isRework;
		this.branchName = branchName;
	}

	public ApplicationMaster(int versionNum, String applicationId, String applicationStatus, LocalDateTime createTs,
			String createdBy, String productCode, String productGroupCode, String applicationType, String mobileNumber,
			String appId) {
		this.applicationId = applicationId;
		this.versionNum = versionNum;
		this.applicationStatus = applicationStatus;
		this.createTs = createTs;
		this.createdBy = createdBy;
		this.productCode = productCode;
		this.productGroupCode = productGroupCode;
		this.applicationType = applicationType;
		this.mobileNumber = mobileNumber;
		this.appId = appId;
	}

	public ApplicationMaster(String applicationStatus, String applicationId, String wfStatus, String wfCreatedBy) {
		this.applicationStatus = applicationStatus;
		this.wfStatus = wfStatus;
		this.applicationId = applicationId;
		this.wfCreatedBy = wfCreatedBy;
	}

	public ApplicationMaster(String applicationStatus, Long count) {
		this.applicationStatus = applicationStatus;
		this.statusCount = count;
	}

	public ApplicationMaster(Long productCount, String productGroupCode) {
		this.productGroupCode = productGroupCode;
		this.productCount = productCount;
	}

	public ApplicationMaster(String appId, String applicationId, Integer versionNum, String applicationStatus,
			String relatedApplicationId) {
		this.appId = appId;
		this.applicationId = applicationId;
		this.versionNum = versionNum;
		this.applicationStatus = applicationStatus;
		this.relatedApplicationId = relatedApplicationId;
	}

	public ApplicationMaster(String workitemNo, String branchId, String branchName, String relatedApplicationId,
			String kendraId, String kendraName, String createdBy, String applicationId, BigDecimal custDtlId,
			String customerName, BigDecimal coAppCustDtlId, String coAppCustomerName, String payloadColumn,
			LocalDate applicationDate, String applicationStatus, LocalDateTime updateTs, BigDecimal loanAmount,
			BigDecimal applicantCBDtlid, BigDecimal coApplicantCBDtlid) {
		this.workitemNo = workitemNo;
		this.branchId = branchId;
		this.branchName = branchName;
		this.relatedApplicationId = relatedApplicationId;
		this.kendraId = kendraId;
		this.kendraName = kendraName;
		this.createdBy = createdBy;
		this.applicationId = applicationId;
		this.custDtlId = custDtlId;
		this.customerName = customerName;
		this.coAppCustDtlId = coAppCustDtlId;
		this.coAppCustomerName = coAppCustomerName;
		this.payloadColumn = payloadColumn;
		this.applicationDate = applicationDate;
		this.applicationStatus = applicationStatus;
		this.updateTs = updateTs;
		this.loanAmount = loanAmount;
		this.applicantCBDtlid = applicantCBDtlid;
		this.coApplicantCBDtlid = coApplicantCBDtlid;
	}

	public ApplicationMaster() {
	}

	@Override
	public String toString() {
		return "ApplicationMaster [appId=" + appId + ", applicationId=" + applicationId + ", versionNum=" + versionNum
				+ ", applicationDate=" + applicationDate + ", createTs=" + createTs + ", updateTs=" + updateTs
				+ ", updatedBy=" + updatedBy + ", createdBy=" + createdBy + ", applicationType=" + applicationType
				+ ", kycType=" + kycType + ", applicationStatus=" + applicationStatus + ", customerId=" + customerId
				+ ", mobileNumber=" + mobileNumber + ", nationalId=" + nationalId + ", pan=" + pan
				+ ", productGroupCode=" + productGroupCode + ", productCode=" + productCode + ", searchCode1="
				+ searchCode1 + ", searchCode2=" + searchCode2 + ", assignedTo=" + assignedTo + ", emailId=" + emailId
				+ ", currentStage=" + currentStage + ", declarationFlag=" + declarationFlag + ", accNumber=" + accNumber
				+ ", mobileVerStatus=" + mobileVerStatus + ", emailVerStatus=" + emailVerStatus + ", currentScreenId="
				+ currentScreenId + ", remarks=" + remarks + ", applicantsCount=" + applicantsCount
				+ ", relatedApplicationId=" + relatedApplicationId + ", kendraId=" + kendraId + ", kendraName="
				+ kendraName + ", branchId=" + branchId + ", branchName=" + branchName + ", memberId=" + memberId
				+ ", primaryKycType=" + primaryKycType + ", primaryKycId=" + primaryKycId + ", secondaryKycType="
				+ secondaryKycType + ", secondaryKycId=" + secondaryKycId + ", workitemNo=" + workitemNo
				+ ", currentStageNo=" + currentStageNo + ", alternateVoterId=" + alternateVoterId
				+ ", mainProductGroupCode=" + mainProductGroupCode + ", custDtlId=" + custDtlId + ", coAppCustDtlId="
				+ coAppCustDtlId + ", coAppCustomerName=" + coAppCustomerName + ", custDtlSlNum=" + custDtlSlNum
				+ ", queueStatus=" + queueStatus + ", applicationWorkflow=" + applicationWorkflow + ", wfStatus="
				+ wfStatus + ", wfCreatedBy=" + wfCreatedBy + ", wfCreateTs=" + wfCreateTs + ", statusCount="
				+ statusCount + ", productCount=" + productCount + ", isRd=" + isRd + ", customerDetails="
				+ customerDetails + ", cibilDetails=" + cibilDetails + ", customerName=" + customerName + ", cbStatus="
				+ cbStatus + ", applicantCBExpiry=" + applicantCBExpiry + ", coApplicantCBExpiry=" + coApplicantCBExpiry
				+ ", caglOs=" + caglOs + ", kendraMeetingDay=" + kendraMeetingDay + ", kendraVintageYrs="
				+ kendraVintageYrs + ", payloadColumn=" + payloadColumn + ", loanAmount=" + loanAmount
				+ ", applicantCBDtlid=" + applicantCBDtlid + ", coApplicantCBDtlid=" + coApplicantCBDtlid + "]";
	}

}
