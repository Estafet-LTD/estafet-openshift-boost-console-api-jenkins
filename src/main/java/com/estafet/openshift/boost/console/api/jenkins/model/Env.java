package com.estafet.openshift.boost.console.api.jenkins.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.estafet.openshift.boost.messages.environments.EnvironmentApp;

@Entity
@Table(name = "ENV")
public class Env {

	@Id
	@Column(name = "ENV_ID", nullable = false)
	private String name;

	@Column(name = "UPDATED_DATE", nullable = false)
	private String updatedDate;

	@Column(name = "LIVE", nullable = false)
	private boolean live;

	@OneToMany(mappedBy = "env", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Microservice> microservices = new HashSet<Microservice>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Set<Microservice> getMicroservices() {
		return microservices;
	}

	public void setMicroservices(Set<Microservice> microservices) {
		this.microservices = microservices;
	}

	public String buildBuildName() {
		if (name.equals("build")) {
			return "build-all";
		} else {
			return null;
		}
	}

	public String testBuildName() {
		if ((name.equals("green") || name.equals("blue")) && !live) {
			return "prod-test";
		} else if (name.equals("test")) {
			return "qa-test";
		} else {
			return null;
		}
	}
	
	public String promoteBuildName() {
		if (name.equals("build")) {
			return "release-all";
		} else if (name.equals("test")) {
			return "promote-all-to-prod";
		} else {
			return null;
		}
	}
	
	public String goLiveBuildName() {
		if ((name.equals("green") || name.equals("blue")) && !live) {
			return "promote-to-live";
		} else {
			return null;
		}
	}
	
	public String backOutBuildName() {
		if ((name.equals("green") || name.equals("blue")) && live) {
			return "promote-to-live";
		} else {
			return null;
		}
	}
	

	public static EnvBuilder builder() {
		return new EnvBuilder();
	}

	public static class EnvBuilder {

		private String name;
		private String updatedDate;
		private boolean live;

		public EnvBuilder setLive(boolean live) {
			this.live = live;
			return this;
		}

		public EnvBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public EnvBuilder setUpdatedDate(String updatedDate) {
			this.updatedDate = updatedDate;
			return this;
		}

		public Env build() {
			Env env = new Env();
			env.setName(name);
			env.setUpdatedDate(updatedDate);
			env.setLive(live);
			return env;
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Env other = (Env) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Env [name=" + name + "]";
	}

	public void add(EnvironmentApp app) {
		Microservice microservice = Microservice.builder()
				.setMicroservice(app.getName())
				.build();
		if (!microservices.contains(microservice)) {
			microservices.add(microservice);
			microservice.setEnv(this);
		}
	}

}
