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
public class BREDairy {
    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("no_of_cows")
    private int noOfCows = 0;

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("hf_cross_cow")
    private int hfCrossCow = 0;

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("jersey_cow")
    private int jerseyCow = 0;

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("local_cow")
    private int localCow = 0;

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("no_of_buffaloes")
    private int noOfBuffaloes = 0;

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("murraha_buffalo")
    private int murrahaBuffalo = 0;

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("mehsani_buffalo")
    private int mehsaniBuffalo = 0;

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("jowari_buffalo")
    private int jowariBuffalo = 0;

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("pandarpur_gawali_buffalo")
    private int pandarpurGawaliBuffalo = 0;

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("mauli_buffalo")
    private int mauliBuffalo = 0;

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("shelter_type")
    private String shelterType = "";

    @ApiModelProperty(required = false, example = "0.0")
    @JsonProperty("shelter_area_in_sqft")
    private double shelterAreaInSqft = 0.0;

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("no_of_lactating_cow")
    private int noOfLactatingCow = 0;

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("no_of_lactating_buffaloes")
    private int noOfLactatingBuffaloes = 0;

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("bcs_body_composition_score")
    private String bcsBodyCompositionScore = "";

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("cattle_insurance_tag")
    private String cattleInsuranceTag = "";

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("cattle_insurance_tag_numbers")
    private String cattleInsuranceTagNumbers = "";

    @JsonProperty("motorized_vehicle_in_image")
    private List<String> motorizedVehicleInImage = new ArrayList<>();

    @ApiModelProperty(required = false, example = "0.0")
    @JsonProperty("feed_storage_area")
    private double feedStorageArea = 0.0;

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("milk_collection_counter")
    private String milkCollectionCounter = "";

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("manure")
    private String manure = "";
}
