package com.amazonaws.lambda.dynamodb_table_writer;

public class Request {

	public String question;

	public Request() {
		super();
	}

	public Request(String question) {
		super();
		this.question = question;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}
	
}
