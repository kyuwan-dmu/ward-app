package com.ward.ward_app.service;

import com.ward.ward_app.dao.RestaurantDAO;
import com.ward.ward_app.dto.MessageRequestDTO;
import com.ward.ward_app.vo.HomeVO;
import com.ward.ward_app.vo.MessageVO;
import com.ward.ward_app.vo.RestaurantVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeService {

	private final RestaurantDAO restaurantDAO;

	public HomeService(RestaurantDAO restaurantDAO) {
		this.restaurantDAO = restaurantDAO;
	}

	public HomeVO getHomeData() {
		return new HomeVO(
			"WARD",
			"Neighborhood food recommendations",
			"Welcome, Gangaji!",
			"Guil Station",
			"Cloudy",
			"22.6C",
			"Good",
			"20.3C"
		);
	}

	public List<RestaurantVO> getRestaurants() {
		return restaurantDAO.findAll();
	}

	public MessageVO getHelloMessage() {
		return new MessageVO(
			"GET",
			"sample endpoint",
			"WARD mock service is running."
		);
	}

	public MessageVO createMessage(MessageRequestDTO requestDTO) {
		String sender = requestDTO.getSender() == null || requestDTO.getSender().isBlank()
			? "anonymous"
			: requestDTO.getSender();

		String content = requestDTO.getContent() == null || requestDTO.getContent().isBlank()
			? "empty message"
			: requestDTO.getContent();

		return new MessageVO(
			"POST",
			sender,
			content
		);
	}
}
