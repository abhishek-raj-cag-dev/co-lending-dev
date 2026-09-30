package com.iexceed.appzillonbanking.cob.core.repository.apz;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cob.core.domain.apz.UserRole;
import com.iexceed.appzillonbanking.cob.core.domain.apz.UserRoleId;

public interface UserRoleRepository extends CrudRepository<UserRole, UserRoleId> {

	Optional<UserRole> findByAppIdAndUserId(String appId, String userId);

}
