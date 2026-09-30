package com.iexceed.appzillonbanking.cob.loans.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cob.loans.domain.user.BranchAreaMappingDetails;

public interface TATBranchDetailsRepository extends CrudRepository<BranchAreaMappingDetails, String> {

	@Query(value = "select new BranchAreaMappingDetails(regionState.stateId,stateData.stateName, "
			+ "regionData.regionName, areaData.areaName, branchArea.branchId) FROM "
			+ "RegionStateMappingDetails regionState "
			+ "left outer join RegionDataDetails regionData on regionState.regionId=regionData.regionId "
			+ "left outer join AreaRegionMappingDetails areaRegion on regionState.regionId=areaRegion.regionId "
			+ "left outer join AreaDataDetails areaData on areaRegion.areaId=areaData.areaId "
			+ "left outer join BranchAreaMappingDetails branchArea on branchArea.areaId=areaData.areaId "
			+ "left outer join StateDataDetails stateData on stateData.stateId=regionState.stateId "
			+ "where regionState.stateId = :stateId AND branchArea.branchId is not null ", nativeQuery = false)
	public List<BranchAreaMappingDetails> findBranchIdDetailsByStateId(@Param("stateId") Integer stateId);
	
    @Query(value = "select new BranchAreaMappingDetails(regionState.stateId,stateData.stateName, "
            + "regionData.regionName, areaData.areaName, branchArea.branchId) FROM "
            + "RegionStateMappingDetails regionState "
            + "left outer join RegionDataDetails regionData on regionState.regionId=regionData.regionId "
            + "left outer join AreaRegionMappingDetails areaRegion on regionState.regionId=areaRegion.regionId "
            + "left outer join AreaDataDetails areaData on areaRegion.areaId=areaData.areaId "
            + "left outer join BranchAreaMappingDetails branchArea on branchArea.areaId=areaData.areaId "
            + "left outer join StateDataDetails stateData on stateData.stateId=regionState.stateId "
            + "where regionState.stateId in :stateIds AND branchArea.branchId is not null ", nativeQuery = false)
    public List<BranchAreaMappingDetails> findBranchIdDetailsByStateIdList(@Param("stateIds") List<Integer> stateIds);
	
	@Query(value = "select new BranchAreaMappingDetails(branchArea.branchId) FROM "
			+ "BranchAreaMappingDetails branchArea "
			+ "left outer join AreaRegionMappingDetails areaRegion on branchArea.areaId=areaRegion.areaId "
			+ "where areaRegion.regionId in :regionId AND branchArea.branchId is not null ", nativeQuery = false)
	public List<BranchAreaMappingDetails> findBranchIdDetailsByRPCId(List<Integer> regionId);

	@Query(value = "select new BranchAreaMappingDetails(branchArea.branchId) FROM "
			+" BranchAreaMappingDetails branchArea "
			+" where branchArea.areaId in :areaId AND branchArea.branchId is not null ", nativeQuery = false)
	public List<BranchAreaMappingDetails> findBranchIdByAreaId(List<Integer> areaId);

	@Query(
			"SELECT b FROM BranchAreaMappingDetails b " +
					"WHERE b.branchId IN :branchIds"
	)
	public List<BranchAreaMappingDetails> findByBranchList(
			@Param("branchIds") List<String> branchIds
	);


	
}
