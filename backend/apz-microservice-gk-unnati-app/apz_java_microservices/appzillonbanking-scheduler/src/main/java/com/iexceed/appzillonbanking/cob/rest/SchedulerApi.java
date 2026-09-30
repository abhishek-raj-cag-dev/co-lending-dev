package com.iexceed.appzillonbanking.cob.rest;

import com.iexceed.appzillonbanking.cob.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.cob.service.SchedulerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cbscheduler")
@Component
@Api(tags = "SCHEDULER", value = "/cbscheduler")
public class SchedulerApi {
    private static final Logger logger = LogManager.getLogger(SchedulerApi.class);

    private final SchedulerService schedulerService;

    public SchedulerApi(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    /**
     * Example curl command to trigger the scheduler manually:
     * curl -X POST "http://localhost:8881/appzillonbankingcob/cbscheduler/run" -H "Content-Type: application/json" & tail -f nohup.out
     */
    @PostMapping("/run")
    @Operation(
            summary = "Run CBS Scheduler Manually",
            description = "This endpoint triggers the CBS Scheduler manually for on-demand execution."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Scheduler executed successfully", response = ResponseWrapper.class),
            @ApiResponse(code = 500, message = "Scheduler job failed", response = ResponseWrapper.class)
    })
    public ResponseEntity<String> runCBSchedulerManually() {
        logger.info("Start: runCBSchedulerManually");
        try {
            logger.debug("Inside runCBSchedulerManually");
            schedulerService.breSchedulerCheck();
            return ResponseEntity.ok("Scheduler executed successfully");
        } catch (Exception e) {
            logger.error("Exception in runCBSchedulerManually: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Scheduler job failed: " + e.getMessage());
        }
    }
}
