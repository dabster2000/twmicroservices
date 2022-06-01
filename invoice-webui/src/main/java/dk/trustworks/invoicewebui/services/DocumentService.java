package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.File;
import dk.trustworks.invoicewebui.network.rest.FileRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private FileRestService fileRestService;

    public List<File> findDocumentsByUserUUID(String useruuid) {
        return fileRestService.findDocumentsByUserUUID(useruuid);
    }

    public List<File> findDocuments() {
        return fileRestService.findDocuments();
    }

    public File findDocumentByUUID(String uuid) {
        return fileRestService.findDocumentByUUID(uuid);
    }

    public void saveDocument(String useruuid, File document) {
        fileRestService.saveDocument(useruuid, document);
    }

    public void deleteDocument(String uuid) {
        fileRestService.deleteDocument(uuid);
    }

}
