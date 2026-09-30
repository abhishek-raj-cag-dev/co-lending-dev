package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.AddressDetailsPayload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_ADDRESS_DETAILS")
@Getter @Setter
@NoArgsConstructor
public class AddressDetails {

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
	
	@Transient
	@JsonProperty("payload")
	private AddressDetailsPayload payload;
	
	@Column(name = "REFERENCE_ID")
	private BigDecimal uniqueId;
	
	@Column(name = "ADDRESS_TYPE")
	private String addressType;
	
	public AddressDetails(BigDecimal addressDtlsId) {
		this.addressDtlsId = addressDtlsId;
	}

	@Override
	public String toString() {
		return "AddressDetails{" +
				"addressDtlsId=" + addressDtlsId +
				", applicationId='" + applicationId + '\'' +
				", versionNum=" + versionNum +
				", appId='" + appId + '\'' +
				", custDtlId=" + custDtlId +
				", payloadColumn='" + payloadColumn + '\'' +
				", payload=" + payload +
				", uniqueId=" + uniqueId +
				", addressType='" + addressType + '\'' +
				'}';
	}


	public AddressDetails(BigDecimal addressDtlsId, String applicationId, int versionNum, String appId,
			BigDecimal custDtlId, String payloadColumn, String addressType) {
		super();
		this.addressDtlsId = addressDtlsId;
		this.applicationId = applicationId;
		this.versionNum = versionNum;
		this.appId = appId;
		this.custDtlId = custDtlId;
		this.payloadColumn = payloadColumn;
		this.addressType = addressType;
	}
	
}
