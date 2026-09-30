package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter; 

@Entity
@Table(name = "TB_ABOB_DB_DOCUMENTS")
@IdClass(DocumentsId.class)
@Getter
@Setter
public class Documents  {

    @Id
    private String applicationId;

    @Id
    private String uploadType;

    @Id
    private String docType;

    @Column(name = "APP_ID")
    @JsonProperty("appId")
    private String appId;

    @Column(name = "DOC_STATUS")
    @JsonProperty("docStatus")
    private String docStatus;

    @Column(name = "DOC_NAME")
    @JsonProperty("docName")
    private String docName;

    @CreationTimestamp
    @Column(name = "CREATE_TS")
    @JsonProperty("createTs")
    private Timestamp createTs;

    @Column(name = "CREATED_BY")
    @JsonProperty("createdBy")
    private String createdBy;

    @Column(name = "APPROVED_TS")
    @JsonProperty("approvedTs")
    private Timestamp approvedTs;

    @Column(name = "PRODUCT_TYPE")
    @JsonProperty("productType")
    private String productType;

    @Column(name = "QUERIES")
    @JsonProperty("queries")
    private String queries;

    @Column(name = "QUERY_RESPONSE")
    @JsonProperty("queryResponse")
    private String queryResponse;

    @Column(name = "APPROVED_BY")
    @JsonProperty("approvedBy")
    private String approvedBy;

    @Column(name = "LANGUAGE")
    @JsonProperty("language")
    private String language;

    @Column(name = "DOC_SIZE")
    @JsonProperty("docSize")
    private String docSize;
    
}
