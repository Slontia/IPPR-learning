import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

class ImageProcessor {
	final public static int GREY_SCALE_RANGE = 256;
	protected int[][] greyMatrix;
	protected int height;
	protected int width;
	
	public ImageProcessor(BufferedImage image) {
        this.greyMatrix = getGreyMatrix(image);
        this.height = this.greyMatrix.length;
        if (this.height == 0) {
        	this.width = 0;
        } else {
        	this.width = this.greyMatrix[0].length;
        }
	}
	
	// @EFFECTS: returns the matrix of grey scale with image (colorized or grey)
	private int[][] getGreyMatrix(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] rgb = new int[3];
        int[][] greyMatrix = new int[width][height]; 
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
            	int pixel = image.getRGB(i, j);            
				rgb[0] = (pixel & 0xff0000) >> 16;  
				rgb[1] = (pixel & 0xff00) >> 8;  
				rgb[2] = (pixel & 0xff);  
	            image.setRGB(i, j, (rgb[0] << 16) | (rgb[1] << 8) | (rgb[2])); 
	            greyMatrix[i][j] = (int)(0.3 * rgb[0] + 0.59 * rgb[1] + 0.11 * rgb[2]); // translate to grey
            }
        }
		return greyMatrix;
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
			ios = ImageIO.createImageOutputStream(new File("output/" + filename));
			writer.setOutput(ios);
			writer.write(image);
			image.flush();
			ios.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/** API **/
	public void grayscale(String outputLabel) {
		outputImage("grey_" + outputLabel + ".png", "png", getGreyImage(greyMatrix));
	}
}