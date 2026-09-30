package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.LUCEntity;

@Repository
public interface LucRepository extends JpaRepository<LUCEntity,String> {
	

	Optional<LUCEntity> findByApplicationIdAndAppId(String applicationId, String appId);

}
