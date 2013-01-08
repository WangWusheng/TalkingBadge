package genie.talkingbadgeserver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;

import javax.media.protocol.FileTypeDescriptor;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.blipsystems.blipnet.api.blipnode.BlipNodeHandle;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnection;
import com.blipsystems.blipnet.api.bluetooth.BluetoothAddress;
import com.blipsystems.blipnet.api.bluetooth.UUID;
import com.blipsystems.blipnet.api.event.EventCause;
import com.blipsystems.blipnet.api.profile.opp.OppClientEvent;
import com.blipsystems.blipnet.api.profile.opp.OppClientEventListener;
import com.blipsystems.blipnet.api.profile.opp.pushobjects.ObexFile;
import com.blipsystems.blipnet.api.profile.opp.pushobjects.ObexPushObject;
import com.cloudgarden.audio.AudioFileSink;
import com.cloudgarden.audio.AudioFileSource;
import com.cloudgarden.audio.AudioFormatConverter;
import com.cloudgarden.audio.AudioMediaURLSink;
import com.cloudgarden.audio.AudioMediaURLSource;
import com.cloudgarden.audio.AudioSink;
import com.cloudgarden.audio.AudioSplitter;
import com.cloudgarden.speech.CGAudioManager;
import com.cloudgarden.speech.userinterface.SpeechEngineChooser;

import genie.talkingbadgeserver.UserDevice;

public class UserInterface extends JFrame {
	protected static UserInterface UI = null;
	private static String selectDeviceString = "";
	private static JTextArea historyArea;
	private static JTextArea replyArea1;
	private static JTextArea replyArea2;
	private static JList<UserDevice> devicesList;
	private static BlipServerConnection server = null;
	private static String message;
	private static String previousMessage;
	private static JTextArea commandArea;
	private static JTextField fileNameText = null;
	private static JTextArea fileContentArea;
	// private UUID sppUUID = new UUID("0000110100001000800000805F9B34FB");
	private UUID sppUUID = new UUID("DF3C7DF603CA493EAD93F6905ACAF3CF");
	static File logFile = new File("res//log.txt");
	private static boolean UsingTTS = true;

	private JMenuBar menuBar = new JMenuBar();
	private JMenu menuFile = new JMenu("File");
	private JMenu menuTab = new JMenu("Tab");
	private JMenu menuHelp = new JMenu("Help");
	private JMenuItem menuItemExit = new JMenuItem("Exit");
	private ButtonGroup buttonGroupWindow = new ButtonGroup();
	private JRadioButton menuItemSendCommand = new JRadioButton("Send Command");
	private JRadioButton menuItemSendFile = new JRadioButton("Send File");
	private JRadioButton menuItemHistory = new JRadioButton("History");
	private JMenuItem menuItemAbout = new JMenuItem("About");

	public static JTextArea getCommandArea() {
		return commandArea;
	}

	public UUID getSppUUID() {
		return sppUUID;
	}

	public static void setDevicesList(JList<UserDevice> devicesList) {
		UserInterface.devicesList = devicesList;
	}

	public static String getPreviousMessage() {
		return previousMessage;
	}

	public static void setPreviousMessage(String thePreviousMessage) {
		previousMessage = thePreviousMessage;
	}

	public static BlipServerConnection getServer() {
		return server;
	}

	public static void setServer(BlipServerConnection se) {
		UserInterface.server = se;
	}

	public static JList<UserDevice> getDevicesList() {
		return devicesList;
	}

