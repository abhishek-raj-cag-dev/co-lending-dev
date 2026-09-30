package com.iexceed.appzillonbanking.cob.loans.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.loans.payload.BRECBCheckRequestExt;
import com.iexceed.appzillonbanking.cob.loans.payload.BRECBReportRequest;
import com.iexceed.appzillonbanking.cob.loans.payload.BRECBReportRequestFields;
import com.iexceed.appzillonbanking.cob.loans.payload.FetchDocumentsRequest;
import com.iexceed.appzillonbanking.cob.loans.payload.WipDedupeRequestExt;
import com.iexceed.appzillonbanking.cob.loans.payload.WipDedupeRequestFields;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationAddressDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationApplnQuestionDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationAshaCmplxCbDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationBankingDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationBorrowingDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationChequeDtls;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationCibilIntegrationUtility;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationCustDtls;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationCustOtherSourceDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationDocumentDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationExtDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationFinancialAnalysis;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationHighmarkDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationInsuranceDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationLeadDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationLoanInfo;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationLoanOtherInfo;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationMetadataDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationOccupationDetails;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationOwnedLandDtls;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationReferenceDtls;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationRequest;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationRequestAddrssDtls;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationRequestCustDtls;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationRequestDocDtls;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationRequestExt;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationRequestFields;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationRequestHighmarkDtls;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationRequestInsuranceDtls;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationRequestOccupationDtls;
import com.iexceed.appzillonbanking.cob.loans.payload.WorkitemCreationUnnatiCbValues;
import com.iexceed.appzillonbanking.cob.loans.service.LoanService;
import reactor.core.publisher.Mono;

public class NewgenService {
	private static final Logger logger = LogManager.getLogger(NewgenService.class);

	private LoanService loanService;

	public NewgenService() {
	}

	@Autowired
	public NewgenService(LoanService loanService) {
		this.loanService = loanService;
	}

	SimpleDateFormat inFormat = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat outFormatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public String getWorkitemCreationRequestXml(WorkitemCreationRequest workitemCreationRequest)
			throws ParseException, JsonProcessingException {
		logger.debug("workitemCreationRequest : {}" , workitemCreationRequest);
		XmlMapper xmlMapper = new XmlMapper();
		WorkitemCreationRequestExt workitemCreationRequestExt = new WorkitemCreationRequestExt();
		workitemCreationRequestExt.setLeadDetails(getLeadDetails(workitemCreationRequest.getRequestObj()));
		workitemCreationRequestExt.setLoanInfo(getLoanInfoDtls(workitemCreationRequest.getRequestObj()));
		workitemCreationRequestExt.setLoanOtherInfo(getLoanOtherInfoDtls());
		workitemCreationRequestExt.setFinancialAnalysis(getFinancialAnalysisDtls());
		workitemCreationRequestExt.setCustDtls(getCustDtls(workitemCreationRequest.getRequestObj()));
		workitemCreationRequestExt.setChequeDtls(getCheqDtls());
		workitemCreationRequestExt.setOwnedLandDtls(getOwnedLandDtls());
		workitemCreationRequestExt.setReferenceDtls(getReferenceDtls());
		workitemCreationRequestExt.setExtDetails(getExtDtls(workitemCreationRequest.getRequestObj()));
		workitemCreationRequestExt.setAddressDtls(getAddressDtls(workitemCreationRequest.getRequestObj()));
		workitemCreationRequestExt.setBankingDtl(getBankingDtls(workitemCreationRequest.getRequestObj()));
		workitemCreationRequestExt.setInsuranceDtls(getInsuranceDtls(workitemCreationRequest.getRequestObj()));
		workitemCreationRequestExt.setCustOtherSourceDtls(getCustOtherSource());
		workitemCreationRequestExt.setApplnQuestionDtls(getApplnQuestion());
		workitemCreationRequestExt.setOccupationDtls(getOccupationDtls(workitemCreationRequest.getRequestObj()));
		workitemCreationRequestExt.setBorrowingDtls(getBorrowingDtls(workitemCreationRequest.getRequestObj()));
		workitemCreationRequestExt.setMetadataDtls(getMetadata(workitemCreationRequest.getRequestObj()));
		workitemCreationRequestExt.setHighmarkDtls(getHighmarkDtls(workitemCreationRequest.getRequestObj()));
		workitemCreationRequestExt.setDocumentDtls(getDocDtls(workitemCreationRequest.getRequestObj()));
		logger.debug("workitemCreationRequestExt.getDocumentDtls().isEmpty(): {}", workitemCreationRequestExt.getDocumentDtls().isEmpty());
		logger.debug("workitemCreationRequestExt.getDocumentDtls(): {}", workitemCreationRequestExt.getDocumentDtls());
		if (workitemCreationRequestExt.getDocumentDtls().isEmpty())
			return StringUtils.EMPTY;
		return xmlMapper.writeValueAsString(workitemCreationRequestExt).replace("<WI-REQ>", "").replace("</WI-REQ>",
				"");
	}

