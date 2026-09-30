package com.iexceed.appzillonbanking.cob.core.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "TB_ABOB_UDHYAM")
@IdClass(UdhyamIdClass.class)
@Getter
@Setter
@ToString
public class Udhyam {

    @Id
    private String applicationId;

    @Id
    private String customerType;

    @Column(name = "APP_ID")
    @JsonProperty("appId")
    private String appId;

    @Column(name = "UDHYAM_REG_ID")
    @JsonProperty("udhyamRegId")
    private String udhyamRegId;

    @Column(name = "UDHYAM_STATUS")
    @JsonProperty("udhyamStatus")
    private String udhyamStatus;

    @Column(name = "REMARKS")
    @JsonProperty("remarks")
    private String remarks;

    @CreationTimestamp
    @Column(name = "CREATE_TS")
    @JsonProperty("createTs")
    private LocalDateTime createTs;

    @Column(name = "CREATED_BY")
    @JsonProperty("createdBy")
    private String createdBy;

    @Column(name = "UPDATED_TS")
    @JsonProperty("updatedTs")
    private LocalDateTime updatedTs;
}
