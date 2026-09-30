package com.iexceed.appzillonbanking.cob.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.iexceed.appzillonbanking.cob.core.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.payload.Address;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.ApplicationTimelineDtl;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.InsuranceDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.InsuranceDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.LoanDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.RepaymentSchedule;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
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
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;

public class SanctionLetter {

    private StyleBuilder borderedStyle, boldText, boldCenteredStyle, boldTextWithBorder, boldLeftStyle, rightStyle,
            leftStyle;

    private String applicantCustId = "";
    private String coApplicantCustId = "";
    private String applicantName = "";
    private String coApplicantName = "";

    private String bmId = "";
    private String bmName = "";
    private String productName = "";
    
    String appltGender = "";
    String coAppltGender = "";

    private static final Logger logger = LogManager.getLogger(SanctionLetter.class);

    static String space = "\u00a0\u00a0\u00a0";
    int width30 = 30;
    int width70 = 70;
    int width80 = 80;
    int width20 = 20;
    int width50 = 50;
    int width0 = 0;

    public SanctionLetter() {

        borderedStyle = stl.style(stl.penThin()).setPadding(5);
        boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
        boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        boldText = stl.style().bold();
        boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

        rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
        leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

    }

    public Response generatePdf(JSONObject keysForContent, CustomerDataFields customerFields, String sactionedDateStr, List<RepaymentSchedule> repaymentList, String bmUserId, String usernameBM, String language, String productDetail) throws DRException, IOException {
        logger.debug("Inside generatePdf - sanctionLetter");

		bmId = bmUserId;
		bmName = usernameBM;
		productName = productDetail;
		
		String base64String = null;
		
        String filePath = "";
        Response response;
        String serverImagePath = CommonUtils.getExternalProperties("images") + "logo-name.png";

        StyleBuilder headerStyle = stl.style().setFontSize(20)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();

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
        JasperReportBuilder subReport9 = new JasperReportBuilder();
        JasperReportBuilder subReport10 = new JasperReportBuilder();
        JasperReportBuilder subReport11 = new JasperReportBuilder();
        JasperReportBuilder subReport12 = new JasperReportBuilder();
        JasperReportBuilder subReportThankYouBlock = new JasperReportBuilder();

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
        subReportThankYouBlock.setDataSource(emptyDataSource);

        try {
            report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));
            CustomerDetailsPayload payload1 = null;
            CustomerDetailsPayload payload2 = null;
            Gson gsonObj = new Gson();
            for (CustomerDetails custDtl : customerFields.getCustomerDetailsList()) {
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


            BigDecimal sactionAmtDb = customerFields.getLoanDetails().getSanctionedLoanAmount();
            String sactionAmt = String.valueOf(sactionAmtDb == null ? "" : sactionAmtDb.toPlainString());

            long amount = sactionAmtDb == null ? 0L : sactionAmtDb.longValue();
            String snAmtInWords = convert(amount);

            String note = keysForContent.getString("letter1");
            String letter1 = note.replace(Constants.SANCTIONED_AMOUNT, CommonUtils.amountFormat(sactionAmt)+"/- ");
            String productCode = customerFields.getApplicationMaster().getProductCode();
            String customerId = productCode
                    .equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE) ?
                    customerFields.getApplicationMaster().getApplicantT24Id() :
                    customerFields.getApplicationMaster().getSearchCode2();

            String applicationName = "";

            if (Constants.UNNATI_PRODUCT_CODE.equals(productCode)) {
            	applicationName = keysForContent.getString("applicationName");
            }else if(Constants.RENEWAL_PRODUCT_CODE.equals(productCode)) {
            	applicationName = keysForContent.getString("applicationNameRenewal");
            }else if(Constants.OPENMARKET_LOAN_PRODUCT_CODE.equalsIgnoreCase(productCode)) {
                applicationName = keysForContent.getString("applicationNameOpenMarket");
            }
            
            subReport1.title(cmp.text(applicationName).setMarkup(Markup.HTML).setStyle(headerStyle.setFontSize(12).underline()));
            subReport1.title(
                    cmp.horizontalList(
                            cmp.text(keysForContent.getString("customerId") +" "+ customerId).setMarkup(Markup.HTML).setStyle(leftStyle),
                            cmp.text(keysForContent.getString("date") +" "+ sactionedDateStr).setMarkup(Markup.HTML).setStyle(rightStyle)
                    )
            );



