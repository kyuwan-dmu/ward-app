package com.ward.ward_app.service;

import com.ward.ward_app.dto.MessageRequestDTO;
import com.ward.ward_app.vo.HomeVO;
import com.ward.ward_app.vo.MessageVO;
import com.ward.ward_app.vo.RestaurantVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeService {

	public HomeVO getHomeData() {
		return new HomeVO(
			"WARD",
			"동네 기반 음식 추천",
			"강아지 님 환영합니다!",
			"구일역",
			"흐림",
			"22.6°",
			"좋음",
			"20.3°"
		);
	}

	public List<RestaurantVO> getRestaurants() {
		return List.of(
			new RestaurantVO(
				"고척칼국수",
				"칼국수, 만두",
				"비 오는 날 가볍게 들르기 좋은 따뜻한 국물 메뉴 중심의 식당입니다.",
				"서울 구로구 고척동 76-173",
				37.5009,
				126.8648,
				4.0
			),
			new RestaurantVO(
				"구일분식",
				"분식, 김밥",
				"학생 예제용으로 넣은 가벼운 한 끼 메뉴 구성입니다.",
				"서울 구로구 고척동 63-3",
				37.4969,
				126.8690,
				4.0
			),
			new RestaurantVO(
				"고척돈가스",
				"돈가스, 우동",
				"구일역 근처 직장인 점심 메뉴를 가정한 목업 데이터입니다.",
				"서울 구로구 경인로 43길 49",
				37.4976,
				126.8674,
				4.0
			),
			new RestaurantVO(
				"안양천포차",
				"한식, 주점",
				"지도 마커와 목록 연계를 설명하기 위한 샘플 매장입니다.",
				"서울 구로구 고척동 66-41",
				37.4987,
				126.8711,
				4.0
			)
		);
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
