package com.blipsystems.blipnet.samples.statscollector;

import com.blipsystems.blipnet.api.blipserver.BlipServer;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnection;
import com.blipsystems.blipnet.api.bluetooth.Session;
import com.blipsystems.blipnet.api.event.SessionEvent;
import com.blipsystems.blipnet.api.event.SessionEventListener;
import com.blipsystems.blipnet.api.util.BlipNetSecurityManager;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * This Sample Application demonstrates a simple session statistics collector.
 * The application will log information every time a Session is estabished or removed to/from any terminal connected
 * to any BlipNode on the BlipServer.
 * <p/>
 * The information beeing logged is:
 * - start time:     The date/time the session was established
 * - end time:       The date/time the session was removed
 * - session type:   The Session type, ie. Opp Client, Opp Server, Pan, Lan, etc.
 * - terminal id:    The Bluetooth Address of the Terminal
 * - bytes sent:     The number of bytes the terminal has sent during the session
 * - bytes received: The number of bytes the terminal has received during the session
 */
public class StatisticsCollector implements SessionEventListener {

    private static Logger log = Logger.getLogger("com.blipsystems.blipnet.samples.statscollector");

    public StatisticsCollector(String username, String password, String hostname) throws Exception {
        // Get a BlipServer connection to localhost.
        BlipServerConnection conn = BlipServer.getConnection(username, password, hostname);
        // Register this class to recieve all session events from the BlipServer
        conn.addSessionEventListener(this);
    }

    /*
     * Implements the SessionEventListerner. When a Session evt is received the statistics are extracted from
     * the SessionEvent and the result is logged to the Logger
     */
    public void handleSessionEvent(SessionEvent evt) {
        switch (evt.getEventID()) {
            case SessionEvent.SESSION_CREATED:
                log.info(evt.getStatistics().getStartTime() + " - " +
                        Session.getFriendlyName(evt.getSessionType()) + " Session Created - Terminal ID: " +
                        evt.getTerminalID().getColonSeperatedUpperCaseString());
                break;
            case SessionEvent.SESSION_REMOVED:
                log.info(evt.getStatistics().getEndTime() + " - " +
                        Session.getFriendlyName(evt.getSessionType()) + " Session Removed - Terminal ID: " +
                        evt.getTerminalID().getColonSeperatedUpperCaseString() + " - Bytes Sent: " +
                        evt.getStatistics().getBytesSent() + " - Bytes Received: " +
                        evt.getStatistics().getBytesReceived());
                break;
        }
    }

    public static void main(String[] args) throws Exception {
        System.setSecurityManager(new BlipNetSecurityManager());
        log.setUseParentHandlers(false);
        // Create a simple Console Logger with a secuence number and the logged text string.
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            public String format(LogRecord record) {
                return record.getSequenceNumber() + " " + record.getMessage() + "\n";
            }
        });
        log.addHandler(consoleHandler);

        new StatisticsCollector(args[0], args[1], args[2]);
    }

}