	UserInterface(boolean tts) {

		super("TalkingBadge Server");
		UI = this;
		UsingTTS = tts;
		// Left is device list, right is tab panel
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				false);
		splitPane.setDividerLocation(250);
		devicesList = new JList<UserDevice>();
		devicesList.setSelectedIndex(0);
		devicesList.setModel(new DefaultListModel<UserDevice>());
		devicesList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (devicesList.getSelectedValue() != null) {
					selectDeviceString = ((UserDevice) devicesList
							.getSelectedValue()).getCd().getTerminalID()
							.toString();
				} else {
					selectDeviceString = "";
				}
				setHistory(selectDeviceString);

			}
		});
		JScrollPane devicesListPane = new JScrollPane(devicesList);

		final JTabbedPane tabPane = new JTabbedPane();
		final JPanel sendCommandPanel = new JPanel();
		final String commandAreaInitWord = "Input command here";
		commandArea = new JTextArea(commandAreaInitWord, 1, 10);
		commandArea.setRows(18);
		commandArea.setLineWrap(true);
		commandArea.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				if (commandArea.getText().equals(commandAreaInitWord))
					commandArea.setText("");
			}

			public void focusLost(FocusEvent arg0) {
			}
		});
		JScrollPane commandAreaPane = new JScrollPane(commandArea);
		JPanel outerCommandPane = new JPanel();
		outerCommandPane.setLayout(new BorderLayout());
		outerCommandPane.add(commandAreaPane, BorderLayout.CENTER);
		outerCommandPane.setSize(200, 300);
		replyArea1 = new JTextArea();
		replyArea1.setEditable(false);
		replyArea1.setRows(10);
		replyArea1.setBounds(100, 200, 200, 100);
		JScrollPane replyAreaPane1 = new JScrollPane(replyArea1);
		JButton sendCommandButton = new JButton("Send Command");
		sendCommandButton.setMnemonic('C');
		sendCommandButton.setBounds(300, 250, 40, 25);

		final SPPClient sc = new SPPClient(UI);
		sendCommandButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sc.start();
			}
		});
		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		sendCommandPanel.setLayout(gridBag);
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 5;
		constraints.gridheight = 3;
		constraints.fill = GridBagConstraints.BOTH;
		this.add(sendCommandPanel, outerCommandPane, gridBag, constraints);
		constraints.gridx = 3;
		constraints.gridy = 3;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.NONE;
		this.add(sendCommandPanel, sendCommandButton, gridBag, constraints);
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 5;
		constraints.gridheight = 2;
		constraints.fill = GridBagConstraints.BOTH;
		this.add(sendCommandPanel, replyAreaPane1, gridBag, constraints);

		// sendFilePanel
		final JPanel sendFilePanel = new JPanel();
		final String fileNameTextInitWord = UsingTTS ? "Input the file name here, without path or expanded-name."
				: "Input the file path and expanded-name here.";
		fileNameText = new JTextField(fileNameTextInitWord);
		fileNameText.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				if (fileNameText.getText().equals(fileNameTextInitWord))
					fileNameText.setText("");
			}

			public void focusLost(FocusEvent arg0) {
			}
		});
		final String fileContentAreaInitWord = "Input the content for the sound file here. Input nothing to send an exist file.";
		fileContentArea = new JTextArea(fileContentAreaInitWord, 1, 10);
		fileContentArea.setRows(16);
		fileContentArea.setLineWrap(true);
		fileContentArea.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				if (fileContentArea.getText().equals(fileContentAreaInitWord))
					fileContentArea.setText("");
			}

			public void focusLost(FocusEvent arg0) {
			}
		});
		if (!UsingTTS) {
			fileContentArea.setVisible(false);
		}
		JScrollPane fileContentAreaPane = new JScrollPane(fileContentArea);
		JPanel outerFileContentPane = new JPanel();
		outerFileContentPane.setLayout(new BorderLayout());
		outerFileContentPane.add(fileNameText, BorderLayout.NORTH);
		outerFileContentPane.add(fileContentAreaPane, BorderLayout.SOUTH);
		outerCommandPane.setSize(200, 300);
		JButton sendFileButton = new JButton("Send File");
		sendFileButton.setMnemonic('F');
		sendFileButton.setBounds(300, 250, 40, 25);
		sendFileButton.addActionListener(new SendFileEventListener());
		replyArea2 = new JTextArea();
		replyArea2.setEditable(false);
		replyArea2.setRows(10);
		replyArea2.setBounds(100, 200, 200, 100);
		JScrollPane replyAreaPane2 = new JScrollPane(replyArea2);
		sendFilePanel.setLayout(gridBag);
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 5;
		constraints.gridheight = 3;
		constraints.fill = GridBagConstraints.BOTH;
		this.add(sendFilePanel, outerFileContentPane, gridBag, constraints);
		constraints.gridx = 3;
		constraints.gridy = 3;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.NONE;
		this.add(sendFilePanel, sendFileButton, gridBag, constraints);
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 5;
		constraints.gridheight = 2;
		constraints.fill = GridBagConstraints.BOTH;
		this.add(sendFilePanel, replyAreaPane2, gridBag, constraints);

		final JPanel historyPanel = new JPanel();
		historyArea = new JTextArea("Select a device to see its history");
		historyArea.setEditable(false);
		historyPanel.setLayout(new GridLayout(1, 1));
		JScrollPane historyAreaPane = new JScrollPane(historyArea);
		historyPanel.add(historyAreaPane);
		tabPane.add(sendCommandPanel, "Send Command");
		tabPane.add(sendFilePanel, "Send File");
		tabPane.add(historyPanel, "History");
		splitPane.add(devicesListPane, 1);
		splitPane.add(tabPane, 2);

		menuItemSendCommand.setSelected(true);
		buttonGroupWindow.add(menuItemSendCommand);
		buttonGroupWindow.add(menuItemSendFile);
		buttonGroupWindow.add(menuItemHistory);
		menuFile.add(menuItemExit);
		menuTab.add(menuItemSendCommand);
		menuTab.add(menuItemSendFile);
		menuTab.add(menuItemHistory);
		menuHelp.add(menuItemAbout);
		menuBar.add(menuFile);
		menuBar.add(menuTab);
		menuBar.add(menuHelp);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(menuBar, BorderLayout.NORTH);
		this.getContentPane().add(splitPane, BorderLayout.SOUTH);
		menuItemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		menuItemSendCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tabPane.setSelectedComponent(sendCommandPanel);
			}
		});

		menuItemSendFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tabPane.setSelectedComponent(sendFilePanel);
			}
		});

		menuItemHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tabPane.setSelectedComponent(historyPanel);
			}
		});

		menuItemAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane
						.showConfirmDialog(
								null,
								"TalkingBadgeServer \nVersion 0.1 \n(c) Copyright GenieGroup, PKU & ITU, 2012.  All rights reserved.",
								"About TalkingBadgeServer",
								JOptionPane.DEFAULT_OPTION,
								JOptionPane.QUESTION_MESSAGE);
			}
		});

		tabPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JTabbedPane pane = (JTabbedPane) evt.getSource();
				int sel = pane.getSelectedIndex();
				switch (sel) {
				case 0:
					menuItemSendCommand.setSelected(true);
					break;
				case 1:
					menuItemSendFile.setSelected(true);
					break;
				case 2:
					menuItemHistory.setSelected(true);
					break;
				}

			}
		});

		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		setSize(1024, 620);
		setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
	}

	private void add(Container f, Component c, GridBagLayout gridBag,
			GridBagConstraints constraints) {
		gridBag.setConstraints(c, constraints);
		f.add(c);
	}

	public static void addReply(String reply) {
		String newMessage = (new Date()).toString() + ": " + reply;
		replyArea1.insert(newMessage + " \n", replyArea1.getText().length());
		replyArea2.insert(newMessage + " \n", replyArea2.getText().length());
		expandLog(newMessage);
		if (selectDeviceString != null)
			if (reply.contains(selectDeviceString))
				setHistory(selectDeviceString);
	}

	public static void setHistory(String deviceMac) {
		if (deviceMac == "") {
			historyArea.setText("Select a device to see its history");
		} else {
			String[] allMessage = replyArea1.getText().split(" \n");
			String history = "";
			for (int cur = 0; cur < allMessage.length; cur++) {
				if (allMessage[cur].contains(deviceMac))
					history += (allMessage[cur] + "\n");
			}
			historyArea.setText(history);
		}
	}

	protected static String text2sound(String name, String text) {
		if (!name.endsWith(".wav"))
			name = name.concat(".wav");
		String outPutFile = "res\\" + name;
		if (text.equals(""))
			return outPutFile;
		try {
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
			if (false) {
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

	protected static void wav2MPEG() {
		try {
			AudioFileSource fileSrc = new AudioFileSource(new File(
					"res\\Beijing.wav"));

			AudioMediaURLSink fileSink = new AudioMediaURLSink(new URL(
					"file:res\\Beijing1.mp3"), FileTypeDescriptor.MPEG_AUDIO);
			fileSrc.setSink(fileSink);

			// Start the whole thing going!
			fileSrc.startSending();

			// and wait for the output files to be written.
			fileSink.drain();

			System.out.println("All done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class SendFileEventListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
/**
			File dir = new File("tobadge//");
			File[] files = dir.listFiles();
			System.out.println(files.length);
			for (File file : files) {
				fileNameText.setText(file.getAbsolutePath());
				toSendFile();
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		
	*/		
			toSendFile();
		}
	}

	public static synchronized void expandLog(String newLog) {

		try {
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(logFile, true)));
			output.write(newLog + "\n");
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void toSendFile() {

		if (fileNameText.getText().replaceAll(" ", "").isEmpty()) {
			JOptionPane.showConfirmDialog(null, "Invalid file name.", "Error",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE);
		} else {
			UserDevice selectedUD = (UserDevice) UserInterface.getDevicesList()
					.getSelectedValue();
			if (selectedUD == null) {
				UserInterface
						.addReply("Send file failed, since no device is selected.");
				return;
			}
			if (selectedUD == null) {
				UserInterface
						.addReply("Send file failed, since no device is selected.");
				return;
			}
			sendFile(selectedUD.getCd().getTerminalID().toString());
		}

	}

	public void sendFile(String destTerminalAddress) {

		if (fileNameText.getText().replaceAll(" ", "").isEmpty()) {
			JOptionPane.showConfirmDialog(null, "Invalid file name.", "Error",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE);
		} else {
			UserDevice selectedUD = (UserDevice) UserInterface.getDevicesList()
					.getSelectedValue();
			if (selectedUD == null) {
				UserInterface
						.addReply("Send file failed, since no device is selected.");
				return;
			}
			final String theFilepath = UsingTTS ? text2sound(
					fileNameText.getText(), fileContentArea.getText())
					: fileNameText.getText();
			File theFile = new File(theFilepath);
			if (!theFile.exists()) {
				JOptionPane.showConfirmDialog(null, "File does not exist.",
						"Error", JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE);
			} else {
				try {
					final BlipNodeHandle handle = server
							.getBlipNodeHandle(selectedUD.getBlipMac());
					handle.getOppClientHandler().addEventListener(
							new OppClientEventListener() {
								public void handleOppClientEvent(
										OppClientEvent evt) {
									int eventID = evt.getEventID();

									switch (eventID) {
									case OppClientEvent.OBEX_PUSH_COMPLETED:
										message = theFilepath
												+ " has been pushed to "
												+ evt.getTerminalID();
										if (!message.equals(previousMessage)) {
											addReply(message);
											setPreviousMessage(message);
										}

										break;
									case OppClientEvent.OBEX_PUSH_FAILED:
										message = "Push "
												+ theFilepath
												+ " to '"
												+ evt.getTerminalID()
												+ "' failed. Cause is "
												+ EventCause.causeName(evt
														.getCause());
										if (!message.equals(previousMessage)) {
											addReply(message);
											setPreviousMessage(message);
										}

										break;
									}
								}
							});

					ObexPushObject opo = new ObexFile(theFile);
					handle.getOppClientHandler().push(selectedUD.getCd(), opo);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

	}

}
