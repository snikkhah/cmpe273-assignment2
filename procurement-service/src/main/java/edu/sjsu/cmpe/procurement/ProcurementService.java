package edu.sjsu.cmpe.procurement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.sound.midi.Receiver;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.message.StompJmsMessage;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;



import de.spinscale.dropwizard.jobs.JobsBundle;

import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;
import edu.sjsu.cmpe.procurement.domain.Book;
import edu.sjsu.cmpe.procurement.jobs.ProcurementSchedulerJob;


public class ProcurementService extends Service<ProcurementServiceConfiguration> {

    private final Logger log = LoggerFactory.getLogger(getClass());
//	public static Connection connection; 
	public static String queueName;
	public static WebResource sendOrder;
	public static WebResource receiveOrder;
	
    public static void main(String[] args) throws Exception {
	new ProcurementService().run(args);
    }

    @Override
    public void initialize(Bootstrap<ProcurementServiceConfiguration> bootstrap) {
	bootstrap.setName("procurement-service");
	bootstrap.addBundle(new JobsBundle("edu.sjsu.cmpe.procurement.jobs"));
    }

    @Override
    public void run(ProcurementServiceConfiguration configuration,
	    Environment environment) throws Exception {
	queueName = configuration.getStompQueueName();
	String topicName = configuration.getStompTopicName();
	log.debug("Queue name is {}. Topic is {}", queueName, topicName);

	// TODO: Jersey

		ClientConfig cc = new DefaultClientConfig();
	    cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
	    Client c = Client.create(cc);
	    sendOrder = c.resource("http://54.215.210.214:9000/orders");
	    receiveOrder = c.resource("http://54.215.210.214:9000/orders/59640");
	    

//	new ProcurementSchedulerJob(connection, queueName, sendOrder, receiveOrder);
  }
    


   
}
