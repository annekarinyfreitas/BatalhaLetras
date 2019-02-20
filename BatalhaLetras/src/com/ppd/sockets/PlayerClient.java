package com.ppd.sockets;

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
    String diceImagesPath = "/Users/annekarinysilvafreitas/Desktop/BatalhaLetras/";

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
                clientPrintWriter.println("chat"+ playerName + " : " + board.textToSend.getText());
                clientPrintWriter.flush();
                board.textToSend.setText("");
                board.textToSend.requestFocus();
            }
        });


        // EVENTO DE JOGAR O DADO
        board.diceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Gera uma face aleatoria do dado
                int num = ThreadLocalRandom.current().nextInt(1, 7);

                // Altera a imagem do dado
                ImageIcon img = new ImageIcon(diceImagesPath + num +".png");
                board.diceButton.setIcon(img);
                board.diceButton.setEnabled(false);

                // Envia para o servidor a atualização da posição do jogador
                clientPrintWriter.println("dice"+ playerName + num);
                clientPrintWriter.flush();
            }
        });

        // EVENTO ENVIAR PALAVRA DO JOGO
        board.sendGameWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Formato wordfirstPlayer,casa
                clientPrintWriter.println("word"+ playerName + ":" + board.selectedWord.getText());
                clientPrintWriter.flush();
                board.selectedWord.setText("");
                board.selectedWord.requestFocus();
                board.sendGameWordButton.setEnabled(false);

                // Passa o turno
                clientPrintWriter.println("turn"+playerName);
                clientPrintWriter.flush();
            }
        });

        // EVENTO PARA DESISTIR DO JOGO
        board.giveUpGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.frame.dispatchEvent(new WindowEvent(board.frame, WindowEvent.WINDOW_CLOSING));
            }
        });

        // EVENTO PARA REINICIAR A PARTIDA
        board.restartPlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientPrintWriter.println("rest");
                clientPrintWriter.flush();
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
                String messageBegin = text.substring(0,4);
                String messageWithoutBegin = text.substring(4);

                System.out.println("Recebido por " + playerName + " -> " + text);

                switch (messageBegin) {

                    // INICIO DO JOGO
                    case "init":
                        board.boardLog.setText("");
                        board.receivedText.setText("");

                    // CHAT
                    case "chat":
                        board.receivedText.append(messageWithoutBegin + "\n");
                        break;

                    // MENSAGENS DE ATUALIZACAO DO JOGO, QUE FICAM NA LATERAL DIREITA
                    case "game":
                        board.boardLog.append(messageWithoutBegin + "\n");
                        break;

                    // TROCA DE TURNOS
                    case "turn":
                        play(messageWithoutBegin.equals("play") ? true : false);
                        break;

                    // POSICIONAMENTO DOS JOGADORES NO TABULEIRO
                    case "dice":
                        updateBoardPosition(messageWithoutBegin);
                        break;

                    // ENVIO DA PALAVRA DA JOGADA
                    case "word":
                        updateAllLetters(messageWithoutBegin);
                        break;

                }
            }
        }
    }

    // ATIVA/DESATIVA BOTOES DE JOGAR
    private void play(boolean shouldPlay) {
        board.diceButton.setEnabled(shouldPlay);
        board.sendGameWordButton.setEnabled(shouldPlay);
    }

    //  ATUALIZA A POSICAO DOS JOGADORES NO TABULEIRO (EX: firstPlayer:0,secondPlayer:0)
    private void updateBoardPosition(String message) {
        String[] positionsArray = message.split(",");

        // Pega as posições de cada jogador
        String[] firstInfo = positionsArray[0].split(":");
        String[] secondInfo = positionsArray[1].split(":");
        int firstPosition = Integer.parseInt(firstInfo[1]);
        int secondPosition = Integer.parseInt(secondInfo[1]);

        // Reseta as letras para atualizar de acordo com a mensagem
        board.resetLetters();

        // Posiciona o 1 e 2 no tabuleiro
        board.boardLetters[firstPosition].setText(board.boardLetters[firstPosition].getText() + " (1)");
        board.boardLetters[secondPosition].setText(board.boardLetters[secondPosition].getText() + " (2)");
    }

    // ATUALIZA TODAS AS LETRAS DISPONIVEIS DO JOGADOR E DO OPONENTE (EX: A,B,C,D,E,F,G,H,I,J:A,B,C) antes do : é do jogador, e as depois é do oponente
    private void updateAllLetters(String message) {
        String[] messageArray = message.split(":");
        List <String> myLetters =  Arrays.asList(messageArray[0].split(","));
        List <String> opponentsLetters =  Arrays.asList(messageArray[1].split(","));

        // Envia os arrays com as letras para o tabuleiro
        board.updateGameLetters(myLetters, board.myGameLetters);
        board.updateGameLetters(opponentsLetters, board.oponentsGameLetters);
    }
}
