package itupku.genie.talkingbadge;

import java.util.UUID;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

/**
 * This class need to be implemented
 */
public class OBEXService extends Service {
	private final IBinder OBEXBinder = new OBEXBinder();

	private static final String TAG = "OBEX";
	private static final String serviceName = "OBEX";
	private static final UUID OBEXUUID = UUID
			.fromString("00001105-0000-1000-8000-00805F9B34FB");
	private BluetoothAdapter mBtAdapter = null;

	public int onStartCommand(Intent intent, int flags, int startId) {

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		Intent intentOpp = new Intent(
				"com.android.bluetooth/.BluetoothOppService");
		stopService(intentOpp);
		Intent intentOpp2 = new Intent(
				"it.medieval.blueftp/.opp_server");
		startService(intentOpp2);
		return startId;
	}

	public IBinder onBind(Intent intent) {
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		Intent intentOpp = new Intent(
				"com.android.bluetooth/.BluetoothOppService");
		stopService(intentOpp);
		Intent intentOpp2 = new Intent(
				"it.medieval.blueftp/.opp_server");
		startService(intentOpp2);
		return OBEXBinder;
	}

	public void onDestroy() {
	}

	private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
		public void onServiceConnected(int profile, BluetoothProfile proxy) {
			if (profile == BluetoothProfile.A2DP) {
				android.bluetooth.BluetoothA2dp mBluetoothA2dp = (android.bluetooth.BluetoothA2dp) proxy;
			}
		}

		public void onServiceDisconnected(int profile) {
			if (profile == BluetoothProfile.A2DP) {

			}
		}
	};

	public class OBEXBinder extends Binder {
		OBEXService getService() {
			return OBEXService.this;
		}
	}
}
