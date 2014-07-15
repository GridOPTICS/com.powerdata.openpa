package com.powerdata.openpa;

import com.powerdata.openpa.PAModel.ListMetaType;

public class SwitchedShuntListImpl extends ShuntListImpl<SwitchedShunt> implements SwitchedShuntList
{

	public static final SwitchedShuntList	Empty	= new SwitchedShuntListImpl();

	public SwitchedShuntListImpl(PAModel model, int[] keys)
	{
		super(model, keys);
		// TODO Auto-generated constructor stub
	}

	public SwitchedShuntListImpl(PAModel model, int size)
	{
		super(model, size);
		// TODO Auto-generated constructor stub
	}

	public SwitchedShuntListImpl() {super();}

	@Override
	public SwitchedShunt get(int index)
	{
		return new SwitchedShunt(this, index);
	}

	@Override
	protected ListMetaType getMetaType()
	{
		return ListMetaType.SwitchedShunt;
	}
	
	
}