	private WorkitemCreationLeadDetails getLeadDetails(WorkitemCreationRequestFields workitemCreationRequestFields)
			throws ParseException {
		WorkitemCreationLeadDetails leadDetails = new WorkitemCreationLeadDetails();
		leadDetails.setPreScreeningStatus("POSSITIVE");
		leadDetails.setPreviousLoanId("GLKM");
		leadDetails.setNoOfYrsRel(workitemCreationRequestFields.getNoOfYrsRel());
		leadDetails.setAreaMapping("1");
		leadDetails.setCustomerPar("0");
		leadDetails.setSourcingOfficer(workitemCreationRequestFields.getKmName());
		leadDetails.setTypeOfCustomer("1");
		leadDetails.setRelWithCagl("2");
		leadDetails.setCustomerId(workitemCreationRequestFields.getCustomerId());
		leadDetails.setTitle(workitemCreationRequestFields.getTitle());
		leadDetails.setFirstName(workitemCreationRequestFields.getFirstName());
		leadDetails.setMiddleName(workitemCreationRequestFields.getMiddleName());
		leadDetails.setLastName(workitemCreationRequestFields.getLastName());
		leadDetails.setGender(workitemCreationRequestFields.getGender());
		leadDetails.setDob(workitemCreationRequestFields.getDob());
		leadDetails.setPrimaryPhoneNo(workitemCreationRequestFields.getMobileNo());
		leadDetails.setNatureOfBussEmp(workitemCreationRequestFields.getNatureOfBussEmp());
		leadDetails.setBussEmpActivity(workitemCreationRequestFields.getBussEmpActivity());
		leadDetails.setMainLoanProduct("");
		leadDetails.setMainLoanAmount("");
		leadDetails.setFreshRepeat("1");
		leadDetails.setSourceOfLead("7");
		leadDetails.setBdoName("");
		leadDetails.setKendraId(workitemCreationRequestFields.getKendraId());
		leadDetails.setKendraName(workitemCreationRequestFields.getKendraName());
		leadDetails.setKmName(workitemCreationRequestFields.getKmName());
		leadDetails.setKmGkId(workitemCreationRequestFields.getKmId());
		leadDetails.setGlBranchName(workitemCreationRequestFields.getBranchName());
		leadDetails.setGlBranchId(workitemCreationRequestFields.getBranchId());
		leadDetails.setBranchName(workitemCreationRequestFields.getBranchName());
		leadDetails.setArea(workitemCreationRequestFields.getArea());
		leadDetails.setRegion(workitemCreationRequestFields.getRegion());
		leadDetails.setRfBranchDesc(workitemCreationRequestFields.getBranchId());
		leadDetails.setCrmName("");
		leadDetails.setCrmGkId("");
		leadDetails.setGroupId(workitemCreationRequestFields.getGroupId());
		leadDetails.setKendraSize(workitemCreationRequestFields.getKendraSize());
		leadDetails.setKendraVintageYrs(workitemCreationRequestFields.getKendraVintageYrs());
		leadDetails.setGroupSize(workitemCreationRequestFields.getGroupSize());
		leadDetails.setKendraParStatus(workitemCreationRequestFields.getKendraParStatus());
		leadDetails.setKendraMeetingFreq(workitemCreationRequestFields.getKendraMeetingFreq());
		leadDetails.setKendraMeetingDay(workitemCreationRequestFields.getKendraMeetingDay());
		leadDetails.setLoanType("9");
		return leadDetails;
	}

	private WorkitemCreationLoanInfo getLoanInfoDtls(WorkitemCreationRequestFields workitemCreationRequestFields) {
		WorkitemCreationLoanInfo loanInfo = new WorkitemCreationLoanInfo();
		loanInfo.setDdFavDetCustPrefix("");
		loanInfo.setPurposeOfLoanOthers("");
		loanInfo.setGrtLoanType("9");
		loanInfo.setGrtDdFav("");
		loanInfo.setRelWithCagl("2");
		loanInfo.setLoanType("9");
		loanInfo.setRequestedLoanAmount(workitemCreationRequestFields.getRequestedLoanAmount());
		loanInfo.setLoanTenureInMonths(workitemCreationRequestFields.getLoanTenureInMonths());
		loanInfo.setPurposeOfLoan(workitemCreationRequestFields.getPurposeOfLoan());
		loanInfo.setSubPurposeOfLoan(workitemCreationRequestFields.getSubPurposeOfLoan());
		loanInfo.setRateOfInterest(workitemCreationRequestFields.getRateOfInterest());
		loanInfo.setLangForComm(workitemCreationRequestFields.getLangForComm());
		loanInfo.setModeOfDisbursement(workitemCreationRequestFields.getModeOfDisbursement());
		loanInfo.setPddStatus("");
		loanInfo.setCroName("");
		loanInfo.setCroID("");
		loanInfo.setOverallGrossIncome("");
		loanInfo.setOverallNetIncome("");
		loanInfo.setTotObliConsidered("");
		loanInfo.setActualFoirOnGI("");
		loanInfo.setActualFoirOnNI("");
		loanInfo.setTotalAssetCost("");
		loanInfo.setMarginMoney("");
		loanInfo.setDiffSalTurnoverCaReport("");
		loanInfo.setFinalLoanAmount("");
		loanInfo.setLtv("");
		loanInfo.setTotalExposure("");
		loanInfo.setRepaymentFrequency(workitemCreationRequestFields.getRepaymentFrequency());
		return loanInfo;
	}

	private WorkitemCreationLoanOtherInfo getLoanOtherInfoDtls() {
		WorkitemCreationLoanOtherInfo loanOtherInfo = new WorkitemCreationLoanOtherInfo();
		loanOtherInfo.setQcSamplingRequired("");
		loanOtherInfo.setQcPlaceOfPd("");
		loanOtherInfo.setQcPersonMetDuringVisit("");
		loanOtherInfo.setQcRecommendation("");
		loanOtherInfo.setPdMainLoanId("");
		loanOtherInfo.setPdLoanAccountId("");
		loanOtherInfo.setPdNeftDdAmount("");
		loanOtherInfo.setPdPriBankAccHolder("");
		loanOtherInfo.setPdGlRfOutstandingAmt("");
		loanOtherInfo.setPd1Place("");
		loanOtherInfo.setPd1PersonMetDuringVisit("");
		loanOtherInfo.setFFamilyType("");
		loanOtherInfo.setFTotalFamilyMembers("");
		loanOtherInfo.setFChildrenBelow18Years("");
		loanOtherInfo.setFNoOfEarningMembers("");
		loanOtherInfo.setFMembersAbove60Years("");
		loanOtherInfo.setLObTotOtherObliConsidered("");
		loanOtherInfo.setLObTotObliConsidered("");
		loanOtherInfo.setLObCurrentCaglExposure("");
		loanOtherInfo.setLObCurrentCaglEmi("");
		loanOtherInfo.setLObToBeConsidered("");
		loanOtherInfo.setFOtherMemIncome("");
		loanOtherInfo.setRecommendation("");
		loanOtherInfo.setLobTotGkObliConsidered("");
		loanOtherInfo.setLobConsiderForFoirDevi("");
		loanOtherInfo.setPdtAmtCreditedToPbhAcc("");
		loanOtherInfo.setPdtCustAwareOfDisbursement("");
		loanOtherInfo.setPdtNoOfAttemptsToContact("");
		loanOtherInfo.setPdtReceivedYourVehicle("");
		loanOtherInfo.setPdtDateOfDelivery("");
		return loanOtherInfo;
	}

