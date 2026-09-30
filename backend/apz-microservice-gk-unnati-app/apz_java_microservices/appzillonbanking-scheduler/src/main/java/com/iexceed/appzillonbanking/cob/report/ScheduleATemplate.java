package com.iexceed.appzillonbanking.cob.report;

import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.builder.style.ReportStyleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.Markup;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.payload.Address;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.ExistingLoanDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.InsuranceDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.LoanDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.NomineeDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.OccupationDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.RepaymentSchedule;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.BankDetailsRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ProductDetailsrepository;
import com.iexceed.appzillonbanking.cob.core.utils.AppStatus;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.loans.payload.ApplyLoanRequestFields;
import com.iexceed.appzillonbanking.cob.loans.payload.BCMPIIncomeDetailsWrapper;
import com.iexceed.appzillonbanking.cob.loans.payload.BCMPIOtherDetailsWrapper;
import com.iexceed.appzillonbanking.cob.loans.payload.FetchAppRequest;
import com.iexceed.appzillonbanking.cob.loans.payload.FetchAppRequestFields;
import com.iexceed.appzillonbanking.cob.loans.payload.LoanObligationsWrapper;
import com.iexceed.appzillonbanking.cob.loans.service.LoanService;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;
import com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserFields;
import com.iexceed.appzillonbanking.cob.payload.FetchDeleteUserRequest;
import com.iexceed.appzillonbanking.cob.service.COBService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;



public class ScheduleATemplate {
	private static final Logger logger = LogManager.getLogger(ScheduleATemplate.class); 
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
	
	public ScheduleATemplate() {
		 
		borderedStyle = stl.style(stl.penThin()).setPadding(5);
		boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
		boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		boldText = stl.style().bold();
		boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		
//		StyleBuilder headerStyle = stl.style().setFontSize(20).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
	    rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
	    leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
	    
	    
	}

