package com.iexceed.appzillonbanking.cob.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.AddressDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;
import com.iexceed.appzillonbanking.cob.core.payload.Address;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsWrapper;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
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



public class InsuranceCoverageForLoan {
	private static final Logger logger = LogManager.getLogger(InsuranceCoverageForLoan.class); 
	private StyleBuilder borderedStyle, boldText, boldCenteredStyle, boldTextWithBorder, boldLeftStyle , rightStyle, leftStyle;

	static String space = "\u00a0\u00a0\u00a0";
	
	private String applicantCustId="";
	private String coApplicantCustId ="";
	private String applicantName = "";
	private String coApplicantName = "";

	int width30 = 30;
	int width70 = 70;
	private String productName = "";   
	
	public InsuranceCoverageForLoan() {
		 
		borderedStyle = stl.style(stl.penThin()).setPadding(5);
		boldTextWithBorder = stl.style(stl.penThin()).setPadding(5).bold();
		boldCenteredStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		boldText = stl.style().bold();
		boldLeftStyle = stl.style().bold().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		
	    rightStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
	    leftStyle = stl.style().setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
	    
	    
	}
	public String generatePdfForDbkit(JSONObject keysForContent, String filePath, CustomerDataFields custmrDataFields, List<Enach> enachDetails) throws DRException, IOException {
		
//		productName = productDetail;
		productName = "RENEWAL_LOAN_PRODUCT";
		
		/* Basic Application Details */	
		Gson gsonObj = new Gson();
		for(CustomerDetails custDtl : custmrDataFields.getCustomerDetailsList()) {
			logger.debug("customer Type : " + custDtl.getCustomerType());
			if(custDtl.getCustomerType().equalsIgnoreCase("Applicant")) { 
				 applicantCustId = String.valueOf(custDtl.getCustDtlId());
				 applicantName = custDtl.getCustomerName();
				 logger.debug("applicantCustId : " + applicantCustId);				
			}else if(custDtl.getCustomerType().equalsIgnoreCase("Co-App")) {
				coApplicantCustId = String.valueOf(custDtl.getCustDtlId());
				coApplicantName = custDtl.getCustomerName();
				logger.debug("coApplicantCustId : " + coApplicantCustId);
			}
		}
			 
		BigDecimal sactionAmtDb = custmrDataFields.getLoanDetails().getSanctionedLoanAmount();
		String sactionAmt = (sactionAmtDb == null) ? "" : sactionAmtDb.toPlainString();
		
		String loanId = custmrDataFields.getLoanDetails().getT24LoanId() != null ? custmrDataFields.getLoanDetails().getT24LoanId() : "";
		
		
		 CibilDetailsPayload cibilPayloadCoApp = new CibilDetailsPayload();
	        for (CibilDetailsWrapper cibilDetailsWrapper : custmrDataFields.getCibilDetailsWrapperList()) {
	            String custId = cibilDetailsWrapper.getCibilDetails().getCustDtlId().toString();
//		String customerType = applicant ? applicantCustId : coApplicantCustId;
	            logger.debug("cibilDetailsPayload Payload : " + cibilPayloadCoApp);
	            if (custId.equals(coApplicantCustId)) {
	                cibilPayloadCoApp = gsonObj.fromJson(
	                        cibilDetailsWrapper.getCibilDetails().getPayloadColumn(), CibilDetailsPayload.class);
	            }
	        }
	   
	    String tenure = cibilPayloadCoApp.getFinalTenure();
		
		String line2 = keysForContent.getString("line2");
		String finalLine2 = line2.replace("<applicantName>", applicantName).replace("<coApplicantName>", coApplicantName).replace("<productName>", productName).replace("<loanAccountNo>", loanId).replace("<sanctionedAmount>", CommonUtils.amountFormat(sactionAmt)+"/- ").replace("<loanTenure>", tenure);		logger.debug("finalLine2 : " + finalLine2);
		
		
		JasperReportBuilder report = new JasperReportBuilder();
		JasperReportBuilder subReport = new JasperReportBuilder();
		JasperReportBuilder subReport1 = new JasperReportBuilder();
		JasperReportBuilder subReport2 = new JasperReportBuilder();
		JasperReportBuilder subReport3 = new JasperReportBuilder();
		JasperReportBuilder subReport4 = new JasperReportBuilder();
		JasperReportBuilder subReport5 = new JasperReportBuilder();
		JasperReportBuilder subReport6 = new JasperReportBuilder();
		JasperReportBuilder subReport7 = new JasperReportBuilder();
		
		//from
		JSONObject addressDetailsObj = getAllAddressDeatils(custmrDataFields);
		String fromAddressAppnt = addressDetailsObj.getString("presentAddressApplicant");
		String fromAddressCoAppnt = addressDetailsObj.getString("presentAddressCoApplicant");
		
        
		report.setPageFormat(PageType.A4, PageOrientation.PORTRAIT).setPageMargin(DynamicReports.margin(30));

		/* Basic Application Details */
		subReport.title(cmp.text(keysForContent.getString("applicationName")).setMarkup(Markup.HTML).setStyle(boldCenteredStyle.setFontSize(14)));	
		subReport1.title(cmp.text(keysForContent.getString("date") + CommonUtils.getCurDate()).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport1.title(cmp.text(""));
		
		subReport1.title(cmp.text(keysForContent.getString("to")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport1.title(cmp.text(keysForContent.getString("toAddress1")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport1.title(cmp.text(keysForContent.getString("toAddress2")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport1.title(cmp.text(custmrDataFields.getApplicationMaster().getBranchName()));
		
		subReport2.title(cmp.text(keysForContent.getString("from")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport2.title(cmp.text(applicantName).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport2.title(cmp.text(fromAddressAppnt).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport2.title(cmp.text(coApplicantName).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport2.title(cmp.text(fromAddressCoAppnt).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport2.title(cmp.text(""));
		
		subReport3.title(cmp.text(keysForContent.getString("subject")).setMarkup(Markup.HTML).setStyle(leftStyle));
//		subReport3.title(cmp.text(""));
		subReport3.title(cmp.text(keysForContent.getString("line1")).setMarkup(Markup.HTML).setStyle(leftStyle));
//		subReport3.title(cmp.text(""));	
		subReport4.title(cmp.text(finalLine2).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport4.title(cmp.text(""));
		subReport4.title(getDeclarationTable(keysForContent)).title(cmp.text(""));
		subReport4.title(cmp.text(""));
		subReport5.title(cmp.text(keysForContent.getString("line3")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport5.title(cmp.text(""));
		subReport5.title(cmp.text(keysForContent.getString("line4")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport5.title(cmp.text(""));
		subReport5.title(cmp.text(keysForContent.getString("line5")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport5.title(cmp.text(""));
		subReport6.title(cmp.text(keysForContent.getString("line6")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport6.title(cmp.text(""));
		subReport6.title(cmp.text(keysForContent.getString("line7")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport6.title(cmp.text(""));
		subReport7.title(cmp.text(keysForContent.getString("line8")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport7.title(cmp.text(""));
		subReport7.title(cmp.text(keysForContent.getString("line9")).setMarkup(Markup.HTML).setStyle(leftStyle));
		subReport7.title(cmp.text(""));
		
		subReport7.title(
			    cmp.horizontalList(
			        cmp.text(keysForContent.getString("applicantName") + applicantName).setMarkup(Markup.HTML).setStyle(leftStyle),
			        cmp.text(keysForContent.getString("coApplicantName") + coApplicantName).setMarkup(Markup.HTML).setStyle(rightStyle)
			        )
				);
	
		subReport7.title(
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
		.addSummary(cmp.subreport(subReport6))
		.addSummary(cmp.subreport(subReport7));
		
		// .show();
	        FileOutputStream fos = new FileOutputStream(filePath);
	        try {
	            report.toPdf(fos);
	        } catch (DRException e) {
	            
	        }
	        fos.close();
	        byte[] inputfile = Files.readAllBytes(Paths.get(filePath));
	        byte[] encodedBytes = Base64.getEncoder().encode(inputfile);
	        return new String(encodedBytes);
				
	}


	private ComponentBuilder<?, ?> getDeclarationTable(JSONObject keysForContent) {

		VerticalListBuilder verticalList = cmp.verticalList();

		verticalList.add(createTwoHorizontalList(keysForContent.getString("availed"), keysForContent.getString("reasons"), width30, width70));
		verticalList.add(createTwoHorizontalList(keysForContent.getString("applicant"), keysForContent.getString("applicantReasons"), width30, width70));
		verticalList.add(createTwoHorizontalList(keysForContent.getString("coApplicant"), keysForContent.getString("coApplicantReasons"), width30, width70));
		
		return verticalList;
	}
	

	private ComponentBuilder<?, ?> createTwoHorizontalList(String Key, String value, int width1,
			int width2) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		horizontalList.add(cmp.text(Key).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(width1));
		horizontalList.add(cmp.text(value).setMarkup(Markup.HTML).setStyle(borderedStyle).setWidth(width2));

		return horizontalList;

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
 	

	
}
