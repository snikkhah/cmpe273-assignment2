package edu.sjsu.cmpe.library.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.yammer.dropwizard.config.Configuration;

public class LibraryServiceConfiguration extends Configuration {
    @NotEmpty
    @JsonProperty
    private String stompQueueName;

    @NotEmpty
    @JsonProperty
    private String stompTopicName;

    @NotEmpty
    @JsonProperty
    private String libraryName;

    @NotEmpty
    @JsonProperty
    private String apolloUser;

	@NotEmpty
    @JsonProperty
    private String apolloPassword;
	
	@NotEmpty
    @JsonProperty
    private String apolloHost;
	
	@NotEmpty
    @JsonProperty
    private String apolloPort;

    public String getApolloUser() {
		return apolloUser;
	}

	public void setApolloUser(String apolloUser) {
		this.apolloUser = apolloUser;
	}

    public String getApolloPassword() {
		return apolloPassword;
	}

	public void setApolloPassword(String apolloPassword) {
		this.apolloPassword = apolloPassword;
	}

    public String getApolloHost() {
		return apolloHost;
	}

	public void setApolloHost(String apolloHost) {
		this.apolloHost = apolloHost;
	}

	public String getApolloPort() {
		return apolloPort;
	}

	public void setApolloPort(String apolloPort) {
		this.apolloPort = apolloPort;
	}

	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}

	/**
     * @return the stompQueueName
     */
    public String getStompQueueName() {
	return stompQueueName;
    }

    /**
     * @param stompQueueName
     *            the stompQueueName to set
     */
    public void setStompQueueName(String stompQueueName) {
	this.stompQueueName = stompQueueName;
    }

    /**
     * @return the stompTopicName
     */
    public String getStompTopicName() {
	return stompTopicName;
    }

    /**
     * @param stompTopicName
     *            the stompTopicName to set
     */
    public void setStompTopicName(String stompTopicName) {
	this.stompTopicName = stompTopicName;
    }
}
