package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.CustomerDetails;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerDetailsRepository extends CrudRepository<CustomerDetails, BigDecimal> {

	public List<CustomerDetails> findByApplicationIdAndAppIdAndVersionNum(String applicationId, String appId,
			int versionNum);

	@Transactional
	public void deleteByApplicationIdAndAppId(String applicationId, String appId);

	@Transactional
	public void deleteByApplicationIdAndAppIdAndCustDtlId(String applicationId, String appId, BigDecimal custDtlId);

	public List<CustomerDetails> findByApplicationIdAndAppId(String applicationId, String appId);

	public List<CustomerDetails> findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(String applicationId,
			String appId, int versionNum, BigDecimal custDtlId);

	public Optional<CustomerDetails> findByApplicationIdAndAppIdAndVersionNumAndSeqNumber(String applicationId,
			String appId, Integer versionNum, int i);

	public Optional<CustomerDetails> findByApplicationIdAndAppIdAndCustomerType(String applicationId, String appId,
			String customerType);

	@Query(value = "SELECT COUNT(*) FROM TB_ABOB_CUSTOMER_DETAILS WHERE CAST(PAYLOAD AS jsonb)->> 'custId' != :custId AND (CAST(PAYLOAD AS jsonb)->> 'primaryKycId' = :primaryKycId OR CAST(PAYLOAD AS jsonb)->> 'alternateVoterId' = :alternateKycId)", nativeQuery = true)
	public int countByPrimaryKycIdAndAlternateVoterId(@Param("primaryKycId") String primaryKycId,
			@Param("alternateKycId") String alternateKycId, @Param("custId") String custId);

	@Query(value = "SELECT COUNT(*) FROM TB_ABOB_CUSTOMER_DETAILS WHERE APPLICATION_ID NOT IN :applicationIds AND CAST(PAYLOAD AS jsonb)->> 'custId' != :custId AND ( CAST(PAYLOAD AS jsonb)->> 'primaryKycId' = :primaryKycId OR CAST(PAYLOAD AS jsonb)->> 'alternateVoterId' = :alternateKycId )", nativeQuery = true)
	public int countByPrimaryKycIdAndAlternateVoterIdAndApplicationIdNotIn(
			@Param("applicationIds") List<String> applicationIds, @Param("primaryKycId") String primaryKycId,
			@Param("alternateKycId") String alternateKycId, @Param("custId") String custId);

	@Query(value = "SELECT COUNT(*) FROM TB_ABOB_CUSTOMER_DETAILS WHERE CAST(PAYLOAD AS jsonb)->> 'custId' != :custId AND (MOBILE_NUMBER = :mobileNo OR CAST(PAYLOAD AS jsonb) ->> 'secMobileNo' = :secMobileNo)", nativeQuery = true)
	public int countByMobileNoAndSecMobileNo(@Param("mobileNo") String mobileNo,
			@Param("secMobileNo") String secMobileNo, @Param("custId") String custId);

	@Query(value = "SELECT COUNT(*) FROM TB_ABOB_CUSTOMER_DETAILS WHERE APPLICATION_ID NOT IN :applicationIds AND CAST(PAYLOAD AS jsonb)->> 'custId' != :custId AND ( MOBILE_NUMBER = :mobileNo OR CAST(PAYLOAD AS jsonb) ->> 'secMobileNo' = :secMobileNo )", nativeQuery = true)
	public int countByMobileNoAndSecMobileNoAndApplicationIdNotIn(@Param("applicationIds") List<String> applicationIds,
			@Param("mobileNo") String mobileNo, @Param("secMobileNo") String secMobileNo,
			@Param("custId") String custId);

	//A
	public Optional<CustomerDetails> findByApplicationIdAndCustomerType(String applicationId, String customerType);

    public List<CustomerDetails> findByApplicationId(String applicationId);


	@Query(value = "SELECT * FROM TB_ABOB_CUSTOMER_DETAILS WHERE APPLICATION_ID = :applicationId AND CUST_DTL_ID != :custDtlId", nativeQuery = true)
	Optional<CustomerDetails> findByApplicationIdAndNotCustDtlId(String applicationId, BigDecimal custDtlId);

	@Query(value = "SELECT new CustomerDetails(c.applicationId, a.searchCode2) FROM CustomerDetails c " +
			" LEFT JOIN ApplicationMaster a ON c.applicationId = a.applicationId " +
			" WHERE c.applicationId NOT IN :applicationIds AND " +
			" c.custId != :custId AND (Upper(c.primaryKycId) = Upper(:primaryKycId) OR Upper(c.alternateVoterId) = Upper(:alternateKycId)) ", nativeQuery = false )
	Optional<CustomerDetails> findByPrimaryKycIdAndAlternateVoterIdAndApplicationIdNotIn(
			@Param("applicationIds") List<String> applicationIds, @Param("primaryKycId") String primaryKycId,
			@Param("alternateKycId") String alternateKycId, @Param("custId") String custId);
	
	@Query(value = "SELECT new CustomerDetails(c.applicationId, a.searchCode2) FROM CustomerDetails c " +
			"LEFT JOIN ApplicationMaster a ON c.applicationId = a.applicationId " +
			"WHERE c.custId != :custId AND (Upper(c.primaryKycId) = Upper(:primaryKycId) OR Upper(c.alternateVoterId) = Upper(:alternateKycId))", nativeQuery = false)
	Optional<CustomerDetails> findByPrimaryKycIdAndAlternateVoterId(@Param("primaryKycId") String primaryKycId,
			@Param("alternateKycId") String alternateKycId, @Param("custId") String custId);
}
