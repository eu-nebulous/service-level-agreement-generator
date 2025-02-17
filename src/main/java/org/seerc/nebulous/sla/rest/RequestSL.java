package org.seerc.nebulous.sla.rest;

import org.seerc.nebulous.sla.components.SL;

public class RequestSL extends SL{
	private String slaName, slName;

	public String getSlaName() {
		return slaName;
	}
		
	public RequestSL(String slaName,  SL sl) {
		super();
		this.setSlaName(slaName);
		this.setSlName(sl.getSlName());
		this.setOperands(sl.getOperands());
		this.setOperator(sl.getOperator());
	}

	public void setSlaName(String slaName) {
		this.slaName = slaName;
	}

	public String getSlName() {
		return slName;
	}

	public void setSlName(String slName) {
		this.slName = slName;
	}
	
	
}
