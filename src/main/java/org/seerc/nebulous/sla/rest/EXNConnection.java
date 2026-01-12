package org.seerc.nebulous.sla.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.qpid.protonj2.client.Message;
import org.apache.qpid.protonj2.client.exceptions.ClientException;
import org.seerc.nebulous.sla.components.ComplexConstraint;
import org.seerc.nebulous.sla.components.CompositeMetric;
import org.seerc.nebulous.sla.components.Constraint;
import org.seerc.nebulous.sla.components.Metric;
import org.seerc.nebulous.sla.components.RawMetric;
import org.seerc.nebulous.sla.components.SL;
import org.seerc.nebulous.sla.components.SLA;
import org.seerc.nebulous.sla.components.SLTransition;
import org.seerc.nebulous.sla.components.Settlement;
import org.seerc.nebulous.sla.components.SimpleConstraint;
import org.seerc.nebulous.sla.components.WindowOutput;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.nebulouscloud.exn.Connector;
import eu.nebulouscloud.exn.core.Consumer;
import eu.nebulouscloud.exn.core.Context;
import eu.nebulouscloud.exn.core.Handler;
import eu.nebulouscloud.exn.core.Publisher;
import eu.nebulouscloud.exn.handlers.ConnectorHandler;
import eu.nebulouscloud.exn.settings.StaticExnConfig;

public class EXNConnection {
	
