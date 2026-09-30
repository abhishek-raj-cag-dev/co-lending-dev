package com.iexceed.appzillonbanking.cob.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import net.sf.dynamicreports.report.constant.*;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.payload.ApplicationTimelineDtl;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.Header;
import com.iexceed.appzillonbanking.cob.core.payload.LoanDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.RepaymentSchedule;
import com.iexceed.appzillonbanking.cob.core.payload.RepaymentScheduleDisbursed;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.ReportStyleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;

@Component
public class KfsReport {

	private static final Logger logger = LogManager.getLogger(KfsReport.class);

	private StyleBuilder borderedStyle, boldText, boldCenteredStyle, boldTextWithBorder, boldLeftStyle;

	private String applicantCustId="";
	private String coApplicantCustId ="";
	private String applicantName = "";

	String appltGender ="";
	String coAppltGender ="";

	static String space = "\u00a0\u00a0\u00a0";
	private String BLANK_STRING = " ";
	private String productName = "";


	public KfsReport() {

		borderedStyle = stl.style(stl.penThin()).setPadding(5);
		boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
		boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		boldText = stl.style().bold();
		boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

		// StyleBuilder headerStyle =
		// stl.style().setFontSize(20).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
//		rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
//		leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

	}

	public byte[] generatePdfForDbKit(JSONObject keysForContent, CustomerDataFields customerFields, String filePath, String productName1, List<RepaymentSchedule> repaymentList, String sanctionDateStr) throws DRException, IOException {
		logger.debug("onEntrty :: generatePdfForDbKit");
		productName = productName1;
		JasperReportBuilder report = new JasperReportBuilder();

		JasperReportBuilder subReport = new JasperReportBuilder();
		JasperReportBuilder subReport1 = new JasperReportBuilder();

		JasperReportBuilder subReport2 = new JasperReportBuilder();
		JasperReportBuilder subReport3 = new JasperReportBuilder();
		JasperReportBuilder subReport4 = new JasperReportBuilder();
		JasperReportBuilder subReport5 = new JasperReportBuilder();
		JasperReportBuilder subReport6 = new JasperReportBuilder();
		JasperReportBuilder subReport7 = new JasperReportBuilder();
		JasperReportBuilder subReport8 = new JasperReportBuilder();
		JasperReportBuilder insuranceCoverageReport = new JasperReportBuilder();
		JasperReportBuilder insuranceCoverageReportTitle = new JasperReportBuilder();

		try {
			report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));
			CustomerDetailsPayload payload1 = null;
			CustomerDetailsPayload payload2 =null;
			Gson gsonObj = new Gson();
			for(CustomerDetails custDtl : customerFields.getCustomerDetailsList()) {
				logger.debug("customer Type : " + custDtl.getCustomerType());
				if(custDtl.getCustomerType().equalsIgnoreCase("Applicant")) {
					applicantCustId = String.valueOf(custDtl.getCustDtlId());
					logger.debug("applicantCustId : " + applicantCustId);
					applicantName = custDtl.getCustomerName();
					payload1  = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
					logger.debug("custApplicantPayload :" + payload1);
					appltGender = payload1.getGender();
				}else if(custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
					coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
					logger.debug("coApplicantCustId : " + coApplicantCustId);
					payload2  = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
					logger.debug("custCo-ApplicantPayload :" + payload2);
					coAppltGender = payload2.getGender();
				}
			}


			CibilDetailsPayload cibilPayloadCoApp = null;

			for (CibilDetailsWrapper cibilDetailsWrapper : customerFields.getCibilDetailsWrapperList()) {
				String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
//			String customerType = applicant ? applicantCustId : coApplicantCustId;

				logger.debug("CreditDetailsPayload Payload : " + cibilPayloadCoApp);
				if (custId.equals(coApplicantCustId)) {
					cibilPayloadCoApp = gsonObj.fromJson(
							cibilDetailsWrapper.getCibilDetails().getPayloadColumn(), CibilDetailsPayload.class);
				}

			}

			int noOfEPIs = repaymentList.size();
			logger.debug("Number of Records: " + noOfEPIs);
			
			String repymtStartDate = "";
			String emi = "";
			if( repaymentList.size() > 0) {
				logger.debug("repaymentList size: " + repaymentList.size()); 
				emi = repaymentList.get(2).getTotalDue(); 
				String startDate = repaymentList.get(0).getDate();
				logger.debug("startDate from repaymentList: " + startDate);

				repymtStartDate = formatRepaymentStartDate(startDate);
				
			}else {
				logger.debug("repaymentList is empty" + repaymentList.size());
			}
			logger.debug("repymtStartDate: " + repymtStartDate);

			// Application Name
			subReport.title(cmp.text(keysForContent.getString("title")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle));
			subReport.title(cmp.text(keysForContent.getString("applicationName")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle));
			
			StyleBuilder boldCenteredStyle1 = stl.style()
				    .bold()
				    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
			subReport.title(cmp.text(keysForContent.getString("subHeading")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle1.setFontSize(10)));
			
			/* Basic Application Details */
			subReport1.title(getBasicAppnDetails(keysForContent, customerFields, cibilPayloadCoApp, noOfEPIs, emi, sanctionDateStr)).title(cmp.text(""));
			subReport2.title(getBasicAppnDetails2(keysForContent,cibilPayloadCoApp)).title(cmp.text(""));
			subReport3.title(getBasicAppnDetails3(keysForContent)).title(cmp.text(""));

			//Computation of APR
			subReport4.title(cmp.text(keysForContent.getString("ComputationOfAPR")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));
			subReport5.title(getComputationDetails(keysForContent, customerFields, cibilPayloadCoApp, repaymentList, noOfEPIs, emi, sanctionDateStr)).title(cmp.text(""));

			//Insurance coverage:
			insuranceCoverageReport.title(cmp.text(keysForContent.getString("InsuranceCoverage")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));
			insuranceCoverageReport.title(getInsuranceCoverageDetails(keysForContent, cibilPayloadCoApp)).title(cmp.text(""));

