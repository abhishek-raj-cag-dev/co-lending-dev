package com.iexceed.appzillonbanking.cob.payload;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ExtractOcrDataRequesttFields {

	@ApiModelProperty(required = true, position = 1, example = "AADHAR_ID")
	@JsonProperty("documentType")
	private String documentType;
	
	@ApiModelProperty(required = true, position = 2, example = "em_back")
	@JsonProperty("userId")
	private String userId;
	
	@ApiModelProperty(required = true, position = 3, example = "INDIAN_Aadhar_Front")
	@JsonProperty("documentId")
	private String documentId;
	
	@ApiModelProperty(required = true, position = 4, example = "camera")
	@JsonProperty("dataSource")
	private String dataSource;
	
	@ApiModelProperty(required = true, position = 5, example = "BASE64")
	@JsonProperty("dataType")
	private String dataType;
	
	@ApiModelProperty(required = true, position = 6, example = "true")
	@JsonProperty("dataPreprocessed")
	private String dataPreprocessed;
	
	@ApiModelProperty(required = true, position = 7, example = "COLOR")
	@JsonProperty("preprocessedType")
	private String preprocessedType;
	
	@ApiModelProperty(required = true, position = 8, example = "COLOR")
	@JsonProperty("deviceType")
	private String deviceType;
	
	@ApiModelProperty(required = true, position = 9, example = " ")
	@JsonProperty("dataText")
	private String dataText;
	
	@ApiModelProperty(required = true, position = 10, example = " ")
	@JsonProperty("dataCord")
	private String dataCord;
	
	@ApiModelProperty(required = true, position = 11, example = " ")
	@JsonProperty("addtionalParam")
	private List<String> addtionalParam;
	
	@ApiModelProperty(required = true, position = 12, example = "Aa003")
	@JsonProperty("txnId")
	private String txnId;
	
	@ApiModelProperty(required = true, position = 13, example = "IMG_BASE64")
	@JsonProperty("dataFormat")
	private String dataFormat;
	
	@ApiModelProperty(required = true, position = 14, example = " ")
	@JsonProperty("dataBase64")
	private String dataBase64;
	
	@ApiModelProperty(required = true, position = 15, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, position = 16, example = "Aadhaar.JPG")
	@JsonProperty("fileName")
	private String fileName;

	@ApiModelProperty(required = true, position = 17, example = "/APZCOB/")
	@JsonProperty("filePath")
	private String filePath;
	
	@ApiModelProperty(required = true, position = 17, example = "front")
	@JsonProperty("docSide")
	private String docSide;
	
	@ApiModelProperty(required = true, position = 5, example = "16185889475433589")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, position = 6, example = "1")
	@JsonProperty("versionNum")
	private int versionNum;
}