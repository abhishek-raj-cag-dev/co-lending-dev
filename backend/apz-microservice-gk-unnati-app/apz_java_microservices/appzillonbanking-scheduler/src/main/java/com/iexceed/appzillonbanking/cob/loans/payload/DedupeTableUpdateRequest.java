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
public class DedupeTableUpdateRequest {

    private String id = "";
    private String gkv = "";
    private String method= "";
    private String customerId = "";
    private String custqualify = "";
    private String kendraId = "";
    private String groupId = "";
    private String name = "";
    private String recordtype = "";
    private String phoneNum1 = "";
    private String spkycid = "";
    private String spkycname = "";
    private String bankAccNo = "";
    private String bankname = "";
    private String bankBranchName = "";
    private String ifscCode = "";
    private String accHolderName ="";
    private String cb_status = "";
    private String branchId = "";
    private List<LegalDocument> legalDocument = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LegalDocument {
        private String name = "";
        private String id = "";
    }
}
