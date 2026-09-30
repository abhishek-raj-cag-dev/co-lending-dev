package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MergeImageToPdfRequestWrapper {

	@ApiModelProperty(required = true, position = 1, example = "4564755645645")
	@JsonProperty("applicationId")
	private String applicationId;

	@ApiModelProperty(required = true, position = 2, example = "Applicant")
	@JsonProperty("applicantType")
	private String applicantType;

	@ApiModelProperty(required = true, position = 3, example = "Voter id")
	@JsonProperty("documentType")
	private String documentType;

}
