package com.iexceed.appzillonbanking.cob.core.payload;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.domain.ab.OccupationDetails;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class OccupationDetailsWrapper {
	
	@JsonProperty("custDtlId")
	private BigDecimal custDtlId;
	
	@JsonProperty("occupationDetails")
	private OccupationDetails occupationDetails;

	@Override
	public String toString() {
		return "OccupationDetailsWrapper{" +
				"custDtlId=" + custDtlId +
				", occupationDetails=" + occupationDetails +
				'}';
	}
}