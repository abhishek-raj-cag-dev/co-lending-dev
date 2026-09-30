package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RpcReportRequestWrapper {
    @JsonProperty("apiRequest")
    private RpcReportRequest apiRequest;

    @Data
    public static class RpcReportRequest {

        @JsonProperty("requestObj")
        private RpcReportRequestFields requestObj;

        @JsonProperty("interfaceName")
        private String interfaceName;

        @JsonProperty("appId")
        private String appId;
    }

    @Data
    public static class RpcReportRequestFields {

        @JsonProperty("reportType")
        private String reportType;

        @JsonProperty("fromDate")
        private LocalDate fromDate;

        @JsonProperty("toDate")
        private LocalDate toDate;

    }
}
