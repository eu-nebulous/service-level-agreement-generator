package org.seerc.nebulous.sla;


import org.seerc.nebulous.sla.rest.EXNConnection;
import org.seerc.nebulous.sla.rest.OntologyConnection;
import org.seerc.nebulous.sla.rest.SALConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SLAGenerator {
	
	
	public static void main(String[] args) {
//		SALConnection.getInstance(args[1]);
		String ontUrl = "http://nebulous-ontology-server:80";
		String bqaUrl = "http://nebulous-brokerage-quality-assurance-server :8080";
		OntologyConnection.getInstance(ontUrl, bqaUrl);
		EXNConnection.getInstance();
		SpringApplication.run(SLAGenerator.class, args);
	}
	
}
