package com.iexceed.appzillonbanking.cob.core.payload;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExistingLoan {
	
	@JsonProperty("productType")
	private String productType;
	
	@JsonProperty("subProductType")
	private String subProductType;
	
	@JsonProperty("loanAmount")
	private String loanAmount;

	@JsonProperty("accountNumber")
	private String accountNumber;
	
	@JsonProperty("outstandingAmount")
	private String outstandingAmount;	
	
	@JsonProperty("expectedClosureDate")
	private LocalDate expectedClosureDate;
	
}