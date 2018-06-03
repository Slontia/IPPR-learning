import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
* 二进制读写文件
*/
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;

//public class MainClass {
//	/**
//	 * java.io包中的OutputStream及其子类专门用于写二进制数据。 FileOutputStream是其子类，可用于将二进制数据写入文件。
//	 * DataOutputStream是OutputStream的另一个子类，它可以
//	 * 连接到一个FileOutputStream上，便于写各种基本数据类型的数据。
//	 */
//	public void writeMethod1() {
//		String fileName = "c:/kuka1.dat";
//		int value0 = 255;
//		int value1 = 0;
//		int value2 = -1;
//		try {
//			// 将DataOutputStream与FileOutputStream连接可输出不同类型的数据
//			// FileOutputStream类的构造函数负责打开文件kuka.dat，如果文件不存在，
//			// 则创建一个新的文件，如果文件已存在则用新创建的文件代替。然后FileOutputStream
//			// 类的对象与一个DataOutputStream对象连接，DataOutputStream类具有写
//			// 各种数据类型的方法。
//			DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName));
//			out.writeInt(value0);
//			out.writeInt(value1);
//			out.writeInt(value2);
//			out.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	// 对于大量数据的写入，使用缓冲流BufferedOutputStream类可以提高效率
//	public void writeMethod2() {
//		String fileName = "c:/kuka2.txt";
//		try {
//			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
//			out.writeInt(10);
//			System.out.println(out.size() + " bytes have been written.");
//			out.writeDouble(31.2);
//			System.out.println(out.size() + " bytes have been written.");
//			out.writeBytes("JAVA");
//			System.out.println(out.size() + " bytes have been written.");
//			out.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 对二进制文件比较常见的类有FileInputStream，DataInputStream
//	 * BufferedInputStream等。类似于DataOutputStream，DataInputStream
//	 * 也提供了很多方法用于读入布尔型、字节、字符、整形、长整形、短整形、 单精度、双精度等数据。
//	 */
//	public void readMethod1() {
//		String fileName = "c:/kuka1.dat";
//		int sum = 0;
//		try {
//			DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
//			sum += in.readInt();
//			sum += in.readInt();
//			sum += in.readInt();
//			System.out.println("The sum is:" + sum);
//			in.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void readMethod2() {
//		try {
//			FileInputStream stream = new FileInputStream("c:/kuka.dat");
//			int c;
//			while ((c = stream.read()) != -1) {
//				System.out.println(c);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}

public class DCTProcessor extends SignalTransformation {
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
			// res[x] = sum;
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
		for (int j = 0; j < height; j++) {
			schedule = (double) (j + width) / (height + width) * 100;
			System.out.println(decimalFormat.format(schedule) + "%");
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
				greyMatrix[i][j] = (long) res[i][j];
				// System.out.println(greyMatrix[i][j] + " ");
			}
		}
		BufferedImage image = getGreyImage(normalizeImage(greyMatrix, GREY_SCALE_RANGE - 1));
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
		int[][] greyMatrix = normalizeImage(res, EDGE);
		outputImage("DCT_" + outputLabel + ".png", "png", getGreyImage(greyMatrix));
		return res;
	}
}
