package com.iexceed.appzillonbanking.cob.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.OccupationDetails;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.LoanDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.OccupationDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.OccupationDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationWorkflowRepository;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.WorkflowStatus;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;
import com.iexceed.appzillonbanking.cob.service.COBService;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.ReportStyleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.Markup;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.exception.DRException;

public class MSMEReport {
	private static final Logger logger = LogManager.getLogger(MSMEReport.class);
	private StyleBuilder borderedStyle, boldText, boldCenteredStyle, boldTextWithBorder, boldLeftStyle , rightStyle, leftStyle;

	static String space = "\u00a0\u00a0\u00a0";
	
	private String applicantCustId="";
	private String coApplicantCustId ="";
	private String applicantName = "";
	private String coApplicantName = "";
	private CustomerDetails applicantCustDtls = null;
	private CustomerDetails coApplicantCustDtls = null;
	String appltGender ="";
	String coAppltGender ="";
	String interestRate = "";
	
	public MSMEReport() {
		 
		borderedStyle = stl.style(stl.penThin()).setPadding(5);
		boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
		boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		boldText = stl.style().bold();
		boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		
	    rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
	    leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
	    
	}

	public String generatePdfForDbkit(JSONObject keysForContent, String filePath, CustomerDataFields custmrDataFields, String sactionedDateStr, String language) throws DRException, IOException {

		StyleBuilder tempStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
		
		JasperReportBuilder report = new JasperReportBuilder();
		
		StyleBuilder headerStyle = stl.style().setFontSize(20)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
		StyleBuilder style = stl.style().setBackgroundColor(Color.GRAY).setFontSize(20)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

		StyleBuilder style1 = stl.style().setBackgroundColor(Color.GRAY).setFontSize(10)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		

//		JasperReportBuilder subReport = new JasperReportBuilder();
//		JasperReportBuilder subReport1 = new JasperReportBuilder();
//		JasperReportBuilder subReport2 = new JasperReportBuilder();
//		JasperReportBuilder subReport3 = new JasperReportBuilder();
//		JasperReportBuilder subReport4 = new JasperReportBuilder();
//		JasperReportBuilder subReport5 = new JasperReportBuilder();
//		JasperReportBuilder subReport6 = new JasperReportBuilder();
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
		
		
		report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));		
		
		/* Basic Application Details */
		CustomerDetailsPayload payload1 = null;
		CustomerDetailsPayload payload2 =null;
		Gson gsonObj = new Gson();
		for(CustomerDetails custDtl : custmrDataFields.getCustomerDetailsList()) {
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
		
		OccupationDetails coApplicant = null;
		OccupationDetailsPayload occPayload1 =null;
		OccupationDetailsPayload occPayload2 =null;
		for(OccupationDetailsWrapper applicantwrpr : custmrDataFields.getOccupationDetailsWrapperList()) {
			logger.debug("applicantCustId " + applicantCustId);
			if(String.valueOf(applicantwrpr.getOccupationDetails().getCustDtlId()).equals(applicantCustId)){
				occPayload1  = gsonObj.fromJson(applicantwrpr.getOccupationDetails().getPayloadColumn(), OccupationDetailsPayload.class);
				logger.debug("occupationApplicantPayload : " + payload1.toString());
			}else if(String.valueOf(applicantwrpr.getOccupationDetails().getCustDtlId()).equals(coApplicantCustId)){
				coApplicant = applicantwrpr.getOccupationDetails();
				occPayload2 = gsonObj.fromJson(applicantwrpr.getOccupationDetails().getPayloadColumn(), OccupationDetailsPayload.class);
				logger.debug("occupationCo-appliocantPayload : "+payload2.toString());
			}
		}
		
		BigDecimal sactionAmtDb = custmrDataFields.getLoanDetails().getSanctionedLoanAmount();
		String sactionAmt = String.valueOf(sactionAmtDb == null ? "" : sactionAmtDb.toPlainString());
		
		long amount = sactionAmtDb == null ? 0L : sactionAmtDb.longValue();
		String snAmtInWords = convert(amount);
		
        String urn = "NA";
        if(custmrDataFields.getApplicationMaster().getProductCode().equalsIgnoreCase(Constants.UNNATI_PRODUCT_CODE)) {
            urn = StringUtils.isEmpty(custmrDataFields.getLeadDetails().getUrn()) ? "NA" : custmrDataFields.getLeadDetails().getUrn();
        }else if(custmrDataFields.getApplicationMaster().getProductCode().equalsIgnoreCase(Constants.RENEWAL_LOAN_PRODUCT_CODE)){
            urn = StringUtils.isEmpty(custmrDataFields.getRenewalLeadDetails().getUrn()) ? "NA" : custmrDataFields.getRenewalLeadDetails().getUrn();
        }
//		Gson gsonObj = new Gson();
		LoanDetailsPayload loanPayload  = gsonObj.fromJson(custmrDataFields.getLoanDetails().getPayloadColumn(), LoanDetailsPayload.class);
		logger.debug("LoanDetailsPayload : " + loanPayload);
		
		CibilDetailsPayload cibilPayloadCoApp = new CibilDetailsPayload();
		for (CibilDetailsWrapper cibilDetailsWrapper : custmrDataFields.getCibilDetailsWrapperList()) {
			String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
//		String customerType = applicant ? applicantCustId : coApplicantCustId;
			
			logger.debug("CreditDetailsPayload Payload : " + cibilPayloadCoApp);
			if (custId.equals(coApplicantCustId)) {
				cibilPayloadCoApp = gsonObj.fromJson(
						cibilDetailsWrapper.getCibilDetails().getPayloadColumn(), CibilDetailsPayload.class);	
			}
		
		}
		
		String interest = String.valueOf(cibilPayloadCoApp.getRoi() == null ? "": cibilPayloadCoApp.getRoi().toString());
		logger.debug("interest : " + interest);
		
		String letter1 = keysForContent.getString("letter1");
		String letter2 = keysForContent.getString("letter2");
		String letter3 = keysForContent.getString("letter3");
		logger.debug("letter1 : " + letter1);
		
		String finalLetter2 = letter2.replace("<purpose>", loanPayload.getLoanPurpose()).replace("<subPurpose>", loanPayload.getSubCategory()).replace("<nameOfTheOrganisation>", occPayload1.getOrganisationName());
		String finalLetter3	= letter3.replace("<sanctionedAmount>", CommonUtils.amountFormat(sactionAmt)+"/- ").replace("<sanctionedAmountInWords>", snAmtInWords);
		logger.debug("finalLetter2 : " + finalLetter2);
		logger.debug("finalLetter3 : " + finalLetter3);
		
		subReport7.title(cmp.text(keysForContent.getString("subApplicationName1")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14).underline()));
