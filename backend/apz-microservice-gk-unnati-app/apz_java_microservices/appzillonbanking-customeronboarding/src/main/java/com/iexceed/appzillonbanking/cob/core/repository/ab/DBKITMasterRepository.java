package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.DBKITMaster;


@Repository
public interface DBKITMasterRepository extends JpaRepository<DBKITMaster, Integer>{

    List<DBKITMaster> findByStageName(String stageName);

    List<DBKITMaster> findByCategory(String category);
}

