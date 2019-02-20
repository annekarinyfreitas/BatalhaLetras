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

    int firstPlayerPosition;
    int secondPlayerPosition;

    String[] firstPlayerLetters;
    String[] secondPlayerLetters;

    public Server() {
        setupConnection();
    }
    public static void main(String[] args) { new Server(); }

    // CONEXAO COM SOCKETS
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

                // Salva as saídas dos sockets, para enviar mensagens gerais do jogo
                PrintWriter w = new PrintWriter(clientSocket.getOutputStream());
                clientWriters.add(w);

                // Inicializa variáveis e comeca a partida
                startGame();

                // Listener para os sockets
                new Thread(new ClientListener(clientSocket)).start();
            }
        } catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
    }

    // THREAD PARA ESCUTAR MENSAGEM DOS SOCKETS
    private class ClientListener implements Runnable {
        Scanner serverReader;

        public ClientListener(Socket client) {
            try {
                serverReader = new Scanner(client.getInputStream());
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
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
                    // Mensagens do chat
                    case "chat":
                        sendMessageToAll("chat"+messageWithoutBegin);
                         break;

                     // Mensagens sobre a rolagem de dados
                    case "dice":
                        calculatePosition(messageWithoutBegin);
                         sendMessageToAll("dicefirstPlayer:" + firstPlayerPosition + "," + "secondPlayer:" + secondPlayerPosition);
                         break;

                     // Mensagens sobre o envio da palavra da jogada
                    case "word":
                        updatePlayersLetters(messageWithoutBegin);
                        break;

                    // Mensagens de passar o turno
                    case "turn":
                        passTurn(messageWithoutBegin);
                        break;

                    // Mensagens de reiniciar a partida
                    case "rest":
                        startGame();
                        break;

                    default:
                        break;
                }
            }
        }
    }

    //  ENVIA MENSAGENS PARA TODOS OS SOCKETS
    private void sendMessageToAll(String message) {
        for (PrintWriter w: clientWriters) {
            try {
                w.println(message);
                w.flush();
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }

    // ENVIA MENSAGENS PARA TODOS UM SOCKET
    private void sendMessageToSocket(Socket player, String message) {
        try {
            PrintWriter w = new PrintWriter(player.getOutputStream());
            w.println(message);
            w.flush();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    // PASSAR O TURNO (EX: firstPlayer)
    private void passTurn(String message) {
        if (message.startsWith("f")) {
            sendMessageToSocket(secondPlayer, "turnplay");
            sendMessageToSocket(firstPlayer, "turnwait");

            sendMessageToAll("gameJogo: Agora é a vez do Jogador 2!");
        }
        else {
            sendMessageToSocket(firstPlayer, "turnplay");
            sendMessageToSocket(secondPlayer, "turnwait");

            sendMessageToAll("gameJogo: Agora é a vez do Jogador 1!");
        }
    }

    // CALCULA A POSICAO DOS JOGADORES NO TABULEIRO (EX: firstPlayer:0,secondPlayer:0)
    private void calculatePosition(String message) {
        String position = message.substring(message.length() - 1);

        if (message.startsWith("f")) {
            firstPlayerPosition += Integer.parseInt(position);
            if (firstPlayerPosition > 25) {
                firstPlayerPosition = firstPlayerPosition - 26;
            }
            sendMessageToAll("gameJogo: O Jogador 1 anda "+ position +  " casas!");
        }
        else {
            secondPlayerPosition += Integer.parseInt(position);
            if (secondPlayerPosition > 25) {
                secondPlayerPosition = secondPlayerPosition - 26;
            }
            sendMessageToAll("gameJogo: O Jogador 2 anda "+ position +  " casas!");
        }
    }

    // ATUALIZA AS LETRAS DISPONIVEIS DO JOGADOR E DE SEU OPONENTE (EX: firstPlayer:casa)
    private void updatePlayersLetters(String message) {
        String[] messageArray = message.split(":");

        // Apaga as letras descritas nos arrays dos jogadores
        if (message.startsWith("f")) {
            secondPlayerLetters = removeWordFromLetters(messageArray[1], secondPlayerLetters);
            sendMessageToAll("gameJogo: A palavra enviada pelo Jogador 1 é "+ messageArray[1]);
        } else {
            firstPlayerLetters = removeWordFromLetters(messageArray[1], firstPlayerLetters);
            sendMessageToAll("gameJogo: A palavra enviada pelo Jogador 2 é "+ messageArray[1]);
        }

        // Envia uma mensagem para os jogadores com suas letras atualizadas
        sendLettersUpdate();
    }

    // ENVIA MENSAGEM DE ATUALIZACAO DE LETRAS
    private void sendLettersUpdate() {
        // Envia no formato wordA,B,C,D:A,B,C,D onde antes do : são as letras do jogador e após são as letras do oponente
        sendMessageToSocket(firstPlayer, "word" + String.join(",", firstPlayerLetters) + ":" + String.join(",", secondPlayerLetters));
        sendMessageToSocket(secondPlayer, "word" + String.join(",", secondPlayerLetters) + ":" + String.join(",", firstPlayerLetters));
    }

    // REMOVE A PALAVRA RECEBIDA DO ARRAY DE LETRAS DO OPONENTE
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

    // REINICIA AS VARIAVEIS DO JOGO
    private void startGame() {
        // Ambos jogadores comecam da primeira letra
        firstPlayerPosition = 0;
        secondPlayerPosition = 0;

        // Ambos jogadores possuem todas as letras disponíveis
        firstPlayerLetters = new String[] {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        secondPlayerLetters = new String[] {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

        // Inicia a partida pelo jogador 1
        if (firstPlayer != null && secondPlayer != null) {
            sendMessageToAll("init");

            // Define quem espera e quem aguarda, neste caso o jogador firstPlayer inicia a partida
            sendMessageToSocket(firstPlayer, "turnplay");
            sendMessageToSocket(secondPlayer, "turnwait");

            // Inicializa os jogadores com todas as letras disponiveis e envia para eles suas letras
            sendLettersUpdate();

            // Avisa aos jogadores
            sendMessageToAll("dicefirstPlayer:" + firstPlayerPosition + "," + "secondPlayer:" + secondPlayerPosition);
            sendMessageToAll("gameJogo: É a vez do Jogador 1");
        }
    }
}
