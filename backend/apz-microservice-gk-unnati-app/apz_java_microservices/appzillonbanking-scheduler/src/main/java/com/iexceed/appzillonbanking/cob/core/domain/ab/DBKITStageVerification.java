package com.iexceed.appzillonbanking.cob.core.domain.ab;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_ABOB_DB_KIT_STAGE_VERIFICATION")
@Getter
@Setter
@ToString
public class DBKITStageVerification {
    @Id
	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@Column(name = "APPROVED_DOCS")
	private String approvedDocs;
	
	@Column(name = "QUERIES")
	private String queries;
	
	@Column(name = "VERIFIED_STAGES")
	private String verifiedStages;

	@Column(name = "RESPONSE")
	private String response;
	
	@Column(name = "QUERY_DOCS")
	private String queryDocs;

	@Column(name = "REUPLOADED_DOCS")
	private String reuploadedDocs;
}
