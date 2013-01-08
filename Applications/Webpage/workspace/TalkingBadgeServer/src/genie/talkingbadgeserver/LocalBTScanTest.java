package genie.talkingbadgeserver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

public class LocalBTScanTest implements DiscoveryListener {
	// object used for waiting
	private static Object lock = new Object();
	private static File BTFile = new File("res//BTlog.txt");
	// vector containing the devices discovered
	private static HashMap<RemoteDevice, Date> mapDevices = new HashMap();

	// private static Vector vecDevicesTemp = new Vector();
	// private static boolean specifiedDeviceDiscovered = false;
	// private static String specifiedDeviceMac = "B0EC71740C41";
	// boolean deviceFound=false;
	// methods of DiscoveryListener

	/**
	 * This call back method will be called for each discovered bluetooth
	 * devices.
	 */
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		// System.out.println("Device discovered: "+
		// btDevice.getBluetoothAddress());
		// add the device to the vector
		if (mapDevices.containsKey(btDevice)) {
			mapDevices.remove(btDevice);
		}else{
			expandLog(btDevice.getBluetoothAddress()+" is detected.");
		}
		mapDevices.put(btDevice, new Date());
	}

	// no need to implement this method since services are not being discovered
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
	}

	// no need to implement this method since services are not being discovered
	public void serviceSearchCompleted(int transID, int respCode) {
	}

	/**
	 * This callback method will be called when the device discovery is
	 * completed.
	 */
	public void inquiryCompleted(int discType) {
		synchronized (lock) {
			lock.notify();
		}

		switch (discType) {
		case DiscoveryListener.INQUIRY_COMPLETED:
			// System.out.println("INQUIRY_COMPLETED");
			break;

		case DiscoveryListener.INQUIRY_TERMINATED:
			System.out.println("INQUIRY_TERMINATED");
			break;

		case DiscoveryListener.INQUIRY_ERROR:
			System.out.println("INQUIRY_ERROR");
			break;

		default:
			System.out.println("Unknown Response Code");
			break;
		}
	}// end method

	public void startScan() {
		try {

			if (!BTFile.exists())
				BTFile.createNewFile();
			// vecDevices = new Vector();
			// vecDevicesTemp = new Vector();
			// System.out.println(vecDevices.size());
			// create an instance of this class
			LocalBTScanTest bluetoothDeviceDiscovery = new LocalBTScanTest();

			// display local device address and name
			LocalDevice localDevice = LocalDevice.getLocalDevice();

			System.out.println("Address: " + localDevice.getBluetoothAddress());
			System.out.println("Name: " + localDevice.getFriendlyName());

			// find devices

			DiscoveryAgent agent = localDevice.getDiscoveryAgent();

			System.out.println("Starting device inquiry...");
			while (true) {
				// vecDevicesTemp = new Vector();
				agent.startInquiry(DiscoveryAgent.GIAC,
						bluetoothDeviceDiscovery);

				try {
					synchronized (lock) {
						lock.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// System.out.println("Device Inquiry Completed. ");

				// print all devices in vecDevices
				// int deviceCountTemp = vecDevicesTemp.size();
				// int deviceCount = vecDevices.size();
				// boolean deviceMark[]=new boolean[deviceCount];

				// if (deviceCount <= 0) {
				// if (specifiedDeviceDiscovered == true) {
				// specifiedDeviceDiscovered = false;
				// expandLog(specifiedDeviceMac + " disappears.");
				// }
				// } else
				{
					// expandLog("Bluetooth Devices: " + "\n");

					// print bluetooth device addresses and names in the format
					// [
					// No. address (name) ]
					// System.out.println("Bluetooth Devices: ");
					//int i = 0;
					// deviceFound=false;
					Date curDate=new Date();
					//Iterator<Entry<RemoteDevice, Date>> 
					Set<Entry<RemoteDevice, Date>> setDevices = mapDevices.entrySet();
					Iterator<Entry<RemoteDevice, Date>> iteDevices =setDevices.iterator();
					while(iteDevices.hasNext()){
						Entry<RemoteDevice, Date> theDevice = iteDevices.next();
						if(curDate.getTime()-theDevice.getValue().getTime()>60000){
							//setDevices.remove(theDevice);
							iteDevices.remove();
							//mapDevices.remove(theDevice.getKey());
							expandLog(((RemoteDevice) theDevice.getKey())
									.getBluetoothAddress() + " disappears."+" for lost duration "+(curDate.getTime()-theDevice.getValue().getTime()));
							
						}
					}
//					for (i = 0; i < mapDevices.size(); i++) {
//						if(curDate.getTime()-mapDevices.)
//						RemoteDevice remoteDevice = (RemoteDevice) vecDevicesTemp
//								.elementAt(i);
//						// expandLog(i+")"+remoteDevice.getBluetoothAddress() +
//						// " is detected.");
//						if (vecDevices.contains(remoteDevice)) {
//							deviceMark[vecDevices.indexOf(remoteDevice)] = true;
//						} else {
//							expandLog(remoteDevice.getBluetoothAddress()
//									+ " is discovered.");
//						}
//						// if (remoteDevice.getBluetoothAddress().equals(
//						// specifiedDeviceMac)) {
//						// deviceFound=true;
//						// if (specifiedDeviceDiscovered == false) {
//						// specifiedDeviceDiscovered = true;
//						// expandLog(specifiedDeviceMac + " is detected.");
//						// break;
//						// }
//						// }
//					}
//					for (i = 0; i < deviceCount; i++) {
//						if (deviceMark[i] == false)
//							expandLog(((RemoteDevice) vecDevices.elementAt(i))
//									.getBluetoothAddress() + " disappears.");
//					}
//					vecDevices = vecDevicesTemp;
					// if (deviceFound==false) {
					// if (specifiedDeviceDiscovered == true) {
					// specifiedDeviceDiscovered = false;
					// expandLog(specifiedDeviceMac + " disappears.");
					// }
					// }
					// deviceFound=false;
					// vecDevices = new Vector();
				}
				expandLog("^^^^^^^^^^^^Device count: " + mapDevices.size()+"{"+mapDevices.keySet()+"}");
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public static synchronized boolean expandLog(String newLog) {

		try {
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(BTFile, true)));
			output.write((new Date()) + ": " + newLog + "\n");
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
