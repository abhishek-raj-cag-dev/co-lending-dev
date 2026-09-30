package com.iexceed.appzillonbanking.cob.entity.ngoConnectCabinet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NGOExecuteAPIBDO {

    @JsonProperty("base64Encoded")
    private String base64Encoded;

    @JsonProperty("locale")
    private String locale;

    @JsonProperty("inputData")
    private InputData input;
}
