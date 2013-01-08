package genie.talkingbadgeemulator.pc;

import java.io.File;
import java.util.Date;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;

public class Control {
	private String introductionMessageSoundFile = "introduction-message.wav";
	private String lowBatteryAlertSoundFile = "low-battery-alert.wav";
	private String powerOffAlertSoundFile = "power-off-alert.wav";
	private String loudVolumeAlertSoundFile = "loud-volume-alert.wav";
	private String mediumVolumeAlertSoundFile = "medium-volume-alert.wav";
	private String lowVolumeAlertSoundFile = "low-volume-alert.wav";
	private String lastPlayedSoundFile = null;
	private File soundFile = null;
	private AudioInputStream audioStream = null;
	private Clip clip = null;
	private FloatControl volCtrl;
	private Float volumn = (float) 6;
	private int batteryInitLevel = 100;
	private int batteryLevelForAlarm = 50;
	private Date startTime = null;

	public String getLastPlayedSoundFile() {
		return lastPlayedSoundFile;
	}

	public void setLastPlayedSoundFile(String lastPlayedSoundFile) {
		this.lastPlayedSoundFile = lastPlayedSoundFile;
	}

	public Clip getClip() {
		return clip;
	}

	public void setClip(Clip clip) {
		this.clip = clip;
	}

	public FloatControl getVolCtrl() {
		return volCtrl;
	}

	public void setVolCtrl(FloatControl volCtrl) {
		this.volCtrl = volCtrl;
	}

	Control() {
		startTime = new Date();
		playLowBatteryAlarm();
		playIntroductionMessage();
	}

	public String playSoundFile(String filename) {
		soundFile = new File("X\\" + filename);
		try {
			audioStream = AudioSystem.getAudioInputStream(soundFile);
			AudioFormat format = audioStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(audioStream);

			volCtrl = (FloatControl) clip
					.getControl(FloatControl.Type.MASTER_GAIN);
			volCtrl.setValue(volumn);
			clip.start();
		} catch (Exception e) {
			return e.getLocalizedMessage();
		}
		if (filename != lowBatteryAlertSoundFile
				&& filename != powerOffAlertSoundFile
				&& filename != loudVolumeAlertSoundFile
				&& filename != lowVolumeAlertSoundFile
				&& filename != mediumVolumeAlertSoundFile)
			lastPlayedSoundFile = filename;
		return "PLAYED " + filename;
	}

	public void setVolumn(float num) {
		volumn = num;
		volCtrl.setValue(num);

	}

	public void playPowerOffAlert() {
		if (clip != null) {
			clip.stop();
		}
		playSoundFile(powerOffAlertSoundFile);

	}

	public void playLoudVolumeAlert() {
		if (clip != null) {
			clip.stop();
		}
		playSoundFile(loudVolumeAlertSoundFile);
	}

	public void playMediumVolumeAlert() {
		if (clip != null) {
			clip.stop();
		}
		playSoundFile(mediumVolumeAlertSoundFile);
	}

	public void playLowVolumeAlertSoundFile() {
		if (clip != null) {
			clip.stop();
		}
		playSoundFile(lowVolumeAlertSoundFile);
	}

	public void playIntroductionMessage() {
		if (clip != null) {
			while (clip.isActive()) {
			}
		}
		playSoundFile(introductionMessageSoundFile);
	}

	public void playLowBatteryAlarm() {
		long interval = ((new Date()).getTime() - startTime.getTime()) / 1000;
		if (batteryInitLevel - interval < batteryLevelForAlarm) {
			if (clip != null) {
				clip.stop();
			}
			playSoundFile(lowBatteryAlertSoundFile);
		}
	}

	public void replayLastMessage() {
		if (clip != null) {
			clip.stop();
		}
		playSoundFile(lastPlayedSoundFile);
	}

	public String batteryCheck() {
		long interval = ((new Date()).getTime() - startTime.getTime()) / 1000;
		if (batteryInitLevel - interval < 0) {
			return "1%";
		} else {
			return (batteryInitLevel - interval) + "%";
		}
	}
}
