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
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cob.core.domain.ab.BankDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.DeviationRATracker;
import com.iexceed.appzillonbanking.cob.core.domain.ab.SanctionMaster;
import com.iexceed.appzillonbanking.cob.core.payload.Address;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.LoanDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.loans.payload.BCMPIIncomeDetailsWrapper;
import com.iexceed.appzillonbanking.cob.loans.payload.BCMPIOtherDetailsWrapper;
import com.iexceed.appzillonbanking.cob.loans.payload.LoanObligationsNestedClass;
import com.iexceed.appzillonbanking.cob.loans.payload.LoanObligationsWrapper;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;
import net.sf.dynamicreports.report.constant.Markup;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.ReportStyleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.exception.DRException;

@Service
public class Form97 {

    private static final Logger logger = LogManager.getLogger(Form97.class);

    private StyleBuilder borderedStyle, boldText, headerTextWithBorder, boldCenteredStyle, boldTextWithBorder,
            boldLeftStyle, rightStyle, leftStyle;

    static String space = "\u00a0\u00a0\u00a0";
    private static final int PAGE_W      = 595;
    private static final int PAGE_H      = 842;
    private String applicantCustId = "";
    private String coApplicantCustId = "";
    private String applicantName = "";
    String  sactionedDate = "";
    String appMobNo = "";
    String coappMobNo = "";
    String apptKycNo = "NA";
    String coApptKycNo = "NA";
    BigDecimal sactionAmtDb = null;
    String sanctionedAmount = "";
    CustomerDetailsPayload applicantPayload = null;
    CustomerDetailsPayload coApplicantPayload = null;
    private String coApplicantName = "";
    private CustomerDetails applicantCustDtls = null;
    private CustomerDetails coApplicantCustDtls = null;
    String appltGender = "";
    String coAppltGender = "";
    String interestRate = "";
    String place = "";
    BankDetails coApplicant = null;
    BankDetailsPayload bankPayload1 = null;
    BankDetailsPayload bankPayload2 = null;
    private String bmId = "";

    public Form97() {

        borderedStyle = stl.style(stl.penThin()).setPadding(5);
        boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
        boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        boldText = stl.style().bold();
        boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
        headerTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold()
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
        rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
        leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

    }

    public String generatePdfForDbkit(JSONObject keysForContent, CustomerDataFields custmrDataFields, String language, String filePath, String sactionedDateStr) throws DRException, IOException {

        Response response;

        try {
            sactionedDate = sactionedDateStr;
            StyleBuilder tempStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);

            JasperReportBuilder report = new JasperReportBuilder();

            StyleBuilder headerStyle = stl.style().setFontSize(20)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();

            StyleBuilder style = stl.style().setBackgroundColor(Color.BLUE).setFontSize(20)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

            StyleBuilder style1 = stl.style().setFont(stl.font("SansSerif", true, false, 12))
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
            StyleBuilder style2 = stl.style().setFont(stl.font("SansSerif", true, false, 12))
                    .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT).bold();

            boolean loanOblExist = false;

            Gson gsonObj = new Gson();
            sactionAmtDb = custmrDataFields.getLoanDetails().getSanctionedLoanAmount();

            if (sactionAmtDb != null) {
                NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(2);

                sanctionedAmount = formatter.format(sactionAmtDb);
            }


            place = custmrDataFields.getApplicationMaster().getBranchName();
            for (CustomerDetails custDtl : custmrDataFields.getCustomerDetailsList()) {

                String customerType = custDtl.getCustomerType();
                logger.debug("Customer Type : {}", customerType);

                if ("Applicant".equalsIgnoreCase(customerType)) {

                    applicantCustId = String.valueOf(custDtl.getCustDtlId());
                    applicantName = custDtl.getCustomerName();
                    appMobNo = custDtl.getMobileNumber();
                    applicantCustDtls = custDtl;

                    applicantPayload = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    appltGender = applicantPayload.getGender();
                    logger.debug("Applicant CustId : {}", applicantCustId);
                    logger.debug("Applicant Payload : {}", applicantPayload);

                } else if ("Co-App".equalsIgnoreCase(customerType)) {

                    coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
                    coApplicantName = custDtl.getCustomerName();
                    coappMobNo = custDtl.getMobileNumber();
                    coApplicantCustDtls = custDtl;

                    coApplicantPayload = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
                    coAppltGender = coApplicantPayload.getGender();

                    logger.debug("Co-Applicant CustId : {}", coApplicantCustId);
                    logger.debug("Co-Applicant Payload : {}", coApplicantPayload);
                }
            }

            for (BankDetailsWrapper bkWrpr : custmrDataFields.getBankDetailsWrapperList()) {
                logger.debug("applicantCustId " + applicantCustId);
                if (String.valueOf(bkWrpr.getBankDetails().getCustDtlId()).equals(applicantCustId)) {
                    bankPayload1 = gsonObj.fromJson(bkWrpr.getBankDetails().getPayloadColumn(), BankDetailsPayload.class);
                    logger.debug("BankDetailsApplicantPayload : " + bankPayload1.toString());
                } else if (String.valueOf(bkWrpr.getBankDetails().getCustDtlId()).equals(coApplicantCustId)) {
                    coApplicant = bkWrpr.getBankDetails();
                    bankPayload2 = gsonObj.fromJson(bkWrpr.getBankDetails().getPayloadColumn(), BankDetailsPayload.class);
                    logger.debug("BankDetailsCo-applicantPayload : " + bankPayload2.toString());
                }
            }


            if (applicantPayload != null) {
                boolean isPrimaryVerified = Constants.VERIFIED_STS
                        .equalsIgnoreCase(applicantPayload.getPrimaryKycIdValStatus());
                boolean isAlternateVerified = Constants.VERIFIED_STS
                        .equalsIgnoreCase(applicantPayload.getAlternateVoterIdValStatus());

                if (isPrimaryVerified && isAlternateVerified) {
                    apptKycNo = applicantPayload.getAlternateVoterId();
                } else if (isAlternateVerified) {
                    apptKycNo = applicantPayload.getAlternateVoterId();
                } else if (isPrimaryVerified) {
                    apptKycNo = applicantPayload.getPrimaryKycId();
                }
            }

            if (coApplicantPayload != null) {
                boolean isPrimaryVerified = Constants.VERIFIED_STS
                        .equalsIgnoreCase(coApplicantPayload.getPrimaryKycIdValStatus());
                boolean isAlternateVerified = Constants.VERIFIED_STS
                        .equalsIgnoreCase(coApplicantPayload.getAlternateVoterIdValStatus());

                if (isPrimaryVerified && isAlternateVerified) {
                    coApptKycNo = coApplicantPayload.getAlternateVoterId();
                } else if (isAlternateVerified) {
                    coApptKycNo = coApplicantPayload.getAlternateVoterId();
                } else if (isPrimaryVerified) {
                    coApptKycNo = coApplicantPayload.getPrimaryKycId();
                }
            }


            report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));


            // ===================== PAN-BASED SECTION GENERATION (NEW) =====================
            // Decide whether applicant PAN is missing
            boolean applicantPanMissing = applicantPayload == null
                    || applicantPayload.getPanNumber() == null
                    || applicantPayload.getPanNumber().trim().isEmpty();

            // Decide whether co-applicant PAN is missing (only relevant if co-applicant exists)
            boolean coApplicantPanMissing = coApplicantPayload != null
                    && (coApplicantPayload.getPanNumber() == null
                    || coApplicantPayload.getPanNumber().trim().isEmpty());

            logger.debug("applicantPanMissing : {}", applicantPanMissing);
            logger.debug("coApplicantPanMissing : {}", coApplicantPanMissing);

            if (applicantPanMissing) {
                logger.info("Applicant PAN missing - generating Applicant Form97 section");
                buildPersonSection(report, keysForContent, custmrDataFields, "applicant");
            }

            if (coApplicantPanMissing) {
                logger.info("Co-Applicant PAN missing - generating Co-Applicant Form97 section");
                report.addSummary(cmp.pageBreak());

                // Swap instance fields to point to co-applicant data,
                // since all the helper methods (getIDDetails, getDeclaration,
                // getVerification etc.) read applicantName / applicantPayload /
                // apptKycNo / applicantCustDtls directly.
                String tempName = applicantName;
                String tempKyc = apptKycNo;
                CustomerDetailsPayload tempPayload = applicantPayload;
                CustomerDetails tempCustDtls = applicantCustDtls;

                applicantName = coApplicantName;
                apptKycNo = coApptKycNo;
                applicantPayload = coApplicantPayload;
                applicantCustDtls = coApplicantCustDtls;
                appMobNo = coappMobNo;
                buildPersonSection(report, keysForContent, custmrDataFields, "coapplicant");

                // restore original applicant fields back
                applicantName = tempName;
                apptKycNo = tempKyc;
                applicantPayload = tempPayload;
                applicantCustDtls = tempCustDtls;
            }

            // report.show();
            // ================================================================================

