package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@ToString
public class CAStatDetails {
    @JsonProperty("stageID")
    private int stageID;

    @JsonProperty("custType")
    private String custType;

    @JsonProperty("query")
    private List<String> query;

    @JsonProperty("editedFields")
    private List<String> editedFields;
}
