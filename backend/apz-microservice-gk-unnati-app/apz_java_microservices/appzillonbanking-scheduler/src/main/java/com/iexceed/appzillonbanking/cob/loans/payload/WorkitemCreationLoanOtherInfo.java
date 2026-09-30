package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationLoanOtherInfo {
	
	@JacksonXmlProperty(localName = "QC_Sampling_Required")
	private String qcSamplingRequired;
	
	@JacksonXmlProperty(localName = "QC_Place_of_PD")
    private String qcPlaceOfPd;
    
	@JacksonXmlProperty(localName = "QC_Person_Met_during_visit")
    private String qcPersonMetDuringVisit;
    
	@JacksonXmlProperty(localName = "QC_Recommendation")
    private String qcRecommendation;
    
	@JacksonXmlProperty(localName = "PD_Main_Loan_ID")
    private String pdMainLoanId;
    
	@JacksonXmlProperty(localName = "PD_Loan_Account_ID")
    private String pdLoanAccountId;
    
	@JacksonXmlProperty(localName = "PD_NEFT_DD_Amount")
    private String pdNeftDdAmount;
    
	@JacksonXmlProperty(localName = "PD_Pri_Bank_Acc_Holder")
    private String pdPriBankAccHolder;
    
	@JacksonXmlProperty(localName = "PD_GL_RF_Outstanding_Amt")
    private String pdGlRfOutstandingAmt;
    
	@JacksonXmlProperty(localName = "PD1_Place")
    private String pd1Place;
    
	@JacksonXmlProperty(localName = "PD1_Person_Met_During_Visit")
    private String pd1PersonMetDuringVisit;
    
	@JacksonXmlProperty(localName = "F_Family_type")
    private String fFamilyType;
    
	@JacksonXmlProperty(localName = "F_Total_Family_members")
    private String fTotalFamilyMembers;
    
	@JacksonXmlProperty(localName = "F_Children_below_18_years")
    private String fChildrenBelow18Years;
    
	@JacksonXmlProperty(localName = "F_No_of_earning_members")
    private String fNoOfEarningMembers;
    
	@JacksonXmlProperty(localName = "F_Members_above_60_years")
    private String fMembersAbove60Years;
    
	@JacksonXmlProperty(localName = "LOb_Tot_OTHER_OBLI_CONSIDERED")
    private String lObTotOtherObliConsidered;
    
	@JacksonXmlProperty(localName = "LOb_Tot_Obli_Considered")
    private String lObTotObliConsidered;
    
	@JacksonXmlProperty(localName = "LOb_Current_CAGL_Exposure")
    private String lObCurrentCaglExposure;
    
	@JacksonXmlProperty(localName = "LOb_Current_CAGL_EMI")
    private String lObCurrentCaglEmi;
    
	@JacksonXmlProperty(localName = "LOb_To_be_considered")
    private String lObToBeConsidered;
    
	@JacksonXmlProperty(localName = "F_Other_mem_Income")
    private String fOtherMemIncome;
    
	@JacksonXmlProperty(localName = "Recommendation")
    private String recommendation;
    
	@JacksonXmlProperty(localName = "Lob_Tot_GK_Obli_Considered")
    private String lobTotGkObliConsidered;
    
	@JacksonXmlProperty(localName = "Lob_Consider_for_FOIR_Devi")
    private String lobConsiderForFoirDevi;
    
	@JacksonXmlProperty(localName = "PDT_Amt_credited_to_PBH_acc")
    private String pdtAmtCreditedToPbhAcc;
    
	@JacksonXmlProperty(localName = "PDT_Cust_aware_of_disbursement")
    private String pdtCustAwareOfDisbursement;
    
	@JacksonXmlProperty(localName = "PDT_No_of_attempts_to_contact")
    private String pdtNoOfAttemptsToContact;
    
	@JacksonXmlProperty(localName = "PDT_Received_Your_vehicle")
    private String pdtReceivedYourVehicle;
    
	@JacksonXmlProperty(localName = "PDT_Date_of_delivery")
    private String pdtDateOfDelivery;

}
