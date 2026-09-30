package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.ArrayList;
import java.util.List;

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
public class BREAnimalHusbandry {

    @JsonProperty("type_of_animals")
    private List<String> typeOfAnimals = new ArrayList<>();

    @JsonProperty("number_of_animals_with_category")
    private BRENumberOfAnimalsWithCategory numberOfAnimalsWithCategory;

    @ApiModelProperty(required = false, example = "500.5")
    @JsonProperty("shed_shelter_size")
    private Double shedShelterSize = 0.0;

    @ApiModelProperty(required = false, example = "Yes")
    @JsonProperty("labor_presence")
    private String laborPresence = "";

    @ApiModelProperty(required = false, example = "Owned")
    @JsonProperty("occupancy")
    private String occupancy = "";

    @ApiModelProperty(required = false, example = "Good")
    @JsonProperty("lighting_condition")
    private String lightingCondition = "";

    @ApiModelProperty(required = false, example = "200.0")
    @JsonProperty("fish_pond_size")
    private Double fishPondSize = 0.0;

    @ApiModelProperty(required = false, example = "3")
    @JsonProperty("number_of_fish_ponds")
    private int numberOfFishPonds = 0;

}