package com.iexceed.appzillonbanking.cob.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DmsRequestExt {

    @JsonProperty("appId")
    private String appId;

    @JsonProperty("interfaceName")
    private String interfaceName;

    @JsonProperty("requestObj")
    private Object requestObj;
}
