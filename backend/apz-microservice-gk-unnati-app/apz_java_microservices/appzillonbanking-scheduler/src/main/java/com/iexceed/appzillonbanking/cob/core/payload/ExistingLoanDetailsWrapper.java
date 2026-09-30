package com.iexceed.appzillonbanking.cob.core.payload;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ExistingLoanDetails;

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
public class ExistingLoanDetailsWrapper {

	@JsonProperty("custDtlId")
	private BigDecimal custDtlId;

	@JsonProperty("existingLoansDetailList")
	private List<ExistingLoanDetails> existingLoanDetailsList;

	@Override
	public String toString() {
		return "ExistingLoanDetailsWrapper{" + "custDtlId=" + custDtlId + ", addressDetailsList="
				+ existingLoanDetailsList + '}';
	}
}