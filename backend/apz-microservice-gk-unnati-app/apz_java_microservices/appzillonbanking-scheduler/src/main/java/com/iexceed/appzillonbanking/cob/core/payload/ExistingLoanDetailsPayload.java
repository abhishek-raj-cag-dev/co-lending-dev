package com.iexceed.appzillonbanking.cob.core.payload;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExistingLoanDetailsPayload {
	
//	@JsonProperty("existingLoanList")
//	private List<ExistingLoan> existingLoanList;
	
//	@JsonProperty("hasExistingLoans")
//	private String hasExistingLoans;
	
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
	private String expectedClosureDate;
	
}
