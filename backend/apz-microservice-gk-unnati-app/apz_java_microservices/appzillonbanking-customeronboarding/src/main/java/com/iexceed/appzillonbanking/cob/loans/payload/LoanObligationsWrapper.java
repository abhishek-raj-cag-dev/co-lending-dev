package com.iexceed.appzillonbanking.cob.loans.payload;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoanObligationsWrapper {
	
	private static final Logger logger = LogManager.getLogger(LoanObligationsWrapper.class);

    @ApiModelProperty(required = false, example = "")
	@JsonProperty("chitFunds")
	private LoanObligationsNestedClass chitFunds;

    @ApiModelProperty(required = false, example = "")
	@JsonProperty("moneyLender")
	private LoanObligationsNestedClass moneyLender;

    @ApiModelProperty(required = false, example = "")
	@JsonProperty("Friends")
	private LoanObligationsNestedClass Friends;

    @ApiModelProperty(required = false, example = "")
	@JsonProperty("Relatives")
	private LoanObligationsNestedClass Relatives;

    @ApiModelProperty(required = false, example = "")
	@JsonProperty("Coopsociety")
	private LoanObligationsNestedClass Coopsociety;

    @ApiModelProperty(required = false, example = "")
	@JsonProperty("Localfinance")
	private LoanObligationsNestedClass Localfinance;

    @ApiModelProperty(required = false, example = "")
	@JsonProperty("Other")
	private LoanObligationsNestedClass Other;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("totalLoanObligations")
	private String totalLoanObligations;

	 /**
     * Computes the total loan obligations by summing all 'otherLoanObligation' values.
     */
    public void computeTotalLoanObligations() {
        BigDecimal total = BigDecimal.ZERO;

        total = total.add(getLoanObligationValue(chitFunds));
        total = total.add(getLoanObligationValue(moneyLender));
        total = total.add(getLoanObligationValue(Friends));
        total = total.add(getLoanObligationValue(Relatives));
        total = total.add(getLoanObligationValue(Coopsociety));
        total = total.add(getLoanObligationValue(Localfinance));
        total = total.add(getLoanObligationValue(Other));

        this.totalLoanObligations = total.toString();
    }

    /**
     * Helper method to safely extract and convert loan obligations to BigDecimal.
     */
    private BigDecimal getLoanObligationValue(LoanObligationsNestedClass loanObligation) {
        if (loanObligation != null && loanObligation.getOtherLoanObligation() != null) {
            try {
                return new BigDecimal(loanObligation.getOtherLoanObligation());
            } catch (NumberFormatException e) {
               logger.error("Error parsing loan obligation value: " + loanObligation.getOtherLoanObligation(), e);
            }
        }
        return BigDecimal.ZERO;
    }
}
