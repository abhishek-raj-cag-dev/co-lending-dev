package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkflowDefinitionId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "APP_ID", nullable = false)
	private String appId;
	
	@Column(name = "WORKFLOW_ID", nullable = false)
	private String workFlowId;
	
	@Column(name = "STAGE_SEQ_NO", nullable = false)
	private int stageSeqNum;
}
