package com.ppd.rmi;

import java.rmi.RemoteException;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

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

    @Override
    public void registerRemotePlayerClient(ClientInterface clientInterface) throws RemoteException {
        this.clientInterfaces.add(clientInterface);
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
    void sendChatMessage(String message) throws RemoteException;
}