	private WorkitemCreationFinancialAnalysis getFinancialAnalysisDtls() {
		WorkitemCreationFinancialAnalysis financialAnalysis = new WorkitemCreationFinancialAnalysis();
		financialAnalysis.setPid("");
		financialAnalysis.setInsertionOrderId("0");
		financialAnalysis.setBfaMonthlySalesOfBuss("");
		financialAnalysis.setBfaOtherBussIncome("");
		financialAnalysis.setBfaTotalBussIncome("");
		financialAnalysis.setBfaCostOfSale("");
		financialAnalysis.setBfaRentPaid("");
		financialAnalysis.setBfaSalWagesPaid("");
		financialAnalysis.setBfaTransportCommuExpenses("");
		financialAnalysis.setBfaBussLoanRepayments("");
		financialAnalysis.setBfaOtherBussExpenses("");
		financialAnalysis.setBfaTotalBussExpenses("");
		financialAnalysis.setBfaNetBussIncome("");
		financialAnalysis.setPfaSalaryIncome("");
		financialAnalysis.setPfaOfMemberIncome("");
		financialAnalysis.setPfaRentalIncome("");
		financialAnalysis.setPfaPensionIncome("");
		financialAnalysis.setPfaAgriIncome("");
		financialAnalysis.setPfaSeasonalIncome("");
		financialAnalysis.setPfaOtherIncome("");
		financialAnalysis.setPfaTotalIncome("");
		financialAnalysis.setPfaRentPaid("");
		financialAnalysis.setPfaFoodExpenses("");
		financialAnalysis.setPfaTransportAndCommu("");
		financialAnalysis.setPfaPersonalLoanRepayments("");
		financialAnalysis.setPfaChildrenEduExpenses("");
		financialAnalysis.setPfaMedicalExpenses("");
		financialAnalysis.setPfaOtherExpenses("");
		financialAnalysis.setPfaTotalHhExpense("");
		financialAnalysis.setPfaNetHhIncome("");
		financialAnalysis.setBfaUtilityExpenses("");
		financialAnalysis.setPfaTotalNetIncome("");
		financialAnalysis.setPfaUtilityPaid("");
		financialAnalysis.setCustNameList("");
		financialAnalysis.setFid("");
		return financialAnalysis;
	}

	private List<WorkitemCreationCustDtls> getCustDtls(WorkitemCreationRequestFields requestFields)
			throws ParseException {
		List<WorkitemCreationCustDtls> custDtls = new ArrayList<>();
		for (WorkitemCreationRequestCustDtls requestCustDtls : requestFields.getCustDtls()) {
			WorkitemCreationCustDtls custDtl = new WorkitemCreationCustDtls();
			custDtl.setPid("");
			custDtl.setPassportFileNo("");
			custDtl.setPassportDoi("");
			custDtl.setPassportVfyFlag(requestCustDtls.getPassportVfyFlag());
			custDtl.setRelationshipOthers("");
			custDtl.setAadharVfyStatus("");
			custDtl.setAaadhar("");
			custDtl.setNrega("");
			custDtl.setLandline(requestCustDtls.getLandline());
			custDtl.setOtpStatus(requestCustDtls.getOtpStatus());
			custDtl.setCustomerType(requestCustDtls.getCustomerType());
			custDtl.setCustomerName(requestCustDtls.getCustomerName());
			custDtl.setCustomerId(requestCustDtls.getCustomerId());
			if ("1".equalsIgnoreCase(requestCustDtls.getCustomerType()))
				custDtl.setCustomerId(requestFields.getCustomerId());
			custDtl.setTitle(requestCustDtls.getTitle());
			custDtl.setFirstName(requestCustDtls.getFirstName());
			custDtl.setMiddleName(requestCustDtls.getMiddleName());
			custDtl.setLastName(requestCustDtls.getLastName());
			custDtl.setFullName(requestCustDtls.getFullName());
			custDtl.setGender(requestCustDtls.getGender());
			if (null != requestCustDtls.getDob() && (!requestCustDtls.getDob().equals(""))) {
				custDtl.setDateOfBirth(outFormatDateTime.format(outFormat.parse(requestCustDtls.getDob())));
			} else {
				custDtl.setDateOfBirth("");
			}
			custDtl.setAge(requestCustDtls.getAge());
			custDtl.setMobileNo(requestCustDtls.getMobileNo());
			custDtl.setMaritalStatus(requestCustDtls.getMaritalStatus());
			custDtl.setFatherName(requestCustDtls.getFathersName());
			custDtl.setSpouseName(requestCustDtls.getSpouseName());
			custDtl.setMotherMaidenName(requestCustDtls.getMothersName());
			custDtl.setSecMobileNo(requestCustDtls.getLandline());
			custDtl.setEmailId(requestCustDtls.getEmailId());
			custDtl.setReligion(requestCustDtls.getReligion());
			custDtl.setOthersReligion("");
			custDtl.setCaste(requestCustDtls.getCaste());
			custDtl.setEducation(requestCustDtls.getEducation());
			custDtl.setVoterIdNo(requestCustDtls.getPrimaryKycId());
			custDtl.setSecVoterId(requestCustDtls.getAlternateKycId());
			if (requestCustDtls.getSecondaryKycType().equals(Constants.PAN_VALUE)) {
				custDtl.setPan(requestCustDtls.getSecondaryKycId());
				custDtl.setDrivingLicenseNo("");
				custDtl.setPassportNo("");
			} else if (requestCustDtls.getSecondaryKycType().equals(Constants.DRIVING_LICENSE_VALUE)) {
				custDtl.setPan("");
				custDtl.setDrivingLicenseNo(requestCustDtls.getSecondaryKycId());
				custDtl.setPassportNo("");
			} else if (requestCustDtls.getSecondaryKycType().equals(Constants.PASSPORT_VALUE)) {
				custDtl.setPan("");
				custDtl.setDrivingLicenseNo("");
				custDtl.setPassportNo(requestCustDtls.getSecondaryKycId());
			} else {
				custDtl.setPan("");
				custDtl.setDrivingLicenseNo("");
				custDtl.setPassportNo("");
			}
			custDtl.setLpgGasConnectionNo("");
			custDtl.setOtherGovtIdDesc("");
			custDtl.setGovtIdNo("");

			custDtl.setOccupation(requestCustDtls.getOccupation());
			custDtl.setInsertionOrderId("0");
			if (requestCustDtls.getCustomerType().equals("1")) {
				custDtl.setCustPrefix("A01");
				custDtl.setNoOfYrsRel(requestFields.getNoOfYrsRel());
				custDtl.setExistingCustomer("1");
				custDtl.setRelationshipToApplicant("14");
			} else {
				custDtl.setCustPrefix("C01");
				custDtl.setNoOfYrsRel("");
				custDtl.setExistingCustomer("2");
				custDtl.setRelationshipToApplicant(requestCustDtls.getApplicantRel());
			}
			custDtl.setRationDistrict("");
			custDtl.setRationTaluk("");
			custDtl.setRationShop("");
			custDtl.setCkyc(requestCustDtls.getCkyc());
			logger.debug("custDtl.tostring() : " + custDtl.toString());
			custDtls.add(custDtl);
		}
		logger.debug("custDtls.tostring() : " + custDtls.toString());
		return custDtls;
	}

