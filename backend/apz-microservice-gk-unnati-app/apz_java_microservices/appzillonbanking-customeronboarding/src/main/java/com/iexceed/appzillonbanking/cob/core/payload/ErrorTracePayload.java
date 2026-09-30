package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorTracePayload {

    @JsonProperty("functionName")
    private String functionName;

    @JsonProperty("lineNumber")
    private BigInteger lineNumber;

    @JsonProperty("columnNumber")
    private BigInteger columnNumber;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("stackTrace")
    private String stackTrace;

    @JsonProperty("screenName")
    private String screenName;

    @JsonProperty("stage")
    private String stage;

    @JsonProperty("additionalInfo")
    private String additionalInfo;

}
