package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BRECommonBusinessCategory {
    @JsonProperty("Kirana")
    private BREKirana kirana = new BREKirana();

    @JsonProperty("Dairy")
    private BREDairy dairy = new BREDairy();

    @JsonProperty("Tailoring")
    private BRETailoring tailoring = new BRETailoring();

    @JsonProperty("Agriculture")
    private BREAgriculture agriculture = new BREAgriculture();

    @JsonProperty("Restaurant_eatery")
    private BRERestaurantEatery restaurantEatery = new BRERestaurantEatery();

    @JsonProperty("Barber")
    private BREBarber barber = new BREBarber();

    @JsonProperty("Animal_Husbandry")
    private BREAnimalHusbandry animalHusbandry = new BREAnimalHusbandry();

    @JsonProperty("Cloth_shop")
    private BREClothShop clothShop = new BREClothShop();
}
