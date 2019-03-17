package com.ppd.rmi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiClient {
    public static void main(String[] args) {
        // Permissões de segurança
        System.setProperty("java.security.policy","file:src/.java.policy");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        // Se registra na localmente e recupera o stub do servidor
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1");
            ServerInterface stub = (ServerInterface) registry.lookup("MyRemoteObject");

            ClientInterface remoteClient1 = new ClientRemoteObject("P1", stub);
            ClientInterface remoteClient2 = new ClientRemoteObject("P2", stub);

            stub.registerRemotePlayerClient(remoteClient1);
            stub.registerRemotePlayerClient(remoteClient2);

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

    @Override
    public void retrieveChatMessage(String message) throws RemoteException {
        board.receivedText.append(message + "\n");
    }
}

// INTERFACE
interface ClientInterface extends Remote {
    void retrieveChatMessage(String message) throws RemoteException;
}
