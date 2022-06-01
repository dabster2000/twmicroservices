package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Service
public class FileRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public FileRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    @Cacheable("photo")
    public File findPhotoByRelateduuid(String relateduuid) {
        String url = apiGatewayUrl+"/files/photos/"+relateduuid;
        ResponseEntity<File> result = systemRestService.secureCall(url, GET, File.class);
        return result.getBody();
    }

    @Cacheable("photo")
    public File findPhotoByUseruuid(String useruuid) {
        String url = apiGatewayUrl+"/users/"+useruuid+"/photo";
        ResponseEntity<File> result = systemRestService.secureCall(url, GET, File.class);
        return result.getBody();
    }

    @CacheEvict("photo")
    public void update(File photo) {
        System.out.println("FileRestService.update");
        String url = apiGatewayUrl+"/files/photos";
        System.out.println("url = " + url);
        systemRestService.secureCall(url, PUT, Void.class, photo);
    }

    public List<File> findDocumentsByUserUUID(String useruuid) {
        String url = apiGatewayUrl+"/users/"+useruuid+"/documents";
        ResponseEntity<File[]> result = systemRestService.secureCall(url, GET, File[].class);
        return Arrays.asList(result.getBody());
    }

    public List<File> findDocuments() {
        String url = apiGatewayUrl+"/files/documents";
        ResponseEntity<File[]> result = systemRestService.secureCall(url, GET, File[].class);
        return Arrays.asList(result.getBody());
    }

    public File findDocumentByUUID(String uuid) {
        String url = apiGatewayUrl+"/files/documents/"+uuid;
        ResponseEntity<File> result = systemRestService.secureCall(url, GET, File.class);
        return result.getBody();
    }

    public void saveDocument(String useruuid, File document) {
        System.out.println("FileRestService.saveDocument");
        String url = apiGatewayUrl+"/users/"+useruuid+"/documents";
        System.out.println("url = " + url);
        systemRestService.secureCall(url, POST, Void.class, document);
    }

    public void deleteDocument(String uuid) {
        System.out.println("FileRestService.deleteDocument");
        String url = apiGatewayUrl+"/files/documents/"+uuid;
        System.out.println("url = " + url);
        systemRestService.secureCall(url, DELETE, Void.class);
    }
}
