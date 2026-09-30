package com.iexceed.appzillonbanking.cob.core.repository.ab;

import com.iexceed.appzillonbanking.cob.core.domain.ab.BipDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BipDetailsRepository extends CrudRepository<BipDetails, String> {
        Optional<BipDetails> findByApplicationId(String applicationId);
}
