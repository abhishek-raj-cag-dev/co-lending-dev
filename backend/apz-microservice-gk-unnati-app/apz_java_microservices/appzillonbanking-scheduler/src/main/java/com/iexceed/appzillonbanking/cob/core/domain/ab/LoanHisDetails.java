package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_LOAN_DTLS_HIS")
@Getter @Setter
public class LoanHisDetails {
	
	@Id
	@JsonProperty("loanDtlId")
	@Column(name = "LOAN_DTL_ID")
	private BigDecimal loanDtlId;
	
	@JsonProperty("applicationId")
	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@JsonProperty("appId")
	@Column(name = "APP_ID")
	private String appId;	
	
	@JsonProperty("versionNum")
	@Column(name = "LATEST_VERSION_NO")
	private Integer versionNum;
	
	@JsonProperty("loanAmount")
	@Column(name = "LOAN_AMOUNT")
	private BigDecimal loanAmount;
	
	@JsonProperty("tenureInMonths")
	@Column(name = "TENURE_MONTHS")
	private Integer tenureInMonths;
	
	@JsonProperty("tenureInYears")
	@Column(name = "TENURE_YEARS")
	private Integer tenureInYears;
	
	@JsonProperty("roi")
	@Column(name = "ROI")
	private Float roi;
	
	@JsonProperty("interest")
	@Column(name = "INTEREST_AMOUNT")
	private Float interest;
	             
	@JsonProperty("loanClosureDate")
	@Column(name = "LOAN_CLOSURE_DATE")	
	private Date loanClosureDate;
	
	@JsonProperty("totPayableAmount")
	@Column(name = "TOTAL_PAYABLE_AMOUNT")
	private BigDecimal totPayableAmount;
	
	@JsonProperty("autoEmiAccount")
	@Column(name = "AUTO_EMI_ACCOUNT")
	private String autoEmiAccount;
	
	@JsonProperty("autoEmiAccountType")
	@Column(name = "AUTO_EMI_ACCOUNT_TYPE")
	private String autoEmiAccountType;
	
	@JsonProperty("emiDate")
	@Column(name = "EMI_DATE")	
	private Date emiDate;
	
	@JsonProperty("loanCrAccount")
	@Column(name = "LOAN_CREDIT_ACCOUNT")
	private String loanCrAccount;
	
	@JsonProperty("loanCrAccountType")
	@Column(name = "LOAN_CREDIT_ACCOUNT_TYPE")
	private String loanCrAccountType;
	
	@JsonProperty("monthlyEmi")
	@Column(name = "MONTHLY_EMI")
	private BigDecimal monthlyEmi;
	
	@Column(name = "PAYLOAD")
	private String payloadColumn;

	@CreationTimestamp
	@Column(name = "HISTORY_TS")
	private LocalDateTime historyTs;	
}