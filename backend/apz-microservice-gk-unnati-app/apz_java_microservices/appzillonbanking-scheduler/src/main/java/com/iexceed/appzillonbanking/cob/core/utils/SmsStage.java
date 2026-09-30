package com.iexceed.appzillonbanking.cob.core.utils;

import lombok.Getter;

public enum SmsStage {
    OTP(Constants.OTP),
    SANCTION(Constants.SANCTION),
    DISBURSED(Constants.DISBURSED),
    SOURCING_SUBMISSION(Constants.SOURCING_SUBMISSION),
    REJECTION(Constants.REJECTION);

    @Getter
    private final String templateActionType;

    SmsStage(String templateActionType) {
        this.templateActionType = templateActionType;
    }

}
