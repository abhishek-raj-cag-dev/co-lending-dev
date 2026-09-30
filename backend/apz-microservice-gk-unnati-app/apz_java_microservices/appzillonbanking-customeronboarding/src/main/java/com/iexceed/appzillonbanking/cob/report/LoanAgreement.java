package com.iexceed.appzillonbanking.cob.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedCDHLead;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ProductCode;
import com.iexceed.appzillonbanking.cob.core.utils.WorkflowStatus;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;

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
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;

public class LoanAgreement {

	private static final Logger logger = LogManager.getLogger(LoanAgreement.class);
	private StyleBuilder borderedStyle, boldText, boldCenteredStyle, boldTextWithBorder, boldLeftStyle, rightStyle,
			leftStyle, leftSpacedStyle, marginTop, boldCenteredStyleWithMargin;

	static String space = "\u00a0\u00a0\u00a0";
	private String applicantName = "";
	private String coApplicantName = "";
	private String bmId = "-";


	private String applicantCustId = "";
	private String coApplicantCustId = "";
	private CustomerDetails applicantCustDtls = null;
	private CustomerDetails coApplicantCustDtls = null;
	private String applicantFirstname = "";
	private String applicantLastname = "";
	private String coApplicantFirstname = "";
	private String coApplicantLastname = "";

	private String kmId = "-";

	private String bmName = "-";
	private String kmName = "-";

	String appltGender = "";
	String coAppltGender = "";

	private String kmSubmDateStr = "";
	private String bmSubmDateStr = "";

	int width30 = 30;
	int width70 = 70;
	int width80 = 80;
	int width20 = 20;
	int width50 = 50;
	int width60 = 60;
	int width40 = 40;


	public LoanAgreement() {

		borderedStyle = stl.style(stl.penThin()).setPadding(5);
		boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
		boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        boldCenteredStyleWithMargin = stl.style().setTopPadding(800).bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		boldText = stl.style().bold();
		boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

		rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
		leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

		leftSpacedStyle = stl.style()
				.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)
				.setBottomPadding(10);

