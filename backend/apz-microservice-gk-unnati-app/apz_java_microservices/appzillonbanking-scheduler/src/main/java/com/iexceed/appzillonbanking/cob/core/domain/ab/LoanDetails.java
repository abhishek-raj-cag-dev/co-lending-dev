package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.LoanDetailsPayload;

import lombok.Data;

@Entity
@Table(name = "TB_ABOB_LOAN_DTLS")
@Data
public class LoanDetails {

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
	@Column(name = "TENURE_YEARS")
	private Integer tenureInMonths;
	
	@JsonProperty("tenure")
	@Column(name = "TENURE_MONTHS")
	private Integer tenure;
	
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
	private String emiDate;
	
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
	
	@Column(name = "SANCTIONED_LOAN_AMOUNT")
	private BigDecimal sanctionedLoanAmount;

	@Column(name = "BM_RECOMMENDED_LOAN_AMOUNT")
	private BigDecimal bmRecommendedLoanAmount;

	@Column(name = "OLD_BM_RECOMMENDED_LOAN_AMOUNT")
	private BigDecimal oldBmRecommendedLoanAmount;

	@Column(name = "OLD_SANCTION_RECOMMENDED_LOAN_AMOUNT")
	private BigDecimal oldSanctionRecommendedLoanAmount;
	
	@Transient
	@JsonProperty("payload")
	private LoanDetailsPayload payload;
	
	@Transient
	@JsonProperty("productCode")
	private String productCode;
	
	@Transient
	@JsonProperty("productGroupCode")
	private String productGroupCode;	
	
	@JsonProperty("coapplicantId")
	private String coapplicantId;
	
	@JsonProperty("t24LoanId")
	private String t24LoanId;
	
	@JsonProperty("loanStatus")
	private String loanStatus;
	
	@JsonProperty("coapplicantUpdateId")
	private String coapplicantUpdateId;
	
	@JsonProperty("loanRepaymentSchedule")
	private String loanRepaymentSchedule;
	
}
