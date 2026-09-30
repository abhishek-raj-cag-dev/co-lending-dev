package com.iexceed.appzillonbanking.cob.loans.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cob.loans.domain.user.KendraDetails;

public interface KendraDetailsRepository extends CrudRepository<KendraDetails, String> {

	@Query(value ="SELECT T24_ID FROM tb_kendra_data WHERE BRANCH_ID= :branchId and T24_ID IS NOT NULL", nativeQuery = true)
	List<String> findKendraIdByBranchId(@Param("branchId") String branchId);
	
    @Query(value ="SELECT * FROM tb_kendra_data WHERE BRANCH_ID= :branchId and T24_ID IS NOT NULL", nativeQuery = true)
    List<KendraDetails> findKendraDetailsByBranchId(@Param("branchId") String branchId);
	
	@Query(value ="SELECT T24_ID FROM tb_kendra_data WHERE HANDLED_BY=:handledBy and T24_ID IS NOT NULL", nativeQuery = true)
	List<String> findKendraIdByHandledBy(@Param("handledBy") String handledBy);
	
    @Query(value ="SELECT * FROM tb_kendra_data WHERE HANDLED_BY=:handledBy and T24_ID IS NOT NULL", nativeQuery = true)
    List<KendraDetails> findKendraDetailsByHandledBy(@Param("handledBy") String handledBy);
	
	@Query(value ="SELECT T24_ID FROM tb_kendra_data WHERE BRANCH_ID in :branchIds and T24_ID IS NOT NULL", nativeQuery = true)
	List<String> findKendraIdByBranchIdIn(@Param("branchIds") List<String> branchIds);

    @Query(value ="SELECT * FROM tb_kendra_data WHERE BRANCH_ID in :branchIds and T24_ID IS NOT NULL", nativeQuery = true)
    List<KendraDetails> findKendraDetailsByBranchIdIn(@Param("branchIds") List<String> branchIds);
}
