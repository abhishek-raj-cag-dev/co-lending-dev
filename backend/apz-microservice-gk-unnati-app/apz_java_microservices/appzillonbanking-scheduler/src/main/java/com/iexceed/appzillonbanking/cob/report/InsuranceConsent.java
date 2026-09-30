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
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.iexceed.appzillonbanking.cob.core.payload.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.BankDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.InsuranceDetails;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.BankDetailsRepository;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.WorkflowActions;
import com.iexceed.appzillonbanking.cob.nesl.repository.ab.EnachRepository;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.ReportStyleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.Markup;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;

public class InsuranceConsent {
    private static final Logger logger = LogManager.getLogger(InsuranceConsent.class);
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
    String appltMobNo ="-";
    String coAppltMoNo ="";

    public InsuranceConsent() {

        borderedStyle = stl.style(stl.penThin()).setPadding(5);
        boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
        boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        boldText = stl.style().bold();
        boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

        rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
        leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

    }

    public String generatePdfForDbKit(JSONObject keysForContent, String filePath, CustomerDataFields custmrDataFields, String language, String userNameBM) throws DRException, IOException, JRException {


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


        report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));

        /* Basic Application Details */
        CustomerDetailsPayload payload1 = new CustomerDetailsPayload();
        CustomerDetailsPayload payload2 =new CustomerDetailsPayload();
        String custType = "";
        Gson gsonObj = new Gson();
        for(CustomerDetails custDtl : custmrDataFields.getCustomerDetailsList()) {
            logger.debug("customer Type : " + custDtl.getCustomerType());
            if(custDtl.getCustomerType().equalsIgnoreCase("Applicant")) {
                applicantCustId = String.valueOf(custDtl.getCustDtlId());
                logger.debug("applicantCustId : " + applicantCustId);
                applicantName = custDtl.getCustomerName();
                custType = custDtl.getCustomerType();
                appltMobNo = custDtl.getMobileNumber();
                payload1  = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                logger.debug("custApplicantPayload :" + payload1);
                applicantCustDtls = custDtl;
                appltGender = payload1.getGender();
            }else if(custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
                coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
                coApplicantName = custDtl.getCustomerName();
                custType = custDtl.getCustomerType();
                coAppltMoNo = custDtl.getMobileNumber();
                logger.debug("coApplicantCustId : " + coApplicantCustId);
                payload2  = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                logger.debug("custCo-ApplicantPayload :" + payload2);
                coAppltGender = payload2.getGender();
                coApplicantCustDtls = custDtl;
            }
        }


        String customerId = custmrDataFields.getApplicationMaster().getProductCode()
                .equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE) ?
                custmrDataFields.getApplicationMaster().getApplicantT24Id() :
                custmrDataFields.getApplicationMaster().getSearchCode2();
        InsuranceDetails coApplicant = null;
        InsuranceDetailsPayload insurancePayload1 =new InsuranceDetailsPayload();
        InsuranceDetailsPayload insurancePayload2 =new InsuranceDetailsPayload();
        for(InsuranceDetailsWrapper insurer : custmrDataFields.getInsuranceDetailsWrapperList()) {
            if(String.valueOf(insurer.getInsuranceDetails().getCustDtlId()).equals(applicantCustId)){
                insurancePayload1  = gsonObj.fromJson(insurer.getInsuranceDetails().getPayloadColumn(), InsuranceDetailsPayload.class);
                logger.debug("InsuranceDetailsApplicantPayload : " + insurancePayload1);
            }else if(String.valueOf(insurer.getInsuranceDetails().getCustDtlId()).equals(coApplicantCustId)){
                coApplicant = insurer.getInsuranceDetails();
                insurancePayload2 = gsonObj.fromJson(insurer.getInsuranceDetails().getPayloadColumn(), InsuranceDetailsPayload.class);
                logger.debug("InsuranceDetailsCo-ApplicantPayload : " + insurancePayload2);
            }
        }

        subReport.title(cmp.text(keysForContent.getString("applicationName1"))
                .setMarkup(Markup.HTML)
                .setStyle(boldCenteredStyle.setFontSize(14).underline()));
        subReport.title(cmp.text(keysForContent.getString("applicationName2"))
                .setMarkup(Markup.HTML)
                .setStyle(boldCenteredStyle.setFontSize(14).underline()));
        subReport1.title(cmp.text(""));
