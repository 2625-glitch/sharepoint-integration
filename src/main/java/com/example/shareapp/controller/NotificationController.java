//package com.example.shareapp.controller;
//
//import com.example.shareapp.model.ChangeLog;
//import com.example.shareapp.repository.ChangeLogRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/notifications")
//public class NotificationController {
//
//    @Autowired
//    private ChangeLogRepository changeLogRepository;
//
//
//
//    @PostMappingpublic String handleWebhook(@RequestBody(required = false) WebhookRequest webhookRequest, @RequestParam(required = false) String validationtoken){ if (validationtoken != null) { // Return the validation token to verify the webhookreturn validationtoken; } // Process the webhook notificationif (webhookRequest != null) { System.out.println("Received notification: " + webhookRequest); } // Return a response to acknowledge receipt of the notificationreturn "Received"; }
//
//
//
//
////        @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
////    public ResponseEntity<String> handleNotification(@RequestParam(value = "validationToken", required = false) String validationToken,
////                                                     @RequestBody Map<String, Object> payload) {
////        if (validationToken != null) {
////            return ResponseEntity.ok(validationToken);
////        }
////
////        List<Map<String, Object>> value = (List<Map<String, Object>>) payload.get("value");
////        for (Map<String, Object> change : value) {
////            String changeType = (String) change.get("changeType");
////            String itemId = (String) change.get("resourceData.id");
////
////            ChangeLog changeLog = new ChangeLog();
////            changeLog.setChangeType(changeType);
////            changeLog.setItemId(itemId);
////            changeLog.setTimestamp(java.time.LocalDateTime.now().toString());
////
////            changeLogRepository.save(changeLog);
////        }
////
////        return ResponseEntity.ok("recieved");
////    }
////}
