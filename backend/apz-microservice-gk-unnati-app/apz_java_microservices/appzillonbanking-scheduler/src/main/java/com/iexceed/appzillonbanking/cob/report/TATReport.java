package com.iexceed.appzillonbanking.cob.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.TATReportPayload;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;

@Component
public class TATReport {

	private static final Logger logger = LogManager.getLogger(Report.class);

	@Autowired
	private static CommonParamService commonService;

	public static Response genrateTATReportXLSService(List<TATReportPayload> tATlist) {
		CommonParamService commonService = new CommonParamService();
		logger.info("GeneratePdfService start:: ");
		Response response = new Response();
		
		String base64String = "";
		int columnCounter = 0;
		// Gson gson = new Gson().disableHtmlEscaping();
		try {
			logger.debug("Exporter ends 1");
			String reportPath = CommonUtils.getExternalProperties("loanReport");
			String filename = reportPath + "temp.xlsx";
			File file = new File(filename);
			// creating an instance of HSSFWorkbook class
			XSSFWorkbook workbook = new XSSFWorkbook();
			logger.debug("Exporter ends2");
			// invoking creatSheet() method and passing the name of the sheet to be created
			XSSFSheet sheet = workbook.createSheet("TAT Report");
			// creating the 0th row using the createRow() method
			XSSFRow rowhead = sheet.createRow((short) columnCounter);
			// creating cell by using the createCell() method and setting the values to the
			// cell by using the setCellValue() method
			rowhead.createCell(columnCounter++).setCellValue("SrNo.");
			rowhead.createCell(columnCounter++).setCellValue("Work ID");
			rowhead.createCell(columnCounter++).setCellValue("Branch ID");
			rowhead.createCell(columnCounter++).setCellValue("Branch Name");
			rowhead.createCell(columnCounter++).setCellValue("Area");
			rowhead.createCell(columnCounter++).setCellValue("Region");
			rowhead.createCell(columnCounter++).setCellValue("Zone");
			rowhead.createCell(columnCounter++).setCellValue("State");
			rowhead.createCell(columnCounter++).setCellValue("Meeting Day");
			rowhead.createCell(columnCounter++).setCellValue("Product");
			rowhead.createCell(columnCounter++).setCellValue("Kendra ID");
			rowhead.createCell(columnCounter++).setCellValue("Kendra name");
			rowhead.createCell(columnCounter++).setCellValue("KM GK ID");
			rowhead.createCell(columnCounter++).setCellValue("KM name");
			rowhead.createCell(columnCounter++).setCellValue("BM GK ID ");
			rowhead.createCell(columnCounter++).setCellValue("BM Name");
			rowhead.createCell(columnCounter++).setCellValue("i-Exceed Application ID");
			rowhead.createCell(columnCounter++).setCellValue("Member ID");
			rowhead.createCell(columnCounter++).setCellValue("Member Name");
			rowhead.createCell(columnCounter++).setCellValue("Co Applicant ID");
			rowhead.createCell(columnCounter++).setCellValue("Co Applicant Name");
			rowhead.createCell(columnCounter++).setCellValue("Relationship to Applicant");
			rowhead.createCell(columnCounter++).setCellValue("Requested Loan Amount");
			rowhead.createCell(columnCounter++).setCellValue("CB Status Applicant");
			rowhead.createCell(columnCounter++).setCellValue("CB Status Co Applicant");
			rowhead.createCell(columnCounter++).setCellValue("Sourcing Date");
			rowhead.createCell(columnCounter++).setCellValue("Lead Assigned Date");
			rowhead.createCell(columnCounter++).setCellValue("Current Status");
			rowhead.createCell(columnCounter++).setCellValue("Rejection Date");
			rowhead.createCell(columnCounter++).setCellValue("Last Updated Date");
			rowhead.createCell(columnCounter++).setCellValue("Total Time taken for KM Sourcing");
			rowhead.createCell(columnCounter++).setCellValue("Total Time taken for BM Sourcing Recommendation");
			rowhead.createCell(columnCounter++).setCellValue("Total Time taken for Sourcing");
			rowhead.createCell(columnCounter++).setCellValue("Pushback from RPC");
			rowhead.createCell(columnCounter++).setCellValue("Comments from RPC");
			rowhead.createCell(columnCounter++).setCellValue("Total time taken for RPC Pushback Resolution");
			rowhead.createCell(columnCounter++).setCellValue("Time Stamp of KM Application Submission");
			rowhead.createCell(columnCounter++).setCellValue("Time Stamp BM Application Submission");
			if (!tATlist.isEmpty()) {
				int rowno = 1;
				logger.debug("Exporter ends 2");
				for (TATReportPayload tat : tATlist) {
					// creating the 1st row
					XSSFRow row = sheet.createRow((short) rowno);
					// inserting data in the first row
					columnCounter = 0;
					row.createCell(columnCounter++).setCellValue(rowno);
					row.createCell(columnCounter++).setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getPid()));
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getBranchId()));
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getBranchName()));
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getAreaName()));
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getRegionName()));
					row.createCell(columnCounter++).setCellValue("-");
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getStateName()));
					row.createCell(columnCounter++).setCellValue("-");
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getProduct()));
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getKendraId()));
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getKendraName()));
					row.createCell(columnCounter++).setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getKmId()));
					row.createCell(columnCounter++).setCellValue("-");
					row.createCell(columnCounter++).setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getBmId()));
					row.createCell(columnCounter++).setCellValue("-");
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getApplicationId()));
					row.createCell(columnCounter++)
							.setCellValue("" + CommonUtils.getDefaultValueIfObjNull(tat.getApplicantId()));
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getApplicantName()));
					row.createCell(columnCounter++)
							.setCellValue("" + CommonUtils.getDefaultValueIfObjNull(tat.getCoApplicantId()));
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getCoApplicantName()));
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getRelationshipToApplicant()));
					row.createCell(columnCounter++)
							.setCellValue("" + CommonUtils.getDefaultValueIfObjNull(tat.getLoanAmount()));
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getApplicantCBStatus()));
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getCoApplicantCBStatus()));
					row.createCell(columnCounter++).setCellValue("-");
					row.createCell(columnCounter++).setCellValue(CommonUtils.formatdate(tat.getLeadAssignedDate()));
					row.createCell(columnCounter++).setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getStatus()));
					row.createCell(columnCounter++).setCellValue(CommonUtils.formattime(tat.getRejectionDate()));
					row.createCell(columnCounter++).setCellValue(CommonUtils.formattime(tat.getLastUpdatedDate()));
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getTimeTakenByKM()));
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getTimeTakenByBM()));
					row.createCell(columnCounter++)
							.setCellValue(CommonUtils.getDefaultValueIfObjNull(tat.getTotalTimeTakenBySourcing()));
					row.createCell(columnCounter++).setCellValue(CommonUtils.formattime(tat.getRPCPushback()));//
					row.createCell(columnCounter++).setCellValue("-");
					row.createCell(columnCounter++).setCellValue(
							CommonUtils.getDefaultValueIfObjNull(tat.getTimeTakenForRPCPushbackResolution()));
					row.createCell(columnCounter++).setCellValue(CommonUtils.formattime(tat.getKMSubmissionTime()));
					row.createCell(columnCounter++).setCellValue(CommonUtils.formattime(tat.getBMSubmissionTime()));
					rowno++;
				}
			}

			logger.debug("Exporter ends 3");
			// FileOutputStream fileOut = new FileOutputStream(tempFile);
			try (FileOutputStream fileOut1 = new FileOutputStream(filename);) {
			workbook.write(fileOut1);
			}
			// closing the workbook
			workbook.close();
			logger.debug("Exporter ends 4");
			try(FileInputStream fis = new FileInputStream(filename);){

			byte[] bytes = new byte[(int) file.length()];
			int b = fis.read(bytes);
			logger.debug("processFile file bytes : " + b);
			// base64String = gson.toJson(Base64.encodeBase64(bytes));
			base64String = Base64.getEncoder().encodeToString(bytes);
			logger.debug("processFile base64 : " + base64String);
			// prints the message on the console
			logger.debug("Excel file has been generated successfully.");
			response = commonService.getSuccessJson(base64String);
			logger.info("genrateTATReportXLSService Report Generated " + response.toString());
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			response = commonService.getFailureJson(e.getMessage());
			logger.error(e.getMessage(), e);
		}
		logger.debug("genrateTATReportXLSService Function end " + response.toString());
		return response;
	}

}
