package com.iexceed.appzillonbanking.cob.loans.payload;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscardCoApplicantRequestFields {

	@ApiModelProperty(required = true, example = "APZDEP")
	@JsonProperty("appId")
	private String appId;

	@ApiModelProperty(required = true, position = 2, example = "NEW00063")
	@JsonProperty("applicationId")
	private String applicationId;

	@ApiModelProperty(required = true, position = 3, example = "1")
	@JsonProperty("versionNum")
	private int versionNum;

	@ApiModelProperty(required = true, position = 4, example = "10000000")
	@JsonProperty("custDtlId")
	private BigDecimal custDtlId;

	@ApiModelProperty(required = false, position = 5, example = "sfda")
	@JsonProperty("reason")
	private String reason;

	@ApiModelProperty(required = false, position = 6, example = "sdfaasfd")
	@JsonProperty("remarks")
	private String remarks;

}