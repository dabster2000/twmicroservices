package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;

@Service
public class PhotoRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public PhotoRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    @Cacheable("photo")
    public Photo findPhotoByRelateduuid(String uuid) {
        String url = apiGatewayUrl+"/photos/"+uuid;
        ResponseEntity<Photo> result = systemRestService.secureCall(url, GET, Photo.class);
        return result.getBody();
    }

    @CacheEvict("photo")
    public void update(Photo photo) {
        String url = apiGatewayUrl+"/photos";
        systemRestService.secureCall(url, PUT, Void.class, photo);
    }
}
