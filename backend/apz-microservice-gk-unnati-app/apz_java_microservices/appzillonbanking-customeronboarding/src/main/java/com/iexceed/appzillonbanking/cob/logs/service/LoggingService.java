package com.iexceed.appzillonbanking.cob.logs.service;

import com.iexceed.appzillonbanking.cob.constants.CommonConstants;
import com.iexceed.appzillonbanking.cob.core.utils.AESUtilsWithSha;
import com.iexceed.appzillonbanking.cob.logs.domain.apz.TbAslgTxnDetail;
import com.iexceed.appzillonbanking.cob.logs.domain.apz.TbAsmiSecurityParams;
import com.iexceed.appzillonbanking.cob.logs.domain.apz.TbAstpLdRecs;
import com.iexceed.appzillonbanking.cob.logs.domain.apz.TbAstpLdRecsPK;
import com.iexceed.appzillonbanking.cob.logs.payload.LargeData;
import com.iexceed.appzillonbanking.cob.logs.payload.LogData;
import com.iexceed.appzillonbanking.cob.logs.repository.apz.TbAslgTxnDetailRepository;
import com.iexceed.appzillonbanking.cob.logs.repository.apz.TbAsmiSecurityParamsRepository;
import com.iexceed.appzillonbanking.cob.logs.repository.apz.TbAstpLdRecsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class LoggingService {

	private static final Logger logger = LogManager.getLogger(LoggingService.class);

	@Autowired
	private TbAstpLdRecsRepository tbAstpLdRecsRepo;

	@Autowired
	private TbAslgTxnDetailRepository tbAslgTxnDetailRepository;

	@Autowired
	private TbAsmiSecurityParamsRepository tbAsmiSecurityParamsRepository;

	private TbAsmiSecurityParams securityParams = null;

	public void logTransactionDetails(LogData logData, JSONObject interfaceJsonContent) {

		logger.debug("Start: logTransactionDetails with request = " + logData);
		try {
			String appId = logData.getAppId();
			populateSecurityParams(appId);
			if (interfaceJsonContent != null) {

				String secLogRequired = securityParams.getFmwTxnReq();

				String servicePayloadLogReq = interfaceJsonContent.getString(CommonConstants.SERVICE_TXN_LOG_REQ);
				String servicePayloadLogMode = interfaceJsonContent.getString(CommonConstants.SERVICE_TXN_LOG_MODE);
				logger.debug("Interface Params for request Logging servicePayloadLogReq:" + servicePayloadLogReq
						+ ",servicePayloadLogMode" + servicePayloadLogMode);

				if (CommonConstants.YES.equalsIgnoreCase(secLogRequired)
						&& CommonConstants.YES.equalsIgnoreCase(servicePayloadLogReq)) {

					logger.debug("Storing the details in TB_ASLG_TXN_DETAIL table");
					updateFmwTxnDetailObj(logData);

					logger.debug("Storing the details in TB_ASTP_LD_RECS table");
					logReqResp(logData, servicePayloadLogMode);
				}
			} else {
				logger.debug("No Interface details found. Request and response will not be stored");
			}
		} catch (Exception e) {
			logger.error("Exception while logging the request and the response. ", e);
		}
	}

	private void updateFmwTxnDetailObj(LogData logData) {
		TbAslgTxnDetail txnDetail = new TbAslgTxnDetail();
		logger.debug("Start: updateFmwTxnDetailObj with request = " + logData);
		String status = CommonConstants.SUCCESS_FLAG_S;

		if ("F".equalsIgnoreCase(logData.getStatus())) {
			status = CommonConstants.FAILURE_FLAG_F;
		}

		txnDetail.setCreateTs(LocalDateTime.now());
		txnDetail.setEndpointType(logData.getEndpointType());
		txnDetail.setEndTm(logData.getEndTm());
		txnDetail.setInterfaceId(logData.getInterfaceId());
		txnDetail.setMasterTxnRef(logData.getMasterTxnRefNo());
		txnDetail.setReqLdRefNo(logData.getRequestRefNo());
		txnDetail.setReqNoRecs(logData.getRequestSize());
		txnDetail.setResLdRefNo(logData.getResponseRefNo());
		txnDetail.setResNoRecs(logData.getResponseSize());
		txnDetail.setStatus(status);
		txnDetail.setStTm(logData.getStTm());
		txnDetail.setTxnRef(logData.getUserId() + System.currentTimeMillis() + getRandomNumeric(100000));
		txnDetail.setAppId(logData.getAppId());
		txnDetail.setExtEndTm(logData.getEndTm());
		txnDetail.setExtStTm(logData.getStTm());
		txnDetail.setSource(CommonConstants.SOURCE_APZ);
		txnDetail.setDeviceId(logData.getDeviceId());
		txnDetail.setTxnStatus(status);
		txnDetail.setUserId(logData.getUserId());
		logger.debug("TB_ASLG_TXN_DETAIL db request = " + txnDetail);
		tbAslgTxnDetailRepository.save(txnDetail);
		logger.debug("End: updateFmwTxnDetailObj");
	}

	public static String getRandomNumeric(int count) {
		SecureRandom rand = new SecureRandom();
		return String.valueOf(rand.nextInt(count));
	}

	private void logReqResp(LogData logData, String servicePayloadLogMode) {
		String userId = logData.getUserId();
		String reqRefNo = "";
		int reqSize = 0;
		String resRefNo = "";
		int resSize = 0;

		String payloadLogLevel = servicePayloadLogMode;

		if (reqLogRequired(payloadLogLevel)) {
			List<LargeData> reqLdRecList;
			String encRequest = encryptData(logData.getAppId(), userId, logData.getDeviceId(), logData.getRequest());
			reqLdRecList = getPayloadList(encRequest, userId);
			persistLdRecs(reqLdRecList);
			reqRefNo = !reqLdRecList.isEmpty() ? reqLdRecList.get(0).getRefNo() : "";
			reqSize = reqLdRecList.size();
		}

		if (respLogRequired(payloadLogLevel)) {
			List<LargeData> resLdRecList;
			String encResponse = encryptData(logData.getAppId(), userId, logData.getDeviceId(), logData.getResponse());
			resLdRecList = getPayloadList(encResponse, userId);
			persistLdRecs(resLdRecList);
			resRefNo = !resLdRecList.isEmpty() ? resLdRecList.get(0).getRefNo() : "";
			resSize = resLdRecList.size();
		}

		logData.setRequestRefNo(reqRefNo);
		logData.setRequestSize(reqSize);
		logData.setResponseRefNo(resRefNo);
		logData.setResponseSize(resSize);

	}

	private void persistLdRecs(List<LargeData> ldRecList) {
		TbAstpLdRecs tbAstpLdRecs = null;
		TbAstpLdRecsPK bAstpLdRecsPK = null;
		List<TbAstpLdRecs> ldRecs = new ArrayList<>();
		for (LargeData recs : ldRecList) {
			bAstpLdRecsPK = new TbAstpLdRecsPK(recs.getRefNo(), recs.getSeqNo());
			tbAstpLdRecs = new TbAstpLdRecs(bAstpLdRecsPK, recs.getData1(), recs.getData2(), recs.getData3(),
					recs.getData4(), recs.getData5());
			ldRecs.add(tbAstpLdRecs);

			logger.debug("persistLdRecs db request = " + tbAstpLdRecs);
			tbAstpLdRecsRepo.save(tbAstpLdRecs);
		}
	}

	private boolean reqLogRequired(String payloadLogLevel) {
		boolean flag = false;
		if (CommonConstants.LOG_LEVEL_BOTH.equals(payloadLogLevel)
				|| CommonConstants.LOG_LEVEL_REQUEST.equals(payloadLogLevel)) {
			flag = true;
		}
		return flag;
	}

	private boolean respLogRequired(String payloadLogLevel) {
		boolean flag = false;
		if (CommonConstants.LOG_LEVEL_BOTH.equals(payloadLogLevel)
				|| CommonConstants.LOG_LEVEL_RESPONSE.equals(payloadLogLevel)) {
			flag = true;
		}
		return flag;
	}

	private static List<LargeData> getPayloadList(String payload, String userId) {
		List<LargeData> ldRecsList = new ArrayList<>();
		LargeData ldrec = null;
		int i = 0;
		int maxTxnLoglen = CommonConstants.MAX_TXN_LOG_LEN;
		int seqNo = 0;
		int payloadlen = payload.length();

		String refNo = getTxnRefNum(userId);
		while (i < payloadlen) {
			ldrec = new LargeData();
			ldrec.setRefNo(refNo);
			ldrec.setSeqNo(++seqNo);
			ldrec.setData1(getPayload(i, maxTxnLoglen, payloadlen, payload));
			i += maxTxnLoglen;
			ldrec.setData2(getPayload(i, maxTxnLoglen, payloadlen, payload));
			i += maxTxnLoglen;
			ldrec.setData3(getPayload(i, maxTxnLoglen, payloadlen, payload));
			i += maxTxnLoglen;
			ldrec.setData4(getPayload(i, maxTxnLoglen, payloadlen, payload));
			i += maxTxnLoglen;
			ldrec.setData5(getPayload(i, maxTxnLoglen, payloadlen, payload));
			i += maxTxnLoglen;
			ldRecsList.add(ldrec);
		}
		return ldRecsList;
	}

	private String encryptData(String pAppId, String pUserId, String pDevciceId, String pAppzillonBody) {
		logger.debug("encryption key = " + pAppId + pUserId + pDevciceId);
		return AESUtilsWithSha.encryptString(pAppId + pUserId + pDevciceId, pAppzillonBody);
	}

	private static String getTxnRefNum(String pUserId) {
		SecureRandom random = new SecureRandom();
		int randomno = random.nextInt(1000000);
		if (isNullOrEmpty(pUserId))
			return System.currentTimeMillis() + "" + randomno;
		else
			return (pUserId + System.currentTimeMillis() + "" + randomno);
	}

	private static boolean isNullOrEmpty(String pValue) {

		return pValue == null || pValue.isEmpty();
	}

	private static String getPayload(int stindex, int maxTxnLoglen, int payloadlen, String payload) {
		return (stindex > payloadlen) ? "" : payload.substring(stindex, Math.min(payloadlen, stindex + maxTxnLoglen));
	}

	public void populateSecurityParams(String appId) {
		if (securityParams == null) {
			securityParams = tbAsmiSecurityParamsRepository.findSecurityParamsbyAppId(appId);
		}
	}
}
