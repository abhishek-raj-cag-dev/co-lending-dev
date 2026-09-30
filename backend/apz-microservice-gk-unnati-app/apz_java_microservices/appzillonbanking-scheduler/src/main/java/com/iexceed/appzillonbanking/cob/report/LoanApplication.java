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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.iexceed.appzillonbanking.cob.core.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cob.core.domain.ab.BankDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.OccupationDetails;
import com.iexceed.appzillonbanking.cob.core.payload.Address;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.ApplicationTimelineDtl;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.InsuranceDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.InsuranceDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.LoanDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.OccupationDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.OccupationDetailsWrapper;
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
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.Markup;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;

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

    public LoanApplication() {
        borderedStyle = stl.style(stl.penThin()).setPadding(5);
        boldTextWithBorder = stl.style(stl.penThin()).setPadding(5);
        boldCenteredStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        boldText = stl.style();
        boldLeftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

//		StyleBuilder headerStyle = stl.style().setFontSize(20).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
        leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

    }

    public Response generateLoanApplicationPdf(ApplicationMaster applicationMasterData,
			CustomerDataFields customerFileds, JSONObject keysForContent, String language, String kmId2, String bmId2,
			String kmSubmDateStr2, String bmSubmDateStr2, String usernameKM, String usernameBM)
            throws DRException, IOException {
    	 kmId = kmId2;
         bmId = bmId2;
         kmSubmDateStr = kmSubmDateStr2;
         bmSubmDateStr = bmSubmDateStr2;
         kmName = usernameKM;
         bmName = usernameBM;
         
        String base64String = null;
        
        Response response;
        try {
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
                    .title(getAddressDetails(keysForContent, customerFileds)).title(cmp.text(""));

            /* Other Deatails */
            subReport5.title(cmp.text(keysForContent.getString("otherDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getOtherDetails(keysForContent, customerFileds)).title(cmp.text(""));

            /* Ocupation / Employemnent Details */
            subReport6
                    .title(cmp.text(keysForContent.getString("employemnentDetails")).setMarkup(Markup.HTML)
                            .setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getEmployemnentDetails(keysForContent, customerFileds, payload1, payload2))
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
                    .title(getCustNameSignPhoto(keysForContent, customerFileds)).title(cmp.text(""));

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

	public String generateLoanApplicationPdfForDBKit(ApplicationMaster applicationMasterData,
                                                     CustomerDataFields customerFileds, JSONObject keysForContent, String filePath, String language, String kmId2, String bmId2, String kmSubmDateStr2, String bmSubmDateStr2, String usernameKM, String usernameBM)
			throws DRException, IOException, JRException {

        kmId = kmId2;
        bmId = bmId2;
        kmSubmDateStr = kmSubmDateStr2;
        bmSubmDateStr = bmSubmDateStr2;
        kmName = usernameKM;
        bmName = usernameBM;

        try {
            logger.debug("Inside loan application generation: ");

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
                    .title(getAddressDetails(keysForContent, customerFileds)).title(cmp.text(""));

            /* Other Deatails */
            subReport5.title(cmp.text(keysForContent.getString("otherDetails")).setMarkup(Markup.HTML).setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getOtherDetails(keysForContent, customerFileds)).title(cmp.text(""));

            /* Ocupation / Employemnent Details */
            subReport6
                    .title(cmp.text(keysForContent.getString("employemnentDetails")).setMarkup(Markup.HTML)
                            .setStyle(boldLeftStyle.setFontSize(14)))
                    .title(getEmployemnentDetails(keysForContent, customerFileds, payload1, payload2))
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
                    .title(getCustNameSignPhoto(keysForContent, customerFileds)).title(cmp.text(""));

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
            CibilDetailsPayload cibilPayloadCoApp = null;
            for (CibilDetailsWrapper cibilDetailsWrapper : customerFields.getCibilDetailsWrapperList()) {
                String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
//			String customerType = applicant ? applicantCustId : coApplicantCustId;

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

        VerticalListBuilder verticalList = cmp.verticalList();
        try {
            verticalList.add(createThreeHorizontalList2(keysForContent.getString(Constants.DETAILS),
                    keysForContent.getString(Constants.APPLICANT_KEY), keysForContent.getString(Constants.COAPPLICANT_KEY)));

            verticalList.add(createThreeHorizontalList(keysForContent.getString("title"), payload1.getTitle(),
                    CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getTitle())));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberFirstName"), applicantFirstname,
                    (coApplicant == null) ? "" : coApplicantFirstname));
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberLastName"), applicantLastname,
                    (coApplicant == null) ? "" : coApplicantLastname));
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
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberReligion"),
                    payload1.getReligion(), CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getReligion())));
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
        try {
            verticalList.add(createThreeHorizontalList(keysForContent.getString("memberCast"), payload1.getCaste(),
                    CommonUtils.getDefaultValue((coApplicant == null) ? "" : payload2.getCaste())));
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

    // 4
    // Address Applicant and Co-Applicant
    private ComponentBuilder<?, ?> getAddressDetails(JSONObject keysForContent, CustomerDataFields req) {
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

        String presentResidenceOwnershipCo = "";
        String presentAddressYearsCo = "";
        String presntCityYearsCo = "";
        String presentResidenceAddressProofCo = "";
        String presentResidenceTypeCo = "";
        String presentResidenceSizeCo = "NA";

        // Occupation Address
        String occpnAddrApplicant = "";
        String occpnAddrCoApplicant = "";
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
                    logger.debug("Present Address - Applicant : " + presentAddressApplicant);
                    presentResidenceOwnership = addr.getResidenceOwnership();
                    presentAddressYears = addr.getResidenceAddressSince();
                    presntCityYears = addr.getResidenceCitySince();
                    presentResidenceAddressProof = addr.getCurrentAddressProof();
                    presentResidenceType = addr.getHouseType();
                } else if (addr.getAddressType().equalsIgnoreCase("Permanent")) {
//					permanetAddressApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3()
//							+ addr.getArea()  + addr.getLandMark() + addr.getCity() + addr.getDistrict() + addr.getState()
//							+ addr.getCountry() + addr.getPinCode();
                    permanetAddressApplicant = getFullAddress(addr);
                    logger.debug("Permanent Address - Applicant :" + permanetAddressApplicant);
                }
            }

            // Present Address - CoApplicant
            if (coApplicantAddrPayLoadLst != null) {
                for (Address addr : coApplicantAddrPayLoadLst) {
                    if (addr.getAddressType().equalsIgnoreCase("present")) {
//						presentAddressCoApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3() + addr.getArea() + addr.getLandMark() + addr.getCity() + addr.getDistrict()+ addr.getState() + addr.getCountry() + addr.getPinCode();
                        presentAddressCoApplicant = getFullAddress(addr);
                        logger.debug("Present Address - Co-applicant :" + presentAddressCoApplicant);
                        presentResidenceOwnershipCo = addr.getResidenceOwnership();
                        presentAddressYearsCo = addr.getResidenceAddressSince();
                        presntCityYearsCo = addr.getResidenceCitySince();
                        presentResidenceAddressProofCo = addr.getCurrentAddressProof();
                        presentResidenceTypeCo = addr.getHouseType();
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
            occpnAddrCoApplicant = getFullAddress(ocupnAddrCo);
            logger.debug("Ocupation Address Applicnt : " + occpnAddrApplicant);
            logger.debug("Ocupation Address Co-Applicnt : " + occpnAddrCoApplicant);
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

    // 5. Other Deatails
    private ComponentBuilder<?, ?> getOtherDetails(JSONObject keysForContent, CustomerDataFields req) {
        VerticalListBuilder verticalList = cmp.verticalList();
        try {
            JSONObject addressDetailsObj = getAllAddressDeatils(req);
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

    private String applicantValueNA(boolean isHomeMaker, String value) {
    	return isHomeMaker ? "NA" : CommonUtils.getDefaultValue(value);
    }

    // 6.
    private ComponentBuilder<?, ?> getEmployemnentDetails(JSONObject keysForContent, CustomerDataFields req,
                                                          CustomerDetailsPayload custPayload1, CustomerDetailsPayload custPayload2) {
        VerticalListBuilder verticalList = cmp.verticalList();
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
            		applicantValueNA(isApplicantHomeMaker, payload1.getBusinessEmpStartDate()),
                    CommonUtils.getDefaultValue((coApplicant == null) ? "" : getValueIfNotNull(payload2.getBusinessEmpStartDate()))));
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
        }
        logger.debug("Occupation Details added");
        return verticalList;
    }

    private String getValueIfNotNull(String val) {
        if (StringUtils.isEmpty(val) || Constants.PLEASE_SELECT.equalsIgnoreCase(val.trim())) {
            return "NA";
        }
        return val.trim();
    }


    // 7.
    private ComponentBuilder<?, ?> getIncomeDetails4(JSONObject keysForContent, CustomerDataFields req) {
        VerticalListBuilder verticalList = cmp.verticalList();
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
                    payload1.getFreqOfIncome(),
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
        }
        logger.debug("Income Details added");
        return verticalList;
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
            verticalList.add(createSevenHorizontalList2(keysForContent.getString("forInsurance"),
                    keysForContent.getString("optedInsuranceYN"), keysForContent.getString("insuranceNominee"),
                    keysForContent.getString("nomineeDob"), keysForContent.getString("nomineeAge"),
                    keysForContent.getString("nomineeRelationShip"), keysForContent.getString("nomineeGender")));

            verticalList.add(createSevenHorizontalList(applicantName, getYNFlagValues1(appinsurance), appNomName,
                    appNomDob, appNomAge, appNomRel, appNomGender)); // Applicant
            verticalList.add(createSevenHorizontalList(coApplicantName, getYNFlagValues1(coappinsurance), coappNomName,
                    coappNomDob, coappNomAge, coappNomRel, coappNomGender)); // Co-aplicant
        } catch (Exception e) {
            logger.error("error - getInsuranceDetails");
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

    // 13. declaration2
    private String getDeclaration2(JSONObject keysForContent) {
        return keysForContent.getString("declaration2").trim();
    }

    // 14 declaration3
    private String getDeclaration3(JSONObject keysForContent) {
        return keysForContent.getString("declaration3").trim();
    }

    // 16.
    private ComponentBuilder<?, ?> getCustNameSignPhoto(JSONObject keysForContent, CustomerDataFields req) {
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(createFourHorizontalList(keysForContent.getString("applicantType"),
                keysForContent.getString("applicantName"), keysForContent.getString("applicantSign"),
                keysForContent.getString("applicantPhotoAndSign")));

        verticalList
                .add(createFourHorizontalListWithHeight2(keysForContent.getString(Constants.APPLICANT_KEY), applicantName, "", "")); // Applicant
        verticalList.add(
                createFourHorizontalListWithHeight2(keysForContent.getString(Constants.COAPPLICANT_KEY), coApplicantName, "", "")); // Co-applicant

        logger.debug("CustNameSignPhoto added");
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
        verticalList.add(createFourHorizontalList3(kmName, kmId, "", kmSubmDateStr)); // Applicant
        verticalList.add(createFourHorizontalList3(bmName, bmId, "", bmSubmDateStr)); // Co-applicant
        logger.debug("BranchStaffDetails added");
        return verticalList;
    }

    private ComponentBuilder<?, ?> createThreeHorizontalList2(String value, String value1, String value2) {
        HorizontalListBuilder horizontalList = cmp.horizontalList();
        horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(20));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(40));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(40));
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
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setHeight(140));

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

}
