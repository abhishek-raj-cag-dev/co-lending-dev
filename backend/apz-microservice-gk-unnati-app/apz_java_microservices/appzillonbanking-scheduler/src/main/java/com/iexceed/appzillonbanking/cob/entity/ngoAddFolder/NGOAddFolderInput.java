package com.iexceed.appzillonbanking.cob.entity.ngoAddFolder;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NGOAddFolderInput {

    @JsonProperty("Option")
    private String option;

    @JsonProperty("CabinetName")
    private String cabinetName;

    @JsonProperty("UserDBId")
    private String userDBId;

    @JsonProperty("Folder")
    private Folder folderInput;
}
