package com.iexceed.appzillonbanking.cob.payload;

import com.iexceed.appzillonbanking.cob.utils.customAnnotations.ExcelColumn;
import lombok.Data;

@Data
public class RpcProductivityReportPayload {

    @ExcelColumn("PID")
    private String pid;

    @ExcelColumn("Customer ID")
    private String customerId;

    @ExcelColumn("Customer Name")
    private String customerName;

    @ExcelColumn("Branch ID")
    private String branchId;

    @ExcelColumn("Branch Name")
    private String branchName;

    @ExcelColumn("Stage")
    private String stage;

    @ExcelColumn("Case Type")
    private String caseType;

    @ExcelColumn("Action")
    private String action;

    @ExcelColumn("Entry Date")
    private String entryDate;

    @ExcelColumn("Entry Time")
    private String entryTime;

    @ExcelColumn("Action Date")
    private String actionDate;

    @ExcelColumn("Action Time")
    private String actionTime;

    @ExcelColumn("User ID")
    private String userId;

    @ExcelColumn("RPC Staff Name")
    private String rpcStaffName;

    @ExcelColumn("RPC Name")
    private String rpcName;

    @ExcelColumn("Remarks")
    private String remarks;

    public RpcProductivityReportPayload(
            String pid,
            String customerId,
            String customerName,
            String stage,
            String caseType,
            String action,
            String entryDate,
            String entryTime,
            String actionDate,
            String actionTime,
            String userId,
            String rpcStaffName,
            String remarks,
            String branchId,
            String branchName,
            String rpcName
    ) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.pid = pid;
        this.customerId = customerId;
        this.customerName = customerName;
        this.stage = stage;
        this.caseType = caseType;
        this.action = action;
        this.entryDate = entryDate;
        this.entryTime = entryTime;
        this.actionDate = actionDate;
        this.actionTime = actionTime;
        this.userId = userId;
        this.rpcStaffName = rpcStaffName;
        this.remarks = remarks;
        this.rpcName = rpcName;
    }
}
