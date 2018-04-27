
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
        outputResultHist(outputLabel, transGrey(greyMatrix, greyTransMap));
	}
	
	/** API **/
	public void piecewiseStretch(String outputLabel, int oriLowerScale, int oriUpperScale, int lowerScale, int upperScale) {
		int[] greyCounts = getGreyCounts(greyMatrix);
        int[] greyTransMap = getPiecewiseGreyTransMap(oriLowerScale, oriUpperScale, lowerScale, upperScale, greyCounts);
        outputResultHist(outputLabel, transGrey(greyMatrix, greyTransMap));
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