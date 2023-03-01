import java.io.File;
import java.util.concurrent.ExecutionException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics;

public class Converter {
    
    private boolean createdSuccessfully = false;

    private int   sectionCount;
    private int[] sectionCountArray;

    public Converter(int fixedSectionCount) {
        this.sectionCount      = 0;
        this.sectionCountArray = null;

        this.createdSuccessfully = true;
    }

    public Converter(String[] args) {
        int i = 0;
        this.sectionCountArray = new int[args.length];

        for (String s : args) {
            try {
                this.sectionCountArray[i] = Integer.parseInt(s);
                i++;
            } catch (Exception e) {
                System.out.println("Invalid Arguments : ");
                System.out.println("How to use : java Converter stripeCountForAllImages OR java Converter stripeCount1 stripeCount2 ...");
                System.out.println("The section count must be a whole number");
                return;
            }
        }

        this.createdSuccessfully = true;
    }

    private BufferedImage openImage(File source) {
        try {
            return ImageIO.read(source);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

    private BufferedImage[] splitImage(BufferedImage source, int sectionCount) {
        int sectionWidth = source.getWidth()/sectionCount;

        BufferedImage[] sectionArray = new BufferedImage[sectionCount];
        
        for (int i = 0; i < sectionCount; i++) {
            sectionArray[i] = source.getSubimage(i*sectionWidth, 0, sectionWidth, source.getHeight());
        }

        return sectionArray;
    }

    private BufferedImage mergeImage(BufferedImage[] sources) {
        BufferedImage result = new BufferedImage(sources[0].getWidth(), sources[0].getHeight()*sources.length, sources[0].getType());

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

    public void process() {
        if (!createdSuccessfully) {return;}
        
        // Open Folder
        File   inputDirectory   = new File("input");
        File[] inputImagesPaths = inputDirectory.listFiles();

        if (this.sectionCountArray != null && this.sectionCountArray.length < inputImagesPaths.length) {System.out.println("Not enough section counts. Did you forget an image ?");}

        //Processing Files
        for (int i = 0; i < inputImagesPaths.length; i++) {
            int currentImageSectionCount = (this.sectionCountArray == null ? this.sectionCount : this.sectionCountArray[i]);
            
            BufferedImage   currentImage    = this.openImage(inputImagesPaths[i]);
            BufferedImage[] currentSections = this.splitImage(currentImage, currentImageSectionCount);
            BufferedImage   result          = this.mergeImage(currentSections);
            this.exportImage(result, inputImagesPaths[i].getName());
        }
    }
    
    public static void main(String[] args) {
        Converter converter;

        if (args.length == 0) {System.out.println("How to use : java Converter stripeCountForAllImages OR java Converter stripeCount1 stripeCount2 ..."); System.out.println("The section count must be a whole number"); return;}

        if (args.length > 1) {converter = new Converter(args);}
        else                 {
            try {
                int count = Integer.parseInt(args[0]);
                converter = new Converter(count);
            } catch (Exception e) {
                System.out.println("Invalid Arguments : ");
                System.out.println("How to use : java Converter stripeCountForAllImages OR java Converter stripeCount1 stripeCount2 ...");
                System.out.println("The section count must be a whole number");
                return;
            }
        }
        
        converter.process();
    }
}