            subReport2.title(getSanctionLetterTable(keysForContent, customerFields)).title(cmp.text(""));
            subReport2.title(cmp.text(keysForContent.getString("letter")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport2.title(cmp.text(""));
            subReport2.title(cmp.text(keysForContent.getString("subject")).setMarkup(Markup.HTML).setStyle(boldText));
            subReport2.title(cmp.text(""));
            subReport2.title(cmp.text(letter1).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport3.title(cmp.text(""));
            subReport3
                    .title(getTermsAndConditionsForSanction(keysForContent, customerFields, sactionAmt, snAmtInWords, repaymentList))
                    .title(cmp.text(""));

            subReport5.title(getTermsAndConditionsForSanction2(keysForContent, customerFields)).title(cmp.text(""));
            subReport6.title(cmp.text(keysForContent.getString("termsANdConsitions")).setMarkup(Markup.HTML).setStyle(boldText));
            subReport6.title(cmp.text(""));
            subReport6.title(cmp.text(keysForContent.getString("termsAndConditions1")).setMarkup(Markup.HTML).setStyle(leftStyle));

            String ageLimit = "";
            if(Constants.RENEWAL_LOAN_PRODUCT_CODE.equalsIgnoreCase(productCode)){
                ageLimit = Constants.RENEWAL_AGE_LIMIT;
            }else if (Constants.OPENMARKET_LOAN_PRODUCT_CODE.equalsIgnoreCase(productCode)){
                ageLimit = Constants.OPENMARKET_AGE_LIMIT;
            }else{
                ageLimit = Constants.UNNATI_AGE_LIMIT;
            }

            subReport6.title(cmp.text(keysForContent.getString("termsAndConditions2").replace("<ageLimit>", ageLimit)).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport6.title(cmp.text(keysForContent.getString("termsAndConditions3")).setMarkup(Markup.HTML).setStyle(leftStyle));

            subReport6.title(cmp.text(keysForContent.getString("termsAndConditions4")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport6.title(cmp.text(keysForContent.getString("termsAndConditions5")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions6")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions7")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions8")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions9")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions10")).setMarkup(Markup.HTML).setStyle(leftStyle));
                subReport7.title(cmp.text(keysForContent.getString("termsAndConditions11")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions12")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions13")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions14")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions15")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions16")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions17")).setMarkup(Markup.HTML).setStyle(leftStyle));

            subReport8.title(cmp.text(keysForContent.getString("termsAndConditions18")).setMarkup(Markup.HTML).setStyle(leftStyle));

            subReport8.title(cmp.text(keysForContent.getString("termsAndConditions19")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport8.title(cmp.text(keysForContent.getString("termsAndConditions20")).setMarkup(Markup.HTML).setStyle(leftStyle));

            subReport8.title(cmp.text(keysForContent.getString("termsAndConditions21")).setMarkup(Markup.HTML).setStyle(leftStyle));

            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions22")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions23")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions24")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions25")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions26")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions27")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions28")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions29")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(""));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions30")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions31")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(""));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions32")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(""));

            subReportThankYouBlock.title(cmp.text(keysForContent.getString("termsAndConditionsMutualBeneficial")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReportThankYouBlock.title(cmp.text(""));

            subReportThankYouBlock.title(cmp.text(keysForContent.getString("termsAndConditions33")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReportThankYouBlock.title(cmp.text(""));

            subReport10.title(cmp.text(keysForContent.getString("authorization")).setMarkup(Markup.HTML).setStyle(boldText));
            subReport10.title(cmp.text(keysForContent.getString("name") + " : "+ bmName).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport10.title(cmp.text(keysForContent.getString("designation")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport10.title(cmp
                    .text(keysForContent.getString("employeeID") + bmId).setMarkup(Markup.HTML)
                    .setStyle(leftStyle));
            subReport10.title(cmp.text(keysForContent.getString(Constants.SIGNATURE) + " : ").setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport10.title(cmp.text(""));
            subReport11.title(cmp.text(keysForContent.getString("termsAndConditions34")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport12.title(getSanctionLetterTable2(keysForContent)).title(cmp.text(""));

            report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30))
                    .pageHeader(cmp.image(serverImagePath).setHorizontalImageAlignment(HorizontalImageAlignment.CENTER)
                            .setStyle(boldCenteredStyle.setFontSize(12)))
//.pageFooter(cmp.pageNumber().setStyle(leftStyle))
                    .setDataSource(new JREmptyDataSource(1))
                    .detail(cmp.verticalList(cmp.subreport(subReport), cmp.subreport(subReport1),
                            cmp.subreport(subReport2), cmp.subreport(subReport3), cmp.subreport(subReport4),
                            cmp.subreport(subReport5), cmp.subreport(subReport6), cmp.subreport(subReport7),
                            cmp.subreport(subReport8), cmp.subreport(subReport9),cmp.subreport(subReportThankYouBlock), cmp.subreport(subReport10),
                            cmp.subreport(subReport11), cmp.subreport(subReport12)));

            logger.debug("added all subreports to report builder");

            try {
                response = new Response();
                Properties prop = CommonUtils.readPropertyFile();
                // Construct file path
                String filePathDest = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/" + "APZCBO" + "/"
                        + Constants.LOAN + "/" + customerFields.getApplicationId() + "/";
                logger.debug("filePathDest :: {}", filePathDest);

                // Ensure directory exists
                File directory = new File(filePathDest);
                if (!directory.exists()) {
                    boolean isCreated = directory.mkdirs();
                    if (!isCreated) {
                        throw new IOException("Failed to create directory: " + filePathDest);
                    }
                }

                filePath = filePathDest + customerFields.getApplicationId() + "_SanctionLetter" + ".pdf";
                
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
			        response = getSuccessJson(mergedHtml.toString());
			        
			        
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

                // Delete the file
                // Files.deleteIfExists(Paths.get(filePath));
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

	public String generatePdfForDbKit(JSONObject keysForContent, CustomerDataFields customerFields, String filePath, String sactionedDateStr, List<RepaymentSchedule> repaymentList, String language, String bmUserId, String usernameBM, String productName1) throws DRException, IOException, JRException {
        bmId = bmUserId;
        bmName = usernameBM;
        productName = productName1;

        String serverImagePath = CommonUtils.getExternalProperties("images") + "logo-name.png";

        StyleBuilder headerStyle = stl.style().setFontSize(20)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();

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
        JasperReportBuilder subReport9 = new JasperReportBuilder();
        JasperReportBuilder subReport10 = new JasperReportBuilder();
        JasperReportBuilder subReport11 = new JasperReportBuilder();
        JasperReportBuilder subReport12 = new JasperReportBuilder();
        JasperReportBuilder subReportThankYouBlock = new JasperReportBuilder();


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
        subReportThankYouBlock.setDataSource(emptyDataSource);

        try {
            report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));
            CustomerDetailsPayload payload1 = null;
            CustomerDetailsPayload payload2 = null;
            Gson gsonObj = new Gson();
            for (CustomerDetails custDtl : customerFields.getCustomerDetailsList()) {
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

            BigDecimal sactionAmtDb = customerFields.getLoanDetails().getSanctionedLoanAmount();
            String sactionAmt = String.valueOf(sactionAmtDb == null ? "" : sactionAmtDb.toPlainString());

            long amount = sactionAmtDb == null ? 0L : sactionAmtDb.longValue();
            String snAmtInWords = convert(amount);

            String note = keysForContent.getString("letter1");
            String letter1 = note.replace(Constants.SANCTIONED_AMOUNT, CommonUtils.amountFormat(sactionAmt)+"/- ");

            /* Basic Application Details */
            String productCode = customerFields.getApplicationMaster().getProductCode();
            String customerId = productCode
                    .equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE) ?
                    customerFields.getApplicationMaster().getApplicantT24Id() :
                    customerFields.getApplicationMaster().getSearchCode2();
            
            String applicationName = "";

            if (Constants.UNNATI_PRODUCT_CODE.equals(productCode)) {
            	applicationName = keysForContent.getString("applicationName");
            }else if(Constants.RENEWAL_PRODUCT_CODE.equals(productCode)) {
            	applicationName = keysForContent.getString("applicationNameRenewal");
            }else if(Constants.OPENMARKET_LOAN_PRODUCT_CODE.equalsIgnoreCase(productCode)) {
                applicationName = keysForContent.getString("applicationNameOpenMarket");
            }
            
            subReport1.title(cmp.text(applicationName).setMarkup(Markup.HTML).setStyle(headerStyle.setFontSize(12).underline()));
            subReport1.title(
                    cmp.horizontalList(
                            cmp.text(keysForContent.getString("customerId") +" "+ customerId).setMarkup(Markup.HTML).setStyle(leftStyle),
                            cmp.text(keysForContent.getString("date") +" "+ sactionedDateStr).setMarkup(Markup.HTML).setStyle(rightStyle)
                    )
            );

            subReport2.title(getSanctionLetterTable(keysForContent, customerFields)).title(cmp.text(""));
            subReport2.title(cmp.text(keysForContent.getString("letter")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport2.title(cmp.text(""));
            subReport2.title(cmp.text(keysForContent.getString("subject")).setMarkup(Markup.HTML).setStyle(boldText));
            subReport2.title(cmp.text(""));
            subReport2.title(cmp.text(letter1).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport3.title(cmp.text(""));
            subReport3
                    .title(getTermsAndConditionsForSanction(keysForContent, customerFields, sactionAmt, snAmtInWords, repaymentList))
                    .title(cmp.text(""));

            subReport4.title(getTermsAndConditionsForSanction2(keysForContent, customerFields)).title(cmp.text(""));
            subReport5.title(cmp.text(keysForContent.getString("termsANdConsitions")).setMarkup(Markup.HTML).setStyle(boldText));
            subReport5.title(cmp.text(""));
            subReport5.title(cmp.text(keysForContent.getString("termsAndConditions1")).setMarkup(Markup.HTML).setStyle(leftStyle));

            String ageLimit = "";
            if(Constants.RENEWAL_LOAN_PRODUCT_CODE.equalsIgnoreCase(productCode)){
                ageLimit = Constants.RENEWAL_AGE_LIMIT;
            }else if (Constants.OPENMARKET_LOAN_PRODUCT_CODE.equalsIgnoreCase(productCode)){
                ageLimit = Constants.OPENMARKET_AGE_LIMIT;
            }else{
                ageLimit = Constants.UNNATI_AGE_LIMIT;
            }
            subReport5.title(cmp.text(keysForContent.getString("termsAndConditions2").replace("<ageLimit>", ageLimit)).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport5.title(cmp.text(keysForContent.getString("termsAndConditions3")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport5.title(cmp.text(keysForContent.getString("termsAndConditions4")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport6.title(cmp.text(keysForContent.getString("termsAndConditions5")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport6.title(cmp.text(keysForContent.getString("termsAndConditions6")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport6.title(cmp.text(keysForContent.getString("termsAndConditions7")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport6.title(cmp.text(keysForContent.getString("termsAndConditions8")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport6.title(cmp.text(keysForContent.getString("termsAndConditions9")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport6.title(cmp.text(keysForContent.getString("termsAndConditions10")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport6.title(cmp.text(keysForContent.getString("termsAndConditions11")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions12")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions13")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions14")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions15")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions16")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions17")).setMarkup(Markup.HTML).setStyle(leftStyle));

            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions18")).setMarkup(Markup.HTML).setStyle(leftStyle));

            subReport7.title(cmp.text(keysForContent.getString("termsAndConditions19")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport8.title(cmp.text(keysForContent.getString("termsAndConditions20")).setMarkup(Markup.HTML).setStyle(leftStyle));

            subReport8.title(cmp.text(keysForContent.getString("termsAndConditions21")).setMarkup(Markup.HTML).setStyle(leftStyle));

            subReport8.title(cmp.text(keysForContent.getString("termsAndConditions22")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport8.title(cmp.text(keysForContent.getString("termsAndConditions23")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport8.title(cmp.text(keysForContent.getString("termsAndConditions24")).setMarkup(Markup.HTML).setStyle(leftStyle));

            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions25")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions26")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions27")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions28")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions29")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions30")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions31")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport9.title(cmp.text(keysForContent.getString("termsAndConditions32")).setMarkup(Markup.HTML).setStyle(leftStyle));

            subReportThankYouBlock.title(cmp.text(keysForContent.getString("termsAndConditionsMutualBeneficial")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReportThankYouBlock.title(cmp.text(""));

            subReportThankYouBlock.title(cmp.text(keysForContent.getString("termsAndConditions33")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReportThankYouBlock.title(cmp.text(""));

            subReport10.title(cmp.text(keysForContent.getString("authorization")).setMarkup(Markup.HTML).setStyle(boldText));
            subReport10.title(cmp.text(keysForContent.getString("name") + " : "+ bmName).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport10.title(cmp.text(keysForContent.getString("designation")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport10.title(cmp
                    .text(keysForContent.getString("employeeID") + bmId).setMarkup(Markup.HTML)
                    .setStyle(leftStyle));
            subReport10.title(cmp.text(keysForContent.getString(Constants.SIGNATURE) + " : ").setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport10.title(cmp.text(""));
            subReport11.title(cmp.text(keysForContent.getString("termsAndConditions34")).setMarkup(Markup.HTML).setStyle(leftStyle));
            subReport12.title(getSanctionLetterTable2(keysForContent)).title(cmp.text(""));

            report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30))
                    .pageHeader(cmp.image(serverImagePath).setHorizontalImageAlignment(HorizontalImageAlignment.CENTER)
                            .setStyle(boldCenteredStyle.setFontSize(12)))
//.pageFooter(cmp.pageNumber().setStyle(leftStyle))
                    .setDataSource(new JREmptyDataSource(1))
                    .detail(cmp.verticalList(cmp.subreport(subReport), cmp.subreport(subReport1),
                            cmp.subreport(subReport2), cmp.subreport(subReport3), cmp.subreport(subReport4),
                            cmp.subreport(subReport5), cmp.subreport(subReport6), cmp.subreport(subReport7),
                            cmp.subreport(subReport8), cmp.subreport(subReport9), cmp.subreport(subReportThankYouBlock),cmp.subreport(subReport10),
                            cmp.subreport(subReport11), cmp.subreport(subReport12)));

            logger.debug("added all subreports to report builder");


            try {
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
                try {
                    keysForContent.keySet().forEach(key -> {
                        Object val = keysForContent.get(key);
                        if (val instanceof String) {
                            String text = (String) val;
                            if(text.length() > 800) {
                                logger.error("KEY [{}] length={} value={}", key, text.length(), text);
                            }
                        }
                    });
                } catch (Exception ex) {
                    logger.error("Failed dumping keysForContent", ex);
                }

                throw e;
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return null;
    }

    private HorizontalListBuilder addCheckBox(String line, HorizontalListBuilder horizontalList) {
        String[] parts = line.split("1");
        for (int i = 0; i < parts.length; i++) {
            // Add text with appropriate width and no wrapping
            horizontalList.add(cmp.text(parts[i])
                    .setStyle(stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT).setFontSize(10))
                    .setFixedWidth(160) // Adjust the width to fit your report layout
            );

            // Add checkbox image
            if (i < parts.length - 1) {
                horizontalList.add(cmp.image("src/main/resources/images/checkBox.png").setFixedDimension(12, 12) // Set
                        // fixed
                        // size
                        // for
                        // the
                        // checkbox
                        .setStyle(stl.style().setHorizontalImageAlignment(HorizontalImageAlignment.LEFT)));
            }
        }
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

    /*
     * public static String formatCurrency(String amount) { double d =
     * Double.parseDouble(amount); //DecimalFormat f = new
     * DecimalFormat("#,##,##0.00"); return f.format(d); }
     */

    private ComponentBuilder<?, ?> createSingleHorizontalListForScheduleA(String Key, String value) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();
//		horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(25));
//		horizontalList.add(cmp.text(value).setStyle(borderedStyle).setWidth(75));

        horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setWidth(100));
        return horizontalList;

    }

    private ComponentBuilder<?, ?> createFourHorizontalListForConsentLetter(String Key, String value, String key1,
                                                                            String value1) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

//	horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(25));
//	horizontalList.add(cmp.text(value).setStyle(borderedStyle).setWidth(75));

        horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setWidth(15));
        horizontalList.add(cmp.text(value).setStyle(borderedStyle).setWidth(25));
        horizontalList.add(cmp.text(key1).setStyle(borderedStyle).setWidth(25));
        horizontalList.add(cmp.text(value1).setStyle(borderedStyle).setWidth(35));

        return horizontalList;

    }

    private ComponentBuilder<?, ?> getSanctionLetterTable(JSONObject keysForContent, CustomerDataFields req) {

        VerticalListBuilder verticalList = cmp.verticalList();
        JSONObject addressDetailsObj = getAllAddressDeatils(req);
        verticalList.add(createThreeHorizontalListForSanctionLetter(keysForContent.getString("applicantType"),
                keysForContent.getString("name"), keysForContent.getString("residingAt"), boldTextWithBorder));
        verticalList.add(createThreeHorizontalListForSanctionLetter(keysForContent.getString("applicant"),
                applicantName, addressDetailsObj.getString(Constants.PRESENT_ADDRESS_APPLICANT), borderedStyle));
        verticalList.add(createThreeHorizontalListForSanctionLetter(keysForContent.getString("coApplicant"),
                coApplicantName, addressDetailsObj.getString(Constants.PRESENT_ADDRESS_COAPPLICANT), borderedStyle));

        return verticalList;
    }

    private ComponentBuilder<?, ?> getSanctionLetterTable2(JSONObject keysForContent) {

        VerticalListBuilder verticalList = cmp.verticalList();

        verticalList.add(createThreeHorizontalListForSanctionLetter(keysForContent.getString("applicantType"),
                keysForContent.getString("name"), keysForContent.getString(Constants.SIGNATURE), boldTextWithBorder));
        verticalList.add(createThreeHorizontalListForSanctionLetter(keysForContent.getString("applicant"),
                applicantName, "", borderedStyle));
        verticalList.add(createThreeHorizontalListForSanctionLetter(keysForContent.getString("coApplicant"),
                coApplicantName, "", borderedStyle));

        return verticalList;
    }

    private ComponentBuilder<?, ?> createThreeHorizontalListForSanctionLetter(String key, String value1, String value2,
                                                                              ReportStyleBuilder style) {
        HorizontalListBuilder horizontalList = cmp.horizontalList();
//		horizontalList.add(cmp.text(key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(20));
        horizontalList.add(cmp.text(key).setMarkup(Markup.HTML).setStyle(style).setWidth(30));
        horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(style).setWidth(30));
        horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(style).setWidth(40));
        return horizontalList;
    }

    private ComponentBuilder<?, ?> createTwoHorizontalListForSanctionLetter(String Key, String value, int width1,
                                                                            int width2) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(width1));
        horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(width2));

        return horizontalList;

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

    private ComponentBuilder<?, ?> incidentalFinancialCharges(JSONObject keysForContent, CustomerDataFields req,
                                                              String Key, String value) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();
        VerticalListBuilder verticalList = cmp.verticalList();
        Gson gsonObj = new Gson();
        InsuranceDetailsPayload coapplicantPayload = null;
        InsuranceDetailsPayload applicantPayload = null;
        InsuranceDetailsPayload jointPayload = null;
        String productCode = req.getApplicationMaster().getProductCode();
        try {
            horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(30));

            for (InsuranceDetailsWrapper insuranceDetailsWrapper : req.getInsuranceDetailsWrapperList()) {
                String custId = String.valueOf(insuranceDetailsWrapper.getInsuranceDetails().getCustDtlId());

                if (custId.equals(applicantCustId)) {
                    applicantPayload = gsonObj.fromJson(
                            insuranceDetailsWrapper.getInsuranceDetails().getPayloadColumn(),
                            InsuranceDetailsPayload.class);
                    logger.debug("InsuranceDetailsApplicantPayload : " + applicantPayload);
                } else if (custId.equals(coApplicantCustId)) {
                    coapplicantPayload = gsonObj.fromJson(
                            insuranceDetailsWrapper.getInsuranceDetails().getPayloadColumn(),
                            InsuranceDetailsPayload.class);
                    logger.debug("InsuranceDetailsCo-ApplicantPayload : " + coapplicantPayload);
                }else {
                    jointPayload = gsonObj.fromJson(insuranceDetailsWrapper.getInsuranceDetails().getPayloadColumn(),
                            InsuranceDetailsPayload.class);
                    logger.debug("InsuranceDetailsCo-ApplicantPayload : " + jointPayload);
                }
            }

            CibilDetailsPayload cibilPayloadCoApp = null;
            for (CibilDetailsWrapper cibilDetailsWrapper : req.getCibilDetailsWrapperList()) {
                String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();

                if (custId.equals(coApplicantCustId)) {
                    cibilPayloadCoApp = gsonObj.fromJson(
                            cibilDetailsWrapper.getCibilDetails().getPayloadColumn(), CibilDetailsPayload.class);
                }
            }

            String appinsurance = "";
            String coappinsurance = "";
            String apptPrInsuAmt = "";
            String coAppPrInsuAmt = "";
            String tenure = cibilPayloadCoApp.getFinalTenure();

            String processingFees = "";
            String processingFeesPercentage = "";
            if(null!= cibilPayloadCoApp){
                processingFees = cibilPayloadCoApp.getProcessingFees();
            }
            if(productCode.equalsIgnoreCase(Constants.RENEWAL_PRODUCT_CODE)) {
                processingFeesPercentage = Constants.RENEWAL_PROCESSING_FEE_PERCENTAGE;
            }else if (productCode.equalsIgnoreCase(Constants.OPENMARKET_LOAN_PRODUCT_CODE)){
                processingFeesPercentage = Constants.OPENMARKET_PROCESSING_FEE_PERCENTAGE;
            }else{
                processingFeesPercentage = Constants.UNNATI_PROCESSING_FEE_PERCENTAGE;
            }
            BigDecimal sanctionedAmount = req.getLoanDetails().getSanctionedLoanAmount() == null ? BigDecimal.ZERO : req.getLoanDetails().getSanctionedLoanAmount();

            BigDecimal limit = BigDecimal.valueOf(200000);
            BigDecimal insuranceSumAssured =
                    sanctionedAmount.compareTo(limit) > 0 ? limit : sanctionedAmount;

            String sumAssuredAmt = CommonUtils.formatIndianCurrency(String.valueOf(insuranceSumAssured));

            verticalList.add(cmp.text(value.replace("<processingFee>",processingFees)
                    .replace("<processingFeePercentage>", processingFeesPercentage))
                    .setMarkup(Markup.HTML).setStyle(borderedStyle));
            
            switch (applicantPayload.getInsuranceOption()) {
                case Constants.APPLICANT:
                    appinsurance = Constants.YES;
                    coappinsurance = Constants.NO;
                    apptPrInsuAmt = cibilPayloadCoApp.getInsuranceChargeMember();
                    break;
                case Constants.BOTH:
                    appinsurance = Constants.YES;
                    coappinsurance = Constants.YES;
                    apptPrInsuAmt = cibilPayloadCoApp.getInsuranceChargeMember();
                    coAppPrInsuAmt = cibilPayloadCoApp.getInsuranceChargeSpouse();
                    break;
                case Constants.JOINT:
                    appinsurance = Constants.YES;
                    coappinsurance = Constants.YES;
                    apptPrInsuAmt = cibilPayloadCoApp.getInsuranceChargeMember();
                    coAppPrInsuAmt = cibilPayloadCoApp.getInsuranceChargeSpouse();
                    break;
                default:
                    appinsurance = Constants.NO;
                    coappinsurance = Constants.NO;
                    sumAssuredAmt = "NA";
                    tenure = "NA";
                    break;
            }

            verticalList
                    .add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("incFinChargesPayKeyPair1"),
                            getYNFlagValues(appinsurance), width80, width20));
            verticalList
                    .add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("incFinChargesPayKeyPair2"),
                            CommonUtils.formatIndianCurrency(apptPrInsuAmt), width80, width20));
            verticalList
                    .add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("incFinChargesPayKeyPair3"),
                            getYNFlagValues(coappinsurance), width80, width20));
            verticalList
                    .add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("incFinChargesPayKeyPair4"),
                            CommonUtils.formatIndianCurrency(coAppPrInsuAmt), width80, width20));

            Map<String, Integer> sumAssuredRow = new LinkedHashMap<>();
            sumAssuredRow.put(keysForContent.getString("sumAssured"), 80);
            sumAssuredRow.put(sumAssuredAmt, 20);
            verticalList.add(createHorizontalList(sumAssuredRow));

            Map<String, Integer> insuranceTenureRow = new LinkedHashMap<>();
            insuranceTenureRow.put(keysForContent.getString("insuranceTenure"), 80);
            insuranceTenureRow.put(tenure, 20);
            verticalList.add(createHorizontalList(insuranceTenureRow));

            horizontalList.add(verticalList.setWidth(70));
        } catch (Exception e) {
            logger.error("error - incidentalFinancialCharges", e);
            logger.error(e.getMessage());
        }

        return horizontalList;

    }

    private String getYNFlagValues(String insuranceReqd) {
        if ("Y".equalsIgnoreCase(insuranceReqd)) {
            return "YES";
        } else if ("N".equalsIgnoreCase(insuranceReqd)) {
            return "NO";
        }
        return "NO";
    }

//verticalList.add(disbursementCharges(keysForContent, "13)"+keysForContent.getString("disbursement"), keysForContent.getString("disbursementValuePair")));

    private ComponentBuilder<?, ?> disbursementCharges(JSONObject keysForContent, String Key, String value) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();
        VerticalListBuilder verticalList = cmp.verticalList();

        horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(30));
        verticalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle));
        verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("disbursementValuePairA"),
                "", width50, width0));
        verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("disbursementValuePairB"),
                "", width50, width0));
        verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("disbursementValuePairC"),
                "", width50, width0));
        verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("disbursementValuePairD"),
                "", width50, width0));
        verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("disbursementValuePairE"),
                "", width50, width0));
        verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("disbursementValuePairF"),
                "", width50, width0));
        verticalList.add(createTwoHorizontalListForSanctionLetter(keysForContent.getString("disbursementValuePairG"),
                "", width50, width0));
        horizontalList.add(verticalList.setWidth(70));

        return horizontalList;

    }

    private ComponentBuilder<?, ?> getTermsAndConditionsForSanction(JSONObject keysForContent, CustomerDataFields req,
                                                                    String sactionAmt, String snAmtInWords, List<RepaymentSchedule> repaymentList) {

        VerticalListBuilder verticalList = cmp.verticalList();
        Gson gsonObj = new Gson();
        LoanDetailsPayload payload = gsonObj.fromJson(req.getLoanDetails().getPayloadColumn(),
                LoanDetailsPayload.class);
        logger.debug("LoanDetailsPayload : " + payload);

        CibilDetailsPayload cibilPayloadCoApp = new CibilDetailsPayload();
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
        String repaymentFrequency = cibilPayloadCoApp.getRepaymentFrequency();

//		String loanAmount = String.valueOf(req.getLoanDetails().getBmRecommendedLoanAmount());

        int noOfEPIs = repaymentList.size();
        logger.debug("Number of Records: " + noOfEPIs);

        String emi = "";
        if( repaymentList.size() > 0) {
            emi = repaymentList.get(2).getTotalDue();
        }

        String loanAmountValue = keysForContent.getString("loanAmountValue");
        String note5 = loanAmountValue.replace(Constants.SANCTIONED_AMOUNT, CommonUtils.amountFormat(sactionAmt) + "/- ")
                .replace("<sanctionedAmountInWords>", snAmtInWords);

        verticalList.add(createTwoHorizontalListForSanctionLetter("1) " + keysForContent.getString("natureOfFacility"),
        		productName, width30, width70));
        verticalList.add(createTwoHorizontalListForSanctionLetter("2) " + keysForContent.getString("purpose"),
                payload.getLoanPurpose(), width30, width70));
        verticalList.add(createTwoHorizontalListForSanctionLetter("3) " + keysForContent.getString("interestRate"),
                interest + keysForContent.getString("interestRateValuepair"), width30, width70));
        verticalList.add(createTwoHorizontalListForSanctionLetter("4) " + keysForContent.getString("interestType"),
                keysForContent.getString("interestTypeValuePair"), width30, width70));

        Map<String, Integer> loanAmountRow = new LinkedHashMap<>();
        loanAmountRow.put("5) " + keysForContent.getString("loanAmount"), 30);
        loanAmountRow.put(note5, 70);
        verticalList.add(createHorizontalList(loanAmountRow));

        verticalList.add(createTwoHorizontalListForSanctionLetter("6) " + keysForContent.getString("instalment"),
                keysForContent.getString("rs") + CommonUtils.amountFormat(emi)+"/-", width30, width70));
        verticalList.add(createTwoHorizontalListForSanctionLetter(
                "7) " + keysForContent.getString("numberOfInstallments"), String.valueOf(noOfEPIs), width30, width70));
        verticalList.add(createTwoHorizontalListForSanctionLetter("8) " + keysForContent.getString("repaymentFrequency"),
                repaymentFrequency, width30, width70));
        verticalList.add(createTwoHorizontalListForSanctionLetter("9) " + keysForContent.getString("tenureInMonths"),
                String.valueOf(term), width30, width70));
        verticalList.add(createTwoHorizontalListForSanctionLetter("10) " + keysForContent.getString("repayment"),
                keysForContent.getString("repaymentValuePair"), width30, width70));

        return verticalList;
    }

    private ComponentBuilder<?, ?> getTermsAndConditionsForSanction2(JSONObject keysForContent,
                                                                     CustomerDataFields req) {
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(incidentalFinancialCharges(keysForContent, req,
                "11) " + keysForContent.getString("incFinChargesPayable"),
                keysForContent.getString("incFinChargesPayableValuePair1")));
        verticalList.add(createTwoHorizontalListForSanctionLetter("12) " + keysForContent.getString("otherCharges"),
                keysForContent.getString("otherChargesValuePair"), width30, width70));
        verticalList.add(disbursementCharges(keysForContent, "13) " + keysForContent.getString("disbursement"),
                keysForContent.getString("disbursementValuePair")));

        verticalList.add(createTwoHorizontalListForSanctionLetter("14) " + keysForContent.getString("sanctionValidity"),
                keysForContent.getString("sanctionValidityValuePair"), width30, width70));

        return verticalList;
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
            String occpnAddrApplicant = "";
            String occpnAddrCoApplicant = "";

            // Applicant Address
            for (Address addr : applicantAddrPayLoadLst) {
                if (addr.getAddressType().equalsIgnoreCase("present")) {
//				presentAddressApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3()
//						+ addr.getArea() +  addr.getLandMark() + addr.getCity() + addr.getDistrict() + addr.getState()
//						+ addr.getCountry() + addr.getPinCode();

                    presentAddressApplicant = getFullAddress(addr);
                    logger.debug("Present Address - Applicant : " + presentAddressApplicant);
                    presentResidenceOwnership = addr.getResidenceOwnership();
                    presentAddressYears = addr.getResidenceAddressSince();
                    presntCityYears = addr.getResidenceCitySince();
                    presentResidenceAddressProof = addr.getCurrentAddressProof();
                    presentResidenceType = addr.getHouseType();
                } else if (addr.getAddressType().equalsIgnoreCase("Permanent")) {
//				permanetAddressApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3()
//						+ addr.getArea()  + addr.getLandMark() + addr.getCity() + addr.getDistrict() + addr.getState()
//						+ addr.getCountry() + addr.getPinCode();
                    permanetAddressApplicant = getFullAddress(addr);
                    logger.debug("Permanent Address - Applicant :" + permanetAddressApplicant);
                }
            }

            // Co-Applicant Address
            if (coApplicantAddrPayLoadLst != null) {
                for (Address addr : coApplicantAddrPayLoadLst) {
                    if (addr.getAddressType().equalsIgnoreCase("present")) {
//					presentAddressCoApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3() + addr.getArea() + addr.getLandMark() + addr.getCity() + addr.getDistrict()+ addr.getState() + addr.getCountry() + addr.getPinCode();
                        presentAddressCoApplicant = getFullAddress(addr);
                        logger.debug("Present Address - Co-applicant :" + presentAddressCoApplicant);
                        presentResidenceOwnershipCo = addr.getResidenceOwnership();
                        presentAddressYearsCo = addr.getResidenceAddressSince();
                        presntCityYearsCo = addr.getResidenceCitySince();
                        presentResidenceAddressProofCo = addr.getCurrentAddressProof();
                        presentResidenceTypeCo = addr.getHouseType();
                    } else if (addr.getAddressType().equalsIgnoreCase("Permanent")) {
//					permanetAddressCoApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3() + addr.getArea() + addr.getLandMark() + addr.getCity() + addr.getDistrict()+ addr.getState() + addr.getCountry() + addr.getPinCode();
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
//		if(addr.getAddressType().equalsIgnoreCase("Office")) {
            occpnAddrApplicant = getFullAddress(ocupnAddr);
            occpnAddrCoApplicant = getFullAddress(ocupnAddrCo);
            logger.debug("Ocupation Address Applicnt : " + occpnAddrApplicant);
            logger.debug("Ocupation Address Co-Applicnt : " + occpnAddrCoApplicant);
//			ocupnAddr.getAddressLine1() + ocupnAddr.getAddressLine2() + ocupnAddr.getAddressLine3()
//			+ ocupnAddr.getArea() +  ocupnAddr.getLandMark() + ocupnAddr.getCity() + ocupnAddr.getDistrict() + ocupnAddr.getState()
//			+ ocupnAddr.getCountry() + ocupnAddr.getPinCode();

//		}else if(addr.getAddressType().equalsIgnoreCase("Office")) {
//			occpnAddrCoApplicant = ocupnAddrCo.getAddressLine1() + ocupnAddrCo.getAddressLine2() + ocupnAddrCo.getAddressLine3()
//			+ ocupnAddrCo.getArea() +  ocupnAddrCo.getLandMark() + ocupnAddrCo.getCity() + ocupnAddrCo.getDistrict() + ocupnAddrCo.getState()
//			+ ocupnAddrCo.getCountry() + ocupnAddrCo.getPinCode();
//			}

            jsnObj = new JSONObject();
            jsnObj.put(Constants.PRESENT_ADDRESS_APPLICANT, presentAddressApplicant);
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

//	public class NumberToWordsConverter {

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
