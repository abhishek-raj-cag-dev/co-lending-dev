package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VoterBackOcrRequestFields {

	@JsonProperty("imageUrl")
	private String imageUrl;

	@JsonProperty("RequestID")
	private String RequestID;

	@JsonProperty("isBlackWhiteCheck")
	private String isBlackWhiteCheck;

	@JsonProperty("confidence")
	private String confidence;

	@JsonProperty("fraudCheck")
	private String fraudCheck;

	@JsonProperty("doctype")
	private String doctype;

	public VoterBackOcrRequestFields(VoterBackOcrRequestFields voterBackOcrRequestFields){
		this.doctype = voterBackOcrRequestFields.doctype;
		this.confidence = voterBackOcrRequestFields.confidence;
		this.RequestID = voterBackOcrRequestFields.RequestID;
		this.fraudCheck = voterBackOcrRequestFields.fraudCheck;
		this.isBlackWhiteCheck = voterBackOcrRequestFields.isBlackWhiteCheck;
	}
}
