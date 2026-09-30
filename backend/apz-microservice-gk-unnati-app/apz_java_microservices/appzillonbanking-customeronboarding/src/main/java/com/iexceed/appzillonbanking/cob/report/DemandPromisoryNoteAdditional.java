package com.iexceed.appzillonbanking.cob.report;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.payload.Address;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.Markup;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.exception.DRException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

public class DemandPromisoryNoteAdditional {
	private static final Logger logger = LogManager.getLogger(DemandPromisoryNoteAdditional.class);
	private StyleBuilder borderedStyle, boldText, boldCenteredStyle, boldTextWithBorder, boldLeftStyle, rightStyle,
			leftStyle;

	static String space = "\u00a0\u00a0\u00a0";

	private String applicantCustId = "";
	private String coApplicantCustId = "";
	private String applicantName = "";
	private String coApplicantName = "";
	private CustomerDetails applicantCustDtls = null;
	private CustomerDetails coApplicantCustDtls = null;
	String appltGender = "";
	String coAppltGender = "";
	String interestRate = "";

	public DemandPromisoryNoteAdditional() {

		borderedStyle = stl.style(stl.penThin()).setPadding(5);
		boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
		boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		boldText = stl.style().bold();
		boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

		rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
		leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

	}


	public String generatePdfForDbkit(JSONObject keysForContent, String filePath, CustomerDataFields custmrDataFields, String language, String leadInitiationDateStr)
			throws DRException, IOException {

		JasperReportBuilder report = new JasperReportBuilder();

		JasperReportBuilder subReport = new JasperReportBuilder();
		JasperReportBuilder subReport1 = new JasperReportBuilder();
		JasperReportBuilder subReport2 = new JasperReportBuilder();
		JasperReportBuilder subReport3 = new JasperReportBuilder();
		JasperReportBuilder subReport4 = new JasperReportBuilder();

		report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));

		/* Basic Application Details */
		CustomerDetailsPayload payload1 = new CustomerDetailsPayload();
		CustomerDetailsPayload payload2 = new CustomerDetailsPayload();
		Gson gsonObj = new Gson();
		for (CustomerDetails custDtl : custmrDataFields.getCustomerDetailsList()) {
			logger.debug("customer Type : " + custDtl.getCustomerType());
			if (custDtl.getCustomerType().equalsIgnoreCase("Applicant")) {
				applicantCustId = String.valueOf(custDtl.getCustDtlId());
				logger.debug("applicantCustId : " + applicantCustId);
				applicantName = custDtl.getCustomerName();

				payload1 = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
				logger.debug("custApplicantPayload :" + payload1);
				applicantCustDtls = custDtl;
				appltGender = payload1.getGender();
			} else if (custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
				coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
				coApplicantName = custDtl.getCustomerName();
				logger.debug("coApplicantCustId : " + coApplicantCustId);
				payload2 = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
				logger.debug("custCo-ApplicantPayload :" + payload2);
				coAppltGender = payload2.getGender();
				coApplicantCustDtls = custDtl;
			}
		}

		// Address
		JSONObject addressDetailsObj = getAllAddressDeatils(custmrDataFields);

		// Gson gsonObj2 = new Gson();
		// String bmLoanAmount =
		// String.valueOf(custmrDataFields.getLoanDetails().getBmRecommendedLoanAmount().intValue());
//		String roi = String.valueOf(custmrDataFields.getLoanDetails().getRoi());
		// LoanDetailsPayload laonpayload =
		// gsonObj2.fromJson(custmrDataFields.getLoanDetails().getPayloadColumn(),
		// LoanDetailsPayload.class);
