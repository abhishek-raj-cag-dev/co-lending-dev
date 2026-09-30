package com.iexceed.appzillonbanking.cob.entity.ngoAddDoc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NGOAddDocumentBDORequest {

    @JsonProperty("NGOAddDocumentBDO")
    private NGOAddDocumentBDO ngoAddDocumentBDO;
}
