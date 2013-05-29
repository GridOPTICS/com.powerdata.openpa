package com.powerdata.openpa.tools;

/**
 * Complex number in polar form
 * 
 * @author chris@powerdata.com
 *
 */

public class PComplex
{
	private float _r;
	private float _theta;
	
	public static final Complex Zero = new Complex(0,0);

	public PComplex(float r, float theta)
	{
		_r = r;
		_theta = theta;
	}

	public float r() {return _r;}
	public float theta() {return _theta;}
	
	public PComplex mult(PComplex v) {return new PComplex(_r*v.r(), _theta+v.theta());}
	public PComplex mult(Complex v) {return mult(v.polar());}

	public PComplex div(PComplex v) {return new PComplex(_r/v.r(), _theta-v.theta());}
	
	public Complex cartesian()
	{
		return new Complex((float) (_r * Math.cos(_theta)),
				(float) (_r * Math.sin(_theta)));
	}
}

