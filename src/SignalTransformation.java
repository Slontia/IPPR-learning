import java.awt.image.BufferedImage;

public class SignalTransformation extends ImageProcessor{
	
	public SignalTransformation(BufferedImage image) {
		super(image);
	}
	
	public SignalTransformation(int[][] greyMatrix) {
		super(greyMatrix);
	}

}
