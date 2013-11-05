package edu.sjsu.cmpe.library;

import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

import edu.sjsu.cmpe.library.api.resources.BookResource;
import edu.sjsu.cmpe.library.api.resources.RootResource;
import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.ui.resources.HomeResource;





import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.message.StompJmsMessage;

public class LibraryService extends Service<LibraryServiceConfiguration> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    public static void main(String[] args) throws Exception {
	new LibraryService().run(args);
    }

    @Override
    public void initialize(Bootstrap<LibraryServiceConfiguration> bootstrap) {
	bootstrap.setName("library-service");
	bootstrap.addBundle(new ViewBundle());
    }

    @Override
    public void run(LibraryServiceConfiguration configuration,
	    Environment environment) throws Exception {
	// This is how you pull the configurations from library_x_config.yml
	String queueName = configuration.getStompQueueName();
	String topicName = configuration.getStompTopicName();
	String libraryName = configuration.getLibraryName();
	String listeningQueue="";
	if ("library-a".equals(libraryName)){
		listeningQueue=topicName;
		}
	else if ("library-b".equals(libraryName)){
		listeningQueue="/topic/59640.book.computer";
		}
	
	log.debug("Queue name is {}. Topic name is {}", queueName,
		topicName);
	// TODO: Apollo STOMP Broker URL and login

	String user = env("APOLLO_USER", configuration.getApolloUser());
	String password = env("APOLLO_PASSWORD", configuration.getApolloPassword());
	String host = env("APOLLO_HOST", configuration.getApolloHost());
	int port = Integer.parseInt(env("APOLLO_PORT", configuration.getApolloPort()));
	StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
	factory.setBrokerURI("tcp://" + host + ":" + port);

	Connection connection = factory.createConnection(user, password);
	connection.start();
//	producer.send(session.createTextMessage("SHUTDOWN"));
//	connection.close();


	/** Root API */
	environment.addResource(RootResource.class);
	/** Books APIs */
	BookRepositoryInterface bookRepository = new BookRepository();
	environment.addResource(new BookResource(bookRepository,connection,libraryName,queueName));

	/** UI Resources */
	environment.addResource(new HomeResource(bookRepository));
    /** Run the queue reader thread concurrently */
	Runnable r = new ReaderThread(connection,bookRepository,listeningQueue);
	new Thread(r).start(); 
    }
    private static String env(String key, String defaultValue) {
    	String rc = System.getenv(key);
    	if( rc== null ) {
    	    return defaultValue;
    	}
    	return rc;
        }

//        private static String arg(String []args, int index, String defaultValue) {
//    	if( index < args.length ) {
//    	    return args[index];
//    	} else {
//    	    return defaultValue;
//    	}
//        }
}
