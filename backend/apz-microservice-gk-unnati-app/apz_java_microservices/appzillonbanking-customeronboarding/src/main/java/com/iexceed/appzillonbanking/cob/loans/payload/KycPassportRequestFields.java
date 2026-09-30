package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class KycPassportRequestFields {

	@JsonProperty("client_ref_num")
	private String client_ref_num;

	@JsonProperty("file_number")
	private String file_number;

	@JsonProperty("dob")
	private String dob;

}
