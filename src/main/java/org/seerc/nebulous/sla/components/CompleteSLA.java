package org.seerc.nebulous.sla.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.seerc.nebulous.sla.rest.RequestSLO;

public class CompleteSLA {
	private String slaName;
	private List<RequestSLO> slos;
	private List<SLTransition> slTransitions;
	private Set<Metric> metrics;
	
	public CompleteSLA(String slaName) {
		this.slaName = slaName;
		slos = new ArrayList<RequestSLO>();
		metrics = new HashSet<Metric>();
	}
	public CompleteSLA() {}
	public String getSlaName() {
		return slaName;
	}
	public void setSlaName(String slaName) {
		this.slaName = slaName;
	}
	public List<RequestSLO> getSlos() {
		return slos;
	}
	public void setSlos(List<RequestSLO> slos) {
		this.slos = slos;
	}
	public Set<Metric> getMetrics() {
		return metrics;
	}
	public void setMetrics(Set<Metric> metrics) {
		this.metrics = metrics;
	}
	
	public List<SLTransition> getSlTransitions() {
		return slTransitions;
	}
	public void setSlTransitions(List<SLTransition> slTransitions) {
		this.slTransitions = slTransitions;
	}
	@Override
	public String toString() {
		return "CompleteSLA [slaName = " + slaName + ", slos = " + slos + ", metrics = " + metrics + "]";
	}
	
	
}
