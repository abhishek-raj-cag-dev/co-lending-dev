package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ErrorTrace;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter @Setter
@ToString
public class ErrorLoggerRequest {

    @JsonProperty("requestObj")
    private ErrorTrace errorTrace;

    @JsonProperty("interfaceName")
    private String interfaceName;

    @JsonProperty("appId")
    private String appId;
}