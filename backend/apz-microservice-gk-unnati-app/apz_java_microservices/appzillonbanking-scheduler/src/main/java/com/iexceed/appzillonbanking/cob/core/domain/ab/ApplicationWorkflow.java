package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_APPLN_WORKFLOW")
@IdClass(ApplicationWorkflowId.class)
@Getter @Setter
public class ApplicationWorkflow {

	@Id
	private String appId;
	
	@Id
	private String applicationId;
	
	@Id
	private int versionNum;
	
	@Id
	private int workflowSeqNum;
	
	@JsonProperty("createdBy")
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@JsonProperty("createTs")
	@Column(name = "CREATED_TS")
	private LocalDateTime createTs;
	
	@JsonProperty("applicationStatus")
	@Column(name = "APPLICATION_STATUS")
	private String applicationStatus;
	
	@JsonProperty("remarks")
	@Column(name = "REMARKS")
	private String remarks;
	
	@JsonProperty("currentRole")
	@Column(name = "CURR_ROLE")
	private String currentRole;
	
	@JsonProperty("nextWorkFlowStage")
	@Column(name = "NEXT_WORKFLOW_STAGE")
	private String nextWorkFlowStage;

	public ApplicationWorkflow(String appId, String applicationId, int versionNum, int workflowSeqNum, String createdBy,
			LocalDateTime createTs, String applicationStatus, String remarks, String currentRole, String nextWorkFlowStage) {
		this.appId = appId;
		this.applicationId = applicationId;
		this.versionNum = versionNum;
		this.workflowSeqNum = workflowSeqNum;
		this.createdBy = createdBy;
		this.createTs = createTs;
		this.applicationStatus = applicationStatus;
		this.remarks = remarks;
		this.currentRole = currentRole;
		this.nextWorkFlowStage = nextWorkFlowStage;
	}
	
	public ApplicationWorkflow() {}

	@Override
	public String toString() {
		return "ApplicationWorkflow [appId=" + appId + ", applicationId=" + applicationId + ", versionNum=" + versionNum
				+ ", workflowSeqNum=" + workflowSeqNum + ", createdBy=" + createdBy + ", createTs=" + createTs
				+ ", applicationStatus=" + applicationStatus + ", remarks=" + remarks + ", currentRole=" + currentRole
				+ ", nextWorkFlowStage=" + nextWorkFlowStage + "]";
	}
	
}
