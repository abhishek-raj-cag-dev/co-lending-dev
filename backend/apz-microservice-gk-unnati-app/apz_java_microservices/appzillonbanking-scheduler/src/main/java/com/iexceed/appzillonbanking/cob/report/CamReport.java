package com.iexceed.appzillonbanking.cob.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.DeviationRATracker;
import com.iexceed.appzillonbanking.cob.core.domain.ab.SanctionMaster;
import com.iexceed.appzillonbanking.cob.core.payload.Address;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.LoanDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.loans.payload.BCMPIIncomeDetailsWrapper;
import com.iexceed.appzillonbanking.cob.loans.payload.BCMPIOtherDetailsWrapper;
import com.iexceed.appzillonbanking.cob.loans.payload.LoanObligationsNestedClass;
import com.iexceed.appzillonbanking.cob.loans.payload.LoanObligationsWrapper;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.ReportStyleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.exception.DRException;

@Service
public class CamReport {

	private static final Logger logger = LogManager.getLogger(CamReport.class);

	private StyleBuilder borderedStyle, boldText, boldCenteredStyle, boldTextWithBorder, boldLeftStyle, rightStyle,
			leftStyle;

	static String space = "\u00a0\u00a0\u00a0";

	private String applicantCustId = "";
	private String coApplicantCustId = "";
	private String applicantName = "";
	private String coApplicantName = "";
	private CustomerDetails applicantCustDtls = null;
	private CustomerDetails coApplicantCustDtls = null;
	String appltGender = "";
	String coAppltGender = "";
	String interestRate = "";
	private String bmId = "";
	
	public CamReport() {

		borderedStyle = stl.style(stl.penThin()).setPadding(5);
		boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
		boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		boldText = stl.style().bold();
		boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

		rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
		leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

	}

	public Response generatePdf(CustomerDataFields customerFields, JSONObject keysForContent,
			List<DeviationRATracker> deviationTracker, Optional<SanctionMaster> sanctionMaster, String productName, String gkUserId, String gkUserName)
			throws DRException, IOException {

		Response response;

		try {
			StyleBuilder tempStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);

			JasperReportBuilder report = new JasperReportBuilder();

			StyleBuilder headerStyle = stl.style().setFontSize(20)
					.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();

			StyleBuilder style = stl.style().setBackgroundColor(Color.BLUE).setFontSize(20)
					.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

			StyleBuilder style1 = stl.style().setFont(stl.font("SansSerif", true, false, 12))
					.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
			StyleBuilder style2 = stl.style().setFont(stl.font("SansSerif", true, false, 12))
					.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT).bold();

			boolean loanOblExist = false;
			JasperReportBuilder subReport1 = new JasperReportBuilder();

			JasperReportBuilder subReport2 = new JasperReportBuilder();
			JasperReportBuilder subReport3 = new JasperReportBuilder();
			JasperReportBuilder subReport4 = new JasperReportBuilder();
			JasperReportBuilder subReport5 = new JasperReportBuilder();
			JasperReportBuilder subReport6 = new JasperReportBuilder();
			JasperReportBuilder subReport7 = new JasperReportBuilder();
			JasperReportBuilder subReport8 = new JasperReportBuilder();
			JasperReportBuilder subReport9 = new JasperReportBuilder();
			JasperReportBuilder subReport10 = new JasperReportBuilder();
			JasperReportBuilder subReport11 = new JasperReportBuilder();
			JasperReportBuilder subReport12 = new JasperReportBuilder();
			JasperReportBuilder subReport13 = new JasperReportBuilder();
			JasperReportBuilder subReport14 = new JasperReportBuilder();
			JasperReportBuilder subReport15 = new JasperReportBuilder();
			JasperReportBuilder subReport16 = new JasperReportBuilder();
			JasperReportBuilder subReport17 = new JasperReportBuilder();
			JasperReportBuilder subReport18 = new JasperReportBuilder();
			JasperReportBuilder subReportLoanObligationChitFunds = new JasperReportBuilder();
			JasperReportBuilder subReportLoanObligationMoneyLender = new JasperReportBuilder();
			JasperReportBuilder subReportLoanObligationFriends = new JasperReportBuilder();
			JasperReportBuilder subReportLoanObligationRelatives = new JasperReportBuilder();
			JasperReportBuilder subReportLoanObligationCoOpSociety = new JasperReportBuilder();
			JasperReportBuilder subReportLoanObligationLocalFinance = new JasperReportBuilder();
			JasperReportBuilder subReportLoanObligationOthers = new JasperReportBuilder();

			CustomerDetailsPayload payload1 = null;
			CustomerDetailsPayload payload2 = null;
			Gson gsonObj = new Gson();
			for (CustomerDetails custDtl : customerFields.getCustomerDetailsList()) {
				logger.debug("customer Type : " + custDtl.getCustomerType());
				if (custDtl.getCustomerType().equalsIgnoreCase("Applicant")) {
					applicantCustId = String.valueOf(custDtl.getCustDtlId());
					logger.debug("applicantCustId : " + applicantCustId);
					applicantName = custDtl.getCustomerName();

					payload1 = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
					logger.debug("custApplicantPayload :" + payload1);
					applicantCustDtls = custDtl;
					appltGender = payload1.getGender();
				} else if (custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
					coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
					coApplicantName = custDtl.getCustomerName();
					logger.debug("coApplicantCustId : " + coApplicantCustId);
					payload2 = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
					logger.debug("custCo-ApplicantPayload :" + payload2);
					coAppltGender = payload2.getGender();
					coApplicantCustDtls = custDtl;
				}
			}
			
//			if (Constants.NEW_LOAN_PRODUCT_CODE.equals(customerFields.getApplicationMaster().getProductCode())) {
//			    logger.info("Unnati application");
//
//			    List<ApplicationWorkflow> workflows = customerFields.getApplicationWorkflowList();
//		    for (int i = 0; i < workflows.size() - 1; i++) {
//		        ApplicationWorkflow current = workflows.get(i);
//		        ApplicationWorkflow next = workflows.get(i + 1);
//
//		        if (Constants.APPROVED.equalsIgnoreCase(current.getApplicationStatus()) &&
//		        		Constants.PENDING_FOR_APPROVAL.equalsIgnoreCase(next.getApplicationStatus())) {
//
//		            bmId = current.getCreatedBy();
//
//		            break;
//			        }
//			    }
//
//			} else {
//			    logger.info("Not an Unnati application");
//			}

			report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));

			report.title(cmp.text(keysForContent.getString("applicationName")).setStyle(boldCenteredStyle));
			subReport1.title(cmp.text(keysForContent.getString("applicationId") + ": "
					+ customerFields.getApplicationId() + "\t\t\t\t" + keysForContent.getString("kendraId") + ": "
					+ customerFields.getApplicationMaster().getKendraId()).setStyle(boldText));

//		subReport1.title(
//			    cmp.horizontalList(
//			        cmp.text(keysForContent.getString("applicationId"))
//			            .setStyle(boldLeftStyle.setFontSize(14)), // First word in bold
//			        cmp.text(" : " + requestFromUI.getString("applicationId"))
//			    )
//			);

			/* Loan Details */
			subReport2.title(cmp.text(keysForContent.getString("loanDetails")).setStyle(boldText))
					.title(getLoanDetails(keysForContent, customerFields, productName)).title(cmp.text(""));

