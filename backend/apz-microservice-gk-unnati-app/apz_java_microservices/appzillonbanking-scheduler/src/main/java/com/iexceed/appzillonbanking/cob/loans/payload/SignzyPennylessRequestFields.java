package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignzyPennylessRequestFields {

	@Override
	public String toString() {
		return "SignzyPennylessRequestFields [beneficiaryAccount=" + beneficiaryAccount + ", beneficiaryIFSC="
				+ beneficiaryIFSC + ", beneficiaryMobile=" + beneficiaryMobile + ", beneficiaryName=" + beneficiaryName
				+ ", nameFuzzy=" + nameFuzzy + "]";
	}

	@ApiModelProperty(required = true, position = 1, example = "34565101583")
	@JsonProperty("beneficiaryAccount")
	private String beneficiaryAccount;

	@ApiModelProperty(required = true, position = 2, example = "SBIN0007911")
	@JsonProperty("beneficiaryIFSC")
	private String beneficiaryIFSC;

	@ApiModelProperty(required = true, position = 3, example = "8050000000")
	@JsonProperty("beneficiaryMobile")
	private String beneficiaryMobile;

	@ApiModelProperty(required = true, position = 4, example = "Shobha")
	@JsonProperty("beneficiaryName")
	private String beneficiaryName;

	@ApiModelProperty(required = false, position = 5, example = "true")
	@JsonProperty("nameFuzzy")
	private String nameFuzzy;

}