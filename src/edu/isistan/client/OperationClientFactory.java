package edu.isistan.client;

import edu.isistan.chat.ChatGUI;
import edu.isistan.common.errorhandler.ConflictException;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static edu.isistan.common.MessageError.ADD_USER_FAILED;
import static edu.isistan.common.MessageError.GENERAL_MSG_FAILED;
import static edu.isistan.common.MessageError.PRIVATE_MSG_FAILED;
import static edu.isistan.common.MessageError.REMOVE_USER_FAILED;
import static edu.isistan.common.Protocol.ADD_USER;
import static edu.isistan.common.Protocol.GENERAL_MSG;
import static edu.isistan.common.Protocol.PRIVATE_MSG;
import static edu.isistan.common.Protocol.REMOVE_USER;

public class OperationClientFactory {

    public static void operationFactory(ChatGUI gui, DataInputStream dis) throws IOException {
        byte type = dis.readByte();
        createMapFactory(dis).get(type).accept(gui);
    }

    private static Map<Byte, Consumer<ChatGUI>> createMapFactory(DataInputStream dis) {
        Map<Byte, Consumer<ChatGUI>> operationFactory = new HashMap<>();

        operationFactory.put(ADD_USER, chatGUI -> addUser(chatGUI, dis));

        operationFactory.put(REMOVE_USER, chatGUI -> removeUser(chatGUI, dis));

        operationFactory.put(GENERAL_MSG, chatGUI -> generalMsg(chatGUI, dis));

        operationFactory.put(PRIVATE_MSG, chatGUI -> privateMsg(chatGUI, dis));

        return operationFactory;
    }

    private static void privateMsg(ChatGUI gui, DataInputStream dis) {
        try {
            String user = dis.readUTF();
            String text = dis.readUTF();
            gui.addNewMsg(user, text);

        } catch (IOException e) {
            throw new ConflictException(PRIVATE_MSG_FAILED);
        }

    }

    private static void generalMsg(ChatGUI gui, DataInputStream dis) {
        try {
            String user = dis.readUTF();
            String text = dis.readUTF();
            gui.addNewGeneralMsg(user, text);

        } catch (IOException e) {
            throw new ConflictException(GENERAL_MSG_FAILED);
        }
    }

    private static void removeUser(ChatGUI gui, DataInputStream dis) {
        try {
            String user = dis.readUTF();
            gui.removeUser(user);

        } catch (IOException e) {
            throw new ConflictException(REMOVE_USER_FAILED);
        }
    }

    private static void addUser(ChatGUI gui, DataInputStream dis) {
        try {
            String user = dis.readUTF();
            gui.addUser(user);

        } catch (IOException e) {
            throw new ConflictException(ADD_USER_FAILED);
        }
    }
}
