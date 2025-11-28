package org.seerc.nebulous.sla.components;

import java.io.Serializable;

import org.seerc.nebulous.sla.rest.OntologyConnection;

public class Metric implements Serializable {
	
	protected String name;
	protected WindowOutput window;
	protected WindowOutput output;
//	protected boolean isMaximizing;
	private OntologyConnection ontology;

	
	public Metric() {
		ontology = OntologyConnection.getInstance();

	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public WindowOutput getWindow() {
		return window;
	}
	public void setWindow(WindowOutput window) {
		this.window = window;
	}
	public WindowOutput getOutput() {
		return output;
	}
	public void setOutput(WindowOutput output) {
		this.output = output;
	}
	public Metric(String name, WindowOutput window, WindowOutput output) {
		super();
		this.name = name;
		this.window = window;
		this.output = output;
	}
		

	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof Metric))
			return false;
		
		
		Metric m2 = (Metric) obj;
//		System.out.println(m2.name + "\t" + this.name);
		
		return m2.name.equals(this.name);
	}
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	@Override
	public String toString() {
		return "Metric [name = " + name + ", window = " + window + ", output = " + output + "]";
	}
	
	public String createMetric(String slaName) {
    	

    	String metricName = slaName + "_" + this.getName();
		ontology.createIndividual(metricName, "odrl:AssetCollection");

    	//Depending on the type of metric, create the appropriate associations.
    	if(this instanceof CompositeMetric m) {
    		
			ontology.createIndividual(metricName, "owlq:CompositeMetric");
//			System.out.println("metric name: " + metricName);
			ontology.createDataProperty("neb:formula", metricName, m.getFormula(), "rdf:PlainLiteral");

    	}else if(this instanceof RawMetric m){
    		
    		Sensor s = m.getSensor();
    		    			
			ontology.createIndividual(metricName, "owlq:RawMetric");
			
			if(s != null) {
			ontology.createIndividual(metricName + "_SENSOR", "owlq:Sensor");
			
			ontology.createObjectProperty("owlq:sensor", metricName, metricName + "_SENSOR") ;
			if(s.getType() != null)
				ontology.createDataProperty("neb:type", metricName + "_SENSOR", s.getType(), "xsd:string");
			if(s.getAffinity() != null)
				ontology.createDataProperty("neb:affinity", metricName + "_SENSOR", s.getAffinity(), "xsd:string");

			}
    	}else {
			ontology.createIndividual(metricName, "owlq:Metric");

    	}
//    	ontology.createDataProperty("neb:isMaximizing", metricName, m.isMaximizing());
    	
    	this.getWindow().createWindowOutput(metricName, "owlq:Window");
    	this.getOutput().createWindowOutput(metricName, "neb:Output");
//    	if(this.getWindow() != null)
//    		createWindowOutput(metricName, "owlq:Window", this.getWindow());
//    	if(this.getOutput() != null)
//    		createWindowOutput(metricName, "neb:Output", this.getOutput());

    	return metricName;
    }
}
