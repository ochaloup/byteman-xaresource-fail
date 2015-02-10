package org.jboss.qa.ochaloup.service;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.jboss.qa.ochaloup.model.TestEntity;

@Stateless
@LocalBean
public class Worker {
    private static final Logger log = Logger.getLogger(Worker.class);
    
    @Resource(lookup = "java:/JmsXA")
    private ConnectionFactory connectionFactory;    

    @Resource(lookup="java:jboss/queue/testQueue")
    private Destination destination;
    
    @PersistenceContext
    EntityManager em;
    
    private static final String message = "Hello world!";
    
    
    public void doWork() {
        saveToDB(message);
        sendMessage(message);
    }
    
    private void sendMessage(String message) {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            // for sending messages you don't need to start connection
            // connection.start();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            MessageProducer producer = session.createProducer(destination);
            log.infof("Sending message %s to queue %s", message, destination);
            producer.send(session.createTextMessage(message));
        } catch (Exception e) {
            log.error("Error in sending a message:", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ignored) {}
            }
        }
    }
    
    private void saveToDB(String message) {
        TestEntity entity = new TestEntity();
        entity.setMessage(message);
        em.persist(entity);
        log.infof("Message %s persisted by default entity manager %s", message, em);
    }
}