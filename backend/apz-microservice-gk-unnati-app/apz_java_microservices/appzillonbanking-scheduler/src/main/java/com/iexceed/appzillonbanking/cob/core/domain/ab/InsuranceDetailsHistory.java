package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_CGOB_INSURANCE_DTLS_HIS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InsuranceDetailsHistory {

	@JsonProperty("insuranceDtlId")
	@Id
	@Column(name = "INSURANCE_DTLS_ID")
	private BigDecimal insuranceDtlId;

	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@Column(name = "VERSION_NO")
	private int versionNum;

	@Column(name = "APP_ID")
	private String appId;

	@Column(name = "CUST_DTL_ID")
	private BigDecimal custDtlId;

	@Column(name = "PAYLOAD")
	private String payloadColumn;

	@CreationTimestamp
	@Column(name = "HISTORY_TS")
	private LocalDateTime historyTs;
}	
