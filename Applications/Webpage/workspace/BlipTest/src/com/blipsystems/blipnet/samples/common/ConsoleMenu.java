package com.blipsystems.blipnet.samples.common;

import com.blipsystems.blipnet.api.blipnode.ConnectData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class ConsoleMenu {
    private HashMap devices;

    public ConsoleMenu(HashMap devices) {
        this.devices = devices;
    }

    public ConnectData getSelection() {
        LinkedList menuOrder = new LinkedList();
        int index = 1;
        for (Iterator iterator = devices.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            ConnectData cd = (ConnectData) entry.getKey();
            String terminalName = (String) entry.getValue();
            menuOrder.addLast(cd);
            System.out.println(index++ + ". " + terminalName + " (" + cd.getTerminalID().getColonSeperatedUpperCaseString() + ")\n");
        }
        System.out.println();
        System.out.print("Enter the number of the terminal you want to connect to: ");
        try {
            byte[] bytes = new byte[10];
            int read = System.in.read(bytes);
            String input = new String(bytes, 0, read - 1);

            int number = Integer.parseInt(input.trim());

            ConnectData cData = (ConnectData) menuOrder.get(number-1);
            System.out.println();
            return cData;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
