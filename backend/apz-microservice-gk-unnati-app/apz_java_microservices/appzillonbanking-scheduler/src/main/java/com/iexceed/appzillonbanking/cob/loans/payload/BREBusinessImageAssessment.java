package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BREBusinessImageAssessment {
    @JsonProperty("App_business_category")
    private BREAppBusinessCategory appBusinessCategory = new BREAppBusinessCategory();

    @JsonProperty("Co_app_business_category")
    private BRECoAppBusinessCategory coAppBusinessCategory = new BRECoAppBusinessCategory();
    
    @JsonProperty("Common_business_category")
    private BRECommonBusinessCategory commonBusinessCategory = new BRECommonBusinessCategory();
}
