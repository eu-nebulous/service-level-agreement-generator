package org.seerc.nebulous.sla.components;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = SLO.class)
public class SLO extends SimpleConstraint implements Serializable{
	
//	protected SimpleConstraint qualifyingCondition;
//	protected boolean soft, negotiable;
//	protected char sloType;
//	protected SLTransition transition;
	protected String sloName;
	protected double settlementPricePercentage;
	
	
	
	public SLO() {
		super();
	}

	public double getSettlementPricePercentage() {
		return settlementPricePercentage;
	}

	public void setSettlementPricePercentage(double settlementPricePercentage) {
		this.settlementPricePercentage = settlementPricePercentage;
	}
	public String getSloName() {
		return sloName;
	}

	public void setSloName(String sloName) {
		this.sloName = sloName;
	}
//	public SimpleConstraint getQualifyingCondition() {
//		return qualifyingCondition;
//	}
//
//	public void setQualifyingCondition(SimpleConstraint qualifyingCondition) {
//		this.qualifyingCondition = qualifyingCondition;
//	}

	@Override
	public String toString() {
		return "SLO [sloName = " + sloName + ", settlementPricePercentage = " + settlementPricePercentage
				+ ", firstArgument = " + firstArgument + ", operator = " + operator + ", secondArgument = "
				+ secondArgument + "]";
	}

//	public boolean isSoft() {
//		return soft;
//	}
//
//	public void setSoft(boolean soft) {
//		this.soft = soft;
//	}
//
//	public boolean isNegotiable() {
//		return negotiable;
//	}
//
//	public void setNegotiable(boolean negotiable) {
//		this.negotiable = negotiable;
//	}


//	public char getSloType() {
//		return sloType;
//	}
//
//	public void setSloType(char sloType) {
//		this.sloType = sloType;
//	}
	

//	public SLTransition getTransition() {
//		return transition;
//	}
//
//	public void setTransition(SLTransition transition) {
//		this.transition = transition;
//	}

	


//	public double getSettlementPricePercentage() {
//		return settlementPricePercentage;
//	}
//
//	public void setSettlementPricePercentage(double settlementPricePercentage) {
//		this.settlementPricePercentage = settlementPricePercentage;
//	}
//	




			
	
}