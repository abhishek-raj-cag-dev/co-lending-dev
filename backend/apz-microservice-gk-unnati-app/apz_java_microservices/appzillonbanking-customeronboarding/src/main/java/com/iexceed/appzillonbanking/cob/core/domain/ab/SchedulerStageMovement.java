package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_ABOB_SCHEDULER_STAGE_MOVEMENT")
@Getter
@Setter
@ToString
public class SchedulerStageMovement {

    @Id
    @Column(name = "APPLICATION_ID")
    @JsonProperty("applicationId")
    private String applicationId;


    @Column(name = "FROM_STATUS")
    @JsonProperty("fromStatus")
    private String fromStatus;

    @Column(name = "CREATE_TS")
    @JsonProperty("createTs")
    private LocalDateTime createTs;

    @Column(name = "TO_STATUS")
    @JsonProperty("toStatus")
    private String toStatus;

    @Column(name = "CB_STATUS")
    @JsonProperty("cbStatus")
    private String cbStatus;
}
