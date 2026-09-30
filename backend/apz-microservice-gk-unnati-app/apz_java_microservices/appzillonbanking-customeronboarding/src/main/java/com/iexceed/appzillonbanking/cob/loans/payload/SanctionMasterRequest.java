package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SanctionMasterRequest {

    @JsonProperty("appId")
    private String appId;

    @JsonProperty("interfaceName")
    private String interfaceName;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("requestType")
    private String requestType;

    @JsonProperty("requestObj")
    private SanctionMasterRequestFields requestObj;

    @Override
    public String toString() {
        return "SanctionMasterRequest [appId=" + appId + ", interfaceName=" + interfaceName
                + ", userId=" + userId + ", requestObj=" + requestObj + "]";
    }
}