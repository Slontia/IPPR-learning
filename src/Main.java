import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Main {
	public static BufferedImage readImage(String filename) {
        BufferedImage image = null;
        try{
        	image = ImageIO.read(new File(filename)); // read image
        }catch(Exception e){
            e.printStackTrace();
        }
        return image;
	}
	
	public static void main(String[] args) {
		String filename = "D:/test.png";
		BufferedImage image = readImage(filename);
//		DCTProcessor dctProcessor = new DCTProcessor(image);
//		dctProcessor.CosineTransformation("test");
		FFTProcessor fftProcessor = new FFTProcessor(image);
		FourierComplex[][] fourierComplexs = fftProcessor.fourierTransformation("trans");
		fftProcessor.fourierInverse("inverse", fourierComplexs);
		
	}
}
