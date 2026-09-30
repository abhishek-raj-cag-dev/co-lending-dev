package com.iexceed.appzillonbanking.cob.payload;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoanCreationReqFields {

	@ApiModelProperty(required = true, position = 1, example = "100817511")
	@JsonProperty("applicantId")
	private String applicantId;

	@ApiModelProperty(required = true, position = 2, example = "100817511")
	@JsonProperty("unnatiCoCustomer")
	private List<Map<String, Integer>> unnatiCoCustomer ;

	@ApiModelProperty(required = true, position = 3, example = "GL.GRM.UNNATI.LN")
	@JsonProperty("product")
	private String product;

	@ApiModelProperty(required = true, example = "INR") 
	@JsonProperty("currency")
	private String currency;

	@ApiModelProperty(required = true, example = "156W")
	@JsonProperty("term")
	private String term;

	@ApiModelProperty(required = true, example = "210000")
	@JsonProperty("amount")
	private BigDecimal amount;

	@ApiModelProperty(required = true, example = "23.00")
	@JsonProperty("intRate")
	private String intRate;

	@ApiModelProperty(required = true, example = "BI_WEEKLY")
	@JsonProperty("frequency")
	private String frequency;

	@ApiModelProperty(required = true, example = "NEFT")
	@JsonProperty("disburseMode")
	private String disburseMode;

	@ApiModelProperty(required = true, example = "54")
	@JsonProperty("purpose")
	private String purpose;

	@ApiModelProperty(required = true, example = "TAILORING.MACHINE")
	@JsonProperty("subPurpose")
	private String subPurpose;

	@ApiModelProperty(required = false, example = "Shivappa Mada")
	@JsonProperty("nomnieeName")
	private String nomnieeName;

	@ApiModelProperty(required = false, example = "112")
	@JsonProperty("nomineeRelation")
	private String nomineeRelation;

	@ApiModelProperty(required = false, example = "7876312322")
	@JsonProperty("nomineePhone")
	private String nomineePhone;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("cbResponseDate")
	private String cbResponseDate;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("cbRemarks")
	private String cbRemarks;
	
	@ApiModelProperty(required = true, example = "YES")
	@JsonProperty("borrowerInsurance")
	private String borrowerInsurance;
	
	@ApiModelProperty(required = true, example = "YES")
	@JsonProperty("jointOwnerOrCoborrowerInsurance")
	private String jointOwnerOrCoborrowerInsurance;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("systemApplicationId")
	private String systemApplicationId;
	
	@ApiModelProperty(required = true, example = "3- All Loan Preclosure")
	@JsonProperty("preCloseType")
	private String preCloseType;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("payoffAccount")
	private String payoffAccount;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("companyIdTemp")
	private String companyIdTemp;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("foir")
	private String foir;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("annualPercentageRate")
	private String annualPercentageRate;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("earningMembers")
	private String earningMembers;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("familyIncome")
	private String familyIncome;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("memberIns")
	private String memberIns;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("coBorrowerIns")
	private String coBorrowerIns;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("procsFees")
	private String procsFees;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("procsFeeTax")
	private String procsFeeTax;
	
}
