package org.seerc.nebulous.sla.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seerc.nebulous.sla.components.SLA;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.nebulouscloud.exn.Connector;
import eu.nebulouscloud.exn.core.Context;
import eu.nebulouscloud.exn.core.Publisher;
import eu.nebulouscloud.exn.core.SyncedPublisher;
import eu.nebulouscloud.exn.handlers.ConnectorHandler;
import eu.nebulouscloud.exn.settings.StaticExnConfig;

public class EXNConnection {
	
	private static EXNConnection singleton = null;
	private static ConnectorHandler h;
	private static Connector conn;
	private static SyncedPublisher getFromUi;
	private static Publisher postToSla;
	
	private EXNConnection() {
		
	
		h = new ConnectorHandler() {
			@Override
			public void onReady(Context context) {
				super.onReady(context);
//				context.getPublisher("eu-app-get-publisher").send(Map.of("appId", "e2f237e2-82a5-472b-862f-8017800bd404"));
				
			}
		};

		getFromUi = new SyncedPublisher("eu-app-get-publisher", "eu.nebulouscloud.ui.app.get", true, true);
		postToSla= new Publisher("eu-ontology-sla-publisher", "eu.nebulouscloud.ontology.sla", true, true);

		conn = new Connector("eu", h , List.of(getFromUi, postToSla), List.of(), new StaticExnConfig("nebulous-activemq",5672,"admin","admin",5));

		conn.start();

	}
	public static EXNConnection getInstance() {
		if(singleton == null)
			singleton = new EXNConnection();
		
		return singleton;
	}
	
	public Map getApp(String appId) {
		return getFromUi.sendSync(Map.of("appId", appId), appId, null, false);
	}
	
	public void publishSLA(SLA sla) {
		Map m = new HashMap(new ObjectMapper().convertValue(sla, new TypeReference<Map<String, Object>>() {}));

		postToSla.send(m, sla.getSlaName(), null, false);
	}
}

