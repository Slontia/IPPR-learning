import java.awt.image.BufferedImage;

class HistogramCorrector extends GreyTransformation {
	public HistogramCorrector(BufferedImage image) {
		super(image);
	}

	/** API **/
	public BufferedImage histogramCorrection(String outputLabel, int partNum) {
        int[] greyCounts = getGreyCounts(greyMatrix);
        int[] greyTransMap = getGreyTransMap(greyCounts, partNum);
        int[][] newGreyMatrix = transGrey(greyMatrix, greyTransMap);
        greyCounts = getGreyCounts(newGreyMatrix);
        return outputResult(outputLabel, newGreyMatrix);        
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
