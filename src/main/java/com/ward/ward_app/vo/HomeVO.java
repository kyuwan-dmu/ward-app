package com.ward.ward_app.vo;

public class HomeVO {

	private final String serviceName;
	private final String subtitle;
	private final String welcomeMessage;
	private final String location;
	private final String weatherStatus;
	private final String temperature;
	private final String dustStatus;
	private final String feelsLike;

	public HomeVO(
		String serviceName,
		String subtitle,
		String welcomeMessage,
		String location,
		String weatherStatus,
		String temperature,
		String dustStatus,
		String feelsLike
	) {
		this.serviceName = serviceName;
		this.subtitle = subtitle;
		this.welcomeMessage = welcomeMessage;
		this.location = location;
		this.weatherStatus = weatherStatus;
		this.temperature = temperature;
		this.dustStatus = dustStatus;
		this.feelsLike = feelsLike;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	public String getLocation() {
		return location;
	}

	public String getWeatherStatus() {
		return weatherStatus;
	}

	public String getTemperature() {
		return temperature;
	}

	public String getDustStatus() {
		return dustStatus;
	}

	public String getFeelsLike() {
		return feelsLike;
	}
}
