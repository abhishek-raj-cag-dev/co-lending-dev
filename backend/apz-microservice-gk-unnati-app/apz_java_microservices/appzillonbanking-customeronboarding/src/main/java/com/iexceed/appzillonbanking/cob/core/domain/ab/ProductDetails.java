package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_PRODUCT_DETAILS")
@Getter @Setter
public class ProductDetails {

	@JsonProperty("productCode")
	@Id
	@Column(name = "PRODUCT_CODE")
	private String productCode;
	
	@JsonProperty("productGroupCode")
	@Column(name = "PRODUCT_GROUP_CODE")
	private String productGroupCode;
	
	@JsonProperty("productName")
	@Column(name = "PRODUCT_NAME")
	private String productName;
	
	@JsonProperty("productDesc")
	@Column(name = "PRODUCT_DESC")
	private String productDesc;
	
	@JsonProperty("startDate")
	@Column(name = "START_DATE")
	private LocalDate startDate;
	
	@JsonProperty("endDate")
	@Column(name = "END_DATE")
	private LocalDate endDate;
	
	@JsonProperty("productFeatures")
	@Column(name = "PRODUCT_FEATURES")
	private String productFeatures;
	
	@JsonProperty("productStatus")
	@Column(name = "PRODUCT_STATUS")
	private String productStatus;
	
	@JsonProperty("productRule")
	@Column(name = "PRODUCT_RULE")
	private String productRule;
	
	@JsonProperty("productType")
	@Column(name = "PRODUCT_TYPE")
	private String productType;
	
	@JsonProperty("slNum")
	@Column(name = "SEQ_NO")
	private int slNum;
}