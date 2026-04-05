package com.ward.ward_app.service;

import com.ward.ward_app.dto.MessageRequestDTO;
import com.ward.ward_app.vo.HomeVO;
import com.ward.ward_app.vo.MessageVO;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

	public HomeVO getHomeData() {
		return new HomeVO(
			"Ward App",
			"Spring Boot REST API practice project",
			"Controller -> Service -> VO(JSON Response)",
			"Local: embedded Tomcat / External: Apache Tomcat WAR deployment"
		);
	}

	public MessageVO getHelloMessage() {
		return new MessageVO(
			"GET",
			"basic get api response",
			"Hello from ward-app."
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
