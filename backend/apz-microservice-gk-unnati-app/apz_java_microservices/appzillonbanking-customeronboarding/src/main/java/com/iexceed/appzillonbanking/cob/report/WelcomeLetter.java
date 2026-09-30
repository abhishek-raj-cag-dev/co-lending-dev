package com.iexceed.appzillonbanking.cob.report;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.payload.*;
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
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.*;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

public class WelcomeLetter {
	private static final Logger logger = LogManager.getLogger(WelcomeLetter.class);

	private final StyleBuilder borderedStyle;
    private final StyleBuilder boldCenteredStyle;
    private final StyleBuilder boldLeftStyle;
    private final StyleBuilder rightStyle;
    private final StyleBuilder leftStyle;

	private String applicantCustId = "";
	private String coApplicantCustId = "";
	private String applicantName = "";
	private String coApplicantName = "";

	String appltGender = "";
	String coAppltGender = "";

	public WelcomeLetter() {
		borderedStyle = stl.style(stl.penThin()).setPadding(5);
		boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

		rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
		leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
	}

	public Response generatePdf(JSONObject keysForContent, CustomerDataFields customerDataFields, String sactionedDateStr, List<RepaymentScheduleDisbursed> repaymentList, String disburseDateStr, String lonAmountDisbursed)
			throws DRException, IOException {
		logger.debug("inside generatePdf welcome letter: ");
		Response response;
		try {
			String filePath;
			Gson gsonObj = new Gson();

		JasperReportBuilder report = new JasperReportBuilder();
		JasperReportBuilder subReport = new JasperReportBuilder();
		JasperReportBuilder subReport1 = new JasperReportBuilder();
		JasperReportBuilder subReport2 = new JasperReportBuilder();
		JasperReportBuilder subReport3 = new JasperReportBuilder();
		JasperReportBuilder subReport4 = new JasperReportBuilder();
		JasperReportBuilder subReport5 = new JasperReportBuilder();
		JasperReportBuilder subReport6 = new JasperReportBuilder();
		JasperReportBuilder subReport7 = new JasperReportBuilder();

		report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));


			CibilDetailsPayload cibilPayload = CommonUtils
					.resolveCibilPayload(customerDataFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId, gsonObj);
			logger.debug("CreditDetailsPayload Payload : " + cibilPayload);
		
		String loanAcountNumber = customerDataFields.getLoanDetails().getT24LoanId() == null ? ""
				: customerDataFields.getLoanDetails().getT24LoanId();

			String customerId = customerDataFields.getApplicationMaster().getProductCode()
					.equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE) ?
					customerDataFields.getApplicationMaster().getApplicantT24Id() :
					customerDataFields.getApplicationMaster().getSearchCode2();

		/* Basic Application Details */
		String serverImagePath = CommonUtils.getExternalProperties("images") + "logo-name.png";
		logger.debug("serverImagePath :" + serverImagePath);

		subReport.title(cmp.image(serverImagePath).setHorizontalImageAlignment(HorizontalImageAlignment.CENTER));

		subReport.title(cmp.text(keysForContent.getString("applicationName")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14).underline()));	
