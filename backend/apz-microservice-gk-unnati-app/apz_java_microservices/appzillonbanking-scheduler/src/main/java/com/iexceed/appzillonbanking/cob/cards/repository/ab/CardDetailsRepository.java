package com.iexceed.appzillonbanking.cob.cards.repository.ab;


import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillonbanking.cob.cards.domain.ab.CardDetails;

import java.math.BigDecimal;
import java.util.List;


public interface CardDetailsRepository extends CrudRepository<CardDetails, BigDecimal> {
	
	@Transactional
	void deleteByApplicationIdAndAppId(String applicationId, String appId);
	
	List<CardDetails> findByApplicationIdAndAppId(String applicationId, String appId);
	
	CardDetails findByApplicationIdAndAppIdAndVersionNum(String applicationId, String appId, int versionNum);
}