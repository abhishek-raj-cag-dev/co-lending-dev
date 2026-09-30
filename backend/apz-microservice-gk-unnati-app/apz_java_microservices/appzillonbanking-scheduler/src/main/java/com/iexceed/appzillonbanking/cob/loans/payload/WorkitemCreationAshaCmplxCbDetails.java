package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationAshaCmplxCbDetails {
	
	@JacksonXmlProperty(localName = "PID")
	private String pid;
	
	@JacksonXmlProperty(localName = "custPrefix")
    private String custPrefix;
	
	@JacksonXmlProperty(localName = "CB_Type")
    private String cbType;
	
	@JacksonXmlProperty(localName = "Name")
    private String name;
	
	@JacksonXmlProperty(localName = "CB_Score")
    private String cbScore;
	
	@JacksonXmlProperty(localName = "Applicant_Type")
    private String applicantType;
	
	@JacksonXmlProperty(localName = "Generated_On")
    private String generatedOn;
	
	@JacksonXmlProperty(localName = "Generated_By")
    private String generatedBy;
	
	@JacksonXmlProperty(localName = "DOCUMENT_NAME")
    private String documentName;
	
	@JacksonXmlProperty(localName = "Report_Expiry_Date")
    private String reportExpiryDate;
	
	@JacksonXmlProperty(localName = "Closed_account")
    private String closedAccount;
	
	@JacksonXmlProperty(localName = "Active_account")
    private String activeAccount;
	
	@JacksonXmlProperty(localName = "Credit_Vintage")
    private String creditVintage;
	
	@JacksonXmlProperty(localName = "Last_Six_Month_Enquires")
    private String lastSixMonthEnquires;
	
	@JacksonXmlProperty(localName = "Principle_Outstanding")
    private String principleOutstanding;
	
	@JacksonXmlProperty(localName = "CB_1_to_29_count")
    private String cb1To29Count;
	
	@JacksonXmlProperty(localName = "CB_30_to_59_count")
    private String cb30To59Count;
	
	@JacksonXmlProperty(localName = "CB_60_to_89_count")
    private String cb60To89Count;
	
	@JacksonXmlProperty(localName = "CB_1_to_29_count_last_6_Mon")
    private String cb1To29CountLast6Mon;
	
	@JacksonXmlProperty(localName = "CB_30_to_59_count_last_6_Mon")
    private String cb30To59CountLast6Mon;
	
	@JacksonXmlProperty(localName = "CB_60_to_89_count_last_6_Mon")
    private String cb60To89CountLast6Mon;
	
	@JacksonXmlProperty(localName = "Write_off")
    private String writeOff;
	
	@JacksonXmlProperty(localName = "Charge_Off")
    private String chargeOff;
	
	@JacksonXmlProperty(localName = "Credit_Score")
    private String creditScore;

}
