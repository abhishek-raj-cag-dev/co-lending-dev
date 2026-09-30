package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportWrapper {

	@JsonProperty("apiRequest")
	private ReportRequest apiRequest;

}
