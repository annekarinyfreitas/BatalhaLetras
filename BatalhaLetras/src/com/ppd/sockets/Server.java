package com.ppd.sockets;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class Server {
    ServerSocket server;
    List <PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        setupConnection();
    }

    private void setupConnection() {
        try {
            server = new ServerSocket(5000);
            while (true) {
                Socket clientSocket = server.accept();
                PrintWriter w = new PrintWriter(clientSocket.getOutputStream());
                clientWriters.add(w);

                new Thread(new ClientListener(clientSocket)).start();
            }
        } catch (Exception e){

        }
    }

    private class ClientListener implements Runnable {
        Scanner serverReader;

        public ClientListener(Socket client) {
            try {
                serverReader = new Scanner(client.getInputStream());
            } catch (Exception e) {
            }
        }

        @Override
        public void run() {
            String text;
            while ((text = serverReader.nextLine()) != null) {
                System.out.println(text);
                sendMessageToAll(text);
            }
        }
    }

    private void sendMessageToAll(String message) {
        for (PrintWriter w: clientWriters) {
            try {
                w.println(message);
                w.flush();

            } catch (Exception e) {

            }
        }
    }
}
