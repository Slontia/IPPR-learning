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


class LinearStretcher extends ImageProcessor {
	public LinearStretcher(String filename) {
		super(filename);
	}

	/** API **/
	// @REQUIRES: valid scale
	public void globalStretch(String outputLabel, int lowerScale, int upperScale) {
        int[] greyCounts = getGreyCounts(greyMatrix);
        int[] edgeScales = getEdgeGreyScale(greyCounts);
        localStretch(outputLabel, edgeScales[0], edgeScales[1], lowerScale, upperScale);
	}
	
	/** API **/
	public void localStretch(String outputLabel, int oriLowerScale, int oriUpperScale, int lowerScale, int upperScale) {
        int[] greyTransMap = getGreyTransMap(oriLowerScale, oriUpperScale, lowerScale, upperScale);
        outputResult(outputLabel, transGrey(greyMatrix, greyTransMap));
	}
	
	/** API **/
	public void piecewiseStretch(String outputLabel, int oriLowerScale, int oriUpperScale, int lowerScale, int upperScale) {
		int[] greyCounts = getGreyCounts(greyMatrix);
        int[] greyTransMap = getPiecewiseGreyTransMap(oriLowerScale, oriUpperScale, lowerScale, upperScale, greyCounts);
        outputResult(outputLabel, transGrey(greyMatrix, greyTransMap));
	}
	
	private static int[] getGreyTransMap(int oriLowerScale, int oriUpperScale, int lowerScale, int upperScale) {
		int[] transMap = new int[GREY_SCALE_RANGE];
		fillGreyTransMap(transMap, oriLowerScale, oriUpperScale, lowerScale, upperScale);
		fillGreyTransMap(transMap, 0, oriLowerScale, lowerScale, lowerScale + 1);
    	fillGreyTransMap(transMap, oriUpperScale, GREY_SCALE_RANGE, upperScale - 1, upperScale);	
    	return transMap;
	}
	
	private static int[] getPiecewiseGreyTransMap(int oriLowerScale, int oriUpperScale, int lowerScale, int upperScale, int[] greyCounts) {
		int[] transMap = new int[GREY_SCALE_RANGE];
		int[] edgeScale = getEdgeGreyScale(greyCounts);
		int minScale = edgeScale[0], maxScale = edgeScale[1];
    	fillGreyTransMap(transMap, minScale, oriLowerScale, minScale, lowerScale + 1);
    	fillGreyTransMap(transMap, oriLowerScale, oriUpperScale, lowerScale, upperScale);
		fillGreyTransMap(transMap, oriUpperScale, maxScale + 1, upperScale - 1, maxScale + 1);	
		return transMap;
	}
	
	// @REQUIRES: valid upper / lower
	private static void fillGreyTransMap(int[] transMap, int oriLowerScale, int oriUpperScale, int lowerScale, int upperScale) {
		if (upperScale <= lowerScale) return;
		double rate = (double)(upperScale - lowerScale) / (double)(oriUpperScale - oriLowerScale);
		for (int i = oriLowerScale; i < oriUpperScale; i++) {
			transMap[i] = (int)((i - oriLowerScale) * rate) + lowerScale;
			if (transMap[i] == 256) {
				System.out.println("The World!!");
			}
		}
	}
}


class HistogramCorrector extends ImageProcessor {
	public HistogramCorrector(String filename) {
		super(filename);
		// TODO Auto-generated constructor stub
	}

	/** API **/
	public void histogramCorrection(String outputLabel, int partNum) {
        int[] greyCounts = getGreyCounts(greyMatrix);
        int[] greyTransMap = getGreyTransMap(greyCounts, partNum);
        int[][] newGreyMatrix = transGrey(greyMatrix, greyTransMap);
        greyCounts = getGreyCounts(newGreyMatrix);
        outputImage("grey_" + outputLabel + ".png", "png", getGreyImage(newGreyMatrix));
        outputImage("hist_" + outputLabel + ".png", "png", getHist(greyCounts));          
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
}


public class Main {
	public static void main(String[] args) {
		String filename = "D:/input_3.jpg";
//		HistogramCorrector histogramCorrector = new HistogramCorrector(filename);
//		histogramCorrector.grayscale("origin");
//		histogramCorrector.histogramCorrection("hist_cor_1", 1); 		// task 1
//		histogramCorrector.histogramCorrection("hist_cor_2", 2); 		// task 1
//		histogramCorrector.histogramCorrection("hist_cor_3", 3); 		// task 1
//		histogramCorrector.histogramCorrection("hist_cor_4", 4); 		// task 1
//		histogramCorrector.histogramCorrection("hist_cor_5", 5); 		// task 1
		
		LinearStretcher linearStretcher = new LinearStretcher(filename);
//		linearStretcher.grayscale("cas_str_global");
//		linearStretcher.globalStretch("str_global", 0, 256);
		linearStretcher.localStretch("str_local", 0, 90, 0, 256);	// task 2
//		linearStretcher.piecewiseStretch("str_piece", 256/8*3, 256/8*5, 256/8*2, 256/8*6);	// task 2
	}
}
