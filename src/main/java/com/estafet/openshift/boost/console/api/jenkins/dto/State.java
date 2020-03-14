package com.estafet.openshift.boost.console.api.jenkins.dto;

public enum State {

	NEW("New"), FAILED("Failed"), COMPLETE("Complete"), RUNNING("Running"), PENDING("Pending");

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
	
	public static State fromString(String value) {
        for (State state : State.values()) {
            if (state.value.equalsIgnoreCase(value)) {
                return state;
            }
        }
        throw new RuntimeException("Cannot match enum for - " + value);
    }

}
