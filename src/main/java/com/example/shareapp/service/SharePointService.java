package com.example.shareapp.service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.example.shareapp.config.SharePointConfig;
import com.example.shareapp.model.FileWithName;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.DriveItemSearchParameterSet;
import com.microsoft.graph.models.Subscription;
import com.microsoft.graph.requests.DriveItemCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SharePointService {

    private final GraphServiceClient<Request> graphClient;
    private final SharePointConfig sharePointConfig;
    private String currentSubscriptionId;

    @Autowired
    public SharePointService(SharePointConfig sharePointConfig) {
        this.sharePointConfig = sharePointConfig;

        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(sharePointConfig.getClientId())
                .clientSecret(sharePointConfig.getClientSecret())
                .tenantId(sharePointConfig.getTenantId())
                .build();

        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
                Collections.singletonList("https://graph.microsoft.com/.default"), clientSecretCredential);

        this.graphClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .buildClient();
    }

    public InputStream downloadFile(String filePath) {
        return graphClient
                .sites(sharePointConfig.getSiteId())
                .drives(sharePointConfig.getDriveId())
                .root()
                .itemWithPath(filePath)
                .content()
                .buildRequest()
                .get();
    }

    public DriveItem uploadFile(String folderId, MultipartFile file) throws IOException {
        InputStream fileInputStream = file.getInputStream();
        byte[] fileContent = fileInputStream.readAllBytes();

        return graphClient
                .sites(sharePointConfig.getSiteId())
                .drives(sharePointConfig.getDriveId())
                .items(folderId)
                .itemWithPath(Objects.requireNonNull(file.getOriginalFilename()))
                .content()
                .buildRequest()
                .put(fileContent);
    }


    public String getFolderIdByName(String folderName) {
        DriveItemSearchParameterSet searchParameterSet = DriveItemSearchParameterSet.newBuilder()
                .withQ(folderName)
                .build();

        List<DriveItem> items = graphClient
                .sites(sharePointConfig.getSiteId())
                .drives(sharePointConfig.getDriveId())
                .root()
                .search(searchParameterSet)
                .buildRequest()
                .get()
                .getCurrentPage();

        for (DriveItem item : items) {
            if (item.folder != null && item.name.equals(folderName)) {
                return item.id;
            }
        }
        return null;
    }


    public List<DriveItem> listFilesInFolder(String folderId) {
        return graphClient
                .sites(sharePointConfig.getSiteId())
                .drives(sharePointConfig.getDriveId())
                .items(folderId)
                .children()
                .buildRequest()
                .get()
                .getCurrentPage();
    }

    public FileWithName downloadFileWithFileName(String itemId, String fileName) {
        InputStream fileStream = graphClient
                .sites(sharePointConfig.getSiteId())
                .drives(sharePointConfig.getDriveId())
                .items(itemId)
                .content()
                .buildRequest()
                .get();
        return new FileWithName(fileStream, fileName);
    }

    public List<FileWithName> downloadAllFilesInFolder(String folderName) {
        String folderId = getFolderIdByName(folderName);
        if (folderId == null) {
            throw new IllegalArgumentException("Folder not found");
        }

        List<DriveItem> files = listFilesInFolder(folderId);
        return files.stream()
                .map(file -> downloadFileWithFileName(file.id, file.name))
                .collect(Collectors.toList());
    }


    public void deleteFile(String fileId) {
        graphClient
                .sites(sharePointConfig.getSiteId())
                .drives(sharePointConfig.getDriveId())
                .items(fileId)
                .buildRequest()
                .delete();
    }


    public List<Map<String, Object>> fetchDeltaChanges(String deltaLink) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(deltaLink, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> body = response.getBody();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> delta = (List<Map<String, Object>>) body.get("value");

            return delta != null ? delta : List.of();
        } else {
            throw new RuntimeException("Error fetching delta changes: " + response.getStatusCode() + " " + response.getBody());
        }
    }

    public String getAccessToken() {
        String tenantId = sharePointConfig.getTenantId();
        String clientId = sharePointConfig.getClientId();
        String clientSecret = sharePointConfig.getClientSecret();
        String scope = "https://graph.microsoft.com/.default";
        String tokenUrl = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("scope", scope);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            return (String) responseBody.get("access_token");
        } else {
            throw new RuntimeException("Error fetching access token: " + response.getStatusCode() + " " + response.getBody());
        }


}




}
