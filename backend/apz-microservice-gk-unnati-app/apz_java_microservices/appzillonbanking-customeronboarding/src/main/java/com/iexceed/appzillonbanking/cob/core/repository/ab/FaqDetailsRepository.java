package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.FaqDetails;
import com.iexceed.appzillonbanking.cob.core.domain.ab.FaqDetailsId;

@Repository
public interface FaqDetailsRepository extends CrudRepository<FaqDetails, FaqDetailsId> {

	List<FaqDetails> findByAppIdAndProductAndStageOrderBySeqNumAsc(String appId, String product, String stage);
	
}