import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

class ImageProcessor {
	final public static int GREY_SCALE_RANGE = 256;
	final public static int PROGRESS_BAR_LEN = 10;
	final protected int[][] greyMatrix;
	final protected int[][] saturMatrix;
	final protected int[][] chromaMatrix;
	final protected int height;
	final protected int width;
	ProgressBar progressBar;
	
	public ImageProcessor(BufferedImage image) {
        width = image.getWidth();
        height = image.getHeight();
        int r, g, b;
        greyMatrix = new int[width][height];
        saturMatrix = new int[width][height];
        chromaMatrix = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
            	int pixel = image.getRGB(i, j);            
				r = (pixel & 0xff0000) >> 16;  
				g = (pixel & 0xff00) >> 8;  
				b = (pixel & 0xff);  
				greyMatrix[i][j] = (int)(0.299 * r + 0.587 * g + 0.114 * b); // translate to grey
				saturMatrix[i][j] = (int)(-0.1687 * r - 0.3313 * g + 0.5 * b + 128);
				chromaMatrix[i][j] = (int)(0.5 * r - 0.418 * g - 0.0813 * b + 128);
            }
        }
	}
	
	public ImageProcessor(int[][] greyMatrix) {
		width = greyMatrix.length;
		if (width == 0) {
			height = 0;
		} else {
			height = greyMatrix[0].length;
		}
		this.greyMatrix = greyMatrix;
		this.saturMatrix = new int[height][width];
		this.chromaMatrix = new int[height][width];
	}
	
	// @REQUIRES: greyMap must be a matrix
	// @EFFECTS: returns the grey image with matrix of grey scale
	protected BufferedImage getGreyImage(int[][] greyMatrix) {
		int width = greyMatrix.length;
		if (width == 0) return null;
		int height = greyMatrix[0].length;
		if (height == 0) return null;
        BufferedImage greyImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < width; i++) {
        	for (int j = 0; j < height; j++) {
        		int grey = greyMatrix[i][j];
        		greyImage.setRGB(i, j, (grey << 16) | (grey << 8) | (grey));
        	}
        }
        return greyImage;
	}
	
	// @EFFECTS: output the image
	protected void outputImage(String filename, String type, BufferedImage image) {
	    Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName(type);
	    ImageWriter writer = it.next();
	    ImageOutputStream ios;
	    new File("output").mkdirs();
		try {
			ios = ImageIO.createImageOutputStream(new File("output/" + filename + "." + type));
			writer.setOutput(ios);
			writer.write(image);
			image.flush();
			ios.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	protected int[][] normalizeMatrix(long[][] matrix) {
		 return normalizeImage(matrix, Long.MAX_VALUE);
	}
	
	static protected int[][] normalizeImage(long[][] matrix, long edgeValue) {
		int width = matrix.length;
		if (width == 0 || edgeValue <= 0) {
			return null;
		}
		int height = matrix[0].length;
		long maxValue = edgeValue;
		long minValue = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (matrix[i][j] > maxValue) {
					maxValue = matrix[i][j];
				} else if (matrix[i][j] < minValue) {
					minValue = matrix[i][j];
				}
			}
		}
		double ratio = (double) (GREY_SCALE_RANGE - 1) / (maxValue - minValue);
		int[][] res = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				res[i][j] = (int) ((matrix[i][j] - minValue) * ratio);
			}
		}
		return res;
	}
	
	/** API **/
	public BufferedImage grayscale(String outputLabel) {
		BufferedImage image = getGreyImage(greyMatrix);
		outputImage("grey_" + outputLabel + ".png", "png", image);
		return image;
	}
}