package genie.talkingbadgeemulator.pc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class SPPService implements Runnable {

	// Create a UUID for SPP
	// static final UUID uuid = new UUID("0000110100001000800000805F9B34FB",
	// false);
	static final UUID uuid = new UUID("DF3C7DF603CA493EAD93F6905ACAF3CF", false);
	// Create the servicve url
	String connectionString = "btspp://localhost:" + uuid + ";name=SPP";
	UI ui = null;

	// start server
	SPPService(UI theUI) {
		this.ui = theUI;
	}

	public void run() {
		// open server url
		StreamConnectionNotifier streamConnNotifier;
		try {
			streamConnNotifier = (StreamConnectionNotifier) Connector
					.open(connectionString);
			while (true) {
				// Wait for client connection
				StreamConnection connection = streamConnNotifier
						.acceptAndOpen();
				RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);

				// read string from spp client
				InputStream inStream = connection.openInputStream();
				BufferedReader bReader = new BufferedReader(
						new InputStreamReader(inStream));
				String lineRead = bReader.readLine();
				ui.appendDisplayText("$Received command from "
						+ dev.getBluetoothAddress() + ": " + lineRead);

				// send response to spp client
				OutputStream outStream = connection.openOutputStream();
				PrintWriter pWriter = new PrintWriter(new OutputStreamWriter(
						outStream));
				String replyMessage = executeCommand(lineRead);
				pWriter.write(replyMessage);
				pWriter.flush();
				ui.appendDisplayText("$Replied message to " + dev.getBluetoothAddress()
						+ ": " + replyMessage);

				pWriter.close();
				inStream.close();
				outStream.close();
				connection.close();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String executeCommand(String lineRead) {
		String[] command = lineRead.split(" ");
		if (command[0].equals("TB")) {
			switch (command[1]) {
			case "PLAYSOUND":
				try {
					if (command.length != 3)
						return "BAD COMMAND";
					else {
						File file = new File("X\\" + command[2]);
						if (!file.exists())
							return "File not exist: " + command[2];
						return ui.control.playSoundFile(command[2]);
					}
				} catch (Exception e) {
					return "PLAYSOUND FAILED";
				}
			case "BATTERYCHECK":
				try {
					if (command.length != 2)
						return "BAD COMMAND";
					else {
						return ui.control.batteryCheck();
					}
				} catch (Exception e) {
					return "BATTERYCHECK FAILED";
				}
			case "VOLUMECHECK":
				try {
					if (command.length != 2)
						return "BAD COMMAND";
					else {
						return UI.getVolumeLevel();
					}
				} catch (Exception e) {
					return "VOLUMECHECK FAILED";
				}
			case "VOLUMESET":
				try {
					if (command.length != 3)
						return "BAD COMMAND";
					else {
						return ui.SetVolume(command[2]);
					}
				} catch (Exception e) {
					return "VOLUMESET FAILED";
				}
			case "FILEDELETE":
				try {
					if (command.length != 3) {
						System.out.println(command.length);
						return "BAD COMMAND";
					} else {
						File file = new File("X\\" + command[2]);
						if (!file.exists())
							return "File not exist: " + command[2];
						else if (file.delete())
							return "DELETE FILE " + command[2];
					}
				} catch (Exception e) {
					return "FILEDELETE FAILED";
				}
			case "FILELIST":
				try {
					if (command.length != 2)
						return "BAD COMMAND";
					else {
						File directory = new File("X\\");
						File[] files = directory.listFiles();
						String replyMessage = "";
						for (int i = 0; i < files.length; i++)
							replyMessage = replyMessage.concat(files[i]
									.getName() + " ");
						return replyMessage;
					}
				} catch (Exception e) {
					return "VOLUMESET FAILED";
				}
			case "FILENAMECHANGE":
				try {
					if (command.length != 4)
						return "BAD COMMAND";
					else {
						File file1 = new File("X\\" + command[2]);
						File file2 = new File("X\\" + command[3]);
						if (!file1.exists())
							return "File not exist: " + command[2];
						else if (file1.renameTo(file2))
							return command[2] + " BECOME " + command[3];
						else
							return "FILENAMECHANGE FAILED";
					}
				} catch (Exception e) {
					return "FILENAMECHANGE FAILED";
				}

			default:
				return "BAD COMMAND";
			}
		} else
			return "BAD COMMAND";
	}
}