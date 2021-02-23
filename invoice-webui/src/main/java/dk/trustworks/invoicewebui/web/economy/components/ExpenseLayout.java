package dk.trustworks.invoicewebui.web.economy.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.utils.EconomicExcelUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.LocalDate;

import static com.vaadin.ui.Notification.Type.*;

@SpringComponent
@SpringUI
public class ExpenseLayout extends VerticalLayout {

    @Autowired
    public ExpenseLayout(PhotoService photoService, GlobalPhotoUploader globalPhotoUploader, NotificationManager notificationManager) {

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

        responsiveLayout.addRow().addColumn().withDisplayRules(12,12,6,6).withComponent(new PhotoUploader(photoService).init());
        responsiveLayout.addRow().addColumn().withDisplayRules(12, 12, 6 ,6).withComponent(globalPhotoUploader.init());
        responsiveLayout.addRow().addColumn().withDisplayRules(12, 12, 6 ,6).withComponent(notificationManager.init());
        this.addComponent(responsiveLayout);
    }

    public ExpenseLayout init() {
        return this;
    }

}
