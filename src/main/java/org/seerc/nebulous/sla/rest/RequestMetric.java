package org.seerc.nebulous.sla.rest;

import org.seerc.nebulous.sla.components.Metric;

public class RequestMetric{
	
	private Metric metric;
	private String slaName;
	
	
	public RequestMetric() {}

	public RequestMetric(Metric metric, String slaName) {
		this.metric = metric;
		this.slaName = slaName;
	}

	public String getSlaName() {
		return slaName;
	}

	public void setSlaName(String slaName) {
		this.slaName = slaName;
	}

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	@Override
	public String toString() {
		return "RequestMetric [slaName = " + slaName  + ", metric = " + metric + "]";
	}
	
}
