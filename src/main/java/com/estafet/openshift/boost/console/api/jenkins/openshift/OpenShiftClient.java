package com.estafet.openshift.boost.console.api.jenkins.openshift;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openshift.restclient.ClientBuilder;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IBuild;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;

@Component
public final class OpenShiftClient {

	private static final Logger log = LoggerFactory.getLogger(OpenShiftClient.class);
	
	@Autowired
	private Tracer tracer;
	
	private String product;

	private IClient getClient() {
		product = System.getenv("PRODUCT");
		return new ClientBuilder("https://" + System.getenv("OPENSHIFT_HOST_PORT"))
				.withUserName(System.getenv("OPENSHIFT_USER"))
				.withPassword(System.getenv("OPENSHIFT_PASSWORD"))
				.build();
	}
	
	@SuppressWarnings("deprecation")
	public List<IBuild> getBuilds() {
		Span span = tracer.buildSpan("OpenShiftClient.getBuilds").start();
		try {
			return getClient().list(ResourceKind.BUILD, product + "-cicd");
		} catch (RuntimeException e) {
			log.error("an error has occured whilst retreiving the builds", handleException(span, e));
			return new ArrayList<IBuild>();
		} finally {
			span.finish();
		}
	}
	
	private RuntimeException handleException(Span span, RuntimeException e) {
		Tags.ERROR.set(span, true);
		Map<String, Object> logs = new HashMap<String, Object>();
		logs.put("event", "error");
		logs.put("error.object", e);
		logs.put("message", e.getMessage());
		StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
		logs.put("stack", sw.toString());
		span.log(logs);
		return e;
	}
	
}
