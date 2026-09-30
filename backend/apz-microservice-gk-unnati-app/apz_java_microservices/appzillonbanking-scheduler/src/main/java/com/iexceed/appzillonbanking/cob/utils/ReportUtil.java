package com.iexceed.appzillonbanking.cob.utils;

import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.awt.Color;

import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;

import lombok.experimental.UtilityClass;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;

@UtilityClass
public class ReportUtil {

	public static final String REPORT_HEADLINE = "Application Status Report";
	/**
	 * Styles for All Report
	 */
	public static final StyleBuilder TITLE_STYLE = stl.style().setFontSize(10).setBold(true)
			.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
			.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE).setPadding(2).setBackgroundColor(new Color(155, 187, 89)).setForegroundColor(Color.black);

	public static final StyleBuilder COLUMN_STYLE = stl.style().setFontSize(ReportUtil.FONT_SIZE)
			.setBorder(stl.penThin()).setPadding(2).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
			.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE).setBold(false);

	/**
	 * Report Font Variable Constant
	 */
	public static final Integer FONT_SIZE = 10;
	

	/**
	 * Report Field Variable Constant
	 */
	public static final String FIELD1 = "field1";
	public static final String FIELD2 = "field2";
	public static final String FIELD3 = "field3";
	public static final String FIELD4 = "field4";
	public static final String FIELD5 = "field5";
	public static final String FIELD6 = "field6";
	public static final String FIELD7 = "field7";
	public static final String FIELD8 = "field8";
	public static final String FIELD9 = "field9";
	public static final String FIELD10 = "field10";
	public static final String FIELD11 = "field11";
	public static final String FIELD12 = "field12";
	public static final String FIELD13 = "field13";
	public static final String FIELD14 = "field14";
	public static final String FIELD15 = "field15";
	public static final String FIELD16 = "field16";
	public static final String FIELD17 = "field17";
	public static final String FIELD18 = "field18";
	public static final String FIELD19 = "field19";
	public static final String FIELD20 = "field20";
	public static final String FIELD21 = "field21";
	public static final String FIELD22 = "field22";
	public static final String FIELD23 = "field23";
	public static final String FIELD24 = "field24";
	public static final String FIELD25 = "field25";

	/**
	 * For Report Types
	 */
	public enum ReportType {

		APPLICATION_STATUS_REPORT(REPORT_HEADLINE);
		private String type;

		private ReportType(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}

	/**
	 * For Report Columns
	 */
	public enum ReportColumns {

		REFERENCE_NUMBER("Reference Number"),APPLICANT_TYPE("Applicant type (Prospect or existing cust)"),CUSTOMER_NAME("Customer Name"),RELATIONSHIP_TYPE("Relationship type (Primary/ Secondary/ authorised signatory/ UBO/ Co-applicant etc."),EXISTING_CUSTID("Existing Customer ID"),PRIMARY_ID("Primary ID"),PRIMARY_ID_VALUE("Primary ID value"),
		BRANCH("Branch"),PRODUCT_CODE("product Code"),PRODUCT_TYPE("Product Type"),CUSTOMER_TYPE("Customer Type"),CUSTOMER_CATEGORY("Customer Category"),RESIDENCE_NR_STATUS("Residence/ NR Status"),KYC_MODE("KYC mode VKYC/ RM VISIT"),KYC_STATUS("KYC Status"),APPLICATION_STATUS("Application Status"),REJECT_REASON("reason for rejection"),ACCOUNT_NO_GENERATE("Account number Generated "),
		CUSTOMER_ID_GENERATE("Customer id generated "),SUBMISSION_DATE("Submission Date"),ASSIGNED_TO("Assigned To"),LAST_UPDATED("Last Updated On"),DOORS_WORKFLOW_ID("DoORS workflow Id"),VKYC_REFERENCE_NO("VKYC reference no "),RM_ID("RM ID to which application is assigned");

		private String columnName;

		private ReportColumns(String name) {
			this.columnName = name;
		}

		public String getColumnName() {
			return columnName;
		}
	}

	public static Response getSuccessJson(String baseString) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();

		responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
		responseBody.setResponseObj(baseString);
		response.setResponseBody(responseBody);
		return response;
	}

	public static Response getFailureJson(String error) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();

		responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
		responseBody.setResponseObj(error);
		response.setResponseBody(responseBody);
		return response;
	}
}
