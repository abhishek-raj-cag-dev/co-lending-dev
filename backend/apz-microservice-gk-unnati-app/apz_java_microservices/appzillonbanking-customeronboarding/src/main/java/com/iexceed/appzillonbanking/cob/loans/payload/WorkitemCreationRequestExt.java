package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
@JacksonXmlRootElement(localName = "WI-REQ")
public class WorkitemCreationRequestExt {
	
	@JacksonXmlProperty(localName = "Q_Lead_Details")
	private WorkitemCreationLeadDetails leadDetails;
	
	@JacksonXmlProperty(localName = "Q_LOAN_INFO")
	private WorkitemCreationLoanInfo loanInfo;
	
	@JacksonXmlProperty(localName = "Q_LOAN_OTHER_INFO")
	private WorkitemCreationLoanOtherInfo loanOtherInfo;
	
	@JacksonXmlProperty(localName = "Q_Financial_Analysis")
	private WorkitemCreationFinancialAnalysis financialAnalysis;
	
	@JacksonXmlElementWrapper(useWrapping = false)   
	@JacksonXmlProperty(localName = "Q_Cust_DG_DET")
	private List<WorkitemCreationCustDtls> custDtls;
	
	@JacksonXmlProperty(localName = "Q_CHEQUE_DET")
	private WorkitemCreationChequeDtls chequeDtls;
	
	@JacksonXmlProperty(localName = "Q_Owned_Land_Det")
	private WorkitemCreationOwnedLandDtls ownedLandDtls;
	
	@JacksonXmlProperty(localName = "Q_REFERENCE_DET")
	private WorkitemCreationReferenceDtls referenceDtls;
	
	@JacksonXmlProperty(localName = "Ext_Table")
	private WorkitemCreationExtDetails extDetails;
	
	@JacksonXmlElementWrapper(useWrapping = false)   
	@JacksonXmlProperty(localName = "Q_Cust_Address_DET")
	private List<WorkitemCreationAddressDetails> addressDtls;
	
	@JacksonXmlProperty(localName = "Q_BANKING_DET")
	private WorkitemCreationBankingDetails bankingDtl;
	
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "Q_INSURANCE_DET")
	private List<WorkitemCreationInsuranceDetails> insuranceDtls;
	
	@JacksonXmlProperty(localName = "Q_Cust_Other_Source_Det")
	private WorkitemCreationCustOtherSourceDetails custOtherSourceDtls;
	
	@JacksonXmlProperty(localName = "Q_BMPD_Application_Questions")
	private WorkitemCreationApplnQuestionDetails applnQuestionDtls;
	
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "Q_Cust_Occupation_DET")
	private List<WorkitemCreationOccupationDetails> occupationDtls;
	
	@JacksonXmlProperty(localName = "Q_BORROWING_DET")
	private WorkitemCreationBorrowingDetails borrowingDtls;
	
	@JacksonXmlProperty(localName = "MetaData")
	private WorkitemCreationMetadataDetails metadataDtls;
	
	@JacksonXmlProperty(localName = "HIGHMARK")
	private WorkitemCreationHighmarkDetails highmarkDtls;
	
	@JacksonXmlElementWrapper(localName = "Documents")
	@JacksonXmlProperty(localName = "Document")
	private List<WorkitemCreationDocumentDetails> documentDtls;
}
