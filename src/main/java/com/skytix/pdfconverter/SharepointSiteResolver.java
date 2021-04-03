package com.skytix.pdfconverter;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.DriveItemCreateUploadSessionParameterSet;
import com.microsoft.graph.models.Site;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.SiteCollectionPage;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;

import java.io.InputStream;

@Slf4j
public class SharepointSiteResolver implements DriveItemResolver {
    private final String siteName;
    private final String remoteFolder;

    private GraphServiceClient<Request> graphClient;
    private String siteId;

    public SharepointSiteResolver(String aSiteName, String aRemoteFolder) {
        siteName = aSiteName;
        remoteFolder = aRemoteFolder;
    }

    @Override
    public void register(GraphServiceClient<Request> aGraphServiceClient) throws OneDriveException {
        this.graphClient = aGraphServiceClient;

        SiteCollectionPage currentPage = graphClient.sites().buildRequest().get();

        if (currentPage != null) {

            while (currentPage != null) {

                for (Site site : currentPage.getCurrentPage()) {

                    if (site.name != null && site.name.equals(siteName)) {
                        siteId = site.id;
                        break;
                    }

                }

                if (currentPage.getNextPage() != null) {
                    currentPage = currentPage.getNextPage().buildRequest().get();

                } else {
                    currentPage = null;
                }

            }

            if (siteId == null) {
                throw new OneDriveException(String.format("Unable to fine site named '%s'", siteName));
            }

        } else {
            throw new OneDriveException("There are no sites configured");
        }

    }

    @Override
    public UploadSession createUploadSession(String filename) {

        return graphClient
                .sites().byId(siteId)
                .drive()
                .root()
                .itemWithPath(remoteFolder + "/" + filename)
                .createUploadSession(DriveItemCreateUploadSessionParameterSet.newBuilder().build())
                .buildRequest()
                .post();

    }

    public InputStream download(String itemId) {

        return new CustomOnCloseInputStream(
                graphClient
                        .sites().byId(siteId)
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

        try {
            graphClient
                    .sites().byId(siteId)
                    .drive()
                    .items(itemId)
                    .buildRequest().delete();

        } catch (ClientException aE) {
            log.error(String.format("Unable to delete remove item '%s' from site '%s'", itemId, siteId));
        }

    }

}
