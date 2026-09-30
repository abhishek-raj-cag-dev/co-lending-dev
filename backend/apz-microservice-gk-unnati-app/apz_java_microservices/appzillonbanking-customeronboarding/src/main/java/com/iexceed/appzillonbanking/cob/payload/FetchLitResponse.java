package com.iexceed.appzillonbanking.cob.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FetchLitResponse {

	@JsonProperty("apiResponse")
	private List<LITDomain> litDomain;

}
