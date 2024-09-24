package org.seerc.nebulous.sla;

import org.seerc.nebulous.sla.rest.OntologyConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SLAGenerator {
	public static void main(String[] args) {
		
		OntologyConnection.getInstance(args[0]);


		SpringApplication.run(SLAGenerator.class, args);
	
	}
	
}