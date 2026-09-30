package com.iexceed.appzillonbanking.cob.payload;

import com.iexceed.appzillonbanking.cob.utils.customAnnotations.ExcelColumn;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class RpcMISReportPayload {

    @ExcelColumn("Registration No")
    private String registrationNo;

    @ExcelColumn("Workstep Name")
    private String workstepName;

    @ExcelColumn("Customer Id")
    private String customerId;

    @ExcelColumn("Customer Name")
    private String customerName;

    @ExcelColumn("Loan Account Number")
    private String loanAccountNumber;

    @ExcelColumn("Q_Sanctioned_Loan_Amount")
    private String qSanctionedLoanAmount;

    @ExcelColumn("Q_Branch_ID")
    private String qBranchId;

    @ExcelColumn("Branch_Name")
    private String branchName;

    @ExcelColumn("RPC Name")
    private String rpcName;

    @ExcelColumn("Case Type")
    private String caseType;

    @ExcelColumn("NACH Reference No")
    private String nachReferenceNo;

    @ExcelColumn("Entry Date")
    private String entryDate;

    @ExcelColumn("Entry Time")
    private String entryTime;

    @ExcelColumn("Turn Around Bucket")
    private String turnAroundBucket;

    @ExcelColumn("Processed By Name")
    private String processedByName;

    @ExcelColumn("Processed By GK ID")
    private String processedGkId;

    @ExcelColumn("Queue Name")
    private String queueName;

    private LocalDateTime createTs;

    public RpcMISReportPayload(
            String registrationNo,
            String workstepName,
            String customerId,
            String customerName,
            String loanAccountNumber,
            BigDecimal qSanctionedLoanAmount,
            String qBranchId,
            String branchName,
            String rpcName,
            String caseType,
            String nachReferenceNo,
            String entryDate,
            String entryTime,
            LocalDateTime createTs,
            String processedByName,
            String processedGkId,
            String queueName
    ) {
        this.qBranchId = qBranchId;
        this.branchName = branchName;
        this.registrationNo = registrationNo;
        this.workstepName = workstepName;
        this.customerId = customerId;
        this.customerName = customerName;
        this.loanAccountNumber = loanAccountNumber;
        this.qSanctionedLoanAmount =
                qSanctionedLoanAmount == null
                        ? "NA"
                        : qSanctionedLoanAmount.stripTrailingZeros().toPlainString();
        this.rpcName = rpcName;
        this.caseType = caseType;
        this.nachReferenceNo = nachReferenceNo;
        this.entryDate = entryDate;
        this.entryTime = entryTime;
        this.createTs = createTs;
        this.turnAroundBucket = computeBucket(createTs);
        this.processedByName = processedByName;
        this.processedGkId = processedGkId;
        this.queueName = queueName;
    }

    private String computeBucket(LocalDateTime createTs) {
        Duration d = Duration.between(createTs, LocalDateTime.now());

        long hours = d.toHours();
        long days = d.toDays();

        if (hours < 1) return "Less than an hour";
        if (days < 1) return "Less than a day";
        if (days == 1) return "2 days";
        if (days == 2) return "3 days";
        if (days == 3) return "4 days";
        if (days == 4) return "5 days";
        return "Above 5 days";
    }
}
