package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoanObligationsNestedClass {

    @JsonProperty("otherLoanObligation")
    private String otherLoanObligation;

    @JsonProperty("monthlyEMI")
    private String monthlyEMI;

    @JsonProperty("tenure")
    private String tenure;
}
