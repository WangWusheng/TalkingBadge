package com.blipsystems.blipnet.samples.sppchatserver;

import com.blipsystems.blipnet.api.bluetooth.BluetoothAddress;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Implements a message queue and a Single threaded message Queue which dispatches incoming messages from all
 * connected clients to all other clients.
 *
 * This file is not commented because there is no BlipNet Specific code in this file.
 */
public class MessageDispatcher {
    private LinkedList clientList;
    private LinkedList messageQueue;
    private Dispatcher dispatcher;
    private Logger log;

    public MessageDispatcher() {
        log = Logger.getLogger("MessageDispatcher");
        clientList = new LinkedList();
        messageQueue = new LinkedList();
        dispatcher = new Dispatcher();
        new Thread(dispatcher, "SPP ChatServer Message Dispatcher").start();
    }

    public void addClient(ChatServerClient client) {
        synchronized(clientList) {
            clientList.addLast(client);
            log.info("Client added: " + client.getTerminalID());
        }
    }

    public void removeClient(ChatServerClient client) {
        synchronized(clientList) {
            clientList.remove(client);
            client.tearDown();
            log.info("Client removed: " + client.getTerminalID());
        }
    }

    public void removeClient(BluetoothAddress terminalID) {
        synchronized(clientList) {
            for (Iterator iterator = clientList.iterator(); iterator.hasNext();) {
                ChatServerClient chatServerClient = (ChatServerClient) iterator.next();
                if (chatServerClient.getTerminalID().equals(terminalID)) {
                    iterator.remove();
                    chatServerClient.tearDown();
                    log.info("Client removed: " + terminalID);
                    break;
                }
            }
        }
    }

    public void addMessage(ChatServerClient sender, String message) {
        log.info("Adding message to queue");
        synchronized(messageQueue) {
            messageQueue.addLast(new MessageObject(sender, message));
            messageQueue.notify();
        }
    }

    private class Dispatcher implements Runnable {
        boolean keepRunning = true;

        public void run() {
            log.info("Dispatcher Thread started");
            while (keepRunning) {
                MessageObject message = null;

                synchronized(messageQueue) {
                    while(messageQueue.isEmpty()) {
                        log.info("Waiting for messages");
                        try {
                            messageQueue.wait();
                        } catch (InterruptedException e) {}
                    }
                    message = (MessageObject) messageQueue.removeFirst();
                }

                int dispatchCount = 0;
                String messageString = "[" + message.sender.getTerminalID() + "] " + message.contents;

                synchronized(clientList) {
                    // Go through list of clients and dispatch contents to each
                    for (Iterator iterator = clientList.iterator(); iterator.hasNext();) {
                        ChatServerClient client = (ChatServerClient) iterator.next();
                        if (client != message.sender) { // Don't dispatch contents to sender
                            try {
                                client.write(messageString);
                                dispatchCount++;
                            } catch (IOException e) {
                                log.info("Error writing to client. Removing it: " + client.getTerminalID());
                                iterator.remove();
                            }
                        }
                    }
                }

                log.info("Message dispatched to " + dispatchCount + " clients: " + messageString);
            }
            log.info("Dispatcher thread stopped");
        }

        void stop() {
            log.info("Stopping dispatcher thread");
            keepRunning = false;
        }
    }

    void tearDown() {
        dispatcher.stop();
    }

    private class MessageObject {
        public final ChatServerClient sender;
        public final String contents;

        MessageObject(ChatServerClient sender, String message) {
            this.sender = sender;
            this.contents = message;
        }
    }
}
