package com.blipsystems.blipnet.samples.ftpclient;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipnode.NoSuchSessionException;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;
import com.blipsystems.blipnet.api.bluetooth.BluetoothAddress;
import com.blipsystems.blipnet.api.event.EventCause;
import com.blipsystems.blipnet.api.event.EventFilter;
import com.blipsystems.blipnet.api.event.SessionEvent;
import com.blipsystems.blipnet.api.event.SessionEventListener;
import com.blipsystems.blipnet.api.profile.ProfileException;
import com.blipsystems.blipnet.api.profile.ftp.*;
import com.blipsystems.blipnet.samples.common.BlipNetSample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class FtpClientSample implements BlipNetSample, FtpClientEventListener, SessionEventListener {
    private BlipNodeHandle blipnode;
    private ConnectData connectData;
    private boolean sessionEstablished;
    private FtpFolderListing folderListing;
    private File f;

    public void startSample(BlipNodeHandle handle, ConnectData connectData) {
        this.blipnode = handle;
        this.connectData = connectData;

        EventFilter filter = new EventFilter(null, new BluetoothAddress[]{connectData.getTerminalID()});
        try {
            blipnode.addSessionEventListener(this, filter);
            blipnode.getFtpClientHandler().addEventListener(this, filter);
            blipnode.getFtpClientHandler().establishSession(connectData);
        } catch (BlipServerConnectionException e) {
            handleBlipServerConnectionException(e);
        } catch (ProfileException e) {
            handleProfileException("establish session", e);
        }

    }

    public void handleSessionEvent(SessionEvent evt) {
        int eventID = evt.getEventID();

        switch (eventID) {
            case SessionEvent.SESSION_CREATED:
                sessionEstablished = true;
                System.out.println("Session Established.");
                try {
                    blipnode.getFtpClientHandler().requestFolderListing(connectData);
                } catch (BlipServerConnectionException e) {
                    handleBlipServerConnectionException(e);
                } catch (FtpException e) {
                    handleProfileException("getting folderListing", e);
                }
                break;
            case SessionEvent.SESSION_CREATE_FAILED:
                System.out.println("Session establishment failed: " + EventCause.causeName(evt.getCause()));
                close();
                break;
            case SessionEvent.SESSION_REMOVED:
                sessionEstablished = false;
                System.out.println("Session removed: " + EventCause.causeName(evt.getCause()));
                close();
                break;
        }
    }

    private void close() {
        if (sessionEstablished) {
            try {
                blipnode.getFtpClientHandler().removeSession(connectData.getTerminalID());
            } catch (BlipServerConnectionException e) {
                handleBlipServerConnectionException(e);
            } catch (ProfileException e) {
                if (e.getCause() != null && (e.getCause() instanceof NoSuchSessionException)) {
                    sessionEstablished = false;
                    close();
                } else {
                    handleProfileException("Removing session", e);
                }
            }
        } else {
            try {
                blipnode.removeSessionEventListener(this);
                blipnode.getFtpClientHandler().removeEventListener(this);
            } catch (BlipServerConnectionException e) {
                // Ignore, we are closing down anyway
            }
            System.exit(0);
        }
    }

    private FileChannel fc;

    public void handleFtpClientEvent(FtpClientEvent evt) {
        int eventID = evt.getEventID();

        switch (eventID) {
            case FtpClientEvent.FOLDER_LISTING:
                folderListing = evt.getFolderListing();
                showSelectionMenu();
                break;
            case FtpClientEvent.FOLDER_LISTING_FAILED:
                System.out.println("Folder Listing failed. Cause is " + EventCause.causeName(evt.getCause()));
                close();
                break;
            case FtpClientEvent.PATH_CHANGED:
                try {
                    blipnode.getFtpClientHandler().requestFolderListing(connectData);
                } catch (BlipServerConnectionException e) {
                    handleBlipServerConnectionException(e);
                } catch (FtpException e) {
                    handleProfileException("getting folderListing", e);
                }
                break;
            case FtpClientEvent.PATH_CHANGE_FAILED:
                System.out.println("Path change failed. Cause is " + EventCause.causeName(evt.getCause()));
                close();
                break;
            case FtpClientEvent.OBJECT_FRAGMENT_RECEIVED:
                try {
                    if (fc == null) {
                        System.out.println("Creating file for receiving data: " + f.getAbsolutePath());
                        fc = new FileOutputStream(f).getChannel();
                    }

                    ByteBuffer buffer = ByteBuffer.wrap(evt.getObjectFragment());
                    System.out.println("Received " + buffer.remaining() + " bytes");
                    while (buffer.hasRemaining()) {
                        fc.write(buffer);
                    }

                    if (evt.isLastFragment()) {
                        System.out.println("No more fragments. Closing file");
                        fc.close();
                    }
                } catch (IOException e) {
                    System.out.println("Error storing incoming data: " + e.getMessage());
                    try {
                        blipnode.getFtpClientHandler().abortCurrentOperation(connectData.getTerminalID());
                    } catch (BlipServerConnectionException e1) {
                        handleBlipServerConnectionException(e1);
                    } catch (FtpException e1) {
                        handleProfileException("aborting current operation", e1);
                    }
                }
                break;
            case FtpClientEvent.OBJECT_RECEIVED:
                System.out.println("File received succesfully: " + evt.getObjectName());
                showSelectionMenu();
                break;
            case FtpClientEvent.OBJECT_PULL_FAILED:
                System.out.println("Failed to pull file: " + EventCause.causeName(evt.getCause()));
                showSelectionMenu();
                break;
        }
    }

    private void showSelectionMenu() {
        LinkedHashMap menuSelections = new LinkedHashMap();
        int index = 1;
        if (folderListing.hasParentFolder()) {
            menuSelections.put(new Integer(index++), null);
        }

        FtpFileDescriptor[] folders = folderListing.getFolders();
        for (int i = 0; i < folders.length; i++) {
            FtpFileDescriptor folder = folders[i];
            menuSelections.put(new Integer(index++), folder);
        }

        FtpFileDescriptor[] files = folderListing.getFiles();
        for (int i = 0; i < files.length; i++) {
            FtpFileDescriptor file = files[i];
            menuSelections.put(new Integer(index++), file);
        }

        menuSelections.put(new Integer(0), "Exit");

        for (Iterator iterator = menuSelections.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            System.out.println(entry.getKey() + ". " + (entry.getValue() == null ? ".." : entry.getValue()));
        }

        System.out.println();
        System.out.print("Enter selection: ");

        try {
            byte[] bytes = new byte[10];
            int read = System.in.read(bytes);
            String input = new String(bytes, 0, read - 1);

            int number = Integer.parseInt(input.trim());

            if (number == 0) {
                close();
                return;
            }
            System.out.println();

            FtpFileDescriptor ftpFileDescriptor = (FtpFileDescriptor) menuSelections.get(new Integer(number));
            if (ftpFileDescriptor != null) {
                if (ftpFileDescriptor.isFolder()) {
                    System.out.println("Changing folder to: " + ftpFileDescriptor);
                    try {
                        blipnode.getFtpClientHandler().setPath(connectData, ftpFileDescriptor.getName());
                    } catch (BlipServerConnectionException e) {
                        handleBlipServerConnectionException(e);
                    } catch (FtpException e) {
                        handleProfileException("setPath to " + ftpFileDescriptor.getName(), e);
                    }
                } else {
                    System.out.println("Getting " + ftpFileDescriptor);
                    try {
                        f = new File(ftpFileDescriptor.getName());
                        blipnode.getFtpClientHandler().getObject(connectData, ftpFileDescriptor.getName());
                    } catch (BlipServerConnectionException e) {
                        handleBlipServerConnectionException(e);
                    } catch (FtpException e) {
                        handleProfileException("get of " + ftpFileDescriptor.getName(), e);
                    }
                }
            } else {
                System.out.println("Changing folder to ..");
                try {
                    blipnode.getFtpClientHandler().setPath(connectData, "..");
                } catch (BlipServerConnectionException e) {
                    handleBlipServerConnectionException(e);
                } catch (FtpException e) {
                    handleProfileException("setPath to ..", e);
                }
            }
        } catch (IOException e) {
            System.out.println("IOException while reading input...");
            showSelectionMenu();
        }
    }

    private void handleProfileException(String operation, ProfileException e) {
        System.out.println("Error during " + operation + ": " + e.getMessage());
        if (e.getCause() != null) System.out.println("Caused by: " + e.getCause().getMessage());
        System.exit(-1);
    }

    private void handleBlipServerConnectionException(BlipServerConnectionException e) {
        System.out.println("Problem communicating with server: " + e.getMessage());
        if (e.getCause() != null) System.out.println("Caused by: " + e.getCause().getMessage());
        System.exit(-1);
    }
}