//			//saving report to the directory and creating base64 string for response

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
                logger.info("Form 97 Assessment PDF Report Generated");

                byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
                byte[] encodedBytes = Base64.getEncoder().encode(inputfile);
                return new String(encodedBytes);
            }





        } catch (Exception e) {
            logger.error("Error generating Form97 PDF", e);
            throw new DRException("Failed to generate Form97 PDF", e);
        }

    }

    /**
     * Builds the full Part A - Part F section (applicant or co-applicant)
     * and appends it directly into the passed-in report's summary band.
     * Relies on instance fields (applicantName, applicantPayload, apptKycNo,
     * applicantCustDtls) already being pointed at the correct person
     * by the caller before invoking this method.
     */
    private void buildPersonSection(JasperReportBuilder report, JSONObject keysForContent,
                                    CustomerDataFields custmrDataFields, String role) throws DRException {

        JasperReportBuilder subReport1 = new JasperReportBuilder();
        JasperReportBuilder subReport2 = new JasperReportBuilder();
        JasperReportBuilder subReport3 = new JasperReportBuilder();
        JasperReportBuilder subReport4 = new JasperReportBuilder();
        JasperReportBuilder subReport6 = new JasperReportBuilder();
        JasperReportBuilder subReport7 = new JasperReportBuilder();
        JasperReportBuilder subReport8 = new JasperReportBuilder();

        report.addSummary(
                cmp.text(keysForContent.getString("applicationName"))
                        .setStyle(boldCenteredStyle),
                cmp.verticalGap(10));

        subReport1.title(
                cmp.text(keysForContent.getString("applicationId"))
                        .setStyle(boldCenteredStyle),
                cmp.verticalGap(8),
                cmp.text(keysForContent.getString("kendraId"))
                        .setStyle(boldCenteredStyle),
                cmp.verticalGap(10));
        // report.addSummary(cmp.text(keysForContent.getString("applicationName")).setStyle(boldCenteredStyle));
        // subReport1.title(cmp.text(keysForContent.getString("applicationId")).setStyle(boldCenteredStyle));
        // subReport1.title(cmp.text(keysForContent.getString("kendraId")).setStyle(boldCenteredStyle));

        subReport2.title(cmp.text(keysForContent.getString("partA")).setStyle(headerTextWithBorder))
                .title(getNatureOfTran(keysForContent)).title(cmp.text(""));

        subReport3.title(cmp.text(keysForContent.getString("partB")).setStyle(headerTextWithBorder))
                .title(getDeclaration(keysForContent, custmrDataFields).setStyle(headerTextWithBorder)).title(cmp.text(""))
                .title(cmp.verticalGap(20))
                .title(cmp.text(keysForContent.getString("declarantSign"))
                        .setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT));

        subReport4.title(cmp.text(keysForContent.getString("partC")).setStyle(headerTextWithBorder))
                .title(getIDDetails(keysForContent, custmrDataFields)).title(cmp.text(""));

        subReport6.title(cmp.text(keysForContent.getString("partD")).setStyle(headerTextWithBorder))
                .title(getTranDetails(keysForContent)).title(cmp.text(""));

        subReport7.title(cmp.text(keysForContent.getString("partE")).setStyle(headerTextWithBorder))
                .title(getTranDocs(keysForContent)).title(cmp.text(""));

        subReport8.title(cmp.text(keysForContent.getString("partF")).setStyle(headerTextWithBorder))
                .title(getVerification(keysForContent).setStyle(headerTextWithBorder)).title(cmp.text(""));

        report.addSummary(cmp.subreport(subReport1))
                .addSummary(cmp.subreport(subReport2))
                .addSummary(cmp.subreport(subReport3))
                .addSummary(cmp.subreport(subReport4))
                .addSummary(cmp.subreport(subReport6))
                .addSummary(cmp.subreport(subReport7))
                .addSummary(cmp.subreport(subReport8));

        logger.debug("buildPersonSection completed for role : {}", role);
    }

    private ComponentBuilder<?, ?> getIDDetails(JSONObject keysForContent, CustomerDataFields custmrDataFields) {
        JSONObject addressDetailsObj = getAllAddressDeatils(custmrDataFields);
        String dob = applicantPayload.getDob();
        if (dob != null && !dob.isEmpty()) {
            dob = LocalDate.parse(dob)
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        // TODO Auto-generated method stub
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(createThreeHorizontalList("1", keysForContent.getString("idName"),
                applicantName));
        verticalList.add(createThreeHorizontalList("2", keysForContent.getString("idDOB"),
                dob));
        verticalList.add(createThreeHorizontalList("3", keysForContent.getString("fatherName"),
                applicantPayload.getFathersName()));
        verticalList.add(getPANAckdetails("4", keysForContent));

        verticalList.add(createThreeHorizontalList("5", keysForContent.getString("aadharNo"),
                ""));
        verticalList.add(createThreeHorizontalList("6", keysForContent.getString("residenceAddress"),
                addressDetailsObj.getString("permanentAddress")));
        verticalList.add(createThreeHorizontalList("7", keysForContent.getString("officeAddress"),
                ""));
        verticalList.add(createThreeHorizontalList("8", keysForContent.getString("communicationAddr"),
                keysForContent.getString("communicationAddrVal")));

        verticalList.add(getContactDetails("9", keysForContent));
        verticalList.add(createThreeHorizontalList("10", keysForContent.getString("tin"),
                ""));
        verticalList.add(getIncomeDetails("11", keysForContent));

        return verticalList;
    }

    private ComponentBuilder<?, ?> getContactDetails(String no, JSONObject keysForContent) {
        // TODO Auto-generated method stub
        HorizontalListBuilder horizontalList = cmp.horizontalList();
        VerticalListBuilder verticalList1 = cmp.verticalList();
        horizontalList.add(cmp.text(no).setStyle(borderedStyle).setWidth(10));
        verticalList1.add(cmp.horizontalList().add(cmp.text(keysForContent.getString("contactDetails")).setStyle(borderedStyle)));
        verticalList1.add(createThreeHorizontalListContact("(i)", keysForContent.getString("mobile"),
                keysForContent));
        verticalList1.add(createThreeHorizontalListContact("(ii)", keysForContent.getString("tel"),
                keysForContent));
        verticalList1.add(createThreeHorizontalList("(iii)", keysForContent.getString("email"),
                ""));
        horizontalList.add(verticalList1);
        return horizontalList;
    }

	/*private ComponentBuilder<?, ?> createThreeHorizontalListContact(String no, String string,
			ComponentBuilder<?, ?> two) {
		// TODO Auto-generated method stub
		return null;
	}*/

    private ComponentBuilder<?, ?> getTwo(JSONObject keysForContent) {
        // TODO Auto-generated method stub
        VerticalListBuilder verticalList = cmp.verticalList();
        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(keysForContent.getString("countryCode")).setStyle(borderedStyle));
        horizontalList.add(cmp.text(keysForContent.getString("number")).setStyle(borderedStyle));


        verticalList.add(horizontalList);
        HorizontalListBuilder horizontalList1 = cmp.horizontalList();

        horizontalList1.add(cmp.text("").setStyle(borderedStyle));
        horizontalList1.add(cmp.text("").setStyle(borderedStyle));


        verticalList.add(horizontalList1);
        //verticalList.add(createThreeHorizontalList("2", keysForContent.getString("tranTotalAmt"),
        //		""));
        //verticalList.add(createThreeHorizontalList("3", keysForContent.getString("tranAmt"),
        //""));
        //verticalList.add(createThreeHorizontalList("4", keysForContent.getString("tranMode"),
        //	keysForContent.getString("tranModeVal")));
        verticalList.add(getTranDoc("1", keysForContent,"POI"));
        verticalList.add(getTranDoc("2", keysForContent,"POA"));
        verticalList.add(getTranDoc("3", keysForContent,"DOBDOI"));
        //verticalList.add(getTranShares(keysForContent));

        return verticalList;
    }

    private ComponentBuilder<?, ?> getTranDocs(JSONObject keysForContent) {
        // TODO Auto-generated method stub
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(cmp.text(keysForContent.getString("partENote")).setStyle(borderedStyle));
        //verticalList.add(createThreeHorizontalList("2", keysForContent.getString("tranTotalAmt"),
        //		""));
        //verticalList.add(createThreeHorizontalList("3", keysForContent.getString("tranAmt"),
        //""));
        //verticalList.add(createThreeHorizontalList("4", keysForContent.getString("tranMode"),
        //	keysForContent.getString("tranModeVal")));
        verticalList.add(getTranDoc("1", keysForContent,"POI"));
        verticalList.add(getTranDoc("2", keysForContent,"POA"));
        verticalList.add(getTranDoc("3", keysForContent,"DOBDOI"));
        //verticalList.add(getTranShares(keysForContent));

        return verticalList;
    }

    private ComponentBuilder<?, ?> getTranDoc(String no, JSONObject keysForContent, String type) {
        // TODO Auto-generated method stub
        String key1 = "";
        String key2 = "";
        String key3 = "";
        String key4 = "";
        String value1 = "";
        String value2 = "";
        String value3 = "";
        String value4 = "";
        HorizontalListBuilder horizontalList = cmp.horizontalList();
        VerticalListBuilder verticalList1 = cmp.verticalList();
        horizontalList.add(cmp.text(no).setStyle(borderedStyle).setWidth(10));
        if("POI".equalsIgnoreCase(type)) {
            key1 = keysForContent.getString("poi");
        }else if("POA".equalsIgnoreCase(type)) {
            key1 = keysForContent.getString("poa");
        }else if("DOBDOI".equalsIgnoreCase(type)) {
            key1 = keysForContent.getString("dobdoi");
        }
        key2 = keysForContent.getString("din");
        key3 = keysForContent.getString("authorityName");
        key4 = keysForContent.getString("authorityAddress");
        verticalList1.add(createTwoHorizontalListKeyValue(key1,
                keysForContent.getString("poiVal")));
        verticalList1.add(createTwoHorizontalListKeyValue(key2,
                apptKycNo));
        verticalList1.add(createTwoHorizontalListKeyValue(key3,
                keysForContent.getString("authorityNameVal")));
        verticalList1.add(createTwoHorizontalListKeyValue(key4,
                keysForContent.getString("authorityAddressVal")));
        horizontalList.add(verticalList1);
        return horizontalList;
    }

    private ComponentBuilder<?, ?> getTranDetails(JSONObject keysForContent) {
        // TODO Auto-generated method stub
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(createThreeHorizontalList("1", keysForContent.getString("tranDate"),
                sactionedDate));
        verticalList.add(createThreeHorizontalList("2", keysForContent.getString("tranTotalAmt"),
                sanctionedAmount));
        verticalList.add(createThreeHorizontalList("3", keysForContent.getString("tranAmt"),
                ""));
        verticalList.add(createThreeHorizontalList("4", keysForContent.getString("tranMode"),
                keysForContent.getString("tranModeVal")));
        verticalList.add(getTranShares("5", keysForContent));
        //verticalList.add(getTranShares(keysForContent));

        return verticalList;
    }

    private ComponentBuilder<?, ?> getTranShares(String no, JSONObject keysForContent) {
        // TODO Auto-generated method stub
        HorizontalListBuilder horizontalList = cmp.horizontalList();
        VerticalListBuilder verticalList1 = cmp.verticalList();
        horizontalList.add(cmp.text(no).setStyle(borderedStyle).setWidth(10));
        verticalList1.add(cmp.horizontalList().add(cmp.text(keysForContent.getString("tranJointHeader")).setStyle(borderedStyle)));
        verticalList1.add(createThreeHorizontalList("(i)", keysForContent.getString("tranNoOfPerson"),
                keysForContent.getString("tranNoOfPersonVal")));
        verticalList1.add(createThreeHorizontalList("(ii)", keysForContent.getString("tranShare"),
                keysForContent.getString("tranShareVal")));
        horizontalList.add(verticalList1);
        return horizontalList;
    }

    private ComponentBuilder<?, ?> getPANAckdetails(String no, JSONObject keysForContent) {
        // TODO Auto-generated method stub
        HorizontalListBuilder horizontalList = cmp.horizontalList();
        VerticalListBuilder verticalList1 = cmp.verticalList();
        horizontalList.add(cmp.text(no).setStyle(borderedStyle).setWidth(10));
        verticalList1.add(cmp.horizontalList().add(cmp.text(keysForContent.getString("panAckHeader")).setStyle(borderedStyle)));
        verticalList1.add(createThreeHorizontalList("(i)", keysForContent.getString("panAckDate"),
                ""));
        verticalList1.add(createThreeHorizontalList("(ii)", keysForContent.getString("panAckNo"),
                ""));
        horizontalList.add(verticalList1);
        return horizontalList;
    }

    private ComponentBuilder<?, ?> getIncomeDetails(String no, JSONObject keysForContent) {
        // TODO Auto-generated method stub
        HorizontalListBuilder horizontalList = cmp.horizontalList();
        VerticalListBuilder verticalList1 = cmp.verticalList();
        horizontalList.add(cmp.text(no).setStyle(borderedStyle).setWidth(10));
        verticalList1.add(cmp.horizontalList().add(cmp.text(keysForContent.getString("incomeDetails")).setStyle(borderedStyle)));
        verticalList1.add(createThreeHorizontalList("(i)", keysForContent.getString("chargeableIncome"),
                ""));
        verticalList1.add(createThreeHorizontalList("(ii)", keysForContent.getString("nonChargeableIncome"),
                ""));
        horizontalList.add(verticalList1);
        return horizontalList;
    }


    private ComponentBuilder<?, ?> getDeclaration(JSONObject keysForContent, CustomerDataFields custmrDataFields) {
        // TODO Auto-generated method stub
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(cmp.text(keysForContent.getString("declaration")).setMarkup(Markup.HTML));
        verticalList.add(getDeclarations(keysForContent));
        verticalList.add(
                cmp.text(keysForContent.getString("place") + " " + custmrDataFields.getApplicationMaster().getBranchName())
        );
        verticalList.add(cmp.text(keysForContent.getString("date") + " " + sactionedDate).setMarkup(Markup.HTML));
        return verticalList;
    }

    private ComponentBuilder<?, ?> getDeclarations(JSONObject keysForContent) {
        // TODO Auto-generated method stub
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(createTwoHorizontalList("(i)", keysForContent.getString("declaration1")));
        verticalList.add(createTwoHorizontalList("(ii)", keysForContent.getString("declaration2")));
        return verticalList;
    }

    private ComponentBuilder<?, ?> getVerification(JSONObject keysForContent) {
        // TODO Auto-generated method stub
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(getVerifications(keysForContent));
        // verticalList.add(
        //         cmp.horizontalList()
        //                 .add(cmp.text(keysForContent.getString("place")))
        //                 .add(cmp.horizontalGap(5))
        //                 .add(cmp.text(place))
        // );
        verticalList.add(cmp.text(keysForContent.getString("place") + place));
        verticalList.add(cmp.text(keysForContent.getString("date") + " " + sactionedDate).setMarkup(Markup.HTML));
        verticalList.add(cmp.text(keysForContent.getString("declarantSign") )
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
        verticalList.add(
                cmp.text(keysForContent.getString("name") + applicantName).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
//        verticalList.add(cmp.text(keysForContent.getString("designation"))
//                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER));
        return verticalList;
    }

    private ComponentBuilder<?, ?> getVerifications(JSONObject keysForContent) {
        // TODO Auto-generated method stub
        VerticalListBuilder verticalList = cmp.verticalList();
        String verification1 = keysForContent.getString("verification1")
                .replace("{CUSTOMER_NAME}", "<u><b>" + applicantName + "</b></u>");

        verticalList.add(
                createTwoHorizontalList("1.", verification1));

        String verification2 = keysForContent.getString("verification2")
                .replace("{CUSTOMER_NAME}", "<u><b>" + applicantName + "</b></u>");

        verticalList.add(
                createTwoHorizontalList("2.", verification2));
        verticalList.add(createTwoHorizontalList("3.", keysForContent.getString("verification3")));
        return verticalList;
    }

    private ComponentBuilder<?, ?> getNatureOfTran(JSONObject keysForContent) {
        // TODO Auto-generated method stub
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(createThreeHorizontalList("1", keysForContent.getString("NatureOfTran"),
                keysForContent.getString("NatureOfTranVal")));
        return verticalList;
    }

    private ComponentBuilder<?, ?> createTwoHorizontalList(String no, String key) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(no).setWidth(5));
        horizontalList.add(cmp.text(key).setMarkup(Markup.HTML));
        return horizontalList;
    }
    private ComponentBuilder<?, ?> createTwoHorizontalListKeyValue(String no, String key) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(no).setStyle(borderedStyle));
        horizontalList.add(cmp.text(key).setStyle(borderedStyle));
        return horizontalList;
    }

    private ComponentBuilder<?, ?> createThreeHorizontalList(String no, String key, String value) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(no).setStyle(borderedStyle).setWidth(10));
        horizontalList.add(cmp.text(key).setStyle(borderedStyle));
        horizontalList.add(cmp.text(value).setStyle(borderedStyle));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createThreeHorizontalListContact(String no, String key, JSONObject keysForContent) {
        int width = 55;
        HorizontalListBuilder horizontalList = cmp.horizontalList();
        HorizontalListBuilder h1 = cmp.horizontalList();
        HorizontalListBuilder h2 = cmp.horizontalList();
        VerticalListBuilder v1 = cmp.verticalList();

        horizontalList.add(cmp.text(no).setStyle(borderedStyle).setWidth(10));
        horizontalList.add(cmp.text(key).setStyle(borderedStyle));
        //horizontalList.add(two.setStyle(borderedStyle));
        h1.add(cmp.text(keysForContent.getString("countryCode")).setStyle(borderedStyle).setWidth(width));
        h1.add(cmp.text(keysForContent.getString("number")).setStyle(borderedStyle).setWidth(width));
        v1.add(h1);

        h2.add(cmp.text(Constants.IND_MOBILE_CODE).setStyle(borderedStyle).setWidth(width));
        h2.add(cmp.text(appMobNo).setStyle(borderedStyle).setWidth(width));
        v1.add(h2);

        //v1.add(cmp.text("").setStyle(borderedStyle));
        horizontalList.add(v1);

        return horizontalList;
    }

    private ComponentBuilder<?, ?> getBasicDemographicDetails(JSONObject keysForContent, CustomerDataFields req,
                                                              CustomerDetailsPayload applicantPayload, CustomerDetailsPayload coapplicantPayload) {
        VerticalListBuilder verticalList = cmp.verticalList();

        String appMobNo = "";
        String coappMobNo = "";
        String applicantName = "";
        String coApplicantName = "";

        try {
            for (CustomerDetails customer : req.getCustomerDetailsList()) {
                if (customer.getCustomerType().equalsIgnoreCase("Applicant")) {
                    appMobNo = customer.getMobileNumber();
                    applicantName = customer.getCustomerName();
                } else if (customer.getCustomerType().equalsIgnoreCase("Co-App")) {
                    coappMobNo = customer.getMobileNumber();
                    coApplicantName = customer.getCustomerName();
                }
            }

            String apptKycNo = "NA";
            if (applicantPayload != null) {
                boolean isPrimaryVerified = Constants.VERIFIED_STS
                        .equalsIgnoreCase(applicantPayload.getPrimaryKycIdValStatus());
                boolean isAlternateVerified = Constants.VERIFIED_STS
                        .equalsIgnoreCase(applicantPayload.getAlternateVoterIdValStatus());

                if (isPrimaryVerified && isAlternateVerified) {
                    apptKycNo = applicantPayload.getAlternateVoterId();
                } else if (isAlternateVerified) {
                    apptKycNo = applicantPayload.getAlternateVoterId();
                } else if (isPrimaryVerified) {
                    apptKycNo = applicantPayload.getPrimaryKycId();
                }
            }

            String coApptKycNo = "NA";
            if (coapplicantPayload != null) {
                boolean isPrimaryVerified = Constants.VERIFIED_STS
                        .equalsIgnoreCase(coapplicantPayload.getPrimaryKycIdValStatus());
                boolean isAlternateVerified = Constants.VERIFIED_STS
                        .equalsIgnoreCase(coapplicantPayload.getAlternateVoterIdValStatus());

                if (isPrimaryVerified && isAlternateVerified) {
                    coApptKycNo = coapplicantPayload.getAlternateVoterId();
                } else if (isAlternateVerified) {
                    coApptKycNo = coapplicantPayload.getAlternateVoterId();
                } else if (isPrimaryVerified) {
                    coApptKycNo = coapplicantPayload.getPrimaryKycId();
                }
            }

            verticalList.add(createThreeHorizontalListWithCustomisedWidth(
                    keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString("applicant"),
                    keysForContent.getString("coApplicant"), boldTextWithBorder));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("nameAsPerSystem"),
                    applicantName, coApplicantName, borderedStyle));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("nameAsPerKyc"),
                    applicantPayload.getNamePerKyc(), coapplicantPayload.getNamePerKyc(), borderedStyle));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("primaryKyc"),
                    CommonUtils.getDefaultValue(apptKycNo), CommonUtils.getDefaultValue(coApptKycNo), borderedStyle));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("dateOfBirth"),
                    CommonUtils.dateFormat3(applicantPayload.getDob()),
                    CommonUtils.dateFormat3(coapplicantPayload.getDob()), borderedStyle));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("memberGender"),
                    applicantPayload.getGender(), coapplicantPayload.getGender(), borderedStyle));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("mobileNumber"),
                    appMobNo, coappMobNo, borderedStyle));
            verticalList
                    .add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("memberMarritalStstus"),
                            applicantPayload.getMaritalStatus(), coapplicantPayload.getMaritalStatus(), borderedStyle));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("memberOccupation"),
                    applicantPayload.getOccupation(), coapplicantPayload.getOccupation(), borderedStyle));
            verticalList
                    .add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("relationshipToApplicant"),
                            coapplicantPayload.getRelationShipWithApplicant(), 26, 74, borderedStyle));
        } catch (JSONException e) {
            logger.error("error - getBasicDemographicDetails");
            logger.error(e.getMessage());
        }

        return verticalList;
    }

    private ComponentBuilder<?, ?> getResidenceDetails(JSONObject keysForContent, CustomerDataFields req) {
        VerticalListBuilder verticalList = cmp.verticalList();
        try {
            JSONObject addressDetailsObj = getAllAddressDeatils(req);

            String presentCityYrs = addressDetailsObj.getString(Constants.PRESNT_CITY_IN_YEARS);
            presentCityYrs = presentCityYrs.endsWith("Years") ? presentCityYrs : presentCityYrs + "Years";
            logger.debug("presentCityYrs :" + presentCityYrs);

            verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString(Constants.FIELD_NAME),
                    keysForContent.getString(Constants.FIELD_VALUE), 40, 60, boldTextWithBorder));
            verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("presentAddress"),
                    addressDetailsObj.getString("presentAddressApplicant"), 40, 60, borderedStyle));
            verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("permanentAddress"),
                    addressDetailsObj.getString("permanetAddressApplicant"), 40, 60, borderedStyle));
            verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("residenceOwnership"),
                    addressDetailsObj.getString("presentResidenceOwnership"), 40, 60, borderedStyle));
            verticalList.add(createTwoHorizontalListWithCustomisedWidth(
                    keysForContent.getString(Constants.PRESENT_ADDRESS_IN_YEARS),
                    addressDetailsObj.getString(Constants.PRESENT_ADDRESS_IN_YEARS), 40, 60, borderedStyle));
            verticalList.add(createTwoHorizontalListWithCustomisedWidth(
                    keysForContent.getString(Constants.PRESNT_CITY_IN_YEARS), presentCityYrs, 40, 60, borderedStyle));
        } catch (JSONException e) {
            logger.error("error - getResidenceDetails");
            logger.error(e.getMessage());
        }

        return verticalList;
    }

    // Credit Bureau Details
    private ComponentBuilder<?, ?> getCreditBureauDetails(JSONObject keysForContent, CustomerDataFields req,
                                                          boolean applicant) {
        VerticalListBuilder verticalList = cmp.verticalList();
        Gson gsonObj = new Gson();
        String appOverdueAmt = "";
        String appWriteOffAmt = "";
        String appCbScore = "";
        String appTotIndebtness = "";
        String appFoir = "";
        String coappOverdueAmt = "";
        String coappWriteOffAmt = "";
        String coappCbScore = "";
        String coappTotIndebtness = "";
        String coappFoir = "";

        try {
            for (CibilDetailsWrapper cibilDetailsWrapper : req.getCibilDetailsWrapperList()) {
                String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
//				String customerType = applicant ? applicantCustId : coApplicantCustId;
                CibilDetailsPayload payload = gsonObj.fromJson(cibilDetailsWrapper.getCibilDetails().getPayloadColumn(),
                        CibilDetailsPayload.class);
                logger.debug("CreditDetailsPayload Payload : " + payload);
                if (custId.equals(applicantCustId)) {
                    appOverdueAmt = payload.getOverdueAmt();
                    appWriteOffAmt = payload.getWriteOffAmt();
                    appCbScore = payload.getCbScore();
//					appTotIndebtness = payload.getTotIndebtness();
                    appFoir = payload.getFoirPercentage();
                    appTotIndebtness = extractValue(payload.getIndividualIndebtness());
                } else {
                    coappOverdueAmt = payload.getOverdueAmt();
                    coappWriteOffAmt = payload.getWriteOffAmt();
                    coappCbScore = payload.getCbScore();
//					appTotIndebtness = payload.getAppIndebtednessLimit();
//					coappTotIndebtness = payload.getCoappIndebtednessLimit();
                    coappTotIndebtness = extractValue(payload.getIndividualIndebtness());
                    coappFoir = payload.getFoirPercentage();
                    interestRate = payload.getRoi();
                }
            }
            String appNetIncome = String
                    .valueOf(req.getBcmpiIncomeDetails().getBcmpiIncomeDetailsWrapper().getApplicantTotalIncome());
            String coAppNetIncome = String
                    .valueOf(req.getBcmpiIncomeDetails().getBcmpiIncomeDetailsWrapper().getCoApplicantTotalIncome());
            verticalList.add(createThreeHorizontalListWithCustomisedWidth(
                    keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString("applicant"),
                    keysForContent.getString("coApplicant"), boldTextWithBorder));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("overdueAmount"),
                    appOverdueAmt, coappOverdueAmt, borderedStyle));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("writtenOffAmount"),
                    appWriteOffAmt, coappWriteOffAmt, borderedStyle));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("cbScore"),
                    appCbScore, coappCbScore, borderedStyle));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("netIncome"),
                    CommonUtils.amountFormat(appNetIncome), CommonUtils.amountFormat(coAppNetIncome), borderedStyle));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("unsecuredDebt"),
                    CommonUtils.amountFormat(appTotIndebtness), CommonUtils.amountFormat(coappTotIndebtness),
                    borderedStyle));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth(keysForContent.getString("foir"),
                    appFoir + "%", coappFoir + "%", borderedStyle));
        } catch (Exception e) {
            logger.error("error - getCreditBureauDetails");
            logger.error(e.getMessage());
        }
        logger.debug("getCreditBureauDetails added");
        return verticalList;
    }

    private ComponentBuilder<?, ?> getLoanObligationsDetails(JSONObject keysForContent,
                                                             LoanObligationsNestedClass loanObligationsNestedClass) {

        VerticalListBuilder verticalList = cmp.verticalList();
        Gson gson = new Gson();
        verticalList.add(createTwoHorizontalList(keysForContent.getString(Constants.FIELD_NAME),
                keysForContent.getString(Constants.FIELD_VALUE), boldTextWithBorder));

        try {
            verticalList.add(createTwoHorizontalList(keysForContent.getString("otherLoanObligations"),
                    CommonUtils.formatAmount(Double.parseDouble(loanObligationsNestedClass.getOtherLoanObligation())),
                    borderedStyle));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("monthlyEmi"),
                    CommonUtils.formatAmount(Double.parseDouble(loanObligationsNestedClass.getMonthlyEMI())),
                    borderedStyle));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("remainingTenure"),
                    loanObligationsNestedClass.getTenure().split("\\.")[0] + " months", borderedStyle));

        } catch (Exception e) {
            logger.error("Error - getLoanObligationsDetails", e);
            logger.error(e.getMessage());
        }
        return verticalList;
    }

    private ComponentBuilder<?, ?> getOtherDetails(JSONObject keysForContent, CustomerDataFields customerFields) {

        VerticalListBuilder verticalList = cmp.verticalList();
        try {
            BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = customerFields.getBcmpiOtherDetails()
                    .getBcmpiOtherDetailsWrapper();
            logger.debug("bcmpiOtherDetailsWrapper-->" + bcmpiOtherDetailsWrapper);
            String applicantUsesSmartPhone = Integer.parseInt(bcmpiOtherDetailsWrapper.getSmartphonesOwned()) > 0
                    ? "Yes"
                    : "No";

            verticalList.add(createTwoHorizontalList(keysForContent.getString(Constants.FIELD_NAME),
                    keysForContent.getString(Constants.FIELD_VALUE), boldTextWithBorder));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("houseOwnership"),
                    bcmpiOtherDetailsWrapper.getHouseOwnership(), borderedStyle));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("typeOfHouse"),
                    bcmpiOtherDetailsWrapper.getTypeOfHouse(), borderedStyle));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("typeOfRoof"),
                    bcmpiOtherDetailsWrapper.getTypeOfRoof(), borderedStyle));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("numberOfRooms"),
                    bcmpiOtherDetailsWrapper.getNoOfRoomsInHouse(), borderedStyle));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("modeOfSavings"),
                    bcmpiOtherDetailsWrapper.getModeOfSavings(), borderedStyle));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("basicAmenities"),
                    bcmpiOtherDetailsWrapper.getBasicAmenities().toString(), borderedStyle));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("otherAssets"),
                    bcmpiOtherDetailsWrapper.getOtherAssets().toString(), borderedStyle));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("applicantUsesSmartphone"),
                    applicantUsesSmartPhone, borderedStyle));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("numberOfSmartphones"),
                    bcmpiOtherDetailsWrapper.getSmartphonesOwned(), borderedStyle));
            /*
             * verticalList.add(createTwoHorizontalList(keysForContent.getString(
             * "hasAlmirahOrDressingTable"), bcmpiOtherDetailsWrapper.getAlmirah(),
             * borderedStyle));
             * verticalList.add(createTwoHorizontalList(keysForContent.getString(
             * "hasFurniture"), bcmpiOtherDetailsWrapper.getChair(), borderedStyle));
             */
        } catch (Exception e) {
            logger.error("error - getOtherDetails", e);
            logger.error(e.getMessage());
        }

        return verticalList;
    }

    private ComponentBuilder<?, ?> getOtherExpenseDetails(JSONObject keysForContent,
                                                          CustomerDataFields customerFields) {

        VerticalListBuilder verticalList = cmp.verticalList();
        Gson gsonObj = new Gson();

        try {
//			BCMPIOtherDetailsWrapper payload = gsonObj.fromJson(customerFields.getBcmpiOtherDetails().getPayload(), BCMPIOtherDetailsWrapper.class);

//			BCMPIOtherDetails payload = gsonObj.fromJson(customerFields.getBcmpiOtherDetails().getPayload(), BCMPIOtherDetails.class);
            BCMPIOtherDetailsWrapper payload = customerFields.getBcmpiOtherDetails().getBcmpiOtherDetailsWrapper();
            logger.debug("Other Details Wrapper " + payload);
//			BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = payload.getBcmpiOtherDetailsWrapper();
            verticalList.add(createTwoHorizontalList(keysForContent.getString(Constants.FIELD_NAME),
                    keysForContent.getString(Constants.FIELD_VALUE), boldTextWithBorder));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("householdExpenses"),
                    payload.getHouseholdExpenses(), borderedStyle));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("educationExpenses"),
                    payload.getEducationExpense(), borderedStyle));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("medicalExpenses"),
                    payload.getMedicalExpense(), borderedStyle));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("foodExpenses"), payload.getFoodExpense(),
                    borderedStyle));
            verticalList.add(createTwoHorizontalList(keysForContent.getString("clothingExpenses"),
                    payload.getExpenseOnClothing(), borderedStyle));
        } catch (Exception ex) {
            logger.error("error - getOtherExpenseDetails", ex);
            logger.error(ex.getMessage());
        }

        return verticalList;
    }

    private ComponentBuilder<?, ?> getLandDetails(JSONObject keysForContent, CustomerDataFields customerFields) {

        VerticalListBuilder verticalList = cmp.verticalList();
        Gson gsonObj = new Gson();

        try {
//			BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = gsonObj.fromJson(customerFields.getBcmpiOtherDetails().getPayload(), BCMPIOtherDetailsWrapper.class);

//			BCMPIOtherDetails payload = gsonObj.fromJson(customerFields.getBcmpiOtherDetails().getPayload(), BCMPIOtherDetails.class);
//			BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = payload.getBcmpiOtherDetailsWrapper();
            BCMPIOtherDetailsWrapper bcmpiOtherDetailsWrapper = customerFields.getBcmpiOtherDetails()
                    .getBcmpiOtherDetailsWrapper();

            String landOwnerName = "NA";
            String relationshipWithApplicant = "NA";
            String agriland = "0";
            if (bcmpiOtherDetailsWrapper != null) {
                if (bcmpiOtherDetailsWrapper.getAgriland() != null
                        && !"0".equals(bcmpiOtherDetailsWrapper.getAgriland())) {
                    agriland = bcmpiOtherDetailsWrapper.getAgriland();
                    landOwnerName = bcmpiOtherDetailsWrapper.getLandOwnerName();
                    relationshipWithApplicant = bcmpiOtherDetailsWrapper.getRelationshipWithApplicant();
                }
            }

            verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString(Constants.FIELD_NAME),
                    keysForContent.getString(Constants.FIELD_VALUE), 60, 40, boldTextWithBorder));
            verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("agriLandHoldings"),
                    agriland, 60, 40, borderedStyle));
            verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("landOwnerName"),
                    landOwnerName, 60, 40, borderedStyle));
            verticalList.add(
                    createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("relationshipWithApplicant"),
                            relationshipWithApplicant, 60, 40, borderedStyle));

        } catch (Exception ex) {
            logger.error("error - getLandDetails", ex);
            logger.error(ex.getMessage());
        }

        return verticalList;
    }

    private ComponentBuilder<?, ?> getFinalIncomeDetails(JSONObject keysForContent, CustomerDataFields customerFields) {

        VerticalListBuilder verticalList = cmp.verticalList();
        BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper = customerFields.getBcmpiIncomeDetails()
                .getBcmpiIncomeDetailsWrapper();
        logger.debug("bcmpiIncomeDetailsWrapper ::{}", bcmpiIncomeDetailsWrapper);
        List<BCMPIIncomeDetailsWrapper.Dairy> dairyList = bcmpiIncomeDetailsWrapper.getBusiness().getDairy();
        List<BCMPIIncomeDetailsWrapper.Kirana> kiranaList = bcmpiIncomeDetailsWrapper.getBusiness().getKirana();
        List<BCMPIIncomeDetailsWrapper.Tailoring> tailoringList = bcmpiIncomeDetailsWrapper.getBusiness()
                .getTailoring();
        List<BCMPIIncomeDetailsWrapper.OtherBusiness> otherList = bcmpiIncomeDetailsWrapper.getBusiness().getOther();
        BCMPIIncomeDetailsWrapper.Wage wage = bcmpiIncomeDetailsWrapper.getWage();
        BCMPIIncomeDetailsWrapper.Agriculture agri = bcmpiIncomeDetailsWrapper.getAgriculture();
        BCMPIIncomeDetailsWrapper.Salary salary = bcmpiIncomeDetailsWrapper.getSalary();
        BCMPIIncomeDetailsWrapper.Pension pension = bcmpiIncomeDetailsWrapper.getPension();
        BCMPIIncomeDetailsWrapper.RentalIncome rental = bcmpiIncomeDetailsWrapper.getRentalIncome();
        String wageCustType = "";
        BigDecimal wageIncome = BigDecimal.ZERO;
        String agriCustType = "";
        BigDecimal agriIncome = BigDecimal.ZERO;
        String salaryCustType = "";
        BigDecimal salaryIncome = BigDecimal.ZERO;
        String pensionCustType = "";
        BigDecimal pensionIncome = BigDecimal.ZERO;
        String rentalCustType = "";
        BigDecimal rentalIncome = BigDecimal.ZERO;
        String dairyOwnedBy = "";
        int dairyNetIncome = 0;
        if (dairyList.size() > 0) {
            boolean hasApplicant = false;
            boolean hasCoApplicant = false;
            for (BCMPIIncomeDetailsWrapper.Dairy dairy : dairyList) {
                if (Constants.APPLICANT.equalsIgnoreCase(dairy.getDairyType())) {
                    hasApplicant = true;
                } else if (Constants.CO_APPLICANT.equalsIgnoreCase(dairy.getDairyType())) {
                    hasCoApplicant = true;
                }
                dairyOwnedBy = dairy.getDairyType();
                dairyNetIncome += dairy.getIncomeAssessmentChecknetBusinessIncome().intValue();
            }
            if (hasApplicant && hasCoApplicant)
                dairyOwnedBy = Constants.BOTH;
        }
        String kiranaOwnedBy = "";
        int kiranaNetIncome = 0;
        if (kiranaList.size() > 0) {
            boolean hasApplicant = false;
            boolean hasCoApplicant = false;
            for (BCMPIIncomeDetailsWrapper.Kirana kirana : kiranaList) {
                if (Constants.APPLICANT.equalsIgnoreCase(kirana.getKiranaType())) {
                    hasApplicant = true;
                } else if (Constants.CO_APPLICANT.equalsIgnoreCase(kirana.getKiranaType())) {
                    hasCoApplicant = true;
                }
                kiranaOwnedBy = kirana.getKiranaType();
                kiranaNetIncome += kirana.getFinalNetIncome().intValue();
            }
            if (hasApplicant && hasCoApplicant)
                kiranaOwnedBy = Constants.BOTH;
        }
        String tailoringOwnedBy = "";
        int tailoringNetIncome = 0;
        if (tailoringList.size() > 0) {
            boolean hasApplicant = false;
            boolean hasCoApplicant = false;
            for (BCMPIIncomeDetailsWrapper.Tailoring tailoring : tailoringList) {
                if (Constants.APPLICANT.equalsIgnoreCase(tailoring.getTailoringType())) {
                    hasApplicant = true;
                } else if (Constants.CO_APPLICANT.equalsIgnoreCase(tailoring.getTailoringType())) {
                    hasCoApplicant = true;
                }
                tailoringOwnedBy = tailoring.getTailoringType();
                tailoringNetIncome += tailoring.getNetBusinessIncome().intValue();
            }
            if (hasApplicant && hasCoApplicant)
                tailoringOwnedBy = Constants.BOTH;
        }
        String otherOwnedBy = "";
        int otherNetIncome = 0;
        if (otherList.size() > 0) {
            boolean hasApplicant = false;
            boolean hasCoApplicant = false;
            for (BCMPIIncomeDetailsWrapper.OtherBusiness other : otherList) {
                if (Constants.APPLICANT.equalsIgnoreCase(other.getOtherType())) {
                    hasApplicant = true;
                } else if (Constants.CO_APPLICANT.equalsIgnoreCase(other.getOtherType())) {
                    hasCoApplicant = true;
                }
                otherOwnedBy = other.getOtherType();
                otherNetIncome += other.getNetBusinessIncome().intValue();
            }
            if (hasApplicant && hasCoApplicant)
                otherOwnedBy = Constants.BOTH;
        }
        if (wage != null) {
            boolean hasApplicant = wage.getApplicant() != null;
            boolean hasCoApplicant = wage.getCoApplicant() != null;
            if (hasApplicant && hasCoApplicant) {
                wageIncome = BCMPIIncomeDetailsWrapper.calculateWageIncome(wage, Constants.APPLICANT)
                        .add(BCMPIIncomeDetailsWrapper.calculateWageIncome(wage, Constants.CO_APPLICANT));
                wageCustType = Constants.BOTH;
            } else if (hasApplicant) {
                wageIncome = BCMPIIncomeDetailsWrapper.calculateWageIncome(wage, Constants.APPLICANT);
                wageCustType = Constants.APPLICANT;
            } else if (hasCoApplicant) {
                wageIncome = BCMPIIncomeDetailsWrapper.calculateWageIncome(wage, Constants.CO_APPLICANT);
                wageCustType = Constants.CO_APPLICANT;
            }
        }
        if (agri != null) {
            boolean hasApplicant = agri.getApplicant() != null;
            boolean hasCoApplicant = agri.getCoApplicant() != null;
            if (hasApplicant && hasCoApplicant) {
                agriIncome = new BigDecimal(agri.getApplicant().getConsideredIncome())
                        .add(new BigDecimal(agri.getCoApplicant().getConsideredIncome()));
                agriCustType = Constants.BOTH;
            } else if (hasApplicant) {
                agriIncome = new BigDecimal(agri.getApplicant().getConsideredIncome());
                agriCustType = Constants.APPLICANT;
            } else if (hasCoApplicant) {
                agriIncome = new BigDecimal(agri.getCoApplicant().getConsideredIncome());
                agriCustType = Constants.CO_APPLICANT;
            }
        }
        if (salary != null) {
            boolean hasApplicant = salary.getApplicant() != null;
            boolean hasCoApplicant = salary.getCoApplicant() != null;
            if (hasApplicant && hasCoApplicant) {
                salaryIncome = BigDecimal.valueOf(Integer.parseInt(salary.getApplicant().getNetSalary())
                        + Integer.parseInt(salary.getCoApplicant().getNetSalary()));
                salaryCustType = Constants.BOTH;
            } else if (hasApplicant) {
                salaryIncome = new BigDecimal(salary.getApplicant().getNetSalary());
                salaryCustType = Constants.APPLICANT;
            } else if (hasCoApplicant) {
                salaryIncome = new BigDecimal(Integer.parseInt(salary.getCoApplicant().getNetSalary()));
                salaryCustType = Constants.CO_APPLICANT;
            }
        }
        if (pension != null) {
            boolean hasApplicant = pension.getApplicant() != null;
            boolean hasCoApplicant = pension.getCoApplicant() != null;
            if (hasApplicant && hasCoApplicant) {
                pensionIncome = new BigDecimal(pension.getApplicant().getApplicantPensionIncome())
                        .add(new BigDecimal(pension.getCoApplicant().getCoApplicantPensionIncome()));
                pensionCustType = Constants.BOTH;
            } else if (hasApplicant) {
                pensionIncome = new BigDecimal(pension.getApplicant().getApplicantPensionIncome());
                pensionCustType = Constants.APPLICANT;
            } else if (hasCoApplicant) {
                pensionIncome = new BigDecimal(pension.getCoApplicant().getCoApplicantPensionIncome());
                pensionCustType = Constants.CO_APPLICANT;
            }
        }
        if (rental != null) {
            boolean hasApplicant = rental.getApplicant() != null;
            boolean hasCoApplicant = rental.getCoApplicant() != null;
            if (hasApplicant && hasCoApplicant) {
                rentalIncome = (rental.getApplicant().getConsideredMonthlyIncome()
                        .add(rental.getCoApplicant().getConsideredMonthlyIncome()));
                rentalCustType = Constants.BOTH;
            } else if (hasApplicant) {
                rentalIncome = rental.getApplicant().getConsideredMonthlyIncome();
                rentalCustType = Constants.APPLICANT;
            } else if (hasCoApplicant) {
                rentalIncome = rental.getCoApplicant().getConsideredMonthlyIncome();
                rentalCustType = Constants.CO_APPLICANT;
            }
        }
        try {
            verticalList.add(createThreeHorizontalListWithCustomisedWidth2Bold(keysForContent.getString("incomeSource"),
                    keysForContent.getString("ownedBy"), keysForContent.getString("netIncome")));
            verticalList.add(createSingleHorizontalList(keysForContent.getString("businessIncome"))
                    .setStyle(stl.style().setBackgroundColor(new Color(204, 229, 255))));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("businessDairy"),
                    dairyOwnedBy, CommonUtils.amountFormat(String.valueOf(dairyNetIncome))));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("businessKirana"),
                    kiranaOwnedBy, CommonUtils.amountFormat(String.valueOf(kiranaNetIncome))));
            verticalList
                    .add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("businessTailoring"),
                            tailoringOwnedBy, CommonUtils.amountFormat(String.valueOf(tailoringNetIncome))));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("businessOther"),
                    otherOwnedBy, CommonUtils.amountFormat(String.valueOf(otherNetIncome))));
            verticalList.add(createSingleHorizontalList(keysForContent.getString("otherIncome"))
                    .setStyle(stl.style().setBackgroundColor(new Color(204, 255, 204))));

            verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("agriculture"),
                    agriCustType, CommonUtils.amountFormat(String.valueOf(agriIncome))));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("salary"),
                    salaryCustType, CommonUtils.amountFormat(String.valueOf(salaryIncome))));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("wage"),
                    wageCustType, CommonUtils.amountFormat(String.valueOf(wageIncome))));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("pension"),
                    pensionCustType, CommonUtils.amountFormat(String.valueOf(pensionIncome))));
            verticalList.add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("rental"),
                    rentalCustType, CommonUtils.amountFormat(String.valueOf(rentalIncome))));
            verticalList
                    .add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("totalHouseHoldIncome"),
                            CommonUtils
                                    .amountFormat(String.valueOf(bcmpiIncomeDetailsWrapper.getFieldAssessedIncome())),
                            70, 30, boldTextWithBorder)
                            .setStyle(stl.style().setBackgroundColor(new Color(255, 204, 255))));
        } catch (Exception ex) {
            logger.error("error - getFinalIncomeDetails");
            logger.error(ex.getMessage());
        }

        return verticalList;
    }

    private ComponentBuilder<?, ?> getInprincipalDecisionDetails(JSONObject keysForContent,
                                                                 CustomerDataFields customerFields) {

        VerticalListBuilder verticalList = cmp.verticalList();
        Gson gsonObj = new Gson();
        Gson gsonObj2 = new Gson();
        Gson gsonObj3 = new Gson();
        String cbDateStr = "";
        try {
            String bmLoanAmount = String
                    .valueOf(customerFields.getLoanDetails().getBmRecommendedLoanAmount().intValue());
            logger.debug("bmLoanAmount : " + bmLoanAmount);
//			String roi = String.valueOf(customerFields.getLoanDetails().getRoi());
            LoanDetailsPayload payload2 = gsonObj2.fromJson(customerFields.getLoanDetails().getPayloadColumn(),
                    LoanDetailsPayload.class);
            String outstandingAmount = String.valueOf(0);
            logger.debug("outstandingAmount : " + outstandingAmount);

            // after sanction need use Sanction Loan Amount instead of
            // BmRecommendedLoanAmount()
            BigDecimal sanctionAmtDb = ((null == customerFields.getLoanDetails().getSanctionedLoanAmount())
                    ? BigDecimal.ZERO
                    : customerFields.getLoanDetails().getSanctionedLoanAmount());

            int bmLoanAmountInt = Integer.parseInt(bmLoanAmount);
            try {
                BigDecimal bmLoan = new BigDecimal(bmLoanAmount.trim());
                if (sanctionAmtDb.compareTo(BigDecimal.ZERO) > 0 && sanctionAmtDb.compareTo(bmLoan) <= 0) {
                    logger.debug("sanction is less than or equal to bmLoanAmount");
//				        	sanction is less than or equal to bmLoanAmount
//				            bmLoanAmount = sanctionAmt; // Use the lesser amount
                    bmLoanAmountInt = sanctionAmtDb.intValue();
                    bmLoanAmount = sanctionAmtDb.toPlainString();
                }
            } catch (NumberFormatException e) {
                // Handle invalid bmLoanAmount string
                logger.warn("Invalid bmLoanAmount: {}", bmLoanAmount);
            }

            for (CibilDetailsWrapper cibilDetailsWrapper : customerFields.getCibilDetailsWrapperList()) {
                String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
//			String customerType = applicant ? applicantCustId : coApplicantCustId;
                CibilDetailsPayload payload = gsonObj.fromJson(cibilDetailsWrapper.getCibilDetails().getPayloadColumn(),
                        CibilDetailsPayload.class);
                logger.debug("CreditDetailsPayload Payload : " + payload);
                if (custId.equals(coApplicantCustId)) {
                    LocalDate cbDateDb = cibilDetailsWrapper.getCibilDetails().getCbDate();
                    cbDateStr = CommonUtils.getCurDateMinusOne(cbDateDb);

                    int totalInsurance = toFindSum(payload.getInsuranceChargeJoint(),
                            payload.getInsuranceChargeMember(), payload.getInsuranceChargeSpouse());
                    logger.debug("totalInsurance : " + totalInsurance);

                    int netOffAmt = toFindSum(outstandingAmount, payload.getStampDutyCharge(),
                            String.valueOf(totalInsurance), payload.getProcessingFees());
                    logger.debug("netOffAmt : " + netOffAmt);

                    int postNetOff = toFindDifference(payload.getEligibleAmt(), String.valueOf(netOffAmt)); // sanctined
                    // - netoff
                    logger.debug("postNetOff : " + String.valueOf(postNetOff));

//					int postNetOff2 = Integer.parseInt(bmLoanAmount) - netOffAmt;
                    int postNetOff2 = bmLoanAmountInt - netOffAmt;

                    verticalList.add(createThreeHorizontalListWithCustomisedWidth2Bold(
                            keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString("breResponse"),
                            keysForContent.getString("bmRecommended")));
                    verticalList.add(createThreeHorizontalListWithCustomisedWidth2(
                            keysForContent.getString("loanAmount"), CommonUtils.amountFormat(payload.getEligibleAmt()),
                            CommonUtils.amountFormat(bmLoanAmount == null ? "0" : bmLoanAmount)));
                    verticalList.add(
                            createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("rateOfInterest"),
                                    payload.getRoi() + "%", payload.getRoi() + "%"));
                    verticalList.add(
                            createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("processingFee"),
                                    CommonUtils.amountFormat(payload.getProcessingFees()),
                                    CommonUtils.amountFormat(payload.getProcessingFees())));
                    // InsuranceDetailsWrapper
                    verticalList.add(
                            createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("insurancePremium"),
                                    CommonUtils.amountFormat(String.valueOf(totalInsurance)),
                                    CommonUtils.amountFormat(String.valueOf(totalInsurance))));
                    verticalList.add(
                            createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("stampDutyCharges"),
                                    CommonUtils.amountFormat(payload.getStampDutyCharge()),
                                    CommonUtils.amountFormat(payload.getStampDutyCharge())));
                    // existingloanDetails
                    verticalList.add(createThreeHorizontalListWithCustomisedWidth2(
                            keysForContent.getString("currentCAGLOutstanding") + cbDateStr + ")",
                            CommonUtils.amountFormat(outstandingAmount), CommonUtils.amountFormat(outstandingAmount)));
                    verticalList
                            .add(createThreeHorizontalListWithCustomisedWidth2(keysForContent.getString("netOffAmount"),
                                    CommonUtils.amountFormat(String.valueOf(netOffAmt)),
                                    CommonUtils.amountFormat(String.valueOf(netOffAmt))));
                    verticalList.add(createThreeHorizontalListWithCustomisedWidth2(
                            keysForContent.getString("totalAmountPostNetOff"),
                            CommonUtils.amountFormat(String.valueOf(postNetOff)),
                            CommonUtils.amountFormat(String.valueOf(postNetOff2))));
                    verticalList.add(createThreeHorizontalListWithCustomisedWidth2(
                            keysForContent.getString("instalmentAmountFrequency"),
                            payload.getApprovedLoanEMI() + " / " + payload.getRepaymentFrequency(),
                            payload.getApprovedLoanEMI() + " / " + payload2.getFrequencyOfRepayment()));
                }
            }
        } catch (Exception e) {
            logger.error("error - getInprincipalDecisionDetails", e);
            logger.error(e.getMessage());
        }

        return verticalList;
    }

    private int toFindSum(String... value) {
        int totalInsurance = Stream.of(value).map(val -> {
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
                    presentAddressApplicant = getFullAddress(addr);
                    logger.debug("Present Address - Applicant : " + presentAddressApplicant);
                    presentResidenceOwnership = addr.getResidenceOwnership();
                    presentAddressYears = addr.getResidenceAddressSince();
                    presntCityYears = addr.getResidenceCitySince();
                    presentResidenceAddressProof = addr.getCurrentAddressProof();
                    presentResidenceType = addr.getHouseType();
                } else if (addr.getAddressType().equalsIgnoreCase("Permanent")) {
                    permanetAddressApplicant = getFullAddress(addr);
                    logger.debug("Permanent Address - Applicant :" + permanetAddressApplicant);
                }
            }

            // Co-Applicant Address
            if (coApplicantAddrPayLoadLst != null) {
                for (Address addr : coApplicantAddrPayLoadLst) {
                    if (addr.getAddressType().equalsIgnoreCase("present")) {
                        presentAddressCoApplicant = getFullAddress(addr);
                        logger.debug("Present Address - Co-applicant :" + presentAddressCoApplicant);
                        presentResidenceOwnershipCo = addr.getResidenceOwnership();
                        presentAddressYearsCo = addr.getResidenceAddressSince();
                        presntCityYearsCo = addr.getResidenceCitySince();
                        presentResidenceAddressProofCo = addr.getCurrentAddressProof();
                        presentResidenceTypeCo = addr.getHouseType();
                    } else if (addr.getAddressType().equalsIgnoreCase("Permanent")) {
                        permanetAddressCoApplicant = getFullAddress(addr);
                        logger.debug("permanent Address - Co-applicant. :" + permanetAddressCoApplicant);
                    }
                }
            }

            // Occupation Address
            Address ocupnAddr = (applicantOccupnAddrPayLoadLst == null || applicantOccupnAddrPayLoadLst.isEmpty() ? null : applicantOccupnAddrPayLoadLst.get(0));
            Address ocupnAddrCo = (coApplicantOccupnAddrPayLoadLst == null || coApplicantOccupnAddrPayLoadLst.isEmpty() ? null
                    : coApplicantOccupnAddrPayLoadLst.get(0));
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
            jsnObj.put(Constants.PRESENT_ADDRESS_IN_YEARS, presentAddressYears);
            jsnObj.put(Constants.PRESNT_CITY_IN_YEARS, presntCityYears);
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

            // NOTE: also expose "permanentAddress" key used by getIDDetails()
            jsnObj.put("permanentAddress", permanetAddressApplicant);

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

    // Deviation
    private ComponentBuilder<?, ?> getDeviationDetails(JSONObject keysForContent, CustomerDataFields req,
                                                       List<DeviationRATracker> deviationRecords) {

        VerticalListBuilder verticalList = cmp.verticalList();

        verticalList.add(createThreeHorizontalListWithCustomisedWidth2Bold(
                keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.FIELD_VALUE),
                keysForContent.getString("deviationAuthority")));
        int count = 1;
        try {
            logger.debug("ApplicationId " + req.getApplicationId());

            for (DeviationRATracker deviationRATracker : deviationRecords) {
                logger.debug("Checking recordType: " + deviationRATracker.getRecordType());
                if ("CA_DEVIATION".equalsIgnoreCase(deviationRATracker.getRecordType())) {
                    verticalList.add(createThreeHorizontalListWithCustomisedWidth2("DV " + count,
                            deviationRATracker.getRecordMsg(), deviationRATracker.getAuthority()));
                    count++;
                }
            }
            if (deviationRecords.isEmpty()) {
                verticalList.add(createThreeHorizontalListWithCustomisedWidth2("--", "--", "--"));
                logger.debug("Records is empty");
            }
        } catch (Exception e) {
            logger.error("Error in getDeviationDetails", e);
        }

        return verticalList;
    }

    // Deviation
    private ComponentBuilder<?, ?> getReassessmentDetails(JSONObject keysForContent, CustomerDataFields req,
                                                          List<DeviationRATracker> deviationRecords) {
        VerticalListBuilder verticalList = cmp.verticalList();
        try {
            verticalList.add(createThreeHorizontalListWithCustomisedWidth2Bold(
                    keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.FIELD_VALUE),
                    keysForContent.getString("reassessmentAuthority")));
            int count = 1;
            for (DeviationRATracker deviationRATracker : deviationRecords) {
                logger.debug("Checking recordType: " + deviationRATracker.getRecordType());
                if ("REASSESSMENT".equalsIgnoreCase(deviationRATracker.getRecordType())) {
                    verticalList.add(createThreeHorizontalListWithCustomisedWidth2("RA " + count,
                            deviationRATracker.getRecordMsg(), deviationRATracker.getAuthority()));
                    count++;
                }
            }
            if (deviationRecords.isEmpty()) {
                verticalList.add(createThreeHorizontalListWithCustomisedWidth2("--", "--", "--"));
            }
        } catch (Exception e) {
            logger.error("Error in getDeviationDetails", e);
        }
        return verticalList;
    }

    private ComponentBuilder<?, ?> getSanctionCondition(JSONObject keysForContent, CustomerDataFields req,
                                                        Optional<SanctionMaster> sanctionMaster) {
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(createTwoHorizontalList(keysForContent.getString("sanctionCondition"),
                keysForContent.getString("raisedBy"), boldTextWithBorder));
        try {
            if (sanctionMaster.isPresent()) {
                SanctionMaster sanction = sanctionMaster.get();
                Map<String, String> map = new HashMap<>();
                map.put("AM", sanction.getAm());
                map.put("BM", sanction.getBm());
                map.put("RM", sanction.getRm());
                map.put("DM", sanction.getDm());

                String sanctioner = map.entrySet().stream().filter(entry -> "Y".equals(entry.getValue()))
                        .map(Map.Entry::getKey).collect(Collectors.joining("/"));
                verticalList.add(createTwoHorizontalList(sanctioner, "--", borderedStyle));
            } else {
                verticalList.add(createTwoHorizontalList("--", "--", borderedStyle));
            }
        } catch (Exception e) {
            logger.error("GetSanctionConditon ", e);
        }

        return verticalList;
    }

    private ComponentBuilder<?, ?> getOfficerDetails(JSONObject keysForContent, CustomerDataFields customerFields,
                                                     String gkUserId, String gkUserName) {
        VerticalListBuilder verticalList = cmp.verticalList();

        verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("recommendingOfficerName"),
                gkUserName, 40, 60, borderedStyle));
        verticalList.add(createTwoHorizontalListWithCustomisedWidth(keysForContent.getString("gkId"), gkUserId, 40, 60,
                borderedStyle)); // bmId
        return verticalList;
    }

    private ComponentBuilder<?, ?> createTwoHorizontalListWithCustomisedWidth(String Key, String value, int firstWidth,
                                                                              int secondWidth, ReportStyleBuilder style) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key).setStyle(style).setWidth(firstWidth));
        horizontalList.add(cmp.text(value).setStyle(style).setWidth(secondWidth));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createThreeHorizontalListWithCustomisedWidth(String Key, String value1,
                                                                                String value2, ReportStyleBuilder style) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key).setStyle(style).setWidth(26));
        horizontalList.add(cmp.text(value1).setStyle(style).setWidth(37));
        horizontalList.add(cmp.text(value2).setStyle(style).setWidth(37));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createThreeHorizontalListWithCustomisedWidth2(String Key, String value1,
                                                                                 String value2) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setWidth(40));
        horizontalList.add(cmp.text(value1).setStyle(borderedStyle).setWidth(30));
        horizontalList.add(cmp.text(value2).setStyle(borderedStyle).setWidth(30));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createThreeHorizontalListWithCustomisedWidth2Bold(String Key, String value1,
                                                                                     String value2) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key).setStyle(boldTextWithBorder).setWidth(40));
        horizontalList.add(cmp.text(value1).setStyle(boldTextWithBorder).setWidth(30));
        horizontalList.add(cmp.text(value2).setStyle(boldTextWithBorder).setWidth(30));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createTwoHorizontalList(String Key, String value, ReportStyleBuilder style) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key).setStyle(style));
        horizontalList.add(cmp.text(value).setStyle(style));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createSingleHorizontalList(String value) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(value).setStyle(borderedStyle));

        return horizontalList;
    }

    private ComponentBuilder<?, ?> createEightHorizontalList(String Key1, String Value1, String Key2, String Value2,
                                                             String Key3, String Value3, String Key4, String Value4) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(Key1).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Value1).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key2).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Value2).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key3).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Value3).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Key4).setStyle(borderedStyle));
        horizontalList.add(cmp.text(Value4).setStyle(borderedStyle));

        return horizontalList;
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

    private String extractValue(String input) {
        // Normalize delimiters: Remove unnecessary spaces around ":" and ","
        String normalizedInput = CommonUtils.normalizeInput(input);

        // Find the first key-value pair ("Score : 650")
        int colonIndex = normalizedInput.indexOf(":");
        if (colonIndex != -1) {
            // Get the value after the colon
            int startIndex = colonIndex + 1;
            int endIndex = normalizedInput.indexOf(",", startIndex); // Find the next comma
            if (endIndex == -1) { // If no comma, find the next space or take till the end
                endIndex = normalizedInput.indexOf(" ", startIndex);
                if (endIndex == -1) {
                    endIndex = normalizedInput.length(); // No space found; take till the end
                }
            }
            return normalizedInput.substring(startIndex, endIndex).trim(); // Extract and trim
        }
        return null; // No key-value pair found

    }

    public String getContentFromFile(String filePath) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

    // public static void main(String[] args) throws Exception {

    // 	String languagePath = "";

    // 	languagePath = "D:\\Downloads\\Form97_English.json";
    // 	JSONObject keysForContent = new JSONObject(new Form97().getContentFromFile(languagePath))
    // 			.getJSONObject("keysForContent");

    // 	new Form97().generatePdfForDbkit(keysForContent);
    // }
}