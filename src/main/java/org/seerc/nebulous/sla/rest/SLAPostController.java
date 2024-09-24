package org.seerc.nebulous.sla.rest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

import org.seerc.nebulous.fileParsers.KubevelaParser;
import org.seerc.nebulous.fileParsers.MetricModelParser;
import org.seerc.nebulous.sla.components.ComparisonOperator;
import org.seerc.nebulous.sla.components.CompleteSLA;
import org.seerc.nebulous.sla.components.CompositeMetric;
import org.seerc.nebulous.sla.components.Metric;
import org.seerc.nebulous.sla.components.RawMetric;
import org.seerc.nebulous.sla.components.SLA;
import org.seerc.nebulous.sla.components.SLO;
import org.seerc.nebulous.sla.components.Sensor;
import org.seerc.nebulous.sla.components.WindowOutput;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SuppressWarnings("unchecked")
public class SLAPostController {
	//TODO: add error checking and discuss non-volatile storage.
	private OntologyConnection ontology = OntologyConnection.getInstance();
	
	@PostMapping("create/models")
	public void parseModels() {
		
		Map<String, String> slaNames = new HashMap<String, String>();
		
		Map<String, RequestMetric> metrics = new HashMap<String, RequestMetric>();
		Map<String, RequestSLO> requirements = new HashMap<String, RequestSLO>();
		
		parseMetricModel(slaNames, metrics, requirements);
		parseKubevela(slaNames, requirements);
		
		//Inserting into ontology
		for(Map.Entry<String, RequestMetric> entry: metrics.entrySet()) 
			createMetric(entry.getValue());
		
		for(Map.Entry<String, RequestSLO> entry: requirements.entrySet())
			createSLO(entry.getValue());
	}
	
