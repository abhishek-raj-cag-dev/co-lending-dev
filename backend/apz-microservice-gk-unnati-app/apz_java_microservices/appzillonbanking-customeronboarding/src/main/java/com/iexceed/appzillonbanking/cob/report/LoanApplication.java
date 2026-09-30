package com.iexceed.appzillonbanking.cob.report;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iexceed.appzillonbanking.cob.core.domain.ab.*;
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
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.ReportStyleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.*;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsWrapper;
import com.iexceed.appzillonbanking.cob.loans.payload.BCMPIIncomeDetailsWrapper;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

public class LoanApplication {

    private static final Logger logger = LogManager.getLogger(LoanApplication.class);

    private StyleBuilder borderedStyle, boldText, boldCenteredStyle, boldTextWithBorder, boldLeftStyle, leftStyle;

    static String space = "\u00a0\u00a0\u00a0";

    private String applicantCustId = "";
    private String coApplicantCustId = "";
    private String applicantName = "";
    private String coApplicantName = "";
    private CustomerDetails applicantCustDtls = null;
    private CustomerDetails coApplicantCustDtls = null;
    private String applicantFirstname = "";
    private String applicantLastname = "";
    private String coApplicantFirstname = "";
    private String coApplicantLastname = "";

    private String bmId = "-";
    private String kmId = "-";

    private String bmName = "-";
    private String kmName = "-";

    String appltGender = "";
    String coAppltGender = "";

    private String kmSubmDateStr = "";
    private String bmSubmDateStr = "";

    private String loanApplicationDateStr = "";



    public LoanApplication() {
        borderedStyle = stl.style(stl.penThin()).setPadding(5);
        boldTextWithBorder = stl.style(stl.penThin()).setPadding(5);
        boldCenteredStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        boldText = stl.style();
        boldLeftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

//		StyleBuilder headerStyle = stl.style().setFontSize(20).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
        leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
    }

    public Response generateLoanApplicationPdf(ApplicationMaster applicationMasterData,List<ApplicationDocuments> applnDocumentList,CustomerDataFields customerFileds, JSONObject keysForContent, String language, String kmId2, String bmId2,
                                               String kmSubmDateStr2, String bmSubmDateStr2, String usernameKM, String usernameBM, String leadInitiationDateStr, String kmIdFromCDH, String bmIdFromCDH, String usernameKMFromCDH, String usernameBMForAddProdct)
            throws DRException, IOException {
        kmId = kmId2;
        bmId = bmId2;
        kmSubmDateStr = kmSubmDateStr2;
        bmSubmDateStr = bmSubmDateStr2;
        kmName = usernameKM;
        bmName = usernameBM;
        Properties prop = null;

        if(Constants.UNNATI_RESTART_PRODUCT_CODE.equals(applicationMasterData.getProductCode()) ||
                Constants.Vishesh_LOAN_PRODUCT_CODE.equals(applicationMasterData.getProductCode())){
            kmSubmDateStr=leadInitiationDateStr;
            kmId = kmIdFromCDH;
            bmId = bmIdFromCDH;
            kmName = usernameKMFromCDH;
            bmName= usernameBMForAddProdct;
        }

        String base64String = null;

        Response response;
        try {
            try {
                prop = CommonUtils.readPropertyFile();
            } catch (IOException e) {
                logger.error("Error while reading property file in deleteDocument ", e);

            }
            String uploadLocation = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey())
                    +"/" + applicationMasterData.getAppId() + "/" + Constants.LOAN + "/" + applicationMasterData.getApplicationId() + "/";
            StyleBuilder tempStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);

            JasperReportBuilder report = new JasperReportBuilder();

            StyleBuilder headerStyle = stl.style().setFontSize(20)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
            StyleBuilder style = stl.style().setBackgroundColor(Color.GRAY).setFontSize(20)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

            StyleBuilder style1 = stl.style().setBackgroundColor(Color.GRAY).setFontSize(10)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

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


