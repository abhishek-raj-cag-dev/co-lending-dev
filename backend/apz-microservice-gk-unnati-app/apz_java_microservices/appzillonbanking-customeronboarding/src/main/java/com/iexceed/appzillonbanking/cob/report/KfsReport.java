package com.iexceed.appzillonbanking.cob.report;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.LoanDetails;
import com.iexceed.appzillonbanking.cob.core.payload.*;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedCDHLead;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.ReportStyleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.*;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JREmptyDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

@Component
public class KfsReport {

	private static final Logger logger = LogManager.getLogger(KfsReport.class);

	private StyleBuilder borderedStyle, boldText, boldCenteredStyle, boldTextWithBorder, boldLeftStyle, rightStyle, leftStyle, mtop;

	private String applicantCustId="";
	private String coApplicantCustId ="";
	private String applicantName = "";
	private String coApplicantName = "";

	String appltGender ="";
	String coAppltGender ="";

	static String space = "\u00a0\u00a0\u00a0";
	private String BLANK_STRING = " ";
	private String productName = "";
	int width30 = 30;
	int width70 = 70;

	public KfsReport() {

		borderedStyle = stl.style(stl.penThin()).setPadding(5);
		boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
		boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		boldText = stl.style().bold();
		boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

		rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
		leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		mtop = stl.style().setPadding(100);



		// StyleBuilder headerStyle =
		// stl.style().setFontSize(20).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
//		rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
//		leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

	}

	//kmSubmDateStr as sanctionDate
	public byte[] generatePdfForDbKit(JSONObject keysForContent, CustomerDataFields customerFields, String filePath, String productName1, String productName2, List<RepaymentSchedule> repaymentList, String sanctionDateStr) throws DRException, IOException {
		logger.debug("onEntrty :: generatePdfForDbKit");
		productName = productName2;
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

			CibilDetailsPayload cibilDetailsPayload = CommonUtils
					.resolveCibilPayload(customerFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId, gsonObj);
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
			subReport1.title(getBasicAppnDetails(keysForContent, customerFields, cibilDetailsPayload, noOfEPIs, emi, sanctionDateStr)).title(cmp.text(""));
			subReport2.title(getBasicAppnDetails2(keysForContent,cibilDetailsPayload)).title(cmp.text(""));
			subReport3.title(getBasicAppnDetails3(keysForContent)).title(cmp.text(""));

			//Computation of APR
			subReport4.title(cmp.text(keysForContent.getString("ComputationOfAPR")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));
			subReport5.title(getComputationDetails(keysForContent, customerFields, cibilDetailsPayload, repaymentList, noOfEPIs, emi, sanctionDateStr)).title(cmp.text(""));

			//Insurance coverage:
			insuranceCoverageReport.title(cmp.text(keysForContent.getString("InsuranceCoverage")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));
			insuranceCoverageReport.title(getInsuranceCoverageDetails(keysForContent, cibilDetailsPayload)).title(cmp.text(""));

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

	public byte[] generatePdfForDbKitAdd(JSONObject keysForContent, CustomerDataFields customerFields, String filePath, String productName1, List<RepaymentSchedule> repaymentList, String sanctionDateStr) throws DRException, IOException {
		logger.debug("onEntrty :: generatePdfForDbKitAdd");
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

			CibilDetailsPayload cibilDetailsPayload = CommonUtils
					.resolveCibilPayload(customerFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId, gsonObj);
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
			subReport1.title(getBasicAppnDetails(keysForContent, customerFields, cibilDetailsPayload, noOfEPIs, emi, sanctionDateStr)).title(cmp.text(""));
			subReport2.title(getBasicAppnDetails2(keysForContent,cibilDetailsPayload)).title(cmp.text(""));
			subReport3.title(getBasicAppnDetails3(keysForContent)).title(cmp.text(""));

			//Computation of APR
			subReport4.title(cmp.text(keysForContent.getString("ComputationOfAPR")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));
			subReport5.title(getComputationDetails(keysForContent, customerFields, cibilDetailsPayload, repaymentList, noOfEPIs, emi, sanctionDateStr)).title(cmp.text(""));

			//Insurance coverage:
			insuranceCoverageReport.title(cmp.text(keysForContent.getString("InsuranceCoverage")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));
			insuranceCoverageReport.title(getInsuranceCoverageDetails(keysForContent, cibilDetailsPayload)).title(cmp.text(""));

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



	// @author Abhishek.Raj.CAG



	private ComponentBuilder<?, ?> createTwoHorizontalList(String Key, String value) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

//			horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(25));
		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(25));
		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(75));
		return horizontalList;

	}

	public byte[] generatePdfForDbKitKfsSummary(ApplicationMaster applicationMasterData, JSONObject keysForContent, CustomerDataFields customerFields, String filePath, String productName1, String productName, List<RepaymentSchedule> repaymentList, String sanctionDateStr) throws DRException, IOException {
		logger.debug("onEntrty :: generatePdfForDbKitKfsSummary");
		productName = productName;
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


//			CibilDetailsPayload cibilPayloadCoApp = null;
			CibilDetailsPayload cibilPayloadCoApp = CommonUtils.resolveCibilPayload(customerFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId, gsonObj);

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
			subReport1.title(getLoanTableDetails(keysForContent, applicationMasterData, productName, customerFields, repaymentList));

//			subReport2.title(cmp.text(keysForContent.getString("Details1")).setMarkup(Markup.HTML).setStyle(leftStyle));
			if(Constants.FAMILY_WELFARE_PRODUCT_CODE
					.equals(customerFields.getApplicationMaster().getProductCode()) || Constants.UNNATI_EMERGENCY_PRODUCT_CODE
					.equals(customerFields.getApplicationMaster().getProductCode())){
				subReport2.title(cmp.text(keysForContent.getString("Details2")).setMarkup(Markup.HTML).setStyle(leftStyle));
			}else{
			subReport2.title(cmp.text(keysForContent.getString("Details1")).setMarkup(Markup.HTML).setStyle(leftStyle));
			}
//			subReport5.title(cmp.text("").setMarkup(Markup.HTML).setStyle(mtop));
//			subReport4.title(Signatory(keysForContent, customerFields));
			subReport4.title(cmp.text(keysForContent.getString("BorrowerNameSign") + ": " +"..........")
					.setMarkup(Markup.HTML).setStyle(leftStyle));
			if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(customerFields.getApplicationMaster().getProductCode())) {
				subReport4.title(cmp.text(keysForContent.getString("CoBorrowerNameSign") + ": " + "..........")
						.setMarkup(Markup.HTML).setStyle(leftStyle));
			}
			subReport4.title(cmp.text(keysForContent.getString("AuthorizedSignatory") + ": " +"..........")
					.setMarkup(Markup.HTML).setStyle(rightStyle));

