package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.CADeviationMaster;

@Repository
public interface CADeviationMasterRepository extends CrudRepository<CADeviationMaster, String> {
	List<CADeviationMaster> findByProduct(String product);

	List<CADeviationMaster> findByProductAndActive(String productCode, String yes);
	
}