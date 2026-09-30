package com.iexceed.appzillonbanking.cob.report;

import com.google.gson.Gson;
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
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

public class RepaymentScheduleTemplate {
	private static final Logger logger = LogManager.getLogger(RepaymentScheduleTemplate.class);
	private StyleBuilder borderedStyle, boldText, boldCenteredStyle, boldTextWithBorder, boldLeftStyle, rightStyle,
			leftStyle;

	static String space = "\u00a0\u00a0\u00a0";
	
	private String applicantCustId="";
	private String coApplicantCustId ="";
	private String applicantName = "";
	private String coApplicantName = "";
	private CustomerDetails applicantCustDtls = null;
	private CustomerDetails coApplicantCustDtls = null;

	private String bmId ="";
	private String kmId ="";

	String appltGender ="";
	String coAppltGender ="";

	private String kmSubmDateStr ="";

	private String BLANK_STRING = " ";
	

	public RepaymentScheduleTemplate() {

		borderedStyle = stl.style(stl.penThin()).setPadding(5);
		boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
		boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		boldText = stl.style().bold();
		boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

//		StyleBuilder headerStyle = stl.style().setFontSize(20).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
		rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
		leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

	}
	public String generateRepaymentScuduleForWelcomeKit(JSONObject keysForContent, CustomerDataFields customerFields,
			String filePath, String productName, List<RepaymentScheduleDisbursed> repaymentList, String disburseDateStr, String language) {
		logger.debug("onEntrty :: generatePdfForDbKit");
		
		JasperReportBuilder report = new JasperReportBuilder();

		JasperReportBuilder subReport = new JasperReportBuilder();
		JasperReportBuilder subReport1 = new JasperReportBuilder();

		JasperReportBuilder subReport2 = new JasperReportBuilder();
		JasperReportBuilder subReport3 = new JasperReportBuilder();
		JasperReportBuilder subReport4 = new JasperReportBuilder();
		
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
					applicantCustDtls = custDtl;
					appltGender = payload1.getGender();
				}else if(custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
					coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
					coApplicantName = custDtl.getCustomerName();
					logger.debug("coApplicantCustId : " + coApplicantCustId);
					payload2  = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
					logger.debug("custCo-ApplicantPayload :" + payload2);
					coAppltGender = payload2.getGender();
					coApplicantCustDtls = custDtl;
				}
			}


			CibilDetailsPayload cibilDetailsPayload = CommonUtils.resolveCibilPayload(customerFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId,gsonObj );
			String repayntFrequency = "";
			repayntFrequency = cibilDetailsPayload.getRepaymentFrequency();

			int noOfEPIs = repaymentList.size();
			logger.debug("Number of Records: " + noOfEPIs);
			
			subReport1.title(repaymentScheduleTable(keysForContent, customerFields, cibilDetailsPayload, productName, disburseDateStr)).title(cmp.text(""));

			subReport2.title(cmp.text(""));
			subReport3.title(cmp.text(keysForContent.getString("closureLine1")).setMarkup(Markup.HTML));
			subReport3.title(cmp.text(keysForContent.getString("closureLine2")).setMarkup(Markup.HTML));
			subReport4.title(cmp.text(""));
			
			report
			.addSummary(cmp.subreport(subReport)).addSummary(cmp.subreport(subReport1))
			.addSummary(cmp.subreport(subReport2))
		    .summary(
		        cmp.verticalList(
		        		getRepaymentDetailsAsTable8(keysForContent, repaymentList, repayntFrequency)
		        )
		    )
		    .addSummary(cmp.subreport(subReport4))
		    .addSummary(cmp.subreport(subReport3));


			try {
				// Save report to file
//				try (FileOutputStream fos = new FileOutputStream(filePath)) {
//					report.toPdf(fos);
//				}
//
//				// Read file and encode to Base64
//				byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
//				return inputfile;
				
				 String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
				    logger.debug("inputLanguage: " + language);
				    
				    boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
				            .anyMatch(lang -> lang.equalsIgnoreCase(language));

				    if (isValidLanguage) {
				    	// Generate HTML in-memory, no file creation
				        ByteArrayOutputStream htmlOut = new ByteArrayOutputStream();
				        report.toHtml(htmlOut);
				       
				        // Return raw HTML instead of Base64
				        return htmlOut.toString(StandardCharsets.UTF_8.name());
//				    	JasperPrint jasperPrint = report.toJasperPrint();
//
//				    	HtmlExporter exporter = new HtmlExporter();
//				    	exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//
//				    	ByteArrayOutputStream htmlOut = new ByteArrayOutputStream();
//				    	SimpleHtmlExporterOutput output = new SimpleHtmlExporterOutput(htmlOut);
//
//				    	// Embed images as Base64
//				    	SimpleHtmlReportConfiguration reportConfig = new SimpleHtmlReportConfiguration();
//				    	reportConfig.setEmbedImage(true);  // crucial for images
//
//				    	exporter.setConfiguration(reportConfig);
//				    	exporter.setExporterOutput(output);
//
//				    	// Export fully in memory
//				    	exporter.exportReport();
//
//				    	// Return HTML string
//				    	return htmlOut.toString(StandardCharsets.UTF_8.name());

				    	
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
		return null;
	}
	
	private ComponentBuilder<?, ?> createSingleHorizontal(String Key) {
		HorizontalListBuilder horizontalList = cmp.horizontalList();
//		horizontalList.add(cmp.text(Key).setStyle(boldTextWithBorder).setWidth(100));
		 horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML)
			        .setStyle(stl.style(boldTextWithBorder).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER))
			        .setWidth(100));
		return horizontalList;

	}

	private ComponentBuilder<?, ?> createTwoHorizontalList(String Key, String value) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(37));
		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(33));

		return horizontalList;
	}

	private ComponentBuilder<?, ?> createFiveHorizontalList(String key1, String key2, String key3, String key4,
			String key5, int width1) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(key1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(18)); 
		horizontalList.add(cmp.text(key2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(19));
		horizontalList.add(cmp.text(key3).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(16)); 
		horizontalList.add(cmp.text(key4).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(17));
		horizontalList.add(cmp.text(key5).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(width1));

		return horizontalList;

	}

	private ComponentBuilder<?, ?> repaymentScheduleTable(JSONObject keysForContent, CustomerDataFields customerFields, CibilDetailsPayload cibilPayload, String productName, String disburseDateStr) {
		Gson gsonObj = new Gson();
		LoanDetailsPayload loanPayload = gsonObj.fromJson(customerFields.getLoanDetails().getPayloadColumn(),
				LoanDetailsPayload.class);
		
		int totalInsurance = toFindSum(cibilPayload.getInsuranceChargeJoint(),
				cibilPayload.getInsuranceChargeMember(), cibilPayload.getInsuranceChargeSpouse());

		BigDecimal sactionAmtDb = customerFields.getLoanDetails().getSanctionedLoanAmount();
		String sactionAmt = (sactionAmtDb == null) ? "" : sactionAmtDb.toPlainString();
		
		String interest = (cibilPayload.getRoi() == null) ? "" : cibilPayload.getRoi().toString();
		logger.debug("Interest rate :"+ interest);
		
		String loanAcountNumber = customerFields.getLoanDetails().getT24LoanId() == null ? "" : customerFields.getLoanDetails().getT24LoanId();
		
		HorizontalListBuilder noGapList = cmp.horizontalList();
		VerticalListBuilder noGapVerticalList = cmp.verticalList();

		noGapVerticalList
				.add(createSingleHorizontal(keysForContent.getString("applicationName")).setStyle(boldCenteredStyle));
		noGapVerticalList.add(createFiveHorizontalList(keysForContent.getString("customerID"), customerFields.getApplicationMaster().getProductCode()
						.equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE) ?
						customerFields.getApplicationMaster().getApplicantT24Id() :
						customerFields.getApplicationMaster().getSearchCode2() ,
				keysForContent.getString("customerName"), applicantName, keysForContent.getString("scan"), 30));
		noGapList.add(noGapVerticalList.setWidth(70));

		HorizontalListBuilder withGapList = cmp.horizontalList();
		VerticalListBuilder withGapVerticalList = cmp.verticalList();

		withGapVerticalList.add(createFiveHorizontalList(keysForContent.getString("spouseName"), coApplicantName,
				keysForContent.getString("kendraName"), customerFields.getApplicationMaster().getKendraName(), "", 0));
		withGapVerticalList.add(createFiveHorizontalList(keysForContent.getString("loanAccount"), loanAcountNumber,
				keysForContent.getString("loanAmount"), CommonUtils.formatIndianCurrency(sactionAmt), "", 0));
		withGapVerticalList.add(createFiveHorizontalList(keysForContent.getString("productName"), productName,
				keysForContent.getString("moratorium"), keysForContent.getString("moratoriumValue"), "", 0));
		withGapVerticalList.add(createFiveHorizontalList(keysForContent.getString("roi"), interest + " %",
				keysForContent.getString("loanPurpose"), loanPayload.getLoanPurpose(), "", 0));
		withGapVerticalList.add(createFiveHorizontalList(keysForContent.getString("disbursementDate"), disburseDateStr, 
				keysForContent.getString("loanTerm"), cibilPayload.getFinalTenure(), "", 0));
		withGapVerticalList.add(createFiveHorizontalList(keysForContent.getString("insurancePremium"), CommonUtils.formatIndianCurrency(String.valueOf(totalInsurance)),
				keysForContent.getString("phoneNumber"), applicantCustDtls.getMobileNumber(), "", 0));
		withGapVerticalList.add(createTwoHorizontalList(keysForContent.getString("processingFee"), CommonUtils.formatIndianCurrency(cibilPayload.getProcessingFees())));

		withGapList.add(withGapVerticalList.setWidth(70));
		withGapList.add(cmp.text("").setStyle(borderedStyle).setWidth(30)); // right-side gap

		return cmp.verticalList(noGapList, withGapList);
	}
	
	private ComponentBuilder<?, ?> getRepaymentDetailsAsTable8(JSONObject keysForContent, List<RepaymentScheduleDisbursed> repaymentList, String repayntFrequency) {
	    logger.debug("inside :: getRepaymentDetailsAsTable");
	   
	    DRDataSource dataSource = new DRDataSource(
	        "SlNo", "Date", Constants.PRINCIPAL, Constants.INTEREST, Constants.TOTAL,
	        Constants.PRINCIPAL_OS, Constants.INTEREST_OS, Constants.TOTAL_OS, Constants.KM_SIGNATURE
	    );

	    for (RepaymentScheduleDisbursed schedule : repaymentList) {
	        dataSource.add(
	            schedule.getSlNo(),
	            CommonUtils.dateFormat1(schedule.getDate()),
	            formatIndianCurrency(schedule.getPrincipal()),
	            formatIndianCurrency(schedule.getInterest()),
	            formatIndianCurrency(schedule.getTotal()),
	            formatIndianCurrency(schedule.getPrincipalOs()),
	            formatIndianCurrency(schedule.getInterestOs()),
	            formatIndianCurrency(String.valueOf(
	            		toFindSumAsString(schedule.getPrincipalOs(),schedule.getInterestOs())
	            		)), // totalOs placeholder
	            ""   // KM Signature placeholder
	        );
	    }

	    // Base cell style
//	    StyleBuilder cellStyle = stl.style()
//	        .setPadding(3)
//	        .setTopBorder(stl.pen1Point())
//	        .setLeftBorder(stl.penThin())
//	        .setRightBorder(stl.penThin())
//	        .setBottomBorder(stl.penThin());

    StyleBuilder borderStyle = stl.style(stl.penThin()).setPadding(3);
	    
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
		        cmp.text(keysForContent.getString("slno")).setMarkup(Markup.HTML).setFixedWidth(40).setFixedHeight(45).setStyle(headerStyle),
		        cmp.text(keysForContent.getString("date")).setMarkup(Markup.HTML).setFixedWidth(60).setFixedHeight(45).setStyle(headerStyle),

		        cmp.verticalList(
		            cmp.text(repayntFrequency +"  "+ keysForContent.getString("instalment")).setMarkup(Markup.HTML).setStyle(headerStyle).setFixedHeight(20),
////		        	    cmp.horizontalList(
////		        	            cmp.text(frequency).setStyle(frequencyStyle),  // different style for frequency
////		        	            cmp.text(keysForContent.getString("instalment")).setStyle(headerStyle)
//		        	        ).setFixedHeight(20),  creates new column
		        	cmp.horizontalList(
		                cmp.text(keysForContent.getString(Constants.PRINCIPAL)).setMarkup(Markup.HTML).setFixedWidth(60).setFixedHeight(25).setStyle(headerStyle),
		                cmp.text(keysForContent.getString(Constants.INTEREST.toLowerCase())).setMarkup(Markup.HTML).setFixedWidth(55).setFixedHeight(25).setStyle(headerStyle),
		                cmp.text(keysForContent.getString(Constants.TOTAL.toLowerCase())).setMarkup(Markup.HTML).setFixedWidth(65).setFixedHeight(25).setStyle(headerStyle)
		            ).setGap(0)
		        ).setGap(0).setFixedWidth(180),

		        cmp.verticalList(
		            cmp.text(keysForContent.getString("balance")).setMarkup(Markup.HTML).setStyle(headerStyle).setFixedHeight(20),
		            cmp.horizontalList(
		                cmp.text(keysForContent.getString(Constants.PRINCIPAL)).setMarkup(Markup.HTML).setFixedWidth(60).setFixedHeight(25).setStyle(headerStyle),
		                cmp.text(keysForContent.getString(Constants.INTEREST.toLowerCase())).setMarkup(Markup.HTML).setFixedWidth(55).setFixedHeight(25).setStyle(headerStyle),
		                cmp.text(keysForContent.getString(Constants.TOTAL.toLowerCase())).setMarkup(Markup.HTML).setFixedWidth(65).setFixedHeight(25).setStyle(headerStyle)
		            ).setGap(0)
		        ).setGap(0).setFixedWidth(180),

		        cmp.text(keysForContent.getString(Constants.KM_SIGNATURE)).setMarkup(Markup.HTML).setFixedWidth(75).setFixedHeight(45).setStyle(headerStyle)
		    ).setGap(0);	
	
	JasperReportBuilder subReport = report()
		    .columnHeader(groupedHeaderRow)
		    .columns(
		        col.column("", "SlNo", type.stringType()).setFixedWidth(40).setStyle(centerStyle),
		        col.column("", "Date", type.stringType()).setFixedWidth(60).setStyle(centerStyle),
		        col.column("", Constants.PRINCIPAL, type.stringType()).setFixedWidth(60).setStyle(centerStyle),
		        col.column("", Constants.INTEREST, type.stringType()).setFixedWidth(55).setStyle(centerStyle),
		        col.column("", Constants.TOTAL, type.stringType()).setFixedWidth(65).setStyle(centerStyle),
		        col.column("", Constants.PRINCIPAL_OS, type.stringType()).setFixedWidth(60).setStyle(centerStyle),
		        col.column("", Constants.INTEREST_OS, type.stringType()).setFixedWidth(55).setStyle(centerStyle),
		        col.column("", Constants.TOTAL_OS, type.stringType()).setFixedWidth(65).setStyle(centerStyle),
		        col.column("", Constants.KM_SIGNATURE, type.stringType()).setFixedWidth(75)
		    )
		    .setColumnStyle(borderStyle)  // this is useful
		    .setDataSource(dataSource);
    return cmp.subreport(subReport);
}

	
	   public static String formatIndianCurrency(String amountStr) {
	      	 if (amountStr == null || amountStr.trim().isEmpty()) {
	      	        return "0.00";
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

  public Response generatePdf(JSONObject keysForContent, CustomerDataFields customerFields,
				List<RepaymentScheduleDisbursed> repaymentList, String sactionedDateStr, String productName, String disburseDateStr) {
			logger.debug("inside generatePdf : ");
			Response response;
			try {
				String filePath = "";
				JasperReportBuilder report = new JasperReportBuilder();

				JasperReportBuilder subReport = new JasperReportBuilder();
				JasperReportBuilder subReport1 = new JasperReportBuilder();

				JasperReportBuilder subReport2 = new JasperReportBuilder();
				JasperReportBuilder subReport3 = new JasperReportBuilder();
//				JasperReportBuilder subReport4 = new JasperReportBuilder();
			
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
							applicantCustDtls = custDtl;
							appltGender = payload1.getGender();
						}else if(custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
							coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
							coApplicantName = custDtl.getCustomerName();
							logger.debug("coApplicantCustId : " + coApplicantCustId);
							payload2  = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
							logger.debug("custCo-ApplicantPayload :" + payload2);
							coAppltGender = payload2.getGender();
							coApplicantCustDtls = custDtl;
						}
					}

					CibilDetailsPayload cibilDetailsPayload = CommonUtils.resolveCibilPayload(customerFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId,gsonObj );
					String repayntFrequency = "";
					repayntFrequency = cibilDetailsPayload.getRepaymentFrequency();
					
					subReport1.title(repaymentScheduleTable(keysForContent, customerFields, cibilDetailsPayload, productName, disburseDateStr)).title(cmp.text(""));

					subReport2.title(cmp.text(""));
					subReport3.title(cmp.text(keysForContent.getString("closureLine1")).setMarkup(Markup.HTML));
					subReport3.title(cmp.text(keysForContent.getString("closureLine2")).setMarkup(Markup.HTML));
					
					report
					.addSummary(cmp.subreport(subReport)).addSummary(cmp.subreport(subReport1))
					.addSummary(cmp.subreport(subReport2))
				    .summary(
				        cmp.verticalList(
				        		getRepaymentDetailsAsTable8(keysForContent, repaymentList, repayntFrequency)
				        )
				    )
				    .addSummary(cmp.subreport(subReport3));
					
				
					response = new Response();
					
					Properties prop = CommonUtils.readPropertyFile();
					// Construct file path
					String filePathDest = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + "APZCBO" + "/"
							+ Constants.LOAN + "/" + customerFields.getApplicationId() + "/" + Constants.WELCOMEKIT + "/";
					
					logger.debug("filePathDest :: {}", filePathDest);

					// Ensure directory exists
					File directory = new File(filePathDest);
					if (!directory.exists()) {
						boolean isCreated = directory.mkdirs();
						if (!isCreated) {
							throw new IOException("Failed to create directory: " + filePathDest);
						}
					}

					filePath = filePathDest + customerFields.getApplicationId() + "_WelcomeLetter" + ".pdf";

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
		
		private String toFindSumAsString(String... value) {
		    double total = Stream.of(value)
		        .map(val -> {
		            try {
		                String cleaned = Optional.ofNullable(val).orElse("0")
		                                         .replaceAll("[^\\d.]", "");
		                return Double.parseDouble(cleaned);
		            } catch (NumberFormatException e) {
		                return 0.0;
		            }
		        })
		        .reduce(0.0, Double::sum);

		    return String.valueOf(total);
		}
}
