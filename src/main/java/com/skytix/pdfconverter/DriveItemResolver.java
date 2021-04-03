package com.skytix.pdfconverter;

import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;

import java.io.InputStream;

public interface DriveItemResolver {
    void register(GraphServiceClient<Request> graphClient) throws OneDriveException;
    UploadSession createUploadSession(String filename);
    public InputStream download(String itemId);
}
