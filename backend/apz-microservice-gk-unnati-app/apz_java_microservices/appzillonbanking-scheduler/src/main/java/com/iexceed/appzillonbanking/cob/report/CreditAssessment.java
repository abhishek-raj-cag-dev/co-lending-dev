package com.iexceed.appzillonbanking.cob.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.loans.service.LoanService;
import com.iexceed.appzillonbanking.cob.payload.DownloadReportRequestFields;
import com.iexceed.appzillonbanking.cob.service.COBService;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.BorderBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.exception.DRException;



public class CreditAssessment {
	
	private static final Logger logger = LogManager.getLogger(CreditAssessment.class); 
	
	private StyleBuilder borderedStyle, boldText, boldCenteredStyle, boldTextWithBorder, boldLeftStyle , rightStyle, leftStyle, borderedStyleCustomizedLeft, borderedStyleCustomizedRight;

	static String space = "\u00a0\u00a0\u00a0";
	
//	String photoPath1 = "";
//	String photoPath2 = "";
//	String photoPath3 = "";
	
	public CreditAssessment() {
		 
		borderedStyle = stl.style(stl.penThin()).setPadding(5);
		
		BorderBuilder customBorderLeft = stl.border().setTopPen(stl.penThin()).setRightPen(stl.penThin()).setBottomPen(stl.penThin()).setLeftPen(null);
		BorderBuilder customBorderRight = stl.border().setTopPen(stl.penThin()).setRightPen(null).setBottomPen(stl.penThin()).setLeftPen(stl.penThin());
			    
		borderedStyleCustomizedLeft = stl.style().setBorder(customBorderLeft).setPadding(5);
		borderedStyleCustomizedRight = stl.style().setBorder(customBorderRight).setPadding(5);

		boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
		boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		boldText = stl.style().bold();
		boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		
//		StyleBuilder headerStyle = stl.style().setFontSize(20).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
	    rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
	    leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
	    
	}

		public Response generatePdf(DownloadReportRequestFields requestFields, JSONObject keysForContent) throws DRException, IOException {
		
		JSONObject requestFromUI = null;
		Response response;
		try {
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
		JasperReportBuilder subReport13 = new JasperReportBuilder();
		JasperReportBuilder subReport14 = new JasperReportBuilder();
		JasperReportBuilder subReport15 = new JasperReportBuilder();
		JasperReportBuilder subReport16 = new JasperReportBuilder();
		JasperReportBuilder subReport17 = new JasperReportBuilder();
		JasperReportBuilder subReport18 = new JasperReportBuilder();
		JasperReportBuilder subReport19 = new JasperReportBuilder();
		JasperReportBuilder subReport20 = new JasperReportBuilder();
		JasperReportBuilder subReport21 = new JasperReportBuilder();
		JasperReportBuilder subReport22 = new JasperReportBuilder();
		
		report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));

		/* Basic Application Details */
		
		subReport.title(cmp.text(keysForContent.getString("applicationName")).setStyle(boldCenteredStyle.setFontSize(14)));	
		
