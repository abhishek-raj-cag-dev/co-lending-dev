package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BREDocumentDetails {

	@Override
	public String toString() {
		return "BREDocumentDetails [docType=" + docType + ", docId=" + docId + "]";
	}

	@ApiModelProperty(required = true, example = "VOTER-ID")
	@JsonProperty("docType")
	private String docType;

	@ApiModelProperty(required = true, example = "23163266655343")
	@JsonProperty("docId")
	private String docId;
}
