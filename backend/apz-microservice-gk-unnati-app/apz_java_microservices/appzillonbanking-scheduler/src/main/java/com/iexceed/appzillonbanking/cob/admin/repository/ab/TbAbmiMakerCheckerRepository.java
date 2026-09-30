package com.iexceed.appzillonbanking.cob.admin.repository.ab;

import com.iexceed.appzillonbanking.cob.admin.domain.ab.TbAbmiMakerChecker;
import com.iexceed.appzillonbanking.cob.admin.domain.ab.TbAbmiMakerCheckerIds;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface TbAbmiMakerCheckerRepository extends CrudRepository<TbAbmiMakerChecker, TbAbmiMakerCheckerIds> {
	
	Optional<TbAbmiMakerChecker> findById(String id);

	List<TbAbmiMakerChecker> findByFeatureId(String featureId);
	
	@Transactional
	@Modifying
	@Query("DELETE TbAbmiMakerChecker mc WHERE mc.id=:id AND mc.featureId=:featureId")
	void deleteMakerCheckerPayloadByIdAndFeatureId(@Param("id") String id, @Param("featureId") String featureId);
	
	@Transactional
	@Modifying
	@Query("DELETE TbAbmiMakerChecker mc WHERE mc.featureId=:featureId")
	void deleteMakerCheckerPayloadByFeatureId(@Param("featureId") String featureId);

	List<TbAbmiMakerChecker> findByMakerIdNotAndAuthStatusNot(String userId, String string);
}