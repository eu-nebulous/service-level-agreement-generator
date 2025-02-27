package org.seerc.nebulous.sla.components;

import org.seerc.nebulous.sla.rest.OntologyConnection;

public class RecurseConstraint {
	private int iterator;
	private String constraintNameTemplate;
	private String slaName;
	private OntologyConnection ontology;
	
	public RecurseConstraint(String constraintNameTemplate, String slaName) {
		this.constraintNameTemplate = constraintNameTemplate;
		this.slaName = slaName;
		iterator = 0;
		ontology = OntologyConnection.getInstance();
	}
	
	public String buildConstraint(Constraint constraint) {
		
		String constraintName = constraintName();
		
		if(!constraint.isComplex()) {
			SLO constr = (SLO) constraint;
			System.out.println(slaName +  "_" + constr.getFirstArgument());
			ontology.createIndividual(constraintName, "owlq:SLO");
			ontology.createObjectProperty("owlq:firstArgument", constraintName, slaName +  "_" + constr.getFirstArgument());
			ontology.createObjectProperty("owlq:operator", constraintName, "owlq:" + constr.getOperator().toString());
			ontology.createDataProperty("odrl:rightOperand", constraintName, constr.getSecondArgument());
			
			System.out.println(constraintName);
			if(constr.getSettlementPricePercentage() >= 0d ) {
				ontology.createIndividual(constraintName + "_PN", "owlq:Penalty");
				ontology.createIndividual(constraintName + "_PN", "odrl:Asset"); 	
		
				ontology.createObjectProperty("owlq:penalty", constraintName, constraintName + "_PN");
				ontology.createObjectProperty("odrl:partOf", constraintName + "_PN", constraintName);
		
				ontology.createIndividual(constraintName + "_PN_C", "owlq:SLOCompensation");
				ontology.createIndividual(constraintName + "_PN_C", "odrl:Asset");
		
				ontology.createObjectProperty("owlq:compensation", constraintName + "_PN", constraintName + "_PN_C");
				ontology.createObjectProperty("odrl:partOf", constraintName + "_PN_C", constraintName);
		
				ontology.createDataProperty("owlq:settlementPricePercentage", constraintName + "_PN_C", constr.getSettlementPricePercentage());
			}
		}else {
			ComplexConstraint constr = (ComplexConstraint) constraint;
			
			ontology.createIndividual(constraintName, "owlq:ComplexConstraint");
			ontology.createIndividual(constraintName, "odrl:AssetCollection");

			ontology.createObjectProperty("owlq:logicalOperator", constraintName, "owlq:" + constr.getOperator());
			
			ontology.createObjectProperty("owlq:logicalOperator", constraintName, "owlq:" + constr.getOperator().toString());
			for(Constraint c : constr.getOperands()) {
				String insideConstraintName = buildConstraint(c);
				ontology.createObjectProperty("owlq:constraint", constraintName, insideConstraintName);
				ontology.createObjectProperty("odrl:partOf", insideConstraintName, constraintName);

			}
		}
		return constraintName;
	}
	private String constraintName() {
		return constraintNameTemplate + "_" + iterator();
	}
	private int iterator() {
		return iterator++;
	}
}
