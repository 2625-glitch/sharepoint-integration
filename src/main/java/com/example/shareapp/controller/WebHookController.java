package com.example.shareapp.controller;

import com.example.shareapp.config.SharePointConfig;
import com.example.shareapp.model.ChangeLog;
import com.example.shareapp.repository.ChangeLogRepository;
import com.example.shareapp.service.SharePointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class WebHookController {

    @Autowired
    private ChangeLogRepository changeLogRepository;

    @Autowired
    private SharePointService sharePointService;

    @Autowired
    private  SharePointConfig sharePointConfig;

    private static final String DELTA_LINK = "https://graph.microsoft.com/v1.0/sites/{site-id}/drives/{drive-id}/root/delta";


    private static final Logger logger = LoggerFactory.getLogger(WebHookController.class);

    @RequestMapping(value = "/webhook", method = RequestMethod.POST, headers = "Content-Type=text/plain;charset=utf-8")
    public ResponseEntity<String> handleValidationPlainText(@RequestParam("validationToken") String validationToken) {
        logger.info("Received validation request with token: {}", validationToken);
        return ResponseEntity.ok()
                .header("Content-Type", "text/plain;charset=utf-8")
                .body(validationToken);
    }

    @PostMapping(value = "/webhook", consumes = "application/json")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        logger.info("Received notification payload: {}", payload);

        List<Map<String, Object>> value = (List<Map<String, Object>>) payload.get("value");
        for (Map<String, Object> notification : value) {
            String subscriptionId = (String) notification.get("subscriptionId");
            String changeType = (String) notification.get("changeType");
            String resource = (String) notification.get("resource");
            String tenantId = (String) notification.get("tenantId");
            String timestamp = Instant.now().toString();

            logger.info("Subscription ID: {}", subscriptionId);
            logger.info("Change Type: {}", changeType);
            logger.info("Resource: {}", resource);
            logger.info("Tenant ID: {}", tenantId);
            logger.info("Timestamp: {}", timestamp);
            //fetch delta chnages
            String siteId = sharePointConfig.getSiteId();
            String driveId = sharePointConfig.getDriveId();
            String deltaLink = DELTA_LINK.replace("{site-id}", siteId).replace("{drive-id}", driveId);
            List<Map<String, Object>> deltaChanges = sharePointService.fetchDeltaChanges(deltaLink);

            logger.info("recieved delta changes are",deltaChanges);
            for (Map<String, Object> delta : deltaChanges) {
                String detailedChangeType = determineChangeType(delta);

                String itemId = (String) delta.get("id");
                String createdDateTime = (String) delta.get("createdDateTime");
                String lastModifiedDateTime = (String) delta.get("lastModifiedDateTime");

                saveChangeLog(subscriptionId, detailedChangeType, resource, itemId, tenantId, timestamp, createdDateTime, lastModifiedDateTime);
            }
        }


        return ResponseEntity.ok("Notification received and processed");

    }

    private String determineChangeType(Map<String, Object> delta) {
        if (delta.containsKey("deleted")) {
            return "deleted";
        }

        String itemId = (String) delta.get("id");
        Optional<ChangeLog> existingLog = changeLogRepository.findByItemId(itemId);
        if (existingLog.isEmpty()) {
            return "created";
        }

        return "updated";
    }


    private void saveChangeLog(String subscriptionId, String changeType, String resource, String itemId, String tenantId, String timestamp,String createdDateTime, String lastModifiedDateTime) {
        ChangeLog changeLog = new ChangeLog();
        changeLog.setSubscriptionId(subscriptionId);
        changeLog.setChangeType(changeType);
        changeLog.setResource(resource);
        changeLog.setItemId(itemId);
        changeLog.setTenantId(tenantId);
        changeLog.setTimestamp(timestamp);
        changeLog.setCreatedDateTime(createdDateTime);
        changeLog.setLastModifiedDateTime(lastModifiedDateTime);

        changeLogRepository.save(changeLog);

        logger.info("Saved change log: {}", changeLog);
    }



}
