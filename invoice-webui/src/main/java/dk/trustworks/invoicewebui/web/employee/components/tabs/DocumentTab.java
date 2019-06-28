package dk.trustworks.invoicewebui.web.employee.components.tabs;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.Document;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.DocumentType;
import dk.trustworks.invoicewebui.repositories.DocumentRepository;
import dk.trustworks.invoicewebui.web.employee.components.parts.DocumentImpl;
import dk.trustworks.invoicewebui.web.employee.components.parts.ImportantMessageBoxImpl;

@SpringUI
@SpringComponent
public class DocumentTab {

    private final DocumentRepository documentRepository;

    private ResponsiveRow messageRow;
    private ResponsiveRow documentCardsRow;
    private User user;

    public DocumentTab(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
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
        for (Document document : documentRepository.findByUseruuidAndType(user.getUuid(), DocumentType.CONTRACT)) {
            documentCardsRow.addColumn().withDisplayRules(12, 6, 4, 3).withComponent(new DocumentImpl(document));
        }
    }
}
