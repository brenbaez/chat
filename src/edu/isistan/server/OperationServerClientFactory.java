package edu.isistan.server;

import edu.isistan.common.Protocol;
import edu.isistan.common.errorhandler.ConflictException;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static edu.isistan.common.MessageError.GENERAL_MSG_FAILED;
import static edu.isistan.common.MessageError.PRIVATE_MSG_FAILED;

public class OperationServerClientFactory {

    public static void operationServerFactory(DataInputStream dis, Server server, String userName) throws IOException {
        byte type = dis.readByte();
        createMapFactory(dis, userName).get(type).accept(server);
    }

    private static Map<Byte, Consumer<Server>> createMapFactory(DataInputStream dis, String userName) {
        Map<Byte, Consumer<Server>> operationFactory = new HashMap<>();
        operationFactory.put(Protocol.GENERAL_MSG, server -> generalMsg(dis, server, userName));
        operationFactory.put(Protocol.PRIVATE_MSG, server -> privateMsg(dis, server, userName));
        return operationFactory;
    }

    private static void generalMsg(DataInputStream dis, Server server, String userName) {
        try {
            String text = dis.readUTF();
            server.sendGeneralMsg(userName, text);

        } catch (IOException e) {
            throw new ConflictException(GENERAL_MSG_FAILED);
        }
    }

    private static void privateMsg(DataInputStream dis, Server server, String userName) {
        try {
            String to = dis.readUTF();
            String text = dis.readUTF();
            server.sendPrivateMsg(userName, to, text);

        } catch (IOException e) {
            throw new ConflictException(PRIVATE_MSG_FAILED);
        }
    }
}
