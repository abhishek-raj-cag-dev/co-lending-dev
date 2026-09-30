package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AmlQuestion {

    @JsonProperty("questionType")
    private String questionType;

    @JsonProperty("coApplicant")
    private String coApplicant;

    @JsonProperty("applicant")
    private String applicant;

}