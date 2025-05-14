package org.seerc.nebulous.sla;


import org.seerc.nebulous.sla.rest.OntologyConnection;
import org.seerc.nebulous.sla.rest.SALConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SLAGenerator {
	
	
	public static void main(String[] args) {
//		SALConnection.getInstance(args[1]);
		OntologyConnection.getInstance("nebulous-ontology-server:80");
		SpringApplication.run(SLAGenerator.class, args);
	}
	
}
