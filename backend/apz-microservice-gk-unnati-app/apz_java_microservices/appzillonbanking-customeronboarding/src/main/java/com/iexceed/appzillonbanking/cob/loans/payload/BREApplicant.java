package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BREApplicant {

	@ApiModelProperty(required = false, example = "P")
	@JsonProperty("applicantType")
	private String applicantType;

	@JsonProperty("address")
	private List<BREAddressDetails> addressDetails;

	@JsonProperty("document")
	private List<BREDocumentDetails> documentDetails;

	@ApiModelProperty(required = false, example = "F")
	@JsonProperty("depType")
	private String depType;

	@ApiModelProperty(required = false, example = "depName")
	@JsonProperty("depName")
	private String depName;

	@ApiModelProperty(required = false, example = "1")
	@JsonProperty("categoryId")
	private String categoryId;

	@ApiModelProperty(required = false, example = "CCR")
	@JsonProperty("productCode")
	private String productCode;

	@ApiModelProperty(required = false, example = "24838")
	@JsonProperty("dob")
	private String dob;

	@ApiModelProperty(required = false, example = "12")
	@JsonProperty("durationOfAgreement")
	private String durationOfAgreement;

	@ApiModelProperty(required = false, example = "1")
	@JsonProperty("bankProductId")
	private String bankProductId;

	@ApiModelProperty(required = false, example = "KASHIBAI SIDARAY KUMBAR")
	@JsonProperty("custName")
	private String custName;

	@ApiModelProperty(required = false, example = "2")
	@JsonProperty("gender")
	private String gender;

	@ApiModelProperty(required = false, example = "6559537791")
	@JsonProperty("phone")
	private String phone;

	@ApiModelProperty(required = false, example = "2")
	@JsonProperty("loanType")
	private String loanType;

	@ApiModelProperty(required = false, example = "77777777")
	@JsonProperty("appId")
	private String appId;

	@ApiModelProperty(required = false, example = "1")
	@JsonProperty("losIndicator")
	private String losIndicator;

	@ApiModelProperty(required = false, example = "LOS")
	@JsonProperty("losIndex")
	private String losIndex;

	@ApiModelProperty(required = false, example = "1")
	@JsonProperty("slNo")
	private String slNo;

	@ApiModelProperty(required = false, example = "220000")
	@JsonProperty("loanAmount")
	private String loanAmount;

	@ApiModelProperty(required = false, example = "2")
	@JsonProperty("term")
	private int term;

	@ApiModelProperty(required = false, example = "54")
	@JsonProperty("applied_frequency")
	private String appliedFrequency;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("email")
	private String email;

	@ApiModelProperty(required = false, example = "2")
	@JsonProperty("maritalStatus")
	private String maritalStatus;

	@ApiModelProperty(required = false, example = "kendra")
	@JsonProperty("kendra")
	private String kendra;

	@ApiModelProperty(required = false, example = "IN0010010")
	@JsonProperty("branch")
	private String branch;

	@ApiModelProperty(required = false, example = "5")
	@JsonProperty("stateBranch")
	private String stateBranch;

	@ApiModelProperty(required = false, example = "1002027")
	@JsonProperty("custId")
	private String custId;

	@ApiModelProperty(required = false, example = "OTHERS")
	@JsonProperty("source")
	private String source;

	@ApiModelProperty(required = false, example = "H")
	@JsonProperty("enquiryType")
	private String enquiryType;

	@ApiModelProperty(required = false, example = "LN00103040")
	@JsonProperty("loanId")
	private String loanId;

	@ApiModelProperty(required = false, example = "DigiAgil")
	@JsonProperty("DigiAgil_DFA_Flag")
	private String digiAgilDFAFlag;

	@ApiModelProperty(required = false, example = "290000")
	@JsonProperty("hh_annual_income")
	private int hhAnnualIncome;

	@ApiModelProperty(required = false, example = "ABC")
	@JsonProperty("earning_flag")
	private String earningFlag;

	@ApiModelProperty(required = false, example = "ABC")
	@JsonProperty("loan_product_type")
	private String loanProductType;

	@ApiModelProperty(required = false, example = "43889")
	@JsonProperty("activation_date")
	private String activationDate;

	@ApiModelProperty(required = false, example = "GL.GRM.UNNATI.LN")
	@JsonProperty("product_code")
	private String product_code;

	@ApiModelProperty(required = false, example = "Y")
	@JsonProperty("unnati_renewal_flag")
	@Getter(onMethod_ = @JsonProperty("unnati_renewal_flag"))
	private String unnatiRenewalFlag = "N";

	@ApiModelProperty(required = false, example = "Option 1")
	@JsonProperty("Q1")
	private String Q1;

	@ApiModelProperty(required = false, example = "Option 3")
	@JsonProperty("Q2")
	private String Q2;

	@ApiModelProperty(required = false, example = "Option 1")
	@JsonProperty("Q3")
	private String Q3;

	@ApiModelProperty(required = false, example = "Option 5")
	@JsonProperty("Q4")
	private String Q4;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q5")
	private String Q5;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q6")
	private String Q6;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q7")
	private String Q7;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q8")
	private String Q8;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q9")
	private String Q9;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q10")
	private String Q10;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q11")
	private String Q11;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q12")
	private String Q12;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q13")
	private String Q13;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q14")
	private String Q14;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q15")
	private String Q15;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q16")
	private String Q16;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q17")
	private String Q17;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q18")
	private String Q18;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q19")
	private String Q19;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q20")
	private String Q20;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q21")
	private String Q21;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q22")
	private String Q22;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q23")
	private String Q23;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q24")
	private String Q24;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q25")
	private String Q25;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q26")
	private String Q26;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q27")
	private String Q27;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Q28")
	private String Q28;

	@ApiModelProperty(required = false, example = " ")
	@JsonProperty("Field_Assessed_Income_of_customer")
	private String Field_Assessed_Income_of_customer;

	@ApiModelProperty(required = false, example = "NP001")
	@JsonProperty("Income_Assessment_Flag")
	private String Income_Assessment_Flag;

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("Self_Declared_Income_of_customer")
    private String Self_Declared_Income_of_customer;

	@ApiModelProperty(required = false, example = "2023-09-21")
	@JsonProperty("kendra_activation_date")
	private String kendraActivationDate;

	@ApiModelProperty(required = false,example = "52")
	@JsonProperty("applied_term_weeks")
	private Integer appliedTermWeeks;

	@ApiModelProperty(required = false, example = "120000")
	@JsonProperty("crt_approved_amount")
	private String crt_approved_amount;

	@ApiModelProperty(required = false, example = "1")
	@JsonProperty("crt_flag")
	private String crt_flag;

	@ApiModelProperty(required = false, example = "CRT")
	@JsonProperty("crt_identifier")
	private String crt_identifier;

}