//		subReport7.title(cmp.text(keysForContent.getString("to")+"\t\t\t\t\t\t\t\t\t\t"+keysForContent.getString("appId")+":_________").setStyle(boldLeftStyle));
		subReport7.title(
			    cmp.horizontalList(
			        cmp.text(keysForContent.getString("to")).setMarkup(Markup.HTML).setStyle(leftStyle),
			        cmp.text(keysForContent.getString("appId") + ": "+custmrDataFields.getApplicationId()).setMarkup(Markup.HTML).setStyle(rightStyle)
			    		)
				);
//		subReport7.title(cmp.text(keysForContent.getString("cagl")+"\t\t\t\t"+keysForContent.getString("loanId")+"_________").setStyle(boldLeftStyle));
		subReport7.title(
			    cmp.horizontalList(
			        cmp.text(keysForContent.getString("cagl")).setMarkup(Markup.HTML).setStyle(leftStyle),
			        cmp.text(keysForContent.getString("loanId") + ": "+custmrDataFields.getLoanDetails().getT24LoanId()).setMarkup(Markup.HTML).setStyle(rightStyle)
			    		)
				);
		
		subReport7.title(cmp.text(keysForContent.getString("caglAddress1")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport7.title(cmp.text(keysForContent.getString("caglAddress2")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport7.title(cmp.text(""));
		
		subReport7.title(cmp.text(keysForContent.getString("date")+": " + sactionedDateStr).setMarkup(Markup.HTML).setStyle(leftStyle));
//		subReport7.title(cmp.text(keysForContent.getString("letter1")).setStyle(leftStyle));
//		subReport7.title(cmp.text(keysForContent.getString("letter2")).setStyle(leftStyle));
		subReport7.title(cmp.text(letter1).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport7.title(cmp.text(finalLetter2).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport7.title(cmp.text(finalLetter3).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport7.title(cmp.text(keysForContent.getString("tableName")).setMarkup(Markup.HTML).setStyle(boldLeftStyle));
		
		subReport7.title(getManufacturingLoanForCombinedBooklet(keysForContent)).title(cmp.text(""));
		subReport8.title(cmp.text(keysForContent.getString("particulars")).setMarkup(Markup.HTML).setStyle(leftStyle));
		
		subReport9.title(getInvestmentParticularsForCombinedBooklet(keysForContent)).title(cmp.text(""));
		
		subReport10.title(cmp.text(keysForContent.getString("gstinNo")+"  NA").setMarkup(Markup.HTML).setStyle(boldLeftStyle));
//		subReport10.title(cmp.text(keysForContent.getString("udyamNo")+"  ___________________________").setStyle(boldLeftStyle));
		subReport10.title(cmp.text(keysForContent.getString("udyamNo")+" " + urn).setMarkup(Markup.HTML).setStyle(boldLeftStyle));
		subReport10.title(cmp.text(""));
//		subReport11.title(createTwoHorizontalListWithCustomisedWidth("i", keysForContent.getString("gstinDeclaration1"), 5, 80, leftStyle));
//		subReport11.title(createTwoHorizontalListWithCustomisedWidth("\nii", keysForContent.getString("gstinDeclaration2"), 5, 80, leftStyle));
//		subReport11.title(createTwoHorizontalListWithCustomisedWidth("\niii", keysForContent.getString("gstinDeclaration3"), 5, 80, leftStyle));
//		subReport11.title(createTwoHorizontalListWithCustomisedWidth("\niv", keysForContent.getString("gstinDeclaration4"), 5, 80, leftStyle));
//		subReport11.title(createTwoHorizontalListWithCustomisedWidth("\nv", keysForContent.getString("gstinDeclaration5"), 5, 80, leftStyle));
		
		subReport11.title(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("number1"), keysForContent.getString("gstinDeclaration1"), 5, 80, leftStyle));
		subReport11.title(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("number2"), keysForContent.getString("gstinDeclaration2"), 5, 80, leftStyle));
		subReport11.title(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("number3"), keysForContent.getString("gstinDeclaration3"), 5, 80, leftStyle));
		subReport11.title(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("number4"), keysForContent.getString("gstinDeclaration4"), 5, 80, leftStyle));
		subReport11.title(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("number5"), keysForContent.getString("gstinDeclaration5"), 5, 80, leftStyle));
		
		subReport12.title(cmp.text(keysForContent.getString("thankingYou")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport12.title(cmp.text(""));
		subReport12.title(createFourHorizontalListForConsentLetter(keysForContent.getString("borrowerName"), applicantName, keysForContent.getString("coborrowerName"), coApplicantName));
		subReport12.title(createFourHorizontalListForConsentLetter(keysForContent.getString("borrowerSign"), "", keysForContent.getString("coborrowerSign"), ""));
		
		report.
		 addSummary(cmp.subreport(subReport7))
		.addSummary(cmp.subreport(subReport8))
		.addSummary(cmp.subreport(subReport9))
		.addSummary(cmp.subreport(subReport10))
		.addSummary(cmp.subreport(subReport11))
		.addSummary(cmp.subreport(subReport12))
		.addSummary(cmp.subreport(subReport13))
		.addSummary(cmp.subreport(subReport14))
		.addSummary(cmp.subreport(subReport15))
		.addSummary(cmp.subreport(subReport16));

//        FileOutputStream fos = new FileOutputStream(filePath);
//	        try {
//	            report.toPdf(fos);
//	        } catch (DRException e) {
//	            
//	        }
//	        fos.close();
//	        byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
//	        byte[] encodedBytes = Base64.getEncoder().encode(inputfile);
//	      util.deleteFile(filePath);
//	        return new String(encodedBytes);

	        try {
		    String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
		    logger.debug("inputLanguage " + language);
		    
		    boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
		            .anyMatch(lang -> lang.equalsIgnoreCase(language));

		    if (isValidLanguage) {
		        // Generate HTML in-memory, no file creation
		        ByteArrayOutputStream htmlOut = new ByteArrayOutputStream();
		        report.toHtml(htmlOut);
		        
		        // Return raw HTML instead of Base64
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

		} catch (Exception e) {
		    throw e;
		}

	}

		
	private ComponentBuilder<?, ?> getInvestmentParticularsForCombinedBooklet(JSONObject keysForContent) {
		
			VerticalListBuilder verticalList = cmp.verticalList();
	
			verticalList.add(createThreeHorizontalList(keysForContent.getString("serialNum"), keysForContent.getString("machineryDetails").trim(), keysForContent.getString("cost")));
			verticalList.add(createThreeHorizontalList("1","NA","NA"));
			verticalList.add(createThreeHorizontalList("2","NA","NA"));
			verticalList.add(createThreeHorizontalList("3","NA","NA"));
			
			return verticalList;
		}	

	
	private ComponentBuilder<?, ?> getManufacturingLoanForCombinedBooklet(JSONObject keysForContent) {
		
		VerticalListBuilder verticalList = cmp.verticalList();
		verticalList.add(createThreeHorizontalListForCombinedBooklet(keysForContent.getString("microLoan"), keysForContent.getString("smallLoan"), keysForContent.getString("mediumLoan")));
		verticalList.add(createThreeHorizontalListForCombinedBooklet(keysForContent.getString("microLoanValue"), keysForContent.getString("smallLoanValue"), keysForContent.getString("mediumLoanValue")));
		
		return verticalList;
	}
	
	private ComponentBuilder<?, ?> createTwoHorizontalList(String Key, String value, ReportStyleBuilder style) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(style));
		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(style));

		return horizontalList;
	}
		private ComponentBuilder<?, ?> createThreeHorizontalListForCombinedBooklet(String value, String value1, String value2) {
			HorizontalListBuilder horizontalList = cmp.horizontalList();
			horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(33));
			horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(33));
			horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(34));
			return horizontalList;
		}
		
		
		private ComponentBuilder<?, ?> createThreeHorizontalList(String key, String value1, String value2) {
			HorizontalListBuilder horizontalList = cmp.horizontalList();
	//		horizontalList.add(cmp.text(key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(20));
			horizontalList.add(cmp.text(key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(20));
			horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(40));
			horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(40));
			return horizontalList;
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

	/*public static String formatCurrency(String amount) {
		double d = Double.parseDouble(amount);
		//DecimalFormat f = new DecimalFormat("#,##,##0.00");
		return f.format(d);
	}*/

