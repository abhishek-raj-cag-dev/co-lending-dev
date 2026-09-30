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
public class BREAgriculture {
    @ApiModelProperty(required = false, example = "")
    @JsonProperty("crop_type")
    private String cropType = "";

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("crop_name")
    private String cropName = "";

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("crop_quality")
    private String cropQuality = "";

    @ApiModelProperty(required = false, example = "0.0")
    @JsonProperty("area_of_the_farm")
    private double areaOfTheFarm = 0.0;

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("occupancy_of_farm")
    private String occupancyOfFarm = "";

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("irrigated_nonirrigated")
    private String irrigatedNonirrigated = "";

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("any_crop_disease_observed")
    private String anyCropDiseaseObserved = "";

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("name_of_disease")
    private String nameOfDisease = "";

    @JsonProperty("machinery_visible")
    private List<String> machineryVisible = new ArrayList<>();

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("irrigation_source")
    private String irrigationSource = "";

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("storage_warehouse")
    private String storageWarehouse = "";

    @ApiModelProperty(required = false, example = "")
    @JsonProperty("labor_activity")
    private String laborActivity = "";
}