	private WorkitemCreationChequeDtls getCheqDtls() {
		WorkitemCreationChequeDtls chequeDtls = new WorkitemCreationChequeDtls();
		chequeDtls.setType("1");
		chequeDtls.setChequeNumber("");
		chequeDtls.setChequeDate("");
		chequeDtls.setAmount("");
		chequeDtls.setInsertionOrderId("0");
		chequeDtls.setPrimaryBankName("");
		chequeDtls.setPrimaryBranchName("");
		chequeDtls.setSno("1");
		chequeDtls.setPid("");
		return chequeDtls;
	}

	private WorkitemCreationOwnedLandDtls getOwnedLandDtls() {
		WorkitemCreationOwnedLandDtls ownedLandDtls = new WorkitemCreationOwnedLandDtls();
		ownedLandDtls.setOthers("");
		ownedLandDtls.setAssetType("");
		ownedLandDtls.setAssetItems("");
		ownedLandDtls.setNoOfUnit("");
		ownedLandDtls.setEstimatedValue("");
		ownedLandDtls.setInsertionOrderId("0");
		ownedLandDtls.setPid("");
		return ownedLandDtls;
	}

	private WorkitemCreationReferenceDtls getReferenceDtls() {
		WorkitemCreationReferenceDtls referenceDtls = new WorkitemCreationReferenceDtls();
		referenceDtls.setReferenceNo("");
		referenceDtls.setName("");
		referenceDtls.setCustomerId("");
		referenceDtls.setRshipWithApplicant("");
		referenceDtls.setRshipInYears("");
		referenceDtls.setMobileNo("");
		referenceDtls.setResiAddress("");
		referenceDtls.setInsertionOrderId("0");
		return referenceDtls;
	}

	private WorkitemCreationExtDetails getExtDtls(WorkitemCreationRequestFields requestFields) {
		WorkitemCreationExtDetails extDtls = new WorkitemCreationExtDetails();
		extDtls.setProductTypeDesc("");
		extDtls.setBranchName(requestFields.getBranchName());
		extDtls.setArea(requestFields.getArea());
		extDtls.setRegion(requestFields.getRegion());
		extDtls.setBranchNameDesc(requestFields.getBranchName());
		extDtls.setRfBranchName("");
		extDtls.setGstin("Gstijn");
		extDtls.setUdyamRegisNum("Udayam_test");
		extDtls.setRepaymentFreq(requestFields.getRepaymentFrequency());
		extDtls.setRoi(requestFields.getRateOfInterest());
		extDtls.setInsuranceForCoapplicant(requestFields.getCoApplicantInsuranceReq());
		extDtls.setIexceedFlag("Y");
		extDtls.setLeadInitiatedFrom("IEXCEED");
		extDtls.setApplicantName(requestFields.getFirstName());
		extDtls.setProductName("UNNATHI");
		if (requestFields.getRelApplication() == null || "".equals(requestFields.getRelApplication())) {
			extDtls.setProductType(Constants.GRAMEEN_UNNATI_LOAN);
		} else {
			extDtls.setProductType(Constants.GRAMEEN_RENEWAL_LOAN);
		}
		extDtls.setActDate(requestFields.getActivationDate().toString());
		for (WorkitemCreationRequestHighmarkDtls requestHighmarkDtls : requestFields.getHighmarkDtls()) {
			if (!requestHighmarkDtls.getCustomerType().equals("1")) {
				String runningEmi = requestHighmarkDtls.getFoir();
				extDtls.setRunningEMI(runningEmi);
				
				extDtls.setApprovedAmount(requestHighmarkDtls.getApprovedAmount());
				extDtls.setFoir(requestHighmarkDtls.getFoirPercentage());
			}
		}
		return extDtls;
	}

