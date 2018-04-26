import java.util.ArrayList;

import javax.security.auth.kerberos.KerberosKey;

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


class FftProcessor extends ImageProcessor {
	public FftProcessor(String filename) {
		super(filename);
	}
	
	public int roundPower(int x) {
		if (((x - 1) & x) == 0) {
			return x;
		}
		int res = 0;
		for (res = 1; x > 0; x >>= 1, res <<= 1);
		return res;
	}
	
	public Complex[] fft2d(Complex[] sigs) {
		// round signals
		int len = sigs.length;
		int N = roundPower(len);
		Complex[] newSigs;
		try {
			newSigs = new Complex[N];
		} catch (Exception e) {
			return null;
		}
		for (int i = 0; i < N; i++) {
			newSigs[i] = sigs[i % len];
		}
		
		// cal WN
		Complex[] WN = new Complex[N / 2];
		for (int i = 0; i < N / 2; i++) {
			WN[i] = new Complex(Math.cos(2 * Math.PI * i / N), -Math.sin(2 * Math.PI * i / N));
		}
		
		// initial list
		int[] indexMap = new int[N];
		indexMap[0] = 0;
		for (int i = 1; i < N; i *= 2) {
			int step = N / 2 / i;
			for (int j = 0; j < N; j += step * 2) {
				indexMap[j + step] = indexMap[j] + i;
			}
		}
		Complex[] res = new Complex[N];
		for (int i = 0; i < N; i++) {
			res[i] = newSigs[indexMap[i]];
		}
		
		// dp
		for (int i = 1; i < N; i *= 2) { // level
			for (int j = 0; j < N / 2 / i; j++) { // group
				for (int k = 0; k < i; k++) { // pair
					int index1 = j * i * 2 + k;
					int index2 = index1 + i;
					Complex temp1 = res[index1];
					Complex temp2 = WN[N / 2 / i * k].mul(res[index2]);
					res[index1] = temp1.add(temp2);
					res[index2] = temp1.sub(temp2);
				}
			}
		}
		
		return res;
	}
}


public class Main {
	public static void main(String[] args) {
		String filename = "D:/yuu_grey.png";
//		HistogramCorrector histogramCorrector = new HistogramCorrector(filename);
//		histogramCorrector.grayscale("origin");
//		histogramCorrector.histogramCorrection("hist_cor_1", 1); 		// task 1
//		histogramCorrector.histogramCorrection("hist_cor_2", 2); 		// task 1
//		histogramCorrector.histogramCorrection("hist_cor_3", 3); 		// task 1
//		histogramCorrector.histogramCorrection("hist_cor_4", 4); 		// task 1
//		histogramCorrector.histogramCorrection("hist_cor_5", 5); 		// task 1
		
		LinearStretcher linearStretcher = new LinearStretcher(filename);
//		linearStretcher.grayscale("cas_str_global");
		linearStretcher.globalStretch("str_global", 0, 256);
//		linearStretcher.localStretch("str_local", 0, 90, 0, 256);	// task 2
//		linearStretcher.piecewiseStretch("str_piece", 256/8*3, 256/8*5, 256/8*2, 256/8*6);	// task 2
	}
}
