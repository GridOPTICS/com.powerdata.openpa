package com.powerdata.openpa.psse;

public abstract class TransformerList<T extends Transformer> 
	extends PsseBaseList<T>
{
	public TransformerList(PsseModel model) {super(model);}

	protected float rmDefault(int ndx, char mm, int wnd, float defval, int abscod)
			throws PsseModelException
	{
		int cw = getCW(ndx);
		if (cw == 2 && (abscod ==1 || abscod == 2))
		{
			throw new PsseModelException("No default allowed for RM"
					+ mm + wnd + " when |COD| is 1 or 2 and CW is 2");
		}
		else if (abscod == 3)
		{
			throw new PsseModelException("No default allowed for RM" + mm
					+ wnd + " when |COD| is 3");
		}
		return defval;
	}		
		
	protected float vmDefault(int ndx, char mm, int wnd, float defval,
			int abscod) throws PsseModelException
	{
		if (abscod == 2 || abscod == 3)
		{
			throw new PsseModelException("No default allowed for VM" + mm + wnd
					+ " when |COD| is 2 or 3");
		}
		return defval;
	}

	protected float ratioLim(int ndx, int abscod, Bus bus, float val)
			throws PsseModelException
	{
		if (abscod == 1 || abscod == 2)
		{
			return (getCW(ndx) == 2) ? val / getBus1(ndx).getBASKV() : val;
		}
		else if (abscod == 0 || abscod == 4)
		{
			return val;
		}
		else
		{
			throw new PsseModelException(
					"No valid ratio limits for Transformer " + getObjectID(ndx)
							+ " (index " + ndx + ")");
		}
	}

	protected float shiftLim(int ndx, int rcod, float val)
			throws PsseModelException
	{
		if (Math.abs(rcod) != 3)
		{
			throw new PsseModelException(
					"No valid phase shift limits for Transformer "
							+ getObjectID(ndx) + " (index " + ndx + ")");
		}
		return val;
	}

	protected float voltLim(int ndx, int abscod, float val)
			throws PsseModelException
	{
		if (abscod == 2 || abscod == 3)
		{
			throw new PsseModelException(
					"No valid controlled bus voltage limits for Transformer "
							+ getObjectID(ndx) + " (index " + ndx + ")");
		}
		return val;
	}

	protected float reacPwrLim(int ndx, int abscod, float val)
			throws PsseModelException
	{
		if (abscod != 2)
		{
			throw new PsseModelException(
					"No valid reactive power limits for Transformer "
							+ getObjectID(ndx) + " (index " + ndx + ")");
		}
		return val;
	}

	protected float actvPwrLim(int ndx, int abscod, float val)
			throws PsseModelException
	{
		if (abscod != 3)
		{
			throw new PsseModelException(
					"No valid active power limits for Transformer "
							+ getObjectID(ndx) + " (index " + ndx + ")");
		}
		return val;
	}	
	
	/* Convenience Methods */
	
	public Bus getBus1(int ndx) throws PsseModelException {return _model.getBus(getI(ndx));}
	public Bus getBus2(int ndx) throws PsseModelException {return _model.getBus(getJ(ndx));}
	public Bus getBus3(int ndx) throws PsseModelException {return _model.getBus(getK(ndx));}
	public float getMagCondPerUnit(int ndx) {return 0;}
	public float getMagSuscPerUnit(int ndx) {return 0;}
	public float getNoLoadLoss(int ndx) {return 0;}
	public float getExcitingCurrent(int ndx) {return 0;}
	public TransformerStatus getInitTransformerStat(int ndx)
	{
		return TransformerStatus.fromCode(getSTAT(ndx));
	}
	public float getResistance1_2(int ndx) {return 0;}
	public float getReactance1_2(int ndx)  {return 0;}
	public float getResistance2_3(int ndx) {return 0;}
	public float getReactance2_3(int ndx) {return 0;}
	public float getResistance3_1(int ndx) {return 0;}
	public float getReactance3_1(int ndx) {return 0;}
	public float getWnd1Ratio(int ndx) throws PsseModelException
	{
		float wv1 = getWINDV1(ndx);
		return (getCW(ndx)==2)?wv1/getBus1(ndx).getBASKV():wv1;
	}
	public float getWnd1NomKV(int ndx) throws PsseModelException
	{
		float nv1 = getNOMV1(ndx);
		return (nv1==0F)?getBus1(ndx).getBASKV():nv1;
	}
	public TransformerCtrlMode getCtrlMode1(int ndx) {return TransformerCtrlMode.fromCode(Math.abs(getCOD1(ndx)));}
	public boolean enableAutoCtrl1(int ndx) {return getCOD1(ndx) > 0;}
	public Bus getCtrlBus1(int ndx) throws PsseModelException
	{
		String cod = getCONT1(ndx);
		return _model.getBus((cod.charAt(0)=='-')?cod.substring(1):cod);
	}
	public boolean ctrlOnWnd1(int ndx) {return getCONT1(ndx).charAt(0) == '-';}
	public float getMaxRatio1(int ndx) throws PsseModelException {return ratioLim(ndx, getCOD1(ndx), getBus1(ndx), getRMA1(ndx));}
	public float getMinRatio1(int ndx) throws PsseModelException {return ratioLim(ndx, getCOD1(ndx), getBus1(ndx), getRMI1(ndx));}
	public float getMaxShift1(int ndx) throws PsseModelException {return shiftLim(ndx, getCOD1(ndx), getRMA1(ndx));}
	public float getMinShift1(int ndx) throws PsseModelException {return shiftLim(ndx, getCOD1(ndx), getRMI1(ndx));}
	public float getMaxVolt1(int ndx) throws PsseModelException {return voltLim(ndx, getCOD1(ndx), getVMA1(ndx));}   
	public float getMinVolt1(int ndx) throws PsseModelException  {return voltLim(ndx, getCOD1(ndx), getVMI1(ndx));}
	public float getMaxReacPwr1(int ndx) throws PsseModelException {return reacPwrLim(ndx, getCOD1(ndx), getVMA1(ndx));}
	public float getMinReacPwr1(int ndx) throws PsseModelException {return reacPwrLim(ndx, getCOD1(ndx), getVMI1(ndx));}
	public float getMaxActvPwr1(int ndx) throws PsseModelException {return actvPwrLim(ndx, getCOD1(ndx), getVMA1(ndx));}
	public float getMinActvPwr1(int ndx) throws PsseModelException {return actvPwrLim(ndx, getCOD1(ndx), getVMA1(ndx));}
	public ImpCorrTbl getImpCorrTbl1(int ndx) throws PsseModelException {return _model.getImpCorrTables().get(String.valueOf(getTAB1(ndx)));}

	public float getWnd2Ratio(int ndx) throws PsseModelException
	{
		float wv2 = getWINDV2(ndx);
		return (getCW(ndx)==2)?wv2/getBus2(ndx).getBASKV():wv2;
	}
	public float getWnd2NomKV(int ndx) throws PsseModelException
	{
		float nv = getNOMV2(ndx);
		return (nv==0F)?getBus2(ndx).getBASKV():nv;
	}
	public TransformerCtrlMode getCtrlMode2(int ndx) {return TransformerCtrlMode.fromCode(Math.abs(getCOD2(ndx)));}
	public boolean enableAutoCtrl2(int ndx) {return getCOD2(ndx) > 0;}
	public Bus getCtrlBus2(int ndx) throws PsseModelException
	{
		String cod = getCONT2(ndx);
		return _model.getBus((cod.charAt(0)=='-')?cod.substring(1):cod);
	}
	public boolean ctrlOnWnd2(int ndx) {return getCONT2(ndx).charAt(0) == '-';}
	public float getMaxRatio2(int ndx) throws PsseModelException {return ratioLim(ndx, getCOD2(ndx), getBus2(ndx), getRMA2(ndx));}
	public float getMinRatio2(int ndx) throws PsseModelException {return ratioLim(ndx, getCOD2(ndx), getBus2(ndx), getRMI2(ndx));}
	public float getMaxShift2(int ndx) throws PsseModelException {return shiftLim(ndx, getCOD2(ndx), getRMA2(ndx));}
	public float getMinShift2(int ndx) throws PsseModelException {return shiftLim(ndx, getCOD2(ndx), getRMI2(ndx));}
	public float getMaxVolt2(int ndx) throws PsseModelException  {return voltLim(ndx, getCOD2(ndx), getVMA2(ndx));}   
	public float getMinVolt2(int ndx) throws PsseModelException  {return voltLim(ndx, getCOD2(ndx), getVMI2(ndx));}
	public float getMaxReacPwr2(int ndx) throws PsseModelException {return reacPwrLim(ndx, getCOD2(ndx), getVMA2(ndx));}
	public float getMinReacPwr2(int ndx) throws PsseModelException {return reacPwrLim(ndx, getCOD2(ndx), getVMI2(ndx));}
	public float getMaxActvPwr2(int ndx) throws PsseModelException {return actvPwrLim(ndx, getCOD2(ndx), getVMA2(ndx));}
	public float getMinActvPwr2(int ndx) throws PsseModelException {return actvPwrLim(ndx, getCOD2(ndx), getVMA2(ndx));}
	public ImpCorrTbl getImpCorrTbl2(int ndx) throws PsseModelException {return _model.getImpCorrTables().get(String.valueOf(getTAB2(ndx)));}
	
	public float getWnd3Ratio(int ndx) throws PsseModelException
	{
		float wv = getWINDV3(ndx);
		return (getCW(ndx)==2)?wv/getBus3(ndx).getBASKV():wv;
	}
	public float getWnd3NomKV(int ndx) throws PsseModelException
	{
		float nv = getNOMV3(ndx);
		return (nv==0F)?getBus3(ndx).getBASKV():nv;
	}
	public TransformerCtrlMode getCtrlMode3(int ndx) {return TransformerCtrlMode.fromCode(Math.abs(getCOD3(ndx)));}
	public boolean enableAutoCtrl3(int ndx) {return getCOD3(ndx) > 0;}
	public Bus getCtrlBus3(int ndx) throws PsseModelException
	{
		String cod = getCONT3(ndx);
		return _model.getBus((cod.charAt(0)=='-')?cod.substring(1):cod);
	}
	public boolean ctrlOnWnd3(int ndx) {return getCONT3(ndx).charAt(0) == '-';}
	public float getMaxRatio3(int ndx) throws PsseModelException {return ratioLim(ndx, getCOD3(ndx), getBus3(ndx), getRMA3(ndx));}
	public float getMinRatio3(int ndx) throws PsseModelException {return ratioLim(ndx, getCOD3(ndx), getBus3(ndx), getRMI3(ndx));}
	public float getMaxShift3(int ndx) throws PsseModelException {return shiftLim(ndx, getCOD3(ndx), getRMA3(ndx));}
	public float getMinShift3(int ndx) throws PsseModelException {return shiftLim(ndx, getCOD3(ndx), getRMI3(ndx));}
	public float getMaxVolt3(int ndx) throws PsseModelException  {return voltLim(ndx, getCOD3(ndx), getVMA3(ndx));}   
	public float getMinVolt3(int ndx) throws PsseModelException  {return voltLim(ndx, getCOD3(ndx), getVMI3(ndx));}
	public float getMaxReacPwr3(int ndx) throws PsseModelException {return reacPwrLim(ndx, getCOD3(ndx), getVMA3(ndx));}
	public float getMinReacPwr3(int ndx) throws PsseModelException {return reacPwrLim(ndx, getCOD3(ndx), getVMI3(ndx));}
	public float getMaxActvPwr3(int ndx) throws PsseModelException {return actvPwrLim(ndx, getCOD3(ndx), getVMA3(ndx));}
	public float getMinActvPwr3(int ndx) throws PsseModelException {return actvPwrLim(ndx, getCOD3(ndx), getVMA3(ndx));}
	public ImpCorrTbl getImpCorrTbl3(int ndx) throws PsseModelException {return _model.getImpCorrTables().get(String.valueOf(getTAB3(ndx)));}

	
	/* Raw methods */
	
	public abstract String getI(int ndx);
	public abstract String getJ(int ndx);
	public abstract String getK(int ndx);
	public abstract String getCKT(int ndx);
	public abstract int getCW(int ndx);
	public abstract int getCZ(int ndx);
	public abstract int getCM(int ndx);
	public abstract float getMAG1(int ndx);
	public abstract float getMAG2(int ndx);
	public abstract int getNMETR(int ndx);
	public abstract String getNAME(int ndx);
	public abstract int getSTAT(int ndx);
	public abstract float getR1_2(int ndx);
	public abstract float getX1_2(int ndx);
	public abstract float getSBASE1_2(int ndx);
	public abstract float getR2_3(int ndx);
	public abstract float getX2_3(int ndx);
	public abstract float getSBASE2_3(int ndx);
	public abstract float getR3_1(int ndx);
	public abstract float getX3_1(int ndx);
	public abstract float getSBASE3_1(int ndx);
	public abstract float getVMSTAR(int ndx);
	public abstract float getANSTAR(int ndx);
	public abstract float getWINDV1(int ndx) throws PsseModelException;
	public abstract float getNOMV1(int ndx);
	public abstract float getANG1(int ndx);
	public abstract float getRATA1(int ndx);
	public abstract float getRATB1(int ndx);
	public abstract float getRATC1(int ndx);
	public abstract int getCOD1(int ndx);
	public abstract String getCONT1(int ndx);
	public abstract float getRMA1(int ndx) throws PsseModelException;
	public abstract float getRMI1(int ndx) throws PsseModelException;
	public abstract float getVMA1(int ndx) throws PsseModelException;
	public abstract float getVMI1(int ndx) throws PsseModelException;
	public abstract int getNTP1(int ndx);
	public abstract int getTAB1(int ndx);
	public abstract float getCR1(int ndx);
	public abstract float getCX1(int ndx);

	public abstract float getWINDV2(int ndx) throws PsseModelException;
	public abstract float getNOMV2(int ndx);
	public abstract float getANG2(int ndx);
	public abstract float getRATA2(int ndx);
	public abstract float getRATB2(int ndx);
	public abstract float getRATC2(int ndx);
	public abstract int getCOD2(int ndx);
	public abstract String getCONT2(int ndx);
	public abstract float getRMA2(int ndx) throws PsseModelException;
	public abstract float getRMI2(int ndx) throws PsseModelException;
	public abstract float getVMA2(int ndx) throws PsseModelException;
	public abstract float getVMI2(int ndx) throws PsseModelException;
	public abstract int getNTP2(int ndx);
	public abstract int getTAB2(int ndx);
	public abstract float getCR2(int ndx);
	public abstract float getCX2(int ndx);

	// TODO:  Do we need any special checking if it's 2-winding(and hence none of these are valid...or line 4 for that matter)
	public abstract float getWINDV3(int ndx) throws PsseModelException;
	public abstract float getNOMV3(int ndx);
	public abstract float getANG3(int ndx);
	public abstract float getRATA3(int ndx);
	public abstract float getRATB3(int ndx);
	public abstract float getRATC3(int ndx);
	public abstract int getCOD3(int ndx);
	public abstract String getCONT3(int ndx);
	public abstract float getRMA3(int ndx) throws PsseModelException;
	public abstract float getRMI3(int ndx) throws PsseModelException;
	public abstract float getVMA3(int ndx) throws PsseModelException;
	public abstract float getVMI3(int ndx) throws PsseModelException;
	public abstract int getNTP3(int ndx);
	public abstract int getTAB3(int ndx);
	public abstract float getCR3(int ndx);
	public abstract float getCX3(int ndx);

	public abstract OwnershipList<?> getOwnership(int ndx);


	/* Default methods */
	
	public String getDeftK(int ndx) {return "0";}
	public String getDeftCKT(int ndx) {return "1";}
	public int getDeftCW(int ndx) {return 1;}
	public int getDeftCZ(int ndx) {return 1;}
	public int getDeftCM(int ndx) {return 1;}
	public float getDeftMAG1(int ndx) {return 0;}
	public float getDeftMAG2(int ndx)  {return 0;}
	public int getDeftNMETR(int ndx) {return 2;}
	public String getDeftNAME(int ndx) {return "";}
	public int getDeftSTAT(int ndx) {return 1;}
	public float getDeftR1_2(int ndx) {return 0F;}
	public float getDeftSBASE1_2(int ndx) {return _model.getSBASE();}
	public float getDeftR2_3(int ndx) {return 0F;}
	public float getDeftSBASE2_3(int ndx) {return _model.getSBASE();}
	public float getDeftR3_1(int ndx) {return 0F;}
	public float getDeftSBASE3_1(int ndx) {return _model.getSBASE();}
	public float getDeftVMSTAR(int ndx) {return 1F;}
	public float getDeftANSTAR(int ndx) {return 0F;}
	public float getDeftWINDV1(int ndx) throws PsseModelException {return (getCW(ndx)==2)?getBus1(ndx).getBASKV():1F;}
	public float getDeftNOMV1(int ndx)  {return 0F;}
	public float getDeftANG1(int ndx)   {return 0F;}
	public float getDeftRATA1(int ndx) {return 0F;}
	public float getDeftRATB1(int ndx) {return 0F;}
	public float getDeftRATC1(int ndx) {return 0F;}
	public int getDeftCOD1(int ndx) {return 0;}
	public String getDeftCONT1(int ndx) {return "0";}
	public float getDeftRMA1(int ndx) throws PsseModelException {return rmDefault(ndx, 'A', 1, 1.1F, Math.abs(getCOD1(ndx)));}
	public float getDeftRMI1(int ndx) throws PsseModelException {return rmDefault(ndx, 'I', 1, 0.9F, Math.abs(getCOD1(ndx)));}
	public float getDeftVMA1(int ndx) throws PsseModelException {return vmDefault(ndx, 'A', 1, 1.1F, Math.abs(getCOD1(ndx)));}
	public float getDeftVMI1(int ndx) throws PsseModelException {return vmDefault(ndx, 'I', 1, 0.9F, Math.abs(getCOD1(ndx)));}
	public int getDeftNTP1(int ndx) {return 33;}
	public int getDeftTAB1(int ndx) {return 0;}
	public float getDeftCR1(int ndx) {return 0F;}
	public float getDeftCX1(int ndx) {return 0F;}
	public float getDeftWINDV2(int ndx) throws PsseModelException {return (getDeftCW(ndx)==2)?getBus2(ndx).getBASKV():1F;}
	public float getDeftNOMV2(int ndx)  {return 0F;}
	public float getDeftANG2(int ndx)   {return 0F;}
	public float getDeftRATA2(int ndx) {return 0F;}
	public float getDeftRATB2(int ndx) {return 0F;}
	public float getDeftRATC2(int ndx) {return 0F;}
	public int getDeftCOD2(int ndx) {return 0;}
	public String getDeftCONT2(int ndx) {return "0";}
	public float getDeftRMA2(int ndx) throws PsseModelException {return rmDefault(ndx, 'A', 1, 1.1F, Math.abs(getCOD2(ndx)));}
	public float getDeftRMI2(int ndx) throws PsseModelException {return rmDefault(ndx, 'I', 1, 0.9F, Math.abs(getCOD2(ndx)));}
	public float getDeftVMA2(int ndx) throws PsseModelException {return vmDefault(ndx, 'A', 1, 1.1F, Math.abs(getCOD2(ndx)));}
	public float getDeftVMI2(int ndx) throws PsseModelException {return vmDefault(ndx, 'I', 1, 0.9F, Math.abs(getCOD2(ndx)));}
	public int getDeftNTP2(int ndx) {return 33;}
	public int getDeftTAB2(int ndx) {return 0;}
	public float getDeftCR2(int ndx) {return 0F;}
	public float getDeftCX2(int ndx) {return 0F;}
	public float getDeftWINDV3(int ndx) throws PsseModelException {return (getCW(ndx)==2)?getBus3(ndx).getBASKV():1F;}
	public float getDeftNOMV3(int ndx)  {return 0F;}
	public float getDeftANG3(int ndx)   {return 0F;}
	public float getDeftRATA3(int ndx) {return 0F;}
	public float getDeftRATB3(int ndx) {return 0F;}
	public float getDeftRATC3(int ndx) {return 0F;}
	public int getDeftCOD3(int ndx) {return 0;}
	public String getDeftCONT3(int ndx) {return "0";}
	public float getDeftRMA3(int ndx) throws PsseModelException {return rmDefault(ndx, 'A', 1, 1.1F, Math.abs(getCOD3(ndx)));}
	public float getDeftRMI3(int ndx) throws PsseModelException {return rmDefault(ndx, 'I', 1, 0.9F, Math.abs(getCOD3(ndx)));}
	public float getDeftVMA3(int ndx) throws PsseModelException {return vmDefault(ndx, 'A', 1, 1.1F, Math.abs(getCOD3(ndx)));}
	public float getDeftVMI3(int ndx) throws PsseModelException {return vmDefault(ndx, 'I', 1, 0.9F, Math.abs(getCOD3(ndx)));}
	public int getDeftNTP3(int ndx) {return 33;}
	public int getDeftTAB3(int ndx) {return 0;}
	public float getDeftCR3(int ndx) {return 0F;}
	public float getDeftCX3(int ndx) {return 0F;}

}	