//		subReport2.title(cmp.text(keysForContent.getString("splitLines")));

			// Applicant
			/* Basic Demographics Details */
			subReport3.title(cmp.text(keysForContent.getString("basicDemographicsDetails")).setStyle(boldText))
					.title(getBasicDemographicDetails(keysForContent, customerFields, payload1, payload2))
					.title(cmp.text(""));

			/* Residence Details */
			subReport4
					.title(cmp.text(keysForContent.getString("residence") + " " + keysForContent.getString("details"))
							.setStyle(boldText))
					.title(getResidenceDetails(keysForContent, customerFields)).title(cmp.text(""));

			/* Credit Bureau Details */
			subReport5.title(cmp.text(keysForContent.getString("creditBureauDetails")).setStyle(boldText))
					.title(getCreditBureauDetails(keysForContent, customerFields, true)).title(cmp.text(""));

			if (null != customerFields.getBcmpiLoanObligations().getLoanObligationsWrapper().getChitFunds()) {
				loanOblExist = true;
				subReportLoanObligationChitFunds
						.title(cmp.text(keysForContent.getString("chitFunds")).setStyle(boldText))
						.title(getLoanObligationsDetails(keysForContent,
								customerFields.getBcmpiLoanObligations().getLoanObligationsWrapper().getChitFunds()))
						.title(cmp.text(""));
			}

			if (null != customerFields.getBcmpiLoanObligations().getLoanObligationsWrapper().getMoneyLender()) {
				loanOblExist = true;
				subReportLoanObligationMoneyLender
						.title(cmp.text(keysForContent.getString("Mondaylender")).setStyle(boldText))
						.title(getLoanObligationsDetails(keysForContent,
								customerFields.getBcmpiLoanObligations().getLoanObligationsWrapper().getMoneyLender()))
						.title(cmp.text(""));
			}

			if (null != customerFields.getBcmpiLoanObligations().getLoanObligationsWrapper().getFriends()) {
				loanOblExist = true;
				subReportLoanObligationFriends.title(cmp.text(keysForContent.getString("Friends")).setStyle(boldText))
						.title(getLoanObligationsDetails(keysForContent,
								customerFields.getBcmpiLoanObligations().getLoanObligationsWrapper().getFriends()))
						.title(cmp.text(""));
			}

			if (null != customerFields.getBcmpiLoanObligations().getLoanObligationsWrapper().getRelatives()) {
				loanOblExist = true;
				subReportLoanObligationRelatives
						.title(cmp.text(keysForContent.getString("Relatives")).setStyle(boldText))
						.title(getLoanObligationsDetails(keysForContent,
								customerFields.getBcmpiLoanObligations().getLoanObligationsWrapper().getRelatives()))
						.title(cmp.text(""));
			}

			if (null != customerFields.getBcmpiLoanObligations().getLoanObligationsWrapper().getCoopsociety()) {
				loanOblExist = true;
				subReportLoanObligationCoOpSociety
						.title(cmp.text(keysForContent.getString("coopSociety")).setStyle(boldText))
						.title(getLoanObligationsDetails(keysForContent,
								customerFields.getBcmpiLoanObligations().getLoanObligationsWrapper().getCoopsociety()))
						.title(cmp.text(""));
			}

			if (null != customerFields.getBcmpiLoanObligations().getLoanObligationsWrapper().getLocalfinance()) {
				loanOblExist = true;
				subReportLoanObligationLocalFinance
						.title(cmp.text(keysForContent.getString("Localfinance")).setStyle(boldText))
						.title(getLoanObligationsDetails(keysForContent,
								customerFields.getBcmpiLoanObligations().getLoanObligationsWrapper().getLocalfinance()))
						.title(cmp.text(""));
			}

			if (null != customerFields.getBcmpiLoanObligations().getLoanObligationsWrapper().getOther()) {
				loanOblExist = true;
				subReportLoanObligationOthers.title(cmp.text(keysForContent.getString("Other")).setStyle(boldText))
						.title(getLoanObligationsDetails(keysForContent,
								customerFields.getBcmpiLoanObligations().getLoanObligationsWrapper().getOther()))
						.title(cmp.text(""));
			}

			// Loan Obligations
			subReport6.title(cmp.text(keysForContent.getString("loanObligations") + (loanOblExist ? "" : " - Nill"))
					.setStyle(boldText));

			// Final Income
			subReport8.title(cmp.text(keysForContent.getString("finalIncome")).setStyle(boldText))
					.title(getFinalIncomeDetails(keysForContent, customerFields)).title(cmp.text(""));

			// other Details
			subReport9.title(cmp.text(keysForContent.getString("otherDetails")).setStyle(boldText))
					.title(getOtherDetails(keysForContent, customerFields)).title(cmp.text(""));



			// other Details
			subReport11.title(cmp.text(keysForContent.getString("landDetails")).setStyle(boldText))
					.title(getLandDetails(keysForContent, customerFields)).title(cmp.text(""));

			// In principal decision
			subReport12.title(cmp.text(keysForContent.getString("inPrincipalDecision")).setStyle(boldText))
					.title(getInprincipalDecisionDetails(keysForContent, customerFields)).title(cmp.text(""));

			/* Deviation */
			subReport13.title(cmp.text(keysForContent.getString("deviationsNote")).setStyle(boldText))
					.title(getDeviationDetails(keysForContent, customerFields, deviationTracker)).title(cmp.text(""));

			/* Credit Assessment */
			subReport14.title(cmp.text(keysForContent.getString("reassessment")).setStyle(boldText))
					.title(getReassessmentDetails(keysForContent, customerFields, deviationTracker))
					.title(cmp.text(""));

			/* Sanction Condition */
			subReport15.title(cmp.text(keysForContent.getString("sanctionCondition")).setStyle(boldText))
					.title(getSanctionCondition(keysForContent, customerFields, sanctionMaster)).title(cmp.text(""));

			/* Officer Details */
			subReport16.title(cmp.text(keysForContent.getString("recommendedBy")).setStyle(boldText))
					.title(getOfficerDetails(keysForContent, customerFields, gkUserId, gkUserName)).title(cmp.text(""));

			report.addSummary(cmp.subreport(subReport1)).addSummary(cmp.subreport(subReport2))
					.addSummary(cmp.subreport(subReport3)).addSummary(cmp.subreport(subReport4))
					.addSummary(cmp.subreport(subReport5)).addSummary(cmp.subreport(subReport6))
					.addSummary(cmp.subreport(subReportLoanObligationChitFunds))
					.addSummary(cmp.subreport(subReportLoanObligationMoneyLender))
					.addSummary(cmp.subreport(subReportLoanObligationFriends))
					.addSummary(cmp.subreport(subReportLoanObligationRelatives))
					.addSummary(cmp.subreport(subReportLoanObligationCoOpSociety))
					.addSummary(cmp.subreport(subReportLoanObligationLocalFinance))
					.addSummary(cmp.subreport(subReportLoanObligationOthers)).addSummary(cmp.subreport(subReport7))
					.addSummary(cmp.subreport(subReport8)).addSummary(cmp.subreport(subReport9))
					.addSummary(cmp.subreport(subReport10)).addSummary(cmp.subreport(subReport11))
					.addSummary(cmp.subreport(subReport12)).addSummary(cmp.subreport(subReport13))
					.addSummary(cmp.subreport(subReport14)).addSummary(cmp.subreport(subReport15))
					.addSummary(cmp.subreport(subReport16)).addSummary(cmp.subreport(subReport17))
					.addSummary(cmp.subreport(subReport18));

//				.show();

