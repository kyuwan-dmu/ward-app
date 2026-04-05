package com.ward.ward_app.vo;

public class MessageVO {

	private final String method;
	private final String sender;
	private final String content;

	public MessageVO(String method, String sender, String content) {
		this.method = method;
		this.sender = sender;
		this.content = content;
	}

	public String getMethod() {
		return method;
	}

	public String getSender() {
		return sender;
	}

	public String getContent() {
		return content;
	}
}
