package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerIdentificationLoan;
import com.iexceed.appzillonbanking.cob.loans.payload.ApplyLoanRequestFields;

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
public class CallReplicateFields {
	
	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("userId")
	private String userId;
	
	@JsonProperty("versionNum")
	private int versionNum;
	
	@JsonProperty("product")
	private String product;
	
	@JsonProperty("customerId")
	private String customerId;
	
	@JsonProperty("applicationId")
	private String applicationId;

}
