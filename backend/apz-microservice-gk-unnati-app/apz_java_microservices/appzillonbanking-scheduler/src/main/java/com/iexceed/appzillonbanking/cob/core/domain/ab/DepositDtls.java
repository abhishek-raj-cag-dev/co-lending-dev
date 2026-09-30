package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Entity
@Table(name = "TB_ABOB_DEPOSIT_DTLS")
@Data
public class DepositDtls {

	@Id
	@JsonProperty("depositDtlId")
	@Column(name = "DEPOSIT_DTL_ID")
	private BigDecimal depositDtlId;
	
	@JsonProperty("applicationId")
	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@JsonProperty("appId")
	@Column(name = "APP_ID")
	private String appId;	
	
	@JsonProperty("versionNum")
	@Column(name = "LATEST_VERSION_NO")
	private Integer versionNum;
	
	@JsonProperty("depositAmount")
	@Column(name = "DEPOSIT_AMOUNT")
	private BigDecimal depositAmount;
	
	@JsonProperty("tenureInMonths")
	@Column(name = "TENURE_MONTHS")
	private Integer tenureInMonths;
	
	@JsonProperty("tenureInDays")
	@Column(name = "TENURE_DAYS")
	private Integer tenureInDays;
	
	@JsonProperty("tenureInYears")
	@Column(name = "TENURE_YEARS")
	private Integer tenureInYears;
	
	@JsonProperty("roi")
	@Column(name = "ROI")
	private Float roi;
	
	@JsonProperty("interest")
	@Column(name = "INTEREST_PAYOUT")
	private Float interest;

	@JsonProperty("maturityDate")
	@Column(name = "MATURITY_DATE")	
	private Date maturityDate;
	
	@JsonProperty("maturityAmount")
	@Column(name = "MATURITY_AMOUNT")
	private BigDecimal maturityAmount;
	
	@JsonProperty("autopayEnabled")
	@Column(name = "AUTOPAY_ENABLED")
	private String autopayEnabled;
	
	@JsonProperty("autopaySrcAccount")
	@Column(name = "AUTOPAY_SRC_ACCOUNT")
	private String autopaySrcAccount;

	@JsonProperty("autopaySrcAccountType")
	@Column(name = "AUTOPAY_SRC_ACCOUNT_TYPE")
	private String autopaySrcAccountType;
	
	@JsonProperty("autopayDate")
	@Column(name = "AUTOPAY_DATE")	
	private String autopayDate;
	
	@JsonProperty("maturityInstn")
	@Column(name = "MATURITY_INSTRUCTION")
	private String maturityInstn;
	
	@JsonProperty("payoutAccount")
	@Column(name = "PAYOUT_ACCOUNT")
	private String payoutAccount;
	
	@JsonProperty("payoutAccountType")
	@Column(name = "PAYOUT_ACCOUNT_TYPE")
	private String payoutAccountType;

	@JsonProperty("initialFundAccount")
	@Column(name = "INITIAL_FUND_ACCOUNT_NUMBER")
	private String initialFundAccount;
	
	@JsonProperty("initialFundAccountType")
	@Column(name = "INITIAL_FUND_ACCOUNT_TYPE")
	private String initialFundAccountType;
	
	@JsonProperty("productType")
	@Column(name = "PRODUCT_TYPE")
	private String productType;
	
	@Transient
	@JsonProperty("productCode")
	private String productCode;
	
	@Transient
	@JsonProperty("productGroupCode")
	private String productGroupCode;	
}