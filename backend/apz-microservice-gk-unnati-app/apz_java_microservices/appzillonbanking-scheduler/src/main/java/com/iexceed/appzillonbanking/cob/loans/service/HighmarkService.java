package com.iexceed.appzillonbanking.cob.loans.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.loans.payload.HighMarkAddressDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.HighMarkApplicantAddress;
import com.iexceed.appzillonbanking.cob.loans.payload.HighMarkApplicantDob;
import com.iexceed.appzillonbanking.cob.loans.payload.HighMarkApplicantId;
import com.iexceed.appzillonbanking.cob.loans.payload.HighMarkApplicantName;
import com.iexceed.appzillonbanking.cob.loans.payload.HighMarkApplicantPhone;
import com.iexceed.appzillonbanking.cob.loans.payload.HighMarkApplicantRelation;
import com.iexceed.appzillonbanking.cob.loans.payload.HighMarkApplicantSegment;
import com.iexceed.appzillonbanking.cob.loans.payload.HighMarkApplicationSegment;
import com.iexceed.appzillonbanking.cob.loans.payload.HighMarkCheckRequest;
import com.iexceed.appzillonbanking.cob.loans.payload.HighMarkCheckRequestFields;
import com.iexceed.appzillonbanking.cob.loans.payload.HighMarkHeaderSegment;
import com.iexceed.appzillonbanking.cob.loans.payload.HighMarkMfiConsumer;
import com.iexceed.appzillonbanking.cob.loans.payload.HighMarkRequestExt;

public class HighmarkService {
	
	SimpleDateFormat inFormat = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat outFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	public String getRequestXml(HighMarkCheckRequest highmarkCheckRequest, Properties prop, String loanId)
			throws ParseException, JsonProcessingException {
		XmlMapper xmlMapper = new XmlMapper();
		HighMarkRequestExt highMarkRequest = new HighMarkRequestExt();
		highMarkRequest.setHeaderSegment(getRequestHeader(prop));
		highMarkRequest.setApplicantSegment(getRequestApplicant(highmarkCheckRequest.getRequestObj(), prop));
		highMarkRequest
				.setApplicationSegment(getRequestApplication(highmarkCheckRequest.getRequestObj(), prop, loanId));
		return xmlMapper.writeValueAsString(highMarkRequest);
	}

	private HighMarkHeaderSegment getRequestHeader(Properties prop) {
		HighMarkHeaderSegment headerSegment = new HighMarkHeaderSegment();
		headerSegment.setProductType(prop.getProperty(CobFlagsProperties.PRODUCT_TYPE.getKey()));
		headerSegment.setResFrmt("XML");
		headerSegment.setLosName("MainLoanProduct");
		headerSegment.setLosVender("genpact");
		headerSegment.setLosVersion(prop.getProperty(CobFlagsProperties.LOS_VERSION.getKey()));
		headerSegment.setUserId(prop.getProperty(CobFlagsProperties.USER_ID.getKey()));

		HighMarkMfiConsumer mfiConsumer = new HighMarkMfiConsumer();
		mfiConsumer.setIndv(prop.getProperty(CobFlagsProperties.INDV.getKey()));
		mfiConsumer.setScore(prop.getProperty(CobFlagsProperties.SCORE.getKey()));
		mfiConsumer.setGroup(prop.getProperty(CobFlagsProperties.GROUP.getKey()));
		mfiConsumer.setCnsIndv(prop.getProperty(CobFlagsProperties.CNS_INDV.getKey()));
		mfiConsumer.setCnsScore(prop.getProperty(CobFlagsProperties.CNS_SCORE.getKey()));
		mfiConsumer.setIoi(prop.getProperty(CobFlagsProperties.IOI.getKey()));
		headerSegment.setMfiConsumer(mfiConsumer);
		return headerSegment;
	}

	private HighMarkApplicantSegment getRequestApplicant(HighMarkCheckRequestFields highmarkCheckRequestFields,
			Properties prop) throws ParseException {
		HighMarkApplicantSegment applicantSegment = new HighMarkApplicantSegment();
		applicantSegment.setApplicantName(getApplicantNameDtls(highmarkCheckRequestFields));
		applicantSegment.setDob(getDobDtls(highmarkCheckRequestFields));
		applicantSegment.setIds(getApplicantIdDtls(highmarkCheckRequestFields));
		applicantSegment.setRelations(getApplicantRelationDtls(highmarkCheckRequestFields));
		applicantSegment.setPhones(getApplicantPhoneDtls(highmarkCheckRequestFields));
		applicantSegment.setGender(highmarkCheckRequestFields.getGender().toUpperCase());
		applicantSegment.setMaritalStatus(highmarkCheckRequestFields.getMaritalStatus().toUpperCase());
		applicantSegment.setEntityId(prop.getProperty(CobFlagsProperties.ENTITY_ID.getKey()));
		applicantSegment.setAddesses(getApplicantAddressDtls(highmarkCheckRequestFields));
		return applicantSegment;
	}

	private HighMarkApplicantName getApplicantNameDtls(HighMarkCheckRequestFields highmarkCheckRequestFields) {
		HighMarkApplicantName applicantName = new HighMarkApplicantName();
		applicantName.setName1(highmarkCheckRequestFields.getFirstName());
		applicantName.setName2(highmarkCheckRequestFields.getMiddleName());
		applicantName.setName3(highmarkCheckRequestFields.getLastName());
		applicantName.setName4("");
		applicantName.setName5("");
		return applicantName;
	}

