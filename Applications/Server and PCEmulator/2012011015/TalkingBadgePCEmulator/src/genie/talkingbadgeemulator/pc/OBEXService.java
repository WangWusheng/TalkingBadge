package genie.talkingbadgeemulator.pc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;
import javax.obex.ServerRequestHandler;
import javax.obex.SessionNotifier;

public class OBEXService implements Runnable {

	UI ui = null;
	static final String serverUUID = "0000110500001000800000805F9B34FB";

	OBEXService(UI theUI) {
		ui = theUI;
	}

	private class RequestHandler extends ServerRequestHandler {
		public int onPut(Operation op) {
			try {
				HeaderSet hs = op.getReceivedHeaders();
				String name = (String) hs.getHeader(HeaderSet.NAME);
				File receivedFile = new File("X\\" + name);
				if (receivedFile.exists()) {
					receivedFile.createNewFile();
				}
				receivedFile.setWritable(true);
				FileOutputStream out = new FileOutputStream(
						receivedFile.getAbsolutePath());

				InputStream is = op.openInputStream();

				int data;
				while ((data = is.read()) != -1) {
					out.write(data);
				}

				out.close();
				is.close();

				ui.appendDisplayText("@Received file: " + name);
				op.close();
				return ResponseCodes.OBEX_HTTP_OK;
			} catch (IOException e) {
				e.printStackTrace();
				return ResponseCodes.OBEX_HTTP_UNAVAILABLE;
			}
		}
	}

	public void run() {
		try {
			LocalDevice.getLocalDevice().setDiscoverable(DiscoveryAgent.GIAC);
			SessionNotifier serverConnection = (SessionNotifier) Connector
					.open("btgoep://localhost:" + serverUUID + ";name=OBEX");
			while (true) {
				RequestHandler handler = new RequestHandler();
				serverConnection.acceptAndOpen(handler);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
