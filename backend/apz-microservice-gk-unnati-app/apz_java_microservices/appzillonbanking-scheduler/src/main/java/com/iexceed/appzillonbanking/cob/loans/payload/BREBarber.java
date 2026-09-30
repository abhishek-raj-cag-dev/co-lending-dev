package com.iexceed.appzillonbanking.cob.loans.payload;

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
public class BREBarber {

    @ApiModelProperty(required = false, example = "Star Hair Salon")
    @JsonProperty("name_of_shop")
    private String nameOfShop = "";

    @ApiModelProperty(required = false, example = "Owned")
    @JsonProperty("business_premise")
    private String businessPremise = "";

    @ApiModelProperty(required = false, example = "45 MG Road, Chennai")
    @JsonProperty("address_of_the_shop")
    private String addressOfTheShop = "";

    @ApiModelProperty(required = false, example = "6")
    @JsonProperty("number_of_chairs_stations")
    private int numberOfChairsStations = 0;

    @ApiModelProperty(required = false, example = "8")
    @JsonProperty("barber_count_workers")
    private int barberCountWorkers = 0;

    @ApiModelProperty(required = false, example = "25")
    @JsonProperty("customer_footfall_visible")
    private int customerFootfallVisible = 0;

    @ApiModelProperty(required = false, example = "350.5")
    @JsonProperty("shop_size_setup")
    private Double shopSizeSetup = 0.0;

    @JsonProperty("visible_services_board")
    private BREVisibleServicesBoard visibleServicesBoard;

    @ApiModelProperty(required = false, example = "10")
    @JsonProperty("number_of_services")
    private int numberOfServices = 0;

    @ApiModelProperty(required = false, example = "Good")
    @JsonProperty("lighting_cleanliness")
    private String lightingCleanliness = "";

    @ApiModelProperty(required = false, example = "AC")
    @JsonProperty("ac_fan")
    private String acFan = "";

    @ApiModelProperty(required = false, example = "8")
    @JsonProperty("number_of_chairs_in_waiting_area")
    private int numberOfChairsInWaitingArea = 0;

    @ApiModelProperty(required = false, example = "POS, QR, Cash")
    @JsonProperty("payment_methods_visible_pos_qr_cash")
    private String paymentMethodsVisiblePosQrCash = "";

    @JsonProperty("menu_display_price_list")
    private BREMenuDisplayPriceList menuDisplayPriceList;


}