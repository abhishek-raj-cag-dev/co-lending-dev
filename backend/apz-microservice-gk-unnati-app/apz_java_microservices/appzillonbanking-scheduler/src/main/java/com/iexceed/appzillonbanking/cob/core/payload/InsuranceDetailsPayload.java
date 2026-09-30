package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InsuranceDetailsPayload {

	@JsonProperty("insuranceReqd")
	private String insuranceReqd;

	@JsonProperty("insuredName")
	private String insuredName;

	@JsonProperty("nomineeName")
	private String nomineeName;

	@JsonProperty("nomineeRelation")
	private String nomineeRelation;

	@JsonProperty("nomineeDob")
	private String nomineeDob;
	
	@JsonProperty("coApplicantInsurance")
	private String coApplicantInsurance;
	
	@JsonProperty("age")
	private String age;

	@JsonProperty("rpcEditCheck")
	private boolean rpcEditCheck;
	
	@JsonProperty("gender")
	private String gender;
	
	@JsonProperty("loanAmount")
	private String loanAmount;
	
	@JsonProperty("insurerName")
	private String insurerName;
	
	@JsonProperty("insurancePremium")
	private String insurancePremium;
	
	@JsonProperty("insuranceOption")
	private String insuranceOption;

	@JsonProperty("nomineeAdded")
    private String nomineeAdded;

}