	public String generatePdfForDbKit(JSONObject keysForContent, String filePath, CustomerDataFields custmrDataFields, String productName, List<RepaymentSchedule> repaymentList, String language) throws DRException, IOException {
			
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
		Gson gsonObj2 = new Gson();
		
		CibilDetailsPayload cibilPayloadCoApp = null;
		int totalInsurance = 0;
		String processingFee = "0";
		for (CibilDetailsWrapper cibilDetailsWrapper : custmrDataFields.getCibilDetailsWrapperList()) {
			String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
//		String customerType = applicant ? applicantCustId : coApplicantCustId;
			
			logger.debug("CreditDetailsPayload Payload : " + cibilPayloadCoApp);
			if (custId.equals(coApplicantCustId)) {
				cibilPayloadCoApp = gsonObj.fromJson(
						cibilDetailsWrapper.getCibilDetails().getPayloadColumn(), CibilDetailsPayload.class);
				processingFee = cibilPayloadCoApp.getProcessingFees() == null ? "0" : cibilPayloadCoApp.getProcessingFees();

				totalInsurance = toFindSum(cibilPayloadCoApp.getInsuranceChargeJoint(),
						cibilPayloadCoApp.getInsuranceChargeMember(), cibilPayloadCoApp.getInsuranceChargeSpouse());
			}
		
		}
		
		int noOfEPIs = repaymentList.size();
		logger.debug("Number of Records: " + noOfEPIs);
		
//		String repymtStartDate = "";
		String emi = "";
		String firstEmi = "";
		BigDecimal totalInterest = BigDecimal.ZERO;
		if( repaymentList.size() > 0) {
			firstEmi = repaymentList.get(0).getTotalDue();
			emi = repaymentList.get(2).getTotalDue();

			for (RepaymentSchedule schedule : repaymentList) {
				if (schedule.getInterest() != null && !schedule.getInterest().isEmpty()) {
					try {
						BigDecimal interest1 = new BigDecimal(schedule.getInterest());
						totalInterest = totalInterest.add(interest1);
					} catch (NumberFormatException e) {
						logger.error("Invalid interest value: " + e);
					}
				}
			}
		}

		LoanDetails loanDetails = custmrDataFields.getLoanDetails();
		BigDecimal sanctionedLoanAmount = loanDetails.getSanctionedLoanAmount();
		String sanctionedLoanAmountInWords = toCurrencyWords(sanctionedLoanAmount);

		List<ExistingGLLoanDetails> existingLoanDetailsList = new ArrayList<>();
		if(null != custmrDataFields.getExistingGLLoanDetails()
				&& !custmrDataFields.getExistingGLLoanDetails().isEmpty()){
			existingLoanDetailsList = custmrDataFields.getExistingGLLoanDetails();
		}
		String totalPreClosureAmount = "0";
		for(ExistingGLLoanDetails existingGLLoanDetails : existingLoanDetailsList){
			if(existingGLLoanDetails.getOutstandingAmount() != null) {
				totalPreClosureAmount = String.valueOf(new BigDecimal(totalPreClosureAmount).add(existingGLLoanDetails.getOutstandingAmount()));
			}
		}

		BigDecimal loanNetSanctionAmount = sanctionedLoanAmount.subtract(new BigDecimal(processingFee)).subtract(new BigDecimal(totalInsurance)).subtract(new BigDecimal(totalPreClosureAmount));

		String loanNetSanctionAmountInWords = toCurrencyWords(loanNetSanctionAmount);
		
		
		JasperReportBuilder report = new JasperReportBuilder();
		JasperReportBuilder subReport = new JasperReportBuilder();
		JasperReportBuilder subReport1 = new JasperReportBuilder();
		JasperReportBuilder subReport2 = new JasperReportBuilder();
		JasperReportBuilder subReport3 = new JasperReportBuilder();
		JasperReportBuilder subReport4 = new JasperReportBuilder();
		JasperReportBuilder subReport6 = new JasperReportBuilder();
		JasperReportBuilder subReport7 = new JasperReportBuilder();
		JasperReportBuilder subReport8 = new JasperReportBuilder();
		JasperReportBuilder subReport9 = new JasperReportBuilder();
		JasperReportBuilder disbursementConfirmationReport = new JasperReportBuilder();
		JasperReportBuilder loanDetailsReport = new JasperReportBuilder();
		JasperReportBuilder preCloseLoanDetailsReport = new JasperReportBuilder();
		
		JRDataSource emptyDataSource = new JREmptyDataSource(1);

		subReport.setDataSource(emptyDataSource);
		subReport1.setDataSource(emptyDataSource);
		subReport2.setDataSource(emptyDataSource);
		subReport3.setDataSource(emptyDataSource);
		subReport4.setDataSource(emptyDataSource);
		
		subReport6.setDataSource(emptyDataSource);
		subReport7.setDataSource(emptyDataSource);
		subReport8.setDataSource(emptyDataSource);
		subReport9.setDataSource(emptyDataSource);
		disbursementConfirmationReport.setDataSource(emptyDataSource);
		loanDetailsReport.setDataSource(emptyDataSource);
		preCloseLoanDetailsReport.setDataSource(emptyDataSource);
		
		report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));

		/* Basic Application Details */
		
		subReport.title(cmp.text(keysForContent.getString("applicationName")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));
		subReport.title(cmp.text(""));
		subReport.setDataSource(emptyDataSource);
		subReport1.title(getParticularsAndDetailsForScheduleA(keysForContent, custmrDataFields)).title(cmp.text(""));
		subReport1.setDataSource(emptyDataSource);
		subReport2.title(getBorrowerDetailsForScheduleA(keysForContent, custmrDataFields, payload1)).title(cmp.text(""));
		subReport2.setDataSource(emptyDataSource);
		subReport3.title(getLoanDetailsForScheduleA(keysForContent, custmrDataFields, cibilPayloadCoApp, productName)).title(cmp.text(""));
		subReport3.setDataSource(emptyDataSource);
		subReport4.title(getLoanAmortizationForScheduleA(keysForContent, custmrDataFields, cibilPayloadCoApp, noOfEPIs, firstEmi, emi)).title(cmp.text(""));
		subReport4.setDataSource(emptyDataSource);
		subReport6.title(getDetailChargesForScheduleA(keysForContent, cibilPayloadCoApp)).title(cmp.text(""));
		subReport6.setDataSource(emptyDataSource);
		disbursementConfirmationReport.title(cmp.text(keysForContent.getString("disbursementConfirmation")
				.replace("#SanctionedLoanAmount", loanNetSanctionAmount.toString())
				.replace("#LoanAmountInWords", loanNetSanctionAmountInWords))
				.setMarkup(Markup.HTML).setStyle(leftStyle));
		disbursementConfirmationReport.setDataSource(emptyDataSource);
		loanDetailsReport.title(getLoanDetailsTable(custmrDataFields, keysForContent)).title(cmp.text(""));
		loanDetailsReport.setDataSource(emptyDataSource);
		preCloseLoanDetailsReport.title(getPreCloseLoanDetailsTable(custmrDataFields, keysForContent)).title(cmp.text(""));
		preCloseLoanDetailsReport.setDataSource(emptyDataSource);
		subReport7.title(getKfsSummaryDetails(keysForContent, cibilPayloadCoApp, custmrDataFields, productName, totalInsurance, noOfEPIs, emi, totalInterest)).title(cmp.text(""));
		subReport7.setDataSource(emptyDataSource);	
		subReport8.title(cmp.text(keysForContent.getString("confirmation1")).setMarkup(Markup.HTML));
		subReport8.setDataSource(emptyDataSource);
