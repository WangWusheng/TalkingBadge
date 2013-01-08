package com.blipsystems.blipnet.samples.oppserver;

import com.blipsystems.blipnet.api.blipserver.*;
import com.blipsystems.blipnet.api.event.ServerEventFilter;
import com.blipsystems.blipnet.api.profile.opp.OppServerEvent;
import com.blipsystems.blipnet.api.profile.opp.OppServerEventListener;
import com.blipsystems.blipnet.api.util.BlipNetSecurityManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * The com.blipsystems.blipnet.samples.oppserver.ImageDisplay sample application registers an OPP Server event listener with the BlipServer, and
 * displays all incoming .jpg or .gif files on a full screen display.
 *
 * The display is automatically updated when a new image is received.
 *
 * At least one BlipNode should be connected to the server and be configured with the OPP server service record.
 *
 * To exit the application, press Alt+F4
 *
 * <textbf>NB!</textbf> if using Sony Ericsson Cell phones, the BlipNodes master/slave switch policy should be
 * set to <texttt>Not Supported</texttt>.
 */
public class ImageDisplay extends JFrame implements OppServerEventListener {

    private JLabel image;
    private BlipServerConnection serverConnection;

    public ImageDisplay(String user, String pass, String host) {
        // Install BlipNet security manager to allow dynamic classloading
        System.setSecurityManager(new BlipNetSecurityManager());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().setLayout(new GridBagLayout());
        image = new JLabel();
        getContentPane().add(image);
        getContentPane().setBackground(Color.BLACK);

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getScreenDevices()[0];

        boolean fullScreenSupported = device.isFullScreenSupported();
        setUndecorated(fullScreenSupported);
        setResizable(!fullScreenSupported);
        if (fullScreenSupported) {
            device.setFullScreenWindow(this);
            validate();
        } else {
            pack();
            Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            Dimension imageDisplaySize = getSize();
            setLocation((screenSize.width - imageDisplaySize.width) / 2, (screenSize.height - imageDisplaySize.height) / 2);
            setVisible(true);
        }

        try {
            // Get connection to BlipServer
            serverConnection = BlipServer.getConnection(user, pass, host);
            // Set eventfilter to CLIENT_PUSH_RECEIVED events only
            ServerEventFilter filter = new ServerEventFilter(new int[]{OppServerEvent.CLIENT_PUSH_RECEIVED}, null, null);
            // Register OPP Server event listener
            serverConnection.addOppServerEventListener(this, filter);
        } catch (BlipServerConnectionException e) {
            System.out.println("Connection to BlipServer lost");
            System.exit(1);
        } catch (BlipServerAccessException e) {
            System.out.println("Error accessing blipserver. Try with different user/pass");
            System.exit(1);
        }
    }

    public void handleOppServerEvent(OppServerEvent evt) {
        // Get the path of the incoming file
        File receivedFile = evt.getPath();
        int pos = receivedFile.getName().lastIndexOf(".");
        if (pos != -1) {
            // Check if extension is .jpg or .gif
            String extension = receivedFile.getName().substring(pos);
            if (extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".gif")) {
                byte[] imageBytes = new byte[0];
                try {
                    // Get the incoming image from the server
                    imageBytes = serverConnection.getObexObject(evt.getPath());
                    // Update display
                    image.setIcon(new ImageIcon(imageBytes));
                } catch (BlipServerConnectionException e) {
                    // Connection to server lost. Exit with error code 1
                    System.out.println("Connection to BlipServer lost");
                    System.exit(1);
                } catch (FileNotFoundException e) {
                    // This should never happen. If it does, just ignore this image
                }
            }
        }
    }

    public static void main(String[] args) {
        new ImageDisplay(args[0], args[1], args[2]);
    }
}
