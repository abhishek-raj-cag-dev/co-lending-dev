package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_APPLN_DOCUMENTS_HIS")
@Getter @Setter
public class ApplicationDocumentsHistory {

	@Id
	@Column(name = "APP_DOC_ID")
	private BigDecimal appDocId;
	
	@Column(name = "APPLICATION_ID")
	private String applicationId;
	  
	@Column(name = "VERSION_NO")
	private int versionNum;
	
	@Column(name = "APP_ID")
	private String appId;

	@Column(name = "CUST_DTL_ID")
	private BigDecimal custDtlId;
	
	@Column(name = "DOCUMENT_ID")
	private BigDecimal documentId;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "PAYLOAD")
	private String payloadColumn;	
	
	@CreationTimestamp
	@Column(name = "HISTORY_TS")
	private LocalDateTime historyTs;
}