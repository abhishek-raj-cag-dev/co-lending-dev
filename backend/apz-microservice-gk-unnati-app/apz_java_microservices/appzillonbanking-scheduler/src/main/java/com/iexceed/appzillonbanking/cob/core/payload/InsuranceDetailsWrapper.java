package com.iexceed.appzillonbanking.cob.core.payload;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.domain.ab.InsuranceDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InsuranceDetailsWrapper {

	@JsonProperty("custDtlId")
	private BigDecimal custDtlId;

	@JsonProperty("insuranceDetails")
	private InsuranceDetails insuranceDetails;

}
