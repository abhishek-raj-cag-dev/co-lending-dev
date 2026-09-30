package com.iexceed.appzillonbanking.cob.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CRSDetailsPayload {

	@JsonProperty("otherCountryTaxResidant")
	private String otherCountryTaxResidant;
	
	@JsonProperty("taxDetailsList")
	private List<TaxDetails> taxDetailsList;

	@Override
	public String toString() {
		return "CRSDetailsPayload{" +
				"otherCountryTaxResidant='" + otherCountryTaxResidant + '\'' +
				", taxDetailsList=" + taxDetailsList +
				'}';
	}
}