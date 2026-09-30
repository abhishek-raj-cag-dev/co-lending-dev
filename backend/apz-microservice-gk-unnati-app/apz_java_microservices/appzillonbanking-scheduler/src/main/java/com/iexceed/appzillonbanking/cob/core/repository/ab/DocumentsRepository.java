package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.Documents;
import com.iexceed.appzillonbanking.cob.core.domain.ab.DocumentsId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DocumentsRepository extends JpaRepository<Documents, DocumentsId>{

	Optional<Documents> findByApplicationIdAndDocTypeAndUploadType(String applicationId, String documentType, String uploadType);

    List<Documents> findByApplicationIdAndDocTypeIn(String applicationId, List<String> documentTypes);

    Optional<List<Documents>> findByApplicationId(String applicationId);

    Optional<List<Documents>> findByApplicationIdAndUploadType(String applicationId, String uploadType);

    @Modifying
    @Transactional
    long deleteByApplicationIdAndDocTypeAndUploadType(String applicationId, String documentType, String uploadType);
}
