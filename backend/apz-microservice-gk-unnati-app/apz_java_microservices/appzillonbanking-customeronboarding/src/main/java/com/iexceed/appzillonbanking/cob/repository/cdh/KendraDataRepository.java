package com.iexceed.appzillonbanking.cob.repository.cdh;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cob.domain.cdh.GkKendraData;
import com.iexceed.appzillonbanking.cob.loans.domain.user.KendraDetails;

public interface KendraDataRepository extends JpaRepository<GkKendraData, Integer> {

	@Query(value = ("select * from gk_kendra_data where kmid = :userId"), nativeQuery = true)
	public List<GkKendraData> findKendraList(String userId);
	
	@Query(value = ("select KENDRAID from gk_kendra_data where kmid = :userId"), nativeQuery = true)
	public List<String> findKendraIdByKmId(String userId);
	
	@Query(value = ("select KENDRAID from gk_kendra_data where BRANCHID = :branchId"), nativeQuery = true)
	public List<String> findKendraIdByBranchId(String branchId);
	
	@Query(value ="select KENDRAID from gk_kendra_data where BRANCHID in :branchIds", nativeQuery = true)
	public List<String> findKendraIdByBranchIdIn(@Param("branchIds") List<String> branchIds);
	
}
