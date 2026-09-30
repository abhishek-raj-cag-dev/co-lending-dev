package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ProductGroupId implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "APP_ID", nullable = false)
	private String appId;
	
	@Column(name = "PRODUCT_GROUP_CODE",nullable = false)
	private String productGroupCode;

	
}