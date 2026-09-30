package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ApiExecutionLog;

import javax.transaction.Transactional;

@Repository
public interface ApiExecutionLogRepository extends CrudRepository<ApiExecutionLog, Long> {

	 Optional<ApiExecutionLog> findFirstByApplicationIdAndApiName(String applicationId, String apiName);
	 
	 List<ApiExecutionLog> findAllByApplicationIdAndApiNameAndCurrentStage(String applicationId, String apiName, String currentStage);

	Optional<ApiExecutionLog> findTopByApplicationIdAndApiNameOrderByCreateTsDesc(String applicationId, String string);

	List<ApiExecutionLog> findAllByApiStatusAndApiName(String apiStatus, String apiName);

	@Modifying
    @Transactional
	@Query("UPDATE ApiExecutionLog a SET a.apiStatus = :apiStatus WHERE a.applicationId = :applicationId and a.apiStatus = 'Success' ")
	void updateStatusAfterDMSPush(@Param("apiStatus") String apiStatus, @Param("applicationId") String applicationId);
	
}
