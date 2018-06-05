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
//		int n = 5;
//		for (int i = 0; i < n; i++) {
//			BufferedImage image = readImage("D://test" + i + ".png");	// read file
//			
////			FFTProcessor fftProcessor = new FFTProcessor(image);
////			FourierComplex[][] res = fftProcessor.fourierTransformation();
////			FrequencyFilter filter = new FrequencyFilter();
////			res = filter.filter(res, 20, 0);
////			fftProcessor.fourierInverse("fourier" + i, res);
//			
//			EdgeExtractor edgeExtractor = new EdgeExtractor(image);
//			edgeExtractor.sobelFilter(i + "sobel");
//			edgeExtractor.prewittFilter(i + "perwitt");
//			edgeExtractor.laplaceFilter(i + "laplace");
//			edgeExtractor.robertsFilter(i + "robert");
//			edgeExtractor.cannyFilter(i + "canny");
//			System.out.println(i);
//		}

		// DCT
		DCTProcessor dctProcessor = new DCTProcessor(readImage("D://test2.png"));	// create processor
		double[][] sigs = dctProcessor.cosineTransformation("trans");				// DCT
		dctProcessor.cosineInverse("inverse_", sigs);
	}
}
