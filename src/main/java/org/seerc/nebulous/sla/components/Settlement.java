package org.seerc.nebulous.sla.components;

import java.io.Serializable;

public class Settlement implements Serializable{
	private String evaluationPeriod;
	private int settlementCount;
	private int concernedSL;
	private String settlementAction;

	
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
	public int getConcernedSL() {
		return concernedSL;
	}
	public void setConcernedSL(int concernedSL) {
		this.concernedSL = concernedSL;
	}
	public String getSettlementAction() {
		return settlementAction;
	}
	public void setSettlementAction(String settlementAction) {
		this.settlementAction = settlementAction;
	}
	
	
	
}