//		String outstandingAmount = custmrDataFields.getLeadDetails().getCaglOs();

		CibilDetailsPayload cibilDetailsPayload = CommonUtils
				.resolveCibilPayload(custmrDataFields.getCibilDetailsWrapperList(), coApplicantCustId, applicantCustId, gsonObj);

		String interest = String
				.valueOf(cibilDetailsPayload.getRoi() == null ? "" : cibilDetailsPayload.getRoi().toString());
		logger.debug("interest : " + interest);
		BigDecimal sactionAmtDb = custmrDataFields.getLoanDetails().getSanctionedLoanAmount();
		String sactionAmt = String.valueOf(sactionAmtDb == null ? "" : sactionAmtDb.toPlainString());

		long amount = sactionAmtDb == null ? 0L : sactionAmtDb.longValue();
		String snAmtInWords = convert(amount);

		String note = "";
		String finalNote = "";


		if(Constants.FAMILY_WELFARE_PRODUCT_CODE
				 .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode()) ||
				 Constants.UNNATI_EMERGENCY_PRODUCT_CODE
						 .equalsIgnoreCase(custmrDataFields.getApplicationMaster().getProductCode())){
			 note = keysForContent.getString("noteNew");
			 finalNote = note.replace("<applicantName>", applicantName)
					 .replace("<applicantAge>", payload1.getAge())
					 .replace("<applicantAddress>", addressDetailsObj.getString(Constants.PRESENT_ADDRESS_APPLICANT))
					 .replace("<sanctionedAmount>", CommonUtils.amountFormat(sactionAmt) + "/- ")
					 .replace("<sanctionedAmountInWords>", snAmtInWords).replace("<rateOfInterest>", interest + " %");
		 }else{
			 note = keysForContent.getString("note");
			 finalNote = note.replace("<applicantName>", applicantName).replace("<coApplicantName>", coApplicantName)
					.replace("<applicantAge>", payload1.getAge()).replace("<coApplicantAge>", payload2.getAge())
					.replace("<applicantAddress>", addressDetailsObj.getString(Constants.PRESENT_ADDRESS_APPLICANT))
					.replace("<coApplicantAddress>", addressDetailsObj.getString(Constants.PRESENT_ADDRESS_COAPPLICANT))
					.replace("<sanctionedAmount>", CommonUtils.amountFormat(sactionAmt) + "/- ")
					.replace("<sanctionedAmountInWords>", snAmtInWords).replace("<rateOfInterest>", interest + " %");
		 }

		logger.debug("finalNote : " + finalNote);

		String loanAcountNumber = custmrDataFields.getLoanDetails().getT24LoanId() == null ? ""
				: custmrDataFields.getLoanDetails().getT24LoanId();

		subReport.title(cmp.text(keysForContent.getString("applicationName"))
				.setMarkup(Markup.HTML)
				.setStyle(boldCenteredStyle.setFontSize(14).underline()));
		subReport1.title(cmp.text(""));
//		subReport1.title(cmp.text(keysForContent.getString("rs") +"_____________\t\t\t\t\t\t\t"+keysForContent.getString("place")).setStyle(leftStyle));

		subReport1.title(cmp.horizontalList(
				cmp.text(keysForContent.getString("rs") + " " + CommonUtils.amountFormat(sactionAmt) + "/-")
						.setMarkup(Markup.HTML)
						.setStyle(leftStyle),

				cmp.text(keysForContent.getString("place") + " "
								+ custmrDataFields.getApplicationMaster().getBranchName())
						.setMarkup(Markup.HTML)
						.setStyle(rightStyle)
//	        .setStyle(boldTextWithUnderline)
		));
//		subReport1.title(cmp.text(keysForContent.getString("date") + Constants.BLANK_SPACE).setStyle(leftStyle));
		subReport1
				.title(cmp.text(keysForContent.getString("date") + " " + leadInitiationDateStr)
						.setMarkup(Markup.HTML)
						.setStyle(leftStyle));

		subReport2.title(cmp.text(""));
