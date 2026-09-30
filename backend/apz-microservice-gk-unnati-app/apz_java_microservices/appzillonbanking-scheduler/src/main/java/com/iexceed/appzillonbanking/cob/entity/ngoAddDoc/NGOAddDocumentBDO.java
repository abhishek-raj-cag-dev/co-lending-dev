package com.iexceed.appzillonbanking.cob.entity.ngoAddDoc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NGOAddDocumentBDO {

    @JsonProperty("cabinetName")
    private String cabinetName;

    @JsonProperty("folderIndex")
    private String folderIndex;

    @JsonProperty("documentName")
    private String documentName;

    @JsonProperty("userDBId")
    private String userDBId;

    @JsonProperty("volumeId")
    private String volumeId;

    @JsonProperty("createdByAppName")
    private String createdByAppName;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("userPassword")
    private String userPassword;

    @JsonProperty("comment")
    private String comment;

}
