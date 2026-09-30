package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.InsuranceDetailsPayload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_CGOB_INSURANCE_DTLS")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InsuranceDetails {

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
	
	@JsonProperty("custDtlId")
	@Column(name = "CUST_DTL_ID")
	private BigDecimal custDtlId;

	@Column(name = "PAYLOAD")
	private String payloadColumn;

	@Transient
	@JsonProperty("payload")
	private InsuranceDetailsPayload  payload;
	
	public InsuranceDetails(BigDecimal insuranceDtlId) {
		this.insuranceDtlId = insuranceDtlId;
	}
}
