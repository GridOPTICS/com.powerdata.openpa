package com.powerdata.openpa.pwrflow;

import java.util.Arrays;
import com.powerdata.openpa.ACBranch;
import com.powerdata.openpa.ACBranchListIfc;
import com.powerdata.openpa.PAModelException;
import com.powerdata.openpa.tools.Complex;
import com.powerdata.openpa.tools.PAMath;
import com.powerdata.openpa.pwrflow.ACBranchFlows.ACBranchFlow;
import com.powerdata.openpa.pwrflow.BusRefIndex.TwoTerm;

public class ACBranchFlowsI extends ACBranchExtListI<ACBranchFlow> implements ACBranchFlows
{
	float _sbase = 100f;
	float[] _fp, _tp, _fq, _tq;
	
	
	public ACBranchFlowsI(ACBranchListIfc<? extends ACBranch> branches, BusRefIndex bri)
		throws PAModelException
	{
		super(branches, bri);
		int n = size();
		_fp = new float[n];
		_tp = new float[n];
		_fq = new float[n];
		_tq = new float[n];
	}
	

	@Override
	public ACBranchFlow get(int index)
	{
		return new ACBranchFlow(this, index);
	}


	@Override
	public int size()
	{
		return _list.size();
	}
	
	@Override
	public void calc(float[] vm, float[] va) throws PAModelException
	{
		int n = size();
		Arrays.fill(_fp,  0f);
		Arrays.fill(_tp,  0f);
		Arrays.fill(_fq,  0f);
		Arrays.fill(_tq,  0f);
		BusRefIndex.TwoTerm bus = _bri.get2TBus(_list);
		int[] fb = bus.getFromBus(), tb = bus.getToBus();
		for(int i=0; i < n; ++i)
		{
			ACBranch br = _list.get(i);
			Complex y = _y.get(i);
			
			int f = fb[i], t = tb[i];
			float fvm = vm[f], tvm = vm[t], fva = va[f], tva = va[t];
			float shift = fva - tva - br.getShift();

			float ft = br.getFromTap(), tt = br.getToTap();
			float tvmpq = fvm * tvm / (ft * tt);
			float tvmp2 = fvm * fvm / (ft * ft);
			float tvmq2 = tvm * tvm / (tt * tt);
			float ctvmpq = tvmpq * (float) Math.cos(shift);
			float stvmpq = tvmpq * (float) Math.sin(shift);
			float yg = y.re(), yb = y.im();
			float gcos = ctvmpq * yg;
			float bcos = ctvmpq * yb;
			float gsin = stvmpq * yg;
			float bsin = stvmpq * yb;
			float ybmag = yb + br.getBmag();
			_fp[i] = -gcos - bsin + tvmp2 * yg;
			_fq[i] = -gsin + bcos - tvmp2 * (ybmag + br.getFromBchg());
			_tp[i] = -gcos + bsin + tvmq2 * yg;
			_tq[i] = gsin + bcos - tvmq2 * (ybmag + br.getToBchg());
		}
	}
	
	@Override
	public float getFromPpu(int ndx) {return _fp[ndx];}
	@Override
	public float getFromQpu(int ndx) {return _fq[ndx];}
	@Override
	public float getToPpu(int ndx) {return _tp[ndx];}
	@Override
	public float getToQpu(int ndx) {return _tq[ndx];}


	@Override
	public void applyMismatches(Mismatch pmm, Mismatch qmm) throws PAModelException
	{
		_apply(_fp, _tp, pmm);
		_apply(_fq, _tq, qmm);
	}
	
	void _apply(float[] f, float[] t, Mismatch mm) throws PAModelException
	{
		TwoTerm bx = mm.getBusRefIndex().get2TBus(_list);
		int[] fndx = bx.getFromBus(), tndx = bx.getToBus();
		int n = size();
		for(int i=0; i < n; ++i)
		{
			mm.add(fndx[i], f[i]);
			mm.add(tndx[i], t[i]);
		}
	}


	@Override
	public void update() throws PAModelException
	{
		_list.setFromP(PAMath.pu2mva(_fp, _sbase));
		_list.setToP(PAMath.pu2mva(_tp, _sbase));
		_list.setFromQ(PAMath.pu2mva(_fq, _sbase));
		_list.setToQ(PAMath.pu2mva(_tq, _sbase));
	}


	@Override
	public void update(int ndx) throws PAModelException
	{
		ACBranch b = getBranch(ndx);
		b.setFromP(PAMath.pu2mva(_fp[ndx], _sbase));
		b.setToP(PAMath.pu2mva(_tp[ndx], _sbase));
		b.setFromQ(PAMath.pu2mva(_fq[ndx], _sbase));
		b.setToQ(PAMath.pu2mva(_tq[ndx], _sbase));
	}


}































