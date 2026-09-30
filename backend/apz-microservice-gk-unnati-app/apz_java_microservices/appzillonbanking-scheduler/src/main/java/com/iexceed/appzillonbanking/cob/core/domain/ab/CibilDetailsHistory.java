package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "TB_CGOB_CB_DTLS_HIS")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CibilDetailsHistory {

	@JsonProperty("cbDtlId")
	@Id
	@Column(name = "CB_DTLS_ID")
	private BigDecimal cbDtlId;
	
	@Column(name = "APPLICATION_ID")
	private String applicationId;
	
	@Column(name = "VERSION_NO")
	private int versionNum;
	
	@Column(name = "APP_ID")
	private String appId;
	
	@JsonProperty("custDtlId")
	@Column(name = "CUST_DTL_ID")
	private BigDecimal custDtlId;
	
	@JsonProperty("requestId")
	@Column(name = "REQUEST_ID")
	private String requestId;
	
	@JsonProperty("request")
	@Column(name = "REQUEST")
	private String request;
	
	@JsonProperty("responseId")
	@Column(name = "RESPONSE_ID")
	private String responseId;
	
	@JsonProperty("cbDate")
	@Column(name = "CB_DATE")
	private LocalDate cbDate;
	
	@JsonProperty("cbStatus")
	@Column(name = "CB_STATUS")
	private String cbStatus;

	@Column(name = "PAYLOAD")
	private String payloadColumn;

	@CreationTimestamp
	@Column(name = "HISTORY_TS")
	private LocalDateTime historyTs;

	@Column(name = "LOAN_AMOUNT")
	private String loanAmount;

	@JsonProperty("additionalInfo")
	@Column(name = "ADDITIONAL_INFO")
	private String additionalInfo;

	@Formula("COALESCE((ADDITIONAL_INFO::json -> 'map' ->> 'caglOs'), '')")
	private String caglOs;

	@Formula("COALESCE((ADDITIONAL_INFO::json -> 'map' ->> 'stage'), '')")
	private String stage;

	@Formula("COALESCE((ADDITIONAL_INFO::json -> 'map' ->> 'subStage'), '')")
	private String subStage;

}
