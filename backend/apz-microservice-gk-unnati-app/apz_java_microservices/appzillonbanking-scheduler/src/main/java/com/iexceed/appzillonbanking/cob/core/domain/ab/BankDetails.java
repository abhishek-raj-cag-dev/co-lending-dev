package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.BankDetailsPayload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_CGOB_BANK_DTLS")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankDetails {

	@JsonProperty("bankDtlId")
	@Id
	@Column(name = "BANK_DTLS_ID")
	private BigDecimal bankDtlId;
	
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
	private BankDetailsPayload  payload;

	@Override
	public String toString() {
		return "BankDetails{" +
				"bankDtlId=" + bankDtlId +
				", applicationId='" + applicationId + '\'' +
				", versionNum=" + versionNum +
				", appId='" + appId + '\'' +
				", custDtlId=" + custDtlId +
				", payloadColumn='" + payloadColumn + '\'' +
				", payload=" + payload +
				'}';
	}
	
	@Formula("(payload::json ->> 'accountNumber')")
	private String accountNumber;
	

}