            CustomerDetailsPayload payload1 = null;
            CustomerDetailsPayload payload2 = null;
            Gson gsonObj = new Gson();
            for (CustomerDetails custDtl : customerFileds.getCustomerDetailsList()) {
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

            String applicationName = "";
            if (Constants.UNNATI_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                applicationName = keysForContent.getString("applicationName");
            }else if(Constants.RENEWAL_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                applicationName = keysForContent.getString("applicationNameRenewal");
            }else if(Constants.OPENMARKET_LOAN_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                applicationName = keysForContent.getString("applicationNameOpenMarket");
            }
            else if(Constants.UNNATI_RESTART_PRODUCT_CODE.equals(applicationMasterData.getProductCode())){
                applicationName = keysForContent.getString("applicationNameUnnatiRestart");
            }
            else if(Constants.Vishesh_LOAN_PRODUCT_CODE.equals(applicationMasterData.getProductCode())){
                applicationName = keysForContent.getString("applicationNameUnnatiLite");
            }

            subReport1
                    .title(cmp.text(applicationName).setMarkup(Markup.HTML).setStyle(headerStyle.setFontSize(16)));
//			.title(cmp.text(""));
            /* Basic Application Details */
            subReport1.title(getBasicAppnDetails(keysForContent, customerFileds, applicationMasterData,kmSubmDateStr))
                    .title(cmp.text(""));

            /* Loan Deatails */
            subReport2.title(cmp.text(keysForContent.getString("loanDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getLoanDetails4(keysForContent, customerFileds)).title(cmp.text(""));

            /* Customer Personal Details */
            subReport3
                    .title(cmp.text(keysForContent.getString("personalDetails")).setMarkup(Markup.HTML)
                            .setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getCustomerDetails(keysForContent, customerFileds, applicantCustDtls, coApplicantCustDtls,
                            payload1, payload2))
                    .title(cmp.text(""));

            subReport16.title(getCustomerDetails1(keysForContent, customerFileds, applicantCustDtls,
                    coApplicantCustDtls, payload1, payload2)).title(cmp.text(""));

            /* Address */
            subReport4
                    .title(cmp.text(keysForContent.getString("addressDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getAddressDetails(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));

            /* Other Deatails */
            subReport5.title(cmp.text(keysForContent.getString("otherDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getOtherDetails(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));

            /* Ocupation / Employemnent Details */

            if(Constants.Vishesh_LOAN_PRODUCT_CODE.equals(applicationMasterData.getProductCode())){
                        subReport6
                    .title(cmp.text(keysForContent.getString("employemnentDetails")).setMarkup(Markup.HTML)
                        .setStyle(boldLeftStyle.setFontSize(14)))
                        .title(getEmployemnentDetailsUnnatiLight(keysForContent, customerFileds, payload1, payload2))
                        .title(cmp.text(""));
            }else{
                subReport6
                        .title(cmp.text(keysForContent.getString("employemnentDetails")).setMarkup(Markup.HTML)
                                .setStyle(boldLeftStyle.setFontSize(14)))
                        .title(getEmployemnentDetails(keysForContent, customerFileds, payload1, payload2, applicationMasterData))
                        .title(cmp.text(""));
            }

            /* Income Details */
            subReport7
                    .title(cmp.text(keysForContent.getString("incomeDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getIncomeDetails4(keysForContent, customerFileds)).title(cmp.text(""));

            /* Banking Deatails */
            subReport8
                    .title(cmp.text(keysForContent.getString("bankingDeatails")).setMarkup(Markup.HTML)
                            .setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getBankDetails4(keysForContent, customerFileds)).title(cmp.text(""));

            /* Insurance Details */
            InsuranceDetailsPayload appPayload = null;
            for (InsuranceDetailsWrapper insurer : customerFileds.getInsuranceDetailsWrapperList()) {
                if (String.valueOf(insurer.getInsuranceDetails().getCustDtlId()).equals(applicantCustId)) {
                    appPayload = gsonObj.fromJson(insurer.getInsuranceDetails().getPayloadColumn(),
                            InsuranceDetailsPayload.class);
                    logger.debug("InsuranceDetailsApplicantPayload1 : " + appPayload);
                }
            }

            subReport10.title(cmp.text(keysForContent.getString(Constants.INSURANCE_DETAILS) + " "+ appPayload.getInsuranceOption())
                            .setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getInsuranceDetails4(keysForContent, customerFileds)).title(cmp.text(""));

            /* Lead and Source Details */
            subReport11
                    .title(cmp.text(keysForContent.getString("leadAndSourcingDetails")).setMarkup(Markup.HTML)
                            .setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getLeadAndSourcingDetails4(keysForContent, customerFileds)).title(cmp.text(""));

            /* Declaration */
            subReport12.title(cmp.text(keysForContent.getString("declaration")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(cmp.text(getDeclaration1(keysForContent, customerFileds)).setMarkup(Markup.HTML))

                    .title(cmp.text(getDeclaration2(keysForContent)).setMarkup(Markup.HTML))
                    .title(cmp.text(getDeclaration3(keysForContent)).setMarkup(Markup.HTML));

            logger.debug("declarations added");

            /* Applicant Details */
            subReport13.title(cmp.text(""), cmp.verticalGap(10))
                    .title(getCustNameSignPhoto(keysForContent, customerFileds, applicationMasterData,uploadLocation,applnDocumentList)).title(cmp.text(""));

            /* branch Declaration */
            subReport14.title(cmp.text(keysForContent.getString("confBranch")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(cmp.text(getConfBranch(keysForContent, customerFileds)).setMarkup(Markup.HTML));
            // .title(cmp.text(""));
            logger.debug("getConfBranch added");
            subReport15.title(getBranchStaffDetails(keysForContent, customerFileds)).title(cmp.text(""));
            String serverImagePath = CommonUtils.getExternalProperties("images") + "logo-name.png";
            logger.debug("serverImagePath :" + serverImagePath);

            report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30))
                    .pageHeader(cmp.image(serverImagePath).setHorizontalImageAlignment(HorizontalImageAlignment.CENTER)
                            .setStyle(boldCenteredStyle.setFontSize(12)))
//		    .pageFooter(cmp.pageNumber().setStyle(leftStyle))
                    .setDataSource(new JREmptyDataSource(1))
                    .detail(cmp.verticalList(cmp.subreport(subReport1), cmp.subreport(subReport2),
                            cmp.subreport(subReport3), cmp.subreport(subReport16), cmp.subreport(subReport4),
                            cmp.subreport(subReport5), cmp.subreport(subReport6), cmp.subreport(subReport7),
                            cmp.subreport(subReport8), cmp.subreport(subReport9), cmp.subreport(subReport10),
                            cmp.subreport(subReport11), cmp.subreport(subReport12), cmp.subreport(subReport13),
                            cmp.subreport(subReport14), cmp.subreport(subReport15)));
            logger.debug("added all subreports to report builder");

            // saving report to the directory and creating base64 string for response
            try {
                response = new Response();
                //Properties prop = CommonUtils.readPropertyFile();
                // Construct file path
                String filePathDest = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + "APZCBO" + "/"
                        + Constants.LOAN + "/" + customerFileds.getApplicationId() + "/";
                logger.debug("filePathDest :: {}", filePathDest);

                // Ensure directory exists
                File directory = new File(filePathDest);
                if (!directory.exists()) {
                    boolean isCreated = directory.mkdirs();
                    if (!isCreated) {
                        throw new IOException("Failed to create directory: " + filePathDest);
                    }
                }

                String filePath = filePathDest + customerFileds.getApplicationId() + "_LoanApplication" + ".pdf";
                logger.debug("final filePath : {}", filePath);


                String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
                logger.debug("inputLanguage " + language);

                boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                        .anyMatch(lang -> lang.equalsIgnoreCase(language));

                if (isValidLanguage) {

                    JasperPrint jasperPrint = report.toJasperPrint();

                    HtmlExporter exporter = new HtmlExporter();
                    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

                    ByteArrayOutputStream htmlOut = new ByteArrayOutputStream();
                    SimpleHtmlExporterOutput output = new SimpleHtmlExporterOutput(htmlOut);

                    //  Embed images as Base64
                    SimpleHtmlReportConfiguration reportConfig = new SimpleHtmlReportConfiguration();
                    reportConfig.setEmbedImage(true);  // crucial for images

                    exporter.setConfiguration(reportConfig);
                    exporter.setExporterOutput(output);

                    //  Export fully in memory
                    exporter.exportReport();

                    //  Return HTML string
                    base64String = htmlOut.toString(StandardCharsets.UTF_8.name());

                    //
                    String htmlContent = base64String.replaceAll("(?i)<\\/?html>|<\\/?body>", "");

                    // Wrap properly in single HTML structure
                    StringBuilder mergedHtml = new StringBuilder();
                    mergedHtml.append("<html><body>");
                    mergedHtml.append(htmlContent);
                    mergedHtml.append("</body></html>");

                    JsonObject mergedHtmlJson = new JsonObject();
                    mergedHtmlJson.addProperty("base64", mergedHtml.toString());
                    mergedHtmlJson.addProperty("fileType", "html");
                    mergedHtmlJson.addProperty("status", ResponseCodes.SUCCESS.getValue());

                    Gson gson = new Gson();
                    response = getSuccessJson1(gson.toJson(mergedHtmlJson));

                } else {
                    //  Normal PDF flow (write to disk)
                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
                        report.toPdf(fos);
                    }

                    byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
                    base64String = java.util.Base64.getEncoder().encodeToString(inputfile);
                    response = getSuccessJson(base64String);
                }
                logger.info("Loan applicationPDF Report Generated");

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
        logger.debug("generateLoanApplicationPdf Function end");
        return response;

    }

    private ComponentBuilder<?, ?> getInsuranceHeader(JSONObject keysForContent, InsuranceDetailsPayload appPayload) {

        VerticalListBuilder verticallist = cmp.verticalList();
        verticallist.add(createTwoHorizontalList1(keysForContent.getString(Constants.INSURANCE_DETAILS),
                appPayload.getInsuranceOption()));
        // h.add(cmp.text(keysForContent.getString(Constants.INSURANCE_DETAILS))
        return verticallist;
    }

    public String generateLoanApplicationPdfForDBKit(ApplicationMaster applicationMasterData, List<ApplicationDocuments> applnDocumentList, CustomerDataFields customerFileds, JSONObject keysForContent, String filePath, String language, String kmId2, String bmId2, String kmSubmDateStr2, String bmSubmDateStr2, String usernameKM, String usernameBM, String leadInitiationDateStr, String kmIdFromCDH, String bmIdFromCDH, String usernameKMFromCDH, String usernameBMForAddProdct)
            throws DRException, IOException, JRException {

        kmId = kmId2;
        bmId = bmId2;
        kmSubmDateStr = kmSubmDateStr2;
        bmSubmDateStr = bmSubmDateStr2;
        kmName = usernameKM;
        bmName = usernameBM;
        Properties prop = null;

        if(Constants.UNNATI_RESTART_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
                || Constants.Vishesh_LOAN_PRODUCT_CODE.equals(applicationMasterData.getProductCode())){
            kmSubmDateStr=leadInitiationDateStr;
            kmId = kmIdFromCDH;
            bmId = bmIdFromCDH;
            kmName = usernameKMFromCDH;
            bmName= usernameBMForAddProdct;
        }

        try {
            logger.debug("Inside loan application generation: ");
            try {
                prop = CommonUtils.readPropertyFile();
            } catch (IOException e) {
                logger.error("Error while reading property file in deleteDocument ", e);

            }
            String uploadLocation = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey())
                    +"/" + applicationMasterData.getAppId() + "/" + Constants.LOAN + "/" + applicationMasterData.getApplicationId() + "/";
            StyleBuilder tempStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);

            JasperReportBuilder report = new JasperReportBuilder();

            StyleBuilder headerStyle = stl.style().setFontSize(20)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
            StyleBuilder style = stl.style().setBackgroundColor(Color.GRAY).setFontSize(20)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

            StyleBuilder style1 = stl.style().setBackgroundColor(Color.GRAY).setFontSize(10)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

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


            CustomerDetailsPayload payload1 = null;
            CustomerDetailsPayload payload2 = null;
            Gson gsonObj = new Gson();
            for (CustomerDetails custDtl : customerFileds.getCustomerDetailsList()) {
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


            String applicationName = "";
            if (Constants.UNNATI_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                applicationName = keysForContent.getString("applicationName");
            }else if(Constants.RENEWAL_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                applicationName = keysForContent.getString("applicationNameRenewal");
            }else if(Constants.OPENMARKET_LOAN_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                applicationName = keysForContent.getString("applicationNameOpenMarket");
            }else if(Constants.Vishesh_LOAN_PRODUCT_CODE.equals(applicationMasterData.getProductCode())){
                applicationName = keysForContent.getString("applicationNameUnnatiLite");
            }
            else if(Constants.UNNATI_RESTART_PRODUCT_CODE.equals(applicationMasterData.getProductCode())){
                applicationName = keysForContent.getString("applicationNameUnnatiRestart");
            }

            subReport1
                    .title(cmp.text(applicationName).setMarkup(Markup.HTML).setStyle(headerStyle.setFontSize(16)));
            /* Basic Application Details */
            subReport1.title(getBasicAppnDetails(keysForContent, customerFileds, applicationMasterData, kmSubmDateStr))
                    .title(cmp.text(""));

            /* Loan Deatails */
            subReport2.title(cmp.text(keysForContent.getString("loanDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getLoanDetails4(keysForContent, customerFileds)).title(cmp.text(""));

            /* Customer Personal Details */
            subReport3
                    .title(cmp.text(keysForContent.getString("personalDetails")).setMarkup(Markup.HTML)
                            .setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getCustomerDetails(keysForContent, customerFileds, applicantCustDtls, coApplicantCustDtls,
                            payload1, payload2))
                    .title(cmp.text(""));

            subReport16.title(getCustomerDetails1(keysForContent, customerFileds, applicantCustDtls,
                    coApplicantCustDtls, payload1, payload2)).title(cmp.text(""));

            /* Address */
            subReport4
                    .title(cmp.text(keysForContent.getString("addressDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getAddressDetails(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));

            /* Other Deatails */
            subReport5.title(cmp.text(keysForContent.getString("otherDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getOtherDetails(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));

            /* Ocupation / Employemnent Details */
            subReport6
                    .title(cmp.text(keysForContent.getString("employemnentDetails")).setMarkup(Markup.HTML)
                            .setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getEmployemnentDetails(keysForContent, customerFileds, payload1, payload2, applicationMasterData))
                    .title(cmp.text(""));

            /* Income Details */
            subReport7
                    .title(cmp.text(keysForContent.getString("incomeDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getIncomeDetails4(keysForContent, customerFileds)).title(cmp.text(""));

            /* Banking Deatails */
            subReport8
                    .title(cmp.text(keysForContent.getString("bankingDeatails")).setMarkup(Markup.HTML)
                            .setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getBankDetails4(keysForContent, customerFileds)).title(cmp.text(""));

            /* Borrowing Details */
            /*
             * subReport9.title(cmp.text(keysForContent.getString("borrowingDetails")).
             * setStyle(boldLeftStyle.setFontSize(14)))
             * .title(getBorrowingDetails(keysForContent,customerFileds)).title(cmp.text("")
             * );
             */

            /* Insurance Details */
            InsuranceDetailsPayload appPayload = null;
            for (InsuranceDetailsWrapper insurer : customerFileds.getInsuranceDetailsWrapperList()) {
                if (String.valueOf(insurer.getInsuranceDetails().getCustDtlId()).equals(applicantCustId)) {
                    appPayload = gsonObj.fromJson(insurer.getInsuranceDetails().getPayloadColumn(),
                            InsuranceDetailsPayload.class);
                    logger.debug("InsuranceDetailsApplicantPayload2 : " + appPayload);
                }
            }

            subReport10
                    .title(cmp
                            .text(keysForContent.getString(Constants.INSURANCE_DETAILS) + " " + appPayload.getInsuranceOption()).setMarkup(Markup.HTML)
                            .setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getInsuranceDetails4(keysForContent, customerFileds)).title(cmp.text(""));

            /* Lead and Source Details */
            subReport11
                    .title(cmp.text(keysForContent.getString("leadAndSourcingDetails")).setMarkup(Markup.HTML)
                            .setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getLeadAndSourcingDetails4(keysForContent, customerFileds)).title(cmp.text(""));

            /* Declaration */
            subReport12.title(cmp.text(keysForContent.getString("declaration")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(cmp.text(getDeclaration1(keysForContent, customerFileds)).setMarkup(Markup.HTML))

                    .title(cmp.text(getDeclaration2(keysForContent)).setMarkup(Markup.HTML))
                    .title(cmp.text(getDeclaration3(keysForContent)).setMarkup(Markup.HTML)).title(cmp.text(""));

            logger.debug("declarations added");

            /* Applicant Details */
            subReport13.title(cmp.text(""), cmp.verticalGap(10))
                    .title(getCustNameSignPhoto(keysForContent, customerFileds, applicationMasterData,uploadLocation,applnDocumentList)).title(cmp.text(""));

            /* branch Declaration */
            subReport14.title(cmp.text(keysForContent.getString("confBranch")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(cmp.text(getConfBranch(keysForContent, customerFileds)).setMarkup(Markup.HTML));
            // .title(cmp.text(""));
            logger.debug("getConfBranch added");
            subReport15.title(getBranchStaffDetails(keysForContent, customerFileds)).title(cmp.text(""));
            String serverImagePath = CommonUtils.getExternalProperties("images") + "logo-name.png";
            logger.debug("serverImagePath :" + serverImagePath);

            report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30))
                    .pageHeader(cmp.image(serverImagePath).setHorizontalImageAlignment(HorizontalImageAlignment.CENTER)
                            .setStyle(boldCenteredStyle.setFontSize(12)))
//		    .pageFooter(cmp.pageNumber().setStyle(leftStyle))
                    .setDataSource(new JREmptyDataSource(1))
                    .detail(cmp.verticalList(cmp.subreport(subReport1), cmp.subreport(subReport2),
                            cmp.subreport(subReport3), cmp.subreport(subReport16), cmp.subreport(subReport4),
                            cmp.subreport(subReport5), cmp.subreport(subReport6), cmp.subreport(subReport7),
                            cmp.subreport(subReport8), cmp.subreport(subReport9), cmp.subreport(subReport10),
                            cmp.subreport(subReport11), cmp.subreport(subReport12), cmp.subreport(subReport13),
                            cmp.subreport(subReport14), cmp.subreport(subReport15)));
            logger.debug("added all subreports to report builder");

            // saving report to the directory and creating base64 string for response
//            try {
//                // Save report to file
//                try (FileOutputStream fos = new FileOutputStream(filePath)) {
//                    report.toPdf(fos);
//                }
//                // Read file and encode to Base64
//                byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
//                logger.info("Loan applicationPDF Report Generated");
//                return inputfile;

            try {
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
    // @author Abhishek.Raj.CAG
    private static final class SectionLabeler {
        private int index = 0;

        /** Returns "A. ", "B. ", "C. " ... and advances. Call ONLY when the section is rendered. */
        String next() {
            int i = index++;
            // handles > 26 sections as AA, AB ... just in case
            StringBuilder sb = new StringBuilder();
            do {
                sb.insert(0, (char) ('A' + (i % 26)));
                i = i / 26 - 1;
            } while (i >= 0);
            return sb + ". ";
        }
    }

    public String generateLoanApplicationPdfForDBKitAdd(ApplicationMaster applicationMasterData, List<ApplicationDocuments> applnDocumentList, CustomerDataFields customerFileds, JSONObject keysForContent, String filePath, String language, String kmId2, String bmId2, String kmSubmDateStr2, String bmSubmDateStr2, String usernameKM, String usernameBM,  String leadInitiationDateStr)
            throws DRException, IOException, JRException {

        kmId = kmId2;
        bmId = bmId2;
        kmSubmDateStr = kmSubmDateStr2;
        bmSubmDateStr = bmSubmDateStr2;
        kmName = usernameKM;
        bmName = usernameBM;

        loanApplicationDateStr = leadInitiationDateStr;



        SectionLabeler section = new SectionLabeler();

        try {
            logger.debug("Inside generateLoanApplicationPdfForDBKitAdd: ");

            StyleBuilder tempStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);

            JasperReportBuilder report = new JasperReportBuilder();

            StyleBuilder headerStyle = stl.style().setFontSize(20)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
            StyleBuilder style = stl.style().setBackgroundColor(Color.GRAY).setFontSize(20)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

            StyleBuilder style1 = stl.style().setBackgroundColor(Color.GRAY).setFontSize(10)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

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
            // @author Abhishek.Raj.CAG
            JasperReportBuilder subReport17 = new JasperReportBuilder();
            //----END----

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
            // @author Abhishek.Raj.CAG
            subReport17.setDataSource(emptyDataSource);
            //----END----


            CustomerDetailsPayload payload1 = null;
            CustomerDetailsPayload payload2 = null;
            Gson gsonObj = new Gson();
            for (CustomerDetails custDtl : customerFileds.getCustomerDetailsList()) {
                logger.debug("customer Type add : " + custDtl.getCustomerType());
                if (custDtl.getCustomerType().equalsIgnoreCase(Constants.APPLICANT)) {
                    applicantCustId = String.valueOf(custDtl.getCustDtlId());
                    logger.debug("applicantCustId add : " + applicantCustId);
                    applicantName = custDtl.getCustomerName();
                    payload1 = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    applicantFirstname = payload1.getFirstName();
                    applicantLastname = payload1.getLastName();
                    logger.debug("custApplicantPayload add :" + payload1);
                    applicantCustDtls = custDtl;
                    appltGender = payload1.getGender();
                } else if (custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
                    coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
                    coApplicantName = custDtl.getCustomerName();
                    logger.debug("coApplicantCustId add : " + coApplicantCustId);
                    payload2 = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    coApplicantFirstname = payload2.getFirstName();
                    coApplicantLastname = payload2.getLastName();
                    logger.debug("custCo-ApplicantPayload add :" + payload2);
                    coAppltGender = payload2.getGender();
                    coApplicantCustDtls = custDtl;
                }
            }


            String applicationName = "";

            // @author Abhishek.Raj.CAG
            if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                applicationName = keysForContent.getString("applicationNameSUPPLEMENTARYLOAN");
            }
            else if(Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                applicationName = keysForContent.getString("applicationNameEMERGENCYLOAN");
            }
            else if(Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                applicationName = keysForContent.getString("applicationNameFAMILYWELFARELOAN");
            }
            else if(Constants.UNNATI_RESTART_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                applicationName = keysForContent.getString("applicationNameUnnatiRestart");
            }

            //----END----

            subReport1
                    .title(cmp.text(applicationName).setMarkup(Markup.HTML).setStyle(headerStyle.setFontSize(16)));
            /* Basic Application Details */
            subReport1.title(getBasicAppnDetails(keysForContent, customerFileds, applicationMasterData, loanApplicationDateStr))
                    .title(cmp.text(""));

            /* Loan Deatails */
            subReport2.title(cmp.text(section.next() + keysForContent.getString("loanDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getLoanDetails4Add(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));

            /* Insurance Details */
            InsuranceDetailsPayload appPayload = null;
            for (InsuranceDetailsWrapper insurer : customerFileds.getInsuranceDetailsWrapperList()) {
                if (String.valueOf(insurer.getInsuranceDetails().getCustDtlId()).equals(applicantCustId)) {
                    appPayload = gsonObj.fromJson(insurer.getInsuranceDetails().getPayloadColumn(),
                            InsuranceDetailsPayload.class);
                    logger.debug("InsuranceDetailsApplicantPayload2 add : " + appPayload);
                }
            }

            if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
                    ||Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
            ){

//                subReport10
//                        .title(cmp
//                                .text(section.next() + keysForContent.getString(Constants.INSURANCE_DETAILS) + " " + appPayload.getInsuranceOption()).setMarkup(Markup.HTML)
//                                .setStyle(boldLeftStyle.setFontSize(14)))
//                        .title(getInsuranceDetails4Add(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));
                subReport10
                        .title(cmp
                                .text(section.next() + keysForContent.getString(Constants.INSURANCE_DETAILS)).setMarkup(Markup.HTML)
                                .setStyle(boldLeftStyle.setFontSize(14)))
                        .title(getInsuranceDetails4Add(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));
            }

            /* Customer Personal Details */
            subReport3
                    .title(cmp.text(section.next() + keysForContent.getString("personalDetails")).setMarkup(Markup.HTML)
                            .setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getCustomerDetailsAdd(keysForContent, customerFileds, applicantCustDtls, coApplicantCustDtls,
                            payload1, payload2, applicationMasterData))
                    .title(cmp.text(""));

//            subReport16.title(getCustomerDetails1Add(keysForContent, customerFileds, applicantCustDtls,
//                    coApplicantCustDtls, payload1, payload2)).title(cmp.text(""));

            /* Address */
            subReport4
                    .title(cmp.text(section.next() + keysForContent.getString("addressDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getAddressDetailsAdd(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));


            //
            if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                subReport5.title(cmp.text(section.next() + keysForContent.getString("employemnentDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                        .title(getOtherDetailsAdd(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));
            }
            /* Banking Deatails */
            subReport8
                    .title(cmp.text(section.next() + keysForContent.getString("bankingDeatails")).setMarkup(Markup.HTML)
                            .setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getBankDetails4(keysForContent, customerFileds)).title(cmp.text(""));

            /* Declaration */
            subReport12.title(cmp.text(section.next() + keysForContent.getString("declaration")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(cmp.text(getDeclaration1(keysForContent, customerFileds)).setMarkup(Markup.HTML))

                    .title(cmp.text(getDeclaration2(keysForContent)).setMarkup(Markup.HTML))
                    .title(cmp.text(getDeclaration3(keysForContent)).setMarkup(Markup.HTML))
                    .title(cmp.text(getDeclaration4(keysForContent)).setMarkup(Markup.HTML)).title(cmp.text(""));

            logger.debug("declarations added add");



            if(Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(applicationMasterData.getProductCode()) || Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())){
                /* Applicant Details */
                subReport13.title(cmp.text(""), cmp.verticalGap(10))
                        .title(getCustNameSignPhotoFWLAdd(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));
            }else{
                /* Applicant Details */
                subReport13.title(cmp.text(""), cmp.verticalGap(10))
                        .title(getCustNameSignPhotoAdd(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));
            }

            /* branch Declaration */
            subReport14.title(cmp.text(keysForContent.getString("confBranch")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(cmp.text(getConfBranch(keysForContent, customerFileds)).setMarkup(Markup.HTML));
            // .title(cmp.text(""));
            logger.debug("getConfBranch added add");
            subReport15.title(getBranchStaffDetailsAdd(keysForContent, customerFileds)).title(cmp.text(""));
            String serverImagePath = CommonUtils.getExternalProperties("images") + "logo-name.png";
            logger.debug("serverImagePath add :" + serverImagePath);


            if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())){
                logger.debug("getSourcingOfficerDetails added add");
                subReport17.title(getSourcingOfficerDetails(keysForContent, customerFileds)).title(cmp.text(""));
            }


            report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30))
                    .pageHeader(cmp.image(serverImagePath).setHorizontalImageAlignment(HorizontalImageAlignment.CENTER)
                            .setStyle(boldCenteredStyle.setFontSize(12)))
//		    .pageFooter(cmp.pageNumber().setStyle(leftStyle))
                    .setDataSource(new JREmptyDataSource(1))
                    .detail(cmp.verticalList(cmp.subreport(subReport1), cmp.subreport(subReport2),
                            cmp.subreport(subReport10),
                            cmp.subreport(subReport3), cmp.subreport(subReport16), cmp.subreport(subReport4),
                            cmp.subreport(subReport5), cmp.subreport(subReport6), cmp.subreport(subReport7),
                            cmp.subreport(subReport8), cmp.subreport(subReport9),
                            cmp.subreport(subReport11), cmp.subreport(subReport12), cmp.subreport(subReport13),
                            cmp.subreport(subReport14), cmp.subreport(subReport15), cmp.subreport(subReport17)));
            logger.debug("added all subreports to report builder add");

            // saving report to the directory and creating base64 string for response
//            try {
//                // Save report to file
//                try (FileOutputStream fos = new FileOutputStream(filePath)) {
//                    report.toPdf(fos);
//                }
//                // Read file and encode to Base64
//                byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
//                logger.info("Loan applicationPDF Report Generated");
//                return inputfile;

            try {
                String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
                logger.debug("inputLanguage add " + language);

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
                logger.error("Error generating PDF report add: ", e);
            } catch (IOException e) {
                logger.error("Error handling file operations add: ", e);
            } catch (Exception e) {
                logger.error("Unexpected error add: ", e);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.debug("generateLoanApplicationPdf Function end add");
        return null;

    }

    // @author Abhishek.Raj.CAG
    public Response generateLoanApplicationPdfAdd(ApplicationMaster applicationMasterData,List<ApplicationDocuments> applnDocumentList, CustomerDataFields customerFileds, JSONObject keysForContent, String language,
                                                  String kmId2, String bmId2, String kmSubmDateStr2, String bmSubmDateStr2,
                                                  String usernameKM, String usernameBM)
            throws DRException, IOException {

        kmId = kmId2;
        bmId = bmId2;
        kmSubmDateStr = kmSubmDateStr2;
        bmSubmDateStr = bmSubmDateStr2;
        kmName = usernameKM;
        bmName = usernameBM;

        String base64String = null;

        Response response;

        SectionLabeler section = new SectionLabeler();

        try {
            logger.debug("Inside generateLoanApplicationPdfAdd: ");

            StyleBuilder tempStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);

            JasperReportBuilder report = new JasperReportBuilder();

            StyleBuilder headerStyle = stl.style().setFontSize(20)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
            StyleBuilder style = stl.style().setBackgroundColor(Color.GRAY).setFontSize(20)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

            StyleBuilder style1 = stl.style().setBackgroundColor(Color.GRAY).setFontSize(10)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

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
            // @author Abhishek.Raj.CAG
            JasperReportBuilder subReport17 = new JasperReportBuilder();
            //----END----

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
            // @author Abhishek.Raj.CAG
            subReport17.setDataSource(emptyDataSource);
            //----END----


            CustomerDetailsPayload payload1 = null;
            CustomerDetailsPayload payload2 = null;
            Gson gsonObj = new Gson();
            for (CustomerDetails custDtl : customerFileds.getCustomerDetailsList()) {
                logger.debug("customer Type add : " + custDtl.getCustomerType());
                if (custDtl.getCustomerType().equalsIgnoreCase(Constants.APPLICANT)) {
                    applicantCustId = String.valueOf(custDtl.getCustDtlId());
                    logger.debug("applicantCustId add : " + applicantCustId);
                    applicantName = custDtl.getCustomerName();
                    payload1 = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    applicantFirstname = payload1.getFirstName();
                    applicantLastname = payload1.getLastName();
                    logger.debug("custApplicantPayload add :" + payload1);
                    applicantCustDtls = custDtl;
                    appltGender = payload1.getGender();
                } else if (custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
                    coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
                    coApplicantName = custDtl.getCustomerName();
                    logger.debug("coApplicantCustId add : " + coApplicantCustId);
                    payload2 = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    coApplicantFirstname = payload2.getFirstName();
                    coApplicantLastname = payload2.getLastName();
                    logger.debug("custCo-ApplicantPayload add :" + payload2);
                    coAppltGender = payload2.getGender();
                    coApplicantCustDtls = custDtl;
                }
            }


            String applicationName = "";

            // @author Abhishek.Raj.CAG
            if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                applicationName = keysForContent.getString("applicationNameSUPPLEMENTARYLOAN");
            }
            else if(Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                applicationName = keysForContent.getString("applicationNameEMERGENCYLOAN");
            }
            else if(Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                applicationName = keysForContent.getString("applicationNameFAMILYWELFARELOAN");
            }

            //----END----

            subReport1
                    .title(cmp.text(applicationName).setMarkup(Markup.HTML).setStyle(headerStyle.setFontSize(16)));
            /* Basic Application Details */
            subReport1.title(getBasicAppnDetails(keysForContent, customerFileds, applicationMasterData, kmSubmDateStr))
                    .title(cmp.text(""));

            /* Loan Deatails */
            subReport2.title(cmp.text(section.next() + keysForContent.getString("loanDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getLoanDetails4Add(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));

            /* Insurance Details */
            InsuranceDetailsPayload appPayload = null;
            for (InsuranceDetailsWrapper insurer : customerFileds.getInsuranceDetailsWrapperList()) {
                if (String.valueOf(insurer.getInsuranceDetails().getCustDtlId()).equals(applicantCustId)) {
                    appPayload = gsonObj.fromJson(insurer.getInsuranceDetails().getPayloadColumn(),
                            InsuranceDetailsPayload.class);
                    logger.debug("InsuranceDetailsApplicantPayload2 add : " + appPayload);
                }
            }

            if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
                    ||Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
            ){

//                subReport10
//                        .title(cmp
//                                .text(section.next() + keysForContent.getString(Constants.INSURANCE_DETAILS) + " " + appPayload.getInsuranceOption()).setMarkup(Markup.HTML)
//                                .setStyle(boldLeftStyle.setFontSize(14)))
//                        .title(getInsuranceDetails4Add(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));
                subReport10
                        .title(cmp
                                .text(section.next() + keysForContent.getString(Constants.INSURANCE_DETAILS)).setMarkup(Markup.HTML)
                                .setStyle(boldLeftStyle.setFontSize(14)))
                        .title(getInsuranceDetails4Add(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));
            }else{
                //
            }

            /* Customer Personal Details */
            subReport3
                    .title(cmp.text(section.next() + keysForContent.getString("personalDetails")).setMarkup(Markup.HTML)
                            .setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getCustomerDetailsAdd(keysForContent, customerFileds, applicantCustDtls, coApplicantCustDtls,
                            payload1, payload2, applicationMasterData))
                    .title(cmp.text(""));

//            subReport16.title(getCustomerDetails1Add(keysForContent, customerFileds, applicantCustDtls,
//                    coApplicantCustDtls, payload1, payload2)).title(cmp.text(""));

            /* Address */
            subReport4
                    .title(cmp.text(section.next() + keysForContent.getString("addressDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getAddressDetailsAdd(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));


            //
            if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                subReport5.title(cmp.text(section.next() + keysForContent.getString("employemnentDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                        .title(getOtherDetailsAdd(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));
            }
            /* Banking Deatails */
            subReport8
                    .title(cmp.text(section.next() + keysForContent.getString("bankingDeatails")).setMarkup(Markup.HTML)
                            .setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getBankDetails4(keysForContent, customerFileds)).title(cmp.text(""));


            /* Declaration */
            subReport12.title(cmp.text(section.next() + keysForContent.getString("declaration")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(cmp.text(getDeclaration1(keysForContent, customerFileds)).setMarkup(Markup.HTML))
                    .title(cmp.text(getDeclaration2(keysForContent)).setMarkup(Markup.HTML))
                    .title(cmp.text(getDeclaration3(keysForContent)).setMarkup(Markup.HTML))
                    .title(cmp.text(getDeclaration4(keysForContent)).setMarkup(Markup.HTML)).title(cmp.text(""));

            logger.debug("declarations added add");

            /* Applicant Details */
            subReport13.title(cmp.text(""), cmp.verticalGap(10))
                    .title(getCustNameSignPhotoAdd(keysForContent, customerFileds, applicationMasterData)).title(cmp.text(""));

            /* branch Declaration */
            subReport14.title(cmp.text(keysForContent.getString("confBranch")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(cmp.text(getConfBranch(keysForContent, customerFileds)).setMarkup(Markup.HTML));
            // .title(cmp.text(""));
            logger.debug("getConfBranch added add");
            subReport15.title(getBranchStaffDetailsAdd(keysForContent, customerFileds)).title(cmp.text(""));
            String serverImagePath = CommonUtils.getExternalProperties("images") + "logo-name.png";
            logger.debug("serverImagePath add :" + serverImagePath);


            if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())){
                logger.debug("getSourcingOfficerDetails added add");
                subReport17.title(getSourcingOfficerDetails(keysForContent, customerFileds)).title(cmp.text(""));
            }


            report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30))
                    .pageHeader(cmp.image(serverImagePath).setHorizontalImageAlignment(HorizontalImageAlignment.CENTER)
                            .setStyle(boldCenteredStyle.setFontSize(12)))
//		    .pageFooter(cmp.pageNumber().setStyle(leftStyle))
                    .setDataSource(new JREmptyDataSource(1))
                    .detail(cmp.verticalList(cmp.subreport(subReport1), cmp.subreport(subReport2),
                            cmp.subreport(subReport10),
                            cmp.subreport(subReport3), cmp.subreport(subReport16), cmp.subreport(subReport4),
                            cmp.subreport(subReport5), cmp.subreport(subReport6), cmp.subreport(subReport7),
                            cmp.subreport(subReport8), cmp.subreport(subReport9),
                            cmp.subreport(subReport11), cmp.subreport(subReport12), cmp.subreport(subReport13),
                            cmp.subreport(subReport14), cmp.subreport(subReport15), cmp.subreport(subReport17)));
            logger.debug("added all subreports to report builder add");

            // saving report to the directory and creating base64 string for response
            try {
                response = new Response();
                Properties prop = CommonUtils.readPropertyFile();
                // Construct file path
                String filePathDest = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + "APZCBO" + "/"
                        + Constants.LOAN + "/" + customerFileds.getApplicationId() + "/";
                logger.debug("filePathDest :: {}", filePathDest);

                // Ensure directory exists
                File directory = new File(filePathDest);
                if (!directory.exists()) {
                    boolean isCreated = directory.mkdirs();
                    if (!isCreated) {
                        throw new IOException("Failed to create directory: " + filePathDest);
                    }
                }

                String filePath = filePathDest + customerFileds.getApplicationId() + "_LoanApplication" + ".pdf";
                logger.debug("final filePath : {}", filePath);


                String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
                logger.debug("inputLanguage add " + language);

                boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                        .anyMatch(lang -> lang.equalsIgnoreCase(language));

                if (isValidLanguage) {

                    JasperPrint jasperPrint = report.toJasperPrint();

                    HtmlExporter exporter = new HtmlExporter();
                    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

                    ByteArrayOutputStream htmlOut = new ByteArrayOutputStream();
                    SimpleHtmlExporterOutput output = new SimpleHtmlExporterOutput(htmlOut);

                    //  Embed images as Base64
                    SimpleHtmlReportConfiguration reportConfig = new SimpleHtmlReportConfiguration();
                    reportConfig.setEmbedImage(true);  // crucial for images

                    exporter.setConfiguration(reportConfig);
                    exporter.setExporterOutput(output);

                    //  Export fully in memory
                    exporter.exportReport();

                    //  Return HTML string
                    base64String = htmlOut.toString(StandardCharsets.UTF_8.name());

                    //
                    String htmlContent = base64String.replaceAll("(?i)<\\/?html>|<\\/?body>", "");

                    // Wrap properly in single HTML structure
                    StringBuilder mergedHtml = new StringBuilder();
                    mergedHtml.append("<html><body>");
                    mergedHtml.append(htmlContent);
                    mergedHtml.append("</body></html>");

                    JsonObject mergedHtmlJson = new JsonObject();
                    mergedHtmlJson.addProperty("base64", mergedHtml.toString());
                    mergedHtmlJson.addProperty("fileType", "html");
                    mergedHtmlJson.addProperty("status", ResponseCodes.SUCCESS.getValue());

                    Gson gson = new Gson();
                    response = getSuccessJson1(gson.toJson(mergedHtmlJson));

                } else {
                    //  Normal PDF flow (write to disk)
                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
                        report.toPdf(fos);
                    }

                    byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
                    base64String = java.util.Base64.getEncoder().encodeToString(inputfile);
                    response = getSuccessJson(base64String);
                }
                logger.info("Loan applicationPDF Report Generated");

            } catch (DRException e) {
                logger.error("Error generating PDF report add: ", e);
                response = getFailureJson(e.getMessage());
            } catch (IOException e) {
                logger.error("Error handling file operations add: ", e);
                response = getFailureJson(e.getMessage());
            } catch (Exception e) {
                logger.error("Unexpected error add: ", e);
                response = getFailureJson(e.getMessage());
            }

        } catch (Exception e) {
            response = getFailureJson(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        logger.debug("generateLoanApplicationPdfAdd Function end add");
        return response;

    }
    //----END----


    // 1
    private ComponentBuilder<?, ?> getBasicAppnDetails(JSONObject keysForContent, CustomerDataFields req,
                                                       ApplicationMaster applicationMasterData, String kmSubmDateStr) {
        VerticalListBuilder verticalList = cmp.verticalList();
        try {
            // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            // LocalDate date = req.getApplicationMaster().getApplicationDate();
            // String ApplnDateString = LocalDate.now().format(formatter);
            // String ApplnDateString = kmSubmDate.format(formatter);


//			String applicationDate = "";
            String loanAcountNumber = req.getLoanDetails().getT24LoanId() == null ? ""
                    : req.getLoanDetails().getT24LoanId();

//			try {
//				applicationDate = kmSubmDateStr
//						.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//			} catch (Exception e) {
//				logger.error("error at date conversion", e);
//			}
            verticalList.add(createFourHorizontalList(keysForContent.getString("customerId"),
                    req.getApplicationMaster().getProductCode()
                            .equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE) ?
                            req.getApplicationMaster().getApplicantT24Id() :
                            req.getApplicationMaster().getSearchCode2(), keysForContent.getString("appltDate"),
                    kmSubmDateStr));
            verticalList.add(createFourHorizontalList(keysForContent.getString("gkBranchName"),
                    req.getApplicationMaster().getBranchName(), keysForContent.getString("applicationId"),
                    loanAcountNumber));
            verticalList.add(createFourHorizontalList(keysForContent.getString("gkAreaName"),
                    req.getApplicationMaster().getKendraName(), keysForContent.getString("gkBranchId"),
                    req.getApplicationMaster().getBranchId()));
            // author Abhishek.Raj.CAG
           if(
                   Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode()) ||
                           Constants.UNNATI_EMERGENCY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())||
                           Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(applicationMasterData.getProductCode())
           ){
               verticalList.add(createFourHorizontalList(keysForContent.getString("kmName"),
                       kmName, keysForContent.getString("kmId"),
                       kmId));
           }
            //----END----
            logger.debug("BasicAppnDetails added");

        } catch (Exception e) {
            logger.error("error - getBasicAppnDetails");
            logger.error(e.getMessage());
        }
        return verticalList;
    }

    // 2
    private ComponentBuilder<?, ?> getLoanDetails4(JSONObject keysForContent, CustomerDataFields customerFields) {
        VerticalListBuilder verticalList = cmp.verticalList();
        try {
            Gson gsonObj = new Gson();
            CibilDetailsPayload cibilDetailsPayload = CommonUtils.resolveCibilPayload(customerFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId, gsonObj);

            for (CibilDetailsWrapper cibilDetailsWrapper : customerFields.getCibilDetailsWrapperList()) {
                String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
                //	String customerType = applicant ? applicantCustId : coApplicantCustId;
                logger.debug("cibilDetailsPayload Payload : " + cibilDetailsPayload);
                if (custId.equals(coApplicantCustId)) {
                    cibilDetailsPayload = gsonObj.fromJson(
                            cibilDetailsWrapper.getCibilDetails().getPayloadColumn(), CibilDetailsPayload.class);
                }
            }

            Gson gsonObj1 = new Gson();
            LoanDetailsPayload payload = gsonObj1.fromJson(customerFields.getLoanDetails().getPayloadColumn(),
                    LoanDetailsPayload.class);

            logger.debug("LoanDetailsPayload : " + payload);
//			String interest = String
//					.valueOf(req.getLoanDetails().getRoi() == null ? "" : req.getLoanDetails().getRoi().toString());

            String tenure = cibilDetailsPayload.getFinalTenure();
            String interest = (cibilDetailsPayload.getRoi() == null) ? "" : cibilDetailsPayload.getRoi().toString();
            String repaymentFrequency = cibilDetailsPayload.getRepaymentFrequency();

            verticalList.add(createSixHorizontalList(keysForContent.getString("reqstdLoanAmount"),
                    "Rs." + CommonUtils.amountFormat(String.valueOf(customerFields.getLoanDetails().getLoanAmount()))
                            + "/-",
                    keysForContent.getString("loanTenure"), tenure, keysForContent.getString("loanFrequency"),
                    repaymentFrequency));
            verticalList
                    .add(createTwoHorizontalList(keysForContent.getString("loanPurpose"), payload.getLoanPurpose()));
            verticalList.add(createFourHorizontalList(keysForContent.getString("language"), payload.getLanguage(),
                    keysForContent.getString("loanIntrest"), interest + " %"));

        } catch (Exception e) {
            logger.error("error - getLoanDetails");
            logger.error(e.getMessage());
        }
        logger.debug("LoanDetails added");
        return verticalList;
    }

    // @author Abhishek.Raj.CAG
    private ComponentBuilder<?, ?> getLoanDetails4Add(JSONObject keysForContent, CustomerDataFields customerFields, ApplicationMaster applicationMasterData) {
        VerticalListBuilder verticalList = cmp.verticalList();
        try {
            Gson gsonObj = new Gson();
            CibilDetailsPayload cibilPayloadCoApp = null;
            for (CibilDetailsWrapper cibilDetailsWrapper : customerFields.getCibilDetailsWrapperList()) {
                String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
                //String customerType = applicant ? applicantCustId : coApplicantCustId;

                logger.debug("CreditDetailsPayload Payload : " + cibilPayloadCoApp);
                if (custId.equals(coApplicantCustId)) {
                    cibilPayloadCoApp = gsonObj.fromJson(cibilDetailsWrapper.getCibilDetails().getPayloadColumn(),
                            CibilDetailsPayload.class);
                }
            }

            Gson gsonObj1 = new Gson();
            LoanDetailsPayload payload = gsonObj1.fromJson(customerFields.getLoanDetails().getPayloadColumn(),
                    LoanDetailsPayload.class);
            logger.debug("LoanDetailsPayload : " + payload);
//			String interest = String
//					.valueOf(req.getLoanDetails().getRoi() == null ? "" : req.getLoanDetails().getRoi().toString());

            String tenure = cibilPayloadCoApp.getFinalTenure();
            String interest = (cibilPayloadCoApp.getRoi() == null) ? "" : cibilPayloadCoApp.getRoi().toString();

            verticalList.add(createSixHorizontalList(keysForContent.getString("reqstdLoanAmount"),
                    "Rs." + CommonUtils.amountFormat(String.valueOf(customerFields.getLoanDetails().getLoanAmount()))
                            + "/-",
                    keysForContent.getString("loanTenure"), tenure, keysForContent.getString("loanFrequency"),
                    cibilPayloadCoApp.getRepaymentFrequency()));

            // @author Sharath Chandra.CAG

            if (Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                verticalList.add(createTwoHorizontalList(keysForContent.getString("loanPurpose"),
                        payload.getLoanPurpose()));

                verticalList.add(createFourHorizontalList(keysForContent.getString("language"),
                        payload.getLanguage(),
                        keysForContent.getString("loanIntrest"), interest + " %"));
            } else {
                verticalList.add(createFourHorizontalList(
                        keysForContent.getString("loanPurpose"), payload.getLoanPurpose(),
                        keysForContent.getString("loanIntrest"), interest + " %"));
            }

        } catch (Exception e) {
            logger.error("error - getLoanDetails");
            logger.error(e.getMessage());
        }
        logger.debug("LoanDetails added");
        return verticalList;
    }
    //----END----

    // 3
    private ComponentBuilder<?, ?> getCustomerDetails(JSONObject keysForContent, CustomerDataFields req,
                                                      CustomerDetails applicant, CustomerDetails coApplicant, CustomerDetailsPayload payload1,
                                                      CustomerDetailsPayload payload2) {

        String apptDobStr = "";
        String coApptDobStr ="";
        try {
            apptDobStr = CommonUtils.dateFormat3(payload1.getDob());
            coApptDobStr = CommonUtils.dateFormat3(payload2.getDob());
        }catch (Exception e) {
            logger.error("error while date formating added");
        }

        List<ExistingGLLoanDetails> existingGLLoanDetails = req.getExistingGLLoanDetails();
        ExistingGLLoanDetails glLoan = null;
        String existingLoanName = "GL.GRM.UNNATI.LN";

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


        VerticalListBuilder verticalList = cmp.verticalList();
        try {
            verticalList.add(createThreeHorizontalList2(keysForContent.getString(Constants.DETAILS),
                    keysForContent.getString(Constants.APPLICANT_KEY), keysForContent.getString(Constants.COAPPLICANT_KEY)));

            verticalList.add(createThreeHorizontalList(keysForContent.getString("title"), payload1.getTitle(),
                    CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getTitle())));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberFirstName"), applicantFirstname,
                    (coApplicant == null) ? "" : coApplicantFirstname));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberLastName"), CommonUtils.getDefaultValue(applicantLastname),
            		CommonUtils.getDefaultValue((coApplicant == null) ? "" : coApplicantLastname)));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberFullName"), applicantName,
                    (coApplicant == null) ? "" : coApplicantName));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberGender"), payload1.getGender(),
                    CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getGender())));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberDOB"), apptDobStr,
                    CommonUtils.getDefaultValue((coApplicant == null) ? "" : coApptDobStr)));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberAge"), payload1.getAge(),
                    CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getAge())));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberMarritalStstus"),
                    payload1.getMaritalStatus(), CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getMaritalStatus())));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberFather"),
                    CommonUtils.getDefaultValue(payload1.getFathersName()), CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getFathersName())));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberSpouse"),
                    CommonUtils.getDefaultValue(payload1.getSpouseName()), CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getSpouseName())));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberMothersFirstName"), "NA", "NA"));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberMobNumber"),
                    applicant.getMobileNumber(), CommonUtils.getDefaultValue((coApplicant == null) ? "" : coApplicant.getMobileNumber())));
            if(Constants.UNNATI_RESTART_PRODUCT_CODE
                    .equals(req.getApplicationMaster().getProductCode())){
                verticalList.add(createThreeHorizontalList(keysForContent.getString("memberReligion"),
                        glLoan.getReligion(), CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getReligion())));
            }else{
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberReligion"),
                    payload1.getReligion(), CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getReligion())));
            }

        } catch (Exception e) {
            logger.error("error - getCustomerDetails");
            logger.error(e.getMessage());
        }
        logger.debug("CustomerDetails added");
        return verticalList;
    }


    // @author Abhishek.Raj.CAG
    private ComponentBuilder<?, ?> getCustomerDetailsAdd(JSONObject keysForContent, CustomerDataFields req,
                                                         CustomerDetails applicant, CustomerDetails coApplicant, CustomerDetailsPayload payload1,
                                                         CustomerDetailsPayload payload2, ApplicationMaster applicationMasterData) {

        String apptDobStr = "";
        String coApptDobStr ="";
        try {
            apptDobStr = CommonUtils.dateFormat3(payload1.getDob());
            coApptDobStr = CommonUtils.dateFormat3(payload2.getDob());
        }catch (Exception e) {
            logger.error("error while date formating added");
        }
        String apptCkyc = "NA";
        if(payload1.getCkyc() != null && !"0".equals(payload1.getCkyc()) && !payload1.getCkyc().isEmpty()) {
            apptCkyc = payload1.getCkyc();
        }

        String coApptCkyc = "NA";
        if (coApplicant != null && payload2.getCkyc() != null && !"0".equals(payload2.getCkyc()) && !payload2.getCkyc().isEmpty()) {
            coApptCkyc = payload2.getCkyc();
        }

        String apptKycNo = "NA";
        if (payload1 != null) {
            boolean isPrimaryVerified = Constants.VERIFIED_STS.equalsIgnoreCase(payload1.getPrimaryKycIdValStatus());
            boolean isAlternateVerified = Constants.VERIFIED_STS.equalsIgnoreCase(payload1.getAlternateVoterIdValStatus());
            boolean hasAlternateId = org.apache.commons.lang3.StringUtils.isNotBlank(payload1.getAlternateVoterId());
            boolean hasPrimaryId = org.apache.commons.lang3.StringUtils.isNotBlank(payload1.getPrimaryKycId());

            if (isAlternateVerified && hasAlternateId) {
                apptKycNo = payload1.getAlternateVoterId();
            } else if (isPrimaryVerified && hasPrimaryId) {
                apptKycNo = payload1.getPrimaryKycId();
            }else{
                logger.debug(
                        "No valid verified Voter ID found for customerType {}. " +
                                "Alternate verified={}, Alternate blank={}, " +
                                "Primary verified={}, Primary blank={}", "Applicant",
                        isAlternateVerified, !hasAlternateId,
                        isPrimaryVerified, !hasPrimaryId
                );
            }
        }

        String coApptKycNo = "NA";
        if (coApplicant != null && payload2 != null) {
            boolean isPrimaryVerified = Constants.VERIFIED_STS.equalsIgnoreCase(payload2.getPrimaryKycIdValStatus());
            boolean isAlternateVerified = Constants.VERIFIED_STS.equalsIgnoreCase(payload2.getAlternateVoterIdValStatus());
            boolean hasAlternateId = org.apache.commons.lang3.StringUtils.isNotBlank(payload2.getAlternateVoterId());
            boolean hasPrimaryId = org.apache.commons.lang3.StringUtils.isNotBlank(payload2.getPrimaryKycId());

            if (isAlternateVerified && hasAlternateId) {
                coApptKycNo = payload2.getAlternateVoterId();
            } else if (isPrimaryVerified && hasPrimaryId) {
                coApptKycNo = payload2.getPrimaryKycId();
            } else{
                logger.debug(
                        "No valid verified Voter ID found for customerType {}. " +
                                "Alternate verified={}, Alternate blank={}, " +
                                "Primary verified={}, Primary blank={}", "Co-Applicant",
                        isAlternateVerified, !hasAlternateId,
                        isPrimaryVerified, !hasPrimaryId
                );
            }
        }

        VerticalListBuilder verticalList = cmp.verticalList();
        Gson gsonObj = new Gson();
        LoanDetailsPayload payload = gsonObj.fromJson(req.getLoanDetails().getPayloadColumn(), LoanDetailsPayload.class);
        // @author Sharath Chandra.CAG
        try {
            if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                verticalList.add(createThreeHorizontalList2(keysForContent.getString(Constants.DETAILS),
                        keysForContent.getString(Constants.APPLICANT_KEY), keysForContent.getString(Constants.COAPPLICANT_KEY)));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("title"), payload1.getTitle(),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getTitle())));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("memberFirstName"), applicantFirstname,
                        (coApplicant == null) ? "" : coApplicantFirstname));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("memberLastName"), (applicantLastname == null) ? "-" :applicantLastname,
                        (coApplicant == null) ? "-" : coApplicantLastname));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("memberFullName"), applicantName,
                        (coApplicant == null) ? "" : coApplicantName));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("memberGender"), payload1.getGender(),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getGender())));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("memberDOB"), apptDobStr,
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : coApptDobStr)));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("memberMobNumber"),
                        applicant.getMobileNumber(), CommonUtils.getDefaultValue((coApplicant == null) ? "" : coApplicant.getMobileNumber())));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("memberVoterNo"),
                        CommonUtils.getDefaultValue(apptKycNo), CommonUtils.getDefaultValue(coApptKycNo)));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("ckycNumber"), apptCkyc,
