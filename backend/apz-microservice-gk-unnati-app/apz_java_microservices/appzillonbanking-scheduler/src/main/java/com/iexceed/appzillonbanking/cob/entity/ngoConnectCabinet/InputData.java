package com.iexceed.appzillonbanking.cob.entity.ngoConnectCabinet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.entity.ngoDisconnectCabinet.NGODisconnectCabinetInput;
import lombok.Data;

@Data
public class InputData {

    @JsonProperty("NGOConnectCabinet_Input")
    private NGOConnectCabinetInput ngoConnectCabinetInput;

    @JsonProperty("NGODisconnectCabinet_Input")
    private NGODisconnectCabinetInput ngoDisconnectCabinetInput;
}
