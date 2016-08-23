package io.pivotal.poc.cs;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    void startMeUp() throws Exception {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(9090);
                    System.out.println("Waiting for clients to connect...");
                    while (true) {
                        Socket socket = serverSocket.accept();

                        try {
                            PrintWriter out =
                                    new PrintWriter(socket.getOutputStream(), true);
                            out.println("----------> data from socket server....." + new Date().toString());
                        } finally {
                            socket.close();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();

    }

    Socket whatAmI() throws Exception {

        String serverAddress = "192.168.11.1";
        Socket socket = new Socket(serverAddress, 9090);
        BufferedReader input =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String answer = input.readLine();
        System.out.println(answer);
        return socket;
    }
}
