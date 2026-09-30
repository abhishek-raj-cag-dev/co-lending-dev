package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.iexceed.appzillonbanking.cob.loans.payload.LucPayloadRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_ABOB_LUC_LOAN_OBLIGATIONS")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LUCEntity {
    
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
    
    @Column(name = "QUERIES", length = 4000)
    private String queries;
    
    @Transient
    private LucPayloadRequest LucDetailsWrapper;


}