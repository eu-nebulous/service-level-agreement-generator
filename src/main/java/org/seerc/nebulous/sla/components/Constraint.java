package org.seerc.nebulous.sla.components;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ConstraintDeserialiser.class)
public interface Constraint {
	
	public static Constraint constructConstraint(Map m) {
		Constraint constraint;
		if(m.get("isComposite").equals(true)) {
			
			ComplexConstraint c = new ComplexConstraint();
			c.setOperator((String) m.get("condition"));
			for(Map i : ((List<Map>) m.get("children")))
				c.addOperand(constructConstraint(i));
			
			constraint = c;
		}else {
			SimpleConstraint c = new SimpleConstraint();
			c.setFirstArgument((String) m.get("metricName"));
			c.setOperator(ComparisonOperator.convert((String) m.get("operator")));
			c.setSecondArgument(m.get("value"));
			constraint = c;
		}
		return constraint;
	}
}
