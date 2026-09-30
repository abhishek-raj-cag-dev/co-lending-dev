package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BREKirana {
	   @ApiModelProperty(required = false, example = "Stationary")
	    @JsonProperty("type_of_shop")
	    private String typeOfShop = "";

	    @ApiModelProperty(required = false, example = "happy shop")
	    @JsonProperty("name_of_shop")
	    private String nameOfShop = "";

	    @ApiModelProperty(required = false, example = "Market Stall")
	    @JsonProperty("shop_setup")
	    private String shopSetup = "";

	    @ApiModelProperty(required = false, example = "Market Area")
	    @JsonProperty("surrounding_area_classification")
	    private String surroundingAreaClassification = "";

	    @ApiModelProperty(required = false, example = "Local Retail Market")
	    @JsonProperty("market_classification")
	    private String marketClassification = "";

	    @ApiModelProperty(required = false, example = "shop")
	    @JsonProperty("business_location")
	    private String businessLocation = "";

	    @ApiModelProperty(required = false, example = "75.5")
	    @JsonProperty("area_of_shop_sqft")
	    private double areaOfShopSqft = 0.0;

	    @ApiModelProperty(required = false, example = "100")
	    @JsonProperty("area_of_godown")
	    private int areaOfGodown = 0;

	    @ApiModelProperty(required = false, example = "High")
	    @JsonProperty("occupancy_level_of_shop")
	    private String occupancyLevelOfShop = "";

	    @ApiModelProperty(required = false, example = "low")
	    @JsonProperty("occupancy_level_of_godown")
	    private String occupancyLevelOfGodown = "";

	    @JsonProperty("prominent_sku_items")
	    private List<String> prominentSkuItems = new ArrayList<>();

	    @JsonProperty("storage_of_bulk_items")
	    private List<String> storageOfBulkItems = new ArrayList<>();

	    @JsonProperty("inventory_purchase_bill_records")
	    private BREPurchaseItems inventoryPurchaseBillrecords = new BREPurchaseItems();

	    @ApiModelProperty(required = false, example = "6")
	    @JsonProperty("customer_footfall_or_visible_crowd")
	    private int customerFootfallOrVisibleCrowd = 0;

	    @ApiModelProperty(required = false, example = "cash")
	    @JsonProperty("payment_methods_visible")
	    private String paymentMethodsVisible = "";

	    @ApiModelProperty(required = false, example = "new")
	    @JsonProperty("condition_of_shop")
	    private String conditionOfShop = "";

	    @ApiModelProperty(required = false, example = "Yes")
	    @JsonProperty("presence_of_fridge_or_freezer")
	    private String presenceOfFridgeOrFreezer = "";

	    @ApiModelProperty(required = false, example = "Powder gali , mumbai")
	    @JsonProperty("address_of_the_kirana")
	    private String addressOfTheKirana = "";
}
