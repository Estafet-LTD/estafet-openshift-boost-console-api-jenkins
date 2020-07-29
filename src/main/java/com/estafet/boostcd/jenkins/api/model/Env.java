package com.estafet.boostcd.jenkins.api.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.estafet.openshift.boost.messages.environments.Environment;
import com.estafet.openshift.boost.messages.environments.EnvironmentApp;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "ENV")
public class Env {

	@Id
	@SequenceGenerator(name = "ENV_ID_SEQ", sequenceName = "ENV_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENV_ID_SEQ")
	@Column(name = "ENV_ID")
	private Long id;
	
	@Column(name = "ENV_NAME", nullable = false)
	private String name;

	@Column(name = "UPDATED_DATE", nullable = false)
	private String updatedDate;

	@Column(name = "LIVE", nullable = false)
	private Boolean live;
	
	@Column(name = "NEXT", nullable = true)
	private String next;

	@OneToMany(mappedBy = "env", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Microservice> microservices = new HashSet<Microservice>();
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "PRODUCT_ID", nullable = false, referencedColumnName = "PRODUCT_ID", foreignKey = @ForeignKey(name = "ENV_TO_PRODUCT_FK"))
	private Product product;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void addMicroservice(Microservice microservice) {
		microservices.add(microservice);
		microservice.setEnv(this);
	}
	
	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public Boolean getLive() {
		return live;
	}

	public void setLive(Boolean live) {
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
		if (name.equals("green") || name.equals("blue")) {
			return "qa-prod";
		} else if (!name.equals("build")) {
			return "qa-" + name;
		} else {
			return null;
		}
	}
	
	public String promoteBuildName() {
		if (name.equals("build")) {
			return "release-all";
		} else if (next.equals("prod")) {
			return "promote-all-to-prod";
		} else {
			return "promote-all-" + name;
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
		private String next;

		public EnvBuilder setNext(String next) {
			this.next = next;
			return this;
		}

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
			env.setNext(next);
			return env;
		}

	}
	
	public boolean microserviceExists(Microservice newMicroservice) {
		for (Microservice microservice : microservices) {
			if (microservice.getMicroservice().equals(newMicroservice.getMicroservice())) {
				return true;
			}
		}
		return false;
	}
	
	public Env update(Env newEnv) {
		this.updatedDate = newEnv.updatedDate;
		this.live = newEnv.live;
		this.next = newEnv.next;
		for (Microservice newMicroservice : newEnv.microservices) {
			if (!microserviceExists(newMicroservice)) {
				addMicroservice(newMicroservice);
			}
		}
		return this;
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
				.setEnv(this)
				.build();
		if (!microservices.contains(microservice)) {
			microservices.add(microservice);
		}
	}
	
	public static Env getEnv(Environment environment) {
		Env env = Env.builder()
				.setName(environment.getName())
				.setUpdatedDate(environment.getUpdatedDate())
				.setNext(environment.getNext())
				.build();
		for (EnvironmentApp app : environment.getApps()) {
			env.add(app);
		}
		return env;
	}

}
