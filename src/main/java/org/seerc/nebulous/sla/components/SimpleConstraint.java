package org.seerc.nebulous.sla.components;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 *  A simple constraint is made up of a first and second argument along with an operator. They take th form of first argument operator second argument. The first argument refers to a Metric and the second to a threshold value, which if surpassed is considered a violation.
 */
@JsonDeserialize(as = SimpleConstraint.class)
public class SimpleConstraint implements Constraint{
		
	protected String firstArgument;
	protected ComparisonOperator operator;
	protected Object secondArgument;

	public String getFirstArgument() {
		return firstArgument;
	}
	public void setFirstArgument(String firstArgument) {
		this.firstArgument = firstArgument;
	}
	public ComparisonOperator getOperator() {
		return operator;
	}
	public void setOperator(ComparisonOperator operator) {
		this.operator = operator;
	}
	public Object getSecondArgument() {
		return secondArgument;
	}
	public void setSecondArgument(Object secondArgument) {
		this.secondArgument = secondArgument;
	}

	@Override
	public boolean isComplex() {
		return false;
	}
	@Override
	public String toString() {
		return "SimpleConstraint [firstArgument = " + firstArgument + ", operator = " + operator + ", secondArgument = "
				+ secondArgument + "]";
	}
	protected String getDatatype(){
		String literalValue = secondArgument.toString();
		String result = "xsd:string";
		
		if(literalValue.matches("(\\+|-)?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)"))
			result =  "xsd:decimal";
		else if(literalValue.matches("-?P((([0-9]+Y([0-9]+M)?([0-9]+D)?|([0-9]+M)([0-9]+D)?|([0-9]+D))(T(([0-9]+H)([0-9]+M)?([0-9]+(\\.[0-9]+)?S)?|([0-9]+M)([0-9]+(\\.[0-9]+)?S)?|([0-9]+(\\.[0-9]+)?S)))?)|(T(([0-9]+H)([0-9]+M)?([0-9]+(\\.[0-9]+)?S)?|([0-9]+M)([0-9]+(\\.[0-9]+)?S)?|([0-9]+(\\.[0-9]+)?S))))"))
			result = "xsd:duration";
		else 
			result = "xsd:string";
		
		return result;
	}	

}
