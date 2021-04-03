# onedrive-pdf-converter

Small library to use OneDrive or Sharepoint as a PDF conversion conversion service.  Uses the Microsoft Graph API to upload office documents and download them as a PDF file.

There will be more validation to come.

## Gradle Import
```
implementation 'au.com.skytix:onedrive-pdf-converter:1.0.0'
```

## Sample

```
final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
        .clientId("") // Application client ID as registered in your 'Add Registrations' in AAD
        .clientSecret("") // Secret configured on App.
        .tenantId("") // Your AAD Tenant OD
        .build();

final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(clientSecretCredential);

final OneDriveConverter converter = OneDriveConverter.builder()
        .aItemResolver(new SharepointSiteResolver("Sharepoint Site Name", "some/path"))
        .aAuthenticationProvider(tokenCredentialAuthProvider)
        .build();

try (InputStream convert = converter.convert(new File("myoffice.docx"));
     FileOutputStream fos = new FileOutputStream("output.pdf")) {            

    convert.transferTo(fos);
} // Closing the returned InputStream will delete the file out of the source location.

```
