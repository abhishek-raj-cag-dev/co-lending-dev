package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdharRedactOcrRequest {
	@Override
	public String toString() {
		return "AdharRedactOcrRequest [interfaceName=" + interfaceName + ", appId=" + appId + ", requestObj=" + requestObj + "]";
	}

	@ApiModelProperty(required = true, position = 1, example = "validateKyc")
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@ApiModelProperty(required = true, position = 2, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;

	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("custDtlId")
	private BigDecimal custDtlId;

	@JsonProperty("customerId")
	private String customerId;

	@JsonProperty("docType")
	private String docType;

	@JsonProperty("docSide")
	private String docSide;

	@JsonProperty("versionNum")
	private int versionNum;

	@JsonProperty("createdBy")
	private String createdBy;
	
	@JsonProperty("requestObj")
	private AdharUploadRequest requestObj;

}
