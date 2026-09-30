package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.OCRDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface OCRDetailsRepository extends CrudRepository<OCRDetails, Long> {
    public Optional<OCRDetails> findFirstByCustDtlIdAndDocTypeAndDocSideOrderByCreatedAtDesc(BigDecimal custDtlId, String docType, String docSide);

}
