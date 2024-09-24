package org.seerc.nebulous.sla.rest;

import org.seerc.nebulous.sla.components.SLO;

public class RequestSLO extends SLO{
	
	private String slaName;
	private String sloName;
	private String slName;

	
	
	public RequestSLO(String slaName, String sloName, String slName) {
		super();
		this.slaName = slaName;
		this.sloName = sloName;
		this.slName = slName;
	}

	public RequestSLO(SLO slo) {
		
	}
	public RequestSLO() {
		// TODO Auto-generated constructor stub
	}

	public String getSlaName() {
		return slaName;
	}

	public void setSlaName(String slaName) {
		this.slaName = slaName;
	}

	public String getSloName() {
		return sloName;
	}

	public void setSloName(String name) {
		this.sloName = name;
	}

	public String getSlName() {
		return slName;
	}

	public void setSlName(String slName) {
		this.slName = slName;
	}

	@Override
	public String toString() {
		return "RequestSLO [slaName = " + slaName + ", slName = " + slName + ", name = " + sloName + ", sloType = " + sloType
				+ ", firstArgument = " + firstArgument
				+ ", operator = " + operator + ", secondArgument = " + secondArgument + "]";
	}
	
	
}
