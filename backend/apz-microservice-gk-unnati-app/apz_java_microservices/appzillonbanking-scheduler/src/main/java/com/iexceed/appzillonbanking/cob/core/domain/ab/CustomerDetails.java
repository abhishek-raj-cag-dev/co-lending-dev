package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.CustomerDetailsPayload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_ABOB_CUSTOMER_DETAILS")
@Getter @Setter
@ToString
@AllArgsConstructor
public class CustomerDetails {

	@JsonProperty("custDtlId")
	@Id
	@Column(name = "CUST_DTL_ID")
	private BigDecimal custDtlId;
	
	@Column(name = "APPLICATION_ID")
	private String applicationId;
	
	@Column(name = "VERSION_NO")
	private int versionNum;
	
	@Column(name = "APP_ID")
	private String appId;
	
	@JsonProperty("customerType")
	@Column(name = "CUSTOMER_TYPE")
	private String customerType;
	
	@JsonProperty("customerName")
	@Column(name = "CUSTOMER_NAME")
	private String customerName;
	
	@JsonProperty("mobileNumber")
	@Column(name = "MOBILE_NUMBER")
	private String mobileNumber;
	
	@JsonProperty("kycStatus")
	@Column(name = "KYC_STATUS")
	private String kycStatus;
	
	@JsonProperty("amlStatus")
	@Column(name = "AML_STATUS")
	private String amlStatus;
	
	@JsonProperty("customerId")
	@Column(name = "CUSTOMER_ID")
	private BigDecimal customerId;
	
	@JsonProperty("memberId")
	@Column(name = "MEMBER_ID")
	private BigDecimal memberId;

	@JsonProperty("payloadColumn")
	@Column(name = "PAYLOAD")
	private String payloadColumn;
	
	@Transient
	@JsonProperty("payload")
	private CustomerDetailsPayload payload;
	
	@Column(name = "SEQ_NO")
	private int seqNumber;
	

	@Formula("(payload::json ->> 'custId')")
	private String custId;

	@Formula("(payload::json ->> 'primaryKycId')")
	private String primaryKycId;

	@Formula("(payload::json ->> 'alternateVoterId')")
	private String alternateVoterId;
	
	public CustomerDetails() {
	}
	
	public CustomerDetails(String applicationId, String custId) {
		this.applicationId = applicationId;
		this.custId = custId;
	}
}


