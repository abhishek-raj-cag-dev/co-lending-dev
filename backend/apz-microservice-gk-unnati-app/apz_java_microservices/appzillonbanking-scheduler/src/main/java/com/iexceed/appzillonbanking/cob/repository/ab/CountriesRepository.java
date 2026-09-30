package com.iexceed.appzillonbanking.cob.repository.ab;

import org.springframework.data.repository.CrudRepository;
import com.iexceed.appzillonbanking.cob.domain.ab.Countries;

public interface CountriesRepository extends CrudRepository<Countries, String> {

	Iterable<Countries> findByAppId(String appId);

}