	@PostMapping("create/kubevela")
	public void parseKubevela(Map<String, String> slaNames, Map<String, RequestSLO> requirements ) {
		try {
			KubevelaParser kubevela = new KubevelaParser((new FileInputStream(new File("KubeVela_v2.yaml"))));
			
			for(int i = 0; i <  kubevela.getComponents().size(); i++) {
				Map<String, Map<String, String>> resources = kubevela.getResources(i);
				
				if(resources == null || resources.size() == 0)
					continue;
				
				String slaName = slaNames.get(kubevela.getComponentName(i));
				
				RequestSLO maxCpu = new RequestSLO();
//				System.out.println(resources);
				maxCpu.setFirstArgument("neb:MAX_CPU_CORES");
				maxCpu.setOperator(ComparisonOperator.LESS_EQUAL_THAN);
				maxCpu.setSecondArgument(resources.get("limits").get("cpu"));
				maxCpu.setSlaName(slaName);
				maxCpu.setSloType('D');
				
				RequestSLO maxRam= new RequestSLO();
				
				maxRam.setFirstArgument("neb:MAX_RAM");
				maxRam.setOperator(ComparisonOperator.LESS_EQUAL_THAN);
				maxRam.setSecondArgument(resources.get("limits").get("memory"));
				maxRam.setSlaName(slaName);
				maxRam.setSloType('D');

				
				RequestSLO requestedCpu = new RequestSLO();
				
				requestedCpu.setFirstArgument("neb:REQUESTED_CPU_CORES");
				requestedCpu.setOperator(ComparisonOperator.EQUALS);
				requestedCpu.setSecondArgument(resources.get("requests").get("cpu"));
				requestedCpu.setSlaName(slaName);
				requestedCpu.setSloType('D');

				RequestSLO requestRam= new RequestSLO();
				
				requestRam.setFirstArgument("neb:REQUESTED_RAM");
				requestRam.setOperator(ComparisonOperator.EQUALS);
				requestRam.setSecondArgument(resources.get("requests").get("memory"));
				requestRam.setSlaName(slaName);
				requestRam.setSloType('D');

				requirements.put(slaName + "_MAX_CPU_CORES", maxCpu);
				requirements.put(slaName + "_MAX_RAM", maxRam);
				requirements.put(slaName + "_REQUESTED_CPU_CORES", requestedCpu);
				requirements.put(slaName + "_REQUESTED_RAM", requestRam);

			}
					
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	@PostMapping("create/metricModel")
	public void parseMetricModel(Map<String, String> slaNames, Map<String, RequestMetric> metrics, Map<String, RequestSLO> requirements) {
		try {
			MetricModelParser metricModel = new MetricModelParser((new FileInputStream(new File("augmenta_metric_model_draft_v4.yml"))));
			Map<String, String> references = new HashMap<String, String>();

			for(int i = 0; i < metricModel.getComponents().size(); i++) {
				
				String slaName = createSLA();
				slaNames.put((String) metricModel.getComponent(i).get("name"), slaName);
				
				List<Map<String, Object>> reqs = metricModel.getComponentRequirements(i);
				List<Map<String, Object>> mets = metricModel.getComponentMetrics(i);
				
				insertMetrics(slaName, mets, metrics, references);
				insertRequirements(slaName, reqs, requirements, 'R');
			} 
			
			//scopes
			for(Object sc : metricModel.getScopes()) {
				Map<String, Object> scope = (Map<String, Object>) sc;
				
				List<String> components = (List<String>) scope.get("components");
				
				List<Map<String, Object>> mets = (List<Map<String, Object>>) scope.get("metrics");
				List<Map<String, Object>> reqs = (List<Map<String, Object>>) scope.get("requirements");
				if(components != null)
					for(String comp : components) {
						
						String slaName = slaNames.get(comp);
												
						insertMetrics(slaName, mets, metrics, references);
						insertRequirements(slaName, reqs, requirements, 'R');
					}			
				else
					slaNames.forEach((t, u) -> {
						insertMetrics(u, mets, metrics, references);
						insertRequirements(u, reqs, requirements, 'R');
					});
						
			}
						
			references.forEach((t, u) -> {
				String []parts = u.split("[^a-z_]+");
				RequestMetric m = metrics.get(slaNames.get(parts[1]) + "_" + parts[2].toUpperCase());
				metrics.put(t, m);
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    @PostMapping("/create/sla")
    public String createSLA() {
    	
		String SLANAME = "neb:SLA_" + ontology.countInstances("SLA");;
		
		ontology.createIndividual(SLANAME, "owlq:SLA");
//		ontology.createIndividual(SLANAME + "_SL", "owlq:SL");
//		ontology.createObjectProperty("owlq:serviceLevel", SLANAME, SLANAME + "_SL");				
		return SLANAME;
    }
    @PostMapping("/create/sl")
    public String createSL(@RequestBody RequestSL rsl) {
    	
    	String slName = rsl.getSlaName() + "_SL_" + rsl.getSlName();
		ontology.createIndividual(slName, "owlq:SL");
		ontology.createObjectProperty("owlq:serviceLevel", rsl.getSlaName(), slName);			
    	
    	return slName;
    }
    //curl -X POST -F "firstArgument=CPU_CORES" -F "operator=GREATER_EQUAL_THAN" -F "secondArgument=4	" -F "qualifyingConditionFirstArgument=REQUESTS_PER_HOUR" -F "sl=SLA_0_NORMAL_SL" -F "qualifyingConditionOperator=LESS_EQUAL_THAN" -F "qualifyingConditionSecondArgument=1000" -F "soft=false" -F "negotiable=false" localhost:8080/create/slo
    /**
     * Creates a new SLO.
     * @param slo THE SLO.
     * @return the URI of the new SLO.
     */
    @PostMapping("/create/slo")	
    public String createSLO(@RequestBody RequestSLO slo){
    	System.out.println(slo);
    	final String slaName = slo.getSlaName().split(":")[1];
		if(ontology.countInstances(URLEncoder.encode("{" + slaName + "}", StandardCharsets.UTF_8)) != 1)
			return null;

		String sloName = slaName +"_SLO_";
		
		int i = 0;
		while(ontology.countInstances(URLEncoder.encode("{" + sloName + i + "}", StandardCharsets.UTF_8)) != 0)
			i++;
		
		sloName += i;
//		System.out.println(sloName + " " + slo.getFirstArgument());
		
//		if(ontology.countInstances("%7B" + slo.getFirstArgument().split(":")[1] + "%7D%20and%20Metric") != 1)
//			throw new Exception("First argument (" + slo.getFirstArgument().split(":")[1] + ") doesn't exist...");
		
		ontology.createIndividual(sloName, "neb:" + slo.getSloType() + "_SLO");
		ontology.createObjectProperty("owlq:firstArgument", sloName, slo.getFirstArgument());
		
		//Add second argument and operator.
		ontology.createDataProperty("owlq:secondArgument", sloName, slo.getSecondArgument());		
		ontology.createObjectProperty("owlq:operator", sloName, slo.getOperator().toString());
		
		//Add to SL.
		ontology.createObjectProperty("owlq:constraint", "neb:" + slaName + "_SL_" + slo.getSlName(), sloName);

//		if(slo.getSloType() == 'R' && slo.getTransition() != null) {
//			final String slTransName = "neb:SL_TRANSITION_" + sloName;
//			ontology.createIndividual(slTransName, "owlq:SLTransition");
//			ontology.createObjectProperty("owlq:SLTransition", sloName, slTransName);
//			
//			ontology.createObjectProperty("owlq:firstSL", slTransName, slo.getSlName());
//			ontology.createObjectProperty("owlq:secondSL", slTransName, slo.getTransition().getSl());
//			
//			ontology.createDataProperty("owlq:violationThreshold", slTransName, slo.getTransition().getViolationThreshold());
//			ontology.createDataProperty("owlq:evaluationPeriod", slTransName, slo.getTransition().getEvaluationPeriod());
//		}else if(slo.getSloType() == 'D') {
//			
//			ontology.createIndividual("neb:" + sloName + "_PENALTY", "owlq:Penalty");
//			ontology.createObjectProperty("owlq:sloSettlement", sloName, "neb:" + sloName + "_PENALTY");
//			
//			ontology.createIndividual("neb:" + sloName + "_COMPENSATION", "owlq:SLOCompensation");
//			ontology.createDataProperty("owlq:settlementPricePercentage", "neb:" + sloName + "_COMPENSATION", slo.getSettlementPricePercentage());
//			ontology.createObjectProperty("owlq:compensation", sloName, "neb:" + sloName + "_COMPENSATION");
//		}
		//Add soft/negotiable.
//		ontology.createDataProperty("owlq:soft", sloName, slo.isSoft());
//		ontology.createDataProperty("owlq:negotiable", sloName, slo.isNegotiable());
		
		//Add penalty
//		if(slo.getSettlementPricePercentage() != null) {
//		ontology.createIndividual(sloName + "_PN", "owlq:Penalty"); 	
//		ontology.createObjectProperty("owlq:sloSettlement", sloName, sloName + "_PN");
//		ontology.createIndividual(sloName + "_PN_C", "owlq:SLOCompensation");
//		ontology.createObjectProperty("owlq:compensation", sloName + "_PN", sloName + "_PN_C");
//		ontology.createDataProperty("owlq:settlementPricePercentage", sloName + "_PN_C", slo.getSettlementPricePercentage());
//		}

	
//		System.out.println("constraint made: neb:" + slaName + "_SL" + " ||| " +  sloName );
//		if(slo.getQualifyingCondition() == null) 
//			return sloName;
//		
//		SimpleConstraint qc = slo.getQualifyingCondition();
//		//If QC exists, add create the class.
//		ontology.createIndividual(sloName + "_QC", "owlq:QualifyingCondition");
//		
//		ontology.createIndividual("neb:" + qc.getFirstArgument(), "owlq:Metric");
//		ontology.createObjectProperty("owlq:firstArgument", sloName + "_QC", "neb:" + qc.getFirstArgument());
//		
//		ontology.createDataProperty("owlq:secondArgument", sloName + "_QC", qc.getSecondArgument());
//		ontology.createObjectProperty("owlq:operator", sloName + "_QC", qc.getOperator().toString());
//		
//		ontology.createObjectProperty("owlq:qualifyingCondition", sloName, sloName + "_QC");
		
		return sloName;
    }   
    
    @PostMapping("create/metric")
    public String createMetric(@RequestBody RequestMetric metric) {
    	
    	Metric m;
//    	System.out.println("metric:" +  metric);
    	try {
    	m = metric.getMetric();
    	}catch(Exception e) {
    		return null;
    	}
    	
    	String metricName = metric.getSlaName() + "_" + m.getName().toUpperCase();
    	
    	if(m instanceof CompositeMetric) {
    		
			ontology.createIndividual(metricName, "owlq:CompositeMetric");
//			System.out.println("metric name: " + metricName);
			ontology.createDataProperty("neb:formula", metricName, ((CompositeMetric) m).getFormula());
    	}else if(m instanceof RawMetric){
    		
    		Sensor s = ((RawMetric) m).getSensor();
    		    			
			ontology.createIndividual(metricName, "owlq:SimpleMetric");
			ontology.createIndividual(metricName + "_SENSOR", "owlq:Sensor");
			
			ontology.createObjectProperty("owlq:sensor", metricName, metricName + "_SENSOR") ;
			if(s.getType() != null)
				ontology.createDataProperty("neb:type", metricName + "_SENSOR", s.getType());
			if(s.getAffinity() != null)
				ontology.createDataProperty("neb:affinity", metricName + "_SENSOR", s.getAffinity());
    	}
    	ontology.createDataProperty("neb:isMaximizing", metricName, m.isMaximizing());
    	createWindowOutput(metricName, "owlq:Window", m.getWindow());
    	createWindowOutput(metricName, "neb:Output", m.getOutput());

    	return metricName;
    }
        
    
	public RequestSLO constructSLO(Map<String, Object> req, String slaName, char type) {
		RequestSLO slo = new RequestSLO();
		
		slo.setSlaName(slaName);
		Object constraint = req.get("constraint");
		String name = (String) req.get("name");
		String firstArgument;
		Object secondArgument;
		if(constraint instanceof String) {
			String[] cons = ((String) constraint).split("((?<=[<>=])|(?=[<>=]))");
			firstArgument = cons[0];
			
			String secArg = cons[2].trim();
			 
			try {
				secondArgument = Integer.parseInt(secArg);
			}catch(Exception e) {
				try {
					secondArgument = Double.parseDouble(secArg);
				}catch(Exception f) {
					secondArgument = secArg;
				}
			}

			slo.setOperator(ComparisonOperator.convert(cons[1]));
										
		}else {
			Map<String, Object> cons = (Map<String, Object>)constraint;
			firstArgument = (String) cons.get("metric");
			secondArgument = cons.get("threshold");
			slo.setOperator(ComparisonOperator.convert((String)cons.get("operator")));
		}
		
		String []argumentComponents = firstArgument.split("((?<=[*\\/+-])|(?=[*\\/+-]))");
		String firstArgumentFinal = "";
		
		for(int j = 0; j < argumentComponents.length; j++) {
			if(j % 2 == 0)						
				firstArgumentFinal = firstArgumentFinal + slaName + "_" + argumentComponents[j].trim().toUpperCase();
			else
				firstArgumentFinal = firstArgumentFinal +  argumentComponents[j].trim();
		}

		
		slo.setFirstArgument(firstArgumentFinal);
		slo.setSecondArgument(secondArgument);
		
		slo.setSloName(name);
		slo.setSloType(type);
		
		return slo;
    }
    
    
	public Metric constructMetric(Map<String, Object> metr, Map<String, String> references) {
    	
    	Metric metric = null;
		
		String name = ((String) metr.get("name"));
		String formula = (String) metr.get("formula");
		Object sensor =  metr.get("sensor");
		
		String type = (String) metr.get("type");

		String ref = (String) metr.get("ref");
		
		Object windowObj = metr.get("window");
		Object outputObj = metr.get("output");
		
		System.out.println("n: " + name);
		if(ref != null) {
//			System.out.println(name + " ref: " + ref + "\n");
			references.put(name, ref);
			return null;
		}
		
		if(type != null && type.equals("constant")) {
			return null;
		}
		
		if(formula != null){
			CompositeMetric cMetric = new CompositeMetric();
			metric = cMetric;
			
			cMetric.setFormula(formula);
//			System.out.println("in");
									
		}else if(sensor != null){
			RawMetric rMetric = new RawMetric();
			metric = rMetric;
			
			Sensor s = new Sensor();

			if(sensor instanceof String) {
				String []comps = ((String) sensor).split(" ");
				s.setType(comps[0]);
				s.setAffinity(comps[1]);
			}else {
				Map<String, Object> comps = (Map<String, Object>) sensor;
				s.setType((String) comps.get("type"));
				s.setAffinity((String) comps.get("affinity"));
			}
			
			rMetric.setSensor(s);
//			System.out.println(rMetric.getSensor());
		}
		
		metric.setOutput(parseWindowOutput(outputObj));
		metric.setWindow(parseWindowOutput(windowObj));
		
		
		metric.setName(name);
//		System.out.println(metric);
		return metric;
    }
    
    
	private WindowOutput parseWindowOutput(Object winOutObj) {
    	
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
    	
    	System.out.println(windowOutput);
    	return windowOutput;
    }

    private void createWindowOutput(String name, String type, WindowOutput wo) {
    	
    	if(wo == null)
    		return;
    	
    	String woName;
    	
    	if(type.equals("owlq:Window")) {
    		woName = name + "_WINDOW";
    		ontology.createObjectProperty("owlq:window", name, woName);
    	}else {
    		woName = name  + "_OUTPUT";
    		ontology.createObjectProperty("neb:output", name, woName);
    	}
//    	System.out.println("WO: " + wo);
     	ontology.createIndividual(woName, type);

    	 
//    	System.out.println(woName);
    	ontology.createDataProperty("neb:type", woName, wo.getType());
    	ontology.createDataProperty("neb:value", woName, wo.getValue());
    	
    	
    	if(ontology.countInstances(URLEncoder.encode("{" + wo.getUnit() + "}", StandardCharsets.UTF_8)) == 0)
    		ontology.createIndividual(wo.getUnit(), "owlq:Unit");
   	
    	ontology.createObjectProperty("owlq:unit", woName, wo.getUnit());
    }
    @PostMapping("create/completeSla")
    public void createCompleteSla(@RequestBody CompleteSLA sla) {
    	
    	final String slaName = this.createSLA();
 	    	
//    	System.out.println(sla);
   	
    	
    	this.createSL(new RequestSL(slaName, "NORMAL"));
    	this.createSL(new RequestSL(slaName, "LOW"));
    	sla.getMetrics().forEach(t -> System.out.println(t));
    	sla.getMetrics().forEach(t -> this.createMetric(new RequestMetric(t, slaName)));
    	
    	Map<String, List<RequestSLO>> sloMap = new HashMap<String, List<RequestSLO>>();
    	
    	for(int i = 0; i < sla.getSlos().size(); i++) {
    		RequestSLO slo = sla.getSlos().get(i);
    		
    		slo.setFirstArgument(slaName + "_" + slo.getFirstArgument());
    		
    		if(sloMap.get(slo.getFirstArgument()) == null) {
    			sloMap.put(slo.getFirstArgument(), new ArrayList<RequestSLO>());
    		}
    		sloMap.get(slo.getFirstArgument()).add(slo);
    	}
    	
    	sloMap.forEach((key, value) -> {
    		
    		if(value.size() == 1) {
   			
    			this.createSLO(value.get(0));
    		}else if (value.size() == 2){
    			
    			RequestSLO slo0 = value.get(0);
    			RequestSLO slo1 = value.get(1);
    			boolean isMaximizing = false;
    			
    			slo0.setSlaName(slaName);
    			slo1.setSlaName(slaName);
    			    			
    			for(Metric m : sla.getMetrics()) {
    				if(m.getName() == slo0.getFirstArgument())
    					isMaximizing = m.isMaximizing();
    			}
    			
    			if(slo0.getSecondArgument() instanceof Number) {
    				double arg0 = Double.parseDouble(slo0.getSecondArgument().toString());
    				double arg1 = Double.parseDouble(slo1.getSecondArgument().toString());
    				
    				
    				if((isMaximizing && arg0 > arg1 || (!isMaximizing && arg0< arg0))) {
    					slo0.setSlName("NORMAL");
    					slo1.setSlName("LOW");
    				}else {
    					slo0.setSlName("LOW");
    					slo1.setSlName("NORMAL");
    				}
    				
    				this.createSLO(slo0);
    				this.createSLO(slo1);
    			}else {
					slo0.setSlName("NORMAL");
					slo1.setSlName("LOW");
    				this.createSLO(slo0);
    				this.createSLO(slo1);
					slo0.setSlName("LOW");
					slo1.setSlName("NORMAL");
    				this.createSLO(slo0);
    				this.createSLO(slo1);
    			}		
    		}
    	});
    	
    	sla.getSlTransitions().forEach(transition -> {
    		final String slTransName = slaName + "_" + transition.getFirstSl() + "_TO_" + transition.getSecondSl();
    		
    		ontology.createIndividual(slTransName, "owlq:SLTransition");
    		ontology.createObjectProperty("owlq:slTransition", slaName, slTransName);
    		
    		
    		ontology.createObjectProperty("owlq:firstSL", slTransName, slaName + "_SL_" + transition.getFirstSl());
    		ontology.createObjectProperty("owlq:secondSL", slTransName, slaName + "_SL_" + transition.getSecondSl());
    		
    		ontology.createDataProperty("owlq:violationThreshold", slTransName, transition.getViolationThreshold());
    		ontology.createDataProperty("owlq:evaluationPeriod", slTransName, transition.getEvaluationPeriod());
    	});

    	
//    	sla.getSlos().forEach((sl, slos) -> {
//    		this.createSL(new RequestSL(slaName, sl));
//    		slos.forEach(slo -> {
//    			
//        		RequestSLO requestSlo = new RequestSLO();
//        		requestSlo.setSlaName(slaName);
//        		requestSlo.setSlName(sl);
//        		
//        		requestSlo.setFirstArgument(slo.getFirstArgument());
//        		requestSlo.setOperator(slo.getOperator());
//        		requestSlo.setSecondArgument(slo.getSecondArgument());
//        		
//        		requestSlo.setSloType(slo.getSloType());
//        		requestSlo.setTransition(slo.getTransition());
//        		requestSlo.setSettlementPricePercentage(slo.getSettlementPricePercentage());
//        		
//        		this.createSLO(requestSlo);
//    		});

//    	});
    }
    
    private void insertMetrics(String slaName, List<Map<String, Object>> mets,  Map<String, RequestMetric> metrics, Map<String, String> references) {
    	if(mets != null)
			for(Map<String, Object> metr : mets) {
				Metric metric = constructMetric(metr, references);
				
				if(metric != null) 
					metrics.put(slaName + "_" + metric.getName().toUpperCase(), new RequestMetric(metric, slaName));
			}
    }
    
    private void insertRequirements(String slaName, List<Map<String, Object>> reqs,  Map<String, RequestSLO> requirements, char type) {
		if(reqs != null)
			for(Map<String, Object> req : reqs) {
				RequestSLO slo = constructSLO(req, slaName, type);
				requirements.put(slaName + "_" + slo.getSloName().toUpperCase(), slo);
				
			}
    }
}