	private List<WorkitemCreationAddressDetails> getAddressDtls(WorkitemCreationRequestFields requestFields) {
		List<WorkitemCreationAddressDetails> addressDtls = new ArrayList<>();
		for (WorkitemCreationRequestAddrssDtls requestAddressDtls : requestFields.getAddressDtls()) {
			WorkitemCreationAddressDetails addressDtl = new WorkitemCreationAddressDetails();
			if (requestAddressDtls.getCustomerType().equals("1")) {
				addressDtl.setCustPrefix("A01");
			} else {
				addressDtl.setCustPrefix("C01");
			}
			addressDtl.setNameOfOrgEmployer(requestAddressDtls.getNameOfOrgEmployer());
			addressDtl.setCustType(requestAddressDtls.getCustomerType());
			addressDtl.setAddressType(requestAddressDtls.getAddressType());
			addressDtl.setCopyAddressFrom("");
			addressDtl.setLocationCoOrdinates(requestAddressDtls.getLocationCoOrdinates());
			addressDtl.setLine1(requestAddressDtls.getLine1());
			addressDtl.setLine2(requestAddressDtls.getLine2());
			addressDtl.setLine3(requestAddressDtls.getLine3());
			addressDtl.setLandmark(requestAddressDtls.getLandmark());
			addressDtl.setPincode(requestAddressDtls.getPincode());
			addressDtl.setArea(requestAddressDtls.getArea());
			addressDtl.setCityTownVillage(requestAddressDtls.getCityTownVillage());
			addressDtl.setDistrict(requestAddressDtls.getDistrict());
			addressDtl.setState(requestAddressDtls.getState());
			addressDtl.setCountry(requestAddressDtls.getCountry());
			addressDtl.setCurrentResidenceProof(requestAddressDtls.getCurrentResidenceProof());
			if (requestAddressDtls.getCommunicationAddress().equals("Y")) {
				addressDtl.setCommunicationAddress("1");
			} else {
				addressDtl.setCommunicationAddress("2");
			}
			addressDtl.setResidenceOwnership(requestAddressDtls.getResidenceOwnership());
			addressDtl.setResiStabiInPrsntAddress(requestAddressDtls.getResiStabiInPrsntAddress());
			addressDtl.setResiStabiInPrsntCity(requestAddressDtls.getResiStabiInPrsntCity());
			addressDtl.setTypeOfHouse(requestAddressDtls.getTypeOfHouse());
			addressDtl.setAreaOfHouse(requestAddressDtls.getAreaOfHouse());
			addressDtl.setInsertionOrderId("0");
			addressDtl.setCustNameList("");
			addressDtl.setAreaList("");
			addressDtl.setCroId("");
			addressDtl.setCroName("");
			addressDtl.setFid("");
			addressDtls.add(addressDtl);
		}
		return addressDtls;
	}

	private WorkitemCreationBankingDetails getBankingDtls(WorkitemCreationRequestFields requestFields) {
		WorkitemCreationBankingDetails bankingDtl = new WorkitemCreationBankingDetails();
		bankingDtl.setCustPrefix("A01");
		bankingDtl.setCustomerId(requestFields.getCustomerId());
		bankingDtl.setAccountHolder("1");
		bankingDtl.setTypeOfAccount(requestFields.getBankingDtl().getTypeOfAcc());
		bankingDtl.setCategoryOfBusiness("");
		bankingDtl.setNameAsPerBankAccount(requestFields.getBankingDtl().getNameAsPerBankAcc());
		bankingDtl.setIfscCode(requestFields.getBankingDtl().getIfscCode());
		bankingDtl.setBankName(requestFields.getBankingDtl().getBankName());
		bankingDtl.setBranchName(requestFields.getBankingDtl().getBranchName());
		bankingDtl.setBankBranchPincode(requestFields.getBankingDtl().getPincode());
		bankingDtl.setMicrCode("");
		bankingDtl.setAccountNumber(requestFields.getBankingDtl().getAccNo());
		bankingDtl.setBankingSince("");
		bankingDtl.setPrimaryBankAccount("");
		bankingDtl.setUpi("");
		bankingDtl.setInsertionOrderId("0");
		bankingDtl.setCustNameList("");
		bankingDtl.setFid("");
		return bankingDtl;
	}

	private List<WorkitemCreationInsuranceDetails> getInsuranceDtls(WorkitemCreationRequestFields requestFields)
			throws ParseException {
		List<WorkitemCreationInsuranceDetails> insuranceDtls = new ArrayList<>();
		for (WorkitemCreationRequestInsuranceDtls requestInsuranceDtls : requestFields.getInsuranceDtls()) {
			WorkitemCreationInsuranceDetails insuranceDtl = new WorkitemCreationInsuranceDetails();
			if (requestInsuranceDtls.getCustomerType().equals("1")) {
				insuranceDtl.setCustPrefix("A01");
			} else {
				insuranceDtl.setCustPrefix("C01");
			}
			insuranceDtl.setRelationshipOthers("");
			insuranceDtl.setCustomerId(requestInsuranceDtls.getCustomerId());
			insuranceDtl.setInsuranceFor(requestInsuranceDtls.getInsuranceFor());
			insuranceDtl.setNomineeInsurance(requestInsuranceDtls.getNomineeInsurance());
			if (null != requestInsuranceDtls.getNomineeDob() && (!requestInsuranceDtls.getNomineeDob().equals(""))) {
				insuranceDtl.setDobOfNominee(
						outFormatDateTime.format(inFormat.parse(requestInsuranceDtls.getNomineeDob())));
			} else {
				insuranceDtl.setDobOfNominee("");
			}
			insuranceDtl.setAgeOfNominee(requestInsuranceDtls.getNomineeAge());
			insuranceDtl.setRShipWithNominee(requestInsuranceDtls.getNomineeRelationship());
			insuranceDtl.setGender("");
			insuranceDtl.setOccupation("");
			insuranceDtl.setLine1("");
			insuranceDtl.setLine2("");
			insuranceDtl.setLine3("");
			insuranceDtl.setLandmark("");
			insuranceDtl.setPincode("");
			insuranceDtl.setArea("");
			insuranceDtl.setCityTownVillage("");
			insuranceDtl.setDistrict("");
			insuranceDtl.setState("");
			insuranceDtl.setCountry("");
			insuranceDtl.setInsertionOrderId("0");
			insuranceDtl.setCustNameList("");
			insuranceDtl.setAreaList("");
			insuranceDtl.setFid("");
			insuranceDtls.add(insuranceDtl);
		}
		return insuranceDtls;
	}

	private WorkitemCreationCustOtherSourceDetails getCustOtherSource() {
		WorkitemCreationCustOtherSourceDetails custOtherSourceDtls = new WorkitemCreationCustOtherSourceDetails();
		custOtherSourceDtls.setCustPrefix("A01");
		custOtherSourceDtls.setInsertionOrderId("0");
		custOtherSourceDtls.setCustNameList("");
		custOtherSourceDtls.setCustomerType("");
		custOtherSourceDtls.setOtherSrcOfIncome("");
		custOtherSourceDtls.setOtherSrcAnnualIncome("");
		custOtherSourceDtls.setFid("");
		return custOtherSourceDtls;
	}

