package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdharUploadRequest {

    @JsonProperty("uploadLoanRequestFields")
    private UploadLoanRequestFields uploadLoanRequestFields;

    @JsonProperty("fetchAadhaar")
    private Boolean fetchAadhaar;

    @JsonProperty("adharRedactOcrRequestFields")
    private AdharRedactOcrRequestFields adharRedactOcrRequestFields;
}
