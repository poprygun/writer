package io.pivotal.poc.cs;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class WriterServiceImpl implements WriterService {
    private static final Logger LOG = LoggerFactory.getLogger(WriterService.class);

    private final HazelcastInstance hazelcastInstance;

    public static final String ACCEPTED_MESSAGES_TRACKING_MAP_NAME = "received";
    public static final String RECIPIENT_QUEUE_NAME_SUFFIX = "recipient-";
    public static final String SOCKET_QUEUE_NAME_SUFFIX = "socket-";

    @Autowired
    public WriterServiceImpl(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    // Starting the HazelcastInstance is heavyweight, while retrieving a distributed object from it is not

    private IQueue<DataMessage> recipientQueue(String user) {
        return hazelcastInstance.getQueue(RECIPIENT_QUEUE_NAME_SUFFIX + user);
    }

    private IQueue<Socket> socketQueue(String user) {
        return hazelcastInstance.getQueue(SOCKET_QUEUE_NAME_SUFFIX + user);
    }


    private IMap<Object, Object> acceptedMessageUidMap() {
        return hazelcastInstance.getMap(ACCEPTED_MESSAGES_TRACKING_MAP_NAME);
    }


    @Override
    public void send(DataMessage message)  {

        // Check if the message is duplicate. If duplicate, silently ignore it
        if( !isDuplicate(message)) {
            LOG.info("Submitting the message id:{}", message.getMessageUid());
            recipientQueue(message.getRecipient()).offer(message);

            // Save UID as accepted
            markAsAccepted(message);
        }

    }

    @Override
    public void send(String socket) {
        Map<Integer, String> sockets = hazelcastInstance.getMap("names");
        sockets.put(1, "Joe");
        sockets.put(2, "Ali");
        sockets.put(3, "Avi");
        System.out.println("======>loaded writer collection..");
//        Map<Integer, Socket> sockets = hazelcastInstance.getMap("sockets");
//        sockets.put(1, socket);
    }

    @Override
    public List<DataMessage> receive(String recipient) {
        LOG.debug("Polling message for recipient: {}", recipient);

        final List<DataMessage> messages = new ArrayList();
        while ( true ) {
            final DataMessage message = recipientQueue(recipient).poll();
            if ( message == null ) break;
            LOG.info("Polled message id:{}", message.getMessageUid());
            messages.add(message);
        }
        LOG.info("Returning {} messages", messages.size());

        return Collections.unmodifiableList(messages);
    }



    private boolean isDuplicate(DataMessage message) {
        // We just store and check the message UID. A distributed Set would suffice, but unfortunately
        // Hazelcast ISet doesn't support automatic eviction
        final boolean duplicate = acceptedMessageUidMap().containsKey(message.getMessageUid());
        LOG.debug("Message id:{} is duplicate? {}", message.getMessageUid(), duplicate);
        return duplicate;
    }

    private void markAsAccepted(DataMessage message) {
        LOG.debug("Marking message id:{} as accepted", message.getMessageUid());
        acceptedMessageUidMap().put(message.getMessageUid(),"");
    }
}
