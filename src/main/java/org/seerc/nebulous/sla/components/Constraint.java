package org.seerc.nebulous.sla.components;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ConstraintDeserialiser.class)
public interface Constraint {

	public boolean isComplex();
}
