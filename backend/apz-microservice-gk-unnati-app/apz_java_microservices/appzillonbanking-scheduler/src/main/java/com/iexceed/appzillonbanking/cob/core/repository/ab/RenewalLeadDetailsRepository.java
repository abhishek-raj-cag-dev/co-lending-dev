package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cob.core.domain.ab.RenewalLeadDetails;


public interface RenewalLeadDetailsRepository extends CrudRepository<RenewalLeadDetails, BigDecimal> {

	List<RenewalLeadDetails> findAll();

	List<RenewalLeadDetails> findByKendraIdIn(List<String> kendraIdList);

	List<RenewalLeadDetails> findByKendraIdInAndCustomerIdNotIn(List<String> kendraIdList, List<String> customerIdList);

	Optional<RenewalLeadDetails> findByCustomerId(String customerId);

	Optional<RenewalLeadDetails> findByPid(String pid);

	@Query(value = "Select new RenewalLeadDetails(leadDetails.pid,leadDetails.firstName,leadDetails.kendraName,"
			+ "leadDetails.kendraId,leadDetails.kendraVintageYrs,leadDetails.kendraMeetingDay,leadDetails.customerId,leadDetails.caglOs,leadDetails.priority) "
			+ "FROM RenewalLeadDetails leadDetails WHERE "
			+ "leadDetails.kendraId in :kendraIdList AND leadDetails.customerId not in :customerIdList", nativeQuery = false)
	public List<RenewalLeadDetails> findUnactionedLeadsByKendraIdInAndCustomerIdNotIn(
			@Param("kendraIdList") List<String> kendraIdList, @Param("customerIdList") List<String> customerIdList);
}
