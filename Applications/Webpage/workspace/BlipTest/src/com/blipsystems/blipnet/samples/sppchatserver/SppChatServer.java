package com.blipsystems.blipnet.samples.sppchatserver;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.BlipNodeHandleInUseException;
import com.blipsystems.blipnet.api.blipnode.BlipNodeSettings;
import com.blipsystems.blipnet.api.blipserver.BlipServer;
import com.blipsystems.blipnet.api.blipserver.BlipServerAccessException;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnection;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;
import com.blipsystems.blipnet.api.bluetooth.BluetoothAddress;
import com.blipsystems.blipnet.api.bluetooth.UUID;
import com.blipsystems.blipnet.api.profile.spp.SppException;
import com.blipsystems.blipnet.api.profile.spp.SppServerEvent;
import com.blipsystems.blipnet.api.profile.spp.SppServerEventListener;
import com.blipsystems.blipnet.api.profile.spp.SppService;
import com.blipsystems.blipnet.api.util.BlipNetSecurityManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;

/**
 * This sample application shows how to use the BlipNet SPP Server API for implementing a ChatServer.
 *
 * Before starting the ChatServer, use the BlipManager to add the BlipNodes that should act as ChatServers
 * to a BlipNodeGroup called "ChatServer".
 *
 * The SppChatServer will register an SPP service on all BlipNodes in this group, and dispatch incoming
 * messages to all connected clients.
 */
public class SppChatServer implements SppServerEventListener {
    private BlipServerConnection server;

    // Initialize the Service we want to register. An optional UUID is specified, which clients can search for to identify
    // this SPP Server. The UUID is a 128-bit identifier defined by the application developer.
    private static final SppService SERVICE = new SppService("BlipChat", new UUID("DEADBEEFCAFEBABEDEADBEEFCAFEBABE"));
    private Map blipnodeHandles = new HashMap();
    private MessageDispatcher messageDispatcher = new MessageDispatcher();
    private Logger log;

    public SppChatServer(String user, String pass, String host) throws BlipServerAccessException, BlipServerConnectionException {
        log = Logger.getLogger("SppChatServer");

        System.setSecurityManager(new BlipNetSecurityManager());

        try {
            // Get connection to server
            server = BlipServer.getConnection(user, pass, host);

            // Get all connected BlipNodes in the 'ChatServer' group
            BlipNodeHandle[] handleArray = server.getBlipNodeHandles("ChatServer", null, true, false);

            for (int i = 0; i < handleArray.length; i++) {
                BlipNodeHandle blipNodeHandle = handleArray[i];
                BluetoothAddress blipNodeID = blipNodeHandle.getBlipNodeID();
                try {
                    // Lock each handle to get exclusive rights to the SPP service
                    blipNodeHandle.lock();

                    // Make sure the BlipNode is Discoverable and Connectable
                    BlipNodeSettings blipNodeSettings = blipNodeHandle.getBlipNodeSettings();
                    blipNodeSettings.setDiscoverable(true);
                    blipNodeSettings.setConnectable(true);
                    blipNodeHandle.setBlipNodeSettings(blipNodeSettings);

                    // Add an SppServerEventListener to each handle
                    blipNodeHandle.getSppServerHandler().addEventListener(this);
                    // Register an SPP service on each blipnode
                    blipNodeHandle.getSppServerHandler().registerSppServer(SERVICE);

                    // Store the handles for later cleanup and to avoid locks being garbage collected.
                    blipnodeHandles.put(blipNodeID, blipNodeHandle);
                    log.log(Level.INFO, "Got BlipNodeHandle for " + blipNodeID);
                } catch (BlipNodeHandleInUseException e) {
                    // The requested BlipNodeHandle was locked by another application
                    // Solution: Create BlipNodeEventListener to claim lock when it is released
                    log.log(Level.WARNING, "BlipNodeHandle for '" + blipNodeID + "' is in use: " + e.getMessage());
                } catch (SppException e) {
                    // Another SPP server is already registered on this BlipNode.
                    // In this case the only possible cause (assuming we are using a unique username) is that our
                    // application was shut down abruptly the last time it ran, and the lock references have not
                    // timed out yet.
                    //
                    // We simply ignore the exception, because we have re-claimed the lock,
                    // and re-registered the event listener.
                    log.log(Level.WARNING, "Error registering SPP server on '" + blipNodeID + "': " + e.getMessage());
                }
            }
        } catch (BlipServerConnectionException e) {
            // Error connecting to the BlipServer. There is nothing we can do about that here.
            // Re-throw exception.
            throw e;
        } catch (BlipServerAccessException e) {
            // User name, password and/or host authentication failed. Nothing we can do about that here.
            // Re-throw exception.
            throw e;
        }
    }

