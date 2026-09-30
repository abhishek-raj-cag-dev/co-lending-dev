package com.iexceed.appzillonbanking.cob.service;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.payload.Response;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cob.core.repository.ab.ApplicationMasterRepository2;
import com.iexceed.appzillonbanking.cob.core.utils.AppStatus;
import com.iexceed.appzillonbanking.cob.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;
import com.iexceed.appzillonbanking.cob.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cob.loans.domain.user.BranchAreaMappingDetails;
import com.iexceed.appzillonbanking.cob.loans.repository.user.TATBranchDetailsRepository;
import com.iexceed.appzillonbanking.cob.payload.FetchRoleRequest;
import com.iexceed.appzillonbanking.cob.payload.FetchRoleRequestFields;
import com.iexceed.appzillonbanking.cob.payload.MasterSearchQueryDTO;
import com.iexceed.appzillonbanking.cob.payload.MasterSearchRequestWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final Logger logger = LogManager.getLogger(DashboardService.class);

    private final ApplicationMasterRepository applicationMasterRepository;
    private final ApplicationMasterRepository2 applicationMasterRepository2;
    private final TATBranchDetailsRepository tatBranchDetailsRepository;

    public DashboardService(ApplicationMasterRepository applicationMasterRepository, ApplicationMasterRepository2 applicationMasterRepository2,
                            TATBranchDetailsRepository tatBranchDetailsRepository) {
        this.applicationMasterRepository = applicationMasterRepository;
        this.applicationMasterRepository2 = applicationMasterRepository2;
        this.tatBranchDetailsRepository = tatBranchDetailsRepository;
    }

    //Commented until CR is removed from HOLD

//    public Response filterDashboardApplications(FetchRoleRequest apiRequest, Properties prop) {
//        Gson gson = new Gson();
//        Response response = new Response();
//        ResponseHeader responseHeader = new ResponseHeader();
//        ResponseBody responseBody = new ResponseBody();
//        FetchRoleRequestFields requestObj = apiRequest.getRequestObj();
//        logger.debug("Request obj: {}", requestObj);
//        String filterType = requestObj.getFilterType();
//        int numOfRecords = Integer.parseInt(prop.getProperty(CobFlagsProperties.NUM_OF_REC_IN_WIDGET.getKey()));
//        int pageNo = requestObj.getPageNo();
//        int currentPage = Math.max(0, pageNo - 1);
//        Pageable page = PageRequest.of(currentPage, numOfRecords);
//        Page<ApplicationMaster> filteredApplications = Page.empty(page);
//
//        String responseCode = ResponseCodes.SUCCESS.getKey();
//        String responseMessage = ResponseCodes.SUCCESS.getValue();
//
//        try {
//            switch (filterType.toUpperCase()) {
//                case Constants.PRODUCT:
//                    break;
//                case Constants.STATE:
//
//                    break;
//                case Constants.ZONE:
//                    break;
//                case Constants.REGION:
//                    break;
//                case Constants.AREA:
//                    break;
//                case Constants.BRANCH:
//                    break;
//                case Constants.KENDRA:
//                    break;
//                case Constants.MEETING_DAY:
//                    break;
//                default:
//                    logger.error("Invalid filterType: {}", filterType);
//                    break;
//            }
//
//        } catch (Exception e) {
//            logger.error("Error while extracting filtered applications, with error: {}", e.getMessage(), e);
//            responseCode = ResponseCodes.FAILURE.getKey();
//            responseMessage = ResponseCodes.FAILURE.getValue();
//        }
//        responseBody.setResponseObj(gson.toJson(filteredApplications));
//        responseHeader.setResponseCode(responseCode);
//        responseHeader.setResponseMessage(responseMessage);
//        response.setResponseBody(responseBody);
//        response.setResponseHeader(responseHeader);
//        return response;
//    }


//    private Page<ApplicationMaster> fetchApplicationsByState(FetchRoleRequestFields requestObj, Properties prop, Pageable page) {
//        Page<ApplicationMaster> filteredApplications = Page.empty(page);
//        List<String> states = requestObj.getFilterList();
//        List<Integer> statesInt = states.stream()
//                .map(Integer::parseInt)
//                .collect(Collectors.toList());
//        List<BranchAreaMappingDetails> stateBranches = tatBranchDetailsRepository.findBranchIdDetailsByStateIdList(statesInt);
//        List<String> branchIds = stateBranches.stream()
//                .map(BranchAreaMappingDetails::getBranchId)
//                .collect(Collectors.toList());
//
//
//        return filteredApplications;
//    }

    public Response dashboardMasterSearch(MasterSearchRequestWrapper.MasterSearchRequest apiRequest) {
        logger.debug("Entered into dashboardMasterSearch method with request: {}", apiRequest);
        Gson gson = new Gson();
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        MasterSearchRequestWrapper.MasterSearchRequestFields requestFields = apiRequest.getRequestObj();
        logger.debug("dashboardMasterSearch Request obj: {}", requestFields);
        String searchValue = requestFields.getSearchValue();
        logger.debug("Search value: {}", searchValue);
        String responseCode = ResponseCodes.SUCCESS.getKey();
        String responseMessage = ResponseCodes.SUCCESS.getValue();
        String paddedSearchValue = searchValue != null ? StringUtils.leftPad(searchValue, 8, '0') : "00000000";
        String searchValWorkItemNo = "Unnati-" + paddedSearchValue + "-Process";
        try {
            List<MasterSearchQueryDTO> masterSearchApplications = applicationMasterRepository2.masterSearchApplications(searchValue, searchValue, searchValWorkItemNo);
            if (!masterSearchApplications.isEmpty()) {
                masterSearchApplications.forEach(app -> {
                    String currentStatus = app.getCurrentStatus();
                    if (currentStatus != null && !currentStatus.isEmpty()) {
                        app.setCurrentStage(AppStatus.getStageDescriptionByValue(currentStatus));
                    }
                });
            }
            responseBody.setResponseObj(gson.toJson(masterSearchApplications));

        } catch (Exception e) {
            logger.error("Error while extracting searched applications, with error: {}", e.getMessage(), e);
            responseCode = ResponseCodes.FAILURE.getKey();
            responseMessage = ResponseCodes.FAILURE.getValue();
        }

        responseHeader.setResponseCode(responseCode);
        responseHeader.setResponseMessage(responseMessage);
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }
}
