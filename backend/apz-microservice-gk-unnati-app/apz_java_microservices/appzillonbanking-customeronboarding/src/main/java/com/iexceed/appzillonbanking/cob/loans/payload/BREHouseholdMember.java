package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BREHouseholdMember {

	@Override
	public String toString() {
		return "BREHouseholdMember [applicantType=" + applicantType + ", addressDetails=" + addressDetails
				+ ", documentDetails=" + documentDetails + ", relationtype=" + relationtype + ", custName=" + custName
				+ ", gender=" + gender + ", phone=" + phone + ", dob=" + dob + ", earningFlag=" + earningFlag + "]";
	}

	@ApiModelProperty(required = false, example = "H")
	@JsonProperty("applicantType")
	private String applicantType;

	@JsonProperty("address")
	private List<BREAddressDetails> addressDetails;

	@JsonProperty("document")
	private List<BREDocumentDetails> documentDetails;

	@ApiModelProperty(required = false, example = "Husband")
	@JsonProperty("relationtype")
	private String relationtype;

	@ApiModelProperty(required = false, example = "KASHIBAI SIDARAY KUMBAR")
	@JsonProperty("custName")
	private String custName;

	@ApiModelProperty(required = false, example = "2")
	@JsonProperty("gender")
	private String gender;

	@ApiModelProperty(required = false, example = "6559537791")
	@JsonProperty("phone")
	private String phone;

	@ApiModelProperty(required = false, example = "24838")
	@JsonProperty("dob")
	private String dob;

	@ApiModelProperty(required = false, example = "ABC")
	@JsonProperty("earning_flag")
	private String earningFlag;
}
