package dk.trustworks.invoicewebui.network.clients;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxTeamClientV2;
import com.dropbox.core.v2.files.*;
import com.dropbox.core.v2.sharing.DbxUserSharingRequests;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import dk.trustworks.invoicewebui.network.clients.model.DropboxFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hans on 16/09/2017.
 */

@Service
public class DropboxAPI {

    private static final Logger log = LoggerFactory.getLogger(DropboxAPI.class);

    private final DbxTeamClientV2 client;


    @Autowired
    public DropboxAPI(@Value("${dropboxToken}") final String dropboxToken) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        //new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        client = new DbxTeamClientV2(config, dropboxToken);
    }

    public List<String> getFilesInFolder(String path) {
        List<String> filePaths = new ArrayList<>();
        try {
            DbxUserFilesRequests files = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").files();
            ListFolderResult result = files.listFolder(path);
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    if(metadata instanceof FolderMetadata) continue;
                    filePaths.add(metadata.getPathLower());
                }

                if (!result.getHasMore()) {
                    break;
                }

                result = files.listFolderContinue(result.getCursor());
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return filePaths;
    }

    public List<SearchMatch> searchFiles(String query, long resultSize) {
        try {
            DbxUserFilesRequests files = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").files();
            SearchResult searchResult = (resultSize==0)?files.searchBuilder("/SHARED", query).withMode(SearchMode.FILENAME_AND_CONTENT).start():
                    files.searchBuilder("/SHARED", query).withMaxResults(resultSize).withMode(SearchMode.FILENAME_AND_CONTENT).start();
            return searchResult.getMatches();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        log.debug("no file");
        return new ArrayList<>();
    }

    public DropboxFile getRandomBinaryFile(String folder) {
        log.debug("DropboxAPI.getRandomBinaryFile");
        log.debug("folder = [" + folder + "]");
        try {
            DbxUserFilesRequests files = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").files();
            ListFolderResult result = files.listFolder(folder);
            List<Metadata> metadataList = result.getEntries().stream()
                    .filter(p -> FilenameUtils.isExtension(p.getName(), "jpg")).collect(Collectors.toList());
            Metadata metadata = metadataList.get(new Random().nextInt(metadataList.size()));
            DbxDownloader<FileMetadata> thumbnail = files.download(metadata.getPathLower());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            thumbnail.download(outputStream);
            return new DropboxFile(outputStream.toByteArray(), metadata.getPathLower());
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
        log.debug("no file");
        return new DropboxFile(new byte[0], "");
    }

    public byte[] getSpecificBinaryFile(String filePath) {
        log.debug("DropboxAPI.getSpecificBinaryFile");
        log.debug("filePath = [" + filePath + "]");
        try {
            DbxUserFilesRequests files = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").files();
            DbxDownloader<FileMetadata> file = files.download(filePath);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            file.download(outputStream);
            return outputStream.toByteArray();

        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
        log.debug("no file");
        return new byte[0];
    }

    public Resource getThumbnail(String filePath) {
        log.debug("DropboxAPI.getThumbnail");
        log.debug("filePath = [" + filePath + "]");

        try {
            DbxDownloader<FileMetadata> thumbnail = client.asMember("").files().getThumbnail(filePath);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            thumbnail.download(outputStream);
            return new StreamResource((StreamResource.StreamSource) () -> new ByteArrayInputStream(outputStream.toByteArray()),new Random(1000000)+".jpg");
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }

        return new ThemeResource("images/clients/missing-logo.jpg");
    }

    public String getSpecificTextFile(String filePath) {
        return getSpecificTextFile(filePath, StandardCharsets.ISO_8859_1);
    }

    public String getSpecificTextFile(String filePath, Charset charset) {
        log.debug("filePath = [" + filePath + "]");
        try {
            DbxUserFilesRequests files = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").files();
            DbxDownloader<FileMetadata> file = files.download(filePath);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            file.download(outputStream);
            return new String(outputStream.toByteArray(), charset);
        } catch (DbxException | IOException e) {
            log.warn("'"+filePath+"' not found!");
        }
        log.debug("no text file will be shown");
        return "";
    }

    public void uploadInvoice(StreamResource resource, LocalDate invoicedate) {
        // /Shared/Administration/Fakturering/2019/201904 April
        DbxUserFilesRequests files = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").files();

        try {
            files.uploadBuilder("/Shared/Administration/Fakturering/" +
                    invoicedate.getYear() + "/" +
                    invoicedate.format(DateTimeFormatter.ofPattern("yyyyMM")) + " " +
                    WordUtils.capitalize(invoicedate.format(DateTimeFormatter.ofPattern("MMMM"))) + "/" +
                    resource.getFilename()).uploadAndFinish(resource.getStreamSource().getStream());
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileURL(String filePath) {
        log.debug("DropboxAPI.getFileURL");
        log.debug("filePath = [" + filePath + "]");
        //String relativeFilePath = filePath.replace("/Users/hans/Dropbox (TrustWorks ApS)","");
        //CacheHandler cache = CacheHandler.createCacheHandler();

        SharedLinkMetadata sharedLink = null;
        try {

            Map<String, String> urls = new HashMap<>();
                DbxUserSharingRequests sharing = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").sharing();
                for (SharedLinkMetadata url : sharing.listSharedLinks().getLinks()) {
                    urls.put(url.getPathLower(), url.getUrl());
                    log.debug("url.getPathLower() = " + url.getPathLower() + " | url.getUrl() = " + url.getUrl());
                }

            if(urls.containsKey(filePath.toLowerCase())) {
                log.debug("filePath.toLowerCase() = " + filePath.toLowerCase());
                log.debug("urls.get(filePath.toLowerCase()) = " + urls.get(filePath.toLowerCase()));
                return urls.get(filePath.toLowerCase());
            }

/*
            DbxUserSharingRequests sharing = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").sharing();
            for (SharedLinkMetadata linkMetadata : sharing.listSharedLinks().getLinks()) {
                if(linkMetadata.getPathLower().equals(filePath.toLowerCase())) return linkMetadata.getUrl();
            }
*/

            DbxUserSharingRequests sharing2 = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").sharing();
            sharedLink = sharing2.createSharedLinkWithSettings(filePath);
            String url = sharedLink.getUrl();
            urls.put(filePath.toLowerCase(), url);
            log.debug("sharedLink.getUrl() = " + url);
            return url;
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return "";
    }
}