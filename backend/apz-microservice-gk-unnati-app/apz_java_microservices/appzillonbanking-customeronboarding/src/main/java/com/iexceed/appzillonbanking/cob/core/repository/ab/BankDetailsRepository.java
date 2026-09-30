package com.iexceed.appzillonbanking.cob.core.repository.ab;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillonbanking.cob.core.domain.ab.BankDetails;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetails, BigDecimal> {

	Optional<List<BankDetails>> findByApplicationIdAndAppIdAndVersionNum(String applicationId, String appId,int versionNum);
	
	List<BankDetails> findByApplicationIdAndAppId(String applicationId, String appId);

	@Transactional
	void deleteByApplicationIdAndAppId(String applicationId, String appId);
	
	Optional<List<BankDetails>> findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(String applicationId, String appId,int versionNum, BigDecimal custDtlId);
	
	Optional<BankDetails> findTopByApplicationIdAndAppId(String applicationId, String appId);
	
	Optional<BankDetails> findByApplicationId(String applicationId);

	@Query(value = "SELECT bankDetails "
		       + "FROM BankDetails bankDetails "
		       + "LEFT OUTER JOIN CustomerDetails customerDetails ON bankDetails.applicationId = :applicationId and bankDetails.applicationId = customerDetails.applicationId "
		       + "AND bankDetails.custDtlId = customerDetails.custDtlId "
		       + "WHERE customerDetails.customerType = :customerType ", nativeQuery = false)
	Optional<BankDetails> findBankDetailsByCustomerType(@Param("customerType") String customerType,@Param("applicationId") String applicationId);
	
	Optional<BankDetails> findByApplicationIdAndCustDtlId(String applicationId, BigDecimal custDtlId); 

//	@Query(value = "SELECT b FROM BankDetails b "
//			+ "LEFT JOIN ApplicationMaster a ON b.applicationId = a.applicationId "
//			+ "AND a.searchCode2 != :customerId "
//			+ "WHERE b.applicationId != :applicationId AND "
//			+ "b.custDtlId != :custDtlId AND (b.accountNumber = :accountNumber) ", nativeQuery = false )
//	Optional<List<BankDetails>> findByCustIdAndAccountNoAndApplicationIdNotIn(@Param("applicationId") String applicationId, @Param("accountNumber") String accountNumber,
//			 @Param("custDtlId") BigDecimal custDtlId, @Param("customerId") String customerId);
	
	
	@Query(value = "SELECT b FROM BankDetails b " +
			" LEFT JOIN ApplicationMaster a On b.applicationId = a.applicationId " +
			" WHERE b.accountNumber = :accountNumber AND b.applicationId != :applicationId " +
			" AND b.custDtlId != :custDtlId AND a.searchCode2 != :customerId", nativeQuery = false)
	Optional<List<BankDetails>> findByCustIdAndAccountNoAndApplicationIdNotIn(@Param("applicationId") String applicationId, @Param("accountNumber") String accountNumber,
																			  @Param("custDtlId") BigDecimal custDtlId, @Param("customerId") String customerId);
}
