package genie.talkingbadgeserver;

import com.blipsystems.blipnet.api.blipserver.BlipServerAccessException;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;

public class StartServer {

	public static void main(String[] args)
			throws BlipServerConnectionException, BlipServerAccessException {
		// Using TTS
		// UserInterface ui = new UserInterface(true);
		//do not using TTS
		 UserInterface ui = new UserInterface(false);
		 SearchDevice sd = new SearchDevice();
		 sd.Start();
		// UserInterface.text2sound( "name", "String text");

	}

}
