package com.powerdata.openpa.impl;

import com.powerdata.openpa.ColumnMeta;
import com.powerdata.openpa.Shunt;
import com.powerdata.openpa.ShuntList;

public abstract class ShuntListI<T extends Shunt> 
	extends OneTermDevListI<T> implements ShuntList<T>
{
	interface ShuntEnum extends OneTermDevEnum
	{
		ColumnMeta b();
	}
	
	FloatData _b;
	
	public ShuntListI(PAModelI model, int[] keys, ShuntEnum le)
	{
		super(model, keys, le);
		setFields(le);
	}
	public ShuntListI(PAModelI model, int size, ShuntEnum le)
	{
		super(model, size, le);
		setFields(le);
	}

	public ShuntListI() {super();}
	
	private void setFields(ShuntEnum le)
	{
		_b = new FloatData(le.b());
	}

	@Override
	public float getB(int ndx)
	{
		return _b.get(ndx);
	}
	@Override
	public void setB(int ndx, float b)
	{
		_b.set(ndx, b);
	}
	@Override
	public float[] getB()
	{
		return _b.get();
	}
	@Override
	public void setB(float[] b)
	{
		_b.set(b);
	}
	
}