package com.iexceed.appzillonbanking.cob.core.payload;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetails;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddressDetailsWrapper {
	
	@JsonProperty("custDtlId")
	private BigDecimal custDtlId;

	@JsonProperty("addressDetailsList")
	private List<AddressDetails> addressDetailsList;

	@Override
	public String toString() {
		return "AddressDetailsWrapper{" +
				"custDtlId=" + custDtlId +
				", addressDetailsList=" + addressDetailsList +
				'}';
	}
}