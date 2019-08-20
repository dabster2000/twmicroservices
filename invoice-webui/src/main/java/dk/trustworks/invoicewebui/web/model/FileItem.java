package dk.trustworks.invoicewebui.web.model;

import com.google.common.base.Objects;
import com.vaadin.server.Resource;

public class FileItem {

    String name;
    Resource icon;

    public FileItem() {
    }

    public FileItem(String name, Resource icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Resource getIcon() {
        return icon;
    }

    public void setIcon(Resource icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileItem fileItem = (FileItem) o;
        return Objects.equal(getName(), fileItem.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}

