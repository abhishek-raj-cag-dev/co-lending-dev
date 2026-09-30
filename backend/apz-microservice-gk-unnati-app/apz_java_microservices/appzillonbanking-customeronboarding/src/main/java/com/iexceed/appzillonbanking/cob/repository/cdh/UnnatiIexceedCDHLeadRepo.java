package com.iexceed.appzillonbanking.cob.repository.cdh;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedCDHLead;
import com.iexceed.appzillonbanking.cob.loans.payload.AdditionalLoanLeadProjection;

public interface UnnatiIexceedCDHLeadRepo extends JpaRepository<UnnatiIexceedCDHLead, String> {

    @Query(value = "SELECT cdhLead FROM UnnatiIexceedCDHLead cdhLead " +
            " WHERE cdhLead.kendraId IN (:kendraIds) " +
            " and cdhLead.product in (:productCodes)", nativeQuery = false)
    List<UnnatiIexceedCDHLead> findByKendraId(@Param("kendraIds") List<String> kendraIds,
                                              @Param("productCodes") List<String> productCodes);

    @Query(value = "SELECT cdhLead FROM UnnatiIexceedCDHLead cdhLead " +
            " WHERE cdhLead.kendraId IN (:kendraIds) " +
            " AND cdhLead.unnatiStatus in (:status) " +
            " and cdhLead.product in (:productCodes)", nativeQuery = false)
    List<UnnatiIexceedCDHLead> findByKendraIdAndUnnatiStatus(@Param("kendraIds") List<String> kendraIds,
                                                             @Param("status") List<String> status,
                                                             @Param("productCodes") List<String> productCodes);

	Optional<UnnatiIexceedCDHLead> findByCustomerIdAndProductAndUnnatiStatusNotInAndSourceOfTheApplicationIn(String customerId,String productId, List<String> status, List<String> sourceOfTheApplication);
	Optional<UnnatiIexceedCDHLead> findByCustomerIdAndProductAndReferenceId(String customerId,String productId, String referenceId);
    /**
     * @author Ankit.CAG
     * Note(Need to Add fetchAdditionalLoanLeads)-- AND status_of_the_application  IN ('Initialize')
     * Initialize: Hard Status for New Leads.
     */
//    List<UnnatiIexceedCDHLead> findByCustomerIdAndProduct(String customerId,String productId);
    List<UnnatiIexceedCDHLead> findByCustomerIdAndProductAndUnnatiStatusIn(String customerId,String productId, List<String> status);
    List<UnnatiIexceedCDHLead> findByCustomerIdAndProductAndOpenMarketId(String customerId,String productId, Long applicationId);
    List<UnnatiIexceedCDHLead> findByGlBranchIdInAndProductIn(List<String> branchIds,List<String> productIds);
	long countByGlBranchIdInAndProductAndUnnatiStatusIn(List<String> branchIds, String productId, List<String> status);
	long countByProductAndUnnatiStatusIn(String productId, List<String> status);

	@Query(value = "SELECT " + "application_id AS applicationId,unnati_application_id AS unnatiApplicationId, "
			+ "customer_id AS customerId,kendra_name AS kendraName,gl_branch_id AS branchId,unnati_status As cdhUnnatiStatus, "
			+ "full_name AS customerName, " + "gl_branch_name AS branchName, " + "CONCAT( "
			+ " FLOOR(TIMESTAMPDIFF(SECOND, lead_initiation_date, CURRENT_TIMESTAMP) % 60), 'S|', "
			+ " FLOOR(TIMESTAMPDIFF(MINUTE, lead_initiation_date, CURRENT_TIMESTAMP) % 60), 'M|', "
			+ " FLOOR(TIMESTAMPDIFF(HOUR, lead_initiation_date, CURRENT_TIMESTAMP) % 24), 'H|', "
			+ " FLOOR(TIMESTAMPDIFF(DAY, lead_initiation_date, CURRENT_TIMESTAMP)), 'D' " + ") AS rpcTAT, "
			+ "amount_approved_from_bre AS loanAmount, " + "status_of_the_application AS applicationStatus, "
			+ "product AS productCode, " + "kendra_id AS kendraId, "+ "crt_approved_amount AS crtApprovedAmt "  + "FROM unnati_iexceed_lead "
			+ "WHERE gl_branch_id IN (:branches) " + "AND product IN (:productCodes) AND unnati_status  IN ('Initialize','Initiate')"
			+ " ORDER BY TIMESTAMPDIFF(SECOND, lead_initiation_date, CURRENT_TIMESTAMP) ASC", nativeQuery = true)
	List<AdditionalLoanLeadProjection> fetchAdditionalLoanLeads(@Param("branches") List<String> branches,
			@Param("productCodes") List<String> productCodes);

	
	
	@Query(value = "SELECT " + "application_id AS applicationId, unnati_application_id AS unnatiApplicationId, " + "customer_id AS customerId,kendra_name AS kendraName,gl_branch_id AS branchId,unnati_status AS cdhUnnatiStatus, "
			+ "full_name AS customerName, " + "gl_branch_name AS branchName, " + "CONCAT( "
			+ " FLOOR(TIMESTAMPDIFF(SECOND, lead_initiation_date, CURRENT_TIMESTAMP) % 60), 'S|', "
			+ " FLOOR(TIMESTAMPDIFF(MINUTE, lead_initiation_date, CURRENT_TIMESTAMP) % 60), 'M|', "
			+ " FLOOR(TIMESTAMPDIFF(HOUR, lead_initiation_date, CURRENT_TIMESTAMP) % 24), 'H|', "
			+ " FLOOR(TIMESTAMPDIFF(DAY, lead_initiation_date, CURRENT_TIMESTAMP)), 'D' " + ") AS rpcTAT, "
			+ "amount_approved_from_bre AS loanAmount, " + "status_of_the_application AS applicationStatus, "
			+ "product AS productCode, " + "kendra_id AS kendraId, " + "crt_approved_amt AS crtApprovedAmt " + "FROM unnati_iexceed_lead "
			+ "WHERE kendra_id IN (:kendraIds) " + "AND product IN (:productCodes) AND unnati_status  IN ('Initialize','Initiate')"
			+ " ORDER BY TIMESTAMPDIFF(SECOND, lead_initiation_date, CURRENT_TIMESTAMP) ASC", nativeQuery = true)
	List<AdditionalLoanLeadProjection> fetchAdditionalLoanLeadsbykendraIds(@Param("kendraIds") List<String> kendraIds,
			@Param("productCodes") List<String> productCodes);

    @Query("SELECT cdhLead.glBranchState FROM UnnatiIexceedCDHLead cdhLead WHERE cdhLead.customerId = :customerId AND cdhLead.product = :productCode AND cdhLead.referenceId = :referenceId")
    String findStateByCustomerIdAndReferenceId(@Param("customerId") String customerId, @Param("productCode") String productCode, @Param("referenceId") String referenceId);

    @Query("SELECT cdhLead.groupId FROM UnnatiIexceedCDHLead cdhLead WHERE cdhLead.customerId = :customerId AND cdhLead.referenceId = :referenceId")
    String findGroupIdByCustomerIdAndReferenceId(@Param("customerId") String customerId, @Param("referenceId") String referenceId);

    
}
