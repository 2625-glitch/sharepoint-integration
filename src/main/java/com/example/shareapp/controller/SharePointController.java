package com.example.shareapp.controller;

import com.example.shareapp.model.FileWithName;
import com.example.shareapp.service.SharePointService;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.requests.DriveItemCollectionPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/sharepoint")
public class SharePointController {
    @Autowired
    private SharePointService sharePointService;

    @GetMapping("/files/{folderId}")
    public List<DriveItem> listFiles(@PathVariable String folderId) {
        return sharePointService.listFilesInFolder(folderId);
    }

    @PostMapping("/upload")
    public DriveItem uploadFile(@RequestParam("folderId") String folderId, @RequestParam("file") MultipartFile file) throws IOException {
        return sharePointService.uploadFile(folderId, file);
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String filePath) throws IOException {
        InputStream fileContent = sharePointService.downloadFile(filePath);
        InputStreamResource resource = new InputStreamResource(fileContent);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filePath.substring(filePath.lastIndexOf("/") + 1))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/downloadFolder")
    public ResponseEntity<InputStreamResource> downloadFolder(@RequestParam String folderName) throws IOException {
        List<FileWithName> files = sharePointService.downloadAllFilesInFolder(folderName);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream);

        for (FileWithName file : files) {
            ZipEntry zipEntry = new ZipEntry(file.getFileName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = file.getFileStream().readAllBytes();
            zipOut.write(bytes, 0, bytes.length);
            zipOut.closeEntry();
        }

        zipOut.close();

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + folderName + ".zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    @DeleteMapping("/delete/{fileId}")
    public void deleteFile(@PathVariable String fileId) {
        sharePointService.deleteFile(fileId);
    }

}
