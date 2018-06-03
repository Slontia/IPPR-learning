import java.awt.image.BufferedImage;

public class EdgeExtractor extends ImageProcessor {
	final private int[][] sobelOperatorX =
		{
				{-1, 0, 1},
				{-2, 0, 2},
				{-1, 0, 1}
		};
	final private int[][] sobelOperatorY = 
		{
				{1,	-2,	 1},
				{0,	 0,	 0},
				{1,	 2,	 1}
		};
	final private int[][] laplaceOperator = 
		{
				{0, -1, 0},
				{-1, 4, -1},
				{0, -1, 0}
		};
	final private int[][] prewittOperatorX = 
		{
				{-1, 0, 1},
				{-1, 0, 1},
				{-1, 0, 1}
		};
	final private int[][] prewittOperatorY = 
		{
				{-1, -1, -1},
				{0, 0, 0},
				{1, 1, 1}
		};
	
	public EdgeExtractor (BufferedImage image) {
		super(image);
	}
	
	private int[][] operate(int[][] operator) {
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
						}
					}
				}
				// System.out.println(sum);
				res[i][j] = sum;
			}
		}
		return res;
	}
	
	public long[][] sobelFilter(String filename) {
		int[][] filterX = operate(sobelOperatorX);
		int[][] filterY = operate(sobelOperatorY);
		long[][] res = new long[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				res[i][j] = (long) Math.pow(filterX[i][j] * filterX[i][j] + filterY[i][j] * filterY[i][j], 0.5);
//				if (res[i][j] >= GREY_SCALE_RANGE) {
//					res[i][j] = GREY_SCALE_RANGE - 1;
//				}
			}
		}
		outputImage(filename + ".png", "png", getGreyImage(normalizeImage(res, GREY_SCALE_RANGE - 1)));
		return res;
	}
	
	public long[][] laplaceFilter(String filename) {
		int[][] filter = operate(laplaceOperator);
		long[][] res = new long[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				res[i][j] = Math.abs(filter[i][j]);
				if (res[i][j] >= GREY_SCALE_RANGE) {
					res[i][j] = GREY_SCALE_RANGE - 1;
				}
			}
		}
		outputImage(filename + ".png", "png", getGreyImage(normalizeImage(res, GREY_SCALE_RANGE - 1)));
		return res;
	}
}
