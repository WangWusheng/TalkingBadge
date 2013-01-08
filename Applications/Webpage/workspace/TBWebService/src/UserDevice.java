

import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.bluetooth.BluetoothAddress;

public class UserDevice implements Comparable {

	private String user;
	private ConnectData cd;
	private int rssi;
	private BluetoothAddress blipMac;
	private String blipCurZoneName;
	private String blipPreZoneName;

	public UserDevice(String user, ConnectData cd, int rssi,
			BluetoothAddress blipMac, String blipCurZoneName,
			String blipPreZoneName) {
		super();
		this.user = user;
		this.cd = cd;
		this.rssi = rssi;
		this.blipMac = blipMac;
		this.blipPreZoneName = blipPreZoneName;
		this.blipCurZoneName = blipCurZoneName;
	}

	public String getBlipCurZoneName() {
		return blipCurZoneName;
	}

	public void setBlipCurZoneName(String blipCurZoneName) {
		this.blipCurZoneName = blipCurZoneName;
	}

	public String getBlipPreZoneName() {
		return blipPreZoneName;
	}

	public void setBlipPreZoneName(String blipPreZoneName) {
		this.blipPreZoneName = blipPreZoneName;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public BluetoothAddress getBlipMac() {
		return blipMac;
	}

	public void setBlipMac(BluetoothAddress blipMac) {
		this.blipMac = blipMac;
	}

	public ConnectData getCd() {
		return cd;
	}

	public void setCd(ConnectData cd) {
		this.cd = cd;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int compareTo(Object arg0) {
		return ((UserDevice) arg0).getCd().getTerminalID()
				.compareTo(this.cd.getTerminalID());
	}

	public String toString() {
		return "<" + cd.getTerminalID() + ">:" + " " + rssi + " by <" + blipMac
				+ ">";
	}

}
