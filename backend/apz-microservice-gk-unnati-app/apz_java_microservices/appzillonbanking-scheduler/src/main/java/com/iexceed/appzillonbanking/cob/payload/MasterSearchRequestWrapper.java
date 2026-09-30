package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MasterSearchRequestWrapper {

    @JsonProperty("apiRequest")
    private MasterSearchRequest apiRequest;

    @Data
    public static class MasterSearchRequest {

        @JsonProperty("requestObj")
        private MasterSearchRequestFields requestObj;

        @JsonProperty("interfaceName")
        private String interfaceName;

        @JsonProperty("appId")
        private String appId;
    }

    @Data
    public static class MasterSearchRequestFields {

        @JsonProperty("searchValue")
        private String searchValue;

    }
}
