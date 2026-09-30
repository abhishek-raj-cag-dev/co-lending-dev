package com.iexceed.appzillonbanking.cob.repository.ab;

import org.springframework.data.repository.CrudRepository;
import com.iexceed.appzillonbanking.cob.domain.ab.Branches;

public interface BranchesRepository extends CrudRepository<Branches, String> {

	Iterable<Branches> findByPinCode(String pinCode);
	
	Iterable<Branches> findByStateCodeAndCityCode(String stateCode, String cityCode);
}