//		subReport1
//				.title(cmp.text(keysForContent.getString("applicationName")).setStyle(headerStyle.setFontSize(16)));

		subReport1.title(cmp.horizontalList(
				cmp.text(keysForContent.getString("customerId") + ":" + customerId).setMarkup(Markup.HTML).setStyle(leftStyle),
				cmp.text(keysForContent.getString("date") + ":" + disburseDateStr).setMarkup(Markup.HTML).setStyle(rightStyle)));

		subReport2.title(cmp.horizontalList(
				cmp.text(keysForContent.getString("loanId") + ":" + loanAcountNumber).setMarkup(Markup.HTML).setStyle(leftStyle),

				cmp.text(keysForContent.getString("disbursedAmount") + ":" + "Rs."+CommonUtils.amountFormat(lonAmountDisbursed)+"/-").setMarkup(Markup.HTML)
						.setStyle(rightStyle)));

		subReport3.title(cmp.text(
				keysForContent.getString("branch") + ":" + customerDataFields.getApplicationMaster().getBranchName()
						+ " & " + customerDataFields.getApplicationMaster().getBranchId()).setMarkup(Markup.HTML)
				.setStyle(leftStyle));

		subReport4.title(getBorrowerDetailsForWelcomeLetter(keysForContent, customerDataFields)).title(cmp.text(""));
		subReport5.title(cmp.text(keysForContent.getString("greetingLine") +" "+ applicantName +" ,").setMarkup(Markup.HTML).setStyle(leftStyle));	
		subReport5.title(cmp.text(""));
		
	    String congratulationsParagraph = "";
        if (Constants.UNNATI_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())) {
        	congratulationsParagraph = keysForContent.getString("congratulationsParagraph");
        	logger.debug("UNNATI_PRODUCT_CODE :" + customerDataFields.getApplicationMaster().getProductCode());
        }else if(Constants.RENEWAL_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())) {
        	congratulationsParagraph = keysForContent.getString("congratulationsParagraphRenewal");
        	logger.debug("RENEWAL_PRODUCT_CODE :" + customerDataFields.getApplicationMaster().getProductCode());
        }else if(Constants.OPENMARKET_LOAN_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())) {
			congratulationsParagraph = keysForContent.getString("congratulationsParagraphOpenMarket");
			logger.debug("OPEN_MARKET_CODE :" + customerDataFields.getApplicationMaster().getProductCode());
        }
        logger.debug("productCode :" + customerDataFields.getApplicationMaster().getProductCode());
        
		subReport5.title(cmp.text(congratulationsParagraph).setMarkup(Markup.HTML).setStyle(leftStyle));
		
		subReport5.title(cmp.text(keysForContent.getString("missionParagraph")).setMarkup(Markup.HTML).setStyle(leftStyle));
