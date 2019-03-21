package com.ppd.rmi;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RmiClient {
    static PlayerConnection playerConnection;

    public static void main(String[] args) {
        playerConnection = new PlayerConnection();
        RmiClient.connectionEvent();
    }

    static void connectionEvent() {
        playerConnection.conectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = playerConnection.ipTextField.getText();
                String name = playerConnection.playerNameTextField.getText();
                RmiClient.registerToServer(ip, name);

                playerConnection.frame.setVisible(false);
                playerConnection.frame.dispose();
            }
        });
    }

    static void registerToServer(String ip, String playerName) {
        // Permissões de segurança
        System.setProperty("java.security.policy","file:src/.java.policy");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        // Se registra na localmente e recupera o stub do servidor
        try {
            Registry registry = LocateRegistry.getRegistry(ip);
            ServerInterface stub = (ServerInterface) registry.lookup("BatalhaLetras");

            ClientInterface remoteClient1 = new ClientRemoteObject(playerName, stub);
            stub.registerRemotePlayerClient(remoteClient1, playerName);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}

// OBJETO REMOTO
class ClientRemoteObject extends UnicastRemoteObject implements ClientInterface {
    String playerName;
    Board board;
    ServerInterface serverInterface;

    public ClientRemoteObject(String playerName, ServerInterface serverInterface) throws RemoteException{
        this.playerName = playerName;
        this.serverInterface = serverInterface;
        this.board = new Board(playerName);

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

    // ENVIA MENSAGEM CHAT
    private void sendChatMessage() {
        try {
            serverInterface.sendChatMessage(playerName + ":" + board.textToSend.getText());
            board.textToSend.setText("");
            board.textToSend.requestFocus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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

        try {
            // Envia para o servidor a atualização da posição do jogador
            serverInterface.calculatePosition(playerName, num);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // ENVIA A PALAVRA DA JOGADA
    private void sendGameWord() {
        try {
            // Envia para o servidor a palavra jogada
            serverInterface.sendPlayerWord(playerName,board.selectedWord.getText() );

            // Passa o turno
            serverInterface.passTurn(playerName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        board.selectedWord.setText("");
        board.selectedWord.requestFocus();
        board.sendGameWordButton.setEnabled(false);
    }

    // DESISTE DO JOGO
    private void giveUpGame() {
        try {
            playerGaveUpGame();
            serverInterface.giveUpGame(playerName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // REINICIA A PARTIDA
    private void restartGame() {
        try {
            serverInterface.restartGame(playerName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updatePosition(String playerName, int position) {
        board.boardLetters[position].setText(board.boardLetters[position].getText() + " "+ playerSymbol(playerName));
    }

    private String playerSymbol(String playerName) {
        return "("+ playerName.substring(0, Math.min(playerName.length(), 3)) + ")";
    }

    /////////////////////////
    //// METODOS DA INTERFACE
    /////////////////////////

    @Override
    public void init() throws RemoteException {
        board.boardLog.setText("");
        board.receivedText.setText("");
    }

    @Override
    public void play(boolean shouldPlay) throws RemoteException {
        board.diceButton.setEnabled(shouldPlay);
        board.sendGameWordButton.setEnabled(false);
    }

    @Override
    public void updateLetters(String firstPlayerLetters, String secondPlayerLetters) throws RemoteException {
        List <String> firstPlayerLettersList =  Arrays.asList(firstPlayerLetters.split(","));
        List <String> secondPlayerLettersList =  Arrays.asList(secondPlayerLetters.split(","));

        board.updateGameLetters(firstPlayerLettersList, board.myGameLetters);
        board.updateGameLetters(secondPlayerLettersList, board.oponentsGameLetters);
    }

    @Override
    public void updatePosition(String firstPlayerName, String secondPlayerName, int firstPlayerPosition, int secondPlayerPosition) throws RemoteException {
        board.resetLetters();
        updatePosition(firstPlayerName, firstPlayerPosition);
        updatePosition(secondPlayerName, secondPlayerPosition);
    }

    @Override
    public void retrieveChatMessage(String message) throws RemoteException {
        board.receivedText.append(message + "\n");
    }

    @Override
    public void retrieveBoardMessage(String message) throws RemoteException {
        board.boardLog.append(message + "\n");
    }

    public void playerGaveUpGame() {
        board.frame.setVisible(false);
        board.frame.dispose();
    }

    @Override
    public void otherPlayerGaveUpGame() throws RemoteException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int input = JOptionPane.showOptionDialog(board.frame, "O oponente desistiu do jogo!", "Atenção", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

                if (input == JOptionPane.OK_OPTION || input == JOptionPane.CLOSED_OPTION) {
                    board.frame.dispatchEvent(new WindowEvent(board.frame, WindowEvent.WINDOW_CLOSING));
                }
            }
        });
    }

    @Override
    public void showWinnerMessage(String playerName) throws RemoteException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
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
        });
    }
}

// INTERFACE
interface ClientInterface extends Remote {
    void init() throws RemoteException;
    void play(boolean shouldPlay) throws RemoteException;
    void updateLetters(String firstPlayerLetters, String secondPlayerLetters) throws RemoteException;
    void updatePosition(String firstPlayerName, String secondPlayerName, int firstPlayerPosition, int secondPlayerPosition) throws RemoteException;
    void retrieveChatMessage(String message) throws RemoteException;
    void retrieveBoardMessage(String message) throws RemoteException;
    void otherPlayerGaveUpGame() throws RemoteException;
    void showWinnerMessage(String playerName) throws RemoteException;
}