private ComponentBuilder<?, ?> createSingleHorizontalListForScheduleA(String Key) {
		
		HorizontalListBuilder horizontalList = cmp.horizontalList();

		// for (int i = 0; i < columns; i++) {

//		horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(25));
//		horizontalList.add(cmp.text(value).setStyle(borderedStyle).setWidth(75));
		
		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(100));
		// horizontalList.add(createVerticalList("6","5"));

		// }

		return horizontalList;

	}
private ComponentBuilder<?, ?> createFourHorizontalListForConsentLetter(String Key, String value, String key1, String value1) {
	
	HorizontalListBuilder horizontalList = cmp.horizontalList();

	// for (int i = 0; i < columns; i++) {

//	horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(25));
//	horizontalList.add(cmp.text(value).setStyle(borderedStyle).setWidth(75));
	
	horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(15));
	horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(25));
	horizontalList.add(cmp.text(key1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(25));
	horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(35));
	// horizontalList.add(createVerticalList("6","5"));

	// }

	return horizontalList;

}
	private ComponentBuilder<?, ?> createFourHorizontalListForCombinedBooklet2(String Key, String value, String value1, String value2) {
		
		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(20));
		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(30));
		horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(20));
		horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(30));

		return horizontalList;
	}
		
	
