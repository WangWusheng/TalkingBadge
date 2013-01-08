package com.blipsystems.blipnet.samples.pannap;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;
import com.blipsystems.blipnet.api.bluetooth.BluetoothAddress;
import com.blipsystems.blipnet.api.event.*;
import com.blipsystems.blipnet.api.profile.ProfileException;
import com.blipsystems.blipnet.samples.common.BlipNetSample;

import java.io.IOException;

public class PanNapSample implements BlipNetSample, SessionEventListener, IpAddressEventListener {
    private BlipNodeHandle blipnode;
    private BluetoothAddress terminalID;

    public void startSample(BlipNodeHandle handle, ConnectData connectData) {
        this.blipnode = handle;
        this.terminalID = connectData.getTerminalID();
        try {
            handle.addSessionEventListener(this);
            handle.addIpAddressEventListener(this);
            handle.getPanNapHandler().establishSession(connectData);
        } catch (BlipServerConnectionException e) {
            System.out.println("Problem communicating with server: " + e.getMessage());
            if (e.getCause() != null) System.out.println("Caused by: " + e.getCause().getMessage());
            System.exit(-1);
        } catch (ProfileException e) {
            System.out.println("Error establishing PAN NAP Session with " + connectData.getTerminalID() + ": " + e.getMessage());
            if (e.getCause() != null) System.out.println("Caused by: " + e.getCause().getMessage());
            close();
        }

    }

    public void handleSessionEvent(SessionEvent evt) {
        int eventID = evt.getEventID();

        switch (eventID) {
            case SessionEvent.SESSION_CREATED:
                System.out.println("Session established");
                new ManualDisconnecter().start();
                break;
            case SessionEvent.SESSION_CREATE_FAILED:
                System.out.println("Session establishment failed. Cause is " + EventCause.causeName(evt.getCause()));
                close();
                break;
            case SessionEvent.SESSION_REMOVED:
                System.out.println("Session removed. Cause is " + EventCause.causeName(evt.getCause()));
                close();
                break;
        }
    }

    public void handleIpAddressEvent(IpAddressEvent evt) {
        System.out.println("IP Address allocated for '" + evt.getTerminalID() + "' : " + evt.getTerminalIP());
    }

    private void close() {
        try {
            blipnode.removeSessionEventListener(this);
        } catch (BlipServerConnectionException e) {
            // We are exiting now anyway, so ignore
        }
        System.exit(0);
    }

    private class ManualDisconnecter extends Thread {
        public void run() {
            System.out.println("Press [ENTER] to disconnect terminal");
            try {
                System.in.read();
                System.out.println("Disconnecting terminal");
                try {
                    blipnode.getPanNapHandler().removeSession(terminalID);
                } catch (BlipServerConnectionException e) {
                    System.out.println("Problem communicating with server: " + e.getMessage());
                    if (e.getCause() != null) System.out.println("Caused by: " + e.getCause().getMessage());
                    System.exit(-1);
                } catch (ProfileException e) {
                    System.out.println("Error removing session: " + e.getMessage());
                    if (e.getCause() != null) System.out.println("Caused by: " + e.getCause().getMessage());
                    close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
