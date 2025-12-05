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

		System.out.println("Ontology URL:" + args[0]);
		System.out.println("BQA URL:" + args[1]);
		OntologyConnection.getInstance(args[0], args[1]);
		EXNConnection.getInstance();
		SpringApplication.run(SLAGenerator.class, args);
	}
	
}
