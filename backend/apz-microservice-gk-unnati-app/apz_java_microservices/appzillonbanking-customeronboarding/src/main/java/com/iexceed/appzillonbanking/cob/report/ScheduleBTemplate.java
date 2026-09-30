package com.iexceed.appzillonbanking.cob.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;
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
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.dynamicreports.report.constant.Markup;

@Service
public class ScheduleBTemplate {

    private static final Logger logger = LogManager.getLogger(ScheduleBTemplate.class);

    private StyleBuilder borderedStyle, boldText, headerTextWithBorder, boldCenteredStyle, boldTextWithBorder,
            boldLeftStyle, rightStyle, leftStyle;

    static String space = "\u00a0\u00a0\u00a0";


    private String applicantName = "";
    private String coApplicantName = "";
    public ScheduleBTemplate() {

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

  public String generatePdfForDbKit(JSONObject keysForContent, CustomerDataFields custmrDataFields,
          String productName, String language, String filePath) throws DRException, IOException {

      logger.debug("generate ScheduleBReport Function start");

      // Basic Application Details
      for (CustomerDetails custDtl : custmrDataFields.getCustomerDetailsList()) {
          logger.debug("customer Type : {}", custDtl.getCustomerType());

          if ("Applicant".equalsIgnoreCase(custDtl.getCustomerType())) {
              applicantName = custDtl.getCustomerName();
              logger.debug("applicantName : {}", applicantName);

          } else if ("Co-App".equalsIgnoreCase(custDtl.getCustomerType())) {
              coApplicantName = custDtl.getCustomerName();
              logger.debug("coApplicantName : {}", coApplicantName);
          }
      }

      try {
          JasperReportBuilder report = new JasperReportBuilder();
          JasperReportBuilder subReport1 = new JasperReportBuilder();
          JasperReportBuilder subReport2 = new JasperReportBuilder();

          report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT)
                  .setPageMargin(DynamicReports.margin(30));

          report.title(cmp.text(keysForContent.getString("applicationName")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle))
                  .title(cmp.text(""));

          subReport1.title(cmp.text(keysForContent.getString("applicationId")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle))
                  .title(cmp.text(""));

          subReport2.title(getTnC("1.", keysForContent.getString("TnC1"))).title(cmp.text(""));
          subReport2.title(getTnC("2.", keysForContent.getString("TnC2"))).title(cmp.text(""));
          subReport2.title(getTnC("3.", keysForContent.getString("TnC3")))
                  .title(cmp.text(""))
                  .title(cmp.text(""))
                  .title(cmp.text(""));

          subReport2.title(getSignatory(keysForContent))
                  .title(cmp.text(""));

          report.addSummary(cmp.subreport(subReport1))
                  .addSummary(cmp.subreport(subReport2));

          String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
          logger.debug("inputLanguage : {}", language);

          boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
                  .anyMatch(lang -> lang.equalsIgnoreCase(language));

          if (isValidLanguage) {
              ByteArrayOutputStream htmlOut = new ByteArrayOutputStream();
              report.toHtml(htmlOut);
              return htmlOut.toString(StandardCharsets.UTF_8.name());
          } else {
              try (FileOutputStream fos = new FileOutputStream(filePath)) {
                  report.toPdf(fos);
              }

              byte[] inputFile = Files.readAllBytes(Paths.get(filePath));
              logger.info("Schedule B PDF Report Generated");
              return Base64.getEncoder().encodeToString(inputFile);
          }

      } catch (Exception e) {
          logger.error("Error generating Schedule B report", e);
          throw e;
      } finally {
          logger.debug("generate ScheduleBReport Function end");
      }
  }
    private ComponentBuilder<?, ?> getTnC(String no, String text) {
        // TODO Auto-generated method stub
        VerticalListBuilder verticalList = cmp.verticalList();
        verticalList.add(createTwoHorizontalList(no, text));
        return verticalList;
    }

    private ComponentBuilder<?, ?> createTwoHorizontalList(String no, String key) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();

        horizontalList.add(cmp.text(no).setWidth(10));
        horizontalList.add(cmp.text(key).setMarkup(Markup.HTML));
        return horizontalList;
    }

    private ComponentBuilder<?, ?> getSignatory(JSONObject keysForContent) {

        VerticalListBuilder verticalList = cmp.verticalList();
        String blank = " ................";

        verticalList.add(
                cmp.horizontalList(
                        cmp.text(keysForContent.getString("applicantName") + ": "
                                        + applicantName)
                                .setMarkup(Markup.HTML)
                                .setStyle(stl.style()
                                        .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT))));
        verticalList.add(
                cmp.horizontalList(
                        cmp.text(keysForContent.getString("applicantSign") + ": "
                                        + blank)
                                .setMarkup(Markup.HTML)
                                .setStyle(stl.style()
                                        .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT))));

//        verticalList.add(
//                cmp.horizontalList(
//                        cmp.text(keysForContent.getString("applicantNameSign") + ": "
//                                        + applicantName + blank)
//                                .setMarkup(Markup.HTML)
//                                .setStyle(stl.style()
//                                        .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT))));
        if (coApplicantName != null && !coApplicantName.isEmpty()) {

            verticalList.add(
                    cmp.horizontalList(
                            cmp.text(keysForContent.getString("coApplicantName") + ": "
                                            + coApplicantName)
                                    .setMarkup(Markup.HTML)
                                    .setStyle(stl.style()
                                            .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT))));

            verticalList.add(
                    cmp.horizontalList(
                            cmp.text(keysForContent.getString("coApplicantSign") + ": "
                                            + blank)
                                    .setMarkup(Markup.HTML)
                                    .setStyle(stl.style()
                                            .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT))));

//            verticalList.add(
//                    cmp.horizontalList(
//                            cmp.text(keysForContent.getString("coapplicantNameSign") + ": "
//                                            + coApplicantName + blank)
//                                    .setMarkup(Markup.HTML)
//                                    .setStyle(stl.style()
//                                            .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT))));
        }
        verticalList.add(
                cmp.horizontalList(
                        cmp.text(keysForContent.getString("authorizedSign") + ": "
                                        + blank)
                                .setMarkup(Markup.HTML)
                                .setStyle(stl.style()
                                        .setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT))));
        return verticalList;
    }

    private ComponentBuilder<?, ?> createTwoHorizontalListSign(String key1, String key2) {

        HorizontalListBuilder horizontalList = cmp.horizontalList();
        horizontalList.add(cmp.text(key1).setMarkup(Markup.HTML));
        horizontalList.add(cmp.text(key2).setMarkup(Markup.HTML));
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

    public String getContentFromFile(String filePath) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }


}