private ComponentBuilder<?, ?> createTwoHorizontalListWithCustomisedWidth(String Key, String value, int firstWidth, int secondWidth, ReportStyleBuilder style) {
	HorizontalListBuilder horizontalList = cmp.horizontalList();
	horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(style).setWidth(firstWidth));
	horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(style).setWidth(secondWidth));

	return horizontalList;
}


private ComponentBuilder<?, ?> createFiveHorizontalListForCombinedBooklet(String Key, String value1, String value2,String value3, String value4) {
	
	HorizontalListBuilder horizontalList = cmp.horizontalList();

//	horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(25));
//	horizontalList.add(cmp.text(value).setStyle(borderedStyle).setWidth(75));
	
	horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(15).setStyle(boldTextWithBorder));
	horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(35).setStyle(boldTextWithBorder));
	horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(10).setStyle(boldTextWithBorder));
	horizontalList.add(cmp.text(value3).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(10).setStyle(boldTextWithBorder));
	horizontalList.add(cmp.text(value4).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(30).setStyle(boldTextWithBorder));

	return horizontalList;

}

private ComponentBuilder<?, ?> getNomineeDecForCombinedBooklet(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createFiveHorizontalListForCombinedBooklet("", keysForContent.getString("name"),keysForContent.getString("age"),keysForContent.getString("gender"),keysForContent.getString("relationship")).setStyle(boldLeftStyle.setFontSize(12)));
	verticalList.add(createSingleHorizontalListForScheduleA(keysForContent.getString("incaseBorrower")).setStyle(boldLeftStyle.setFontSize(12)));
	verticalList.add(createFiveHorizontalListForCombinedBooklet(keysForContent.getString("nominee"),"","","","").setStyle(boldLeftStyle.setFontSize(12)));
	verticalList.add(createFiveHorizontalListForCombinedBooklet(keysForContent.getString("appointee"),"","","","").setStyle(boldLeftStyle.setFontSize(12)));
	verticalList.add(createSingleHorizontalListForScheduleA(keysForContent.getString("insuranceCover")).setStyle(boldLeftStyle.setFontSize(12)));
		
	return verticalList;	
}


private static final String[] units = {
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
        "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] tens = {
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    public static String convert(long number) {
        if (number == 0) return "Zero";
        return convertNumber(number).trim();
    }

    private static String convertNumber(long number) {
        if (number < 20) return units[(int) number];
        if (number < 100) return tens[(int) number / 10] + " " + units[(int) number % 10];
        if (number < 1000) return units[(int) number / 100] + " Hundred " + convertNumber(number % 100);
        if (number < 100000) return convertNumber(number / 1000) + " Thousand " + convertNumber(number % 1000);
        if (number < 10000000) return convertNumber(number / 100000) + " Lakh " + convertNumber(number % 100000);
        return convertNumber(number / 10000000) + " Crore " + convertNumber(number % 10000000);
    }

	
}
