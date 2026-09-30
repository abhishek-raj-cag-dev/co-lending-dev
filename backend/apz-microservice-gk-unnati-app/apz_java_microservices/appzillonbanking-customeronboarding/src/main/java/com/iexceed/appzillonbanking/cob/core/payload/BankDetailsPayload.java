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
public class BankDetailsPayload {

	@JsonProperty("accountType")
	private String accountType;

	@JsonProperty("accountName")
	private String accountName;

	@JsonProperty("ifsc")
	private String ifsc;

	@JsonProperty("bankName")
	private String bankName;
	
	@JsonProperty("branchName")
	private String branchName;
	
	@JsonProperty("accountNumber")
	private String accountNumber;
	
	@JsonProperty("editBankDetails")
	private String editBankDetails;

	@JsonProperty("pennyCheckStatus")
	private String pennyCheckStatus;
	
	@JsonProperty("reEnterAccountNumber")
	private String reEnterAccountNumber;
	
	@JsonProperty("rpcEditCheck")
	private boolean rpcEditCheck;
	
	@JsonProperty("pennyResp")
	private String pennyResp;
	
	@JsonProperty("eNachStatus")
	private String eNachStatus;
	
	@JsonProperty("accntVerified")
	private String accntVerified;
	
	@JsonProperty("rpcaccntVerified")
	private String rpcaccntVerified;
	
	@JsonProperty("bankdocumentType")
	private String bankdocumentType;

	/* updated changes 25-05-2026 */

	@JsonProperty("primaryPennyCheckStatus")
	private String primaryPennyCheckStatus;

	@JsonProperty("primaryENachStatus")
	private String primaryENachStatus;

	@JsonProperty("secondaryENachStatus")
	private String secondaryENachStatus;

	@JsonProperty("isAccountVerified")
	private String isAccountVerified;

	public void setIfsc(String ifsc){
		this.ifsc = (null == ifsc) ? "" : ifsc.trim().toUpperCase();
	}
}
