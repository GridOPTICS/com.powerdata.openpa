package com.powerdata.openpa.psse.conversion;

import com.powerdata.openpa.psse.PsseModelException;
import com.powerdata.openpa.psse.TransformerInList;

public abstract class XfrWndTool
{

	private static final XfrWndTool[]	_ToolSet;
	static
	{
		XfrWndTool cw1 = new XfrWndcw1();
		_ToolSet = new XfrWndTool[]
		{
			cw1,
			cw1,
			new XfrWndcw2(),
			new XfrWndcw3()
		};
	}
	
	public static XfrWndTool get(int cw) {return _ToolSet[cw];}

	public abstract float getRatio1(TransformerInList list, int ndx) throws PsseModelException;
	public abstract float getRatio2(TransformerInList list, int ndx) throws PsseModelException;
	public abstract float getRatio3(TransformerInList list, int ndx) throws PsseModelException;

}