//		subReport9.title(cmp.text(keysForContent.getString("confirmation2")).setMarkup(Markup.HTML));
//		subReport9.setDataSource(emptyDataSource);
		
		report
		    .setPageFormat(PageType.A4, PageOrientation.PORTRAIT)
		    .setPageMargin(DynamicReports.margin(30))
		    .pageFooter(
		    	    cmp.verticalList(Signatory(keysForContent))
		    	        .setFixedHeight(60)
		    	)
		    
//		    .pageFooter(Signatory(keysForContent))
		    .setDataSource(new JREmptyDataSource(1)) 
		   
		    .detail(
				    cmp.verticalList(
				    	cmp.subreport(subReport),	
				        cmp.subreport(subReport1),
				        cmp.subreport(subReport2),
				        cmp.subreport(subReport3),
				        cmp.subreport(subReport4),
				        cmp.subreport(subReport6),
						cmp.subreport(disbursementConfirmationReport),
						cmp.subreport(loanDetailsReport),
						cmp.subreport(preCloseLoanDetailsReport),
				        cmp.subreport(subReport7),
				        cmp.subreport(subReport8),
				        cmp.subreport(subReport9)
	
				    )
				);
		
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

	private TextFieldBuilder<String> createTextField(String label) {
		return cmp.text(label).setMarkup(Markup.HTML).setStyle(boldCenteredStyle);
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
			
			
		}
		System.out.println(ds2);
		return ds2;
	}

