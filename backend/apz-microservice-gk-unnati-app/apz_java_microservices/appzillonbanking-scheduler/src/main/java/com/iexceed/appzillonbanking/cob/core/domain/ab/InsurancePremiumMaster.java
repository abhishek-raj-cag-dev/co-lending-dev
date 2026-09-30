package com.iexceed.appzillonbanking.cob.core.domain.ab;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_ABOB_INSURANCE_PREMIUM_MASTER")
@Getter 
@Setter
@ToString
public class InsurancePremiumMaster {

    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "Loan_Amount")
    private int loanAmount;
    
    @Column(name = "TENURE_YEAR")
    private int tenureYear;

    @Column(name = "MEMBER_PREMIUM_RATE")
    private int memberPremiumRate;

    @Column(name = "Multiple")
    private int multiple;

    @Column(name = "ACTUAL_MEMBER_PREMIUM")
    private int actualMemberPremium;

    @Column(name = "COBORROWER_PREMIUM_RATE")
    private int coborrowerPremiumRate;

    @Column(name = "ACTUAL_COBORROWER_PREMIUM")
    private int actualCoborrowerPremium;
    
    @Column(name = "TOTAL_PREMIUM")
    private int totalPremium;

}
