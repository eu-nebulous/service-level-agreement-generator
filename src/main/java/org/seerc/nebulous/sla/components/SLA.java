package org.seerc.nebulous.sla.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SLA{
	private String slaName;
	private Map<String, List<SLO>> slos;
	private Set<Metric> metrics;
	private List<SLTransition> slTransitions;
	
	public SLA(String slaName) {
		this.slaName = slaName;
		slos = new HashMap<String, List<SLO>>();
		metrics = new HashSet<Metric>();
		slTransitions = new ArrayList<SLTransition>();
	}
	
	
	
	public SLA() {
	}



	public Map<String, List<SLO>> getSlos() {
		return slos;
	}


	public List<SLTransition> getSlTransitions() {
		return slTransitions;
	}



	public void setSlTransitions(List<SLTransition> slTransitions) {
		this.slTransitions = slTransitions;
	}



	public void setSlos(Map<String, List<SLO>> slos) {
		this.slos = slos;
	}
	
	public void setMetrics(Set<Metric> metrics) {
		this.metrics = metrics;
	}
	
	public String getSlaName() {
		return slaName;
	}

	public void setSlaName(String slaName) {
		this.slaName = slaName;
	}
	
	public Set<Metric> getMetrics() {
		return metrics;
	}

	public void addMetric(Metric metric) {
		metrics.add(metric);
	}
	
	public void addTransition(SLTransition transition) {
		slTransitions.add(transition);
	}

	@Override
	public String toString() {
		return "SLA [slaName = " + slaName + ", slos = " + slos + ", metrics = " + metrics + "]";
	}

	public void addSl(String sl) {
		slos.put(sl, new ArrayList<SLO>());
	}
	public void addSlo(String slName, SLO slo) {
		slos.get(slName).add(slo);
	}


	
}