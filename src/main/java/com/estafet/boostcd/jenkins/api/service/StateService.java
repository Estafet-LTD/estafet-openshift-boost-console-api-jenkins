package com.estafet.boostcd.jenkins.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.estafet.boostcd.commons.date.DateUtils;
import com.estafet.boostcd.jenkins.api.dao.EnvDAO;
import com.estafet.boostcd.jenkins.api.dao.ProductDAO;
import com.estafet.boostcd.jenkins.api.dto.EnvState;
import com.estafet.boostcd.jenkins.api.model.Env;
import com.estafet.boostcd.openshift.OpenShiftClient;
import com.openshift.restclient.model.IBuild;

@Service
public class StateService {

	private static final Logger log = LoggerFactory.getLogger(StateService.class);
	private static Pattern pattern = Pattern.compile("(.+)(\\-\\d+)");

	@Autowired
	private OpenShiftClient client;

	@Autowired
	private EnvDAO envDAO;
	
	@Autowired
	private ProductDAO productDAO;

	public List<EnvState> getStates(String productId) {
		Map<String, IBuild> builds = latestBuilds(productId);
		List<EnvState> states = new ArrayList<EnvState>();
		for (Env env : productDAO.getProduct(productId).getEnvs()) {
			states.add(getState(productId, env.getName(), builds));	
		}
		return states;
	}

	public EnvState getState(String productId, String env) {
		return getState(productId, env, latestBuilds(productId));
	}

	private EnvState getState(String productId, String envId, Map<String, IBuild> builds) {
		return EnvState.builder().setEnv(envDAO.getEnv(productId, envId)).setBuilds(builds).build();
	}

	private Map<String, IBuild> latestBuilds(String productId) {
		Map<String, IBuild> builds = new HashMap<String, IBuild>();
		for (IBuild build : client.getBuilds(productId)) {
			log.info("checking build - " + build.getName());
			String buildName = buildName(build);
			if (DateUtils.isValidDate(build.getCreationTimeStamp())) {
				if (builds.get(buildName) == null || DateUtils.getDate(build.getCreationTimeStamp())
						.after(DateUtils.getDate(builds.get(buildName).getCreationTimeStamp()))) {
					log.info("adding build - " + buildName);
					builds.put(buildName, build);
				}
			}
		}
		return builds;
	}
	
	private String buildName(IBuild build) {
		Matcher matcher = pattern.matcher(build.getName());
		matcher.find();
		return matcher.group(1);
	}

}
