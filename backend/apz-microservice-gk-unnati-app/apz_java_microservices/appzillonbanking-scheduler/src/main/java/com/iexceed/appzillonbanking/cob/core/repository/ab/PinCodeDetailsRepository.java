package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cob.core.domain.ab.PinCodeDetails;

@Repository
public interface PinCodeDetailsRepository extends JpaRepository<PinCodeDetails, Integer>{

   public Optional<PinCodeDetails> findByPinCode(Integer valueOf);

}