//		subReport5.title(cmp.text(keysForContent.getString("repaymentScheduleNotice")).setMarkup(Markup.HTML).setStyle(leftStyle));
			if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())){
				subReport5.title(cmp.text(keysForContent.getString("repaymentScheduleNoticeSup")).setMarkup(Markup.HTML).setStyle(leftStyle));
			}else if(Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())){
				subReport5.title(cmp.text(keysForContent.getString("repaymentScheduleNoticeFWl")).setMarkup(Markup.HTML).setStyle(leftStyle));
			}
			else if(Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())){
				subReport5.title(cmp.text(keysForContent.getString("repaymentScheduleNoticeEMER")).setMarkup(Markup.HTML).setStyle(leftStyle));
			}
			else if(Constants.UNNATI_RESTART_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())){
				subReport5.title(cmp.text(keysForContent.getString("repaymentScheduleNoticeRESTR")).setMarkup(Markup.HTML).setStyle(leftStyle));
			}
			else {
		subReport5.title(cmp.text(keysForContent.getString("repaymentScheduleNotice")).setMarkup(Markup.HTML).setStyle(leftStyle));
			}
		subReport5.title(cmp.text(""));
		
		
		subReport6.title(cmp.text(keysForContent.getString("loanRepaymentDetailsHeading")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
		.title(getLoanAndRepaymrntDetails(keysForContent, repaymentList, sactionedDateStr, cibilPayload, disburseDateStr,lonAmountDisbursed)).title(cmp.text(""));
		
		subReport7.title(cmp.text(keysForContent.getString("termsParagraph")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport7.title(cmp.text(keysForContent.getString("closingParagraph")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport7.title(cmp.text(keysForContent.getString("feedbackCallNotice")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport7.title(cmp.text(""));
		subReport7.title(cmp.text(keysForContent.getString("signoffLine")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport7.title(cmp.text(keysForContent.getString("organizationName")).setMarkup(Markup.HTML).setStyle(leftStyle));
		
		report.addSummary(cmp.subreport(subReport)).addSummary(cmp.subreport(subReport1))
				.addSummary(cmp.subreport(subReport2)).addSummary(cmp.subreport(subReport3))
				.addSummary(cmp.subreport(subReport4)).addSummary(cmp.subreport(subReport5))
				.addSummary(cmp.subreport(subReport6)).addSummary(cmp.subreport(subReport7));
		
		logger.debug("added all subreports to report builder");

			try {
				Properties prop = CommonUtils.readPropertyFile();
				// Construct file path			
				String filePathDest = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + "APZCBO" + "/"
						+ Constants.LOAN + "/" + customerDataFields.getApplicationId() + "/" + Constants.WELCOMEKIT + "/";
				
				logger.debug("filePathDest :: {}", filePathDest);

				// Ensure directory exists
				File directory = new File(filePathDest);
				if (!directory.exists()) {
					boolean isCreated = directory.mkdirs();
					if (!isCreated) {
						throw new IOException("Failed to create directory: " + filePathDest);
					}
				}

				filePath = filePathDest + customerDataFields.getApplicationId() + "_WelcomeLetter" + ".pdf";

				// Save report to file
				try (FileOutputStream fos = new FileOutputStream(filePath)) {
					report.toPdf(fos);
				}

				// Read file and encode to Base64
				byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
				String base64String = java.util.Base64.getEncoder().encodeToString(inputfile);

				// Delete the file
				// Files.deleteIfExists(Paths.get(filePath));

				response = getSuccessJson(base64String);

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
	
	
	public String generateWelcomeLetterPdfForDBKit(CustomerDataFields customerDataFields, JSONObject keysForContent, String filePath, String sactionedDateStr, List<RepaymentScheduleDisbursed> repaymentList, String disburseDateStr, String lonAmountDisbursed, String language)
			throws DRException, IOException, JRException {
		try {
			
			logger.debug("Inside loan generateWelcomeLetterPdfForDBKit generation: ");

			CustomerDetailsPayload payload1;
			CustomerDetailsPayload payload2;
			Gson gsonObj = new Gson();
			for (CustomerDetails custDtl : customerDataFields.getCustomerDetailsList()) {
				logger.debug("customer Type : " + custDtl.getCustomerType());
				if (custDtl.getCustomerType().equalsIgnoreCase("Applicant")) {
					applicantCustId = String.valueOf(custDtl.getCustDtlId());
					logger.debug("applicantCustId : " + applicantCustId);
					applicantName = custDtl.getCustomerName();

					payload1 = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
					logger.debug("custApplicantPayload :" + payload1);
					appltGender = payload1.getGender();
				} else if (custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
					coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
					coApplicantName = custDtl.getCustomerName();
					logger.debug("coApplicantCustId : " + coApplicantCustId);
					payload2 = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
					logger.debug("custCo-ApplicantPayload :" + payload2);
					coAppltGender = payload2.getGender();
				}
			}

			int noOfEmis = repaymentList.size();
			logger.debug("Number of Records: " + noOfEmis);
			
//			String repymtStartDate = "";
//			String repymtEndDate = "";
//			String firstEmi = "";
//			String emi = "";
//			if( repaymentList.size() > 0) {
//				firstEmi = repaymentList.get(0).getTotal();
//				emi = repaymentList.get(2).getTotal(); 
//				String startDate = repaymentList.get(0).getDate();	
//				String endDate = repaymentList.get(repaymentList.size() - 1).getDate();	
//				try {
//					repymtStartDate = CommonUtils.dateFormat1(startDate);
//					repymtEndDate = CommonUtils.dateFormat1(endDate);
//				}catch (Exception e) {
//					logger.debug("error while date format " + e);
//				}
//			}
		
			JasperReportBuilder report = new JasperReportBuilder();
			JasperReportBuilder subReport = new JasperReportBuilder();
			JasperReportBuilder subReport1 = new JasperReportBuilder();
			JasperReportBuilder subReport2 = new JasperReportBuilder();
			JasperReportBuilder subReport3 = new JasperReportBuilder();
			JasperReportBuilder subReport4 = new JasperReportBuilder();
			JasperReportBuilder subReport5 = new JasperReportBuilder();
			JasperReportBuilder subReport6 = new JasperReportBuilder();
			JasperReportBuilder subReport7 = new JasperReportBuilder();

			report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));

			CibilDetailsPayload cibilPayload = CommonUtils
					.resolveCibilPayload(customerDataFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId, gsonObj);
			logger.debug("CreditDetailsPayload Payload : " + cibilPayload);

			String loanAcountNumber = customerDataFields.getLoanDetails().getT24LoanId() == null ? ""
					: customerDataFields.getLoanDetails().getT24LoanId();

			String customerId = customerDataFields.getApplicationMaster().getProductCode()
					.equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE) ?
					customerDataFields.getApplicationMaster().getApplicantT24Id() :
					customerDataFields.getApplicationMaster().getSearchCode2();

			/* Basic Application Details */
			String serverImagePath = CommonUtils.getExternalProperties("images") + "logo-name.png";
			logger.debug("serverImagePath :" + serverImagePath);

			subReport.title(cmp.image(serverImagePath).setHorizontalImageAlignment(HorizontalImageAlignment.CENTER));

			subReport.title(cmp.text(keysForContent.getString("applicationName")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14).underline()));	

			subReport1.title(cmp.horizontalList(
					cmp.text(keysForContent.getString("customerId") + ":" + customerId).setMarkup(Markup.HTML).setStyle(leftStyle),
					cmp.text(keysForContent.getString("date") + ":" + disburseDateStr).setMarkup(Markup.HTML).setStyle(rightStyle)));

			subReport2.title(cmp.horizontalList(
					cmp.text(keysForContent.getString("loanId") + ":" + loanAcountNumber).setMarkup(Markup.HTML).setStyle(leftStyle),

					cmp.text(keysForContent.getString("disbursedAmount") + ":" +"Rs."+ CommonUtils.amountFormat(lonAmountDisbursed)+"/-").setMarkup(Markup.HTML)
							.setStyle(rightStyle)));

			subReport3.title(cmp.text(
					keysForContent.getString("branch") + ":" + customerDataFields.getApplicationMaster().getBranchName()
							+ " & " + customerDataFields.getApplicationMaster().getBranchId()).setMarkup(Markup.HTML)
					.setStyle(leftStyle));
			
			

			subReport4.title(getBorrowerDetailsForWelcomeLetter(keysForContent, customerDataFields)).title(cmp.text(""));
			subReport5.title(cmp.text(keysForContent.getString("greetingLine") +" "+ applicantName +" ,").setMarkup(Markup.HTML).setStyle(leftStyle));	
			subReport5.title(cmp.text(""));

			String congratulationsParagraph = "";
			if (Constants.UNNATI_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())) {
				congratulationsParagraph = keysForContent.getString("congratulationsParagraph");
				logger.debug("UNNATI_PRODUCT_CODE :" + customerDataFields.getApplicationMaster().getProductCode());
			} else if (Constants.RENEWAL_PRODUCT_CODE
					.equals(customerDataFields.getApplicationMaster().getProductCode())) {
				congratulationsParagraph = keysForContent.getString("congratulationsParagraphRenewal");
				logger.debug("RENEWAL_PRODUCT_CODE :" + customerDataFields.getApplicationMaster().getProductCode());
			}else if(Constants.OPENMARKET_LOAN_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())) {
				congratulationsParagraph = keysForContent.getString("congratulationsParagraphOpenMarket");
				logger.debug("OPEN_MARKET_CODE :" + customerDataFields.getApplicationMaster().getProductCode());
			}
//
			else if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())) {
				congratulationsParagraph = keysForContent.getString("congratulationsParagraphUnnatiSupplementary");
				logger.debug("UNNATI_SUPPLEMENTRY_PRODUCT_CODE :" + customerDataFields.getApplicationMaster().getProductCode());
			}
			else if(Constants.UNNATI_RESTART_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())) {
				congratulationsParagraph = keysForContent.getString("congratulationsParagraphUnnatiRestart");
				logger.debug("UNNATI_RESTART_PRODUCT_CODE :" + customerDataFields.getApplicationMaster().getProductCode());
			}
			else if(Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())) {
				congratulationsParagraph = keysForContent.getString("congratulationsParagraphUnnatiFamilyWelFare");
				logger.debug("FAMILY_WELFARE_PRODUCT_CODE :" + customerDataFields.getApplicationMaster().getProductCode());
			}
			else if(Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())) {
				congratulationsParagraph = keysForContent.getString("congratulationsParagraphUnnatiEmergency");
				logger.debug("UNNATI_EMERGENCY_PRODUCT_CODE :" + customerDataFields.getApplicationMaster().getProductCode());
			}
			else if(Constants.UNNATI_LITE_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())) {
				congratulationsParagraph = keysForContent.getString("congratulationsParagraphUnnatiLite");
				logger.debug("UNNATI_LITE_PRODUCT_CODE :" + customerDataFields.getApplicationMaster().getProductCode());
			}

			logger.debug("productCode :" + customerDataFields.getApplicationMaster().getProductCode());

			subReport5.title(cmp.text(congratulationsParagraph).setMarkup(Markup.HTML).setStyle(leftStyle));
			subReport5.title(cmp.text(keysForContent.getString("missionParagraph")).setMarkup(Markup.HTML).setStyle(leftStyle));
			if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())){
				subReport5.title(cmp.text(keysForContent.getString("repaymentScheduleNoticeSup")).setMarkup(Markup.HTML).setStyle(leftStyle));
			}else if(Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())){
				subReport5.title(cmp.text(keysForContent.getString("repaymentScheduleNoticeFWl")).setMarkup(Markup.HTML).setStyle(leftStyle));
			}
			else if(Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())){
				subReport5.title(cmp.text(keysForContent.getString("repaymentScheduleNoticeEMER")).setMarkup(Markup.HTML).setStyle(leftStyle));
			}
			else if(Constants.UNNATI_RESTART_PRODUCT_CODE.equals(customerDataFields.getApplicationMaster().getProductCode())){
				subReport5.title(cmp.text(keysForContent.getString("repaymentScheduleNoticeRESTR")).setMarkup(Markup.HTML).setStyle(leftStyle));
			}
			else {
			subReport5.title(cmp.text(keysForContent.getString("repaymentScheduleNotice")).setMarkup(Markup.HTML).setStyle(leftStyle));
			}
			
			subReport5.title(cmp.text(""));
			
			
			subReport6.title(cmp.text(keysForContent.getString("loanRepaymentDetailsHeading")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(16)))
			.title(getLoanAndRepaymrntDetails(keysForContent, repaymentList, sactionedDateStr, cibilPayload, disburseDateStr,lonAmountDisbursed)).title(cmp.text(""));
			
			subReport7.title(cmp.text(keysForContent.getString("termsParagraph")).setMarkup(Markup.HTML).setStyle(leftStyle));
			subReport7.title(cmp.text(keysForContent.getString("closingParagraph")).setMarkup(Markup.HTML).setStyle(leftStyle));
			subReport7.title(cmp.text(keysForContent.getString("feedbackCallNotice")).setMarkup(Markup.HTML).setStyle(leftStyle));
			subReport7.title(cmp.text(""));
			subReport7.title(cmp.text(keysForContent.getString("signoffLine")).setMarkup(Markup.HTML).setStyle(leftStyle));
			subReport7.title(cmp.text(keysForContent.getString("organizationName")).setMarkup(Markup.HTML).setStyle(leftStyle));
			
			report.addSummary(cmp.subreport(subReport)).addSummary(cmp.subreport(subReport1))
					.addSummary(cmp.subreport(subReport2)).addSummary(cmp.subreport(subReport3))
					.addSummary(cmp.subreport(subReport4)).addSummary(cmp.subreport(subReport5))
					.addSummary(cmp.subreport(subReport6)).addSummary(cmp.subreport(subReport7));
			
			logger.debug("added all subreports to report builder");

			// saving report to the directory and creating base64 string for response
			try {
//				// Save report to file
//				try (FileOutputStream fos = new FileOutputStream(filePath)) {
//					report.toPdf(fos);
//				}
//				// Read file and encode to Base64
//				byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
//				logger.info("Loan applicationPDF Report Generated");
//				return inputfile;
				
				 String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
				    logger.debug("inputLanguage " + language);
				    
				    boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
				            .anyMatch(lang -> lang.equalsIgnoreCase(language));

				    if (isValidLanguage) {
//				        // Generate HTML in-memory, no file creation
//				        ByteArrayOutputStream htmlOut = new ByteArrayOutputStream();
//				        report.toHtml(htmlOut);
//				        
//				        // Return raw HTML instead of Base64
//				        return htmlOut.toString(StandardCharsets.UTF_8.name());
				    	
				    	JasperPrint jasperPrint = report.toJasperPrint();

				    	HtmlExporter exporter = new HtmlExporter();
				    	exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

				    	ByteArrayOutputStream htmlOut = new ByteArrayOutputStream();
				    	SimpleHtmlExporterOutput output = new SimpleHtmlExporterOutput(htmlOut);

				    	// Embed images as Base64
				    	SimpleHtmlReportConfiguration reportConfig = new SimpleHtmlReportConfiguration();
				    	reportConfig.setEmbedImage(true);  // crucial for images

				    	exporter.setConfiguration(reportConfig);
				    	exporter.setExporterOutput(output);

				    	// Export fully in memory
				    	exporter.exportReport();

				    	// Return HTML string
				    	return htmlOut.toString(StandardCharsets.UTF_8.name());

				    	
				    } else {
				        // Normal PDF flow (write to disk)
				        try (FileOutputStream fos = new FileOutputStream(filePath)) {
				            report.toPdf(fos);
				        }

				        byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
				        byte[] encodedBytes = Base64.getEncoder().encode(inputfile);
				        return new String(encodedBytes);
				    }

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
		logger.debug("generateLoanApplicationPdf Function end");
		return null;

	}
	
	
	private ComponentBuilder<?, ?> createThreeHorizontalList2(String value, String value1, String value2) {
		HorizontalListBuilder horizontalList = cmp.horizontalList();
		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(33));
		horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(33));
		horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(34));
		return horizontalList;
	}

	private ComponentBuilder<?, ?> createSixHorizontalList(String Key1, String value1, String Key2, String value2,
														   String Key3, String value3) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(18));
		horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(15));
		horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(18));
		horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(15));
		horizontalList.add(cmp.text(Key3).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(18));
		horizontalList.add(cmp.text(value3).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(16));

		return horizontalList;
	}

	public static String getTodayData() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
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
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
		String ds2 = null;
		try {
			ds2 = sdf2.format(sdf1.parse(DOB));
		} catch (ParseException e) {
            logger.debug("Error while date format {}", String.valueOf(e));
		}
		return ds2;
	}

	private ComponentBuilder<?, ?> getLoanAndRepaymrntDetails(JSONObject keysForContent, List<RepaymentScheduleDisbursed> repaymentList, String sactionedDateStr, CibilDetailsPayload cibilPayload, String disburseDateStr, String lonAmountDisbursed) {
		VerticalListBuilder verticalList = cmp.verticalList();
		try {
			int noOfEmis = repaymentList.size();
			logger.debug("Number of Records: " + noOfEmis);
			String interest = (cibilPayload.getRoi() == null) ? "" : cibilPayload.getRoi();
			
			String repymtStartDate = "";
			String repymtEndDate = "";
			String emi = "";
			if(!repaymentList.isEmpty()) {
				emi = repaymentList.get(2).getTotal(); 
				String startDate = repaymentList.get(0).getDate();	
				String endDate = repaymentList.get(repaymentList.size() - 1).getDate();	
				try {
					repymtStartDate = CommonUtils.dateFormat1(startDate);
					repymtEndDate = CommonUtils.dateFormat1(endDate);
				}catch (Exception e) {
					logger.debug("error while date format " + e);
				}
			}
				
			verticalList.add(createSixHorizontalList(keysForContent.getString("sanctionedDate"), 
					sactionedDateStr, keysForContent.getString("loanTenure"),
					cibilPayload.getFinalTenure(), keysForContent.getString("loanAmountDisbursed"), "Rs."+CommonUtils.amountFormat(lonAmountDisbursed)+"/-"));
			
			verticalList.add(createSixHorizontalList(keysForContent.getString("dateOfDisbursement"), 
					disburseDateStr, keysForContent.getString("repaymentFrequency"),
					cibilPayload.getRepaymentFrequency(), keysForContent.getString("noOfInstallments"), String.valueOf(noOfEmis)));
			
			verticalList.add(createSixHorizontalList(keysForContent.getString("repaymentStartDate"), 
					repymtStartDate, keysForContent.getString("interestRate"),
					interest + " %", keysForContent.getString("installmentAmount"),  "Rs."+CommonUtils.amountFormat(emi)+"/-"));
			
			verticalList.add(createSixHorizontalList(keysForContent.getString("repaymentEndDate"), repymtEndDate, "","", "", ""));
	
			} catch (Exception e) {
				logger.error("error - getLoanDetails");
				logger.error(e.getMessage());
			}
		logger.debug("LoanDetails added");
		return verticalList;
	}

	private ComponentBuilder<?, ?> getBorrowerDetailsForWelcomeLetter(JSONObject keysForContent,
																	  CustomerDataFields custmrDataFields) {
	
		// Address
		JSONObject addressDetailsObj = getAllAddressDeatils(custmrDataFields);

		VerticalListBuilder verticalList = cmp.verticalList();

		verticalList.add(createThreeHorizontalList2(keysForContent.getString("applicantType"),
				keysForContent.getString("name"), keysForContent.getString("residingAt")));
		verticalList.add(createThreeHorizontalList2(keysForContent.getString("applicant"), applicantName,
				addressDetailsObj.getString(Constants.PRESENT_ADDRESS_APPLICANT)));
		verticalList.add(createThreeHorizontalList2(keysForContent.getString("coApplicant"), coApplicantName,
				addressDetailsObj.getString(Constants.PRESENT_ADDRESS_COAPPLICANT)));

		return verticalList;
	}

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
			String occpnAddrApplicant;
			String occpnAddrCoApplicant;

			// Applicant Address
			if( applicantAddrPayLoadLst != null) {
				for (Address addr : applicantAddrPayLoadLst) {
					if (addr.getAddressType().equalsIgnoreCase("present")) {
// 					presentAddressApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3()
// 							+ addr.getArea() +  addr.getLandMark() + addr.getCity() + addr.getDistrict() + addr.getState()
// 							+ addr.getCountry() + addr.getPinCode();

						presentAddressApplicant = getFullAddress(addr);
						logger.debug("Present Address - Applicant : " + presentAddressApplicant);
						presentResidenceOwnership = addr.getResidenceOwnership();
						presentAddressYears = addr.getResidenceAddressSince();
						presntCityYears = addr.getResidenceCitySince();
						presentResidenceAddressProof = addr.getCurrentAddressProof();
						presentResidenceType = addr.getHouseType();
					} else if (addr.getAddressType().equalsIgnoreCase("Permanent")) {
// 					permanetAddressApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3()
// 							+ addr.getArea()  + addr.getLandMark() + addr.getCity() + addr.getDistrict() + addr.getState()
// 							+ addr.getCountry() + addr.getPinCode();
						permanetAddressApplicant = getFullAddress(addr);
						logger.debug("Permanent Address - Applicant :" + permanetAddressApplicant);
					}
				}
			}

			// Co-Applicant Address
			if (coApplicantAddrPayLoadLst != null) {
				for (Address addr : coApplicantAddrPayLoadLst) {
					if (addr.getAddressType().equalsIgnoreCase("present")) {
// 						presentAddressCoApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3() + addr.getArea() + addr.getLandMark() + addr.getCity() + addr.getDistrict()+ addr.getState() + addr.getCountry() + addr.getPinCode();
						presentAddressCoApplicant = getFullAddress(addr);
						logger.debug("Present Address - Co-applicant :" + presentAddressCoApplicant);
						presentResidenceOwnershipCo = addr.getResidenceOwnership();
						presentAddressYearsCo = addr.getResidenceAddressSince();
						presntCityYearsCo = addr.getResidenceCitySince();
						presentResidenceAddressProofCo = addr.getCurrentAddressProof();
						presentResidenceTypeCo = addr.getHouseType();
					} else if (addr.getAddressType().equalsIgnoreCase("Permanent")) {
// 						permanetAddressCoApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3() + addr.getArea() + addr.getLandMark() + addr.getCity() + addr.getDistrict()+ addr.getState() + addr.getCountry() + addr.getPinCode();
						permanetAddressCoApplicant = getFullAddress(addr);
						logger.debug("permanent Address - Co-applicant. :" + permanetAddressCoApplicant);
					}
				}
			}

			// Occupation Address
			Address ocupnAddr = null;
 			Address ocupnAddrCo = null;
 			if (applicantOccupnAddrPayLoadLst != null && !applicantOccupnAddrPayLoadLst.isEmpty()) {
 			    ocupnAddr = applicantOccupnAddrPayLoadLst.get(0);
 			}
 			if (coApplicantOccupnAddrPayLoadLst != null && !coApplicantOccupnAddrPayLoadLst.isEmpty()) {
 				ocupnAddrCo = coApplicantOccupnAddrPayLoadLst.get(0);
 			}