	private static EXNConnection singleton = null;
	private static ConnectorHandler h;
	private static Handler slaHandler;
	private static Connector conn;
//	private static SyncedPublisher getFromUi;
	private static Consumer slaFromUi;
	private static Publisher postToSla;
	private static OntologyConnection ontology;
	private EXNConnection() {
		
	
		h = new ConnectorHandler() {
			@Override
			public void onReady(Context context) {
				super.onReady(context);
//				context.getPublisher("eu-app-get-publisher").send(Map.of("appId", "e2f237e2-82a5-472b-862f-8017800bd404"));
				
			}
		};
		slaHandler = new Handler() {
		    @SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
		    public void onMessage(String key, String address, Map body, Message message, Context context) {

//		    	System.out.println(body.get("sloViolations"));
		    	try {
			    	String uuid = (String) message.property("application");

					SLA sla = new SLA(uuid);
//					System.out.println("SLANAME: " +sla.getSlaName());
//					System.out.println(ontology.countInstances("{SLA_" + sla.getSlaName() + "}"));
					
					if(ontology.countInstances("{SLA_" + sla.getSlaName() + "}") > 0) {
						for(String indName:ontology.getInstances("partOf value SLA_" + sla.getSlaName())) {
							System.out.println(indName);
							ontology.deleteIndividual(indName);
						}
						ontology.deleteIndividual("SLA_" + sla.getSlaName());
					}
					
					Set<Metric> metrics = new HashSet<Metric>();
					
					List<SL> sls = new ArrayList<SL>();
					Constraint sl1Con  = Constraint.constructConstraint(((Map) body.get("sloViolations")));
					
//					ComplexConstraint slCons  = (ComplexConstraint) Constraint.constructConstraint(					
//						new ObjectMapper().readValue(
//						(String) body.get("slCreations"), new TypeReference<Map<String,Object>>(){})
//					);
//					
					int slCounter = 1;

					if(sl1Con instanceof ComplexConstraint c)
						sls.add(new SL(c, Integer.toString(slCounter++)));
					
//					for(Constraint cons : slCons.getOperands()) {
//						if(cons instanceof ComplexConstraint c)
//							sls.add(new SL(c, Integer.toString(slCounter++)));
//						else if (cons instanceof SimpleConstraint c) {
//							SL x = new SL(Integer.toString(slCounter++));
//							x.addOperand(c);
//							x.setOperator("owlq:AND");
//							sls.add(x);
//							
//						}
//					}
						
										
					sla.setSls(sls);
					sla.setMetrics(metrics);
					
					
					
//					System.out.println(sl1);
					WindowOutput win = new WindowOutput();
					WindowOutput output = new WindowOutput();
					
					win.setType("owlq:Window");
					win.setUnit("");
					win.setValue(0);
					
					output.setType("owlq:Window");
					output.setUnit("");
					output.setValue(0);
					for(Map m : (List<Map>) body.get("metrics")) {
						if(m.get("type").equals("raw")) {
							RawMetric rm = new RawMetric((String) m.get("name"));
							rm.setOutput(output);
							rm.setWindow(win);

							metrics.add(rm);
						}else if(m.get("type").equals("composite")) {
							CompositeMetric cm = new CompositeMetric((String) m.get("name"));
							cm.setOutput(output);
							cm.setWindow(win);
							cm.setFormula((String) m.get("formula"));
							metrics.add(cm);
						}
					}
					
					List<SLTransition> trans = new ArrayList<SLTransition>();
					for(int i = 1; i <= sla.getSls().size(); i++){
						if(i == sla.getSls().size()) {
							Settlement stl = new Settlement();
							stl.setConcernedSL(i);
							stl.setEvaluationPeriod("PT1H");
							stl.setSettlementCount(4);
							stl.setSettlementAction("CANCEL");
							sla.setSettlement(stl);
						}else {
							SLTransition tr = new SLTransition();
							tr.setEvaluationPeriod("PT1H");
							tr.setViolationThreshold(4);
							System.out.println(i);
							tr.setFirstSl(Integer.toString(i));
							tr.setFirstSl(Integer.toString(i + 1));
							trans.add(tr);
						}

						sla.setTransitions(trans);
						
					}

					sla.createCompleteSla();
					ontology.validate(uuid);
				} catch (ClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		    	
//				try {
//					String uuid = (String) message.property("application");
//					map = objectMapper.readValue((String) body.get("sloViolations"), new TypeReference<Map<String,Object>>(){});
//			        
////					ontology.createPolicy(p);
//					System.out.println(map);
//
//				} catch (JsonProcessingException | ClientException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
		    	
//		        Policy p = Policy.ConstructPolicy((Map) ((Map) body.get("body")).get("slMetaConstraints"));
		        
//		        String uuid = p.getAsset().split(":")[1];
//		        ontology.createPolicy(p);
//		        boolean valid = ontology.validate(uuid);
//		        
//		        try {
//					bqaVerification.send(Map.of("uuid", uuid, "valid", valid, "message", 
//							valid ? "The application is valid" : "The application is not valid"),
//							uuid, Map.of("correlation-id", message.correlationId().toString()));
//				} catch (ClientException e) {
//					e.printStackTrace();
//				}
		    }
		};
//		getFromUi = new SyncedPublisher("eu-app-get-publisher", "eu.nebulouscloud.ui.app.get", true, true);
		
		String brokerUrl = System.getenv("BROKER_URL");
		if (brokerUrl == null) brokerUrl = "localhost";
		
		String brokerPortStr = System.getenv("BROKER_PORT");
		Integer brokerPort = brokerPortStr != null ? Integer.parseInt(brokerPortStr) : 5672;
		
		String brokerUsername = System.getenv("BROKER_USERNAME");
		if (brokerUsername == null) brokerUsername = "admin";
		
		String brokerPassword = System.getenv("BROKER_PASSWORD");
		if (brokerPassword == null) brokerPassword = "admin";
		
		postToSla= new Publisher("eu-ontology-sla-publisher", "eu.nebulouscloud.ontology.sla", true, true);
		slaFromUi = new Consumer("bqa",        "eu.nebulouscloud.ontology.bqa", slaHandler, true, true);

		System.out.println(String.format("Got connection properties: BROKER_URL: %s, BROKER_PORT: %s BROKER_USERNAME: %s", brokerUrl, brokerPort, brokerUsername));
		conn = new Connector("eu.nebulouscloud", h , List.of(postToSla), List.of(slaFromUi), new StaticExnConfig(brokerUrl,brokerPort,brokerUsername,brokerPassword,5));		
		conn.start();

	}
	public static EXNConnection getInstance() {
		if(singleton == null) {
			ontology = OntologyConnection.getInstance();
			singleton = new EXNConnection();
		}
		return singleton;
	}
	
//	public Map getApp(String appId) {
//		return getFromUi.sendSync(Map.of("appId", appId), appId, null, false);
//	}
	
	public void publishSLA(SLA sla) {
		Map m = new HashMap(new ObjectMapper().convertValue(sla, new TypeReference<Map<String, Object>>() {}));
		postToSla.send(m, sla.getSlaName(), null, false);
	}
}

