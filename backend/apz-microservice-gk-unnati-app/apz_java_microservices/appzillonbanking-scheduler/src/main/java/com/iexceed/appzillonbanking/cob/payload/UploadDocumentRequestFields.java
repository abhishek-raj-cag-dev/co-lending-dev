package com.iexceed.appzillonbanking.cob.payload;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class UploadDocumentRequestFields {
	
	
	@ApiModelProperty(required = true, position = 1, example = "dfsdsgsfg")
	@JsonProperty("base64Value")
	private String base64Value;

	@ApiModelProperty(required = true, position = 2, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, position = 3, example = "/APZCOB/")
	@JsonProperty("filePath")
	private String filePath;
	
	@ApiModelProperty(required = true, position = 4, example = "Aadhaar")
	@JsonProperty("fileName")
	private String fileName;
	
	@ApiModelProperty(required = true, position = 5, example = "16185889475433589")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, position = 6, example = "1")
	@JsonProperty("versionNum")
	private int versionNum;
	
	@ApiModelProperty(required = true, position = 7, example = "KYC")
	@JsonProperty("srcScreen")
	private String srcScreen;
	
	@ApiModelProperty(required = true, position = 8, example = "CASA")
	@JsonProperty("productGroupCode")
	private String productGroupCode;
	
	@ApiModelProperty(required = true, position = 9, example = "N")
	@JsonProperty("isExistingCustomer")
	private String isExistingCustomer;
	
	@ApiModelProperty(required = false, position = 10, example = "")
	@JsonProperty("documentId")
	private BigDecimal documentId;
	
	@ApiModelProperty(required = false, position = 11, example = "")
	@JsonProperty("documentLanguage")
	private String documentLanguage;
	
	@ApiModelProperty(required = false, position = 12, example = "")
	@JsonProperty("documentGenerationType")
	private String documentGenerationType;

	@ApiModelProperty(required = false, position = 13, example = "")
	@JsonProperty("documentType")
	private String documentType;

	@ApiModelProperty(required = false, position = 14, example = "")
	@JsonProperty("userId")
	private String userId;

	@ApiModelProperty(required = false, position = 15, example = "")
	@JsonProperty("mergeDocument")
	private String mergeDocument;

	@ApiModelProperty(required = false, position = 16, example = "")
	@JsonProperty("docSize")
	private String docSize;

	@ApiModelProperty(required = false)
	@JsonProperty("isReupload")
	private String isReupload;
	
	@ApiModelProperty(required = false)
	@JsonProperty("fileList")
	List<JsonObject> fileList;
}
