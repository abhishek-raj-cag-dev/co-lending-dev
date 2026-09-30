package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillonbanking.cob.core.domain.ab.LoanDetails;

@Repository
public interface LoanDtlsRepo extends CrudRepository<LoanDetails, BigDecimal> {

	LoanDetails findByApplicationIdAndAppIdAndVersionNum(String applicationId, String appId, int versionNum);

	LoanDetails findByApplicationId(String applicationId);

	@Transactional
	void deleteByApplicationIdAndAppId(String applicationId, String appId);

	List<LoanDetails> findByApplicationIdAndAppId(String applicationId, String appId);

	Optional<LoanDetails> findTopByAppIdAndApplicationIdOrderByVersionNumDesc(String appId, String relatedApplicationId);
	
	Optional<LoanDetails> findTopByApplicationIdAndAppId(String applicationId, String appId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE LoanDetails loan SET loan.coapplicantId = :coapplicantId "
			+ "WHERE loan.applicationId = :applicationId", nativeQuery = false)
	void updateCoapplicantId(@Param("applicationId") String applicationId,
			@Param("coapplicantId") String coapplicantId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE LoanDetails loan SET loan.t24LoanId = :t24LoanId "
			+ "WHERE loan.applicationId = :applicationId", nativeQuery = false)
	void updateT24LoanId(@Param("applicationId") String applicationId,
			@Param("t24LoanId") String t24LoanId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE LoanDetails loan SET loan.loanStatus = :loanStatus "
			+ "WHERE loan.applicationId = :applicationId", nativeQuery = false)
	void updateT24LoanStatus(@Param("applicationId") String applicationId,
			@Param("loanStatus") String loanStatus);
	
    @Modifying
    @Transactional
    @Query(value = "UPDATE LoanDetails loan SET loan.tenure = :tenure "
            + "WHERE loan.applicationId = :applicationId", nativeQuery = false)
    void updateTenure(@Param("applicationId") String applicationId, @Param("tenure") Integer tenure);

    @Query("SELECT CASE WHEN bre.eligibleAmt < ld.sanctionedLoanAmount THEN false ELSE true END " +
            "FROM LoanDetails ld " +
            "LEFT JOIN CustomerDetails cd ON cd.applicationId = ld.applicationId AND cd.customerType != 'Applicant' " +
            "LEFT JOIN CibilDetails bre ON bre.applicationId = cd.applicationId AND bre.custDtlId = cd.custDtlId " +
            "WHERE ld.applicationId = :applicationId")
    boolean eliglibleAmtMoreThanSanctionedAmt(@Param("applicationId") String applicationId);


    @Query("SELECT CASE WHEN bre.eligibleAmt < ld.bmRecommendedLoanAmount THEN false ELSE true END " +
            "FROM LoanDetails ld " +
            "LEFT JOIN CustomerDetails cd  ON cd.applicationId = ld.applicationId AND cd.customerType != 'Applicant' " +
            "LEFT JOIN CibilDetails bre ON bre.applicationId = cd.applicationId AND bre.custDtlId = cd.custDtlId " +
            "WHERE ld.applicationId = :applicationId")
    boolean eliglibleAmtMoreThanBmRecommendedAmt(@Param("applicationId") String applicationId);

}
