package genie.talkingbadgeserver;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.DefaultListModel;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.BlipNodeHandleInUseException;
import com.blipsystems.blipnet.api.blipnode.BlipNodeNotConnectedException;
import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipnode.InquiryData;
import com.blipsystems.blipnet.api.blipserver.BlipServer;
import com.blipsystems.blipnet.api.blipserver.BlipServerAccessException;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnection;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;
import com.blipsystems.blipnet.api.bluetooth.BluetoothAddress;
import com.blipsystems.blipnet.api.event.InquiryEvent;
import com.blipsystems.blipnet.api.event.InquiryEventListener;
import com.blipsystems.blipnet.api.util.BlipNetSecurityManager;
import com.blipsystems.blipnet.api.zones.ZoneEvent;
import com.blipsystems.blipnet.api.zones.ZoneEventFilter;
import com.blipsystems.blipnet.api.zones.ZoneEventListener;
import com.blipsystems.blipnet.api.zones.ZoneHandler;
import com.blipsystems.blipnet.api.zones.ZonesException;

public class SearchDevice implements ZoneEventListener {
	private static final String username = "Administrator";
	private static final String password = "BLIPNET";
	private static final String hostname = "localhost";

	private BlipServerConnection server;

	DefaultListModel<UserDevice> devicesListModel = (DefaultListModel) UserInterface
			.getDevicesList().getModel();

	public void Start() throws BlipServerConnectionException,
			BlipServerAccessException {
		System.setSecurityManager(new BlipNetSecurityManager());
		server = BlipServer.getConnection(username, password, hostname);
		UserInterface.setServer(server);
		ZoneHandler blipZoneHandle = server.getZoneHandler();

		HashSet<Integer> eventIds = new HashSet<Integer>();
		eventIds.add(new Integer(ZoneEvent.DEVICE_MOVED));
		eventIds.add(new Integer(ZoneEvent.DEVICE_DETECTED));
		ZoneEventFilter zoneEventFilter = new ZoneEventFilter(eventIds, null,
				null);
		try {
			blipZoneHandle.addEventListener(this, zoneEventFilter);
		} catch (ZonesException e) {
			e.printStackTrace();
		}
	}

	public void handleZoneEvent(ZoneEvent evt) {
		ConnectData cd = evt.getConnectData();
		Enumeration<UserDevice> eles = devicesListModel.elements();
		UserDevice ud = null;
		switch (evt.getEventID()) {
		case ZoneEvent.DEVICE_DETECTED:
			while (eles.hasMoreElements()) {
				ud = (UserDevice) eles.nextElement();
				if (ud.getCd().getTerminalID().equals(evt.getTerminalID()))
					break;
				ud = null;
			}
			if (ud != null) {
				int posi = devicesListModel.indexOf(ud);
				ud.setBlipMac(evt.getBlipNodeID());
				ud.setRssi(evt.getLastRssiValue());
				ud.setUser(evt.getTerminalFriendlyName());
				ud.setCd(cd);
				ud.setBlipCurZoneName(evt.getCurrentZone());
				ud.setBlipPreZoneName(evt.getPreviousZone());
				devicesListModel.setElementAt(ud, posi);
			} else {
				devicesListModel.addElement(new UserDevice(evt
						.getTerminalFriendlyName(), cd, evt.getLastRssiValue(),
						evt.getBlipNodeID(), evt.getCurrentZone(), evt
								.getPreviousZone()));
				String message = evt.getTerminalID() + " is detected in "
						+ evt.getCurrentZone();
				if (!message.equals(UserInterface.getPreviousMessage())) {
					UserInterface.addReply(message);
					UserInterface.setPreviousMessage(message);
				}
			}
			break;
		case ZoneEvent.DEVICE_MOVED:
			while (eles.hasMoreElements()) {
				ud = (UserDevice) eles.nextElement();
				if (ud.getCd().getTerminalID().equals(evt.getTerminalID()))
					break;
				ud = null;
			}
			if (ud != null) {
				devicesListModel.removeElement(ud);
			}
			String message = evt.getTerminalID() + " is removed from "
					+ evt.getCurrentZone();
			if (!message.equals(UserInterface.getPreviousMessage())) {
				UserInterface.addReply(message);
				UserInterface.setPreviousMessage(message);
			}
			break;
		}
	}
}
