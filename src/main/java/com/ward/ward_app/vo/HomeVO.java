package com.ward.ward_app.vo;

public class HomeVO {

	private final String title;
	private final String description;
	private final String architecture;
	private final String runtime;

	public HomeVO(String title, String description, String architecture, String runtime) {
		this.title = title;
		this.description = description;
		this.architecture = architecture;
		this.runtime = runtime;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getArchitecture() {
		return architecture;
	}

	public String getRuntime() {
		return runtime;
	}
}
