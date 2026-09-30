package com.iexceed.appzillonbanking.cob.loans.domain.user;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_kendra_data")
@Getter@Setter
public class KendraDetails {

	@Id
	@JsonProperty("kendraId")
	@Column(name = "KENDRA_ID")
	private String kendraId;

	@JsonProperty("kendraName")
	@Column(name = "KENDRA_NAME")
	private String kendraName;

	@JsonProperty("kmName")
	@Column(name = "KM_NAME")
	private String kmName;

	@JsonProperty("branchId")
	@Column(name = "BRANCH_ID")
	private String branchId;

	@JsonProperty("villageType")
	@Column(name = "VILLAGE_TYPE")
	private String villageType;

	@JsonProperty("kendraAddr1")
	@Column(name = "KENDRA_ADDR1")
	private String kendraAddr1;

	@JsonProperty("kendraAddr2")
	@Column(name = "KENDRA_ADDR2")
	private String kendraAddr2;

	@JsonProperty("kendraAddr3")
	@Column(name = "KENDRA_ADDR3")
	private String kendraAddr3;

	@JsonProperty("kendraAddr4")
	@Column(name = "KENDRA_ADDR4")
	private String kendraAddr4;

	@JsonProperty("state")
	@Column(name = "STATE")
	private String state;

	@JsonProperty("district")
	@Column(name = "DISTRICT")
	private String district;

	@JsonProperty("taluk")
	@Column(name = "TALUK")
	private String taluk;

	@JsonProperty("areaType")
	@Column(name = "AREA_TYPE")
	private String areaType;

	@JsonProperty("village")
	@Column(name = "VILLAGE")
	private String village;

	@JsonProperty("pincode")
	@Column(name = "PINCODE")
	private Integer pincode;

	@JsonProperty("meetingFreq")
	@Column(name = "MEETING_FREQ")
	private String meetingFreq;

	@JsonProperty("firstMeetingDate")
	@Column(name = "FIRST_MEETING_DATE")
	private LocalDate firstMeetingDate;

	@JsonProperty("nextMeetingDate")
	@Column(name = "NEXT_MEETING_DATE")
	private LocalDate nextMeetingDate;

	@JsonProperty("meetingDay")
	@Column(name = "MEETING_DAY")
	private String meetingDay;

	@JsonProperty("meetingPlace")
	@Column(name = "MEETING_PLACE")
	private String meetingPlace;

	@JsonProperty("startingTime")
	@Column(name = "STARTING_TIME")
	private String startingTime;

	@JsonProperty("endingTime")
	@Column(name = "ENDING_TIME")
	private String endingTime;

	@JsonProperty("distance")
	@Column(name = "DISTANCE")
	private BigDecimal distance;

	@JsonProperty("leader")
	@Column(name = "LEADER")
	private String leader;

	@JsonProperty("secretary")
	@Column(name = "SECRETARY")
	private String secretary;

	@JsonProperty("createdBy")
	@Column(name = "CREATED_BY")
	private String createdBy;

	@JsonProperty("createdTs")
	@Column(name = "CREATED_TS")
	private LocalDateTime createdTs;

	@JsonProperty("updatedBy")
	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@JsonProperty("kendraStatus")
	@Column(name = "KENDRA_STATUS")
	private String kendraStatus;

	@JsonProperty("activationDate")
	@Column(name = "ACTIVATION_DATE")
	private String activationDate;

	@JsonProperty("handledBy")
	@Column(name = "HANDLED_BY")
	private String handledBy;

	@JsonProperty("t24Id")
	@Column(name = "T24_ID")
	private String t24Id;
}