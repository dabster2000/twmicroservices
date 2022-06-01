package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.File;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.DocumentService;
import dk.trustworks.invoicewebui.web.employee.model.DocumentWithOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.simplefiledownloader.SimpleFileDownloader;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringUI
@SpringComponent
public class DocumentListImpl extends DocumentList {

    @Autowired
    private DocumentService documentService;
    //private DocumentRepository documentRepository;

    private List<User> users;

    public DocumentListImpl() {
        getGridFiles().getColumn("name").setExpandRatio(10);
        getGridFiles().addSelectionListener(event -> {
            if(event.getAllSelectedItems().size()==0) {
                getBtnDownload().setEnabled(false);
                getBtnDelete().setEnabled(false);
            } else if(event.getAllSelectedItems().size() == 1) {
                getBtnDelete().setEnabled(true);
                getBtnDownload().setEnabled(true);
            } else if(event.getAllSelectedItems().size() > 1) {
                getBtnDownload().setEnabled(false);
                getBtnDelete().setEnabled(true);
            }
        });

        getBtnDownload().addClickListener(event -> {
            if(!getGridFiles().getSelectedItems().stream().findFirst().isPresent()) return;
            File document = getGridFiles().getSelectedItems().stream().findFirst().get();
            File documentWithFile = documentService.findDocumentByUUID(document.getUuid());
            final StreamResource resource = new StreamResource(() -> new ByteArrayInputStream(documentWithFile.getFile()), document.getFilename());
            //final StreamResource resource = new StreamResource(() -> new ByteArrayInputStream(document.getFile()), document.getName()+".pdf");

            SimpleFileDownloader downloader = new SimpleFileDownloader();
            addExtension(downloader);
            downloader.setFileDownloadResource(resource);
            downloader.download();
        });

        getBtnDelete().addClickListener(event -> {
            Set<DocumentWithOwner> selectedItems = getGridFiles().getSelectedItems();
            if(selectedItems.size()==0) return;
            for (File selectedItem : selectedItems) {
                documentService.deleteDocument(selectedItem.getUuid());
            }
            init(users);
        });
    }

    //userService.findByUUID(document.getRelateduuid(), true).getUsername())

    public void init(List<User> users) {
        this.users = users;
        List<DocumentWithOwner> documents = new ArrayList<>();
        for (User user : users) {
            List<File> docList = documentService.findDocumentsByUserUUID(user.getUuid());
            for (File file : docList) {
                documents.add(new DocumentWithOwner(file, user.getUsername()));
            }
        }
        getGridFiles().setItems(documents);
    }

}
