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

    Socket clientSocket;
    PrintWriter clientPrintWriter;
    Scanner clientReader;

    boolean playedDice = false;


    public static void main(String[] args) {
        new PlayerClient("firstPlayer");
        new PlayerClient("secondPlayer");
    }

    public PlayerClient(String playerName) {
        this.playerName = playerName;
        board = new Board(playerName);
        setupConnection();

        // Evento para enviar mensagem do chat
        board.sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientPrintWriter.println("chat"+ playerName + " : " + board.textToSend.getText());
                clientPrintWriter.flush();
                board.textToSend.setText("");
                board.textToSend.requestFocus();
            }
        });


        // Evento para Jogar o dado
        board.diceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int num = ThreadLocalRandom.current().nextInt(1, 7);

                // Altera a imagem do dado
                ImageIcon img = new ImageIcon("/Users/annekarinysilvafreitas/Desktop/BatalhaLetras/"+ num +".png");
                board.diceButton.setIcon(img);
                board.diceButton.setEnabled(false);

                // Envia para o servidor a atualização da posição do jogador
                clientPrintWriter.println("dice"+ playerName + num);
                clientPrintWriter.flush();
            }
        });

        // Evento para enviar a palavra do jogo para o oponente
        board.sendGameWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Formato wordfirstPlayer,casa
                clientPrintWriter.println("word"+ playerName + "," + board.selectedWord.getText());
                clientPrintWriter.flush();
                board.selectedWord.setText("");
                board.selectedWord.requestFocus();
                board.sendGameWordButton.setEnabled(false);

                // Passa o turno
                clientPrintWriter.println("turn"+playerName);
                clientPrintWriter.flush();
            }
        });

        // Evento para desistir do jogo
        board.giveUpGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.frame.dispatchEvent(new WindowEvent(board.frame, WindowEvent.WINDOW_CLOSING));
            }
        });

        // Evento para reiniciar a partida
        board.restartPlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientPrintWriter.println("rest");
                clientPrintWriter.flush();
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

    private void play() {
        board.diceButton.setEnabled(true);
        board.sendGameWordButton.setEnabled(true);
    }

    private void waitTurn() {
        board.diceButton.setEnabled(false);
        board.sendGameWordButton.setEnabled(false);
    }

    private void updateBoardPosition(String message) {
        // A mensagem vem no formato (firstPlayer pos1, secondPlayer pos2)
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

    private void updateAllLetters(String message) {
        String[] messageArray = message.split(":");
        List <String> myLetters =  Arrays.asList(messageArray[0].split(","));
        List <String> opponentsLetters =  Arrays.asList(messageArray[1].split(","));

        board.updateGameLetters(myLetters, board.myGameLetters);
        board.updateGameLetters(opponentsLetters, board.oponentsGameLetters);
    }

    private class ServerListener implements Runnable {
        @Override
        public void run() {
            String text;
            while ((text = clientReader.nextLine()) != null) {
                String messageBegin = text.substring(0,4);
                String messageWithoutBegin = text.substring(4);
                System.out.println("Recebido por " + playerName + " -> " + text);

                switch (messageBegin) {
                    // Chat
                    case "chat":
                        board.receivedText.append(messageWithoutBegin + "\n");
                        break;

                    // Mensagens gerais do jogo (que vão para o log)
                    case "game":
                        board.boardLog.append(messageWithoutBegin + "\n");
                        break;

                    // Troca de turnos entre os jogadores
                    case "turn":
                        if (messageWithoutBegin.equals("play")) {
                            play();
                        } else {
                            waitTurn();
                        }
                        break;

                    // Atualização do valor do dado e posicionamento dos jogadores no tabuleiro
                    case "dice":
                        updateBoardPosition(messageWithoutBegin);
                        break;

                    case "word":
                        updateAllLetters(messageWithoutBegin);
                        break;

                }
            }
        }
    }
}
