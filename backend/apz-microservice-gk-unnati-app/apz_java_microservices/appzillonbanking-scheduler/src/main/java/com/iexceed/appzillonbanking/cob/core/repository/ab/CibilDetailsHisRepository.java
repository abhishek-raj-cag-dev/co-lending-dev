package com.iexceed.appzillonbanking.cob.core.repository.ab;


import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.CibilDetailsHistory;

@Repository
public interface CibilDetailsHisRepository extends CrudRepository<CibilDetailsHistory, BigDecimal> {

    @Query(value = "SELECT CH FROM CibilDetailsHistory CH  " +
            " LEFT JOIN CustomerDetails CD ON CD.applicationId = CH.applicationId AND " +
            " CD.custDtlId = CH.custDtlId WHERE CH.applicationId = :applicationId " +
            " AND CD.customerType = :customerType AND CH.stage = :stage AND CH.subStage = :subStage " +
            " ORDER BY CH.historyTs DESC", nativeQuery = false)
    Page<CibilDetailsHistory> findByApplicationIdAndStageAndSubStage(String applicationId, String stage, String subStage, String customerType, Pageable page);


}
