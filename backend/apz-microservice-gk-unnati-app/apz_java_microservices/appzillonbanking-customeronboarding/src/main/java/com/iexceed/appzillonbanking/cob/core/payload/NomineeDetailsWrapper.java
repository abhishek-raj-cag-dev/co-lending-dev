package com.iexceed.appzillonbanking.cob.core.payload;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.domain.ab.NomineeDetails;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class NomineeDetailsWrapper {

	@JsonProperty("custDtlId")
	private BigDecimal custDtlId;
	
	@JsonProperty("nomineeDetailsList")
	private List<NomineeDetails> nomineeDetailsList;

	@Override
	public String toString() {
		return "NomineeDetailsWrapper{" +
				"custDtlId=" + custDtlId +
				", nomineeDetailsList=" + nomineeDetailsList +
				'}';
	}
}