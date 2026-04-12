package com.ward.ward_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Routes the root path to the static index page so Tomcat/Spring both render the same landing screen.
 */
@Controller
public class RootController {

	@GetMapping("/")
	public String root() {
		return "forward:/index.html";
	}
}
