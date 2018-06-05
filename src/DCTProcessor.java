import java.awt.image.BufferedImage;


public class DCTProcessor extends SignalTransformation {
	final long EDGE = 10000;

	public DCTProcessor(BufferedImage image) {
		super(image);
	}
	
	public DCTProcessor(int[][] greyMatrix) {
		super(greyMatrix);
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
			// res[x] = sum;
			res[x] = sum * Math.pow(2.0 / sigs.length, 0.5);
		}
		return res;
	}

	private double[][] dct2d(double[][] sigs, boolean inverseFlag) {
		double[][] res = new double[width][height];
		double progress = 0;
		for (int i = 0; i < width; i++) {
			progress = (double) i / (height + width);
			progressBar.showProgressStatus(progress);
			res[i] = inverseFlag ? idct1d(sigs[i]) : dct1d(sigs[i]);
		}
		for (int j = 0; j < height; j++) {
			progress = (double) (j + width) / (height + width);
			progressBar.showProgressStatus(progress);
			double[] col = new double[width];
			for (int i = 0; i < width; i++) {
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
	public double[][] cosineTransformation(String filename) {
		progressBar = new ProgressBar(filename, PROGRESS_BAR_LEN);
		double[][] sigs = new double[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				sigs[i][j] = (double) greyMatrix[i][j]; // change type to double
			}
		}
		double[][] res = dct2d(sigs, false);
		outputResult(filename, res);
		return res;
	}

	/** API **/
	public BufferedImage cosineInverse(String filename, double[][] sigs) {
		progressBar = new ProgressBar(filename, PROGRESS_BAR_LEN);
		double[][] res = dct2d(sigs, true);
		long[][] greyMatrix = new long[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				greyMatrix[i][j] = (long) res[i][j];
				// System.out.println(greyMatrix[i][j] + " ");
			}
		}
		BufferedImage image = getGreyImage(normalizeImage(greyMatrix, GREY_SCALE_RANGE - 1));
		outputImage(filename, "png", image);
		return image;
	}

	private long[][] outputResult(String filename, double[][] frequencyMatrix) {
		long[][] res = new long[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				res[i][j] = (int) frequencyMatrix[i][j];
			}
		}
		int[][] greyMatrix = normalizeImage(res, EDGE);
		outputImage(filename, "png", getGreyImage(greyMatrix));
		return res;
	}
}
