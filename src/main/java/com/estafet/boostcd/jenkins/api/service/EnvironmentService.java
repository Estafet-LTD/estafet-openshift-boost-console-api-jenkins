package com.estafet.boostcd.jenkins.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.boostcd.jenkins.api.dao.EnvDAO;
import com.estafet.boostcd.jenkins.api.model.Env;
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
			envDAO.updateEnv(oldEnv.update(newEnv));	
		}
	}

}
