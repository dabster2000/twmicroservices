package dk.trustworks.invoicewebui.utils;

import com.vaadin.server.StreamResource;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Convert a PDF document to an image.
 *
 * @author <a href="ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.6 $
 */
public class PDFToImage {

    private PDFToImage() {
        //static class
    }

    public static StreamResource createImage(InputStream src) throws Exception {
        return new StreamResource((StreamResource.StreamSource) () -> {
            try {
                PDDocument document = PDDocument.load(src);
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);

                document.close();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(bim, "png", bos);
                return new ByteArrayInputStream(bos.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }, "dateImage.png");
    }

}