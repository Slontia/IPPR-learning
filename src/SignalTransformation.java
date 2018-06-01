import java.awt.image.BufferedImage;

public class SignalTransformation extends ImageProcessor{
	
	public SignalTransformation(BufferedImage image) {
		super(image);
	}
	
	
	protected int[][] normalizeMatrix(long[][] matrix) {
		 return normalizeMatrix(matrix, Long.MAX_VALUE);
	}
	
	protected int[][] normalizeMatrix(long[][] matrix, long edgeValue) {
		int height = matrix.length;
		if (height == 0 || edgeValue <= 0) {
			return null;
		}
		int width = matrix[0].length;
		long maxValue = edgeValue;
		long minValue = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (matrix[i][j] > maxValue) {
					maxValue = matrix[i][j];
				} else if (matrix[i][j] < minValue) {
					minValue = matrix[i][j];
				}
			}
		}
		double ratio = (double) (GREY_SCALE_RANGE - 1) / (maxValue - minValue);
		int[][] res = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				res[i][j] = (int) ((matrix[i][j] - minValue) * ratio);
			}
		}
		return res;
	}
}
