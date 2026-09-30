package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.Udhyam;
import com.iexceed.appzillonbanking.cob.core.domain.ab.UdhyamIdClass;
import org.springframework.transaction.annotation.Transactional;

public interface UdhyamRepository extends JpaRepository<Udhyam, UdhyamIdClass>{

    Optional<List<Udhyam>> findByApplicationId(String applicationId);

    @Transactional
    int deleteByApplicationId(String applicationId);

}
