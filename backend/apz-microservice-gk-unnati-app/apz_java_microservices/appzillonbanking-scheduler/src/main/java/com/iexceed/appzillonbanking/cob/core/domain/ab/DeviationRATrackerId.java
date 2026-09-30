package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeviationRATrackerId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "APPLICATION_ID", nullable = false)
	private String applicationId;

	@Column(name = "record_id", nullable = false)
	private String recordId;

}