		marginTop = stl.style().setTopPadding(100);

	}

	public String generatePdfForDbkit(JSONObject keysForContent, String filePath, CustomerDataFields custmrDataFields, String language)
			throws DRException, IOException {
		StyleBuilder tempStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);

		JasperReportBuilder report = new JasperReportBuilder();

		StyleBuilder headerStyle = stl.style().setFontSize(20)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
		StyleBuilder style = stl.style().setBackgroundColor(Color.GRAY).setFontSize(20)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

		StyleBuilder style1 = stl.style().setBackgroundColor(Color.GRAY).setFontSize(10)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

		JasperReportBuilder subReport = new JasperReportBuilder();

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
		JasperReportBuilder subReport19 = new JasperReportBuilder();
		JasperReportBuilder subReport20 = new JasperReportBuilder();
		JasperReportBuilder subReport21 = new JasperReportBuilder();
		JasperReportBuilder loanAcountNumerReport = new JasperReportBuilder();

		JRDataSource emptyDataSource = new JREmptyDataSource(1);

		subReport1.setDataSource(emptyDataSource);
		subReport2.setDataSource(emptyDataSource);
		subReport3.setDataSource(emptyDataSource);
		subReport4.setDataSource(emptyDataSource);
		subReport5.setDataSource(emptyDataSource);
		subReport6.setDataSource(emptyDataSource);
		subReport7.setDataSource(emptyDataSource);
		subReport8.setDataSource(emptyDataSource);
		subReport9.setDataSource(emptyDataSource);
		subReport10.setDataSource(emptyDataSource);
		subReport11.setDataSource(emptyDataSource);
		subReport12.setDataSource(emptyDataSource);
		subReport13.setDataSource(emptyDataSource);
		subReport14.setDataSource(emptyDataSource);
		subReport15.setDataSource(emptyDataSource);
		subReport16.setDataSource(emptyDataSource);
		subReport17.setDataSource(emptyDataSource);
		subReport18.setDataSource(emptyDataSource);
		subReport19.setDataSource(emptyDataSource);
		subReport20.setDataSource(emptyDataSource);
		subReport21.setDataSource(emptyDataSource);

		CustomerDetailsPayload payload1 = null;
		CustomerDetailsPayload payload2 = null;
		Gson gsonObj = new Gson();
		for (CustomerDetails custDtl : custmrDataFields.getCustomerDetailsList()) {
			logger.debug("customer Type : " + custDtl.getCustomerType());
			if (custDtl.getCustomerType().equalsIgnoreCase("Applicant")) {
//				applicantCustId = String.valueOf(custDtl.getCustDtlId());
				applicantName = custDtl.getCustomerName();
				logger.debug("custApplicantPayload :" + payload1);
			} else if (custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
//				coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
				coApplicantName = custDtl.getCustomerName();
//				logger.debug("coApplicantCustId : " + coApplicantCustId);
				payload2 = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
				logger.debug("custCo-ApplicantPayload :" + payload2);

			}
		}

		if (Constants.UNNATI_PRODUCT_CODE
				.equals(custmrDataFields.getApplicationMaster().getProductCode())
				|| Constants.OPENMARKET_LOAN_PRODUCT_CODE
				.equals(custmrDataFields.getApplicationMaster().getProductCode())) {
			logger.info("Unnati application");

			String previousWorkflowStatus = null;
			for (ApplicationWorkflow appnWorkflow : custmrDataFields.getApplicationWorkflowList()) {
				String currentStatus = appnWorkflow.getApplicationStatus();
				if (Constants.APPROVED.equalsIgnoreCase(appnWorkflow.getApplicationStatus())) {
					if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(previousWorkflowStatus)) {
						bmId = appnWorkflow.getCreatedBy();
						LocalDateTime bmSubmDate = appnWorkflow.getCreateTs();
					}
				}
				previousWorkflowStatus = currentStatus;
			}

		} else {
			logger.info("Not an Unnati application");
		}

		report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));

		subReport1.title(cmp.text(keysForContent.getString("subApplicationName2")).setMarkup(Markup.HTML)
				.setStyle(boldCenteredStyle.setFontSize(14).underline()));
		subReport1.setDataSource(emptyDataSource);

		loanAcountNumerReport.title(getLoanAccountNumber(custmrDataFields, keysForContent)).title(cmp.text(""));

		subReport2.title(cmp.text(keysForContent.getString("content")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport2.setDataSource(emptyDataSource);
		subReport2.title(cmp.text(keysForContent.getString("contentA")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport2.setDataSource(emptyDataSource);
		subReport3.title(cmp.text(keysForContent.getString("contentC")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport3.setDataSource(emptyDataSource);
		subReport4.title(cmp.text(keysForContent.getString("content1")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport4.setDataSource(emptyDataSource);
		subReport5.title(cmp.text(keysForContent.getString("content2")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport5.setDataSource(emptyDataSource);
		subReport6.title(cmp.text(keysForContent.getString("content3")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport6.setDataSource(emptyDataSource);
		subReport7.title(cmp.text(keysForContent.getString("content4")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport7.setDataSource(emptyDataSource);
		subReport8.title(cmp.text(keysForContent.getString("content5")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport8.setDataSource(emptyDataSource);
		subReport9.title(cmp.text(keysForContent.getString("content6")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport9.setDataSource(emptyDataSource);
		subReport10.title(cmp.text(keysForContent.getString("content7")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport10.setDataSource(emptyDataSource);
		subReport11.title(cmp.text(keysForContent.getString("content8")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport11.setDataSource(emptyDataSource);
		subReport12.title(cmp.text(keysForContent.getString("content9")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport12.setDataSource(emptyDataSource);
		subReport13.title(cmp.text(keysForContent.getString("content10")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport13.setDataSource(emptyDataSource);
		subReport14.title(cmp.text(keysForContent.getString("content11")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport14.setDataSource(emptyDataSource);
		subReport15.title(cmp.text(keysForContent.getString("content12")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport15.setDataSource(emptyDataSource);
		subReport16.title(cmp.text(keysForContent.getString("content13")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport16.setDataSource(emptyDataSource);
		subReport17.title(cmp.text(keysForContent.getString("content14")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport17.setDataSource(emptyDataSource);
		subReport18.title(cmp.text(keysForContent.getString("content15")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport18.setDataSource(emptyDataSource);
		subReport19.title(cmp.text(keysForContent.getString("content16")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content17")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.setDataSource(emptyDataSource);
		subReport20.title(cmp.text(keysForContent.getString("content18")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content19")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content20")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content21")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content22")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content23")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content24")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content25")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content26")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content27")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content28")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content29")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content30")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content31")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("loanDeclaration")).setMarkup(Markup.HTML)
				.setStyle(boldCenteredStyle.setFontSize(12).underline()));
		subReport20.title(
				cmp.text(keysForContent.getString("loanDeclarationValue")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(""));
		subReport20.setDataSource(emptyDataSource);
		subReport21.title(postdeclaration(keysForContent));
		subReport21.setDataSource(emptyDataSource);

		report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30))

				.pageFooter(
						cmp.verticalList(Signatory(keysForContent))
								.setFixedHeight(90)
								.setStyle(stl.style().setTopPadding(10))
				)

				.setDataSource(new JREmptyDataSource(1))
				.detail(cmp.verticalList(cmp.subreport(subReport1), cmp.subreport(loanAcountNumerReport),cmp.subreport(subReport2),
						cmp.subreport(subReport3), cmp.subreport(subReport4), cmp.subreport(subReport5),
						cmp.subreport(subReport6), cmp.subreport(subReport7), cmp.subreport(subReport8),
						cmp.subreport(subReport9), cmp.subreport(subReport10), cmp.subreport(subReport11),
						cmp.subreport(subReport12), cmp.subreport(subReport13), cmp.subreport(subReport14),
						cmp.subreport(subReport15), cmp.subreport(subReport16), cmp.subreport(subReport17),
						cmp.subreport(subReport18), cmp.subreport(subReport19), cmp.subreport(subReport20),
						cmp.subreport(subReport21)));

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

	private static final String[] units = {
			"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
			"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
			"Sixteen", "Seventeen", "Eighteen", "Nineteen"
	};

	private static final String[] tens = {
			"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
	};

	private static String convertNumber(long number) {
		if (number < 20) return units[(int) number];
		if (number < 100) return tens[(int) number / 10] + " " + units[(int) number % 10];
		if (number < 1000) return units[(int) number / 100] + " Hundred " + convertNumber(number % 100);
		if (number < 100000) return convertNumber(number / 1000) + " Thousand " + convertNumber(number % 1000);
		if (number < 10000000) return convertNumber(number / 100000) + " Lakh " + convertNumber(number % 100000);
		return convertNumber(number / 10000000) + " Crore " + convertNumber(number % 10000000);
	}
	public static String convert(long number) {
		if (number == 0) return "Zero";
		return convertNumber(number).trim();
	}

	private String productName = "";
	// @author Abhishek.Raj.CAG
	public String generatePdfForDbkitAdd(JSONObject keysForContent, String filePath, CustomerDataFields custmrDataFields, String language, String productName1, String sactionedDateStr, String kmIdFromCdh, String kmUserNameFromCdh, String bmIdFromCdh, String bmUserNameFromCdh,List<RepaymentSchedule> repaymentList)
			throws DRException, IOException {
		StyleBuilder tempStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);

		productName = productName1;
		String existingLoanName = "GL.GRM.UNNATI.LN";
		JasperReportBuilder report = new JasperReportBuilder();

		StyleBuilder headerStyle = stl.style().setFontSize(20)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
		StyleBuilder style = stl.style().setBackgroundColor(Color.GRAY).setFontSize(20)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

		StyleBuilder style1 = stl.style().setBackgroundColor(Color.GRAY).setFontSize(10)
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

		JasperReportBuilder subReport = new JasperReportBuilder();

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
		JasperReportBuilder subReport19 = new JasperReportBuilder();
		JasperReportBuilder subReport20 = new JasperReportBuilder();
		JasperReportBuilder subReport21 = new JasperReportBuilder();
		JasperReportBuilder subReport22 = new JasperReportBuilder();
		JasperReportBuilder subReport23 = new JasperReportBuilder();
		JasperReportBuilder subReport24 = new JasperReportBuilder();
		JasperReportBuilder subReport25 = new JasperReportBuilder();
		JasperReportBuilder subReport26 = new JasperReportBuilder();
		JasperReportBuilder subReport27 = new JasperReportBuilder();
		JasperReportBuilder subReport28 = new JasperReportBuilder();
		JasperReportBuilder subReport29 = new JasperReportBuilder();
		JasperReportBuilder subReport30 = new JasperReportBuilder();
		JasperReportBuilder subReport31 = new JasperReportBuilder();
		JasperReportBuilder subReport32 = new JasperReportBuilder();
		JasperReportBuilder subReport33 = new JasperReportBuilder();
		JasperReportBuilder subReport34 = new JasperReportBuilder();
		JasperReportBuilder subReport35 = new JasperReportBuilder();
		JasperReportBuilder subReport36 = new JasperReportBuilder();
		JasperReportBuilder subReport37 = new JasperReportBuilder();
		JasperReportBuilder loanAcountNumerReport = new JasperReportBuilder();

		JRDataSource emptyDataSource = new JREmptyDataSource(1);

		subReport1.setDataSource(emptyDataSource);
		subReport2.setDataSource(emptyDataSource);
		subReport3.setDataSource(emptyDataSource);
		subReport4.setDataSource(emptyDataSource);
		subReport5.setDataSource(emptyDataSource);
		subReport6.setDataSource(emptyDataSource);
		subReport7.setDataSource(emptyDataSource);
		subReport8.setDataSource(emptyDataSource);
		subReport9.setDataSource(emptyDataSource);
		subReport10.setDataSource(emptyDataSource);
		subReport11.setDataSource(emptyDataSource);
		subReport12.setDataSource(emptyDataSource);
		subReport13.setDataSource(emptyDataSource);
		subReport14.setDataSource(emptyDataSource);
		subReport15.setDataSource(emptyDataSource);
		subReport16.setDataSource(emptyDataSource);
		subReport17.setDataSource(emptyDataSource);
		subReport18.setDataSource(emptyDataSource);
		subReport19.setDataSource(emptyDataSource);
		subReport20.setDataSource(emptyDataSource);
		subReport21.setDataSource(emptyDataSource);
		subReport22.setDataSource(emptyDataSource);
		subReport23.setDataSource(emptyDataSource);
		subReport24.setDataSource(emptyDataSource);
		subReport25.setDataSource(emptyDataSource);
		subReport26.setDataSource(emptyDataSource);
		subReport27.setDataSource(emptyDataSource);
		subReport28.setDataSource(emptyDataSource);
		subReport29.setDataSource(emptyDataSource);
		subReport30.setDataSource(emptyDataSource);
		subReport31.setDataSource(emptyDataSource);
		subReport32.setDataSource(emptyDataSource);
		subReport33.setDataSource(emptyDataSource);
		subReport34.setDataSource(emptyDataSource);
		subReport35.setDataSource(emptyDataSource);
		subReport36.setDataSource(emptyDataSource);
		subReport37.setDataSource(emptyDataSource);

		CustomerDetailsPayload payload1 = null;
		CustomerDetailsPayload payload2 = null;
		Gson gsonObj = new Gson();
//		for (CustomerDetails custDtl : custmrDataFields.getCustomerDetailsList()) {
//			logger.debug("customer Type : " + custDtl.getCustomerType());
//			if (custDtl.getCustomerType().equalsIgnoreCase("Applicant")) {
//				applicantName = custDtl.getCustomerName();
//				logger.debug("custApplicantPayload :" + payload1);
//			} else if (custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
//				coApplicantName = custDtl.getCustomerName();
//				payload2 = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
//				logger.debug("custCo-ApplicantPayload :" + payload2);
//
//			}
//		}



		for (CustomerDetails custDtl : custmrDataFields.getCustomerDetailsList()) {
			logger.debug("customer Type : " + custDtl.getCustomerType());
			if (custDtl.getCustomerType().equalsIgnoreCase(Constants.APPLICANT)) {
				applicantCustId = String.valueOf(custDtl.getCustDtlId());
				logger.debug("applicantCustId : " + applicantCustId);
				applicantName = custDtl.getCustomerName();
				payload1 = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
				applicantFirstname = payload1.getFirstName();
				applicantLastname = payload1.getLastName();
				logger.debug("custApplicantPayload :" + payload1);
				applicantCustDtls = custDtl;
				appltGender = payload1.getGender();
			} else if (custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
				coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
				coApplicantName = custDtl.getCustomerName();
				logger.debug("coApplicantCustId : " + coApplicantCustId);
				payload2 = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
				coApplicantFirstname = payload2.getFirstName();
				coApplicantLastname = payload2.getLastName();
				logger.debug("custCo-ApplicantPayload :" + payload2);
				coAppltGender = payload2.getGender();
				coApplicantCustDtls = custDtl;
			}
		}

		if (
			// @author Abhishek.Raj.CAG
				Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
						.equals(custmrDataFields.getApplicationMaster().getProductCode())
						|| Constants.UNNATI_EMERGENCY_PRODUCT_CODE
						.equals(custmrDataFields.getApplicationMaster().getProductCode())
						|| Constants.FAMILY_WELFARE_PRODUCT_CODE
						.equals(custmrDataFields.getApplicationMaster().getProductCode())
			//----END----
		) {
			logger.info("Unnati application");

			String previousWorkflowStatus = null;
			for (ApplicationWorkflow appnWorkflow : custmrDataFields.getApplicationWorkflowList()) {
				String currentStatus = appnWorkflow.getApplicationStatus();
				if (Constants.APPROVED.equalsIgnoreCase(appnWorkflow.getApplicationStatus())) {
					if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(previousWorkflowStatus)) {
						bmId = appnWorkflow.getCreatedBy();
						LocalDateTime bmSubmDate = appnWorkflow.getCreateTs();
					}
				}
				previousWorkflowStatus = currentStatus;
			}

		} else {
			logger.info("Not an Unnati application");
		}

		report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));

//		subReport1.title(cmp.text(keysForContent.getString("subApplicationName2")).setMarkup(Markup.HTML)
//				.setStyle(boldCenteredStyle.setFontSize(14).underline()));

		subReport1.title(cmp.text(keysForContent.getString("Heading")).setMarkup(Markup.HTML)
				.setStyle(boldCenteredStyle.setFontSize(14).underline()));
		subReport1.setDataSource(emptyDataSource);
		loanAcountNumerReport.title(getLoanAccountNumber(custmrDataFields, keysForContent)).title(cmp.text(""));


		subReport2.title(cmp.text(keysForContent.getString("content")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport2.setDataSource(emptyDataSource);
		subReport2.title(cmp.text(keysForContent.getString("content1")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle));
		subReport2.setDataSource(emptyDataSource);
		subReport3.title(cmp.text(keysForContent.getString("content2")).setMarkup(Markup.HTML).setStyle(leftSpacedStyle));
		subReport3.setDataSource(emptyDataSource);
		subReport4.title(cmp.text(keysForContent.getString("content4")).setMarkup(Markup.HTML).setStyle(leftSpacedStyle));
		subReport4.setDataSource(emptyDataSource);
		subReport5.title(cmp.text(keysForContent.getString("content5")).setMarkup(Markup.HTML).setStyle(leftSpacedStyle));
		subReport5.setDataSource(emptyDataSource);
		subReport6.title(cmp.text(keysForContent.getString("content6")).setMarkup(Markup.HTML).setStyle(leftSpacedStyle));
		subReport6.setDataSource(emptyDataSource);
		subReport7.title(cmp.text(keysForContent.getString("content7")).setMarkup(Markup.HTML).setStyle(leftSpacedStyle));
		subReport7.setDataSource(emptyDataSource);
		subReport8.title(cmp.text(keysForContent.getString("content8")).setMarkup(Markup.HTML).setStyle(leftSpacedStyle));
		subReport8.setDataSource(emptyDataSource);
		subReport9.title(cmp.text(keysForContent.getString("content9")).setMarkup(Markup.HTML).setStyle(leftStyle).setStyle(boldText));
		subReport9.setDataSource(emptyDataSource);
		subReport10.title(cmp.text(keysForContent.getString("content9a")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport10.setDataSource(emptyDataSource);
		subReport11.title(cmp.text(keysForContent.getString("content9b")).setMarkup(Markup.HTML).setStyle(leftSpacedStyle));
		subReport11.setDataSource(emptyDataSource);
		subReport12.title(cmp.text(keysForContent.getString("content10")).setMarkup(Markup.HTML).setStyle(leftStyle).setStyle(boldText));
		subReport12.setDataSource(emptyDataSource);
		subReport13.title(cmp.text(keysForContent.getString("content10a")).setMarkup(Markup.HTML).setStyle(leftSpacedStyle));
		subReport13.setDataSource(emptyDataSource);
		subReport14.title(cmp.text(keysForContent.getString("content11")).setMarkup(Markup.HTML).setStyle(leftStyle).setStyle(boldText));
		subReport14.setDataSource(emptyDataSource);
		subReport15.title(cmp.text(keysForContent.getString("content11a")).setMarkup(Markup.HTML).setStyle(leftSpacedStyle));
		subReport15.setDataSource(emptyDataSource);
		subReport16.title(cmp.text(keysForContent.getString("content12")).setMarkup(Markup.HTML).setStyle(leftStyle).setStyle(boldText));
		subReport16.setDataSource(emptyDataSource);
		subReport17.title(cmp.text(keysForContent.getString("content12a")).setMarkup(Markup.HTML).setStyle(leftSpacedStyle));
		subReport17.setDataSource(emptyDataSource);
		subReport18.title(cmp.text(keysForContent.getString("content13")).setMarkup(Markup.HTML).setStyle(leftStyle).setStyle(boldText));
		subReport18.setDataSource(emptyDataSource);
		subReport19.title(cmp.text(keysForContent.getString("content13a")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13b")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.setDataSource(emptyDataSource);
		subReport20.title(cmp.text(keysForContent.getString("content13c")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13d")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13e")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13f")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13g")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13h")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13i")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13j")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13k")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13l")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13m")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13n")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13na")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13nb")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(keysForContent.getString("content13nc")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport22.title(cmp.text(keysForContent.getString("content13nd")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport23.title(cmp.text(keysForContent.getString("content13ne")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport24.title(cmp.text(keysForContent.getString("content13nf")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport25.title(cmp.text(keysForContent.getString("content13o")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport26.title(cmp.text(keysForContent.getString("content13p")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport27.title(cmp.text(keysForContent.getString("content13q")).setMarkup(Markup.HTML).setStyle(leftSpacedStyle));
		if( Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
				.equals(custmrDataFields.getApplicationMaster().getProductCode())){
			subReport28.title(cmp.text(keysForContent.getString("BorrowerAuthorization")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
					.title(getBorrowerContentForSuppAdd(keysForContent, custmrDataFields)).title(cmp.text(""));
		}else{
			subReport28.title(cmp.text(keysForContent.getString("BorrowerAuthorization")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
					.title(getBorrowerContentAdd(keysForContent)).title(cmp.text(""));
		}

		subReport29.title(cmp.text(keysForContent.getString("companyAuthorization")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
				.title(getCompanyAuthAdd(keysForContent, kmIdFromCdh, kmUserNameFromCdh)).title(cmp.text(""));

//		subReport30.title(cmp.text(keysForContent.getString("SCHEDULE")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle));
        subReport30.title(
                cmp.verticalList(
                        cmp.pageBreak(),
                        cmp.text(keysForContent.getString("SCHEDULE"))
                                .setMarkup(Markup.HTML)
                                .setStyle(boldCenteredStyle)
                )
        );
        subReport30.setDataSource(emptyDataSource);

		//		PARTICULARS OF BORROWER/S
		subReport31.title(cmp.text(keysForContent.getString("PARTICULARSOFBORROWER")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)))
				.title(getPARTICULARSOFBORROWERAdd(keysForContent, custmrDataFields)).title(cmp.text(""));

		//		DETAILS OF THE PRINCIPAL LOAN
		subReport32.title(cmp.text(keysForContent.getString("MainLoanDetails")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)))
				.title(getMainLoanDetailsAdd(keysForContent, custmrDataFields, existingLoanName, repaymentList)).title(cmp.text(""));




		String appInsuranceFlag = "N";
		String coAppInsuranceFlag = "N";


		UnnatiIexceedCDHLead cdhLead = custmrDataFields.getCdhLeadDetails();
		if (cdhLead == null) {
			logger.error("cdhLeadDetails is null for request");
		}else{
			logger.debug("insuranceReqd : {} , coInsuranceReqd : {}",
					cdhLead.getInsuranceReqd(), cdhLead.getCoInsuranceReqd());

			appInsuranceFlag = StringUtils.isBlank(cdhLead.getInsuranceReqd())
					? "N" : cdhLead.getInsuranceReqd().trim();
			coAppInsuranceFlag = StringUtils.isBlank(cdhLead.getCoInsuranceReqd())
					? "N" : cdhLead.getCoInsuranceReqd().trim();
		}

		CibilDetailsPayload cibilPayload = CommonUtils
				.resolveCibilPayload(custmrDataFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId, gsonObj);
		logger.debug("CreditDetailsPayload Payload add : " + cibilPayload);

		String processingFees = "";
		if (cibilPayload != null) {
			processingFees = cibilPayload.getProcessingFees();
		}

		boolean isAppInsured = "Y".equalsIgnoreCase(appInsuranceFlag);
		boolean isCoAppInsured = "Y".equalsIgnoreCase(coAppInsuranceFlag);

		String apptPrInsuAmt = (isAppInsured && cibilPayload != null)
				? StringUtils.defaultString(cibilPayload.getInsuranceChargeMember()) : "";
		String coAppPrInsuAmt = (isCoAppInsured && cibilPayload != null)
				? StringUtils.defaultString(cibilPayload.getInsuranceChargeSpouse()) : "";


		JSONObject loanObj = getAllLoanDetails(custmrDataFields);
		String loanAmount = loanObj.optString("loanAmountPrincipal", "");
		int loanAmt = parseSafe2(loanAmount);
		int apptIns = parseSafe(apptPrInsuAmt);
		int coAppIns = parseSafe(coAppPrInsuAmt);
		int procFees = parseSafe(processingFees);

		int result = loanAmt - (apptIns + coAppIns + procFees);
		String amount_NEFT = String.valueOf(result);

		String amount_NEFT_WORD = (result > 0) ? convert(result) : "Zero";

		subReport33.title(cmp.text(keysForContent.getString("content14").replace("{VAR}", amount_NEFT).replace("{ApprovedAmountinWords}", amount_NEFT_WORD)).setMarkup(Markup.HTML).setStyle(leftStyle));

		//		DETAILS OF THE Addition LOAN
		subReport34.title(cmp.text(keysForContent.getString("DETAILSOFADDITIONALLOANADVANCED")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)))
				.title(getAdditionalLoanDetailsAdd(keysForContent, custmrDataFields, productName, sactionedDateStr, repaymentList)).title(cmp.text(""));

//		subReport35.title(getLoanSanctionAmountAdd(keysForContent)).title(cmp.text(""));
//		subReport33.title(cmp.text(keysForContent.getString("Preclosure")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
//				.title(getPreclosureAdd(keysForContent)).title(cmp.text(""));
//		subReport35.title(getLoanSanctionAndPreclosureSideBySide(keysForContent)).title(cmp.text(""));
		subReport35.title(getLoanSanctionAmountAdd(keysForContent, custmrDataFields)).title(cmp.text(""));
		subReport35.title(getPreclosureAdd(keysForContent, custmrDataFields, existingLoanName)).title(cmp.text(""));



		if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE
				.equals(custmrDataFields.getApplicationMaster().getProductCode())){
			subReport36.title(cmp.text(keysForContent.getString("BorrowersSignature")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
					.title(getBorrowerContentForSuppAdd(keysForContent, custmrDataFields)).title(cmp.text(""));
		}else{
			subReport36.title(cmp.text(keysForContent.getString("BorrowersSignature")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
					.title(getBorrowerContentAdd(keysForContent)).title(cmp.text(""));
		}
		subReport37.title(cmp.text(keysForContent.getString("companyAuthorization")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
				.title(getCompanyAuthAdd(keysForContent, kmIdFromCdh, kmUserNameFromCdh)).title(cmp.text(""));





//		subReport20.title(cmp.text(keysForContent.getString("loanDeclaration")).setMarkup(Markup.HTML)
//				.setStyle(boldCenteredStyle.setFontSize(12).underline()));
//		subReport20.title(
//				cmp.text(keysForContent.getString("loanDeclarationValue")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport20.title(cmp.text(""));
		subReport20.setDataSource(emptyDataSource);
//		subReport21.title(postdeclaration(keysForContent));
		subReport21.setDataSource(emptyDataSource);

		report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30))

				.pageFooter(
						cmp.verticalList(SignatoryAdd(keysForContent, custmrDataFields))
								.setFixedHeight(90)
								.setStyle(stl.style().setTopPadding(5))
				)

				.setDataSource(new JREmptyDataSource(1))
				.detail(cmp.verticalList(cmp.subreport(subReport1), cmp.subreport(loanAcountNumerReport),cmp.subreport(subReport2),
						cmp.subreport(subReport3), cmp.subreport(subReport4), cmp.subreport(subReport5),
						cmp.subreport(subReport6), cmp.subreport(subReport7), cmp.subreport(subReport8),
						cmp.subreport(subReport9), cmp.subreport(subReport10), cmp.subreport(subReport11),
						cmp.subreport(subReport12), cmp.subreport(subReport13), cmp.subreport(subReport14),
						cmp.subreport(subReport15), cmp.subreport(subReport16), cmp.subreport(subReport17),
						cmp.subreport(subReport18), cmp.subreport(subReport19), cmp.subreport(subReport20),
						cmp.subreport(subReport22),cmp.subreport(subReport23),cmp.subreport(subReport24),
						cmp.subreport(subReport25),cmp.subreport(subReport26),cmp.subreport(subReport27),
						cmp.subreport(subReport28),cmp.subreport(subReport29),
						cmp.subreport(subReport30),cmp.subreport(subReport31),
						cmp.subreport(subReport32),
						cmp.subreport(subReport34),cmp.subreport(subReport33),
						cmp.subreport(subReport35),cmp.subreport(subReport36),
						cmp.subreport(subReport37),
						cmp.subreport(subReport21)));

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
	private ComponentBuilder<?, ?> getLoanDetails4Add(JSONObject keysForContent) {
		VerticalListBuilder verticalList = cmp.verticalList();
		try {
			Gson gsonObj = new Gson();
			CibilDetailsPayload cibilPayloadCoApp = null;
			verticalList.add(createTwoHorizontalList(keysForContent.getString("loanApplicationNumber"),
					"123456_654321"
			));
		} catch (Exception e) {
			logger.error("error - getLoanDetails");
			logger.error(e.getMessage());
		}
		logger.debug("LoanDetails added");
		return verticalList;
	}

	private ComponentBuilder<?, ?> getBorrowerContentForSuppAdd(JSONObject keysForContent,CustomerDataFields custmrDataFields) {
		VerticalListBuilder verticalList = cmp.verticalList();
		try {
			if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(custmrDataFields.getApplicationMaster().getProductCode())) {
				verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("name"),
						keysForContent.getString("signature"), width50, width50
				));
				verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("borrower") +" :"+ applicantFirstname+" "+applicantLastname,
						keysForContent.getString("signature") + "", width50, width50
				));
				verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("coborrower") + " :" + coApplicantFirstname+ " " + coApplicantLastname,
						keysForContent.getString("signature"), width50, width50
				));
			}
			else{
				verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("name"),
						keysForContent.getString("signature"), width50, width50
				));
				verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("borrower") +" :"+ applicantFirstname+" "+applicantLastname,
						keysForContent.getString("signature") + "____________", width50, width50
				));
			}
		} catch (Exception e) {
			logger.error("error - getBorrowerContentForSuppAdd");
			logger.error(e.getMessage());
		}
		logger.debug("BorrowerContentForSuppAdd added");
		return verticalList;
	}

	private ComponentBuilder<?, ?> getBorrowerContentAdd(JSONObject keysForContent) {
		VerticalListBuilder verticalList = cmp.verticalList();
		try {
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("name"),
					keysForContent.getString("signature"), width50, width50
			));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("borrower") + applicantFirstname+" "+applicantLastname,
					keysForContent.getString("signature") + "____________", width50, width50
			));

		} catch (Exception e) {
			logger.error("error - getBorrowerContentAdd");
			logger.error(e.getMessage());
		}
		logger.debug("BorrowerContentAdd added");
		return verticalList;
	}

	private ComponentBuilder<?, ?> getPreclosureAdd(JSONObject keysForContent, CustomerDataFields custmrDataFields, String existingLoanName) {
		VerticalListBuilder verticalList = cmp.verticalList();

		try {

			List<ExistingGLLoanDetails> existingGLLoanDetails = custmrDataFields.getExistingGLLoanDetails();
			ExistingGLLoanDetails glLoan = null;
			if (existingGLLoanDetails == null || existingGLLoanDetails.isEmpty()) {
				logger.debug("No existingGLLoanDetails");
			} else {
				logger.debug("existingGLLoanDetails size: {}", existingGLLoanDetails.size());
				glLoan = existingGLLoanDetails.stream()
						.filter(Objects::nonNull)
						.filter(d -> existingLoanName.equals(d.getExistingLoanName()))
						.findFirst()
						.orElse(null);
			}

			String loanAmountStr = (glLoan.getLoanAmount() != null)
					? String.format("%,.2f", glLoan.getLoanAmount())
					: "0.00";

			String existingLoanId = String.valueOf(glLoan.getExistingLoanId());

			// __raj1
			verticalList.add(createOneHorizontalList(keysForContent.getString("Preclosure"), keysForContent.getString("Preclosure")));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("PreClosedLoanAccountNumber"),
					keysForContent.getString("PreClosureAmt"), width60, width40
			));
			// __raj
//			verticalList.add(createTwoHorizontalListForLoanAgreement(existingLoanId,
//					loanAmountStr, width60, width40
//			));
			verticalList.add(createTwoHorizontalListForLoanAgreement("NA",
					"NA", width60, width40
			));
//			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("PreClosedLoanTotal"),
//					loanAmountStr, width60, width40
//			));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("PreClosedLoanTotal"),
					"NA", width60, width40
			));
		} catch (Exception e) {
			logger.error("error - getPreclosureAdd");
			logger.error(e.getMessage());
		}
		logger.debug("PreclosureAdd added");
		return verticalList;
	}



	private ComponentBuilder<?, ?> getLoanSanctionAmountAdd(JSONObject keysForContent, CustomerDataFields custmrDataFields) {
		VerticalListBuilder verticalList = cmp.verticalList();
		JSONObject jsnObj = new JSONObject();
		Gson gsonObj = new Gson();
		try {
            //--abhi--
//			JSONObject loanObj = getAllLoanDetails(custmrDataFields);

			List<ExistingGLLoanDetails> existingGLLoanDetails = custmrDataFields.getExistingGLLoanDetails();

			ExistingGLLoanDetails glLoan = null;
			if (existingGLLoanDetails == null || existingGLLoanDetails.isEmpty()) {
				logger.debug("No existingGLLoanDetails");
			} else {
				logger.debug("existingGLLoanDetails size: {}", existingGLLoanDetails.size());
				glLoan = existingGLLoanDetails.stream()
						.filter(Objects::nonNull)
						.findFirst()
						.orElse(null);
			}

			CibilDetailsPayload cibilPayload = CommonUtils
					.resolveCibilPayload(custmrDataFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId, gsonObj);
			logger.debug("CreditDetailsPayload Payload add : " + cibilPayload);

			JSONObject loanObj = getAllLoanDetails(custmrDataFields);
			String loanAmount = loanObj.optString("loanAmountPrincipal", "");

			String processingFees = "";
			if (cibilPayload != null) {
				processingFees = cibilPayload.getProcessingFees();
			}


			String appInsuranceFlag = "N";
			String coAppInsuranceFlag = "N";


			UnnatiIexceedCDHLead cdhLead = custmrDataFields.getCdhLeadDetails();
			if (cdhLead == null) {
				logger.error("cdhLeadDetails is null for request");
			}else{
				logger.debug("insuranceReqd : {} , coInsuranceReqd : {}",
						cdhLead.getInsuranceReqd(), cdhLead.getCoInsuranceReqd());

				appInsuranceFlag = StringUtils.isBlank(cdhLead.getInsuranceReqd())
						? "N" : cdhLead.getInsuranceReqd().trim();
				coAppInsuranceFlag = StringUtils.isBlank(cdhLead.getCoInsuranceReqd())
						? "N" : cdhLead.getCoInsuranceReqd().trim();
			}

			boolean isAppInsured = "Y".equalsIgnoreCase(appInsuranceFlag);
			boolean isCoAppInsured = "Y".equalsIgnoreCase(coAppInsuranceFlag);

			String apptPrInsuAmt = (isAppInsured && cibilPayload != null)
					? StringUtils.defaultString(cibilPayload.getInsuranceChargeMember()) : "";
			String coAppPrInsuAmt = (isCoAppInsured && cibilPayload != null)
					? StringUtils.defaultString(cibilPayload.getInsuranceChargeSpouse()) : "";

			int loanAmt = parseSafe2(loanAmount);
			int apptIns = parseSafe(apptPrInsuAmt);
			int coAppIns = parseSafe(coAppPrInsuAmt);
			int procFees = parseSafe(processingFees);

			int totalPreimium = apptIns + coAppIns;

			int finalNeftAmt = loanAmt - (totalPreimium + procFees);

			int result = loanAmt - (apptIns + coAppIns + procFees);
			String amount = String.valueOf(result);

			verticalList.add(cmp.verticalGap(20));
//			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("PrincipalLoanAgreementDate"),
//					glLoan == null ? "NA" : dtStr(glLoan.getPrincipalLoanAgreementDate()), width60, width40));

			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("LoanSanctionAmounta"),
					 loanObj == null ? "NA" : "Rs. " + loanAmount, width60, width40
			));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("Lessb"),
					"",  width60, width40
			));
			// __raj
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("ProcessingFeeInclGST"),
					"Rs. " + processingFees,  width60, width40
			));
//			__raj
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("InsurancePremium"),
					"Rs. " + totalPreimium,  width60, width40
			));

			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("PreClosureAmount"),
					"NA",  width60, width40
			));


			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("LoanNetSanctionAmountc"),
					"Rs." + finalNeftAmt,  width60, width40
			));

		} catch (Exception e) {
			logger.error("error - getLoanSanctionAmountAdd");
			logger.error(e.getMessage());
		}
		logger.debug("LoanSanctionAmountAdd added");
		return verticalList;
	}

	private int parseSafe(String value) {
		if (value == null || value.trim().isEmpty()) {
			return 0;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			return 0; // or log the error, or handle differently
		}
	}

	private int parseSafe2(String value) {
		if (value == null || value.trim().isEmpty()) {
			return 0;
		}
		try {
			return new BigDecimal(value.trim()).intValue();
		} catch (NumberFormatException e) {
			logger.warn("Failed to parse numeric value: '{}'", value);
			return 0;
		}
	}


	private ComponentBuilder<?, ?> getCompanyAuthAdd(JSONObject keysForContent, String kmIdFromCdh, String kmUserNameFromCdh) {
		VerticalListBuilder verticalList = cmp.verticalList();
		try {
			verticalList.add(createFourHorizontalList(keysForContent.getString("OfficerName"),
					keysForContent.getString("empId"), keysForContent.getString("designation"), keysForContent.getString("signature")
			));
			verticalList.add(createFourHorizontalList(kmUserNameFromCdh,
					kmIdFromCdh, "KM",""
			));
		} catch (Exception e) {
			logger.error("error - getCompanyAuthAdd");
			logger.error(e.getMessage());
		}
		logger.debug("CompanyAuthAdd added");
		return verticalList;
	}

	public JSONObject getAllAddressDeatils(CustomerDataFields req) {
		logger.debug("Entry - getAllAddressDetails method");
		JSONObject jsnObj = null;
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
		String presentResidenceSize = "NA";

		//landmark

		String presentLandmarkApplicant = "";
		String permanentLandmarkApplicant = "";

		String presentLandmarkCoApplicant = "";
		String permanentLandmarkCoApplicant = "";


		String presentResidenceOwnershipCo = "";
		String presentAddressYearsCo = "";
		String presntCityYearsCo = "";
		String presentResidenceAddressProofCo = "";
		String presentResidenceTypeCo = "";
		String presentResidenceSizeCo = "NA";

		// Occupation Address
		String occpnAddrApplicant = "";
		String occpnAddrCoApplicant = "";

		String occpnLandmarkApplicant = "";
		String occpnLandmarkCoApplicant = "";
		try {
			Gson gsonObj = new Gson();

			List<Address> applicantAddrPayLoadLst = null;
			List<Address> coApplicantAddrPayLoadLst = null;

			List<Address> applicantOccupnAddrPayLoadLst = null;
			List<Address> coApplicantOccupnAddrPayLoadLst = null;

			// Personal
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

			// Present Address - Applicant
			for (Address addr : applicantAddrPayLoadLst) {
				if (addr.getAddressType().equalsIgnoreCase("present")) {
//					presentAddressApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3()
//							+ addr.getArea() +  addr.getLandMark() + addr.getCity() + addr.getDistrict() + addr.getState()
//							+ addr.getCountry() + addr.getPinCode();

					presentAddressApplicant = getFullAddress(addr);
					presentLandmarkApplicant = CommonUtils.getDefaultValue(addr.getLandMark());
					logger.debug("Present Address - Applicant : " + presentLandmarkApplicant);
//                    presentResidenceOwnership = addr.getResidenceOwnership();
//                    presentAddressYears = addr.getResidenceAddressSince();
//                    presntCityYears = addr.getResidenceCitySince();
//                    presentResidenceAddressProof = addr.getCurrentAddressProof();
//                    presentResidenceType = addr.getHouseType();
					presentResidenceOwnership =
							addr.getResidenceOwnership() == null ? "" : addr.getResidenceOwnership();

					presentAddressYears =
							addr.getResidenceAddressSince() == null ? "" : addr.getResidenceAddressSince();

					presntCityYears =
							addr.getResidenceCitySince() == null ? "" : addr.getResidenceCitySince();

					presentResidenceAddressProof =
							addr.getCurrentAddressProof() == null ? "" : addr.getCurrentAddressProof();

					presentResidenceType =
							addr.getHouseType() == null ? "" : addr.getHouseType();

				} else if (addr.getAddressType().equalsIgnoreCase("Permanent")) {
//					permanetAddressApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3()
//							+ addr.getArea()  + addr.getLandMark() + addr.getCity() + addr.getDistrict() + addr.getState()
//							+ addr.getCountry() + addr.getPinCode();
					permanetAddressApplicant = getFullAddress(addr);
					permanentLandmarkApplicant = CommonUtils.getDefaultValue(addr.getLandMark());
					logger.debug("Permanent Address - Applicant :" + permanetAddressApplicant);
					logger.debug("Permanent Address - Applicant :" + permanentLandmarkApplicant);
				}
			}

			// Present Address - CoApplicant
			if (coApplicantAddrPayLoadLst != null) {
				for (Address addr : coApplicantAddrPayLoadLst) {
					if (addr.getAddressType().equalsIgnoreCase("present")) {
//						presentAddressCoApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3() + addr.getArea() + addr.getLandMark() + addr.getCity() + addr.getDistrict()+ addr.getState() + addr.getCountry() + addr.getPinCode();
						presentAddressCoApplicant = getFullAddress(addr);
						logger.debug("Present Address - Co-applicant :" + presentAddressCoApplicant);
//                        presentResidenceOwnershipCo = addr.getResidenceOwnership();
//                        presentAddressYearsCo = addr.getResidenceAddressSince();
//                        presntCityYearsCo = addr.getResidenceCitySince();
//                        presentResidenceAddressProofCo = addr.getCurrentAddressProof();
//                        presentResidenceTypeCo = addr.getHouseType();
						presentResidenceOwnershipCo =
								addr.getResidenceOwnership() == null ? "" : addr.getResidenceOwnership();

						presentAddressYearsCo =
								addr.getResidenceAddressSince() == null ? "" : addr.getResidenceAddressSince();

						presntCityYearsCo =
								addr.getResidenceCitySince() == null ? "" : addr.getResidenceCitySince();

						presentResidenceAddressProofCo =
								addr.getCurrentAddressProof() == null ? "" : addr.getCurrentAddressProof();

						presentResidenceTypeCo =
								addr.getHouseType() == null ? "" : addr.getHouseType();
					} else if (addr.getAddressType().equalsIgnoreCase("Permanent")) {
//						permanetAddressCoApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3() + addr.getArea() + addr.getLandMark() + addr.getCity() + addr.getDistrict()+ addr.getState() + addr.getCountry() + addr.getPinCode();
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
//			if(addr.getAddressType().equalsIgnoreCase("Office")) {
			occpnAddrApplicant = getFullAddress(ocupnAddr);
			occpnLandmarkApplicant =
					ocupnAddr == null ? "" : CommonUtils.getDefaultValue(ocupnAddr.getLandMark());

			occpnAddrCoApplicant = getFullAddress(ocupnAddrCo);
			occpnLandmarkCoApplicant =
					ocupnAddrCo == null ? "" : CommonUtils.getDefaultValue(ocupnAddrCo.getLandMark());

			logger.debug("Ocupation Address Applicnt : " + occpnAddrApplicant);
			logger.debug("Ocupation Address Co-Applicnt : " + occpnAddrCoApplicant);

			logger.debug("Ocupation Address Applicnt : " + occpnLandmarkApplicant);
			logger.debug("Ocupation Address Co-Applicnt : " + occpnLandmarkCoApplicant);

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
			jsnObj.put("presentLandmarkApplicant", presentLandmarkApplicant);
			jsnObj.put("permanentLandmarkApplicant", permanentLandmarkApplicant);

			// other Deatils
			jsnObj.put("presentResidenceOwnership", CommonUtils.getDefaultValue(presentResidenceOwnership));
			jsnObj.put("presentAddressYears", CommonUtils.getDefaultValue(presentAddressYears));
			jsnObj.put("presntCityYears", CommonUtils.getDefaultValue(presntCityYears));
			jsnObj.put("presentResidenceAddressProof", CommonUtils.getDefaultValue(presentResidenceAddressProof));
			jsnObj.put("presentResidenceType", CommonUtils.getDefaultValue(presentResidenceType));
			jsnObj.put("presentResidenceSize", CommonUtils.getDefaultValue(presentResidenceSize));

			jsnObj.put("presentResidenceOwnershipCo", CommonUtils.getDefaultValue(presentResidenceOwnershipCo));
			jsnObj.put("presentAddressYearsCo", CommonUtils.getDefaultValue(presentAddressYearsCo));
			jsnObj.put("presntCityYearsCo", CommonUtils.getDefaultValue(presntCityYearsCo));
			jsnObj.put("presentResidenceAddressProofCo", CommonUtils.getDefaultValue(presentResidenceAddressProofCo));
			jsnObj.put("presentResidenceTypeCo", CommonUtils.getDefaultValue(presentResidenceTypeCo));
			jsnObj.put("presentResidenceSizeCo", CommonUtils.getDefaultValue(presentResidenceSizeCo));

			// Occupation Address
			jsnObj.put("occpnAddrApplicant", occpnAddrApplicant);
			jsnObj.put("occpnAddrCoApplicant", occpnAddrCoApplicant);

			jsnObj.put("occpnLandmarkApplicant", occpnLandmarkApplicant);
			jsnObj.put("occpnLandmarkCoApplicant", occpnLandmarkCoApplicant);

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

	public JSONObject getAllExistingLoanDetails(CustomerDataFields req) {
		logger.debug("Entry - getAllExistingLoanDetails method");
		JSONObject jsnObj = new JSONObject();

		String loanAmountApplicant = "";
		String loanAmountCoApplicant = "";

		BigDecimal totalLoanAmtApplicant = BigDecimal.ZERO;
		BigDecimal totalLoanAmtCoApplicant = BigDecimal.ZERO;

		List<String> applicantLoanAmtLst = new ArrayList<>();
		List<String> coApplicantLoanAmtLst = new ArrayList<>();

		try {
			Gson gsonObj = new Gson();

			if (req.getExistingLoanDetailsWrapperList() != null) {

				for (ExistingLoanDetailsWrapper wrapper : req.getExistingLoanDetailsWrapperList()) {

					if (wrapper == null || wrapper.getExistingLoanDetailsList() == null) {
						continue;
					}

					for (ExistingLoanDetails loan : wrapper.getExistingLoanDetailsList()) {

						if (loan == null || loan.getPayloadColumn() == null
								|| loan.getPayloadColumn().trim().isEmpty()) {
							continue;
						}

						ExistingLoanDetailsPayload payload = gsonObj.fromJson(loan.getPayloadColumn(),
								ExistingLoanDetailsPayload.class);

						if (payload == null || payload.getLoanAmount() == null
								|| payload.getLoanAmount().trim().isEmpty()) {
							continue;
						}

						String amtStr = payload.getLoanAmount().replaceAll("[^0-9.]", "").trim();
						if (amtStr.isEmpty()) {
							continue;
						}

						BigDecimal amt;
						try {
							amt = new BigDecimal(amtStr);
						} catch (NumberFormatException nfe) {
							logger.error("Invalid loanAmount : " + payload.getLoanAmount());
							continue;
						}

						if (String.valueOf(loan.getCustDtlId()).equals(applicantCustId)) {
							applicantLoanAmtLst.add(amt.toPlainString());
							totalLoanAmtApplicant = totalLoanAmtApplicant.add(amt);
							logger.debug("ExistingLoan amount - Applicant : " + amt);
						} else if (String.valueOf(loan.getCustDtlId()).equals(coApplicantCustId)) {
							coApplicantLoanAmtLst.add(amt.toPlainString());
							totalLoanAmtCoApplicant = totalLoanAmtCoApplicant.add(amt);
							logger.debug("ExistingLoan amount - Co-applicant : " + amt);
						}
					}
				}
			}

			// first loan amount, for single-value report fields
			loanAmountApplicant = applicantLoanAmtLst.isEmpty() ? "" : applicantLoanAmtLst.get(0);
			loanAmountCoApplicant = coApplicantLoanAmtLst.isEmpty() ? "" : coApplicantLoanAmtLst.get(0);

			jsnObj.put("loanAmountApplicant", CommonUtils.getDefaultValue(loanAmountApplicant));
			jsnObj.put("loanAmountCoApplicant", CommonUtils.getDefaultValue(loanAmountCoApplicant));

			jsnObj.put("totalLoanAmountApplicant", totalLoanAmtApplicant.toPlainString());
			jsnObj.put("totalLoanAmountCoApplicant", totalLoanAmtCoApplicant.toPlainString());

			// all amounts joined, when the report needs every loan listed
			jsnObj.put("loanAmountListApplicant", String.join(", ", applicantLoanAmtLst));
			jsnObj.put("loanAmountListCoApplicant", String.join(", ", coApplicantLoanAmtLst));

		} catch (Exception e) {
			logger.error("error - getAllExistingLoanDetails Method");
			logger.error(e.getMessage());
		}
		logger.debug("Exit - getAllExistingLoanDetails method completed.");
		return jsnObj;
	}

	public JSONObject getAllPersonalDetails(CustomerDataFields req) {
		logger.debug("Entry - getAllPersonalDetails method");
		JSONObject jsnObj = new JSONObject();

		String titleApplicant = "", titleCoApplicant = "";
		String firstNameApplicant = "", firstNameCoApplicant = "";
		String lastNameApplicant = "", lastNameCoApplicant = "";
		String fullNameApplicant = "", fullNameCoApplicant = "";
		String genderApplicant = "", genderCoApplicant = "";
		String dobApplicant = "", dobCoApplicant = "";
		String ageApplicant = "", ageCoApplicant = "";                          // added
		String mobileApplicant = "", mobileCoApplicant = "";
		String voterIdApplicant = "", voterIdCoApplicant = "";
		String ckycApplicant = "", ckycCoApplicant = "";

		try {
			Gson gsonObj = new Gson();

			if (req.getCustomerDetailsList() != null) {

				for (CustomerDetails cust : req.getCustomerDetailsList()) {

					if (cust == null) {
						continue;
					}

					CustomerDetailsPayload payload = cust.getPayload();

					// transient field is null when the entity comes straight from the DB
					if (payload == null && cust.getPayloadColumn() != null
							&& !cust.getPayloadColumn().trim().isEmpty()) {
						payload = gsonObj.fromJson(cust.getPayloadColumn(), CustomerDetailsPayload.class);
					}

					if (payload == null) {
						continue;
					}

					String custIdStr = String.valueOf(cust.getCustDtlId());

					if (custIdStr.equals(applicantCustId)) {
						titleApplicant = CommonUtils.getDefaultValue(payload.getTitle());
						firstNameApplicant = CommonUtils.getDefaultValue(payload.getFirstName());
						lastNameApplicant = CommonUtils.getDefaultValue(payload.getLastName());
						fullNameApplicant = CommonUtils.getDefaultValue(
								buildFullName(cust.getCustomerName(), payload));
						genderApplicant = CommonUtils.getDefaultValue(payload.getGender());
						dobApplicant = CommonUtils.getDefaultValue(payload.getDob());
						ageApplicant = CommonUtils.getDefaultValue(payload.getAge());        // added
						mobileApplicant = CommonUtils.getDefaultValue(cust.getMobileNumber());
						voterIdApplicant = CommonUtils.getDefaultValue(getVoterId(payload));
						ckycApplicant = CommonUtils.getDefaultValue(payload.getCkyc());
						logger.debug("Personal details - Applicant : " + fullNameApplicant);

					} else if (custIdStr.equals(coApplicantCustId)) {
						titleCoApplicant = CommonUtils.getDefaultValue(payload.getTitle());
						firstNameCoApplicant = CommonUtils.getDefaultValue(payload.getFirstName());
						lastNameCoApplicant = CommonUtils.getDefaultValue(payload.getLastName());
						fullNameCoApplicant = CommonUtils.getDefaultValue(
								buildFullName(cust.getCustomerName(), payload));
						genderCoApplicant = CommonUtils.getDefaultValue(payload.getGender());
						dobCoApplicant = CommonUtils.getDefaultValue(payload.getDob());
						ageCoApplicant = CommonUtils.getDefaultValue(payload.getAge());      // added
						mobileCoApplicant = CommonUtils.getDefaultValue(cust.getMobileNumber());
						voterIdCoApplicant = CommonUtils.getDefaultValue(getVoterId(payload));
						ckycCoApplicant = CommonUtils.getDefaultValue(payload.getCkyc());
						logger.debug("Personal details - Co-applicant : " + fullNameCoApplicant);
					}
				}
			}

			jsnObj.put("titleApplicant", titleApplicant);
			jsnObj.put("titleCoApplicant", titleCoApplicant);
			jsnObj.put("firstNameApplicant", firstNameApplicant);
			jsnObj.put("firstNameCoApplicant", firstNameCoApplicant);
			jsnObj.put("lastNameApplicant", lastNameApplicant);
			jsnObj.put("lastNameCoApplicant", lastNameCoApplicant);
			jsnObj.put("fullNameApplicant", fullNameApplicant);
			jsnObj.put("fullNameCoApplicant", fullNameCoApplicant);
			jsnObj.put("genderApplicant", genderApplicant);
			jsnObj.put("genderCoApplicant", genderCoApplicant);
			jsnObj.put("dobApplicant", dobApplicant);
			jsnObj.put("dobCoApplicant", dobCoApplicant);
			jsnObj.put("ageApplicant", ageApplicant);                            // added
			jsnObj.put("ageCoApplicant", ageCoApplicant);                        // added
			jsnObj.put("mobileApplicant", mobileApplicant);
			jsnObj.put("mobileCoApplicant", mobileCoApplicant);
			jsnObj.put("voterIdApplicant", voterIdApplicant);
			jsnObj.put("voterIdCoApplicant", voterIdCoApplicant);
			jsnObj.put("ckycApplicant", ckycApplicant);
			jsnObj.put("ckycCoApplicant", ckycCoApplicant);

		} catch (Exception e) {
			logger.error("error - getAllPersonalDetails Method");
			logger.error(e.getMessage());
		}
		logger.debug("Exit - getAllPersonalDetails method completed.");
		return jsnObj;
	}

	private static String buildFullName(String customerName, CustomerDetailsPayload payload) {
		if (customerName != null && !customerName.trim().isEmpty()) {
			return customerName.trim();
		}
		if (payload == null) {
			return "";
		}
		String[] parts = { payload.getFirstName(), payload.getMiddleName(), payload.getLastName() };
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			if (part != null && !part.trim().isEmpty()) {
				sb.append(part.trim()).append(" ");
			}
		}
		return sb.toString().trim();
	}

	private static String getVoterId(CustomerDetailsPayload payload) {
		if (payload == null) {
			return "";
		}
		if (payload.getAlternateVoterId() != null && !payload.getAlternateVoterId().trim().isEmpty()) {
			return payload.getAlternateVoterId();
		}
		// voter ID may instead have been captured as the primary or secondary KYC document
		if ("VOTERID".equalsIgnoreCase(trimOrEmpty(payload.getPrimaryKycType()))
				|| "VOTER ID".equalsIgnoreCase(trimOrEmpty(payload.getPrimaryKycType()))) {
			return trimOrEmpty(payload.getPrimaryKycId());
		}
		if ("VOTERID".equalsIgnoreCase(trimOrEmpty(payload.getSecondaryKycType()))
				|| "VOTER ID".equalsIgnoreCase(trimOrEmpty(payload.getSecondaryKycType()))) {
			return trimOrEmpty(payload.getSecondaryKycId());
		}
		return "";
	}

	private static String trimOrEmpty(String s) {
		return s == null ? "" : s.trim();
	}

//	private ComponentBuilder<?, ?> getPersonalDetailsAdd(JSONObject keysForContent,
//														 CustomerDataFields custmrDataFields) {
//		VerticalListBuilder verticalList = cmp.verticalList();
//		try {
//			JSONObject p = getAllPersonalDetails(custmrDataFields);
//
//			verticalList.add(createThreeHorizontalList("Details", "Applicant details", "Co-applicant details"));
//			verticalList.add(createThreeHorizontalList(keysForContent.getString("Title"),
//					p.optString("titleApplicant", ""), p.optString("titleCoApplicant", "")));
//			verticalList.add(createThreeHorizontalList(keysForContent.getString("FirstName"),
//					p.optString("firstNameApplicant", ""), p.optString("firstNameCoApplicant", "")));
//			verticalList.add(createThreeHorizontalList(keysForContent.getString("LastName"),
//					p.optString("lastNameApplicant", ""), p.optString("lastNameCoApplicant", "")));
//			verticalList.add(createThreeHorizontalList(keysForContent.getString("FullName"),
//					p.optString("fullNameApplicant", ""), p.optString("fullNameCoApplicant", "")));
//			verticalList.add(createThreeHorizontalList(keysForContent.getString("Gender"),
//					p.optString("genderApplicant", ""), p.optString("genderCoApplicant", "")));
//			verticalList.add(createThreeHorizontalList(keysForContent.getString("DateOfBirth"),
//					p.optString("dobApplicant", ""), p.optString("dobCoApplicant", "")));
//			verticalList.add(createThreeHorizontalList(keysForContent.getString("MobileNumber"),
//					p.optString("mobileApplicant", ""), p.optString("mobileCoApplicant", "")));
//			verticalList.add(createThreeHorizontalList(keysForContent.getString("VoterIdNumber"),
//					p.optString("voterIdApplicant", ""), p.optString("voterIdCoApplicant", "")));
//			verticalList.add(createThreeHorizontalList(keysForContent.getString("CkycNumber"),
//					p.optString("ckycApplicant", ""), p.optString("ckycCoApplicant", "")));
//
//		} catch (Exception e) {
//			logger.error("error - getPersonalDetailsAdd");
//			logger.error(e.getMessage());
//		}
//		logger.debug("PersonalDetailsAdd added");
//		return verticalList;
//	}

	private ComponentBuilder<?, ?> getPARTICULARSOFBORROWERAdd(JSONObject keysForContent, CustomerDataFields custmrDataFields) {
		VerticalListBuilder verticalList = cmp.verticalList();
		try {
			JSONObject addressDetailsObj = getAllAddressDeatils(custmrDataFields);
			JSONObject existingLoanObj = getAllExistingLoanDetails(custmrDataFields);
			JSONObject personalObj = getAllPersonalDetails(custmrDataFields);
			if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(custmrDataFields.getApplicationMaster().getProductCode())) {
				verticalList.add(createTwoHorizontalList(keysForContent.getString("Details"),
						keysForContent.getString("Details")
				));
				verticalList.add(createTwoHorizontalList(keysForContent.getString("BorrowerName"),
						applicantFirstname + " " + applicantLastname
				));

				verticalList.add(createTwoHorizontalList(keysForContent.getString("BorrowerAge"),
						personalObj.optString("ageApplicant", "")
				));
				verticalList.add(createTwoHorizontalList(keysForContent.getString("BorrowerAddress"),
						addressDetailsObj.getString("presentAddressApplicant")
				));
				verticalList.add(createTwoHorizontalList(keysForContent.getString("CoBorrowerName"),
						coApplicantFirstname + " " + coApplicantLastname
				));
				verticalList.add(createTwoHorizontalList(keysForContent.getString("CoBorrowerAge"),
						personalObj.optString("ageCoApplicant", "")
				));
				verticalList.add(createTwoHorizontalList(keysForContent.getString("CoBorrowerAddress"),
						addressDetailsObj.getString("presentAddressCoApplicant")
				));
			}
			else {
				verticalList.add(createTwoHorizontalList(keysForContent.getString("Details"),
						keysForContent.getString("Details")
				));
				verticalList.add(createTwoHorizontalList(keysForContent.getString("BorrowerName"),
						applicantFirstname + " " + applicantLastname
				));
				verticalList.add(createTwoHorizontalList(keysForContent.getString("BorrowerAge"),
						personalObj.optString("ageApplicant", "")
				));
				verticalList.add(createTwoHorizontalList(keysForContent.getString("BorrowerAddress"),
						addressDetailsObj.getString("presentAddressApplicant")
				));
			}
		} catch (Exception e) {
			logger.error("error - getPARTICULARSOFBORROWERAdd");
			logger.error(e.getMessage());
		}
		logger.debug("PARTICULARSOFBORROWERAdd added");
		return verticalList;
	}

	private static String getModeOfRepayment(CustomerDataFields req, LoanDetails loan) {
		if (req.getEnachDetails() != null && !req.getEnachDetails().isEmpty()) {
			return "e-NACH";
		}
		if (loan.getAutoEmiAccount() != null && !loan.getAutoEmiAccount().trim().isEmpty()) {
			return "Auto Debit - " + loan.getAutoEmiAccount();
		}
		return "";
	}

	public JSONObject getAllLoanDetails(CustomerDataFields req) {
		logger.debug("Entry - getAllLoanDetails method");
		JSONObject jsnObj = new JSONObject();
		Gson gsonObj = new Gson();
		CibilDetailsPayload cibilPayloadCoApp = CommonUtils.resolveCibilPayload(req.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId, gsonObj);

		for (CibilDetailsWrapper cibilDetailsWrapper : req.getCibilDetailsWrapperList()) {
			String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
			//	String customerType = applicant ? applicantCustId : coApplicantCustId;
			logger.debug("cibilDetailsPayload Payload : " + cibilPayloadCoApp);
			if (custId.equals(coApplicantCustId)) {
				cibilPayloadCoApp = gsonObj.fromJson(
						cibilDetailsWrapper.getCibilDetails().getPayloadColumn(), CibilDetailsPayload.class);
			}
		}
		String interest = (cibilPayloadCoApp.getRoi() == null) ? "" : cibilPayloadCoApp.getRoi().toString();
		String term = cibilPayloadCoApp.getFinalTenure();
		String repaymentFrequencyNew = cibilPayloadCoApp.getRepaymentFrequency();

		String loanAmount = "";
		String roi = "";
		String tenure = "";
		String numberOfInstalments = "";
		String instalmentAmount = "";
		String repaymentFrequency = "";
		String modeOfRepayment = "";
		String loanPurpose = "";
		String modeOfSecurity = "";
		String modeOfDisbursement = "";
		String subCategory = "";
		String totalPayable = "";
		String emiDate = "";

		try {
			LoanDetails loan = req.getLoanDetails();

			if (loan != null) {

				LoanDetailsPayload payload = loan.getPayload();

				// transient field is null when the entity comes straight from the DB
				if (payload == null && loan.getPayloadColumn() != null
						&& !loan.getPayloadColumn().trim().isEmpty()) {
					payload = gsonObj.fromJson(loan.getPayloadColumn(), LoanDetailsPayload.class);
				}

				// sanctioned amount is the agreed figure; applied amount is the fallback
				BigDecimal amt = loan.getSanctionedLoanAmount() != null
						? loan.getSanctionedLoanAmount()
						: loan.getLoanAmount();
				loanAmount = amt == null ? "" : amt.toPlainString();

				roi = loan.getRoi() == null ? "" : String.format("%.2f", loan.getRoi());

				// NOTE: field names and column names disagree here - verify against the data
				Integer tenureMonths = loan.getTenure();
				tenure = tenureMonths == null ? "" : String.valueOf(tenureMonths);
				numberOfInstalments = tenureMonths == null ? "" : String.valueOf(tenureMonths);

				instalmentAmount = loan.getMonthlyEmi() == null
						? "" : loan.getMonthlyEmi().toPlainString();

				totalPayable = loan.getTotPayableAmount() == null
						? "" : loan.getTotPayableAmount().toPlainString();

				emiDate = CommonUtils.getDefaultValue(loan.getEmiDate());

				modeOfRepayment = getModeOfRepayment(req, loan);

				if (payload != null) {
					loanPurpose = CommonUtils.getDefaultValue(payload.getLoanPurpose());
					modeOfSecurity = CommonUtils.getDefaultValue(payload.getModeOfSecurity());
					modeOfDisbursement = CommonUtils.getDefaultValue(payload.getModeOfDisbursement());
					subCategory = CommonUtils.getDefaultValue(payload.getSubCategory());

					repaymentFrequency = payload.getFrequencyOfRepayment() == null
							|| payload.getFrequencyOfRepayment().trim().isEmpty()
							? "" : payload.getFrequencyOfRepayment().trim();
				}
			}

			jsnObj.put("loanAmountPrincipal", CommonUtils.getDefaultValue(loanAmount));
			jsnObj.put("roiPrincipal", CommonUtils.getDefaultValue(roi));
			jsnObj.put("tenurePrincipal", CommonUtils.getDefaultValue(tenure));
			jsnObj.put("numberOfInstalmentsPrincipal", CommonUtils.getDefaultValue(numberOfInstalments));
			jsnObj.put("instalmentAmountPrincipal", CommonUtils.getDefaultValue(instalmentAmount));
			jsnObj.put("repaymentFrequencyPrincipal", CommonUtils.getDefaultValue(repaymentFrequencyNew));
			jsnObj.put("modeOfRepaymentPrincipal", CommonUtils.getDefaultValue(modeOfRepayment));
			jsnObj.put("loanPurposePrincipal", CommonUtils.getDefaultValue(loanPurpose));
			jsnObj.put("modeOfSecurityPrincipal", CommonUtils.getDefaultValue(modeOfSecurity));
			jsnObj.put("modeOfDisbursementPrincipal", CommonUtils.getDefaultValue(modeOfDisbursement));
			jsnObj.put("subCategoryPrincipal", CommonUtils.getDefaultValue(subCategory));
			jsnObj.put("totalPayablePrincipal", CommonUtils.getDefaultValue(totalPayable));
			jsnObj.put("emiDatePrincipal", CommonUtils.getDefaultValue(emiDate));

		} catch (Exception e) {
			logger.error("error - getAllLoanDetails Method");
			logger.error(e.getMessage());
		}
		logger.debug("Exit - getAllLoanDetails method completed.");
		return jsnObj;
	}

	private ComponentBuilder<?, ?> getMainLoanDetailsAdd(JSONObject keysForContent, CustomerDataFields custmrDataFields, String existingLoanName, List<RepaymentSchedule> repaymentList) {
		VerticalListBuilder verticalList = cmp.verticalList();
		try {
			JSONObject loanObj = getAllLoanDetails(custmrDataFields);

			List<ExistingGLLoanDetails> existingGLLoanDetails = custmrDataFields.getExistingGLLoanDetails();
			ExistingGLLoanDetails glLoan = null;
			if (existingGLLoanDetails == null || existingGLLoanDetails.isEmpty()) {
				logger.debug("No existingGLLoanDetails");
			} else {
				logger.debug("existingGLLoanDetails size: {}", existingGLLoanDetails.size());
				glLoan = existingGLLoanDetails.stream()
						.filter(Objects::nonNull)
						.filter(d -> existingLoanName.equals(d.getExistingLoanName()))
						.findFirst()
						.orElse(null);
			}

			int noOfEPIs = repaymentList.size();
			logger.debug("Number of Records: " + noOfEPIs);

			String emi = "";
			if( repaymentList.size() > 0) {
				emi = repaymentList.get(2).getTotalDue();
			}



			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("PrincipalLoanAgreementDate"),
					glLoan == null ? "NA" : dtStr(glLoan.getPrincipalLoanAgreementDate()), width60, width40));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("TypeOfLoanPrincipal"),
					glLoan == null ? "NA" : nvl(glLoan.getExistingLoanName()), width60, width40));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("LoanAmountPrincipal"),
					glLoan == null ? "NA" : amtStr(glLoan.getLoanAmount()), width60, width40));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("RateOfInterestPrincipal"),
					glLoan == null ? "NA" : amtStr(glLoan.getRateOfInterest()), width60, width40));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("PurposeEndUsePrincipal"),
					glLoan == null ? "NA" : nvl(glLoan.getPurposeEndUseOfLoan()), width60, width40));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("TenureOfLoanPrincipal"),
					glLoan == null ? "NA" : nvl(glLoan.getTenureOfLoan()), width60, width40));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("NumberOfInstalmentsPrincipal"),
					glLoan == null ? "NA" : nvl(glLoan.getNumberOfInstalments()), width60, width40));
