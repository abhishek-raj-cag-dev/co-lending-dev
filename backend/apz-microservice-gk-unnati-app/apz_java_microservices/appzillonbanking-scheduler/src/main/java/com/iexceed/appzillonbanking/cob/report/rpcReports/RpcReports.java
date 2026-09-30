package com.iexceed.appzillonbanking.cob.report.rpcReports;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iexceed.appzillonbanking.cob.core.domain.ab.DBKITMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.RPCMaster;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.repository.ab.DBKITMasterRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.RPCMasterRepository;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.loans.domain.user.BranchAreaMappingDetails;
import com.iexceed.appzillonbanking.cob.loans.domain.user.TbAsmiUser;
import com.iexceed.appzillonbanking.cob.loans.repository.user.TATBranchDetailsRepository;
import com.iexceed.appzillonbanking.cob.loans.repository.user.TbUserRepository;
import com.iexceed.appzillonbanking.cob.payload.RpcMISReportPayload;
import com.iexceed.appzillonbanking.cob.payload.RpcProductivityReportPayload;
import com.iexceed.appzillonbanking.cob.payload.RpcReportRequestWrapper;
import com.iexceed.appzillonbanking.cob.repository.ab.RpcMISReportRepository;
import com.iexceed.appzillonbanking.cob.repository.ab.RpcProductivityReportRepository;
import com.iexceed.appzillonbanking.cob.utils.ExcelGenerator;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RpcReports {

    @Data
    private static class FileResponse {
        private String fileName;
        private String fileData;
    }

    private static final Logger logger = LogManager.getLogger(RpcReports.class);

    private final RpcMISReportRepository rpcMISReportRepository;
    private final RpcProductivityReportRepository rpcProductivityReportRepository;
    private final Gson gson;
    private final DBKITMasterRepository dbkitMasterRepository;
    private final RPCMasterRepository rpcMasterRepository;
    private final TbUserRepository tbUserRepository;
    private final TATBranchDetailsRepository tatBranchDetailsRepository;

    public RpcReports(RpcMISReportRepository rpcMISReportRepository,
                      RpcProductivityReportRepository rpcProductivityReportRepository, Gson gson, DBKITMasterRepository dbkitMasterRepository,
                      RPCMasterRepository rpcMasterRepository, TbUserRepository tbUserRepository, TATBranchDetailsRepository tatBranchDetailsRepository) {
        this.tbUserRepository = tbUserRepository;
        this.rpcMasterRepository = rpcMasterRepository;
        this.dbkitMasterRepository = dbkitMasterRepository;
        this.rpcMISReportRepository = rpcMISReportRepository;
        this.rpcProductivityReportRepository = rpcProductivityReportRepository;
        this.gson = gson;
        this.tatBranchDetailsRepository = tatBranchDetailsRepository;
    }

    public static final String PATTERN_1 = "^(\\d+)_([A-Z])_Q(\\d+)$";
    public static final String PATTERN_2 = "^(\\d+)_([A-Z])_oth\\?(.+)$";
    public static final String PATTERN_3 = "^(\\d+)_([^_]+)_(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+)$";
    public static final String PATTERN_4 = "^(\\d+)_oth\\?(.+)_(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+)$";

    public static final Pattern RPC_QUERY_REGEX_PATTERN_1 = Pattern.compile(PATTERN_1);
    public static final Pattern RPC_OTHER_QUERY_REGEX_PATTERN_2 = Pattern.compile(PATTERN_2);
    public static final Pattern DBKIT_VERIFICATION_QUERY_REGEX_PATTERN_3 = Pattern.compile(PATTERN_3);
    public static final Pattern DBKIT_VERIFICATION_OTHER_QUERY_REGEX_PATTERN_4 = Pattern.compile(PATTERN_4);

    public Response generateReport(RpcReportRequestWrapper.RpcReportRequest apiRequest) {
        RpcReportRequestWrapper.RpcReportRequestFields requestFields = apiRequest.getRequestObj();
        String reportType = requestFields.getReportType();
        LocalDate fromDate = requestFields.getFromDate();
        LocalDate toDate = requestFields.getToDate();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        response.setResponseHeader(responseHeader);
        ResponseBody responseBody = new ResponseBody();
        if (reportType == null) {
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            responseBody.setResponseObj("Report Type cannot be null");
            return response;
        }
        if (fromDate == null || toDate == null) {
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            responseBody.setResponseObj("From Date and To Date cannot be null");
            response.setResponseHeader(responseHeader);
            response.setResponseBody(responseBody);
            return response;
        }
        if (fromDate.isAfter(toDate)) {
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            responseBody.setResponseObj("From Date cannot be after To Date");
            response.setResponseHeader(responseHeader);
            response.setResponseBody(responseBody);
            return response;
        }
        if(toDate.isAfter(LocalDate.now())){
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            responseBody.setResponseObj("To Date cannot be more than the current date");
            response.setResponseHeader(responseHeader);
            response.setResponseBody(responseBody);
            return response;
        }
        switch (reportType.toUpperCase()) {
            case "MIS":
                return generateMISReport(fromDate, toDate);
            case "PRODUCTIVITY":
                return generateProductivityReport(fromDate, toDate);
            default:
                responseBody.setResponseObj("Invalid report type");
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
                response.setResponseBody(responseBody);
                response.setResponseHeader(responseHeader);
                return response;
        }
    }

    public Response generateMISReport(LocalDate fromDate, LocalDate toDate) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        ResponseBody responseBody = new ResponseBody();
        try {
            LocalDateTime fromDateTime = fromDate.atStartOfDay();
            LocalDateTime toDateTime = toDate.isEqual(LocalDate.now())
                    ? LocalDateTime.now()
                    : toDate.atTime(LocalTime.MAX);
            String reportFileName = "RPC_MIS_Report_" + fromDate + "_to_" + toDate + ".xlsx";

            List<RpcMISReportPayload> misReportData = rpcMISReportRepository.getMISReportData(fromDateTime, toDateTime);
            if (misReportData.isEmpty()) {
                responseBody.setResponseObj("No data found for the given date range");
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            } else {
//                getRPCDetailsForMISReport(misReportData);
                logger.debug("Fetched MIS report data count={}", misReportData.size());
                String responseString = createExcelBase64(misReportData, RpcMISReportPayload.class, reportFileName);
                responseBody.setResponseObj(responseString);
            }
        } catch (Exception e) {
            logger.error("Error generating MIS report: {}", e.getMessage(), e);
            responseBody.setResponseObj("Error generating MIS report");
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
        }
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    public Response generateProductivityReport(LocalDate fromDate, LocalDate toDate) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
        ResponseBody responseBody = new ResponseBody();
        try {
            LocalDateTime fromDateTime = fromDate.atStartOfDay();
            LocalDateTime toDateTime = toDate.isEqual(LocalDate.now())
                    ? LocalDateTime.now()
                    : toDate.atTime(LocalTime.MAX);
            String reportFileName = "RPC_PRODUCTIVITY_Report_" + fromDate + "_to_" + toDate + ".xlsx";
            List<RpcProductivityReportPayload> productivityReportData = rpcProductivityReportRepository.getProductivityReportData(fromDateTime, toDateTime);
            if (productivityReportData.isEmpty()) {
                responseBody.setResponseObj("No data found for the given date range");
                responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
            } else {
                getUserDetailsForRPCProductivityReport(productivityReportData);
                logger.debug("Fetched productivity report data count={}", productivityReportData.size());
                paintSendbackQueryForProductivityReport(productivityReportData);
                String responseString = createExcelBase64(productivityReportData, RpcProductivityReportPayload.class, reportFileName);
                responseBody.setResponseObj(responseString);
            }
        } catch (Exception e) {
            logger.error("Error generating Productivity report: {}", e.getMessage(), e);
            responseBody.setResponseObj("Error generating Productivity report");
            responseHeader.setResponseCode(ResponseCodes.FAILURE.getKey());
        }

        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
        return response;
    }

    private <T> String createExcelBase64(
            List<T> data,
            Class<T> type,
            String fileName
    ) throws IOException {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ExcelGenerator.generateExcel(data, out, type);
            byte[] bytes = out.toByteArray();
            FileResponse fileResponse = new FileResponse();
            fileResponse.setFileName(fileName);
            fileResponse.setFileData(Base64.getEncoder().encodeToString(bytes));
            return gson.toJson(fileResponse);
        }
    }

    private void paintSendbackQueryForProductivityReport(List<RpcProductivityReportPayload> productivityReportData) {
            logger.debug("paintSendbackQueryForProductivityReport - start, productivityReportData is null? {}", productivityReportData == null);
            if (productivityReportData == null || productivityReportData.isEmpty()) {
                logger.debug("No productivity report data to process. Exiting method.");
                return;
            }

            List<RPCMaster> rpcMasters = rpcMasterRepository.findAll();
            List<DBKITMaster> dbkitMasters = dbkitMasterRepository.findAll();
            logger.debug("Fetched rpcMasters count={}, dbkitMasters count={}", rpcMasters.size(),
                    dbkitMasters.size());

            for (int idx = 0; idx < productivityReportData.size(); idx++) {
                StringBuilder finalRemarks = null;
                RpcProductivityReportPayload reportPayload = null;
                try {
                    reportPayload = productivityReportData.get(idx);
                    String rpcRemarks = reportPayload == null ? null : reportPayload.getRemarks();
                    logger.debug("Processing payload index={}, rpcRemarks is blank? {}", idx, StringUtils.isBlank(rpcRemarks));
                    logger.debug("Processing payload : {}", reportPayload);
                    if (StringUtils.isBlank(rpcRemarks)) {
                        logger.debug("Skipping payload index={} because remarks are blank", idx);
                        continue;
                    }

                    finalRemarks = new StringBuilder();

                    String[] rpcQueryIterable = rpcRemarks.split("\\|");
                    logger.debug("Payload index={} split into {} token(s)", idx, rpcQueryIterable.length);

                    for (int qIdx = 0; qIdx < rpcQueryIterable.length; qIdx++) {
                        String rpcQuery = rpcQueryIterable[qIdx];
                        if (StringUtils.isBlank(rpcQuery)) {
                            logger.debug("Skipping empty token at payloadIndex={}, tokenIndex={}", idx, qIdx);
                            continue;
                        }
                        logger.debug("Processing token at payloadIndex={}, tokenIndex={}, token={}", idx, qIdx, rpcQuery);

                        String patternType = classifyPattern(rpcQuery);
                        logger.debug("Token classified as patternType={} for token={}", patternType, rpcQuery);

                        switch (patternType) {

                            case PATTERN_1: // 5_A_Q3
                                String[] parts = rpcQuery.split("_");
                                if (parts.length < 3) {
                                    logger.debug("PATTERN_1 - invalid format for token={}, partsLength={}", rpcQuery, parts.length);
                                    finalRemarks.append(rpcQuery).append(",\n");
                                    continue;
                                }

                                String stageId = parts[0];
                                String customerType = parts[1].equals("A") ? Constants.APPLICANT : Constants.COAPPLICANT;
                                String queryId = parts[2];
                                logger.debug("PATTERN_1 - stageId={}, customerType={}, queryId={}", stageId, customerType, queryId);

                                RPCMaster matchingMaster = null;
                                for (RPCMaster master : rpcMasters) {
                                    if (master != null && master.getId() != null && master.getId().getId() != null &&
                                            master.getId().getId().compareTo(new BigDecimal(stageId)) == 0) {
                                        matchingMaster = master;
                                        break;
                                    }
                                }

                                if (matchingMaster == null) {
                                    logger.debug("PATTERN_1 - no matching RPCMaster found for stageId={}", stageId);
                                    finalRemarks.append(rpcQuery).append(",\n");
                                    continue;
                                }
                                logger.debug("PATTERN_1 - matched RPCMaster stageName={}", matchingMaster.getStageName());

                                JsonObject queriesJson = new JsonObject();

                                String raw = matchingMaster.getQueries();

                                if (raw != null && !raw.trim().isEmpty()) {
                                    queriesJson = JsonParser.parseString(raw).getAsJsonObject();
                                }

                                logger.debug("queriesJson: {}", queriesJson);

                                JsonArray queryArray = queriesJson.has(customerType)
                                        ? queriesJson.getAsJsonArray(customerType)
                                        : null;

                                logger.debug("queryArray: {}", queryArray);

                                if (queryArray == null) {
                                    finalRemarks.append(rpcQuery).append(",\n");
                                    continue;
                                }

                                String queryText = null;
                                for (int i = 0; i < queryArray.size(); i++) {
                                    JsonObject obj = queryArray.get(i).getAsJsonObject();
                                    if (queryId.equals(obj.get("id").getAsString())) {
                                        queryText = obj.get("value").getAsString();
                                        break;
                                    }
                                }

                                if (queryText != null) {
                                    logger.debug("PATTERN_1 - resolved queryText='{}' for token={}", queryText, rpcQuery);
                                    finalRemarks
                                            .append(customerType)
                                            .append(" - ")
                                            .append(queryText)
                                            .append(",\n");
                                } else {
                                    logger.debug("PATTERN_1 - no queryText found for queryId={} in RPCMaster id={}", queryId, stageId);
                                    finalRemarks.append(rpcQuery).append(",\n");
                                }
                                break;

                            case PATTERN_2: // 1_A_oth?%$%$%
                                String[] othParts = rpcQuery.split("_oth\\?");
                                logger.debug("PATTERN_2 - othParts length={} for token={}", othParts.length, rpcQuery);
                                if (othParts.length == 0) {
                                    logger.debug("PATTERN_2 - invalid token format: {}", rpcQuery);
                                    finalRemarks.append(rpcQuery).append(",\n");
                                    continue;
                                }
                                String stage = othParts[0].split("_")[0];
                                String cust = othParts[0].split("_").length > 1 && othParts[0].split("_")[1].equals("A")
                                        ? Constants.APPLICANT : Constants.COAPPLICANT;
                                String rawText = othParts.length > 1 ? othParts[1] : "";
                                logger.debug("PATTERN_2 - stage={}, cust={}, rawText={}", stage, cust, rawText);

                                RPCMaster m2 = null;
                                for (RPCMaster m : rpcMasters) {
                                    if (m != null && m.getId() != null && m.getId().getId() != null &&
                                            m.getId().getId().compareTo(new BigDecimal(stage)) == 0) {
                                        m2 = m;
                                        break;
                                    }
                                }

                                if (m2 != null) {
                                    logger.debug("PATTERN_2 - matched RPCMaster stageName={}", m2.getStageName());
                                    finalRemarks
                                            .append(cust)
                                            .append(" - ")
                                            .append(rawText)
                                            .append(",\n");
                                } else {
                                    logger.debug("PATTERN_2 - no RPCMaster match for stage={}", stage);
                                    finalRemarks.append(rpcQuery).append(",\n");
                                }
                                break;

                            case PATTERN_3:
                                String[] dbkitParts = rpcQuery.split("_");
                                logger.debug("PATTERN_3 - dbkitParts length={} for token={}", dbkitParts.length, rpcQuery);
                                if (dbkitParts.length < 3) {
                                    logger.debug("PATTERN_3 - invalid format for token={}", rpcQuery);
                                    finalRemarks.append(rpcQuery).append(",\n");
                                    continue;
                                }
                                String dbkitId = dbkitParts[0];
                                String dbKitQueryId = dbkitParts[1];
                                logger.debug("PATTERN_3 - dbkitId={}, dbKitQueryId={}", dbkitId, dbKitQueryId);

                                DBKITMaster matchingDbkitMaster = null;
                                for (DBKITMaster dbkitMaster : dbkitMasters) {
                                    if (dbkitMaster != null && dbkitMaster.getId() == Integer.parseInt(dbkitId)) {
                                        matchingDbkitMaster = dbkitMaster;
                                        break;
                                    }
                                }

                                if (matchingDbkitMaster == null) {
                                    logger.debug("PATTERN_3 - no matching DBKITMaster found for id={}", dbkitId);
                                    finalRemarks.append(rpcQuery).append(",\n");
                                    continue;
                                }
                                logger.debug("PATTERN_3 - matched DBKITMaster stageName={}", matchingDbkitMaster.getStageName());

                                if (dbKitQueryId.toUpperCase().startsWith("R")) {

                                    JsonObject rejectReasonsJson = new JsonObject();
                                    String rawRejectReasonJson = matchingDbkitMaster.getQueries();
                                    if (rawRejectReasonJson != null && !rawRejectReasonJson.trim().isEmpty()) {
                                        rejectReasonsJson = JsonParser.parseString(rawRejectReasonJson).getAsJsonObject();
                                    }
                                    logger.debug("PATTERN_3 - rejectReasonsJson : {}", rejectReasonsJson);

                                    JsonArray rejectReasonArray = rejectReasonsJson.has("rejectReason")
                                            ? rejectReasonsJson.getAsJsonArray("rejectReason")
                                            : null;
                                    logger.debug("PATTERN_3 - rejectReasonArray {}", rejectReasonArray);

                                    if (rejectReasonArray == null) {
                                        finalRemarks.append(rpcQuery).append(",\n");
                                        continue;
                                    }
                                    String rejectReasonText = null;
                                    for (int i = 0; i < rejectReasonArray.size(); i++) {
                                        JsonObject obj = rejectReasonArray.get(i).getAsJsonObject();
                                        if (dbKitQueryId.equals(obj.get("id").getAsString())) {
                                            rejectReasonText = obj.get("value").getAsString();
                                            break;
                                        }
                                    }
                                    if (rejectReasonText != null) {
                                        logger.debug("PATTERN_3 - resolved rejectReasonText='{}' for id={}", rejectReasonText, dbKitQueryId);
                                        finalRemarks
                                                .append("Reject Reason")
                                                .append(" - ")
                                                .append(rejectReasonText).append(",\n");
                                    } else {
                                        logger.debug("PATTERN_3 - no rejectReasonText found for id={}", dbKitQueryId);
                                        finalRemarks.append(rpcQuery).append(",\n");
                                    }
                                } else if (dbKitQueryId.toUpperCase().startsWith("Q")) {
                                    //USING this mapped variable because, in the frontend they are using the same table for DBKit generation and
                                    //RPC Dbkit verification. So, to avoid confusion we are mapping the stage ids.
                                    Map<Integer, Integer> rpcDBKITStageIdMapping = new HashMap<>();
                                    rpcDBKITStageIdMapping.put(1, 8);
                                    rpcDBKITStageIdMapping.put(2, 9);
                                    rpcDBKITStageIdMapping.put(3, 10);

                                    Integer mappedStageId = rpcDBKITStageIdMapping.get(Integer.valueOf(dbkitId));
                                     for (DBKITMaster dbkitMaster : dbkitMasters) {
                                        if (dbkitMaster != null && dbkitMaster.getId() == mappedStageId) {
                                            matchingDbkitMaster = dbkitMaster;
                                            break;
                                        }
                                    }

                                    JsonObject dbkitQueryJson = new JsonObject();
                                    String rawDbkitQueryJson = matchingDbkitMaster.getQueries();
                                    if (rawDbkitQueryJson != null && !rawDbkitQueryJson.trim().isEmpty()) {
                                        dbkitQueryJson = JsonParser.parseString(rawDbkitQueryJson).getAsJsonObject();
                                    }
                                    logger.debug("PATTERN_3 - dbkitQueryJson : {}", dbkitQueryJson);

                                    JsonArray dbkitQueryArray = dbkitQueryJson.has("sendbackReason")
                                            ? dbkitQueryJson.getAsJsonArray("sendbackReason")
                                            : null;
                                    logger.debug("PATTERN_3 - dbkitQueryArray {}", dbkitQueryArray);

                                    if (dbkitQueryArray == null) {
                                        finalRemarks.append(rpcQuery).append(",\n");
                                        continue;
                                    }
                                    String dbkitQueryText = null;
                                    for (int i = 0; i < dbkitQueryArray.size(); i++) {
                                        JsonObject obj = dbkitQueryArray.get(i).getAsJsonObject();
                                        if (dbKitQueryId.equals(obj.get("id").getAsString())) {
                                            dbkitQueryText = obj.get("value").getAsString();
                                            break;
                                        }
                                    }

                                    if (dbkitQueryText != null) {
                                        logger.debug("PATTERN_3 - resolved dbkitQueryText='{}' for id={}", dbkitQueryText, dbKitQueryId);
                                        finalRemarks
                                                .append("Query")
                                                .append(" - ")
                                                .append(dbkitQueryText).append(",\n");
                                    } else {
                                        logger.debug("PATTERN_3 - no dbkitQueryText found for id={}", dbKitQueryId);
                                        finalRemarks.append(rpcQuery).append(",\n");
                                    }
                                }
                                break;
                            case PATTERN_4:
                                String[] dbkitOthParts = rpcQuery.split("_oth\\?");
                                logger.debug("PATTERN_4 - dbkitOthParts length={} for token={}", dbkitOthParts.length, rpcQuery);
                                if (dbkitOthParts.length < 2) {
                                    logger.debug("PATTERN_4 - invalid format for token={}", rpcQuery);
                                    finalRemarks.append(rpcQuery).append(",\n");
                                    continue;
                                }
                                String dbkitStage = dbkitOthParts[0].split("_")[0];
                                String[] rawParts = dbkitOthParts[1].split("_");

                                if (rawParts.length < 2) {
                                    logger.debug("PATTERN_4 - rawParts length < 2 for token={}", rpcQuery);
                                    finalRemarks.append(rpcQuery).append(",\n");
                                    continue;
                                }
                                String dbKitRawText = rawParts[0];
                                logger.debug("PATTERN_4 - dbkitStage={}, dbKitRawText={}", dbkitStage, dbKitRawText);

                                DBKITMaster dbkitMaster2 = null;
                                for (DBKITMaster dbkitMaster : dbkitMasters) {
                                    if (dbkitMaster != null && dbkitMaster.getId() == Integer.parseInt(dbkitStage)) {
                                        dbkitMaster2 = dbkitMaster;
                                        break;
                                    }
                                }

                                if (dbkitMaster2 != null) {
                                    logger.debug("PATTERN_4 - matched DBKITMaster stageName={}", dbkitMaster2.getStageName());
                                    finalRemarks
                                            .append(dbKitRawText)
                                            .append(",\n");
                                } else {
                                    logger.debug("PATTERN_4 - no DBKITMaster match for stage={}", dbkitStage);
                                    finalRemarks.append(rpcQuery).append(",\n");
                                }

                                break;

                            default:
                                // Unknown pattern
                                logger.error("Unknown pattern for RPC Query: {}", rpcQuery);
                                logger.warn("Unknown pattern for RPC Query: {} — preserving as-is", rpcQuery);
                                finalRemarks
                                        .append(rpcQuery)
                                        .append(",\n");
                                break;
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error in paintSendbackQueryForProductivityReport: {}", e.getMessage(), e);
                }

                // Remove trailing comma+space
                String result = finalRemarks.toString().replaceAll(", $", "");
                logger.debug("Setting final remarks for payloadIndex={} resultPresent? {} result='{}'", idx, StringUtils.isNotBlank(result), result);
                reportPayload.setRemarks(result);
            }

            logger.debug("paintSendbackQueryForProductivityReport - completed processing all payloads");
    }


    public String classifyPattern(String token) {

        if (RPC_QUERY_REGEX_PATTERN_1.matcher(token).matches()) {
            return PATTERN_1;
        }

        if (RPC_OTHER_QUERY_REGEX_PATTERN_2.matcher(token).matches()) {
            return PATTERN_2;
        }

        if (DBKIT_VERIFICATION_QUERY_REGEX_PATTERN_3.matcher(token).matches()) {
            return PATTERN_3;
        }

        if (DBKIT_VERIFICATION_OTHER_QUERY_REGEX_PATTERN_4.matcher(token).matches()) {
            return PATTERN_4;
        }

        return "UNKNOWN";
    }

    private void getUserDetailsForRPCProductivityReport(List<RpcProductivityReportPayload> productivityReportData) {
        List<String> userIds =
                productivityReportData.stream()
                        .map(RpcProductivityReportPayload::getUserId)
                        .distinct()
                        .collect(Collectors.toList());
        List<TbAsmiUser.UserSummary> userSummaries =tbUserRepository.findUserNamesByUserIdList(userIds);

        Map<String, TbAsmiUser.UserSummary> userMap =
                userSummaries.stream()
                        .collect(Collectors.toMap(
                                TbAsmiUser.UserSummary::getUserId,
                                s -> s
                        ));

        for(RpcProductivityReportPayload payload : productivityReportData){
            TbAsmiUser.UserSummary matchingUser = userMap.get(payload.getUserId());
            if(matchingUser !=  null){
                payload.setRpcStaffName(matchingUser.getUserName());
                payload.setRpcName(matchingUser.getAddInfo3());
            }
        }

    }

    private void getRPCDetailsForMISReport(List<RpcMISReportPayload> misReportPayloads){
        List<String> branchIds = misReportPayloads.stream().map(RpcMISReportPayload::getQBranchId).distinct().collect(Collectors.toList());

        List<BranchAreaMappingDetails> branchAreaMappingDetails = tatBranchDetailsRepository.findByBranchList(branchIds);

        Map<String, BranchAreaMappingDetails> branchMap =
                branchAreaMappingDetails.stream()
                        .collect(Collectors.toMap(
                                BranchAreaMappingDetails::getBranchId,
                                s -> s
                        ));

        for(RpcMISReportPayload payload : misReportPayloads){
            BranchAreaMappingDetails matchingBranch = branchMap.get(payload.getQBranchId());
            if(matchingBranch !=  null){
                payload.setRpcName(matchingBranch.getRegionName());
            }
        }
    }

}
