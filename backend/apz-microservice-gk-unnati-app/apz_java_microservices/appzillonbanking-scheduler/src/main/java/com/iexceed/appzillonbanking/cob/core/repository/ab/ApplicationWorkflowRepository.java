package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationWorkflowId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationWorkflowRepository extends CrudRepository<ApplicationWorkflow, ApplicationWorkflowId> {

	Optional<ApplicationWorkflow> findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(String appId, String applicationId, int versionNum);

	List<ApplicationWorkflow> findByAppIdAndApplicationIdAndApplicationStatusOrderByCreateTsAsc(String appId, String applicationId, String applicationrejectedstatus);

	List<ApplicationWorkflow> findByApplicationIdAndApplicationStatusInOrderByWorkflowSeqNum(String applicationId, List<String> statusList);
	
	List<ApplicationWorkflow> findByApplicationIdAndApplicationStatusInOrderByWorkflowSeqNumAsc(String applicationId, List<String> statusList);
	
	List<ApplicationWorkflow> findByApplicationIdAndApplicationStatusInOrderByWorkflowSeqNumDesc(String applicationId, List<String> statusList);
	
	List<ApplicationWorkflow> findByApplicationIdAndApplicationStatusInOrderByCreateTsDesc(String applicationId, List<String> statusList);
	
	List<ApplicationWorkflow> findByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumAsc(String appId, String applicationId, int versionNum);

	boolean existsByApplicationIdAndApplicationStatus(
			String applicationId,
			String applicationStatus
	);

}
