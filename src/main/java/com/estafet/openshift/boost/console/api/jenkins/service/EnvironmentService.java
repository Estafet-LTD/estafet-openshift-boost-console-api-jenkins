package com.estafet.openshift.boost.console.api.jenkins.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.jenkins.dao.EnvDAO;
import com.estafet.openshift.boost.console.api.jenkins.model.Env;
import com.estafet.openshift.boost.messages.environments.Environment;
import com.estafet.openshift.boost.messages.environments.EnvironmentApp;

@Service
public class EnvironmentService {
	
	@Autowired
	private EnvDAO envDAO;
	
	@Transactional
	public void updateEnv(Environment environment) {
		Env env = envDAO.getEnv(environment.getName());
		if (env == null) {
			env = Env.builder()
					.setName(environment.getName())
					.setUpdatedDate(environment.getUpdatedDate())
					.build();
			updateApps(environment, env);
			envDAO.createEnv(env);
		} else if (!env.getUpdatedDate().equals(environment.getUpdatedDate())) {
			updateApps(environment, env);
			envDAO.updateEnv(env);	
		}
	}

	private void updateApps(Environment environment, Env env) {
		for (EnvironmentApp app : environment.getApps()) {
			env.add(app);
		}
	}
	
}
