package javacode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipserver.BlipServerAccessException;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;
import com.blipsystems.blipnet.api.event.EventCause;
import com.blipsystems.blipnet.api.profile.opp.OppClientEvent;
import com.blipsystems.blipnet.api.profile.opp.OppClientEventListener;
import com.blipsystems.blipnet.api.profile.opp.pushobjects.ObexFile;
import com.blipsystems.blipnet.api.profile.opp.pushobjects.ObexPushObject;
//import com.cloudgarden.audio.AudioFileSink;
//import com.cloudgarden.audio.AudioFormatConverter;
//import com.cloudgarden.audio.AudioMediaURLSink;
//import com.cloudgarden.audio.AudioSink;
//import com.cloudgarden.speech.CGAudioManager;
//import com.sun.media.JMFSecurityManager;
import com.blipsystems.blipnet.api.profile.spp.SppClientEventListener;
import com.iSpeech.TTSResult;
import com.iSpeech.iSpeechSynthesis;

public class OPPService implements OppClientEventListener {

	// private UUID oppUUID = new UUID("DF3C7DF603CA493EAD93F6905ACAF3CF");
	// private UUID oppUUID = new UUID("0000110500001000800000805F9B34FB");//
	// for talkingbadge

	private String destDeviceMac = null;
	private static String messageContent = null;
	String message = "init message";
	private UserDevice ud = null;
	boolean lock = true;
	private static String filePath = "C:\\wusheng\\res\\";
	static String theFilepath = "";
	BlipNodeHandle handle = null;
	private static String voiceLanguage = null;
	private static String voiceGender = null;
	public static String api = "81ee22255c029d5755445fb11c2f6a40";
	// public static String api = "developerdemokeydeveloperdemokey";
	// false is development and true is production
	public static boolean production = false;

	public String sendMessageFile(String destDeviceMac, String messageContent,
			String language, String gender) throws Exception {
		this.destDeviceMac = destDeviceMac;
		OPPService.messageContent = messageContent;
		OPPService.voiceGender = gender;
		OPPService.voiceLanguage = language;
		// System.out.println("OPP"+destDeviceMac);
		String result = "Send file to " + destDeviceMac + ": " + init(true);

		return result;
	}

	private String init(boolean usingTTS) throws Exception {
		if (usingTTS && messageContent.replaceAll(" ", "").isEmpty()) {
			return "Error: Empty message.";
		}
		try {
			SearchDevice.startSearchingService();
		} catch (BlipServerAccessException e) {
			return e.toString();
		} catch (BlipServerConnectionException e) {
			return e.toString();
		}
		for (UserDevice device : SearchDevice.devicesList) {
			if (device.getCd().getTerminalID().toString().equals(destDeviceMac)) {
				ud = device;
				break;
			}
		}
		if (ud == null) {
			message = "Send message file failed, because the device can not be found.";
			// System.out.println(message);
			return message;
		} else
			return send(usingTTS);
	}

	public String send(boolean usingTTS) throws Exception {
		// final String theFilepath = text2sound2(messagename, messageContent);
		// final String theFilepath = "C:\\wusheng\\res\\string.mp3";
		// theFilepath = acapelaTTS();
		if (usingTTS) {
			theFilepath = iSpeechTTS();
		}
		//System.out.println("Send:"+theFilepath);
		File theFile = new File(theFilepath);
		if (!theFile.exists()) {
			message = "File does not exist.";
		} else {
			try {
				handle = SearchDevice.server.getBlipNodeHandle(ud.getBlipMac());
				handle.getOppClientHandler().addEventListener(this);

				ObexPushObject opo = new ObexFile(theFile);
				handle.getOppClientHandler().push(ud.getCd(), opo);
				while (lock) {
				}
				// handle.getOppClientHandler().removeSession(
				// ud.getCd().getTerminalID());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		return message;
	}

	public void handleOppClientEvent(OppClientEvent evt) {
		int eventID = evt.getEventID();
		switch (eventID) {
		case OppClientEvent.OBEX_PUSH_COMPLETED:
			message = "success. The file is " + theFilepath;
			close(this);
			lock = false;
			break;
		case OppClientEvent.OBEX_PUSH_FAILED:
			message = "Push " + theFilepath + " to '" + evt.getTerminalID()
					+ "' failed. Cause is "
					+ EventCause.causeName(evt.getCause());
			close(this);
			lock = false;
			break;
		}
	}

	public static String iSpeechTTS() {
		try {
			iSpeechSynthesis iSpeechTTS = iSpeechSynthesis.getInstance(api,
					production);
			// String format = "wav"; or mp3
			String format = "mp3";
			iSpeechTTS.setOptionalCommand("format", format);
			File file = new File(filePath + (int) (Math.random() * 1000) + "."
					+ format);
			file.deleteOnExit();
			// file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);

			iSpeechTTS.setOptionalCommand("voice", chooseVoiceiSpeech());
			TTSResult result = iSpeechTTS.speak(messageContent);
			// System.out.println(messageContent+chooseVoiceiSpeech());
			DataInputStream in = result.getDataInputStream();

			byte[] buffer = new byte[2048];
			int size = 0;
			while ((size = in.read(buffer)) > -1) {
				out.write(buffer, 0, size);
			}
			return file.getAbsolutePath();
		} catch (Exception e) {
			return "null " + e.toString();
		}
	}

	public String acapelaTTS() throws Exception {
		// System.out.println(messageContent);
		String messageContent4URL = messageContent.replace(" ", "%20")
				.replace("\r", "%20").replace("\n", "%20").replace("=", "%20")
				.replace("/", "%20").replace("&", "%20");
		while (messageContent4URL.endsWith("%20")) {
			messageContent4URL = messageContent4URL.substring(0,
					messageContent4URL.length() - 3);
		}
		// System.out.println(messageContent4URL);
		URL url = new URL(
				"http://vaas.acapela-group.com/Services/Synthesizer?prot_vers=2&cl_env=PHP_APACHE_2.2.3_LINUX_SUSE&cl_vers=1-30&cl_login=EVAL_VAAS&cl_app=EVAL_2655723&cl_pwd=9to9au1w&req_voice="
						+ chooseVoiceacapela()
						+ "&req_text="
						+ messageContent4URL + "&req_vol=65535");
		// System.out.println(url);
		URLConnection urlc = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				urlc.getInputStream()));
		String inputLine;
		String resultFromTTS = "";

		while ((inputLine = in.readLine()) != null)
			resultFromTTS = resultFromTTS + inputLine;
		in.close();
		String fileFromTTS = resultFromTTS.substring(
				resultFromTTS.indexOf("http://vaas.acapela-group.com"),
				resultFromTTS.indexOf("&snd_size="));
		return saveToFile(fileFromTTS, (int) (Math.random() * 1000) + ".mp3");
	}

