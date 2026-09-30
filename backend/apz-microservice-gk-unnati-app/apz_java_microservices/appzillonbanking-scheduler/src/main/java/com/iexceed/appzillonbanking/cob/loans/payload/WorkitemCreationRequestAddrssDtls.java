package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WorkitemCreationRequestAddrssDtls {
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerType")
	private String customerType;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("nameOfOrgEmployer")
	private String nameOfOrgEmployer;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("addressType")
	private String addressType;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("locationCoOrdinates")
	private String locationCoOrdinates;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("line1")
	private String line1;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("line2")
	private String line2;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("line3")
	private String line3;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("landmark")
	private String landmark;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("pincode")
	private String pincode;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("area")
	private String area;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("cityTownVillage")
	private String cityTownVillage;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("district")
	private String district;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("state")
	private String state;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("country")
	private String country;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("currentResidenceProof")
	private String currentResidenceProof;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("communicationAddress")
	private String communicationAddress;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("residenceOwnership")
	private String residenceOwnership;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("resiStabiInPrsntAddress")
	private String resiStabiInPrsntAddress;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("resiStabiInPrsntCity")
	private String resiStabiInPrsntCity;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("typeOfHouse")
	private String typeOfHouse;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("areaOfHouse")
	private String areaOfHouse;
}
