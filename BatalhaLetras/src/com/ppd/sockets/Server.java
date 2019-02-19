package com.ppd.sockets;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.List;

public class Server {
    ServerSocket server;
    List <PrintWriter> clientWriters = new ArrayList<>();
    Socket firstPlayer;
    Socket secondPlayer;
    Socket currentPlayer;
    int firstPlayerPosition = 0;
    int secondPlayerPosition = 0;
    String[] firstPlayerLetters = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    String[] secondPlayerLetters = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

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

                // Define o primeiro e o segundo jogador
                if (firstPlayer == null) {
                    firstPlayer = clientSocket;
                } else if (secondPlayer == null) {
                    secondPlayer = clientSocket;
                }

                PrintWriter w = new PrintWriter(clientSocket.getOutputStream());
                clientWriters.add(w);

                // Inicia a partida pelo jogador 1
                if (firstPlayer != null && secondPlayer != null) {
                    currentPlayer = firstPlayer;
                    currentPlaying(firstPlayer);
                    currentWaiting(secondPlayer);
                    sendMessageToAll("dicefirstPlayer" + firstPlayerPosition + "," + "secondPlayer" + secondPlayerPosition);
                    sendMessageToAll("gameJogo: É a vez do Jogador 1");
                }

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
                String messageBegin = text.substring(0,4);
                String messageWithoutBegin = text.substring(4);

                switch (messageBegin) {
                    case "chat":
                        sendMessageToAll("chat"+messageWithoutBegin);
                         break;

                    case "dice":
                        calculatePosition(messageWithoutBegin);
                         sendMessageToAll("dicefirstPlayer" + firstPlayerPosition + "," + "secondPlayer" + secondPlayerPosition);
                         break;

                    case "word":
                        updatePlayersLetters(messageWithoutBegin);
                        break;

                }
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

    private void currentPlaying(Socket player) {
        try {
            PrintWriter w = new PrintWriter(player.getOutputStream());
            w.println("turnplay");
            w.flush();
        } catch (Exception e) {

        }
    }

    private void currentWaiting(Socket player) {
        try {
            PrintWriter w = new PrintWriter(player.getOutputStream());
            w.println("turnwait");
            w.flush();
        } catch (Exception e) {

        }
    }

    // Envia uma mensagem direcionada para um player
    private void sendMessageToSocket(Socket player, String message) {
        try {
            PrintWriter w = new PrintWriter(player.getOutputStream());
            w.println(message);
            w.flush();
        } catch (Exception e) {

        }
    }

    private void calculatePosition(String message) {
        String position = message.substring(message.length() - 1);

        if (message.startsWith("f")) {
            firstPlayerPosition += Integer.parseInt(position);
            sendMessageToAll("gameJogo: O Jogador 1 anda "+ position +  " casas!");
        } else {
            secondPlayerPosition += Integer.parseInt(position);
            sendMessageToAll("gameJogo: O Jogador 2 anda "+ position +  " casas!");
        }
    }

    private void updatePlayersLetters(String message) {
        String[] messageArray = message.split(",");

        // Apaga as letras descritas nos arrays dos jogadores
        if (message.startsWith("f")) {
            secondPlayerLetters = removeWordFromLetters(messageArray[1], secondPlayerLetters);
        } else {
            firstPlayerLetters = removeWordFromLetters(messageArray[1], firstPlayerLetters);
        }

        // Envia as mensagens de atualização
        // Formato wordA,B,C,D:A,B,C,D onde antes do : são as letras do jogador e após são as letras do oponente
        sendMessageToSocket(firstPlayer, "word" + String.join(",", firstPlayerLetters) + ":" + String.join(",", secondPlayerLetters));
        sendMessageToSocket(secondPlayer, "word" + String.join(",", secondPlayerLetters) + ":" + String.join(",", firstPlayerLetters));
    }

    // Remove a palavra do array de letras
    private String[] removeWordFromLetters(String word, String[] array) {
        List<String> list = new ArrayList<String>(Arrays.asList(array));

        for (char c: word.toCharArray()) {
            for (String letter: array) {

                if (Character.toString(Character.toUpperCase(c)).equals(letter)) {
                    list.remove(letter);
                }
            }
        }
        return list.toArray(new String[0]);
    }
}
