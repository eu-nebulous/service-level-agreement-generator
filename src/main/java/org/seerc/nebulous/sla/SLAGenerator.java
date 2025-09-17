package org.seerc.nebulous.sla;


import org.seerc.nebulous.sla.rest.OntologyConnection;
import org.seerc.nebulous.sla.rest.SALConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SLAGenerator {
	
	
	public static void main(String[] args) {
//		SALConnection.getInstance(args[1]);
		String url = "http://nebulous-ontology-server:80";
		OntologyConnection.getInstance(url);

		SpringApplication.run(SLAGenerator.class, args);
	}
	
}
