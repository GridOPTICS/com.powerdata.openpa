package com.powerdata.openpa.psse.conversion;

import com.powerdata.openpa.psse.PsseModelException;
import com.powerdata.openpa.psse.TransformerInList;

public class XfrWndcw1 extends XfrWndTool
{

	@Override
	public float getRatio1(TransformerInList list, int ndx)
			throws PsseModelException
	{
		return list.getWINDV1(ndx);
	}

	@Override
	public float getRatio2(TransformerInList list, int ndx)
			throws PsseModelException
	{
		return list.getWINDV2(ndx);
	}

	@Override
	public float getRatio3(TransformerInList list, int ndx)
			throws PsseModelException
	{
		return list.getWINDV3(ndx);
	}
}