package com.iexceed.appzillonbanking.cob.repository.ab;

import org.springframework.data.repository.CrudRepository;
import com.iexceed.appzillonbanking.cob.domain.ab.States;

public interface StatesRepository extends CrudRepository<States, String> {

	Iterable<States> findByAppId(String appId);

}
