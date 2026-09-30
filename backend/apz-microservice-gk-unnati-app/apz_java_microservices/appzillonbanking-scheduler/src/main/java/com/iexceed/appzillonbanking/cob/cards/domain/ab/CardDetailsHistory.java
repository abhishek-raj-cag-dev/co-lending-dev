package com.iexceed.appzillonbanking.cob.cards.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ABOB_CREDIT_CARD_DTL_HIS")
@Getter @Setter
public class CardDetailsHistory {

	@Column(name = "APP_ID")
	private String appId;	
	
	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@Column(name = "LATEST_VERSION_NO")
	private Integer versionNum;
	
	@Id
	@Column(name = "CC_DTL_ID")
	private BigDecimal ccDtlId;
	
	@Column(name = "CARD_NAME")
	private String cardName;
	
	@Column(name = "NAME_ON_CARD")
	private String nameOnCard;
	
	@Column(name = "EMAIL_STATEMENT")
	private String emailStmtReq;
	
	@Column(name = "PHYSICAL_STATEMENT")
	private String physicalStmtReq;
	
	@Column(name = "CARD_THEME")
	private String theme;
	
	@Column(name = "CUSTOM_THEME")
	private String customTheme;

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
	
	@CreationTimestamp
	@Column(name = "HISTORY_TS")
	private LocalDateTime historyTs;
}