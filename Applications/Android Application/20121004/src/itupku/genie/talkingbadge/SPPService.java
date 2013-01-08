package itupku.genie.talkingbadge;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.UUID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SPPService extends Service {
	private static final String TAG = "SPP";
	private final IBinder mBinder = new SPPBinder();
	private static final String serviceName = "SPP";
	private static final UUID SPPUUID = UUID
			.fromString("DF3C7DF6-03CA-493E-AD93-F6905ACAF3CF");
	private BluetoothAdapter mBtAdapter = null;
	private BluetoothSocket socket = null;
	static final int ID_LED = 19871103;

	public int onStartCommand(Intent intent, int flags, int startId) {
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		(new AcceptThread()).start();
		return startId;
	}

	public IBinder onBind(Intent intent) {
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		(new AcceptThread()).start();
		return mBinder;
	}

	public void onDestroy() {
		super.onDestroy();
		// Make sure we're not doing discovery anymore
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}
		// Unregister broadcast listeners
		// this.unregisterReceiver(mReceiver);
	}

	private class AcceptThread extends Thread {
		// The local server socket
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			// Log.d(TAG, "AcceptThread");
			BluetoothServerSocket tmp = null;
			// Create a new listening server socket
			try {
				tmp = mBtAdapter.listenUsingInsecureRfcommWithServiceRecord(
						serviceName, SPPUUID);
			} catch (IOException e) {
				Log.e(TAG, "listen() failed", e);
			}
			mmServerSocket = tmp;
		}

		public void run() {

			// Keep listening until exception occurs or a socket is returned
			while (true) {
				if (socket == null) {
					try {
						socket = mmServerSocket.accept();
					} catch (IOException e) {
						break;
					}
					// If a connection was accepted
					if (socket != null) {
						// Log.d(TAG, "a connection was accepted");
						// Do work to manage the connection
						(new manageConnectedSocket(socket)).start();
					}
				}
			}
		}

	}

	private class manageConnectedSocket extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public manageConnectedSocket(BluetoothSocket theSocket) {
			// Log.d(TAG, "manageConnectedSocket ");
			mmSocket = theSocket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = mmSocket.getInputStream();
				tmpOut = mmSocket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			// Log.d(TAG, "run manageConnectedSocket");

			try {
				byte[] lineRead1 = new byte[1024];
				mmInStream.read(lineRead1);
				// BufferedReader bReader = new BufferedReader(
				// new InputStreamReader(mmInStream));
				String lineRead = new String(lineRead1);
				lineRead = lineRead.substring(0, lineRead.indexOf('\r'));
				// Log.d(TAG,"Message recieved from "+
				// mmSocket.getRemoteDevice() + ": "+ lineRead);
				sendMessage("APPENDMESSAGE", "Message recieved from "
						+ mmSocket.getRemoteDevice() + ": " + lineRead);
				String replyMessage = executeCommand(lineRead)+"\r\nOVER\r\n";

				PrintWriter pWriter = new PrintWriter(new OutputStreamWriter(
						mmOutStream));
				pWriter.write(replyMessage);
				pWriter.flush();
				sendMessage("APPENDMESSAGE",
						"Message sent to " + mmSocket.getRemoteDevice() + ": "
								+ replyMessage);
				// Log.d(TAG, "Message sent to " +
				// mmSocket.getRemoteDevice()+ ": " + replyMessage);

				pWriter.close();
				mmInStream.close();
				mmOutStream.close();
				mmSocket.close();
				socket = null;
			} catch (IOException e) {
				Log.e(TAG, "disconnected", e);
			}

		}
	}

	public class SPPBinder extends Binder {
		SPPService getService() {
			return SPPService.this;
		}
	}

	public BluetoothAdapter getmBtAdapter() {
		return mBtAdapter;
	}

	public String executeCommand(String lineRead) {
		String[] command = lineRead.split(" ");
		if (command[0].equals("TB")) {
			if (command[1].equals("PLAYSOUNDEP"))
				try {
					if (command.length != 3)
						return "BAD COMMAND";
					else if (!TalkingBadgeActivity.EarphonePlugedIn) {
						return "EARPHONE NOT READY";
					} else {
						File file = DataStorage.findFile(command[2]);
						if (file == null) {
							return "File not exist: " + command[2];
						}
						PlayMusicService.getMediaPlayer().reset();
						PlayMusicService.getMediaPlayer().setDataSource(
								file.getAbsolutePath());
						PlayMusicService.getMediaPlayer().prepare();
						PlayMusicService.getMediaPlayer().start();
						if (TalkingBadgeActivity.VibrateOnPlaySound)
							TalkingBadgeActivity.vibrate
									.vibrate(PlayMusicService.getMediaPlayer()
											.getDuration());
						TalkingBadgeActivity.soundHistoryList.add(command[2]);
						return "PLAYED " + command[2];
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					return "PLAYSOUND FAILED";
				}
			else if (command[1].equals("PLAYSOUND"))
				try {
					if (command.length != 3)
						return "BAD COMMAND";
					else {
						File file = DataStorage.findFile(command[2]);
						if (file == null) {
							return "File not exist: " + command[2];
						}
						PlayMusicService.getMediaPlayer().reset();
						PlayMusicService.getMediaPlayer().setDataSource(
								file.getAbsolutePath());
						PlayMusicService.getMediaPlayer().prepare();
						PlayMusicService.getMediaPlayer().start();
						if (TalkingBadgeActivity.VibrateOnPlaySound)
							TalkingBadgeActivity.vibrate
									.vibrate(PlayMusicService.getMediaPlayer()
											.getDuration());
						if (TalkingBadgeActivity.volumeLevel
								.equals(TalkingBadgeActivity.VOLUME_SILENCE)) {
							ledControl(true);
						}
						TalkingBadgeActivity.soundHistoryList.add(command[2]);
						return "PLAYED " + command[2];
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					return "PLAYSOUND FAILED";
				}
			else if (command[1].equals("BATTERYCHECK"))
				try {
					if (command.length != 2)
						return "BAD COMMAND";
					else {
						return TalkingBadgeActivity.batteryLevel + "%";
					}
				} catch (Exception e) {
					return "BATTERYCHECK FAILED";
				}
			else if (command[1].equals("VOLUMECHECK"))
				try {
					if (command.length != 2)
						return "BAD COMMAND";
					else {
						return TalkingBadgeActivity.volumeLevel;
					}
				} catch (Exception e) {
					return "VOLUMECHECK FAILED";
				}

			else if (command[1].equals("VIBRATIONCHECK"))
				try {
					if (command.length != 2)
						return "BAD COMMAND";
					else {
						if (TalkingBadgeActivity.VibrateOnPlaySound)
							return "VIBRATION ON";
						else
							return "VIBRATION OFF";
					}
				} catch (Exception e) {
					return "VIBRATIONCHECK FAILED";
				}
			else if (command[1].equals("VIBRATIONSET"))
				try {
					if (command.length != 3)
						return "BAD COMMAND";
					else {
						if (command[2].equals("ON")) {
							TalkingBadgeActivity.VibrateOnPlaySound = true;
							return "SET VIBRATION ON";
						} else if (command[2].equals("OFF")) {
							TalkingBadgeActivity.VibrateOnPlaySound = false;
							return "SET VIBRATION OFF";
						} else {
							return "BAD VIBRATION VALUE";
						}
					}
				} catch (Exception e) {
					return "VIBRATIONSET FAILED";
				}
			else if (command[1].equals("FILEDELETE"))
				try {
					if (command.length != 3) {
						return "BAD COMMAND";
					} else {
						File file = DataStorage.findFile(command[2]);
						if (file == null) {
							return "File not exist: " + command[2];
						} else if (file.delete())
							return "DELETE FILE " + command[2];
					}
				} catch (Exception e) {
					return "FILEDELETE FAILED";
				}
			else if (command[1].equals("FILELIST"))
				try {
					if (command.length != 2)
						return "BAD COMMAND";
					else {
						return DataStorage.ListFile();
					}
				} catch (Exception e) {
					return "FILELIST FAILED";
				}
			else if (command[1].equals("FILENAMECHANGE"))
				try {
					if (command.length != 4)
						return "BAD COMMAND";
					else {
						File file1 = DataStorage.findFile(command[2]);
						if (file1 == null) {
							return "File not exist: " + command[2];
						}
						File file2 = new File(file1.getParent(), command[3]);
						if (file1.renameTo(file2))
							return command[2] + " BECOME " + command[3];
						else
							return "FILENAMECHANGE FAILED";
					}
				} catch (Exception e) {
					return "FILENAMECHANGE FAILED";
				}
			else if (command[1].equals("LOGCLEAR"))
				try {
					if (command.length != 2)
						return "BAD COMMAND";
					else {
						DataStorage.clearLog();
						return "LOG CLEARED";
					}
				} catch (Exception e) {
					return "LOGCLEAR FAILED";
				}
			else if (command[1].equals("LOGREAD"))
				try {
					if (command.length != 2)
						return "BAD COMMAND";
					else {
						return DataStorage.readLog();
					}
				} catch (Exception e) {
					return "LOGREAD FAILED";
				}
			else if (command[1].equals("BACKGROUNDSET"))
				try {
					if (command.length != 3)
						return "BAD COMMAND";
					else {
						File picFile = DataStorage.findFile(command[2]);
						if (picFile == null) {
							return "FILE NOT EXIST " + command[2];
						}
						sendMessage(command[1], command[2]);
						return "BACKGROUND CHANGED";
					}
				} catch (Exception e) {
					return "BACKGROUNDSET FAILED";
				}
			else if (command[1].equals("THIEFMODESET"))
				try {
					if (command.length != 3)
						return "BAD COMMAND";
					else {
						if (command[2].equals("ON")) {
							sendMessage(command[1], command[2]);
							return "SET THIEFMODE ON";
						} else if (command[2].equals("OFF")) {
							sendMessage(command[1], command[2]);
							return "SET THIEFMODE OFF";
						} else
							return "BAD THIEFMODE VALUE";
					}
				} catch (Exception e) {

					Log.e(TAG, e.toString());
					return "THIEFMODESET FAILED";
				}
			else if (command[1].equals("VOLUMESET"))
				try {
					if (command.length != 3)
						return "BAD COMMAND";
					else {
						if (command[2].equals(TalkingBadgeActivity.VOLUME_LOUD)
								|| command[2]
										.equals(TalkingBadgeActivity.VOLUME_LOW)
								|| command[2]
										.equals(TalkingBadgeActivity.VOLUME_MEDIUM)
								|| command[2]
										.equals(TalkingBadgeActivity.VOLUME_SILENCE)) {
							sendMessage(command[1], command[2]);
							return "SET VOLUME " + command[2];
						} else
							return "BAD VOLUME VALUE";
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					return "VOLUMESET FAILED" + e;
				}
			else if (command[1].equals("SHUTDOWN"))
				try {
					if (command.length != 2)
						return "BAD COMMAND";
					else {
						sendMessage(command[1], "");
						return "TURN OFF";
					}
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
					return "SHUTDOWN FAILED";
				}
			return "BAD COMMAND";

		} else
			return "BAD COMMAND";
	}

	public void sendMessage(String command, String theMessage) {
		LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager
				.getInstance(SPPService.this);
		Intent tempint = new Intent("genie.talkingbadge");
		tempint.putExtra("Command", command);
		tempint.putExtra("Message", theMessage);
		mLocalBroadcastManager.sendBroadcast(tempint);
	}

	void ledControl(boolean on) {

		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		Notification notification = new Notification();
		// notification.ledARGB = 0xff00ff00; //
		notification.defaults = Notification.DEFAULT_LIGHTS;
		// 这里是颜色，我们可以尝试改变，理论上0xFF0000是红色，0x00FF00是绿色
		// notification.flags = Notification.FLAG_SHOW_LIGHTS;
		// notification.ledOnMS = 100;
		// notification.ledOffMS = 100;

		if (on) {
			nm.notify(ID_LED, notification);
			// Log.e("LED", "ON");
		} else {
			nm.cancel(ID_LED);
			// Log.e("LED", "OFF");
		}
	}
}
