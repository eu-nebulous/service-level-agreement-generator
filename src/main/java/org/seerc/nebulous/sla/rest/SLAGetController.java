//package org.seerc.nebulous.sla.rest;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.qpid.protonj2.client.Message;
//import org.seerc.nebulous.sla.components.CompositeMetric;
//import org.seerc.nebulous.sla.components.Metric;
//import org.seerc.nebulous.sla.components.RawMetric;
//import org.seerc.nebulous.sla.components.SL;
//import org.seerc.nebulous.sla.components.SLA;
//import org.seerc.nebulous.sla.components.SLO;
//import org.seerc.nebulous.sla.components.SLTransition;
//import org.seerc.nebulous.sla.components.Sensor;
//import org.seerc.nebulous.sla.components.Settlement;
//import org.seerc.nebulous.sla.components.WindowOutput;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import eu.nebulouscloud.exn.Connector;
//
//import eu.nebulouscloud.exn.core.Consumer;
//import eu.nebulouscloud.exn.core.Context;
//import eu.nebulouscloud.exn.core.Handler;
//import eu.nebulouscloud.exn.core.Publisher;
//import eu.nebulouscloud.exn.handlers.ConnectorHandler;
//import eu.nebulouscloud.exn.settings.StaticExnConfig;
//
//@RestController
//public class SLAGetController {
//	private OntologyConnection ontology = OntologyConnection.getInstance();
//	private EXNConnection exn = EXNConnection.getInstance();
//	/**
//	 * @param slaName
//	 * @return sla json
//	 * @throws Exception 
//	 */
//
//	@GetMapping("/get/metrics/sla")
//	List<Metric> test(@RequestParam("appId") String appId) {
//		List<Metric> res = new ArrayList<Metric>();
//		List<Map<String, Object>> metrics = (List<Map<String, Object>>) exn.getApp(appId).get("metrics");
//		System.out.println(metrics);
//		for(var m: metrics) {
//			Metric metric = new Metric();
//			
//			metric.setName((String) m.get("name"));
//			
//			String outputKey = null;
//			if(m.containsKey("output"))
//				outputKey = "output";
//			else if(m.containsKey("outputRaw"))
//				outputKey = "outputRaw";
//			
//			if(outputKey != null) {
//				Map out = (Map) m.get(outputKey);
//				WindowOutput output = new WindowOutput();
//				output.setType((String) out.get("type"));
//				output.setUnit((String) out.get("unit"));
//				output.setValue((Number) out.get("interval"));
//				
//				metric.setOutput(output);
//
//			}
//			
//			res.add(metric);
//		}
//	
//		return res;
//
//	}
//	
//	@GetMapping("/get/metrics")
//	public List<String> getMetrics(@RequestParam("slaName") String slaName){
//		List<String> result = new ArrayList<String>(ontology.getInstances("Metric and partOf value " + slaName));
//
//		for(int i = 0; i < result.size(); i++) 
//			result.add(i, result.remove(i).replaceFirst("SLA_[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}_", ""));
//		
//		result.add("violationThreshold");
//		result.add("settlementCount");
//		result.add("evaluationPeriod");
//
//		return result;
//	}
//
//	
//	@GetMapping("/query/sla")
//	public SLA getSLA(@RequestParam("slaName") String slaName) throws Exception {
//		String slaFragment;
////		System.out.println("Get SLA");
//		if(slaName.contains(":"))
//			slaFragment = slaName.split(":")[1];
//		else
//			slaFragment = slaName;
//		
//		SLA sla;
////		System.out.println(slaFragment);
//		int x = ontology.countInstances(encode("{" + slaFragment + "}"));
////		System.out.println(x);
//		if(x != 1)
//			return null;
//		
////		System.out.println("SLA exists");
//		
//		sla = new SLA(slaFragment);
//
//		List<String> slUris = ontology.getInstances(encode("inverse serviceLevel value " + slaFragment));
////		System.out.println(slUris);
//		for(String slURI : slUris) {
//			final String slName = slURI.split("SLA_\\d+_")[1];
//			SL sl = new SL();
//			sl.setSlName(slName);
//			sla.getSls().add(sl);
//			
//			List<String> sloUris = ontology.getInstances(encode("inverse owlqConstraint value " + slURI));
//			
////			System.out.println("inverse constraint value " + slURI);
////			System.out.println(sloUris);
//			for(String sloName : sloUris) {
////				System.out.print("SLO name: " + sloName);
////				System.out.println("\tSL name: " + slName);
//				SLO slo = new SLO();
//					
//				ontology.getSimpleConstraint(slo, sloName);
//				
//				String fullSloName = slo.getFirstArgument();
//				slo.setFirstArgument(slo.getFirstArgument().replaceFirst("(SLA_\\d+_)", ""));
//				slo.setSloName(sloName);
////				System.out.println(slo);
//				
////				slo.setSloType(ontology.getSuperclasses(encode("{" + sloName + "}"), true).get(0).charAt(0));	
//				 
////				if(slo.getSloType() == 'R') {
////					SLTransition trans = new SLTransition();
////					String tName = "SL_TRANSITION_" + sloName;
////
////					if(ontology.countInstances(URLEncoder.encode("inverse secondSL value " + tName, StandardCharsets.UTF_8)) != 0 ) {
////					
////						trans.setSl(ontology.getInstances(URLEncoder.encode("inverse secondSL value " + tName, StandardCharsets.UTF_8)).get(0));
////						trans.setViolationThreshold((int) ontology.getDataProperty("neb:" + tName, "owlq:violationThreshold").get(0));
////						trans.setEvaluationPeriod((String) ontology.getDataProperty("neb:" + tName, "owlq:evaluationPeriod").get(0));
////							
////						slo.setTransition(trans);
////					}
////				}
////				System.out.println(slName + " " + slo);
//				sl.addOperand(slo);
//				
//				Metric m = new Metric();
//				m.setName(slo.getFirstArgument());
//				
//				m.setWindow(getWindowOutput(slaName, m, "WINDOW"));
//				m.setOutput(getWindowOutput(slaName, m, "OUTPUT"));
//				
//				List<Object> formula;
//				
//				try{
//					formula = ontology.getDataProperty(fullSloName, "neb:formula");
//				}catch(Exception e) {
//					formula = null;
//				}
//				
//				if(formula == null) {
//					Sensor s = new Sensor();
//					
//					List<Object> t, a;
//					try {
//						t = ontology.getDataProperty(slo.getFirstArgument(), "neb:type");
//						s.setType((String) t.get(0));
//					}catch(Exception e){
//						
//					}
//									
//					try{
//						a = ontology.getDataProperty(slo.getFirstArgument(), "neb:affinity");
//						s.setAffinity((String) a.get(0));	
//					}catch(Exception e) {
//						
//					}
//					RawMetric rm = new RawMetric(m);
//					rm.setSensor(s);
//					
//					 m = rm;
//					
//				}else if(formula.size() == 1){
//					CompositeMetric cm = new CompositeMetric(m);
//					cm.setFormula(((String) formula.get(0)).split("\"")[1]);
//					m = cm;
//				}
//				
////				m.setMaximizing((boolean) ontology.getDataProperty(fullSloName, "neb:isMaximizing").get(0));
//
//				sla.getMetrics().add(m);
//				
//	//			slo.setNegotiable((boolean) ontology.getDataProperty(sloName, "owlq:negotiable").get(0));
//	//			slo.setSoft((boolean) ontology.getDataProperty(sloName, "owlq:soft").get(0));
//				
//	//			if(ontology.countInstances("%7B" + sloName + "_QC%7D") == 1) {
//	//			
//	//				SimpleConstraint qc = new SimpleConstraint();
//	//				ontology.getSimpleConstraint(qc, sloName + "_QC");
//	//				slo.setQualifyingCondition(qc);
//	//			}
//				slo.setSettlementPricePercentage((double) ontology.getDataProperty(sloName + "_PN_C", "owlq:settlementPricePercentage").get(0));
//			}
//		}
//		ontology.getInstances(encode("inverse slTransition value " + slaFragment)).forEach(slTransName -> {
//
//			SLTransition transition = new SLTransition();
////			System.out.println("slTransName:" + slTransName);
//						
//			transition.setEvaluationPeriod((String) ontology.getDataProperty("neb:" + slTransName, "owlq:evaluationPeriod").get(0));
//			transition.setViolationThreshold((int) ontology.getDataProperty("neb:" + slTransName, "owlq:violationThreshold").get(0));
//			transition.setFirstSl(ontology.getInstances(encode("inverse firstSL value " + slTransName)).get(0).split("SLA_\\d+_SL_")[1]);
//			transition.setSecondSl(ontology.getInstances(encode("inverse secondSL value " + slTransName)).get(0).split("SLA_\\d+_SL_")[1]);
////			System.out.println(transition);
////			sla.getTransitions().add(transition);
//		});	
//		
////    	System.out.println(sla);
//
//		ontology.getInstances(encode("inverse settlement value " + slaFragment)).forEach(settlementName -> {
//			Settlement s = new Settlement();
//			s.setConcernedSL(Integer.parseInt(ontology.getInstances(encode("inverse concernedSL value " + slaFragment)).get(0).split("_")[3]));
//			s.setEvaluationPeriod((String) ontology.getDataProperty("neb:" + settlementName, "owlq:evaluationPeriod").get(0));
//			s.setSettlementCount((int) ontology.getDataProperty("neb:" + settlementName, "owlq:settlementCount").get(0));
//
////			sla.setSettlement(s);
//		});
//		
//		
//		return sla;	
//	}
//		
//	private WindowOutput getWindowOutput(String slaName, Metric m, String type) {
//		if(ontology.countInstances(encode("{" + slaName + "_" + m.getName() + "_" + type + "}")) != 1)
//			return null;
//		
//		WindowOutput window = new WindowOutput();
//		
//		List<Object> t = ontology.getDataProperty(slaName + "_" + m.getName() + "_" + type, "neb:type");
//		List<String> u = ontology.getInstances(encode("inverse unit value " + slaName + "_" + m.getName() + "_" + type));
//		List<Object> v = ontology.getDataProperty(slaName + "_" + m.getName() + "_" + type, "neb:value");
//		
////		System.out.println("slaName + \"_\" + m.getName() + \"_\" + type: " + slaName + "_" + m.getName() + "_" + type + "\n" +t + " " + u + " " + v);
//		
//		if(t.size() == 1)
//			window.setType(((String) t.get(0)).split("\"")[1]);
//		if(u.size() == 1)
//			window.setUnit(u.get(0));
//		if(v.size() == 1)
//			window.setValue((Number) v.get(0));
//			
//		return window;
//	}
//	
//	private String encode(String query) {
//		return URLEncoder.encode(query, StandardCharsets.UTF_8);
//	}
//}
