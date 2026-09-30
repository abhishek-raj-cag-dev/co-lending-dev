package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.ApplicationDocumentsPayload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "TB_ABOB_APPLN_DOCUMENTS")
@Getter @Setter
@ToString
public class ApplicationDocuments {

	@Id
	@Column(name = "APP_DOC_ID")
	private BigDecimal appDocId;
	
	@Column(name = "APPLICATION_ID")
	private String applicationId;
	  
	@Column(name = "VERSION_NO")
	private int versionNum;
	
	@Column(name = "APP_ID")
	private String appId;

	@JsonProperty("custDtlId")
	@Column(name = "CUST_DTL_ID")
	private BigDecimal custDtlId;
	
	@JsonProperty("documentId")
	@Column(name = "DOCUMENT_ID")
	private BigDecimal documentId;
	
	@JsonProperty("status")
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "PAYLOAD")
	private String payloadColumn;	
	
	@Transient
	@JsonProperty("payload")
	private ApplicationDocumentsPayload  payload;

    @Formula("(PAYLOAD::json ->> 'docLevel')")
    private String docLevel;

    @Formula("(PAYLOAD::json ->> 'documentType')")
    private String documentType;

	@Formula("(PAYLOAD::json ->> 'custType')")
	private String custType;

	@Formula("(PAYLOAD::json ->> 'documentFileName')")
	private String documentFileName;

}
