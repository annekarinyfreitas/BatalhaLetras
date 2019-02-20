package com.ppd.sockets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.Arrays;

public class PlayerClient {
    String playerName;
    Board board;

    Socket clientSocket;
    PrintWriter clientPrintWriter;
    Scanner clientReader;

    public static void main(String[] args) {
        new PlayerClient("firstPlayer");
        new PlayerClient("secondPlayer");
    }

    public PlayerClient(String playerName) {
        this.playerName = playerName;
        board = new Board(playerName);
        setupConnection();

        // EVENTO PARA ENVIAR MENSAGEM DO CHAT
        board.sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendChatMessage();
            }
        });

        // EVENTO DE JOGAR O DADO
        board.diceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playDice();
            }
        });

        // EVENTO ENVIAR PALAVRA DO JOGO
        board.sendGameWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendGameWord();
            }
        });

        // EVENTO PARA DESISTIR DO JOGO
        board.giveUpGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                giveUpGame();
            }
        });

        // EVENTO PARA REINICIAR A PARTIDA
        board.restartPlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
    }

    // CONEXAO COM O SERVIDOR
    private void setupConnection() {
        try {
            clientSocket = new Socket("127.0.0.1", 5000);
            clientPrintWriter = new PrintWriter(clientSocket.getOutputStream());
            clientReader = new Scanner(clientSocket.getInputStream());
            new Thread(new ServerListener()).start();
        } catch (Exception e) {
        }
    }

    // THREAD PARA ESCUTAR MENSAGENS DO SERVIDOR
    private class ServerListener implements Runnable {
        @Override
        public void run() {
            String text;
            while ((text = clientReader.nextLine()) != null) {
                System.out.println("Recebido por " + playerName + " -> " + text);

                // Faz a conversao da mensagem recebida em json
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject receivedObject = (JSONObject) parser.parse(text);

                    // INICIO DO JOGO
                    if (receivedObject.get("init") != null) {
                        beginGame();
                    }

                    // CHAT
                    if (receivedObject.get("chat") != null) {
                        showChatMessage(receivedObject.get("chat").toString());
                    }

                    // MENSAGENS DE ATUALIZACAO DO JOGO, QUE FICAM NA LATERAL DIREITA
                    if (receivedObject.get("gameLog") != null) {
                        showGameLogMessage(receivedObject.get("gameLog").toString());
                    }

                    // TROCA DE TURNOS
                    if (receivedObject.get("turn") != null) {
                        play(receivedObject.get("turn").equals(playerName));
                    }

                    // POSICIONAMENTO DOS JOGADORES NO TABULEIRO E ATUALIZACAO DAS LETRAS RESTANTES
                    if (receivedObject.get("firstPlayer") != null && receivedObject.get("secondPlayer") != null) {
                        updatePlayersStatus((JSONArray) receivedObject.get("firstPlayer"), (JSONArray) receivedObject.get("secondPlayer"));
                    }

                    // DESISTENCIA DO JOGO
                    if (receivedObject.get("giveUp") != null) {
                        if (receivedObject.get("giveUp") .toString().equals(playerName)) {
                            playerGaveUpGame();
                        } else {
                            otherPlayerGaveUpGame();
                        }
                    }

                    // VITORIA
                    if (receivedObject.get("win") != null) {
                        showWinnerMessage(receivedObject.get("win").toString());
                    }

                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }
    }

    // Limpa os campos de texto no inicio da partida
    private void beginGame() {
        board.boardLog.setText("");
        board.receivedText.setText("");
    }

    // Mostra no JTextArea do jogador as mensagens do chat
    private void showChatMessage(String message) {
        board.receivedText.append(message + "\n");
    }

    private void showGameLogMessage(String message) {
        board.boardLog.append(message + "\n");
    }

    // ATIVA/DESATIVA BOTOES DE JOGAR
    private void play(boolean shouldPlay) {
        board.diceButton.setEnabled(shouldPlay);
        board.sendGameWordButton.setEnabled(false);
    }

    // ENVIA UM JSON PARA O SERVIDOR
    private void sendJSONToServer(JSONObject json) {
        clientPrintWriter.println(json.toJSONString());
        clientPrintWriter.flush();
    }

    // ENVIA MENSAGEM CHAT
    private void sendChatMessage() {
        JSONObject object = new JSONObject();
        object.put("chat", playerName + ": " + board.textToSend.getText());

        board.textToSend.setText("");
        board.textToSend.requestFocus();

        sendJSONToServer(object);
    }

    // JOGA O DADO
    private void playDice() {
        // Gera uma face aleatoria do dado
        int num = ThreadLocalRandom.current().nextInt(1, 7);

        // Altera a imagem do dado
        ImageIcon img = new ImageIcon("images/dice/" + num +".png");
        board.diceButton.setIcon(img);
        board.diceButton.setEnabled(false);
        board.sendGameWordButton.setEnabled(true);

        // Envia para o servidor a atualização da posição do jogador
        JSONObject object = new JSONObject();
        object.put("dice", playerName + ": " + num);
        sendJSONToServer(object);
    }

    // ENVIA A PALAVRA DA JOGADA
    private void sendGameWord() {
        JSONObject object = new JSONObject();

        // Formato firstPlayer:casa
        object.put("playedWord", playerName + ":" + board.selectedWord.getText());
        board.selectedWord.setText("");
        board.selectedWord.requestFocus();
        board.sendGameWordButton.setEnabled(false);

        // Passa o turno
        object.put("passTurn", playerName);

        sendJSONToServer(object);
    }

    // DESISTE DO JOGO
    private void giveUpGame() {
        JSONObject object = new JSONObject();
        object.put("over", playerName);

        sendJSONToServer(object);
    }

    // REINICIA A PARTIDA
    private void restartGame() {
        JSONObject object = new JSONObject();
        object.put("resetGame", true);

        sendJSONToServer(object);
    }

    // ATUALIZA OS STATUS DOS JOGADORES
    private void updatePlayersStatus(JSONArray firstPlayerArray, JSONArray secondPlayerArray) {
        for (int i = 0; i < firstPlayerArray.size(); i++) {
            JSONObject firstPlayerObject = (JSONObject) firstPlayerArray.get(i);
            JSONObject secondPlayerObject = (JSONObject) secondPlayerArray.get(i);

            // POSICIONAMENTO NO TABULEIRO
            if (firstPlayerObject.get("boardPosition") != null && secondPlayerObject.get("boardPosition") != null) {
                updateAllPlayersPosition(firstPlayerObject.get("boardPosition").toString(), secondPlayerObject.get("boardPosition").toString());
            }

            // LETRAS RESTANTES
            if (firstPlayerObject.get("remainingLetters") != null && secondPlayerObject.get("remainingLetters") != null) {
                updateAllPlayersLetters(firstPlayerObject.get("remainingLetters").toString(), secondPlayerObject.get("remainingLetters").toString());
            }
        }
    }

    // ATUALIZA A POSICAO DOS JOGADORES NO TABULEIRO
    private void updateAllPlayersPosition(String firstPlayerPosition, String secondPlayerPosition) {
        board.resetLetters();
        updatePosition("firstPlayer", Integer.parseInt(firstPlayerPosition));
        updatePosition("secondPlayer", Integer.parseInt(secondPlayerPosition));
    }

    private void updatePosition(String playerName, int position) {
        String playerSymbol = playerName.equals("firstPlayer") ? "(1)" : "(2)";
        board.boardLetters[position].setText(board.boardLetters[position].getText() + " "+ playerSymbol);
    }

    // ATUALIZA AS LETRAS DISPONIVEIS
    private void updateAllPlayersLetters(String firstPlayerLetters, String secondPlayerLetters) {
        List <String> firstPlayerLettersList =  Arrays.asList(firstPlayerLetters.split(","));
        List <String> secondPlayerLettersList =  Arrays.asList(secondPlayerLetters.split(","));

        if (playerName.equals("firstPlayer")) {
            board.updateGameLetters(firstPlayerLettersList, board.myGameLetters);
            board.updateGameLetters(secondPlayerLettersList, board.oponentsGameLetters);
        } else {
            board.updateGameLetters(secondPlayerLettersList, board.myGameLetters);
            board.updateGameLetters(firstPlayerLettersList, board.oponentsGameLetters);
        }
    }

    // DESISTENCIA DO JOGO
    private void playerGaveUpGame() {
        board.frame.setVisible(false);
        board.frame.dispose();
    }

    private void otherPlayerGaveUpGame() {
        int input = JOptionPane.showOptionDialog(board.frame, "O oponente desistiu do jogo!", "Atenção", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if (input == JOptionPane.OK_OPTION || input == JOptionPane.CLOSED_OPTION) {
            board.frame.dispatchEvent(new WindowEvent(board.frame, WindowEvent.WINDOW_CLOSING));
        }
    }

    // Mostra opcoes apos algum jogador vencer
    private void showWinnerMessage(String playerName) {
        String[] options = {"Jogar novamente", "Sair do jogo"};
        int x = JOptionPane.showOptionDialog(board.frame, "O Jogador " + playerName + " é o vencedor! Parabéns!!",
                "Vitória",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (x == 0) {
            restartGame();
        } else {
            board.frame.dispatchEvent(new WindowEvent(board.frame, WindowEvent.WINDOW_CLOSING));
        }
    }
}
