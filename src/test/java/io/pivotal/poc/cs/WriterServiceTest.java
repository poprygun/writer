package io.pivotal.poc.cs;


import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WriterServiceTest {

    @Before
    public void setUp() {
        final HazelcastInstance instance = testInstanceFactory.newHazelcastInstance();
        service = new WriterServiceImpl(instance);
    }

    @Test(timeout = 10000L)
    public void testSendAndReceive() throws Exception {
        final DataMessage message = new DataMessage("mssg-id", "recipient", "sender", "text");
        service.send(message);

        List<DataMessage> received = new ArrayList<>();
        while(received.isEmpty()) {
            received = service.receive("recipient");
        }
        assertNotNull(received);
        assertEquals(1, received.size());
        assertEquals(message, received.get(0));
    }

    @After
    public void shutdown( ) {
        Hazelcast.shutdownAll();
    }

    private static TestHazelcastInstanceFactory testInstanceFactory = new TestHazelcastInstanceFactory();

    private WriterService service;
}
