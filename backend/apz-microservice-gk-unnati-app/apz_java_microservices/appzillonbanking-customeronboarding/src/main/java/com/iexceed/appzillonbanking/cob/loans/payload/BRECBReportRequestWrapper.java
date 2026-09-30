package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BRECBReportRequestWrapper {

	@Override
	public String toString() {
		return "BRECBReportRequestWrapper [apiRequest=" + apiRequest + "]";
	}

	@JsonProperty("apiRequest")
	private BRECBReportRequest apiRequest;
}