//					(coApplicant == null) ? "" : payload2.getCkyc()));
                        coApptCkyc));
            }
            else{
                verticalList.add(createFourHorizontalList(keysForContent.getString("memberFullName"),
                        applicantName, keysForContent.getString("memberDOB"), apptDobStr));

                verticalList.add(createFourHorizontalList(keysForContent.getString("memberGender"),
                        payload1.getGender(), keysForContent.getString("memberMobNumber"), applicant.getMobileNumber()));

                verticalList.add(createFourHorizontalList(keysForContent.getString("ckycNumber"), CommonUtils.getDefaultValue(apptCkyc),
                        keysForContent.getString("memberVoterNo"),
                        apptKycNo));

                verticalList.add(createTwoHorizontalList(keysForContent.getString("language"),
                        payload.getLanguage()));
            }
        } catch (Exception e) {
            logger.error("error - getCustomerDetails");
            logger.error(e.getMessage());
        }
        logger.debug("CustomerDetails added");
        return verticalList;
    }

    private ComponentBuilder<?, ?> getCustomerDetails1(JSONObject keysForContent, CustomerDataFields req,
                                                       CustomerDetails applicant, CustomerDetails coApplicant, CustomerDetailsPayload payload1,
                                                       CustomerDetailsPayload payload2) {
        String apptCkyc = "NA";
        if(payload1.getCkyc() != null && !"0".equals(payload1.getCkyc()) && !payload1.getCkyc().isEmpty()) {
            apptCkyc = payload1.getCkyc();
        }

        String coApptCkyc = "NA";
        if (coApplicant != null && payload2.getCkyc() != null && !"0".equals(payload2.getCkyc()) && !payload2.getCkyc().isEmpty()) {
            coApptCkyc = payload2.getCkyc();
        }

        String apptKycNo = "NA";
        if (payload1 != null) {
            boolean isPrimaryVerified = Constants.VERIFIED_STS.equalsIgnoreCase(payload1.getPrimaryKycIdValStatus());
            boolean isAlternateVerified = Constants.VERIFIED_STS.equalsIgnoreCase(payload1.getAlternateVoterIdValStatus());
            boolean hasAlternateId = org.apache.commons.lang3.StringUtils.isNotBlank(payload1.getAlternateVoterId());
            boolean hasPrimaryId = org.apache.commons.lang3.StringUtils.isNotBlank(payload1.getPrimaryKycId());

            if (isAlternateVerified && hasAlternateId) {
                apptKycNo = payload1.getAlternateVoterId();
            } else if (isPrimaryVerified && hasPrimaryId) {
                apptKycNo = payload1.getPrimaryKycId();
            }else{
                logger.debug(
                        "No valid verified Voter ID found for customerType {}. " +
                                "Alternate verified={}, Alternate blank={}, " +
                                "Primary verified={}, Primary blank={}", "Applicant",
                        isAlternateVerified, !hasAlternateId,
                        isPrimaryVerified, !hasPrimaryId
                );
            }
        }

        String coApptKycNo = "NA";
        if (coApplicant != null && payload2 != null) {
            boolean isPrimaryVerified = Constants.VERIFIED_STS.equalsIgnoreCase(payload2.getPrimaryKycIdValStatus());
            boolean isAlternateVerified = Constants.VERIFIED_STS.equalsIgnoreCase(payload2.getAlternateVoterIdValStatus());
            boolean hasAlternateId = org.apache.commons.lang3.StringUtils.isNotBlank(payload2.getAlternateVoterId());
            boolean hasPrimaryId = org.apache.commons.lang3.StringUtils.isNotBlank(payload2.getPrimaryKycId());

            if (isAlternateVerified && hasAlternateId) {
                coApptKycNo = payload2.getAlternateVoterId();
            } else if (isPrimaryVerified && hasPrimaryId) {
                coApptKycNo = payload2.getPrimaryKycId();
            } else{
                logger.debug(
                        "No valid verified Voter ID found for customerType {}. " +
                                "Alternate verified={}, Alternate blank={}, " +
                                "Primary verified={}, Primary blank={}", "Co-Applicant",
                        isAlternateVerified, !hasAlternateId,
                        isPrimaryVerified, !hasPrimaryId
                );
            }
        }

        VerticalListBuilder verticalList = cmp.verticalList();

        String existingLoanName = "GL.GRM.UNNATI.LN";
        List<ExistingGLLoanDetails> existingGLLoanDetails = req.getExistingGLLoanDetails();
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
        try {
            if(Constants.UNNATI_RESTART_PRODUCT_CODE
                    .equals(req.getApplicationMaster().getProductCode())){
                verticalList.add(createThreeHorizontalList(keysForContent.getString("memberCast"), glLoan.getCaste(),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getCaste())));
            }else{
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberCast"), payload1.getCaste(),
                    CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getCaste())));
            }
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberVoterNo"),
                    CommonUtils.getDefaultValue(apptKycNo), CommonUtils.getDefaultValue(coApptKycNo)));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("otherKYCNameIfAny"),
                    CommonUtils.getDefaultValue(payload1.getSecondaryKycType()), CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getSecondaryKycType())));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("otherKYCNumberIfAny"),
                    CommonUtils.getDefaultValue(payload1.getSecondaryKycId()), CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getSecondaryKycId())));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("ckycNumber"), apptCkyc,
