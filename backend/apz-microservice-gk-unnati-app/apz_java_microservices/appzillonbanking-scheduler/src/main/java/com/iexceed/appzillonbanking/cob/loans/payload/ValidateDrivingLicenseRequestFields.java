package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ValidateDrivingLicenseRequestFields {

	@ApiModelProperty(required = true, position = 1, example = "876876876868768")
	@JsonProperty("client_ref_num")
	private String reqRefNo;
	
	@ApiModelProperty(required = true, example = "XCX2872851")
	@JsonProperty("dl_number")
	private String kycId;
	
	@ApiModelProperty(required = true, example = "dd/mm/yyyy")
	@JsonProperty("dob")
	private String dob;
}