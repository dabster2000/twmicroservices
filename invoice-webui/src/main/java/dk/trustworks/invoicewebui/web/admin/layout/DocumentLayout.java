package dk.trustworks.invoicewebui.web.admin.layout;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.web.admin.components.DocumentListImpl;
import dk.trustworks.invoicewebui.web.admin.components.DocumentUploadImpl;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI
@SpringComponent
public class DocumentLayout extends ResponsiveLayout {

    @Autowired
    public DocumentLayout(DocumentUploadImpl documentUpload, DocumentListImpl documentList) {
        ResponsiveRow row = this.addRow();
        row.addColumn().withDisplayRules(12, 12, 4, 4)
                .withComponent(documentUpload);
        row.addColumn().withDisplayRules(12, 12, 8, 8)
                .withComponent(documentList);
        documentList.reload();
    }
}
