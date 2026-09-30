package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationAddressDetails {
	
	@JacksonXmlProperty(localName = "custPrefix")
	private String custPrefix;
	
	@JacksonXmlProperty(localName = "Name_of_Org_Employer")
    private String nameOfOrgEmployer;
    
	@JacksonXmlProperty(localName = "cust_type")
    private String custType;
    
	@JacksonXmlProperty(localName = "Address_Type")
    private String addressType;
    
	@JacksonXmlProperty(localName = "Copy_Address_From")
    private String copyAddressFrom;
    
	@JacksonXmlProperty(localName = "Location_Co_ordinates")
    private String locationCoOrdinates;
    
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
    
	@JacksonXmlProperty(localName = "Current_Residence_Proof")
    private String currentResidenceProof;
    
	@JacksonXmlProperty(localName = "Communication_address")
    private String communicationAddress;
    
	@JacksonXmlProperty(localName = "Residence_Ownership")
    private String residenceOwnership;
    
	@JacksonXmlProperty(localName = "Resi_Stabi_in_Prsnt_Address")
    private String resiStabiInPrsntAddress;
    
	@JacksonXmlProperty(localName = "Resi_Stabi_in_Prsnt_City")
    private String resiStabiInPrsntCity;
    
	@JacksonXmlProperty(localName = "Type_of_House")
    private String typeOfHouse;
    
	@JacksonXmlProperty(localName = "Area_of_House")
    private String areaOfHouse;
    
	@JacksonXmlProperty(localName = "insertionOrderId")
    private String insertionOrderId;
    
	@JacksonXmlProperty(localName = "Cust_Name_list")
    private String custNameList;
    
	@JacksonXmlProperty(localName = "area_list")
    private String areaList;
    
	@JacksonXmlProperty(localName = "Cro_ID")
    private String croId;
    
	@JacksonXmlProperty(localName = "cro_name")
    private String croName;
    
	@JacksonXmlProperty(localName = "fid")
    private String fid;

}
