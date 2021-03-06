package com.powerdata.openpa.tools.psmfmt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import com.powerdata.openpa.PAModel;
import com.powerdata.openpa.PAModelException;
import com.powerdata.openpa.TransformerList;

public class CaseRatioTapChangerOPA extends ExportOpenPA<TransformerList>
{
	FmtInfo[] _frmi;
	public CaseRatioTapChangerOPA(PAModel m) throws PAModelException
	{
		super(m.getTransformers(), CaseRatioTapChanger.values().length);
		assignFrom();
		_frmi = _finfo.clone();
		assignTo();
	}
	
	void assignFrom()
	{
		assign(CaseRatioTapChanger.ID, new StringWrap(i -> _list.getID(i)+"_ftap"));
		assign(CaseRatioTapChanger.Ratio,
			i -> String.valueOf(_list.getFromTap(i)));
	}
	
	void assignTo()
	{
		assign(CaseRatioTapChanger.ID, new StringWrap(i -> _list.getID(i)+"_ttap"));
		assign(CaseRatioTapChanger.Ratio,
			i -> String.valueOf(_list.getToTap(i)));
	}

	@Override
	void export(File outputdir) throws PAModelException, IOException
	{
		PrintWriter pw = new PrintWriter(new BufferedWriter(
			new FileWriter(new File(outputdir, getPsmFmtName()+".csv"))));
		printHeader(pw);
		printData(pw, _frmi, getCount());
		printData(pw);
		pw.close();

	}
	
	
	@Override
	protected String getPsmFmtName()
	{
		return "PsmCase" + PsmCaseFmtObject.RatioTapChanger.toString();
	}
}
