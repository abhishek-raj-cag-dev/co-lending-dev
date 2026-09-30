package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BCMPIOtherDetailsWrapper {

    @JsonProperty("saveAsDraft")
    private String saveAsDraft;

    @JsonProperty("totalExpense")
    private String totalExpense;

    @JsonProperty("houseOwnership")
    private String houseOwnership;

    @JsonProperty("typeOfHouse")
    private String typeOfHouse;

    @JsonProperty("typeOfRoof")
    private String typeOfRoof;

    @JsonProperty("noOfRoomsInHouse")
    private String noOfRoomsInHouse;

    @JsonProperty("modeOfSavings")
    private String modeOfSavings;

    @JsonProperty("noOfTwoWheelerOwned")
    private String noOfTwoWheelerOwned;

    @JsonProperty("smartphonesOwned")
    private String smartphonesOwned;

    @JsonProperty("householdExpenses")
    private String householdExpenses;

    @JsonProperty("educationExpense")
    private String educationExpense;

    @JsonProperty("medicalExpense")
    private String medicalExpense;

    @JsonProperty("foodExpense")
    private String foodExpense;

    @JsonProperty("expenseOnClothing")
    private String expenseOnClothing;

    @JsonProperty("agriland")
    private String agriland;

    @JsonProperty("landOwnerName")
    private String landOwnerName;

    @JsonProperty("relationshipWithApplicant")
    private String relationshipWithApplicant;

    @JsonProperty("basicAmenities")
    private List<String> basicAmenities;

    @JsonProperty("otherAssets")
    private List<String> otherAssets;

    @JsonProperty("smartphoneRadio")
    private String smartphoneRadio;

    @JsonProperty("almirah")
    private String almirah;

    @JsonProperty("chair")
    private String chair;

    @JsonProperty("applicantExService")
    private String applicantExService;

    @JsonProperty("coApplicantExService")
    private String coApplicantExService;

    @JsonProperty("applicantDivyang")
    private String applicantDivyang;

    @JsonProperty("coApplicantDivyang")
    private String coApplicantDivyang;

    @JsonProperty("applicantHealthCondition")
    private String applicantHealthCondition;

    @JsonProperty("coApplicantHealthCondition")
    private String coApplicantHealthCondition;

    @JsonProperty("applicantHealthInsurance")
    private String applicantHealthInsurance;

    @JsonProperty("coApplicantHealthInsurance")
    private String coApplicantHealthInsurance;
    
    @JsonProperty("noOfFamilyMembers")
    private String noOfFamilyMembers;
    
    @JsonProperty("noOfOtherEarningMembers")
    private String noOfOtherEarningMembers;
 
}