//			old
			report
					.addSummary(cmp.subreport(subReport)).addSummary(cmp.subreport(subReport1))
					.addSummary(cmp.subreport(subReport2)).addSummary(cmp.subreport(subReport5)).addSummary(cmp.subreport(subReport4));
//			new
//			report
//					.setPageFormat(PageType.A4, PageOrientation.PORTRAIT)
//					.setPageMargin(DynamicReports.margin(30))
//					.pageFooter(
//							cmp.verticalList(
//											Signatory(keysForContent, customerFields)
//									)
//									.setFixedHeight(90)
//									.setStyle(stl.style().setTopPadding(10))
//					)
//					.setDataSource(new JREmptyDataSource(1))
//					.addSummary(cmp.subreport(subReport))
//					.addSummary(cmp.subreport(subReport1))
//					.addSummary(cmp.subreport(subReport2));

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


	private ComponentBuilder<?, ?> Signatory(JSONObject keysForContent, CustomerDataFields custmrDataFields) {
		VerticalListBuilder verticalList = cmp.verticalList();
		String blank = " ............";

		verticalList.add(cmp.horizontalList(
				cmp.text(keysForContent.getString("BorrowerNameSign") + ": " +""+blank)
						.setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)))
		);
		if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(custmrDataFields.getApplicationMaster().getProductCode())){
			verticalList.add(cmp.horizontalList(
					cmp.text(keysForContent.getString("CoBorrowerNameSign") + ": " +""+blank)
							.setFixedHeight(15)
							.setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)))
			);
		}

		verticalList.add(cmp.horizontalList(
				cmp.text(keysForContent.getString("AuthorizedSignatory") + ": " +""+blank)
						.setFixedHeight(15)
						.setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)))
		);

		logger.debug("authorizedSignatory" + keysForContent.getString("authorizedSignatory"));
		verticalList.add(cmp.horizontalList(
				cmp.text(keysForContent.getString("authorizedSignatory") + ": " +blank)
						.setFixedHeight(15)
						.setMarkup(Markup.HTML).setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)))
		);
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
			Gson gsonObj = new Gson();
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
			jsnObj.put("repaymentFrequencyPrincipal", CommonUtils.getDefaultValue(repaymentFrequency));
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


	private ComponentBuilder<?, ?> getLoanTableDetails(JSONObject keysForContent,
													   ApplicationMaster applicationMasterData,
													   String productName1,
													   CustomerDataFields customerFields,  List<RepaymentSchedule> repaymentList) {
		VerticalListBuilder verticalList = cmp.verticalList();
		String productName = productName1;


		String loanAmount = "";
		String tenure     = "";
		String frequency  = "";
		String interest   = "";

		try {
			Gson gsonObj = new Gson();

			// 1. derive the cust IDs this class doesn't hold
			String appCustId   = "";
			String coAppCustId = "";
			if (customerFields.getCustomerDetailsList() != null) {
				for (CustomerDetails custDtl : customerFields.getCustomerDetailsList()) {
					if (Constants.APPLICANT.equalsIgnoreCase(custDtl.getCustomerType())) {
						appCustId = String.valueOf(custDtl.getCustDtlId());
					} else if ("Co-App".equalsIgnoreCase(custDtl.getCustomerType())) {
						coAppCustId = String.valueOf(custDtl.getCustDtlId());
					}
				}
			}

			// 2. loan amount straight off the loan record
			if (customerFields.getLoanDetails() != null) {
				loanAmount = CommonUtils.amountFormat(
						String.valueOf(customerFields.getLoanDetails().getLoanAmount()));
			}

			// 3. tenure / frequency / roi off the CIBIL payload
			CibilDetailsPayload cibil = CommonUtils.resolveCibilPayload(
					customerFields.getCibilDetailsWrapperList(), coAppCustId, appCustId, gsonObj);

			if (cibil != null) {
				tenure    = CommonUtils.getDefaultValue(cibil.getFinalTenure());
				frequency = CommonUtils.getDefaultValue(cibil.getRepaymentFrequency());
				interest  = (cibil.getRoi() == null) ? "" : cibil.getRoi().toString();
			}

//			String emi = "";
//			if (customerFields.getLoanDetails() != null
//					&& customerFields.getLoanDetails().getMonthlyEmi() != null) {
//				emi = CommonUtils.amountFormat(
//						customerFields.getLoanDetails().getMonthlyEmi().toPlainString());
//			}

			String processingFees = "";
			if (cibil != null) {
				processingFees = cibil.getProcessingFees();
			}

			String rs = keysForContent.getString("Rs");
			String apr = String.valueOf(cibil.getEir() == null ? "": cibil.getEir().toString());
			String repaymentFrequency = "";
			JSONObject loanObj = getAllLoanDetails(customerFields);

			// ---- Insurance flags now sourced from CDH lead (values are Y / N) ----
			String appInsuranceFlag = "N";
			String coAppInsuranceFlag = "N";

			UnnatiIexceedCDHLead cdhLead = customerFields.getCdhLeadDetails();
			if (cdhLead != null) {
				logger.debug("insuranceReqd : {} , coInsuranceReqd : {}",
						cdhLead.getInsuranceReqd(), cdhLead.getCoInsuranceReqd());
				appInsuranceFlag = StringUtils.isBlank(cdhLead.getInsuranceReqd()) ? "N" : cdhLead.getInsuranceReqd().trim();
				coAppInsuranceFlag = StringUtils.isBlank(cdhLead.getCoInsuranceReqd()) ? "N" : cdhLead.getCoInsuranceReqd().trim();
			} else {
				logger.debug("cdhLeadDetails is null - defaulting insurance flags to N");
			}

			boolean isAppInsured = "Y".equalsIgnoreCase(appInsuranceFlag);
			boolean isCoAppInsured = "Y".equalsIgnoreCase(coAppInsuranceFlag);



			// Premiums follow the flags
			String apptPrInsuAmt = (isAppInsured && cibil != null)
					? cibil.getInsuranceChargeMember() : "";
			String coAppPrInsuAmt = (isCoAppInsured && cibil != null)
					? cibil.getInsuranceChargeSpouse() : "";

			String applicantInsuranceAmt =
					(apptPrInsuAmt == null || apptPrInsuAmt.isEmpty())
							? "NA"
							: apptPrInsuAmt;

			int noOfEPIs = repaymentList.size();
			logger.debug("Number of Records: " + noOfEPIs);
			String emi = "";
			if( repaymentList.size() > 0) {
				emi = repaymentList.get(2).getTotalDue();
			}

			repaymentFrequency = loanObj.optString("repaymentFrequencyPrincipal", "");

			verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("LoanProductName"), "Grameen " + productName, width70, width30));
			verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("LoanAmountSanctioned"),
					rs + loanAmount + "/-", width70, width30));
			verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("RepaymentFrequency"), repaymentFrequency, width70, width30));
			verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("NumberofInstalments"), tenure, width70, width30));