//		subReport1.title(cmp.text(keysForContent.getString("memberId")+"_____________"+"\t\t\t"+ keysForContent.getString("loanAccount")+"______________").setStyle(boldLeftStyle.setFontSize(14)));
        subReport1.title(
                cmp.horizontalList(
                        cmp.text(keysForContent.getString("memberId") + " "+ customerId)
                                .setMarkup(Markup.HTML)
                                .setStyle(leftStyle),
                        cmp.text(keysForContent.getString("loanAccount") +" "+ custmrDataFields.getLoanDetails().getT24LoanId())
                                .setMarkup(Markup.HTML)
                                .setStyle(rightStyle)
                )
        );
        subReport2.title(getBorrowerTable(keysForContent, payload1, payload2, appltMobNo,coAppltMoNo, custmrDataFields)).title(cmp.text(""));

        subReport2.title(cmp.text(keysForContent.getString("consent"))
                .setMarkup(Markup.HTML)
                .setStyle(leftStyle));

        String consent1 = keysForContent.getString("consent1");
        String insurerName = keysForContent.getString("insurerName");
        String insuProdctName = keysForContent.getString("insuProdctName");
        String finalConsent1 = consent1.replace("<insurerName>", insurerName).replace("<productName>", insuProdctName);
        logger.debug("finalNote : " + finalConsent1);


        subReport2.title(cmp.text(finalConsent1)
                .setMarkup(Markup.HTML)
                .setStyle(leftStyle));
        subReport2.title(cmp.text(keysForContent.getString("consent2"))
                .setMarkup(Markup.HTML)
                .setStyle(leftStyle));
        subReport2.title(cmp.text(""));
        subReport2.title(getBorrowerNameSignatureForCombinedBooklet(keysForContent)).title(cmp.text(""));

        subReport2.title(cmp.text(keysForContent.getString("declaration"))
                .setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(12)));
        subReport4.title(getNomineeDeclaration(keysForContent, insurancePayload1, insurancePayload2));

        subReport5.title(cmp.text(keysForContent.getString("ageCriteria")).setMarkup(Markup.HTML).setStyle(leftStyle));

        subReport5.title(getMemberApointeeDetails(keysForContent));
        subReport5.title(cmp.text(keysForContent.getString("notMandatory")).setMarkup(Markup.HTML).setStyle(leftStyle));

        subReport6.title(cmp.text(keysForContent.getString("splitPaymentConsent")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(12)));
        subReport6.title(cmp.text(keysForContent.getString("splitPaymentConsent1")).setMarkup(Markup.HTML).setStyle(leftStyle));
        subReport6.title(cmp.text(""));
        subReport6.title(cmp.text(keysForContent.getString("splitPaymentConsent2")).setMarkup(Markup.HTML).setStyle(leftStyle));
        subReport6.title(getBorrowerNameSignatureForCombinedBooklet(keysForContent)).title(cmp.text(""));
        subReport7.title(cmp.text(keysForContent.getString("verifiedBy")+"").setMarkup(Markup.HTML).setStyle(boldLeftStyle));
        subReport7.title(Signatory(keysForContent, custmrDataFields, userNameBM));


        String serverImagePath = CommonUtils.getExternalProperties("images") + "gk-logo.png";
        logger.debug("serverImagePath :" + serverImagePath);

        report
                .setPageFormat(PageType.A4, PageOrientation.PORTRAIT)
                .setPageMargin(DynamicReports.margin(30))
                .pageHeader(
                        cmp.image(serverImagePath).setHorizontalImageAlignment(HorizontalImageAlignment.LEFT)
                                .setHorizontalImageAlignment(HorizontalImageAlignment.LEFT)
                                .setFixedDimension(100, 60) // Adjust size: width x height
                                .setStyle(leftStyle)
                )
                //    .pageFooter(cmp.pageNumber().setStyle(leftStyle))
                .setDataSource(new JREmptyDataSource(1))
                .detail(
                        cmp.verticalList(
                                cmp.subreport(subReport),
                                cmp.subreport(subReport1),
                                cmp.subreport(subReport2),
                                cmp.subreport(subReport3),
                                cmp.subreport(subReport16),
                                cmp.subreport(subReport4),
                                cmp.subreport(subReport5),
                                cmp.subreport(subReport6),
                                cmp.subreport(subReport7),
                                cmp.subreport(subReport8),
                                cmp.subreport(subReport9),
                                cmp.subreport(subReport10),
                                cmp.subreport(subReport11),
                                cmp.subreport(subReport12),
                                cmp.subreport(subReport13),
                                cmp.subreport(subReport14),
                                cmp.subreport(subReport15),
                                cmp.subreport(subReport16)
                        )
                );
		logger.debug("added all subreports to report builder");


		try {
			String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
			logger.debug("inputLanguage" + language);
			boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
					.anyMatch(lang -> lang.equalsIgnoreCase(language));
			if (!isValidLanguage) {
				try (FileOutputStream fos = new FileOutputStream(filePath)) {
					report.toPdf(fos);
				}
			} else {
				JasperPrint jasperPrint = report.toJasperPrint();

				HtmlExporter exporter = new HtmlExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

				ByteArrayOutputStream htmlOut = new ByteArrayOutputStream();
				SimpleHtmlExporterOutput output = new SimpleHtmlExporterOutput(htmlOut);

				// Embed images as Base64
				SimpleHtmlReportConfiguration reportConfig = new SimpleHtmlReportConfiguration();
				reportConfig.setEmbedImage(true); // crucial for images

				exporter.setConfiguration(reportConfig);
				exporter.setExporterOutput(output);

				// Export fully in memory
				exporter.exportReport();

				// Return HTML string
				return htmlOut.toString(StandardCharsets.UTF_8.name());

			}

		} catch (DRException e) {

		}
