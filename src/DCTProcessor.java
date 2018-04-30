import java.awt.image.BufferedImage;

public class DCTProcessor extends SignalTransformation{
	final long EDGE = 10000;
	
	public DCTProcessor(BufferedImage image) {
		super(image);
	}
	
	private double[] dct1d(double[] sigs) {
		double[] res = new double[sigs.length];
		for (int u = 0; u < sigs.length; u++) {
			double sum = 0;
			for (int x = 0; x < sigs.length; x++) {
				sum += sigs[x] * Math.cos((2 * x + 1) * u * Math.PI / 2 / sigs.length);
			}
			res[u] = sum * (u == 0 ? (1 / Math.pow(sigs.length, 0.5)) : Math.pow(2.0 / sigs.length, 0.5));
			// System.out.println(res[u]);
		}
		return res;
	}
	
	private double[][] dct2d(double[][] sigs) {
		double[][] res = new double[height][width];
		for (int i = 0; i < height; i++) {
			System.out.println((double) i / (height + width) * 100 + "%");
			res[i] = dct1d(sigs[i]);
		}
		for (int j = 0 ; j < width; j++) {
			System.out.println((double) (j + height) / (height + width) * 100 + "%");
			double[] col = new double[height];
			for (int i = 0 ;i < height; i++) {
				col[i] = res[i][j];
			}
			col = dct1d(col);
			for (int i = 0; i < height; i++) {
				res[i][j] = col[i];
			}
		}
		return res;
	}
	
	public double[][] CosineTransformation(String outputLabel) {
		double[][] sigs = new double[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				sigs[i][j] = (double) greyMatrix[i][j];
			}
		}
		double[][] res = dct2d(sigs);
		outputResult(outputLabel, res);
		return res;
	}
	
	private long[][] outputResult(String outputLabel, double[][] frequencyMatrix) {
		long[][] res = new long[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				res[i][j] = (int) frequencyMatrix[i][j];
			}
		}
		int[][] greyMatrix = normalizeMatrix(res, EDGE);
		outputImage("DCT_" + outputLabel + ".png", "png", getGreyImage(greyMatrix));
		return res;
	}
}
