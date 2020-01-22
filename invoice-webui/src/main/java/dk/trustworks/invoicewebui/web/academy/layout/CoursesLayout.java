package dk.trustworks.invoicewebui.web.academy.layout;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.functions.TokenEventListener;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.network.clients.DropboxAPI;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.repositories.KnowArchiColumnRepository;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.ProjectService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.common.ImageCardDesign;
import dk.trustworks.invoicewebui.web.knowledge.components.ArchitectureCell;
import dk.trustworks.invoicewebui.web.knowledge.components.SideBannerDesign;
import dk.trustworks.invoicewebui.web.model.FileItem;
import dk.trustworks.invoicewebui.web.photoupload.components.PhotoUploader;
import dk.trustworks.invoicewebui.web.project.components.TokenListImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.vaadin.ui.themes.ValoTheme.COMBOBOX_BORDERLESS;

@SpringComponent
@SpringUI
public class CoursesLayout extends VerticalLayout {

    public CoursesLayout init() {

        return this;
    }
}