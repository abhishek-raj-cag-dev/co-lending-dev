package com.iexceed.appzillonbanking.cob.core.payload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TATReportPayload {

	@JsonProperty("pid")
	private String pid;

	@JsonProperty("branchId")
	private String branchId;

	@JsonProperty("branchName")
	private String branchName;

	@JsonProperty("areaName")
	private String areaName;

	@JsonProperty("regionName")
	private String regionName;

	@JsonProperty("zone")
	private String zone;

	@JsonProperty("stateName")
	private String stateName;

	@JsonProperty("meetingDay")
	private String meetingDay;

	@JsonProperty("product")
	private String product;

	@JsonProperty("kendraId")
	private String kendraId;

	@JsonProperty("kendraName")
	private String kendraName;

	@JsonProperty("kmId")
	private String kmId;

	@JsonProperty("kmName")
	private String kmName;

	@JsonProperty("bmId")
	private String bmId;

	@JsonProperty("bmName")
	private String bmName;

	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("applicantId")
	private BigDecimal applicantId;

	@JsonProperty("applicantName")
	private String applicantName;

	@JsonProperty("coApplicantId")
	private BigDecimal coApplicantId;

	@JsonProperty("coApplicantName")
	private String coApplicantName;

	@JsonProperty("relationshipToApplicant")
	private String relationshipToApplicant;

	@JsonProperty("loanAmount")
	private BigDecimal loanAmount;

	@JsonProperty("applicantCBStatus")
	private String applicantCBStatus;

	@JsonProperty("coApplicantCBStatus")
	private String coApplicantCBStatus;

	@JsonProperty("sourcingDate")
	private LocalDateTime sourcingDate;

	@JsonProperty("leadAssignedDate")
	private LocalDate leadAssignedDate;

	@JsonProperty("status")
	private String status;

	@JsonProperty("rejectionDate")
	private LocalDateTime rejectionDate;

	@JsonProperty("lastUpdatedDate")
	private LocalDateTime lastUpdatedDate;

	@JsonProperty("timeTakenByKM")
	private String timeTakenByKM;

	@JsonProperty("timeTakenByBM")
	private String timeTakenByBM;

	@JsonProperty("totalTimeTakenBySourcing")
	private String totalTimeTakenBySourcing;

	@JsonProperty("RPCPushback")
	private LocalDateTime RPCPushback;

	@JsonProperty("RPCComments")
	private String RPCComments;

	@JsonProperty("timeTakenForRPCPushbackResolution")
	private String timeTakenForRPCPushbackResolution;

	@JsonProperty("KMSubmissionTime")
	private LocalDateTime KMSubmissionTime;

	@JsonProperty("BMSubmissionTime")
	private LocalDateTime BMSubmissionTime;

}