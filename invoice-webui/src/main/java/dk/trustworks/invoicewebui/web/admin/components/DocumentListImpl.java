package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.Document;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.DocumentType;
import dk.trustworks.invoicewebui.repositories.DocumentRepository;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.simplefiledownloader.SimpleFileDownloader;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Set;

@SpringUI
@SpringComponent
public class DocumentListImpl extends DocumentList {

    @Autowired
    private DocumentRepository documentRepository;

    private User user;

    public DocumentListImpl() {
        user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();

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
            Document document = getGridFiles().getSelectedItems().stream().findFirst().get();
            final StreamResource resource = new StreamResource(() -> new ByteArrayInputStream(document.getContent()), document.getName()+".pdf");

            SimpleFileDownloader downloader = new SimpleFileDownloader();
            addExtension(downloader);
            downloader.setFileDownloadResource(resource);
            downloader.download();
        });

        getBtnDelete().addClickListener(event -> {
            Set<Document> selectedItems = getGridFiles().getSelectedItems();
            if(selectedItems.size()==0) return;
            for (Document selectedItem : selectedItems) {
                documentRepository.delete(selectedItem.getId());
            }
            reload();
        });
    }

    public void reload() {
        List<Document> documents = documentRepository.findByUserAndType(user, DocumentType.CONTRACT);
        getGridFiles().setItems(documents);
    }

}
