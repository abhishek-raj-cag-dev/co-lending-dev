package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.OccupationDetailsPayload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_OCCUPATION_DETAILS")
@Getter @Setter
@NoArgsConstructor
public class OccupationDetails {

	@JsonProperty("occptDtlId")
	@Id
	@Column(name = "OCCPT_DTLS_ID")
	private BigDecimal occptDtlId;
	
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
	private OccupationDetailsPayload  payload;

	public OccupationDetails(BigDecimal occptDtlId) {
		this.occptDtlId = occptDtlId;
	}

	@Override
	public String toString() {
		return "OccupationDetails{" +
				"occptDtlId=" + occptDtlId +
				", applicationId='" + applicationId + '\'' +
				", versionNum=" + versionNum +
				", appId='" + appId + '\'' +
				", custDtlId=" + custDtlId +
				", payloadColumn='" + payloadColumn + '\'' +
				", payload=" + payload +
				'}';
	}
}
