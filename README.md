# sharepoint-integration
This project demonstrates how to integrate a Spring Boot application with Microsoft SharePoint to perform CRUD operations on files and folders using Microsoft Graph API.

# Setup Instructions

### Prerequisites

1. **Java 11 or later**
2. **Gradle**
3. **A Microsoft 365 account with access to SharePoint**
4. **Microsoft Azure app registration** with the necessary permissions to access SharePoint

### Clone the Repository

```sh
git clone https://github.com/your-username/sharepoint-integration.git
cd sharepoint-integration
```
### Configure Application Properties
Update the src/main/resources/application.properties file with your SharePoint and Azure app credentials

```sh
spring.application.name=shareapp
sharepoint.client-id=YOUR_CLIENT_ID
sharepoint.client-secret=YOUR_CLIENT_SECRET
sharepoint.tenant-id=YOUR_TENANT_ID
sharepoint.site-id=YOUR_SITE_ID
sharepoint.drive-id=YOUR_DRIVE_ID
```

## Build and Run the Application

```sh
./gradlew build
./gradlew bootRun
```

## Endpoints and Testing with Postman

1. Upload a File
  Endpoint: POST /api/sharepoint/upload

  Description: Upload a file to a specified folder in SharePoint.

  Parameters:

    folderId: The ID of the folder where the file will be uploaded.
    file: The file to upload.

  Postman Request:

    URL: http://localhost:8080/api/sharepoint/upload.
    In the Body tab, select form-data.
      Key: folderId (type: text, value: <YOUR_FOLDER_ID>).
      Key: file (type: file, choose a file from your local machine).

2. Download a File
   
  Endpoint: GET /api/sharepoint/download

  Description: Download a file from SharePoint by specifying the file path.

  Parameters:

    filePath: The path to the file in SharePoint.
  Postman Request:

    URL: http://localhost:8080/api/sharepoint/download?filePath=<YOUR_FILE_PATH>.

3. List Files in a Folder
   
  Endpoint: GET /api/sharepoint/files/{folderId}

  Description: List all files in a specified folder.

  Parameters:

    folderId: The ID of the folder.
  Postman Request:
  
    URL: http://localhost:8080/api/sharepoint/files/<YOUR_FOLDER_ID>.

4. Download All Files in a Folder
   
  Endpoint: GET /api/sharepoint/downloadFolder

  Description: Download all files in a specified folder as a ZIP file.

  Parameters:

    folderName: The name of the folder.
  Postman Request:
  
    URL: http://localhost:8080/api/sharepoint/downloadFolder?folderName=<YOUR_FOLDER_NAME>.

5. Delete a File

  Endpoint: DELETE /api/sharepoint/delete/{fileId}

  Description: Delete a file from SharePoint by specifying the file ID.

  Parameters:

    fileId: The ID of the file.
  Postman Request:

    URL: http://localhost:8080/api/sharepoint/delete/<YOUR_FILE_ID>.


