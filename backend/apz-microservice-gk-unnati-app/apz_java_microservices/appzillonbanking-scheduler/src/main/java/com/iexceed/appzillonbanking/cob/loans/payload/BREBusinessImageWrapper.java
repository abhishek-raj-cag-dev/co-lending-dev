package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BREBusinessImageWrapper {

    @JsonProperty("Business_image_assessment")
    private BREBusinessImageAssessment businessImageAssessment = new BREBusinessImageAssessment();

    public BREBusinessImageAssessment getBusinessImageAssessment() {
        return businessImageAssessment;
    }

    public void setBusinessImageAssessment(BREBusinessImageAssessment businessImageAssessment) {
        this.businessImageAssessment = businessImageAssessment;
    }
}


