package com.estafet.boostcd.jenkins.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.estafet.boostcd.jenkins.api.model.Env;
import com.estafet.boostcd.jenkins.api.model.Microservice;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.openshift.restclient.model.IBuild;

@JsonInclude(Include.NON_NULL)
public class EnvState {

	private static final Logger log = LoggerFactory.getLogger(EnvState.class);
	
	private String name;

	private State build;

	private State promote;

	private State test;

	private State goLive;

	private State backOut;

	private List<AppState> apps = new ArrayList<AppState>();

	public void addMicroservice(Microservice microservice, Map<String, IBuild> builds) {
		apps.add(AppState.builder().setMicroservice(microservice).setBuilds(builds).build());
	}
	
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

	public State getTest() {
		return test;
	}

	public void setTest(State test) {
		this.test = test;
	}

	public State getGoLive() {
		return goLive;
	}

	public void setGoLive(State goLive) {
		this.goLive = goLive;
	}

	public State getBackOut() {
		return backOut;
	}

	public void setBackOut(State backOut) {
		this.backOut = backOut;
	}

	public List<AppState> getApps() {
		return apps;
	}

	public void setApps(List<AppState> apps) {
		this.apps = apps;
	}
	
	public static EnvStateBuilder builder() {
		return new EnvStateBuilder();
	}
	
	public static class EnvStateBuilder {
		
		private Env env;
		private Map<String, IBuild> builds;
		
		public EnvStateBuilder setEnv(Env env) {
			this.env = env;
			return this;
		}
		
		public EnvStateBuilder setBuilds(Map<String, IBuild> builds) {
			this.builds = builds;
			return this;
		}
		
		public EnvState build() {
			log.info(env.toString());
			EnvState envState = new EnvState();
			envState.setName(env.getName());
			if (env.getName().equals("build")) {
				envState.setBuild(getBuildState(env.buildBuildName()));
				envState.setPromote(getBuildState(env.promoteBuildName()));
			} else if (env.getName().equals("green") || env.getName().equals("blue")) {
				envState.setTest(getBuildState(env.testBuildName()));
				envState.setBackOut(getBuildState(env.backOutBuildName()));
				envState.setGoLive(getBuildState(env.goLiveBuildName()));
			} else {
				envState.setTest(getBuildState(env.testBuildName()));
				envState.setPromote(getBuildState(env.promoteBuildName()));
			}
			for (Microservice microservice : env.getMicroservices()) {
				envState.addMicroservice(microservice, builds);
			}
			return envState;
		}
		
		private State getBuildState(String buildName) {
			if (buildName != null) {
				IBuild build = builds.get(buildName);
				return build != null ? State.fromString(build.getBuildStatus().getPhase()) : null;	
			} else {
				return null;
			}
		}
		
	}

}