	private WorkitemCreationApplnQuestionDetails getApplnQuestion() {
		WorkitemCreationApplnQuestionDetails applnQuestionDtls = new WorkitemCreationApplnQuestionDetails();
		applnQuestionDtls.setPid("");
		applnQuestionDtls.setQuestions("");
		applnQuestionDtls.setOptions("");
		applnQuestionDtls.setRemarks("");
		applnQuestionDtls.setInsertionOrderId("0");
		applnQuestionDtls.setCustPrefix("A01");
		return applnQuestionDtls;
	}

	private List<WorkitemCreationOccupationDetails> getOccupationDtls(WorkitemCreationRequestFields requestFields)
			throws ParseException {
		List<WorkitemCreationOccupationDetails> occupationDtls = new ArrayList<>();
		int i = 0;
		for (WorkitemCreationRequestOccupationDtls requestOccupationDtls : requestFields.getOccupationDtls()) {
			WorkitemCreationOccupationDetails occupationDtl = new WorkitemCreationOccupationDetails();
			if (requestOccupationDtls.getCustomerType().equals("1")) {
				occupationDtl.setCustPrefix("A01");
			} else {
				occupationDtl.setCustPrefix("C01");
			}
			occupationDtl.setBussEmpEntityType(requestOccupationDtls.getBussEmpEntityType());
			occupationDtl.setNatureOfBussEmp(requestOccupationDtls.getNatureOfBussEmp());
			occupationDtl.setBussEmpActivity(requestOccupationDtls.getBussEmpActivity());
			occupationDtl.setStreetVendor(requestOccupationDtls.getStreetVendor());
			if (null != requestOccupationDtls.getBussEmpStartDate()
					&& (!requestOccupationDtls.getBussEmpStartDate().equals(""))) {
				occupationDtl.setBussEmpStartDate(
						outFormatDateTime.format(inFormat.parse(requestOccupationDtls.getBussEmpStartDate())));
			} else {
				occupationDtl.setBussEmpStartDate("");
			}

			occupationDtl.setBussEmpVintage(requestOccupationDtls.getBussEmpVintage());
			occupationDtl.setBussAddressProof(requestOccupationDtls.getBussAddressProof());
			occupationDtl.setNameOfOrgEmployer(requestOccupationDtls.getNameOfOrgEmployer());
			occupationDtl.setEmpProof(requestOccupationDtls.getEmpProof());
			occupationDtl.setBussPremiseArea("");
			occupationDtl.setBussPremiseOwnership(requestOccupationDtls.getBussPremiseOwnership());
			occupationDtl.setModeOfIncome(requestOccupationDtls.getModeOfIncome());
			occupationDtl.setFreqOfIncome(requestOccupationDtls.getFreqOfIncome());
			occupationDtl.setAnnualIncome(requestOccupationDtls.getAnnualIncome());
			occupationDtl.setOtherSrcOfIncome(requestOccupationDtls.getOtherSrcOfIncome());
			occupationDtl.setOtherSrcAnnualIncome(requestOccupationDtls.getOtherSrcAnnualIncome());
			occupationDtl.setInsertionOrderID(Integer.toString(i));
			occupationDtl.setCustomerName(requestOccupationDtls.getCustomerName());
			occupationDtl.setCustomerType(requestOccupationDtls.getCustomerType());
			occupationDtl.setCustNameList("");
			occupationDtl.setFid("");
			occupationDtl.setBussAssetOthers("");
			occupationDtl.setGrtNatureEmp("");
			occupationDtl.setGrtBusinessEmp("");
			occupationDtl.setBussAssetDetails("");
			occupationDtls.add(occupationDtl);
			i++;
		}
		return occupationDtls;
	}

	private WorkitemCreationBorrowingDetails getBorrowingDtls(WorkitemCreationRequestFields requestFields) {
		WorkitemCreationBorrowingDetails borrowingDtl = new WorkitemCreationBorrowingDetails();
		borrowingDtl.setCustPrefix("A01");
		borrowingDtl.setNameOfFinancier("");
		borrowingDtl.setLoanAmount(requestFields.getRequestedLoanAmount());
		borrowingDtl.setPos("");
		borrowingDtl.setEmi("");
		borrowingDtl.setInsertionOrderId("0");
		borrowingDtl.setApplicantType("1");
		borrowingDtl.setCustNameList("");
		borrowingDtl.setFid("");
		return borrowingDtl;
	}

	private WorkitemCreationMetadataDetails getMetadata(WorkitemCreationRequestFields requestFields) {
		WorkitemCreationMetadataDetails metadataDtl = new WorkitemCreationMetadataDetails();
		metadataDtl.setTxnId(requestFields.getTxnId());
		metadataDtl.setApplnRefNo(requestFields.getApplnRefNo());
		metadataDtl.setPid(requestFields.getPid());
		return metadataDtl;
	}

