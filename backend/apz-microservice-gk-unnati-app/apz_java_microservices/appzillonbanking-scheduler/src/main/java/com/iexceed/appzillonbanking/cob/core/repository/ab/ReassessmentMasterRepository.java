package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ReassessmentMaster;


public interface ReassessmentMasterRepository extends CrudRepository<ReassessmentMaster, String> {

        List<ReassessmentMaster> findByProduct(String product);

    List<ReassessmentMaster> findByProductAndActive(String productCode, String activeStatus);

    @Query(value = "SELECT r FROM ReassessmentMaster r WHERE r.state IN ( SELECT CASE WHEN :productCode IN :productCodesList "
            + "THEN ld.glBranchState ELSE rld.glBranchState END FROM LeadDetails ld, RenewalLeadDetails rld "
            + "WHERE ld.pid = :workItemNo OR rld.pid = :workItemNo ) AND r.active = :activeStatus AND r.product = :productCode", nativeQuery = false)
    List<ReassessmentMaster> findByProductAndActiveStatusAndState(String productCode, String activeStatus,
            String workItemNo, List<String> productCodesList);

        @Query(value = "SELECT r FROM ReassessmentMaster r WHERE r.state = (SELECT ld.glBranchState FROM LeadDetails ld WHERE ld.pid = :workItemNo)"
                +" AND r.active = :activeStatus AND r.product = :productCode", nativeQuery = false)
        List<ReassessmentMaster> findByProductAndActiveStatusAndStateForLead(String productCode, String activeStatus,
                String workItemNo);

        @Query(value = "SELECT r FROM ReassessmentMaster r WHERE r.state = (SELECT rld.glBranchState FROM LeadDetails rld WHERE rld.pid = :workItemNo)"
                +" AND r.active = :activeStatus AND r.product = :productCode", nativeQuery = false)
        List<ReassessmentMaster> findByProductAndActiveStatusAndStateForRenewalLead(String productCode, String activeStatus,
                String workItemNo);

        
        @Query(value = "SELECT CASE " +
                        "WHEN app.productCode = :productCode THEN lead.glBranchState " +
                        "ELSE renewal.glBranchState END " +
                        "FROM ApplicationMaster app " +
                        "LEFT JOIN LeadDetails lead ON app.workitemNo = lead.pid " +
                        "LEFT JOIN RenewalLeadDetails renewal ON app.workitemNo = renewal.pid " +
                        "WHERE app.workitemNo = :workitemNo", nativeQuery = false)
        String findStateByWorkitemNo(@Param("workitemNo") String workitemNo, @Param("productCode") String productCode);


        @Query(value = "SELECT r FROM ReassessmentMaster r WHERE r.state = :state AND r.product = :productCode AND r.active = :activeStatus")
        List<ReassessmentMaster> findByProductAndActiveStatusAndState(@Param("productCode") String productCode,
                @Param("activeStatus") String activeStatus, @Param("state") String state);

}