	private HighMarkApplicantDob getDobDtls(HighMarkCheckRequestFields highmarkCheckRequestFields)
			throws ParseException {
		HighMarkApplicantDob dob = new HighMarkApplicantDob();
		dob.setDobDate(outFormat.format(inFormat.parse(highmarkCheckRequestFields.getDob())));
		dob.setAge(highmarkCheckRequestFields.getAge());
		dob.setAgeAsOn(outFormat.format(new Date()));
		return dob;
	}

	private List<HighMarkApplicantId> getApplicantIdDtls(HighMarkCheckRequestFields highmarkCheckRequestFields) {
		List<HighMarkApplicantId> ids = new ArrayList<>();
		HighMarkApplicantId primaryId = new HighMarkApplicantId();
		primaryId.setType("VoterID");
		primaryId.setValue(highmarkCheckRequestFields.getPrimaryKycId());
		ids.add(primaryId);
		if (null != highmarkCheckRequestFields.getSecondaryKycType()
				&& null != highmarkCheckRequestFields.getSecondaryKycId()) {
			HighMarkApplicantId secondaryId = new HighMarkApplicantId();
			String type = "VoterID";
			if (highmarkCheckRequestFields.getSecondaryKycType().equals("pan")) {
				type = "PAN";
			} else if (highmarkCheckRequestFields.getSecondaryKycType().equals("drivingLicense")) {
				type = "DRIVINGLICENCENO";
			} else if (highmarkCheckRequestFields.getSecondaryKycType().equals("passport")) {
				type = "PASSPORT";
			}
			secondaryId.setType(type);
			secondaryId.setValue(highmarkCheckRequestFields.getSecondaryKycId());
			ids.add(secondaryId);
		}
		return ids;
	}

	private List<HighMarkApplicantRelation> getApplicantRelationDtls(
			HighMarkCheckRequestFields highmarkCheckRequestFields) {
		List<HighMarkApplicantRelation> relations = new ArrayList<>();
		if (null != highmarkCheckRequestFields.getFathersName()) {
			HighMarkApplicantRelation father = new HighMarkApplicantRelation();
			father.setType("FATHER");
			father.setValue(highmarkCheckRequestFields.getFathersName());
			relations.add(father);
		}
		if (null != highmarkCheckRequestFields.getMothersName()) {
			HighMarkApplicantRelation mother = new HighMarkApplicantRelation();
			mother.setType("MOTHER");
			mother.setValue(highmarkCheckRequestFields.getMothersName());
			relations.add(mother);
		}
		if (null != highmarkCheckRequestFields.getSpouseName()) {
			HighMarkApplicantRelation spouse = new HighMarkApplicantRelation();
			spouse.setType("SPOUSE");
			spouse.setValue(highmarkCheckRequestFields.getSpouseName());
			relations.add(spouse);
		}
		return relations;
	}

	private List<HighMarkApplicantPhone> getApplicantPhoneDtls(HighMarkCheckRequestFields highmarkCheckRequestFields) {
		List<HighMarkApplicantPhone> phones = new ArrayList<>();
		HighMarkApplicantPhone phone = new HighMarkApplicantPhone();
		phone.setType("MOBILE");
		phone.setValue(highmarkCheckRequestFields.getMobileNo());
		phones.add(phone);
		return phones;
	}

	private List<HighMarkApplicantAddress> getApplicantAddressDtls(
			HighMarkCheckRequestFields highmarkCheckRequestFields) {
		List<HighMarkApplicantAddress> addresses = new ArrayList<>();
		for (HighMarkAddressDetails highmarkAddressDetails : highmarkCheckRequestFields.getAddressDetails()) {
			HighMarkApplicantAddress applicantAddress = new HighMarkApplicantAddress();
			applicantAddress.setType(highmarkAddressDetails.getType().toUpperCase());
			applicantAddress.setAddressLine1(highmarkAddressDetails.getAddressLine1()
					+ highmarkAddressDetails.getAddressLine2() + highmarkAddressDetails.getAddressLine3());
			applicantAddress.setCity(highmarkAddressDetails.getCity());
			applicantAddress.setState(highmarkAddressDetails.getState());
			applicantAddress.setPincode(highmarkAddressDetails.getPincode());
			addresses.add(applicantAddress);
		}
		return addresses;
	}

	private HighMarkApplicationSegment getRequestApplication(HighMarkCheckRequestFields highmarkCheckRequestFields,
			Properties prop, String highmarkApplicationId) {
		HighMarkApplicationSegment applicationSegment = new HighMarkApplicationSegment();
		applicationSegment.setLoanType(prop.getProperty(CobFlagsProperties.LOAN_TYPE.getKey()));
		applicationSegment.setLoanPurpose(highmarkCheckRequestFields.getLoanPurpose());
		applicationSegment.setApplicationDt(outFormat.format(new Date()));
		applicationSegment.setConsumerId(highmarkCheckRequestFields.getCustomerId());
		applicationSegment.setApplicationId(highmarkApplicationId);
		applicationSegment.setLoanAmount(highmarkCheckRequestFields.getLoanAmount());
		applicationSegment.setBranch(highmarkCheckRequestFields.getBranch());
		applicationSegment.setKendra(highmarkCheckRequestFields.getKendra());
		applicationSegment.setIfscCode(highmarkCheckRequestFields.getIfsc());
		applicationSegment.setBranchState(highmarkCheckRequestFields.getBranchState());
		applicationSegment.setOwnershipIndicator("OWR");
		return applicationSegment;
	}

}
