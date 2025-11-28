package org.seerc.nebulous.sla.components;

import java.io.Serializable;
import java.util.Map;

import org.seerc.nebulous.sla.rest.OntologyConnection;

public class WindowOutput implements Serializable {
	private String type;
	private Number value;
	private String unit;
	private OntologyConnection ontology;

	public WindowOutput() {
		ontology = OntologyConnection.getInstance();
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Number getValue() {
		return value;
	}
	public void setValue(Number value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	@Override
	public String toString() {
		return "WindowOutput [type = " + type + ", value = " + value + ", unit = " + unit + "]";
	}
	
	
	public void createWindowOutput(String metricName, String type) {
    	
    	if(this == null)
    		return;
    	
    	String woName;
    	String typeStr;
    	
    	if(type.equals("owlq:Window")) {
    		woName = metricName + "_WINDOW";
    		typeStr = "owlq:window";
    	}else {
    		woName = metricName  + "_OUTPUT";
    		typeStr = "neb:output";

    	}
    	
//    	System.out.println("WO: " + wo);
     	ontology.createIndividual(woName, type);
     	ontology.createIndividual(woName, "odrl:Asset");
     	
		ontology.createObjectProperty(typeStr, metricName, woName);
		ontology.createObjectProperty("odrl:partOf", woName, metricName);

    	 
//    	System.out.println(woName);
    	ontology.createDataProperty("neb:type", woName, this.getType(), "xsd:string");
    	ontology.createDataProperty("neb:value", woName, this.getValue().toString(), getDatatype(this.getValue().toString()));
    	
    	
    	if(ontology.countInstances("{" + this.getUnit() + "}") == 0)
    		ontology.createIndividual(this.getUnit(), "owlq:Unit");
   	
    	ontology.createObjectProperty("owlq:unit", woName, this.getUnit());
    }
	
	private String getDatatype(String literalValue){
		String result = "xsd:string";
		
		if(literalValue.matches("(\\+|-)?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)"))
			result =  "xsd:decimal";
		else if(literalValue.matches("-?P((([0-9]+Y([0-9]+M)?([0-9]+D)?|([0-9]+M)([0-9]+D)?|([0-9]+D))(T(([0-9]+H)([0-9]+M)?([0-9]+(\\.[0-9]+)?S)?|([0-9]+M)([0-9]+(\\.[0-9]+)?S)?|([0-9]+(\\.[0-9]+)?S)))?)|(T(([0-9]+H)([0-9]+M)?([0-9]+(\\.[0-9]+)?S)?|([0-9]+M)([0-9]+(\\.[0-9]+)?S)?|([0-9]+(\\.[0-9]+)?S))))"))
			result = "xsd:duration";
				
		return result;
	}
	
	
	
	public static WindowOutput parseWindowOutput(Object winOutObj) {
    	
    	WindowOutput windowOutput = null;
    	if(winOutObj != null) {
			windowOutput = new WindowOutput();
			
			if(winOutObj instanceof String) {
				String[] w = ((String) winOutObj).split(" ");
			
				Number value;

				try {
					value = Integer.parseInt(w[1]);
				}catch(Exception e){
					value = Double.parseDouble(w[1]);
				}
				
				windowOutput.setType(w[0]);
				windowOutput.setValue(value);
				windowOutput.setUnit(w[2]);
				
			} else {
				Map<String, Object> wo = (Map<String, Object>) winOutObj;
				
				windowOutput.setType((String) wo.get("type"));
				
				
				Object sizeSchedule = wo.get("size");
				
				if(sizeSchedule == null)
					sizeSchedule = (Map<String, Object>) wo.get("schedule");
				
				if(sizeSchedule instanceof String) {
					String[] s = ((String) sizeSchedule).split(" ");
					
					Number value;

					try {
						value = Integer.parseInt(s[0]);
					}catch(Exception e){
						value = Double.parseDouble(s[0]);
					}

					windowOutput.setValue(value);
					windowOutput.setUnit(s[1]);
				}else {
					Map<String, Object> s = (Map<String, Object>) sizeSchedule;
					windowOutput.setValue((Number) s.get("value"));
					windowOutput.setUnit((String) s.get("unit"));					
				}			
			}
		}
    	
    	return windowOutput;
    }
	
}