//		subReport2.title(cmp.text(keysForContent.getString("note")).setStyle(leftStyle));
		subReport2.title(cmp.text(finalNote).setMarkup(Markup.HTML));
		subReport2.title(cmp.text(""));
		subReport2
				.title(cmp.text(keysForContent.getString("loanAccount") + " " + loanAcountNumber)
						.setMarkup(Markup.HTML)
						.setStyle(leftStyle));
		subReport2.title(cmp.text(""));

		subReport3.title(cmp.text(keysForContent.getString("applicantName") + " " + applicantName)
				.setMarkup(Markup.HTML)
				.setStyle(leftStyle));
		subReport3.title(cmp.text(keysForContent.getString(Constants.SIGNATURE) + Constants.BLANK_SPACE)
				.setMarkup(Markup.HTML)
				.setStyle(leftStyle));
		subReport3.title(cmp.text(""));



		subReport4.title(cmp.text(""));

		report.addSummary(cmp.subreport(subReport)).addSummary(cmp.subreport(subReport1))
				.addSummary(cmp.subreport(subReport2)).addSummary(cmp.subreport(subReport3))
				.addSummary(cmp.subreport(subReport4));

		// .show();
		//FileOutputStream fos = new FileOutputStream(filePath);
		//try {
		//	report.toPdf(fos);
		//} catch (DRException e) {
			
		//}
		//fos.close();
		//byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
		//byte[] encodedBytes = Base64.getEncoder().encode(inputfile);
		// util.deleteFile(filePath);
		//return new String(encodedBytes);

		try {
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
			
		byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
		byte[] encodedBytes = Base64.getEncoder().encode(inputfile);
		return new String(encodedBytes);
		    }

		} catch (Exception e) {
		    throw e;
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

			// Co-Applicant Address
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
			jsnObj.put(Constants.PRESENT_ADDRESS_APPLICANT, presentAddressApplicant);
			jsnObj.put("permanetAddressApplicant", permanetAddressApplicant);
			jsnObj.put(Constants.PRESENT_ADDRESS_COAPPLICANT, presentAddressCoApplicant);
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

	private static final String[] units = { "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
			"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen",
			"Nineteen" };

	private static final String[] tens = { "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty",
			"Ninety" };

	public static String convert(long number) {
		if (number == 0)
			return "Zero";
		return convertNumber(number).trim();
	}

	private static String convertNumber(long number) {
		if (number < 20)
			return units[(int) number];
		if (number < 100)
			return tens[(int) number / 10] + " " + units[(int) number % 10];
		if (number < 1000)
			return units[(int) number / 100] + " Hundred " + convertNumber(number % 100);
		if (number < 100000)
			return convertNumber(number / 1000) + " Thousand " + convertNumber(number % 1000);
		if (number < 10000000)
			return convertNumber(number / 100000) + " Lakh " + convertNumber(number % 100000);
		return convertNumber(number / 10000000) + " Crore " + convertNumber(number % 10000000);
	}

//	public static void main(String[] args) throws Exception {
//		
//		String languagePath ="";
//		///condition for language
//		String lang = "Kannada";
//		if(lang.equals("English")){ // English
//			//languagePath="C:\\Users\\rs.karthik\\Desktop\\Vishal\\GenTemplates & JSON keys\\Demand Promissory Note\\Demand_Promisory_English.json";
//			languagePath="C:\\Users\\ashoka.raja\\Desktop\\Vishal\\GenTemplates & JSON keys\\Demand Promissory Note\\Demand_Promisory_English.json";
//		}else if(lang.equals("Kannada")){ //Kannada
//		//	languagePath="C:\\Users\\vishal.a\\Downloads\\LOANAPPLICATION (1)\\LOANAPPLICATION\\Demand Promissory Note\\Demand_Promisory_Kannada.txt";
//			languagePath="C:\\Users\\ashoka.raja\\Desktop\\Vishal\\GenTemplates & JSON keys\\Demand Promissory Note\\Demand_Promisory_Kannada.json";
//		}else if(lang.equals("Hindi")){ //Hindi
//		//	languagePath="C:\\Users\\vishal.a\\Downloads\\LOANAPPLICATION (1)\\LOANAPPLICATION\\Demand Promissory Note\\Demand_Promisory_Hindi.txt";
//			languagePath="C:\\Users\\ashoka.raja\\Desktop\\Vishal\\GenTemplates & JSON keys\\Demand Promissory Note\\Demand_Promisory_Hindi.json";
//		}else if(lang.equals("Marathi")){ //Marathi
//			//languagePath="C:\\Users\\vishal.a\\Downloads\\LOANAPPLICATION (1)\\LOANAPPLICATION\\Demand Promissory Note\\Demand_Promisory_Marathi.txt";
//			languagePath="C:\\Users\\ashoka.raja\\Desktop\\Vishal\\GenTemplates & JSON keys\\Demand Promissory Note\\Demand_Promisory_Marathi.json";
//		}else if(lang.equals("Tamil")){ //Tamil
//		//	languagePath="C:\\Users\\vishal.a\\Downloads\\LOANAPPLICATION (1)\\LOANAPPLICATION\\Demand Promissory Note\\Demand_Promisory_Tamil.txt";
//			languagePath="C:\\Users\\ashoka.raja\\Desktop\\Vishal\\GenTemplates & JSON keys\\Demand Promissory Note\\Demand_Promisory_Tamil.json";
//		}else if(lang.equals("Malayalam")){ //Malayalam
//			languagePath="C:\\Users\\vishal.a\\Downloads\\LOANAPPLICATION (1)\\LOANAPPLICATION\\Demand Promissory Note\\Demand_Promisory_Mallu.txt";
//		}		
//		
//			JSONObject keysForContent = new JSONObject(new DemandPromisoryNote()
//				.getContentFromFile(languagePath))
//				.getJSONObject("keysForContent");
//			
//			String base64 = new DemandPromisoryNote().generatePdf( keysForContent);		
//	}

}
