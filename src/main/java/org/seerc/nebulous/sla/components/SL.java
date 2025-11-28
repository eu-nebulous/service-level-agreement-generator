package org.seerc.nebulous.sla.components;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


import org.seerc.nebulous.sla.rest.OntologyConnection;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = SL.class)
public class SL extends ComplexConstraint implements Serializable{
	
	private String slName;
	private static OntologyConnection ontology;
	
	public SL(ComplexConstraint constr, String slName) {
		super();
		this.slName = slName;

		this.setOperands(constr.getOperands());
		this.setOperator(constr.getOperator());
		
		ontology = OntologyConnection.getInstance();
	}
	
	public SL() {
		super();
		ontology = OntologyConnection.getInstance();
	}
	public SL(String slName) {
		super();
		this.slName = slName;
		ontology = OntologyConnection.getInstance();
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
	
	public String createSL(String slaName) {
    	//The name of the SL is determined by the parameter
    	String slName = slaName + "_SL_" + this.getSlName();
		ontology.createIndividual(slName, "owlq:SL"); //Create the SL
		ontology.createIndividual(slName, "odrl:AssetCollection"); //Create the SL

		ontology.createObjectProperty("owlq:serviceLevel", slaName, slName);	 //Connect it to the SLA	
		ontology.createObjectProperty("odrl:partOf", slName,  slaName);	 //Connect it to the SLA		
		ontology.createObjectProperty("owlq:logicalOperator", slName, "owlq:" + this.getOperator());
    	
    	return slName;
    }
}
