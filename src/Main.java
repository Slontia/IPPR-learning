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
		String filename = "D:/test3.png";
		BufferedImage image = readImage(filename);
//		DCTProcessor dctProcessor = new DCTProcessor(image);
//		dctProcessor.CosineTransformation("test");
		FFTProcessor fftProcessor = new FFTProcessor(image);
		FourierComplex[][] fourierComplexs = fftProcessor.fourierTransformation("trans");
		FrequencyFilter filter = new FrequencyFilter();
		
		FourierComplex[][] filteredComplexs;
		
		filteredComplexs = filter.filter(fourierComplexs, 10, 1);
		fftProcessor.fourierInverse("inverse_10_1", filteredComplexs);
		System.out.println(1);
		
		filteredComplexs = filter.filter(fourierComplexs, 20, 1);
		fftProcessor.fourierInverse("inverse_20_1", filteredComplexs);
		System.out.println(2);
		
		filteredComplexs = filter.filter(fourierComplexs, 50, 1);
		fftProcessor.fourierInverse("inverse_50_1", filteredComplexs);
		System.out.println(3);
		
		filteredComplexs = filter.filter(fourierComplexs, 20, 0);
		fftProcessor.fourierInverse("inverse_20_0", filteredComplexs);
		System.out.println(4);
		
		filteredComplexs = filter.filter(fourierComplexs, 50, 0);
		fftProcessor.fourierInverse("inverse_50_0", filteredComplexs);
		System.out.println(5);
		
		filteredComplexs = filter.filter(fourierComplexs, 100, 0);
		fftProcessor.fourierInverse("inverse_100_0", filteredComplexs);
		System.out.println(6);
	}
}
