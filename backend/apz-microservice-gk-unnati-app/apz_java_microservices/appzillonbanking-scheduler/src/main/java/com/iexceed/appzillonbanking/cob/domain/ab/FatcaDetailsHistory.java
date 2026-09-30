package com.iexceed.appzillonbanking.cob.domain.ab;

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
@Table(name = "TB_ABOB_FATCA_DETAILS_HIS")
@Getter @Setter
public class FatcaDetailsHistory {

	@JsonProperty("fatcaDtlsId")
	@Id
	@Column(name = "FATCA_DTLS_ID")
	private BigDecimal fatcaDtlsId;

	@Column(name = "APP_ID")
	private String appId;
		
	@Column(name = "APPLICATION_ID")
	private String applicationId;
	
	@Column(name = "VERSION_NO")
	private int versionNum;
	
	@JsonProperty("custDtlId")
	@Column(name = "CUST_DTL_ID")
	private BigDecimal custDtlId;
	
	@Column(name = "PAYLOAD")
	private String payloadColumn;
	
	@CreationTimestamp
	@Column(name = "HISTORY_TS")
	private LocalDateTime historyTs;
}