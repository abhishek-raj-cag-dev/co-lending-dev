package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MasterSearchQueryDTO {

    @JsonProperty("customerId")
    private String customerId;

    @JsonProperty("customerName")
    private String customerName;

    @JsonProperty("applicationId")
    private String applicationId;

    @JsonProperty("branchId")
    private String branchId;

    @JsonProperty("branchName")
    private String branchName;

    @JsonProperty("kendraId")
    private String kendraId;

    @JsonProperty("kendraName")
    private String kendraName;

    @JsonProperty("currentStatus")
    private String currentStatus;

    @JsonProperty("currentStage")
    private String currentStage;

    @JsonProperty("loanType")
    private String loanType;

    @JsonProperty("loanId")
    private String loanId;

    @JsonProperty("loanAmount")
    private BigDecimal loanAmount;
}
