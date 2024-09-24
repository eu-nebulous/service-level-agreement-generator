package org.seerc.nebulous.sla.components;

import java.util.ArrayList;
import java.util.List;

public class SL {
	private List<SLO> slos;

	public SL() {
		slos = new ArrayList<SLO>();
	}

	public void addSlo(SLO slo) {
		slos.add(slo);
	}
	
	public List<SLO> getSlos() {
		return slos;
	}

	public void setSlos(List<SLO> slos) {
		this.slos = slos;
	}

	
	
	
}