	private WorkitemCreationHighmarkDetails getHighmarkDtls(WorkitemCreationRequestFields requestFields)
			throws ParseException {
		WorkitemCreationHighmarkDetails highmarkDetails = new WorkitemCreationHighmarkDetails();
		Calendar cal = Calendar.getInstance();

		List<WorkitemCreationAshaCmplxCbDetails> ashaCmplxCbDetails = new ArrayList<>();
		List<WorkitemCreationUnnatiCbValues> workitemCreationUnnatiCbValues = new ArrayList<>();
		List<WorkitemCreationCibilIntegrationUtility> creationCibilIntegrationUtilities = new ArrayList<>();
		String actualApplicantIndebtedness = "";
		String actualCoApplicantIndebtedness = "";
		String actualApplicantIndebtednessLimit = "";
		String actualCoApplicantIndebtednessLimit = "";
		String actualApplicantMaxLoanLimit = "";
		String actualCoApplicantMaxLoanLimit = "";
		for( WorkitemCreationRequestHighmarkDtls requestHighmarkDtls : requestFields.getHighmarkDtls()){
			if(!"1".equalsIgnoreCase(requestHighmarkDtls.getCustomerType())){
				actualApplicantIndebtedness = requestHighmarkDtls.getApplicantIndebtedness();
				actualCoApplicantIndebtedness = requestHighmarkDtls.getCoApplicantIndebtedness();
				actualApplicantIndebtednessLimit = requestHighmarkDtls.getAppIndebtednessLimit();
				actualCoApplicantIndebtednessLimit = requestHighmarkDtls.getCoappIndebtednessLimit();
				actualApplicantMaxLoanLimit = requestHighmarkDtls.getAppMaxLoanLimit();
				actualCoApplicantMaxLoanLimit = requestHighmarkDtls.getCoappMaxLoanLimit();
			}
		}
		for (WorkitemCreationRequestHighmarkDtls requestHighmarkDtls : requestFields.getHighmarkDtls()) {
			WorkitemCreationAshaCmplxCbDetails ashaCmplxCbDetail = new WorkitemCreationAshaCmplxCbDetails();
			WorkitemCreationCibilIntegrationUtility cibilIntegrationUtility = new WorkitemCreationCibilIntegrationUtility();
			ashaCmplxCbDetail.setPid("");
			if (requestHighmarkDtls.getCustomerType().equals("1")) {
				ashaCmplxCbDetail.setCustPrefix("A01");
				ashaCmplxCbDetail.setDocumentName("A01_CB Report_Highmark");
				cibilIntegrationUtility.setCustPrefix("A01");
			} else {
				ashaCmplxCbDetail.setCustPrefix("C01");
				ashaCmplxCbDetail.setDocumentName("C01_CB Report_Highmark");

				cibilIntegrationUtility.setCustPrefix("C01");
			}
			ashaCmplxCbDetail.setCbType("2");
			ashaCmplxCbDetail.setName(requestHighmarkDtls.getCustomerName());
			ashaCmplxCbDetail.setCbScore(requestHighmarkDtls.getHighmarkScore());
			ashaCmplxCbDetail.setApplicantType("");
			ashaCmplxCbDetail.setGeneratedOn(requestHighmarkDtls.getGeneratedOn());
			ashaCmplxCbDetail.setGeneratedBy("System Generated");
			cal.setTime(outFormat.parse(requestHighmarkDtls.getGeneratedOn()));
			cal.add(Calendar.DAY_OF_MONTH, 30);
			ashaCmplxCbDetail.setReportExpiryDate(outFormat.format(cal.getTime()));
			ashaCmplxCbDetail.setClosedAccount("");
			ashaCmplxCbDetail.setActiveAccount("");
			ashaCmplxCbDetail.setCreditVintage("");
			ashaCmplxCbDetail.setLastSixMonthEnquires("");
			ashaCmplxCbDetail.setPrincipleOutstanding("");
			ashaCmplxCbDetail.setCb1To29Count("");
			ashaCmplxCbDetail.setCb30To59Count("");
			ashaCmplxCbDetail.setCb60To89Count("");
			ashaCmplxCbDetail.setCb1To29CountLast6Mon("");
			ashaCmplxCbDetail.setCb30To59CountLast6Mon("");
			ashaCmplxCbDetail.setCb60To89CountLast6Mon("");
			ashaCmplxCbDetail.setWriteOff("");
			ashaCmplxCbDetail.setChargeOff("");
			ashaCmplxCbDetail.setCreditScore("0");
			ashaCmplxCbDetails.add(ashaCmplxCbDetail);

			WorkitemCreationUnnatiCbValues unnatiCbValues = new WorkitemCreationUnnatiCbValues();
			unnatiCbValues.setPid("");
			unnatiCbValues.setAppType(requestHighmarkDtls.getCustomerType());
			unnatiCbValues.setService("Highmark");
			unnatiCbValues.setRulename("TotalIndebtness");
			unnatiCbValues.setVal(CommonUtils.getHighmarkValue(requestHighmarkDtls.getTotalIndebitness()));
			if("1".equalsIgnoreCase(requestHighmarkDtls.getCustomerType())) {
//				unnatiCbValues.setLimit(requestHighmarkDtls.getAppIndebtednessLimit());
//				unnatiCbValues.setLoanLimit(requestHighmarkDtls.getAppMaxLoanLimit());
				unnatiCbValues.setLimit(actualApplicantIndebtednessLimit);
				unnatiCbValues.setLoanLimit(actualApplicantMaxLoanLimit);
				if(StringUtils.isNotBlank(actualApplicantIndebtedness)) {
				unnatiCbValues.setVal(actualApplicantIndebtedness);
				}
			}else {
//				unnatiCbValues.setLimit(requestHighmarkDtls.getCoappIndebtednessLimit());
//				unnatiCbValues.setLoanLimit(requestHighmarkDtls.getCoappMaxLoanLimit());
				unnatiCbValues.setLimit(actualCoApplicantIndebtednessLimit);
				unnatiCbValues.setLoanLimit(actualCoApplicantMaxLoanLimit);
				if(StringUtils.isNotBlank(actualCoApplicantIndebtedness)) {
				unnatiCbValues.setVal(actualCoApplicantIndebtedness);
				}
			}

			//unnatiCbValues.setLimit("300000");
			//unnatiCbValues.setLoanLimit(CommonUtils.unformatAmount(requestHighmarkDtls.getLoanLimit()));
			workitemCreationUnnatiCbValues.add(unnatiCbValues);

			cibilIntegrationUtility.setPid("");
			cibilIntegrationUtility.setCustomerType("");
			cibilIntegrationUtility.setServiceType("CBHighmark Services");
			cibilIntegrationUtility.setDescription(requestHighmarkDtls.getHighmarkStatus());
			cibilIntegrationUtility.setInsertedOn(outFormat.format(new Date()));
			cibilIntegrationUtility.setDgReferenceFid("");
			creationCibilIntegrationUtilities.add(cibilIntegrationUtility);
		}

		highmarkDetails.setAshaCmplxCbDetails(ashaCmplxCbDetails);
		highmarkDetails.setUnnatiCbValues(workitemCreationUnnatiCbValues);
		highmarkDetails.setCibilIntegrationUtility(creationCibilIntegrationUtilities);
		return highmarkDetails;
	}

