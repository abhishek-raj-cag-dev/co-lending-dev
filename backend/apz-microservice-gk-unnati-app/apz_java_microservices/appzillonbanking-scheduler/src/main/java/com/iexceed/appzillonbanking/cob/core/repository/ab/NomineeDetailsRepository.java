package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.NomineeDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface NomineeDetailsRepository extends CrudRepository<NomineeDetails, BigDecimal> {

	public List<NomineeDetails> findByApplicationIdAndAppIdAndVersionNumAndStatus(String applicationId,String appId,int versionNum, String status);

	@Transactional
	public void deleteByApplicationIdAndAppId(String applicationId, String appId);

	//public NomineeDetails findByAppIdAndApplicationIdAndVersionNumAndCustDtlId(String appId, String applicationId, int versionNum, BigDecimal custDtlId);
	
	public List<NomineeDetails> findByApplicationIdAndAppIdAndVersionNumAndStatusAndCustDtlId(String applicationId,String appId,int versionNum, String status, BigDecimal custDtlId);

	
	@Query(value ="select new com.iexceed.appzillonbanking.cob.core.domain.ab.NomineeDetails(nomineeDtls.nomineeDtlsId, nomineeDtls.applicationId, nomineeDtls.versionNum, "
			+ "nomineeDtls.appId, nomineeDtls.custDtlId, nomineeDtls.status, nomineeDtls.payloadColumn) FROM com.iexceed.appzillonbanking.cob.core.domain.ab.NomineeDetails nomineeDtls"
			+ " where nomineeDtls.appId=:appId AND nomineeDtls.applicationId=:applicationId AND nomineeDtls.status=:status AND nomineeDtls.versionNum in "
			+ "(select max(nomineeDtls2.versionNum) from com.iexceed.appzillonbanking.cob.core.domain.ab.NomineeDetails nomineeDtls2 where nomineeDtls2.appId=nomineeDtls.appId and "
			+ "nomineeDtls2.applicationId=nomineeDtls.applicationId and nomineeDtls2.status=nomineeDtls.status) ", nativeQuery = false)
	public List<NomineeDetails> fetchNominees(String applicationId, String appId, String status);
	
	public List<NomineeDetails> findByApplicationIdAndAppId(String applicationId, String appId);
}