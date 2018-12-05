package dk.trustworks.invoicewebui.web.employee.components.parts;

import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import dk.trustworks.invoicewebui.model.Document;
import org.vaadin.simplefiledownloader.SimpleFileDownloader;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;

public class DocumentImpl extends DocumentDesign {

    public DocumentImpl(Document document) {
        getImgIcon().setSource(new ThemeResource("images/icons/document-icon.svg"));
        getLblDate().setValue(document.getUploaddate().format(DateTimeFormatter.ofPattern("dd. MMMM yyyy")));
        getLblDescription().setValue(document.getName());
        getLblType().setValue(document.getType().name());

        getBtnDownload().setIcon(new ThemeResource("images/icons/download-icon.svg"));
        getBtnDownload().addClickListener(event -> {
            final StreamResource resource = new StreamResource(() -> new ByteArrayInputStream(document.getContent()), document.getFilename());
            SimpleFileDownloader downloader = new SimpleFileDownloader();
            addExtension(downloader);
            downloader.setFileDownloadResource(resource);
            downloader.download();
        });
    }
}
