package org.jboss.qa.ochaloup.service;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.jboss.logging.Logger;
import org.jboss.qa.ochaloup.model.TestEntity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ThreadLocalRandom;

@Stateless
@LocalBean
public class Worker {
    private static final Logger log = Logger.getLogger(Worker.class);

    // @Resource(lookup = "java:/JmsXA")
    private ConnectionFactory connectionFactory;    

    // @Resource(lookup="java:jboss/queue/testQueue")
    private Destination destination;

    @Resource(lookup="java:jboss/datasources/TestDS") // defined in persistence.xml as well
    private DataSource dataSource;
    
    @PersistenceContext
    EntityManager em;
    
    private static final String message = "Hello world!";

    // @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) - failure as DB insert can't work without a transaction
    public void doWork() {
        saveToDBDatasource(message);
        // sendMessage(message);
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

    private void saveToDBDatasource(String message) {
        java.sql.Connection conn;
        try {
            conn = dataSource.getConnection();
            PreparedStatement stm = conn.prepareStatement("INSERT INTO testentity (id,message) VALUES (?,?)");
            stm.setLong(1, ThreadLocalRandom.current().nextLong());
            stm.setString(2, message);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot get connection from datasource " + dataSource, sqle);
        } finally {
            // if (conn != null) conn.close();
        }
        TestEntity entity = new TestEntity();
        entity.setMessage(message);
        em.persist(entity);
        log.infof("Message %s persisted by default entity manager %s", message, em);
    }
}