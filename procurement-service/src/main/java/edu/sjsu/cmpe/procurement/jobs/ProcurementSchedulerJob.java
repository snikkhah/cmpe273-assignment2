package edu.sjsu.cmpe.procurement.jobs;

import java.util.ArrayList;
import java.io.Serializable;
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

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import edu.sjsu.cmpe.procurement.ProcurementService;

/**
 * This job will run at every 5 second.
 */
@Every("40s")
public class ProcurementSchedulerJob extends Job {
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Override
    public void doJob() {
//	String strResponse = ProcurementService.jerseyClient.resource(
//		"http://ip.jsontest.com/").get(String.class);
//	log.debug("Response from jsontest.com: {}", strResponse);
    	System.out.println("Hi I'm Tom!");
    	try {
    	// TODO: Apollo STOMP Broker URL and login
    	String user = env("APOLLO_USER", "admin");
    	String password = env("APOLLO_PASSWORD", "password");
    	String host = env("APOLLO_HOST", "54.215.210.214");
    	int port = Integer.parseInt(env("APOLLO_PORT", "61613"));

    	StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
    	factory.setBrokerURI("tcp://" + host + ":" + port);

    	Connection connection = factory.createConnection(user, password);
//    	Connection connection = ProcurementService.connection;
    	String queueName = ProcurementService.queueName;
    	WebResource sendOrder = ProcurementService.sendOrder;
    	WebResource receiveOrder = ProcurementService.receiveOrder;
   	
		
			connection.start();
	    	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
    	Destination dest = new StompJmsDestination(queueName);
    	MessageConsumer consumer = session.createConsumer(dest);
    	ArrayList<String> response = new ArrayList<String>();
    	ArrayList<String> isbns = new ArrayList<String>();
    	
    	String topicQueue = "/topic/59640.book";
    	long waitUntil = 5000; // 5000 wait for 5 sec
    	System.out.println("Waiting for messages from " + queueName + "...");
   
    	while(true) {
    	    Message msg = consumer.receive(waitUntil);
    		    if( msg instanceof  TextMessage) {
    			String body = ((TextMessage) msg).getText();
   			
    			System.out.println("Received TextMessage = " + body);
    			String parse[] = body.split("[:]");
    			isbns.add(parse[1]);
    		    } 
    		    else if (msg == null) {
    		          System.out.println("No new messages. Existing due to timeout - " + waitUntil / 1000 + " sec");
    		          break;
    		    } else {
    		         System.out.println("Unexpected message type: " + msg.getClass());
    		    }
    	}
    	
    	if (isbns.size()!= 0){   		
			sendHttpMessage(sendOrder,isbns);
			response = recieveHttpMessage(receiveOrder);
	//		System.out.println(response);
			for (int i = 0 ; i<response.size() ; i++){
				topicQueue = "/topic/59640.book";
				if (response.get(i).toLowerCase().contains(":computer:")){
					sendStompMessage(response.get(i),connection,topicQueue+".computer");
				}
					else if (response.get(i).toLowerCase().contains(":comics:")){
						sendStompMessage(response.get(i),connection,topicQueue+".comics");
				}
					else if (response.get(i).toLowerCase().contains(":management:")){
						sendStompMessage(response.get(i),connection,topicQueue+".management");
				}
					else if (response.get(i).toLowerCase().contains(":selfimprovement:")){
						sendStompMessage(response.get(i),connection,topicQueue+".selfimprovement");
				}
					else 
						sendStompMessage(response.get(i),connection,topicQueue);
			}

    	}
    	connection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private static void sendHttpMessage(WebResource r,ArrayList<String> isbns){
    	HashMap<String, Serializable> request = new HashMap<String, Serializable>();
	    request.put("id", "59640");
//	    ArrayList<String> isbns = new ArrayList<String>();
//	    isbns.add(isbn);
	    request.put("order_book_isbns", isbns);
	    System.out.println(request);
	    String response = r.accept(
	        MediaType.APPLICATION_JSON_TYPE,
	        MediaType.APPLICATION_XML_TYPE).
	        header("X-FOO", "BAR").
	        entity(request, MediaType.APPLICATION_JSON_TYPE).
	        post(String.class);
	    System.out.println(response);
    }
    private static ArrayList<String> recieveHttpMessage(WebResource w){
    	HashMap items = new HashMap();
    	items = w.accept(
		         MediaType.APPLICATION_JSON_TYPE,
		         MediaType.APPLICATION_XML_TYPE).
		         header("X-FOO", "BAR").
		         type(MediaType.APPLICATION_JSON_TYPE).
		         get(HashMap.class);
    	ArrayList<HashMap> books = new ArrayList<HashMap>();
    	ArrayList<String> output = new ArrayList<String>();
    	books = (ArrayList<HashMap>) items.get("shipped_books");
    	String parsed;
    	for (int i= 0; i < books.size(); i++ ){
    		parsed = "";
    		parsed = parsed + books.get(i).get("isbn")+":"+books.get(i).get("title")
    				+":"+books.get(i).get("category")+":"+books.get(i).get("coverimage");
    		output.add(parsed);
//    		System.out.println(output);
    	}
    	return output;
//		System.out.println(items);
    }
    public void sendStompMessage(String message, Connection connection, String queue) throws JMSException{
//    	String queue = "/topic/59640.book";
		String destination = queue;
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest = new StompJmsDestination(destination);
		MessageProducer producer = session.createProducer(dest);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		System.out.println("Sending messages to " + queue + "..." + message);
		String data = message; 
		TextMessage msg = session.createTextMessage(data);
		msg.setLongProperty("id", System.currentTimeMillis());
		producer.send(msg);
    }
    private static String env(String key, String defaultValue) {
	String rc = System.getenv(key);
	if( rc== null ) {
	    return defaultValue;
	}
	return rc;
    }

}
