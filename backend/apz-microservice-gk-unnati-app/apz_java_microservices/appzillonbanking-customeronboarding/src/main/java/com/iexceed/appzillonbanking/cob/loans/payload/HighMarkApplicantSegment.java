package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HighMarkApplicantSegment {
	
	@JacksonXmlProperty(localName = "APPLICANT-NAME")
	private HighMarkApplicantName applicantName;
	
	@JacksonXmlProperty(localName = "DOB")
	private HighMarkApplicantDob dob;
	
	@JacksonXmlElementWrapper(localName = "IDS")
	@JacksonXmlProperty(localName = "ID")
	private List<HighMarkApplicantId> ids;
	
	@JacksonXmlElementWrapper(localName = "RELATIONS")
	@JacksonXmlProperty(localName = "RELATION")
	private List<HighMarkApplicantRelation> relations;
	
	@JacksonXmlElementWrapper(localName = "PHONES")
	@JacksonXmlProperty(localName = "PHONE")
	private List<HighMarkApplicantPhone> phones;
	
	@JacksonXmlProperty(localName = "GENDER")
	private String gender;
	
	@JacksonXmlProperty(localName = "MARITAL-STATUS")
	private String maritalStatus;
	
	@JacksonXmlProperty(localName = "ENTITY-ID")
	private String entityId;
	
	@JacksonXmlElementWrapper(localName = "ADDRESSES")
	@JacksonXmlProperty(localName = "ADDRESS")
	private List<HighMarkApplicantAddress> addesses;

}
