import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class DCTProcessor extends SignalTransformation{
	final long EDGE = 10000;
	
	public DCTProcessor(BufferedImage image) {
		super(image);
	}
	
	private double calC(int n) {
		return n == 0 ? (1.0 / Math.pow(2, 0.5)) : 1.0;
	}
	
	private double[] dct1d(double[] sigs) {
		double[] res = new double[sigs.length];
		for (int u = 0; u < sigs.length; u++) {
			double sum = 0;
			for (int x = 0; x < sigs.length; x++) {
				sum += sigs[x] * Math.cos((2 * x + 1) * u * Math.PI / 2 / sigs.length);
			}
			res[u] = sum * Math.pow(2.0 / sigs.length, 0.5) * calC(u);
		}
		return res;
	}
	
	private double[] idct1d(double[] sigs) {
		double[] res = new double[sigs.length];
		for (int x = 0; x < sigs.length; x++) {
			double sum = 0;
			for (int u = 0; u < sigs.length; u++) {
				sum += sigs[u] * calC(u) * Math.cos((2 * x + 1) * u * Math.PI / 2 / sigs.length);
			}
			//res[x] = sum;
			res[x] = sum * Math.pow(2.0 / sigs.length, 0.5);
		}
		return res;
	}
	
	private double[][] dct2d(double[][] sigs, boolean inverseFlag) {
		double[][] res = new double[width][height];
		double schedule = 0;
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		for (int i = 0; i < width; i++) {
			schedule = (double) i / (height + width) * 100;
			System.out.println(decimalFormat.format(schedule) + "%");
			res[i] = inverseFlag ? idct1d(sigs[i]) : dct1d(sigs[i]);
		}
		for (int j = 0 ; j < height; j++) {
			schedule = (double) (j + width) / (height + width) * 100;
			System.out.println(decimalFormat.format(schedule) + "%");
			double[] col = new double[width];
			for (int i = 0 ;i < width; i++) {
				col[i] = res[i][j];
			}
			col = inverseFlag ? idct1d(col) : dct1d(col);
			for (int i = 0; i < width; i++) {
				res[i][j] = col[i];
			}
		}
		return res;
	}
	
	/** API **/
	public double[][] cosineTransformation(String outputLabel) {
		double[][] sigs = new double[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				sigs[i][j] = (double) greyMatrix[i][j]; // change type to double
			}
		}
		
		double[][] res = dct2d(sigs, false);
		
		outputResult(outputLabel, res);
		return res;
	}
	
	/** API **/
	public BufferedImage cosineInverse(String outputLabel, double[][] sigs) {
		double[][] res = dct2d(sigs, true);
		long[][] greyMatrix = new long[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				greyMatrix[i][j] = (long)res[i][j];
//				System.out.println(greyMatrix[i][j] + " ");
			}
		}
		BufferedImage image = getGreyImage(normalizeMatrix(greyMatrix, GREY_SCALE_RANGE - 1));
		outputImage("FFT_Inverse_" + outputLabel + ".png", "png", image);
		return image;
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
