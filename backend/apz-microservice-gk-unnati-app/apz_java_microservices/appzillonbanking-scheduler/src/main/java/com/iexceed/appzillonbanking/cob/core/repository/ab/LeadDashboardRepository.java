package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.LeadDashboardDetails;

public interface LeadDashboardRepository extends CrudRepository<LeadDashboardDetails, String> {

	List<LeadDashboardDetails> findAll();

	List<LeadDashboardDetails> findByPid(String pid);

	List<LeadDashboardDetails> findByKendraIdIn(List<String> kendraIdList);

	List<LeadDashboardDetails> findBySourceOfLeadAndProductTypeAndKendraIdInAndCurrentStatusIn(String sourceOfLead,
			String productType, List<String> kendraIdList, List<String> allowedStatus);
	
	List<LeadDashboardDetails> findBySourceOfLeadAndProductTypeAndBranchIdInAndCurrentStatusIn(String sourceOfLead,
			String productType, List<String> branchIdList, List<String> allowedStatus);
}
