package com.iexceed.appzillonbanking.cob.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMasterId;
import com.iexceed.appzillonbanking.cob.payload.RpcProductivityReportPayload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(transactionManager = "abCOBTransactionManager")
public interface RpcProductivityReportRepository extends JpaRepository<ApplicationMaster, ApplicationMasterId> {

    @Query(value = "SELECT new com.iexceed.appzillonbanking.cob.payload.RpcProductivityReportPayload(" +
            " am.workitemNo, " +
            " am.searchCode2, " +
            " cd.customerName," +
            " CASE WHEN aw.applicationStatus IN ('APPROVED', 'RPCPUSHBACK') THEN 'RPC Maker' " +
            " WHEN aw.applicationStatus IN ('PENDINGFORRPCVERIFICATION') THEN 'RPC Checker' " +
            " WHEN aw.applicationStatus IN ('DBKITGENERATED') THEN 'RPC DB Verification' END, " +
            " CASE WHEN EXISTS ( " +
            "     SELECT 1 FROM ApplicationWorkflow wfPrev " +
            "     WHERE wfPrev.applicationId = aw.applicationId " +
            "     AND wfPrev.workflowSeqNum < aw.workflowSeqNum " +
            "     AND wfPrev.applicationStatus = aw.applicationStatus " +
            " ) THEN 'REWORK' ELSE 'NEW' END, " +
            "CASE WHEN aw.applicationStatus IN ('APPROVED', 'RPCPUSHBACK') AND nextAw.applicationStatus IN ('IPUSHBACK') THEN 'Maker Send Back' " +
            " WHEN aw.applicationStatus IN ('APPROVED', 'RPCPUSHBACK') AND nextAw.applicationStatus IN ('REJECTED') THEN 'Maker Reject' " +
            " WHEN aw.applicationStatus IN ('APPROVED', 'RPCPUSHBACK') AND nextAw.applicationStatus IN ('PENDINGFORRPCVERIFICATION') THEN 'Maker to Checker' " +
            " WHEN aw.applicationStatus IN ('APPROVED', 'RPCPUSHBACK') AND nextAw.applicationStatus IS NULL THEN 'Maker Proceed' " +
            " WHEN aw.applicationStatus IN ('PENDINGFORRPCVERIFICATION') AND nextAw.applicationStatus IN ('RPCPUSHBACK') THEN 'Checker Send Back to Maker' " +
            " WHEN aw.applicationStatus IN ('PENDINGFORRPCVERIFICATION') AND nextAw.applicationStatus IN ('REJECTED') THEN 'Checker Reject' " +
            " WHEN aw.applicationStatus IN ('PENDINGFORRPCVERIFICATION') AND nextAw.applicationStatus IN ('RPCVERIFIED') THEN 'Checker to Credit Assessment' " +
            " WHEN aw.applicationStatus IN ('PENDINGFORRPCVERIFICATION') AND nextAw.applicationStatus IN ('IPUSHBACK') THEN 'Checker to KM Send Back' " +
            " WHEN aw.applicationStatus IN ('PENDINGFORRPCVERIFICATION') AND nextAw.applicationStatus IS NULL THEN 'Checker Proceed' " +
            " WHEN aw.applicationStatus IN ('DBKITGENERATED') AND nextAw.applicationStatus IN ('IPUSHBACK') THEN 'DB Kit Verification Send Back' " +
            " WHEN aw.applicationStatus IN ('DBKITGENERATED') AND nextAw.applicationStatus IN ('REJECTED') THEN 'DB Kit Verification Reject' " +
            " WHEN aw.applicationStatus IN ('DBKITGENERATED') AND nextAw.applicationStatus IN ('DBPUSHBACK') THEN 'DB Kit Verification Send Back to BM' " +
            " WHEN aw.applicationStatus IN ('DBKITGENERATED') AND nextAw.applicationStatus IN ('DBKITVERIFIED') THEN 'DB Kit Verified' " +
            " WHEN aw.applicationStatus IN ('DBKITGENERATED') AND nextAw.applicationStatus IN ('RESANCTION') THEN 'DB Kit Verification send back to Resanction' " +
            " WHEN aw.applicationStatus IN ('DBKITGENERATED') AND nextAw.applicationStatus IS NULL THEN 'DB Kit Verification Proceed' END, " +
            " FUNCTION('to_char', aw.createTs, 'DD/MM/YYYY'), " +
            " FUNCTION('to_char', aw.createTs, 'HH12:MI AM'), " +
            " FUNCTION('to_char', nextAw.createTs, 'DD/MM/YYYY'), " +
            " FUNCTION('to_char', nextAw.createTs, 'HH12:MI AM'), " +
            " nextAw.createdBy, " +
            " 'NA', " +
            " nextAw.remarks, " +
            " am.branchId, " +
            " am.branchName, " +
            " 'NA' ) " +
            " FROM ApplicationWorkflow aw " +
            " JOIN ApplicationMaster am ON am.applicationId = aw.applicationId " +
            " LEFT JOIN CustomerDetails cd ON cd.applicationId = am.applicationId " +
            " AND cd.customerType = 'Applicant' " +
            " JOIN ApplicationWorkflow nextAw ON nextAw.applicationId = aw.applicationId " +
            " AND nextAw.workflowSeqNum = aw.workflowSeqNum + 1 " +
            " WHERE aw.applicationStatus IN ('APPROVED','PENDINGFORRPCVERIFICATION','RPCPUSHBACK','DBKITGENERATED') " +
            " AND am.declarationFlag = 'I' AND nextAw.applicationStatus NOT IN ('PUSHBACK') " +
            " AND nextAw.createTs BETWEEN :fromDate AND :toDate " +
            " AND nextAw.applicationStatus <> aw.applicationStatus ")
    List<RpcProductivityReportPayload> getProductivityReportData(LocalDateTime fromDate, LocalDateTime toDate);
}
