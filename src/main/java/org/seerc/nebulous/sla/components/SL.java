package org.seerc.nebulous.sla.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = SL.class)
public class SL extends ComplexConstraint implements Serializable{
	
	private String slName;
	
	public SL() {
		super();
	}
	
	public String getSlName() {
		return slName;
	}

	public void setSlName(String slName) {
		this.slName = slName;
	}

	@Override
	public String toString() {
		return "SL [slName = " + slName + ", operator = " + operator + ", operands = " + operands + "]";
	}
	
	
}