//	        fos.close();
		byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
		byte[] encodedBytes = Base64.getEncoder().encode(inputfile);
		// util.deleteFile(filePath);
		return new String(encodedBytes);

    }

    private ComponentBuilder<?, ?> getBorrowerTable(JSONObject keysForContent, CustomerDetailsPayload payload1, CustomerDetailsPayload payload2, String appltMobNo, String coAppltMoNo, CustomerDataFields custmrDataFields) {

        VerticalListBuilder verticalList = cmp.verticalList();
        String apptDateOfBirth = "";
        String coApptDateOfBirth = "";
        try {
            apptDateOfBirth = CommonUtils.formatDateWithInputOutput(payload1.getDob(), Constants.DATE_FORMAT_STD, Constants.DATE_FORMAT);
            coApptDateOfBirth = CommonUtils.formatDateWithInputOutput(payload2.getDob(), Constants.DATE_FORMAT_STD, Constants.DATE_FORMAT);
        } catch (Exception e) {
            logger.error("error while date formatting." + e.getMessage());
        }

        BigDecimal sanctionedLoanAmount = custmrDataFields.getLoanDetails().getSanctionedLoanAmount() != null ? custmrDataFields.getLoanDetails().getSanctionedLoanAmount() : BigDecimal.ZERO;

        String sanctionedAmount = custmrDataFields.getLoanDetails().getSanctionedLoanAmount() != null ? String.valueOf(custmrDataFields.getLoanDetails().getSanctionedLoanAmount()) : "NA";

        BigDecimal limit = BigDecimal.valueOf(200000);
        String insuranceSumAssured =
                String.valueOf(sanctionedLoanAmount.compareTo(limit) > 0 ? limit : sanctionedLoanAmount);

        CibilDetailsPayload applicantCibilPayload = new CibilDetailsPayload();
        CibilDetailsPayload coApplicantCibilPayload = new CibilDetailsPayload();
        Gson gsonObj = new Gson();
        for (CibilDetailsWrapper cibilDtl : custmrDataFields.getCibilDetailsWrapperList()) {
            if (String.valueOf(cibilDtl.getCibilDetails().getCustDtlId()).equals(applicantCustId)) {
                applicantCibilPayload = gsonObj.fromJson(cibilDtl.getCibilDetails().getPayloadColumn(), CibilDetailsPayload.class);
                logger.debug("applicantCibilPayload :" + applicantCibilPayload);
            } else if (String.valueOf(cibilDtl.getCibilDetails().getCustDtlId()).equals(coApplicantCustId)) {
                coApplicantCibilPayload = gsonObj.fromJson(cibilDtl.getCibilDetails().getPayloadColumn(), CibilDetailsPayload.class);
                logger.debug("coApplicantCibilPayload :" + coApplicantCibilPayload);
            }
        }
        String applicantInsurancePremiumAmount = coApplicantCibilPayload.getInsuranceChargeMember() != null ? String.valueOf(coApplicantCibilPayload.getInsuranceChargeMember()) : "NA";
        String coApplicantInsurancePremiumAmount = coApplicantCibilPayload.getInsuranceChargeSpouse() != null ? String.valueOf(coApplicantCibilPayload.getInsuranceChargeSpouse()) : "NA";

        String finalTenure = "NA";
        if (coApplicantCibilPayload.getFinalTenure() != null) {
            finalTenure = String.valueOf(coApplicantCibilPayload.getFinalTenure());
        }


        verticalList.add(createHorizontalList(keysForContent.getString("assuredLife"), keysForContent.getString("name"), keysForContent.getString("age"), keysForContent.getString("gender"), keysForContent.getString("mobileNo"), keysForContent.getString("emailId"), keysForContent.getString("sumAssured"), keysForContent.getString("insurancePremiumAmount"),
                keysForContent.getString("insuranceTenure")));
        verticalList.add(createHorizontalList(keysForContent.getString("borrower"), applicantName, apptDateOfBirth, payload1.getGender(), appltMobNo, "", applicantInsurancePremiumAmount == "NA" ? "NA" : insuranceSumAssured, applicantInsurancePremiumAmount, finalTenure));
        verticalList.add(createHorizontalList(keysForContent.getString("coborrower"), coApplicantName, CommonUtils.getDefaultValue(coApptDateOfBirth), CommonUtils.getDefaultValue(payload2.getGender()), CommonUtils.getDefaultValue(coAppltMoNo), "", coApplicantInsurancePremiumAmount == "NA" ? "NA" : insuranceSumAssured, coApplicantInsurancePremiumAmount, finalTenure));
        return verticalList;
    }

    public static String getTodayData() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
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
        String ds1 = DOB;
        SimpleDateFormat sdf1 = new SimpleDateFormat(Constants.DATE_FORMAT_STD);
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

        horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(100));
        return horizontalList;

    }

    private ComponentBuilder<?, ?> createFourHorizontalListForCombinedBooklet2(String Key, String value, String value1, String value2) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle));
        return horizontalList;

    }

    private ComponentBuilder<?, ?> getBorrowerNameSignatureForCombinedBooklet(JSONObject keysForContent) {

        VerticalListBuilder verticalList = cmp.verticalList();

        verticalList.add(createFourHorizontalListForCombinedBooklet2(keysForContent.getString("borrowerName"), applicantName,keysForContent.getString("borrowerSign"),""));
        verticalList.add(createFourHorizontalListForCombinedBooklet2(keysForContent.getString("coborrowerName"), coApplicantName,keysForContent.getString("coborrowerSign"),""));

        return verticalList;
    }

    private ComponentBuilder<?, ?> createHorizontalList(String... values) {
        HorizontalListBuilder horizontalList = cmp.horizontalList();
        if (values == null || values.length < 1) {
            return horizontalList;
        }
        for (String value : values) {
            horizontalList.add(
                    cmp.text(value)
                            .setMarkup(Markup.HTML)
                            .setStyle(borderedStyle)
            );
        }
        return horizontalList;
    }

    private ComponentBuilder<?, ?> getNomineeDeclaration(JSONObject keysForContent, InsuranceDetailsPayload insurancePayload1, InsuranceDetailsPayload insurancePayload2) {

        VerticalListBuilder verticalList = cmp.verticalList();

        verticalList.add(createSevenHorizontalList("", keysForContent.getString("name"),keysForContent.getString("age"),keysForContent.getString("gender"),keysForContent.getString("relationship"), keysForContent.getString("mobileNo"), keysForContent.getString("emailId"))
                .setStyle(boldLeftStyle));

        verticalList.add(createSingleHorizontalListForScheduleA(keysForContent.getString("incaseBorrower")).setStyle(boldLeftStyle.setFontSize(12)));
        verticalList.add(createSevenHorizontalList(keysForContent.getString("nominee"),insurancePayload1.getNomineeName(),insurancePayload1.getNomineeDob(),insurancePayload1.getGender(), insurancePayload1.getNomineeRelation(), "NA","NA").setStyle(boldLeftStyle.setFontSize(12))); //nominee mobile id and email
        verticalList.add(createSevenHorizontalList(keysForContent.getString("appointee"),"NA","NA","NA","NA","NA","NA").setStyle(boldLeftStyle.setFontSize(12)));

        verticalList.add(createSingleHorizontalListForScheduleA(keysForContent.getString("insuranceCover")).setStyle(boldLeftStyle));
        return verticalList;
    }

    private ComponentBuilder<?, ?> getMemberApointeeDetails(JSONObject keysForContent) {

        VerticalListBuilder verticalList = cmp.verticalList();

        verticalList.add(createSevenHorizontalList1(keysForContent.getString("details"),keysForContent.getString("presntAddress"),keysForContent.getString("permanetAddress"),keysForContent.getString("bankAccountNo"), keysForContent.getString("ifsc"), keysForContent.getString("bankName"),keysForContent.getString("branchName"))
                .setStyle(boldLeftStyle));
        verticalList.add(createSevenHorizontalList1(keysForContent.getString("memeber1"),"NA","NA","NA","NA","NA","NA").setStyle(boldLeftStyle));
        verticalList.add(createSevenHorizontalList1(keysForContent.getString("nominee1"),"NA","NA","NA","NA","NA","NA").setStyle(boldLeftStyle));

        return verticalList;
    }

    private ComponentBuilder<?, ?> createSevenHorizontalList(String Key1, String value1, String Key2, String value2,
                                                             String Key3, String value3, String v7) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();
        horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(80));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(90));
        horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(65));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(45));
        horizontalList.add(cmp.text(Key3).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(90));
        horizontalList.add(cmp.text(value3).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(60));
        horizontalList.add(cmp.text(v7).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(95));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createSevenHorizontalList1(String Key1, String value1, String Key2, String value2,
                                                              String Key3, String value3, String v7) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(80));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(90));
        horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(90));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(70));
        horizontalList.add(cmp.text(Key3).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(60));
        horizontalList.add(cmp.text(value3).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(60));
        horizontalList.add(cmp.text(v7).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(60));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> Signatory(JSONObject keysForContent, CustomerDataFields custmrDataFields, String userNameBM) {
        // LEFT: Signature Details
        VerticalListBuilder leftBlock = cmp.verticalList();
        String blank = " .............";

        leftBlock.add(createTwoHorizontalList(keysForContent.getString("bmSignature") + blank,
                "", null));
        leftBlock.add(cmp.text("").setFixedHeight(3));
        leftBlock.add(createTwoHorizontalList(keysForContent.getString("bmName") + userNameBM, "", null));
        leftBlock.add(cmp.text("").setFixedHeight(3));
        leftBlock.add(createTwoHorizontalList(
                keysForContent.getString("bmBranchName") + custmrDataFields.getApplicationMaster().getBranchName(),
                "", null));
        leftBlock.add(cmp.text("").setFixedHeight(3));
        leftBlock.add(createTwoHorizontalList(keysForContent.getString("date") + CommonUtils.getCurDate(), "", null));

        // RIGHT: Boxed Section with Title
        VerticalListBuilder rightBox = cmp.verticalList();
        rightBox.add(
                cmp.text(keysForContent.getString("branchseal")).setMarkup(Markup.HTML)
                        .setStyle(stl.style().bold())
                        .setFixedHeight(130)
        );
//	rightBox.add(
//	    cmp.text("") // empty space inside box
//	        .setFixedHeight(60)
//	);
        // Wrap in bordered frame
        ComponentBuilder<?, ?> rightBoxWithBorder = cmp.horizontalList(rightBox).setStyle(
//	    stl.style().setBorder(stl.pen1Point())
                stl.style(stl.penThin()).setPadding(5)
                        .setPadding(5)
        ).setFixedWidth(200); // adjust width as needed

        // Combine both into a single horizontal layout
        return cmp.horizontalList()
                .add(leftBlock)
                .add(cmp.horizontalGap(10)) // space between left and right
                .add(rightBoxWithBorder);
    }



    private ComponentBuilder<?, ?> createTwoHorizontalList(String Key, String value, ReportStyleBuilder style) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(style));
        horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(style));

        return horizontalList;
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
}
