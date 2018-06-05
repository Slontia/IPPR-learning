import java.awt.image.BufferedImage;

public class EdgeExtractor extends ImageProcessor {
	final private int HIGH_THRESHOLD = GREY_SCALE_RANGE / 4;
	final private int LOW_THRESHOLD = GREY_SCALE_RANGE / 32;
	final private double[][] guassOperator = 
		{
				{0.0924, 0.1192, 0.0924},
				{0.1192, 0.1538, 0.1192},
				{0.0924, 0.1192, 0.0924}
		};
	
	final private double[][] sobelOperatorX =
		{
				{-1, 0, 1},
				{-2, 0, 2},
				{-1, 0, 1}
		};
	final private double[][] sobelOperatorY = 
		{
				{-1,	-2,	 -1},
				{0,	 0,	 0},
				{1,	 2,	 1}
		};
	final private double[][] laplaceOperator = 
		{
				{0, -1, 0},
				{-1, 4, -1},
				{0, -1, 0}
		};
	final private double[][] prewittOperatorX = 
		{
				{-1, 0, 1},
				{-1, 0, 1},
				{-1, 0, 1}
		};
	final private double[][] prewittOperatorY = 
		{
				{-1, -1, -1},
				{0, 0, 0},
				{1, 1, 1}
		};
	
	public EdgeExtractor (BufferedImage image) {
		super(image);
	}
	