// 			if(addr.getAddressType().equalsIgnoreCase("Office")) {
			occpnAddrApplicant = getFullAddress(ocupnAddr);
			occpnAddrCoApplicant = getFullAddress(ocupnAddrCo);
			logger.debug("Ocupation Address Applicnt : " + occpnAddrApplicant);
			logger.debug("Ocupation Address Co-Applicnt : " + occpnAddrCoApplicant);
// 				ocupnAddr.getAddressLine1() + ocupnAddr.getAddressLine2() + ocupnAddr.getAddressLine3()
// 				+ ocupnAddr.getArea() +  ocupnAddr.getLandMark() + ocupnAddr.getCity() + ocupnAddr.getDistrict() + ocupnAddr.getState()
// 				+ ocupnAddr.getCountry() + ocupnAddr.getPinCode();

// 			}else if(addr.getAddressType().equalsIgnoreCase("Office")) {
// 				occpnAddrCoApplicant = ocupnAddrCo.getAddressLine1() + ocupnAddrCo.getAddressLine2() + ocupnAddrCo.getAddressLine3()
// 				+ ocupnAddrCo.getArea() +  ocupnAddrCo.getLandMark() + ocupnAddrCo.getCity() + ocupnAddrCo.getDistrict() + ocupnAddrCo.getState()
// 				+ ocupnAddrCo.getCountry() + ocupnAddrCo.getPinCode();
// 				}

			jsnObj = new JSONObject();
			jsnObj.put(Constants.PRESENT_ADDRESS_APPLICANT, presentAddressApplicant);
			jsnObj.put("permanetAddressApplicant", permanetAddressApplicant);
			jsnObj.put(Constants.PRESENT_ADDRESS_COAPPLICANT, presentAddressCoApplicant);
			jsnObj.put("permanetAddressCoApplicant", permanetAddressCoApplicant);

			// other Deatils
			jsnObj.put("presentResidenceOwnership", presentResidenceOwnership);
			jsnObj.put("presentAddressYears", presentAddressYears);
			jsnObj.put("presntCityYears", presntCityYears);
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

}
