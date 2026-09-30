package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PanCheckRequestFields {
	
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

	public PanCheckRequestFields(PanCheckRequestFields panCheckRequestFields){
		this.doctype = panCheckRequestFields.doctype;
		this.confidence = panCheckRequestFields.confidence;
		this.RequestID = panCheckRequestFields.RequestID;
		this.fraudCheck = panCheckRequestFields.fraudCheck;
		this.isBlackWhiteCheck = panCheckRequestFields.isBlackWhiteCheck;
		this.isCompleteImageCheck = panCheckRequestFields.isCompleteImageCheck;
	}
}