//			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("InstalmentAmountPrincipal"),
//					glLoan == null ? "NA" : amtStr(glLoan.getInstalmentAmount()), width60, width40));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("InstalmentAmountPrincipal"),
					glLoan == null ? "NA" : amtStr(glLoan.getInstalmentAmount()), width60, width40));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("RepaymentFrequencyPrincipal"),
					glLoan == null ? "NA" : nvl(glLoan.getRepaymentFrequency()), width60, width40));
//			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("ModeOfRepaymentPrincipal"),
//					glLoan == null ? "NA" : nvl(glLoan.getModeOfRepayment()), width60, width40));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("ModeOfRepaymentPrincipal"),
					"Cash or Digital Payment in Kendra Meeting", width60, width40));

		} catch (Exception e) {
			logger.error("error - getMainLoanDetailsAdd");
			logger.error(e.getMessage());
		}
		logger.debug("MainLoanDetailsAdd added");
		return verticalList;
	}

	private String nvl(Object value) {
		if (value == null) return "NA";
		String s = String.valueOf(value).trim();
		return s.isEmpty() ? "NA" : s;
	}

	private String amtStr(BigDecimal value) {
		return value == null ? "NA" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}

	private String dtStr(LocalDate value) {
		return value == null ? "NA" : value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}



	private String getStageTimestamp(CustomerDataFields req, String stageName) {
		if (req.getApplicationTimelineDtl() == null || stageName == null) {
			return "";
		}

		String latestTs = "";

		for (ApplicationTimelineDtl timeline : req.getApplicationTimelineDtl()) {

			if (timeline == null || timeline.getStage() == null) {
				continue;
			}

			if (stageName.equalsIgnoreCase(timeline.getStage().trim())
					&& timeline.getTimeStamp() != null
					&& !timeline.getTimeStamp().trim().isEmpty()) {
				// keep the last matching entry - a stage can be revisited on rework
				latestTs = timeline.getTimeStamp().trim();
			}
		}

		return latestTs;
	}


	private ComponentBuilder<?, ?> getAdditionalLoanDetailsAdd(JSONObject keysForContent,
															   CustomerDataFields custmrDataFields, String productName, String sactionedDateStr, List<RepaymentSchedule> repaymentList) {
		VerticalListBuilder verticalList = cmp.verticalList();
		Gson gsonObj = new Gson();
		try {
			JSONObject loanObj = getAllLoanDetails(custmrDataFields);

			CibilDetailsPayload cibilPayload = CommonUtils
					.resolveCibilPayload(custmrDataFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId, gsonObj);
			logger.debug("CreditDetailsPayload Payload add : " + cibilPayload);

			String processingFees = "";
			if (cibilPayload != null) {
				processingFees = cibilPayload.getProcessingFees();
			}

			// TODO replace null with the real sanction stage name
			String sanctionDate = getStageTimestamp(custmrDataFields, null);

//			String typeOfLoan = "";
//			if (custmrDataFields.getApplicationMaster() != null) {
//				typeOfLoan = CommonUtils.getDefaultValue(
//						custmrDataFields.getApplicationMaster().getProductName());
//			}

			int noOfEPIs = repaymentList.size();
			logger.debug("Number of Records: " + noOfEPIs);




			String emi = "";
			if( repaymentList.size() > 0) {
				emi = repaymentList.get(2).getTotalDue();
			}

			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("SupplementaryLoanAgreementDate"),
					sactionedDateStr, width40, width60
			));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("SanctionLetterDated"),
					sactionedDateStr, width40, width60
			));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("TypeOfLoanAdditional"),
					"Grameen "+productName, width40, width60
			));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("LoanAmountAdditional"),
					loanObj.optString("loanAmountPrincipal", ""), width40, width60
			));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("RateOfInterestAdditional"),
					loanObj.optString("roiPrincipal", ""), width40, width60
			));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("PurposeEndUseAdditional"),
					loanObj.optString("loanPurposePrincipal", ""), width40, width60
			));
