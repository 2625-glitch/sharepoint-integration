package com.example.shareapp.model;

import lombok.Data;

@Data
public class WebhookRequest {
    private String subscriptionId;
    private String clientState;
    private String expirationDateTime;
    private String resource;
    private String tenantId;
    private String siteUrl;
    private String webId;
    private String[] resourceData;
}
