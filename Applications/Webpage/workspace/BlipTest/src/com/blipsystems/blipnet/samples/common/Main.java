package com.blipsystems.blipnet.samples.common;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipserver.BlipServer;
import com.blipsystems.blipnet.api.blipserver.BlipServerAccessException;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnection;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;
import com.blipsystems.blipnet.api.util.BlipNetSecurityManager;

import java.util.HashMap;

public class Main {
	private static final String username = "Administrator";
	private static final String password = "BLIPNET";
	private static final String hostname = "cerebro.itu.dk";
	// private static final String hostname = "130.226.133.62";
	private static final String GROUP_NAME = "BlipNet";
	//private static final String CONFIG_NAME = "ITU BlipZones Inquiry node";//E, node4c-inquirer
	//private static final String CONFIG_NAME = "ITU BlipZones Receiver Node";// ITU-4C
	// private static final String CONFIG_NAME ="ITU BlipZones Sender Node";//D, node4c-1
	private static final String CONFIG_NAME = "BlipZones Inquiry and Data Node";

	private String sampleName = "oppclient.OppClientPushSample";
	private BlipServerConnection server;
	private BlipNodeHandle blipnode;

	public Main(String[] args) { // TODO

		try {
			server = BlipServer.getConnection(username, password, hostname);

			BlipNodeHandle[] blipNodeHandles = server.getBlipNodeHandles(
					GROUP_NAME, CONFIG_NAME, true, false);

			System.out.println(server.getBlipServerName());
			if (blipNodeHandles.length != 0 && blipNodeHandles[0] != null) {
				for (BlipNodeHandle node : blipNodeHandles) {
					if (node.getBlipNodeIP().equals("192.168.20.39")) {
						blipnode = node;
					}
				}
				//blipnode = blipNodeHandles[0];
				System.out.println(blipnode.getBlipNodeIP());
				try {
					SampleRunner runner = new SampleRunner(blipnode, sampleName);
					HashMap devicesInRange = new Inquirer(blipnode)
							.findDevicesInRange(true);
					ConnectData selectedDevice = new ConsoleMenu(devicesInRange)
							.getSelection();
					runner.runSample(selectedDevice);
				} catch (SampleRunner.SampleException e) {
					System.out.println(e.getMessage() + "\n  Caused by: "
							+ e.getCause());
				}
			} else {
				System.out.println("No connected BlipNodes found in the '"
						+ GROUP_NAME + "' group");
			}
		} catch (BlipServerConnectionException e) {
			e.printStackTrace();
		} catch (BlipServerAccessException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			System.setSecurityManager(new BlipNetSecurityManager());
			new Main(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void usage() {
		System.out.println("Missing arguments!");
		System.out
				.println("Usage: com.blipsystems.blipnet.samples.common.Main <username> <password> <hostname> <samplename>");
		System.exit(0);
	}
}
