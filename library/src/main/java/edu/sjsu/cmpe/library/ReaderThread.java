package edu.sjsu.cmpe.library;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.message.StompJmsMessage;

import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.domain.Book.Status;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;

import java.net.MalformedURLException;
import java.net.URL;

public class ReaderThread implements Runnable{
	private Connection connection;
	private String queue;
	private final BookRepositoryInterface bookRepository;
	   public ReaderThread(Connection connection, BookRepositoryInterface bookRepository, String queue) {
	       // store parameter for later user
		   this.connection=connection;
		   this.bookRepository = bookRepository;
		   this.queue=queue;
	   }

	   public void run() {
		   try {
//			    String queue = "/topic/59640.book";
				String destination = queue;
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				
				Destination dest = new StompJmsDestination(destination);
				MessageConsumer consumer = session.createConsumer(dest);
				System.out.println("Waiting for messages from " + queue + "...");
				String[] parse=null;
				while(true) {
				    Message msg = consumer.receive();
				    if( msg instanceof  TextMessage ) { 
				    parse=null;
					String body = ((TextMessage) msg).getText();
					if( "SHUTDOWN".equals(body)) {
					    break;
					}			
					parse = body.split("[:]");
					System.out.println("Received TextMessage = " + body);

						Book received= bookRepository.getBookByISBN(Long.parseLong(parse[0]));
						if (received!=null)
						{
							bookRepository.getBookByISBN(Long.parseLong(parse[0])).setStatus(Status.available);
						}
						else {
							Book newBook = new Book();
							newBook.setIsbn(Long.parseLong(parse[0]));
							newBook.setTitle(parse[1]);
							newBook.setCategory(parse[2]);
							newBook.setStatus(Status.available);
							newBook.setCoverimage( new URL(parse[3]+":"+parse[4]));
							bookRepository.saveBook(newBook);
						}
//						System.out.println("Parsed TextMessage = " + parse[i]);	
					
					
				    } else if (msg instanceof StompJmsMessage) {
					StompJmsMessage smsg = ((StompJmsMessage) msg);
					String body = smsg.getFrame().contentAsString();
					if ("SHUTDOWN".equals(body)) {
					    break;
					}
					System.out.println("Received JmsMessage = " + body);

				    } else {
					System.out.println("Unexpected message type: "+msg.getClass());
				    }
				}
				connection.close();  
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	   }

}
