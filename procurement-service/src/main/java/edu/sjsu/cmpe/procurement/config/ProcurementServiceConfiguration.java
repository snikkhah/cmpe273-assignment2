package edu.sjsu.cmpe.procurement.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class ProcurementServiceConfiguration extends Configuration {
    @NotEmpty
    @JsonProperty
    private String stompQueueName;

    @NotEmpty
    @JsonProperty
    private String stompAllBooksTopicName;

    @NotEmpty
    @JsonProperty
    private String stompComputerBooksTopicName;

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
     * @return the stompAllBooksTopicName
     */
    public String getStompAllBooksTopicName() {
	return stompAllBooksTopicName;
    }

    /**
     * @param stompAllBooksTopicName
     *            the stompAllBooksTopicName to set
     */
    public void setStompAllBooksTopicName(String stompTopicName) {
	this.stompAllBooksTopicName = stompTopicName;
    }

    /**
     * @return the stompComputerBooksTopicName
     */
    public String getStompComputerBooksTopicName() {
	return stompComputerBooksTopicName;
    }

    /**
     * @param stompComputerBooksTopicName
     *            the stompComputerBooksTopicName to set
     */
    public void setStompComputerBooksTopicName(
	    String stompComputerBooksTopicName) {
	this.stompComputerBooksTopicName = stompComputerBooksTopicName;
    }

}
