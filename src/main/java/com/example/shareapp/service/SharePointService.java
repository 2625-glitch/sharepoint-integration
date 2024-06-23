package com.example.shareapp.service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.example.shareapp.config.SharePointConfig;
import com.example.shareapp.model.FileWithName;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.DriveItemSearchParameterSet;
import com.microsoft.graph.requests.DriveItemCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SharePointService {

    private final GraphServiceClient<Request> graphClient;
    private final SharePointConfig sharePointConfig;

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
}
