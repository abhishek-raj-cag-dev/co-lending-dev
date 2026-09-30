package com.iexceed.appzillonbanking.cob.loans.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cob.domain.cdh.GkKendraData;
import com.iexceed.appzillonbanking.cob.loans.domain.user.OfficeData;

public interface OfficeDataRepo extends JpaRepository<OfficeData, String> {

	/*@Query(value = "SELECT gkBranchData FROM OfficeData gkBranchData " +
            " WHERE gkBranchData.stateId IN (:stateIds) ", nativeQuery = false)
    List<OfficeData> findBranchDetailsByStateId(@Param("stateIds") List<String> stateIds);
	
	@Query(value = "SELECT gkBranchData FROM OfficeData gkBranchData " +
            " WHERE gkBranchData.regionId IN (:regionId) ", nativeQuery = false)
    List<OfficeData> findBranchDetailsByregionId(@Param("regionIds") List<String> regionIds);
	
	@Query(value = "SELECT gkBranchData FROM OfficeData gkBranchData " +
            " WHERE gkBranchData.areaId IN (:areaIds) ", nativeQuery = false)
    List<OfficeData> findBranchDetailsByAreaId(@Param("areaIds") List<String> areaIds);
	*/
	@Query(value = ("select * from t24_office where region_id in :regionIds"), nativeQuery = true)
	public List<OfficeData> findBranchIdDetailsByRPCId(@Param("regionIds") List<String> regionIds);
	
	@Query(value = ("select * from t24_office where area_id in :areaIds"), nativeQuery = true)
	public List<OfficeData> findBranchIdByAreaId(@Param("areaIds") List<String> areaIds);
	
}