private ComponentBuilder<?, ?> createSingleHorizontalListForScheduleA(String Key) {
		
		HorizontalListBuilder horizontalList = cmp.horizontalList();

		 horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML)
			        .setStyle(stl.style(boldTextWithBorder).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER))
			        .setWidth(100));
		 
		return horizontalList;

	}

	private ComponentBuilder<?, ?> createTwoHorizontalListForScheduleAHeader(String Key, String value) {
		
		HorizontalListBuilder horizontalList = cmp.horizontalList();
		
		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(stl.style(boldTextWithBorder).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)).setWidth(40));
		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(stl.style(boldTextWithBorder).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)).setWidth(60));
	
		return horizontalList;
	}

	private ComponentBuilder<?, ?> getLoanDetailsTable(CustomerDataFields custmrDataFields, JSONObject keysForContent){
		VerticalListBuilder verticalList = cmp.verticalList();
		LoanDetails loanDtls = custmrDataFields.getLoanDetails();
		BigDecimal sanctionedLoanAmt = loanDtls.getSanctionedLoanAmount();

		Gson gsonObj = new Gson();

		for(CustomerDetails custDtl : custmrDataFields.getCustomerDetailsList()) {
			logger.debug("customer Type : " + custDtl.getCustomerType());
			if(custDtl.getCustomerType().equalsIgnoreCase("Applicant")) {
				applicantCustDtls = custDtl;
			}else if(custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
				coApplicantCustDtls = custDtl;
			}
		}

		CibilDetailsPayload cibilPayloadCoApp = null;
		int totalInsurance = 0;
		String processingFee = "0";
		for (CibilDetailsWrapper cibilDetailsWrapper : custmrDataFields.getCibilDetailsWrapperList()) {
			String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
			//		String customerType = applicant ? applicantCustId : coApplicantCustId;
			logger.debug("CreditDetailsPayload Payload : " + cibilPayloadCoApp);
			if (custId.equals(coApplicantCustId)) {
				cibilPayloadCoApp = gsonObj.fromJson(
						cibilDetailsWrapper.getCibilDetails().getPayloadColumn(), CibilDetailsPayload.class);
				totalInsurance = toFindSum(cibilPayloadCoApp.getInsuranceChargeJoint(),
						cibilPayloadCoApp.getInsuranceChargeMember(), cibilPayloadCoApp.getInsuranceChargeSpouse());
				processingFee = cibilPayloadCoApp.getProcessingFees() == null ? "0" : cibilPayloadCoApp.getProcessingFees();
			}
		}

		List<ExistingGLLoanDetails> existingLoanDetailsList = new ArrayList<>();
		if(null != custmrDataFields.getExistingGLLoanDetails()
				&& !custmrDataFields.getExistingGLLoanDetails().isEmpty()){
			existingLoanDetailsList = custmrDataFields.getExistingGLLoanDetails();
		}
		String totalPreClosureAmount = "0";
		for(ExistingGLLoanDetails existingGLLoanDetails : existingLoanDetailsList){
			if(existingGLLoanDetails.getOutstandingAmount() != null) {
				totalPreClosureAmount = String.valueOf(new BigDecimal(totalPreClosureAmount).add(existingGLLoanDetails.getOutstandingAmount()));
			}
		}

		String loanNetSanctionAmount =
				String.valueOf(sanctionedLoanAmt
						.subtract(new BigDecimal(processingFee))
						.subtract(new BigDecimal(totalInsurance))
						.subtract(new BigDecimal(totalPreClosureAmount)));



		Map<String, Integer> sanctionedLoanAmountRow = new LinkedHashMap<>();
		sanctionedLoanAmountRow.put(keysForContent.getString("sanctionedLoanAmountA"), 30);
		sanctionedLoanAmountRow.put(keysForContent.getString("inRupees").replace("<<amount>>", sanctionedLoanAmt.toString()), 30);
		verticalList.add(createHorizontalList(sanctionedLoanAmountRow));

		Map<String, Integer> lessRow = new LinkedHashMap<>();
		lessRow.put(keysForContent.getString("lessB"), 30);
		lessRow.put("", 30);
		verticalList.add(createHorizontalList(lessRow));

		Map<String, Integer> processingFeesRow = new LinkedHashMap<>();
		processingFeesRow.put(keysForContent.getString("processingFeesBullet"), 30);
		processingFeesRow.put(keysForContent.getString("inRupees").replace("<<amount>>",processingFee), 30);
		verticalList.add(createHorizontalList(processingFeesRow));

		Map<String, Integer> insPremiumRow = new LinkedHashMap<>();
		insPremiumRow.put(keysForContent.getString("insurancePremiumBullet"), 30);
		insPremiumRow.put(keysForContent.getString("inRupees").replace("<<amount>>",String.valueOf(totalInsurance)), 30);
		verticalList.add(createHorizontalList(insPremiumRow));

		Map<String, Integer> preClosureAmountRow = new LinkedHashMap<>();
		preClosureAmountRow.put(keysForContent.getString("preClosureAmountBullet"), 30);
		preClosureAmountRow.put(keysForContent.getString("inRupees").replace("<<amount>>",totalPreClosureAmount), 30);
		verticalList.add(createHorizontalList(preClosureAmountRow));

		Map<String, Integer> netAmountRow = new LinkedHashMap<>();
		netAmountRow.put(keysForContent.getString("loanNetSanctionAmount"), 30);
		netAmountRow.put(keysForContent.getString("inRupees").replace("<<amount>>",loanNetSanctionAmount), 30);
		verticalList.add(createHorizontalList(netAmountRow));

		return verticalList;
	}

	private ComponentBuilder<?, ?> getPreCloseLoanDetailsTable(CustomerDataFields custmrDataFields, JSONObject keysForContent){
		VerticalListBuilder verticalList = cmp.verticalList();
		LoanDetails loanDtls = custmrDataFields.getLoanDetails();
		BigDecimal sanctionedLoanAmt = loanDtls.getSanctionedLoanAmount();

		List<ExistingGLLoanDetails> existingLoanDetailsList = new ArrayList<>();
		if(null != custmrDataFields.getExistingGLLoanDetails()
				&& !custmrDataFields.getExistingGLLoanDetails().isEmpty()){
			existingLoanDetailsList = custmrDataFields.getExistingGLLoanDetails();
		}
		String totalPreClosureAmount = "0";
		for(ExistingGLLoanDetails existingGLLoanDetails : existingLoanDetailsList){
			if(existingGLLoanDetails.getOutstandingAmount() != null) {
				totalPreClosureAmount = String.valueOf(new BigDecimal(totalPreClosureAmount).add(existingGLLoanDetails.getOutstandingAmount()));
			}
		}

		Map<String, Integer> row = new LinkedHashMap<>();
		row.put(keysForContent.getString("preClosedLoanDetails"), 50);
		verticalList.add(createHorizontalList(row));

		Map<String, Integer> preClosedLoaDetailsHeaderRow = new LinkedHashMap<>();
		preClosedLoaDetailsHeaderRow.put(keysForContent.getString("LoanAccountnumber"), 30);
		preClosedLoaDetailsHeaderRow.put(keysForContent.getString("preClosureAmount"), 20);
		verticalList.add(createHorizontalList(preClosedLoaDetailsHeaderRow));

		for(int i=0; i<existingLoanDetailsList.size(); i++) {
			 ExistingGLLoanDetails existingGLLoanDetails = existingLoanDetailsList.get(i);

			Map<String, Integer> preClosedLoaDetailsRow = new LinkedHashMap<>();
			preClosedLoaDetailsRow.put(String.valueOf(i+1)+ ". " + existingGLLoanDetails.getExistingLoanId(), 30);
			preClosedLoaDetailsRow.put("Rs. " + String.valueOf(existingGLLoanDetails.getOutstandingAmount()), 20);
			verticalList.add(createHorizontalList(preClosedLoaDetailsRow));
		}

		Map<String, Integer> totalRow = new LinkedHashMap<>();
		totalRow.put(keysForContent.getString("Total"), 30);
		totalRow.put(keysForContent.getString("inRupees").replace("<<amount>>",totalPreClosureAmount), 20);
		verticalList.add(createHorizontalList(totalRow));

		return verticalList;

	}

    private ComponentBuilder<?, ?> Signatory1(JSONObject keysForContent) {
        VerticalListBuilder verticalList = cmp.verticalList();
        String blank = " .............";
        verticalList.add(cmp.horizontalList(
                        cmp.text(keysForContent.getString("applicantNameAndSign") + ": " + applicantName+" &"+blank)
                                .setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)),
                        cmp.text(keysForContent.getString("authorizedSignatory") + ": " + blank).setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT))
                )
        );
        verticalList.add(cmp.horizontalList(
                        cmp.text(keysForContent.getString("coapplicantNameAndSign") + ": " + coApplicantName+" &"+blank)
                                .setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT))
                )
        );
        return verticalList;
    }

    private ComponentBuilder<?, ?> Signatory(JSONObject keysForContent) {
        VerticalListBuilder verticalList = cmp.verticalList();
        String blank = " .........";
        verticalList.add(cmp.horizontalList(
                cmp.text(keysForContent.getString("applicantNameAndSign") + ": " + applicantName+" &"+blank)
                        .setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)))
        );
        verticalList.add(cmp.horizontalList(
                        cmp.text(keysForContent.getString("coapplicantNameAndSign") + ": " + coApplicantName+" &"+blank)
                                .setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT))
                )
        );
        verticalList.add(cmp.horizontalList(
                cmp.text(keysForContent.getString("authorizedSignatory") + ": " + blank).setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)))
        );

        return verticalList;
    }
	
	private ComponentBuilder<?, ?> createTwoHorizontalList(String Key, String value, ReportStyleBuilder style) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(style));
		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(style));

		return horizontalList;
	}
	private ComponentBuilder<?, ?> createTwoHorizontalListForScheduleA(String Key, String value) {
		
		HorizontalListBuilder horizontalList = cmp.horizontalList();
		
		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(40));
		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(60));

		return horizontalList;

	}