			//Repayment Schedule
			subReport6.title(cmp.text(keysForContent.getString("RepaymentSchedule")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));
			//		subReport7.title(getRepaymentDetails3(keysForContent, repaymentList)).title(cmp.text(""));
			subReport7.title(cmp.text(""));
			subReport8.title(cmp.text(keysForContent.getString("Note")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(10)));

			report
			.addSummary(cmp.subreport(subReport)).addSummary(cmp.subreport(subReport1))
			.addSummary(cmp.subreport(subReport2)).addSummary(cmp.subreport(subReport3))
			.addSummary(cmp.subreport(subReport4)).addSummary(cmp.subreport(subReport5))
			.addSummary(cmp.subreport(insuranceCoverageReport))
			.addSummary(cmp.subreport(subReport6))
			.summary(
					cmp.verticalList(
//            getRepaymentDetailsAsTable(keysForContent, repaymentList)
							getRepaymentDetailsAsTwoTables(keysForContent, repaymentList)
					)
			)
			.addSummary(cmp.subreport(subReport7))
			.addSummary(cmp.subreport(subReport8)).setDetailSplitType(SplitType.PREVENT);
			
			try {
				// Save report to file
				try (FileOutputStream fos = new FileOutputStream(filePath)) {
					report.toPdf(fos);
				}

				// Read file and encode to Base64
				return Files.readAllBytes(Paths.get(filePath));

			} catch (DRException e) {
				logger.error("Error generating PDF report: ", e);
			} catch (IOException e) {
				logger.error("Error handling file operations: ", e);
			} catch (Exception e) {
				logger.error("Unexpected error: ", e);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public Response generatePdf(JSONObject keysForContent, CustomerDataFields customerFields, String productName1, List<RepaymentSchedule> repaymentList, String sanctionDateStr, String language) throws DRException, IOException {
		logger.debug("inside generatePdf : ");
		Response response;
		String base64String = null;
		productName = productName1;
		String filePath = "";
		
		JasperReportBuilder report = new JasperReportBuilder();

		JasperReportBuilder subReport = new JasperReportBuilder();
		JasperReportBuilder subReport1 = new JasperReportBuilder();

		JasperReportBuilder subReport2 = new JasperReportBuilder();
		JasperReportBuilder subReport3 = new JasperReportBuilder();
		JasperReportBuilder subReport4 = new JasperReportBuilder();
		JasperReportBuilder subReport5 = new JasperReportBuilder();
		JasperReportBuilder subReport6 = new JasperReportBuilder();
		JasperReportBuilder subReport7 = new JasperReportBuilder();
		JasperReportBuilder subReport8 = new JasperReportBuilder();
		JasperReportBuilder insuranceCoverageReport = new JasperReportBuilder();
		JasperReportBuilder insuranceCoverageReportTitle = new JasperReportBuilder();

		try {
			report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));
			CustomerDetailsPayload payload1 = null;
			CustomerDetailsPayload payload2 =null;
			Gson gsonObj = new Gson();
			for(CustomerDetails custDtl : customerFields.getCustomerDetailsList()) {
				logger.debug("customer Type : " + custDtl.getCustomerType());
				if(custDtl.getCustomerType().equalsIgnoreCase("Applicant")) {
					applicantCustId = String.valueOf(custDtl.getCustDtlId());
					logger.debug("applicantCustId : " + applicantCustId);
					applicantName = custDtl.getCustomerName();

					payload1  = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
					logger.debug("custApplicantPayload :" + payload1);
					appltGender = payload1.getGender();
				}else if(custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
					coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
					logger.debug("coApplicantCustId : " + coApplicantCustId);
					payload2  = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
					logger.debug("custCo-ApplicantPayload :" + payload2);
					coAppltGender = payload2.getGender();
				}
			}

			
			CibilDetailsPayload cibilPayloadCoApp = null;
			for (CibilDetailsWrapper cibilDetailsWrapper : customerFields.getCibilDetailsWrapperList()) {
				String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
//		String customerType = applicant ? applicantCustId : coApplicantCustId;

				logger.debug("CreditDetailsPayload Payload : " + cibilPayloadCoApp);
				if (custId.equals(coApplicantCustId)) {
					cibilPayloadCoApp = gsonObj.fromJson(
							cibilDetailsWrapper.getCibilDetails().getPayloadColumn(), CibilDetailsPayload.class);

				}

			}

			int noOfEPIs = repaymentList.size();
			logger.debug("Number of Records: " + noOfEPIs);
			
			String repymtStartDate = "";
			String emi = "";
			if(repaymentList.size() > 0) {
				logger.debug("repaymentList size: " + repaymentList.size()); 
				emi = repaymentList.get(2).getTotalDue(); 
				String startDate = repaymentList.get(0).getDate();	

				repymtStartDate = formatRepaymentStartDate(startDate);
			}else {
				logger.debug("repaymentList is empty" + repaymentList.size());
			}
			logger.debug("repymtStartDate : " + repymtStartDate);
			// Application Name
			subReport.title(cmp.text(keysForContent.getString("title")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle));
			subReport.title(cmp.text(keysForContent.getString("applicationName")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle));
			
			StyleBuilder boldCenteredStyle1 = stl.style()
				    .bold()
				    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
			subReport.title(cmp.text(keysForContent.getString("subHeading")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle1.setFontSize(10)));

			/* Basic Application Details */
			subReport1.title(getBasicAppnDetails(keysForContent, customerFields, cibilPayloadCoApp, noOfEPIs, emi, sanctionDateStr)).title(cmp.text(""));
			subReport2.title(getBasicAppnDetails2(keysForContent, cibilPayloadCoApp)).title(cmp.text(""));
			subReport3.title(getBasicAppnDetails3(keysForContent)).title(cmp.text(""));

			//Computation of APR
			subReport4.title(cmp.text(keysForContent.getString("ComputationOfAPR")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));
			subReport5.title(getComputationDetails(keysForContent, customerFields, cibilPayloadCoApp, repaymentList, noOfEPIs, emi, sanctionDateStr)).title(cmp.text(""));
			//Insurance coverage:
			insuranceCoverageReport.title(cmp.text(keysForContent.getString("InsuranceCoverage")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));
			insuranceCoverageReport.title(getInsuranceCoverageDetails(keysForContent, cibilPayloadCoApp)).title(cmp.text(""));

			//Repayment Schedule
			subReport6.title(cmp.text(keysForContent.getString("RepaymentSchedule")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));

			subReport7.title(cmp.text(""));
			subReport8.title(cmp.text(keysForContent.getString("Note")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(10)));

			report
					.addSummary(cmp.subreport(subReport)).addSummary(cmp.subreport(subReport1))
					.addSummary(cmp.subreport(subReport2)).addSummary(cmp.subreport(subReport3))
					.addSummary(cmp.subreport(subReport4)).addSummary(cmp.subreport(subReport5))
					.addSummary(cmp.subreport(insuranceCoverageReport))
					.addSummary(cmp.subreport(subReport6))
					.summary(
							cmp.verticalList(
//		            getRepaymentDetailsAsTable(keysForContent, repaymentList)
									getRepaymentDetailsAsTwoTables(keysForContent, repaymentList)
							)
					)
					.addSummary(cmp.subreport(subReport7))
					.addSummary(cmp.subreport(subReport8));

			try {
				response = new Response();
				Properties prop = CommonUtils.readPropertyFile();
				// Construct file path
//			String filePathDest = prop1.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + "APZCBO"
//					+ "/LOAN/" + customerFields.getApplicationId()+ "/";

				String filePathDest = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + "APZCBO" + "/" + Constants.LOAN + "/"
						+ customerFields.getApplicationId() + "/";
				logger.debug("filePathDest :: {}", filePathDest);
				// Ensure directory exists
				File directory = new File(filePathDest);
				if (!directory.exists()) {
					boolean isCreated = directory.mkdirs();
					if (!isCreated) {
						throw new IOException("Failed to create directory: " + filePathDest);
					}
				}

				filePath = filePathDest + customerFields.getApplicationId() + "_KfsSheetReport" + ".pdf";

				 String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
				    logger.debug("inputLanguage : " + language);
				    
				    boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
				            .anyMatch(lang -> lang.equalsIgnoreCase(language));

				    if (isValidLanguage) {
				        // Generate HTML in-memory, no file creation
				        ByteArrayOutputStream htmlOut = new ByteArrayOutputStream();
				        report.toHtml(htmlOut);
				       
				        // Return raw HTML instead of Base64
				        base64String = htmlOut.toString(StandardCharsets.UTF_8.name()); 
				     // 
				        String htmlContent = base64String.replaceAll("(?i)<\\/?html>|<\\/?body>", "");

				        // Wrap properly in single HTML structure
				        StringBuilder mergedHtml = new StringBuilder();
				        mergedHtml.append("<html><body>");
				        mergedHtml.append(htmlContent);
				        mergedHtml.append("</body></html>");
						mergedHtml.append("<style>");
						mergedHtml.append("@page { size: A4; margin: 12mm; }");
						mergedHtml.append("body { margin: 0; padding: 0; }");
						mergedHtml.append("table { width: 100%; border-collapse: collapse; }");
						mergedHtml.append("tr, td { page-break-inside: avoid; }");
						mergedHtml.append("thead { display: table-header-group; }");
						mergedHtml.append("tfoot { display: table-footer-group; }");
						mergedHtml.append("</style>");
				        
				        JsonObject mergedHtmlJson = new JsonObject();
				        mergedHtmlJson.addProperty("base64", mergedHtml.toString());
				        mergedHtmlJson.addProperty("fileType", "html");
				        mergedHtmlJson.addProperty("status", ResponseCodes.SUCCESS.getValue());

				        Gson gson = new Gson();
				        response = getSuccessJson1(gson.toJson(mergedHtmlJson));
				        
				    } else {
				        // Normal PDF flow (write to disk)
				        try (FileOutputStream fos = new FileOutputStream(filePath)) {
				            report.toPdf(fos);
				        }

				        byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
				        base64String = java.util.Base64.getEncoder().encodeToString(inputfile);
				        
				        response = getSuccessJson(base64String);
				    }

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
		return response;
	}


	private ComponentBuilder<?,?> getInsuranceCoverageDetails(JSONObject keysForContent,  CibilDetailsPayload cibilPayloadCoApp){
		String memberInsuranceCharge = cibilPayloadCoApp.getInsuranceChargeMember();
		String spouseInsuranceCharge = cibilPayloadCoApp.getInsuranceChargeSpouse();
		VerticalListBuilder verticalList = cmp.verticalList();

		Map<String, Integer> insuranceChargeRow = new LinkedHashMap<>();
		insuranceChargeRow.put(keysForContent.getString("MemberInsurancePremium"), 25);
		insuranceChargeRow.put("Rs. "+ memberInsuranceCharge, 25);
		insuranceChargeRow.put(keysForContent.getString("SpouseInsurancePremium"), 25);
		insuranceChargeRow.put("Rs. "+ spouseInsuranceCharge +" ", 25); //Adding space at the end, so that the key can be unique when memberInsuranceCharge = spouseInsuranceCharge
		verticalList.add(createHorizontalList(insuranceChargeRow));

		return verticalList;
	}

	private ComponentBuilder<?, ?> getBasicAppnDetails(JSONObject keysForContent, CustomerDataFields custmrDataFields, CibilDetailsPayload cibilPayloadCoApp, int noOfEPIs, String emi, String sanctionDateStr) {

		VerticalListBuilder verticalList = cmp.verticalList();
		Gson gson = new Gson();
		String customerId = "";


		String interest = (cibilPayloadCoApp.getRoi() == null) ? "" : cibilPayloadCoApp.getRoi().toString();
		logger.debug("Interest rate :"+ interest);

		BankDetailsPayload payload = gson.fromJson(custmrDataFields.getBankDetailsWrapperList().get(0).getBankDetails().getPayloadColumn(), BankDetailsPayload.class);
//		LoanDetailsPayload loanPayload = gson.fromJson(custmrDataFields.getLoanDetails().getPayloadColumn(),
//				LoanDetailsPayload.class);
		logger.debug("LoanDetailsPayload : " + payload);
		
		int totalInsurance = toFindSum(cibilPayloadCoApp.getInsuranceChargeJoint(),
				cibilPayloadCoApp.getInsuranceChargeMember(), cibilPayloadCoApp.getInsuranceChargeSpouse());

		BigDecimal sactionAmtDb = custmrDataFields.getLoanDetails().getSanctionedLoanAmount();
		String sactionAmt = (sactionAmtDb == null) ? "" : sactionAmtDb.toPlainString();

		String productCode = custmrDataFields.getApplicationMaster().getProductCode();
		 customerId = productCode
				.equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE) ?
				 custmrDataFields.getApplicationMaster().getApplicantT24Id() :
				 custmrDataFields.getApplicationMaster().getSearchCode2();

		Map<String, Integer> row1 = new LinkedHashMap<>();
		row1.put(keysForContent.getString("customerName") + BLANK_STRING + applicantName, 50);
		row1.put(keysForContent.getString("customerId") + BLANK_STRING +
				customerId, 50);
		verticalList.add(createHorizontalList(row1));

		Map<String, Integer> row2 = new LinkedHashMap<>();
		row2.put(keysForContent.getString("kendraName") + BLANK_STRING +
				custmrDataFields.getApplicationMaster().getKendraName(), 50);
		row2.put(keysForContent.getString("kendraId") + BLANK_STRING +
				custmrDataFields.getApplicationMaster().getKendraId(), 25);
		row2.put(keysForContent.getString("branchName") + BLANK_STRING +
				custmrDataFields.getApplicationMaster().getBranchName(), 25);
		verticalList.add(createHorizontalList(row2));

		Map<String, Integer> row3 = new LinkedHashMap<>();
		row3.put("1",10);
		row3.put(keysForContent.getString("loanAccountNumber") + BLANK_STRING + custmrDataFields.getLoanDetails().getT24LoanId(),40);
		row3.put(keysForContent.getString("typeOfLoan") + BLANK_STRING + productName, 50);
		verticalList.add(createHorizontalList(row3));

		Map<String, Integer> row4 = new LinkedHashMap<>();
		row4.put("2",10);
		row4.put(keysForContent.getString("sanctionLoanAmount") + "<br/>" + CommonUtils.formatIndianCurrency(sactionAmt), 40);
		row4.put(keysForContent.getString("SanctionDate")+"<br/>"+ sanctionDateStr, 50);
		verticalList.add(createHorizontalList(row4));

		Map<String, Integer> row5 = new LinkedHashMap<>();
		row5.put("3",10);
		row5.put(keysForContent.getString("disbursementSchedule") + "<br/>" +
				keysForContent.getString("disbursementSchedule1") + "<br/>" +
				keysForContent.getString("disbursementSchedule2"), 40);
		row5.put("<br>100% Upfront<br/>NA", 50);
		verticalList.add(createHorizontalList(row5));
		
		verticalList.add(createThreeHorizontalList2("4", keysForContent.getString("loanTerm"), cibilPayloadCoApp.getFinalTenure() + "  "+  keysForContent.getString("months")));
		verticalList
				.add(createtwoHorizontalListkfs("5", keysForContent.getString("installmentDetails"), boldTextWithBorder)
						.setStyle(boldLeftStyle));
		verticalList.add(createFourHorizontalList(keysForContent.getString("instalmentType"),
				keysForContent.getString(Constants.EPI_NOS), keysForContent.getString("EPI"),
				keysForContent.getString("CommencementOfRepaymentPostSanction")));
		verticalList.add(createFourHorizontalList(cibilPayloadCoApp.getRepaymentFrequency(), String.valueOf(noOfEPIs), CommonUtils.formatIndianCurrency(emi), sanctionDateStr));

		Map<String, Integer> row6 = new LinkedHashMap<>();
		row6.put("6",10);
		row6.put(keysForContent.getString("InterestRate&Type"), 40);
		row6.put(interest + " % "+ keysForContent.getString("AdditionalInformation1"), 50);
		verticalList.add(createHorizontalList(row6));

		verticalList.add(
				createtwoHorizontalListkfs("7", keysForContent.getString("AdditionalInformation"), boldTextWithBorder)
						.setStyle(boldLeftStyle));
		verticalList.add(createFloatingROIKeyList(keysForContent, keysForContent.getString("ReferenceBenchmark"),
				keysForContent.getString("BenchmarkRate"), keysForContent.getString("Spread"),
				keysForContent.getString("FinalRate"), keysForContent.getString("RestPeriodicity"),
				keysForContent.getString("ChangeImpact")));

		verticalList.add(createFloatingROIValueList("NA", "NA", "NA", "NA", "NA", "NA", "NA", "NA", "NA", "NA"));
		verticalList.add(createtwoHorizontalListkfs("8", keysForContent.getString("FeeOrCharges"), boldTextWithBorder)
				.setStyle(boldLeftStyle));
		verticalList.add(createThreeHorizontalList("", keysForContent.getString("PayableToTheRE(A)"),
				keysForContent.getString("PayableToTheRE(B)"), 35, 35, 30));
		verticalList.add(createSixHorizontalList2("", "", keysForContent.getString("OneTimeOrRecurring1"),
				keysForContent.getString("Amount"), keysForContent.getString("OneTimeOrRecurring2"),
				keysForContent.getString("AmountInRs")));

		Map<String, Integer> processingFeesRow = new LinkedHashMap<>();
		processingFeesRow.put("(i)",10);
		processingFeesRow.put(keysForContent.getString("ProcessingFees"), 25);
		processingFeesRow.put(keysForContent.getString("payableReTypeA"), 15);
		processingFeesRow.put(CommonUtils.formatIndianCurrency(cibilPayloadCoApp.getProcessingFees()), 20);
		processingFeesRow.put("NA", 15);
		processingFeesRow.put("NA ", 15); //adding a space after NA to keep the Map unique
		verticalList.add(createHorizontalList(processingFeesRow));

		Map<String, Integer> insuranceChargeRow = new LinkedHashMap<>();
		insuranceChargeRow.put("(ii)", 10);
		insuranceChargeRow.put(keysForContent.getString("InsuranceCharges"), 25);
		insuranceChargeRow.put("NA", 15);
		insuranceChargeRow.put("NA ", 20);//adding a space after NA to keep the Map unique
		insuranceChargeRow.put( keysForContent.getString("payableReTypeB"), 15);
		insuranceChargeRow.put(keysForContent.getString("referBelowTable"), 15);
		verticalList.add(createHorizontalList(insuranceChargeRow));


		verticalList.add(
				createSixHorizontalList2("(iii)", keysForContent.getString("Valuation Fees"), "NA", "NA", "NA", "NA"));
		verticalList.add(
				createSixHorizontalList2("(iv)", keysForContent.getString("otherLegalCharges"), "NA", "NA", keysForContent.getString("payableReTypeB"), CommonUtils.formatIndianCurrency("0"))); //A // CommonUtils.formatIndianCurrency(cibilPayloadCoApp.getStampDutyCharge())

		return verticalList;
	}

