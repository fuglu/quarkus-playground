package com.fuglu;

public class Mail {
	private String from;
	private String to;
	private String subject;
	private String body;



	public Mail() {
	}

	public Mail(String from, String to, String subject, String body) {
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.body = body;
	}

	public String getFrom() {
		return this.from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return this.to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Mail from(String from) {
		this.from = from;
		return this;
	}

	public Mail to(String to) {
		this.to = to;
		return this;
	}

	public Mail subject(String subject) {
		this.subject = subject;
		return this;
	}

	public Mail body(String body) {
		this.body = body;
		return this;
	}


}