// For Schedule A	
private ComponentBuilder<?, ?> getParticularsAndDetailsForScheduleA(JSONObject keysForContent, CustomerDataFields custmrDataFields) {
		
		VerticalListBuilder verticalList = cmp.verticalList();

		verticalList.add(createTwoHorizontalListForScheduleAHeader(keysForContent.getString("particulars"), keysForContent.getString("details")));
		verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("agreementDate"), CommonUtils.getCurDate()));
		verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("agreementPlace"), custmrDataFields.getApplicationMaster().getBranchName()));
		verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("branchDetails"), custmrDataFields.getApplicationMaster().getBranchName()));
		verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("loanAccountNumber"), custmrDataFields.getLoanDetails().getT24LoanId()));
		
		return verticalList;	
	}
	
private ComponentBuilder<?, ?> getBorrowerDetailsForScheduleA(JSONObject keysForContent, CustomerDataFields custmrDataFields, CustomerDetailsPayload payload1) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	JSONObject addressDetailsObj = getAllAddressDeatils(custmrDataFields);
	String fromAddressAppnt = addressDetailsObj.getString("presentAddressApplicant");
	String fromAddressCoAppnt = addressDetailsObj.getString("presentAddressCoApplicant");

	verticalList.add(createSingleHorizontalListForScheduleA(keysForContent.getString("borrowerDetails")).setStyle(boldCenteredStyle));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("borrowerName"), applicantName));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("borrowerAge"), payload1.getAge()));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("borrowerAddress"), fromAddressAppnt));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("coapplicantName"), coApplicantName));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("coapplicantAddress"), fromAddressCoAppnt));
	
	return verticalList;	
}

