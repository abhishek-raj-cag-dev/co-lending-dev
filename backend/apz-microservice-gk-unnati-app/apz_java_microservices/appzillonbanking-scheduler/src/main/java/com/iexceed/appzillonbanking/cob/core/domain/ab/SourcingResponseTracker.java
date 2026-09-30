package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_abob_sourcing_response_tracker")
public class SourcingResponseTracker {

    @Id
    @JsonProperty("applicationId")
    @Column(name = "application_id", length = 20, nullable = false)
    private String applicationId;

    @JsonProperty("stage")
    @Column(name = "stage", length = 200)
    private String stage;

    @JsonProperty("response")
    @Column(name = "response", length = 22000)
    private String response;

    @JsonProperty("createdAt")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Transient
    @JsonProperty("queryResponse")
    private JsonNode queryResponse;
}
