package com.iexceed.appzillonbanking.cob.repository.ab;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import com.iexceed.appzillonbanking.cob.domain.ab.BankingFacilities;

public interface BankingFacilitiesRepository extends CrudRepository<BankingFacilities, BigDecimal> {

	List<BankingFacilities> findByApplicationIdAndAppIdAndVersionNum(String applicationId, String appId, int versionNum);

	@Transactional
	void deleteByApplicationIdAndAppId(String applicationId, String appId);

	List<BankingFacilities> findByApplicationIdAndAppId(String applicationId, String appId);
	
	List<BankingFacilities> findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(String applicationId, String appId, int versionNum, BigDecimal custDtlId);
}