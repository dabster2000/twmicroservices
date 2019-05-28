package dk.trustworks.invoicewebui.web.model;

import com.vaadin.server.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileItem {

    String name;
    Resource icon;

}

