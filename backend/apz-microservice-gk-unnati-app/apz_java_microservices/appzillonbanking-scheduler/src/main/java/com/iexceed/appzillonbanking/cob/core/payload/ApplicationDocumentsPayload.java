package com.iexceed.appzillonbanking.cob.core.payload;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ApplicationDocumentsPayload {

	@JsonProperty("docLevel")
	private String docLevel;
	
	@JsonProperty("documentType")
	private String documentType;
	
	@JsonProperty("documentName")
	private String documentName;
	
	@JsonProperty("documentDesc")
	private String documentDesc;
	
	@JsonProperty("documentLoc")
	private String documentLoc;
	
	@JsonProperty("screenId")
	private String screenId;
	
	@JsonProperty("issueDate")
	private String issueDate;
	
	@JsonProperty("expiryDate")
	private String expiryDate;
	
	@JsonProperty("docStatus")
	private String docStatus;

	@JsonProperty("docSide")
	private String docSide;
	
	@JsonProperty("documentFileName")
	private String documentFileName;
	
	@JsonProperty("documentFormat")
	private String documentFormat;
	
	@JsonProperty("nameNationalId")
	private String nameNationalId;
	
	@JsonProperty("mobileNationalId")
	private String mobileNationalId;
	
	@JsonProperty("addressNationalId")
	private String addressNationalId;
	
	@JsonProperty("dobNationalId")
	private String dobNationalId;
	
	@JsonProperty("genderNationalId")
	private String genderNationalId;
	
	@JsonProperty("nationalId")
	private String nationalId;
	
	@JsonProperty("nationalIdSecondary")
	private String nationalIdSecondary;
	
	@JsonProperty("nationality")
	private String nationality;		
	
	@JsonProperty("docCategory")
	private String docCategory;
	
	@JsonProperty("isPushbackUpload")
	private String isPushbackUpload;
	
	@JsonProperty("isAdiDoc")
	private String isAdiDoc;
	
	@JsonProperty("uploadedBy")
	private String uploadedBy;

	@JsonProperty("docSize")
	private BigDecimal docSize;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("custType")
	private String custType;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("stage")
	private String stage;

	@JsonProperty("docNo")
	private String docNo;

	@JsonProperty("incomeType")
	private String incomeType;

	@JsonProperty("isReupload")
	private String isReupload;
	
	@JsonProperty("latitude")
	private String latitude;
	
	@JsonProperty("longitude")
	private String longitude;
	
	@JsonProperty("uploadedTs")
	private String uploadedTs;

	@JsonProperty("botTrigger")
	private String botTrigger="N";

	@JsonProperty("botBussinessType")
	private String botBussinessType = "";
}