	public String saveToFile(String destUrl, String fileName)
			throws IOException {
		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		HttpURLConnection httpUrl = null;
		URL url = null;
		byte[] buf = new byte[8096];
		int size = 0;
		String fileFullPath = filePath + fileName;
		File theFile = new File(fileFullPath);

		theFile.deleteOnExit();

		// 建立链接
		url = new URL(destUrl);
		httpUrl = (HttpURLConnection) url.openConnection();
		// 连接指定的资源
		httpUrl.connect();
		// 获取网络输入流
		bis = new BufferedInputStream(httpUrl.getInputStream());
		// 建立文件
		fos = new FileOutputStream(fileFullPath);
		// 保存文件
		while ((size = bis.read(buf)) != -1)
			fos.write(buf, 0, size);

		fos.close();
		bis.close();
		httpUrl.disconnect();
		return fileFullPath;
	}

	private void close(OPPService oppService) {
		// Deregister event listeners
		try {
			handle.getOppClientHandler().removeEventListener(oppService);
		} catch (BlipServerConnectionException e) {
			message += e.toString();
		}
	}

	private static String chooseVoiceiSpeech() {
		if (voiceLanguage == null)
			voiceLanguage = "usenglish";
		if (voiceGender == null)
			voiceGender = "F";
		String TTSGenderFormat = voiceGender.equals("F") ? "female" : "male";
		String TTSOppositeGenderFormat = voiceGender.equals("F") ? "male"
				: "female";
		String result = voiceLanguage + TTSGenderFormat;
		for (String voice : TBService.iSpeechVOICE_LIST) {
			// System.out.println(voice[0]+voice[4]+voice[5]);
			if (voice.equals(result)) {
				return result;
			}
		}
		return voiceLanguage + TTSOppositeGenderFormat;
	}

	private String chooseVoiceacapela() {
		if (voiceLanguage == null)
			voiceLanguage = "English (USA)";
		if (voiceGender == null)
			voiceGender = "F";
		String result = null;
		for (String[] voice : TBService.acapelaVOICE_LIST) {
			// System.out.println(voice[0]+voice[4]+voice[5]);
			if (voice[0].equals(voiceLanguage) && voice[4].equals(voiceGender)) {
				result = voice[5];
				break;
			}
		}
		if (result == null) {
			for (String[] voice : TBService.acapelaVOICE_LIST) {
				if (voice[0].equals(voiceLanguage)) {
					result = voice[5];
					break;
				}
			}
		}
		return result == null ? "heather22k" : result;
	}

	public static String iSpeechTTS(String msg, String language, String gender) {
		messageContent = msg;
		voiceLanguage = language;
		voiceGender = gender;
		return iSpeechTTS();
	}

	public String sendMessageFile(String terminalMac, String filePath)
			throws Exception {
		destDeviceMac = terminalMac;
		OPPService.theFilepath = filePath;
		//System.out.println(OPPService.theFilepath);
		// System.out.println("OPP"+destDeviceMac);
		String result = "Send file to " + destDeviceMac + ": " + init(false);

		return result;
	}
}
