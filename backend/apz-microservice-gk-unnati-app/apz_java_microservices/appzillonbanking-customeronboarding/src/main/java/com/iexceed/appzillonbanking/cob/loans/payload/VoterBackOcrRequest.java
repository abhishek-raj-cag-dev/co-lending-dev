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
public class VoterBackOcrRequest {
	@Override
	public String toString() {
		return "VoterBackOcrRequest [interfaceName=" + interfaceName + ", appId=" + appId + ", requestObj="
				+ requestObj + "]";
	}

	@ApiModelProperty(required = true, position = 1, example = "SignzyPennylessCheck")
	@JsonProperty("interfaceName")
	private String interfaceName;

	@ApiModelProperty(required = true, position = 2, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, example = "NEW00063")
	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("custDtlId")
	private BigDecimal custDtlId;

	@JsonProperty("createdBy")
	private String createdBy;

	@JsonProperty("requestObj")
	private VoterBackOcrRequestFields requestObj;

}
