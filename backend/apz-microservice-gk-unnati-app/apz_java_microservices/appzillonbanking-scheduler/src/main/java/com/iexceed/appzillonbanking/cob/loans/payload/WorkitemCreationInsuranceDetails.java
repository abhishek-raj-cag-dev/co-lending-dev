package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationInsuranceDetails {
	
	@JacksonXmlProperty(localName = "custPrefix")
	private String custPrefix;
	
	@JacksonXmlProperty(localName = "Relationship_Others")
    private String relationshipOthers;
    
	@JacksonXmlProperty(localName = "Customer_ID")
    private String customerId;
    
	@JacksonXmlProperty(localName = "Insurance_for")
    private String insuranceFor;
    
	@JacksonXmlProperty(localName = "Nominee_Insurance")
    private String nomineeInsurance;
    
	@JacksonXmlProperty(localName = "DOB_of_Nominee")
    private String dobOfNominee;
    
	@JacksonXmlProperty(localName = "Age_of_Nominee")
    private String ageOfNominee;
    
	@JacksonXmlProperty(localName = "RShip_with_Nominee")
    private String rShipWithNominee;
    
	@JacksonXmlProperty(localName = "Gender")
    private String gender;
    
	@JacksonXmlProperty(localName = "Occupation")
    private String occupation;
    
	@JacksonXmlProperty(localName = "Line_1")
    private String line1;
    
	@JacksonXmlProperty(localName = "Line_2")
    private String line2;
    
	@JacksonXmlProperty(localName = "Line_3")
    private String line3;
    
	@JacksonXmlProperty(localName = "Landmark")
    private String landmark;
    
	@JacksonXmlProperty(localName = "Pincode")
    private String pincode;
    
	@JacksonXmlProperty(localName = "Area")
    private String area;
    
	@JacksonXmlProperty(localName = "City_Town_Village")
    private String cityTownVillage;
    
	@JacksonXmlProperty(localName = "District")
    private String district;
    
	@JacksonXmlProperty(localName = "State")
    private String state;
    
	@JacksonXmlProperty(localName = "Country")
    private String country;
    
	@JacksonXmlProperty(localName = "insertionOrderId")
    private String insertionOrderId;
    
	@JacksonXmlProperty(localName = "Cust_Name_list")
    private String custNameList;
    
	@JacksonXmlProperty(localName = "area_list")
    private String areaList;
    
	@JacksonXmlProperty(localName = "fid")
    private String fid;

}
