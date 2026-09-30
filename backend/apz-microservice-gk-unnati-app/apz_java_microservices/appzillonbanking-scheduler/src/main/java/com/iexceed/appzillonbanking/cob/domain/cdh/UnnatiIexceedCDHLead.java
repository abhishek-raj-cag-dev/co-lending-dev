package com.iexceed.appzillonbanking.cob.domain.cdh;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(
        name = "unnati_iexceed_lead",
        uniqueConstraints = {
                @UniqueConstraint(name = "application_id_UNIQUE", columnNames = "application_id"),
                @UniqueConstraint(name = "t24_cust_id_UNIQUE", columnNames = "t24_cust_id")
        }
)
public class UnnatiIexceedCDHLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unique_id")
    private Long uniqueId;

    @Column(name = "reference_id", length = 50)
    private String referenceId;

    /**
     * Maps to DB column application_id.
     * Represents Open Market ID in COB context.
     */
    @Column(name = "application_id")
    private Long openMarketId;

    @Column(name = "customer_id", length = 25)
    private String customerId;

    @Column(name = "title", length = 10)
    private String title;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gl_branch_id", length = 50)
    private String glBranchId;

    @Column(name = "gl_branch_name", length = 100)
    private String glBranchName;

    @Column(name = "kendra_id", length = 50)
    private String kendraId;

    @Column(name = "kendra_name", length = 100)
    private String kendraName;

    @Column(name = "group_id", length = 50)
    private String groupId;

    @Column(name = "gl_branch_state", length = 50)
    private String glBranchState;

    @Column(name = "age")
    private Integer age;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(name = "marital_status", length = 20)
    private String maritalStatus;

    @Column(name = "father_name", length = 255)
    private String fatherName;

    @Column(name = "spouse_name", length = 255)
    private String spouseName;

    @Column(name = "primary_kyc", length = 50)
    private String primaryKyc;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "name_as_per_bank_account", length = 255)
    private String nameAsPerBankAccount;

    @Column(name = "ifsc_code", length = 20)
    private String ifscCode;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "branch_name", length = 100)
    private String branchName;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "present_line_1", length = 255)
    private String presentLine1;

    @Column(name = "present_line_2", length = 255)
    private String presentLine2;

    @Column(name = "present_line_3", length = 255)
    private String presentLine3;

    @Column(name = "present_pincode")
    private Integer presentPincode;

    @Column(name = "present_area", length = 100)
    private String presentArea;

    @Column(name = "present_city_town_village", length = 100)
    private String presentCityTownVillage;

    @Column(name = "present_district", length = 100)
    private String presentDistrict;

    @Column(name = "present_state", length = 100)
    private String presentState;

    @Column(name = "present_country", length = 100)
    private String presentCountry;

    @Column(name = "present_location_co_ordinates", length = 100)
    private String presentLocationCoordinates;

    @Column(name = "permanent_line_1", length = 255)
    private String permanentLine1;

    @Column(name = "permanent_line_2", length = 255)
    private String permanentLine2;

    @Column(name = "permanent_line_3", length = 255)
    private String permanentLine3;

    @Column(name = "permanent_pincode")
    private Integer permanentPincode;

    @Column(name = "permanent_area", length = 100)
    private String permanentArea;

    @Column(name = "permanent_city_town_village", length = 100)
    private String permanentCityTownVillage;

    @Column(name = "permanent_district", length = 100)
    private String permanentDistrict;

    @Column(name = "permanent_state", length = 100)
    private String permanentState;

    @Column(name = "permanent_country", length = 100)
    private String permanentCountry;

    @Column(name = "permanent_location_co_ordinates", length = 100)
    private String permanentLocationCoordinates;

    @Column(name = "type_of_account", length = 50)
    private String typeOfAccount;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "no_of_years_relationship")
    private Long noOfYearsRelationship;

    @Column(name = "kendra_size")
    private Integer kendraSize;

    @Column(name = "kendra_vintage_yrs")
    private Integer kendraVintageYrs;

    @Column(name = "group_size")
    private Integer groupSize;

    @Column(name = "kendra_par_status", length = 50)
    private String kendraParStatus;

    @Column(name = "kendra_meeting_freq", length = 50)
    private String kendraMeetingFreq;

    @Column(name = "kendra_meeting_day", length = 20)
    private String kendraMeetingDay;

    @Column(name = "gl_region", length = 50)
    private String glRegion;

    @Column(name = "gl_area", length = 50)
    private String glArea;

    @Column(name = "bank_branch_pincode")
    private Integer bankBranchPincode;

    @Column(name = "customer_type", length = 50)
    private String customerType;

    @Column(name = "present_address_type", length = 50)
    private String presentAddressType;

    @Column(name = "permanent_address_type", length = 50)
    private String permanentAddressType;

    @Column(name = "caglos")
    private Integer caglos;

    @Column(name = "priority", length = 50)
    private String priority;

    @Column(name = "prest_addre_landmark", length = 255)
    private String prestAddreLandmark;

    @Column(name = "prest_addre_currentaddressproof", length = 100)
    private String prestAddreCurrentAddressProof;

    @Column(name = "prest_addre_housetype", length = 50)
    private String prestAddreHouseType;

    @Column(name = "prest_addre_residentownership", length = 50)
    private String prestAddreResidentOwnership;

    @Column(name = "prest_addre_residenceaddresssince", length = 20)
    private String prestAddreResidenceAddressSince;

    @Column(name = "prest_addre_residencecitysince", length = 20)
    private String prestAddreResidenceCitySince;

    @Column(name = "permt_addre_landmark", length = 255)
    private String permtAddreLandmark;

    @Column(name = "permt_addre_currentaddressproof", length = 100)
    private String permtAddreCurrentAddressProof;

    @Column(name = "permt_addre_housetype", length = 50)
    private String permtAddreHouseType;

    @Column(name = "permt_addre_residentownership", length = 50)
    private String permtAddreResidentOwnership;

    @Column(name = "permt_addre_residenceaddresssince", length = 20)
    private String permtAddreResidenceAddressSince;

    @Column(name = "permt_addre_residencecitysince", length = 20)
    private String permtAddreResidenceCitySince;

    @Column(name = "app_education", length = 50)
    private String appEducation;

    @Column(name = "app_religion", length = 50)
    private String appReligion;

    @Column(name = "app_caste", length = 50)
    private String appCaste;

    @Column(name = "branch_e_mail_id", length = 100)
    private String branchEMailId;

    @Column(name = "applicant_c_kyc")
    private Integer applicantCKyc;

    @Column(name = "urn", length = 255)
    private String urn;

    @Column(name = "status_of_the_application", length = 50)
    private String statusOfTheApplication;

    @Column(name = "source_of_the_application", length = 50)
    private String sourceOfTheApplication;

    @Column(name = "nqa_flag", length = 10)
    private String nqaFlag;

    @Column(name = "product", length = 50)
    private String product;

    @Column(name = "amount_approved_from_bre", precision = 15, scale = 2)
    private BigDecimal amountApprovedFromBre;

    @Column(name = "first_act_date")
    private LocalDateTime firstActDate;

    @Column(name = "lead_initiation_date")
    private LocalDateTime leadInitiationDate;

    @UpdateTimestamp
    @Column(name = "latest_updated_date")
    private LocalDateTime latestUpdatedDate;

    @Column(name = "gl_eligible", length = 1)
    private String glEligible;

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;

    @Column(name = "unnati_status", length = 50)
    private String unnatiStatus;

    @Column(name = "km_id", length = 15)
    private String kmId;

    @Column(name = "t24_cust_id")
    private Integer t24CustId;

    @Column(name = "kendra_act_date")
    private LocalDate kendraActDate;

    @Column(name = "annualIncome")
    private String annualIncome;
}