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

public class SPPService implements SppClientEventListener {
	private BlipNodeHandle blipNodeHandle;
	private SppClientHandler sppClientHandler;
	private ConnectData connectData;
	// standard
	//private UUID sppUUID = new UUID("0000110100001000800000805F9B34FB");
	private UUID sppUUID = new UUID("DF3C7DF603CA493EAD93F6905ACAF3CF");

	UserDevice ud = null;
	private String commandContent = null;
	String message = "init message";
	boolean lock = true;

	public String sendCommand(String destDeviceMac, String commandContent) {
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
			message = "Send command to " + destDeviceMac
					+ ": FAILED, because the device can not be found.";
			return message;
		}
		return //"Send command to " + destDeviceMac + ": " + 
		send().replace("\r\nOVER\r\n", "");
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

			connectData.setRequiredServiceID(sppUUID);
			connectData.setServiceName("SPP");
			connectData.setRoleChangeAllowed(true);
			connectData.setAuthenticationRequired(false);

			// Establish SPP Client session using connect data
			try {

				sppClientHandler.establishSession(connectData);
			} catch (SppException e) {

				e.toString();
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
				message += "\nRecieved from " + connectData.getTerminalID()
						+ ": " + repliedMessage.toString();

				close(this);
				try {
					sppClientHandler.removeSession(connectData.getTerminalID());
				} catch (ProfileException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				lock = false;

			} catch (BlipServerConnectionException e) {
				handleBlipServerConnectionException(e);
			}
			break;
		case SppClientEvent.SERIAL_PORT_FAILED:
			message = "FAILED to open serial port: "
					+ EventCause.causeName(evt.getCause());
			close(this);
			lock = false;
			break;
		case SppClientEvent.SERIAL_PORT_CLOSED:
			message = "FAILED, caused by serial Port closed: "
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
					if (new String(readBuffer).contains("\r\nOVER\r\n")) {
						break;
					}
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

	private void close(SPPService sppService) {
		// Deregister event listeners
		try {
			// sppClientHandler.removeSession(connectData.getTerminalID());
			sppClientHandler.removeEventListener(sppService);
		} catch (Exception e) {
			message += e.toString();
			System.out.println(e.toString());
		}
	}
}
