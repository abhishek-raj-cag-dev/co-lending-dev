package com.iexceed.appzillonbanking.cob.entity.ngoAddFolder;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Folder {

    @JsonProperty("ParentFolderIndex")
    private String parentFolderIndex;

    @JsonProperty("FolderName")
    private String folderName;

    @JsonProperty("CreationDateTime")
    private String creationDateTime;

    @JsonProperty("AccessType")
    private String accessType;

    @JsonProperty("ImageVolumeIndex")
    private String imageVolumeIndex;

    @JsonProperty("FolderType")
    private String folderType;

    @JsonProperty("Location")
    private String location;

    @JsonProperty("Comment")
    private String comment;

    @JsonProperty("Owner")
    private String owner;
}
