import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class GreyTransformation extends ImageProcessor {
	public GreyTransformation(BufferedImage image) {
		super(image);
	}
	
	public GreyTransformation(int[][] greyMatrix) {
		super(greyMatrix);
	}
	
	protected int[] getEdgeGreyScale(int[] greyCounts) {
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
	protected int[][] transGrey(int[][] oriGreyMatrix, int[] greyTransMap) {
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
	protected int[] getGreyCounts(int[][] greyMatrix) {
		int[] greyCounts = new int[GREY_SCALE_RANGE];
		for (int i = 0; i < greyMatrix.length; i++) {
			for (int j = 0; j < greyMatrix[i].length; j++) {
				greyCounts[greyMatrix[i][j]]++;
			}
		}
		return greyCounts;
	}
	
	// @EFFECTS: returns the histogram with array of values
	protected BufferedImage getHist(int[] values){   
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
	
	protected BufferedImage outputResult(String filename, int[][] greyMatrix) {
		BufferedImage image = getGreyImage(greyMatrix);
        outputImage(filename, "png", image);
        outputImage(filename + "(hist)", "png", getHist(getGreyCounts(greyMatrix)));
        return image;
	}
}
