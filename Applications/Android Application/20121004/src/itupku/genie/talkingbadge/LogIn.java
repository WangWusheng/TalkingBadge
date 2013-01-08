package itupku.genie.talkingbadge;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends Activity {
	private static Button loginButton = null;
	private static Button registButton = null;
	private static EditText usernameText = null;
	private static EditText passwordText = null;
	private static String userValidateURLString = "http://130.226.142.182/TalkingBadgeDemo/UserValidate4App";
	private static String registURLString = "http://130.226.142.182/TalkingBadgeDemo/Regist";
	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		loginButton = (Button) findViewById(R.id.loginButton);
		registButton = (Button) findViewById(R.id.registButton);
		usernameText = (EditText) findViewById(R.id.username);
		passwordText = (EditText) findViewById(R.id.password);
		final Intent intentDS = new Intent(this, DataStorage.class);
		startService(intentDS);

		// final Intent intentSensor = new Intent(this, SensorService.class);
		// startService(intentSensor);
		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				if (mBluetoothAdapter == null) {
					Toast.makeText(LogIn.this, "Bluetooth is not available",
							Toast.LENGTH_LONG).show();

				}
				if (!mBluetoothAdapter.isEnabled()) {
					Toast.makeText(LogIn.this,
							"Please enable Bluetooth before log in",
							Toast.LENGTH_LONG).show();
					Intent discoverableIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
					discoverableIntent.putExtra(
							BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
					startActivity(discoverableIntent);
				} else {
					ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
					if (networkInfo != null && networkInfo.isConnected()) {
						(new DownloadWebpageText()).execute();
						Toast.makeText(
								LogIn.this,
								"If not log in, please check the user name and password and try again.",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(LogIn.this, "Network is not available",
								Toast.LENGTH_LONG).show();
					}

				}
			}
		});

		registButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent viewIntent = new Intent("android.intent.action.VIEW",
						Uri.parse(registURLString));
				startActivity(viewIntent);
			}
		});
	}

	public void onBackPressed() {
		super.onBackPressed();
		stopService(new Intent(this, DataStorage.class));
		stopService(new Intent(this, PlayMusicService.class));
		stopService(new Intent(this, SensorService.class));
		stopService(new Intent(this, OBEXService.class));
		stopService(new Intent(this, SPPService.class));

	}

	private class DownloadWebpageText extends AsyncTask {
		protected Object doInBackground(Object... arg0) {
			InputStream is;
			String curURL = userValidateURLString
					+ "?Username="
					+ usernameText.getText()
					+ "&Password="
					+ passwordText.getText()
					+ "&MAC="
					+ mBluetoothAdapter.getAddress().replace(":", "")
							.toLowerCase();
			try {
				URL url = new URL(curURL);
				// Log.e("LogIN", curURL);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				// Starts the query
				conn.connect();
				is = conn.getInputStream();

				// Convert the InputStream into a string
				Reader reader = new InputStreamReader(is, "UTF-8");
				char[] buffer = new char[1];
				reader.read(buffer);

				if (is != null) {
					is.close();
				}
				if (new String(buffer).equals("1")) {

					Intent answerIntent = new Intent(LogIn.this,
							TalkingBadgeActivity.class);
					startActivity(answerIntent);
					LogIn.this.finish();
					return "Log in successful";
				} else {
					return "Invalidate user";

				}
			} catch (Exception e) {
				return "Unable to connect to the server" + e;
			}

		}
	}
}
