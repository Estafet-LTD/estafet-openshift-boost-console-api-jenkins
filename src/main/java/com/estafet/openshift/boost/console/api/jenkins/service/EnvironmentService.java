package com.estafet.openshift.boost.console.api.jenkins.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.openshift.boost.console.api.jenkins.dao.EnvDAO;
import com.estafet.openshift.boost.console.api.jenkins.model.Env;
import com.estafet.openshift.boost.messages.environments.Environment;

@Service
public class EnvironmentService {
	
	@Autowired
	private EnvDAO envDAO;
	
	@Transactional
	public void updateEnv(Environment environment) {
		Env oldEnv = envDAO.getEnv(environment.getName());
		Env newEnv = Env.getEnv(environment);
		if (oldEnv == null) {
			envDAO.createEnv(newEnv);
		} else if (!oldEnv.getUpdatedDate().equals(newEnv.getUpdatedDate())) {
			envDAO.updateEnv(newEnv);	
		}
	}

}
