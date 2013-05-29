package com.powerdata.openpa.psseproc;

public abstract class PsseClassSet
{
	private static final int MaxConfigVerMajor = 30;
	
	public static PsseClassSet GetClassSetForVersion(String version) throws PsseProcException
	{
		int ix = version.indexOf('.');
		String svmaj = null;
		svmaj = (ix == -1) ? version : version.substring(0, ix);
		
		int vmaj = Integer.parseInt(svmaj);
		PsseClassSet rv = null;
		
		if (vmaj <= 29)
		{
			rv = new PsseClassSetVersion0();
		}
		else if (vmaj <= MaxConfigVerMajor)
		{
			rv = new PsseClassSetVersion30();
		}
		else
		{
			throw new PsseProcException("Not configured for Version "+version);
		}
		
		return rv;
	}
	
	public abstract PsseClass[] getPsseClasses();
	public abstract int getVersionMajor();
}
