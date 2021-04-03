package com.skytix.pdfconverter;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.tasks.LargeFileUploadResult;
import com.microsoft.graph.tasks.LargeFileUploadTask;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;

import java.io.*;
import java.util.UUID;

@Slf4j
public class OneDriveConverter {
    private final IAuthenticationProvider authenticationProvider;
    private final DriveItemResolver itemResolver;
    private final GraphServiceClient<Request> graphClient;

    @Builder
    public OneDriveConverter(IAuthenticationProvider aAuthenticationProvider, DriveItemResolver aItemResolver) throws OneDriveException {
        authenticationProvider = aAuthenticationProvider;
        itemResolver = aItemResolver;

        graphClient = GraphServiceClient.builder()
                .authenticationProvider(this.authenticationProvider)
                .buildClient();

        aItemResolver.register(graphClient);
    }

    public InputStream convert(File officeDocument) throws OneDriveException {

        try {
            return convert(new FileInputStream(officeDocument), officeDocument.getName(), officeDocument.length());

        } catch (FileNotFoundException aE) {
            throw new OneDriveException(aE);
        }

    }

    public InputStream convert(InputStream officeDocument, String filename, long size) throws OneDriveException {

        try {
            final String tempFilename = UUID.randomUUID().toString() + "_" + filename;
            final UploadSession uploadSession = itemResolver.createUploadSession(tempFilename);

            final LargeFileUploadTask<DriveItem> uploadTask = new LargeFileUploadTask<>(
                    uploadSession,
                    graphClient,
                    officeDocument,
                    size,
                    DriveItem.class
            );

            final LargeFileUploadResult<DriveItem> upload = uploadTask.upload();

            if (upload.responseBody != null) {
                return itemResolver.download(upload.responseBody.id);

            } else {
                throw new OneDriveException("Error while uploading document");
            }

        } catch (ClientException | IOException aE) {
            throw new OneDriveException(aE);
        }

    }

}
