package com.iexceed.appzillonbanking.cob.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.BankDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.payload.Address;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.nesl.domain.ab.Enach;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.Markup;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.exception.DRException;



public class ConsentLetter {
	private static final Logger logger = LogManager.getLogger(ConsentLetter.class); 
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
	
	
	public ConsentLetter() {
		 
		borderedStyle = stl.style(stl.penThin()).setPadding(5);
		boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
		boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		boldText = stl.style().bold();
		boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		
	    rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
	    leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
	    
	    
	}
	public String generatePdfForDbkit(JSONObject keysForContent, String filePath, CustomerDataFields custmrDataFields, List<Enach> enachDetails, String language) throws DRException, IOException {
		
		/* Basic Application Details */
		CustomerDetailsPayload payload1 = null;
		CustomerDetailsPayload payload2 =null;
		String custType = "";
		Gson gsonObj = new Gson();
		for(CustomerDetails custDtl : custmrDataFields.getCustomerDetailsList()) {
			logger.debug("customer Type : " + custDtl.getCustomerType());
			if(custDtl.getCustomerType().equalsIgnoreCase("Applicant")) { 
				 applicantCustId = String.valueOf(custDtl.getCustDtlId());
				 logger.debug("applicantCustId : " + applicantCustId);
				 applicantName = custDtl.getCustomerName();
				 custType = custDtl.getCustomerType();
				payload1  = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
				logger.debug("custApplicantPayload :" + payload1);
				applicantCustDtls = custDtl;
				appltGender = payload1.getGender();
			}else if(custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
				coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
				coApplicantName = custDtl.getCustomerName();
				 custType = custDtl.getCustomerType();
				logger.debug("coApplicantCustId : " + coApplicantCustId);
				payload2  = gsonObj.fromJson(custDtl.getPayloadColumn(), CustomerDetailsPayload.class);
				logger.debug("custCo-ApplicantPayload :" + payload2);
				coAppltGender = payload2.getGender();
				coApplicantCustDtls = custDtl;
			}
		}
		
		
//		Gson gsonObj = new Gson();
		BankDetails coApplicant = null;
		BankDetailsPayload bankPayload1 =null;
		BankDetailsPayload bankPayload2 =null;
		for(BankDetailsWrapper bkWrpr : custmrDataFields.getBankDetailsWrapperList()) {
			logger.debug("applicantCustId " + applicantCustId);
			if(String.valueOf(bkWrpr.getBankDetails().getCustDtlId()).equals(applicantCustId)){
				bankPayload1  = gsonObj.fromJson(bkWrpr.getBankDetails().getPayloadColumn(), BankDetailsPayload.class);
				logger.debug("BankDetailsApplicantPayload : " + bankPayload1.toString());
			}else if(String.valueOf(bkWrpr.getBankDetails().getCustDtlId()).equals(coApplicantCustId)){
				coApplicant = bkWrpr.getBankDetails();
				bankPayload2 = gsonObj.fromJson(bkWrpr.getBankDetails().getPayloadColumn(), BankDetailsPayload.class);
				logger.debug("BankDetailsCo-applicantPayload : "+bankPayload2.toString());
			}
		}
		
		String finalRefId = "";
		String finalCustomerType = "";
		LocalDateTime latestTs = null;

		if(enachDetails != null) {
			for(Enach ench :enachDetails) {

		        // Only successful ENACH
		        boolean isSuccess = Constants.ENACH_SUCCESS_STS.equalsIgnoreCase(ench.getResDesc())
		                || Constants.SUCCESS.equalsIgnoreCase(ench.getResDesc());
		        if (!isSuccess) continue;

		        LocalDateTime ts = ench.getUpdatedTs();

		        // First valid record
		        if (latestTs == null) {
		            latestTs = ts;
		            finalRefId = ench.getPgTranId();
		            finalCustomerType = ench.getCustomerType();
		            continue;
		            }

		        // If this record is newer, update the latest
		        if (ts.isAfter(latestTs)) {
		            latestTs = ts;
		            finalRefId = ench.getPgTranId();
		            finalCustomerType = ench.getCustomerType();
		            }
				}
		    
		    logger.debug("finalCustomerType" + finalCustomerType);
		    logger.debug("finalRefId" + finalRefId);
		}	

		BigDecimal sactionAmtDb = custmrDataFields.getLoanDetails().getSanctionedLoanAmount();
		String sactionAmt = (sactionAmtDb == null) ? "" : sactionAmtDb.toPlainString();
		
        String outstandingAmount = String.valueOf(0);
        if(custmrDataFields.getApplicationMaster().getProductCode().equalsIgnoreCase(Constants.UNNATI_PRODUCT_CODE)) {
            outstandingAmount = custmrDataFields.getLeadDetails().getCaglOs();
        }else if(custmrDataFields.getApplicationMaster().getProductCode().equalsIgnoreCase(Constants.RENEWAL_LOAN_PRODUCT_CODE)){
            outstandingAmount = custmrDataFields.getRenewalLeadDetails().getCaglOs();
        }
		BigDecimal outstandingAmountDecimal = BigDecimal.ZERO;
		 try {
			outstandingAmountDecimal = new BigDecimal(outstandingAmount);
		    } catch (NumberFormatException e) {
		    logger.debug("Invalid number format for outstandingAmount: {}", outstandingAmount, e);
		    }
		
		String outStdgAmtInWords = convert(outstandingAmountDecimal.longValue());
		 
		long amount = sactionAmtDb == null ? 0L : sactionAmtDb.longValue();
		String snAmtInWords = convert(amount);
		
		String loanId = custmrDataFields.getLoanDetails().getT24LoanId() != null ? custmrDataFields.getLoanDetails().getT24LoanId() : "";
		
		String line1 = keysForContent.getString("line1");
		String finalLine1 = line1.replace("<loanId>", loanId).replace("<sanctionedAmount>", CommonUtils.amountFormat(sactionAmt)+"/- ").replace("<sanctionedAmountInWords>", snAmtInWords).replace("<totalLoanOutstandingAmount>",  CommonUtils.amountFormat(outstandingAmount)+"/- ").replace("<totalLoanOutstandingAmountInWords>", outStdgAmtInWords);
		
		String line2 = keysForContent.getString("line2");
		String finalLine2 = line2.replace("<sanctionedAmount>",  CommonUtils.amountFormat(sactionAmt)+"/- ").replace("<sanctionedAmountInWords>", snAmtInWords);
		
		String line3 = keysForContent.getString("line3");
		String finalLine3 = line3.replace("<applicantName>", applicantName).replace("<bankAccountNumber>",bankPayload1.getAccountNumber()).replace("<bankBranchName>", bankPayload1.getBranchName()).replace("<bankName>", bankPayload1.getBankName());
		logger.debug("finalLine1 : " + finalLine1);
		logger.debug("finalLine2 : " + finalLine2);
		logger.debug("finalLine3 : " + finalLine3);
		
		JasperReportBuilder report = new JasperReportBuilder();
		JasperReportBuilder subReport = new JasperReportBuilder();
		JasperReportBuilder subReport1 = new JasperReportBuilder();
		JasperReportBuilder subReport2 = new JasperReportBuilder();
		JasperReportBuilder subReport3 = new JasperReportBuilder();
		JasperReportBuilder subReport4 = new JasperReportBuilder();
		JasperReportBuilder subReport5 = new JasperReportBuilder();
		JasperReportBuilder subReport6 = new JasperReportBuilder();
		
		//from
		JSONObject addressDetailsObj = getAllAddressDeatils(custmrDataFields);
		String fromAddressAppnt = addressDetailsObj.getString("presentAddressApplicant");
		String fromAddressCoAppnt = addressDetailsObj.getString("presentAddressCoApplicant");
		
		report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));

		/* Basic Application Details */
		subReport.title(cmp.text(keysForContent.getString("applicationName")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));	
		subReport1.title(cmp.text(keysForContent.getString("date") + CommonUtils.getCurDate()).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport1.title(cmp.text(keysForContent.getString("from")).setMarkup(Markup.HTML).setStyle(leftStyle));

		subReport2.title(cmp.text(applicantName).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport2.title(cmp.text(fromAddressAppnt).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport2.title(cmp.text(coApplicantName).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport2.title(cmp.text(fromAddressCoAppnt).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport2.title(cmp.text(""));
		subReport3.title(cmp.text(keysForContent.getString("to")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport3.title(cmp.text(keysForContent.getString("subject")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport3.title(cmp.text(""));

		subReport4.title(cmp.text(finalLine1).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport4.title(cmp.text(finalLine2).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport4.title(cmp.text(finalLine3).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport4.title(cmp.text(keysForContent.getString("line4")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport4.title(cmp.text(keysForContent.getString("line5")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport4.title(cmp.text(keysForContent.getString("line6")).setMarkup(Markup.HTML).setStyle(leftStyle));
        subReport5.title(getBankDetailsForConsentLetter(keysForContent, bankPayload1, bankPayload2, finalRefId, finalCustomerType)).title(cmp.text(""));
		subReport6.title(
			    cmp.horizontalList(
			        cmp.text(keysForContent.getString("borrowerName") + applicantName).setMarkup(Markup.HTML).setStyle(leftStyle),
			        cmp.text(keysForContent.getString("coborrowerName") + coApplicantName).setMarkup(Markup.HTML).setStyle(rightStyle)
			        )
				);
	
		subReport6.title(
			    cmp.horizontalList(
			        cmp.text(keysForContent.getString("signature") +" _________________").setMarkup(Markup.HTML).setStyle(leftStyle),
			        cmp.text(keysForContent.getString("signature") +" _________________").setMarkup(Markup.HTML).setStyle(rightStyle)
			    		)
				);
		
		report.addSummary(cmp.subreport(subReport))
		.addSummary(cmp.subreport(subReport1))
		.addSummary(cmp.subreport(subReport2))
		.addSummary(cmp.subreport(subReport3))
		.addSummary(cmp.subreport(subReport4))
		.addSummary(cmp.subreport(subReport5))
		.addSummary(cmp.subreport(subReport6));
		// .show();
	        try {
		    String[] newVrnclrLanguageArr = Constants.NEW_VERNCLR_LANGUAGES.split(",");
		    logger.debug("inputLanguage " + language);
		    
		    boolean isValidLanguage = Arrays.stream(newVrnclrLanguageArr)
		            .anyMatch(lang -> lang.equalsIgnoreCase(language));

		    if (isValidLanguage) {
		        // Generate HTML in-memory, no file creation
		        ByteArrayOutputStream htmlOut = new ByteArrayOutputStream();
		        report.toHtml(htmlOut);
		        
		        //Return raw HTML instead of Base64
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

	private ComponentBuilder<?, ?> createSevenHorizontalListForConsentLetter(String value1, String value2,
			String value3, String value4, String value5, String value6, String value7) {
	
	HorizontalListBuilder horizontalList = cmp.horizontalList();

	// for (int i = 0; i < columns; i++) {

//	horizontalList.add(cmp.text(Key).setStyle(borderedStyle).setStyle(boldTextWithBorder).setWidth(25));
//	horizontalList.add(cmp.text(value).setStyle(borderedStyle).setWidth(75));
	
	horizontalList.add(cmp.text(value1).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(10));
	horizontalList.add(cmp.text(value2).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(16));
	horizontalList.add(cmp.text(value3).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(16));
	horizontalList.add(cmp.text(value4).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(16));
	horizontalList.add(cmp.text(value5).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(14));
	horizontalList.add(cmp.text(value6).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(14));
	horizontalList.add(cmp.text(value7).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(14));
	
	return horizontalList;
}

	//show only latest enach bank details - either of Applicant or Coapplicant
	private ComponentBuilder<?, ?> getBankDetailsForConsentLetter(JSONObject keysForContent,
				BankDetailsPayload bankPayload1, BankDetailsPayload bankPayload2,
				String finalRefId, String finalCustomerType) {
	
	VerticalListBuilder verticalList = cmp.verticalList();
	
		verticalList.add(createSevenHorizontalListForConsentLetter(keysForContent.getString("serialNo"),
				keysForContent.getString("applicantType"), keysForContent.getString("eNachRef"),
				keysForContent.getString("bankAccountName"), keysForContent.getString("branchName"),
				keysForContent.getString("ifscCode"), keysForContent.getString("accountNo")));
	
		int serial = 1;	
		if (!finalRefId.isEmpty()) {

		    if (finalCustomerType.equalsIgnoreCase(Constants.APPLICANT) && bankPayload1 != null) {
		        // add applicant row
			verticalList.add(createSevenHorizontalListForConsentLetter(
		                String.valueOf(serial++), Constants.APPLICANT, finalRefId,
					CommonUtils.getDefaultValue(bankPayload1.getAccountName()),
					CommonUtils.getDefaultValue(bankPayload1.getBranchName()),
					CommonUtils.getDefaultValue(bankPayload1.getIfsc()),
					CommonUtils.getDefaultValue(bankPayload1.getAccountNumber())));
		}

		    else if (finalCustomerType.equalsIgnoreCase(Constants.COAPPLICANT) && bankPayload2 != null) {
		        // add co-applicant row
			verticalList.add(createSevenHorizontalListForConsentLetter(
		                String.valueOf(serial++), Constants.CO_APPLICANT, finalRefId,
					CommonUtils.getDefaultValue(bankPayload2.getAccountName()),
					CommonUtils.getDefaultValue(bankPayload2.getBranchName()),
					CommonUtils.getDefaultValue(bankPayload2.getIfsc()),
					CommonUtils.getDefaultValue(bankPayload2.getAccountNumber())));
		    }
		}

	return verticalList;	
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
 			
 			for(AddressDetails addr : req.getAddressDetailsWrapperList().get(0).getAddressDetailsList()) {
 				if(addr.getAddressType().equalsIgnoreCase("Personal") && 
 						(String.valueOf(addr.getCustDtlId()).equals(applicantCustId))){
 					AddressDetailsPayload applicantPayload  = gsonObj.fromJson(addr.getPayloadColumn(), AddressDetailsPayload.class);
 					applicantAddrPayLoadLst = applicantPayload.getAddressList();
 					logger.debug("PersonalAddLstApplicant :" + applicantAddrPayLoadLst);				
 				}else if(addr.getAddressType().equalsIgnoreCase("Personal") && 
 						(String.valueOf(addr.getCustDtlId()).equals(coApplicantCustId))){
 					AddressDetailsPayload coApplicantPayload  = gsonObj.fromJson(addr.getPayloadColumn(), AddressDetailsPayload.class);
 					coApplicantAddrPayLoadLst = coApplicantPayload.getAddressList();
 					logger.debug("PersonalAddLstCo-aaplicant :" + coApplicantAddrPayLoadLst);	
 				}
 				
 				//occupation Address
 				if(addr.getAddressType().equalsIgnoreCase("Occupation") && 
 						(String.valueOf(addr.getCustDtlId()).equals(applicantCustId))){
 					AddressDetailsPayload applicantPayload  = gsonObj.fromJson(addr.getPayloadColumn(), AddressDetailsPayload.class);
 					applicantOccupnAddrPayLoadLst = applicantPayload.getAddressList();
 					logger.debug("PersonalAddLstApplicant - Occupation :"+ applicantOccupnAddrPayLoadLst);
 				} else if(addr.getAddressType().equalsIgnoreCase("Occupation") && 
 						(String.valueOf(addr.getCustDtlId()).equals(coApplicantCustId))){
 					AddressDetailsPayload coApplicantPayload  = gsonObj.fromJson(addr.getPayloadColumn(), AddressDetailsPayload.class);
 					coApplicantOccupnAddrPayLoadLst = coApplicantPayload.getAddressList();
 					logger.debug("PersonalAddLstCoApplicant - Occupation :"+ coApplicantOccupnAddrPayLoadLst);
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
 			
 			//Occupation Address
 			String occpnAddrApplicant = "";
 			String occpnAddrCoApplicant = "";
 			
 			
 			//Applicant Address
 			for (Address addr : applicantAddrPayLoadLst) {
 				if (addr.getAddressType().equalsIgnoreCase("present")) {
// 					presentAddressApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3()
// 							+ addr.getArea() +  addr.getLandMark() + addr.getCity() + addr.getDistrict() + addr.getState()
// 							+ addr.getCountry() + addr.getPinCode();
 					
 					presentAddressApplicant = getFullAddress(addr);
 					logger.debug("Present Address - Applicant : " + presentAddressApplicant);	
 					presentResidenceOwnership = addr.getResidenceOwnership();
 					presentAddressYears = addr.getResidenceAddressSince();
 					presntCityYears = addr.getResidenceCitySince();
 					presentResidenceAddressProof = addr.getCurrentAddressProof();
 					presentResidenceType = addr.getHouseType();
 				} else if (addr.getAddressType().equalsIgnoreCase("Permanent")) {
// 					permanetAddressApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3()
// 							+ addr.getArea()  + addr.getLandMark() + addr.getCity() + addr.getDistrict() + addr.getState()
// 							+ addr.getCountry() + addr.getPinCode();
 					permanetAddressApplicant = getFullAddress(addr);
 					logger.debug("Permanent Address - Applicant :" + permanetAddressApplicant);
 				} 		
 			}
 				
 			//Co-Applicant Address
 			if(coApplicantAddrPayLoadLst !=null) {
 				for(Address addr: coApplicantAddrPayLoadLst){
 					if(addr.getAddressType().equalsIgnoreCase("present")) {
// 						presentAddressCoApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3() + addr.getArea() + addr.getLandMark() + addr.getCity() + addr.getDistrict()+ addr.getState() + addr.getCountry() + addr.getPinCode();
 						presentAddressCoApplicant = getFullAddress(addr);
 						logger.debug("Present Address - Co-applicant :" + presentAddressCoApplicant);
 						presentResidenceOwnershipCo = addr.getResidenceOwnership();
 						presentAddressYearsCo = addr.getResidenceAddressSince();
 						presntCityYearsCo = addr.getResidenceCitySince();
 						presentResidenceAddressProofCo = addr.getCurrentAddressProof();
 						presentResidenceTypeCo = addr.getHouseType();
 					}else if(addr.getAddressType().equalsIgnoreCase("Permanent")) {
// 						permanetAddressCoApplicant = addr.getAddressLine1() + addr.getAddressLine2() + addr.getAddressLine3() + addr.getArea() + addr.getLandMark() + addr.getCity() + addr.getDistrict()+ addr.getState() + addr.getCountry() + addr.getPinCode();
 						permanetAddressCoApplicant = getFullAddress(addr);
 						logger.debug("permanent Address - Co-applicant. :" + permanetAddressCoApplicant);
 					}
 				}
 			}
 				
 			//Occupation Address
 			Address ocupnAddr = null;
 			Address ocupnAddrCo = null;
 			if (applicantOccupnAddrPayLoadLst != null && !applicantOccupnAddrPayLoadLst.isEmpty()) {
 			    ocupnAddr = applicantOccupnAddrPayLoadLst.get(0);
 			}
 			if (coApplicantOccupnAddrPayLoadLst != null && !coApplicantOccupnAddrPayLoadLst.isEmpty()) {
 				ocupnAddrCo = coApplicantOccupnAddrPayLoadLst.get(0);
 			}
// 			if(addr.getAddressType().equalsIgnoreCase("Office")) {
 				occpnAddrApplicant = getFullAddress(ocupnAddr);
 				occpnAddrCoApplicant = getFullAddress(ocupnAddrCo);
 			logger.debug("Ocupation Address Applicnt : " + occpnAddrApplicant);	
 			logger.debug("Ocupation Address Co-Applicnt : " + occpnAddrCoApplicant);	
// 				ocupnAddr.getAddressLine1() + ocupnAddr.getAddressLine2() + ocupnAddr.getAddressLine3()
// 				+ ocupnAddr.getArea() +  ocupnAddr.getLandMark() + ocupnAddr.getCity() + ocupnAddr.getDistrict() + ocupnAddr.getState()
// 				+ ocupnAddr.getCountry() + ocupnAddr.getPinCode();
 					
// 			}else if(addr.getAddressType().equalsIgnoreCase("Office")) {
// 				occpnAddrCoApplicant = ocupnAddrCo.getAddressLine1() + ocupnAddrCo.getAddressLine2() + ocupnAddrCo.getAddressLine3()
// 				+ ocupnAddrCo.getArea() +  ocupnAddrCo.getLandMark() + ocupnAddrCo.getCity() + ocupnAddrCo.getDistrict() + ocupnAddrCo.getState()
// 				+ ocupnAddrCo.getCountry() + ocupnAddrCo.getPinCode();
// 				}
 			
 				
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
 			
 			//Occupation Address
 			jsnObj.put("occpnAddrApplicant", occpnAddrApplicant);
 			jsnObj.put("occpnAddrCoApplicant", occpnAddrCoApplicant);
 			
 			
 		
 		}catch (Exception e) {
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
 	    String[] addressArr = {
 	        address.getAddressLine1(),
 	        address.getAddressLine2(),
 	        address.getAddressLine3(),
 	        address.getArea(),
 	        address.getLandMark(),
 	        address.getDistrict(),
 	        address.getCity(),
 	        address.getState(),
 	        address.getCountry(),
 	        address.getPinCode()
 	        
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
	
}
