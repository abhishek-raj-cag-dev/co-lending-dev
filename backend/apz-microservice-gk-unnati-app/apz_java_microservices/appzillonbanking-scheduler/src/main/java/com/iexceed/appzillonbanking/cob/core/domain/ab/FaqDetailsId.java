package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FaqDetailsId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "APP_ID", nullable = false)
	private String appId;
	
	@Column(name = "PRODUCT", nullable = false)
	private String product;
	
	@Column(name = "STAGE", nullable = false)
	private String stage;
	
	@Column(name = "SEQ_NUM", nullable = false)
	private String seqNum;		
}