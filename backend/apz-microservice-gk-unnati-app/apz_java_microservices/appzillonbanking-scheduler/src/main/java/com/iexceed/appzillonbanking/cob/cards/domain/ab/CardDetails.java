package com.iexceed.appzillonbanking.cob.cards.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "TB_ABOB_CREDIT_CARD_DETAILS")
@Getter @Setter
public class CardDetails {

	@JsonProperty("appId")
	@Column(name = "APP_ID")
	private String appId;	
	
	@JsonProperty("applicationId")
	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@JsonProperty("versionNum")
	@Column(name = "LATEST_VERSION_NO")
	private Integer versionNum;
	
	@Id
	@JsonProperty("ccDtlId")
	@Column(name = "CC_DTL_ID")
	private BigDecimal ccDtlId;
	
	@JsonProperty("cardName")
	@Column(name = "CARD_NAME")
	private String cardName;
	
	@JsonProperty("nameOnCard")
	@Column(name = "NAME_ON_CARD")
	private String nameOnCard;
	
	@JsonProperty("emailStmtReq")
	@Column(name = "EMAIL_STATEMENT")
	private String emailStmtReq;
	
	@JsonProperty("physicalStmtReq")
	@Column(name = "PHYSICAL_STATEMENT")
	private String physicalStmtReq;
	
	@JsonProperty("theme")
	@Column(name = "CARD_THEME")
	private String theme;
	
	@JsonProperty("customTheme")
	@Column(name = "CUSTOM_THEME")
	private String customTheme;

	@JsonProperty("customImagePath")
	@Column(name = "CUSTOM_IMAGE_PATH")	
	private String customImagePath;
	
	@JsonProperty("creditLimit")
	@Column(name = "CREDIT_LIMIT")	
	private String creditLimit;
	
	@JsonProperty("withdrawalLimit")
	@Column(name = "WITHDRAWAL_LIMIT")	
	private String withdrawalLimit;
	
	@JsonProperty("currency")
	@Column(name = "CURRENCY")	
	private String currency;
	
	  
}