	package org.seerc.nebulous.sla.rest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SLADeleteController {


//	@DeleteMapping("delete/slo")
//	public void deleteSlo(@RequestParam("sloName") String sloName) {
//		ontology.deleteIndividual(sloName);
//		ontology.deleteIndividual(sloName + "_QC");
//		ontology.deleteIndividual(sloName + "_PN");
//		ontology.deleteIndividual(sloName + "_PN_C");
//	}
//
//	@DeleteMapping("delete/sla")
//	public void deleteSla(@RequestParam("slaName") String slaName) {
//		ontology.getInstances("inverse%20constraint%20value%20" + slaName + "_SL").forEach(slo -> deleteSlo(slo));
//		ontology.deleteIndividual(slaName + "_SL");
//		ontology.deleteIndividual(slaName);
//	}
//}

}