//			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("TenureOfLoanAdditional"),
//					loanObj.optString("tenurePrincipal", ""), width40, width60
//			));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("TenureOfLoanAdditional"),
					loanObj.optString("numberOfInstalmentsPrincipal", ""), width40, width60
			));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("NumberOfInstalmentsAdditional"),
					String.valueOf(noOfEPIs), width40, width60
			));



//			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("InstalmentAmountAdditional"),
//					loanObj.optString("instalmentAmountPrincipal", ""), width40, width60
//			));

			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("InstalmentAmountAdditional"),
					CommonUtils.amountFormat(emi) , width40, width60
			));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("RepaymentFrequencyAdditional"),
					loanObj.optString("repaymentFrequencyPrincipal", ""), width40, width60
			));
//			verticalList.add(createTwoHorizontalList(keysForContent.getString("ModeOfRepaymentAdditional"),
//					loanObj.optString("modeOfRepaymentPrincipal", "")
//			));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("ModeOfRepaymentAdditional"),
					"Cash or Digital Payment in Kendra Meeting", width40, width60
			));
//			verticalList.add(createTwoHorizontalList(keysForContent.getString("ProcessingFeesChargesGST"),
//					keysForContent.optString("ProcessingFeesChargesGSTAmount", "")
//			));
//			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("ProcessingFeesChargesGST"),
//					processingFees, width40, width60
//			));
            verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("ProcessingFeesChargesGST"),
                    "Processing Fee at 1.27% of the loan amount + applicable GST, will be netted off from disbursement amount", width40, width60
            ));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("StampDutyCharges"),
					"NA", width40, width60
			));
			verticalList.add(createTwoHorizontalListForLoanAgreement(keysForContent.getString("LatePaymentChargesGST"),
					"Rs NIL- per instalment (Inclusive GST)", width40, width60
			));

