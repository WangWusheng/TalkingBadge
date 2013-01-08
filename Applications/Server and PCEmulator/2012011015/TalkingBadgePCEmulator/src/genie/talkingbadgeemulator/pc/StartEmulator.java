package genie.talkingbadgeemulator.pc;

public class StartEmulator {

	public static void main(String[] args) {
		Control soundcontrol = new Control();
		UI ui = new UI(soundcontrol);

		new Thread(new OBEXService(ui)).start();
		new Thread(new SPPService(ui)).start();

	}

}
