package com.powerdata.openpa.pwrflow;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import com.powerdata.openpa.Bus;
import com.powerdata.openpa.Island;
import com.powerdata.openpa.IslandList;
import com.powerdata.openpa.PAModelException;
import com.powerdata.openpa.pwrflow.Mismatch.WorstMM;

public class ConvergenceList extends AbstractList<com.powerdata.openpa.pwrflow.ConvergenceList.ConvergenceInfo>
{
	public enum Status
	{
		Untested, Converge, NoReferenceBus, VoltageCollapse, HighVoltage,
			Pmismatch, Qmismatch, BlowsUp, ReferenceOnly; 
	}

	public interface WorstVoltage
	{
		Bus getBus();
		float getVM();
	}
	
	IslandList _islands;
	Status[] _status;
	Mismatch _pmm, _qmm;
	WorstMM[] _pw, _qw;
	int[] _worstvbus;
	float[] _worstv;
	float _ptol, _qtol;
	float[] _vm;
	int[] _niter;
	
	public ConvergenceList(IslandList hotislands, BusTypeUtil btu,
			Mismatch pmm, Mismatch qmm, float ptol, float qtol, float[] vm)
	{
		
		_islands = hotislands;
		int n = hotislands.size();
		_status = new Status[n];
		_niter = new int[n];
		_pmm = pmm;
		_qmm = qmm;
		_pw = new WorstMM[n];
		_qw = new WorstMM[n];
		_worstvbus = new int[n];
		_worstv = new float[n];
		_ptol = ptol;
		_qtol = qtol;
		for (int i = 0; i < n; ++i)
		{
			_status[i] = (btu.getBuses(BusType.Reference, _islands.get(i)).length == 0) ?
				Status.NoReferenceBus : Status.Untested;
		}
		_vm = vm;
	}

	private interface CLStringBuilder
	{
		String build(ConvergenceInfo ci) throws PAModelException;
	}
	
	static private Map<Status,CLStringBuilder> _PrtMap = new EnumMap<>(Status.class);
	static
	{
		CLStringBuilder sbnd = ConvergenceInfo::sbldrNoDetail,
				sball = ConvergenceInfo::sbldrAll,
				sbvolt = ConvergenceInfo::sbldrVoltage;
		_PrtMap.put(Status.Untested, sbnd);
		_PrtMap.put(Status.Converge, sball);
		_PrtMap.put(Status.NoReferenceBus, sbnd);
		_PrtMap.put(Status.VoltageCollapse, sbvolt);
		_PrtMap.put(Status.HighVoltage, sbvolt);
		_PrtMap.put(Status.Pmismatch, sball);
		_PrtMap.put(Status.Qmismatch, sball);
		_PrtMap.put(Status.BlowsUp, sball);
		_PrtMap.put(Status.ReferenceOnly, sbnd);
	}
	
	public class ConvergenceInfo
	{
		WorstVoltage _wvolt = new WorstVoltage()
		{
			@Override
			public Bus getBus()
			{
				return _pmm.getBusRefIndex().getBuses().get(_worstvbus[_ndx]);
			}

			@Override
			public float getVM()
			{
				return _worstv[_ndx];
			}

			@Override
			public String toString()
			{ 
				try
				{
					return String.format("%f @ %s", getVM(), getBus().getName());
				}
				catch (PAModelException e)
				{
					e.printStackTrace();
				}
				return "";
			}
		};
		int _ndx;
		ConvergenceInfo(int ndx) {_ndx = ndx;}
		
		String sbldrNoDetail() throws PAModelException
		{
			return String.format("[%s] Island %d niter=%d", getStatus(), getIsland().getIndex(), getNumIter());
		}
		String sbldrVoltage()
		{
			return String.format("[%s] Island %d  niter=%d, worst voltage %s",
				getStatus(), getIsland().getIndex(), getNumIter(), getWorstVoltage());
		}
		String sbldrAll()
		{
			return String.format("[%s] Island %d niter=%d, pmm %s, qmm %s, worst voltage %s",
				getStatus(), getIsland().getIndex(), getNumIter(), getWorstP().toString("MW"), getWorstQ().toString("MVAr"), getWorstVoltage());
		}
		public Status getStatus() {return _status[_ndx];}
		public WorstMM getWorstP() {return _pw[_ndx];}
		public WorstMM getWorstQ() {return _qw[_ndx];}
		public WorstVoltage getWorstVoltage()
		{
			return _wvolt;
		}
		public Island getIsland()
		{
			return _islands.get(_ndx);
		}
		
