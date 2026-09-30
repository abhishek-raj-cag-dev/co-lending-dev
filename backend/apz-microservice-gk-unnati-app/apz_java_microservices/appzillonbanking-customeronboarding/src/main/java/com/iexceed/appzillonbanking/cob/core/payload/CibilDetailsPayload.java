package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CibilDetailsPayload {

	@JsonProperty("bureauName")
	private String bureauName;

	@JsonProperty("hitNohit")
	private String hitNohit;

	@JsonProperty("cbScore")
	private String cbScore;

	@JsonProperty("totIndebtness")
	private String totIndebtness;

	@JsonProperty("NoParInLastMonths")
	private String NoParInLastMonths;

	@JsonProperty("maxDpdInLastMonths")
	private String maxDpdInLastMonths;

	@JsonProperty("writtenOff")
	private String writtenOff;

	@JsonProperty("overlapWithMmfl")
	private String overlapWithMmfl;

	@JsonProperty("overdueAmt")
	private String overdueAmt;

	@JsonProperty("writeOffAmt")
	private String writeOffAmt;

	@JsonProperty("eligibleAmt")
	private String eligibleAmt;

	@JsonProperty("cbReport")
	private String cbReport;

	@JsonProperty("consentType")
	private String consentType;

	@JsonProperty("cgpDpd")
	private String cgpDpd;

	@JsonProperty("foir")
	private String foir;

	@JsonProperty("finalDecision")
	private String finalDecision;

	@JsonProperty("cbLoanId")
	private String cbLoanId;

	// A
	@JsonProperty("writeoffSuitFiledFlag")
	private String writeoffSuitFiledFlag;

	@JsonProperty("appliedLoanCode")
	private String appliedLoanCode;

	@JsonProperty("otsFlag")
	private String otsFlag;

	@JsonProperty("approvedLoanEMI")
	private String approvedLoanEMI;

	@JsonProperty("finalTenure")
	private String finalTenure;

	@JsonProperty("eligibleEMI")
	private String eligibleEMI;

	@JsonProperty("caglDpdFlag")
	private String caglDpdFlag;

	@JsonProperty("caglUnnatiFlag")
	private String caglUnnatiFlag;

	@JsonProperty("roi")
	private String roi;

	@JsonProperty("eir")
	private String eir;

	@JsonProperty("irisMessage")
	private String irisMessage;

	@JsonProperty("rejectionReason")
	private String rejectionReason;

	@JsonProperty("flowResponse")
	private String flowResponse;

	@JsonProperty("cbRetrigger")
	private boolean cbRetrigger;
	
	@JsonProperty("memberId")
	private String memberId;

	@JsonProperty("kycId")
	private String kycId;
	
	@JsonProperty("individualIndebtness")
	private String individualIndebtness;
	
	@JsonProperty("processingFees")
	private String processingFees;

	@JsonProperty("processingFeesWithoutGST")
	private String processingFeesWithoutGST;

	@JsonProperty("gstOnProcessingFees")
	private String gstOnProcessingFees;

	@JsonProperty("insuranceChargeMember")
	private String insuranceChargeMember;

	@JsonProperty("insuranceChargeSpouse")
	private String insuranceChargeSpouse;

	@JsonProperty("insuranceChargeJoint")
	private String insuranceChargeJoint;

	@JsonProperty("stampDutyCharge")
	private String stampDutyCharge;

	@JsonProperty("repaymentFrequency")
	private String repaymentFrequency;

	@JsonProperty("foirPercentage")
	private String foirPercentage;

	@JsonProperty("appIndebtednessLimit")
	private String appIndebtednessLimit;
	
	@JsonProperty("coappIndebtednessLimit")
	private String coappIndebtednessLimit;
	
	@JsonProperty("appMaxLoanLimit")
	private String appMaxLoanLimit;
	
	@JsonProperty("coappMaxLoanLimit")
	private String coappMaxLoanLimit;

	@JsonProperty("applicantIndebtedness")
	private String applicantIndebtedness;

	@JsonProperty("coApplicantIndebtedness")
	private String coApplicantIndebtedness;
	
    @JsonProperty("retryAttempts")
    private int retryAttempts;
	
	@JsonProperty("isReinitiated")
	private String isReinitiated;

	@JsonProperty("visheshEligibility")
	private String visheshEligibility;

	@JsonProperty("visheshIndebtEligibleAmount")
	private String visheshIndebtEligibleAmount;

	@JsonProperty("unnatiTentativeEligibility")
	private String unnatiTentativeEligibility;

	@JsonProperty("unnatiTentativeEligibleAmount")
	private String unnatiTentativeEligibleAmount;

	@JsonProperty("unnatiLiteTentativeEligibility")
	private String unnatiLiteTentativeEligibility;

	@JsonProperty("unnatiLiteTentativeEligibleAmount")
	private String unnatiLiteTentativeEligibleAmount;
	
}
