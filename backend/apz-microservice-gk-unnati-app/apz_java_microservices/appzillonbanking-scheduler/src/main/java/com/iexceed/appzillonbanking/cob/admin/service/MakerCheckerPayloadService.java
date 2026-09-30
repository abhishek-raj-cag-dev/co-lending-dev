/**
 * @author akshay.upadhya
 * @date 19.03.2021
 */
package com.iexceed.appzillonbanking.cob.admin.service;

import com.iexceed.appzillonbanking.cob.admin.domain.ab.TbAbmiMakerChecker;
import com.iexceed.appzillonbanking.cob.admin.domain.ab.TbAbmiMakerCheckerHistory;
import com.iexceed.appzillonbanking.cob.admin.domain.ab.TbAbmiMakerCheckerIds;
import com.iexceed.appzillonbanking.cob.admin.payload.MakerCheckerFetchPayloadRequest;
import com.iexceed.appzillonbanking.cob.admin.payload.MakerCheckerPayloadRequest;
import com.iexceed.appzillonbanking.cob.admin.repository.ab.TbAbmiMakerCheckerHistoryRepository;
import com.iexceed.appzillonbanking.cob.admin.repository.ab.TbAbmiMakerCheckerRepository;
import com.iexceed.appzillonbanking.cob.admin.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.domain.ab.TbAbmiCommonCodeDomain;
import com.iexceed.appzillonbanking.cob.core.repository.ab.TbAbmiCommonCodeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MakerCheckerPayloadService {

	@Autowired
	private TbAbmiMakerCheckerRepository tbAbmiMakerCheckerRepository;

	@Autowired
	private	TbAbmiMakerCheckerHistoryRepository tbAbmiMakerCheckerHistoryRepository;

	@Autowired
	private	TbAbmiCommonCodeRepository tbAbmiCommonCodeRepository;

	private static final Logger logger = LogManager.getLogger(MakerCheckerPayloadService.class);

	/**
	 * @author akshay.upadhya
	 * @date 19.03.2021 This is the common function to insert/update the payload
	 *       data in TB_ABOB_MAKER_CHECKER table
	 * @param makerCheckerPayloadRequest, serviceType, controlType
	 * @return Status of insertion/updation of the record.
	 */
	public String insertUpdatePayload(MakerCheckerPayloadRequest makerCheckerPayloadRequest, String controlActionType) {
		String lStatus = null;
		String authStatus = "U";
		int versionNo = 1;
		try {
			String uniqueValue = Long.toString(System.currentTimeMillis());
			if ("ADD".equalsIgnoreCase(controlActionType)) {
				logger.warn("Inside Payload add case.");
				String id = makerCheckerPayloadRequest.getFeatureId() + uniqueValue;
				makerCheckerPayloadRequest.setId(id);
				updateMakerCheckerTable(makerCheckerPayloadRequest, controlActionType, authStatus, versionNo);
				logger.warn("Data stored succesfully in Maker Checker on Add.");
				lStatus = CommonUtils.TRUE;
			} else if ("EDIT".equalsIgnoreCase(controlActionType)) {
				Optional<TbAbmiMakerChecker> tbAbmiMakerCheckerData = tbAbmiMakerCheckerRepository.findById(makerCheckerPayloadRequest.getId());
				if (tbAbmiMakerCheckerData.isPresent()) {
					TbAbmiMakerChecker tbAbmiMakerChecker = tbAbmiMakerCheckerData.get();
					logger.warn("Data available in TbAbmiMakerChecker.");
					String reqUserId = makerCheckerPayloadRequest.getMakerId();
					String dbUserId = tbAbmiMakerChecker.getMakerId();
					makerCheckerPayloadRequest.setId(tbAbmiMakerChecker.getId());
					makerCheckerPayloadRequest.setMakerId(dbUserId);
					makerCheckerPayloadRequest.setFeatureId(tbAbmiMakerChecker.getFeatureId());
					logger.warn("tbAbmiMakerChecker.getAuthStatus() is "+tbAbmiMakerChecker.getAuthStatus());
					logger.warn("reqUserId is "+reqUserId);
					logger.warn("dbUserId is "+dbUserId);
					if ("U".equalsIgnoreCase(tbAbmiMakerChecker.getAuthStatus())) {
						if (!reqUserId.equalsIgnoreCase(dbUserId)) {
							logger.warn("4");
							lStatus = "editNotAllowed";
						} else {
							logger.warn("5");
							versionNo = tbAbmiMakerChecker.getVersionNo();
							tbAbmiMakerCheckerRepository.deleteById(new TbAbmiMakerCheckerIds(tbAbmiMakerChecker.getId(), tbAbmiMakerChecker.getVersionNo()));
							logger.warn("Deleting the existing record and updating the entry in MakerChecker table");
							boolean isUpdated = updateMakerCheckerTable(makerCheckerPayloadRequest, controlActionType, authStatus, versionNo);
							if (isUpdated) {
								logger.warn("Record saved successfully after edit.");
								lStatus = CommonUtils.TRUE;
							} else {
								logger.warn("Failure in saving the record after edit.");
								lStatus = CommonUtils.FAILURE;
							}
						}
					} else if ("A".equalsIgnoreCase(tbAbmiMakerChecker.getAuthStatus())) {
						logger.warn("Authorized record edit by the Maker, increment the version number.");
						makerCheckerPayloadRequest.setMakerId(reqUserId);
						versionNo = tbAbmiMakerChecker.getVersionNo() + 1;
						tbAbmiMakerCheckerRepository.deleteById(new TbAbmiMakerCheckerIds(tbAbmiMakerChecker.getId(), tbAbmiMakerChecker.getVersionNo()));
						logger.warn("Deleting the existing record and updating the entry in MakerChecker table");
						boolean isModified = updateMakerCheckerTable(makerCheckerPayloadRequest, controlActionType, authStatus, versionNo);
						if (isModified) {
							logger.warn("Authorized Record saved successfully after edit.");
							lStatus = CommonUtils.TRUE;
						} else {
							logger.warn("Failure in authorizing the record after edit.");
							lStatus = CommonUtils.FAILURE;
						}
					}
				} else {
					logger.warn("No Data in TbAbmiMakerChecker.");
					lStatus = CommonUtils.FALSE;
				}
			}
		} catch (Exception e) {
			logger.error(CommonUtils.EXCEPTION_OCCURED, e);
			lStatus = CommonUtils.FALSE;
		}
		logger.warn("lStatus is "+lStatus);
		return lStatus;
	}

	/**
	 * Method to validate for a existing record in the master table based on the
	 * featureId
	 * 
	 * @param makerCheckerPayloadRequest
	 * @return
	 */
	public boolean validateRecordExist(MakerCheckerPayloadRequest makerCheckerPayloadRequest) {
		logger.warn("Inside validateRecordExist");
		Boolean isValid = Boolean.valueOf(false);
		try {
			String featureId = makerCheckerPayloadRequest.getFeatureId();
			logger.warn("Inside validateRecordExist featureId is "+featureId);
			JSONObject payloadJSON = new JSONObject(makerCheckerPayloadRequest.getPayload());
			logger.warn("Inside validateRecordExist payloadJSON is "+payloadJSON);
			TbAbmiCommonCodeDomain tbAbmiCommonCodeDomain = null;
			switch (featureId) {

			case "APZSFMNT":
			case "APZSSMNT":
			case "APZKYCMNT":
			case "APZUPDOCMT":
			case "APZWDBO":
				logger.warn("Validating for master table record in" + featureId);
				JSONObject crdObj = payloadJSON.getJSONObject(CommonUtils.TB_ABOB_COMMON_CODES);
				tbAbmiCommonCodeDomain = tbAbmiCommonCodeRepository.findByCode(crdObj.getString(CommonUtils.CODE));
				if (null != tbAbmiCommonCodeDomain && null != tbAbmiCommonCodeDomain.getCodeDesc()) {
					isValid = Boolean.valueOf(true);
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error(CommonUtils.EXCEPTION_OCCURED, e);
		}
		return isValid;
	}

	/**
	 * Common method to update the TB_ABOB_MAKER_CHECKER table.
	 * 
	 * @param makerCheckerPayloadRequest
	 * @param controlType
	 * @param authStatus
	 * @param versionNo
	 */
	private boolean updateMakerCheckerTable(MakerCheckerPayloadRequest makerCheckerPayloadRequest, String controlType,
			String authStatus, int versionNo) {
		TbAbmiMakerChecker abmiMCTable = new TbAbmiMakerChecker();
		boolean isUpdated = false;
		try {
			abmiMCTable.setId(makerCheckerPayloadRequest.getId());
			abmiMCTable.setFeatureId(makerCheckerPayloadRequest.getFeatureId());
			abmiMCTable.setUserAction(controlType);
			abmiMCTable.setAuthStatus(authStatus);
			abmiMCTable.setPayload(makerCheckerPayloadRequest.getPayload());
			abmiMCTable.setMakerId(makerCheckerPayloadRequest.getMakerId());
			abmiMCTable.setMakerTs(LocalDateTime.now());
			abmiMCTable.setCheckerId(null);
			abmiMCTable.setCheckerTs(null);
			abmiMCTable.setVersionNo(versionNo);
			tbAbmiMakerCheckerRepository.save(abmiMCTable);
			isUpdated = true;
		} catch (Exception e) {
			logger.error(CommonUtils.EXCEPTION_OCCURED, e);
		}
		return isUpdated;
	}

	/**
	 * This function is used to authorize the record and update the master table
	 * based on the service type.
	 * 
	 * @param makerCheckerPayloadRequest
	 * @return Status of Authorization
	 */
	public String authorizeMakerCheckerRecord(MakerCheckerFetchPayloadRequest makerCheckerPayloadRequest) {

		String lStatus = null;
		TbAbmiMakerChecker tbAbmiMakerChecker = null;
		try {
			logger.warn("request inside authorizeMakerCheckerRecord::" + makerCheckerPayloadRequest);
			long timeM = System.currentTimeMillis();
			Optional<TbAbmiMakerChecker> tbAbmiMakerCheckerData = tbAbmiMakerCheckerRepository
					.findById(makerCheckerPayloadRequest.getId());
			if (tbAbmiMakerCheckerData.isPresent()) {
				TbAbmiMakerChecker tbMakerChecker = tbAbmiMakerCheckerData.get();
				String makerId = tbMakerChecker.getMakerId();
				String checkerId = makerCheckerPayloadRequest.getCheckerId();
				Timestamp checkerTs = new Timestamp(timeM);
				int versionNo = tbMakerChecker.getVersionNo();
				if (makerId.equalsIgnoreCase(checkerId)) {
					logger.warn("Same user cannot authorize the Record");
					lStatus = "SAME_MAKER_CHECKER";
				} else {
					moveToHistoryOnAuthorize(tbMakerChecker);
					String payload = tbMakerChecker.getPayload();
					JSONObject payLoadJSON = new JSONObject(payload);
					payLoadJSON.put("authStatus", "A");
					logger.warn("Payload JSON after resetting the auth Status in Authorzie:" + payLoadJSON);
					tbAbmiMakerChecker = new TbAbmiMakerChecker();
					tbAbmiMakerChecker.setId(tbMakerChecker.getId());
					tbAbmiMakerChecker.setFeatureId(tbMakerChecker.getFeatureId());
					tbAbmiMakerChecker.setPayload(payLoadJSON.toString());
					tbAbmiMakerChecker.setUserAction("AUTHORIZE");
					tbAbmiMakerChecker.setAuthStatus("A");
					tbAbmiMakerChecker.setMakerId(makerId);
					tbAbmiMakerChecker.setMakerTs(tbMakerChecker.getMakerTs());
					tbAbmiMakerChecker.setCheckerId(checkerId);
					tbAbmiMakerChecker.setCheckerTs(checkerTs);
					tbAbmiMakerChecker.setVersionNo(versionNo);
					tbAbmiMakerCheckerRepository.save(tbAbmiMakerChecker);
					boolean isUpdated = updateMasterTable(tbMakerChecker, makerCheckerPayloadRequest);
					if (isUpdated) {
						lStatus = CommonUtils.SUCCESS_UC;
					} else {
						lStatus = CommonUtils.FAILURE_UC;
					}
				}
			} else {
				lStatus = "NO_DATA";
			}
		} catch (Exception e) {
			logger.error(CommonUtils.EXCEPTION_OCCURED, e);
			lStatus = CommonUtils.FAILURE_UC;
		}
		return lStatus;
	}

	/**
	 * Method to move the Maker Checker record in TB_ABOB_MAKER_CHECKER to
	 * TB_ABOB_MAKER_CHECKER_HISTORY on authorize
	 * 
	 * @param tbAbmiMakerCheckerData
	 */
	private void moveToHistoryOnAuthorize(TbAbmiMakerChecker tbAbmiMakerCheckerData) {
		try {
			TbAbmiMakerCheckerHistory tbAbmiMakerCheckerHistory = new TbAbmiMakerCheckerHistory();
			tbAbmiMakerCheckerHistory.setId(tbAbmiMakerCheckerData.getId());
			tbAbmiMakerCheckerHistory.setFeatureId(tbAbmiMakerCheckerData.getFeatureId());
			tbAbmiMakerCheckerHistory.setPayload(tbAbmiMakerCheckerData.getPayload());
			tbAbmiMakerCheckerHistory.setUserAction(tbAbmiMakerCheckerData.getUserAction());
			tbAbmiMakerCheckerHistory.setAuthStatus(tbAbmiMakerCheckerData.getAuthStatus());
			tbAbmiMakerCheckerHistory.setMakerId(tbAbmiMakerCheckerData.getMakerId());
			tbAbmiMakerCheckerHistory.setMakerTs(tbAbmiMakerCheckerData.getMakerTs());
			tbAbmiMakerCheckerHistory.setCheckerId(tbAbmiMakerCheckerData.getCheckerId());
			tbAbmiMakerCheckerHistory.setCheckerTs(tbAbmiMakerCheckerData.getCheckerTs());
			tbAbmiMakerCheckerHistory.setVersionNo(tbAbmiMakerCheckerData.getVersionNo());
			tbAbmiMakerCheckerHistoryRepository.save(tbAbmiMakerCheckerHistory);
			logger.warn("MakerChecker history table updated successfully.");
		} catch (Exception e) {
			logger.error(CommonUtils.EXCEPTION_OCCURED, e);
		}
	}

	/**
	 * Common Method to update/insert the master table records based on the
	 * featureId
	 * 
	 * @param tbAbmiMakerCheckerData
	 * @param makerCheckerPayloadRequest
	 */
	private boolean updateMasterTable(TbAbmiMakerChecker tbAbmiMakerCheckerData,
			MakerCheckerFetchPayloadRequest makerCheckerPayloadRequest) {

		boolean isSuccess = false;
		try {
			logger.warn("Updating Master table.");
			JSONObject payloadJSON = new JSONObject(tbAbmiMakerCheckerData.getPayload());
			String feature = makerCheckerPayloadRequest.getFeatureId();
			logger.warn("Payload JSON:" + payloadJSON);
			logger.warn("feature id is " + feature);
			JSONObject reqObj = null;
			TbAbmiCommonCodeDomain tbAbmiCommonCodeDomain = null;
			switch (feature) {

			case "APZSFMNT":
			case "APZSSMNT":
			case "APZKYCMNT":
			case "APZUPDOCMT":
			case "APZWDBO":
				reqObj = payloadJSON.getJSONObject(CommonUtils.TB_ABOB_COMMON_CODES);
				logger.warn("Inserting into common code table for" + reqObj.getString(CommonUtils.CODE));
				tbAbmiCommonCodeDomain = new TbAbmiCommonCodeDomain();
				tbAbmiCommonCodeDomain.setCode(reqObj.getString(CommonUtils.CODE));
				tbAbmiCommonCodeDomain.setCodeDesc(reqObj.getJSONObject(CommonUtils.CODE_DESC).toString());
				tbAbmiCommonCodeDomain.setCodeType(reqObj.getString(CommonUtils.CODE_TYPE));
				tbAbmiCommonCodeDomain.setLanguage(reqObj.getString(CommonUtils.LANGUAGE));
				tbAbmiCommonCodeDomain.setAccessType(reqObj.getString(CommonUtils.ACCESS_TYPE));
				tbAbmiCommonCodeRepository.save(tbAbmiCommonCodeDomain);
				isSuccess = true;
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error("Exception occured while inserting/updating master table.", e);
		}
		return isSuccess;
	}
}
