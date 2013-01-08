package com.blipsystems.blipnet.chatclient;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: claus
 * Date: 17-10-2004
 * Time: 02:32:59
 * To change this template use File | Settings | File Templates.
 */
public class ChatScreen extends Form implements CommandListener {
    private static final int LINE_SIZE = 64;

    private ChatClientMidlet midlet;
    private TextField sendString;
    private StringItem receivedString;
    private StringItem statusString;
    private Command sendCommand;
    private Command exitCommand;
    private StreamConnection streamConnection;
    private OutputStream outputStream;
    private InputStream inputStream;


    public ChatScreen(ChatClientMidlet midlet) {
        super("Chat Now");
        this.midlet = midlet;
        sendCommand = new Command("Send", Command.SCREEN, 1);
        exitCommand = new Command("Exit", Command.EXIT, 1);

        statusString = new StringItem("Status:", null);
        statusString.setLayout(Item.LAYOUT_TOP + Item.LAYOUT_NEWLINE_AFTER);
        receivedString = new StringItem("Chat Screen", null);
        sendString = new TextField("Send", "Hi All!", LINE_SIZE, TextField.ANY);
        sendString.setLayout(Item.LAYOUT_BOTTOM);

        append(statusString);
        append(receivedString);
        append(sendString);
        statusString.setText("Connecting");

        addCommand(sendCommand);
        addCommand(exitCommand);
        setCommandListener(this);
    }

    public void connect(String connectionURL) {
        try {
            streamConnection = (StreamConnection) Connector.open(connectionURL);

            inputStream = streamConnection.openInputStream();
            outputStream = streamConnection.openOutputStream();
            statusString.setText("Connected");


            ReadThread readThread = new ReadThread(inputStream);
            readThread.start();

        } catch (IOException e) {
            statusString.setText("Error connecting: " + e.getMessage());
        }

    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == sendCommand)
        {
            String sendData = sendString.getString()+"\n";
            writeData(sendData.getBytes());
        } else if (command == exitCommand) {
            exit();
        }
    }

    private void exit() {
        try {
            streamConnection.close();
        } catch (IOException e) {
        }
        midlet.destroyApp(false);
    }

    private void writeData(byte[] bytes) {
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            statusString.setText("Write Error: "+e.getMessage());
        }
    }

    class ReadThread extends Thread {
        InputStream inputStream = null;
        byte[] buffer = new byte[LINE_SIZE];

        public ReadThread(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public void run() {
            int count = 0;

            do {
                try {
                    inputStream.read(buffer, count, 1);
                    if (buffer[count] == 0x0d || count >= LINE_SIZE-1) {
                        statusString.setText("recieved "+count+" bytes");
                        receivedString.setText(new String(buffer, 0, count));
                        count = 0;
                    } else {
                        count++;
                    }
                } catch (IOException e) {
                    statusString.setText("Read Error: "+e.getMessage());
                }
            } while (true);
        }
    }

}
