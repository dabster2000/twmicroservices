package dk.trustworks.invoicewebui.web.common;

import com.vaadin.server.Resource;

public class ImageBoxImpl extends ImageBox {

    public ImageBox instance(Resource resource) {
        this.getImgFull().setSource(resource);
        return this;
    }

}
