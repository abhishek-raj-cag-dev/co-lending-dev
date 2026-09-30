package com.iexceed.appzillonbanking.cob.core.payload;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AmlQuestionnairePayload {

    @JsonProperty("amlQuestionList")
    private List<AmlQuestion> amlQuestionList;

    @JsonProperty("amlQuestionsConsent")
    private String amlQuestionsConsent;

    @JsonProperty("amlQuestionsConsentBM")
    private String amlQuestionsConsentBM;
}
