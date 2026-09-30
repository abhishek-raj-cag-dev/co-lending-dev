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
public class BRETailoring {
    @ApiModelProperty(required = false, example = "")
    @JsonProperty("name_of_shop")
    private String nameOfShop = "";

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("business_premise")
    private String businessPremise = "";

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("business_location")
    private String businessLocation = "";

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("number_of_tailors")
    private int numberOfTailors = 0;

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("no_of_tailoring_machines")
    private int noOfTailoringMachines = 0;

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("number_of_power_tailoring_machines")
    private int numberOfPowerTailoringMachines = 0;

    @ApiModelProperty(required = false, example = "0")
    @JsonProperty("number_of_manual_tailoring_machines")
    private int numberOfManualTailoringMachines = 0;

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("tailoring_machine_manufacturer")
    private String tailoringMachineManufacturer = "";

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("address_of_the_shop")
    private String addressOfTheShop = "";

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("type_of_cloth_stitched_as_seen_in_picture")
    private List<String> typeOfClothStitchedAsSeenInPicture = new ArrayList<>();

    @JsonProperty("number_of_stitching_job_visible_in_image")
    private List<String> numberOfStitchingJobVisibleInImage = new ArrayList<>();

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("signboards_branding")
    private String signboardsBranding = "";
}
