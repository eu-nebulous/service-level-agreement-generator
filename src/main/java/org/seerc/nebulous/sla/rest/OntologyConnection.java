package org.seerc.nebulous.sla.rest;

import java.net.URI;
import java.time.Instant;

import java.util.Arrays;
import java.util.List;

import org.seerc.nebulous.sla.components.ComparisonOperator;
import org.seerc.nebulous.sla.components.SimpleConstraint;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class OntologyConnection{

	private static OntologyConnection singleton = null;
	
	private WebClient client;
//	private JmsTemplate jmsTemplate;

	private OntologyConnection( String host) {
		client = WebClient.create(host); //localhost:80
//		this.jmsTemplate = jmsTemplate;
//		jmsTemplate.setPubSubDomain(true);

	}
	
	public static OntologyConnection getInstance( String host) {
		if(singleton == null)
			singleton = new OntologyConnection(host);
		
		return singleton;
	}
	
	public static OntologyConnection getInstance() {
		return singleton;
	}

//	public void convertAndSend(String address, Object obj) {
//		jmsTemplate.convertAndSend(address, obj);
//
//	}
	public void getSimpleConstraint(SimpleConstraint constraint, String constraintName) {
		constraint.setFirstArgument(getInstances("inverse%20firstArgument%20value%20" + constraintName).get(0));
		Object secArg = getDataProperty(constraintName, "owlq:secondArgument").get(0);
		if(secArg instanceof String)
			constraint.setSecondArgument(((String) secArg).split("\"")[1]);
		else
			constraint.setSecondArgument(secArg);
		constraint.setOperator(ComparisonOperator.valueOf((String) getInstances("inverse%20operator%20value%20" + constraintName).get(0)));
	}
	public String createIndividual(String individualURI, String classURI) {
		return client.post().uri("/create/individual")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(new CreateIndividualPostBody (individualURI, classURI)))
			    .retrieve().bodyToMono(String.class).block();
	}
	public String createObjectProperty(String objectPropertyURI, String domainURI, String rangeURI) {
		return client.post().uri("/create/objectProperty")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(new CreateObjectPropertyPostBody(objectPropertyURI, domainURI, rangeURI)))
			    .retrieve().bodyToMono(String.class).block();
	}
	
	public String createDataProperty(String dataPropertyURI, String domainURI, String value, String type) {
		return client.post().uri("/create/dataProperty")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(new CreateDataPropertyPostBody(dataPropertyURI, domainURI, value, type)))

			    .retrieve().bodyToMono(String.class).block();
	}
	
	public int countInstances(String dlQuery) {
		return client.get().uri("/countInstances?dlQuery=" + dlQuery)
					.retrieve().bodyToMono(Integer.class).block();
	}
	
	public List<String> getInstances(String dlQuery) {
		return Arrays.asList(client.get().uri("/get/instances?dlQuery=" + dlQuery)
				.retrieve().bodyToMono(String[].class).block());
	}
	
	public List<Object> getDataProperty(String individualName, String dataProperty) {
		return Arrays.asList(client.get().uri("/get/dataProperty?individualName=" + individualName + "&dataProperty=" + dataProperty)
				.retrieve().bodyToMono(Object[].class).block());
	}
	
	public void deleteIndividual(String individualName) {
		client.delete().uri(URI.create("/delete/individual?individualName=" + individualName))
			.accept(MediaType.APPLICATION_JSON)
		    .retrieve().bodyToMono(String.class).block();
	}
	
	public List<String> getSuperclasses(String dlQuery, boolean direct){
		return Arrays.asList(client.get().uri("/get/superclasses?dlQuery=" + dlQuery + "&direct=" + direct)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve().bodyToMono(String[].class).block());	
	}
	public String registerAsset(String assetName) {
		return client.post().uri("/register/asset")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(new RegisterAssetBodyInserter(assetName, Instant.now().getEpochSecond())))
			    .retrieve().bodyToMono(String.class).block();
	}
	public int countAssets(){
		return Arrays.asList(client.get().uri("/count/assets")
			.accept(MediaType.APPLICATION_JSON)
			.retrieve().bodyToMono(Integer.class).block()).get(0);	
	}

	
}
