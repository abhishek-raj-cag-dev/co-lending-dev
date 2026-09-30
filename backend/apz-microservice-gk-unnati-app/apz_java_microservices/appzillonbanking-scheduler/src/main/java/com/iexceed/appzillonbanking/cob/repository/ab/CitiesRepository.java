package com.iexceed.appzillonbanking.cob.repository.ab;

import org.springframework.data.repository.CrudRepository;
import com.iexceed.appzillonbanking.cob.domain.ab.Cities;

public interface CitiesRepository  extends CrudRepository<Cities, String> {

	public Iterable<Cities> findByStateCodeAndAppId(String stateCode, String appId);

}
