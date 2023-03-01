import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics;

public class Converter {
    
    public Converter() {
        // Open Folder
        File   inputDirectory   = new File("input");
        File[] inputImagesPaths = inputDirectory.listFiles();

        for (File f : inputImagesPaths) {
            BufferedImage   currentImage    = this.openImage(f);
            BufferedImage[] currentSections = this.splitImage(currentImage);
            BufferedImage   result          = this.mergeImage(currentSections);
            this.exportImage(result, f.getName());
        }
    }

    private BufferedImage openImage(File source) {
        try {
            return ImageIO.read(source);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

    private BufferedImage[] splitImage(BufferedImage source) {
        //TODO: Get section count from user input
        int sectionCount = source.getWidth()/88;

        BufferedImage[] sectionArray = new BufferedImage[sectionCount];
        
        for (int i = 0; i < sectionCount; i++) {
            sectionArray[i] = source.getSubimage(i*88, 0, 88, source.getHeight());
        }

        return sectionArray;
    }

    private BufferedImage mergeImage(BufferedImage[] sources) {
        BufferedImage result = new BufferedImage(88, sources[0].getHeight()*sources.length, sources[0].getType());

        Graphics g = result.getGraphics();
        for (int i = 0; i < sources.length; i++) {
            g.drawImage(sources[i], 0, i*sources[i].getHeight(), null);
        }

        return result;
    }

    private void exportImage(BufferedImage source, String name) {
        File output = new File("output/" + name + "-full.png");
        try {
            ImageIO.write(source, "png", output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new Converter();
    }
}