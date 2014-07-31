package org.am.sl;

import at.vcity.androidim.R;
import org.am.sl.interfaces.IAppManager;
import org.am.sl.services.IService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Cindy Espínola
 *
 */
public class SignUp extends Activity {

	/**
	 * 
	 */
	private static final int FILL_ALL_FIELDS = 0;
	/**
	 * 
	 */
	protected static final int TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS = 1;
	/**
	 * 
	 */
	private static final int SIGN_UP_FAILED = 2;
	/**
	 * 
	 */
	private static final int SIGN_UP_USERNAME_CRASHED = 3;
	/**
	 * 
	 */
	private static final int SIGN_UP_SUCCESSFULL = 4;
	/**
	 * 
	 */
	protected static final int USERNAME_AND_PASSWORD_LENGTH_SHORT = 5;
	// private static final String SERVER_RES_SIGN_UP_FAILED = "0";
	/**
	 * 
	 */
	private static final String SERVER_RES_RES_SIGN_UP_SUCCESFULL = "1";
	/**
	 * 
	 */
	private static final String SERVER_RES_SIGN_UP_USERNAME_CRASHED = "2";

	/**
	 * 
	 */
	private EditText usernameText;
	/**
	 * 
	 */
	private EditText passwordText;
	/**
	 * 
	 */
	private EditText eMailText;
	/**
	 * 
	 */
	private EditText passwordAgainText;
	/**
	 * 
	 */
	private IAppManager imService;
	/**
	 * 
	 */
	private Handler handler = new Handler();

	/**
	 * 
	 */
	private ServiceConnection connection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			imService = ((IService.IMBinder) service).getService();

		}

		public void onServiceDisconnected(ComponentName className) {
			imService = null;
			Toast.makeText(SignUp.this, R.string.local_service_stopped,
					Toast.LENGTH_SHORT).show();
		}
	};

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sign_up_screen);
		setTitle("Sign up");

		Button signUpButton = (Button) findViewById(R.id.signUp);
		Button cancelButton = (Button) findViewById(R.id.cancel_signUp);
		// Button language = (Button) findViewById(R.id.btnLanguage);
		usernameText = (EditText) findViewById(R.id.userName);
		passwordText = (EditText) findViewById(R.id.password);
		passwordAgainText = (EditText) findViewById(R.id.passwordAgain);
		eMailText = (EditText) findViewById(R.id.email);
		
	//	Toast.makeText(getApplicationContext(),"LANGUAGE RECEIVED: "+getLanguageReceive(),Toast.LENGTH_LONG).show();
		signUpButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//Log.d("SIGN UP", "SGASG");
				//Toast.makeText(getApplicationContext(),"LANGUAGE RECEIVED: "+getLanguageReceive(),Toast.LENGTH_LONG).show();
				if (usernameText.length() > 0 && passwordText.length() > 0
						&& passwordAgainText.length() > 0
						&& eMailText.length() > 0) {

					if (passwordText.getText().toString()
							.equals(passwordAgainText.getText().toString())) {

						if (usernameText.length() >= 5
								&& passwordText.length() >= 5) {

							Thread thread = new Thread() {
								String result = new String();

								@Override
								public void run() {
									Log.d("LANGUAGE", getLanguageReceive());
result = imService.signUpUser(usernameText.getText().toString(), passwordText.getText().toString(), eMailText.getText().toString(),getLanguageReceive());
									
									handler.post(new Runnable() {

										public void run() {
											if (result.equals(SERVER_RES_RES_SIGN_UP_SUCCESFULL)) {
												Toast.makeText(
														getApplicationContext(),
														R.string.signup_successfull,
														Toast.LENGTH_LONG)
														.show();
											} else if (result.equals(SERVER_RES_SIGN_UP_USERNAME_CRASHED)) {
												Toast.makeText(
														getApplicationContext(),
														R.string.signup_username_crashed,
														Toast.LENGTH_LONG)
														.show();
											} else // if 
												{
												Toast.makeText(getApplicationContext(),R.string.signup_failed,Toast.LENGTH_LONG).show();
											}
										}

									});
								}

							};
							thread.start();
						} else {
							Toast.makeText(
									getApplicationContext(),
									R.string.username_and_password_length_short,
									Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(
								getApplicationContext(),
								R.string.signup_type_same_password_in_password_fields,
								Toast.LENGTH_LONG).show();
					}

				} else {
					Toast.makeText(getApplicationContext(),
							R.string.signup_fill_all_fields, Toast.LENGTH_LONG)
							.show();

				}
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS:
			return new AlertDialog.Builder(SignUp.this)
					.setMessage(
							R.string.signup_type_same_password_in_password_fields)
					.setPositiveButton(R.string.OK,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();
		case FILL_ALL_FIELDS:
			return new AlertDialog.Builder(SignUp.this)
					.setMessage(R.string.signup_fill_all_fields)
					.setPositiveButton(R.string.OK,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();
		case SIGN_UP_FAILED:
			return new AlertDialog.Builder(SignUp.this)
					.setMessage(R.string.signup_failed)
					.setPositiveButton(R.string.OK,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();
		case SIGN_UP_USERNAME_CRASHED:
			return new AlertDialog.Builder(SignUp.this)
					.setMessage(R.string.signup_username_crashed)
					.setPositiveButton(R.string.OK,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();
		case SIGN_UP_SUCCESSFULL:
			return new AlertDialog.Builder(SignUp.this)
					.setMessage(R.string.signup_successfull)
					.setPositiveButton(R.string.OK,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									finish();
								}
							}).create();
		case USERNAME_AND_PASSWORD_LENGTH_SHORT:
			return new AlertDialog.Builder(SignUp.this)
					.setMessage(R.string.username_and_password_length_short)
					.setPositiveButton(R.string.OK,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();
		default:
			return null;

		}

	}

	// BUTTON LANGUAGE
//	public void onClick(View v) {
//		// Intent i = new Intent(SignUp.this, Language.class);
//		// startActivity(i);
//		auxLanguage = 1;
//		Intent i = new Intent(SignUp.this, ListLanguage.class);
//		startActivity(i);
//	}

	/**
	 * @return
	 */
	public String getLanguageReceive() {
//		String language=null;
		Intent intent = getIntent();
		String language = intent.getExtras().getString("language");

		//if (auxLanguage == 1) {
			
			return language;
		//} else {
//			return language;
//		}
//		return language;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		bindService(new Intent(SignUp.this, IService.class), connection,Context.BIND_AUTO_CREATE);

		super.onResume();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		unbindService(connection);
		super.onPause();
	}

}
