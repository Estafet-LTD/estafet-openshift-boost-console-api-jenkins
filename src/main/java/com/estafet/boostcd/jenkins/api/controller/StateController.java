package com.estafet.boostcd.jenkins.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.estafet.boostcd.commons.model.API;
import com.estafet.boostcd.jenkins.api.dto.EnvState;
import com.estafet.boostcd.jenkins.api.service.StateService;

@RestController
public class StateController {

	@Autowired
	private StateService stateService;
	
	@Value("${app.version}")
	private String appVersion;
	
	@GetMapping("/api")
	public API getAPI() {
		return new API(appVersion);
	}
	
	@GetMapping("/states/{product}")
	public List<EnvState> getStates(@PathVariable String product) {
		return stateService.getStates(product);
	}
	
	@GetMapping("/state/{product}/{env}")
	public EnvState getState(@PathVariable String product, @PathVariable String env) {
		return stateService.getState(product, env);
	}

}
