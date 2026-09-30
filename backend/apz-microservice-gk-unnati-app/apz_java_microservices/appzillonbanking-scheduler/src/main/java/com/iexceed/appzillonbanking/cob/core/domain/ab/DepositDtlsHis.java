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
@Table(name = "TB_ABOB_DEPOSIT_DTLS_HIS")
@Getter @Setter
public class DepositDtlsHis {

	@Id
	@Column(name = "DEPOSIT_DTL_ID")
	private BigDecimal depositDtlId;
	
	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@Column(name = "APP_ID")
	private String appId;

	@Column(name = "LATEST_VERSION_NO")
	private Integer versionNum;

	@Column(name = "DEPOSIT_AMOUNT")
	private BigDecimal depositAmount;
	
	@Column(name = "TENURE_MONTHS")
	private Integer tenureInMonths;

	@Column(name = "TENURE_DAYS")
	private Integer tenureInDays;

	@Column(name = "TENURE_YEARS")
	private Integer tenureInYears;

	@Column(name = "ROI")
	private Float roi;

	@Column(name = "INTEREST_PAYOUT")
	private Float interest;

	@Column(name = "MATURITY_DATE")	
	private Date maturityDate;
	
	@Column(name = "MATURITY_AMOUNT")
	private BigDecimal maturityAmount;

	@Column(name = "AUTOPAY_ENABLED")
	private String autopayEnabled;

	@Column(name = "AUTOPAY_SRC_ACCOUNT")
	private String autopaySrcAccount;

	@Column(name = "AUTOPAY_SRC_ACCOUNT_TYPE")
	private String autopaySrcAccountType;

	@Column(name = "AUTOPAY_DATE")	
	private String autopayDate;

	@Column(name = "MATURITY_INSTRUCTION")
	private String maturityInstn;

	@Column(name = "PAYOUT_ACCOUNT")
	private String payoutAccount;

	@Column(name = "PAYOUT_ACCOUNT_TYPE")
	private String payoutAccountType;

	@Column(name = "INITIAL_FUND_ACCOUNT_NUMBER")
	private String initialFundAccount;
	
	@Column(name = "INITIAL_FUND_ACCOUNT_TYPE")
	private String initialFundAccountType;
	
	@JsonProperty("productType")
	@Column(name = "PRODUCT_TYPE")
	private String productType;
	
	@CreationTimestamp
	@Column(name = "HISTORY_TS")
	private LocalDateTime historyTs;
}