//			//saving report to the directory and creating base64 string for response 
			try {
				response = new Response();
				Properties prop = CommonUtils.readPropertyFile();

				// Construct file path
//		    String filePathDest = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/"
//		            + customerFields.getAppId() + "/CamReport/" + customerFields.getApplicationId() + "/";

				String filePathDest = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + "APZCBO" + "/"
						+ Constants.LOAN + "/" + customerFields.getApplicationId() + "/";

				logger.debug("filePathDest :: {}", filePathDest);

				// Ensure directory exists
				File directory = new File(filePathDest);
				if (!directory.exists()) {
					boolean isCreated = directory.mkdirs();
					if (!isCreated) {
						throw new IOException("Failed to create 1: " + filePathDest);
					}
				}

				String filePath = filePathDest + customerFields.getApplicationId() + "_CamSheetReport" + ".pdf";
				logger.debug("final filePath : {}", filePath);

				// Save report to file
				try (FileOutputStream fos = new FileOutputStream(filePath)) {
					report.toPdf(fos);
				}

				// Read file and encode to Base64
				byte[] utfile = Files.readAllBytes(Paths.get(filePath));
				String base64String = java.util.Base64.getEncoder().encodeToString(utfile);
				logger.debug("base64String generated : {}", base64String);

				// Delete the file
				// Files.deleteIfExists(Paths.get(filePath));

				response = getSuccessJson(base64String);

				logger.info("Credit Assessment PDF Report Generated");

			} catch (DRException e) {
				logger.error("Error generating PDF report: ", e);
				response = getFailureJson(e.getMessage());
			} catch (IOException e) {
				logger.error("Error handling file operations: ", e);
				response = getFailureJson(e.getMessage());
			} catch (Exception e) {
				logger.error("Unexpected error: ", e);
				response = getFailureJson(e.getMessage());
			}

		} catch (Exception e) {
			response = getFailureJson(e.getMessage());
			logger.error(e.getMessage(), e);
		}
		logger.debug("generate CamReport Function end");
		return response;
	}

	//
	private ComponentBuilder<?, ?> getLoanDetails(JSONObject keysForContent, CustomerDataFields req,
			String productName) {
		logger.debug("Loan Details");
		VerticalListBuilder verticalList = cmp.verticalList();
		Gson gsonObj = new Gson();
		try {
			
			LoanDetailsPayload payload = gsonObj.fromJson(req.getLoanDetails().getPayloadColumn(),
					LoanDetailsPayload.class);
			logger.debug("LoanDetailsPayload : " + payload);

//			String interest = Optional.ofNullable(req.getLoanDetails().getRoi())
//					.filter(i -> Float.compare(i, 0.0f) != 0).map(String::valueOf).orElse("");//
//			logger.debug("Interest rate fro loan details:" + req.getLoanDetails().getRoi());//

			String cbInterest = "";
			CibilDetailsPayload cibilPayloadCoApp = new CibilDetailsPayload();
			for (CibilDetailsWrapper cibilDetailsWrapper : req.getCibilDetailsWrapperList()) {
				String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
//				String customerType = applicant ? applicantCustId : coApplicantCustId;

				logger.debug("CreditDetailsPayload Payload : " + cibilPayloadCoApp);
				if (custId.equals(coApplicantCustId)) {
					cibilPayloadCoApp = gsonObj.fromJson(cibilDetailsWrapper.getCibilDetails().getPayloadColumn(),
							CibilDetailsPayload.class);
					cbInterest = String
							.valueOf(cibilPayloadCoApp.getRoi() == null ? "" : cibilPayloadCoApp.getRoi().toString());
					logger.debug("interest : " + cbInterest);
				}
			}

//			if(interest.isEmpty()) {
//				interest = interestRate;
//			}
			verticalList.add(createEightHorizontalList(keysForContent.getString("loanType"), productName,
					keysForContent.getString("loanPurpose"), payload.getLoanPurpose(),
					keysForContent.getString("loanSubpurpose"), payload.getSubCategory(),
					keysForContent.getString("reqstdLoanAmount"),
					CommonUtils.amountFormat(String.valueOf(req.getLoanDetails().getLoanAmount()))));
			verticalList.add(createEightHorizontalList(keysForContent.getString("loanTenure"),
					String.valueOf(req.getLoanDetails().getTenure()) + " months",
					keysForContent.getString("loanIntrest"), cbInterest + "%", keysForContent.getString("loanFrequency"),
					payload.getFrequencyOfRepayment(), keysForContent.getString("customerId"),
					req.getApplicationMaster().getMemberId()));

		} catch (Exception e) {
			logger.error("error - getLoanDetails");
			logger.error(e.getMessage());
		}
		logger.debug("LoanDetails added");

		return verticalList;
	}

	private ComponentBuilder<?, ?> getBasicDemographicDetails(JSONObject keysForContent, CustomerDataFields req,
			CustomerDetailsPayload applicantPayload, CustomerDetailsPayload coapplicantPayload) {
		VerticalListBuilder verticalList = cmp.verticalList();

		String appMobNo = "";
		String coappMobNo = "";
		String applicantName = "";
		String coApplicantName = "";

		try {
			for (CustomerDetails customer : req.getCustomerDetailsList()) {
				if (customer.getCustomerType().equalsIgnoreCase("Applicant")) {
					appMobNo = customer.getMobileNumber();
					applicantName = customer.getCustomerName();
				} else if (customer.getCustomerType().equalsIgnoreCase("Co-App")) {
					coappMobNo = customer.getMobileNumber();
					coApplicantName = customer.getCustomerName();
				}
			}

			
			String apptKycNo = "NA";
			if (applicantPayload != null) {
	            boolean isPrimaryVerified = Constants.VERIFIED_STS.equalsIgnoreCase(applicantPayload.getPrimaryKycIdValStatus());
	            boolean isAlternateVerified = Constants.VERIFIED_STS.equalsIgnoreCase(applicantPayload.getAlternateVoterIdValStatus());

	            if (isPrimaryVerified && isAlternateVerified) {
	                apptKycNo = applicantPayload.getAlternateVoterId();
	            } else if (isAlternateVerified) {
	                apptKycNo = applicantPayload.getAlternateVoterId();
	            } else if (isPrimaryVerified) {
			        apptKycNo = applicantPayload.getPrimaryKycId();
			    }
			}
		
			String coApptKycNo = "NA";
			if (coapplicantPayload != null) {
	            boolean isPrimaryVerified = Constants.VERIFIED_STS.equalsIgnoreCase(coapplicantPayload.getPrimaryKycIdValStatus());
	            boolean isAlternateVerified = Constants.VERIFIED_STS.equalsIgnoreCase(coapplicantPayload.getAlternateVoterIdValStatus());

	            if (isPrimaryVerified && isAlternateVerified) {
	            	coApptKycNo = coapplicantPayload.getAlternateVoterId();
	            } else if (isAlternateVerified) {
	            	coApptKycNo = coapplicantPayload.getAlternateVoterId();
	            } else if (isPrimaryVerified) {
			        coApptKycNo = coapplicantPayload.getPrimaryKycId();
			    }
			}

			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString(Constants.FIELD_NAME),
					keysForContent.getString("applicant"), keysForContent.getString("coApplicant"),
					boldTextWithBorder));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("nameAsPerSystem"),
					applicantName, coApplicantName, borderedStyle));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("nameAsPerKyc"),
					applicantPayload.getNamePerKyc(), coapplicantPayload.getNamePerKyc(), borderedStyle));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("primaryKyc"),
			CommonUtils.getDefaultValue(apptKycNo), CommonUtils.getDefaultValue(coApptKycNo), borderedStyle));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("dateOfBirth"),
                    CommonUtils.dateFormat3(applicantPayload.getDob()), CommonUtils.dateFormat3(coapplicantPayload.getDob()), borderedStyle));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("memberGender"),
					applicantPayload.getGender(), coapplicantPayload.getGender(), borderedStyle));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("mobileNumber"),
					appMobNo, coappMobNo, borderedStyle));
			verticalList
					.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("memberMarritalStstus"),
							applicantPayload.getMaritalStatus(), coapplicantPayload.getMaritalStatus(), borderedStyle));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("memberOccupation"),
					applicantPayload.getOccupation(), coapplicantPayload.getOccupation(), borderedStyle));
			verticalList
					.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("relationshipToApplicant"),
							coapplicantPayload.getRelationShipWithApplicant(), 26, 74, borderedStyle));
		} catch (JSONException e) {
			logger.error("error - getBasicDemographicDetails");
			logger.error(e.getMessage());
		}

		return verticalList;
	}

	private ComponentBuilder<?, ?> getResidenceDetails(JSONObject keysForContent, CustomerDataFields req) {
		VerticalListBuilder verticalList = cmp.verticalList();
		try {
			JSONObject addressDetailsObj = getAllAddressDeatils(req);

			String presentCityYrs = addressDetailsObj.getString(Constants.PRESNT_CITY_IN_YEARS);
			presentCityYrs = presentCityYrs.endsWith("Years") ? presentCityYrs : presentCityYrs + "Years";
			logger.debug("presentCityYrs :" + presentCityYrs);

			verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString(Constants.FIELD_NAME),
					keysForContent.getString(Constants.FIELD_VALUE), 40, 60, boldTextWithBorder));
			verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("presentAddress"),
					addressDetailsObj.getString("presentAddressApplicant"), 40, 60, borderedStyle));
			verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("permanentAddress"),
					addressDetailsObj.getString("permanetAddressApplicant"), 40, 60, borderedStyle));
			verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("residenceOwnership"),
					addressDetailsObj.getString("presentResidenceOwnership"), 40, 60, borderedStyle));
			verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString(Constants.PRESENT_ADDRESS_IN_YEARS),
					addressDetailsObj.getString(Constants.PRESENT_ADDRESS_IN_YEARS), 40, 60, borderedStyle));
			verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString(Constants.PRESNT_CITY_IN_YEARS),
					presentCityYrs, 40, 60, borderedStyle));
		} catch (JSONException e) {
			logger.error("error - getResidenceDetails");
			logger.error(e.getMessage());
		}

