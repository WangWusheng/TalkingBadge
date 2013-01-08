import java.io.File;
import java.net.URL;
import java.util.Random;

import javax.media.protocol.FileTypeDescriptor;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipnode.ConnectData;
import com.blipsystems.blipnet.api.blipserver.BlipServerAccessException;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;
import com.blipsystems.blipnet.api.bluetooth.UUID;
import com.blipsystems.blipnet.api.event.EventCause;
import com.blipsystems.blipnet.api.profile.opp.OppClientEvent;
import com.blipsystems.blipnet.api.profile.opp.OppClientEventListener;
import com.blipsystems.blipnet.api.profile.opp.pushobjects.ObexFile;
import com.blipsystems.blipnet.api.profile.opp.pushobjects.ObexPushObject;
import com.blipsystems.blipnet.api.profile.spp.SppClientHandler;
import com.cloudgarden.audio.AudioFileSink;
import com.cloudgarden.audio.AudioFormatConverter;
import com.cloudgarden.audio.AudioMediaURLSink;
import com.cloudgarden.audio.AudioSink;
import com.cloudgarden.speech.CGAudioManager;
import com.sun.media.JMFSecurityManager;

public class OPPService {

	private BlipNodeHandle blipNodeHandle;
	private SppClientHandler sppClientHandler;
	private ConnectData connectData;
	//private UUID oppUUID = new UUID("DF3C7DF603CA493EAD93F6905ACAF3CF");
	private UUID oppUUID = new UUID("0000110500001000800000805F9B34FB");// for talkingbadge

	private String destDeviceMac = null;
	private String messageContent = null;
	private String messagename = null;
	String message = "";
	private UserDevice ud;

	public OPPService(String destDeviceMac, String messageContent) {
		super();
		this.destDeviceMac = destDeviceMac;
		this.messageContent = messageContent;
		init();
	}

	public OPPService(String destDeviceMac, String messageContent,
			String messagename) {
		super();
		this.destDeviceMac = destDeviceMac;
		this.messageContent = messageContent;
		this.messagename = messagename;
		init();
	}

	private void init() {
		if (messageContent.replaceAll(" ", "").isEmpty()) {
			System.out.println("Error: Empty message.");
			return;
		}
		try {
			SearchDevice.startSearchingService();
		} catch (BlipServerAccessException e) {
			e.printStackTrace();
		} catch (BlipServerConnectionException e) {
			e.printStackTrace();
		}
		for (UserDevice device : SearchDevice.devicesList) {
			if (device.getCd().getTerminalID().toString().equals(destDeviceMac)) {
				ud = device;
				break;
			}
		}
		if (ud == null) {
			message = "Send command failed, because the device can not be found.";
			System.out.println(message);
			return;
		}
		send();
	}

	public void send() {
		// final String theFilepath = text2sound2(messagename, messageContent);
		final String theFilepath = "res\\string.mp3";

		File theFile = new File(theFilepath);
		if (!theFile.exists()) {
			message = "File does not exist.";
			System.out.println(message);
		} else {
			try {
				final BlipNodeHandle handle = SearchDevice.server
						.getBlipNodeHandle(ud.getBlipMac());
				handle.getOppClientHandler().addEventListener(
						new OppClientEventListener() {
							public void handleOppClientEvent(OppClientEvent evt) {
								int eventID = evt.getEventID();

								switch (eventID) {
								case OppClientEvent.OBEX_PUSH_COMPLETED:
									message = theFilepath
											+ " has been pushed to "
											+ evt.getTerminalID();
									System.out.println(message);
									break;
								case OppClientEvent.OBEX_PUSH_FAILED:
									message = "Push "
											+ theFilepath
											+ " to '"
											+ evt.getTerminalID()
											+ "' failed. Cause is "
											+ EventCause.causeName(evt
													.getCause());
									System.out.println(message);
									break;
								}
							}
						});

				ObexPushObject opo = new ObexFile(theFile);
				handle.getOppClientHandler().push(ud.getCd(), opo);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	protected static String text2sound2(String name, String text) {
		if (name == null)
			name = new Long((new Random(System.currentTimeMillis())).nextLong())
					.toString();
		if (!name.endsWith(".mp3"))
			name = name.concat(".mp3");
		String outPutFile = "res\\" + name;
		if (text.equals(""))
			return outPutFile;
		try {
			com.sun.media.util.Registry.set("secure.allowSaveFileFromApplets",
					true);

			JMFSecurityManager.enableSecurityFeatures();
			// System.setSecurityManager(new JMFSecurityManager());
			Synthesizer synth = Central.createSynthesizer(null);
			CGAudioManager audioMan = (CGAudioManager) synth.getAudioManager();
			synth.allocate();
			synth.resume();
			AudioFormat fmt;
			fmt = audioMan.getAudioFormat();
			System.out.println(fmt);
			AudioFileFormat ff = new AudioFileFormat(AudioFileFormat.Type.WAVE,
					fmt, AudioSystem.NOT_SPECIFIED);
			AudioSink sink = null;
			if (true) {
				// mp3
				sink = new AudioMediaURLSink(new URL("file:" + outPutFile),
						FileTypeDescriptor.MPEG_AUDIO);
				sink.setAudioFormat(fmt);
			} else {
				// wav
				sink = new AudioFileSink(new File(outPutFile), ff);
			}
			System.out.println(sink.getAudioFormat());
			new AudioFormatConverter(audioMan, sink, true);
			audioMan.startSending();
			synth.speakPlainText(text, null);
			synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
			audioMan.closeOutput();
			sink.drain();
			audioMan.setDefaultOutput();
			synth.deallocate();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return outPutFile;
	}

}
