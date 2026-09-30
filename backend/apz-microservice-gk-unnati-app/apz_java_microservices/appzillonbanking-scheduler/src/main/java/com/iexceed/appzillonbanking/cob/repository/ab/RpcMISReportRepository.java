package com.iexceed.appzillonbanking.cob.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMasterId;
import com.iexceed.appzillonbanking.cob.payload.RpcMISReportPayload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(transactionManager = "abCOBTransactionManager")
public interface RpcMISReportRepository extends JpaRepository<ApplicationMaster, ApplicationMasterId> {

    @Query(value = "SELECT new com.iexceed.appzillonbanking.cob.payload.RpcMISReportPayload( " +
            " app.workitemNo , " +
            " CASE WHEN app.applicationStatus IN ('APPROVED', 'RPCPUSHBACK') THEN 'RPC Maker' " +
            " WHEN app.applicationStatus IN ('PENDINGFORRPCVERIFICATION') THEN 'RPC Checker' " +
            " WHEN app.applicationStatus IN ('DBKITGENERATED') THEN 'RPC DB Verification' END, " +
            " app.searchCode2, " +
            " COALESCE(cd.customerName, 'NA'), " +
            " COALESCE(loanDtl.t24LoanId, 'NA'), " +
            " loanDtl.sanctionedLoanAmount, " +
            " app.branchId, " +
            " app.branchName, " +
            " 'NA', " +
            " CASE WHEN COUNT(DISTINCT applnWfRework.applicationId) > 0 " +
            " THEN 'REWORK' ELSE 'NEW' END, " +
            " COALESCE(en.pgTranId, 'NA'), " +
            " FUNCTION('to_char', aw.createTs, 'DD/MM/YYYY'), " +
            " FUNCTION('to_char', aw.createTs, 'HH12:MI AM'), " +
            " aw.createTs, " +
            " 'NA', " +
            " 'NA', " +
            " CASE WHEN app.applicationStatus IN ('APPROVED', 'RPCPUSHBACK') THEN 'Unnati_RPC  Maker' " +
            " WHEN app.applicationStatus IN ('PENDINGFORRPCVERIFICATION') THEN 'Unnati_RPC  Checker' " +
            " WHEN app.applicationStatus IN ('DBKITGENERATED') THEN 'Unnati_RPC  DB Verification' END " +
            ") " +
            " FROM ApplicationMaster app " +
            " LEFT JOIN LoanDetails loanDtl ON app.applicationId = loanDtl.applicationId " +
            " LEFT JOIN ApplicationWorkflow aw ON app.applicationId = aw.applicationId AND aw.workflowSeqNum = ( " +
            " SELECT MAX(aw2.workflowSeqNum) FROM ApplicationWorkflow aw2 WHERE aw2.applicationId = app.applicationId ) " +
            " LEFT JOIN Enach en on en.applicationId = app.applicationId and " +
            " en.pgTranId = (SELECT MAX(e2.pgTranId) FROM Enach e2 WHERE e2.applicationId = app.applicationId " +
            " AND e2.pgTranId <> '0') " +
            " LEFT JOIN ApplicationWorkflow applnWfRework ON applnWfRework.applicationId = app.applicationId AND " +
            " (applnWfRework.applicationStatus IN ('IPUSHBACK','RPCPUSHBACK','DBPUSHBACK') OR " +
            " EXISTS (SELECT 1 FROM ApplicationWorkflow wf2 WHERE wf2.applicationId = applnWfRework.applicationId AND " +
            " wf2.workflowSeqNum <= applnWfRework.workflowSeqNum - 2 AND wf2.applicationStatus = 'APPROVED')) " +
            " LEFT JOIN CustomerDetails cd ON cd.applicationId = app.applicationId AND cd.customerType = 'Applicant' " +
            " WHERE app.applicationStatus " +
            " IN ('APPROVED','PENDINGFORRPCVERIFICATION','RPCPUSHBACK','DBKITGENERATED') " +
            " AND app.declarationFlag = 'I' AND aw.createTs BETWEEN :fromDate AND :toDate " +
            " GROUP BY app.workitemNo, app.applicationStatus, app.searchCode2, loanDtl.t24LoanId, " +
            " app.branchId, loanDtl.sanctionedLoanAmount, app.branchName, en.pgTranId, aw.createTs,cd.customerName ")
    List<RpcMISReportPayload> getMISReportData(LocalDateTime fromDate, LocalDateTime toDate);
}
