package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CoApplicantDetailRequestField {
	@JsonProperty("coApplicantRelation")
	private String coApplicantRelation = "";

	@JsonProperty("coApplicantCustomerID")
	private String coApplicantCustomerID = "";
}
