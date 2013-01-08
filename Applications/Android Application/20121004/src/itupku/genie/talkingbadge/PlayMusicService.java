package itupku.genie.talkingbadge;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PlayMusicService extends Service {
	private static final String TAG = "Music";
	private final IBinder mBinder = new MusicBinder();
	private static MediaPlayer mediaPlayer = null;

	public static MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public static void setMediaPlayer(MediaPlayer mediaPlayer) {
		PlayMusicService.mediaPlayer = mediaPlayer;
	}

	@Override
	public void onDestroy() {
		if (mediaPlayer != null)
			mediaPlayer.release();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mediaPlayer = MediaPlayer.create(getApplicationContext(),
				R.raw.introduction_message);
		mediaPlayer.setVolume(1.0f, 1.0f);
		try {
			if ((TalkingBadgeActivity.batteryLevel < TalkingBadgeActivity.lowBatteryAlertLevel)
					&& (!TalkingBadgeActivity.batteryCharging)) {
				PlayMusicService.getMediaPlayer().reset();
				PlayMusicService.getMediaPlayer().setDataSource(
						DataStorage.findFile(
								TalkingBadgeActivity.lowBatteryAlertSoundFile)
								.getAbsolutePath());
				PlayMusicService.getMediaPlayer().prepare();
				PlayMusicService.getMediaPlayer().start();
				if (TalkingBadgeActivity.VibrateOnPlaySound)
					TalkingBadgeActivity.vibrate.vibrate(PlayMusicService
							.getMediaPlayer().getDuration());
			}
			while (PlayMusicService.getMediaPlayer().isPlaying()) {
			}
			PlayMusicService.getMediaPlayer().reset();
			PlayMusicService.getMediaPlayer().setDataSource(
					DataStorage.findFile(
							TalkingBadgeActivity.introductionMessageSoundFile)
							.getAbsolutePath());
			PlayMusicService.getMediaPlayer().prepare();
			PlayMusicService.getMediaPlayer().start();
			if (TalkingBadgeActivity.VibrateOnPlaySound)
				TalkingBadgeActivity.vibrate.vibrate(PlayMusicService
						.getMediaPlayer().getDuration());
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return startId;
	}

	@Override
	public IBinder onBind(Intent intent) {
		mediaPlayer = MediaPlayer.create(getApplicationContext(),
				R.raw.introduction_message);
		mediaPlayer.setVolume(1.0f, 1.0f);
		try {
			if ((TalkingBadgeActivity.batteryLevel < TalkingBadgeActivity.lowBatteryAlertLevel)
					&& (!TalkingBadgeActivity.batteryCharging)) {
				PlayMusicService.getMediaPlayer().reset();
				PlayMusicService.getMediaPlayer().setDataSource(
						DataStorage.findFile(
								TalkingBadgeActivity.lowBatteryAlertSoundFile)
								.getAbsolutePath());
				PlayMusicService.getMediaPlayer().prepare();
				PlayMusicService.getMediaPlayer().start();
				if (TalkingBadgeActivity.VibrateOnPlaySound)
					TalkingBadgeActivity.vibrate.vibrate(PlayMusicService
							.getMediaPlayer().getDuration());
			}
			while (PlayMusicService.getMediaPlayer().isPlaying()) {
			}
			PlayMusicService.getMediaPlayer().reset();
			PlayMusicService.getMediaPlayer().setDataSource(
					DataStorage.findFile(
							TalkingBadgeActivity.introductionMessageSoundFile)
							.getAbsolutePath());
			PlayMusicService.getMediaPlayer().prepare();
			PlayMusicService.getMediaPlayer().start();
			if (TalkingBadgeActivity.VibrateOnPlaySound)
				TalkingBadgeActivity.vibrate.vibrate(PlayMusicService
						.getMediaPlayer().getDuration());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		// Log.d(TAG, "return mBinder");
		return mBinder;
	}

	public class MusicBinder extends Binder {
		PlayMusicService getService() {
			return PlayMusicService.this;
		}
	}
}
