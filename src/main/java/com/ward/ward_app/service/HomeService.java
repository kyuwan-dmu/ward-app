package com.ward.ward_app.service;

import com.ward.ward_app.vo.HomeVO;
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
}
