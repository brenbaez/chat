package edu.isistan.client;

import edu.isistan.chat.ChatGUI;
import edu.isistan.chat.gui.MainWindows;
import edu.isistan.common.MessageError;
import edu.isistan.common.Protocol;
import edu.isistan.common.errorhandler.ConflictException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.MessageFormat;

import static edu.isistan.client.OperationClientFactory.operationFactory;
import static edu.isistan.common.MessageError.FAILED_TO_OBTAIN_INPUT_STREAM_CODE;

public class Client {

    public static void main(String[] args) {
        try {
            Socket s = new Socket(args[0], 6662);
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            ChatGUI gui = MainWindows.launchOrGet(new Callback(dos));
            startChat(s, gui);
            dos.writeByte(Protocol.HANDSHAKE);
            dos.writeUTF(args[1]);
        } catch (IOException e) {
            throw new ConflictException(MessageError.PUERTO_INACCESIBLE);
        }
    }

    private static void startChat(Socket s, ChatGUI gui) {
        new Thread(() -> {
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream());
                while (true)
                    operationFactory(gui, dis);
            } catch (IOException e) {
                throw new ConflictException(MessageFormat.format(FAILED_TO_OBTAIN_INPUT_STREAM_CODE, 1));
            }
        }).start();
    }


}
