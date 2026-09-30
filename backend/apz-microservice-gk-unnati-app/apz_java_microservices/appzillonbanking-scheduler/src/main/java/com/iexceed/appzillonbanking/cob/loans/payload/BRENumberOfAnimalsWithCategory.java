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
public class BRENumberOfAnimalsWithCategory {
    @ApiModelProperty(required = false, example = "25")
    @JsonProperty("goat")
    private int goat = 0;

    @ApiModelProperty(required = false, example = "30")
    @JsonProperty("sheep")
    private int sheep = 0;

    @ApiModelProperty(required = false, example = "100")
    @JsonProperty("hens")
    private int hens = 0;

    @ApiModelProperty(required = false, example = "500")
    @JsonProperty("fish")
    private int fish = 0;

    @ApiModelProperty(required = false, example = "10")
    @JsonProperty("animal_1")
    private int animal1 = 0;

    @ApiModelProperty(required = false, example = "15")
    @JsonProperty("animal_2")
    private int animal2 = 0;
}
