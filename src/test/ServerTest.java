package test;

import edu.isistan.client.Client;
import edu.isistan.server.Server;

public class ServerTest {

    public static void main(String[] args) {
        new Thread(() -> Server.main(new String[]{})).start();
        System.out.println("PASOOOOO");
        Thread t = new Thread(() -> {
            Client.main(new String[]{"localhost", "juan"});
        });
        t.start();
        t = new Thread(() -> {
        });
        t.start();
        t = new Thread(() -> {
        });
        t.start();
        t = new Thread(() -> {
        });
        t.start();
        t = new Thread(() -> {
        });
        t.start();
        t = new Thread(() -> {
        });
        t.start();
    }

}
