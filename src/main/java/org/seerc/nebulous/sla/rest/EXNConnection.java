package org.seerc.nebulous.sla.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
//		    	System.out.println(body);
		    	try {
			    	String uuid = (String) message.property("application");	

					SLA sla = new SLA(uuid);
					
					if(ontology.countInstances("{SLA_" + sla.getSlaName() + "}") > 0) {
						for(String indName:ontology.getInstances("partOf value SLA_" + sla.getSlaName())) {
							ontology.deleteIndividual(indName);
						}
						ontology.deleteIndividual("SLA_" + sla.getSlaName());
					}
					
					Set<Metric> metrics = new HashSet<Metric>();
					
					List<SL> sls = new ArrayList<SL>();
					List<SLTransition> trans = new ArrayList<SLTransition>();
					Settlement settlement = null;
											
			    	List<Map> slList = new ArrayList<Map>();
			    	
			    	Map sl1Map = (Map) body.get("sloViolations");
			    	slList.add(sl1Map);
			    	
			    	ObjectMapper objectMapper = new ObjectMapper();
			    	slList.addAll(objectMapper.readValue((String) body.get("slCreations"), new TypeReference<List>(){}));
			    	
			    	
			    	for(int i = 0; i < slList.size(); i++) {
			    		Map slMap = (Map) slList.get(i);
			    		int slNumber = i + 1;
			    		if(Constraint.constructConstraint(slMap) instanceof ComplexConstraint sl) {
			    			sls.add(new SL(sl, Integer.toString(slNumber)));

			    			if (slNumber == slList.size()) {
			    				settlement = new Settlement(
			    						"PT" + Integer.toString( Integer.parseInt(((String) slMap.get("evaluationPeriod")).trim())) + "S",
			    						Integer.parseInt(((String) slMap.get("violationThreshold")).trim()),
			    						Integer.toString(slNumber));

			    			}else {		
			    				trans.add(new SLTransition(
				    					Integer.toString(slNumber), 
				    					Integer.toString(slNumber + 1),
			    						"PT" + Integer.toString(Integer.parseInt(((String) slMap.get("evaluationPeriod")).trim())) + "S",
						    			Integer.parseInt(((String) slMap.get("violationThreshold")).trim())));
			    			}
			    		}
			    		
			    	}	
					
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
	
					sla.setSls(sls);
					sla.setTransitions(trans);
					sla.setSettlement(settlement);
					sla.setMetrics(metrics);
					
					
					sla.createCompleteSla();
					boolean valid = ontology.validate(uuid);
					
					if(valid)
						publishSLA(sla);
						
				} catch (ClientException | JsonProcessingException  e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 

		    }
		};
		
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
	
	
	public void publishSLA(SLA sla) {
		Map m = new HashMap(new ObjectMapper().convertValue(sla, new TypeReference<Map<String, Object>>() {}));
		postToSla.send(m, sla.getSlaName(), null, false);
	}
}

