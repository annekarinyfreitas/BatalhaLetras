package com.ppd.sockets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class PlayerClient {
    String playerName;
    Board clientBoard;

    Socket clientSocket;
    PrintWriter clientPrintWriter;
    Scanner clientReader;

    public static void main(String[] args) {
        new PlayerClient("Jogador 1");
        new PlayerClient("Jogador 2");
    }

    public PlayerClient(String playerName) {
        this.playerName = playerName;
        clientBoard = new Board(playerName);
        setupConnection();

        // Evento para enviar mensagem do chat
        clientBoard.sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientPrintWriter.println(playerName + " : " + clientBoard.textToSend.getText());
                clientPrintWriter.flush();
                clientBoard.textToSend.setText("");
                clientBoard.textToSend.requestFocus();
            }
        });
    }

    private void setupConnection() {
        try {
            clientSocket = new Socket("127.0.0.1", 5000);
            clientPrintWriter = new PrintWriter(clientSocket.getOutputStream());
            clientReader = new Scanner(clientSocket.getInputStream());
            new Thread(new ServerListener()).start();
        } catch (Exception e) {
        }
    }

    private class ServerListener implements Runnable {
        @Override
        public void run() {
            while (clientReader.hasNextLine()) {
                System.out.println("Recebido");
                clientBoard.receivedText.append(clientReader.nextLine() + "\n");
            }
        }
    }
}
