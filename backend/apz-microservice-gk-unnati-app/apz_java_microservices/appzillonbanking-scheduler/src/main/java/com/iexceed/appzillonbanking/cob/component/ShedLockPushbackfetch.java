package com.iexceed.appzillonbanking.cob.component;

import java.io.FileReader;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.iexceed.appzillonbanking.cob.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.cob.core.utils.SpringCloudProperties;
import com.iexceed.appzillonbanking.cob.loans.service.LoanService;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
public class ShedLockPushbackfetch {
}
