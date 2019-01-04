package dk.trustworks.invoicewebui.utils;

import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.enums.PhotoGlobalType;
import dk.trustworks.invoicewebui.repositories.PhotoGlobalRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@SpringComponent
@SpringUI
public class SpriteSheet {

    private final PhotoGlobalRepository photoGlobalRepository;

    int _imageCount = 0;
    int _animationStart = 0;
    int _spriteSize;

    BufferedImage[] sprites;

    public SpriteSheet(PhotoGlobalRepository photoGlobalRepository){
        this.photoGlobalRepository = photoGlobalRepository;
    }

    private void buildSprites(BufferedImage spriteSheet, int columns, int rows, int frameSize){
        sprites = new BufferedImage[40];
        for(int x = 0; x < columns; x++){
            for(int y = 0; y < rows; y++){
                sprites[(x * 6) + y] = spriteSheet.getSubimage(
                        x * (frameSize),  // my sprite software adds 1 pixel border to each frame
                        y * (frameSize),
                        frameSize,
                        frameSize
                );
            }
        }
    }

    public StreamResource getSprite(int imageNumber){
        int frameSize = 200;
        int columns = 6;
        int rows = 5;

        try{
            BufferedImage spriteSheet = ImageIO.read(new ByteArrayInputStream(photoGlobalRepository.findByType(PhotoGlobalType.ACHIEVEMENT).getPhoto()));
            buildSprites(spriteSheet, columns, rows, frameSize);
        }catch(IOException e){
            System.out.println("Image not found: Sprite2.java line 50");
        }

        return new StreamResource((StreamResource.StreamSource) () -> {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(sprites[imageNumber], "png", bos);
                return new ByteArrayInputStream(bos.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }, imageNumber+".png");
    }

    public BufferedImage getSprite(){
        return sprites[_imageCount];
    }
}

