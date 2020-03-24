package com.estafet.openshift.boost.console.api.jenkins.service;

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

import com.estafet.openshift.boost.commons.lib.date.DateUtils;
import com.estafet.openshift.boost.console.api.jenkins.dao.EnvDAO;
import com.estafet.openshift.boost.console.api.jenkins.dto.EnvState;
import com.estafet.openshift.boost.console.api.jenkins.openshift.OpenShiftClient;
import com.openshift.restclient.model.IBuild;

@Service
public class StateService {

	private static final Logger log = LoggerFactory.getLogger(StateService.class);
	private static Pattern pattern = Pattern.compile("(.+)(\\-\\d+)");

	@Autowired
	private OpenShiftClient client;

	@Autowired
	private EnvDAO envDAO;

	public List<EnvState> getStates() {
		Map<String, IBuild> builds = latestBuilds();
		List<EnvState> states = new ArrayList<EnvState>();
		states.add(getState("build", builds));
		states.add(getState("test", builds));
		states.add(getState("green", builds));
		states.add(getState("blue", builds));
		return states;
	}

	public EnvState getState(String env) {
		return getState(env, latestBuilds());
	}

	private EnvState getState(String envId, Map<String, IBuild> builds) {
		return EnvState.builder().setEnv(envDAO.getEnv(envId)).setBuilds(builds).build();
	}

	private Map<String, IBuild> latestBuilds() {
		Map<String, IBuild> builds = new HashMap<String, IBuild>();
		for (IBuild build : client.getBuilds()) {
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
