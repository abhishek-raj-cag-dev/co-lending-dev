package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.ExistingLoanDetailsPayload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_CGOB_EXISTINGLOANS_DTLS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ExistingLoanDetailsId.class)
public class ExistingLoanDetails {

	@JsonProperty("loanDtlsId")
	@Id
	private BigDecimal loanDtlsId;
	
	@Id
	private BigDecimal existingLoanId;

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
	@JsonProperty("hasExistingLoans")
	private String hasExistingLoans;

	@Transient
	@JsonProperty("payload")
	private ExistingLoanDetailsPayload payload;

	@Override
	public String toString() {
		return "ExistingLoanDetails [loanDtlsId=" + loanDtlsId + ", applicationId=" + applicationId + ", versionNum="
				+ versionNum + ", appId=" + appId + ", custDtlId=" + custDtlId + ", payloadColumn=" + payloadColumn
				+ ", hasExistingLoans=" + hasExistingLoans + ", payload=" + payload + "]";
	}

}