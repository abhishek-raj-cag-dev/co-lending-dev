package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.CibilDetailsPayload;

import lombok.*;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "TB_CGOB_CB_DTLS")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CibilDetails {

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

	@Transient
	@JsonProperty("payload")
	private CibilDetailsPayload payload;

	@Transient
	@JsonProperty("applicationMaster")
	private ApplicationMaster applicationMaster;

	@Transient
	@JsonProperty("customerDetails")
	private CustomerDetails customerDetails;

	@Formula("CASE " +
			"WHEN (payload::json ->> 'eligibleAmt') ~ '^[0-9]+(\\.[0-9]+)?$' THEN (payload::json ->> 'eligibleAmt')::numeric " +
			"ELSE 0 " +
			"END")
	private BigDecimal eligibleAmt;

	@JsonProperty("additionalInfo")
	@Column(name = "ADDITIONAL_INFO")
	private String additionalInfo;


	public CibilDetails(BigDecimal cbDtlId){
		this.cbDtlId = cbDtlId;
	}


	public CibilDetails(BigDecimal cbDtlId, String applicationId, int versionNum, String appId,
                    BigDecimal custDtlId, String request, String responseId, LocalDate cbDate,
                    String cbStatus, String payloadColumn, ApplicationMaster applicationMaster,
                    CustomerDetails customerDetails) {
    this.cbDtlId = cbDtlId;
    this.applicationId = applicationId;
    this.versionNum = versionNum;
    this.appId = appId;
    this.custDtlId = custDtlId;
    this.request = request;
    this.responseId = responseId;
    this.cbDate = cbDate;
    this.cbStatus = cbStatus;
    this.payloadColumn = payloadColumn;
    this.applicationMaster = applicationMaster;
    this.customerDetails = customerDetails;
	}

}
