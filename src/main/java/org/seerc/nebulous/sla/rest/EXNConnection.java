package org.seerc.nebulous.sla.rest;

import java.util.List;
import java.util.Map;


import eu.nebulouscloud.exn.Connector;
import eu.nebulouscloud.exn.core.Context;

import eu.nebulouscloud.exn.core.SyncedPublisher;
import eu.nebulouscloud.exn.handlers.ConnectorHandler;
import eu.nebulouscloud.exn.settings.StaticExnConfig;

public class EXNConnection {
	
	private static EXNConnection singleton = null;
	private static ConnectorHandler h;
	private static Connector conn;
	private static SyncedPublisher p;
	
	private EXNConnection() {
		
	
		h = new ConnectorHandler() {
			@Override
			public void onReady(Context context) {
				super.onReady(context);
//				context.getPublisher("eu-app-get-publisher").send(Map.of("appId", "e2f237e2-82a5-472b-862f-8017800bd404"));
				
			}
		};

		p = new SyncedPublisher("eu-app-get-publisher", "eu.nebulouscloud.ui.app.get", true, true);
		conn = new Connector("eu", h , List.of(p), List.of(), new StaticExnConfig("localhost",5672,"admin","admin",5));

		conn.start();

	}
	public static EXNConnection getInstance() {
		if(singleton == null)
			singleton = new EXNConnection();
		
		return singleton;
	}
	
	public Map getApp(String appId) {
			
		return p.sendSync(Map.of("appId", appId), appId, null, false);
	}
}

