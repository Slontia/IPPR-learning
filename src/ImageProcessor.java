import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

class ImageProcessor {
	final public static int GREY_SCALE_RANGE = 256;
	int[][] greyMatrix;
	
	public ImageProcessor(String filename) {
        BufferedImage image = null;
        try{
        	image = ImageIO.read(new File(filename)); // read image
        }catch(Exception e){
            e.printStackTrace();
        }
        greyMatrix = getGreyMatrix(image);
	}
	
	public void grayscale(String outputLabel) {
        outputResult(outputLabel, greyMatrix);
	}
	
	protected static int[] getEdgeGreyScale(int[] greyCounts) {
		int minScale, maxScale;
        for (minScale = 0;
        		greyCounts[minScale] == 0 && minScale < GREY_SCALE_RANGE; 
        		minScale++);
        for (maxScale = GREY_SCALE_RANGE - 1; 
        		greyCounts[maxScale] == 0 && maxScale >= 0; 
        		maxScale--);
        int[] res = {minScale, maxScale};
        return res;
	}
	
	// @EFFECTS: returns transformed grey scale matrix with transforming map
	protected static int[][] transGrey(int[][] oriGreyMatrix, int[] greyTransMap) {
		int width = oriGreyMatrix.length;
		if (width == 0) return null;
		int height = oriGreyMatrix[0].length;
		int[][] corGreyMatrix = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				corGreyMatrix[i][j] = greyTransMap[oriGreyMatrix[i][j]];
			}
		}
		return corGreyMatrix;
	}
	
	// @REQUIRES: the grey value must between 0 and 255
	protected static int[] getGreyCounts(int[][] greyMatrix) {
		int[] greyCounts = new int[GREY_SCALE_RANGE];
		for (int i = 0; i < greyMatrix.length; i++) {
			for (int j = 0; j < greyMatrix[i].length; j++) {
				greyCounts[greyMatrix[i][j]]++;
			}
		}
		return greyCounts;
	}
	
	protected static void outputResult(String outputLabel, int[][] greyMatrix) {
        outputImage("grey_" + outputLabel + ".png", "png", getGreyImage(greyMatrix));
        outputImage("hist_" + outputLabel + ".png", "png", getHist(getGreyCounts(greyMatrix)));
	}
	
	// @EFFECTS: output the image
	protected static void outputImage(String filename, String type, BufferedImage image) {
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
	
	// @EFFECTS: returns the matrix of grey scale with image (colorized or grey)
	protected static int[][] getGreyMatrix(BufferedImage image) {
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
	protected static BufferedImage getGreyImage(int[][] greyMatrix) {
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
	
	// @EFFECTS: returns the histogram with array of values
	protected static BufferedImage getHist(int[] values){   
	    int weight = 295;
	    int height = 240;
	    int frame = 20;
	    BufferedImage graph = new BufferedImage(weight, height, BufferedImage.TYPE_4BYTE_ABGR);  
	    Graphics2D g2d = graph.createGraphics();    
        g2d.setPaint(Color.BLACK); // background
        g2d.fillRect(0, 0, weight, height);
        g2d.setPaint(Color.WHITE); // axis
        g2d.drawLine(frame - 5, height - frame + 1, weight - frame + 5, height - frame + 1);
        g2d.setPaint(Color.GREEN); // hist
        double max = -1;  // find max value
        for (int i = 0; i < values.length; i++) {
        	max = (values[i] > max) ? values[i] : max;
        }
        float rate = (float)(height - 2 * frame) / ((float)max);        
        for(int i=0; i<values.length; i++) { // draw hist   
            int frequency = (int)(values[i] * rate);    
            g2d.drawLine(frame + i, height - frame, frame + i, height - frame - frequency);    
        }      
        return graph;  
	}
}