	private List<WorkitemCreationDocumentDetails> getDocDtls(WorkitemCreationRequestFields requestFields) {
		List<WorkitemCreationDocumentDetails> documentDtls = new ArrayList<>();
		String applicationId = requestFields.getApplnRefNo();
		String fileContent;
		try {
			Properties prop = CommonUtils.readPropertyFile();

			for (WorkitemCreationRequestDocDtls requestDocDtls : requestFields.getDocDtls()) {
				WorkitemCreationDocumentDetails documentDtl = new WorkitemCreationDocumentDetails();
				documentDtl.setDocName(requestDocDtls.getDocName());
				documentDtl.setDocExtn(requestDocDtls.getDocExtn());
				documentDtl.setDocContent(requestDocDtls.getDocContent());
				// documentDtl.setDocContent("");
				documentDtl.setDocSize(requestDocDtls.getDocSize());
				documentDtls.add(documentDtl);
				if (StringUtils.isBlank(documentDtl.getDocContent()))
					return new ArrayList<>();
				/*
				 * fileContent = CommonUtils.newgenDocUpload(requestDocDtls.getDocContent(),
				 * null, applicationId, requestDocDtls.getDocName());
				 * if(StringUtils.isEmpty(fileContent))return new ArrayList<>();
				 */
			}
			for (WorkitemCreationRequestHighmarkDtls requestHighmarkDtls : requestFields.getHighmarkDtls()) {
				WorkitemCreationDocumentDetails documentDtl2 = new WorkitemCreationDocumentDetails();
				if (requestHighmarkDtls.getCustomerType().equals("1")) {
					documentDtl2.setDocName("A01_CB Report_Highmark");
				} else {
					documentDtl2.setDocName("C01_CB Report_Highmark");
				}
				documentDtl2.setDocExtn("pdf");
				logger.debug("requestHighmarkDtls :" + requestHighmarkDtls.toString());
				if (requestHighmarkDtls.getBureauName().equalsIgnoreCase("Highmark")) {
					String externalURL = prop.getProperty(CobFlagsProperties.CB_REPORT_EXTERNAL_URL.getKey());
					logger.debug("externalURL : " + externalURL);
					String internalURL = prop.getProperty(CobFlagsProperties.CB_REPORT_INTERNAL_URL.getKey());
					logger.debug("internalURL : " + internalURL);
					String ReportURL = (requestHighmarkDtls.getCbReport()).replaceAll(externalURL, internalURL);
					logger.debug("ReportURL : " + ReportURL);
					documentDtl2.setDocContent(CommonUtils.URLtoBase64Str(ReportURL));
					/*
					 * fileContent =
					 * CommonUtils.newgenDocUpload(CommonUtils.URLtoBase64Str(ReportURL), null,
					 * applicationId, documentDtl2.getDocName());
					 * documentDtl2.setDocContent(fileContent);
					 */

				} else if (requestHighmarkDtls.getBureauName().equalsIgnoreCase("BRE")) {
					FetchDocumentsRequest req = new FetchDocumentsRequest();
					req.setAppId(Constants.APPID);
					req.setApplicationId(applicationId);
					req.setDocType("BREDocument");
					req.setLoanId(requestHighmarkDtls.getLoanId());
					documentDtl2.setDocContent(getServerUploadedFiles(req));
				}
				documentDtl2.setDocSize("");
				// if (!"".equalsIgnoreCase(documentDtl2.getDocContent()))
				// if (StringUtils.isEmpty(documentDtl2.getDocContent()))
				// return new ArrayList<>();
				// documentDtl2.setDocContent("");
				logger.debug("response from the report API has base 64 200 :" + documentDtl2.getDocContent() + " ---  "
						+ requestHighmarkDtls.getLoanId());
				documentDtls.add(documentDtl2);
				if (StringUtils.isBlank(documentDtl2.getDocContent()))
					return new ArrayList<>();
			}
		} catch (IOException e) {
			
			
		}
		return documentDtls;
	}

	public String getServerUploadedFiles(FetchDocumentsRequest fetchDocumentsRequest) {
		String base64String = "";
		try {
			Properties prop = null;
			try {
				prop = CommonUtils.readPropertyFile();
			} catch (IOException e) {
				logger.error("Error while reading property file in workitemCreation ", e);
			}

			String appId = fetchDocumentsRequest.getAppId();
			String applicationId = fetchDocumentsRequest.getApplicationId();
			String docType = fetchDocumentsRequest.getDocType();
			switch (docType) {
			case "BREDocument":
				try {
					String loanId = fetchDocumentsRequest.getLoanId();
					String fileName = loanId + ".pdf"; // A012313213131311.pdf
					String uploadLocation = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + appId
							+ "/LOAN/" + applicationId + "/";
					String filePath1 = uploadLocation + fileName;
					File file = new File(filePath1);
					if (file.exists()) {
						try {
							logger.debug("file exist ");
							byte[] fileContent = Files.readAllBytes(file.toPath());
							base64String = java.util.Base64.getEncoder().encodeToString(fileContent);
							return base64String;

						} catch (IOException e) {
							logger.error("file does not exist 1 " + e.getMessage());
						}
					} else {
						logger.error("file does not exist 2 ");
					}
				} catch (Exception e) {
					logger.error("file does not exist 3 " + e.getMessage());
					
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error("file does not exist 4 " + e.getMessage());
			
		}
		return base64String;
	}

	public String getWipDedupeRequestXml(WipDedupeRequestFields wipDedupeRequestFields) throws JsonProcessingException {
		XmlMapper xmlMapper = new XmlMapper();
		WipDedupeRequestExt wipDedupeRequestExt = new WipDedupeRequestExt();
		wipDedupeRequestExt.setCustomerType(wipDedupeRequestFields.getCustomerType());
		wipDedupeRequestExt.setCustomerId(wipDedupeRequestFields.getCustomerId());
		wipDedupeRequestExt.setPrimaryKycId(wipDedupeRequestFields.getPrimaryKycId());
		return xmlMapper.writeValueAsString(wipDedupeRequestExt).replace("<WI-REQ>", "").replace("</WI-REQ>", "");
	}

}
