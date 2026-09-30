package com.iexceed.appzillonbanking.cob.loans.report;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.loans.payload.ApplyLoanRequestFields;

@Component
public class LoanReport {

    private static final Logger logger = LogManager.getLogger(LoanReport.class);

    @Autowired
    private CommonParamService commonService;
    
    public Response genratePdfService(ApplyLoanRequestFields customerDataFields) throws FileNotFoundException {
        logger.info("GeneratePdfService start for loans:: " + customerDataFields);
        ApplicationMaster applicationMaster = customerDataFields.getApplicationMaster();
        int applicantsCount = applicationMaster.getApplicantsCount();
        Map<String, Object> param = new HashMap<>();
        String reportPath = CommonUtils.getExternalProperties("loanReport");
        String imagePath = CommonUtils.getExternalProperties("loanImages");
        commonService.putLogoAndMaster(param, applicationMaster, applicantsCount, imagePath);
        commonService.putCustomerDtls(param, customerDataFields.getCustomerDetailsList(), applicantsCount);
        commonService.putLoanDtls(param, customerDataFields.getLoanDetails());
        commonService.putProfessionDtls(param, customerDataFields.getOccupationDetailsWrapperList());
        commonService.putAddressDtls(param, customerDataFields.getAddressDetailsWrapperList());
        return commonService.getReportResponse(param, reportPath);
    }
}