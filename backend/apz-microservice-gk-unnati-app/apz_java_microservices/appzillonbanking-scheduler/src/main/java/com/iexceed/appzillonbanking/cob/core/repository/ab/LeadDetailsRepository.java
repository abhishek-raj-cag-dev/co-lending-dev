package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cob.core.domain.ab.LeadDetails;


public interface LeadDetailsRepository extends CrudRepository<LeadDetails, BigDecimal> {

	List<LeadDetails> findAll();

	List<LeadDetails> findByKendraIdIn(List<String> kendraIdList);

	List<LeadDetails> findByKendraIdInAndCustomerIdNotIn(List<String> kendraIdList, List<String> customerIdList);

	Optional<LeadDetails> findByCustomerId(String customerId);

	Optional<LeadDetails> findByPid(String pid);

	@Query(value = "Select new LeadDetails(leadDetails.pid,leadDetails.firstName,leadDetails.kendraName,"
			+ "leadDetails.kendraId,leadDetails.kendraVintageYrs,leadDetails.kendraMeetingDay,leadDetails.customerId,leadDetails.caglOs,leadDetails.priority) "
			+ "FROM LeadDetails leadDetails WHERE "
			+ "leadDetails.kendraId in :kendraIdList AND leadDetails.customerId not in :customerIdList", nativeQuery = false)
	public List<LeadDetails> findUnactionedLeadsByKendraIdInAndCustomerIdNotIn(
			@Param("kendraIdList") List<String> kendraIdList, @Param("customerIdList") List<String> customerIdList);
}
