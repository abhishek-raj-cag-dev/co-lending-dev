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
public class BRERestaurantEatery {

    @ApiModelProperty(required = false, example = "Annapurna Restaurant")
    @JsonProperty("name_of_restaurant")
    private String nameOfRestaurant = "";

    @ApiModelProperty(required = false, example = "123 Main Street, Chennai")
    @JsonProperty("address_of_the_restaurant")
    private String addressOfTheRestaurant = "";

    @ApiModelProperty(required = false, example = "Dine-in")
    @JsonProperty("type_of_setup")
    private String typeOfSetup = "";

    @ApiModelProperty(required = false, example = "500.5")
    @JsonProperty("area_of_shop")
    private Double areaOfShop = 0.0;

    @ApiModelProperty(required = false, example = "Commercial Area")
    @JsonProperty("location_of_shop")
    private String locationOfShop = "";

    @ApiModelProperty(required = false, example = "Vegetarian")
    @JsonProperty("serving_type")
    private String servingType = "";

    @ApiModelProperty(required = false, example = "15")
    @JsonProperty("number_of_tables")
    private int numberOfTables = 0;

    @ApiModelProperty(required = false, example = "50")
    @JsonProperty("customer_footfall_visible_crowd")
    private int customerFootfallVisibleCrowd = 0;

    @ApiModelProperty(required = false, example = "8")
    @JsonProperty("number_of_waiter_cook")
    private int numberOfWaiterCook = 0;

    @ApiModelProperty(required = false, example = "High")
    @JsonProperty("occupancy_level")
    private String occupancyLevel = "";

    @JsonProperty("maximum_dish_observed")
    private List<String> maximumDishObserved = new ArrayList<>();

    @ApiModelProperty(required = false, example = "25")
    @JsonProperty("number_of_dish_observed")
    private int numberOfDishObserved = 0;

    @ApiModelProperty(required = false, example = "10")
    @JsonProperty("stock_of_milk")
    private int stockOfMilk = 0;

    @ApiModelProperty(required = false, example = "5")
    @JsonProperty("stock_of_tea")
    private int stockOfTea = 0;

    @ApiModelProperty(required = false, example = "3")
    @JsonProperty("stock_coffee")
    private int stockCoffee = 0;

    @ApiModelProperty(required = false, example = "12")
    @JsonProperty("biscuit_container_number")
    private int biscuitContainerNumber = 0;

    @ApiModelProperty(required = false, example = "8")
    @JsonProperty("cake_container_number")
    private int cakeContainerNumber = 0;

    @JsonProperty("menu_display_price_list")
    private BREMenuDisplayPriceList menuDisplayPriceList;

    @ApiModelProperty(required = false, example = "POS, QR, Cash")
    @JsonProperty("payment_methods_visible_pos_qr_cash")
    private String paymentMethodsVisiblePosQrCash = "";

}