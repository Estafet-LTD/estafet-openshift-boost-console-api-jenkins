package com.estafet.openshift.boost.console.api.jenkins.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "MICROSERVICE", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"ENV_ID", "MICROSERVICE"}, name = "MICROSERVICE_KEY") })
public class Microservice {

	@Id
	@SequenceGenerator(name = "MICROSERVICE_ID_SEQ", sequenceName = "MICROSERVICE_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MICROSERVICE_ID_SEQ")
	@Column(name = "MICROSERVICE_ID")
	private Long id;

	@Column(name = "MICROSERVICE", nullable = false)
	private String microservice;

	@ManyToOne
	@JoinColumn(name = "ENV_ID", nullable = false, referencedColumnName = "ENV_ID", foreignKey = @ForeignKey(name = "MICROSERVICE_TO_ENV_FK"))
	private Env env;

	public String getMicroservice() {
		return microservice;
	}

	public void setMicroservice(String microservice) {
		this.microservice = microservice;
	}

	public Env getEnv() {
		return env;
	}

	public void setEnv(Env microserviceEnv) {
		this.env = microserviceEnv;
	}

	public String buildBuildName() {
		if (env.getName().equals("build")) {
			return "build-" + microservice;
		} else {
			return null;
		}
	}
	
	public String promoteBuildName() {
		if (env.getNext() == null) {
			return null;
		} else if (env.getName().equals("build")) {
			return "release-" + microservice;
		} else if (env.getNext().equals("prod")) {
			return "promote-to-prod-" + microservice;
		} else {
			return "promote-" + env.getName() + "-" + microservice;
		}
	}
	
	public static MicroserviceBuilder builder() {
		return new MicroserviceBuilder();
	}
	
	public static class MicroserviceBuilder {
		
		private String microservice;
		private Env env;

		public MicroserviceBuilder setEnv(Env env) {
			this.env = env;
			return this;
		}

		public MicroserviceBuilder setMicroservice(String microservice) {
			this.microservice = microservice;
			return this;
		}
		
		public Microservice build() {
			Microservice app = new Microservice();
			app.setMicroservice(microservice);
			app.setEnv(env);
			return app;
		}
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((env == null) ? 0 : env.hashCode());
		result = prime * result + ((microservice == null) ? 0 : microservice.hashCode());
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
		Microservice other = (Microservice) obj;
		if (env == null) {
			if (other.env != null)
				return false;
		} else if (!env.equals(other.env))
			return false;
		if (microservice == null) {
			if (other.microservice != null)
				return false;
		} else if (!microservice.equals(other.microservice))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Microservice [id=" + id + ", microservice=" + microservice + ", env=" + env + "]";
	}

}
