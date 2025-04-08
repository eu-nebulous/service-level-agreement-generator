package org.seerc.nebulous.sla.components;

import java.util.ArrayList;
import java.util.List;

public class InMemorySLAAttributes {
	private String activeSL;
	private List<Long> violationTimeStamp;
	
	public InMemorySLAAttributes(String activeSL) {
		this.activeSL = activeSL;
		violationTimeStamp = new ArrayList<Long>();

	}
	public InMemorySLAAttributes() {
		violationTimeStamp = new ArrayList<Long>();
	}

	public String getActiveSL() {
		return activeSL;
	}
	public void setActiveSL(String activeSL) {
		this.activeSL = activeSL;
	}
	public List<Long> getViolationTimeStamp() {
		return violationTimeStamp;
	}
	public void appendViolation(long timestamp) {
		violationTimeStamp.add(timestamp);
	}
	
	public void setViolationTimeStamp(List<Long> violationTimeStamp) {
		this.violationTimeStamp = violationTimeStamp;
	}
	@Override
	public String toString() {
		return "InMemorySLAAttributes [activeSL = " + activeSL + ", violationTimeStamp = " + violationTimeStamp + "]";
	}
	
	
}
