import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

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
		double[][] res = new double[width][height];
		double schedule = 0;
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		for (int i = 0; i < width; i++) {
			schedule = (double) i / (height + width) * 100;
			System.out.println(decimalFormat.format(schedule) + "%");
			res[i] = dct1d(sigs[i]);
		}
		for (int j = 0 ; j < height; j++) {
			schedule = (double) (j + width) / (height + width) * 100;
			System.out.println(decimalFormat.format(schedule) + "%");
			double[] col = new double[width];
			for (int i = 0 ;i < width; i++) {
				col[i] = res[i][j];
			}
			col = dct1d(col);
			for (int i = 0; i < width; i++) {
				res[i][j] = col[i];
			}
		}
		return res;
	}
	
	public double[][] CosineTransformation(String outputLabel) {
		double[][] sigs = new double[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				//System.out.println(height + " " + width + " " + i + " " + j);
				sigs[i][j] = (double) greyMatrix[i][j];
			}
		}
		double[][] res = dct2d(sigs);
		outputResult(outputLabel, res);
		return res;
	}
	
	private long[][] outputResult(String outputLabel, double[][] frequencyMatrix) {
		long[][] res = new long[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				res[i][j] = (int) frequencyMatrix[i][j];
			}
		}
		int[][] greyMatrix = normalizeMatrix(res, EDGE);
		outputImage("DCT_" + outputLabel + ".png", "png", getGreyImage(greyMatrix));
		return res;
	}
}
