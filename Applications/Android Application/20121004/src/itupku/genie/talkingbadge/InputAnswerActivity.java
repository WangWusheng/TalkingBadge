package itupku.genie.talkingbadge;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InputAnswerActivity extends Activity {
	private static final int DIALOG_NOINPUT_ID = 0;
	private static TextView answerText = null;
	private static Button submitNumberButton = null;
	private static Button deleteButton = null;
	private static Button number1 = null;
	private static Button number2 = null;
	private static Button number3 = null;
	private static Button number4 = null;
	private static Button number5 = null;
	private static Button number6 = null;
	private static Button number7 = null;
	private static Button number8 = null;
	private static Button number9 = null;
	private static Button number0 = null;
	private static LinearLayout answerLayout;
	private LocalBroadcastManager mLocalBroadcastManager;
	private BroadcastReceiver mReceiver;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.answer);
		answerText = (TextView) findViewById(R.id.answertext);
		answerText.setEnabled(false);

		submitNumberButton = (Button) findViewById(R.id.submitanswerbutton);
		deleteButton = (Button) findViewById(R.id.deletebutton);
		number0 = (Button) findViewById(R.id.number0);
		number1 = (Button) findViewById(R.id.number1);
		number2 = (Button) findViewById(R.id.number2);
		number3 = (Button) findViewById(R.id.number3);
		number4 = (Button) findViewById(R.id.number4);
		number5 = (Button) findViewById(R.id.number5);
		number6 = (Button) findViewById(R.id.number6);
		number7 = (Button) findViewById(R.id.number7);
		number8 = (Button) findViewById(R.id.number8);
		number9 = (Button) findViewById(R.id.number9);
		answerLayout = (LinearLayout) findViewById(R.id.answerLayout);
		if (TalkingBadgeActivity.backgroundPic != null) {
			setBackgroundPic(TalkingBadgeActivity.backgroundPic);
		}
		submitNumberButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (answerText.getText().length() > 0) {

					LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
							.getInstance(InputAnswerActivity.this);
					Intent tempint = new Intent("genie.talkingbadge");
					tempint.putExtra("Command", "RECEIVEDINPUT");
					tempint.putExtra("Message", answerText.getText().toString());
					myLocalBroadcastManager.sendBroadcast(tempint);
					sendMessage("Submit Number button is pressed");
					Intent tagIntent = new Intent(InputAnswerActivity.this,
							TalkingBadgeActivity.class);
					startActivity(tagIntent);
				} else {
					showDialog(DIALOG_NOINPUT_ID);
				}
			}
		});

		deleteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (answerText.getText().length() > 0)
					answerText.setText(answerText.getText().subSequence(0,
							answerText.getText().length() - 1));
				sendMessage("Delete button is pressed, and current input is "
						+ answerText.getText());
			}
		});

		number0.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				answerText.setText(answerText.getText() + "0");
				sendMessage("Button 0 is pressed, and current input is "
						+ answerText.getText());
			}
		});
		number1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				answerText.setText(answerText.getText() + "1");
				sendMessage("Button 1 is pressed, and current input is "
						+ answerText.getText());
			}
		});
		number2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				answerText.setText(answerText.getText() + "2");
				sendMessage("Button 2 is pressed, and current input is "
						+ answerText.getText());
			}
		});
		number3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				answerText.setText(answerText.getText() + "3");
				sendMessage("Button 3 is pressed, and current input is "
						+ answerText.getText());
			}
		});
		number4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				answerText.setText(answerText.getText() + "4");
				sendMessage("Button 4 is pressed, and current input is "
						+ answerText.getText());
			}
		});
		number5.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				answerText.setText(answerText.getText() + "5");
				sendMessage("Button 5 is pressed, and current input is "
						+ answerText.getText());
			}
		});
		number6.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				answerText.setText(answerText.getText() + "6");
				sendMessage("Button 6 is pressed, and current input is "
						+ answerText.getText());
			}
		});
		number7.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				answerText.setText(answerText.getText() + "7");
				sendMessage("Button 7 is pressed, and current input is "
						+ answerText.getText());
			}
		});
		number8.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				answerText.setText(answerText.getText() + "8");
				sendMessage("Button 8 is pressed, and current input is "
						+ answerText.getText());
			}
		});
		number9.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				answerText.setText(answerText.getText() + "9");
				sendMessage("Button 9 is pressed, and current input is "
						+ answerText.getText());
			}
		});

		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction("genie.talkingbadge");
		mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if (intent.getStringExtra("Command").equals("BACKGROUNDSET")) {
					setBackgroundPic(intent.getStringExtra("Message"));
				}
			}
		};
		mLocalBroadcastManager.registerReceiver(mReceiver, filter);
	}

	protected Dialog onCreateDialog(int i) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (i == DIALOG_NOINPUT_ID) {
			builder.setMessage("You have not inputted the numeber yet!")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).setTitle("No input error");
		}
		AlertDialog alert = builder.create();
		return alert;
	}

	private static boolean setBackgroundPic(String picName) {
		File picFile = DataStorage.findFile(picName);
		if (picFile == null) {
			return false;
		}
		Drawable d = Drawable.createFromPath(picFile.getAbsolutePath());
		answerLayout.setBackgroundDrawable(d);
		TalkingBadgeActivity.backgroundPic = picName;
		return true;
	}

	protected void onDestroy() {
		super.onDestroy();

	}

	private void sendMessage(String theMessage) {
		LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager
				.getInstance(InputAnswerActivity.this);
		Intent tempint = new Intent("genie.talkingbadge");
		tempint.putExtra("Command", "APPENDMESSAGE");
		tempint.putExtra("Message", theMessage);
		mLocalBroadcastManager.sendBroadcast(tempint);
	}
}
