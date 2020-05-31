package edu.isistan.client;

import edu.isistan.chat.ChatGUI;
import edu.isistan.chat.gui.MainWindows;
import edu.isistan.common.Protocol;
import edu.isistan.common.errorhandler.ConflictException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static edu.isistan.client.OperationClientFactory.operationFactory;

public class Client {

    public static final String FAILED_TO_OBTAIN_INPUT_STREAM_CODE = "Failed to obtain input stream code: {0}";

    public static void main(String[] args) {
        try {
            Socket s = new Socket(args[0], 6663);
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            ChatGUI gui = MainWindows.launchOrGet(new Callback(dos));
            startChat(s, gui);
            dos.writeByte(Protocol.HANDSHAKE);
            dos.writeUTF(args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startChat(Socket s, ChatGUI gui) {
        new Thread(() -> {
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream());
                while (true) {
                    byte type = dis.readByte();
                    operationFactory(gui, dis, type);

                }
            } catch (IOException e) {
                throw new ConflictException(MessageFormat.format(FAILED_TO_OBTAIN_INPUT_STREAM_CODE, 1));
            }
        }).start();
    }


}
