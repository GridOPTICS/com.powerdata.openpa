package com.powerdata.openpa.tools.psmfmt;

public enum CasePhaseTapChanger implements VersionedDoc
{
	ID, ControlStatus, PhaseShift;

	@Override
	public String getVersion() {return "1.9";}
}
