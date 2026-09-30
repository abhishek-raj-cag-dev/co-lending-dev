package com.iexceed.appzillonbanking.cob.repository.ab;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillonbanking.cob.domain.ab.CRSDetails;

public interface CRSDetailsRepository extends CrudRepository<CRSDetails, BigDecimal> {

	List<CRSDetails> findByApplicationIdAndAppIdAndVersionNum(String applicationId, String appId, int versionNum);

	List<CRSDetails> findByApplicationIdAndAppId(String applicationId, String appId);

	@Transactional
	void deleteByApplicationIdAndAppId(String applicationId, String appId);

	List<CRSDetails> findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(String applicationId, String appId, int oldVersionNum,BigDecimal oldCustDtlId);
	
	
}
