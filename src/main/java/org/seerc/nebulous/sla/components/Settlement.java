package org.seerc.nebulous.sla.components;

import java.io.Serializable;

public class Settlement implements Serializable{
	private String evaluationPeriod;
	private int settlementCount;
	private String concernedSL;
	private String settlementAction;

	
	
	public Settlement(String evaluationPeriod, int settlementCount, String concernedSL) {
		super();
		this.evaluationPeriod = evaluationPeriod;
		this.settlementCount = settlementCount;
		this.concernedSL = concernedSL;
		this.settlementAction = "CANCEL";
	}
	public String getEvaluationPeriod() {
		return evaluationPeriod;
	}
	public void setEvaluationPeriod(String evaluationPeriod) {
		this.evaluationPeriod = evaluationPeriod;
	}
	public int getSettlementCount() {
		return settlementCount;
	}
	public void setSettlementCount(int settlementCount) {
		this.settlementCount = settlementCount;
	}
	public String getConcernedSL() {
		return concernedSL;
	}
	public void setConcernedSL(String concernedSL) {
		this.concernedSL = concernedSL;
	}
	public String getSettlementAction() {
		return settlementAction;
	}
	public void setSettlementAction(String settlementAction) {
		this.settlementAction = settlementAction;
	}
	@Override
	public String toString() {
		return "Settlement [evaluationPeriod = " + evaluationPeriod + ", settlementCount = " + settlementCount
				+ ", concernedSL = " + concernedSL + ", settlementAction = " + settlementAction + "]";
	}
	
	
	
}
