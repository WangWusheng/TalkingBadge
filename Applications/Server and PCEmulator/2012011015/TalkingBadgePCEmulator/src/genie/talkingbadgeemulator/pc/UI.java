package genie.talkingbadgeemulator.pc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class UI extends JFrame {
	Control control = null;

	private JMenuBar menuBar = new JMenuBar();
	private JMenu menuFile = new JMenu("File");
	private JMenu menuWindow = new JMenu("Window");
	private JMenu menuHelp = new JMenu("Help");
	private JMenuItem menuItemExit = new JMenuItem("Exit");
	private ButtonGroup buttonGroupWindow = new ButtonGroup();
	private JRadioButton menuItemTag = new JRadioButton("TagView");
	private JRadioButton menuItemAdvanced = new JRadioButton("AdvancedView");
	private JMenuItem menuItemAbout = new JMenuItem("About");

	private JFrame frameUI = this;
	private JTextArea displayText = new JTextArea("^_^ TalkingBadge started.");

	public JTextArea getDisplayText() {
		return displayText;
	}

	public void appendDisplayText(String text) {
		displayText.setText(displayText.getText() + "\n" + text);
	}

	private JLayeredPane layeredPaneTag = new JLayeredPane();
	private JScrollPane scrollPanelAdd = new JScrollPane(displayText);
	private JPanel panelAdd = new JPanel();
	static String volumeLevel = null;

	public static String getVolumeLevel() {
		return volumeLevel;
	}

	private JButton buttonReplay = new JButton("Replay");
	private JButton buttonVolumn = new JButton("Volumn");
	private JLabel labelOnOff = new JLabel("On");

	UI(Control con) {
		super("TalkingBadgeEmulator");
		control = con;
		this.setLayout(new BorderLayout());
		this.setSize(210, 510);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		volumeLevel = "LOUD";

		menuItemTag.setSelected(true);
		buttonGroupWindow.add(menuItemTag);
		buttonGroupWindow.add(menuItemAdvanced);
		menuFile.add(menuItemExit);
		menuWindow.add(menuItemTag);
		menuWindow.add(menuItemAdvanced);
		menuHelp.add(menuItemAbout);
		menuBar.add(menuFile);
		menuBar.add(menuWindow);
		menuBar.add(menuHelp);
		this.add(menuBar, BorderLayout.NORTH);
		menuItemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		menuItemTag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelAdd.setVisible(false);
				frameUI.setSize(210, 510);
			}
		});

		menuItemAdvanced.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelAdd.setVisible(true);
				frameUI.setSize(710, 510);
			}
		});

		menuItemAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane
						.showConfirmDialog(
								null,
								"TalkingBadgeEmulator \nVersion 0.1 \n(c) Copyright GenieGroup, PKU & ITU, 2012.  All rights reserved.",
								"About TalkingBadgeEmulator",
								JOptionPane.DEFAULT_OPTION,
								JOptionPane.QUESTION_MESSAGE);
			}
		});

		labelOnOff.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				if (labelOnOff.getText() == "On") {
					labelOnOff.setText("Off");
					labelOnOff.setForeground(Color.red);
					control.getClip().stop();
					buttonReplay.setEnabled(false);
					buttonVolumn.setEnabled(false);
				} else {
					labelOnOff.setText("On");
					control.playLowBatteryAlarm();
					labelOnOff.setForeground(Color.blue);
					buttonReplay.setEnabled(true);
					buttonVolumn.setEnabled(true);
					control.playIntroductionMessage();
				}
			}
		});

		buttonVolumn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				switch (volumeLevel) {
				case "LOUD":
					SetVolume("MEDIUM");
					break;
				case "MEDIUM":
					SetVolume("LOW");
					break;
				case "LOW":
					SetVolume("SILENCE");
					break;
				case "SILENCE":
					SetVolume("LOUD");
					break;

				}

			}

		});

		buttonReplay.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				control.replayLastMessage();
			}

		});

		ImageIcon img = new ImageIcon("res\\talkingbadge.jpg");
		JLabel labelTag = new JLabel(img);
		labelTag.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());

		displayText.setAutoscrolls(true);
		displayText.setRows(40);
		displayText.setSize(500, 500);
		displayText.setLineWrap(true);
		displayText.setEditable(false);

		layeredPaneTag.add(labelTag, JLayeredPane.PALETTE_LAYER);
		layeredPaneTag.add(buttonReplay,
				(Integer) (JLayeredPane.PALETTE_LAYER + 50));
		layeredPaneTag.add(buttonVolumn,
				(Integer) (JLayeredPane.PALETTE_LAYER + 50));
		layeredPaneTag.add(labelOnOff,
				(Integer) (JLayeredPane.PALETTE_LAYER + 50));
		labelOnOff.setBounds(136, 130, 40, 50);
		labelOnOff.setForeground(Color.blue);
		buttonReplay.setBounds(40, 200, 80, 50);
		buttonVolumn.setBounds(40, 300, 80, 50);
		buttonVolumn.setText(volumeLevel);

		panelAdd.add(scrollPanelAdd);
		panelAdd.setVisible(false);
		this.add(layeredPaneTag);
		this.add(panelAdd, BorderLayout.EAST);

		setVisible(true);
		this.setResizable(true);
	}

	protected String SetVolume(String newVolume) {
		switch (newVolume) {
		case "LOUD":
			volumeLevel = newVolume;
			buttonVolumn.setText(newVolume);
			control.setVolumn(6);
			control.playLoudVolumeAlert();
			return "SET VOLUME " + newVolume;
		case "MEDIUM":
			volumeLevel = newVolume;
			buttonVolumn.setText(newVolume);
			control.setVolumn(-20);
			control.playMediumVolumeAlert();
			return "SET VOLUME " + newVolume;
		case "LOW":
			volumeLevel = newVolume;
			buttonVolumn.setText(newVolume);
			control.setVolumn(-40);
			control.playLowVolumeAlertSoundFile();
			return "SET VOLUME " + newVolume;
		case "SILENCE":
			volumeLevel = newVolume;
			buttonVolumn.setText(newVolume);
			control.setVolumn(-80);
			return "SET VOLUME " + newVolume;
		default:
			return "BAD VOLUME VALUE";

		}

	}

}
