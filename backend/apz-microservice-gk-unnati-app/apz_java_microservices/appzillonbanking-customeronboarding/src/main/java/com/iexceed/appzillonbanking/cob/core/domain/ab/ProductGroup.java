package com.iexceed.appzillonbanking.cob.core.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_PRODUCT_GROUP")
@IdClass(ProductGroupId.class)
@Getter @Setter
public class ProductGroup {
	
	@JsonProperty("appId")
	@Id
	private String appId;
	
	@JsonProperty("productGroupCode")
	@Id
	private String productGroupCode;
	
	@JsonProperty("productGroupName")
	@Column(name = "PRODUCT_GROUP_NAME")
	private String productGroupName;	
	
	@JsonProperty("productGroupDesc")
	@Column(name = "PRODUCT_GROUP_DESC")
	private String productGroupDesc;
	
	@JsonProperty("productGroupStatus")
	@Column(name = "PRODUCT_GROUP_STATUS")
	private String productGroupStatus;
	
	@JsonProperty("slNum")
	@Column(name = "SEQ_NO")
	private int slNum;

	
}