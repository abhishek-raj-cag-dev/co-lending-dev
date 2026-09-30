package com.iexceed.appzillonbanking.cob.repository.apz;

import org.springframework.data.repository.CrudRepository;
import com.iexceed.appzillonbanking.cob.domain.apz.User;
import com.iexceed.appzillonbanking.cob.domain.apz.UserId;

public interface UserRepository extends CrudRepository<User, UserId> {

}
