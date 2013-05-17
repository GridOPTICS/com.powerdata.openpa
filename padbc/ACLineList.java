package com.powerdata.openpa.padbc;

public abstract class ACLineList extends BaseList<ACLine>
{
	public abstract int getFromNode(int ndx);
	public abstract int getToNode(int ndx);
	public abstract float getR(int ndx);
	public abstract float getX(int ndx);
	public abstract float getFromBChg(int ndx);
	public abstract float getToBChg(int ndx);
	public abstract void updateActvPower(int ndx, float p);
	public abstract void updateReacPower(int ndx, float q);
	
//	public abstract void updateMW(int ndx, float p);
//	public abstract void updateMVAr(int ndx, float q);
	
	@Override
	public ACLine get(int ndx) {return new ACLine(ndx, this);}

}