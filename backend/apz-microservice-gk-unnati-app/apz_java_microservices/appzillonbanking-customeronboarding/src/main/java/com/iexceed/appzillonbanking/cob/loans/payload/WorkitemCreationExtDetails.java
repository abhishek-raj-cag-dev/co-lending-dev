package com.iexceed.appzillonbanking.cob.loans.payload;
import java.time.LocalDate;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationExtDetails {

	@JacksonXmlProperty(localName = "product_type_desc")
	private String productTypeDesc;

	@JacksonXmlProperty(localName = "Branch_Name")
	private String branchName;

	@JacksonXmlProperty(localName = "Area")
	private String area;

	@JacksonXmlProperty(localName = "Region")
	private String region;

	@JacksonXmlProperty(localName = "Branch_Name_Desc")
	private String branchNameDesc;

	@JacksonXmlProperty(localName = "RF_BRANCH_NAME")
	private String rfBranchName;

	@JacksonXmlProperty(localName = "GSTIN")
	private String gstin;

	@JacksonXmlProperty(localName = "UDYAM_REGIS_NUM")
	private String udyamRegisNum;

	@JacksonXmlProperty(localName = "Repayment_Freq")
	private String repaymentFreq;

	@JacksonXmlProperty(localName = "ROI")
	private String roi;

	@JacksonXmlProperty(localName = "Insurance_for_coapplicant")
	private String insuranceForCoapplicant;

	@JacksonXmlProperty(localName = "Iexceed_Flag")
	private String iexceedFlag;

	@JacksonXmlProperty(localName = "Lead_initiated_from")
	private String leadInitiatedFrom;

	@JacksonXmlProperty(localName = "Applicant_Name")
	private String applicantName;

	@JacksonXmlProperty(localName = "Product_Name")
	private String productName;

	@JacksonXmlProperty(localName = "Running_EMI")
	private String runningEMI;

	@JacksonXmlProperty(localName = "Product_Type")
	private String productType;
	@JacksonXmlProperty(localName = "ACT_Date")
	private String actDate;
	
	@JacksonXmlProperty(localName = "Approved_amount")
	private String approvedAmount;
	
	@JacksonXmlProperty(localName = "Foir")
	private String foir;
}
