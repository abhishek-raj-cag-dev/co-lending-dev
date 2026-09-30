package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class WorkFlowDetails {	
	
	@Override
	public String toString() {
		return "WorkFlowDetails [workflowId=" + workflowId + ", currentStage=" + currentStage + ", action=" + action
				+ ", seqNo=" + seqNo + ", nextStageId=" + nextStageId + ", currentRole=" + currentRole + ", remarks="
				+ remarks + ", nextWorkflowStatus=" + nextWorkflowStatus + "]";
	}

	@JsonProperty("workflowId")
	private String workflowId;
	
	@JsonProperty("currentStage")
	private String currentStage;
	
	@JsonProperty("action")
	private String action;
	
	@JsonProperty("seqNo")
	private int seqNo;
	
	@JsonProperty("nextStageId")
	private String nextStageId;
	
	@JsonProperty("currentRole")
	private String currentRole;
	
	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("nextWorkflowStatus")
	private String nextWorkflowStatus;
}