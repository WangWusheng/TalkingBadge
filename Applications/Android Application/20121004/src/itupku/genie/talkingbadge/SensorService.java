package itupku.genie.talkingbadge;

import java.util.Calendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class SensorService extends Service implements SensorEventListener {
	private static final String TAG = "Sensor";
	// private SensorManager mSensorManager;
	private LocationManager locationManager;
	private LocationListener locationListener;
	static Location curLocation = new Location("No value");;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// mSensorManager = (SensorManager)
		// getSystemService(Context.SENSOR_SERVICE);
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		// List<Sensor> deviceSensors = mSensorManager
		// .getSensorList(Sensor.TYPE_ALL);
		// for (Sensor sensor : deviceSensors) {
		// Log.i(TAG, sensor.getType() + " " + sensor.getName());
		// }
		// mSensorManager.registerListener(this,
		// mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
		// SensorManager.SENSOR_DELAY_GAME);
		// mSensorManager.registerListener(this,
		// mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		// SensorManager.SENSOR_DELAY_GAME);
		// mSensorManager.registerListener(this,
		// mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
		// SensorManager.SENSOR_DELAY_GAME);

		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				curLocation = location;

				String loc = null;
				if (SensorService.curLocation.getProvider().equals(
						"InputInformation")) {
					loc = SensorService.curLocation.getExtras().getString(
							"Zone");
				} else {
					loc = SensorService.curLocation.getLatitude() + ","
							+ SensorService.curLocation.getLongitude() + ","
							+ SensorService.curLocation.getAltitude();
				}

				String theMessage = "The most recent location is (" + loc
						+ "), with accuricy "
						+ SensorService.curLocation.getAccuracy()
						+ ", acquired  from "
						+ SensorService.curLocation.getProvider() + " at "
						+ SensorService.curLocation.getTime();
				// TalkingBadgeActivity.appendMessage(theMessage);
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		return startId;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_GRAVITY:
			String accelerometer = event.timestamp + " " + "X£º"
					+ event.values[0] + " " + "Y:" + event.values[1] + " "
					+ "Z:" + event.values[2];
			// DataStorage.expandLog(accelerometer);
			// Log.i(TAG, accelerometer);
			break;
		case Sensor.TYPE_ACCELEROMETER:
			break;
		case Sensor.TYPE_ROTATION_VECTOR:
			break;
		}

	}

	public void onDestroy() {
		locationManager.removeUpdates(locationListener);
		// mSensorManager.unregisterListener(this);
	}
}
