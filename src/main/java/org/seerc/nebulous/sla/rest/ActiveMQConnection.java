package org.seerc.nebulous.sla.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.qpid.protonj2.client.Message;

import eu.nebulouscloud.exn.Connector;
import eu.nebulouscloud.exn.core.Context;
import eu.nebulouscloud.exn.core.Handler;
import eu.nebulouscloud.exn.core.Publisher;
import eu.nebulouscloud.exn.handlers.ConnectorHandler;
import eu.nebulouscloud.exn.settings.StaticExnConfig;

public class ActiveMQConnection {
	private static ActiveMQConnection clusterConnection;
	private static List<Publisher> publishers;
	private static ConnectorHandler connectorHandler;
	private static Connector connector;
	private static Context context;
	
	private ActiveMQConnection() {
		publishers = new ArrayList<Publisher>();
		
		
		connectorHandler = new ConnectorHandler() {
			@Override
			public void onReady(Context cont) {
				super.onReady(cont);
				context = cont;
				System.out.println("\nReady to transmit to ActiveMQ\n");				
			}
		};


		publishers.add(new Publisher("slas", "slas", false));
		
		connector = new Connector("sla", connectorHandler , publishers, null, new StaticExnConfig("localhost",5672,"admin","admin",5));

		connector.start();

//		Handler a = new Handler() {
//		@Override
//		public void onMessage(String key, String address, Map body, Message message, Context context) {
//			super.onMessage(key, address, body, message, context);
//			System.out.println(body);
//		}
//	};
//	Consumer c = new Consumer("slas", "slas", a, false);
	}
	
	public static ActiveMQConnection getInstance() {
		if(clusterConnection == null)
			clusterConnection = new ActiveMQConnection();
		return clusterConnection;
	}

	public void publish(String publisherKey, Map message) {
		context.getPublisher(publisherKey).send(message);
	}
	public List<Publisher> getPublishers() {
		return publishers;
	}

	public ConnectorHandler getConnectorHandler() {
		return connectorHandler;
	}

	public Connector getConnector() {
		return connector;
	}

	public Context getContext() {
		return context;
	}
	
	
}
