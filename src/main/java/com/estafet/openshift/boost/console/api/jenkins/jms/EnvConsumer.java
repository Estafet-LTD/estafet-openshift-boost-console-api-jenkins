package com.estafet.openshift.boost.console.api.jenkins.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.estafet.openshift.boost.console.api.jenkins.service.EnvironmentService;
import com.estafet.openshift.boost.messages.environments.Environment;

import io.opentracing.Tracer;

@Component
public class EnvConsumer {

	public final static String TOPIC = "env.topic";

	@Autowired
	private Tracer tracer;
	
	@Autowired
	private EnvironmentService environmentService;

	@JmsListener(destination = TOPIC, containerFactory = "myFactory")
	public void onMessage(String message) {
		try {
			environmentService.updateEnv(Environment.fromJSON(message));
		} finally {
			if (tracer.activeSpan() != null) {
				tracer.activeSpan().close();
			}
		}
	}

}
