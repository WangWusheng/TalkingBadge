package com.blipsystems.blipnet.chatclient;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * Created by IntelliJ IDEA.
 * User: cela
 * Date: 15-10-2004
 * Time: 12:25:56
 * To change this template use File | Settings | File Templates.
 */
public class ChatClientMidlet extends MIDlet {


    private DiscoveryList discoveryList = null;
    private ChatScreen chatScreen;

    public ChatClientMidlet() {
        discoveryList = new DiscoveryList(this);
    }

    protected void startApp() throws MIDletStateChangeException {
        Display display = Display.getDisplay(this);
        display.setCurrent(discoveryList);
    }

    protected void startChat(String connectionURL) {
        chatScreen = new ChatScreen(this);
        Display.getDisplay(this).setCurrent(chatScreen);
        chatScreen.connect(connectionURL);

    }

    protected void exit() {
        destroyApp(false);
        notifyDestroyed();
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean b) {
    }

}
