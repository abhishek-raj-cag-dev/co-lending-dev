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
public class VoterFrontOcrRequestFields {
	
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
	
	@JsonProperty("isCompleteImageCheck")
	private String isCompleteImageCheck;
	
	@JsonProperty("doctype")
	private String doctype;

	public VoterFrontOcrRequestFields(VoterFrontOcrRequestFields voterFrontOcrRequestFields){
		this.doctype = voterFrontOcrRequestFields.doctype;
		this.confidence = voterFrontOcrRequestFields.confidence;
		this.RequestID = voterFrontOcrRequestFields.RequestID;
		this.fraudCheck = voterFrontOcrRequestFields.fraudCheck;
		this.isBlackWhiteCheck = voterFrontOcrRequestFields.isBlackWhiteCheck;
		this.isCompleteImageCheck = voterFrontOcrRequestFields.isCompleteImageCheck;
	}
}
