package org.seerc.nebulous.sla.components;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.seerc.nebulous.sla.rest.EXNConnection;
import org.seerc.nebulous.sla.rest.OntologyConnection;
import org.springframework.stereotype.Component;
@Component 
public class SLA implements Serializable{
	
	private String slaName;
	private List<SL> sls;
	private Set<Metric> metrics;
	private List<SLTransition> transitions;
	private Settlement settlement;
	private OntologyConnection ontology;
	
	public SLA() {
		ontology = OntologyConnection.getInstance();
		sls = new ArrayList<SL>();
		metrics = new HashSet<Metric>();
		transitions = new ArrayList<SLTransition>();
	}
	public SLA(String slaName) {
		ontology = OntologyConnection.getInstance();
		this.slaName = slaName;
		sls = new ArrayList<SL>();
		metrics = new HashSet<Metric>();
		transitions = new ArrayList<SLTransition>();
	}
	
	public String getSlaName() {
		return slaName;
	}
	public void setSlaName(String slaName) {
		this.slaName = slaName;
	}
	public List<SL> getSls() {
		return sls;
	}
	public void setSls(List<SL> sls) {
		this.sls = sls;
	}
	public Set<Metric> getMetrics() {
		return metrics;
	}
	public void setMetrics(Set<Metric> metrics) {
		this.metrics = metrics;
	}
	public List<SLTransition> getTransitions() {
		return transitions;
	}
	public void setTransitions(List<SLTransition> transitions) {
		this.transitions = transitions;
	}
	
	public Settlement getSettlement() {
		return settlement;
	}
	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
	}
	@Override
	public String toString() {
		return "CompleteSLA [sls = " + sls + ", metrics = " + metrics + ", transitions = " + transitions + "]";
	}
	
	
	
	public void createCompleteSla() { 	

//		System.out.println(this);
		
    	final String slaName = createSLA(this.getSlaName());
    	System.out.println("CREATING NEW SLA :" + this.getSlaName());
    	
    	this.getMetrics().forEach(metric -> {
    		String metricName = metric.createMetric(slaName);
    		ontology.createObjectProperty("odrl:partOf", metricName, slaName);
    	});
	System.out.println("Creating metrics...");
    	
    	for(int i = 0; i < this.getSls().size(); i++) {
    		final SL sl =  this.getSls().get(i);
    		
    		final String slName = sl.createSL(slaName);
    		System.out.println("Creating SL and SLOs...");
    		RecurseConstraint rc = new RecurseConstraint(slName, slaName);
    		for(Constraint c : sl.getOperands()) {
    			
    			String constName = rc.buildConstraint(c);
    			
    			ontology.createObjectProperty("owlq:constraint", slName, constName);
    			ontology.createObjectProperty("odrl:partOf", constName, slName);
    		}
    	}
    	
    	if(this.getTransitions() != null)
	    	for(SLTransition trans : this.getTransitions()) {
			System.out.println("Creating Transition...");
	    		final String slTransName = slaName + "_" + trans.getFirstSl() + "_TO_" + trans.getSecondSl();

	    		
	    		ontology.createIndividual(slTransName, "owlq:SLTransition");
	    		ontology.createIndividual(slTransName, "odrl:Asset");
	    		
	        	ontology.createObjectProperty("owlq:slTransition", slaName, slTransName);
	        	ontology.createObjectProperty("odrl:partOf", slTransName, slaName);
	
	        		
	        	ontology.createObjectProperty("owlq:firstSL", slTransName, slaName + "_SL_" + trans.getFirstSl());
	        	ontology.createObjectProperty("owlq:secondSL", slTransName, slaName + "_SL_" + trans.getSecondSl());
	        	
	        	ontology.createDataProperty("owlq:violationThreshold", slTransName, Integer.toString(trans.getViolationThreshold()), "xsd:integer");
	        	ontology.createDataProperty("owlq:evaluationPeriod", slTransName, trans.getEvaluationPeriod(), "xsd:duration");

	    		
	    	}
	    	
    	if(this.getSettlement() != null) {
		System.out.println("Creating Settlement...");
	    	ontology.createIndividual(slaName + "_SETTLEMENT", "owlq:Settlement");
	    	ontology.createIndividual(slaName + "_SETTLEMENT", "odrl:Asset");
	    	ontology.createObjectProperty("odrl:partOf", slaName + "_SETTLEMENT", slaName);
	
	    	ontology.createDataProperty("owlq:evaluationPeriod", slaName + "_SETTLEMENT", this.getSettlement().getEvaluationPeriod().toString(), "xsd:duration");
	    	ontology.createDataProperty("owlq:settlementCount", slaName + "_SETTLEMENT", Integer.toString(this.getSettlement().getSettlementCount()),  "xsd:integer");
	    	ontology.createDataProperty("owlq:settlementAction", slaName + "_SETTLEMENT", this.getSettlement().getSettlementAction(), "xsd:string");

	
	    	
	    	ontology.createObjectProperty("owlq:settlement", slaName, slaName + "_SETTLEMENT");
	    	ontology.createObjectProperty("owlq:concernedSL", slaName + "_SETTLEMENT", slaName + "_SL_" + this.getSettlement().getConcernedSL());
    	}

		EXNConnection.getInstance().publishSLA(this);
    	
//    	ontology.registerAsset(slaName);
    	System.out.println(slaName);
	    System.out.println("Finished Creating SLA...");
	}
	
	public String createSLA(String id) {
    	
		final String SLANAME = "neb:SLA_" + id;
		
//		ontology.registerAsset(SLANAME);
		System.out.println("Creating SLA: " + SLANAME);
		ontology.createIndividual(SLANAME, "owlq:SLA");
		ontology.createIndividual(SLANAME, "odrl:AssetCollection");
	    System.out.println("Created SLA: " + SLANAME);

		return SLANAME;
    }
	
}