    // Implement the handleSppServerEvent method from the SppServerEventListener interface
    // In this application we assume that service registration succeeds, so we just log most of the
    // events.
    public void handleSppServerEvent(SppServerEvent evt) {
        switch (evt.getEventID()) {
            case SppServerEvent.SERVICE_REGISTERED:
                {
                    log.log(Level.INFO, "Service registered successfully on " + evt.getBlipNodeID());
                } break;
            case SppServerEvent.SERVICE_DEREGISTERED:
                {
                    log.log(Level.INFO, "Service de-registered on " + evt.getBlipNodeID());
                } break;
            case SppServerEvent.SERVICE_REGISTRATION_FAILED:
                {
                    log.log(Level.WARNING, "Service registration failed on " + evt.getBlipNodeID());
                } break;
            case SppServerEvent.SERIAL_PORT_READY:
                {
                    // A new client has connected to our server.
                    log.log(Level.INFO, "Terminal '" + evt.getTerminalID() + "' connected to BlipNode '" + evt.getBlipNodeID() + "'");
                    // Create a threaded client object to manage the input/ouput streams and read messages from the client
                    try {
                        new ChatServerClient(evt.getTerminalID(), evt.getSerialPort(), messageDispatcher);
                    } catch (BlipServerConnectionException e) {
                        // Connection to server problem
                        log.log(Level.WARNING, "Connection Error while initializing client: " + e.getCause());
                        tearDown(-1);
                    }
                } break;
            case SppServerEvent.SERIAL_PORT_CLOSED:
                {
                    // A Client has disconnected from our server.
                    log.log(Level.INFO, "Terminal '" + evt.getTerminalID() + "' disconnected from BlipNode '" + evt.getBlipNodeID() + "'");

                    // Inform messageDispatcher
                    messageDispatcher.removeClient(evt.getTerminalID());
                } break;
        }
    }

    public static void main(String[] args) {
        if (args.length < 3) usage();
        String user = args[0];
        String pass = args[1];
        String host = args[2];

        try {
            SppChatServer sppServer = new SppChatServer(user, pass, host);

            System.out.println("Press [ENTER] to exit");
            try {
                System.in.read();
                sppServer.tearDown(0);
            } catch (IOException e) {
                e.printStackTrace();
                sppServer.tearDown(-1);
            }
        } catch (BlipServerAccessException e) {
            System.out.println("Authentication failed: " + e.getMessage());
        } catch (BlipServerConnectionException e) {
            System.out.println("Error connecting to server: " + e.getCause());
        }
    }

    // De-register all SPP servers, SppServerEventListeners, and release all BlipNodeHandles.
    private void tearDown(int returnCode) {
        log.log(Level.INFO, "Shutting down");

        messageDispatcher.tearDown();

        for (Iterator iterator = blipnodeHandles.values().iterator(); iterator.hasNext();) {
            BlipNodeHandle handle = (BlipNodeHandle) iterator.next();
            try {
                // De-register SPP service
                handle.getSppServerHandler().deregisterSppServer();
                // De-register event listener
                handle.getSppServerHandler().removeEventListener(this);
            } catch (SppException e) {
                // Ignore. We are tearing down
            } catch (BlipServerConnectionException e) {
                // Ignore. We are tearing down
            } finally {
                try {
                    // Make sure handle is released.
                    handle.release();
                } catch (BlipServerConnectionException e) {
                    // Ignore. Nothing we can do about it
                }
            }
        }

        log.log(Level.INFO, "Shutdown complete");
        System.exit(returnCode);
    }


    private static void usage() {
        System.out.println("usage: SppChatServer <user> <pass> <host>");
        System.exit(0);
    }
}
