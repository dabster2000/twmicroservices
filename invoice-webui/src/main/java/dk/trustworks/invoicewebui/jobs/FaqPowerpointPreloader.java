package dk.trustworks.invoicewebui.jobs;

import dk.trustworks.invoicewebui.network.clients.DropboxAPI;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FaqPowerpointPreloader {

    private static final Logger log = LoggerFactory.getLogger(FaqPowerpointPreloader.class);

    private final DropboxAPI dropboxAPI;

    @Autowired
    public FaqPowerpointPreloader(DropboxAPI dropboxAPI) {
        this.dropboxAPI = dropboxAPI;
    }

    @PostConstruct
    public void onStartup() {
        //loadNewPhoto();
        //loadFaqPDFs();
        //loadSlidephotos();
    }

    private Map<String, List<byte[]>> slidesShows = new HashMap<>();

    @Scheduled(cron = "0 1 1 * * ?")
    public void loadNewPhoto() {
        List<String> powerpointPaths = dropboxAPI.getFilesInFolder("/Shared/Administration/Intra/faq/pptx");
        for (String powerpointPath : powerpointPaths) {
            String[] filenameParts = powerpointPath.split("/");
            String filename = filenameParts[filenameParts.length-1].split("\\.")[0];
            byte[] specificFile = dropboxAPI.getSpecificBinaryFile(powerpointPath);

            try {

                XMLSlideShow ppt = new XMLSlideShow(new ByteArrayInputStream(specificFile));

                double zoom = 1.0; // magnify it by 2
                AffineTransform at = new AffineTransform();
                at.setToScale(zoom, zoom);
                //getting the dimensions and size of the slide
                Dimension pgsize = ppt.getPageSize();
                List<XSLFSlide> slide = ppt.getSlides();

                slidesShows.put(filename, new ArrayList<>());

                for (XSLFSlide aSlide : slide) {
                    BufferedImage img = new BufferedImage((int) Math.ceil(pgsize.width * zoom), (int) Math.ceil(pgsize.height * zoom), BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = img.createGraphics();
                    graphics.setPaint(Color.white);
                    graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
                    aSlide.draw(graphics);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ImageIO.write(img, "png", out);
                    ppt.write(out);

                    byte[] bytes = out.toByteArray();

                    slidesShows.get(filename).add(bytes);

                    log.debug("Image successfully created");
                    out.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    //private Map<String, String> pdfs = new HashMap<>();
/*
    @Scheduled(cron = "0 1 1 * * ?")
    public void loadFaqPDFs() {
        List<String> powerpointPaths = dropboxAPI.getFilesInFolder("/Shared/Administration/Intra/faq/pdf");
        for (String powerpointPath : powerpointPaths) {
            String[] filenameParts = powerpointPath.split("/");
            String filename = filenameParts[filenameParts.length-1].split("\\.")[0];
            //byte[] specificFile = dropboxAPI.getSpecificBinaryFile(powerpointPath);
            //pdfs.put(filename, powerpointPath);
        }
    }
    */
}
