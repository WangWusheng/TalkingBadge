package genie.talkingbadgeserver;

import com.blipsystems.blipnet.api.blipserver.BlipServerAccessException;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;

public class StartServer {

	public static void main(String[] args)
			throws BlipServerConnectionException, BlipServerAccessException {
		UserInterface ui = new UserInterface();
		SearchDevice sd = new SearchDevice();
		sd.Start();

	}

}
