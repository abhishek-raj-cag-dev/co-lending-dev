package com.iexceed.appzillonbanking.cob.loans.repository.user;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cob.loans.domain.user.StateDataDetails;

public interface StateDetailsRepository extends CrudRepository<StateDataDetails, Integer> {

	public List<StateDataDetails> findAll();
}
