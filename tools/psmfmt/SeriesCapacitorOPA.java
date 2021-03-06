package com.powerdata.openpa.tools.psmfmt;

import com.powerdata.openpa.BusList;
import com.powerdata.openpa.PAModel;
import com.powerdata.openpa.PAModelException;
import com.powerdata.openpa.SeriesCapList;
import com.powerdata.openpa.pwrflow.BusRefIndex;
import com.powerdata.openpa.pwrflow.BusRefIndex.TwoTerm;

public class SeriesCapacitorOPA extends ExportOpenPA<SeriesCapList>
{
	public SeriesCapacitorOPA(PAModel m, BusRefIndex bri) throws PAModelException
	{
		super(m.getSeriesCapacitors(), SeriesCapacitor.values().length);
		TwoTerm bx = bri.get2TBus(_list);
		BusList buses = bri.getBuses();
		assign(SeriesCapacitor.ID, new StringWrap(i -> _list.getID(i)));
		assign(SeriesCapacitor.Name, new StringWrap(i -> _list.getName(i)));
		assign(SeriesCapacitor.Node1, new StringWrap(i -> buses.get(bx.getFromBus()[i])
				.getID()));
		assign(SeriesCapacitor.Node2, new StringWrap(i -> buses.get(bx.getToBus()[i])
				.getID()));
		assign(SeriesCapacitor.R, i -> String.valueOf(_list.getR(i)));
		assign(SeriesCapacitor.X, i -> String.valueOf(_list.getX(i)));
		assign(SeriesCapacitor.NormalOperatingLimit,
			i -> String.valueOf(_list.getLTRating(i)));
	}

	
	@Override
	protected String getPsmFmtName()
	{
		return PsmMdlFmtObject.SeriesCapacitor.toString();
	}
}
