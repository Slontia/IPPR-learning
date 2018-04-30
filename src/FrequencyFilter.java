
public class FrequencyFilter {
	public FrequencyFilter() {}
	
	public FourierComplex[][] filter(FourierComplex[][] sigs, int radius, int mode) {
		int height = sigs.length;
		if (height == 0) {
			return null;
		}
		int width = sigs[0].length;
		if (width == 0) {
			return null;
		}
		FourierComplex[][] res = new FourierComplex[height][width];
		for (int i = 0; i < height / 2; i++) {
			for (int j = 0; j < width / 2; j++) {
				if (Math.pow(Math.pow(i, 2) + Math.pow(j, 2), 0.5) <= radius) {
					res[i][j] = sigs[i][j].mul(mode);
					res[height - i - 1][j] = sigs[height - i - 1][j].mul(mode);
					res[i][width - j - 1] = sigs[i][width - j - 1].mul(mode);
					res[height - i - 1][width - j - 1] = sigs[height - i - 1][width - j - 1].mul(mode);
				} else {
					res[i][j] = sigs[i][j].mul(1 - mode);
					res[height - i - 1][j] = sigs[height - i - 1][j].mul(1 - mode);
					res[i][width - j - 1] = sigs[i][width - j - 1].mul(1 - mode);
					res[height - i - 1][width - j - 1] = sigs[height - i - 1][width - j - 1].mul(1 - mode);
				}
			}
		}
		return res;
	}
}
