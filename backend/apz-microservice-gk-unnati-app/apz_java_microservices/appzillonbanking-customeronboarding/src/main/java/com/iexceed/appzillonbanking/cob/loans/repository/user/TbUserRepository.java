package com.iexceed.appzillonbanking.cob.loans.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cob.loans.domain.user.TbAsmiUser;
import com.iexceed.appzillonbanking.cob.loans.domain.user.TbAsmiUserPK;


@Repository
public interface TbUserRepository extends CrudRepository<TbAsmiUser, TbAsmiUserPK>{
	
	Optional<TbAsmiUser> findByAppIdAndUserId(String appId, String userId);
	
	Optional<TbAsmiUser> findByAppIdAndUserIdAndUserActive(String appId, String userId, String userActive);
	
	@Query("SELECT u.userName FROM TbAsmiUser u WHERE u.userId = :userId")
	Optional<String> findUserNameByUserId(@Param("userId") String userId);	

	@Query("SELECT new com.iexceed.appzillonbanking.cob.loans.domain.user.TbAsmiUser$UserSummary(u.userId,u.userName, u.addInfo3) FROM TbAsmiUser u WHERE u.userId in (:userIds)")
	List<TbAsmiUser.UserSummary> findUserNamesByUserIdList(@Param("userIds") List<String> userIds);
	
	@Query("SELECT u.userName FROM TbAsmiUser u WHERE u.addInfo1 = :addInfo1 AND u.addInfo2 = :addInfo2")
	Optional<String> findUserNameByAppIdAndRoleAndBranchId(@Param("addInfo1") String addInfo1, @Param("addInfo2") String addInfo2);

//	@Query("SELECT new com.iexceed.appzillonbanking.cob.loans.domain.user.TbAsmiUser$UserSummary(u.userId,u.userName, u.addInfo3) FROM TbAsmiUser u WHERE u.appId =:appId and u.userId in (:userIds)")
//	List<TbAsmiUser.UserSummary> findUserNamesByUserIdList(@Param("userIds") List<String> userIds, @Param("appId") String appId);
	
}

	