//            Processing Fee at 1.27% of the loan amount + applicable GST, will be netted off from disbursement amount

		} catch (Exception e) {
			logger.error("error - getAdditionalLoanDetailsAdd");
			logger.error(e.getMessage());
		}
		logger.debug("AdditionalLoanDetailsAdd added");
		return verticalList;
	}


	private ComponentBuilder<?, ?> createFourHorizontalList(String Key1, String value1, String Key2, String value2) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(boldTextWithBorder));
		horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle));
		horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(boldTextWithBorder));
		horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle));

		return horizontalList;

	}

	private ComponentBuilder<?, ?> createTwoHorizontalList(String Key, String value) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

//			horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(25));
		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(25));
		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(75));
		return horizontalList;

	}

	private ComponentBuilder<?, ?> createOneHorizontalList(String Key, String value) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

//			horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(25));
		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(25));

		return horizontalList;

	}


	private ComponentBuilder<?, ?> getLoanSanctionAndPreclosureSideBySide(
			JSONObject keysForContent, CustomerDataFields custmrDataFields) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		try {

			// LEFT SIDE
			ComponentBuilder<?, ?> loanSanctionSection =
					cmp.verticalList().add(getLoanSanctionAmountAdd(keysForContent, custmrDataFields));

			// RIGHT SIDE
//			ComponentBuilder<?, ?> preclosureSection =
//					cmp.verticalList().add(cmp.text(keysForContent.getString("Preclosure")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
//							.add(getPreclosureAdd(keysForContent));
			ComponentBuilder<?, ?> preclosureSection =
					cmp.verticalList().add(getPreclosureAdd(keysForContent, custmrDataFields, ""));

			// PUT BOTH SECTIONS SIDE BY SIDE
			horizontalList.add(
					loanSanctionSection,

					preclosureSection
			);

		} catch (Exception e) {
			logger.error(
					"Error - getLoanSanctionAndPreclosureSideBySide",
					e
			);
		}
		return horizontalList;
	}
	//----END----

	private ComponentBuilder<?, ?> postdeclaration(JSONObject keysForContent) {

		VerticalListBuilder verticalList = cmp.verticalList();

		verticalList.add(createTwoHorizontalList(keysForContent.getString("borrowers"),
				keysForContent.getString("companyAuthorization"), boldText));
		verticalList.add(createTwoHorizontalList(keysForContent.getString(Constants.DECLARATION_SIGNATURE),
				keysForContent.getString(Constants.DECLARATION_SIGNATURE), null));
		verticalList.add(createTwoHorizontalList(keysForContent.getString("declarationName") + applicantName,
				keysForContent.getString("declarationNameWithAddress"), null));
		verticalList.add(createTwoHorizontalList(keysForContent.getString(Constants.DECLARATION_SIGNATURE),
				keysForContent.getString("empId")+ bmId, null));
		verticalList.add(createTwoHorizontalList(keysForContent.getString("declarationName") + coApplicantName,
				keysForContent.getString("designation"), null));

		return verticalList;
	}

	private ComponentBuilder<?,?> getLoanAccountNumber(CustomerDataFields customerDeets, JSONObject keysForContent){
		LoanDetails loanDetails = customerDeets.getLoanDetails();
		String t24LoanId = loanDetails.getT24LoanId();

		VerticalListBuilder verticalList = cmp.verticalList();

		Map<String, Integer> loanAccountNumberRow = new LinkedHashMap<>();
		loanAccountNumberRow.put(keysForContent.optString("loanApplicationNumber","Loan Account Number"), 50);
		loanAccountNumberRow.put(t24LoanId, 50);

		verticalList.add(createHorizontalList(loanAccountNumberRow));

		return verticalList;
	}


	private ComponentBuilder<?, ?> createTwoHorizontalList(String Key, String value, ReportStyleBuilder style) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(style));
		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(style));

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


		}
		System.out.println(ds2);
		return ds2;
	}

	private ComponentBuilder<?, ?> Signatory(JSONObject keysForContent) {
		VerticalListBuilder verticalList = cmp.verticalList();
		String blank = " ............";

		verticalList.add(cmp.horizontalList(
				cmp.text(keysForContent.getString("applicantNameAndSign") + ": " + applicantName+" &"+blank)
						.setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)))
		);
		verticalList.add(cmp.horizontalList(
				cmp.text(keysForContent.getString("coapplicantNameAndSign") + ": " + coApplicantName+" &"+blank)
						.setFixedHeight(15)
						.setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)))
		);
		logger.debug("authorizedSignatory" + keysForContent.getString("authorizedSignatory"));
		verticalList.add(cmp.horizontalList(
				cmp.text(keysForContent.getString("authorizedSignatory") + ": " + blank)
						.setFixedHeight(15)
						.setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)))
		);
		return verticalList;
	}




	private ComponentBuilder<?, ?> SignatoryAdd(JSONObject keysForContent, CustomerDataFields custmrDataFields) {
		VerticalListBuilder verticalList = cmp.verticalList();
		String blank = " ............";


		if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(custmrDataFields.getApplicationMaster().getProductCode())){
			verticalList.add(cmp.horizontalList(
					cmp.text(keysForContent.getString("borrower") + " " + keysForContent.getString("signature") +blank)
							.setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)))
			);
			verticalList.add(cmp.horizontalList(
					cmp.text(keysForContent.getString("coborrower") + " " + keysForContent.getString("signature") +blank)
							.setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)))
			);
		}
		else{
			verticalList.add(cmp.horizontalList(
					cmp.text(keysForContent.getString("borrower") + " " + keysForContent.getString("signature") +blank)
							.setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)))
			);
		}

		return verticalList;
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

	private ComponentBuilder<?, ?> createTwoHorizontalListForLoanAgreement(String Key, String value, int width1,
																			int width2) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(width1));
		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(width2));

		return horizontalList;

	}


}
