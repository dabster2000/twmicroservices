package dk.trustworks.invoicewebui.web.employee.components.tabs;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.File;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.DocumentService;
import dk.trustworks.invoicewebui.web.employee.components.parts.DocumentImpl;
import dk.trustworks.invoicewebui.web.employee.components.parts.ImportantMessageBoxImpl;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI
@SpringComponent
public class DocumentTab {

    private final DocumentService documentService;

    private ResponsiveRow messageRow;
    private ResponsiveRow documentCardsRow;
    private User user;

    @Autowired
    public DocumentTab(DocumentService documentService) {
        this.documentService = documentService;
    }


    public ResponsiveLayout getTabLayout(User user) {
        this.user = user;
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        messageRow = responsiveLayout.addRow();

        documentCardsRow = responsiveLayout.addRow();
        createImportantMessgeBox();
        createEquipmentCards();
        return responsiveLayout;
    }

    private void createImportantMessgeBox() {
        messageRow.removeAllComponents();
        ImportantMessageBoxImpl importantMessageBox = new ImportantMessageBoxImpl("These are all your private", "documents at Trustworks").withHalftoneSecondline();

        messageRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(importantMessageBox);
    }

    private void createEquipmentCards() {
        documentCardsRow.removeAllComponents();
        for (File document : documentService.findDocumentsByUserUUID(user.getUuid())) {
            documentCardsRow.addColumn().withDisplayRules(12, 6, 4, 3).withComponent(new DocumentImpl(document, documentService ));
        }
    }
}
