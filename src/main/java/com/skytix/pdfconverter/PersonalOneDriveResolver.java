package com.skytix.pdfconverter;

import com.microsoft.graph.models.DriveItemCreateUploadSessionParameterSet;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;

import java.io.InputStream;

public class PersonalOneDriveResolver implements DriveItemResolver {
    private final String remoteFolder;
    private GraphServiceClient<Request> graphClient;


    public PersonalOneDriveResolver(String aRemoteFolder) {
        remoteFolder = aRemoteFolder;
    }

    @Override
    public void register(GraphServiceClient<Request> graphClient) throws OneDriveException {
        this.graphClient = graphClient;

    }

    @Override
    public UploadSession createUploadSession(String filename) {

        return graphClient.me()
                .drive()
                .root()
                .itemWithPath(remoteFolder + "/" + filename)
                .createUploadSession(DriveItemCreateUploadSessionParameterSet.newBuilder().build())
                .buildRequest().post();
    }

    @Override
    public InputStream download(String itemId) {

        return new CustomOnCloseInputStream(

                graphClient
                        .me()
                        .drive()
                        .items(itemId)
                        .content()
                        .buildRequest(
                                new QueryOption("format", "pdf")
                        ).get(),
                () -> cleanup(itemId)
        );

    }

    private void cleanup(String itemId) {

        graphClient.me()
                .drive()
                .items(itemId)
                .buildRequest()
                .delete();

    }

}
