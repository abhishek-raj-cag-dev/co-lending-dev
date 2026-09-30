package com.iexceed.appzillonbanking.cob.core.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_ABOB_RPC_STAGE_VERIFICATION")
@Getter
@Setter
@ToString
public class RpcStageVerification {

	@Id
	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@Column(name = "EDITED_FIELDS")
	private String editedFields;
	
	@Column(name = "QUERIES")
	private String queries;
	
	@Column(name = "VERIFIED_STAGES")
	private String verifiedStages;
	
}
