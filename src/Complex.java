
public class Complex {
	private double real;
	private double imag;
	
	public Complex(double real, double imag) {
		this.real = real;
		this.imag = imag;
	}
	
	public double getReal() {
		return this.real;
	}
	
	public double getImag() {
		return this.imag;
	}
	
	public Complex add(Complex x) {
		return new Complex(
				this.real + x.real, 
				this.imag + x.imag);
	}
	
	public Complex sub(Complex x) {
		return new Complex(
				this.real - x.real, 
				this.imag - x.imag);
	}
	
	public Complex mul(Complex x) {
		return new Complex(
				this.real * x.real - this.imag * x.imag, 
				this.real * x.imag + this.imag * x.real);
	}
}
