package com.estafet.openshift.boost.console.api.jenkins.dto;

import java.util.Map;

import com.estafet.openshift.boost.console.api.jenkins.model.Microservice;
import com.openshift.restclient.model.IBuild;

public class AppState {

	private State build;

	private State promote;

	public State getBuild() {
		return build;
	}

	public void setBuild(State build) {
		this.build = build;
	}

	public State getPromote() {
		return promote;
	}

	public void setPromote(State promote) {
		this.promote = promote;
	}
	
	public static AppStateBuilder builder() {
		return new AppStateBuilder();
	}
	
	public static class AppStateBuilder {
		
		private Microservice microservice;
		private Map<String, IBuild> builds;
		
		public AppStateBuilder setMicroservice(Microservice microservice) {
			this.microservice = microservice;
			return this;
		}
		
		public AppStateBuilder setBuilds(Map<String, IBuild> builds) {
			this.builds = builds;
			return this;
		}
		
		public AppState build() {
			AppState appState = new AppState();
			appState.setBuild(getBuildState(microservice.buildBuildName()));
			appState.setPromote(getBuildState(microservice.promoteBuildName()));
			return appState;
		}
		
		private State getBuildState(String buildName) {
			if (buildName != null) {
				IBuild build = builds.get(buildName);
				return State.valueOf(build.getBuildStatus().toString());	
			} else {
				return null;
			}
		}
		
	}

}