package com.iexceed.appzillonbanking.cob.entity.ngoConnectCabinet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NGOConnectCabinetInput {

    @JsonProperty("Option")
    private String option;

    @JsonProperty("UserExist")
    private String userExist;

    @JsonProperty("CabinetName")
    private String cabinetName;

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("UserPassword")
    private String userPassword;

    @JsonProperty("locale")
    private String locale;
}
