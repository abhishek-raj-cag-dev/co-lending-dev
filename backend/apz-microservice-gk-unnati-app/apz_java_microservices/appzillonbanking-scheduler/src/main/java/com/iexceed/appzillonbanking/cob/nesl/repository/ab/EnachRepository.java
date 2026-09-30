package com.iexceed.appzillonbanking.cob.nesl.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillonbanking.cob.nesl.domain.ab.Enach;


@Repository
public interface EnachRepository extends JpaRepository<Enach, Long> {
	
	Optional<Enach> findByApplicationIdAndCustomerType(String applicationId, String applicantType);

	Optional<Enach> findByApplicationIdAndCustomerTypeAndEnachReqId(String applicationId, String applicantType,
			String paynimoRequestId);
	
	Optional<List<Enach>> findByApplicationIdAndAppId(String applicationId, String appId);

    //List<Enach> findByApplicationIdAndAppId(String applicationId, String appId);
	
	@Transactional
	int deleteByApplicationIdAndCustomerType(String applicationId, String customerType);

	@Transactional
	int deleteByApplicationId(String applicationId);


}
