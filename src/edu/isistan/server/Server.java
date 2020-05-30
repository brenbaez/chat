package edu.isistan.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class
Server {

    private Map<String, Client> clients;
    private int port;

    public Server(int port) {
        this.port = port;
        this.clients = new HashMap<>();
    }

    public static void main(String[] args) {
        int port = 6663;
        Server server = new Server(port);
        server.listen();
    }

    public void listen() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            Executor executor = Executors.newCachedThreadPool();
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket s = serverSocket.accept();
                executor.execute(()-> new Client(s, this).run());
//                new Thread(new Client(s, this)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean addClient(String userName, Client client) {
        if (this.clients.containsKey(userName)) {
            return false;
        }
        this.clients.values().forEach(c -> c.addUser(userName));
        //noinspection Convert2MethodRef
        this.clients.keySet().forEach(i -> client.addUser(i));
        this.clients.put(userName, client);
        return true;
    }

    public synchronized void removeUser(String userName) {
        this.clients.remove(userName);
        this.clients.values().forEach(c -> c.removeUser(userName));
    }

    public synchronized void sendGeneralMsg(String userName, String text) {
        this.clients.entrySet().parallelStream().
                filter(e -> !e.getKey().equals(userName)).
                forEach(e -> e.getValue().sendGeneralMsg(userName, text));
    }

    //TODO agregamos esto (mejorar)
    public synchronized void sendPrivateMsg(String userName, String to, String text) {
        this.clients.entrySet().parallelStream().
                filter(e -> e.getKey().equals(to)).
                forEach(e -> e.getValue().sendPrivateMsg(userName, to, text));
    }
}
