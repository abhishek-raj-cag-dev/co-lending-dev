package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BREItemsObservedInList {
    @ApiModelProperty(required = false, example = "150")
    @JsonProperty("stock_saree")
    private int stockSaree = 0;

    @ApiModelProperty(required = false, example = "200")
    @JsonProperty("stock_salwar")
    private int stockSalwar = 0;

    @ApiModelProperty(required = false, example = "300")
    @JsonProperty("stock_shirt")
    private int stockShirt = 0;

    @ApiModelProperty(required = false, example = "250")
    @JsonProperty("stock_pants")
    private int stockPants = 0;

    @ApiModelProperty(required = false, example = "180")
    @JsonProperty("stock_kids_wears")
    private int stockKidsWears = 0;

    @ApiModelProperty(required = false, example = "100")
    @JsonProperty("cloth_1")
    private int cloth1 = 0;

    @ApiModelProperty(required = false, example = "120")
    @JsonProperty("cloth_2")
    private int cloth2 = 0;

    @ApiModelProperty(required = false, example = "80")
    @JsonProperty("stock_of_miscleneous")
    private int stockOfMiscleneous = 0;
}
