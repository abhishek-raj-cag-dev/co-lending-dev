package com.iexceed.appzillonbanking.cob.entity.ngoAddFolder;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NGOExecuteAPIBDO<T> {

    @JsonProperty("inputData")
    private InputData inputData;
}

