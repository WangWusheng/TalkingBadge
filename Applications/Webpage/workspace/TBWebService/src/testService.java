public class testService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//TBWebService test = new TBWebService();
			//TBWebService.getDevicesList();
			// SearchDevice.startSearchingService();

			//for (double i = 0; i < 999999999; i = i + 0.25) {}
			//System.out.println(TBWebService.getDevicesList());
			TBWebService.dbUpdate("INSERT INTO user values (default, 'wusheng', 'wusheng','M', 26, 'TalkingBadge');");
			TBWebService.dbUpdate("INSERT INTO user values (default, 'user1', 'user1','F', 26, 'TalkingBadge');");
			System.out.print(TBWebService.dbQueryAllUsers());
			//TBWebService.sendCommand("b0ec71740c41", "TB PLAYSOUND copenhagenw.wav");
			// new SPPService("b0ec71740c41", "TB PLAYSOUND copenhagenw.wav");
			// new OPPService("b0ec71740c41", "command1");
			// OPPService.text2sound2( "string.mp3", "String text");
		} catch (Exception e) {
			System.out.print("!!");
			e.printStackTrace();
		}
	}
}
