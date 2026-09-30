package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BankingFacilitiesPayload {

	@JsonProperty("branchName")
	private String branchName;
	
	@JsonProperty("branchAddress")
	private String branchAddress;
	
	@JsonProperty("branchCode")
	private String branchCode;
	
	@JsonProperty("chequeBookRequired")
	private String chequeBookRequired;
	
	@JsonProperty("debitCardRequired")
	private String debitCardRequired;
	
	@JsonProperty("debitCardNameSameAsNID")
	private String debitCardNameSameAsNID;
	
	@JsonProperty("nameOnCard")
	private String nameOnCard;
	
	@JsonProperty("mbRequired")
	private String mbRequired;
	
	@JsonProperty("ibRequired")
	private String ibRequired;
	
	@JsonProperty("smsAlertsRequired")
	private String smsAlertsRequired;
	
	@JsonProperty("eStmtRequired")
	private String eStmtRequired;
	
	@JsonProperty("passBookRequired")
	private String passBookRequired;

	@JsonProperty("noOfChequeLeaves")
	private int noOfChequeLeaves;

	@Override
	public String toString() {
		return "BankingFacilitiesPayload{" +
				"branchName='" + branchName + '\'' +
				", branchAddress='" + branchAddress + '\'' +
				", branchCode='" + branchCode + '\'' +
				", chequeBookRequired='" + chequeBookRequired + '\'' +
				", debitCardRequired='" + debitCardRequired + '\'' +
				", debitCardNameSameAsNID='" + debitCardNameSameAsNID + '\'' +
				", nameOnCard='" + nameOnCard + '\'' +
				", mbRequired='" + mbRequired + '\'' +
				", ibRequired='" + ibRequired + '\'' +
				", smsAlertsRequired='" + smsAlertsRequired + '\'' +
				", eStmtRequired='" + eStmtRequired + '\'' +
				", passBookRequired='" + passBookRequired + '\'' +
				", noOfChequeLeaves=" + noOfChequeLeaves +
				'}';
	}
}