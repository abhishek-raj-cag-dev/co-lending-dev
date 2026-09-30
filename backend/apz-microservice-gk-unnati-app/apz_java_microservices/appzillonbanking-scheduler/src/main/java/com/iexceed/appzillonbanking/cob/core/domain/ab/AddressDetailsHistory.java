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
@Table(name = "TB_ABOB_ADDRESS_DETAILS_HIS")
@Getter @Setter
public class AddressDetailsHistory {
	
	@JsonProperty("addressDtlsId")
	@Id
	@Column(name = "ADDRESS_DTLS_ID")
	private BigDecimal addressDtlsId;
	
	@Column(name = "APPLICATION_ID")
	private String applicationId;
	
	@Column(name = "VERSION_NO")
	private int versionNum;
	
	@Column(name = "APP_ID")
	private String appId;
	
	@JsonProperty("custDtlId")
	@Column(name = "CUST_DTL_ID")
	private BigDecimal custDtlId;
	
	@Column(name = "PAYLOAD")
	private String payloadColumn;
	
	@Column(name = "REFERENCE_ID") 
	private BigDecimal uniqueId;
	
	@Column(name = "ADDRESS_TYPE")
	private String addressType;
	
	@CreationTimestamp
	@Column(name = "HISTORY_TS")
	private LocalDateTime historyTs;
}