package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.WorkflowDefinition;
import com.iexceed.appzillonbanking.cob.core.domain.ab.WorkflowDefinitionId;

@Repository
public interface WorkflowDefinitionRepository extends CrudRepository<WorkflowDefinition, WorkflowDefinitionId> {

	List<WorkflowDefinition> findByFromStageId(String nextWorkFlowStage);
	
	Optional<WorkflowDefinition> findByAppIdAndWorkFlowIdAndFromStageId(String appId, String workFlowId,
			String fromStageId);
	WorkflowDefinition findByWorkFlowIdAndFromStageIdAndAction(String workFlowId, String fromStageId, String action);
}
