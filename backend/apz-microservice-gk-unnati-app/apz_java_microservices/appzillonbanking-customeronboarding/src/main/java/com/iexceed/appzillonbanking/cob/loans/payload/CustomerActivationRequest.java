package com.iexceed.appzillonbanking.cob.loans.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerActivationRequest {
    private Header header;
    private Body body;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {
        private Override override;
        private Audit audit;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Override {
        private List<OverrideDetails> overrideDetails = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverrideDetails {
        private String id ="";
        private String description ="";
        private String code="";
        private String responseCode="";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Audit {
        private String versionNumber="";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Body {
        private String customerIdTemp;
        private String companyIdTemp;
        private List<LegalDocument> legalDocument = new ArrayList<>();
        private List<LegalDocumentRepository> legalDocumentRepositry = new ArrayList<>();
        private List<PresentAddress> presentAddress = new ArrayList<>();
        private List<CommunicationAddress> communicationAddress = new ArrayList<>();
        private List<SpouseKyc> spouseKyc = new ArrayList<>();
        private String member = "";
        private List<Nominee> nominee = new ArrayList<>();
        private String customerQualify="";
        private String customerType="";
        private String custType="";
        private String gender="";
        private String groupId="";
        private String custLanguage="";
        private String kycDataCollectionDate="";
        private String cbDate="";
        private String state="";
        private String address="";
        private String village="";
        private String landHolding="";
        private String branchId="";
        private String village1="";
        private String taluk1="";
        private String districtId1="";
        private String state1="";
        private String pinCode1="";
        private String latitude1="";
        private String longitude1="";
        private String curAddress1="";
        private String residingSince1="";
        private String kendraId="";
        private String taluk="";
        private String dob="";
        private String religion="";
        private String districtId="";
        private String phone="";
        private String pinCode="";
        private String name="";
        private String maritalStatus="";
        private String spName="";
        private String spouseDob="";
        private String latitude="";
        private String longitude="";
        private String occupation="";
        private String caste="";
        private String totalIncome="";
        private String agriLandDry="";
        private String agriLandWet="";
        private String bankACNo="";
        private String nomineeIfscCode="";
        private String nomineeAcHolderName="";
        private String potentialCustomerNumber="";
        private String resident="";
        private String customerlanguage="";
        private String nationality="";
        private String residence="";
        private String fatherName="";
        private String totalFamilyMembers="";
        private String noOfAdults="";
        private String noOfChildren="";
        private String noOfEarningMembers="";
        private String customerUPIID="";
        private String bankifsccode="";
        private String accountNumber="";
        private String accountHolderName="";
        private String pennyCheckValidated="";
        private String industry="";
        private String sector="";
        private String cgtBy="";
        private String reinterviewDate="";
        private String reInterviewBy="";
        private String grtRemark="";
        private String secondaryMobileNumber="";
        private String emailId="";
        private String joiningDateofEnquiry="";
        private String joiningCbStatus="";
        private String joiningCbSummary="";
        private String joiningCbReportLink="";
        private String joiningCbRemarks="";
        private String joiningCbScore="";
        private String customerName="";
        private String middleName="";
        private String lastName="";
        private String title="";
        private String spouseKycLink="";
        private String spouseCustId="";
        private String educationLevel="";
        private String customerPhotoLink="";
        private String customerSpousePhotoLink="";
        private String customerSignaturePhotoLink="";
        private String residingSince="";
        private String currentAddress="";
        private String commAddrSameAsPresentAddr="";
        private String addressProofLink="";
        private String nomineePassbookLink="";
        private String nomineeFormDmsRepoLink="";
        private String bankPassbookLink="";
        private String openingDate="";
        private String mobAppId="";
        private String valSource="";
        private String mobileNumberVal="";
        private String ValidationDate="";
        private String cbSummary="";
        private String cbReplink="";
        private String cbRemarks="";
        private String cbScore="";
        private String cbStatus="";
        private String nominalForm="";
        private String reActivationDate="";
        private String firstAcOfficer="";
        private String override="";
        private String recordStatus="";
        private String currentNumber="";
        private String inputter="";
        private String authoriser="";
        private String dateTime="";
        private String companyCode="";
        private String departmentCode="";
        private String auditor="";
        private String auditDateTime="";
        private String prospectCustomerId ="";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LegalDocument {
        private String id="";
        private String legalHolderName="";
        private String name="";
        private String date="";
        private String legalExpDate="";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LegalDocumentRepository {
        private String legalIdType="";
        private String primaryIdLink="";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PresentAddress {
        private String permanentAddressLine="";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunicationAddress {
        private String address="";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpouseKyc {
        private String name="";
        private String id="";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Nominee {
        private String name="";
        private String upi="";
        private String taluk="";
        private String district="";
        private String state="";
        private String postalCode="";
        private String phone="";
        private String relationCode="";
        private String amount="";
    }
}