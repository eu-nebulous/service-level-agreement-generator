package org.seerc.nebulous.sla.components;

public class SLO extends SimpleConstraint{
	
//	protected SimpleConstraint qualifyingCondition;
//	protected boolean soft, negotiable;
	protected char sloType;
//	protected SLTransition transition;
	protected double settlementPricePercentage;
	
//	protected double settlementPricePercentage;
//
//	public double getSettlementPricePercentage() {
//		return settlementPricePercentage;
//	}
//
//	public void setSettlementPricePercentage(double settlementPricePercentage) {
//		this.settlementPricePercentage = settlementPricePercentage;
//	}

//	public SimpleConstraint getQualifyingCondition() {
//		return qualifyingCondition;
//	}
//
//	public void setQualifyingCondition(SimpleConstraint qualifyingCondition) {
//		this.qualifyingCondition = qualifyingCondition;
//	}

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


	public char getSloType() {
		return sloType;
	}

	public void setSloType(char sloType) {
		this.sloType = sloType;
	}
	

//	public SLTransition getTransition() {
//		return transition;
//	}
//
//	public void setTransition(SLTransition transition) {
//		this.transition = transition;
//	}

	
	public double getSettlementPricePercentage() {
		return settlementPricePercentage;
	}

	public void setSettlementPricePercentage(double settlementPricePercentage) {
		this.settlementPricePercentage = settlementPricePercentage;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}



			
	
}