	private ComponentBuilder<?, ?> getBasicAppnDetails2(JSONObject keysForContent, CibilDetailsPayload cibilPayloadCoApp) {
		VerticalListBuilder verticalList = cmp.verticalList();

		verticalList.add(createThreeHorizontalList2("9", keysForContent.getString("AnnualPercentageRate"), cibilPayloadCoApp.getEir()));
		verticalList.add(
				createtwoHorizontalListkfs("10", keysForContent.getString("ContingentCharges"), boldTextWithBorder));
		verticalList.add(createThreeHorizontalList2("(i)", keysForContent.getString("PenalCharges"), "NA"));
		verticalList.add(createThreeHorizontalList2("(ii)", keysForContent.getString("otherPenalCharges"), "NA"));
		verticalList.add(createThreeHorizontalList2("(iii)", keysForContent.getString("ForeclosureCharges"), "NA"));
		verticalList.add(createThreeHorizontalList2("(iv)", keysForContent.getString("SwitchingLoanCharges"), "NA"));
		verticalList.add(createThreeHorizontalList2("(v)", keysForContent.getString("AnyOtherCharges"), "NA"));

		return verticalList;
	}

	private ComponentBuilder<?, ?> getBasicAppnDetails3(JSONObject keysForContent) {

		VerticalListBuilder verticalList = cmp.verticalList();

		verticalList.add(createSingleHorizontalList(keysForContent.getString("OtherQualitativeInformation")));
		verticalList.add(createThreeHorizontalList2("1",
				keysForContent.getString("ClauseOfLoanAgreementRelatingToEngagementOfRecoveryAgent"), "NA"));
		verticalList.add(createThreeHorizontalList2("2",
				keysForContent.getString("ClauseOfLoanAgreementwhichDetailsGrievanceRedressalMechanism"),
				keysForContent.getString("RedressalMechanismValue")));
		verticalList.add(createThreeHorizontalList2("3", keysForContent.getString("phoneNum"), keysForContent.getString("caglPnoneAndEmail")));
		verticalList.add(createThreeHorizontalList2("4", keysForContent.getString("securitization(Yes/NO)"), keysForContent.getString("yesOrNo")));
		verticalList.add(createtwoHorizontalListkfs("5",
				keysForContent.getString("lendingArrangementDetailsMaybeFurnished"), borderedStyle));
		verticalList.add(createThreeHorizontalList(keysForContent.getString("fundingProportion"),
				keysForContent.getString("NameOfThePartner"), keysForContent.getString("BlendedRateOfInterest"), 33, 33,
				34));
		verticalList.add(createThreeHorizontalList("NA", "NA", "NA", 33, 33, 34));
		verticalList.add(cmp.pageBreak());
		verticalList.add(
				createtwoHorizontalListkfs("6", keysForContent.getString("digitalLoansDisclosures"), borderedStyle));
		verticalList
				.add(createThreeHorizontalList2("(i)", keysForContent.getString("CoolingOffOrlook-upPeriod"), "NA"));
		verticalList.add(createThreeHorizontalList2("(ii)", keysForContent.getString("DetailsOfLSP"), "NA"));

		return verticalList;
	}

