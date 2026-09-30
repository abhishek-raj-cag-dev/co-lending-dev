package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ExistingLoanDetails;

@Repository
public interface ExistingLoanDetailsRepository extends CrudRepository<ExistingLoanDetails, BigDecimal> {

	public Optional<List<ExistingLoanDetails>> findByApplicationIdAndAppIdAndVersionNum(String applicationId,
			String appId, int versionNum);

	public List<ExistingLoanDetails> findByApplicationIdAndAppId(String applicationId, String appId);

	@Transactional
	public void deleteByApplicationIdAndAppId(String applicationId, String appId);

	public Optional<List<ExistingLoanDetails>> findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(String applicationId,
			String appId, int versionNum, BigDecimal custDtlId);
}