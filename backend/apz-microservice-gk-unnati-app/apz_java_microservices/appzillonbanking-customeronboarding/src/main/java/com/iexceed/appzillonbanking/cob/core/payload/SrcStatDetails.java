package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class SrcStatDetails {
    @JsonProperty("stageID")
    private int stageID;

    @JsonProperty("subStageID")
    private String subStageId;

    @JsonProperty("custType")
    private String custType;

    @JsonProperty("query")
    private List<String> query;

    @JsonProperty("editedFields")
    private List<String> editedFields;

    @JsonProperty("timeStamp")
    private List<LocalDateTime> timeStamp;

}