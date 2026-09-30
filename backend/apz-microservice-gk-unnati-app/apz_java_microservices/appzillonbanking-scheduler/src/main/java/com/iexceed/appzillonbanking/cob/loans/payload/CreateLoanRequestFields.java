package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.LoanDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLoanRequestFields {

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("versionNum")
	private int versionNum;

	@JsonProperty("applicationMaster")
	private ApplicationMaster applicationMaster;

	@JsonProperty("loanDetails")
	private LoanDetails loanDetails;
}