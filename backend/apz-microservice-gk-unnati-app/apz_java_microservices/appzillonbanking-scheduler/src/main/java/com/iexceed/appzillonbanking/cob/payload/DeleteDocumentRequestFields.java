package com.iexceed.appzillonbanking.cob.payload;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class DeleteDocumentRequestFields {
	
	@ApiModelProperty(required = true, position = 1, example = "16190967474451390")
	@JsonProperty("appDocId")
	private BigDecimal appDocId;
	
	@ApiModelProperty(required = true, position = 2, example = "1")
	@JsonProperty("versionNum")
	private int versionNum;
	
	@ApiModelProperty(required = true, position = 3, example = "16185889475433589")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, position = 4, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, position = 5, example = "/APZCOB/")
	@JsonProperty("filePath")
	private String filePath;	
	
	@ApiModelProperty(required = true, position = 6, example = "id.png")
	@JsonProperty("fileName")
	private String fileName;	

	@ApiModelProperty(required = true, position = 7, example = "yes")
	@JsonProperty("bulkDelete")
	private String bulkDelete;

	@ApiModelProperty(required = true, position = 8, example = "otherDocs")
	@JsonProperty("documentType")
	private String documentType;

	@ApiModelProperty(required = true, position = 9, example = "otherDocs")
	@JsonProperty("customerType")
	private String customerType;

	@JsonProperty("docNo")
	private String docNo;

	@JsonProperty("incomeType")
	private String incomeType;

	@JsonProperty("botTrigger")
	private String botTrigger;

}
