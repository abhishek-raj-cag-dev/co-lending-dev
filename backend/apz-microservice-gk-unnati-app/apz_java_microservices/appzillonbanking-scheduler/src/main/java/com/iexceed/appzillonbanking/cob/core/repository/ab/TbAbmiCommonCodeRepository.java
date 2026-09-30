package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.TbAbmiCommonCodeDomain;
import com.iexceed.appzillonbanking.cob.core.domain.ab.TbAbmiCommonCodeId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbAbmiCommonCodeRepository extends CrudRepository<TbAbmiCommonCodeDomain, TbAbmiCommonCodeId> {
	
	List<TbAbmiCommonCodeDomain> findAllByCodeAndAccessType(String code, String accessType);
	
	List<TbAbmiCommonCodeDomain> findAllByAccessType(String accessType);
	
	List<TbAbmiCommonCodeDomain> findAllByCode(String code);
	
	TbAbmiCommonCodeDomain findByCode(String cmCode);
}