private ComponentBuilder<?, ?> getLoanDetailsForScheduleA(JSONObject keysForContent,  CustomerDataFields custmrDataFields, CibilDetailsPayload cibilPayloadCoApp, String productDetail) {
	VerticalListBuilder verticalList = cmp.verticalList();
	try {
		Gson gsonObj = new Gson();
		LoanDetailsPayload payload = gsonObj.fromJson(custmrDataFields.getLoanDetails().getPayloadColumn(),
				LoanDetailsPayload.class);
		logger.debug("LoanDetailsPayload : " + payload);
		
		String interest = (cibilPayloadCoApp.getRoi() == null) ? "" : cibilPayloadCoApp.getRoi().toString();
		logger.debug("Interest rate fro loan details2:"+ interest);
		
		verticalList.add(createSingleHorizontalListForScheduleA(keysForContent.getString("loanDetails")).setStyle(boldCenteredStyle));
		verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("loanType"), productDetail));
		verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("loanAmount"), "Rs."+CommonUtils.amountFormat(String.valueOf(custmrDataFields.getLoanDetails().getLoanAmount()))+"/-")); 
		verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("loanInterestRate"), CommonUtils.amountFormat(interest) + " %"));
		verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("loanPurpose"), payload.getLoanPurpose()));
				
	} catch (Exception e) {
		logger.error("error - getLoanDetails");
		logger.error(e.getMessage());
	}
	logger.debug("LoanDetails added");

	return verticalList;	
}

private ComponentBuilder<?, ?> getLoanAmortizationForScheduleA(JSONObject keysForContent, CustomerDataFields custmrDataFields, CibilDetailsPayload cibilPayloadCoApp, int noOfEPIs, String firstEmi, String emi) {
	
	VerticalListBuilder verticalList = cmp.verticalList();

	verticalList.add(createSingleHorizontalListForScheduleA(keysForContent.getString("loanAmortization")).setStyle(boldCenteredStyle));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("tenure"), cibilPayloadCoApp.getFinalTenure()));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("installments"), String.valueOf(noOfEPIs)));
//	
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("installmentAmount"),
			keysForContent.getString("firstInstallment")+ ": "+ "Rs." +CommonUtils.amountFormat(firstEmi) +"/-" + "<br>" + keysForContent.getString("equatedInstallment")+": " +"Rs."+CommonUtils.amountFormat(emi) +"/-")); 
			
	//equatedInstallment
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("repaymentFrequency"), cibilPayloadCoApp.getRepaymentFrequency()));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("repaymentMode"), "Cash/Cashless")); //Cash/Cashless
	
	return verticalList;	
}

private ComponentBuilder<?, ?> getDetailChargesForScheduleA(JSONObject keysForContent, CibilDetailsPayload cibilPayloadCoApp) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createSingleHorizontalListForScheduleA(keysForContent.getString("chargesDetails")).setStyle(boldCenteredStyle));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("processingCharges"), "Rs."+CommonUtils.amountFormat(cibilPayloadCoApp.getProcessingFees())+ "/-"));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("stampDutyCharges"), "Rs."+CommonUtils.amountFormat("0")+ "/-")); // as of now  - 0 //conditionally fertch based on state for NESL 
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("latePaymentCharges"), "NA"));
	
	return verticalList;	
}

