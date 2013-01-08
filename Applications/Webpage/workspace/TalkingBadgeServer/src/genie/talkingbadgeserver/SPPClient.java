package genie.talkingbadgeserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;
import com.blipsystems.blipnet.api.bluetooth.BluetoothAddress;
import com.blipsystems.blipnet.api.bluetooth.UUID;
import com.blipsystems.blipnet.api.event.EventCause;
import com.blipsystems.blipnet.api.event.EventFilter;
import com.blipsystems.blipnet.api.profile.ProfileException;
import com.blipsystems.blipnet.api.profile.spp.SppClientEvent;
import com.blipsystems.blipnet.api.profile.spp.SppClientEventListener;
import com.blipsystems.blipnet.api.profile.spp.SppClientHandler;
import com.blipsystems.blipnet.api.profile.spp.SppException;
import com.blipsystems.blipnet.api.profile.spp.SppInputStream;
import com.blipsystems.blipnet.api.profile.spp.SppOutputStream;
import genie.talkingbadgeserver.UserDevice;;

public class SPPClient implements SppClientEventListener {
	private BlipNodeHandle blipNodeHandle;
	private SppClientHandler sppClientHandler;
	private ConnectData connectData;
	private UUID sppUUID = null;
	private String messageToBeSent = null;
	private UserInterface UI = null;

	public SPPClient(UserInterface ui) {
		UI = ui;
	}

	public void start() {
		try {

			UserDevice ud = (UserDevice) UserInterface.getDevicesList()
					.getSelectedValue();
			if (ud == null) {
				UserInterface
						.addReply("Send command failed, since no device is selected.");
				return;
			}
			connectData = ud.getCd();
			sppUUID = UI.getSppUUID();
			messageToBeSent = UserInterface.getCommandArea().getText();
			// Get a reference to the SPP Client Handler for this BlipNode
			blipNodeHandle = UserInterface.getServer().getBlipNodeHandle(
					ud.getBlipMac());
			sppClientHandler = blipNodeHandle.getSppClientHandler();

			// Add event listener to listener for SPP events
			EventFilter filter = new EventFilter(null,
					new BluetoothAddress[] { connectData.getTerminalID() });
			sppClientHandler.addEventListener(this, filter);

			// Require the 128-bit UUID, that was specified when registering the
			// server.
			connectData.setRequiredServiceID(sppUUID);
			connectData.setServiceName("SPP");
			connectData.setRoleChangeAllowed(true);
			connectData.setAuthenticationRequired(false);

			// Establish SPP Client session using connect data
			try {

				sppClientHandler.establishSession(connectData);
			} catch (SppException e) {
				handleProfileException("establishing session", e);
			} catch (ProfileException e) {
				e.printStackTrace();
			} finally {
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
				SppInputStream inputStream = evt.getSerialPort()
						.getInputStream();
				SppOutputStream outputStream = evt.getSerialPort()
						.getOutputStream();
				byte[] readBuffer = (messageToBeSent + "\r").getBytes();
				ByteArrayOutputStream repliedMessage = new ByteArrayOutputStream();
				;
				Thread sendThread = new Thread(new StreamConnector(
						new ByteArrayInputStream(readBuffer), outputStream));
				sendThread.start();
				Thread receiveThread = new Thread(new StreamConnector(
						inputStream, repliedMessage));
				receiveThread.start();

				while (sendThread.isAlive()) {
				}
				UserInterface.addReply("Sent to " + connectData.getTerminalID()
						+ ": " + messageToBeSent);

				while (receiveThread.isAlive()) {
				}
				UserInterface.addReply("Recieved from "
						+ connectData.getTerminalID() + ": "
						+ repliedMessage.toString());
				sppClientHandler.removeEventListener(this);
			} catch (BlipServerConnectionException e) {
				handleBlipServerConnectionException(e);
			}
			break;
		case SppClientEvent.SERIAL_PORT_FAILED:
			System.out.println("Failed to open serial port: "
					+ EventCause.causeName(evt.getCause()));
			break;
		case SppClientEvent.SERIAL_PORT_CLOSED:
			System.out.println("Serial Port closed: "
					+ EventCause.causeName(evt.getCause()));
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
		}
	}

	private void handleProfileException(String operation, ProfileException e) {
		System.out.println("Error during " + operation + ": " + e.getMessage());
		if (e.getCause() != null)
			System.out.println("Caused by: " + e.getCause().getMessage());
	}

	private void handleBlipServerConnectionException(
			BlipServerConnectionException e) {
		System.out.println("Problem communicating with server: "
				+ e.getMessage());
		if (e.getCause() != null)
			System.out.println("Caused by: " + e.getCause().getMessage());
		System.exit(-1);
	}

}
