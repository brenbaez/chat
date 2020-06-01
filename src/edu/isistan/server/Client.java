package edu.isistan.server;

import edu.isistan.client.OperationClientFactory;
import edu.isistan.common.Protocol;
import edu.isistan.common.errorhandler.ConflictException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
                operationFactory(dis, type);
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

    public void operationFactory(DataInputStream dis, byte type) {
        createMapFactory(dis).get(type).accept(server);
    }

    private Map<Byte, Consumer<Server>> createMapFactory(DataInputStream dis) {
        Map<Byte, Consumer<Server>> operationFactory = new HashMap<>();
        operationFactory.put(Protocol.GENERAL_MSG, server -> generalMsg(dis));
        operationFactory.put(Protocol.PRIVATE_MSG, server -> privateMsg(dis));
        return operationFactory;
    }

    private void generalMsg(DataInputStream dis) {
        try {
            String text = dis.readUTF();
            server.sendGeneralMsg(userName, text);

        } catch (IOException e) {
            throw new ConflictException(OperationClientFactory.GENERAL_MSG_FAILED);
        }
    }

    private void privateMsg(DataInputStream dis) {
        try {
            String to = dis.readUTF();
            String text = dis.readUTF();
            server.sendPrivateMsg(userName, to, text);

        } catch (IOException e) {
            throw new ConflictException(OperationClientFactory.PRIVATE_MSG_FAILED);
        }
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
