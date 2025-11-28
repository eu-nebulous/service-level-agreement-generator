package org.seerc.nebulous.sla.rest;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.List;


import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class OntologyConnection{

	private static OntologyConnection singleton = null;
	
	private WebClient ontologyClient;
	private WebClient bqaClient;
	private OntologyConnection(String slaHost, String bqaHost) {
		ontologyClient = WebClient.create(slaHost); //localhost:80
		bqaClient = WebClient.create(bqaHost);
		
	}
	
	public static OntologyConnection getInstance(String slaHost, String bqaHost) {
		if(singleton == null)
			singleton = new OntologyConnection(slaHost, bqaHost);
		
		return singleton;
	}
	
	public static OntologyConnection getInstance() {
		return singleton;
	}

//	public void convertAndSend(String address, Object obj) {
//		jmsTemplate.convertAndSend(address, obj);
//
//	}

	public String createIndividual(String individualURI, String classURI) {
		return ontologyClient.post().uri("/create/individual")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(new CreateIndividualPostBody (individualURI, classURI)))
			    .retrieve().bodyToMono(String.class).block();
	}
	public String createObjectProperty(String objectPropertyURI, String domainURI, String rangeURI) {
		return ontologyClient.post().uri("/create/objectProperty")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(new CreateObjectPropertyPostBody(objectPropertyURI, domainURI, rangeURI)))
			    .retrieve().bodyToMono(String.class).block();
	}
	
	public String createDataProperty(String dataPropertyURI, String domainURI, String value, String type) {
		return ontologyClient.post().uri("/create/dataProperty")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(new CreateDataPropertyPostBody(dataPropertyURI, domainURI, value, type)))

			    .retrieve().bodyToMono(String.class).block();
	}
	
	public int countInstances(String dlQuery) {
		return ontologyClient.get().uri("/countInstances?dlQuery=" + encode(dlQuery))
					.retrieve().bodyToMono(Integer.class).block();
	}
	
	public List<String> getInstances(String dlQuery) {
		return Arrays.asList(ontologyClient.get().uri("/get/instances?dlQuery=" + dlQuery)
				.retrieve().bodyToMono(String[].class).block());
	}
	
	public List<Object> getDataProperty(String individualName, String dataProperty) {
		return Arrays.asList(ontologyClient.get().uri("/get/dataProperty?individualName=" + individualName + "&dataProperty=" + dataProperty)
				.retrieve().bodyToMono(Object[].class).block());
	}
	
	public void deleteIndividual(String individualName) {
		ontologyClient.delete().uri(URI.create("/delete/individual?individualName=" + individualName))
			.accept(MediaType.APPLICATION_JSON)
		    .retrieve().bodyToMono(String.class).block();
	}
	
	public List<String> getSuperclasses(String dlQuery, boolean direct){
		return Arrays.asList(ontologyClient.get().uri("/get/superclasses?dlQuery=" + dlQuery + "&direct=" + direct)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve().bodyToMono(String[].class).block());	
	}
	
	public void validate(String uuid) {
		bqaClient.get().uri("/validate?uuid=" + encode(uuid))
		.accept(MediaType.APPLICATION_JSON)
		.retrieve().bodyToMono(String[].class).block();	
	}
	
	private String encode(String query) {
		return URLEncoder.encode(query, StandardCharsets.UTF_8);
	}
//	public String registerAsset(String assetName) {
//		return client.post().uri("/register/asset")
//				.accept(MediaType.APPLICATION_JSON)
//				.contentType(MediaType.APPLICATION_JSON)
//				.body(BodyInserters.fromValue(new RegisterAssetBodyInserter(assetName, Instant.now().getEpochSecond())))
//			    .retrieve().bodyToMono(String.class).block();
//	}
//	public int countAssets(){
//		return Arrays.asList(client.get().uri("/count/assets")
//			.accept(MediaType.APPLICATION_JSON)
//			.retrieve().bodyToMono(Integer.class).block()).get(0);	
//	}

	
}
