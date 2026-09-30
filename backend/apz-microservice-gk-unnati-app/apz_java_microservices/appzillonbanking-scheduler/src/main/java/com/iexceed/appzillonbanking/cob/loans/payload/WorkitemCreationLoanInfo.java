package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationLoanInfo {

	@JacksonXmlProperty(localName = "dd_fav_det_cust_prefix")
	private String ddFavDetCustPrefix;
	
	@JacksonXmlProperty(localName = "Purpose_of_Loan_others")
    private String purposeOfLoanOthers;
    
	@JacksonXmlProperty(localName = "GRT_Loan_Type")
    private String grtLoanType;
    
	@JacksonXmlProperty(localName = "GRT_DD_Fav")
    private String grtDdFav;
    
	@JacksonXmlProperty(localName = "Relationship_with_CAGL")
    private String relWithCagl;
    
	@JacksonXmlProperty(localName = "Loan_Type")
    private String loanType;
    
	@JacksonXmlProperty(localName = "Requested_Loan_Amount")
    private String requestedLoanAmount;
    
	@JacksonXmlProperty(localName = "Loan_Tenure_In_Months")
    private String loanTenureInMonths;
    
	@JacksonXmlProperty(localName = "Purpose_of_Loan")
    private String purposeOfLoan;
    
	@JacksonXmlProperty(localName = "Sub_Purpose_of_Loan")
    private String subPurposeOfLoan;
    
	@JacksonXmlProperty(localName = "Rate_of_Interest")
    private String rateOfInterest;
    
	@JacksonXmlProperty(localName = "Lang_for_Comm")
    private String langForComm;
    
	@JacksonXmlProperty(localName = "Mode_of_Disbursement")
    private String modeOfDisbursement;
    
	@JacksonXmlProperty(localName = "PDD_Status")
    private String pddStatus;
    
	@JacksonXmlProperty(localName = "CRO_Name")
    private String croName;
    
	@JacksonXmlProperty(localName = "CRO_ID")
    private String croID;
    
	@JacksonXmlProperty(localName = "Overall_GrossIncome")
    private String overallGrossIncome;
    
	@JacksonXmlProperty(localName = "Overall_Net_Income")
    private String overallNetIncome;
    
	@JacksonXmlProperty(localName = "Tot_Obli_Considered")
    private String totObliConsidered;
    
	@JacksonXmlProperty(localName = "Actual_FOIR_on_G_I")
    private String actualFoirOnGI;
    
	@JacksonXmlProperty(localName = "Actual_FOIR_on_N_I")
    private String actualFoirOnNI;
    
	@JacksonXmlProperty(localName = "Total_Asset_Cost")
    private String totalAssetCost;
    
	@JacksonXmlProperty(localName = "Margin_Money")
    private String marginMoney;
    
	@JacksonXmlProperty(localName = "Diff_Sal_Turnover_CA_Report")
    private String diffSalTurnoverCaReport;
    
	@JacksonXmlProperty(localName = "Final_Loan_Amount")
    private String finalLoanAmount;
    
	@JacksonXmlProperty(localName = "LTV")
    private String ltv;
    
	@JacksonXmlProperty(localName = "Total_Exposure")
    private String totalExposure;
    
	@JacksonXmlProperty(localName = "repayment_Frequency")
    private String repaymentFrequency;
}
