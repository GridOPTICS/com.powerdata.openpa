package com.powerdata.openpa.padbc;

public interface BranchList extends BaseList
{
	public int getFromNode(int ndx);
	public int getToNode(int ndx);
	public float getR(int ndx);
	public float getX(int ndx);
}
