package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_CUSTOMER_DETAILS_HIS")
@Getter @Setter
public class CustomerDetailsHistory {

	@JsonProperty("custDtlId")
	@Id
	@Column(name = "CUST_DTL_ID")
	private BigDecimal custDtlId;
	
	@Column(name = "APPLICATION_ID")
	private String applicationId;
	
	@Column(name = "VERSION_NO")
	private int versionNum;
	
	@Column(name = "APP_ID")
	private String appId;
	
	@Column(name = "CUSTOMER_TYPE")
	private String customerType;
	
	@Column(name = "CUSTOMER_NAME")
	private String customerName;
	
	@Column(name = "MOBILE_NUMBER")
	private String mobileNumber;
	
	@Column(name = "KYC_STATUS")
	private String kycStatus;
	
	@Column(name = "AML_STATUS")
	private String amlStatus;
	
	@Column(name = "CUSTOMER_ID")
	private BigDecimal customerId;

	@Column(name = "PAYLOAD")
	private String payloadColumn;
	
	@CreationTimestamp
	@Column(name = "HISTORY_TS")
	private LocalDateTime historyTs;
}