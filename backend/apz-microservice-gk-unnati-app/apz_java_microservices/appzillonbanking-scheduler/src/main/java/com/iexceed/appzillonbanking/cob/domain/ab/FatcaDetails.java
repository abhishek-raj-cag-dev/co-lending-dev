package com.iexceed.appzillonbanking.cob.domain.ab;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.payload.FatcaDetailsPayload;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_FATCA_DETAILS")
@Getter @Setter
public class FatcaDetails {
	
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
	
	@Transient
	@JsonProperty("payload")
	private FatcaDetailsPayload payload;

	@Override
	public String toString() {
		return "FatcaDetails{" +
				"fatcaDtlsId=" + fatcaDtlsId +
				", appId='" + appId + '\'' +
				", applicationId='" + applicationId + '\'' +
				", versionNum=" + versionNum +
				", custDtlId=" + custDtlId +
				", payloadColumn='" + payloadColumn + '\'' +
				", payload=" + payload +
				'}';
	}
}