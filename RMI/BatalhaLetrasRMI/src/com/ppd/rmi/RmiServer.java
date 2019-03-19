package com.ppd.rmi;

import java.rmi.RemoteException;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RmiServer {
    public static void main(String[] args) {
        // Permissões de segurança
        System.setProperty("java.security.policy","file:src/.java.policy");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        // Inicializa o objeto remoto do servidor e registra no servidor de nomes
        ServerInterface myRemoteObject = new ServerRemoteObject();
        try {
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(myRemoteObject,
                    0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("MyRemoteObject", stub);

            System.out.println("bound myremoteobject");
        } catch (Throwable cause) {
            System.out.println(cause.getMessage());
        }
    }
}

// OBJETO REMOTO
class ServerRemoteObject implements ServerInterface {
    private ArrayList<ClientInterface> clientInterfaces = new ArrayList<>();

    int firstPlayerPosition;
    int secondPlayerPosition;

    String[] firstPlayerLetters;
    String[] secondPlayerLetters;

    @Override
    public void registerRemotePlayerClient(ClientInterface clientInterface) throws RemoteException {
        clientInterfaces.add(clientInterface);

        if (clientInterfaces.size() == 2) {
            startGame();
        }
    }

    @Override
    public void startGame() throws RemoteException {
        // Ambos jogadores comecam da primeira letra
        firstPlayerPosition = 0;
        secondPlayerPosition = 0;

        // Ambos jogadores possuem todas as letras disponíveis
        firstPlayerLetters = new String[] {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        secondPlayerLetters = new String[] {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

        // Inicializacao do jogo e do turno
        clientInterfaces.get(0).play(true);
        clientInterfaces.get(1).play(false);

        for (int i = 0; i < this.clientInterfaces.size(); i++) {
            clientInterfaces.get(i).init();
            clientInterfaces.get(i).updateLetters(String.join(",",firstPlayerLetters), String.join(",",secondPlayerLetters));
            clientInterfaces.get(i).updatePosition(firstPlayerPosition, secondPlayerPosition);
            clientInterfaces.get(i).retrieveBoardMessage("É a vez do jogador " + RmiClient.firstPlayerIdentifier);
        }
    }

    @Override
    public void calculatePosition(String playerName, int newPosition) throws RemoteException {
        if (playerName.equals(RmiClient.firstPlayerIdentifier)) {
            firstPlayerPosition += newPosition;
            if (firstPlayerPosition > 25) {
                firstPlayerPosition = firstPlayerPosition - 26;
            }

            for (int i = 0; i < this.clientInterfaces.size(); i++) {
                clientInterfaces.get(i).retrieveBoardMessage("O jogador " + RmiClient.firstPlayerIdentifier + " anda " + newPosition + " casas!");
                clientInterfaces.get(i).updatePosition(firstPlayerPosition, secondPlayerPosition);
            }

        } else {
            secondPlayerPosition += newPosition;
            if (secondPlayerPosition > 25) {
                secondPlayerPosition = secondPlayerPosition - 26;
            }

            for (int i = 0; i < this.clientInterfaces.size(); i++) {
                clientInterfaces.get(i).retrieveBoardMessage("O jogador " + RmiClient.secondPlayerIdentifier + " anda " + newPosition + " casas!");
                clientInterfaces.get(i).updatePosition(firstPlayerPosition, secondPlayerPosition);
            }
        }
    }

    @Override
    public void sendPlayerWord(String playerName, String word) throws RemoteException {
        // Apaga as letras descritas nos arrays dos jogadores
        if (playerName.equals(RmiClient.firstPlayerIdentifier)) {
            secondPlayerLetters = removeWordFromLetters(word, secondPlayerLetters);

            //Detecta a vitoria
            if (secondPlayerLetters.length == 0) {
                for (int i = 0; i < this.clientInterfaces.size(); i++) {
                    clientInterfaces.get(i).showWinnerMessage(RmiClient.firstPlayerIdentifier);
                }
            }

            for (int i = 0; i < this.clientInterfaces.size(); i++) {
                clientInterfaces.get(i).retrieveBoardMessage("A palavra enviada por " + RmiClient.firstPlayerIdentifier + " é " + word);
            }

        } else {
            firstPlayerLetters = removeWordFromLetters(word, firstPlayerLetters);

            //Detecta a vitoria
            if (firstPlayerLetters.length == 0) {
                for (int i = 0; i < this.clientInterfaces.size(); i++) {
                    clientInterfaces.get(i).showWinnerMessage(RmiClient.secondPlayerIdentifier);
                }
            }

            for (int i = 0; i < this.clientInterfaces.size(); i++) {
                clientInterfaces.get(i).retrieveBoardMessage("A palavra enviada por " + RmiClient.secondPlayerIdentifier + " é " + word);
            }
        }

        clientInterfaces.get(0).updateLetters(String.join(",", firstPlayerLetters), String.join(",", secondPlayerLetters));
        clientInterfaces.get(1).updateLetters(String.join(",", secondPlayerLetters), String.join(",", firstPlayerLetters));
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

    @Override
    public void passTurn(String playerName) throws RemoteException {
        if (playerName.equals(RmiClient.firstPlayerIdentifier)) {
            clientInterfaces.get(0).play(false);
            clientInterfaces.get(1).play(true);
        } else {
            clientInterfaces.get(0).play(true);
            clientInterfaces.get(1).play(false);
        }
    }

    @Override
    public void giveUpGame(String playerName) throws RemoteException {
        if (playerName.equals(RmiClient.firstPlayerIdentifier)) {
            clientInterfaces.get(1).otherPlayerGaveUpGame();
        } else {
            clientInterfaces.get(0).otherPlayerGaveUpGame();
        }
    }

    @Override
    public void restartGame(String playerName) throws RemoteException {
        startGame();
    }

    @Override
    public void sendChatMessage(String message) throws RemoteException {
        for (int i = 0; i < this.clientInterfaces.size(); i++) {
            clientInterfaces.get(i).retrieveChatMessage(message);
        }
    }
}

// INTERFACE
interface ServerInterface extends Remote {
    void registerRemotePlayerClient(ClientInterface clientInterface) throws RemoteException;
    void startGame() throws RemoteException;
    void calculatePosition(String playerName, int newPosition) throws RemoteException;
    void sendPlayerWord(String playerName, String word) throws RemoteException;
    void passTurn(String playerName) throws RemoteException;
    void giveUpGame(String playerName) throws RemoteException;
    void restartGame(String playerName) throws RemoteException;
    void sendChatMessage(String message) throws RemoteException;
}