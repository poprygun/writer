package io.pivotal.poc.cs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WriterApplication implements CommandLineRunner {

	@Autowired
	private WriterService writerService;

	public static void main(String[] args) {
		SpringApplication.run(WriterApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {

//		SocketServer socketServer = new SocketServer();
//		socketServer.startMeUp();
//		Socket socket = socketServer.whatAmI();
//		writerService.send(socket);

//		writerService.send(new DataMessage("mssg-id", "recipient", "sender", "text"));
		writerService.send("some message");

//		writerService.receive("recipient");
	}
}
