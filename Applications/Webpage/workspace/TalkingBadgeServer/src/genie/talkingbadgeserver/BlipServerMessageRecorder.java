package genie.talkingbadgeserver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dk.itu.infobus.comet.EventBus;
import dk.itu.infobus.comet.Listener;
import dk.itu.infobus.comet.PatternBuilder;
import dk.itu.infobus.comet.PatternOperator;

public class BlipServerMessageRecorder {
	//static File logFile = new File("res//BlipServerlog.txt");
	static File logFile = new File("res//BlipServerRssilog.txt");
	static HashMap<Object, DeviceDetectionInfo> hm = new HashMap();

	BlipServerMessageRecorder() {
		hm = new HashMap();
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		Thread subThread = new Thread() {
			public void run() {
				EventBus eb = new EventBus("tiger.itu.dk", 8004);
				eb.start();
				Listener listener = new AllEventsListener();
				eb.addListener(listener);
			}
		};
		subThread.start();
		Object available = new Object();
		synchronized (available) {
			try {
				available.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public class AllEventsListener extends Listener {
		public AllEventsListener() {
			super(new PatternBuilder()
			 .add("terminal.btmac", PatternOperator.EQ, "b0ec71740c41","001dfd8e7d42","34159EDF853C")
				//	.add("type", PatternOperator.EQ, "device.detected","device.moved") // and the property 'type' is either
											// detected or moved
				//	.add("zone.current", PatternOperator.EQ,"itu.zone4.zone4c") // and have
																	// a
					// 'zone.current'
					// property
					.getPattern());
		}

		public void onStarted() {
			System.out.println("Hello, the listener has been registered!");
		}

		public void cleanUp() {
			System.out.println("Bye bye!");
		}

		public void onMessage(Map<String, Object> evt) {

			if (evt.get("type").toString().equals("device.detected")
					) {
				String message1 = "!!detected!!" + evt.get("terminal.btmac") + " "
						+ " is detected "+" in "+ evt.get("zone.current")+" last rssi:"+evt.get("last.rssi")+" mean rssi:"+evt.get("mean.rssi");
				System.out.println(message1);
				expandLog(message1);
/**				if (!hm.containsKey(evt.get("terminal.btmac"))) {
					hm.put(evt.get("terminal.btmac"), new DeviceDetectionInfo(
							evt.get("zone.current")));
					String message = evt.get("terminal.btmac") + " "
							+ " is detected in " + evt.get("zone.current")+"last rssi:"+evt.get("last.rssi")+"mean rssi:"+evt.get("mean.rssi");
					System.out.println(message);
					expandLog(message);
					expandLog("^^^^^^^^^^^^^^^^^^Device Count: " + hm.size()+"{"+hm.keySet()+"}");
				} else {
					if (hm.get(evt.get("terminal.btmac")).getZone()
							.equals(evt.get("zone.current"))) {
						hm.remove(evt.get("terminal.btmac"));
						hm.put(evt.get("terminal.btmac"),
								new DeviceDetectionInfo(evt.get("zone.current")));
					} else {
						String message = evt.get("terminal.btmac") + " "
								+ " is moved from "
								+ hm.get(evt.get("terminal.btmac")).getZone()
								+ " to " + evt.get("zone.current")+"last rssi:"+evt.get("last.rssi")+"mean rssi:"+evt.get("mean.rssi");
						System.out.println(message);
						expandLog(message);
						hm.remove(evt.get("terminal.btmac"));
						hm.put(evt.get("terminal.btmac"),
								new DeviceDetectionInfo(evt.get("zone.current")));
						expandLog("^^^^^^^^^^^^^^^^^^Device Count: " + hm.size()+"{"+hm.keySet()+"}");
					}
				}
*/
			}
			if (evt.get("type").toString().equals("device.moved")) {
				String message1 = "!!moved!!" + evt.get("terminal.btmac") + " "
						+ " is moved from "+evt.get("zone.previous") +" to "+ evt.get("zone.current")+" last rssi:"+evt.get("last.rssi")+" mean rssi:"+evt.get("mean.rssi");
				System.out.println(message1);
				expandLog(message1);
/**				if (evt.get("zone.previous").toString().equals(" ")) {
					if (hm.containsKey(evt.get("terminal.btmac")))
						hm.remove(evt.get("terminal.btmac"));
					hm.put(evt.get("terminal.btmac"), new DeviceDetectionInfo(
							evt.get("zone.current")));
					String message = "moved" + evt.get("terminal.btmac") + " "
							+ " is detected in " + evt.get("zone.current")+"last rssi:"+evt.get("last.rssi")+"mean rssi:"+evt.get("mean.rssi");
					System.out.println(message);
					expandLog(message);
					expandLog("^^^^^^^^^^^^^^^^^^Device Count: " + hm.size()+"{"+hm.keySet()+"}");
				} else if (evt.get("zone.current").toString().equals(" ")) {
					if (hm.containsKey(evt.get("terminal.btmac")))
						hm.remove(evt.get("terminal.btmac"));
					String message = "moved" + evt.get("terminal.btmac") + " "
							+ " is removed from " + evt.get("zone.previous")+" for lost duration "+((new Date()).getTime()-(long)evt.get("timestamp"))+"last rssi:"+evt.get("last.rssi")+"mean rssi:"+evt.get("mean.rssi");
					System.out.println(message);
					expandLog(message);
					expandLog("^^^^^^^^^^^^^^^^^^Device Count: " + hm.size()+"{"+hm.keySet()+"}");
				} else {
					String message = "moved" + evt.get("terminal.btmac") + " "
							+ " is moved from " + evt.get("zone.previous")
							+ " to " + evt.get("zone.current")+"last rssi:"+evt.get("last.rssi")+"mean rssi:"+evt.get("mean.rssi");
					System.out.println(message);
					expandLog(message);
					hm.remove(evt.get("terminal.btmac"));
					hm.put(evt.get("terminal.btmac"), new DeviceDetectionInfo(
							evt.get("zone.current")));
					expandLog("^^^^^^^^^^^^^^^^^^Device Count: " + hm.size()+"{"+hm.keySet()+"}");
				}
*/			}
/**			Date dt = new Date();
			Iterator<Entry<Object, DeviceDetectionInfo>> hmSet = hm.entrySet()
					.iterator();
			while (hmSet.hasNext()) {
				Entry<Object, DeviceDetectionInfo> theEntry = hmSet.next();
				if (dt.getTime() - theEntry.getValue().getDate().getTime() > 60000) {
					hmSet.remove();
					//hm.remove(theEntry.getKey());
					String message = "Time up" + theEntry.getKey() + " "
							+ " is removed from "
							+ theEntry.getValue().getZone()+" for lost duration "+(dt.getTime() - theEntry.getValue().getDate().getTime())+"last rssi:"+evt.get("last.rssi")+"mean rssi:"+evt.get("mean.rssi");
					System.out.println(message);
					expandLog(message);
					expandLog("^^^^^^^^^^^^^^^^^^Device Count: " + hm.size()+"{"+hm.keySet()+"}");
				}
			}

*/		}
	}

	public static synchronized void expandLog(String newLog) {

		try {
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(logFile, true)));
			output.write((new Date()) + ": " + newLog + "\n");
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new BlipServerMessageRecorder();
	}

	private class DeviceDetectionInfo {
		public Object getZone() {
			return zone;
		}

		public void setZone(Object zone) {
			this.zone = zone;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public DeviceDetectionInfo(Object zone, Date date) {
			super();
			this.zone = zone;
			this.date = date;
		}

		public DeviceDetectionInfo(Object zone) {
			super();
			this.zone = zone;
			this.date = new Date();
		}

		Object zone;
		Date date;
	}
}