//		verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString(Constants.FIELD_NAME), addressDetailsObj.getString("presentResidenceOwnership"),
//				keysForContent.getString("Constants.PRESENT_ADDRESS_IN_YEARS), addressDetailsObj.getString("Constants.PRESENT_ADDRESS_IN_YEARS), keysForContent.getString(Constants.PRESNT_CITY_IN_YEARS),
//				addressDetailsObj.getString(Constants.PRESNT_CITY_IN_YEARS)));
		return verticalList;
	}

	// Credit Bureau Details
	private ComponentBuilder<?, ?> getCreditBureauDetails(JSONObject keysForContent, CustomerDataFields req,
			boolean applicant) {
		VerticalListBuilder verticalList = cmp.verticalList();
		Gson gsonObj = new Gson();
		String appOverdueAmt = "";
		String appWriteOffAmt = "";
		String appCbScore = "";
		String appTotIndebtness = "";
		String appFoir = "";
		String coappOverdueAmt = "";
		String coappWriteOffAmt = "";
		String coappCbScore = "";
		String coappTotIndebtness = "";
		String coappFoir = "";

		try {
			for (CibilDetailsWrapper cibilDetailsWrapper : req.getCibilDetailsWrapperList()) {
				String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
//				String customerType = applicant ? applicantCustId : coApplicantCustId;
				CibilDetailsPayload payload = gsonObj.fromJson(cibilDetailsWrapper.getCibilDetails().getPayloadColumn(),
						CibilDetailsPayload.class);
				logger.debug("CreditDetailsPayload Payload : " + payload);
				if (custId.equals(applicantCustId)) {
					appOverdueAmt = payload.getOverdueAmt();
					appWriteOffAmt = payload.getWriteOffAmt();
					appCbScore = payload.getCbScore();
//					appTotIndebtness = payload.getTotIndebtness();
					appFoir = payload.getFoirPercentage();
					appTotIndebtness = extractValue(payload.getIndividualIndebtness());
				} else {
					coappOverdueAmt = payload.getOverdueAmt();
					coappWriteOffAmt = payload.getWriteOffAmt();
					coappCbScore = payload.getCbScore();
//					appTotIndebtness = payload.getAppIndebtednessLimit();
//					coappTotIndebtness = payload.getCoappIndebtednessLimit();
					coappTotIndebtness =  extractValue(payload.getIndividualIndebtness());
					coappFoir = payload.getFoirPercentage();
					interestRate = payload.getRoi();
				}
			}
			String appNetIncome = String
					.valueOf(req.getBcmpiIncomeDetails().getBcmpiIncomeDetailsWrapper().getApplicantTotalIncome());
			String coAppNetIncome = String
					.valueOf(req.getBcmpiIncomeDetails().getBcmpiIncomeDetailsWrapper().getCoApplicantTotalIncome());
			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString(Constants.FIELD_NAME),
					keysForContent.getString("applicant"), keysForContent.getString("coApplicant"),
					boldTextWithBorder));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("overdueAmount"),
					appOverdueAmt, coappOverdueAmt, borderedStyle));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("writtenOffAmount"),
					appWriteOffAmt, coappWriteOffAmt, borderedStyle));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("cbScore"),
					appCbScore, coappCbScore, borderedStyle));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("netIncome"),
					CommonUtils.amountFormat(appNetIncome), CommonUtils.amountFormat(coAppNetIncome), borderedStyle));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("unsecuredDebt"),
					CommonUtils.amountFormat(appTotIndebtness), CommonUtils.amountFormat(coappTotIndebtness), borderedStyle));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("foir"),
					coappFoir + "%", coappFoir + "%", borderedStyle));
		} catch (Exception e) {
			logger.error("error - getCreditBureauDetails");
			logger.error(e.getMessage());
		}
		logger.debug("getCreditBureauDetails added");
		return verticalList;
	}

	private ComponentBuilder<?, ?> getLoanObligationsDetails(JSONObject keysForContent,
			LoanObligationsNestedClass loanObligationsNestedClass) {

		VerticalListBuilder verticalList = cmp.verticalList();
		Gson gson = new Gson();
		verticalList.add(createTwoHorizontalList(keysForContent.getString(Constants.FIELD_NAME),
				keysForContent.getString(Constants.FIELD_VALUE), boldTextWithBorder));

		try {
			verticalList.add(createTwoHorizontalList(keysForContent.getString("otherLoanObligations"),
					CommonUtils.formatAmount(Double.parseDouble(loanObligationsNestedClass.getOtherLoanObligation())),
					borderedStyle));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("monthlyEmi"),
					CommonUtils.formatAmount(Double.parseDouble(loanObligationsNestedClass.getMonthlyEMI())),
					borderedStyle));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("remainingTenure"),
					loanObligationsNestedClass.getTenure().split("\\.")[0] + " months", borderedStyle));

		} catch (Exception e) {
			logger.error("Error - getLoanObligationsDetails", e);
			logger.error(e.getMessage());
		}
		return verticalList;
	}

	private ComponentBuilder<?, ?> getOtherDetails(JSONObject keysForContent, CustomerDataFields customerFields) {

		VerticalListBuilder verticalList = cmp.verticalList();
		try {
			BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = customerFields.getBcmpiOtherDetails()
					.getBcmpiOtherDetailsWrapper();
			logger.debug("bcmpiOtherDetailsWrapper-->" + bcmpiOtherDetailsWrapper);
			String applicantUsesSmartPhone = Integer.parseInt(bcmpiOtherDetailsWrapper.getSmartphonesOwned()) > 0
					? "Yes"
					: "No";

			verticalList.add(createTwoHorizontalList(keysForContent.getString(Constants.FIELD_NAME),
					keysForContent.getString(Constants.FIELD_VALUE), boldTextWithBorder));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("houseOwnership"),
					bcmpiOtherDetailsWrapper.getHouseOwnership(), borderedStyle));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("typeOfHouse"),
					bcmpiOtherDetailsWrapper.getTypeOfHouse(), borderedStyle));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("typeOfRoof"),
					bcmpiOtherDetailsWrapper.getTypeOfRoof(), borderedStyle));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("numberOfRooms"),
					bcmpiOtherDetailsWrapper.getNoOfRoomsInHouse(), borderedStyle));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("modeOfSavings"),
					bcmpiOtherDetailsWrapper.getModeOfSavings(), borderedStyle));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("basicAmenities"),
					bcmpiOtherDetailsWrapper.getBasicAmenities().toString(), borderedStyle));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("otherAssets"),
					bcmpiOtherDetailsWrapper.getOtherAssets().toString(), borderedStyle));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("applicantUsesSmartphone"),
					applicantUsesSmartPhone, borderedStyle));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("numberOfSmartphones"),
					bcmpiOtherDetailsWrapper.getSmartphonesOwned(), borderedStyle));
			/*
			 * verticalList.add(createTwoHorizontalList(keysForContent.getString(
			 * "hasAlmirahOrDressingTable"), bcmpiOtherDetailsWrapper.getAlmirah(),
			 * borderedStyle));
			 * verticalList.add(createTwoHorizontalList(keysForContent.getString(
			 * "hasFurniture"), bcmpiOtherDetailsWrapper.getChair(), borderedStyle));
			 */
		} catch (Exception e) {
			logger.error("error - getOtherDetails", e);
			logger.error(e.getMessage());
		}

		return verticalList;
	}

	private ComponentBuilder<?, ?> getLandDetails(JSONObject keysForContent, CustomerDataFields customerFields) {

		VerticalListBuilder verticalList = cmp.verticalList();
		Gson gsonObj = new Gson();

		try {
//			BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = gsonObj.fromJson(customerFields.getBcmpiOtherDetails().getPayload(), BCMPIOtherDetailsWrapper.class);

//			BCMPIOtherDetails payload = gsonObj.fromJson(customerFields.getBcmpiOtherDetails().getPayload(), BCMPIOtherDetails.class);
//			BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = payload.getBcmpiOtherDetailsWrapper();
			BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = customerFields.getBcmpiOtherDetails()
					.getBcmpiOtherDetailsWrapper();

			String landOwnerName = "NA";
			String relationshipWithApplicant = "NA";
			String agriland = "0";
			if (bcmpiOtherDetailsWrapper != null) {
				if (bcmpiOtherDetailsWrapper.getAgriland() != null && !"0".equals(bcmpiOtherDetailsWrapper.getAgriland())) {
					agriland = bcmpiOtherDetailsWrapper.getAgriland();
					landOwnerName = bcmpiOtherDetailsWrapper.getLandOwnerName();
					relationshipWithApplicant = bcmpiOtherDetailsWrapper.getRelationshipWithApplicant();
				}
			}

			verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString(Constants.FIELD_NAME),
					keysForContent.getString(Constants.FIELD_VALUE), 60, 40, boldTextWithBorder));
			verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("agriLandHoldings"),
					agriland, 60, 40, borderedStyle));
			verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("landOwnerName"),
					landOwnerName, 60, 40, borderedStyle));
			verticalList.add(
					createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("relationshipWithApplicant"),
							relationshipWithApplicant, 60, 40, borderedStyle));



		} catch (Exception ex) {
			logger.error("error - getLandDetails", ex);
			logger.error(ex.getMessage());
		}


		return verticalList;
	}

	private ComponentBuilder<?, ?> getFinalIncomeDetails(JSONObject keysForContent, CustomerDataFields customerFields) {

		VerticalListBuilder verticalList = cmp.verticalList();
		BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper = customerFields.getBcmpiIncomeDetails()
				.getBcmpiIncomeDetailsWrapper();
		logger.debug("bcmpiIncomeDetailsWrapper ::{}", bcmpiIncomeDetailsWrapper);
		List<BCMPIIncomeDetailsWrapper.Dairy> dairyList = bcmpiIncomeDetailsWrapper.getBusiness().getDairy();
		List<BCMPIIncomeDetailsWrapper.Kirana> kiranaList = bcmpiIncomeDetailsWrapper.getBusiness().getKirana();
		List<BCMPIIncomeDetailsWrapper.Tailoring> tailoringList = bcmpiIncomeDetailsWrapper.getBusiness()
				.getTailoring();
		List<BCMPIIncomeDetailsWrapper.OtherBusiness> otherList = bcmpiIncomeDetailsWrapper.getBusiness().getOther();
		BCMPIIncomeDetailsWrapper.Wage wage = bcmpiIncomeDetailsWrapper.getWage();
		BCMPIIncomeDetailsWrapper.Agriculture agri = bcmpiIncomeDetailsWrapper.getAgriculture();
		BCMPIIncomeDetailsWrapper.Salary salary = bcmpiIncomeDetailsWrapper.getSalary();
		BCMPIIncomeDetailsWrapper.Pension pension = bcmpiIncomeDetailsWrapper.getPension();
		BCMPIIncomeDetailsWrapper.RentalIncome rental = bcmpiIncomeDetailsWrapper.getRentalIncome();
		String wageCustType = "";
		BigDecimal wageIncome = BigDecimal.ZERO;
		String agriCustType = "";
		BigDecimal agriIncome = BigDecimal.ZERO;
		String salaryCustType = "";
		BigDecimal salaryIncome = BigDecimal.ZERO;
		String pensionCustType = "";
		BigDecimal pensionIncome = BigDecimal.ZERO;
		String rentalCustType = "";
		BigDecimal rentalIncome = BigDecimal.ZERO;
		String dairyOwnedBy = "";
		int dairyNetIncome = 0;
		if (dairyList.size() > 0) {
			boolean hasApplicant = false;
			boolean hasCoApplicant = false;
			for (BCMPIIncomeDetailsWrapper.Dairy dairy : dairyList) {
				if (Constants.APPLICANT.equalsIgnoreCase(dairy.getDairyType())) {
					hasApplicant = true;
				} else if (Constants.CO_APPLICANT.equalsIgnoreCase(dairy.getDairyType())) {
					hasCoApplicant = true;
				}
				dairyOwnedBy = dairy.getDairyType();
				dairyNetIncome += dairy.getIncomeAssessmentChecknetBusinessIncome().intValue();
			}
			if (hasApplicant && hasCoApplicant)
				dairyOwnedBy = Constants.BOTH;
		}
		String kiranaOwnedBy = "";
		int kiranaNetIncome = 0;
		if (kiranaList.size() > 0) {
			boolean hasApplicant = false;
			boolean hasCoApplicant = false;
			for (BCMPIIncomeDetailsWrapper.Kirana kirana : kiranaList) {
				if (Constants.APPLICANT.equalsIgnoreCase(kirana.getKiranaType())) {
					hasApplicant = true;
				} else if (Constants.CO_APPLICANT.equalsIgnoreCase(kirana.getKiranaType())) {
					hasCoApplicant = true;
				}
				kiranaOwnedBy = kirana.getKiranaType();
				kiranaNetIncome += kirana.getFinalNetIncome().intValue();
			}
			if (hasApplicant && hasCoApplicant)
				kiranaOwnedBy = Constants.BOTH;
		}
		String tailoringOwnedBy = "";
		int tailoringNetIncome = 0;
		if (tailoringList.size() > 0) {
			boolean hasApplicant = false;
			boolean hasCoApplicant = false;
			for (BCMPIIncomeDetailsWrapper.Tailoring tailoring : tailoringList) {
				if (Constants.APPLICANT.equalsIgnoreCase(tailoring.getTailoringType())) {
					hasApplicant = true;
				} else if (Constants.CO_APPLICANT.equalsIgnoreCase(tailoring.getTailoringType())) {
					hasCoApplicant = true;
				}
				tailoringOwnedBy = tailoring.getTailoringType();
				tailoringNetIncome += tailoring.getNetBusinessIncome().intValue();
			}
			if (hasApplicant && hasCoApplicant)
				tailoringOwnedBy = Constants.BOTH;
		}
		String otherOwnedBy = "";
		int otherNetIncome = 0;
		if (otherList.size() > 0) {
			boolean hasApplicant = false;
			boolean hasCoApplicant = false;
			for (BCMPIIncomeDetailsWrapper.OtherBusiness other : otherList) {
				if (Constants.APPLICANT.equalsIgnoreCase(other.getOtherType())) {
					hasApplicant = true;
				} else if (Constants.CO_APPLICANT.equalsIgnoreCase(other.getOtherType())) {
					hasCoApplicant = true;
				}
				otherOwnedBy = other.getOtherType();
				otherNetIncome += other.getNetBusinessIncome().intValue();
			}
			if (hasApplicant && hasCoApplicant)
				otherOwnedBy = Constants.BOTH;
		}
		if (wage != null) {
			boolean hasApplicant = wage.getApplicant() != null;
			boolean hasCoApplicant = wage.getCoApplicant() != null;
			if (hasApplicant && hasCoApplicant) {
				wageIncome = BCMPIIncomeDetailsWrapper.calculateWageIncome(wage, Constants.APPLICANT)
						.add(BCMPIIncomeDetailsWrapper.calculateWageIncome(wage, Constants.CO_APPLICANT));
				wageCustType = Constants.BOTH;
			} else if (hasApplicant) {
				wageIncome = BCMPIIncomeDetailsWrapper.calculateWageIncome(wage, Constants.APPLICANT);
				wageCustType = Constants.APPLICANT;
			} else if (hasCoApplicant) {
				wageIncome = BCMPIIncomeDetailsWrapper.calculateWageIncome(wage, Constants.CO_APPLICANT);
				wageCustType = Constants.CO_APPLICANT;
			}
		}
		if (agri != null) {
			boolean hasApplicant = agri.getApplicant() != null;
			boolean hasCoApplicant = agri.getCoApplicant() != null;
			if (hasApplicant && hasCoApplicant) {
				agriIncome = new BigDecimal(agri.getApplicant().getConsideredIncome())
						.add(new BigDecimal(agri.getCoApplicant().getConsideredIncome()));
				agriCustType = Constants.BOTH;
			} else if (hasApplicant) {
				agriIncome = new BigDecimal(agri.getApplicant().getConsideredIncome());
				agriCustType = Constants.APPLICANT;
			} else if (hasCoApplicant) {
				agriIncome = new BigDecimal(agri.getCoApplicant().getConsideredIncome());
				agriCustType = Constants.CO_APPLICANT;
			}
		}
		if (salary != null) {
			boolean hasApplicant = salary.getApplicant() != null;
			boolean hasCoApplicant = salary.getCoApplicant() != null;
			if (hasApplicant && hasCoApplicant) {
				salaryIncome = BigDecimal.valueOf(Integer.parseInt(salary.getApplicant().getNetSalary())
						+ Integer.parseInt(salary.getCoApplicant().getNetSalary()));
				salaryCustType = Constants.BOTH;
			} else if (hasApplicant) {
				salaryIncome = new BigDecimal(salary.getApplicant().getNetSalary());
				salaryCustType = Constants.APPLICANT;
			} else if (hasCoApplicant) {
				salaryIncome = new BigDecimal(Integer.parseInt(salary.getCoApplicant().getNetSalary()));
				salaryCustType = Constants.CO_APPLICANT;
			}
		}
		if (pension != null) {
			boolean hasApplicant = pension.getApplicant() != null;
			boolean hasCoApplicant = pension.getCoApplicant() != null;
			if (hasApplicant && hasCoApplicant) {
				pensionIncome = new BigDecimal(pension.getApplicant().getApplicantPensionIncome())
						.add(new BigDecimal(pension.getCoApplicant().getCoApplicantPensionIncome()));
				pensionCustType = Constants.BOTH;
			} else if (hasApplicant) {
				pensionIncome = new BigDecimal(pension.getApplicant().getApplicantPensionIncome());
				pensionCustType = Constants.APPLICANT;
			} else if (hasCoApplicant) {
				pensionIncome = new BigDecimal(pension.getCoApplicant().getCoApplicantPensionIncome());
				pensionCustType = Constants.CO_APPLICANT;
			}
		}
		if (rental != null) {
			boolean hasApplicant = rental.getApplicant() != null;
			boolean hasCoApplicant = rental.getCoApplicant() != null;
			if (hasApplicant && hasCoApplicant) {
				rentalIncome = (rental.getApplicant().getConsideredMonthlyIncome()
						.add(rental.getCoApplicant().getConsideredMonthlyIncome()));
				rentalCustType = Constants.BOTH;
			} else if (hasApplicant) {
				rentalIncome = rental.getApplicant().getConsideredMonthlyIncome();
				rentalCustType = Constants.APPLICANT;
			} else if (hasCoApplicant) {
				rentalIncome = rental.getCoApplicant().getConsideredMonthlyIncome();
				rentalCustType = Constants.CO_APPLICANT;
			}
		}
		try {
			verticalList.add(createThreeHorizontalListWithCustomisedWidth2Bold(keysForContent.getString("incomeSource"),
					keysForContent.getString("ownedBy"), keysForContent.getString("netIncome")));
			verticalList.add(createSingleHorizontalList(keysForContent.getString("businessIncome"))
					.setStyle(stl.style().setBackgroundColor(new Color(204, 229, 255))));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("businessDairy"),
					dairyOwnedBy, CommonUtils.amountFormat(String.valueOf(dairyNetIncome))));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("businessKirana"),
					kiranaOwnedBy, CommonUtils.amountFormat(String.valueOf(kiranaNetIncome))));
			verticalList
					.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("businessTailoring"),
							tailoringOwnedBy, CommonUtils.amountFormat(String.valueOf(tailoringNetIncome))));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("businessOther"),
					otherOwnedBy, CommonUtils.amountFormat(String.valueOf(otherNetIncome))));
			verticalList.add(createSingleHorizontalList(keysForContent.getString("otherIncome"))
					.setStyle(stl.style().setBackgroundColor(new Color(204, 255, 204))));

			verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("agriculture"),
					agriCustType, CommonUtils.amountFormat(String.valueOf(agriIncome))));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("salary"),
					salaryCustType, CommonUtils.amountFormat(String.valueOf(salaryIncome))));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("wage"),
					wageCustType, CommonUtils.amountFormat(String.valueOf(wageIncome))));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("pension"),
					pensionCustType, CommonUtils.amountFormat(String.valueOf(pensionIncome))));
			verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("rental"),
					rentalCustType, CommonUtils.amountFormat(String.valueOf(rentalIncome))));
			verticalList
					.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("totalHouseHoldIncome"),
							CommonUtils
									.amountFormat(String.valueOf(bcmpiIncomeDetailsWrapper.getFieldAssessedIncome())),
							70, 30, boldTextWithBorder)
									.setStyle(stl.style().setBackgroundColor(new Color(255, 204, 255))));
		} catch (Exception ex) {
			logger.error("error - getFinalIncomeDetails");
			logger.error(ex.getMessage());
		}

		return verticalList;
	}

	private ComponentBuilder<?, ?> getInprincipalDecisionDetails(JSONObject keysForContent,
			CustomerDataFields customerFields) {

		VerticalListBuilder verticalList = cmp.verticalList();
		Gson gsonObj = new Gson();
		Gson gsonObj2 = new Gson();
		Gson gsonObj3 = new Gson();
		String cbDateStr ="";
		try {
			String bmLoanAmount = String
					.valueOf(customerFields.getLoanDetails().getBmRecommendedLoanAmount().intValue());
			logger.debug("bmLoanAmount : " + bmLoanAmount);
//			String roi = String.valueOf(customerFields.getLoanDetails().getRoi());
			LoanDetailsPayload payload2 = gsonObj2.fromJson(customerFields.getLoanDetails().getPayloadColumn(),
					LoanDetailsPayload.class);
            String outstandingAmount = String.valueOf(0);
            if(customerFields.getApplicationMaster().getProductCode().equalsIgnoreCase(Constants.UNNATI_PRODUCT_CODE)) {
                outstandingAmount = customerFields.getLeadDetails().getCaglOs();
            }else if(customerFields.getApplicationMaster().getProductCode().equalsIgnoreCase(Constants.RENEWAL_LOAN_PRODUCT_CODE)){
                outstandingAmount = customerFields.getRenewalLeadDetails().getCaglOs();
            }
			logger.debug("outstandingAmount : " + outstandingAmount);
			
					
			//after sanction need use Sanction Loan Amount instead of BmRecommendedLoanAmount()
			BigDecimal sanctionAmtDb = ((null==customerFields.getLoanDetails().getSanctionedLoanAmount())? BigDecimal.ZERO:customerFields.getLoanDetails().getSanctionedLoanAmount());
	
			int bmLoanAmountInt = Integer.parseInt(bmLoanAmount);
				 try {
				        BigDecimal bmLoan = new BigDecimal(bmLoanAmount.trim());
				        if ( sanctionAmtDb.compareTo(BigDecimal.ZERO) > 0 && sanctionAmtDb.compareTo(bmLoan) <= 0 ) {
				        	logger.debug("sanction is less than or equal to bmLoanAmount");
//				        	sanction is less than or equal to bmLoanAmount
//				            bmLoanAmount = sanctionAmt; // Use the lesser amount
				        	bmLoanAmountInt = sanctionAmtDb.intValue(); 
				        	bmLoanAmount = sanctionAmtDb.toPlainString();
				        }
				    } catch (NumberFormatException e) {
				        // Handle invalid bmLoanAmount string
				    	logger.warn("Invalid bmLoanAmount: {}", bmLoanAmount);
				    }
			

			for (CibilDetailsWrapper cibilDetailsWrapper : customerFields.getCibilDetailsWrapperList()) {
				String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
//			String customerType = applicant ? applicantCustId : coApplicantCustId;
				CibilDetailsPayload payload = gsonObj.fromJson(cibilDetailsWrapper.getCibilDetails().getPayloadColumn(),
						CibilDetailsPayload.class);
				logger.debug("CreditDetailsPayload Payload : " + payload);
				if (custId.equals(coApplicantCustId)) {
					LocalDate cbDateDb = cibilDetailsWrapper.getCibilDetails().getCbDate();
					cbDateStr = CommonUtils.getCurDateMinusOne(cbDateDb);
					
					int totalInsurance = toFindSum(payload.getInsuranceChargeJoint(),
							payload.getInsuranceChargeMember(), payload.getInsuranceChargeSpouse());
					logger.debug("totalInsurance : " + totalInsurance);
					
					int netOffAmt = toFindSum(outstandingAmount, payload.getStampDutyCharge(),
							String.valueOf(totalInsurance), payload.getProcessingFees());
					logger.debug("netOffAmt : " + netOffAmt);
	
					int postNetOff = toFindDifference(payload.getEligibleAmt(), String.valueOf(netOffAmt)); //sanctined - netoff
					logger.debug("postNetOff : " +  String.valueOf(postNetOff));
					
//					int postNetOff2 = Integer.parseInt(bmLoanAmount) - netOffAmt;
					int postNetOff2 = bmLoanAmountInt - netOffAmt;

					verticalList.add(createThreeHorizontalListWithCustomisedWidth2Bold(
							keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString("breResponse"),
							keysForContent.getString("bmRecommended")));
					verticalList.add(createThreeHorizontalListWithCustomisedWidth2(
							keysForContent.getString("loanAmount"), CommonUtils.amountFormat(payload.getEligibleAmt()),
							CommonUtils.amountFormat(bmLoanAmount == null ? "0" : bmLoanAmount)));
					verticalList.add(createThreeHorizontalListWithCustomisedWidth2(
							keysForContent.getString("rateOfInterest"), payload.getRoi() + "%", payload.getRoi() + "%"));
					verticalList.add(
							createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("processingFee"),
									CommonUtils.amountFormat(payload.getProcessingFees()),
									CommonUtils.amountFormat(payload.getProcessingFees())));
					// InsuranceDetailsWrapper
					verticalList.add(
							createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("insurancePremium"),
									CommonUtils.amountFormat(String.valueOf(totalInsurance)),
									CommonUtils.amountFormat(String.valueOf(totalInsurance))));
					verticalList.add(
							createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("stampDutyCharges"),
									CommonUtils.amountFormat(payload.getStampDutyCharge()),
									CommonUtils.amountFormat(payload.getStampDutyCharge())));
					// existingloanDetails
					verticalList.add(createThreeHorizontalListWithCustomisedWidth2(
							keysForContent.getString("currentCAGLOutstanding") + cbDateStr +")",
							CommonUtils.amountFormat(outstandingAmount), CommonUtils.amountFormat(outstandingAmount)));
					verticalList
							.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("netOffAmount"),
									CommonUtils.amountFormat(String.valueOf(netOffAmt)),
									CommonUtils.amountFormat(String.valueOf(netOffAmt))));
					verticalList.add(createThreeHorizontalListWithCustomisedWidth2(
							keysForContent.getString("totalAmountPostNetOff"),
							CommonUtils.amountFormat(String.valueOf(postNetOff)),
							CommonUtils.amountFormat(String.valueOf(postNetOff2))));
					verticalList.add(createThreeHorizontalListWithCustomisedWidth2(
							keysForContent.getString("instalmentAmountFrequency"), payload.getApprovedLoanEMI() + " / " + payload.getRepaymentFrequency(),
							payload.getApprovedLoanEMI() + " / " + payload2.getFrequencyOfRepayment()));
				}
			}
		} catch (Exception e) {
			logger.error("error - getInprincipalDecisionDetails", e);
			logger.error(e.getMessage());
		}

		return verticalList;
	}

	private int toFindSum(String... value) {
		int totalInsurance = Stream.of(value).map(val -> {
			try {
				return Integer.parseInt(Optional.ofNullable(val).orElse("0"));
			} catch (NumberFormatException e) {
				return 0;
			}
		}).reduce(0, Integer::sum);
		return totalInsurance;
	}

	private int toFindDifference(String val1, String val2) {
		try {
			int num1 = Integer.parseInt(Optional.ofNullable(val1).orElse("0").replaceAll(",", ""));
			int num2 = Integer.parseInt(Optional.ofNullable(val2).orElse("0").replaceAll(",", ""));
			return num1 - num2;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

//	private ComponentBuilder<?, ?> getEmployemnentDetails(JSONObject keysForContent,
//			boolean applicant) {
//
//		VerticalListBuilder verticalList = cmp.verticalList();
//		try {
//			Gson gsonObj = new Gson();
//			OccupationDetailsPayload payload = null;
//			for (OccupationDetailsWrapper applicantwrpr : req.getOccupationDetailsWrapperList()) {
//				String custId = applicantwrpr.getOccupationDetails().getCustDtlId().toString();
//				String customerType = applicant ? applicantCustId : coApplicantCustId;
//				logger.debug("applicantCustId " + applicantCustId);
//				if (custId.equals(customerType)) {
//					payload = gsonObj.fromJson(applicantwrpr.getOccupationDetails().getPayloadColumn(),
//							OccupationDetailsPayload.class);
//					logger.debug("occupationPayload : " + payload.toString());
//
//					verticalList.add(createSixHorizontalList(keysForContent.getString("employmentTye"),
//							payload.getOccupationType(), keysForContent.getString("empFormat"),
//							payload.getNatureOfOccupation(), keysForContent.getString("empActivity"),
//							payload.getEmployeeActivity()));
//					verticalList.add(createSixHorizontalList(keysForContent.getString("empstreetVendor"),
//							payload.getStreetVendor(), keysForContent.getString("empExprience"),
//							payload.getExperience(), keysForContent.getString("empSize"), ""));
//					verticalList.add(createSixHorizontalList(keysForContent.getString("empOwnership"),
//							payload.getBusinessPremiseOwnerShip(), "", "", "", ""));
//				}
//			}
//
//		} catch (Exception e) {
//			logger.error("error - EmployemnentDetails");
//			logger.error(e.getMessage());
//		}
//		logger.debug("Occupation Details added");
//
//		return verticalList;
//	}
	private JSONObject getAllAddressDeatils(CustomerDataFields req) {
		logger.debug("Entry - getAllAddressDetails method");
		JSONObject jsnObj = null;
		try {
			Gson gsonObj = new Gson();

			List<Address> applicantAddrPayLoadLst = null;
			List<Address> coApplicantAddrPayLoadLst = null;

			List<Address> applicantOccupnAddrPayLoadLst = null;
			List<Address> coApplicantOccupnAddrPayLoadLst = null;

			for (AddressDetails addr : req.getAddressDetailsWrapperList().get(0).getAddressDetailsList()) {
				if (addr.getAddressType().equalsIgnoreCase("Personal")
						&& (String.valueOf(addr.getCustDtlId()).equals(applicantCustId))) {
					AddressDetailsPayload applicantPayload = gsonObj.fromJson(addr.getPayloadColumn(),
							AddressDetailsPayload.class);
					applicantAddrPayLoadLst = applicantPayload.getAddressList();
					logger.debug("PersonalAddLstApplicant :" + applicantAddrPayLoadLst);
				} else if (addr.getAddressType().equalsIgnoreCase("Personal")
						&& (String.valueOf(addr.getCustDtlId()).equals(coApplicantCustId))) {
					AddressDetailsPayload coApplicantPayload = gsonObj.fromJson(addr.getPayloadColumn(),
							AddressDetailsPayload.class);
					coApplicantAddrPayLoadLst = coApplicantPayload.getAddressList();
					logger.debug("PersonalAddLstCo-aaplicant :" + coApplicantAddrPayLoadLst);
				}

				// occupation Address
				if (addr.getAddressType().equalsIgnoreCase("Occupation")
						&& (String.valueOf(addr.getCustDtlId()).equals(applicantCustId))) {
					AddressDetailsPayload applicantPayload = gsonObj.fromJson(addr.getPayloadColumn(),
							AddressDetailsPayload.class);
					applicantOccupnAddrPayLoadLst = applicantPayload.getAddressList();
					logger.debug("PersonalAddLstApplicant - Occupation :" + applicantOccupnAddrPayLoadLst);
				} else if (addr.getAddressType().equalsIgnoreCase("Occupation")
						&& (String.valueOf(addr.getCustDtlId()).equals(coApplicantCustId))) {
					AddressDetailsPayload coApplicantPayload = gsonObj.fromJson(addr.getPayloadColumn(),
							AddressDetailsPayload.class);
					coApplicantOccupnAddrPayLoadLst = coApplicantPayload.getAddressList();
					logger.debug("PersonalAddLstCoApplicant - Occupation :" + coApplicantOccupnAddrPayLoadLst);
				}
			}

			// Address
			String presentAddressApplicant = "";
			String permanetAddressApplicant = "";

			String presentAddressCoApplicant = "";
			String permanetAddressCoApplicant = "";

			// other Deatils
			String presentResidenceOwnership = "";
			String presentAddressYears = "";
			String presntCityYears = "";
			String presentResidenceAddressProof = "";
			String presentResidenceType = "";
			String presentResidenceSize = "--";

			String presentResidenceOwnershipCo = "";
			String presentAddressYearsCo = "";
			String presntCityYearsCo = "";
			String presentResidenceAddressProofCo = "";
			String presentResidenceTypeCo = "";
			String presentResidenceSizeCo = "--";

			// Occupation Address
			String occpnAddrApplicant = "";
			String occpnAddrCoApplicant = "";

			// Applicant Address
			for (Address addr : applicantAddrPayLoadLst) {
				if (addr.getAddressType().equalsIgnoreCase("present")) {
//					presentAddressApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3()
//							+ addr.getArea() +  addr.getLandMark() + addr.getCity() + addr.getDistrict() + addr.getState()
//							+ addr.getCountry() + addr.getPinCode();

					presentAddressApplicant = getFullAddress(addr);
					logger.debug("Present Address - Applicant : " + presentAddressApplicant);
					presentResidenceOwnership = addr.getResidenceOwnership();
					presentAddressYears = addr.getResidenceAddressSince();
					presntCityYears = addr.getResidenceCitySince();
					presentResidenceAddressProof = addr.getCurrentAddressProof();
					presentResidenceType = addr.getHouseType();
				} else if (addr.getAddressType().equalsIgnoreCase("Permanent")) {
//					permanetAddressApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3()
//							+ addr.getArea()  + addr.getLandMark() + addr.getCity() + addr.getDistrict() + addr.getState()
//							+ addr.getCountry() + addr.getPinCode();
					permanetAddressApplicant = getFullAddress(addr);
					logger.debug("Permanent Address - Applicant :" + permanetAddressApplicant);
				}
			}

			// Co-Applicant Address
			if (coApplicantAddrPayLoadLst != null) {
				for (Address addr : coApplicantAddrPayLoadLst) {
					if (addr.getAddressType().equalsIgnoreCase("present")) {
//						presentAddressCoApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3() + addr.getArea() + addr.getLandMark() + addr.getCity() + addr.getDistrict()+ addr.getState() + addr.getCountry() + addr.getPinCode();
						presentAddressCoApplicant = getFullAddress(addr);
						logger.debug("Present Address - Co-applicant :" + presentAddressCoApplicant);
						presentResidenceOwnershipCo = addr.getResidenceOwnership();
						presentAddressYearsCo = addr.getResidenceAddressSince();
						presntCityYearsCo = addr.getResidenceCitySince();
						presentResidenceAddressProofCo = addr.getCurrentAddressProof();
						presentResidenceTypeCo = addr.getHouseType();
					} else if (addr.getAddressType().equalsIgnoreCase("Permanent")) {
//						permanetAddressCoApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3() + addr.getArea() + addr.getLandMark() + addr.getCity() + addr.getDistrict()+ addr.getState() + addr.getCountry() + addr.getPinCode();
						permanetAddressCoApplicant = getFullAddress(addr);
						logger.debug("permanent Address - Co-applicant. :" + permanetAddressCoApplicant);
					}
				}
			}

			// Occupation Address
			Address ocupnAddr = (applicantOccupnAddrPayLoadLst==null?null:applicantOccupnAddrPayLoadLst.get(0));
			Address ocupnAddrCo = (coApplicantOccupnAddrPayLoadLst==null?null:coApplicantOccupnAddrPayLoadLst.get(0));
//			if(addr.getAddressType().equalsIgnoreCase("Office")) {
			occpnAddrApplicant = getFullAddress(ocupnAddr);
			occpnAddrCoApplicant = getFullAddress(ocupnAddrCo);
			logger.debug("Ocupation Address Applicnt : " + occpnAddrApplicant);
			logger.debug("Ocupation Address Co-Applicnt : " + occpnAddrCoApplicant);
//				ocupnAddr.getAddressLine1() + ocupnAddr.getAddressLine2() + ocupnAddr.getAddressLine3()
//				+ ocupnAddr.getArea() +  ocupnAddr.getLandMark() + ocupnAddr.getCity() + ocupnAddr.getDistrict() + ocupnAddr.getState()
//				+ ocupnAddr.getCountry() + ocupnAddr.getPinCode();

//			}else if(addr.getAddressType().equalsIgnoreCase("Office")) {
//				occpnAddrCoApplicant = ocupnAddrCo.getAddressLine1() + ocupnAddrCo.getAddressLine2() + ocupnAddrCo.getAddressLine3()
//				+ ocupnAddrCo.getArea() +  ocupnAddrCo.getLandMark() + ocupnAddrCo.getCity() + ocupnAddrCo.getDistrict() + ocupnAddrCo.getState()
//				+ ocupnAddrCo.getCountry() + ocupnAddrCo.getPinCode();
//				}

			jsnObj = new JSONObject();
			jsnObj.put("presentAddressApplicant", presentAddressApplicant);
			jsnObj.put("permanetAddressApplicant", permanetAddressApplicant);
			jsnObj.put("presentAddressCoApplicant", presentAddressCoApplicant);
			jsnObj.put("permanetAddressCoApplicant", permanetAddressCoApplicant);

			// other Deatils
			jsnObj.put("presentResidenceOwnership", presentResidenceOwnership);
			jsnObj.put(Constants.PRESENT_ADDRESS_IN_YEARS, presentAddressYears);
			jsnObj.put(Constants.PRESNT_CITY_IN_YEARS, presntCityYears);
			jsnObj.put("presentResidenceAddressProof", presentResidenceAddressProof);
			jsnObj.put("presentResidenceType", presentResidenceType);
			jsnObj.put("presentResidenceSize", presentResidenceSize);

			jsnObj.put("presentResidenceOwnershipCo", presentResidenceOwnershipCo);
			jsnObj.put("presentAddressYearsCo", presentAddressYearsCo);
			jsnObj.put("presntCityYearsCo", presntCityYearsCo);
			jsnObj.put("presentResidenceAddressProofCo", presentResidenceAddressProofCo);
			jsnObj.put("presentResidenceTypeCo", presentResidenceTypeCo);
			jsnObj.put("presentResidenceSizeCo", presentResidenceSizeCo);

			// Occupation Address
			jsnObj.put("occpnAddrApplicant", occpnAddrApplicant);
			jsnObj.put("occpnAddrCoApplicant", occpnAddrCoApplicant);

		} catch (Exception e) {
			logger.error("error - getAllAddressDeatils Method");
			logger.error(e.getMessage());
		}
		logger.debug("Exit - getAllAddressDetails method completed.");
		return jsnObj;
	}

	private static String getFullAddress(Address address) {
		// Retrieve individual components from the Address object
		if (address == null) {
			return "";
		}
		String[] addressArr = { address.getAddressLine1(), address.getAddressLine2(), address.getAddressLine3(),
				address.getArea(), address.getLandMark(), address.getDistrict(), address.getCity(), address.getState(),
				address.getCountry(), address.getPinCode()

		};

		// Construct the full address
		StringBuilder totalAddress = new StringBuilder();
		for (String part : addressArr) {
			if (part != null && !part.isEmpty()) { // Ensure non-null and non-empty
				totalAddress.append(part).append(", ");
			}
		}

		// Remove trailing comma, if any
		if (totalAddress.length() > 0 && totalAddress.toString().endsWith(", ")) {
			totalAddress.setLength(totalAddress.length() - 2);
		}

		return totalAddress.toString();
	}

	private ComponentBuilder<?, ?> getDeviationDetails(JSONObject keysForContent, CustomerDataFields req,
			List<DeviationRATracker> deviationRecords) {

		VerticalListBuilder verticalList = cmp.verticalList();

		verticalList.add(createThreeHorizontalListWithCustomisedWidth2Bold(keysForContent.getString(Constants.FIELD_NAME),
				keysForContent.getString(Constants.FIELD_VALUE), keysForContent.getString("deviationAuthority")));
		int count = 1;
		try {
			logger.debug("ApplicationId " + req.getApplicationId());

			for (DeviationRATracker deviationRATracker : deviationRecords) {
				logger.debug("Checking recordType: " + deviationRATracker.getRecordType());
				if ("CA_DEVIATION".equalsIgnoreCase(deviationRATracker.getRecordType())) {
					verticalList.add(createThreeHorizontalListWithCustomisedWidth2("DV " + count,
							deviationRATracker.getRecordMsg(), deviationRATracker.getAuthority()));
					count++;
				}
			}
			if (deviationRecords.isEmpty()) {
				verticalList.add(createThreeHorizontalListWithCustomisedWidth2("--", "--", "--"));
				logger.debug("Records is empty");
			}
		} catch (Exception e) {
			logger.error("Error in getDeviationDetails", e);
		}

		return verticalList;
	}

	// Deviation
	private ComponentBuilder<?, ?> getReassessmentDetails(JSONObject keysForContent, CustomerDataFields req,
			List<DeviationRATracker> deviationRecords) {
		VerticalListBuilder verticalList = cmp.verticalList();
		try {
			verticalList.add(createThreeHorizontalListWithCustomisedWidth2Bold(keysForContent.getString(Constants.FIELD_NAME),
					keysForContent.getString(Constants.FIELD_VALUE), keysForContent.getString("reassessmentAuthority")));
			int count = 1;
			for (DeviationRATracker deviationRATracker : deviationRecords) {
				logger.debug("Checking recordType: " + deviationRATracker.getRecordType());
				if ("REASSESSMENT".equalsIgnoreCase(deviationRATracker.getRecordType())) {
					verticalList.add(createThreeHorizontalListWithCustomisedWidth2("RA " + count,
							deviationRATracker.getRecordMsg(), deviationRATracker.getAuthority()));
					count++;
				}
			}
			if (deviationRecords.isEmpty()) {
				verticalList.add(createThreeHorizontalListWithCustomisedWidth2("--", "--", "--"));
			}
		} catch (Exception e) {
			logger.error("Error in getDeviationDetails", e);
		}
		return verticalList;
	}

	private ComponentBuilder<?, ?> getSanctionCondition(JSONObject keysForContent, CustomerDataFields req,
			Optional<SanctionMaster> sanctionMaster) {
		VerticalListBuilder verticalList = cmp.verticalList();
		verticalList.add(createTwoHorizontalList(keysForContent.getString("sanctionCondition"),
				keysForContent.getString("raisedBy"), boldTextWithBorder));
		try {
			if (sanctionMaster.isPresent()) {
				SanctionMaster sanction = sanctionMaster.get();
				Map<String, String> map = new HashMap<>();
				map.put("AM", sanction.getAm());
				map.put("BM", sanction.getBm());
				map.put("RM", sanction.getRm());
				map.put("DM", sanction.getDm());

				String sanctioner = map.entrySet().stream().filter(entry -> "Y".equals(entry.getValue()))
						.map(Map.Entry::getKey).collect(Collectors.joining("/"));
				verticalList.add(createTwoHorizontalList(sanctioner, "--", borderedStyle));
			} else {
				verticalList.add(createTwoHorizontalList("--", "--", borderedStyle));
			}
		} catch (Exception e) {
			logger.error("GetSanctionConditon ", e);
		}

		return verticalList;
	}

	private ComponentBuilder<?, ?> getOfficerDetails(JSONObject keysForContent, CustomerDataFields customerFields, String gkUserId, String gkUserName) {
		VerticalListBuilder verticalList = cmp.verticalList();

		verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("recommendingOfficerName"),
				gkUserName, 40, 60, borderedStyle));
		verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("gkId"),
				gkUserId, 40, 60, borderedStyle)); //bmId
		return verticalList;
	}

	private ComponentBuilder<?, ?> createTwoHorizontalListWithCustomisedWidth(String Key, String value, int firstWidth,
			int secondWidth, ReportStyleBuilder style) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key).setStyle(style).setWidth(firstWidth));
		horizontalList.add(cmp.text(value).setStyle(style).setWidth(secondWidth));

		return horizontalList;
	}

	private ComponentBuilder<?, ?> createThreeHorizontalListWithCustomisedWidth(String Key, String value1,
			String value2, ReportStyleBuilder style) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key).setStyle(style).setWidth(26));
		horizontalList.add(cmp.text(value1).setStyle(style).setWidth(37));
		horizontalList.add(cmp.text(value2).setStyle(style).setWidth(37));

		return horizontalList;
	}

	private ComponentBuilder<?, ?> createThreeHorizontalListWithCustomisedWidth2(String Key, String value1,
			String value2) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setWidth(40));
		horizontalList.add(cmp.text(value1).setStyle(borderedStyle).setWidth(30));
		horizontalList.add(cmp.text(value2).setStyle(borderedStyle).setWidth(30));

		return horizontalList;
	}

	private ComponentBuilder<?, ?> createThreeHorizontalListWithCustomisedWidth2Bold(String Key, String value1,
			String value2) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key).setStyle(boldTextWithBorder).setWidth(40));
		horizontalList.add(cmp.text(value1).setStyle(boldTextWithBorder).setWidth(30));
		horizontalList.add(cmp.text(value2).setStyle(boldTextWithBorder).setWidth(30));

		return horizontalList;
	}

	private ComponentBuilder<?, ?> createTwoHorizontalList(String Key, String value, ReportStyleBuilder style) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key).setStyle(style));
		horizontalList.add(cmp.text(value).setStyle(style));

		return horizontalList;
	}

	private ComponentBuilder<?, ?> createSingleHorizontalList(String value) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(value).setStyle(borderedStyle));

		return horizontalList;
	}

	private ComponentBuilder<?, ?> createEightHorizontalList(String Key1, String Value1, String Key2, String Value2,
			String Key3, String Value3, String Key4, String Value4) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key1).setStyle(borderedStyle));
		horizontalList.add(cmp.text(Value1).setStyle(borderedStyle));
		horizontalList.add(cmp.text(Key2).setStyle(borderedStyle));
		horizontalList.add(cmp.text(Value2).setStyle(borderedStyle));
		horizontalList.add(cmp.text(Key3).setStyle(borderedStyle));
		horizontalList.add(cmp.text(Value3).setStyle(borderedStyle));
		horizontalList.add(cmp.text(Key4).setStyle(borderedStyle));
		horizontalList.add(cmp.text(Value4).setStyle(borderedStyle));

		return horizontalList;
	}

	public Response getSuccessJson(String baseString) {
		logger.debug("Inside getSuccessJson");
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();

		responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
		logger.debug("responseCode added to responseHeader");
		responseBody.setResponseObj(
				"{\"base64\":\"" + baseString + "\", \"status\":\"" + ResponseCodes.SUCCESS.getValue() + "\"}");
		logger.debug("string added to resonseBody as responseObj");
		response.setResponseHeader(responseHeader);
		logger.debug("responseHeader added");
		response.setResponseBody(responseBody);

		logger.debug("SuccessJson created");
		return response;
	}

	public Response getFailureJson(String error) {
		logger.debug("Inside getFailureJson");
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();

		responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
		responseBody.setResponseObj(
				"{\"errorMessage\":\"" + error + "\", \"status\":\"" + ResponseCodes.FAILURE.getValue() + "\"}");
		response.setResponseHeader(responseHeader);
		response.setResponseBody(responseBody);
		logger.debug("FailureJson created");
		return response;
	}
	
	private String extractValue(String input) {
		// Normalize delimiters: Remove unnecessary spaces around ":" and ","
		String normalizedInput = CommonUtils.normalizeInput(input);

		// Find the first key-value pair ("Score : 650")
		int colonIndex = normalizedInput.indexOf(":");
		if (colonIndex != -1) {
			// Get the value after the colon
			int startIndex = colonIndex + 1;
			int endIndex = normalizedInput.indexOf(",", startIndex); // Find the next comma
			if (endIndex == -1) { // If no comma, find the next space or take till the end
				endIndex = normalizedInput.indexOf(" ", startIndex);
				if (endIndex == -1) {
					endIndex = normalizedInput.length(); // No space found; take till the end
				}
			}
			return normalizedInput.substring(startIndex, endIndex).trim(); // Extract and trim
		}
		return null; // No key-value pair found
	}
	
}
