package com.iexceed.appzillonbanking.cob.repository.ab;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.iexceed.appzillonbanking.cob.domain.ab.RoleAccessMap;
import com.iexceed.appzillonbanking.cob.domain.ab.RoleAccessMapId;

public interface RoleAccessMapRepository extends CrudRepository<RoleAccessMap, RoleAccessMapId> {
	List<RoleAccessMap> findByAppId(String appId);
}