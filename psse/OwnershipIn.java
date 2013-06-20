package com.powerdata.openpa.psse;

import com.powerdata.openpa.tools.BaseObject;

public class OwnershipIn extends BaseObject
{
	protected OwnershipInList _list;

	public OwnershipIn(int ndx, OwnershipInList list)
	{
		super(ndx);
		_list = list;
	}

	@Override
	public String getObjectID() {return _list.getObjectID(_ndx);}

	/* Convenience Methods */

	public OwnerIn getOwner() throws PsseModelException {return _list.getOwner(_ndx);}
	
	/* raw psse methods */
	
	/** Owner number */
	public int getO() {return _list.getO(_ndx);}
	/** Fraction of total ownership assigned to owner */
	public float getF() {return _list.getF(_ndx);}
}