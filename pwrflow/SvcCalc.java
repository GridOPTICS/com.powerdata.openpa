package com.powerdata.openpa.pwrflow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import com.powerdata.openpa.BusList;
import com.powerdata.openpa.PAModel;
import com.powerdata.openpa.PAModelException;
import com.powerdata.openpa.PflowModelBuilder;
import com.powerdata.openpa.SVC.SVCState;
import com.powerdata.openpa.SVCList;
import com.powerdata.openpa.impl.GroupMap;
import com.powerdata.openpa.tools.PAMath;


// TODO:  Make sure we are setting the SVC to a PV bus if slope is 0.

public class SvcCalc extends CalcBase
{
	int[] _busndx;
	float _sbase;
	BusList _buses;
	SVCList _svcs;
	float[] _b, _q;
	float[] _qmin, _qmax, _slope, _basekv;
	SVCState[] _state;
	
	public SvcCalc(float sbase, BusRefIndex bndx, SVCList svcs) throws PAModelException
	{
		super(svcs);
		_sbase = sbase;
		_buses = bndx.getBuses();
		_svcs = svcs;
		setup(bndx);
	}

	/**
	 *	Same constructor, but add a mapping to designate which SVCs's should not
	 *  calculated because they are calculated elsewhere (i.e., behave like a PV bus
	 *  in a power flow) 
	 */
	public SvcCalc(float sbase, BusRefIndex bndx, int[] insvc, SVCList svcs)
			throws PAModelException
	{
		super(svcs);
		_sbase = sbase;
		_buses = bndx.getBuses();
		_svcs = svcs;
		setup(bndx);
		_oosndx = insvc;
	}
	
	private void setup(BusRefIndex bref) throws PAModelException
	{
		_busndx = bref.get1TBus(_svcs);
		_qmin = PAMath.mva2pu(_svcs.getMinQ(), _sbase);
		_qmax = PAMath.mva2pu(_svcs.getMaxQ(), _sbase);
		_slope = _svcs.getSlope();
		
		int n = _svcs.size();
		_basekv = new float[n];
		for(int i=0; i < n; ++i)
		{
			_basekv[i] = _buses.getVoltageLevel(_busndx[i]).getBaseKV();
		}
	}

	public void calc() throws PAModelException
	{
		calc(null, PAMath.vmpu(_buses));
	}

	@Override
	public void calc(float[] varad, float[] vmpu)
	{
		try
		{
			boolean[] isregkv = _svcs.isRegKV();
			float[] vs = _svcs.getVS(), qs = _svcs.getQS();
			int[] insvc = getInSvc();
			int ninsvc = insvc.length, nsvc = _svcs.size();
			_state = new SVCState[nsvc];
			_b = new float[nsvc];
			_q = new float[nsvc];
			Arrays.fill(_state, SVCState.Off);
			for (int in = 0; in < ninsvc; ++in)
			{
				int i = insvc[in];
				float qmax = _qmax[i], qmin = _qmin[i];
				float s = _slope[i];
				float vmsc = vs[i] / _basekv[i];
				float vmin = vmsc - s * qmax;
				float vmax = vmsc - s * qmin;
				float vm = vmpu[_busndx[i]];
				float vmsq = vm * vm;
				float bcap = qmax / (vmin * vmin);
				float breac = qmin / (vmax * vmax);
				if (isregkv[i])
				{
					if (vm < vmin)
					{
						_b[i] = bcap;
						_q[i] = bcap * vmsq;
						_state[i] = SVCState.CapacitorLimit;
					}
					else if (vm > vmax)
					{
						_b[i] = breac;
						_q[i] = breac * vmsq;
						_state[i] = SVCState.ReactorLimit;
					}
					else
					{
						_state[i] = SVCState.Normal;
						_q[i] = (vmsc - vm) / s;
						_b[i] = -1f / s;
					}
				}
				else
				{
					_state[i] = SVCState.FixedMVAr;
					float q = qs[i];
					if (q > 0f)
						q = Math.min(q, bcap * vmsq);
					else
						q = Math.max(q, breac * vmsq);
					_q[i] = q;
				}
			}
		}
		catch (PAModelException e)
		{
			_e = e;
			return;
		}
	}
	
	/** get solved MVAr's */
	public float[] getQ() {return _q;}
	
	/** get susceptance values appropriate for B'' */
	public float[] getBpp() {return _b;}
	
	
	public static void main(String[] args) throws Exception
	{
		String uri = null;
		File outdir = new File(System.getProperty("user.dir"));
		for(int i=0; i < args.length;)
		{
			String s = args[i++].toLowerCase();
			int ssx = 1;
			if (s.startsWith("--")) ++ssx;
			switch(s.substring(ssx))
			{
				case "uri":
					uri = args[i++];
					break;
				case "outdir":
					outdir = new File(args[i++]);
					break;
			}
		}
		if (uri == null)
		{
			System.err.format("Usage: -uri model_uri "
					+ "[ --outdir output_directory (deft to $CWD ]\n");
			System.exit(1);
		}

		PflowModelBuilder bldr = PflowModelBuilder.Create(uri);
		bldr.enableFlatVoltage(false);
		PAModel m = bldr.load();
		SVCList svcs = m.getSVCs();
		BusRefIndex bri = BusRefIndex.CreateFromSingleBus(m);
		SvcCalc sc = new SvcCalc(m.getSBASE(), bri, svcs);
		sc.calc();
		
		float[] q = sc.getQ();
		
		int n = q.length;
		PrintWriter pw = new PrintWriter(new BufferedWriter(
			new FileWriter(new File(outdir,"svccalc.csv"))));
		pw.println("ID,Bus,MVAr");
		
		for(int i=0; i < n; ++i)
		{
			pw.format("%s,%s,%f\n", svcs.getID(i), 
				bri.getBuses().getByBus(svcs.getBus(i)).getName(),
				q[i]); 
		}
		pw.close();
	}

	public int[] getBus() {return _busndx;}

	public SVCState[] getState() {return _state;}

	@Override
	public void applyMismatches(float[] pmm, float[] qmm)
	{
		for(int bx : getInSvc())
			qmm[_busndx[bx]] -= _q[bx];
	}

	public void update() throws PAModelException
	{
		_svcs.setQ(PAMath.pu2mva(_q, _sbase));
	}
}