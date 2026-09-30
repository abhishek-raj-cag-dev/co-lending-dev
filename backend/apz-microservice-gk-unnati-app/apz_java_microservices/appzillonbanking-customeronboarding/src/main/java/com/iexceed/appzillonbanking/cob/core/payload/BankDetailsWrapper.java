package com.iexceed.appzillonbanking.cob.core.payload;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.domain.ab.BankDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankDetailsWrapper {

	@JsonProperty("bankDtlId")
	private BigDecimal bankDtlId;

	@JsonProperty("bankDetails")
	private BankDetails bankDetails;

	@Override
	public String toString() {
		return "BankDetailsWrapper{" + "custDtlId=" + bankDtlId + ", bankDetails=" + bankDetails + '}';
	}
}