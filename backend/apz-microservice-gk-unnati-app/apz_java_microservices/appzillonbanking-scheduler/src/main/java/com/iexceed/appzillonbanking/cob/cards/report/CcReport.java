package com.iexceed.appzillonbanking.cob.cards.report;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iexceed.appzillonbanking.cob.cards.domain.ab.CardDetails;
import com.iexceed.appzillonbanking.cob.cards.payload.ApplyCreditCardRequestFields;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.services.CommonParamService;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;

@Component
public class CcReport {

	@Autowired
    private CommonParamService commonService;
	
    public Response genratePdfService(ApplyCreditCardRequestFields customerDataFields) throws FileNotFoundException {
        Map<String, Object> param = new HashMap<>();
        String reportPath = CommonUtils.getExternalProperties("ccReport");
        String imagePath = CommonUtils.getExternalProperties("ccImages");
        ApplicationMaster applicationMaster = customerDataFields.getApplicationMaster();
        commonService.putLogoAndMaster(param, applicationMaster, 1, imagePath);
        int applicantsCount = applicationMaster.getApplicantsCount();
        commonService.putCustomerDtls(param, customerDataFields.getCustomerDetailsList(), applicantsCount);
        putCardDtls(param, customerDataFields.getCardDetails());
        commonService.putProfessionDtls(param, customerDataFields.getOccupationDetailsWrapperList());
        commonService.putAddressDtls(param, customerDataFields.getAddressDetailsWrapperList());
        return commonService.getReportResponse(param, reportPath);
    }


	private void putCardDtls(Map<String, Object> param, CardDetails cardDetails) {
		if(cardDetails != null){
            param.put("cardName", cardDetails.getCardName());
            param.put("nameOnCard", cardDetails.getNameOnCard());
            param.put("emailStmtReq", cardDetails.getEmailStmtReq());
            param.put("physicalStmtReq", cardDetails.getPhysicalStmtReq());
            if(!CommonUtils.isNullOrEmpty(cardDetails.getCreditLimit())) {
            	param.put("creditLimit", CommonUtils.formatAmount(Double.parseDouble(cardDetails.getCreditLimit())));
            }
            if(!CommonUtils.isNullOrEmpty(cardDetails.getWithdrawalLimit())) {
            	param.put("withdrawalLimit", CommonUtils.formatAmount(Double.parseDouble(cardDetails.getWithdrawalLimit())));
            }
            param.put("currency", cardDetails.getCurrency());
        }
	}
}