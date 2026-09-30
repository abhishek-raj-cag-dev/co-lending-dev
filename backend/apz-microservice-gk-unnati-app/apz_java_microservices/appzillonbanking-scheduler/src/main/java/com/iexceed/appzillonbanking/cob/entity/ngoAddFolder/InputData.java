package com.iexceed.appzillonbanking.cob.entity.ngoAddFolder;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InputData {

    @JsonProperty("NGOAddFolder_Input")
    private NGOAddFolderInput ngoAddFolderInput;

}
