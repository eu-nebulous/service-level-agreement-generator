package org.seerc.nebulous.sla.rest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seerc.nebulous.sla.components.ComparisonOperator;
import org.seerc.nebulous.sla.components.CompositeMetric;
import org.seerc.nebulous.sla.components.Constraint;
import org.seerc.nebulous.sla.components.InMemorySLAAttributes;
import org.seerc.nebulous.sla.components.Metric;
import org.seerc.nebulous.sla.components.RawMetric;
import org.seerc.nebulous.sla.components.RecurseConstraint;
import org.seerc.nebulous.sla.components.SL;
import org.seerc.nebulous.sla.components.SLA;
import org.seerc.nebulous.sla.components.SLTransition;
import org.seerc.nebulous.sla.components.Sensor;
import org.seerc.nebulous.sla.components.WindowOutput;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SLAPostController {
	private OntologyConnection ontology = OntologyConnection.getInstance();
//    Map<String, InMemorySLAAttributes> slaAttributes = new HashMap<String, InMemorySLAAttributes>();

//	private SALConnection sal = SALConnection.getInstance();
	
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
	
	
    /**
     * Creates a new SLA.
     * @return the URI of the new SLA.
     */
//    @PostMapping("/create/sla")
    public String createSLA(String id) {
    	
		final String SLANAME = "neb:SLA_" + id;
		
//		ontology.registerAsset(SLANAME);
		System.out.println("Creating SLA: " + SLANAME);
		ontology.createIndividual(SLANAME, "owlq:SLA");
		ontology.createIndividual(SLANAME, "odrl:AssetCollection");
	    	System.out.println("Created SLA: " + SLANAME);


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
		ontology.createIndividual(slName, "odrl:AssetCollection"); //Create the SL

		ontology.createObjectProperty("owlq:serviceLevel", rsl.getSlaName(), slName);	 //Connect it to the SLA	
		ontology.createObjectProperty("odrl:partOf", slName,  rsl.getSlaName());	 //Connect it to the SLA		
		ontology.createObjectProperty("owlq:logicalOperator", slName, "owlq:" + rsl.getOperator());
    	
    	return slName;
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
    	String metricName = metric.getSlaName() + "_" + m.getName();
		ontology.createIndividual(metricName, "odrl:AssetCollection");

    	//Depending on the type of metric, create the appropriate associations.
    	if(m instanceof CompositeMetric) {
    		
			ontology.createIndividual(metricName, "owlq:CompositeMetric");
//			System.out.println("metric name: " + metricName);
			ontology.createDataProperty("neb:formula", metricName, ((CompositeMetric) m).getFormula(), "rdf:PlainLiteral");

    	}else if(m instanceof RawMetric){
    		
    		Sensor s = ((RawMetric) m).getSensor();
    		    			
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
    	
    	return windowOutput;
    }

    private void createWindowOutput(String metricName, String type, WindowOutput wo) {
    	
    	if(wo == null)
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
    	ontology.createDataProperty("neb:type", woName, wo.getType(), "xsd:string");
    	ontology.createDataProperty("neb:value", woName, wo.getValue().toString(), getDatatype(wo.getValue().toString()));
    	
    	
    	if(ontology.countInstances(encode("{" + wo.getUnit() + "}")) == 0)
    		ontology.createIndividual(wo.getUnit(), "owlq:Unit");
   	
    	ontology.createObjectProperty("owlq:unit", woName, wo.getUnit());
    }
    
    
    @PostMapping("create/sla")
    public void createCompleteSla(@RequestBody SLA sla) { 	
    	System.out.println("CREATING NEW SLA :" + sla.getSlaName());

    	final String slaName = this.createSLA(sla.getSlaName());

    	sla.getMetrics().forEach(metric -> {
    		String metricName = this.createMetric(new RequestMetric(metric, slaName));
    		ontology.createObjectProperty("odrl:partOf", metricName, slaName);
    	});
	System.out.println("Creating metrics...");
    	
    	for(int i = 0; i < sla.getSls().size(); i++) {
    		final SL sl =  sla.getSls().get(i);
    		
    		final String slName = this.createSL(new RequestSL(slaName, sl));
    		System.out.println("Creating SL and SLOs...");
    		RecurseConstraint rc = new RecurseConstraint(slName, slaName);
    		for(Constraint c : sl.getOperands()) {
    			
    			String constName = rc.buildConstraint(c);
    			
    			ontology.createObjectProperty("owlq:constraint", slName, constName);
    			ontology.createObjectProperty("odrl:partOf", constName, slName);
    		}
    	}
    	
    	if(sla.getTransitions() != null)
	    	for(SLTransition trans : sla.getTransitions()) {
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
	    	
    	if(sla.getSettlement() != null) {
		System.out.println("Creating Settlement...");
	    	ontology.createIndividual(slaName + "_SETTLEMENT", "owlq:Settlement");
	    	ontology.createIndividual(slaName + "_SETTLEMENT", "odrl:Asset");
	    	ontology.createObjectProperty("odrl:partOf", slaName + "_SETTLEMENT", slaName);
	
	    	ontology.createDataProperty("owlq:evaluationPeriod", slaName + "_SETTLEMENT", sla.getSettlement().getEvaluationPeriod().toString(), "xsd:duration");
	    	ontology.createDataProperty("owlq:settlementCount", slaName + "_SETTLEMENT", Integer.toString(sla.getSettlement().getSettlementCount()),  "xsd:integer");
	    	ontology.createDataProperty("owlq:settlementAction", slaName + "_SETTLEMENT", sla.getSettlement().getSettlementAction(), "xsd:string");

	
	    	
	    	ontology.createObjectProperty("owlq:settlement", slaName, slaName + "_SETTLEMENT");
	    	ontology.createObjectProperty("owlq:concernedSL", slaName + "_SETTLEMENT", slaName + "_SL_" + sla.getSettlement().getConcernedSL());
    	}

//    	slaAttributes.put(slaName, new InMemorySLAAttributes("1"));
//    	cluster.publish("slas", new ObjectMapper().convertValue(sla, new TypeReference<Map<String, Object>>() {}));
    	
//    	ontology.registerAsset(slaName);
    	System.out.println(slaName);
	    System.out.println("Finished Creating SLA...");
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
//    	final long evaluationPeriod;// = 3600000; // 60 s * 60 min * 1000 to convert to milliseconds.
//    	long currentTime = System.currentTimeMillis();
//    	int activeViolations = 0;
////    	
////    	System.out.println("Begin");
////    	slaAttributes.forEach((t, u) -> System.out.println(t + " " + u));
////    	System.out.println("End");
//
////    	InMemorySLAAttributes attr = slaAttributes.get("neb:" + slaName);
//    	
//    	System.out.println(slaName + "_SL_" + attr.getActiveSL());
//    	List<String> activeTransition = ontology.getInstances(encode("firstSL value " + slaName + "_SL_" + attr.getActiveSL()));
//    	List<String> activeSettlement = ontology.getInstances(encode("concernedSL value " + slaName + "_SL_" + attr.getActiveSL()));
//    	String active;
//    	if(activeTransition == null || activeTransition.size() == 0) {
//    		active = activeSettlement.get(0);;
//    	}else
//    		active = activeTransition.get(0);;
//    	
//        String ep = (String) ontology.getDataProperty(active, "owlq:evaluationPeriod").get(0);
//      	
//        evaluationPeriod = getDurationInMilliseconds(ep);
//        System.out.println(ep + " " + evaluationPeriod);
//    	for(int i =0; i< attr.getViolationTimeStamp().size(); i++) {
//    		if(currentTime - attr.getViolationTimeStamp().get(i) <= evaluationPeriod) 
//    			activeViolations++;
//    	}
//    	int threshold;
//    	char type = 't';
//    	try {
//    		threshold  = (int) ontology.getDataProperty(active, "owlq:violationThreshold").get(0);
//    	}catch (Exception e) {
//    		threshold = (int) ontology.getDataProperty(active, "owlq:settlementCount").get(0);
//    		type = 's';
//		}
//    	if(activeViolations >= threshold) {
//    		System.out.println("Violated (" + activeViolations + ")");
//    		if (type == 't'){
//	    		String s = ontology.getInstances("inverse secondSL value " + active).get(0).split("_")[3];
//	    		attr.setActiveSL(s);
//	    		attr.setViolationTimeStamp(new ArrayList<Long>());
//	    		System.out.println("Switching to SL_" + attr.getActiveSL());
//    		}else if(type == 's') {
//    			System.out.println("Violated lowest SL...");
//    		}
//    	}
//    	else
//    		System.out.println("Not violated (" + activeViolations + ")");
//    	
//    	System.out.println(attr);
//    }

	private String encode(String query) {
		return URLEncoder.encode(query, StandardCharsets.UTF_8);
	}
//	private long getDurationInMilliseconds(String duration) {
//		String date = duration.split("T")[0];
//		String time = duration.split("T")[1];
//
//		long milliseconds = 0;
////		String h[] = time.split("H");
////		
////		for(int i = 0; i < h.length; i++)
////			System.out.println("h " + i + ": " + h[i]);
//			
//		if(date.matches("Y"))
//			milliseconds += 31557600000L * Long.parseLong(date.split("Y")[0]); //Years
//		if(date.matches("M"))
//			milliseconds +=  2629746000L * Long.parseLong(date.split("M)")[0]); //Months
//		if(date.matches("D"))
//			milliseconds +=    86400000L * Long.parseLong(date.split("D")[0]); //Days
//		
//		if(time.matches("H"))
//			milliseconds +=     3600000L * Long.parseLong(time.split("H")[0]); //Hours
//		if(time.matches("M"))
//		milliseconds +=       60000L * Long.parseLong(time.split("M")[0]); //Hours
//		if(time.matches("S"))
//			milliseconds +=        1000L * Long.parseLong(time.split("S")[0]); //Minutes
//		
//		return milliseconds;
//	}
	private String getDatatype(String literalValue){
		String result = "xsd:string";
		
		if(literalValue.matches("(\\+|-)?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)"))
			result =  "xsd:decimal";
		else if(literalValue.matches("-?P((([0-9]+Y([0-9]+M)?([0-9]+D)?|([0-9]+M)([0-9]+D)?|([0-9]+D))(T(([0-9]+H)([0-9]+M)?([0-9]+(\\.[0-9]+)?S)?|([0-9]+M)([0-9]+(\\.[0-9]+)?S)?|([0-9]+(\\.[0-9]+)?S)))?)|(T(([0-9]+H)([0-9]+M)?([0-9]+(\\.[0-9]+)?S)?|([0-9]+M)([0-9]+(\\.[0-9]+)?S)?|([0-9]+(\\.[0-9]+)?S))))"))
			result = "xsd:duration";
				
		return result;
	}

}