//					(coApplicant == null) ? "" : payload2.getCkyc()));
                    coApptCkyc));
//---------------Swaroop Raj R_CR86   11/12/2025  --------------------------------
            verticalList.add(createThreeHorizontalList(keysForContent.getString("relationshipWithApplicant"), "Self", CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getRelationShipWithApplicant())));
//--------------------------------------------------
        } catch (Exception e) {
            logger.error("error - getCustomerDetails1");
            logger.error(e.getMessage());
        }
        logger.debug("CustomerDetails added");
        return verticalList;
    }

    // @author Abhishek.Raj.CAG
//    private ComponentBuilder<?, ?> getCustomerDetails1Add(JSONObject keysForContent, CustomerDataFields req,
//                                                          CustomerDetails applicant, CustomerDetails coApplicant, CustomerDetailsPayload payload1,
//                                                          CustomerDetailsPayload payload2) {
//        String apptCkyc = "NA";
//        if(payload1.getCkyc() != null && !"0".equals(payload1.getCkyc()) && !payload1.getCkyc().isEmpty()) {
//            apptCkyc = payload1.getCkyc();
//        }
//
//        String coApptCkyc = "NA";
//        if (coApplicant != null && payload2.getCkyc() != null && !"0".equals(payload2.getCkyc()) && !payload2.getCkyc().isEmpty()) {
//            coApptCkyc = payload2.getCkyc();
//        }
//
//        String apptKycNo = "NA";
//        if (payload1 != null) {
//            boolean isPrimaryVerified = Constants.VERIFIED_STS.equalsIgnoreCase(payload1.getPrimaryKycIdValStatus());
//            boolean isAlternateVerified = Constants.VERIFIED_STS.equalsIgnoreCase(payload1.getAlternateVoterIdValStatus());
//            boolean hasAlternateId = org.apache.commons.lang3.StringUtils.isNotBlank(payload1.getAlternateVoterId());
//            boolean hasPrimaryId = org.apache.commons.lang3.StringUtils.isNotBlank(payload1.getPrimaryKycId());
//
//            if (isAlternateVerified && hasAlternateId) {
//                apptKycNo = payload1.getAlternateVoterId();
//            } else if (isPrimaryVerified && hasPrimaryId) {
//                apptKycNo = payload1.getPrimaryKycId();
//            }else{
//                logger.debug(
//                        "No valid verified Voter ID found for customerType {}. " +
//                                "Alternate verified={}, Alternate blank={}, " +
//                                "Primary verified={}, Primary blank={}", "Applicant",
//                        isAlternateVerified, !hasAlternateId,
//                        isPrimaryVerified, !hasPrimaryId
//                );
//            }
//        }
//
//        String coApptKycNo = "NA";
//        if (coApplicant != null && payload2 != null) {
//            boolean isPrimaryVerified = Constants.VERIFIED_STS.equalsIgnoreCase(payload2.getPrimaryKycIdValStatus());
//            boolean isAlternateVerified = Constants.VERIFIED_STS.equalsIgnoreCase(payload2.getAlternateVoterIdValStatus());
//            boolean hasAlternateId = org.apache.commons.lang3.StringUtils.isNotBlank(payload2.getAlternateVoterId());
//            boolean hasPrimaryId = org.apache.commons.lang3.StringUtils.isNotBlank(payload2.getPrimaryKycId());
//
//            if (isAlternateVerified && hasAlternateId) {
//                coApptKycNo = payload2.getAlternateVoterId();
//            } else if (isPrimaryVerified && hasPrimaryId) {
//                coApptKycNo = payload2.getPrimaryKycId();
//            } else{
//                logger.debug(
//                        "No valid verified Voter ID found for customerType {}. " +
//                                "Alternate verified={}, Alternate blank={}, " +
//                                "Primary verified={}, Primary blank={}", "Co-Applicant",
//                        isAlternateVerified, !hasAlternateId,
//                        isPrimaryVerified, !hasPrimaryId
//                );
//            }
//        }
//
//
//        VerticalListBuilder verticalList = cmp.verticalList();
//        try {
//
//            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberVoterNo"),
//                    CommonUtils.getDefaultValue(apptKycNo), CommonUtils.getDefaultValue(coApptKycNo)));
//            verticalList.add(createThreeHorizontalList(keysForContent.getString("ckycNumber"), apptCkyc,
    ////					(coApplicant == null) ? "" : payload2.getCkyc()));
