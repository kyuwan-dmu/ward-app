package com.ward.ward_app.vo;

public class RestaurantVO {

	private final String name;
	private final String category;
	private final String summary;
	private final String address;
	private final double latitude;
	private final double longitude;
	private final double rating;

	public RestaurantVO(
		String name,
		String category,
		String summary,
		String address,
		double latitude,
		double longitude,
		double rating
	) {
		this.name = name;
		this.category = category;
		this.summary = summary;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.rating = rating;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public String getSummary() {
		return summary;
	}

	public String getAddress() {
		return address;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getRating() {
		return rating;
	}
}
