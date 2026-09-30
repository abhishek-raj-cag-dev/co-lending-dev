package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BRECBInputRequest2 {


	@ApiModelProperty(required = false, example = "0")
	@JsonProperty("roi_type")
	private int roiType;

	@ApiModelProperty(required = false, example = "1")
	@JsonProperty("Coapp_flag")
	private int coappFlag;

	@ApiModelProperty(required = false, example = "Yes")
	@JsonProperty("App_Insurance_flag")
	private String appInsuranceFlag;

	@ApiModelProperty(required = false, example = "Yes")
	@JsonProperty("Coapp_Insurance_flag")
	private String coappInsuranceFlag;

	@ApiModelProperty(required = false, example = "Yes")
	@JsonProperty("Joint_Insurance_flag")
	private String jointInsuranceFlag;

	@ApiModelProperty(required = false, example = "1000")
	@JsonProperty("App_Insurance_amt")
	private int appInsuranceAmt;

	@ApiModelProperty(required = false, example = "1000")
	@JsonProperty("Coapp_Insurance_amt")
	private int coappInsuranceAmt;

	@ApiModelProperty(required = false, example = "1000")
	@JsonProperty("Joint_Insurance_amt")
	private int jointInsuranceAmt;

	@JsonProperty("applicant")
	private BREApplicant applicant;

	@JsonProperty("household_member")
	private List<BREHouseholdMember> householdMember;

	@JsonProperty("business_image_assessment")
	private  BREBusinessImageAssessment businessImageAssessment;

	@JsonProperty("caglOs")
	private String caglOs;

	@JsonProperty("currentStage")
	private String currentStage;

}
