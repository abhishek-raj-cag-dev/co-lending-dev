package com.iexceed.appzillonbanking.cob.loans.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.LUCEntity;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.LucRepository;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.loans.payload.LucUploadLoanRequest;
import com.iexceed.appzillonbanking.cob.loans.payload.LucUploadLoanRequestFields;
import com.iexceed.appzillonbanking.cob.payload.CustomerDataFields;
import com.iexceed.appzillonbanking.cob.service.COBService;


@Service
public class LucService {
    private static final Logger logger = LogManager.getLogger(LucService.class);
    
    private final LucRepository lucRepository;
    private final ApplicationMasterRepository applicationMasterRepo;
    private final COBService cobService;
    
    public LucService(LucRepository lucRepository,ApplicationMasterRepository applicationMasterRepo,COBService cobService){
    	this.lucRepository=lucRepository;
    	this.applicationMasterRepo=applicationMasterRepo;
    	this.cobService=cobService;
    }
	
	public Response LucUploadData(LucUploadLoanRequest apirequest, boolean isQueryUpdate) {
		logger.debug("OnEntry :: LucUploadLoan :" + apirequest.toString());
		Gson gson = new Gson();
		Response fetchUserDetailsResponse = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		fetchUserDetailsResponse.setResponseHeader(responseHeader);
		ResponseBody responseBody = new ResponseBody();

		try {
			LucUploadLoanRequestFields requestObj = apirequest.getRequestObj();
			logger.debug("requestObj - LUC : " + requestObj.toString());
			String applicationId = requestObj.getApplicationId();
			String payLoad = gson.toJson(requestObj.getPayload());

			List<ApplicationMaster> appData = applicationMasterRepo.findByAppIdAndApplicationId(requestObj.getAppId(),
					applicationId);
			if (appData.isEmpty()) {
				logger.debug("ApplicationMaster data not found!");
				responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
				responseBody.setResponseObj("ApplicationMaster data not found!!");
				fetchUserDetailsResponse.setResponseHeader(responseHeader);
				fetchUserDetailsResponse.setResponseBody(responseBody);
				return fetchUserDetailsResponse;
			}

			Optional<LUCEntity> lucData = lucRepository.findByApplicationIdAndAppId(applicationId,
					requestObj.getAppId());
			if (!lucData.isPresent()) {
				logger.debug("LUC data not found!");
							
				LUCEntity lucUpload = LUCEntity.builder().applicationId(applicationId).appId(requestObj.getAppId())
						.versionNo(requestObj.getVersionNum()).payload(payLoad).createdBy(requestObj.getCreatedBy())
						.updatedBy(requestObj.getUpdatedBy()).createTs(LocalDateTime.now()).updateTs(LocalDateTime.now())
						.queries(requestObj.getQueries())
						.build();
				lucRepository.save(lucUpload);
				logger.debug("LUC data object is created in table");

			} else {
				logger.debug("LUC data found");
				LUCEntity lucUpload = lucData.get();
				logger.debug("requestObj.getQueries(): " + requestObj.getQueries());
				lucUpload.setQueries(requestObj.getQueries());
			
				if (isQueryUpdate) {
					lucUpload.setPayload(payLoad);
					lucUpload.setVersionNo(requestObj.getVersionNum());
					lucUpload.setUpdatedBy(requestObj.getUpdatedBy());
					lucUpload.setUpdateTs(LocalDateTime.now());
				}
				logger.debug("final queries " + lucUpload.getQueries());
				logger.debug("final lucUpload : " + lucUpload.toString());
				lucRepository.save(lucUpload);
				logger.debug("LUC data updated");
			}
			CustomerDataFields custData = cobService.getCustomerData(appData.get(0), applicationId,
					requestObj.getAppId(), requestObj.getVersionNum());
			String customerdata = gson.toJson(custData);
			responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
			responseBody.setResponseObj(customerdata);
			fetchUserDetailsResponse.setResponseHeader(responseHeader);
			fetchUserDetailsResponse.setResponseBody(responseBody);
			return fetchUserDetailsResponse;

		} catch (Exception e) {
			logger.error("Exception in uploadLoan : {}", e.getMessage(), e);
			responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
			responseBody.setResponseObj(e.getMessage());
			fetchUserDetailsResponse.setResponseHeader(responseHeader);
			fetchUserDetailsResponse.setResponseBody(responseBody);
			return fetchUserDetailsResponse;
		}

	}
}
