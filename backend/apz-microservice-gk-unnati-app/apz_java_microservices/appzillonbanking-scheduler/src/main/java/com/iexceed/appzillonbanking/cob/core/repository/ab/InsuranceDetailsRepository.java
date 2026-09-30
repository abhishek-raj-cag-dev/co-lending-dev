package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.iexceed.appzillonbanking.cob.core.domain.ab.InsuranceDetails;

@Repository
public interface InsuranceDetailsRepository extends CrudRepository<InsuranceDetails, BigDecimal> {

	Optional<List<InsuranceDetails>> findByApplicationIdAndAppIdAndVersionNum(String applicationId, String appId,
			int versionNum);

	List<InsuranceDetails> findByApplicationIdAndAppId(String applicationId, String appId);

	@Transactional
	void deleteByApplicationIdAndAppId(String applicationId, String appId);

	@Transactional
	void deleteByApplicationIdAndAppIdAndCustDtlId(String applicationId, String appId, BigDecimal custDtlId);

	Optional<List<InsuranceDetails>> findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(String applicationId,
			String appId, int versionNum, BigDecimal custDtlId);

	Optional<InsuranceDetails> findByApplicationIdAndAppIdAndCustDtlId(String applicationId, String appId,
			BigDecimal custDtlId);

	Optional<InsuranceDetails> findByApplicationIdAndAppIdAndCustDtlIdNot(String applicationId, String appId,
			BigDecimal custDtlId);
	
	@Query(value = "SELECT new InsuranceDetails(insuranceDetails.insuranceDtlId) "
		    + "FROM InsuranceDetails insuranceDetails "
		    + "LEFT OUTER JOIN CustomerDetails customerDetails ON insuranceDetails.applicationId = :applicationId and insuranceDetails.applicationId = customerDetails.applicationId "
		    + "AND insuranceDetails.custDtlId = customerDetails.custDtlId "
		    + "WHERE customerDetails.customerType = :customerType", nativeQuery = false)
	Optional<InsuranceDetails> findInsuranceDetailsByCustomerType(@Param("customerType") String customerType,@Param("applicationId") String applicationId);


	@Query(value = "SELECT i.*  FROM TB_CGOB_INSURANCE_DTLS i JOIN TB_ABOB_CUSTOMER_DETAILS c ON c.CUST_DTL_ID = i.CUST_DTL_ID "
			+ " WHERE i.APPLICATION_ID = :applicationId "
			+ " AND i.CUST_DTL_ID != :custDtlId "
			+ " AND c.APPLICATION_ID = :applicationId ", nativeQuery = true)
	Optional<InsuranceDetails> findByApplicationIdAndCustDtlIdNot(@Param("applicationId") String applicationId,
																  @Param("custDtlId") BigDecimal custDtlId);

}
