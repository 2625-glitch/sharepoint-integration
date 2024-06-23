package com.example.shareapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharePointConfig {
    @Value("${sharepoint.client-id}")
    private String clientId;

    @Value("${sharepoint.client-secret}")
    private String clientSecret;

    @Value("${sharepoint.tenant-id}")
    private String tenantId;

    @Value("${sharepoint.site-id}")
    private String siteId;

    @Value("${sharepoint.drive-id}")
    private String driveId;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }
}
