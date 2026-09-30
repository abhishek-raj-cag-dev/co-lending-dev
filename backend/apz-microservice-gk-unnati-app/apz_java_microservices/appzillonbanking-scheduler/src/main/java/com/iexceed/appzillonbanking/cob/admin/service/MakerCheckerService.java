/**
 * @author akshay.upadhya
 * @date 13.06.2020
 */
package com.iexceed.appzillonbanking.cob.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iexceed.appzillonbanking.cob.admin.domain.ab.TbAbmiMakerChecker;
import com.iexceed.appzillonbanking.cob.admin.payload.MakerCheckerFetchPayloadRequest;
import com.iexceed.appzillonbanking.cob.admin.payload.MakerCheckerPayloadEditResponse;
import com.iexceed.appzillonbanking.cob.admin.payload.MakerCheckerPayloadEditResponseWrapper;
import com.iexceed.appzillonbanking.cob.admin.payload.MakerCheckerPayloadFetchResponse;
import com.iexceed.appzillonbanking.cob.admin.payload.MakerCheckerPayloadRequest;
import com.iexceed.appzillonbanking.cob.admin.repository.ab.TbAbmiMakerCheckerRepository;
import com.iexceed.appzillonbanking.cob.admin.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;

@Service
public class MakerCheckerService {
	
	private static final Logger logger = LogManager.getLogger(MakerCheckerService.class);
	
	@Autowired
	private TbAbmiMakerCheckerRepository tbAbmiMakerCheckerRepository;

	@Autowired
	private	MakerCheckerPayloadService makerCheckerPayloadService;

	public String insertUpdatePayload(MakerCheckerPayloadRequest makerCheckerPayloadRequest) {
		String lStatus = null;
		try {
			logger.warn("request inside insertUpdatePayload::" + makerCheckerPayloadRequest);
			String userAction = makerCheckerPayloadRequest.getUserAction();
			logger.warn("userAction is "+userAction);
			if ("ADD".equalsIgnoreCase(userAction)) {
				logger.warn("In add case validate if record already exists.");
				if (!makerCheckerPayloadService.validateRecordExist(makerCheckerPayloadRequest)) {
					logger.warn("1");
					lStatus = makerCheckerPayloadService.insertUpdatePayload(makerCheckerPayloadRequest, userAction);
				} else {
					logger.warn("2");
					logger.warn("Record already exists in master table.");
					lStatus = "recordExist";
				}
			} else {
				logger.warn("3");
				lStatus = makerCheckerPayloadService.insertUpdatePayload(makerCheckerPayloadRequest, userAction);
			}
		} catch (Exception e) {
			logger.error("Exception Occured while inserting/updating the payload:" , e);
			lStatus = CommonUtils.FALSE;
		}
		return lStatus;
	}

	public List<MakerCheckerPayloadFetchResponse> fetchAllData(String featureId, String userId) {
		List<MakerCheckerPayloadFetchResponse> mcPayloadFetchResp = new ArrayList<>();
		try {
			Iterable<TbAbmiMakerChecker> mcList;
			if(com.iexceed.appzillonbanking.cob.core.utils.CommonUtils.isNullOrEmpty(featureId)) {
				mcList = tbAbmiMakerCheckerRepository.findByMakerIdNotAndAuthStatusNot(userId, "A");
			} else {
				mcList = this.tbAbmiMakerCheckerRepository.findByFeatureId(featureId);	
			}
			for (TbAbmiMakerChecker mcFetch : mcList) {
				MakerCheckerPayloadFetchResponse mobj = new MakerCheckerPayloadFetchResponse();
				mobj.setId(mcFetch.getId());
				mobj.setFeatureId(mcFetch.getFeatureId());
				mobj.setAuthStatus(mcFetch.getAuthStatus());
				mobj.setPayload(mcFetch.getPayload());
				mobj.setUserAction(mcFetch.getUserAction());
				mobj.setMakerId(mcFetch.getMakerId());
				mobj.setMakerTs(mcFetch.getMakerTs().format(Constants.ADMINFORMATTER));
				mobj.setCheckerId(mcFetch.getCheckerId());
				mobj.setCheckerTs(mcFetch.getCheckerTs());
				mobj.setVersionNo(mcFetch.getVersionNo());
				mcPayloadFetchResp.add(mobj);
			}
		} catch (Exception e) {
			logger.error("Exception Occured while fetching the payload:", e);
		}
		return mcPayloadFetchResp;
	}

	/**
	 * @author akshay.upadhya
	 * @date 19.03.2021
	 * @param makerCheckerPayloadRequest
	 * @return Status of Authorization
	 */
	public String authorizeMakerCheckerRecord(MakerCheckerFetchPayloadRequest makerCheckerPayloadRequest) {
		String lStatus = null;
		try {
			logger.warn("Inside authorizeMCRecord:" + makerCheckerPayloadRequest);
			lStatus = makerCheckerPayloadService.authorizeMakerCheckerRecord(makerCheckerPayloadRequest);
		} catch (Exception e) {
			logger.error(CommonUtils.EXCEPTION_OCCURED, e);
			lStatus = CommonUtils.FAILURE_UC;
		}
		return lStatus;
	}

	public MakerCheckerPayloadEditResponseWrapper validateResponseStatus(
			MakerCheckerPayloadRequest makerCheckerPayloadRequest, String lStatus) {
		MakerCheckerPayloadEditResponseWrapper makerCheckerPayloadEditResponseWrapper = new MakerCheckerPayloadEditResponseWrapper();
		MakerCheckerPayloadEditResponse makerCheckerPayloadEditResponse = new MakerCheckerPayloadEditResponse();
		ResponseHeader respHeader = new ResponseHeader();
		try {
			if (CommonUtils.TRUE.equalsIgnoreCase(lStatus)) {
				makerCheckerPayloadEditResponse.setId(makerCheckerPayloadRequest.getId());
				makerCheckerPayloadEditResponse.setStatus(CommonUtils.SUCCESS_UC);
				makerCheckerPayloadEditResponse.setFeatureId(makerCheckerPayloadRequest.getFeatureId());
				makerCheckerPayloadEditResponse.setErrorCode("");
				makerCheckerPayloadEditResponse.setErrorMessage("");
				makerCheckerPayloadEditResponseWrapper.setMakerCheckerPayloadResponse(makerCheckerPayloadEditResponse);
				respHeader.setResponseCode(CommonUtils.SUCCESS_CODE);
				respHeader.setErrorCode("");
				respHeader.setResponseMessage("");
				makerCheckerPayloadEditResponseWrapper.setResponseHeader(respHeader);
			} else {
				respHeader.setResponseCode(CommonUtils.FAILURE_CODE);
				respHeader.setErrorCode(CommonUtils.FAILURE_CODE);
				if ("recordExist".equalsIgnoreCase(lStatus)) {
					respHeader.setResponseMessage("Record already exists.");
				} else if ("editNotAllowed".equalsIgnoreCase(lStatus)) {
					respHeader.setResponseMessage("User not allowed to edit unauthorized record.");
				} else {
					respHeader.setResponseMessage("Record insertion/updation failed");
				}
				makerCheckerPayloadEditResponseWrapper.setResponseHeader(respHeader);
			}
		} catch (Exception e) {
			logger.error(CommonUtils.EXCEPTION_OCCURED, e);
		}
		return makerCheckerPayloadEditResponseWrapper;
	}
}
