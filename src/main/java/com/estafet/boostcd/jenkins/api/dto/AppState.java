package com.estafet.boostcd.jenkins.api.dto;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.estafet.boostcd.jenkins.api.model.Microservice;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.openshift.restclient.model.IBuild;

@JsonInclude(Include.NON_NULL)
public class AppState {

	private static final Logger log = LoggerFactory.getLogger(EnvState.class);
	
	private String name;
	
	private State build;

	private State promote;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
			log.info(microservice.toString());
			AppState appState = new AppState();
			appState.setName(microservice.getMicroservice());
			appState.setBuild(getBuildState(microservice.buildBuildName()));
			appState.setPromote(getBuildState(microservice.promoteBuildName()));
			return appState;
		}
		
		private State getBuildState(String buildName) {
			log.info("buildName - " + buildName);
			if (buildName != null) {
				IBuild build = builds.get(buildName);
				return build != null ? State.fromString(build.getBuildStatus().getPhase()) : null;		
			} else {
				return null;
			}
		}
		
	}

}