//			verticalList.add(createTwoHorizontalList(keysForContent.getString("InstalmentAmount"), rs + "_________"));
			verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("InstalmentAmount"),
					emi.isEmpty() ? rs + "_________" : rs + emi + "/-", width70, width30));
			verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("Interest"),
					interest.isEmpty() ? "" : interest + " %", width70, width30));
			verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("loantenor"), tenure, width70, width30));
			verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("ProcessingFees"),
					rs + " " + processingFees, width70, width30));
			verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("InsuranceCoverage"), rs + " " + applicantInsuranceAmt, width70, width30));
			verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("StampdutyCharges"), "0", width70, width30));
			verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("LatePaymentChargesGST"),
					"NA", width70, width30));
			verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("AnnualPercentageRate"), apr, width70, width30));

		} catch (Exception e) {
			logger.error("error - getLoanTableDetails", e);
		}
		logger.debug("getLoanTableDetails added");
		return verticalList;
	}

	private ComponentBuilder<?, ?> createTwoHorizontalListForSanctionLetter(String Key, String value, int width1,
																			int width2) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(width1));
		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(width2));

		return horizontalList;

	}

	private ComponentBuilder<?, ?> getLoanTableDetailsOld(JSONObject keysForContent, ApplicationMaster applicationMasterData, String productName1, CustomerDataFields customerFields) {
		VerticalListBuilder verticalList = cmp.verticalList();
		String productName = productName1;
		String loanAmount = "";
		try {
			verticalList.add(createTwoHorizontalList(keysForContent.getString("LoanProductName"),
					productName
			));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("LoanAmountSanctioned"),
					keysForContent.getString("Rs")
			));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("RepaymentFrequency"),
					" "
			));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("NumberofInstalments"),
					" "
			));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("InstalmentAmount"),
					keysForContent.getString("Rs") + "_________"
			));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("Interest"),
					""
			));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("loantenor"),
					keysForContent.getString("Rs") + "_________"
			));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("ProcessingFees"),
					"_______________%&"+ keysForContent.getString("Rs") + "_______________"
			));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("InsuranceCoverage"),
					keysForContent.getString("Rs") + "_________"
			));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("StampdutyCharges"),
					keysForContent.getString("Rs") + "_________"
			));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("LatePaymentChargesGST"),
					keysForContent.getString("Rs") +"_________/-"+ keysForContent.getString("Perinstalment")
			));
			verticalList.add(createTwoHorizontalList(keysForContent.getString("AnnualPercentageRate"),
					""
			));
		} catch (Exception e) {
			logger.error("error - getLoanTableDetails");
			logger.error(e.getMessage());
		}
		logger.debug("PreclosureAdd added");
		return verticalList;
	}


	public Response generatePdf(JSONObject keysForContent, CustomerDataFields customerFields, String productName1, List<RepaymentSchedule> repaymentList, String sanctionDateStr, String language, boolean isSmsReport) throws DRException, IOException {
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


			CibilDetailsPayload cibilDetailsPayload = CommonUtils.resolveCibilPayload(customerFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId, gsonObj);

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
			subReport1.title(getBasicAppnDetails(keysForContent, customerFields, cibilDetailsPayload, noOfEPIs, emi, sanctionDateStr)).title(cmp.text(""));
			subReport2.title(getBasicAppnDetails2(keysForContent, cibilDetailsPayload)).title(cmp.text(""));
			subReport3.title(getBasicAppnDetails3(keysForContent)).title(cmp.text(""));

			//Computation of APR
			subReport4.title(cmp.text(keysForContent.getString("ComputationOfAPR")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));
			subReport5.title(getComputationDetails(keysForContent, customerFields, cibilDetailsPayload, repaymentList, noOfEPIs, emi, sanctionDateStr)).title(cmp.text(""));
			//Insurance coverage:
			insuranceCoverageReport.title(cmp.text(keysForContent.getString("InsuranceCoverage")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));
			insuranceCoverageReport.title(getInsuranceCoverageDetails(keysForContent, cibilDetailsPayload)).title(cmp.text(""));

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

				if (isValidLanguage && !isSmsReport) {
					// Generate HTML in-memory, no file creation
					logger.debug("Generating HTML report for language: {}, since this is not for SMS, isSms: {}",  language, isSmsReport);
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
					logger.debug("Generating PDF report for language: {}, since this is for SMS, isSms: {}",  language, isSmsReport);
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
		if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equalsIgnoreCase(productCode) ||
				Constants.UNNATI_RESTART_PRODUCT_CODE.equalsIgnoreCase(productCode) ||
				Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equalsIgnoreCase(productCode) ||
				Constants.FAMILY_WELFARE_PRODUCT_CODE.equalsIgnoreCase(productCode)
		){
			row3.put(keysForContent.getString("typeOfLoan") + BLANK_STRING + "Grameen " + productName, 50);
		}
		else if(Constants.Vishesh_LOAN_PRODUCT_CODE.equalsIgnoreCase(productCode)){
			row3.put(keysForContent.getString("typeOfLoan") + BLANK_STRING + "Grameen Unnati Lite", 50);
		}
		else{
		row3.put(keysForContent.getString("typeOfLoan") + BLANK_STRING + productName, 50);
		}
//		row3.put(keysForContent.getString("typeOfLoan") + BLANK_STRING + "Grameen " + productName, 50);
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