private ComponentBuilder<?, ?> getKfsSummaryDetails(JSONObject keysForContent, CibilDetailsPayload cibilPayloadCoApp, CustomerDataFields custmrDataFields, String productName, int totalInsurance, int noOfEPIs, String emi, BigDecimal totalInterest) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	String interest = String.valueOf(cibilPayloadCoApp.getRoi() == null ? "": cibilPayloadCoApp.getRoi().toString());
	String apr = String.valueOf(cibilPayloadCoApp.getEir() == null ? "": cibilPayloadCoApp.getEir().toString()); 
	
	BigDecimal sactionAmtDb = custmrDataFields.getLoanDetails().getSanctionedLoanAmount();
	String sactionAmt = String.valueOf(sactionAmtDb == null ? "" : sactionAmtDb.toPlainString());
	
	verticalList.add(createSingleHorizontalListForScheduleA(keysForContent.getString("module2")).setStyle(boldCenteredStyle));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("loanProductName"), productName));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("loanAmountSanctioned"), "Rs. "+ CommonUtils.amountFormat(sactionAmt) + "/-"));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("repaymentFrequency"), cibilPayloadCoApp.getRepaymentFrequency()));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("numberOfInstallments"), String.valueOf(noOfEPIs)));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("installmentAmoun"),  "Rs. "+ CommonUtils.amountFormat(emi)+ "/-"));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("rateOfInterest"), CommonUtils.amountFormat(interest) + " %"));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("totalInterest"),  "Rs. "+ CommonUtils.amountFormat(String.valueOf(totalInterest))+ "/-"));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("processingFees"),"Rs. "+ CommonUtils.amountFormat(cibilPayloadCoApp.getProcessingFees())+ "/-"));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("insurancePremium"), "Rs. " + CommonUtils.amountFormat(String.valueOf(totalInsurance))+ "/-"));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("stampDutyCharges"), "Rs. "+ CommonUtils.amountFormat("0")+ "/-"));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("latePaymentCharges2"), "NA"));
	verticalList.add(createTwoHorizontalListForScheduleA(keysForContent.getString("annualPercentageRate"), apr));
	
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
 			
 			for(AddressDetails addr : req.getAddressDetailsWrapperList().get(0).getAddressDetailsList()) {
 				if(addr.getAddressType().equalsIgnoreCase("Personal") && 
 						(String.valueOf(addr.getCustDtlId()).equals(applicantCustId))){
 					AddressDetailsPayload applicantPayload  = gsonObj.fromJson(addr.getPayloadColumn(), AddressDetailsPayload.class);
 					applicantAddrPayLoadLst = applicantPayload.getAddressList();
 					logger.debug("PersonalAddLstApplicant :" + applicantAddrPayLoadLst);				
 				}else if(addr.getAddressType().equalsIgnoreCase("Personal") && 
 						(String.valueOf(addr.getCustDtlId()).equals(coApplicantCustId))){
 					AddressDetailsPayload coApplicantPayload  = gsonObj.fromJson(addr.getPayloadColumn(), AddressDetailsPayload.class);
 					coApplicantAddrPayLoadLst = coApplicantPayload.getAddressList();
 					logger.debug("PersonalAddLstCo-aaplicant :" + coApplicantAddrPayLoadLst);	
 				}
 				
 				//occupation Address
 				if(addr.getAddressType().equalsIgnoreCase("Occupation") && 
 						(String.valueOf(addr.getCustDtlId()).equals(applicantCustId))){
 					AddressDetailsPayload applicantPayload  = gsonObj.fromJson(addr.getPayloadColumn(), AddressDetailsPayload.class);
 					applicantOccupnAddrPayLoadLst = applicantPayload.getAddressList();
 					logger.debug("PersonalAddLstApplicant - Occupation :"+ applicantOccupnAddrPayLoadLst);
 				} else if(addr.getAddressType().equalsIgnoreCase("Occupation") && 
 						(String.valueOf(addr.getCustDtlId()).equals(coApplicantCustId))){
 					AddressDetailsPayload coApplicantPayload  = gsonObj.fromJson(addr.getPayloadColumn(), AddressDetailsPayload.class);
 					coApplicantOccupnAddrPayLoadLst = coApplicantPayload.getAddressList();
 					logger.debug("PersonalAddLstCoApplicant - Occupation :"+ coApplicantOccupnAddrPayLoadLst);
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
 			
 			//Occupation Address
 			String occpnAddrApplicant = "";
 			String occpnAddrCoApplicant = "";
 			
 			
 			//Applicant Address
 			for (Address addr : applicantAddrPayLoadLst) {
 				if (addr.getAddressType().equalsIgnoreCase("present")) {
 					
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
 				
 			//Co-Applicant Address
 			if(coApplicantAddrPayLoadLst !=null) {
 				for(Address addr: coApplicantAddrPayLoadLst){
 					if(addr.getAddressType().equalsIgnoreCase("present")) {
 						presentAddressCoApplicant = getFullAddress(addr);
 						logger.debug("Present Address - Co-applicant :" + presentAddressCoApplicant);
 						presentResidenceOwnershipCo = addr.getResidenceOwnership();
 						presentAddressYearsCo = addr.getResidenceAddressSince();
 						presntCityYearsCo = addr.getResidenceCitySince();
 						presentResidenceAddressProofCo = addr.getCurrentAddressProof();
 						presentResidenceTypeCo = addr.getHouseType();
 					}else if(addr.getAddressType().equalsIgnoreCase("Permanent")) {
 						permanetAddressCoApplicant = getFullAddress(addr);
 						logger.debug("permanent Address - Co-applicant. :" + permanetAddressCoApplicant);
 					}
 				}
 			}
 				
 			//Occupation Address
 			Address ocupnAddr = null;
 			Address ocupnAddrCo = null;
 			if (applicantOccupnAddrPayLoadLst != null && !applicantOccupnAddrPayLoadLst.isEmpty()) {
 			    ocupnAddr = applicantOccupnAddrPayLoadLst.get(0);
 			}
 			if (coApplicantOccupnAddrPayLoadLst != null && !coApplicantOccupnAddrPayLoadLst.isEmpty()) {
 				ocupnAddrCo = coApplicantOccupnAddrPayLoadLst.get(0);
 			}
 				occpnAddrApplicant = getFullAddress(ocupnAddr);
 				occpnAddrCoApplicant = getFullAddress(ocupnAddrCo);
 			logger.debug("Ocupation Address Applicnt : " + occpnAddrApplicant);	
 			logger.debug("Ocupation Address Co-Applicnt : " + occpnAddrCoApplicant);
 			
 				
 			jsnObj = new JSONObject();
 			jsnObj.put("presentAddressApplicant", presentAddressApplicant);
 			jsnObj.put("permanetAddressApplicant", permanetAddressApplicant);
 			jsnObj.put("presentAddressCoApplicant", presentAddressCoApplicant);
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
 			
 			//Occupation Address
 			jsnObj.put("occpnAddrApplicant", occpnAddrApplicant);
 			jsnObj.put("occpnAddrCoApplicant", occpnAddrCoApplicant);
 			
 			
 		
 		}catch (Exception e) {
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
 	    String[] addressArr = {
 	        address.getAddressLine1(),
 	        address.getAddressLine2(),
 	        address.getAddressLine3(),
 	        address.getArea(),
 	        address.getLandMark(),
 	        address.getDistrict(),
 	        address.getCity(),
 	        address.getState(),
 	        address.getCountry(),
 	        address.getPinCode()
 	        
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


	public static String toCurrencyWords(BigDecimal amount) {
		if (amount == null) {
			return "";
		}

		final String[] UNITS = {
				"", "One", "Two", "Three", "Four", "Five", "Six",
				"Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
				"Thirteen", "Fourteen", "Fifteen", "Sixteen",
				"Seventeen", "Eighteen", "Nineteen"
		};

		final String[] TENS = {
				"", "", "Twenty", "Thirty", "Forty", "Fifty",
				"Sixty", "Seventy", "Eighty", "Ninety"
		};
		long rupees = amount.longValue();
		int paise = amount
				.subtract(BigDecimal.valueOf(rupees))
				.movePointRight(2)
				.intValue();
		StringBuilder result = new StringBuilder();
		// Rupees
		if (rupees == 0) {
			result.append("Zero Rupees");
		} else {
			result.append(convertToUnits(rupees, UNITS, TENS)).append(" Rupees");
		}
		// Paise
		if (paise > 0) {
			result.append(" and ")
					.append(convertToUnits(paise, UNITS, TENS))
					.append(" Paise");
		}
		result.append(" Only");
		String cleaned = result.toString().replaceAll("\\s+", " ").trim();
		return Character.toUpperCase(cleaned.charAt(0)) + cleaned.substring(1);
	}

	private static String convertToUnits(long number, String[] UNITS, String[] TENS) {
		StringBuilder result = new StringBuilder();

		if (number >= 10000000) {
			result.append(convertToUnits(number / 10000000, UNITS, TENS)).append(" Crore ");
			number %= 10000000;
		}

		if (number >= 100000) {
			result.append(convertToUnits(number / 100000, UNITS, TENS)).append(" Lakh ");
			number %= 100000;
		}

		if (number >= 1000) {
			result.append(convertToUnits(number / 1000, UNITS, TENS)).append(" Thousand ");
			number %= 1000;
		}

		if (number >= 100) {
			result.append(UNITS[(int) (number / 100)]).append(" Hundred ");
			number %= 100;
		}

		if (number >= 20) {
			result.append(TENS[(int) (number / 10)]).append(" ");
			number %= 10;
		}

		if (number > 0) {
			result.append(UNITS[(int) number]).append(" ");
		}
		return result.toString();
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

	private int toFindDifference(String val1, String val2) {
		try {
			int num1 = Integer.parseInt(Optional.ofNullable(val1).orElse("0").replaceAll(",", ""));
			int num2 = Integer.parseInt(Optional.ofNullable(val2).orElse("0").replaceAll(",", ""));
			return num1 - num2;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private ComponentBuilder<?, ?> createHorizontalList(Map<String, Integer> columns) {
		HorizontalListBuilder horizontalList = cmp.horizontalList();
		if (columns == null || columns.isEmpty()) {
			return horizontalList;
		}
		for (Map.Entry<String, Integer> entry : columns.entrySet()) {
			horizontalList.add(
					cmp.text(entry.getKey() == null ? "" : entry.getKey())
							.setMarkup(Markup.HTML)
							.setStyle(borderedStyle)
							.setWidth(entry.getValue())
			);
		}
		return horizontalList;
	}
}
