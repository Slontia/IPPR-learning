
public class ProgressBar {
	final String label;
	final int barLen;
	int lastBar = -1;
	
	public ProgressBar(String label, int barLen) {
		this.label = label;
		this.barLen = barLen;
	}
	
	public void showProgressStatus(double progress) {
		int bar = (int) (progress * barLen);
		if (bar != lastBar) {
			System.out.print(label + " ‖");
			for (int i = 0; i < barLen; i++) {
				if (i < bar) {
					System.out.print("█");
				} else {
					System.out.print("░");
				}
			}
			System.out.println("‖ ");
			lastBar = bar;
		}
	}
}
