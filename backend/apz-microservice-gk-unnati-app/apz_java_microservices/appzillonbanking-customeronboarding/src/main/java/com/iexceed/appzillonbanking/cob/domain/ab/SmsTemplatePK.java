package com.iexceed.appzillonbanking.cob.domain.ab;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmsTemplatePK implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "ACTION_TYPE", nullable = false)
	private String actionType;

	@Column(name = "LANGUAGE", nullable = false)
	private String language;

	@Column(name = "APP_ID", nullable = false)
	private String appId;
	
	
}