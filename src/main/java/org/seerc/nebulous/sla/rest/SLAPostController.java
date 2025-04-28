package org.seerc.nebulous.sla.rest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.seerc.nebulous.sla.components.ComparisonOperator;
import org.seerc.nebulous.sla.components.SLA;
import org.seerc.nebulous.sla.components.CompositeMetric;
import org.seerc.nebulous.sla.components.Constraint;
import org.seerc.nebulous.sla.components.Metric;
import org.seerc.nebulous.sla.components.RawMetric;
import org.seerc.nebulous.sla.components.RecurseConstraint;
import org.seerc.nebulous.sla.components.SL;

import org.seerc.nebulous.sla.components.SLTransition;
import org.seerc.nebulous.sla.components.Sensor;
import org.seerc.nebulous.sla.components.WindowOutput;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SLAPostController {
	//TODO: add error checking and discuss non-volatile storage.
	private OntologyConnection ontology = OntologyConnection.getInstance();
//	private SALConnection sal = SALConnection.getInstance();
//	private ActiveMQConnection cluster = ActiveMQConnection.getInstance();
	


//
//	@PostMapping("create/models")
//	public void parseModels() {
//		
//		Map<String, String> slaNames = new HashMap<String, String>();
//		
//		Map<String, RequestMetric> metrics = new HashMap<String, RequestMetric>();
//		Map<String, RequestSLO> requirements = new HashMap<String, RequestSLO>();
//		
////		parseMetricModel(slaNames, metrics, requirements);
//		parseKubevela(slaNames, requirements);
//		
//		//Inserting into ontology
//		for(Map.Entry<String, RequestMetric> entry: metrics.entrySet()) 
//			createMetric(entry.getValue());
//		
//		for(Map.Entry<String, RequestSLO> entry: requirements.entrySet())
//			createSLO(entry.getValue());
//	}
//	
//	@PostMapping("create/kubevela")
//	public void parseKubevela(Map<String, String> slaNames, Map<String, RequestSLO> requirements ) {
//		try {
//			KubevelaParser kubevela = new KubevelaParser((new FileInputStream(new File("KubeVela_v2.yaml"))));
//			
//			for(int i = 0; i <  kubevela.getComponents().size(); i++) {
//				Map<String, Map<String, String>> resources = kubevela.getResources(i);
//				
//				if(resources == null || resources.size() == 0)
//					continue;
//				
//				String slaName = slaNames.get(kubevela.getComponentName(i));
//	
//				RequestSLO maxCpu = new RequestSLO();
////				System.out.println(resources);
//				maxCpu.setFirstArgument("neb:MAX_CPU_CORES");
//				maxCpu.setOperator(ComparisonOperator.LESS_EQUAL_THAN);
//				maxCpu.setSecondArgument(resources.get("limits").get("cpu"));
//				maxCpu.setSlaName(slaName);
////				maxCpu.setSloType('D');
//				
//				RequestSLO maxRam= new RequestSLO();
//				
//				maxRam.setFirstArgument("neb:MAX_RAM");
//				maxRam.setOperator(ComparisonOperator.LESS_EQUAL_THAN);
//				maxRam.setSecondArgument(resources.get("limits").get("memory"));
//				maxRam.setSlaName(slaName);
////				maxRam.setSloType('D');
//
//				
//				RequestSLO requestedCpu = new RequestSLO();
//				
//				requestedCpu.setFirstArgument("neb:REQUESTED_CPU_CORES");
//				requestedCpu.setOperator(ComparisonOperator.EQUALS);
//				requestedCpu.setSecondArgument(resources.get("requests").get("cpu"));
//				requestedCpu.setSlaName(slaName);
////				requestedCpu.setSloType('D');
//
//				RequestSLO requestRam = new RequestSLO();
//				
//				requestRam.setFirstArgument("neb:REQUESTED_RAM");
//				requestRam.setOperator(ComparisonOperator.EQUALS);
//				requestRam.setSecondArgument(resources.get("requests").get("memory"));
//				requestRam.setSlaName(slaName);
////				requestRam.setSloType('D');
//
//				requirements.put(slaName + "_MAX_CPU_CORES", maxCpu);
//				requirements.put(slaName + "_MAX_RAM", maxRam);
//				requirements.put(slaName + "_REQUESTED_CPU_CORES", requestedCpu);
//				requirements.put(slaName + "_REQUESTED_RAM", requestRam);
//
//			}
//					
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
	
	
//	@PostMapping("create/metricModel")
//	public void parseMetricModel(Map<String, String> slaNames, Map<String, RequestMetric> metrics, Map<String, RequestSLO> requirements) {
//		try {
//			MetricModelParser metricModel = new MetricModelParser((new FileInputStream(new File("augmenta_metric_model_draft_v4.yml"))));
//			Map<String, String> references = new HashMap<String, String>();
//
//			for(int i = 0; i < metricModel.getComponents().size(); i++) {
//				
//				String slaName = createSLA();
//				slaNames.put((String) metricModel.getComponent(i).get("name"), slaName);
//				
//				List<Map<String, Object>> reqs = metricModel.getComponentRequirements(i);
//				List<Map<String, Object>> mets = metricModel.getComponentMetrics(i);
//				
//				insertMetrics(slaName, mets, metrics, references);
//				insertRequirements(slaName, reqs, requirements, 'R');
//			} 
//			
//			//scopes
//			for(Object sc : metricModel.getScopes()) {
//				Map<String, Object> scope = (Map<String, Object>) sc;
//				
//				List<String> components = (List<String>) scope.get("components");
//				
//				List<Map<String, Object>> mets = (List<Map<String, Object>>) scope.get("metrics");
//				List<Map<String, Object>> reqs = (List<Map<String, Object>>) scope.get("requirements");
//				if(components != null)
//					for(String comp : components) {
//		 				
//						String slaName = slaNames.get(comp);
//												
//						insertMetrics(slaName, mets, metrics, references);
//						insertRequirements(slaName, reqs, requirements, 'R');
//					}			
//				else
//		 			slaNames.forEach((t, u) -> {
//						insertMetrics(u, mets, metrics, references);
//						insertRequirements(u, reqs, requirements, 'R');
//					});
//						
//			}
//						
//			references.forEach((t, u) -> {
//				String []parts = u.split("[^a-z_]+");
//				RequestMetric m = metrics.get(slaNames.get(parts[1]) + "_" + parts[2].toUpperCase());
//				metrics.put(t, m);
//			});
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	Map<String, InMemorySLAAttributes> slaAttributes = new HashMap<String, InMemorySLAAttributes>();


//	@PostMapping("create/models")
//	public void parseModels() {
//		
//		Map<String, String> slaNames = new HashMap<String, String>();
//		
//		Map<String, RequestMetric> metrics = new HashMap<String, RequestMetric>();
//		Map<String, RequestSLO> requirements = new HashMap<String, RequestSLO>();
//		
//		parseMetricModel(slaNames, metrics, requirements);
//		parseKubevela(slaNames, requirements);
//		
//		//Inserting into ontology
//		for(Map.Entry<String, RequestMetric> entry: metrics.entrySet()) 
//			createMetric(entry.getValue());
//		
//		for(Map.Entry<String, RequestSLO> entry: requirements.entrySet())
//			createSLO(entry.getValue());
//	}
//	
//	@PostMapping("create/kubevela")
//	public void parseKubevela(Map<String, String> slaNames, Map<String, RequestSLO> requirements ) {
//		try {
//			KubevelaParser kubevela = new KubevelaParser((new FileInputStream(new File("KubeVela_v2.yaml"))));
//			
//			for(int i = 0; i <  kubevela.getComponents().size(); i++) {
//				Map<String, Map<String, String>> resources = kubevela.getResources(i);
//				
//				if(resources == null || resources.size() == 0)
//					continue;
//				
//				String slaName = slaNames.get(kubevela.getComponentName(i));
//	
//				RequestSLO maxCpu = new RequestSLO();
////				System.out.println(resources);
//				maxCpu.setFirstArgument("neb:MAX_CPU_CORES");
//				maxCpu.setOperator(ComparisonOperator.LESS_EQUAL_THAN);
//				maxCpu.setSecondArgument(resources.get("limits").get("cpu"));
//				maxCpu.setSlaName(slaName);
////				maxCpu.setSloType('D');
//				
//				RequestSLO maxRam= new RequestSLO();
//				
//				maxRam.setFirstArgument("neb:MAX_RAM");
//				maxRam.setOperator(ComparisonOperator.LESS_EQUAL_THAN);
//				maxRam.setSecondArgument(resources.get("limits").get("memory"));
//				maxRam.setSlaName(slaName);
////				maxRam.setSloType('D');
//
//				
//				RequestSLO requestedCpu = new RequestSLO();
//				
//				requestedCpu.setFirstArgument("neb:REQUESTED_CPU_CORES");
//				requestedCpu.setOperator(ComparisonOperator.EQUALS);
//				requestedCpu.setSecondArgument(resources.get("requests").get("cpu"));
//				requestedCpu.setSlaName(slaName);
////				requestedCpu.setSloType('D');
//
//				RequestSLO requestRam = new RequestSLO();
//				
//				requestRam.setFirstArgument("neb:REQUESTED_RAM");
//				requestRam.setOperator(ComparisonOperator.EQUALS);
//				requestRam.setSecondArgument(resources.get("requests").get("memory"));
//				requestRam.setSlaName(slaName);
////				requestRam.setSloType('D');
//
//				requirements.put(slaName + "_MAX_CPU_CORES", maxCpu);
//				requirements.put(slaName + "_MAX_RAM", maxRam);
//				requirements.put(slaName + "_REQUESTED_CPU_CORES", requestedCpu);
//				requirements.put(slaName + "_REQUESTED_RAM", requestRam);
//
//			}
//					
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
	
	
//	@PostMapping("create/metricModel")
//	public void parseMetricModel(Map<String, String> slaNames, Map<String, RequestMetric> metrics, Map<String, RequestSLO> requirements) {
//		try {
//			MetricModelParser metricModel = new MetricModelParser((new FileInputStream(new File("augmenta_metric_model_draft_v4.yml"))));
//			Map<String, String> references = new HashMap<String, String>();
//
//			for(int i = 0; i < metricModel.getComponents().size(); i++) {
//				
//				String slaName = createSLA();
//				slaNames.put((String) metricModel.getComponent(i).get("name"), slaName);
//				
//				List<Map<String, Object>> reqs = metricModel.getComponentRequirements(i);
//				List<Map<String, Object>> mets = metricModel.getComponentMetrics(i);
//				
//				insertMetrics(slaName, mets, metrics, references);
//				insertRequirements(slaName, reqs, requirements, 'R');
//			} 
//			
//			//scopes
//			for(Object sc : metricModel.getScopes()) {
//				Map<String, Object> scope = (Map<String, Object>) sc;
//				
//				List<String> components = (List<String>) scope.get("components");
//				
//				List<Map<String, Object>> mets = (List<Map<String, Object>>) scope.get("metrics");
//				List<Map<String, Object>> reqs = (List<Map<String, Object>>) scope.get("requirements");
//				if(components != null)
//					for(String comp : components) {
//		 				
//						String slaName = slaNames.get(comp);
//												
//						insertMetrics(slaName, mets, metrics, references);
//						insertRequirements(slaName, reqs, requirements, 'R');
//					}			
//				else
//		 			slaNames.forEach((t, u) -> {
//						insertMetrics(u, mets, metrics, references);
//						insertRequirements(u, reqs, requirements, 'R');
//					});
//						
//			}
//						
//			references.forEach((t, u) -> {
//				String []parts = u.split("[^a-z_]+");
//				RequestMetric m = metrics.get(slaNames.get(parts[1]) + "_" + parts[2].toUpperCase());
//				metrics.put(t, m);
//			});
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
    /**
     * Creates a new SLA.
     * @return the URI of the new SLA.
     */
    public String createSLA(String id) {
    	//Currently, the SLA's name is automatically generated by counting how many SLAs exist and giving it that number. I.e, if there are 3 SLAs,the first 3 they'd be named SLA_0 ... SLA_2, and the new one SLA_3
		final String SLANAME = "neb:SLA_" + id;
		System.out.println(SLANAME);
		ontology.createIndividual(SLANAME, "owlq:SLA");
		ontology.createIndividual(SLANAME, "odrl:AssetCollection");
			
		return SLANAME;
    }
    
    /**
     * Creates a new SL.
     * @param rsl THE SL.
     * @return the URI of the new SL.
     */
//    @PostMapping("/create/sl")
    public String createSL(@RequestBody RequestSL rsl) {
    	//The name of the SL is determined by the parameter
    	String slName = rsl.getSlaName() + "_SL_" + rsl.getSlName();
		ontology.createIndividual(slName, "owlq:SL"); //Create the SL
		ontology.createIndividual(slName, "owlq:AssetCollection"); //Create the SL
		ontology.createObjectProperty("owlq:serviceLevel", rsl.getSlaName(), slName);	 //Connect it to the SLA	
		ontology.createObjectProperty("odrl:partOf", slName,  rsl.getSlaName());	 //Connect it to the SLA		
		ontology.createObjectProperty("owlq:logicalOperator", slName, "owlq:" + rsl.getOperator());
    	
    	return slName;
    }
    /**
     * Creates a new SLO.
     * @param slo THE SLO.
     * @return the URI of the new SLO.
     */
//    @PostMapping("/create/slo")	
    public String createSLO(@RequestBody RequestSLO slo){
    	final String slaName = slo.getSlaName().split(":")[1];
		if(ontology.countInstances(encode("{" + slaName + "}")) != 1)
			return null;
		
		String sloName = slaName + "_SLO_";
		//The name of the SLO is determined by how many SLOs its SLA has.
		int i = 0;
		while(ontology.countInstances(encode("{" + sloName + i + "}")) != 0)
			i++;
		
		sloName += i;
		ontology.createIndividual("neb:" + sloName, "owlq:SLO"); //Create the SLO
		ontology.createIndividual("neb:" + sloName, "odrl:Asset"); //Create the SLO
		ontology.createObjectProperty("odrl:partOf", "neb:" + sloName, slaName); //Connect it with its Metric

		
		ontology.createObjectProperty("owlq:firstArgument", "neb:" + sloName, "neb:" + slaName + "_" + slo.getFirstArgument()); //Connect it with its Metric
		
		//Add second argument and operator.
		ontology.createDataProperty("owlq:secondArgument", "neb:" + sloName, slo.getSecondArgument());		
		ontology.createObjectProperty("owlq:operator", "neb:" + sloName, "owlq:" + slo.getOperator().toString());
		
		//Connect to SL.
		ontology.createObjectProperty("owlq:constraint", slo.getSlName(), "neb:" + sloName);
		ontology.createObjectProperty("odrl:partOf", "neb:" + sloName, slo.getSlName());

		
//		System.out.println("SL NAME: " + slo.getSlName());
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
		
		if(slo.getSettlementPricePercentage() >= 0d ) {
			ontology.createIndividual("neb:" + sloName + "_PN", "owlq:Penalty");
			ontology.createIndividual("neb:" + sloName + "_PN", "odrl:Asset"); 	
	
			ontology.createObjectProperty("owlq:penalty", "neb:" + sloName, "neb:" + sloName + "_PN");
			ontology.createObjectProperty("odrl:partOf", "neb:" + sloName + "_PN", "neb:" + sloName);
	
			ontology.createIndividual("neb:" + sloName + "_PN_C", "owlq:SLOCompensation");
			ontology.createIndividual("neb:" + sloName + "_PN_C", "odrl:Asset");
	
			ontology.createObjectProperty("owlq:compensation", "neb:" + sloName + "_PN", "neb:" + sloName + "_PN_C");
			ontology.createObjectProperty("odrl:partOf", "neb:" + sloName + "_PN_C", "neb:" + sloName);
	
			ontology.createDataProperty("owlq:settlementPricePercentage", "neb:" + sloName + "_PN_C", slo.getSettlementPricePercentage());
		}
	
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
    /**
     * Creates a new Metric.
     * @param rsl THE SL.
     * @return the URI of the new SL.
     */
//    @PostMapping("create/metric")
    public String createMetric(@RequestBody RequestMetric metric) {
    	
    	Metric m;
//    	System.out.println("metric:" +  metric);
    	try {
    	m = metric.getMetric();
    	}catch(Exception e) {
    		return null;
    	}
    	System.out.println(m.getClass());


    	String metricName = metric.getSlaName() + "_" + m.getName();
		ontology.createIndividual(metricName, "odrl:AssetCollection");

    	//Depending on the type of metric, create the appropriate associations.
    	if(m instanceof CompositeMetric) {
    		
			ontology.createIndividual(metricName, "owlq:CompositeMetric");
//			System.out.println("metric name: " + metricName);
			ontology.createDataProperty("neb:formula", metricName, ((CompositeMetric) m).getFormula());
    	}else if(m instanceof RawMetric){
    		
    		Sensor s = ((RawMetric) m).getSensor();
    		    			
			ontology.createIndividual(metricName, "owlq:RawMetric");
			System.out.println("Raw metric:" + metricName);
			
			if(s != null) {
			ontology.createIndividual(metricName + "_SENSOR", "owlq:Sensor");
			
			ontology.createObjectProperty("owlq:sensor", metricName, metricName + "_SENSOR") ;
			if(s.getType() != null)
				ontology.createDataProperty("neb:type", metricName + "_SENSOR", s.getType());
			if(s.getAffinity() != null)
				ontology.createDataProperty("neb:affinity", metricName + "_SENSOR", s.getAffinity());
			}
    	}else {
			ontology.createIndividual(metricName, "owlq:Metric");

    	}
//    	ontology.createDataProperty("neb:isMaximizing", metricName, m.isMaximizing());
    	
    	if(m.getWindow() != null)
    		createWindowOutput(metricName, "owlq:Window", m.getWindow());
    	if(m.getOutput() != null)
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
//		slo.setSloType(type);
		
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

    private void createWindowOutput(String metricName, String type, WindowOutput wo) {
    	
    	if(wo == null)
    		return;
    	
    	String woName;
    	
    	if(type.equals("owlq:Window")) {
    		woName = metricName + "_WINDOW";
    		ontology.createObjectProperty("owlq:window", metricName, woName);
    	}else {
    		woName = metricName  + "_OUTPUT";
    		ontology.createObjectProperty("neb:output", metricName, woName);
    	}
    	
//    	System.out.println("WO: " + wo);
     	ontology.createIndividual(woName, type);
     	ontology.createIndividual(woName, "odrl:Asset");
		ontology.createObjectProperty("odrl:partOf", woName, metricName);


    	 
//    	System.out.println(woName);
    	ontology.createDataProperty("neb:type", woName, wo.getType());
    	ontology.createDataProperty("neb:value", woName, wo.getValue());
    	
    	
    	if(ontology.countInstances(URLEncoder.encode("{" + wo.getUnit() + "}", StandardCharsets.UTF_8)) == 0)
    		ontology.createIndividual(wo.getUnit(), "owlq:Unit");
   	
    	ontology.createObjectProperty("owlq:unit", woName, wo.getUnit());
    }
    
    
    @PostMapping("create/sla")
    public void createCompleteSla(@RequestBody SLA sla) { 	
    	
    	System.out.println(sla.getSls());
    	
    	

    	final String slaName = this.createSLA(sla.getSlaName());

    	sla.getMetrics().forEach(metric -> {
    		String metricName = this.createMetric(new RequestMetric(metric, slaName));
    		ontology.createObjectProperty("odrl:partOf", metricName, slaName);
    	});
    	
    	for(int i = 0; i < sla.getSls().size(); i++) {
    		final SL sl =  sla.getSls().get(i);
    		
    		final String slName = this.createSL(new RequestSL(slaName, sl));
    
    		RecurseConstraint rc = new RecurseConstraint(slName, slaName);
    		for(Constraint c : sl.getOperands()) {
    			
    			String constName = rc.buildConstraint(c);
    			
    			ontology.createObjectProperty("owlq:constraint", slName, constName);
    			ontology.createObjectProperty("odrl:partOf", constName, slName);
    		}
    	}
    	
    	if(sla.getTransitions() != null)
	    	for(SLTransition trans : sla.getTransitions()) {
	    		final String slTransName = slaName + "_" + trans.getFirstSl() + "_TO_" + trans.getSecondSl();

	    		
	    		ontology.createIndividual(slTransName, "owlq:SLTransition");
	    		ontology.createIndividual(slTransName, "odrl:Asset");
	    		
	        	ontology.createObjectProperty("owlq:slTransition", slaName, slTransName);
	        	ontology.createObjectProperty("odrl:partOf", slTransName, slaName);
	
	        		
	        	ontology.createObjectProperty("owlq:firstSL", slTransName, slaName + "_SL_" + trans.getFirstSl());
	        	ontology.createObjectProperty("owlq:secondSL", slTransName, slaName + "_SL_" + trans.getSecondSl());
	        	
	        	ontology.createDataProperty("owlq:violationThreshold", slTransName, trans.getViolationThreshold());
	        	ontology.createDataProperty("owlq:evaluationPeriod", slTransName, trans.getEvaluationPeriod());
	    		
	    	}
	    	
    	if(sla.getSettlement() != null) {
	    	ontology.createIndividual(slaName + "_SETTLEMENT", "owlq:Settlement");
	    	ontology.createIndividual(slaName + "_SETTLEMENT", "owlq:Asset");
	    	ontology.createObjectProperty("odrl:partOf", slaName + "_SETTLEMENT", slaName);
	
	    	ontology.createDataProperty("owlq:evaluationPeriod", slaName + "_SETTLEMENT", sla.getSettlement().getEvaluationPeriod());
	    	ontology.createDataProperty("owlq:settlementCount", slaName + "_SETTLEMENT", sla.getSettlement().getSettlementCount());
	    	ontology.createDataProperty("owlq:settlementAction", slaName + "_SETTLEMENT", sla.getSettlement().getSettlementAction());
	
	    	
	    	ontology.createObjectProperty("owlq:settlement", slaName, slaName + "_SETTLEMENT");
	    	ontology.createObjectProperty("owlq:concernedSL", slaName + "_SETTLEMENT", slaName + "_SL_" + sla.getSettlement().getConcernedSL());
    	}
//    	cluster.publish("slas", new ObjectMapper().convertValue(sla, new TypeReference<Map<String, Object>>() {}));
    }
    
//    @PostMapping("/create/device")
//    public void createDeviceSLA(@RequestBody int jobId) {
//    	List<DeviceSLA> deviceSlas = sal.getDevicesFromJob(jobId);
//    	
//    	for(DeviceSLA deviceSla : deviceSlas) {
//    		
//    		NodeProperties properties = deviceSla.getNodeProperties();
//    		GeoLocation loc = properties.getGeoLocation();
//    		OperatingSystem os = properties.getOperatingSystem();
//    		
//    		SLA sla = new SLA(deviceSla.getName());
//    		
//    		SL sl = new SL();
//    		sl.setSlName("NORMAL");
//    		sla.getSls().add(sl);
//    		
//    		SLO ram = new SLO();
//    		ram.setFirstArgument("RAM");
//    		ram.setOperator(ComparisonOperator.EQUALS);
//    		ram.setSecondArgument(properties.getRam());
//    		ram.setSettlementPricePercentage(-1);
//    		sl.addSlo(ram);
//    		sla.getMetrics().add(new RawMetric("RAM"));
//
//    		SLO cores = new SLO();
//    		cores.setFirstArgument("CORES");
//    		cores.setOperator(ComparisonOperator.EQUALS);
//    		cores.setSecondArgument(properties.getCores());
//    		cores.setSettlementPricePercentage(-1);
//    		sl.addSlo(cores);
//    		sla.getMetrics().add(new Metric("CORES", null, null));
//
//    		SLO frequency = new SLO();
//    		frequency.setFirstArgument("FREQUENCY");
//    		frequency.setOperator(ComparisonOperator.EQUALS);
//    		frequency.setSecondArgument(properties.getCpuFrequency());
//    		frequency.setSettlementPricePercentage(-1);
//    		sl.addSlo(frequency);
//    		sla.getMetrics().add(new RawMetric("FREQUENCY"));
//    		
//    		SLO disk = new SLO();
//    		disk.setFirstArgument("DISK");
//    		disk.setOperator(ComparisonOperator.EQUALS);
//    		disk.setSecondArgument(properties.getDisk());
//    		disk.setSettlementPricePercentage(-1);
//    		sl.addSlo(disk);
//    		sla.getMetrics().add(new RawMetric("DISK"));
//
//    		SLO gpu = new SLO();
//    		gpu.setFirstArgument("GPU");
//    		gpu.setOperator(ComparisonOperator.EQUALS);
//    		gpu.setSecondArgument(properties.getGpu());
//    		gpu.setSettlementPricePercentage(-1);
//    		sl.addSlo(gpu);
//    		sla.getMetrics().add(new RawMetric("GPU"));
//
//    		SLO city = new SLO();
//    		city.setFirstArgument("CITY");
//    		city.setOperator(ComparisonOperator.EQUALS);
//    		city.setSecondArgument(loc.getCity());
//    		city.setSettlementPricePercentage(-1);
//    		sl.addSlo(city);
//    		sla.getMetrics().add(new RawMetric("CITY"));
//
//    		SLO country = new SLO();
//    		country.setFirstArgument("COUNTRY");
//    		country.setOperator(ComparisonOperator.EQUALS);
//    		country.setSecondArgument(loc.getCountry());
//    		country.setSettlementPricePercentage(-1);
//    		sl.addSlo(country);
//    		sla.getMetrics().add(new RawMetric("COUNTRY"));
//
//    		SLO latitude = new SLO();
//    		latitude.setFirstArgument("LATITUDE");
//    		latitude.setOperator(ComparisonOperator.EQUALS);
//    		latitude.setSecondArgument(loc.getLatitude());
//    		latitude.setSettlementPricePercentage(-1);
//    		sl.addSlo(latitude);
//    		sla.getMetrics().add(new RawMetric("LATITUDE"));
//
//    		SLO longitude = new SLO();
//    		longitude.setFirstArgument("neb:LONGITUDE");
//    		longitude.setOperator(ComparisonOperator.EQUALS);
//    		longitude.setSecondArgument(loc.getLongitude());
//    		longitude.setSettlementPricePercentage(-1);
//    		sl.addSlo(longitude);
//    		sla.getMetrics().add(new RawMetric("LONGITUDE"));
//
//    		SLO osArch = new SLO();
//    		osArch.setFirstArgument("OS_ARCHITECTURE");
//    		osArch.setOperator(ComparisonOperator.EQUALS);
//    		osArch.setSecondArgument(os.getOperatingSystemArchitecture());
//    		osArch.setSettlementPricePercentage(-1);
//    		sl.addSlo(osArch);
//    		sla.getMetrics().add(new RawMetric("OS_ARCHITECTURE"));
//
//    		SLO osFamily = new SLO();
//    		osFamily.setFirstArgument("OS_FAMILY");
//    		osFamily.setOperator(ComparisonOperator.EQUALS);
//    		osFamily.setSecondArgument(os.getOperatingSystemFamily());
//    		osFamily.setSettlementPricePercentage(-1);
//    		sl.addSlo(osFamily);
//    		sla.getMetrics().add(new RawMetric("OS_FAMILY"));
//
//    		SLO osVersion = new SLO();
//    		osVersion.setFirstArgument("OS_VERSION");
//    		osVersion.setOperator(ComparisonOperator.EQUALS);
//    		osVersion.setSecondArgument(os.getOperatingSystemVersion());
//    		osVersion.setSettlementPricePercentage(-1);
//    		sl.addSlo(osVersion);
//    		sla.getMetrics().add(new RawMetric("OS_VERSION"));
//
//    		createCompleteSla(sla);
//    	}
//    }

//    @PostMapping("append/violation")
//    public void addViolation(@RequestBody String slaName) {
//    	final long hour = 3600000; // 60 s * 60 min * 1000 to convert to milliseconds.
//    	long currentTime = System.currentTimeMillis();
//    	int activeViolations = 0;
//    	
////    	System.out.println("Begin");
////    	slaAttributes.forEach((t, u) -> System.out.println(t + " " + u));
////    	System.out.println("End");
//
//    	InMemorySLAAttributes attr = slaAttributes.get(slaName);
//    	System.out.println(slaName + "_SL_" + attr.getActiveSL());
//    	List<String> activeTransition = ontology.getInstances(encode("firstSL value " + slaName + "_SL_" + attr.getActiveSL()));
//    	List<String> activeSettlement = ontology.getInstances(encode("concernedSL value " + slaName + "_SL_" + attr.getActiveSL()));
//    	String active;
//    	if(activeTransition == null || activeTransition.size() == 0) {
//    		active = activeSettlement.get(0);;
//    	}else
//    		active = activeTransition.get(0);;
//    	
//    	System.out.println(active);
//    	attr.appendViolation(currentTime);
//    	 
//    	for(int i =0; i< attr.getViolationTimeStamp().size(); i++) {
//    		if(currentTime - attr.getViolationTimeStamp().get(i) <= hour) 
//    			activeViolations++;
//    	}
//    	
//    	int violationThreshold  = (int) ontology.getDataProperty(active, "owlq:violationThreshold").get(0);
//    	
//    	if(activeViolations >= violationThreshold) {
//    		System.out.println("Violated (" + activeViolations + ")");
//    		String s = ontology.getInstances("inverse secondSL value " + active).get(0).split("_")[3];
//    		attr.setActiveSL(s);
//    		attr.setViolationTimeStamp(new ArrayList<Long>());
//    		
//    		System.out.println("Switching to SL_" + attr.getActiveSL());
//    	}
//    	else
//    		System.out.println("Not violated (" + activeViolations + ")");
//    	
//    	System.out.println(attr);
//    }

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

//    @PostMapping("append/violation")
//    public void addViolation(@RequestBody String slaName) {
//    	final long hour = 3600000; // 60 s * 60 min * 1000 to convert to milliseconds.
//    	long currentTime = System.currentTimeMillis();
//    	int activeViolations = 0;
//    	
////    	System.out.println("Begin");
////    	slaAttributes.forEach((t, u) -> System.out.println(t + " " + u));
////    	System.out.println("End");
//
//    	InMemorySLAAttributes attr = slaAttributes.get(slaName);
//    	System.out.println(slaName + "_SL_" + attr.getActiveSL());
//    	List<String> activeTransition = ontology.getInstances(encode("firstSL value " + slaName + "_SL_" + attr.getActiveSL()));
//    	List<String> activeSettlement = ontology.getInstances(encode("concernedSL value " + slaName + "_SL_" + attr.getActiveSL()));
//    	String active;
//    	if(activeTransition == null || activeTransition.size() == 0) {
//    		active = activeSettlement.get(0);;
//    	}else
//    		active = activeTransition.get(0);;
//    	
//    	System.out.println(active);
//    	attr.appendViolation(currentTime);
//    	 
//    	for(int i =0; i< attr.getViolationTimeStamp().size(); i++) {
//    		if(currentTime - attr.getViolationTimeStamp().get(i) <= hour) 
//    			activeViolations++;
//    	}
//    	
//    	int violationThreshold  = (int) ontology.getDataProperty(active, "owlq:violationThreshold").get(0);
//    	
//    	if(activeViolations >= violationThreshold) {
//    		System.out.println("Violated (" + activeViolations + ")");
//    		String s = ontology.getInstances("inverse secondSL value " + active).get(0).split("_")[3];
//    		attr.setActiveSL(s);
//    		attr.setViolationTimeStamp(new ArrayList<Long>());
//    		
//    		System.out.println("Switching to SL_" + attr.getActiveSL());
//    	}
//    	else
//    		System.out.println("Not violated (" + activeViolations + ")");
//    	
//    	System.out.println(attr);
//    }

//    private void insertMetrics(String slaName, List<Map<String, Object>> mets,  Map<String, RequestMetric> metrics, Map<String, String> references) {
//    	if(mets != null)
//			for(Map<String, Object> metr : mets) {
//				Metric metric = constructMetric(metr, references);
//				
//				if(metric != null) 
//					metrics.put(slaName + "_" + metric.getName().toUpperCase(), new RequestMetric(metric, slaName));
//			}
//    }
//    
//    private void insertRequirements(String slaName, List<Map<String, Object>> reqs,  Map<String, RequestSLO> requirements, char type) {
//		if(reqs != null)
//			for(Map<String, Object> req : reqs) {
//				RequestSLO slo = constructSLO(req, slaName, type);
//				requirements.put(slaName + "_" + slo.getSloName().toUpperCase(), slo);
//				
//			}
//    }

	private String encode(String query) {
		return URLEncoder.encode(query, StandardCharsets.UTF_8);
	}
}