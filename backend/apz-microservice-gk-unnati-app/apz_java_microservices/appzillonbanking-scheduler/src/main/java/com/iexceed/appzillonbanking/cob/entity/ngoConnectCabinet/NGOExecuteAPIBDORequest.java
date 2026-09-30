package com.iexceed.appzillonbanking.cob.entity.ngoConnectCabinet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NGOExecuteAPIBDORequest {

    @JsonProperty("NGOExecuteAPIBDO")
    private NGOExecuteAPIBDO ngoExecuteAPIBDO;
}
