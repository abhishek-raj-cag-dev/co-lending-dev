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
public class BREClothShop {

    @ApiModelProperty(required = false, example = "Owned")
    @JsonProperty("business_premise")
    private String businessPremise = "";

    @ApiModelProperty(required = false, example = "Fashion Point")
    @JsonProperty("name_of_shop")
    private String nameOfShop = "";

    @ApiModelProperty(required = false, example = "T Nagar, Chennai")
    @JsonProperty("shop_location")
    private String shopLocation = "";

    @ApiModelProperty(required = false, example = "Retail")
    @JsonProperty("type_of_setup")
    private String typeOfSetup = "";

    @ApiModelProperty(required = false, example = "800.5")
    @JsonProperty("size_of_shop_sqft")
    private Double sizeOfShopSqft = 0.0;

    @ApiModelProperty(required = false, example = "Owned")
    @JsonProperty("occupancy")
    private String occupancy = "";

    @ApiModelProperty(required = false, example = "12")
    @JsonProperty("number_of_cells")
    private int numberOfCells = 0;

    @JsonProperty("items_observed_in_list")
    private BREItemsObservedInList itemsObservedInList;

    @ApiModelProperty(required = false, example = "POS, QR, Cash")
    @JsonProperty("payment_methods_visible_pos_qr_cash")
    private String paymentMethodsVisiblePosQrCash = "";

    @ApiModelProperty(required = false, example = "123 Main Road, T Nagar, Chennai - 600017")
    @JsonProperty("address_of_the_shop")
    private String addressOfTheShop = "";

}