package com.iexceed.appzillonbanking.cob.core.services;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ErrorMaintainance;
import com.iexceed.appzillonbanking.cob.core.payload.ErrorParameterValues;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ErrorMaintainanceRepository;
import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ErrorMaintenanceService {
	private Logger logger = LogManager.getLogger(ErrorMaintenanceService.class);

	@Autowired
	public ErrorMaintainanceRepository errorMaintainanceRepository;

	/*
	 * Below method populateErrorMap is used to populate a commonutils hashmap using
	 * data from database, at startup. It is only executed once at startup. It
	 * should not be invoked from any other class. Use commonutils method
	 * getHostToApzErrorMapping to get access to errorcode translation.
	 */
	@Bean
	public void populateErrorMap() {
		Iterable<ErrorMaintainance> errorMaintainance = errorMaintainanceRepository.findAll();
		Map<String, Map<String, ErrorParameterValues>> hostIdToHostErrorMap = new HashMap<>();
		Map<String, ErrorParameterValues> hostErrorToApzErrorMap = null;
		for (ErrorMaintainance element : errorMaintainance) {
			if (hostIdToHostErrorMap.containsKey(element.getHostId())) {
				hostErrorToApzErrorMap = hostIdToHostErrorMap.get(element.getHostId());
				ErrorParameterValues errorParameterValues = new ErrorParameterValues();
				errorParameterValues.setApzErrorCode(element.getErrorCode());
				errorParameterValues.setApzErrorDescription(element.getErrorDesc());
				errorParameterValues.setHostErrorDescription(element.getHostErrorDesc());
				hostErrorToApzErrorMap.put(element.getHostErrorCode(), errorParameterValues);
			} else {
				hostErrorToApzErrorMap = new HashMap<>();
				ErrorParameterValues errorParameterValues = new ErrorParameterValues();
				errorParameterValues.setApzErrorCode(element.getErrorCode());
				errorParameterValues.setApzErrorDescription(element.getErrorDesc());
				errorParameterValues.setHostErrorDescription(element.getHostErrorDesc());
				hostErrorToApzErrorMap.put(element.getHostErrorCode(), errorParameterValues);
				hostIdToHostErrorMap.put(element.getHostId(), hostErrorToApzErrorMap);
			}
		}
		logger.info("Error codes initialized from TB_ABMI_ERROR_MAINTENANCE...");
		CommonUtils.initializeHostToApzErrorMap(hostIdToHostErrorMap);
	}
}
