package javacode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipserver.BlipServerAccessException;
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

public class CopyOfSPPService implements SppClientEventListener {
	private BlipNodeHandle blipNodeHandle;
	private SppClientHandler sppClientHandler;
	private ConnectData connectData;
	// private UUID sppUUID = new UUID("0000110100001000800000805F9B34FB");//for
	// exampriment
	private UUID sppUUID = new UUID("DF3C7DF603CA493EAD93F6905ACAF3CF");// for
																		// TalkingBadge
	UserDevice ud = null;
	private String destDeviceMac = null;
	private String commandContent = null;
	String message = "init message";
	boolean lock = true;

	public String sendCommand(String destDeviceMac, String commandContent) {
		//System.out.println("Send command to "+destDeviceMac);
		this.destDeviceMac = destDeviceMac;
		this.commandContent = commandContent;
		try {
			SearchDevice.startSearchingService();
		} catch (BlipServerAccessException e) {
			e.printStackTrace();
		} catch (BlipServerConnectionException e) {
			e.printStackTrace();
		}

		for (UserDevice device : SearchDevice.devicesList) {
			if (device.getCd().getTerminalID().toString().equals(destDeviceMac)) {
				ud = device;
				break;
			}
		}
		if (ud == null) {
			message = "Send command to " + destDeviceMac + ": failed, because the device can not be found.";
			// System.out.println(message);
			return message;
		}
		return "Send command to " + destDeviceMac + ": " + send();
	}

	public String send() {
		try {
			connectData = ud.getCd();
			blipNodeHandle = SearchDevice.server.getBlipNodeHandle(ud
					.getBlipMac());
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
				return e.toString();
			}
		} catch (BlipServerConnectionException e) {
			return e.toString();
		}
		while (lock) {
		}
		return message;
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
				byte[] readBuffer = (commandContent + "\r").getBytes();
				ByteArrayOutputStream repliedMessage = new ByteArrayOutputStream();
				Thread sendThread = new Thread(new StreamConnector(
						new ByteArrayInputStream(readBuffer), outputStream));
				sendThread.start();
				Thread receiveThread = new Thread(new StreamConnector(
						inputStream, repliedMessage));
				receiveThread.start();

				while (sendThread.isAlive()) {
				}
				message = "Sent to " + connectData.getTerminalID() + ": "
						+ commandContent;

				while (receiveThread.isAlive()) {
				}
				message += "\nReceived from " + connectData.getTerminalID()
						+ ": " + repliedMessage.toString();
				// System.out.println(message);
				
				close(this);
				lock = false;
			} catch (BlipServerConnectionException e) {
				handleBlipServerConnectionException(e);
			}
			break;
		case SppClientEvent.SERIAL_PORT_FAILED:
			message = "Failed to open serial port: "
					+ EventCause.causeName(evt.getCause());
			close(this);
			lock = false;
			break;
		case SppClientEvent.SERIAL_PORT_CLOSED:
			message = "Serial Port closed: "
					+ EventCause.causeName(evt.getCause());
			close(this);
			lock = false;
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

	private void handleBlipServerConnectionException(
			BlipServerConnectionException e) {
		System.out.println("Problem communicating with server: "
				+ e.getMessage());
		if (e.getCause() != null)
			System.out.println("Caused by: " + e.getCause().getMessage());
		System.exit(-1);
	}
	 private void close(CopyOfSPPService sppService) {
	        // Deregister event listeners
	        try {
	            sppClientHandler.removeEventListener(sppService);
	        } catch (BlipServerConnectionException e) {
	        	message+=e.toString();
	        }
	    }
}
