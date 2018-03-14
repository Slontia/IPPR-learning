import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class Main {
	final public static int GREY_SCALE_RANGE = 256;
	
	public static void grayscale(String filename) {
        BufferedImage image = null;
        try{
        	image = ImageIO.read(new File(filename)); // read image
        }catch(Exception e){
            e.printStackTrace();
        }
        int[][] oriGreyMatrix = getGreyMatrix(image);
        int[] oriGreyCounts = getGreyCounts(oriGreyMatrix);	
        outputImage("d:/grey_ori.jpg", "png", getGreyImage(oriGreyMatrix));
        outputImage("d:/hist_ori.jpg", "png", getHist(oriGreyCounts));
	}
	
	
	/** task 1 **/
	public static void histogramCorrection(String filename, int partNum) {
        BufferedImage image = null;
        try{
        	image = ImageIO.read(new File(filename)); // read image
        }catch(Exception e){
            e.printStackTrace();
        }
        int[][] greyMatrix = getGreyMatrix(image);
        int[] greyCounts = getGreyCounts(greyMatrix);
        int[] greyTransMap = getGreyTransMap(greyCounts, partNum);
        greyMatrix = transGrey(greyMatrix, greyTransMap);
        greyCounts = getGreyCounts(greyMatrix);
        outputImage("d:/grey_cor.jpg", "png", getGreyImage(greyMatrix));
        outputImage("d:/hist_cor.jpg", "png", getHist(greyCounts));          
	}
	
	
	/** task 2 **/
	// @REQUIRES: valid scale
	public static void globalStretch(String filename, int lowerScale, int upperScale) {
        BufferedImage image = null;
        try{
        	image = ImageIO.read(new File(filename)); // read image
        }catch(Exception e){
            e.printStackTrace();
        }
        int[][] greyMatrix = getGreyMatrix(image);
        int[] greyCounts = getGreyCounts(greyMatrix);
        int oriLowerScale, oriUpperScale;
        for (oriLowerScale = 0; 
        		greyCounts[oriLowerScale] == 0 && oriLowerScale < GREY_SCALE_RANGE; 
        		oriLowerScale++);
        for (oriUpperScale = GREY_SCALE_RANGE - 1; 
        		greyCounts[oriUpperScale] == 0 && oriUpperScale >= 0; 
        		oriUpperScale--);
        if (oriLowerScale >= oriUpperScale) return;
        double rate = (double)(upperScale - lowerScale) / (double)(oriUpperScale - oriLowerScale);
        for (int i = 0; i < greyMatrix.length; i++) {
        	for (int j = 0; j < greyMatrix[i].length; j++) {
        		greyMatrix[i][j] = (int)(rate * (greyMatrix[i][j] - oriLowerScale) + lowerScale);
        	}
        }
        greyCounts = getGreyCounts(greyMatrix);
        outputImage("d:/grey_str_glo.jpg", "png", getGreyImage(greyMatrix));
        outputImage("d:/hist_str_glo.jpg", "png", getHist(greyCounts));
	}
	
	
	/** task 2 **/
	public static void localStretch
			(String filename, int oriLowerScale, int oriUpperScale, int lowerScale, int upperScale) {
        BufferedImage image = null;
        try{
        	image = ImageIO.read(new File(filename)); // read image
        }catch(Exception e){
            e.printStackTrace();
        }
        int[][] greyMatrix = getGreyMatrix(image);
        int[] greyCounts = getGreyCounts(greyMatrix); 
        double rate = (double)(upperScale - lowerScale) / (double)(oriUpperScale - oriLowerScale);
        for (int i = 0; i < greyMatrix.length; i++) {
        	for (int j = 0; j < greyMatrix[i].length; j++) {
        		int grey = greyMatrix[i][j];
        		if (grey >= oriLowerScale && grey <= oriUpperScale) {
        			greyMatrix[i][j] = (int)(rate * (grey - oriLowerScale) + lowerScale);
        		} else if (grey < oriLowerScale) {
        			greyMatrix[i][j] = lowerScale;
        		} else if (grey > oriUpperScale) {
        			greyMatrix[i][j] = upperScale;
        		}
        	}
        }
        greyCounts = getGreyCounts(greyMatrix);
        outputImage("d:/grey_str_loc.jpg", "png", getGreyImage(greyMatrix));
        outputImage("d:/hist_str_loc.jpg", "png", getHist(greyCounts));
	}
	
	
	// @EFFECTS: returns transformed grey scale matrix with transforming map
	private static int[][] transGrey(int[][] oriGreyMatrix, int[] greyTransMap) {
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
	private static int[] getGreyCounts(int[][] greyMatrix) {
		int[] greyCounts = new int[GREY_SCALE_RANGE];
		for (int i = 0; i < greyMatrix.length; i++) {
			for (int j = 0; j < greyMatrix[i].length; j++) {
				greyCounts[greyMatrix[i][j]]++;
			}
		}
		return greyCounts;
	}	
	
	
	// returns transforming map for grey scale in histogram correction
	private static int[] getGreyTransMap(int[] greyCounts, int partNum) {
		int sum = 0;
		if (greyCounts.length != GREY_SCALE_RANGE) return null;		
		for (int i = 0; i < GREY_SCALE_RANGE; sum += greyCounts[i++]);
		
		int avgPartSum = sum / partNum;
		int[] cuts = new int[partNum];
		int[] partSums = new int[partNum];
		int curSum = 0, excSum = 0;
		int curPartNum = 0;
		
		for (int i = 0; i < GREY_SCALE_RANGE; i++) { // get intervals
			curSum += greyCounts[i];
			while (curSum + excSum >= avgPartSum && curPartNum < partNum - 1) { // may jumps over several cuts
				cuts[curPartNum] = i;
				partSums[curPartNum] = curSum;
				excSum = curSum + excSum - avgPartSum;
				curPartNum++;
				curSum = 0;
			}
		}
		cuts[partNum - 1] = GREY_SCALE_RANGE - 1; // record the last cuts
		partSums[partNum - 1] = curSum;
		
		int[] greyTransMap = new int[GREY_SCALE_RANGE]; // fill transMap
		int baseGreyScale = 0;
		for (int i = 0; i < partNum; i++) {
			curSum = 0;
			for (int j = baseGreyScale; j <= cuts[i]; j++) {
				curSum += greyCounts[j];
				greyTransMap[j] = baseGreyScale + (cuts[i] - baseGreyScale) * curSum / partSums[i];
			}
			baseGreyScale = cuts[i] + 1;
		}
		return greyTransMap;
	}
	
	
	// @EFFECTS: output the image
	private static void outputImage(String filename, String type, BufferedImage image) {
	    Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName(type);
	    ImageWriter writer = it.next();
	    ImageOutputStream ios;
		try {
			ios = ImageIO.createImageOutputStream(new File(filename));
			writer.setOutput(ios);
			writer.write(image);
			image.flush();
			ios.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	
	// @EFFECTS: returns the matrix of grey scale with image (colorized or grey)
	private static int[][] getGreyMatrix(BufferedImage image) {
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
	private static BufferedImage getGreyImage(int[][] greyMatrix) {
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
	private static BufferedImage getHist(int[] values){   
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
        g2d.setPaint(Color.RED);    
        return graph;  
	}  

	public static void main(String[] args) {
		String filename = "D:/input.jpg";
		grayscale(filename);
		histogramCorrection(filename, 2); 			// task 1
		globalStretch(filename, 0, 255);			// task 2
		localStretch(filename, 64, 190, 0, 255);	// task 2
	}
}
