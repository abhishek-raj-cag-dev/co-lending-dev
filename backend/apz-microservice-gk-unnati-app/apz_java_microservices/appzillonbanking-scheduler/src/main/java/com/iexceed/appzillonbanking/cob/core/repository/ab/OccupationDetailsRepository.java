package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillonbanking.cob.core.domain.ab.OccupationDetails;

@Repository
public interface OccupationDetailsRepository extends CrudRepository<OccupationDetails, BigDecimal> {

	List<OccupationDetails> findByApplicationIdAndAppIdAndVersionNum(String applicationId, String appId,
			int versionNum);

	List<OccupationDetails> findByApplicationIdAndAppId(String applicationId, String appId);

	@Transactional
	void deleteByApplicationIdAndAppId(String applicationId, String appId);

	@Transactional
	void deleteByApplicationIdAndAppIdAndCustDtlId(String applicationId, String appId, BigDecimal custDtlId);

	List<OccupationDetails> findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(String applicationId, String appId,
			int versionNum, BigDecimal custDtlId);

	Optional<OccupationDetails> findByApplicationIdAndAppIdAndCustDtlId(String applicationId, String appId,
			BigDecimal custDtlId);

	List<OccupationDetails> findByApplicationIdAndAppIdAndCustDtlIdNot(String applicationId, String appId,
			BigDecimal custDtlId);
	
	@Query(value = "SELECT new OccupationDetails(occupationDetails.occptDtlId) "
		       + "FROM OccupationDetails occupationDetails "
		       + "LEFT OUTER JOIN CustomerDetails customerDetails ON occupationDetails.applicationId = :applicationId and occupationDetails.applicationId = customerDetails.applicationId "
		       + "AND occupationDetails.custDtlId = customerDetails.custDtlId "
		       + "WHERE customerDetails.customerType = :customerType ", nativeQuery = false)
	Optional<OccupationDetails> findOccupationDetailsByCustomerType(@Param("customerType") String customerType,@Param("applicationId") String applicationId);

	@Query(value = "SELECT occupationDetails "
		       + "FROM OccupationDetails occupationDetails "
		       + "LEFT OUTER JOIN CustomerDetails customerDetails ON occupationDetails.applicationId = :applicationId and occupationDetails.applicationId = customerDetails.applicationId "
		       + "AND occupationDetails.custDtlId = customerDetails.custDtlId "
		       + "WHERE customerDetails.customerType = :customerType ", nativeQuery = false)
	Optional<OccupationDetails> findOccupationDetailsByCustomerTypeForRpc(@Param("customerType") String customerType,@Param("applicationId") String applicationId);

}
