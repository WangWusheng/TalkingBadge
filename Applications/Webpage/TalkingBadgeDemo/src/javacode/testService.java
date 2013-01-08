package javacode;

public class testService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// TBWebService test = new TBWebService();
			// TBWebService.getDevicesList();
			SearchDevice.startSearchingService();

			for (double i = 0; i < 999999999; i = i + 0.5) {
			}
			// System.out.println(TBService.getDevicesList());
			// TBService.dbUpdate("INSERT INTO user values (default, 'wusheng', 'wusheng','M', 26, 'TalkingBadge');");
			// TBService.dbUpdate("INSERT INTO user values (default, 'user1', 'user1','F', 26, 'TalkingBadge');");
			// System.out.print(TBService.validateUser("wusheng", "wusheng"));
			// TBWebService.sendCommand("b0ec71740c41",001dfd8e7d42
			// "TB PLAYSOUND copenhagenw.wav");
			// new SPPService("b0ec71740c41", "TB PLAYSOUND copenhagenw.wav");

			// TBService tb = new TBService();
			// tb.messagesToBeSent.add(new String[]{"001dfd8e7d42",
			// "command1"});
			// tb.messagesToBeSent.add(new String[]{"b0ec71740c41",
			// "command1"});

			// System.out.println(TBService.sendFile("b0ec71740c41", "commmand",
			// "",""));
			System.out.println(TBService.sendCommand("b0ec71740c41",
					"TB PLAYSOUND commmand4"));
			// System.out.println(new
			// TBService().sendMessageForTB("nouser","b0ec71740c41",
			// "Copenhagen is a beautiful city", "en_GB", "F"));
			// System.out.println(new
			// TBService().sendMessageForTB("001dfd8e7d42", "command1", null,
			// null));
			// OPPService.text2sound2( "string.mp3", "String text");
		} catch (Exception e) {
			System.out.print("!!");
			e.printStackTrace();
		}
	}
}
