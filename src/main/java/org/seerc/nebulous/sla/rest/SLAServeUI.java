package org.seerc.nebulous.sla.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SLAServeUI {
	@GetMapping({"/index", "/"})
	public String greeting(@RequestParam(name="appId", required = true) String applicationId, @RequestParam(name="nonce", required = true) String nonce ) {
		System.out.println(applicationId);
		System.out.println(nonce);
		return "index";
	}
	
	
}
