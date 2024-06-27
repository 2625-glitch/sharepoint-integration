package com.example.shareapp.controller;


import com.example.shareapp.model.WebhookRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
public class WebHookController {


    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleWebhook(@RequestBody(required = false) WebhookRequest webhookRequest, @RequestParam(required = false) String validationtoken) {
        System.out.println("validation token"+validationtoken);
        if (validationtoken != null) {
            // Return the validation token to verify the webhook
            System.out.println("validation token is"+validationtoken);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(validationtoken);

        }

        // Process the webhook notification
        if (webhookRequest != null) {
            System.out.println("Received notification: " + webhookRequest);
        }

        // Return a response to acknowledge receipt of the notification
        return ResponseEntity.ok("Received");
    }
}
