package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ApplicationMasterId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "APP_ID", nullable = false)
	private String appId;

	@Column(name = "APPLICATION_ID", nullable = false)
	private String applicationId;

	@Column(name = "LATEST_VERSION_NO", nullable = false)
	private Integer versionNum;
}