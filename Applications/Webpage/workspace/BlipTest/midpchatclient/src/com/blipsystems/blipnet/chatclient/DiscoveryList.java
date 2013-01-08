package com.blipsystems.blipnet.chatclient;

import javax.bluetooth.*;
import javax.microedition.lcdui.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: cela
 * Date: 15-10-2004
 * Time: 12:25:56
 * To change this template use File | Settings | File Templates.
 */
public class DiscoveryList extends List implements CommandListener, DiscoveryListener {

    private final static int DEFAULT_COMMAND_PRIORITY = 1;

    private Alert alert = new Alert("BlipChat");
    private final static Command EXIT_COMMAND = new Command("Exit", Command.EXIT, DEFAULT_COMMAND_PRIORITY);
    private final static Command SEARCH_COMMAND = new Command("Search", Command.SCREEN, DEFAULT_COMMAND_PRIORITY);
    private final static String CHAT_SERVER_NAME = "BlipChat";

    /* Bluetooth defines */
    private final static int MAJOR_CLASS_ACCESS_POINT = 0x0300;
    private final static int ATTRIBUTE_SERVICE_NAME = 0x0100;
    private final static String UUID_SPP = "1101";

    private RemoteDevice[] devices = new RemoteDevice[10];
    private DiscoveryAgent discoveryAgent = null;
    private String connectionURL = null;

    private int count = 0;
    private ChatClientMidlet midlet;
    private Image blipNodeImage;

    public DiscoveryList(ChatClientMidlet midlet) {
        super("BlipChat", List.IMPLICIT);
        this.midlet = midlet;
        alert.setTimeout(Alert.FOREVER);
        try {
            blipNodeImage = Image.createImage("/blipnode.png");
        } catch (IOException e) {
            showError("Failed to load blipnode image", e.getMessage());
        }

        addCommand(SEARCH_COMMAND);
        addCommand(EXIT_COMMAND);
        setCommandListener(this);

    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == EXIT_COMMAND) {
            midlet.exit();
        } else if (command == SEARCH_COMMAND) {
            try {
                LocalDevice localDevice = LocalDevice.getLocalDevice();
                discoveryAgent = localDevice.getDiscoveryAgent();
                discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
                setTitle("Searching");

            } catch (BluetoothStateException e) {
                showError("Failed to start Inquiry", e.getMessage());
                return;
            }
        } else if (command == List.SELECT_COMMAND) {
            int selectedIndex = getSelectedIndex();

            int[] attrSet = {ATTRIBUTE_SERVICE_NAME};
            UUID[] uuids = new UUID[1];
            uuids[0] = new UUID(UUID_SPP, true);

            try {
                discoveryAgent.searchServices(attrSet, uuids, devices[selectedIndex], this);
            } catch (BluetoothStateException e) {
                showError("Failed to start service search", e.getMessage());
            }
        }


    }

    public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
        if (deviceClass.getMajorDeviceClass() == MAJOR_CLASS_ACCESS_POINT) {
            // We only wants Access Points
            if (count == 1) {
                addCommand(List.SELECT_COMMAND);
            }
            append(remoteDevice.getBluetoothAddress(), blipNodeImage);
            devices[count++] = remoteDevice;
        }
    }

    public void inquiryCompleted(int i) {
        if (count == 0) {
            removeCommand(List.SELECT_COMMAND);
            showError("Inquiry Error", "No BlipNodes Discovered");
        }
        setTitle("BlipChat");
        count = 0;
    }

    public void servicesDiscovered(int i, ServiceRecord[] serviceRecords) {

        for (int j = 0; j < serviceRecords.length; j++) {
            ServiceRecord serviceRecord = serviceRecords[j];
            DataElement attributeValue = serviceRecord.getAttributeValue(ATTRIBUTE_SERVICE_NAME);
            if (attributeValue != null) {
                if (((String) attributeValue.getValue()).equals(CHAT_SERVER_NAME)) {
                    connectionURL = serviceRecords[0].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                    break;
                }
            }
        }
    }

    public void serviceSearchCompleted(int i, int i1) {
        if (connectionURL != null) {
            midlet.startChat(connectionURL);
        } else {
            showError("Service Search error", "Remote device does not have a BlipChat Server");
        }

    }


    private void showError(String title, String text) {
        alert.setTitle(title);
        alert.setString(text);
        alert.setType(AlertType.ERROR);
        Display.getDisplay(midlet).setCurrent(alert, this);
    }


}
