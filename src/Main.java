import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Main {
	public static BufferedImage readImage(String filename) {
        BufferedImage image = null;
        try{
        	image = ImageIO.read(new File(filename));
        }catch(Exception e){
            e.printStackTrace();
        }
        return image;
	}
	
	public static void main(String[] args) {
		String filename = "D:/test0.png";			// open file
		BufferedImage image = readImage(filename);	// read file
		
		// DCT
		DCTProcessor dctProcessor = new DCTProcessor(image);	// create processor
		double[][] sigs = dctProcessor.cosineTransformation("trans");				// DCT
		dctProcessor.cosineInverse("inverse", sigs);
	}
}
