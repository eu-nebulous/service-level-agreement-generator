package org.seerc.nebulous.sla.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SLAServeUI {
	@GetMapping({"/index", "/"})
	public String greeting(@RequestParam(name="appId", required = true) String appId ) {
		return "index";
	}

	@GetMapping({"/test"})
	public String test() {
		return "connected";
	}
}
