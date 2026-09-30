package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationMasterId;
import com.iexceed.appzillonbanking.cob.core.utils.Constants;

@Repository
@Transactional(transactionManager = "abCOBTransactionManager")
public interface ApplicationMasterRepository
		extends JpaRepository<ApplicationMaster, ApplicationMasterId> {
	@Transactional
	public void deleteByApplicationIdAndAppId(String applicationId, String appId);

	public Optional<ApplicationMaster> findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(String appId,
			String applicationId, int versionNum, String applicationStatus);
	
	public Optional<ApplicationMaster> findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusIn(String appId,
			String applicationId, int versionNum, List<String> dbFeaturesList);
	
	public List<ApplicationMaster> findByAppIdAndApplicationId(String appId, String applicationId);

	public Optional<ApplicationMaster> findByAppIdAndWorkitemNo(String appId, String workitemNo);
	
	public Optional<ApplicationMaster> findByAppIdAndMemberId(String appId, String memberId);

	public Optional<ApplicationMaster> findByAppIdAndApplicationIdAndVersionNum(String appId, String applicationId,
			int versionNum);
	
	@Query(value="Select new ApplicationMaster(appMaster.appId, appMaster.applicationId, appMaster.versionNum, appMaster.applicationDate, "
			+ "appMaster.createTs,appMaster.createdBy, appMaster.applicationType, appMaster.kycType, appMaster.applicationStatus, appMaster.customerId, appMaster.mobileNumber, "
			+ "appMaster.nationalId, appMaster.pan, appMaster.productGroupCode, appMaster.productCode, appMaster.searchCode1, appMaster.searchCode2, appMaster.assignedTo, "
			+ "appMaster.emailId, appMaster.currentStage, appMaster.declarationFlag, appMaster.accNumber, appMaster.mobileVerStatus, appMaster.emailVerStatus, "
			+ "appMaster.currentScreenId, appMaster.remarks, appMaster.relatedApplicationId) FROM ApplicationMaster appMaster WHERE appMaster.appId=:appId and "
	  		+ "(:mobileNumber is null or appMaster.mobileNumber=:mobileNumber) and "
	  		+ "(:nationalId is null or appMaster.nationalId=:nationalId) and "
			+ "(:pan is null or appMaster.pan=:pan) and " + "(:emailId is null or appMaster.emailId=:emailId) and "
	  		+ "(:productGroupCode is null or appMaster.productGroupCode=:productGroupCode) and "
	  		+ "(:branchCode is null or appMaster.searchCode1=:branchCode) and "
	  		+ "(appMaster.applicationStatus in :statusList) and "
	  		+ "(appMaster.versionNum=(select max(appMaster2.versionNum) from ApplicationMaster appMaster2 "
	  		+ "where appMaster2.appId=:appId and (:mobileNumber is null or appMaster2.mobileNumber=:mobileNumber) and "
	  		+ "(:nationalId is null or appMaster2.nationalId=:nationalId) and "
			+ "(:pan is null or appMaster2.pan=:pan) and " + "(:emailId is null or appMaster2.emailId=:emailId) and "
	  		+ "(:productGroupCode is null or appMaster2.productGroupCode=:productGroupCode) and "
	  		+ "(:branchCode is null or appMaster2.searchCode1=:branchCode) and "
	  		+ "(appMaster2.applicationStatus in :statusList)))", nativeQuery = false)
	public List<ApplicationMaster> findDataCasa(@Param("appId") String appId,
			@Param("mobileNumber") String mobileNumber, @Param("nationalId") String nationalId,
			@Param("pan") String pan, @Param("emailId") String emailId,
			@Param("productGroupCode") String productGroupCode, @Param("statusList") List<String> statusList,
			@Param("branchCode") String branchCode);	

	@Query(value ="select new ApplicationMaster(appMaster.versionNum, appMaster.applicationId, appMaster.applicationStatus, "
			+ "appMaster.createTs, appMaster.createdBy, appMaster.productCode, appMaster.productGroupCode, appMaster.applicationType, appMaster.mobileNumber, appMaster.appId) FROM "
			+ "ApplicationMaster " + "appMaster where "
			+ "appMaster.searchCode1=:branchCode and appMaster.applicationStatus in :featuresList AND "
			+ "(appMaster.mobileNumber like :mobileNum OR appMaster.applicationId=:applicationId) AND "
			+ "appMaster.versionNum=(select max(appMaster2.versionNum) from ApplicationMaster appMaster2 where "
			+ "appMaster.applicationId = appMaster2.applicationId and appMaster2.searchCode1=:branchCode and "
			+ "(appMaster2.mobileNumber like :mobileNum OR appMaster2.applicationId=:applicationId)) "
			+ "order by appMaster.applicationStatus", nativeQuery = false)
	public List<ApplicationMaster> searchApplications(@Param("featuresList") List<String> featuresList, 
			@Param("mobileNum") String mobileNum, @Param("applicationId") String applicationId,
													  @Param("branchCode") String branchCode);
	
	public Optional<ApplicationMaster> findByAppIdAndApplicationIdAndApplicationStatus(String appId,
			String applicationID, String status);

	public Optional<ApplicationMaster> findTopByAppIdAndApplicationIdOrderByVersionNumDesc(String appId,
			String applicationID);

	@Query(value ="select new ApplicationMaster(appMaster1.applicationId, appMaster1.versionNum, appMaster1.applicationStatus, "
			+ "appMaster1.createTs, appMaster1.createdBy, appMaster1.mobileNumber, appMaster1.applicationDate, appMaster1.kycType, appMaster1.pan, appMaster1.nationalId, "
			+ "appMaster1.productCode, appMaster1.customerId, appMaster1.emailId, appMaster1.searchCode1, appMaster1.accNumber, appMaster1.productGroupCode, "
			+ "appMaster1.appId, applnWf1.applicationStatus, applnWf1.createdBy, applnWf1.createTs, appMaster1.applicantsCount, appMaster1.applicationType, appMaster1.workitemNo, appMaster1.memberId) FROM "
			+ "ApplicationMaster appMaster1 " + "left outer join " + "ApplicationWorkflow applnWf1 "
			+ "on appMaster1.applicationId=applnWf1.applicationId and appMaster1.appId=applnWf1.appId and appMaster1.versionNum=applnWf1.versionNum "
			+ "where " + "appMaster1.searchCode1=:branchCode AND "
			+ "appMaster1.applicationStatus =:status AND (appMaster1.applicationDate between :fromDay and :toDay) "
			+ "AND ((appMaster1.productGroupCode=:casaProductGrp AND (appMaster1.relatedApplicationId is null OR appMaster1.relatedApplicationId='')) OR (appMaster1.productGroupCode !=:casaProductGrp))"
			+ "AND appMaster1.versionNum=(select max(appMaster2.versionNum) from "
			+ "ApplicationMaster appMaster2 where " + "appMaster1.applicationId = appMaster2.applicationId) and "
			+ "((applnWf1.applicationId is null OR applnWf1.applicationId='') or applnWf1.workflowSeqNum=(select max(applnWf2.workflowSeqNum) from "
			+ "ApplicationWorkflow applnWf2 " 
			+ "where applnWf2.applicationId=appMaster1.applicationId and applnWf2.appId=appMaster1.appId and applnWf2.versionNum=(select max(appMaster3.versionNum) from " 
			+ "ApplicationMaster appMaster3 where " + "appMaster1.applicationId = appMaster3.applicationId)))"
			+ "order by appMaster1.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchDashBoardData(String status, String branchCode, LocalDate fromDay,
			LocalDate toDay, Pageable page, String casaProductGrp);
	
	@Query(value ="select new ApplicationMaster(appMaster1.applicationId, appMaster1.versionNum, appMaster1.applicationStatus, "
			+ "appMaster1.createTs, appMaster1.createdBy, appMaster1.mobileNumber, appMaster1.applicationDate, appMaster1.kycType, appMaster1.pan, appMaster1.nationalId, "
			+ "appMaster1.productCode, appMaster1.customerId, appMaster1.emailId, appMaster1.searchCode1, appMaster1.accNumber, appMaster1.productGroupCode, "
			+ "appMaster1.appId, applnWf1.applicationStatus, applnWf1.createdBy, applnWf1.createTs, appMaster1.applicantsCount, appMaster1.applicationType, "
			+ "appMaster1.workitemNo, appMaster1.memberId, custdtls.customerName, appMaster1.updatedBy,'',appMaster1.branchName, loanDtls.loanAmount) FROM "
			+ "ApplicationMaster appMaster1 " + "left outer join " + "ApplicationWorkflow applnWf1 "
			+ "on appMaster1.applicationId=applnWf1.applicationId and appMaster1.appId=applnWf1.appId and appMaster1.versionNum=applnWf1.versionNum "
			+ "left outer join CustomerDetails custdtls on appMaster1.applicationId=custdtls.applicationId and custdtls.customerType = 'Applicant' "
			+"left outer join LoanDetails loanDtls on appMaster1.applicationId = loanDtls.applicationId "
			+ "where " + "appMaster1.searchCode1 in :kendraIds AND "
			+ "appMaster1.applicationStatus =:status AND (appMaster1.applicationDate between :fromDay and :toDay) "
			+ "AND (appMaster1.relatedApplicationId is null OR appMaster1.relatedApplicationId='') "
			+ "AND applnWf1.workflowSeqNum=(select max(applnWf2.workflowSeqNum) from " + "ApplicationWorkflow applnWf2 "
			+ "where applnWf2.applicationId=appMaster1.applicationId and applnWf2.appId=appMaster1.appId) "
			+ "order by appMaster1.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchDashBoardData(String status, List<String> kendraIds, LocalDate fromDay,
			LocalDate toDay, Pageable page);

	@Query(value ="select new ApplicationMaster(appMaster1.applicationId, appMaster1.versionNum, appMaster1.applicationStatus, "
			+ "appMaster1.createTs, appMaster1.createdBy, appMaster1.mobileNumber, appMaster1.applicationDate, appMaster1.kycType, appMaster1.pan, appMaster1.nationalId, "
			+ "appMaster1.productCode, appMaster1.customerId, appMaster1.emailId, appMaster1.searchCode1, appMaster1.accNumber, appMaster1.productGroupCode, "
			+ "appMaster1.appId, applnWf1.applicationStatus, applnWf1.createdBy, applnWf1.createTs, appMaster1.applicantsCount, appMaster1.applicationType, "
			+ "appMaster1.workitemNo, appMaster1.memberId, custdtls.customerName, appMaster1.updatedBy,'',appMaster1.branchName, loanDtls.loanAmount) FROM "
			+ "ApplicationMaster appMaster1 " + "left outer join " + "ApplicationWorkflow applnWf1 "
			+ "on appMaster1.applicationId=applnWf1.applicationId and appMaster1.appId=applnWf1.appId and appMaster1.versionNum=applnWf1.versionNum "
			+ "left outer join CustomerDetails custdtls on appMaster1.applicationId=custdtls.applicationId and custdtls.customerType = 'Applicant' "
			+"left outer join LoanDetails loanDtls on appMaster1.applicationId = loanDtls.applicationId "
			+ "where " + "appMaster1.searchCode1 in :kendraIds AND "
			+ "appMaster1.applicationStatus =:status AND (appMaster1.applicationDate between :fromDay and :toDay) "
			+ "AND (appMaster1.relatedApplicationId is not null) "
			+ "AND applnWf1.workflowSeqNum=(select max(applnWf2.workflowSeqNum) from " + "ApplicationWorkflow applnWf2 "
			+ "where applnWf2.applicationId=appMaster1.applicationId and applnWf2.appId=appMaster1.appId) "
			+ "order by appMaster1.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchRenewalDashBoardData(String status, List<String> kendraIds, LocalDate fromDay,
			LocalDate toDay, Pageable page);
	
	@Query(value ="select new ApplicationMaster(appMaster1.applicationId, appMaster1.versionNum, appMaster1.applicationStatus, "
			+ "appMaster1.createTs, appMaster1.createdBy, appMaster1.mobileNumber, appMaster1.applicationDate, appMaster1.kycType, appMaster1.pan, appMaster1.nationalId, "
			+ "appMaster1.productCode, appMaster1.customerId, appMaster1.emailId, appMaster1.searchCode1, appMaster1.accNumber, appMaster1.productGroupCode, "
			+ "appMaster1.appId, applnWf1.applicationStatus, applnWf1.createdBy, applnWf1.createTs, appMaster1.applicantsCount, appMaster1.applicationType, appMaster1.workitemNo, appMaster1.memberId) FROM "
			+ "ApplicationMaster appMaster1 " + "left outer join " + "ApplicationWorkflow applnWf1 "
			+ "on appMaster1.applicationId=applnWf1.applicationId and appMaster1.appId=applnWf1.appId and appMaster1.versionNum=applnWf1.versionNum "
			+ "where " + "appMaster1.searchCode1 in :kendraIds AND " + "appMaster1.relatedApplicationId is null "
			+ "and applnWf1.workflowSeqNum=(select max(applnWf2.workflowSeqNum) from " + "ApplicationWorkflow applnWf2 "
			+ "where applnWf2.applicationId=appMaster1.applicationId and applnWf2.appId=appMaster1.appId) "
			+ "order by appMaster1.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchDashBoardData(List<String> kendraIds);
	
	@Query(value ="select new ApplicationMaster(appMaster1.applicationId, appMaster1.versionNum, appMaster1.applicationStatus, "
			+ "appMaster1.createTs, appMaster1.createdBy, appMaster1.mobileNumber, appMaster1.applicationDate, appMaster1.kycType, appMaster1.pan, appMaster1.nationalId, "
			+ "appMaster1.productCode, appMaster1.customerId, appMaster1.emailId, appMaster1.searchCode1, appMaster1.accNumber, appMaster1.productGroupCode, "
			+ "appMaster1.appId, applnWf1.applicationStatus, applnWf1.createdBy, applnWf1.createTs, appMaster1.applicantsCount, appMaster1.applicationType, appMaster1.workitemNo, appMaster1.memberId) FROM "
			+ "ApplicationMaster appMaster1 " + "left outer join " + "ApplicationWorkflow applnWf1 "
			+ "on appMaster1.applicationId=applnWf1.applicationId and appMaster1.appId=applnWf1.appId and appMaster1.versionNum=applnWf1.versionNum "
			+ "where " + "appMaster1.searchCode1 in :kendraIds AND " + "appMaster1.relatedApplicationId is not null "
			+ "AND applnWf1.workflowSeqNum=(select max(applnWf2.workflowSeqNum) from " + "ApplicationWorkflow applnWf2 "
			+ "where applnWf2.applicationId=appMaster1.applicationId and applnWf2.appId=appMaster1.appId) "
			+ "order by appMaster1.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchRenewalDashBoardData(List<String> kendraIds);
		
	@Query(value ="select new ApplicationMaster(appMaster1.applicationId, appMaster1.versionNum, appMaster1.applicationStatus, "
			+ "appMaster1.createTs, appMaster1.createdBy, appMaster1.mobileNumber, appMaster1.applicationDate, appMaster1.kycType, appMaster1.pan, appMaster1.nationalId, "
			+ "appMaster1.productCode, appMaster1.customerId, appMaster1.emailId, appMaster1.searchCode1, appMaster1.accNumber, appMaster1.productGroupCode, "
			+ "appMaster1.appId, applnWf1.applicationStatus, applnWf1.createdBy, applnWf1.createTs, appMaster1.applicantsCount, appMaster1.applicationType, appMaster1.workitemNo, appMaster1.memberId) FROM "
			+ "ApplicationMaster appMaster1 " + "left outer join " + "ApplicationWorkflow applnWf1 "
			+ "on appMaster1.applicationId=applnWf1.applicationId and appMaster1.appId=applnWf1.appId and appMaster1.versionNum=applnWf1.versionNum "
			+ "where " + "appMaster1.searchCode1=:branchCode AND "
			+ "appMaster1.applicationStatus=:status AND (appMaster1.applicationDate between :fromDay and :toDay) "
			+ "AND ((appMaster1.productGroupCode=:casaProductGrp AND (appMaster1.relatedApplicationId is null OR appMaster1.relatedApplicationId='')) OR (appMaster1.productGroupCode !=:casaProductGrp))"
			+ "AND appMaster1.versionNum=(select max(appMaster2.versionNum) from "
			+ "ApplicationMaster appMaster2 where " + "appMaster1.applicationId = appMaster2.applicationId) and "
			+ "((applnWf1.applicationId is null OR applnWf1.applicationId='') or applnWf1.workflowSeqNum=(select max(applnWf2.workflowSeqNum) from "
			+ "ApplicationWorkflow applnWf2 " 
			+ "where applnWf2.applicationId=appMaster1.applicationId and applnWf2.appId=appMaster1.appId and applnWf2.versionNum=(select max(appMaster3.versionNum) from " 
			+ "ApplicationMaster appMaster3 where " + "appMaster1.applicationId = appMaster3.applicationId)))"
			+ "order by appMaster1.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchDashBoardData(@Param("status") String status, 
			@Param("branchCode") String branchCode, @Param("fromDay") LocalDate fromDay,
			@Param("toDay") LocalDate toDay, @Param("casaProductGrp") String casaProductGrp);
	
	@Query(value = "select new ApplicationMaster(appMaster.applicationId, appMaster.createdBy, appMaster.applicationDate, "
			+ "appMaster.kendraId,appMaster.kendraName, appMaster.workitemNo, appMaster.memberId, custdtls.customerName, appMaster.currentStageNo,'','','',appMaster.applicationStatus) FROM "
			+ "ApplicationMaster appMaster "
			+ "left outer join CustomerDetails custdtls on appMaster.applicationId=custdtls.applicationId and custdtls.customerType = 'Applicant' "
			+ "where " + "appMaster.kendraId in :kendraIds AND " + "appMaster.applicationStatus =:status "
			+ "AND (appMaster.relatedApplicationId is null OR appMaster.relatedApplicationId = '') "
			+ "and custdtls.customerType = 'Applicant' " + "order by appMaster.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchPendingDashBoardData(@Param("status") String status,
			@Param("kendraIds") List<String> kendraIds);
	
	@Query(value = "select new ApplicationMaster(appMaster.applicationId, appMaster.createdBy, appMaster.applicationDate, "
			+ "appMaster.kendraId,appMaster.kendraName, appMaster.workitemNo, appMaster.memberId, custdtls.customerName, appMaster.currentStageNo,'','','',appMaster.applicationStatus) FROM "
			+ "ApplicationMaster appMaster "
			+ "left outer join CustomerDetails custdtls on appMaster.applicationId=custdtls.applicationId "
			/*
			 * +
			 * "left outer join CibilDetails cbdtls on custdtls.custDtlId=cbdtls.custDtlId "
			 */
			+ "where appMaster.kendraId in :kendraIds AND appMaster.applicationStatus =:status "
			+ "AND (appMaster.relatedApplicationId is null OR appMaster.relatedApplicationId = '') "
			+ "AND appMaster.currentStageNo in :stageNos " + "and custdtls.customerType = 'Applicant' "
			+ "order by appMaster.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchInprogressDashBoardData(@Param("status") String status,
			@Param("kendraIds") List<String> kendraIds, List<Integer> stageNos);
	
	@Query(value = "select new ApplicationMaster(appMaster.applicationId, appMaster.createdBy, appMaster.applicationDate, "
			+ "appMaster.kendraId,appMaster.kendraName, appMaster.workitemNo, appMaster.memberId, custdtls.customerName, appMaster.currentStageNo,'','','',appMaster.applicationStatus) FROM "
			+ "ApplicationMaster appMaster "
			+ "left outer join CustomerDetails custdtls on appMaster.applicationId=custdtls.applicationId "
			/*
			 * +
			 * "left outer join CibilDetails cbdtls on custdtls.custDtlId=cbdtls.custDtlId "
			 */
			+ "where appMaster.kendraId in :kendraIds AND appMaster.applicationStatus =:status "
			+ "AND (appMaster.relatedApplicationId is not null AND appMaster.relatedApplicationId!='') "
			+ "AND appMaster.currentStageNo in :stageNos " + "and custdtls.customerType = 'Applicant' "
			+ "order by appMaster.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchInprogressRenewalDashBoardData(@Param("status") String status,
			@Param("kendraIds") List<String> kendraIds, List<Integer> stageNos);
		
	@Query(value ="select new ApplicationMaster(appMaster1.applicationId, appMaster1.versionNum, appMaster1.applicationStatus, "
			+ "appMaster1.createTs, appMaster1.createdBy, appMaster1.mobileNumber, appMaster1.applicationDate, appMaster1.kycType, appMaster1.pan, appMaster1.nationalId, "
			+ "appMaster1.productCode, appMaster1.customerId, appMaster1.emailId, appMaster1.searchCode1, appMaster1.accNumber, appMaster1.productGroupCode, "
			+ "appMaster1.appId, applnWf1.applicationStatus, applnWf1.createdBy, applnWf1.createTs, appMaster1.applicantsCount, appMaster1.applicationType, appMaster1.workitemNo, appMaster1.memberId, custdtls.customerName, appMaster1.updatedBy,'',appMaster1.branchName) FROM "
			+ "ApplicationMaster appMaster1 " + "left outer join " + "ApplicationWorkflow applnWf1 "
			+ "on appMaster1.applicationId=applnWf1.applicationId and appMaster1.appId=applnWf1.appId and appMaster1.versionNum=applnWf1.versionNum "
			+ "left outer join CustomerDetails custdtls on appMaster1.applicationId=custdtls.applicationId and custdtls.customerType = 'Applicant' "
			+ "where " + "appMaster1.searchCode1 in :kendraIds AND "
			+ "appMaster1.applicationStatus =:status AND (appMaster1.applicationDate between :fromDay and :toDay) "
			+ "AND (appMaster1.relatedApplicationId is null OR appMaster1.relatedApplicationId='') "
			+ "AND applnWf1.workflowSeqNum=(select max(applnWf2.workflowSeqNum) from " + "ApplicationWorkflow applnWf2 "
			+ "where applnWf2.applicationId=appMaster1.applicationId and applnWf2.appId=appMaster1.appId) "
			+ "order by appMaster1.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchNewDashBoardData(@Param("status") String status, 
			@Param("kendraIds") List<String> kendraIds, @Param("fromDay") LocalDate fromDay,
													  @Param("toDay") LocalDate toDay);
	
	@Query(value ="select new ApplicationMaster(appMaster1.applicationId, appMaster1.versionNum, appMaster1.applicationStatus, "
			+ "appMaster1.createTs, appMaster1.createdBy, appMaster1.mobileNumber, appMaster1.applicationDate, appMaster1.kycType, appMaster1.pan, appMaster1.nationalId, "
			+ "appMaster1.productCode, appMaster1.customerId, appMaster1.emailId, appMaster1.searchCode1, appMaster1.accNumber, appMaster1.productGroupCode, "
			+ "appMaster1.appId, applnWf1.applicationStatus, applnWf1.createdBy, applnWf1.createTs, appMaster1.applicantsCount, appMaster1.applicationType, appMaster1.workitemNo, appMaster1.memberId, custdtls.customerName, appMaster1.updatedBy,'',appMaster1.branchName) FROM "
			+ "ApplicationMaster appMaster1 " + "left outer join " + "ApplicationWorkflow applnWf1 "
			+ "on appMaster1.applicationId=applnWf1.applicationId and appMaster1.appId=applnWf1.appId and appMaster1.versionNum=applnWf1.versionNum "
			+ "left outer join CustomerDetails custdtls on appMaster1.applicationId=custdtls.applicationId and custdtls.customerType = 'Applicant' "
			+ "where " + "appMaster1.searchCode1 in :kendraIds AND "
			+ "appMaster1.applicationStatus in (:status, 'CAPUSHBACK') AND (appMaster1.applicationDate between :fromDay and :toDay) "
			+ "AND (appMaster1.relatedApplicationId is null OR appMaster1.relatedApplicationId='') "
			+ "AND applnWf1.workflowSeqNum=(select max(applnWf2.workflowSeqNum) from " + "ApplicationWorkflow applnWf2 "
			+ "where applnWf2.applicationId=appMaster1.applicationId and applnWf2.appId=appMaster1.appId) "
			+ "order by appMaster1.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchNewDashBoardData1(@Param("status") String status,
			@Param("kendraIds") List<String> kendraIds, @Param("fromDay") LocalDate fromDay,
			@Param("toDay") LocalDate toDay);

	@Query(value = "select new ApplicationMaster(appMaster1.applicationId, appMaster1.versionNum, appMaster1.applicationStatus, "
			+ "appMaster1.createTs, appMaster1.createdBy, appMaster1.mobileNumber, appMaster1.applicationDate, appMaster1.kycType, appMaster1.pan, appMaster1.nationalId, "
			+ "appMaster1.productCode, appMaster1.customerId, appMaster1.emailId, appMaster1.searchCode1, appMaster1.accNumber, appMaster1.productGroupCode, "
			+ "appMaster1.appId, applnWf1.applicationStatus, applnWf1.createdBy, applnWf1.createTs, appMaster1.applicantsCount, appMaster1.applicationType, appMaster1.workitemNo, appMaster1.memberId, custdtls.customerName, appMaster1.updatedBy,'',appMaster1.branchName) FROM "
			+ "ApplicationMaster appMaster1 " + "left outer join " + "ApplicationWorkflow applnWf1 "
			+ "on appMaster1.applicationId=applnWf1.applicationId and appMaster1.appId=applnWf1.appId and appMaster1.versionNum=applnWf1.versionNum "
			+ "left outer join CustomerDetails custdtls on appMaster1.applicationId=custdtls.applicationId and custdtls.customerType = 'Applicant' "
			+ "where " + "appMaster1.searchCode1 in :kendraIds AND "
			+ "appMaster1.applicationStatus =:status AND (appMaster1.applicationDate between :fromDay and :toDay) "
			+ "AND (appMaster1.relatedApplicationId is not null) "
			+ "AND applnWf1.workflowSeqNum=(select max(applnWf2.workflowSeqNum) from " + "ApplicationWorkflow applnWf2 "
			+ "where applnWf2.applicationId=appMaster1.applicationId and applnWf2.appId=appMaster1.appId) "
			+ "order by appMaster1.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchRenewalDashBoardData(@Param("status") String status, 
			@Param("kendraIds") List<String> kendraIds, @Param("fromDay") LocalDate fromDay,
			@Param("toDay") LocalDate toDay);

			@Query(value = "select new ApplicationMaster(appMaster1.applicationId, appMaster1.versionNum, appMaster1.applicationStatus, "
			+ "appMaster1.createTs, appMaster1.createdBy, appMaster1.mobileNumber, appMaster1.applicationDate, appMaster1.kycType, appMaster1.pan, appMaster1.nationalId, "
			+ "appMaster1.productCode, appMaster1.customerId, appMaster1.emailId, appMaster1.searchCode1, appMaster1.accNumber, appMaster1.productGroupCode, "
			+ "appMaster1.appId, applnWf1.applicationStatus, applnWf1.createdBy, applnWf1.createTs, appMaster1.applicantsCount, appMaster1.applicationType, appMaster1.workitemNo, appMaster1.memberId, custdtls.customerName, appMaster1.updatedBy,'',appMaster1.branchName) FROM "
			+ "ApplicationMaster appMaster1 " + "left outer join " + "ApplicationWorkflow applnWf1 "
			+ "on appMaster1.applicationId=applnWf1.applicationId and appMaster1.appId=applnWf1.appId and appMaster1.versionNum=applnWf1.versionNum "
			+ "left outer join CustomerDetails custdtls on appMaster1.applicationId=custdtls.applicationId and custdtls.customerType = 'Applicant' "
			+ "where " + "appMaster1.searchCode1 in :kendraIds AND "
			+ "appMaster1.applicationStatus in (:status, 'CAPUSHBACK') AND (appMaster1.applicationDate between :fromDay and :toDay) "
			+ "AND (appMaster1.relatedApplicationId is not null) "
			+ "AND applnWf1.workflowSeqNum=(select max(applnWf2.workflowSeqNum) from " + "ApplicationWorkflow applnWf2 "
			+ "where applnWf2.applicationId=appMaster1.applicationId and applnWf2.appId=appMaster1.appId) "
			+ "order by appMaster1.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchRenewalDashBoardData1(@Param("status") String status,
			@Param("kendraIds") List<String> kendraIds, @Param("fromDay") LocalDate fromDay,
													  @Param("toDay") LocalDate toDay);
	
	@Query(value ="select new ApplicationMaster(appMaster1.applicationStatus, count(appMaster1.applicationStatus)) "
			+ "from ApplicationMaster appMaster1 where " + "appMaster1.applicationStatus in :statusList and "
			+ "(appMaster1.applicationDate between :fromDay and :toDay) and " + "appMaster1.searchCode1=:branchCode and"
			+ "(:productGroupCode is null or appMaster1.productGroupCode=:productGroupCode) and "
			+ "((appMaster1.productGroupCode=:casaProductGrp AND (appMaster1.relatedApplicationId is null OR appMaster1.relatedApplicationId='')) OR (appMaster1.productGroupCode !=:casaProductGrp)) and "
			+ "appMaster1.versionNum=(select max(appMaster2.versionNum) from ApplicationMaster appMaster2 where "
			+ "appMaster1.applicationId=appMaster2.applicationId "
			+ "and (appMaster2.applicationDate between :fromDay and :toDay) "
			+ "and appMaster2.searchCode1=:branchCode and "
			+ "(:productGroupCode is null or appMaster2.productGroupCode=:productGroupCode) and "
			+ "((appMaster2.productGroupCode=:casaProductGrp AND (appMaster2.relatedApplicationId is null OR appMaster2.relatedApplicationId='')) OR (appMaster2.productGroupCode !=:casaProductGrp)))"
			+ "group by appMaster1.applicationStatus", nativeQuery = false)
	public List<ApplicationMaster> fetchStatusReport(@Param("branchCode") String branchCode, 
			@Param("fromDay") LocalDate fromDay, @Param("toDay") LocalDate toDay,
			@Param("statusList") List<String> statusList, @Param("casaProductGrp") String casaProductGrp,
													 @Param("productGroupCode") String productGroupCode);
		
	@Query(value ="select new ApplicationMaster(appMaster1.applicationStatus, appMaster1.applicationId, applnWf1.applicationStatus, applnWf1.createdBy) "
			+ "from ApplicationMaster appMaster1 " + "left outer join " + "ApplicationWorkflow applnWf1 "
			+ "on appMaster1.applicationId=applnWf1.applicationId and appMaster1.appId=applnWf1.appId and appMaster1.versionNum=applnWf1.versionNum "
			+ "where " + "appMaster1.applicationStatus in :statusList and "
			+ "(appMaster1.applicationDate between :fromDay and :toDay) and " + "appMaster1.searchCode1=:branchCode and"
			+ "(:productGroupCode is null or appMaster1.productGroupCode=:productGroupCode) and "
			+ "((appMaster1.productGroupCode=:casaProductGrp AND (appMaster1.relatedApplicationId is null OR appMaster1.relatedApplicationId='')) OR (appMaster1.productGroupCode !=:casaProductGrp)) and "
			+ "appMaster1.versionNum=(select max(appMaster2.versionNum) from ApplicationMaster appMaster2 where "
			+ "appMaster1.applicationId=appMaster2.applicationId "
			+ "and (appMaster2.applicationDate between :fromDay and :toDay) "
			+ "and appMaster2.searchCode1=:branchCode and "
			+ "(:productGroupCode is null or appMaster2.productGroupCode=:productGroupCode) and "
			+ "((appMaster2.productGroupCode=:casaProductGrp AND (appMaster2.relatedApplicationId is null OR appMaster2.relatedApplicationId='')) OR (appMaster2.productGroupCode !=:casaProductGrp))) and "
			+ "((applnWf1.applicationId is null OR applnWf1.applicationId='') or "
			+ "applnWf1.workflowSeqNum=(select max(applnWf2.workflowSeqNum) from " + "ApplicationWorkflow applnWf2 "
			+ "where applnWf2.applicationId=appMaster1.applicationId and applnWf2.appId=appMaster1.appId and applnWf2.versionNum=(select max(appMaster3.versionNum) from "
			+ "ApplicationMaster appMaster3 where "
			+ "appMaster1.applicationId = appMaster3.applicationId)))", nativeQuery = false)
	public List<ApplicationMaster> fetchStatusReportNew(@Param("branchCode") String branchCode, 
			@Param("fromDay") LocalDate fromDay, @Param("toDay") LocalDate toDay,
			@Param("statusList") List<String> statusList, @Param("casaProductGrp") String casaProductGrp,
													 @Param("productGroupCode") String productGroupCode);
		
	@Query(value ="select new ApplicationMaster(count(appMaster1.productGroupCode), appMaster1.productGroupCode) "
			+ "from ApplicationMaster appMaster1 where " + "appMaster1.applicationStatus in :statusList and "
			+ "(appMaster1.applicationDate between :fromDay and :toDay) and " + "appMaster1.searchCode1=:branchCode and"
			+ "(:productGroupCode is null or appMaster1.productGroupCode=:productGroupCode) and "
			+ "((appMaster1.productGroupCode=:casaProductGrp AND (appMaster1.relatedApplicationId is null OR appMaster1.relatedApplicationId='')) OR (appMaster1.productGroupCode !=:casaProductGrp)) and "
			+ "appMaster1.versionNum=(select max(appMaster2.versionNum) from ApplicationMaster appMaster2 where "
			+ "appMaster1.applicationId=appMaster2.applicationId "
			+ "and (appMaster2.applicationDate between :fromDay and :toDay) "
			+ "and appMaster2.searchCode1=:branchCode and "
			+ "(:productGroupCode is null or appMaster2.productGroupCode=:productGroupCode) and "
			+ "((appMaster2.productGroupCode=:casaProductGrp AND (appMaster2.relatedApplicationId is null OR appMaster2.relatedApplicationId='')) OR (appMaster2.productGroupCode !=:casaProductGrp)))"
			+ "group by appMaster1.productGroupCode", nativeQuery = false)
	public List<ApplicationMaster> fetchStatusReportProduct(@Param("branchCode") String branchCode, 
			@Param("fromDay") LocalDate fromDay, @Param("toDay") LocalDate toDay,
			@Param("statusList") List<String> statusList, @Param("casaProductGrp") String casaProductGrp,
													 @Param("productGroupCode") String productGroupCode);
		
	@Query(value="Select new ApplicationMaster(appMaster.appId, appMaster.applicationId, appMaster.versionNum, appMaster.applicationStatus, appMaster.relatedApplicationId) "
			+ "FROM ApplicationMaster appMaster WHERE " + "appMaster.appId=:appId and "
			+ "(:customerId is null or appMaster.customerId=:customerId) and "
			+ "(:productGroupCode is null or appMaster.productGroupCode=:productGroupCode) and "
			+ "(appMaster.applicationStatus in :statusList)", nativeQuery = false)
	public List<ApplicationMaster> findData(@Param("appId") String appId, @Param("customerId") BigDecimal customerId,
			@Param("productGroupCode") String productGroupCode, @Param("statusList") List<String> statusList);

	public Optional<ApplicationMaster> findByAppIdAndApplicationIdAndVersionNumAndApplicationStatusAndCustomerId(
			String appId, String applicationId, int versionNum, String inprogressStatus, BigDecimal customerId);

	@Query(value="Select new ApplicationMaster(appMaster.appId, appMaster.applicationId, appMaster.versionNum, appMaster.applicationDate, "
			+ "appMaster.createTs,appMaster.createdBy, appMaster.applicationType, appMaster.kycType, appMaster.applicationStatus, appMaster.customerId, appMaster.mobileNumber, "
			+ "appMaster.nationalId, appMaster.pan, appMaster.productGroupCode, appMaster.productCode, appMaster.searchCode1, appMaster.searchCode2, appMaster.assignedTo, "
			+ "appMaster.emailId, appMaster.currentStage, appMaster.declarationFlag, appMaster.accNumber, appMaster.mobileVerStatus, appMaster.emailVerStatus, "
			+ "appMaster.currentScreenId, appMaster.remarks, appMaster.relatedApplicationId) FROM ApplicationMaster appMaster WHERE appMaster.appId=:appId and "
	  		+ "(:mobileNumber is null or appMaster.mobileNumber=:mobileNumber) and "
	  		+ "(:nationalId is null or appMaster.nationalId=:nationalId) and "
			+ "(:pan is null or appMaster.pan=:pan) and " + "(:emailId is null or appMaster.emailId=:emailId) and "
	  		+ "(:productGroupCode is null or appMaster.productGroupCode=:productGroupCode) and "
	  		+ "(:customerId is null or appMaster.customerId=:customerId) and "
	  		+ "(appMaster.applicationStatus in :statusList) and "
	  		+ "(appMaster.versionNum=(select max(appMaster2.versionNum) from ApplicationMaster appMaster2 "
	  		+ "where appMaster2.appId=:appId and (:mobileNumber is null or appMaster2.mobileNumber=:mobileNumber) and "
	  		+ "(:nationalId is null or appMaster2.nationalId=:nationalId) and "
			+ "(:pan is null or appMaster2.pan=:pan) and " + "(:emailId is null or appMaster2.emailId=:emailId) and "
	  		+ "(:productGroupCode is null or appMaster2.productGroupCode=:productGroupCode) and "
	  		+ "(:customerId is null or appMaster2.customerId=:customerId) and "
	  		+ "(appMaster2.applicationStatus in :statusList)))", nativeQuery = false)
	public List<ApplicationMaster> findData(@Param("appId") String appId, @Param("mobileNumber") String mobileNumber,
			@Param("nationalId") String nationalId, @Param("pan") String pan, @Param("emailId") String emailId,
			@Param("productGroupCode") String productGroupCode, @Param("statusList") List<String> statusList,
			@Param("customerId") BigDecimal customerId);
	
	@Query(value ="select new ApplicationMaster(appMaster.versionNum, appMaster.applicationId, appMaster.applicationStatus, "
			+ "appMaster.createTs, appMaster.createdBy, appMaster.productCode, appMaster.productGroupCode, appMaster.applicationType, appMaster.mobileNumber, appMaster.appId) FROM ApplicationMaster "
			+ "appMaster where " + "appMaster.searchCode1=:branchCode AND "
			+ "(:mobileNum is null or appMaster.mobileNumber=:mobileNum) " 
			+ "AND (coalesce(:subProduct, null) is null or appMaster.productCode in :subProduct) " 
			+ "AND (:product is null or appMaster.productGroupCode=:product) " 
			+ "AND (coalesce(:applicationStatus, null) is null or appMaster.applicationStatus in :applicationStatus) "
			+ "AND (:startDate is null or :endDate is null or appMaster.applicationDate BETWEEN :startDate and :endDate) AND "
			+ "appMaster.versionNum=(select max(appMaster2.versionNum) from ApplicationMaster appMaster2 where "
			+ "appMaster.applicationId=appMaster2.applicationId and appMaster2.searchCode1=:branchCode and "
			+ "((:mobileNum is null or appMaster2.mobileNumber=:mobileNum) "
			+ "AND (coalesce(:subProduct, null) is null or appMaster2.productCode in :subProduct) " 
			+ "AND (:product is null or appMaster2.productGroupCode=:product) " 
			+ "AND (coalesce(:applicationStatus, null) is null or appMaster2.applicationStatus in :applicationStatus) " 
			+ "AND (:startDate is null or :endDate is null or appMaster2.applicationDate BETWEEN :startDate and :endDate))) "
			+ "order by appMaster.applicationStatus", nativeQuery = false)
	public List<ApplicationMaster> advanceSearchApplications(@Param("mobileNum") String mobileNum, 
			@Param("subProduct") List<String> subProduct, @Param("product") String product,
			@Param("applicationStatus") List<String> applicationStatus, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate, @Param("branchCode") String branchCode);
	
	public long countByMemberIdAndPrimaryKycId(String memberId, String primaryKycId);
	
	public Optional<String> findTopByMemberIdAndPrimaryKycId(String memberId, String primaryKycId);
	
	public Optional<ApplicationMaster> findTopByWorkitemNo(String workitemNo);
	
	public Optional<ApplicationMaster> findTopByApplicationId(String applicationId);
	
	@Query(value = "select new ApplicationMaster(appMaster.workitemNo,appMaster.branchId, appMaster.branchName, "
			+ " appMaster.relatedApplicationId, appMaster.kendraId, appMaster.kendraName, "
			+ " appMaster.createdBy, appMaster.applicationId, applicantCustdtls.custDtlId, "
			+ " applicantCustdtls.customerName,coApplicantCustdtls.custDtlId,"
			+ " coApplicantCustdtls.customerName, coApplicantCustdtls.payloadColumn, appMaster.applicationDate, appMaster.applicationStatus,"
			+ " appMaster.updateTs,loanDetail.loanAmount,applicantCBDtls.custDtlId,coApplicantCBDtls.custDtlId) FROM ApplicationMaster appMaster"
			+ " left outer join CustomerDetails applicantCustdtls on appMaster.applicationId=applicantCustdtls.applicationId and applicantCustdtls.customerType = 'Applicant' "
			+ " left outer join CustomerDetails coApplicantCustdtls on appMaster.applicationId=coApplicantCustdtls.applicationId and coApplicantCustdtls.customerType = 'Co-App' "
			+ " left outer join LoanDetails loanDetail on loanDetail.applicationId = appMaster.applicationId "

			+ " left outer join CibilDetails applicantCBDtls on applicantCBDtls.applicationId = appMaster.applicationId and applicantCBDtls.custDtlId = applicantCustdtls.custDtlId"

			+ " left outer join CibilDetails coApplicantCBDtls on coApplicantCBDtls.applicationId = appMaster.applicationId and coApplicantCBDtls.custDtlId = coApplicantCustdtls.custDtlId "

			+ " where appMaster.branchId in :branchIds "
			+ " AND appMaster.applicationDate BETWEEN :startDate and :endDate "
			+ " order by appMaster.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchApplicationTATReport(@Param("branchIds") List<String> branchIds,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query(value = "select new ApplicationMaster(appMaster1.applicationId, appMaster1.versionNum, appMaster1.applicationStatus, "
			+ "appMaster1.createTs, appMaster1.createdBy, appMaster1.mobileNumber, appMaster1.applicationDate, appMaster1.kycType, appMaster1.pan, appMaster1.nationalId, "
			+ "appMaster1.productCode, appMaster1.customerId, appMaster1.emailId, appMaster1.searchCode1, appMaster1.accNumber, appMaster1.productGroupCode, "
			+ "appMaster1.appId, applnWf1.applicationStatus, applnWf1.createdBy, applnWf1.createTs, appMaster1.applicantsCount, appMaster1.applicationType, "
			+ "appMaster1.workitemNo, appMaster1.memberId, custdtls.customerName, appMaster1.updatedBy, "
			+ "CASE WHEN (select count(*) FROM ApplicationWorkflow applnWf3 where applnWf3.applicationId = appMaster1.applicationId and "
			+ "applnWf3.applicationStatus in :reworkStages) >0 THEN 'true' ELSE 'false' END AS isRework,appMaster1.branchName,loanDtl.loanAmount) FROM "
			+ "ApplicationMaster appMaster1 " + "left outer join "
			+ "LoanDetails loanDtl on appMaster1.applicationId=loanDtl.applicationId " + "left outer join "
			+ "ApplicationWorkflow applnWf1 " + "on appMaster1.applicationId=applnWf1.applicationId "
			+ "left outer join CustomerDetails custdtls on appMaster1.applicationId=custdtls.applicationId and custdtls.customerType = 'Applicant' "
			+ "where appMaster1.branchId in :branchIds AND "
			+ "appMaster1.applicationStatus in :status AND (appMaster1.updatedBy != :userId OR appMaster1.updatedBy is null) "
			+ "AND applnWf1.workflowSeqNum=(select max(applnWf2.workflowSeqNum) from " + "ApplicationWorkflow applnWf2 "
			+ "where applnWf2.applicationId=appMaster1.applicationId) "
			+ "order by appMaster1.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchDashBoardDataBranchIdsIn(List<String> status, List<String> branchIds,
			List<String> reworkStages, String userId);

	@Query(value = "SELECT new ApplicationMaster( "
			+ "CASE WHEN :role = 'RPC_MAKER' THEN "
			+ "     (COUNT(CASE WHEN :role = 'RPC_MAKER' AND appMaster1.applicationStatus = 'APPROVED' AND NOT EXISTS "
			+ "         (SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId "
			+ "          AND applnWf3.applicationStatus IN :reworkStages) THEN 1 END) "
			+ "     + COUNT(CASE WHEN :role = 'RPC_MAKER' AND appMaster1.applicationStatus = 'APPROVED' AND EXISTS "
			+ "         (SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId "
			+ "          AND applnWf3.applicationStatus IN :ipushback) THEN 1 END) "
			+ "     + COUNT(CASE WHEN :role = 'RPC_MAKER' AND appMaster1.applicationStatus = :rpcPushBack AND EXISTS "
			+"(SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId "
			+"AND applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ "          AND applnWf3.applicationStatus IN :rpcPushBack) AND NOT EXISTS("
			+"SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) AND applnWf3.createdBy = :userId) THEN 1 END)) "
			+ "WHEN :role = 'RPC_CHECKER' THEN "
			+ "     (COUNT(CASE WHEN :role = 'RPC_CHECKER' AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION' AND NOT EXISTS "
			+ "         (SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId "
			+ "          AND applnWf3.applicationStatus IN (:ipushback, :rpcPushBack)) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " ) THEN 1 END) "
			+ "     + COUNT(CASE WHEN :role = 'RPC_CHECKER'  AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION' AND EXISTS "
			+ "         (SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId "
			+ "         AND applnWf3.applicationStatus IN (:ipushback, :rpcPushBack)) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " ) THEN 1 END)) "
			+ "END AS totalApplications, "
			+ "COUNT(CASE WHEN :role = 'RPC_MAKER' AND  appMaster1.applicationStatus = 'APPROVED' AND NOT EXISTS "
			+"(SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId "
			+ "AND applnWf3.applicationStatus IN :reworkStages) THEN 1 "
			+ "WHEN :role = 'RPC_CHECKER' AND  appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION' AND NOT EXISTS "
			+"( SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId "
			+ "AND applnWf3.applicationStatus IN (:ipushback, :rpcPushBack))AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " ) THEN 1 END) AS freshCases, "
			+ "COUNT(CASE WHEN :role = 'RPC_MAKER' AND  appMaster1.applicationStatus = 'APPROVED' AND EXISTS "
			+"( SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId "
			+ "AND applnWf3.applicationStatus IN :ipushback) THEN 1 "
			+ "WHEN :role = 'RPC_CHECKER' AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION' AND EXISTS "
			+"( SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId "
			+ "AND applnWf3.applicationStatus IN (:ipushback, :rpcPushBack)) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " ) THEN 1 END) AS reworkCases, "
			+ "COUNT(CASE WHEN :role = 'RPC_MAKER' AND appMaster1.applicationStatus = :rpcPushBack AND EXISTS "
			+"(SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId "
			+"AND applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ "          AND applnWf3.applicationStatus IN :rpcPushBack) AND NOT EXISTS("
			+"SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) AND applnWf3.createdBy = :userId) THEN 1 "
			+ "WHEN :role = 'RPC_CHECKER' AND appMaster1.applicationStatus = :rpcPushBack THEN 1 END) AS rpcCheckerToMaker, "
			+ "COUNT(CASE WHEN (:role = 'RPC_MAKER' AND appMaster1.applicationStatus = :ipushback AND EXISTS("
			+" SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+" applnWf3.workflowSeqNum = ("
			+" SELECT MAX(applnWf4.workflowSeqNum) FROM ApplicationWorkflow applnWf4 "
            + "    WHERE applnWf4.applicationId = appMaster1.applicationId "
            + "    AND applnWf4.workflowSeqNum < applnWf1.workflowSeqNum) AND applnWf3.applicationStatus IN ('APPROVED',:rpcPushBack)))"
			+" OR (:role = 'RPC_CHECKER' AND appMaster1.applicationStatus = :ipushback AND EXISTS("
			+" SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+" applnWf3.workflowSeqNum = ("
			+" SELECT MAX(applnWf4.workflowSeqNum) FROM ApplicationWorkflow applnWf4 "
            + "    WHERE applnWf4.applicationId = appMaster1.applicationId "
            + "    AND applnWf4.workflowSeqNum < applnWf1.workflowSeqNum) AND applnWf3.applicationStatus = 'PENDINGFORRPCVERIFICATION' )) THEN 1 END) AS pushbackCases, "
			+ "COUNT(CASE WHEN (:role = 'RPC_MAKER' AND appMaster1.applicationStatus = :completedCaseStatus) "
			+ "OR (:role = 'RPC_CHECKER' AND appMaster1.applicationStatus = :completedCaseStatus) THEN 1 END) AS completedCases, "
			+ "COUNT(CASE WHEN ("
			+ "     (:role = 'RPC_MAKER' AND ("
			+ "(appMaster1.applicationStatus IN ('APPROVED') AND "
			+ "NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf"
			+ "        WHERE wf.applicationId = appMaster1.applicationId "
			+ "          AND wf.applicationStatus IN :reworkStages)) "
			+ "OR (appMaster1.applicationStatus IN ('APPROVED') AND EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "       WHERE wf.applicationId = appMaster1.applicationId"
			+ "         AND wf.applicationStatus IN :ipushback)) "
			+ "OR (appMaster1.applicationStatus IN (:rpcPushBack) AND EXISTS (SELECT 1 FROM ApplicationWorkflow applnWf3"
			+ "         WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) "
			+ "          AND applnWf3.applicationStatus IN :rpcPushBack) AND NOT EXISTS("
			+"SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) AND applnWf3.createdBy = :userId) )"
			+ ")) OR "
			+ "     (:role = 'RPC_CHECKER' AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION')" + "AND ( "
			+ "    (NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                 WHERE wf.applicationId = appMaster1.applicationId "
			+ "                 AND wf.applicationStatus IN :reworkStages "
			+ "                 ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " )) "
			+ "    OR (EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :reworkStages "
			+ "                ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " ))"
			+ ")) "
			+ "AND FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400) = 0 THEN 1 END) AS dayZero, "
			+ "COUNT(CASE WHEN ("
			+ "     (:role = 'RPC_MAKER' AND ("
			+ "(appMaster1.applicationStatus IN ('APPROVED') AND "
			+ "NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf"
			+ "        WHERE wf.applicationId = appMaster1.applicationId "
			+ "          AND wf.applicationStatus IN :reworkStages)) "
			+ "OR (appMaster1.applicationStatus IN ('APPROVED') AND EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "       WHERE wf.applicationId = appMaster1.applicationId"
			+ "         AND wf.applicationStatus IN :ipushback)) "
			+ "OR (appMaster1.applicationStatus IN (:rpcPushBack) AND EXISTS (SELECT 1 FROM ApplicationWorkflow applnWf3"
			+ "         WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) "
			+ "          AND applnWf3.applicationStatus IN :rpcPushBack) AND NOT EXISTS("
			+"SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) AND applnWf3.createdBy = :userId) )"
			+ ")) OR "
			+ "     (:role = 'RPC_CHECKER' AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION')" + "AND ( "
			+ "    (NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                 WHERE wf.applicationId = appMaster1.applicationId "
			+ "                 AND wf.applicationStatus IN :reworkStages "
			+ "                 ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " )) "
			+ "    OR (EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :reworkStages "
			+ "                ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " ))"
			+ ")) "
			+ "AND FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400) = 1 THEN 1 END) AS dayOne,"
			+ "COUNT(CASE WHEN("
			+ "     (:role = 'RPC_MAKER' AND ("
			+ "(appMaster1.applicationStatus IN ('APPROVED') AND "
			+ "NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf"
			+ "        WHERE wf.applicationId = appMaster1.applicationId "
			+ "          AND wf.applicationStatus IN :reworkStages)) "
			+ "OR (appMaster1.applicationStatus IN ('APPROVED') AND EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "       WHERE wf.applicationId = appMaster1.applicationId"
			+ "         AND wf.applicationStatus IN :ipushback)) "
			+ "OR (appMaster1.applicationStatus IN (:rpcPushBack) AND EXISTS (SELECT 1 FROM ApplicationWorkflow applnWf3"
			+ "         WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) "
			+ "          AND applnWf3.applicationStatus IN :rpcPushBack) AND NOT EXISTS("
			+"SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) AND applnWf3.createdBy = :userId) )"
			+ ")) OR "
			+ "     (:role = 'RPC_CHECKER' AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION')" + "AND ( "
			+ "    (NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                 WHERE wf.applicationId = appMaster1.applicationId "
			+ "                 AND wf.applicationStatus IN :reworkStages "
			+ "                 ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " )) "
			+ "    OR (EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :reworkStages "
			+ "                ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " ))"
			+ ")) "
			+ "AND FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400) = 2 THEN 1 END) AS dayTwo, "
			+ "COUNT(CASE WHEN("
			+ "     (:role = 'RPC_MAKER' AND ("
			+ "(appMaster1.applicationStatus IN ('APPROVED') AND "
			+ "NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf"
			+ "        WHERE wf.applicationId = appMaster1.applicationId "
			+ "          AND wf.applicationStatus IN :reworkStages)) "
			+ "OR (appMaster1.applicationStatus IN ('APPROVED') AND EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "       WHERE wf.applicationId = appMaster1.applicationId"
			+ "         AND wf.applicationStatus IN :ipushback)) "
			+ "OR (appMaster1.applicationStatus IN (:rpcPushBack) AND EXISTS (SELECT 1 FROM ApplicationWorkflow applnWf3"
			+ "         WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) "
			+ "          AND applnWf3.applicationStatus IN :rpcPushBack) AND NOT EXISTS("
			+"SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) AND applnWf3.createdBy = :userId) )"
			+ ")) OR "
			+ "     (:role = 'RPC_CHECKER' AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION')" + "AND ( "
			+ "    (NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                 WHERE wf.applicationId = appMaster1.applicationId "
			+ "                 AND wf.applicationStatus IN :reworkStages "
			+ "                 ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " )) "
			+ "    OR (EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :reworkStages "
			+ "                ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " ))"
			+ ")) "
			+ "AND FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400) = 3 THEN 1 END) AS dayThree,"
			+ "COUNT(CASE WHEN("
			+ "     (:role = 'RPC_MAKER' AND ("
			+ "(appMaster1.applicationStatus IN ('APPROVED') AND "
			+ "NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf"
			+ "        WHERE wf.applicationId = appMaster1.applicationId "
			+ "          AND wf.applicationStatus IN :reworkStages)) "
			+ "OR (appMaster1.applicationStatus IN ('APPROVED') AND EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "       WHERE wf.applicationId = appMaster1.applicationId"
			+ "         AND wf.applicationStatus IN :ipushback)) "
			+ "OR (appMaster1.applicationStatus IN (:rpcPushBack) AND EXISTS (SELECT 1 FROM ApplicationWorkflow applnWf3"
			+ "         WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) "
			+ "          AND applnWf3.applicationStatus IN :rpcPushBack) AND NOT EXISTS("
			+"SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) AND applnWf3.createdBy = :userId) )"
			+ ")) OR "
			+ "     (:role = 'RPC_CHECKER' AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION')" + "AND ( "
			+ "    (NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                 WHERE wf.applicationId = appMaster1.applicationId "
			+ "                 AND wf.applicationStatus IN :reworkStages "
			+ "                 ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " )) "
			+ "    OR (EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :reworkStages "
			+ "                ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " ))"
			+ ")) "
			+ "AND FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400) = 4 THEN 1 END) AS dayFour, "
			+ "COUNT(CASE WHEN("
			+ "     (:role = 'RPC_MAKER' AND ("
			+ "(appMaster1.applicationStatus IN ('APPROVED') AND "
			+ "NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf"
			+ "        WHERE wf.applicationId = appMaster1.applicationId "
			+ "          AND wf.applicationStatus IN :reworkStages)) "
			+ "OR (appMaster1.applicationStatus IN ('APPROVED') AND EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "       WHERE wf.applicationId = appMaster1.applicationId"
			+ "         AND wf.applicationStatus IN :ipushback)) "
			+ "OR (appMaster1.applicationStatus IN (:rpcPushBack) AND EXISTS (SELECT 1 FROM ApplicationWorkflow applnWf3"
			+ "         WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) "
			+ "          AND applnWf3.applicationStatus IN :rpcPushBack) AND NOT EXISTS("
			+"SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) AND applnWf3.createdBy = :userId) )"
			+ ")) OR "
			+ "     (:role = 'RPC_CHECKER' AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION')" + "AND ( "
			+ "    (NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                 WHERE wf.applicationId = appMaster1.applicationId "
			+ "                 AND wf.applicationStatus IN :reworkStages "
			+ "                 ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " )) "
			+ "    OR (EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :reworkStages "
			+ "                ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " ))"
			+ ")) "
			+ "AND FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400) = 5 THEN 1 END) AS dayFive, "
			+ "COUNT(CASE WHEN("
			+ "     (:role = 'RPC_MAKER' AND ("
			+ "(appMaster1.applicationStatus IN ('APPROVED') AND "
			+ "NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf"
			+ "        WHERE wf.applicationId = appMaster1.applicationId "
			+ "          AND wf.applicationStatus IN :reworkStages)) "
			+ "OR (appMaster1.applicationStatus IN ('APPROVED') AND EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "       WHERE wf.applicationId = appMaster1.applicationId"
			+ "         AND wf.applicationStatus IN :ipushback)) "
			+ "OR (appMaster1.applicationStatus IN (:rpcPushBack) AND EXISTS (SELECT 1 FROM ApplicationWorkflow applnWf3"
			+ "         WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) "
			+ "          AND applnWf3.applicationStatus IN :rpcPushBack) AND NOT EXISTS("
			+"SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) AND applnWf3.createdBy = :userId) )"
			+ ")) OR "
			+ "     (:role = 'RPC_CHECKER' AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION')" + "AND ( "
			+ "    (NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                 WHERE wf.applicationId = appMaster1.applicationId "
			+ "                 AND wf.applicationStatus IN :reworkStages "
			+ "                 ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " )) "
			+ "    OR (EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :reworkStages "
			+ "                ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " ))"
			+ ")) "
			+ "AND FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400) > 5 THEN 1 END) AS moreThanFiveDays )"
			+ "FROM ApplicationMaster appMaster1 "
			+ "LEFT JOIN ApplicationWorkflow applnWf1 ON appMaster1.applicationId = applnWf1.applicationId "
			+ "LEFT JOIN CustomerDetails custdtls ON appMaster1.applicationId = custdtls.applicationId "
			+ "WHERE appMaster1.branchId IN :branchIds "
			+ "AND appMaster1.applicationStatus in :status AND appMaster1.declarationFlag = :IexceedFlag "
			+ "AND custdtls.customerType = 'Applicant' "
			+ "AND applnWf1.workflowSeqNum = ( "
			+ "SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)", nativeQuery = false)
	public ApplicationMaster fetchDashBoardDataWithCounts(List<String> status, List<String> branchIds,
			List<String> reworkStages, String rpcPushBack, String ipushback, String completedCaseStatus,
			String role, String userId, String IexceedFlag);

	@Query(value = "SELECT new ApplicationMaster( "
			+ "appMaster1.applicationId AS applicationId, "
			+ "appMaster1.memberId AS customerId, "
			+ "custdtls.customerName AS customerName, "
			+ "appMaster1.branchName AS branchName, "
			+ "CONCAT("
			+ "FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) "
			+ "		  - FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 60) * 60), 'S|',"
			+ "	FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 60 "
			+ "		  - FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 3600) * 60), 'M|',"
			+ "	FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 3600 "
			+ "		  - FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400) * 24), 'H|',"
			+ "	FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400), 'D' ) AS rpcTAT,"
			+ "loanDtl.loanAmount AS loanAmount,"
			+ " appMaster1.applicationStatus as applicationStatus,"
			+ "CASE WHEN COUNT(DISTINCT applnWfRework.applicationId) > 0 THEN 'true' ELSE 'false' END AS isRework) "
			+ "FROM ApplicationMaster appMaster1 "
			+ "LEFT JOIN LoanDetails loanDtl ON appMaster1.applicationId = loanDtl.applicationId "
			+ "LEFT JOIN ApplicationWorkflow applnWf1 ON appMaster1.applicationId = applnWf1.applicationId "
			+ " LEFT JOIN ApplicationWorkflow applnWfRework ON applnWfRework.applicationId = appMaster1.applicationId " +
            " AND (applnWfRework.applicationStatus IN :reworkStages OR EXISTS (SELECT 1 FROM ApplicationWorkflow wf2 WHERE wf2.applicationId = applnWfRework.applicationId " +
            " AND wf2.workflowSeqNum <= applnWfRework.workflowSeqNum - 2 AND wf2.applicationStatus = 'APPROVED')) "
			+ "LEFT JOIN CustomerDetails custdtls ON appMaster1.applicationId = custdtls.applicationId "
			+ "AND custdtls.customerType = 'Applicant' " + "WHERE appMaster1.branchId IN :branchIds "
			+ "AND appMaster1.applicationStatus in :status AND appMaster1.declarationFlag = :IexceedFlag "
			+ "AND applnWf1.workflowSeqNum = (SELECT max(applnWf2.workflowSeqNum) "
			+ "FROM ApplicationWorkflow applnWf2 " + "WHERE applnWf2.applicationId = appMaster1.applicationId) "
			+ "AND ( " + "    (:role = 'RPC_MAKER' AND ( "
			+ "(:filterType = 'all' "
			+ "AND ("
			+"(appMaster1.applicationStatus IN ('APPROVED') AND "
			+ "NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf"
			+ "        WHERE wf.applicationId = appMaster1.applicationId "
			+ "          AND wf.applicationStatus IN :reworkStages)) "
			+ "OR (appMaster1.applicationStatus IN ('APPROVED') AND EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "       WHERE wf.applicationId = appMaster1.applicationId"
			+ "         AND wf.applicationStatus IN :ipushback)) "
			+ "OR (appMaster1.applicationStatus IN (:rpcPushBack) AND EXISTS (SELECT 1 FROM ApplicationWorkflow applnWf3"
			+ "         WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) "
			+ "          AND applnWf3.applicationStatus IN :rpcPushBack) AND NOT EXISTS("
			+"SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) AND applnWf3.createdBy = :userId) ) "
			+ ")) OR "
			+ "        (:filterType = 'freshCases'"
			+ "            AND appMaster1.applicationStatus = 'APPROVED' AND NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :reworkStages)) OR "
			+ "        (:filterType = 'reworkCases' "
			+ "           AND appMaster1.applicationStatus = 'APPROVED' AND EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :ipushback)) OR "
			+ "        (:filterType = 'rpcCheckerToMaker' AND appMaster1.applicationStatus = :rpcPushBack "
			+"AND EXISTS(SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) "
			+ "          AND applnWf3.applicationStatus IN :rpcPushBack) AND NOT EXISTS("
			+"SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) AND applnWf3.createdBy = :userId) ) "
			+ "    )) "
			+ "    OR " + "    (:role = 'RPC_CHECKER' AND ( "
			+ "(:filterType = 'all' "
			+ "AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION' "
			+ "AND ( "
			+ "    (NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                 WHERE wf.applicationId = appMaster1.applicationId "
			+ "                 AND wf.applicationStatus IN :reworkStages "
			+ "                 ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " )) "
			+ "    OR (EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :reworkStages "
			+ "                ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " ))"
			+ ")) OR "
			+ "(:filterType = 'freshCases' "
			+ "  AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION' "
			+ "AND NOT EXISTS ( "
			+ "  SELECT 1 FROM ApplicationWorkflow wf "
			+ "WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.applicationStatus IN :reworkStages) "
			+ " AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " )) OR "
			+ "        (:filterType = 'reworkCases'"
			+ "           AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION' AND EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :reworkStages )"+ " AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId)"
			+ " )) " + "    )) "
			+ ") "
			+ " GROUP BY appMaster1.applicationId, "
			+ "appMaster1.memberId, "
			+ "custdtls.customerName,"
			+ "appMaster1.branchName, "
			+ "loanDtl.loanAmount, "
			+ "appMaster1.applicationStatus, appMaster1.createTs, applnWf1.createTs, applnWf1.workflowSeqNum "
            + "ORDER BY EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) ASC", nativeQuery = false)
    //ascending order since epoch works opposite to timestamp in terms of ordering
    public Page<ApplicationMaster> fetchDashBoardApplications(@Param("status") List<String> status,
            @Param("branchIds") List<String> branchIds, Pageable pageable,
            @Param("role") String role, @Param("filterType") String filterType,
            @Param("reworkStages") List<String> reworkStages, @Param("ipushback") String ipushback,
            @Param("rpcPushBack") String rpcPushBack, String userId, String IexceedFlag);

	@Query(value = "SELECT new ApplicationMaster("
            + "appMaster1.applicationId AS applicationId, "
            + "appMaster1.memberId AS customerId, "
            + "custdtls.customerName AS customerName, "
            + "appMaster1.branchName AS branchName, "
            + "CONCAT("
			+	"FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) "
			+"		  - FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 60) * 60), 'S|',"
			+"	FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 60 "
			+"		  - FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 3600) * 60), 'M|',"
			+"	FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 3600 "
			+"		  - FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400) * 24), 'H|',"
			+"	FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400), 'D' ) AS rpcTAT,"
            + "loanDtl.loanAmount AS loanAmount, "
            + "appMaster1.applicationStatus AS applicationStatus, "
			+ "CASE WHEN COUNT(DISTINCT applnWfRework.applicationId) > 0 THEN 'true' ELSE 'false' END AS isRework) "
			+ "FROM ApplicationMaster appMaster1 "
            + "LEFT JOIN LoanDetails loanDtl ON appMaster1.applicationId = loanDtl.applicationId "
            + "LEFT JOIN ApplicationWorkflow applnWf1 ON appMaster1.applicationId = applnWf1.applicationId "
            + "LEFT JOIN CustomerDetails custdtls ON appMaster1.applicationId = custdtls.applicationId "
            + "AND custdtls.customerType = 'Applicant' "
			+ " LEFT JOIN ApplicationWorkflow applnWfRework ON applnWfRework.applicationId = appMaster1.applicationId AND " +
            " (applnWfRework.applicationStatus IN :reworkStages OR EXISTS (SELECT 1 FROM ApplicationWorkflow wf2 WHERE wf2.applicationId = applnWfRework.applicationId AND " +
            " wf2.workflowSeqNum <= applnWfRework.workflowSeqNum - 2 AND wf2.applicationStatus = 'APPROVED')) "
			+ "WHERE appMaster1.branchId IN :branchIds "
            + "AND appMaster1.applicationStatus IN :status AND appMaster1.declarationFlag = :IexceedFlag "
            + "AND applnWf1.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) "
            + "FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) "
            + "AND ((:role = 'RPC_MAKER' AND ("
            + "(:filterType = 'PENDINGFORRPCVERIFICATION' AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION') OR "
            + "(:filterType = :ipushback AND appMaster1.applicationStatus = :ipushback AND EXISTS("
			+" SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+" applnWf3.workflowSeqNum = ("
			+" SELECT MAX(applnWf4.workflowSeqNum) FROM ApplicationWorkflow applnWf4 "
            + "    WHERE applnWf4.applicationId = appMaster1.applicationId "
            + "    AND applnWf4.workflowSeqNum < applnWf1.workflowSeqNum) AND applnWf3.applicationStatus IN ('APPROVED',:rpcPushBack)))"
            + ")) OR "
            + "(:role = 'RPC_CHECKER' AND ("
            + "(:filterType = 'RPCVERIFIED' AND appMaster1.applicationStatus = 'RPCVERIFIED') OR "
            +"(:filterType = 'ALLSENTBACK' AND appMaster1.applicationStatus IN (:rpcPushBack, :ipushback) AND EXISTS("
			+" SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+" applnWf3.workflowSeqNum = ("
			+" SELECT MAX(applnWf4.workflowSeqNum) FROM ApplicationWorkflow applnWf4 "
            + "    WHERE applnWf4.applicationId = appMaster1.applicationId "
            + "    AND applnWf4.workflowSeqNum < applnWf1.workflowSeqNum) AND applnWf3.applicationStatus = 'PENDINGFORRPCVERIFICATION' )) OR "
            + "(:filterType = :rpcPushBack AND appMaster1.applicationStatus = :rpcPushBack) OR "
            + "(:filterType = :ipushback AND appMaster1.applicationStatus = :ipushback AND EXISTS("
			+" SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+" applnWf3.workflowSeqNum = ("
			+" SELECT MAX(applnWf4.workflowSeqNum) FROM ApplicationWorkflow applnWf4 "
            + "    WHERE applnWf4.applicationId = appMaster1.applicationId "
            + "    AND applnWf4.workflowSeqNum < applnWf1.workflowSeqNum) AND applnWf3.applicationStatus = 'PENDINGFORRPCVERIFICATION' ))"
            + "))) "
            + "GROUP BY appMaster1.applicationId, appMaster1.memberId,custdtls.customerName, "
            + "appMaster1.branchName, loanDtl.loanAmount, "
            + "appMaster1.applicationStatus, appMaster1.createTs, applnWf1.createTs, applnWf1.workflowSeqNum "
			+"ORDER BY EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) ASC", nativeQuery = false)
    Page<ApplicationMaster> fetchDashBoardALTApplications(
            @Param("status") List<String> status,
            @Param("branchIds") List<String> branchIds, Pageable pageable,
            @Param("role") String role, @Param("filterType") String filterType,
            @Param("ipushback") String ipushback, @Param("reworkStages") List<String> reworkStages,
            @Param("rpcPushBack") String rpcPushBack, String IexceedFlag);

		@Query(value = "SELECT new ApplicationMaster( "
			+ "appMaster1.applicationId AS applicationId, "
			+ "appMaster1.memberId AS customerId, "
			+ "custdtls.customerName AS customerName, "
			+ "appMaster1.branchName AS branchName, "
			+ "CONCAT("
			+ "FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) "
			+ "		  - FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 60) * 60), 'S|',"
			+ "	FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 60 "
			+ "		  - FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 3600) * 60), 'M|',"
			+ "	FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 3600 "
			+ "		  - FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400) * 24), 'H|',"
			+ "	FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400), 'D' ) AS rpcTAT,"
			+ "loanDtl.loanAmount AS loanAmount,"
			+ " appMaster1.applicationStatus as applicationStatus,"
				+ "CASE WHEN COUNT(DISTINCT applnWfRework.applicationId) > 0 THEN 'true' ELSE 'false' END AS isRework) "
			+ "FROM ApplicationMaster appMaster1 "
			+ "LEFT JOIN LoanDetails loanDtl ON appMaster1.applicationId = loanDtl.applicationId "
			+ "LEFT JOIN ApplicationWorkflow applnWf1 ON appMaster1.applicationId = applnWf1.applicationId "
            + "LEFT JOIN ApplicationWorkflow applnWfRework ON applnWfRework.applicationId = appMaster1.applicationId AND " +
            " (applnWfRework.applicationStatus IN :reworkStages OR EXISTS (SELECT 1 FROM ApplicationWorkflow wf2 WHERE wf2.applicationId = applnWfRework.applicationId AND " +
            " wf2.workflowSeqNum <= applnWfRework.workflowSeqNum - 2 AND wf2.applicationStatus = 'APPROVED')) "
			+ "LEFT JOIN CustomerDetails custdtls ON appMaster1.applicationId = custdtls.applicationId "
			+ "AND custdtls.customerType = 'Applicant' " + "WHERE appMaster1.branchId IN :branchIds "
			+ "AND appMaster1.applicationStatus in :status AND appMaster1.declarationFlag = :IexceedFlag "
			+ "AND applnWf1.workflowSeqNum = (SELECT max(applnWf2.workflowSeqNum) "
			+ "FROM ApplicationWorkflow applnWf2 "
			+ "WHERE applnWf2.applicationId = appMaster1.applicationId) AND (LOWER(appMaster1.applicationId) = LOWER(:searchVal) OR "
			+ " LOWER(appMaster1.memberId) = LOWER(:searchVal) OR LOWER(appMaster1.branchName) = LOWER(:searchVal) OR "
			+"LOWER(appMaster1.alternateVoterId) = LOWER(:searchVal) OR LOWER(appMaster1.primaryKycId) = LOWER(:searchVal)"
			+" OR LOWER(appMaster1.secondaryKycId) = LOWER(:searchVal) OR LOWER(custdtls.mobileNumber) = LOWER(:searchVal))"
			+ "AND ( " + "    (:role = 'RPC_MAKER' AND ( "
			+ "(:filterType = 'all' "
			+ "AND ("
			+"(appMaster1.applicationStatus IN ('APPROVED') AND "
			+ "NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf"
			+ "        WHERE wf.applicationId = appMaster1.applicationId "
			+ "          AND wf.applicationStatus IN :reworkStages)) "
			+ "OR (appMaster1.applicationStatus IN ('APPROVED') AND EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "       WHERE wf.applicationId = appMaster1.applicationId"
			+ "         AND wf.applicationStatus IN :ipushback)) "
			+ "OR (appMaster1.applicationStatus IN (:rpcPushBack) AND EXISTS (SELECT 1 FROM ApplicationWorkflow applnWf3"
			+ "         WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) "
			+ "          AND applnWf3.applicationStatus IN :rpcPushBack) AND NOT EXISTS("
			+"SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
            + "applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) AND " +
            " applnWf3.createdBy = :userId) ) "
			+ ")) OR "
			+ "        (:filterType = 'freshCases'"
			+ "            AND appMaster1.applicationStatus = 'APPROVED' AND NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :reworkStages)) OR "
			+ "        (:filterType = 'reworkCases' "
			+ "           AND appMaster1.applicationStatus = 'APPROVED' AND EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :ipushback)) OR "
			+ "        (:filterType = 'rpcCheckerToMaker' AND appMaster1.applicationStatus = :rpcPushBack "
			+"AND EXISTS(SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+"applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) "
			+ "          AND applnWf3.applicationStatus IN :rpcPushBack) AND NOT EXISTS("
			+"SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
            + "applnWf3.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) AND " +
            " applnWf3.createdBy = :userId) ) "
			+ "    )) "
			+ "    OR " + "    (:role = 'RPC_CHECKER' AND ( "
			+ "(:filterType = 'all' "
			+ "AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION' "
			+ "AND ( "
			+ "    (NOT EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                 WHERE wf.applicationId = appMaster1.applicationId "
			+ "                 AND wf.applicationStatus IN :reworkStages "
			+ "                 ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
            + " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE " +
            " applnWf2.applicationId = appMaster1.applicationId)"
			+ " )) "
			+ "    OR (EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :reworkStages "
			+ "                ) AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
            + " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE " +
            " applnWf2.applicationId = appMaster1.applicationId)"
			+ " ))"
			+ ")) OR "
			+ "(:filterType = 'freshCases' "
			+ "  AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION' "
			+ "AND NOT EXISTS ( "
			+ "  SELECT 1 FROM ApplicationWorkflow wf "
			+ "WHERE wf.applicationId = appMaster1.applicationId "
			+ " AND wf.applicationStatus IN :reworkStages) "
			+ " AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
            + " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE " +
            " applnWf2.applicationId = appMaster1.applicationId)"
			+ " )) OR "
			+ "        (:filterType = 'reworkCases'"
			+ "           AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION' AND EXISTS (SELECT 1 FROM ApplicationWorkflow wf "
			+ "                WHERE wf.applicationId = appMaster1.applicationId "
			+ "                AND wf.applicationStatus IN :reworkStages )"+ " AND NOT EXISTS ("
			+ "   SELECT 1 FROM ApplicationWorkflow wf "
			+ " WHERE wf.applicationId = appMaster1.applicationId "
            + " AND wf.createdBy = :userId AND wf.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) FROM ApplicationWorkflow applnWf2 WHERE " +
            " applnWf2.applicationId = appMaster1.applicationId)"
			+ " )) " + "    )) "
			+ ") "
			+ " GROUP BY appMaster1.applicationId, "
			+ "appMaster1.memberId, "
			+ "custdtls.customerName,"
			+ "appMaster1.branchName, "
			+ "loanDtl.loanAmount, "
			+ "appMaster1.applicationStatus, appMaster1.createTs, applnWf1.createTs, applnWf1.workflowSeqNum  "
			+ "ORDER BY EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) ASC", nativeQuery = false)
    public Page<ApplicationMaster> fetchDashBoardApplicationsWithSearch(@Param("status") List<String> status,
            @Param("branchIds") List<String> branchIds, Pageable pageable,
            @Param("role") String role, @Param("filterType") String filterType,
            @Param("reworkStages") List<String> reworkStages, @Param("ipushback") String ipushback,
            @Param("rpcPushBack") String rpcPushBack, @Param("searchVal") String searchVal, String userId, String IexceedFlag);

	@Query(value = "SELECT new ApplicationMaster("
            + "appMaster1.applicationId AS applicationId, "
            + "appMaster1.memberId AS customerId, "
            + "custdtls.customerName AS customerName, "
            + "appMaster1.branchName AS branchName, "
			+ "CONCAT("
			+	"FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) "
			+"		  - FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 60) * 60), 'S|',"
			+"	FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 60 "
			+"		  - FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 3600) * 60), 'M|',"
			+"	FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 3600 "
			+"		  - FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400) * 24), 'H|',"
			+"	FLOOR(EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) / 86400), 'D' ) AS rpcTAT,"
            + "loanDtl.loanAmount AS loanAmount, "
            + "appMaster1.applicationStatus AS applicationStatus, "
			+ "CASE WHEN COUNT(DISTINCT applnWfRework.applicationId) > 0 THEN 'true' ELSE 'false' END AS isRework) "
            + "FROM ApplicationMaster appMaster1 "
            + "LEFT JOIN LoanDetails loanDtl ON appMaster1.applicationId = loanDtl.applicationId "
            + "LEFT JOIN ApplicationWorkflow applnWf1 ON appMaster1.applicationId = applnWf1.applicationId "
			+ " LEFT JOIN ApplicationWorkflow applnWfRework ON applnWfRework.applicationId = appMaster1.applicationId AND " +
            " (applnWfRework.applicationStatus IN :reworkStages OR EXISTS (SELECT 1 FROM ApplicationWorkflow wf2 WHERE wf2.applicationId = applnWfRework.applicationId AND " +
            " wf2.workflowSeqNum <= applnWfRework.workflowSeqNum - 2 AND wf2.applicationStatus = 'APPROVED')) "
            + "LEFT JOIN CustomerDetails custdtls ON appMaster1.applicationId = custdtls.applicationId "
            + "AND custdtls.customerType = 'Applicant' "
            + "WHERE appMaster1.branchId IN :branchIds AND appMaster1.declarationFlag = :IexceedFlag "
            + "AND appMaster1.applicationStatus IN :status "
            + "AND applnWf1.workflowSeqNum = (SELECT MAX(applnWf2.workflowSeqNum) "
            + "FROM ApplicationWorkflow applnWf2 WHERE applnWf2.applicationId = appMaster1.applicationId) AND (LOWER(appMaster1.applicationId) = LOWER(:searchVal) OR "
			+" LOWER(appMaster1.memberId) = LOWER(:searchVal) OR LOWER(appMaster1.branchName) = LOWER(:searchVal) OR "
			+"LOWER(appMaster1.alternateVoterId) = LOWER(:searchVal) OR "
			+"LOWER(appMaster1.primaryKycId) = LOWER(:searchVal) OR LOWER(appMaster1.secondaryKycId) = LOWER(:searchVal) OR LOWER(custdtls.mobileNumber) = LOWER(:searchVal))"
            + "AND ((:role = 'RPC_MAKER' AND ("
            + "(:filterType = 'PENDINGFORRPCVERIFICATION' AND appMaster1.applicationStatus = 'PENDINGFORRPCVERIFICATION') OR "
            + "(:filterType = :ipushback AND appMaster1.applicationStatus = :ipushback AND EXISTS("
			+" SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+" applnWf3.workflowSeqNum = ("
			+" SELECT MAX(applnWf4.workflowSeqNum) FROM ApplicationWorkflow applnWf4 "
            + "    WHERE applnWf4.applicationId = appMaster1.applicationId "
            + "    AND applnWf4.workflowSeqNum < applnWf1.workflowSeqNum) AND applnWf3.applicationStatus IN ('APPROVED',:rpcPushBack)))"
            + ")) OR "
            + "(:role = 'RPC_CHECKER' AND ("
            + "(:filterType = 'RPCVERIFIED' AND appMaster1.applicationStatus = 'RPCVERIFIED') OR "
            +"(:filterType = 'ALLSENTBACK' AND appMaster1.applicationStatus IN (:rpcPushBack, :ipushback) AND EXISTS("
			+" SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+" applnWf3.workflowSeqNum = ("
			+" SELECT MAX(applnWf4.workflowSeqNum) FROM ApplicationWorkflow applnWf4 "
            + "    WHERE applnWf4.applicationId = appMaster1.applicationId "
            + "    AND applnWf4.workflowSeqNum < applnWf1.workflowSeqNum) AND applnWf3.applicationStatus = 'PENDINGFORRPCVERIFICATION' )) OR "
            + "(:filterType = :rpcPushBack AND appMaster1.applicationStatus = :rpcPushBack) OR "
            + "(:filterType = :ipushback AND appMaster1.applicationStatus = :ipushback AND EXISTS("
			+" SELECT 1 FROM ApplicationWorkflow applnWf3 WHERE applnWf3.applicationId = appMaster1.applicationId AND "
			+" applnWf3.workflowSeqNum = ("
			+" SELECT MAX(applnWf4.workflowSeqNum) FROM ApplicationWorkflow applnWf4 "
            + "    WHERE applnWf4.applicationId = appMaster1.applicationId "
            + "    AND applnWf4.workflowSeqNum < applnWf1.workflowSeqNum) AND applnWf3.applicationStatus = 'PENDINGFORRPCVERIFICATION' ))"
            + "))) "
            + "GROUP BY appMaster1.applicationId, appMaster1.memberId,custdtls.customerName, "
            + "appMaster1.branchName, loanDtl.loanAmount, "
            + "appMaster1.applicationStatus, appMaster1.createTs, applnWf1.createTs, applnWf1.workflowSeqNum "
			+"ORDER BY EXTRACT(EPOCH FROM AGE(CURRENT_TIMESTAMP, applnWf1.createTs)) ASC", nativeQuery = false)
    Page<ApplicationMaster> fetchDashBoardALTApplicationsWithSearch(
            @Param("status") List<String> status,
            @Param("branchIds") List<String> branchIds, Pageable pageable,
            @Param("role") String role, @Param("filterType") String filterType,
            @Param("ipushback") String ipushback,  @Param("reworkStages") List<String> reworkStages,
            @Param("rpcPushBack") String rpcPushBack, @Param("searchVal") String searchVal, String IexceedFlag);

	@Query(value = "select count(1) FROM ApplicationMaster appMaster1 where appMaster1.branchId in :branchIds "
			+ "AND (appMaster1.applicationStatus not in :completedStatus  OR "
			+ "(appMaster1.applicationStatus = :pendingCheckerStatus AND appMaster1.updatedBy = :makerId)) ", nativeQuery = false)
	public int countRPCMakerCompletedCases(List<String> completedStatus, String pendingCheckerStatus,
			List<String> branchIds, String makerId);

	@Query(value = "select count(1) FROM ApplicationMaster appMaster1 where appMaster1.branchId in :branchIds "
			+ "AND appMaster1.applicationStatus not in :completedStatus ", nativeQuery = false)
	public int countRPCCheckerCompletedCases(List<String> completedStatus, List<String> branchIds);

	@Query(value = "select count(1) FROM ApplicationMaster appMaster1 where appMaster1.branchId in :branchIds "
			+ "AND appMaster1.applicationStatus = :pushbackStatus ", nativeQuery = false)
	public int countRPCSourcingPushbackCases(String pushbackStatus, List<String> branchIds);

	@Query(value = "select count(1) FROM ApplicationMaster appMaster1 where appMaster1.branchId in :branchIds "
			+ "AND appMaster1.applicationStatus = :rpcPushbackStatus AND appMaster1.updatedBy = :checkerId ", nativeQuery = false)
	public int countRPCPushbackCases(String rpcPushbackStatus, List<String> branchIds, String checkerId);

	@Query(value = "select new ApplicationMaster(appMaster1.applicationId, appMaster1.versionNum, appMaster1.applicationStatus, "
			+ "appMaster1.createTs, appMaster1.createdBy, appMaster1.mobileNumber, appMaster1.applicationDate, appMaster1.kycType, appMaster1.pan, appMaster1.nationalId, "
			+ "appMaster1.productCode, appMaster1.customerId, appMaster1.emailId, appMaster1.searchCode1, appMaster1.accNumber, appMaster1.productGroupCode, "
			+ "appMaster1.appId, applnWf1.applicationStatus, applnWf1.createdBy, applnWf1.createTs, appMaster1.applicantsCount, appMaster1.applicationType, appMaster1.workitemNo, appMaster1.memberId, custdtls.customerName, appMaster1.updatedBy,'',appMaster1.branchName) FROM "
			+ "ApplicationMaster appMaster1 " + "left outer join " + "ApplicationWorkflow applnWf1 "
			+ "on appMaster1.applicationId=applnWf1.applicationId and appMaster1.appId=applnWf1.appId and appMaster1.versionNum=applnWf1.versionNum "
			+ "left outer join CustomerDetails custdtls on appMaster1.applicationId=custdtls.applicationId and custdtls.customerType = 'Applicant' "
			+ "where " + "appMaster1.branchId in :branchIds AND "
			+ "appMaster1.applicationStatus in :status AND (appMaster1.applicationDate between :fromDay and :toDay) "
			+ "AND (appMaster1.relatedApplicationId is not null) "
			+ "AND applnWf1.workflowSeqNum=(select max(applnWf2.workflowSeqNum) from " + "ApplicationWorkflow applnWf2 "
			+ "where applnWf2.applicationId=appMaster1.applicationId and applnWf2.appId=appMaster1.appId) "
			+ "order by appMaster1.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchRenewalDashBoardDataBranchIdsIn(@Param("status") List<String> status,
			@Param("branchIds") List<String> branchIds, @Param("fromDay") LocalDate fromDay,
			@Param("toDay") LocalDate toDay);

	@Query(value = "select new ApplicationMaster(appMaster1.applicationId, appMaster1.versionNum, appMaster1.applicationStatus, "
			+ "appMaster1.createTs, appMaster1.createdBy, appMaster1.mobileNumber, appMaster1.applicationDate, appMaster1.kycType, appMaster1.pan, appMaster1.nationalId, "
			+ "appMaster1.productCode, appMaster1.customerId, appMaster1.emailId, appMaster1.searchCode1, appMaster1.accNumber, appMaster1.productGroupCode, "
			+ "appMaster1.appId, applnWf1.applicationStatus, applnWf1.createdBy, applnWf1.createTs, appMaster1.applicantsCount, appMaster1.applicationType, appMaster1.workitemNo, appMaster1.memberId, custdtls.customerName, appMaster1.updatedBy,'',appMaster1.branchName) FROM "
			+ "ApplicationMaster appMaster1 " + "left outer join " + "ApplicationWorkflow applnWf1 "
			+ "on appMaster1.applicationId=applnWf1.applicationId and appMaster1.appId=applnWf1.appId and appMaster1.versionNum=applnWf1.versionNum "
			+ "left outer join CustomerDetails custdtls on appMaster1.applicationId=custdtls.applicationId and custdtls.customerType = 'Applicant' "
			+ "where " + "appMaster1.branchId in :branchIds AND "
			+ "appMaster1.applicationStatus in :status AND (appMaster1.applicationDate between :fromDay and :toDay) "
			+ "AND (appMaster1.relatedApplicationId is null OR appMaster1.relatedApplicationId='') "
			+ "AND applnWf1.workflowSeqNum=(select max(applnWf2.workflowSeqNum) from " + "ApplicationWorkflow applnWf2 "
			+ "where applnWf2.applicationId=appMaster1.applicationId and applnWf2.appId=appMaster1.appId) "
			+ "order by appMaster1.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchNewDashBoardDataBranchIdsIn(@Param("status") List<String> status,
			@Param("branchIds") List<String> branchIds, @Param("fromDay") LocalDate fromDay,
			@Param("toDay") LocalDate toDay);

	@Query(value = "select new ApplicationMaster(appMaster.applicationId, appMaster.createdBy, appMaster.applicationDate, "
			+ "appMaster.kendraId,appMaster.kendraName, appMaster.workitemNo, appMaster.memberId, custdtls.customerName, appMaster.currentStageNo,'','','',appMaster.applicationStatus) FROM "
			+ "ApplicationMaster appMaster "
			+ "left outer join CustomerDetails custdtls on appMaster.applicationId=custdtls.applicationId "
			/*
			 * +
			 * "left outer join CibilDetails cbdtls on custdtls.custDtlId=cbdtls.custDtlId "
			 */
			+ "where appMaster.branchId in :branchId AND appMaster.applicationStatus =:status "
			+ "AND (appMaster.relatedApplicationId is null OR appMaster.relatedApplicationId = '') "
			+ "AND appMaster.currentStageNo in :stageNos " + "and custdtls.customerType = 'Applicant' "
			+ "order by appMaster.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchInprogressDashBoardDataBranchIdIn(@Param("status") String status,
			@Param("branchId") List<String> branchId, List<Integer> stageNos);

	@Query(value = "select new ApplicationMaster(appMaster.applicationId, appMaster.createdBy, appMaster.applicationDate, "
			+ "appMaster.kendraId,appMaster.kendraName, appMaster.workitemNo, appMaster.memberId, custdtls.customerName, appMaster.currentStageNo,'','','',appMaster.applicationStatus) FROM "
			+ "ApplicationMaster appMaster "
			+ "left outer join CustomerDetails custdtls on appMaster.applicationId=custdtls.applicationId "
			/*
			 * +
			 * "left outer join CibilDetails cbdtls on custdtls.custDtlId=cbdtls.custDtlId "
			 */
			+ "where appMaster.branchId in :branchId AND appMaster.applicationStatus =:status "
			+ "AND (appMaster.relatedApplicationId is not null AND appMaster.relatedApplicationId!='') "
			+ "AND appMaster.currentStageNo in :stageNos " + "and custdtls.customerType = 'Applicant' "
			+ "order by appMaster.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchInprogressRenewalDashBoardDataBranchIdIn(@Param("status") String status,
			@Param("branchId") List<String> branchId, List<Integer> stageNos);

	@Query(value = "select new ApplicationMaster(appMaster.applicationId, appMaster.createdBy, appMaster.applicationDate, "
			+ "appMaster.kendraId,appMaster.kendraName, appMaster.workitemNo, appMaster.memberId, custdtls.customerName, appMaster.currentStageNo,'','','',appMaster.applicationStatus,appMaster.productCode) FROM "
			+ "ApplicationMaster appMaster "
			+ "left outer join CustomerDetails custdtls on appMaster.applicationId=custdtls.applicationId "
			/*
			 * +
			 * "left outer join CibilDetails cbdtls on custdtls.custDtlId=cbdtls.custDtlId "
			 */
			+ "where appMaster.branchId in :branchId AND appMaster.applicationStatus in :status "
			+ "AND (appMaster.relatedApplicationId is null OR appMaster.relatedApplicationId = '') "
			+ "and custdtls.customerType = 'Applicant' " + "order by appMaster.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchAllDashBoardDataBranchIdIn(@Param("status") List<String> status,
			@Param("branchId") List<String> branchId);

	@Query(value = "select new ApplicationMaster(appMaster.applicationId, appMaster.createdBy, appMaster.applicationDate, "
			+ "appMaster.kendraId,appMaster.kendraName, appMaster.workitemNo, appMaster.memberId, custdtls.customerName, appMaster.currentStageNo,'','','',appMaster.applicationStatus,appMaster.productCode) FROM "
			+ "ApplicationMaster appMaster "
			+ "left outer join CustomerDetails custdtls on appMaster.applicationId=custdtls.applicationId "
			/*
			 * +
			 * "left outer join CibilDetails cbdtls on custdtls.custDtlId=cbdtls.custDtlId "
			 */
			+ "where appMaster.branchId in :branchId AND appMaster.applicationStatus in :status "
			+ "AND (appMaster.relatedApplicationId is not null AND appMaster.relatedApplicationId!='') "
			+ "and custdtls.customerType = 'Applicant' " + "order by appMaster.createTs DESC", nativeQuery = false)
	public List<ApplicationMaster> fetchAllRenewalDashBoardDataBranchIdIn(@Param("status") List<String> status,
			@Param("branchId") List<String> branchId);

	@Modifying
	@Query(value = "UPDATE ApplicationMaster appMaster SET appMaster.lockTs = NULL WHERE appMaster.updatedBy = :updatedBy", nativeQuery = false)
	public int updateLockOut(@Param("updatedBy") String updatedBy);

	@Modifying
	@Query(value = "UPDATE ApplicationMaster appMaster SET appMaster.lockTs = current_timestamp, appMaster.updatedBy = :updatedBy "
			+ "WHERE appMaster.applicationId = :applicationId", nativeQuery = false)
	public int updateTimestampOnLogin(@Param("applicationId") String applicationId,
			@Param("updatedBy") String updatedBy);

	@Query(value = "SELECT CASE WHEN "
			+ "appMaster.productCode = '"+Constants.RENEWAL_LOAN_PRODUCT_CODE+"' THEN renewalLeadDetails.coCustomerId "
			+ "ELSE leadDetails.coCustomerId END FROM ApplicationMaster appMaster "
			+ "LEFT JOIN LeadDetails leadDetails ON appMaster.memberId = leadDetails.customerId "
			+ "LEFT JOIN RenewalLeadDetails renewalLeadDetails ON appMaster.memberId = renewalLeadDetails.customerId "
			+ "WHERE appMaster.memberId = :customerId", nativeQuery = false)
	public String getCoAppCustId(@Param("customerId") String customerId);
	
	
}
