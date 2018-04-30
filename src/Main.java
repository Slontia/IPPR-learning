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
		
		// FFT
		FFTProcessor fftProcessor = new FFTProcessor(image);	// create processor
		FourierComplex[][] fourierComplexs = fftProcessor.fourierTransformation("trans");	// FFT
		
		FrequencyFilter filter = new FrequencyFilter();			// create filter
		FourierComplex[][] filteredComplexs;
		
		filteredComplexs = filter.filter(fourierComplexs, 10, 1);		// filter frequency
		fftProcessor.fourierInverse("inverse_10_1", filteredComplexs);	// IFFT
		
		filteredComplexs = filter.filter(fourierComplexs, 20, 1);
		fftProcessor.fourierInverse("inverse_20_1", filteredComplexs);
		
		filteredComplexs = filter.filter(fourierComplexs, 50, 1);
		fftProcessor.fourierInverse("inverse_50_1", filteredComplexs);
		
		filteredComplexs = filter.filter(fourierComplexs, 5, 0);
		fftProcessor.fourierInverse("inverse_5_0", filteredComplexs);
		
		filteredComplexs = filter.filter(fourierComplexs, 10, 0);
		fftProcessor.fourierInverse("inverse_10_0", filteredComplexs);
		
		filteredComplexs = filter.filter(fourierComplexs, 20, 0);
		fftProcessor.fourierInverse("inverse_20_0", filteredComplexs);
		
		// DCT
		DCTProcessor dctProcessor = new DCTProcessor(image);	// create processor
		dctProcessor.CosineTransformation("trans");				// DCT
	}
}
