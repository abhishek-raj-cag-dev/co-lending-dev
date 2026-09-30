package com.iexceed.appzillonbanking.cob.core.domain.ab;


import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ABOB_OCR_DETAILS")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OCRDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OCR_DTL_ID")
    private Long ocrDtlId;

    @Column(name = "CUSTOMER_DTL_ID")
    private BigDecimal custDtlId;

    @Column(name = "APPLICATION_ID")
    private String applicationId;

    @Column(name = "requestPayload")
    private String requestPayload;

    @JsonProperty("responsePayload")
    private String responsePayload;

    @Column(name = "docType")
    private String docType;

    @Column(name = "docSide")
    private String docSide;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Formula("(responsePayload::jsonb -> 'result' -> 0 -> 'details')")
    private String details;

}
