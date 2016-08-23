package io.pivotal.poc.cs;


import java.net.Socket;
import java.util.List;

public interface WriterService {
    void send(DataMessage message);
    void send(String socket);

    List<DataMessage> receive(String recipient);
}
