package com.ppd.sockets;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
                System.out.println("Recebido Servidor -> " + text);

                // Faz a conversao da mensagem recebida em json
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject receivedObject = (JSONObject) parser.parse(text);

                    // CHAT
                    if (receivedObject.get("chat") != null) {
                        sendMessageToAll(text);
                    }

                    // Mensagens sobre a rolagem de dados
                    if (receivedObject.get("dice") != null) {
                        calculatePosition(receivedObject.get("dice").toString());
                    }

                    // Mensagens sobre o envio da palavra da jogada
                    if (receivedObject.get("playedWord") != null) {
                        updatePlayersLetters(receivedObject.get("playedWord").toString());
                    }

                    // Mensagens de passar o tueo
                    if (receivedObject.get("passTurn") != null) {
                        passTurn(receivedObject.get("passTurn").toString());
                    }

                    // Mensagens de quando um jogador desiste da partida
                    if (receivedObject.get("over") != null) {
                        sendGameOverMessages(receivedObject.get("over").toString());
                    }

                    // Mensagem para resetar a partida
                    if (receivedObject.get("resetGame") != null) {
                        startGame();
                    }

                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
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

    // PASSAR O TURNO (Exemplo de entrada: firstPlayer)
    private void passTurn(String message) {
        JSONObject object = new JSONObject();

        if (message.equals("firstPlayer")) {
            object.put("turn", "secondPlayer");
            object.put("gameLog","Jogo: Agora é a vez do Jogador 2!");
        }
        else {
            object.put("turn", "firstPlayer");
            object.put("gameLog","Jogo: Agora é a vez do Jogador 1!");
        }

        sendMessageToAll(object.toJSONString());
    }

    // CALCULA A POSICAO DOS JOGADORES NO TABULEIRO (Exemplo de entrada: firstPlayer:1)
    private void calculatePosition(String message) {
        JSONObject object = new JSONObject();
        String position = message.substring(message.length() - 1);

        if (message.startsWith("firstPlayer")) {
            firstPlayerPosition += Integer.parseInt(position);
            if (firstPlayerPosition > 25) {
                firstPlayerPosition = firstPlayerPosition - 26;
            }
            object.put("gameLog", "Jogo: O Jogador 1 anda "+ position +  " casas!");
        }
        else {
            secondPlayerPosition += Integer.parseInt(position);
            if (secondPlayerPosition > 25) {
                secondPlayerPosition = secondPlayerPosition - 26;
            }
            object.put("gameLog", "Jogo: O Jogador 2 anda "+ position +  " casas!");
        }

        // Envia as mensagens de atualização da posição
        object.put("firstPlayer", playerInformationJSONArray(firstPlayerLetters, firstPlayerPosition));
        object.put("secondPlayer", playerInformationJSONArray(secondPlayerLetters, secondPlayerPosition));
        sendMessageToAll(object.toJSONString());
    }

    // ATUALIZA AS LETRAS DISPONIVEIS DO JOGADOR E DE SEU OPONENTE (Exemplo de entrada: firstPlayer:casa)
    private void updatePlayersLetters(String message) {
        JSONObject object = new JSONObject();
        String[] messageArray = message.split(":");

        // Apaga as letras descritas nos arrays dos jogadores
        if (message.startsWith("firstPlayer")) {
            secondPlayerLetters = removeWordFromLetters(messageArray[1], secondPlayerLetters);
            object.put("gameLog", "Jogo: A palavra enviada pelo Jogador 1 é "+ messageArray[1]);
        } else {
            firstPlayerLetters = removeWordFromLetters(messageArray[1], firstPlayerLetters);
            object.put("gameLog", "Jogo: A palavra enviada pelo Jogador 2 é "+ messageArray[1]);
        }

        // Envia as mensagens de atualização das letras
        object.put("firstPlayer", playerInformationJSONArray(firstPlayerLetters, firstPlayerPosition));
        object.put("secondPlayer", playerInformationJSONArray(secondPlayerLetters, secondPlayerPosition));
        sendMessageToAll(object.toJSONString());
    }

    // DESISTENCIA DO JOGO (Exemplo de entrada: firstPlayer)
    private void sendGameOverMessages(String message) {
        // Se foi o primeiro jogador a desistir, envia a mensagem de game over pra ele e pro oponente uma de give up
        JSONObject object = new JSONObject();
        object.put("giveUp", message);
        sendMessageToAll(object.toJSONString());
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

            // Mensagem de inicializacao do jogo
            JSONObject initMessageObject = new JSONObject();
            initMessageObject.put("init", true);
            initMessageObject.put("turn", "firstPlayer");

            // Dados do firstPlayer
            initMessageObject.put("firstPlayer", playerInformationJSONArray(firstPlayerLetters, firstPlayerPosition));

            // Dados do firstPlayer
            initMessageObject.put("secondPlayer", playerInformationJSONArray(secondPlayerLetters, secondPlayerPosition));

            // Mensagem do jogo
            initMessageObject.put("gameLog", "Jogo: É a vez do Jogador 1!");

            // Envia mensagem aos sockets
            sendMessageToAll(initMessageObject.toJSONString());
        }
    }

    // COMPACTA UM JSONARRAY COM AS INFORMACOES DOS JOGADORES (LETRAS E POSICAO)
    private JSONArray playerInformationJSONArray(String[] letters, int position) {
        JSONArray playerArray = new JSONArray();
        JSONObject playerObject = new JSONObject();
        playerObject.put("remainingLetters",  String.join(",", letters));
        playerObject.put("boardPosition", position);
        playerArray.add(playerObject);
        return  playerArray;
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
}
