package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_ABOB_BIP_API_LOG")
@Getter @Setter
@ToString
public class BipApiExecutionLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "API_UNIQUE_ID")
	private Long apiUniqueId;
	
	@Column(name= "APPLICATION_ID")
	private String applicationId;

	@Column(name = "API_NAME")
	private String apiName;

	@Column(name = "REQUEST_PAYLOAD")
	private String requestPayload;

	@Column(name = "RESPONSE_PAYLOAD")
	private String responsePayload;

	@Column(name = "ERROR_MESSAGE")
	private String errorMessage;

	@Column(name = "API_STATUS")
	private String apiStatus;

	@Column(name = "CREATE_TS")
	private LocalDateTime createTs;
	
	@Column(name = "CURRRENT_STAGE")
	private String currentStage;
	
	
}


