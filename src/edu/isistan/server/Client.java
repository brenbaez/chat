package edu.isistan.server;

import edu.isistan.common.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static edu.isistan.server.OperationServerClientFactory.operationServerFactory;

public class Client implements Runnable {
    private Socket s;
    private Server server;
    private DataOutputStream dos;
    private String userName;

    public Client(Socket s, Server server) {
        this.s = s;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(this.s.getInputStream());
            dos = new DataOutputStream(this.s.getOutputStream());
            byte type = dis.readByte();
            if (!connectUser(dis, type)) return;
            //noinspection InfiniteLoopStatement
            while (true) {
                type = dis.readByte();
                operationServerFactory(dis, type, server, userName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (userName != null) {
                this.server.removeUser(userName);
            }
        }
    }

    public boolean connectUser(DataInputStream dis, byte type) throws IOException {
        if (type == Protocol.HANDSHAKE) {
            userName = dis.readUTF();
            if (!this.server.addClient(userName, this)) {
                userName = null;
                s.close();
                return false;
            }
        }
        return true;
    }

    public void removeUser(String userName) {
        try {
            this.dos.writeByte(Protocol.REMOVE_USER);
            this.dos.writeUTF(userName);
        } catch (IOException ignored) {

        }
    }

    public void addUser(String userName) {
        try {
            this.dos.writeByte(Protocol.ADD_USER);
            this.dos.writeUTF(userName);
        } catch (IOException ignored) {

        }
    }

    public void sendGeneralMsg(String userName, String text) {
        try {
            dos.writeByte(Protocol.GENERAL_MSG);
            dos.writeUTF(userName);
            dos.writeUTF(text);
        } catch (IOException ignored) {

        }
    }

    public void sendPrivateMsg(String userName, String text) {
        try {
            dos.writeByte(Protocol.PRIVATE_MSG);
            dos.writeUTF(userName);
            dos.writeUTF(text);
        } catch (IOException ignored) {
        }

    }
}
