package org.seerc.nebulous.sla.rest;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

import org.seerc.nebulous.sla.components.DeviceSLA;
public class SALConnection {
	private static SALConnection singleton = null;
	private static String sessionid  = null;
	
	private WebClient client;
//	private JmsTemplate jmsTemplate;

	private SALConnection( String host) {
		client = WebClient.create(host); //localhost:80
		sessionid = connect();
	}
	
	public static SALConnection getInstance( String host) {
		if(singleton == null)
			singleton = new SALConnection(host);
		
		return singleton;
	}
	
	public static SALConnection getInstance() {
		return singleton;
	}
	private String connect() {
		return client.post().uri("/sal/pagateway/connect")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(new  SALConnectPostBody("cd", "5rf0A@sj01=")))
			    .retrieve().bodyToMono(String.class).block();

	}
	public List<DeviceSLA> getDevicesFromJob(int jobId) {
		List<DeviceSLA> result;
		
		try {
			result = Arrays.asList(client.get().uri("/sal/edge/" + jobId)
					.header("sessionid", sessionid)
					.retrieve().bodyToMono(DeviceSLA[].class).block());

		} catch (Exception e) {
			sessionid = connect();
			result = Arrays.asList(client.get().uri("/sal/edge/" + jobId)
					.header("sessionid", sessionid)
					.retrieve().bodyToMono(DeviceSLA[].class).block());

		}
		
		return result;

	}
}
