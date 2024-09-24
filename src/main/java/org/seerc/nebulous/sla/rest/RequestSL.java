package org.seerc.nebulous.sla.rest;

public class RequestSL {
	private String slaName, slName;

	public String getSlaName() {
		return slaName;
	}
		
	public RequestSL(String slaName, String slName) {
		super();
		this.slaName = slaName;
		this.slName = slName;
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
