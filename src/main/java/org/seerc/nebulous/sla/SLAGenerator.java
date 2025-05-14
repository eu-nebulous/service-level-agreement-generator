package org.seerc.nebulous.sla;


import org.seerc.nebulous.sla.rest.OntologyConnection;
import org.seerc.nebulous.sla.rest.SALConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SLAGenerator {
	
	
	public static void main(String[] args) {

		for(int i = 0; i < args.length; i++) {
			System.out.println("args: " + args[i]);
		}
//		SALConnection.getInstance(args[1]);
		OntologyConnection.getInstance(args[0]);
		SpringApplication.run(SLAGenerator.class, args);
	}
	
}
