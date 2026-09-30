package com.iexceed.appzillonbanking.cob.core.repository.ab;
 
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
 
import com.iexceed.appzillonbanking.cob.core.domain.ab.DeviationRATracker;
import com.iexceed.appzillonbanking.cob.core.domain.ab.DeviationRATrackerId;
 
@Repository
public interface DeviationRATrackerRepository extends JpaRepository<DeviationRATracker, DeviationRATrackerId> {
 
	List<DeviationRATracker> findByApplicationId(String applicationId);

	List<DeviationRATracker> findByApplicationIdOrderByCreateTsAsc(String applicationId);
	
	@Transactional
	int deleteByApplicationId(String applicationId);
	
    List<DeviationRATracker> findByApplicationIdAndApprovedStatus(String applicationId, String approvedStatus);

	Optional<DeviationRATracker> findByApplicationIdAndRecordId(String applicationId, String recordId);

	List<DeviationRATracker> findByApplicationIdAndRecordType(String applicationId, String recordType);

	List<DeviationRATracker> findByApplicationIdAndRecordTypeAndAuthority(String applicationId, String recordType,
			String authority);

	List<DeviationRATracker> findByApplicationIdAndRecordTypeIn(String applicationId, List<String> recordTypes);

}