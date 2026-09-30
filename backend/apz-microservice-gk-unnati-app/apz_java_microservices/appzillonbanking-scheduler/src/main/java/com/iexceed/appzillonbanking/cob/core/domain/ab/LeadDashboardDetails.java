package com.iexceed.appzillonbanking.cob.core.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Iexceed_Unnati_current_status")
@Getter
@Setter
public class LeadDashboardDetails {

	@Id
	@JsonProperty("pid")
	@Column(name = "PID")
	private String pid;

	@JsonProperty("currentStatus")
	@Column(name = "Current_status")
	private String currentStatus;

	@JsonProperty("kendraId")
	@Column(name = "Kendra_ID")
	private String kendraId;

	@JsonProperty("kendraName")
	@Column(name = "Kendra_name")
	private String kendraName;

	@JsonProperty("branchName")
	@Column(name = "Branch_name")
	private String branchName;

	@JsonProperty("branchId")
	@Column(name = "Branch_ID")
	private String branchId;

	@JsonProperty("caglOs")
	@Column(name = "Outstanding")
	private String caglOs;

	@JsonProperty("kendraMeetingDay")
	@Column(name = "Meeting_day")
	private String kendraMeetingDay;

	@JsonProperty("sourceOfLead")
	@Column(name = "Source_of_lead")
	private String sourceOfLead;

	@JsonProperty("productType")
	@Column(name = "Product_type")
	private String productType;

	@JsonProperty("memberId")
	@Column(name = "member_id")
	private String memberId;

	@JsonProperty("customerName")
	@Column(name = "Member_Name")
	private String customerName;

	@JsonProperty("kendraVintageYrs")
	@Column(name = "Vintage")
	private String kendraVintageYrs;

	@JsonProperty("createdBy")
	@Column(name = "KM_ID")
	private String createdBy;

	@Override
	public String toString() {
		return "LeadDashboardDetails [pid=" + pid + ", currentStatus=" + currentStatus + ", kendraId=" + kendraId
				+ ", kendraName=" + kendraName + ", branchName=" + branchName + ", branchId=" + branchId
				+ ", caglOs=" + caglOs + ", kendraMeetingDay=" + kendraMeetingDay + ", sourceOfLead="
				+ sourceOfLead + ", productType=" + productType + ", memberId=" + memberId + ", customerName="
				+ customerName + ", kendraVintageYrs=" + kendraVintageYrs + ", createdBy=" + createdBy + "]";
	}

}