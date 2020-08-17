package com.estafet.boostcd.jenkins.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.boostcd.jenkins.api.dao.EnvDAO;
import com.estafet.boostcd.jenkins.api.dao.ProductDAO;
import com.estafet.boostcd.jenkins.api.model.Env;
import com.estafet.boostcd.jenkins.api.model.Product;
import com.estafet.openshift.boost.messages.environments.Environment;
import com.estafet.openshift.boost.messages.environments.Environments;

@Service
public class EnvironmentService {
	
	@Autowired
	private EnvDAO envDAO;
	
	@Autowired
	private ProductDAO productDAO;
	
	@Transactional
	public void updateEnv(Environments environments) {
		Product product = productDAO.getProduct(environments.getProductId());
		if (product == null) {
			product = Product.builder().setProductId(environments.getProductId()).build();
			productDAO.create(product);
		}
		for (Environment environment : environments.getEnvironments()) {
			Env oldEnv = envDAO.getEnv(product.getProductId(), environment.getName());
			Env newEnv = Env.getEnv(environment);
			if (oldEnv == null) {
				product.addEnv(newEnv);
				envDAO.createEnv(newEnv);
			} else if (!oldEnv.getUpdatedDate().equals(newEnv.getUpdatedDate())) {
				envDAO.updateEnv(oldEnv.update(newEnv));	
			}
		}
	}

	@Transactional
	public void deleteEnvironments(Environments environments) {
		productDAO.deleteProduct(environments.getProductId());
	}

}
