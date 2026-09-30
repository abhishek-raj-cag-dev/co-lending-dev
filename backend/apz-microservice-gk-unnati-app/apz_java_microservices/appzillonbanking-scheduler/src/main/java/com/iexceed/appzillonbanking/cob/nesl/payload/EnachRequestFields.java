package com.iexceed.appzillonbanking.cob.nesl.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnachRequestFields {

	@ApiModelProperty(required = true,position = 1, example = "1789879879797")
	@JsonProperty("applicationId")
	private String applicationId;

	@ApiModelProperty(required = false, position = 2, example = "SBIN0007911")
	@JsonProperty("accountNumber")
	private String accountNumber;

	@ApiModelProperty(required = false, position = 3, example = "Applicant")
	@JsonProperty("applicantType")
	private String applicantType;

	@ApiModelProperty(required = false, position = 4, example = "9480-N")
	@JsonProperty("additionalField2")
	private String additionalField2;

	@ApiModelProperty(required = false, position = 5)
	@JsonProperty("paynimoRequestId")
	private String paynimoRequestId;	
	
	@ApiModelProperty(required = false, position = 6)
	@JsonProperty("enachType")
	private String enachType;

}