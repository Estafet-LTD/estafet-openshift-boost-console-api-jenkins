package com.estafet.openshift.boost.console.api.jenkins.dto;

public enum State {

	FAILED("Failed"), COMPLETE("Complete"), RUNNING("Running");

	private String value;

	State(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
