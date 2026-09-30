package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BREMenuDisplayPriceList {
    @ApiModelProperty(required = false, example = "[\"Biryani\", \"Dosa\", \"Coffee\"]")
    @JsonProperty("item_names")
    private List<String> itemNames = new ArrayList<>();

    @ApiModelProperty(required = false, example = "[150, 50, 30]")
    @JsonProperty("item_prices")
    private List<Integer> itemPrices = new ArrayList<>();
}
