package dk.trustworks.invoicewebui.web.admin.layout;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.web.admin.components.DocumentListImpl;
import dk.trustworks.invoicewebui.web.admin.components.DocumentUploadImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringUI
@SpringComponent
public class DocumentLayout extends ResponsiveLayout {

    private DocumentUploadImpl documentUpload;
    private final DocumentListImpl documentList;

    @Autowired
    public DocumentLayout(DocumentUploadImpl documentUpload, DocumentListImpl documentList) {
        this.documentUpload = documentUpload;
        this.documentList = documentList;
        ResponsiveRow row = this.addRow();
        row.addColumn().withDisplayRules(12, 12, 4, 4)
                .withComponent(documentUpload);
        row.addColumn().withDisplayRules(12, 12, 8, 8)
                .withComponent(documentList);
    }

    public Component init(List<User> users) {
        documentList.init(users);
        documentUpload.init(users);
        return this;
    }
}
