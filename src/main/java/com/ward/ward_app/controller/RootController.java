package com.ward.ward_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple root endpoint so hitting http://localhost:8080/ confirms the app is running.
 */
@RestController
public class RootController {

	@GetMapping("/")
	public String root() {
		return "ward-app is running. Try /api/info for details.";
	}
}
