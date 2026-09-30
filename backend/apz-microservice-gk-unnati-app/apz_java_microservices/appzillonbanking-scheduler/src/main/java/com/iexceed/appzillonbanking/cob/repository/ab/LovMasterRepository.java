package com.iexceed.appzillonbanking.cob.repository.ab;

import org.springframework.data.repository.CrudRepository;
import com.iexceed.appzillonbanking.cob.domain.ab.LovMaster;

public interface LovMasterRepository extends CrudRepository<LovMaster, Integer> {

	Iterable<LovMaster> findByAppId(String appId);

	LovMaster findByLovName(String lovName);

}
