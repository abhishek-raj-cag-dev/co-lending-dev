package com.iexceed.appzillonbanking.cob.repository.cdh;

import com.iexceed.appzillonbanking.cob.domain.cdh.UnnatiIexceedCDHLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UnnatiIexceedCDHLeadRepo extends JpaRepository<UnnatiIexceedCDHLead, Long> {

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

    Optional<UnnatiIexceedCDHLead> findByCustomerId(String customerId);

    @Query("SELECT cdhLead.glBranchState FROM UnnatiIexceedCDHLead cdhLead WHERE cdhLead.customerId = :customerId")
    String findStateByCustomerId(@Param("customerId") String customerId);

    @Query("SELECT cdhLead.groupId FROM UnnatiIexceedCDHLead cdhLead WHERE cdhLead.customerId = :customerId")
    String findGroupIdByCustomerId(@Param("customerId") String customerId);
}
