package com.ward.ward_app.controller;

import com.ward.ward_app.dto.MessageRequestDTO;
import com.ward.ward_app.service.HomeService;
import com.ward.ward_app.vo.HomeVO;
import com.ward.ward_app.vo.MessageVO;
import com.ward.ward_app.vo.RestaurantVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

	@GetMapping("/restaurants")
	public List<RestaurantVO> restaurants() {
		return homeService.getRestaurants();
	}

	@GetMapping("/hello")
	public MessageVO hello() {
		return homeService.getHelloMessage();
	}

	@PostMapping("/messages")
	public MessageVO createMessage(@RequestBody MessageRequestDTO requestDTO) {
		return homeService.createMessage(requestDTO);
	}
}
