package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.ErrorTrace;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ErrorTraceRepository  extends CrudRepository<ErrorTrace, Long> {
    Optional<ErrorTrace> findTopByUniqueDataOrderByCreatedTsDesc(String uniqueData);

    Optional<ErrorTrace> findTopByUniqueDataAndApplicationId(String uniqueData, String applicationId);
}

