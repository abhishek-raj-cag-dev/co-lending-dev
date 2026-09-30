package com.iexceed.appzillonbanking.cob.core.repository.ab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.BCMPIMaster;
import java.util.List;


@Repository
public interface BCMPIMasterRepository extends JpaRepository<BCMPIMaster, Integer>{

    List<BCMPIMaster> findByStageName(String stageName);
}
