import java.util.Locale.FilteringMode;

public class FrequencyFilter {
	public FrequencyFilter() {}
	
	public FourierComplex[][] filter(FourierComplex[][] sigs, int radius) {
		int height = sigs.length;
		if (height == 0) {
			return null;
		}
		int width = sigs[0].length;
		if (width == 0) {
			return null;
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				
			}
		}
	}
}
