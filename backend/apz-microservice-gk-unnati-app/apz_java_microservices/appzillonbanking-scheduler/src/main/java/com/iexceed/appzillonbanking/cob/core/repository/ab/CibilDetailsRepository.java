package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillonbanking.cob.core.domain.ab.CibilDetails;

@Repository
public interface CibilDetailsRepository extends JpaRepository<CibilDetails, BigDecimal> {

    Optional<List<CibilDetails>> findByApplicationIdAndAppIdAndVersionNum(String applicationId, String appId,
            int versionNum);
	
	List<CibilDetails> findByApplicationIdAndAppId(String applicationId, String appId);

	@Transactional
	void deleteByApplicationIdAndAppId(String applicationId, String appId);
	
    Optional<List<CibilDetails>> findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(String applicationId,
            String appId, int versionNum, BigDecimal custDtlId);
	
    Optional<CibilDetails> findByApplicationIdAndAppIdAndCustDtlId(String applicationId, String appId,
            BigDecimal custDtlId);
	
	@Transactional
	void deleteByApplicationIdAndAppIdAndCustDtlId(String applicationId, String appId, BigDecimal custDtlId);
	
	@Query(value = "SELECT new CibilDetails(cibilDetails.cbDtlId) "
            + "FROM CibilDetails cibilDetails "
            + "LEFT OUTER JOIN CustomerDetails customerDetails ON cibilDetails.applicationId = customerDetails.applicationId "
            + "AND cibilDetails.custDtlId = customerDetails.custDtlId "
            + "WHERE customerDetails.customerType = :customerType and cibilDetails.applicationId = :applicationId", nativeQuery = false)
    Optional<CibilDetails> findCibilDetailsByCustomerType(@Param("customerType") String customerType,
            @Param("applicationId") String applicationId);

    @Query(value = "SELECT new com.iexceed.appzillonbanking.cob.core.domain.ab.CibilDetails("
            + "cibildtls.cbDtlId, cibildtls.applicationId, cibildtls.versionNum, cibildtls.appId, "
            + "cibildtls.custDtlId, cibildtls.request, cibildtls.responseId, cibildtls.cbDate, "
            + "cibildtls.cbStatus, cibildtls.payloadColumn, appMaster, custdtls) "
            + "FROM CibilDetails cibildtls "
            + "LEFT JOIN ApplicationMaster appMaster ON cibildtls.applicationId = appMaster.applicationId "
            + "LEFT JOIN CustomerDetails custdtls ON cibildtls.custDtlId = custdtls.custDtlId "
            + "AND cibildtls.applicationId = custdtls.applicationId "
            + "WHERE cibildtls.cbDate < :expiryDate and appMaster.declarationFlag = 'I' and "
            + " appMaster.applicationStatus not in ('INPROGRESS','PENDING','DISBURSED','REJECTED','PUSHBACK','IPUSHBACK','LUC','PENDINGLUCVERIFICATION','LUCVERIFIED') "
            + " AND custdtls.customerType = 'Co-App' ", nativeQuery = false)
    List<CibilDetails> findCibilDetailsByExpiryLimit(@Param("expiryDate") LocalDate expiryDate);

    CibilDetails findByApplicationIdAndCustDtlId(String applicationId, BigDecimal custDtlId);

    CibilDetails findByApplicationIdAndCustDtlIdAndCbStatus(String applicationId, BigDecimal custDtlId, String passString);
	@Query(value = "SELECT cibilDetails "
            + "FROM CibilDetails cibilDetails "
            + "LEFT OUTER JOIN CustomerDetails customerDetails ON cibilDetails.applicationId = customerDetails.applicationId "
            + "AND cibilDetails.custDtlId = customerDetails.custDtlId "
            + "WHERE customerDetails.customerType = :customerType and cibilDetails.applicationId = :applicationId", nativeQuery = false)
	Optional<CibilDetails> findCibilDetailsByCustomerTypeAndApplicationId(@Param("customerType") String customerType,@Param("applicationId") String applicationId);
}
