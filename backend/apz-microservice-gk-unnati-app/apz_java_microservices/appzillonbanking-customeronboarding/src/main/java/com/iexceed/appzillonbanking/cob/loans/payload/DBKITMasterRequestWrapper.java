package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DBKITMasterRequestWrapper {

    @JsonProperty("apiRequest")
    private DBKITMasterRequest apiRequest;

    @Getter
    @Setter
    public static class DBKITMasterRequest {
        @JsonProperty("requestObj")
        private DBKITMasterRequestObj requestObj;
    }

    @Getter
    @Setter
    public static class DBKITMasterRequestObj {
        @JsonProperty("category")
        private String category;
    }

}
