package com.iexceed.appzillonbanking.cob.domain.ab;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Table(name = "TB_ABOB_WHITELISTED_BRANCHES")
@Entity
public class WhitelistedBranches {

    @Id
    @Column(name = "BRANCH_CODE")
    private String branchCode;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "UNNATI_ENABLED")
    private String unnatiEnabled;

    @Column(name = "RENEWAL_ENABLED")
    private String renewalEnabled;

    @Column(name = "BOT_API_ENABLED")
    private String botApiEnabled;
}
