package com.iexceed.appzillonbanking.cob.core.repository.ab;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.BranchWageDetails;

@Repository
public interface BranchWageDetailsRepository extends CrudRepository<BranchWageDetails, String> {

	public BranchWageDetails findByBranchId(String branchId);

}