		subReport1.title(cmp.text(keysForContent.getString("loanDetails")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport1.title(getLoanDetails(keysForContent,requestFromUI)).title(cmp.text("").setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT));

		subReport2.title(cmp.text(keysForContent.getString("verification")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport2.title(getVerificationDetails(keysForContent,requestFromUI)).title(cmp.text(""));
		
		subReport3.title(getVerificationDetails2(keysForContent,requestFromUI)).title(cmp.text(""));
		subReport3.title(cmp.text(keysForContent.getString("relationship")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport3.title(getRelationshipDetails(keysForContent,requestFromUI)).title(cmp.text(""));

		subReport4.title(cmp.text(keysForContent.getString("residenceProof")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport4.title(getResidenceProof(keysForContent,requestFromUI)).title(cmp.text(""));
		
		subReport5.title(cmp.text(keysForContent.getString("insuranceDetails")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport5.title(getInsuranceDetails(keysForContent,requestFromUI)).title(cmp.text(""));
		
		subReport6.title(cmp.text(keysForContent.getString("creditAssessment")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport6.title(getCreditAssessment(keysForContent,requestFromUI)).title(cmp.text(""));
		
		subReport6.title(cmp.text(keysForContent.getString("lineOfBusiness")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport6.title(getDairyBusiness(keysForContent,requestFromUI)).title(cmp.text(""));
		
		subReport7.title(getDairyBusiness2(keysForContent,requestFromUI)).title(cmp.text(""));
		
		subReport8.title(getTailoringBusiness(keysForContent,requestFromUI)).title(cmp.text(""));

		subReport9.title(getKiranaBusiness(keysForContent,requestFromUI)).title(cmp.text(""));
		subReport9.title(cmp.text(keysForContent.getString("otherLineOfBusiness")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport9.title(getOtherBusiness(keysForContent,requestFromUI)).title(cmp.text(""));

		subReport10.title(getOtherBusiness2(keysForContent,requestFromUI)).title(cmp.text(""));

		subReport11.title(cmp.text(keysForContent.getString("incomeDetailsOtherThanBusiness")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport11.title(cmp.text(keysForContent.getString("incomeDetailsNote")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport11.title(cmp.text(keysForContent.getString("agriculture")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport11.title(getAgricultureDetails(keysForContent,requestFromUI)).title(cmp.text(""));

		subReport12.title(cmp.text(keysForContent.getString("salary")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport12.title(getSalaryDetails(keysForContent,requestFromUI)).title(cmp.text(""));

		subReport13.title(cmp.text(keysForContent.getString("wages")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport13.title(getWageDetails(keysForContent,requestFromUI)).title(cmp.text(""));

		subReport14.title(cmp.text(keysForContent.getString("pension")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport14.title(getPensionDetails(keysForContent,requestFromUI)).title(cmp.text(""));

		subReport15.title(cmp.text(keysForContent.getString("rentalIncome")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport15.title(getRentalIncomeDetails(keysForContent,requestFromUI)).title(cmp.text(""));

		subReport16.title(cmp.text(keysForContent.getString("loanObligations")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport16.title(getLoanObligations(keysForContent,requestFromUI)).title(cmp.text(""));
		subReport16.title(cmp.text(keysForContent.getString("otherDetails")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport16.title(getOtherFieldDetails(keysForContent,requestFromUI)).title(cmp.text(""));
		subReport16.title(cmp.text(keysForContent.getString("prayaasFieldList")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport16.title(cmp.text(keysForContent.getString("additionalExpenses")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport16.title(getOtherExpenseDetails(keysForContent,requestFromUI)).title(cmp.text(""));
		
		subReport17.title(getOtherExpenseDetails2(keysForContent,requestFromUI)).title(cmp.text(""));

		subReport18.title(cmp.text(keysForContent.getString("landDetails")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport18.title(getLandDetails(keysForContent,requestFromUI)).title(cmp.text(""));

		subReport19.title(cmp.text(keysForContent.getString("finalIncome")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport19.title(getFinalIncome(keysForContent,requestFromUI)).title(cmp.text(""));

		subReport20.title(cmp.text(keysForContent.getString("inPrincipalDecision")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport20.title(getPrincipalDecision(keysForContent,requestFromUI)).title(cmp.text(""));
		subReport20.title(getPrincipalDecision2(keysForContent,requestFromUI)).title(cmp.text(""));

		subReport21.title(cmp.text(keysForContent.getString("deviations")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport21.title(cmp.text(keysForContent.getString("deviationsNote")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport21.title(getDeviations(keysForContent,requestFromUI)).title(cmp.text(""));

		subReport22.title(cmp.text(keysForContent.getString("reassessment")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport22.title(cmp.text(keysForContent.getString("reassessmentNote")).setStyle(boldLeftStyle.setFontSize(12)));	
		subReport22.title(getReassesssment(keysForContent,requestFromUI)).title(cmp.text(""));


		report.addSummary(cmp.subreport(subReport)).addSummary(cmp.subreport(subReport1))
				.addSummary(cmp.subreport(subReport2)).addSummary(cmp.subreport(subReport3))
				.addSummary(cmp.subreport(subReport4)).addSummary(cmp.subreport(subReport5))
				.addSummary(cmp.subreport(subReport6)).addSummary(cmp.subreport(subReport7))
				.addSummary(cmp.subreport(subReport8)).addSummary(cmp.subreport(subReport9))
				.addSummary(cmp.subreport(subReport10)).addSummary(cmp.subreport(subReport11))
				.addSummary(cmp.subreport(subReport12)).addSummary(cmp.subreport(subReport13))
				.addSummary(cmp.subreport(subReport14)).addSummary(cmp.subreport(subReport15))
				.addSummary(cmp.subreport(subReport16)).addSummary(cmp.subreport(subReport17))
				.addSummary(cmp.subreport(subReport18)).addSummary(cmp.subreport(subReport19))
				.addSummary(cmp.subreport(subReport20)).addSummary(cmp.subreport(subReport21))
				.addSummary(cmp.subreport(subReport22));
		
//		.show();
				logger.debug("added all subreports to report builder"); 
		
		//saving report to the directory and creating base64 string for response
		try {
			response = new Response();
			Properties prop = CommonUtils.readPropertyFile();
		    // Construct file path
		    String filePathDest = prop.getProperty(CobFlagsProperties.FILE_UPLOAD.getKey()) + "/"
		            + requestFields.getAppId() + "/CreditAsessement/" + requestFields.getApplicationId() + "/";
		    logger.debug("filePathDest :: {}", filePathDest); 
		    
		    // Ensure directory exists
		    File directory = new File(filePathDest);
		    if (!directory.exists()) {
		        boolean isCreated = directory.mkdirs();
		        if (!isCreated) {
		            throw new IOException("Failed to create 1: " + filePathDest);
		        }
		    }

		    String filePath = filePathDest + requestFields.getApplicationId() + "_CreditAssessmentReport" + ".pdf";
		    logger.debug("final filePath : {}", filePath);

		    // Save report to file
		    try (FileOutputStream fos = new FileOutputStream(filePath)) {
		        report.toPdf(fos);
		    }

		    // Read file and encode to Base64
		    byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
		    String base64String = java.util.Base64.getEncoder().encodeToString(inputfile);
		    logger.debug("base64String generated : {}", base64String);

		    // Delete the file
		   // Files.deleteIfExists(Paths.get(filePath));
		    
		    response = getSuccessJson(base64String);
		    
			logger.info("Credit Assessment PDF Report Generated");
			
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
	logger.debug("generate CreditAssessment Function end");
return response;

}

	
		//Two Horizontal List	
				private ComponentBuilder<?, ?> createTwoHorizontalListDef(String Key, String value) {
					
					HorizontalListBuilder horizontalList = cmp.horizontalList();
					
					horizontalList.add(cmp.text(Key).setStyle(borderedStyle));
					horizontalList.add(cmp.text(value).setStyle(borderedStyle));
					
					return horizontalList;
					
				}
				private ComponentBuilder<?, ?> createTwoHorizontalListBold(String Key, String value, Integer key1, Integer value1) {
					
					HorizontalListBuilder horizontalList = cmp.horizontalList();
					
					horizontalList.add(cmp.text(Key).setStyle(boldTextWithBorder).setWidth(key1));
					horizontalList.add(cmp.text(value).setStyle(boldTextWithBorder).setWidth(value1));
					
					return horizontalList;
					
				}
		
//Three Horizontal List		
		private ComponentBuilder<?, ?> createThreeHorizontalList(String key, String value1, String value2, boolean isImagePresent, String path1, String path2) {
			HorizontalListBuilder horizontalList = cmp.horizontalList();

			horizontalList.add(cmp.text(key).setStyle(borderedStyle).setWidth(24));
			if (isImagePresent) {
				horizontalList.add(cmp.image(path1).setHorizontalImageAlignment((HorizontalImageAlignment.RIGHT)).setStyle(borderedStyleCustomizedRight).setWidth(19));
				horizontalList.add(cmp.image(path2).setHorizontalImageAlignment((HorizontalImageAlignment.LEFT)).setStyle(borderedStyleCustomizedLeft).setWidth(19));
				horizontalList.add(cmp.image(path1).setHorizontalImageAlignment((HorizontalImageAlignment.RIGHT)).setStyle(borderedStyleCustomizedRight).setWidth(19));
				horizontalList.add(cmp.image(path2).setHorizontalImageAlignment((HorizontalImageAlignment.LEFT)).setStyle(borderedStyleCustomizedLeft).setWidth(19));
			} else {
				horizontalList.add(cmp.text(value1).setStyle(borderedStyle).setWidth(38));
				horizontalList.add(cmp.text(value2).setStyle(borderedStyle).setWidth(38));
			}
			
			return horizontalList;
		}
		private ComponentBuilder<?, ?> createThreeHorizontalListMaxKey(String key, String value1, String value2) {
			HorizontalListBuilder horizontalList = cmp.horizontalList();
			//		horizontalList.add(cmp.text(key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(20));
			horizontalList.add(cmp.text(key).setStyle(borderedStyle).setWidth(50));
			horizontalList.add(cmp.text(value1).setStyle(borderedStyle).setWidth(25));
			horizontalList.add(cmp.text(value2).setStyle(borderedStyle).setWidth(25));
			return horizontalList;
		}
		private ComponentBuilder<?, ?> createThreeHorizontalListBold(String key, String value1, String value2, Integer key1, Integer value3, Integer value4) {
			HorizontalListBuilder horizontalList = cmp.horizontalList();
			//		horizontalList.add(cmp.text(key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(20));
			horizontalList.add(cmp.text(key).setStyle(boldTextWithBorder).setWidth(key1));
			horizontalList.add(cmp.text(value1).setStyle(boldTextWithBorder).setWidth(value3));
			horizontalList.add(cmp.text(value2).setStyle(boldTextWithBorder).setWidth(value4));
			return horizontalList;
		}
		//Four Horizontal List	
				private ComponentBuilder<?, ?> createFourHorizontalList(String Key1, String value1, String Key2, String value2) {
			
					HorizontalListBuilder horizontalList = cmp.horizontalList();
			
					horizontalList.add(cmp.text(Key1).setStyle(borderedStyle).setWidth(40));
					horizontalList.add(cmp.text(value1).setStyle(borderedStyle).setWidth(20));
					horizontalList.add(cmp.text(Key2).setStyle(borderedStyle).setWidth(20));
					horizontalList.add(cmp.text(value2).setStyle(borderedStyle).setWidth(20));
					
			
					return horizontalList;
			
				}
				private ComponentBuilder<?, ?> createFourHorizontalListBold(String Key1, String value1, String Key2, String value2) {
					
					HorizontalListBuilder horizontalList = cmp.horizontalList();
					
					horizontalList.add(cmp.text(Key1).setStyle(boldTextWithBorder).setWidth(40));
					horizontalList.add(cmp.text(value1).setStyle(boldTextWithBorder).setWidth(20));
					horizontalList.add(cmp.text(Key2).setStyle(boldTextWithBorder).setWidth(20));
					horizontalList.add(cmp.text(value2).setStyle(boldTextWithBorder).setWidth(20));
					
					
					return horizontalList;
					
				}
				private ComponentBuilder<?, ?> createFourHorizontalListDef(String Key1, String value1, String Key2, String value2) {
					
					HorizontalListBuilder horizontalList = cmp.horizontalList();
					
					horizontalList.add(cmp.text(Key1).setStyle(borderedStyle));
					horizontalList.add(cmp.text(value1).setStyle(borderedStyle));
					horizontalList.add(cmp.text(Key2).setStyle(borderedStyle));
					horizontalList.add(cmp.text(value2).setStyle(borderedStyle));
					
					
					return horizontalList;
					
				}
				private ComponentBuilder<?, ?> createFourHorizontalListDefBold(String Key1, String value1, String Key2, String value2) {
					
					HorizontalListBuilder horizontalList = cmp.horizontalList();
					
					horizontalList.add(cmp.text(Key1).setStyle(boldTextWithBorder));
					horizontalList.add(cmp.text(value1).setStyle(boldTextWithBorder));
					horizontalList.add(cmp.text(Key2).setStyle(boldTextWithBorder));
					horizontalList.add(cmp.text(value2).setStyle(boldTextWithBorder));
					
					
					return horizontalList;
					
				}
	
//Five Horizontal List	
				
//				private ComponentBuilder<?, ?> createHorizontalList(List<String> textValues, boolean isBold,
//						boolean isImagePresent, List<String> imagePaths, List<Integer> setWidths) {
//
//					HorizontalListBuilder horizontalList = cmp.horizontalList();
//					StyleBuilder style = isBold ? boldTextWithBorder : borderedStyle;
//
//					// Add text or first key
//					horizontalList.add(cmp.text(textValues.get(0)).setStyle(style).setWidth(setWidths.get(0)));
//
//					if (isImagePresent && imagePaths != null && !imagePaths.isEmpty()) {
//						// Add images dynamically
//						for (String path : imagePaths) {
//							horizontalList.add(cmp.image(path)
//									.setHorizontalImageAlignment(HorizontalImageAlignment.CENTER).setStyle(style));
//						}
//					} else {
//						// Add text elements dynamically
//						for (int i = 1; i < textValues.size(); i++) {
//							horizontalList.add(cmp.text(textValues.get(i)).setStyle(style));
//						}
//					}
//
//					return horizontalList;
//				}

		private ComponentBuilder<?, ?> createFiveHorizontalList(String Key1, String key2, String Key3, String key4,
						String key5, boolean isImagePresent, String path1, String path2, String path3) {

			HorizontalListBuilder horizontalList = cmp.horizontalList();

			horizontalList.add(cmp.text(Key1).setStyle(borderedStyle).setWidth(32));
			if (isImagePresent) {
				horizontalList.add(cmp.image(path1).setHorizontalImageAlignment((HorizontalImageAlignment.CENTER))
						.setStyle(borderedStyle).setWidth(17));
				horizontalList.add(cmp.image(path2).setHorizontalImageAlignment((HorizontalImageAlignment.CENTER))
						.setStyle(borderedStyle).setWidth(17));
				horizontalList.add(cmp.image(path3).setHorizontalImageAlignment((HorizontalImageAlignment.CENTER))
						.setStyle(borderedStyle).setWidth(17));
			} else {
				horizontalList.add(cmp.text(key2).setStyle(borderedStyle).setWidth(17));
				horizontalList.add(cmp.text(Key3).setStyle(borderedStyle).setWidth(17));
				horizontalList.add(cmp.text(key4).setStyle(borderedStyle).setWidth(17));
			}
			horizontalList.add(cmp.text(key5).setStyle(borderedStyle).setWidth(17));

			return horizontalList;

		}
		private ComponentBuilder<?, ?> createFiveHorizontalListBold(String Key1, String value1, String Key2, String value2, String Key3) {
			
			HorizontalListBuilder horizontalList = cmp.horizontalList();
			
			horizontalList.add(cmp.text(Key1).setStyle(boldTextWithBorder).setWidth(32));
			horizontalList.add(cmp.text(value1).setStyle(boldTextWithBorder).setWidth(17));
			horizontalList.add(cmp.text(Key2).setStyle(boldTextWithBorder).setWidth(17));
			horizontalList.add(cmp.text(value2).setStyle(boldTextWithBorder).setWidth(17));
			horizontalList.add(cmp.text(Key3).setStyle(boldTextWithBorder).setWidth(17));
			
			return horizontalList;
			
		}
		
		private ComponentBuilder<?, ?> createFiveHorizontalListDef(String v1, String v2, String v3, String v4,
				String v5, boolean isImagePresent, String path1, String path2, String path3) {

			HorizontalListBuilder horizontalList = cmp.horizontalList();

			horizontalList.add(cmp.text(v1).setStyle(borderedStyle));
			if (isImagePresent) {
				horizontalList.add(cmp.image(path1).setHorizontalImageAlignment((HorizontalImageAlignment.CENTER))
						.setStyle(borderedStyle));
				horizontalList.add(cmp.image(path2).setHorizontalImageAlignment((HorizontalImageAlignment.CENTER))
						.setStyle(borderedStyle));
				horizontalList.add(cmp.image(path3).setHorizontalImageAlignment((HorizontalImageAlignment.CENTER))
						.setStyle(borderedStyle));
			} else {
				horizontalList.add(cmp.text(v2).setStyle(borderedStyle));
				horizontalList.add(cmp.text(v3).setStyle(borderedStyle));
				horizontalList.add(cmp.text(v4).setStyle(borderedStyle));
			}
			horizontalList.add(cmp.text(v5).setStyle(borderedStyle));

			return horizontalList;
		}
		
		private ComponentBuilder<?, ?> createFiveHorizontalListDefBold(String v1, String v2, String v3, String v4,
				String v5) {

			HorizontalListBuilder horizontalList = cmp.horizontalList();

			horizontalList.add(cmp.text(v1).setStyle(boldTextWithBorder));
			horizontalList.add(cmp.text(v2).setStyle(boldTextWithBorder));
			horizontalList.add(cmp.text(v3).setStyle(boldTextWithBorder));
			horizontalList.add(cmp.text(v4).setStyle(boldTextWithBorder));
			horizontalList.add(cmp.text(v5).setStyle(boldTextWithBorder));

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

	/*public static String formatCurrency(String amount) {
		double d = Double.parseDouble(amount);
		//DecimalFormat f = new DecimalFormat("#,##,##0.00");
		return f.format(d);
	}*/

private ComponentBuilder<?, ?> createSingleHorizontalList(String Key, String value) {
		
		HorizontalListBuilder horizontalList = cmp.horizontalList();

		// for (int i = 0; i < columns; i++) {

//		horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(25));
//		horizontalList.add(cmp.text(value).setStyle(borderedStyle).setWidth(75));
		
		horizontalList.add(cmp.text(Key).setStyle(boldTextWithBorder).setWidth(100));
		// horizontalList.add(createVerticalList("6","5"));

		// }

		return horizontalList;

	}
	private ComponentBuilder<?, ?> createTwoHorizontalListMaxFieldValue(String Key, String value, boolean isImagePresent ,String path1) {
		
		HorizontalListBuilder horizontalList = cmp.horizontalList();
		
		horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setWidth(40));
		if(isImagePresent) {
		horizontalList.add(cmp.image(path1).setHorizontalImageAlignment((HorizontalImageAlignment.RIGHT))
				.setStyle(borderedStyleCustomizedRight).setWidth(30));
			horizontalList.add(cmp.image(path1).setHorizontalImageAlignment((HorizontalImageAlignment.LEFT))
					.setStyle(borderedStyleCustomizedLeft).setWidth(30));
		} else {
		horizontalList.add(cmp.text(value).setStyle(borderedStyle).setWidth(60));
		}
		return horizontalList;

	}
	private ComponentBuilder<?, ?> createTwoHorizontalListMinFieldValue(String Key, String value) {
		
		HorizontalListBuilder horizontalList = cmp.horizontalList();
		
		// for (int i = 0; i < columns; i++) {
		
//		horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(25));
//		horizontalList.add(cmp.text(value).setStyle(borderedStyle).setWidth(75));
		
		horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setWidth(60));
		horizontalList.add(cmp.text(value).setStyle(borderedStyle).setWidth(40));
		// horizontalList.add(createVerticalList("6","5"));
		
		// }
		
		return horizontalList;
		
	}
// For Land Details
private ComponentBuilder<?, ?> getLandDetails(JSONObject keysForContent, JSONObject req) {
		
		VerticalListBuilder verticalList = cmp.verticalList();

		verticalList.add(createTwoHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.FIELD_VALUE), 60, 40).setStyle(boldCenteredStyle));
		verticalList.add(createTwoHorizontalListMinFieldValue(keysForContent.getString("agriLandHoldingsByFamilyAcres"), ""));
		verticalList.add(createTwoHorizontalListMinFieldValue(keysForContent.getString("landOwnerName"), ""));
		verticalList.add(createTwoHorizontalListMinFieldValue(keysForContent.getString("relationshipWithApplicant"), ""));
		verticalList.add(createTwoHorizontalListMinFieldValue(keysForContent.getString("landImages"), ""));
		verticalList.add(createTwoHorizontalListMinFieldValue(keysForContent.getString("relationshipProof"), ""));
		
		return verticalList;	
	}
//get Loan details
private ComponentBuilder<?, ?> getLoanDetails(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createTwoHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.FIELD_VALUE), 50, 50).setStyle(boldCenteredStyle));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("applicationNumber"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("kendraId"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("kendraName"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("appliedAmount"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("repaymentFrequency"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("loanTenureMonths"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("kendraMeetingFrequency"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("kendraMeetingDay"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("kendraManagerId"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("sourcingDate"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("purposeOfLoan"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("subPurposeOfLoan"), ""));
	
	return verticalList;	
}

private ComponentBuilder<?, ?> getRelationshipDetails(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createTwoHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.FIELD_VALUE), 40, 60).setStyle(boldCenteredStyle));
	verticalList.add(createTwoHorizontalListMaxFieldValue(keysForContent.getString("relationshipWithApplicant"), "",false, ""));
	verticalList.add(createTwoHorizontalListMaxFieldValue(keysForContent.getString("relationshipProofImage"), "", false ,"" ));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getResidenceProof(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createTwoHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.FIELD_VALUE), 40, 60).setStyle(boldCenteredStyle));
	verticalList.add(createTwoHorizontalListMaxFieldValue(keysForContent.getString("presentAddress"), "",false, ""));
	verticalList.add(createTwoHorizontalListMaxFieldValue(keysForContent.getString("permanentAddress"), "",false, ""));
	verticalList.add(createTwoHorizontalListMaxFieldValue(keysForContent.getString("residencyOwnership"), "",false, ""));
	verticalList.add(createTwoHorizontalListMaxFieldValue(keysForContent.getString("residentialStabilityPresentAddressYears"), "",false, ""));
	verticalList.add(createTwoHorizontalListMaxFieldValue(keysForContent.getString("residentialStabilityPresentCityYears"), "",false, ""));
	verticalList.add(createTwoHorizontalListMaxFieldValue(keysForContent.getString("addressProofImage"), "",false, ""));
	
	return verticalList;	
}
	
private ComponentBuilder<?, ?> getVerificationDetails(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();

	verticalList.add(createThreeHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY), 24, 38, 38));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("nameAsPerSystem"), "","", false, "",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("nameAsPerKYC"), "","", false, "",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("primaryKYCNumber"), "","", false, "",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("dateOfBirth"), "","", false, "",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("gender"), "","", false, "",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("mobileNumber"), "","", false, "",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("maritalStatus"), "","", false, "",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("occupation"), "","", false, "",""));
//	verticalList.add(createThreeHorizontalList(keysForContent.getString("photo"), "",""));
//	verticalList.add(createThreeHorizontalList(keysForContent.getString("voterIdPhoto"), "",""));
	
	verticalList.add(createThreeHorizontalList(keysForContent.getString("photo"),"","",false, "", ""));
//	verticalList.add(createThreeHorizontalList(keysForContent.getString("voterIdPhoto"),"","",true, photoPath1, photoPath2));
		
	return verticalList;
}
private ComponentBuilder<?, ?> getVerificationDetails2(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();

	verticalList.add(createThreeHorizontalList(keysForContent.getString("voterIdPhoto"),"","",false, "", ""));
		
	return verticalList;
}
private ComponentBuilder<?, ?> getInsuranceDetails(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createThreeHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY), 24, 38, 38));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("insuranceOpted"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("nameOfInsurer"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("insurancePremium"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("nomineeForInsurance"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("genderOfNominee"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("dateOfBirthOfNominee"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("ageOfNominee"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("relationshipWithNominee"), "","",false,"",""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getPrincipalDecision(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createThreeHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY),24,38,38));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("loanAmount"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("rateOfInterest"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("processingFee"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("insurancePremium"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("stampDutyCharges"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("currentCAGLOutstanding"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("netOffAmount"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("totalAmountPostNetOff"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("instalmentAmountFrequency"), "","",false,"",""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getPrincipalDecision2(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createThreeHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY), 24, 38, 38));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("overdueAmount"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("writtenOffAmount"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("cbScore"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("netIncome"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("unsecuredIndebtedness"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("foir"), "","",false,"",""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getDeviations(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createThreeHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.FIELD_VALUE),keysForContent.getString("deviationAuthority"), 24, 38, 38));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("dv1"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("dv2"), "","",false,"",""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getReassesssment(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createThreeHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.FIELD_VALUE),keysForContent.getString("reassessmentAuthority"), 24, 38, 38));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("ra1"), "","",false,"",""));
	verticalList.add(createThreeHorizontalList(keysForContent.getString("ra2"), "","",false,"",""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getCreditAssessment(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createFourHorizontalListDefBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY),keysForContent.getString("both")));
	verticalList.add(createFourHorizontalListDef(keysForContent.getString("sourceOfIncome"), "","",""));
	verticalList.add(createFourHorizontalListDef(keysForContent.getString("businessType"), "","",""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getFinalIncome(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createFourHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY),keysForContent.getString("both")));
	verticalList.add(createFourHorizontalList(keysForContent.getString("totalIncomeOfApplicant"), "","",""));
	verticalList.add(createFourHorizontalList(keysForContent.getString("totalIncomeOfCoApplicant"), "","",""));
	verticalList.add(createFourHorizontalList(keysForContent.getString("otherFamilyMembersIncomeConsidered"), "","",""));
	verticalList.add(createFourHorizontalList(keysForContent.getString("fieldAssessedIncome"), "","",""));
	verticalList.add(createFourHorizontalList(keysForContent.getString("fieldAssessedIncomeDate"), "","",""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getDairyBusiness(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createSingleHorizontalList(keysForContent.getString("dairyBusiness"), ""));
	
	verticalList.add(createFiveHorizontalListBold("", keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY),keysForContent.getString("both"),keysForContent.getString(Constants.TOTAL)));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.BUSINESS_LOCATION), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.BUSINESS_TENURE), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("breedOfCow"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("numberOfCows"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("breedOfBuffaloes"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("numberOfBuffaloes"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.NET_MONTHLY_DECLARED_INCOME), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.APPLICANT_BUS_PRE_AS_RES), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("sameAsResidence"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.BUSINESS_TYPE), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.ORGANISATION_NAME), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("dailyRevenuePerCow"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("dailyRevenuePerBuffalo"), "","","","",false, "","",""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getDairyBusiness2(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createFiveHorizontalList(keysForContent.getString("monthlyIncomePerCow"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("monthlyIncomePerBuffalo"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.NET_BUSINESS_INCOME), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("totalNumberOfCowsAndBuffaloes"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.IMAGE), "","","","", false, "", "", ""));
	
	return verticalList;	
}

private ComponentBuilder<?, ?> getTailoringBusiness(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createSingleHorizontalList(keysForContent.getString("tailoring"), ""));
	
	verticalList.add(createFiveHorizontalListBold("", keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY),keysForContent.getString("both"),keysForContent.getString(Constants.TOTAL)));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.BUSINESS_LOCATION), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.BUSINESS_TENURE), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("businessPremise"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("typeOfMachine"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("numberOfTailorsWorking"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("numberOfTailoringMachines"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("workType"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.NET_MONTHLY_DECLARED_INCOME), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("itemsStitched"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.APPLICANT_BUS_PRE_AS_RES), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.BUSINESS_TYPE), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.ORGANISATION_NAME), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("revenuePerDay"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("revenuePerMonth"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("totalMonthlyRevenue"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.NET_BUSINESS_INCOME), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.IMAGE), "","","","", false, "", "", ""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getKiranaBusiness(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createSingleHorizontalList(keysForContent.getString("kirana"), ""));
	
	verticalList.add(createFiveHorizontalListBold("", keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY),keysForContent.getString("both"),keysForContent.getString(Constants.TOTAL)));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.BUSINESS_LOCATION), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.BUSINESS_TENURE), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("typeOfShop"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("marketClassification"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("unitAreaOfShop"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("unitAreaOfGodown"), "","","","",false, "","",""));
	
	verticalList.add(createFiveHorizontalList(keysForContent.getString("occupancyLevelOfShopAndGodown"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.NET_MONTHLY_DECLARED_INCOME), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.APPLICANT_BUS_PRE_AS_RES), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.BUSINESS_TYPE), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.ORGANISATION_NAME), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("areaOfShopSqFt"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("areaOfGodownSqFt"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("totalAreaSqFt"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("inventoryArea"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("inventoryValue"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("monthlySales"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("calculatedNetBusinessIncome"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString("finalNetBusinessIncome"), "","","","",false, "","",""));
	verticalList.add(createFiveHorizontalList(keysForContent.getString(Constants.IMAGE), "","","","", false, "", "", ""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getOtherBusiness(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createFiveHorizontalListDefBold(keysForContent.getString(Constants.FIELD_NAME) ,keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY),keysForContent.getString("both"),keysForContent.getString(Constants.TOTAL)));
	
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("otherBusiness"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString(Constants.BUSINESS_TENURE), "","","","", false, "","",""));
	
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getOtherBusiness2(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString(Constants.APPLICANT_BUS_PRE_AS_RES), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString(Constants.BUSINESS_TYPE), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString(Constants.ORGANISATION_NAME), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("salesPerDay"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("numberOfDaysBusinessOperated"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("monthlySales"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("operatingExpenses"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("grossIncome"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("rentLeaseExpense"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("salariesWagesExpense"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("numberOfEmployees"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("salaryPaidPerEmployee"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("totalSalary"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("transportAndCommunication"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("waterBill"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("electricityBill"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("pipedGasBill"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("telephoneBill"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("others"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("totalUtilities"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("additionalExpenses"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("totalBusinessExpenses"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString(Constants.NET_BUSINESS_INCOME), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString(Constants.IMAGE), "","","","", false, "","",""));
	return verticalList;	
}
private ComponentBuilder<?, ?> getAgricultureDetails(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createFiveHorizontalListDefBold(keysForContent.getString(Constants.FIELD_NAME) ,keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY),keysForContent.getString("both"),keysForContent.getString(Constants.TOTAL)));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("land"), "","","","", false, "","",""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getSalaryDetails(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createFiveHorizontalListDefBold(keysForContent.getString(Constants.FIELD_NAME) ,keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY),keysForContent.getString("both"),keysForContent.getString(Constants.TOTAL)));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("grossSalary"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("netSalary"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("salaryMode"), "","","","", false, "","",""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getWageDetails(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createFiveHorizontalListDefBold(keysForContent.getString(Constants.FIELD_NAME) ,keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY),keysForContent.getString("both"),keysForContent.getString(Constants.TOTAL)));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("typeOfActivity"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("ifOther"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("wageEarnedPerDay"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("numberOfDaysInMonth"), "","","","", false, "","",""));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("totalWagePerMonth"), "","","","", false, "","",""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getPensionDetails(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createFiveHorizontalListDefBold(keysForContent.getString(Constants.FIELD_NAME) ,keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY),keysForContent.getString("both"),keysForContent.getString(Constants.TOTAL)));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("pensionIncome"), "","","","", false, "","",""));
	
	return verticalList;	
}

private ComponentBuilder<?, ?> getRentalIncomeDetails(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createFiveHorizontalListDefBold(keysForContent.getString(Constants.FIELD_NAME) ,keysForContent.getString(Constants.APPLICANT_KEY),keysForContent.getString(Constants.COAPPLICANT_KEY),keysForContent.getString("both"),keysForContent.getString(Constants.TOTAL)));
	verticalList.add(createFiveHorizontalListDef(keysForContent.getString("rentalIncomeMonthly"), "","","","", false, "","",""));
	
	return verticalList;	
}

private ComponentBuilder<?, ?> getLoanObligations(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createTwoHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.FIELD_VALUE),50,50));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("otherLoanObligations"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("monthlyEMI"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("remainingTenureMonths"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("sourceOfOtherLoans"), ""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getOtherExpenseDetails(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createTwoHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.FIELD_VALUE),50,50));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("householdExpenses"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("educationExpenses"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("medicalExpenses"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("foodExpenses"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("clothingExpenses"), ""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getOtherFieldDetails(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createTwoHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.FIELD_VALUE),50,50));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("houseOwnership"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("typeOfHouse"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("typeOfRoof"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("numberOfRoomsInHouse"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("modeOfSavings"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("basicAmenities"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("otherAssets"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("doesApplicantUseSmartphone"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("numberOfSmartphonesInFamily"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("doesFamilyHaveAlmirahOrDressingTable"), ""));
	verticalList.add(createTwoHorizontalListDef(keysForContent.getString("doesFamilyHaveChairStoolBenchTable"), ""));
	
	return verticalList;	
}
private ComponentBuilder<?, ?> getOtherExpenseDetails2(JSONObject keysForContent, JSONObject req) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
	verticalList.add(createThreeHorizontalListBold(keysForContent.getString(Constants.FIELD_NAME), keysForContent.getString(Constants.APPLICANT_KEY), keysForContent.getString(Constants.COAPPLICANT_KEY),50,25,25));
	verticalList.add(createThreeHorizontalListMaxKey(keysForContent.getString("isApplicantOrCoApplicantExServiceman"), "",""));
	verticalList.add(createThreeHorizontalListMaxKey(keysForContent.getString("isApplicantOrCoApplicantDivyang"), "",""));
	verticalList.add(createThreeHorizontalListMaxKey(keysForContent.getString("isApplicantOrCoApplicantHavingHealthCondition"), "",""));
	verticalList.add(createThreeHorizontalListMaxKey(keysForContent.getString("isApplicantOrCoApplicantHavingHealthInsurance"), "",""));
	return verticalList;	
}

public Response getSuccessJson(String baseString) {
	logger.debug("Inside getSuccessJson");
	Response response = new Response();
	ResponseHeader responseHeader = new ResponseHeader();
	ResponseBody responseBody = new ResponseBody();

	responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
	logger.debug("responseCode added to responseHeader");
	responseBody.setResponseObj("{\"base64\":\"" + baseString + "\", \"status\":\"" + ResponseCodes.SUCCESS.getValue() + "\"}");
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
	responseBody.setResponseObj("{\"errorMessage\":\"" + error + "\", \"status\":\"" + ResponseCodes.FAILURE.getValue() + "\"}");
	response.setResponseHeader(responseHeader);
	response.setResponseBody(responseBody);
	logger.debug("FailureJson created");
	return response;
	}

}