	private ComponentBuilder<?, ?> getComputationDetails(JSONObject keysForContent, CustomerDataFields custmrDataFields, CibilDetailsPayload cibilPayloadCoApp, List<RepaymentSchedule> repaymentList, int noOfEPIs, String emi, String sanctionDateStr) {

		VerticalListBuilder verticalList = cmp.verticalList();

		String interest = (cibilPayloadCoApp.getRoi() == null) ? "" : cibilPayloadCoApp.getRoi().toString();
		logger.debug("Interest rate :"+ interest); //t24

		BigDecimal sactionAmtDb = ((null==custmrDataFields.getLoanDetails().getSanctionedLoanAmount())? BigDecimal.ZERO:custmrDataFields.getLoanDetails().getSanctionedLoanAmount());
		//BigDecimal sactionAmtDb = custmrDataFields.getLoanDetails().getSanctionedLoanAmount();
		String sactionAmt = sactionAmtDb.toPlainString(); //1

		int totalInsurance = toFindSum(cibilPayloadCoApp.getInsuranceChargeJoint(),
				cibilPayloadCoApp.getInsuranceChargeMember(), cibilPayloadCoApp.getInsuranceChargeSpouse());

		int chargesPayableFee = toFindSum(String.valueOf(totalInsurance), cibilPayloadCoApp.getProcessingFees());
		BigDecimal chargesPayableFeeBD = BigDecimal.valueOf(chargesPayableFee);

		BigDecimal totalInterest = BigDecimal.ZERO;
		for (RepaymentSchedule schedule : repaymentList) {
		    if (schedule.getInterest() != null && !schedule.getInterest().trim().isEmpty()) {
		        try {
		            BigDecimal interest1 = new BigDecimal(schedule.getInterest().trim());
		            totalInterest = totalInterest.add(interest1);
		        } catch (NumberFormatException e) {
		            logger.error("Invalid interest value: " + schedule.getInterest() + ", Error: " + e);
		        }
		    }
		}
		logger.info("totalInterest : " + totalInterest);
		
		BigDecimal netDisbursementAmount = sactionAmtDb.subtract(chargesPayableFeeBD);
		logger.debug("netDisbursementAmount :" + netDisbursementAmount);
		String netDisbursementAmountStr = netDisbursementAmount.setScale(2, RoundingMode.HALF_UP).toPlainString();
		logger.info("Net Disbursement Amount: " + netDisbursementAmountStr);
		
		BigDecimal amountToBePaidDB = sactionAmtDb.add(totalInterest);
		String amountToBePaidStr = amountToBePaidDB.setScale(2, RoundingMode.HALF_UP).toPlainString();
		logger.info("amountToBePaidStr: " + amountToBePaidStr);
		
		verticalList.add(createThreeHorizontalList2(keysForContent.getString("Slno"),
				keysForContent.getString("Parameter"), keysForContent.getString("Details")));
		verticalList.add(createThreeHorizontalList2("1", keysForContent.getString("SanctionedLoanAmount"), CommonUtils.formatIndianCurrency(sactionAmt)));
		verticalList.add(createThreeHorizontalList2("2", keysForContent.getString("LoanTerms"), cibilPayloadCoApp.getFinalTenure() + "  "+  keysForContent.getString("months"))); //in months
		verticalList.add(createThreeHorizontalList2("a)", keysForContent.getString("NoOfInstalments"), "NA"));
		verticalList.add(createFiveHorizontalList("b)", keysForContent.getString("instalmentType"),
				keysForContent.getString(Constants.EPI_NOS), keysForContent.getString("EPI"),
				keysForContent.getString("Commencementofrepaymentpostsanction")).setStyle(boldText));
		verticalList.add(createFiveHorizontalList("", cibilPayloadCoApp.getRepaymentFrequency(), String.valueOf(noOfEPIs), CommonUtils.formatIndianCurrency(emi), sanctionDateStr));//A //Date format - 17-02-2025
		verticalList.add(createThreeHorizontalList2("c)", keysForContent.getString("Noofinstalmentsforpayment"), "NA"));
		verticalList.add(
				createThreeHorizontalList2("d)", keysForContent.getString("Commencementofrepaymentpostsanction"), sanctionDateStr) //repStartDate -t24 //A
						.setStyle(stl.style().setForegroundColor(Color.YELLOW)));

		Map<String, Integer> interestRateTypeRow = new LinkedHashMap<>();
		interestRateTypeRow.put("3",10);
		interestRateTypeRow.put(keysForContent.getString("Interestratetype"), 40);
		interestRateTypeRow.put(keysForContent.getString("AdditionalInformation1").replace("(", "").replace(")",""), 50);
		verticalList.add(createHorizontalList(interestRateTypeRow));
		verticalList.add(createThreeHorizontalList2("4", keysForContent.getString("Rateofinterest"), interest + " %"));
		verticalList
				.add(createThreeHorizontalList2("5", keysForContent.getString("Totalinterestamounttobecharged"),  CommonUtils.formatIndianCurrency(String.valueOf(totalInterest))));
		verticalList.add(createThreeHorizontalList2("6", keysForContent.getString("Fee/ChargesPayable"), CommonUtils.formatIndianCurrency(String.valueOf(chargesPayableFee)))); //processing + total insurance
		verticalList.add(createThreeHorizontalList2("A", keysForContent.getString("PayabletotheRE"), CommonUtils.formatIndianCurrency(cibilPayloadCoApp.getProcessingFees())));
		verticalList.add(createThreeHorizontalList2("B", keysForContent.getString("Payabletothirdparty"), CommonUtils.formatIndianCurrency(String.valueOf(totalInsurance)))); //total insurance
		verticalList.add(createThreeHorizontalList2("7", keysForContent.getString("NetDisbursementAmount"), CommonUtils.formatIndianCurrency(String.valueOf(netDisbursementAmountStr)))); //1-6
		verticalList.add(createThreeHorizontalList2("8", keysForContent.getString("amountToBePaid"),  CommonUtils.formatIndianCurrency(String.valueOf(amountToBePaidStr)))); //1+5
		verticalList
				.add(createThreeHorizontalList2("9", keysForContent.getString("AnnualPercentagerateInPercent"), cibilPayloadCoApp.getEir()));

		verticalList.add(createThreeHorizontalList2("10", keysForContent.getString("ScheduleofDisbursement"),  keysForContent.getString("scheduleCondition"))); //scheduleCondition
		verticalList.add(cmp.verticalGap(40));
		verticalList.add(createThreeHorizontalList2("11",
				keysForContent.getString("Duedatepaymentofinstalmentandinterest"), keysForContent.getString("scheduleCondition"))); //scheduleCondition

		return verticalList;
	}

