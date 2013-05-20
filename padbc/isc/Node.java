package com.powerdata.openpa.padbc.isc;

public class Node extends com.powerdata.openpa.padbc.Node
{
	private NodeList _list;
	public Node(int ndx, NodeList list)
	{
		super(ndx, list);
		_list = list;
	}
	public int getNdx(String id) { return _list.getNdx(id); }
	public int getFlag() { return _list.getFlag(_ndx); }
	public String getName() { return _list.getName(_ndx); }
	public String getIDE() { return _list.getIDE(_ndx); }
	public String getArea() { return _list.getArea(_ndx); }
	public String getZone() { return _list.getZone(_ndx); }
	public String getOwner() { return _list.getOwner(_ndx); }
	@Override
	public String toString()
	{
		return String.format("[%d] %s Name: %s Area: %s",_ndx,getObjectID(),getName(),getArea());
	}
}