		public int getNumIter()
		{
			return _niter[_ndx];
		}
		
		public boolean completed()
		{
			return !_Incomplete.contains(_status[_ndx]);
		}
		@Override
		public String toString()
		{
			try
			{
				return _PrtMap.get(getStatus()).build(this);
			}
			catch (PAModelException e)
			{
				e.printStackTrace();
			}
			return null;
		}

		public float getLoadMW()
		{
			// TODO Auto-generated method stub
			return 0;
		}
		
		
	}

	/**
	 * Test for convergence in each island
	 * 
	 * @return true if we either converged or know we failed (returns internal
	 *         call to completed())
	 */
	public boolean test()
	{
		int nislands = _islands.size();
		Arrays.fill(_pw, null);
		Arrays.fill(_qw, null);
		Arrays.fill(_worstvbus, 0);
		Arrays.fill(_worstv, 1f);
		
		for(int i=0; i < nislands; ++i)
		{
			Island island = _islands.get(i);
			if (_status[i] == Status.NoReferenceBus || _status[i] == Status.Converge)
				continue;
			/* first check voltage problems */
			++_niter[i];
			if (goodVoltage(i))
			{
				WorstMM p = _pmm.test(island);
				WorstMM q = _qmm.test(island);
				if (p.getStatus() == Mismatch.Status.BlowsUp
						|| q.getStatus() == Mismatch.Status.BlowsUp)
				{
					_status[i] = Status.BlowsUp;
				}
				else if (p.getStatus() == Mismatch.Status.RefOnly ||
						q.getStatus() == Mismatch.Status.RefOnly)
				{
					_status[i] = Status.ReferenceOnly;
				}
				else
				{
					if(Math.abs(q.getValue()) > _qtol)
						_status[i] = Status.Qmismatch;
					else if (Math.abs(p.getValue()) > _ptol)
						_status[i] = Status.Pmismatch;
					else
						_status[i] = Status.Converge;
					
				}			
				
				_pw[i] = p;
				_qw[i] = q;
			}
//			System.out.println(get(i));
		}
		return completed();
	}

	/**
	 * Check bus voltage problems in an island
	 * @param island
	 * @return Returns true if voltage is reasonable
	 */
	boolean goodVoltage(int islx)
	{
		float wn = Float.MAX_VALUE, wx = Float.MIN_VALUE;
		int wnbus=-1, wxbus=-1;
		
		Collection<int[]> tbus = _qmm.getTestBuses(_islands.get(islx));
		boolean rv = true;
		for(int[] list : tbus)
		{
			for(int b : list)
			{
				float v = _vm[b];
				if (wn > v)
				{
					wn = v;
					wnbus = b;
				}
				if (wx < v)
				{
					wx = v;
					wxbus = b;
				}
			}
		}

		_worstv[islx] = wn;
		_worstvbus[islx] = wnbus;

		_worstv[islx] = wx;
		_worstvbus[islx] = wxbus;

		if (wn < 0.5f)
		{
			_status[islx] = Status.VoltageCollapse;
			rv = false;
		}
		else if (wx > 2f)
		{
			_status[islx] = Status.HighVoltage;
			rv = false;
		}
		
		return rv;
	}

	@Override
	public ConvergenceInfo get(int index)
	{
		return new ConvergenceInfo(index);
	}

	@Override
	public int size()
	{
		return _islands.size();
	}

	static Set<Status> _Incomplete = EnumSet.of(Status.Untested,
		Status.Pmismatch, Status.Qmismatch);
	
	public boolean completed()
	{
		for(Status s : _status)
			if (_Incomplete.contains(s))
				return false;
		return true;
	}
}
