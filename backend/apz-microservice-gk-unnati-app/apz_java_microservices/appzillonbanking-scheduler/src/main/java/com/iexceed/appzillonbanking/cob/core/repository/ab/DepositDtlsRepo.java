package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.DepositDtls;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepositDtlsRepo extends CrudRepository<DepositDtls, BigDecimal> {

    DepositDtls findByApplicationIdAndAppIdAndVersionNum(String applicationId, String appId, int versionNum);

	List<DepositDtls> findByApplicationIdAndAppId(String applicationId, String appId);

	@Transactional
	void deleteByApplicationIdAndAppId(String applicationId, String appId);

	Optional<DepositDtls> findTopByAppIdAndApplicationIdOrderByVersionNumDesc(String appId, String relatedApplicationId);

}