//                    coApptCkyc));
//
//        } catch (Exception e) {
//            logger.error("error - getCustomerDetails1");
//            logger.error(e.getMessage());
//        }
//        logger.debug("CustomerDetails added");
//        return verticalList;
//    }


    // 4
    // Address Applicant and Co-Applicant
    private ComponentBuilder<?, ?> getAddressDetails(JSONObject keysForContent, CustomerDataFields req, ApplicationMaster applicationMasterData) {
        VerticalListBuilder verticalList = cmp.verticalList();

        try {
            JSONObject addressDetailsObj = getAllAddressDeatils(req);

            verticalList.add(createThreeHorizontalList2(keysForContent.getString(Constants.DETAILS),
                    keysForContent.getString(Constants.APPLICANT_KEY), keysForContent.getString(Constants.COAPPLICANT_KEY)));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("presentAddress"),
                        addressDetailsObj.getString("presentAddressApplicant"),
                        addressDetailsObj.getString("presentAddressCoApplicant")));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("permanentAddress"),
                        addressDetailsObj.getString("permanetAddressApplicant"),
                        addressDetailsObj.getString("permanetAddressCoApplicant")));


        } catch (Exception e) {
            logger.error("error - getAddressDetails");
            logger.error(e.getMessage());
        }
        logger.debug("AddressDetails added");
        return verticalList;
    }

    private ComponentBuilder<?, ?> getAddressDetailsAdd(JSONObject keysForContent, CustomerDataFields req, ApplicationMaster applicationMasterData) {
        VerticalListBuilder verticalList = cmp.verticalList();

        try {
            JSONObject addressDetailsObj = getAllAddressDeatils(req);

            if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                verticalList.add(createThreeHorizontalList2(keysForContent.getString(Constants.DETAILS),
                        keysForContent.getString(Constants.APPLICANT_KEY), keysForContent.getString(Constants.COAPPLICANT_KEY)));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("presentAddress"),
                        addressDetailsObj.getString("presentAddressApplicant"),
                        addressDetailsObj.getString("presentAddressCoApplicant")));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("permanentAddress"),
                        addressDetailsObj.getString("permanetAddressApplicant"),
                        addressDetailsObj.getString("permanetAddressCoApplicant")));
            }else{
//                verticalList.add(createTwoHorizontalList(keysForContent.getString(Constants.DETAILS),
//                        keysForContent.getString(Constants.APPLICANT_KEY)));
//                verticalList.add(createTwoHorizontalList(keysForContent.getString("presentAddress"),
//                        addressDetailsObj.getString("presentAddressApplicant")));
//                verticalList.add(createTwoHorizontalList(keysForContent.getString("permanentAddress"),
//                        addressDetailsObj.getString("permanetAddressApplicant")));
//                verticalList.add(createTwoHorizontalList(keysForContent.getString("empLandmark"),
//                        addressDetailsObj.getString("presentLandmarkApplicant")));
                ///
                verticalList.add(createThreeHorizontalList(keysForContent.getString(Constants.DETAILS),
                        keysForContent.getString("presentAddress"),
                        keysForContent.getString("permanentAddress")));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empAddress"),
                        addressDetailsObj.getString("presentAddressApplicant"),
                        addressDetailsObj.getString("permanetAddressApplicant")));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empLandmark"),
                        addressDetailsObj.getString("presentLandmarkApplicant"),
                        addressDetailsObj.getString("permanentLandmarkApplicant")));
            }
        } catch (Exception e) {
            logger.error("error - getAddressDetailsAdd");
            logger.error(e.getMessage());
        }
        logger.debug("getAddressDetailsAdd added");
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

    // 5. Other Deatails
    private ComponentBuilder<?, ?> getOtherDetails(JSONObject keysForContent, CustomerDataFields req,ApplicationMaster applicationMasterData) {
        VerticalListBuilder verticalList = cmp.verticalList();
        try {

            JSONObject addressDetailsObj = getAllAddressDeatils(req);
            if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())){
                verticalList.add(createThreeHorizontalList(
                        keysForContent.getString("address"),
                        addressDetailsObj.getString("occpnAddrApplicant"),
                        addressDetailsObj.getString("occpnAddrCoApplicant")));

                verticalList.add(createThreeHorizontalList(
                        keysForContent.getString("landmark"),
                        addressDetailsObj.getString("occpnLandmarkApplicant"),
                        addressDetailsObj.getString("occpnLandmarkCoApplicant")));
            }
            verticalList.add(createThreeHorizontalList2(keysForContent.getString(Constants.DETAILS),
                    keysForContent.getString(Constants.APPLICANT_KEY), keysForContent.getString(Constants.COAPPLICANT_KEY)));

            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentResidenceOwnership"),
                    addressDetailsObj.getString("presentResidenceOwnership"),
                    addressDetailsObj.getString("presentResidenceOwnershipCo")));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentAddressYears"),
                    addressDetailsObj.getString("presentAddressYears"),
                    addressDetailsObj.getString("presentAddressYearsCo")));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("presntCityYears"),
                    addressDetailsObj.getString("presntCityYears"), addressDetailsObj.getString("presntCityYearsCo")));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentResidenceAddressProof"),
                    addressDetailsObj.getString("presentResidenceAddressProof"),
                    addressDetailsObj.getString("presentResidenceAddressProofCo")));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentResidenceType"),
                    addressDetailsObj.getString("presentResidenceType"),
                    addressDetailsObj.getString("presentResidenceTypeCo")));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentResidenceSize"),
                    addressDetailsObj.getString("presentResidenceSize"),
                    addressDetailsObj.getString("presentResidenceSizeCo")));
        } catch (Exception e) {
            logger.error("error - getOtherDetails");
            logger.error(e.getMessage());
        }
        logger.debug("otherDetails added");
        return verticalList;
    }

    private ComponentBuilder<?, ?> getOtherDetailsAdd(JSONObject keysForContent, CustomerDataFields req,ApplicationMaster applicationMasterData) {
        VerticalListBuilder verticalList = cmp.verticalList();
        logger.debug("getOtherDetailsAdd start");
        try {
            logger.debug("getOtherDetailsAdd start try block");
            JSONObject addressDetailsObj = getAllAddressDeatils(req);



                verticalList.add(createThreeHorizontalList(
                        keysForContent.getString("details"),
                        keysForContent.getString("applicant") + " " + keysForContent.getString("employemnentDetails"),
                        keysForContent.getString("coApplicant") + " " + keysForContent.getString("employemnentDetails")));

                verticalList.add(createThreeHorizontalList(
                        keysForContent.getString("empAddress"),
                        addressDetailsObj.getString("occpnAddrApplicant"),
                        (addressDetailsObj.getString("occpnAddrCoApplicant") != null
                                && !addressDetailsObj.getString("occpnAddrCoApplicant").trim().isEmpty())
                                ? addressDetailsObj.getString("occpnAddrCoApplicant")
                                : "NA"
                ));

                verticalList.add(createThreeHorizontalList(
                        keysForContent.getString("landmark"),
                        addressDetailsObj.getString("occpnLandmarkApplicant"),
                        (addressDetailsObj.getString("occpnLandmarkCoApplicant") != null
                                && !addressDetailsObj.getString("occpnLandmarkCoApplicant").trim().isEmpty())
                                ? addressDetailsObj.getString("occpnLandmarkCoApplicant")
                                : "NA"
                ));


//            verticalList.add(createThreeHorizontalList2(keysForContent.getString(Constants.DETAILS),
//                    keysForContent.getString(Constants.APPLICANT_KEY), keysForContent.getString(Constants.COAPPLICANT_KEY)));
//
//            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentResidenceOwnership"),
//                    addressDetailsObj.getString("presentResidenceOwnership"),
//                    addressDetailsObj.getString("presentResidenceOwnershipCo")));
//            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentAddressYears"),
//                    addressDetailsObj.getString("presentAddressYears"),
//                    addressDetailsObj.getString("presentAddressYearsCo")));
//            verticalList.add(createThreeHorizontalList(keysForContent.getString("presntCityYears"),
//                    addressDetailsObj.getString("presntCityYears"), addressDetailsObj.getString("presntCityYearsCo")));
//            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentResidenceAddressProof"),
//                    addressDetailsObj.getString("presentResidenceAddressProof"),
//                    addressDetailsObj.getString("presentResidenceAddressProofCo")));
//            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentResidenceType"),
//                    addressDetailsObj.getString("presentResidenceType"),
//                    addressDetailsObj.getString("presentResidenceTypeCo")));
//            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentResidenceSize"),
//                    addressDetailsObj.getString("presentResidenceSize"),
//                    addressDetailsObj.getString("presentResidenceSizeCo")));

        } catch (Exception e) {
            logger.error("error - getOtherDetailsAdd");
            logger.error(e.getMessage());
        }
        logger.debug("getOtherDetailsAdd added");
        return verticalList;
    }


    private ComponentBuilder<?, ?> getOtherDetailsAddOld2608(JSONObject keysForContent, CustomerDataFields req,ApplicationMaster applicationMasterData) {
        VerticalListBuilder verticalList = cmp.verticalList();
        logger.debug("getOtherDetailsAdd start");
        try {
            logger.debug("getOtherDetailsAdd start try block");
            JSONObject addressDetailsObj = getAllAddressDeatils(req);

            if(Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())){
                logger.debug("getOtherDetailsAdd start try  if condition");
                verticalList.add(createThreeHorizontalList(
                        keysForContent.getString("address"),
                        addressDetailsObj.getString("occpnAddrApplicant"),
                        addressDetailsObj.getString("occpnAddrCoApplicant")));

                verticalList.add(createThreeHorizontalList(
                        keysForContent.getString("landmark"),
                        addressDetailsObj.getString("occpnLandmarkApplicant"),
                        addressDetailsObj.getString("occpnLandmarkCoApplicant")));
                logger.debug("getOtherDetailsAdd end try  if condition");
            }

            verticalList.add(createThreeHorizontalList2(keysForContent.getString(Constants.DETAILS),
                    keysForContent.getString(Constants.APPLICANT_KEY), keysForContent.getString(Constants.COAPPLICANT_KEY)));

            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentResidenceOwnership"),
                    addressDetailsObj.getString("presentResidenceOwnership"),
                    addressDetailsObj.getString("presentResidenceOwnershipCo")));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentAddressYears"),
                    addressDetailsObj.getString("presentAddressYears"),
                    addressDetailsObj.getString("presentAddressYearsCo")));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("presntCityYears"),
                    addressDetailsObj.getString("presntCityYears"), addressDetailsObj.getString("presntCityYearsCo")));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentResidenceAddressProof"),
                    addressDetailsObj.getString("presentResidenceAddressProof"),
                    addressDetailsObj.getString("presentResidenceAddressProofCo")));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentResidenceType"),
                    addressDetailsObj.getString("presentResidenceType"),
                    addressDetailsObj.getString("presentResidenceTypeCo")));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("presentResidenceSize"),
                    addressDetailsObj.getString("presentResidenceSize"),
                    addressDetailsObj.getString("presentResidenceSizeCo")));

        } catch (Exception e) {
            logger.error("error - getOtherDetailsAdd");
            logger.error(e.getMessage());
        }
        logger.debug("getOtherDetailsAdd added");
        return verticalList;
    }






    private String applicantValueNA(boolean isHomeMaker, String value) {
        return isHomeMaker ? "NA" : CommonUtils.getDefaultValue(value);
    }

    // 6.
    private ComponentBuilder<?, ?> getEmployemnentDetails(JSONObject keysForContent, CustomerDataFields req,
                                                          CustomerDetailsPayload custPayload1, CustomerDetailsPayload custPayload2, ApplicationMaster applicationMasterData) {
        VerticalListBuilder verticalList = cmp.verticalList();
        String newSourcing = req.getApplicationMaster().getAssignedTo();

        if (StringUtils.isEmpty(newSourcing) || newSourcing.equalsIgnoreCase("N")) {
            logger.debug("KM Sourcing Old Flow Triggered for Occupation Details");
            try {
                Gson gsonObj = new Gson();
                OccupationDetails coApplicant = null;
                OccupationDetailsPayload payload1 = null;
                OccupationDetailsPayload payload2 = null;
                for (OccupationDetailsWrapper applicantwrpr : req.getOccupationDetailsWrapperList()) {
                    logger.debug("applicantCustId " + applicantCustId);
                    if (String.valueOf(applicantwrpr.getOccupationDetails().getCustDtlId()).equals(applicantCustId)) {
                        payload1 = gsonObj.fromJson(applicantwrpr.getOccupationDetails().getPayloadColumn(),
                                OccupationDetailsPayload.class);
                        logger.debug("occupationApplicantPayload : " + payload1.toString());
                    } else if (String.valueOf(applicantwrpr.getOccupationDetails().getCustDtlId())
                            .equals(coApplicantCustId)) {
                        coApplicant = applicantwrpr.getOccupationDetails();
                        payload2 = gsonObj.fromJson(applicantwrpr.getOccupationDetails().getPayloadColumn(),
                                OccupationDetailsPayload.class);
                        logger.debug("occupationCo-appliocantPayload : " + payload2.toString());
                    }
                }


                boolean isApplicantHomeMaker =
                        payload1 != null &&
                                Constants.HOMEMAKER.equalsIgnoreCase(custPayload1.getOccupation());
                boolean isCoapplicantHomeMaker = custPayload2.getOccupation().equalsIgnoreCase(Constants.HOMEMAKER);


                JSONObject addressDetailsObj = getAllAddressDeatils(req);

                verticalList.add(createThreeHorizontalList2(keysForContent.getString(Constants.DETAILS),
                        keysForContent.getString(Constants.APPLICANT_KEY), keysForContent.getString(Constants.COAPPLICANT_KEY)));


                verticalList.add(createThreeHorizontalList(keysForContent.getString("employment"), custPayload1.getOccupation(),
                        ((coApplicant == null && !isCoapplicantHomeMaker) ? "" : getValueIfNotNull(custPayload2.getOccupation()))));

                logger.debug("OccupationType : " + applicantValueNA(isApplicantHomeMaker, payload1.getOccupationType()));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("employmentTye"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getOccupationType()),
                        (coApplicant == null) ? "NA" : getValueIfNotNull(payload2.getOccupationType())));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empFormat"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getNatureOfOccupation()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getNatureOfOccupation()))));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empActivity"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getEmployeeActivity()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getEmployeeActivity()))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empOrganization"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getOrganisationName()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getOrganisationName()))));
                verticalList.add(
                        createThreeHorizontalList(keysForContent.getString("empstreetVendor"),
                                applicantValueNA(isApplicantHomeMaker, payload1.getStreetVendor()),
                                CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getStreetVendor()))));
                if(Constants.UNNATI_RESTART_PRODUCT_CODE.equals(applicationMasterData.getProductCode())){
                    verticalList.add(createThreeHorizontalList(keysForContent.getString("empStrtDate"),
                            applicantValueNA(isApplicantHomeMaker, CommonUtils.formatdate(LocalDate.parse(payload1.getBusinessEmpStartDate()))),
                            CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(CommonUtils.formatdate(LocalDate.parse((payload2.getBusinessEmpStartDate())))))));
                }else{
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empStrtDate"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getBusinessEmpStartDate()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getBusinessEmpStartDate()))));
                }
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empExprience"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getBusinessEmpVintageYear()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getBusinessEmpVintageYear()))));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empAddress"),
                        applicantValueNA(isApplicantHomeMaker, addressDetailsObj.getString("occpnAddrApplicant")), (coApplicant == null) ? ""
                                : getValueIfNotNull(addressDetailsObj.getString("occpnAddrCoApplicant"))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empAddressProof"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getBusinessAddressProof()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getBusinessAddressProof()))));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empProof"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getEmploymentProof()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getEmploymentProof()))));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empSize"), "NA", "NA"));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empOwnership"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getBusinessPremiseOwnerShip()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getBusinessPremiseOwnerShip()))));

            } catch (Exception e) {
                logger.error("error - EmployemnentDetails");
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            logger.debug("Occupation Details added");
            return verticalList;
        }
        else
        {
            logger.debug("KM Sourcing New Flow Triggered for Occupation Details");
            //KMSourcing New Flow BCMP
            Map<String, String> map1 = new HashMap<>(); // Applicant
            Map<String, String> map2 = new HashMap<>(); // Co-Applicant
            try {
                boolean[] applicantTypes = {false, true};

                for (boolean useCoApplicant : applicantTypes) {
                    logger.debug("Getting Data from getKMNewOccupationData");
                    Map<String, String> getKMNewOccupationData = getNewKMOccupationDetails(req, useCoApplicant);
                    logger.debug("Data from getKMNewOccupationData : " + getKMNewOccupationData);
                    if (useCoApplicant) {
                        map2 = getKMNewOccupationData;
                        logger.debug("Getting Customers for CoApplicant : "+ map2);
                    } else {
                        map1 = getKMNewOccupationData;
                        logger.debug("Getting Customers for Applicant : "+ map1);
                    }
                }

                verticalList.add(createThreeHorizontalList2(keysForContent.getString(Constants.DETAILS),
                        keysForContent.getString(Constants.APPLICANT_KEY),
                        keysForContent.getString(Constants.COAPPLICANT_KEY)));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("employment"),
                        getValueIfNotNull(map1.get("occupationType")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("occupationType")))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("employmentTye"),
                        getValueIfNotNull(map1.get("typeOfBusiness")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("typeOfBusiness")))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empFormat"),
                        getValueIfNotNull(map1.get("natureOfBusiness")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("natureOfBusiness")))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empActivity"),
                        getValueIfNotNull(map1.get("EmploymentActivity")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("EmploymentActivity")))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empOrganization"),
                        getValueIfNotNull(map1.get("NameofOrganization")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("NameofOrganization")))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empstreetVendor"),
                        getValueIfNotNull(map1.get("StreetVendor")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("StreetVendor")))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empStrtDate"),
                        getValueIfNotNull(map1.get("EmploymentStartDate")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("EmploymentStartDate")))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empExprience"),
                        getValueIfNotNull(map1.get("EmployemntVintage")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("EmployemntVintage")))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empAddress"),
                        getValueIfNotNull(map1.get("Address")),
                        getValueIfNotNull(map2.get("Address"))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empAddressProof"),
                        getValueIfNotNull(map1.get("BusinessAdreesProof")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("BusinessAdreesProof")))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empProof"),
                        getValueIfNotNull(map1.get("EmploymentProof")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("EmploymentProof")))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empSize"),
                        getValueIfNotNull(map1.get("BusinessPremiseArea")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("BusinessPremiseArea")))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empOwnership"),
                        getValueIfNotNull(map1.get("BusinessPremiseOwnship")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("BusinessPremiseOwnship")))));
                logger.debug("Occupation Details added :" + verticalList);
            }catch (Exception e) {
                logger.error("error - EmployemnentDetails");
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            logger.debug("Occupation Details added to List :" + verticalList);
            return verticalList;
        }
    }

    // @author Abhishek.Raj
    // 6.
    private ComponentBuilder<?, ?> getEmployemnentDetailsUnnatiLight(JSONObject keysForContent, CustomerDataFields req,
                                                          CustomerDetailsPayload custPayload1, CustomerDetailsPayload custPayload2) {
        VerticalListBuilder verticalList = cmp.verticalList();
        String newSourcing = req.getApplicationMaster().getAssignedTo();

        if (StringUtils.isEmpty(newSourcing) || newSourcing.equalsIgnoreCase("N")) {
            logger.debug("getEmployemnentDetailsUnnatiLight- KM Sourcing Old Flow Triggered for Occupation Details");
            try {
                Gson gsonObj = new Gson();
                OccupationDetails coApplicant = null;
                OccupationDetailsPayload payload1 = null;
                OccupationDetailsPayload payload2 = null;
                for (OccupationDetailsWrapper applicantwrpr : req.getOccupationDetailsWrapperList()) {
                    logger.debug("applicantCustId " + applicantCustId);
                    if (String.valueOf(applicantwrpr.getOccupationDetails().getCustDtlId()).equals(applicantCustId)) {
                        payload1 = gsonObj.fromJson(applicantwrpr.getOccupationDetails().getPayloadColumn(),
                                OccupationDetailsPayload.class);
                        logger.debug("occupationApplicantPayload : " + payload1.toString());
                    } else if (String.valueOf(applicantwrpr.getOccupationDetails().getCustDtlId())
                            .equals(coApplicantCustId)) {
                        coApplicant = applicantwrpr.getOccupationDetails();
                        payload2 = gsonObj.fromJson(applicantwrpr.getOccupationDetails().getPayloadColumn(),
                                OccupationDetailsPayload.class);
                        logger.debug("occupationCo-appliocantPayload : " + payload2.toString());
                    }
                }


                boolean isApplicantHomeMaker =
                        payload1 != null &&
                                Constants.HOMEMAKER.equalsIgnoreCase(custPayload1.getOccupation());
                boolean isCoapplicantHomeMaker = custPayload2.getOccupation().equalsIgnoreCase(Constants.HOMEMAKER);


                JSONObject addressDetailsObj = getAllAddressDeatils(req);

                verticalList.add(createThreeHorizontalList2(keysForContent.getString(Constants.DETAILS),
                        keysForContent.getString(Constants.APPLICANT_KEY), keysForContent.getString(Constants.COAPPLICANT_KEY)));


                verticalList.add(createThreeHorizontalList(keysForContent.getString("employment"), custPayload1.getOccupation(),
                        ((coApplicant == null && !isCoapplicantHomeMaker) ? "" : getValueIfNotNull(custPayload2.getOccupation()))));

                logger.debug("OccupationType : " + applicantValueNA(isApplicantHomeMaker, payload1.getOccupationType()));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("employmentTye"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getOccupationType()),
                        (coApplicant == null) ? "NA" : getValueIfNotNull(payload2.getOccupationType())));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empFormat"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getNatureOfOccupation()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getNatureOfOccupation()))));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empActivity"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getEmployeeActivity()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getEmployeeActivity()))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empOrganization"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getOrganisationName()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getOrganisationName()))));
                verticalList.add(
                        createThreeHorizontalList(keysForContent.getString("empstreetVendor"),
                                applicantValueNA(isApplicantHomeMaker, payload1.getStreetVendor()),
                                CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getStreetVendor()))));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empStrtDate"),
                        applicantValueNA(isApplicantHomeMaker, CommonUtils.formatdate(LocalDate.parse(payload1.getBusinessEmpStartDate()))),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(CommonUtils.formatdate(LocalDate.parse(payload2.getBusinessEmpStartDate()))))));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empExprience"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getBusinessEmpVintageYear()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getBusinessEmpVintageYear()))));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empAddress"),
                        applicantValueNA(isApplicantHomeMaker, addressDetailsObj.getString("occpnAddrApplicant")), (coApplicant == null) ? ""
                                : getValueIfNotNull(addressDetailsObj.getString("occpnAddrCoApplicant"))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empAddressProof"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getBusinessAddressProof()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getBusinessAddressProof()))));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empProof"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getEmploymentProof()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getEmploymentProof()))));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empSize"), "NA", "NA"));
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empOwnership"),
                        applicantValueNA(isApplicantHomeMaker, payload1.getBusinessPremiseOwnerShip()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getBusinessPremiseOwnerShip()))));

            } catch (Exception e) {
                logger.error("error - EmployemnentDetails");
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            logger.debug("Occupation Details added");
            return verticalList;
        }
        else
        {
            logger.debug("KM Sourcing New Flow Triggered for Occupation Details");
            //KMSourcing New Flow BCMP
            Map<String, String> map1 = new HashMap<>(); // Applicant
            Map<String, String> map2 = new HashMap<>(); // Co-Applicant
            try {
                boolean[] applicantTypes = {false, true};

                for (boolean useCoApplicant : applicantTypes) {
                    logger.debug("Getting Data from getKMNewOccupationData");
                    Map<String, String> getKMNewOccupationData = getNewKMOccupationDetails(req, useCoApplicant);
                    logger.debug("Data from getKMNewOccupationData : " + getKMNewOccupationData);
                    if (useCoApplicant) {
                        map2 = getKMNewOccupationData;
                        logger.debug("Getting Customers for CoApplicant : "+ map2);
                    } else {
                        map1 = getKMNewOccupationData;
                        logger.debug("Getting Customers for Applicant : "+ map1);
                    }
                }

                verticalList.add(createThreeHorizontalList2(keysForContent.getString(Constants.DETAILS),
                        keysForContent.getString(Constants.APPLICANT_KEY),
                        keysForContent.getString(Constants.COAPPLICANT_KEY)));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("employment"),
                        "NA",
                        "NA"));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("employmentTye"),
                        "NA",
                        "NA"));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empFormat"),
                        "NA",
                        "NA"));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empActivity"),
                        "NA",
                        "NA"));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empOrganization"),
                        "NA",
                        "NA"));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empstreetVendor"),
                        "NA",
                        "NA"));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empStrtDate"),
                        "NA",
                        "NA"));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empExprience"),
                        "NA",
                        "NA"));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empAddress"),
                        "NA",
                        "NA"));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empAddressProof"),
                        "NA",
                        "NA"));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empProof"),
                        "NA",
                        "NA"));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empSize"),
                        "NA",
                        "NA"));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empOwnership"),
                        "NA",
                        "NA"));
                logger.debug("Occupation Details added :" + verticalList);
            }catch (Exception e) {
                logger.error("error - EmployemnentDetails");
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            logger.debug("Occupation Details added to List :" + verticalList);
            return verticalList;
        }
    }

    //
    private String getValueIfNotNull(String val) {
        if (StringUtils.isEmpty(val) || Constants.PLEASE_SELECT.equalsIgnoreCase(val.trim())) {
            return "NA";
        }
        return val.trim();
    }


    // 7.
    private ComponentBuilder<?, ?> getIncomeDetails4(JSONObject keysForContent, CustomerDataFields req) {
        VerticalListBuilder verticalList = cmp.verticalList();
        String newSourcing = req.getApplicationMaster().getAssignedTo();

        if (StringUtils.isEmpty(newSourcing) || newSourcing.equalsIgnoreCase("N")) {
            logger.debug("KM Souring Old Flow triggered for Income details");
            try {
                Gson gsonObj = new Gson();
                OccupationDetails coApplicant = null;
                OccupationDetailsPayload payload1 = new OccupationDetailsPayload();
                OccupationDetailsPayload payload2 = new OccupationDetailsPayload();
                for (OccupationDetailsWrapper applicantwrpr : req.getOccupationDetailsWrapperList()) {
                    if (String.valueOf(applicantwrpr.getOccupationDetails().getCustDtlId()).equals(applicantCustId)) {
                        payload1 = gsonObj.fromJson(applicantwrpr.getOccupationDetails().getPayloadColumn(),
                                OccupationDetailsPayload.class);
                        logger.debug("IncomeDetailsApplicantPayload : " + payload1);
                    } else if (String.valueOf(applicantwrpr.getOccupationDetails().getCustDtlId())
                            .equals(coApplicantCustId)) {
                        coApplicant = applicantwrpr.getOccupationDetails();
                        payload2 = gsonObj.fromJson(applicantwrpr.getOccupationDetails().getPayloadColumn(),
                                OccupationDetailsPayload.class);
                        logger.debug("IncomeDetailsCo-ApplicantPayload : " + payload2);
                    }
                }


                verticalList.add(createThreeHorizontalList2(keysForContent.getString(Constants.DETAILS),
                        keysForContent.getString(Constants.APPLICANT_KEY), keysForContent.getString(Constants.COAPPLICANT_KEY)));

                verticalList.add(
                        createThreeHorizontalList(keysForContent.getString("empIncomeType"), payload1.getModeOfIncome().trim().isEmpty() ? "NA" :  payload1.getModeOfIncome(),
                                (coApplicant == null) ? "" : payload2.getModeOfIncome().trim().isEmpty() ? "NA" :  payload2.getModeOfIncome()));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empIncomeFrequency"),
                        getValueIfNotNull(payload1.getFreqOfIncome()),
                        CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getFreqOfIncome()))));

                String appAnIncome = formatAmount(payload1.getAnnualIncome());
                String coAppAnIncome = formatAmount(payload2.getAnnualIncome());

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empYearlyIncome"),
                        "Rs." + appAnIncome + "/-", "Rs." + coAppAnIncome + "/-"));
