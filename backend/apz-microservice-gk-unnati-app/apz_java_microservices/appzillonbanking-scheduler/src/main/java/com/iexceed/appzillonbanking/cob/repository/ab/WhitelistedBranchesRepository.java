package com.iexceed.appzillonbanking.cob.repository.ab;

import com.iexceed.appzillonbanking.cob.domain.ab.WhitelistedBranches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WhitelistedBranchesRepository extends JpaRepository<WhitelistedBranches, String> {

    @Query(
            value = "SELECT branch_code FROM TB_ABOB_WHITELISTED_BRANCHES WHERE branch_code IN (:branchCodes)",
            nativeQuery = true
    )
    List<String> findWhitelistedBranches(@Param("branchCodes") List<String> branchCodes);


    @Query(
            value = "SELECT CASE WHEN EXISTS (" +
                    "SELECT 1 FROM TB_ABOB_WHITELISTED_BRANCHES " +
                    "WHERE branch_code IN (:branchCodes)) " +
                    "THEN TRUE ELSE FALSE END",
            nativeQuery = true
    )
    boolean isAnyBranchWhitelisted(@Param("branchCodes") List<String> branchCodes);

    Optional<WhitelistedBranches> findByBranchCode(String branchCode);

    @Query(
            value = "SELECT branch_code FROM TB_ABOB_WHITELISTED_BRANCHES WHERE branch_code IN (:branchCodes) AND RENEWAL_ENABLED = 'Y'",
            nativeQuery = true
    )
    List<String> findRenewalWhitelistedBranches(List<String> branchCodes);

    @Query(
            value = "SELECT branch_code FROM TB_ABOB_WHITELISTED_BRANCHES WHERE RENEWAL_ENABLED = 'Y'",
            nativeQuery = true
    )
    List<String> findAllRenewalEnabledBranches();


}
