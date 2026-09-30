package com.iexceed.appzillonbanking.cob.core.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_FAQ_DETAILS")
@IdClass(FaqDetailsId.class)
@Getter @Setter
public class FaqDetails {
	
	@Id
	private String appId;
	
	@Id
	private String product;
	
	@Id
	private String stage;
	
	@Id
	private String seqNum;	
	
	@Column(name = "QUESTION")
	private String question;
	
	@Column(name = "ANSWER")
	private String answer;	
}