//				CommonUtils.amountFormat(String.valueOf(payload1.getAnnualIncome()))+ "/-",
//				CommonUtils.amountFormat(String.valueOf(payload2.getAnnualIncome()))+ "/-"));

                String appOtherIncome = formatAmount1(payload1.getOtherSourceAnnualIncome());
                String coAppOtherIncome = formatAmount1(
                        (coApplicant == null) ? "0" : payload2.getOtherSourceAnnualIncome());

                verticalList.add(createThreeHorizontalList(keysForContent.getString("otherSourcesOfAnnualIncome"),
                        "Rs." + appOtherIncome + "/-", "Rs." + coAppOtherIncome + "/-"));
//					CommonUtils.amountFormat(payload1.getOtherSourceAnnualIncome())+ "/-" ,
//					(coApplicant == null) ? "" : CommonUtils.amountFormat(payload2.getOtherSourceAnnualIncome())+ "/-"));
            } catch (Exception e) {
                logger.error("error - getIncomeDetails");
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            logger.debug("Income Details added");
            return verticalList;
        }else {

            //KMSourcing New Flow BCMP
            logger.debug("KM Souring New Flow triggered for Income details");
            Map<String, String> map1 = null; // Applicant
            Map<String, String> map2 = null; // Co-Applicant

            try {
                boolean[] applicantTypes = {false, true};

                for (boolean useCoApplicant : applicantTypes) {
                    logger.debug("Getting Data from getNewKMIncomeDetails");
                    Map<String, String> getKMNewIncomeData = getNewKMIncomeDetails(req, useCoApplicant);
                    logger.debug("Data from getNewKMIncomeDetails :" + getKMNewIncomeData);
                    if (useCoApplicant) {
                        map2 = getKMNewIncomeData;
                        logger.debug("Getting Customers for CoApplicant : "+ map2);
                    } else {
                        map1 = getKMNewIncomeData;
                        logger.debug("Getting Customers for Applicant : "+ map1);
                    }
                }

                verticalList.add(createThreeHorizontalList2(keysForContent.getString(Constants.DETAILS),
                        keysForContent.getString(Constants.APPLICANT_KEY),
                        keysForContent.getString(Constants.COAPPLICANT_KEY)));

                // ---- Income rows sourced from getNewKMIncomeDetails (map1 / map2) ----
                verticalList.add(createThreeHorizontalList(keysForContent.getString("empIncomeType"),
                        getValueIfNotNull(map1.get("modeOfIncome")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("modeOfIncome")))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empIncomeFrequency"),
                        getValueIfNotNull(map1.get("frequencyOfIncome")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("frequencyOfIncome")))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("empYearlyIncome"),
                        getValueIfNotNull(map1.get("annualIncome")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("annualIncome")))));

                verticalList.add(createThreeHorizontalList(keysForContent.getString("otherSourcesOfAnnualIncome"),
                        getValueIfNotNull(map1.get("otherSourceIncome")),
                        CommonUtils.getDefaultValue(getValueIfNotNull(map2.get("otherSourceIncome")))));
                logger.debug("Income Details added : "+ verticalList);
            } catch (Exception e) {
                logger.error("error - EmployemnentDetails");
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            logger.debug("Income Details added to List: "+ verticalList.toString());
            return verticalList;
        }
    }

    private String formatAmount(BigDecimal amount) {
        return CommonUtils.amountFormat(amount == null ? "0" : String.valueOf(amount));
    }

    private String formatAmount1(String amount) {
        return CommonUtils.amountFormat(amount == null ? "0" : amount);
    }

    // 8.
    private ComponentBuilder<?, ?> getBankDetails4(JSONObject keysForContent, CustomerDataFields req) {
        VerticalListBuilder verticalList = cmp.verticalList();
        try {
            Gson gsonObj = new Gson();
            BankDetailsPayload payload = new BankDetailsPayload();

            for (BankDetailsWrapper applicantwrpr : req.getBankDetailsWrapperList()) {
                if (String.valueOf(applicantwrpr.getBankDetails().getCustDtlId()).equals(applicantCustId)) {
                    payload = gsonObj.fromJson(applicantwrpr.getBankDetails().getPayloadColumn(),
                            BankDetailsPayload.class);
                    logger.debug("BankDetailsPayload : " + payload);
                }

            }

//			BankDetails bankDetails = req.getBankDetailsWrapperList().get(0).getBankDetails();
//			BankDetailsPayload payload = gsonObj.fromJson(bankDetails.getPayloadColumn(), BankDetailsPayload.class);
//			logger.debug("BankDetailsPayload : " + payload);

            verticalList.add(createSixHorizontalList2(keysForContent.getString("bankName"),
                    keysForContent.getString("bankbranchName"), keysForContent.getString("accountType"),
                    keysForContent.getString("nameAsperAccount"), keysForContent.getString("accountNo"),
                    keysForContent.getString("ifscCode")));

            verticalList.add(createSixHorizontalList3(payload.getBankName(), payload.getBranchName(),
                    payload.getAccountType(), payload.getAccountName(), payload.getAccountNumber(), payload.getIfsc()));
        } catch (Exception e) {
            logger.error("error - getBankDetails");
            logger.error(e.getMessage());
        }
        logger.debug("Bank Details added");
        return verticalList;
    }

    // 9.
    private ComponentBuilder<?, ?> getBorrowingDetails(JSONObject keysForContent, CustomerDataFields req) {
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(createFiveHorizontalList(keysForContent.getString("loanApplicantType"),
                keysForContent.getString("finaciarName"), keysForContent.getString("loanAmount"),
                keysForContent.getString("pos"), keysForContent.getString("emi")));

//			verticalList.add(createFiveHorizontalList("--", "--", String.valueOf(req.getLoanDetails().getLoanAmount()), "--", "--"));
        verticalList.add(createFiveHorizontalList("--", "--", "--", "--", "--"));

        return verticalList;
    }

    // 10.
    private ComponentBuilder<?, ?> getInsuranceDetails4(JSONObject keysForContent, CustomerDataFields req) {
        VerticalListBuilder verticalList = cmp.verticalList();
        try {
            Gson gsonObj = new Gson();
            // InsuranceDetails coApplicant = null;
            InsuranceDetailsPayload appPayload = new InsuranceDetailsPayload();
            InsuranceDetailsPayload coappPayload = new InsuranceDetailsPayload();
            InsuranceDetailsPayload jointPayload = new InsuranceDetailsPayload();
            for (InsuranceDetailsWrapper insurer : req.getInsuranceDetailsWrapperList()) {
                if (String.valueOf(insurer.getInsuranceDetails().getCustDtlId()).equals(applicantCustId)) {
                    appPayload = gsonObj.fromJson(insurer.getInsuranceDetails().getPayloadColumn(),
                            InsuranceDetailsPayload.class);
                    logger.debug("InsuranceDetailsApplicantPayload3 : " + appPayload);
                } else if (String.valueOf(insurer.getInsuranceDetails().getCustDtlId()).equals(coApplicantCustId)) {
                    // coApplicant = insurer.getInsuranceDetails();
                    coappPayload = gsonObj.fromJson(insurer.getInsuranceDetails().getPayloadColumn(),
                            InsuranceDetailsPayload.class);
                    logger.debug("InsuranceDetailsCo-ApplicantPayload : " + coappPayload);
                } else {
                    jointPayload = gsonObj.fromJson(insurer.getInsuranceDetails().getPayloadColumn(),
                            InsuranceDetailsPayload.class);
                    logger.debug("InsuranceDetailsCo-ApplicantPayload : " + jointPayload);
                }
            }
            String appinsurance = "";
            String appNomName = "-";
            String appNomDob = "-";
            String appNomAge = "-";
            String appNomRel = "-";
            String appNomGender = "-";
            String coappinsurance = "";
            String coappNomName = "-";
            String coappNomDob = "-";
            String coappNomAge = "-";
            String coappNomRel = "-";
            String coappNomGender = "-";
            switch (appPayload.getInsuranceOption()) {
                case Constants.APPLICANT:
                    appinsurance = Constants.YES;
                    coappinsurance = Constants.NO;
                    appNomName = appPayload.getNomineeName();
                    appNomDob = appPayload.getNomineeDob();
                    appNomAge = appPayload.getAge();
                    appNomRel = appPayload.getNomineeRelation();
                    appNomGender = appPayload.getGender();
                    break;
                case Constants.BOTH:
                    appinsurance = Constants.YES;
                    coappinsurance = Constants.YES;
                    appNomName = appPayload.getNomineeName();
                    appNomDob = appPayload.getNomineeDob();
                    appNomAge = appPayload.getAge();
                    appNomRel = appPayload.getNomineeRelation();
                    appNomGender = appPayload.getGender();
                    coappNomName = coappPayload.getNomineeName();
                    coappNomDob = coappPayload.getNomineeDob();
                    coappNomAge = coappPayload.getAge();
                    coappNomRel = coappPayload.getNomineeRelation();
                    coappNomGender = coappPayload.getGender();
                    break;
                case Constants.JOINT:
                    appinsurance = Constants.YES;
                    coappinsurance = Constants.YES;
                    if (Constants.YES.equalsIgnoreCase(appPayload.getNomineeAdded())) {
                        appNomName = jointPayload.getNomineeName();
                        appNomDob = jointPayload.getNomineeDob();
                        appNomAge = jointPayload.getAge();
                        appNomRel = jointPayload.getNomineeRelation();
                        appNomGender = jointPayload.getGender();
                        coappNomName = jointPayload.getNomineeName();
                        coappNomDob = jointPayload.getNomineeDob();
                        coappNomAge = jointPayload.getAge();
                        coappNomRel = jointPayload.getNomineeRelation();
                        coappNomGender = jointPayload.getGender();
                    } else {
                        appNomName = appPayload.getNomineeName();
                        appNomDob = appPayload.getNomineeDob();
                        appNomAge = appPayload.getAge();
                        appNomRel = appPayload.getNomineeRelation();
                        appNomGender = appPayload.getGender();
                        coappNomName = coappPayload.getNomineeName();
                        coappNomDob = coappPayload.getNomineeDob();
                        coappNomAge = coappPayload.getAge();
                        coappNomRel = coappPayload.getNomineeRelation();
                        coappNomGender = coappPayload.getGender();
                    }
                    break;
                default:
                    appinsurance = Constants.NO;
                    coappinsurance = Constants.NO;
                    break;
            }

            //TODO : To be removed once Joint insurance is brought to the system
            String applicantName = "";
            String coApplicantName = "";
            String appDOB = "";
            String coAppDOB = "";
            String appAge = "";
            String coAppAge = "";
            String appRel = "";
            String coAppRel = "";
            String appGender = "";
            String coAppGender = "";

            for(CustomerDetails applicantwrpr : req.getCustomerDetailsList())
            {
                if (String.valueOf(applicantwrpr.getCustDtlId()).equals(applicantCustId)) {
                    CustomerDetailsPayload appCustPayload = gsonObj.fromJson(applicantwrpr.getPayloadColumn(), CustomerDetailsPayload.class);
                    applicantName = appCustPayload.getFirstName() + " "
                            + appCustPayload.getLastName();
                    appDOB = appCustPayload.getDob();
                    appAge = appCustPayload.getAge();
                    appRel = appCustPayload.getRelationShipWithApplicant();
                    appGender = appCustPayload.getGender();
                } else if (String.valueOf(applicantwrpr.getCustDtlId())
                        .equals(coApplicantCustId)) {
                    CustomerDetailsPayload coAppCustPayload = gsonObj.fromJson(applicantwrpr.getPayloadColumn(), CustomerDetailsPayload.class);
                    coApplicantName = coAppCustPayload.getFirstName() + " "
                            + coAppCustPayload.getLastName();
                    coAppDOB = coAppCustPayload.getDob();
                    coAppAge = coAppCustPayload.getAge();
                    coAppRel = coAppCustPayload.getRelationShipWithApplicant();
                    coAppGender = coAppCustPayload.getGender();
                }
            }

            verticalList.add(createSevenHorizontalList2(keysForContent.getString("forInsurance"),
                    keysForContent.getString("optedInsuranceYN"), keysForContent.getString("insuranceNominee"),
                    keysForContent.getString("nomineeDob"), keysForContent.getString("nomineeAge"),
                    keysForContent.getString("nomineeRelationShip"), keysForContent.getString("nomineeGender")));

            boolean isAppInsuranceRequired = !"NO".equalsIgnoreCase(appinsurance);
            boolean isCoAppInsuranceRequired = !"NO".equalsIgnoreCase(coappinsurance);

            verticalList.add(createSevenHorizontalList(applicantName, getYNFlagValues1(appinsurance),
                    !isAppInsuranceRequired ? "-" : (StringUtils.isBlank(appNomName) ? coApplicantName : appNomName),
                    !isAppInsuranceRequired ? "-" : (StringUtils.isBlank(appNomDob) ? coAppDOB : appNomDob),
                    !isAppInsuranceRequired ? "-" : (StringUtils.isBlank(appNomAge) ? coAppAge : appNomAge),
                    !isAppInsuranceRequired ? "-" : (StringUtils.isBlank(appNomRel) ? coAppRel : appNomRel),
                    !isAppInsuranceRequired ? "-" : (StringUtils.isBlank(appNomGender) ? coAppGender : appNomGender)
            )); // Applicant nominee details, which is the co-app details

            verticalList.add(createSevenHorizontalList(coApplicantName, getYNFlagValues1(coappinsurance),
                    !isCoAppInsuranceRequired ? "-" : (StringUtils.isBlank(coappNomName) ? applicantName : coappNomName),
                    !isCoAppInsuranceRequired ? "-" : (StringUtils.isBlank(coappNomDob) ? appDOB : coappNomDob),
                    !isCoAppInsuranceRequired ? "-" : (StringUtils.isBlank(coappNomAge) ? appAge : coappNomAge),
                    !isCoAppInsuranceRequired ? "-" : (StringUtils.isBlank(coappNomRel) ? appRel : coappNomRel),
                    !isCoAppInsuranceRequired ? "-" : (StringUtils.isBlank(coappNomGender) ? appGender : coappNomGender)
            )); // Co-applicant
        } catch (Exception e) {
            logger.error("error - getInsuranceDetails");
            logger.error(e.getMessage());
        }
        logger.debug("Insurance Details added");
        return verticalList;
    }

    // @author Abhishek.Raj.CAG
    private ComponentBuilder<?, ?> getInsuranceDetails4Add(JSONObject keysForContent, CustomerDataFields req,
                                                           ApplicationMaster applicationMasterData) {
        VerticalListBuilder verticalList = cmp.verticalList();
        try {
            String appinsurance = Constants.NO;   // resolved display value (YES/NO)
            String coappinsurance = Constants.NO;

            UnnatiIexceedCDHLead cdhLead = req.getCdhLeadDetails();
            if (cdhLead != null) {
                logger.debug("insuranceReqd : {} , coInsuranceReqd : {}",
                        cdhLead.getInsuranceReqd(), cdhLead.getCoInsuranceReqd());

//                appinsurance   = getYNFlagValues1(cdhLead.getInsuranceReqd());
//                coappinsurance = getYNFlagValues1(cdhLead.getCoInsuranceReqd());
                appinsurance   = (cdhLead.getInsuranceReqd()   == null) ? "-" : cdhLead.getInsuranceReqd();
                coappinsurance = (cdhLead.getCoInsuranceReqd() == null) ? "-" : cdhLead.getCoInsuranceReqd();
            } else {
                logger.debug("cdhLeadDetails is null - defaulting insurance flags to NO");
                appinsurance   = "NO";
                coappinsurance = "NO";
            }

//
            Gson gsonObj = new Gson();
            CibilDetailsPayload cibilPayload = CommonUtils
                    .resolveCibilPayload(req.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId, gsonObj);

            String appInsuranceFlag = "N";
            String coAppInsuranceFlag = "N";

//            UnnatiIexceedCDHLead cdhLead = req.getCdhLeadDetails();
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
            String apptPrInsuAmt = (isAppInsured && cibilPayload != null)
                    ? cibilPayload.getInsuranceChargeMember() : "";
            String coAppPrInsuAmt = (isCoAppInsured && cibilPayload != null)
                    ? cibilPayload.getInsuranceChargeSpouse() : "";

            if (Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                verticalList.add(createThreeHorizontalListForLoanApplication(keysForContent.getString("insurance"),
                        keysForContent.getString("applicant"),
                        keysForContent.getString("coApplicant"), boldTextWithBorder));

                verticalList.add(createThreeHorizontalListForLoanApplication(keysForContent.getString("optedInsuranceYN"), appinsurance, coappinsurance, boldTextWithBorder));

//                refer from sanction letter premium
                verticalList.add(createThreeHorizontalListForLoanApplication(keysForContent.getString("insuranceAmount"), (apptPrInsuAmt == null || apptPrInsuAmt.trim().isEmpty())
                        ? "NA"
                        : apptPrInsuAmt ,   (coAppPrInsuAmt == null || coAppPrInsuAmt.trim().isEmpty())
                        ? "NA"
                        : coAppPrInsuAmt, boldTextWithBorder));

            } else if (Constants.FAMILY_WELFARE_PRODUCT_CODE.equals(applicationMasterData.getProductCode())) {
                verticalList.add(createThreeHorizontalListForLoanApplication(keysForContent.getString("insurance"),
                        keysForContent.getString("applicant"), keysForContent.getString("insuranceAmount"), boldTextWithBorder));

                verticalList.add(createThreeHorizontalListForLoanApplication(keysForContent.getString("optedInsuranceYN"), appinsurance, (apptPrInsuAmt == null || apptPrInsuAmt.trim().isEmpty())
                        ? "NA"
                        : apptPrInsuAmt, boldTextWithBorder));

//                verticalList.add(createTwoHorizontalList(keysForContent.getString("insuranceAmount"), apptPrInsuAmt));
            }
            // Emergency loan -> insurance section not displayed
        } catch (Exception e) {
            logger.error("error - getInsuranceDetails4Add");
            logger.error(e.getMessage());
        }
        logger.debug("Insurance Details added");
        return verticalList;
    }

    private String getYNFlagValues(String insuranceReqd) {

        if ("Y".equalsIgnoreCase(insuranceReqd)) {
            return Constants.YES;
        } else if ("N".equalsIgnoreCase(insuranceReqd)) {
            return Constants.NO;
        }
        return null;
    }

    private String getYNFlagValues1(String insuranceReqd) {
        if ("Y".equalsIgnoreCase(insuranceReqd)) {
            return "YES";
        } else if ("N".equalsIgnoreCase(insuranceReqd)) {
            return "NO";
        }
        return "";
    }

    // 11.
    private ComponentBuilder<?, ?> getLeadAndSourcingDetails4(JSONObject keysForContent, CustomerDataFields req) {
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(
                createSixHorizontalList2(keysForContent.getString("centreName"), keysForContent.getString("centreId"),
                        keysForContent.getString("glBMName"), keysForContent.getString("glBMId"),
                        keysForContent.getString("kmName"), keysForContent.getString("kmId")));

        verticalList.add(createSixHorizontalList3(req.getApplicationMaster().getKendraName(),
                req.getApplicationMaster().getKendraId(), bmName, bmId, kmName, kmId));
        logger.debug("LeadAndSourcingDetails");
        return verticalList;
    }

    // 12. Declaration
    // declaration1
    private String getDeclaration1(JSONObject keysForContent, CustomerDataFields req) {
        return keysForContent.getString("declaration1").trim();
    }

    private String getDeclaration1Add(JSONObject keysForContent, CustomerDataFields req, String productCode) {
        String applicantOrCoApplicant =  "";
        if (Constants.UNNATI_SUPPLEMENTRY_PRODUCT_CODE.equals(productCode)) {
            applicantOrCoApplicant = keysForContent.getString("applicantBoth").trim();
        } else {
            applicantOrCoApplicant = keysForContent.getString("applicantOnly").trim();
        }
        return keysForContent.getString("declaration1").trim() + " " + applicantOrCoApplicant + keysForContent.getString("declaration1b").trim();
    }

    // 13. declaration2
    private String getDeclaration2(JSONObject keysForContent) {
        return keysForContent.getString("declaration2").trim();
    }

    // 14 declaration3
    private String getDeclaration3(JSONObject keysForContent) {
        return keysForContent.getString("declaration3").trim();
    }

    // 14 declaration4
    private String getDeclaration4(JSONObject keysForContent) {
        return keysForContent.getString("declaration4").trim();
    }

    // 16.
    private ComponentBuilder<?, ?> getCustNameSignPhoto(JSONObject keysForContent, CustomerDataFields req, ApplicationMaster applicationMasterData, String uploadLocation, List<ApplicationDocuments> applnDocumentList) {
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(createFourHorizontalList(keysForContent.getString("applicantType"),
                keysForContent.getString("applicantName"), keysForContent.getString("applicantSign"),
                keysForContent.getString("applicantPhotoAndSign")));
        String applicantPhotoPath = "";
        String coApplicantPhotoPath = "";
        if(!applnDocumentList.isEmpty()) {
            for(ApplicationDocuments document:applnDocumentList) {
                if("Applicant".equalsIgnoreCase(document.getCustType())) {
                    applicantPhotoPath = uploadLocation+""+document.getDocumentFileName();
                }else {
                    coApplicantPhotoPath = uploadLocation+""+document.getDocumentFileName();
                }
            }
        }

        verticalList
                .add(createFourHorizontalListWithHeight2(keysForContent.getString(Constants.APPLICANT_KEY), applicantName, "", applicantPhotoPath)); // Applicant
            verticalList.add(
                createFourHorizontalListWithHeight2(keysForContent.getString(Constants.COAPPLICANT_KEY), coApplicantName, "", coApplicantPhotoPath)); // Co-applicant
        logger.debug("CustNameSignPhoto added");
        return verticalList;
    }


    private ComponentBuilder<?, ?> getCustNameSignPhotoAdd(JSONObject keysForContent, CustomerDataFields req, ApplicationMaster applicationMasterData) {
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(createFourHorizontalList(keysForContent.getString("applicantType"),
                keysForContent.getString("applicantName"), keysForContent.getString("applicantSign"),
                keysForContent.getString("applicantPhotoAndSign")));

        verticalList
                .add(createFourHorizontalListWithHeight2(keysForContent.getString(Constants.APPLICANT_KEY), applicantName, "", "")); // Applicant
        //@author Sharath Chandra.cagl

            verticalList.add(
                    createFourHorizontalListWithHeight2(keysForContent.getString(Constants.COAPPLICANT_KEY), coApplicantName, "", "")); // Co-applicant

        logger.debug("getCustNameSignPhotoAdd added");
        return verticalList;
    }

    private ComponentBuilder<?, ?> getCustNameSignPhotoFWLAdd(JSONObject keysForContent, CustomerDataFields req, ApplicationMaster applicationMasterData) {
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(createThreeHorizontalListWithHeight1(
                keysForContent.getString("applicant") + " " + keysForContent.getString("applicantName"),  keysForContent.getString("applicant") + " " + keysForContent.getString("applicantSign"),
                keysForContent.getString("applicantPhotoAndSign")));
        verticalList
                .add(createThreeHorizontalListWithHeight2( applicantName, "", "")); // Applicant
        logger.debug("getCustNameSignPhotoFWLAdd added");
        return verticalList;
    }

    // 17. branch Staff confirmation
    private String getConfBranch(JSONObject keysForContent, CustomerDataFields req) {
        return keysForContent.getString("branchDeclaration").trim();
    }

    // 18
    private ComponentBuilder<?, ?> getBranchStaffDetails(JSONObject keysForContent, CustomerDataFields req) {
        VerticalListBuilder verticalList = cmp.verticalList();

        verticalList.add(
                createFourHorizontalList2(keysForContent.getString("staffName"), keysForContent.getString("staffId"),
                        keysForContent.getString("staffSign"), keysForContent.getString("staffDate")));
//		verticalList.add(createFourHorizontalList3(Constants.KM, kmId, "", kmSubmDateStr)); // Applicant
//		verticalList.add(createFourHorizontalList3(Constants.BM, bmId, "", bmSubmDateStr)); // Co-applicant
//        verticalList.add(createFourHorizontalList3(kmName, kmId, "", kmSubmDateStr)); // Applicant
//        verticalList.add(createFourHorizontalList3(bmName, bmId, "", bmSubmDateStr)); // Co-applicant
        if(Constants.UNNATI_RESTART_PRODUCT_CODE
                .equals(req.getApplicationMaster().getProductCode()) || Constants.Vishesh_LOAN_PRODUCT_CODE
                .equals(req.getApplicationMaster().getProductCode()) ){
            verticalList.add(createFourHorizontalList3(bmName, bmId, "", kmSubmDateStr)); // Co-applicant
        }else{
        verticalList.add(createFourHorizontalList3(bmName, bmId, "", bmSubmDateStr)); // Co-applicant
        }
        logger.debug("BranchStaffDetails added");
        return verticalList;
    }


    private ComponentBuilder<?, ?> getBranchStaffDetailsAdd(JSONObject keysForContent, CustomerDataFields req) {
        VerticalListBuilder verticalList = cmp.verticalList();

        verticalList.add(
                createFourHorizontalList2(keysForContent.getString("staffName"), keysForContent.getString("staffId"),
                        keysForContent.getString("staffSign"), keysForContent.getString("staffDate")));
//		verticalList.add(createFourHorizontalList3(Constants.KM, kmId, "", kmSubmDateStr)); // Applicant
//		verticalList.add(createFourHorizontalList3(Constants.BM, bmId, "", bmSubmDateStr)); // Co-applicant
//        verticalList.add(createFourHorizontalList3(kmName, kmId, "", kmSubmDateStr)); // Applicant
//        verticalList.add(createFourHorizontalList3(bmName, bmId, "", bmSubmDateStr)); // Co-applicant
        verticalList.add(createFourHorizontalList3(bmName, bmId, "", loanApplicationDateStr)); // Co-applicant
        logger.debug("BranchStaffDetails added");
        return verticalList;
    }

    private ComponentBuilder<?, ?> getSourcingOfficerDetails(
            JSONObject keysForContent,
            CustomerDataFields req) {

        VerticalListBuilder verticalList = cmp.verticalList();

        try {

            // Table header
            verticalList.add(
                    createThreeHorizontalList2(keysForContent.getString("sourcingOfficerName"),
                            keysForContent.getString("sourcingOfficerSign"),
                            keysForContent.getString("sourcingOfficerDate")
                    )
            );

            verticalList.add(createThreeHorizontalList(kmName, "", loanApplicationDateStr)); // Applicant

            // Actual values from DB
//            verticalList.add(
//                    createThreeHorizontalList("sourcingOfficerName",
//                            "",
//                            ""
//                    )
//            );

            logger.debug("SourcingOfficerDetails added");

        } catch (Exception e) {
            logger.error("Error while adding Sourcing Officer Details", e);
        }

        return verticalList;
    }



    private ComponentBuilder<?, ?> createThreeHorizontalList2(String value, String value1, String value2) {
        HorizontalListBuilder horizontalList = cmp.horizontalList();
        horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(20));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(40));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(40));
        return horizontalList;
    }


    private ComponentBuilder<?, ?> createThreeHorizontalListForLoanApplication(String key, String value1, String value2,
                                                                              ReportStyleBuilder style) {
        HorizontalListBuilder horizontalList = cmp.horizontalList();
//		horizontalList.add(cmp.text(key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(20));
        horizontalList.add(cmp.text(key).setMarkup(Markup.HTML).setStyle(style).setWidth(50));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(style).setWidth(25));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(style).setWidth(25));
        return horizontalList;
    }

    private ComponentBuilder<?, ?> createThreeHorizontalList(String key, String value1, String value2) {
        HorizontalListBuilder horizontalList = cmp.horizontalList();
        horizontalList.add(cmp.text(key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(20));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(40));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(40));
        return horizontalList;
    }

    private ComponentBuilder<?, ?> createTwoHorizontalList(String Key, String value) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

