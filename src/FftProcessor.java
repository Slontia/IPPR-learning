import java.awt.image.BufferedImage;

class FftProcessor extends ImageProcessor {
	public FftProcessor(BufferedImage image) {
		super(image);
	}
	
	private int roundPower(int x) {
		if (((x - 1) & x) == 0) {
			return x;
		}
		int res = 0;
		for (res = 1; x > 0; x >>= 1, res <<= 1);
		return res;
	}
	
	private FourierComplex[] fft1d(FourierComplex[] sigs) {
		// round signals
		int len = sigs.length;
		int N = roundPower(len);
		FourierComplex[] newSigs;
		try {
			newSigs = new FourierComplex[N];
		} catch (Exception e) {
			return null;
		}
		for (int i = 0; i < N; i++) {
			newSigs[i] = sigs[i % len];
		}
		
		// cal WN
		FourierComplex[] WN = new FourierComplex[N / 2];
		for (int i = 0; i < N / 2; i++) {
			WN[i] = new FourierComplex(Math.cos(2 * Math.PI * i / N), -Math.sin(2 * Math.PI * i / N));
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
		FourierComplex[] res = new FourierComplex[N];
		for (int i = 0; i < N; i++) {
			res[i] = newSigs[indexMap[i]];
		}
		
		// dp
		for (int i = 1; i < N; i *= 2) { // level
			for (int j = 0; j < N / 2 / i; j++) { // group
				for (int k = 0; k < i; k++) { // pair
					int index1 = j * i * 2 + k;
					int index2 = index1 + i;
					FourierComplex temp1 = res[index1];
					FourierComplex temp2 = WN[N / 2 / i * k].mul(res[index2]);
					res[index1] = temp1.add(temp2);
					res[index2] = temp1.sub(temp2);
				}
			}
		}
		return res;
	}
	
	private FourierComplex[][] fft2d(FourierComplex[][] sigs) {
		int height = sigs.length;
		if (height == 0) {
			return null;
		}
		int width = sigs[0].length;
		int N = roundPower(height);
		int M = roundPower(width);
		FourierComplex[][] res;
		try {
			res = new FourierComplex[N][M];
		} catch (Exception e) {
			return null;
		}
		
		for (int i = 0; i < height; i++) {
			res[i] = fft1d(sigs[i]);
		}
		
		for (int j = 0; j < M; j++) {
			FourierComplex[] col = new FourierComplex[height];
			for (int i = 0; i < height; i++) {
				col[i] = res[i][j];
			}
			FourierComplex[] newCol = fft1d(col);
			for (int i = 0; i < N; i++) {
				res[i][j] = newCol[i];
			}
		}
		
		return res;
	}
	
	private int[][] translation(int[][] matrix, int N, int M) {
		int i, j;
		int[][] res = new int[N][M];
		for (i = 0; i < N / 2; i++) {
			for (j = 0; j < M / 2; j++) {
				res[i][j] = matrix[i + N / 2][j + M / 2];
				res[i + N / 2][j + M / 2] = matrix[i][j];
			}
			for (; j < M; j++) {
				res[i][j] = matrix[i + N / 2][j - M / 2];
				res[i + N / 2][j - M / 2] = matrix[i][j];
			}
		}
		return res;
	}
	
	/** API **/
	public FourierComplex[][] fourierTransformation(String outputlabel) {
		if (height == 0 || width == 0) {
			return null;
		}
		FourierComplex[][] sigs = new FourierComplex[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				sigs[i][j] = new FourierComplex(greyMatrix[i][j]);
			}
		}
		FourierComplex[][] res = fft2d(sigs);
		outputResult(outputlabel, res);
		return res;
	}
	
	public int[][] normalizeMatrix(int[][] matrix, int edgeValue) {
		int height = matrix.length;
		if (height == 0 || edgeValue <= 0) {
			return null;
		}
		int width = matrix[0].length;
		int maxValue = 1;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (matrix[i][j] > maxValue) {
					maxValue = matrix[i][j];
				}
			}
		}
		double ratio = (double) edgeValue / (double) maxValue;
		int[][] res = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				res[i][j] = (int) (matrix[i][j] * ratio);
			}
		}
		return res;
	}
	
	private void outputResult(String outputLabel, FourierComplex[][] frequencyMatrix) {
		if (frequencyMatrix == null) {
			return;
		}
		int N = frequencyMatrix.length;
		int M = frequencyMatrix[0].length;
		int[][] powerMatrix = new int[N][M];
		int[][] rangeMatrix = new int[N][M];
		int[][] phaseMatrix = new int[N][M];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				FourierComplex fComplex = frequencyMatrix[i][j];
				powerMatrix[i][j] = (int) fComplex.getPower();
				rangeMatrix[i][j] = (int) fComplex.getRange();
				phaseMatrix[i][j] = (int) fComplex.getPhase();
			}
		}
		rangeMatrix = translation(normalizeMatrix(rangeMatrix, GREY_SCALE_RANGE - 1), N, M);
		powerMatrix = translation(normalizeMatrix(powerMatrix, GREY_SCALE_RANGE - 1), N, M);
		phaseMatrix = translation(normalizeMatrix(phaseMatrix, GREY_SCALE_RANGE - 1), N, M);
		outputImage("power_" + outputLabel + ".png", "png", getGreyImage(powerMatrix));
		outputImage("range_" + outputLabel + ".png", "png", getGreyImage(rangeMatrix));
		outputImage("phase_" + outputLabel + ".png", "png", getGreyImage(phaseMatrix));
	}
}