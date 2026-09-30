package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationDocuments;

@Repository
public interface ApplicationDocumentsRepository extends CrudRepository<ApplicationDocuments, BigDecimal> {

	public List<ApplicationDocuments> findByApplicationIdAndAppIdAndVersionNumAndStatus(String applicationId,
			String appId, int versionNum, String status);

	@Transactional
	public void deleteByApplicationIdAndAppId(String applicationId, String appId);

	@Transactional
	public void deleteByApplicationIdAndCustDtlId(String applicationId, BigDecimal custDtlId);

	public ApplicationDocuments findByApplicationIdAndAppIdAndVersionNumAndCustDtlId(String applicationId, String appId,
			int versionNum, BigDecimal custDtlId);

	public List<ApplicationDocuments> findByApplicationIdAndAppId(String applicationId, String appId);
	
	public List<ApplicationDocuments> findByApplicationIdAndAppIdAndVersionNumAndStatusAndCustDtlId(
			String applicationId, String appId, int versionNum, String status, BigDecimal custDtlId);

	public Optional<List<ApplicationDocuments>> findByApplicationIdAndCustDtlId(String applicationId, BigDecimal custDtlId);

	@Query(value = "SELECT documents FROM ApplicationDocuments documents "
			+ "LEFT OUTER JOIN CustomerDetails customerDetails ON documents.applicationId = :applicationId and documents.applicationId = customerDetails.applicationId "
			+ "AND documents.custDtlId = customerDetails.custDtlId "
			+ "WHERE customerDetails.customerType = :customerType", nativeQuery = false)
	public Optional<List<ApplicationDocuments>> findByApplicationIdAndCustType(
			@Param("customerType") String customerType, @Param("applicationId") String applicationId);


    @Query(value = "SELECT documents FROM ApplicationDocuments documents "
            + "LEFT OUTER JOIN CustomerDetails customerDetails ON documents.applicationId = :applicationId and documents.applicationId = customerDetails.applicationId "
            + "AND documents.custDtlId = customerDetails.custDtlId "
            + "WHERE customerDetails.customerType = :customerType and documents.documentType = :documentType", nativeQuery = false)
	public Optional<List<ApplicationDocuments>> findByApplicationIdAndCustTypeAndDocType(
            @Param("customerType") String customerType, @Param("applicationId") String applicationId, @Param("documentType") String documentType);


    List<ApplicationDocuments> findByApplicationIdAndCustDtlIdAndDocLevelAndDocumentType(
            String applicationId,
            BigDecimal custDtlId,
            String docLevel,
            String documentType
    );

	@Query(value = "SELECT documents FROM ApplicationDocuments documents WHERE documents.applicationId = :applicationId AND documents.documentType = :documentType AND documents.custType = :customerType", nativeQuery = false)
	public Optional<List<ApplicationDocuments>> applicationDocsRecords(
			@Param("customerType") String customerType, @Param("applicationId") String applicationId, @Param("documentType") String documentType);

}
