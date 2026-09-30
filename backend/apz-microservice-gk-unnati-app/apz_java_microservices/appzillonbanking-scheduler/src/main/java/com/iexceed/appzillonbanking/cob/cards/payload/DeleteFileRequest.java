package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeleteFileRequest {

	
	@JsonProperty("requestObj")
	private DeleteFileReqFields requestObj;
}