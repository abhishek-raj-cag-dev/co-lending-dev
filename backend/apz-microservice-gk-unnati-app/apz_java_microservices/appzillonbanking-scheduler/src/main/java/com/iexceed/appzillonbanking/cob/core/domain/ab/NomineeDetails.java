package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.NomineeDetailsPayload;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_NOMINEE_DETAILS")
@Getter @Setter
public class NomineeDetails {

	@JsonProperty("nomineeDtlsId")
	@Id
	@Column(name = "NOMINEE_DTLS_ID")
	private BigDecimal nomineeDtlsId;
	
	@Column(name = "APPLICATION_ID")
	private String applicationId;
	
	@Column(name = "VERSION_NO")
	private int versionNum;
	
	@Column(name = "APP_ID")
	private String appId;

	@JsonProperty("custDtlId")
	@Column(name = "CUST_DTL_ID")
	private BigDecimal custDtlId;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "PAYLOAD")
	private String payloadColumn;
	
	@Transient
	@JsonProperty("payload")
	private NomineeDetailsPayload payload;

	public NomineeDetails() {}
	
	public NomineeDetails(BigDecimal nomineeDtlsId, String applicationId, int versionNum, String appId,
			BigDecimal custDtlId, String status, String payloadColumn) {
		this.nomineeDtlsId = nomineeDtlsId;
		this.applicationId = applicationId;
		this.versionNum = versionNum;
		this.appId = appId;
		this.custDtlId = custDtlId;
		this.status = status;
		this.payloadColumn = payloadColumn;
	}

	@Override
	public String toString() {
		return "NomineeDetails{" +
				"nomineeDtlsId=" + nomineeDtlsId +
				", applicationId='" + applicationId + '\'' +
				", versionNum=" + versionNum +
				", appId='" + appId + '\'' +
				", custDtlId=" + custDtlId +
				", status='" + status + '\'' +
				", payloadColumn='" + payloadColumn + '\'' +
				", payload=" + payload +
				'}';
	}
}