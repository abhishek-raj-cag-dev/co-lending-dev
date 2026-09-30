package com.iexceed.appzillonbanking.cob.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import com.iexceed.appzillonbanking.cob.core.domain.ab.LoanDetails;
import com.iexceed.appzillonbanking.cob.core.utils.WorkflowStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
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
			leftStyle;

	static String space = "\u00a0\u00a0\u00a0";
	private String applicantName = "";
	private String coApplicantName = "";
	private String bmId = "-";

	public LoanAgreement() {

		borderedStyle = stl.style(stl.penThin()).setPadding(5);
		boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
		boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		boldText = stl.style().bold();
		boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

		rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
		leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

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
		subReport19.setDataSource(emptyDataSource);
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