//			horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(25));
        horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(25));
        horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(75));
        return horizontalList;

    }

    private ComponentBuilder<?, ?> createTwoHorizontalList1(String Key, String value) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

//			horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(25));
        horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(boldText).setWidth(25));
        horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(boldText).setWidth(75));
        return horizontalList;

    }

    private ComponentBuilder<?, ?> createTwoVerticalList(String Key, String value) {

        VerticalListBuilder verticalList = cmp.verticalList();

        // verticalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setHeight(30));
        // verticalList.add(cmp.text(value).setStyle(borderedStyle).setHeight(45));

        verticalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder));
        verticalList.add(cmp.text(value).setStyle(borderedStyle));

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

    private ComponentBuilder<?, ?> createFourHorizontalList2(String Key1, String value1, String Key2, String value2) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(boldTextWithBorder));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(boldTextWithBorder));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle));

        return horizontalList;

    }

    private ComponentBuilder<?, ?> createFourHorizontalList3(String Key1, String value1, String Key2, String value2) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(boldTextWithBorder));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(boldTextWithBorder));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle));

        return horizontalList;

    }

//	private ComponentBuilder<?, ?> createFourHorizontalListWithHeight3(String Key1, String value1, String Key2,
//			String value2) {
//
//		HorizontalListBuilder horizontalList = cmp.horizontalList();
//
//		horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle).setHeight(40));
//		horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setHeight(40));
//		horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle).setHeight(40));
//		horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setHeight(40));
//
//		return horizontalList;
//	}

    private ComponentBuilder<?, ?> createFourHorizontalListWithHeight2(String Key1, String value1, String Key2,
                                                                       String value2) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle).setHeight(140));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setHeight(140));
        horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle).setHeight(140));
        //horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setHeight(140));
        horizontalList.add("".equals(value2)
                ? cmp.text("").setStyle(borderedStyle).setHeight(140)
                : cmp.image(value2).setStyle(borderedStyle).setHeight(140).setImageScale(ImageScale.RETAIN_SHAPE));

        return horizontalList;
    }


    private ComponentBuilder<?, ?> createThreeHorizontalListWithHeight2(String Key1, String value1, String Key2) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle).setHeight(140));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setHeight(140));
        horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle).setHeight(140));


        return horizontalList;
    }


    private ComponentBuilder<?, ?> createThreeHorizontalListWithHeight1(String Key1, String value1, String Key2) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle));


        return horizontalList;
    }




    private ComponentBuilder<?, ?> createFiveHorizontalList(String v1, String v2, String v3, String v4, String v5) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(v1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(v2).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(v3).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(v4).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(v5).setMarkup(Markup.HTML).setStyle(borderedStyle));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createSixHorizontalList(String Key1, String value1, String Key2, String value2,
                                                           String Key3, String value3) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key3).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value3).setMarkup(Markup.HTML).setStyle(borderedStyle));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createSixHorizontalList2(String Key1, String value1, String Key2, String value2,
                                                            String Key3, String value3) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key3).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value3).setMarkup(Markup.HTML).setStyle(borderedStyle));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createSixHorizontalList3(String Key1, String value1, String Key2, String value2,
                                                            String Key3, String value3) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key3).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value3).setMarkup(Markup.HTML).setStyle(borderedStyle));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createSevenHorizontalList(String Key1, String value1, String Key2, String value2,
                                                             String Key3, String value3, String v7) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key3).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value3).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(v7).setMarkup(Markup.HTML).setStyle(borderedStyle));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createSevenHorizontalList2(String Key1, String value1, String Key2, String value2,
                                                              String Key3, String value3, String v7) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key2).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key3).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value3).setMarkup(Markup.HTML).setStyle(borderedStyle));
        horizontalList.add(cmp.text(v7).setMarkup(Markup.HTML).setStyle(borderedStyle));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createEightHorizontalList(String Key1, String value1, String Key2, String value2,
                                                             String Key3, String value3, String v7, String v8) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key1).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value1).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key2).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value2).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key3).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value3).setStyle(borderedStyle));
        horizontalList.add(cmp.text(v7).setStyle(borderedStyle));
        horizontalList.add(cmp.text(v8).setStyle(borderedStyle));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createNineHorizontalList(String Key1, String value1, String Key2, String value2,
                                                            String Key3, String value3, String v7, String v8, String v9) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key1).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value1).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key2).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value2).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key3).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value3).setStyle(borderedStyle));
        horizontalList.add(cmp.text(v7).setStyle(borderedStyle));
        horizontalList.add(cmp.text(v8).setStyle(borderedStyle));
        horizontalList.add(cmp.text(v9).setStyle(borderedStyle));

        return horizontalList;
    }

    private TextFieldBuilder<String> createTextField(String label) {

        return cmp.text(label).setStyle(boldCenteredStyle);

    }

    private TextFieldBuilder<String> createLeftAlignTextField(String label) {

        return cmp.text(label).setStyle(boldLeftStyle);

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

    /*
     * public static String formatCurrency(String amount) { double d =
     * Double.parseDouble(amount); //DecimalFormat f = new
     * DecimalFormat("#,##,##0.00"); return f.format(d); }
     */

    public Response getSuccessJson(String base64String, String fileType) {
        logger.debug("Inside getSuccessJson");

        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();

        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        logger.debug("responseCode added to responseHeader");

        // Build final JSON cleanly
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("base64", base64String);
        jsonObj.addProperty("fileType", fileType);
        jsonObj.addProperty("status", ResponseCodes.SUCCESS.getValue());

        responseBody.setResponseObj(jsonObj.toString());
        logger.debug("string added to responseBody as responseObj");

        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);

        logger.debug("SuccessJson created");
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

    public Map<String, String> getNewKMOccupationDetails(CustomerDataFields customerFileds,boolean useCoApplicant) {

        Map<String, String> occupationMap = new HashMap<>();
        String occupationType = "";
        String typeOfBusiness = "";
        String natureOfBusiness = "";
        String EmploymentActivity = "NA";
        String NameofOrganization = "";
        String StreetVendor = "";
        String EmploymentStartDate = "-";
        String EmployemntVintage= "";
        String Address = "";
        String BusinessAdreesProof = "";
        String EmploymentProof = "NA";
        String BusinessPremiseArea = "NA";
        String BusinessPremiseOwnship = "NA";
        Gson gsonObj = new Gson();
        logger.debug("Getting Occupation/Business Details");



        BCMPIIncomeDetails bcmpiIncomeDetails = customerFileds.getBcmpiIncomeDetails();
        logger.debug("Bcmpi IncomDetails {}", bcmpiIncomeDetails);

        if (bcmpiIncomeDetails != null) {

            BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper = gsonObj.fromJson(bcmpiIncomeDetails.getPayload(),
                    BCMPIIncomeDetailsWrapper.class);
            logger.debug("Wrapper Details {}", bcmpiIncomeDetailsWrapper);

            if (bcmpiIncomeDetailsWrapper != null) {

                String type = useCoApplicant ? Constants.CO_APPLICANT : Constants.APPLICANT;

                logger.debug("Customer Type"+ type);

                BCMPIIncomeDetailsWrapper.Business business = bcmpiIncomeDetailsWrapper.getBusiness();

	           /* Optional — empty if no business (Dairy/Other/Kirana/Tailoring) matched the applicant/co-applicant
	              Level 1 — key = businessType label (e.g. "Dairy", "Kirana", "Tailoring", or specific Other type)
	              Level 2 — key = businessIndex (int), used to pick the winning business via .min()
	              Level 3 — key = organizationName for the winning business
	              Level 4 — key = tenure, value = streetVendor (Kirana only; "" for other types) */
                logger.debug("Fetching Business Data for the Customer");
                Optional<AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<Integer, AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<String, String>>>>> BusinessEntry = Stream.of(
                                business.getDairy().stream()
                                        .filter(d -> "Both".equalsIgnoreCase(d.getDairyType())
                                                || type.equalsIgnoreCase(d.getDairyType()))
                                        .map(d -> new AbstractMap.SimpleEntry<>("Dairy",
                                                new AbstractMap.SimpleEntry<>(Integer.parseInt(d.getBusinessIndex()),
                                                        new AbstractMap.SimpleEntry<>(d.getOrganizationName(),
                                                                new AbstractMap.SimpleEntry<>(d.getDairyTenure(), ""))))),   // no streetVendor for Dairy

                                business.getOther().stream()
                                        .filter(o -> "Both".equalsIgnoreCase(o.getOtherType())
                                                || type.equalsIgnoreCase(o.getOtherType()))
                                        .map(o -> new AbstractMap.SimpleEntry<>(o.getOtherBusinessType(),
                                                new AbstractMap.SimpleEntry<>(Integer.parseInt(o.getBusinessIndex()),
                                                        new AbstractMap.SimpleEntry<>(o.getNameOfOrganization(),
                                                                new AbstractMap.SimpleEntry<>(o.getOtherBusinessTenure(), ""))))),  // no streetVendor for Other

                                business.getKirana().stream()
                                        .filter(k -> "Both".equalsIgnoreCase(k.getKiranaType())
                                                || type.equalsIgnoreCase(k.getKiranaType()))
                                        .map(k -> new AbstractMap.SimpleEntry<>("Kirana",
                                                new AbstractMap.SimpleEntry<>(Integer.parseInt(k.getBusinessIndex()),
                                                        new AbstractMap.SimpleEntry<>(k.getNameOfOrganization(),
                                                                new AbstractMap.SimpleEntry<>(k.getKiranaTenure(), k.getStreetVendor()))))),  // <-- streetVendor here

                                business.getTailoring().stream()
                                        .filter(t -> "Both".equalsIgnoreCase(t.getTailoringType())
                                                || type.equalsIgnoreCase(t.getTailoringType()))
                                        .map(t -> new AbstractMap.SimpleEntry<>("Tailoring",
                                                new AbstractMap.SimpleEntry<>(Integer.parseInt(t.getBusinessIndex()),
                                                        new AbstractMap.SimpleEntry<>(t.getOrganizationName(),
                                                                new AbstractMap.SimpleEntry<>(t.getTailoringTenure(), "")))))  // no streetVendor for Tailoring
                        )
                        .flatMap(Function.identity())
                        .min(Comparator.comparingInt(e -> e.getValue().getKey()));

                String businessType = BusinessEntry.map(Map.Entry::getKey).orElse("");
                logger.debug("Business Type id"+businessType);
                logger.debug("Data from Business/Ocuppation : " + BusinessEntry);

                if (!businessType.isEmpty()) {
                    occupationType = "Self employed";
                    logger.debug("Occupation Type :"+occupationType);
                    typeOfBusiness = businessType; // Dairy / Kirana / Tailoring / otherBusinessType
                    logger.debug("Type of Business :"+typeOfBusiness);
                    natureOfBusiness = mapNatureOfBusiness(businessType);
                    logger.debug("Nature of Business: "+natureOfBusiness);
                    // ADDED: pull organization name from the winning entry
                    NameofOrganization = BusinessEntry.map(e -> e.getValue().getValue().getKey()).orElse("");
                    logger.debug("Oraganization name: "+NameofOrganization);
                    EmployemntVintage = BusinessEntry.map(e -> e.getValue().getValue().getValue().getKey()).orElse("");
                    logger.debug("Employment Tenure: "+EmployemntVintage);
                    StreetVendor = BusinessEntry.map(e -> e.getValue().getValue().getValue().getValue()).orElse("");
                    if ("Kirana".equalsIgnoreCase(businessType)) {
                        StreetVendor = ("Yes".equalsIgnoreCase(StreetVendor) || "Y".equalsIgnoreCase(StreetVendor)) ? "Yes" : "No";
                        logger.debug("BusinessType is  Kirana and StreetVendor: "+ StreetVendor);
                    } else {
                        StreetVendor = "NA";
                        logger.debug("BusinessType is  Kirana and StreetVendor: "+ StreetVendor);
                    }

                    logger.debug("Fetching Business Address for businessIndex match");
                    if (BusinessEntry.isPresent()) {

                        String BusinessIndex = String.valueOf(BusinessEntry.get().getValue().getKey());
                        logger.debug("businessIndex to match address: " + BusinessIndex);

                        List<AddressDetailsWrapper> addressWrapperList = customerFileds.getAddressDetailsWrapperList();

                        if (addressWrapperList != null) {

                            for (AddressDetailsWrapper addressWrapper : addressWrapperList) {
                                if (addressWrapper.getAddressDetailsList() != null) {

                                    for (AddressDetails addressDetails : addressWrapper.getAddressDetailsList()) {

                                        AddressDetailsPayload addressPayload = gsonObj.fromJson(
                                                addressDetails.getPayloadColumn(), AddressDetailsPayload.class);

                                        if (addressPayload != null && addressPayload.getAddressList() != null) {

                                            Optional<Address> matchedAddress = addressPayload.getAddressList().stream()
                                                    .filter(a -> BusinessIndex.equals(a.getBusinessIndex()))
                                                    .findFirst();

                                            if (matchedAddress.isPresent()) {

                                                Address address = matchedAddress.get();
                                                Address = getFullAddress(address);
                                                logger.debug("Address Details : " + Address);
                                                boolean hasVoter = matchedAddress.get().getAddressSameAs().toLowerCase().contains("voter");
                                                if(hasVoter) {
                                                    BusinessAdreesProof ="As per voter ID";
                                                    logger.debug("Business Address Proof as : " + BusinessAdreesProof);
                                                }else{
                                                    BusinessAdreesProof="NA";
                                                    logger.debug("Business Address Proof as : " + BusinessAdreesProof);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                } else {

                    // ---- 2. Agriculture ----
                    logger.debug("Fetching Agriculture Data");
                    BCMPIIncomeDetailsWrapper.Agriculture agriculture = bcmpiIncomeDetailsWrapper.getAgriculture();
                    BCMPIIncomeDetailsWrapper.AgricultureDetail agricultureDetail = (agriculture != null)
                            ? (useCoApplicant ? agriculture.getCoApplicant() : agriculture.getApplicant())
                            : null;

                    if (agricultureDetail != null) {
                        occupationType = "Self employed";
                        typeOfBusiness = "Agriculture";
                        natureOfBusiness="Agri Allied";

                    } else {

                        // ---- 3. Salary ----
                        logger.debug("Fetching Salary Data");
                        BCMPIIncomeDetailsWrapper.Salary salary = bcmpiIncomeDetailsWrapper.getSalary();
                        BCMPIIncomeDetailsWrapper.SalaryDetails salaryDetails = (salary != null)
                                ? (useCoApplicant ? salary.getCoApplicant() : salary.getApplicant())
                                : null;

                        if (salaryDetails != null) {
                            occupationType = "Salaried";
                            typeOfBusiness = "Salaried";
                            natureOfBusiness = "NA";
                            EmploymentActivity="NA";

                        } else {

                            // ---- 4. Wage ----
                            logger.debug("Fetching Wage Data");
                            BCMPIIncomeDetailsWrapper.Wage wage = bcmpiIncomeDetailsWrapper.getWage();
                            BCMPIIncomeDetailsWrapper.WageDetail wageDetail = (wage != null)
                                    ? (useCoApplicant ? wage.getCoApplicant() : wage.getApplicant())
                                    : null;

                            if (wageDetail != null) {
                                occupationType = "Wages";
                                typeOfBusiness = "Wages";
                                natureOfBusiness = "NA";

                            } else {

                                // ---- 5. Rental Income ----
                                logger.debug("Fetching Rental Income Data");
                                BCMPIIncomeDetailsWrapper.RentalIncome rentalIncome = bcmpiIncomeDetailsWrapper.getRentalIncome();
                                BCMPIIncomeDetailsWrapper.RentalDetail rentalIncomeDetail = (rentalIncome != null)
                                        ? (useCoApplicant ? rentalIncome.getCoApplicant() : rentalIncome.getApplicant())
                                        : null;

                                if (rentalIncomeDetail != null) {
                                    occupationType = "Unemployed";
                                    typeOfBusiness = "NA";
                                    natureOfBusiness = "NA";
                                } else {

                                    // ---- 6. Pension ----
                                    logger.debug("Fetching Pension Data");
                                    BCMPIIncomeDetailsWrapper.Pension pension = bcmpiIncomeDetailsWrapper.getPension();

                                    if (pension != null) {
                                        occupationType = "Unemployed";
                                        typeOfBusiness = "NA";
                                        natureOfBusiness = "NA";

                                    } else {

                                        // ---- 7. Homemaker ----
                                        logger.debug("Fetching Homemaker Data");
                                        String homemaker= bcmpiIncomeDetailsWrapper.getHomeMaker();

                                        if (homemaker != null) {
                                            occupationType = "Unemployed";
                                            typeOfBusiness = "NA";
                                            natureOfBusiness ="NA";

                                        } else {
                                            // ---- 8. Nothing matched ----
                                            occupationType = "Unemployed";
                                            typeOfBusiness = "NA";
                                            natureOfBusiness ="NA";
                                            EmploymentActivity="NA";
                                            StreetVendor="";
                                            EmploymentStartDate="-";
                                            Address = "";
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }



        occupationMap.put("occupationType", occupationType);
        occupationMap.put("typeOfBusiness", typeOfBusiness);
        occupationMap.put("natureOfBusiness", natureOfBusiness);
        occupationMap.put("EmploymentActivity", EmploymentActivity);
        occupationMap.put("NameofOrganization", NameofOrganization);
        occupationMap.put("StreetVendor", StreetVendor);
        occupationMap.put("EmploymentStartDate", EmploymentStartDate);
        occupationMap.put("EmployemntVintage", EmployemntVintage);
        occupationMap.put("Address", Address);
        occupationMap.put("BusinessAdreesProof", BusinessAdreesProof);
        occupationMap.put("EmploymentProof", EmploymentProof);
        occupationMap.put("BusinessPremiseArea", BusinessPremiseArea);
        occupationMap.put("BusinessPremiseOwnship", BusinessPremiseOwnship);
        logger.debug("returing Occupation/Business Details : "+ occupationMap);
        return occupationMap;
    }

    /**
     * Nature of Business mapping per the comments: Dairy -> Animal Husbandry
     * Tailoring -> Services Kirana -> Trading Other -> (not specified; left blank)
     */
    private String mapNatureOfBusiness(String businessType) {
        if (businessType == null) {
            return "";
        }

        switch (businessType.trim().toLowerCase()) {
            case "dairy":
                return "Animal Husbandry";
            case "tailoring":
                return "Services";
            case "kirana":
                return "Trading";
            default:
                return "";
        }
    }

    public Map<String, String> getNewKMIncomeDetails(CustomerDataFields customerFileds, boolean useCoApplicant) {
        logger.debug("Inside getNewKMIncomeDetails");

        Map<String, String> incomeMap = new HashMap<>();
        String empIncomeType = "NA";           // flat, always "NA" per mapping
        String empIncomeFrequency = "";
        String empYearlyIncome = "";             // getTotalDeclaredIncome getFieldAssessedIncome smaller * 12
        String otherSourcesOfAnnualIncome = "-";       // flat, always "-" per mapping

        Gson gsonObj = new Gson();


        BCMPIIncomeDetails bcmpiIncomeDetails = customerFileds.getBcmpiIncomeDetails();
        logger.debug("Bcmpi IncomDetails {}", bcmpiIncomeDetails);

        if (bcmpiIncomeDetails != null) {

            BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper = gsonObj.fromJson(
                    bcmpiIncomeDetails.getPayload(), BCMPIIncomeDetailsWrapper.class);
            logger.debug("Wrapper Details {}", bcmpiIncomeDetailsWrapper);

            if (bcmpiIncomeDetailsWrapper != null) {

                String type = useCoApplicant ? Constants.CO_APPLICANT : Constants.APPLICANT;
                logger.debug("Customer Type: "+ type);

                BigDecimal totalDeclaredIncome = bcmpiIncomeDetailsWrapper.getTotalDeclaredIncome();
                if (totalDeclaredIncome == null) {
                    totalDeclaredIncome = BigDecimal.ZERO;
                }

                BigDecimal fieldAssessedIncome = bcmpiIncomeDetailsWrapper.getFieldAssessedIncome();
                if (fieldAssessedIncome == null) {
                    fieldAssessedIncome = BigDecimal.ZERO;
                }

                BigDecimal smallerMonthlyIncome = totalDeclaredIncome.min(fieldAssessedIncome);
                empYearlyIncome = smallerMonthlyIncome.multiply(BigDecimal.valueOf(12)).toPlainString();
                logger.debug("Calculated Annual Income"+ empYearlyIncome);

                BCMPIIncomeDetailsWrapper.Business business = bcmpiIncomeDetailsWrapper.getBusiness();

                Optional<AbstractMap.SimpleEntry<String, Integer>> winningBusinessEntry = Stream.of(
                                business.getDairy().stream()
                                        .filter(d -> "Both".equalsIgnoreCase(d.getDairyType())
                                                || type.equalsIgnoreCase(d.getDairyType()))
                                        .map(d -> new AbstractMap.SimpleEntry<>("Dairy",
                                                Integer.parseInt(d.getBusinessIndex()))),

                                business.getTailoring().stream()
                                        .filter(t -> "Both".equalsIgnoreCase(t.getTailoringType())
                                                || type.equalsIgnoreCase(t.getTailoringType()))
                                        .map(t -> new AbstractMap.SimpleEntry<>("Tailoring",
                                                Integer.parseInt(t.getBusinessIndex()))),

                                business.getKirana().stream()
                                        .filter(k -> "Both".equalsIgnoreCase(k.getKiranaType())
                                                || type.equalsIgnoreCase(k.getKiranaType()))
                                        .map(k -> new AbstractMap.SimpleEntry<>("Kirana",
                                                Integer.parseInt(k.getBusinessIndex()))),

                                business.getOther().stream()
                                        .filter(o -> "Both".equalsIgnoreCase(o.getOtherType())
                                                || type.equalsIgnoreCase(o.getOtherType()))
                                        .map(o -> new AbstractMap.SimpleEntry<>(o.getOtherBusinessType(),
                                                Integer.parseInt(o.getBusinessIndex())))
                        )
                        .flatMap(Function.identity())
                        .min(Comparator.comparingInt(Map.Entry::getValue));

                String businessType = winningBusinessEntry.map(Map.Entry::getKey).orElse("");

                if (!businessType.isEmpty()) {
                    // Dairy / Tailoring / Kirana / Other -> Daily
                    empIncomeFrequency = "Daily";

                } else {

                    // ---- 2. Agriculture ----
                    BCMPIIncomeDetailsWrapper.Agriculture agriculture = bcmpiIncomeDetailsWrapper.getAgriculture();
                    BCMPIIncomeDetailsWrapper.AgricultureDetail agricultureDetail = (agriculture != null)
                            ? (useCoApplicant ? agriculture.getCoApplicant() : agriculture.getApplicant())
                            : null;

                    if (agricultureDetail != null) {
                        empIncomeFrequency = "Daily";

                    } else {

                        // ---- 3. Salary ----
                        BCMPIIncomeDetailsWrapper.Salary salary = bcmpiIncomeDetailsWrapper.getSalary();
                        BCMPIIncomeDetailsWrapper.SalaryDetails salaryDetails = (salary != null)
                                ? (useCoApplicant ? salary.getCoApplicant() : salary.getApplicant())
                                : null;

                        if (salaryDetails != null) {
                            empIncomeFrequency = "NA";

                        } else {

                            // ---- 4. Wage ----
                            BCMPIIncomeDetailsWrapper.Wage wage = bcmpiIncomeDetailsWrapper.getWage();
                            BCMPIIncomeDetailsWrapper.WageDetail wageDetail = (wage != null)
                                    ? (useCoApplicant ? wage.getCoApplicant() : wage.getApplicant())
                                    : null;

                            if (wageDetail != null) {
                                empIncomeFrequency = "NA";

                            } else {

                                // ---- 5. Rental Income ----
                                BCMPIIncomeDetailsWrapper.RentalIncome rentalIncome = bcmpiIncomeDetailsWrapper.getRentalIncome();
                                BCMPIIncomeDetailsWrapper.RentalDetail rentalIncomeDetail = (rentalIncome != null)
                                        ? (useCoApplicant ? rentalIncome.getCoApplicant() : rentalIncome.getApplicant())
                                        : null;

                                if (rentalIncomeDetail != null) {
                                    empIncomeFrequency = "NA";

                                } else {

                                    // ---- 6. Pension ----
                                    BCMPIIncomeDetailsWrapper.Pension pension = bcmpiIncomeDetailsWrapper.getPension();

                                    if (pension != null) {
                                        empIncomeFrequency = "NA";

                                    } else {

                                        // ---- 7. Homemaker ----
                                        String homemaker = bcmpiIncomeDetailsWrapper.getHomeMaker();

                                        if (homemaker != null) {
                                            empIncomeFrequency = "NA";

                                        } else {
                                            // ---- 8. Nothing matched ----
                                            empIncomeFrequency = "NA";
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        incomeMap.put("modeOfIncome", empIncomeType);
        incomeMap.put("frequencyOfIncome", empIncomeFrequency);
        incomeMap.put("annualIncome", empYearlyIncome);
        incomeMap.put("otherSourceIncome", otherSourcesOfAnnualIncome);
        logger.debug("Returning IncomeData"+ incomeMap);
        return incomeMap;
    }

}
