package com.blipsystems.blipnet.samples.sppchatserver;

import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;
import com.blipsystems.blipnet.api.bluetooth.BluetoothAddress;
import com.blipsystems.blipnet.api.profile.spp.BluetoothSerialPort;
import com.blipsystems.blipnet.api.profile.spp.SppInputStream;
import com.blipsystems.blipnet.api.profile.spp.SppOutputStream;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * The ChatServerClient manages Input- and Output-Streams for a Connected client
 */
public class ChatServerClient {
    private BluetoothAddress terminalID;
    private MessageDispatcher messageDispatcher;
    private SppInputStream inputStream;
    private SppOutputStream outputStream;
    private Reader reader;
    private Logger log;

    private static final String CARRIAGE_RETURN = new String(new byte[]{13});
    private static final String LINE_FEED = new String(new byte[]{10});

    /**
     * Creates a reader thread to read from the InputStream and send messages to messageDispatcher
     */
    public ChatServerClient(BluetoothAddress terminalID, BluetoothSerialPort serialPort, MessageDispatcher dispatcher) throws BlipServerConnectionException {
        log = Logger.getLogger("ChatServerClient (" + terminalID + ")");
        this.terminalID = terminalID;
        this.messageDispatcher = dispatcher;

        // Get streams from BluetoothSerialPort
        try {
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        } catch (BlipServerConnectionException e) {
            // Error communicating with the server. Nothing we can do about it.
            // Re-throw exception
            throw e;
        }

        // Create reader and start thread
        reader = new Reader(this);
        new Thread(reader, "ChatClient$Reader (" + terminalID + ")").start();

        // Register with message dispatcher
        messageDispatcher.addClient(this);
        log.info("Registered with dispatcher");
    }

    /**
     * Worker class which reads and dispatces incoming messages from the InputStream
     */
    private class Reader implements Runnable {
        private ChatServerClient parent;
        private boolean keepRunning = true;

        public Reader(ChatServerClient parent) {
            this.parent = parent;
        }

        public void run() {
            log.info("Reader Thread started");

            byte[] buffer = new byte[2000];
            StringBuffer stringBuffer = new StringBuffer();

            try {
                while (keepRunning) {
                    // Read data from the InputStream. Detect EOL to seperate messages
                    int bytesRead = 0;
                    while (bytesRead != -1) {
                        bytesRead = inputStream.read(buffer);
                        if (bytesRead != -1) {
                            String message = new String(buffer, 0, bytesRead);
                            int newLinePos = -1;
                            if ((newLinePos = findLineEnd(message)) == -1) {
                                stringBuffer.append(message);
                            } else {
                                String messageEnd = message.substring(0, newLinePos);
                                stringBuffer.append(messageEnd);
                                log.info("Sending message to dispatcher: " + stringBuffer.toString());
                                messageDispatcher.addMessage(parent, stringBuffer.toString());
                                stringBuffer = new StringBuffer(message.substring(newLinePos + 1, message.length()));
                            }
                        }
                    }
                    log.info("InputStream closed");
                    break;
                }
            } catch (IOException e) {
                log.info("IOException from InputStream");
            }
            log.info("Reader thread stopped");
            if (keepRunning) {
                // We are here because there was a read error.
                // Inform messageDispatcher. Dispatcher will call tearDown()
                messageDispatcher.removeClient(parent);
            }
        }

        private int findLineEnd(String message) {
            int position = message.indexOf(CARRIAGE_RETURN);
            if (position == -1) position = message.indexOf(LINE_FEED);

            return position;
        }

        void stop() {
            keepRunning = false;
        }
    }

    public synchronized void write(String s) throws IOException {
        // Add EOL to message for compatibility with Serial Port Terminal Applications
        String tmpS = s + CARRIAGE_RETURN;
        // Write the string to the stream
        outputStream.write(tmpS.getBytes());
        log.info("Message written to stream: " + s);
    }

    public BluetoothAddress getTerminalID() {
        return terminalID;
    }

    void tearDown() {
        // Stop reader and close streams if it has not been done.
        reader.stop();

        try {
            inputStream.close();
        } catch (IOException e) {
        }

        try {
            outputStream.close();
        } catch (IOException e) {
        }
    }
}
