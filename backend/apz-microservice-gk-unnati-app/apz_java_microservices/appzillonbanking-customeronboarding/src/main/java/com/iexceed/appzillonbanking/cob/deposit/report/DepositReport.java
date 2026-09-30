package com.iexceed.appzillonbanking.cob.deposit.report;

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
import com.iexceed.appzillonbanking.cob.deposit.payload.CreateDepositRequestFields;

@Component
public class DepositReport {

	@Autowired
    private CommonParamService commonService;
	
    private static final Logger logger = LogManager.getLogger(DepositReport.class);

    public Response genratePdfService(CreateDepositRequestFields customerDataFields) throws FileNotFoundException {
        logger.info("GeneratePdfService start for deposit:: " + customerDataFields);
        String reportPath = CommonUtils.getExternalProperties("depositReport");
        String imagePath = CommonUtils.getExternalProperties("depositImages");
        ApplicationMaster applicationMaster = customerDataFields.getApplicationMaster();
        int applicantsCount = applicationMaster.getApplicantsCount();
        Map<String, Object> param = new HashMap<>();
        commonService.putLogoAndMaster(param, applicationMaster, applicantsCount, imagePath);
        commonService.putDepositDtls(param, customerDataFields.getDepositDetails());
        commonService.putAddressDtls(param, customerDataFields.getAddressDetailsWrapperList());
        commonService.putNomineeDtls(param, customerDataFields.getNomineeDetailsWrapperList());
        return commonService.getReportResponse(param, reportPath);
    }
}