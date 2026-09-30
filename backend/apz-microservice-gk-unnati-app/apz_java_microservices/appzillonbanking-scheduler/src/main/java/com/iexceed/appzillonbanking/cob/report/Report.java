package com.iexceed.appzillonbanking.cob.report;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.domain.ab.BankingFacilities;
import com.iexceed.appzillonbanking.cob.domain.ab.CRSDetails;
import com.iexceed.appzillonbanking.cob.domain.ab.FatcaDetails;
import com.iexceed.appzillonbanking.cob.payload.BankingFacilitiesPayload;
import com.iexceed.appzillonbanking.cob.payload.CRSDetailsPayload;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;
import com.iexceed.appzillonbanking.cob.payload.FatcaDetailsPayload;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

@Component
public class Report {
	
	@Autowired
    private CommonParamService commonService;

    private static final Logger logger = LogManager.getLogger(Report.class);
    
    private String check="check";
    private String checkNot="checkNot";
    private String value1="value1";
    private String value2="value2";
    private String value3="value3";
    private String value4="value4";
    private String key1="key1";
    private String key2="key2";
    private String key3="key3";
    private String key4="key4"; 
    
    

    public Response genratePdfService(CustomerDataFields customerDataFields) throws FileNotFoundException {
        logger.info("GeneratePdfService start:: " + customerDataFields);
        Response response;
        Map<String, Object> param = new HashMap<>();
        String reportPath = CommonUtils.getExternalProperties("casaReport");
        ApplicationMaster applicationMaster = customerDataFields.getApplicationMaster();
        int applicantsCount = applicationMaster.getApplicantsCount();
        String imagePath = CommonUtils.getExternalProperties("casaImages");
   	 	String checkbox = ResourceUtils.getFile(imagePath + "checkbox.png").getAbsolutePath();
        String checkboxUnselected = ResourceUtils.getFile(imagePath + "checkboxUnselected.png").getAbsolutePath();
        commonService.putLogoAndMaster(param, applicationMaster, applicantsCount, imagePath);
        commonService.putCustomerDtls(param, customerDataFields.getCustomerDetailsList(), applicantsCount);
        commonService.putProfessionDtls(param, customerDataFields.getOccupationDetailsWrapperList());
        commonService.putAddressDtls(param, customerDataFields.getAddressDetailsWrapperList());
        putBankingFacDtls(param, customerDataFields.getBankingFacilityList());
        putFatcaDtls(param, customerDataFields.getFatcaDetailsList(), checkbox, checkboxUnselected);
        putCrsDtls(param, customerDataFields.getCrsDetailsList(), checkbox, checkboxUnselected);
        commonService.putNomineeDtls(param, customerDataFields.getNomineeDetailsWrapperList());
        commonService.putDepositDtls(param, customerDataFields.getDepositDetails());
        commonService.putLoanDtls(param, customerDataFields.getLoanDetails());
        // page number
        for (int i = 1; i <= 3; i++) {
            param.put("p" + i, "Page " + i + " of 3");
        }

        List<JasperPrint> jasperPrintList = new ArrayList<>();
        try {
            JasperPrint jasperPrint1 = JasperFillManager.fillReport(
                    ResourceUtils.getFile(reportPath + "Page_1.jasper").getAbsolutePath(), param,
                    new JREmptyDataSource());
            jasperPrintList.add(jasperPrint1);
            logger.debug("Page 1 COMPLETED");

            JasperPrint jasperPrint2 = JasperFillManager.fillReport(
                    ResourceUtils.getFile(reportPath + "Page_2.jasper").getAbsolutePath(),
                    param, new JREmptyDataSource());
            jasperPrintList.add(jasperPrint2);
            logger.debug("Page 2 COMPLETED");

            JasperPrint jasperPrint3;
            if (customerDataFields.getDepositDetails() != null) {
                jasperPrint3 = JasperFillManager.fillReport(
                        ResourceUtils.getFile(reportPath + "Page_4.jasper").getAbsolutePath(), param,
                        new JREmptyDataSource());
            }else if(customerDataFields.getLoanDetails() != null){
                jasperPrint3 = JasperFillManager.fillReport(
                        ResourceUtils.getFile(reportPath + "Page_5.jasper").getAbsolutePath(), param,
                        new JREmptyDataSource());
            }else{
                jasperPrint3 = JasperFillManager.fillReport(
                        ResourceUtils.getFile(reportPath + "Page_3.jasper").getAbsolutePath(), param,
                        new JREmptyDataSource());
            }
            jasperPrintList.add(jasperPrint3);
            logger.debug("Page 3 COMPLETED");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.debug("Exporter starts");
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
        ByteArrayOutputStream pdfReportStream = new ByteArrayOutputStream();
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfReportStream));
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        configuration.setCreatingBatchModeBookmarks(true);
        exporter.setConfiguration(configuration);
        try {
            exporter.exportReport();
            logger.debug("Exporter ends");
            String base64String = Base64.getEncoder().encodeToString(pdfReportStream.toByteArray());
            response = commonService.getSuccessJson(base64String);
            logger.info("PDF Report Generated");
        } catch (JRException e) {
            response = commonService.getFailureJson(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        logger.debug("generatePdfService Function end");
        return response;
    }

	private void putCrsDtls(Map<String, Object> param, List<CRSDetails> crsDetailsList, String checkbox, String checkboxUnselected) {
    	 if (!crsDetailsList.isEmpty()) {
             for (CRSDetails crsDetails:crsDetailsList) {
                 Gson gsonObj = new Gson();
                 putCrsDtlsPayload(param, gsonObj.fromJson(crsDetails.getPayloadColumn(), CRSDetailsPayload.class), crsDetails, checkbox, checkboxUnselected);
             }
         }
	}

	private void putCrsDtlsPayload(Map<String, Object> param, CRSDetailsPayload crsPayload, CRSDetails crsDetails, String checkbox, String checkboxUnselected) {
		if (crsPayload != null) {
            if (crsDetails.getCustDtlId() != null) {
                if (crsPayload.getOtherCountryTaxResidant().equalsIgnoreCase("Y")) {
                	putCrsDtlsPayloadForOthrCountry1(param, checkbox, checkboxUnselected, crsPayload);
                } else {
                    param.put("crsDeclare0", checkboxUnselected);
                    param.put("otherTaxResi0", "No");
                }
            }
            if (crsDetails.getCustDtlId() != null) {
                if (crsPayload.getOtherCountryTaxResidant().equalsIgnoreCase("Y")) {
                	putCrsDtlsPayloadForOthrCountry2(param, checkbox, checkboxUnselected, crsPayload);
                    
                } else {
                    param.put("crsDeclar1", checkboxUnselected);
                    param.put("otherTaxResi1", "No");
                }
            }
        }
	}

	private void putCrsDtlsPayloadForOthrCountry2(Map<String, Object> param, String checkbox, String checkboxUnselected, CRSDetailsPayload crsPayload) {
		param.put("crsDeclar1", checkbox);
        param.put("otherTaxResi1", "Yes");
        HashMap<String, String> hm= new HashMap<>();
        hm.put(value1, crsPayload.getTaxDetailsList().get(0).getCountry());
        hm.put(value2, crsPayload.getTaxDetailsList().get(0).getTin());
        hm.put(value3, crsPayload.getTaxDetailsList().get(0).getTinType());
        hm.put(value4, crsPayload.getTaxDetailsList().get(0).getReason());
        for (int j = 11; j < 16; j++) {
        	param.put("countryTaxResi" + j, "-");
            param.put("tinNo" + j, "-");
            param.put("tinType" + j, "-");
            param.put("reasonNoTin" + j, "-");
        }
            hm.put(key1, "countryTaxResi11");
            hm.put(key2, "tinNo11");
            hm.put(key3, "tinType11");
            hm.put(key4, "reasonNoTin11");
            putIntoMapTin(param, hm);
        	hm.put(key1, "countryTaxResi12");
            hm.put(key2, "tinNo12");
            hm.put(key3, "tinType12");
            hm.put(key4, "reasonNoTin12");
        	putIntoMapTin(param, hm);
        	hm.put(key1, "countryTaxResi13");
            hm.put(key2, "tinNo13");
            hm.put(key3, "tinType13");
            hm.put(key4, "reasonNoTin13");
        	putIntoMapTin(param, hm);
        	hm.put(key1, "countryTaxResi14");
            hm.put(key2, "tinNo14");
            hm.put(key3, "tinType14");
            hm.put(key4, "reasonNoTin14");
        	putIntoMapTin(param, hm);
        	hm.put(key1, "countryTaxResi15");
            hm.put(key2, "tinNo15");
            hm.put(key3, "tinType15");
            hm.put(key4, "reasonNoTin15");
        	putIntoMapTin(param, hm);
	}

	private void putCrsDtlsPayloadForOthrCountry1(Map<String, Object> param, String checkbox, String checkboxUnselected, CRSDetailsPayload crsPayload) {
		param.put("crsDeclare0", checkbox);
        param.put("otherTaxResi0", "Yes");
        HashMap<String, String> hm= new HashMap<>();
        hm.put(value1, crsPayload.getTaxDetailsList().get(0).getCountry());
        hm.put(value2, crsPayload.getTaxDetailsList().get(0).getTin());
        hm.put(value3, crsPayload.getTaxDetailsList().get(0).getTinType());
        hm.put(value4, crsPayload.getTaxDetailsList().get(0).getReason());
        
            for (int k = 0; k < 5; k++) {
                param.put("countryTaxResi" + k, "-");
                param.put("tinNo" + k, "-");
                param.put("tinType" + k, "-");
                param.put("reasonNoTin" + k, "-");
            }
            hm.put(key1, "countryTaxResi0");                               
            hm.put(key2, "tinNo0");
            hm.put(key3, "tinType0");
            hm.put(key4, "reasonNoTin0");
            putIntoMapTin(param, hm);
        
        	 hm.put(key1, "countryTaxResi1");
             hm.put(key2, "tinNo1");
             hm.put(key3, "tinType1");
             hm.put(key4, "reasonNoTin1");
             putIntoMapTin(param, hm);
             
        	hm.put(key1, "countryTaxResi2");
            hm.put(key2, "tinNo2");
            hm.put(key3, "tinType2");
            hm.put(key4, "reasonNoTin2");
        	putIntoMapTin(param, hm);
           
        	hm.put(key1, "countryTaxResi3");
            hm.put(key2, "tinNo3");
            hm.put(key3, "tinType3");
            hm.put(key4, "reasonNoTin3");
        	putIntoMapTin(param, hm);
            
        	hm.put(key1, "countryTaxResi4");
            hm.put(key2, "tinNo4");
            hm.put(key3, "tinType4");
            hm.put(key4, "reasonNoTin4");
        	putIntoMapTin(param, hm);
            
		
	}

	private void putFatcaDtls(Map<String, Object> param, List<FatcaDetails> fatcaDetailsList, String checkbox, String checkboxUnselected) {
    	if (!fatcaDetailsList.isEmpty()) {
            for (int i = 0; i < fatcaDetailsList.size(); i++) {
                Gson gsonObj = new Gson();
                FatcaDetailsPayload fatcaPayload = gsonObj.fromJson(fatcaDetailsList.get(i).getPayloadColumn(), FatcaDetailsPayload.class);
                if(fatcaPayload != null){
                    if (fatcaPayload.getUsCitizenFlag().equalsIgnoreCase("Y")) {
                        param.put(checkNot + i, checkbox);
                        param.put(check + i, checkboxUnselected);
                    } else {
                        param.put(checkNot + i, checkboxUnselected);
                        param.put(check + i, checkbox);
                    }
                    param.put("documentIdName" + i, fatcaPayload.getDocumentIdName());
                    param.put("documentIdValue" + i, fatcaPayload.getDocumentIdValue());
                }
            }
        }
	}

	private void putBankingFacDtls(Map<String, Object> param, List<BankingFacilities> bankingFacilityList) {
    	if (!bankingFacilityList.isEmpty()) {
            for (BankingFacilities bankingFacilities:bankingFacilityList) {
                Gson gsonObj = new Gson();
                BankingFacilitiesPayload bankingPayload = gsonObj.fromJson(bankingFacilities.getPayloadColumn(), BankingFacilitiesPayload.class);
                if (bankingFacilities.getCustDtlId() != null) {
                    param.put("branchName", bankingPayload.getBranchName());
                    param.put("branchAddress", bankingPayload.getBranchAddress());
                    param.put("branchCode", bankingPayload.getBranchCode());
                    param.put("chequeBookRequired", getFullName(bankingPayload.getChequeBookRequired()));
                    param.put("debitCardRequired", getFullName(bankingPayload.getDebitCardRequired()));
                    param.put("debitCardNameSameAsNID", getFullName(bankingPayload.getDebitCardNameSameAsNID()));
                    param.put("nameOnCard", bankingPayload.getNameOnCard());
                    param.put("mbRequired", getFullName(bankingPayload.getMbRequired()));
                    param.put("ibRequired", getFullName(bankingPayload.getIbRequired()));
                    param.put("smsAlertsRequired", getFullName(bankingPayload.getSmsAlertsRequired()));
                    param.put("eStmtRequired", getFullName(bankingPayload.getEStmtRequired()));
                    param.put("passBookRequired", getFullName(bankingPayload.getPassBookRequired()));
                }
            }
        }
	}

	private void putIntoMapTin(Map<String, Object> param, HashMap<String, String> hm) {
    	String keyOne=hm.get(key1);
    	String valueOne=hm.get(value1);
    	String keyTwo=hm.get(key2);
    	String valueTwo=hm.get(value2);
    	String keyThree=hm.get(key3);
    	String valueThree=hm.get(value3);
    	String keyFour=hm.get(key4);
    	String valueFour=hm.get(value4);
    	putIntoMap(param, keyOne, valueOne);	
    	putIntoMap(param, keyTwo, valueTwo);	
    	putIntoMap(param, keyThree, valueThree);	
    	putIntoMap(param, keyFour, valueFour);	
    }

	private void putIntoMap(Map<String, Object> param, String key, String value) {
    	 param.put(key, value);	
	}

	private String getFullName(String name){
        if("Y".equalsIgnoreCase(name)){
            return "Yes";
        }else{
            return "No";
        }
    }
}