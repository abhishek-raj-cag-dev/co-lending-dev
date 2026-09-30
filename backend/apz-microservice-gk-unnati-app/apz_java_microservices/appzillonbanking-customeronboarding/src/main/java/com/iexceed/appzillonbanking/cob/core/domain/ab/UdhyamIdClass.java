package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class UdhyamIdClass implements Serializable{

    private static final long serialVersionUID = 1L;

    @Column(name = "APPLICATION_ID")
    private String applicationId;

    @Column(name = "CUSTOMER_TYPE")
    private String customerType;
}
