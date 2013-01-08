package itupku.genie.talkingbadge;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class TalkingBadgeActivity extends Activity {
	private static final String TAG = "TalkingBadgeActivity";
	static final String VOLUME_LOUD = "LOUD";
	static final String VOLUME_MEDIUM = "MEDIUM";
	static final String VOLUME_LOW = "LOW";
	static final String VOLUME_SILENCE = "SILENCE";
	static boolean VOLUME_UP = false;
	private static final int DIALOG_DOWNLOADQRSCANER_ID = 0;
	private static final int DIALOG_SCANRESULT_ID = 1;
	private static final int DIALOG_DOWNLOADBTFTP_ID = 2;
	private static Button replayButton = null;
	private static Button volumeButton = null;
	private static Button bluetoothButton = null;
	private static Button numberButton = null;
	private static Button qrButton = null;
	private static TextView messageText = null;

	static String introductionMessageSoundFile = "introduction_message.mp3";
	static String lowBatteryAlertSoundFile = "low_battery_alert.wav";
	static String matchXMLFile = "matches.xml";
	static String soundHistoryTXTFile = "soundhistory.txt";
	static String volumeLevel = VOLUME_LOUD;
	static boolean bluetoothStatus = false;
	static int batteryLevel = 100;
	static final int lowBatteryAlertLevel = 15;
	static boolean batteryCharging = false;
	static Vibrator vibrate;
	static boolean VibrateOnPlaySound = false;
	static boolean EarphonePlugedIn = false;
	// repeat mode, repeat one or repeat more
	static boolean RepeatOne = false;
	static Date replayButtonPressedTime = new Date();
	static int curReplyLocationInSoundHistory = 0;
	static LinkedList<String> soundHistoryList = null;
	static String scanedString = "";
	private itupku.genie.talkingbadge.LocalBroadcastManager mLocalBroadcastManager;
	private BroadcastReceiver mReceiver;
	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private BroadcastReceiver batteryLevelReceiver;

	private IntentFilter batteryLevelFilter;
	private static LinearLayout mainLayout;
	private static ScrollView scrollView;
	static String backgroundPic = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		bluetoothStatus = bluetoothCheck(false);

		// Log.d(TAG, "++ bindService ++");
		batteryLevel();
		final Intent intent = new Intent(this, PlayMusicService.class);
		startService(intent);
		mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		replayButton = (Button) findViewById(R.id.replaybutton);
		volumeButton = (Button) findViewById(R.id.volumebutton);
		bluetoothButton = (Button) findViewById(R.id.bluetoothbutton);
		numberButton = (Button) findViewById(R.id.numberbutton);
		qrButton = (Button) findViewById(R.id.qrbutton);
		messageText = (TextView) findViewById(R.id.messagetext);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		qrButton.setVisibility(View.GONE);
		numberButton.setVisibility(View.GONE);
		if (bluetoothStatus) {
			final Intent intentSPP = new Intent(this, SPPService.class);
			startService(intentSPP);
			final Intent intentOBEX = new Intent(this, OBEXService.class);
			startService(intentOBEX);
			bluetoothButton.setBackgroundResource(R.drawable.bluetoothon);

			try {
				this.getPackageManager().getApplicationInfo(
						"it.medieval.blueftp",
						PackageManager.GET_UNINSTALLED_PACKAGES);
			} catch (NameNotFoundException e) {
				showDialog(DIALOG_DOWNLOADBTFTP_ID);
			}

		}
		vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if (backgroundPic != null) {
			setBackgroundPic(backgroundPic);
		}
		replayButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				nm.cancel(SPPService.ID_LED);
				appendMessage("Replay button is pressed");
				Date curdate = new Date();
				if (RepeatOne
						|| (curdate.getTime()
								- replayButtonPressedTime.getTime() > 5000)) {
					PlayMusicService.getMediaPlayer().reset();
					try {
						PlayMusicService.getMediaPlayer().setDataSource(
								DataStorage
										.findFile(soundHistoryList.getLast())
										.getAbsolutePath());
						curReplyLocationInSoundHistory = soundHistoryList
								.size();
						PlayMusicService.getMediaPlayer().prepare();
						PlayMusicService.getMediaPlayer().start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					PlayMusicService.getMediaPlayer().reset();
					try {
						String aFile = DataStorage
								.findFile(
										soundHistoryList
												.get(curReplyLocationInSoundHistory - 1))
								.getAbsolutePath();
						PlayMusicService.getMediaPlayer().setDataSource(aFile);
						curReplyLocationInSoundHistory--;
						if (curReplyLocationInSoundHistory < 1) {
							curReplyLocationInSoundHistory = 1;
						}
						PlayMusicService.getMediaPlayer().prepare();
						PlayMusicService.getMediaPlayer().start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				replayButtonPressedTime = curdate;
			}
		});
		numberButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				appendMessage("Number button is pressed");
				Intent answerIntent = new Intent(TalkingBadgeActivity.this,
						InputAnswerActivity.class);
				startActivity(answerIntent);
			}
		});

		qrButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				appendMessage("QR button is pressed");
				Intent intent = new Intent(
						"com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				try {
					startActivityForResult(intent, 0);
				} catch (ActivityNotFoundException e) {
					showDialog(DIALOG_DOWNLOADQRSCANER_ID);
				}
			}
		});

		bluetoothButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				appendMessage("Exit button is pressed");
				exitProgrames();

				// if (bluetoothStatus == false) {
				// bluetoothCheck(true);
				// } else {
				// Log.e(TAG, mBluetoothAdapter.getAddress() + " is MAC");
				// String loc = null;
				// if (SensorService.curLocation.getProvider().equals(
				// "InputInformation")) {
				// loc = SensorService.curLocation.getExtras().getString(
				// "Zone");
				// } else {
				// loc = SensorService.curLocation.getLatitude() + ","
				// + SensorService.curLocation.getLongitude()
				// + "," + SensorService.curLocation.getAltitude();
				// }
				//
				// String theMessage = "The most recent location is (" + loc
				// + "), with accuricy "
				// + SensorService.curLocation.getAccuracy()
				// + ", acquired  from "
				// + SensorService.curLocation.getProvider() + " at "
				// + SensorService.curLocation.getTime();
				// Toast.makeText(TalkingBadgeActivity.this, theMessage,
				// Toast.LENGTH_LONG).show();
				// }
			}
		});

		volumeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (volumeLevel.equals(VOLUME_LOUD)) {
					VOLUME_UP = false;
					appendMessage("Volume button is pressed, and volume become "
							+ VOLUME_MEDIUM);
					SetVolume(VOLUME_MEDIUM);
				} else if (volumeLevel.equals(VOLUME_MEDIUM)) {
					if (VOLUME_UP) {
						appendMessage("Volume button is pressed, and volume become "
								+ VOLUME_LOUD);
						SetVolume(VOLUME_LOUD);
					} else {
						appendMessage("Volume button is pressed, and volume become "
								+ VOLUME_LOW);
						SetVolume(VOLUME_LOW);
					}
				} else if (volumeLevel.equals(VOLUME_LOW)) {
					if (VOLUME_UP) {
						appendMessage("Volume button is pressed, and volume become "
								+ VOLUME_MEDIUM);
						SetVolume(VOLUME_MEDIUM);
					} else {
						appendMessage("Volume button is pressed, and volume become "
								+ VOLUME_SILENCE);
						SetVolume(VOLUME_SILENCE);
					}
				} else if (volumeLevel.equals(VOLUME_SILENCE)) {
					VOLUME_UP = true;
					appendMessage("Volume button is pressed, and volume become "
							+ VOLUME_LOW);
					SetVolume(VOLUME_LOW);
				}
			}
		});
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction("genie.talkingbadge");
		mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if (intent.getStringExtra("Command").equals("SHUTDOWN")) {
					exitProgrames();
				} else if (intent.getStringExtra("Command").equals(
						"APPENDMESSAGE")) {
					appendMessage(intent.getStringExtra("Message"));
				} else if (intent.getStringExtra("Command").equals(
						"THIEFMODESET")) {
					String msg = intent.getStringExtra("Message");
					if (msg.equals("ON"))
						hideAllComponents(true);
					else if (msg.equals("OFF"))
						hideAllComponents(false);
				} else if (intent.getStringExtra("Command").equals("VOLUMESET")) {
					SetVolume(intent.getStringExtra("Message"));
				} else if (intent.getStringExtra("Command").equals(
						"BACKGROUNDSET")) {
					setBackgroundPic(intent.getStringExtra("Message"));
				} else if (intent.getStringExtra("Command").equals(
						"RECEIVEDINPUT")) {
					dealWithInput(intent.getStringExtra("Message"));
				}
			}
		};
		mLocalBroadcastManager.registerReceiver(mReceiver, filter);

		// for(int i=0;i<999999;i++){}
		soundHistoryList = DataStorage.readSoundHistory();
		if (soundHistoryList == null)
			soundHistoryList = new LinkedList<String>();
		curReplyLocationInSoundHistory = soundHistoryList.size();
		appendMessage("Turned on");
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				scanedString = data.getStringExtra("SCAN_RESULT");
				// showDialog(DIALOG_SCANRESULT_ID);
				dealWithInput(scanedString);
			}
		}
	}

	protected static String SetVolume(String theVolume) {
		if (theVolume == VOLUME_MEDIUM) {
			volumeLevel = VOLUME_MEDIUM;
			volumeButton.setBackgroundResource(R.drawable.mediumvolumebutton);
			PlayMusicService.getMediaPlayer().setVolume(0.5f, 0.5f);
			return "SET VOLUME " + VOLUME_MEDIUM;
		} else if (theVolume.equals(VOLUME_LOW)) {
			volumeLevel = VOLUME_LOW;
			volumeButton.setBackgroundResource(R.drawable.lowvolumebutton);
			PlayMusicService.getMediaPlayer().setVolume(0.2f, 0.2f);
			return "SET VOLUME " + VOLUME_LOW;
		} else if (theVolume.equals(VOLUME_SILENCE)) {
			volumeLevel = VOLUME_SILENCE;
			volumeButton.setBackgroundResource(R.drawable.silencevolumebutton);
			PlayMusicService.getMediaPlayer().setVolume(0f, 0f);
			return "SET VOLUME " + VOLUME_SILENCE;
		} else if (theVolume.equals(VOLUME_LOUD)) {
			volumeLevel = VOLUME_LOUD;
			volumeButton.setBackgroundResource(R.drawable.loudvolumebutton);
			PlayMusicService.getMediaPlayer().setVolume(1.5f, 1.5f);
			return "SET VOLUME " + VOLUME_LOUD;
		}
		return "BAD VOLUME VALUE";

	}

	protected void onDestroy() {
		super.onDestroy();
		appendMessage("Turned off");
		stopService(new Intent(this, DataStorage.class));
		stopService(new Intent(this, PlayMusicService.class));
		stopService(new Intent(this, SensorService.class));
		// if (bluetoothStatus) {
		stopService(new Intent(this, OBEXService.class));
		stopService(new Intent(this, SPPService.class));
		bluetoothStatus = false;
		// }
		unregisterReceiver(batteryLevelReceiver);
		// unregisterReceiver(mReceiver);
		DataStorage.writeSoundHistory(soundHistoryList);
	}

	/**
	 * Computes the battery level by registering a receiver to the intent
	 * triggered by a battery status/level change.
	 */
	private void batteryLevel() {
		batteryLevelReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if (intent
						.getAction()
						.equals(android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED)) {
					if (intent.getIntExtra(
							android.bluetooth.BluetoothAdapter.EXTRA_STATE, 0) == android.bluetooth.BluetoothAdapter.STATE_OFF) {
						if (bluetoothStatus) {
							bluetoothStatus = false;
							bluetoothButton
									.setBackgroundResource(R.drawable.bluetoothoff);
							stopService(new Intent(TalkingBadgeActivity.this,
									OBEXService.class));
							stopService(new Intent(TalkingBadgeActivity.this,
									SPPService.class));
						}
					} else if (intent.getIntExtra(
							android.bluetooth.BluetoothAdapter.EXTRA_STATE, 0) == android.bluetooth.BluetoothAdapter.STATE_ON) {
						if (!bluetoothStatus) {
							bluetoothStatus = true;
							bluetoothButton
									.setBackgroundResource(R.drawable.bluetoothon);
							Intent intentSPP = new Intent(
									TalkingBadgeActivity.this, SPPService.class);
							startService(intentSPP);
							Intent intentOBEX = new Intent(
									TalkingBadgeActivity.this,
									OBEXService.class);
							startService(intentOBEX);

						}
					}
				} else if (intent.getAction()
						.equals(Intent.ACTION_HEADSET_PLUG)) {
					if (intent.getIntExtra("state", 0) == 0)
						EarphonePlugedIn = false;
					else if (intent.getIntExtra("state", 0) == 1) {
						EarphonePlugedIn = true;
					}

				} else {
					int rawlevel = intent.getIntExtra(
							BatteryManager.EXTRA_LEVEL, -1);
					int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,
							-1);
					int level = -1;
					if (rawlevel >= 0 && scale > 0) {
						level = (rawlevel * 100) / scale;
					}
					batteryLevel = level;

					int status = intent.getIntExtra(
							BatteryManager.EXTRA_STATUS,
							BatteryManager.BATTERY_STATUS_UNKNOWN);

					if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
						if (batteryCharging == false) {
							appendMessage("Start charging with battery level "
									+ batteryLevel + "%");
							batteryCharging = true;
						}
					} else {
						if (batteryCharging == true) {
							appendMessage("Stop charging with battery level "
									+ batteryLevel + "%");
							batteryCharging = false;
						}
					}

				}
			}
		};
		batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		batteryLevelFilter.addAction(Intent.ACTION_HEADSET_PLUG);
		batteryLevelFilter
				.addAction(android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(batteryLevelReceiver, batteryLevelFilter);
	}

	private boolean bluetoothCheck(boolean showToast) {

		if (mBluetoothAdapter == null) {
			if (showToast)
				Toast.makeText(this, "Bluetooth is not available",
						Toast.LENGTH_LONG).show();
			return false;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			if (showToast) {
				// Log.e(TAG, mBluetoothAdapter.getAddress() + " is MA");
				Intent discoverableIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(
						BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
				startActivity(discoverableIntent);
				return true;
			}
			return false;
		}
		return true;
	}

	private void appendMessage(String theMessage) {
		DataStorage.expandLog("(" + Calendar.getInstance().getTime() + " "
				+ Calendar.getInstance().getTimeInMillis() + ") " + theMessage);
	}

	private void showMessage(String theMessage) {
		messageText.setText(theMessage);
		scrollView.post(new Runnable() {
			public void run() {
				scrollView.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	protected Dialog onCreateDialog(int i) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (i == DIALOG_DOWNLOADQRSCANER_ID) {
			builder.setMessage("You need to download QR reader!")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Uri uri = Uri
											.parse("market://search?q=pname:"
													+ "com.google.zxing.client.android");
									Intent intent = new Intent(
											Intent.ACTION_VIEW, uri);
									startActivity(intent);
								}
							}).setTitle("Need QR reader");
		} else if (i == DIALOG_SCANRESULT_ID) {
			builder.setMessage(scanedString)
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).setTitle("Scan result");
		} else if (i == DIALOG_DOWNLOADBTFTP_ID) {
			builder.setMessage(
					"You need to download Bluetooth File Transfer, check OPP service to be \"Start at boot\", and uncheck \"Stop when close\"!")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Uri uri = Uri
											.parse("market://search?q=pname:"
													+ "it.medieval.blueftp");
									Intent intent = new Intent(
											Intent.ACTION_VIEW, uri);
									startActivity(intent);
								}
							}).setTitle("Need Bluetooth File Transfer");
		}

		AlertDialog alert = builder.create();
		return alert;
	}

	private static void hideAllComponents(boolean hide) {
		if (hide) {
			replayButton.setVisibility(View.INVISIBLE);
			volumeButton.setVisibility(View.INVISIBLE);
			bluetoothButton.setVisibility(View.INVISIBLE);
			numberButton.setVisibility(View.INVISIBLE);
			qrButton.setVisibility(View.INVISIBLE);
			messageText.setVisibility(View.INVISIBLE);
			scrollView.setVisibility(View.INVISIBLE);
		} else {
			replayButton.setVisibility(View.VISIBLE);
			volumeButton.setVisibility(View.VISIBLE);
			bluetoothButton.setVisibility(View.VISIBLE);
			numberButton.setVisibility(View.VISIBLE);
			qrButton.setVisibility(View.VISIBLE);
			messageText.setVisibility(View.VISIBLE);
			scrollView.setVisibility(View.VISIBLE);
		}
	}

	private static boolean setBackgroundPic(String picName) {
		File picFile = DataStorage.findFile(picName);
		if (picFile == null) {
			return false;
		}
		Drawable d = Drawable.createFromPath(picFile.getAbsolutePath());
		mainLayout.setBackgroundDrawable(d);
		backgroundPic = picName;
		return true;
	}

	private void dealWithInput(String theInput) {
		appendMessage("Received input:" + theInput);
		Document dom = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new FileReader(DataStorage
					.findFile(matchXMLFile)));
			dom = db.parse(is);
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName("match");

			for (int i = 0; i < items.getLength(); i++) {
				Element item = (Element) items.item(i);
				NodeList itemInputs = item.getElementsByTagName("input");
				if (itemInputs.getLength() != 0) {
					Element itemInput = (Element) itemInputs.item(0);
					if (itemInput.getTextContent().equals(theInput)) {
						NodeList itemPositions = item
								.getElementsByTagName("position");
						if (itemPositions.getLength() != 0) {
							Location l = new Location("InputInformation");
							l.setTime(Calendar.getInstance().getTimeInMillis());

							Bundle extras = new Bundle(1);
							extras.putString("Zone", ((Element) itemPositions
									.item(0)).getTextContent());
							l.setExtras(extras);
							SensorService.curLocation.set(l);
						}
						NodeList itemReplytexts = item
								.getElementsByTagName("replytext");
						if (itemReplytexts.getLength() != 0) {
							appendMessage(((Element) itemReplytexts.item(0))
									.getTextContent());
							showMessage(((Element) itemReplytexts.item(0))
									.getTextContent());
						}
						NodeList itemSoundfiles = item
								.getElementsByTagName("soundfile");
						// Log.e(TAG, "" + itemSoundfiles.getLength());
						if (itemSoundfiles.getLength() != 0) {
							String fileName = ((Element) itemSoundfiles.item(0))
									.getTextContent();
							File file = DataStorage.findFile(fileName);
							// Log.e(TAG, fileName + file);
							if (file != null) {
								PlayMusicService.getMediaPlayer().reset();
								PlayMusicService.getMediaPlayer()
										.setDataSource(file.getAbsolutePath());
								PlayMusicService.getMediaPlayer().prepare();
								PlayMusicService.getMediaPlayer().start();
								if (TalkingBadgeActivity.VibrateOnPlaySound)
									TalkingBadgeActivity.vibrate
											.vibrate(PlayMusicService
													.getMediaPlayer()
													.getDuration());
							}
						}
						return;
					}

				}
			}
			Toast.makeText(TalkingBadgeActivity.this,
					"Your input is not illegal for our scenario!",
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

	}

	public void onBackPressed() {
		// 实现Home键效果
		// super.onBackPressed();这句话一定要注掉,不然又去调用默认的back处理方式了
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}

	public void exitProgrames() {
		super.onBackPressed();
		// DataStorage.writeSoundHistory(soundHistoryList);
		// Intent startMain = new Intent(Intent.ACTION_MAIN);
		// startMain.addCategory(Intent.CATEGORY_HOME);
		// startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// android.os.Process.killProcess(android.os.Process.myPid());
		// this.onDestroy();
		// this.finish();
	}

	public boolean onKeyDownback(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		// 获取手机当前音量值
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		float volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		float max = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		Log.e("VOL", volume + " " + max);
		if (volume / max == 1) {
			SetVolume(VOLUME_LOUD);
		} else if (volume / max > 0.5) {
			SetVolume(VOLUME_MEDIUM);
		}
		if (volume / max > 0.2) {
			SetVolume(VOLUME_LOW);
		}
		if (volume / max == 0) {
			SetVolume(VOLUME_SILENCE);
		}

		return true;

	}
}
