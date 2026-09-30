package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentsId implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "APPLICATION_ID")
    @JsonProperty("applicationId")
    private String applicationId;

    @Id
    @Column(name = "UPLOAD_TYPE")
    @JsonProperty("uploadType")
    private String uploadType;

    @Id
    @Column(name = "DOC_TYPE")
    @JsonProperty("docType")
    private String docType;
}