	private ComponentBuilder<?, ?> createThreeHorizontalList2(String value, String value1, String value2) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(10));
		horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(40));
		horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(50));

		VerticalListBuilder wrapper = cmp.verticalList(cmp.multiPageList(horizontalList));
    return wrapper;
	}

	private ComponentBuilder<?, ?> createThreeHorizontalList(String key, String value1, String value2, int width1,
															 int width2, int width3) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(width1));
		horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(width2));
		horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(width3));

		VerticalListBuilder wrapper = cmp.verticalList(cmp.multiPageList(horizontalList));
    return wrapper;
	}

	private ComponentBuilder<?, ?> createHorizontalList(Map<String, Integer> columns) {
		HorizontalListBuilder horizontalList = cmp.horizontalList();
		if (columns == null || columns.isEmpty()) {
			VerticalListBuilder wrapper = cmp.verticalList(cmp.multiPageList(horizontalList));
    return wrapper;
		}
		for (Map.Entry<String, Integer> entry : columns.entrySet()) {
			horizontalList.add(
					cmp.text(entry.getKey() == null ? "" : entry.getKey())
							.setMarkup(Markup.HTML)
							.setStyle(borderedStyle)
							.setWidth(entry.getValue())
			);
		}
		VerticalListBuilder wrapper = cmp.verticalList(cmp.multiPageList(horizontalList));
    return wrapper;
	}

	private ComponentBuilder<?, ?> createSingleHorizontalList(String Key) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();
		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(boldCenteredStyle).setWidth(100));

		VerticalListBuilder wrapper = cmp.verticalList(cmp.multiPageList(horizontalList));
    return wrapper;

	}

	private ComponentBuilder<?, ?> createtwoHorizontalListkfs(String Key, String value, ReportStyleBuilder style) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(style).setWidth(10));
		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(style).setWidth(90));

		VerticalListBuilder wrapper = cmp.verticalList(cmp.multiPageList(horizontalList));
    return wrapper;

	}

	private ComponentBuilder<?, ?> createFourHorizontalList(String Key1, String value1, String Key2, String value2) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle));
		horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle));
		horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle));
		horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle));

		VerticalListBuilder wrapper = cmp.verticalList(cmp.multiPageList(horizontalList));
    return wrapper;

	}
	
	private ComponentBuilder<?, ?> createFiveHorizontalList(String v1, String v2, String v3, String v4, String v5) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(v1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(10));
		horizontalList.add(cmp.text(v2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(22));
		horizontalList.add(cmp.text(v3).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(23));
		horizontalList.add(cmp.text(v4).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(22));
		horizontalList.add(cmp.text(v5).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(23));

		VerticalListBuilder wrapper = cmp.verticalList(cmp.multiPageList(horizontalList));
    return wrapper;

	}

	private ComponentBuilder<?, ?> createFloatingROIKeyList(JSONObject keysForContent, String Key1, String value1,
															String Key2, String value2, String Key3, String value3) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		VerticalListBuilder verticalList = cmp.verticalList();
		VerticalListBuilder verticalList1 = cmp.verticalList();
		horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle));
		horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle));
		horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle));
		horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle));

		verticalList.add(cmp.text(Key3).setMarkup(Markup.HTML).setStyle(borderedStyle).setFixedHeight(65));
		verticalList.add(cmp.horizontalList().add(cmp.text(keysForContent.getString("B")).setMarkup(Markup.HTML).setStyle(borderedStyle))
				.add(cmp.text(keysForContent.getString("S")).setMarkup(Markup.HTML).setStyle(borderedStyle)));

		horizontalList.add(verticalList);

		verticalList1.add(cmp.text(value3).setMarkup(Markup.HTML).setStyle(borderedStyle).setFixedHeight(65));
		verticalList1.add(cmp.horizontalList().add(cmp.text(keysForContent.getString("EPI")).setMarkup(Markup.HTML).setStyle(borderedStyle))
				.add(cmp.text(keysForContent.getString(Constants.EPI_NOS)).setMarkup(Markup.HTML).setStyle(borderedStyle)));
		horizontalList.add(verticalList1);

		VerticalListBuilder wrapper = cmp.verticalList(cmp.multiPageList(horizontalList));
    return wrapper;
	}


	private ComponentBuilder<?, ?> createFloatingROIValueList(String Key1, String value1, String Key2, String value2,
															  String Key3, String value3, String Key4, String value4, String Key5, String value5) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		VerticalListBuilder verticalList = cmp.verticalList();
		VerticalListBuilder verticalList1 = cmp.verticalList();
		horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle));
		horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle));
		horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle));
		horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle));

		horizontalList.add(verticalList);
		horizontalList.add(cmp.text(Key3).setMarkup(Markup.HTML).setStyle(borderedStyle));
		horizontalList.add(cmp.text(value3).setMarkup(Markup.HTML).setStyle(borderedStyle));
		horizontalList.add(cmp.text(Key4).setMarkup(Markup.HTML).setStyle(borderedStyle));
		horizontalList.add(cmp.text(value4).setMarkup(Markup.HTML).setStyle(borderedStyle));

		horizontalList.add(verticalList1);

		VerticalListBuilder wrapper = cmp.verticalList(cmp.multiPageList(horizontalList));
    return wrapper;
	}

	private ComponentBuilder<?, ?> createSixHorizontalList2(String Key1, String value1, String Key2, String value2,
															String Key3, String value3) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(10));
		horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(25));
		horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(15));
		horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(20));
		horizontalList.add(cmp.text(Key3).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(15));
		horizontalList.add(cmp.text(value3).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(15));

		VerticalListBuilder wrapper = cmp.verticalList(cmp.multiPageList(horizontalList));
    return wrapper;
	}

	public static String getTodayData() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String strDate = formatter.format(date);
		return strDate;
	}

	public static String getYearandMonth() {
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		return dayOfMonth + "," + month + "," + year;

	}

	public static String dateFormatyyyymmddtoddmmyyyy(String DOB) {
		String ds1 = DOB;
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
		String ds2 = null;
		try {
			ds2 = sdf2.format(sdf1.parse(ds1));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
		System.out.println(ds2);
		return ds2;
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
	
	public Response getSuccessJson1(String jsonString) {
        logger.debug("Inside getSuccessJson");
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();

        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        logger.debug("responseCode added to responseHeader");

        // Directly set the JSON (no wrapping)
        responseBody.setResponseObj(jsonString);

        logger.debug("string added to responseBody as responseObj");
        response.setResponseHeader(responseHeader);
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

	private int toFindSum(String... value) {
		int totalInsurance = Stream.of(
				value
		).map(val -> {
			try {
				return Integer.parseInt(Optional.ofNullable(val).orElse("0"));
			} catch (NumberFormatException e) {
				return 0;
			}
		}).reduce(0, Integer::sum);
		return totalInsurance;
	}

	private ComponentBuilder<?, ?> getRepaymentDetailsAsTwoTables(JSONObject keysForContent, List<RepaymentSchedule> repaymentList) {
		logger.debug("inside :: getRepaymentDetailsAsTwoTables");

		int mid = (int) Math.ceil(repaymentList.size() / 2.0);
		List<RepaymentSchedule> firstHalf = repaymentList.subList(0, mid);
		List<RepaymentSchedule> secondHalf = repaymentList.subList(mid, repaymentList.size());

		ComponentBuilder<?, ?> firstTable = getRepaymentDetailsAsTable(keysForContent, firstHalf);
		ComponentBuilder<?, ?> secondTable = getRepaymentDetailsAsTable(keysForContent, secondHalf);

		return cmp.horizontalList()
				.add(firstTable, cmp.horizontalGap(10), secondTable);
	}
	
	private ComponentBuilder<?, ?> getRepaymentDetailsAsTable(JSONObject keysForContent, List<RepaymentSchedule> repaymentList) {
		logger.debug("inside :: getRepaymentDetailsAsTable");

		DRDataSource dataSource = new DRDataSource(Constants.INSTALMENT_NO, Constants.OUTSTANDING_PRINCIPAL, Constants.PRINCIPAL, Constants.INTEREST, Constants.INSTALMENT);
		for (RepaymentSchedule schedule : repaymentList) {
			dataSource.add(
					schedule.getSlNo(),
					formatIndianCurrency(schedule.getOutstanding()),
					formatIndianCurrency(schedule.getPrincipal()),
					formatIndianCurrency(schedule.getInterest()),
					formatIndianCurrency(schedule.getTotalDue())
			);
		}
		
	    StyleBuilder borderStyle = stl.style(stl.penThin()).setPadding(5);

	    
//	    StyleBuilder cellStyle = stl.style()
//	        .setPadding(3)
//	        .setBorder(stl.pen1Point());

	    StyleBuilder headerStyle = stl.style(borderStyle)
	        .bold()
	        .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
	        .setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);

	    StyleBuilder centerStyle = stl.style(borderStyle)
		        .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		        
	    
	    // === Grouped Header Row with equal height ===
	    ComponentBuilder<?, ?> groupedHeaderRow = cmp.horizontalList(
		        cmp.text(keysForContent.getString(Constants.INSTALMENT_NO)).setMarkup(Markup.HTML).setFixedWidth(32).setStyle(headerStyle),
		        cmp.text(keysForContent.getString(Constants.OUTSTANDING_PRINCIPAL)).setMarkup(Markup.HTML).setFixedWidth(68).setStyle(headerStyle),
		        cmp.text(keysForContent.getString(Constants.PRINCIPAL)).setMarkup(Markup.HTML).setFixedWidth(60).setStyle(headerStyle),
		        cmp.text(keysForContent.getString(Constants.INTEREST)).setMarkup(Markup.HTML).setFixedWidth(50).setStyle(headerStyle),
		        cmp.text(keysForContent.getString(Constants.INSTALMENT)).setMarkup(Markup.HTML).setStyle(headerStyle)
		    ).setGap(0);	
	
	JasperReportBuilder subReport = report()
		    .columnHeader(groupedHeaderRow)
		    .columns(
		        col.column("", Constants.INSTALMENT_NO, type.stringType()).setFixedWidth(32).setStyle(centerStyle),
		        col.column("", Constants.OUTSTANDING_PRINCIPAL, type.stringType()).setFixedWidth(68).setStyle(centerStyle),
		        col.column("", Constants.PRINCIPAL, type.stringType()).setFixedWidth(60).setStyle(centerStyle),
		        col.column("", Constants.INTEREST, type.stringType()).setFixedWidth(50).setStyle(centerStyle),
		        col.column("", Constants.INSTALMENT, type.stringType()).setStyle(centerStyle)
		    )
		    .setColumnStyle(borderStyle)  // this is useful
		    .setDataSource(dataSource);
    return cmp.subreport(subReport);
}
	
    public static String formatIndianCurrency(String amountStr) {
   	 if (amountStr == null || amountStr.trim().isEmpty()) {
   	        return "0.00/-";
   	    }

   	    try {
   	        double amount = Double.parseDouble(amountStr.trim());
   	        boolean isNegative = amount < 0;
   	        amount = Math.abs(amount); // Work with the positive value

   	        String[] parts = String.format(Locale.ENGLISH, "%.2f", amount).split("\\.");
   	        String intPart = parts[0];
   	        String decPart = parts[1];

   	        StringBuilder result = new StringBuilder();
   	        int len = intPart.length();

   	        if (len > 3) {
   	            result.insert(0, "," + intPart.substring(len - 3));
   	            intPart = intPart.substring(0, len - 3);

   	            while (intPart.length() > 2) {
   	                result.insert(0, "," + intPart.substring(intPart.length() - 2));
   	                intPart = intPart.substring(0, intPart.length() - 2);
   	            }
   	        }
   	        result.insert(0, intPart);

   	        String formatted = result + "." + decPart;
   	        return (isNegative ? "-" : "") + formatted;
   	    } catch (NumberFormatException e) {
   	        return "";
   	    }
   }
    
    private String formatRepaymentStartDate(String startDate) {
        String repymtStartDate = null;
        try {
            repymtStartDate = CommonUtils.dateFormat2(startDate);
            logger.debug("repymtStartDate from date format : " + repymtStartDate);
        } catch (Exception e) {
            logger.debug("error while date format " + e);
        }
        return repymtStartDate;
   }
}
