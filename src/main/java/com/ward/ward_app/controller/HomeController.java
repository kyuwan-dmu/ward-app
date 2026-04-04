package com.ward.ward_app.controller;

import com.ward.ward_app.service.HomeService;
import com.ward.ward_app.vo.HomeVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

	private final HomeService homeService;

	public HomeController(HomeService homeService) {
		this.homeService = homeService;
	}

	@GetMapping("/info")
	public HomeVO info() {
		return homeService.getHomeData();
	}
}
