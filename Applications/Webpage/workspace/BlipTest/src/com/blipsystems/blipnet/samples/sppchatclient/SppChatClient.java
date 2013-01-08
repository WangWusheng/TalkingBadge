package com.blipsystems.blipnet.samples.sppchatclient;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipserver.BlipServer;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnection;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;
import com.blipsystems.blipnet.api.bluetooth.BluetoothAddress;
import com.blipsystems.blipnet.api.bluetooth.UUID;
import com.blipsystems.blipnet.api.event.EventFilter;
import com.blipsystems.blipnet.api.event.EventCause;
import com.blipsystems.blipnet.api.profile.ProfileException;
import com.blipsystems.blipnet.api.profile.spp.*;
import com.blipsystems.blipnet.api.util.BlipNetSecurityManager;
import com.blipsystems.blipnet.samples.common.BlipNetSample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This application implements a Simple Chat client for use with the BlipChat Server example.
 * <p/>
 * The client uses the BlipNet SPP Client API to connect to a BlipChat server. The Client BlipNode ID and server
 * BlipNode ID is specified in the constructor.
 * <p/>
 * When a connection is established, the SPP streams are connected to System.in and System.out.
 */
public class SppChatClient implements BlipNetSample, SppClientEventListener {
    private BlipNodeHandle blipNodeHandle;
    private SppClientHandler sppClientHandler;

    public void startSample(BlipNodeHandle handle, ConnectData connectData) {
        blipNodeHandle = handle;

        try {
            // Get a reference to the SPP Client Handler for this BlipNode
            sppClientHandler = blipNodeHandle.getSppClientHandler();

            // Add event listener to listener for SPP events
            System.out.println(connectData.getTerminalID());
            EventFilter filter = new EventFilter(null, new BluetoothAddress[]{connectData.getTerminalID()});
            sppClientHandler.addEventListener(this, filter);


            // Require the 128-bit UUID, that was specified when registering the server.
           // connectData.setRequiredServiceID(new UUID("0000110100001000800000805F9B34FB"));
            connectData.setRequiredServiceID(new UUID("1101000000001000800000805F9B34FB"));
            connectData.setServiceName("SPP");
            connectData.setRoleChangeAllowed(true);
            connectData.setAuthenticationRequired(false);

            // Establish SPP Client session using connect data
            try {
                sppClientHandler.establishSession(connectData);
            } catch (SppException e) {
                handleProfileException("establishing session", e);
            }
        } catch (BlipServerConnectionException e) {
            handleBlipServerConnectionException(e);
        }
    }

    // Implement the SppClientEventListener interface
    public void handleSppClientEvent(SppClientEvent evt) {
        switch (evt.getEventID()) {
            case SppClientEvent.SERIAL_PORT_READY:
                try {
                    // Connect the SPP streams to System.in and System.out
                    SppInputStream inputStream = evt.getSerialPort().getInputStream();
                    SppOutputStream outputStream = evt.getSerialPort().getOutputStream();
                    new Thread(new StreamConnector(System.in, outputStream)).start();
                    new Thread(new StreamConnector(inputStream, System.out)).start();
                } catch (BlipServerConnectionException e) {
                    handleBlipServerConnectionException(e);
                }
                break;
            case SppClientEvent.SERIAL_PORT_FAILED:
                System.out.println("Failed to open serial port: " + EventCause.causeName(evt.getCause()));
                close();
                break;
            case SppClientEvent.SERIAL_PORT_CLOSED:
                System.out.println("Serial Port closed: " + EventCause.causeName(evt.getCause()));
                close();
                break;
        }
    }

    // Helper class used to connect streams
    private class StreamConnector implements Runnable {
        private InputStream input;
        private OutputStream output;

        public StreamConnector(InputStream input, OutputStream output) {
            this.input = input;
            this.output = output;
        }

        public void run() {
            byte[] readBuffer = new byte[200];
            int bytesRead = 0;
            try {
                while ((bytesRead = input.read(readBuffer)) != -1) {
                    output.write(readBuffer, 0, bytesRead);
                }
            } catch (IOException e) {
            }
            // Input stream closed.
            try {
                output.close();
            } catch (IOException e) {
            } finally {
                // Exit
                close();
            }
        }
    }

    private void close() {
        // Deregister event listeners
        try {
            sppClientHandler.removeEventListener(this);
        } catch (BlipServerConnectionException e) {
        }
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 5) usage();

        // Install BlipNetSecurityManager, to disable all security policies (and enable dynamic class loading)
        System.setSecurityManager(new BlipNetSecurityManager());

        String user = args[0];
        String pass = args[1];
        String host = args[2];
        BluetoothAddress clientID = new BluetoothAddress(args[3]);
        BluetoothAddress serverID = new BluetoothAddress(args[4]);

        // Start the client
        SppChatClient client = new SppChatClient();
        BlipServerConnection server = BlipServer.getConnection(user, pass, host);
        BlipNodeHandle blipNodeHandle = server.getBlipNodeHandle(clientID);

        client.startSample(blipNodeHandle, new ConnectData(serverID));
    }

    private static void usage() {
        System.out.println("usage: SppChatServer <user> <pass> <host> <clientNode> <serverNode>");
        System.exit(0);
    }

    private void handleProfileException(String operation, ProfileException e) {
        System.out.println("Error during " + operation + ": " + e.getMessage());
        if (e.getCause() != null) System.out.println("Caused by: " + e.getCause().getMessage());
        close();
    }

    private void handleBlipServerConnectionException(BlipServerConnectionException e) {
        System.out.println("Problem communicating with server: " + e.getMessage());
        if (e.getCause() != null) System.out.println("Caused by: " + e.getCause().getMessage());
        System.exit(-1);
    }

}
