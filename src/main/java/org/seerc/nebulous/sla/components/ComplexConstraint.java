package org.seerc.nebulous.sla.components;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = ComplexConstraint.class)
public class ComplexConstraint implements Constraint{
	
	protected String operator;
	protected List<Constraint> operands;
	
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public List<Constraint> getOperands() {
		return operands;
	}
	public void setOperands(List<Constraint> operands) {
		this.operands = operands;
	}
	
	public void addOperand(Constraint c) {
		operands.add(c);
	}

	@Override
	public boolean isComplex() {
		return true;
	}

	@Override
	public String toString() {
		return "ComplexConstraint [operator = " + operator + ", operands = " + operands + "]";
	}
	
	
}
