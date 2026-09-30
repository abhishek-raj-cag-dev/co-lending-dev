package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.iexceed.appzillonbanking.cob.loans.payload.BCMPIIncomeDetailsWrapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_ABOB_BCMPI_INCOME_DETAILS")
@Getter
@Setter
@ToString
public class BCMPIIncomeDetails {

    @Id
    @Column(name = "APPLICATION_ID", nullable = false)
    private String applicationId;

    @Column(name = "APP_ID", nullable = false)
    private String appId;

    @Column(name = "VERSION_NO")
    private Integer versionNo;

    @Column(name = "PAYLOAD", length = 4000)
    private String payload;

    @Column(name = "CREATE_TS")
    private LocalDateTime createTs;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATE_TS")
    private LocalDateTime updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Transient
    private BCMPIIncomeDetailsWrapper bcmpiIncomeDetailsWrapper;

}