	private int[][] operate(int[][] greyMatrix, double[][] operator) {
		int[][] res = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int sum = 0;
				for (int ii = 0; ii < 3; ii++)  {
					for (int jj = 0; jj < 3; jj++) {
						int x = i - 1 + ii;
						int y = j - 1 + jj;
						if (x >= 0 && x < width && y >=0 && y < height) {
							sum += greyMatrix[x][y] * operator[ii][jj];
						} else {
							sum += greyMatrix[i][j] * operator[ii][jj];
						}
					}
				}
				res[i][j] = sum;
			}
		}
		return res;
	}
	
	public long[][] prewittFilter(String filename) {
		int[][] guassFilter = operate(greyMatrix, guassOperator);
		int[][] filterX = operate(guassFilter, prewittOperatorX);
		int[][] filterY = operate(guassFilter, prewittOperatorY);
		long[][] res = new long[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				res[i][j] = (long) Math.pow(filterX[i][j] * filterX[i][j] + filterY[i][j] * filterY[i][j], 0.5);
				if (res[i][j] >= GREY_SCALE_RANGE) {
					res[i][j] = GREY_SCALE_RANGE - 1;
				}
			}
		}
		outputImage(filename, "png", getGreyImage(normalizeImage(res, GREY_SCALE_RANGE - 1)));
		return res;
	}
	
	public long[][] sobelFilter(String filename) {
		int[][] guassFilter = operate(greyMatrix, guassOperator);
		int[][] filterX = operate(guassFilter, sobelOperatorX);
		int[][] filterY = operate(guassFilter, sobelOperatorY);
		long[][] res = new long[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				res[i][j] = (long) Math.pow(filterX[i][j] * filterX[i][j] + filterY[i][j] * filterY[i][j], 0.5);
				if (res[i][j] >= GREY_SCALE_RANGE) {
					res[i][j] = GREY_SCALE_RANGE - 1;
				}
			}
		}
		outputImage(filename, "png", getGreyImage(normalizeImage(res, GREY_SCALE_RANGE - 1)));
		return res;
	}
	
	public long[][] laplaceFilter(String filename) {
		int[][] guassFilter = operate(greyMatrix, guassOperator);
		int[][] filter = operate(guassFilter, laplaceOperator);
		long[][] res = new long[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				res[i][j] = Math.abs(filter[i][j]);
				if (res[i][j] >= GREY_SCALE_RANGE) {
					res[i][j] = GREY_SCALE_RANGE - 1;
				}
			}
		}
		new LinearStretcher(normalizeImage(res, GREY_SCALE_RANGE - 1))
			.localStretch(filename, 0, GREY_SCALE_RANGE / 8, 0, GREY_SCALE_RANGE);
		return res;
	}
	
	public long[][] robertsFilter(String filename) {
		int[][] guassFilter = operate(greyMatrix, guassOperator);
		long[][] res = new long[width][height];
		for (int i = 0; i < width - 1; i++) {
			for (int j = 0; j < height - 1; j++) {
				int sub1 = guassFilter[i][j] - guassFilter[i+1][j+1];
				int sub2 = guassFilter[i][j+1] - guassFilter[i+1][j];
				res[i][j] = (long) Math.pow(sub1 * sub1 + sub2 * sub2, 0.5);
			}
		}
		new LinearStretcher(normalizeImage(res, GREY_SCALE_RANGE - 1))
			.localStretch(filename, 0, GREY_SCALE_RANGE / 4, 0, GREY_SCALE_RANGE);
		return res;
	}
	
	private boolean isValid(int x, int y) {
		return x < width && y < height && x >= 0 && y >= 0;
	}
	
	private enum EdgeStatus {
		STRONG, WEAK, SUPPRESS, CERTAIN
	}
	
	private void enhanceEdge(EdgeStatus[][] status, int i, int j) {
		if (isValid(i, j) && status[i][j] == EdgeStatus.STRONG) {
			status[i][j] = EdgeStatus.CERTAIN;
			for (int ii = -1; ii <= 1; ii++) {
				for (int jj = -1; jj <= 1; jj++) {
					enhanceEdge(status, ii, jj);
				}
			}
		}
	}
	
	public int[][] cannyFilter(String filename) {
		// 卷积
		int[][] guassFilter = operate(greyMatrix, guassOperator);
		// 计算初始边缘
		int[][] filterX = operate(guassFilter, sobelOperatorX);
		int[][] filterY = operate(guassFilter, sobelOperatorY);
		int[][] intensity = new int[width][height];
		double[][] angle = new double[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				intensity[i][j] = (int) Math.pow(filterX[i][j] * filterX[i][j] + filterY[i][j] * filterY[i][j], 0.5);
				if (intensity[i][j] >= GREY_SCALE_RANGE) {
					intensity[i][j] = GREY_SCALE_RANGE - 1;
				}
				angle[i][j] = Math.atan2(filterY[i][j], filterX[i][j]) / Math.PI * 180; // get angle
			}
		}
		// 非极大值抑制
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int x1, y1, x2, y2;
				if (angle[i][j] <= -67.5 || angle[i][j] >= 67.5) { // left - right
					x1 = i - 1;		y1 = j;
					x2 = i + 1;		y2 = j;
				} else if (angle[i][j] < -22.5) {	// left&top - right-bottom
					x1 = i - 1;		y1 = j + 1;
					x2 = i + 1;		y2 = j - 1;
				} else if (angle[i][j] <= 22.5) {	// top - bottom
					x1 = i;			y1 = j + 1;
					x2 = i;			y2 = j - 1;
				} else {	// left&bottom - right-top
					x1 = i - 1;		y1 = j - 1;
					x2 = i + 1;		y2 = j + 1;
				}
				if ((isValid(x1, y1) && intensity[x1][y1] >= intensity[i][j]) ||
						isValid(x2, y2) && intensity[x2][y2] >= intensity[i][j]) { // if found one bigger than itself, clean it
					intensity[i][j] = 0;
				}
			}
		}
		outputImage(filename + "(thin)", "png", getGreyImage(intensity));
		// 双阈值检测
		EdgeStatus status[][] = new EdgeStatus[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (intensity[i][j] >= HIGH_THRESHOLD) {
					status[i][j] = EdgeStatus.STRONG;
				} else if (intensity[i][j] < LOW_THRESHOLD) {
					status[i][j] = EdgeStatus.SUPPRESS;
				} else {
					status[i][j] = EdgeStatus.WEAK;
				}
			}
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				enhanceEdge(status, i, j);
				if (status[i][j] != EdgeStatus.CERTAIN) {
					intensity[i][j] = 0;
				} else {
					intensity[i][j] = GREY_SCALE_RANGE - 1;
				}
			}
		}
		outputImage(filename, "png", getGreyImage(intensity));
		return intensity;
	}
}
