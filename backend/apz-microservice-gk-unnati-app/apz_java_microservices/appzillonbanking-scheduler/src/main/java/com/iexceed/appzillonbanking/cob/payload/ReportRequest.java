package com.iexceed.appzillonbanking.cob.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {

    private String reportType;
    private String referenceNumber;
    private String branch;
    private String productType;
    private String customerName;
    private String applicationStatus;
    private String vkycRefNo;
    private String doorsWorkflowId;
    private String lastUpdateFromDate;
    private String lastUpdateToDate;
    private String fromDate;
    private String toDate;
    private String reportFormat;

}
