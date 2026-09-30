package com.iexceed.appzillonbanking.cob.core.repository.ab;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.iexceed.appzillonbanking.cob.core.domain.ab.BCMPILoanObligations;

public interface BCMPILoanObligationsRepository extends JpaRepository<BCMPILoanObligations, String> {

}
