
public class FourierComplex {
	private double real;
	private double imag;
	
	public FourierComplex(double real, double imag) {
		this.real = real;
		this.imag = imag;
	}
	
	public FourierComplex(int n) {
		this.real = n;
		this.imag = 0;
	}
	
	public double getReal() {
		return this.real;
	}
	
	public double getImag() {
		return this.imag;
	}
	
	public FourierComplex add(FourierComplex x) {
		return new FourierComplex(
				this.real + x.real, 
				this.imag + x.imag);
	}
	
	public FourierComplex sub(FourierComplex x) {
		return new FourierComplex(
				this.real - x.real, 
				this.imag - x.imag);
	}
	
	public FourierComplex mul(FourierComplex x) {
		return new FourierComplex(
				this.real * x.real - this.imag * x.imag, 
				this.real * x.imag + this.imag * x.real);
	}
	
	public FourierComplex mul(int x) {
		return new FourierComplex(
				this.real * x,
				this.imag * x);
	}
	
	public FourierComplex div(int x) {
		return new FourierComplex(
				this.real / x, 
				this.imag / x);
	}
	
	public double getPower() {
		return (this.real * this.real + this.imag * this.imag);
	}
	
	public double getRange() {
		return Math.pow(getPower(), 0.5);
	}
	
	public double getPhase() {
		return Math.atan2(this.imag, this.real);
	}
}
