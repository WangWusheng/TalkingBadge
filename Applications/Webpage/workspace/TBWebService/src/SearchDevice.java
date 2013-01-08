import java.util.HashSet;
import java.util.LinkedList;

import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipserver.BlipServer;
import com.blipsystems.blipnet.api.blipserver.BlipServerAccessException;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnection;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;
import com.blipsystems.blipnet.api.util.BlipNetSecurityManager;
import com.blipsystems.blipnet.api.zones.ZoneEvent;
import com.blipsystems.blipnet.api.zones.ZoneEventFilter;
import com.blipsystems.blipnet.api.zones.ZoneEventListener;
import com.blipsystems.blipnet.api.zones.ZoneHandler;
import com.blipsystems.blipnet.api.zones.ZonesException;

public class SearchDevice {
	private static final String username = "Administrator";
	private static final String password = "BLIPNET";
	private static final String hostname = "cerebro.itu.dk";
	//private static final String hostname = "localhost";
	// private static final String GROUP_NAME = "BlipNet";
	// private static final String CONFIG_NAME = "ITU BlipZones Inquiry node";//
	// E,
	static SearchDevice sd = new SearchDevice(); // node4c-inquirer
	// private static final String CONFIG_NAME =
	// "ITU BlipZones Receiver node";//C, ITU-4C
	// private static final String CONFIG_NAME =
	// "ITU BlipZones Sender node";//D, node4c-1
	// private static final String CONFIG_NAME =
	// "BlipZones Inquiry and Data Node";

	private static boolean searching = false;
	public static BlipServerConnection server;
	public static LinkedList<UserDevice> devicesList;

	private static void Start() throws BlipServerConnectionException,
			BlipServerAccessException {
		System.setSecurityManager(new BlipNetSecurityManager());
		server = BlipServer.getConnection(username, password, hostname);
		ZoneHandler blipZoneHandle = server.getZoneHandler();

		HashSet<Integer> eventIds = new HashSet<Integer>();
		eventIds.add(new Integer(ZoneEvent.DEVICE_MOVED));
		eventIds.add(new Integer(ZoneEvent.DEVICE_DETECTED));
		ZoneEventFilter zoneEventFilter = new ZoneEventFilter(eventIds, null,
				null);
		try {
			blipZoneHandle.addEventListener(sd.new listener(), zoneEventFilter);
			devicesList = new LinkedList<UserDevice>();
			searching = true;
		} catch (ZonesException e) {
			e.printStackTrace();
		}
	}

	public static void startSearchingService()
			throws BlipServerConnectionException, BlipServerAccessException {
		if (!searching) {
			System.out.println("Starting to search...");
			Start();
			System.out.println("Searching service is started.");
		}
	}

	class listener implements ZoneEventListener {
		public void handleZoneEvent(ZoneEvent evt) {
			ConnectData cd = evt.getConnectData();
			UserDevice ud = null;
			switch (evt.getEventID()) {
			case ZoneEvent.DEVICE_DETECTED:
				for (UserDevice device : devicesList) {
					if (device.getCd().getTerminalID()
							.equals(evt.getTerminalID())) {
						ud = device;
						break;
					}
				}
				if (ud != null) {
					ud.setBlipMac(evt.getBlipNodeID());
					ud.setRssi(evt.getLastRssiValue());
					ud.setUser(evt.getTerminalFriendlyName());
					ud.setCd(cd);
					ud.setBlipCurZoneName(evt.getCurrentZone());
					ud.setBlipPreZoneName(evt.getPreviousZone());
					String message = evt.getTerminalID() + " is detected in "
							+ evt.getCurrentZone();
					// System.out.println(message);
				} else {
					devicesList.add(new UserDevice(evt
							.getTerminalFriendlyName(), cd, evt
							.getLastRssiValue(), evt.getBlipNodeID(), evt
							.getCurrentZone(), evt.getPreviousZone()));
					String message = evt.getTerminalID() + " is detected in "
							+ evt.getCurrentZone();
					System.out.println(message);
				}
				break;
			case ZoneEvent.DEVICE_MOVED:
				for (UserDevice device : devicesList) {
					if (device.getCd().getTerminalID()
							.equals(evt.getTerminalID())) {
						ud = device;
						break;
					}
				}
				if (ud != null) {
					devicesList.remove(ud);
				}
				String message = evt.getTerminalID() + " is removed from "
						+ evt.getCurrentZone();
				// System.out.println(message);
				break;
			}
		}
	}
}
