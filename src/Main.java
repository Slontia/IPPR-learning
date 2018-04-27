
public class Main {
	public static void main(String[] args) {
		String filename = "D:/test.png";
		FftProcessor fftProcessor = new FftProcessor(filename);
		System.out.println("output");
		fftProcessor.fourierTransformation("");
		
//		HistogramCorrector histogramCorrector = new HistogramCorrector(filename);
//		histogramCorrector.grayscale("origin");
//		histogramCorrector.histogramCorrection("hist_cor_1", 1); 		// task 1
//		histogramCorrector.histogramCorrection("hist_cor_2", 2); 		// task 1
//		histogramCorrector.histogramCorrection("hist_cor_3", 3); 		// task 1
//		histogramCorrector.histogramCorrection("hist_cor_4", 4); 		// task 1
//		histogramCorrector.histogramCorrection("hist_cor_5", 5); 		// task 1
		
//		LinearStretcher linearStretcher = new LinearStretcher(filename);
//		linearStretcher.grayscale("cas_str_global");
//		linearStretcher.globalStretch("str_global", 0, 256);
//		linearStretcher.localStretch("str_local", 0, 90, 0, 256);	// task 2
//		linearStretcher.piecewiseStretch("str_piece", 256/8*3, 256/8*5, 256/8*2, 256/8*6);	// task 2
	}
}
